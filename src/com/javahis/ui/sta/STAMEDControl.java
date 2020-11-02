package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jdo.sys.SystemTool;
import jdo.sys.SYSRuleTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 医技科室工作量汇总
 * </p>
 * 
 * <p>
 * Description: 医疗科室工作量汇总
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
 * @author yangjj 2015-05-18
 * @version 1.0
 */

public class STAMEDControl extends TControl{
	private TRadioButton RADIO_YEAR;
	private TRadioButton RADIO_MONTH;
	private TTextFormat START_YEAR;
	private TTextFormat END_YEAR;
	private TTextFormat START_MONTH;
	private TTextFormat END_MONTH;
	private TComboBox TYPE;
	private TTable TABLE;
	
	
	/*
	 * 初始化页面
	 * */
	public void onInit(){
		super.init();
		
		RADIO_YEAR = (TRadioButton) this.getComponent("RADIO_YEAR");
		RADIO_MONTH = (TRadioButton) this.getComponent("RADIO_MONTH");
		START_YEAR = (TTextFormat) this.getComponent("START_YEAR");
		END_YEAR = (TTextFormat) this.getComponent("END_YEAR");
		START_MONTH = (TTextFormat) this.getComponent("START_MONTH");
		END_MONTH = (TTextFormat) this.getComponent("END_MONTH");
		TYPE = (TComboBox) this.getComponent("TYPE");
		TABLE = (TTable) this.getComponent("TABLE");
		
		String year = SystemTool.getInstance().getDate().toString().substring(0,4);
		START_YEAR.setText(year);
		END_YEAR.setText(year);
		
		START_MONTH.setEnabled(false);
		END_MONTH.setEnabled(false);
		
		String stringData = "[[id,name],[,]";
		TParm typeParm = new TParm(TJDODBTool.getInstance().select(getTypeSql()));
		for(int i = 0 ; i < typeParm.getCount() ; i++){
			stringData += ",["+typeParm.getValue("CATEGORY_CODE", i)+","+typeParm.getValue("CATEGORY_CHN_DESC", i)+"]";
		}
		stringData += "]";
		TYPE.setStringData(stringData);
		
	}
	
	/*
	 * 年选择事件
	 * */
	public void onYearSelected(){
		String year = SystemTool.getInstance().getDate().toString().substring(0,4);
		
		START_YEAR.setEnabled(true);
		START_YEAR.setText(year);
		END_YEAR.setEnabled(true);
		END_YEAR.setText(year);
		
		
		START_MONTH.setEnabled(false);
		START_MONTH.setText("");
		END_MONTH.setEnabled(false);
		END_MONTH.setText("");
	}
	
	/*
	 * 月选择事件
	 * */
	public void onMonthSelected(){
		String month = SystemTool.getInstance().getDate().toString().substring(0,7).replace("-", "/");
		
		START_YEAR.setEnabled(false);
		START_YEAR.setText("");
		END_YEAR.setEnabled(false);
		END_YEAR.setText("");
		
		
		START_MONTH.setEnabled(true);
		START_MONTH.setText(month);
		END_MONTH.setEnabled(true);
		END_MONTH.setText(month);
	}
	
	
	/*
	 * 清空操作
	 * */
	public void onClear(){
		onYearSelected();
		this.clearValue("TYPE");
	}
	
	/*
	 * 查询
	 * */
	public void onQuery(){
		if("".equals(getValueString("TYPE"))){
			this.messageBox("请选择类型！");
			return;
		}
		
		String startTime = "";
		String endTime = "";
		Calendar c = Calendar.getInstance();
		DateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd");
		
		if(RADIO_YEAR.isSelected()){
			startTime = START_YEAR.getValue().toString().replace("-", "/").substring(0, 19);

			Timestamp t = (Timestamp)END_YEAR.getValue();  
			c.setTime(t);
			c.add(Calendar.YEAR, 1);
			endTime = f.format(c.getTime()).replace("-", "/")+" 00:00:00";
		}else if(RADIO_MONTH.isSelected()){
			startTime = START_MONTH.getValue().toString().replace("-", "/").substring(0, 19);
			
			Timestamp t = (Timestamp)END_MONTH.getValue();  
			c.setTime(t);
			c.add(Calendar.MONTH, 1);
			endTime = f.format(c.getTime()).replace("-", "/")+" 00:00:00";
		}
		
		String type = TYPE.getValue();
		
		TParm parm = new TParm();
		parm.setData("TYPE", type);
		parm.setData("START_TIME", startTime);
		parm.setData("END_TIME", endTime);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(getQuerySql(parm)));

		if(result.getErrCode() < 0){
			this.messageBox(result.getErrText());
			return;
		}
		if(result.getCount() <= 0){
			this.messageBox("查无数据！");
			TABLE.setParmValue(result);
			return;
		}
		createTable(result);
	}
	
	/*
	 * 生成查询表格
	 * */
	public void createTable(TParm parm){
		TTable table = (TTable) this.getComponent("TABLE");
		
		TParm cateParm = new TParm(TJDODBTool.getInstance().select(getCategorySql(TYPE.getValue())));
		
		String strHeader = "科室,200,DEPT_CODE";
		String strParm = "DEPT_CODE";
		String strCol = "0,left";
		
		for(int i = 0 ; i < cateParm.getCount() ; i++){
			strHeader += ";"+cateParm.getValue("CATEGORY_CHN_DESC", i)+",80,"+cateParm.getValue("CATEGORY_CODE", i);
			strParm += ";"+cateParm.getValue("CATEGORY_CODE", i);
			strCol += ";"+(i+1)+",right";
		}
		
		table.setParmValue(null);
		table.setHeader(strHeader);
		table.setParmMap(strParm);
		table.setColumnHorizontalAlignmentData(strCol);
		
		TParm p = getParm(parm);
		table.setParmValue(p);
	}
	
	public TParm getParm(TParm parm){
		TParm tableParm = new TParm();
		
		Set<String> set = new HashSet<String>();
		for(int i = 0 ; i < parm.getCount() ; i++){
			set.add(parm.getValue("DEPT_CODE",i));
		}
		
		TParm cateParm = new TParm(TJDODBTool.getInstance().select(getCategorySql(TYPE.getValue())));
		
		Iterator<String> iterator=set.iterator();
		while(iterator.hasNext()){
			String deptCode = iterator.next();
			TParm p = new TParm();
			p.addData("DEPT_CODE", deptCode);
			for(int i = 0 ; i < cateParm.getCount() ; i++){
				p.addData(cateParm.getValue("CATEGORY_CODE", i), "");
			}
			
			for(int i = 0 ; i < parm.getCount() ; i++){
				if(deptCode.equals(parm.getValue("DEPT_CODE", i))){
					p.setData(parm.getValue("CATEGORY_CODE", i),0, parm.getValue("TOT_COUNT", i));
				}
			}
			p.setCount(cateParm.getCount()+1);
			tableParm.addRowData(p, 0);
		}
		
		tableParm.setCount(set.size());
		
		return tableParm;
	}
	
	/*
	 * 查询生成SQL
	 * */
	public String getQuerySql(TParm parm){
		String type = parm.getValue("TYPE");
		String startTime = parm.getValue("START_TIME");
		String endTime = parm.getValue("END_TIME");
		if("".equals(startTime)){
			startTime = "0000/00/00 00:00:00";
		}
		if("".equals(endTime)){
			endTime = "9999/12/31 23:59:59";
		}
		
		SYSRuleTool ruleTool = new SYSRuleTool("EXM_RULE");
        int length = ruleTool.getClassNumber(ruleTool.getNumberClass(type) + 1);

		
		String sql = "";
		
		sql += " SELECT " +
			   		" G.DEPT_CODE, " +
			   		" SUM(G.TOT_COUNT) TOT_COUNT, " +
			   		" I.CATEGORY_CHN_DESC, " + 
			   		" I.CATEGORY_CODE " +
			   	" FROM " +
			   		" (SELECT " +
			   			" A.EXEC_DEPT_CODE AS DEPT_CODE, " +
			   			" COUNT(*) AS TOT_COUNT, " +
			   			" C.CATEGORY_CHN_DESC AS CATEGORY_CHN_DESC, " +
			   			" C.CATEGORY_CODE AS CATEGORY_CODE " +
			   		" FROM " +
			   			" OPD_ORDER A, " +
			   			" SYS_FEE B, " +
			   			" SYS_CATEGORY C " +
			   		" WHERE " +
			   			" A.ORDER_CODE = B.ORDER_CODE " +
			   			" AND B.RPTTYPE_CODE = C.CATEGORY_CODE " +
			   			" AND C.RULE_TYPE = 'EXM_RULE' " +
			   			" AND A.CAT1_TYPE IN ('LIS','RIS') " +
			   			" AND A.SETMAIN_FLG = 'Y' " +
			   			" AND A.EXEC_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:MI:SS')" +
			   		" GROUP BY" +
			   			" C.CATEGORY_CODE, " +
			   			" C.CATEGORY_CHN_DESC, " +
			   			" A.EXEC_DEPT_CODE " +
			   		" UNION ALL " +
			   		" SELECT " +
			   			" D.EXE_DEPT_CODE AS DEPT_CODE, " +
			   			" COUNT(*) AS TOT_COUNT, " +
			   			" F.CATEGORY_CHN_DESC AS CATEGORY_CHN_DESC, " +
			   			" F.CATEGORY_CODE " +
			   		" FROM " + 
			   		    " IBS_ORDD D , " + 
			   		    " SYS_FEE E, " + 
			   		    " SYS_CATEGORY F " +
			   		" WHERE " +
			   			" D.ORDER_CODE = E.ORDER_CODE " +
			   			" AND E.RPTTYPE_CODE = F.CATEGORY_CODE " +
			   			" AND F.RULE_TYPE = 'EXM_RULE' " +
			   			" AND D.CAT1_TYPE IN ('LIS','RIS') " +
			   			" AND D.INDV_FLG = 'N' " +
			   			" AND D.EXEC_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:MI:SS')" +
			   		" GROUP BY " +
			   			" F.CATEGORY_CODE, " +
			   			" F.CATEGORY_CHN_DESC," +
			   			" D.EXE_DEPT_CODE) G, " +
			   		" SYS_DEPT H,(SELECT CATEGORY_CODE,CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE RULE_TYPE = 'EXM_RULE' AND length(CATEGORY_CODE)=" + length +" AND CATEGORY_CODE like '" + type + "%' ) I"+ 
			   	" WHERE " +
			   		" G.DEPT_CODE = H.DEPT_CODE " +
			   		" AND H.CLASSIFY = '1' " +
			   		" AND G.CATEGORY_CODE(+) = I.CATEGORY_CODE";
		
		
		sql += " GROUP BY G.DEPT_CODE,I.CATEGORY_CHN_DESC,I.CATEGORY_CODE " +
			   " ORDER BY G.DEPT_CODE ";
		return sql;
	}
	
	/*
	 * 获取检验、检查分类SQL
	 * */
	public String getTypeSql(){
		String sql = " SELECT " +
						" CATEGORY_CODE," +
						" CATEGORY_CHN_DESC " +
					 " FROM " +
					 	" SYS_CATEGORY B," +
					 	" SYS_RULE A "+
					 " WHERE " +
					 	" A.RULE_TYPE=B.RULE_TYPE "+
					 	" AND A.RULE_TYPE='EXM_RULE' "+
					 	" AND length(B.CATEGORY_CODE)=(select A.CLASSIFY1 FROM SYS_RULE A where A.RULE_TYPE='EXM_RULE') ";
		return sql;
	}
	
	/*
	 * 查询项目SQL
	 * */
	public String getCategorySql(String t){
		String type = t;
		SYSRuleTool ruleTool = new SYSRuleTool("EXM_RULE");
        int length = ruleTool.getClassNumber(ruleTool.getNumberClass(type) + 1);
		String sql = "SELECT " +
						" CATEGORY_CODE, " +
						" CATEGORY_CHN_DESC "+
            		 " FROM " +
            		 	" SYS_CATEGORY "+
            		 " WHERE " +
            		 	" RULE_TYPE = 'EXM_RULE'"+
            		 	" AND LENGTH (CATEGORY_CODE) = '"+length+"'"+
            		 	" AND CATEGORY_CODE LIKE '"+type+"%'";
        return sql ;
	}
	
	/**
     * 汇出Excel
     */
    public void onExcel() {
        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "医技科室工作量统计");
    }
}
