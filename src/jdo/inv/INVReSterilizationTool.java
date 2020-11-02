package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * Description: ���ʧ�ܵǼ����ݲ�����
 * 
 * 
 * @author wangming	2013.8.1
 * @version 1.0
 */
public class INVReSterilizationTool extends TJDOTool {

	/**
	 * ������
	 */
	private INVReSterilizationTool() {
		setModuleName("inv\\INVReSterilizationModule.x");
		onInit();
	}

	/**
	 * ʵ������
	 */
	private static INVReSterilizationTool instanceObject;

	/**
	 * ���ʵ��
	 * 
	 * @return
	 */
	public static INVReSterilizationTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new INVReSterilizationTool();
		}
		return instanceObject;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertRSInfo(TParm parm) {
		 TParm result = this.update("insertRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
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
	public TParm updateRSInfo(TParm parm) {
		 TParm result = this.update("updateRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
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
	public TParm deleteRSInfo(TParm parm) {
		 TParm result = this.update("deleteRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ��inv_packstockm�в�ѯ��������Ϣ
	 * @param tparm
	 * @return
	 */
	public TParm queryPackMByBarcode(TParm parm) {
		TParm result = query("queryPackMByBarcode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ��inv_repack�в�ѯ��������Ϣ
	 * @param tparm
	 * @return
	 */
	public TParm queryPackRByBarcode(TParm parm) {
		TParm result = query("queryPackRByBarcode", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ��inv_backwashing�в�ѯ��ϴ�Ǽ���Ϣ
	 * @param tparm
	 * @return
	 */
	public TParm queryRSInfo(TParm parm) {
		TParm result = query("queryRSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ����������״̬Ϊ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatePackageStatus(TParm parm) {
		 TParm result = this.update("updatePackageStatus", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	
	/**
	 * ��ѯ����������¹�����
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm queryRSCount(TParm parm) {
		TParm result = query("queryRSCount", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}


}
