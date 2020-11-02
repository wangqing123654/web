package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVSterilizationHelpTool extends TJDOTool {
	public static INVSterilizationHelpTool instanceObject;

	public INVSterilizationHelpTool() {
		setModuleName("inv\\INVNewSterilizationModule.x");
		onInit();
	}

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndPurPlanMTool
	 */
	public static INVSterilizationHelpTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INVSterilizationHelpTool();
		return instanceObject;
	}
	
	
	/**
     * ���������¼
     *
     *
     */
    public TParm insertSterilization(TParm parm, TConnection connection) {
        TParm result = this.update("insertSterilization", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
	
    /**
     * ����������״̬Ϊ �������� ״̬ ���ڿ⣩
     *
     *
     */
	public TParm updatePackageStatus(TParm parm, TConnection connection){
		TParm result = this.update("updatePackageStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * ���Ļ��յ������������״̬Ϊ  Y
     *
     *
     */
	public TParm updateDisinfectionFinishFlg(TParm parm, TConnection connection){
		TParm result = this.update("updateDisFinishFlg", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * ������������Ų�ѯ���������ʹ���
     *
     *
     */
	public TParm queryPackDByPackCode(TParm parm, TConnection connection){
		
		TParm result = this.query("queryPackDByPackCode", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	/**
     * ɾ����������ϸ����
     *
     *
     */
	public TParm deletePackageDetailInfo(TParm parm, TConnection connection){
		
		TParm result = this.update("delPackageDInfo", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	
	
	/**
     * ������������ϸ����
     *
     *
     */
	public TParm insertPackageDetailInfo(TParm parm, TConnection connection){
		
		TParm result = this.update("insertPackageDInfo", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	
	
	/**
     * ��ѯ������״̬
     *
     *
     */
	public TParm queryPackageStatus(TParm parm, TConnection connection){
		
		TParm result = this.query("queryPackageStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	
	/**
     * �����������ڻ��յ��е���ǰ��¼��Ϊ���
     *
     *
     */
	public TParm updatePackageDisStatus(TParm parm, TConnection connection){
		TParm result = this.update("updatePackageDisStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	
	
	/**
     * ������������������е���ǰ��¼��Ϊ���
     *
     *
     */
	public TParm updatePackageSterStatus(TParm parm, TConnection connection){
		TParm result = this.update("updatePackageSterStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	
	
}
