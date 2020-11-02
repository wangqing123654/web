package jdo.adm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:����ȼ� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMNursingClassTool extends TJDOTool {

    /**
   * ʵ��
   */
  public static ADMNursingClassTool instanceObject;

  /**
   * �õ�ʵ��
   * @return RegMethodTool
   */
  public static ADMNursingClassTool getInstance()
  {
      if(instanceObject == null)
          instanceObject = new ADMNursingClassTool();
      return instanceObject;
  }

  /**
   * ������
   */
  public ADMNursingClassTool()
  {
      setModuleName("adm\\ADMNursingClassModule.x");
      onInit();
  }

  /**
   * �����Һŷ�ʽ
   * @param regMethod String
   * @return TParm
   */
  public TParm insertdata(TParm parm) {
      TParm result = new TParm();
      String regMethod = parm.getValue("REGMETHOD_CODE");
      if(existsRegMethod(regMethod)){
          result.setErr(-1,"�Һŷ�ʽ "+" �Ѿ�����!");
          return result ;
      }
      result = update("insertdata", parm);
      // �жϴ���ֵ
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * ���¹Һŷ�ʽ
   * @param regMethod String
   * @return TParm
   */
  public TParm updatedata(TParm parm) {
      TParm result = update("updatedata", parm);
      // �жϴ���ֵ
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * ���ݹҺŷ�ʽ�����ѯ��ʽ��Ϣ(�Һ���)
   * @param regMethod String �Һŷ�ʽ����
   * @return TParm
   */
  public TParm selectdata(String regMethod){
      TParm parm = new TParm();
      regMethod += "%";
      parm.setData("REGMETHOD_CODE",regMethod);
      TParm result = query("selectdata",parm);
      // �жϴ���ֵ
      if(result.getErrCode() < 0)
      {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * ɾ���Һŷ�ʽ
   * @param regMethod String
   * @return boolean
   */
  public TParm deletedata(String regMethod){
      TParm parm = new TParm();
      parm.setData("REGMETHOD_CODE",regMethod);
      TParm result = update("deletedata",parm);
      // �жϴ���ֵ
      if(result.getErrCode() < 0)
      {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * �ж��Ƿ���ڹҺŷ�ʽ
   * @param regMethod String �Һŷ�ʽ����
   * @return boolean TRUE ���� FALSE ������
   */
  public boolean existsRegMethod(String regMethod){
      TParm parm = new TParm();
      parm.setData("REGMETHOD_CODE",regMethod);
      return getResultInt(query("existsRegMethod",parm),"COUNT") > 0;
  }

}
