package action.hrm;

import jdo.hrm.HRMPersonReportTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>Title: ���������˱�������</p>
 * 
 * <p>Description: ���������˱�������</p>
 * 
 * <p>Copyright: javahis 20090922</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMPersonReportAction extends TAction {
	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm){
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("��������");
			return result;
		}
		// ȡ������
		TConnection conn = getConnection();
		result = HRMPersonReportTool.getInstance().onSave(parm, conn);
		if(result.getErrCode()!=0){
			conn.rollback();
			conn.close();
		}
		conn.commit();
		conn.close();
		return result;
	}
}
