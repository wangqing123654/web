package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;

import jdo.sys.Operator;
import jdo.mro.MROInfectTool;
import jdo.mro.MROPrintTool;
import jdo.sys.SYSDiagnosisTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TPopupMenuEvent;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 * <p>
 * Title: 传染病报告卡
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: Javahis
 * </p>
 *
 * @author zhangk 2009-10-12
 * @version 1.0
 */
public class MROInfectControl extends TControl {
	private String MR_NO = "";
	private String CASE_NO = "";
	private String CARD_SEQ_NO = "";
	private String IPD_NO = "";
	private String CARD_NO = "";
	private String SAVE_FLG = "NEW";// NEW:新建 UPDATE:修改
	private String OPEN_TYPE = "";// 调用类型 如果是被医生站调用的 OPEN_TYPE="DR"
	private String ADM_TYPE = "";// 门急住别

	/**
	 * 初始化
	 */
	public void onInit() {
		// //只有text有这个方法，调用ICD10弹出框
		// callFunction("UI|ICD_CODE|setPopupMenuParameter", "aaa",
		// "%ROOT%\\config\\sys\\SYSICDPopup.x");
		// //textfield接受回传值
		// callFunction("UI|ICD_CODE|addEventListener",
		// TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		pageInit();
	}

	/**
	 * 页面初始化
	 */
	private void pageInit() {
		/**
		 * 调用参数 MR_NO CASE_NO ICD_CODE 诊断CODE DEPT_CODE 填报科室 USER_NAME 填报医师
		 */
		// TParm can = new TParm();
		// can.setData("MR_NO","000000000091");
		// can.setData("CASE_NO","123456789012");
		// can.setData("ICD_CODE","A00");
		// can.setData("DEPT_CODE","10101");
		// can.setData("USER_NAME","admin");
		// can.setData("ADM_TYPE","O");
		// Object obj = can;
		Object obj = this.getParameter();
		if (obj == null) {
			return;
		}
		//ILLNESS_DATE
		//COMFIRM_DATE
		Timestamp date = SystemTool.getInstance().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// 初始化查询区间
		this.setValue("ILLNESS_DATE", sdf.format(date));
		this.setValue("COMFIRM_DATE", sdf1.format(date));
		TParm parm = new TParm();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
			MR_NO = parm.getValue("MR_NO");
			CASE_NO = parm.getValue("CASE_NO");
			IPD_NO = parm.getValue("IPD_NO");
			ADM_TYPE = parm.getValue("ADM_TYPE");
			OPEN_TYPE = "DR";// 表示是被各个医生站调用的
			// 如果是医生站调用 隐藏查询按钮
			this.callFunction("UI|query|setVisible", false);
			hasInfectData("M");// 判断病患是否存在传染病卡记录
			if (MR_NO.length() <= 0) {
				this.closeWindow();
				return;
			}
			if ("NEW".equals(SAVE_FLG))// 新建数据的时候才会显示医生站传来的诊断CODE
			{
				this.setValue("FIRST_FLG", "0");
				// 取得最大的序号
				CARD_SEQ_NO = MROInfectTool.getInstance().getMaxSEQ(MR_NO, CASE_NO) + "";
				this.setValue("CARD_SEQ_NO", CARD_SEQ_NO);
				// String ICD_CODE = parm.getValue("ICD_CODE");
				// this.setValue("ICD_CODE", ICD_CODE);
				// TParm icd = SYSDiagnosisTool.getInstance().selectDataWithCode(ICD_CODE);
				// this.setValue("DISEASETYPE_CODE",icd.getValue("DISEASETYPE_CODE",0));
				// this.setValue("ICD_DESC",icd.getValue("ICD_CHN_DESC",0));
				// 初始化界面信息
				Pat pat = Pat.onQueryByMrNo(MR_NO);
				this.setValue("MR_NO", pat.getMrNo());
				this.callFunction("UI|MR_NO|setEnabled", false);
				this.setValue("PAT_NAME", pat.getName());// 姓名
				this.setValue("IDNO", pat.getIdNo());// 身份证
				this.setValue("BIRTH_DATE", pat.getBirthday());
				if (pat.getBirthday() != null) {
					String age[] = StringTool.CountAgeByTimestamp(pat.getBirthday(),
							SystemTool.getInstance().getDate());
					this.setValue("AGE", age[0] + "岁" + age[1] + "月" + age[2] + "日");
				} else {
					this.setValue("AGE", "");
				}
				this.setValue("SEX", pat.getSexCode());
				this.setValue("CONT_TEL", pat.getCellPhone());
				this.setValue("OFFICE", pat.getCompanyDesc());
				// this.setValue("PAD_DEPT",parm.getValue("DEPT_CODE"));//医生站传来的 填报科室
				this.setValue("SPEAKER", parm.getValue("USER_NAME"));// 
				this.setValue("PAD_DATE", SystemTool.getInstance().getDate());
			}
		}
	}

	/**
	 * 判断此病患 在本次看诊期间是否已经有传染病报告卡的数据 如果有 弹出查询界面 可以选择原有的报告卡数据进行修改 没有 则新建传染病报告卡
	 * 
	 * @param TYPE
	 *            String 调用类型 H:“历史查询”按钮调用 M:当指定病患存在多条传染病记录时自动调用
	 */
	private void hasInfectData(String TYPE) {
		boolean flg;
		// 如果是各个医生站调用的 那么查询指定患者的历史信息
		// if("DR".equals(OPEN_TYPE)){ 现传染病报告卡不由住院医生填写，暂时先注释掉条件
		TParm check = new TParm();
		check.setData("MR_NO", MR_NO);
		if (CASE_NO.trim().length() > 0) {
			check.setData("CASE_NO", CASE_NO);
		}
		flg = MROInfectTool.getInstance().checkHasInfect(check);
		// }else//不是医生站调用 可以查询所有病患的所有传染病报告卡的信息
		// flg = true;
		this.setValue("FIRST_FLG", "0");// 报卡类别默认为初次报卡
		if (flg) {// 存在历史信息
			// 调用历史查询界面
			TParm parm = new TParm();
			parm.setData("MR_NO", MR_NO);
			if (CASE_NO.trim().length() > 0)
				parm.setData("CASE_NO", CASE_NO);
			parm.setData("TYPE", TYPE);// 标识调用状态 当患者本次就诊已经存在填写过报告调用
			Object obj = this.openDialog("%ROOT%/config/mro/MROInfectQuery.x", parm);
			if (obj == null) {
				// 如果返回空值 那么清空界面
				this.onClear();
				return;
			}
			TParm reParm = (TParm) obj;
			SAVE_FLG = reParm.getValue("SAVE_FLG");
			if ("UPDATE".equals(SAVE_FLG)) {// 修改原有数据
				CARD_SEQ_NO = reParm.getValue("CARD_SEQ_NO");
				MR_NO = reParm.getValue("MR_NO");
				CASE_NO = reParm.getValue("CASE_NO");

				TParm selectParm = new TParm();
				selectParm.setData("MR_NO", MR_NO);
				selectParm.setData("CASE_NO", CASE_NO);
				selectParm.setData("CARD_SEQ_NO", CARD_SEQ_NO);
				TParm result = MROInfectTool.getInstance().selectInfect(selectParm);
				this.setDataValue(result);
				this.callFunction("UI|MR_NO|setEnabled", false);
				this.setValue("FIRST_FLG", "1");// 如果是修改 那么报卡类别默认为 修正
			}
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("MR_NO;PAT_NAME;GENEARCH_NAME;FIRST_FLG;CARD_NO;CARD_SEQ_NO;IDNO;BIRTH_DATE");
		this.clearValue("AGE;SEX;CONT_TEL;PAD_DATE;OFFICE;ADDRESS_PROVICE;ADDRESS_COUNTRY;ADDRESS_ROAD");
		this.clearValue("ADDRESS_THORP;DOORPLATE;ILLNESS_DATE;DEAD_DATE;COMFIRM_DATE");// ;ICD_CODE;ICD_DESC;DISEASETYPE_CODE
		this.clearValue("REST_PROF;REMARK;REVISALILLNESS_NAME;COUNTERMAND_REAS;REPORT_UNIT;CONT_TEL2;SPEAKER");// REST_INFECTION;PAD_DEPT
		//add by yanglu 20190426 增加 REMAIN_IN_HOSPITAL 留观病例 字段
		this.clearValue("DOUBT_CASE;CLINIC_DIAGNOSE;LAB_DIAGNOSE;PATHOGENY_SCHLEP;PLAGUE_SPOT;CHOLERA_FLG;");
		this.clearValue(
				"SARS_FLG;AIDS_FLG;POLIOMYELITIS_FLG;HIGH_FLU;EPIDEMIC_CEPHALITIS;DENGUE;LYSSA;EPIDEMIC_HEPATITIS");
		this.clearValue(
				"HIVES_FLG;EPIDEMIC_BLOOD;SCARLATINA;BRUCE_DISEASE;GONORRHEA;CHINCOUGH;DIPHTHERIA;NEW_LOCKJAW;CATCH_LEPTOSPIRA;SCHISTOSOMIASIS_FLG");
		this.clearValue(
				"GRIPPE_FLG;MUMPS;MEASLES;ACUTE_CONJUNCTIVITIS;LEPRA;SHIP_FEVER;KALA_AZAR;ECHINOCOCCOSIS;FILARIASIS;EXPECT_CHOLERA");
		this.clearValue(
				"H_ADDRESS_PROVICE;H_ADDRESS_COUNTRY;H_ADDRESS_ROAD;H_ADDRESS_THORP;H_DOORPLATE;POSITIVE_TEST;HIV;FH1N1;HFMDIS");// add
																																	// by
																																	// wanglong
													//add by yanglu 20190426  增加EMPHASIS_OTHER字段 																				// 20140307
		this.clearValue(
				"NGU;CONCA;GENHER;VARICELLA;AKAMUSHI;GCTI;HEPATIC;ENCEPHALITIS;PLEURISY;HGDISEASE;HGA;UNPNEUMONIA;UNKNOWNCAUSE;THROMSYN;AFP;OTHER;EMPHASIS_OTHER");// add
																																					// by
																																					// wanglong
																																					// 20140307
		this.setValue("SICK_ZONE_0", "Y");
		this.setValue("INVALID_PROF_16", "Y");
		// add by wangqing 20171129
		this.clearValue("OBSERVATION_CASE;H7N9_FLU;MERS;EHF");

		// this.setValue("VIRUS_TYPE_2","Y");//没有此病
		// this.setValue("VIRUS_HEPATITIS_5","Y");//没有此病
		// this.setValue("TYPHOID_2","Y");//没有此病
		// this.setValue("CHARCOAL_3","Y");//没有此病
		// this.setValue("DIARRHEA_2","Y");//没有此病
		// this.setValue("PHTHISIC_4","Y");//没有此病
		// this.setValue("LUES_5","Y");//没有此病
		// this.setValue("AGUE_3","Y");//没有此病
		// ===================add by wanglong 20140307
		String radioBtnStr = "VIRUS_TYPE_0;VIRUS_HEPATITIS_0;TYPHOID_0;CHARCOAL_0;DIARRHEA_0;PHTHISIC_0;LUES_0;AGUE_0";
		String[] radioBtnName = radioBtnStr.split(";");
		for (int i = 0; i < radioBtnName.length; i++) {
			TRadioButton tmp = (TRadioButton) this.callFunction("UI|" + radioBtnName[i] + "|getThis");
			((ButtonGroup) tmp.callFunction("getButtonGroup", new Object[] { tmp.getGroup() })).clearSelection();
		}
		// ===================add end
		SAVE_FLG = "NEW";
		CASE_NO = "";
		MR_NO = "";
		CARD_SEQ_NO = "";
		CARD_NO = "";
		IPD_NO = "";
		OPEN_TYPE = "";
		ADM_TYPE = "";
		this.callFunction("UI|MR_NO|setEnabled", true);
	}

	/**
	 * 保存
	 */
	public void onSave() {
		if (!checkData()) {
			return;
		}
		String type = this.getValueString("FIRST_FLG");
		// 如果是初次报告 那么新增数据
		if ("0".equals(type)) {
			// 判断是否存在此条数据,存在则需要生成最大序号 防止主键重复
			if (checkNew()) {
				CARD_SEQ_NO = MROInfectTool.getInstance().getMaxSEQ(MR_NO, CASE_NO) + "";
				this.setValue("CARD_SEQ_NO", CARD_SEQ_NO);
			}
			insertData();
		} else {// 如果是订正报告 那么是修改数据
				// 判断是否存在此条数据,存在则可以修改
			if (checkNew()) {
				updateData();
			}

			else
				this.messageBox_("请将择报卡类别选择为“初次报卡”");
		}
	}

	/**
	 * 插入新的报告卡信息
	 */
	private void insertData() {
		// 如果CASE_NO不存在不可新增 只有当各个医生站调用的时候才会传参CASE_NO 直接调用报告卡页面时不能够新增
		if ("".equals(CASE_NO)) {
			return;
		}
		TParm parm = this.getDataValue();// 获取页面的数据
		TParm result = MROInfectTool.getInstance().insertInfect(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		SAVE_FLG = "UPDATE";
		this.messageBox("P0005");
	}

	/**
	 * 修改报告卡信息
	 */
	private void updateData() {
		TParm parm = this.getDataValue();// 获取页面的数据
		if(!isChinese(parm.getValue("SPEAKER"))) {
			String speaker = MROPrintTool.getInstance().getDesc("SYS_OPERATOR", "", "USER_NAME", "USER_ID",parm.getValue("SPEAKER"));
			parm.setData("SPEAKER",speaker);
		}
		TParm result = MROInfectTool.getInstance().updateInfect(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
	}

	/**
	 * 检查要修改的传染病报告信息是否存在
	 * 
	 * @return boolean
	 */
	private boolean checkNew() {
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("CARD_SEQ_NO", this.getValue("CARD_SEQ_NO"));
		TParm result = MROInfectTool.getInstance().selectInfect(parm);
		if (result.getCount() > 0) {
			return true;
		} else
			return false;
	}

	/**
	 * 病案号回车事件
	 */
	public void onMRNO() {
		SAVE_FLG = "NEW";
		Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
		if (pat == null) {
			this.messageBox_("查无此病患!");
			return;
		}
		MR_NO = pat.getMrNo();
		this.setValue("MR_NO", MR_NO);
		hasInfectData("H");
		if ("NEW".equals(SAVE_FLG)) {
			this.onClear();
		}
		// 2013-04-09 zhangh 添加输入病案号回车后自动带入信息功能
		this.setValue("MR_NO", pat.getMrNo());
		this.setValue("PAT_NAME", pat.getName());// 姓名
		this.setValue("IDNO", pat.getIdNo());// 身份证
		this.setValue("BIRTH_DATE", pat.getBirthday());
		if (pat.getBirthday() != null) {
			String age[] = StringTool.CountAgeByTimestamp(pat.getBirthday(), SystemTool.getInstance().getDate());
			this.setValue("AGE", age[0] + "岁" + age[1] + "月" + age[2] + "日");
		} else {
			this.setValue("AGE", "");
		}
		this.setValue("SEX", pat.getSexCode());
		if (null == pat.getCellPhone())
			this.setValue("CONT_TEL", pat.getCellPhone());
		else
			this.setValue("CONT_TEL", pat.getTelHome());
		this.setValue("OFFICE", pat.getCompanyDesc());
		this.setValue("PAD_DATE", SystemTool.getInstance().getDate());
		this.setValue("ADDRESS_PROVICE", pat.getPostCode().substring(0, 2));
		this.setValue("ADDRESS_COUNTRY", pat.getPostCode());
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		onMRNO();
	}

	/**
	 * 页面控件赋值
	 */
	private void setDataValue(TParm parm) {
		ADM_TYPE = parm.getValue("ADM_TYPE", 0);
		this.setValue("MR_NO", parm.getValue("MR_NO", 0));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
		this.setValue("GENEARCH_NAME", parm.getValue("GENEARCH_NAME", 0));
		this.setValue("FIRST_FLG", parm.getValue("FIRST_FLG", 0));
		this.setValue("CARD_NO", parm.getValue("CARD_NO", 0));
		this.setValue("CARD_SEQ_NO", parm.getValue("CARD_SEQ_NO", 0));
		this.setValue("IDNO", parm.getValue("IDNO", 0));
		this.setValue("BIRTH_DATE", parm.getTimestamp("BIRTH_DATE", 0));
		if (parm.getTimestamp("BIRTH_DATE", 0) != null) {
			String age[] = StringTool.CountAgeByTimestamp(parm.getTimestamp("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate());
			this.setValue("AGE", age[0] + "岁" + age[1] + "月" + age[2] + "日");
		} else {
			this.setValue("AGE", "");
		}
		this.setValue("SEX", parm.getValue("SEX", 0));
		this.setValue("CONT_TEL", parm.getValue("CONT_TEL", 0));
		this.setValue("PAD_DATE", parm.getTimestamp("PAD_DATE", 0));
		this.setValue("OFFICE", parm.getValue("OFFICE", 0));
		this.setValue("SICK_ZONE_" + parm.getValue("SICK_ZONE", 0), "Y");
		this.setValue("ADDRESS_PROVICE", parm.getValue("ADDRESS_PROVICE", 0));
		onADDRESS_PROVICE();// add by wanglong 20140307
		this.setValue("ADDRESS_COUNTRY", parm.getValue("ADDRESS_COUNTRY", 0));
		this.setValue("ADDRESS_ROAD", parm.getValue("ADDRESS_ROAD", 0));
		this.setValue("ADDRESS_THORP", parm.getValue("ADDRESS_THORP", 0));
		this.setValue("DOORPLATE", parm.getValue("DOORPLATE", 0));
		this.setValue("ILLNESS_DATE", parm.getTimestamp("ILLNESS_DATE", 0));
		this.setValue("DEAD_DATE", parm.getTimestamp("DEAD_DATE", 0));
		this.setValue("COMFIRM_DATE", parm.getTimestamp("COMFIRM_DATE", 0));
		this.setValue("INVALID_PROF_" + parm.getValue("INVALID_PROF", 0), "Y");
		this.setValue("REST_PROF", parm.getValue("REST_PROF", 0));
		this.setValue("DOUBT_CASE", parm.getValue("DOUBT_CASE", 0));
		this.setValue("CLINIC_DIAGNOSE", parm.getValue("CLINIC_DIAGNOSE", 0));
		this.setValue("LAB_DIAGNOSE", parm.getValue("LAB_DIAGNOSE", 0));
		this.setValue("PATHOGENY_SCHLEP", parm.getValue("PATHOGENY_SCHLEP", 0));
		
		//add by yanglu 20190426 增加 REMAIN_IN_HOSPITAL 留观病例 字段  begin
//		this.setValue("REMAIN_IN_HOSPITAL", parm.getValue("REMAIN_IN_HOSPITAL", 0));
		//add by yanglu 20190426 增加 REMAIN_IN_HOSPITAL 留观病例 字段  end

		// add by wangqing 20171129
		this.setValue("OBSERVATION_CASE", parm.getValue("OBSERVATION_CASE", 0));// 留观病例
		this.setValue("H7N9_FLU", parm.getValue("H7N9_FLU", 0));// 人感染H7N9禽流感
		this.setValue("MERS", parm.getValue("MERS", 0));// 中东呼吸综合征
		this.setValue("EHF", parm.getValue("EHF", 0));// 埃博拉出血热
		this.setValue("ZIKA_VIRUS", parm.getValue("ZIKA_VIRUS", 0));// 寨卡病毒病
		

		this.setValue("PLAGUE_SPOT", parm.getValue("PLAGUE_SPOT", 0));
		this.setValue("CHOLERA_FLG", parm.getValue("CHOLERA_FLG", 0));
		if (!parm.getValue("VIRUS_TYPE", 0).equals("")) {
			this.setValue("VIRUS_TYPE_" + parm.getValue("VIRUS_TYPE", 0), "Y");
		}
		this.setValue("SARS_FLG", parm.getValue("SARS_FLG", 0));
		this.setValue("AIDS_FLG", parm.getValue("AIDS_FLG", 0));
		if (!parm.getValue("VIRUS_HEPATITIS", 0).equals("")) {
			this.setValue("VIRUS_HEPATITIS_" + parm.getValue("VIRUS_HEPATITIS", 0), "Y");
		}
		this.setValue("POLIOMYELITIS_FLG", parm.getValue("POLIOMYELITIS_FLG", 0));
		this.setValue("HIGH_FLU", parm.getValue("HIGH_FLU", 0));
		if (!parm.getValue("TYPHOID", 0).equals("")) {
			this.setValue("TYPHOID_" + parm.getValue("TYPHOID", 0), "Y");
		}
		this.setValue("EPIDEMIC_CEPHALITIS", parm.getValue("EPIDEMIC_CEPHALITIS", 0));
		this.setValue("DENGUE", parm.getValue("DENGUE", 0));
		if (!parm.getValue("CHARCOAL", 0).equals("")) {
			this.setValue("CHARCOAL_" + parm.getValue("CHARCOAL", 0), "Y");
		}
		if (!parm.getValue("DIARRHEA", 0).equals("")) {
			this.setValue("DIARRHEA_" + parm.getValue("DIARRHEA", 0), "Y");
		}
		if (!parm.getValue("PHTHISIC", 0).equals("")) {
			this.setValue("PHTHISIC_" + parm.getValue("PHTHISIC", 0), "Y");
		}
		this.setValue("LYSSA", parm.getValue("LYSSA", 0));
		this.setValue("EPIDEMIC_HEPATITIS", parm.getValue("EPIDEMIC_HEPATITIS", 0));
		this.setValue("HIVES_FLG", parm.getValue("HIVES_FLG", 0));
		this.setValue("EPIDEMIC_BLOOD", parm.getValue("EPIDEMIC_BLOOD", 0));
		this.setValue("SCARLATINA", parm.getValue("SCARLATINA", 0));
		this.setValue("BRUCE_DISEASE", parm.getValue("BRUCE_DISEASE", 0));
		this.setValue("GONORRHEA", parm.getValue("GONORRHEA", 0));
		if (!parm.getValue("LUES", 0).equals("")) {
			this.setValue("LUES_" + parm.getValue("LUES", 0), "Y");
		}
		this.setValue("CHINCOUGH", parm.getValue("CHINCOUGH", 0));
		this.setValue("DIPHTHERIA", parm.getValue("DIPHTHERIA", 0));
		this.setValue("NEW_LOCKJAW", parm.getValue("NEW_LOCKJAW", 0));
		this.setValue("CATCH_LEPTOSPIRA", parm.getValue("CATCH_LEPTOSPIRA", 0));
		this.setValue("SCHISTOSOMIASIS_FLG", parm.getValue("SCHISTOSOMIASIS_FLG", 0));
		if (!parm.getValue("AGUE", 0).equals("")) {
			this.setValue("AGUE_" + parm.getValue("AGUE", 0), "Y");
		}
		this.setValue("GRIPPE_FLG", parm.getValue("GRIPPE_FLG", 0));
		this.setValue("MUMPS", parm.getValue("MUMPS", 0));
		this.setValue("MEASLES", parm.getValue("MEASLES", 0));
		this.setValue("ACUTE_CONJUNCTIVITIS", parm.getValue("ACUTE_CONJUNCTIVITIS", 0));
		this.setValue("LEPRA", parm.getValue("LEPRA", 0));
		this.setValue("SHIP_FEVER", parm.getValue("SHIP_FEVER", 0));
		this.setValue("KALA_AZAR", parm.getValue("KALA_AZAR", 0));
		this.setValue("ECHINOCOCCOSIS", parm.getValue("ECHINOCOCCOSIS", 0));
		this.setValue("FILARIASIS", parm.getValue("FILARIASIS", 0));
		this.setValue("EXPECT_CHOLERA", parm.getValue("EXPECT_CHOLERA", 0));
		// this.setValue("REST_INFECTION",parm.getValue("REST_INFECTION",0));
		this.setValue("REMARK", parm.getValue("REMARK", 0));
		this.setValue("REVISALILLNESS_NAME", parm.getValue("REVISALILLNESS_NAME", 0));
		this.setValue("COUNTERMAND_REAS", parm.getValue("COUNTERMAND_REAS", 0));
		this.setValue("REPORT_UNIT", parm.getValue("REPORT_UNIT", 0));
		this.setValue("CONT_TEL2", parm.getValue("CONT_TEL2", 0));
		this.setValue("SPEAKER", parm.getValue("SPEAKER", 0));
		// this.setValue("PAD_DEPT", parm.getValue("PAD_DEPT", 0));
		// String ICD_CODE = parm.getValue("ICD_CODE",0);
		// this.setValue("ICD_CODE", ICD_CODE);
		// TParm icd = SYSDiagnosisTool.getInstance().selectDataWithCode(ICD_CODE);
		// this.setValue("DISEASETYPE_CODE", icd.getValue("DISEASETYPE_CODE", 0));
		// this.setValue("ICD_DESC", icd.getValue("ICD_CHN_DESC", 0));
		// this.setValue("DISEASETYPE_CODE",parm.getValue("DISEASETYPE_CODE",0));
		// add by wanglong 20140307
		this.setValue("H_ADDRESS_PROVICE", parm.getValue("H_ADDRESS_PROVICE", 0));
		onH_ADDRESS_PROVICE();// add by wanglong 20140307
		this.setValue("H_ADDRESS_COUNTRY", parm.getValue("H_ADDRESS_COUNTRY", 0));
		this.setValue("H_ADDRESS_ROAD", parm.getValue("H_ADDRESS_ROAD", 0));
		this.setValue("H_ADDRESS_THORP", parm.getValue("H_ADDRESS_THORP", 0));
		this.setValue("H_DOORPLATE", parm.getValue("H_DOORPLATE", 0));
		this.setValue("POSITIVE_TEST", parm.getValue("POSITIVE_TEST", 0));
		this.setValue("HIV", parm.getValue("HIV", 0));
		this.setValue("FH1N1", parm.getValue("FH1N1", 0));
		this.setValue("HFMDIS", parm.getValue("HFMDIS", 0));
		this.setValue("NGU", parm.getValue("NGU", 0));
		this.setValue("CONCA", parm.getValue("CONCA", 0));
		this.setValue("GENHER", parm.getValue("GENHER", 0));
		this.setValue("VARICELLA", parm.getValue("VARICELLA", 0));
		this.setValue("AKAMUSHI", parm.getValue("AKAMUSHI", 0));
		this.setValue("GCTI", parm.getValue("GCTI", 0));
		this.setValue("HEPATIC", parm.getValue("HEPATIC", 0));
		this.setValue("ENCEPHALITIS", parm.getValue("ENCEPHALITIS", 0));
		this.setValue("PLEURISY", parm.getValue("PLEURISY", 0));
		this.setValue("HGDISEASE", parm.getValue("HGDISEASE", 0));
		this.setValue("HGA", parm.getValue("HGA", 0));
		this.setValue("UNPNEUMONIA", parm.getValue("UNPNEUMONIA", 0));
		this.setValue("UNKNOWNCAUSE", parm.getValue("UNKNOWNCAUSE", 0));
		this.setValue("THROMSYN", parm.getValue("THROMSYN", 0));
		this.setValue("AFP", parm.getValue("AFP", 0));
		this.setValue("OTHER", parm.getValue("OTHER", 0));
		
		//add by yanglu 20190426 增加 EMPHASIS_OTHER 字段
		this.setValue("EMPHASIS_OTHER", parm.getValue("EMPHASIS_OTHER", 0));
		
		// add by wangqing 20200929 新增  <乙类传染病>新型冠状病毒感染的肺炎
		this.setValue("NCOV", parm.getValue("NCOV", 0));// <乙类传染病>新型冠状病毒感染的肺炎
		
		// add by wanglong 20140307
	}

	/**
	 * 获取页面数据
	 */
	private TParm getDataValue() {
		TParm parm = new TParm();
		// ===============================PANEL_0
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("ADM_TYPE", ADM_TYPE);
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("IPD_NO", IPD_NO);
		parm.setData("FIRST_FLG", this.getValue("FIRST_FLG"));// 报卡类别
		parm.setData("CARD_NO", this.getValue("CARD_NO"));// 卡片编号
		parm.setData("CARD_SEQ_NO", this.getValue("CARD_SEQ_NO"));// 序号
		parm.setData("PAD_DATE", this.getValue("PAD_DATE") == null ? "" : this.getValue("PAD_DATE"));// 填卡时间
		parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		parm.setData("GENEARCH_NAME", this.getValue("GENEARCH_NAME"));// 患儿家长姓名
		parm.setData("IDNO", this.getValue("IDNO"));
		parm.setData("BIRTH_DATE", this.getValue("BIRTH_DATE") == null ? "" : this.getValue("BIRTH_DATE"));
		parm.setData("SEX", this.getValue("SEX"));
		parm.setData("CONT_TEL", this.getValue("CONT_TEL"));// 联系电话
		parm.setData("OFFICE", this.getValue("OFFICE"));// 工作单位
		if (this.getRadioSelected("SICK_ZONE_0"))
			parm.setData("SICK_ZONE", "0");// 本区县
		else if (this.getRadioSelected("SICK_ZONE_1"))
			parm.setData("SICK_ZONE", "1");// 本市其他区县
		else if (this.getRadioSelected("SICK_ZONE_2"))
			parm.setData("SICK_ZONE", "2");// 外省
		else if (this.getRadioSelected("SICK_ZONE_3"))
			parm.setData("SICK_ZONE", "3");// 港澳台
		else if (this.getRadioSelected("SICK_ZONE_4"))
			parm.setData("SICK_ZONE", "4");// 外籍
		else if (this.getRadioSelected("SICK_ZONE_5"))// add by wanglong 20140307
			parm.setData("SICK_ZONE", "5");// 本省其他地市
		parm.setData("ADDRESS_PROVICE", this.getValueString("ADDRESS_PROVICE"));
		parm.setData("ADDRESS_COUNTRY", this.getValueString("ADDRESS_COUNTRY"));
		parm.setData("ADDRESS_ROAD", this.getValue("ADDRESS_ROAD"));
		parm.setData("ADDRESS_THORP", this.getValue("ADDRESS_THORP"));
		parm.setData("DOORPLATE", this.getValue("DOORPLATE"));
		parm.setData("ILLNESS_DATE", this.getValue("ILLNESS_DATE") == null ? "" : this.getValue("ILLNESS_DATE"));// 发病日期
		parm.setData("COMFIRM_DATE", this.getValue("COMFIRM_DATE") == null ? "" : this.getValue("COMFIRM_DATE"));// 诊断日期
		parm.setData("DEAD_DATE", this.getValue("DEAD_DATE") == null ? "" : this.getValue("DEAD_DATE"));// 死亡日期
		parm.setData("ICD_CODE", "");// this.getValue("ICD_CODE")该字段暂时不用了，界面上不显示
		parm.setData("DISEASETYPE_CODE", "");// this.getValue("DISEASETYPE_CODE")
		// ===============================PANEL_1
		if (this.getRadioSelected("INVALID_PROF_0"))
			parm.setData("INVALID_PROF", "0");// 患者职业
		else if (this.getRadioSelected("INVALID_PROF_1"))
			parm.setData("INVALID_PROF", "1");
		else if (this.getRadioSelected("INVALID_PROF_2"))
			parm.setData("INVALID_PROF", "2");
		else if (this.getRadioSelected("INVALID_PROF_3"))
			parm.setData("INVALID_PROF", "3");
		else if (this.getRadioSelected("INVALID_PROF_4"))
			parm.setData("INVALID_PROF", "4");
		else if (this.getRadioSelected("INVALID_PROF_5"))
			parm.setData("INVALID_PROF", "5");
		else if (this.getRadioSelected("INVALID_PROF_6"))
			parm.setData("INVALID_PROF", "6");
		else if (this.getRadioSelected("INVALID_PROF_7"))
			parm.setData("INVALID_PROF", "7");
		else if (this.getRadioSelected("INVALID_PROF_8"))
			parm.setData("INVALID_PROF", "8");
		else if (this.getRadioSelected("INVALID_PROF_9"))
			parm.setData("INVALID_PROF", "9");
		else if (this.getRadioSelected("INVALID_PROF_10"))
			parm.setData("INVALID_PROF", "10");
		else if (this.getRadioSelected("INVALID_PROF_11"))
			parm.setData("INVALID_PROF", "11");
		else if (this.getRadioSelected("INVALID_PROF_12"))
			parm.setData("INVALID_PROF", "12");
		else if (this.getRadioSelected("INVALID_PROF_13"))
			parm.setData("INVALID_PROF", "13");
		else if (this.getRadioSelected("INVALID_PROF_14"))
			parm.setData("INVALID_PROF", "14");
		else if (this.getRadioSelected("INVALID_PROF_15"))
			parm.setData("INVALID_PROF", "15");
		else if (this.getRadioSelected("INVALID_PROF_16"))
			parm.setData("INVALID_PROF", "16");
		else if (this.getRadioSelected("INVALID_PROF_17"))
			parm.setData("INVALID_PROF", "17");
		else if (this.getRadioSelected("INVALID_PROF_18"))// 公共产所服务员add by wanglong 20140307
			parm.setData("INVALID_PROF", "18");
		else if (this.getRadioSelected("INVALID_PROF_19"))// 海员及长途驾驶员
			parm.setData("INVALID_PROF", "19");
		parm.setData("REST_PROF", this.getValue("REST_PROF"));// 其他职业
		// ===============================PANEL_2
		parm.setData("DOUBT_CASE", this.getCheckBoxSelected("DOUBT_CASE"));// 疑似病例
		parm.setData("CLINIC_DIAGNOSE", this.getCheckBoxSelected("CLINIC_DIAGNOSE"));// 临床诊断病例
		parm.setData("LAB_DIAGNOSE", this.getCheckBoxSelected("LAB_DIAGNOSE"));// 实验室确诊病例
		parm.setData("PATHOGENY_SCHLEP", this.getCheckBoxSelected("PATHOGENY_SCHLEP"));// 病源携带者

		//add by yanglu 20190426 增加 REMAIN_IN_HOSPITAL 留观病例 字段  begin
//		parm.setData("REMAIN_IN_HOSPITAL", this.getCheckBoxSelected("REMAIN_IN_HOSPITAL"));// 病源携带者
		//add by yanglu 20190426 增加 REMAIN_IN_HOSPITAL 留观病例 字段  end

		// add by wangqing 201741129
		 parm.setData("OBSERVATION_CASE",this.getCheckBoxSelected("OBSERVATION_CASE"));//留观病例
		parm.setData("H7N9_FLU", this.getCheckBoxSelected("H7N9_FLU"));// 人感染H7N9禽流感
		parm.setData("MERS", this.getCheckBoxSelected("MERS"));// 中东呼吸综合征
		parm.setData("EHF", this.getCheckBoxSelected("EHF"));// 埃博拉出血热

		parm.setData("PLAGUE_SPOT", this.getCheckBoxSelected("PLAGUE_SPOT"));// 鼠疫
		parm.setData("CHOLERA_FLG", this.getCheckBoxSelected("CHOLERA_FLG"));// 霍乱
		if (this.getRadioSelected("VIRUS_TYPE_0"))
			parm.setData("VIRUS_TYPE", "0");// 急性
		else if (this.getRadioSelected("VIRUS_TYPE_1"))
			parm.setData("VIRUS_TYPE", "1");// 慢性
		// else if(this.getRadioSelected("VIRUS_TYPE_2"))//没有此病
		// parm.setData("VIRUS_TYPE","2");
		else
			parm.setData("VIRUS_TYPE", "");
		// ===============================PANEL_3
		parm.setData("SARS_FLG", this.getCheckBoxSelected("SARS_FLG"));// 传染性非典型肺炎
		parm.setData("AIDS_FLG", this.getCheckBoxSelected("AIDS_FLG"));// 艾滋病
		if (this.getRadioSelected("VIRUS_HEPATITIS_0"))
			parm.setData("VIRUS_HEPATITIS", "0");// 病毒性肝炎
		else if (this.getRadioSelected("VIRUS_HEPATITIS_1"))
			parm.setData("VIRUS_HEPATITIS", "1");
		else if (this.getRadioSelected("VIRUS_HEPATITIS_2"))
			parm.setData("VIRUS_HEPATITIS", "2");
		else if (this.getRadioSelected("VIRUS_HEPATITIS_3"))
			parm.setData("VIRUS_HEPATITIS", "3");
		else if (this.getRadioSelected("VIRUS_HEPATITIS_4"))
			parm.setData("VIRUS_HEPATITIS", "4");
		else if (this.getRadioSelected("VIRUS_HEPATITIS_5"))// 丁型
			parm.setData("VIRUS_HEPATITIS", "5");
		else
			parm.setData("VIRUS_HEPATITIS", "");
		parm.setData("POLIOMYELITIS_FLG", this.getCheckBoxSelected("POLIOMYELITIS_FLG"));// 骨髓灰质炎
		parm.setData("HIGH_FLU", this.getCheckBoxSelected("HIGH_FLU"));// 人感染高致病性禽流感
		parm.setData("HIVES_FLG", this.getCheckBoxSelected("HIVES_FLG"));// 麻疹
		parm.setData("LYSSA", this.getCheckBoxSelected("LYSSA"));// 狂犬病
		parm.setData("EPIDEMIC_HEPATITIS", this.getCheckBoxSelected("EPIDEMIC_HEPATITIS"));// 流行性乙型脑炎
		parm.setData("DENGUE", this.getCheckBoxSelected("DENGUE"));// 登革热
		if (this.getRadioSelected("CHARCOAL_0"))
			parm.setData("CHARCOAL", "0");// 肺炭疽
		else if (this.getRadioSelected("CHARCOAL_1"))
			parm.setData("CHARCOAL", "1");// 皮肤炭疽
		else if (this.getRadioSelected("CHARCOAL_2"))
			parm.setData("CHARCOAL", "2");//
		// else if(this.getRadioSelected("CHARCOAL_3"))//没有此病
		// parm.setData("CHARCOAL","3");
		else
			parm.setData("CHARCOAL", "");
		if (this.getRadioSelected("DIARRHEA_0"))
			parm.setData("DIARRHEA", "0");// 细菌性
		else if (this.getRadioSelected("DIARRHEA_1"))
			parm.setData("DIARRHEA", "1");// 阿米巴性
		// else if(this.getRadioSelected("DIARRHEA_2"))//没有此病
		// parm.setData("DIARRHEA","2");
		else
			parm.setData("DIARRHEA", "");
		if (this.getRadioSelected("PHTHISIC_0"))
			parm.setData("PHTHISIC", "0");// 利福平耐药
		else if (this.getRadioSelected("PHTHISIC_1"))
			parm.setData("PHTHISIC", "1");// 病原学阳性
		else if (this.getRadioSelected("PHTHISIC_2"))
			parm.setData("PHTHISIC", "2");// 病原学阴性
		else if (this.getRadioSelected("PHTHISIC_3"))
			parm.setData("PHTHISIC", "3");// 无病原学结果
		else
			parm.setData("PHTHISIC", "");
		if (this.getRadioSelected("TYPHOID_0"))
			parm.setData("TYPHOID", "0");// 伤寒
		else if (this.getRadioSelected("TYPHOID_1"))
			parm.setData("TYPHOID", "1");// 副伤寒
		// else if(this.getRadioSelected("TYPHOID_2"))//没有此病
		// parm.setData("TYPHOID","2");
		else
			parm.setData("TYPHOID", "");
		parm.setData("EPIDEMIC_CEPHALITIS", this.getCheckBoxSelected("EPIDEMIC_CEPHALITIS"));// 流行性脑脊髓膜炎
		parm.setData("CHINCOUGH", this.getCheckBoxSelected("CHINCOUGH"));// 百日咳
		parm.setData("DIPHTHERIA", this.getCheckBoxSelected("DIPHTHERIA"));// 白喉
		parm.setData("NEW_LOCKJAW", this.getCheckBoxSelected("NEW_LOCKJAW"));// 新生儿破伤风
		parm.setData("CATCH_LEPTOSPIRA", this.getCheckBoxSelected("CATCH_LEPTOSPIRA"));// 钩端螺旋体病
		parm.setData("SCHISTOSOMIASIS_FLG", this.getCheckBoxSelected("SCHISTOSOMIASIS_FLG"));// 血吸虫病
		if (this.getRadioSelected("AGUE_0"))
			parm.setData("AGUE", "0");// 间日疟
		else if (this.getRadioSelected("AGUE_1"))
			parm.setData("AGUE", "1");// 恶性虐
		else if (this.getRadioSelected("AGUE_2"))
			parm.setData("AGUE", "2");// 未分型
		// else if(this.getRadioSelected("AGUE_3"))//没有此病
		// parm.setData("AGUE","3");
		else
			parm.setData("AGUE", "");
		parm.setData("EPIDEMIC_BLOOD", this.getCheckBoxSelected("EPIDEMIC_BLOOD"));// 流行性出血热
		parm.setData("SCARLATINA", this.getCheckBoxSelected("SCARLATINA"));// 猩红热
		parm.setData("BRUCE_DISEASE", this.getCheckBoxSelected("BRUCE_DISEASE"));// 布鲁氏菌病
		parm.setData("GONORRHEA", this.getCheckBoxSelected("GONORRHEA"));// 淋病
		if (this.getRadioSelected("LUES_0"))
			parm.setData("LUES", "0");// Ⅰ型
		else if (this.getRadioSelected("LUES_1"))
			parm.setData("LUES", "1");// Ⅱ期
		else if (this.getRadioSelected("LUES_2"))
			parm.setData("LUES", "2");// Ⅲ期
		else if (this.getRadioSelected("LUES_3"))
			parm.setData("LUES", "3");// 胎传
		else if (this.getRadioSelected("LUES_4"))
			parm.setData("LUES", "4");// 隐性
		// else if(this.getRadioSelected("LUES_5"))//没有此病
		// parm.setData("LUES","5");
		else
			parm.setData("LUES", "");
		// ===============================PANEL_4
		parm.setData("GRIPPE_FLG", this.getCheckBoxSelected("GRIPPE_FLG"));// 流行性感冒
		parm.setData("MUMPS", this.getCheckBoxSelected("MUMPS"));// 流行性腮腺炎
		parm.setData("MEASLES", this.getCheckBoxSelected("MEASLES"));// 风疹
		parm.setData("ACUTE_CONJUNCTIVITIS", this.getCheckBoxSelected("ACUTE_CONJUNCTIVITIS"));// 急性出血性结膜炎
		parm.setData("LEPRA", this.getCheckBoxSelected("LEPRA"));// 麻风病
		parm.setData("SHIP_FEVER", this.getCheckBoxSelected("SHIP_FEVER"));// 流行性和地方性斑疹伤寒
		parm.setData("KALA_AZAR", this.getCheckBoxSelected("KALA_AZAR"));// 黑热病
		parm.setData("ECHINOCOCCOSIS", this.getCheckBoxSelected("ECHINOCOCCOSIS"));// 包虫病
		parm.setData("FILARIASIS", this.getCheckBoxSelected("FILARIASIS"));// 丝虫病
		parm.setData("EXPECT_CHOLERA", this.getCheckBoxSelected("EXPECT_CHOLERA"));// 除霍乱，细菌性和阿米巴性痢疾，伤寒和副伤寒以外的感染性腹泻
		// ===============================PANEL_5
		// add by wanglong 20140221
		// parm.setData("H_ADDRESS_PROVICE",this.getValueString("H_ADDRESS_PROVICE"));//户籍地址
		// parm.setData("H_ADDRESS_COUNTRY",this.getValueString("H_ADDRESS_COUNTRY"));
		// parm.setData("H_ADDRESS_ROAD",this.getValueString("H_ADDRESS_ROAD"));
		// parm.setData("H_ADDRESS_THORP",this.getValueString("H_ADDRESS_THORP"));
		// parm.setData("H_DOORPLATE",this.getValueString("H_DOORPLATE"));
		// parm.setData("POSITIVE_TEST",this.getCheckBoxSelected("POSITIVE_TEST"));//阳性检测结果

		parm.setData("HIV", this.getCheckBoxSelected("HIV"));// HIV
		// parm.setData("FH1N1",this.getCheckBoxSelected("FH1N1"));//甲型H1N1流感
		parm.setData("HFMDIS", this.getCheckBoxSelected("HFMDIS"));// 手足口病
		parm.setData("NGU", this.getCheckBoxSelected("NGU"));// 非淋菌性尿道炎
		parm.setData("CONCA", this.getCheckBoxSelected("CONCA"));// 尖锐湿疣
		parm.setData("GENHER", this.getCheckBoxSelected("GENHER"));// 生殖器疱疹
		parm.setData("VARICELLA", this.getCheckBoxSelected("VARICELLA"));// 水痘
		parm.setData("AKAMUSHI", this.getCheckBoxSelected("AKAMUSHI"));// 恙虫病
		parm.setData("GCTI", this.getCheckBoxSelected("GCTI"));// 风生殖道沙眼衣原体感染
		parm.setData("HEPATIC", this.getCheckBoxSelected("HEPATIC"));// 肝吸虫病
		parm.setData("ENCEPHALITIS", this.getCheckBoxSelected("ENCEPHALITIS"));// 森林脑炎
		parm.setData("PLEURISY", this.getCheckBoxSelected("PLEURISY"));// 结核性胸膜炎
		parm.setData("HGDISEASE", this.getCheckBoxSelected("HGDISEASE"));// 人感染猪链球菌
		parm.setData("HGA", this.getCheckBoxSelected("HGA"));// 人粒细胞无形体病
		parm.setData("UNPNEUMONIA", this.getCheckBoxSelected("UNPNEUMONIA"));// 不明原因肺炎
		parm.setData("UNKNOWNCAUSE", this.getCheckBoxSelected("UNKNOWNCAUSE"));// 不明原因
		parm.setData("THROMSYN", this.getCheckBoxSelected("THROMSYN"));// 发热伴血小板减少综合征
		parm.setData("AFP", this.getCheckBoxSelected("AFP"));// AFP
		parm.setData("OTHER", this.getCheckBoxSelected("OTHER"));// 其他
		
		// add by wangqing 20200929 新增 <乙类传染病>新型冠状病毒感染的肺炎
		parm.setData("NCOV", this.getCheckBoxSelected("NCOV"));// <乙类传染病>新型冠状病毒感染的肺炎
		
		//add by yanglu 20190426  增加其他输入框  EMPHASIS_OTHER 字段  begin
		if("Y".equals(this.getCheckBoxSelected("OTHER"))) {
			parm.setData("EMPHASIS_OTHER", this.getValue("EMPHASIS_OTHER"));// 其他输入框
		}else {
			parm.setData("EMPHASIS_OTHER", "");// 其他输入框

		}
		//add by yanglu 20190426  增加其他输入框  EMPHASIS_OTHER 字段  end
		
		// add end
		parm.setData("REST_INFECTION", "");// 其他法定管理以及重点监测传染病this.getValue("REST_INFECTION")该字段暂时不用了，界面上不显示
		// ===============================PANEL_6
		parm.setData("REMARK", this.getValue("REMARK"));// 备注
		parm.setData("REVISALILLNESS_NAME", this.getValue("REVISALILLNESS_NAME"));// 订正病名
		parm.setData("COUNTERMAND_REAS", this.getValue("COUNTERMAND_REAS"));// 退卡原因
		parm.setData("REPORT_UNIT", this.getValue("REPORT_UNIT"));// 报告单位
		parm.setData("CONT_TEL2", this.getValue("CONT_TEL2"));// 联系电话
		parm.setData("SPEAKER", this.getValue("SPEAKER"));// 报告医生
		parm.setData("PAD_DEPT", "");// this.getValueString("PAD_DEPT")该字段暂时不用了，界面上不显示
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		//
		parm.setData("H_ADDRESS_PROVICE", "");// 户籍地址
		parm.setData("H_ADDRESS_COUNTRY", "");
		parm.setData("H_ADDRESS_ROAD", "");
		parm.setData("H_ADDRESS_THORP", "");
		parm.setData("H_DOORPLATE", "");
		parm.setData("POSITIVE_TEST", false);// 阳性检测结果
		parm.setData("FH1N1", false);// 甲型H1N1流感
//		parm.setData("OBSERVATION_CASE", false);// 留观病例
		parm.setData("ZIKA_VIRUS", this.getValue("ZIKA_VIRUS"));// 寨卡病毒病

		return parm;
	}

	/**
	 * 检查保存信息
	 */
	private boolean checkData() {
		
		if(this.getValueString("PAT_NAME").length() <= 0) {
			this.messageBox_("请填写患者姓名");
			this.grabFocus("PAT_NAME");
			return false;
		}
		if("".equals(this.getValueString("AGE"))) {
			this.messageBox_("年龄为空，请完善病患资料");
			this.grabFocus("PAT_NAME");
			return false;
		}
		String age = this.getValueString("AGE");
		String[] strArr = age.split("岁");
		int intAge = Integer.parseInt(strArr[0]);
		if(this.getValueString("GENEARCH_NAME").length() <= 0 &&  intAge<14) {
			this.messageBox_("请填写患儿家长姓名");
			this.grabFocus("GENEARCH_NAME");
			return false;
		}
		if (this.getValueString("FIRST_FLG").length() <= 0) {
			this.messageBox_("请选择报卡类别");
			this.grabFocus("FIRST_FLG");
			return false;
		}
		if (this.getValue("PAD_DATE") == null) {
			this.messageBox_("请选择报卡时间");
			this.grabFocus("PAD_DATE");
			return false;
		}
		if(this.getValueString("IDNO").length() <= 0) {
			this.messageBox_("请填写有效证件号");
			this.grabFocus("IDNO");
			return false;
		}
		if(this.getValueString("SEX").length() <= 0) {
			this.messageBox_("请填写性别");
			this.grabFocus("SEX");
			return false;
		}
		if(this.getValueString("BIRTH_DATE").length() <= 0) {
			this.messageBox_("请填写出生日期");
			this.grabFocus("BIRTH_DATE");
			return false;
		}
		if(
				!this.getRadioSelected("SICK_ZONE_0") &&
				!this.getRadioSelected("SICK_ZONE_1") &&
				!this.getRadioSelected("SICK_ZONE_2") &&
				!this.getRadioSelected("SICK_ZONE_3") &&
				!this.getRadioSelected("SICK_ZONE_4") &&
				!this.getRadioSelected("SICK_ZONE_5") 
				) {
			this.messageBox_("请填写病人属于");
			this.grabFocus("SICK_ZONE");
			return false;
		}
		if(this.getValueString("ADDRESS_PROVICE").length() <= 0) {
			this.messageBox_("请填写省/直辖市");
			this.grabFocus("ADDRESS_PROVICE");
			return false;
		}
		if(this.getValueString("ADDRESS_COUNTRY").length() <= 0 ) {
			this.messageBox_("请填写区、县地址");
			this.grabFocus("ADDRESS_COUNTRY");
			return false;
		}
		if( this.getValueString("ADDRESS_ROAD").length() <= 0) {
			this.messageBox_("请填写乡(镇，街道)地址");
			this.grabFocus("ADDRESS_ROAD");
			return false;
		}
		if(
				!this.getRadioSelected("INVALID_PROF_0") &&
				!this.getRadioSelected("INVALID_PROF_1") &&
				!this.getRadioSelected("INVALID_PROF_2") &&
				!this.getRadioSelected("INVALID_PROF_3") &&
				!this.getRadioSelected("INVALID_PROF_4") &&
				!this.getRadioSelected("INVALID_PROF_5") &&
				!this.getRadioSelected("INVALID_PROF_6") &&
				!this.getRadioSelected("INVALID_PROF_7") &&
				!this.getRadioSelected("INVALID_PROF_8") &&
				!this.getRadioSelected("INVALID_PROF_9") &&
				!this.getRadioSelected("INVALID_PROF_10") &&
				!this.getRadioSelected("INVALID_PROF_11") &&
				!this.getRadioSelected("INVALID_PROF_12") &&
				!this.getRadioSelected("INVALID_PROF_13") &&
				!this.getRadioSelected("INVALID_PROF_14") &&
				!this.getRadioSelected("INVALID_PROF_15") &&
				!this.getRadioSelected("INVALID_PROF_16") &&
				!this.getRadioSelected("INVALID_PROF_17") &&
				!this.getRadioSelected("INVALID_PROF_18") &&
				!this.getRadioSelected("INVALID_PROF_19") 
				) {
			this.messageBox_("请填写人群分类");
			this.grabFocus("INVALID_PROF_19");
			return false;
		}
		if(this.getValueString("ILLNESS_DATE").length() <= 0) {
			this.messageBox_("请填写发病日期");
			this.grabFocus("ILLNESS_DATE");
			return false;
		}
		if(this.getValueString("COMFIRM_DATE").length() <= 0) {
			this.messageBox_("请填写诊断日期");
			this.grabFocus("COMFIRM_DATE");
			return false;
		}		
		if (this.getValue("DEAD_DATE") != null && this.getValue("ILLNESS_DATE") != null) {
			if (StringTool.getDateDiffer((Timestamp) this.getValue("DEAD_DATE"),
					(Timestamp) this.getValue("ILLNESS_DATE")) < 0) {
				this.messageBox_("死亡日期不可早于发病日期");
				this.grabFocus("DEAD_DATE");
				return false;
			}
		}
		if (this.getValue("COMFIRM_DATE") != null && this.getValue("ILLNESS_DATE") != null) {
			if (StringTool.getDateDiffer((Timestamp) this.getValue("COMFIRM_DATE"),
					(Timestamp) this.getValue("ILLNESS_DATE")) < 0) {
				this.messageBox_("诊断日期不可早于发病日期");
				this.grabFocus("COMFIRM_DATE");
				return false;
			}
		}
		if(
				"N".equals(this.getCheckBoxSelected("CLINIC_DIAGNOSE")) &&
				"N".equals(this.getCheckBoxSelected("LAB_DIAGNOSE"))  &&
				"N".equals(this.getCheckBoxSelected("DOUBT_CASE"))  &&
				"N".equals(this.getCheckBoxSelected("PATHOGENY_SCHLEP")) &&
				"N".equals(this.getCheckBoxSelected("OBSERVATION_CASE")) 
				) {
			this.messageBox_("请填写病例分类");
			this.grabFocus("CLINIC_DIAGNOSE");
			return false;
		}
		if (this.getValueString("REPORT_UNIT").length() <= 0) {
			this.messageBox_("请填写报告单位");
			this.grabFocus("REPORT_UNIT");
			return false;
		}
		if(this.getValueString("SPEAKER").length() <= 0) {
			this.messageBox_("请填写填卡医生");
			this.grabFocus("SPEAKER");
			return false;
		}

		return true;
	}

	/**
	 * 获取单选按钮的选中状态
	 * 
	 * @param tag
	 *            String
	 * @return boolean
	 */
	private boolean getRadioSelected(String tag) {
		TRadioButton a = (TRadioButton) this.getComponent(tag);
		return a.isSelected();
	}

	/**
	 * 获取复选框的选中状态
	 * 
	 * @param tag
	 *            String
	 * @return String
	 */
	private String getCheckBoxSelected(String tag) {
		TCheckBox a = (TCheckBox) this.getComponent(tag);
		if (a.isSelected())
			return "Y";
		else
			return "N";
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		// System.out.println("@test by wangqing@---MR_NO="+MR_NO);
		// System.out.println("@test by wangqing@---CASE_NO="+CASE_NO);
		// System.out.println("@test by wangqing@---CARD_SEQ_NO="+CARD_SEQ_NO);
		if (MR_NO == null || MR_NO.trim().length() <= 0) {
			this.messageBox("病案号为空");
			return;
		}
		if (CASE_NO == null || CASE_NO.trim().length() <= 0) {
			this.messageBox("就诊号为空");
			return;
		}
		if (CARD_SEQ_NO == null || CARD_SEQ_NO.trim().length() <= 0) {
			this.messageBox("感染卡号为空");
			return;
		}
		// 获取打印数据
		TParm parm = MROInfectTool.getInstance().getPrintData(MR_NO, CASE_NO, CARD_SEQ_NO);
//		System.out.println("**********输出parm**********"+parm);
		if (parm.getErrCode() < 0) {
			return;
		}
//		System.out.println("########################################REMARK=" + parm.getValue("REMARK"));

		// parm.setData("SARS_FLG", "TEXT", "Y");

		this.openPrintDialog("%ROOT%\\config\\prt\\MRO\\MROInfect.jhw", parm);
		// this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROInfect.jhw",parm);
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		if ("UPDATE".equals(SAVE_FLG)) {
			int re = this.messageBox("提示", "确定要删除此条信息吗？", 0);
			if (re == 0) {
				TParm result = MROInfectTool.getInstance().delInfect(MR_NO, CASE_NO, CARD_SEQ_NO);
				if (result.getErrCode() < 0) {
					this.messageBox("E0005");
					return;
				}
				this.messageBox("P0005");
				this.onClear();
			}
		}
	}

	/**
	 * 历史查询
	 */
	public void onHistory() {
		if ("DR".equals(OPEN_TYPE)) {
			hasInfectData("M");
		} else
			hasInfectData("H");
	}

	/**
	 * 现住址，省选择事件
	 */
	public void onADDRESS_PROVICE() {
		this.clearValue("ADDRESS_COUNTRY");
		this.callFunction("UI|ADDRESS_COUNTRY|onQuery");
	}

	/**
	 * 户籍地址，省选择事件
	 */
	public void onH_ADDRESS_PROVICE() {// add by wanglong 20140307
		this.clearValue("H_ADDRESS_COUNTRY");
		this.callFunction("UI|H_ADDRESS_COUNTRY|onQuery");
	}

	/**
	 * 选单按钮点击事件
	 * 
	 * @param tag
	 */
	public void onRadioButtonClick(String tag) {// add by wanglong 20140307
		TRadioButton trb = (TRadioButton) this.getComponent(tag);
		ButtonGroup buttonGroup = (ButtonGroup) trb.callFunction("getButtonGroup", new Object[] { trb.getGroup() });
		Enumeration<AbstractButton> e = buttonGroup.getElements();
		while (e.hasMoreElements()) {
			TRadioButton erb = (TRadioButton) e.nextElement();
			if (trb == erb) {
				if (trb.getZhTip().equalsIgnoreCase("Y")) {
					buttonGroup.clearSelection();
					trb.setZhTip("");
				} else
					trb.setZhTip("Y");
			} else
				erb.setZhTip("");
		}
	}
	
	
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return true;
    }
    
    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

}
