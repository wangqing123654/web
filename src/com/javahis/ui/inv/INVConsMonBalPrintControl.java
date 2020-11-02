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
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
* <p>
* Title: ���������½ᱨ��
* </p>
*
* <p>
* Description: ���������½ᱨ��
* </p>
*
* <p>
* Copyright: Copyright (c) 2009
* </p>
*
* <p>
* Company: BlueCore
* </p>
*
* @author  Huangtt 2013.05.06
* @version 1.0
*/
public class INVConsMonBalPrintControl extends TControl{
	public INVConsMonBalPrintControl(){
		
	}
	private TTable table;
	
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
                      StringTool.rollDate(date, -30).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        
     // ��ʼ��ͳ������
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("S_DATE",
                      StringTool.rollDate(date, -30).toString().substring(0, 10).
                      replace('-', '/'));
        //���TABLE����
        table=(	TTable)this.getComponent("TABLE");
        TTextFormat dept = (TTextFormat) getComponent("DEPT");
        dept.setEnabled(false);
        dept.setValue(Operator.getDept());
    }
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+" 00:00:00";
		String endTime = StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+" 23:59:59";
		String dept=this.getValueString("DEPT");
		String sql = "SELECT B.INV_CHN_DESC, B.DESCRIPTION, COUNT(A.INV_CODE) QTY, C.MAN_CHN_DESC" +
				" FROM INV_STOCKDD A, INV_BASE B, SYS_MANUFACTURER C" +
				" WHERE A.INV_CODE like '08.%'" +
				" AND A.WAST_FLG = 'Y'" +
				" AND A.INV_CODE = B.INV_CODE" +
				" AND B.MAN_CODE = C.MAN_CODE" +
    			" AND A.OUT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
		
		sql += " GROUP BY C.MAN_CHN_DESC,A.INV_CODE,B.DESCRIPTION,B.INV_CHN_DESC";
		
	System.out.println("sql="+sql);
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
   	System.out.println("selParm="+selParm);
    	if(selParm.getCount()<0){
        	this.messageBox("û��Ҫ��ѯ������");
        	return;
        }
    	//����table�ϵ�����
	    this.callFunction("UI|TABLE|setParmValue", selParm);
    
    	
    	
    }
    
    /**
     * ��ӡ����
     */
    public void onPrint() {
    	// HH:mm:ss
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyy/MM/dd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy/MM/dd");
        Timestamp datetime = SystemTool.getInstance().getDate();

        table = this.getTable("TABLE");


        if (table.getRowCount() <= 0) {
           this.messageBox("û�д�ӡ����");
           return;
        }
        TParm data = new TParm();
        TTextFormat dept = (TTextFormat) getComponent("DEPT");
        data.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
        data.setData("TITLE", "TEXT", dept.getText()+"��������ʹ���嵥("+startTime+"---"+endTime+")");
        data.setData("DATE", "TEXT", "�Ʊ�����: " +datetime.toString().substring(0, 10).replace('-', '/'));
        data.setData("DEPT", "TEXT", "ִ�п��ң�"+dept.getText());
       
    	//����
    	TParm parm = new TParm();
    	TParm tableParm = table.getParmValue();
    	String manDesc = tableParm.getValue("MAN_CHN_DESC", 0);
    	
    	for(int i=0;i<table.getRowCount();i++){
    		if(!manDesc.equals(tableParm.getValue("MAN_CHN_DESC", i))){
    			parm.addData("INV_DESC", "����Ϊ"+manDesc+"�ṩ");
        		parm.addData("DESCRIPTION", "");
        		parm.addData("QTY", "");
        		manDesc = tableParm.getValue("MAN_CHN_DESC", i);
    		}
    		parm.addData("INV_DESC", tableParm.getValue("INV_CHN_DESC",i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
    		parm.addData("QTY", tableParm.getValue("QTY",i));
    		
    	}
    	if(manDesc.equals(tableParm.getValue("MAN_CHN_DESC", table.getRowCount()-1))){
    		parm.addData("INV_DESC", "����Ϊ"+manDesc+"�ṩ");
    		parm.addData("DESCRIPTION", "");
    		parm.addData("QTY", "");
    	}
    	
    	
    	parm.setCount(parm.getCount("INV_DESC"));
    	parm.addData("SYSTEM", "COLUMNS", "INV_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
    	parm.addData("SYSTEM", "COLUMNS", "QTY");

    	
    	data.setData("TABLE",parm.getData());
    	
    	this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVConsMonBalPrint.jhw", data);
    }
	
	/**
     * �رմ���
     */
    public void close(){
         this.closeWindow();
    }
    

	/**
     * ��շ���
     */
	
	public void onClear() {
		
		String clearString ="START_DATE;END_DATE"; 	
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ��ͳ������
        this.setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("S_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/'));
        // ��ʼ����ѯ����
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("START_DATE",StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        
        table.removeRowAll();
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

}
