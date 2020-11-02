package action.spc;

import jdo.spc.SPCOperationRoomReturnTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title:��������ҩ�黹Action
 * </p>
 * 
 * <p>
 * Description:��������ҩ�黹Action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-08-12
 * @version 1.0
 */
public class SPCOperationRoomReturnAction extends TAction {

	/**
	 * ����IND_TOXICD�黹���(RETURN_FLG) IND_CONTAINERD�������
	 * 
	 * @param parm
	 */
	public TParm update(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		String toxic_id = parm.getValue("TOXIC_ID");
		String container_id = parm.getValue("CONTAINER_ID");
		String cabinet_id = parm.getValue("CABINET_ID");
		// ����IND_TOXICD
		result = SPCOperationRoomReturnTool.getInstance().updateINDToxicDInfo(
				toxic_id, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			conn.rollback();
			conn.close();
			return result;
		}
		// ����IND_CONTAINERD
		result = SPCOperationRoomReturnTool.getInstance()
				.updateINDContainerDInfo(container_id, toxic_id, cabinet_id,
						conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

}
