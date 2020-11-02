package jdo.ins;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: ���������Ϣ����
 * </p>
 * 
 * <p>
 * Description:���������Ϣ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSBlackListTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	public static INSBlackListTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSNoticeTool
	 */
	public static INSBlackListTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSBlackListTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INSBlackListTool() {
		setModuleName("ins\\INSBlackListModule.x");
		onInit();
	}

	/**
	 * ����ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm saveTParm = parm.getParm("saveTParm");
		if (parm.getValue("ISALLOWCOVER").equalsIgnoreCase("Y")) {
			result = this.deleteData(parm, connection);
			// �жϴ���ֵ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		int count = saveTParm.getCount();
		for (int i = 0; i < count; i++) {
			TParm insertTParm = saveTParm.getRow(i);
			result = update("insertdata", insertTParm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}

		}

		return result;
	}

	/**
	 * ����ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		TParm result = new TParm();

		result = update("insertdata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��ָ��ʱ���ڵ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteData(TParm parm, TConnection connection) {
		TParm result = update("deletedata", parm, connection);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����֪ͨ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatedata(TParm parm, TConnection connection) {
		TParm result = update("updatedata", parm, connection);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ
	 * 
	 * @param queryTParm
	 *            TParm ��ѯ����
	 * @return TParm
	 */
	public TParm selectdata(TParm queryTParm) {
		TParm result = query("selectdata", queryTParm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
