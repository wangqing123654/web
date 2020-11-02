package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
*
* <p>Title: 销售套餐汇总表</p>
*
* <p>Description: 销售套餐汇总表</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151118
* @version 4.0
*/
public class MEMPackageSalesSummaryControl extends TControl{
	
	TTable table;
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("E_DATE",today.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", today.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
	}
	
	public void onQuery(){
		 boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			
		
		String sDate = this.getValueString("S_DATE").replace("-", "").replace("/", "").replace(":","").replace(" ", "").substring(0,14 );
		String eDate = this.getValueString("E_DATE").replace("-", "").replace("/", "").replace(":","").replace(" ", "").substring(0,14 );
		String sql = "  SELECT A.*  FROM (  SELECT A.PACKAGE_CODE, A.PACKAGE_DESC, A.BILL_DATE,A.STATUS," +
				" COUNT (A.MR_NO) COUNT, SUM (A.AR_AMT) AR_AMT FROM (SELECT DISTINCT B.PACKAGE_CODE," +
				" C.PACKAGE_DESC, A.MR_NO, TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD HH24:MI:SS') BILL_DATE, A.AR_AMT," +
				" CASE WHEN A.REST_FLAG = 'Y' THEN '退费' ELSE '购买' END STATUS" +
				" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C" +
				" WHERE A.TRADE_NO = B.TRADE_NO AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
				" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')) A" +
				" GROUP BY A.PACKAGE_CODE, A.PACKAGE_DESC, A.BILL_DATE,A.STATUS" +
				" UNION ALL" +
				" SELECT A.PACKAGE_CODE, A.PACKAGE_DESC, A.BILL_DATE,A.STATUS, COUNT (A.MR_NO) COUNT," +
				" SUM (A.AR_AMT) AR_AMT FROM (SELECT DISTINCT B.PACKAGE_CODE, C.PACKAGE_DESC," +
				" A.MR_NO, TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD HH24:MI:SS') BILL_DATE,  A.AR_AMT," +
				" CASE WHEN A.REST_FLAG = 'Y' THEN '退费' ELSE '购买' END STATUS" +
				" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C" +
				" WHERE A.TRADE_NO = B.REST_TRADE_NO AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
				" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')) A" +
				" GROUP BY A.PACKAGE_CODE, A.PACKAGE_DESC, A.BILL_DATE,A.STATUS) A" +
				" ORDER BY A.BILL_DATE, A.PACKAGE_CODE";
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		List<String> packageCode = new ArrayList<String>();
		List<String> date = new ArrayList<String>();
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("BILL_DATE",i ,parm.getValue("BILL_DATE", i).substring(0, 7));
			if(!packageCode.contains(parm.getValue("PACKAGE_DESC", i))){
				packageCode.add(parm.getValue("PACKAGE_DESC", i));
			}
			if(!date.contains(parm.getValue("BILL_DATE", i))){
				date.add(parm.getValue("BILL_DATE", i));
			}
		}
		
		//初始化表格
		String heard = " 日期,80";
		String parmMap = "BILL_DATE";
		String column ="0,left";
		
		for (int i = 0; i < packageCode.size(); i++) {
			String s = packageCode.get(i);
			heard =heard+ "; ,50;"+s+"销售,170;,50;"+s+"退费,170;,50;"+s+"小计,170";		
			parmMap = parmMap + ";SALE_COUNT"+i+";SALE_AMT"+i+";RE_COUNT"+i+";RE_AMT"+i+";SUM_COUNT"+i+";SUM_AMT"+i;
			int row = 2 + 6 * i;
			column = column +";"+row+",right;"+(row+2)+",right;"+(row+4)+",right";
		}
		table.setHeader(heard);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);
		
		
		TParm tableParm = new TParm();
		//添加第一行数据
		tableParm.addData("BILL_DATE", "");
		String names[] = parmMap.split(";");
		for (int i = 1; i < names.length; i++) {
			if(i%2 == 0){
				tableParm.addData(names[i], "金额");
			}else{
				tableParm.addData(names[i], "数量");
			}
		}
		
		DecimalFormat df = new DecimalFormat("##########0.00");
		for (int i = 0; i < date.size(); i++) {
			
			tableParm.addData("BILL_DATE", date.get(i));
			
			for (int j = 0; j < packageCode.size(); j++) {
				int saleCount =0;
				double saleAmt =0;
				int reCount =0;
				double reAmt =0;
				for (int k = 0; k < parm.getCount(); k++) {
					if(packageCode.get(j).equals(parm.getValue("PACKAGE_DESC", k)) &&
							date.get(i).equals(parm.getValue("BILL_DATE", k))){
						if(parm.getValue("STATUS", k).equals("购买")){
							saleCount += parm.getInt("COUNT", k);
							saleAmt += parm.getDouble("AR_AMT", k);
						}
						if(parm.getValue("STATUS", k).equals("退费")){
							reCount += parm.getInt("COUNT", k);
							reAmt += parm.getDouble("AR_AMT", k);
						}
					}
				}
				
				tableParm.addData("SALE_COUNT"+j, saleCount);
				tableParm.addData("SALE_AMT"+j, df.format(saleAmt));
				tableParm.addData("RE_COUNT"+j, -reCount);
				tableParm.addData("RE_AMT"+j, df.format(reAmt));
				tableParm.addData("SUM_COUNT"+j, saleCount-reCount);
				tableParm.addData("SUM_AMT"+j, df.format(saleAmt+reAmt));
				
			}
	
		}
		
		TParm sumParm = new TParm();//合计
		sumParm.addData("BILL_DATE", "合计");
		for (int i = 1; i < names.length; i++) {
			double sum = 0;
			for (int j = 0; j < tableParm.getCount("BILL_DATE"); j++) {
				sum += tableParm.getDouble(names[i], j);
			}
			if(i%2 == 0){
				tableParm.addData(names[i], df.format(sum));
			}else{

				tableParm.addData(names[i], (int)sum);
			}
		}
		
		//将合计添加到表格最后一行
		tableParm.addRowData(sumParm, 0);
		
		
		table.setParmValue(tableParm);
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: MEMPackageSalesSummaryControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
	
	}
	
	public void onClear(){
		table.removeRowAll();
		table.setHeader("");
		table.setParmMap("");
		table.setColumnHorizontalAlignmentData("");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("E_DATE",today.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", today.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
	}
	
	public void onExport(){
		table.acceptText();
		ExportExcelUtil.getInstance().exportExcel(table, Operator.getHospitalCHNShortName()+"销售套餐汇总表");
	}

}
