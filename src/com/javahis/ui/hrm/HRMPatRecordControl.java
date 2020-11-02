package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p> Title: ����������Ϣѡ�񴰿� </p>
 * 
 * <p> Description: ����������Ϣѡ�񴰿�  </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 2012.12.17
 * @version 1.0
 */
public class HRMPatRecordControl extends TControl {
    private static String TABLE = "TABLE";
    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent(TABLE);
        TParm parm = (TParm) this.getParameter();
        if ((parm.getErrCode() != 0) || parm.getCount() <= 0) {
            messageBox("E0024");// ��ʼ������ʧ��
            return;
        }
        this.callFunction("UI|" + TABLE + "|setParmValue", parm);
        parm=parm.getRow(0);
        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;TEL;IDNO",
                parm);
        this.setValue("BIRTH_DATE", StringTool.getString(parm.getTimestamp("BIRTH_DATE"),"yyyy/MM/dd"));
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
        TParm result = new TParm();
        for (int i = 0; i < selparm.getNames().length; i++) {
            result.addData(selparm.getNames()[i], selparm.getData(selparm.getNames()[i]));
        }
        result.setCount(1);
        this.setReturnValue(result);
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
