package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:���ʲ����趨Tool
 * </p>
 * 
 * <p>
 * Description:���ʲ����趨Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangzl 2013.1.5
 * @version 1.0
 */

public class InvSysParmTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static InvSysParmTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndSysParmTool
	 */
	public static InvSysParmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InvSysParmTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public InvSysParmTool() {
		setModuleName("inv\\INVSysParmModule.x");
		onInit();
	}

	/**
	 * ���������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TParm result = this.update("insertData", parm);
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
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = this.update("updateData", parm);
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
	 * @return
	 */
	public TParm onQuery() {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INVNewSQL.getINDSysParm()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
