package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.cxf.binding.corba.wsdl.Array;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ��������ͳ��
 * </p>
 * 
 * <p>
 * Description: ��������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) BlueCore
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author caowl 2012.10.30
 * @version 1.0
 */
public class BILExaDeptPrintWordControl extends TControl {

	private TTable table;
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ��ʼ������
	 * 
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("S_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("E_DATE", rollDay);
		// ��ʼ��ִ�п��ҺͲ���Ϊ��
		this.setValue("DEPT_CODE", "");
		this.setValue("STATION_CODE", "");

		// �Ƴ�������������
		this.callFunction("UI|Table|removeRowAll");
		//�õ���
		table =(TTable) this.getComponent("Table");
		
		
		//���ñ�ͷ
//		table.setHeader("ִ�п���,100;��һ��/7����,100,double,########0.00;��һ��/13����,100,double,########0.00;�ڶ���/9����,100,double,########0.00;��һ��/����,100,double,########0.00;������/11����,100,double,########0.00;���Ŀ�/11����,100,double,########0.00;ICU,100,double,########0.00;CCU,100,double,########0.00;����ҽ�Ʋ�,100,double,########0.00;��ͯ���ಡ�ڿ�/7����,150,double,########0.00;��л��/13����,100,double,########0.00;�ܼ�,100,double,########0.00");//caowl add ��ͯ���ಡ�ڿƺʹ�л�� 20130407		
//		table.setHeader("ִ�п���,100; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00; ,100,double,########0.00;�ܼ�,100,double,########0.00");//caowl add ��ͯ���ಡ�ڿƺʹ�л�� 20130407		
		table.setHeader("ִ�п���,100;�ܼ�,100,double,########0.00");
		//		table.setHeader(header.toString());
		//����parmMap
//		table.setParmMap("COST_CENTER_CHN_DESC;SUM1;SUM2;SUM3;SUM4;SUM5;SUM6;SUM7;SUM8;SUM9;SUM10;SUM11;SUM");//caowl add sum10,sum11 20130407
		table.setParmMap("COST_CENTER_CHN_DESC;SUM");
	}
	/**
	 * ��ѯ
	 * */
	public void onQuery() {
		this.callFunction("UI|Table|removeRowAll");
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		startTime = startTime +" "+ this.getValueString("T1");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		endTime = endTime +" "+ this.getValueString("T2");
		//���ñ�ͷ add by lich 20150107 start
		String sqlc = "SELECT DISTINCT(A.EXE_DEPT_CODE),B.DEPT_CHN_DESC  FROM IBS_ORDD A,SYS_DEPT B WHERE A.EXE_DEPT_CODE = B.DEPT_CODE "+
				" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')";
		TParm exeDeptParm =  new TParm(TJDODBTool.getInstance().select(sqlc));
//		System.out.println("sqlc="+sqlc);
		StringBuffer header = new StringBuffer();
		header.append("ִ�п���,100;");	
		StringBuffer parmMap = new StringBuffer();
		parmMap.append("COST_CENTER_CHN_DESC;");
		for (int i = 0; i < exeDeptParm.getCount("EXE_DEPT_CODE"); i++) {
			
			if(null != exeDeptParm.getValue("DEPT_CHN_DESC", i) && 
					!"".equals(exeDeptParm.getValue("DEPT_CHN_DESC", i))&& 
					exeDeptParm.getValue("DEPT_CHN_DESC", i).length() > 0){
				header.append(exeDeptParm.getValue("DEPT_CHN_DESC",i)+",120,double,########0.00;");
				parmMap.append("SUM"+(i+1)+";");
			}else{
//				header.append(" ,100,double,########0.00;");
				continue;
			}			
		}	
		header.append("�ܼ�,100,double,########0.00");
		parmMap.append("SUM");
//		System.out.println("parmMap- - - - - -" + parmMap);
//		System.out.println("Header="+header.toString());
		table.setHeader(header.toString());
		table.setParmMap(parmMap.toString());
		//���ñ�ͷ add by lich 20150107 end
		
		double sumN = 0.0;
		double sumAll = 0.0;
		TParm tParm = new TParm();
		tParm.setData("COST_CENTER_CHN_DESC", "�ϼ�");
		String sql = "";
//		List list = new ArrayList();
		for (int i = 0; i < exeDeptParm.getCount("EXE_DEPT_CODE"); i++) {
//			System.out.println("i---------------------------"+i);
			sql = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
			" FROM IBS_ORDD A,SYS_COST_CENTER B"+
			" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
			" AND EXE_DEPT_CODE = '"+exeDeptParm.getValue("EXE_DEPT_CODE",i)+"'"+
			" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
			" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
			" ORDER BY EXE_DEPT_CODE";
//			System.out.println("sql = " + sql);
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			sumN = selParm.getDouble("SUM(A.TOT_AMT)", 0);
			tParm.setData("SUM"+(i+1), sumN);
			sumAll += sumN;
//			System.out.println("forsumAll: : : : : :" + sumAll);
		}
		tParm.setData("SUM", sumAll);
//		System.out.println("tParm = = = = = " + tParm);
//		messageBox("tParm.getCount()=" + tParm.getCount()+"   table.getRowCount()=" + table.getRowCount());
//		if( tParm.getCount("COST_CENTER_CHN_DESC") > 0 && table.getRowCount() < 1){
//			messageBox("if");
//			table.setParmValue(tParm);
			table.addRow(tParm);
//		}else{
//			messageBox("else");
//			table.setRowParmValue(table.getShowCount(), tParm);
//		}

		if (table.getRowCount() < 1) {
			// ��������
			this.messageBox("E0008");
			this.initPage();			
		}			

		
		
		
//		//��һ��    �߲���
//		String sql1 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030201' AND STATION_CODE = 'S07'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//	
//		System.out.println("sql1="+sql1);
//		TParm selParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		
//		//��һ�� ʮ������
//		String sql2 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030101' AND STATION_CODE = 'S13'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//	
//		
//		TParm selParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		
//		//�ڶ��� �Ų���
//		String sql3 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030102' AND STATION_CODE = 'S09'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm3 = new TParm(TJDODBTool.getInstance().select(sql3));
//		
//		//��һ��/����
//		String sql4 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030101' AND STATION_CODE = 'V01'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm4 = new TParm(TJDODBTool.getInstance().select(sql4));
//		
//		//������   ʮһ����
//		String sql5 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030103' AND STATION_CODE = 'S11'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm5 = new TParm(TJDODBTool.getInstance().select(sql5));
//		
//		//���Ŀ� ʮһ����
//		String sql6 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030104' AND STATION_CODE = 'S11'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm6 = new TParm(TJDODBTool.getInstance().select(sql6));
//		
//		//ICU
//		String sql7 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '0303' AND STATION_CODE = 'I01'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm7 = new TParm(TJDODBTool.getInstance().select(sql7));
//		
//		//CCU
//		String sql8 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '0304' AND STATION_CODE = 'C02'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm8 = new TParm(TJDODBTool.getInstance().select(sql8));
//		
//		//�������Ʋ�
//		String sql9 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//						" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//						" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//						" AND DEPT_CODE = '030901' AND STATION_CODE = 'V01'"+
//						" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//						" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//						" ORDER BY EXE_DEPT_CODE";
//		
//		TParm selParm9 = new TParm(TJDODBTool.getInstance().select(sql9));
//		
//		//caowl 20130407 start
//		//��ͯ���ಡ�ڿ�/7����
//		String sql10 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//		" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//		" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//		" AND DEPT_CODE = '030106' AND STATION_CODE = 'S07'"+
//		" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//		" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//		" ORDER BY EXE_DEPT_CODE";
//		TParm selParm10 = new TParm(TJDODBTool.getInstance().select(sql10));
//		
//		//��л��/13����
//		String sql11 = "SELECT A.EXE_DEPT_CODE ,B.COST_CENTER_CHN_DESC,SUM(A.TOT_AMT)"+
//		" FROM IBS_ORDD A,SYS_COST_CENTER B"+
//		" WHERE A.EXE_DEPT_CODE = B.COST_CENTER_CODE"+
//		" AND DEPT_CODE = '030105' AND STATION_CODE = 'S13'"+
//		" AND BILL_DATE BETWEEN TO_DATE('"+startTime+"','yyyymmdd hh24:mi:ss') AND TO_DATE('"+endTime+"','yyyymmdd hh24:mi:ss')"+
//		" GROUP BY EXE_DEPT_CODE ,COST_CENTER_CHN_DESC"+
//		" ORDER BY EXE_DEPT_CODE";
//		TParm selParm11 = new TParm(TJDODBTool.getInstance().select(sql11));
//		//caowl 20130407 end
//		
//		//��ѯ���е�ִ�п���
//		String sql = "SELECT COST_CENTER_CODE AS EXE_DEPT_CODE ,COST_CENTER_CHN_DESC FROM SYS_COST_CENTER ";
//		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));									
//		TParm parmValue = new TParm();
//	
//		double sum1=0.00;
//		double sum2=0.00;
//		double sum3=0.00;
//		double sum4=0.00;
//		double sum5=0.00;
//		double sum6=0.00;
//		double sum7=0.00;
//		double sum8=0.00;
//		double sum9=0.00;
//		double sum10=0.00;//caowl add 20130407
//		double sum11 = 0.00;//caowl add 20130407
//		double sums = 0.00;
//		for(int i = 0;i<selParm.getCount();i++){
//			String exe_dept_code =selParm.getValue("EXE_DEPT_CODE",i);
//			
//			parmValue.setData("COST_CENTER_CHN_DESC",selParm.getValue("COST_CENTER_CHN_DESC",i));
//			//1111111111111111
//			double sum = 0.00;
//			if(selParm1.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm1.getCount();j++){				
//					if(selParm1.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){						
//						parmValue.setData("SUM1",selParm1.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm1.getDouble("SUM(A.TOT_AMT)",j);
//						sum1 += selParm1.getDouble("SUM(A.TOT_AMT)",j);;
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM1",0.00);
//				sum += 0.00;
//				sum1 += 0.00;
//			}
//			//222222222222222222
//			if(selParm2.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm2.getCount();j++){
//					if(selParm2.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM2",selParm2.getValue("SUM(A.TOT_AMT)",j));
//						sum +=   selParm2.getDouble("SUM(A.TOT_AMT)",j);
//						sum2 += selParm2.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM2",0.00);
//				sum += 0.00;
//				sum2 += 0.00;
//			}
//			//333333333333333333
//			if(selParm3.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm3.getCount();j++){
//					if(selParm3.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM3",selParm3.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm3.getDouble("SUM(A.TOT_AMT)",j);
//						sum3 += selParm3.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					
//					}
//				}
//			}
//			
//			else{
//				parmValue.setData("SUM3",0.00);
//				sum += 0.00;
//				sum3 += 0.00;
//			}
//			//4444444444444444444
//			if(selParm4.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm4.getCount();j++){
//					if(selParm4.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM4",selParm4.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm4.getDouble("SUM(A.TOT_AMT)",j);
//						sum4 += selParm4.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM4",0.00);
//				sum += 0.00;
//				sum4 +=0.00;
//			}
//			//5555555555555555555
//			if(selParm5.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm5.getCount();j++){
//					if(selParm5.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM5",selParm5.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm5.getDouble("SUM(A.TOT_AMT)",j);
//						sum5 += selParm5.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM5",0.00);
//				sum += 0.00;
//				sum5 += 0.00;
//			}
//			//66666666666666666
//			if(selParm6.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm6.getCount();j++){
//					if(selParm6.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM6",selParm6.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm6.getDouble("SUM(A.TOT_AMT)",j);
//						sum6 += selParm6.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM6",0.00);
//				sum += 0.00;
//				sum6 += 0.00;
//			}
//			//77777777777777
//			if(selParm7.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm7.getCount();j++){
//					if(selParm7.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM7",selParm7.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm7.getDouble("SUM(A.TOT_AMT)",j);
//						sum7 += selParm7.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM7",0.00);
//				sum += 0.00;
//				sum7 += 0.00;
//			}
//			//88888888888888
//			if(selParm8.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm8.getCount();j++){
//					if(selParm8.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM8",selParm8.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm8.getDouble("SUM(A.TOT_AMT)",j);
//						sum8 += selParm8.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM8",0.00);
//				sum += 0.00;
//				sum8 += 0.00;
//			}
//			//999999999999999
//			if(selParm9.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm9.getCount();j++){
//					if(selParm9.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM9",selParm9.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm9.getDouble("SUM(A.TOT_AMT)",j);
//						sum9 += selParm9.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM9",0.00);
//				sum += 0.00;
//				sum9 += 0.00;
//			}
//			//caowl 20130407 start
//			//��ʮ��
//			if(selParm10.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm10.getCount();j++){
//					if(selParm10.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM10",selParm10.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm10.getDouble("SUM(A.TOT_AMT)",j);
//						sum10 += selParm10.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM10",0.00);
//				sum += 0.00;
//				sum10 += 0.00;
//			}
//			
//			//��ʮһ��
//			if(selParm11.getValue("EXE_DEPT_CODE").contains(exe_dept_code)){
//				for(int j = 0 ;j<selParm11.getCount();j++){
//					if(selParm11.getValue("EXE_DEPT_CODE",j).equals(exe_dept_code)){
//						parmValue.setData("SUM11",selParm11.getValue("SUM(A.TOT_AMT)",j));
//						sum += selParm11.getDouble("SUM(A.TOT_AMT)",j);
//						sum11 += selParm11.getDouble("SUM(A.TOT_AMT)",j);
//					}
//					else{
//						continue;
//					}
//				}
//			}
//			else{
//				parmValue.setData("SUM11",0.00);
//				sum += 0.00;
//				sum11 += 0.00;
//			}
//			//caowl 20130407 end
//			
//			parmValue.setData("SUM",sum);
//			sums += sum;
//			
//			if(sum != 0){
//				table.addRow(parmValue);				
//			}								
//		}
//	   
//		TParm parm = new TParm();
//		parm.setData("COST_CENTER_CHN_DESC", "�ϼ�");
//		parm.setData("SUM1", sum1);
//		parm.setData("SUM2", sum2);
//		parm.setData("SUM3", sum3);
//		parm.setData("SUM4", sum4);
//		parm.setData("SUM5", sum5);
//		parm.setData("SUM6", sum6);
//		parm.setData("SUM7", sum7);
//		parm.setData("SUM8", sum8);
//		parm.setData("SUM9", sum9);
//		//caowl 20130407 start
//		parm.setData("SUM10",sum10);
//		parm.setData("SUM11",sum11);
//		//caowl 20130407 end
//		parm.setData("SUM", sums);
//		
//		table.addRow(parm);
//
//		if (table.getRowCount() < 1) {
//			// ��������
//			this.messageBox("E0008");
//			this.initPage();			
//		}			
		
	}

	/**
	 * ���
	 * */
	public void onClear() {
		initPage();		
	}



	/**
	 * ����Excel
	 * */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "��������ͳ�Ʊ�");
	}

	/**
	 * �õ��ϸ���
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}

	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}

}
