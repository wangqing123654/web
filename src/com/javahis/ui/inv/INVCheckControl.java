package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;

/**
 * <p>
 * Title: �����̵�Control 
 * </p>
 *
 * <p>
 * Description: �����̵�Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangh 2013.5.8
 * @version 1.0
 */

public class INVCheckControl
    extends TControl {
	
    public INVCheckControl() {
    }
    
    /*
     * �����ʼ��
     * @see com.dongyang.control.TControl#onInit()
     */
    public void onInit(){
    	Timestamp date = new Timestamp(new Date().getTime());
    	this.messageBox(date.toString().replace("-", "/"));
		this.setValue("START_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
    }
    
    /*
     * ��ѯ����
     */
	private void query() {
		TParm parm = new TParm();
		TParm result = null;
		if(this.getValueString("START_TIME") == null || this.getValueString("START_TIME").length() <= 0){
			this.messageBox("����д����ʱ�䣡");
			return;
		}
		//��ʼʱ��
		String startTime = this.getValueString("START_TIME").substring(0, 
				this.getValueString("START_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		parm.setData("START_TIME", startTime);
		if(this.getRadioButtonCmp("CHECK_RADIO_BUTTON").isSelected()) {
			
		}else if(this.getRadioButtonCmp("DETAIL_RADIO_BUTTON").isSelected()){
			
		}
//		result = InvVerifyInReportTool.getInstance().onQueryVerifyInReport(parm);
//		if(result.getCount() <= 0){
//			this.messageBox("�޲�ѯ���");
//			return;
//		}
		openPrintWindow("%ROOT%\\config\\prt\\INV\\INVCheck.jhw", result);
	}
	
	/*
     * ��շ���
     */
    public void onClear(){
    	Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME", new Timestamp(date.getTime()).toString().replace("-", "/"));
		this.getRadioButtonCmp("CHECK_RADIO_BUTTON").setSelected(true);
    }
    
    /*
     * ��ӡ����
     */
    public void onPrint(){
    	query();
    }
    
    /*
     * ͨ��������õ���ѡ�����
     */
    private TRadioButton getRadioButtonCmp(String tagName){
    	return (TRadioButton) this.getComponent(tagName);
    }
}
