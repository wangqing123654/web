package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 用户管理</p>
*
* <p>Description:用户管理</p>
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
     * 实例
     */
    public static SYSOperatorStationTool instanceObject;
    /**
     * 得到实例
     * @return SYSOperatorStationTool
     */
    public static SYSOperatorStationTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSOperatorStationTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSOperatorStationTool()
    {
        setModuleName("sys\\SYSOperatorStationModule.x");
        onInit();
    }
    /**
     * 初始化界面，查询所有的数据
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
	 * 新增医嘱
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
	 * 删除医嘱
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
	 * 更新数据
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
	 * 删除
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
	 * 插入
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
	 * 更新
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
	 * Operator异动主入口
	 * @param parm
	 * @param connection
	 * @return result 保存结果
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
         * 初始化界面，查询所有的数据
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
