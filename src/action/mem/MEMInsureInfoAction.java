package action.mem;

import jdo.mem.MEMInsureInfoTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
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
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author sunqy 2015.05.13
 * @version 4.5
 */
public class MEMInsureInfoAction extends TAction {
	
	/**
	 * 执行保存事务
	 */
	public TParm onSave(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			result = MEMInsureInfoTool.getInstance().insertMemInsureInfo(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	    }catch(Exception e){
        	e.printStackTrace();
        	err(e.getMessage());
        }
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 查询信息事务(SYS_PATINFO)
	 */
	public TParm onQuery(TParm parm){
		//System.out.println("---------------->action");
		TConnection conn = getConnection();
		TParm result = new TParm();
		try{
			result = MEMInsureInfoTool.getInstance().onQuerySysPatInfo(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
		}catch(Exception e){
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 查询信息事务(MEM_INSURE_INFO)
	 */
	public TParm onQueryMEM(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		try{
			result = MEMInsureInfoTool.getInstance().selectMemInsureInfo(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
		}catch(Exception e){
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 修改信息
	 */
	public TParm update(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		String sql = "SELECT * FROM MEM_INSURE_INFO WHERE MR_NO ='"+ 
				parm.getValue("MR_NO") +"' AND CONTRACTOR_CODE ='"+ 
				parm.getValue("CONTRACTOR_CODE") +"' AND INSURANCE_NUMBER ='"+ 
				parm.getValue("INSURANCE_NUMBER") +"'";
		TParm sqlParm  = new TParm(TJDODBTool.getInstance().select(sql));
		if(sqlParm.getCount()<0){
			try {//新增病患保险信息
				result = MEMInsureInfoTool.getInstance().insertMemInsureInfo(parm,conn);
		        if (result.getErrCode() < 0) {
		            err("ERR:" + result.getErrCode() + result.getErrText() +
		                result.getErrName());
		        }
		    }catch(Exception e){
	        	e.printStackTrace();
	        	err(e.getMessage());
	        }
		}else{
			try{//修改病患保险信息
				result = MEMInsureInfoTool.getInstance().updateMemInsureInfo(parm, conn);
		        if (result.getErrCode() < 0) {
		            err("ERR:" + result.getErrCode() + result.getErrText() +
		                result.getErrName());
		        }
			}catch(Exception e){
				e.printStackTrace();
				err(e.getMessage());
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 删除信息
	 */
	public TParm delete(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try{
			result = MEMInsureInfoTool.getInstance().deleteMemInsureInfo(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
		}catch(Exception e){
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
}
