package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:ҩƷ���� </p>
 *
 * <p>Description: �����й�ҩƷ���͵�ȫ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author JiaoY 2008-09-5
 * @version 1.0
 */

public class SYSPhaDoseTool extends TJDOTool{

    /**
   * ʵ��
   */
  public static SYSPhaDoseTool instanceObject;
  /**
   * �õ�ʵ��
   * @return PositionTool
   */
  public static SYSPhaDoseTool getInstance()
  {
      if(instanceObject == null)
          instanceObject = new SYSPhaDoseTool();
      return instanceObject;
  }
  /**
   * ������
   */
  public SYSPhaDoseTool()
  {
      setModuleName("sys\\SYSPhaDoseModule.x");
      onInit();
  }

  /**
   * ��ѯ����
   * @param doseCode String
   * @return TParm
   */

  public TParm selectdata(String doseCode, String dose_type){
      TParm parm = new TParm();

      if (doseCode != null && doseCode.length() > 0)
          parm.setData("DOSE_CODE", doseCode);
      if (dose_type != null && dose_type.length() > 0)
          parm.setData("DOSE_TYPE", dose_type);
      TParm result = query("selectall", parm);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
        return result;
    }

    public TParm existsData(TParm parm ){
//        System.out.println("��֤begin");
        TParm result = query("existsPHA_DOSE",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
             return result;
         }
//         System.out.println("��֤end--��"+result);
        return result;


    }
    /**
    * ����һ������
    * @param parm TParm
    * @return TParm
    */
   public TParm insertData(TParm parm){
//       System.out.println("����begin");
       TParm result = new TParm();
       result = update("insertdata",parm);
       if (result.getErrCode() < 0) {
    err("ERR:" + result.getErrCode() + result.getErrText() +
        result.getErrName());
//    System.out.println("����end");
     return result;
 }
 return result;

   }

   /**
   * ����һ������
   * @param parm TParm
   * @return TParm
   */
  public TParm updataData(TParm parm){
      TParm result = new TParm();
      result = update("updatedata", parm);
      if (result.getErrCode() < 0) {
   err("ERR:" + result.getErrCode() + result.getErrText() +
       result.getErrName());
    return result;
}

      return result;


  }

  /**
   * ɾ��һ������
   * @param parm TParm
   * @return TParm
   */
  public TParm deleteData(TParm parm){
      TParm result = update("deletedata",parm);
      if (result.getErrCode() < 0) {
   err("ERR:" + result.getErrCode() + result.getErrText() +
       result.getErrName());
    return result;
}

      return result ;


  }








}
