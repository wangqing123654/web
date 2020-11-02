package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 患者会员注册
 * </p>
 * 
 * <p>
 * Description: 患者会员注册
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
 * @author duzhw 2014/01/14
 * @version 4.5
 */
public class MEMPatRegisterTool extends TJDOTool {
	
	public MEMPatRegisterTool() {
		setModuleName("mem\\MEMPatRegisterModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static MEMPatRegisterTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return MEMPatRegisterTool
	 */
	public static MEMPatRegisterTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MEMPatRegisterTool();
		return instanceObject;
	}
	
	/**
	 * 保存-会员注册表sql（MEM_PATINFO）
	 * @param parm
	 * @return
	 */
	public TParm insertMemPatinfoDate(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.update("insertMemPatinfoDate", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	/**
	 * 保存-插入会员卡交易表sql（MEM_TRADE）
	 * @param parm
	 * @return
	 */
	public TParm insertMemTradeData(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.update("insertMemTradeData", parm.getRow(0), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	/**
	 * 根据病案号查询mem_patinfo表数据
	 */
	public TParm onQueryMem(TParm parm) {
		TParm result = new TParm();
		result = this.query("onQueryMem", parm.getRow(0));
		//System.out.println("TOOL=result="+result);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 查询会员交易表主档-预办卡信息
	 */
	public TParm onQueryTrade(TParm parm) {
		TParm result = new TParm();
		result = this.query("onQueryTrade", parm.getRow(0));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 修改会员信息
	 */
	public TParm updateMemData(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("updateMemData1="+parm);
		result = this.update("updateMemData", parm, conn);
		System.out.println("tool1-result.getErrCode()="+result.getErrCode());
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	
	/**
	 * 修改病患信息-sys_patinfo表（有“MODIFY”权限）
	 */
	public TParm updatePatinfoData(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("updatePatinfoData-parm="+parm);
		result = this.update("updatePatinfoData", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 修改病患信息-sys_patinfo表（没有MODIFY权限） add by huangjw 20150717
	 *//*
	public TParm modifyPatinfoData(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("updatePatinfoData-parm="+parm);
		result = this.update("modifyPatinfoData", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}*/
	/**
	 * 修改交易表
	 */
	public TParm updateMemTrade(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("updateMemTrade-parm="+parm);
		result = this.update("updateMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 新增交易表
	 */
	public TParm insertMemTrade(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("insertMemTrade-parm="+parm);
		result = this.update("insertMemTrade", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 新增病患
	 */
	public TParm newSysPatinfo(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("newSysPatinfo-parm="+parm);
		result = this.update("newSysPatinfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 插入会员表-非会员注册
	 */
	public TParm insertNoMemPatinfoDate(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//System.out.println("insertNoMemPatinfoDate-parm="+parm);
		result = this.update("insertNoMemPatinfoDate", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	public String getMemCustomerSource(String mrNo){
		String sql = "SELECT CUSTOMER_SOURCE FROM MEM_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("CUSTOMER_SOURCE", 0);
	}
	
	public TParm insertMemPat(TParm parm){
		String mrNo = parm.getValue("MR_NO");
		String customerSource = parm.getValue("CUSTOMER_SOURCE");
		String sql ="INSERT INTO MEM_PATINFO (MR_NO,CUSTOMER_SOURCE) VALUSE ('"+mrNo+"','"+customerSource+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm updateMemPat(TParm parm){
		String mrNo = parm.getValue("MR_NO");
		String customerSource = parm.getValue("CUSTOMER_SOURCE");
		String sql="UPDATE MEM_PATINFO SET CUSTOMER_SOURCE='"+customerSource+"' WHERE MR_NO='"+mrNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
}
