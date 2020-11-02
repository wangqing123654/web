package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �ɹ��ƻ���ϸTool
 * </p>
 * 
 * <p>
 * Description: �ɹ��ƻ���ϸTool
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
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class IndPurPlanDTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static IndPurPlanDTool instanceObject;

	/**
	 * ������
	 */
	public IndPurPlanDTool() {
		setModuleName("ind\\INDPurPlanDModule.x");
		onInit();
	}

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndPurPlanDTool
	 */
	public static IndPurPlanDTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndPurPlanDTool();
		return instanceObject;
	}

	/**
	 * ��ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("queryPlanD", parm);
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
		TParm result = this.update("createNewPlanD", parm, conn);
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
		TParm result = this.update("updatePlanD", parm, conn);
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
		TParm result = this.update("deletePlanD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�ɹ��ƻ����ɶ�������ϸ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryCreate(TParm parm) {
		TParm result = this.query("queryCreatePurPlanD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
