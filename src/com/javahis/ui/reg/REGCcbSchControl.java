package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: ҽ��ͨ��Ŀ���Ű�
 * </p>
 * 
 * <p>
 * Description:ҽ��ͨ��Ŀ���Ű�
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
	 * ԤԼ�Һ�
	 */
	public void q1(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"upload", new TParm());
	}
	
	/**
	 * ���չҺ�
	 */
	public void q2(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"upload2", new TParm());
	}
	
	/**
	 * ������Ϣ
	 */
	public void dept(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"uploadDept", new TParm());
	}
	
	/**
	 * ҽʦ��Ϣ
	 */
	public void dr(){ 
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"uploadDr", new TParm());
	}
	

}
