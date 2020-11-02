package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class InvoiceTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvoiceTool instanceObject;
    /**
     * �õ�ʵ��
     * @return InvoiceTool
     */
    public static InvoiceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvoiceTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public InvoiceTool() {
        setModuleName("sys\\BILInvoiceModule.x");
        onInit();
    }
    /**
     * ��ѯȫ������
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData",parm);
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
   /**
    * ��Ʊ��������������
    * @return TParm
    */
   public TParm insertData(TParm parm) {
       TParm result = update("insertData",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * ���˸�����������
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
   /**
      * ���ظ�����������
      * @return TParm
      */
     public TParm updatainData(TParm parm) {
         TParm result = update("updatainData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }
   /**
    * ���Ʊ���Ƿ�ʹ�ù�
    * @param parm TParm
    * @return TParm
    */
   public TParm checkData(TParm parm){
    TParm result = update("checkData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;

}
}
