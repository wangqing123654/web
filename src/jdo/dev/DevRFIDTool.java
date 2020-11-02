package jdo.dev;

import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: RFID�豸������</p>
 *
 * <p>Description:RFID�豸������ </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author wanglong 2012.12.03
 * @version 1.0
 */
public class DevRFIDTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static DevRFIDTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return MainStockRoomTool
	 */
	public static DevRFIDTool getInstance() {
		if (instanceObject == null)
			instanceObject = new DevRFIDTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public DevRFIDTool() {
		setModuleName("dev\\DevRFIDModule.x");
		onInit();
	}

	/**
	 * ��ѯRFID�豸
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectRFIDDevice(TParm parm) {
		TParm result = new TParm();
		result = query("selectRFIDDevice", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ��ѯRFID�豸
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectRFID(TParm parm) {
		TParm result = new TParm();
		result = query("selectRFID", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����RFID�豸
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insertRFIDDevice(TParm parm) {
		TParm result = update("insertRFIDDevice", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����RFID�豸
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm updateRFIDDevice(TParm parm) {
		TParm result = update("updateRFIDDevice", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����RFID�豸
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm updateRFIDRunStatus(TParm parm) {
		TParm result = update("updateRFIDRunStatus", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��RFID�豸
	 * @param rfidCode String
	 * @return TParm
	 */
	public TParm deleteRFIDDevice(String rfidCode) {
		TParm parm = new TParm();
		parm.setData("RFID_CODE", rfidCode);
		TParm result = update("deleteRFIDDevice", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯRFID�豸��־
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm selectRFIDLogs(TParm parm) {
		TParm result = new TParm();
		result = query("selectRFIDLogs", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * RFID�豸������־
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insertRFIDLog(TParm parm) {
        String logCode = SystemTool.getInstance().getNo("ALL", "DEV", "RFID_CODE", "LOG_CODE"); //����ȡ��ԭ��
        parm.setData("RFID_LOG_CODE", logCode);
		TParm result = update("insertRFIDLog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ��RFID�豸��־
	 * @param logCode String
	 * @return TParm
	 */
	public TParm deleteRFIDLog(String logCode) {
		TParm parm = new TParm();
		parm.setData("RFID_LOG_CODE", logCode);
		TParm result = update("deleteRFIDLog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����Ip��ѯ�������˿�
	 * @param parm
	 * @return
	 */
	public TParm selectRFIDByIP(TParm parm) {
		TParm result = query("selectRFIDByIP", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����Ip��ѯ׷���豸����
	 * @param parm
	 * @return
	 */
	public TParm selectDeptInfoByIP(TParm parm) {
		TParm result = query("selectDeptInfoByIP", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
