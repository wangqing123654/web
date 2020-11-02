package action.spc;

import jdo.spc.SYSDictionaryPatchTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * ͳһ����������
 * 
 * @author shendr 20131031
 * 
 */
public class SYSDictionaryAction extends TAction {

	/**
	 * ����IND_STOCKM & IND_STOCK�� ACTIVE_FLG
	 * 
	 * @return
	 */
	public TParm updateActiveFlg(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = SYSDictionaryPatchTool.getInstance().updateActiveFlgM(parm,
				conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		result = SYSDictionaryPatchTool.getInstance().updateActiveFlgD(parm,
				conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

}
