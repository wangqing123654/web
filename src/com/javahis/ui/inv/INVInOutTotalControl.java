package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.InvVerifyInReportTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;

/**
 * <p>
 * Title: ���ʳ�������Control 
 * </p>
 *
 * <p>
 * Description: ���ʳ�������Control
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

public class INVInOutTotalControl
    extends TControl {
	
    public INVInOutTotalControl() {
    }
    
    /*
     * �����ʼ��
     * @see com.dongyang.control.TControl#onInit()
     */
    public void onInit(){
    	initUI();
    }

    /*
     * ��ʼ������
     */
    private void initUI() {
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME", 
				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
					.substring(0, 10).replace("-", "/") + " 00:00:00");
		this.setValue("END_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
	}
    
    /*
     * ��ѯ����
     */
    public void onQuery(){
    	query();
    }

	private void query() {
		TParm parm = new TParm();
		if(this.getValueString("ORG_CODE") == null || this.getValueString("ORG_CODE").length() <= 0){
			this.messageBox("��ѡ��ͳ�Ʋ��ţ�");
			return;
		}
		if(this.getValueString("START_TIME") == null || this.getValueString("START_TIME").length() <= 0
				|| this.getValueString("END_TIME") == null || this.getValueString("END_TIME").length() <= 0){
			this.messageBox("����д��ѯʱ�䣡");
			return;
		}
		//��ʼʱ��
		String startTime = this.getValueString("START_TIME").substring(0, 
				this.getValueString("START_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		parm.setData("START_TIME", startTime);
		//����ʱ��
		String endTime = this.getValueString("END_TIME").substring(0, 
				this.getValueString("END_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		parm.setData("END_TIME", endTime);
		//ͳ��״̬
		if(this.getRadioButtonCmp("ORG_STATUS_OUT").isSelected()){
			
		}else if(this.getRadioButtonCmp("ORG_STATUS_REGRESS").isSelected()){
			
		}
		//����
		if(this.getRadioButtonCmp("ORG_UNIT_MAIN").isSelected()){
			
		}else if(this.getRadioButtonCmp("ORG_UNIT_MIDDLE").isSelected()){//�п�
			
		}
		//ͳ�Ʋ���
		if(this.getValueString("ORG_CODE") != null && this.getValueString("ORG_CODE").length() > 0){
			String orgCode = this.getValueString("ORG_CODE");
			parm.setData("VERIFYIN_DEPT", orgCode);
		}
		TParm result = InvVerifyInReportTool.getInstance().onQueryVerifyInReport(parm);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVInOutTotal.jhw", result);
	}
	
	/*
     * ��շ���
     */
    public void onClear(){
    	this.clearValue("ORG_CODE");
    	this.getRadioButtonCmp("ORG_UNIT_MAIN").setSelected(true);
    	this.getRadioButtonCmp("ORG_STATUS_OUT").setSelected(true);
    }
    
    /*
     * ��ӡ����
     */
    public void onPrint(){
    	print();
    }

	private void print() {
		/*
		if(table.getRowCount() <= 0){
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		TParm tableData = table.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("SUP_CODE", tableData.getData("SUP_CODE", i));
			String verifyinDate = "";
			Timestamp date = tableData.getTimestamp("VERIFYIN_DATE", i);
			if(date != null)
				verifyinDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(date.getTime()))
				.replace("/", "").replace(" ", "").replace(":", "");
			printData.addData("VERIFYIN_DATE", verifyinDate);
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INVSEQ_NO", tableData.getData("INVSEQ_NO", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("GIFT_QTY", tableData.getData("GIFT_QTY", i));
			printData.addData("BILL_UNIT", tableData.getData("BILL_UNIT", i));
			printData.addData("UNIT_PRICE", tableData.getData("UNIT_PRICE", i));
			printData.addData("IN_PRICE", tableData.getData("IN_PRICE", i));
			printData.addData("VALID_DATE", tableData.getData("VALID_DATE", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "SUP_CODE");
		printData.addData("SYSTEM", "COLUMNS", "VERIFYIN_DATE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "GIFT_QTY");
		printData.addData("SYSTEM", "COLUMNS", "BILL_UNIT");
		printData.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "IN_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		
		printParm.setData("TABLE", printData.getData());
		printParm.addData("TITLE", "TEXT", "�������ͳ�Ʊ�");
		printParm.addData("ORG_CODE", "TEXT", Operator.getDept());
		printParm.addData("DATE", "TEXT", "�Ʊ�ʱ�䣺" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVVerifyInReport.jhw",
				printData);
				*/
	}
    
    /*
     * ͨ��������õ���ѡ�����
     */
    private TRadioButton getRadioButtonCmp(String tagName){
    	return (TRadioButton) this.getComponent(tagName);
    }
}
