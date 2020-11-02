package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * 
 * 
 * <p>
 * Title:ÿ��������������ܱ�
 * </p>
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author huangtt 2015.05.04
 * @version 1.0
 */
public class REGDailyOutpatientSummaryControl extends TControl{
	private TTable table;
	private TParm tableParm = new TParm();
	private String parmMap="";  
	private String parmMap_ek="";  //����
	private String parmMap_jz="";  //����
	private String parmMap_cr="";  //����
	private String parmMap_fk="";  //����
	private String parmMap_ck="";  //����
	private String parmMap_jhsy="";  //�ƻ�����


	public void onInit() {
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", StringTool.rollDate(today, -30));
		this.setValue("E_DATE", today);
		table = (TTable) this.getComponent("TABLE");
		onInitTable();
	}
	
	public void onInitTable(){
		String header = "��,50;��,50;��,50;����,100;��Ŀ,100;����,80";
	    parmMap="YEAR;MONTH;DAY;DEPT_CODE;PROJECT;COUNT";
		parmMap_ek="TRQ_EK;TJ_EK;020325;020311;020312;020314;020313;020326;020319;020320;020318;020321;020400;020303;020308;020316;020302;020301;YGGY_EK";
		parmMap_jz="020103;020104;020102;YGGY_JZ";
		parmMap_cr="020705;YGGY_CR";
		parmMap_fk = "TRQ_FK;TJ_FK;020701;YGGY_FK";
		parmMap_ck = "TRQ_CK;TJ_CK;020702;TCKH_CK;YGGY_CK";
		parmMap_jhsy = "020800;YGGY_JHSY;TRQ_JHSY;TJ_JHSY";
		String column ="0,left;1,left;2,left;3,left;4,left;5,right";
		table.setHeader(header);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(column);


	
	}
	/**
	 * �����ʾ����
	 */
	public void onShowTableValue(){
		TParm showParm = new TParm();
		showParm.setData("TRQ_EK", "����ȯ");
		showParm.setData("TJ_EK", "���");
		showParm.setData("020325", "����");
		showParm.setData("020311", "����");
		showParm.setData("020312", "�ǿ�");
		showParm.setData("020314", "������");
		showParm.setData("020313", "����");
		showParm.setData("020326", "����");
		showParm.setData("020319", "��ǻ");
		showParm.setData("020320", "ENT");
		showParm.setData("020318", "�ۿ�");
		showParm.setData("020321", "Ƥ��");
		showParm.setData("020400", "����");
		showParm.setData("020303", "����");
		showParm.setData("020308", "����");
		showParm.setData("020316", "��������");
		showParm.setData("020302", "����");
		showParm.setData("020301", "�����ڿ�");
		showParm.setData("YGGY_EK", "Ա����ҩ");
		showParm.setData("020103", "���ڼ���");
		showParm.setData("020104", "���⼱��");
		showParm.setData("020102", "���Ƽ���");
		showParm.setData("YGGY_JZ", "Ա����ҩ");
		showParm.setData("020705", "�ڿ�");
		showParm.setData("YGGY_CR", "Ա����ҩ");
		showParm.setData("TRQ_FK", "����ȯ");
		showParm.setData("TJ_FK", "���");
		showParm.setData("020701", "���ﻼ��");
		showParm.setData("YGGY_FK", "Ա����ҩ");
		showParm.setData("TRQ_CK", "����ȯ");
		showParm.setData("TJ_CK", "���");
		showParm.setData("020702", "���ﻼ��");
		showParm.setData("TCKH_CK", "�ײͿͻ�");
		showParm.setData("YGGY_CK", "Ա����ҩ");
		showParm.setData("020800", "�ƻ�����");
		showParm.setData("YGGY_JHSY", "Ա����ҩ");
		showParm.setData("TRQ_JHSY", "����ȯ");
		showParm.setData("TJ_JHSY", "���");
		
		
		for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
			tableParm.setData("PROJECT", i, showParm.getValue(tableParm.getValue("PROJECT", i)));
		}

	}
	
	public void onQuery(){
		tableParm = new TParm();
		String sDate = this.getValueString("S_DATE").replace("-", "").replace(
				"/", "").substring(0, 8);
		String eDate = this.getValueString("E_DATE").replace("-", "").replace(
				"/", "").substring(0, 8);
		
		
		List<String> admDateAll = new ArrayList<String>();
		
		String sql = "SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, REALDEPT_CODE, COUNT(CASE_NO) COUNT" +
				" FROM REG_PATADM WHERE REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" GROUP BY ADM_DATE,REALDEPT_CODE" +
				" ORDER BY ADM_DATE,REALDEPT_CODE";
		TParm regCount = new TParm(TJDODBTool.getInstance().select(sql)); //���п��ҵĵĹҺ���
		
		for (int i = 0; i < regCount.getCount(); i++) {
			if(!admDateAll.contains(regCount.getValue("ADM_DATE", i))){
				admDateAll.add(regCount.getValue("ADM_DATE", i));
			}
		}
		
		//��������ȯ 02����� 01��Ա����ҩ 03
		String[] depts = parmMap_ek.split(";");
		String dept = "";
		for (int i = 2; i < depts.length-1; i++) {
			dept = dept + "'"+depts[i]+"',";
		}
		dept = dept.substring(0,dept.length()-1);
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM" +
				" WHERE CTZ1_CODE IN ('02', '01', '03','04')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND REALDEPT_CODE IN ("+dept+")" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE";
//		System.out.println("sql==="+sql);
		TParm ekCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < ekCount.getCount(); i++) {
			if(!admDateAll.contains(ekCount.getValue("ADM_DATE", i))){
				admDateAll.add(ekCount.getValue("ADM_DATE", i));
			}
		}
		
		//���� Ա����ҩ 03
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM" +
				" WHERE     CTZ1_CODE IN ('03')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND ADM_TYPE = 'E'" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE";
		TParm jzCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < jzCount.getCount(); i++) {
			if(!admDateAll.contains(jzCount.getValue("ADM_DATE", i))){
				admDateAll.add(jzCount.getValue("ADM_DATE", i));
			}
		}
		
		//���� Ա����ҩ 03
		sql = "SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM WHERE CTZ1_CODE IN ('03')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND REALDEPT_CODE IN('020705')" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE ";
		TParm crCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < crCount.getCount(); i++) {
			if(!admDateAll.contains(crCount.getValue("ADM_DATE", i))){
				admDateAll.add(crCount.getValue("ADM_DATE", i));
			}
		}
		
		//��������ȯ 02����� 01��Ա����ҩ 03
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
				" FROM REG_PATADM" +
				"  WHERE CTZ1_CODE IN ('02', '01', '03','04')" +
				" AND REGCAN_USER IS NULL" +
				" AND ARRIVE_FLG = 'Y'" +
				" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
				" AND REALDEPT_CODE='020701'" +
				" GROUP BY ADM_DATE, CTZ1_CODE" +
				" ORDER BY ADM_DATE, CTZ1_CODE ";
		
		TParm fkCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < fkCount.getCount(); i++) {
			if(!admDateAll.contains(fkCount.getValue("ADM_DATE", i))){
				admDateAll.add(fkCount.getValue("ADM_DATE", i));
			}
		}
		
		//��������ȯ 02����� 01��Ա����ҩ 03 ,�ײ� �ͻ�04
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
		" FROM REG_PATADM" +
		"  WHERE CTZ1_CODE IN ('02', '01', '03','04')" +
		" AND REGCAN_USER IS NULL" +
		" AND ARRIVE_FLG = 'Y'" +
		" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
		" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
		" AND REALDEPT_CODE='020702'" +
		" GROUP BY ADM_DATE, CTZ1_CODE" +
		" ORDER BY ADM_DATE, CTZ1_CODE ";
		TParm ckCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < ckCount.getCount(); i++) {
			if(!admDateAll.contains(ckCount.getValue("ADM_DATE", i))){
				admDateAll.add(ckCount.getValue("ADM_DATE", i));
			}
		}
		
		//�ƻ�����  Ա����ҩ 03 
		sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE, CTZ1_CODE, COUNT (CASE_NO) COUNT" +
		" FROM REG_PATADM" +
		"  WHERE CTZ1_CODE IN ('02', '01','03')" +
		" AND REGCAN_USER IS NULL" +
		" AND ARRIVE_FLG = 'Y'" +
		" AND ADM_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDD')" +
		" AND TO_DATE ('"+eDate+"', 'YYYYMMDD')" +
		" AND REALDEPT_CODE='020800'" +
		" GROUP BY ADM_DATE, CTZ1_CODE" +
		" ORDER BY ADM_DATE, CTZ1_CODE ";
		TParm jhsyCount = new TParm(TJDODBTool.getInstance().select(sql));
		
		for (int i = 0; i < jhsyCount.getCount(); i++) {
			if(!admDateAll.contains(jhsyCount.getValue("ADM_DATE", i))){
				admDateAll.add(jhsyCount.getValue("ADM_DATE", i));
			}
		}
		
		//�ײ���������
		sql = "SELECT TO_CHAR (START_DATE, 'YYYY/MM/DD') ADM_DATE ,COUNT(TRADE_NO) COUNT" +
				" FROM MEM_PACKAGE_TRADE_M" +
				" WHERE BILL_DATE BETWEEN TO_DATE ('"+sDate+"000000', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"235959', 'YYYYMMDDHH24MISS')" +
				" GROUP BY START_DATE" +
				" ORDER BY START_DATE";
		TParm packageCount = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(packageCount);
		for (int i = 0; i < jhsyCount.getCount(); i++) {
			if(!admDateAll.contains(jhsyCount.getValue("ADM_DATE", i))){
				admDateAll.add(jhsyCount.getValue("ADM_DATE", i));
			}
		}
		
		String [] depts_jz = parmMap_jz.split(";"); //�������
		String [] depts_cr = parmMap_cr.split(";"); //���˿���
		String [] depts_fk = parmMap_fk.split(";"); //���ƿ���
		String [] depts_ck = parmMap_ck.split(";"); //���ƿ���
		String [] depts_jhsy = parmMap_jhsy.split(";"); //�ƻ���������
		
		for (int i = 0; i < admDateAll.size(); i++) {
			String [] valueD = admDateAll.get(i).split("/"); 

			//����
			for (int j = 0; j < depts.length; j++) {
//				System.out.println(depts[j]);
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "����");
				tableParm.addData("PROJECT", depts[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//����
			for (int j = 0; j < depts_jz.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "����");
				tableParm.addData("PROJECT", depts_jz[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//����
			for (int j = 0; j < depts_cr.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "����");
				tableParm.addData("PROJECT", depts_cr[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//����
			for (int j = 0; j < depts_fk.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "����");
				tableParm.addData("PROJECT", depts_fk[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//����
			for (int j = 0; j < depts_ck.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "����");
				tableParm.addData("PROJECT", depts_ck[j]);
				tableParm.addData("COUNT", "0");
			}
			
			//�ƻ�����
			for (int j = 0; j < depts_jhsy.length; j++) {
				tableParm.addData("YEAR", valueD[0]);
				tableParm.addData("MONTH", valueD[1]);
				tableParm.addData("DAY", valueD[2]);
				tableParm.addData("ADM_DATE", admDateAll.get(i));
				tableParm.addData("DEPT_CODE", "�ƻ�����");
				tableParm.addData("PROJECT", depts_jhsy[j]);
				tableParm.addData("COUNT", "0");
			}
			
		}

		for (int i = 0; i < tableParm.getCount("ADM_DATE"); i++) {
			
			//�������ݺϲ�
			for (int j = 0; j < regCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(regCount.getValue("ADM_DATE", j)) &&
						tableParm.getValue("PROJECT", i).equals(regCount.getValue("REALDEPT_CODE", j))){
					
					tableParm.setData("COUNT", i, regCount.getInt("COUNT", j));
			
				}
			}
			
			//������ݺϲ�  ����ȯ 02����� 01��Ա����ҩ 03 ,�ײ� �ͻ�04
			//����
			
			for (int j = 0; j < ekCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(ekCount.getValue("ADM_DATE", j)) &&
					"����".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = ekCount.getInt("CTZ1_CODE", j);
					String project = "";
					if( v == 2){
						project="TRQ_EK";
					}else if(v == 1){
						project="TJ_EK";
					}else if(v == 3){
						project="YGGY_EK";
					}

					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, ekCount.getInt("COUNT", j));
					}
				}
				
			}
	
			//����
			for (int j = 0; j < jzCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(jzCount.getValue("ADM_DATE", j)) &&
						"����".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = jzCount.getInt("CTZ1_CODE", j);
					String project = "";
				    if(v == 3){
						project="YGGY_JZ";
					}
				    if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, jzCount.getInt("COUNT", j));
					}
				}
			}
			
			//����
			for (int j = 0; j < crCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(crCount.getValue("ADM_DATE", j)) &&
						"����".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = crCount.getInt("CTZ1_CODE", j);
					String project = "";
				    if(v == 3){
						project = "YGGY_CR";
					}
				    if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, crCount.getInt("COUNT", j));
					}
				}
			}
			
			//����
			for (int j = 0; j < fkCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(fkCount.getValue("ADM_DATE", j)) &&
						"����".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = fkCount.getInt("CTZ1_CODE", j);
					String project = "";
					if( v == 2){
//						tableParm.setData("TRQ_FK", i, fkCount.getInt("COUNT", j));
						project = "TRQ_FK";
					}else if(v == 1){
//						tableParm.setData("TJ_FK", i, fkCount.getInt("COUNT", j));
						project = "TJ_FK";
					}else if(v == 3){
//						tableParm.setData("YGGY_FK", i, fkCount.getInt("COUNT", j));
						project = "YGGY_FK";
					}
					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, fkCount.getInt("COUNT", j));
					}
				}
			}
			
			//����
			for (int j = 0; j < ckCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(ckCount.getValue("ADM_DATE", j)) &&
						"����".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = ckCount.getInt("CTZ1_CODE", j);
					String project = "";
					if( v == 2){
						project = "TRQ_CK";
					}else if(v == 1){
						project = "TJ_CK";
					}else if(v == 3){
						project = "YGGY_CK";
					}else if(v == 4){
						project = "TCKH_CK";
					}
					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, ckCount.getInt("COUNT", j));
					}
				}
			}
			
			//�ƻ����� 
			for (int j = 0; j < jhsyCount.getCount("ADM_DATE"); j++) {
				if(tableParm.getValue("ADM_DATE", i).equals(jhsyCount.getValue("ADM_DATE", j)) &&
						"�ƻ�����".equals(tableParm.getValue("DEPT_CODE", i))){
					int v = jhsyCount.getInt("CTZ1_CODE", j);
					String project = "";
					if(v == 3){
						project = "YGGY_JHSY";
					}else if(v == 1){
						project = "TJ_JHSY";
					}else if(v == 2){
						project = "TRQ_JHSY";
					}
					if(project.equals(tableParm.getValue("PROJECT", i))){
						tableParm.setData("COUNT", i, jhsyCount.getInt("COUNT", j));
					}
				}
			}
	
		}
		
		onShowTableValue();
		
		if(tableParm.getCount("ADM_DATE") == 1){
			this.messageBox("û��Ҫ��ѯ������!");
			return;
		}
		
		table.setParmValue(tableParm);
		
	}
	
	public void onClear(){
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", StringTool.rollDate(today, -30));
		this.setValue("E_DATE", today);
		table.removeRowAll();
		tableParm = new TParm();

	}
	
	/**
	 * ���Excel
	 */
	public void onExecl() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "ÿ��������������ܱ�");
	}
	
	
	
}
