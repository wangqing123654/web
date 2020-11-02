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
public class InsPaydTool extends TJDOTool {
    /**
    * ʵ��
    */
   public static InsPaydTool instanceObject;
   /**
    * �õ�ʵ��
    * @return RuleTool
    */
   public static InsPaydTool getInstance() {
       if (instanceObject == null)
           instanceObject = new InsPaydTool();
       return instanceObject;
   }
   /**
    * ������
    */
   public InsPaydTool() {
       setModuleName("ins\\INSPAYD.x");
       onInit();
   }
   /**
    * ��ѯ�걨������ϸ
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsPayd(TParm parm){
       TParm result = new TParm();
       result = query("queryInsPayd", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯͳ���סԺ����
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsOrderPrice(TParm parm){
       TParm result = new TParm();
       result = query("queryInsOrderPrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯ���÷ָ�������
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsOrderList(TParm parm){
       TParm result = new TParm();
          result = query("queryInsOrderList", parm);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;
   }
   /**
    * ��ѯ֧����׼����
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsRuel(TParm parm){
       TParm result = new TParm();
       result = query("queryInsRuel", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯ����޼�֧������
    * @param parm TParm
    * @return TParm
    */
   public TParm queryLimit(TParm parm){
       TParm result = new TParm();
       result = query("queryLimit", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯ�ۼ�֧��
    * @param parm TParm
    * @return TParm
    */
   public TParm queryAddpay(TParm parm){
       TParm result = new TParm();
       result = query("queryAddpay", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ȫΪN�����
    * @param parm TParm
    * @return TParm
    */
   public TParm queryAllNo(TParm parm){
       TParm result = new TParm();
       result = query("queryAllNo", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ������ȫ��֧��
    * @param parm TParm
    * @return TParm
    */
   public TParm qeuryAllPrice(TParm parm){
       TParm result = new TParm();
       result = query("qeuryAllPrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��������INS_PAYD
    * @param parm TParm
    * @return TParm
    */
   public TParm insertInsPayDList(TParm parm,TConnection conn){
       TParm result = new TParm();
        result = update("insertInsPayDList",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
   }
   /**
    * ɾ����������
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    */
   public TParm deleteInsPaydData(TParm parm,TConnection conn){
       TParm result = new TParm();
       result = update("deleteInsPaydData",parm,conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯ���߽��
    * @param parm TParm
    * @return TParm
    */
   public TParm queryStartLinePrice(TParm parm){
       TParm result = new TParm();
       result = query("queryStartLinePrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯͳ��
    * @param parm TParm
    * @return TParm
    */
   public TParm queryPlanPrice(TParm parm){
       TParm result = new TParm();
      result = query("queryPlanPrice", parm);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
   }
   /**
    * ȫΪ���������֧����׼
    * @param parm TParm
    * @return TParm
    */
   public TParm queryFSRule(TParm parm){
       TParm result = new TParm();
       result = query("queryFSRule", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ����
    * @return TParm
    */
   public TParm updateInsPaymData(TParm parm){
       TParm result = new TParm();
       result = update("updateInsPaymData",parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯ��ӡ����
    * @param parm TParm
    * @return TParm
    */
   public TParm queryPrintData(TParm parm){
       TParm result = new TParm();
        result = query("queryPrintData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
   }
   /**
    * �����ܽ��
    * @param parm TParm
    * @return TParm
    */
   public TParm queryInsPaydAllPrice(TParm parm){
       TParm result = new TParm();
       result = query("queryInsPaydAllPrice", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * ��ѯ�Ը�����
    * @param parm TParm
    * @return TParm
    */
   public TParm queryRate(TParm parm){
       TParm result = new TParm();
       result = query("queryRate", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * �õ��������
    * @param feeTypeCode String
    * @return String
    */
   public String getFeeType(String feeTypeCode){
       TParm result = new TParm();
       TParm action = new TParm();
       action.setData("FEE_TYPE",feeTypeCode);
       result = query("getFeeType",action);
       if(result.getErrCode()<0){
           err("ERR"+ result.getErrCode() + result.getErrText() +
               result.getErrName());
           return "";
       }
       return result.getData("FEE_TYPE_DESC",0).toString();
   }
   /**
    * �õ��������
    * @param parm TParm
    * @return TParm
    */
   public TParm getRPTType(TParm parm){
       TParm result = query("getRPTType",parm);
       if(result.getErrCode()<0){
           err("ERR"+ result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
}
