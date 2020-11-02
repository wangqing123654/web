package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>Title: ���Ͳ�ѯ</p>
 *
 * <p>Description: ���Ͳ�ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.12.1
 * @version 1.0
 */
public class NSSPantryQueryControl
    extends TControl {

    private TTable table;

    public NSSPantryQueryControl() {
    }

    /*
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("DIET_DATE", datetime.substring(0, 10).replace("-", "/"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String sql =
            "SELECT B.MENU_CHN_DESC, COUNT(B.MENU_CHN_DESC) || '��' AS COUNT "
            + " FROM NSS_ORDER A, NSS_PACKDD B, ADM_INP C "
            + " WHERE A.PACK_CODE = B.PACK_CODE "
            + " AND A.MEAL_CODE = B.MEAL_CODE "
            + " AND A.CASE_NO = C.CASE_NO ";
        String group_by = " GROUP BY B.MENU_CHN_DESC, B.MENU_CODE "
            + " ORDER BY B.MENU_CODE ";
        // ����
        String station_code = this.getValueString("STATION_CODE");
        if (station_code != null && station_code.length() > 0) {
            sql += " AND C.STATION_CODE = '" + station_code + "' ";
        }
        // �ò�����
        String diet_date = this.getValueString("DIET_DATE");
        diet_date = diet_date.substring(0, 4) + diet_date.substring(5, 7) +
            diet_date.substring(8, 10);
        if (diet_date != null && diet_date.length() > 0) {
            sql += " AND A.DIET_DATE = '" + diet_date + "' ";
        }
        // �ʹ�
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code != null && meal_code.length() > 0) {
            sql += " AND A.MEAL_CODE = '" + meal_code + "' ";
        }
        sql = sql + group_by;
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            table.removeRowAll();
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String datetime = SystemTool.getInstance().getDate().toString();
        this.setValue("DIET_DATE", datetime.substring(0, 10).replace("-", "/"));
        this.clearValue("STATION_CODE;MEAL_CODE");
        table.removeRowAll();
    }

    /**
     * ��ӡ��������
     */
    public void onPrint() {
        TParm parmTable = table.getParmValue();
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "���͵�");
        // �������
        TParm parm = new TParm();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("MENU_CHN_DESC", parmTable.getValue("MENU_CHN_DESC", i));
            parm.addData("SUM_COUNT", parmTable.getValue("COUNT", i));
        }
        if (parm.getCount("MENU_CHN_DESC") <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        parm.setCount(parm.getCount("MENU_CHN_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "MENU_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SUM_COUNT");

        date.setData("TABLE", parm.getData());
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        date.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSPantry.jhw", date);
    }
}
