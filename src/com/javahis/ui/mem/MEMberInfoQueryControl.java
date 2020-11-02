package com.javahis.ui.mem;
 
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 高级到期会员查询</p>
 *
 * <p>Description: 高级到期会员查询</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore</p>
 *
 * @author sunqy 20140425
 * @version 1.0
 */

public class MEMberInfoQueryControl extends TControl{
	
	private String startDateQuery;
	private String endDateQuery;
	
	public MEMberInfoQueryControl() {
		super();
	}
	/**
     * 初始化
     */
    public void onInit(){
    	//初始化查询时间
		 this.setValue("LOCAL_TIME",SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0,10)+ " 00:00:00");
		 this.setValue("CHECK_TIME",SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0,10)+ " 23:59:59");
		 
    }
    /**
     * 查询方法
     */
    public void onQuery(){
    	onEnter1();
    }
    /**
     * 输入结束日期后的回车事件
     */
	public void onEnter1() {
		
		startDateQuery = getValue("LOCAL_TIME").toString().trim().replace("-", "").substring(0, 8)+"000000";
		endDateQuery = getValue("CHECK_TIME").toString().trim().replace("-", "").substring(0, 8)+"235959";
		
//		String sql = "SELECT MEM_CODE, MEM_FEE, REASON, COUNT(MEM_CODE)" +
//					"FROM MEM_TRADE " +
//					"WHERE STATUS IN ('1') and end_date between " +
//					"TO_DATE('"+ startDateQuery
//					+"', 'YYYYMMDDHH24MISS') and " +
//					"TO_DATE('"+ endDateQuery 
//					+"', 'YYYYMMDDHH24MISS') " +
//					"GROUP BY MEM_CODE, MEM_FEE, REASON";
		String sql = "SELECT   A.MEM_CODE, B.VALID_DAYS, B.MEM_IN_REASON, B.MEM_FEE," +
				" COUNT (A.MEM_CODE) MEM_COUNT, B.MEM_FEE * COUNT (A.MEM_CODE) FEE_SUM" +
				" FROM MEM_TRADE A, MEM_MEMBERSHIP_INFO B" +
				" WHERE A.MEM_CODE = B.MEM_CODE" +
				" AND A.STATUS = '1'" +
				" AND A.END_DATE BETWEEN TO_DATE ('"+startDateQuery+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endDateQuery+"', 'YYYYMMDDHH24MISS')" +
				" GROUP BY A.MEM_CODE, B.VALID_DAYS, B.MEM_IN_REASON, B.MEM_FEE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result="+result);
		//判断会员卡类型：如果为1-普通卡；如果为2-熊猫卡；如果为3-龙卡；如果为空或者空字符串-无卡
//		for(int i = 0 ; i<result.getCount();i++){
//			String memCode = result.getValue(("MEM_CODE"),i);
//			if(memCode != null && memCode != ""){
//				if(memCode.equals("1")){
//					memCode = "普通卡";
//				}
//				if(memCode.equals("2")){
//					memCode = "熊猫卡";
//				}
//				if(memCode.equals("3")){
//					memCode = "龙卡";
//				}
//			}else{
//				memCode = "无卡";
//			}
//			result.setData("MEM_CODE", i, memCode);
//		}
		//判断办卡方式:如果为1-新客户;如果为2-普通卡入会;如果为3-龙卡升级熊猫;如果为空或者空字符串-没有办理会员卡
//		for(int i = 0;i<result.getCount();i++){
//			String reason = result.getValue(("REASON"),i);
//			if(reason != null && reason != ""){
//				if(reason.equals("1")){
//					reason = "新客户";
//				}
//				if(reason.equals("2")){
//					reason = "普通卡入会";
//				}
//				if(reason.equals("3")){
//					reason = "龙卡升级熊猫";
//				}
//			}
//			result.setData("REASON", i, reason);
//		}
		getTable("TABLE1").setParmValue(result);
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
	 * 点击查询结果中的一条在下面TABLE显示详细信息
	 */
	public void onCtzClick(){
//		System.out.println("-----------enter-------------");
		//得到页面TABLE1，TABLE2表格
		TTable table1 = getTable("TABLE1");
		TTable table2 = getTable("TABLE2");
		int i = table1.getSelectedRow();//所选的行数
		TParm parm = table1.getParmValue();
		//获取 所选行数的各段数据
		String memCode0 = parm.getValue("MEM_CODE", i);
//		String reason0 = parm.getValue("REASON", i);
//		String meeFee0 = parm.getValue("MEM_FEE", i);
		//获取输入的失效日期
//		String endDateQuery = getValue("CHECK_TIME").toString().trim().replace("-", "").substring(0, 8);

//		System.out.println("memCode=="+meeCode+";reason=="+reason+";meeFee=="+meeFee+";endDateQuery=="+endDateQuery);
//		String sql = "SELECT a.mr_no, b.pat_name, b.sex_code, b.birth_date, a.start_date," +
//				" a.end_date, a.mem_code,c.INTRODUCER1,c.INTRODUCER2,c.INTRODUCER3 " +
//				"FROM mem_patinfo a, sys_patinfo b, " +
//				"(SELECT mr_no,INTRODUCER1,INTRODUCER2，INTRODUCER3 " +
//				"FROM mem_trade WHERE mem_code = '"+ memCode0 +"' " +
//				"AND mem_fee = '"+ meeFee0 +"' " +
//				"AND reason = '"+ reason0 +"' AND status IN ('1') " +
//				"and end_date between TO_DATE('"+SystemTool.getInstance().getDate().toString().replace("-", "").trim().substring(0,8)+"', 'YYYYMMDD') " +
//				"and TO_DATE('"+ endDateQuery +"', 'YYYYMMDD')) c " +
//				"WHERE a.mr_no = b.mr_no AND a.mr_no = c.mr_no";
		String sql = "SELECT A.MR_NO, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, A.START_DATE," +
				" A.END_DATE, C.MEM_CODE, C.INTRODUCER1, C.INTRODUCER2, C.INTRODUCER3" +
				" FROM MEM_PATINFO A," +
				" SYS_PATINFO B," +
				" (SELECT MR_NO, INTRODUCER1, INTRODUCER2，INTRODUCER3, MEM_CODE" +
				" FROM MEM_TRADE" +
				" WHERE STATUS = '1'" +
				" AND MEM_CODE = '"+memCode0+"'" +
				" AND END_DATE BETWEEN TO_DATE ('"+startDateQuery+"', 'YYYYMMDDHH24MISS')" +
				"  AND TO_DATE ('"+endDateQuery+"', 'YYYYMMDDHH24MISS')) C" +
				" WHERE A.MR_NO = B.MR_NO AND A.MR_NO = C.MR_NO";
//		System.out.println("------执行sql语句------"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table2.setParmValue(result);
	}
	/**
	 * 打印报表
	 */
	public void onPrint(){
		//得到TABLE上的数据
		TParm tableParm = getTable("TABLE2").getParmValue();
		TParm tableParm1 = getTable("TABLE2").getShowParmValue();
		//判断有无打印数据
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		TParm result = new TParm();
		for(int i=0;i<tableParm.getCount();i++){
			//MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;START_DATE;END_DATE;MEM_CODE;INTRODUCER
			result.addData("MR_NO", tableParm.getValue("MR_NO", i));
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
			String startDate = tableParm.getValue("START_DATE",i);
			if(startDate == null || startDate.equals("")){
				startDate = "无";
			}else{
				startDate = startDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("START_DATE", startDate);
			String endDate = tableParm.getValue("END_DATE",i);
			if(endDate == null || endDate.equals("")){
				endDate = "无";
			}else{
				endDate = endDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("END_DATE", endDate); 
//			String memCode = tableParm.getValue("MEM_CODE", i);
//			if(memCode.equals("1")){
//				memCode = "普通卡";
//			}
//			if(memCode.equals("2")){
//				memCode = "熊猫卡";
//			}
//			if(memCode.equals("3")){
//				memCode = "龙卡";
//			}
			result.addData("MEM_CODE", tableParm1.getValue("MEM_CODE",i));
			//add by huangtt 20140528 start
			String introducer1 = tableParm1.getValue("INTRODUCER1", i);
			String introducer2 = tableParm1.getValue("INTRODUCER2", i);
			String introducer3 = tableParm1.getValue("INTRODUCER3", i);
			StringBuffer sb=new StringBuffer();
			if(introducer1 != null && !introducer1.equals("")){
				sb.append(introducer1+","); 
			}
			if(introducer2 != null && !introducer2.equals("")){
				sb.append(introducer2+",");
			}
			if(introducer3 != null && !introducer3.equals("")){
				sb.append(introducer3+",");
			}
			if(sb.toString().length()>0){
				sb.deleteCharAt(sb.length()-1);
			}
			//add by huangtt 20140528 end
			
			result.addData("INTRODUCER", sb);
//			System.out.println("result1=="+result);
		}
		result.setCount(tableParm.getCount("MR_NO")) ;//设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "MR_NO");//排序
		result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		result.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		result.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
		result.addData("SYSTEM", "COLUMNS", "START_DATE");
		result.addData("SYSTEM", "COLUMNS", "END_DATE");
		result.addData("SYSTEM", "COLUMNS", "MEM_CODE");
		result.addData("SYSTEM", "COLUMNS", "INTRODUCER");
//		System.out.println("result2=="+result);
		//将数据放入要打印的报表中   
		TParm print = new TParm();
		print.setData("TABLE", result.getData());
		print.setData("AUTHER", "TEXT", "制表人:"+Operator.getName());
		print.setData("CREATETIME", "TEXT","制表日期:"+SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0, 10));
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMberInfoQuery.jhw", print);
	}
	
	public void onClear(){
		TTable table1 = getTable("TABLE1");
		TTable table2 = getTable("TABLE2");
		table1.removeRowAll();
		table2.removeRowAll();
		this.setValue("LOCAL_TIME",SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0,10)+ " 00:00:00");
		 this.setValue("CHECK_TIME",SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0,10)+ " 23:59:59");
	}
}









