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
public class InvRequestDTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvRequestDTool instanceObject;

    /**
     * ������
     */
    public InvRequestDTool() {
        setModuleName("inv\\INVRequestDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvRequestDTool
     */
    public static InvRequestDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvRequestDTool();
        return instanceObject;
    }

    /**
     * ��ѯ���뵥����
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("queryRequestD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ��������ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createRequestD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ɾ������ϸ��(ȫ��)
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteRequestDAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��Ҫ��������뵥ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryRequestDOut(TParm parm) {
        TParm result = this.query("queryRequestDOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���뵥ϸ��״̬(�������뵥ϸ�������ۻ��������״̬)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateActualQtyAndType(TParm parm, TConnection conn){
        TParm result = this.update("updateActualQtyAndType", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
