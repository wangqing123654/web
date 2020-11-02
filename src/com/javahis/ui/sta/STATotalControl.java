package com.javahis.ui.sta;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class STATotalControl extends TControl{
	private TRadioButton RADIO_YEAR;
	private TRadioButton RADIO_MONTH;
	private TTextFormat START_YEAR;
	private TTextFormat END_YEAR;
	private TTextFormat START_MONTH;
	private TTextFormat END_MONTH;
	private TComboBox DEPT_CODE;
	private TTable TABLE;
	
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
	
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
		DEPT_CODE = (TComboBox) this.getComponent("DEPT_CODE");
		TABLE = (TTable) this.getComponent("TABLE");
		
		String year = SystemTool.getInstance().getDate().toString().substring(0,4);
		START_YEAR.setText(year);
		END_YEAR.setText(year);
		
		START_MONTH.setEnabled(false);
		END_MONTH.setEnabled(false);
		
		addListener(TABLE);
		
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
		this.clearValue("DEPT_CODE");
	}
	
	/**
     * 汇出Excel
     */
    public void onExcel() {
        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "门急诊及住院病人人均费用同期对比表");
    }
	
	
	public void onQuery(){
		TParm parm = getParm();
		TTable t = (TTable) this.getComponent("TABLE");
		t.setParmValue(parm);
	}
	
	public TParm getParm(){
		String startTime = "";
		String endTime = "";
		Calendar c = Calendar.getInstance();
		DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String type = "";
		
		if(RADIO_YEAR.isSelected()){
			startTime = START_YEAR.getValue().toString().replace("-", "/").substring(0, 19);

			Timestamp t = (Timestamp)END_YEAR.getValue();  
			c.setTime(t);
			c.add(Calendar.YEAR, 1);
			endTime = f.format(c.getTime()).replace("-", "/")+" 00:00:00";
			
			type = "YEAR";
		}else if(RADIO_MONTH.isSelected()){
			startTime = START_MONTH.getValue().toString().replace("-", "/").substring(0, 19);
			
			Timestamp t = (Timestamp)END_MONTH.getValue();  
			c.setTime(t);
			c.add(Calendar.MONTH, 1);
			endTime = f.format(c.getTime()).replace("-", "/")+" 00:00:00";
			
			type = "MONTH";
		}
		
		String deptCode = DEPT_CODE.getValue();
		
		TParm parmPara = new TParm();
		parmPara.setData("DEPT_CODE", deptCode);
		parmPara.setData("START_TIME", startTime);
		parmPara.setData("END_TIME", endTime);
		parmPara.setData("TYPE", type);
		
		
		
		
		TParm parm = new TParm();
		TParm oParm = new TParm(TJDODBTool.getInstance().select(getOSql(parmPara)));
		TParm eParm = new TParm(TJDODBTool.getInstance().select(getESql(parmPara)));
		TParm iParm = new TParm(TJDODBTool.getInstance().select(getISql(parmPara)));
		HashSet<List<String>> key = new HashSet<List<String>>();
		DecimalFormat df=new DecimalFormat("#.00");
		
		//获取科室和病区
		for(int i = 0 ; i < oParm.getCount() ; i++){
			List<String> oSet = new ArrayList<String>();
			oSet.add(oParm.getValue("DAY", i));
			oSet.add(oParm.getValue("DEPT_CODE", i));
			key.add(oSet);
		}
		
		for(int i = 0 ; i < iParm.getCount() ; i++){
			List<String> iSet = new ArrayList<String>();
			iSet.add(iParm.getValue("DAY", i));
			iSet.add(iParm.getValue("DEPT_CODE", i));
			key.add(iSet);
		}
		
		for(int i = 0 ; i < eParm.getCount() ; i++){
			List<String> eSet = new ArrayList<String>();
			eSet.add(eParm.getValue("DAY", i));
			eSet.add(eParm.getValue("DEPT_CODE", i));
			key.add(eSet);
		}
		
		//根据日期、科室和病区组装数据
		parm.setCount(key.size());
		Iterator<List<String>> iterator=key.iterator();
		int count = 0;
		while(iterator.hasNext()){
			List<String> lst = (List<String>) iterator.next();
			String date = lst.get(0);
			String dept = lst.get(1);
			
			TParm p = new TParm();
			
			if("YEAR".equals(type)){
				p.addData("DAY", date+"年");
			}else if("MONTH".equals(type)){
				p.addData("DAY", date.replace("/", "年")+"月");
			}else{
				p.addData("DAY", "");
			}
			
			p.addData("DEPT_CODE", dept);
			
			for(int i = 0 ; i <= oParm.getCount() ; i++){
				if(date.equals(oParm.getValue("DAY", i))&&dept.equals(oParm.getValue("DEPT_CODE", i))){
					p.addData("O_AR_AMT", oParm.getValue("AR_AMT", i));
					p.addData("O_TOT_COUNT",oParm.getValue("TOT_COUNT", i));
					double d = Double.parseDouble(oParm.getValue("PER", i));
					p.addData("O_PER",df.format(d));
					break;
				}
				if(i == oParm.getCount()){
					p.addData("O_AR_AMT", "");
					p.addData("O_TOT_COUNT","");
					p.addData("O_PER","");
				}
			}
			
			for(int i = 0 ; i <= eParm.getCount() ; i++){
				if(date.equals(eParm.getValue("DAY", i))&&dept.equals(eParm.getValue("DEPT_CODE", i))){
					p.addData("E_AR_AMT", eParm.getValue("AR_AMT", i));
					p.addData("E_TOT_COUNT",eParm.getValue("TOT_COUNT", i));
					double d = Double.parseDouble(eParm.getValue("PER", i));
					p.addData("E_PER",df.format(d));
					break;
				}
				if(i == eParm.getCount()){
					p.addData("E_AR_AMT", "");
					p.addData("E_TOT_COUNT","");
					p.addData("E_PER","");
				}
			}
			
			for(int i = 0 ; i <= iParm.getCount() ; i++){
				if(date.equals(iParm.getValue("DAY", i))&&dept.equals(iParm.getValue("DEPT_CODE", i))){
					p.addData("I_AR_AMT", iParm.getValue("AR_AMT", i));
					p.addData("I_TOT_COUNT",iParm.getValue("TOT_COUNT", i));
					double d = Double.parseDouble(iParm.getValue("PER", i));
					p.addData("I_PER",df.format(d));
					break;
				}
				if(i == iParm.getCount()){
					p.addData("I_AR_AMT", "");
					p.addData("I_TOT_COUNT","");
					p.addData("I_PER","");
				}
			}
		
			p.setCount(p.getCount("DEPT_CODE"));
			parm.addRowData(p,0);
			count++;	
		}
		
		return parm;
	};

	public String getOSql(TParm parm){
		String deptCode = parm.getValue("DEPT_CODE");
		String startTime = parm.getValue("START_TIME");
		String endTime = parm.getValue("END_TIME");
		String type = parm.getValue("TYPE");
		
		if("".equals(startTime)){
			startTime = "0000/00/00 00:00:00";
		}
		if("".equals(endTime)){
			endTime = "9999/12/31 23:59:59";
		}
		
		String sql = "";
		
		sql += " SELECT " ;
		
		if("YEAR".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy') AS DAY, ";
		}else if("MONTH".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy/MM') AS DAY, ";
		}else{
			sql += " '' AS DAY";
		}
	    	sql +=  " SUM(AR_AMT) AR_AMT, " + 
	    	   		" COUNT(DISTINCT B.CASE_NO) TOT_COUNT, " +
	    	   		" (SUM(AR_AMT)/COUNT(DISTINCT B.CASE_NO)) AS PER, " +
	    	   		" B.DEPT_CODE AS DEPT_CODE " +
	    	   	" FROM " +
	    	   		" BIL_OPB_RECP A, " +
	    	   		" REG_PATADM B " +
	    	   	" WHERE " + 
	    	   		" A.CASE_NO = B.CASE_NO " +
	    	   		" AND B.REGCAN_USER IS NULL " +
	    	   		" AND B.DEPT_CODE IS NOT NULL " +
	    	   		" AND A.ADM_TYPE = 'O' " +
	    	   		" AND B.ADM_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:MI:SS')";
	    if(!"".equals(deptCode)){
	    	sql += " AND B.DEPT_CODE = '"+deptCode+"'";
	    }
		
		sql +=" GROUP BY " ;
		if("YEAR".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy') , ";
		}else if("MONTH".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy/MM') , ";
		}
	    sql +=  " B.DEPT_CODE ";
	    
		return sql;
	}
	
	public String getESql(TParm parm){
		String deptCode = parm.getValue("DEPT_CODE");
		String startTime = parm.getValue("START_TIME");
		String endTime = parm.getValue("END_TIME");
		String type = parm.getValue("TYPE");
		
		if("".equals(startTime)){
			startTime = "0000/00/00 00:00:00";
		}
		if("".equals(endTime)){
			endTime = "9999/12/31 23:59:59";
		}
		
		String sql = "";
		
		sql += " SELECT " ;
		
		if("YEAR".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy') AS DAY, ";
		}else if("MONTH".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy/MM') AS DAY, ";
		}else{
			sql += " '' AS DAY";
		}
		
			sql +=	" SUM(AR_AMT) AR_AMT, " + 
					" COUNT(DISTINCT B.CASE_NO) TOT_COUNT, " +
					" (SUM(AR_AMT)/COUNT(DISTINCT B.CASE_NO)) AS PER, " +
					" B.DEPT_CODE AS DEPT_CODE " +
	    	   	" FROM " +
	    	   		" BIL_OPB_RECP A, " +
	    	   		" REG_PATADM B " +
	    	   	" WHERE " + 
	    	   		" A.CASE_NO = B.CASE_NO " +
	    	   		" AND B.REGCAN_USER IS NULL " +
	    	   		" AND B.DEPT_CODE IS NOT NULL " +
	    	   		" AND A.ADM_TYPE = 'E' " +
	    	   		" AND B.ADM_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:MI:SS')";
	    if(!"".equals(deptCode)){
	    	sql += " AND B.DEPT_CODE = '"+deptCode+"'";
	    }
	    sql += " GROUP BY " ;
	    
	    if("YEAR".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy'), ";
		}else if("MONTH".equals(type)){
			sql += " TO_CHAR(B.ADM_DATE,'yyyy/MM'), ";
		}
	    
	  	sql += "B.DEPT_CODE ";
	  	
		return sql;
	}
	
	public String getISql(TParm parm){
		String deptCode = parm.getValue("DEPT_CODE");
		String startTime = parm.getValue("START_TIME");
		String endTime = parm.getValue("END_TIME");
		String type = parm.getValue("TYPE");
		
		if("".equals(startTime)){
			startTime = "0000/00/00 00:00:00";
		}
		if("".equals(endTime)){
			endTime = "9999/12/31 23:59:59";
		}
		
		String sql = "";
		
		sql += " SELECT " ;  
	    
		if("YEAR".equals(type)){
			sql += " TO_CHAR(B.DS_DATE,'yyyy') AS DAY, ";
		}else if("MONTH".equals(type)){
			sql += " TO_CHAR(B.DS_DATE,'yyyy/MM') AS DAY, ";
		}else{
			sql += " '' AS DAY";
		}
		
		sql += " COUNT(DISTINCT B.CASE_NO) AS TOT_COUNT, " + 
	    			" SUM(A.TOT_AMT) AS AR_AMT, " +
	    			" (SUM(A.TOT_AMT)/COUNT(DISTINCT B.CASE_NO)) AS PER, "+
	    			" B.DEPT_CODE AS DEPT_CODE " +
	    		" FROM " +
	    			" IBS_ORDD A, " +
	    			" ADM_INP B " +
	    		" WHERE " + 
	    			" A.CASE_NO = B.CASE_NO " +
	    			" AND B.DS_DATE IS NOT NULL " +
	    			" AND B.CANCEL_FLG = 'N' " +
	    			" AND B.DS_DATE BETWEEN TO_DATE('"+startTime+"','yyyy/MM/dd HH24:MI:SS') AND TO_DATE('"+endTime+"','yyyy/MM/dd HH24:MI:SS')";
	    if(!"".equals(deptCode)){
	    	sql += " AND B.DEPT_CODE = '"+deptCode+"'";
	    }
	    sql += " GROUP BY " ;
	    
	    if("YEAR".equals(type)){
			sql += " TO_CHAR(B.DS_DATE,'yyyy') , ";
		}else if("MONTH".equals(type)){
			sql += " TO_CHAR(B.DS_DATE,'yyyy/MM') , ";
		}
	    
	    sql += 	" B.DEPT_CODE" ;
	    
	    return sql;
	}
	
	
	
	
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				 //System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		((TTable)this.getComponent("TABLE")).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
}
