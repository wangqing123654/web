package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ·���ײ�ҵ����
 * </p>
 * 
 * <p>
 * Description: ·���ײ�ҵ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20150810
 * @version 1.0
 */
public class CLPPackAgeTool extends TJDOTool {
	public CLPPackAgeTool() {
		setModuleName("clp\\CLPPackAgeModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	public static CLPPackAgeTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return IBSTool
	 */
	public static CLPPackAgeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new CLPPackAgeTool();
		return instanceObject;
	}
	/**
	 * 
	* @Title: onQuery
	* @Description: TODO(��ѯ����)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm queryPackAge(TParm parm){
		TParm result=this.query("queryPackAge",parm);
		return result;
	}
	/**
	 * 
	* @Title: getMaxSeqNo
	* @Description: TODO(��ѯ������)
	* @author Dangzhang
	* @param parm
	* @return
	* @throws
	 */
	public TParm getMaxSeqNo(TParm parm){
		TParm result=this.query("getMaxSeqNo",parm);
		return result;
	}
	/**
	 * 
	* @Title: onInsert
	* @Description: TODO(��Ӳ���)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm onInsert(TParm parm){
		TParm result=this.update("onInsert",parm);
		return result;
	}
	/**
	 * 
	* @Title: onUpdate
	* @Description: TODO(�޸Ĳ���)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm onUpdate(TParm parm){
		TParm result=this.update("onUpdate",parm);
		return result;
	}
	/**
	 * 
	* @Title: onDelete
	* @Description: TODO(ɾ��)
	* @author pangben
	* @param parm
	* @return
	* @throws
	 */
	public TParm onDelete(TParm parm){
		TParm result=this.update("onDelete",parm);
		return result;
	}
}
