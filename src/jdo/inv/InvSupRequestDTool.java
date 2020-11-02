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
public class InvSupRequestDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvSupRequestDTool instanceObject;

    /**
     * ������
     */
    public InvSupRequestDTool() {
        setModuleName("inv\\INVSupRequestDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvSupRequestDTool
     */
    public static InvSupRequestDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupRequestDTool();
        return instanceObject;
    }

    /**
     * �������뵥��ϸ
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
     * �������뵥ϸ��
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
     * ��ѯһ������ϸ��
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
     * ��ѯ�����������ϸ��
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
     * ɾ�����뵥ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm) {
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ�����뵥ȫ��ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�����ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��Ӧ�ҳ���������쵥״̬��������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateFlgAndQtyBySupDispense(TParm parm, TConnection conn) {
        TParm result = this.update("updateFlgAndQtyBySupDispense", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
