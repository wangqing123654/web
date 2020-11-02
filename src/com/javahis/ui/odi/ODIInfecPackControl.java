package com.javahis.ui.odi;

import java.awt.Component;
import java.util.Vector;

import jdo.odi.ODIInfecPackTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:��Ⱦҽ���ײ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
public class ODIInfecPackControl extends TControl {
	TTable tablem;// ����
	TTable tableicd;// ICD��
	TTable tableorder;// ҽ���� ODIInf

	public void onInit() {
		super.onInit();
		this.setValue("SMEALTYPE", "W");
		onClickonchageButton();
		tablem = this.getTTable("TABLEM");
		tableicd = this.getTTable("TABLEICD");
		tableorder = this.getTTable("TABLEORDER");
		getTTable("TABLEORDER").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		getTTable("TABLEICD").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponentS");
		
		// ����ҽ�������¼�
		getTTable("TABLEORDER").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");
	}

	/**
	 *ORDERֵ�ı��¼�
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = getFactColumnName("TABLEORDER", col);
		int row = table.getSelectedRow();
		// ����ҽ��
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(table.getItemString(row, "LINKMAIN_FLG"))) {
				if (table.getItemString(row, "ORDER_CODE").length() == 0) {
					// �뿪��ҽ��
					this.messageBox("�뿪��ҽ��");
					table.setItem(row, "LINKMAIN_FLG", "N");
					return;
				}
				// ��ѯ��������
				int maxLinkNo = getMaxLinkNo();
				table.setItem(row, "LINK_NO", maxLinkNo);
			} else {
				table.setItem(row, "LINK_NO", "");
				if ("PHA".equals(table.getItemString(row, "CAT1_TYPE"))) {
					// �õ�PHA_BASE����
					TParm action = new TParm();
					action.setData("ORDER_CODE", table.getItemString(row,
							"ORDER_CODE"));
					TParm result = OdiMainTool.getInstance().queryPhaBase(
							action);
					table.setItem(row, "ROUTE_CODE", result.getValue(
							"ROUTE_CODE", 0));
					table.setItem(row, "FREQ_CODE", result.getValue(
							"FREQ_CODE", 0));
				} else {
					table.setItem(row, "ROUTE_CODE", "");
					table.setItem(row, "FREQ_CODE", "");
				}
			}
		}
	}

	/**
	 * �õ���������
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxLinkNo() {
		tableorder.acceptText();
		int result = 0;
		for (int i = 0; i < tableorder.getRowCount(); i++) {
			if (tableorder.getItemString(i, "ORDER_CODE").equals(""))
				continue;
			if (TypeTool.getInt(tableorder.getItemData(i, "LINK_NO")) > result){
				result = TypeTool.getInt(tableorder.getItemData(i, "LINK_NO"));
			}
		}
		return result == 0 ? 1 : result + 1;
	}

	/**
	 * ����ʵ������
	 * 
	 * @param column
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * �õ�����֮ǰ���к�
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTTable(table).getColumnModel().getColumnIndex(column);
	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		// �õ�����
		String columnName = this.getFactColumnName("TABLEORDER", column);
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}

	/**
	 * ����ֵ�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj;
		if ("ITEM".equals(tag)) {
			this.getTTable("TABLEORDER").acceptText();
			int selRow = this.getTTable("TABLEORDER").getSelectedRow();
			// ������
			onInsertOrderList(selRow, parm);
		}
	}

	/**
	 * ����ϸ��
	 * 
	 * @param type
	 *            int
	 */
	public void onInsertOrderList(int row, TParm parm) {
		tableorder.acceptText();
		String orderCode = parm.getValue("ORDER_CODE");
		if (!isAnti(orderCode)) {
			this.messageBox("��ҩƷΪ�ǿ�����,��ֹ����");
			tableorder.setItem(row, "ORDER_DESC", "");
			return;
		} else if (this.isSame(tableorder, orderCode, row)) {
			if (messageBox("��ʾ��Ϣ", "����ͬҽ���Ƿ�����ҽ��?", this.YES_NO_OPTION) != 0) {
				tableorder.setItem(row, "ORDER_DESC", "");
				return;
			} else {
				insertPackOrder(row, parm);
				if (row == tableorder.getRowCount() - 1)
					this.insertOrderRow();
			}
		} else {
			insertPackOrder(row, parm);
			if (row == tableorder.getRowCount() - 1)
				this.insertOrderRow();
		}
	}

	/**
	 */
	private boolean isAnti(String orderCode) {
		TParm actionDs = new TParm();
		actionDs.setData("ORDER_CODE", orderCode);
		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
		if (resultDs.getValue("ANTIBIOTIC_CODE", 0).equals("")) {
			return false;
		} else {
			return true;
		}
	}

	private void insertPackOrder(int row, TParm parm) {
		// TODO Auto-generated method stub
		tableorder.acceptText();
		if (row > 0) {
			String blinkNo = tableorder.getItemString(row - 1, "LINK_NO");
			if (blinkNo.length() != 0) {
				tableorder.setItem(row, "LINK_NO", blinkNo);
			} else {
				tableorder.setItem(row, "LINK_NO", "");
			}
		}
		tableorder.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
		tableorder.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
		tableorder.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
		tableorder.setItem(row, "ORDER_CAT1_CODE", parm
				.getData("ORDER_CAT1_CODE"));
		tableorder.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
		tableorder.setItem(row, "TRADE_ENG_DESC", parm
				.getData("TRADE_ENG_DESC"));
		tableorder.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
		tableorder.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
		tableorder.setItem(row, "TAKE_DAYS", 1);
		// �õ�PHA_BASE����
		TParm actionDs = new TParm();
		actionDs.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
		tableorder.setItem(row, "MEDI_QTY", resultDs.getData("MEDI_QTY", 0));
		tableorder.setItem(row, "MEDI_UNIT", resultDs.getData("MEDI_UNIT", 0));
		if (row > 0) {
			String ROUTE_CODE = tableorder.getItemString(row - 1, "ROUTE_CODE");
			if (ROUTE_CODE.length() != 0) {
				tableorder.setItem(row, "ROUTE_CODE", ROUTE_CODE);
			} else {
				tableorder.setItem(row, "ROUTE_CODE", resultDs.getData(
						"ROUTE_CODE", 0));
			}
			String FREQ_CODE = tableorder.getItemString(row - 1, "FREQ_CODE");
			if (FREQ_CODE.length() != 0) {
				tableorder.setItem(row, "FREQ_CODE", FREQ_CODE);
			} else {
				tableorder.setItem(row, "FREQ_CODE", resultDs.getData(
						"FREQ_CODE", 0));
			}
		} else {
			tableorder.setItem(row, "ROUTE_CODE", resultDs.getData(
					"ROUTE_CODE", 0));
			tableorder.setItem(row, "FREQ_CODE", "");
		}
	}

	/**
	 * 
	 * @param table
	 * @param orderCode
	 * @param row
	 * @return
	 */
	public boolean isSame(TTable table, String orderCode, int row) {
		table.acceptText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (i == row)
				continue;
			String orderCodeC = table.getItemString(i, "ORDER_CODE");
			if (orderCodeC.equals(orderCode))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public void onDeleteRowicd() {
		TTable table = getTTable("TABLEICD");
		int row = table.getTable().getSelectedRow();
		table.removeRow(row);
	}

	public void onDeleteRoworder() {
		TTable table = getTTable("TABLEORDER");
		int row = table.getTable().getSelectedRow();
		table.removeRow(row);
	}

	/**
	 * ��TABLE�����༭�ؼ�
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentS(Component com, int row, int column) {
		if (column != 0 && column != 2) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		String icd_type = "W";
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", icd_type);
		parm.setData("ICD_EXCLUDE", "Y");
		parm.setData("ICD_MIN_EX", "M80000/0");
		parm.setData("ICD_MAX_EX", "M99890/1");
		parm.setData("ICD_START_EX", "V");
		parm.setData("ICD_END_EX", "Y");
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		String returmMethodName = "";
		if (column == 0) {
			returmMethodName = "popReturnicdS";
		} else if (column == 2) {
			returmMethodName = "popReturnicdE";
		}
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				returmMethodName);
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnicdS(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		TTable table = tableicd;
		table.acceptText();
		String icd_code = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icd_code)) {
			table.setItem(table.getSelectedRow(), "ICD_DESC_BEGIN", orderDesc
					.getTableShowValue(icd_code));
			table.setItem(table.getSelectedRow(), "ICD_CODE_BEGIN", icd_code);
		}
		table.getTable().grabFocus();
		this.insertIcdRow();
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnicdE(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		TTable table = tableicd;
		table.acceptText();
		String icd_code = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icd_code)) {
			table.setItem(table.getSelectedRow(), "ICD_DESC_END", orderDesc
					.getTableShowValue(icd_code));
			table.setItem(table.getSelectedRow(), "ICD_CODE_END", icd_code);
		}
		table.getTable().grabFocus();
	}

	/**
	 * ģ����ѯ���ڲ��ࣩ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * ���һ��������
	 */
	public void insertIcdRow() {
		tableicd.acceptText();
		int row = tableicd.addRow();
		tableicd.setItem(row, "ICD_CODE_BEGIN", "");
		tableicd.setItem(row, "ICD_CODE_END", "");
	}

	/**
	 * ���һ��������
	 */
	public void insertOrderRow() {
		tableorder.acceptText();
		int row = tableorder.addRow();
		tableorder.setItem(row, "LINKMAIN_FLG", "N");
		tableorder.setItem(row, "LINK_NO", "");
		tableorder.setItem(row, "ORDER_DESC", "");
		tableorder.setItem(row, "MEDI_QTY", "");
		tableorder.setItem(row, "MEDI_UNIT", "");
		tableorder.setItem(row, "ROUTE_CODE", "");
		tableorder.setItem(row, "FREQ_CODE", "");
		tableorder.setItem(row, "SPECIFICATION", "");
		tableorder.setItem(row, "DESCRIPTION", "");
		tableorder.setItem(row, "ORDER_CODE", "");
		tableorder.setItem(row, "CAT1_TYPE", "");
		tableorder.setItem(row, "ORDER_CAT1_CODE", "");
		tableorder.setItem(row, "TRADE_ENG_DESC", "");
	}

	/**
	 * ����
	 */
	public void onNew() {
		this.onClear();
		String packCodestr = SystemTool.getInstance().getNo("ALL", "ODI",
				"ODIPACK_NO", "ODIPACK_NO");
		this.setValue("PACK_CODE", packCodestr);
	}

	/**
	 * 
	 * @return
	 */
	private TParm getIcdParm() {
		tableicd.acceptText();
		TParm parm = new TParm();
		int row = tableicd.getRowCount();
		int count = 1;
		for (int i = 0; i < row; i++) {
			if (tableicd.getItemString(i, "ICD_CODE_BEGIN").equals(""))
				continue;
			String begin=tableicd.getItemString(i, "ICD_CODE_BEGIN");
			String bdesc=tableicd.getItemString(i, "ICD_DESC_BEGIN");
			String end=tableicd.getItemString(i, "ICD_CODE_END");
			String edesc=tableicd.getItemString(i, "ICD_DESC_END");
			if(StringTool.compareTo(begin, end)>0){
				parm.setErrCode(-1);
				parm.setErrText(bdesc+" �������С��  "+edesc+" �������");
				return parm;
			}
			parm.addData("PACK_CODE", this.getValue("PACK_CODE"));
			parm.addData("ICD_TYPE_BEGIN", 'W');
			parm.addData("ICD_CODE_BEGIN", tableicd.getItemData(i,
					"ICD_CODE_BEGIN"));
			parm.addData("ICD_TYPE_END", 'W');
			parm.addData("ICD_CODE_END", tableicd
					.getItemData(i, "ICD_CODE_END"));
			parm.addData("SEQ", count);
			parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("OPT_USER", Operator.getID());
			count++;
		}
		parm.setCount(parm.getCount("PACK_CODE"));
		return parm;
	}

	/**
	 * 
	 * @return
	 */
	private TParm getOrderParm() {
		tableorder.acceptText();
		TParm parm = new TParm();
		int row = tableorder.getRowCount();
		int seq = 1;
		for (int i = 0; i < row; i++) {
			if (tableorder.getItemString(i, "ORDER_CODE").equals(""))
				continue;
			String order_desc=tableorder.getItemString(i, "ORDER_DESC");
			String freq=tableorder.getItemString(i, "FREQ_CODE");
			if(freq.equals("")){
				parm.setErrCode(-1);
				parm.setErrText(order_desc+"��Ƶ�β���Ϊ��");
				return parm;
			}
			parm.addData("PACK_CODE", this.getValue("PACK_CODE"));
			parm.addData("SEQ_NO", seq);
			parm.addData("LINKMAIN_FLG", tableorder.getItemData(i,
					"LINKMAIN_FLG"));
			parm.addData("LINK_NO", tableorder.getItemData(i, "LINK_NO"));
			parm.addData("ORDER_DESC", tableorder.getItemData(i, "ORDER_DESC"));
			parm.addData("MEDI_QTY", tableorder.getItemData(i, "MEDI_QTY"));
			parm.addData("MEDI_UNIT", tableorder.getItemData(i, "MEDI_UNIT"));
			parm.addData("ROUTE_CODE", tableorder.getItemData(i, "ROUTE_CODE"));
			parm.addData("FREQ_CODE", tableorder.getItemData(i, "FREQ_CODE"));
			parm.addData("SPECIFICATION", tableorder.getItemData(i,
					"SPECIFICATION"));
			parm.addData("DESCRIPTION", tableorder
					.getItemData(i, "DESCRIPTION"));
			parm.addData("ORDER_CODE", tableorder.getItemData(i, "ORDER_CODE"));
			parm.addData("CAT1_TYPE", tableorder.getItemData(i, "CAT1_TYPE"));
			parm.addData("ORDER_CAT1_CODE", tableorder.getItemData(i,
					"ORDER_CAT1_CODE"));
			parm.addData("TRADE_ENG_DESC", tableorder.getItemData(i,
					"TRADE_ENG_DESC"));
			parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("OPT_USER", Operator.getID());
			seq++;
		}
		parm.setCount(parm.getCount("PACK_CODE"));
		return parm;
	}

	/**
	 * �����¼�
	 */
	public void onTableMclick() {
		int row = tablem.getSelectedRow();
		if (row < 0) {
			return;
		}
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		TParm parmRow = tablem.getParmValue().getRow(row);
		this.setValueForParm("PACK_CODE;PACK_DESC;PY1;MESSAGE;PHA_PREVENCODE;SMEALTYPE", parmRow);
		if (this.getValue("SMEALTYPE").equals("OP")) {//��������
			callFunction("UI|PHA_PREVENCODE|setEnabled", true);
		}else{
			callFunction("UI|PHA_PREVENCODE|setEnabled", false);
		}
		String packcode = parmRow.getValue("PACK_CODE");
		TParm parmicd = ODIInfecPackTool.getInstance()
				.getInfecIcdParm(packcode);
		if (parmicd.getErrCode() < 0) {
			this.messageBox("�ײ���Ϸ�Χ��ѯ����");
			return;
		}
		for (int i = 0; i < parmicd.getCount(); i++) {
			parmicd.addData("ICD_DESC_BEGIN", orderDesc
					.getTableShowValue(parmicd.getValue("ICD_CODE_BEGIN", i)));
			parmicd.addData("ICD_DESC_END", orderDesc.getTableShowValue(parmicd
					.getValue("ICD_CODE_END", i)));
		}
		tableicd.setParmValue(parmicd);
		if(this.getValueString("SMEALTYPE").equals("W")){// add caoyong 2013 �ж��ײ�����
			insertIcdRow();
		}
		
		TParm parmorder = ODIInfecPackTool.getInstance().getInfecOrderParm(
				packcode);
		if (parmorder.getErrCode() < 0) {
			this.messageBox("�ײ�ҽ����ѯ����");
			return;
		}
		tableorder.setParmValue(parmorder);
		this.insertOrderRow();
	}
	
	
	
/**
 * 	�ײ�����:W:��� OP:���� 
 *  add  caoyong 2013830
 */
	
	public void onClickonchageButton() {
		if("OP".equalsIgnoreCase(getValueString("SMEALTYPE"))){
			callFunction("UI|PHA_PREVENCODE|setEnabled", true);
			this.grabFocus("PHA_PREVENCODE");
		}
		if ("W".equalsIgnoreCase(this.getValueString("SMEALTYPE"))) {
			callFunction("UI|PHA_PREVENCODE|setEnabled", false);
			this.grabFocus("PHA_PREVENCODE");
		}
		    //this.onClear();
	}
	
	
	/**
	 * ����
	 */
	public void onSave() {
		tableicd.acceptText();
		tableorder.acceptText();
		if("OP".equalsIgnoreCase(getValueString("SMEALTYPE"))){//add caoyong 2013830 �ж��ײ�����
			if("".equals(getValueString("PHA_PREVENCODE"))){//add caoyong 2013829 �������������Ͳ���Ϊ��
				messageBox("�������������Ͳ���Ϊ��");
				this.grabFocus("PHA_PREVENCODE");
				return;
			}	
		}else{
			callFunction("UI|PHA_PREVENCODE|setEnabled", false);
			this.setValue("PHA_PREVENCODE", "");//��ϲ���Ҫ��������
		}
		TParm parmM = new TParm();
		parmM.setData("PHA_PREVENCODE", this.getValueString("PHA_PREVENCODE"));//add �������������� coyong 2013829
		parmM.setData("SMEALTYPE", this.getValueString("SMEALTYPE"));//add �ײ������ײ�����  coyong 2013829
		parmM.setData("PACK_CODE", this.getValueString("PACK_CODE"));
		parmM.setData("PACK_CODE", this.getValueString("PACK_CODE"));
		parmM.setData("PACK_DESC", this.getValueString("PACK_DESC"));
		parmM.setData("PY1", this.getValueString("PY1"));
		parmM.setData("MESSAGE", this.getValueString("MESSAGE"));
		parmM.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parmM.setData("OPT_TERM", Operator.getIP());
		parmM.setData("OPT_USER", Operator.getID());
		TParm parmicd = getIcdParm();
		if(parmicd.getErrCode()<0){
			this.messageBox(parmicd.getErrText());
			return;
		}
		TParm parmorder = this.getOrderParm();
		if(parmorder.getErrCode()<0){
			this.messageBox(parmorder.getErrText());
			return;
		}
		if (this.getValueString("PACK_CODE").equals("")) {
			this.messageBox("�ײͱ���Ϊ��");
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("mPARM", parmM.getData());
		inparm.setData("icdPARM", parmicd.getData());
		inparm.setData("orderPARM", parmorder.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.odi.ODIInfecPackAction", "onSave", inparm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			onQuery();
			return;
		}
		this.messageBox("����ɹ�");
		onQuery();
	}
	/**
	 * ɾ��
	 */
	public void onDelete() {
		tablem.acceptText();
		tableicd.acceptText();
		tableorder.acceptText();
		TParm parmM = tablem.getParmValue();
		if (parmM.getCount() <= 0) {
			this.messageBox("û�б��������");
			return;
		}
		int row = tablem.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("mPARM", parmM.getRow(row).getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.odi.ODIInfecPackAction", "onDelete", inparm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			onQuery();
			return;
		}
		this.messageBox("����ɹ�");
		this.onClear();
		onQuery();
	}

	/**
	 * ���ƻس��¼�
	 */
	public void onUserNameAction() {
		String userName = getValueString("PACK_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String where = "";
		if (!this.getValueString("PACK_CODE").equals(""))
			where = " WHERE PACK_CODE='" + this.getValueString("PACK_CODE")
					+ "' ";
		if (!this.getValueString("PACK_DESC").equals("")) {
			if (where.length() > 0) {
				where += " AND PACK_DESC='" + this.getValueString("PACK_DESC")+"' ";
			} else {
				where += " WHERE PACK_DESC='"
						+ this.getValueString("PACK_DESC")+"' ";
						
			}
		}
		where +=" ORDER BY PACK_CODE";
		String sql = "SELECT * FROM ODI_INFEC_PACKM " + where;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("��ѯ����");
			return;
		}
		if (parm.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return;
		}
		this.tablem.setParmValue(parm);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("PACK_CODE;PACK_DESC;PY1;MESSAGE;PHA_PREVENCODE;SMEALTYPE;PHA_PREVENCODE");
		tablem.removeRowAll();
		tableicd.removeRowAll();
		tableorder.removeRowAll();
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
}
