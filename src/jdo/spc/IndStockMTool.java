package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ҩ��������Tool
 * </p>
 *
 * <p>
 * Description: ҩ��������Tool
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class IndStockMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndStockMTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndStockMTool
     */
    public static IndStockMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndStockMTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndStockMTool() {
        setModuleName("spc\\INDStockMModule.x");
        onInit();
    }

    /**
     * ��ѯ����������
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    //��������
    public TParm onQueryAll(TParm parm) {
        TParm result = this.query("queryStockMAll", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createNewStockM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������;��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateBuyUnreceiveQty(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockMBUQty", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * update buyunreceive lock qty
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateBuyUnreceiveQtyAndLockQty(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockMBUAndLockQty", parm, conn);
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
    public TParm onInsertIndStockM(TParm parm) {
        TParm result = this.update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @param conn
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
    
    //Add
    public TParm onUpdateAddLockQty(TParm parm,TConnection conn){
    	 TParm result = this.update("updateAddLockQty", parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
    
    //Minus
    public TParm onUpdateMinusLockQty(TParm parm,TConnection conn){
   	 TParm result = this.update("updateMinusLockQty", parm, conn);
     if (result.getErrCode() < 0) {
         err("ERR:" + result.getErrCode() + result.getErrText()
             + result.getErrName());
         return result;
     }
     return result;
}

}
