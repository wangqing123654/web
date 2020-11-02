package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
* <p>
* Title: �˻�����ͳ�Ʊ�
* </p>
*
* <p>
* Description: �˻�����ͳ�Ʊ�
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
* @author  Huangtt 2013.05.09
* @version 1.0
*/
public class INVRegressgoodsQueryControl extends TControl {
	public INVRegressgoodsQueryControl(){}
	
	private TTable table;
	String state="";
	
	/**
	 * ��ʼ��
	 */
	public void init(){
		super.init();
		initPage();

	}
	
	/**
     * ��ʼ��������
     */
    private void initPage() {
    	Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        //���TABLE����
        table=(	TTable)this.getComponent("TABLE");
		
    }
    
    /**
     * ��ѯ
     */
	public void onQuery(){

		
		String sql="SELECT F.DISPENSE_NO, A.DISPENSE_DATE, F.INV_CODE, F.INVSEQ_NO," +
				" C.INV_CHN_DESC, C.DESCRIPTION, B.QTY, E.UNIT_CHN_DESC, F.UNIT_PRICE," +
				" B.QTY * F.UNIT_PRICE AS AMT, B.VALID_DATE, B.BATCH_NO" +
				" FROM INV_DISPENSEM A," +
				" INV_DISPENSED B," +
				" INV_BASE C," +
				" INV_REQUESTD D," +
				" SYS_UNIT E," +
				" INV_DISPENSEDD F" +
				" WHERE A.DISPENSE_NO = B.DISPENSE_NO" +
				" AND B.DISPENSE_NO=F.DISPENSE_NO" +
				" AND F.INV_CODE = C.INV_CODE" +
				" AND A.REQUEST_NO = D.REQUEST_NO" +
				" AND B.DISPENSE_UNIT = E.UNIT_CODE";
		
		if (this.getRadioButton("RadioButton1").isSelected()) {
			sql += " AND F.FLG='N'";
			state="δ����";
		}
		if (this.getRadioButton("RadioButton2").isSelected()) {
			sql += " AND F.FLG='Y'";
			state="����";
		}
		
		 // ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "START_DATE")), "yyyyMMdd")+" 00:00:00";
	         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "END_DATE")), "yyyyMMdd")+" 23:59:59";
	         
//	         System.out.println(startTime);
	         
	         sql += " AND A.DISPENSE_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
        }
        
     // �˻�����
        
        if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
        	String orgCode=TypeTool.getString(getValue("TO_ORG_CODE"));
            sql +=" AND A.TO_ORG_CODE="+orgCode; 
        }else{
        	this.messageBox("�˻����Ų���Ϊ��");
            return;
        }
		
     // ���ʴ���
        if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
       	 	sql +=" AND F.INV_CODE='"+invCode+"'";
        }
        
        System.out.println(sql);
        
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        //�����˻��ܽ��   SUM_REG_AMT
        Double sum = 0.00;
        for(int i=0;i<selParm.getCount();i++){
        	sum += selParm.getDouble("AMT", i);
        }
//        System.out.println(sum);
        this.setValue("SUM_REG_AMT", sum);
        
        //����table�ϵ�����
	    this.callFunction("UI|TABLE|setParmValue", selParm);
		
	}
	
	  /**
     * ��ӡ����
     */
    public void onPrint() {
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyy/MM/dd HH:mm:ss");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy/MM/dd HH:mm:ss");
        Timestamp datetime = SystemTool.getInstance().getDate();

        table = this.getTable("TABLE");


        if (table.getRowCount() <= 0) {
           this.messageBox("û�д�ӡ����");
           return;
        }
        TParm data = new TParm();
        //��ͷ    getSelectedName
   	
//   	String supply=TypeTool.getString(getValue("VERIFYIN_DEPT"));
   	
        data.setData("TITLE", "TEXT", "�����˻���");
        data.setData("DEPT", "TEXT", "ͳ�Ʋ��ţ�"+TypeTool.getString(getValue("TO_ORG_CODE")));
        data.setData("STATE", "TEXT", "����״̬��"+state);
        data.setData("DATE_AREA", "TEXT", "ͳ�����䣺"+startTime+"~"+endTime);
        data.setData("DATE", "TEXT", "�Ʊ�����: " +
               datetime.toString().substring(0, 10).replace('-', '/'));
        data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        //����
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();

        for(int i=0;i<table.getRowCount();i++){
        	parm.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO",i));
        	parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC",i));
        	parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
        	parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC",i));
        	parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE",i));
        	parm.addData("QTY", tableParm.getValue("QTY",i));
        	parm.addData("AMT", tableParm.getValue("AMT",i));
        	parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO",i));
        	parm.addData("VALID_DATE", StringTool.getString(tableParm.getTimestamp("VALID_DATE",i),"yyyy/MM/dd"));
        	
   		
        }
   	
        parm.setCount(parm.getCount("DISPENSE_NO"));
        parm.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");       
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "AMT");
        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");



//        System.out.println(parm);
        data.setData("TABLE",parm.getData());
   	
        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVRegressgoodsQuery.jhw", data);

    	
    }
    
    
    
    
    /**
     * ��շ���
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_CHN_DESC;TO_ORG_CODE;SUM_REG_AMT"; 	
        this.clearValue(clearString);
     // ��ʼ����ѯ����
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        
        table.removeRowAll();
	}
    
    /**
     * �رմ���
     */
    public void close(){
         this.closeWindow();
    }
    
    /**
	 * ����EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
				"�������ͳ�Ʊ�");
	}
	

	
     /**
      * �õ�Table����
      *
      * @param tagName
      *            Ԫ��TAG����
      * @return
      */
     private TTable getTable(String tagName) {
         return (TTable) getComponent(tagName);
     }
     
     /**
      * �õ�RadioButton����
      *
      * @param tagName
      *            Ԫ��TAG����
      * @return
      */
     private TRadioButton getRadioButton(String tagName) {
         return (TRadioButton) getComponent(tagName);
     }

     
	

}
