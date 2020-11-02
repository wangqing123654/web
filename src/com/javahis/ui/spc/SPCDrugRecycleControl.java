package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jdo.spc.SPCDrugRecycleTool;
import jdo.spc.SPCInStoreTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: �����龫��ƿ����Control
 * </p>
 * 
 * <p>
 * Description: �����龫��ƿ����Control
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
 * @author shendr 2013.07.18
 * @version 1.0
 */
public class SPCDrugRecycleControl extends TControl {

	/**
	 * ����UI�еĿؼ�
	 */
	private TTable table_n;
	private TTable table_y;
	private TPanel PANEL_N;
	private TPanel PANEL_Y;

	private String ip = "";
	private String orgCode = "";

	/**
	 * ������
	 */
	public SPCDrugRecycleControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table_n = getTable("TABLE_N");
		table_y = getTable("TABLE_Y");
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
		this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
		this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
		this.setValue("ORG_CHN_DESC", result.getData("ORG_CHN_DESC", 0));
		orgCode = (String) result.getData("ORG_CODE", 0);
		onQuery();
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (PANEL_N.isShowing()) {
			int rowNum = table_n.getSelectedRow();
			if (rowNum == -1) {
				this.messageBox("��ѡ��Ҫ����ҩƷ");
			}
			String toxicId = table_n.getItemString(rowNum, "TOXIC_ID");
			String userId = Operator.getID();
			TParm parm = new TParm();
			parm.setData("TOXIC_ID", toxicId);
			parm.setData("RECLAIM_USER", userId);
			TParm result = TIOM_AppServer.executeAction(
					"action.spc.SPCDrugRecycleAction", "onRecover", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
				return;
			}
			this.messageBox("���ճɹ�");
		}
	}

	/**
	 * ҳǩ�ı��¼�
	 */
	public void onPaneChange() {
		onQuery();
	}

//	/**
//	 * ˢ�龫����
//	 */
//	public void onToxicIdLost() {
//		String toxicId = this.getValueString("TOXIC_ID");
//		String userId = Operator.getID();
//		TParm parm = new TParm();
//		parm.setData("TOXIC_ID", toxicId);
//		parm.setData("RECLAIM_USER", userId);
//		TParm result = TIOM_AppServer.executeAction(
//				"action.spc.SPCDrugRecycleAction", "onRecover", parm);
//		if (result.getErrCode() < 0) {
//			this.messageBox("����ʧ��");
//			return;
//		}
//		this.messageBox("���ճɹ�");
//	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		parm.setData("IS_RECOVER", "N");
		if (PANEL_N.isShowing()) {
			parm.setData("IS_RECOVER", "N");
		} else if (PANEL_Y.isShowing()) {
			parm.setData("IS_RECOVER", "Y");
		}
		String toxicId = this.getValueString("TOXIC_ID");
		parm.setData("TOXIC_ID", toxicId);
		TParm result = SPCDrugRecycleTool.getInstance().getRecover(parm);
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			onClear();
			return;
		}
		String flg = parm.getValue("IS_RECOVER");
		if ("N".equals(flg)) {
			table_n.setParmValue(result);
		} else if ("Y".equals(flg)) {
			table_y.setParmValue(result);
		}
	}

	/**
	 * ��񵥻�
	 */
	public void onTableClicked() {
		String toxic_id = "";
		TParm result = new TParm();
		if (PANEL_N.isShowing()) {
			int row = table_n.getSelectedRow();
			TParm selParm = table_n.getParmValue().getRow(row);
			toxic_id = selParm.getValue("TOIXC_ID");
			result = SPCDrugRecycleTool.getInstance().queryCabinet(toxic_id);
			setValue("TOXIC_ID", selParm.getValue("TOXIC_ID"));
		} else {
			int row = table_y.getSelectedRow();
			TParm selParm = table_y.getParmValue().getRow(row);
			toxic_id = selParm.getValue("TOIXC_ID");
			result = SPCDrugRecycleTool.getInstance().queryCabinet(toxic_id);
			setValue("TOXIC_ID", selParm.getValue("TOXIC_ID"));
		}
	}

	/**
	 * ɨ���龫�����
	 */
	public void onClick() {
		String str = this.getValueString("TOXIC_ID");
		if (PANEL_N.isShowing()) {
			int rowNum = table_n.getRowCount();
			if (rowNum == -1) {
				this.messageBox("�龫����Ŵ���");
				return;
			}
			for (int i = 0; i < rowNum; i++) {
				String toxicId = table_n.getItemString(i, "TOXIC_ID");
				if (str.equals(toxicId)) {
					TParm parm = new TParm();
					parm.setData("TOXIC_ID", toxicId);
					String userId = Operator.getID();
					parm.setData("RECLAIM_USER", userId);
					TParm result = TIOM_AppServer.executeAction(
							"action.spc.SPCDrugRecycleAction", "onRecover",
							parm);
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

	/**
	 * ��շ���
	 */
	public void onClear() {
		table_n.removeRowAll();
		table_y.removeRowAll();
		this.setValue("TOXIC_ID", "");
	}

	/**
	 * ��ȡtable����
	 * 
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

}
