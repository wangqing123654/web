package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCRecoverTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: �龫��ƿ����Control
 * </p>
 * 
 * <p>
 * Description: �龫��ƿ����Control
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
 * @author fuwj 2013.05.12
 * @version 1.0
 */

public class SPCRecoverControl extends TControl {
														
	TPanel PANEL_N;
	TPanel PANEL_Y;
	TTable TABLE_M_N;								
	TTable TABLE_M_Y;
	String ip="";
	String orgCode = "";
															
	/**
	 * ������
	 */
	public SPCRecoverControl() {
		super();
	}
	
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		TABLE_M_N = getTable("TABLE_M_N");
		TABLE_M_Y = getTable("TABLE_M_Y");
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// ��ñ���IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if ("".equals(ip)) {
			this.messageBox("��ȡ���ܹ���Ϣʧ��");
			return;
		}
		TParm parm = new TParm();   
		parm.setData("CABINET_IP", ip);  
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);	
	    orgCode = (String) result.getData("ORG_CODE",0);
		onQuery();
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		parm.setData("IS_RECOVER","N");      							
		if (PANEL_N.isShowing()) {		
			 parm.setData("IS_RECOVER","N");      
		} else if (PANEL_Y.isShowing()) {
			 parm.setData("IS_RECOVER","Y");			
		}
		//String toxicId = this.getValueString("TOXIC_ID");
		parm.setData("ORG_CODE",orgCode);	
		TParm result = SPCRecoverTool.getInstance().getRecover(parm);
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			if (PANEL_N.isShowing()) {					
				TABLE_M_N.setParmValue(new TParm());
			}
			if (PANEL_Y.isShowing()) {
				TABLE_M_Y.setParmValue(new TParm());
			}
			return;
		}
		String flg = parm.getValue("IS_RECOVER");		
		if("N".equals(flg)) {		
			TABLE_M_N.setParmValue(result);
		}else if("Y".equals(flg)) {
			TABLE_M_Y.setParmValue(result);
		}
		/*if (PANEL_N.isShowing()) {
			TABLE_M_N.setParmValue(result);
		} else if (PANEL_Y.isShowing()) {   
			TABLE_M_Y.setParmValue(result);
		}*/
	}
	  
	/**
	 * ɨ���龫�����
	 */
	public void onClick() {
		String str  = this.getValueString("TOXIC_ID");
		if (PANEL_N.isShowing()) {
			int rowNum = TABLE_M_N.getRowCount();
			if (rowNum == -1) {
				this.messageBox("�龫����Ŵ���");
				return;
			}						
			for(int i=0;i<rowNum;i++) {
				String toxicId = TABLE_M_N.getItemString(i, "TOXIC_ID1");			
				if(str.equals(toxicId)) {
					TParm parm = new TParm();
					parm.setData("TOXIC_ID",toxicId);
					String userId = Operator.getID();
					parm.setData("RECLAIM_USER",userId);
					TParm result = TIOM_AppServer.executeAction(
							"action.spc.SPCRecoverAction", "onRecover", parm);
					if (result.getErrCode() < 0) {
						this.messageBox("����ʧ��");
						return;
					}
					this.messageBox("���ճɹ�");
					this.setValue("TOXIC_ID", "");
					onQuery();
					break;
				}
			}
		}
	}
	
/*	*//**
	 * ����
	 *//*
	public void onSave() {
		if (PANEL_N.isShowing()) {
			int rowNum = TABLE_M_N.getSelectedRow();
			if (rowNum == -1) {
				this.messageBox("��ѡ��Ҫ����ҩƷ");
			}
			String toxicId = TABLE_M_N.getItemString(rowNum, "TOXIC_ID1");
			TParm parm = new TParm();
			parm.setData("TOXIC_ID",toxicId);
			String userId = Operator.getID();
			parm.setData("RECLAIM_USER",userId);
			TParm result = TIOM_AppServer.executeAction(
					"action.spc.SPCRecoverAction", "onRecover", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
				return;
			}
			this.messageBox("���ճɹ�");
			onQuery();
		}
	}*/
	
	/**
	 * ��ȡtable����
	 * 
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}
	
	/**
	 * ��շ���	
	 */
	public void onClear() {
		TABLE_M_N.removeRowAll();
		TABLE_M_Y.removeRowAll();
		this.setValue("TOXIC_ID", "");
	}
	
}
