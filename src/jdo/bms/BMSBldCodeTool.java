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
public class BMSBldCodeTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static BMSBldCodeTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return BMSBloodTool
     */
    public static BMSBldCodeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSBldCodeTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BMSBldCodeTool() {
        setModuleName("bms\\BMSBldCodeModule.x");
        onInit();
    }

    /**
     * ��ѯ
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
