package com.javahis.ui.ope;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: ������IP�������� </p>
 * 
 * <p> Description: ������IP�������� </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author wanglong 2014-9-29
 * @version 1.0
 */
public class OPEIPRoomConfigControl extends TControl {

    private TTable table;
    private int selectRow = -1;// ѡ����
    private TParm parmValue;// table����

    /**
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        this.setValue("IP", Operator.getIP());
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        onClear();
    
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
        this.setValueForParm("IP;ROOM_NO;TYPE_CODE", parmValue, row);
        selectRow = row;
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String sql = "SELECT * FROM OPE_IPROOM WHERE 1=1 ";
        if (!Operator.getRole().equals("ADMIN")) {
            sql += " AND IP = '" + this.getValueString("IP").trim() + "' ";
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        this.callFunction("UI|TABLE|setParmValue",result);
//        table.setParmValue(result);
//        parmValue = table.getParmValue();
//        selectRow = -1;
        parmValue = table.getParmValue();
        selectRow = -1;
        this.clearValue("ROOM_NO;TYPE_CODE");
        this.setValue("IP", Operator.getIP());
    }

    /**
     * ����
     */
    public void onSave() {
        if (this.getValueString("IP").trim().equals("")) {
            this.messageBox("����д�ն˻�IP");
            return;
        }
        if (this.getValueString("ROOM_NO").trim().equals("")) {
            this.messageBox("��ѡ��������");
            return;
        }
        if (this.getValueString("TYPE_CODE").trim().equals("")) {
            this.messageBox("��ѡ����������");
            return;
        }
        selectRow = table.getSelectedRow();
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
                "INSERT INTO OPE_IPROOM (IP, ROOM_NO, TYPE_CODE, OPT_USER, OPT_DATE, OPT_TERM ) "
                        + " VALUES ('#', '#', '#', '@', SYSDATE, '@')  ";
        sql = sql.replaceFirst("#", this.getValueString("IP").trim());
        sql = sql.replaceFirst("#", this.getValueString("ROOM_NO").trim());
        sql = sql.replaceFirst("#", this.getValueString("TYPE_CODE").trim());
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
                "UPDATE OPE_IPROOM SET IP = '#', " 
                        + "                  ROOM_NO = '#',      "
                        + "                  TYPE_CODE = '#',    "
                        + "                  OPT_USER = '@',     "
                        + "                  OPT_DATE = SYSDATE, "
                        + "                  OPT_TERM = '@'      "
                        + " WHERE IP = '&'                       ";
        sql = sql.replaceFirst("#", this.getValueString("IP").trim());
        sql = sql.replaceFirst("#", this.getValueString("ROOM_NO").trim());
        sql = sql.replaceFirst("#", this.getValueString("TYPE_CODE").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
        sql = sql.replaceFirst("&", parmValue.getValue("IP", selectRow));
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
        String printerTerm = parmValue.getValue("IP", selectRow);
        if (this.messageBox("��ʾ", "P0010", 2) == 0) {// ȷ��ɾ��
            String sql = "DELETE FROM OPE_IPROOM WHERE IP = '#' ";
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
        this.clearValue("ROOM_NO;TYPE_CODE");
        onQuery();
    }
    

}
