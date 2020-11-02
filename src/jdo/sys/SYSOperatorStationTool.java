package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: �û�����</p>
*
* <p>Description:�û�����</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800901
* @version 1.0
*/
public class SYSOperatorStationTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSOperatorStationTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSOperatorStationTool
     */
    public static SYSOperatorStationTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSOperatorStationTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSOperatorStationTool()
    {
        setModuleName("sys\\SYSOperatorStationModule.x");
        onInit();
    }
    /**
     * ��ʼ�����棬��ѯ���е�����
     * @return TParm
     */
    public TParm select(TParm parm){
         TParm result = query("select",parm);
         if(result.getErrCode() < 0)
         {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
	 * ����ҽ��
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insert(TParm parm,TConnection connection) {
		TParm result = new TParm();
		result = update("insert", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ɾ��ҽ��
	 * @param parm TParm
	 * @return TParm
	 */
    public TParm delete(TParm parm,TConnection connection) {
		TParm result = update("delete", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}
	/**
	 * ��������
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm update(TParm parm,TConnection connection) {
		TParm result = new TParm();
		result = update("update", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ɾ��
	 * @param parm
	 * @return result
	 */
	public TParm onDelete(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.delete(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}

		return result;
	}

	/**
	 * ����
	 * @param parm
	 * @return result
	 */
	public TParm onInsert(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.insert(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * ����
	 * @param parm
	 * @return result
	 */
	public TParm onUpdate(TParm parm, TConnection connection) {
		int count = parm.getCount();
		TParm result = new TParm();
		for (int i = 0; i < count; i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, parm, i);
			result = this.update(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * Operator�춯�����
	 * @param parm
	 * @param connection
	 * @return result ������
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(JDOStationList.DELETED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(JDOStationList.NEW),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onUpdate(parm.getParm(JDOStationList.MODIFIED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

        /**
         * ��ʼ�����棬��ѯ���е�����
         * @return TParm
         */
public TParm selectStationClinicCode(TParm parm){
     TParm result = query("selectStationClinicCode",parm);
     if(result.getErrCode() < 0)
     {
         err("ERR:" + result.getErrCode() + result.getErrText() +
             result.getErrName());
         return result;
     }
     return result;
}

}
