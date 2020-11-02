package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �������ֵ�</p>
 *
 * <p>Description: �������ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110429
 * @version 1.0
 */
public class ClpchkTypeTool extends TJDOTool {

    /**
     * ʵ��
     */
    private static ClpchkTypeTool instanceObject;
    /**
     * ��ʼ�������
     * @return ClpchkTypeTool
     */
    public static ClpchkTypeTool getInstance() {
        if (null == instanceObject)
            instanceObject = new ClpchkTypeTool();
        return instanceObject;
    }
    /**
    * ���췽��
    */
   public ClpchkTypeTool() {
       this.setModuleName("clp\\ClpchkTypeModule.x");
       onInit();
   }
   /**
    * ������ݷ���
    * @param parm TParm
    * @return TParm
    */
   public TParm saveClpchkType(TParm parm) {
       TParm result = this.update("insertClpchkType", parm);
       return boolTParmNUll(result);
   }
   /**
    * �޸����ݷ���
    * @param parm TParm
    * @return TParm
    */
   public TParm updateClpchkType(TParm parm){
       TParm result = this.update("updateClpchkType", parm);
       return boolTParmNUll(result);
   }
   /**
    * ɾ�����ݷ���
    * @param parm TParm
    * @return TParm
    */
   public TParm deleteClpchkType(TParm parm){
       TParm result = this.update("deleteClpchkType", parm);
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
   /**
    *
    * @param parm TParm
    * @return TParm
    */
   public TParm selectAllCheckCode(TParm parm) {
       TParm result = this.query("selectAll", parm);
       return boolTParmNUll(result);
   }

}
