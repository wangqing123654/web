package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * 
 * 
 * <p>
 * Title:每日门诊量情况汇总表
 * </p>
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author huangtt 2015.05.04
 * @version 1.0
 */
public class REGDailyOutpatientSummaryControl extends TControl{
	private TTable table;
	private TParm tableParm = new TParm();
	private String parmMap="";  
	private String parmMap_ek="";  //儿科
	private String parmMap_jz="";  //急诊
	private String parmMap_cr="";  //成人
	private String parmMap_fk="";  //妇科
	private String parmMap_ck="";  //产科
	private String parmMap_jhsy="";  //计划生育


	public void onInit() {
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", StringTool.rollDate(today, -30));
		this.setValue("E_DATE", today);
		table = (TTable) this.getComponent("TABLE");
		onInitTable();
	}
	
	public void onInitTable(){
		String header = "年,50;月,50;日,50;科室,100;项目,100;数量,80";
	    parmMap="YEAR;MONTH;DAY;DEPT_CODE;PROJECT;COUNT";
		parmMap_ek="TRQ_EK;TJ_EK;020325;020311;020312;020314;020313;020326;020319;020320;020318;020321;020400;020303;020308;020316;020302;020301;YGGY_EK";
		parmMap_jz="020103;020104;020102;YGGY_JZ";
		parmMap_cr="020705;YGGY_CR";
		parmMap_fk = "TRQ_FK;TJ_FK;020701;YGGY_FK";
		parmMap_ck = "TRQ_CK;TJ_CK;020702;TCKH_CK;YGGY_CK";
		parmMap_jhsy = "020800;YGGY_JHSY;TRQ_JHSY;TJ_JHSY";
		String column ="0,left;1,left;2,left;3,left;4,left;5,right";
		table.setHeader(header);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);


	
	}
	/**
	 * 表格显示整理
	 */
	public void onShowTableValue(){
		TParm showParm = new TParm();
		showParm.setData("TRQ_EK", "体验券");
		showParm.setData("TJ_EK", "体检");
		showParm.setData("020325", "儿外");
		showParm.setData("020311", "普外");
		showParm.setData("020312", "骨科");
		showParm.setData("020314", "泌尿外");
		showParm.setData("020313", "胸外");
		showParm.setData("020326", "儿内");
		showParm.setData("020319", "口腔");
		showParm.setData("020320", "ENT");
		showParm.setData("020318", "眼科");
		showParm.setData("020321", "皮肤");
		showParm.setData("020400", "保健");
		showParm.setData("020303", "肾病");
		showParm.setData("020308", "神内");
		showParm.setData("020316", "新生儿内");
		showParm.setData("020302", "消化");
		showParm.setData("020301", "呼吸内科");
		showParm.setData("YGGY_EK", "员工购药");
		showParm.setData("020103", "儿内急诊");
		showParm.setData("020104", "儿外急诊");
		showParm.setData("020102", "产科急诊");
		showParm.setData("YGGY_JZ", "员工购药");
		showParm.setData("020705", "内科");
		showParm.setData("YGGY_CR", "员工购药");
		showParm.setData("TRQ_FK", "体验券");
		showParm.setData("TJ_FK", "体检");
		showParm.setData("020701", "随诊患者");
		showParm.setData("YGGY_FK", "员工购药");
		showParm.setData("TRQ_CK", "体验券");
		showParm.setData("TJ_CK", "体检");
		showParm.setData("020702", "随诊患者");
		showParm.setData("TCKH_CK", "套餐客户");
		showParm.setData("YGGY_CK", "员工购药");
		showParm.setData("020800", "计划生育");
		showParm.setData("YGGY_JHSY", "员工购药");
		showParm.setData("TRQ_JHSY", "体验券");
		showParm.setData("TJ_JHSY", "体检");
		
		
		for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
			tableParm.setData("PROJECT", i, showParm.getValue(tableParm.getValue("PROJECT", i)));
		}

	}
	
	public void onQuery(){
		tableParm = new TParm();
		String sDate = this.getValueString("S_DATE").replace("-", "").replace(
				"/", "").substring(0, 8);
		String eDate = this.getValueString("E_DATE").replace("-", "").replace(
				"/", "").substring(0, 8);
		
		
		List<String> admDateAll = new ArrayList<String>();
		
		String sql = "SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, REALDEPT_CODE, COUNT(CASE_NO) COUNT" +
				" FROM REG_PATADM WHERE REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" GROUP BY ADM_DATE,REALDEPT_CODE" +
				" ORDER BY ADM_DATE,REALDEPT_CODE";
		TParm regCount = new TParm(TJDODBTool.getInstance().select(sql)); //所有科室的的挂号数
		
		for (int i = 0; i < regCount.getCount(); i++) {
			if(!admDateAll.contains(regCount.getValue("ADM_DATE", i))){
				admDateAll.add(regCount.getValue("ADM_DATE", i));
			}
		}
		
		//儿科体验券 02，体检 01，员工购药 03
		String[] depts = parmMap_ek.split(";");
		String dept = "";
		for (int i = 2; i < depts.length-1; i++) {
			dept = dept + "'"+depts[i]+"',";
		}
		dept = dept.substring(0,dept.length()-1);
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM" +
				" WHERE CTZ1_CODE IN ('02', '01', '03','04')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND REALDEPT_CODE IN ("+dept+")" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE";
//		System.out.println("sql==="+sql);
		TParm ekCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < ekCount.getCount(); i++) {
			if(!admDateAll.contains(ekCount.getValue("ADM_DATE", i))){
				admDateAll.add(ekCount.getValue("ADM_DATE", i));
			}
		}
		
		//急诊 员工购药 03
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM" +
				" WHERE     CTZ1_CODE IN ('03')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND ADM_TYPE = 'E'" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE";
		TParm jzCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < jzCount.getCount(); i++) {
			if(!admDateAll.contains(jzCount.getValue("ADM_DATE", i))){
				admDateAll.add(jzCount.getValue("ADM_DATE", i));
			}
		}
		
		//成人 员工购药 03
		sql = "SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM WHERE CTZ1_CODE IN ('03')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND REALDEPT_CODE IN('020705')" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE ";
		TParm crCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < crCount.getCount(); i++) {
			if(!admDateAll.contains(crCount.getValue("ADM_DATE", i))){
				admDateAll.add(crCount.getValue("ADM_DATE", i));
			}
		}
		
		//妇科体验券 02，体检 01，员工购药 03
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM" +
				"  WHERE CTZ1_CODE IN ('02', '01', '03','04')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND REALDEPT_CODE='020701'" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE ";
		
		TParm fkCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < fkCount.getCount(); i++) {
			if(!admDateAll.contains(fkCount.getValue("ADM_DATE", i))){
				admDateAll.add(fkCount.getValue("ADM_DATE", i));
			}
		}
		
		//产科体验券 02，体检 01，员工购药 03 ,套餐 客户04
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
		" FROM REG_PATADM" +
		"  WHERE CTZ1_CODE IN ('02', '01', '03','04')" +
		" AND REGCAN_USER IS NULL" +
		" AND ARRIVE_FLG = 'Y'" +
		" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
		" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
		" AND REALDEPT_CODE='020702'" +
		" GROUP BY ADM_DATE, CTZ1_CODE" +
		" ORDER BY ADM_DATE, CTZ1_CODE ";
		TParm ckCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < ckCount.getCount(); i++) {
			if(!admDateAll.contains(ckCount.getValue("ADM_DATE", i))){
				admDateAll.add(ckCount.getValue("ADM_DATE", i));
			}
		}
		
		//计划生育  员工购药 03 
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
		" FROM REG_PATADM" +
		"  WHERE CTZ1_CODE IN ('02', '01','03')" +
		" AND REGCAN_USER IS NULL" +
		" AND ARRIVE_FLG = 'Y'" +
		" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
		" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
		" AND REALDEPT_CODE='020800'" +
		" GROUP BY ADM_DATE, CTZ1_CODE" +
		" ORDER BY ADM_DATE, CTZ1_CODE ";
		TParm jhsyCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < jhsyCount.getCount(); i++) {
			if(!admDateAll.contains(jhsyCount.getValue("ADM_DATE", i))){
				admDateAll.add(jhsyCount.getValue("ADM_DATE", i));
			}
		}
		
		//套餐销售数据
		sql = "SELECT TO_CHAR (START_DATE, 'YYYY/MM/DD') ADM_DATE ,COUNT(TRADE_NO) COUNT" +
				" FROM MEM_PACKAGE_TRADE_M" +
				" WHERE BILL_DATE BETWEEN TO_DATE ('"+sDate+"000000', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"235959', 'YYYYMMDDHH24MISS')" +
				" GROUP BY START_DATE" +
				" ORDER BY START_DATE";
		TParm packageCount = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(packageCount);
		for (int i = 0; i < jhsyCount.getCount(); i++) {
			if(!admDateAll.contains(jhsyCount.getValue("ADM_DATE", i))){
				admDateAll.add(jhsyCount.getValue("ADM_DATE", i));
			}
		}
		
		String [] depts_jz = parmMap_jz.split(";"); //急诊科室
		String [] depts_cr = parmMap_cr.split(";"); //成人科室
		String [] depts_fk = parmMap_fk.split(";"); //妇科科室
		String [] depts_ck = parmMap_ck.split(";"); //产科科室
		String [] depts_jhsy = parmMap_jhsy.split(";"); //计划生育科室
		
		for (int i = 0; i < admDateAll.size(); i++) {
			String [] valueD = admDateAll.get(i).split("/"); 

			//儿科
			for (int j = 0; j < depts.length; j++) {
//				System.out.println(depts[j]);
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "儿科");
				tableParm.addData("PROJECT", depts[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//急诊
			for (int j = 0; j < depts_jz.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "急诊");
				tableParm.addData("PROJECT", depts_jz[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//成人
			for (int j = 0; j < depts_cr.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "成人");
				tableParm.addData("PROJECT", depts_cr[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//妇科
			for (int j = 0; j < depts_fk.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "妇科");
				tableParm.addData("PROJECT", depts_fk[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//产科
			for (int j = 0; j < depts_ck.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "产科");
				tableParm.addData("PROJECT", depts_ck[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//计划生育
			for (int j = 0; j < depts_jhsy.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "计划生育");
				tableParm.addData("PROJECT", depts_jhsy[j]);
				tableParm.addData("COUNT", "0");
			}
			
		}

		for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
			
			//科室数据合并
			for (int j = 0; j < regCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(regCount.getValue("ADM_DATE", j)) &&
						tableParm.getValue("PROJECT", i).equals(regCount.getValue("REALDEPT_CODE", j))){
					
					tableParm.setData("COUNT", i, regCount.getInt("COUNT", j));
			
				}
			}
			
			//身份数据合并  体验券 02，体检 01，员工购药 03 ,套餐 客户04
			//儿科
			
			for (int j = 0; j < ekCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(ekCount.getValue("ADM_DATE", j)) &&
					"儿科".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = ekCount.getInt("CTZ1_CODE", j);
					String project = "";
					if( v == 2){
						project="TRQ_EK";
					}else if(v == 1){
						project="TJ_EK";
					}else if(v == 3){
						project="YGGY_EK";
					}

					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, ekCount.getInt("COUNT", j));
					}
				}
				
			}
	
			//急诊
			for (int j = 0; j < jzCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(jzCount.getValue("ADM_DATE", j)) &&
						"急诊".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = jzCount.getInt("CTZ1_CODE", j);
					String project = "";
				    if(v == 3){
						project="YGGY_JZ";
					}
				    if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, jzCount.getInt("COUNT", j));
					}
				}
			}
			
			//成人
			for (int j = 0; j < crCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(crCount.getValue("ADM_DATE", j)) &&
						"成人".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = crCount.getInt("CTZ1_CODE", j);
					String project = "";
				    if(v == 3){
						project = "YGGY_CR";
					}
				    if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, crCount.getInt("COUNT", j));
					}
				}
			}
			
			//妇科
			for (int j = 0; j < fkCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(fkCount.getValue("ADM_DATE", j)) &&
						"妇科".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = fkCount.getInt("CTZ1_CODE", j);
					String project = "";
					if( v == 2){
//						tableParm.setData("TRQ_FK", i, fkCount.getInt("COUNT", j));
						project = "TRQ_FK";
					}else if(v == 1){
//						tableParm.setData("TJ_FK", i, fkCount.getInt("COUNT", j));
						project = "TJ_FK";
					}else if(v == 3){
//						tableParm.setData("YGGY_FK", i, fkCount.getInt("COUNT", j));
						project = "YGGY_FK";
					}
					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, fkCount.getInt("COUNT", j));
					}
				}
			}
			
			//产科
			for (int j = 0; j < ckCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(ckCount.getValue("ADM_DATE", j)) &&
						"产科".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = ckCount.getInt("CTZ1_CODE", j);
					String project = "";
					if( v == 2){
						project = "TRQ_CK";
					}else if(v == 1){
						project = "TJ_CK";
					}else if(v == 3){
						project = "YGGY_CK";
					}else if(v == 4){
						project = "TCKH_CK";
					}
					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, ckCount.getInt("COUNT", j));
					}
				}
			}
			
			//计划生育 
			for (int j = 0; j < jhsyCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(jhsyCount.getValue("ADM_DATE", j)) &&
						"计划生育".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = jhsyCount.getInt("CTZ1_CODE", j);
					String project = "";
					if(v == 3){
						project = "YGGY_JHSY";
					}else if(v == 1){
						project = "TJ_JHSY";
					}else if(v == 2){
						project = "TRQ_JHSY";
					}
					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, jhsyCount.getInt("COUNT", j));
					}
				}
			}
	
		}
		
		onShowTableValue();
		
		if(tableParm.getCount("ADM_DATE") == 1){
			this.messageBox("没有要查询的数据!");
			return;
		}
		
		table.setParmValue(tableParm);
		
	}
	
	public void onClear(){
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", StringTool.rollDate(today, -30));
		this.setValue("E_DATE", today);
		table.removeRowAll();
		tableParm = new TParm();

	}
	
	/**
	 * 汇出Excel
	 */
	public void onExecl() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "每日门诊量情况汇总表");
	}
	
	
	
}
