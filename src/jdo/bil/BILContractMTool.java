package jdo.bil;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: ��ͬ��λ��Ϣά��
 * </p>
 * 
 * <p>
 * Description: ��ͬ��λ��Ϣά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILContractMTool extends TJDOTool{

	 public BILContractMTool() {
	        setModuleName("bil\\BILContractMModule.x");
	        onInit();
	    }
	    /**
	    * ʵ��
	    */
	   public static BILContractMTool instanceObject;

	    /**
	     * �õ�ʵ��
	     * @return IBSTool
	     */
	    public static BILContractMTool getInstance() {
	        if (instanceObject == null)
	            instanceObject = new BILContractMTool();
	        return instanceObject;
	    }
	    /**
	     * ��ʾ����
	     * @param parm TParm
	     */
	    public TParm selectData(String sqlName,TParm parm){
	        TParm result = query(sqlName, parm);
	        if (result.getErrCode() < 0) {
	            err(result.getErrCode() + " " + result.getErrText());
	            return result;
	        }
	        return result;
	    }
	    /**
	      * ɾ����ͬ��λ����
	     * @param connection 
	      * @param ACIRecordNo String
	      * @return TParm
	      */
	     public TParm deleteData(TParm parm, TConnection connection) {
	         TParm result = update("deleteData", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
	   

	     /**
	      * �жϺ�ͬ��λ�����Ƿ����
	      * @param parm TParm
	      * @return TParm
	      */
	     public TParm checkDataExist(TParm parm) {
	         TParm result = this.query("checkDataExist", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }

	     /**
	      * �޸ĺ�ͬ��λ����
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm updateData(TParm parm, TConnection connection) {
	         TParm result = this.update("updateData", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }

	         return result;
	     }
	  /**
	   * ���º�ͬ��λԤ����
	   * */
	     public TParm updPrePay(TParm parm,TConnection connection){
	    	 TParm result = this.update("updPrePay",parm);
	    	 if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }

	         return result;
	     }

	     /**
	      * �����ͬ��λ��Ϣ
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm insertData(TParm parm, TConnection connection) {
	         TParm result = this.update("insertData", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
	  
}
