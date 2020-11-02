package com.javahis.ui.adm;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.DictionaryTool;
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
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>
 * Title:预约管理
 * </p>
 * 
 * 
 * <p>
 * Description: 住院预约管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20140730
 * @version 4.5
 */
public class ADMResvInfoQueryControl extends TControl {
	private TTable table;
	private TParm lumpworkParm;
	DecimalFormat formatObject = new DecimalFormat("###########0.00");
	/**
	 * 初始化方法
	 */
	public void onInit() {
		initPage();
		// 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		String lumpworkSql=" SELECT LUMPWORK_CODE, LUMPWORK_DESC AS NAME,FEE FROM MEM_LUMPWORK ";
		lumpworkParm=new  TParm(TJDODBTool.getInstance().select(lumpworkSql));
		dateEnable(true);
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		table.removeRowAll();
		String date_s="";
		String date_e="";
		String tableDate="";
		if(((TRadioButton)this.getComponent("RTO_S")).isSelected()){
			date_s = getValueString("DATE_S");
			date_e = getValueString("DATE_E");
			tableDate="APP_DATE";
		}else{
			date_s = getValueString("IN_DATE_S");
			date_e = getValueString("IN_DATE_E");
			tableDate="RESV_DATE";
		}
		String whereDate="";
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			if (this.getValue("MR_NO").toString().length()<=0) {
				this.messageBox("请输入病案号");
				return;
			}
		}else{
			date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
			date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
			whereDate=" AND A."+tableDate+" BETWEEN TO_DATE ('"+ date_s+ "', 'YYYYMMDDHH24MISS')"+ " AND TO_DATE ('"+ date_e
			+ "', 'YYYYMMDDHH24MISS')";
		}

		String sql = " SELECT   A.MR_NO, B.PAT_NAME, A.APP_DATE, A.RESV_DATE, A.ADM_DAYS, A.URG_FLG, "
				+ " CASE WHEN A.CAN_CLERK IS NOT NULL THEN 'Y' ELSE 'N' END CAN_CLERK,B.BIRTH_DATE, "
				+ // add caoyong A.CAN_CLERK 2014/4/29
				" A.ADM_SOURCE,'' CLERK , A.OPD_DEPT_CODE, A.OPD_DR_CODE, A.DEPT_CODE,A.RESV_NO,B.SEX_CODE AS SEX,C.IPD_NO,"
				+ " A.STATION_CODE, A.DR_CODE,CASE WHEN C.CASE_NO IS NOT NULL THEN 'Y' ELSE 'N' END ADM_FLG,A.LUMPWORK_CODE,'0' LUMPWORK_AMT ,A.LUMPWORK_CASE_NO,C.CASE_NO,C.CANCEL_FLG,C.DS_DATE "
				+ " FROM ADM_RESV A,ADM_INP C, SYS_PATINFO B"
				+ " WHERE A.IN_CASE_NO=C.CASE_NO(+) AND A.MR_NO = B.MR_NO "+whereDate;
				
		if (null != this.getValueString("URG_FLG")
				&& this.getValueString("URG_FLG").length() > 0) {
			sql += " AND A.URG_FLG = '" + getValueString("URG_FLG") + "'";
		}
		if (null != this.getValueString("ADM_SOURCE")
				&& this.getValueString("ADM_SOURCE").length() > 0) {
			sql += " AND A.ADM_SOURCE = '" + getValueString("ADM_SOURCE") + "'";
		}
		if (null != this.getValueString("LUMPWORK_CODE")
				&& this.getValueString("LUMPWORK_CODE").length() > 0) {
			sql += " AND A.LUMPWORK_CODE = '" + getValueString("LUMPWORK_CODE") + "'";
		}
		if (null != this.getValueString("OPD_DEPT_CODE")
				&& this.getValueString("OPD_DEPT_CODE").length() > 0) {
			sql += " AND A.OPD_DEPT_CODE = '" + getValueString("OPD_DEPT_CODE")
					+ "'";
		}
		if (null != this.getValueString("OPD_DR_CODE")
				&& this.getValueString("OPD_DR_CODE").length() > 0) {
			sql += " AND A.OPD_DR_CODE = '" + getValueString("OPD_DR_CODE")
					+ "'";
		}
		if (null != this.getValueString("DEPT_CODE")
				&& this.getValueString("DEPT_CODE").length() > 0) {
			sql += " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		}
		if (null != this.getValueString("STATION_CODE")
				&& this.getValueString("STATION_CODE").length() > 0) {
			sql += " AND A.STATION_CODE = '" + getValueString("STATION_CODE")
					+ "'";
		}
		if (null != this.getValueString("DR_CODE")
				&& this.getValueString("DR_CODE").length() > 0) {
			sql += " AND A.DR_CODE = '" + getValueString("DR_CODE") + "'";
		}
		if (null != this.getValueString("MR_NO")
				&& this.getValueString("MR_NO").length() > 0) {
			sql += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
		}
		// ------ add caoyong 2014/4/29----start
		if (null != this.getValueString("CAN_CLERK")
				&& this.getValueString("CAN_CLERK").length() > 0) {
			if ("0".equals(this.getValueString("CAN_CLERK"))) {
				sql += " AND A.CAN_CLERK IS NOT NULL";
			} else if ("1".equals(this.getValueString("CAN_CLERK"))) {
				sql += " AND A.CAN_CLERK IS  NULL AND A.IN_CASE_NO IS NULL ";
			}else if ("2".equals(this.getValueString("CAN_CLERK"))) {
				sql += " AND A.CAN_CLERK IS  NULL AND A.IN_CASE_NO IS NOT NULL AND C.CANCEL_FLG !='Y' AND C.DS_DATE IS  NULL";
			}else if ("3".equals(this.getValueString("CAN_CLERK"))) {//==liling 20140804 add 取消住院
				sql += " AND A.CAN_CLERK IS  NULL AND A.IN_CASE_NO IS NOT NULL AND C.CANCEL_FLG='Y' ";
			}else if ("4".equals(this.getValueString("CAN_CLERK"))) {//==liling 20140804 add 已出院
				sql += " AND A.CAN_CLERK IS  NULL AND A.IN_CASE_NO IS NOT NULL AND C.DS_DATE IS NOT NULL ";
			}
			// ------ add caoyong 2014/4/29----end
		}
		// " AND A.RESV_DATE BETWEEN TO_DATE ('', 'YYYYMMDDHH24MISS')" +
		// " AND TO_DATE ('', 'YYYYMMDDHH24MISS')" +
		sql += " ORDER BY A.LUMPWORK_CODE,A.APP_DATE DESC," + " A.RESV_DATE DESC," + " A.URG_FLG,"
				+ " A.ADM_SOURCE," + " A.OPD_DEPT_CODE," + " A.OPD_DR_CODE,"
				+ " A.DEPT_CODE," + " A.STATION_CODE," + " A.DR_CODE,"
				+ " A.MR_NO";
//		System.out.println("======++++==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			this.messageBox("没有查询数据");
			table.removeRowAll();
			return;
		}
		
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getValue("CAN_CLERK",i).equals("Y")) {//取消预约
				result.setData("CLERK",i,"取消预约");
			}else{//未取消
				if (result.getValue("ADM_FLG",i).equals("Y")) {//办理住院
					if (result.getValue("CANCEL_FLG",i).equals("Y")) {//取消住院
						result.setData("CLERK",i,"取消住院");
					}else if(null ==result.getValue("DS_DATE",i)||"".equals(result.getValue("DS_DATE",i))){
							result.setData("CLERK",i,"已住院");
						  }else {
							  result.setData("CLERK",i,"已出院");	
							  }
				}else{//未办理
					result.setData("CLERK",i,"未住院");
				}
			}
			if (result.getValue("LUMPWORK_CODE",i).length()>0) {//查询包干套餐金额
				for (int j = 0; j < lumpworkParm.getCount(); j++) {
					if (result.getValue("LUMPWORK_CODE",i).equals(lumpworkParm.getValue("LUMPWORK_CODE",j))) {
						result.setData("LUMPWORK_AMT",i,formatObject.format(lumpworkParm.getDouble("FEE",j)));
						break;
					}
				}
			}		
		}	
		table.setParmValue(result);
	}

	/**
	 * 初始画面数据
	 */

	private void initPage() {
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTABLEClicked");// 单击事件
		// Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");
		this.setValue("REGION_CODE", Operator.getRegion());
	}

	/**
	 * 打印住院证
	 */
	public void onPrint() {
		String caseNo = "";
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择要打印的数据");
			return;
		}

		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (parm.getValue("CAN_CLERK").equals("Y")) {
			this.messageBox("已经取消住院,不可以打住院证");
			return;
		}
		if (parm.getValue("CASE_NO").length() > 0) {
			caseNo = parm.getValue("CASE_NO");
		} else if (parm.getValue("LUMPWORK_CASE_NO").length() > 0) {
			caseNo = parm.getValue("LUMPWORK_CASE_NO");
		}
//		if (caseNo.length() <= 0) {
//			this.messageBox("该病患未入院！");
//			return;
//		}
		String subClassCode = TConfig
				.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");

		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo
				+ "'";
		sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='"
				+ subClassCode + "'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		parm.setData("SEX", parm.getValue("SEX").length() > 0 ? DictionaryTool
				.getInstance().getSexName(parm.getValue("SEX")) : "");
		if (null != parm.getTimestamp("BIRTH_DATE")) {
			parm.setData("AGE", StringUtil.showAge(parm
							.getTimestamp("BIRTH_DATE"), parm
							.getTimestamp("APP_DATE"))); // 年龄
		} else {
			parm.setData("AGE", ""); // 年龄
		}
		if (result1.getCount() < 0) {
			this.onPrint1(parm, caseNo);
		} else {
			// 获取预约号
			String resvNo = parm.getValue("RESV_NO");
			String filePath = result1.getValue("FILE_PATH", 0);
			String fileName = result1.getValue("FILE_NAME", 0);

			// parm.setData("CASE_NO", caseNo);
			parm.setData("CASE_NO", resvNo);// duzhw add
			Timestamp ts = SystemTool.getInstance().getDate();
			parm.setData("ADM_TYPE", "O");
			parm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
			parm.setData("STATION_CODE", parm.getValue(
					"STATION_CODE"));
			parm.setData("ADM_DATE", ts);
			parm.setData("STYLETYPE", "1");
			parm.setData("RULETYPE", "3");
			parm.setData("SYSTEM_TYPE", "ODO");
			TParm emrFileData = new TParm();
			emrFileData.setData("FILE_PATH", filePath);
			emrFileData.setData("FILE_NAME", fileName);
			emrFileData.setData("FILE_SEQ", result1.getValue("FILE_SEQ", 0));
			emrFileData.setData("SUBCLASS_CODE", subClassCode);
			emrFileData.setData("CLASS_CODE", classCode);
			emrFileData.setData("FLG", true);

			parm.setData("EMR_FILE_DATA", emrFileData);
			this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		}
	}

	public void onPrint1(TParm parm, String caseNo) {

		// 获取预约号
		TParm myParm = new TParm();
		myParm.setData("CASE_NO", caseNo);
		TParm actionParm = new TParm();
		if (caseNo.length() > 0) {
			TParm casePrint = ADMInpTool.getInstance().selectall(myParm);
			actionParm.setData("DEPT_CODE", null!=casePrint.getValue("DEPT_CODE", 0)?
					casePrint.getValue("DEPT_CODE", 0):"");
			actionParm.setData("STATION_CODE",null!=casePrint.getValue(
					"STATION_CODE", 0)?casePrint.getValue(
							"STATION_CODE", 0):"");
		} else {
			actionParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
			actionParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
		}
		actionParm.setData("MR_NO", parm.getValue("MR_NO"));
		actionParm.setData("IPD_NO", parm.getValue("IPD_NO"));
		actionParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
		actionParm.setData("SEX", parm.getValue("SEX"));
		actionParm.setData("AGE", parm.getValue("AGE")); // 年龄
		Timestamp ts = SystemTool.getInstance().getDate();
		actionParm.setData("CASE_NO", parm.getValue("RESV_NO")); // duzhw add
		actionParm.setData("ADM_TYPE", "O");

		actionParm.setData("ADM_DATE", ts);
		actionParm.setData("STYLETYPE", "1");
		actionParm.setData("RULETYPE", "3");
		actionParm.setData("SYSTEM_TYPE", "ODO");
		TParm emrFileData = new TParm();
		String path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
		String fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
		String subClassCode = TConfig
				.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		emrFileData.setData("TEMPLET_PATH", path);
		emrFileData.setData("EMT_FILENAME", fileName);
		emrFileData.setData("SUBCLASS_CODE", subClassCode);
		emrFileData.setData("CLASS_CODE", classCode);
		actionParm.setData("EMR_FILE_DATA", emrFileData);
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("MR_NO;PAT_NAME;URG_FLG;ADM_SOURCE;OPD_DEPT_CODE;OPD_DR_CODE;DEPT_CODE;STATION_CODE;LUMPWORK_CODE;DR_CODE;DATE_S;DATE_E;IN_DATE_S;IN_DATE_E;CAN_CLERK");
		initPage();
		table.removeRowAll();
		((TRadioButton)this.getComponent("RTO_S")).setSelected(true);
		dateEnable(true);
	}

	/**
	 * 查询病案号
	 */
	public void onQueryMrno() {
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
		String mr_no = PatTool.getInstance().checkMrno(
				this.getValueString("MR_NO"));
		this.setValue("MR_NO", mr_no);
		setValue("PAT_NAME", pat.getName());
		this.onQuery();
	}

	public void onTABLEClicked(int row) {
		if (row < 0) {
			return;
		}
		TParm result = table.getParmValue().getRow(row);
		this.setValue("MR_NO", result.getValue("MR_NO"));
		this.setValue("PAT_NAME", result.getValue("PAT_NAME"));
		this.setValue("ADM_SOURCE", result.getValue("ADM_SOURCE"));
		this.setValue("OPD_DR_CODE", result.getValue("OPD_DR_CODE"));
		this.setValue("OPD_DEPT_CODE", result.getValue("OPD_DEPT_CODE"));
		this.setValue("DEPT_CODE", result.getValue("DEPT_CODE"));
		this.setValue("STATION_CODE", result.getValue("STATION_CODE"));
		this.setValue("DR_CODE", result.getValue("DR_CODE"));
		this.setValue("URG_FLG", result.getValue("URG_FLG"));
		//==liling 20140804 modify start====
//		this.setValue("CAN_CLERK","Y".equals(result.getValue("CAN_CLERK")) ? "0" : "1");
		if (result.getValue("CAN_CLERK").equals("Y")) {//取消预约
			this.setValue("CAN_CLERK","0");
		}else{//未取消
			if (result.getValue("ADM_FLG").equals("Y")) {//办理住院
				if (result.getValue("CANCEL_FLG").equals("Y")) {//取消住院
					this.setValue("CAN_CLERK","3");
				}else {
						if (null !=result.getValue("DS_DATE")) {//已出院
							this.setValue("CAN_CLERK","4");
						}else {
							this.setValue("CAN_CLERK","2");
							}
					}
				
			}else{//未办理
				this.setValue("CAN_CLERK","1");
			}
		}//==liling 20140804 modify end====
		this.setValue("LUMPWORK_CODE", result.getValue("LUMPWORK_CODE"));
	}
	 /**
     * 预交金
     */
    public void onBilpay() {
    	if (table.getSelectedRow()<0) {
    		this.messageBox("请选择病患");
			return;
		}
    	TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		if (parm.getValue("CAN_CLERK").equals("Y")) {
			this.messageBox("已经取消住院,不可以操作预交金");
			return;
		}
		String caseNo="";
		if (parm.getValue("CASE_NO").length() > 0) {
			caseNo = parm.getValue("CASE_NO");
		} else if (parm.getValue("LUMPWORK_CASE_NO").length() > 0) {
			caseNo = parm.getValue("LUMPWORK_CASE_NO");
		}
		TParm sendParm=new TParm();
        if (parm.getValue("LUMPWORK_CODE").length()>0) {//预约是否使用包干套餐
     		if (caseNo.length()<=0) {
     			sendParm.setData("RESV_NO",parm.getValue("RESV_NO"));
         		TParm ressvParm=ADMResvTool.getInstance().selectAll(sendParm);
         		if (ressvParm.getValue("LUMPWORK_CASE_NO",0).length()>0) {//查询是否存在套餐就诊号码
         			caseNo = ressvParm.getValue("LUMPWORK_CASE_NO",0);
         		}
         		sendParm.setData("CTZ1_CODE",ressvParm.getValue("CTZ1_CODE",0));
         		sendParm.setData("DEPT_CODE",ressvParm.getValue("DEPT_CODE",0));
         		sendParm.setData("STATION_CODE",ressvParm.getValue("STATION_CODE",0));
			}
		}else{
			if (parm.getValue("CASE_NO").length()<=0) {
				this.messageBox("请办理入院登记");
				return;
			}
		}
        sendParm.setData("CASE_NO", caseNo);
        //sendParm.setData("PRE_AMT",this.getValueString("TOTAL_BILPAY"));//add by sunqy 20140523
//        if(caseNo==null || "".equals(caseNo)){
//        	this.messageBox("请办理住院或预约住院使用套餐操作,再选择预交金!");
//        	return;
//        }
        //====pangben 包干套餐，可以操作
        if (null!=parm.getValue("LUMPWORK_CODE")
        		&&parm.getValue("LUMPWORK_CODE").length()>0) {
        	sendParm.setData("LUMPWORK_FLG","Y");
            sendParm.setData("PRE_AMT",parm.getDouble("LUMPWORK_AMT"));//套餐金额   
		}else{
			 sendParm.setData("PRE_AMT",0);//已经办理住院的病患执行正常充值操作
		}
        this.openWindow("%ROOT%\\config\\bil\\BILPay.x", sendParm);
    }
	public void onSel(){
		if(((TRadioButton)this.getComponent("RTO_S")).isSelected()){
			this.setValue("IN_DATE_S", "");
			this.setValue("IN_DATE_E", "");
			dateEnable(true);
		}else{
			this.setValue("DATE_S", "");
			this.setValue("DATE_E", "");
			dateEnable(false);
		}	
	}
	private void dateEnable(boolean flg){
		this.callFunction("UI|DATE_S|setEnabled", flg);
		this.callFunction("UI|DATE_E|setEnabled", flg);
		this.callFunction("UI|IN_DATE_S|setEnabled", !flg);
		this.callFunction("UI|IN_DATE_E|setEnabled", !flg);
		
	}
}
