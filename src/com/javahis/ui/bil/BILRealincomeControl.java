package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;


import sun.text.resources.FormatData;

import jdo.bil.BILComparator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 住院实收入明细报表</p>
 *
 * <p>Description: 住院实收入明细报表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author pany
 * @version 1.0 
 */
public class BILRealincomeControl extends TControl{

	private TTable table;//获取单档表单
	private SimpleDateFormat formateDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:SS");//格式化日期
	private TParm parm;

	
	/**
	 * 初始化方法
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		initPage();
		
	}
	
	/**
	 * 初始化界面
	 */
	public void initPage(){
		
//		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
//		
//		this.setValue("S_DATE", formateDate.format(yesterday));
//		this.setValue("E_DATE", formateDate.format(SystemTool.getInstance().getDate()));
		/*this.setValue("IN_DATE", formateDate.format(SystemTool.getInstance().getDate()));
		this.setValue("DS_DATE", formateDate.format(SystemTool.getInstance().getDate()));
		*/
		
		//2016.9.28  zl 修改结算时间在院时间默认调整开始时间为当月1日00:00:00
		//结束时间默认为查询当日23:59:59
		// 初始化时间
        Timestamp date = SystemTool.getInstance().getDate();
        
        // 初始化查询区间
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("S_DATE",
                date.toString().substring(0, 8).replace('-', '/') +
                "01 00:00:00");
		
		
		this.setValue("DEPT_CHN_DESC", "");
		this.setValue("DEPT_CNH_DESC", "");
		
		
		String sql = getSql().replaceFirst("@", "");
	
	}
	
	/**
	 * sql语言
	 * @return
	 */
	public String getSql(){
		String sql = 
			"SELECT K.MR_NO ,A.CASE_NO ,K.PAT_NAME ,CASE  WHEN K.SEX_CODE = '1' THEN '男' WHEN K.SEX_CODE = '2' THEN  '女' ELSE   '未知' END SEX_CODE ,  "+
			      " K.BIRTH_DATE,Q.CTZ_DESC,ms.customer_source,ms.chn_desc ,p.post_description ,K.CURRENT_ADDRESS ,  C.DEPT_CHN_DESC DEPT_CHN_DESC ,B.DEPT_CHN_DESC AS DEPT_CHN_DESC4 ,F.USER_NAME USER_NAME ,  "+
			       "TO_CHAR(G.CHARGE_DATE, 'YYYY/MM/DD HH:mm:SS')  CHARGE_DATE , "+
			       "TO_CHAR(G.CHARGE_DATE, 'YYYY/MM/DD')  CHARGE_MONTH , "+
			       "A.IN_DATE , "+
			       "A.DS_DATE , "+
			       "E.ICD_CODE || E.ICD_CHN_DESC  ICD , "+
			       "L.PACKAGE_DESC , "+
			       "G.ACCOUNT_SEQ , "+  
			       "G.AR_AMT , "+
			       "G.AR_AMT - CASE "+
			        " WHEN G.REDUCE_AMT IS NULL THEN 0 "+
			        " ELSE "+
			        "  G.REDUCE_AMT  END  REAL_AMT , "+
			       "G.REDUCE_AMT , "+
			       "G.LUMPWORK_AMT , "+
			       "G.LUMPWORK_OUT_AMT, "+
			       "H.*, DM.AMT,"+
			       "G.PAY_CASH ,  "+
			       "G.PAY_MEDICAL_CARD , "+
			       "G.PAY_BANK_CARD  PAY_CODE, "+
			       "G.PAY_CHECK, "+
			       "PAY_DEBIT , "+
			       "PAY_BILPAY,  "+
			       " A.LUMPWORK_CODE "+
			  "FROM  SYS_CTZ Q," +
			  "ADM_INP A,  "+
			      " SYS_DEPT B,  "+
			      " SYS_DEPT C,  "+
			      " BIL_IBS_RECPM G, " +
			      "(SELECT D.CASE_NO, SUM(D.TOT_AMT) AMT  FROM IBS_ORDD D" +
			      " WHERE D.ORDER_CHN_DESC LIKE '%点名%' GROUP BY D.CASE_NO) DM,  "+
			       "(SELECT PACKAGE_CODE, PACKAGE_DESC  FROM MEM_PACKAGE   WHERE ADM_TYPE = 'I') L, "+
			       "(SELECT * FROM (SELECT A.REXP_CODE, A.WRT_OFF_AMT, A.RECEIPT_NO,B.PAY_TYPE09,B.PAY_TYPE10 "+
			                  "FROM BIL_IBS_RECPD A,BIL_IBS_RECPM B "+
			                " WHERE A.RECEIPT_NO=B.RECEIPT_NO AND " +
			                " A.RECEIPT_NO IN (SELECT RECEIPT_NO FROM BIL_IBS_RECPD)) PIVOT(SUM(WRT_OFF_AMT) FOR REXP_CODE IN(" +
			                "'201' CHARGE01,  "+
			               "  '202' CHARGE02,'215.1' CHARGE03, '215.2' CHARGE04,'216' CHARGE05,'217' CHARGE06,'207' CHARGE07, '209' CHARGE08, '203' CHARGE09, '212' CHARGE10, '208' CHARGE11," +
			               " '211' CHARGE12, '210' CHARGE13,'219' CHARGE14,'213' CHARGE15,'205' CHARGE16,  "+
			              "  '204' CHARGE17,'206' CHARGE18, '218' CHARGE19,'214' CHARGE20, '220' CHARGE21))) H, "+
			       "SYS_PATINFO K, "+
			       "SYS_OPERATOR F, "+
			      " sys_postcode p, "+
			      " (SELECT CASE_NO, A.ICD_CODE, B.ICD_CHN_DESC "+
			        "  FROM MRO_RECORD_DIAG A, SYS_DIAGNOSIS B "+
			       "  WHERE A.ICD_CODE = B.ICD_CODE "+  
			        "   AND IO_TYPE = 'O' "+
			         "  AND MAIN_FLG = 'Y') E, "+
			       "(select m.mr_no,t.chn_desc,T.ID,M.CUSTOMER_SOURCE from mem_patinfo M, sys_dictionary t where M.SOURCE = T.ID(+) AND t.group_id = 'MEM_SOURCE' ) MS "+
			    "  WHERE Q.CTZ_CODE=A.CTZ1_CODE AND  A.CASE_NO = G.CASE_NO  and a.mr_no = ms.mr_no(+) AND A.DEPT_CODE = B.DEPT_CODE # "+
			  " AND A.IN_DEPT_CODE = C.DEPT_CODE  AND A.CASE_NO = E.CASE_NO(+)  AND G.RECEIPT_NO = H.RECEIPT_NO   AND A.VS_DR_CODE = F.USER_ID(+) "+
			  " AND A.MR_NO = K.MR_NO " +
			  "AND A.CASE_NO=DM.CASE_NO(+)" +
			  " and k.post_code = p.post_code(+) @";
		
		return sql;
	}
	/*
	 * 病案号补零
	 */
//	public void onMrno(){
//
//		Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
//		if (pat == null) {
//			this.messageBox("无此病案号!");
//			return;
//		}
//		pat.getMrNo();
////		setValue("MR_NO",this.getValueString("MR_NO"));
//	}
//	
//	
	public void onQueryNO() {
		

	Pat	pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
			
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		}
	/**
	 * 合计方法
	 * @param parm
	 */
	public void getData(TParm parm){

		double arMt = 0.00;
		double realAmt = 0.00;
		double reduceAmt = 0.00;
		double lumpworkAmt = 0.00;
		double lumpworkOutAmt = 0.00;
		double wxAmt = 0.00;
		double zfbAmt = 0.00;
		double chargeArray[]=new double [21];
		String [] chargeString=new String [21];
		for (int i = 1; i <= chargeString.length; i++) {
			if (i<=9) {
				chargeString[i-1]="CHARGE0"+i;
			}else{
				chargeString[i-1]="CHARGE"+i;
			}
			
		}
		double payCash = 0.00;
		double payMedicalCard = 0.00;
		double payBankCard = 0.00;
		double payCheck = 0.00;
		double payDebit = 0.00;
		double payBilpay = 0.00;
		double amt=0.00;
		
		DecimalFormat df = new DecimalFormat("#########0.00");
		
		for(int i = 0; i < parm.getCount("MR_NO");i++){
			arMt += parm.getDouble("AR_AMT",i);
			realAmt += parm.getDouble("REAL_AMT",i);
			reduceAmt += parm.getDouble("REDUCE_AMT",i);
			lumpworkAmt += parm.getDouble("LUMPWORK_AMT",i);
			lumpworkOutAmt += parm.getDouble("LUMPWORK_OUT_AMT",i);
			wxAmt += parm.getDouble("PAY_TYPE09",i);
			zfbAmt += parm.getDouble("PAY_TYPE10",i);
			for (int j = 0; j < chargeString.length; j++) {
				chargeArray[j]+= parm.getDouble(chargeString[j],i);
			}
			amt+=parm.getDouble("AMT",i);
			payCash += parm.getDouble("PAY_CASH",i);
			payMedicalCard += parm.getDouble("PAY_MEDICAL_CARD",i);
			payBankCard += parm.getDouble("PAY_CODE",i);
			payCheck += parm.getDouble("PAY_CHECK",i);
			payDebit += parm.getDouble("PAY_DEBIT",i);
			payBilpay += parm.getDouble("PAY_BILPAY",i);
			
		}
		
		
		int count = parm.getCount("MR_NO")+1;
		parm.setData("MR_NO",count,"");
		parm.setData("CASE_NO",count,"");
		parm.setData("PAT_NAME",count,"");
		parm.setData("SEX_CODE",count,"");
		parm.setData("BIRTH_DATE",count,"");
		parm.setData("CTZ_DESC",count,"");
		parm.setData("CHN_DESC",count,"");
		parm.setData("CURRENT_ADDRESS",count,"");
		parm.setData("POST_DESCRIPTION",count,"");
		parm.setData("DEPT_CHN_DESC",count,"");
		parm.setData("DEPT_CNH_DESC",count,"");
		parm.setData("USER_NAME",count,"");
		
		parm.setData("CHARGE_DATE",count,"");
		parm.setData("CHARGE_MONTH",count,"");
		parm.setData("IN_DATE",count,"");
		parm.setData("DS_DATE",count,"");
		parm.setData("ICD",count,"");
		parm.setData("PACKAGE_DESC",count,"");
		parm.setData("ACCOUNT_SEQ",count,"合计:");
		parm.setData("AR_AMT", count, df.format(arMt));
		parm.setData("REAL_AMT",count,df.format(realAmt));
		parm.setData("REDUCE_AMT",count,df.format(reduceAmt));
		parm.setData("LUMPWORK_AMT",count,df.format(lumpworkAmt));
		parm.setData("PAY_TYPE09",count,df.format(wxAmt));
		parm.setData("PAY_TYPE10",count,df.format(zfbAmt));
		
		parm.setData("LUMPWORK_OUT_AMT",count,df.format(lumpworkOutAmt));
		parm.setData("RECEIPT_NO",count,"");
		
		for (int j = 0; j < chargeString.length; j++) {
			parm.setData(chargeString[j],count,df.format(chargeArray[j]));
		}
		
		parm.setData("AMT",count,df.format(amt));
		parm.setData("PAY_CASH",count,df.format(payCash));
		parm.setData("PAY_MEDICAL_CARD",count,df.format(payMedicalCard));
		parm.setData("PAY_CODE",count,df.format(payBankCard));
		parm.setData("PAY_CHECK",count,df.format(payCheck));
		parm.setData("PAY_DEBIT",count,df.format(payDebit));
		parm.setData("PAY_BILPAY",count,df.format(payBilpay));
	
		
		
		parm.setCount(count);
		
		table.setParmValue(parm);
	}
	
	
	/**
	 * 查询方法
	 */
	public void onQuery(){
		/**
		 * 根据查询条件进行查询
		 */
		//初始化对象
		//TParm parm = new TParm();
		
		//String sql = getSql().replaceFirst("@", "");
		
		
		 
		String sql = " ";
		String sDate = "";
		String eDate = "";
		String inDate = "";
		String dsDate = "";
			
		
		sql = getSql();
		
		//结算时间
		if(this.getValue("S_DATE")!=null && !this.getValue("S_DATE").equals("")){
			sDate = this.getValue("S_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "").replace(" ", "");
			eDate = this.getValue("E_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "").replace(" ", "");
			//sql = sql.replaceFirst("@","");
			sql = sql.replace("@", " AND G.ACCOUNT_DATE BETWEEN TO_DATE('"+sDate+"', 'YYYYMMDDHH24MISS') AND" +
					" TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS') ");
			
		}else {
			sql = sql.replaceFirst("@","");
		}
		
		//入院时间
		if(this.getValue("IN_DATE")!=null && !this.getValue("IN_DATE").toString().equals("")){
			inDate = this.getValue("IN_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "");
			dsDate = this.getValue("DS_DATE").toString().substring(0,19).replaceAll("-", "").replaceAll(":", "");

			sql += "AND A.IN_DATE BETWEEN TO_DATE('"+inDate+"', 'YYYYMMDDHH24MISS') AND " +
			"TO_DATE('"+dsDate+"', 'YYYYMMDDHH24MISS') ";
			System.out.println("sql"+sql);
			
		}
	
	    //出院时间
		if(this.getValue("DEPT_CHN_DESC")!=null && !this.getValue("DEPT_CHN_DESC").equals("")){
			
			sql += "AND C.DEPT_CODE = '" +this.getValue("DEPT_CHN_DESC")+"' ";
			
		}
		
		//入院科室与出院科室相同
		if(this.getValue("DEPT_CNH_DESC")!=null && !this.getValue("DEPT_CNH_DESC").equals("")){
			sql += "AND B.DEPT_CODE = '" +this.getValue("DEPT_CNH_DESC")+"' ";
		}
		
		//病案号
		if(this.getValue("MR_NO")!=null && !this.getValue("MR_NO").equals("")){
			sql += "AND K.MR_NO = '" +this.getValue("MR_NO")+"' ";
		}
		// RECEIPT_NO;收据号
		if(this.getValue("RECEIPT_NO")!=null && !this.getValue("RECEIPT_NO").equals("")){
			sql += "AND H.RECEIPT_NO = '" +this.getValue("RECEIPT_NO")+"' ";
		}
		// ACCOUNT_SEQ日结号
		if(this.getValue("ACCOUNT_SEQ")!=null && !this.getValue("ACCOUNT_SEQ").equals("")){
			sql += "AND G.ACCOUNT_SEQ = '" +this.getValue("ACCOUNT_SEQ")+"' ";
		}
		//PACKAGE_DESC;套餐名称
		if(this.getValue("PACKAGE_DESC")!=null && !this.getValue("PACKAGE_DESC").equals("")){
			sql += "AND L.PACKAGE_CODE = '" +this.getValue("PACKAGE_DESC")+"' ";
		}
//		else{
//			sql += "AND A.LUMPWORK_CODE IS " +this.getValue("PACKAGE_DESC")+"";
//			
//		}
		if(this.getValue("tCheckBox_0").equals("Y")){
			if(this.getRadioButton("IS").isSelected()){
				sql = sql.replaceFirst("#", " AND A.LUMPWORK_CODE = L.PACKAGE_CODE(+)");
				sql += "AND A.LUMPWORK_CODE IS NOT NULL ";
			}else{
				sql = sql.replaceFirst("#", " AND A.LUMPWORK_CODE = L.PACKAGE_CODE(+)");
				sql += "AND A.LUMPWORK_CODE IS  NULL ";
			}
		}else{
			sql = sql.replaceFirst("#", " AND A.LUMPWORK_CODE = L.PACKAGE_CODE(+)");
		}
		
		//CHN_DESC获知方式
		if(this.getValue("CHN_DESC")!=null && !this.getValue("CHN_DESC").equals("")){
			sql += "AND MS.ID = '" +this.getValue("CHN_DESC")+"' ";
		}
		
		//POST_DESCRIPTION现住址
		if(this.getValue("POST_DESCRIPTION")!=null && !this.getValue("POST_DESCRIPTION").equals("")){
			sql += "AND P.POST_CODE = '" +this.getValue("POST_DESCRIPTION")+"' ";
		}
		
		//USER_NAME;经治医生
		if(this.getValue("USER_NAME")!=null && !this.getValue("USER_NAME").equals("")){
			sql += "AND F.USER_ID = '" +this.getValue("USER_NAME")+"' ";
		}
		
		
		
		
		sql += " ORDER BY G.CHARGE_DATE, B.DEPT_CHN_DESC, PAT_NAME";
		System.out.println("ddddd::::::"+sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		 if(parm.getErrCode() < 0){
			 this.messageBox("查询错误");
			 return;
		 }
		 if(parm.getCount("MR_NO") <= 0){
			 this.messageBox("没有查到数据");
			 table.setParmValue(new TParm());
			 //onClear();
			 return;
		 }
		 getData(parm);
		
	}
	
	/**
	 * 在院时间显示
	 * 
	 */
	public void onCheck(){
		// 初始化时间
        Timestamp date = SystemTool.getInstance().getDate();
        
		this.setValue("IN_DATE", "");
		this.setValue("DS_DATE", "");
		if(this.getValue("tCheckBox_1").equals("Y")){
//			this.setValue("IN_DATE", formateDate.format(SystemTool.getInstance().getDate()));
//			this.setValue("DS_DATE", formateDate.format(SystemTool.getInstance().getDate()));
	        // 初始化查询区间
	        this.setValue("DS_DATE",
	                      date.toString().substring(0, 10).replace('-', '/') +
	                      " 23:59:59");
	        this.setValue("IN_DATE",
	                date.toString().substring(0, 8).replace('-', '/') +
	                "01 00:00:00");
	        
	        
	        
			this.callFunction("UI|IN_DATE|setEnabled", true);
			this.callFunction("UI|DS_DATE|setEnabled", true);
		}else{
			this.callFunction("UI|IN_DATE|setEnabled", false);
			this.callFunction("UI|DS_DATE|setEnabled", false);
		}
	}
	
	/**
	 * 套餐界面方法
	 */
	public void onCheckBox() {
		if(this.getValue("tCheckBox_0").equals("Y")){
			
			this.callFunction("UI|IS|setEnabled", true);
			this.callFunction("UI|NO|setEnabled", true);

			if (this.getValue("IS").equals("Y")) {
				this.callFunction("UI|PACKAGE_DESC|setEnabled", true);
			} else {
				this.setValue("PACKAGE_DESC", "");
				this.callFunction("UI|PACKAGE_DESC|setEnabled", false);
			}	
			this.callFunction("UI|NOT|setEnabled", true);
			
		}else{
			this.callFunction("UI|IS|setEnabled", false);
			this.callFunction("UI|NO|setEnabled", false);
			this.setValue("PACKAGE_DESC", "");
			this.callFunction("UI|PACKAGE_DESC|setEnabled", false);
			this.callFunction("UI|NOT|setEnabled", false);
		}
	}
	
	/**
	 * 清空方法
	 */
	public void onClear() {
		//初始化结算时间 2016.10.14 zl 修改清空结算时间不能正确初始化添加下行date属性
		Timestamp date = SystemTool.getInstance().getDate();
		
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
		//2016.10.14 zl修改点击清空查询时间不能正确初始化问题 注释以下两行
		//this.setValue("S_DATE", formateDate.format(yesterday));
		//this.setValue("E_DATE", formateDate.format(SystemTool.getInstance().getDate()));
		//初始化结算时间 2016.10.14 zl 修改清空结算时间不能正确初始化
        this.setValue("E_DATE",
        			date.toString().substring(0, 10).replace('-', '/') +
        			" 23:59:59");
        this.setValue("S_DATE",
        			date.toString().substring(0, 8).replace('-', '/') +
        			"01 00:00:00");
        
		this.setValue("IN_DATE", formateDate.format(SystemTool.getInstance().getDate()));
		this.setValue("DS_DATE", formateDate.format(SystemTool.getInstance().getDate()));
		
		this.setValue("DEPT_CHN_DESC", "");
		this.setValue("DEPT_CNH_DESC", "");
		
		this.setValue("MR_NO", "");
		this.setValue("RECEIPT_NO", "");
		this.setValue("ACCOUNT_SEQ", "");
		this.setValue("PACKAGE_DESC", "");
		this.setValue("CHN_DESC", "");
		this.setValue("post_description", "");
		this.setValue("USER_NAME", "");
		
		this.onCheck();
		table.setParmValue(new TParm());
	}
	
	/**
	 * 导出Excel方法
	 */
	public void onExport() {
	
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "住院实收入明细报表");
	}
	
	/**
	 * 
	 * @param tagName
	 * @return
	 */
	public TRadioButton getRadioButton(String tagName){
		return (TRadioButton) this.getComponent(tagName);
	}
	
}
