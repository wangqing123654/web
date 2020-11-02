package com.javahis.ui.sys;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.javahis.manager.sysfee.sysOdrsetDtlObserver;
import java.awt.Component;
import java.util.Vector;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTableNode;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.SYSHzpyTool;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;
import jdo.sys.SystemTool;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title:�������ֵ䵵
 * </p>
 * 
 * 
 * <p>
 * Description:
 * </p>
 * 
 * 
 * <p>
 * Copyright: JAVAHIS 1.0 (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 */
public class SYSFee_ExmControl extends TControl {

	public SYSFee_ExmControl() {
	}

	/**
	 * �����
	 */
	private TDSObject bigObject = new TDSObject();
	/**
	 * ����
	 */
	private TTreeNode treeRoot;
	/**
	 * ��Ź�����𹤾�
	 */
	private SYSRuleTool ruleTool;
	/**
	 * �������ݷ���datastore���ڶ��������ݹ���
	 */
	private TDataStore treeDataStore = new TDataStore();
	/**
	 * ���ǰѡ�е��к�
	 */
	int selRow = -1;
	/**
	 * ��ǰѡ�е���Ŀ����(SYS_FEE_HISTORY�У�ORDER_CODE/SYS_ORDERSETDETAI�У�ORDERSET_CODE)
	 */
	String orderCode = "";
	/**
	 * SYS_FEE_HISTORY���TDS
	 */
	TDS sysFeeHisDS = new TDS();
	/**
	 * SYS_FEE���TDS
	 */
	TDS sysFeeDS = new TDS();
	/**
	 * SYS_ORDERSETDETAI���TDS
	 */
	TDS sysOrdSetDtlDS = new TDS();
	/**
	 * SYS_ORDEROPTITEM���TDS
	 */
	TDS eXMItem = new TDS();
	// --------------------
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	// ����order�õ�����
	public double getPrice(String code) {
		if (dataStore == null)
			return 0.0;
		String bufferString = dataStore.isFilter() ? dataStore.FILTER
				: dataStore.PRIMARY;
		TParm parm = dataStore.getBuffer(bufferString);
		Vector vKey = (Vector) parm.getData("ORDER_CODE");
		Vector vPrice = (Vector) parm.getData("OWN_PRICE");
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (code.equals(vKey.get(i)))
				return TypeTool.getDouble(vPrice.get(i));
		}
		return 0.0;
	}

	// --------------------

	/**
	 * ���пؼ�������
	 */
	private String controlName = "TRADE_ENG_DESC;SUB_SYSTEM_CODE;RPTTYPE_CODE;DEV_CODE;OPTITEM_CODE;MR_CODE;"
			+ "DEGREE_CODE;EXEC_DEPT_CODE;ACTIVE_FLG;CHARGE_HOSP_CODE;ORDER_DESC;PY1;"
			+ "DESCRIPTION;ORDER_CAT1_CODE;TRANS_OUT_FLG;TIME_LIMIT;EXE_PLACE;"// add
																				// yanj
																				// ʱ��
																				// �ص�
																				// 20140319
			+ "OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;IS_REMARK;ORD_SUPERVISION;MEDANT_FLG;ORDER_DEPT_CODE";// add
																														// caoyong
																														// ҩ��ʵ��20130313

	/**
	 * ����Ŀؼ�
	 */
	// ��
	TTree tree;
	// ����
	TTable upTable, downTable;

	// ȫ��/����/ͣ�ñ��
	TRadioButton ALL;
	TRadioButton ACTIVE_Y;
	TRadioButton ACTIVE_N;

	// ��,��,ס,��,��ҽ���ñ��
	TCheckBox OPD_FIT_FLG;
	TCheckBox EMG_FIT_FLG;
	TCheckBox IPD_FIT_FLG;
	TCheckBox HRM_FIT_FLG;

	TTextFormat SUB_SYSTEM_CODE;
	TTextFormat RPTTYPE_CODE;
	TTextFormat DEV_CODE;
	TTextFormat OPTITEM_CODE;
	TTextFormat MR_CODE;
	TTextFormat DEGREE_CODE;
	TTextFormat CHARGE_HOSP_CODE;
	TTextFormat EXEC_DEPT_CODE;
	TTextFormat ORDER_CAT1_CODE;
	TTextFormat ORDER_DEPT_CODE;

	// ����ע��
	TCheckBox ACTIVE_FLG;
	// ҩ��ʵ��
	TCheckBox MEDANT_FLG;

	TTextField QUERY;
	// fux modify 20141020
	TTextField QUERY_N;
	TTextField ORDER_CODE;
	TTextField ORDER_DESC;
	TTextField TRADE_ENG_DESC;
	TTextField PY1;
	TTextField DESCRIPTION;
	// ʱ�� ��ִ�еص� yanjing 20140319
	TTextField TIME_LIMIT;
	TTextFormat EXE_PLACE;

	// ת��ע��
	TCheckBox TRANS_OUT_FLG;
	TComboBox TRANS_HOSP_CODE;

	TNumberTextField TOT_FEE;

	// ����ҽ��ע��(�ڽ���������)
	TCheckBox ORDERSET_FLG;
	// ����ʱ��(��ʱ��Ч--��ǰʱ��)
	String START_DATE;
	// ͣ��ʱ��(99991231235959)
	String END_DATE;
	// ҽ���������
	TTextFormat ORD_SUPERVISION;

	public void onInit() { // ��ʼ������
		super.onInit();
		myInitControler();

		// ��ʼ����
		onInitTree();
		// ��tree��Ӽ����¼�
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		// ��ʼ�����
		onInitNode();
		callFunction("UI|downTable|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
		canSave();
		// ========pangben modify 20110427 start Ȩ�����
		// ��ʼ��Ժ��
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110427 stop

	}

	private void canSave() {
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj.equals("QUERY"))
			callFunction("UI|save|setEnabled", false);

		if (obj.equals("QUERY_N"))
			callFunction("UI|save|setEnabled", false);
	}

	/**
	 * ���(TABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 */
	public void onTableCheckBoxClicked(Object obj) {
		TTable table = (TTable) obj;
		table.removeRow(table.getSelectedRow());
	}

	/**
	 * ��ʼ����
	 */
	public void onInitTree() {
		// �õ�����
		treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
		if (treeRoot == null)
			return;
		// �����ڵ����������ʾ
		treeRoot.setText("���������");
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
	 * ��ʼ�����Ľ��
	 */

	public void onInitNode() {
		// ��dataStore��ֵ
		treeDataStore
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
		// �����dataStore���õ�������С��0
		if (treeDataStore.retrieve() <= 0)
			return;
		// ��������,�Ǳ�������еĿ�������
		ruleTool = new SYSRuleTool("EXM_RULE");
		if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
			TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
					"CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
			// ѭ����������ڵ�
			for (int i = 0; i < node.length; i++)
				treeRoot.addSeq(node[i]);
		}
		// �õ������ϵ�������
		TTree tree = (TTree) callMessage("UI|TREE|getThis");
		// ������
		tree.update();
		// ��������Ĭ��ѡ�нڵ�
		tree.setSelectNode(treeRoot);
	}

	/**
	 * ������
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) {

		if (ACTIVE_Y.isSelected()) {
			QUERY.setVisible(true);
			QUERY_N.setVisible(false);
		} else if (ACTIVE_N.isSelected()) {
			QUERY.setVisible(false);
			QUERY_N.setVisible(true);
		} else if (ALL.isSelected()) {
			QUERY.setVisible(true);
			QUERY_N.setVisible(false);
		}
		// ���
		onClear();

		// ������ť������
		callFunction("UI|new|setEnabled", false);
		// �õ�������Ľڵ����
		// TTreeNode node = (TTreeNode) parm;
		TTreeNode node = tree.getSelectNode();
		// TTreeNode node=tree.getSelectionNode();
		if (node == null)
			return;
		// �õ�table����
		TTable table = (TTable) this.callFunction("UI|upTable|getThis");
		// table�������иı�ֵ
		table.acceptText();
		// �жϵ�����Ƿ������ĸ����
		if (node.getType().equals("Root")) {
			// ��������ĸ��ӵ�table�ϲ���ʾ����
			table.removeRowAll();
		} else { // �����Ĳ��Ǹ����
			// �õ���ǰѡ�еĽڵ��idֵ
			String id = node.getID();
			// �õ���ѯTDS��SQL��䣨ͨ����һ���IDȥlike���е�orderCode��
			// ======pangben modify 20110427 start ����������
			String sql = getSQL(id, Operator.getRegion());
			// ======pangben modify 20110427 stop
			// ��ʼ��table��TDS
			initTblAndTDS(sql);

		}
		// ��table���ݼ���������
		// table.setSort("ORDER_CODE");
		// table��������¸�ֵ
		// table.sort();
		// �õ���ǰ�������ID
		String nowID = node.getID();

		int classify = 1;
		if (nowID.length() > 0)
			classify = ruleTool.getNumberClass(nowID) + 1;
		// �������С�ڵ�,��������һ��(ʹ������ť����)
		if (classify > ruleTool.getClassifyCurrent()) {
			this.callFunction("UI|new|setEnabled", true);
		}
		// �����TABLE��������ܷ��á�
		updateMastOwnPrice();
	}

	/**
	 * �õ���ʼ��TDS��SQL���(����Ŀǰ�������õ���Ŀ�б�����START_DATE/END_DATE ֻ��ACTIVE_FLGΪ��Y��)
	 * 
	 * @return String ============pangben modify 20110427 ����������
	 */
	private String getSQL(String orderCode, String regionCode) {
		String active = "";

		if (ACTIVE_Y.isSelected()) { // ����
			active = " AND ACTIVE_FLG='Y'";
			setEnabledForCtl(true);
		} else if (ACTIVE_N.isSelected()) { // ͣ��
			active = " AND ACTIVE_FLG='N'";
			setEnabledForCtl(false);
		} else { // ȫ��
			setEnabledForCtl(false);
		}
		// =========pangben modify 20110427 start ����������
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		// =========pangben modify 20110427 stop
		String sql = "";
		// ���������
		if (orderCode != null && orderCode.length() > 0)
			sql = " SELECT * " + " FROM SYS_FEE_HISTORY" + " WHERE "
					+ " ORDER_CODE like '" + orderCode + "%'" + active + region
					// //fux modify 20141020 ֻ��ѯ������
					// + "AND ORDERSET_FLG = 'Y'  "
					+ " ORDER BY ORDER_CODE";
		System.out.println("-------------" + sql);
		return sql;
	}

	/**
	 * ��ʼ������ı�������еĿؼ�����
	 * 
	 * @param sql
	 *            String
	 */
	public void initTblAndTDS(String sql) {
		sysFeeHisDS.setSQL(sql);
		sysFeeHisDS.retrieve();
		// ���û��������ձ���ϵ�����
		if (sysFeeHisDS.rowCount() <= 0) {
			upTable.removeRowAll();
		}
		upTable.setDataStore(sysFeeHisDS);
		upTable.setDSValue();

	}

	/**
	 * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼� ����
	 */
	public void myInitControler() {

		tree = (TTree) callFunction("UI|TREE|getThis");

		// �õ�table�ؼ�
		upTable = (TTable) this.getComponent("upTable");
		downTable = (TTable) this.getComponent("downTable");

		ALL = (TRadioButton) this.getComponent("ALL");
		ACTIVE_Y = (TRadioButton) this.getComponent("ACTIVE_Y");
		ACTIVE_N = (TRadioButton) this.getComponent("ACTIVE_N");

		SUB_SYSTEM_CODE = (TTextFormat) this.getComponent("SUB_SYSTEM_CODE");
		RPTTYPE_CODE = (TTextFormat) this.getComponent("RPTTYPE_CODE");
		DEV_CODE = (TTextFormat) this.getComponent("DEV_CODE");
		OPTITEM_CODE = (TTextFormat) this.getComponent("OPTITEM_CODE");
		MR_CODE = (TTextFormat) this.getComponent("MR_CODE");
		DEGREE_CODE = (TTextFormat) this.getComponent("DEGREE_CODE");

		ACTIVE_FLG = (TCheckBox) this.getComponent("ACTIVE_FLG");
		MEDANT_FLG = (TCheckBox) this.getComponent("MEDANT_FLG");// add caoyong
																	// 20140313
		QUERY = (TTextField) this.getComponent("QUERY");
		QUERY_N = (TTextField) this.getComponent("QUERY_N");
		ORDER_CODE = (TTextField) this.getComponent("ORDER_CODE");
		ORDER_DESC = (TTextField) this.getComponent("ORDER_DESC");
		TRADE_ENG_DESC = (TTextField) this.getComponent("TRADE_ENG_DESC");
		PY1 = (TTextField) this.getComponent("PY1");
		DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
		TIME_LIMIT = (TTextField) this.getComponent("TIME_LIMIT");// add yanjing
																	// 20140319
		EXE_PLACE = (TTextFormat) this.getComponent("EXE_PLACE");// add yanjing
																	// 20140319
		TRANS_OUT_FLG = (TCheckBox) this.getComponent("TRANS_OUT_FLG");
		TRANS_HOSP_CODE = (TComboBox) this.getComponent("TRANS_HOSP_CODE");
		CHARGE_HOSP_CODE = (TTextFormat) this.getComponent("CHARGE_HOSP_CODE");
		EXEC_DEPT_CODE = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");
		ORDER_CAT1_CODE = (TTextFormat) this.getComponent("ORDER_CAT1_CODE");
		ORDER_DEPT_CODE = (TTextFormat) this.getComponent("ORDER_DEPT_CODE");

		ORD_SUPERVISION = (TTextFormat) this.getComponent("ORD_SUPERVISION");
		// �ܷ���
		TOT_FEE = (TNumberTextField) this.getComponent("TOT_FEE");

		OPD_FIT_FLG = (TCheckBox) this.getComponent("OPD_FIT_FLG");
		EMG_FIT_FLG = (TCheckBox) this.getComponent("EMG_FIT_FLG");
		IPD_FIT_FLG = (TCheckBox) this.getComponent("IPD_FIT_FLG");
		HRM_FIT_FLG = (TCheckBox) this.getComponent("HRM_FIT_FLG");

		// ����tableע�ᵥ���¼�����
		this.callFunction("UI|upTable|addEventListener", "upTable->"
				+ TTableEvent.CLICKED, this, "onUpTableClicked");
		// ����tableע������¼�
		downTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateSYSFEE");
		downTable.addEventListener(downTable.getTag() + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onChangeFee");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "");
		// LIS RIS/ORDERSET_FLG='Y'
		parm.setData("RX_TYPE", 5);
		// ���õ����˵�
		QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// ���õ����˵�
		QUERY_N.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeeNPopup.x"), parm);
		// ������ܷ���ֵ����
		QUERY_N.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturnN");

		// ������ť������
		callFunction("UI|new|setEnabled", false);
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			ORDER_CODE.setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			ORDER_DESC.setValue(order_desc);
		// ��ղ�ѯ�ؼ�
		QUERY.setValue("");
		onQuery();
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturnN(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			ORDER_CODE.setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			ORDER_DESC.setValue(order_desc);
		// ��ղ�ѯ�ؼ�
		QUERY_N.setValue("");
		onQuery();
	}

	public void onQuery() {

		String selCode = ORDER_CODE.getValue();
		if ("".equals(selCode)) {
			this.messageBox("������Ҫ��ѯ��Ŀ�ı��룡");
		}

		// �õ���ѯcode��SQL���
		// ========pangben modify 20110427 start
		String sql = getSQL(selCode, this.getValueString("REGION_CODE"));
		// ========pangben modify 20110427 stops
		// ��ʼ��table��TDS
		initTblAndTDS(sql);
		// ����ѯ���ֻ��һ�����ݵ�ʱ��ֱ����ʾ����ϸ��Ϣ
		if (upTable.getRowCount() == 1) {
			onUpTableClicked(0);
		}

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
	public void onCreateSYSFEE(Component com, int row, int column) {
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ��ѯ������6������ϸ��
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 6);
		textFilter.setPopupMenuParameter("IG", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturnSYSFee");
	}

	public void setDownTableAndTDS(String code) {

		downTable.acceptText();
		int selrow = downTable.getSelectedRow();
		downTable.setItem(selrow, "ORDER_CODE", code);
		// ������һ����Ŀ֮��Ĭ��Ϊ1.0
		if (TypeTool.getDouble(downTable.getValueAt_(selrow, 5)) == 0.0) {
			downTable.setItem(selrow, "DOSAGE_QTY", 1.0);
		}

		// ����һ��(�����ǰ�޸ĵ��С�ѡ���������һ�У�������)
		// sysOrdSetDtlDS.insertRow();
		if (downTable.getSelectedRow() + 1 == downTable.getRowCount())
			downTable.addRow();
		// ˢ���ܷ��ÿؼ�
		onBrushTotFeeCtl();

	}

	/**
	 * ���ڳ�ʼ���ܷ��ú�ˢ���ܷ���
	 */
	public void onBrushTotFeeCtl() {
		// �ǵ�table������
		double totFee = 0.0;
		int rows = downTable.getRowCount();
		for (int i = 0; i < rows; i++) {
			totFee += TypeTool.getDouble(downTable.getValueAt(i, 6));
		}

		TOT_FEE.setValue(totFee);

	}

	/**
	 * ��ص�5�У�����ֵ�ĸı�ˢ���ܷ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onChangeFee(TTableNode node) {
		if (node.getColumn() == 5) {
			double ownPrice = TypeTool.getDouble(downTable.getValueAt(node
					.getRow(), 4));
			double oldValue = TypeTool.getDouble(node.getOldValue());
			double newValue = TypeTool.getDouble(node.getValue());
			// ȡ������ֵ�ò�
			double diffValue = newValue - oldValue;
			// �ø���Ŀ����*�������+ԭ�����ܷ���=���ڵ��ܷ���
			double totFee = diffValue * ownPrice
					+ TypeTool.getDouble(TOT_FEE.getValue());
			TOT_FEE.setValue(totFee);
		}
	}

	/**
	 * ���������table�¼�
	 */
	public void onUpTableClicked(int row) {
		// ��ǰѡ�е��к�
		selRow = row;
		// �������ؼ���ֵ
		clearCtl();
		// �õ����parm
		TParm tableDate = ((TDS) upTable.getDataStore()).getBuffer(TDS.PRIMARY)
				.getRow(selRow);
		// �����еĿؼ�ֵ
		// setValueForDownCtl(tableDate, row);
		setValueForDownCtl(tableDate);
		// ������ʾ��ȫ������ʱ��Ĭ�ϲ��ɱ༭(����״̬�ָ��༭/ͣ��״̬���ɱ༭)
		if (ALL.isSelected()) {
			boolean activeFlg = ACTIVE_FLG.isSelected();
			setEnabledForCtl(activeFlg);
		}
		// �õ���ǰ��Ŀ����(SYS_FEE_HISTORY�У�ORDER_CODE/SYS_ORDERSETDETAI�У�ORDERSET_CODE)
		orderCode = ORDER_CODE.getValue();
		// ��ʼ�������table
		initDownTable(orderCode);

		// ˢ���ܷ��ÿؼ������ڳ�ʼ�������table��
		onBrushTotFeeCtl();
		ACTIVE_FLG.setEnabled(true);
	}

	public void initDownTable(String ordersetCode) {
		downTable.acceptText();
		String sqlForDtl = "SELECT * FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE = '"
				+ ordersetCode + "'";
		sysOrdSetDtlDS.setSQL(sqlForDtl);
		sysOrdSetDtlDS.retrieve();
		// ����������
		sysOdrsetDtlObserver obser = new sysOdrsetDtlObserver();
		// �Ѽ��������õ�ĳ��DS��
		sysOrdSetDtlDS.addObserver(obser);

		// ���û��������ձ���ϵ�����
		if (sysOrdSetDtlDS.rowCount() <= 0) {
			downTable.removeRowAll();
		}

		sysOrdSetDtlDS.insertRow();

		downTable.setDataStore(sysOrdSetDtlDS);

		downTable.setDSValue();
	}

	/**
	 * s ��ճ���table����Ŀؼ���ֵ
	 */
	public void clearCtl() {
		this.clearValue(controlName + ";TRANS_HOSP_CODE;ORDER_CODE"
				+ ";TOT_FEE");
		// ========pangben modify 20110427
		this.setValue("REGION_CODE", Operator.getRegion());
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		clearCtl();
		upTable.removeRowAll();
		downTable.removeRowAll();
		setValue("UNIT", "");
		setValue("TUBE_TYPE", "");
	}

	/**
	 * ��������
	 */
	public void onExport() {
		//���������ѯ�����Ľ����������ҳ����ʾ�����е���������ʱ��ֱ������
		String Sql = "SELECT * FROM (SELECT AA.ORDER_CODE ORDERSET_CODE,"
				+ "AA.ORDER_CODE,"
				+ "AA.ORDER_DESC,"
				+ "CC.UNIT_CHN_DESC,"
				+ "AA.OWN_PRICE,"
				+ "AA.SPECIFICATION,"
				+ "BB.DOSAGE_QTY,"
				+ "AA.ORDERSET_FLG"
				+ " FROM SYS_FEE AA, SYS_ORDERSETDETAIL BB,SYS_UNIT CC"
				+ " WHERE     AA.ORDER_CODE = BB.ORDERSET_CODE AND AA.UNIT_CODE = CC.UNIT_CODE"
				+ " AND AA.ORDER_CODE LIKE 'X%'"
				+ " AND ACTIVE_FLG = 'Y'"
				+ " AND (   AA.REGION_CODE = 'H01'"
				+ " OR AA.REGION_CODE IS NULL"
				+ " OR AA.REGION_CODE = '')"
				+ " UNION"
				+ " SELECT AA.ORDERSET_CODE,"
				+ "BB.ORDER_CODE,"
				+ "BB.ORDER_DESC,"
				+ "CC.UNIT_CHN_DESC,"
				+ "BB.OWN_PRICE,"
				+ "BB.SPECIFICATION,"
				+ "AA.DOSAGE_QTY,"
				+ "BB.ORDERSET_FLG "
				+ "FROM SYS_ORDERSETDETAIL AA, SYS_FEE BB,SYS_UNIT CC"
				+ " WHERE     AA.ORDER_CODE = BB.ORDER_CODE AND BB.UNIT_CODE = CC.UNIT_CODE"
				+ " AND AA.ORDERSET_CODE LIKE 'X%'"
				+ " AND BB.ACTIVE_FLG = 'Y'"
				+ " AND BB.ORDERSET_FLG = 'N'"
				+ " AND (   BB.REGION_CODE = 'H01'"
				+ " OR BB.REGION_CODE IS NULL"
				+ " OR BB.REGION_CODE = ''))"
				+ " ORDER BY ORDERSET_CODE,(CASE ORDERSET_FLG WHEN 'Y' THEN 0 ELSE 1 END)";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(Sql));
		
		//System.out.println("������������������������������������������������������������"+parm);
		if (parm.getCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		//���õ������ķ�������ͷ���ֶ�������������Ҫ����д
		ExportExcelUtil.getInstance().exportExcel(
				"����,100;��������Ŀ����,200;���,170;�ԷѼ�,100,double,########0.0000;��λ,50,UNIT_CODE;"+"����,70;����,70;С��,100",
				"ORDER_CODE;ORDER_DESC;SPECIFICATION;OWN_PRICE;UNIT_CHN_DESC;"+"OWN_PRICE;DOSAGE_QTY;N_TOTFEE",
				parm, "������ҽ��������Ϣ������ϸ��Ŀ");

	}

	/**
	 * �½�
	 */
	public void onNew() {

		clearCtl();
		cerateNewDate();
		// �ָ��༭״̬
		setEnabledForCtl(true);
		String nowOrdCode = ORDER_CODE.getValue();
		// ��ʼ�������table
		initDownTable(nowOrdCode);
	}

	/**
	 * ����
	 */
	public boolean onSave() {
		boolean activeDateFlg = true;
		if (onSaveCheck())
			return false;
		// ��ִ�б�ǲ���ѡ�е�ʱ��
		// =========pangben modify 20110427 ���checkItemExist�����������
		if (!ACTIVE_FLG.isSelected()
				&& !checkItemExist(ORDER_CODE.getText(), this
						.getValueString("REGION_CODE"))) {
			// �½���Ŀ��ʱ���������
			this.messageBox("��ѡ�����ã�");
			return false;
		} else {
			onExe();
		}
		// =========pangben modify 20110427 ���checkItemExist�����������
		if (!ACTIVE_FLG.isSelected()
				&& checkItemExist(ORDER_CODE.getText(), this
						.getValueString("REGION_CODE"))) {
			// ȡ����ʱ��
			activeDateFlg = false;
		}

		// �õ���ǰѡ���е����ݣ�Ҫ���ĺ��½����У�
		selRow = upTable.getSelectedRow();
		// ȡtable����
		TDataStore dataStore = upTable.getDataStore();
		// ���õ�������λһ�У���Ĭ�ϱ�����Ǹ��С���0
		if (selRow == -1 && dataStore.rowCount() == 1)
			selRow = 0;
		// �ӽ������õ����ݣ��ŵ�TDS�У����ڸ��±��桪��SYS_FEE_HISTORY
		setDataInTDS(dataStore, selRow, activeDateFlg);
		// ���ӱ���SYS_FEE��TDS
		if (setDataInSysFeeTds())
			bigObject.addDS("SYS_FEE", sysFeeDS);
		// �ô���󱣴棬�γ�һ������
		bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);
		getdownTableDS();
		bigObject.addDS("SYS_ORDERSETDETAIL", sysOrdSetDtlDS);
		bigObject.addDS("SYS_ORDEROPTITEM", eXMItem);
		if (!bigObject.update()) {
			messageBox("E0001");
			return false;
		}
		messageBox("P0001");
		// ǰ̨ˢ��
		TIOM_Database.logTableAction("SYS_FEE");
		return true;

	}

	/**
	 * �õ�SYS_FEE��TDS
	 */
	private boolean setDataInSysFeeTds() {
		String sql = "";
		String orderCode = ORDER_CODE.getValue();
		// ==========pangben modify 20110427 start ��������ѯ
		String region = "";
		if (null != this.getValueString("REGION_CODE")
				&& !"".equals(this.getValueString("REGION_CODE")))
			region = " AND (REGION_CODE='" + this.getValueString("REGION_CODE")
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"
				+ region;
		// ==========pangben modify 20110427 stop
		sysFeeDS.setSQL(sql);
		if (sysFeeDS.retrieve() <= 0)
			return false;

		// ��ǰ���ݿ�ʱ��
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// ���������
		sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
		sysFeeDS.setItem(0, "OPT_DATE", date);
		sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());

		sysFeeDS.setItem(0, "SUB_SYSTEM_CODE", SUB_SYSTEM_CODE.getValue());
		sysFeeDS.setItem(0, "RPTTYPE_CODE", RPTTYPE_CODE.getValue());
		sysFeeDS.setItem(0, "DEV_CODE", DEV_CODE.getValue());
		sysFeeDS.setItem(0, "OPTITEM_CODE", OPTITEM_CODE.getValue());
		sysFeeDS.setItem(0, "MR_CODE", MR_CODE.getValue());
		sysFeeDS.setItem(0, "DEGREE_CODE", DEGREE_CODE.getValue());

		sysFeeDS.setItem(0, "ORDER_CODE", ORDER_CODE.getValue());
		sysFeeDS.setItem(0, "ORDER_DESC", ORDER_DESC.getValue());
		sysFeeDS.setItem(0, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
		sysFeeDS.setItem(0, "PY1", PY1.getValue());

		sysFeeDS.setItem(0, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
		sysFeeDS.setItem(0, "DESCRIPTION", DESCRIPTION.getValue());
		sysFeeDS.setItem(0, "TIME_LIMIT", TIME_LIMIT.getValue());// add yanj
																	// 20140319
		sysFeeDS.setItem(0, "EXE_PLACE", EXE_PLACE.getValue());// add yanj
																// 20140319
		sysFeeDS.setItem(0, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());
		sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
		sysFeeDS.setItem(0, "ORDER_DEPT_CODE", ORDER_DEPT_CODE.getValue());

		sysFeeDS.setItem(0, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
		sysFeeDS.setItem(0, "MEDANT_FLG", MEDANT_FLG.getValue());// add caoyong
																	// 20140313
		// sysFeeDS.setItem(0, "UNIT_CODE", "584");
		sysFeeDS.setItem(0, "UNIT_CODE", getValue("UNIT"));
		sysFeeDS.setItem(0, "CAT1_TYPE", getCta1Type(""
				+ ORDER_CAT1_CODE.getValue()));
		sysFeeDS.setItem(0, "TUBE_TYPE", getValue("TUBE_TYPE"));
		sysFeeDS.setItem(0, "ACTIVE_FLG", getValue("ACTIVE_FLG"));
		sysFeeDS.setItem(0, "IS_REMARK", getValue("IS_REMARK"));
		// ==========pangben modify 20110427 start
		sysFeeDS.setItem(0, "REGION_CODE", getValue("REGION_CODE"));
		// ==========pangben modify 20110427 start
		sysFeeDS.setItem(0, "ORD_SUPERVISION", getValue("ORD_SUPERVISION"));
		return true;
	}

	/**
	 * ȡ��ҽ��ϸ����
	 * 
	 * @param orderCat1Type
	 *            String
	 * @return String
	 */
	private String getCta1Type(String orderCat1Type) {
		String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"
				+ orderCat1Type + "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		if (parm.getErrCode() != 0)
			return "";
		return parm.getValue("CAT1_TYPE", 0);
	}

	/**
	 * ȡ�����ݿ������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	public void getdownTableDS() {

		// orderCode = ORDER_CODE.getValue();
		// ��ǰ���ݿ�ʱ��
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int detailCount = sysOrdSetDtlDS.rowCount() - 1;
		String orderSetCode = ORDER_CODE.getValue();
		for (int i = 0; i < detailCount; i++) {
			sysOrdSetDtlDS.setItem(i, "ORDERSET_CODE", orderSetCode);
			sysOrdSetDtlDS.setItem(i, "OPT_USER", Operator.getID());
			sysOrdSetDtlDS.setItem(i, "OPT_DATE", now);
			sysOrdSetDtlDS.setItem(i, "OPT_TERM", Operator.getIP());

		}
		// ɾ�����һ�У���Ϊ�Զ����������û������
		sysOrdSetDtlDS.deleteRow(detailCount);
	}

	/**
	 * �ռ������ϵ�ֵ����ʹ�ã����±��棩
	 * 
	 * @param dataStore
	 *            TDataStore
	 */
	public void setDataInTDS(TDataStore dataStore, int row,
			boolean activeDateFlg) {
		// ��ǰ���ݿ�ʱ��
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		String dateString = StringTool.getString(date, "yyyyMMddHHmmss");
		// ���������
		dataStore.setItem(row, "OPT_USER", Operator.getID());
		dataStore.setItem(row, "OPT_DATE", date);
		dataStore.setItem(row, "OPT_TERM", Operator.getIP());

		dataStore.setItem(row, "START_DATE",
				!"".equals(START_DATE) ? START_DATE : dateString);
		dataStore.setItem(row, "END_DATE", activeDateFlg ? END_DATE
				: dateString);

		dataStore.setItem(row, "SUB_SYSTEM_CODE", SUB_SYSTEM_CODE.getValue());
		dataStore.setItem(row, "RPTTYPE_CODE", RPTTYPE_CODE.getValue());
		dataStore.setItem(row, "DEV_CODE", DEV_CODE.getValue());
		dataStore.setItem(row, "OPTITEM_CODE", OPTITEM_CODE.getValue());
		dataStore.setItem(row, "MR_CODE", MR_CODE.getValue());
		dataStore.setItem(row, "DEGREE_CODE", DEGREE_CODE.getValue());

		// ִ�б��
		dataStore.setItem(row, "ACTIVE_FLG", activeDateFlg ? "Y" : "N");

		dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
		dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
		dataStore.setItem(row, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
		dataStore.setItem(row, "PY1", PY1.getValue());

		dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
		dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
		dataStore.setItem(row, "TIME_LIMIT", TIME_LIMIT.getValue());// yanjing
																	// 20140319
																	// ʱ��
		dataStore.setItem(row, "EXE_PLACE", EXE_PLACE.getValue());// yanjing
																	// 20140319
																	// ִ�еص�
		dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());
		dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
		dataStore.setItem(row, "ORDER_DEPT_CODE", ORDER_DEPT_CODE.getValue());

		dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
		dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
		dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
		dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
		dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
		dataStore.setItem(row, "MEDANT_FLG", MEDANT_FLG.getValue());
		// dataStore.setItem(row, "UNIT_CODE", "584");
		dataStore.setItem(row, "UNIT_CODE", getValue("UNIT"));
		dataStore.setItem(row, "RPP_CODE", null);

		dataStore.setItem(row, "ORDERSET_FLG", "Y");
		TParm parm = getPriceAction();
		dataStore.setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE", 0));
		dataStore.setItem(row, "OWN_PRICE2", parm.getValue("OWN_PRICE2", 0));
		dataStore.setItem(row, "OWN_PRICE3", parm.getValue("OWN_PRICE3", 0));
		dataStore.setItem(row, "TUBE_TYPE", getValue("TUBE_TYPE"));
		dataStore.setItem(row, "IS_REMARK", getValue("IS_REMARK"));
		dataStore.setItem(row, "CAT1_TYPE", getCta1Type(""
				+ getValue("ORDER_CAT1_CODE")));
		// =============pangben modify 20110427 start
		dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
		// =============pangben modify 20110427 stop
		dataStore.setItem(row, "ORD_SUPERVISION", getValue("ORD_SUPERVISION"));
	}

	public TParm getPriceAction() {
		double ownPrice = 0;
		double nhiPrice = 0;
		double govPrice = 0;
		/*
		 * for(int i = 0;i<downTable.getRowCount();i++){
		 * if(sysOrdSetDtlDS.getRowParm(i).getValue("ORDER_CODE").length() == 0)
		 * continue; String SQL =
		 * " SELECT (CASE WHEN OWN_PRICE IS NULL THEN 0 ELSE OWN_PRICE END) OWN_PRICE,"
		 * +
		 * "        (CASE WHEN OWN_PRICE2 IS NULL THEN 0 ELSE OWN_PRICE2 END) OWN_PRICE2,"
		 * +
		 * "        (CASE WHEN OWN_PRICE3 IS NULL THEN 0 ELSE OWN_PRICE3 END) OWN_PRICE3"
		 * + " FROM SYS_FEE"+
		 * " WHERE ORDER_CODE = '"+sysOrdSetDtlDS.getRowParm(i
		 * ).getValue("ORDER_CODE")+"'"; TParm price = new
		 * TParm(TJDODBTool.getInstance().select(SQL)); ownPrice +=
		 * (price.getDouble("OWN_PRICE",0) *
		 * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY")); nhiPrice +=
		 * (price.getDouble("OWN_PRICE2",0) *
		 * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY")); govPrice +=
		 * (price.getDouble("OWN_PRICE3",0) *
		 * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY")); }
		 */
		TParm parmPrice = new TParm();
		parmPrice.addData("OWN_PRICE", ownPrice);
		parmPrice.addData("OWN_PRICE2", nhiPrice);
		parmPrice.addData("OWN_PRICE3", govPrice);
		return parmPrice;
	}

	/**
	 * ���½���ʱ���Զ����ɱ���ŵ�������
	 */
	public void cerateNewDate() {
		String newCode = "";

		// �����ı�
		upTable.acceptText();
		// ȡtable����
		// �ҵ������Ҵ���(dataStore,����)
		// ========pangben modify 20110427 start
		// ע��ȥ������Ҫ��ѯ���е�����ţ�ͨ����ѯ���ݿ��еĵ���ֵ��ʾ�����
		// TDataStore dataStore = upTable.getDataStore();
		// String maxCode = getMaxCode(dataStore, "ORDER_CODE");
		// ========pangben modify 20110427 start
		// �ҵ�ѡ�е����ڵ�
		TTreeNode node = tree.getSelectionNode();
		// ���û��ѡ�еĽڵ�
		if (node == null)
			return;
		// �õ���ǰID
		String nowID = node.getID();
		int classify = 1;
		// �����������ĸ��ڵ����,�õ���ǰ�������
		if (nowID.length() > 0)
			classify = ruleTool.getNumberClass(nowID) + 1;
		// �������С�ڵ�,��������һ��
		if (classify > ruleTool.getClassifyCurrent()) {
			// �õ�Ĭ�ϵ��Զ���ӵ�ҽ������
			// ===pangben modify 20110427 start
			// ============���Ҵ˱�Ź����б�����ֵ
			String sql = "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM SYS_FEE_HISTORY WHERE ORDER_CODE LIKE '"
					+ nowID + "%'";
			TParm parm = new TParm(getDBTool().select(sql));
			String maxCode = parm.getValue("ORDER_CODE", 0);
			// ===pangben modify 20110427 stop

			String no = ruleTool.getNewCode(maxCode, classify);
			newCode = nowID + no;
			// �õ�����ӵ�table�����к�(�൱��TD�е�insertRow()����)
			int row = upTable.addRow();
			// ���õ�ǰѡ����Ϊ��ӵ���
			upTable.setSelectedRow(row);
			// �����Ҵ������Ĭ��ֵ
			upTable.setItem(row, "ORDER_CODE", newCode);
			// Ĭ�Ͽ�������
			upTable.setItem(row, "ORDER_DESC", "(�½�����)");
			// Ĭ�Ͽ��Ҽ��
			upTable.setItem(row, "SPECIFICATION", null);
			// ʱ��
			upTable.setItem(row, "TIME_LIMIT", "");
			// ִ�еص�
			upTable.setItem(row, "EXE_PLACE", "");
			// Ĭ�Ͽ���
			upTable.setItem(row, "OWN_PRICE", null);
			// Ĭ����С����ע��
			upTable.setItem(row, "UNIT_CODE", null);
		}
		// ���Զ����ɵ�orderCode���õ�ORDER_CODE��
		ORDER_CODE.setText(newCode);

	}

	/**
	 * �õ����ı��
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public String getMaxCode(TDataStore dataStore, String columnName) {
		if (dataStore == null)
			return "";
		String s = "";
		if (dataStore.isFilter()) {
			TParm a = dataStore.getBuffer(TDataStore.FILTER);
			int count = a.getCount();
			for (int i = 0; i < count; i++) {
				String value = a.getValue(columnName, i);
				if (StringTool.compareTo(s, value) < 0)
					s = value;
			}
		} else {
			int count = dataStore.rowCount();
			for (int i = 0; i < count; i++) {
				String value = dataStore.getItemString(i, columnName);
				if (StringTool.compareTo(s, value) < 0)
					s = value;
			}
		}
		return s;
	}

	/**
	 * ����table�ϵ�ĳһ�����ݸ�����Ŀؼ���ʼ��ֵ
	 * 
	 * @param date
	 *            TParm
	 */
	public void setValueForDownCtl(TParm date) {

		clearCtl();
		START_DATE = date.getValue("START_DATE");
		END_DATE = date.getValue("END_DATE");
		this.setValue("SUB_SYSTEM_CODE", date.getValue("SUB_SYSTEM_CODE"));
		this.setValue("RPTTYPE_CODE", date.getValue("RPTTYPE_CODE"));
		this.setValue("DEV_CODE", date.getValue("DEV_CODE"));
		this.setValue("OPTITEM_CODE", date.getValue("OPTITEM_CODE"));
		this.setValue("MR_CODE", date.getValue("MR_CODE"));
		this.setValue("DEGREE_CODE", date.getValue("DEGREE_CODE"));

		this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
		this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
		this.setValue("TRADE_ENG_DESC", date.getValue("TRADE_ENG_DESC"));
		this.setValue("PY1", date.getValue("PY1"));

		this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
		this.setValue("TIME_LIMIT", date.getValue("TIME_LIMIT"));// 20140319
																	// yanjing
																	// ʱ��
		this.setValue("EXE_PLACE", date.getValue("EXE_PLACE"));// 20140319
																// yanjing ִ�еص�
		this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
		this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));
		this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
		this.setValue("ORDER_DEPT_CODE", date.getValue("ORDER_DEPT_CODE"));

		this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
		this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
		this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
		this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
		this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
		this.setValue("MEDANT_FLG", date.getValue("MEDANT_FLG"));
		this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
		this.setValue("UNIT", date.getValue("UNIT_CODE"));
		this.setValue("TUBE_TYPE", date.getValue("TUBE_TYPE"));
		this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
		// ===========pangben modify 20110427 start
		this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
		// ===========pangben modify 20110427 stop
		this.setValue("ORD_SUPERVISION", date.getValue("ORD_SUPERVISION"));
	}

	/**
	 * ѡ��TRANS_OUT_FLG���
	 */
	public void onOutHosp() {
		String value = TRANS_OUT_FLG.getValue();
		TRANS_HOSP_CODE.setEnabled(TypeTool.getBoolean(value));
		if (!TypeTool.getBoolean(value)) {
			TRANS_HOSP_CODE.setText("");
		}
	}

	/**
	 * �õ�����ƴ��
	 */
	public void onPY1() {
		String orderDesc = ORDER_DESC.getText();
		String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
		PY1.setText(orderPy);
		DESCRIPTION.grabFocus();
	}

	/**
	 * �������еĿؼ��Ƿ���ã���ʷ���ݲ������޸ģ�
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setEnabledForCtl(boolean flg) {
		String tag[] = controlName.split(";");
		int count = tag.length;
		for (int i = 0; i < count; i++) {
			this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
		}
	}

	/**
	 * �����á�ֻ����end_date�������start_date(�½�����ǰʱ�䣻��ѯ���ģ��Ǿɵ�ʱ��)
	 */
	public void onExe() {
		// �����ȡ������Ҫ�������޸�ʱ���޸�
		if (!ACTIVE_FLG.isSelected())
			return;
		Timestamp time = TJDODBTool.getInstance().getDBTime();
		String tempStartDate = StringTool
				.getString(time, "yyyy/MM/dd HH:mm:ss");
		START_DATE = tempStartDate.substring(0, 4)
				+ tempStartDate.substring(5, 7)
				+ tempStartDate.substring(8, 10)
				+ tempStartDate.substring(11, 13)
				+ tempStartDate.substring(14, 16)
				+ tempStartDate.substring(17, 19);

		// �������õ�ʱ���ʧЧ����дΪ"99991231235959"
		if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
			END_DATE = "99991231235959";
		} else { // ͣ�õ�ʱ��end_date��Ϊ��ǰʱ��
			String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
			END_DATE = date.substring(0, 4) + date.substring(5, 7)
					+ date.substring(8, 10) + date.substring(11, 13)
					+ date.substring(14, 16) + date.substring(17, 19);
		}

	}

	/**
	 * table˫��ѡ����
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubleCleck() {

		upTable.acceptText();
		int row = upTable.getSelectedRow();
		String value = upTable.getItemString(row, 0);
		// �õ��ϲ����
		String partentID = ruleTool.getNumberParent(value);
		TTreeNode node = treeRoot.findNodeForID(partentID);
		if (node == null)
			return;
		// �õ������ϵ�������
		TTree tree = (TTree) callMessage("UI|TREE|getThis");
		// ��������ѡ�нڵ�
		tree.setSelectNode(node);
		tree.update();
		// ���ò�ѯ�¼�
		// =====pangben modify 20110427 start ����������
		onCleckClassifyNode(partentID, upTable, Operator.getRegion());
		// =====pangben modify 20110427 stop
		// table������ѡ����
		int count = upTable.getRowCount(); // table������
		for (int i = 0; i < count; i++) {
			// �õ����ʴ���
			String invCode = upTable.getItemString(i, 0);
			if (value.equals(invCode)) {
				// ����ѡ����
				upTable.setSelectedRow(i);
				return;
			}
		}
	}

	/**
	 * ѡ�ж�Ӧ���ڵ���¼�
	 * 
	 * @param parentID
	 *            String
	 * @param table
	 *            TTable
	 */
	public void onCleckClassifyNode(String parentID, TTable table,
			String regionCode) {
		// =======pangben modify 20110427 start ����������
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		// table�е�datastore�в�ѯ����sql
		table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '"
				+ parentID + "%'" + region);
		// =======pangben modify 20110427 start
		// ��������
		table.retrieve();
		// �������ݵ�table��
		table.setDSValue();
		// ����������ť
		callFunction("UI|new|setEnabled", true);

	}

	/**
	 * ���������ʱ�����ϸ����ʵ�۸���µ�����
	 */
	private void updateMastOwnPrice() {
		String ordersetCode = "";
		int tblCount = upTable.getRowCount();
		TDataStore ds = upTable.getDataStore();
		// ѭ��ÿһ��
		for (int j = 0; j < tblCount; j++) {
			ordersetCode = (String) ds.getItemData(j, "ORDER_CODE");
			// �õ�������Ķ�Ӧϸ���б�
			String selordCodeForDtl = "SELECT ORDER_CODE,DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE = '"
					+ ordersetCode + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					selordCodeForDtl));
			int count = parm.getCount();
			double totFee = 0.0;
			for (int i = 0; i < count; i++) {
				String code = parm.getValue("ORDER_CODE", i);
				double qty = TypeTool.getDouble(parm.getValue("DOSAGE_QTY", i));
				totFee += getPrice(code) * qty;
			}
			upTable.setValueAt(totFee, j, 3);
		}

	}

	/**
	 * ����
	 * 
	 * @param ob
	 *            Object
	 */
	public void onFilter(Object ob) {
		if ("ALL".equals(ob.toString())) {
			upTable.setFilter("");
			upTable.filter();
			// �������ݵ�table��
			upTable.setDSValue();
			// ����������ť
			callFunction("UI|new|setEnabled", true);
		} else if ("Y".equals(ob.toString())) {
			upTable.setFilter("ACTIVE_FLG='Y'");
			upTable.filter();
			// �������ݵ�table��
			upTable.setDSValue();
			// ����������ť
			callFunction("UI|new|setEnabled", true);
		} else {
			upTable.setFilter("ACTIVE_FLG='N'");
			upTable.filter();
			// �������ݵ�table��
			upTable.setDSValue();
			// ����������ť
			callFunction("UI|new|setEnabled", false);
		}
		updateMastOwnPrice();
	}

	/**
	 * �жϸ���Ŀ�Ƿ�Ӧ�����������
	 * 
	 * @param code
	 *            String
	 * @return boolean ============pangben modify 20110427 ����������
	 */
	private boolean checkItemExist(String code, String regionCode) {
		// =====pangben modify 20110427 start
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		String sql = " SELECT COUNT(START_DATE) AS COUNT FROM SYS_FEE_HISTORY "
				+ " WHERE ORDER_CODE='" + code + "'" + region;
		// =====pangben modify 20110427 stop
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if ("0".equals(result.getData("COUNT", 0) + "")) {
			return false;
		}
		return true;
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnSYSFee(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		String ordCode = result.getValue("ORDER_CODE");
		// ���ݷ��ص�CODE����table��TDS��ֵ
		setDownTableAndTDS(ordCode);

	}

	public void onCheckItem() {
		if (getValueString("ORDER_CODE").length() == 0)
			return;
		TParm parmIn = new TParm();
		parmIn.setData("ORDER_CODE", getValue("ORDER_CODE"));
		Object obj = openDialog(
				"%ROOT%\\config\\sys\\SYS_FEE\\SYSFEEEXMItem.x", parmIn);
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		int count = parm.getCount("ID");
		if (count <= 0)
			return;
		eXMItem
				.setSQL("SELECT ORDER_CODE,OPTITEM_CODE,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_ORDEROPTITEM WHERE ORDER_CODE = '"
						+ getValue("ORDER_CODE") + "'");
		eXMItem.retrieve();
		eXMItem.deleteRowAll();
		Timestamp timestamp = SystemTool.getInstance().getDate();
		for (int i = 0; i < count; i++) {
			int row = eXMItem.insertRow();
			eXMItem.setItem(row, "ORDER_CODE", getValue("ORDER_CODE"));
			eXMItem.setItem(row, "OPTITEM_CODE", parm.getData("ID", i));
			eXMItem.setItem(row, "OPT_USER", Operator.getID());
			eXMItem.setItem(row, "OPT_DATE", timestamp);
			eXMItem.setItem(row, "OPT_TERM", Operator.getIP());
		}
	}

	public boolean onSaveCheck() {
		if (getValueString("SUB_SYSTEM_CODE").length() == 0) {
			messageBox("ϵͳ���Ʋ���Ϊ��");
			return true;
		}
		if (getValueString("RPTTYPE_CODE").length() == 0) {
			messageBox("������𲻿�Ϊ��");
			return true;
		}
		if (getValueString("DEV_CODE").length() == 0) {
			messageBox("������𲻿�Ϊ��");
			return true;
		}
		if (getValueString("OPTITEM_CODE").length() == 0) {
			messageBox("��鲿λ����Ϊ��");
			return true;
		}
		// if(getValueString("EXEC_DEPT_CODE").length() == 0){
		// messageBox("ִ�п��Ҳ���Ϊ��");
		// return true;
		// }
		if (getValueString("ORDER_CODE").length() == 0) {
			messageBox("ҽ�����벻��Ϊ��");
			return true;
		}
		if (getValueString("CHARGE_HOSP_CODE").length() == 0) {
			messageBox("Ժ�ڴ��벻��Ϊ��");
			return true;
		}
		if (getValueString("ORDER_DESC").length() == 0) {
			messageBox("ҽ�����Ʋ���Ϊ��");
			return true;
		}
		if (getValueString("UNIT").length() == 0) {
			messageBox("��λ����Ϊ��");
			return true;
		}
		if (getValueString("PY1").length() == 0) {
			messageBox("ҽ��ƴ������Ϊ��");
			return true;
		}
		if (getValueString("ORDER_CAT1_CODE").length() == 0) {
			messageBox("ҽ�����಻��Ϊ��");
			return true;
		}
		return false;
	}

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		JavaHisDebug.TBuilder();
		// JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_EXM.x");
	}

}
