package com.javahis.ui.hrm;

import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:����������ͳ�� </p>
 *
 * <p>Description:����������ͳ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class HRMLisANDRISStatisticsControl
        extends TControl {

    private String start_Date;
    private String end_Date;

    /**
     * ��ʼ������
     */
    public void onInit() {
        // �õ���ǰʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("end_Date", date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("start_Date", StringTool.rollDate(date, -7).toString().substring(0, 10)
                .replace('-', '/'));
        setValue("PURORDER_DATE", date);
        // this.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        start_Date =
                this.getValue("start_Date").toString().substring(0, 10).replace('-', '/')
                        + " 00:00:00";
        end_Date =
                this.getValue("end_Date").toString().substring(0, 10).replace('-', '/')
                        + " 23:59:59";
        String sql =
                "  SELECT CAT1_TYPE, COUNT(CASE_NO) " + " FROM HRM_ORDER "
                        + " WHERE CAT1_TYPE IS NOT NULL " + "AND SETMAIN_FLG = 'Y'"//modify by wanglong 20130503
                        + " AND ORDER_DATE BETWEEN TO_DATE('" + start_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " AND TO_DATE('" + end_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " GROUP BY CAT1_TYPE";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount() < 1) {
            this.messageBox("E0008");// û������
        } else {
            this.getTTable("tbl1").setParmValue(result);
        }
    }
    
    /**
     * ��ϸ
     */
    public void onDetial() {
        TTable table = (TTable) this.getComponent("tbl1");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("���ѡ������!");
            return;
        }
        TParm tableParm = table.getParmValue();
        String catType = tableParm.getValue("CAT1_TYPE", row);
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", catType);
        parm.setData("start_Date", start_Date);
        parm.setData("end_Date", end_Date);
        this.openDialog("%ROOT%\\config\\hrm\\HRMCJBloodDIS.x", parm);
    }
    
    /**
     * ��ӡ����
     */
    public void onPrint() {
        int ATM = 0;
        TTable table = getTTable("tbl1");
        if (table.getRowCount() > 0) {
            // ��ӡ����OPT_USER;COUNT(CASE_NO)
            TParm date = new TParm();
            // ��ͷ����
            date.setData("Title", "TEXT",
                         Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
                                 + "����������ͳ�Ʊ���");
            date.setData("createDate", "TEXT",
                         "�Ʊ�����: "
                                 + SystemTool.getInstance().getDate().toString().substring(0, 10)
                                         .replace('-', '/'));
            date.setData("start_Date", "TEXT",
                         "��ʼʱ��: " + start_Date.substring(0, 10).replace('-', '/'));
            date.setData("end_Date", "TEXT", "����ʱ��: " + end_Date.substring(0, 10).replace('-', '/'));
            // ������� B.CHN_DESC, COUNT (A.CASE_NO)
            TParm parm = new TParm();
            TParm tableParm = table.getParmValue();
            // System.out.println("tableParm====" + tableParm);
            int count = tableParm.getCount();
            for (int i = 0; i < count; i++) {
                parm.addData("CAT1_TYPE", tableParm.getValue("CAT1_TYPE", i));
                parm.addData("COUNT(CASE_NO)", tableParm.getValue("COUNT(CASE_NO)", i));
                ATM += StringTool.getInt(tableParm.getValue("COUNT(CASE_NO)", i));
            }
            parm.setCount(parm.getCount("COUNT(CASE_NO)"));
            // System.out.println("parm====" + parm);
            parm.addData("SYSTEM", "COLUMNS", "CAT1_TYPE");
            parm.addData("SYSTEM", "COLUMNS", "COUNT(CASE_NO)");
            date.setData("tbl1", parm.getData());
            // ��β����
            date.setData("createUser", "TEXT", "�Ʊ���: " + Operator.getName());
            date.setData("pass", "TEXT", "�����: ");
            date.setData("ATM", "TEXT", "�ܼ�������" + ATM);
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMLisANDRISStatistics.jhw", date);
        } else {
            this.messageBox("E0010");// ������ʾ�Ի��򣨡�û��ӡ�����ݡ���
            return;
        }
    }
    
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        // �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table_d = getTTable("tbl1");
        ExportExcelUtil.getInstance().exportExcel(table_d, "����������ͳ�Ʊ���");
    }

    /**
     * ���
     */
    public void onClear() {
        this.getTTable("tbl1").setSelectionMode(0);
        this.getTTable("tbl1").removeRowAll();
        onInit();
    }


}
