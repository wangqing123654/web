package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: 医建通项目：排班
 * </p>
 * 
 * <p>
 * Description:医建通项目：排班
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author zhangp 20120815
 * @version 1.0
 */
public class REGCcbSchControl extends TControl{
	
	/**
	 * 预约挂号
	 */
	public void q1(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"upload", new TParm());
	}
	
	/**
	 * 当日挂号
	 */
	public void q2(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"upload2", new TParm());
	}
	
	/**
	 * 科室信息
	 */
	public void dept(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"uploadDept", new TParm());
	}
	
	/**
	 * 医师信息
	 */
	public void dr(){ 
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"uploadDr", new TParm());
	}
	

}
