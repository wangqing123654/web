package action.hrm;

import jdo.hrm.HRMCompanyTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
*
* <p>Title: ���������㶯����</p>
*
* <p>Description: ���������㶯����</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMDepositAction extends TAction {
	/**
	 * ���涯��
	 * @param parm
	 * @return
	 */
	public TParm onSaveDeposit(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("��������");
			return result;
		}
		// ȡ������
		TConnection conn = getConnection();
		result = HRMCompanyTool.getInstance().onSaveContract(parm, conn);
		if(result.getErrCode()!=0){
			conn.rollback();
			conn.close();
		}
		conn.commit();
		conn.close();
		return result;
	}
}
