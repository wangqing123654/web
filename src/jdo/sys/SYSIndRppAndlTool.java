package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 *
 * <p>
 * Title: ���ۿ�������
 * </p>
 *
 * <p>
 * Description:���ۿ�������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangy 2009/07/22
 * @version 1.0
 */

public class SYSIndRppAndlTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SYSIndRppAndlTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return CTZTool
     */
    public static SYSIndRppAndlTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSIndRppAndlTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSIndRppAndlTool() {
        setModuleName("sys\\SYSIndRppAndlModule.x");
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


}
