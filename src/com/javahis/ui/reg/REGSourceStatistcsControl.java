package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
*
* <p>Title: 客户获取方式统计报表</p>
*
* <p>Description: 客户获取方式统计报表</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151211
* @version 4.0
*/
public class REGSourceStatistcsControl extends TControl {
	
	TTable table;
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		initTable();
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		
	}
	
	public void initTable(){
		String sql = "SELECT ID,CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEM_SOURCE' ORDER BY SEQ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String header = "就诊科室,150";
		String parmMap= "DEPT_CHN_DESC";
		String column ="0,left;";
		for (int i = 0; i < parm.getCount(); i++) {
			header = header + ";" + parm.getValue("CHN_DESC", i)+",80";
			parmMap = parmMap + ";" + parm.getValue("ID", i);
			column = column +(i+1)+",right;";
			
		}
		column=column.substring(0,column.length()-1);
		table.setHeader(header);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);
		
	}
	
	public void onQuery(){
		
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
		
		String startTime = getValueString("S_DATE");
		startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
		String endTime = getValueString("E_DATE");
		endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
		
		String sql = "SELECT B.DEPT_CHN_DESC, C.SOURCE, COUNT (A.MR_NO) COUNT " +
				" FROM REG_PATADM A, SYS_DEPT B, MEM_PATINFO C" +
				" WHERE A.SEE_DR_FLG <> 'N' AND A.VISIT_CODE = '0'" +
				" AND A.REALDEPT_CODE = B.DEPT_CODE" +
				" AND A.MR_NO = C.MR_NO" +
				" AND C.SOURCE = '#'" +
				" AND A.REG_DATE BETWEEN TO_DATE ('"+startTime+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"', 'YYYYMMDDHH24MISS')" +
				" GROUP BY B.DEPT_CHN_DESC, C.SOURCE";

		String sqlS="";
		String names[] = table.getParmMap().split(";");
		for (int i = 1; i < names.length; i++) {
			String sql1 = sql.replace("#", names[i]);
			if(i == 1){
				sqlS = sqlS + sql1;
			}else{
				sqlS = sqlS + " UNION ALL "+sql1;
			}
		}
//		System.out.println(sqlS);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlS));
	    
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		List<String> dateList = new ArrayList<String>();
		for (int i = 0; i < parm.getCount(); i++) {
			if(!dateList.contains(parm.getValue("DEPT_CHN_DESC", i))){
				dateList.add(parm.getValue("DEPT_CHN_DESC", i));
			}
		}
		Collections.sort(dateList);
		
		TParm tableParm = new TParm();
		for (int i = 0; i < dateList.size(); i++) {
			tableParm.addData("DEPT_CHN_DESC", dateList.get(i));
			for (int j = 1; j < names.length; j++) {
				int count = 0;
				
				for (int k = 0; k < parm.getCount(); k++) {
					if(dateList.get(i).equals(parm.getValue("DEPT_CHN_DESC", k)) && 
							names[j].equals(parm.getValue("SOURCE", k))){
						count = parm.getInt("COUNT", k);
						break;
					}
				}
				
				tableParm.addData(names[j], count );
				
			}
		}
		
		int [] sumCount = new int[names.length];
		for (int i = 1; i < names.length; i++) {
			for (int j = 0; j < tableParm.getCount("DEPT_CHN_DESC"); j++) {
				sumCount[i] += tableParm.getInt(names[i], j);
			}
		}
		
		tableParm.addData("DEPT_CHN_DESC", "合计");
		for (int i = 1; i < names.length; i++) {
			tableParm.addData(names[i], sumCount[i]);
		}
//		System.out.println(tableParm);
		
		table.setParmValue(tableParm);
		
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: REGSourceStatistcsControl.class ，method ：onQuery");
				e.printStackTrace();
			}
		}
		
	
	}
	
	public void onClear(){
		table.removeRowAll();
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
	}
	
	public void onExport(){
		table.acceptText();
		ExportExcelUtil.getInstance().exportExcel(table, "客户获取方式统计表");
	}

}
