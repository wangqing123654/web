package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.clp.CLPCauseHistoryTool;
import jdo.clp.CLPManagemTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatCLPEvlStandm;
/**
 * <p>
 * Title: �ٴ�·��׼��׼��
 * </p>
 * 
 * <p>
 * Description: סԺҽ��վ��һ�ν�������ٴ�·��׼��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangben 2015-8-11
 * @version 1.0
 */
public class CLPManageMNewControl extends TControl{
	/**
	 * �������
	 */
	private String case_no;
	private String mr_no;
	private String optUser;
	private String optTerm;
	private String regionCode;
	private String clpNewFlg;
	/**
	 * ҳ���ʼ������
	 */
	public void onInit() {
		super.onInit();
		// ȥ���˵���
		TDialog F = (TDialog) this.getComponent("UI");
		F.setUndecorated(true);
		initPage();
	}
	/**
	 * ��ʼ��
	 */
	private void initPage() {
		TParm inParm = (TParm) this.getParameter();
		case_no = inParm.getValue("CASE_NO");
		mr_no = inParm.getValue("MR_NO");
		optUser=inParm.getValue("OPT_USER");
		optTerm=inParm.getValue("OPT_TERM");
		regionCode=inParm.getValue("REGION_CODE");
		clpNewFlg=inParm.getValue("CLP_NEW_FLG");//��һ�ν���ҽ��վ����������ת�Ʊ�ADM_TRANS_LOG
		//���渳ֵ������Ϣ
		String patName=inParm.getValue("PAT_NAME");
		String deptCode=inParm.getValue("DEPT_CODE");//�������ڿ���
		String ctzCode=inParm.getValue("CTZ_CODE");//���
		String diagCode=inParm.getValue("DIAG_CODE");//���
		String diagDesc=inParm.getValue("DIAG_DESC");
		this.setValue("PAT_NAME", patName);
		this.setValue("DEPT_CODE", deptCode);
		this.setValue("CTZ_CODE", ctzCode);
		this.setValue("DIAG_CODE", diagCode+diagDesc);
		this.setValue("MR_NO", mr_no);
		this.setValue("CLNCPATH_CODE", inParm.getValue("CLNCPATH_CODE"));
		onCheckQuery();
		
		//����webService ��ѯĬ��·������
	}
	/**
	 * 
	* @Title: onUnEnter
	* @Description: TODO(������)
	* @author pangben
	* @throws
	 */
	public void onUnEnter(){
		String causeCode=this.getValueString("CAUSE_CODE");
		if (causeCode.length()<=0) {
			this.messageBox("������ԭ����Ϊ��");
			this.grabFocus("CAUSE_CODE");
			return;
		}
		TParm parm=new TParm();
		parm.setData("CASE_NO",case_no);
		parm.setData("MR_NO",mr_no);
		//��ѯ������
		TParm getMaxSeqNoParm=CLPCauseHistoryTool.getInstance().queryMaxSeqNo(parm);
		if (getMaxSeqNoParm.getCount()>0&&getMaxSeqNoParm.getValue("SEQ_NO",0).length()>0) {
			parm.setData("SEQ_NO",getMaxSeqNoParm.getInt("SEQ_NO",0)+1);
		}else{
			parm.setData("SEQ_NO",0);
		}
		parm.setData("CAUSE_CODE",causeCode);
		parm.setData("CAUSE_USER",optUser);
		parm.setData("OPT_USER",optUser);
		parm.setData("OPT_TERM",optTerm);
		parm.setData("CLP_NEW_FLG", clpNewFlg);//��һ�ν���ҽ��վ����������ת�Ʊ�ADM_TRANS_LOG
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "insertCauseHistory", parm);
		if (result.getErrCode()< 0) {
			this.messageBox("E0002");
		} else {
			this.messageBox("P0002");
			this.closeWindow();
		}
	}
	/**
	 * 
	* @Title: onSave
	* @Description: TODO(����)
	* @author pangben
	* @throws
	 */
	public void onSave(){
		TParm parm = new TParm();
		parm.setData("MR_NO", this.mr_no);
		parm.setData("CASE_NO", this.case_no);
		this.putBasicSysInfoIntoParm(parm);
		TParm result = CLPManagemTool.getInstance().getPatientInfo(parm);
		if (result.getCount() <= 0) {
			this.messageBox("���Ȳ�ѯסԺ������Ϣ");
			return;
		}
		// ���ò�ѯ����
		TParm resultTParm = CLPManagemTool.getInstance().selectData(parm);
		if (result.getCount() <= 0) {
			this.messageBox("���Ȳ�ѯסԺ������Ϣ");
			return;
		}
		if (resultTParm.getCount() > 0) {
			this.messageBox("�ò����ٴ�·����Ϣ�Ѿ�����!");
			return;
		}
		if (!validData()) {
			return;
		}
		parm.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));//�ٴ�·������
		parm.setData("SCHD_CODE", this.getValue("SCHD_CODE"));//ʱ��
		parm.setData("EVL_CODE", this.getValue("EVL_CODE"));//��������
		parm.setData("CASE_NO",case_no);
		parm.setData("MR_NO",mr_no);
		putBasicSysInfoIntoParm(parm);
		String sql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND END_DTTM IS NOT NULL";
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selparm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (selparm.getCount("CASE_NO")>0) {
			this.messageBox("�˲������ٴ�·����Ŀ�Ѿ����,���������");
			return;
		}
		//====yanjing 20140710 ��ѯ��·���Ƿ�������ϵ����� start
		String cancleSql = "SELECT CASE_NO FROM CLP_MANAGEM WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' " +
				"AND CLNCPATH_CODE = '"+parm.getValue("CLNCPATH_CODE")+"'";
		TParm cancleParm = new TParm(TJDODBTool.getInstance().select(cancleSql));
		if (cancleParm.getErrCode() < 0) {
			this.messageBox("E0002");
			return;
		}
		if (cancleParm.getCount("CASE_NO")>0) {
			this.messageBox("�˲����������ϵĸ��ٴ�·����Ŀ");
			return;
		}
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("IN_DATE", datestr);
		parm.setData("START_DTTM", getCurrentDateTimeStr());
		parm.setData("CLP_NEW_FLG", clpNewFlg);//��һ�ν���ҽ��վ����������ת�Ʊ�ADM_TRANS_LOG
		TParm resultParam = TIOM_AppServer.executeAction(
				"action.clp.CLPManagemAction", "insertManagem", parm);
		if (resultParam.getErrCode()< 0) {
			this.messageBox("E0002");
		} else {
			this.messageBox("P0002");
			this.closeWindow();
		}
		
	}
	/**
	 * ��TParm�м���ϵͳĬ����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		//int total = parm.getCount();
		//System.out.println("total" + total);
		parm.setData("REGION_CODE",regionCode );
		parm.setData("OPT_USER", optUser);
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", optTerm);
	}
	/**
	 * ������֤����
	 * 
	 * @return boolean
	 */
	private boolean validData() {
		boolean flag = true;
		if (this.getValueString("CLNCPATH_CODE") == null
				|| this.getValueString("CLNCPATH_CODE").length() <= 0) {
			this.messageBox("��ѡ���ٴ�·��");
			return false;
		}
		if (this.getValueString("EVL_CODE") == null
				|| this.getValueString("EVL_CODE").length() <= 0) {
			this.messageBox("��ѡ����������");
			return false;
		}
		return flag;
	}
	/**
	 * �õ���ǰ��ʱ�䣬������ʱ����
	 * 
	 * @return String
	 */
	private String getCurrentDateTimeStr() {
		return getCurrentDateTimeStr("yyyyMMddHHmmss");
	}

	/**
	 * �õ���ǰʱ�䣬������ʱ����
	 * 
	 * @param formatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateTimeStr(String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		String dateStr = format.format(new Date());
		return dateStr;
	}
	/**
	 * 
	* @Title: onCheckQuery
	* @Description: TODO(�ٴ�·�������б��¼�)
	* @author pangben
	* @throws
	 */
	public void onCheckQuery(){
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_schd.onQuery();
        String sql="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+
        this.getValueString("CLNCPATH_CODE")+"' ORDER BY SEQ";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        TextFormatCLPEvlStandm combo_evl=  (TextFormatCLPEvlStandm)this.getComponent("EVL_CODE");
        combo_evl.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_evl.onQuery();
        if (result.getCount()>0) {
        	this.setValue("SCHD_CODE", result.getValue("SCHD_CODE",0));
		}
        sql="SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE = '" + 
        this.getValueString("CLNCPATH_CODE") + "' ORDER BY EVL_CODE,SEQ ";
        result=new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount()>0) {
       	    this.setValue("EVL_CODE", result.getValue("EVL_CODE",0));
		}
	}
}
