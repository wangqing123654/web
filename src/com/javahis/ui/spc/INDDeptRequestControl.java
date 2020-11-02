package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.bil.BILComparator;
import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.ODOTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ���ұ�ҩ����Control
 * </p>
 *
 * <p>
 * Description: ���ұ�ҩ����Control
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
 * @author zhangy 2009.05.12
 * @version 1.0
 */
public class INDDeptRequestControl
    extends TControl {

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    // ���뵥��
    private String request_no;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    // �ż�ס���
    private String type;
    private ODO odo;
    private static final String NULLSTR = "";
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
    "##########0.0000");
    
    java.text.DecimalFormat df3 = new java.text.DecimalFormat(
    "##########0.000");
    
    java.text.DecimalFormat df2 = new java.text.DecimalFormat(
    "##########0.00");
    
 // ==========modify-begin (by wangjc 20171122)===============
 	// ������Ϊ������ĸ���
 	//private Compare compare = new Compare();
 	private BILComparator comparator=new BILComparator();
 	private boolean ascending = false;
 	private int sortColumn = -1;
 	// ==========modify-end========================================
    
    
    public INDDeptRequestControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��������¼�
        addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
        // ��TABLE_M�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_M|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableMCheckBoxClicked");
        // ��TABLE_D�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableDCheckBoxClicked");
        
	    // ==========modify-begin (by wangjc 20171122)===============
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener((TTable) this.getComponent("TABLE_M"));
		addListener((TTable) this.getComponent("TABLE_D"));
		// ==========modify-end========================================

        // ��ʼ��������
        initPage();
        onChangeRequestFlg();
    }
    
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
        if (!CheckDataM()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));    
        parm.setData("START_DATE",
                     formatString(this.getValueString("START_DATE")));
        parm.setData("END_DATE", formatString(this.getValueString("END_DATE")));
        if ("O".equals(type) || "E".equals(type)) {
            parm.setData("TYPE", "OPD");
        }
        else if ("I".equals(type)) {
            parm.setData("TYPE", "IBS");
        }     
        //=======pangben modify 20110511 start ����������
        if(null!= Operator.getRegion()&& Operator.getRegion().length()>0){
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //=======pangben modify 20110511 stop
        // ������
        if (this.getRadioButton("REQUEST_FLG_A").isSelected()) {
            parm.setData("REQUEST_FLG_A", "Y");
        }else{
            parm.setData("REQUEST_FLG_B", "N");
        }
        
//    lirui 2012-6-4 start ��ҩƷ���࣬���ƶ���ҩƷ
        // ���ؽ����
        TParm result=new TParm(); 
        if (!"".equals(getValueString("REQUEST_NO"))) {           
            parm.setData("REQUEST_NO",getValueString("REQUEST_NO"));
        }
        // ҩƷ����
        if (getRadioButton("Normal").isSelected()) {
            // ��ͨҩƷ
        	parm.setData("CTRLDRUGCLASS_CODE_A","A");
        	
        }
        if (getRadioButton("drug").isSelected()) {
            // ����ҩ
        	parm.setData("CTRLDRUGCLASS_CODE_B","B");
        }        
//        lirui 2012-6-4 end ��ҩƷ���࣬���ƶ���ҩƷ
        //ȫ��ҩƷ
        result = INDTool.getInstance().onQueryDeptExm(parm);
//        result = TIOM_AppServer.executeAction(
//            "action.ind.INDRequestAction", "onQueryDeptExm", parm);
        TParm parmM = result.getParm("RESULT_M",0);
        TParm parmD = result.getParm("RESULT_D",0);
        //add by wangjc 20180905
        if("O".equals(type)){
        	if(this.getRadioButton("REQUEST_FLG_A").isSelected()){
        		if(parmM.getCount()>0){
        			boolean flg = true;
        			for (int i = 0; i < parmM.getCount(); i++) {
        				flg = true;
        				for (int j = 0; j < parmD.getCount(); j++) {
        					if(parmD.getValue("ORDER_CODE", j).equals(parmM.getValue("ORDER_CODE", i)) && parmD.getValue("REQUEST_NO", j).equals(parmM.getValue("REQUEST_NO", i))){
        						parmM.setData("REAL_QTY",i, parmM.getDouble("REAL_QTY", i)+parmD.getDouble("DOSAGE_QTY", j));
        						double returnQty = parmM.getDouble("DOSAGE_QTY", i) - parmM.getDouble("REAL_QTY", i);
                				parmM.setData("RETURN_QTY",i, returnQty);
                				flg = false;
        					}
        				}
        				if(flg){
        					parmM.setData("REAL_QTY",i, parmM.getDouble("DOSAGE_QTY", i));
        					parmM.setData("RETURN_QTY",i, 0);
        				}
        			}
        		}
        	}else{
        		for (int i = 0; i < parmM.getCount(); i++) {
        			parmM.setData("REAL_QTY",i, parmM.getDouble("DOSAGE_QTY", i));
					parmM.setData("RETURN_QTY",i, 0);
    			}
        	}
        }
//        System.out.println(parmM);
        // fux modify ������Ϊ0����� 20150826
        TParm parmMNew = new TParm();  
        TParm parmDNew = new TParm();
        for (int i = 0; i < parmM.getCount(); i++) {  
        	if(!"0".equals(parmM.getValue("DOSAGE_QTY", i))){
        		cloneTParm(parmM.getRow(i),parmMNew,i);	   
        	}         
		}                  
        for (int i = 0; i < parmD.getCount(); i++) {  
         	if(!"0".equals(parmD.getValue("DOSAGE_QTY", i))){
         		cloneTParm(parmD.getRow(i),parmDNew,i); 	     
        	}   
        	
		}
//        if (parmM.getCount() == 0 || parmD.getCount("STOCK_PRICE") <= 0) {
//            this.messageBox("�޲�ѯ����");
//            return;
//        }
//        System.out.println(parmMNew);
        table_m.setParmValue(parmMNew);
        //System.out.println("parmD:::"+parmD);
        table_d.setParmValue(parmDNew);
        //Ĭ�ϼ����ܽ�� by liyh 20120910  
        setSumRetailMoneyOnQuery(parmM);
    }
    
    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from,TParm to,int row){
        String names[] = from.getNames();
        for(int i = 0;i < names.length;i++){
            Object obj = from.getData(names[i]);              
            if(obj == null)
                obj = "";  
            to.addData(names[i],obj);
        }
     }
    
//    /**
//     * ����TParm
//     * @param from TParm
//     * @param to TParm
//     * @param row int
//     */
//    private void cloneTParm(TParm from, TParm to, int row) {
//        for (int i = 0; i < from.getNames().length; i++) {
//            to.addData(from.getNames()[i],
//                       from.getValue(from.getNames()[i], row));
//        }
//    }
    

    /**
     * �������쵥
     */
    public void onSave() {
    	
    	table_m.getCellEditor(table_m.getColumnIndex("DOSAGE_QTY")).stopCellEditing();
    	table_m.acceptText();
    	TParm tmParm = table_m.getParmValue();
    	double dq;
    	for (int i = 0; i < tmParm.getCount("DOSAGE_QTY"); i++) {
			dq = tmParm.getDouble("DOSAGE_QTY", i);
			if(dq != (int)dq){
				messageBox("������\""+tmParm.getValue("ORDER_DESC", i)+"\"������Ϊ����\r\n���ڻ��������������ҩƷ����");
				return;
			}
		}
    	
        if (!CheckDataM()) {
            return;
        }
        if (!CheckDataD()) {
            return;
        }
        TParm parm = new TParm();
        // �������ݣ����뵥����
        getRequestExmParmM(parm);
        //System.out.println("parm--1-" + parm);
        // �������ݣ����뵥ϸ��
        getRequestExmParmD(parm);
        //System.out.println("parm--2-" + parm);
        // �жϸ������(�ż�ס)
        parm.setData("TYPE", type);
        // �������ݣ���������״̬
        getDeptRequestUpdate(parm);

        TParm result = new TParm();
        //System.out.println("parm--3-" + parm);  
        String spcFlg = Operator.getSpcFlg() ;
        if(spcFlg != null && spcFlg.equals("Y")){
        	
        	 //�����������ӿڷ���������ǰ��onCreateDeptExmRequest��ΪonCreateDeptExmRequestSpc
	        result = TIOM_AppServer.executeAction(
	            "action.spc.INDRequestAction", "onCreateDeptExmRequestSpc", parm);
	
	        String msg = "" ;
	        // �����ж�
	        if (result == null || result.getErrCode() < 0) {
	            //this.messageBox(result.getErrText());
	            String errText = result.getErrText() ;
	            String [] errCode = errText.split(";");
	            for(int i = 0 ; i < errCode.length ; i++){
	            	String orderCode = errCode[i];
	            	TParm returnParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
	            	if(returnParm != null && returnParm.getCount() >  0 ){
	            		returnParm = returnParm.getRow(0);
	            		msg += orderCode +" "+returnParm.getValue("ORDER_DESC")+"  "+ returnParm.getValue("SPECIFICATION")+"\n";
	            		if( i == errCode.length-1 ){
	            			msg += "������������ҩƷ���ձ���" ;
	            		}
	            	}else{
	            		msg += orderCode +"\n";
	            	}
	            }
	            this.messageBox(msg);
	            return;
	        }
        }else {
        	TParm result1 = TIOM_AppServer.executeAction(
                    "action.spc.INDRequestAction", "onCreateDeptExmRequest", parm);

            // �����ж�
            if (result1 == null || result1.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
		}
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("printM")).setEnabled(true);
        ( (TMenuItem) getComponent("printD")).setEnabled(false);
        ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
        table_m.setVisible(true);
        table_m.removeRowAll();
        table_d.setVisible(false);
        table_d.removeRowAll();
        // ��ջ�������
        String clearString =
            "APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
            +
            "SELECT_ALL;URGENT_FLG;CHECK_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
            + "PRICE_DIFFERENCE";
        clearValue(clearString);
        getRadioButton("REQUEST_FLG_B").setSelected(true);
        getRadioButton("REQUEST_TYPE_A").setSelected(true);
    }

    /**
     * �������뵥��
     */
    public boolean request()
    {
        
        
//    	String REQUEST_NO = (String) table_m.getItemData(0,"REQUEST_NO");
//    	TParm num=new TParm(TJDODBTool.getInstance().select(INDSQL.checkData(REQUEST_NO)));
//        int number = num.getCount();
//        this.messageBox("wocao--"+number);
//        if(number>1)
//        {
//        	this.messageBox("���뵥̫�࣡");
//        	
//        }
      
    	
    	  Set<String> set = new HashSet<String>();
          for(int i=0;i<table_m.getRowCount();i++)
          {
        	  if("Y".equals(table_m.getItemString(i, "SELECT_FLG"))){
        		  set.add((String) table_m.getItemData(i, "REQUEST_NO"));
        	  }
          }
            int number = set.size();
  	      if(number>1)
  	      {
  	      	this.messageBox("һ��ֻ�ܴ�ӡһ�����뵥�����ݣ�");
  	      	return false;
  	      	
  	      }
  	      return true;
    }
    
    /**
     * ��ӡ���ܵ�
     */
    public void onPrintM() {
	
	    	 boolean flg = true;
	         for (int i = 0; i < table_m.getRowCount(); i++) {
	             if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
	                 flg = false;
	             }
	         }
	         if (flg) {
	             this.messageBox("û�л�����Ϣ");
	             return;
	         }
	    	boolean no= request();
	         if(no==true)
	         {
	        	 Timestamp datetime = StringTool.getTimestamp(new Date());

		          // ��ӡ����
		          TParm date = new TParm();
		          // ��ͷ����
		          date.setData("TITLE", "TEXT", Manager.getOrganization().
		                       getHospitalCHNFullName(Operator.getRegion()) +
		                       "���ұ�ҩ��");
		        /*  date.setData("DATE_AREA", "TEXT", "���뵥��:" + table_m.getItemData(0,"REQUEST_NO"));*/
		          date.setData("ORG_CODE_IN", "TEXT", "���벿��: " +
		                       this.getComboBox("APP_ORG_CODE").getSelectedName());
		          date.setData("ORG_CODE_OUT", "TEXT", "���ܲ���: " +
		                       this.getComboBox("TO_ORG_CODE").getSelectedName());
		          date.setData("DATE", "TEXT",
		                       "�Ʊ�ʱ��: " + datetime.toString().substring(0, 10));
		          // �������
		          String order_code = "";
		          String unit_type = "1";
		          String order_desc = "";
		          TParm parm = new TParm();
		          int count=0;
		          int qty=0;//����ҩƷ��������add by huangjw 20150310
		          for (int i = 0; i < table_m.getRowCount(); i++) {
		              if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
		                  continue;
		              }
		              qty++;
		              count++;
		              order_code = table_m.getParmValue().getValue("ORDER_CODE", i);
		              TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
		                  getOrderInfoByCode(order_code, unit_type)));
		              if (inparm == null || inparm.getErrCode() < 0) {
		                  this.messageBox("ҩƷ��Ϣ����");
		                  return;
		              }
		              if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
		                  order_desc = inparm.getValue("ORDER_DESC", 0);
		              }
		              else {
		                  order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
		                      inparm.getValue("GOODS_DESC", 0) + ")";
		              }
		              parm.addData("ORDER_DESC", order_desc);
		         
		              parm.addData("SPECIFICATION",
		                           table_m.getItemData(i, "SPECIFICATION"));
		              parm.addData("UNIT", table_m.getItemData(i, "UNIT_CHN_DESC"));
		              parm.addData("QTY", df3.format(table_m.getItemDouble(i, "DOSAGE_QTY")));
//		              parm.addData("STOCK_PRICE",
//		            		  df4.format(table_m.getItemDouble(i, "STOCK_PRICE")));
//		              parm.addData("STOCK_AMT", df2.format(table_m.getItemDouble(i, "STOCK_AMT")));
		              parm.addData("OWN_PRICE", df4.format(table_m.getItemDouble(i, "OWN_PRICE")));
		              parm.addData("OWN_AMT", df2.format(table_m.getItemDouble(i, "OWN_AMT")));
		              if("O".equals(type)){
		            	  parm.addData("RETURN_QTY", df3.format(table_m.getItemDouble(i, "RETURN_QTY")));
		            	  parm.addData("REAL_QTY", df3.format(table_m.getItemDouble(i, "REAL_QTY")));
		              }
		          }
		          parm.setCount(parm.getCount("ORDER_DESC"));
		          parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		          parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		          parm.addData("SYSTEM", "COLUMNS", "UNIT");
		          parm.addData("SYSTEM", "COLUMNS", "QTY");
//		          parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
//		          parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
		          parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		          parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		          if("O".equals(type)){
		        	  parm.addData("SYSTEM", "COLUMNS", "RETURN_QTY");
			          parm.addData("SYSTEM", "COLUMNS", "REAL_QTY");
		          }
//		          System.out.println(parm);
		          date.setData("TABLE", parm.getData());
		          // ��β����
//		          date.setData("STOCK_AMT", "TEXT", "�ɹ��ܽ��: " +
//		                       df2.format(Double.parseDouble(this.
//		              getValueString("SUM_VERIFYIN_PRICE"))));
		          
		          date.setData("OWN_AMT", "TEXT", "�����ܽ��: " +
		                       df2.format(Double.parseDouble(this.
		              getValueString("SUM_RETAIL_PRICE"))));
		          
//		          date.setData("DIFF_AMT", "TEXT", "�������: " +
//		                       StringTool.round(Double.parseDouble(this.
//		              getValueString("PRICE_DIFFERENCE")), 4));		          
//		          
		          if(count == 1 && this.getRadioButton("REQUEST_FLG_A").isSelected() ){
			          	date.setData("DATE_AREA", "TEXT", "���뵥��:" + table_d.getParmValue().getValue("REQUEST_NO",0));
			      }
		          date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
		          date.setData("TOT_QTY", "TEXT", "ҩƷ��������: " + qty);//ҩƷ��������add by huangjw 20150310
		          // ���ô�ӡ����
		          if("O".equals(type)){
		        	  this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DeptRequestMO.jhw",
		        			  date);
		          }else{
		        	  this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DeptRequestM.jhw",
		        			  date);
		          }
	         }

	      
      
    }

    /**
     * ��ӡ��ϸ��
     */
    public void onPrintD() {
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("û����ϸ��Ϣ");
            return;
        }
        boolean no= true;
        if(no==true)
        {
       	 Timestamp datetime = StringTool.getTimestamp(new Date());

	          // ��ӡ����
	          TParm date = new TParm();
	          // ��ͷ����
	          date.setData("TITLE", "TEXT", Manager.getOrganization().
	                       getHospitalCHNFullName(Operator.getRegion()) +
	                       "���ұ�ҩ��ϸ��");
	          
	          date.setData("ORG_CODE_IN", "TEXT", "���벿��: " +
	                       this.getComboBox("APP_ORG_CODE").getSelectedName());
	          date.setData("ORG_CODE_OUT", "TEXT", "���ܲ���: " +
	                       this.getComboBox("TO_ORG_CODE").getSelectedName());
	          date.setData("DATE", "TEXT",
	                       "�Ʊ�ʱ��: " + datetime.toString().substring(0, 10));
	          // �������
	          String order_code = "";
	          String unit_type = "1";
	          String order_desc = "";
	          TParm parm = new TParm();
	          int count = 0;
	          for (int i = 0; i < table_d.getRowCount(); i++) {
	              if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
	                  continue;
	              }
	              count++;//�����ӡ�ж���������
	              order_code = table_d.getParmValue().getValue("ORDER_CODE", i);
	              TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
	                  getOrderInfoByCode(order_code, unit_type)));
	              if (inparm == null || inparm.getErrCode() < 0) {
	                  this.messageBox("ҩƷ��Ϣ����");
	                  return;
	              }
	              if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
	                  order_desc = inparm.getValue("ORDER_DESC", 0);
	              }
	              else {
	                  order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
	                      inparm.getValue("GOODS_DESC", 0) + ")";
	              }
	              parm.addData("REQUEST_NO", table_d.getParmValue().getValue("REQUEST_NO",i));
	              parm.addData("MR_NO", table_d.getItemData(i, "MR_NO"));
	              parm.addData("PAT_NAME", table_d.getItemData(i, "PAT_NAME"));
	              if(table_d.getParmValue().getValue("BILL_DATE",i).toString().length()>0){//����Ʒ�����Ϊ�գ�˵�������ײ�ҽ��
	            	  parm.addData("BILL_DATE", table_d.getParmValue().getValue("BILL_DATE",i).toString().substring(0, 10));
	              }else{
	            	  parm.addData("BILL_DATE", "");
	              }	            	  
	              parm.addData("ORDER_DESC", order_desc);
	              parm.addData("DEPT_CODE", selectDeptDesc(table_d.getItemData(i, "DEPT_CODE").toString()));
	              parm.addData("DR_CODE", selectDrDesc(table_d.getItemData(i, "DR_CODE").toString()));
	              parm.addData("SPECIFICATION",
	                           table_d.getItemData(i, "SPECIFICATION"));
	              parm.addData("UNIT", table_d.getItemData(i, "UNIT_CHN_DESC"));
	              parm.addData("QTY", df3.format(table_d.getItemDouble(i, "DOSAGE_QTY")));
//	              parm.addData("STOCK_PRICE",
//	            		  df4.format(table_m.getItemDouble(i, "STOCK_PRICE")));
//	              parm.addData("STOCK_AMT", df2.format(table_m.getItemDouble(i, "STOCK_AMT")));
	              parm.addData("OWN_PRICE", df4.format(table_d.getItemDouble(i, "OWN_PRICE")));
	              parm.addData("OWN_AMT", df2.format(table_d.getItemDouble(i, "OWN_AMT")));
	          }
	          parm.setCount(parm.getCount("ORDER_DESC"));
	          parm.addData("SYSTEM", "COLUMNS", "REQUEST_NO");
	          parm.addData("SYSTEM", "COLUMNS", "MR_NO");
	          parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
	          parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
	          parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
	          parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
	          parm.addData("SYSTEM", "COLUMNS", "DR_CODE");
	          parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
	          parm.addData("SYSTEM", "COLUMNS", "QTY");
	          parm.addData("SYSTEM", "COLUMNS", "UNIT");
//	          parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
//	          parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
	          parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
	          parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
	          date.setData("TABLE", parm.getData());
	          // ��β����
//	          date.setData("STOCK_AMT", "TEXT", "�ɹ��ܽ��: " +
//	                       df2.format(Double.parseDouble(this.
//	              getValueString("SUM_VERIFYIN_PRICE"))));
	          
	          date.setData("OWN_AMT", "TEXT", "�����ܽ��: " +
	                       df2.format(Double.parseDouble(this.
	              getValueString("SUM_RETAIL_PRICE"))));
	          
//	          date.setData("DIFF_AMT", "TEXT", "�������: " +
//	                       StringTool.round(Double.parseDouble(this.
//	              getValueString("PRICE_DIFFERENCE")), 4));		          
//	          
	          if(count == 1 && this.getRadioButton("REQUEST_FLG_A").isSelected() ){
		          	date.setData("DATE_AREA", "TEXT", "���뵥��:" + table_d.getParmValue().getValue("REQUEST_NO",0));
		      }
	          date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
	          // ���ô�ӡ����
	          this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DeptRequestD.jhw",
	                               date);
        }
    }
    /**
     * ��ѯ��������
     * @param deptCode
     * @return
     */
    private String selectDeptDesc(String deptCode){
    	String deptDesc = "";
    	String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+deptCode+"'";
    	TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(inparm.getCount()>0){
    		deptDesc = inparm.getValue("DEPT_CHN_DESC",0);
    	}
    	return deptDesc;
    	
    }
    /**
     * ��ѯҽ������
     * @param deptCode
     * @return
     */
    private String selectDrDesc(String drCode){
    	String drDesc = "";
    	String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+drCode+"'";
    	TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(inparm.getCount()>0){
    		drDesc = inparm.getValue("USER_NAME",0);
    	}
    	return drDesc;
    	
    }

    /**
     * ������벿��
     */
    public void onChangeAppOrg() {
        if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
            // Ԥ������ⷿ
            TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"), Operator.getRegion())));
            getComboBox("TO_ORG_CODE").setSelectedID(sup_org_code.getValue(
                "SUP_ORG_CODE", 0));
        }
    }

    /**
     * ���ͳ��״̬
     */
    public void onChangeRequestFlg() {
        if (this.getRadioButton("REQUEST_FLG_B").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
                ( (TMenuItem) getComponent("printM")).setEnabled(true);
                ( (TMenuItem) getComponent("printD")).setEnabled(false);
                ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
                table_m.setVisible(true);
                table_d.setVisible(false);
            }
            else {
                ( (TMenuItem) getComponent("printM")).setEnabled(false);
                ( (TMenuItem) getComponent("printD")).setEnabled(true);
                ( (TMenuItem) getComponent("printRecipe")).setEnabled(true);
                table_m.setVisible(false);
                table_d.setVisible(true);
            }
            this.setValue("SELECT_ALL", "N");
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
                ( (TMenuItem) getComponent("printM")).setEnabled(true);
                ( (TMenuItem) getComponent("printD")).setEnabled(false);
                ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
                table_m.setVisible(true);
                table_d.setVisible(false);
                this.setValue("SELECT_ALL", "Y");
            }
            else {
                ( (TMenuItem) getComponent("printM")).setEnabled(false);
                ( (TMenuItem) getComponent("printD")).setEnabled(true);
                ( (TMenuItem) getComponent("printRecipe")).setEnabled(true);
                table_m.setVisible(false);
                table_d.setVisible(true);
                this.setValue("SELECT_ALL", "N");
            }
        }
        this.onQuery();
    }

    /**
     * ȫѡ
     */
    public void onSelectAll() {
        String flg = "N";
        if (getCheckBox("SELECT_ALL").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        for (int i = 0; i < table_m.getRowCount(); i++) {
            table_m.setItem(i, "SELECT_FLG", flg);
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
            table_d.setItem(i, "SELECT_FLG", flg);
        }
        setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
        setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
        setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
            - getSumRegMoney(), 4));
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableMCheckBoxClicked(Object obj) {
        table_m.acceptText();
        //this.messageBox("2222222222");
        // ���ѡ�е���
        int column = table_m.getSelectedColumn();
        if (column == 0) {
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 4));
        }
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        int row = node.getRow();

        if (column == 0) {
//            this.messageBox("11111111");
//            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
//            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
//            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
//                - getSumRegMoney(), 4));
            return false;
        }


        if (column == 4) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            double amt1 = StringTool.round(qty * table_m.getItemDouble(row,
                "STOCK_PRICE"), 2);
            double amt2 = StringTool.round(qty * table_m.getItemDouble(row,
                "OWN_PRICE"), 2);
            table_m.setItem(row, "STOCK_AMT", amt1);
            table_m.setItem(row, "OWN_AMT", amt2);
            table_m.setItem(row, "DIFF_AMT", amt2 - amt1);
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                                                          - getSumRegMoney(), 4));
            return false;
        }
        return true;
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableDCheckBoxClicked(Object obj) {
        table_d.acceptText();
        // ���ѡ�е���
        int column = table_d.getSelectedColumn();
        if (column == 0) {
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 4));
        }
    }
    /**
     * ��ӡ����ǩ
     */
    public void onPrintRecipe(){
    	TParm tableParm=table_d.getParmValue();
    	TParm tableParm1=table_d.getParmValue();
    	int count=0;
    	for(int i=0;i<tableParm.getCount();i++){
    		if("Y".equals(tableParm.getValue("SELECT_FLG",i))){
    			count++;
    		}
    	}
    	if(count==0){
    		this.messageBox("��ѡ���ӡ���ݣ�");
    		return;
    	}
    	for(int k=0;k<tableParm.getCount();k++){
    		if(tableParm.getValue("SELECT_FLG",k).equals("N")){//�����������û�й�ѡ��ѭ����һ������
    			continue;
    		}
    		boolean flg=false;
			for (int m = k + 1; m < tableParm.getCount(); m++) {
				if (tableParm.getValue("RX_NO", k).equals(
						tableParm.getValue("RX_NO", m))
						&& tableParm.getValue("SELECT_FLG", k).equals(
								tableParm.getValue("SELECT_FLG", m))
						&& tableParm.getValue("PRESRT_NO", k).equals(
								tableParm.getValue("PRESRT_NO", m))) {// �ó���һ���������������������Ƚ�
					flg = true;
					break;
				}

			}
	    	if(flg){//����������ݵĴ��������ظ�����ѭ����һ������
	    		continue;
	    	}
	    	String caseNo=tableParm.getValue("CASE_NO",k);
	    	String mrNo=tableParm.getValue("MR_NO",k);
	    	String rxNo=tableParm.getValue("RX_NO",k);
	    	String presrtNo=tableParm.getValue("PRESRT_NO",k);
	    	String exeDeptCode=tableParm.getValue("EXEC_DEPT_CODE",k);
			// add caoyong 20140321 ����ҩƷ start
			String psdesc = "Ƥ�Խ��(       )����_____________ ";// Ƥ��
			String PS = "Ƥ��";// Ƥ��
			StringBuffer buf = new StringBuffer();
			boolean flag = false;// �Ƿ��й���ҩ
			boolean pflag = false;// �ж��Ƿ��Ѿ�����Ƥ��
			boolean dosflg=false;//Ƥ���÷���������ʾ
			boolean newpsflg=true;
			TParm Aresult = ODOTool.getInstance().getAllergyData(mrNo);// ����ҩƷ
			if (Aresult.getCount() > 0) {
				for (int j = 0; j < Aresult.getCount(); j++) {
					buf.append(",").append(Aresult.getValue("ORDER_DESC", j))
							.append(" ")
							.append(Aresult.getValue("ALLERGY_NOTE", j));
				}
				flag = true;
			}
			String allerg = buf.toString();
			DecimalFormat df2 = new DecimalFormat("############0.00");
	    	//case_no mr_no 
			odo = new ODO(caseNo, mrNo, Operator.getDept(), Operator.getID(),
					"O");
	//		TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
	//				realDeptCode, rxType, odo, rxNo,
	//				order.getItemString(0, "PSY_FLG"));
			//030101
	//		System.out.println(caseNo+"====="+exeDeptCode+"===="+rxNo);
			TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
					exeDeptCode, "1", odo, rxNo,
					"Y");
			//luhai add ����ǩsql begin
			String rxNo2 = inParam.getValue("RX_NO") ;
			String caseNo2 =caseNo;
			//**********************************************************
			//luhai modify 2012-05-09 begin ��ҩƷ����Ŀ����ӡ����ǩ begin 
			//**********************************************************
	    	/*String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '��' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "+
				           " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "+
				           " OPD_ORDER.SPECIFICATION cc,OPD_ORDER.FREQ_CODE||' '  ss, "+
				           " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN 'Ƥ��' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"+
				           " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"+
				           " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (16-LENGTH(SYS_PHAFREQ.FREQ_CHN_DESC)), ' ')|| OPD_ORDER.TAKE_DAYS FF,"+
				           " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "+
				           " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,OPD_ORDER.DR_NOTE DR,"+
				           " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '�Ա�  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE "+
				         " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE "+
				         " WHERE       CASE_NO = '"+caseNo2+"'"+
				         "  AND RX_NO = '"+rxNo2+"'"+
				         "  @ "+
				         " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "+
				         "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "+
				         "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "+
				         "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "+
				         "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "+
				         "  AND OPD_ORDER.CAT1_TYPE='PHA' "+
				         " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO" ;*/
			//**********************************************************
			//luhai modify 2012-05-09 begin ��ҩƷ����Ŀ����ӡ����ǩ  end
			//**********************************************************
	//    	TParm westResult = new TParm(TJDODBTool.getInstance().select(westsql));
	    	String westsql = this.getWestsql(
					caseNo2, rxNo2);
	    	if(!"".equals(presrtNo) && presrtNo!=null){
	    		westsql=westsql.replace("@", "AND PRESRT_NO='"+presrtNo+"'");
	    	}else{
	    		westsql=westsql.replace("@", "");
	    	}
	    	TParm westResult=new TParm(TJDODBTool.getInstance().select(
	    			westsql));
			if(westResult.getErrCode()<0){
				this.messageBox("��ѯ��ӡ����ʧ��"); 
				return ;
			}
			if(westResult.getCount()<0){
				this.messageBox("û�д���ǩ����.") ;
				return ; 
			}  
			
			TParm westParm = new TParm() ;
			double pageAmt2 = 0 ; 
	//		DecimalFormat df2 = new DecimalFormat("############0.00");
			//add caoyong 20140322 Ƥ�Խ��,����--- start
			StringBuffer bufB = new StringBuffer();
			StringBuffer bufM = new StringBuffer();
			for (int i = 0; i < westResult.getCount(); i++) {
				if (PS.equals(westResult.getData("DD", i))) {// �Ƿ���Ƥ����ҩ
					psdesc = "";
					dosflg=true;
					newpsflg=false;
				}else{
					dosflg=false;
				}
				if (!"".equals(westResult.getData("BATCH_NO", i))){
					bufB.append(",").append(
							westResult.getData("BATCH_NO", i));
					bufM.append(",").append(
							westResult.getData("SKINTEST_FLG", i));
					pflag = true;
				}
				westParm.addData("AA", "");
				westParm.addData("BB", westResult.getData("BB", i)+"  "+westResult.getData("ER", i));
				westParm.addData("CC", NULLSTR);
				
				westParm.addData("AA", NULLSTR);
				if(dosflg){;//1.���ҽʦ��ע 2.Ƶ����ʾΪ���� modify by huangjw 20150324
//					westParm.addData("BB","                    "+westResult.getData("FF", i)+"  "+westResult.getData("DD", i));
					westParm.addData("BB","                    "+westResult.getData("FF", i)+"  "+westResult.getData("SS", i)+"  "+westResult.getData("DR", i));
				}else{
					//westParm.addData("BB", "    "+"�÷���ÿ��"+westResult.getData("HH", i)+"  "+westResult.getData("FF", i)+"  "+westResult.getData("DD", i)+"  "+westResult.getData("DR", i));
					westParm.addData("BB", "    " + "�÷���ÿ��"
							+ westResult.getData("HH", i) + "  "
							+ westResult.getData("FF", i) + "  "
							+ westResult.getData("DD", i) + "  "
							+westResult.getData("DR", i) );//1.���ҽʦ��ע 2.Ƶ����ʾΪ����  modify by huangjw 20150324
					/*//ȥ��ҽʦ��ע modify by huangjw 20141222
					westParm.addData("BB", "    "+"�÷���ÿ��"+westResult.getData("HH", i)+"  "+westResult.getData("FF", i)+"  "+westResult.getData("DD", i));*/
				}
				westParm.addData("CC", NULLSTR);
			// add caoyong 20140322 Ƥ�Խ��,����--- start
		
			pageAmt2 += (westResult.getDouble("DOSAGE_QTY", i)
					* westResult.getDouble("OWN_PRICE", i) * westResult
					.getDouble("DISCOUNT_RATE", i));// modify by
													// wanglong 20121226
			pageAmt2 = StringTool.round(pageAmt2, 2);// add by
			}
			westParm.setCount(westParm.getCount("AA"));
			westParm.addData("SYSTEM", "COLUMNS","");
			westParm.addData("SYSTEM", "COLUMNS", "BB");
			westParm.addData("SYSTEM", "COLUMNS", "CC");
			inParam.setData("ORDER_TABLE", westParm.getData());
			// add caoyong ��Ӵ���ǩ���Ƥ�Ժ͹������� 2014��3��21 start
			inParam.setData("SKINTEST", "TEXT", psdesc);
			inParam.setData("TOT_AMT", "TEXT", df2.format(pageAmt2));
			inParam.setData("SIDE", "TEXT", "������");
			if (pflag) {
				inParam.setData("SKINTESTM", "TEXT", bufB.toString()
						.substring(1, bufB.toString().length()));
				inParam.setData("SKINTESTB", "TEXT", bufM.toString()
						.substring(1, bufM.toString().length()));
			}
			westParm.setCount(westParm.getCount("AA"));
			westParm.addData("SYSTEM", "COLUMNS","");
			westParm.addData("SYSTEM", "COLUMNS", "BB");
			westParm.addData("SYSTEM", "COLUMNS", "CC");
			westParm.addData("SYSTEM", "COLUMNS", "DR");
			inParam.setData("ORDER_TABLE", westParm.getData());
			// add caoyong ��Ӵ���ǩ���Ƥ�Ժ͹������� 2014��3��21 start
			
				
			inParam.setData("SKINTEST", "TEXT", psdesc);
			inParam.setData("TOT_AMT", "TEXT", df2.format(pageAmt2));
			inParam.setData("SIDE", "TEXT", "������");
			if (newpsflg&&pflag) {
				//inParam.setData("SKINTEST", "TEXT", psdesc);
				inParam.setData("SKINTESTM", "TEXT", bufB.toString()
								.substring(1, bufB.toString().length()));
				inParam.setData("SKINTESTB", "TEXT", bufM.toString()
							.substring(1, bufM.toString().length()));
			}
			if (flag) {
				inParam.setData("ALLERGY", "TEXT", "����ʷ:"
						+ allerg.substring(1, allerg.length()));
			}
			// add caoyong ��Ӵ���ǩ���Ƥ�Ժ͹������� 2014��3��21 end
			// =============modify by lim end
			//==liling 20140718 add ��ҩ������ӡ���� start===
	//		String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdOrderSheet.prtSwitch");
	//		if(prtSwitch.equals(IReportTool.ON)){//�����Զ���ӡ���ʲ��Ӹ�У��
			//==liling 20140718 add ��ҩ������ӡ���� end===
			 Object obj = this.openPrintDialog(
					 "%ROOT%\\config\\prt\\OPD\\OpdOrderSheet_V45.jhw", inParam,
						false);
	//		}
    	}
    }
    /**
	 * ȡ�ô�����ӡ����
	 * @param caseNo2
	 * @param rxNo2
	 * @return
	 */
	public String getWestsql(String caseNo2, String rxNo2){
		
		String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '��' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
			+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC ||' '|| OPD_ORDER.SPECIFICATION  END bb , "
			+ " OPD_ORDER.SPECIFICATION cc, "
			+ " OPD_ORDER.DR_NOTE DR,"
			+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN 'Ƥ��' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
			+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
			//modigy  by huangtt 20141103 RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (LENGTH (sys_phafreq.freq_chn_desc)), ' ')  ��Ϊ SYS_PHAFREQ.FREQ_CHN_DESC||' '
			//+ " SYS_PHAFREQ.FREQ_CHN_DESC||' ' FF,"
			+ " OPD_ORDER.FREQ_CODE||' ' FF,"//��ȡƵ�δ���add by huangjw 20150324
			+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.00') ELSE "
			+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC" +
			" ||CASE " +
			" WHEN " +
			" OPD_ORDER.EXEC_DEPT_CODE IN (SELECT ORG_CODE FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = 'C' AND EXINV_FLG = 'Y') " +
			" THEN '  (��ҩ��:'||IND_ORG.ORG_CHN_DESC||')'" +
			" ELSE ''" +
			" END "+
			" ER ,"
			//modify by wanglong 20121226
			+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '�Ա�  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE, " 
			+ " OPD_ORDER.BATCH_NO, "//add caoyong 20140322 Ƥ������ 
			+ " CASE WHEN  OPD_ORDER.SKINTEST_FLG='0' THEN '(-)����' WHEN OPD_ORDER.SKINTEST_FLG='1' THEN '(+)����'  END SKINTEST_FLG,  "//add caoyong 20140322 Ƥ�Խ��
			//modify end
			+ " OPD_ORDER.DOSAGE_QTY || C.UNIT_CHN_DESC ||'/' || B.UNIT_CHN_DESC AS TT, "
			+ " RTRIM (RTRIM (TO_CHAR (OPD_ORDER.MEDI_QTY,'FM9999999990.000' ),'0'),'.')|| ''|| A.UNIT_CHN_DESC AS HH"
			+ " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE, SYS_UNIT C,PHA_TRANSUNIT, IND_ORG "
			+ " WHERE       CASE_NO = '"
			+ caseNo2
			+ "'"
			+ "  AND RX_NO = '"
			+ rxNo2
			+ "'"
			+ " @ "
			+ " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
			+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
			+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
			+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
			+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
			+ "  AND OPD_ORDER.DOSAGE_UNIT=C.UNIT_CODE "
            + "  AND OPD_ORDER.ORDER_CODE=PHA_TRANSUNIT.ORDER_CODE " +
            " AND OPD_ORDER.EXEC_DEPT_CODE = IND_ORG.ORG_CODE(+) "
			+ " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
		
//		System.out.println("westsql::::::::"+westsql);
		
		return westsql;
	}
    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
    	int selRow = table_m.getSelectedRow();
    	String request_no = table_m.getItemString(selRow, "REQUEST_NO");
    	TParm result = getUrgentFlg(request_no);
    	this.setValue("URGENT_FLG", result.getValue("URGENT_FLG",0));
    }
    
    private TParm getUrgentFlg(String requrst_no) {
		String sql = "SELECT URGENT_FLG FROM IND_REQUESTM WHERE REQUEST_NO = '"+requrst_no+"'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

    /**
     * ������(TABLE_D)�����¼�
     */
    public void onTableDClicked() {

    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ȡ���ż�ס���
        type = this.getParameter().toString();
        if("O".equals(type)){
        	this.setTitle("������ұ�ҩ����");
        }else if("I".equals(type)){
        	this.setTitle("סԺ���ұ�ҩ����");
        }
        /**
         * Ȩ�޿���
         * Ȩ��1:ֻ��ʾ������������
         * Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ��
         */
        // �ж��Ƿ���ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                  "AND B.ORG_TYPE = 'C' AND B.EXINV_FLG = 'Y' ")));
            getComboBox("APP_ORG_CODE").setParmValue(parm);
            dept_flg = false;
            if (parm.getCount("NAME") > 0) {
                getComboBox("APP_ORG_CODE").setSelectedIndex(1);
            }
            // Ԥ������ⷿgetINDORG
            TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
                INDSQL.
                getINDORG(this.getValueString("APP_ORG_CODE"), Operator.getRegion())));
            getComboBox("TO_ORG_CODE").setSelectedID(sup_org_code.getValue(
                "SUP_ORG_CODE", 0));
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        //( (TMenuItem) getComponent("save")).setEnabled(false);
        ( (TMenuItem) getComponent("printM")).setEnabled(false);
        ( (TMenuItem) getComponent("printD")).setEnabled(false);
        ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
        //add by wangjc 20180905 ���ﱸҩ��������
        String header_o = "ѡ,30,boolean;���뵥��,100;ҩƷ����,160;���,120;����,80,double,#####0.000;��λ,60,UNIT;���ۼ�,100,double,#####0.0000;���۽��,100,double,#####0.00";
    	String parmMap_o = "SELECT_FLG;REQUEST_NO;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT;MAN_NAME";
    	String columnHorizontalAlignmentData_o = "1,left;2,left;3,left;4,right;5,left;6,right;7,right;8,right;9,right;10,right;11,left";
    	String lockColumns = "1,2,3,4,5,6,7,8,9,10,11";
    	if ("O".equals(type)) {
    		header_o = "ѡ,30,boolean;���뵥��,100;ҩƷ����,160;���,120;��������,80,double,#####0.000;��λ,60,UNIT;���ۼ�,100,double,#####0.0000;���۽��,100,double,#####0.00;��ҩ��,100;ʵ������,100";
    		parmMap_o = "SELECT_FLG;REQUEST_NO;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT;RETURN_QTY;REAL_QTY;MAN_NAME";
    		columnHorizontalAlignmentData_o += ";12,right;13,right";
    		lockColumns = "1,2,3,4,5,6,7,8,9,10,11,12,13";
        }
    	this.table_m.setHeader(header_o);
    	this.table_m.setParmMap(parmMap_o);
    	this.table_m.setColumnHorizontalAlignmentData(columnHorizontalAlignmentData_o);
    	this.table_m.setLockColumns(lockColumns);
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if ("Y".equals(this.getValue("REQUEST_FLG_A"))) {
        	if ("".equals(getValueString("TO_ORG_CODE"))) {
        		this.messageBox("���ղ��Ų���Ϊ��");
        		return false;
        	}
        }
        return true;
    }

    /**
     * ���ݼ���
     * @return boolean
     */
    private boolean CheckDataD() {
        if ("".equals(getValueString("TO_ORG_CODE"))) {
            this.messageBox("���ܲ��Ų���Ϊ��");
            return false;
        }
               
        if (table_m.getRowCount() == 0||table_d.getRowCount() == 0) {
            this.messageBox("û����������");
            return false;
        }
        boolean flg = true;
        int i = 0;
        for (; i < table_m.getRowCount(); i++) {
            if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if(i!=table_m.getRowCount()){
            for (int j = 0; j < table_d.getRowCount(); j++) {
                if ("Y".equals(table_d.getItemString(j, "SELECT_FLG"))) {
                    flg = false;
                }
            }
        }
        if (flg) {
            this.messageBox("û����������");
            return false;
        }
        return true;
    }

    /**
     * �������ݣ����뵥����
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestExmParmM(TParm parm) {
        TParm inparm = new TParm();
        Timestamp date = StringTool.getTimestamp(new Date());
        request_no = SystemTool.getInstance().getNo("ALL", "IND", "IND_REQUEST",
                                                    "No");
        inparm.setData("REQUEST_NO", request_no);
        inparm.setData("REQTYPE_CODE", "EXM");
        inparm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
        inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        inparm.setData("REQUEST_DATE", date);
        inparm.setData("REQUEST_USER", Operator.getID());
        inparm.setData("REASON_CHN_DESC", this.getValueString("REASON_CHN_DESC"));
        inparm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        inparm.setData("UNIT_TYPE", "1");
        inparm.setData("URGENT_FLG",
                       this.getValueString("URGENT_FLG") == "N" ? "N" : "Y");
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", date);
        inparm.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        inparm.setData("REGION_CODE", Operator.getRegion());
        
     // ҩƷ����
        if (getRadioButton("Normal").isSelected()) {
            // ��ͨҩƷ
        	inparm.setData("DRUG_CATEGORY", "1");
        	
        }else if (getRadioButton("drug").isSelected()) {
            // ����ҩ
        	inparm.setData("DRUG_CATEGORY", "2");
        } else {
        	inparm.setData("DRUG_CATEGORY", "3");
        }
        inparm.setData("APPLY_TYPE", "1");
        parm.setData("REQUEST_M", inparm.getData());
        return parm;
    }

    /**
     * �������ݣ����뵥ϸ��
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestExmParmD(TParm parm) {
        TParm inparm = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        Timestamp date = SystemTool.getInstance().getDate();
        String user_id = Operator.getID();
        String user_ip = Operator.getIP();
        int count = 0;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            inparm.addData("REQUEST_NO", request_no);
            inparm.addData("SEQ_NO", count + 1);
            inparm.addData("ORDER_CODE",
                           table_m.getParmValue().getValue("ORDER_CODE", i));
            inparm.addData("BATCH_NO", "");
            inparm.addData("VALID_DATE", tnull);
            inparm.addData("QTY", table_m.getItemDouble(i, "DOSAGE_QTY"));
            inparm.addData("ACTUAL_QTY", 0);
            inparm.addData("UPDATE_FLG", "0");
            inparm.addData("OPT_USER", user_id);
            inparm.addData("OPT_DATE", date);
            inparm.addData("OPT_TERM", user_ip);
            count++;
        }
        parm.setData("REQUEST_D", inparm.getData());
        return parm;
    }

    /**
     * �������ݣ���������״̬
     * @param parm TParm
     * @return TParm
     */
    private TParm getDeptRequestUpdate(TParm parm) {
        TParm inparm = new TParm();
        int count = 0;
        //�õ��������Ѿ���ѡ���ݵ�orderCode��SPECIFICATION��unit_chn_desc��own_price
        for(int i = 0;i<table_m.getRowCount();i++){
        	if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
        	String orderCodem = table_m.getItemString(i, "ORDER_CODE");
        	String specificationm = table_m.getItemString(i, "SPECIFICATION");
        	String unitCodem = table_m.getItemString(i, "UNIT_CHN_DESC");
        	String ownPricem = table_m.getItemString(i, "OWN_PRICE");
        	for(int j = 0;j<table_d.getRowCount();j++){
        		String orderCoded = table_d.getItemString(j, "ORDER_CODE");
        		String specificationd = table_d.getItemString(j, "SPECIFICATION");
        		String unitCoded = table_d.getItemString(j, "UNIT_CHN_DESC");
        		String ownPriced = table_d.getItemString(j, "OWN_PRICE");
        		if(orderCodem.equals(orderCoded)&&specificationm.equals(specificationd)&&
        				unitCodem.equals(unitCoded)&&ownPricem.equals(ownPriced)){
        			table_d.setItem(j, "SELECT_FLG", "Y");
        		}
        	}
        }
        if ("I".equals(type)) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                inparm.setData("CASE_NO", count,
                               table_d.getParmValue().getValue("CASE_NO", i));
                inparm.setData("CASE_NO_SEQ", count,
                               table_d.getParmValue().getInt("CASE_NO_SEQ", i));
                inparm.setData("SEQ_NO", count,
                               table_d.getParmValue().getInt("SEQ_NO", i));
                inparm.setData("REQUEST_FLG", count, "Y");
                inparm.setData("REQUEST_NO", count, request_no);
                count++;
            }
        }
        else if ("O".equals(type) || "E".equals(type)) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                inparm.setData("CASE_NO", count,
                               table_d.getParmValue().getValue("CASE_NO", i));
                inparm.setData("RX_NO", count,
                               table_d.getParmValue().getValue("RX_NO", i));
                inparm.setData("SEQ_NO", count,
                               table_d.getParmValue().getInt("SEQ_NO", i));
                inparm.setData("REQUEST_FLG", count, "Y");
                inparm.setData("REQUEST_NO", count, request_no);
                count++;
            }
        }
        parm.setData("UPDATE", inparm.getData());
        return parm;
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
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
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

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * ��ʽ���ַ���(ʱ���ʽ)
     * @param arg String
     * @return String YYYYMMDDHHMMSS
     */
    private String formatString(String arg) {
        arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10) +
            arg.substring(11, 13) + arg.substring(14, 16) +
            arg.substring(17, 19);
        return arg;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailMoney() {
        table_m.acceptText();
        table_d.acceptText();
        double sum = 0;
        if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                sum += table_m.getItemDouble(i, "OWN_AMT");
            }
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                sum += table_d.getItemDouble(i, "OWN_AMT");
            }
        }
        return StringTool.round(sum, 4);
    }
    
    /**
     * ����ɹ�/�����ܽ��
     *
     * @return
     * @author liyh
     * @date 20120910
     */
    private void setSumRetailMoneyOnQuery(TParm parmM) {
        //�����ܽ��
        double sum_retail = 0.0;
        //�ɹ��ܽ��
        double sum_verifyin=0.0;
        int count = parmM.getCount();
        if (null != parmM && count > 0) {
            for (int i = 0; i < count; i++) {
            	sum_retail   += parmM.getDouble("OWN_AMT",i);
            	sum_verifyin += parmM.getDouble("STOCK_AMT",i);
            }
            
        }
        setValue("SUM_RETAIL_PRICE", sum_retail);
        setValue("SUM_VERIFYIN_PRICE", sum_verifyin);
    }    
    

    /**
     * ����ɱ��ܽ��
     *
     * @return
     */
    private double getSumRegMoney() {
        table_m.acceptText();
        table_d.acceptText();
        double sum = 0;
        if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
            for (int i = 0; i < table_m.getRowCount(); i++) {
                if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                sum += table_m.getItemDouble(i, "STOCK_AMT");
            }
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                sum += table_d.getItemDouble(i, "STOCK_AMT");
            }
        }
        return StringTool.round(sum, 4);
    }

    /**
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }
    
    
    // ==========modify-begin (by wangjc 20171122)===============
 	// ����Ϊ��Ӧ��굥���¼��ķ��������ڻ�ȡȫ����Ԫ���ֵ������ĳ�������Լ���ظ���������
 	/**
 	 * �����������������
 	 * @param table TTable
 	 */
 	public void addListener(final TTable table) {
 		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
 			public void mouseClicked(MouseEvent me) {
 				int i = table.getTable().columnAtPoint(me.getPoint());
 				int j = table.getTable().convertColumnIndexToModel(i);
 				// �������򷽷�;
 				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
 				if (j == sortColumn) {
 					ascending = !ascending;
 				} else {
 					ascending = true;
 					sortColumn = j;
 				}
 				// �����parmֵһ��,
 				// 1.ȡparamwֵ;
 				TParm tableData = table.getParmValue();
 				// 2.ת�� vector����, ��vector ;
 				String columnName[] = tableData.getNames("Data");
 				String strNames = "";
 				for (String tmp : columnName) {
 					strNames += tmp + ";";
 				}
 				strNames = strNames.substring(0, strNames.length() - 1);
 				Vector vct = getVector(tableData, "Data", strNames, 0);
 				// 3.���ݵ������,��vector����
 				// System.out.println("sortColumn===="+sortColumn);
 				// ������������;
 				String tblColumnName = table.getParmMap(sortColumn);
 				// ת��parm�е���
 				int col = tranParmColIndex(columnName, tblColumnName);
 				// System.out.println("==col=="+col);
 				comparator.setDes(ascending);
 				comparator.setCol(col);
 				java.util.Collections.sort(vct, comparator);
 				// ��������vectorת��parm;
 				cloneVectoryParam(vct, new TParm(), strNames,table);
 				//getTMenuItem("save").setEnabled(false);
 			}
 		});
 	}
 	
 	/**
	 * �õ� Vector ֵ
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	
	/**
	 * ת��parm�е���
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}
	
	/**
	 * vectoryת��param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames,TTable table) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);
	}
	// ==========modify-end========================================
}
