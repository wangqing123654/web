package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class MEMMainpackageTool extends TJDOTool{
	/**
     * 实例
     */
    private static MEMMainpackageTool instanceObject;
    /**
     * 得到实例
     * @return PanelGroupTool
     */
    public static MEMMainpackageTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMMainpackageTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public MEMMainpackageTool() {
    	setModuleName("mem\\MEMMainPackageModule.x");
        onInit();
    }
    
    /**
     * 新增
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
     * 修改
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
