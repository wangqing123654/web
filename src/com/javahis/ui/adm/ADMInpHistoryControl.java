package com.javahis.ui.adm;

import java.sql.Timestamp;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.DateUtil;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * 历史住院证打印
 *
 */
public class ADMInpHistoryControl extends TControl {

	TParm acceptData = new TParm(); // 接参

	TTable table;

	Pat pat;

	public void onInit() {
		init();
		//
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
		}
		this.queryPat(acceptData.getValue("MR_NO"));
		this.setValue("MR_NO", acceptData.getData("MR_NO"));
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("SEX_CODE1", pat.getSexCode());
		//
		// 查询历史就诊数据
		String sql = "SELECT A.CASE_NO, A.ADM_DATE FROM ADM_INP A WHERE A.MR_NO ='" + pat.getMrNo()
				+ "' AND A.CANCEL_FLG IS NULL OR A.CANCEL_FLG = 'N' ORDER BY CASE_NO DESC";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		table = (TTable) this.getComponent("TABLE");
		table.setParmValue(result1);
	}

	/**
	 * 查询病患信息
	 */
	public void queryPat(String mrNo) {
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("E0081");
			return;
		}
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		this.print(table.getParmValue().getRow(table.getSelectedRow()).getValue("CASE_NO"));
	}

	/**
	 * 打印
	 */
	private void print(String caseNo) {
		String subClassCode = TConfig.getSystemValue("ADMEmrINHOSPSUBCLASSCODE");
		String classCode = TConfig.getSystemValue("ADMEmrINHOSPCLASSCODE");
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE CASE_NO='" + caseNo + "'";
		sql += " AND CLASS_CODE='" + classCode + "' AND  SUBCLASS_CODE='" + subClassCode + "'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		String path, fileName, seq;
		boolean flg;// “病历是否存在”标识
		if (result1.getCount() < 0) {
			path = TConfig.getSystemValue("ADMEmrINHOSPPATH");
			fileName = TConfig.getSystemValue("ADMEmrINHOSPFILENAME");
			seq = "";
			flg = false;
		} else {
			path = result1.getValue("FILE_PATH", 0);
			fileName = result1.getValue("FILE_NAME", 0);
			seq = result1.getValue("FILE_SEQ", 0);
			flg = true;
		}
		//
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// 查询在院病患的基本信息
		TParm casePrint = ADMInpTool.getInstance().queryCaseNo(parm);
		//
		TParm actionParm = new TParm();
		actionParm.setData("MR_NO", pat.getMrNo());
		actionParm.setData("IPD_NO", pat.getIpdNo());
		actionParm.setData("PAT_NAME", pat.getName());
		actionParm.setData("SEX", pat.getSexString());
		Timestamp sysDate = SystemTool.getInstance().getDate();
		actionParm.setData("AGE", DateUtil.showAge(pat.getBirthday(), sysDate)); // 年龄
		Timestamp ts = SystemTool.getInstance().getDate();
		actionParm.setData("CASE_NO", caseNo);
		actionParm.setData("ADM_TYPE", "O");
		actionParm.setData("DEPT_CODE", casePrint.getValue("DEPT_CODE", 0));
		actionParm.setData("STATION_CODE", casePrint.getValue("STATION_CODE", 0));
		actionParm.setData("ADM_DATE", ts);// 打印日期
		actionParm.setData("STYLETYPE", "1");
		actionParm.setData("RULETYPE", "3");
		actionParm.setData("SYSTEM_TYPE", "ODO");
		//
		TParm emrFileData = new TParm();
		emrFileData.setData("TEMPLET_PATH", path);
		emrFileData.setData("EMT_FILENAME", fileName);
		emrFileData.setData("FILE_PATH", path);
		emrFileData.setData("FILE_NAME", fileName);
		emrFileData.setData("SUBCLASS_CODE", subClassCode);
		emrFileData.setData("CLASS_CODE", classCode);
		emrFileData.setData("FILE_SEQ", seq);
		emrFileData.setData("FLG", flg);
		//
		actionParm.setData("EMR_FILE_DATA", emrFileData);
		//
		sql = "SELECT A.IN_CASE_NO, A.RESV_NO FROM ADM_RESV A WHERE A.IN_CASE_NO = '"+caseNo+"'";
		result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		actionParm.setData("RESV_NO", result1.getValue("RESV_NO", 0));// 预约号
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", actionParm);
	}

}
