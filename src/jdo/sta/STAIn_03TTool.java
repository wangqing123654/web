package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: STA_IN_03医院住院病患动态及疗效台帐</p>
 *
 * <p>Description: STA_IN_03医院住院病患动态及疗效台帐</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-1
 * @version 1.0
 */
public class STAIn_03TTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAIn_03TTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAIn_03TTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_03TTool();
        return instanceObject;
    }

    public STAIn_03TTool() {
        setModuleName("sta\\STAIn_03TModule.x");
        onInit();
    }
    /**
     * 查询本期数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBQ(TParm parm){
        TParm result = this.query("selectBQ",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询趋势数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectQS(TParm parm){
        TParm result = this.query("selectQS",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
