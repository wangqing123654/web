package com.javahis.ui.hrm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class HRMLis extends TControl {

    private TParm parm;

    public void onInit() {
        // �õ�ǰһ��ҳ�������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            this.setValue("StartDate", parm.getValue("start_Date"));
            this.setValue("EndDate", parm.getValue("end_Date"));
            TParm result =
                    onDisQuery(parm.getValue("CAT1_TYPE"), parm.getValue("start_Date"),
                               parm.getValue("end_Date"));
            this.getTTable("tblDis").setParmValue(result);
        }
    }

    /**
     * ��ѯ��ϸ��Ϣ
     */
    public TParm onDisQuery(String catType, String start_Date, String end_Date) {
        String sql =
                "SELECT ORDER_DESC, COUNT(CASE_NO) FROM  MED_APPLY " + " WHERE CAT1_TYPE = '"
                        + catType + "' AND STATUS <> 9 AND ORDER_DATE BETWEEN TO_DATE('" + start_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " AND TO_DATE('" + end_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " GROUP BY ORDER_DESC";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * ��ӡ��ϸ��Ϣ
     */
    public void onPrintDis() {
        int ATM = 0;
        TTable table_d = getTTable("tblDis");
        if (table_d.getRowCount() > 0) {
            // ��ӡ����OPT_USER;COUNT(CASE_NO)
            TParm date = new TParm();
            // ��ͷ����
            date.setData("Title", "TEXT",
                         Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
                                 + "��������ϸ�˴�ͳ�Ʊ���");
            date.setData("createDate", "TEXT",
                         "�Ʊ�����: "
                                 + SystemTool.getInstance().getDate().toString().substring(0, 10)
                                         .replace('-', '/'));
            date.setData("start_Date", "TEXT", "��ʼʱ��: "
                    + parm.getValue("start_Date").toString().substring(0, 10).replace('-', '/'));
            date.setData("end_Date", "TEXT", "����ʱ��: "
                    + parm.getValue("end_Date").toString().substring(0, 10).replace('-', '/'));
            // ������� B.CHN_DESC, COUNT (A.CASE_NO)
            TParm parm = new TParm();
            TParm tableParm = table_d.getParmValue();
            int count = tableParm.getCount();
            for (int i = 0; i < count; i++) {
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("COUNT(CASE_NO)", tableParm.getValue("COUNT(CASE_NO)", i));
                ATM += StringTool.getInt(tableParm.getValue("COUNT(CASE_NO)", i));
            }
            parm.setCount(parm.getCount("COUNT(CASE_NO)"));
            // System.out.println("parm====" + parm);
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "COUNT(CASE_NO)");
            date.setData("tbl1", parm.getData());
            // ��β����
            date.setData("createUser", "TEXT", "�Ʊ���: " + Operator.getName());
            date.setData("pass", "TEXT", "�����: ");
            date.setData("ATM", "TEXT", "�ܼ�������" + ATM);
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMCJBloodstatistics.jhw", date);
        } else {
            this.messageBox("E0010");// ������ʾ�Ի��򣨡�û��ӡ�����ݡ���
            return;
        }
    }

    /**
     * �����ϸ��Ϣ��Excel
     */
    public void onExportDis() {
        // �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = getTTable("tblDis");
        ExportExcelUtil.getInstance().exportExcel(table, "�������˴���ϸ��Ϣͳ�Ʊ���");
    }

    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }
}
