package jdo.bms;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSDeptBillTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BMSDeptBillTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static BMSDeptBillTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSDeptBillTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSDeptBillTool() {
        setModuleName("bms\\BMSDeptBillModule.x");
        onInit();
    }


    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
