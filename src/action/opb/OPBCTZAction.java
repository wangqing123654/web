package action.opb;

import jdo.ibs.IBSTool;
import jdo.opb.OPBCTZTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>Title: 门诊身份修改</p>
 *
 * <p>Description: 门诊身份修改</p>
 *
 * <p>Copyright: Copyright (c) Bluecore</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author caowl 20140213
 * @version 1.0
 */
public class OPBCTZAction extends TAction{

	/**
	 * 修改身份
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author caowl
	 */
	public TParm updBill(TParm parm) {

		TConnection connection = getConnection();
		TParm result = new TParm();
		result = OPBCTZTool.getInstance().updBill(parm, connection);

		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();

		return result;
	}	
}
