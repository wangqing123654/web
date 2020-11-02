package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
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
public class SYSFeeHistoryTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SYSFeeHistoryTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return CTZTool
     */
    public static SYSFeeHistoryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFeeHistoryTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSFeeHistoryTool() {
        setModuleName("sys\\SYSFeeHistoryModule.x");
        onInit();
    }

    /**
     * ����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


}
