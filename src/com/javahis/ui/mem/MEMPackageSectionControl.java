package com.javahis.ui.mem;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import jdo.bil.BILLumpWorkTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;

import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.opb.Objects;

/**
 *
 * <p>
 * Title: �ײ�ʱ������
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
 * Company: bluecore
 * </p>
 *
 * @author duzhw 20131224
 * @version 1.0
 */
public class MEMPackageSectionControl extends TControl {
	// ����ϸTABLE
	private TTable table, orderTable;

	String nodeId = "";// ���ڵ�-����ˢ��ҳ��

	int orderSeqNo = 0;// ҽ�����
	// int orderIdNo = 0;//ҽ��ID
	int sectionSeqNo = 0;// ʱ�����
	int sectionIdNo = 0;// ʱ��ID ��sectionCodeһ��
	int ordersetGroupNo = 0;// ���
	String sectionCode = "";// ʱ�̱��
	String packageCode = "";// �ײͱ��
	String sectionDesc = "";// ʱ������

	TParm orderDelParm = new TParm();// ҽ����ɾ��TParm
	// TParm operSectionData = new TParm();//��ǰ����ʱ������--��ʶ
	TParm sectionDelParm = new TParm();// ʱ�̱�ɾ��TParm

	private boolean isRateChanged = false;
	private TNumberTextField allFee;
	private TTableNode tNode;
	private boolean synButtonflag = true;

	private double rateOld = 1.0;
	private double rateOldOrder = 1.0;
	private double reatialPrice = 1.0;
	boolean flag = false;
	/**
	 * ����
	 */
	private TTreeNode treeRoot;

	/**
	 * ��Ź�����𹤾�
	 */
	SYSRuleTool ruleTool;

	/**
	 * �������ݷ���datastore���ڶ��������ݹ���
	 */
	TDataStore treeDataStore = new TDataStore();

	private boolean idOrderChanged = false;

	/**
	 * ��ʼ��
	 */
	public void onInit() { // ��ʼ������
		super.onInit();
		table = getTable("TABLE");
		orderTable = getTable("ORDER_TABLE");
		// ��ʼ����
		onInitSelectTree();
		// ��tree��Ӽ����¼�
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");

		orderTable.addEventListener("ORDER_TABLE->" + TTableEvent.CHANGE_VALUE, this, "onOrderTableChangeValue");
		// ����
		onCreatTree();
		callFunction("UI|new|setEnabled", false);
		onPageInit();

		initComponent();
		initCombo();
		this.setValue("PRICE_TYPE", "01");

		allFee = (TNumberTextField) this.getComponent("ALLFEE");
		allFee.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				getRate();
			}
		});

	}

	/**
	 * ������
	 *
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) { // ������ť������
		callFunction("UI|new|setEnabled", false);
		// �õ�������Ľڵ����
		TTreeNode node = (TTreeNode) parm;
		if (node == null)
			return;
		// �õ�table����
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		// table�������иı�ֵ
		table.acceptText();
		// �������������ĸ����
		if (node.getType().equals("Root")) {
			// ���ݹ�����������Tablet�ϵ�����
			String id = node.getID();
			nodeId = id;
			table.setFilter(getQuerySrc());
		} else { // �����Ĳ��Ǹ����

			// �õ���ǰѡ�еĽڵ��idֵ
			String id = node.getID();
			nodeId = id;
			packageCode = id;
			if (isLeaf(id)) {
				getPackageMsg(id);
				showSectionDetail(id);// ʱ����ϸ
				setTotOriginalPrice();
				selectCheckBox();
				selectSaleCheckBox();
			} else {
				onClear();
			}
		}

	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		// �ײ�ϸ������ҽ���¼�
		orderTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onDetailCreateEditComponent");
	}

	private void initCombo() {
		String sql = " SELECT ID, CHN_DESC NAME" + " FROM SYS_DICTIONARY" + " WHERE GROUP_ID = 'MEM_PACKAGE_TYPE'"
				// 
				+ " AND ACTIVE_FLG = 'Y' ORDER BY SEQ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		TTextFormat memCombo = (TTextFormat) getComponent("PRICE_TYPE");

		memCombo.setHorizontalAlignment(2);
		memCombo.setPopupMenuHeader("����,100;����,100");
		memCombo.setPopupMenuWidth(300);
		memCombo.setPopupMenuHeight(300);
		memCombo.setFormatType("combo");
		memCombo.setShowColumnList("NAME");
		memCombo.setValueColumn("ID");
		memCombo.setPopupMenuData(parm);

	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onDetailCreateEditComponent(Component com, int row, int column) {
		// �����ǰ�кţ�ֻ������ҽ������������һ��ҽ��
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		column = orderTable.getColumnModel().getColumnIndex(column);
		String columnName = orderTable.getParmMap(column);
		if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
	}

	/**
	 * ����ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOrderReturn(String tag, Object obj) {
		orderTable.acceptText();
		TParm orderparm = orderTable.getParmValue();
		TParm parm = (TParm) obj;
		// �ж��Ƿ���ҩƷ(PHA_WҩƷ)
		int row = orderTable.getSelectedRow();
		String order_code = parm.getValue("ORDER_CODE");
		String orderSetFlg = parm.getValue("ORDERSET_FLG");// add by sunqy 20140722 �Ƿ��Ǽ���ҽ��
		if ("Y".equals(orderSetFlg)) {// ��ѡ���ҽ�����ص��Ǽ���ҽ�������۶��������� add by sunqy 20140807
			orderTable.setLockCell(row, orderTable.getColumnIndex("RETAIL_PRICE"), true);// modify by lich
																							// 20141106//��6��ΪorderTable.getColumnIndex("RETAIL_PRICE")
																							// modify by huangjw
																							// 20150508
		}

		// �ж��Ƿ����ظ�����
		for (int i = 0; i < orderTable.getDataStore().rowCount(); i++) {
			if (i == orderTable.getSelectedRow()) {
				continue;
			}
			if (order_code.equals(orderTable.getDataStore().getItemData(i, "ORDER_CODE"))
					&& orderTable.getDataStore().getItemData(i, "SETMAIN_FLG").equals("Y")) {
				return;
			}
		}
		orderTable.acceptText();
		// orderTable.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		orderTable.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		orderTable.setItem(row, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
		orderTable.setItem(row, "ORDER_NUM", "1");// ������Ĭ��1
		orderTable.setItem(row, "UNIT_CODE", parm.getValue("UNIT_CODE"));
		orderparm.setData("ORDER_CODE", orderTable.getSelectedRow(), parm.getValue("ORDER_CODE"));
		// �ж��Ƿ��Ǽ���ҽ��
		if (checkIsDetailOrder(parm)) {// �Ǽ���ҽ��
			// ����ϸ���ܼ�
			String sumPrice = onSumDetailPrice(parm);
			orderTable.setItem(row, "UNIT_PRICE", sumPrice);// ԭʼ�۸�--ϸ����ܼ۸�֮��
			orderTable.setItem(row, "RETAIL_PRICE", sumPrice);// ���ۼ۸�-������Ϊԭʼ�۸�
		} else {
			orderTable.setItem(row, "UNIT_PRICE", parm.getValue("OWN_PRICE"));// ԭʼ�۸�
			orderTable.setItem(row, "RETAIL_PRICE", parm.getValue("OWN_PRICE"));// ���ۼ۸�-������Ϊԭʼ�۸�
		}

		orderTable.getTable().grabFocus(); // ��ȡ���㣬���Իس��������
		// ���һ��������
		if (orderTable.getSelectedRow() == orderTable.getRowCount() - 1) {
			String orderIdNo = SystemTool.getInstance().getNo("ALL", "MEM", "PACKAGESECTIOND", "PACKAGESECTIOND");
			orderparm.setData("ID", orderTable.getSelectedRow(), String.valueOf(orderIdNo));// ��������ID���
			if (orderTable.getItemString(orderTable.getSelectedRow(), "SECTION_DESC").length() == 0) {// �����ǰ��ʱ��������Ϊ�գ���ֵ
				orderparm.setData("SECTION_DESC", orderTable.getSelectedRow(), sectionDesc);
			}
			orderTable.setParmValue(orderparm);
			insertRow("ORDER_TABLE");
			orderTable.setLockRows((row + 1) + ""); // add by huangtt 20160808
		} else {
			orderTable.setParmValue(orderparm);
		}
		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			TParm parmBase = PhaBaseTool.getInstance().selectByOrderRoute(parm.getValue("ORDER_CODE"));
			orderTable.setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT", 0));
			orderTable.setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE", 0));
			orderTable.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
		}
		parm = null;
		try {
			setTotDetailRetailPrice(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTotDetailOriginalPrice();
		selectSaleCheckBox();
		setTotOriginalPrice();
		selectSaleCheckBox();

	}

	/**
	 * ��ʼ����
	 */
	public void onInitSelectTree() {
		// �õ�����
		treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
		if (treeRoot == null)
			return;
		// �����ڵ����������ʾ
		treeRoot.setText("�ײ͹���");
		// �����ڵ㸳tag
		treeRoot.setType("Root");
		// ���ø��ڵ��id
		treeRoot.setID("");
		// ������нڵ������
		treeRoot.removeAllChildren();
		// ���������ʼ������
		callMessage("UI|TREE|update");
	}

	/**
	 * ��ʼ�����ϵĽڵ�
	 */
	public void onCreatTree() {
		// ��dataStore��ֵ
		treeDataStore.setSQL("SELECT PACKAGE_CODE, PARENT_PACKAGE_CODE,PACKAGE_DESC,"
				+ " PACKAGE_ENG_DESC, PY1, PY2, SEQ,DESCRIPTION,"
				+ "  ORIGINAL_PRICE, PACKAGE_PRICE, OPT_DATE, OPT_USER," + "  OPT_TERM  FROM MEM_PACKAGE" + "  ");

		// �����dataStore���õ�������С��0
		if (treeDataStore.retrieve() <= 0)
			return;
		// ��������,�Ǳ�������еĿ�������
		ruleTool = new SYSRuleTool("SYS_MEMPACKAGE");
		if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
			TTreeNode node[] = ruleTool.getTreeNode(treeDataStore, "PACKAGE_CODE", "PACKAGE_DESC", "Path", "SEQ");
			// ѭ����������ڵ�
			for (int i = 0; i < node.length; i++) {
				treeRoot.addSeq(node[i]);
			}

		}
		// �õ������ϵ�������
		TTree tree = (TTree) callMessage("UI|TREE|getThis");
		// ������
		tree.update();
		// ��������Ĭ��ѡ�нڵ�
		tree.setSelectNode(treeRoot);
	}

	/**
	 * �õ�������
	 *
	 * @return TTree
	 */
	public TTree getTree() {
		return (TTree) callFunction("UI|TREE|getThis");
	}

	/**
	 * ��ʼ������
	 */
	public void onPageInit() {
		String s = "";
		if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
			s = " REGION_CODE='" + Operator.getRegion() + "' ";
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		table.setFilter(s);
		// ��table���ݼ���������
		table.setSort("DEPT_CODE");
		// table��������¸�ֵ
		table.sort();
		// ִ��table�Ĺ���
		table.filter();
	}

	/**
	 * ���淽��
	 * 
	 * @param flg
	 *            true ����ҽ��ϸ��
	 */
	public void onSaveFlg(boolean flg) {
		// ʱ���� ��ϸ�ϼ�

		stopAllTableEditing();
		try {

			double allFee = this.getValueDouble("AllFee");
			double ownPrice = this.getValueDouble("OWN_PRICE");
			if (allFee > ownPrice) {
				messageBox("�ײͼ۸��ܴ���ԭ�۸�");
				return;
			}

			TParm update = new TParm();
			TParm result = new TParm();

			TTable table = (TTable) callFunction("UI|TABLE|getThis");
			TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
			int selectedRow = table.getSelectedRow();

			// modify bu huangjw 20160113 start
			if (!flag) {
				boolean updateFlg1 = false;
				boolean updateFlg2 = false;
				for (int i = 0; i < table.getParmValue().getCount("SEQ") - 1; i++) {
					if (table.getParmValue().getValue("EXEC", i).equals("Y")) {
						if (table.getParmValue().getValue("FLG", i).equals("Y")) {
							updateFlg1 = true;
							break;
						}
					} else {
						updateFlg1 = true;
						break;
					}
				}

				if (selectedRow >= 0) {//
					for (int i = 0; i < orderTable.getParmValue().getCount("SELECTD") - 1; i++) {
						if (orderTable.getParmValue().getValue("SELECTD", i).equals("Y")) {
							if (orderTable.getParmValue().getValue("ACTIVE_FLG", i).equals("Y")) {
								messageBox("����ͣ�õ�ҽ��,���ܱ���");
								return;
							}
						}
					}

				}

				if (selectedRow >= 0) {//
					for (int i = 0; i < orderTable.getParmValue().getCount("SELECTD") - 1; i++) {
						if (orderTable.getParmValue().getValue("EXEC", i).equals("Y")) {
							if (orderTable.getParmValue().getValue("SELECTD", i).equals("Y")) {
								updateFlg2 = true;
								break;
							}
						} else {
							updateFlg2 = true;
							break;
						}
					}
					if (!updateFlg1 && !updateFlg2) {
						this.messageBox("�빴ѡʱ������");
						String columnName = table.getDataStoreColumnName(tNode.getColumn());
						if (columnName.equals("DISCOUNT_RATE")) {
							table.setItem(tNode.getRow(), tNode.getColumn(), tNode.getValue());
						}
						return;
					}
				} else {
					if (!updateFlg1) {
						this.messageBox("�빴ѡʱ������");
						String columnName = table.getDataStoreColumnName(tNode.getColumn());
						if (columnName.equals("DISCOUNT_RATE")) {
							table.setItem(tNode.getRow(), tNode.getColumn(), tNode.getValue());
						}
						return;
					}
				}
			}
			// modify bu huangjw 20160113 end

			table.acceptText();
			orderTable.acceptText();
			TParm orderParm = orderTable.getParmValue();
			TParm tableParm = table.getParmValue();
			//
			setTotDetailRetailPrice(1, 0);
			setTotDetailOriginalPrice();
			//
			setTotOriginalPrice();// 20141107
			selectCheckBox();// add by lich 20141107
			selectSaleCheckBox();// add by lich 20141107
			TParm insertOrderParm = new TParm();
			TParm insertOrderData = new TParm();
			TParm updateOrderParm = new TParm();
			TParm updateOrderData = new TParm();

			if (orderParm.getCount() > 0) {
				Timestamp date = StringTool.getTimestamp(new Date());
				for (int i = 0; i < orderParm.getCount(); i++) {
					String exec = orderParm.getValue("EXEC", i);
					if ("Y".equals(exec)) {// ������
						if ("Y".equals(orderParm.getValue("SELECTD", i))) {
							/* add by lich 20141010 ����Ӣ������ TRADE_ENG_DESC start */
							updateOrderParm.addData("TRADE_ENG_DESC", orderParm.getValue("TRADE_ENG_DESC", i));
							/* add by lich 20141010 ����Ӣ������ TRADE_ENG_DESC end */
							updateOrderParm.addData("ID", orderParm.getValue("ID", i));
							updateOrderParm.addData("SEQ", orderParm.getValue("SEQ", i));
							updateOrderParm.addData("SECTION_DESC", orderTable.getItemString(i, "SECTION_DESC"));
							updateOrderParm.addData("ORDER_CODE", orderParm.getValue("ORDER_CODE", i));
							updateOrderParm.addData("ORDER_DESC", orderTable.getItemString(i, "ORDER_DESC"));
							updateOrderParm.addData("ORDER_NUM", orderTable.getItemString(i, "ORDER_NUM"));

							// ��� ������λ���÷���Ƶ�� add by huangjw 20150516
							updateOrderParm.addData("MEDI_UNIT", orderTable.getItemString(i, "MEDI_UNIT"));
							updateOrderParm.addData("ROUTE_CODE", orderTable.getItemString(i, "ROUTE_CODE"));
							updateOrderParm.addData("FREQ_CODE", orderTable.getItemString(i, "FREQ_CODE"));

							updateOrderParm.addData("UNIT_CODE", orderTable.getItemString(i, "UNIT_CODE"));
							updateOrderParm.addData("UNIT_PRICE", orderTable.getItemString(i, "UNIT_PRICE"));
							updateOrderParm.addData("DESCRIPTION", orderTable.getItemString(i, "DESCRIPTION"));
							updateOrderParm.addData("OPT_DATE", date);
							updateOrderParm.addData("OPT_USER", Operator.getID());
							updateOrderParm.addData("OPT_TERM", Operator.getIP());
							updateOrderParm.addData("SECTION_CODE", orderParm.getValue("SECTION_CODE", i));
							updateOrderParm.addData("PACKAGE_CODE", orderParm.getValue("PACKAGE_CODE", i));

							updateOrderParm.addData("SETMAIN_FLG", orderParm.getValue("SETMAIN_FLG", i));
							updateOrderParm.addData("ORDERSET_GROUP_NO", orderParm.getValue("ORDERSET_GROUP_NO", i));
							updateOrderParm.addData("RETAIL_PRICE", orderTable.getItemString(i, "RETAIL_PRICE"));
							updateOrderParm.addData("ORDERSET_CODE", orderParm.getValue("ORDERSET_CODE", i));

							if ("".equals(orderTable.getItemString(i, "DISCOUNT_RATE"))) {
								updateOrderParm.addData("DISCOUNT_RATE", "1.0");// add by lich 20141021 �����ۿ���
							} else {
								updateOrderParm.addData("DISCOUNT_RATE", orderTable.getItemString(i, "DISCOUNT_RATE"));// add
																														// by
																														// lich
																														// 20141021
																														// �����ۿ���
							}

							// add by lich 20141030 �����ײͼ۸����ͱ��ã����ִ�в��������ʱ��PRICE_TYPEΪ�գ�ʹ��PRICE_TYPE_BY���д�ֵ
							updateOrderParm.addData("PRICE_TYPE_BY", this.getValueString("PRICE_TYPE"));
							updateOrderParm.addData("PRICE_TYPE", orderParm.getValue("PRICE_TYPE", i));// add by lich
																										// 20141021
																										// �����ײͼ۸�����
							updateOrderParm.addData("UN_NUM_FLG", orderParm.getValue("UN_NUM_FLG", i));// ===pangben
																										// 2015-9-2
																										// ��Ӳ�����
							// trueʱ�Ÿ��¼���ҽ��
							if (flg) {
								// ϸ��ϼƽ��
								double totalDetialRetailPrice = 0.0;

								// �Ǽ���ҽ���������
								if ("Y".equals(orderParm.getValue("SETMAIN_FLG", i))) {
									int mainItemIndex = updateOrderParm.getCount("SEQ") - 1;
									String ssql = " SELECT A.UNIT_PRICE, A.ID, A.SEQ, A.SECTION_DESC, A.ORDER_CODE, A.ORDER_DESC, A.ORDER_NUM,"
											+ " A.UNIT_CODE, B.RETAIL_PRICE, A.DESCRIPTION, A.SECTION_CODE,"
											+ " A.PACKAGE_CODE, B.DISCOUNT_RATE, B.PRICE_TYPE, B.ORIGINAL_PRICE"
											+ " FROM MEM_PACKAGE_SECTION_D A,"
											+ " (SELECT ID, PRICE_TYPE, PACKAGE_CODE, SECTION_CODE, RETAIL_PRICE,"
											+ " DISCOUNT_RATE, ORIGINAL_PRICE" + " FROM MEM_PACKAGE_SECTION_D_PRICE"
											+ " WHERE PRICE_TYPE = '" + this.getValueString("PRICE_TYPE") + "') B"
											+ " WHERE A.SECTION_CODE = '" + orderParm.getValue("SECTION_CODE", i) + "'"
											+ " AND A.PACKAGE_CODE = '" + orderParm.getValue("PACKAGE_CODE", i) + "'"
											+ " AND A.ORDERSET_GROUP_NO = '"
											+ orderParm.getValue("ORDERSET_GROUP_NO", i) + "'"
											+ " AND A.SETMAIN_FLG = 'N'" + " AND A.PACKAGE_CODE = B.PACKAGE_CODE(+)"
											+ " AND A.SECTION_CODE = B.SECTION_CODE(+)" + " AND A.ID = B.ID(+)";

									TParm sparm = new TParm(TJDODBTool.getInstance().select(ssql));

									for (int j = 0; j < sparm.getCount(); j++) {
										updateOrderParm.addData("TRADE_ENG_DESC", sparm.getValue("TRADE_ENG_DESC", j));
										/*
										 * add by lich 20141010 ����Ӣ������ TRADE_ENG_DESC end
										 */
										updateOrderParm.addData("ID", sparm.getValue("ID", j));
										updateOrderParm.addData("SEQ", sparm.getValue("SEQ", j));
										updateOrderParm.addData("SECTION_DESC", sparm.getValue("SECTION_DESC", j));
										updateOrderParm.addData("ORDER_CODE", sparm.getValue("ORDER_CODE", j));
										updateOrderParm.addData("ORDER_DESC", sparm.getValue("ORDER_DESC", j));
										updateOrderParm.addData("ORDER_NUM", orderTable.getItemString(i, "ORDER_NUM"));// add
																														// by
																														// lich
																														// 20141127

										// ���ӵ�λ���÷���Ƶ�� add by huangjw 20150511
										updateOrderParm.addData("MEDI_UNIT", orderTable.getItemString(i, "MEDI_UNIT"));
										updateOrderParm.addData("ROUTE_CODE",
												orderTable.getItemString(i, "ROUTE_CODE"));
										updateOrderParm.addData("FREQ_CODE", orderTable.getItemString(i, "FREQ_CODE"));
										// ���ӵ�λ���÷���Ƶ�� add by huangjw 20150511

										updateOrderParm.addData("UNIT_CODE", sparm.getValue("UNIT_CODE", j));
										updateOrderParm.addData("UNIT_PRICE", sparm.getValue("UNIT_PRICE", j));

										double op = StringTool.round(sparm.getDouble("UNIT_PRICE", j)
												* orderTable.getItemDouble(i, "ORDER_NUM"), 2);
										updateOrderParm.addData("ORIGINAL_PRICE", op);
										// double rt = getValueDouble("RATE");
										double rt = orderTable.getItemDouble(i, "DISCOUNT_RATE");
										// ����󣬼���ʱ�̼۸�
										updateOrderParm.addData("RETAIL_PRICE", StringTool.round(rt * op, 0));
										//
										updateOrderParm.addData("DESCRIPTION", sparm.getValue("DESCRIPTION", j));
										updateOrderParm.addData("OPT_DATE", date);
										updateOrderParm.addData("OPT_USER", Operator.getID());
										updateOrderParm.addData("OPT_TERM", Operator.getIP());
										updateOrderParm.addData("SECTION_CODE", sparm.getValue("SECTION_CODE", j));
										updateOrderParm.addData("PACKAGE_CODE", sparm.getValue("PACKAGE_CODE", j));

										updateOrderParm.addData("DISCOUNT_RATE", rt);// add
																						// by
																						// lich
																						// 20141021
																						// �����ۿ���

										// add by lich 20141030
										// �����ײͼ۸����ͱ��ã����ִ�в��������ʱ��PRICE_TYPEΪ�գ�ʹ��PRICE_TYPE_BY���д�ֵ
										updateOrderParm.addData("PRICE_TYPE_BY", this.getValueString("PRICE_TYPE"));
										updateOrderParm.addData("PRICE_TYPE", sparm.getValue("PRICE_TYPE", j));// add
																												// by
																												// lich
																												// 20141021
																												// �����ײͼ۸�����
										updateOrderParm.addData("UN_NUM_FLG", orderParm.getValue("UN_NUM_FLG", i));// ===pangben
																													// 2015-9-2
																													// ��Ӳ�����
										totalDetialRetailPrice += StringTool.round(rt * op, 0);
									}
									// Modified by lx 2014/11/20 ��ϸ�����������������뼯��ҽ��������
									updateOrderParm.setData("RETAIL_PRICE", mainItemIndex, totalDetialRetailPrice);
									orderTable.setValueAt(totalDetialRetailPrice, i,
											orderTable.getColumnIndex("RETAIL_PRICE"));// ��6��ΪorderTable.getColumnIndex("RETAIL_PRICE")
																						// modify by huangjw 20150525
								}
							}
						}
					} else {// ����
						if (orderParm.getValue("ORDER_CODE", i).length() > 0) {
							/* add by lich 20141010 ����Ӣ������ TRADE_ENG_DESC start */
							insertOrderParm.addData("TRADE_ENG_DESC", orderParm.getValue("TRADE_ENG_DESC", i));
							/* add by lich 20141010 ����Ӣ������ TRADE_ENG_DESC end */
							insertOrderParm.addData("ID", orderParm.getValue("ID", i));
							insertOrderParm.addData("SEQ", orderParm.getValue("SEQ", i));
							insertOrderParm.addData("SECTION_DESC", orderTable.getItemString(i, "SECTION_DESC"));
							insertOrderParm.addData("ORDER_CODE", orderParm.getValue("ORDER_CODE", i));
							insertOrderParm.addData("ORDER_DESC", orderTable.getItemString(i, "ORDER_DESC"));
							insertOrderParm.addData("ORDER_NUM", orderTable.getItemString(i, "ORDER_NUM"));
							// ���ӵ�λ���÷���Ƶ�� add by huangjw 20150511
							insertOrderParm.addData("MEDI_UNIT", orderTable.getItemString(i, "MEDI_UNIT"));
							insertOrderParm.addData("ROUTE_CODE", orderTable.getItemString(i, "ROUTE_CODE"));
							insertOrderParm.addData("FREQ_CODE", orderTable.getItemString(i, "FREQ_CODE"));
							// ���ӵ�λ���÷���Ƶ�� add by huangjw 20150511
							insertOrderParm.addData("UNIT_CODE", orderTable.getItemString(i, "UNIT_CODE"));
							insertOrderParm.addData("UNIT_PRICE", orderTable.getItemString(i, "UNIT_PRICE"));
							insertOrderParm.addData("RETAIL_PRICE", orderTable.getItemString(i, "RETAIL_PRICE"));
							insertOrderParm.addData("DESCRIPTION", orderTable.getItemString(i, "DESCRIPTION"));
							insertOrderParm.addData("OPT_DATE", orderTable.getItemString(i, "OPT_DATE"));
							insertOrderParm.addData("OPT_USER", orderTable.getItemString(i, "OPT_USER"));
							insertOrderParm.addData("OPT_TERM", orderTable.getItemString(i, "OPT_TERM"));
							insertOrderParm.addData("DISCOUNT_RATE", orderTable.getItemString(i, "DISCOUNT_RATE"));// add
																													// by
																													// lich
																													// 20141021
																													// �����ۿ���
							insertOrderParm.addData("PRICE_TYPE", this.getValueString("PRICE_TYPE"));// add by lich
																										// 20141021
																										// �����ײͼ۸�����
							insertOrderParm.addData("SETMAIN_FLG", "N");// ������Ϊ"N",һ���ж�����Ǽ���ҽ����������ֶ��޸�Ϊ"Y"
							insertOrderParm.addData("ORDERSET_CODE", orderParm.getValue("ORDER_CODE", i));
							insertOrderParm.addData("ORDERSET_GROUP_NO", ordersetGroupNo);// ���
							insertOrderParm.addData("HIDE_FLG", "N");// ����hide_flg����N ϸ����Y
							insertOrderParm.addData("UN_NUM_FLG", orderParm.getValue("UN_NUM_FLG", i));// ===pangben
																										// 2015-9-2
																										// ��Ӳ�����
							insertOrderParm.addData("SECTION_CODE", sectionCode);
							insertOrderParm.addData("PACKAGE_CODE", packageCode);
							ordersetGroupNo++;

						}
					}
				}

				// ѭ������ҽ����������-�޸�����
				for (int k = 0; k < updateOrderParm.getCount("SEQ"); k++) {
					updateOrderData.addRowData(updateOrderParm, k);
				}
				// ѭ������ҽ������������
				for (int j = 0; j < insertOrderParm.getCount("SEQ"); j++) {
					insertOrderData.addRowData(insertOrderParm, j);
				}
				TParm orderDetailParm = new TParm();// ϸ��

				orderDetailParm = getOrderDetail(insertOrderData);

				for (int p = 0; p < orderDetailParm.getCount("ORDER_CODE"); p++) {
					insertOrderData.addRowData(orderDetailParm, p);
				}
			}

			// ����ҽ����ҽ�����ۣ����¼�����ʱ��ҽ����ϸ�ϼƣ���ʱ����ϸ��ֵ
			setTotDetailRetailPrice(0, 0);
			// ��ǰ����ʱ������
			TParm operSectionParm = new TParm();
			operSectionParm.addData("SECTION_CODE", sectionCode);
			operSectionParm.addData("PACKAGE_CODE", packageCode);
			TParm operSectionData = new TParm();
			operSectionData.addRowData(operSectionParm, 0);

			/** -----------------------------ʱ�̱��ȡ���޸ġ�ɾ������----------------------------- **/
			TParm sectionParm = table.getParmValue();
			TParm insertSectionParm = new TParm();
			TParm insertSectionData = new TParm();
			TParm updateSectionParm = new TParm();
			TParm updateSectionData = new TParm();
			// int secRow = table.getSelectedRow();
			if (sectionParm.getCount() > 0) {
				Timestamp date = StringTool.getTimestamp(new Date());
				for (int i = 0; i < sectionParm.getCount(); i++) {

					String exec = sectionParm.getValue("EXEC", i);
					if ("Y".equals(exec)) {// ������
						if (sectionParm.getValue("FLG", i).equals("Y")) {
							updateSectionParm.addData("SECTION_CODE", tableParm.getValue("SECTION_CODE", i));
							updateSectionParm.addData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE", i));
							updateSectionParm.addData("SECTION_DESC", table.getItemString(i, "SECTION_DESC"));
							updateSectionParm.addData("SECTION_ENG_DESC", table.getItemString(i, "SECTION_ENG_DESC"));
							updateSectionParm.addData("PY1", table.getItemString(i, "PY1"));
							updateSectionParm.addData("SEQ", tableParm.getValue("SEQ", i));
							updateSectionParm.addData("DESCRIPTION", table.getItemString(i, "DESCRIPTION"));
							updateSectionParm.addData("ORIGINAL_PRICE", table.getItemString(i, "ORIGINAL_PRICE"));
							updateSectionParm.addData("SECTION_PRICE", table.getItemString(i, "SECTION_PRICE"));
							updateSectionParm.addData("OPT_DATE", date);
							updateSectionParm.addData("OPT_USER", Operator.getID());
							updateSectionParm.addData("OPT_TERM", Operator.getIP());
							updateSectionParm.addData("START_DATE", table.getItemString(i, "START_DATE"));
							updateSectionParm.addData("END_DATE", table.getItemString(i, "END_DATE"));

							if ("".equals(table.getItemString(i, "DISCOUNT_RATE"))) {
								updateSectionParm.addData("DISCOUNT_RATE", "1.0");// add by lich 20141021 �����ۿ���
							} else {
								updateSectionParm.addData("DISCOUNT_RATE", table.getItemString(i, "DISCOUNT_RATE"));// add
																													// by
																													// lich
																													// 20141021
																													// �����ۿ���
							}

							// add by lich 20141030 �����ײͼ۸����ͱ��ã����ִ�в��������ʱ��PRICE_TYPEΪ�գ�ʹ��PRICE_TYPE_BY���д�ֵ
							updateSectionParm.addData("PRICE_TYPE_BY", this.getValueString("PRICE_TYPE"));// add by lich
																											// 20141021
																											// �����ײͼ۸�����
							updateSectionParm.addData("PRICE_TYPE", tableParm.getValue("PRICE_TYPE", i));// add by lich
																											// 20141021
																											// �����ײͼ۸�����
						}
					} else {// ����
						if (sectionParm.getValue("SECTION_DESC", i).length() > 0) {
							insertSectionParm.addData("SECTION_DESC", table.getItemString(i, "SECTION_DESC"));
							insertSectionParm.addData("SECTION_ENG_DESC", table.getItemString(i, "SECTION_ENG_DESC"));
							insertSectionParm.addData("PY1", table.getItemString(i, "PY1"));
							insertSectionParm.addData("SEQ", tableParm.getValue("SEQ", i));
							insertSectionParm.addData("DESCRIPTION", table.getItemString(i, "DESCRIPTION"));
							insertSectionParm.addData("ORIGINAL_PRICE", table.getItemString(i, "ORIGINAL_PRICE"));
							insertSectionParm.addData("SECTION_PRICE", table.getItemString(i, "SECTION_PRICE"));
							insertSectionParm.addData("OPT_DATE", table.getItemString(i, "OPT_DATE"));
							insertSectionParm.addData("OPT_USER", table.getItemString(i, "OPT_USER"));
							insertSectionParm.addData("OPT_TERM", table.getItemString(i, "OPT_TERM"));
							insertSectionParm.addData("SECTION_CODE", sectionCode);
							insertSectionParm.addData("PACKAGE_CODE", packageCode);
							if ("".equals(table.getItemString(i, "DISCOUNT_RATE"))) {
								insertSectionParm.addData("DISCOUNT_RATE", "1.0");// add by lich 20141021 �����ۿ���
							} else {
								insertSectionParm.addData("DISCOUNT_RATE", table.getItemString(i, "DISCOUNT_RATE"));// add
																													// by
																													// lich
																													// 20141021
																													// �����ۿ���
							}
							insertSectionParm.addData("PRICE_TYPE", this.getValueString("PRICE_TYPE"));// add by lich
																										// 20141021
																										// �����ײͼ۸�����

						}
					}
				}
			}
			// ѭ������ʱ�̱�������-�޸�����
			for (int k = 0; k < updateSectionParm.getCount("SEQ"); k++) {
				updateSectionData.addRowData(updateSectionParm, k);
			}
			// ѭ������ʱ�̱���������
			for (int j = 0; j < insertSectionParm.getCount("SEQ"); j++) {
				insertSectionData.addRowData(insertSectionParm, j);
			}

			/*------------�ײͱ��ȡ���޸ġ�ɾ������  add by  lich  20141022-----------start*/
			TParm packageParm = new TParm();
			packageParm.addData("OPT_USER", Operator.getID());
			packageParm.addData("OPT_TERM", Operator.getIP());

			packageParm.addData("PACKAGE_CODE", this.getValueString("PACKAGE_CODE"));
			allFee = this.getValueDouble("ALLFEE");
			double originalTotalSectionPrice = this.getValueDouble("ORIGINAL_TOTAL_SECTION_PRICE");

			packageParm.addData("ORIGINAL_TOTAL_SECTION_PRICE", originalTotalSectionPrice);
			packageParm.addData("PACKAGE_PRICE", allFee);
			packageParm.addData("PRICE_TYPE", this.getValueString("PRICE_TYPE"));
			packageParm.addData("RATE", this.getValueString("RATE"));
			// �����ײ�����ۿ۹������ż�ס���--xiongwg20150630
			packageParm.addData("CTZ_CODE", this.getValueString("CTZ_CODE"));
			packageParm.addData("ADM_TYPE", this.getValueString("ADM_TYPE"));

			TParm packageData = new TParm();
			packageData.addRowData(packageParm, 0);

			update.setData("PACKAGEDATA", packageData.getData());

			/*------------�ײͱ��ȡ���޸ġ�ɾ������  add by  lich  20141022-----------end*/

			update.setData("OPERSECTIONDATA", operSectionData.getData());
			update.setData("UPDATEORDERDATA", updateOrderData.getData());
			update.setData("INSERTORDERDATA", insertOrderData.getData());
			update.setData("DELORDERDATA", orderDelParm.getData());

			update.setData("UPDATESECTIONDATA", updateSectionData.getData());
			update.setData("INSERTSECTIONDATA", insertSectionData.getData());

			result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction", "onSave", update);

			// ҳ����ʾ����
			setTotDetailRetailPrice(1, 0);// ���¼���ϸ���е����ۼ۸��ܶ�
			setTotRetailPrice();// ����ʱ�̱��е����ۼ۸��ܶ�
			setTotOriginalPrice();

			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ�ܣ�");
				return;
			} else {
				if (synButtonflag) {
					this.messageBox("����ɹ���");
					synButtonflag = true;
				}
			}

			orderDelParm = new TParm();

			if (packageCode.length() > 0) {
				idOrderChanged = false;
				onMainTableClick();// --����
			}
			if (isRateChanged) {
				updataPrice();
			}

			onQuery();

			table.setSelectedRow(selectedRow);
			onMainClick2(selectedRow);
			this.onMainTableClick();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ɾ��-ҽ���б���Ϣ
	 */
	public void onDelete() {
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		orderTable.acceptText();
		// int selectedIndx=orderTable.getSelectedRow();
		TParm parm = orderTable.getParmValue();
		if (!flag) {// ɾ��ʱУ��ҽ����ϸ�����ܹ�ѡ�˼�������ֻУ��һ��
			for (int i = 0; i < parm.getCount("SELECTD") - 1; i++) {
				if (parm.getValue("SELECTD", i).equals("Y")) {
					if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ�е�ҽ����ϸ��", "��Ϣ", JOptionPane.YES_NO_OPTION) == 0) {
						break;
					}
					return;
				}
			}
		}
		for (int i = 0; i < parm.getCount("SELECTD") - 1; i++) {
			if (parm.getValue("SELECTD", i).equals("Y")) {
				String seq = orderTable.getItemString(i, "SEQ");
				String discountRate = orderTable.getItemString(i, "DISCOUNT_RATE");
				orderDelParm.addData("SEQ", seq);
				orderDelParm.addData("ID", parm.getValue("ID", i));
				orderDelParm.addData("SECTION_CODE", sectionCode);
				orderDelParm.addData("PACKAGE_CODE", packageCode);
				orderDelParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				orderDelParm.addData("SETMAIN_FLG", parm.getValue("SETMAIN_FLG", i));// ��ϸ��
				orderDelParm.addData("PRICE_TYPE", getValue("PRICE_TYPE"));// �۸����� add by lich 20141023
				orderDelParm.addData("DISCOUNT_RATE", discountRate);// �����ۿ��� add by lich 20141023
				orderTable.removeRow(i);
				flag = true;
				try {
					setTotDetailRetailPrice(0, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				setTotDetailOriginalPrice();
				selectCheckBox();

				setTotOriginalPrice();
				selectSaleCheckBox();
				onDelete();
				return;
			}
		}
		if (!flag) {
			int row = table.getSelectedRow();
			if (row < 0) {
				this.messageBox("��ѡ��һ��ʱ����ϸ");
				return;
			}
			// �ж�ʱ�̱��Ƿ�ѡ������-ɾ��
			String sectionCode2 = sectionCode;
			String packageCode2 = packageCode;
			// �ж������Ƿ����ɾ��-������ӵ�sectionDelParm��
			if (!onIfDelSection(sectionCode2, packageCode2)) {
				this.messageBox("��ʱ���ײ���ҽ����ϸ��������ɾ����ʱ���ײͣ�");
				return;
			} else {
				if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ��ʱ���ײͣ�", "��Ϣ", JOptionPane.YES_NO_OPTION) == 0) {
					TParm result = new TParm();
					TParm delParm = new TParm();
					delParm.addData("SECTION_CODE", sectionCode2);
					delParm.addData("PACKAGE_CODE", packageCode2);
					result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction", "onDelSection",
							delParm);
					if ("true".equals(result.getValue("FLAG", 0))) {
						this.messageBox("ɾ���ɹ���");
						orderTable.removeRowAll();
						onInit();// ��ʼ��
					} else {
						this.messageBox("ɾ��ʧ�ܣ�");
					}

				}
			}

			try {
				setTotDetailRetailPrice(0, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			setTotDetailOriginalPrice();
			selectCheckBox();

			setTotOriginalPrice();
			selectSaleCheckBox();
		} else {
			onSaveFlg(false);
		}

		flag = false;

	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("PACKAGE_CODE;ALLFEE;OWN_PRICE;ALL");
		this.clearValue("CTZ_CODE;ADM_TYPE");
		table.removeRowAll();
		orderTable.removeRowAll();
		isRateChanged = false;
		rateClear();// add by lich 20141105
		idOrderChanged = false;
		flag = false;
	}

	/**
	 * �ײ���ϸ��ѯ yuml
	 */
	public void onDetailQuery() {
		TParm reParm = new TParm();
		openDialog("%ROOT%\\config\\mem\\MemPackageDetailQuery.x", reParm);
	}

	/**
	 * ��������
	 *
	 * @return String
	 */
	private String getQuerySrc() { // �õ����Ҵ���
		String code = getText("PACKAGE_CODE");
		// �õ���������
		String sb = "";
		if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
			// ���������
			if (code != null && code.length() > 0)
				sb += "PACKAGE_CODE like '" + code + "%'";
		return sb;
	}

	/**
	 * ʱ����ϸ�б���ʾ
	 */
	public void showSectionDetail(String packageCode) {
		// ʱ�̱��ÿ�parm
		TParm newparm = new TParm();
		table.setParmValue(newparm);
		// ��ȡʱ�̱�CODE���ֵ
		sectionIdNo = getMaxSeq("SECTION_CODE", "MEM_PACKAGE_SECTION", "PACKAGE_CODE", packageCode, "", "");// �����иĶ�
		// ��ȡʱ�̱�SEQ���ֵ
		sectionSeqNo = getMaxSeq("SEQ", "MEM_PACKAGE_SECTION", "PACKAGE_CODE", packageCode, "", "");
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		/* modify by lich 20141021-------start */
		TParm nullParm = new TParm();
		orderTable.setParmValue(nullParm);
		/* modify by lich 20141021-------end */
		String sql = " SELECT   'N' AS FLG, 'Y' AS EXEC, A.SECTION_CODE, A.PACKAGE_CODE,"
				+ " A.SECTION_DESC, A.SECTION_ENG_DESC, A.PY1, A.PY2, A.SEQ,"
				+ " A.DESCRIPTION, A.ORIGINAL_PRICE, B.SECTION_PRICE, A.OPT_DATE,"
				+ " A.OPT_USER, A.OPT_TERM, A.START_DATE, A.END_DATE, B.DISCOUNT_RATE," + " B.PRICE_TYPE"
				+ " FROM MEM_PACKAGE_SECTION A, "
				+ " ( SELECT PRICE_TYPE,PACKAGE_CODE,SECTION_CODE,DISCOUNT_RATE,SECTION_PRICE FROM MEM_PACKAGE_SECTION_PRICE "
				+ " WHERE PRICE_TYPE = '" + this.getValue("PRICE_TYPE") + "') B"
				+ " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE(+)" + " AND A.SECTION_CODE = B.SECTION_CODE(+)"
				+ " AND A.PACKAGE_CODE = '" + packageCode + "'" + " ORDER BY A.PACKAGE_CODE, A.SEQ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// //�����ײ��ܼ�=ʱ�̼۸�֮��
		double ownPrice = 0.00;
		int count = result.getCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				ownPrice += Double.parseDouble(result.getValue("ORIGINAL_PRICE", i));
			}
			DecimalFormat df = new DecimalFormat("0.00");
			String newOwnPrice = df.format(ownPrice);
			this.setValue("ORIGINAL_TOTAL_SECTION_PRICE", newOwnPrice);// �ײ�ԭ��
			this.setValue("PACKAGE_CODE", packageCode);
			table.setParmValue(result);
			insertTableRow("TABLE");
		} else {// ���ײ�û����
			onClear();
			this.setValue("PACKAGE_CODE", packageCode);
			insertTableRow("TABLE");
		}
		isRateChanged = false;
	}

	/**
	 * ʱ�̱����¼�-ҽ������ʾ
	 */
	public void onMainTableClick() {

		if (idOrderChanged) {
			idOrderChanged = false;
		}
		try {

			// ��ɾ��orderDelParm������
			orderDelParm.removeData("SEQ");
			orderDelParm.removeData("SECTION_CODE");
			orderDelParm.removeData("PACKAGE_CODE");
			// ���sectionCode
			// sectionCode = "";

			TTable table = (TTable) callFunction("UI|TABLE|getThis");
			TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
			/* modify by zhp 20141020 ----------start */
			TParm nullParm = new TParm();
			orderTable.setParmValue(nullParm);
			/* modify by zhp 20141020 ----------end */
			int selectedIndx = table.getSelectedRow();
			if (selectedIndx < 0) {
				return;
			}
			TParm tableparm = table.getParmValue();
			sectionDesc = tableparm.getValue("SECTION_DESC", selectedIndx);// ʱ��������ֵ
			if (!"".equals(tableparm.getValue("SECTION_CODE", selectedIndx))) {
				sectionCode = tableparm.getValue("SECTION_CODE", selectedIndx);
			}
			if (!"".equals(tableparm.getValue("PACKAGE_CODE", selectedIndx))) {
				packageCode = tableparm.getValue("PACKAGE_CODE", selectedIndx);
			}

			rateOld = tableparm.getDouble("DISCOUNT_RATE", selectedIndx);

			String orderSql = "SELECT 'N' AS SELECTD,'N' AS FLG, 'Y' AS EXEC, A.ID, A.SEQ, A.SECTION_DESC, A.ORDER_CODE,"
					+ " A.ORDER_DESC, A.TRADE_ENG_DESC, A.ORDER_NUM, A.UNIT_CODE,"
					+ " A.UNIT_PRICE, B.RETAIL_PRICE, A.DESCRIPTION, A.OPT_DATE, A.OPT_USER,"
					+ " A.OPT_TERM, B.DISCOUNT_RATE, A.SECTION_CODE, A.PACKAGE_CODE,"
					+ " A.SETMAIN_FLG, A.ORDERSET_CODE, A.ORDERSET_GROUP_NO, A.HIDE_FLG,"
					+ " B.PRICE_TYPE,A.MEDI_UNIT,A.ROUTE_CODE,A.FREQ_CODE,A.UN_NUM_FLG,C.ACTIVE_FLG "// ==pangben
																										// 2015-9-2
																										// ���UN_NUM_FLG�������ֶ�
					+ " FROM MEM_PACKAGE_SECTION_D A,SYS_FEE C,"
					+ " (SELECT ID, PRICE_TYPE, PACKAGE_CODE, SECTION_CODE, DISCOUNT_RATE, RETAIL_PRICE"
					+ " FROM MEM_PACKAGE_SECTION_D_PRICE" + " WHERE PRICE_TYPE = '" + this.getValue("PRICE_TYPE")
					+ "') b" + " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE(+)" + " AND A.SECTION_CODE = B.SECTION_CODE(+)"
					+ " AND A.ORDER_CODE = C.ORDER_CODE(+)" + " AND A.ID = B.ID(+)" + " AND A.PACKAGE_CODE = '"
					+ packageCode + "'" + " AND A.SECTION_CODE = '" + sectionCode + "'" + " AND A.HIDE_FLG = 'N'"
					+ " ORDER BY SEQ";

			// System.out.println("orderSql=========="+orderSql);
			TParm result = new TParm(TJDODBTool.getInstance().select(orderSql));
			for (int i = 0; i < result.getCount("SEQ"); i++) {
				String setmainFlg = result.getValue("SETMAIN_FLG", i);// add by lich 20141117 �Ƿ��Ǽ���ҽ��
				if ("Y".equals(setmainFlg)) {// ��ѡ���ҽ�����ص��Ǽ���ҽ�������۶��������� add by lich 20141117
					orderTable.setLockCell(i, orderTable.getColumnIndex("RETAIL_PRICE"), true);// modify by lich
																								// 20141106
				} else {
					orderTable.setLockCell(i, orderTable.getColumnIndex("RETAIL_PRICE"), false);// ��6��ΪorderTable.getColumnIndex("RETAIL_PRICE")
																								// modify by huangjw
																								// 20150508
				}
			}

			// ��ѯ������
			TParm seqParm = new TParm(TJDODBTool.getInstance()
					.select("SELECT MAX(SEQ) AS SEQ " + " FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE='" + sectionCode
							+ "' " + " AND PACKAGE_CODE = '" + packageCode + "'"));
			String seq = seqParm.getValue("SEQ", 0).toString() == null ? "0" : seqParm.getValue("SEQ", 0).toString();
			orderSeqNo = Integer.parseInt(seq) + 1;

			// ��ȡ���
			ordersetGroupNo = getMaxSeq("ORDERSET_GROUP_NO", "MEM_PACKAGE_SECTION_D", "SECTION_CODE", sectionCode,
					"PACKAGE_CODE", packageCode);

			if (result.getCount() > 0) {

				for (int i = 0; i < result.getCount(); i++) {
					if (!"Y".equals(result.getData("ACTIVE_FLG", i))) {
						result.setData("ACTIVE_FLG", i, "Y");
					} else {
						result.setData("ACTIVE_FLG", i, "N");
					}
				}
				orderTable.setParmValue(result);
			} else {
				orderTable.setParmValue(result);
			}

			this.setValue("ALL", "N");
			insertRow("ORDER_TABLE");
			setTotDetailRetailPrice(1, 0);// ����ϸ���ܺ�
			setTotDetailOriginalPrice();
			setTotOriginalPrice();// 20141107
			selectCheckBox();// 20141107
			selectSaleCheckBox();// add by lich 20141107

			isRateChanged = false;

			try {
				if (table.getSelectedColumn() == table.getColumnIndex("FLG")) {
					if ("Y".equals(table.getCellEditor(table.getSelectedColumn()).getCellEditorValue())) {
						table.setItem(table.getSelectedRow(), "FLG", "N");
					} else {
						table.setItem(table.getSelectedRow(), "FLG", "Y");
					}
					table.getCellEditor(table.getSelectedColumn()).stopCellEditing();
					selectSaleCheckBox();
					selectCheckBox();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ҽ����ϸ�����¼�
	 */
	public void onDetailTableClick() {
		TTable table = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		table.acceptText();
		table.getTable().grabFocus(); // ��ȡ���㣬���Իس��������
		// ���һ��������
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			insertRow("ORDER_TABLE");
			// autoHeight();
		}
	}

	/**
	 * 
	 */
	public void onOrderTableChangeValue(TTableNode tNode) {
		idOrderChanged = true;
		try {
			TTable table = (TTable) callFunction("UI|ORDER_TABLE|getThis");
			table.acceptText();
			// �õ�table�ϵ�parmmap������
			String columnName = table.getDataStoreColumnName(tNode.getColumn());
			// �õ���ǰ�ı�������
			if ("DISCOUNT_RATE".equals(columnName)) {
				updateOrderTableData(tNode);
				orderTable.acceptText();
				setTotDetailRetailPrice(0, 0);
			}
			// add by lich 20141127
			if ("ORDER_NUM".equals(columnName)) {
				updateOrderTableDataSalePriceByNum(tNode);
				orderTable.acceptText();
				stopTableEditing(orderTable);
				setTotDetailRetailPrice(0, 0);
			}
			// RETAIL_PRICE
			if ("RETAIL_PRICE".equals(columnName)) {
				updateOrderTableDataSalePrice(tNode);
				orderTable.acceptText();
				orderTable.setItem(orderTable.getSelectedRow(), "DISCOUNT_RATE",
						this.table.getItemString(this.table.getSelectedRow(), "DISCOUNT_RATE"));
				stopTableEditing(orderTable);
				setTotDetailRetailPrice(0,
						Double.valueOf("" + tNode.getValue()) - Double.valueOf("" + tNode.getOldValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// MessageBox();
	}

	/**
	 * ʱ�̱�����¼�
	 */
	public void onTableChangeValue(TTableNode tNode) {
		try {
			TTable table = (TTable) callFunction("UI|TABLE|getThis");
			table.acceptText();
			TParm parm = table.getParmValue();
			table.acceptText();
			int row = tNode.getRow();
			// int column = tNode.getColumn();
			// �õ�table�ϵ�parmmap������
			String columnName = table.getDataStoreColumnName(tNode.getColumn());
			// �õ���ǰ�ı�������
			String value = "" + tNode.getValue();
			// ��������Ƹı���ƴ��1�Զ�����,�������Ʋ���Ϊ��
			if ("SECTION_DESC".equals(columnName)) {
				if (value.equals("") || value == null) {
					messageBox_("���Ʋ���Ϊ��!");
					return;
				}
				// ��ȡPY1
				String py = SYSHzpyTool.getInstance().charToCode(value);
				table.setItem(row, "PY1", py);
			}
			// ���һ��������
			if (table.getSelectedRow() == table.getRowCount() - 1) {
				if (tNode.getColumn() == 3) {
					insertTableRow("TABLE");
					if (table.getSelectedRow() == -1) {
						messageBox("���ȱ���ʱ�̺�ѡ��ʱ�̣������ҽ����ϸ��");
						return;
					}
					parm.setData("SECTION_CODE", table.getSelectedRow(), String.valueOf(sectionIdNo));
					table.setParmValue(parm);
					sectionSeqNo += 1;
					sectionIdNo += 1; //
				}
			}

			if ("DISCOUNT_RATE".equals(columnName)) {
				double rate = Double.valueOf("" + tNode.getValue());
				updateTableData(tNode);
				orderTable.acceptText();
				table.acceptText();
				// DecimalFormat df = new DecimalFormat("########0.00");
				// double temp = 0.0;
				if (rate > 1) {
					return;
				}
				setTotDetailRetailPrice(1, 0);// add by lich 20141105
			}
			if ("SECTION_PRICE".equals(columnName)) {
				DecimalFormat df = new DecimalFormat("#########0.00");
				updateTableData("TABLE", row, tNode.getColumn(), tNode.getValue());
				double o = parm.getDouble("ORIGINAL_PRICE", tNode.getRow());
				double sp = Double.valueOf((("" + tNode.getValue()).length() == 0 ? "0" : "" + tNode.getValue()));
				double dr = Double.parseDouble(df.format(sp / o));
				updateTableData("TABLE", row, table.getColumnIndex("DISCOUNT_RATE"), dr);
				if (StringTool.round(Double.valueOf("" + tNode.getOldValue()), 0) != StringTool.round(sp, 0)) {
					getMessageBox(tNode);
				}
				selectCheckBox();// add 20141107
				selectSaleCheckBox();// add by lich 20141107
				orderTable.acceptText();
				table.acceptText();
				// double rate = table.getItemDouble(row,"DISCOUNT_RATE");
				setTotDetailRetailPrice(1, 0);// add by lich 20141105
			}

			if ("FLG".equals(columnName)) {
				updateTableData("TABLE", row, table.getColumnIndex("FLG"), tNode.getValue());
				selectCheckBox();// add 20141107
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	/**
	 * ��Tableѡ�е�����,ѭ��ÿ��ѡ�е�����ͬ��ҽ���ۿ��ʼ����
	 */
	public void changeOrderByRate() {
		int rowCount = table.getRowCount();
		String strFlg = "N";
		// 1.ȡ��ѡ�е�ֵ
		List checksRows = new ArrayList();
		for (int i = 0; i < rowCount - 1; i++) {
			strFlg = table.getItemString(i, "FLG");
			// this.messageBox(""+strFlg);
			if (strFlg.equalsIgnoreCase("Y")) {
				checksRows.add(i);
			}
		}
		if (checksRows.size() == 0) {
			this.messageBox("��ѡ��Ҫͬ���ۿ��ʵ�ʱ�̣�");
			return;
		}
		// 2.ѭ������ڼ�¼
		for (int i = 0; i < rowCount - 1; i++) {
			// ��ȷ���Ƿ���ѡ�е�
			strFlg = table.getItemString(i, "FLG");
			if (strFlg.equalsIgnoreCase("Y")) {
				table.setSelectedRow(i);

				try {
					changeOrderOne();
					onMainTableClick();
					// ���³ɹ���ָ�ѡ�У�/�Զ��ָ� checkboxѡ��
					for (int j = 0; j < checksRows.size(); j++) {
						table.setItem((Integer) checksRows.get(j), "FLG", "Y");
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.messageBox("ͬ���ۿ���ʧ�ܣ�");
					return;
				}
			}
		}
	}

	/**
	 * ͨ��ʱ���ۿ��ʼ���ҽ��ϸ���ۿ��ʣ�������ͬ����ťʹ�ã� add by lich 20141117
	 * 
	 * @throws Exception
	 */
	public void changeOrderOne() throws Exception {
		synButtonflag = false;
		double unitPrice;
		double num;
		DecimalFormat df = new DecimalFormat("########0.00");
		int row = table.getSelectedRow();
		double rate = table.getItemDouble(row, "DISCOUNT_RATE");
		if (rate > 0.0 && rate <= 1.0) {
			int rowCount = orderTable.getRowCount();
			for (int i = 0; i < rowCount - 1; i++) {
				orderTable.setItem(i, "DISCOUNT_RATE", rate);
				unitPrice = orderTable.getItemDouble(i, "UNIT_PRICE");
				num = orderTable.getItemDouble(i, "ORDER_NUM");
				orderTable.setItem(i, "RETAIL_PRICE",
						Math.round(num * unitPrice * Double.parseDouble(df.format(rate))));

			}
			setTotDetailRetailPrice(0, 0);
			// ���棬ͬ��ϸ��۸�
			onSaveFlg(true);
		} else {
			messageBox("�ۿ���ֻ�ܴ���0��С��1");
			return;
		}
	}

	public void changeOrderByOrderRate() {
		onSaveFlg(true);
	}

	/**
	 * ÿ��λ�Ӷ��Ŵ���
	 */
	public String feeConversion(String fee) {
		String str1 = "";
		String[] s = fee.split("\\.");// ��"."���ָ�

		str1 = new StringBuilder(s[0].toString()).reverse().toString(); // �Ƚ��ַ����ߵ�˳��
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// ����ٽ�˳��ת����
		String str3 = new StringBuilder(str2).reverse().toString();
		// ����С��������
		StringBuffer str4 = new StringBuffer(str3);
		str4 = str4.append(".").append(s[1]);
		return str4.toString();
	}

	/**
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * ���һ��������-ҽ����
	 */
	public void insertRow(String opertable) {
		TTable table = (TTable) callFunction("UI|" + opertable + "|getThis");
		table.acceptText();
		// int oldrow = table.getRowCount() - 1;
		int row = table.addRow();
		table.setItem(row, "OPT_USER", Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		table.setItem(row, "OPT_DATE", date);
		table.setItem(row, "OPT_TERM", Operator.getIP());
		table.setItem(row, "DISCOUNT_RATE", 1.0);// ����Ĭ���ۿ���Ϊ1.0 add by lich 20141022
		table.setItem(row, "SEQ", String.valueOf(orderSeqNo));// �������add duzhw 0508
		orderSeqNo += 1;

	}

	/**
	 * ���һ��ʱ��������-ʱ�̱�
	 */
	public void insertTableRow(String opertable) {
		TTable table = (TTable) callFunction("UI|" + opertable + "|getThis");
		table.acceptText();
		TParm parm = table.getParmValue();
		// int oldrow = table.getRowCount() - 1;
		int row = table.addRow();
		table.setItem(row, "OPT_USER", Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		table.setItem(row, "OPT_DATE", date);
		table.setItem(row, "DISCOUNT_RATE", 1.0);// ����Ĭ���ۿ���Ϊ1.0 add by lich 20141022
		table.setItem(row, "OPT_TERM", Operator.getIP());
		// ����һ��ʱ�Ͳ�����������-add duzhw 0508
		parm.setData("SEQ", row, String.valueOf(sectionSeqNo));
		sectionSeqNo += 1;
		parm.setData("PACKAGE_CODE", row, packageCode);
		parm.setData("DISCOUNT_RATE", row, 1.0);// ����Ĭ���ۿ���Ϊ1.0 add by lich 20141022
		parm.setData("SECTION_CODE", row, String.valueOf(sectionIdNo));
		table.setParmValue(parm);

	}

	/**
	 * ˢ�²���
	 */
	public void refresh() {

	}

	/**
	 * �õ����ı�� +1
	 *
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(String maxValue, String tableName, String where1, String value1, String where2,
			String value2) {
		String sql = "SELECT MAX(CAST(" + maxValue + " AS INT)) AS " + maxValue + " FROM " + tableName + " WHERE 1=1 ";
		if (where1.trim().length() > 0) {
			sql += " AND " + where1 + " ='" + value1 + "'";
		}
		if (where2.trim().length() > 0) {
			sql += " AND " + where2 + " ='" + value2 + "'";
		}
		// System.out.println("���ı��sql="+sql);
		// ��������
		int max = 0;
		// ��ѯ������
		TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
		String seq = seqParm.getValue(maxValue, 0).toString().equals("") ? "0"
				: seqParm.getValue(maxValue, 0).toString();
		int value = Integer.parseInt(seq);
		// �������ֵ
		if (max < value) {
			max = value;
		}
		// ���ż�1
		max++;
		return max;

	}

	/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 */
	public void showPopMenu() {
		orderTable.acceptText();
		TParm orderParm = orderTable.getParmValue();
		String exec = orderParm.getValue("EXEC", orderTable.getSelectedRow());
		String setmainFlg = orderParm.getValue("SETMAIN_FLG", orderTable.getSelectedRow());
		// �����ݲ����Ǽ���ҽ���Ż����Ҽ�
		if ("Y".equals(exec)) {
			if ("Y".equals(setmainFlg)) {
				orderTable.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onOrderSetShow");
			} else {
				orderTable.setPopupMenuSyntax("");
			}
		} else if (exec == null || "".equals(exec) || exec.length() == 0) {
			orderTable.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onOrderSetShow");
		}

	}

	/**
	 * �޸�ҽ��ϸ�࣬�ײ�ϸ��TABLE�һ��¼�������ϸ���б������޸�ϸ����Ϣ
	 */
	public void onOrderSetShow() {
		TParm parm = new TParm();

		TParm orderparm = orderTable.getParmValue();
		int tableSelectedRow = table.getSelectedRow();
		int row = orderTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		// String orderCode = orderTable.getItemString(row, "ORDER_CODE");
		String orderCode = orderparm.getValue("ORDER_CODE", row);
		String exec = orderparm.getValue("EXEC", row);
		parm.setData("ORDERSET_CODE", orderCode);
		parm.setData("EXEC", exec);
		parm.setData("SECTION_CODE", sectionCode);
		parm.setData("PACKAGE_CODE", packageCode);
		parm.setData("PRICE_TYPE", getValue("PRICE_TYPE"));
		parm.setData("ORDERSET_GROUP_NO", orderparm.getValue("ORDERSET_GROUP_NO", row));
		// parm.setData("DOSAGE_QTY",orderparm.getValue("ORDER_NUM"),row );
		parm.setData("DISCOUNT_RATE", orderparm.getValue("DISCOUNT_RATE", row));

		parm.setData("OWN_PRICE", orderparm.getValue("OWN_PRICE", row));
		/*
		 * double unitPrice = Double.parseDouble(orderparm.getValue("UNIT_PRICE", row));
		 * double rate = Double.parseDouble(orderparm.getValue("DISCOUNT_RATE", row));
		 * double orderNum = Double.parseDouble(orderparm.getValue("ORDER_NUM", row));
		 */
		parm.setData("ID", orderparm.getValue("ID", row));

		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\mem\\MEMOrderSetShow.x", parm);
		if (reParm != null) {
			if ("SAVE".equals(reParm.getValue("OPER"))) {
				// ˢ��
				showSectionDetail(nodeId);
				table.setSelectedRow(tableSelectedRow);
				onMainClick2(tableSelectedRow);
				orderTable.setSelectedRow(row);
			}
			this.onSaveFlg(false);
		}

	}

	public void getOrderParmRate() {
		// DOSAGE_QTY;����;OWN_PRICE ԭ��;PACKAGE_PRICE�ײͼ� ;OWN_AMT�ܼ�
	}

	/**
	 * �����ײ�����ά��ҳ��-��ά��
	 */
	public void openMainPackage() {
		TParm parm = new TParm();
		// �������
		this.openDialog("%ROOT%\\config\\mem\\MemMainPackage.x", parm);
		onInit();

	}

	/**
	 * �Ƿ����ɾ��ʱ�̱������ж�
	 */
	public boolean onIfDelSection(String sectionCode, String packageCode) {
		boolean flag = false;
		TParm result = new TParm();
		TParm delParm = new TParm();
		delParm.addData("SECTION_CODE", sectionCode);
		delParm.addData("PACKAGE_CODE", packageCode);
		result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction", "onIfDelSection", delParm);
		if (result.getCount("ID") > 0) {
			flag = false;// ������ɾ��
		} else {
			flag = true;// ����ɾ��
		}
		return flag;
	}

	/**
	 * �����Ƿ��Ǽ���ҽ��
	 */
	public boolean checkIsDetailOrder(TParm parm) {
		boolean flag = false;
		TParm result = new TParm();
		result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction", "checkIsDetailOrder", parm);
		if (result.getCount("ORDER_CODE") > 0) {
			flag = true;// �Ǽ���ҽ��
		} else {
			flag = false;// ���Ǽ���ҽ��
		}
		return flag;
	}

	/**
	 * ���㼯��ҽ��-ϸ����ܼ۸�
	 */
	public String onSumDetailPrice(TParm parm) {
		String sumPrice = "0.00";
		TParm result = new TParm();
		result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction", "onSumDetailPrice", parm);
		if (result.getCount("OWN_AMT") > 0) {
			sumPrice = result.getValue("OWN_AMT", 0);
		}
		return sumPrice;
	}

	/**
	 * ��ѯ����ҽ��-ϸ����ӵ�result��
	 */
	public TParm getOrderDetail(TParm insertOrderParm) {
		TParm parm = new TParm();
		TParm result = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		for (int i = 0; i < insertOrderParm.getCount(); i++) {
			TParm detailOrder = insertOrderParm.getRow(i);
			parm = new TParm(TJDODBTool.getInstance().select(getDetailOrderDataSql(detailOrder)));
			if (parm.getCount() > 0) {
				// �Ƚ�insertOrderParm �е����� SETMAIN_FLG ��ΪY
				insertOrderParm.setData("SETMAIN_FLG", i, "Y");
				for (int j = 0; j < parm.getCount(); j++) {
					String orderIdNo = SystemTool.getInstance().getNo("ALL", "MEM", "PACKAGESECTIOND",
							"PACKAGESECTIOND");
					insertOrderParm.addData("ID", orderIdNo);
					insertOrderParm.addData("SEQ", "");// ��ϸ����Ϊ�� ��ռ�ű�������������ʾ
					// orderSeqNo++;
					insertOrderParm.addData("SECTION_DESC", insertOrderParm.getValue("SECTION_DESC", i));
					insertOrderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", j));
					insertOrderParm.addData("ORDER_DESC", parm.getValue("ORDER_DESC", j));
					insertOrderParm.addData("ORDER_NUM", parm.getValue("ORDER_NUM", i));// ϸ������
					insertOrderParm.addData("UNIT_CODE", parm.getValue("MEDI_UNIT", j));
					insertOrderParm.addData("UNIT_PRICE", parm.getValue("OWN_PRICE", j));
					insertOrderParm.addData("RETAIL_PRICE", parm.getValue("OWN_PRICE", j));// Ŀǰ��д��ԭ��
					insertOrderParm.addData("DESCRIPTION", insertOrderParm.getValue("DESCRIPTION", i));
					insertOrderParm.addData("OPT_DATE", date);
					insertOrderParm.addData("OPT_USER", Operator.getID());
					insertOrderParm.addData("OPT_TERM", Operator.getIP());

					insertOrderParm.addData("SETMAIN_FLG", "N");// ϸ��ΪN
					insertOrderParm.addData("ORDERSET_CODE", insertOrderParm.getValue("ORDER_CODE", i));
					insertOrderParm.addData("ORDERSET_GROUP_NO", insertOrderParm.getValue("ORDERSET_GROUP_NO", i));// ���
					insertOrderParm.addData("HIDE_FLG", "Y");// ϸ����Y
					insertOrderParm.addData("SECTION_CODE", sectionCode);
					insertOrderParm.addData("PACKAGE_CODE", packageCode);

					insertOrderParm.addData("DISCOUNT_RATE", orderTable.getItemString(i, "DISCOUNT_RATE"));// add by
																											// lich
																											// 20141021
																											// �����ۿ���
					insertOrderParm.addData("PRICE_TYPE", this.getValueString("PRICE_TYPE"));// add by lich 20141021
																								// �����ײͼ۸�����
					insertOrderParm.addData("UN_NUM_FLG", detailOrder.getValue("UN_NUM_FLG"));// ====pangben 2015-9-2
																								// ������
				}
			}
		}
		return result;
	}

	/**
	 * �ж��Ƿ�Ϊ���ڵ� add by sunqy 20140806
	 * 
	 * @param packageCode
	 * @return
	 */
	private boolean isLeaf(String packageCode) {
		String sql = "SELECT SEQ FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE = '" + packageCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * ͨ������۸�����ۿ��� add by lich 20141017
	 */
	public void getRate() {
		double allFee = this.getValueDouble("AllFee");
		double ownPrice = this.getValueDouble("OWN_PRICE");
		if (allFee > ownPrice) {
			messageBox("�ײͼ۸��ܴ���ԭ�۸�");
			this.setValue("AllFee", ownPrice);
			return;
		}
		if (allFee < 0) {
			messageBox("�ײͼ۸�������0");
			this.setValue("AllFee", ownPrice);
			return;
		}
		DecimalFormat df = new DecimalFormat("#######0.00");
		double temp = 0.0;
		String ownPriceStr = this.getValueString("OWN_PRICE");
		ownPrice = Double.parseDouble(ownPriceStr.replaceAll(",", ""));
		TTable table = getTable("TABLE");
		// TTable orderTable = getTable("ORDER_TABLE");

		if (ownPrice == 0.0) {
			messageBox("�ײ�ԭʼ�۸���Ϊ0");
			return;
		}
		allFee = this.getValueDouble("ALLFEE");
		double rate = Double.parseDouble(df.format(allFee / ownPrice));
		this.setValue("RATE", rate);

		int rowCount = table.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			table.setItem(i, "DISCOUNT_RATE", rate);
			temp = table.getItemDouble(i, "ORIGINAL_PRICE");
			table.setItem(i, "SECTION_PRICE", df.format(temp * Double.parseDouble(df.format(rate))));
		}
		selectSaleCheckBox();// 20141111 modify by lich
		isRateChanged = true;

	}

	/**
	 * ����ۿ����е�ֵ��ϸ��ϼ� add by lich 20141021
	 */
	private void rateClear() {
		this.setValue("RATE", 1.0);
		this.setValue("ALL_SELECT", false);
		this.clearValue("TOTAL_RETAIL_PRICE");
		this.clearValue("ORIGINAL_TOTAL_SECTION_PRICE");
		this.clearValue("TOTAL_SECTION_PRICE");
		this.clearValue("ORIGINAL_TOTAL_RETAIL_PRICE");

	}

	/**
	 * ����ϸ���е����ۼ۸��ܶ� add by lich 20141020
	 */
	public void setTotDetailRetailPrice(int p, double v) throws Exception {

		/* ����ϸ���е����ۼ۸��ܶ� add by lich 20141020------start */
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		double totalRetailPrice = 0.0;
		double retailPrice = 0.0;
		double totalUnitPrice = 0.0;
		double unitPrice = 0.0;
		int orderNum = 1;
		String orderNumStr = "";
		for (int i = 0; i < orderTable.getRowCount(); i++) {
			retailPrice = Double.parseDouble(
					("" + orderTable.getValueAt(i, orderTable.getColumnIndex("RETAIL_PRICE"))).length() == 0 ? "0"
							: "" + orderTable.getValueAt(i, orderTable.getColumnIndex("RETAIL_PRICE")));// ��6��ΪorderTable.getColumnIndex("RETAIL_PRICE")
																										// modify by
																										// huangjw
																										// 20150508
			unitPrice = Double.parseDouble(
					("" + orderTable.getValueAt(i, orderTable.getColumnIndex("UNIT_PRICE"))).length() == 0 ? "0"
							: "" + orderTable.getValueAt(i, orderTable.getColumnIndex("UNIT_PRICE")));// ��4��ΪorderTable.getColumnIndex("UNIT_PRICE")
																										// modify by
																										// huangjw
																										// 20150508

			orderNumStr = (String) ("" + orderTable.getValueAt(i, orderTable.getColumnIndex("ORDER_NUM")));
			if ("".equals(orderNumStr)) {
				continue;
			} else {
				orderNum = Integer.parseInt(orderNumStr);
				totalRetailPrice = totalRetailPrice + retailPrice;
				totalUnitPrice = totalUnitPrice + (unitPrice * orderNum);
			}
		}
		this.setValue("TOTAL_RETAIL_PRICE", totalRetailPrice + v);

		switch (p) {
		case 0:
			int selectRow = table.getSelectedRow();
			if (selectRow != -1) {
				table.setValueAt(this.getValue("TOTAL_RETAIL_PRICE"), selectRow, 7);
				table.setValueAt(totalUnitPrice, selectRow, 6);
				break;
			}
		}
		/* ����ϸ���е����ۼ۸��ܶ� add by lich 20141020------end */
	}

	/**
	 * ����ʱ�̱��е����ۼ۸��ܶ� add by lich 20141029
	 */
	public void setTotRetailPrice() {

		/* ����ʱ�̱��е����ۼ۸��ܶ� add by lich 20141020------start */
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		double totalSectionPrice = 0.0;
		double sectionPrice = 0.0;
		for (int i = 0; i < table.getRowCount(); i++) {
			sectionPrice = Double
					.parseDouble(("" + table.getValueAt(i, 7)).length() == 0 ? "0" : "" + table.getValueAt(i, 7));
			totalSectionPrice += sectionPrice;
		}
		this.setValue("TOTAL_SECTION_PRICE", totalSectionPrice);
		double allFee = this.getValueDouble("ALLFEE");
		if (allFee == 0.0) {
			this.setValue("ALLFEE", totalSectionPrice);
		}
		/* ����ʱ�̱��е����ۼ۸��ܶ� add by lich 20141020------end */
	}

	/**
	 * ʱ�̱�ԭʼ�۸����ԭʼ�ܼ۸� add by lich 20141029
	 */
	public void setTotOriginalPrice() {
		double originalPrice = 0.0;
		double sectionPrice = 0.0;
		double totalOriginalPrice = 0.0;
		double totalSectionPrice = 0.0;
		for (int i = 0; i < table.getRowCount(); i++) {
			originalPrice = Double
					.parseDouble(("" + table.getValueAt(i, 6)).length() == 0 ? "0" : "" + table.getValueAt(i, 6));
			sectionPrice = Double
					.parseDouble(("" + table.getValueAt(i, table.getColumnIndex("SECTION_PRICE"))).length() == 0 ? "0"
							: "" + table.getValueAt(i, table.getColumnIndex("SECTION_PRICE")));
			totalOriginalPrice += originalPrice;
			totalSectionPrice += sectionPrice;
		}
		this.setValue("OWN_PRICE", totalOriginalPrice);
		this.setValue("ALLFEE", totalSectionPrice);
	}

	/**
	 * ҽ����ԭʼ�۸����ԭʼ�ܼ۸� add by lich 20141029
	 */
	public void setTotDetailOriginalPrice() {
		double originalPrice = 0.0;
		double totalOriginalPrice = 0.0;
		double num = 0.0;
		for (int i = 0; i < orderTable.getRowCount(); i++) {
			originalPrice = Double.parseDouble(
					("" + orderTable.getValueAt(i, orderTable.getColumnIndex("UNIT_PRICE"))).length() == 0 ? "0"
							: "" + orderTable.getValueAt(i, orderTable.getColumnIndex("UNIT_PRICE")));// ��4��ΪorderTable.getColumnIndex("UNIT_PRICE")
																										// modify by
																										// huangjw
																										// 20150508
			num = Double.parseDouble(
					("" + orderTable.getValueAt(i, orderTable.getColumnIndex("ORDER_NUM"))).length() == 0 ? "0"
							: "" + orderTable.getValueAt(i, orderTable.getColumnIndex("ORDER_NUM")));// ��4��ΪorderTable.getColumnIndex("ORDER_NUM")
																										// modify by
																										// huangjw
																										// 20151012
			totalOriginalPrice += (originalPrice * num);
		}
		if (totalOriginalPrice == 0) {
			this.setValue("ORIGINAL_TOTAL_RETAIL_PRICE", 0);
		}
		this.setValue("ORIGINAL_TOTAL_RETAIL_PRICE", totalOriginalPrice);
	}

	/**
	 * ͨ���ı�ʱ�̱���ۿ��ʼ������ۼ۸�add by lich 20141020
	 */
	public void updateTableData(TTableNode tNode) throws Exception {
		DecimalFormat df = new DecimalFormat("########0.00");
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		int selectedRow = tNode.getRow();
		double temp = Double.parseDouble(
				("" + table.getValueAt(selectedRow, 6)).length() == 0 ? "0" : "" + table.getValueAt(selectedRow, 6));
		double rate = Double.parseDouble(("" + tNode.getValue()).length() == 0 ? "0" : "" + tNode.getValue());
		if (rate > 1.0) {
			messageBox("�ۿ��ʲ��ܴ���1");
			tNode.setValue(1.0);
			rate = 1.0;

		}
		if (rateOld != rate) {
			updateTableData("TABLE", selectedRow, 7, df.format(temp * Double.parseDouble(df.format(rate))));// �������ݱ�������ͬ���Parm
			rateOld = rate;
			getMessageBox(tNode);
		}
		setTotOriginalPrice();// 20141107//����ʱ�̱��е����ۼ۸��ܶ�
		selectCheckBox();// add 20141107
		selectSaleCheckBox();// add by lich 20141107
	}

	/**
	 * ͨ���ı�ҽ������ۿ��ʼ������ۼ۸�add by lich 20141020
	 */
	public void updateOrderTableData(TTableNode tNode) throws Exception {
		// DecimalFormat df = new DecimalFormat("########0.00");
		TTable table = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		TParm orderTableparm = table.getParmValue();
		int selectedRow = tNode.getRow();
		rateOldOrder = orderTableparm.getDouble("DISCOUNT_RATE", selectedRow);
		/*
		 * double temp = Double.parseDouble(("" + table.getValueAt(selectedRow,
		 * table.getColumnIndex("UNIT_PRICE"))).length() == 0 ? "0" :
		 * ""//��4��Ϊtable.getColumnIndex("UNIT_PRICE") modify by huangjw 20150508 +
		 * table.getValueAt(selectedRow,
		 * table.getColumnIndex("UNIT_PRICE")));//��4��Ϊtable.getColumnIndex("UNIT_PRICE")
		 * modify by huangjw 20150508 double num = Double.parseDouble(("" +
		 * table.getValueAt(selectedRow, 3)).length() == 0 ? "0" :"" +
		 * table.getValueAt(selectedRow, 3));
		 */
		double rate = Double.parseDouble(("" + tNode.getValue()).length() == 0 ? "0" : "" + tNode.getValue());

		if (rate > 1.0) {
			messageBox("�ۿ��ʲ��ܴ���1");
			tNode.setValue(1.0);
			rate = 1.0;

		}

		setTotDetailRetailPrice(1, 0);// ����ϸ���ܺ�
		setTotDetailOriginalPrice();
		setTotOriginalPrice();// 20141107
		selectCheckBox();// add 20141107
		selectSaleCheckBox();// add by lich 20141107

	}

	/**
	 * ͨ���ı�ҽ����������������ۼ۸�add by lich 20141127
	 */
	public void updateOrderTableDataSalePriceByNum(TTableNode tNode) throws Exception {
		DecimalFormat df = new DecimalFormat("########0.00");
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		TParm orderTableparm = orderTable.getParmValue();
		orderTable.acceptText();
		int selectedRow = tNode.getRow();
		reatialPrice = orderTableparm.getDouble("REATIAL_PRICE", selectedRow);

		int num = Integer.parseInt(("" + tNode.getValue()).length() == 0 ? "0" : "" + tNode.getValue());

		double unitPrice = Double.parseDouble(
				("" + orderTable.getValueAt(selectedRow, orderTable.getColumnIndex("UNIT_PRICE"))).length() == 0 ? "0"
						: "" + orderTable.getValueAt(selectedRow, orderTable.getColumnIndex("UNIT_PRICE")));// ��4��ΪorderTable.getColumnIndex("UNIT_PRICE")
																											// modify by
																											// huangjw
																											// 20150508

		double rate = Double.parseDouble(
				("" + orderTable.getValueAt(selectedRow, orderTable.getColumnIndex("DISCOUNT_RATE"))).length() == 0
						? "0"
						: "" + orderTable.getValueAt(selectedRow, orderTable.getColumnIndex("DISCOUNT_RATE")));// ��7��Ϊtable.getColumnIndex("DISCOUNT_RATE")
																												// modify
																												// by
																												// huangjw
																												// 20150508
		if (!tNode.getValue().toString().equals(tNode.getOldValue().toString())) {// �������ʱ�����������䣬�����۶��۾Ͳ���modify by
																					// huangjw 20150427
			orderTable.setItem(selectedRow, orderTable.getColumnIndex("RETAIL_PRICE"),
					df.format((num * unitPrice * rate)));// ��6��Ϊtable.getColumnIndex("RETAIL_PRICE") modify by huangjw
															// 20150508
		}
		setTotDetailRetailPrice(1, Double.valueOf("" + tNode.getValue()));// ����ϸ���ܺ�
		setTotDetailOriginalPrice();// 20141127
		setTotOriginalPrice();// 20141127
		selectCheckBox();// add 20141127
		selectSaleCheckBox();// add by lich 20141127
	}

	/**
	 * ͨ���ı�ҽ��������ۼ۸�����ۿ���add by lich 20141020
	 */
	public void updateOrderTableDataSalePrice(TTableNode tNode) throws Exception {
		// DecimalFormat df = new DecimalFormat("########0.00");
		TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
		TParm orderTableparm = orderTable.getParmValue();
		orderTable.acceptText();
		int selectedRow = tNode.getRow();// 20
		reatialPrice = orderTableparm.getDouble("REATIAL_PRICE", selectedRow);

		double num = Double.parseDouble(("" + orderTable.getValueAt(selectedRow, 3)).length() == 0 ? "0"
				: "" + orderTable.getValueAt(selectedRow, 3));// by lich 20141127

		double unitPrice = Double.parseDouble(
				("" + orderTable.getValueAt(selectedRow, orderTable.getColumnIndex("UNIT_PRICE"))).length() == 0 ? "0"
						: "" + orderTable.getValueAt(selectedRow, orderTable.getColumnIndex("UNIT_PRICE")));// ��4��ΪorderTable.getColumnIndex("UNIT_PRICE")
																											// modify by
																											// huangjw
																											// 20150508

		double retailPrice = Double.parseDouble(("" + tNode.getValue()).length() == 0 ? "0" : "" + tNode.getValue());
		orderTable.setItem(selectedRow, orderTable.getColumnIndex("DISCOUNT_RATE"), (retailPrice / unitPrice) / num);// ��7��ΪorderTable.getColumnIndex("DISCOUNT_RATE")
																														// modify
																														// by
																														// huangjw
																														// 20150508
		setTotDetailRetailPrice(1, Double.valueOf("" + tNode.getValue()));// ����ϸ���ܺ�
		setTotDetailOriginalPrice();// 20141107
		setTotOriginalPrice();// 20141107
		selectCheckBox();// add 20141107
		selectSaleCheckBox();// add by lich 20141107
	}

	//
	/**
	 * �������ݱ�������ͬ���Parm
	 * 
	 * @param tableTag
	 *            String
	 * @param row
	 *            int
	 * @param column
	 *            int
	 * @param obj
	 *            Object
	 */
	public void updateTableData(String tableTag, int row, int column, Object obj) throws Exception {
		TTable table = ((TTable) getComponent(tableTag));
		table.setValueAt(obj, row, column);
		table.getParmValue().setData(getFactColumnName(tableTag, column), row, obj);
		table.getCellEditor(column).stopCellEditing();
	}

	public String getFactColumnName(String tableTag, int column) {
		int col = getThisColumnIndex(column);
		return getTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * �õ����������
	 * 
	 * @param column
	 * @return int
	 */
	public int getThisColumnIndex(int column) {
		return getTable("ORDER_TABLE").getColumnModel().getColumnIndex(column);
	}

	/**
	 * ��ѯ�ײͱ�MEM_PACKAGE���ײͼ۸�� MEM_PACKAGE_PRICE ��ѯ�����Ϣ
	 */
	/*
	 * ���ײͼ۸�� MEM_PACKAGE_PRICE �в�ѯ�ײͼ۸񡢺��ۿ��� add by lich 20141029 ----------start
	 */
	public void getPackageMsg(String packageCode) {

		String packagePriceSql = "SELECT B.PACKAGE_PRICE,A.START_DATE, A.END_DATE,B.DISCOUNT_RATE, "
				+ " A.CTZ_CODE,A.ADM_TYPE, " + " A.ORIGINAL_PRICE "
				+ " FROM MEM_PACKAGE A, (SELECT  PACKAGE_PRICE, PRICE_TYPE, PACKAGE_CODE,  DISCOUNT_RATE"
				+ " FROM MEM_PACKAGE_PRICE" + " WHERE PRICE_TYPE = '" + this.getValueString("PRICE_TYPE") + "') B"
				+ " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE(+) " + " AND A.PACKAGE_CODE = '" + packageCode + "' ";
		TParm packageResult = new TParm(TJDODBTool.getInstance().select(packagePriceSql));
		int count = packageResult.getCount();
		if (count > 0) {
			this.setValue("ALLFEE", packageResult.getValue("PACKAGE_PRICE", 0));
			this.setValue("OWN_PRICE", packageResult.getValue("ORIGINAL_PRICE", 0));
			this.setValue("RATE", packageResult.getValue("DISCOUNT_RATE", 0));
			this.setValue("CTZ_CODE", packageResult.getValue("CTZ_CODE", 0));
			this.setValue("ADM_TYPE", packageResult.getValue("ADM_TYPE", 0));
		}
		String discountRate = "";
		discountRate = packageResult.getValue("DISCOUNT_RATE", 0);
		if ("".equals(discountRate)) {
			this.setValue("RATE", 1.0);
		}
	}

	/*
	 * ���ײͼ۸�� MEM_PACKAGE_PRICE �в�ѯ�ײͼ۸񡢺��ۿ��� add by lich 20141029 ----------end
	 */
	// ��ѯ add by lich 20141030
	public void onQuery() {
		if (isLeaf(packageCode)) {
			getPackageMsg(packageCode);
			showSectionDetail(packageCode);// ʱ����ϸ
			setTotOriginalPrice();// 20141107
			selectCheckBox();// 20141107
			selectSaleCheckBox();// add by lich 20141107
		}
	}

	public void updataPrice() {
		String priceType = getValueString("PRICE_TYPE");
		String rate = getValueString("RATE");
		if (Double.valueOf(rate) == 0) {
			rate = "1";
		}
		String sql;
		TParm result;
		sql = " UPDATE MEM_PACKAGE_SECTION_D_PRICE" + " SET RETAIL_PRICE = ORIGINAL_PRICE * " + rate
				+ ", DISCOUNT_RATE = " + rate + " WHERE PACKAGE_CODE = '" + packageCode + "'" + " AND PRICE_TYPE = '"
				+ priceType + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));

	}

	public void changePriceType() {
		onQuery();
	}

	public String getDetailOrderDataSql(TParm parm) {
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC,B.DOSAGE_QTY ORDER_NUM,A.SPECIFICATION, DOSAGE_QTY,"
				+ " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY"
				+ " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE " + " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' "
				+ " AND B.ORDERSET_CODE = '" + parm.getValue("ORDER_CODE") + "'";
		return sql;

	}

	/**
	 * ȫѡ��ť add by lich 20141107
	 */
	public void allSelect() {
		int count = table.getRowCount();
		for (int i = 0; i < count; i++) {
			if ("Y".equals(this.getValue("ALL_SELECT"))) {
				table.setItem(i, "FLG", "Y");
				selectCheckBox();// add by lich 20141107
				selectSaleCheckBox();// add by lich 20141107
			} else if ("N".equals(this.getValue("ALL_SELECT"))) {
				table.setItem(i, "FLG", "N");
				selectCheckBox();// add by lich 20141107
				selectSaleCheckBox();// add by lich 20141107
			}
		}
	}

	/**
	 * ʱ�̱�ѡ��ť����ԭʼ�۸�
	 */
	public void selectCheckBox() {
		int count = table.getRowCount();
		double originalPrice = 0.0;
		double totalOriginalPrice = 0.0;

		for (int i = 0; i < count; i++) {
			if ("Y".equals(table.getItemString(i, "FLG"))) {
				originalPrice = table.getItemDouble(i, "ORIGINAL_PRICE");
				totalOriginalPrice += originalPrice;

			}
		}

		this.setValue("ORIGINAL_TOTAL_SECTION_PRICE", totalOriginalPrice);
	}

	/**
	 * ʱ�̱�ѡ��ť�������ۼ۸� add by lich
	 */
	public void selectSaleCheckBox() {
		int count = table.getRowCount();
		double totalSectionPrice = 0.0;
		double sectionPrice = 0.0;
		for (int i = 0; i < count; i++) {
			if ("Y".equals(table.getItemString(i, "FLG"))) {
				sectionPrice = Double
						.parseDouble(("" + table.getValueAt(i, 7)).length() == 0 ? "0" : "" + table.getValueAt(i, 7));
				totalSectionPrice += sectionPrice;

			}
		}

		this.setValue("TOTAL_SECTION_PRICE", totalSectionPrice);
	}

	/**
	 * �����޸�ʱ����ʾ��Ϣ
	 */
	public void getMessageBox(TTableNode node) {
		if (JOptionPane.showConfirmDialog(null, "�Ƿ񱣴��޸���Ϣ��", "��Ϣ", JOptionPane.YES_NO_OPTION) == 0) {
			node.getTable().getCellEditor(node.getColumn()).stopCellEditing();

			tNode = node;
			table.setItem(tNode.getRow(), tNode.getColumn(), tNode.getValue());

			onSaveFlg(false);
		} else {
			node.setValue(node.getOldValue());
			node.getTable().getCellEditor(node.getColumn()).stopCellEditing();
			// onTableChangeValue(node);
			onMainTableClick();
		}

	}

	/**
	 * �����޸�ҽ����ʾ��Ϣ
	 */
	public void getMessageBoxOrder(TTableNode node) {
		if (JOptionPane.showConfirmDialog(null, "�Ƿ񱣴��޸���Ϣ��", "��Ϣ", JOptionPane.YES_NO_OPTION) == 0) {
			onSaveFlg(false);
		} else {

		}

	}

	private void stopAllTableEditing() {
		stopTableEditing(table);
		stopTableEditing(orderTable);
	}

	private void stopTableEditing(TTable t) {
		int cc = t.getColumnCount();
		TTableCellEditor cellEditor;
		for (int i = 0; i < cc; i++) {
			cellEditor = t.getCellEditor(i);
			if (cellEditor.isEdit()) {
				cellEditor.stopCellEditing();
			}
		}
	}

	public void onMainClick2(int selectedIndx) {

		if (idOrderChanged) {
			idOrderChanged = false;
		}

		try {

			// ��ɾ��orderDelParm������
			orderDelParm.removeData("SEQ");
			orderDelParm.removeData("SECTION_CODE");
			orderDelParm.removeData("PACKAGE_CODE");
			// ���sectionCode
			TTable table = (TTable) callFunction("UI|TABLE|getThis");
			TTable orderTable = (TTable) callFunction("UI|ORDER_TABLE|getThis");
			/* modify by zhp 20141020 ----------start */
			TParm nullParm = new TParm();
			orderTable.setParmValue(nullParm);
			/* modify by zhp 20141020 ----------end */
			if (selectedIndx < 0) {
				return;
			}
			TParm tableparm = table.getParmValue();
			sectionDesc = tableparm.getValue("SECTION_DESC", selectedIndx);// ʱ��������ֵ
			if (!"".equals(tableparm.getValue("SECTION_CODE", selectedIndx))) {
				sectionCode = tableparm.getValue("SECTION_CODE", selectedIndx);
			}
			if (!"".equals(tableparm.getValue("PACKAGE_CODE", selectedIndx))) {
				packageCode = tableparm.getValue("PACKAGE_CODE", selectedIndx);
			}

			rateOld = tableparm.getDouble("DISCOUNT_RATE", selectedIndx);

			String orderSql = "SELECT 'N' AS SELECTD,'N' AS FLG, 'Y' AS EXEC, A.ID, A.SEQ, A.SECTION_DESC, A.ORDER_CODE,"
					+ " A.ORDER_DESC, A.TRADE_ENG_DESC, A.ORDER_NUM, A.UNIT_CODE,"
					+ " A.UNIT_PRICE, B.RETAIL_PRICE, A.DESCRIPTION, A.OPT_DATE, A.OPT_USER,"
					+ " A.OPT_TERM, B.DISCOUNT_RATE, A.SECTION_CODE, A.PACKAGE_CODE,"
					+ " A.SETMAIN_FLG, A.ORDERSET_CODE, A.ORDERSET_GROUP_NO, A.HIDE_FLG,"
					+ " B.PRICE_TYPE,A.MEDI_UNIT,A.ROUTE_CODE,A.FREQ_CODE,A.UN_NUM_FLG "
					+ " FROM MEM_PACKAGE_SECTION_D A,"
					+ " (SELECT ID, PRICE_TYPE, PACKAGE_CODE, SECTION_CODE, DISCOUNT_RATE, RETAIL_PRICE"
					+ " FROM MEM_PACKAGE_SECTION_D_PRICE" + " WHERE PRICE_TYPE = '" + this.getValue("PRICE_TYPE")
					+ "') b" + " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE(+)" + " AND A.SECTION_CODE = B.SECTION_CODE(+)"
					+ " AND A.ID = B.ID(+)" + " AND A.PACKAGE_CODE = '" + packageCode + "'" + " AND A.SECTION_CODE = '"
					+ sectionCode + "'" + " AND A.HIDE_FLG = 'N'" + " ORDER BY SEQ";

			TParm result = new TParm(TJDODBTool.getInstance().select(orderSql));

			// ��ѯ������
			TParm seqParm = new TParm(TJDODBTool.getInstance()
					.select("SELECT MAX(SEQ) AS SEQ " + " FROM MEM_PACKAGE_SECTION_D WHERE SECTION_CODE='" + sectionCode
							+ "' " + " AND PACKAGE_CODE = '" + packageCode + "'"));
			String seq = seqParm.getValue("SEQ", 0).toString() == null ? "0" : seqParm.getValue("SEQ", 0).toString();
			orderSeqNo = Integer.parseInt(seq) + 1;

			// ��ȡ���
			ordersetGroupNo = getMaxSeq("ORDERSET_GROUP_NO", "MEM_PACKAGE_SECTION_D", "SECTION_CODE", sectionCode,
					"PACKAGE_CODE", packageCode);

			if (result.getCount() > 0) {
				orderTable.setParmValue(result);
			} else {
				orderTable.setParmValue(result);
			}
			insertRow("ORDER_TABLE");

			// Modified by lx 2014/11/18
			setTotDetailRetailPrice(0, 0);// ����ϸ���ܺ�
			setTotDetailOriginalPrice();
			setTotOriginalPrice();// 20141107
			selectCheckBox();// 20141107
			selectSaleCheckBox();// add by lich 20141107
			isRateChanged = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		onSaveFlg(false);
		orderTable.setLockRows(""); // add by huangtt 20160808
	}

	/**
	 * ȫѡҽ����
	 */
	public void selectOrderTable() {
		if (((TCheckBox) this.getComponent("ALL")).isSelected()) {
			for (int i = 0; i < orderTable.getParmValue().getCount() - 1; i++) {
				orderTable.setItem(i, "SELECTD", "Y");
			}
		} else {
			for (int i = 0; i < orderTable.getParmValue().getCount() - 1; i++) {
				orderTable.setItem(i, "SELECTD", "N");
			}
		}
	}

	/**
	 * �����ײ��ۿ�
	 */
	public void onFeeRate() {
		table.acceptText();
		TParm tableParm = table.getParmValue();
		String sectionCode = "";
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("FLG", i).equals("Y")) {
				sectionCode += "'" + tableParm.getValue("SECTION_CODE", i) + "',";
			}
		}
		if (sectionCode.length() <= 0) {
			this.messageBox("��ѡ����Ҫ������ʱ��");
			return;
		}
		sectionCode = sectionCode.substring(0, sectionCode.lastIndexOf(","));
		TParm result = BILLumpWorkTool.getInstance().onLumpWorkRateFee(this.getValueString("PACKAGE_CODE"),
				this.getValueString("PRICE_TYPE"), sectionCode);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.messageBox("���ײ͵��ۿ���Ϊ:" + result.getValue("RATE"));
	}
	
	/**
	 * ���������ײ��ۿ�
	 */
	public void onFeeRateMul() {
		table.acceptText();
		TParm tableParm = table.getParmValue();
		String sectionCode = "";
		for (int i = 0; i < tableParm.getCount(); i++) {
			sectionCode += "'" + tableParm.getValue("SECTION_CODE", i) + "',";
		}
		sectionCode = sectionCode.substring(0, sectionCode.lastIndexOf(","));
		//
		String sql = " SELECT ID, CHN_DESC NAME" + " FROM SYS_DICTIONARY" + " WHERE GROUP_ID = 'MEM_PACKAGE_TYPE'"
		//
				+ " AND ACTIVE_FLG = 'Y' ORDER BY SEQ";
		//
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//
		TParm result;
		String message = "";
		for (int i = 0; i < parm.getCount(); i++) {
			result = BILLumpWorkTool.getInstance().onLumpWorkRateFee(this.getValueString("PACKAGE_CODE"),
					parm.getValue("ID", i), sectionCode);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return;
			}
			message += (parm.getValue("NAME", i) + "��" + result.getValue("RATE") + "\n\r");
		}
		//
		this.messageBox(message);
	}
	

}
