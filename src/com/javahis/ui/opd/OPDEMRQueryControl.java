/**
 * 
 */
package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import jdo.ekt.EKTIO;
import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.odo.ODOMainReg;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-7-9上午11:18:02
 * @version 1.0
 */
public class OPDEMRQueryControl extends TControl {
	Pat pat;
	String MR_NO = ""; // 病案号

	public void onPatQuery() {
		TParm sendParm = new TParm();
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onQueryMrNo();
		// this.onMrno();
	}

	/**
	 * 查询病案号 ===zhangp 20120326
	 */
	public void onQueryMrNo() {
		String mrNo = getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		mrNo = pat.getMrNo();
		setValue("PAT_NAME", pat.getName());
		setValue("MR_NO", pat.getMrNo());
		onQuery();
		// TParm parm = new TParm();

		// REGION_CHN_ABN;ADM_DATE;MR_NO;PAT_NAME;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
		// parm.addData("REGION_CHN_ABN", pat.get);//=============pangben modify
		// 20110408,fuxin modify 20120306
		// parm.addData("ADM_DATE", admDateStr);
		// parm.addData("MR_NO", mrNo);
		// parm.addData("PAT_NAME", patName);
		// parm.addData("CLINICTYPE_DESC", clinicDesc);
		// parm.addData("DEPT_ABS_DESC", deptDesc);
		// parm.addData("USER_NAME", useName);
		// parm.addData("QUE_NO", queNo);
		// parm.addData("CTZ_DESC", ctzDesc);
		// parm.addData("AR_AMT", df.format(ar_amt));
	}

	public void onInit() {
		super.onInit();
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		initPage();
		// ========pangben modify 20110421 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop
		
		//====add by huangtt 20150204 start
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm acceptData = (TParm) obj;
			this.setValue("MR_NO", acceptData.getValue("MR_NO"));
			this.onQueryMrNo();
		}
		//====add by huangtt 20150204 end

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
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.MONTH, -3);
		Timestamp threeMonthAgo = new Timestamp(calendar.getTimeInMillis());
		setValue("S_DATE", threeMonthAgo);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		setValue("CLINICTYPE_CODE", "");
		setValue("DEPT_CODE", "");
		// 默认区域
		setValue("REGION_CODE", Operator.getRegion());
		setValue("DR_CODE", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		this.callFunction("UI|Table|removeRowAll");
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
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		TParm printData = this.getPrintDate(startTime, endTime);
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("S_TIME");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("E_TIME");
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
		parm.setData("OPT_USER", Operator.getName());
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
	private TParm getPrintDate(String startTime, String endTime) {
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
		// ================pangben modify 20110408 stop,fuxin modify 20120306
		// ================yuml modify 20141028 添加主诊断字段，去掉科室代码、就诊号、金额
		// modify by wangb 2017/6/9 增加主诊断备注
		String sql = " SELECT H.REGION_CHN_ABN,A.ADM_TYPE, A.ADM_DATE, J.ICD_CHN_DESC,I.DIAG_NOTE, A.CASE_NO, A.MR_NO,C.PAT_NAME, A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE," 
				+ " (CASE WHEN C.HIGHRISKMATERNAL_FLG='Y' THEN '是' ELSE '' END ) AS HIGHRISKMATERNAL_FLG, "
				+ "        A.QUE_NO, A.CTZ1_CODE, B.AR_AMT, D.CLINICTYPE_DESC,"
				+ "        E.USER_NAME, F.DEPT_ABS_DESC,G.CTZ_DESC,A.REGION_CODE"
				+ "   FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,REG_CLINICTYPE D,"
				+ "        SYS_OPERATOR E,SYS_DEPT F,SYS_CTZ G,SYS_REGION H,OPD_DIAGREC I,SYS_DIAGNOSIS J  "
				+ "  WHERE A.ADM_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000"
				+ "', 'yyyyMMddHH24miss') "
				+ "                       AND TO_DATE ('"
				+ endTime
				+ "235959"
				+ "', 'yyyyMMddHH24miss') "
				//===zhangp 20120714 start
				+ "    AND A.SEE_DR_FLG IN ('Y','T') "
				//===zhangp 20120714 end
				+ "    AND A.MR_NO = C.MR_NO "
				+ "    AND A.CASE_NO = B.CASE_NO(+) "
				+ "    AND A.REALDEPT_CODE = F.DEPT_CODE "
				+ "    AND A.REGION_CODE = H.REGION_CODE(+) "
				+ // =========pangben modify 20110408
				"    AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE "
				// =========yuml 20141028
				+ "    AND A.CASE_NO = I.CASE_NO  AND I.MAIN_DIAG_FLG='Y'  AND I.ICD_CODE=J.ICD_CODE AND I.ICD_TYPE=J.ICD_TYPE "
				+ clinicTypeCodeWhere
				+ deptCodeWhere
				+ drCodeWhere
				+ mrNoWhere
				+ reqion
				+ // ======pangben modify 20110325
				"    AND A.REALDR_CODE = E.USER_ID "
				+ "    AND A.CTZ1_CODE = G.CTZ_CODE ORDER BY H.REGION_CHN_ABN,A.ADM_DATE DESC"; // =====fuxin
																								// modify
																								// 20120306
		 System.out.println("sql!!!!!!!!!!!!!!!::::::" + sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount("MR_NO") < 1) {
			this.messageBox("查无数据");
			this.initPage();
			return selParm;
		}
		// ==========pangben modify 20110425 start 累计
//		double sumAramt = 0.00;//yuml  20141028
		// ==========pangben modify 20110425 stop
		TParm endDate = new TParm();
		int count = selParm.getCount("MR_NO");
		// ADM_DATE;MR_NO;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
		for (int i = 0; i < count; i++) {
			// =============pangben modify 20110408 start 在打印的报表中添加区域
			String reqionTemp = selParm.getValue("REGION_CHN_ABN", i); // ====fuxin
																		// modify
																		// 20120306
			// =============pangben modify 20110408 stop
			Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);
			String admDateStr = StringTool.getString(admDate, "yyyy/MM/dd");
			//yuml  s   20141028
			String admtype = selParm.getValue("ADM_TYPE", i);
			String icddesc = selParm.getValue("ICD_CHN_DESC", i);
			//yuml  e   20141028
			String mrNo = selParm.getValue("MR_NO", i);
			String patName = selParm.getValue("PAT_NAME", i);
			String maternalDesc = selParm.getValue("HIGHRISKMATERNAL_FLG", i);
			String clinicDesc = selParm.getValue("CLINICTYPE_DESC", i);
			String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);
			String useName = selParm.getValue("USER_NAME", i);
			int queNo = selParm.getInt("QUE_NO", i);
			String ctzDesc = selParm.getValue("CTZ_DESC", i);
			//yuml  s   20141028
//			String drCode = selParm.getValue("REALDR_CODE", i);
//			double ar_amt = selParm.getDouble("AR_AMT", i);
			// ==========pangben modify 20110425 start
//			sumAramt += StringTool.round(ar_amt, 2);
			// ==========pangben modify 20110425 stop
			//yuml  e   20141028
			endDate.addData("REGION_CHN_ABN", reqionTemp);// =============pangben
															// modify
															// 20110408,fuxin
															// modify 20120306
			endDate.addData("ADM_DATE", admDateStr);
			//yuml  s   20141028
			endDate.addData("ADM_TYPE", admtype);
			endDate.addData("ICD_CHN_DESC", icddesc);
			//yuml  e   20141028
			endDate.addData("MR_NO", mrNo);
			endDate.addData("PAT_NAME", patName);
			endDate.addData("HIGHRISKMATERNAL_FLG", maternalDesc);
			endDate.addData("CLINICTYPE_DESC", clinicDesc);
			endDate.addData("DEPT_ABS_DESC", deptDesc);
			endDate.addData("USER_NAME", useName);
			endDate.addData("QUE_NO", queNo);
			endDate.addData("CTZ_DESC", ctzDesc);
			//yuml  s   20141028
//			endDate.addData("AR_AMT", df.format(ar_amt));
//			endDate.addData("DR_CODE", drCode);
            //yuml  e   20141028
		}
		endDate.setCount(count);
		// =============pangben modify 20110408 start
		endDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN"); // fuxin modify
																// 20120306
		// =============pangben modify 20110408 stop
		endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		//yuml  s   20141028
		endDate.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
		endDate.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
		//yuml  e   20141028
		endDate.addData("SYSTEM", "COLUMNS", "MR_NO");
		endDate.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "HIGHRISKMATERNAL_FLG");
		endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "QUE_NO");
		endDate.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		//yuml  s   20141028
//		endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
//		endDate.addData("SYSTEM", "COLUMNS", "DR_CODE");
		// ==========pangben modify 20110425 start
		selParm.setData("REGION_CHN_ABN", count, "");
		selParm.setData("ADM_DATE", count, "总计:");
		selParm.setData("ADM_TYPE", count, count+"人");
		selParm.setData("ICD_CHN_DESC", count, "");
//		selParm.setData("CASE_NO", count, "");
        //yuml  e   20141028
		selParm.setData("MR_NO", count, "");
		selParm.setData("PAT_NAME", count, "");
		selParm.setData("HIGHRISKMATERNAL_FLG", count, "");
		selParm.setData("CLINICTYPE_DESC", count, "");
		selParm.setData("DEPT_ABS_DESC", count, "");
		selParm.setData("USER_NAME", count, "");
		selParm.setData("QUE_NO", count, "");
		selParm.setData("CTZ_DESC", count, "");
		//yuml  s   20141028
//		selParm.setData("AR_AMT", count, df.format(sumAramt));
//		selParm.setData("DR_CODE", count, "");
        //yuml  e   20141028
		// ==========pangben modify 20110425 start
		this.callFunction("UI|Table|setParmValue", selParm);
		return endDate;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		TParm printData = this.getPrintDate(startTime, endTime);
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
		table.removeRowAll();

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
	 * 读医疗卡
	 */
	public void onReadEKT() {
		// 读取医疗卡
		TParm parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		String mrNo = parmEKT.getValue("MR_NO");
		this.setValue("MR_NO", mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		setValue("PAT_NAME", pat.getName());
		onQuery();
	}

	// /**
	// * 调用留观病历
	// */
	// public void onErdSheet() {
	// TTable table = (TTable)this.getComponent("Table");
	// int selRow = table.getSelectedRow();
	// if (selRow < 0) {
	// this.messageBox("请点选行数据!");
	// return;
	// }
	// TParm tableParm = table.getParmValue();
	// TParm parm = new TParm();
	// if ("O".equalsIgnoreCase(tableParm.getValue("ADM_TYPE", selRow))) {
	// parm.setData("SYSTEM_TYPE", "ODO");
	// parm.setData("ADM_TYPE", "O");
	// } else {
	// parm.setData("SYSTEM_TYPE", "EMG");
	// parm.setData("ADM_TYPE", "E");
	// }
	// parm.setData("CASE_NO", tableParm.getValue("CASE_NO", selRow));
	// parm.setData("PAT_NAME", tableParm.getValue("PAT_NAME", selRow));
	// parm.setData("MR_NO", tableParm.getValue("MR_NO", selRow));
	// parm.setData("IPD_NO", "");
	// parm.setData("ADM_DATE", Timestamp.valueOf(tableParm.getValue("ADM_DATE",
	// selRow)));
	// parm.setData("DEPT_CODE", tableParm.getValue("DEPT_ABS_DESC", selRow));
	// // parm.setData("STYLETYPE","1");
	// parm.setData("RULETYPE", "2");
	// parm.setData("EMR_DATA_LIST", new TParm());
	// this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	// }

	/**
	 * 打印病历
	 * 
	 * @return Object
	 */
	public void onErdSheet() {
		TParm parm = new TParm();
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
			return;
		}
		TParm tableParm = table.getParmValue();
		// 添加 SUBCLASSCODE 主诉的
		parm.setData("SUBCLASSCODE_ZHUSU", TConfig
				.getSystemValue("ODOEmrTempletZSSUBCLASSCODE"));
		//报表页眉上的病人信息  huangjunwen add 2014611
		Pat pat = Pat.onQueryByMrNo(tableParm.getValue("MR_NO", selRow));
	    parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT",tableParm.getValue("MR_NO", selRow));
	    parm.setData("filePatName","TEXT",pat.getName());
	    parm.setData("fileSex","TEXT",pat.getSexString());
	    String birthdate;
	    if(!"".equals(pat.getBirthday()))
	    	birthdate=pat.getBirthday().toString().substring(0,10).replace('-', '/');
	    else
	    	birthdate="";
	    parm.setData("fileBirthday","TEXT",birthdate);
	    parm.setData("FILE_HEAD_TITLE_IPD_NO","TEXT",pat.getIpdNo());
	    //add by huangjw
		
		parm.setData("CASE_NO", tableParm.getValue("CASE_NO", selRow));
		parm.setData("MR_NO", tableParm.getValue("MR_NO", selRow));
		parm.setData("MR", "TEXT", "病案号：" + tableParm.getValue("MR_NO", selRow));
		// if (isEng) {
		// parm
		// .setData("HOSP_NAME", "TEXT", Operator
		// .getHospitalENGFullName());
		// } else {
		parm.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
		// }
		parm.setData(
				"DR_NAME",
				"TEXT",
				"医师签字:"
						+ OpdRxSheetTool.getInstance().GetRealRegDr(
								tableParm.getValue("CASE_NO", selRow)));
		parm.setData("REALDEPT_CODE",
				tableParm.getValue("REALDEPT_CODE", selRow));
		if(getAllegeParm(tableParm.getValue("MR_NO", selRow))<=0){//判断过敏史是否为空 add by huangjw 20150204
	    	parm.setData("ALLEGE","TEXT","-");
	    }
		if(this.getDataCount("5",tableParm.getValue("CASE_NO", selRow))<=0){
	    	parm.setData("data1","-");//判断检验检查是否为空 add by huangjw 20150204
	    }
	    if(this.getDataCount("4",tableParm.getValue("CASE_NO", selRow))<=0){
	    	parm.setData("data2","-");//判断处置是否为空 add by huangjw 20150204
	    }
	    if((this.getDataCount("1",tableParm.getValue("CASE_NO", selRow))+this.getDataCount("2",tableParm.getValue("CASE_NO", selRow)))<=0){
	    	parm.setData("data3","-");//判断药品时候为空 add by huangjw 20150204
	    }
	    if(OpdRxSheetTool.getInstance().getPastHistory(tableParm.getValue("MR_NO", selRow)).equals("")){//判断既往史是否为空
	    	parm.setData("pastHistory","-");
	    }else{
	    	parm.setData("pastHistory",OpdRxSheetTool.getInstance().getPastHistory(tableParm.getValue("MR_NO", selRow)));
	    }
	    if(OpdRxSheetTool.getInstance().getRelatedHistory(tableParm.getValue("MR_NO", selRow)).equals("")){//判断家族史是否为空
	    	parm.setData("familyHistory","-");
	    }else{
	    	parm.setData("familyHistory",OpdRxSheetTool.getInstance().getRelatedHistory(tableParm.getValue("MR_NO", selRow)));
	    }
	    // add by wangb 2017/10/13 病历中增加手术输血史
		String opBloodHistory = OpdRxSheetTool.getInstance().getOpBloodHistory(
				tableParm.getValue("MR_NO", selRow));
		if (StringUtils.isEmpty(opBloodHistory)) {
			parm.setData("opBloodHistory", "-");
		} else {
			parm.setData("opBloodHistory", opBloodHistory);
		}
	    
	    if(getDiagcount(tableParm.getValue("CASE_NO", selRow))<=0){//判断诊断是否为空
	    	parm.setData("data4","-");
	    }
		Object obj = new Object();
		if ("O".equals(tableParm.getValue("ADM_TYPE", selRow))) {
			obj = this.openPrintDialog(
					"%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010_V45.jhw", parm,
					false);
			// 加入EMR保存 beign
			// this.saveEMR(obj, "门诊病历记录", "EMR020001", "EMR02000106");
			// 加入EMR保存 end
		} else if ("E".equals(tableParm.getValue("ADM_TYPE", selRow))) {
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\OPD\\EMG_V45.jhw",
					parm, false);

		}

	}
	/**
	 * 过敏史数据
	 * @param mrno
	 * @return
	 */
	public int getAllegeParm(String mrno){
		String sql=" "
			+" SELECT    SUBSTR (OPD_DRUGALLERGY.ADM_DATE, 0, 4)|| '/'"
			+" || SUBSTR (OPD_DRUGALLERGY.ADM_DATE, 5, 2)|| '/'"
			+" || SUBSTR (OPD_DRUGALLERGY.ADM_DATE, 7, 2) AS ADM_DATE,"
			+" (SELECT CHN_DESC FROM SYS_DICTIONARY"
			+" WHERE GROUP_ID = 'SYS_ALLERGY' AND ID = OPD_DRUGALLERGY.DRUG_TYPE) AS DRUG_TYPE,"
			+" (SELECT ORDER_DESC FROM SYS_FEE"
			+" WHERE ORDER_CODE = OPD_DRUGALLERGY.DRUGORINGRD_CODE)|| (SELECT CHN_DESC"
			+"  FROM SYS_DICTIONARY WHERE     GROUP_ID = 'PHA_INGREDIENT'"
			+" AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE) AS ORDER_DESC,"
			+" OPD_DRUGALLERGY.ALLERGY_NOTE"
			+" FROM OPD_DRUGALLERGY, SYS_DEPT, SYS_OPERATOR"
			+" WHERE     OPD_DRUGALLERGY.MR_NO = '"+mrno+"'"
			+" AND OPD_DRUGALLERGY.DEPT_CODE = SYS_DEPT.DEPT_CODE"
			+" AND OPD_DRUGALLERGY.DR_CODE = SYS_OPERATOR.USER_ID";
		System.out.println("sql:::"+sql);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		int count=parm.getCount();
		return count;
	}
	/**
	 * 医嘱数据
	 * @param rx_type
	 * @param caseno
	 * @return
	 */
	public  int getDataCount(String rx_type,String caseno){
		int i=0;
		String sql="SELECT OPD_ORDER.ORDER_DESC || '  ' || SYS_FEE.TRADE_ENG_DESC, "+
                   "TO_CHAR (OPD_ORDER.ORDER_DATE, 'mm/dd hh24:mi')|| ' '|| SYS_OPERATOR.USER_NAME "+
                   "FROM OPD_ORDER, SYS_FEE, SYS_OPERATOR "+
                   "WHERE     CASE_NO = '"+caseno+"' "+
                   "AND RX_TYPE = '"+rx_type+"' "+
                   "AND SYS_FEE.ORDER_CODE = OPD_ORDER.ORDER_CODE "+
                   "AND SYS_OPERATOR.USER_ID = OPD_ORDER.DR_CODE "+
                   "AND (   OPD_ORDER.ORDERSET_CODE IS NULL OR OPD_ORDER.SETMAIN_FLG = 'Y') "+
                   "ORDER BY rx_no, link_no, seq_no ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		i=parm.getCount();
		return i;
	}
	
	/**
	 * 诊断 数据
	 * @param caseNo
	 * @return
	 */
	public int getDiagcount(String caseNo){
		int i=0;
		String sql="SELECT CASE WHEN OPD_DIAGREC.MAIN_DIAG_FLG = 'Y' THEN '主' ELSE '' END || '',"+
			" SYS_DIAGNOSIS.ICD_CHN_DESC || '  ' || OPD_DIAGREC.DIAG_NOTE || '   '"+
			" FROM OPD_DIAGREC, SYS_DIAGNOSIS"+
			" WHERE     CASE_NO = '"+caseNo+"'"+
			" AND SYS_DIAGNOSIS.ICD_CODE = OPD_DIAGREC.ICD_CODE";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			i=parm.getCount();
		}
		return i;
	}
	/**
	 * 处方签打印
	 * ======zhangp 20121210
	 */
	public void onCaseSheet(){
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
			return;
		}
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getValue("CASE_NO", selRow);
		String mrNo = tableParm.getValue("MR_NO", selRow);
		String deptCode = tableParm.getValue("REALDEPT_CODE", selRow);
		String drCode = tableParm.getValue("DR_CODE", selRow);
		String admType = tableParm.getValue("ADM_TYPE", selRow);
		Timestamp admDate = tableParm.getTimestamp("ADM_DATE", selRow);
		ODO odo = new ODO(caseNo, mrNo, deptCode, drCode, admType);
		odo.onQuery();
		pat = Pat.onQueryByMrNo(mrNo);
		TParm inParm = new TParm();
		if (odo == null || pat == null)
			return;
		inParm.setData("MR_NO", odo.getMrNo());
		inParm.setData("CASE_NO", odo.getCaseNo());
		inParm.setData("DEPT_CODE", deptCode);
		if ("en".equals(this.getLanguage())) // 判断是否是英文界面
			inParm.setData("PAT_NAME", pat.getName1());
		else
			inParm.setData("PAT_NAME", pat.getName());
		inParm.setData("OPD_ORDER", odo.getOpdOrder());
		inParm.setData("ADM_DATE", admDate);
		inParm.setData("ODO", odo);
		int[] mainDiag = new int[1];
		if (odo.getDiagrec().haveMainDiag(mainDiag)) {
			String icdCode = odo.getDiagrec().getItemString(mainDiag[0],
					"ICD_CODE");
			inParm.setData("ICD_CODE", icdCode);
			inParm.setData("ICD_DESC", odo.getDiagrec().getIcdDesc(icdCode,
					this.getLanguage()));
		}
		this.openDialog("%ROOT%\\config\\opd\\ODOCaseSheet.x", inParm, false);
	}
	
	/**
	 * 门诊病案借阅
	 * 
	 * @author wangbin 20140919
	 */
	public void onLend() {
		// 获取选中行
		TTable table = (TTable) this.getComponent("Table");
		int rowIndex = table.getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("请选择一份病历！");
			return;
		}
		
		TParm data = table.getParmValue();
		TParm parm = new TParm();
		
		parm.setData("ADM_TYPE", "O");
		parm.setData("MR_NO", data.getValue("MR_NO", rowIndex));
		parm.setData("IPD_NO", "");
		parm.setData("CASE_NO", data.getValue("CASE_NO", rowIndex));
		parm.setData("PAT_NAME", data.getValue("PAT_NAME", rowIndex));// 患者姓名
		this.openDialog("%ROOT%\\config\\mro\\MROLendReg.x", parm);
	}
	/**
	 * 写病历
	 */
	public void onAddEmrWrite(){

		   TTable table = (TTable) this.getComponent("Table");
		   int row = table.getSelectedRow();
		   
		   TParm tableParm = table.getParmValue();
		   String mrNo = tableParm.getValue("MR_NO",row);
		   if(mrNo == null || "".equals(mrNo)){
			   this.messageBox("请选择病患") ;
			   return   ;   
		   }
		   TParm parm = new TParm();
		   String admType = tableParm.getValue("ADM_TYPE",row);
		  
			parm.setData("ADM_TYPE_ZYZ", admType);
			if ("O".equalsIgnoreCase(admType)) {
				parm.setData("SYSTEM_TYPE", "ODO");
				parm.setData("ADM_TYPE", "O");
			} else {
				parm.setData("SYSTEM_TYPE", "EMG");
				parm.setData("ADM_TYPE", "E");
			}
			parm.setData("CASE_NO", tableParm.getValue("CASE_NO",row));
			parm.setData("PAT_NAME", tableParm.getValue("PAT_NAME",row));
			parm.setData("MR_NO", mrNo);
			parm.setData("IPD_NO", "");
			parm.setData("ADM_DATE", tableParm.getTimestamp("ADM_DATE",row));
			parm.setData("DEPT_CODE", Operator.getDept());
			parm.setData("RULETYPE", "2");
			parm.setData("EMR_DATA_LIST", new TParm());
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		   
	}
}
