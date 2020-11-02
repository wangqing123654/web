package com.javahis.ui.reg;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * 
 * 
 * <p>
 * Title:客户就诊数据统计表
 * </p>
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author huangtt 2015.05.04
 * @version 1.0
 */
public class REGMedicalStatisticsControl extends TControl {
	private TTable table;

	public void onInit() {
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", StringTool.rollDate(today, -30));
		this.setValue("E_DATE", today);
		table = (TTable) this.getComponent("TABLE");
	}

	public void onQuery() {
		String sDate = this.getValueString("S_DATE").replace("-", "").replace(
				"/", "").substring(0, 8)
				+ "000000";
		String eDate = this.getValueString("E_DATE").replace("-", "").replace(
				"/", "").substring(0, 8)
				+ "235959";
		//表格数据
		TParm tableParm = new TParm();

		//预约挂号数据
		TParm crmCountParm = this.getRegCrmCount(sDate.substring(0, 8), eDate.substring(0, 8));
//		System.out.println(crmCountParm);
		//门急诊到诊人数
		TParm regCountParm = this.getRegCount(sDate, eDate);
		//住院人数
		TParm admCountParm = this.getAdmInpCount(sDate, eDate);
		//套餐金额
		TParm memAmtParm = this.getMemPackageAmt(sDate, eDate);
		//门急诊金额
		TParm regAmtParm = this.getRegAmt(sDate, eDate);
		//住院金额
		TParm admAmtParm = this.getAdmInpAmt(sDate, eDate);
		//套餐数量
		TParm memParm = this.getMemPackageCount(sDate, eDate);
		String header="年,50;月,50;日,50;科别,100;门诊预约,80;门诊到诊,80;急诊人数,80;住院人数,80;";
		String parmMap="YEAR;MONTH;DAY;DEPT_DESC;CRM_COUNT;O_COUNT;E_COUNT;I_COUNT;";
		String column ="0,left;1,left;2,left;3,left;4,right;5,right;6,right;7,right;";
		int col =7;
		List<String> headerAll = new ArrayList<String>();
		List<String> parmMapAll = new ArrayList<String>();
		if(memParm.getCount() > 0){
			for (int i = 0; i < memParm.getCount(); i++) {
				if(!parmMapAll.contains(memParm.getValue("PACKAGE_CODE", i))){
					headerAll.add(memParm.getValue("PACKAGE_DESC", i));
					parmMapAll.add(memParm.getValue("PACKAGE_CODE", i));
				}
			}
			for (int i = 0; i < headerAll.size(); i++) {
				header = header + headerAll.get(i)+",100;";
				parmMap = parmMap + parmMapAll.get(i)+";";
				col = 8+i;
				column = column + col+",right;";
			}
			
		}
		header += "套餐金额,100,double,#########0.00;门诊金额,100,double,#########0.00;急诊金额,100,double,#########0.00;住院金额,100,double,#########0.00;收费金额,100,double,#########0.00";
		parmMap += "M_AR_AMT;O_AR_AMT;E_AR_AMT;I_AR_AMT;SUM_AMT";
		for (int i = 1; i < 6; i++) {
			column = column + (col+i)+",right;";
		}
		column = column.substring(0, column.length()-1);
		
		table.setHeader(header);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);
		
		//预约和门急诊人数合并

		if(crmCountParm.getCount("ADM_DATE") < 0 ){
			for (int i = 0; i < regCountParm.getCount("ADM_DATE"); i++) {
				String [] valueD = regCountParm.getValue("ADM_DATE", i).split("/"); 			
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", regCountParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", regCountParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", regCountParm.getInt("O_COUNT", i));
				tableParm.addData("E_COUNT", regCountParm.getInt("E_COUNT", i));
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int j = 0; j < parmMapAll.size(); j++) {
						tableParm.addData(parmMapAll.get(j), "0");
					}
				}

			}
		}else{
			for (int i = 0; i < regCountParm.getCount("ADM_DATE"); i++) {			
				for (int j = 0; j < crmCountParm.getCount("ADM_DATE"); j++) {
					if(regCountParm.getValue("ADM_DATE", i).equals(crmCountParm.getValue("ADM_DATE", j)) &&
							regCountParm.getValue("DEPT_DESC", i).equals(crmCountParm.getValue("DEPT_DESC", j))){
						String [] valueD = regCountParm.getValue("ADM_DATE", i).split("/"); 
						tableParm.addData("YEAR", valueD[0]);
						tableParm.addData("MONTH", valueD[1]);
						tableParm.addData("DAY", valueD[2]);
						tableParm.addData("ADM_DATE", regCountParm.getValue("ADM_DATE", i));
						tableParm.addData("DEPT_DESC", regCountParm.getValue("DEPT_DESC", i));
						tableParm.addData("CRM_COUNT", crmCountParm.getInt("CRM_COUNT", j));
						tableParm.addData("O_COUNT", regCountParm.getInt("O_COUNT", i));
						tableParm.addData("E_COUNT", regCountParm.getInt("E_COUNT", i));
						tableParm.addData("I_COUNT", "0");
						tableParm.addData("M_AR_AMT", "0.00");
						tableParm.addData("O_AR_AMT", "0.00");
						tableParm.addData("E_AR_AMT", "0.00");
						tableParm.addData("I_AR_AMT", "0.00");
						tableParm.addData("SUM_AMT", "0.00");
						if(memParm.getCount() > 0){
							for (int k = 0; k < parmMapAll.size(); k++) {
								tableParm.addData(parmMapAll.get(k), "0");
							}
						}
						regCountParm.removeRow(i);
						crmCountParm.removeRow(j);
						
					}
				}
			}
			for (int i = 0; i < regCountParm.getCount("ADM_DATE"); i++) {
				String [] valueD = regCountParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", regCountParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", regCountParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", regCountParm.getInt("O_COUNT", i));
				tableParm.addData("E_COUNT", regCountParm.getInt("E_COUNT", i));
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}

			}
			for (int i = 0; i < crmCountParm.getCount("ADM_DATE"); i++) {
				String [] valueD = crmCountParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", crmCountParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", crmCountParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", crmCountParm.getInt("CRM_COUNT", i));
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
			
			
		}
		
		//门急诊，预约数据  和住院数据合并
		if(tableParm.getCount("ADM_DATE") < 0){
			for (int i = 0; i < admCountParm.getCount("ADM_DATE"); i++) {
				String [] valueD = admCountParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admCountParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", admCountParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", admCountParm.getInt("I_COUNT", i));
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
		}else{
			for (int i = 0; i < tableParm.getCount("DEPT_DESC"); i++) {
				for (int j = 0; j < admCountParm.getCount("ADM_DATE"); j++) {
					if(tableParm.getValue("ADM_DATE", i).equals(admCountParm.getValue("ADM_DATE", j)) &&
							tableParm.getValue("DEPT_DESC", i).equals(admCountParm.getValue("DEPT_DESC", j))){
						tableParm.setData("I_COUNT", i, admCountParm.getInt("I_COUNT", j));
						admCountParm.removeRow(j);
					}
				}
			}
			for (int i = 0; i < admCountParm.getCount("ADM_DATE"); i++) {
				String [] valueD = admCountParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admCountParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", admCountParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", admCountParm.getInt("I_COUNT", i));
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
		}
		
		//门急诊，预约数据,住院数据和套餐数据合并
		List<String> admDates = new ArrayList<String>();
		if(memParm.getCount() > 0){
			
			//取日期
			for (int k = 0; k < memParm.getCount(); k++) {
				if(!admDates.contains(memParm.getValue("ADM_DATE", k))){
					admDates.add(memParm.getValue("ADM_DATE", k));
				}
			}
			
			if( admDates.size()>0){
				//赋值 日期与科室
				for (int i = 0; i < admDates.size(); i++) {
					String [] valueD = admDates.get(i).split("/"); 
					tableParm.addData("YEAR", valueD[0]);
					tableParm.addData("MONTH", valueD[1]);
					tableParm.addData("DAY", valueD[2]);
					tableParm.addData("ADM_DATE", admDates.get(i));
					tableParm.addData("DEPT_DESC", "其他");
					tableParm.addData("CRM_COUNT", "0");
					tableParm.addData("O_COUNT", "0");
					tableParm.addData("E_COUNT", "0");
					tableParm.addData("I_COUNT", "0");
					tableParm.addData("M_AR_AMT", "0.00");
					tableParm.addData("O_AR_AMT", "0.00");
					tableParm.addData("E_AR_AMT", "0.00");
					tableParm.addData("I_AR_AMT", "0.00");
					tableParm.addData("SUM_AMT", "0.00");
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
				
				//套餐数量赋值
				for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
					if("其他".equals(tableParm.getValue("DEPT_DESC", i))){
						for (int j = 0; j < memParm.getCount(); j++) {
							if(tableParm.getValue("ADM_DATE", i).equals(memParm.getValue("ADM_DATE", j))){
								for (int k = 0; k < parmMapAll.size(); k++) {
									if(memParm.getValue("PACKAGE_CODE", j).equals(parmMapAll.get(k))){
										tableParm.setData(parmMapAll.get(k), i, memParm.getInt("M_COUNT",j));
									}
								}
								
							}
							
						}
					}
					
				}	
			}

		}
		
		//门急诊，预约数据,住院数据，套餐数据与套餐金额合并		
		for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
			for (int j = 0; j < memAmtParm.getCount() ; j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(memAmtParm.getValue("ADM_DATE", j)) &&
						tableParm.getValue("DEPT_DESC", i).equals("其他")){
					tableParm.setData("M_AR_AMT", i, memAmtParm.getDouble("M_AR_AMT", j));
				}
			}
		}
		
		
		//门急诊，预约数据,住院数据，套餐数据,套餐金额与门急诊金额合并	
		if(tableParm.getCount("ADM_DATE") < 0){
			for (int i = 0; i < regAmtParm.getCount("ADM_DATE"); i++) {
				String [] valueD = regAmtParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", regAmtParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", regAmtParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", regAmtParm.getDouble("O_AR_AMT", i));
				tableParm.addData("E_AR_AMT", regAmtParm.getDouble("E_AR_AMT", i));
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
		}else{
			for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
				for (int j = 0; j < regAmtParm.getCount("ADM_DATE"); j++) {
					if(tableParm.getValue("ADM_DATE", i).equals(regAmtParm.getValue("ADM_DATE", j)) &&
							tableParm.getValue("DEPT_DESC", i).equals(regAmtParm.getValue("DEPT_DESC", j))){						
						tableParm.setData("O_AR_AMT", i, regAmtParm.getDouble("O_AR_AMT", j));
						tableParm.setData("E_AR_AMT", i, regAmtParm.getDouble("E_AR_AMT", j));
						regAmtParm.removeRow(j);
						break;
					}
				}
			}
			for (int i = 0; i < regAmtParm.getCount("ADM_DATE"); i++) {
				String [] valueD = regAmtParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", regAmtParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", regAmtParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", regAmtParm.getDouble("O_AR_AMT", i));
				tableParm.addData("E_AR_AMT", regAmtParm.getDouble("E_AR_AMT", i));
				tableParm.addData("I_AR_AMT", "0.00");
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
			
		}
		
		
		//门急诊，预约数据,住院数据，套餐数据,套餐金额，门急诊金额与住院金额合并
		
		if(tableParm.getCount("ADM_DATE") < 0){
			for (int i = 0; i < admAmtParm.getCount("ADM_DATE"); i++) {
				String [] valueD = admAmtParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admAmtParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", admAmtParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", admAmtParm.getDouble("I_AR_AMT", i));
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
		}else{
			for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
				for (int j = 0; j < admAmtParm.getCount("ADM_DATE"); j++) {
					if(tableParm.getValue("ADM_DATE", i).equals(admAmtParm.getValue("ADM_DATE", j)) &&
							tableParm.getValue("DEPT_DESC", i).equals(admAmtParm.getValue("DEPT_DESC", j))){
						tableParm.setData("I_AR_AMT", i, admAmtParm.getDouble("I_AR_AMT", j));
						admAmtParm.removeRow(j);
					}
				}
			}
			
			for (int i = 0; i < admAmtParm.getCount("ADM_DATE"); i++) {
				String [] valueD = admAmtParm.getValue("ADM_DATE", i).split("/"); 
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admAmtParm.getValue("ADM_DATE", i));
				tableParm.addData("DEPT_DESC", admAmtParm.getValue("DEPT_DESC", i));
				tableParm.addData("CRM_COUNT", "0");
				tableParm.addData("O_COUNT", "0");
				tableParm.addData("E_COUNT", "0");
				tableParm.addData("I_COUNT", "0");
				tableParm.addData("M_AR_AMT", "0.00");
				tableParm.addData("O_AR_AMT", "0.00");
				tableParm.addData("E_AR_AMT", "0.00");
				tableParm.addData("I_AR_AMT", admAmtParm.getDouble("I_AR_AMT", i));
				tableParm.addData("SUM_AMT", "0.00");
				if(memParm.getCount() > 0){
					for (int k = 0; k < parmMapAll.size(); k++) {
						tableParm.addData(parmMapAll.get(k), "0");
					}
				}
			}
			
		}
		
		if(tableParm.getCount("ADM_DATE") < 0){
			this.messageBox("没有要查询的数据!");
			return;
		}
		
		List<String> admDateAll = new ArrayList<String>();
		for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
			double sumAmt = tableParm.getDouble("M_AR_AMT", i)+tableParm.getDouble("O_AR_AMT", i)+
				tableParm.getDouble("E_AR_AMT", i)+tableParm.getDouble("I_AR_AMT", i);
			tableParm.setData("SUM_AMT", i, sumAmt);
			if(!admDateAll.contains(tableParm.getValue("ADM_DATE", i))){
				admDateAll.add(tableParm.getValue("ADM_DATE", i));
			}
		}
		
		for (int i = 0; i < admDateAll.size()-1; i++) {
			for (int j = i+1; j < admDateAll.size(); j++) {
				if(admDateAll.get(i).compareTo(admDateAll.get(j)) > 0 ){
					String a = admDateAll.get(i);
					admDateAll.set(i, admDateAll.get(j));
					admDateAll.set(j, a);
				}
			}
		}
		
		
		TParm parmAll = new TParm();
		for (int i = 0; i < admDateAll.size(); i++) {
			TParm other = new TParm();
			for (int k = 0; k < tableParm.getCount("ADM_DATE"); k++) {
				if(admDateAll.get(i).equals(tableParm.getValue("ADM_DATE", k))){
					if("其他".equals(tableParm.getValue("DEPT_DESC", k))){
						other.addRowData(tableParm, k);
					}else{
						parmAll.addRowData(tableParm, k);
					}
				}
			}
			if( other != null){
				parmAll.addRowData(other, 0);
			}
			
		}
		
		table.setParmValue(parmAll);

	}
	
	public void onClear(){
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", StringTool.rollDate(today, -30));
		this.setValue("E_DATE", today);
		table.removeRowAll();
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExecl() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "北京爱育华妇儿医院客户就诊数据统计表");
	}
	
	
	/**
	 * 取得预约挂号数
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getRegCrmCount(String sDate,String eDate){
		TParm result = new TParm();
		 if(this.dateDiff(sDate, eDate, "yyyyMMdd").length() == 0){
			 return new TParm();
		 }
		 int count = Integer.parseInt(this.dateDiff(sDate, eDate, "yyyyMMdd"));
		 SimpleDateFormat sdf =   new SimpleDateFormat("yyyyMMdd");
		 Date d = null;
		 try {
			d = (Date) sdf.parse(sDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Timestamp ts = new Timestamp (d.getTime());
		for (int i = 0; i <= count; i++) {

			String admDate = StringTool.rollDate(ts, i).toString().replace("/", "-").substring(0, 10);
			TParm parm = new TParm();
			parm.setData("admDate", admDate);
			parm.setData("session", "");
			parm.setData("deptCode", "");
			parm.setData("drCode", "");
			parm.setData("quegroup", "");
			TParm orderParm = TIOM_AppServer.executeAction("action.reg.REGCRMAction","getOrder", parm);
			//，是查询预约状态为1、4和5的数据  Timestamp
			for (int j = 0; j < orderParm.getCount(); j++) {
				if("1".equals(orderParm.getValue("STATUS", j)) ||
						"4".equals(orderParm.getValue("STATUS", j)) || 
						"5".equals(orderParm.getValue("STATUS", j)) ){
					result.addData("ADM_DATE", orderParm.getValue("ADM_DATE", j).replace("-", "/"));
					result.addData("DEPT_DESC", orderParm.getValue("DEPT_DESC", j));
					result.addData("STATUS", orderParm.getValue("STATUS", j));
				}
			}
			
		}
		if(result.getCount("ADM_DATE") < 0){
			return new TParm();
		}
		TParm orderCount = new TParm();
		for (int i = 0; i < result.getCount("ADM_DATE")-1; i++) {
			if(!(result.getValue("ADM_DATE", i).equals(result.getValue("ADM_DATE", i+1)) && 
					result.getValue("DEPT_DESC", i).equals(result.getValue("DEPT_DESC", i+1)))){
				orderCount.addData("ADM_DATE", result.getValue("ADM_DATE", i));
				orderCount.addData("DEPT_DESC", result.getValue("DEPT_DESC", i));
				orderCount.addData("CRM_COUNT", "");
				
			}
		}
		orderCount.addData("ADM_DATE", result.getValue("ADM_DATE", result.getCount("ADM_DATE")-1));
		orderCount.addData("DEPT_DESC", result.getValue("DEPT_DESC", result.getCount("ADM_DATE")-1));
		orderCount.addData("CRM_COUNT", "");
		
		for (int i = 0; i < orderCount.getCount("ADM_DATE"); i++) {
			int crmCount =0;
			for (int j = 0; j < result.getCount("ADM_DATE"); j++) {
				if(orderCount.getValue("ADM_DATE", i).equals(result.getValue("ADM_DATE", j)) && 
						orderCount.getValue("DEPT_DESC", i).equals(result.getValue("DEPT_DESC", j))){
					crmCount ++;
				}
			}
			orderCount.setData("CRM_COUNT", i, crmCount);
		}
		orderCount.setCount(orderCount.getCount("ADM_DATE"));
		
		return orderCount;
		
		
		
	}
	
	/**
	 * 取得住院收费总额
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getAdmInpAmt(String sDate,String eDate){
//		String sql = " SELECT TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD') ADM_DATE," +
//				" E.DEPT_CHN_DESC DEPT_DESC, SUM (C.WRT_OFF_AMT) I_AR_AMT" +
//				" FROM BIL_ACCOUNT A,BIL_IBS_RECPM B, BIL_IBS_RECPD C," +
//				" (SELECT DISTINCT BILL_NO, DEPT_CODE FROM IBS_ORDM) D," +
//				" SYS_DEPT E WHERE A.ACCOUNT_DATE BETWEEN " +
//				" TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
//				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')" +
//				" AND A.ACCOUNT_TYPE = 'IBS'" +
//				" AND A.ACCOUNT_SEQ = B.ACCOUNT_SEQ" +
//				" AND B.RECEIPT_NO = C.RECEIPT_NO" +
//				" AND C.BILL_NO = D.BILL_NO" +
//				" AND D.DEPT_CODE = E.DEPT_CODE" +
//				" GROUP BY TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD'), E.DEPT_CHN_DESC" +
//				" ORDER BY TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD'), E.DEPT_CHN_DESC";
		String sql = " SELECT A.ADM_DATE, A.DEPT_DESC,NVL(A.LUMP,0) + NVL(A.AR_AMT,0) I_AR_AMT" +
				" FROM (SELECT CASE WHEN A.ADM_DATE IS NULL THEN B.ADM_DATE ELSE A.ADM_DATE" +
				" END ADM_DATE,CASE WHEN A.DEPT_DESC IS NULL THEN B.DEPT_DESC ELSE A.DEPT_DESC" +
				" END DEPT_DESC, A.LUMP, B.AR_AMT" +
				" FROM (  SELECT A.ADM_DATE, B.DEPT_CHN_DESC DEPT_DESC, SUM (LUMP) LUMP" +
				" FROM (SELECT DISTINCT C.DEPT_CODE, TO_CHAR (B.ACCOUNT_DATE, 'YYYY/MM/DD')" +
				" ADM_DATE, A.RECEIPT_NO, A.LUMPWORK_AMT + A.LUMPWORK_OUT_AMT LUMP" +
				" FROM BIL_IBS_RECPM A, BIL_ACCOUNT B, IBS_BILLM C" +
				" WHERE A.RESET_RECEIPT_NO IS NULL AND A.AR_AMT > 0" +
				" AND A.LUMPWORK_AMT <> 0 AND A.ACCOUNT_SEQ = B.ACCOUNT_SEQ" +
				" AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND A.RECEIPT_NO = C.RECEIPT_NO) A,SYS_DEPT B" +
				" WHERE A.DEPT_CODE = B.DEPT_CODE" +
				" GROUP BY A.ADM_DATE, B.DEPT_CHN_DESC) A" +
				" FULL OUTER JOIN" +
				" (  SELECT A.ADM_DATE,B.DEPT_CHN_DESC DEPT_DESC,SUM (AR_AMT) AR_AMT" +
				" FROM (SELECT DISTINCT C.DEPT_CODE,TO_CHAR (B.ACCOUNT_DATE, 'YYYY/MM/DD') ADM_DATE," +
				" A.RECEIPT_NO, A.AR_AMT FROM BIL_IBS_RECPM A, BIL_ACCOUNT B, IBS_BILLM C" +
				" WHERE A.RESET_RECEIPT_NO IS NULL AND A.AR_AMT > 0 AND (   A.LUMPWORK_AMT = 0.00" +
				" OR A.LUMPWORK_AMT IS NULL) AND A.ACCOUNT_SEQ = B.ACCOUNT_SEQ" +
				" AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ( '"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND A.RECEIPT_NO = C.RECEIPT_NO) A,SYS_DEPT B" +
				" WHERE A.DEPT_CODE = B.DEPT_CODE GROUP BY A.ADM_DATE, B.DEPT_CHN_DESC) B" +
				" ON A.ADM_DATE = B.ADM_DATE AND A.DEPT_DESC = B.DEPT_DESC) A" +
				" ORDER BY A.ADM_DATE, A.DEPT_DESC";
			
//		System.out.println("住院收费总额===="+sql); 
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 取得门急诊收费总额
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getRegAmt(String sDate,String eDate){
		String sql = "SELECT * FROM (SELECT CASE WHEN A.ADM_DATE IS NULL THEN B.ADM_DATE" +
				" ELSE A.ADM_DATE END ADM_DATE, CASE WHEN A.DEPT_DESC IS NULL THEN B.DEPT_DESC" +
				" ELSE A.DEPT_DESC END DEPT_DESC,A.O_AR_AMT, B.E_AR_AMT" +
				" FROM (  SELECT A.ADM_DATE, A.DEPT_DESC, SUM (A.AR_AMT) O_AR_AMT" +
				" FROM (SELECT TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD') ADM_DATE, A.CASE_NO," +
				" C.DEPT_CHN_DESC DEPT_DESC, A.AR_AMT" +
				"  FROM BIL_OPB_RECP A, REG_PATADM B, SYS_DEPT C WHERE A.ACCOUNT_SEQ IN" +
				" (SELECT ACCOUNT_SEQ FROM BIL_ACCOUNT WHERE ACCOUNT_DATE " +
				" BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS') " +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND ADM_TYPE = 'O' AND ACCOUNT_TYPE = 'OPB') AND A.CASE_NO = B.CASE_NO" +
				" AND B.DEPT_CODE = C.DEPT_CODE) A GROUP BY A.ADM_DATE, A.DEPT_DESC) A" +
				" FULL OUTER JOIN" +
				" ( SELECT A.ADM_DATE, A.DEPT_DESC, SUM (A.AR_AMT) E_AR_AMT" +
				" FROM (SELECT TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD') ADM_DATE, A.CASE_NO," +
				" C.DEPT_CHN_DESC DEPT_DESC,A.AR_AMT FROM BIL_OPB_RECP A, REG_PATADM B, " +
				" SYS_DEPT C WHERE A.ACCOUNT_SEQ IN (SELECT ACCOUNT_SEQ FROM BIL_ACCOUNT" +
				" WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS') AND ADM_TYPE = 'E'" +
				" AND ACCOUNT_TYPE = 'OPB') AND A.CASE_NO = B.CASE_NO" +
				" AND B.DEPT_CODE = C.DEPT_CODE) A GROUP BY A.ADM_DATE, A.DEPT_DESC) B" +
				" ON A.ADM_DATE = B.ADM_DATE AND A.DEPT_DESC = B.DEPT_DESC) A" +
				" ORDER BY A.ADM_DATE, A.DEPT_DESC";
//		System.out.println("取得门急诊收费总额==="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 取得套餐的总额
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getMemPackageAmt(String sDate,String eDate){
		String sql = "SELECT TO_CHAR (ACCOUNT_DATE, 'YYYY/MM/DD') ADM_DATE, " +
				" SUM (AR_AMT) M_AR_AMT FROM MEM_PACKAGE_TRADE_M" +
				" WHERE ACCOUNT_SEQ IN (SELECT ACCOUNT_SEQ" +
				" FROM EKT_ACCOUNT WHERE ACCOUNT_DATE " +
				" BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS'))" +
				" GROUP BY TO_CHAR (ACCOUNT_DATE, 'YYYY/MM/DD')";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	} 
	
	/**
	 * 取得套餐的个数
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getMemPackageCount(String sDate,String eDate){
		String sql = "SELECT A.ACCOUNT_DATE ADM_DATE, A.PACKAGE_CODE, A.PACKAGE_DESC, " +
				" COUNT (A.TRADE_NO) M_COUNT FROM (SELECT DISTINCT" +
				" A.TRADE_NO,A.PACKAGE_CODE, A.PACKAGE_DESC," +
				" TO_CHAR (B.ACCOUNT_DATE, 'YYYY/MM/DD') ACCOUNT_DATE" +
				" FROM MEM_PAT_PACKAGE_SECTION A, MEM_PACKAGE_TRADE_M B" +
				" WHERE B.ACCOUNT_DATE BETWEEN " +
				" TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				"  AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" +
				" AND A.TRADE_NO = B.TRADE_NO) A" +
				" GROUP BY A.ACCOUNT_DATE,A.PACKAGE_CODE, A.PACKAGE_DESC" +
				" ORDER BY A.ACCOUNT_DATE,A.PACKAGE_CODE, A.PACKAGE_DESC";
//		System.out.println("取得套餐的个数==="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	} 
	
	/**
	 * 取得住院人数
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getAdmInpCount(String sDate,String eDate){
		String sql = "SELECT TO_CHAR (A.IN_DATE, 'YYYY/MM/DD') ADM_DATE," +
				" B.DEPT_CHN_DESC DEPT_DESC," +
				" COUNT (A.CASE_NO) I_COUNT" +
				" FROM ADM_INP A, SYS_DEPT B" +
				" WHERE A.IN_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" +
				" AND A.DS_DATE IS NULL" +
				" AND A.DEPT_CODE = B.DEPT_CODE" +
				" GROUP BY TO_CHAR (IN_DATE, 'YYYY/MM/DD'), B.DEPT_CHN_DESC" +
				" ORDER BY TO_CHAR (IN_DATE, 'YYYY/MM/DD'), B.DEPT_CHN_DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 取得门急诊挂号数
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getRegCount(String sDate,String eDate){
		String sql = "SELECT * FROM (SELECT CASE" +
				" WHEN A.ADM_DATE IS NULL THEN B.ADM_DATE" +
				" ELSE A.ADM_DATE END ADM_DATE," +
				" CASE WHEN A.DEPT_DESC IS NULL THEN B.DEPT_DESC" +
				" ELSE A.DEPT_DESC END DEPT_DESC," +
				" A.O_COUNT,B.E_COUNT" +
				" FROM (  SELECT TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
				" B.DEPT_CHN_DESC DEPT_DESC," +
				" COUNT (A.CASE_NO) O_COUNT" +
				" FROM REG_PATADM A, SYS_DEPT B" +
				" WHERE A.ADM_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND A.ARRIVE_FLG = 'Y'" +
				" AND A.REGCAN_USER IS NULL" +
				" AND A.ADM_TYPE = 'O'" +
				" AND A.DEPT_CODE = B.DEPT_CODE" +
				" GROUP BY TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD'), B.DEPT_CHN_DESC" +
				" ORDER BY TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD'), B.DEPT_CHN_DESC) A" +
				" FULL OUTER JOIN" +
				" (  SELECT TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
				" B.DEPT_CHN_DESC DEPT_DESC, COUNT (A.CASE_NO) E_COUNT" +
				" FROM REG_PATADM A, SYS_DEPT B" +
				" WHERE A.ADM_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND A.ARRIVE_FLG = 'Y'" +
				" AND A.REGCAN_USER IS NULL" +
				" AND A.ADM_TYPE = 'E'" +
				" AND A.DEPT_CODE = B.DEPT_CODE" +
				" GROUP BY TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD'), B.DEPT_CHN_DESC" +
				" ORDER BY TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD'), B.DEPT_CHN_DESC) B" +
				" ON A.ADM_DATE = B.ADM_DATE AND A.DEPT_DESC = B.DEPT_DESC) A" +
				" ORDER BY A.ADM_DATE, DEPT_DESC";
//		System.out.println("门诊sql=="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 两个时间相差几天
	 * @param startTime
	 * @param endTime
	 * @param format
	 */
	public String dateDiff(String startTime, String endTime, String format) {
		// 按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long nh = 1000 * 60 * 60;// 一小时的毫秒数
		long nm = 1000 * 60;// 一分钟的毫秒数
		long ns = 1000;// 一秒钟的毫秒数
		long diff;
		String result="";
		try {
			// 获得两个时间的毫秒时间差异
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			long day = diff / nd;// 计算差多少天
			long hour = diff % nd / nh;// 计算差多少小时
			long min = diff % nd % nh / nm;// 计算差多少分钟
			long sec = diff % nd % nh % nm / ns;// 计算差多少秒
			// 输出结果
//			System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟"+ sec + "秒。");
			result = String.valueOf(day);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally{
			return result;
		}
	}
}
