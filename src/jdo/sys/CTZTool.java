package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class CTZTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static CTZTool instanceObject;
    /**
     * �õ�ʵ��
     * @return CTZTool
     */
    public static CTZTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CTZTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public CTZTool() {
        setModuleName("sys\\SYSCTZModule.x");
        onInit();
    }
    /**
     * ��ѯȫ������
     * @return TParm CTZ_CODE;CTZ_DESC
     */
    public TParm selecTtreeData() {
        TParm result = query("selectTreeData");
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
    * ��ѯȫ������
    * @return TParm CTZ_CODE;CTZ_DESC
    */
   public TParm selectData(TParm parm) {
       TParm result = query("selectData",parm);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * ��������������
    * @return TParm CTZ_CODE;CTZ_DESC
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
    * ������������
    * @return TParm CTZ_CODE;CTZ_DESC
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
      * ɾ����������
      * @return TParm CTZ_CODE
      */
     public TParm deleteData(TParm parm,TConnection connection) {
         TParm result = update("deleteData",parm,connection);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }
   /**
    * ��������ݲ�ѯҽ����λ
    * @param parm TParm
    * @return TParm(ctz_code)
    */
   public TParm selCompanyCodeByCtz(TParm parm){
       TParm result = query("",parm);
       if(result.getErrCode()<0){
       err(result.getErrCode() + " " +result.getErrText());
       return result;
       }
       return result;

   }
   /**
    * ������ݴ���õ�ҽ����ݱ��
    * @param ctzCode String
    * @return TParm
    */
   public TParm getFlgByCtz(String ctzCode) {
       TParm data = new TParm();
       data.setData("CTZ_CODE", ctzCode);
       TParm result = query("getFlgByCtz", data);
       return result;
   }
   	/**
   	 * ���ҽ����ݴ���
	 * pangb 2012-2-10
   	 * @param ctzCode
   	 * @return
   	 */
   public TParm getNhiNoCtz(String ctzCode) {
	   TParm data = new TParm();
       data.setData("CTZ_CODE", ctzCode);
       TParm result = query("getNhiNoCtz", data);
       return result;
   }
   
   /**
    * ������ݴ���õ�������ҳ���
    * @param ctzCode String
    * @return TParm
    */
  public TParm getMroCtz(String ctzCode) {
      TParm data = new TParm();
      data.setData("CTZ_CODE", ctzCode);
      TParm result = query("getMroCtz", data);
      return result;
  }
}
