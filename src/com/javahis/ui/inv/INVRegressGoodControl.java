package com.javahis.ui.inv;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inv.INVRegressGoodTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTextFieldEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 物资退货
 * </p>
 * 
 * <p>
 * Description: 物资退货
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) BlueCore 2013
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author zhangh
 * @version 1.0
 */
public class INVRegressGoodControl extends TControl {

	TTable table;
	private boolean isHigh = true;//高值低值区分标记
	private String upSupCode = "";

	/**
	 * 初始化
	 * */
	public void onInit() {
		this.table = (TTable) this.getComponent("TABLE");
		this.setValue("ORG_CODE", Operator.getDept());
		this.setValue("OPT_USER", Operator.getID());
		this.setValue("USE_DATE", new Date());

		callFunction("UI|INV_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "getBarCode");
	}

	/**
	 * 得到条码号
	 * */
	public void getBarCode() {
		callFunction("UI|save|setEnabled", true);
		if (getValueString("INV_CODE") != null
				&& getValueString("INV_CODE").trim().length() > 0) {
			getTextField("INV_CODE").grabFocus();
		} else {
			return;
		}
		String barCode = this.getValueString("INV_CODE").toUpperCase();
		String sql1 = " SELECT A.INV_CODE,B.INV_CHN_DESC,A.RFID,B.DESCRIPTION,B.UP_SUP_CODE,B.MAN_CODE " +
					  " FROM INV_STOCKDD A " +
					  " LEFT JOIN INV_BASE B ON A.INV_CODE = B.INV_CODE " +
					  " LEFT JOIN SYS_MANUFACTURER C ON C.MAN_CODE =B.MAN_CODE " +
					  " WHERE  A.RFID='" + barCode + "'";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		int stockQty = 0;
		if(parm1.getCount("INV_CODE") <= 0){
			TParm parm = table.getShowParmValue();
			if(table.getRowCount() > 0){
				for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
					if(getValueString("INV_CODE").equals(parm.getValue("INV_CODE", i))){
						stockQty = parm.getInt("QTY", i);
						table.removeRow(i);
					}
				}
			}
			isHigh = false;
			sql1 = " SELECT A.INV_CODE,A.INV_CHN_DESC,A.DESCRIPTION,A.UP_SUP_CODE,A.MAN_CODE,'' AS QTY " +
				   " FROM INV_BASE A " +
				   " LEFT JOIN SYS_MANUFACTURER B ON B.MAN_CODE = A.MAN_CODE " +
			 	   " WHERE  A.INV_CODE='" + barCode +"'";
			parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		}else{
			isHigh = true;
			TParm parm = table.getShowParmValue();
			if(table.getRowCount() > 0){
				for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
					if(getValueString("INV_CODE").equals(parm.getValue("RFID", i))){
						table.removeRow(i);
					}
				}
			}
		}
		if (parm1.getCount("INV_CODE") <= 0) {
			messageBox("错误的编号，重新扫描");
			return;
		}
		upSupCode = parm1.getValue("UP_SUP_CODE", 0);
		TParm tableParm = table.getShowParmValue();
		for (int i = 0; i < tableParm.getCount("INV_CODE"); i++) {
			if(!tableParm.getValue("UP_SUP_CODE", i).equals(upSupCode)){
				this.messageBox("两种物资供应商不同，不允许退库！");
				break;
			}
		}
		TParm mainParm = new TParm();
		for (int i = 0; i < parm1.getCount("INV_CODE"); i++) {
			mainParm.setData("INV_CODE", parm1.getValue("INV_CODE", i));
			mainParm.setData("INV_CHN_DESC", parm1.getValue("INV_CHN_DESC", i));
			mainParm.setData("RFID", parm1.getValue("RFID", i));
			mainParm.setData("DESCRIPTION", parm1.getValue("DESCRIPTION", i));
//			mainParm.setData("BATCH_NO", parm1.getValue("BATCH_NO", i));
			if(isHigh == true)
				mainParm.setData("QTY", "1");
			else
				mainParm.setData("QTY", stockQty + 1);
			mainParm.setData("UP_SUP_CODE", parm1.getValue("UP_SUP_CODE", i));
			mainParm.setData("MAN_CODE", parm1.getValue("MAN_CODE", i));
			mainParm.setData("ORG_CODE", Operator.getDept());
		}
//		table.removeRowAll();
		table.addRow(mainParm);
		setQtyIsLock(isHigh);
		this.setValue("INV_CODE", "");
	}

	//如果是高值，则把表格中QTY列锁住，如果不是，则放开
	private void setQtyIsLock(boolean isHigh) {
		if(isHigh == true){
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setLockCell(i, 4, true);
			}
		}else{
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setLockCell(i, 4, false);
			}
		}
	}

	/**
	 * 保存
	 * */
	public void onSave() {
		
		table.acceptText();
//		TParm parm = new TParm();// 得到录入数量为负数的数据
		TParm invRegressParm = new TParm();// 物资退货参数
		TParm invStockParm = new TParm();// 扣库参数
		TParm tableParm = table.getShowParmValue();
		int count = tableParm.getCount("INV_CODE");
		if(count <= 0){
			this.messageBox("无保存数据！");
			return;
		}
		String returnNo = SystemTool.getInstance().getNo("ALL",
				"INV", "INV_REGRESSGOOD", "No");
//		HashMap<String, Double> invMap = new HashMap<String, Double>();
		for (int i = 0; i < count; i++) {
			TParm parmV = tableParm.getRow(i);
//			TParm returnTparm = INVRegressGoodTool.getInstance().selInvReturnMaxSeq();
			//根据取号原则得到退货编号
			
			invRegressParm.addData("RETURN_NO", returnNo);
			invRegressParm.addData("SEQ", i + 1);
			invRegressParm.addData("INV_CODE", parmV.getValue("INV_CODE"));
			invRegressParm.addData("RFID", parmV.getValue("RFID"));
			invRegressParm.addData("RETURN_USER", Operator.getID());
			invRegressParm.addData("RETURN_DEPT", Operator.getDept());
			invRegressParm.addData("SUP_CODE", upSupCode);
			invRegressParm.addData("REASON", "");
			invRegressParm.addData("OPT_USER", Operator.getID());
			invRegressParm.addData("OPT_TERM", Operator.getIP());
			invRegressParm.addData("QTY", parmV.getInt("QTY"));
			
			// 高值
			if (parmV.getData("INV_CODE") != null
					&& (!parmV.getData("INV_CODE").toString().equals(""))
					&& parmV.getData("INV_CODE").toString().substring(0, 2).equals("08")) {

//				// 扣库参数组合
//				if (invMap.containsKey(parmV.getData("INV_CODE").toString())) {
//					System.out.println("走IF");
//					Double double1 = invMap.get(parmV.getData("INV_CODE")
//							.toString());
//					invMap.put(parmV.getData("INV_CODE").toString(), double1
//							+ parmV.getDouble("QTY"));
//				} else {
//					System.out.println("走ELSE");
//					invMap.put(parmV.getData("INV_CODE").toString(), parmV
//							.getDouble("QTY"));
//				}
				
				invStockParm.addData("INV_CODE", parmV.getData("INV_CODE"));
				invStockParm.addData("QTY", parmV.getData("QTY"));
				invStockParm.addData("ORG_CODE", Operator.getDept());
				invStockParm.addData("RFID", parmV.getData("RFID"));
				invStockParm.addData("OPT_USER", Operator.getID());
				invStockParm.addData("OPT_TERM", Operator.getIP());
				invStockParm.addData("FLG", "HIGH");
				// 低值
			} else if (parmV.getData("RFID") == null
					|| parmV.getData("RFID").toString().equals("")) {
				// 扣库参数组合
				// INV_STOCKM
				invStockParm.addData("INV_CODE", parmV.getData("INV_CODE"));
				invStockParm.addData("QTY", parmV.getData("QTY"));
				invStockParm.addData("ORG_CODE", Operator.getDept());
				invStockParm.addData("RFID", "");
				invStockParm.addData("OPT_USER", Operator.getID());
				invStockParm.addData("OPT_TERM", Operator.getIP());
				invStockParm.addData("FLG", "LOW");
			}
		}
//		invStockParm.setData("MERGE", invMap);
		TParm Main = new TParm();
		Main.setData("INVRegressGood", invRegressParm.getData());// 退货明细表参数
		Main.setData("INVStock", invStockParm.getData());// 扣库参数
		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVRegressGoodAction", "onSave", Main);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.messageBox("保存成功！");
		this.setValue("RETURN_NO", returnNo);
		callFunction("UI|save|setEnabled", false);
//		onClear();
	}
	
	/**
	 * 清空
	 * */
	public void onClear() {
		table.clearSelection();
		table.removeRowAll();
		callFunction("UI|save|setEnabled", true);
		((TTextFormat)this.getComponent("INV_CODE")).setEnabled(true);
		this.clearValue("INV_CODE;INV_CHN_DESC");
		this.setValue("ORG_CODE", Operator.getDept());
		this.setValue("OPT_USER", Operator.getID());
		this.setValue("USE_DATE", new Date());
		
		this.addDefaultRowForTable("TABLE");
		this.setValue("INV_CODE", "");
		this.setValue("INV_CHN_DESC", "");
		table.removeRowAll();
		this.addDefaultRowForTable("TABLE");
	}

	/**
	 * 加入默认行
	 * 
	 * @param tableName
	 *            String
	 */
	private void addDefaultRowForTable(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if (table.equals(tableName)) {
			if (tableParm.getCount("RFID") > 0
					&& "".equals(tableParm.getValue("RFID",
							(tableParm.getCount("RFID") - 1)).trim())) {
				return;
			}
			// 默认加入一条空数据
			tableParm.addData("INV_CODE", "");
			tableParm.addData("INV_CHN_DESC", "");
			tableParm.addData("RFID", "");
			tableParm.addData("DESCRIPTION", "");
			tableParm.addData("QTY", "");
			tableParm.addData("SUP_CODE", "");
			tableParm.addData("ORG_CODE", "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * 得到TTextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	
	/*
     * 导出excel方法
     */
    public void onExcel(){
    	if(table.getRowCount() <= 0){
			this.messageBox("没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "物资退货");
    }
    
    /*
     * 删除方法
     */
    public void onDelete(){
    	TParm parm = table.getShowParmValue();
		if (parm.getCount("INV_CODE") <= 0) {
			this.messageBox("没有要删除的数据，请选中要删除的行！");
			return;
		}
		table.removeRow(table.getSelectedRow());
    }
    
    /*
     * 打印方法
     */
    public void onPrint(){
		if (table.getRowCount() <= 0) {
			this.messageBox("无打印数据！");
			return;
		}
		TParm tableData = table.getShowParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("RFID", tableData.getData("RFID", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("UP_SUP_CODE", tableData.getData("UP_SUP_CODE", i));
			printData.addData("MAN_CODE", tableData.getData("MAN_CODE", i));
			printData.addData("ORG_CODE", tableData.getData("ORG_CODE", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "RFID");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "UP_SUP_CODE");
		printData.addData("SYSTEM", "COLUMNS", "MAN_CODE");
		printData.addData("SYSTEM", "COLUMNS", "ORG_CODE");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "退货明细");
		printParm.setData("DATE", "TEXT", "制表时间：" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGood.jhw",
				printParm);
    }
    
    /**
     * 查询和退货号回车事件
     */
    public void onQuery(){
    	String returnNo = this.getValueString("RETURN_NO");
    	String supCode = this.getValueString("UP_SUP_CODE");
    	TParm parm = new TParm();
    	if(returnNo != null && !"".equals(returnNo.trim())){
    		parm.setData("RETURN_NO", returnNo);	
    	}
    	if(supCode != null && !"".equals(supCode.trim())){
    		parm.setData("SUP_CODE", supCode);
    	}
    	TParm result = INVRegressGoodTool.getInstance().onQuery(parm);
    	if(result.getCount("RETURN_NO") <= 0){
    		this.messageBox("查询失败！");
    		return;
    	}
    	table.setParmValue(result);
    	callFunction("UI|save|setEnabled", false);
    	((TTextFormat)this.getComponent("INV_CODE")).setEnabled(false);
    }
}
