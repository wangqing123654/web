package com.javahis.ui.hrm;

import jdo.hrm.HRMCheckFeeTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TButton;
import com.dongyang.util.StringTool;

/**
 * <p> Title: ������˷��ö���ʱ��ѡ�񴰿� </p>
 * 
 * <p> Description: ������˷��ö���ʱ��ѡ�񴰿�  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 2013.01.08
 * @version 1.0
 */
public class HRMPatFeeChooseControl extends TControl {

    private static String TABLE = "TABLE";
    private TTable table;
    String type;

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent(TABLE);
        table.setHeader("���,40;����,50;��������,150,COMPANY_CODE;��ͬ����,140,CONTRACT_CODE;�ײ�����,130,PACKAGE_CODE;��������,80;�ϼƽ��,70,double,#########0.00");
        table.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;6,right");
        table.setParmMap("SEQ_NO;STAFF_NO;COMPANY_CODE;CONTRACT_CODE;PACKAGE_CODE;REPORT_DATE;AR_AMT");
        TParm parm = (TParm) this.getParameter();
        if ((parm.getData("TYPE") == null) || (parm.getData("parmValue") == null)) {
            messageBox("E0024");// ��ʼ������ʧ��
            ((TButton) this.getComponent("RETURN")).setEnabled(false);
            return;
        }
        type = (String) parm.getData("TYPE");
        TParm parmValue = (TParm) parm.getData("parmValue");
        this.callFunction("UI|" + TABLE + "|setParmValue", parmValue);
        parmValue = parmValue.getRow(0);
        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;IDNO", parmValue);
    }

	/**
	 * TABLE˫���¼�
	 */
    public void onTableDoubleCliecked(){
		if ((table.getShowCount() > 0) && (table.getSelectedRow() < 0)) {
			messageBox("��ѡ��һ����¼");
			return;
		}
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        if (type.equals("MASTER")) {// ����
            for (int i = parm.getCount() - 1; i >= 0; i--) {
                if (i != row) {
                    parm.removeRow(i);
                } else {
                    for (int j = 0; j < row; row--) {
                        parm.removeRow(j);
                    }
                    break;
                }
            }
        } else {
            TParm inParm = new TParm();
            inParm.setData("COMPANY_CODE", parm.getData("COMPANY_CODE", row));
            inParm.setData("CONTRACT_CODE", parm.getData("CONTRACT_CODE", row));
            inParm.setData("PACKAGE_CODE", parm.getData("PACKAGE_CODE", row));
            inParm.setData("MR_NO", parm.getData("MR_NO", row));
            parm = HRMCheckFeeTool.getInstance().onQueryDetail(inParm);
        }
        this.setReturnValue(parm);
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
