package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:ҽ�������÷ָ�����
 * </p>
 * 
 * <p>
 * Description: ҽ�������÷ָ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdOrderTJTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INSOpdOrderTJTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSOpdApproveTool
	 */
	public static INSOpdOrderTJTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSOpdOrderTJTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INSOpdOrderTJTool() {
		setModuleName("ins\\INSOpdOrderTJModule.x");
		onInit();
	}

	/**
	 * ����ָ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSOpdOrder(TParm parm) {
		TParm result = update("insertINSOpdOrder", parm);
		return result;
	}
	/**
	 * �˷�ʹ�ò�ѯ��Ҫ��ӵ��˷�����
	 * @param parm
	 * @return
	 */
	public TParm selectResetOpdOrder(TParm parm){
		TParm result = query("selectResetOpdOrder", parm);
		return result;
	}
	/**
	 * ɾ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteINSOpdOrder(TParm parm, TConnection conn) {
		TParm result = update("deleteINSOpdOrder", parm, conn);
		return result;
	}
	/**
	 * ɾ��
	 * ����ʹ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteINSOpdOrder(TParm parm) {
		TParm result = update("deleteINSOpdOrder", parm);
		return result;
	}
	/**
	 * ��ѯ���SEQ_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectMAXSeqNo(TParm parm) {
		TParm result = query("selectMAXSeqNo", parm);
		return result;
	}
	/**
	 * �޸Ķ���״̬
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm parm, TConnection conn){
		TParm result = update("updateINSOpdOrder", parm, conn);
		return result;
	}
	/**
	 * �޸Ķ���״̬
	 * ����ʹ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm parm){
		TParm result = update("updateINSOpdOrder", parm);
		return result;
	}
	/**
	 * �޸Ĵ�Ʊ״̬
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePrintNo(TParm parm){
		TParm result = update("updatePrintNo", parm);
		return result;
	}
	/**
	 * INSAMT_FLG���˱�־��Ʊ�ݺ�
	 * @param parm
	 * @return
	 */
	public TParm updateINSOpdOrderPrint(TParm parm, TConnection conn){
		TParm result = update("updateINSOpdOrderPrint", parm,conn);
		return result;
	}
	/**
	 * ������� ��ѯ����ϸ������
	 * @param parm
	 * @return
	 */
	public TParm selectOpdOrderAccount(TParm parm){
		TParm result = query("selectOpdOrderAccount", parm);
		return result;
	}
	/**
	 * ���һ���ۼ�����������
	 * @param parm
	 * @return
	 */
	public TParm insertAddOpdOpder(TParm parm){
		TParm result = update("insertAddOpdOpder", parm);
		return result;
	}
	/**
	 * ɾ������--ɾ���ۼ���������
	 * @param parm
	 * @return
	 */
	public TParm deleteAddINSOpdOrder(TParm parm){
		TParm result = update("deleteAddINSOpdOrder", parm);
		return result;
	}
	/**
	 * ��ѯ��������� ���ۼ�����ʹ��
	 * @param parm
	 * @return
	 */
	public TParm queryAddInsOpdOrder(TParm parm){
		TParm result = query("queryAddInsOpdOrder", parm);
		return result;
	}
	/**
	 * ���÷ָ���ѯ��ִ���ϴ�����
	 * @param parm
	 * @return
	 */
	public TParm selectOpdOrder(TParm parm){
		TParm result = query("selectOpdOrder", parm);
		return result;
	}
	/**
	 * ɾ��
	 * �˾��ﲡ����������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteSumINSOpdOrder(TParm parm) {
		TParm result = update("deleteSumINSOpdOrder", parm);
		return result;
	}
	/**
	 * Ʊ�ݲ����޸�Ʊ�ݺ���
	 */
	public TParm updateInsOpdOrderInvNo(TParm parm, TConnection conn){
		TParm result = update("updateInsOpdOrderInvNo", parm);
		return result;
	}
}
