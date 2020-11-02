package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>Title: Ԥ������ϸ����</p>
 *
 * <p>Description: Ԥ������ϸ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.5
 * @version 1.0
 */
public class BILPayDetailQueryControl
    extends TControl {

    private TTable table;

    public BILPayDetailQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
        String datetime = SystemTool.getInstance().getDate().toString();
        String start_date = datetime.substring(0, 10) + " 00:00:00";
        String end_date = datetime.substring(0, 10) + " 23:59:59";
        this.setValue("START_DATE", start_date.replace("-", "/"));
        this.setValue("END_DATE", end_date.replace("-", "/"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String start_date = this.getValueString("START_DATE").substring(0, 19);
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
        String end_date = this.getValueString("END_DATE").substring(0, 19);
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
            end_date.substring(8, 10) + end_date.substring(11, 13) +
            end_date.substring(14, 16) + end_date.substring(17, 19);
        String sql = getSQL(start_date, end_date);
        //System.out.println("----"+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        TParm result = getParm(parm);
        //System.out.println("result===" + result);
        if (result == null || result.getCount("IPD_NO") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ȡ�ò�ѯSQL
     * @param start_date String
     * @param end_date String
     * @return String
     */
    private String getSQL(String start_date, String end_date) {
        return "SELECT A.IPD_NO, B.PAT_NAME, C.DEPT_ABS_DESC AS DEPT_DESC, "
            + "A.PRE_AMT FROM BIL_PAY A, SYS_PATINFO B, SYS_DEPT C,ADM_INP D "
            + "WHERE A.CHARGE_DATE BETWEEN TO_DATE ('" + start_date
            + "', 'YYYYMMDDHH24MISS') AND TO_DATE ('"
            + end_date + "', 'YYYYMMDDHH24MISS') "
            + "AND A.MR_NO = B.MR_NO AND A.CASE_NO = D.CASE_NO "
            + "AND D.DEPT_CODE = C.DEPT_CODE "
            + "ORDER BY D.DEPT_CODE ";
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getParm(TParm parm) {
        TParm result = new TParm();
        String dept_desc = "";
        double sum_pre_amt = 0;

        if (parm.getCount("IPD_NO") == 1) {
            dept_desc = parm.getValue("DEPT_DESC", 0);
            sum_pre_amt = parm.getDouble("PRE_AMT", 0);
            result.addData("IPD_NO", parm.getValue("IPD_NO", 0));
            result.addData("PAT_NAME", parm.getValue("PAT_NAME", 0));
            result.addData("DEPT_DESC", dept_desc);
            result.addData("PRE_AMT", sum_pre_amt);
            addRowAMT(result, dept_desc, sum_pre_amt);
        }
        else {
            for (int i = 0; i < parm.getCount("IPD_NO"); i++) {
                dept_desc = parm.getValue("DEPT_DESC", i);
                if (i > 0 && !dept_desc.equals(parm.getValue("DEPT_DESC", i - 1))) {
                    addRowAMT(result, parm.getValue("DEPT_DESC", i - 1),
                              sum_pre_amt);
                    sum_pre_amt = 0;
                }
                sum_pre_amt += parm.getDouble("PRE_AMT", i);
                result.addData("IPD_NO", parm.getValue("IPD_NO", i));
                result.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
                result.addData("DEPT_DESC", parm.getValue("DEPT_DESC", i));
                result.addData("PRE_AMT", parm.getDouble("PRE_AMT", i));
            }
            addRowAMT(result, dept_desc, sum_pre_amt);
        }

        return result;
    }

    /**
     * ��Ӻϼ���
     * @param parm TParm
     * @param dept_desc String
     * @param sum_amt double
     * @return TParm
     */
    private TParm addRowAMT(TParm parm, String dept_desc, double sum_amt) {
        parm.addData("IPD_NO", "Ԥ����С��");
        parm.addData("PAT_NAME", dept_desc);
        parm.addData("DEPT_DESC", "");
        parm.addData("PRE_AMT", sum_amt);

        return parm;
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) + "סԺԤ������ϸ����");
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT",
                     "ͳ������: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) +
                     " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        date.setData("DATE", "TEXT",
                     "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).
                     replace('-', '/'));
        // �������
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("IPD_NO", tableParm.getValue("IPD_NO", i));
            parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
            parm.addData("DEPT_DESC", tableParm.getValue("DEPT_DESC", i));
            parm.addData("PRE_AMT", tableParm.getDouble("PRE_AMT", i));
        }
        parm.setCount(parm.getCount("IPD_NO"));
        parm.addData("SYSTEM", "COLUMNS", "IPD_NO");
        parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        parm.addData("SYSTEM", "COLUMNS", "PRE_AMT");
        date.setData("TABLE", parm.getData());
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPayDetailQuery.jhw",
                             date);
    }

    /**
     * ����Excel
     */
    public void onExcel() {
        if (table.getRowCount() <= 0) {
            this.messageBox("û�е�������");
            return;
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String datetime = SystemTool.getInstance().getDate().toString();
        String start_date = datetime.substring(0, 10) + " 00:00:00";
        String end_date = datetime.substring(0, 10) + " 23:59:59";
        this.setValue("START_DATE", start_date.replace("-", "/"));
        this.setValue("END_DATE", end_date.replace("-", "/"));
        table.removeRowAll();
    }

}
