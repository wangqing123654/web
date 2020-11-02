package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title:套餐余额结转日结查询 </p>
 *
 * <p>Description:套餐余额结转  </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author huangtt 2017.11.08
 * @version 4.5
 */
public class MEMPackageBalaTranDetailControl extends TControl{
	private TTable table;
	
	/**
     * 初始化
     */
    public void onInit() { // 初始化程序
        super.onInit();
        
        initData();
        
        table = (TTable) this.getComponent("TABLE"); 
        
    }
    /**
     * 初始化数据   
     */
    private void initData(){
    	// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().replaceAll("-", "/")
				.substring(0, 10)
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().replaceAll("-", "/").substring(
				0, 10)+ " 23:59:59");
    }
    
    public void onQuery(){
    	
		String startDate = this.getValueString("START_DATE");
    	if(startDate.length()>0){
    		startDate = startDate.toString().replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
    	}
    	String endDate = this.getValueString("END_DATE");
    	if(endDate.length()>0){
    		endDate = endDate.toString().replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
    	}
		
		String sql="SELECT A.MR_NO, C.PAT_NAME," +
				" TO_CHAR (C.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE," +
				" TO_CHAR (B.BILL_DATE, 'YYYY-MM-DD') BILL_DATE," +
				" A.PACKAGE_DESC, A.TOT_AMT," +
				" TO_CHAR (A.ACCOUNT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCOUNT_DATE," +
				" D.USER_NAME ACCOUNT_USER" +
				" FROM BIL_MEM_RECP A, MEM_PACKAGE_TRADE_M B," +
				" SYS_PATINFO C,SYS_OPERATOR D" +
				" WHERE A.TRADE_NO = B.TRADE_NO" +
				" AND A.MR_NO = C.MR_NO" +
				" AND A.ACCOUNT_USER = D.USER_ID" +
				" AND A.ACCOUNT_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')";
		
		String mrNo = this.getValue("MR_NO").toString();
    	if(mrNo.length() > 0){
    		sql += " AND A.MR_NO='"+mrNo+"'";
    	}
    	
    	sql +=	" ORDER BY  A.ACCOUNT_DATE,A.MR_NO, B.BILL_DATE,A.PACKAGE_CODE";
    	
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(parm.getCount() < 0){
    		this.messageBox("没有要查询的数据");
    		table.removeRowAll();
    		return;
    	}
    	table.setParmValue(parm);
		
		
    }
    
    public void onMrNo(){
    	String mrNo = this.getValue("MR_NO").toString();
    	if(mrNo.length() > 0){
    		mrNo = PatTool.getInstance().checkMrno(mrNo);
    		this.setValue("MR_NO", mrNo);
    	}
    	onQuery();
    	
    }
    
    public void onClear(){
    	initData();
    	this.clearValue("MR_NO;ALL");
    	table.removeRowAll();
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport() {
		
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "门诊套餐余额结转明细表");
	}

}
