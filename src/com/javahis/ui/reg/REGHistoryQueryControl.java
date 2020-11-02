package com.javahis.ui.reg;

import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import com.dongyang.data.TParm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;

import com.javahis.ui.database.TablePropertyDialogControl;
import com.javahis.util.ExportExcelUtil;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: 挂号历史查询
 * </p>
 * 
 * <p>
 * Description: 挂号历史查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl 2009.08.28
 * @version 1.0
 */
public class REGHistoryQueryControl extends TControl {
	//$$=============add by lich 2014-09-24加入排序功能start==================$$//
	private BILComparator compare = new BILComparator();//modify by wanglong 20121128
	private boolean ascending = false;
	private int sortColumn = -1;
    //$$=============add by lich 2014-09-24 加入排序功能end==================$$//
	TTable table;
	boolean flg = true;
	public void onInit() {
		super.onInit();
//		callFunction("UI|Table|addEventListener", "Table->"
//				+ TTableEvent.CLICKED, this, "onTableClicked");
		initPage();
		// ========pangben modify 20110421 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop
	
	}

	/**
	 * 行单击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		if (row < 0)
			return;
		setPageValue();
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		table = (TTable) this.getComponent("Table");
//		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
//				.getDate(), -1);
//		setValue("S_DATE", yesterday);
//		setValue("E_DATE", SystemTool.getInstance().getDate());
		
		onClickRadioButton(); //add by huangtt 20151130
		setValue("CLINICTYPE_CODE", "");
		setValue("DEPT_CODE", "");
		// 默认区域
		setValue("REGION_CODE", Operator.getRegion());
		setValue("DR_CODE", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
//		this.callFunction("UI|Table|removeRowAll");
//		TTable table = (TTable) this.getComponent("Table");
//		table.removeRowAll();
	}
	
	
	
	/**
	 * TRadioButton选择事件
	 */
	public void deptOrDrAction(){
		table.removeRowAll();
		if(this.getRadioButton("tRadioButton_0").isSelected()){
			flg = true;
			table.setHeader("区域,150;就诊日期,100;科别,100;挂号时间,150;病案号,100;姓名,80;性别,60,SEX_CODE;生日,100;电话,100;号别,120;时段,80;医生,100;诊号,60;身份,80;挂号人,80;退挂人,80;退挂日期,150;金额,100;结算金额,100;接诊科室,100;接诊医生,100;挂号方式,100;挂号备注,100");
			table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,right;17,right;18,left;19,left,20,left,21,left");
			table.setParmMap("REGION_CHN_ABN;ADM_DATE;DEPT_ABS_DESC;REG_DATE;MR_NO;PAT_NAME;SEX;BIRTH_DATE;CELL_PHONE;CLINICTYPE_DESC;SESSION_CODE;USER_NAME;QUE_NO;CTZ_DESC;REG_OPT_USER;REGCAN_USER;REGCAN_DATE;AR_AMT;BALANCE_AR_AMT;DEPT_CODE;DR_CODE;REGMETHOD_CODE;REQUIREMENT");
		}else{
			flg = false;
			table.setHeader("区域,150;就诊日期,100;医生,100;科别,100;挂号时间,150;病案号,100;姓名,80;性别,60,SEX_CODE;生日,100;电话,100;号别,120;时段,80;诊号,60;身份,80;挂号人,80;退挂人,80;退挂日期,150;金额,100;结算金额,100;接诊科室,100;接诊医生,100;挂号方式,100;挂号备注,100");
			table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,right;17,right;18,left;19,left,20,left,21,left");
			table.setParmMap("REGION_CHN_ABN;ADM_DATE;USER_NAME;DEPT_ABS_DESC;REG_DATE;MR_NO;PAT_NAME;SEX;BIRTH_DATE;CELL_PHONE;CLINICTYPE_DESC;SESSION_CODE;QUE_NO;CTZ_DESC;REG_OPT_USER;REGCAN_USER;REGCAN_DATE;AR_AMT;BALANCE_AR_AMT;DEPT_CODE;DR_CODE;REGMETHOD_CODE;REQUIREMENT");
		}
		
	}
	
	/**
	 * @param tagName
	 * @return
	 */
	public TRadioButton getRadioButton(String tagName){
		return (TRadioButton) this.getComponent(tagName);
	}
	
	/**
	 * 打印
	 */
	public void onPrint() {
		print();
	}

	/**
	 * 调用报表打印预览界面
	 */
	private void print() {
		TTable table = (TTable) this.getComponent("Table");
		int row = table.getRowCount();
		if (row < 1) {
			this.messageBox("先查询数据!");
			return;
		}
		
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		TParm printData = this.getPrintDate();
		String sDate="";
		String eDate="";
		if ("Y".equalsIgnoreCase(this.getValueString("ADM_DATE_FLG"))) {
			 sDate = StringTool.getString(
					TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd")
					+ " 00:00:00";
			 eDate = StringTool.getString(
					TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd")
					+ " 23:59:59";
		}
		if ("Y".equalsIgnoreCase(this.getValueString("REG_DATE_FLG"))) {

			 sDate = StringTool.getString(
					TypeTool.getTimestamp(getValue("S_DATE_REG")), "yyyy/MM/dd HH:mm:ss");
			 eDate = StringTool.getString(
					TypeTool.getTimestamp(getValue("E_DATE_REG")), "yyyy/MM/dd HH:mm:ss");
			
		}
		
		TParm parm = new TParm();
		// ========pangben modify 20110329 start,fuxin modify 20120306
		String region = ((TTable) this.getComponent("Table")).getParmValue()
				.getRow(0).getValue("REGION_CHN_ABN");
		parm.setData("TITLE", "TEXT", (this.getValue("REGION_CODE") != null
				&& !this.getValue("REGION_CODE").equals("") ? region : "所有医院")
				+ "挂号历史信息报表");
		// ========pangben modify 20110329 stop
		parm.setData("S_DATE", "TEXT", sDate);
		parm.setData("E_DATE", "TEXT", eDate);
		parm.setData("OPT_USER", "TEXT",Operator.getName());
		parm.setData("OPT_DATE", "TEXT", sysDate);
		parm.setData("historyQuerytable", printData.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGHistoryQuery.jhw",
				parm);

	}

	/**
	 * 整理打印数据
	 * 
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @return TParm
	 */
	private TParm getPrintDate() {
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		
		try {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm selParm = new TParm();
		String clinicTypeCodeWhere = "";
		if (getValue("CLINICTYPE_CODE").toString().length() != 0)
			clinicTypeCodeWhere = " AND A.CLINICTYPE_CODE = '"
					+ getValue("CLINICTYPE_CODE") + "'  ";
		String deptCodeWhere = "";
		if (getValue("DEPT_CODE").toString().length() != 0)
			deptCodeWhere = " AND A.REALDEPT_CODE = '" + getValue("DEPT_CODE")
					+ "'  ";
		String drCodeWhere = "";
		if (getValue("DR_CODE").toString().length() != 0)
			drCodeWhere = " AND A.REALDR_CODE = '" + getValue("DR_CODE")
					+ "'  ";
		String mrNoWhere = "";
		if (getValue("MR_NO").toString().trim().length() != 0)
			mrNoWhere = " AND A.MR_NO = '" + getValue("MR_NO") + "'  ";
		// ================pangben modify 20110408 start
		String reqion = "";
		if (this.getValueString("REGION_CODE").length() != 0)
			reqion = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		String date = "";
		if ("Y".equalsIgnoreCase(this.getValueString("ADM_DATE_FLG"))) {
			String startTime = StringTool.getString(
					TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
			String endTime = StringTool.getString(
					TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
			if(startTime.length() == 0 || endTime.length() == 0){
				this.messageBox("查询日期不能为空");
				this.callFunction("UI|Table|setParmValue", new TParm());
				return null;
			}
			date =" a.adm_date BETWEEN TO_DATE ('"+startTime+"000000', 'yyyyMMddHH24miss') "
            +" AND TO_DATE ('"+endTime+"235959', 'yyyyMMddHH24miss') ";
		}
		if ("Y".equalsIgnoreCase(this.getValueString("REG_DATE_FLG"))) {
			
			String startTime = getValueString("S_DATE_REG");
			String endTime = getValueString("E_DATE_REG");
			
			if(startTime.length() == 0 || endTime.length() == 0){
				this.messageBox("查询日期不能为空");
				this.callFunction("UI|Table|setParmValue", new TParm());
				return null;
			}
						
			startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
			endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
			
			date =" a.reg_date BETWEEN TO_DATE ('"+startTime+"', 'yyyyMMddHH24miss') "
            +" AND TO_DATE ('"+endTime+"', 'yyyyMMddHH24miss') ";
		}
		
		// ================pangben modify 20110408 stop,fuxin modify 20120306
		//=================yanjing modify 20130624 添加退挂人、退挂日期字段
		//=================yuml modify 20141028 添加挂号时间字段
//		String sql = " SELECT J.USER_NAME AS REGCAN_USER,A.REGCAN_DATE, H.REGION_CHN_ABN,A.ADM_DATE,A.REG_DATE,J.OPT_USER, A.MR_NO,C.PAT_NAME, A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE,"
//				+ "        A.QUE_NO, A.CTZ1_CODE, B.AR_AMT, D.CLINICTYPE_DESC,"
//				+ "        E.USER_NAME, F.DEPT_ABS_DESC,G.CTZ_DESC,A.REGION_CODE,"
//				+ "		   I.CHN_DESC AS SEX,C.BIRTH_DATE,C.TEL_HOME," +
//				  "        C.CELL_PHONE,K.USER_NAME AS REG_OPT_USER, "+//20141222 wangjingchun add 959/957
//						" CASE " +
//						" WHEN " +
//						" A.SESSION_CODE = '01' " +
//						" THEN " +
//						" '上午' " +
//						" WHEN " +
//						" A.SESSION_CODE = '02' " +
//						" THEN " +
//						" '下午' " +
//						" END SESSION_CODE "
//				+ "   FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,REG_CLINICTYPE D,"
//				+ "        SYS_OPERATOR E,SYS_DEPT F,SYS_CTZ G,SYS_REGION H,SYS_DICTIONARY I,SYS_OPERATOR J, "
//				+ "        SYS_OPERATOR K "
//				+ "  WHERE A.REG_DATE BETWEEN TO_DATE ('"
//				+ startTime
//				+ "000000"
//				+ "', 'yyyyMMddHH24miss') "
//				+ "                       AND TO_DATE ('"
//				+ endTime
//				+ "235959"
//				+ "', 'yyyyMMddHH24miss') "
//				+ "    AND A.REGCAN_USER = J.USER_ID(+) "
//				+"     AND A.CASH_CODE = K.USER_ID "//20141222 wangjingchun add 959
//				+ "    AND A.MR_NO = C.MR_NO "
//				+ "    AND A.CASE_NO = B.CASE_NO(+) "
//				+ "    AND A.REALDEPT_CODE = F.DEPT_CODE "
//				+ "    AND A.REGION_CODE = H.REGION_CODE(+) "
//				+ // =========pangben modify 20110408
//				"    AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE " +
//				" AND B.RESET_RECEIPT_NO IS NULL" //===add by huangtt 20141205
//				+ clinicTypeCodeWhere
//				+ deptCodeWhere
//				+ drCodeWhere
//				+ mrNoWhere
//				+ reqion
//				+ // ======pangben modify 20110325
//				"    AND A.REALDR_CODE = E.USER_ID   AND I.GROUP_ID = 'SYS_SEX'  AND I.ID = C.SEX_CODE"
//				+ "    AND A.CTZ1_CODE = G.CTZ_CODE ORDER BY H.REGION_CHN_ABN,A.ADM_DATE DESC,A.REALDEPT_CODE"; // =====fuxin//yuml  20141022
		String sql = "SELECT a.case_no,j.user_name AS regcan_user, "
			       +" a.regcan_date, "
			       +" a.requirement, "
			       +" l.REGMETHOD_DESC AS regmethod_code," 
					
			       +" h.region_chn_abn, "
			       +" a.adm_date, "
			       +" a.reg_date, "
			       +" j.opt_user, "
			       +" a.mr_no, "
			       +" c.pat_name, "
			       +" a.clinictype_code, "
			       +" a.realdept_code, "
			       +" a.realdr_code, "
			       +" a.que_no, "
			       +" a.ctz1_code, "
			       +" b.ar_amt, "
			       +" d.clinictype_desc, "
			       +" e.user_name, "
			       +" f.dept_abs_desc, "
			       +" g.ctz_desc, "
			       +" a.region_code, "
			       +" i.chn_desc AS sex, "
			       +" c.birth_date, "
			       +" c.tel_home, "
			       +" c.cell_phone, "
			       +" k.user_name AS reg_opt_user, "
			       +" CASE "
			          +" WHEN a.session_code = '01' THEN '上午' "
			          +" WHEN a.session_code = '02' THEN '下午' "
			          +" WHEN a.session_code = '11' THEN '急诊白班' "
			          +" WHEN a.session_code = '12' THEN '急诊夜班' "
			       +" END "
			          +" session_code "
			  +" FROM reg_patadm a, "
			       +" bil_reg_recp b, "
			       +" sys_patinfo c, "
			       +" reg_clinictype d, "
			       +" sys_operator e, "
			       +" sys_dept f, "
			       +" sys_ctz g, "
			       +" sys_region h, "
			       +" sys_dictionary i, "
			       +" sys_operator j, "
			       +" sys_operator k, "
			       +"reg_regmethod l" 
			       
			 +" WHERE  " + date
			       +" AND a.regcan_user = j.user_id(+) "
			       +" AND a.regcan_user IS NULL "
			       +" AND b.cash_code = k.user_id "
			       +" AND a.mr_no = c.mr_no "
			       +" AND a.case_no = b.case_no "
			       +" AND a.realdept_code = f.dept_code "
			       +" AND a.region_code = h.region_code(+) "
			       +" AND a.clinictype_code = d.clinictype_code "
			       +" AND b.reset_receipt_no IS NULL "
			       + clinicTypeCodeWhere
			       + deptCodeWhere
			       + drCodeWhere
			       + mrNoWhere
			       + reqion
			       +" AND a.realdr_code = e.user_id "
			       +" AND i.GROUP_ID = 'SYS_SEX' "
			       +"AND l.REGMETHOD_CODE = a.REGMETHOD_CODE  "
			       +" AND i.ID = c.sex_code "
			       +" AND a.ctz1_code = g.ctz_code "
			+" UNION ALL "
			+" SELECT a.case_no,j.user_name AS regcan_user, "
			       +" a.regcan_date, "
			       +" a.requirement, "
			       +" l.REGMETHOD_DESC AS regmethod_code," 
					
			       +" h.region_chn_abn, "
			       +" a.adm_date, "
			       +" a.reg_date, "
			       +" j.opt_user, "
			       +" a.mr_no, "
			       +" c.pat_name, "
			       +" a.clinictype_code, "
			       +" a.realdept_code, "
			       +" a.realdr_code, "
			       +" a.que_no, "
			       +" a.ctz1_code, "
			       +" b.ar_amt, "
			       +" d.clinictype_desc, "
			       +" e.user_name, "
			       +" f.dept_abs_desc, "
			       +" g.ctz_desc, "
			       +" a.region_code, "
			       +" i.chn_desc AS sex, "
			       +" c.birth_date, "
			       +" c.tel_home, "
			       +" c.cell_phone, "
			       +" k.user_name AS reg_opt_user, "
			       +" CASE "
			          +" WHEN a.session_code = '01' THEN '上午' "
			          +" WHEN a.session_code = '02' THEN '下午' "
			          +" WHEN a.session_code = '11' THEN '急诊白班' "
			          +" WHEN a.session_code = '12' THEN '急诊夜班' "
			       +" END "
			          +" session_code "
			  +" FROM reg_patadm a, "
			       +" bil_reg_recp b, "
			       +" sys_patinfo c, "
			       +" reg_clinictype d, "
			       +" sys_operator e, "
			       +" sys_dept f, "
			       +" sys_ctz g, "
			       +" sys_region h, "
			       +" sys_dictionary i, "
			       +" sys_operator j, "
			       +" sys_operator k, "
			       +"reg_regmethod l" 
			 +" WHERE  " + date
			       +" AND a.regcan_user = j.user_id(+) "
			       +" AND a.regcan_user IS NOT NULL "
			       +" AND b.cash_code = k.user_id "
			       +" AND a.mr_no = c.mr_no "
			       +" AND a.case_no = b.case_no "
			       +" AND a.realdept_code = f.dept_code "
			       +" AND a.region_code = h.region_code(+) "
			       +" AND a.clinictype_code = d.clinictype_code "
			       +" AND b.reset_receipt_no IS NOT NULL "
			       + clinicTypeCodeWhere
			       + deptCodeWhere
			       + drCodeWhere
			       + mrNoWhere
			       + reqion
			       +" AND a.realdr_code = e.user_id "
			       +"AND l.REGMETHOD_CODE = a.REGMETHOD_CODE  "
			       +" AND i.GROUP_ID = 'SYS_SEX' "
			       +" AND i.ID = c.sex_code "
			       +" AND a.ctz1_code = g.ctz_code ";
			//+" ORDER BY region_chn_abn, adm_date DESC, realdept_code ";
//			       +" ORDER BY realdept_code ,reg_date DESC";
		
		
		String sqlB = "select a.case_no,sum(b.ar_amt) balance_ar_amt from reg_patadm a,opd_order b " +
				" WHERE " +  date +
				" AND a.case_no = b.case_no and b.rexp_code='102' " 
				+ clinicTypeCodeWhere
			       + deptCodeWhere
			       + drCodeWhere
			       + mrNoWhere
			       + reqion +
				"GROUP BY a.case_no";
		
		String sqlS = "SELECT a.*,b.balance_ar_amt FROM ("+sql+") a FULL OUTER JOIN ("+sqlB+") b ON a.case_no=b.case_no " ;
		
		if(flg){
			sqlS += "  ORDER BY realdept_code, adm_date DESC,reg_date DESC";
		}else{
			sqlS += "  ORDER BY realdr_code, adm_date DESC,reg_date DESC";
		}
				
		
		
//		System.out.println("挂号历史＝＝＝＝＝＝＝＝＝＝"+sqlS);																			// modify
		selParm = new TParm(TJDODBTool.getInstance().select(sqlS));
		
		if (selParm.getCount("MR_NO") < 1) {
			this.messageBox("查无数据");
			this.callFunction("UI|Table|setParmValue", new TParm());
			this.initPage();
			return selParm;
		}
		// ==========pangben modify 20110425 start 累计
		double sumAramt = 0.00;
		double sumBaamt = 0.00;
		// ==========pangben modify 20110425 stop
		TParm endDate = new TParm();
		int count = selParm.getCount("MR_NO");
		//yuml  strat 20141022
		String deptOrDrDesc = "";
		String admDateStr = "";
		int j = 0;
		int n = 0;
		int tuigua = 0;
		int alltuigua = 0;
		double jar_amt = 0;
		double bal_jar_amt = 0;
		// ADM_DATE;MR_NO;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
		for (int i = 0; i <= count; i++) {
			Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);
			String now = StringTool.getString(admDate, "yyyy/MM/dd");
			if((i!=0&&(!(flg ? selParm.getValue("DEPT_ABS_DESC", i) : selParm.getValue("USER_NAME", i)).equals(deptOrDrDesc)||!now.equals(admDateStr)))||i==count){
				endDate.addData("REGION_CHN_ABN","小计:");
				endDate.addData("ADM_DATE_S","小计:");
				endDate.addData("ADM_DATE","");
				endDate.addData("REG_DATE","");
				endDate.addData("MR_NO","");
				endDate.addData("REGCAN_DATE","");
				endDate.addData("PAT_NAME",(endDate.getCount("PAT_NAME")-n-tuigua)+"人次");
				endDate.addData("SEX", "");
//				endDate.addData("TEL_HOME", "");//20141222 wangjingchun modify 957
				//手机号码
				endDate.addData("CELL_PHONE", "");//20141222 wangjingchun add 957
				endDate.addData("BIRTH_DATE", "");
				endDate.addData("CLINICTYPE_DESC", "");
				endDate.addData("DEPT_ABS_DESC", "");
				endDate.addData("USER_NAME", "");
				endDate.addData("QUE_NO", "");
				endDate.addData("CTZ_DESC", "");
				endDate.addData("SESSION_CODE", "");
				endDate.addData("AR_AMT", df.format(sumAramt-jar_amt));
				endDate.addData("BALANCE_AR_AMT", df.format(sumBaamt-bal_jar_amt));
				//挂号人
				endDate.addData("REG_OPT_USER", "");//20141222 wangjingchun add 957
				endDate.addData("REGCAN_USER","");
		
				endDate.addData("REQUIREMENT","");
				endDate.addData("REGMETHOD_CODE","");
				
				
				//接诊科室和接诊医生
				endDate.addData("DR_CODE", "");
				endDate.addData("DEPT_CODE", "");
				
				n=endDate.getCount("PAT_NAME");
				jar_amt = sumAramt;
				bal_jar_amt = sumBaamt;
				j++;
				tuigua = 0;
			}
//			if(selParm.getDouble("AR_AMT", i)<=0){
//				tuigua+=2;  
//				alltuigua+=2; //modify by huangtt 20141205
//			}
			//modify by huangtt 20141205
			if(selParm.getValue("REGCAN_USER", i).length() > 0){
				tuigua+=1;  
				alltuigua+=1; 
			}
			//yuml  strat 20141022
			// =============pangben modify 20110408 start 在打印的报表中添加区域
			String reqionTemp = selParm.getValue("REGION_CHN_ABN", i); // ====fuxin
																		// modify
																		// 20120306
			// =============pangben modify 20110408 stop
//			Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);  //yuml 20141022
			admDateStr = StringTool.getString(admDate, "yyyy/MM/dd");
			String regDate = StringTool.getString(selParm.getTimestamp("REG_DATE", i),"yyyy/MM/dd HH:mm:ss");
			String mrNo = selParm.getValue("MR_NO", i);
			String patName = selParm.getValue("PAT_NAME", i);
			String sex_code = selParm.getValue("SEX", i);
//			String tel_home = selParm.getValue("TEL_HOME", i);//20141222 wangjingchun modify 957
			String tel_home = selParm.getValue("CELL_PHONE", i);//20141222 wangjingchun add 957
			
//			String optUser = selParm.getValue("OPT_USER",i);
			String requirement = selParm.getValue("REQUIREMENT",i);
			String regmethodCode = selParm.getValue("REGMETHOD_CODE",i);
			Timestamp birth_date = selParm.getTimestamp("BIRTH_DATE", i);
			String birth_datestr = StringTool.getString(birth_date,
					"yyyy/MM/dd");
			String clinicDesc = selParm.getValue("CLINICTYPE_DESC", i);
			if(flg){
				deptOrDrDesc = selParm.getValue("DEPT_ABS_DESC", i);
			}else{
				deptOrDrDesc = selParm.getValue("USER_NAME", i);
			}
			String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);    //yuml   20141022
			String useName = selParm.getValue("USER_NAME", i);
			String session_code = selParm.getValue("SESSION_CODE", i);
			int queNo = selParm.getInt("QUE_NO", i);
			String ctzDesc = selParm.getValue("CTZ_DESC", i);
			double ar_amt = selParm.getDouble("AR_AMT", i);
			double bal_amt = selParm.getDouble("BALANCE_AR_AMT", i);
//			String opt_user_sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
//					+selParm.getValue("REG_OPT_USER", i)+"'";
//			TParm opt_user_parm = new TParm(TJDODBTool.getInstance().select(opt_user_sql));
			String optuser_name = selParm.getValue("REG_OPT_USER", i);//20141222 wangjingchun add 957
			//yanjing 20130705 添加退挂人、退挂日期
			String regcan_user = selParm.getValue("REGCAN_USER", i);
			String regcan_date = selParm.getValue("REGCAN_DATE", i).toString().replace("-", "/");
			if(!regcan_date.equals("")){
				 regcan_date = regcan_date.substring(0,regcan_date.length()-2);
			}else{
				sumAramt += StringTool.round(ar_amt, 2);
				sumBaamt += StringTool.round(bal_amt, 2);
			}
		//yanjing end

			endDate.addData("REGION_CHN_ABN", reqionTemp);// =============pangben
															// modify
															// 20110408,fuxin
															// modify 20120306
			endDate.addData("ADM_DATE_S",admDateStr);
			endDate.addData("ADM_DATE", admDateStr);
			endDate.addData("REG_DATE", regDate);
			endDate.addData("MR_NO", mrNo);
			endDate.addData("PAT_NAME", patName);
			endDate.addData("SEX", sex_code);
//			endDate.addData("TEL_HOME", tel_home);//20141222 wangjingchun modify 957
			endDate.addData("CELL_PHONE", tel_home);//20141222 wangjingchun add 957
			endDate.addData("BIRTH_DATE", birth_datestr);
			endDate.addData("CLINICTYPE_DESC", clinicDesc);
			endDate.addData("DEPT_ABS_DESC", deptDesc);
			endDate.addData("USER_NAME", useName);
			endDate.addData("QUE_NO", queNo);
			endDate.addData("CTZ_DESC", ctzDesc);
			endDate.addData("SESSION_CODE", session_code);
			endDate.addData("AR_AMT", df.format(ar_amt));
			endDate.addData("BALANCE_AR_AMT", df.format(bal_amt));
//			endDate.addData("sumAramt", df.format(ar_amt));
			endDate.addData("REG_OPT_USER", optuser_name);//20141222 wangjingchun add 957
			endDate.addData("REGCAN_USER",regcan_user);//yanjing 20130624添加退挂人员
			endDate.addData("REGCAN_DATE",regcan_date);//yanjing 20130624添加退挂日期
//			endDate.addData("OPT_USER",optUser);
			endDate.addData("REQUIREMENT",requirement);
			endDate.addData("REGMETHOD_CODE",regmethodCode);
			
			//添加接诊科室和接诊医生
			String caseNo = selParm.getValue("CASE_NO", i);
			TParm reDate = this.getReceiveDrDept(caseNo);
			endDate.addData("DR_CODE", reDate.getData("DR_CODE"));
			endDate.addData("DEPT_CODE", reDate.getData("DEPT_CODE"));
			
		}

		// =============pangben modify 20110408 start
//		endDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN"); // fuxin modify
																// 20120306
		// =============pangben modify 20110408 stop
		endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE_S");
//		endDate.addData("SYSTEM", "COLUMNS", "REG_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "MR_NO");
		endDate.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "SEX");
//		endDate.addData("SYSTEM", "COLUMNS", "TEL_HOME");//20141222 wangjingchun modify 957
//		endDate.addData("SYSTEM", "COLUMNS", "CELL_PHONE");//20141222 wangjingchun add 957
//		endDate.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "SESSION_CODE");
		endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
//		endDate.addData("SYSTEM", "COLUMNS", "");
		endDate.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "REG_OPT_USER");//20141222 wangjingchun add 957
		//yanjing 20130705 添加退挂人、退挂日期
		endDate.addData("SYSTEM", "COLUMNS", "REGCAN_USER");
//		endDate.addData("SYSTEM", "COLUMNS", "REQUIREMENT");
//		endDate.addData("SYSTEM", "COLUMNS", "REGMETHOD_CODE");
		endDate.addData("SYSTEM", "COLUMNS", "REGCAN_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
		
		// ==========pangben modify 20110425 start
		//yuml   20141022  start
		endDate.setData("REGION_CHN_ABN", count+j, "总计:");
		endDate.setData("ADM_DATE_S", count+j, "总计:"); 
		endDate.setData("ADM_DATE", count+j, ""); 
		endDate.setData("REG_DATE", count+j, "");
		endDate.setData("MR_NO", count+j,"1");
		endDate.setData("REGCAN_DATE",count+j,"");
		endDate.setData("PAT_NAME", count+j, (count-alltuigua)+"人次"); 
		endDate.setData("SEX", count+j, "");
//		endDate.setData("TEL_HOME", count+j, "");//20141222 wangjingchun modify 957
		endDate.setData("CELL_PHONE", count+j, "");//20141222 wangjingchun add 957
		endDate.setData("BIRTH_DATE", count+j, "");
		endDate.setData("CLINICTYPE_DESC", count+j, "");
		endDate.setData("DEPT_ABS_DESC", count+j, "");
		endDate.setData("USER_NAME", count+j, "");
		endDate.setData("QUE_NO", count+j, "");
		endDate.setData("CTZ_DESC", count+j, "");
		endDate.setData("SESSION_CODE", count+j, "");
		endDate.setData("AR_AMT", count+j, df.format(sumAramt));
		endDate.setData("BALANCE_AR_AMT", count+j, df.format(sumBaamt));
		endDate.setData("REG_OPT_USER", count+j, "");//20141222 wangjingchun add 957
//		endDate.setData("OPT_USER", count, Operator.getName());
		endDate.setData("REGCAN_USER",count+j,"");  //yuml   20141022  end
		
		
		
		
		//接诊科室和接诊医生
		endDate.setData("DR_CODE",count+j, "");
		endDate.setData("DEPT_CODE",count+j, "");
		
		endDate.setCount(endDate.getCount("MR_NO"));
		
		// ==========pangben modify 20110425 start
		this.callFunction("UI|Table|setParmValue", endDate);
		//$$=====add by lich 2014/09/24 加入排序方法start============$$//
		addListener(getTTable("TABLE"));
		//$$=====add by lich 2014/09/24 加入排序方法end============$$//
		return endDate;
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: MEMPackageReFeeAction ，method ：onSave");
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * 得到接诊医生科室数据
	 * @param caseNo
	 * @return
	 */
	public TParm getReceiveDrDept(String caseNo){
		String sql = "  SELECT C.USER_NAME DR_CODE, B.DEPT_CHN_DESC DEPT_CODE" +
				" FROM OPD_ORDER A, SYS_DEPT B, SYS_OPERATOR C" +
				" WHERE CASE_NO = '"+caseNo+"'" +
				" AND A.DR_CODE = C.USER_ID" +
				" AND A.DEPT_CODE = B.DEPT_CODE" +
				" ORDER BY A.RX_TYPE ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result = new TParm();
		if(parm.getCount() > 0){
			result.setData("DR_CODE", parm.getValue("DR_CODE", 0));
			result.setData("DEPT_CODE", parm.getValue("DEPT_CODE", 0));
		}else{
			result.setData("DR_CODE", "");
			result.setData("DEPT_CODE", "");
		}
		return result;
		
	}

	/**
	 * 查询
	 */
	public void onQuery() {
//		String startTime = StringTool.getString(
//				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
//		String endTime = StringTool.getString(
//				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		
		TParm printData = this.getPrintDate();
		
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "挂号历史数据报表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		TTable table = (TTable) this.getComponent("Table");
//		table.removeRowAll();
		TParm parm = new TParm();
		table.setParmValue(parm);
		this.getRadioButton("tRadioButton_0").setSelected(true);
		deptOrDrAction();
	}

	/**
	 * 点选grid数据给界面翻值
	 */
	public void setPageValue() {
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
		}
		TParm tableParm = table.getParmValue();
		String clinicTypeCode = tableParm.getValue("CLINICTYPE_CODE", selRow);
		String deptCode = tableParm.getValue("REALDEPT_CODE", selRow);
		String drCode = tableParm.getValue("REALDR_CODE", selRow);
		String mrNo = tableParm.getValue("MR_NO", selRow);
		String patName = tableParm.getValue("PAT_NAME", selRow);
		String region_code = tableParm.getValue("REGION_CODE", selRow);// ==pangben
																		// modify
																		// 20110413
		setValue("CLINICTYPE_CODE", clinicTypeCode);
		setValue("DEPT_CODE", deptCode);
		setValue("DR_CODE", drCode);
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", patName);
		setValue("REGION_CODE", region_code);// ==pangben modify 20110413
	}

	/**
	 * 查询病案号 ===zhangp 20120326
	 */
	public void onQueryMrNo() {
		String mrNo = getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		mrNo = pat.getMrNo();
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME",pat.getName());
	}

	//$$==============add by lich 2014/09/24 加入排序功能start=============$$//
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TTable table = getTTable("TABLE");
				TParm tableData = table.getParmValue();
//				System.out.println("tableData---------"+tableData);
				
				TParm tempParm = new TParm();
				tempParm.addRowData(tableData, tableData.getCount()-1);
//				System.out.println("tempParm---------"+tempParm);
				int row = tableData.getCount()-1;
//				System.out.println("row="+row);
				if(row <= 0){
					return;
				}
				tableData.removeRow("Data", row);
//				table.setParmValue(tableData);
//				System.out.println("tableData---------"+tableData);
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
//				System.out.println("columnName=========="+Arrays.toString(columnName));
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				tableData = cloneVectoryParam(vct, new TParm(), strNames);
				tableData.addRowData(tempParm, 0);
				getTTable("TABLE").setParmValue(tableData);
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
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
		return parmTable;
//		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	
	
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
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
	
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	//$$==============add by lich 2014/09/24 加入排序功能end=============$$//
	
	public void onClickRadioButton(){
		
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		Timestamp date = SystemTool.getInstance().getDate();
		if ("Y".equalsIgnoreCase(this.getValueString("ADM_DATE_FLG"))) {
			callFunction("UI|S_DATE_REG|setEnabled", false);
			callFunction("UI|E_DATE_REG|setEnabled", false);
			callFunction("UI|S_DATE|setEnabled", true);
			callFunction("UI|E_DATE|setEnabled", true);
			setValue("S_DATE", yesterday);
			setValue("E_DATE", date);
			setValue("S_DATE_REG", "");
			setValue("E_DATE_REG", "");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("REG_DATE_FLG"))) {
			callFunction("UI|S_DATE_REG|setEnabled", true);
			callFunction("UI|E_DATE_REG|setEnabled", true);
			callFunction("UI|S_DATE|setEnabled", false);
			callFunction("UI|E_DATE|setEnabled", false);
			setValue("S_DATE_REG", yesterday.toString().substring(0, 10).replace('-', '/') + " 08:00:00");
			setValue("E_DATE_REG", date.toString().substring(0, 10).replace('-', '/') + " 07:59:59");
			setValue("S_DATE", "");
			setValue("E_DATE", "");
		}
	}
	
	
}
