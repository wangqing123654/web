package com.javahis.ui.mem;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.bil.PaymentTool;
import jdo.mem.MEMTool;
import jdo.opb.OPBTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title:礼品卡售卡</p>
*
* <p>Description: 礼品卡售卡</p>
*
* <p>Copyright: Copyright (c) /p>
*
* <p>Company: BlueCore</p>
*
* @author huangtt 2013140107
* @version 1.0
*/

public class MEMGiftCardSalesCardControl extends TControl{
	
	private static TTable tableM;
	private static TTable tableD;
	private static boolean updateFlg;
	PaymentTool paymentTool;
	
	 /**
     * 初始化
     */
    public void onInit(){
    	tableM = (TTable) getComponent("TABLE_M"); 
    	tableD = (TTable) getComponent("TABLE_D"); 
    	//初始化查询时间
    	Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 date.toString().substring(0, 10).replace('-', '/')
							+ " 00:00:00");
		 this.setValue("END_DATE", date.toString()
					.substring(0, 10).replace('-', '/')
					+ " 23:59:59");
		// 注册激发MEMCaseCardinfo弹出的事件
		 tableD.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponentUD");
		 
		 tableD.addEventListener("TABLE_D->"+
				 TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
		 
		 this.callFunction("UI|TABLE_M|addEventListener",
					TTableEvent.CHECK_BOX_CLICKED, this,
					"onTableMClicked");  
		 updateFlg=false;
		 
		 TPanel p = (TPanel) getComponent("tPanel_0");
	    	try {
				paymentTool = new PaymentTool(p, this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
    }
    
    public void onTableChangeValue (TTableNode tNode){
    	int column = tNode.getColumn();
    	String colName = tNode.getTable().getParmMap(column);
    	
    	if("GIFTCARD_NUM".equals(colName)){
    		tableD.acceptText();
    		int num = Integer.parseInt(tNode.getValue()+""); 
    		int row = tNode.getRow();
    		double price = tableD.getItemDouble(row, "RETAIL_PRICE");
        	tableD.setItem(row, "AR_AMT", price*num);
        	SumFeeY();
        	paymentTool.setAmt(this.getValueDouble("FeeY"));
    	}
    	
    }
    
    /**
     * 当TABLE创建编辑控件时长期
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
    	
    	if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return; 
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\mem\\MEMCaseCardInfo.x"));
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }
    
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
    	TParm parm = (TParm) obj;
    	tableD.acceptText();
    	setTableDValue(parm, tableD.getSelectedRow());
    	tableD.getTable().grabFocus();
    	tableD.setSelectedColumn(5);
    }
    
    /**
     * 传值给TABLE
     * @param parm TParm
     * @param row int
     */
    public void setTableDValue(TParm parm, int row) {
    	//ID;RETAIL_PRICE;GIFTCARD_CODE;GIFTCARD_NUM;AR_AMT;DESCRIPTION;PURCHASER_NAME;OPT_USER;OPT_DATE;OPT_TERM
    	boolean flg = false;
    	if(tableD.getItemString(row, "ID").length()>0){
    		flg=true;
    	}
    	tableD.setItem(row, "RETAIL_PRICE", parm.getValue("RETAIL_PRICE"));
    	tableD.setItem(row, "GIFTCARD_CODE", parm.getValue("GIFTCARD_CODE"));
    	tableD.setItem(row, "GIFTCARD_DESC", parm.getValue("GIFTCARD_DESC"));
    	tableD.setItem(row, "FACE_VALUE", parm.getValue("FACE_VALUE"));
    	tableD.setItem(row, "AR_AMT", parm.getDouble("RETAIL_PRICE")*tableD.getItemDouble(row, "GIFTCARD_NUM"));
    	
    	if(!flg){
    		
    		tableD.setItem(row, "ID", tableD.getRowCount());
    		onNewD();
    	}
    	SumFeeY();
    }
    
    public void onQuery(){
    	updateFlg=true;
    	String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		String name = getValueString("PURCHASER_NAME1");
		String p_name="";
		if(name.length()>0){
			p_name = " AND PURCHASER_NAME = '"+name+"' " ;
		}
		String sql = "SELECT 'N' FLG, TRADE_NO, PURCHASER_NAME, CERTIFICATE_TYPE, CERTIFICATE_NO, PHONE," +
				" DESCRIPTION, OPT_USER, OPT_DATE, OPT_TERM, AR_AMT,INTRODUCER1,INTRODUCER2,INTRODUCER3" +
				" FROM MEM_GIFTCARD_TRADE_M" +
				" WHERE OPT_DATE BETWEEN TO_DATE ('"+date_s+"', 'YYYYMMDDHH24MISS') " +
				" AND  TO_DATE ('"+date_e+"', 'YYYYMMDDHH24MISS')" +
				p_name+
				"  ORDER BY TRADE_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			this.messageBox("没有要查询的数据！");
			return;
		}	
		tableM.setParmValue(parm);
//		this.callFunction("UI|DELETE|setEnabled", false);
//		this.callFunction("UI|SAVE|setEnabled", false);
    }
    
    public void onTableMClicked(Object obj){
    	tableM = (TTable) obj;
    	tableM.acceptText();
    	TParm parmM = tableM.getParmValue();
    	String tradeNos = "";
    	for(int i=0;i<parmM.getCount("FLG");i++){
    		if(parmM.getBoolean("FLG", i)){
    			tradeNos += "'" + parmM.getValue("TRADE_NO", i) + "',";
			}
    	}
    	if(tradeNos.length()>0){
    		tradeNos = tradeNos.substring(0, tradeNos.length() - 1);
		}
    	
    	TParm parmD = getTableD(tradeNos);
    	tableD.setParmValue(parmD);	
    	SumFeeY();

    }
    
    public TParm getTableD(String tradeNos){
    	String sql = "SELECT A.ID, A.GIFTCARD_CODE, A.RETAIL_PRICE, A.FACE_VALUE, A.GIFTCARD_NUM," +
    			" A.AR_AMT, A.DESCRIPTION, B.GIFTCARD_DESC" +
    			" FROM MEM_GIFTCARD_TRADE_D A, MEM_CASH_CARD_INFO B" +
    			" WHERE A.GIFTCARD_CODE = B.GIFTCARD_CODE" +
    			" AND A.TRADE_NO IN ("+tradeNos+ ")";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return 	parm;
		
    }
    
    
    public void onDelete(){
    	
		if (!this.getPopedem("LEADER")) {
			this.messageBox("非组长不能删除!");
			return;
		}
		tableM.acceptText();
		TParm parm = new TParm();
		for(int i=0;i<tableM.getRowCount();i++){
			if(tableM.getItemData(i, "FLG").equals("Y")){
				parm.addData("TRADE_NO", tableM.getItemString(i, "TRADE_NO"));
			}
		}
		if(parm.getCount("TRADE_NO") < 0){
			this.messageBox("请选择要删除的数据!");
			return;
		}
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction","deleteGiftSalesCardMD",parm);
    	if(result.getErrCode()<0){
    		this.messageBox("删除失败！");
    		return;
    	}
    	this.messageBox("删除成功！");
    	this.onClear();
    	this.onQuery();
//    	tableD.acceptText();
//    	tableD.removeRow(tableD.getSelectedRow());
//    	for(int i=0;i<tableD.getRowCount()-1;i++){
//    		tableD.setItem(i, "ID", i+1);
//    	}
    }
    
    public void onSave(){   	
    	tableD.acceptText();
    	tableM.acceptText();
    	TParm parm = null;
    	//----start-------add by kangy 20160718------微信支付宝支付需要添加交易号
    	TParm payTypeTParm=paymentTool.table.getParmValue();	
    	boolean flg2=paymentTool.onCheckPayType(payTypeTParm);
	    if (flg2) {
	    } else {
			this.messageBox("不允许出现相同的支付方式！");
			return;
		}
		//----end-----add by kangy 20160718------微信支付宝支付需要添加交易号
		try {
			parm = paymentTool.getAmts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messageBox(e.getMessage());
		}
		if(updateFlg){
    		if (!this.getPopedem("LEADER")) {
				this.messageBox("非组长不能修改!");
				return;
			}
		}
    	TParm parmAll = new TParm();
    	//现金打票操作，校验是否存在支付宝或微信金额
		TParm checkCashTypeParm=OPBTool.getInstance().checkCashTypeOther(payTypeTParm);
		TParm payCashParm=null;
		if(null!=checkCashTypeParm.getValue("WX_FLG")&&
				checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
				checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
			Object result = this.openDialog(
    	            "%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
			if(null==result){
				return ;
			}
			payCashParm=(TParm)result;
		}
		if(null!=payCashParm){
			parmAll.setData("payCashParm",payCashParm.getData());
		}
    	
    	if(updateFlg){
    		TParm parmM = new TParm();
        	TParm parmD = new TParm();
        	TParm parmMH = new TParm();
        	TParm parmDH = new TParm();
        	double sum = 0;
        	for(int i=0;i<tableD.getRowCount();i++){
        		parmD.addData("ID", tableD.getItemString(i, "ID"));
        		parmD.addData("TRADE_NO", tableM.getItemString(tableM.getSelectedRow(), "TRADE_NO"));
        		parmD.addData("GIFTCARD_CODE", tableD.getItemString(i, "GIFTCARD_CODE"));
        		parmD.addData("RETAIL_PRICE", tableD.getItemDouble(i, "RETAIL_PRICE"));
        		parmD.addData("FACE_VALUE", tableD.getItemDouble(i, "FACE_VALUE"));
        		parmD.addData("GIFTCARD_NUM", tableD.getItemDouble(i, "GIFTCARD_NUM"));
        		parmD.addData("AR_AMT", tableD.getItemDouble(i, "AR_AMT"));
        		parmD.addData("DESCRIPTION", tableD.getItemString(i, "DESCRIPTION"));
        		sum += tableD.getItemDouble(i, "AR_AMT");
        	}
        	
        	parmM.setData("TRADE_NO", tableM.getItemString(tableM.getSelectedRow(), "TRADE_NO"));
        	parmM.setData("PURCHASER_NAME", tableM.getItemString(tableM.getSelectedRow(), "PURCHASER_NAME"));
        	parmM.setData("CERTIFICATE_TYPE", tableM.getItemString(tableM.getSelectedRow(), "CERTIFICATE_TYPE"));
        	parmM.setData("CERTIFICATE_NO",tableM.getItemString(tableM.getSelectedRow(), "CERTIFICATE_NO"));
        	parmM.setData("PHONE", tableM.getItemString(tableM.getSelectedRow(), "PHONE"));
        	parmM.setData("AR_AMT", sum);
        	parmM.setData("DESCRIPTION", tableM.getItemString(tableM.getSelectedRow(), "DESCRIPTION"));
        	parmM.setData("OPT_USER", Operator.getID());
        	parmM.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
        	parmM.setData("OPT_TERM", Operator.getIP());
        	parmM.setData("INTRODUCER1", tableM.getItemString(tableM.getSelectedRow(), "INTRODUCER1"));
        	parmM.setData("INTRODUCER2", tableM.getItemString(tableM.getSelectedRow(), "INTRODUCER2"));
        	parmM.setData("INTRODUCER3", tableM.getItemString(tableM.getSelectedRow(), "INTRODUCER3"));
        	
        	TParm parmMlog = MEMTool.getInstance().selectGiftSalesCardM(parmM);
        	parmMH.setData("ACTION_DATE", TJDODBTool.getInstance().getDBTime());
			parmMH.setData("ACTION_TYPE", "1");
        	parmMH.setData("TRADE_NO", parmMlog.getValue("TRADE_NO", 0));
        	parmMH.setData("TRADE_NO", parmMlog.getValue("TRADE_NO", 0));
        	parmMH.setData("PURCHASER_NAME", parmMlog.getValue("PURCHASER_NAME", 0));
        	parmMH.setData("CERTIFICATE_TYPE", parmMlog.getValue("CERTIFICATE_TYPE", 0));
        	parmMH.setData("CERTIFICATE_NO",parmMlog.getValue("CERTIFICATE_NO", 0));
        	parmMH.setData("PHONE", parmMlog.getValue("PHONE", 0));
        	parmMH.setData("AR_AMT", parmMlog.getValue("AR_AMT", 0));
        	parmMH.setData("DESCRIPTION", parmMlog.getValue("DESCRIPTION", 0));
        	parmMH.setData("OPT_USER", parmMlog.getValue("OPT_USER", 0));
        	parmMH.setData("OPT_DATE", parmMlog.getData("OPT_DATE", 0));
        	parmMH.setData("OPT_TERM", parmMlog.getValue("OPT_TERM", 0));
        	parmMH.setData("INTRODUCER1", parmMlog.getValue("INTRODUCER1", 0));
        	parmMH.setData("INTRODUCER2", parmMlog.getValue("INTRODUCER2", 0));
        	parmMH.setData("INTRODUCER3", parmMlog.getValue("INTRODUCER3", 0));
        	parmMH.setData("PAY_TYPE01", parmMlog.getValue("PAY_TYPE01", 0));
        	parmMH.setData("PAY_TYPE02", parmMlog.getValue("PAY_TYPE02", 0));
        	parmMH.setData("PAY_TYPE03", parmMlog.getValue("PAY_TYPE03", 0));
        	parmMH.setData("PAY_TYPE04", parmMlog.getValue("PAY_TYPE04", 0));
        	parmMH.setData("PAY_TYPE05", parmMlog.getValue("PAY_TYPE05", 0));
        	parmMH.setData("PAY_TYPE06", parmMlog.getValue("PAY_TYPE06", 0));
        	parmMH.setData("PAY_TYPE07", parmMlog.getValue("PAY_TYPE07", 0));
        	parmMH.setData("PAY_TYPE08", parmMlog.getValue("PAY_TYPE08", 0));
        	parmMH.setData("PAY_TYPE09", parmMlog.getValue("PAY_TYPE09", 0));
        	parmMH.setData("PAY_TYPE10", parmMlog.getValue("PAY_TYPE10", 0));
//        	//add by lich 20150304----start查询支付方式备注项
//        	parmMH.setData("REMARK01", parmMlog.getValue("REMARK01", 0));
//        	parmMH.setData("REMARK02", parmMlog.getValue("REMARK02", 0));
//        	parmMH.setData("REMARK03", parmMlog.getValue("REMARK03", 0));
//        	parmMH.setData("REMARK04", parmMlog.getValue("REMARK04", 0));
//        	parmMH.setData("REMARK05", parmMlog.getValue("REMARK05", 0));
//        	parmMH.setData("REMARK06", parmMlog.getValue("REMARK06", 0));
//        	parmMH.setData("REMARK07", parmMlog.getValue("REMARK07", 0));
//        	parmMH.setData("REMARK08", parmMlog.getValue("REMARK08", 0));
//        	parmMH.setData("REMARK09", parmMlog.getValue("REMARK09", 0));
//        	parmMH.setData("REMARK10", parmMlog.getValue("REMARK10", 0));
//        	//add by lich 20150304----end查询支付方式备注项
        	String key = "";
			double v;
			String remarkKey;
			String remark;
			if (parm.getCount("PAY_TYPE") > 0) {
				for (int i = 1; i < 11; i++) {
					if (i < 10) {
						key = "PAY_TYPE0" + i;
						remarkKey = "REMARK0" + i;
//						parmM.setData("PAY_TYPE0" + i, "");
					} else {
						key = "PAY_TYPE" + i;
						remarkKey="REMARK" + i;
//						parmM.setData("PAY_TYPE" + i, "");
					}
					v = 0;
					remark = "";
					
					for (int j = 0; j < parm.getCount("PAY_TYPE"); j++) {
						if(key.equals(parm.getValue("PAY_TYPE", j))){
							v = parm.getDouble("AMT", j);
							
							if("PAY_TYPE02".equals(parm.getValue("PAY_TYPE", j))
									||"PAY_TYPE09".equals(parm.getValue("PAY_TYPE", j))
									||"PAY_TYPE10".equals(parm.getValue("PAY_TYPE", j))){//modify by kangy 20171019 微信支付宝添加卡类型备注交易号
								remark = parm.getValue("CARD_TYPE",j)+ "#" +parm.getValue("REMARKS",j);//刷卡收费，添加卡类型及卡号add by huangjw 20141230
							}else{
								remark = parm.getValue("REMARKS",j);
							}
//							if (null!=parm) {//现金减免===pangben 2014-8-21
//								for (int j2 = 0; j2 < parm.getCount("PAY_TYPE"); j2++) {
//									if (parm.getValue("PAY_TYPE", j)
//											.equals(parm.getValue("PAY_TYPE", j2))) {
////										v-=parm.getDouble("REDUCE_AMT", j2);
//										break;
//									}
//								}
//							}
							break;
						}
						
					}
					parmM.setData(key, v);
					parmM.setData(remarkKey,remark);
//					System.out.println("parmM = = = + + + " + parmM);
        		}
//        		for(int i = 0;i< parm.getCount("PAY_TYPE");i++){
//        			parmM.setData(parm.getValue("PAY_TYPE", i), parm.getData("AMT", i));
//        		}
        	}else{
        		parmM.setData("PAY_TYPE01", parmMlog.getValue("PAY_TYPE01", 0));
        		parmM.setData("PAY_TYPE02", parmMlog.getValue("PAY_TYPE02", 0));
        		parmM.setData("PAY_TYPE03", parmMlog.getValue("PAY_TYPE03", 0));
        		parmM.setData("PAY_TYPE04", parmMlog.getValue("PAY_TYPE04", 0));
        		parmM.setData("PAY_TYPE05", parmMlog.getValue("PAY_TYPE05", 0));
        		parmM.setData("PAY_TYPE06", parmMlog.getValue("PAY_TYPE06", 0));
        		parmM.setData("PAY_TYPE07", parmMlog.getValue("PAY_TYPE07", 0));
        		parmM.setData("PAY_TYPE08", parmMlog.getValue("PAY_TYPE08", 0));
        		parmM.setData("PAY_TYPE09", parmMlog.getValue("PAY_TYPE09", 0));
        		parmM.setData("PAY_TYPE10", parmMlog.getValue("PAY_TYPE10", 0));
        	}
        	
        	TParm parmDlog = MEMTool.getInstance().selectGiftSalesCardD(parmM);
        	for(int i=0;i<parmDlog.getCount();i++){
        		parmDH.addData("ACTION_DATE", TJDODBTool.getInstance().getDBTime());
        		parmDH.addData("ID", parmDlog.getValue("ID", i));
        		parmDH.addData("TRADE_NO", parmDlog.getValue("TRADE_NO", i));
        		parmDH.addData("GIFTCARD_CODE", parmDlog.getValue("GIFTCARD_CODE", i));
        		parmDH.addData("RETAIL_PRICE", parmDlog.getValue("RETAIL_PRICE", i));
        		parmDH.addData("FACE_VALUE", parmDlog.getValue("FACE_VALUE", i));
        		parmDH.addData("GIFTCARD_NUM", parmDlog.getValue("GIFTCARD_NUM", i));
        		parmDH.addData("AR_AMT", parmDlog.getValue("AR_AMT", i));
        		parmDH.addData("DESCRIPTION", parmDlog.getValue("DESCRIPTION", i));
        	}
        	
        	parmAll.setData("parmM", parmM.getData());
        	parmAll.setData("parmD", parmD.getData()); 
        	parmAll.setData("parmMH", parmMH.getData()); 
        	parmAll.setData("parmDH", parmDH.getData()); 
        	
        	TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction","updateGiftSalesCardMD",parmAll);
        	if(result.getErrCode()<0){
        		this.messageBox("修改失败！");
        		return;
        	}
        	this.messageBox("修改成功！");
        	tableD.removeRowAll();
        	clearValue("FeeY");
        	paymentTool.onClear();
        	this.onQuery();
    		
    	}else{
//    		if(tableD.getRowCount()<=0){
//        		this.messageBox("请录入数据！");
//        		return;
//        	}
    		tableD.acceptText();
        	String name=tableM.getItemString(tableM.getSelectedRow(), "PURCHASER_NAME");
        	if(name.equals("")){
        		this.messageBox("请填写购卡人！");
        		paymentTool.onClear();
        		return;
        	}
        	
        	if(tableD.getRowCount() == 1){
        		if("".equals(tableD.getItemString(0, "ID"))){
        			this.messageBox("请录入数据！");
        			paymentTool.onClear();
        			return;
        		}
        	}else{
        		for(int i=0;i<tableD.getRowCount()-1;i++){
        			if("".equals(tableD.getItemString(i, "GIFTCARD_NUM"))){
        				this.messageBox("请录入数量！");
        				paymentTool.onClear();
        				return;
        			}
        		}
        	}
        	
        	
//        	System.out.println(parm);

        	
        	TParm parmM = new TParm();
        	TParm parmD = new TParm();
        	
        	parmM.setData("TRADE_NO", tableM.getItemString(tableM.getSelectedRow(), "TRADE_NO"));
        	parmM.setData("PURCHASER_NAME", tableM.getItemString(tableM.getSelectedRow(), "PURCHASER_NAME"));
        	parmM.setData("CERTIFICATE_TYPE", tableM.getItemString(tableM.getSelectedRow(), "CERTIFICATE_TYPE"));
        	parmM.setData("CERTIFICATE_NO",tableM.getItemString(tableM.getSelectedRow(), "CERTIFICATE_NO"));
        	parmM.setData("PHONE", tableM.getItemString(tableM.getSelectedRow(), "PHONE"));
        	parmM.setData("AR_AMT", this.getValueDouble("FeeY"));
        	parmM.setData("DESCRIPTION", tableM.getItemString(tableM.getSelectedRow(), "DESCRIPTION"));
        	parmM.setData("OPT_USER", Operator.getID());
        	parmM.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
        	parmM.setData("OPT_TERM", Operator.getIP());
        	parmM.setData("INTRODUCER1", tableM.getItemString(tableM.getSelectedRow(), "INTRODUCER1"));
        	parmM.setData("INTRODUCER2", tableM.getItemString(tableM.getSelectedRow(), "INTRODUCER2"));
        	parmM.setData("INTRODUCER3", tableM.getItemString(tableM.getSelectedRow(), "INTRODUCER3"));
        	
//        	for(int j=1;j<11;j++){
//    			if(j<10){
//    				parmM.setData("PAY_TYPE0"+j, "");
//    			}else{
//    				parmM.setData("PAY_TYPE"+j, "");
//    			}
//    			
//    		}
//    		for(int i=0;i<parm.getCount("PAY_TYPE");i++){
//    			parmM.setData(parm.getValue("PAY_TYPE", i), parm.getData("AMT", i));
//    		}
        	String key = "";
			double v;
			String remarkKey;
			String remark;
			if (parm.getCount("PAY_TYPE") > 0) {
				for (int i = 1; i < 11; i++) {
					if (i < 10) {
						key = "PAY_TYPE0" + i;
						remarkKey = "REMARK0" + i;
//						parmM.setData("PAY_TYPE0" + i, "");
					} else {
						key = "PAY_TYPE" + i;
						remarkKey="REMARK" + i;
//						parmM.setData("PAY_TYPE" + i, "");
					}
					v = 0;
					remark = "";
					
					for (int j = 0; j < parm.getCount("PAY_TYPE"); j++) {
						if(key.equals(parm.getValue("PAY_TYPE", j))){
							v = parm.getDouble("AMT", j);
							
							if("PAY_TYPE02".equals(parm.getValue("PAY_TYPE", j))
								||"PAY_TYPE09".equals(parm.getValue("PAY_TYPE", j))
								||"PAY_TYPE10".equals(parm.getValue("PAY_TYPE", j))){// modify by kangy 20171019 微信支付宝添加卡类型备注交易号
								remark = parm.getValue("CARD_TYPE",j)+ "#" +parm.getValue("REMARKS",j);//刷卡收费，添加卡类型及卡号add by huangjw 20141230
							}else{
								remark = parm.getValue("REMARKS",j);
							}
							break;
						}
						
					}
					parmM.setData(key, v);
					parmM.setData(remarkKey,remark);
//					System.out.println("parmM = = = + + + " + parmM);
        		}
        	}
        	
        	for(int i=0;i<tableD.getRowCount()-1;i++){
        		parmD.addData("ID", tableD.getItemString(i, "ID"));
        		parmD.addData("TRADE_NO", tableM.getItemString(tableM.getSelectedRow(), "TRADE_NO"));
        		parmD.addData("GIFTCARD_CODE", tableD.getItemString(i, "GIFTCARD_CODE"));
        		parmD.addData("RETAIL_PRICE", tableD.getItemDouble(i, "RETAIL_PRICE"));
        		parmD.addData("FACE_VALUE", tableD.getItemDouble(i, "FACE_VALUE"));
        		parmD.addData("GIFTCARD_NUM", tableD.getItemDouble(i, "GIFTCARD_NUM"));
        		parmD.addData("AR_AMT", tableD.getItemDouble(i, "AR_AMT"));
        		parmD.addData("DESCRIPTION", tableD.getItemString(i, "DESCRIPTION"));
        	}

        	parmAll.setData("parmM", parmM.getData());
        	parmAll.setData("parmD", parmD.getData()); 
//        	System.out.println("all=="+parmAll);
        	TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction","insertGiftSalesCardMD",parmAll);
        	if(result.getErrCode()<0){
        		this.messageBox("添加失败！");
        		return;
        	}
        	this.messageBox("添加成功！");
        	onPrint(parmM,"");
        	onClear();
        	onQuery();
    	}
    	
    	
    	
    }
    
    public void onNew(){
    	updateFlg=false;
    	tableD.removeRowAll();
    	tableM.acceptText();
    	for(int i=0;i<tableM.getRowCount();i++){
    		if(tableM.getItemString(i, "FLG").equals("Y")){
    			tableM.setItem(i, "FLG", "N");
    		}
    	}
    	TParm parmM = new TParm();
    	String tradeNo=MEMTool.getInstance().getGIFTTradeNo();
    	parmM.setData("TRADE_NO", tradeNo);
    	parmM.setData("PURCHASER_NAME", null);
    	parmM.setData("CERTIFICATE_TYPE", "01");
    	parmM.setData("CERTIFICATE_NO",null);
    	parmM.setData("PHONE", null);
    	parmM.setData("INTRODUCER1", null);
    	parmM.setData("INTRODUCER2", null);
    	parmM.setData("INTRODUCER3", null);
    	parmM.setData("DESCRIPTION", null);
    	parmM.setData("OPT_USER", Operator.getID());
    	parmM.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
    	parmM.setData("OPT_TERM", Operator.getIP());
    	tableM.addRow(parmM);
    	tableM.setSelectedRow(tableM.getRowCount()-1);
    	onNewD();
    	this.callFunction("UI|NEW|setEnabled", false);
    	this.callFunction("UI|SAVE|setEnabled", true);
    	this.callFunction("UI|DELETE|setEnabled", true);
    }
    
    public void onNewD(){
    	tableD.acceptText();
    	TParm parm = new TParm();
    	parm.setData("ID", null);
    	parm.setData("GIFTCARD_CODE",null);
    	parm.setData("GIFTCARD_NUM", null);
    	parm.setData("RETAIL_PRICE",null);
    	parm.setData("AR_AMT", null);
    	parm.setData("DESCRIPTION", null);
    	parm.setData("FACE_VALUE", null);
    	tableD.addRow(parm);
    }
    
    public void onClear(){
    	tableD.removeRowAll();
    	tableM.removeRowAll();
    	clearValue("PURCHASER_NAME1;END_DATE;START_DATE;FeeY;FeeS;FeeZ");
    	Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 date.toString().substring(0, 10).replace('-', '/')
							+ " 00:00:00");
		 this.setValue("END_DATE", date.toString()
					.substring(0, 10).replace('-', '/')
					+ " 23:59:59");
		 
//		 this.callFunction("UI|SAVE|setEnabled", true);
		 this.callFunction("UI|NEW|setEnabled", true);
//		 this.callFunction("UI|DELETE|setEnabled", true);
		 updateFlg=false;
		 paymentTool.onClear();
		 
    }
    
    /**
     * 打印
     */
    public void onPrint(TParm parmM,String copy){
//    	DecimalFormat df = new DecimalFormat("#0.00");
//    	TParm tableParm = getTableD(parmPrint.getValue("TRADE_NO"));
//    	TParm data = new TParm();
//    	double amt=0;
//        TParm parm = new TParm();
//        for(int i=0;i<tableParm.getCount("ID");i++){
//        	parm.addData("ID", tableParm.getValue("ID", i));
//        	parm.addData("GIFTCARD_CODE", tableParm.getValue("GIFTCARD_DESC", i));
//        	parm.addData("RETAIL_PRICE", tableParm.getValue("RETAIL_PRICE", i));
//        	parm.addData("FACE_VALUE", tableParm.getValue("FACE_VALUE", i));
//        	parm.addData("GIFTCARD_NUM", tableParm.getValue("GIFTCARD_NUM", i));
//        	parm.addData("AR_AMT", df.format(tableParm.getDouble("AR_AMT", i)));
//        	parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
//        	amt += tableParm.getDouble("AR_AMT", i);
//        }
//        parm.setCount(parm.getCount("ID"));
//        parm.addData("SYSTEM", "COLUMNS", "ID");
//        parm.addData("SYSTEM", "COLUMNS", "GIFTCARD_CODE");
//        parm.addData("SYSTEM", "COLUMNS", "RETAIL_PRICE");
//        parm.addData("SYSTEM", "COLUMNS", "FACE_VALUE");
//        parm.addData("SYSTEM", "COLUMNS", "GIFTCARD_NUM");
//        parm.addData("SYSTEM", "COLUMNS", "AR_AMT");
//        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
//        data.setData("TITLE", "TEXT", Operator.getHospitalCHNShortName()+"礼品卡销售");
//        data.setData("USER_NAME", "TEXT", "购卡人："+parmPrint.getValue("PURCHASER_NAME"));
//        data.setData("AMT", "TEXT", "应收金额："+ df.format(parmPrint.getDouble("AR_AMT"))+"元" );
//        data.setData("DATE", "TEXT","购卡时间："+ parmPrint.getValue("OPT_DATE").toString().replace("-", "/").substring(0, parmPrint.getValue("OPT_DATE").toString().length()-2));
//        data.setData("OPT_USER", "TEXT", "制表人："+Operator.getName());
//        data.setData("TABLE", parm.getData());
//        this.openPrintWindow("%ROOT%\\config\\prt\\mem\\MEMCashCardPrint.jhw", data);
    	
    	String sql = "SELECT * FROM MEM_GIFTCARD_TRADE_M WHERE TRADE_NO = '"+parmM.getValue("TRADE_NO")+"'";
		TParm tradeM = new TParm(TJDODBTool.getInstance().select(sql));
    	TParm parm = new TParm();
    	parm.setData("TITLE", "TEXT", "礼品卡售卡收据"); //类别
		parm.setData("TYPE", "TEXT", "礼品卡"); //类别
		parm.setData("MR_NO", "TEXT", ""); // 病案号
		parm.setData("RecNO", "TEXT", parmM.getValue("TRADE_NO")); //票据号
		parm.setData("DEPT_CODE", "TEXT", "");// 科别
		parm.setData("PAT_NAME", "TEXT", tradeM.getValue("PURCHASER_NAME",0)); // 姓名
//		TParm table1 = paymentTool.table.getShowParmValue();//支付方式表格
		String payType="";
//		for (int i = 0; i < table1.getCount("AMT"); i++) {
//			payType = payType +table1.getValue("PAY_TYPE", i)+":"+table1.getDouble("AMT", i)+";";
//		}
		
		
		String sqlType = "SELECT GATHER_TYPE,PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE";
		TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(sqlType));
		for(int i=0;i<payTypeParm.getCount();i++){
			if(tradeM.getDouble(payTypeParm.getValue("PAYTYPE", i), 0) > 0 ){
				String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE  GROUP_ID = 'GATHER_TYPE' AND ID='"+payTypeParm.getValue("GATHER_TYPE", i)+"'";
				TParm parmT = new TParm(TJDODBTool.getInstance().select(sql1));
				payType = payType + parmT.getValue("CHN_DESC",0)+":"+StringTool.round(tradeM.getDouble(payTypeParm.getValue("PAYTYPE", i), 0),2)+"元;";
			}
		}
		if(payType.length()>0){
			payType = payType.substring(0,payType.length()-1);
		}
		
		parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
		parm.setData("MONEY", "TEXT", StringTool.round(tradeM.getDouble("AR_AMT", 0), 2)+"元"); // 金额
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(tradeM.getDouble("AR_AMT", 0))); // 大写金额
		parm.setData("ACOUNT_NO", "TEXT", "");// 账号
		
		String mapSql = "SELECT A.PAYTYPE,A.GATHER_TYPE, B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B " +
						"WHERE B.GROUP_ID='GATHER_TYPE' AND A.GATHER_TYPE=B.ID";//储存PAY_TYPE极其对应的中文
		TParm mapParm = new TParm(TJDODBTool.getInstance().select(mapSql));
		
		String ctzCode="";
		String cardNo="";
		String sqlC = "SELECT B.GIFTCARD_DESC,A.DESCRIPTION FROM MEM_GIFTCARD_TRADE_D A,MEM_CASH_CARD_INFO B" +
				" WHERE A.GIFTCARD_CODE=B.GIFTCARD_CODE" +
				" AND A.TRADE_NO='"+parmM.getValue("TRADE_NO")+"'";
		TParm parmC = new TParm(TJDODBTool.getInstance().select(sqlC));
		for (int i = 0; i < parmC.getCount(); i++) {
			ctzCode = ctzCode+parmC.getValue("GIFTCARD_DESC", i)+";";
			cardNo = cardNo+parmC.getValue("DESCRIPTION", i)+";";
			
		}
		if(ctzCode.length() > 0){
			ctzCode = ctzCode.substring(0,ctzCode.length()-1);
		}
		if(cardNo.length() > 0){
			cardNo = cardNo.substring(0,cardNo.length()-1);
		}
		//add by lich 当支付方式为刷卡的时候，收据显示卡类型和卡号
		String payDetail = "";
		//==start==modify kangy 20171019 微信支付宝添加卡类型备注交易号
		String remark02 = "";
		String remark09 = "";
		String remark10 = "";
		for (int i = 0; i < mapParm.getCount(); i++) {
			if("".equals(tradeM.getValue(mapParm.getValue("PAYTYPE", i),0)) || tradeM.getValue(mapParm.getValue("PAYTYPE", i),0) == null 
					|| tradeM.getDouble(mapParm.getValue("PAYTYPE", i),0) == 0){
				continue;
			}
			payDetail += ";"+mapParm.getValue("CHN_DESC", i) + tradeM.getValue(mapParm.getValue("PAYTYPE", i),0)+"元";
			if(mapParm.getValue("PAYTYPE", i).equals("PAY_TYPE02")){
				remark02 = tradeM.getValue("REMARK02", 0);//收费为刷卡时，收据添加卡号和卡类型
			}
			if(mapParm.getValue("PAYTYPE", i).equals("PAY_TYPE09")){
				remark09 = tradeM.getValue("REMARK09", 0);//收费为微信时，收据添加卡类型备注交易号
			}
			if(mapParm.getValue("PAYTYPE", i).equals("PAY_TYPE10")){
				remark10 = tradeM.getValue("REMARK10", 0);//收费为支付宝时，收据添加卡类型备注交易号
			}
		}
		String cardtypeString = "";
		if(!"".equals(remark02)&&!"#".equals(remark02)){
			String [] str=remark02.split("#");
			String [] str1=str[0].split(";");
//			String [] str2=str[1].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			for(int m=0;m<str1.length;m++){
				String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"+str1[m]+"' AND GROUP_ID='SYS_CARDTYPE'";
				TParm cardParm = new TParm(TJDODBTool.getInstance().select(cardsql));
//				cardtypeString = cardtypeString+cardParm.getValue("CHN_DESC",0)+" "+str2[m]+"  ";
				cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString=cardtypeString+str2[m]+" ";
					}
				}
			}
			//parm.setData("BANK_CARD","TEXT",cardtypeString);
		}
		if(!"".equals(remark09)&&!"#".equals(remark09)){
			String [] str=remark09.split("#");
			String [] str1=str[0].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			for(int m=0;m<str1.length;m++){
				String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"+str1[m]+"' AND GROUP_ID='SYS_CARDTYPE'";
				TParm cardParm = new TParm(TJDODBTool.getInstance().select(cardsql));
				cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString+="备注:"+str2[m]+" ";
					}
				}
				if(tradeM.getValue("WX_BUSINESS_NO",0).length()>0){
					cardtypeString+="交易号:"+tradeM.getValue("WX_BUSINESS_NO",0)+" ";
				}
			}
		}
		if(!"".equals(remark10)&&!"#".equals(remark10)){
			String [] str=remark10.split("#");
			String [] str1=str[0].split(";");
//			String [] str2=str[1].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			for(int m=0;m<str1.length;m++){
				String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"+str1[m]+"' AND GROUP_ID='SYS_CARDTYPE'";
				TParm cardParm = new TParm(TJDODBTool.getInstance().select(cardsql));
//				cardtypeString = cardtypeString+cardParm.getValue("CHN_DESC",0)+" "+str2[m]+"  ";
				cardtypeString=";"+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString+="备注:"+str2[m]+" ";
					}
				}
				if(tradeM.getValue("ZFB_BUSINESS_NO",0).length()>0){
					cardtypeString+="交易号:"+tradeM.getValue("ZFB_BUSINESS_NO",0)+" ";
				}
			}
		}
		if (cardtypeString.length()>1) {
			cardtypeString = cardtypeString.substring(1, cardtypeString.length());
		}
		if(cardtypeString.length()<50){
			parm.setData("BANK_CARD","TEXT",cardtypeString);
		}else{
			parm.setData("BANK_CARD2","TEXT",cardtypeString.substring(0,50));
			parm.setData("BANK_CARD3","TEXT",cardtypeString.substring(50,cardtypeString.length()));
		}
		//==end==modify kangy 20171019 微信支付宝添加卡类型备注交易号
		parm.setData("DESCRIPTION", "TEXT",cardNo );// 产品
		parm.setData("CTZ_CODE", "TEXT", ctzCode);// 产品
		parm.setData("DATE", "TEXT", tradeM.getValue("OPT_DATE",0).replace("-", "/").subSequence(0, tradeM.getValue("OPT_DATE",0).length()-2));// 日期
		parm.setData("OP_NAME", "TEXT", tradeM.getValue("OPT_USER",0)); // 收款人
		parm.setData("RETURN", "TEXT", ""); // 退
		parm.setData("o", "TEXT", "");// 退
		parm.setData("COPY", "TEXT", copy); // 补印注记
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMGiftCardV45.jhw",parm, true);
        
    }
    
    /**
     * 补印
     */
    public void onRePrint(){
    	
    	tableM.acceptText();
    	TParm parmM = tableM.getParmValue();
    	for(int i=0;i<parmM.getCount("FLG");i++){
    		if(parmM.getBoolean("FLG", i)){
    			onPrint(parmM.getRow(i),"(copy)");
    		}
    	}
    	
    }
    
    /**
     * 计算应收金额
     */
    public void SumFeeY(){
    	tableD.acceptText();
    	int count = tableD.getRowCount();
    	double sum=0;
    	for(int i=0;i<count;i++){
    		sum += tableD.getItemDouble(i, "AR_AMT");
    	}
    	this.setValue("FeeY", sum);
//    	paymentTool.setAmt(this.getValueDouble("FeeY"));
    }
    
    /**
     * 得到找零
     */
    public void getFeeZ(){
    	double FeeS = this.getValueDouble("FeeS");
    	double FeeY = this.getValueDouble("FeeY");
    	this.setValue("FeeZ", FeeS-FeeY);
    }
    
//    /**
//     * 计算总金额
//     */
//    public void getAramt(){
//    	tableD.acceptText();
//    	int row = tableD.getSelectedRow();
//    	double num = tableD.getItemDouble(row, "GIFTCARD_NUM");
//    	double price = tableD.getItemDouble(row, "RETAIL_PRICE");
//    	tableD.setItem(row, "AR_AMT", price*num);
//    }
//   

    
    

}
