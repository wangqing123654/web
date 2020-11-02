package com.javahis.ui.mro;

import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p> Title:医疗统计出院病患信息导出EXCLE </p>
 * 
 * <p> Description:医疗统计出院病患信息导出EXCLE </p>
 * 
 * <p> Copyright: Copyright (c) 2011 </p>
 * 
 * <p> Company:bluecore </p>
 * 
 * @author pangb 2012-5-6
 * @version 2.0
 */
public class MROAdmSearchControl extends TControl {
	private TTable table; // 病患基本信息列表

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // 病患基本信息列表
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}
    //CHARGE_01:901.001一般医疗服务费 CHARGE_08:902.004临床诊断项目费 CHARGE_05:902.001病理诊断费CHARGE_07: 902.003影像学诊断费
	//CHARGE_06:902.002实验室诊断费 CHARGE_09:903.001.001 临床物理治疗费 CHARGE_10:903.001.002非临床物理治疗费
	//CHARGE_12:903.002.002手术治疗费-手术费 CHARGE_20:908.001 血费  CHARGE_21:908.002白蛋白类制品费  CHARGE_22:908.003球蛋白类制品费
	//CHARGE23:908.004凝血因子类制品费  CHARGE24:908.005细胞因子类制品费 CHARGE28:910.001其他费CHARGE03:901.003护理费
	//CHARGE26:909.002治疗用一次性医用材料费CHARGE25:909.001检查用一次性医用材料费CHARGE27:909.003 手术用一次性医用材料费
	//CHARGE17:906.001.002非抗菌药物费用CHARGE16:906.001.001西药费-抗菌药物费用 CHARGE18:907.001中成药费
	//CHARGE19:907.002中草药费  CHARGE11:903.002.001手术治疗费-麻醉费 CHARGE13:903.002.003 手术治疗费-其他
	private String sql = "SELECT A.MR_NO, A.CASE_NO, A.PAT_NAME,"
			+ "CASE WHEN A.SEX = '1' THEN '男' WHEN A.SEX = '2' THEN '女' ELSE '未知' END AS SEX,"
			+ "A.IDNO,D.CTZ_DESC,F.DEPT_CHN_DESC AS IN_DEPT_DESC,G.DEPT_CHN_DESC AS OUT_DEPT_DESC,"
			+ "TO_CHAR(A.IN_DATE,'YYYY-MM-DD') AS IN_DATE,TO_CHAR(A.OUT_DATE,'YYYY-MM-DD') AS OUT_DATE,CASE WHEN ROUND(TO_NUMBER(A.OUT_DATE-A.IN_DATE))<=0 THEN 1 ELSE ROUND(TO_NUMBER(A.OUT_DATE-A.IN_DATE)) END AS REAL_STAY_DAYS,"
			+ "TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS BIRTH_DATE,A.AGE,H.POST_DESCRIPTION, A.H_ADDRESS, A.CONT_TEL,"
			+ "I.USER_NAME AS DR_DESC, A.GET_TIMES, A.SUCCESS_TIMES,A.RBC, A.PLATE, A.PLASMA,"
			+ "A.WHOLE_BLOOD, A.OTH_BLOOD,CASE WHEN A.CODE1_STATUS='1' THEN '治愈' WHEN A.CODE1_STATUS='2' THEN '好转' WHEN A.CODE1_STATUS='3' THEN '未愈' "
			+ "WHEN A.CODE1_STATUS='4' THEN '死亡' WHEN A.CODE1_STATUS='5' THEN '其它' ELSE '其它' END  AS OUT_STATUS,"
			+ "A.INTE_DIAG_CODE ,T.ICD_CHN_DESC AS INTE_DIAG_DESC ,C.MAINDIAG,J.ICD_CHN_DESC AS MAIN_DESC, "
			+ "'' IN_DIAG_CODE1, '' IN_DIAG_DESC1 ,'' IN_DIAG_CODE2 ,'' IN_DIAG_DESC2,"
			+ "'' IN_DIAG_CODE3, '' IN_DIAG_DESC3 ,'' OE_DIAG_CODE1 ,'' OE_DIAG_DESC1,"
			+ "'' OE_DIAG_CODE2, '' OE_DIAG_DESC2 ,'' OE_DIAG_CODE3 ,'' OE_DIAG_DESC3,"
			+ "'' OUT_DIAG_CODE1,'' OUT_DIAG_DESC1,'' OUT_DIAG_CODE2,'' OUT_DIAG_DESC2,"
			+ "'' OUT_DIAG_CODE3,'' OUT_DIAG_DESC3,'' OUT_DIAG_CODE4,'' OUT_DIAG_DESC4,"
			+ "'' OP_CODE1 ,'' OP_DESC1,'' OP_DATE1,'' OP_USER_NAME1,'' OP_ANA_DESC1,"
			+ "'' OP_CODE2 ,'' OP_DESC2,'' OP_DATE2,'' OP_USER_NAME2,'' OP_ANA_DESC2,"
			+ "'' OP_CODE3 ,'' OP_DESC3,'' OP_DATE3,'' OP_USER_NAME3,'' OP_ANA_DESC3,"
			+ "'' OP_CODE4 ,'' OP_DESC4,'' OP_DATE4,'' OP_USER_NAME4,'' OP_ANA_DESC4," 
			+ "'' OP_CODE5 ,'' OP_DESC5,'' OP_DATE5,'' OP_USER_NAME5,'' OP_ANA_DESC5," 
			+ "'' OP_CODE6 ,'' OP_DESC6,'' OP_DATE6,'' OP_USER_NAME6,'' OP_ANA_DESC6," 
			+ "A.CHARGE_01 AS MEDICAL_AMT,A.CHARGE_08 AS CLINICAL_DIAG_AMT,A.CHARGE_05 AS PATHOLOG_DIAG_AMT," 
			+ "A.CHARGE_07 AS IMAGE_DIAG_AMT, A.CHARGE_06 AS LABORATORY_DIAG_AMT,A.CHARGE_09 AS PAYSICAL_TY_AMT, "
			+ "A.CHARGE_10 AS NPAYSICAL_TY_AMT,A.CHARGE_12 AS SURGERY_AMT ,A.CHARGE_20 AS BLOOD_AMT,"
			+ "A.CHARGE_21 AS ALBUMINS_AMT, A.CHARGE_22 AS GLOBULIN_AMT,A.CHARGE_23 AS CLOT_FACTOR_AMT, A.CHARGE_24 AS CYTOK_AMT," 
			+ "A.CHARGE_28 AS OTHER_AMT,A.CHARGE_03 AS CARE_AMT,A.CHARGE_26 AS TREATMENT_AMT,A.CHARGE_25 AS CHECK_DISPOSABLE_AMT,"
            + "A.CHARGE_27 AS DISPOSABLE_SURG_AMT, A.CHARGE_17 AS NON_ANTIM_AMT, A.CHARGE_16 AS ANTIBACTERIAL_DRUG_AMT,"
            + "A.CHARGE_18 AS PROPRIETARY_CHIN_AMT, A.CHARGE_19 AS PHA_HERBAL_AMT, A.CHARGE_11 AS ANESTHESIA_AMT, "
            + "A.CHARGE_13 AS SURGERY_OTHER_AMT,"
			+ "A.SUM_TOT AS SUM_AMT," 
			+ "(A.CHARGE_18+A.CHARGE_19+A.CHARGE_16+A.CHARGE_17)AS DRUG_AMT,(A.CHARGE_26+A.CHARGE_25+A.CHARGE_27) AS MATER_AMT"
			+ " FROM MRO_RECORD A,SYS_PATINFO B,ADM_INP C,SYS_CTZ D,SYS_DEPT F,SYS_DEPT G,SYS_POSTCODE H,SYS_OPERATOR I"
			+ ",SYS_DIAGNOSIS J,SYS_DIAGNOSIS T"
			+ " WHERE A.CASE_NO=C.CASE_NO "
			+ " AND A.MR_NO=B.MR_NO AND C.CTZ1_CODE=D.CTZ_CODE(+)"
			+ " AND A.IN_DEPT=F.DEPT_CODE(+) AND A.H_POSTNO=H.POST_CODE(+)"
			+ " AND A.OUT_DEPT=G.DEPT_CODE(+)"
			+ " AND A.VS_DR_CODE=I.USER_ID(+)"
			+ " AND C.MAINDIAG=J.ICD_CODE(+)"
			+ " AND A.INTE_DIAG_CODE=T.ICD_CODE(+)";

	// 病案号,100; 就诊号,100; 病患姓名,70;性别,30;身份证号码,130;身份,70;入院科室 ,
	// 100;出院科室,100;入院时间,100; 出院时间,100;住院天数,100;出生日期,100;
	// 年龄,100;籍贯/出生地,100;户口住址,100;电话号码,100;经治医生,100;抢救次数,40;
	// 成功次数,40;红血球,80;血小板,80; 血浆,80;全血,80;其它血品种类,100;出院情况,100;主诊断代码,100;
	// 主诊断名称,100; 入院诊断代码1,100;入院诊断名称1,100;入院诊断代码2,100;入院诊断名称2 ,100;入院诊断代码3,100;
	// 入院诊断名称3,100;门急诊诊断代码1,100;门急诊诊断名称1 ,100;门急诊诊断代码2,100;门急诊诊断名称2,100;
	// 门急诊诊断代码3,100;门急诊诊断名称3 ,100;出院诊断代码1,100;
	// 出院诊断名称1,100; 出院诊断代码2,100;出院诊断名称2,100;出院诊断代码3,100;出院诊断名称3 ,100;
	// 出院诊断代码4,100;出院诊断名称4,100;
	// 手术代码1,100;手术名称1,100;手术时间1,100;手术人员1,100;麻醉方式1,100;
	// 手术代码2,100;手术名称2,100;手术时间2,100;手术人员2,100;麻醉方式2,100; 手术代码3,100;手术名称3,100;
	// 手术时间3,100;手术人员3,100;麻醉方式3,100;
	// 手术代码4,100;手术名称4,100;手术时间4,100;手术人员4,100;麻醉方式4,100;
	// 总费用,100;药品费,100;材料费,100
	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("请输入出院日期");
			return;
		}
		StringBuffer bf = new StringBuffer();
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			bf.append(" AND A.REGION_CODE ='").append(
					this.getValue("REGION_CODE").toString()).append("'");
		}
		bf.append(" AND A.OUT_DATE BETWEEN TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_START"), true)).append(
				"','YYYYMMDDHH24MISS') AND TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_END").substring(0,10), false)).append(
				"','YYYYMMDDHH24MISS')");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql + bf));
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			StringBuffer tempSql = new StringBuffer();
			tempSql.append("SELECT A.CASE_NO,A.OP_CODE,A.OP_DESC,TO_CHAR(A.OP_DATE,'YYYY-MM-DD') AS OP_DATE,B.CHN_DESC,C.USER_NAME,A.MAIN_FLG FROM MRO_RECORD_OP A,"
									+ "SYS_DICTIONARY B,SYS_OPERATOR C WHERE A.MAIN_SUGEON=C.USER_ID AND TRIM(A.ANA_WAY)=TRIM(B.ID(+)) " +
											" AND B.GROUP_ID='OPE_ANAMETHOD'" +
											" AND A.CASE_NO='")
					.append(result.getValue("CASE_NO", i)).append("'");
			TParm tempParm = getResultParm(tempSql.toString());
			if (tempParm.getErrCode() < 0) {
				this.messageBox("查询失败");
				return;
			} else {
				int index=1;
				for (int j = 0; j < tempParm.getCount(); j++) {
					if (null!=tempParm.getValue(
							"MAIN_FLG", j) && tempParm.getValue(
							"MAIN_FLG", j).equals("Y")) {//主手术
						result.setData("OP_CODE1" , i, tempParm.getValue(
								"OP_CODE", j));
						result.setData("OP_DESC1" , i, tempParm.getValue(
								"OP_DESC", j));
						result.setData("OP_USER_NAME1" , i, tempParm
								.getValue("USER_NAME", j));
						result.setData("OP_DATE1" , i, tempParm
								.getValue("OP_DATE", j));
						result.setData("OP_ANA_DESC1" , i, tempParm.getValue(
								"CHN_DESC", j));
						continue;
					}
					index++;
					result.setData("OP_CODE" + index, i, tempParm.getValue(
							"OP_CODE", j));
					result.setData("OP_DESC" + index, i, tempParm.getValue(
							"OP_DESC", j));
					result.setData("OP_USER_NAME" + index, i, tempParm
							.getValue("USER_NAME", j));
					result.setData("OP_DATE" + index, i, tempParm
							.getValue("OP_DATE", j));
					result.setData("OP_ANA_DESC" + index, i, tempParm.getValue(
							"CHN_DESC", j));
				}
			}
			//入院诊断出院诊断数据
			tempSql=new StringBuffer();
			tempSql.append("SELECT IO_TYPE,ICD_CODE,ICD_DESC,MAIN_FLG FROM MRO_RECORD_DIAG WHERE CASE_NO ='")
			.append(result.getValue("CASE_NO", i)).append("' ORDER BY IO_TYPE,MAIN_FLG");
			tempParm = getResultParm(tempSql.toString());
			if (tempParm.getErrCode() < 0) {
				this.messageBox("查询失败");
				return;
			} else {
				int indexM=1;//入院
				int indexO=1;//出院
				int indexI=1;//门急诊
				for (int j = 0; j < tempParm.getCount(); j++) {
					//主诊断
					if (null != tempParm.getValue("MAIN_FLG", j)
							&& tempParm.getValue("MAIN_FLG", j).equals("Y")) {
						// IO_TYPE =M 入院 O 出院 I 门急诊
						if (tempParm.getValue("IO_TYPE", j).equals("M")) {
							result.setData("IN_DIAG_CODE1", i, tempParm
									.getValue("ICD_CODE", j));
							result.setData("IN_DIAG_DESC1", i, tempParm
									.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("O")) {
							result.setData("OUT_DIAG_CODE1", i, tempParm
									.getValue("ICD_CODE", j));
							result.setData("OUT_DIAG_DESC1", i, tempParm
									.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("I")) {
							result.setData("OE_DIAG_CODE1", i, tempParm
									.getValue("ICD_CODE", j));
							result.setData("OE_DIAG_DESC1", i, tempParm
									.getValue("ICD_DESC", j));
						}
					}else{
						//次诊断
						if (tempParm.getValue("IO_TYPE", j).equals("M")) {
							indexM++;
							result.setData("IN_DIAG_CODE"+indexM, i, tempParm
									.getValue("ICD_CODE", j));
							result.setData("IN_DIAG_DESC"+indexM, i, tempParm
									.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("O")) {
							indexO++;
							result.setData("OUT_DIAG_CODE"+indexO, i, tempParm
									.getValue("ICD_CODE", j));
							result.setData("OUT_DIAG_DESC"+indexO, i, tempParm
									.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("I")) {
							indexI++;
							result.setData("OE_DIAG_CODE"+indexI, i, tempParm
									.getValue("ICD_CODE", j));
							result.setData("OE_DIAG_DESC"+indexI, i, tempParm
									.getValue("ICD_DESC", j));
						}
					}
				}
			}
		}
		((TTextField) this.getComponent("COUNT")).setValue(result.getCount() + "");// add by wanglong 20121221
		table.setParmValue(result);
	}
	
	/**
	 * 执行SQL
	 * @param tempSql
	 * @return
	 */
	private TParm getResultParm(String tempSql) {
		TParm tempParm = new TParm(TJDODBTool.getInstance().select(tempSql));
		if (tempParm.getErrCode() < 0) {
			return tempParm;
		}
		return tempParm;
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {//modify by wanglong 20121226
		TParm parm = table.getShowParmValue();
		TParm temp = cloneParm(parm);
		for (int i = 0; i < temp.getCount("MR_NO"); i++) {
			if ((temp.getData("AGE", i) == null)
					|| temp.getData("AGE", i).equals("")
					|| temp.getData("AGE", i).toString().matches(".*月.*"))
				continue;
			temp.setData("AGE", i, Integer.parseInt(temp.getData("AGE", i)
					.toString().replaceAll("[^0-9]", "")));// 将年龄字段去掉“岁”字，并改为int类型。
		}
        for (int i = 0; i < temp.getCount("MR_NO"); i++) {//转int型
            temp.setData("GET_TIMES", i, temp.getInt("GET_TIMES", i));//抢救次数
            temp.setData("SUCCESS_TIMES", i, temp.getInt("SUCCESS_TIMES", i));//成功次数
            temp.setData("RBC", i, temp.getInt("RBC", i));//红血球
            temp.setData("PLATE", i, temp.getInt("PLATE", i));//血小板
            temp.setData("PLASMA", i, temp.getInt("PLASMA", i));//血浆
            temp.setData("WHOLE_BLOOD", i, temp.getInt("WHOLE_BLOOD", i));//全血
            temp.setData("OTH_BLOOD", i, temp.getInt("OTH_BLOOD", i));//其他血品种类
        }
		table.setParmValue(temp);
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table, StringTool.getString((Timestamp) this.getValue("OUT_DATE_START"), "yyyy.MM")
							+ "患者基本资料");
		table.setParmValue(parm);
	}
	
	/**
	 * 清空     add by wanglong 20121221
	 */
	public void onClear(){
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		table.setParmValue(new TParm());
	}
	
	/**
	 * 克隆TParm
	 * @param srcParm
	 * @return
	 */
	private TParm cloneParm(TParm srcParm) {// add by wanglong 20121221
		TParm copyParm = new TParm();
		String[] names = srcParm.getNames();
		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < srcParm.getCount(); j++) {
				copyParm.addData(names[i], srcParm.getData(names[i], j));
			}
		}
		copyParm.setCount(copyParm.getCount(names[0]));
		return copyParm;
	}
	
}
