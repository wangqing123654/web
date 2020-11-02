package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCOperationRoomReturnTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��������ҩ�黹Control
 * </p>
 * 
 * <p>
 * Description:��������ҩ�黹Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-08-12
 * @version 1.0
 */
public class SPCOperationRoomReturnControl extends TControl {

	/** TABLE�ؼ� */
	private TTable table_y;
	private TTable table_n;
	/** PANEL�ؼ� */
	private TPanel panel_y;
	private TPanel panel_n;
	
	String ip ;
	String cabinet_id ;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		table_y = getTTable("TABLE_Y");
		table_n = getTTable("TABLE_N");
		panel_y = getTPanel("PANEL_Y");
		panel_n = getTPanel("PANEL_N");
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// ��ñ���IP
			TParm tParm = new TParm();
			tParm.setData("CABINET_IP", ip);
			TParm resultParm = SPCInStoreTool.getInstance().queryCabinet(
					tParm);
			cabinet_id = (String) resultParm.getData("CABINET_ID", 0);
			 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ⵥ�Żس��¼�
	 */
	public void onDispenseNoLost() {
		onQuery();
	}

	/**
	 * �龫�س��¼�
	 */
	public void onToxicIdLost() {
		TParm result = new TParm();
		String toxic_id = this.getValueString("TOXIC_ID");
		if (StringUtil.isNullString(toxic_id)) {
			return;
		}
		if (panel_y.isShowing()) {
			String user = Operator.getID();
			result = SPCOperationRoomReturnTool.getInstance().saveInfo(user,
					toxic_id);
		} else {
			if (StringUtil.isNullString(toxic_id)) {
				return;
			}
			TParm resultQuery = SPCOperationRoomReturnTool.getInstance()
					.queryINDToxicDByToxicIdInfo(toxic_id);
			String container_id = "";
			if (!(resultQuery.getErrCode() < 0))
				container_id = resultQuery.getValue("CONTAINER_ID", 0);
			TParm parm = new TParm();
			parm.setData("TOXIC_ID", toxic_id);
			parm.setData("CONTAINER_ID", container_id);
			String cabinet_id = "";
			try {
				 
				TParm tParm = new TParm();
				tParm.setData("CABINET_IP", ip);
				TParm resultParm = SPCInStoreTool.getInstance().queryCabinet(
						tParm);
				cabinet_id = (String) resultParm.getData("CABINET_ID", 0);
				parm.setData("CABINET_ID", cabinet_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			result = TIOM_AppServer.executeAction(
					"action.spc.SPCOperationRoomReturnAction", "update", parm);
		}
		if (result.getErrCode() < 0) {
			messageBox("E0001");
			return;
		}
		messageBox("P0001");
		onClear();
		onQuery();
	}

	/**
	 * ��ѡ��ť�ı��¼�
	 */
	public void onRadioButtonChange() {
		onClear();
		onQuery();
	}

	/**
	 * ҳǩ�ı��¼�
	 */
	public void onTTabbedPaneChange() {
		table_n.removeRowAll();
		table_y.removeRowAll();
		onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String showFlg = "";
		String showFlg1 = "";
		TTable table = new TTable();
		if (panel_n.isShowing()) {
			if (getTRadioButton("Y").isSelected()) {
				showFlg1 = "Y";
			} else {
				showFlg1 = "N";
			}
			showFlg = "N";
			table = table_n;
		} else {
			if (getTRadioButton("Y").isSelected()) {
				showFlg1 = "Y";
			} else {
				showFlg1 = "N";
			}
			showFlg = "Y";
			table = table_y;
		}
		String dispense_no = getValueString("DISPENSE_NO");
		String toxic_id = getValueString("TOXIC_ID");
		TParm result = new TParm();
		if (showFlg == "Y") {
			result = SPCOperationRoomReturnTool.getInstance().queryInfoo(
					showFlg1, dispense_no, toxic_id);
		} else if (showFlg == "N") {
			result = SPCOperationRoomReturnTool.getInstance().queryInfo(
					dispense_no, toxic_id, showFlg1,cabinet_id);
		}
		if (result.getCount() > 0) {
			table.setParmValue(result);
		}
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		int row = 0;
		String dispense_no = "";
		TParm parm = new TParm();
		if (panel_n.isShowing()) {
			row = table_n.getSelectedRow();
			parm = table_n.getParmValue();
			dispense_no = parm.getRow(row).getValue("DISPENSE_NO");
		} else {
			row = table_y.getSelectedRow();
			parm = table_y.getParmValue();
			dispense_no = parm.getRow(row).getValue("DISPENSE_NO");
		}
		this.setValue("DISPENSE_NO", dispense_no);
	}

	/**
	 * ���
	 */
	public void onClear() {
		table_n.removeRowAll();
		table_y.removeRowAll();
		this.setValue("DISPENSE_NO", "");
		this.setValue("TOXIC_ID", "");
	}

	/**
	 * ��ȡTTable�ؼ�
	 * 
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��ȡTPanel�ؼ�
	 * 
	 * @return
	 */
	private TPanel getTPanel(String tag) {
		return (TPanel) getComponent(tag);
	}

	/**
	 * ��ȡTPanel�ؼ�
	 * 
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

}
