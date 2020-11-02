package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title: 发卡原因
 * </p>
 * 
 * <p>
 * Description: 发卡原因
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author zhangpeng
 *
 */
public class EKTMasterQueryControl extends TControl {
	private static TTable table;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
	}
	/**
	 * 查询
	 */
	public void onQuery() {
//		String sql = " SELECT SUM (CURRENT_BALANCE) CURRENT_BALANCE"
//				+ " FROM EKT_ISSUELOG A, EKT_MASTER B"
//				+ " WHERE A.CARD_NO = B.CARD_NO AND A.WRITE_FLG = 'Y'";
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		Timestamp date = StringTool.getTimestamp(new Date());
//		TParm parm = new TParm();
//		parm.addData("C1", "截止时间");
//		parm.addData("C2", date.toString().substring(0, 19)
//				.replaceAll("-", "/"));
//		parm.addData("C1", "医疗卡总余额");
//		parm.addData("C2", StringTool.round(result.getDouble("CURRENT_BALANCE", 0), 2));
//		table.setParmValue(parm);
		
		String sql = "SELECT A.MR_NO, C.PAT_NAME, D.CHN_DESC SEX," +
				" TO_CHAR (BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, A.CURRENT_BALANCE" +
				" FROM EKT_MASTER A, EKT_ISSUELOG B, SYS_PATINFO C, SYS_DICTIONARY D" +
				" WHERE A.CARD_NO = B.CARD_NO" +
				" AND B.WRITE_FLG = 'Y'" +
				" AND A.MR_NO = C.MR_NO" +
				" AND D.GROUP_ID = 'SYS_SEX'" +
				" AND C.SEX_CODE = D.ID" +
				" AND  A.CURRENT_BALANCE <> 0" +
				" ORDER BY MR_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		
		DecimalFormat df = new DecimalFormat("##########0.00");
		double sum=0;
		for(int i=0;i<result.getCount();i++){
			result.setData("CURRENT_BALANCE", i, df.format(result.getDouble("CURRENT_BALANCE", i)));
			sum += result.getDouble("CURRENT_BALANCE", i);
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		result.addData("MR_NO", "截止时间");
		result.addData("PAT_NAME", date.toString().substring(0, 19).replaceAll("-", "/"));
		result.addData("SEX", "");
		result.addData("BIRTH_DATE", "医疗卡总余额");
		result.addData("CURRENT_BALANCE", df.format(sum));
		
		table.setParmValue(result);
		
	}
	/**
	 * 清空
	 */
	public void onClear(){
		table.removeRowAll();
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		table.acceptText();
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "医疗卡余额明细");
	}
	
	

}
