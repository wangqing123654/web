package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:·��ҽ���滻
 * </p>
 * 
 * <p>
 * Description: �ٴ�·��ҽ���滻����
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
 * @author pangben 2014-8-28
 * @version 1.0
 */
public class CLPOrderReplaceTool extends TJDOTool {
	public CLPOrderReplaceTool() {
		setModuleName("clp\\CLPOrderReplaceModule.x");
		onInit();
	}
	 /**
     * ʵ��
     */
    public static CLPOrderReplaceTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static CLPOrderReplaceTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPOrderReplaceTool();
        return instanceObject;
    }
	/**
	 * ��ѯ�滻ҽ��
	 * @param parm
	 * @return
	 */
	public TParm selectComorderReplace(TParm parm){
		TParm result=query("selectComorderReplace",parm);
		return result;
	}
	/**
	 * ��ѯ�ٴ�·��ҽ��
	 * @param parm
	 * @return
	 */
	public TParm selectClpPack(TParm parm){
		TParm result=query("selectClpPack",parm);
		return result;
	}
	/**
	 * ����ҽ������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPack(TParm parm,TConnection conn){
		TParm result=update("updateClpPack",parm,conn);
		return result;
	}
	/**
	 * ������ʷҽ������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPackHistory(TParm parm,TConnection conn){
		TParm result=update("updateClpPackHistory",parm,conn);
		return result;
	}
	
	/**
	 * ��ѯ�ٴ�·����Ŀ���е�ҽ��
	 * @param parm
	 * @return
	 */
	public TParm selectClpPackSum(TParm parm){
		TParm result=query("selectClpPackSum",parm);
		return result;
	}
	/**
	 * �޸İ汾����
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPackVersion(TParm parm,TConnection conn){
		TParm result=update("updateClpPackVersion",parm,conn);
		return result;
	}
	/**
	 * �޸��ٴ�·���汾����
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpBscinfoVersion(TParm parm,TConnection conn){
		TParm result=update("updateClpBscinfoVersion",parm,conn);
		return result;
	}
	
	/**
	 * �����ʷ�ٴ�·��ҽ������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertClpPackHistory(TParm parm,TConnection conn){
		TParm result=update("insertClpPackHistory",parm,conn);
		return result;
	}
	
	/**
	 * �޸���ʷ��汾����
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateClpPackHistoryVersion(TParm parm,TConnection conn){
		TParm result=update("updateClpPackHistoryVersion",parm,conn);
		return result;
	}
}
