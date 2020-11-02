package com.javahis.ui.odi;

import com.dongyang.control.*;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.odi.ODIPACKMAINDataStore;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import jdo.odi.ODIPACKORDERDataStore;
import java.awt.Component;
import com.dongyang.jdo.TDataStore;
import jdo.odi.OdiMainTool;
import java.util.Map;
import java.util.HashMap;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.javahis.util.OrderUtil;
import com.dongyang.data.TNull;

/**
 * <p>
 * Title: סԺҽ��վ�ײ�
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author WangM
 * @version 1.0
 */
public class ODIPackBackControl extends TControl {
	/**
	 * ҽ�����
	 */
	private String rxKind = "ST";
	/**
	 * ��ǰ�༭���ײ�
	 */
	private String packCodeOnly = "";
	/**
	 * ��ǰ��Ҳǩ
	 */
	private int pageIndex;
	/**
	 * ��ʱ��ҩԤ��Ƶ��
	 */
	private String odiUddStatCode;
	/**
	 * ��ʱ����Ԥ��Ƶ��
	 */
	private String odiStatCode;
	/**
	 * ���ڴ���Ԥ��Ƶ��
	 */
	private String odiDefaFreg;
	/**
	 * Ԥ����Ƭ����
	 */
	private String dctTakeDays;
	/**
	 * Ԥ����Ƭʹ�ü���
	 */
	private String dctTakeQty;
	/**
	 * ��ҩ����Ƶ�Σ�Ĭ��һ������
	 */
	private String gFreqCode;
	/**
	 * ��ҩ�����÷����ڷ�
	 */
	private String gRouteCode;
	/**
	 * �巨
	 */
	private String gDctagentCode;
	/**
	 * ��ҽ������
	 */
	private ODIPACKORDERDataStore igOrderData;
	/**
	 * ��ҽ��ǰ�༭��
	 */
	private int rowOnly = -1;

	/**
	 * ����������
	 */
	private String actionName = "action.odi.ODIAction";
	/**
	 * Ȩ��
	 */
	private String rule;

	/**
	 * ��ʼ������
	 */
	public void onInitParameter() {
		// ��������
		// this.setPopedem("deptEnabled",true);
		// this.setPopedem("operatorEnabled",true);

	}

	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		// if(obj==null||obj.toString().length()==0){
		// this.messageBox("��������Ȩ��");
		// this.callFunction("UI|new|setEnabled", false);
		// this.callFunction("UI|save|setEnabled", false);
		// this.callFunction("UI|delete|setEnabled", false);
		// return;
		// }
		rule = (String) this.getParameter();
		// this.rule = "ODI";
		onInitPage();
		onInitPopeDem();
	}

	/**
	 * �ײ���������¼�
	 */
	public void onSelPackType() {
		TComboBox packType = (TComboBox) this.getComponent("DEPT_OR_DR");
		String id = packType.getSelectedID();
		if ("ODI".equals(this.rule)) {
			if ("3".equals(id) || "4".equals(id)) {
				this.messageBox("�����Ա༭��");
				this.setValue("DEPT_OR_DR", "");
				return;
			}
		}
		if ("IBS".equals(this.rule)) {
			if ("1".equals(id) || "2".equals(id)) {
				this.messageBox("�����Ա༭��");
				this.setValue("DEPT_OR_DR", "");
				return;
			}
		}
		if (this.getPopedem("deptEnabled")) {
			if ("1".equals(id) || "3".equals(id)) {
				TextFormatDept dept = (TextFormatDept) this.getComponent("DEPT_CODE");
				TextFormatSYSOperator user = (TextFormatSYSOperator) this.getComponent("USER_ID");
				if (user.isEnabled()) {
					user.setEnabled(false);
				}
				if (!dept.isEnabled()) {
					dept.setEnabled(true);
				}
			}
		}
		if (this.getPopedem("operatorEnabled")) {
			if ("2".equals(id) || "4".equals(id)) {
				TextFormatDept dept = (TextFormatDept) this.getComponent("DEPT_CODE");
				TextFormatSYSOperator user = (TextFormatSYSOperator) this.getComponent("USER_ID");
				if (dept.isEnabled()) {
					dept.setEnabled(false);
				}
				if (!user.isEnabled()) {
					user.setEnabled(true);
				}
			}
		}
	}

	/**
	 * ҳǩ�ı��¼�
	 */
	public void onChange() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			this.rxKind = "ST";
			if (pageIndex != 0) {
				if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
					if (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_OPTION) != 0) {
						tab.setSelectedIndex(pageIndex);
						return;
					} else {
						this.onSave();
					}
				}
			}
			this.packCodeOnly = "";
			pageIndex = 0;
			// ��������TABLE
			this.getTTable("ITEMTABLE").setHeader(
					"��,30,boolean;��,50,int;ҽ������,260;����,60,double,#########0.000;��λ,60,UNIT_COMBO;�÷�,60,ROUTE_CODE;Ƶ��,90,FREQ_CODE;���,180;��ע,200");
			this.getTTable("ITEMTABLE").setFocusIndexList("2,3,5,6");
			this.getTTable("ITEMTABLE").setLockColumns("4,7");
			this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
			this.getTTable("ITEMTABLE").setParmMap(
					"LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
			this.getTTable("ITEMTABLE").setAutoModifyDataStore(true);
			ODIPACKORDERDataStore dsSt = new ODIPACKORDERDataStore();
			dsSt.setPackCode(this.packCodeOnly);
			dsSt.onQuery();
			this.getTTable("ITEMTABLE").setDataStore(dsSt);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		case 1:
			this.rxKind = "UD";
			if (pageIndex != 1) {
				if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
					if (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_OPTION) != 0) {
						tab.setSelectedIndex(pageIndex);
						return;
					} else {
						this.onSave();
					}
				}
			}
			this.packCodeOnly = "";
			pageIndex = 1;
			this.getTTable("ITEMTABLE").setHeader(
					"��,30,boolean;��,50,int;ҽ������,260;����,60,double,#########0.000;��λ,60,UNIT_COMBO;�÷�,60,ROUTE_CODE;Ƶ��,90,FREQ_CODE;���,180;��ע,200");
			this.getTTable("ITEMTABLE").setFocusIndexList("2,3,5,6");
			this.getTTable("ITEMTABLE").setLockColumns("4,7");
			this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
			this.getTTable("ITEMTABLE").setParmMap(
					"LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
			this.getTTable("ITEMTABLE").setAutoModifyDataStore(true);
			ODIPACKORDERDataStore dsUd = new ODIPACKORDERDataStore();
			dsUd.setPackCode(this.packCodeOnly);
			dsUd.onQuery();
			this.getTTable("ITEMTABLE").setDataStore(dsUd);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		case 2:
			this.rxKind = "DS";
			if (pageIndex != 2) {
				if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
					if (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_OPTION) != 0) {
						tab.setSelectedIndex(pageIndex);
						return;
					} else {
						this.onSave();
					}
				}
			}
			this.packCodeOnly = "";
			pageIndex = 2;
			this.getTTable("ITEMTABLE").setHeader(
					"��,30,boolean;��,50,int;ҽ������,260;����,60,double,#########0.000;��λ,60,UNIT_COMBO;�÷�,60,ROUTE_CODE;����,40,int;Ƶ��,90,FREQ_CODE;���,180;��ע,200");
			this.getTTable("ITEMTABLE").setFocusIndexList("2,3,5,6,7");
			this.getTTable("ITEMTABLE").setLockColumns("4,8");
			this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
			this.getTTable("ITEMTABLE").setParmMap(
					"LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;TAKE_DAYS;FREQ_CODE;SPECIFICATION;DESCRIPTION");
			this.getTTable("ITEMTABLE").setAutoModifyDataStore(true);
			ODIPACKORDERDataStore dsDs = new ODIPACKORDERDataStore();
			dsDs.setPackCode(this.packCodeOnly);
			dsDs.onQuery();
			this.getTTable("ITEMTABLE").setDataStore(dsDs);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		case 3:
			this.rxKind = "IG";
			if (pageIndex != 3) {
				if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
					if (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_OPTION) != 0) {
						tab.setSelectedIndex(pageIndex);
						return;
					} else {
						this.onSave();
					}
				}
			}
			this.packCodeOnly = "";
			pageIndex = 3;
			this.getTTable("ITEMTABLE").setHeader(
					"ҽ������,120;����,100,double,#########0.000;����巨,100,DCTAGENT_COMBO;ҽ������,120;����,100,double,#########0.000;����巨,100,DCTAGENT_COMBO;ҽ������,120;����,100,double,#########0.000;����巨,100,DCTAGENT_COMBO;ҽ������,120;����,100,double,#########0.000;����巨,100,DCTAGENT_COMBO");
			this.getTTable("ITEMTABLE").setFocusIndexList("0,3,6,9");
			this.getTTable("ITEMTABLE").setLockColumns("");
			this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData(
					"0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left;");
			this.getTTable("ITEMTABLE").setParmMap(
					"ORDER_DESC_1;MEDI_QTY_1;DCTEXCEP_CODE_1;ORDER_DESC_2;MEDI_QTY_2;DCTEXCEP_CODE_2;ORDER_DESC_3;MEDI_QTY_3;DCTEXCEP_CODE_3;ORDER_DESC_4;MEDI_QTY_4;DCTEXCEP_CODE_4");
			this.getTTable("ITEMTABLE").setAutoModifyDataStore(false);
			ODIPACKORDERDataStore dsIg = new ODIPACKORDERDataStore();
			dsIg.setPackCode(this.packCodeOnly);
			dsIg.onQuery();
			this.getTTable("ITEMTABLE").setDataStore(dsIg);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		}
	}

	/**
	 * �õ�TABLE����
	 * 
	 * @param type
	 *            int
	 * @return String
	 */
	public String getTableTag(int type) {
		if (0 == type) {
			return "ST";
		}
		if (1 == type) {
			return "UD";
		}
		if (2 == type) {
			return "DS";
		}
		if (3 == type) {
			return "IG";
		}
		return "ST";
	}

	/**
	 * ��ʼ������
	 */
	public void onInitPage() {
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("USER_ID", Operator.getID());
		// TTextField QUERY = (TTextField)this.getComponent("PACK_DESC");
		// TParm parm = new TParm();
		// parm.setData("CAT1_TYPE", "");
		// QUERY.setPopupMenuParameter("QUERYPACK", getConfigParm().newConfig(
		// "%ROOT%\\config\\odi\\ODIPackOrderListPopup.x"), parm);
		// // ������ܷ���ֵ����
		// QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// ��ʱTABLEֵ�ı����
		addEventListener("TABLE1" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTableValueST");
		// ����TABLEֵ�ı����
		addEventListener("TABLE2" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTableValueUD");
		// ��Ժ��ҩTABLEֵ�ı����
		addEventListener("TABLE3" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTableValueDS");
		// ��ҩ��ƬTABLEֵ�ı����
		addEventListener("TABLE4" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTableValueIG");
		// TABLE1�����¼�
		callFunction("UI|" + "TABLE1" + "|addEventListener", "TABLE1" + "->" + TTableEvent.CLICKED, this,
				"onTableClicked");
		// TABLE2�����¼�
		callFunction("UI|" + "TABLE2" + "|addEventListener", "TABLE2" + "->" + TTableEvent.CLICKED, this,
				"onTableClicked");
		// TABLE3�����¼�
		callFunction("UI|" + "TABLE3" + "|addEventListener", "TABLE3" + "->" + TTableEvent.CLICKED, this,
				"onTableClicked");
		// TABLE4�����¼�
		callFunction("UI|" + "TABLE4" + "|addEventListener", "TABLE4" + "->" + TTableEvent.CLICKED, this,
				"onTableClicked");
		// ��ʱTABLE1�����¼�
		getTTable("ITEMTABLE").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponent");
		// ����ҽ�������¼�
		getTTable("ITEMTABLE").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxValue");
		// ORDERETABLEֵ�ı����
		addEventListener("ITEMTABLE" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTableValue");
		// ����¼�
		callFunction("UI|" + "ITEMTABLE" + "|addEventListener", "ITEMTABLE" + "->" + TTableEvent.CLICKED, this,
				"onTableClickedItem");

		// ��ǰ��Ҳǩ
		this.pageIndex = 0;
		// ��ʱ��ҩԤ��Ƶ��
		this.setOdiUddStatCode(getOdiSysParmData("UDD_STAT_CODE").toString());
		// ��ʱ����Ԥ��Ƶ��
		this.setOdiStatCode(getOdiSysParmData("ODI_STAT_CODE").toString());
		// ���ڴ���Ԥ��Ƶ��
		this.setOdiDefaFreg(getOdiSysParmData("ODI_DEFA_FREG").toString());
		// Ԥ����Ƭ����
		this.setDctTakeDays(getOpdSysParmData("DCT_TAKE_DAYS").toString());
		// Ԥ����Ƭʹ�ü���
		this.setDctTakeQty(getOpdSysParmData("DCT_TAKE_QTY").toString());
		// ��ҩ����Ƶ�Σ�Ĭ��һ������
		this.setGFreqCode(getOpdSysParmData("G_FREQ_CODE").toString());
		// ��ҩ�����÷����ڷ�
		this.setGRouteCode(getOpdSysParmData("G_ROUTE_CODE").toString());
		// ��ҽ���ã��巨
		this.setGDctagentCode(getOpdSysParmData("G_DCTAGENT_CODE").toString());
	}

	/**
	 * �õ�סԺ����
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOdiSysParmData(String key) {
		return OdiMainTool.getInstance().getOdiSysParmData(key);
	}

	/**
	 * �õ��������
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOpdSysParmData(String key) {
		return OdiMainTool.getInstance().getOpdSysParmData(key);
	}

	/**
	 * TABLE����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedItem(int row) {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			this.getTTable("TABLE1").acceptText();
			this.getTTable("TABLE1").clearSelection();
			break;
		case 1:
			this.getTTable("TABLE2").acceptText();
			this.getTTable("TABLE2").clearSelection();
			break;
		case 2:
			this.getTTable("TABLE3").acceptText();
			this.getTTable("TABLE3").clearSelection();
			break;
		case 3:
			this.getTTable("TABLE4").acceptText();
			this.getTTable("TABLE4").clearSelection();
			break;
		}
	}

	/**
	 * ORDERֵ�ı��¼�
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable("ITEMTABLE").getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm linkParm = table.getDataStore().getRowParm(row);
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			// ����ҽ��
			if ("LINKMAIN_FLG".equals(columnName)) {
				if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
					if (linkParm.getValue("ORDER_CODE").length() == 0) {
						// �뿪��ҽ��
						this.messageBox("�뿪��ҽ��");
						table.getDataStore().setItem(row, "LINKMAIN_FLG", "N");
						table.setDSValue();
						return;
					}
					// ��ѯ��������
					int maxLinkNo = getMaxLinkNo();
					ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
					ds.setItem(row, "LINK_NO", maxLinkNo);
					table.setDSValue(row);
				} else {
					ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
					ds.setItem(row, "LINK_NO", "");
					if ("PHA".equals(linkParm.getValue("CAT1_TYPE"))) {
						// �õ�PHA_BASE����
						TParm action = new TParm();
						action.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
						TParm result = OdiMainTool.getInstance().queryPhaBase(action);
						ds.setItem(row, "ROUTE_CODE", result.getValue("ROUTE_CODE", 0));
						ds.setItem(row, "FREQ_CODE", getOdiUddStatCode());
					} else {
						ds.setItem(row, "ROUTE_CODE", "");
						ds.setItem(row, "FREQ_CODE", getOdiStatCode());
					}
					table.setDSValue(row);
				}
			}
			break;
		case 1:
			// ����ҽ��
			if ("LINKMAIN_FLG".equals(columnName)) {
				if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
					if (linkParm.getValue("ORDER_CODE").length() == 0) {
						// �뿪��ҽ��
						this.messageBox("�뿪��ҽ��");
						table.getDataStore().setItem(row, "LINKMAIN_FLG", "N");
						table.setDSValue();
						return;
					}
					// ��ѯ��������
					int maxLinkNo = getMaxLinkNo();
					ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
					ds.setItem(row, "LINK_NO", maxLinkNo);
					table.setDSValue(row);
				} else {
					ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
					ds.setItem(row, "LINK_NO", "");
					if ("PHA".equals(linkParm.getValue("CAT1_TYPE"))) {
						// �õ�PHA_BASE����
						TParm action = new TParm();
						action.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
						TParm result = OdiMainTool.getInstance().queryPhaBase(action);
						ds.setItem(row, "ROUTE_CODE", result.getValue("ROUTE_CODE", 0));
						ds.setItem(row, "FREQ_CODE", result.getValue("FREQ_CODE", 0));
					} else {
						ds.setItem(row, "ROUTE_CODE", "");
						ds.setItem(row, "FREQ_CODE", getOdiDefaFreg());
					}
					table.setDSValue(row);
				}
			}
			break;
		case 2:
			// ����ҽ��
			if ("LINKMAIN_FLG".equals(columnName)) {
				if ("Y".equals(linkParm.getValue("LINKMAIN_FLG"))) {
					if (linkParm.getValue("ORDER_CODE").length() == 0) {
						// �뿪��ҽ��
						this.messageBox("�뿪��ҽ��");
						table.getDataStore().setItem(row, "LINKMAIN_FLG", "N");
						table.setDSValue();
						return;
					}
					// ��ѯ��������
					int maxLinkNo = getMaxLinkNo();
					ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
					ds.setItem(row, "LINK_NO", maxLinkNo);
					table.setDSValue(row);
				} else {
					ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
					ds.setItem(row, "LINK_NO", "");
					if ("PHA".equals(linkParm.getValue("CAT1_TYPE"))) {
						// �õ�PHA_BASE����
						TParm action = new TParm();
						action.setData("ORDER_CODE", linkParm.getValue("ORDER_CODE"));
						TParm result = OdiMainTool.getInstance().queryPhaBase(action);
						ds.setItem(row, "ROUTE_CODE", result.getValue("ROUTE_CODE", 0));
						ds.setItem(row, "FREQ_CODE", result.getValue("FREQ_CODE", 0));
					} else {
						ds.setItem(row, "ROUTE_CODE", "");
						ds.setItem(row, "FREQ_CODE", getOdiDefaFreg());
					}
					table.setDSValue(row);
				}
			}
			break;
		case 3:
			// Ԥ��
			break;
		}
	}

	/**
	 * �õ���������
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxLinkNo() {
		ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
		String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		TParm parm = ds.getBuffer(buff);
		int result = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("#ACTIVE#", i))
				continue;
			if (parm.getRow(i).getInt("LINK_NO") > result)
				result = parm.getRow(i).getInt("LINK_NO");
		}
		// this.messageBox("����ֵ:"+result);
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
	 * ��TABLE�����༭�ؼ�ʱ��ʱ
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		// �õ�����
		String columnName = this.getFactColumnName("ITEMTABLE", column);
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		// ��ʱҽ������
		if (tab.getSelectedIndex() != 3) {
			parm.setData("ODI_ORDER_TYPE", "A");
		} else {
			String columnArray[] = columnName.split("_");
			TParm columnParm = this.getTTable("ITEMTABLE").getParmValue().getRow(row);
			this.rowOnly = getIGEditRowSetId(columnParm.getInt("ROW_ID_" + columnArray[columnArray.length - 1]));
			parm.setData("ODI_ORDER_TYPE", "F");
		}
		// ���õ����˵�
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * �õ�סԺ��ҽ��ǰ�༭��
	 * 
	 * @param row
	 *            int
	 * @return int
	 */
	public int getIGEditRowSetId(int row) {
		int rowId = -1;
		igOrderData.showDebug();
		int rowCount = igOrderData.rowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowSet = igOrderData.getItemInt(i, "#ID#");
			if (row == rowSet) {
				rowId = i;
				break;
			}
		}
		return rowId;
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
		if ("QUERYPACK".equals(tag)) {
			return;
		}
		if ("ITEM".equals(tag)) {
			this.getTTable("ITEMTABLE").acceptText();
			int selRow = this.getTTable("ITEMTABLE").getSelectedRow();
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
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		ODIPACKORDERDataStore orderds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
		String orderCode = parm.getValue("ORDER_CODE");
		if (tab.getSelectedIndex() != 3) {
			if (this.isSame(orderds, orderCode, row)) {
				if (messageBox("��ʾ��Ϣ", "����ͬҽ���Ƿ�����ҽ��?", this.YES_NO_OPTION) != 0) {
					orderds.setItem(row, "ORDER_DESC", "");
					this.getTTable("ITEMTABLE").setDSValue(row);
					return;
				} else {
					insertPackOrder(row, parm);
				}
			} else {
				insertPackOrder(row, parm);
			}
		} else {
			if (this.isSame(this.igOrderData, orderCode, this.rowOnly)) {
				if (messageBox("��ʾ��Ϣ", "����ͬҽ���Ƿ�����ҽ��?", this.YES_NO_OPTION) != 0) {
					return;
				} else {
					insertPackOrder(row, parm);
				}
			} else {
				insertPackOrder(row, parm);
			}
		}
	}

	/**
	 * ����ϸ��
	 * 
	 * @param row
	 *            int
	 * @param parm
	 *            TParm
	 */
	public void insertPackOrder(int row, TParm parm) {
		// ��ҩѡ����
		int columnIG = -1;
		// ��ҩѡ����
		int rowIG = -1;
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		ODIPACKORDERDataStore orderds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
		switch (tab.getSelectedIndex()) {
		case 0:
			if (row > 0) {
				TParm temp = orderds.getRowParm(row - 1);
				if (temp.getValue("LINK_NO").length() != 0 && "PHA".equals(parm.getValue("CAT1_TYPE"))) {
					orderds.setItem(row, "LINK_NO", temp.getData("LINK_NO"));
				} else {
					orderds.setItem(row, "LINK_NO", "");
				}
			}
			orderds.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
			orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			orderds.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
			orderds.setItem(row, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
			orderds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
			orderds.setItem(row, "TRADE_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
			orderds.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
			orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
				TParm result = OdiMainTool.getInstance().queryPhaBase(action);
				orderds.setItem(row, "MEDI_QTY", result.getData("MEDI_QTY", 0));
				orderds.setItem(row, "MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				if (row > 0) {
					TParm temp = orderds.getRowParm(row - 1);
					if (temp.getValue("LINK_NO").length() != 0) {
						orderds.setItem(row, "ROUTE_CODE", temp.getValue("ROUTE_CODE"));
						orderds.setItem(row, "FREQ_CODE", temp.getValue("FREQ_CODE"));
					} else {
						orderds.setItem(row, "ROUTE_CODE", result.getData("ROUTE_CODE", 0));
						orderds.setItem(row, "FREQ_CODE", this.getOdiUddStatCode());
					}
				} else {
					orderds.setItem(row, "ROUTE_CODE", result.getData("ROUTE_CODE", 0));
					orderds.setItem(row, "FREQ_CODE", this.getOdiUddStatCode());
				}
			} else {
				orderds.setItem(row, "MEDI_QTY", 1);
				orderds.setItem(row, "MEDI_UNIT", parm.getData("UNIT_CODE"));
				orderds.setItem(row, "ROUTE_CODE", "");
				orderds.setItem(row, "FREQ_CODE", getOdiStatCode());
			}
			orderds.setActive(row, true);
			break;
		case 1:
			if ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS".equals(parm.getValue("CAT1_TYPE"))) {
				this.messageBox("����ҽ�����ɿ���");
				orderds.setItem(row, "ORDER_DESC", "");
				this.getTTable("ITEMTABLE").setDSValue(row);
				return;
			}
			if (row > 0) {
				TParm temp = orderds.getRowParm(row - 1);
				if (temp.getValue("LINK_NO").length() != 0 & "PHA".equals(parm.getValue("CAT1_TYPE"))) {
					orderds.setItem(row, "LINK_NO", temp.getData("LINK_NO"));
				} else {
					orderds.setItem(row, "LINK_NO", "");
				}
			}
			orderds.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
			orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			orderds.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
			orderds.setItem(row, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
			orderds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
			orderds.setItem(row, "TRADE_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
			orderds.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
			orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
				// �õ�PHA_BASE����
				TParm action = new TParm();
				action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
				TParm result = OdiMainTool.getInstance().queryPhaBase(action);
				orderds.setItem(row, "MEDI_QTY", result.getData("MEDI_QTY", 0));
				orderds.setItem(row, "MEDI_UNIT", result.getData("MEDI_UNIT", 0));
				if (row > 0) {
					TParm temp = orderds.getRowParm(row - 1);
					if (temp.getValue("LINK_NO").length() != 0) {
						orderds.setItem(row, "ROUTE_CODE", temp.getValue("ROUTE_CODE"));
						orderds.setItem(row, "FREQ_CODE", temp.getValue("FREQ_CODE"));
					} else {
						orderds.setItem(row, "ROUTE_CODE", result.getData("ROUTE_CODE", 0));
						orderds.setItem(row, "FREQ_CODE", this.getOdiUddStatCode());
					}
				} else {
					orderds.setItem(row, "ROUTE_CODE", result.getData("ROUTE_CODE", 0));
					orderds.setItem(row, "FREQ_CODE", this.getOdiUddStatCode());
				}
			} else {
				orderds.setItem(row, "MEDI_QTY", 1);
				orderds.setItem(row, "MEDI_UNIT", parm.getData("UNIT_CODE"));
				orderds.setItem(row, "ROUTE_CODE", "");
				orderds.setItem(row, "FREQ_CODE", this.getOdiDefaFreg());
			}
			orderds.setActive(row, true);
			break;
		case 2:
			if (!"PHA".equals(parm.getValue("CAT1_TYPE"))) {
				this.messageBox("��Ժ��ҩ���ɿ���");
				orderds.setItem(row, "ORDER_DESC", "");
				this.getTTable("ITEMTABLE").setDSValue(row);
				return;
			}
			if (row > 0) {
				TParm temp = orderds.getRowParm(row - 1);
				if (temp.getValue("LINK_NO").length() != 0) {
					orderds.setItem(row, "LINK_NO", temp.getData("LINK_NO"));
				} else {
					orderds.setItem(row, "LINK_NO", "");
				}
			}
			orderds.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
			orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			orderds.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
			orderds.setItem(row, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
			orderds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
			orderds.setItem(row, "TRADE_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
			orderds.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
			orderds.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			orderds.setItem(row, "TAKE_DAYS", 1);
			// �õ�PHA_BASE����
			TParm actionDs = new TParm();
			actionDs.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
			orderds.setItem(row, "MEDI_QTY", resultDs.getData("MEDI_QTY", 0));
			orderds.setItem(row, "MEDI_UNIT", resultDs.getData("MEDI_UNIT", 0));
			if (row > 0) {
				TParm temp = orderds.getRowParm(row - 1);
				if (temp.getValue("LINK_NO").length() != 0) {
					orderds.setItem(row, "ROUTE_CODE", temp.getValue("ROUTE_CODE"));
					orderds.setItem(row, "FREQ_CODE", temp.getValue("FREQ_CODE"));
				} else {
					orderds.setItem(row, "ROUTE_CODE", resultDs.getData("ROUTE_CODE", 0));
					orderds.setItem(row, "FREQ_CODE", this.getOdiUddStatCode());
				}
			} else {
				orderds.setItem(row, "ROUTE_CODE", resultDs.getData("ROUTE_CODE", 0));
				orderds.setItem(row, "FREQ_CODE", this.getOdiUddStatCode());
			}
			orderds.setActive(row, true);
			break;
		case 3:
			this.igOrderData.setItem(this.rowOnly, "ORDER_CODE", parm.getData("ORDER_CODE"));
			this.igOrderData.setItem(this.rowOnly, "DESCRIPTION", parm.getData("DESCRIPTION"));
			this.igOrderData.setItem(this.rowOnly, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
			this.igOrderData.setItem(this.rowOnly, "ORDER_CAT1_CODE", parm.getData("ORDER_CAT1_CODE"));
			this.igOrderData.setItem(this.rowOnly, "ORDER_DESC", parm.getData("ORDER_DESC"));
			this.igOrderData.setItem(this.rowOnly, "TRADE_ENG_DESC", parm.getData("TRADE_ENG_DESC"));
			this.igOrderData.setItem(this.rowOnly, "SPECIFICATION", parm.getData("SPECIFICATION"));
			this.igOrderData.setItem(this.rowOnly, "DESCRIPTION", parm.getData("DESCRIPTION"));
			// �õ�PHA_BASE����
			TParm actionIG = new TParm();
			actionIG.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			TParm resultIG = OdiMainTool.getInstance().queryPhaBase(actionIG);
			this.igOrderData.setItem(this.rowOnly, "MEDI_QTY", this.getDctTakeQty());
			this.igOrderData.setItem(this.rowOnly, "MEDI_UNIT", resultIG.getData("MEDI_UNIT", 0));
			this.igOrderData.setItem(this.rowOnly, "DCTEXCEP_CODE", "");
			this.igOrderData.setActive(rowOnly, true);
			columnIG = this.getTTable("ITEMTABLE").getSelectedColumn();
			rowIG = this.getTTable("ITEMTABLE").getSelectedRow();
			break;
		}
		// �Ƿ���δ�༭��ҽ��
		if (!isNew(orderds) && tab.getSelectedIndex() != 3) {
			int insRow = orderds.insertRow();
			orderds.setItem(insRow, "PACK_CODE", orderds.getItemData(row, "PACK_CODE"));
			orderds.setItem(insRow, "DEPT_OR_DR", orderds.getItemData(row, "DEPT_OR_DR"));
			orderds.setItem(insRow, "DEPTORDR_CODE", orderds.getItemData(row, "DEPTORDR_CODE"));
			orderds.setActive(insRow, false);
			this.getTTable("ITEMTABLE").setDSValue();
			this.getTTable("ITEMTABLE").getTable().grabFocus();
			this.getTTable("ITEMTABLE").setSelectedRow(row);
			this.getTTable("ITEMTABLE").setSelectedColumn(3);
		}
		if (tab.getSelectedIndex() != 3) {
			this.getTTable("ITEMTABLE").setDSValue();
			this.getTTable("ITEMTABLE").getTable().grabFocus();
			this.getTTable("ITEMTABLE").setSelectedRow(row);
			this.getTTable("ITEMTABLE").setSelectedColumn(3);
		} else {
			TParm igD = new TParm();
			igD.setData("PACK_CODE", this.igOrderData.getItemData(this.rowOnly, "PACK_CODE"));
			igD.setData("DEPT_OR_DR", this.igOrderData.getItemData(this.rowOnly, "DEPT_OR_DR"));
			igD.setData("DEPTORDR_CODE", this.igOrderData.getItemData(this.rowOnly, "DEPTORDR_CODE"));
			filter(this.getTTable("ITEMTABLE"), igOrderData, igD);
			this.getTTable("ITEMTABLE").getTable().grabFocus();
			String columnName = this.getFactColumnName("ITEMTABLE", columnIG);
			this.getTTable("ITEMTABLE").setSelectedRow(rowIG);
			this.getTTable("ITEMTABLE").setSelectedColumn(querySetIGFocusColumn(columnName));
			igOrderData.showDebug();
		}
	}

	/**
	 * �������λ��
	 * 
	 * @return int
	 */
	public int querySetIGFocusColumn(String columnName) {
		int row = -1;
		String columnArray[] = columnName.split("_");
		if (columnArray[columnArray.length - 1].equals("1")) {
			row = 1;
		}
		if (columnArray[columnArray.length - 1].equals("2")) {
			row = 4;
		}
		if (columnArray[columnArray.length - 1].equals("3")) {
			row = 7;
		}
		if (columnArray[columnArray.length - 1].equals("4")) {
			row = 10;
		}
		return row;
	}

	/**
	 * �鿴�Ƿ���δ�༭ҽ��
	 */
	public boolean isNew(TDataStore data) {
		boolean falg = false;
		int rowCount = data.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!data.isActive(i)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * �Ƿ�����ͬ
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public boolean isSame(TDataStore data, String orderCode, int row) {
		boolean falg = false;
		int rowCount = data.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!data.isActive(i))
				continue;
			if (i == row)
				continue;
			if (data.getItemString(i, "ORDER_CODE").equals(orderCode)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * ����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		this.getTTable("ITEMTABLE").acceptText();
		this.getTTable("ITEMTABLE").clearSelection();
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		ODIPACKORDERDataStore packOrder = new ODIPACKORDERDataStore();
		switch (tab.getSelectedIndex()) {
		case 0:
			TParm tempST = this.getTTable("TABLE1").getDataStore().getRowParm(row);
			String packCodeSt = tempST.getValue("PACK_CODE");
			String deptOrDrSt = tempST.getValue("DEPT_OR_DR");
			String deptOrDrCodeSt = tempST.getValue("DEPTORDR_CODE");
			if (packCodeOnly != null && packCodeOnly.equals(packCodeSt)) {
				return;
			}
			// �ж��Ƿ����޸�
			if (packCodeOnly != null && isChangeItem()) {
				this.messageBox("�뱣�棡");
				return;
			}
			packCodeOnly = packCodeSt;
			packOrder.setPackCode(packCodeSt);
			packOrder.onQuery();
			int rowidSt = packOrder.insertRow();
			packOrder.setItem(rowidSt, "PACK_CODE", packCodeSt);
			packOrder.setItem(rowidSt, "DEPT_OR_DR", deptOrDrSt);
			packOrder.setItem(rowidSt, "DEPTORDR_CODE", deptOrDrCodeSt);
			packOrder.setActive(rowidSt, false);
			this.getTTable("ITEMTABLE").setDataStore(packOrder);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		case 1:
			TParm tempUD = this.getTTable("TABLE2").getDataStore().getRowParm(row);
			String packCodeUd = tempUD.getValue("PACK_CODE");
			String deptOrDrUd = tempUD.getValue("DEPT_OR_DR");
			String deptOrDrCodeUd = tempUD.getValue("DEPTORDR_CODE");
			if (packCodeOnly != null && packCodeOnly.equals(packCodeUd)) {
				return;
			}
			// �ж��Ƿ����޸�
			if (packCodeOnly != null && isChangeItem()) {
				this.messageBox("�뱣�棡");
				return;
			}
			packCodeOnly = packCodeUd;
			packOrder.setPackCode(packCodeUd);
			packOrder.onQuery();
			int rowidUd = packOrder.insertRow();
			packOrder.setItem(rowidUd, "PACK_CODE", packCodeUd);
			packOrder.setItem(rowidUd, "DEPT_OR_DR", deptOrDrUd);
			packOrder.setItem(rowidUd, "DEPTORDR_CODE", deptOrDrCodeUd);
			packOrder.setActive(rowidUd, false);
			this.getTTable("ITEMTABLE").setDataStore(packOrder);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		case 2:
			TParm tempDS = this.getTTable("TABLE3").getDataStore().getRowParm(row);
			String packCodeDs = tempDS.getValue("PACK_CODE");
			String deptOrDrDs = tempDS.getValue("DEPT_OR_DR");
			String deptOrDrCodeDs = tempDS.getValue("DEPTORDR_CODE");
			if (packCodeOnly != null && packCodeOnly.equals(packCodeDs)) {
				return;
			}
			// �ж��Ƿ����޸�
			if (packCodeOnly != null && isChangeItem()) {
				this.messageBox("�뱣�棡");
				return;
			}
			packCodeOnly = packCodeDs;
			packOrder.setPackCode(packCodeDs);
			packOrder.onQuery();
			int rowidDs = packOrder.insertRow();
			packOrder.setItem(rowidDs, "PACK_CODE", packCodeDs);
			packOrder.setItem(rowidDs, "DEPT_OR_DR", deptOrDrDs);
			packOrder.setItem(rowidDs, "DEPTORDR_CODE", deptOrDrCodeDs);
			packOrder.setActive(rowidDs, false);
			this.getTTable("ITEMTABLE").setDataStore(packOrder);
			this.getTTable("ITEMTABLE").setDSValue();
			break;
		case 3:
			TParm tempIG = this.getTTable("TABLE4").getDataStore().getRowParm(row);
			String packCodeIg = tempIG.getValue("PACK_CODE");
			String deptOrDrIg = tempIG.getValue("DEPT_OR_DR");
			String deptOrDrCodeIg = tempIG.getValue("DEPTORDR_CODE");
			if (packCodeOnly != null && packCodeOnly.equals(packCodeIg)) {
				return;
			}
			// �ж��Ƿ����޸�
			if (packCodeOnly != null && isChangeItem()) {
				this.messageBox("�뱣�棡");
				return;
			}
			packCodeOnly = packCodeIg;
			igOrderData = new ODIPACKORDERDataStore();
			igOrderData.setPackCode(packCodeIg);
			igOrderData.onQuery();
			TParm igData = new TParm();
			igData.setData("PACK_CODE", packCodeIg);
			igData.setData("DEPT_OR_DR", deptOrDrIg);
			igData.setData("DEPTORDR_CODE", deptOrDrCodeIg);
			filter(this.getTTable("ITEMTABLE"), igOrderData, igData);
			// int rowidIg = packOrder.insertRow();
			// packOrder.setItem(rowidIg,"PACK_CODE",packCodeIg);
			// packOrder.setItem(rowidIg,"DEPT_OR_DR",deptOrDrIg);
			// packOrder.setItem(rowidIg,"DEPTORDR_CODE",deptOrDrCodeIg);
			// packOrder.setActive(rowidIg,false);
			// this.getTTable("ITEMTABLE").setDataStore(packOrder);
			// this.getTTable("ITEMTABLE").setDSValue();
			igOrderData.showDebug();
			break;
		}
	}

	/**
	 * ����
	 * 
	 * @return boolean
	 */
	public boolean filter(TTable table, TDataStore ds, TParm data) {
		boolean falg = true;
		if (!ds.filter())
			falg = false;
		int rowPrimaryCount = ds.rowCount();
		// System.out.println("��������"+rowPrimaryCount);
		// ��ɾ��δʹ����
		for (int i = rowPrimaryCount - 1; i >= 0; i--) {
			if (!ds.isActive(i)) {
				// System.out.println("i====="+i);
				ds.deleteRow(i);
			}
		}
		TParm parm = ds.getBuffer(ds.PRIMARY);
		// System.out.println("����������:"+parm);
		// table.setParmValue(this.getIGTableParm(parm));
		TParm action = this.getIGTableParm(parm, ds, data);
		// System.out.println("TABLE��:"+action);
		table.setParmValue(action);
		return falg;
	}

	/**
	 * �õ�סԺ��ҽҽ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getIGTableParm(TParm parm, TDataStore ds, TParm data) {
		TParm result = new TParm();
		// System.out.println("�õ�סԺ��ҽҽ��"+parm);
		int rowCount = parm.getCount() == -1 ? 0 : parm.getCount();
		// System.out.println("����:"+rowCount);
		// ORDER_DESC_1;MEDI_QTY_1;DCTAGENT_CODE_1;ORDER_DESC_2;MEDI_QTY_2;DCTAGENT_CODE_2;ORDER_DESC_3;MEDI_QTY_3;DCTAGENT_CODE_3;ORDER_DESC_4;MEDI_QTY_4;DCTAGENT_CODE_4
		int row = rowCount % 4 == 0 ? rowCount / 4 : Math.abs((rowCount + 4) / 4);
		// System.out.println("TABLE����:"+row);
		int k = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 1; j < 5; j++) {
				if (k < rowCount) {
					result.addData("ORDER_DESC_" + j, parm.getData("ORDER_DESC", k));
					result.addData("MEDI_QTY_" + j, parm.getData("MEDI_QTY", k));
					result.addData("DCTEXCEP_CODE_" + j, parm.getData("DCTEXCEP_CODE", k));
					result.addData("ROW_ID_" + j, parm.getData("#ID#", k));
				} else {
					int insertRow = ds.insertRow();
					ds.setItem(insertRow, "PACK_CODE", data.getData("PACK_CODE"));
					ds.setItem(insertRow, "DEPT_OR_DR", data.getData("DEPT_OR_DR"));
					ds.setItem(insertRow, "DEPTORDR_CODE", data.getData("DEPTORDR_CODE"));
					ds.setActive(insertRow, false);
					result.addData("ORDER_DESC_" + j, "");
					result.addData("MEDI_QTY_" + j, 0);
					result.addData("DCTEXCEP_CODE_" + j, "");
					result.addData("ROW_ID_" + j, ds.getItemData(insertRow, "#ID#"));
				}
				k++;
			}
		}
		// ��������
		int addRowCount = rowCount % 4;
		// �ж���Ҫ��ӵ�����
		if (addRowCount == 0) {
			for (int i = 1; i < 5; i++) {
				int insertRow = ds.insertRow();
				ds.setItem(insertRow, "PACK_CODE", data.getData("PACK_CODE"));
				ds.setItem(insertRow, "DEPT_OR_DR", data.getData("DEPT_OR_DR"));
				ds.setItem(insertRow, "DEPTORDR_CODE", data.getData("DEPTORDR_CODE"));
				ds.setActive(insertRow, false);
				result.addData("ORDER_DESC_" + i, "");
				result.addData("MEDI_QTY_" + i, 0);
				result.addData("DCTEXCEP_CODE_" + i, "");
				result.addData("ROW_ID_" + i, ds.getItemData(insertRow, "#ID#"));
			}
			row += 1;
		}
		result.setData("ACTION", "COUNT", row);
		return result;
	}

	/**
	 * �ж��Ƿ����޸� Y��Nû��
	 * 
	 * @return boolean
	 */
	public boolean isChangeItem() {
		boolean falg = false;
		if (this.getTTable("ITEMTABLE").getDataStore() == null && this.igOrderData == null) {
			return falg;
		}
		TDataStore order = this.getTTable("ITEMTABLE").getDataStore();
		order.showDebug();
		if (order.getNewRows().length > 0 || order.getModifiedRows().length > 0 || order.getDeleteCount() > 0)
			falg = true;
		if (this.igOrderData == null)
			return falg;
		if (this.igOrderData.getNewRows().length > 0 || this.igOrderData.getModifiedRows().length > 0
				|| this.igOrderData.getDeleteCount() > 0)
			return falg;
		return falg;
	}

	/**
	 * ORDERTABLEֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValue(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			/*
			 * modified by wangqing 20180912 5976
			 * ����ҽ���ײ�ά��ʱ����ʱҽ������Ӽ�����ҽ��ʱ������Ĭ��Ϊ1�����޸�����ʱҲ��������ҽ���������޸���������ʾ������ҽ��վ��ʱҽ�����������ʾ��
			 * ע�⣺Ӧֻ���Ƽ�����ҽ����Ŀ���Է�ҽ����Ŀ���շ���Ŀ��Ӧ������
			 */
			if (("LIS".equals(orderP.getValue("CAT1_TYPE")) || "RIS".equals(orderP.getValue("CAT1_TYPE")))
					&& "MEDI_QTY".equals(columnName)) {
				this.messageBox("������ҽ���������޸�����");
				return true;
			}
			if (orderP.getValue("ORDER_CODE").length() == 0) {
				// ���ҽ������
				node.getTable().getDataStore().setItem(selRow, "ORDER_DESC", "");
				node.getTable().setDSValue(selRow);
			}
			if ("FREQ_CODE".equals(columnName)) {
				// �ж�Ƶ���Ƿ��������ʱʹ��
				if (!OrderUtil.getInstance().isSTFreq(node.getValue().toString())) {
					// ������ʱ��ҩƵ�Σ�
					this.messageBox("������ʱ��ҩƵ��");
					return true;
				}
				if (orderP.getValue("LINK_NO").length() != 0) {
					int linkNo = orderP.getInt("LINK_NO");
					int rowCount = node.getTable().getDataStore().rowCount();
					for (int i = 0; i < rowCount; i++) {
						if (!node.getTable().getDataStore().isActive(i))
							continue;
						if (i == selRow)
							continue;
						if (linkNo == node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")) {
							node.getTable().getDataStore().setItem(i, "FREQ_CODE", node.getValue());
						}
					}
					node.getTable().setDSValue();
				}
			}
			if ("ROUTE_CODE".equals(columnName)) {
				if (orderP.getBoolean("LINKMAIN_FLG")) {
					int linkNo = orderP.getInt("LINK_NO");
					int rowCount = node.getTable().getDataStore().rowCount();
					for (int i = 0; i < rowCount; i++) {
						if (!node.getTable().getDataStore().isActive(i))
							continue;
						if (i == selRow)
							continue;
						if (linkNo == node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")) {
							node.getTable().getDataStore().setItem(i, "ROUTE_CODE", node.getValue());
						}
					}
					node.getTable().setDSValue();
				}
			}
			break;
		case 1:
			if (orderP.getValue("ORDER_CODE").length() == 0) {
				// ���ҽ������
				node.getTable().getDataStore().setItem(selRow, "ORDER_DESC", "");
				node.getTable().setDSValue(selRow);
			}
			if ("FREQ_CODE".equals(columnName)) {
				if (orderP.getValue("LINK_NO").length() != 0) {
					int linkNo = orderP.getInt("LINK_NO");
					int rowCount = node.getTable().getDataStore().rowCount();
					for (int i = 0; i < rowCount; i++) {
						if (!node.getTable().getDataStore().isActive(i))
							continue;
						if (i == selRow)
							continue;
						if (linkNo == node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")) {
							node.getTable().getDataStore().setItem(i, "FREQ_CODE", node.getValue());
						}
					}
					node.getTable().setDSValue();
				}
			}
			if ("ROUTE_CODE".equals(columnName)) {
				if (orderP.getBoolean("LINKMAIN_FLG")) {
					int linkNo = orderP.getInt("LINK_NO");
					int rowCount = node.getTable().getDataStore().rowCount();
					for (int i = 0; i < rowCount; i++) {
						if (!node.getTable().getDataStore().isActive(i))
							continue;
						if (i == selRow)
							continue;
						if (linkNo == node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")) {
							node.getTable().getDataStore().setItem(i, "ROUTE_CODE", node.getValue());
						}
					}
					node.getTable().setDSValue();
				}
			}
			break;
		case 2:
			if (orderP.getValue("ORDER_CODE").length() == 0) {
				// ���ҽ������
				node.getTable().getDataStore().setItem(selRow, "ORDER_DESC", "");
				node.getTable().setDSValue(selRow);
			}
			if ("FREQ_CODE".equals(columnName)) {
				if (orderP.getValue("LINK_NO").length() != 0) {
					int linkNo = orderP.getInt("LINK_NO");
					int rowCount = node.getTable().getDataStore().rowCount();
					for (int i = 0; i < rowCount; i++) {
						if (!node.getTable().getDataStore().isActive(i))
							continue;
						if (i == selRow)
							continue;
						if (linkNo == node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")) {
							node.getTable().getDataStore().setItem(i, "FREQ_CODE", node.getValue());
						}
					}
					node.getTable().setDSValue();
				}
			}
			if ("ROUTE_CODE".equals(columnName)) {
				if (orderP.getBoolean("LINKMAIN_FLG")) {
					int linkNo = orderP.getInt("LINK_NO");
					int rowCount = node.getTable().getDataStore().rowCount();
					for (int i = 0; i < rowCount; i++) {
						if (!node.getTable().getDataStore().isActive(i))
							continue;
						if (i == selRow)
							continue;
						if (linkNo == node.getTable().getDataStore().getRowParm(i).getInt("LINK_NO")) {
							node.getTable().getDataStore().setItem(i, "ROUTE_CODE", node.getValue());
						}
					}
					node.getTable().setDSValue();
				}
			}
			break;
		case 3:
			String columnArray[] = columnName.split("_");
			TParm columnParm = node.getTable().getParmValue().getRow(node.getRow());
			// ��ǰ�༭��
			int onlyEditRow = getIGEditRowSetId(columnParm.getInt("ROW_ID_" + columnArray[columnArray.length - 1]));
			// ����
			if (columnName.contains("MEDI_QTY")) {
				this.igOrderData.setItem(onlyEditRow, "MEDI_QTY", node.getValue());
			}
			// ����巨
			if (columnName.contains("DCTEXCEP_CODE")) {
				this.igOrderData.setItem(onlyEditRow, "DCTEXCEP_CODE", node.getValue());
			}
			break;
		}
		return false;
	}

	/**
	 * ��ʱҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValueST(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
		if ("PACK_DESC".equals(columnName)) {
			if (node.getValue() == null || node.getValue().toString().trim().length() == 0) {
				return true;
			} else {
				node.getTable().getDataStore().setActive(selRow, true);
				node.getTable().setDSValue(selRow);
			}
		}
		return false;
	}

	/**
	 * ����ҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValueUD(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
		if ("PACK_DESC".equals(columnName)) {
			if (node.getValue() == null || node.getValue().toString().trim().length() == 0) {
				return true;
			} else {
				node.getTable().getDataStore().setActive(selRow, true);
				node.getTable().setDSValue(selRow);
			}
		}
		return false;
	}

	/**
	 * ��Ժ��ҩҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValueDS(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
		if ("PACK_DESC".equals(columnName)) {
			if (node.getValue() == null || node.getValue().toString().trim().length() == 0) {
				return true;
			} else {
				node.getTable().getDataStore().setActive(selRow, true);
				node.getTable().setDSValue(selRow);
			}
		}
		return false;
	}

	/**
	 * ��ҩҽ���޸��¼�����
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onChangeTableValueIG(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = node.getTable().getDataStore().getRowParm(selRow);
		if ("PACK_DESC".equals(columnName)) {
			if (node.getValue() == null || node.getValue().toString().trim().length() == 0) {
				return true;
			} else {
				node.getTable().getDataStore().setActive(selRow, true);
				node.getTable().setDSValue(selRow);
				node.getTable().getDataStore().showDebug();
			}
		}
		return false;
	}

	/**
	 * ��ʼ��Ȩ��
	 */
	public void onInitPopeDem() {
		// Ȩ�޿ɷ�ѡ�����
		if (!this.getPopedem("deptEnabled")) {
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
		} else {
			this.callFunction("UI|DEPT_CODE|setEnabled", true);
		}
		// Ȩ�޿ɷ�ѡ����
		if (!this.getPopedem("operatorEnabled")) {
			this.callFunction("UI|USER_ID|setEnabled", false);
		} else {
			this.callFunction("UI|USER_ID|setEnabled", true);
		}
	}

	/**
	 * �Ƿ���δ������Ŀ
	 * 
	 * @param tag
	 *            String
	 * @return boolean
	 */
	public boolean isSave(String tag) {
		boolean falg = false;
		if ("ST".equals(tag)) {
			if (this.getTTable("TABLE1").getDataStore() == null) {
				return falg;
			}
			TDataStore da = this.getTTable("TABLE1").getDataStore();
			if (da.getNewRows().length > 0 || da.getModifiedRows().length > 0 || da.getDeleteCount() > 0) {
				falg = true;
			}
		}
		if ("UD".equals(tag)) {
			if (this.getTTable("TABLE2").getDataStore() == null) {
				return falg;
			}
			TDataStore da = this.getTTable("TABLE2").getDataStore();
			if (da.getNewRows().length > 0 || da.getModifiedRows().length > 0 || da.getDeleteCount() > 0) {
				falg = true;
			}
		}
		if ("DS".equals(tag)) {
			if (this.getTTable("TABLE3").getDataStore() == null) {
				return falg;
			}
			TDataStore da = this.getTTable("TABLE3").getDataStore();
			if (da.getNewRows().length > 0 || da.getModifiedRows().length > 0 || da.getDeleteCount() > 0) {
				falg = true;
			}
		}
		if ("IG".equals(tag)) {
			if (this.getTTable("TABLE4").getDataStore() == null) {
				return falg;
			}
			TDataStore da = this.getTTable("TABLE4").getDataStore();
			if (da.getNewRows().length > 0 || da.getModifiedRows().length > 0 || da.getDeleteCount() > 0) {
				falg = true;
			}
		}
		return falg;
	}

	/**
	 * ����
	 */
	public void onNew() {
		if (this.getValueString("DEPT_OR_DR").length() == 0) {
			this.messageBox("����д�ײ����");
			return;
		}
		TComboBox packType = (TComboBox) this.getComponent("DEPT_OR_DR");
		String id = packType.getSelectedID();
		if (this.getValueString("DEPT_CODE").length() == 0 && ("1".equals(id) || "3".equals(id))) {
			this.messageBox("����д���ң�");
			return;
		}
		if (this.getValueString("USER_ID").length() == 0 && ("2".equals(id) || "4".equals(id))) {
			this.messageBox("����д��Ա��");
			return;
		}
		String deptOrDr = "";
		if ("1".equals(id) || "3".equals(id)) {
			deptOrDr = this.getValueString("DEPT_CODE");
		}
		if ("2".equals(id) || "4".equals(id)) {
			deptOrDr = this.getValueString("USER_ID");
		}
		ODIPACKMAINDataStore odiPackMain = new ODIPACKMAINDataStore();
		odiPackMain.setPackCode("");
		odiPackMain.setRxKind("");
		odiPackMain.setDeptOrDr("");
		odiPackMain.setDeptOrDrCode("");
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			this.getTTable("TABLE1").acceptText();
			if (!isActive("ST")) {
				this.messageBox("�����ײ��");
				return;
			}
			// �Ƿ��Ѿ�����
			if (isSave("ST")) {
				this.messageBox("�뱣��");
				return;
			}
			int rowCountSt = this.getTTable("TABLE1").getDataStore().rowCount();
			if (rowCountSt <= 0) {
				odiPackMain.onQuery();
			} else {
				odiPackMain = (ODIPACKMAINDataStore) this.getTTable("TABLE1").getDataStore();
			}
			this.rxKind = "ST";
			TParm parmSt = new TParm();
			parmSt.setData("DEPT_OR_DR", id);
			parmSt.setData("DEPTORDR_CODE", deptOrDr);
			parmSt.setData("RX_KIND", rxKind);
			odiPackMain.onInsertRow(parmSt);
			this.getTTable("TABLE1").setDataStore(odiPackMain);
			this.getTTable("TABLE1").setDSValue();
			break;
		case 1:
			this.getTTable("TABLE2").acceptText();
			if (!isActive("UD")) {
				this.messageBox("�����ײ��");
				return;
			}
			// �Ƿ��Ѿ�����
			if (isSave("UD")) {
				this.messageBox("�뱣��");
				return;
			}
			int rowCountUd = this.getTTable("TABLE2").getDataStore().rowCount();
			if (rowCountUd <= 0) {
				odiPackMain.onQuery();
			} else {
				odiPackMain = (ODIPACKMAINDataStore) this.getTTable("TABLE2").getDataStore();
			}
			this.rxKind = "UD";
			TParm parmUd = new TParm();
			parmUd.setData("DEPT_OR_DR", id);
			parmUd.setData("DEPTORDR_CODE", deptOrDr);
			parmUd.setData("RX_KIND", rxKind);
			odiPackMain.onInsertRow(parmUd);
			this.getTTable("TABLE2").setDataStore(odiPackMain);
			this.getTTable("TABLE2").setDSValue();
			break;
		case 2:
			this.getTTable("TABLE3").acceptText();
			if (!isActive("DS")) {
				this.messageBox("�����ײ��");
				return;
			}
			// �Ƿ��Ѿ�����
			if (isSave("DS")) {
				this.messageBox("�뱣��");
				return;
			}
			int rowCountDs = this.getTTable("TABLE3").getDataStore().rowCount();
			if (rowCountDs <= 0) {
				odiPackMain.onQuery();
			} else {
				odiPackMain = (ODIPACKMAINDataStore) this.getTTable("TABLE3").getDataStore();
			}
			this.rxKind = "DS";
			TParm parmDs = new TParm();
			parmDs.setData("DEPT_OR_DR", id);
			parmDs.setData("DEPTORDR_CODE", deptOrDr);
			parmDs.setData("RX_KIND", rxKind);
			odiPackMain.onInsertRow(parmDs);
			this.getTTable("TABLE3").setDataStore(odiPackMain);
			this.getTTable("TABLE3").setDSValue();
			break;
		case 3:
			this.getTTable("TABLE4").acceptText();
			if (!isActive("IG")) {
				this.messageBox("�����ײ��");
				return;
			}
			// �Ƿ��Ѿ�����
			if (isSave("IG")) {
				this.messageBox("�뱣��");
				return;
			}
			int rowCountIg = this.getTTable("TABLE4").getDataStore().rowCount();
			if (rowCountIg <= 0) {
				odiPackMain.onQuery();
			} else {
				odiPackMain = (ODIPACKMAINDataStore) this.getTTable("TABLE4").getDataStore();
			}
			this.rxKind = "IG";
			TParm parmIG = new TParm();
			parmIG.setData("DEPT_OR_DR", id);
			parmIG.setData("DEPTORDR_CODE", deptOrDr);
			parmIG.setData("RX_KIND", rxKind);
			parmIG.setData("GFREQ_CODE", getGFreqCode());
			parmIG.setData("GROUTE_CODE", getGRouteCode());
			parmIG.setData("GDCTAGENT_CODE", getGDctagentCode());
			odiPackMain.onInsertRow(parmIG);
			this.getTTable("TABLE4").setDataStore(odiPackMain);
			this.getTTable("TABLE4").setDSValue();
			break;
		}
	}

	/**
	 * �ж��Ƿ���û����д���ײ���Ŀ
	 * 
	 * @param rxKind
	 *            String
	 * @return boolean
	 */
	public boolean isActive(String rxKind) {
		boolean falg = true;
		if ("ST".equals(rxKind)) {
			if (this.getTTable("TABLE1").getDataStore().rowCount() <= 0)
				return falg;
			ODIPACKMAINDataStore da = (ODIPACKMAINDataStore) this.getTTable("TABLE1").getDataStore();
			int rowCount = da.rowCount();
			for (int i = 0; i < rowCount; i++) {
				if (da.getItemString(i, "PACK_DESC").length() == 0 || da.getItemString(i, "PACK_DESC") == null) {
					falg = false;
					break;
				}
			}
		}
		if ("UD".equals(rxKind)) {
			if (this.getTTable("TABLE2").getDataStore().rowCount() <= 0)
				return falg;
			ODIPACKMAINDataStore da = (ODIPACKMAINDataStore) this.getTTable("TABLE2").getDataStore();
			int rowCount = da.rowCount();
			for (int i = 0; i < rowCount; i++) {
				if (da.getItemString(i, "PACK_DESC").length() == 0 || da.getItemString(i, "PACK_DESC") == null) {
					falg = false;
					break;
				}
			}
		}
		if ("DS".equals(rxKind)) {
			if (this.getTTable("TABLE3").getDataStore().rowCount() <= 0)
				return falg;
			ODIPACKMAINDataStore da = (ODIPACKMAINDataStore) this.getTTable("TABLE3").getDataStore();
			int rowCount = da.rowCount();
			for (int i = 0; i < rowCount; i++) {
				if (da.getItemString(i, "PACK_DESC").length() == 0 || da.getItemString(i, "PACK_DESC") == null) {
					falg = false;
					break;
				}
			}

		}
		if ("IG".equals(rxKind)) {
			if (this.getTTable("TABLE4").getDataStore().rowCount() <= 0)
				return falg;
			ODIPACKMAINDataStore da = (ODIPACKMAINDataStore) this.getTTable("TABLE4").getDataStore();
			int rowCount = da.rowCount();
			for (int i = 0; i < rowCount; i++) {
				if (da.getItemString(i, "PACK_DESC").length() == 0 || da.getItemString(i, "PACK_DESC") == null) {
					falg = false;
					break;
				}
			}
		}
		return falg;
	}

	/**
	 * ���
	 */
	public void onClear() {
		if (isSave(getTableTag(pageIndex)) || this.isChangeItem()) {
			if (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_OPTION) != 0) {
				packCodeOnly = "";
				this.clearValue("DEPT_OR_DR;PACK_DESC");
				this.setValue("DEPT_CODE", Operator.getDept());
				this.setValue("USER_ID", Operator.getID());
				clearTable();
				TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
				tab.setSelectedIndex(0);
				onInitPopeDem();
				return;
			} else {
				this.onSave();
			}
		}
		packCodeOnly = "";
		this.clearValue("DEPT_OR_DR;PACK_DESC");
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("USER_ID", Operator.getID());
		clearTable();
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		tab.setSelectedIndex(0);
		onInitPopeDem();
	}

	/**
	 * ���TABLE
	 */
	public void clearTable() {
		String[] tableNames = new String[] { "TABLE1", "TABLE2", "TABLE3", "TABLE4" };
		for (String temp : tableNames) {
			ODIPACKMAINDataStore ds = new ODIPACKMAINDataStore();
			ds.setPackCode("");
			ds.setDeptOrDr("");
			ds.setDeptOrDrCode("");
			ds.setRxKind("");
			ds.onQuery();
			this.getTTable(temp).setDataStore(ds);
			this.getTTable(temp).setDSValue();
		}
		ODIPACKORDERDataStore dsItem = new ODIPACKORDERDataStore();
		dsItem.setPackCode(packCodeOnly);
		dsItem.onQuery();
		this.getTTable("ITEMTABLE").setDataStore(dsItem);
		this.getTTable("ITEMTABLE").setDSValue();

	}

	/**
	 * �õ�TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
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

	/**
	 * ɾ��ҽ��
	 */
	public void onDelete() {
		Map tableName = new HashMap();
		tableName.put(0, "TABLE1");
		tableName.put(1, "TABLE2");
		tableName.put(2, "TABLE3");
		tableName.put(3, "TABLE4");
		String tableTag = tableName.get(this.pageIndex).toString();
		// this.messageBox_(this.getTTable(tableTag).getSelectedRow());
		// this.messageBox_(this.getTTable("ITEMTABLE").getSelectedRow());
		int mainSelRow = this.getTTable(tableTag).getSelectedRow();
		if (mainSelRow >= 0) {
			if (!this.getTTable(tableTag).getDataStore().isActive(mainSelRow)) {
				this.messageBox("δʹ�ò���ɾ����");
				return;
			}
			this.getTTable(tableTag).getDataStore().deleteRow(mainSelRow);
			this.getTTable(tableTag).setDSValue();
			// ɾ��ϸ��
			ODIPACKORDERDataStore dsItem = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
			int rowCount = dsItem.rowCount();
			for (int i = rowCount - 1; i >= 0; i--) {
				if (!dsItem.isActive(i))
					continue;
				dsItem.deleteRow(i);
			}
			this.getTTable("ITEMTABLE").setDSValue();
		}
		int orderSelRow = this.getTTable("ITEMTABLE").getSelectedRow();
		if (orderSelRow >= 0) {
			if (!this.getTTable("ITEMTABLE").getDataStore().isActive(orderSelRow)) {
				this.messageBox("δʹ�ò���ɾ����");
				return;
			}
			this.getTTable("ITEMTABLE").getDataStore().deleteRow(orderSelRow);
			this.getTTable("ITEMTABLE").setDSValue();
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (!isSave(getTableTag(pageIndex)) && !this.isChangeItem()) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		this.getTTable("TABLE1").acceptText();
		this.getTTable("TABLE2").acceptText();
		this.getTTable("TABLE3").acceptText();
		this.getTTable("TABLE4").acceptText();
		this.getTTable("ITEMTABLE").acceptText();
		// �õ�����SQL
		String[] sql = getRxKindTableSQL(pageIndex);
		sql = StringUtil.getInstance().copyArray(sql, getOrderTableSQL());
		// ����
		/**
		 * for (String temp : sql) { System.out.println("��ǰSQL:" + temp); }
		 **/
		TParm parm = new TParm();
		parm.setData("ARRAY", sql);
		TParm actionParm = TIOM_AppServer.executeAction(actionName, "saveOrder", parm);
		if (actionParm.getErrCode() < 0)
			this.messageBox("����ʧ�ܣ�");
		else
			this.messageBox("����ɹ���");
		saveQuery();
		// ��յ�ǰ�༭
		this.packCodeOnly = "";
	}

	/**
	 * ������ѯ
	 */
	public void saveQuery() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		Map tableName = new HashMap();
		tableName.put(0, "TABLE1");
		tableName.put(1, "TABLE2");
		tableName.put(2, "TABLE3");
		tableName.put(3, "TABLE4");
		ODIPACKMAINDataStore ds = new ODIPACKMAINDataStore();
		ds.setPackCode(this.packCodeOnly);
		ds.setDeptOrDr("");
		ds.setDeptOrDrCode("");
		ds.setRxKind("");
		ds.onQuery();
		this.getTTable(tableName.get(pageIndex).toString()).setDataStore(ds);
		this.getTTable(tableName.get(pageIndex).toString()).setDSValue();
		if (tab.getSelectedIndex() != 3) {
			ODIPACKORDERDataStore dsItem = new ODIPACKORDERDataStore();
			dsItem.setPackCode(this.packCodeOnly);
			dsItem.onQuery();
			this.getTTable("ITEMTABLE").setDataStore(dsItem);
			this.getTTable("ITEMTABLE").setDSValue();
		} else {
			TParm igD = new TParm();
			igD.setData("PACK_CODE", this.igOrderData.getItemData(this.rowOnly, "PACK_CODE"));
			igD.setData("DEPT_OR_DR", this.igOrderData.getItemData(this.rowOnly, "DEPT_OR_DR"));
			igD.setData("DEPTORDR_CODE", this.igOrderData.getItemData(this.rowOnly, "DEPTORDR_CODE"));
			this.igOrderData.setPackCode(this.packCodeOnly);
			this.igOrderData.onQuery();
			this.filter(this.getTTable("ITEMTABLE"), this.igOrderData, igD);
			this.rowOnly = -1;
		}
	}

	/**
	 * �õ�����������
	 * 
	 * @param type
	 *            int
	 * @return String[]
	 */
	public String[] getRxKindTableSQL(int type) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Map tableName = new HashMap();
		tableName.put(0, "TABLE1");
		tableName.put(1, "TABLE2");
		tableName.put(2, "TABLE3");
		tableName.put(3, "TABLE4");
		ODIPACKMAINDataStore ds = (ODIPACKMAINDataStore) this.getTTable(tableName.get(type).toString()).getDataStore();
		int newRows[] = ds.getNewRows();
		for (int temp : newRows) {
			ds.setItem(temp, "OPT_USER", Operator.getID());
			ds.setItem(temp, "OPT_TERM", Operator.getIP());
			ds.setItem(temp, "OPT_DATE", sysDate);
		}
		return ds.getUpdateSQL();
	}

	/**
	 * �õ�ϸ��SQL
	 * 
	 * @return String[]
	 */
	public String[] getOrderTableSQL() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		TParm parm = new TParm(
				this.getDBTool().select("SELECT NVL(MAX(SEQ_NO)+1,1) AS SEQ_NO FROM ODI_PACK_ORDER WHERE PACK_CODE='"
						+ this.packCodeOnly + "'"));
		int seqNo = parm.getInt("SEQ_NO", 0);
		if (tab.getSelectedIndex() != 3) {
			ODIPACKORDERDataStore ds = (ODIPACKORDERDataStore) this.getTTable("ITEMTABLE").getDataStore();
			int rowId[] = ds.getNewRows();
			for (int temp : rowId) {
				ds.setItem(temp, "SEQ_NO", seqNo);
				ds.setItem(temp, "OPT_DATE", sysDate);
				ds.setItem(temp, "OPT_USER", Operator.getID());
				ds.setItem(temp, "OPT_TERM", Operator.getIP());
				seqNo++;
			}
			return ds.getUpdateSQL();
		} else {
			int rowId[] = this.igOrderData.getNewRows();
			for (int temp : rowId) {
				this.igOrderData.setItem(temp, "SEQ_NO", seqNo);
				this.igOrderData.setItem(temp, "OPT_DATE", sysDate);
				this.igOrderData.setItem(temp, "OPT_USER", Operator.getID());
				this.igOrderData.setItem(temp, "OPT_TERM", Operator.getIP());
				seqNo++;
			}
			return this.igOrderData.getUpdateSQL();
		}
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (this.getValueString("DEPT_OR_DR").length() == 0) {
			this.messageBox("��ѡ���ײ����");
			return;
		}

		Map tableName = new HashMap();
		tableName.put(0, "TABLE1");
		tableName.put(1, "TABLE2");
		tableName.put(2, "TABLE3");
		tableName.put(3, "TABLE4");
		String deptDrCode = getDeptOrCodeString();
		String rxKind = getRxKindString();
		ODIPACKMAINDataStore ds = new ODIPACKMAINDataStore();
		ds.setPackCode(this.packCodeOnly);
		ds.setDeptOrDr(this.getValueString("DEPT_OR_DR"));
		ds.setDeptOrDrCode(deptDrCode);
		ds.setRxKind(rxKind);
		ds.setRetrieveType("Q");
		ds.onQuery();
		this.getTTable(tableName.get(this.pageIndex).toString()).setDataStore(ds);
		this.getTTable(tableName.get(this.pageIndex).toString()).setDSValue();
	}

	/**
	 * �õ�ҽ�����
	 * 
	 * @return String
	 */
	public String getRxKindString() {
		String rxKind = "ST";
		TTabbedPane tab = (TTabbedPane) this.getComponent("TTABBEDPANE");
		switch (tab.getSelectedIndex()) {
		case 0:
			rxKind = "ST";
			break;
		case 1:
			rxKind = "UD";
			break;
		case 2:
			rxKind = "DS";
			break;
		case 3:
			rxKind = "IG";
			break;
		}
		return rxKind;
	}

	/**
	 * �õ�������Ա
	 * 
	 * @return String
	 */
	public String getDeptOrCodeString() {
		TComboBox packType = (TComboBox) this.getComponent("DEPT_OR_DR");
		String id = packType.getSelectedID();
		String deptOrDr = "";
		if ("1".equals(id) || "3".equals(id)) {
			deptOrDr = this.getValueString("DEPT_CODE");
		}
		if ("2".equals(id) || "4".equals(id)) {
			// =========pangben modify 20110518 start �������������setText
			deptOrDr = this.getValueString("USER_ID");// modify by wanglong 20130123
		}
		return deptOrDr;
	}

	public String getDctTakeDays() {
		return dctTakeDays;
	}

	public String getDctTakeQty() {
		return dctTakeQty;
	}

	public String getOdiDefaFreg() {
		return odiDefaFreg;
	}

	public String getOdiStatCode() {
		return odiStatCode;
	}

	public String getOdiUddStatCode() {
		return odiUddStatCode;
	}

	public void setDctTakeDays(String dctTakeDays) {
		this.dctTakeDays = dctTakeDays;
	}

	public void setDctTakeQty(String dctTakeQty) {
		this.dctTakeQty = dctTakeQty;
	}

	public void setOdiDefaFreg(String odiDefaFreg) {
		this.odiDefaFreg = odiDefaFreg;
	}

	public void setOdiStatCode(String odiStatCode) {
		this.odiStatCode = odiStatCode;
	}

	public void setOdiUddStatCode(String odiUddStatCode) {
		this.odiUddStatCode = odiUddStatCode;
	}

	public String getGFreqCode() {
		return this.gFreqCode;
	}

	public void setGFreqCode(String gFreqCode) {
		this.gFreqCode = gFreqCode;
	}

	public String getGDctagentCode() {
		return this.gDctagentCode;
	}

	public void setGDctagentCode(String gDctagentCode) {
		this.gDctagentCode = gDctagentCode;
	}

	public String getGRouteCode() {
		return this.gRouteCode;
	}

	public void setGRouteCode(String gRouteCode) {
		this.gRouteCode = gRouteCode;
	}

}
