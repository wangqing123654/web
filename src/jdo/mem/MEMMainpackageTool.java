package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class MEMMainpackageTool extends TJDOTool{
	/**
     * ʵ��
     */
    private static MEMMainpackageTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelGroupTool
     */
    public static MEMMainpackageTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMMainpackageTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public MEMMainpackageTool() {
    	setModuleName("mem\\MEMMainPackageModule.x");
        onInit();
    }
    
    /**
     * ����
     * @param inParam
     * @param conn
     * @return
     */
    public TParm onSavePackageData(TParm inParam,TConnection conn){
    	TParm result = update("insertMemPackageM", inParam, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
    }
    
    /**
     * �޸�
     * @param inParam
     * @param conn
     * @return
     */
    public TParm onUpdatePackageData(TParm inParam,TConnection conn){
    	TParm result = update("updateMemPackageM", inParam, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
    }
    
    public TParm updateActiveFlg(TParm inParam,TConnection conn){
    	
    	TParm result = update("updateActiveFlg", inParam,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
    }
}
