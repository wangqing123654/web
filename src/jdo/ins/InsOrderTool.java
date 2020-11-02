package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsOrderTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static InsOrderTool instanceObject;
    /**
     * �õ�ʵ��
     * @return RuleTool
     */
    public static InsOrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InsOrderTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public InsOrderTool() {
        setModuleName("ins\\INSORDER.x");
        onInit();
    }
    /**
     * ��ѯ���÷ָ����
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderTable(TParm parm) {
        TParm result = new TParm();
        result = query("queryOrderTable", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�ϲ��ķָ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm queryGroupData(TParm parm){
        TParm result = new TParm();
        result = query("queryGroupData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ���Ǹ���ͼ
     * @param parm TParm
     * @return TParm
     */
    public TParm queryView(TParm parm){
        TParm result = new TParm();
        result = query("queryView", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯҽ����ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataXA(TParm parm){
        TParm result = new TParm();
        result = query("queryOrderMDataXA", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯҽ����ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataSX(TParm parm){
        TParm result = new TParm();
        result = query("queryOrderMDataSX", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯҽ����ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataZG(TParm parm){
        TParm result = new TParm();
        result = query("queryOrderMDataZG", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯҽ����ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOrderMDataGX(TParm parm) {
        TParm result = new TParm();
        result = query("queryOrderMDataGX", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ�����÷ָ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteInsOrderData(TParm parm,TConnection conn){
        TParm result = new TParm();
        //System.out.println("ɾ�����÷ָ����"+parm);
        result = this.update("deleteInsOrderData",parm,conn);
        return result;
    }
    /**
     * ��ѯ���
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInsOrderSEQNO(TParm parm){
        TParm result = new TParm();
        result = this.query("selectInsOrderSEQNO",parm);
       // System.out.println("��ѯ���:"+result);
        return result;
    }
    /**
     * ����ҽ�����÷ָ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveInsOrder(TParm parm,TConnection conn){
        TParm result = new TParm();
        //System.out.println("����ҽ�����÷ָ����"+parm);
        result = this.update("saveInsOrder",parm,conn);
        return result;
    }
    /**
     * ��ѯԤ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm queryAdmInpTOTAL_DEPOSIT(TParm parm){
        TParm result = new TParm();
       // System.out.println("��ѯԤ�������" + parm);
        result = this.query("queryAdmInpTOTAL_DEPOSIT",parm);
        return result;
    }
    /**
    * ���ͨ��
    * @param parm TParm
    * @return TParm
    */
   public TParm onSaveInfo(TParm parm){
       TParm result = new TParm();
       result = this.update("queryInsPaymInsStatus",parm);
       return result;
   }
   /**
   * ����ͨ��
   * @param parm TParm
   * @return TParm
   */
  public TParm onSaveInfoJS(TParm parm){
      TParm result = new TParm();
      result = this.update("queryInsPaymInsStatusJS",parm);
      return result;
  }
  /**
   * ����ͨ��
   * @param parm TParm
   * @return TParm
   */
  public TParm onSaveInfoJS(TParm parm,TConnection conn){
      TParm result = new TParm();
      result = this.update("queryInsPaymInsStatusJS",parm,conn);
      return result;
  }

   /**
    * ��ѯ��ӡ����
    * @return TParm
    */
   public TParm queryPrintData(TParm parm){
       TParm result = new TParm();
       result = this.query("queryPrintData",parm);
       return result;
   }
   /**
    * ��ѯ�������
    * @param parm TParm
    * @return TParm
    */
   public TParm checkInsStatus(TParm parm){
       TParm result = new TParm();
       result = this.query("checkInsStatus",parm);
       return result;
   }
   /**
    * ��ѯ�Ƿ��г�Ժ�˵�
    * @param parm TParm
    * @return TParm
    */
   public TParm checkIbsStatus(TParm parm){
       TParm result = new TParm();
       result = this.query("checkIbsStatus",parm);
       return result;
   }

}
