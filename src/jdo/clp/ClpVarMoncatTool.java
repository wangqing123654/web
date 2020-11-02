package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �����ֵ�</p>
 *
 * <p>Description: �����ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 2011-05-02
 * @version 1.0
 */
public class ClpVarMoncatTool extends TJDOTool{

    /**
     * ʵ��
     */
    private static ClpVarMoncatTool instanceObject;
    /**
     * ��ʼ�������
     * @return ClpVarMoncatTool
     */
    public static ClpVarMoncatTool getInstance() {
        if (null == instanceObject)
            instanceObject = new ClpVarMoncatTool();
        return instanceObject;
    }

    /**
     * ���췽��
     */
    public ClpVarMoncatTool() {
        this.setModuleName("clp\\ClpVarMoncatModule.x");
        onInit();
    }
    /**
    * ��ѯ����
    * ��һ������е�����
    * @param parm TParm
    * @return TParm
    */
   public TParm getQueryClpVarMoncat(TParm parm) {
       TParm result = this.query("queryClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
   * ��ѯ����
   * �ڶ�������е�����
   * @param parm TParm
   * @return TParm
   */
  public TParm getQueryClpVariance(TParm parm) {
      TParm result = this.query("queryClpVariance", parm);
      return boolTParmNUll(result);
  }

   /**
    * ������ݷ���
    * ClpVarMoncat����������������
    * @param parm TParm
    * @return TParm
    */
   public TParm saveClpVarMoncat(TParm parm) {
       TParm result = this.update("insertClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
   * ������ݷ���
   * ClpVariance���������
   * @param parm TParm
   * @return TParm
   */
  public TParm saveClpVariance(TParm parm,TConnection conn) {

      TParm result = this.update("insertClpVariance", parm,conn);
      return boolTParmNUll(result);
  }

   /**
    * �޸����ݷ���
    * ClpVarMoncat���޸����ݣ�������
    * @param parm TParm
    * @return TParm
    */
   public TParm updateClpVarMoncat(TParm parm){
       TParm result = this.update("updateClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
    * �޸����ݷ���
    * ClpVariance���޸����ݣ��ӱ�
    * @param parm TParm
    * @return TParm
    */
   public TParm updateClpVariance(TParm parm,TConnection conn){
       TParm result = this.update("updateClpVariance", parm,conn);
       return boolTParmNUll(result);
   }

   /**
    * ɾ�����ݷ���
    * @param parm TParm
    * @return TParm
    */
   public TParm deleteClpVarMoncat(TParm parm){
       TParm result = this.update("deleteClpVarMoncat", parm);
       return boolTParmNUll(result);
   }
   /**
    * ��ѯ���û��Ƿ����
    * �˱���Ҫ���������ж�
    * @return TParm
    */
   public TParm selectIsExist(TParm parm) {
       TParm result = this.query("selectIsExist", parm);
       return boolTParmNUll(result);
   }
   /**
    * ���ݿ�����Ժ��ж��Ƿ���ִ��󷽷�
    * @param result TParm
    * @return TParm
    */
   public TParm boolTParmNUll(TParm result) {
       //����0�ǲ�����
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return null;
       }
       return result;
   }

}
