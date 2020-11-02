package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ����������ϸTool
 * </p>
 *
 * <p>
 * Description: ����������ϸTool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class IndDispenseDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndDispenseDTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndStockMTool
     */
    public static IndDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDispenseDTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndDispenseDTool() {
        setModuleName("ind\\INDDispenseDModule.x");
        onInit();
    }

    /**
     * ����ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertD(TParm parm, TConnection conn) {
        TParm result = this.update("createNewDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateD(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
