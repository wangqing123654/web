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
public class FeeCodeOperateTool
    extends TJDOTool {

    /**
    * ʵ��
    */
   private static FeeCodeOperateTool instanceObject;
   /**
    * �õ�ʵ��
    * @return PatTool
    */
   public static FeeCodeOperateTool getInstance()
   {
       if(instanceObject == null)
           instanceObject = new FeeCodeOperateTool();
       return instanceObject;
   }
   /**
    * ������
    */
   public FeeCodeOperateTool()
   {
       setModuleName("sys\\BILFeeCodeOperateModule.x");
       onInit();
   }
   /**
       * ��ʼ������ѯȫ����
       * @return TParm
       */
      public TParm selectalldata() {
          return query("selectalldata");
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


}
