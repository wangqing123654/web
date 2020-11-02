package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.TTreeNode;

import jdo.sys.SYSRuleTool;

import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.sys.Operator;

import com.dongyang.util.StringTool;

import jdo.sys.SYSHzpyTool;

import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;

import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: ����ģ���ֵ����
 * </p>
 * 
 * <p>
 * Description:
 * </p>
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
public class SYSFee_OrdSetTypeControl extends TControl {
	
	public SYSFee_OrdSetTypeControl() {
	}

	/**
	 * ȡ�����ݿ������
	 * 
	 * @return TJDODBTool ========pangben modify 20110428
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
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
	String packCode = "";

	TDS sysFeeOrdPackM = new TDS();
	TDS sysFeeOrdPackD = new TDS();
	/**
	 * ���пؼ�������
	 */
	private String controlName = "PACK_DESC;ENG_NAME;DESCRIPTION;PY1;FIT_DEPT";

	/**
	 * ����Ŀؼ�
	 */
	// ��
	TTree tree;
	// ����
	private TTable upTable, downTable;
	TTextFormat FIT_DEPT;
	TTextField PACK_CODE;
	TTextField PACK_DESC;
	TTextField ENG_NAME;
	TTextField PY1;
	TTextField DESCRIPTION;

	public void onInit() { // ��ʼ������
		super.onInit();
		myInitControler();
		this.downTable = (TTable) this.getComponent("downTable");
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
		treeRoot.setText("�ײ�ģ�����");
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
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_ORDERSET_TYPE'");
		// �����dataStore���õ�������С��0
		if (treeDataStore.retrieve() <= 0)
			return;
		// ��������,�Ǳ�������еĿ�������
		ruleTool = new SYSRuleTool("SYS_ORDERSET_TYPE");
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
			// =======pangben modify 20110428 start
			String sql = getSQL(id, Operator.getRegion());
			// ��ʼ��table��TDS
			initTblAndTDS(sql);

		}
		// �õ���ǰ�������ID
		String nowID = node.getID();

		int classify = 1;
		if (nowID.length() > 0)
			classify = ruleTool.getNumberClass(nowID) + 1;
		// �������С�ڵ�,��������һ��(ʹ������ť����)
		if (classify > ruleTool.getClassifyCurrent()) {
			this.callFunction("UI|new|setEnabled", true);
		}
	}

	/**
	 * �õ���ʼ��TDS��SQL���(����Ŀǰ�������õ���Ŀ�б�����START_DATE/END_DATE ֻ��ACTIVE_FLGΪ��Y��)
	 * 
	 * @return String =========pangben modify 20110428 ����������
	 */
	private String getSQL(String packCode, String regionCode) {
		// ==========pangben modify 20110428 start
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		String sql = "";
		// ���������
		if (packCode != null && packCode.length() > 0)
            sql = " SELECT * FROM SYS_ORDER_PACKM WHERE " +
                " PACK_CODE LIKE '" + packCode + "%'" +region+
                " ORDER BY PACK_CODE";
//		System.out.println("-----------"+sql);
		// ==========pangben modify 20110428 stop
		return sql;
	}

	/**
	 * ��ʼ������ı�������еĿؼ�����
	 * 
	 * @param sql
	 *            String
	 */
	public void initTblAndTDS(String sql) {
		sysFeeOrdPackM.setSQL(sql);
		sysFeeOrdPackM.retrieve();
		// ���û��������ձ���ϵ�����
		if (sysFeeOrdPackM.rowCount() <= 0) {
			upTable.removeRowAll();
		}
		sysFeeOrdPackM.showDebug();

		upTable.setDataStore(sysFeeOrdPackM);
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

		FIT_DEPT = (TTextFormat) this.getComponent("FIT_DEPT");
		PACK_DESC = (TTextField) this.getComponent("PACK_DESC");
		ENG_NAME = (TTextField) this.getComponent("PACK_DESC");
		DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
		PACK_CODE = (TTextField) this.getComponent("PACK_CODE");
		PY1 = (TTextField) this.getComponent("PY1");

		// ����tableע�ᵥ���¼�����
		this.callFunction("UI|upTable|addEventListener", "upTable->"
				+ TTableEvent.CLICKED, this, "onUpTableClicked");
		// ����tableע������¼�
		downTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateSYSFEE");

		// ������ť������
		callFunction("UI|new|setEnabled", false);
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
		// ��ѯ������ϸ��--6
		TParm parm = new TParm();
		// parm.setData("RX_TYPE", 6);
		textFilter
				.setPopupMenuParameter(
						"IG",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		String ordCode = result.getValue("ORDER_CODE");
		String ordDesc = result.getValue("ORDER_DESC");
		String unitcode = result.getValue("UNIT_CODE");

		// ���ݷ��ص�CODE����table��TDS��ֵ
		setDownTableAndTDS(ordCode, ordDesc, unitcode);

	}

	public void setDownTableAndTDS(String code, String ordDesc, String unitcode) {
		downTable.acceptText();
		int selrow = downTable.getSelectedRow();
		downTable.setItem(selrow, "ORDER_CODE", code);
		downTable.setItem(selrow, "ORDER_DESC", ordDesc);
		downTable.setItem(selrow, "DOSAGE_UNIT", unitcode);
		//20150126 wangjingchun add start
		if(selrow == 0){
			downTable.setItem(selrow, "SEQ_NO", "1");
		}else{
			downTable.setItem(selrow, "SEQ_NO", TypeTool.getInt(downTable.getValueAt_(selrow-1, 6))+1);
		}
		//20150126 wangjingchun add end
		// ������һ����Ŀ֮��Ĭ��Ϊ1.0
		if (TypeTool.getDouble(downTable.getValueAt_(selrow, 5)) == 0.0) {
			downTable.setItem(selrow, "DOSAGE_QTY", 1.0);
		}
		// ����һ��
		// sysOrdSetDtlDS.insertRow();
		if (downTable.getSelectedRow() + 1 == downTable.getRowCount())
			downTable.addRow();
		downTable.getTable().grabFocus();//20150113 wangjingchun add
		downTable.getTable().editCellAt(downTable.getTable().getSelectedRow(), 4);//20150113 wangjingchun add

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
		// �õ���ǰ��Ŀ����(SYS_FEE_HISTORY�У�ORDER_CODE/SYS_ORDERSETDETAI�У�ORDERSET_CODE)
		packCode = PACK_CODE.getValue();
		// ��ʼ�������table
		initDownTable(packCode);
	}
	
	
	/**
	 * ����Table�س��¼�
	 * 20150113 wangjingchun add
	 */
	public void onTableClick(){
		this.downTable.getTable().grabFocus();
		this.downTable.getTable().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == 10){
					if(downTable.getTable().getSelectedColumn()==1){
						downTable.getTable().editCellAt(downTable.getTable().getSelectedRow(), 4);
					}
					if(downTable.getTable().getSelectedColumn()==4){
						if(downTable.getValueAt(downTable.getTable().getSelectedRow(), 1).toString().equals("")){
							return;
						}
						downTable.getTable().editCellAt(downTable.getTable().getSelectedRow()+1, 1);
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		
		
	}

	/**
	 * ��ʼ�������table
	 * 
	 * @param ordersetCode
	 *            String
	 */
	public void initDownTable(String packCode) {

		downTable.acceptText();
		String sqlForDtl = "SELECT * FROM SYS_ORDER_PACKD WHERE PACK_CODE = '"
				+ packCode + "' "
				+" ORDER BY SEQ_NO";//20150126 wangjingchun add
		sysFeeOrdPackD.setSQL(sqlForDtl);
		sysFeeOrdPackD.retrieve();
		sysFeeOrdPackD.showDebug();
		// ����������
		sysOdrPackDObserver obser = new sysOdrPackDObserver();
		// �Ѽ��������õ�ĳ��DS��
		sysFeeOrdPackD.addObserver(obser);

		// ���û��������ձ���ϵ�����
		if (sysFeeOrdPackD.rowCount() <= 0) {
			downTable.removeRowAll();
		}

		sysFeeOrdPackD.insertRow();

		downTable.setDataStore(sysFeeOrdPackD);
		downTable.setDSValue();
	}

	/**
	 * ��ճ���table����Ŀؼ���ֵ
	 */
	public void clearCtl() {

		this.clearValue(controlName + ";TRANS_HOSP_CODE;ORDER_CODE"
				+ ";TOT_FEE");
		// ====pangben modify 20110427 start
		this.setValue("REGION_CODE", Operator.getRegion());
		// ====pangben modify 20110427 stop
		
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		clearCtl();
		upTable.removeRowAll();
		downTable.removeRowAll();
	}

	/**
	 * �½�
	 */
	public void onNew() {

		clearCtl();
		cerateNewDate();
		// �ָ��༭״̬
		setEnabledForCtl(true);
		String nowpackCode = PACK_CODE.getValue();
		// ��ʼ�������table
		initDownTable(nowpackCode);

	}

	/**
	 * ����
	 */
	public boolean onSave() {

		try {
			// �õ���ǰѡ���е����ݣ�Ҫ���ĺ��½����У�
			selRow = upTable.getSelectedRow();
			// ȡtable����
			TDataStore dataStore = upTable.getDataStore();
			int selRow = upTable.getSelectedRow();
			setDataInTDS(dataStore, selRow);
			// /�ô���󱣴棬�γ�һ������
			bigObject.addDS("SYS_ORDER_PACKM", sysFeeOrdPackM);
			getdownTableDS();
			bigObject.addDS("SYS_ORDER_PACKD", sysFeeOrdPackD);

			String r = check();
			if(r.length()>0 || !r.equals("")){
				messageBox(r);
				return false;
			}
			
			if (!bigObject.update()) {
				messageBox("E0001");
				return false;
			}
			messageBox("P0001");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
			if(downTable.getRowCount() - 1 == sysFeeOrdPackD.rowCount()){
				sysFeeOrdPackD.insertRow();
			}
			return false;
		}
	}

	public void getdownTableDS() {

		// ��ǰ���ݿ�ʱ��
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int detailCount = sysFeeOrdPackD.rowCount() - 1;
		String packCode = PACK_CODE.getValue();
		for (int i = 0; i < detailCount; i++) {
			sysFeeOrdPackD.setItem(i, "PACK_CODE", packCode);
			sysFeeOrdPackD.setItem(i, "OPT_USER", Operator.getID());
			sysFeeOrdPackD.setItem(i, "OPT_DATE", now);
			sysFeeOrdPackD.setItem(i, "OPT_TERM", Operator.getIP());

		}
		// ɾ�����һ�У���Ϊ�Զ����������û������
		if(downTable.getRowCount()-1 == detailCount){
			sysFeeOrdPackD.deleteRow(detailCount);
		}
	}

	/**
	 * �ռ������ϵ�ֵ����ʹ��
	 * 
	 * @param dataStore
	 *            TDataStore
	 */
	public void setDataInTDS(TDataStore dataStore, int row) {
		// ��ǰ���ݿ�ʱ��
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// ���������
		dataStore.setItem(row, "OPT_USER", Operator.getID());
		dataStore.setItem(row, "OPT_DATE", date);
		dataStore.setItem(row, "OPT_TERM", Operator.getIP());

		dataStore.setItem(row, "PACK_CODE", PACK_CODE.getValue());
		dataStore.setItem(row, "PY1", PY1.getValue());
		dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
		dataStore.setItem(row, "FIT_DEPT", FIT_DEPT.getValue());
		dataStore.setItem(row, "PACK_DESC", PACK_DESC.getValue());
		dataStore.setItem(row, "ENG_NAME", ENG_NAME.getValue());
		// =============pangben modify 20110427 start
		dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
		//20150112 wangjingchun add 652
		dataStore.setItem(row, "ACTIVE_FLG", getValue("ACTIVE_FLG"));
		// =============pangben modify 20110427 stop

	}

	/**
	 * ���½���ʱ���Զ����ɱ���ŵ�������
	 */
	public void cerateNewDate() {
		String newCode = "";

		// �����ı�
		upTable.acceptText();
		// ȡtable����
		// TDataStore dataStore = upTable.getDataStore();
		// �ҵ������Ҵ���(dataStore,����)
		// ========pangben modify 20110427 start
		// ע��ȥ������Ҫ��ѯ���е�����ţ�ͨ����ѯ���ݿ��еĵ���ֵ��ʾ�����
		// String maxCode = getMaxCode(dataStore, "ORDER_CODE");
		// System.out.println("maxCode:"+maxCode);
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
			String sql = "SELECT MAX(PACK_CODE) AS PACK_CODE FROM SYS_ORDER_PACKM WHERE PACK_CODE LIKE '"
					+ nowID + "%'";
			TParm parm = new TParm(getDBTool().select(sql));
			String maxCode = parm.getValue("PACK_CODE", 0);
			// ===pangben modify 20110427 start

			String no = ruleTool.getNewCode(maxCode, classify);
			newCode = nowID + no;
			// �õ�����ӵ�table�����к�(�൱��TD�е�insertRow()����)
			int row = upTable.addRow();
			// ���õ�ǰѡ����Ϊ��ӵ���
			upTable.setSelectedRow(row);
			// �����Ҵ������Ĭ��ֵ
			upTable.setItem(row, "PACK_CODE", newCode);
			// Ĭ�Ͽ�������
			upTable.setItem(row, "PACK_DESC", "(�½�����)");
			// Ĭ�Ͽ��Ҽ��
			upTable.setItem(row, "DESCRIPTION", null);
			// Ĭ�Ͽ���
			upTable.setItem(row, "FIT_DEPT", null);

		}
		// ���Զ����ɵ�orderCode���õ�ORDER_CODE��
		PACK_CODE.setText(newCode);
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
		int count = dataStore.rowCount();
		String s = "";
		for (int i = 0; i < count; i++) {
			String value = dataStore.getItemString(i, columnName);
			if (StringTool.compareTo(s, value) < 0)
				s = value;
		}
		return s;
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
		onCleckClassifyNode(partentID, upTable);
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
	public void onCleckClassifyNode(String parentID, TTable table) {
		// table�е�datastore�в�ѯ����sql
		table.setSQL("select * from SYS_ORDER_PACKM where PACK_CODE like '"
				+ parentID + "%'");
		// ��������
		table.retrieve();
		// �������ݵ�table��
		table.setDSValue();
		// ����������ť
		callFunction("UI|new|setEnabled", true);

	}

	/**
	 * ����table�ϵ�ĳһ�����ݸ�����Ŀؼ���ʼ��ֵ
	 * 
	 * @param date
	 *            TParm
	 */
	public void setValueForDownCtl(TParm date) {

		clearCtl();
		this.setValue("PACK_CODE", date.getValue("PACK_CODE"));
		this.setValue("PACK_DESC", date.getValue("PACK_DESC"));
		this.setValue("ENG_NAME", date.getValue("ENG_NAME"));
		this.setValue("PY1", date.getValue("PY1"));
		this.setValue("FIT_DEPT", date.getValue("FIT_DEPT"));
		this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
		// =====pangben modify 20110427 start
		this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
		//20150112 wangjingchun add 652
		this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
	}

	/**
	 * �õ�����ƴ��
	 */
	public void onPY1() {
		String orderDesc = PACK_DESC.getText();
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

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		JavaHisDebug.TBuilder();
		// JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDSETTYPE.x");
	}
	
	private String check(){
		String r = "";
		int detailCount = sysFeeOrdPackD.rowCount();
		List<String> list = new ArrayList<String>();
		String orderCode;
		for (int i = 0; i < detailCount; i++) {
			orderCode = sysFeeOrdPackD.getItemString(i, "ORDER_CODE");
			if(!list.contains(orderCode)){
				list.add(orderCode);
			}else{
				return orderCode+sysFeeOrdPackD.getItemString(i, "ORDER_DESC")+"�ظ�";
			}
		}
		return r;
	}

}
