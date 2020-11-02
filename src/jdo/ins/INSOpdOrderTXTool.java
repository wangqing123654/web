package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:ҽ�������÷ָ�����
 * </p>
 * 
 * <p>
 * Description: ҽ�������÷ָ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdOrderTXTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INSOpdOrderTXTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSOpdApproveTool
	 */
	public static INSOpdOrderTXTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSOpdOrderTXTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INSOpdOrderTXTool() {
		setModuleName("ins\\INSOpdOrderTXModule.x");
		onInit();
	}

	/**
	 * ����ָ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSOpdOrder(TParm parm, TConnection conn) {
		TParm result = update("insertINSOpdOrder", parm, conn);
		return result;
	}

	/**
	 * ɾ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteINSOpdOrder(TParm parm, TConnection conn) {
		TParm result = update("deleteINSOpdOrder", parm, conn);
		return result;
	}

	/**
	 * ��ѯ���SEQ_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectMAXSeqNo(TParm parm) {
		TParm result = query("selectMAXSeqNo", parm);
		return result;
	}
}
