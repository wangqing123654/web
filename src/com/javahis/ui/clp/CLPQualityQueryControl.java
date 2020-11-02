package com.javahis.ui.clp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.text.DecimalFormat;
import java.util.Date;

import jdo.clp.CLPQualityQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.jdo.TJDOTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
//import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
//import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:�ٴ�·����������������ѯ</p>
 *
 * <p>Description:�ٴ�·����������������ѯ </p>
 *
 * <p>Copyright: Copyright (c) blueCore</p>
 *
 * <p>Company: blueCore</p>
 *
 * @author  fux 20121228
 * @version 4.0
 */
public class CLPQualityQueryControl extends TControl{
	
	// ��ʼʱ��
	private TTextFormat start_date;
	// ����ʱ��
	private TTextFormat end_date;
	// ���
	private TTable table;

	public CLPQualityQueryControl() {

	}
	/***
	 * ��ʼ��
	 * */
	 public void onInit() {	       
		 super.onInit();
	     initPage();	       	      	      
	    }
	 /**
	  * ��ʼ��ҳ��
	  * */
	 public void initPage(){
		 this.callFunction("UI|Table|removeRowAll");
		 initControl();
	 }
	/**
	 * ��ʼ���ؼ�
	 * */ 
	 public void initControl(){
		    Timestamp date = StringTool.getTimestamp(new Date());
			// ��ʼ��ʱ���ѯ����
			this.setValue("e_Date", date.toString().substring(0, 10).replace('-',
					'/')
					+ " 23:59:59");
			this.setValue("s_Date", StringTool.rollDate(date, -7).toString()
					.substring(0, 10).replace('-', '/')
					+ " 00:00:00");
			table = (TTable) this.getComponent("Table");
			start_date = (TTextFormat) this.getComponent("s_Date");
			end_date = (TTextFormat) this.getComponent("e_Date");			
	 }
	 private double getRate(double no,double num){
		 if(num==0)
			 return 0.00;
		 return no/num;
	 }
	 /**
	  * ��ѯ
	  * */
	 public void onQuery(){
		// TParm selParm = new TParm();
		 if (!checkData()) {
			return ;
		}
		
		//��Ժ����
		 DecimalFormat df = new DecimalFormat("0.0");
		String deptSelect = "";
		String dept_code = this.getValueString("DEPT_CODE");
		if(dept_code != null && !dept_code.equals("")){
			deptSelect  = " AND M.OUT_DEPT = '"+dept_code+"'";
		}	
	    //�õ���Ժ���ڲ�ѯ����
		String selectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND M.OUT_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}  			
		//1.��ѯ���е��ٴ�·������
		String sqlClpPathName = " SELECT  DISTINCT B.CLNCPATH_CHN_DESC,C.CLNCPATH_CODE "+
								" FROM CLP_MANAGEM C,CLP_BSCINFO B,MRO_RECORD M"+
								" WHERE C.CLNCPATH_CODE = B.CLNCPATH_CODE"+
								" AND C.CASE_NO = M.CASE_NO"+selectCondition+deptSelect;
		TParm result =  new TParm(TJDODBTool.getInstance().select(sqlClpPathName));
		if(result.getCount()<=0){
			this.messageBox("û�в�ѯ������");
			table.setParmValue(new TParm());
			return ;
		}
		for(int i = 0;i<result.getCount();i++){
			TParm parms = result.getRow(i);
			//�뾶����			
			parms.setData("START_DATE", startDate);
			parms.setData("END_DATE", endDate);
			if(dept_code != null && !dept_code.equals("")){
				parms.setData("DS_DEPT_CODE",dept_code);
			}			
			TParm selectData1 = CLPQualityQueryTool.getInstance().selectData("querySum", parms);		
			result.setData("NOSUM",i,selectData1.getInt("NOSUM",0));
			//���·������		
			TParm selectData2 = CLPQualityQueryTool.getInstance().selectData("queryNo1", parms);
		    result.setData("NO1",i,selectData2.getInt("NO1",0));
			//��������		
			//TParm selectData3 = CLPQualityQueryTool.getInstance().selectData("queryNo2", parms);
			result.setData("NO2",i,selectData2.getInt("NO1",0));
			//�������			
			TParm selectData4 = CLPQualityQueryTool.getInstance().selectData("queryNo3", parms);
			result.setData("NO3",i,selectData4.getInt("NO3",0));
			
			//��������		
			TParm selectData5 = CLPQualityQueryTool.getInstance().selectData("queryNo4", parms);
			result.setData("NO4",i,selectData5.getInt("NO4",0));
			
			//Ժ�ڸ�Ⱦ����
			TParm selectData6 = CLPQualityQueryTool.getInstance().selectData("queryNo5", parms);
			result.setData("NO5",i,selectData6.getInt("NO5",0));
			
			//ʵ��סԺ����	��Ժ��ռ������	
			TParm selectData7 = CLPQualityQueryTool.getInstance().selectData("queryNo6", parms);
			result.setData("NO6",i,selectData7.getInt("NO6",0));
			
			//��ǰƽ��סԺ��
			TParm selectData8 =  CLPQualityQueryTool.getInstance().selectData("queryNo8", parms);
			result.setData("NO8",i,selectData8.getDouble("NO8",0));
			
			//ʵ��סԺ��						
			TParm selectData9 = CLPQualityQueryTool.getInstance().selectData("queryNo9", parms);
			result.setData("NO9",i,selectData9.getInt("NO9",0));
			
			//ʵ��ҩƷ�� 
			TParm selectData10 =CLPQualityQueryTool.getInstance().selectData("queryNo10", parms);
			result.setData("NO10",i,selectData10.getDouble("NO10",0));	
		}	

		// �ܼ���Ҫ����		
		// ����·������
		double inPathCount = 0;
		//���·������
		double overPathCount = 0;
		//��������
		double varianceCount = 0;
		//������
		double varRate = 0;
		// ���·������
		double outPathCout = 0;
		//�����
		double outRate = 0;
		//��������
		double deathCount = 0;
		//Ժ�ڸ�Ⱦ����
		double congionCount = 0;
		//ƽ��סԺ��
		double avgAdmDays = 0;
		//��Ժ��ռ���ܴ�
		double sumBed = 0;
		//��ǰƽ��סԺ��
		double preOperatorAdmDays = 0;
		//�˾�סԺ��
		double avgAdmCost = 0;
		//�˾�ҩƷ��
		double avgPhaCost = 0;
		
		int row = result.getCount();
	
		TParm selParm = new TParm();
		for(int i = 0;i<row;i++){
			selParm.addData("CLNCPATH_CHN_DESC", result.getValue("CLNCPATH_CHN_DESC",i));//CLNCPATH_CHN_DESC �ٴ�·������
			selParm.addData("NOSUM",result.getValue("NOSUM",i));//NOSUM �뾶����
			inPathCount += result.getInt("NOSUM",i);
			selParm.addData("NO1",result.getValue("NO1",i));//NO1 ���·������
			overPathCount += result.getInt("NO1",i);
			selParm.addData("NO2", result.getValue("NO2",i));//NO2��������		
			
			double no2 = result.getDouble("NO2",i);	
			varianceCount += no2;
			double nosum = result.getDouble("NOSUM",i);				
			double rate1= 0;
			String variancerate = "";
			if (no2 <= 0) {
				variancerate = rate1 + "%" + "";
				selParm.addData("VARIANCERATE",variancerate );//VARIANCERATE ������
			} else {
				rate1 = getRate(no2,nosum);
				BigDecimal bdi = new BigDecimal(rate1);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				rate1 = bdi.multiply(parmMuli).doubleValue();				
				variancerate = rate1 + "%" + "";
				selParm.addData("VARIANCERATE",variancerate );//VARIANCERATE ������
			}	
			varRate += rate1;
						
			selParm.addData("NO3", result.getValue("NO3",i));//NO3 �������
			double no3 = result.getDouble("NO3",i);
			outPathCout += no3;
			double rate2 = 0;
			String overflowrate = "";
			if(no3 <=0){
				overflowrate = rate2 + "%" + "";
				selParm.addData("OVERFLOWRATE", overflowrate);//OVERFLOWRATE �����
			}else{
				rate2 = getRate(no3,nosum);
				BigDecimal bdi = new BigDecimal(rate2);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				rate2 = bdi.multiply(parmMuli).doubleValue();				
				overflowrate = rate2 + "%" + "";
				selParm.addData("OVERFLOWRATE", overflowrate);//OVERFLOWRATE �����
			}
			outRate += rate2;			
		
			selParm.addData("NO4",result.getValue("NO4",i));//NO4 ��������
			deathCount += result.getInt("NO4",i);
			selParm.addData("NO5",result.getValue("NO5",i));//NO5 Ժ�ڸ�Ⱦ����
			congionCount += result.getInt("NO5",i);
			double no6 = result.getDouble("NO6",i);
			double days = 0;
			if(no6<=0){
				selParm.addData("AVGADMDAYS", 0.0);//AVGADMDAYS ƽ��סԺ��
			}else{
				days = getRate(no6,nosum);
				//yanjing 20130723 ע
//				BigDecimal bdi = new BigDecimal(days);
//				bdi = bdi.setScale(4, 4);
//				BigDecimal parmMuli = new BigDecimal(100);format
//				parmMuli = parmMuli.setScale(0);
//				days = bdi.multiply(parmMuli).doubleValue();
				selParm.addData("AVGADMDAYS",df.format(days));//AVGADMDAYS ƽ��סԺ��  add caoyong df.() 20131014
			}
			avgAdmDays += days;
//			selParm.addData("SUMBEDDAYS", result.getDouble("NO6",i));//SUMBEDDAYS ��Ժ��ռ���ܴ�����      ����סԺ����
			selParm.addData("SUMBEDDAYS", no6);//SUMBEDDAYS ��Ժ��ռ���ܴ�����      ����סԺ����
			sumBed += result.getInt("NO6",i);
			//��ʽ
			double no8 = result.getDouble("NO8",i);
			double aveDays = 0;
			if(no8==0){
				selParm.addData("BEFOREOPERATORADMDAYS",0.0);//BEFOREOPERATORADMDAYS ��ǰƽ��סԺ��
			}else{
				aveDays =getRate(no8,nosum);
				selParm.addData("BEFOREOPERATORADMDAYS",df.format(aveDays) );//BEFOREOPERATORADMDAYS ��ǰƽ��סԺ�� add caoyong df.format() 20131014
				
			}
			preOperatorAdmDays += aveDays;
			double no9 = result.getDouble("NO9",i);
			double cost = 0;
			if(no9<0){
				selParm.addData("AVGCOST", 0.0);//AVGCOST �˾�סԺ��
			}else{
				cost =getRate(no9,nosum);
//				BigDecimal bdi = new BigDecimal(cost);
//				bdi = bdi.setScale(4, 4);
//				BigDecimal parmMuli = new BigDecimal(100);
//				parmMuli = parmMuli.setScale(0);
//				cost = bdi.multiply(parmMuli).doubleValue();
				selParm.addData("AVGCOST",df.format(cost));//AVGCOST �˾�סԺ�� add caoyong df.format() 20131014
			}
			avgAdmCost += cost;
			double no10 = result.getDouble("NO10",i);
			double phaCost = 0;
			if(no10<0){
				selParm.addData("AVGPHACOST",0.0);//AVGPHACOST �˾�ҩƷ��		
			}else{
				phaCost =getRate(no10,nosum);
				selParm.addData("AVGPHACOST",df.format(phaCost));//AVGPHACOST �˾�ҩƷ��	 add caoyong df.format() 20131014
			}
			avgPhaCost += phaCost;
			//selParm.addData("AVGPHACOST","");//AVGPHACOST �˾�ҩƷ��			
		}
		//=================add caoyong 20131014 start=================
    	//   ���й���parm��ֵ    
		//�ٴ�·������,150;�뾶����,70;���·������,80;��������,70;������,70;�������,70;�����,70;��������,70;Ժ�ڸ�Ⱦ����,100;ƽ��סԺ��,70;��Ժ��ռ���ܴ�����,120;��ǰƽ��סԺ��,100;�˾�סԺ��,80;�˾�ҩƷ��,80
		//CLNCPATH_CHN_DESC;NOSUM;NO1;NO2;VARIANCERATE;NO3;OVERFLOWRATE;NO4;NO5;AVGADMDAYS;SUMBEDDAYS;BEFOREOPERATORADMDAYS;AVGCOST;AVGPHACOST
		
		int nosum=0;//�뾶����������
		int no1=0;//���·������
		double no2=0;//��������
		double no3=0;//�������
		int no4=0;//��������
		int no5=0;//Ժ�ڸ�Ⱦ����
		int sumbeddays=0;//��Ժ��ռ���ܴ�����
		double no6=0;
		double no8=0;
		double no9=0;
		double no10=0;
		double avgadmdays=0;//ƽ��סԺ�� 
		double beforeoperatoradmdays=0;//��ǰƽ��סԺ��
		double avgcost=0;//�˾�סԺ��
		double avgphacost=0;//�˾�ҩƷ��
		double variancerate=0;
		double overflowrate=0;
		for (int j = 0; j < selParm.getCount("CLNCPATH_CHN_DESC"); j++) {
			nosum+=selParm.getInt("NOSUM",j);
			no1+=selParm.getInt("NO1",j);
			no2+=selParm.getDouble("NO2",j);
			no3+=selParm.getDouble("NO3",j);
			no4+=selParm.getInt("NO4",j);
			no5+=selParm.getInt("NO5",j);
			no9+=result.getDouble("NO9",j);
			no10+=result.getDouble("NO10",j);
			no6+=result.getDouble("NO6",j);
			no8+=result.getDouble("NO8",j);
			sumbeddays+=selParm.getInt("SUMBEDDAYS",j);
		}
		overflowrate=getRate(no3,no1);
		//���������/���·��������
		variancerate=getRate(no2,no1);;//����������/���·��������
		avgadmdays=getRate(no6,no1);;//����Ժ��ռ���ܴ�����/���·����������
		beforeoperatoradmdays=getRate(no8,no1);;//����ǰסԺ���ܺ�/���·��������
		avgcost=getRate(no9,no1);;//����ǰסԺ���ܺ�/���·��������
		avgphacost=getRate(no10,no1);;//�����·������ҩƷ���ܺ�/���·��������
		
		selParm.addData("CLNCPATH_CHN_DESC", "ȫԺ�ϼ�");
		selParm.addData("NOSUM", nosum);//�ϼ��뾶����������
		selParm.addData("NO1",no1);//�ϼ����·������
		selParm.addData("NO2", no2);//�ϼƱ�������
		selParm.addData("VARIANCERATE", df.format(variancerate*100)+"%");// �ϼƱ�����
		selParm.addData("NO3", no3);//�ϼ��������
		selParm.addData("OVERFLOWRATE",df.format(overflowrate*100)+"%");//�����
		selParm.addData("NO4",no4);//�ϼ���������
		selParm.addData("NO5",no5);//�ϼ�Ժ�ڸ�Ⱦ����
		selParm.addData("AVGADMDAYS", df.format(avgadmdays));//�ϼ�ƽ��סԺ��
		selParm.addData("SUMBEDDAYS", df.format(sumbeddays));//�ϼƳ�Ժ��ռ���ܴ�����
		selParm.addData("BEFOREOPERATORADMDAYS", df.format(beforeoperatoradmdays));//�ϼ���ǰƽ��סԺ��
		selParm.addData("AVGCOST", df.format(avgcost));//�ϼ��˾�סԺ��
		selParm.addData("AVGPHACOST", df.format(avgphacost));//�ϼ��˾�ҩƷ��
		for (int i = 0; i < selParm.getCount("NO1"); i++) {
			selParm.setData("NO1",i,selParm.getDouble("NO1",i)-selParm.getDouble("NO3",i));
		}
		//=================add caoyong 20131014 end=================
		table.setParmValue(selParm);
	 }
    
	 /**
	  * ��ӡ 
	  */
	public void onPrint() {     
		if (this.table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}	
        TParm tableParm = table.getParmValue();
        TParm PrtParm = new TParm();//��ӡ����
        TParm data = new TParm();
        int count = table.getRowCount();
		for(int i = 0 ;i < count;i++){
			//CLNCPATH_CHN_DESC;CLNCPATH_NUMBER;END_CLNCPATH_NUMBER;VARIANCE_NUMBER;
			//VARIANCE_PERCENTAGE;OVERFLOW_NUMBER;OVERFLOW_PERCENTAGE;DEATH_NUMBER;
			//INF_NUNBER;AVG_INDAY;OUT_DAY;AVG_OPINDAY;AVG_AMT;AVG_PHAAMT
			String Clincpath = tableParm.getValue("CLNCPATH_CHN_DESC", i);//�ٴ�·������
			String ClinicNumber = tableParm.getValue("CLNCPATH_NUMBER", i);//�뾶����
			String EndClinicNumber = tableParm.getValue("END_CLNCPATH_NUMBER", i);//���·������
			String VarianceNumber = tableParm.getValue("VARIANCE_NUMBER", i);//��������
			String VariancePercentage = tableParm.getValue("VARIANCE_PERCENTAGE", i);//������
			String OverflowNumber = tableParm.getValue("OVERFLOW_NUMBER", i);//�������
			String OverflowPercentage = tableParm.getValue("OVERFLOW_PERCENTAGE", i);//�����
			String DeathNumber = tableParm.getValue("DEATH_NUMBER", i);//��������
			String InfNumber = tableParm.getValue("INF_NUNBER", i);//Ժ�ڸ�Ⱦ����
			String AvgInday = tableParm.getValue("AVG_INDAY", i);//ƽ��סԺ��
			String Out_Day = tableParm.getValue("OUT_DAY", i);//��Ժ��ռ���ܴ�����
			String Avg_OpInDay = tableParm.getValue("AVG_OPINDAY", i);//��ǰƽ��סԺ��
			String Avg_Amt = tableParm.getValue("AVG_AMT", i);//�˾�סԺ��
			String Avg_PhaAmt = tableParm.getValue("AVG_PHAAMT", i);//�˾�ҩƷ��
			PrtParm.addData("CLNCPATH_CHN_DESC", Clincpath);
			PrtParm.addData("CLNCPATH_NUMBER", ClinicNumber);
			PrtParm.addData("END_CLNCPATH_NUMBER", EndClinicNumber);
			PrtParm.addData("VARIANCE_NUMBER", VarianceNumber);
			PrtParm.addData("VARIANCE_PERCENTAGE", VariancePercentage);
			PrtParm.addData("OVERFLOW_NUMBER", OverflowNumber);
			PrtParm.addData("OVERFLOW_PERCENTAGE", OverflowPercentage);
			PrtParm.addData("DEATH_NUMBER", DeathNumber);
			PrtParm.addData("INF_NUNBER", InfNumber);
			PrtParm.addData("AVG_INDAY", AvgInday);
			PrtParm.addData("OUT_DAY", Out_Day);
			PrtParm.addData("AVG_OPINDAY", Avg_OpInDay);
			PrtParm.addData("AVG_AMT", Avg_Amt);
			PrtParm.addData("AVG_PHAAMT", Avg_PhaAmt);
		}
		PrtParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		PrtParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "END_CLNCPATH_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "VARIANCE_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "VARIANCE_PERCENTAGE");
		PrtParm.addData("SYSTEM", "COLUMNS", "OVERFLOW_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "OVERFLOW_PERCENTAGE");
		PrtParm.addData("SYSTEM", "COLUMNS", "DEATH_NUMBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "INF_NUNBER");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_INDAY");
		PrtParm.addData("SYSTEM", "COLUMNS", "OUT_DAY");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_OPINDAY");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_AMT");
		PrtParm.addData("SYSTEM", "COLUMNS", "AVG_PHAAMT");
        //������ݻ��ܷ��뱨����
		data.setData("TABLE", PrtParm.getData());
		data.setData("NAME", "TEXT", "�����ˣ�" + Operator.getName());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPQualityQuery.jhw", data);
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
		this.initPage();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·��������������ͳ�Ʊ���");
		}
	}
	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		String start = this.getValueString("s_Date");
		if (start == null || start.length() <= 0) {
			this.messageBox("��ʼʱ�䲻��Ϊ��");
			return false;
		}
		
		String end = this.getValueString("e_Date");
		if (end == null || end.length() <= 0) {
			this.messageBox("����ʱ�䲻��Ϊ��");
			return false;
		}
		return true;
	}
	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}
	 
}  
