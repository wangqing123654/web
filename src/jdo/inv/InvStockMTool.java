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
public class InvStockMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvStockMTool instanceObject;

    /**
     * ������
     */
    public InvStockMTool() {
        setModuleName("inv\\INVStockMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvStockMTool
     */
    public static InvStockMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvStockMTool();
        return instanceObject;
    }

    /**
     * ���¿�������Ŀ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQty(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQty", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������ҵ���¿����
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQtyOut(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����������
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("createStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿������
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {
        TParm result = this.update("updateStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����������
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createStockM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������������¿�������Ŀ����
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
     * ��Ӧ�ҳ�����ҵ���¿����(������ҵ)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStockQtyByReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
  //---------------��Ӧ��start--------------------
    /**
     * ���ҿ����
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockQty(TParm parm) {
        TParm result = this.query("getStockQty", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * �����������ʿ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockMQty(TParm parm, TConnection connection) {
//    	System.out.println("InvStockMTool.java-----parm"+parm);
        TParm result = this.update("updateStockQtyGYS", parm, connection);//wm2013-06-04  "updateStockQty"
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    
    
    //---------------��Ӧ��end--------------------

}
