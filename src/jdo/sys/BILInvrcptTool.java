package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILInvrcptTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILInvrcptTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILInvrcptTool
     */
    public static BILInvrcptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILInvrcptTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILInvrcptTool() {
        setModuleName("sys\\BILInvrcptModule.x");
        onInit();
    }
    /**
     * ��ѯȫ��������ϸ
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectData",parm);
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
   /**
    * ��ӡƱ��
    * @return TParm
    */
   public TParm insertData(TParm parm,TConnection connection) {
       TParm result = update("insertData",parm,connection);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * ���£����ϵ�
    * @return TParm
    */
   public TParm updataData(TParm parm,TConnection connection) {
       TParm result = update("updataData",parm,connection);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
      * ������������
      * @return TParm
      */
     public TParm updataData(TParm parm) {
         TParm result = update("updataData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }

}
