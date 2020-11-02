package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �����̴����Ʒ�趨
 * </p>
 * 
 * <p>
 * Description: �����̴����Ʒ�趨
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
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class IndAgentTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static IndAgentTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndAgentTool
	 */
	public static IndAgentTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndAgentTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public IndAgentTool() {
		setModuleName("ind\\INDAgentModule.x");
		onInit();
	}

	/**
	 * ��ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("query", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsert(TParm parm, TConnection conn) {
		TParm result = this.update("insert", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdate(TParm parm, TConnection conn) {
		TParm result = this.update("update", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onDelete(TParm parm, TConnection conn) {
		TParm result = this.update("delete", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ���뵥λ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryUnit(TParm parm) {
		TParm result = this.query("queryUnit", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �Զ�ά�����뵥��,���º�Լ����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateContractPrice(TParm parm, TConnection conn) {
		TParm result = this.update("updateContractPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
