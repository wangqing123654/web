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
public class InvPackStockDTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static InvPackStockDTool instanceObject;

    /**
     * ������
     */
    public InvPackStockDTool() {
        setModuleName("inv\\INVPackStockDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvPackStockDTool
     */
    public static InvPackStockDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPackStockDTool();
        return instanceObject;
    }

    /**
     * ���������������Ź���ϸ��Ŀ����
     *
     * @param parm
     * @return
     */
    public TParm onInsertStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("insertStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���������������Ź���ϸ��Ŀ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * û����Ź����������,�۳������������ϸ�еĿ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����Ź����������,ɾ����������ϸ�е�һ��������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteOnceUserInvBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("deleteOnceUserInvBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ����ϸ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
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
  //---------------��Ӧ��start------------------------
    /**
     * ���������������ϸ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertInv(TParm parm, TConnection connection) {
        TParm result = this.update("insertInv", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * ���¿����ϸ�����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateQty(TParm parm, TConnection connection) {
        TParm result = this.update("updateQty", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }
    
    /**
     * ���¸�ֵ��һ������Ʒ��״̬
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm changeHOStatus(TParm parm, TConnection connection) {
        TParm result = this.update("changeHOStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    //---------------��Ӧ��end------------------------

}
