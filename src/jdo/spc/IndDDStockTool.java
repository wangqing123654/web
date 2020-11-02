package jdo.spc;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: �տ�潻�׵�
 * </p>
 *
 * <p>
 * Description:�տ�潻�׵�
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
 * @author zhangy 2009.08.29
 * @version 1.0
 */
public class IndDDStockTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndDDStockTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndDDStockTool
     */
    public static IndDDStockTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDDStockTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndDDStockTool() {
        setModuleName("spc\\INDDDStockModule.x");
        onInit();
    }

    /**
     * �ս�������ҵ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertDDStock", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * �ս�������ҵ-����stock��ʷ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertStockHistory(TParm parm, TConnection conn) {
        TParm result = this.update("insertStockHistory", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }    

    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("qyeryDDStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩ���ս�ɱ�����ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayA(TParm parm) { 
        TParm result = this.query("queryDDStcokDayA", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());  
            return result;
        }    
        return result;
    }

    /**
     * ҩ���ս�ɱ�����ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayB(TParm parm) {
        TParm result = this.query("queryDDStcokDayB", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()   
                + result.getErrName());                            
            return result;
        }
        return result;
    }

    /**
     * ҩ���ս����۽���ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayC(TParm parm) {
        TParm result = this.query("queryDDStcokDayC", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()                   
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩ���ս����۽���ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayD(TParm parm) {
        TParm result = this.query("queryDDStcokDayD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩ���ս��в�ѯ���ڽ��ĳɱ������۽��
     * @param parm TParm  
     * @return TParm
     */
    public TParm onQueryDDStockDayStockOwn(TParm parm) {
        TParm result = this.query("queryDDStcokDayStockOwn", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ҩ���ս��в�ѯ���ڽ��ĳɱ������۽��
     * @param parm TParm   
     * @return TParm
     */
    public TParm onQueryDDStockDayStockOwn2(TParm parm) {
        TParm result = this.query("queryDDStcokDayStockOwn2", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
      /**
      * ҩƷ�����²�ѯ
      *
      * @param parm
      * @return
      */
     public TParm onQueryBildrug(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.query("getbildrugQuery", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
     
     /**
      * �ս�������ҵ
      * @param parm TParm
      * @param conn TConnection
      * @return TParm
      */
     public TParm onDDInsert(TParm parm, TConnection conn) {
         TParm result = this.update("insertDDStock", parm, conn);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         conn.commit();
         return result;
     }
     
     /**
      * �ս�������ҵ
      * @param parm TParm
      * @param conn TConnection
      * @return TParm
      */
     public TParm updateVerifyin(String sql, TConnection conn) {
         TParm result = new TParm(TJDODBTool.getInstance().update(
        			sql,conn));	
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         conn.commit();     
         return result;
     }
}
