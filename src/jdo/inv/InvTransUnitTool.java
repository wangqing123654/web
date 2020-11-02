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
public class InvTransUnitTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static InvTransUnitTool instanceObject;

    /**
     * ������
     */
    public InvTransUnitTool() {
        setModuleName("inv\\INVTransUnitModule.x");
        onInit();
    }

    /**
    * �õ�ʵ��
    *
    * @return InvTransUnitTool
    */
   public static InvTransUnitTool getInstance() {
       if (instanceObject == null)
           instanceObject = new InvTransUnitTool();
       return instanceObject;
   }

   /**
     * �������ʵ�λ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
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
     * �������ʵ�λ
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
     * ɾ�����ʵ�λ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
       TParm result = this.update("delete", parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
}
