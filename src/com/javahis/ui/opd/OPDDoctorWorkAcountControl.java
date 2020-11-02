package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.DictionaryTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title: 门诊医生工作量统计报表Panel
 * </p>
 * 
 * <p>
 * Description:门诊医生工作量统计报表Panel
 * </p>
 * 
 * <p>
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author huangjw 20150508
 * @version 1.0
 */
public class OPDDoctorWorkAcountControl extends TControl {
	TTable table;
	TParm data = new TParm();
	private String header = ""; // 保存TABLE的HEADER
	private String ParmMap = ""; // 保存TABLE的ParmMap
	private String chageList = ""; // 记录费用类型
	private DecimalFormat df = new DecimalFormat("0.00");

	@Override
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/'));
		this.setValue("START_DATE", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/'));
		intPage();
	}

	/**
	 * 初始化页面
	 */
	public void intPage() {
		header += "年,50;月,50;日,50;科室,100;医生,100;客户,80;数量,80;总金额,80;西药费,80;中药费,80;";
		ParmMap += "YEAR;MONTH;DAY;DEPT_DESC;DR_CODE;VISIT_CODE;VISIT_CODE_COUNT;AR_AMT;CHANGE1AND2;CHANGE3AND4;";
		String alignment = "0,left;1,left;2,left;3,left;4,left;5,left;6,right;7,right;8,right;9,right;";// 列对齐方式
		TParm charge = DictionaryTool.getInstance().getGroupList("SYS_CHARGE");
		String chargeAll = "";
		for (int i = 1; i < 31; i++) {
			if (i < 10) {
				chargeAll += ",CHARGE0" + i;
			} else {
				chargeAll += ",CHARGE" + i;
			}
		}
		chargeAll = chargeAll.substring(1, chargeAll.length());
		String sql = "SELECT " + chargeAll
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE = 'O'";
		TParm chargeParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm p = new TParm();
		String chargeo = "";
		for (int j = 1; j < 31; j++) {
			for (int i = 0; i < charge.getCount(); i++) {
				if (j < 10) {
					chargeo = "CHARGE0" + j;
				} else {
					chargeo = "CHARGE" + j;
				}
				if (charge.getData("ID", i).equals(
						chargeParm.getData(chargeo, 0))) {
					p.addData("CHARGE", charge.getValue("NAME", i));
					p.addData("ID", chargeo);
				}
			}
		}
		for (int i = 0; i < p.getCount("CHARGE"); i++) {
			if("中成药费".equals(p.getValue("CHARGE", i))){
				continue;
			}
			if("中草药费".equals(p.getValue("CHARGE", i))){
				continue;
			}
			if("非抗菌素".equals(p.getValue("CHARGE", i))){
				continue;
			}
			if("抗菌素".equals(p.getValue("CHARGE", i))){
				continue;
			}
			header += p.getValue("CHARGE", i) + ",70;";
			chageList += p.getValue("ID", i) + ";";
			alignment += i + 10 + ",right;";
		}
//		header = header.replaceAll("中成药费,70;", "");
//		header = header.replaceAll("中草药费,70;", "");
		ParmMap += chageList;
//		ParmMap = ParmMap.replaceAll("CHARGE06;", "");
//		ParmMap = ParmMap.replaceAll("CHARGE07;", "");
		table.setHeader(header.substring(0, header.length() - 1));// 绑定表格时去掉最后一个分号
		table.setParmMap(ParmMap.substring(0,ParmMap.length()-1));
		table.setColumnHorizontalAlignmentData(alignment.substring(0, alignment
				.length() - 1));// 设置列对齐

		
	}

	/**
	 * 查询配参
	 * 
	 * @return TParm
	 */
	public TParm finishData() {
		TParm parm = new TParm();
		// 开单科室
		if (getValueString("DEPT_CODE").length() > 0)
			parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		// 开单医师
		if (getValueString("DR_CODE").length() > 0)
			parm.setData("DR_CODE", getValueString("DR_CODE"));
		// 起日
		if (getValueString("START_DATE").length() > 0)
			parm.setData("START_DATE", StringTool.getString(TCM_Transform
					.getTimestamp(getValue("START_DATE")), "yyyyMMdd"));
		// 迄日
		if (getValueString("END_DATE").length() > 0)
			parm.setData("END_DATE", StringTool.getString(TCM_Transform
					.getTimestamp(getValue("END_DATE")), "yyyyMMdd"));
		return parm;
	}

	/**
	 * 查询 
	 */
	public void onQuery() {
		TParm parm = finishData();
		TParm result = selectData(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有数据");
			table.removeRowAll();
			return;
		}
		data = getTableData(result);
		/*
		 * int countAll = data.getCount("DEPT_CODE"); for (int i = countAll - 1;
		 * i >= 0; i--) { if(data.getDouble("AR_AMT", i) <= 0){
		 * //预开检查可以直接作，不用报道，所有REG_patadm这张表中的科室字段没有值 if
		 * ("".equals(data.getValue("DEPT_CODE", i)) ||
		 * data.getData("DEPT_CODE", i) == null) { data.removeRow(i); } }
		 * 
		 * } countAll=data.getCount("DEPT_CODE"); double sumArAmt = 0.00; //实收
		 * double sumTotAmt = 0.00; //应收 double sumReduceAmt = 0.00; //减免 double
		 * sumChange1and2 = 0.00; //西药费 double []changeValue = new double[31];
		 * for(int i=0;i<countAll;i++){ for(int j = 1;j<changeValue.length;j++){
		 * changeValue[j] += StringTool.round(data.getDouble("CHARGE"+ (j<10?
		 * "0"+j : j),i), 2); } sumArAmt +=
		 * StringTool.round(data.getDouble("AR_AMT", i), 2); sumTotAmt +=
		 * StringTool.round(data.getDouble("TOT_AMT", i), 2); sumReduceAmt +=
		 * StringTool.round(data.getDouble("REDUCE_AMT", i), 2); sumChange1and2
		 * += StringTool.round(data.getDouble("CHANGE1AND2", i), 2); } for(int j
		 * = 1;j<changeValue.length;j++){ data.setData("CHARGE"+ (j<10? "0"+j :
		 * j), countAll ,df.format(changeValue[j])); }
		 * data.setData("DEPT_CODE",countAll,"");
		 * data.setData("REGION_CHN_DESC",countAll,"");
		 * data.setData("DR_CODE",countAll,"");
		 * data.setData("DEPT_DESC",countAll,"合计:");
		 * data.setData("DR_NAME",countAll,"");
		 * data.setData("TOT_AMT",countAll,df.format(sumTotAmt));
		 * data.setData("AR_AMT",countAll,df.format(sumArAmt));
		 * data.setData("REDUCE_AMT",countAll,df.format(sumReduceAmt));
		 * data.setData("CHANGE1AND2",countAll,df.format(sumChange1and2));
		 */
		// System.out.println("data::"+data);
		
		int count = 0;
		String name[] = ParmMap.split(";");
		for (int i = 0; i < data.getCount("VISIT_CODE_COUNT"); i++) {
			count += data.getInt("VISIT_CODE_COUNT", i);
		}
		TParm parmC = new TParm ();
		for (int i = 0; i < name.length; i++) {
			parmC.addData(name[i], "");
		}
		parmC.setData("VISIT_CODE_COUNT", 0, count);
		parmC.setData("DEPT_DESC", 0, "合计");
		
		for (int i = 7; i < name.length; i++) {
			double sum = 0;
			for (int j = 0; j < data.getCount("VISIT_CODE_COUNT"); j++) {
				sum += data.getDouble(name[i], j);
			}
			parmC.setData(name[i], 0, df.format(sum) );
		}
		
		
		data.addRowData(parmC, 0);
		table.setParmValue(data);
	}

	/**
	 * 整理数据
	 * 
	 * @param re
	 * @return
	 */
	private TParm getTableData(TParm re) {
		String[] chage = ParmMap.split(";");
		for (int i = 0; i < re.getCount("DEPT_CODE"); i++) {
			re.setData("CHANGE1AND2", i, re.getDouble("CHARGE01", i)+ re.getDouble("CHARGE02", i));
			re.setData("CHANGE3AND4", i, re.getDouble("CHARGE03", i)+ re.getDouble("CHARGE04", i));
			for (int j = 7; j < chage.length; j++) {
				re.setData(chage[j], i, df.format(re.getDouble(chage[j], i)));
			}

		}
		return re;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("DEPT_CODE;DR_CODE");
		table.setParmValue(new TParm());
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/'));
		this.setValue("START_DATE", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/'));
	}

	/**
	 * 汇出Excel
	 */
	public void onExcel() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "门诊医生工作量统计报表");
	}

	/**
	 * 各种费用数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectData(TParm parm) {
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			
		
		String sql = "SELECT "
				+ "E.REGION_CHN_ABN REGION_CHN_DESC, TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE, A.DEPT_CODE, "
				+ " CASE VISIT_CODE WHEN '0' THEN '新客户' WHEN '1' THEN '老客户' END"
				+ " VISIT_CODE,"
				+ "B.DEPT_CHN_DESC AS DEPT_DESC,F.USER_NAME AS DR_CODE, SUM (D.TOT_AMT) AS TOT_AMT, "
				+ "SUM (D.CHARGE01+D.CHARGE02+D.CHARGE03+D.CHARGE04+D.CHARGE05+D.CHARGE06+D.CHARGE07+D.CHARGE08+"
				+ "D.CHARGE09+D.CHARGE10+D.CHARGE11+D.CHARGE12+D.CHARGE13+D.CHARGE14+D.CHARGE15+"
				+ "D.CHARGE16+D.CHARGE17+D.CHARGE18+D.CHARGE19+D.CHARGE20+D.CHARGE21+D.CHARGE22+"
				+ "D.CHARGE23+D.CHARGE24+D.CHARGE25+D.CHARGE26+D.CHARGE27+D.CHARGE28+D.CHARGE29+D.CHARGE30) AS AR_AMT, "
				+ "SUM (D.REDUCE_AMT) AS REDUCE_AMT, SUM (D.CHARGE01) AS CHARGE01, SUM (D.CHARGE02) AS CHARGE02, "
				+ "SUM (D.CHARGE03) AS CHARGE03, SUM (D.CHARGE04) AS CHARGE04, SUM (D.CHARGE05) AS CHARGE05, " 
				+ "SUM (D.CHARGE06) AS CHARGE06, SUM (D.CHARGE07) AS CHARGE07, SUM (D.CHARGE08) AS CHARGE08, "
				+ "SUM (D.CHARGE09) AS CHARGE09, SUM (D.CHARGE10) AS CHARGE10, SUM (D.CHARGE11) AS CHARGE11, "
				+ "SUM (D.CHARGE12) AS CHARGE12, SUM (D.CHARGE13) AS CHARGE13, SUM (D.CHARGE14) AS CHARGE14, "
				+ "SUM (D.CHARGE15) AS CHARGE15, SUM (D.CHARGE16) AS CHARGE16, SUM (D.CHARGE17) AS CHARGE17, "
				+ "SUM (D.CHARGE18) AS CHARGE18, SUM (D.CHARGE19) AS CHARGE19, SUM (D.CHARGE20) AS CHARGE20, "
				+ "SUM (D.CHARGE21) AS CHARGE21, SUM (D.CHARGE22) AS CHARGE22, SUM (D.CHARGE23) AS CHARGE23, "
				+ "SUM (D.CHARGE24) AS CHARGE24, SUM (D.CHARGE25) AS CHARGE25, SUM (D.CHARGE26) AS CHARGE26, "
				+ "SUM (D.CHARGE27) AS CHARGE27, SUM (D.CHARGE28) AS CHARGE28, SUM (D.CHARGE29) AS CHARGE29, "
				+ "SUM (D.CHARGE30) AS CHARGE30, 0 AS CHANGE1AND2, 0 AS CHANGE3AND4 "
				+ "FROM REG_PATADM A,SYS_DEPT B, BIL_OPB_RECP D,SYS_REGION E,SYS_OPERATOR F "
				+ "WHERE A.DEPT_CODE = B.DEPT_CODE(+) "
				+ "AND A.REGION_CODE = E.REGION_CODE "
				+ "AND A.REGCAN_USER IS NULL "
				+ "AND A.ARRIVE_FLG = 'Y' "
				+ "AND A.CASE_NO = D.CASE_NO(+) "
				+ "AND A.DR_CODE = F.USER_ID "
				+ "AND A.ADM_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_DATE") + "','YYYYMMDD') AND TO_DATE('"
				+ parm.getValue("END_DATE") + "','YYYYMMDD') ";
		String sql1 = "SELECT SUM (CASE visit_code WHEN '0' THEN 1 WHEN '1' THEN 0 END) NEWCLIENT,"
				+ "SUM (CASE visit_code WHEN '1' THEN 1 WHEN '0' THEN 0 END) OLDCLIENT,"
				+ "TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE, "
				+ "A.DEPT_CODE, "
				+ "B.DEPT_CHN_DESC AS DEPT_DESC,F.USER_NAME AS DR_CODE "
				+ "FROM REG_PATADM A,SYS_DEPT B,SYS_OPERATOR F "
				+ "WHERE A.DEPT_CODE = B.DEPT_CODE(+) "
				+ "AND A.REGCAN_USER IS NULL " 
				+ "AND A.ARRIVE_FLG = 'Y' "
				+ "AND A.DR_CODE = F.USER_ID "
				+ "AND A.ADM_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "','YYYYMMDD') AND TO_DATE('"
				+ parm.getValue("END_DATE") + "','YYYYMMDD') ";

		if (parm.getValue("DEPT_CODE") != null
				&& parm.getValue("DEPT_CODE").length() > 0) {
			sql += " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
			sql1 += " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		if (parm.getValue("DR_CODE") != null
				&& parm.getValue("DR_CODE").length() > 0) {
			sql += " AND A.DR_CODE='" + parm.getValue("DR_CODE") + "'";
			sql1 += " AND A.DR_CODE='" + parm.getValue("DR_CODE") + "'";
		}
		sql += " GROUP BY E.REGION_CHN_ABN, A.ADM_DATE, A.DEPT_CODE,B.DEPT_CHN_DESC,F.USER_NAME,A.VISIT_CODE ORDER BY E.REGION_CHN_ABN,A.ADM_DATE, A.DEPT_CODE ";
		sql1 += " GROUP BY  A.ADM_DATE,A.DEPT_CODE,B.DEPT_CHN_DESC,F.USER_NAME ORDER BY  A.ADM_DATE,A.DEPT_CODE ";
//		 System.out.println("sql::"+sql);
//		 System.out.println("sql1::"+sql1);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm tableParm = new TParm();
		// if(result.getCount()>0&&result1.getCount()>0){
		// for(int i=0;i<result.getCount();i++){
		// result.setData("NEWCLIENT",i,result1.getValue("NEWCLIENT",i));
		// result.setData("OLDCLIENT",i,result1.getValue("OLDCLIENT",i));
		// }
		// }
		// System.out.println(result);

		String[] valueName = result.getNames();

		for (int j = 0; j < result.getCount(); j++) {
			for (int i = 0; i < result1.getCount(); i++) {
				if (result.getValue("ADM_DATE", j).equals(
						result1.getValue("ADM_DATE", i))
						&& result.getValue("DEPT_CODE", j).equals(
								result1.getValue("DEPT_CODE", i))
						&& result.getValue("DEPT_DESC", j).equals(
								result1.getValue("DEPT_DESC", i))
						&& result.getValue("DR_CODE", j).equals(
								result1.getValue("DR_CODE", i))) {

					if (result1.getInt("NEWCLIENT", i) > 0) {
						if ("新客户".equals(result.getValue("VISIT_CODE", j))) {
							tableParm.addRowData(result, j);
							String[] valueD = result.getValue("ADM_DATE", j)
									.split("/");
							tableParm.addData("YEAR", valueD[0]);
							tableParm.addData("MONTH", valueD[1]);
							tableParm.addData("DAY", valueD[2]);
							tableParm.addData("VISIT_CODE_COUNT", result1
									.getInt("NEWCLIENT", i));
						}
					} else {

						String[] valueD = result.getValue("ADM_DATE", j).split(
								"/");
						tableParm.addData("YEAR", valueD[0]);
						tableParm.addData("MONTH", valueD[1]);
						tableParm.addData("DAY", valueD[2]);
						tableParm.addData("VISIT_CODE_COUNT", 0);
						tableParm.addData("VISIT_CODE", "新客户");
						tableParm.addData("DR_CODE", result.getValue("DR_CODE",
								j));
						tableParm.addData("DEPT_CODE", result.getValue(
								"DEPT_CODE", j));
						tableParm.addData("DEPT_DESC", result.getValue(
								"DEPT_DESC", j));
						tableParm.addData("ADM_DATE", result.getValue(
								"ADM_DATE", j));
						for (int k = 0; k < valueName.length; k++) {
							if (!("VISIT_CODE".equals(valueName[k])
									|| "DR_CODE".equals(valueName[k])
									|| "DEPT_CODE".equals(valueName[k])
									|| "DEPT_DESC".equals(valueName[k]) || "ADM_DATE"
									.equals(valueName[k]))) {
								tableParm.addData(valueName[k], "");
							}
						}
					}

					if (result1.getInt("OLDCLIENT", i) > 0) {

						if ("老客户".equals(result.getValue("VISIT_CODE", j))) {
//							System.out.println(result.getValue("VISIT_CODE", j));
							tableParm.addRowData(result, j);
							String[] valueD = result.getValue("ADM_DATE", j)
									.split("/");
							tableParm.addData("YEAR", valueD[0]);
							tableParm.addData("MONTH", valueD[1]);
							tableParm.addData("DAY", valueD[2]);
							tableParm.addData("VISIT_CODE_COUNT", result1
									.getInt("OLDCLIENT", i));
						}
					} else {
						String[] valueD = result.getValue("ADM_DATE", j).split(
								"/");
						tableParm.addData("YEAR", valueD[0]);
						tableParm.addData("MONTH", valueD[1]);
						tableParm.addData("DAY", valueD[2]);
						tableParm.addData("VISIT_CODE_COUNT", 0);
						tableParm.addData("VISIT_CODE", "老客户");
						tableParm.addData("DR_CODE", result.getValue("DR_CODE",
								j));
						tableParm.addData("DEPT_CODE", result.getValue(
								"DEPT_CODE", j));
						tableParm.addData("DEPT_DESC", result.getValue(
								"DEPT_DESC", j));
						tableParm.addData("ADM_DATE", result.getValue(
								"ADM_DATE", j));
						for (int k = 0; k < valueName.length; k++) {
							if (!("VISIT_CODE".equals(valueName[k])
									|| "DR_CODE".equals(valueName[k])
									|| "DEPT_CODE".equals(valueName[k])
									|| "DEPT_DESC".equals(valueName[k]) || "ADM_DATE"
									.equals(valueName[k]))) {
								tableParm.addData(valueName[k], "");
							}
						}
					}

				}
			}

		}

		tableParm.setCount(tableParm.getCount("ADM_DATE"));
		return tableParm;
		
		} catch (Exception e) {
			// TODO: handle exception
			
			if(isDedug){  
				System.out.println(" come in class: OPDDoctorWorkAcountControl.class ，method ：selectData");
				e.printStackTrace();
			}
			return null;
		}
	}
		
}
