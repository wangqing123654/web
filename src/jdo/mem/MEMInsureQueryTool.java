package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 保险患者信息查询工具类
 * </p>
 * @author liling 20140713
 * 
 *
 */
public class MEMInsureQueryTool extends TJDOTool{
	 /**
     * 实例
     */
    private static MEMInsureQueryTool instanceObject;
    /**
     * 得到实例
     * @return PanelGroupTool
     */
    public static MEMInsureQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMInsureQueryTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public MEMInsureQueryTool() {
        setModuleName("mem\\MEMInsureQueryModule.x");
        onInit();
    }
    /**
     * 查询保险患者信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新保险患者信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
