package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.action.TAction;

import jdo.ins.INSBlackListTool;
import jdo.ins.INSDRQualifyLogTool;
import jdo.ins.INSNoticeTool;
import jdo.ins.INSTool;

/**
 * 
 * <p>
 * Title: ְҵҽʦ֤��Ź���
 * </p>
 * 
 * <p>
 * Description:ְҵҽʦ֤��Ź���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSDRQualifyLogAction extends TAction {
	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		TParm result = new TParm();
		if (parm == null)
			return result.newErrParm(-1, "����Ϊ��");
		TConnection connection = null;
		try {
			connection = getConnection();
			result = INSDRQualifyLogTool.getInstance().insertdata(parm, connection);
			if (result == null || result.getErrCode() < 0) {
				return result;
			}
			connection.commit();
		} catch (Exception ex) {

		} finally {
			if (connection != null) {
				connection.close();
			}

		}
		return result;

	}

}
