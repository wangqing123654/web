package com.javahis.ui.adm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class ADMAcountPatInfoControl extends TControl{
	TTable table;
	@Override
	public void onInit() {
		super.onInit();
		String date = TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
		this.setValue("IN_START_DATE", date+" 00:00:00");
		this.setValue("IN_END_DATE", date+" 23:59:59");
		this.setValue("OUT_START_DATE", date+" 00:00:00");
		this.setValue("OUT_END_DATE", date+" 23:59:59");
		//add by kangy 添加结算时间   20170925
		this.setValue("BIL_START_DATE", date+" 00:00:00");
		this.setValue("BIL_END_DATE", date+" 23:59:59");
		table = (TTable) this.getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		boolean isDebug = true;
		try {
			StringBuffer sql = new StringBuffer("SELECT A.IN_DEPT_CODE," +
					" A.ADM_SOURCE,A.CTZ1_CODE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ICD_CHN_DESC," +
					" A.IN_STATION_CODE,A.BED_NO,A.IN_DATE,A.DS_DATE,A.ADM_DAYS,A.STATUS,A.BILL_DATE," +
				                                	//zhanglei 2016.9.29增加,D.REDUCE_AMT,E.TOT_AMT
					" D.LUMPWORK_AMT,D.LUMPWORK_OUT_AMT,C.PRE_AMT,D.AR_AMT ,D.REDUCE_AMT,D.TOT_AMT," +
					"D.PAY_CASH,D.PAY_BANK_CARD,D.PAY_BILPAY,D.PAY_DEBIT,D.PAY_GIFT_CARD,D.PAY_CHECK," +//add by kangy 20171205
					"D.PAY_DISCNT_CARD,D.PAY_BXZF,D.PAY_MEDICAL_CARD,D.PAY_TYPE09,D.PAY_TYPE10 " +  //add by kangy 20171205
					" FROM SYS_PATINFO B," +
					//A表
					" (SELECT A.IN_DEPT_CODE,ROW_NUMBER() OVER(ORDER BY A.MR_NO) SEQ,	" +
					" A.ADM_SOURCE,A.CTZ1_CODE,A.MR_NO,A.CASE_NO,D.ICD_CHN_DESC,A.IN_STATION_CODE," +
					" A.BED_NO,A.IN_DATE,A.DS_DATE,A.ADM_DAYS,CASE A.BILL_STATUS WHEN '0' THEN '在院' WHEN '1' THEN '出院无账单' WHEN '2' THEN '出院未缴费' " +
					" WHEN '3' THEN '费用已审核' WHEN '4' THEN '出院已结算' ELSE A.BILL_STATUS END  STATUS," +
					" A.BILL_DATE " +
					" FROM ADM_INP A,ADM_INPDIAG C,SYS_DIAGNOSIS D" +
					" WHERE A.CASE_NO = C.CASE_NO(+) AND C.ICD_CODE = D.ICD_CODE(+) " +
					" AND C.MAINDIAG_FLG(+) = 'Y' AND C.IO_TYPE(+) = 'M' "+
					"  ");
			
			//对 表A添加查询条件
			getQueryCondition(sql);//A
			
			sql.append(" GROUP BY A.IN_DEPT_CODE,A.MR_NO,A.CASE_NO,A.ADM_SOURCE,A.CTZ1_CODE," +
					" D.ICD_CHN_DESC,A.IN_STATION_CODE,A.BED_NO," +
					" A.IN_DATE,A.DS_DATE,A.ADM_DAYS,A.BILL_STATUS,A.BILL_DATE) A,");		
			// C表 以及C表添加查询条件
			sql.append(" (SELECT A.CASE_NO,SUM(B.PRE_AMT) PRE_AMT FROM ADM_INP A,BIL_PAY B WHERE  A.CASE_NO = B.CASE_NO(+) ");
			getQueryCondition(sql);//C
			
			sql.append(" GROUP BY A.CASE_NO ) C, ");
			//D表 以及D表添加查询条件                                                zhanglei   2016.9.29增加,SUM(B.REDUCE_AMT) REDUCE_AMT
			sql.append(" (SELECT A.CASE_NO,SUM(B.LUMPWORK_AMT) LUMPWORK_AMT  ,SUM(B.REDUCE_AMT) REDUCE_AMT, " +
					"  SUM(B.LUMPWORK_OUT_AMT) LUMPWORK_OUT_AMT,SUM(B.AR_AMT) AR_AMT, " +
					"  SUM(B.OWN_AMT) TOT_AMT," +//modify by kangy 20171205
					" SUM(B.PAY_CASH) PAY_CASH,SUM(B.PAY_BANK_CARD) PAY_BANK_CARD,SUM(B.PAY_BILPAY) PAY_BILPAY,SUM(B.PAY_DEBIT) PAY_DEBIT, "+// add by kangy 20171205
                    " SUM(B.PAY_GIFT_CARD) PAY_GIFT_CARD,SUM(B.PAY_CHECK) PAY_CHECK,SUM(B.PAY_DISCNT_CARD) PAY_DISCNT_CARD,SUM(B.PAY_BXZF) PAY_BXZF, " +
                    "SUM(B.PAY_MEDICAL_CARD) PAY_MEDICAL_CARD,SUM(B.PAY_TYPE09) PAY_TYPE09,SUM(B.PAY_TYPE10) PAY_TYPE10  " +
					"  FROM ADM_INP A,BIL_IBS_RECPM B WHERE  A.CASE_NO = B.CASE_NO(+) ");
			getQueryCondition(sql);//D
			sql.append(" GROUP BY A.CASE_NO ) D, ");
			//各表关联条件
			//sql.append(" WHERE A.CASE_NO = C.CASE_NO AND A.CASE_NO = D.CASE_NO AND A.MR_NO = B.MR_NO ORDER BY ");
			
			//zhanglei   2016.9.29增加E表及查询条件 
			//E表以及E表添加查询条件
			sql.append(" (SELECT A.CASE_NO,SUM(B.TOT_AMT) TOT_AMTT "+
				       " FROM ADM_INP A,IBS_ORDD B "+ 
				       " WHERE  A.CASE_NO = B.CASE_NO(+) ");
			getQueryCondition(sql);//E
			sql.append(" GROUP BY A.CASE_NO ) E ");
			//各表关联条件
			sql.append(" WHERE A.CASE_NO = C.CASE_NO AND A.CASE_NO = D.CASE_NO AND A.MR_NO = B.MR_NO      AND A.CASE_NO = E.CASE_NO             ORDER BY ");
			
				
			if(getRadioButton("tRadioButton_0").isSelected()){
				sql.append(" A.IN_DATE");
			}
			if(getRadioButton("tRadioButton_1").isSelected()){
				sql.append(" A.DS_DATE");
			}
			if(getRadioButton("tRadioButton_2").isSelected()){
				sql.append(" A.BILL_DATE");
			}
			//打印SQL语句
			//System.out.println("sql:201610141610:"+sql);		
			TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
			if(result.getCount() <= 0){
				this.messageBox("没有数据");
				table.setParmValue(new TParm());
				//table.removeRowAll();
				return;
			}
			double lumpworkAmt=0.00;
			double lumpworkOutAmt=0.00;
			double preAmt=0.00;
			double arAmt=0.00;
			double payCash=0.00;
			double payBankCard=0.00;
			double payBilPay=0.00;
			double payDebit=0.00;
			double payGiftCard=0.00;
			double payCheck=0.00;
			double payDiscntCard=0.00;
			double payBxzf=0.00;
			double payMedicalCard=0.00;
			double wx=0.00;
			double zfb=0.00;
			double totAmt = 0.00;
			double reduceAmt = 0.00;
			int j=0;
			for(int i=0; i < result.getCount(); i++){
				if(result.getData("MR_NO",i).toString().substring(result.getData("MR_NO",i).toString().length()-2, result.getData("MR_NO",i).toString().length()).equals("-1")){
					j++;
				}
				lumpworkAmt+=result.getDouble("LUMPWORK_AMT",i);
				lumpworkOutAmt+=result.getDouble("LUMPWORK_OUT_AMT",i);
				preAmt+=result.getDouble("PRE_AMT",i);
				arAmt+=result.getDouble("AR_AMT",i);
				payCash+=result.getDouble("PAY_CASH",i);
				payBankCard+=result.getDouble("PAY_BANK_CARD",i);
				payBilPay+=result.getDouble("PAY_BILPAY",i);
				payDebit+=result.getDouble("PAY_DEBIT",i);
				payGiftCard+=result.getDouble("PAY_GIFT_CARD",i);
				payCheck+=result.getDouble("PAY_CHECK",i);
				payDiscntCard+=result.getDouble("PAY_DISCNT_CARD",i);
				payBxzf+=result.getDouble("PAY_BXZF",i);
				payMedicalCard+=result.getDouble("PAY_MEDICAL_CARD",i);
				wx+=result.getDouble("PAY_TYPE09",i);
				zfb+=result.getDouble("PAY_TYPE10",i);
				totAmt += result.getDouble("TOT_AMT",i);
				reduceAmt += result.getDouble("REDUCE_AMT",i);
				result.setData("SEQ",i, i+1);
				result.setData("ADM_DAYS",i,getAdmDayNum(result.getTimestamp("DS_DATE", i),result.getTimestamp("IN_DATE", i)));
			}
			
			
			result.addData("SEQ", "");
			result.addData("IN_DEPT_CODE", "");
			result.addData("ADM_SOURCE", "合计：");
			result.addData("CTZ1_CODE", result.getCount()-j+"人");
			result.addData("MR_NO", "");
			result.addData("PAT_NAME", "");
			result.addData("SEX_CODE", "");
			result.addData("ICD_CHN_DESC", "");
			result.addData("IN_STATION__CODE", "");
			result.addData("BED_NO", "");
			result.addData("IN_DATE", "");
			result.addData("DS_DATE", "");
			result.addData("ADM_DAYS", "");
			result.addData("STATUS", "");
			result.addData("LUMPWORK_AMT",StringTool.round(lumpworkAmt, 2));
			result.addData("LUMPWORK_OUT_AMT",StringTool.round(lumpworkOutAmt, 2));
			result.addData("PRE_AMT",StringTool.round(preAmt, 2));
			result.addData("AR_AMT",StringTool.round(arAmt, 2));
			result.addData("PAY_CASH",StringTool.round(payCash, 2));
			result.addData("PAY_BANK_CARD",StringTool.round(payBankCard, 2));
			result.addData("PAY_BILPAY",StringTool.round(payBilPay, 2));
			result.addData("PAY_DEBIT",StringTool.round(payDebit, 2));
			result.addData("PAY_GIFT_CARD",StringTool.round(payGiftCard, 2));
			result.addData("PAY_CHECK",StringTool.round(payCheck, 2));
			result.addData("PAY_DISCNT_CARD",StringTool.round(payDiscntCard, 2));
			result.addData("PAY_BXZF",StringTool.round(payBxzf, 2));
			result.addData("PAY_MEDICAL_CARD",StringTool.round(payMedicalCard, 2));
			result.addData("PAY_TYPE09",StringTool.round(wx, 2));
			result.addData("PAY_TYPE10",StringTool.round(zfb, 2));
			result.addData("TOT_AMT",StringTool.round(totAmt, 2));
			result.addData("REDUCE_AMT",StringTool.round(reduceAmt, 2));
			table.setParmValue(result);
		} catch (Exception e) {
			if(isDebug){
				System.out.println("come in class: ADMAcountPatInfoControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
	} 
	
	/**
	 * 添加查询条件
	 * @param sql
	 */
	public void getQueryCondition(StringBuffer sql){
		if(getRadioButton("tRadioButton_0").isSelected()){
			sql.append(" AND A.IN_DATE BETWEEN TO_DATE('"+this.getValue("IN_START_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll("-", "").replaceAll(":", "")+"','yyyy/MM/dd HH24:mi:ss')" +
					" AND TO_DATE('"+this.getValue("IN_END_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "")+"','yyyy/MM/dd HH24:mi:ss')");
		}
		if(getRadioButton("tRadioButton_1").isSelected()){
			sql.append(" AND A.DS_DATE BETWEEN TO_DATE('"+this.getValue("OUT_START_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll("-", "").replaceAll(":", "")+"','yyyy/MM/dd HH24:mi:ss')" +
					" AND TO_DATE('"+this.getValue("OUT_END_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "")+"','yyyy/MM/dd HH24:mi:ss')");
		}
		if(getRadioButton("tRadioButton_2").isSelected()){
			sql.append(" AND A.BILL_DATE BETWEEN TO_DATE('"+this.getValue("BIL_START_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll("-", "").replaceAll(":", "")+"','yyyy/MM/dd HH24:mi:ss')" +
					" AND TO_DATE('"+this.getValue("BIL_END_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "")+"','yyyy/MM/dd HH24:mi:ss')");
		}
		
		//add by wukai on 20160803 start  取消住院的患者不显示在信息表中
		sql.append( "  AND A.CANCEL_FLG = 'N' ");
		//add by wukai on 20160803 end
		
		
		if(this.getValue("IN_DEPT_CODE") !=null && !"".equals(this.getValue("IN_DEPT_CODE")))	{
			sql.append("AND A.IN_DEPT_CODE = '"+this.getValue("IN_DEPT_CODE").toString()+"'");
		}	
	
		if(this.getValue("STATUS")!=null && this.getValue("STATUS").toString().length() > 0)	{
			sql.append("AND A.BILL_STATUS = '"+this.getValue("STATUS")+"' ");
		}
		if(this.getValue("M_NO")!=null && this.getValue("M_NO").toString().length() > 0)	{
			String mr_no="";
			if(this.getValue("M_NO").toString().substring(this.getValue("M_NO").toString().length()-2,this.getValue("M_NO").toString().length()-1).equals("-")){
				String mr=this.getValue("M_NO").toString().substring(0,this.getValue("M_NO").toString().length()-2);
				mr_no=String.format("%08d", Integer.valueOf(mr));
				mr_no=mr_no+"-"+this.getValue("M_NO").toString().substring(this.getValue("M_NO").toString().length()-1);
			}else{
				 mr_no=String.format("%08d", Integer.valueOf(this.getValue("M_NO").toString()));
			}
			
			this.setValue("M_NO", mr_no);
			sql.append("AND A.MR_NO = '"+mr_no+"' ");
		}
	}
	
	/**
	 * 计算住院天数
	 * @param ds_date
	 * @param in_date
	 * @return
	 */
	public String getAdmDayNum(Timestamp ds_date,Timestamp in_date){
		String inDayNum = "";
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp tp = ds_date;
		if (tp == null) {
			int days = 0;
			if (in_date == null) {
				inDayNum = "" ;
			} else {
				days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
						"00:00:00"), StringTool.setTime(in_date, "00:00:00"));
				inDayNum = (days == 0 ? 1 : days)+"";
			}
		} else {
			int days = 0;
			if (in_date == null) {
				inDayNum = "" ;
			} else {
				days = StringTool.getDateDiffer(StringTool.setTime(ds_date, "00:00:00"),
						StringTool.setTime(in_date,
								"00:00:00"));
				inDayNum = (days == 0 ? 1 : days)+"";
			}
		}
		return inDayNum;
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("IN_DEPT_CODE;STATUS;M_NO");
		table.setParmValue(new TParm());
		//table.removeRowAll();
		String date = TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
		this.setValue("IN_START_DATE", date+" 00:00:00");
		this.setValue("IN_END_DATE", date+" 23:59:59");
		this.setValue("OUT_START_DATE", date+" 00:00:00");
		this.setValue("OUT_END_DATE", date+" 23:59:59");
		this.setValue("BIL_START_DATE", date+" 00:00:00");
		this.setValue("BIL_END_DATE", date+" 23:59:59");
	}
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TParm tableShowParm = table.getShowParmValue();
		TParm  data = new TParm();
		if(tableShowParm.getCount("SEQ") <= 0){
			this.messageBox("没有数据");
			return;
		}
		tableShowParm.addData("SYSTEM", "COLUMNS", "SEQ");
		tableShowParm.addData("SYSTEM", "COLUMNS", "IN_DEPT_CODE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "ADM_SOURCE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "CTZ1_CODE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		tableShowParm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
		tableShowParm.addData("SYSTEM", "COLUMNS", "IN_STATION_CODE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "BED_NO");
		tableShowParm.addData("SYSTEM", "COLUMNS", "IN_DATE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "DS_DATE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "ADM_DAYS");
		tableShowParm.addData("SYSTEM", "COLUMNS", "STATUS");
		tableShowParm.addData("SYSTEM", "COLUMNS", "LUMPWORK_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PRE_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "LUMPWORK_OUT_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_CASH");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_BANK_CARD");
		//tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_BILPAY");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_DEBIT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_GIFT_CARD");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_CHECK");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_DISCNT_CARD");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_BXZF");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_MEDICAL_CARD");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_TYPE09");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_TYPE10");
		data.setData("TABLE",tableShowParm.getData());
		data.setData("PRINT_DATE", "TEXT","打印日期："+TJDODBTool.getInstance().getDBTime().toString().substring(0,19).replaceAll("-", "/"));
		data.setData("PRINT_USER", "TEXT","打印人员："+getUserName(Operator.getID()));
		
		if(getRadioButton("tRadioButton_0").isSelected()){
			data.setData("DATE", "TEXT","入院日期："+this.getValue("IN_START_DATE").toString().substring(0,19).replaceAll("-", "/")+" ~ "
					+this.getValue("IN_END_DATE").toString().substring(0,19).replaceAll("-", "/"));
		}
		if(getRadioButton("tRadioButton_1").isSelected()){
			data.setData("DATE", "TEXT","出院日期："+this.getValue("OUT_START_DATE").toString().substring(0,19).replaceAll("-", "/")+" ~ "
					+this.getValue("OUT_END_DATE").toString().substring(0,19).replaceAll("-", "/"));
		}
		if(getRadioButton("tRadioButton_2").isSelected()){
			data.setData("DATE", "TEXT","结算日期："+this.getValue("BIL_START_DATE").toString().substring(0,19).replaceAll("-", "/")+" ~ "
					+this.getValue("BIL_END_DATE").toString().substring(0,19).replaceAll("-", "/"));
		}
		this
		.openPrintWindow(
				"%ROOT%\\config\\prt\\ADM\\ADMAcountPatInfoPrint.jhw",
				data);
	}
	
	/**
	 * 汇出
	 */
	public void onExcel(){
		if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "住院信息表");
	}
	
	/**
	 * 入院时间和出院时间切换
	 */
	public void changeAction(){//modify by kangy 20170925
		String date = TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
		if(getRadioButton("tRadioButton_0").isSelected()){
			this.setValue("IN_START_DATE", date+" 00:00:00");
			this.setValue("IN_END_DATE", date+" 23:59:59");
			getTextFormat("IN_START_DATE").setEnabled(true);
			getTextFormat("IN_END_DATE").setEnabled(true);
			getTextFormat("OUT_START_DATE").setEnabled(false);
			getTextFormat("OUT_END_DATE").setEnabled(false);
			getTextFormat("BIL_START_DATE").setEnabled(false);
			getTextFormat("BIL_END_DATE").setEnabled(false);
		}
		if(getRadioButton("tRadioButton_1").isSelected()){
			this.setValue("OUT_START_DATE", date+" 00:00:00");
			this.setValue("OUT_END_DATE", date+" 23:59:59");
			getTextFormat("IN_START_DATE").setEnabled(false);
			getTextFormat("IN_END_DATE").setEnabled(false);
			getTextFormat("OUT_START_DATE").setEnabled(true);
			getTextFormat("OUT_END_DATE").setEnabled(true);
			getTextFormat("BIL_START_DATE").setEnabled(false);
			getTextFormat("BIL_END_DATE").setEnabled(false);
		}
		if(getRadioButton("tRadioButton_2").isSelected()){
			this.setValue("BIL_START_DATE", date+" 00:00:00");
			this.setValue("BIL_END_DATE", date+" 23:59:59");
			getTextFormat("IN_START_DATE").setEnabled(false);
			getTextFormat("IN_END_DATE").setEnabled(false);
			getTextFormat("OUT_START_DATE").setEnabled(false);
			getTextFormat("OUT_END_DATE").setEnabled(false);
			getTextFormat("BIL_START_DATE").setEnabled(true);
			getTextFormat("BIL_END_DATE").setEnabled(true);
		}
	}
	
	/**
	 * 获取名称
	 * @param userId
	 * @return
	 */
	public String getUserName(String userId){
		TParm parm =new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+userId+"' "));
		return parm.getValue("USER_NAME",0);
	}
	
	/**
	 * 
	 * @param tagName
	 * @return
	 */
	public TRadioButton getRadioButton(String tagName){
		return (TRadioButton) this.getComponent(tagName);
	}
	
	
	/**
	 * 
	 * @param tagName
	 * @return
	 */
	public TTextFormat getTextFormat(String tagName){
		return (TTextFormat) this.getComponent(tagName);
	}
	
	
}
