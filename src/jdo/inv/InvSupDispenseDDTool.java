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
public class InvSupDispenseDDTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvSupDispenseDDTool instanceObject;

    /**
     * ������
     */
    public InvSupDispenseDDTool() {
        setModuleName("inv\\INVSupDispenseDDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvSupDispenseDDTool
     */
    public static InvSupDispenseDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupDispenseDDTool();
        return instanceObject;
    }

    /**
     * �������뵥����
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
