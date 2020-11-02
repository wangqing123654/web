package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVNewSterilizationTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;



/**
 * 
 * <p>
 * Title:���յ�ѡ�����
 * </p>
 * 
 * <p>
 * Description: ���յ�ѡ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-7-1
 * @version 1.0
 */
public class INVChooseRecycleBillControl extends TControl{

	private TTable  table;
	/**
     * ��ʼ������
     */
    public void onInit() {
    	table = (TTable) getComponent("TABLE");
        //��ʼ��ʱ��ؼ�
        this.onInitDate();
    }
	
    /**
     * ��ѯ����
     */
	public void onQuery(){
		String startDate = getValueString("START_RECYCLE_DATE");	//��ʼʱ��
		String endDate = getValueString("END_RECYCLE_DATE");		//��ֹʱ��

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("��������ֹʱ��!");
			return;
		}

		TParm parm = new TParm();			//��ѯ����
		if(!getValueString("RECYCLE_NO").equals("")){
			parm.setData("RECYCLE_NO", getValueString("RECYCLE_NO"));
		}
		parm.setData("START_RECYCLE_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_RECYCLE_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewSterilizationTool.getInstance().queryBackDisnfection(parm);
		
		table.setParmValue(result);
		
	}
	
	/**
     * ���ط���
     */
	public void onReturn(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("��ѡ����յ���");
			return;
		}
		
		String recycleNo = table.getParmValue().getValue("RECYCLE_NO", row);
		TParm result = new TParm();
		result.setData("RECYCLE_NO", recycleNo);
		setReturnValue(result);
        this.closeWindow();
	}
	/**
     * ��շ���
     */
	public void onClear(){
		
		this.clearValue("RECYCLE_NO");
		table.removeRowAll();
	}
	
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_RECYCLE_DATE", date);
		this.setValue("END_RECYCLE_DATE", date);
	}
	
}
