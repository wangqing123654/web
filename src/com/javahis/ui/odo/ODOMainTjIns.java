package com.javahis.ui.odo;

import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;

import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSTJTool;
import jdo.sys.Operator;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ���ҽ������
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ���ҽ������
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainTjIns {
	public OdoMainControl odoMainControl;
	ODOMainReg odoMainReg;
	ODOMainPat odoMainPat;
	
	private static final String URLINSDRQUERYLIST = "%ROOT%\\config\\ins\\INSDrQueryList.x";
	private static final String URLINSMTREG = "%ROOT%\\config\\ins\\INSMTReg.x";
	private static final String URLINSSPCMEMODIAG = "%ROOT%\\config\\ins\\InsSpcMemoDiag.x";
	
	/**
	 * �Ƿ�ҽ����� true ҽ����� false ��ҽ�����
	 */
	private boolean isINSPat = false;
	/**
	 * ҽ������
	 */
	private INSTJTool insTool = null;

	/**
	 * �������
	 */
	private String ctzCode = "";
	// �Ƿ�����ҽ��
	public boolean whetherCallInsItf = false;
	
	public ODOMainTjIns(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	public void onInit() throws Exception{
		odoMainReg = odoMainControl.odoMainReg;
		odoMainPat = odoMainControl.odoMainPat;
	}
	
	/**
	 * xueyf 2012-02-28 ҽ�����ش�����ѯ
	 */
	public void onINSDrQuery() throws Exception{
		String CASE_NO = odoMainControl.odo.getCaseNo();
		if (StringUtil.isNullString(CASE_NO)) {
			odoMainControl.messageBox("��ѡ�񲡻���");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("INS_TYPE", odoMainReg.reg.getInsPatType());
		odoMainControl.openDialog(URLINSDRQUERYLIST, parm);
	}
	
	/**
	 * ���صǼ�
	 */
	public void onMTRegister() throws Exception{
		TParm parm = new TParm();
		parm.setData("MR_NO", odoMainPat.pat.getMrNo());
		parm.setData("CASE_NO", odoMainReg.reg.caseNo());
		odoMainControl.openDialog(URLINSMTREG, parm);
	}
	
	/**
	 * ҽ��������ѯ
	 */
	public void onINSDrQueryList() throws Exception{
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", odoMainReg.reg.caseNo());
		// �Ƿ���ڲ���
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			odoMainControl.err(result.getErrName() + "��" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			odoMainControl.messageBox("�˾��ﲡ��������ҽ������");
			return;
		}
		odoMainControl.openDialog(URLINSDRQUERYLIST, result
				.getData());
	}
	
	/**
	 * ��ʼ��ҽ���ӿ�
	 * 
	 * @param ctzCode
	 */
	public void initINS()throws Exception {
		if (insTool == null) {
			insTool = new INSTJTool();
		}
	}
	
	/**
	 * �����������ʹ��
	 */
	public void onSpecialCase() throws Exception{
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", odoMainReg.reg.caseNo());
		// ��ѯ�Ƿ���Դ����������:�Һ�ҽ�����ز�������ʹ��
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			odoMainControl.err(result.getErrName() + "��" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			odoMainControl.messageBox("�˾��ﲡ��������ҽ������");
			return;
		}
		TParm spcParm = (TParm) odoMainControl.openDialog(
				URLINSSPCMEMODIAG, result.getData());
		if (null == spcParm || null == spcParm.getValue("SPECIAL_CASE")) {
			return;
		}
		parm.setData("SPC_MEMO", spcParm.getValue("SPECIAL_CASE"));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		result = INSMZConfirmTool.getInstance().updateInsMZConfirmSpcMemo(parm);
		if (result.getErrCode() < 0) {
			odoMainControl.err(result.getErrName() + "��" + result.getErrText());
			odoMainControl.messageBox("E0005");
			return;
		}
		odoMainControl.messageBox("P0005");
	}

}
