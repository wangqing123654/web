package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inv.INVVerifyInReportTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��������ͳ��Control 
 * </p>
 *
 * <p>
 * Description: ��������ͳ��Control 
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

public class INVVerifyInReportControl
    extends TControl {
	
    public INVVerifyInReportControl() {
    }
    
    private TTable table;
    
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
//		this.setValue("ORG_CODE", Operator.getRegion());
		table = (TTable) this.getComponent("TABLE");
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
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
		//���ʴ���
		if(this.getValueString("INV_CODE") != null && this.getValueString("INV_CODE").length() > 0){
			String invCode = this.getValueString("INV_CODE");
			parm.setData("INV_CODE", invCode);
		}
		//���ղ���
		if(this.getValueString("ORG_CODE") != null && this.getValueString("ORG_CODE").length() > 0){
			String orgCode = this.getValueString("ORG_CODE");
			parm.setData("VERIFYIN_DEPT", orgCode);
		}
		TParm result = INVVerifyInReportTool.getInstance().onQueryVerifyInReport(parm);
		if(result.getCount() <= 0){
			this.messageBox("�޲�ѯ���");
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);
	}
	
	/*
     * ��շ���
     */
    public void onClear(){
    	this.clearValue("INV_CODE;INV_DESC;SUP_CODE;ORG_CODE");
    	table.removeRowAll();
    }
    
    /*
     * ��ӡ����
     */
    public void onPrint(){
    	print();
    }

	private void print() {
		if(table.getRowCount() <= 0){
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}
		TParm tableData = table.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("DEPT_ABS_DESC", tableData.getData("DEPT_ABS_DESC", i));
			printData.addData("SUP_ABS_DESC", tableData.getData("SUP_ABS_DESC", i));
			String verifyinDate = "",validdate = "";
			Timestamp verifyInDate = tableData.getTimestamp("VERIFYIN_DATE", i);
			Timestamp validDate = tableData.getTimestamp("VALID_DATE", i);
			if(verifyInDate != null)
				verifyinDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(verifyInDate.getTime()));
			printData.addData("VERIFYIN_DATE", verifyinDate);
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INVSEQ_NO", tableData.getData("INVSEQ_NO", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("GIFT_QTY", tableData.getData("GIFT_QTY", i));
			printData.addData("UNIT_CHN_DESC", tableData.getData("UNIT_CHN_DESC", i));
			printData.addData("UNIT_PRICE", tableData.getData("UNIT_PRICE", i));
			printData.addData("IN_PRICE", tableData.getData("IN_PRICE", i));
			if(validDate != null)
				validdate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(validDate.getTime()));
			printData.addData("VALID_DATE", validdate);
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "SUP_ABS_DESC");
		printData.addData("SYSTEM", "COLUMNS", "VERIFYIN_DATE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "GIFT_QTY");
		printData.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "IN_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "�������ͳ�Ʊ�");
		if(this.getValueString("ORG_CODE") != null && this.getValueString("ORG_CODE").length() > 0){
			TTextFormat dept = (TTextFormat) getComponent("ORG_CODE");
			printParm.setData("ORG_CODE", "TEXT", "���ղ��ţ�" + dept.getText());
		}else{
			printParm.setData("ORG_CODE", "TEXT", "���ղ��ţ�����");
		}
		printParm.setData("DATE", "TEXT", "�Ʊ�ʱ�䣺" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVVerifyInReport.jhw",
				printParm);
	}
	
	/*
     * ����excel����
     */
    public void onExecl(){
    	if(table.getRowCount() <= 0){
			this.messageBox("û�����ݣ�");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "��������������");
    }
    
    /**
     * �õ�TextField����
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
