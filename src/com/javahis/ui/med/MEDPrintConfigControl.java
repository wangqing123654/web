package com.javahis.ui.med;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: �����ӡ������ </p>
 * 
 * <p> Description: �����ӡ������ </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class MEDPrintConfigControl extends TControl {

    private TTable table;
    private int selectRow = -1;// ѡ����
    private TParm parmValue;// table����

    /**
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        this.setValue("PRINTER_TERM", Operator.getIP());
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        onClear();
        if (Operator.getRole().equalsIgnoreCase("ADMIN")) {
            this.callFunction("UI|ZEBRA_FLG|setEnabled", true);
            this.callFunction("UI|ZEBRA_FLG|setSelected", true);
            onChooseType();
        }
    }

    /**
     * �е���¼�
     * 
     * @param row
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        this.setValueForParm("PRINTER_TERM;ZEBRA_FLG;PRINTER_PORT", parmValue, row);
        selectRow = row;
    }
    
    /**
     * �������뷽ʽ��ӡ����ѡ/ȡ����ѡ�¼�
     */
    public void onChooseType() {
        if ((Boolean) this.callFunction("UI|ZEBRA_FLG|isSelected")) {
            this.callFunction("UI|PRINTER_PORT|setEnabled", true);
        } else {
            this.callFunction("UI|PRINTER_PORT|setEnabled", false);
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String sql = "SELECT * FROM MED_PRINTER_LIST WHERE PRINTER_TERM = '#' ";
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_TERM"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table.setParmValue(result);
        parmValue = table.getParmValue();
        selectRow = -1;
    }

    /**
     * ����
     */
    public void onSave() {
        if (this.getValueString("PRINTER_TERM").trim().equals("")) {
            this.messageBox("����д�ն˻�IP");
            return;
        }
        if (this.getValueString("PRINTER_PORT").trim().equals("")) {
            this.messageBox("��ѡ���ӡ�˿�");
            return;
        }
        if (selectRow == -1) {
            onInsert();
        } else {
            onUpdate();
        }
    }

    /**
     * ����
     */
    public void onInsert() {
        String sql =
                "INSERT INTO MED_PRINTER_LIST (PRINTER_TERM, ZEBRA_FLG, PRINTER_PORT, OPT_USER, OPT_DATE, OPT_TERM ) "
                        + " VALUES ('#', '#', '#', '@', SYSDATE, '@')  ";
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_TERM").trim());
        sql = sql.replaceFirst("#", this.getValueString("ZEBRA_FLG").trim());
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_PORT").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ�� " + result.getErrText());
            return;
        } else {
            this.messageBox("P0001");// ����ɹ�
            onClear();
        }
    }

    /**
     * ����
     */
    public void onUpdate() {
        String sql =
                "UPDATE MED_PRINTER_LIST SET PRINTER_TERM = '&', "
                        + "                  ZEBRA_FLG = '#',    "
                        + "                  PRINTER_PORT = '#', "
                        + "                  OPT_USER = '@',     "
                        + "                  OPT_DATE = SYSDATE, "
                        + "                  OPT_TERM = '@'      "
                        + " WHERE PRINTER_TERM = '&'             ";
        sql = sql.replaceAll("&", this.getValueString("PRINTER_TERM").trim());
        sql = sql.replaceFirst("#", this.getValueString("ZEBRA_FLG").trim());
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_PORT").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("�޸�ʧ�� " + result.getErrText());
            return;
        } else {
            this.messageBox("�޸ĳɹ�");
            onClear();
        }
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        if (selectRow < 0) {
            this.messageBox("��ѡ��ĳ����¼");
            return;
        }
        String printerTerm = parmValue.getValue("PRINTER_TERM", selectRow);
        if (this.messageBox("��ʾ", "P0010", 2) == 0) {// ȷ��ɾ��
            String sql = "DELETE FROM MED_PRINTER_LIST WHERE PRINTER_TERM = '#' ";
            sql = sql.replaceFirst("#", printerTerm);
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ�� " + result.getErrText());
                return;
            } else {
                this.messageBox("P0003");// ɾ���ɹ�
                onClear();
            }
        }
    }

    /**
     * ���
     */
    public void onClear() {
        onQuery();
    }
    

}
