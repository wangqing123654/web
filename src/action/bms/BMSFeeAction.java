package action.bms;

import jdo.adm.ADMTool;
import jdo.bms.BMSFeeTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class BMSFeeAction extends TAction{
	
	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm  onInsert(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = BMSFeeTool.getInstance().insertData(parm, connection);
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}

	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm  onUpdate(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = BMSFeeTool.getInstance().updateData(parm, connection) ;
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}

	/**
	 * ɾ��
	 * @param parm
	 * @return
	 */
	public TParm  onDelete(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = BMSFeeTool.getInstance().deleteData(parm, connection) ;
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}	
//	/**
//	   * Ѫ���Զ��Ʒѷ��ò���ibs_ordd��ibs_ordm
//	   */
//	public TParm onAutoBmsFee(TParm parm){
//		 TConnection conn = getConnection();
//		 TParm result = new TParm();
//	        result = BMSFeeTool.getInstance().deleteData(parm, conn) ;
//	        if (result.getErrCode() < 0) {
//	            err(result.getErrName() + " " + result.getErrText());
//	            conn.rollback() ;
//	            conn.close();
//	            return result;
//	        }
//	        conn.commit();
//	        conn.close();
//	        return result;
//	}
	
}
