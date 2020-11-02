package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.INVSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
* <p>
* Title: ����ͳ�Ʊ�
* </p>
*
* <p>
* Description: ����ͳ�Ʊ�
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
* @author  Huangtt 2013.05.10
* @version 1.0
*/
public class INVGoodsQueryControl extends TControl {
	public INVGoodsQueryControl(){}
	private TTable table;
	
	/**
	 * ��ʼ��
	 */
	public void init(){
		super.init();
	      //���TABLE����
        table=(	TTable)this.getComponent("TABLE");

	}
	
	/**
     * ��ѯ
     */
	public void onQuery(){
		String sql="SELECT A.INV_CODE, A.INV_CHN_DESC, A.DESCRIPTION, B.ORG_DESC," +
				" A.COST_PRICE, C.UNIT_CHN_DESC, A.ACTIVE_FLG, A.STOPBUY_FLG," +
				" A.NORMALDRUG_FLG, A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG," +
				" A.EXPENSIVE_FLG" +
				" FROM INV_BASE A, INV_ORG B, SYS_UNIT C, INV_VERIFYINM D,INV_VERIFYIND E" +
				" WHERE A.INV_CODE = E.INV_CODE AND E.VERIFYIN_NO=D.VERIFYIN_NO AND D.VERIFYIN_DEPT=B.ORG_CODE AND A.STOCK_UNIT = C.UNIT_CODE";
		if ("".equals(this.getValueString("INV_CODE")) && "".equals(this.getValueString("INV_CHN_DESC")) && "".equals(this.getValueString("ORG_CODE"))) {
			this.messageBox("��ѯ���Ų���Ϊ��");
            return;
        }
		
		 // ���ʴ���
        if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
        	sql +=" AND A.INV_CODE='"+invCode+"'";
        }
        
        // ��������
        if (!"".equals(this.getValueString("INV_CHN_DESC"))) {
        	String invCode=TypeTool.getString(getValue("INV_CHN_DESC"));
        	sql +=" AND A.INV_CHN_DESC= '"+invCode+"'";
        }
        
//        System.out.println(this.getValueString("INV_CHN_DESC"));
        
        // �ⷿ
        if (!"".equals(this.getValueString("ORG_CODE"))) {
        	String deptCode=TypeTool.getString(getValue("ORG_CODE"));
        	sql +=" AND B.ORG_CODE="+deptCode;
        }
        
//        System.out.println(sql);
        
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(selParm.getCount()<0){
        	this.messageBox("û��Ҫ��ѯ������");
        	return;
        }
//        System.out.println(selParm);
      //����table�ϵ�����
	    this.callFunction("UI|TABLE|setParmValue", selParm);
	}
	
	 /**
     * ��ӡ����
     */
    public void onPrint() {
    	Timestamp datetime = SystemTool.getInstance().getDate();
    	table = this.getTable("TABLE");


        if (table.getRowCount() <= 0) {
           this.messageBox("û�д�ӡ����");
           return;
        }
        TParm data = new TParm();
        //��ͷ    getSelectedName
   	

   	
        data.setData("TITLE", "TEXT", "����ͳ�Ʊ�");
       
        data.setData("DATE", "TEXT", "�Ʊ�����: " +
               datetime.toString().substring(0, 10).replace('-', '/'));
        data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        //���� ;;;;;;;;;;;;
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();

        for(int i=0;i<table.getRowCount();i++){
        	parm.addData("INV_CODE", tableParm.getValue("INV_CODE",i));
        	parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC",i));
        	parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
        	parm.addData("ORG_DESC", tableParm.getValue("ORG_DESC",i));
        	parm.addData("COST_PRICE", tableParm.getValue("COST_PRICE",i));
        	parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC",i));
//        	parm.addData("ACTIVE_FLG", tableParm.getValue("ACTIVE_FLG",i));
//        	parm.addData("STOPBUY_FLG", tableParm.getValue("STOPBUY_FLG",i));
//        	parm.addData("NORMALDRUG_FLG", tableParm.getTimestamp("NORMALDRUG_FLG",i));
//        	parm.addData("REQUEST_FLG", tableParm.getTimestamp("REQUEST_FLG",i));
//        	parm.addData("SEQMAN_FLG", tableParm.getTimestamp("SEQMAN_FLG",i));
//        	parm.addData("VALIDATE_FLG", tableParm.getTimestamp("VALIDATE_FLG",i));
//        	parm.addData("EXPENSIVE_FLG", tableParm.getTimestamp("EXPENSIVE_FLG",i));     	
   		
        }
   	
        parm.setCount(parm.getCount("INV_CODE"));
        parm.addData("SYSTEM", "COLUMNS", "INV_CODE");       
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//        parm.addData("SYSTEM", "COLUMNS", "ACTIVE_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "STOPBUY_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "NORMALDRUG_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "REQUEST_FLG");       
//        parm.addData("SYSTEM", "COLUMNS", "SEQMAN_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "VALIDATE_FLG");
//        parm.addData("SYSTEM", "COLUMNS", "EXPENSIVE_FLG");



//        System.out.println(parm);
        data.setData("TABLE",parm.getData());
   	
        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVGoogsQuery.jhw", data);
    	
    }
    
    /**
     * ���˫���¼�
     */
    public void onTable(){

//    	String inv_code=TypeTool.getString(getValue("INV_CODE"));
    	

    	
    	 int row = table.getSelectedRow();
         TParm parmValue = table.getParmValue();
         String inv_code= parmValue.getValue("INV_CODE", row);
        
//         System.out.println("1"+parmValue);
//    	 System.out.println("inv_code="+inv_code);
    	 TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getInvBaseInfo(inv_code)));
//    	 System.out.println(parm);
    	 this.openWindow("%ROOT%\\config\\inv\\INVBase.x",parm);

    	
    }
    
    
    /**
     * ��շ���
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_CHN_DESC;INVTYPE_CODE"; 	
        this.clearValue(clearString);
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
				"����ͳ�Ʊ�");
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
