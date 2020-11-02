package action.mem;

import jdo.mem.MEMPatRegisterTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 病患会员注册
 * </p>
 * 
 * <p>
 * Description: 病患会员注册
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
 * @author duzhw 2014.01.14
 * @version 4.5
 */
public class MEMPatRegisterAction extends TAction {
	
	/**
	 * 执行保存事物
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//1:插入会员注册表sql（MEM_PATINFO）
			result = MEMPatRegisterTool.getInstance().insertMemPatinfoDate(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
			//2:插入会员卡交易表sql（MEM_TRADE）-【STATUS=0新建】
	        result = MEMPatRegisterTool.getInstance().insertMemTradeData(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        
	        //3:将注册的病患信息同步到sys_patinfo表
	        
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 根据病案号查询mem_patinfo表数据
	 */
	public TParm onQueryMem(TParm parm) {
		TParm result = new TParm();
		try {
			result = MEMPatRegisterTool.getInstance().onQueryMem(parm);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 查询会员交易表主档-预办卡信息
	 */
	public TParm onQueryTrade(TParm parm) {
		TParm result = new TParm();
		try {
			result = MEMPatRegisterTool.getInstance().onQueryTrade(parm);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		return result;
	}
	/**
	 * 修改会员信息
	 */
	public TParm updateMemData(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//会员信息修改
			result = MEMPatRegisterTool.getInstance().updateMemData(parm,conn);
			//System.out.println("updateMemData-result.getErrCode()="+result.getErrCode());
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        
	        result = MEMPatRegisterTool.getInstance().updatePatinfoData(parm,conn);
	        
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        //交易表修改
	        result = MEMPatRegisterTool.getInstance().updateMemTrade(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 新增会员信息
	 */
	public TParm insertMemData(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//会员信息新增
			result = MEMPatRegisterTool.getInstance().insertMemPatinfoDate(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        //新增交易表
//	        result = MEMPatRegisterTool.getInstance().insertMemTrade(parm,conn);
//	        if (result.getErrCode() < 0) {
//	            err("ERR:" + result.getErrCode() + result.getErrText() +
//	                result.getErrName());
//	            conn.rollback();
//	            conn.close();
//	            return result;
//	        }
	        //病患信息表同步SYS_PATINFO
	        result = MEMPatRegisterTool.getInstance().updatePatinfoData(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 新增病患
	 */
	public TParm newSysPatinfo(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			//1：新增数据插入到sys_patinfo表
			result = MEMPatRegisterTool.getInstance().newSysPatinfo(parm,conn);
			//System.out.println("newSysPatinfo-result.getErrCode()="+result.getErrCode());
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
	        //2：新增数据插入到MEM_PATINFO表-非会员
	        result = MEMPatRegisterTool.getInstance().insertNoMemPatinfoDate(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
//	        //3:判断是否办理预办卡业务 是：插入交易表
//	        if(parm.getValue("MEM_CODE").toString().length()>0){
//	        	//新增交易表
//		        result = MEMPatRegisterTool.getInstance().insertMemTrade(parm,conn);
//		        if (result.getErrCode() < 0) {
//		            err("ERR:" + result.getErrCode() + result.getErrText() +
//		                result.getErrName());
//		            conn.rollback();
//		            conn.close();
//		            return result;
//		        }
//	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 插入交易表
	 */
	public TParm insertMemTradeData(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
	        //新增交易表
	        result = MEMPatRegisterTool.getInstance().insertMemTrade(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 修改交易表
	 */
	public TParm updateMemTrade(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
	        //交易表修改
	        result = MEMPatRegisterTool.getInstance().updateMemTrade(parm,conn);
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            conn.rollback();
	            conn.close();
	            return result;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			err(e.getMessage());
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 获取插入会员注册表sql（MEM_PATINFO）
	 */
//	public String getMemPatinfoSql(TParm parm){
//		String sql = "";
//		
//		System.out.println("打印插入会员注册表sql："+sql);
//		return sql;
//	}
	
	/**
	 * 获取插入会员卡交易表sql（MEM_TRADE）
	 */
//	public String getMemTradeSql(TParm parm){
//		String sql = "";
//		
//		System.out.println("打印插入会员卡交易表sql："+sql);
//		return sql;
//	}

}
