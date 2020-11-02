package jdo.ctr;

import com.dongyang.data.*;
import com.dongyang.jdo.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author
 * @version 1.0
 */
public class CtrQueryTool
    extends TJDOTool {

//ʹ��������ǵ�����,ֻ�ܳ�ʼ��һ������
    private static CtrQueryTool instance = null;
    private CtrQueryTool() {

        //����Module�ļ�,�ļ���ʽ������˵��
        this.setModuleName("ctr\\CtrQueryModule.x");
        onInit();
    }

    /**
     *ʵ����
     * @return
     */
    public static CtrQueryTool getNewInstance() {
        if (instance == null) {
            instance = new CtrQueryTool();
        }
        return instance;
    }


    /**
      * ��ѯ
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
     * ����
     * @param parm TParm
     * @return TParm
     */
     public TParm onTableInsert(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
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
      * @param parm TParm
      * @return TParm
      */
    public TParm onTableUpdate(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("update", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
      * ɾ��
      * @param parm TParm
      * @return TParm
      */
    public TParm onTableDelete(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
