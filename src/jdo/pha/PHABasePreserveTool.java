package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �а�װת��ά��������
 * </p>
 * 
 * <p>
 * Description: �а�װת��ά��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Date: 2013.4.29
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author shendongri
 * @version 1.0
 */
public class PHABasePreserveTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static PHABasePreserveTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PhaBaseTool
	 */
	public static PHABasePreserveTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PHABasePreserveTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public PHABasePreserveTool() {
		setModuleName("pha\\PHABasePreserveControlModule.x");
		onInit();
	}

	/**
	 * ����ҩƷ�����ѯ��λ����
	 * @param parm
	 * @return
	 */
	public TParm queryUnitByOrderCodeInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryUnitByOrderCodeInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm updateInfo(TParm parm) {
		TParm result = new TParm();
		result = update("updateInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����ORDER_CODE��PHA_BASE
	 * @param parm
	 * @return
	 */
	public TParm queryByOrderCodeInfo(TParm parm) {
		TParm result = new TParm();
		result = query("queryByOrderCodeInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��PHA_BASE
	 * @return
	 */
	public TParm queryInfo() {
		TParm result = new TParm();
		result = query("queryInfo");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����UNIT_CHN_DESC��ѯ��Ӧ��UNIT_CODE
	 * @param parm
	 * @return
	 */
	public TParm queryUnitDescByCode(TParm parm) {
		TParm result = new TParm();
		result = query("queryUnitDescByCode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

}
