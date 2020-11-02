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
public class InvStockDTool  
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvStockDTool instanceObject;

    /**
     * ������
     */
    public InvStockDTool() {
        setModuleName("inv\\INVStockDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvStockDTool
     */
    public static InvStockDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvStockDTool();
        return instanceObject;
    }

    /**
     * ��ѯ�����ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryStockQty(TParm parm) {
        TParm result = this.query("queryStockQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���������ϸ
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertInvStockD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¿����ϸ(�������)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateInvStockD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���뵥������¿��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyOut(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���뵥�����¿��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyIn(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������������¿����ϸ�Ŀ����
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

    /**
     * ������������¿����ϸ�Ŀ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStockQtyByTear(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByTear", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    
    /**
     * ��ѯ�����ϸ
     * @param parm TParm
     * @return TParm
     */    
    public TParm onQueryStockQTY(TParm parm) {
        TParm result = this.query("StockQTY", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
  //-----------------��Ӧ��start------------------------
    /**
     * �������ʵ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockBatchSeq(TParm parm) {
        //����Ҫ�ǲ����ж��������Σ�
        TParm result = query("getStockBatchSeq", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    /**
     * �����������ʿ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockQty(TParm parm, TConnection connection) {
        TParm result = this.update("updateStockQty", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    //-----------------��Ӧ��end------------------------


}
