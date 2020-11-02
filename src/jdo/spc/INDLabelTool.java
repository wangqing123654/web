package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:���ӱ�ǩ��վ��������ά��
 * </p>
 * 
 * <p>
 * Description:���ӱ�ǩ��վ��������ά��
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
 * @author shendr 2013-05-21
 * @version 1.0
 */
public class INDLabelTool extends TJDOTool {

	private static INDLabelTool instanceObject;

	/**
	 * ������
	 */
	public INDLabelTool() {
		setModuleName("spc\\INDLabelModule.x");
	}

	/**
	 * ��ȡʵ������
	 * 
	 * @return
	 */
	public static INDLabelTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new INDLabelTool();
		}
		return instanceObject;
	}

	/**
	 * ��ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryInfo", parm);
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
	public TParm insertInfo(TParm parm) {
		TParm result = new TParm();
		result = update("insertInfo", parm);
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
	public TParm updateInfo(TParm parm) {
		TParm result = new TParm();
		result = update("updateInfo", parm);
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
	public TParm deleteInfo(TParm parm) {
		TParm result = new TParm();
		result = update("deleteInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
