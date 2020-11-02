package com.javahis.ui.inv;

import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sys.SYSHzpyTool;
import jdo.inv.INVSQL;


/**
 * <p>Title: ��������ֵ�ά��</p>
 *
 * <p>Description: ��������ֵ�ά��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author wangm 2013-07-08
 * @version 1.0
 */
public class INVSUPTitemControl
    extends TControl {

    /**
     * ��������
     */
    private TTable table;


    public void onInit() {
        super.onInit();
        //��Ŀ����
        table = (TTable) getComponent("TABLE");
        initTable();
    }

    /**
     * ��ʼ��table
     */
    public void initTable() {
        table.setSQL("SELECT * FROM INV_SUPTITEM WHERE SUPITEM_CODE IS NULL");
        table.retrieve();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = getQueryValue();
        String sql = INVSQL.getQuerySuptitemSql(parm);
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("û��ѡ��ɾ������");
            return;
        }
        table.removeRow(row);
        if (!table.update()) {
            messageBox("ɾ��ʧ��!");
            return;
        }
        messageBox("ɾ���ɹ�!");
        this.onClear();
    }

    /**
     * �õ���ѯ����
     * @return TParm
     */
    public TParm getQueryValue() {
        TParm parm = new TParm();
        parm.setData("SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        parm.setData("SUPITEM_DESC", getValueString("SUPITEM_DESC"));
        parm.setData("PY1", getValueString("PY1").toUpperCase());
        return parm;
    }

    /**
     * table�ĵ���¼�
     */
    public void onClickedTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        //table���������
        TParm tableValue = table.getDataStore().getBuffer(table.getDataStore().
            PRIMARY);
        setTextValue(tableValue.getRow(row));
        setTextEnabled(false);
    }

    /**
     * �����Ϸ�
     * @param parm TParm
     */
    public void setTextValue(TParm parm) {
        this.setValueForParm(
            "SUPITEM_CODE;SUPITEM_DESC;PY1;COST_PRICE;ADD_PRICE;DESCRIPTION",
            parm);
    }

    /**
     * ��Ŀ����Ŀɱ༭
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
        this.callFunction("UI|SUPITEM_CODE|setEnabled", boo);
    }

    /**
     * ���
     */
    public void onClear() {
        setTextEnabled(true);
        this.clearValue(
            "SUPITEM_CODE;SUPITEM_DESC;PY1;COST_PRICE;ADD_PRICE;DESCRIPTION");
        table.clearSelection();
    }

    /**
     * ��Ŀ���ƻس��¼�
     */
    public void onDescEnter() {
        String py = SYSHzpyTool.getInstance().charToCode(getValueString(
            "SUPITEM_DESC"));
        setValue("PY1", py);
    }

    /**
     * ����
     * @return boolean
     */
    public boolean onSave() {
        int row = table.getSelectedRow();
        boolean saveFlg = false;
        //�����ѡ����Ϊ����
        if (row >= 0) {
            //�����������
            if (!dealUpdate(row, saveFlg))
                return false;
        }
        else {
            //������������
            if (!dealNewData())
                return false;
        }
        //�������ݷ���
        return saveData();
    }

    /**
     * ���ñ��淽��
     * @return boolean
     */
    public boolean saveData() {
        if (!table.update()) {
            messageBox("����ʧ��!");
            return false;
        }
        messageBox("����ɹ�!");
        table.resetModify();
        onClear();
        return true;
    }

    /**
     * ��������
     * @return boolean
     */
    public boolean dealNewData() {
        String supitemCode = getValueString("SUPITEM_CODE");
        if (!checkSupItemCode(supitemCode))
            return false;
        //����
        String supitemDesc = getValueString("SUPITEM_DESC");
        //�������
        if (!checkItemDesc(supitemDesc))
            return false;
        //�õ���ɱ��۸�
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("�ɱ�����С����!");
            return false;
        }
        //�õ����ӽ��
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("���ӽ���С����!");
            return false;
        }
        //������������
        setNewData();
        return true;
    }

    /**
     * ��˴���
     * @param supItemCode String
     * @return boolean
     */
    public boolean checkSupItemCode(String supItemCode) {
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("���벻��Ϊ��");
            return false;
        }
        if (table.getDataStore().exist("SUPITEM_CODE='" + supItemCode + "'")) {
            messageBox("����" + supItemCode + "�ظ�");
            return false;
        }
        return true;
    }

    /**
     * ������������
     */
    public void setNewData() {
        int row = table.addRow();
        table.setItem(row, "SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        table.setItem(row, "SUPITEM_DESC", getValueString("SUPITEM_DESC"));
        table.setItem(row, "PY1", getValueString("PY1"));
        table.setItem(row, "COST_PRICE", getValueDouble("COST_PRICE"));
        table.setItem(row, "ADD_PRICE", getValueDouble("ADD_PRICE"));
        table.setItem(row, "DESCRIPTION", getValue("DESCRIPTION"));
        //��ӹ̶�����
        setTableData(row);
    }

    /**
     * �����������
     * @param row int
     * @param saveFlg boolean
     * @return boolean
     */
    public boolean dealUpdate(int row, boolean saveFlg) {
        //����
        String supitemDesc = getValueString("SUPITEM_DESC");
        //�������
        if (!checkItemDesc(supitemDesc))
            return false;
        String desc = table.getItemString(row, "SUPITEM_DESC");
        //������޸������
        if (!supitemDesc.equals(desc)) {
            table.setItem(row, "SUPITEM_DESC", supitemDesc);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //�õ���ɱ��۸�
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("�ɱ�����С����!");
            return false;
        }
        double price = table.getItemDouble(row, "COST_PRICE");
        if (costPrice != price) {
            table.setItem(row, "COST_PRICE", costPrice);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }

        //�õ����ӽ��
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("���ӽ���С����!");
            return false;
        }
        price = table.getItemDouble(row, "ADD_PRICE");
        if (addPrice != price) {
            table.setItem(row, "ADD_PRICE", addPrice);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //����
        String decription = getValueString("DESCRIPTION");
        String decript = table.getItemString(row, "DESCRIPTION");
        //������޸������
        if (!decription.equals(decript)) {
            table.setItem(row, "DESCRIPTION", decription);
            //��¼�Ƿ����޸�
            saveFlg = true;
        }
        //������޸����޸Ĺ̶�����
        if (saveFlg)
            setTableData(row);
        return true;
    }

    /**
     * �޹̶�����
     * @param row int
     */
    public void setTableData(int row) {
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        table.setItem(row, "OPT_TERM", Operator.getIP());
    }

    /**
     * �������
     * @param supitemItem String
     * @return boolean
     */
    public boolean checkItemDesc(String supitemItem) {
        if (supitemItem == null || supitemItem.length() == 0) {
            messageBox("���Ʋ���Ϊ��");
            return false;
        }
        return true;
    }

}
