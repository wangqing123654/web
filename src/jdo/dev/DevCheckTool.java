package jdo.dev;

import java.util.List;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:�豸�̵� </p>
 *
 * <p>Description:�豸�̵� </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company:javahis </p>
 *
 * @author yuhb
 * @version 1.0
 */
public class DevCheckTool extends TJDOTool{
	/**
	 * ������
	 */
	public DevCheckTool() {
		setModuleName("dev\\DevCheckModule.x");
		onInit();
	}
	
	/**
	 * ʵ��
	 */
	private static DevCheckTool instanceObject;
	
	/**
	 * �õ�ʵ��
	 * @return
	 */
	public static DevCheckTool getInstance() {
		if (null == instanceObject) {
			instanceObject = new DevCheckTool();
		}
		return instanceObject;
	}
	
	/**
	 * ��ѯ��Ź����豸�����Ϣ
	 * @param parm
	 * @return
	 */
	public TParm selectSeqDevInf(TParm parm) {
		parm = query("selectSeqDevInf", parm);
		return parm;
	}
	
	/**
	 * �̵� ���¿������
	 * @param parm
	 * @return
	 */
	public TParm updateStockM(TParm parm, TConnection connection) {
		parm = update("updateStockM",parm,connection);
		return parm;
	}
	
	/**
	 * �̵� ���¿��ϸ��
	 * @param parm
	 * @return
	 */
	public TParm updateStockD(TParm parm, TConnection connection) {
		parm = update("updateStockD",parm,connection);
		return parm;
	}
	
	/**
	 * �̵� �����̵����
	 * @param parm
	 * @return  
	 */
	public TParm insertdevQtycheck(TParm parm, TConnection connection) {
		parm = update("insertdevQtycheck",parm,connection);
		return parm;  
	}
	
	
	/**
	 * �̵� �ۺϸ��¿��
	 * @param parmList
	 * @param connection
	 * @return
	 */
	public TParm updateStockByCheck(List<TParm> parmList, TConnection connection) {
		TParm parm = new TParm();
		for (TParm tParm : parmList) {
			parm = updateStockM(tParm, connection);
			if (parm.getErrCode() < 0) {
				return parm;
			}
			parm = updateStockD(tParm, connection);
			if (parm.getErrCode() < 0) {
				return parm;
			}  
			parm = insertdevQtycheck(tParm, connection);
			if (parm.getErrCode() < 0 ){
				return parm;
			}
			
		}
		return parm;
	}
}
