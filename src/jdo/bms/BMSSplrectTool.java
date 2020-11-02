package jdo.bms;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ��Ѫ��Ӧ
 * </p>
 *
 * <p>
 * Description: ��Ѫ��Ӧ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSSplrectTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BMSSplrectTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return BMSBloodTool
     */
    public static BMSSplrectTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSSplrectTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BMSSplrectTool() {
        setModuleName("bms\\BMSSplreactModule.x");
        onInit();
    }

    /**
     * ��ѯ��Ѫ��Ӧ
     *
     * @param parm
     * @return
     */
    public TParm onQueryTransReaction(TParm parm) {
        TParm result = this.query("queryTransReaction", parm);
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

    /**
     * ɾ��
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


}
