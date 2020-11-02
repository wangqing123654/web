package com.javahis.ui.aci;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p> Title: סԺҽ��ѡ�񴰿� </p>
 * 
 * <p> Description: סԺҽ��ѡ�񴰿�  </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author wanglong 2014.01.07
 * @version 1.0
 */
public class ACIADROrderChooseControl extends TControl {
    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        TParm parm = (TParm) this.getParameter();
        if ((parm.getErrCode() != 0) || parm.getCount() <= 0) {
            messageBox("E0024");// ��ʼ������ʧ��
            return;
        }
        this.callFunction("UI|TABLE|setParmValue", parm);
    }

    /**
     * TABLE˫���¼�
     */
    public void onTableDoubleCliecked() {
        if ((table.getShowCount() > 0) && (table.getSelectedRow() < 0)) {
            messageBox("��ѡ��һ����¼");
            return;
        }
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        TParm selparm = parm.getRow(row);
        this.setReturnValue(selparm);
        this.closeWindow();
    }
    
    /**
     * �ش�
     */
    public void onReturn() {
        onTableDoubleCliecked();
    }

    /**
     * ȡ��
     */
    public void onCancel() {
        this.closeWindow();
    }
}
