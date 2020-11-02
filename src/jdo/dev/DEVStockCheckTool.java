package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

public class DEVStockCheckTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static DEVStockCheckTool instanceObject;
 
    /**
     * �õ�ʵ��
     *
     * @return DEVStockCheckTool  
     */
    public static DEVStockCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DEVStockCheckTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public DEVStockCheckTool() {
        setModuleName("dev\\DEVStockCheckModule.x");
        onInit();
    }
    /**
     * ����ʵ�������͵�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
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
     *����ʵ�������͵�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm ) {
        TParm result = this.update("Insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
    * ��ѯ
    * @param parm
    * @return
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
}
