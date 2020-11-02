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
 * Title: 科室备药生成Control
 * </p>
 *
 * <p>
 * Description: 科室备药生成Control
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

    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    // 申请单号
    private String request_no;

    // 全部部门权限
    private boolean dept_flg = true;

    // 门急住类别
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
 	// 以下作为表单排序的辅助
 	//private Compare compare = new Compare();
 	private BILComparator comparator=new BILComparator();
 	private boolean ascending = false;
 	private int sortColumn = -1;
 	// ==========modify-end========================================
    
    
    public INDDeptRequestControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 添加侦听事件
        addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
        // 给TABLE_M中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_M|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableMCheckBoxClicked");
        // 给TABLE_D中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableDCheckBoxClicked");
        
	    // ==========modify-begin (by wangjc 20171122)===============
		// 为表单添加监听器，为排序做准备。
		addListener((TTable) this.getComponent("TABLE_M"));
		addListener((TTable) this.getComponent("TABLE_D"));
		// ==========modify-end========================================

        // 初始画面数据
        initPage();
        onChangeRequestFlg();
    }
    
    
    /**
     * 查询方法
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
        //=======pangben modify 20110511 start 添加区域参数
        if(null!= Operator.getRegion()&& Operator.getRegion().length()>0){
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //=======pangben modify 20110511 stop
        // 已申请
        if (this.getRadioButton("REQUEST_FLG_A").isSelected()) {
            parm.setData("REQUEST_FLG_A", "Y");
        }else{
            parm.setData("REQUEST_FLG_B", "N");
        }
        
//    lirui 2012-6-4 start 对药品分类，管制毒麻药品
        // 返回结果集
        TParm result=new TParm(); 
        if (!"".equals(getValueString("REQUEST_NO"))) {           
            parm.setData("REQUEST_NO",getValueString("REQUEST_NO"));
        }
        // 药品管制
        if (getRadioButton("Normal").isSelected()) {
            // 普通药品
        	parm.setData("CTRLDRUGCLASS_CODE_A","A");
        	
        }
        if (getRadioButton("drug").isSelected()) {
            // 毒麻药
        	parm.setData("CTRLDRUGCLASS_CODE_B","B");
        }        
//        lirui 2012-6-4 end 对药品分类，管制毒麻药品
        //全部药品
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
        // fux modify 请领量为0的情况 20150826
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
//            this.messageBox("无查询数据");
//            return;
//        }
//        System.out.println(parmMNew);
        table_m.setParmValue(parmMNew);
        //System.out.println("parmD:::"+parmD);
        table_d.setParmValue(parmDNew);
        //默认计算总金额 by liyh 20120910  
        setSumRetailMoneyOnQuery(parmM);
    }
    
    /**
     * 拷贝TParm
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
//     * 拷贝TParm
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
     * 生成请领单
     */
    public void onSave() {
    	
    	table_m.getCellEditor(table_m.getColumnIndex("DOSAGE_QTY")).stopCellEditing();
    	table_m.acceptText();
    	TParm tmParm = table_m.getParmValue();
    	double dq;
    	for (int i = 0; i < tmParm.getCount("DOSAGE_QTY"); i++) {
			dq = tmParm.getDouble("DOSAGE_QTY", i);
			if(dq != (int)dq){
				messageBox("汇总中\""+tmParm.getValue("ORDER_DESC", i)+"\"数量不为整数\r\n请在汇总中重新输入该药品数量");
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
        // 整理数据，申请单主项
        getRequestExmParmM(parm);
        //System.out.println("parm--1-" + parm);
        // 整理数据，申请单细项
        getRequestExmParmD(parm);
        //System.out.println("parm--2-" + parm);
        // 判断更新类别(门急住)
        parm.setData("TYPE", type);
        // 整理数据，更新申请状态
        getDeptRequestUpdate(parm);

        TParm result = new TParm();
        //System.out.println("parm--3-" + parm);  
        String spcFlg = Operator.getSpcFlg() ;
        if(spcFlg != null && spcFlg.equals("Y")){
        	
        	 //调用物联网接口方法，由以前的onCreateDeptExmRequest改为onCreateDeptExmRequestSpc
	        result = TIOM_AppServer.executeAction(
	            "action.spc.INDRequestAction", "onCreateDeptExmRequestSpc", parm);
	
	        String msg = "" ;
	        // 保存判断
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
	            			msg += "不存在物联网药品对照编码" ;
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

            // 保存判断
            if (result1 == null || result1.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
		}
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
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
        // 清空画面内容
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
     * 过滤申请单号
     */
    public boolean request()
    {
        
        
//    	String REQUEST_NO = (String) table_m.getItemData(0,"REQUEST_NO");
//    	TParm num=new TParm(TJDODBTool.getInstance().select(INDSQL.checkData(REQUEST_NO)));
//        int number = num.getCount();
//        this.messageBox("wocao--"+number);
//        if(number>1)
//        {
//        	this.messageBox("申请单太多！");
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
  	      	this.messageBox("一次只能打印一个申请单的内容！");
  	      	return false;
  	      	
  	      }
  	      return true;
    }
    
    /**
     * 打印汇总单
     */
    public void onPrintM() {
	
	    	 boolean flg = true;
	         for (int i = 0; i < table_m.getRowCount(); i++) {
	             if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
	                 flg = false;
	             }
	         }
	         if (flg) {
	             this.messageBox("没有汇总信息");
	             return;
	         }
	    	boolean no= request();
	         if(no==true)
	         {
	        	 Timestamp datetime = StringTool.getTimestamp(new Date());

		          // 打印数据
		          TParm date = new TParm();
		          // 表头数据
		          date.setData("TITLE", "TEXT", Manager.getOrganization().
		                       getHospitalCHNFullName(Operator.getRegion()) +
		                       "科室备药单");
		        /*  date.setData("DATE_AREA", "TEXT", "申请单号:" + table_m.getItemData(0,"REQUEST_NO"));*/
		          date.setData("ORG_CODE_IN", "TEXT", "申请部门: " +
		                       this.getComboBox("APP_ORG_CODE").getSelectedName());
		          date.setData("ORG_CODE_OUT", "TEXT", "接受部门: " +
		                       this.getComboBox("TO_ORG_CODE").getSelectedName());
		          date.setData("DATE", "TEXT",
		                       "制表时间: " + datetime.toString().substring(0, 10));
		          // 表格数据
		          String order_code = "";
		          String unit_type = "1";
		          String order_desc = "";
		          TParm parm = new TParm();
		          int count=0;
		          int qty=0;//计算药品种类数量add by huangjw 20150310
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
		                  this.messageBox("药品信息有误");
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
		          // 表尾数据
//		          date.setData("STOCK_AMT", "TEXT", "采购总金额: " +
//		                       df2.format(Double.parseDouble(this.
//		              getValueString("SUM_VERIFYIN_PRICE"))));
		          
		          date.setData("OWN_AMT", "TEXT", "零售总金额: " +
		                       df2.format(Double.parseDouble(this.
		              getValueString("SUM_RETAIL_PRICE"))));
		          
//		          date.setData("DIFF_AMT", "TEXT", "进销差价: " +
//		                       StringTool.round(Double.parseDouble(this.
//		              getValueString("PRICE_DIFFERENCE")), 4));		          
//		          
		          if(count == 1 && this.getRadioButton("REQUEST_FLG_A").isSelected() ){
			          	date.setData("DATE_AREA", "TEXT", "申请单号:" + table_d.getParmValue().getValue("REQUEST_NO",0));
			      }
		          date.setData("USER", "TEXT", "制表人: " + Operator.getName());
		          date.setData("TOT_QTY", "TEXT", "药品种类总量: " + qty);//药品种类总量add by huangjw 20150310
		          // 调用打印方法
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
     * 打印明细单
     */
    public void onPrintD() {
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("没有明细信息");
            return;
        }
        boolean no= true;
        if(no==true)
        {
       	 Timestamp datetime = StringTool.getTimestamp(new Date());

	          // 打印数据
	          TParm date = new TParm();
	          // 表头数据
	          date.setData("TITLE", "TEXT", Manager.getOrganization().
	                       getHospitalCHNFullName(Operator.getRegion()) +
	                       "科室备药明细单");
	          
	          date.setData("ORG_CODE_IN", "TEXT", "申请部门: " +
	                       this.getComboBox("APP_ORG_CODE").getSelectedName());
	          date.setData("ORG_CODE_OUT", "TEXT", "接受部门: " +
	                       this.getComboBox("TO_ORG_CODE").getSelectedName());
	          date.setData("DATE", "TEXT",
	                       "制表时间: " + datetime.toString().substring(0, 10));
	          // 表格数据
	          String order_code = "";
	          String unit_type = "1";
	          String order_desc = "";
	          TParm parm = new TParm();
	          int count = 0;
	          for (int i = 0; i < table_d.getRowCount(); i++) {
	              if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
	                  continue;
	              }
	              count++;//计算打印有多少条数据
	              order_code = table_d.getParmValue().getValue("ORDER_CODE", i);
	              TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
	                  getOrderInfoByCode(order_code, unit_type)));
	              if (inparm == null || inparm.getErrCode() < 0) {
	                  this.messageBox("药品信息有误");
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
	              if(table_d.getParmValue().getValue("BILL_DATE",i).toString().length()>0){//如果计费日期为空，说明这是套餐医嘱
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
	          // 表尾数据
//	          date.setData("STOCK_AMT", "TEXT", "采购总金额: " +
//	                       df2.format(Double.parseDouble(this.
//	              getValueString("SUM_VERIFYIN_PRICE"))));
	          
	          date.setData("OWN_AMT", "TEXT", "零售总金额: " +
	                       df2.format(Double.parseDouble(this.
	              getValueString("SUM_RETAIL_PRICE"))));
	          
//	          date.setData("DIFF_AMT", "TEXT", "进销差价: " +
//	                       StringTool.round(Double.parseDouble(this.
//	              getValueString("PRICE_DIFFERENCE")), 4));		          
//	          
	          if(count == 1 && this.getRadioButton("REQUEST_FLG_A").isSelected() ){
		          	date.setData("DATE_AREA", "TEXT", "申请单号:" + table_d.getParmValue().getValue("REQUEST_NO",0));
		      }
	          date.setData("USER", "TEXT", "制表人: " + Operator.getName());
	          // 调用打印方法
	          this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DeptRequestD.jhw",
	                               date);
        }
    }
    /**
     * 查询科室中文
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
     * 查询医生中文
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
     * 变更申请部门
     */
    public void onChangeAppOrg() {
        if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
            // 预设归属库房
            TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"), Operator.getRegion())));
            getComboBox("TO_ORG_CODE").setSelectedID(sup_org_code.getValue(
                "SUP_ORG_CODE", 0));
        }
    }

    /**
     * 变更统计状态
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
     * 全选
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
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableMCheckBoxClicked(Object obj) {
        table_m.acceptText();
        //this.messageBox("2222222222");
        // 获得选中的列
        int column = table_m.getSelectedColumn();
        if (column == 0) {
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 4));
        }
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
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
                this.messageBox("申请数量不能小于或等于0");
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
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableDCheckBoxClicked(Object obj) {
        table_d.acceptText();
        // 获得选中的列
        int column = table_d.getSelectedColumn();
        if (column == 0) {
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 4));
        }
    }
    /**
     * 打印处方签
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
    		this.messageBox("请选择打印数据！");
    		return;
    	}
    	for(int k=0;k<tableParm.getCount();k++){
    		if(tableParm.getValue("SELECT_FLG",k).equals("N")){//如果这条数据没有勾选则循环下一条数据
    			continue;
    		}
    		boolean flg=false;
			for (int m = k + 1; m < tableParm.getCount(); m++) {
				if (tableParm.getValue("RX_NO", k).equals(
						tableParm.getValue("RX_NO", m))
						&& tableParm.getValue("SELECT_FLG", k).equals(
								tableParm.getValue("SELECT_FLG", m))
						&& tableParm.getValue("PRESRT_NO", k).equals(
								tableParm.getValue("PRESRT_NO", m))) {// 拿出第一条数据与其他的数据做比较
					flg = true;
					break;
				}

			}
	    	if(flg){//如果这条数据的处方号是重复的则循环下一条数据
	    		continue;
	    	}
	    	String caseNo=tableParm.getValue("CASE_NO",k);
	    	String mrNo=tableParm.getValue("MR_NO",k);
	    	String rxNo=tableParm.getValue("RX_NO",k);
	    	String presrtNo=tableParm.getValue("PRESRT_NO",k);
	    	String exeDeptCode=tableParm.getValue("EXEC_DEPT_CODE",k);
			// add caoyong 20140321 过敏药品 start
			String psdesc = "皮试结果(       )批号_____________ ";// 皮试
			String PS = "皮试";// 皮试
			StringBuffer buf = new StringBuffer();
			boolean flag = false;// 是否有过敏药
			boolean pflag = false;// 判断是否已经做过皮试
			boolean dosflg=false;//皮试用法用量不显示
			boolean newpsflg=true;
			TParm Aresult = ODOTool.getInstance().getAllergyData(mrNo);// 过敏药品
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
			//luhai add 处方签sql begin
			String rxNo2 = inParam.getValue("RX_NO") ;
			String caseNo2 =caseNo;
			//**********************************************************
			//luhai modify 2012-05-09 begin 非药品的项目不打印处方签 begin 
			//**********************************************************
	    	/*String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "+
				           " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "+
				           " OPD_ORDER.SPECIFICATION cc,OPD_ORDER.FREQ_CODE||' '  ss, "+
				           " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"+
				           " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"+
				           " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (16-LENGTH(SYS_PHAFREQ.FREQ_CHN_DESC)), ' ')|| OPD_ORDER.TAKE_DAYS FF,"+
				           " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "+
				           " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,OPD_ORDER.DR_NOTE DR,"+
				           " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE "+
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
			//luhai modify 2012-05-09 begin 非药品的项目不打印处方签  end
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
				this.messageBox("查询打印数据失败"); 
				return ;
			}
			if(westResult.getCount()<0){
				this.messageBox("没有处方签数据.") ;
				return ; 
			}  
			
			TParm westParm = new TParm() ;
			double pageAmt2 = 0 ; 
	//		DecimalFormat df2 = new DecimalFormat("############0.00");
			//add caoyong 20140322 皮试结果,批号--- start
			StringBuffer bufB = new StringBuffer();
			StringBuffer bufM = new StringBuffer();
			for (int i = 0; i < westResult.getCount(); i++) {
				if (PS.equals(westResult.getData("DD", i))) {// 是否是皮试用药
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
				if(dosflg){;//1.添加医师备注 2.频次显示为代码 modify by huangjw 20150324
//					westParm.addData("BB","                    "+westResult.getData("FF", i)+"  "+westResult.getData("DD", i));
					westParm.addData("BB","                    "+westResult.getData("FF", i)+"  "+westResult.getData("SS", i)+"  "+westResult.getData("DR", i));
				}else{
					//westParm.addData("BB", "    "+"用法：每次"+westResult.getData("HH", i)+"  "+westResult.getData("FF", i)+"  "+westResult.getData("DD", i)+"  "+westResult.getData("DR", i));
					westParm.addData("BB", "    " + "用法：每次"
							+ westResult.getData("HH", i) + "  "
							+ westResult.getData("FF", i) + "  "
							+ westResult.getData("DD", i) + "  "
							+westResult.getData("DR", i) );//1.添加医师备注 2.频次显示为代码  modify by huangjw 20150324
					/*//去掉医师备注 modify by huangjw 20141222
					westParm.addData("BB", "    "+"用法：每次"+westResult.getData("HH", i)+"  "+westResult.getData("FF", i)+"  "+westResult.getData("DD", i));*/
				}
				westParm.addData("CC", NULLSTR);
			// add caoyong 20140322 皮试结果,批号--- start
		
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
			// add caoyong 添加处方签添加皮试和过敏试验 2014、3、21 start
			inParam.setData("SKINTEST", "TEXT", psdesc);
			inParam.setData("TOT_AMT", "TEXT", df2.format(pageAmt2));
			inParam.setData("SIDE", "TEXT", "处方笺");
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
			// add caoyong 添加处方签添加皮试和过敏试验 2014、3、21 start
			
				
			inParam.setData("SKINTEST", "TEXT", psdesc);
			inParam.setData("TOT_AMT", "TEXT", df2.format(pageAmt2));
			inParam.setData("SIDE", "TEXT", "处方笺");
			if (newpsflg&&pflag) {
				//inParam.setData("SKINTEST", "TEXT", psdesc);
				inParam.setData("SKINTESTM", "TEXT", bufB.toString()
								.substring(1, bufB.toString().length()));
				inParam.setData("SKINTESTB", "TEXT", bufM.toString()
							.substring(1, bufM.toString().length()));
			}
			if (flag) {
				inParam.setData("ALLERGY", "TEXT", "过敏史:"
						+ allerg.substring(1, allerg.length()));
			}
			// add caoyong 添加处方签添加皮试和过敏试验 2014、3、21 end
			// =============modify by lim end
			//==liling 20140718 add 西药处方打印开关 start===
	//		String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdOrderSheet.prtSwitch");
	//		if(prtSwitch.equals(IReportTool.ON)){//不用自动打印，故不加该校验
			//==liling 20140718 add 西药处方打印开关 end===
			 Object obj = this.openPrintDialog(
					 "%ROOT%\\config\\prt\\OPD\\OpdOrderSheet_V45.jhw", inParam,
						false);
	//		}
    	}
    }
    /**
	 * 取得处方打印数据
	 * @param caseNo2
	 * @param rxNo2
	 * @return
	 */
	public String getWestsql(String caseNo2, String rxNo2){
		
		String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
			+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC ||' '|| OPD_ORDER.SPECIFICATION  END bb , "
			+ " OPD_ORDER.SPECIFICATION cc, "
			+ " OPD_ORDER.DR_NOTE DR,"
			+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
			+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
			//modigy  by huangtt 20141103 RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (LENGTH (sys_phafreq.freq_chn_desc)), ' ')  变为 SYS_PHAFREQ.FREQ_CHN_DESC||' '
			//+ " SYS_PHAFREQ.FREQ_CHN_DESC||' ' FF,"
			+ " OPD_ORDER.FREQ_CODE||' ' FF,"//获取频次代码add by huangjw 20150324
			+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.00') ELSE "
			+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC" +
			" ||CASE " +
			" WHEN " +
			" OPD_ORDER.EXEC_DEPT_CODE IN (SELECT ORG_CODE FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = 'C' AND EXINV_FLG = 'Y') " +
			" THEN '  (发药点:'||IND_ORG.ORG_CHN_DESC||')'" +
			" ELSE ''" +
			" END "+
			" ER ,"
			//modify by wanglong 20121226
			+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE, " 
			+ " OPD_ORDER.BATCH_NO, "//add caoyong 20140322 皮试批号 
			+ " CASE WHEN  OPD_ORDER.SKINTEST_FLG='0' THEN '(-)阴性' WHEN OPD_ORDER.SKINTEST_FLG='1' THEN '(+)阳性'  END SKINTEST_FLG,  "//add caoyong 20140322 皮试结果
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
     * 主项表格(TABLE_M)单击事件
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
     * 主项表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {

    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 取得门急住类别
        type = this.getParameter().toString();
        if("O".equals(type)){
        	this.setTitle("门诊科室备药生成");
        }else if("I".equals(type)){
        	this.setTitle("住院科室备药生成");
        }
        /**
         * 权限控制
         * 权限1:只显示自已所属科室
         * 权限9:最大权限,显示全院药库部门
         */
        // 判断是否显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                  "AND B.ORG_TYPE = 'C' AND B.EXINV_FLG = 'Y' ")));
            getComboBox("APP_ORG_CODE").setParmValue(parm);
            dept_flg = false;
            if (parm.getCount("NAME") > 0) {
                getComboBox("APP_ORG_CODE").setSelectedIndex(1);
            }
            // 预设归属库房getINDORG
            TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
                INDSQL.
                getINDORG(this.getValueString("APP_ORG_CODE"), Operator.getRegion())));
            getComboBox("TO_ORG_CODE").setSelectedID(sup_org_code.getValue(
                "SUP_ORG_CODE", 0));
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        //( (TMenuItem) getComponent("save")).setEnabled(false);
        ( (TMenuItem) getComponent("printM")).setEnabled(false);
        ( (TMenuItem) getComponent("printD")).setEnabled(false);
        ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
        //add by wangjc 20180905 门诊备药增加两列
        String header_o = "选,30,boolean;申请单号,100;药品名称,160;规格,120;数量,80,double,#####0.000;单位,60,UNIT;零售价,100,double,#####0.0000;零售金额,100,double,#####0.00";
    	String parmMap_o = "SELECT_FLG;REQUEST_NO;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT;MAN_NAME";
    	String columnHorizontalAlignmentData_o = "1,left;2,left;3,left;4,right;5,left;6,right;7,right;8,right;9,right;10,right;11,left";
    	String lockColumns = "1,2,3,4,5,6,7,8,9,10,11";
    	if ("O".equals(type)) {
    		header_o = "选,30,boolean;申请单号,100;药品名称,160;规格,120;请领总量,80,double,#####0.000;单位,60,UNIT;零售价,100,double,#####0.0000;零售金额,100,double,#####0.00;退药量,100;实际总量,100";
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
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("申请部门不能为空");
            return false;
        }
        if ("Y".equals(this.getValue("REQUEST_FLG_A"))) {
        	if ("".equals(getValueString("TO_ORG_CODE"))) {
        		this.messageBox("接收部门不能为空");
        		return false;
        	}
        }
        return true;
    }

    /**
     * 数据检验
     * @return boolean
     */
    private boolean CheckDataD() {
        if ("".equals(getValueString("TO_ORG_CODE"))) {
            this.messageBox("接受部门不能为空");
            return false;
        }
               
        if (table_m.getRowCount() == 0||table_d.getRowCount() == 0) {
            this.messageBox("没有申请数据");
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
            this.messageBox("没有申请数据");
            return false;
        }
        return true;
    }

    /**
     * 整理数据，申请单主项
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
        
     // 药品管制
        if (getRadioButton("Normal").isSelected()) {
            // 普通药品
        	inparm.setData("DRUG_CATEGORY", "1");
        	
        }else if (getRadioButton("drug").isSelected()) {
            // 毒麻药
        	inparm.setData("DRUG_CATEGORY", "2");
        } else {
        	inparm.setData("DRUG_CATEGORY", "3");
        }
        inparm.setData("APPLY_TYPE", "1");
        parm.setData("REQUEST_M", inparm.getData());
        return parm;
    }

    /**
     * 整理数据，申请单细项
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
     * 整理数据，更新申请状态
     * @param parm TParm
     * @return TParm
     */
    private TParm getDeptRequestUpdate(TParm parm) {
        TParm inparm = new TParm();
        int count = 0;
        //得到主表中已经勾选数据的orderCode、SPECIFICATION、unit_chn_desc和own_price
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
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 格式化字符串(时间格式)
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
     * 计算零售总金额
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
     * 计算采购/零售总金额
     *
     * @return
     * @author liyh
     * @date 20120910
     */
    private void setSumRetailMoneyOnQuery(TParm parmM) {
        //销售总金额
        double sum_retail = 0.0;
        //采购总金额
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
     * 计算成本总金额
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
     * 取得SYS_FEE信息，放置在状态栏上
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "药品代码:" + order.getValue("ORDER_CODE")
            + " 药品名称:" + order.getValue("ORDER_DESC")
            + " 商品名:" + order.getValue("GOODS_DESC")
            + " 规格:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }
    
    
    // ==========modify-begin (by wangjc 20171122)===============
 	// 以下为响应鼠标单击事件的方法：用于获取全部单元格的值，并按某列排序。以及相关辅助方法。
 	/**
 	 * 加入表格排序监听方法
 	 * @param table TTable
 	 */
 	public void addListener(final TTable table) {
 		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
 			public void mouseClicked(MouseEvent me) {
 				int i = table.getTable().columnAtPoint(me.getPoint());
 				int j = table.getTable().convertColumnIndexToModel(i);
 				// 调用排序方法;
 				// 转换出用户想排序的列和底层数据的列，然后判断 f
 				if (j == sortColumn) {
 					ascending = !ascending;
 				} else {
 					ascending = true;
 					sortColumn = j;
 				}
 				// 表格中parm值一致,
 				// 1.取paramw值;
 				TParm tableData = table.getParmValue();
 				// 2.转成 vector列名, 行vector ;
 				String columnName[] = tableData.getNames("Data");
 				String strNames = "";
 				for (String tmp : columnName) {
 					strNames += tmp + ";";
 				}
 				strNames = strNames.substring(0, strNames.length() - 1);
 				Vector vct = getVector(tableData, "Data", strNames, 0);
 				// 3.根据点击的列,对vector排序
 				// System.out.println("sortColumn===="+sortColumn);
 				// 表格排序的列名;
 				String tblColumnName = table.getParmMap(sortColumn);
 				// 转成parm中的列
 				int col = tranParmColIndex(columnName, tblColumnName);
 				// System.out.println("==col=="+col);
 				comparator.setDes(ascending);
 				comparator.setCol(col);
 				java.util.Collections.sort(vct, comparator);
 				// 将排序后的vector转成parm;
 				cloneVectoryParam(vct, new TParm(), strNames,table);
 				//getTMenuItem("save").setEnabled(false);
 			}
 		});
 	}
 	
 	/**
	 * 得到 Vector 值
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
	 * 转换parm中的列
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}
		return index;
	}
	
	/**
	 * vectory转成param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames,TTable table) {
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);
	}
	// ==========modify-end========================================
}
