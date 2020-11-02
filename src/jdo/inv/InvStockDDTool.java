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
public class InvStockDDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvStockDDTool instanceObject;

    /**
     * ������
     */
    public InvStockDDTool() {
        setModuleName("inv\\INVStockDDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvStockDTool
     */
    public static InvStockDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvStockDDTool();
        return instanceObject;
    }

    /**
     * ���������Ź���ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertInvStockDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�����Ź���ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryStockDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ�����Ź���ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryOut(TParm parm) {  
        TParm result = this.query("queryOutStockDD", parm);
        System.out.println("queryOutStockDD--result"+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
    }  
    

    /**
     * ���¿����Ź���ϸ��(���⼴���)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyOutIn(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyOutIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
  
    
    /**
     * ���¿����Ź���ϸ��(���⼴���)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateOrginCode(TParm parm, TConnection conn) {
        TParm result = this.update("updateOrginCode", parm, conn);
        System.out.println(result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
    
    /**
     * ������λ(�̵�)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateCab(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyCheck", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
  
    /**
     * ���¿����Ź���ϸ��(������;)---�������ã�����
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
  //-------------��Ӧ��start---------------
    /**
     * ������Ź����������ڵ�������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updatePackAge(TParm parm, TConnection connection) {
        TParm result = update("updatePackAge", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    
    //-------------��Ӧ��start---------------


}
