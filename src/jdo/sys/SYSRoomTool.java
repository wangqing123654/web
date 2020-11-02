package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class SYSRoomTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	private static SYSRoomTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static SYSRoomTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSRoomTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSRoomTool() {
		setModuleName("sys\\SYSRoomModule.x");
		onInit();
	}

	/**
	 * ��ѯ
	 * 
	 * @param room_no
	 * @return
	 */
	public TParm selectdata(String room_no) {
		TParm parm = new TParm();
		if (room_no != null && room_no.length() > 0) {
			parm.setData("ROOM_CODE", room_no);
		}
		TParm result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
