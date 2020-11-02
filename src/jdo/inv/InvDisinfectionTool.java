package jdo.inv;

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
public class InvDisinfectionTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvDisinfectionTool instanceObject;

    /**
     * ������
     */
    public InvDisinfectionTool() {
        setModuleName("inv\\INVDisinfectionModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static InvDisinfectionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvDisinfectionTool();
        return instanceObject;
    }

    /**
     *
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
    
    //--------------------��Ӧ��start----------------
    /**
     * ����������¼
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertValue(TParm parm, TConnection connection) {
        TParm result = this.update("insertValue", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * ɾ��������¼
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteValue(TParm parm, TConnection connection) {
        System.out.println("parm="+parm);
        TParm result = this.update("deleteValue", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }
  //--------------------��Ӧ��start----------------
}
