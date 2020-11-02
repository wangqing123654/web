package com.javahis.ui.sys;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.SYSComorderReplaceTool;
import jdo.pha.PhaBaseTool;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
import java.awt.Component;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: ҽ��ģ���滻������
 * </p>
 * 
 * <p>
 * Description: ҽ��ģ���滻������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl 2011.05.26
 * @version 1.0
 */
public class SYSComorderReplaceControl extends TControl {
	// TParm data;
	int selectRow = -1;
	TParm actionParm;
	// ��ҩTABLE
	private TTable medTable;
	// ��ҩ
	// private TDataStore ds;
	// ��ѯ����SQL
	//private final static String SQL = " SELECT * FROM SYS_COMORDER_REPLACE  ";

	public void onInit() {
		super.onInit();
		// ֻ��text���������������sys_fee������
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopupAll.x");
		// ���ܻش�ֵ
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// tableר�õļ���
		((TTable) getComponent("TABLE")).addEventListener(
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
		getInitParam();
		onClear();
		//medTable.setDSValue();
	}

	/**
	 * tableѡ�������¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableComponent(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = medTable.getParmValue();
		// System.out.println("table����"+tableParm);
		// System.out.println("table"+medTable.getParmValue());
		actionParm = new TParm();
		//tableParm.setCount(tableParm.getCount("ORDER_CODE") - 1);
		// ========add-end===================================
//		for (int i = 0; i < count; i++) {
//			if ("Y".equals(tableParm.getValue("UPDATE_FLG", i))) {
//				actionParm.addData("ORDER_CODE", tableParm.getData(
//						"ORDER_CODE", i));
//				actionParm.addData("ORDER_DESC", tableParm.getData(
//						"ORDER_DESC", i));
//				actionParm
//						.addData("MEDI_QTY", tableParm.getData("MEDI_QTY", i));
//				actionParm.addData("MEDI_UNIT", tableParm.getData("MEDI_UNIT",
//						i));
//				actionParm.addData("ROUTE_CODE", tableParm.getData(
//						"ROUTE_CODE", i));
//				actionParm.addData("ORDER_CODE_OLD", tableParm.getData(
//						"ORDER_CODE_OLD", i));
//				actionParm.addData("ORDER_DESC_OLD", tableParm.getData(
//						"ORDER_DESC_OLD", i));
//				actionParm.addData("MEDI_QTY_OLD", tableParm.getData(
//						"MEDI_QTY_OLD", i));
//				actionParm.addData("MEDI_UNIT_OLD", tableParm.getData(
//						"MEDI_UNIT_OLD", i));
//				actionParm.addData("ROUTE_CODE_OLD", tableParm.getData(
//						"ROUTE_CODE_OLD", i));
//				actionParm.addData("SEQ", tableParm.getData("SEQ", i));
//				actionParm.addData("PACK_CODE", tableParm.getData("PACK_CODE",
//						i));
//			}
//		}

		return true;
	}

	/**
	 * ͨ��orderCode�õ�ҽ������
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	public String getOrderDesc(String orderCode) {
		String orderDesc = "";
		String selOrderDesc = " SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE = '"
				+ orderCode + "' ";
		TParm orderDescParm = new TParm(TJDODBTool.getInstance().select(
				selOrderDesc));
		if (orderDescParm.getErrCode() < 0) {
			err(orderDescParm.getErrName() + " " + orderDescParm.getErrName());
			return orderDesc;
		}
		orderDesc = orderDescParm.getValue("ORDER_DESC", 0);
		return orderDesc;
	}

	/**
	 * ���ô��������б�ѡ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
	}

	/**
	 * ����
	 */
	public void onUpdate() {
		// System.out.println("����"+3.0%2.0);
		TParm result = new TParm();
		TParm parm = medTable.getParmValue();
		
		// System.out.println("��̨���"+actionParm);
		int index=0;
		TParm exeParm=new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getValue("UPDATE_FLG",i).equals("N")
					|| parm.getValue("UPDATE_FLG",i).length() <= 0) {
				continue;
			}
			if (parm.getValue("PACK_CODE",i).equals("03")||
					parm.getValue("PACK_CODE",i).equals("04")) {//
				this.messageBox("�ٴ�·�����Ա�ײ����Ͳ����Բ�������");
				return;
			}
			if (parm.getValue("PACK_CODE",i).length() <= 0) {
				this.messageBox("�ײ������Ϊ��");
				return;
			}
			if (parm.getValue("ORDER_CODE",i).length() <= 0) {
				this.messageBox("��ҽ�����벻��Ϊ��");
				return;
			}
			if (parm.getValue("ORDER_CODE_OLD",i).length() <= 0) {
				this.messageBox("��ҽ�����벻��Ϊ��");
				return;
			}
			exeParm.addRowData(parm, i);
			//exeParm.setData("FLG",i,medTable.getItemData(i, "FLG"));
			//setExeParm(exeParm, parm, i);
			index++;
		}
		exeParm.setCount(index);
		if (exeParm.getCount("ORDER_CODE")<=0) {
			this.messageBox("��ѡ��ҽ��");
			return;
		}
		parm.setData("ACTION", exeParm.getData());
		result = TIOM_AppServer.executeAction(
				"action.sys.SYSComorderReplaceAction", "onReplace", parm);

		if (result.getErrCode() < 0) {// modify by wanglong 20120914
			messageBox(result.getErrText());
			return;
		} else {
			if (result.getValue("RETURN").equals("1")) {
				this.messageBox("P0001");
				onClear();
			} else {
				this.messageBox("E0001");
			}
		}

	}

	/**
	 * ����
	 */
	public void onSave() {
		// UPDATE_FLG;ORDER_DESC_OLD;MEDI_QTY_OLD;MEDI_UNIT_OLD;ROUTE_CODE_OLD;
		// ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;REGION_CODE;SEQ
		medTable.acceptText();
		TParm parm = medTable.getParmValue();
		TParm exeParm=new TParm();
		int index=0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getValue("UPDATE_FLG",i).equals("N")
					|| parm.getValue("UPDATE_FLG",i).length() <= 0) {
				continue;
			}
			if (parm.getValue("PACK_CODE",i).length() <= 0) {
				this.messageBox("�ײ������Ϊ��");
				return;
			}
			if (parm.getValue("ORDER_CODE",i).length() <= 0) {
				this.messageBox("��ҽ�����벻��Ϊ��");
				return;
			}
			if (parm.getValue("ORDER_CODE_OLD",i).length() <= 0) {
				this.messageBox("��ҽ�����벻��Ϊ��");
				return;
			}
			exeParm.addRowData(parm, i);
			//exeParm.setData("FLG",i,medTable.getItemData(i, "FLG"));
			//setExeParm(exeParm, parm, i);
			index++;
		}
		exeParm.setCount(index);
		if (exeParm.getCount("PACK_CODE")<=0) {
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.sys.SYSComorderReplaceAction", "onSave", exeParm);
		if (result.getErrCode() != 0) {
			this.messageBox("E0001");
			return;
		} else {
			this.messageBox("P0001");
		}
		onQuery();
		//onClear();
	}
	/**
	 * ɾ��
	 */
	public void onDelete() {
		TParm parm = medTable.getParmValue();
		TParm exeParm=new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getValue("UPDATE_FLG", i).equals("N")
					|| parm.getValue("UPDATE_FLG", i).length() <= 0) {
				continue;
			}
			//  yuml  20141110   
			exeParm.addRowData(parm, i);
		}
		if (exeParm.getCount("PACK_CODE")<=0) {
			this.messageBox("��ѡ����Ҫ����������");
			return;
		}
		TParm result = TIOM_AppServer.executeAction(
				"action.sys.SYSComorderReplaceAction", "onDel", exeParm);
		if (result.getErrCode()< 0) {
			this.messageBox("E0003");
			return;
		} else {
			this.messageBox("P0003");
		}
		onClear();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String orderCode = this.getValueString("ORDER_CODE");
		// =====add-begin (by wanglong 2012903)===============================
		String packCode = this.getValueString("PACK_CODE");
		if (packCode.length() <= 0) {
			this.messageBox("�ײ������Ϊ��");
			return;
		}
		String where = "";
		if (orderCode.length() > 0)
			where = " AND ORDER_CODE_OLD='" + orderCode + "'";
		String sql = "SELECT UPDATE_FLG,ORDER_CODE_OLD,"
				+ "ORDER_DESC_OLD," + "MEDI_QTY_OLD," + "MEDI_UNIT_OLD,"
				+ "ROUTE_CODE_OLD," + "ORDER_CODE," + "ORDER_DESC,"
				+ "MEDI_QTY," + "MEDI_UNIT," + "ROUTE_CODE," + "REGION_CODE,"
				+ "OPT_USER," + "OPT_DATE," + "OPT_TERM," + "SEQ,PACK_CODE,'N' FLG"
				+ " FROM SYS_COMORDER_REPLACE" + " WHERE PACK_CODE = '"
				+ packCode + "'" + where + " ORDER BY SEQ DESC";
		// System.out.println(sql);
		TParm data = new TParm(TJDODBTool.getInstance().select(sql));
		if (data.getCount()<=0) {
			this.messageBox("û�в�ѯ������");
			medTable.removeRowAll();
			data=new TParm();
			getNewRow(data);
			data.setCount(1);
			((TTable) getComponent("TABLE")).setParmValue(data);
			return ;
		}
		getNewRow(data);
		data.setCount(data.getCount()+1);
		((TTable) getComponent("TABLE")).setParmValue(data);
		// ======add-end========================================================
	}

	/**
	 * ���
	 */
	public void onClear() {
		clearValue("ORDER_CODE;ORDER_DESC;PACK_CODE");
		((TTable) getComponent("TABLE")).clearSelection();
		// selectRow = -1;
		TParm  parm= new TParm();
		getNewRow(parm);
		parm.setCount(parm.getCount("PACK_CODE"));
		medTable.setParmValue(parm);
		// // ����ɾ����ť״̬
		// ( (TMenuItem) getComponent("delete")).setEnabled(false);
	}

	private void getNewRow(TParm tableParm) {
		String[] arrayString = medTable.getParmMap().split(";");
		for (int i = 0; i < arrayString.length; i++) {
			if (arrayString[i].equals("FLG")) {
				tableParm.addData(arrayString[i], "Y");
			}else{
				if (arrayString[i].equals("OPT_USER")||
						arrayString[i].equals("OPT_TERM")) {
					if (arrayString[i].equals("OPT_USER")) {
						tableParm.addData("OPT_USER",Operator.getID());
					}else{
						tableParm.addData("OPT_TERM",Operator.getIP());
					}	
				}else
					tableParm.addData(arrayString[i], "");
			}
		}
		//tableParm.setData("FLG",tableParm.getCount()-1, "Y");
	}

	/**
	 * ���ܲ���
	 */
	public void getInitParam() {
		medTable = (TTable) this.getComponent("TABLE");
//		medTable.addEventListener(TTableEvent.CHANGE_VALUE, this,
//				"onChangeValue");
		medTable.addEventListener("TABLE" + "->" + TTableEvent.CHANGE_VALUE,
				this, "onChangeValue");
		medTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		medTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onNewMedCreateEditComponent");
		// ds = new TDataStore();
	}

	/**
	 * ��ҩTABLEֵ�ı��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChangeValue(TTableNode tNode) {
		
//		messageBox(tNode.getColumn()+"");
		
		
		int column = medTable.getSelectedColumn();
		int row=medTable.getSelectedRow();
		String colName = medTable.getParmMap(column);
		TParm tableParm=medTable.getParmValue();
//		if (StringUtil.isNullString(tableParm.getValue("ORDER_CODE_OLD", row))
//				|| StringUtil.isNullString(tableParm
//						.getValue("ORDER_CODE", row))) {
//			return true;
//		}
		if (colName.equals("ORDER_CODE")||colName.equals("ORDER_CODE_OLD")) {
			if (tableParm.getValue("FLG", row).equals("N")) {//�޸�ҽ������
				this.messageBox("�������޸�ҽ������");
				tNode.setValue(tNode.getOldValue());
				return true;
			}
			
		}
//
//		// ORDER_DESC_OLD;MEDI_QTY_OLD;MEDI_UNIT_OLD;ROUTE_CODE_OLD;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE
//		// ҽ��,����,��λ,�÷�,��ҽ��,������,�µ�λ,���÷�
//		if ("ORDER_DESC".equalsIgnoreCase(colName)
//				|| "MEDI_UNIT".equalsIgnoreCase(colName)) {
//			return true;
//		}
		return false;
	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onMedCreateEditComponent(Component com, int row, int column) {

		TTable table = (TTable) this.getComponent("TABLE");
		String columnName = table.getParmMap(column);
		if (!"ORDER_CODE_OLD".equalsIgnoreCase(columnName)) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopupAll.x"));
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	public void onNewMedCreateEditComponent(Component com, int row, int column) {

		TTable table = (TTable) this.getComponent("TABLE");
		String columnName = table.getParmMap(column);
		if (!"ORDER_CODE".equalsIgnoreCase(columnName)) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER_CLP", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopupAll.x"));
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popNewOrderReturn");

	}

	/**
	 * ����ҽ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		int row = medTable.getSelectedRow();
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			return;
		}

		/*
		 * ҽ��,200;����,60,double;��λ,60,UNIT_CODE;Ƶ��,70,FREQ_CODE;�շ�,70,int;
		 * ҽ����ע,200
		 * ;�ض�����,80,DEPT;��,40,boolean;��������,60,boolean;��������,60,boolean;סԺ����
		 * ,60,boolean 0,left;1,right;2,left;3,left;4,right;5,left;6,left;
		 */
		medTable.setItem(row, "ORDER_CODE_OLD", parm.getValue("ORDER_CODE"));
		medTable.setItem(row, "ORDER_DESC_OLD", parm.getValue("ORDER_DESC"));
		medTable.setItem(row, "MEDI_QTY_OLD", 1);
		medTable.setItem(row, "MEDI_UNIT_OLD", parmBase.getValue("MEDI_UNIT", 0));
		medTable.setItem(row, "ROUTE_CODE_OLD", parmBase.getValue("ROUTE_CODE", 0));
		medTable.setItem(row, "REGION_CODE", parmBase.getValue("REGION_CODE", 0));
		if (!StringUtil.isNullString(medTable.getItemString(medTable.getRowCount()-1,"ORDER_CODE_OLD"))) {
			TParm tableParm=new TParm();
			getNewRow(tableParm);
			medTable.addRow(medTable.getRowCount(), tableParm.getRow(0));
		}
		medTable.setSelectedColumn(2);
	}

	public void popNewOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		int row = medTable.getSelectedRow();
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			return;
		}
		/*
		 * ҽ��,200;����,60,double;��λ,60,UNIT_CODE;Ƶ��,70,FREQ_CODE;�շ�,70,int;
		 * ҽ����ע,200
		 * ;�ض�����,80,DEPT;��,40,boolean;��������,60,boolean;��������,60,boolean;סԺ����
		 * ,60,boolean 0,left;1,right;2,left;3,left;4,right;5,left;6,left;
		 */
		medTable.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		medTable.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		medTable.setItem(row, "MEDI_QTY", 1);
		medTable.setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT", 0));
		medTable.setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE", 0));
		medTable.setItem(row, "SEQ", getMaxSeq(parm.getValue("ORDER_CODE")) + 1);
		if (!StringUtil.isNullString(medTable.getItemString(medTable.getRowCount()-1,"ORDER_CODE"))) {
			TParm tableParm=new TParm();
			getNewRow(tableParm);
			medTable.addRow(medTable.getRowCount(), tableParm.getRow(0));
		}
		medTable.setSelectedColumn(2);
	}
	
	/**
	 * ���SEQ_NO
	 * 
	 * @return seq int
	 */
	// =====modify-begin (by wanglong 20120903)===============================
	public int getMaxSeq(String orderCode) {
		TParm seqParm = SYSComorderReplaceTool.getInstance().selMaxSeq(
				orderCode);
		int tempSeq = seqParm.getInt("SEQ", 0);
		int seq = 0;
		TParm tableParm =medTable.getParmValue();
		// ======modify-end========================================================
		int count = tableParm.getCount();
		for (int i = 0; i < count; i++) {
			seq = tableParm.getInt("SEQ",i);
			if (seq > tempSeq)
				tempSeq = seq;
		}
		return tempSeq;
	}
}
