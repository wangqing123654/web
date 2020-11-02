package jdo.sys;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
/**
 * <p>Title:���ô������ </p>
 *
 * <p>Description:���ô������ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSChargeHospCodeTool
    extends TJDOTool {

    /**
    * ʵ��
    */
   private static SYSChargeHospCodeTool instanceObject;
   /**
    * �õ�ʵ��
    * @return PatTool
    */
   public static SYSChargeHospCodeTool getInstance()
   {
       if(instanceObject == null)
           instanceObject = new SYSChargeHospCodeTool();
       return instanceObject;
   }
   /**
    * ������
    */
   public SYSChargeHospCodeTool()
   {
       setModuleName("sys\\SYSChargeHospCodeModule.x");
       onInit();
   }
   /**
       * ��ʼ������ѯȫ����
       * @return TParm
       */
      public TParm selectalldata() {
          TParm result = query("selectalldata");
          if(result.getErrCode() < 0) {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;

   }
   /**
    * ����������ѯ����
    * @return TParm
    */
   public TParm selectalldata(TParm parm) {
      TParm result = query("selectdata",parm);
      if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;

   }
   /**
    * �������ݿ�
    * @return TParm
    */
public TParm savedata(TParm parm){
    TParm result = update("insertdata",parm);
     if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

   }
   /**
    * �������ݿ�
    * @return TParm
    */
   public TParm updata(TParm parm) {
       TParm result = update("updatedata",parm);
       if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;


   }
   /**
       * ɾ������
       * @return TParm
    */
public TParm delete(TParm parm,TConnection connection){
       TParm result = update("deletedata",parm,connection);
       if(result.getErrCode() < 0)
          {
              err(result.getErrCode() + " " + result.getErrText());
              return result;
          }
          return result;

   }
   /**
       * ����CHARGE_HOSP_CODE��ѯ�����շ���Ŀ
       * @return TParm��CHARGE_CODE,MRO_CHARGE_CODE,STA_CHARGE_CODE
       */
      public TParm selectChargeCode(TParm parm) {
         TParm result = query("selectChargeCode",parm);
         if(result.getErrCode() < 0){
              err(result.getErrCode() + " " + result.getErrText());
              return result;
          }
          return result;

   }

}
