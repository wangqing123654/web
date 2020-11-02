package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * Description: ��ϴ�ʵǼ����ݲ�����
 * 
 * 
 * @author wangming	2013.8.1
 * @version 1.0
 */
public class INVBackwashingTool extends TJDOTool {

	/**
	 * ������
	 */
	private INVBackwashingTool() {
		setModuleName("inv\\INVBackwashingModule.x");
		onInit();
	}

	/**
	 * ʵ������
	 */
	private static INVBackwashingTool instanceObject;

	/**
	 * ���ʵ��
	 * 
	 * @return
	 */
	public static INVBackwashingTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new INVBackwashingTool();
		}
		return instanceObject;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertBSInfo(TParm parm) {
		 TParm result = this.update("insertBSInfo", parm);
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
	public TParm updateBSInfo(TParm parm) {
		 TParm result = this.update("updateBSInfo", parm);
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
	public TParm deleteBSInfo(TParm parm) {
		 TParm result = this.update("deleteBSInfo", parm);
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
	public TParm queryBSInfo(TParm parm) {
		TParm result = query("queryBSInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ��ϴ��ͳ�Ʋ�ѯ
	 * @param tparm
	 * @return
	 */
	public TParm queryBSCount(TParm parm) {
		TParm result = query("queryBSCount", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	} 



}
