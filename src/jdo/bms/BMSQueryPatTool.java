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
public class BMSQueryPatTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static BMSQueryPatTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return BMSBloodTool
     */
    public static BMSQueryPatTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSQueryPatTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BMSQueryPatTool() {
        setModuleName("bms\\BMSQueryPatModule.x");
        onInit();
    }

    /**
     * �ż����ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryOE(TParm parm) {
        TParm result = this.query("queryOE", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * סԺ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryI(TParm parm) {
        TParm result = this.query("queryI", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
