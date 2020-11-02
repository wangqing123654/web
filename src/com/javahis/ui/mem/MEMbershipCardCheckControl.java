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
 * <p>Title: ���ڻ�Ա��ѯ</p>
 *
 * <p>Description: ���ڻ�Ա��ѯ</p>
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
	 * ��ʼ�� 
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
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
	/**
	 * ����ʧЧ���ڲ�ѯ
	 */
	public void onQuery(){
		onEnter1();
	}
	/**
	 * ��ѯ���ڻس��¼�   
	 */
	public void onEnter1() {
//		System.out.println("---------onEnter����--------");
		//�õ��ڲ�ѯʱ���������ʱ��
		String endDateQuery = getValue("checkTime").toString().trim().replace("-", "").substring(0, 8)+"235959";
		String startDateQuery = getValue("localTime").toString().trim().replace("-", "").substring(0, 8)+"000000";
//		System.out.println(endDateQuery);
		//�ж������Ƿ�Ϊ���ַ���
//		if(endDateQuery.equals("") || endDateQuery == null){
//			this.messageBox("�������ѯʱ��!");
//			this.onClear();
//			return;
//		}
		//�����ݿ������Ϣ���õ���ǰʱ�䵽����ʱ��֮�����Ա��Ϣ
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
//				sexCode = "��";
//			}
//			if(sexCode.equals("2")){
//				sexCode = "Ů";
//			}
//			if(sexCode.equals("9")){
//				sexCode = "δ����";
//			}
//			if(sexCode.equals("0")){
//				sexCode = "δ֪";
//			}
//			result.setData("SEX_CODE", i, sexCode);
//		}
		getTable("TABLE").setParmValue(result);
	}
	/**
	 * ��շ���
	 */
	public void onClear() {
//		callFunction("UI|checkTime|setEnabled", true);//����ʱ������
//		this.clearValue("checkTime");//��������ÿ�
		table.removeRowAll();
		this.onInit();
	}
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		//�õ�TABLE�ϵ�����
		TParm tableParm = getTable("TABLE").getParmValue();
		//�ж����޴�ӡ����
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		TParm result = new TParm();
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("MR_NO", tableParm.getValue("MR_NO", i)); //��ֵ 
			result.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
//			String sexCode = tableParm.getValue("SEX_CODE",i);
//			if(sexCode.equals("1")){
//				sexCode = "��";
//			}
//			if(sexCode.equals("2")){
//				sexCode = "Ů";
//			}
//			if(sexCode.equals("9")){
//				sexCode = "δ˵��";
//			}
//			if(sexCode.equals("0")){
//				sexCode = "δ֪";
//			}
			result.addData("SEX_CODE", DictionaryTool.getInstance().getSexName(tableParm.getValue("SEX_CODE",i))); 
			String birthDate = tableParm.getValue("BIRTH_DATE",i);
			if(birthDate == null || birthDate.equals("")){
				birthDate = "��";
			}else{
				birthDate = birthDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("BIRTH_DATE", birthDate);
			result.addData("TEL_HOME", tableParm.getValue("TEL_HOME", i));
			result.addData("CELL_PHONE", tableParm.getValue("CELL_PHONE", i));
			String endDate = tableParm.getValue("END_DATE",i);
			if(endDate == null || endDate.equals("")){
				endDate = "��";
			}else{
				endDate = endDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("END_DATE", endDate); 
			String startDate = tableParm.getValue("START_DATE",i);
			if(startDate == null || startDate.equals("")){
				startDate = "��";
			}else{
				startDate = startDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("START_DATE", startDate);
			result.addData("GUARDIAN1_PHONE", tableParm.getValue("GUARDIAN1_PHONE", i));
		}
		result.setCount(tableParm.getCount("MR_NO")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "MR_NO");//����
		result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		result.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		result.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
//		result.addData("SYSTEM", "COLUMNS", "TEL_HOME");
//		result.addData("SYSTEM", "COLUMNS", "CELL_PHONE");
		result.addData("SYSTEM", "COLUMNS", "START_DATE");
		result.addData("SYSTEM", "COLUMNS", "END_DATE");
//		result.addData("SYSTEM", "COLUMNS", "GUARDIAN1_PHONE");
//		System.out.println("result=="+result);
		//�����ݷ���Ҫ��ӡ�ı�����
		TParm print = new TParm();
		print.setData("TABLE1", result.getData());
		print.setData("AUTHER", "TEXT", "�Ʊ���:"+Operator.getName());
		print.setData("CREATETIME", "TEXT","�Ʊ�����:"+SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0, 10));
//		System.out.println("print"+print);
		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMbershipCardManage.jhw", print);
	}
}
