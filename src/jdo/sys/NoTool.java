package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ȡ��ԭ�򴰿�</p>
 *
 * <p>Description: ȡ��ԭ�򴰿ڹ�����</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class NoTool extends TJDOTool{
    /**
    * ʵ��
    */
   public static NoTool instanceObject;
   /**
    * �õ�ʵ��
    * @return PositionTool
    */
   public static NoTool getInstance()
   {
       if(instanceObject == null)
           instanceObject = new NoTool();
       return instanceObject;
   }
   /**
    * ������
    */
   public NoTool()
   {
       setModuleName("sys\\SYSNoModule.x");
       onInit();
   }

   /**
    * ��ѯ
    */

   public TParm selectdata(TParm parm){

       TParm result = new TParm();
       result = query("selectdata", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }


       return result;

   }

   /**
    * ����
    */

   public TParm insertdata(TParm parm){

       TParm result = new TParm();
       String regionCode = parm.getValue("REGION_CODE");
       String systemCode = parm.getValue("SYSTEM_CODE");
       String operation = parm.getValue("OPERATION");
       String subl_operation = parm.getValue("SUB1_OPERATION");

       //�ж��Ƿ��Ѵ��ڸ�����
       if(existsData(regionCode,systemCode,operation,subl_operation)){
           return result ;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result ;
   }

   /**
    * ����
    *
    */
   public TParm updatedata(TParm parm) {
       TParm result = update("updatedata", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
    }

     /**
       * ɾ��
       */
    public TParm deletedata(TParm parm) {

          TParm result = update("deletedata", parm);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;
    }
    /**
     * ����֮ǰ�ж��Ƿ��Դ��ڸ�����
     *
     */

   public boolean existsData(String regionCode, String systemCode, String operation, String subl_operation){
       TParm parm = new TParm();
       parm.setData("REGION_CODE",regionCode);
       parm.setData("SYSTEM_CODE",systemCode);
       parm.setData("OPERATION",operation);
       parm.setData("SUB1_OPERATION",subl_operation);
       return getResultInt(query("existdata",parm),"COUNT") > 0;
   }


}
