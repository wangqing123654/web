package jdo.ins;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ����ҽ����Ŀ�ֵ�
 * </p>
 * 
 * <p>
 * Description:����ҽ����Ŀ�ֵ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2011-12-09
 * @version 2.0
 */
public class INSRuleTXTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INSRuleTXTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RuleTool
	 */
	public static INSRuleTXTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSRuleTXTool();
		return instanceObject;
	}

	

	/**
	 * ������
	 */
	public INSRuleTXTool() {
		setModuleName("ins\\INSRuleTXModule.x");

		onInit();
	}

	/**
	 * �������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSRule(TParm parm, TConnection connection) {
		TParm result = update("insertINSRule", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * �޸�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateINSRule(TParm parm) {
		TParm result = update("updateINSRule", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ɾ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteINSRule(TParm parm) {
		TParm result = update("deleteINSRule", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ��ѯ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectINSRule(TParm parm) {

		TParm result = query("selectINSRule", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	/**
	 * ��ѯ�Ѿ�ƥ�������
	 * @return
	 */
	public TParm selectMateSysFee(TParm parm){
		TParm result = query("selectMateSysFee"+parm.getValue("sqlName"), parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ȫƥ�����ݵ�ѡ��ťѡ��ִ�в�ѯ����
	 * @return
	 */
	public TParm selectSumSame(TParm parm){
		TParm result = query("selectSumSame"+parm.getValue("sqlName"), parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * �޸�SYS_FEEҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateSysFeeNhi(TParm parm,TConnection connection) {
		TParm result = update("updateSysFeeNhi", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	/**
	 * �޸�SYS_FEEҽ������(�޸ĺ�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateSysFee(TParm parm,TConnection connection) {
		TParm result = update("updateSysFee", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	/**
	 * �޸�SYS_FEE_HISTORYҽ������(�޸ĺ�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateSysFeeHistory(TParm parm,TConnection connection) {
		TParm result = update("updateSysFeehistory", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	//===============  chenxi modify 
	/**
	 * ����SYS_FEE_HISTORY �е�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveFeeHistory(TParm parm,TConnection connection) {
		TParm result = update("saveFeeHistory", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;    
		}  
		return result;

	}
	// ==========  chenxi modify 
	/**
	 * �޸�sys_CHARGE_HOSPҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateChargeHosp(TParm parm,TConnection connection) {
		TParm result = update("updateChargeHosp", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	/**
	 * δƥ�����ݲ�ѯ����
	 * @return
	 */
	public TParm selectNotMateSysFee(TParm parm){
		TParm result = query("selectNotMateSysFee"+parm.getValue("sqlName"), parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
