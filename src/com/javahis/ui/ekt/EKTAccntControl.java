package com.javahis.ui.ekt;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 医疗卡结转</p>
 *
 * <p>Description: 医疗卡结转 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20111226 
 * @version 1.0
 */
public class EKTAccntControl extends TControl {
	
	private static final String SQL = 
		" SELECT ROWNUM, EKT_DATE, IN_AMT, OUT_AMT, REG_AMT, OPB_AMT, EKT_TRADE_AMT, EKT_MASTER, EKT_MASTER_LAST" +
		" FROM (SELECT EKT_DATE, IN_AMT, OUT_AMT, REG_AMT, OPB_AMT, EKT_TRADE_AMT, EKT_MASTER, EKT_MASTER_LAST" +
		" FROM EKT_DEBUG" +
		" ORDER BY EKT_DATE DESC)" +
		" WHERE ROWNUM <= 1";
	private TParm ektDebug;//查询结果
	double EKT_TRADE_AMT = 0;
	
	public EKTAccntControl(){
		
	}
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	System.out.println(SQL);
    	ektDebug = new TParm(TJDODBTool.getInstance().select(SQL));
    	if(ektDebug.getErrCode()<0){
    		messageBox("初始化失败");
    		return;
    	}
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = ektDebug.getValue("EKT_DATE", 0);
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 19);
    	setValue("START_DATE", startDate);
    	setValue("DATE_S", startDate);
    	setValue("DATE_E", today);
    	setValue("EKT_MASTER_LAST", ektDebug.getDouble("EKT_MASTER", 0));
    	this.callFunction("UI|save|setEnabled", false);
    }
    
    /**
     * 查询方法
     */
	public void onQuery(){
		String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("DATE_S")) &&
	            !"".equals(this.getValueString("DATE_E"))) {
			startDate = getValueString("DATE_S").substring(0, 19);
			endDate = getValueString("DATE_E").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
		}
		String sql =
			" SELECT EKT_DATE, IN_AMT, OUT_AMT, REG_AMT, OPB_AMT, EKT_TRADE_AMT, EKT_MASTER, EKT_MASTER_LAST" +
			" FROM EKT_DEBUG" +
			" WHERE EKT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
			" ORDER BY EKT_DATE DESC";
		
		TParm result =new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<=0)
            this.messageBox("没有要查询的数据");
        this.callFunction("UI|TABLE|setParmValue", result);
	}
	
	public void onQueryTo(){
    	Timestamp today = SystemTool.getInstance().getDate();
    	String endDate = today.toString();
    	endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 19);
    	setValue("END_DATE", endDate);
    	String startDate = ektDebug.getValue("EKT_DATE", 0);
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
        TParm parm = new TParm();
        TParm result = new TParm();
		result = onEktMaster();
		double EKT_MASTER = result.getDouble("EKT_MASTER",0);
		result = onInAmt(startDate, endDate);
		double IN_AMT = result.getDouble("IN_AMT",0);
		result = onOutAmt(startDate, endDate);
		double OUT_AMT = result.getDouble("OUT_AMT",0);
		result = onRegAmt(startDate, endDate);
		double REG_AMT = result.getDouble("REG_AMT",0);
		result = onOpbAmt(startDate, endDate);
		double OPB_AMT = result.getDouble("OPB_AMT",0);
		result = onEkt_trade(startDate, endDate);
		EKT_TRADE_AMT = result.getDouble("EKT_TRADE_AMT",0);
		parm.setData("EKT_MASTER", EKT_MASTER);
		parm.setData("IN_AMT", IN_AMT);
		parm.setData("OUT_AMT", OUT_AMT);
		parm.setData("REG_AMT", REG_AMT);
		parm.setData("OPB_AMT", OPB_AMT);
		parm.setData("EKT_TRADE_AMT", EKT_TRADE_AMT);
		setValueForParm("EKT_MASTER;IN_AMT;OUT_AMT;REG_AMT;OPB_AMT", parm);
		this.callFunction("UI|save|setEnabled", true);
	}
	
	public void onSave(){
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
		endDate.substring(8, 10) + endDate.substring(11, 13) +
		endDate.substring(14, 16) + endDate.substring(17, 19);
		String sql = 
			" Insert into JAVAHIS.EKT_DEBUG" +
			" (EKT_DATE, IN_AMT, OUT_AMT, REG_AMT, OPB_AMT, EKT_TRADE_AMT, EKT_MASTER, EKT_MASTER_LAST)" +
			" Values" +
			" (TO_DATE('" + endDate + "', 'YYYYMMDDHH24MISS'), " + getDb("IN_AMT") +", " + getDb("OUT_AMT") +", " + getDb("REG_AMT") +", " + getDb("OPB_AMT") +", " + EKT_TRADE_AMT +", " + getDb("EKT_MASTER") +", "+ getDb("EKT_MASTER_LAST") + ")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			messageBox("失败");
			return;
		}
		messageBox("成功");
		onClear();
	}
	
	private double getDb(String tag){
		return getValueDouble(tag);
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		clearValue("EKT_MASTER;IN_AMT;OUT_AMT;REG_AMT;OPB_AMT;END_DATE;EKT_MASTER_LAST");
		ektDebug = new TParm(TJDODBTool.getInstance().select(SQL));
    	if(ektDebug.getErrCode()<0){
    		messageBox("初始化失败");
    		return;
    	}
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = ektDebug.getValue("EKT_DATE", 0);
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 19);
    	setValue("START_DATE", startDate);
    	setValue("DATE_S", startDate);
    	setValue("DATE_E", today);
    	setValue("EKT_MASTER_LAST", ektDebug.getDouble("EKT_MASTER", 0));
    	this.callFunction("UI|save|setEnabled", false);
    	TTable table = (TTable) getComponent("TABLE");
    	table.removeRowAll();
	}
	
	public TParm onInAmt(String startDate, String endDate){
		String sql = 
			" SELECT SUM (business_amt) IN_AMT" +
			" FROM ekt_accntdetail a" +
			" WHERE a.business_date BETWEEN TO_DATE ('" + startDate +"', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + endDate +"', 'YYYYMMDDHH24MISS')" +
			" AND a.charge_flg IN (3, 4, 5)";
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm onOutAmt(String startDate, String endDate){
		String sql = 
			" SELECT SUM (business_amt) OUT_AMT" +
			" FROM ekt_accntdetail a" +
			" WHERE a.business_date BETWEEN TO_DATE ('" + startDate +"', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + endDate +"', 'YYYYMMDDHH24MISS')" +
			" AND a.charge_flg = 7";
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm onRegAmt(String startDate, String endDate){
		//REG_AMT
		String sql = 
			" SELECT SUM (pay_medical_card) REG_AMT" +
			" FROM bil_reg_recp a" +
			" WHERE a.bill_date BETWEEN TO_DATE ('" + startDate +"', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + endDate +"', 'YYYYMMDDHH24MISS')";
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm onOpbAmt(String startDate, String endDate){
		String sql = 
			" SELECT SUM (pay_medical_card) OPB_AMT" +
			" FROM bil_opb_recp  a" +
			" WHERE a.bill_date BETWEEN TO_DATE ('" + startDate +"', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + endDate +"', 'YYYYMMDDHH24MISS')" +
			" AND ADM_TYPE IN ('O','E')";
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm onEkt_trade(String startDate, String endDate){
		String sql = 
			" SELECT SUM (AMT) EKT_TRADE_AMT" +
			" FROM ekt_trade" +
			" WHERE OPT_DATE BETWEEN TO_DATE ('" + startDate +"', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + endDate +"', 'YYYYMMDDHH24MISS')" +
			" AND state = 1";
		TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm onEktMaster(){
		String sql = " SELECT SUM (CURRENT_BALANCE) EKT_MASTER"
			+ " FROM EKT_ISSUELOG A, EKT_MASTER B"
			+ " WHERE A.CARD_NO = B.CARD_NO AND A.WRITE_FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TTable table = (TTable) getComponent("TABLE");
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "医疗卡结转");
	}
}
