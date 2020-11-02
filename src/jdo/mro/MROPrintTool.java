package jdo.mro;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.FieldCache.DoubleParser;

import jdo.adm.ADMChildImmunityTool;
import jdo.emr.EMRCreateXMLTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;


/**
 * <p>
 * Title: 首页打印Tool
 * </p>
 * 
 * <p>
 * Description: 首页打印Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangk 2009-9-20
 * @version 4.0
 */
public class MROPrintTool extends TJDOTool {
	TDataStore ICD_DATA = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	TDataStore DICTIONARY;
	Map drList;
	/**
	 * 实例
	 */
	public static MROPrintTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static MROPrintTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROPrintTool();
		return instanceObject;
	}

	public MROPrintTool() {
		drList = getDrList();
		getDICTIONARY();
		//DICTIONARY.showDebug();
	}

	/**
	 * 获取首页打印信息（废除）
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm getMroRecordPrintData(String CASE_NO) {
		// 获取某一病患的首页信息
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		// 判断该病患是否是产妇
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// 获取新生儿的信息 显示在母亲的病案首页上
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// 查询新生儿的首页信息
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		// 查询手术信息
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		DecimalFormat df = new DecimalFormat("0.00");
		// 整理打印数据
		TParm data = new TParm();
		// 页眉信息
		data.setData("head_mr_no", "TEXT", print.getValue("MR_NO", 0));// MR_NO
		data.setData("head_ipd_no", "TEXT", print.getValue("IPD_NO", 0));// IPD_NO
		// data.setData("CTZ1","TEXT",getDesc("SYS_CTZ","","CTZ_DESC","CTZ_CODE",print.getValue("CTZ1_CODE",0)));//身份1//CTZ1
		// S.13
		/**
		 * add 20120113
		 */
		data.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// 病案首页身份
		data.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		data.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// 组织机构代码
		data.setData("NHI_NO", "TEXT", print.getValue("NHI_NO", 0));// 健康卡号 医保卡
		TParm sumParm = new TParm();
		sumParm.setData("CASE_NO", CASE_NO);
		sumParm.setData("ADM_TYPE", "I");
		data.setData("NB_ADM_WEIGHT", "TEXT", SUMNewArrivalTool.getInstance()
				.getNewAdmWeight(sumParm));// 新生儿入院体重
		data.setData("NB_WEIGHT", "TEXT", SUMNewArrivalTool.getInstance()
				.getNewBornWeight(sumParm));// 新生儿出生体重
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		data.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 7 ? birthplace.substring(0, 7)
						: birthplace);// //籍贯

		data.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// 通信地址
		// HR03.00.005
		// H.05
		data.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// 通信邮编

		data.setData("TEL", "TEXT", print.getValue("TEL", 0));// 通信邮编

		// 设置身份HR56.00.002.05 CDA值
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		data.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		data.setData("IN_COUNT", "TEXT", "第" + print.getValue("IN_COUNT", 0)
		+ "次住院");// 住院次数
		// 病患基本信息
		data.setData("HR02.01.001.01", "TEXT", print.getValue("PAT_NAME", 0));// 姓名
		data.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// 性别
		// HR02.02.001
		// H.03
		// 设置性别CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		data.setData("HR02.02.001", "TEXT", sexCDAValue);
		data.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy年MM月dd日"));// 出生日期
		// HR30.00.001
		// H.03
		data.setData("AGE", "TEXT", print.getValue("AGE", 0));// 年龄 HR02.03.001
		// H.03
		data.setData("MARRIGE", "TEXT", print.getValue("MARRIGE", 0));// 婚姻
		// HR02.06.003
		// H.03
		// 设置婚姻CDA代码
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		data.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		data.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// 职业 HR02.07.011.02 H.03
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		data.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 7 ? HOEMPLACE.substring(0, 7) : HOEMPLACE);// 出生地
		// 代码需要转换
		// HR30.00.005
		// H.03
		data.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// 民族
		// HR02.05.001
		// H.03
		data.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// 国籍
		// HR02.04.001
		// H.03
		data.setData("IDNO", "TEXT", print.getValue("IDNO", 0));// 身份证
		// HR01.01.002.02
		// H.02
		data.setData("OFFICE", "TEXT", print.getValue("OFFICE", 0));// 工作单位
		// HR02.07.006
		// H.05
		data.setData("O_ADDRESS", "TEXT", print.getValue("O_ADDRESS", 0));// 单位地址
		// HR03.00.003
		// H.05
		data.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// 单位电话
		// HR04.00.001.03
		// H.06
		data.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// 单位邮编
		// HR03.00.005
		// H.05
		data.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// 户口地址
		// HR03.00.004.02
		// H.05
		data.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// 户口邮编
		// HR03.00.005
		// H.05
		// HR03.00.005
		// H.05
		data.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// 联系人姓名
		// HR02.01.002
		// H.02
		data.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// 联系人关系 HR02.18.004
		data.setData("CONT_ADDRESS", "TEXT", print.getValue("CONT_ADDRESS", 0));// 联系人地址
		// HR03.00.004.02
		// H.05
		data.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// 联系人电话
		// HR04.00.001.03
		// H.06
		data.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日 HH时"));// 入院日期
		// HR00.00.001.06
		// H.01
		data.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// 入院科别 HR21.01.100.05
		// H.08
		data.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
						print.getValue("IN_ROOM_NO", 0)));// 入院病房 --------无编码

		data.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(print.getValue("TRANS_DEPT", 0)));// 转科科别
		// HR42.03.100
		// H.10
		data.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日 HH时"));// 出院日期
		// HR42.02.201
		// H.10
		if (print.getData("OUT_DATE", 0) != null) {// 判断是否出院
			data.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // 出院科室 HR42.03.100
			// H.10
			data.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
							print.getValue("OUT_ROOM_NO", 0))); // 出院病室
		} else {
			data.setData("OUT_DEPT", "TEXT", "");
			data.setData("OUT_ROOM", "TEXT", "");
		}
		data.setData("	", "TEXT", print.getValue("REAL_STAY_DAYS", 0));// 实际住院天数
		// HR52.02.103
		// S.12
		data.setData("IN_CONDITION", "TEXT", print.getValue("IN_CONDITION", 0)
				.replace("0", ""));// 入院情况
		// 设置入院情况CDA代码
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		data.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		data.setData("CONFIRM_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CONFIRM_DATE", 0), "yyyy年MM月dd日"));// 确诊日期
		// HR42.02.202
		// S.11.002
		// 实际要求以天数为单位
		/**
		 * add 20120113
		 */
		data.setData("ADM_SOURCE", "TEXT",
				getNewadmSource(print.getValue("ADM_SOURCE", 0)));// 病人来源
		data.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// 离院方式
		data.setData("TRAN_HOSP", "TEXT", print.getValue("TRAN_HOSP", 0));// 外转院区
		data.setData("BE_COMA_TIME", "TEXT", print.getValue("BE_COMA_TIME", 0)
				.substring(0, 2)
				+ "天"
				+ print.getValue("BE_COMA_TIME", 0).substring(2, 4)
				+ "小时"
				+ print.getValue("BE_COMA_TIME", 0).substring(4, 6) + "分钟");// 入院前昏迷时间
		data.setData("AF_COMA_TIME", "TEXT", print.getValue("AF_COMA_TIME", 0)
				.substring(0, 2)
				+ "天"
				+ print.getValue("AF_COMA_TIME", 0).substring(2, 4)
				+ "小时"
				+ print.getValue("AF_COMA_TIME", 0).substring(4, 6) + "分钟");// 入院后昏迷时间
		data.setData("VS_NURSE_CODE", "TEXT",
				print.getValue("VS_NURSE_CODE", 0));// 责任护士
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		data.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31天计划标记
		data.setData("AGN_PLAN_INTENTION", "TEXT",
				print.getValue("AGN_PLAN_INTENTION", 0));// 31天计划原因

		String OE_DIAG_CODE = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// 诊断代码：（疾病代码）HR55.02.057.05
			// S.07
			// （疾病名称）HR55.02.057.04
			// S.07
			OE_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		}
		if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
			OE_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		}
		data.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断
		String IN_DIAG_CODE = getICD_DESC(print.getValue("IN_DIAG_CODE", 0));
		if (print.getValue("IN_DIAG_CODE2", 0).length() > 0) {
			IN_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("IN_DIAG_CODE2", 0));
		}
		if (print.getValue("IN_DIAG_CODE3", 0).length() > 0) {
			IN_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("IN_DIAG_CODE3", 0));
		}
		data.setData("IN_DIAG_CODE", "TEXT", IN_DIAG_CODE);// 入院诊断
		// HR55.02.057.04
		// S.07
		/***** 出院诊断部分信息 *******************/
		// 如果是新生儿 那么填写新生儿诊断
		if (child.getCount("CASE_NO") > 0) {
			data.setData("C_OUT_DIAG_CODE1", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
			data.setData("C_OUT_DIAG_CODE2", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
			data.setData("C_OUT_DIAG_CODE3", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3

			data.setData("C_OUT1_" + childDiag.getValue("CODE1_STATUS", 0),
					"TEXT", "√"); // 出院主诊断 转归
			data.setData("C_OUT2_" + childDiag.getValue("CODE2_STATUS", 0),
					"TEXT", "√"); // 出院诊断2 转归
			data.setData("C_OUT3_" + childDiag.getValue("CODE3_STATUS", 0),
					"TEXT", "√"); // 出院诊断3 转归

			data.setData("C_OUT1_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断
			data.setData("C_OUT2_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2
			data.setData("C_OUT3_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3
		}
		data.setData("OUT_DIAG_CODE1", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
		data.setData("OUT_DIAG_CODE2", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
		data.setData("OUT_DIAG_CODE3", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3
		data.setData("OUT_DIAG_CODE4", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // 出院诊断4
		data.setData("OUT_DIAG_CODE5", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // 出院诊断5
		data.setData("OUT_DIAG_CODE6", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // 出院诊断6
		data.setData("OUT1_" + print.getValue("CODE1_STATUS", 0), "TEXT", "√"); // 出院主诊断
		// 转归
		data.setData("OUT2_" + print.getValue("CODE2_STATUS", 0), "TEXT", "√"); // 出院诊断2
		// 转归
		data.setData("OUT3_" + print.getValue("CODE3_STATUS", 0), "TEXT", "√"); // 出院诊断3
		// 转归
		data.setData("OUT4_" + print.getValue("CODE4_STATUS", 0), "TEXT", "√"); // 出院诊断4
		// 转归
		data.setData("OUT5_" + print.getValue("CODE5_STATUS", 0), "TEXT", "√"); // 出院诊断5
		// 转归
		data.setData("OUT6_" + print.getValue("CODE6_STATUS", 0), "TEXT", "√"); // 出院诊断6
		// 转归
		data.setData("OUT1_ICD", "TEXT", print.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断
		data.setData("OUT2_ICD", "TEXT", print.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2
		data.setData("OUT3_ICD", "TEXT", print.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3
		data.setData("OUT4_ICD", "TEXT", print.getValue("OUT_DIAG_CODE4", 0)); // 出院诊断4
		data.setData("OUT5_ICD", "TEXT", print.getValue("OUT_DIAG_CODE5", 0)); // 出院诊断5
		data.setData("OUT6_ICD", "TEXT", print.getValue("OUT_DIAG_CODE6", 0)); // 出院诊断6
		/****** 医师部分 *********/
		String INTE_DIAG_CODE = getICD_DESC(print.getValue("INTE_DIAG_CODE", 0));
		data.setData("INTE_DIAG_CODE", "TEXT",
				INTE_DIAG_CODE.length() == 0 ? "—" : INTE_DIAG_CODE);// 院内感染诊断
		data.setData("INTE_ICD", "TEXT", print.getValue("INTE_DIAG_CODE", 0));// 院内感染诊断CODE
		data.setData("INTE_STATUS" + print.getValue("INTE_DIAG_STATUS", 0),
				"TEXT", "√");// 院内感染转归
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		data.setData("PATHOLOGY_DIAG", "TEXT",
				PATHOLOGY_DIAG.length() == 0 ? "—" : PATHOLOGY_DIAG);// 病理诊断

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		data.setData("EX_RSN", "TEXT", EX_RSN.length() == 0 ? "—" : EX_RSN);// 损伤、中毒的外部因素
		data.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// 药物过敏
		data.setData("HBsAg", "TEXT", print.getValue("HBSAG", 0));// HBSAG
		// 设置HBsAgHRCDA代码
		String HBsAgHRCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HBSAG", 0));
		data.setData("HBsAgHR51.99.004.08", "TEXT", HBsAgHRCDAValue);
		data.setData("HCV-Ab", "TEXT", print.getValue("HCV_AB", 0));// HCV_AB
		// 设置HCV_ABCDA代码
		String HCV_ABCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HCV-Ab", 0));
		data.setData("HCV-AbHR51.99.004.08", "TEXT", HCV_ABCDAValue);
		data.setData("HIV-Ab", "TEXT", print.getValue("HIV_AB", 0));// HIV_AB
		// 设置HIV-AbCDA代码
		String HIV_AbCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HCV-Ab", 0));
		data.setData("HIV-AbHR51.99.004.08", "TEXT", HIV_AbCDAValue);
		data.setData("QUYCHK_OI", "TEXT", print.getValue("QUYCHK_OI", 0));// 门诊与住院
		// 设置门诊与住院代码
		String QUYCHK_OICDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_OI", 0));
		data.setData("QUYCHK_OIHR55.01.045.02", "TEXT", QUYCHK_OICDAValue);
		data.setData("QUYCHK_INOUT", "TEXT", print.getValue("QUYCHK_INOUT", 0));// 入院与出院
		// 设置入院与出院代码
		String QUYCHK_INOUTCDAValue = EMRCreateXMLTool.getInstance()
				.getCDACode("S.11.002", "HR55.01.045.02",
						print.getValue("QUYCHK_INOUT", 0));
		data.setData("QUYCHK_INOUTHR55.01.045.02", "TEXT", QUYCHK_INOUTCDAValue);
		data.setData("QUYCHK_OPBFAF", "TEXT",
				print.getValue("QUYCHK_OPBFAF", 0));// 术前术后
		// 设置术前与术后代码
		String QUYCHK_OPBFAFCDAValue = EMRCreateXMLTool.getInstance()
				.getCDACode("S.11.002", "HR55.01.045.02",
						print.getValue("QUYCHK_OPBFAF", 0));
		data.setData("QUYCHK_OPBFAFHR55.01.045.02", "TEXT",
				QUYCHK_OPBFAFCDAValue);
		data.setData("QUYCHK_CLPA", "TEXT", print.getValue("QUYCHK_CLPA", 0));// 临床与病理
		// 设置临床与病理
		String QUYCHK_CLPACDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_CLPA", 0));
		data.setData("QUYCHK_CLPAHR55.01.045.02", "TEXT", QUYCHK_CLPACDAValue);
		data.setData("QUYCHK_RAPA", "TEXT", print.getValue("QUYCHK_RAPA", 0));// 放射与病理
		// 设置放射与病理
		String QUYCHK_RAPACDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_RAPA", 0));
		data.setData("QUYCHK_RAPAHR55.01.045.02", "TEXT", QUYCHK_RAPACDAValue);
		data.setData("GET_TIMES", "TEXT", print.getValue("GET_TIMES", 0));// 抢救次数
		data.setData("SUCCESS_TIMES", "TEXT",
				print.getValue("SUCCESS_TIMES", 0));// 成功次数
		data.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// 科主任
		data.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// 主任医师
		data.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// 主治医师
		data.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// 住院医师
		data.setData("INDUCATION_DR_CODE", "TEXT",
				drList.get(print.getValue("INDUCATION_DR_CODE", 0)));// 进修医师
		data.setData("GRADUATE_INTERN_CODE", "TEXT",
				drList.get(print.getValue("GRADUATE_INTERN_CODE", 0)));// 研究生实习医师
		data.setData("INTERN_DR_CODE", "TEXT",
				drList.get(print.getValue("INTERN_DR_CODE", 0)));// 实习医师
		data.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// 编码员
		data.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// 病案质量
		// 设置病案质量
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		data.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		data.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// 质控医师
		data.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// 质控护士
		data.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy年MM月dd日"));// 质控日期
		/***** 费用部分 **********/
		data.setData("INFECT_REPORT", "TEXT",
				print.getValue("INFECT_REPORT", 0));// 传染病报告
		data.setData("DIS_REPORT", "TEXT", print.getValue("DIS_REPORT", 0));// 四病报告
		data.setData("BODY_CHECK", "TEXT", print.getValue("BODY_CHECK", 0));// 尸检
		data.setData("FIRST_CASE", "TEXT", print.getValue("FIRST_CASE", 0));// 首例
		// 根据随诊 年 月 周 判断是否随诊
		if (print.getValue("ACCOMPANY_WEEK", 0).length() > 0
				|| print.getValue("ACCOMPANY_MONTH", 0).length() > 0
				|| print.getValue("ACCOMPANY_YEAR", 0).length() > 0)
			data.setData("ACCOMPANY", "TEXT", "1");// 随诊
		else
			data.setData("ACCOMPANY", "TEXT", "2");
		data.setData(
				"ACCOMPANY_DATE",
				"TEXT",
				print.getValue("ACCOMPANY_WEEK", 0) + "周"
						+ print.getValue("ACCOMPANY_MONTH", 0) + "月"
						+ print.getValue("ACCOMPANY_YEAR", 0) + "年");// 随诊日期
		data.setData("SAMPLE_FLG", "TEXT", print.getValue("SAMPLE_FLG", 0));// 示教病例
		data.setData(
				"BLOOD_TYPE",
				"TEXT",
				print.getValue("BLOOD_TYPE", 0).length() == 0
				|| "6".equalsIgnoreCase(print.getValue("BLOOD_TYPE", 0)) ? "-"
						: print.getValue("BLOOD_TYPE", 0));// 血型
		// 设置血型
		String blockType = print.getValue("BLOOD_TYPE", 0).length() == 0
				|| "6".equalsIgnoreCase(print.getValue("BLOOD_TYPE", 0)) ? "-"
						: print.getValue("BLOOD_TYPE", 0);
		String BlockTypeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.02.001", "HR51.03.003", blockType);
		data.setData("HR51.03.003", "TEXT", BlockTypeCDAValue);
		// 设置RH
		String rhType = print.getValue("RH_TYPE", 0).length() == 0 ? "-"
				: print.getValue("RH_TYPE", 0);
		String rhTypeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.02.001", "HR51.03.004", rhType);
		data.setData("HR51.03.004", "TEXT", rhTypeCDAValue);
		data.setData(
				"RH_TYPE",
				"TEXT",
				print.getValue("RH_TYPE", 0).length() == 0 ? "-" : print
						.getValue("RH_TYPE", 0));// RH
		data.setData(
				"TRANS_REACTION",
				"TEXT",
				print.getValue("TRANS_REACTION", 0).length() == 0 ? "-" : print
						.getValue("TRANS_REACTION", 0));// 输血反应
		// 设置输血反应
		String transAction = print.getValue("TRANS_REACTION", 0).length() == 0 ? "-"
				: print.getValue("TRANS_REACTION", 0);
		String transActionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.09.002", "HR55.02.050", transAction);
		data.setData("HR55.02.050", "TEXT", transActionCDAValue);
		data.setData("RBC", "TEXT", print.getValue("RBC", 0));// 红血球
		data.setData("PLATE", "TEXT", print.getValue("PLATE", 0));// 血小板
		data.setData("PLASMA", "TEXT", print.getValue("PLASMA", 0));// 血浆
		data.setData("WHOLE_BLOOD", "TEXT", print.getValue("WHOLE_BLOOD", 0));// 全血
		data.setData("OTH_BLOOD", "TEXT", print.getValue("OTH_BLOOD", 0));// 其他
		data.setData(
				"SUMTOT",
				"TEXT",
				df.format(print.getDouble("CHARGE_01", 0)
						+ print.getDouble("CHARGE_02", 0)
						+ print.getDouble("CHARGE_03", 0)
						+ print.getDouble("CHARGE_04", 0)
						+ print.getDouble("CHARGE_05", 0)
						+ print.getDouble("CHARGE_06", 0)
						+ print.getDouble("CHARGE_07", 0)
						+ print.getDouble("CHARGE_08", 0)
						+ print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)
						+ print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)
						+ print.getDouble("CHARGE_14", 0)
						+ print.getDouble("CHARGE_15", 0)
						+ print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		data.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		data.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		data.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		data.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		data.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		data.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		data.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		data.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		data.setData("CHARGE_09", "TEXT",
				df.format(print.getDouble("CHARGE_09", 0)));
		data.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_10", 0)));
		data.setData("CHARGE_11", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		data.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		data.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_13", 0)));
		data.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		data.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		data.setData("CHARGE_16", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		data.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_17", 0)));
		data.setData("OP", getOP_DATA(op_date).getData());// 手术信息
		// 新生儿信息
		if (child.getCount("CASE_NO") > 0) {
			data.setData("Child_T", true);
			data.setData("C_SEX", "TEXT", childDiag.getValue("SEX", 0));
			data.setData("C_WEIGHT", "TEXT",
					this.getChildWeight(childDiag.getValue("CASE_NO", 0)));// 出生体重
			data.setData("APGAR", "TEXT", child_I.getValue("APGAR_NUMBER", 0));// APGAR评分
			// 婴儿卡介苗
			if (child_I.getBoolean("BABY_VACCINE_FLG", 0))
				data.setData("C_KJ", "TEXT", "1");
			else
				data.setData("C_KJ", "TEXT", "2");
			// 乙肝疫苗
			if (child_I.getBoolean("LIVER_VACCINE_FLG", 0))
				data.setData("C_YG", "TEXT", "1");
			else
				data.setData("C_YG", "TEXT", "2");
			// TSH
			if (child_I.getBoolean("TSH_FLG", 0))
				data.setData("C_TSH", "TEXT", "1");
			else
				data.setData("C_TSH", "TEXT", "2");
			// PKU_FLG
			if (child_I.getBoolean("PKU_FLG", 0))
				data.setData("C_PKU", "TEXT", "1");
			else
				data.setData("C_PKU", "TEXT", "2");

		} else {
			data.setData("Child_T", false);
		}
		return data;
	}

	/**
	 * 打印数据 新方法1
	 * 
	 * @param CASE_NO
	 * @return
	 */
	public TParm getNewMroRecordprintData(String CASE_NO) {
		TParm result = new TParm();
		// 获取某一病患的首页信息
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		boolean childrenFlg = false;// 新生儿注记
		childrenFlg = MRORecordTool.getInstance().getNewBornFlg(parm);

		// 判断该病患是否是产妇
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// 获取新生儿的信息 显示在母亲的病案首页上
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// 查询新生儿的首页信息
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		result.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// 组织机构代码
		// 设置身份HR56.00.002.05 CDA值
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		result.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		result.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// 病案首页身份
		result.setData("NHI_NO", "TEXT",
				this.getcheckStr(print.getValue("NHI_NO", 0)));// 健康卡号
		// 医保卡
		result.setData("MR_NO", "TEXT", print.getValue("MR_NO", 0));// 病案号
		result.setData("IPD_NO", "TEXT", print.getValue("IPD_NO", 0));// 住院号
		// 获取病患基本信息
		Pat pat = Pat.onQueryByMrNo(print.getValue("MR_NO", 0));
		result.setData("IN_COUNT", "TEXT", "第" + print.getInt("IN_COUNT", 0)
		+ "次住院");// 住院次数
		result.setData("PAT_NAME", "TEXT", print.getValue("PAT_NAME", 0));// 患者姓名
		// 病患基本信息
		result.setData("HR02.01.002", "TEXT", print.getValue("PAT_NAME", 0));// 姓名
		result.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// 性别
		// 设置性别CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		result.setData("HR02.02.001", "TEXT", sexCDAValue);
		result.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy年MM月dd日"));// 出生日期
		String[] res;
		res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
				print.getTimestamp("IN_DATE", 0));// 年龄
		// 年龄小于1周岁
		if (TypeTool.getInt(res[0]) < 1) {
			if (TypeTool.getInt(res[1]) >= 10)
				result.setData("MO", "TEXT", res[1]);// 整月数
			else
				result.setData("MO", "TEXT", " " + res[1]);// 整月数
			if (TypeTool.getInt(res[2]) >= 10)
				result.setData("CHDAY", "TEXT", res[2]);// 日数
			else
				result.setData("CHDAY", "TEXT", " " + res[2]);// 日数
			result.setData("CHCOUNT", "TEXT", "30");// 月的基数
			result.setData("AGE", "TEXT", "-");
		} else if (TypeTool.getInt(res[0]) >= 1) {
			result.setData("AGE", "TEXT", res[0] + "岁");// 整岁
			result.setData("MO", "TEXT", "-");
		}
		result.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// 国籍
		// 新生儿
		if (true) {
			parm.setData("ADM_TYPE", "I");
			String bornweight = "";// 出生体重
			String inweight = "";// 入院体重
			TParm bornWParm = SUMNewArrivalTool.getInstance().getNewBornWeight(
					parm);
			// System.out.println("------bornWParm---------------"+bornWParm);
			if (!bornWParm.getValue("NB_WEIGHT").equals(""))
				bornweight = bornWParm.getValue("NB_WEIGHT");
			else
				bornweight = "-";
			TParm inParm = SUMNewArrivalTool.getInstance()
					.getNewAdmWeight(parm);
			// System.out.println("------inParm---------------"+inParm);
			if (!inParm.getValue("NB_ADM_WEIGHT").equals(""))
				inweight = inParm.getValue("NB_ADM_WEIGHT");
			else
				inweight = "-";
			result.setData("NB_ADM_WEIGHT", "TEXT", inweight);// 新生儿入院体重
			result.setData("NB_WEIGHT", "TEXT", bornweight);// 新生儿出生体重
		}
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		result.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 7 ? HOEMPLACE.substring(0, 7) : HOEMPLACE);// 出生地
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		result.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 7 ? birthplace.substring(0, 7)
						: birthplace);// //籍贯
		result.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// 民族
		result.setData("IDNO", "TEXT",
				this.getcheckStr(print.getValue("IDNO", 0)));// 身份证
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		result.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// 职业
		result.setData("MARRIGE", "TEXT",
				this.getNewMarrige(print.getValue("MARRIGE", 0)));// 婚姻
		// 设置婚姻CDA代码
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		result.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		result.setData("ADDRESS", "TEXT",
				print.getValue("ADDRESS", 0));// 通信地址
		result.setData("POST_NO", "TEXT",
				print.getValue("POST_NO", 0));// 通信邮编
		result.setData("TEL", "TEXT",
				print.getValue("TEL", 0));// 通信电话
		result.setData(
				"OFFICE",
				"TEXT",
				print.getValue("OFFICE", 0) + "("
						+ print.getValue("O_ADDRESS", 0)
						+ ")");// 工作单位及地址
		result.setData("O_TEL", "TEXT",
				print.getValue("O_TEL", 0));// 单位电话
		result.setData("O_POSTNO", "TEXT",
				print.getValue("O_POSTNO", 0));// 单位邮编
		result.setData("H_ADDRESS", "TEXT",
				print.getValue("H_ADDRESS", 0));// 户口地址
		result.setData("H_POSTNO", "TEXT",
				print.getValue("H_POSTNO", 0));// 户口邮编
		result.setData("CONTACTER", "TEXT",
				print.getValue("CONTACTER", 0));// 联系人姓名
		result.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// 联系人关系
		result.setData("CONT_ADDRESS", "TEXT",
				print.getValue("CONT_ADDRESS", 0));// 联系人地址
		result.setData("CONT_TEL", "TEXT",
				print.getValue("CONT_TEL", 0));// 联系人电话
		/*---------------------------------基础信息部分-------------------------------------------------*/
		result.setData("ADM_SOURCE", "TEXT",
				this.getNewadmSource(print.getValue("ADM_SOURCE", 0)));// 入院途径
		result.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日 HH时"));// 入院日期
		result.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// 入院科别
		result.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
						print.getValue("IN_STATION", 0)));// 入院病房
		result.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(print.getValue("TRANS_DEPT", 0)));// 转科科别

		result.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日 HH时"));// 出院日期

		if (print.getData("OUT_DATE", 0) != null) {// 判断是否出院
			result.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // 出院科室
			result.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
							print.getValue("OUT_STATION", 0))); // 出院病室
		} else {
			result.setData("OUT_DEPT", "TEXT", "");
			result.setData("OUT_ROOM", "TEXT", "");
		}
		result.setData("REAL_STAY_DAYS", "TEXT",
				print.getValue("REAL_STAY_DAYS", 0));// 实际住院天数
		// 设置入院情况CDA代码
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		result.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		TParm diagnosis = new TParm(
				this.getDBTool()
				.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
						+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
						+ CASE_NO
						+ "' ORDER BY IO_TYPE ASC,MAIN_FLG DESC,SEQ_NO")); // 诊断记录
		int outindex=2;
		String OE_DIAG_CODE="";
		String OE_DIAG_DESC="";
		if (diagnosis.getCount() > 0) {
			for (int j = 0; j < diagnosis.getCount(); j++) {
				if (diagnosis.getValue("TYPE", j).equals("I")) {

					if (OE_DIAG_DESC.length() > 0) {// 诊断代码：（疾病代码）
						OE_DIAG_DESC += ","
								+ diagnosis.getValue(
										"NAME", j);
					}else{
						OE_DIAG_DESC = diagnosis.getValue(
								"NAME", j);
					}
					if (OE_DIAG_CODE.length() > 0) {
						OE_DIAG_CODE += ","
								+ diagnosis.getValue("CODE", j);
					}else{
						OE_DIAG_CODE = diagnosis.getValue("CODE", j);
					}
					result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
					result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
				}
				if (diagnosis.getValue("TYPE", j).equals("O")) {
					if(diagnosis.getValue("MAIN", j).equals("Y")){
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // 出院主诊断疾病编码
						result.setData("DIAG1", "TEXT",
								diagnosis.getValue("NAME", j)); // 出院主诊断疾病名称
						result.setData("DIAG_TYPE1", "TEXT",""); // 出院主诊断疾病编码
						result.setData("DIAG_CONDITION1", "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
					}else{
						result.setData("DIAG_CODE"+outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // 出院诊断疾病编码
						result.setData("DIAG"+outindex, "TEXT",
								diagnosis.getValue("NAME", j)); // 出院诊断疾病名称
						if(outindex==2||outindex==8)
							result.setData("DIAG_TYPE"+outindex, "TEXT","其他诊断:"); // 出院诊断疾病编码
						else
							result.setData("DIAG_TYPE"+outindex, "TEXT",""); // 出院诊断疾病编码	
						result.setData("DIAG_CONDITION"+outindex, "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
						outindex++;
					}	
				}
				if (diagnosis.getValue("TYPE", j).equals("Q")) {
					result.setData("DIAG_CODE"+outindex, "TEXT",diagnosis.getValue("CODE", j)); // 感染诊断疾病编码
					result.setData("DIAG"+outindex, "TEXT",diagnosis.getValue("NAME", j)); // 感染诊断疾病名称
					result.setData("DIAG_TYPE"+outindex, "TEXT","感染诊断:"); // 感染诊断疾病编码
					result.setData("DIAG_CONDITION"+outindex, "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 感染诊断入院病情
					outindex++;
				}	
			}
		}
		if(diagnosis.getErrCode()<0){
			System.out.println("查询MRO_RECORD_DIAG病案诊断数据错误！");
		}
		// shibl 20120618 modify
		// String OE_DIAG_DESC = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		// String OE_DIAG_CODE = print.getValue("OE_DIAG_CODE", 0);
		// if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// 诊断代码：（疾病代码）
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE2", 0);
		// }
		// if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE3", 0);
		// }
		// result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
		// result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
		// /*---------------------------------住院部分-----------------------------------------------------*/
		// result.setData("DIAG1", "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
		// // result.setData("DIAG2", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
		// // result.setData("DIAG3", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3
		// // result.setData("DIAG4", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // 出院诊断4
		// // result.setData("DIAG5", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // 出院诊断5
		// // result.setData("DIAG6", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // 出院诊断6
		// result.setData("DIAG_CODE1", "TEXT",
		// print.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断疾病编码
		// // result.setData("DIAG_CODE2", "TEXT",
		// // print.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2疾病编码
		// // result.setData("DIAG_CODE3", "TEXT",
		// // print.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3疾病编码
		// // result.setData("DIAG_CODE4", "TEXT",
		// // print.getValue("OUT_DIAG_CODE4", 0)); // 出院诊断4疾病编码
		// // result.setData("DIAG_CODE5", "TEXT",
		// // print.getValue("OUT_DIAG_CODE5", 0)); // 出院诊断5疾病编码
		// // result.setData("DIAG_CODE6", "TEXT",
		// // print.getValue("OUT_DIAG_CODE6", 0)); // 出院诊断6疾病编码
		// result.setData("DIAG_CONDITION1", "TEXT",
		// print.getValue("OUT_DIAG_CONDITION1", 0)); // 出院主诊断入院病情
		// // result.setData("DIAG_CONDITION2", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION2", 0)); // 出院诊断2入院病情
		// // result.setData("DIAG_CONDITION3", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION3", 0)); // 出院诊断3入院病情
		// // result.setData("DIAG_CONDITION4", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION4", 0)); // 出院诊断4入院病情
		// // result.setData("DIAG_CONDITION5", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION5", 0)); // 出院诊断5入院病情
		// // result.setData("DIAG_CONDITION6", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION6", 0)); // 出院诊断6入院病情
		// int seq = 2;
		// for (int i = 2; i < 7; i++) {
		// if (!print.getValue("OUT_DIAG_CODE" + i, 0).equals("")) {
		// result.setData("DIAG" + seq, "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE" + i, 0)));
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CODE" + i, 0));
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CONDITION" + i, 0));
		// if (seq == 2)
		// result.setData("DIAG_TYPE" + seq, "TEXT", "其他诊断:");
		// seq++;
		// }
		// }
		// String INTE_DIAG_CODE = getICD_DESC(print.getValue(
		// "INTE_DIAG_CODE", 0));
		// result.setData("DIAG" + seq, "TEXT", INTE_DIAG_CODE);// 院内感染诊断
		// // 待确定
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CODE", 0));// 院内感染诊断CODE
		// // 待确定
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CONDITION", 0));// 院内感染诊断入院病情
		// result.setData("DIAG_TYPE" + seq, "TEXT", "感染诊断:");
		/*----------------------------------------诊断表---------------------------------------------------*/
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		result.setData("PATHOLOGY_DIAG", "TEXT",
				PATHOLOGY_DIAG);// 病理诊断
		result.setData("PATHOLOGY_DIAG_CODE", "TEXT",
				print.getValue("PATHOLOGY_DIAG", 0));// 病理诊断疾病编码
		result.setData("PATHOLOGY_NO", "TEXT",
				print.getValue("PATHOLOGY_NO", 0));// 病理号

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		result.setData("EX_RSN", "TEXT", EX_RSN);// 损伤、中毒的外部因素
		result.setData("EX_RSN_CODE", "TEXT",
				print.getValue("EX_RSN", 0));// 损伤、中毒的外部因素疾病编码

		result.setData("ALLEGIC_CODE", "TEXT", this.getcheckStr(print.getValue("ALLEGIC_FLG", 0)));//是否有药物过敏
		result.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// 药物过敏
		result.setData("BODY_CHECK", "TEXT",
				this.getcheckStr(print.getValue("BODY_CHECK", 0)));// 尸检
		result.setData("BLOOD_TYPE", "TEXT", print.getValue("BLOOD_TYPE", 0));// 血型
		// 设置RH
		String rhType = print.getValue("RH_TYPE", 0);
		result.setData("RH_TYPE", "TEXT", rhType);// RH
		result.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// 科主任
		result.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// 主任医师
		result.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// 主治医师
		result.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// 住院医师
		result.setData("INDUCATION_DR_CODE", "TEXT",
				drList.get(print.getValue("INDUCATION_DR_CODE", 0)));// 进修医师
		result.setData("VS_NURSE_CODE", "TEXT",
				drList.get(print.getValue("VS_NURSE_CODE", 0)));// 责任护士
		result.setData("INTERN_DR_CODE", "TEXT",
				drList.get(print.getValue("INTERN_DR_CODE", 0)));// 实习医师
		result.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// 编码员
		result.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// 病案质量
		// 设置病案质量
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		result.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		result.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// 质控医师
		result.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// 质控护士
		result.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy年MM月dd日"));// 质控日期
		/*-------------------------------------------------------------------------------------------*/
		// 查询手术信息
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		// System.out.println("------op_date---------"+op_date);
		TParm anaParm = getOP_DATA(op_date);
		// System.out.println("-=-=------------------"+anaParm);
		int index = 2;
		for (int i = 0; i < op_date.getCount(); i++) {
			if (op_date.getValue("MAIN_FLG", i).equals("Y")) {
				result.setData("OPE_CODE1", "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE1", "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL1", "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC1", "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if(op_date.getValue("MAIN_SUGEON_REMARK", i) != null &&
						!op_date.getValue("MAIN_SUGEON_REMARK", i).equals("")){
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				}else{
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR11", "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR21", "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL1", "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY1", "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR1", "TEXT", op_date.getValue("ANA_DR", i));// 麻醉师
			} else {
				result.setData("OPE_CODE" + index, "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE" + index, "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL" + index, "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC" + index, "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if(op_date.getValue("MAIN_SUGEON_REMARK", i) != null &&
						!op_date.getValue("MAIN_SUGEON_REMARK", i).equals("")){
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				}else{
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR1" + index, "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR2" + index, "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL" + index, "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY" + index, "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR" + index, "TEXT",
						op_date.getValue("ANA_DR", i));// 麻醉师
				index++;
			}
		}
		result.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// 离院方式
		if (print.getValue("OUT_TYPE", 0).equals("2"))
			result.setData(
					"TRAN_HOSP1",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999")?print.getValue("TRAN_HOSP_OTHER", 0):
						getDesc("SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
								print.getValue("TRAN_HOSP", 0)));// 外转院区     其他999999可以自定义  20120918 shibl 
		if (print.getValue("OUT_TYPE", 0).equals("3"))
			result.setData(
					"TRAN_HOSP2",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999")?print.getValue("TRAN_HOSP_OTHER", 0):
						getDesc("SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
								print.getValue("TRAN_HOSP", 0)));// 外转社区   其他999999可以自定义  20120918 shibl 
		if (print.getValue("BE_COMA_TIME", 0).equals("")) {
			result.setData("BE_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院前昏迷时间
		} else {
			result.setData(
					"BE_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("BE_COMA_TIME", 0)
							.substring(0, 2))
					+ "天"
					+ Integer.parseInt(print
							.getValue("BE_COMA_TIME", 0)
							.substring(2, 4))
					+ "小时"
					+ Integer.parseInt(print
							.getValue("BE_COMA_TIME", 0)
							.substring(4, 6)) + "分钟");// 入院前昏迷时间
		}
		if (print.getValue("AF_COMA_TIME", 0).equals("")) {
			result.setData("AF_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院后昏迷时间
		} else {
			result.setData(
					"AF_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("AF_COMA_TIME", 0)
							.substring(0, 2))
					+ "天"
					+ Integer.parseInt(print
							.getValue("AF_COMA_TIME", 0)
							.substring(2, 4))
					+ "小时"
					+ Integer.parseInt(print
							.getValue("AF_COMA_TIME", 0)
							.substring(4, 6)) + "分钟");// 入院后昏迷时间
		}
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		result.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31天计划标记
		result.setData("AGN_PLAN_INTENTION", "TEXT",
				this.getcheckStr(print.getValue("AGN_PLAN_INTENTION", 0)));// 31天计划原因
		/*------------------------------------费用待确定------------------------------------------------*/
		DecimalFormat df = new DecimalFormat("0.00");
		result.setData("SUMTOT", "TEXT",
				df.format(print.getDouble("SUM_TOT", 0)));
		result.setData("OWN_TOT", "TEXT",
				df.format(print.getDouble("OWN_TOT", 0)));
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		// 一般医疗服务费
		result.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		// 一般治疗操作费
		result.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		// 护理费
		result.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		// 其他费用
		result.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		// 病理诊断费
		result.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		// 实验室诊断费
		result.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		// 影像学诊断费
		result.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		// 临床诊断项目费
		result.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		// 非手术治疗费用
		result.setData(
				"CHARGE_09",
				"TEXT",
				df.format(print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)));
		// 临床物理治疗费
		result.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_9", 0)));
		// 手术治疗费
		result.setData(
				"CHARGE_11",
				"TEXT",
				df.format(print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)));
		// 手术治疗费-麻醉费
		result.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		// 手术治疗费-手术费
		result.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		// 康复费
		result.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		// 中医治疗费
		result.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		// 西药费用
		result.setData(
				"CHARGE_16",
				"TEXT",
				df.format(print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		// 抗菌药物费用
		result.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		// 中成药费
		result.setData("CHARGE_18", "TEXT",
				df.format(print.getDouble("CHARGE_18", 0)));
		// 中草药费
		result.setData("CHARGE_19", "TEXT",
				df.format(print.getDouble("CHARGE_19", 0)));
		// 血费
		result.setData("CHARGE_20", "TEXT",
				df.format(print.getDouble("CHARGE_20", 0)));
		// 白蛋白类制品费
		result.setData("CHARGE_21", "TEXT",
				df.format(print.getDouble("CHARGE_21", 0)));
		// 球蛋白类制品费
		result.setData("CHARGE_22", "TEXT",
				df.format(print.getDouble("CHARGE_22", 0)));
		// 凝血因子类制品费
		result.setData("CHARGE_23", "TEXT",
				df.format(print.getDouble("CHARGE_23", 0)));
		// 细胞因子类制品费
		result.setData("CHARGE_24", "TEXT",
				df.format(print.getDouble("CHARGE_24", 0)));
		// 检查用一次性医用材料费
		result.setData("CHARGE_25", "TEXT",
				df.format(print.getDouble("CHARGE_25", 0)));
		// 治疗用一次性医用材料费
		result.setData("CHARGE_26", "TEXT",
				df.format(print.getDouble("CHARGE_26", 0)));
		// 手术用一次性医用材料费
		result.setData("CHARGE_27", "TEXT",
				df.format(print.getDouble("CHARGE_27", 0)));
		// 其他费
		result.setData("CHARGE_28", "TEXT",
				df.format(print.getDouble("CHARGE_28", 0)));

		//2013-5-23 zhangh modify 增加重症监护表格和呼吸机使用时间
		result.setData("VENTI_TIME", "TEXT", print.getData("VENTI_TIME", 0));//呼吸机使用时间
		//duzhw add 20140423 增加肿瘤分期、护理评分
		result.setData("TUMOR_STAG_T", "TEXT", print.getData("TUMOR_STAG_T", 0));			//肿瘤分期T
		result.setData("TUMOR_STAG_N", "TEXT", print.getData("TUMOR_STAG_N", 0));			//肿瘤分期N
		result.setData("TUMOR_STAG_M", "TEXT", print.getData("TUMOR_STAG_M", 0));			//肿瘤分期M
		result.setData("NURSING_GRAD_IN", "TEXT", print.getData("NURSING_GRAD_IN", 0));		//护理评分 入院
		result.setData("NURSING_GRAD_OUT", "TEXT", print.getData("NURSING_GRAD_OUT", 0));	//护理评分 出院
		//重症监护表格数据
		for (int i = 1; i < 6; i++) {
			String inDate = "",icuInDate = "",outDate = "",icuOutDate = "";
			if(print.getData("ICU_IN_DATE" + i, 0) != null){
				inDate = print.getData("ICU_IN_DATE" + i, 0).toString().
						substring(0, print.getData("ICU_IN_DATE" + i, 0).toString().lastIndexOf("."))
						.replace("-", "/");//得到数据库中的进入时间
			}
			if(print.getData("ICU_OUT_DATE" + i, 0) != null){
				outDate = print.getData("ICU_OUT_DATE" + i, 0).toString().
						substring(0, print.getData("ICU_OUT_DATE" + i, 0).toString().lastIndexOf("."))
						.replace("-", "/");//得到数据库中的退出时间
			}
			icuInDate = getInOutDate(inDate);
			icuOutDate = getInOutDate(outDate);
			//获取房间具体名称
			String deptCode =getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
					print.getValue("ICU_ROOM" + i, 0));
			//			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			result.setData("IN_DATE_" + i, "TEXT", icuInDate);
			result.setData("OUT_DATE_" + i, "TEXT", icuOutDate);
			result.setData("ICU_ROOM_" + i, "TEXT", deptCode);
		}
		return result;
	}

	public TParm getNewMroRecordprintData(TParm parm){
		String CASE_NO = parm.getValue("CASE_NO");
		String MR_NO = parm.getValue("MR_NO");
		Object realStayDays = parm.getData("TP2_REAL_STAY_DAYS");
		TParm result = new TParm();
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		boolean childrenFlg = false;// 新生儿注记
		childrenFlg = MRORecordTool.getInstance().getNewBornFlg(parm);

		// 判断该病患是否是产妇
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// 获取新生儿的信息 显示在母亲的病案首页上

			//modify yangjj 20150602
			for(int i = 0 ; i < child.getCount() ; i++){
				TParm ch = new TParm();
				ch.setData("CASE_NO", child.getValue("CASE_NO", i));
				child_I.addRowData(ADMChildImmunityTool.getInstance().selectData(ch), 0) ;
			}

		}
		// 查询新生儿的首页信息
		TParm childParm = new TParm();

		//modify by yangjj 20150602
		TParm childDiag = new TParm();
		for(int i = 0 ; i < child.getCount() ; i++){
			childParm.setData("CASE_NO", child.getValue("CASE_NO", i));
			childDiag.addRowData(MRORecordTool.getInstance().getInHospInfo(childParm), 0); 
		}



		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		result.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// 组织机构代码
		// 设置身份HR56.00.002.05 CDA值
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		result.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		result.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// 病案首页身份
		result.setData("NHI_NO", "TEXT",
				this.getcheckStr(print.getValue("NHI_NO", 0)));// 健康卡号
		// 医保卡
		result.setData("MR_NO", "TEXT", print.getValue("MR_NO", 0));// 病案号
		result.setData("IPD_NO", "TEXT", print.getValue("IPD_NO", 0));// 住院号
		// 获取病患基本信息
		Pat pat = Pat.onQueryByMrNo(print.getValue("MR_NO", 0));
		result.setData("IN_COUNT", "TEXT", "第" + print.getInt("IN_COUNT", 0)
		+ "次住院");// 住院次数
		result.setData("PAT_NAME", "TEXT", print.getValue("PAT_NAME", 0));// 患者姓名
		// 病患基本信息
		result.setData("HR02.01.002", "TEXT", print.getValue("PAT_NAME", 0));// 姓名
		result.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// 性别
		//add by sunqy 20140627 ----start----
		String sqlSex = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID = '"+ print.getValue("SEX", 0) +"'";
		TParm resultSql = new TParm(TJDODBTool.getInstance().select(sqlSex));
		String sexCode = resultSql.getRow(0).getValue("CHN_DESC");
		result.setData("filePatSex", "TEXT", sexCode);//病案首页处性别
		result.setData("BIRTHDAY", "TEXT", StringTool.getString(print.getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));//病案首页处出生日期
		//add by sunqy 20140627 ----end----
		// 设置性别CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		result.setData("HR02.02.001", "TEXT", sexCDAValue);
		result.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy年MM月dd日"));// 出生日期
		String[] res;
		res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
				print.getTimestamp("IN_DATE", 0));// 年龄
		// 年龄小于1周岁
		if (TypeTool.getInt(res[0]) < 1) {
			if (TypeTool.getInt(res[1]) >= 10)
				result.setData("MO", "TEXT", res[1]);// 整月数
			else
				result.setData("MO", "TEXT", " " + res[1]);// 整月数
			if (TypeTool.getInt(res[2]) >= 10)
				result.setData("CHDAY", "TEXT", res[2]);// 日数
			else
				result.setData("CHDAY", "TEXT", " " + res[2]);// 日数
			result.setData("CHCOUNT", "TEXT", "30");// 月的基数
			result.setData("AGE", "TEXT", "-");
		} else if (TypeTool.getInt(res[0]) >= 1) {
			result.setData("AGE", "TEXT", res[0] + "岁");// 整岁
			result.setData("MO", "TEXT", "-");
		}
		result.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// 国籍

		// add by wangqing 20180105 start
		// 1、入院体重
		String inweight = "";
		inweight = print.getValue("NB_ADM_WEIGHT",0);			
		if(inweight==null || inweight.trim().length()<=0 || stringToDouble(inweight)==0){
			TParm parm1 = new TParm();
			parm1.setData("CASE_NO", CASE_NO);
			parm1.setData("ADM_TYPE", "I");
			TParm inParm = SUMNewArrivalTool.getInstance().getFirstDayWeight(parm1);
			inweight = inParm.getValue("NB_ADM_WEIGHT");
			if(inweight==null || inweight.trim().length()<=0 || stringToDouble(inweight)==0){
				inweight = "-";
			}			
		}	
		result.setData("NB_ADM_WEIGHT", "TEXT", inweight);// 新生儿入院体重
		// 2、出生体重
		String bornweight = "";
		if(isNewBaby(CASE_NO)) {
			bornweight = print.getValue("NB_WEIGHT", 0);
			if(bornweight!=null && bornweight.trim().length()>0){
				bornweight = Math.round(print.getDouble("NB_WEIGHT",0))+"";
			}else{
				String sqlWeight = "SELECT BORNWEIGHT,EXAMINE_DATE FROM SUM_NEWARRIVALSIGN "+
						" WHERE  CASE_NO = '"+CASE_NO+"' AND "+
						" ADM_TYPE= 'I' "+
						" ORDER BY TO_DATE(EXAMINE_DATE,'YYYYMMDD')";
				TParm pp = new TParm(TJDODBTool.getInstance().select(sqlWeight));
				bornweight = pp.getValue("BORNWEIGHT",0);
				if(bornweight!=null && bornweight.trim().length()>0){
					bornweight = Math.round(pp.getDouble("BORNWEIGHT",0))+"";
				}else{
					String sql = "SELECT * FROM SYS_PATINFO WHERE MR_NO = '"+MR_NO+"'";
					TParm p = new TParm(TJDODBTool.getInstance().select(sql));
					bornweight = p.getValue("NEW_BODY_WEIGHT",0);
					if(bornweight!=null && bornweight.trim().length()>0){
						bornweight = Math.round(p.getDouble("NEW_BODY_WEIGHT",0))+"";
					}else{
						bornweight = "-";
					}				
				}
			}	
		}else{
			bornweight = "";
			String sql = "SELECT * "+
					"FROM ADM_INP A, SYS_PATINFO B "+
					"WHERE A.MR_NO = B.MR_NO AND A.M_CASE_NO = '"+CASE_NO+"' "+
					"ORDER BY A.MR_NO ASC";
			TParm p = new TParm(TJDODBTool.getInstance().select(sql));
			int len = p.getCount("CASE_NO");
			for(int i = 0;i<len;i++){
				bornweight +=Math.round(p.getDouble("NEW_BODY_WEIGHT",i))+"/";
			}
			if(bornweight.length()>0){
				bornweight = bornweight.substring(0, bornweight.length()-1);
			}
			if(bornweight!=null && bornweight.trim().length()>0){
				
			}else{				
				bornweight = "-";
			}
		}
		result.setData("NB_WEIGHT", "TEXT", bornweight);// 新生儿出生体重
		// add by wangqing 20180105 end

		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		result.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 7 ? HOEMPLACE.substring(0, 7) : HOEMPLACE);// 出生地
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		result.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 7 ? birthplace.substring(0, 7)
						: birthplace);// //籍贯
		result.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// 民族
		result.setData("IDNO", "TEXT",
				this.getcheckStr(print.getValue("IDNO", 0)));// 身份证
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		result.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// 职业
		result.setData("MARRIGE", "TEXT",
				this.getNewMarrige(print.getValue("MARRIGE", 0)));// 婚姻
		// 设置婚姻CDA代码
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		result.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		result.setData("ADDRESS", "TEXT",
				print.getValue("ADDRESS", 0));// 通信地址
		result.setData("POST_NO", "TEXT",
				print.getValue("POST_NO", 0));// 通信邮编
		result.setData("TEL", "TEXT",
				print.getValue("TEL", 0));// 通信电话
		result.setData(
				"OFFICE",
				"TEXT",
				print.getValue("OFFICE", 0) + "("
						+ print.getValue("O_ADDRESS", 0)
						+ ")");// 工作单位及地址
		result.setData("O_TEL", "TEXT",
				print.getValue("O_TEL", 0));// 单位电话
		result.setData("O_POSTNO", "TEXT",
				print.getValue("O_POSTNO", 0));// 单位邮编
		result.setData("H_ADDRESS", "TEXT",
				print.getValue("H_ADDRESS", 0));// 户口地址
		result.setData("H_POSTNO", "TEXT",
				print.getValue("H_POSTNO", 0));// 户口邮编
		result.setData("CONTACTER", "TEXT",
				print.getValue("CONTACTER", 0));// 联系人姓名
		result.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// 联系人关系
		result.setData("CONT_ADDRESS", "TEXT",
				print.getValue("CONT_ADDRESS", 0));// 联系人地址
		result.setData("CONT_TEL", "TEXT",
				print.getValue("CONT_TEL", 0));// 联系人电话
		/*---------------------------------基础信息部分-------------------------------------------------*/
		result.setData("ADM_SOURCE", "TEXT",
				this.getNewadmSource(print.getValue("ADM_SOURCE", 0)));// 入院途径
		result.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日 HH时"));// 入院日期
		result.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// 入院科别
		result.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
						print.getValue("IN_STATION", 0)));// 入院病房
		result.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(print.getValue("TRANS_DEPT", 0)));// 转科科别

		result.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日 HH时"));// 出院日期

		if (print.getData("OUT_DATE", 0) != null) {// 判断是否出院
			result.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // 出院科室
			result.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
							print.getValue("OUT_STATION", 0))); // 出院病室
		} else {
			result.setData("OUT_DEPT", "TEXT", "");
			result.setData("OUT_ROOM", "TEXT", "");
		}
		result.setData("REAL_STAY_DAYS", "TEXT",
				realStayDays);// 实际住院天数
		// 设置入院情况CDA代码
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		result.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		TParm diagnosis = new TParm(
				this.getDBTool()
				.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
						+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
						+ CASE_NO
						+ "' ORDER BY DECODE(IO_TYPE,'I','1','O','2','Q','3','W','4',IO_TYPE),MAIN_FLG DESC,SEQ_NO")); // 诊断记录 modify by wanglong 20140411
		int outindex=2;
		String OE_DIAG_CODE="";
		String OE_DIAG_DESC="";
		if (diagnosis.getCount() > 0) {
			for (int j = 0; j < diagnosis.getCount(); j++) {
				if (diagnosis.getValue("TYPE", j).equals("I")) {

					if (OE_DIAG_DESC.length() > 0) {// 诊断代码：（疾病代码）
						OE_DIAG_DESC += ","
								+ diagnosis.getValue(
										"NAME", j);
					}else{
						OE_DIAG_DESC = diagnosis.getValue(
								"NAME", j);
					}
					if (OE_DIAG_CODE.length() > 0) {
						OE_DIAG_CODE += ","
								+ diagnosis.getValue("CODE", j);
					}else{
						OE_DIAG_CODE = diagnosis.getValue("CODE", j);
					}
					result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
					result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
				}
				if (diagnosis.getValue("TYPE", j).equals("O")) {
					if(diagnosis.getValue("MAIN", j).equals("Y")){
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // 出院主诊断疾病编码
						result.setData("DIAG1", "TEXT",
								diagnosis.getValue("NAME", j)); // 出院主诊断疾病名称
						result.setData("DIAG_TYPE1", "TEXT",""); // 出院主诊断疾病编码
						result.setData("DIAG_CONDITION1", "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
					}else{
						result.setData("DIAG_CODE"+outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // 出院诊断疾病编码
						result.setData("DIAG"+outindex, "TEXT",
								diagnosis.getValue("NAME", j)); // 出院诊断疾病名称
						if(outindex==2||outindex==8)
							result.setData("DIAG_TYPE"+outindex, "TEXT","其他诊断:"); // 出院诊断疾病编码
						else
							result.setData("DIAG_TYPE"+outindex, "TEXT",""); // 出院诊断疾病编码	
						result.setData("DIAG_CONDITION"+outindex, "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
						outindex++;
					}
				}
				if (diagnosis.getValue("TYPE", j).equals("Q")) {
					result.setData("DIAG_CODE"+outindex, "TEXT",diagnosis.getValue("CODE", j)); // 感染诊断疾病编码
					result.setData("DIAG"+outindex, "TEXT",diagnosis.getValue("NAME", j)); // 感染诊断疾病名称
					result.setData("DIAG_TYPE"+outindex, "TEXT","感染诊断:"); // 感染诊断疾病编码
					result.setData("DIAG_CONDITION"+outindex, "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 感染诊断入院病情
					outindex++;
				}
				if (diagnosis.getValue("TYPE", j).equals("W")) {//并发症诊断 add by wanglong 20140411
					result.setData("DIAG_CODE"+outindex, "TEXT",diagnosis.getValue("CODE", j)); // 疾病编码
					result.setData("DIAG"+outindex, "TEXT","  "+diagnosis.getValue("NAME", j)); // 疾病名称
					result.setData("DIAG_TYPE"+outindex, "TEXT","并发症诊断:"); // 疾病编码
					result.setData("DIAG_CONDITION"+outindex, "TEXT",diagnosis.getValue("IN_PAT_CONDITION", j)); // 入院病情
					outindex++;
				}
			}
		}
		if(diagnosis.getErrCode()<0){
			System.out.println("查询MRO_RECORD_DIAG病案诊断数据错误！");
		}
		// shibl 20120618 modify
		// String OE_DIAG_DESC = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		// String OE_DIAG_CODE = print.getValue("OE_DIAG_CODE", 0);
		// if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// 诊断代码：（疾病代码）
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE2", 0);
		// }
		// if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE3", 0);
		// }
		// result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
		// result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
		// /*---------------------------------住院部分-----------------------------------------------------*/
		// result.setData("DIAG1", "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
		// // result.setData("DIAG2", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
		// // result.setData("DIAG3", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3
		// // result.setData("DIAG4", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // 出院诊断4
		// // result.setData("DIAG5", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // 出院诊断5
		// // result.setData("DIAG6", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // 出院诊断6
		// result.setData("DIAG_CODE1", "TEXT",
		// print.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断疾病编码
		// // result.setData("DIAG_CODE2", "TEXT",
		// // print.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2疾病编码
		// // result.setData("DIAG_CODE3", "TEXT",
		// // print.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3疾病编码
		// // result.setData("DIAG_CODE4", "TEXT",
		// // print.getValue("OUT_DIAG_CODE4", 0)); // 出院诊断4疾病编码
		// // result.setData("DIAG_CODE5", "TEXT",
		// // print.getValue("OUT_DIAG_CODE5", 0)); // 出院诊断5疾病编码
		// // result.setData("DIAG_CODE6", "TEXT",
		// // print.getValue("OUT_DIAG_CODE6", 0)); // 出院诊断6疾病编码
		// result.setData("DIAG_CONDITION1", "TEXT",
		// print.getValue("OUT_DIAG_CONDITION1", 0)); // 出院主诊断入院病情
		// // result.setData("DIAG_CONDITION2", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION2", 0)); // 出院诊断2入院病情
		// // result.setData("DIAG_CONDITION3", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION3", 0)); // 出院诊断3入院病情
		// // result.setData("DIAG_CONDITION4", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION4", 0)); // 出院诊断4入院病情
		// // result.setData("DIAG_CONDITION5", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION5", 0)); // 出院诊断5入院病情
		// // result.setData("DIAG_CONDITION6", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION6", 0)); // 出院诊断6入院病情
		// int seq = 2;
		// for (int i = 2; i < 7; i++) {
		// if (!print.getValue("OUT_DIAG_CODE" + i, 0).equals("")) {
		// result.setData("DIAG" + seq, "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE" + i, 0)));
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CODE" + i, 0));
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CONDITION" + i, 0));
		// if (seq == 2)
		// result.setData("DIAG_TYPE" + seq, "TEXT", "其他诊断:");
		// seq++;
		// }
		// }
		// String INTE_DIAG_CODE = getICD_DESC(print.getValue(
		// "INTE_DIAG_CODE", 0));
		// result.setData("DIAG" + seq, "TEXT", INTE_DIAG_CODE);// 院内感染诊断
		// // 待确定
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CODE", 0));// 院内感染诊断CODE
		// // 待确定
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CONDITION", 0));// 院内感染诊断入院病情
		// result.setData("DIAG_TYPE" + seq, "TEXT", "感染诊断:");
		/*----------------------------------------诊断表---------------------------------------------------*/
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		result.setData("PATHOLOGY_DIAG", "TEXT",
				PATHOLOGY_DIAG);// 病理诊断
		result.setData("PATHOLOGY_DIAG_CODE", "TEXT",
				print.getValue("PATHOLOGY_DIAG", 0));// 病理诊断疾病编码
		result.setData("PATHOLOGY_NO", "TEXT",
				print.getValue("PATHOLOGY_NO", 0));// 病理号

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		result.setData("EX_RSN", "TEXT", EX_RSN);// 损伤、中毒的外部因素
		result.setData("EX_RSN_CODE", "TEXT",
				print.getValue("EX_RSN", 0));// 损伤、中毒的外部因素疾病编码

		result.setData("ALLEGIC_CODE", "TEXT", this.getcheckStr(print.getValue("ALLEGIC_FLG", 0)));//是否有药物过敏
		result.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// 药物过敏
		result.setData("BODY_CHECK", "TEXT",
				this.getcheckStr(print.getValue("BODY_CHECK", 0)));// 尸检
		result.setData("BLOOD_TYPE", "TEXT", print.getValue("BLOOD_TYPE", 0));// 血型
		// 设置RH
		String rhType = print.getValue("RH_TYPE", 0);
		result.setData("RH_TYPE", "TEXT", rhType);// RH
		result.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// 科主任
		result.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// 主任医师
		result.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// 主治医师
		result.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// 住院医师
		result.setData("INDUCATION_DR_CODE", "TEXT",
				drList.get(print.getValue("INDUCATION_DR_CODE", 0)));// 进修医师
		result.setData("VS_NURSE_CODE", "TEXT",
				drList.get(print.getValue("VS_NURSE_CODE", 0)));// 责任护士
		result.setData("INTERN_DR_CODE", "TEXT",
				drList.get(print.getValue("INTERN_DR_CODE", 0)));// 实习医师
		result.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// 编码员
		result.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// 病案质量
		// 设置病案质量
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		result.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		result.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// 质控医师
		result.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// 质控护士
		result.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy年MM月dd日"));// 质控日期
		/*-------------------------------------------------------------------------------------------*/
		// 查询手术信息
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		// System.out.println("------op_date---------"+op_date);
		TParm anaParm = getOP_DATA(op_date);
		// System.out.println("-=-=------------------"+anaParm);
		int index = 2;
		for (int i = 0; i < op_date.getCount(); i++) {
			if (op_date.getValue("MAIN_FLG", i).equals("Y")) {
				result.setData("OPE_CODE1", "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE1", "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL1", "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC1", "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if(op_date.getValue("MAIN_SUGEON_REMARK", i) != null &&
						!op_date.getValue("MAIN_SUGEON_REMARK", i).equals("")){
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				}else{
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}

				//add by yangjj 20150526
				String astDr11 = "";
				if("".equals(op_date.getValue("AST_DR1", i)) || op_date.getValue("AST_DR1", i) == null){
					astDr11 = "   -   ";
				}else{
					astDr11 = op_date.getValue("AST_DR1", i);
				}
				result.setData("AST_DR11", "TEXT",
						astDr11);// 助手1

				String astDr21 = "";
				if("".equals(op_date.getValue("AST_DR2", i)) || op_date.getValue("AST_DR2", i) == null){
					astDr21 = "   -   ";
				}else{
					astDr21 = op_date.getValue("AST_DR2", i);
				}
				result.setData("AST_DR21", "TEXT",
						astDr21);// 助手2


				result.setData("HEL1", "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY1", "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR1", "TEXT", op_date.getValue("ANA_DR", i));// 麻醉师
			} else {
				result.setData("OPE_CODE" + index, "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE" + index, "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL" + index, "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC" + index, "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if(op_date.getValue("MAIN_SUGEON_REMARK", i) != null &&
						!op_date.getValue("MAIN_SUGEON_REMARK", i).equals("")){
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				}else{
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR1" + index, "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR2" + index, "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL" + index, "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY" + index, "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR" + index, "TEXT",
						op_date.getValue("ANA_DR", i));// 麻醉师
				index++;
			}
		}
		result.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// 离院方式
		if (print.getValue("OUT_TYPE", 0).equals("2"))
			result.setData(
					"TRAN_HOSP1",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999")?print.getValue("TRAN_HOSP_OTHER", 0):
						getDesc("SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
								print.getValue("TRAN_HOSP", 0)));// 外转院区     其他999999可以自定义  20120918 shibl 
		if (print.getValue("OUT_TYPE", 0).equals("3"))
			result.setData(
					"TRAN_HOSP2",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999")?print.getValue("TRAN_HOSP_OTHER", 0):
						getDesc("SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
								print.getValue("TRAN_HOSP", 0)));// 外转社区   其他999999可以自定义  20120918 shibl 
		if (print.getValue("BE_COMA_TIME", 0).equals("")) {
			result.setData("BE_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院前昏迷时间
		} else {
			result.setData(
					"BE_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("BE_COMA_TIME", 0)
							.substring(0, 2))
					+ "天"
					+ Integer.parseInt(print
							.getValue("BE_COMA_TIME", 0)
							.substring(2, 4))
					+ "小时"
					+ Integer.parseInt(print
							.getValue("BE_COMA_TIME", 0)
							.substring(4, 6)) + "分钟");// 入院前昏迷时间
		}
		if (print.getValue("AF_COMA_TIME", 0).equals("")) {
			result.setData("AF_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院后昏迷时间
		} else {
			result.setData(
					"AF_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("AF_COMA_TIME", 0)
							.substring(0, 2))
					+ "天"
					+ Integer.parseInt(print
							.getValue("AF_COMA_TIME", 0)
							.substring(2, 4))
					+ "小时"
					+ Integer.parseInt(print
							.getValue("AF_COMA_TIME", 0)
							.substring(4, 6)) + "分钟");// 入院后昏迷时间
		}
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		result.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31天计划标记
		result.setData("AGN_PLAN_INTENTION", "TEXT",
				this.getcheckStr(print.getValue("AGN_PLAN_INTENTION", 0)));// 31天计划原因
		/*------------------------------------费用待确定------------------------------------------------*/
		DecimalFormat df = new DecimalFormat("0.00");
		result.setData("SUMTOT", "TEXT",
				df.format(print.getDouble("SUM_TOT", 0)));
		result.setData("OWN_TOT", "TEXT",
				df.format(print.getDouble("OWN_TOT", 0)));
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		// 一般医疗服务费
		result.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		// 一般治疗操作费
		result.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		// 护理费
		result.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		// 其他费用
		result.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		// 病理诊断费
		result.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		// 实验室诊断费
		result.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		// 影像学诊断费
		result.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		// 临床诊断项目费
		result.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		// 非手术治疗费用
		result.setData(
				"CHARGE_09",
				"TEXT",
				df.format(print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)));
		// 临床物理治疗费
		result.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_9", 0)));
		// 手术治疗费
		result.setData(
				"CHARGE_11",
				"TEXT",
				df.format(print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)));
		// 手术治疗费-麻醉费
		result.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		// 手术治疗费-手术费
		result.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		// 康复费
		result.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		// 中医治疗费
		result.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		// 西药费用
		result.setData(
				"CHARGE_16",
				"TEXT",
				df.format(print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		// 抗菌药物费用
		result.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		// 中成药费
		result.setData("CHARGE_18", "TEXT",
				df.format(print.getDouble("CHARGE_18", 0)));
		// 中草药费
		result.setData("CHARGE_19", "TEXT",
				df.format(print.getDouble("CHARGE_19", 0)));
		// 血费
		result.setData("CHARGE_20", "TEXT",
				df.format(print.getDouble("CHARGE_20", 0)));
		// 白蛋白类制品费
		result.setData("CHARGE_21", "TEXT",
				df.format(print.getDouble("CHARGE_21", 0)));
		// 球蛋白类制品费
		result.setData("CHARGE_22", "TEXT",
				df.format(print.getDouble("CHARGE_22", 0)));
		// 凝血因子类制品费
		result.setData("CHARGE_23", "TEXT",
				df.format(print.getDouble("CHARGE_23", 0)));
		// 细胞因子类制品费
		result.setData("CHARGE_24", "TEXT",
				df.format(print.getDouble("CHARGE_24", 0)));
		// 检查用一次性医用材料费
		result.setData("CHARGE_25", "TEXT",
				df.format(print.getDouble("CHARGE_25", 0)));
		// 治疗用一次性医用材料费
		result.setData("CHARGE_26", "TEXT",
				df.format(print.getDouble("CHARGE_26", 0)));
		// 手术用一次性医用材料费
		result.setData("CHARGE_27", "TEXT",
				df.format(print.getDouble("CHARGE_27", 0)));
		// 其他费
		result.setData("CHARGE_28", "TEXT",
				df.format(print.getDouble("CHARGE_28", 0)));

		//2013-5-23 zhangh modify 增加重症监护表格和呼吸机使用时间
		result.setData("VENTI_TIME", "TEXT", print.getData("VENTI_TIME", 0));//呼吸机使用时间
		//duzhw add 20140423 增加肿瘤分期、护理评分
		result.setData("TUMOR_STAG_T", "TEXT", print.getData("TUMOR_STAG_T", 0));			//肿瘤分期T
		result.setData("TUMOR_STAG_N", "TEXT", print.getData("TUMOR_STAG_N", 0));			//肿瘤分期N
		result.setData("TUMOR_STAG_M", "TEXT", print.getData("TUMOR_STAG_M", 0));			//肿瘤分期M
		result.setData("NURSING_GRAD_IN", "TEXT", print.getData("NURSING_GRAD_IN", 0));		//护理评分 入院
		result.setData("NURSING_GRAD_OUT", "TEXT", print.getData("NURSING_GRAD_OUT", 0));	//护理评分 出院
		//重症监护表格数据
		for (int i = 1; i < 6; i++) {
			String inDate = "",icuInDate = "",outDate = "",icuOutDate = "";
			if(print.getData("ICU_IN_DATE" + i, 0) != null){
				inDate = print.getData("ICU_IN_DATE" + i, 0).toString().
						substring(0, print.getData("ICU_IN_DATE" + i, 0).toString().lastIndexOf("."))
						.replace("-", "/");//得到数据库中的进入时间
			}
			if(print.getData("ICU_OUT_DATE" + i, 0) != null){
				outDate = print.getData("ICU_OUT_DATE" + i, 0).toString().
						substring(0, print.getData("ICU_OUT_DATE" + i, 0).toString().lastIndexOf("."))
						.replace("-", "/");//得到数据库中的退出时间
			}
			icuInDate = getInOutDate(inDate);
			icuOutDate = getInOutDate(outDate);
			//获取房间具体名称
			String deptCode = getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
					print.getValue("ICU_ROOM" + i, 0));
			//			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			result.setData("IN_DATE_" + i, "TEXT", icuInDate);
			result.setData("OUT_DATE_" + i, "TEXT", icuOutDate);
			result.setData("ICU_ROOM_" + i, "TEXT", deptCode);
		}

		//modify by yangjj 20150610 循环生成新生儿打印信息
		/*
		// 新生儿信息 duzhw add 20140425
//		System.out.println("-----------child="+child);
		if (child.getCount("CASE_NO") > 0) {
			result.setData("Child_T2", true);
			//诊断信息
			TParm diagnosis1 = new TParm(
					this.getDBTool()
							.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
									+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
									+ child.getValue("CASE_NO", 0)
									+ "' ORDER BY DECODE(IO_TYPE,'I','1','O','2','Q','3','W','4',IO_TYPE),MAIN_FLG DESC,SEQ_NO")); // 诊断记录 modify by wanglong 20140411

			int num = 2;
			if (diagnosis1.getCount() > 0) {
				for (int j = 0; j < diagnosis1.getCount(); j++) {

					if (diagnosis1.getValue("TYPE", j).equals("O")) {
						if(diagnosis1.getValue("MAIN", j).equals("Y")){
							//result.setData("BODY1_OUT_DIAG_CODE1", "TEXT", diagnosis1.getValue("CODE", j)); // 出院主诊断疾病编码
							result.setData("BODY1_OUT_DIAG_CODE1", "TEXT", diagnosis1.getValue("NAME", j)); // 出院主诊断疾病名称
							result.setData("BODY1_DIAG_TYPE1", "TEXT", diagnosis1.getValue("CODE", j)); // 出院主诊断疾病编码
							result.setData("BODY1_CONDITION1", "TEXT",diagnosis1.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
						}else{
							//result.setData("BODY1_OUT_DIAG_CODE"+num, "TEXT", diagnosis1.getValue("CODE", j)); // 出院诊断疾病编码
							result.setData("BODY1_OUT_DIAG_CODE"+num, "TEXT", diagnosis1.getValue("NAME", j)); // 出院诊断疾病名称
							if(num==2||num==8)
							result.setData("BODY1_DIAG_TYPE"+num, "TEXT",diagnosis1.getValue("CODE", j)); // 出院诊断疾病编码
							else
						    result.setData("BODY1_DIAG_TYPE"+num, "TEXT",diagnosis1.getValue("CODE", j)); // 出院诊断疾病编码	
							result.setData("BODY1_CONDITION"+num, "TEXT",diagnosis1.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
							num++;
						}
					}


				}
			}


			//基本信息
			parm.setData("CASE_NO", child.getValue("CASE_NO", 0));
			parm.setData("ADM_TYPE", "I");
			result.setData("BODY_SEX_CODE1", "TEXT", childDiag.getValue("SEX", 0));
			result.setData("BODY_WEIGHT1", "TEXT",
					this.getChildWeight(childDiag.getValue("CASE_NO", 0)));// 出生体重
			TParm inParm = SUMNewArrivalTool.getInstance().getNewAdmWeight(parm);
			result.setData("BODY_IN_WEIGHT1", "TEXT",inParm.getValue("NB_ADM_WEIGHT"));// 入院体重
			result.setData("BODY_APGAR1", "TEXT", child_I.getValue("APGAR_NUMBER", 0));// APGAR评分
			// 婴儿卡介苗
			if (child_I.getBoolean("BABY_VACCINE_FLG", 0))
				result.setData("BODY_KJM1", "TEXT", "1");
			else
				result.setData("BODY_KJM1", "TEXT", "2");
			// 乙肝疫苗
			if (child_I.getBoolean("LIVER_VACCINE_FLG", 0))
				result.setData("BODY_YGYM1", "TEXT", "1");
			else
				result.setData("BODY_YGYM1", "TEXT", "2");
			// TSH
			if (child_I.getBoolean("TSH_FLG", 0))
				result.setData("BODY_TSH1", "TEXT", "1");
			else
				result.setData("BODY_TSH1", "TEXT", "2");
			// PKU_FLG
			if (child_I.getBoolean("PKU_FLG", 0))
				result.setData("BODY_PUK1", "TEXT", "1");
			else
				result.setData("BODY_PUK1", "TEXT", "2");

		} else {
			result.setData("Child_T2", false);
		}

		//新生儿信息2
		if (child.getCount("CASE_NO") > 1){
			result.setData("Child_T3", true);

			//诊断信息
			TParm diagnosis2 = new TParm(
					this.getDBTool()
							.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
									+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
									+ child.getValue("CASE_NO", 1)
									+ "' ORDER BY DECODE(IO_TYPE,'I','1','O','2','Q','3','W','4',IO_TYPE),MAIN_FLG DESC,SEQ_NO")); // 诊断记录 modify by wanglong 20140411
			int num = 2;
			if (diagnosis2.getCount() > 0) {
				for (int j = 0; j < diagnosis2.getCount(); j++) {

					if (diagnosis2.getValue("TYPE", j).equals("O")) {
						if(diagnosis2.getValue("MAIN", j).equals("Y")){
							//result.setData("BODY2_OUT_DIAG_CODE1", "TEXT", diagnosis2.getValue("CODE", j)); // 出院主诊断疾病编码
							result.setData("BODY2_OUT_DIAG_CODE1", "TEXT", diagnosis2.getValue("NAME", j)); // 出院主诊断疾病名称
							result.setData("BODY2_DIAG_TYPE1", "TEXT",diagnosis2.getValue("CODE", j)); // 出院主诊断疾病编码
							result.setData("BODY2_CONDITION1", "TEXT",diagnosis2.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
						}else{
							//result.setData("BODY2_OUT_DIAG_CODE"+num, "TEXT", diagnosis2.getValue("CODE", j)); // 出院诊断疾病编码
							result.setData("BODY2_OUT_DIAG_CODE"+num, "TEXT", diagnosis2.getValue("NAME", j)); // 出院诊断疾病名称
							if(num==2||num==8)
							result.setData("BODY2_DIAG_TYPE"+num, "TEXT",diagnosis2.getValue("CODE", j)); // 出院诊断疾病编码
							else
						    result.setData("BODY2_DIAG_TYPE"+num, "TEXT",diagnosis2.getValue("CODE", j)); // 出院诊断疾病编码	
							result.setData("BODY2_CONDITION"+num, "TEXT",diagnosis2.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
							num++;
						}
					}


				}
			}
			//基本信息
			parm.setData("CASE_NO", child.getValue("CASE_NO", 1));
			parm.setData("ADM_TYPE", "I");

			result.setData("BODY_SEX_CODE2", "TEXT", childDiag.getValue("SEX", 1));
			result.setData("BODY_WEIGHT2", "TEXT",
					this.getChildWeight(childDiag.getValue("CASE_NO", 1)));// 出生体重
			TParm inParm = SUMNewArrivalTool.getInstance().getNewAdmWeight(parm);
			result.setData("BODY_IN_WEIGHT2", "TEXT",inParm.getValue("NB_ADM_WEIGHT"));// 入院体重
			result.setData("BODY_APGAR2", "TEXT", child_I.getValue("APGAR_NUMBER", 1));// APGAR评分
			// 婴儿卡介苗
			if (child_I.getBoolean("BABY_VACCINE_FLG", 1))
				result.setData("BODY_KJM2", "TEXT", "1");
			else
				result.setData("BODY_KJM2", "TEXT", "2");
			// 乙肝疫苗
			if (child_I.getBoolean("LIVER_VACCINE_FLG", 1))
				result.setData("BODY_YGYM2", "TEXT", "1");
			else
				result.setData("BODY_YGYM2", "TEXT", "2");
			// TSH
			if (child_I.getBoolean("TSH_FLG", 1))
				result.setData("BODY_TSH2", "TEXT", "1");
			else
				result.setData("BODY_TSH2", "TEXT", "2");
			// PKU_FLG
			if (child_I.getBoolean("PKU_FLG", 1))
				result.setData("BODY_PKU2", "TEXT", "1");
			else
				result.setData("BODY_PKU2", "TEXT", "2");

		}else {
			result.setData("Child_T3", false);
		}
		 */


		//add by yangjj 20150702产科情况
		String childBirthSql = " SELECT " + 
				" ANTENATAL_WEEK,ANTENATAL_TIMES,ANTENATAL_GUIDE, " +
				" CHILDBIRTH_WAY,POSTPARTUM_2HOUR,POSTPARTUM_24HOUR, "+
				" CHILDBIRTH_DATE,BIRTH_PROCESS_HOUR,BIRTH_PROCESS_MINUTE, "+
				" BIRTH_PROCESS_1,BIRTH_PROCESS_2,BIRTH_PROCESS_3, "+
				" HEALTHCARE_WAY "+
				" FROM " +
				" MRO_RECORD" +
				" WHERE "+
				" CASE_NO='"+CASE_NO+"'";
		TParm childBirthParm = new TParm(TJDODBTool.getInstance().select(childBirthSql));
		result.setData("ANTENATAL_WEEK","TEXT",childBirthParm.getValue("ANTENATAL_WEEK",0));
		result.setData("ANTENATAL_TIMES","TEXT",childBirthParm.getValue("ANTENATAL_TIMES",0));
		result.setData("ANTENATAL_GUIDE","TEXT",childBirthParm.getValue("ANTENATAL_GUIDE",0));
		result.setData("CHILDBIRTH_WAY","TEXT",childBirthParm.getValue("CHILDBIRTH_WAY",0));
		result.setData("POSTPARTUM_2HOUR","TEXT",childBirthParm.getValue("POSTPARTUM_2HOUR",0));
		result.setData("POSTPARTUM_24HOUR","TEXT",childBirthParm.getValue("POSTPARTUM_24HOUR",0));
		if(childBirthParm.getValue("CHILDBIRTH_DATE",0).length()>16){
			result.setData("CHILDBIRTH_DATE","TEXT",childBirthParm.getValue("CHILDBIRTH_DATE",0).replace("-", "/").substring(0, 16));
		}else{
			result.setData("CHILDBIRTH_DATE","TEXT","");
		}

		result.setData("BIRTH_PROCESS_HOUR","TEXT",childBirthParm.getValue("BIRTH_PROCESS_HOUR",0));
		result.setData("BIRTH_PROCESS_MINUTE","TEXT",childBirthParm.getValue("BIRTH_PROCESS_MINUTE",0));
		result.setData("BIRTH_PROCESS_1","TEXT",childBirthParm.getValue("BIRTH_PROCESS_1",0));
		result.setData("BIRTH_PROCESS_2","TEXT",childBirthParm.getValue("BIRTH_PROCESS_2",0));
		result.setData("BIRTH_PROCESS_3","TEXT",childBirthParm.getValue("BIRTH_PROCESS_3",0));
		result.setData("HEALTHCARE_WAY","TEXT",childBirthParm.getValue("HEALTHCARE_WAY",0));


		//add by yangjj 20150807 查询mro_record.out_dept与TCONFIG.OBSTETRICS进行判断
		String OBSTETRICS = TConfig.getSystemValue("OBSTETRICS");
		String[] obs = OBSTETRICS.split(";");
		TParm outDept = new TParm(TJDODBTool.getInstance().select("SELECT OUT_DEPT FROM MRO_RECORD WHERE CASE_NO = '"+CASE_NO+"'"));
		for(int i = 0 ; i <= obs.length ; i++){
			if(i==obs.length){
				result.setData("OBSTETRICS", false);
				break;
			}

			if(obs[i].equals(outDept.getValue("OUT_DEPT", 0))){
				result.setData("OBSTETRICS", true);
				break;
			}
		}

		//add by yangjj 20150610循环生成新生儿打印信息
		for(int i = 0 ; i < child.getCount() ; i++ ){
			if(child.getCount("CASE_NO")>i){
				//显示新生儿信息，需要与报表中宏的onOpen方法配合使用
				result.setData("Child_T"+(i+2), true);

				//诊断信息
				TParm diagno = new TParm(
						this.getDBTool()
						.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
								+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
								+ child.getValue("CASE_NO", i)
								+ "' ORDER BY DECODE(IO_TYPE,'I','1','O','2','Q','3','W','4',IO_TYPE),MAIN_FLG DESC,SEQ_NO"));

				int num = 2;
				if (diagno.getCount() > 0) {
					for (int j = 0; j < diagno.getCount(); j++) {
						if (diagno.getValue("TYPE", j).equals("O")) {
							if(diagno.getValue("MAIN", j).equals("Y")){
								result.setData("BODY"+(i+1)+"_OUT_DIAG_CODE1", "TEXT", diagno.getValue("NAME", j)); // 出院主诊断疾病名称
								result.setData("BODY"+(i+1)+"_DIAG_TYPE1", "TEXT",diagno.getValue("CODE", j)); // 出院主诊断疾病编码
								result.setData("BODY"+(i+1)+"_CONDITION1", "TEXT",diagno.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
							}else{
								result.setData("BODY"+(i+1)+"_OUT_DIAG_CODE"+num, "TEXT", diagno.getValue("NAME", j)); // 出院诊断疾病名称
								if(num==2||num==8)
									result.setData("BODY"+(i+1)+"_DIAG_TYPE"+num, "TEXT",diagno.getValue("CODE", j)); // 出院诊断疾病编码
								else
									result.setData("BODY"+(i+1)+"_DIAG_TYPE"+num, "TEXT",diagno.getValue("CODE", j)); // 出院诊断疾病编码	
								result.setData("BODY"+(i+1)+"_CONDITION"+num, "TEXT",diagno.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
								num++;
							}
						}
					}
				}


				//基本信息
				parm.setData("CASE_NO", child.getValue("CASE_NO", i));
				parm.setData("ADM_TYPE", "I");

				//add by yangjj 20150630
				TParm childMrNoParm = new TParm(TJDODBTool.getInstance().select("SELECT MR_NO FROM ADM_INP WHERE CASE_NO = '"+child.getValue("CASE_NO", i)+"'"));
				result.setData("BODY_MRNO_"+(i+1),"TEXT",childMrNoParm.getValue("MR_NO",0));

				TParm childNameParm = new TParm(TJDODBTool.getInstance().select("SELECT PAT_NAME , BIRTH_DATE FROM SYS_PATINFO WHERE MR_NO = '"+childMrNoParm.getValue("MR_NO",0)+"'"));
				result.setData("BODY_NAME_"+(i+1),"TEXT",childNameParm.getValue("PAT_NAME",0));
				result.setData("BODY_BIRTH_"+(i+1),"TEXT",childNameParm.getValue("BIRTH_DATE",0).substring(0, 16));

				TParm childHeightParm = new TParm(TJDODBTool.getInstance().select("SELECT NEW_BODY_HEIGHT FROM SYS_PATINFO WHERE MR_NO = '"+childMrNoParm.getValue("MR_NO",0)+"'"));
				//add by guoy 20150730 -----------start---------
				double height = 0.0;
				height = Double.parseDouble(childHeightParm.getValue("NEW_BODY_HEIGHT", 0).toString());
				if(height % 1.0 == 0){
					result.setData("BODY_HEIGHT"+(i+1), "TEXT",((long)height)+"");// 出生身长
				}else{
					result.setData("BODY_HEIGHT"+(i+1), "TEXT",height+"");// 出生身长
				}
				//--------end----------

				String sex = "";
				if("1".equals(childDiag.getValue("SEX", i))){
					sex = "男";
				}else if("2".equals(childDiag.getValue("SEX", i))){
					sex = "女";
				}else{
					sex = "-";
				}
				result.setData("BODY_SEX_CODE"+(i+1), "TEXT", sex);
				//result.setData("BODY_WEIGHT"+(i+1), "TEXT",
				//this.getChildWeight(childDiag.getValue("CASE_NO", i)));// 出生体重

				//modify by yangjj 20150702
				TParm childWeightParm = new TParm(TJDODBTool.getInstance().select("SELECT NB_ADM_WEIGHT,NB_WEIGHT,NB_OUT_WEIGHT FROM MRO_RECORD WHERE CASE_NO = '"+childDiag.getValue("CASE_NO", i)+"'"));
				result.setData("BODY_WEIGHT"+(i+1), "TEXT",
						childWeightParm.getValue("NB_WEIGHT", 0));// 出生体重
				result.setData("BODY_IN_WEIGHT"+(i+1), "TEXT",childWeightParm.getValue("NB_ADM_WEIGHT", 0));// 入院体重

				//add by yangjj 20150703
				result.setData("BODY_OUT_WEIGHT"+(i+1), "TEXT",childWeightParm.getValue("NB_OUT_WEIGHT", 0));// 出院体重


				//add by yangjj 20150630

				if(child_I.getValue("APGAR_NUMBER", i).contains("-")){
					String[] arrApgar = child_I.getValue("APGAR_NUMBER", i).split("-");
					if(arrApgar.length>=3){
						result.setData("BODY_APGAR"+(i+1)+"_1", "TEXT", arrApgar[0]);// APGAR评分1分钟
						result.setData("BODY_APGAR"+(i+1)+"_5", "TEXT", arrApgar[1]);// APGAR评分5分钟
						result.setData("BODY_APGAR"+(i+1)+"_10", "TEXT", arrApgar[2]);// APGAR评分10分钟
					}
				}



				// 婴儿卡介苗
				if (child_I.getBoolean("BABY_VACCINE_FLG", i))
					result.setData("BODY_KJM"+(i+1), "TEXT", "1");
				else
					result.setData("BODY_KJM"+(i+1), "TEXT", "2");
				// 乙肝疫苗
				if (child_I.getBoolean("LIVER_VACCINE_FLG", i))
					result.setData("BODY_YGYM"+(i+1), "TEXT", "1");
				else
					result.setData("BODY_YGYM"+(i+1), "TEXT", "2");
				// TSH
				if (child_I.getBoolean("TSH_FLG", i))
					result.setData("BODY_TSH"+(i+1), "TEXT", "1");
				else
					result.setData("BODY_TSH"+(i+1), "TEXT", "2");
				// PKU_FLG
				if (child_I.getBoolean("PKU_FLG", i))
					result.setData("BODY_PKU"+(i+1), "TEXT", "1");
				else
					result.setData("BODY_PKU"+(i+1), "TEXT", "2");


				//add by yangjj 20150702
				//喂养方式
				result.setData("BODY_FEEDWAY"+(i+1),"TEXT", child_I.getValue("FEEDWAY", i));

				//转科日期、原因
				String deptTransferDate = child_I.getValue("DEPT_TRANSFER_DATE", i);
				if(!"".equals(deptTransferDate)){
					deptTransferDate = deptTransferDate.replace("-", "/").substring(0, 16);
				}
				result.setData("DEPT_TRANSFER_DATE"+(i+1),"TEXT", deptTransferDate);
				result.setData("DEPT_TRANSFER_REASON"+(i+1),"TEXT", child_I.getValue("DEPT_TRANSFER_REASON", i));

				//转院日期、原因
				String hospitalTransferDate = child_I.getValue("HOSPITAL_TRANSFER_DATE", i);
				if(!"".equals(hospitalTransferDate)){
					hospitalTransferDate = hospitalTransferDate.replace("-", "/").substring(0, 16);
				}
				result.setData("HOSPITAL_TRANSFER_DATE"+(i+1),"TEXT", hospitalTransferDate);
				result.setData("HOSPITAL_TRANSFER_REASON"+(i+1),"TEXT", child_I.getValue("HOSPITAL_TRANSFER_REASON", i));

				//死亡日期、原因
				String dieDate = child_I.getValue("DIE_DATE", i);
				if(!"".equals(dieDate)){
					dieDate = dieDate.replace("-", "/").substring(0, 16);
				}
				result.setData("DIE_DATE"+(i+1),"TEXT", dieDate);
				result.setData("DIE_REASON"+(i+1),"TEXT", child_I.getValue("DIE_REASON", i));

			}else{
				//隐藏新生儿信息
				result.setData("Child_T"+(i+2), false);
			}
		}

		return result;
	}




	private String getInOutDate(String date) {
		String icuDate = "",hour = "";
		if(date != null && date.length() >= 10){
			String[] inDateParts = date.split(" ");
			String[] inYmd = inDateParts[0].split("/");
			inYmd[0] += "年";
			inYmd[1] += "月";
			inYmd[2] += "日";
			if(date.length() > 10){
				String[] inHms = inDateParts[1].split(":");
				hour = inHms[0] + "时";
			}
			for (String string : inYmd) {
				icuDate += string;
			}
			icuDate += hour;
		}
		return icuDate;
	}

	/**
	 * 替换中文
	 * 
	 * @param TableName
	 *            String 表名
	 * @param groupID
	 *            String 组名
	 * @param descColunm
	 *            String 中文列名
	 * @param codeColunm
	 *            String code列名
	 * @param code
	 *            String 代码
	 * @return String
	 */
	public String getAnaMayDesc(String TableName, String groupID,
			String descColunm, String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + descColunm + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (descColunm.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code.trim() //+ " "//delete by wanglong 20120921 最新库中，字符串最后不带空格
				+ "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code.trim() //+ " "//delete by wanglong 20120921 最新库中，字符串最后不带空格
				+ "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(descColunm, 0);
	}

	/**
	 * 替换中文
	 * 
	 * @param TableName
	 *            String 表名
	 * @param groupID
	 *            String 组名
	 * @param descColunm
	 *            String 中文列名
	 * @param codeColunm
	 *            String code列名
	 * @param code
	 *            String 代码
	 * @return String
	 */
	public String getDesc(String TableName, String groupID, String descColunm,
			String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + descColunm + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (descColunm.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code + "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code + "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(descColunm, 0);
	}

	String filterICD;

	/**
	 * 获取诊断中文
	 * 
	 * @param ICD
	 *            String 诊断码
	 * @return String
	 */
	private String getICD_DESC(String ICD) {
		filterICD = ICD;
		ICD_DATA.filterObject(this, "filterICD");
		return ICD_DATA.getItemString(0, "ICD_CHN_DESC");
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filterICD(TParm parm, int row) {
		return filterICD.equals(parm.getValue("ICD_CODE", row));
	}

	/**
	 * 获取新生儿 出生体重 查询新生儿体温单信息表
	 * 
	 * @param CASE_NO
	 *            String
	 * @return String
	 */
	public String getChildWeight(String CASE_NO) {
		String sql = MROSqlTool.getInstance().getChildWeightSQL(CASE_NO);
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			return "";
		}
		return result.getValue("BORNWEIGHT", 0);
	}

	/**
	 * 整理手术信息
	 * 
	 * @param opParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getOP_DATA(TParm opParm) {
		// 循环替换麻醉方式为中文
		for (int i = 0; i < opParm.getCount(); i++) {
			// System.out.println("1-------------"+opParm.getValue("ANA_WAY",
			// i));
			String OP_DESC = this.getAnaMayDesc("SYS_DICTIONARY",
					"OPE_ANAMETHOD", "CHN_DESC", "ID",
					opParm.getValue("ANA_WAY", i));
			// System.out.println("2-------------"+OP_DESC);
			opParm.setData("ANA_WAY", i, OP_DESC);
		}
		return opParm;
	}

	/**
	 * 获取医师姓名列表
	 * 
	 * @return Map
	 */
	private Map getDrList() {
		String sql = "SELECT USER_ID,USER_NAME FROM SYS_OPERATOR";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Map list = new HashMap();
		for (int i = 0; i < result.getCount(); i++) {
			list.put(result.getValue("USER_ID", i),
					result.getValue("USER_NAME", i));
		}
		return list;
	}

	/**
	 * 获取大字典数据
	 */
	private void getDICTIONARY() {
		if (DICTIONARY == null) {
			String sql = "SELECT GROUP_ID,ID,CHN_DESC FROM SYS_DICTIONARY ";
			DICTIONARY = new TDataStore();
			DICTIONARY.setSQL(sql);
			DICTIONARY.retrieve();
		}
	}

	String DictionaryID;
	String DictionaryGroup;

	/**
	 * 获取大字典中的中文
	 * 
	 * @param groupId
	 *            String
	 * @param Id
	 *            String
	 * @return String
	 */
	private String getDictionaryDesc(String groupId, String Id) {
		if (DICTIONARY == null) {
			return "";
		}
		DictionaryGroup = groupId;
		DictionaryID = Id;
		DICTIONARY.filterObject(this, "filterDictionary");
		return DICTIONARY.getItemString(0, "CHN_DESC");
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filterDictionary(TParm parm, int row) {
		return DictionaryGroup.equals(parm.getValue("GROUP_ID", row))
				&& DictionaryID.equals(parm.getValue("ID", row));
	}

	/**
	 * 转换连接符
	 * 
	 * @param str
	 * @return
	 */
	private String getLineTrandept(String str) {
		String line = "";
		String regex = "|";
		if (str.indexOf(regex) != -1) {
			String[] dept = str.split("[|]");
			for (int i = 0; i < dept.length; i++) {
				if (line.length() > 0) {
					line += "->";
				}
				line += getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						dept[i]);
			}
		} else {
			line = getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE", str);
		}
		return line;
	}

	/**
	 * 入院情况 转换成国家规定的编码 1.急诊 2.门诊 3.其他医疗机构转入 9.其他
	 * 
	 * @param id
	 * @return
	 */
	private String getNewadmSource(String id) {
		String code = "";
		if (id.equals("01"))
			code = "2";
		else if (id.equals("02"))
			code = "1";
		else if (id.equals("09"))
			code = "3";
		else if (id.equals("99"))
			code = "9";
		else
			code = id;
		return code;
	}

	/**
	 * 婚姻状态 转换成国家规定的编码 1.未婚；2.已婚；3.丧偶；4.离婚；9.其他。
	 * 
	 * @param id
	 * @return
	 */
	private String getNewMarrige(String id) {
		String code = "";
		if (id.equals("3"))
			code = "4";
		else if (id.equals("4"))
			code = "3";
		else
			code = id;
		return code;
	}

	/**
	 * 检验字符串是否为空 空时符"-"值
	 * 
	 * @param str
	 * @return
	 */
	private String getcheckStr(String str) {
		String line = "";
		if (str.trim().length() == 0)
			line = "-";
		else
			line = str;
		return line;
	}

	// /**
	// * 年龄满1周岁的，以实足年龄的相应整数填写；年龄不足1周岁的，按照实足年龄的月龄填写，以分数形式表示：
	// * 分数的整数部分代表实足月龄，分数部分分母为30，分子为不足1个月的天数，如“2 月”代表患儿实足年龄为2个月又15天。
	// * @param odo
	// * @return String 界面显示的年龄
	// */
	// public String showAge(Timestamp birthday, Timestamp t) {
	// String age = "";
	// String[] res;
	// res = StringTool.CountAgeByTimestamp(birthday, t);
	// if (TypeTool.getInt(res[0]) < 1) {
	// age =res[1]+" "+ res[2] + "─—";
	// } else {
	// age = res[0] + "岁";
	// }
	// return age;
	// }
	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	// ---------------add by wangqing 20171229 start----------------------
	/**
	 * 是否是新生儿
	 * @param CASE_NO
	 * @return
	 */
	public boolean isNewBaby(String CASE_NO){
		String sql = " SELECT A.CASE_NO, A.MR_NO, A.IN_DATE, B.BIRTH_DATE "
				+ "FROM ADM_INP A, SYS_PATINFO B "
				+ "WHERE A.MR_NO=B.MR_NO(+) AND A.CASE_NO='"+CASE_NO+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		Timestamp birthDay = parm.getTimestamp("BIRTH_DATE", 0);
		Timestamp inDay = parm.getTimestamp("IN_DATE",0);
		int day = StringTool.getDateDiffer(inDay, birthDay);
		//this.messageBox(day+""); 
		if(day<28 && day>=0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * String转Double
	 * @param s
	 * @return
	 */
	public Double stringToDouble(String s){
		Double d = 0.0;
		try{
			d = Double.valueOf(s);
		}catch(Exception e){
			d = 0.1;
		}
		return d;
	}
	// ---------------add by wangqing 20171229 end----------------------



}
