package com.javahis.ui.adm;


import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.ibs.IBSLumpWorkBatchTool;
import jdo.ibs.IBSTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.system.root.RootClientListener;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: �޸����
 * </p>
 * 
 * <p>
 * Description: �޸����
 * 
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
 * @author caowl 2012.07.04
 * @version 1.0
 */
public class ADMCTZModifyControl extends TControl {

	
	private static final String actionName = "action.ibs.IBSAction";

	String caseNo;
	Pat pat;
	//���Ӿ����ɸѡ����flg
	boolean caseNoFlg = true;
	private String ctz1="";//===pangben 2015-10-29 ������
	private String ctz2="";//===pangben 2015-10-29 ������
	private String ctz3="";//===pangben 2015-10-29 ������
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
			// ��ʼ��
			this.setValue("CASE_NO", caseNo);
			this.setValue("MR_NO", queryParm.getData("MR_NO", 0));
			this.setValue("PAT_NAME", queryParm.getData("PAT_NAME", 0));
			this.setValue("IDNO", queryParm.getData("IDNO", 0));
			this.setValue("SEX_CODE", queryParm.getData("SEX_CODE", 0));
			this.setValue("CTZ1_CODE", queryParm.getData("CTZ1_CODE", 0));
			this.setValue("CTZ2_CODE", queryParm.getData("CTZ2_CODE", 0));
			this.setValue("CTZ3_CODE", queryParm.getData("CTZ3_CODE", 0));

			// ��ɫ��ʾ		
			callFunction("UI|PAT_NAME|setEnabled", false);
			callFunction("UI|IDNO|setEnabled", false);
			callFunction("UI|SEX_CODE|setEnabled", false);
			
			onQuery();
		}
	}

	/**
	 * ����Case_no��ѯ������Ϣ
	 * */
	public TParm queryByCaseNo(String case_no) {

		TParm selParm = new TParm();
		String sql = "SELECT A.MR_NO, A.PAT_NAME, A.IDNO,  A.SEX_CODE, B.CTZ1_CODE"
				+ ", B.CTZ2_CODE, B.CTZ3_CODE "
				+ "FROM SYS_PATINFO A, ADM_INP B "
				+ "WHERE A.MR_NO = B.MR_NO "
				+ "AND B.CASE_NO = '" + case_no + "'";

		selParm = new TParm(TJDODBTool.getInstance().select(sql));

		return selParm;

	}
	
	
	/**
	 * ����MR_NO��ѯ������Ϣ
	 * */
	public void onQuery() {
		
		// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 start
		//���Ӿ����ɸѡ����
		if(caseNoFlg){
			TParm inparm = new TParm();
			pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
			String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
						SystemTool.getInstance().getDate());
			inparm.setData("MR_NO", pat.getMrNo());
			inparm.setData("PAT_NAME", pat.getName());
			inparm.setData("SEX_CODE", pat.getSexCode());
			inparm.setData("AGE", age);
			// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
			inparm.setData("count", "0");
			TParm caseNoParm = (TParm) openDialog(
					"%ROOT%\\config\\bil\\BILChooseVisit.x", inparm);
			caseNo = caseNoParm.getValue("CASE_NO");
			String mrNo = caseNoParm.getValue("MR_NO");
			String ipdNo = caseNoParm.getValue("IPD_NO");
			String sql = "SELECT APPROVE_FLG, MR_NO, IPD_NO, BILL_NO,CASE_NO "
					+ " FROM IBS_BILLM " 
					+ " WHERE   RECEIPT_NO IS NOT NULL "
					+ " AND (REFUND_FLG IS NULL OR REFUND_FLG = 'N') "
					+ " AND MR_NO='" + mrNo + "' " 
					+ " AND IPD_NO='" + ipdNo+ "' " 
					+ " AND CASE_NO='" + caseNo + "'";
			TParm check = new TParm(TJDODBTool.getInstance().select(sql));
			if(check.getCount()>0){
				this.messageBox("���˵��Ѵ�Ʊ,�����޸����");
				TTable table = (TTable) this.getComponent("Table");
				TParm parm = new TParm();
				table.setParmValue(parm);
				return;
			}
		}
		// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 end
		
		TParm selParm = queryByMrno();
		if (null!=selParm.getValue("MESSAGE")
				&&selParm.getValue("MESSAGE").length()>0) {
			// ��������
			this.messageBox(selParm.getValue("MESSAGE"));	
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
		this.setValue("CTZ11_CODE", selParm.getData("CTZ1_CODE", 0));
		this.setValue("CTZ22_CODE", selParm.getData("CTZ2_CODE", 0));
		this.setValue("CTZ33_CODE", selParm.getData("CTZ3_CODE", 0));
		ctz1=selParm.getValue("CTZ1_CODE", 0);//===pangben 2015-10-29 ������У��
		ctz2=null!=selParm.getData("CTZ2_CODE", 0)?selParm.getValue("CTZ2_CODE", 0):"";
		ctz3=null!=selParm.getData("CTZ3_CODE", 0)?selParm.getValue("CTZ3_CODE", 0):"";
		this.setValue("CASE_NO", selParm.getData("CASE_NO", 0));
		// ��ɫ��ʾ		
		callFunction("UI|PAT_NAME|setEnabled", false);
		callFunction("UI|IDNO|setEnabled", false);
		callFunction("UI|SEX_CODE|setEnabled", false);
		
		
		String sql = "SELECT A.MR_NO,B.PAT_NAME,A.CASE_NO,A.CTZ_CODE1_O,A.CTZ_CODE2_O,A.CTZ_CODE3_O,A.CTZ_CODE1_N,A.CTZ_CODE2_N,A.CTZ_CODE3_N,A.OPT_USER,A.OPT_DATE,A.OPT_TERM"+
        " FROM 	ADM_CTZ_LOG A,SYS_PATINFO B "+
        " WHERE A.MR_NO=B.MR_NO "+
        " AND A.MR_NO = '"+selParm.getData("MR_NO", 0)+"'" +
        " AND A.CASE_NO = '"+caseNo+"'";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			//this.messageBox("��������");
			return;
		}
		this.setValue("CTZ_CODE1_O", parm.getData("CTZ_CODE1_O",0));
		this.setValue("CTZ_CODE2_O", parm.getData("CTZ_CODE2_O",0));
		this.setValue("CTZ_CODE3_O", parm.getData("CTZ_CODE3_O",0));
		this.setValue("CTZ_CODE1_N", parm.getData("CTZ_CODE1_N",0));
		this.setValue("CTZ_CODE2_N", parm.getData("CTZ_CODE2_N",0));
		this.setValue("CTZ_CODE3_N", parm.getData("CTZ_CODE3_N",0));
		this.callFunction("UI|Table|setParmValue", parm);						
		//this.callFunction("UI|Table|setParmValue", selParm);
		
		// �ж��Ƿ����
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox(
					"�Ƿ���� Whether to unlock",
					PatTool.getInstance().getLockParmString(
							this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(
						this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ADM");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
					"ADM");
		}
	}
	
	/**
	 * ����MR_NO��ѯ������Ϣ
	 * */
	public TParm queryByMrno() {
		
		String mr_no =PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		TParm selParm = new TParm();
		String sql="SELECT MR_NO FROM ADM_INP WHERE MR_NO='"+mr_no+"' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount()<=0) {
			selParm=new TParm();
			selParm.setData("MESSAGE","E0008");
			return selParm;
		}

		sql = "SELECT A.MR_NO ,A.IPD_NO,A.PAT_NAME,A.IDNO,A.CONTACTS_TEL,A.BIRTH_DATE," +
				"B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,A.SEX_CODE ,B.CASE_NO,B.OPT_DATE,B.OPT_USER," +
				"B.OPT_TERM,B.NEW_BORN_FLG,B.M_CASE_NO,B.LUMPWORK_CODE " +
				" FROM ADM_INP B, SYS_PATINFO A" +				
				" WHERE  A.MR_NO = '"+mr_no+"' " +
				" AND  B.CASE_NO = '"+caseNo+"' " +
				" AND A.MR_NO = B.MR_NO  " +
//				" AND B.DS_DATE IS NULL " +
				" AND B.IN_DATE IS NOT NULL " +
				" AND B.CANCEL_FLG <> 'Y' ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
//		if (selParm.getCount()<=0) {
//			selParm=new TParm();
//			selParm.setData("MESSAGE","�Ѱ����Ժ,�����Բ���");
//			return selParm;
//		}
		return selParm;
	}
	
	/**
	 * ��Ժ����У��
	 * add by xiongwg20150325
	 * */
	public TParm onCheckDS() {		
//		String mr_no = PatTool.getInstance().checkMrno(
//				TypeTool.getString(getValue("MR_NO")));
		String sql = "SELECT A.MR_NO ,A.IPD_NO,A.PAT_NAME,A.IDNO,A.CONTACTS_TEL,A.BIRTH_DATE,"
				+ "B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,A.SEX_CODE ,B.CASE_NO,B.OPT_DATE,"
				+ "B.OPT_USER,B.OPT_TERM "
				+ " FROM ADM_INP B, SYS_PATINFO A"
				+ " WHERE  B.CASE_NO = '" + caseNo + "' "
				+ " AND A.MR_NO = B.MR_NO  "
				+ " AND B.DS_DATE IS NOT NULL "
				+ " AND B.IN_DATE IS NOT NULL " 
				+ " AND B.CANCEL_FLG <> 'Y' ";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
	}

	/**
	 * ����
	 * */
	public void onSave() {
		
		// ��ҳ���ȡ�����Ϣ
		String CTZ1_CODE = (String) this.getValue("CTZ1_CODE");
		
		//����ݲ���Ϊ��
		if(CTZ1_CODE == null || CTZ1_CODE.equals("")){
			this.messageBox("���һ����Ϊ�գ�");
			return;
		}
		
		String CTZ2_CODE = (String) this.getValue("CTZ2_CODE");
		String CTZ3_CODE = (String) this.getValue("CTZ3_CODE");

		// ���ò�����Ϣ
		TParm parm = new TParm();

		TParm selParm = queryByMrno();
		
		if (selParm.getCount() < 0) {
			// ��������
			this.messageBox("E0008");	
			this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;CTZ11_CODE;CTZ22_CODE;CTZ33_CODE");
			TTable table = (TTable) this.getComponent("Table");
	        table.removeRowAll();
		}else{
			if (selParm.getValue("CTZ1_CODE",0).equals(CTZ1_CODE)&&
					selParm.getValue("CTZ2_CODE",0).equals(CTZ2_CODE)&&
					selParm.getValue("CTZ3_CODE",0).equals(CTZ3_CODE)) {
				this.messageBox("�˲���û���޸����");
				return;
			}
			parm.setData("CASE_NO", selParm.getData("CASE_NO",0).toString());
			parm.setData("CTZ1_CODE", CTZ1_CODE);
			parm.setData("CTZ2_CODE", CTZ2_CODE);
			parm.setData("CTZ3_CODE", CTZ3_CODE);
			parm.setData("CTZ11_CODE", CTZ1_CODE);
			parm.setData("CTZ22_CODE", CTZ2_CODE);
			parm.setData("CTZ33_CODE", CTZ3_CODE);
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			//String lumpCaseNo="";
			TParm result=null;
			TParm checkLumpWorkParm=null;
			if (selParm.getValue("NEW_BORN_FLG",0).equals("Y")
					&&selParm.getValue("M_CASE_NO",0).length()>0) {//pangben 2015-7-28�������������޸����
				//lumpCaseNo=selParm.getValue("M_CASE_NO",0);
				if (selParm.getValue("LUMPWORK_CODE",0).length()>0) {//�ײͲ�����Ӥ�������Բ����޸����
					this.messageBox("�ײͲ����������������޸����");
					return;
				}
			}else{
				//=====pangben 2015-6-19 �޸�
				checkLumpWorkParm=ADMInpTool.getInstance().onCheckLumpwork(selParm.getData("CASE_NO",0).toString());
				if (checkLumpWorkParm.getCount()>0) {//�ײͻ�����ҪУ��Ƿ��ѽ���Ժ
					parm.setData("CHECK_LUMPWORK_FLG","Y");//�ײͲ���
					if (null!=checkLumpWorkParm.getData("DS_DATE",0)&&checkLumpWorkParm.getValue("DS_DATE",0).length()>0) {
						messageBox("�����ײͲ�������סԺ�ڼ�����޸����");
				        return;
					}
					String packSql="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO = '"+selParm.getData("CASE_NO",0).toString()+"' AND INCLUDE_EXEC_FLG='N'";
					TParm selIbsParm = new TParm(TJDODBTool.getInstance().select(packSql));
					if(selIbsParm.getCount()>0){
						messageBox("����ִ���ײ�����");
				        return;
					}
					packSql="SELECT CASE_NO FROM IBS_ORDD WHERE CASE_NO IN (SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO = '"+selParm.getData("CASE_NO",0).toString()+"') AND INCLUDE_EXEC_FLG='N'";
					selIbsParm = new TParm(TJDODBTool.getInstance().select(packSql));
					if(selIbsParm.getCount()>0){
						messageBox("����ִ���ײ�����");
				        return;
					}
				}
			}
			// ����action�еķ���
			result = TIOM_AppServer
					.executeAction(actionName, "updBill", parm);
			if (result.getErrCode() == 0) {
				// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 start
				if (onCheckDS().getCount()>0) {//��Ժ����
					onBill();				
				}else{
//					if (null!=caseNo&&caseNo.length()>0) {//�ײͲ������β���====pangben 2015-7-28
//						if (null!=checkLumpWorkParm&&checkLumpWorkParm.getCount()>0) {
//							parm=new TParm();
//							parm.setData("CASE_NO",caseNo);
//							result = TIOM_AppServer.executeAction(actionName, "onExeIbsLumpWorkBatch", parm);
//							if (result.getErrCode()<0) {
//								this.messageBox("E0005");
//								return ;
//							}else{
//								this.messageBox("�ײ�������ִ��");
//							}
//						}
//					}
					
				}
				
				//pangben 2018-8-10 ���ĸ��δ��Ժ Ӥ����Ժ ��Ӥ���޸���ݺ������˵�
				String admNewSql="SELECT CASE_NO,DS_DATE,IPD_NO,IN_DATE,MR_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,DEPT_CODE,STATION_CODE,REGION_CODE,BED_NO FROM ADM_INP WHERE M_CASE_NO = '"+
				selParm.getData("CASE_NO",0).toString()+"' AND CANCEL_FLG <> 'Y'";
				TParm admNewParm = new TParm(TJDODBTool.getInstance().select(admNewSql));
				TParm exeNewBadyParm = new TParm();
				int index = 0;
				if(admNewParm.getCount()>0){
					for(int i= 0; i< admNewParm.getCount(); i++){
						if(null!= admNewParm.getData("DS_DATE",i) && admNewParm.getValue("DS_DATE",i).length()>0){
							//�Ѿ���Ժ �����˵�
							exeNewBadyParm.setRowData(index,admNewParm,i);
							index++;
						}
					}
				}
				if(exeNewBadyParm.getCount("CASE_NO")>0){
					exeNewBadyParm.setData("OPT_USER", Operator.getID());
					exeNewBadyParm.setData("OPT_TERM", Operator.getIP());
					TParm resultBadyParm = TIOM_AppServer.executeAction(actionName, "onNewBadyBill", exeNewBadyParm);
					if(resultBadyParm.getErrCode()<0){
						this.messageBox("����Ӥ���˵�ʧ��");
						return;
					}
				}
				messageBox("Ӥ���˵�ִ�гɹ�");
				// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 end
			} else {
				messageBox("E0005");
			}
		}
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
		caseNoFlg = false;
		onQuery();
		caseNoFlg = true;
	}

	/**
	 * �������˵�
	 * */
	 public void onBill(){
		// �õ�ϵͳʱ��
			Timestamp sysDate = SystemTool.getInstance().getDate();
		    TParm orderParm = new TParm();
	        orderParm.setData("BILL_DATE", sysDate);
	        orderParm.setData("OPT_USER",Operator.getID());
	        orderParm.setData("OPT_TERM",Operator.getIP());
	        orderParm.setData("FLG", "N"); //��Ժ��Y  ת����N
	        // ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 start
			if (onCheckDS().getCount()>0) {//��Ժ����
				 orderParm.setData("FLG", "Y"); //��Ժ��Y  ת����N
			}
			// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 end
	        orderParm.addData("CASE_NO",this.getValue("CASE_NO"));
	        orderParm.setData("TYPE", "ADM");
	        orderParm.setCount(1);
//	        System.out.println("orderParm:"+orderParm);
	     // ����action�еķ���
			TParm result = TIOM_AppServer
					.executeAction(actionName, "onBill", orderParm);
			if (result.getErrCode() == 0) {
				// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 start
				if (onCheckDS().getCount()>0) {//��Ժ����
					messageBox("��Ժ����,�޸���ݲ������˵��ɹ�");
				}else{
					messageBox("P0005");
				}
				// ��Ժ�����޸�����ݱ��������˵�--xiongwg20150325 end
			} else {
				messageBox("E0005");
			}
	}

	/**
	 * ��������
	 */
	public void unLockPat() {
		// �ж��Ƿ����
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
		}
		pat = null;
	}

	/**
	 * �ر��¼� =============pangben 2014-7-11
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		unLockPat();
		return true;
	}

	/**
	 * ���
	 */
	public void onClear() {
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
		this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;CASE_NO");
		TTable table = (TTable) this.getComponent("Table");
        table.removeRowAll();
        ctz2="";ctz1="";ctz3="";
	}
	/**
	 * 
	* @Title: onExeIncludeBatch
	* @Description: TODO(�ײ��޸����ִ������)
	* @author pangben 2015-7-15
	* @throws
	 */
	public void onExeIncludeBatch(){
		String mr_no =PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		if (mr_no.length()<=0) {
			this.messageBox("��ѡ�񲡻�");
			return ;
		}
		TParm selParm = new TParm();
		String sql="SELECT MR_NO FROM ADM_INP WHERE CASE_NO='"+caseNo+"' AND  MR_NO='"+mr_no+"' AND IN_DATE IS NOT NULL AND CANCEL_FLG <> 'Y' AND LUMPWORK_CODE IS NOT NULL ";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount()<=0) {
			this.messageBox("�˲��������ײͻ���,�����Բ���");
			return ;
		}
		if (null!=caseNo&&caseNo.length()>0) {
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			TParm result = TIOM_AppServer
			.executeAction(actionName, "onExeIbsLumpWorkBatch", parm);
			if (result.getErrCode()<0) {
				this.messageBox("����ʧ��,"+result.getErrText());
				return ;
			}
			if (null!=result.getValue("MESSAGE")&&result.getValue("MESSAGE").length()>0) {
	    		this.messageBox(result.getValue("MESSAGE"));
	    		return;
			}
			this.messageBox("P0005");
		}else{
			this.messageBox("��ѡ�񲡻�");
		}
	}
	/**
	 * 
	* @Title: onCheckCtz1
	* @Description: TODO(���У��)
	* @author pangben 2015-10-29
	* @throws
	 */
	public void onCheckCtz1(){
		String ctz=this.getValueString("CTZ1_CODE");
		onCheckCtz(ctz, "CTZ1_CODE",ctz1);
	}
	/**
	 * 
	* @Title: onCheckCtz1
	* @Description: TODO(���У��)
	* @author pangben 2015-10-29
	* @throws
	 */
	public void onCheckCtz2(){
		String ctz=this.getValueString("CTZ2_CODE");
		if(!onCheckCtz(ctz, "CTZ2_CODE",ctz2))
			return;
		onCheckLumpwork(ctz, "CTZ2_CODE", ctz2);
	}
	/**
	 * 
	* @Title: onCheckCtz1
	* @Description: TODO(���У��)
	* @author pangben 2015-10-29
	* @throws
	 */
	public void onCheckCtz3(){
		String ctz=this.getValueString("CTZ3_CODE");
		onCheckCtz(ctz, "CTZ3_CODE",ctz3);
	}
	/**
	 * 
	* @Title: onCheckCtz
	* @Description: TODO(У������Ƿ����)
	* @author pangben 2015-11-13
	* @param ctz
	* @param name
	* @param ctzValue
	* @return
	* @throws
	 */
	private boolean onCheckCtz(String ctz,String name,String ctzValue){
		if(ctz.length()==0){
			return true;
		}
		String sql="SELECT CTZ_CODE,CTZ_DESC,USE_FLG FROM SYS_CTZ WHERE CTZ_CODE='"+ctz+"'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null==ctzParm.getValue("USE_FLG",0)||ctzParm.getValue("USE_FLG",0).length()<=0
				||ctzParm.getValue("USE_FLG",0).equals("N")) {
				this.messageBox("�����δ����");
				this.setValue(name, ctzValue);
				return false;
		}
		 sql="SELECT CTZ_CODE,CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='"+ctz+
		"' AND SYSDATE BETWEEN START_DATE AND END_DATE ";
		//System.out.println("sql:LLLLLL::"+sql);
		ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (ctzParm.getCount()<=0) {
			if(this.messageBox("��ʾ","������ѹ���,�Ƿ����",2)!=0){
				this.setValue(name, ctzValue);
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	* @Title: onCheckLumpwork
	* @Description: TODO(�ײͲ����޸ĵڶ������ҪУ��)
	* @author pangben 2015-11-2
	* @throws
	 */
	private void onCheckLumpwork(String ctz,String name,String ctzValue){
		String sql="SELECT LUMPWORK_CODE FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
		//System.out.println("sql:LLLLLL::"+sql);
		TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (admParm.getCount()<=0) {
			this.messageBox("û�в�ѯ���˲�����������");
			this.setValue(name, ctzValue);
			return ;
		}
	}
}
