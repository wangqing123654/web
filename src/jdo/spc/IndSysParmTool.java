package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:ҩ������趨Tool
 * </p>
 * 
 * <p>
 * Description:ҩ������趨Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author zhangy 2009.4.22
 * @version 1.0
 */

public class IndSysParmTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static IndSysParmTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndSysParmTool
	 */
	public static IndSysParmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndSysParmTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public IndSysParmTool() {
		setModuleName("spc\\INDSysParmModule.x");
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
				INDSQL.getINDSysParm()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
