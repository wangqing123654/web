package com.javahis.ui.reg;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;

public class REGAdmForPregnantControl extends TControl{
	private TTable table;
	private String orderDate;//ԤԼʱ��
	private String mrNo;//������
	private String deptCode;

	
	public void onInit() {
		super.onInit();
		Timestamp date = SystemTool.getInstance().getDate();//��ǰʱ��
		//��ʼ����ѯ����
		this.setValue("ORDER_DATE", date.toString().substring(0, 10).replace('-', '/'));
//		this.setValue("START_DATE", StringTool.rollDate(date, 0).toString().substring(0, 10).replace('-', '/'));
		table=getTable("TABLE");
		TParm deptParm = new TParm(TJDODBTool.getInstance().select("SELECT DEPT_CODE FROM SYS_DEPT WHERE DEPT_CHN_DESC='����'"));
		deptCode= deptParm.getValue("DEPT_CODE", 0);
		this.setValue("DEPT_CODE", deptCode);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		getCondition();
		
	}
	
	/**
	 * ͨ�������Ų�ѯ
	 */
	public void onMrno(){
		setValue("MR_NO", PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO"))));
		getCondition();
		
	}
	
	/**
	 * ��ѯ����
	 */
	public void getCondition(){
		orderDate=this.getValueString("ORDER_DATE");
		mrNo=this.getValueString("MR_NO");
		deptCode = this.getValueString("DEPT_CODE");
		
		TParm parm = new TParm();
		parm.setData("admDate", orderDate);
		parm.setData("session", "");
		parm.setData("deptCode", deptCode);
		parm.setData("drCode", "");
		parm.setData("quegroup", "");		
		TParm result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
				"getOrder", parm);
//		System.out.println(result);
		String mrNoSql="";
		TParm orderParm = new TParm();
		for (int i = 0; i < result.getCount(); i++) {
			if(result.getInt("STATUS", i) == 1 && result.getValue("MR_NO", i).length() > 0){
				mrNoSql += "'"+result.getValue("MR_NO", i)+"',";
				orderParm.addData("MR_NO", result.getValue("MR_NO", i));
				orderParm.addData("ADM_DATE", result.getValue("ADM_DATE", i));
				orderParm.addData("START_TIME", result.getValue("START_TIME", i));
			}
		}
		if(mrNoSql.length() == 0){
			this.messageBox("û�в�ѯ�����ݣ�1");
			table.removeRowAll();
			return;
		}
		mrNoSql = mrNoSql.substring(0,mrNoSql.length()-1);
//		System.out.println("mrNoSql=="+mrNoSql);
		if(mrNo.length() > 0){
			mrNoSql="'"+mrNo+"'";
		}
//		System.out.println("mrNoSql=2="+mrNoSql);
		String patSql = "SELECT ROW_NUMBER() OVER (ORDER BY B.BOX_CODE) AS SEQ, B.BOX_CODE, A.MR_NO, A.PAT_NAME, A.BIRTH_DATE, '' AGE, A.CELL_PHONE," +
				" TO_CHAR(A.LMP_DATE,'YYYY/MM/DD') LMP_DATE, A.PREGNANT_DATE, TO_CHAR(A.FIRST_ADM_DATE,'YYYY/MM/DD') FIRST_ADM_DATE,TO_CHAR( B.OPT_DATE,'YYYY/MM/DD') AS MRO_DATE," +
				" '' ORDER_DATE, '' NEXT_ADM_DATE,TO_CHAR(A.RCNT_IPD_DATE,'YYYY/MM/DD') AS RCNT_IPD_DATE, '' PACK_FLG" +
				" FROM SYS_PATINFO A, MRO_MRV B" +
				" WHERE A.MR_NO = B.MR_NO(+) AND A.MR_NO IN("+mrNoSql+") " +
				" ORDER BY B.BOX_CODE";

		TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
		if(patParm.getCount() < 0){
			this.messageBox("û�в�ѯ�����ݣ�2");
			table.removeRowAll();
			return;
		}
		for (int i = 0; i < patParm.getCount(); i++) {
			//����
			patParm.setData("AGE", i, patAge(patParm.getTimestamp("BIRTH_DATE", i)));
		    //ԤԼʱ��
			String mrNO = patParm.getValue("MR_NO", i);
			for (int j = 0; j < orderParm.getCount("MR_NO"); j++) {
				if(mrNo.equals(orderParm.getValue("MR_NO", j))){
					patParm.setData("ORDER_DATE", i, orderParm.getValue("ADM_DATE", j).replace("-", "/"));
					orderParm.removeRow(j);
					break;
				}
			}
			//����ʱ��
			patParm.setData("NEXT_ADM_DATE", i ,getNextAdmDate(mrNO));
			//�Ƿ��ײ�
			patParm.setData("PACK_FLG", i ,getPackFlg(mrNO));
			
		}
//		System.out.println(patParm);
		table.setParmValue(patParm);
		
		
	}
	
	/**
	 * �õ�����ʱ�� 
	 * @param mr_no
	 * @return
	 */
	public String getNextAdmDate(String mr_no){
		String sql = "SELECT CASE_NO,TO_CHAR(ADM_DATE,'YYYY/MM/DD') ADM_DATE FROM SYS_EMR_INDEX WHERE MR_NO='"+mr_no+"' ORDER BY CASE_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String date = "";
		if(parm.getCount() > 1){
			date = parm.getValue("ADM_DATE", 1);
		}

		return date;
	}
	
	/**
	 * �Ƿ�����ײ�
	 * @param mr_no
	 * @return
	 */
	public String getPackFlg(String mr_no){
		String sql = "SELECT * FROM MEM_PAT_PACKAGE_SECTION WHERE MR_NO='"+mr_no+"' AND REST_TRADE_NO IS NULL";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String flg = "��";
		if(parm.getCount() > 0){
			flg = "��";
		}
		return flg;
	}
	
	/**
	    * ��������
	    * @param date
	    * @return
	    */
	   private String patAge(Timestamp date){
		   Timestamp sysDate = SystemTool.getInstance().getDate();
	       Timestamp temp = date == null ? sysDate : date;
	       String age = "0";
	       age = DateUtil.showAge(temp, sysDate);
	       return age;
	   }
	
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("ORDER_DATE;MR_NO");
		Timestamp date = SystemTool.getInstance().getDate();//��ǰʱ��
		this.setValue("ORDER_DATE", date.toString().substring(0, 10).replace('-', '/'));
		table.removeRowAll();
	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "���ƻ���ԤԼ��Ϣ");
	}
	
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		table.acceptText();
		TParm tableParm=table.getParmValue();
		TParm data=new TParm();
		TParm result=new TParm();
		if(tableParm.getCount()<=0){
			this.messageBox("û������");
			return;
		}
		//SEQ;BOX_CODE;MR_NO;PAT_NAME;AGE;CELL_PHONE;LMP_DATE;PREGNANT_DATE;FIRST_ADM_DATE;MRO_DATE;ORDER_DATE;NEXT_ADM_DATE;RCNT_IPD_DATE;PACK_FLG
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("SEQ", tableParm.getValue("SEQ",i));//���
			result.addData("MR_NO", tableParm.getValue("MR_NO",i));//������
			result.addData("PAT_NAME", tableParm.getValue("PAT_NAME",i));//����
			result.addData("PAT_AGE", tableParm.getValue("AGE",i));//����
			result.addData("TEL_NO", tableParm.getValue("CELL_PHONE",i));//�绰
			result.addData("LMP_DATE", tableParm.getValue("LMP_DATE",i));//ĩ���¾�
			result.addData("EDC_DATE", tableParm.getValue("PREGNANT_DATE",i));//Ԥ����
			result.addData("ARRIVE_DATE", tableParm.getValue("FIRST_ADM_DATE",i));//����ʱ��
			result.addData("EST_DATE", tableParm.getValue("MRO_DATE",i));//����ʱ��
			result.addData("APP_DATE", tableParm.getValue("ORDER_DATE",i));//ԤԼʱ��
			result.addData("REVISIT_DATE", tableParm.getValue("NEXT_ADM_DATE",i));//����ʱ��
			result.addData("ODI_DATE", tableParm.getValue("RCNT_IPD_DATE",i));//סԺʱ��
			result.addData("PACKAGE", tableParm.getValue("PACK_FLG",i));//�ײ�
		}
		result.setCount(result.getCount("SEQ"));
		result.addData("SYSTEM", "COLUMNS","SEQ");
		result.addData("SYSTEM", "COLUMNS","MR_NO");
		result.addData("SYSTEM", "COLUMNS","PAT_NAME");
		result.addData("SYSTEM", "COLUMNS","PAT_AGE");
		result.addData("SYSTEM", "COLUMNS","TEL_NO");
		result.addData("SYSTEM", "COLUMNS","LMP_DATE");
		result.addData("SYSTEM", "COLUMNS","EDC_DATE");
 		result.addData("SYSTEM", "COLUMNS","ARRIVE_DATE");
		result.addData("SYSTEM", "COLUMNS","EST_DATE");
		result.addData("SYSTEM", "COLUMNS","APP_DATE");
		result.addData("SYSTEM", "COLUMNS","REVISIT_DATE");
		result.addData("SYSTEM", "COLUMNS","ODI_DATE");
		result.addData("SYSTEM", "COLUMNS","PACKAGE");
		data.setData("TITLE1","TEXT", Operator.getHospitalCHNShortName());
		data.setData("TITLE2","TEXT", "���ƻ���ԤԼ����");
		data.setData("ORDER_DATE","TEXT", "ԤԼʱ�䣺"+orderDate.replace("-", "/").substring(0, 10));
		data.setData("TABLE",result.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGAdmForPregnant.jhw",data,true);
		
	}
	/**
	 * ���Table
	 * @param tagName
	 * @return
	 */
	public TTable getTable(String tagName){
		return (TTable)getComponent(tagName);
	}
}
