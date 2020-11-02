package action.med;



import jdo.med.MedNodifyTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.javahis.ui.main.SystemMainControl;

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
public class MedNodifyAction extends TAction{
	
	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm  onInsert(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = MedNodifyTool.getInstance().insertData(parm, connection);
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
	public TParm  onUpdateData(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = MedNodifyTool.getInstance().onUpdateData(parm, connection) ;
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
	public TParm  onUpdateName(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = MedNodifyTool.getInstance().onUpdateName(parm, connection) ;
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
	 * ����״̬
	 * SEND_STAT ���ѷ��͸���Ϊ�Ѷ�
	 * 1------2
	 * @param parm
	 * @return
	 */
	public TParm  onUpdateStat(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = MedNodifyTool.getInstance().onUpdateStat(parm,connection) ;
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
	        result = MedNodifyTool.getInstance().deleteData(parm, connection) ;
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
	 * ����˵��ÿͻ��˴���
	 * @param parm
	 * @return
	 */
	public TParm  query(TParm parm){
		  TParm result  = MedNodifyTool.getInstance().query(parm) ;
	return result ;
	}
	/**
	 * ����˵��ÿͻ��˴���
	 * @param parm
	 * @return
	 */
	public TParm  queryAll(TParm parm){
		  TParm result  = MedNodifyTool.getInstance().queryAll(parm) ;
	return result ;
	}
	/**
	 * ����Ƿ��м��顢�����
	 * @param parm
	 * @return
	 */
	public TParm  selectResultOut(TParm parm){
		  TParm result  = MedNodifyTool.getInstance().selectResultOut(parm) ;
	return result ;
	}	
}
