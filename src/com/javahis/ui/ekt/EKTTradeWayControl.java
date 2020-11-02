package com.javahis.ui.ekt;



import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 医疗卡交易方式汇总</p>
 *
 * <p>Description:  医疗卡交易方式汇总 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p> 
 *
 * @author zhangp 20120110
 * @version 1.0
 */

public class EKTTradeWayControl  extends TControl{
	
	TTable table;
	private int radio = 1;
	private String startDate = "";
	private String endDate = "";
	String regionCode = "";
	/**
     * 初始化方法
     */
    public void onInit() {
    	table = (TTable)this.getComponent("TABLE");
    	String region = Operator.getRegion();
    	setValue("REGION_CODE", region);
    	Timestamp today = SystemTool.getInstance().getDate();
    	setValue("START_DATE", today);
    	setValue("END_DATE", today);
 
    	regionCode = getValueString("REGION_CODE");
    }
    /**
     * radio监听
     * @param obj
     */
    public void onCheck(Object obj) {
        if ("1".equals(obj.toString())) {
            table.setHeader("交易方式,120,ACCNT_TYPE;交易金额,120,double,#########0.00;手续费,80,double,#########0.00;总金额,120,double,#########0.00");
            radio = 1;
        }
        if ("2".equals(obj.toString())) {
            table.setHeader("付款方式,120;交易金额,120,double,#########0.00;手续费,80,double,#########0.00;总金额,120,double,#########0.00");
            radio = 2;
        }
        if ("3".equals(obj.toString())) {
        	table.setHeader("人员编号,120;交易金额,120,double,#########0.00;手续费,80,double,#########0.00;总金额,120,double,#########0.00");
        	radio = 3;
        }
    }
    /**
     * 获取sql
     * @return
     */
    public String getSql(){
    	regionCode = getValueString("REGION_CODE");
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + "000000";
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + "235959";
		}
    	StringBuilder sql = new StringBuilder();
    	String sql1 = "";
    	//交易方式
    	if(radio == 1){
    		sql1 = "SELECT A.ACCNT_TYPE AS PARM1,SUM(A.AMT) AS PARM2, " +
    				" SUM(A.PROCEDURE_AMT) AS PARM3,SUM(A.AMT+ A.PROCEDURE_AMT) AS PARM4 " +
    				" FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE " +
    				" A.CREAT_USER = B.USER_ID " +
    				" AND A.ACCNT_TYPE<>'5' ";
    		sql.append(sql1);
    		if(regionCode!=null&&!regionCode.equals("")){
    			sql.append(" AND B.REGION_CODE='"+regionCode+"' ");
    		}
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql.append(" AND A.STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ");
    		}
    		sql.append(" GROUP BY ACCNT_TYPE ORDER BY A.ACCNT_TYPE");
    		return sql.toString();
    	}
    	//付款方式
    	if(radio == 2){
    		//=====zhangp 20120226 modify start
//    		sql1 = "SELECT A.AMT-B.SUM AS CASH,B.SUM AS GREENPATH FROM ";
//    		sql.append(sql1);
//    		String sqla = " (SELECT SUM(A.AMT) AS AMT FROM EKT_TREDE A,REG_PATADM B WHERE B.CASE_NO = A.CASE_NO ";
//    		String sqlb = " (SELECT SUM(GREEN_PATH_TOTAL)-SUM(GREEN_BALANCE) AS SUM FROM REG_PATADM WHERE 1=1 ";
//    		if(regionCode!=null&&!regionCode.equals("")){
//    			sqla = sqla + " AND B.REGION_CODE = '"+regionCode+"' ";
//    			sqlb = sqlb + " AND REGION_CODE = '"+regionCode+"' ";
//    		}
//    		if(!startDate.equals("")&&!endDate.equals("")){
//    			sqla = sqla + " AND A.OPT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
//    			sqlb = sqlb + " AND OPT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
//    		}
//    		sqla = sqla + " ) A, ";
//    		sqlb = sqlb + " ) B ";
//    		sql.append(sqla+sqlb);
//    		System.out.println(sql);
//    		return sql.toString();
    		sql1 =
    			"SELECT SUM(AMT) AS CASH FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '4'";
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql1 = sql1 + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
    		}
    		return sql1;
    		//======zhangp 20120226 modify end
    	}
    	//人员编号
    	if(radio == 3){
    		sql1 = "SELECT CREAT_USER AS PARM1,SUM(A.AMT) AS PARM2, SUM(PROCEDURE_AMT) AS PARM3,SUM(A.AMT+ PROCEDURE_AMT) AS PARM4 " +
    				" FROM EKT_BIL_PAY A, SYS_OPERATOR B " +
    				" WHERE A.CREAT_USER=B.USER_ID " +
    				" AND A.ACCNT_TYPE IN ('4','2') ";
    		sql.append(sql1);
    		if(regionCode!=null&&!regionCode.equals("")){
    			sql.append(" AND B.REGION_CODE='"+regionCode+"' ");
    		}
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql.append(" AND A.STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ");
    		}
    		sql.append(" GROUP BY CREAT_USER ORDER BY A.CREAT_USER");
    		return sql.toString();
    	}
    	
    	return sql.toString();
    }
    
    /**
     * 查询
     * 
     */
    public void onQuery(){
    	String sql = getSql();
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    	}
    	if(result.getCount()<=0){
    		messageBox("查无结果");
    	}
//    	sql = 
//    		"SELECT ISSUERSN_CODE,FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '8'";
//    	TParm factResult = new TParm(TJDODBTool.getInstance().select(sql));
    	sql = 
    		"SELECT SUM(PROCEDURE_AMT) AS PROCEDURE_AMT FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '2'";
    	if(!startDate.equals("")&&!endDate.equals("")){
			sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
		}
    	TParm countResult = new TParm(TJDODBTool.getInstance().select(sql));
    	if(radio ==1){
    		for (int i = 0; i < result.getCount(); i++) {
				if(result.getData("PARM1",i).equals("2")){
					result.setData("PARM3", i, countResult.getDouble("PROCEDURE_AMT", 0) );
				}
			}
    	}
    	if(radio==2){
    		sql =
    			"SELECT SUM(AMT) AS CASH FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '6'";
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
    		}
    		TParm tempParm = new TParm(TJDODBTool.getInstance().select(sql));
    		double tempCash = 0.00;
    		if(tempParm.getCount()>0){
    			tempCash = tempParm.getDouble("CASH", 0);
    		}
    		result.setData("CASH", 0 , result.getDouble("CASH", 0)-tempCash);
    		//==zhangp 20120319 start
    		result.setData("PROCEDURE_AMT", 0 , countResult.getDouble("PROCEDURE_AMT", 0));
    		//===zhangp 20120319 end
    		result = getParm(result);
    	}
    	if(radio==3){
//    		sql = 
//	    		"SELECT SUM(PROCEDURE_AMT) AS PROCEDURE_AMT,CREAT_USER FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '2' ";
//    		if(!startDate.equals("")&&!endDate.equals("")){
//    			sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
//    		}
//    		sql = sql + " GROUP BY CREAT_USER ORDER BY CREAT_USER";
//    		countResult = new TParm(TJDODBTool.getInstance().select(sql));
    		sql =
    			"SELECT CREAT_USER AS PARM1,SUM(A.AMT) AS PARM2, SUM(PROCEDURE_AMT) AS PARM3,SUM(A.AMT)+ SUM(PROCEDURE_AMT) AS PARM4 " +
				" FROM EKT_BIL_PAY A, SYS_OPERATOR B " +
				" WHERE A.CREAT_USER=B.USER_ID " +
				" AND A.ACCNT_TYPE = '6' ";
    		if(regionCode!=null&&!regionCode.equals("")){
    			sql = sql + " AND B.REGION_CODE='"+regionCode+"' ";
    		}
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql = sql + " AND A.STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
    		}
    		sql = sql + " GROUP BY CREAT_USER ORDER BY A.CREAT_USER";
    		countResult = new TParm(TJDODBTool.getInstance().select(sql));
    		double tempCash = 0.00;
    		for (int i = 0; i < result.getCount(); i++) {
    			for (int j = 0; j < countResult.getCount(); j++) {
    				tempCash = countResult.getDouble("PARM2", j);
    				if(result.getData("PARM1", i).equals(countResult.getData("PARM1", j))){
    					result.setData("PARM3", i, result.getDouble("PARM3", i)+countResult.getDouble("PARM3", j) );
    					result.setData("PARM2", i, result.getDouble("PARM2", i)-tempCash);
        			}
				}
			}
//    		System.out.println("3=="+result);
    	}
    	//zhangp 20120129 修改总计
    		for (int i = 0; i < result.getCount(); i++) {
    			//求总和
    			double parm4 = result.getDouble("PARM2", i) + result.getDouble("PARM3", i);
    			result.setData("PARM4", i, parm4);
			}
//    	System.out.println(result);
    	this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    /**
     * 取得付款方式的tparm
     * @param parm
     * @return
     */
    public TParm getParm(TParm parm){
    	//=====zhangp 20120226 modify start
    	TParm result = new TParm();
    	result.addData("PARM1", "现金");
//    	result.addData("PARM1", "院内支付");
    	result.addData("PARM2",parm.getData("CASH", 0));
//    	result.addData("PARM2", parm.getData("GREENPATH", 0));
    	result.addData("PARM3",parm.getData("PROCEDURE_AMT", 0));
//    	result.addData("PARM3", 0.00);
    	result.addData("PARM4",parm.getData("CASH", 0));
//    	result.addData("PARM4", parm.getData("GREENPATH", 0));
//    	result.setCount(2);
    	result.setCount(1);
    	//======zhangp 20120226 modify end 
    	return result;
    }
    /**
     * 打印
     */
    public void onPrint(){
    	TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		TParm tableparm = table.getParmValue();
		double PARM2total = 0.00;
		double PARM3total = 0.00;
		double PARM4total = 0.00;
		DecimalFormat df = new DecimalFormat("#0.00"); 
		TParm data = new TParm();// 打印的数据
		TParm parm = new TParm();// 表格数据
		for (int i = 0; i < table.getRowCount(); i++) {
			if(radio == 1){
				if(tableparm.getData("PARM1", i).equals("1")){
					parm.addData("PARM1", "购卡");
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
				}
				if(tableparm.getData("PARM1", i).equals("3")){
					parm.addData("PARM1", "挂失");
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
				}
				if(tableparm.getData("PARM1", i).equals("4")){
					parm.addData("PARM1", "充值");
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
				}
				if(tableparm.getData("PARM1", i).equals("6")){
					parm.addData("PARM1", "退费");
					PARM2total = PARM2total - tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total - tableparm.getDouble("PARM4", i);
				}
			}else{
				if(radio == 2){
					data.setData("TITLE2", "TEXT", "售卡汇总汇总表-付款方式");
				}else{
					data.setData("TITLE2", "TEXT", "售卡汇总汇总表-人员编号");
				}
				parm.addData("PARM1", tableparm.getData("PARM1", i));
				PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
				PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
				PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
			}
			parm.addData("PARM2", df.format(tableparm.getDouble("PARM2", i)));
			parm.addData("PARM3", df.format(tableparm.getDouble("PARM3", i)));
			parm.addData("PARM4", df.format(tableparm.getDouble("PARM4", i)));
		}
		if(radio == 1){
			data.setData("TITLE2", "TEXT", "售卡汇总汇总表-交易方式");
			data.setData("PARM1TITLE", "TEXT", "交易方式");
		}
		if(radio == 2){
			data.setData("TITLE2", "TEXT", "售卡汇总汇总表-付款方式");
			data.setData("PARM1TITLE", "TEXT", "付款方式");
		}
		if(radio == 3){
			data.setData("TITLE2", "TEXT", "售卡汇总汇总表-人员编号");
			data.setData("PARM1TITLE", "TEXT", "人员编号");
		}
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("PRINTDATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		if(!startDate.equals("")){
			data.setData("BUSINESSDATE", "TEXT", "交易时间: "+startDate.substring(0, 4)+
				"/"+startDate.substring(4, 6)+"/"+startDate.substring(6, 8)+" - "+
				endDate.substring(0, 4)+
				"/"+endDate.substring(4, 6)+"/"+endDate.substring(6, 8));
		}
		data.setData("PARM2TOTAL", "TEXT", df.format(PARM2total) );
		data.setData("PARM3TOTAL", "TEXT", df.format(PARM3total));
		data.setData("PARM4TOTAL", "TEXT", df.format(PARM4total));
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		parm.setCount(parm.getCount("PARM1"));
		parm.addData("SYSTEM", "COLUMNS", "PARM1");
		parm.addData("SYSTEM", "COLUMNS", "PARM2");
		parm.addData("SYSTEM", "COLUMNS", "PARM3");
		parm.addData("SYSTEM", "COLUMNS", "PARM4");
		data.setData("TABLE", parm.getData());
		//==========modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTradeWay.jhw",data);
		//==========modify by lim 2012/02/24 begin
    }
    /**
     * 清空
     */
    public void onClear(){
    	clearValue("START_DATE;END_DATE");
    	Timestamp today = SystemTool.getInstance().getDate();
    	setValue("START_DATE", today);
    	setValue("END_DATE", today);
    	table.removeRowAll();
    }
    
}
