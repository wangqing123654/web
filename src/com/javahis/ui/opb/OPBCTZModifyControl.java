package com.javahis.ui.opb;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdiUtil;
/**
 * <p>Title: ��������޸�</p>
 *
 * <p>Description: ��������޸�</p>
 *
 * <p>Copyright: Copyright (c) Bluecore</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author caowl 20140213
 * @version 1.0
 */
public class OPBCTZModifyControl extends TControl {

	private static final String actionName = "action.opb.OPBCTZAction";

	String caseNo;
	/**
	 * ��ʼ��
	 * */
	public void onInit() {
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if(obj == null || obj.equals("")){
			return ;
		}
		if (obj != null || obj != "") {	
			
			initParm = (TParm) obj;
			caseNo = initParm.getData("CASE_NO").toString();					
			TParm queryParm = queryByCaseNo(caseNo);
			if (queryParm.getCount() < 0) {
				// ��������
				this.messageBox("E0008");	
				this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE");
				TTable table = (TTable) this.getComponent("Table");
		        table.removeRowAll();
		        return;
			}
			// ��ʼ��
			this.setValue("MR_NO", queryParm.getData("MR_NO", 0));
			this.setValue("PAT_NAME", queryParm.getData("PAT_NAME", 0));
			this.setValue("IDNO", queryParm.getData("IDNO", 0));
			this.setValue("SEX_CODE", queryParm.getData("SEX_CODE", 0));
			this.setValue("CTZ1_CODE", queryParm.getData("CTZ1_CODE", 0));
			this.setValue("CTZ2_CODE", queryParm.getData("CTZ2_CODE", 0));
			this.setValue("CTZ3_CODE", queryParm.getData("CTZ3_CODE", 0));
			this.setValue("CASE_NO", caseNo);

			
			// ��ɫ��ʾ		
			callFunction("UI|PAT_NAME|setEnabled", false);
			callFunction("UI|IDNO|setEnabled", false);
			callFunction("UI|SEX_CODE|setEnabled", false);
			
			String sql = "SELECT A.MR_NO,B.PAT_NAME,A.CASE_NO,A.CTZ_CODE1_O,A.CTZ_CODE2_O,A.CTZ_CODE3_O,A.CTZ_CODE1_N,A.CTZ_CODE2_N,A.CTZ_CODE3_N,A.OPT_USER,A.OPT_DATE,A.OPT_TERM"+
	                     " FROM OPB_CTZ_LOG A,SYS_PATINFO B "+
	                     " WHERE A.MR_NO=B.MR_NO "+
	                     " AND A.MR_NO = '"+queryParm.getData("MR_NO", 0)+"'" +
	                     " AND A.CASE_NO = '"+caseNo+"'";
			
			TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
			this.setValue("CTZ_CODE1_O", tableParm.getData("CTZ_CODE1_O", 0));
			this.setValue("CTZ_CODE2_O", queryParm.getData("CTZ_CODE2_O", 0));
			this.setValue("CTZ_CODE3_O", tableParm.getData("CTZ_CODE3_O", 0));
			this.setValue("CTZ_CODE1_N", tableParm.getData("CTZ_CODE1_N", 0));
			this.setValue("CTZ_CODE2_N", queryParm.getData("CTZ_CODE2_N", 0));
			this.setValue("CTZ_CODE3_N", tableParm.getData("CTZ_CODE3_N", 0));
			
			this.callFunction("UI|Table|setParmValue", tableParm);
		}
	}

	/**
	 * ����Case_no��ѯ������Ϣ
	 * */
	public TParm queryByCaseNo(String case_no) {

		TParm selParm = new TParm();
		String sql = "SELECT A.MR_NO ,A.IPD_NO,A.PAT_NAME,A.IDNO,A.CONTACTS_TEL,A.BIRTH_DATE,B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,A.SEX_CODE ,B.CASE_NO,B.OPT_DATE,B.OPT_USER,B.OPT_TERM "				
				+ " FROM SYS_PATINFO A, REG_PATADM B "
				+ " WHERE A.MR_NO = B.MR_NO "
				+ " AND B.CASE_NO = '" + case_no + "'" ;
				//  " AND B.SEE_DR_FLG != 'N' ";
		//System.out.println("���ݾ���Ų�ѯ"+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));

		return selParm;

	}
	
	
	/**
	 * ����MR_NO��ѯ������Ϣ
	 * */
	public void onQuery() {
		
		TParm selParm = queryByMrno();
		if (selParm.getCount() < 0) {
			// ��������
			this.messageBox("E0008");	
			this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE");
			TTable table = (TTable) this.getComponent("Table");
	        table.removeRowAll();
	        return;
		}
		caseNo = selParm.getData("CASE_NO",0).toString();
		this.setValue("MR_NO", selParm.getData("MR_NO", 0));		
		this.setValue("PAT_NAME", selParm.getData("PAT_NAME", 0));
		this.setValue("IDNO", selParm.getData("IDNO", 0));
		this.setValue("SEX_CODE", selParm.getValue("SEX_CODE", 0));
		this.setValue("CTZ1_CODE", selParm.getData("CTZ1_CODE", 0));
		this.setValue("CTZ2_CODE", selParm.getData("CTZ2_CODE", 0));
		this.setValue("CTZ3_CODE", selParm.getData("CTZ3_CODE", 0));
		
		this.setValue("CASE_NO", selParm.getData("CASE_NO",0));
		// ��ɫ��ʾ		
		callFunction("UI|PAT_NAME|setEnabled", false);
		callFunction("UI|IDNO|setEnabled", false);
		callFunction("UI|SEX_CODE|setEnabled", false);
		
		String sql = "SELECT A.MR_NO,B.PAT_NAME,A.CASE_NO,A.CTZ_CODE1_O,A.CTZ_CODE2_O,A.CTZ_CODE3_O,A.CTZ_CODE1_N,A.CTZ_CODE2_N,A.CTZ_CODE3_N,A.OPT_USER,A.OPT_DATE,A.OPT_TERM"+
                     " FROM OPB_CTZ_LOG A,SYS_PATINFO B "+
                     " WHERE A.MR_NO=B.MR_NO "+
                     " AND A.MR_NO = '"+selParm.getData("MR_NO", 0)+"'" +
                     " AND A.CASE_NO = '"+caseNo+"'";
		
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("CTZ_CODE1_O", tableParm.getData("CTZ_CODE1_O", 0));
		this.setValue("CTZ_CODE2_O", selParm.getData("CTZ_CODE2_O", 0));
		this.setValue("CTZ_CODE3_O", tableParm.getData("CTZ_CODE3_O", 0));
		this.setValue("CTZ_CODE1_N", tableParm.getData("CTZ_CODE1_N", 0));
		this.setValue("CTZ_CODE2_N", selParm.getData("CTZ_CODE2_N", 0));
		this.setValue("CTZ_CODE3_N", tableParm.getData("CTZ_CODE3_N", 0));		
		this.callFunction("UI|Table|setParmValue", tableParm);
		
		
	}
	
	/**
	 * ����MR_NO��ѯ������Ϣ
	 * */
	public TParm queryByMrno() {
		
		String mr_no =PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		TParm selParm = new TParm();
		String sql = "SELECT A.MR_NO ,A.BIRTH_DATE,A.IPD_NO,A.PAT_NAME,A.IDNO,A.CONTACTS_TEL," +
				" A.BIRTH_DATE,B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,A.SEX_CODE ," +
				" B.CASE_NO,B.OPT_DATE,B.OPT_USER,B.OPT_TERM " +
				" FROM REG_PATADM B, SYS_PATINFO A" +				
				" WHERE  A.MR_NO = '"+mr_no+"' " +
				" AND A.MR_NO = B.MR_NO  " ;
			//	" AND B.SEE_DR_FLG != 'N' " ;
		//System.out.println("��ѯ������Ϣ---��"+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));		
		if(selParm.getCount()>1){
			//System.out.println("����ɸѡ");
			TParm parm = new TParm();
			parm.setData("MR_NO", mr_no);
			parm.setData("PAT_NAME", selParm.getValue("PAT_NAME",0));
			parm.setData("SEX_CODE", selParm.getValue("SEX_CODE",0));
			Timestamp sysDate = SystemTool.getInstance().getDate();				
			String birthDate = selParm.getData("BIRTH_DATE",0).toString().substring(0,19).replace("-", "/");
			Timestamp birth_date = new Timestamp(Date.parse(birthDate));			 			 
			Timestamp temp = birth_date == null ? sysDate: birth_date;						
			String age = "0";
			if (birth_date != null)
				age = OdiUtil.getInstance().showAge(temp,sysDate);			
			else
				age = "";
			//selParm.addData("AGE", age);
			//��������  end
			parm.setData("AGE", age);
			
			TParm parmResult = new TParm();
			parmResult.setData("PARM",parm.getData());
			//System.out.println(parm);					
			// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
			parm.setData("count", "0");
			String caseNo = (String) openDialog(
					"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
			if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
				//System.out.println("��������");
				return selParm;
			}
			//System.out.println("ɸѡ���");
			selParm = queryByCaseNo(caseNo);			
		}
		
		return selParm;
		
	}
	

	/**
	 * ����
	 * */
	public void onSave() {
		
		// ��ҳ���ȡ�����Ϣ
		String CTZ1_CODE = (String) this.getValue("CTZ1_CODE");
		String case_no = (String)this.getValue("CASE_NO");
		//����ݲ���Ϊ��
		if(CTZ1_CODE == null || CTZ1_CODE.equals("")){
			this.messageBox("���һ����Ϊ�գ�");
			return;
		}
		
		String CTZ2_CODE = (String) this.getValue("CTZ2_CODE");
		String CTZ3_CODE = (String) this.getValue("CTZ3_CODE");

		// ���ò�����Ϣ
		TParm parm = new TParm();

		//TParm selParm = queryByMrno();
		if (case_no==null || case_no.equals("")) {
			// ��������
			this.messageBox("E0008");	
			this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;CTZ11_CODE;CTZ22_CODE;CTZ33_CODE");
			TTable table = (TTable) this.getComponent("Table");
	        table.removeRowAll();
		}else{
			parm.setData("CASE_NO", case_no);
			parm.setData("CTZ1_CODE", CTZ1_CODE);
			parm.setData("CTZ2_CODE", CTZ2_CODE);
			parm.setData("CTZ3_CODE", CTZ3_CODE);
			parm.setData("CTZ11_CODE", CTZ1_CODE);
			parm.setData("CTZ22_CODE", CTZ2_CODE);
			parm.setData("CTZ33_CODE", CTZ3_CODE);
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			// ����action�еķ���
			TParm result = TIOM_AppServer
					.executeAction(actionName, "updBill", parm);
			if (result.getErrCode() == 0) {
				messageBox("P0005");
			} else {
				messageBox("E0005");
			}
		}
		

	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;CASE_NO");
		TTable table = (TTable) this.getComponent("Table");
        table.removeRowAll();
	}
	
	/**
	 * �ж����1�Ƿ����
	 * @throws ParseException
	 */
	public void getCtzValid1() throws ParseException{	
		String ctz=this.getValueString("CTZ1_CODE");
		String sql = "SELECT START_DATE,END_DATE FROM SYS_CTZ WHERE CTZ_CODE = '"+ctz+"' AND USE_FLG='Y'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
			String endDate = parm.getValue("END_DATE", 0).replace("-", "/").substring(0,10);
			String today = StringTool.getTimestamp(new Date()).toString().replace("-", "/").substring(0, 10);
			if(sdf.parse(endDate).before(sdf.parse(today))){
				this.messageBox("������ѹ��ڣ�������ѡ��");
				setValue("CTZ1_CODE", "99");
			}	
		}
	}
	/**
	 * �ж����2�Ƿ����
	 * @throws ParseException
	 */
	public void getCtzValid2() throws ParseException{	
		String ctz=this.getValueString("CTZ2_CODE");
		String sql = "SELECT START_DATE,END_DATE FROM SYS_CTZ WHERE CTZ_CODE = '"+ctz+"' AND USE_FLG='Y'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
			String endDate = parm.getValue("END_DATE", 0).replace("-", "/").substring(0,10);
			String today = StringTool.getTimestamp(new Date()).toString().replace("-", "/").substring(0, 10);
			if(sdf.parse(endDate).before(sdf.parse(today))){
				this.messageBox("������ѹ��ڣ�������ѡ��");
				setValue("CTZ2_CODE", "");
			}	
		}
	}
	/**
	 * �ж����3�Ƿ����
	 * @throws ParseException
	 */
	public void getCtzValid3() throws ParseException{	
		String ctz=this.getValueString("CTZ3_CODE");
		String sql = "SELECT START_DATE,END_DATE FROM SYS_CTZ WHERE CTZ_CODE = '"+ctz+"' AND USE_FLG='Y'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
			String endDate = parm.getValue("END_DATE", 0).replace("-", "/").substring(0,10);
			String today = StringTool.getTimestamp(new Date()).toString().replace("-", "/").substring(0, 10);
			if(sdf.parse(endDate).before(sdf.parse(today))){
				this.messageBox("������ѹ��ڣ�������ѡ��");
				setValue("CTZ3_CODE", "");
			}	
		}
	}

}
