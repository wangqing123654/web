package com.javahis.ui.mem;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 到期会员查询</p>
 *
 * <p>Description: 到期会员查询</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore</p>
 *
 * @author sunqy 20140424
 * @version 1.0
 */


public class MEMbershipCardCheckControl extends TControl {
	
	private TTable table;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	
	public MEMbershipCardCheckControl(){
		super();
	}
	/**
	 * 初始化 
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		String time = SystemTool.getInstance().getDate().toString().trim().substring(0, 10).replace("-", "/") + " 00:00:00";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, +2);
		String time2 = sdf.format(calendar.getTime()) + " 23:59:59";
//		System.out.println(time);
		this.setValue("localTime", time);
//		System.out.println(time2);
		this.setValue("checkTime", time2);
	}
	/**
	 * 得到页面中Table对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
	/**
	 * 按照失效日期查询
	 */
	public void onQuery(){
		onEnter1();
	}
	/**
	 * 查询日期回车事件   
	 */
	public void onEnter1() {
//		System.out.println("---------onEnter方法--------");
		//得到在查询时间内输入的时间
		String endDateQuery = getValue("checkTime").toString().trim().replace("-", "").substring(0, 8)+"235959";
		String startDateQuery = getValue("localTime").toString().trim().replace("-", "").substring(0, 8)+"000000";
//		System.out.println(endDateQuery);
		//判断输入是否为空字符串
//		if(endDateQuery.equals("") || endDateQuery == null){
//			this.messageBox("请输入查询时间!");
//			this.onClear();
//			return;
//		}
		//向数据库插入信息，得到当前时间到输入时间之间的人员信息
		String sql = "select B.START_DATE,B.END_DATE, A.MR_NO,A.PAT_NAME,"
				+ "A.SEX_CODE,A.BIRTH_DATE,A.TEL_HOME,A.CELL_PHONE,B.GUARDIAN1_PHONE "
				+ "FROM SYS_PATINFO A, MEM_PATINFO B "
				+ "WHERE A.MR_NO=B.MR_NO AND " + "B.END_DATE BETWEEN TO_DATE('"
//				+ SystemTool.getInstance().getDate().toString().replace("-", "").trim().substring(0,8)
				+ startDateQuery
				+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endDateQuery
				+ "', 'YYYYMMDDHH24MISS')";
//		System.out.println(SystemTool.getInstance().getDate().toString().replace("-", "").trim().substring(0,8));
		System.out.println("sql=="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result=="+result);
//		for(int i = 0;i<result.getCount();i++){
//			String sexCode = result.getValue(("SEX_CODE"),i);
//			if(sexCode.equals("1")){
//				sexCode = "男";
//			}
//			if(sexCode.equals("2")){
//				sexCode = "女";
//			}
//			if(sexCode.equals("9")){
//				sexCode = "未定义";
//			}
//			if(sexCode.equals("0")){
//				sexCode = "未知";
//			}
//			result.setData("SEX_CODE", i, sexCode);
//		}
		getTable("TABLE").setParmValue(result);
	}
	/**
	 * 清空方法
	 */
	public void onClear() {
//		callFunction("UI|checkTime|setEnabled", true);//输入时间框可用
//		this.clearValue("checkTime");//将输入框置空
		table.removeRowAll();
		this.onInit();
	}
	/**
	 * 打印报表
	 */
	public void onPrint(){
		//得到TABLE上的数据
		TParm tableParm = getTable("TABLE").getParmValue();
		//判断有无打印数据
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		TParm result = new TParm();
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("MR_NO", tableParm.getValue("MR_NO", i)); //赋值 
			result.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
//			String sexCode = tableParm.getValue("SEX_CODE",i);
//			if(sexCode.equals("1")){
//				sexCode = "男";
//			}
//			if(sexCode.equals("2")){
//				sexCode = "女";
//			}
//			if(sexCode.equals("9")){
//				sexCode = "未说明";
//			}
//			if(sexCode.equals("0")){
//				sexCode = "未知";
//			}
			result.addData("SEX_CODE", DictionaryTool.getInstance().getSexName(tableParm.getValue("SEX_CODE",i))); 
			String birthDate = tableParm.getValue("BIRTH_DATE",i);
			if(birthDate == null || birthDate.equals("")){
				birthDate = "无";
			}else{
				birthDate = birthDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("BIRTH_DATE", birthDate);
			result.addData("TEL_HOME", tableParm.getValue("TEL_HOME", i));
			result.addData("CELL_PHONE", tableParm.getValue("CELL_PHONE", i));
			String endDate = tableParm.getValue("END_DATE",i);
			if(endDate == null || endDate.equals("")){
				endDate = "无";
			}else{
				endDate = endDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("END_DATE", endDate); 
			String startDate = tableParm.getValue("START_DATE",i);
			if(startDate == null || startDate.equals("")){
				startDate = "无";
			}else{
				startDate = startDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("START_DATE", startDate);
			result.addData("GUARDIAN1_PHONE", tableParm.getValue("GUARDIAN1_PHONE", i));
		}
		result.setCount(tableParm.getCount("MR_NO")) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "MR_NO");//排序
		result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		result.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		result.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
//		result.addData("SYSTEM", "COLUMNS", "TEL_HOME");
//		result.addData("SYSTEM", "COLUMNS", "CELL_PHONE");
		result.addData("SYSTEM", "COLUMNS", "START_DATE");
		result.addData("SYSTEM", "COLUMNS", "END_DATE");
//		result.addData("SYSTEM", "COLUMNS", "GUARDIAN1_PHONE");
//		System.out.println("result=="+result);
		//将数据放入要打印的报表中
		TParm print = new TParm();
		print.setData("TABLE1", result.getData());
		print.setData("AUTHER", "TEXT", "制表人:"+Operator.getName());
		print.setData("CREATETIME", "TEXT","制表日期:"+SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0, 10));
//		System.out.println("print"+print);
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMbershipCardManage.jhw", print);
	}
}
