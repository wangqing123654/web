package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class HRMPatadMTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static HRMPatadMTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return HRMCompanyTool
	 */
	public static HRMPatadMTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new HRMPatadMTool();
		}
		return instanceObject;
	}

	/**
	 * ������
	 */
	public HRMPatadMTool() {
		setModuleName("hrm\\HRMPatadMModule.x");
		onInit();
	}
	/**
	 * ɾ������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteHrmPatadm(TParm parm, TConnection conn) {
		TParm result = update("deleteHrmPatadm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯHRM_PATADM������
	 * @param parm
	 * @return
	 */
	public TParm selectHrmPatadm(TParm parm) {
		TParm result = query("selectHrmPatadm", parm);
		return result;
	}
}
