package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ����������ϸTool
 * </p>
 *
 * <p>
 * Description: ����������ϸTool
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
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class IndDispenseDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndDispenseDTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndStockMTool
     */
    public static IndDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDispenseDTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndDispenseDTool() {
        setModuleName("spc\\INDDispenseDModule.x");
        onInit();
    }

    /**
     * ����ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertD(TParm parm, TConnection conn) {
        TParm result = this.update("createNewDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateD(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ����ϸ��(�ż���ҩ�����龫)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateDDrug(TParm parm, TConnection conn) {
        TParm result = this.update("updateDDrug", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * �ż���ҩ�����龫��ѯ
     * @param parm
     * @return
     */
    public TParm onQueryDispenseDDrug(TParm parm){
    	 TParm result = this.query("queryDDrug", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
    
    /**
     * ��ѯ�Ƿ�ȫ���ϼ�
     * @param parm
     * @return
     */
    public TParm onQueryDispenseD(TParm parm){
    	 TParm result = this.query("queryDispenseD", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
    
    /**
     * ����ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateDToxic(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseDToxic", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
