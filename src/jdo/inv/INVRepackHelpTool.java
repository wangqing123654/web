package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVRepackHelpTool extends TJDOTool {
	public static INVRepackHelpTool instanceObject;

	public INVRepackHelpTool() {
		setModuleName("inv\\INVNewRepackModule.x");
		onInit();
	}

	/**
	 * �õ�ʵ��
	 * 
	 * @return IndPurPlanMTool
	 */
	public static INVRepackHelpTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INVRepackHelpTool();
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
     * ����������״̬Ϊ ����� ״̬ 
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
     * ��ѯ������״̬  ������ɾ
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
     * ��ѯ����������������Ϊ ��һ�����Ҹ�ֵ�� ��      ������ɾ
     *
     *
     */
	public TParm queryMaterialAttr(TParm parm, TConnection connection){
		
		TParm result = this.query("queryHighOnce", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	/**
     * ����stockdd���и�ֵ�Ĳĵ���������      ������ɾ
     *
     *
     */
	public TParm updateStockDDWastFlg(TParm parm, TConnection connection){
		
		TParm result = this.update("updateStockDDWastFlg", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	/**
     * �����������ڴ�����е���ǰ��¼��Ϊ���      ������ɾ
     *
     *
     */
	public TParm updateRepackStatus(TParm parm, TConnection connection){
		
		TParm result = this.update("updateRepackStatus", parm, connection);
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
	
	/**
     * ��ѯ�������������һ���Ե�����
     *
     *
     */
	public TParm queryHighOnceMaterial(TParm parm){
		TParm result = this.query("queryHOMaterial", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
	}
	
	
	/**
     * ����������¼
     *
     *
     */
    public TParm insertRepack(TParm parm, TConnection connection) {
        TParm result = this.update("insertRepack", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
	
    
    /**
     * ����RFID��ѯ�����һ��������
     *
     *
     */
    public TParm queryMaterialByRFID(TParm parm){
    	TParm result = this.query("queryMaterialByRFID", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * ����packstockm������
     *
     *
     */
    public TParm updatePackStockMBarcode(TParm parm, TConnection connection) {
        TParm result = this.update("updatePackstockMBarcode", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * ����packstockd������
     *
     *
     */
    public TParm updatePackStockDBarcode(TParm parm, TConnection connection) {
        TParm result = this.update("updatePackstockDBarcode", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * ���������ѯpackstockm��¼
     *
     *
     */
    public TParm queryPackageInfoByBarcode(TParm parm){
    	TParm result = this.query("queryPackageInfoByBarcode", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * ���������ѯpackstockd��¼
     *
     *
     */
    public TParm queryPackageDInfoByBarcode(TParm parm){
    	TParm result = this.query("queryPackageDInfoByBarcode", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
}
