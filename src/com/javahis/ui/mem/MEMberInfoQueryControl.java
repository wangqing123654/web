package com.javahis.ui.mem;
 
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: �߼����ڻ�Ա��ѯ</p>
 *
 * <p>Description: �߼����ڻ�Ա��ѯ</p>
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
     * ��ʼ��
     */
    public void onInit(){
    	//��ʼ����ѯʱ��
		 this.setValue("LOCAL_TIME",SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0,10)+ " 00:00:00");
		 this.setValue("CHECK_TIME",SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0,10)+ " 23:59:59");
		 
    }
    /**
     * ��ѯ����
     */
    public void onQuery(){
    	onEnter1();
    }
    /**
     * ����������ں�Ļس��¼�
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
		//�жϻ�Ա�����ͣ����Ϊ1-��ͨ�������Ϊ2-��è�������Ϊ3-���������Ϊ�ջ��߿��ַ���-�޿�
//		for(int i = 0 ; i<result.getCount();i++){
//			String memCode = result.getValue(("MEM_CODE"),i);
//			if(memCode != null && memCode != ""){
//				if(memCode.equals("1")){
//					memCode = "��ͨ��";
//				}
//				if(memCode.equals("2")){
//					memCode = "��è��";
//				}
//				if(memCode.equals("3")){
//					memCode = "����";
//				}
//			}else{
//				memCode = "�޿�";
//			}
//			result.setData("MEM_CODE", i, memCode);
//		}
		//�жϰ쿨��ʽ:���Ϊ1-�¿ͻ�;���Ϊ2-��ͨ�����;���Ϊ3-����������è;���Ϊ�ջ��߿��ַ���-û�а����Ա��
//		for(int i = 0;i<result.getCount();i++){
//			String reason = result.getValue(("REASON"),i);
//			if(reason != null && reason != ""){
//				if(reason.equals("1")){
//					reason = "�¿ͻ�";
//				}
//				if(reason.equals("2")){
//					reason = "��ͨ�����";
//				}
//				if(reason.equals("3")){
//					reason = "����������è";
//				}
//			}
//			result.setData("REASON", i, reason);
//		}
		getTable("TABLE1").setParmValue(result);
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
	 * �����ѯ����е�һ��������TABLE��ʾ��ϸ��Ϣ
	 */
	public void onCtzClick(){
//		System.out.println("-----------enter-------------");
		//�õ�ҳ��TABLE1��TABLE2���
		TTable table1 = getTable("TABLE1");
		TTable table2 = getTable("TABLE2");
		int i = table1.getSelectedRow();//��ѡ������
		TParm parm = table1.getParmValue();
		//��ȡ ��ѡ�����ĸ�������
		String memCode0 = parm.getValue("MEM_CODE", i);
//		String reason0 = parm.getValue("REASON", i);
//		String meeFee0 = parm.getValue("MEM_FEE", i);
		//��ȡ�����ʧЧ����
//		String endDateQuery = getValue("CHECK_TIME").toString().trim().replace("-", "").substring(0, 8);

//		System.out.println("memCode=="+meeCode+";reason=="+reason+";meeFee=="+meeFee+";endDateQuery=="+endDateQuery);
//		String sql = "SELECT a.mr_no, b.pat_name, b.sex_code, b.birth_date, a.start_date," +
//				" a.end_date, a.mem_code,c.INTRODUCER1,c.INTRODUCER2,c.INTRODUCER3 " +
//				"FROM mem_patinfo a, sys_patinfo b, " +
//				"(SELECT mr_no,INTRODUCER1,INTRODUCER2��INTRODUCER3 " +
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
				" (SELECT MR_NO, INTRODUCER1, INTRODUCER2��INTRODUCER3, MEM_CODE" +
				" FROM MEM_TRADE" +
				" WHERE STATUS = '1'" +
				" AND MEM_CODE = '"+memCode0+"'" +
				" AND END_DATE BETWEEN TO_DATE ('"+startDateQuery+"', 'YYYYMMDDHH24MISS')" +
				"  AND TO_DATE ('"+endDateQuery+"', 'YYYYMMDDHH24MISS')) C" +
				" WHERE A.MR_NO = B.MR_NO AND A.MR_NO = C.MR_NO";
//		System.out.println("------ִ��sql���------"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table2.setParmValue(result);
	}
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		//�õ�TABLE�ϵ�����
		TParm tableParm = getTable("TABLE2").getParmValue();
		TParm tableParm1 = getTable("TABLE2").getShowParmValue();
		//�ж����޴�ӡ����
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		TParm result = new TParm();
		for(int i=0;i<tableParm.getCount();i++){
			//MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;START_DATE;END_DATE;MEM_CODE;INTRODUCER
			result.addData("MR_NO", tableParm.getValue("MR_NO", i));
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
			String startDate = tableParm.getValue("START_DATE",i);
			if(startDate == null || startDate.equals("")){
				startDate = "��";
			}else{
				startDate = startDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("START_DATE", startDate);
			String endDate = tableParm.getValue("END_DATE",i);
			if(endDate == null || endDate.equals("")){
				endDate = "��";
			}else{
				endDate = endDate.trim().replace("-", "/").substring(0, 10);
			}
			result.addData("END_DATE", endDate); 
//			String memCode = tableParm.getValue("MEM_CODE", i);
//			if(memCode.equals("1")){
//				memCode = "��ͨ��";
//			}
//			if(memCode.equals("2")){
//				memCode = "��è��";
//			}
//			if(memCode.equals("3")){
//				memCode = "����";
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
		result.setCount(tableParm.getCount("MR_NO")) ;//���ñ��������
		result.addData("SYSTEM", "COLUMNS", "MR_NO");//����
		result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		result.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		result.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
		result.addData("SYSTEM", "COLUMNS", "START_DATE");
		result.addData("SYSTEM", "COLUMNS", "END_DATE");
		result.addData("SYSTEM", "COLUMNS", "MEM_CODE");
		result.addData("SYSTEM", "COLUMNS", "INTRODUCER");
//		System.out.println("result2=="+result);
		//�����ݷ���Ҫ��ӡ�ı�����   
		TParm print = new TParm();
		print.setData("TABLE", result.getData());
		print.setData("AUTHER", "TEXT", "�Ʊ���:"+Operator.getName());
		print.setData("CREATETIME", "TEXT","�Ʊ�����:"+SystemTool.getInstance().getDate().toString().replace("-", "/").trim().substring(0, 10));
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









