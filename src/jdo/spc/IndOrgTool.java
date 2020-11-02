package jdo.spc;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: ҩ���趨
 * </p>
 *
 * <p>
 * Description: ҩ���趨
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
public class IndOrgTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndOrgTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndOrgTool
     */
    public static IndOrgTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndOrgTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndOrgTool() {
        setModuleName("ind\\INDOrgModule.x");
        onInit();
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBatchFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateBatchFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
