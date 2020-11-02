package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVDisnfectionHelpTool extends TJDOTool{

	public static INVDisnfectionHelpTool instanceObject;
	
	public INVDisnfectionHelpTool() {
        setModuleName("inv\\INVNewBackDisinfectionModule.x");
        onInit();
    }
	
	/**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static INVDisnfectionHelpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVDisnfectionHelpTool();
        return instanceObject;
    }
	
    /**
     * ����������¼
     *
     *
     */
    public TParm insertDisnfection(TParm parm, TConnection connection) {
        TParm result = this.update("insertDisinfection", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    } 
    
    /**
     * ����������״̬Ϊ ������ϴ���� ״̬
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
     * ������е�������
     *
     *
     */
	public TParm updateRecountTime(TParm parm, TConnection connection){
		TParm result = this.update("updateRecountTime", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * ��ѯ��е������
     *
     *
     */
	public TParm queryBatchNo(TParm parm, TConnection connection){
		TParm result = this.query("queryBatchNo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * ����������
     *
     *
     */
	public TParm updateStockMQTY(TParm parm, TConnection connection){
		TParm result = this.update("updateStockM", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * ����ϸ����
     *
     *
     */
	public TParm updateStockDQTY(TParm parm, TConnection connection){
		TParm result = this.update("updateStockD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
}
