package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 患者保险信息维护
 * </p>
 * 
 * <p>
 * Description: 患者保险信息维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author sunqy 2014/05/13
 * @version 4.5
 */
public class MEMInsureInfoTool extends TJDOTool {

	public MEMInsureInfoTool() {
		setModuleName("mem\\MEMInsureInfoModule.x");
		onInit();
	}
	/**
	 * 实例
	 */
	public static MEMInsureInfoTool instanceObject;
	
	/**
	 * 得到实例
	 * 
	 * @return MEMPatRegisterTool
	 */
	public static MEMInsureInfoTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MEMInsureInfoTool();
		return instanceObject;
	}
	
	/**
	 * 保存信息(MEM_INSURE_INFO)
	 */
	public TParm insertMemInsureInfo(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.update("insertMemInsureInfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询信息(SYS_PATINFO)
	 */
	public TParm onQuerySysPatInfo(TParm parm, TConnection conn) {
		//System.out.println("------------->tool");
		TParm result = new TParm();
		result = this.query("onQuerySysPatInfo", parm, conn);
//		System.out.println("TOOL=result="+result);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询信息(MEM_INSURE_INFO)
	 */
	public TParm selectMemInsureInfo(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.query("selectMemInsureInfo", parm, conn);
//		System.out.println("TOOL=result="+result);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 删除数据(MEM_INSURE_INFO)
	 */
	public TParm deleteMemInsureInfo(TParm parm, TConnection conn) {
	       
        TParm result = update("deleteMemInsureInfo", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
	
	/**
	 * 修改数据(MEM_INSURE_INFO)
	 */
	public TParm updateMemInsureInfo(TParm parm, TConnection conn) {
		TParm result = new TParm();
//		System.out.println("updateMemData1="+parm);
		result = this.update("updateMemInsureInfo", parm, conn);
		System.out.println("tool1-result.getErrCode()="+result.getErrCode());
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
}
