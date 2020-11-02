package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 过敏史tool
*
* <p>Description: 过敏史tool</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20080911
* @version 1.0
*/
public class DrugAllergyTool extends TJDOTool {
	/**
     * 实例
     */
    public static DrugAllergyTool instanceObject;
    /**
     * 得到实例
     * @return DrugAllergyTool
     */
    public static DrugAllergyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DrugAllergyTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public DrugAllergyTool() {
        setModuleName("opd\\OPDDrugAllergyModule.x");

        onInit();
    }

    /**
     * 新增医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertdata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
	 /**
     * 判断是否存在数据
     * @param TParm parm
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsOrder(TParm parm){
        return getResultInt(query("existsOrder",parm),"COUNT") > 0;
    }
    /**
     * 更新数据
     * @param parm
     * @return
     */
    public TParm updatedata(TParm parm, TConnection connection){
    	TParm result = new TParm();
        result = update("updatedata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除数据
     * @param parm
     * @return
     */
    public TParm deletedata(TParm parm, TConnection connection){
    	TParm result = new TParm();
        result = update("deletedata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检索数据
     * @param parm
     * @return
     */
    public TParm selectdata(TParm parm){
        TParm result = query("selectdata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //System.out.println("allergyresult"+result);
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
			result = this.deletedata(inParm,connection);
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
			result = this.insertdata(inParm,connection);
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
			result = this.updatedata(inParm,connection);
			if (result.getErrCode() < 0)
				return result;
		}
		return result;
	}

	/**
	 * odo异动主入口
	 * @param parm
	 * @param connection
	 * @return result 保存结果
	 */
	public TParm onSave(TParm parm, TConnection connection) {
		TParm result = onDelete(parm.getParm(DrugAllergyList.DELETED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onInsert(parm.getParm(DrugAllergyList.NEW),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = onUpdate(parm.getParm(DrugAllergyList.MODIFIED),connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
