package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inv.InvRegressGoodsTotalTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �����˻�ͳ��Control 
 * </p>
 *
 * <p>
 * Description: �����˻�ͳ��Control
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

public class INVRegressGoodsReportControl
    extends TControl {
	
    public INVRegressGoodsReportControl() {
    }
    
    private TTable tableUp;
    private TTable tableDown;
    
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
		this.setValue("START_TIME", new Date());
		this.setValue("END_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
		tableUp = (TTable) this.getComponent("TABLE");
		tableDown = (TTable) this.getComponent("TABLE_DOWN");
	}
    
    /*
     * ��ѯ����
     */
    public void onQuery(){
    	query();
    }

	private void query() {
		TParm parm = new TParm();
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
		//��Ӧ��
		if(this.getValueString("SUP_CODE") != null && this.getValueString("SUP_CODE").length() > 0){
			String supCode = this.getValueString("SUP_CODE");
			parm.setData("SUP_CODE", supCode);
		}
		TParm result = InvRegressGoodsTotalTool.getInstance().onQueryRegressGoodsTotal(parm);
		if(result.getCount() <= 0){
			this.messageBox("�޲�ѯ���");
			tableUp.removeRowAll();
			return;
		}
		tableUp.setParmValue(result);
	}
	

	/*
     * ��շ���
     */
    public void onClear(){
    	this.clearValue("SUP_CODE");
    	tableUp.removeRowAll();
    }
    
    /*
     * ��ӡ�ϱ�񷽷�
     */
    public void onPrintUp() {
		if(tableUp.getRowCount() <= 0){
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		TParm tableData = tableUp.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("SUP_CHN_DESC", tableData.getValue("SUP_CHN_DESC", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "�˻�ͳ�Ʊ�");
		printParm.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
		printParm.setData("DATE", "TEXT", "�Ʊ�ʱ�䣺" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGoodsDetail.jhw",
				printParm);
	}
    
    /*
     * ��ӡ�±�񷽷�
     */
    public void onPrintDown() {
		if(tableUp.getRowCount() <= 0){
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		TParm tableData = tableDown.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("RETURN_NO", tableData.getData("RETURN_NO", i));
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("RFID", tableData.getData("RFID", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("SUP_CHN_DESC", tableData.getValue("SUP_CHN_DESC", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "RETURN_NO");
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "RFID");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "�˻�ͳ����ϸ��");
		printParm.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
		printParm.setData("DATE", "TEXT", "�Ʊ�ʱ�䣺" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGoodsTotal.jhw",
				printParm);
	}
	
	/**
	 * �ϱ�񵥻��¼�
	 */
	public void onClick(){
		String invCode = tableUp.getValueAt(tableUp.getSelectedRow(), 0).toString();
		TParm parm = new TParm();
		parm.setData("INV_CODE", invCode);
		TParm result = InvRegressGoodsTotalTool.getInstance().onQueryRegressGoodsDetail(parm);
		if(result.getCount() <= 0){
			return;
		}
		tableDown.setParmValue(result);
	}
	
	/*
     * �����ϱ�excel����
     */
    public void onExcelUp(){
    	if(tableUp.getRowCount() <= 0){
			this.messageBox("û�����ݣ�");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(tableUp, "�����˻�ͳ��");
    }
    
    /*
     * �����±�excel����
     */
    public void onExcelDown(){
    	if(tableDown.getRowCount() <= 0){
			this.messageBox("û�����ݣ�");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(tableDown, "�����˻���ϸ");
    }
}