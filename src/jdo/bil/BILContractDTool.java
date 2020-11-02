package jdo.bil;



import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: ������Ϣά��
 * </p>
 * 
 * <p>
 * Description:������Ϣά��
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
public class BILContractDTool extends TJDOTool{

	 public BILContractDTool() {
	        setModuleName("bil\\BILContractDModule.x");
	        onInit();
	    }
	    /**
	    * ʵ��
	    */
	   public static BILContractDTool instanceObject;

	    /**
	     * �õ�ʵ��
	     * @return IBSTool
	     */
	    public static BILContractDTool getInstance() {
	        if (instanceObject == null)
	            instanceObject = new BILContractDTool();
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
	      * �޸Ĳ�������
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm updateDataPat(TParm parm, TConnection connection) {
	         TParm result = this.update("updateDataPat", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }

	         return result;
	     }


	     /**
	      * ���벡����Ϣ
	      * @param parm TParm
	     * @param connection 
	      * @return TParm
	      */
	     public TParm insertDataPat(TParm parm, TConnection connection) {
	         TParm result = this.update("insertDataPat", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
	     /**
	      * ɾ����������
	     * @param connection 
	      * @param ACIRecordNo String
	      * @return TParm
	      */
	     public TParm deleteDataPat(TParm parm, TConnection connection) {
	         TParm result = update("deleteDataPat", parm);
	         if (result.getErrCode() < 0) {
	             err("ERR:" + result.getErrCode() + result.getErrText() +
	                 result.getErrName());
	             return result;
	         }
	         return result;
	     }
}
