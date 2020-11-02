package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class InvSupDispenseDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvSupDispenseDTool instanceObject;

    /**
     * ������
     */
    public InvSupDispenseDTool() {
        setModuleName("inv\\INVSupDispenseDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvSupDispenseDTool
     */
    public static InvSupDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupDispenseDTool();
        return instanceObject;
    }

    /**
     * ��ѯ���ⵥϸ��(һ������)
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryInv(TParm parm) {
        TParm result = this.query("queryInv", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���ⵥϸ��(������)
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryPack(TParm parm) {
        TParm result = this.query("queryPack", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * �������ⵥϸ��
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

}
