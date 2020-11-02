package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:�龫ҩ������
 * </p>
 * 
 * <p>
 * Description:�龫ҩ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */
public class SPCPoisonTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	private static SPCPoisonTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatAdmTool
	 */
	public static SPCPoisonTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCPoisonTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SPCPoisonTool() {
		setModuleName("spc\\SPCPoisonBaleModule.x");
		onInit();
	}

	/**
	 * ��ѯ���������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfo(TParm tparm) {
		TParm result = new TParm();
		result = query("queryInfo", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ���������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryInfoVerifyin(TParm tparm) {
		TParm result = new TParm();
		result = query("queryInfoVerifyin", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯ��������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainer(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainer", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainerNum(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainerNum", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryOrgType() {
		TParm result = new TParm();
		result = query("queryOrgType");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainerCount(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainerCount", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ҩƷ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertContainer(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertContainer", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯfid
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryRfid(TParm tparm) {
		TParm result = new TParm();
		result = query("queryRfid", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��������״̬
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainer(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainer", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�Ѵ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryContainerOrder(TParm tparm) {
		TParm result = new TParm();
		result = query("queryContainerOrder", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��������״̬
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerD(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainerD", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��������״̬
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOldContainerM(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateOldContainerM", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ���������Ѵ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerNum(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainerNum", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ���������Ѵ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateContainerNumVerifyin(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateContainerNumVerifyin", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ӽ��ױ�
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm insertToxicm(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicm", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ӽ��ױ�
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm insertToxicmJj(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicmJj", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	
	/**
	 * ��ӽ��ױ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertToxicd(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicd", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�Ѵ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryYdb(TParm tparm) {
		TParm result = new TParm();
		result = query("queryYdb", tparm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ���½��ױ�
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm updateToxicd(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateToxicd", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�Ѵ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateToxicm(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateToxicm", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ӽ��ױ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertToxicdJj(TParm tparm, TConnection connection) {
		TParm result = new TParm();
		result = update("insertToxicdJj", tparm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}


}
