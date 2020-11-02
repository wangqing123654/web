package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �����ʸ�ȷ��������ȷ��
 * </p>
 * 
 * <p>
 * Description: �����ʸ�ȷ��������ȷ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111202
 * @version 1.0
 */
public class INSMZConfirmTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INSMZConfirmTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSADMConfirmTool
	 */
	public static INSMZConfirmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSMZConfirmTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INSMZConfirmTool() {
		setModuleName("ins\\INSMZConfirmModule.x");
		onInit();
	}

	/**
	 * ����ҽ��վ ������� ��ѯ�Ƿ�����������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectSpcMemo(TParm parm) {
		TParm result = query("selectSpcMemo", parm);
		return result;
	}

	/**
	 * ����ҽ��վ ������� �޸�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInsMZConfirmSpcMemo(TParm parm) {
		TParm result = update("updateInsMZConfirmSpcMemo", parm);
		return result;
	}

	/**
	 * �Һ�ʹ��ҽ��������Ҫ��ӱ�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInsMZConfirm(TParm parm) {
		TParm result = update("insertInsMZConfirm", parm);
		return result;
	}

	/**
	 * �Һ�ʹ��ҽ�������޸ı�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInsMZConfirm(TParm parm) {
		TParm result = update("updateInsMZConfirm", parm);
		return result;
	}

	/**
	 * ����ʧ��ɾ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteInsMZConfirm(TParm parm, TConnection conn) {
		TParm result = update("deleteInsMZConfirm", parm, conn);
		return result;
	}

	/**
	 * ִ������޸Ĳ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSaveInsMZConfirm(TParm parm) {
		TParm result = this.queryMZConfirmOne(parm);
		if (result.getCount("CASE_NO") > 0) {
			// �޸�InsMZConfirm �����ʸ�ȷ���������
			result = this.updateInsMZConfirm(parm);
		} else {
			// ��� InsMZConfirm �����ʸ�ȷ���������
			result = this.insertInsMZConfirm(parm);
		}
		if (result.getErrCode() < 0) {
			return result;
		}
		result = this.queryMZConfirmOne(parm);
		return result;
	}
	/**
	 * ��ѯ�����Ϣ
	 * @param parm
	 * @return
	 */
	public TParm queryMZConfirm(TParm parm){
		TParm result = query("queryMZConfirm", parm);
		return result;
	}
	/**
	 * ��ѯ�����Ϣ
	 * @param parm
	 * @return
	 */
	public TParm queryMZConfirmOne(TParm parm){
		TParm result = query("queryMZConfirmOne", parm);
		return result;
	}
	/**
	 * ��ѯ�Һž���˳���
	 * @param parm
	 * @return
	 */
	public TParm queryInsOpd(TParm parm)
	{
		String sql = " SELECT CONFIRM_NO FROM INS_OPD " +
			 "  WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' " +
			 "	AND RECP_TYPE = 'REG'	";
	    TParm result = new TParm(TJDODBTool.getInstance().select(
			sql));		
		return result;
	}
	/**
	 * ��ѯҽ��ҽ����������
	 * @param parm
	 * @return
	 */
	public TParm queryDrQualifyCode(String userId)
	{
		String sql = " SELECT DR_QUALIFY_CODE FROM SYS_OPERATOR " +
				" WHERE USER_ID='"+ userId + "'";
	    TParm result = new TParm(TJDODBTool.getInstance().select(sql));		
		return result;
	}	
}
