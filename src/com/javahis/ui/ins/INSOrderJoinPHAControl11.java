package com.javahis.ui.ins;

import jdo.ins.INSRuleTXTool;
import jdo.ins.INSSYSRuleTXTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;

/**
 * <p>
 * Title:��Ŀ�ֵ�
 * </p>
 * 
 * <p>
 * Description: ҽ����Ŀ�ֵ��Ӧ��ҩƷ
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author pangb 2011-12-09
 */
public class INSOrderJoinPHAControl11 extends TControl {
	/**
	 * 
	 * ����Ŀؼ�
	 */
	// ��
	TTree tree;
	/**
	 * ����
	 */
	private TTreeNode treeRoot;

	private TParm sysFeeParm;// ҽ����Ϣ

	private String orderCode;// ��ѡ����ͼ���ҽ������

	private TTable upTable;// �޸ı��

	private TTable ruleTable;// ��Ŀ�ֵ���

	private int upTableRow = -1;// upTable��õ�ǰѡ����
	/**
	 * ��Ź�����𹤾�
	 */
	private INSSYSRuleTXTool ruleSysTool;

	/**
	 * �������ݷ���datastore���ڶ��������ݹ���
	 */
	private TDataStore treeDataStore = new TDataStore();

	public void onInit() { // ��ʼ������
		super.onInit();
		onInitTree();
		getInitParm();
		// ��ʼ�����
		onInitNode();
	}

	/**
	 * ��ó�ʼ������
	 */
	private void getInitParm() {
		sysFeeParm = new TParm(TJDODBTool.getInstance().select(
				getSQL(Operator.getRegion())));
		upTable = (TTable) this.callFunction("UI|upTable|getThis");
		ruleTable = (TTable) this.callFunction("UI|ruleTable|getThis");
		// upTable�������¼�
		callFunction("UI|upTable|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// ����ҽ��������
		callFunction("UI|TXT_ORDER_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSFeePopupToINS.x");
		// textfield���ܻش�ֵ
		callFunction("UI|TXT_ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// ������Ŀ�ֵ䵯����
		callFunction("UI|INS_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\ins\\INSFeePopup.x");
		// textfield���ܻش�ֵ
		callFunction("UI|INS_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popInsReturn");
		// onRuleQuery();
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
		treeRoot.setText("ҩƷ����");
		// �����ڵ㸳tag
		treeRoot.setType("Root");
		// ���ø��ڵ��id
		treeRoot.setID("");
		// ������нڵ������
		treeRoot.removeAllChildren();
		// ���������ʼ������
		callMessage("UI|TREE|update");
		// ��tree��Ӽ����¼�
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
	}

	/**
	 * ��ʼ�����Ľ��
	 */

	public void onInitNode() {
		// ��dataStore��ֵ
		treeDataStore
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='PHA_RULE'");
		// �����dataStore���õ�������С��0
		if (treeDataStore.retrieve() <= 0)
			return;
		// ��������,�Ǳ�������еĿ�������
		ruleSysTool = new INSSYSRuleTXTool("PHA_RULE");
		if (ruleSysTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
			TTreeNode node[] = ruleSysTool.getTreeNode(treeDataStore,
					sysFeeParm, "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path",
					"SEQ");
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
	 * ����Ŀǰ�������õ���Ŀ�б�
	 * 
	 * @return String
	 */
	private String getSQL(String regionCode) {
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		String sql = "";
		// ���������
		sql = " SELECT * FROM SYS_FEE WHERE CHARGE_HOSP_CODE IN ('M01','N01','L01','Z01')  "
				+ region + " ORDER BY ORDER_CODE";
		return sql;
	}

	/**
	 * ������
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) {
		// ���
		// onClear();
		// �õ�������Ľڵ����
		TTreeNode node = (TTreeNode) parm;
		if (node == null)
			return;
		// �õ�table����
		// TTable table = (TTable) this.callFunction("UI|upTable|getThis");
		// // table�������иı�ֵ
		// table.acceptText();
		// �жϵ�����Ƿ������ĸ����
		if (node.getType().equals("Root")) {
			// ��������ĸ��ӵ�table�ϲ���ʾ����
			orderCode = null;
		} else { // �����Ĳ��Ǹ����
			// �õ���ǰѡ�еĽڵ��idֵ
			orderCode = node.getID();
			// System.out.println("orderCode::" + orderCode);
			// �õ���ѯTDS��SQL��䣨ͨ����һ���IDȥlike���е�orderCode��
			// ======pangben modify 20110427 start ����������
			// String sql = getSQL(id,Operator.getRegion());
			// ======pangben modify 20110427 stop
			// ��ʼ��table��TDS
			// initTblAndTDS(sql);

		}
	}

	/**
	 * ����޸�ҽ��
	 */
	public void addUpdateSysFee() {

		if (null == orderCode) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		// �õ�table����

		if (null != upTable.getParmValue()) {
			for (int i = 0; i < upTable.getParmValue().getCount(); i++) {
				if (upTable.getParmValue().getValue("ORDER_CODE", i).equals(
						orderCode)) {
					this.messageBox("�Ѿ����ڴ�ҽ����Ϣ");
					return;
				}
			}
		}
		// ���ҽ����Ϣ
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.NHI_CODE_I,A.NHI_CODE_O,A.NHI_CODE_E,"
				+ " CASE A.OPD_FIT_FLG WHEN '1' THEN 'Y' ELSE 'N' END AS OPD_FIT_FLG," //����ʹ��
				+ "CASE A.EMG_FIT_FLG WHEN '1' THEN 'Y'  ELSE 'N' END AS EMG_FIT_FLG ,"//����ʹ��
				+ "CASE A.IPD_FIT_FLG WHEN '1' THEN 'Y' ELSE 'N' END AS IPD_FIT_FLG,A.NHI_FEE_DESC,A.NHI_PRICE,A.INSPAY_TYPE,A.OWN_PRICE,"
				+ "B.DOSE_CODE,A.SPECIFICATION,A.HYGIENE_TRADE_CODE,A.MAN_CODE FROM SYS_FEE A ,PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.ORDER_CODE='"
				+ orderCode + "'";
		TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null == upTable.getParmValue()) {
			upTable.setParmValue(new TParm());
		}
		TParm updateParm = upTable.getParmValue();
		updateParm.addRowData(sysParm, 0);// ���ҽ��
		upTable.setParmValue(updateParm);
	}

	/**
	 * �Ƴ�Ҫ�޸ĵ�ҽ��
	 */
	public void removeUpdateSysFee() {
		int row = upTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫ�Ƴ�������");
			return;
		}
		TParm updateParm = upTable.getParmValue();
		updateParm.removeRow(row);
		upTable.setParmValue(updateParm);
	}

	/**
	 * table ����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		upTableRow = row;
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (this.emptyTextCheck("TXT_ORDER_CODE")) {
			return;
		}

	}

	/**
	 * ��÷���ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parmReturn = (TParm) obj;
		// ��ѯ����ҽ����Ӽ���ҽ��ϸ��
		// ��ѯϸ�����ݣ����ϸ��
		orderCode = parmReturn.getValue("ORDER_CODE");
		this.setValue("TXT_ORDER_CODE", parmReturn.getValue("ORDER_CODE"));
	}

	/**
	 * ��÷���ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popInsReturn(String tag, Object obj) {
		TParm parmReturn = (TParm) obj;
		// ��ѯ����ҽ����Ӽ���ҽ��ϸ��
		// ��ѯϸ�����ݣ����ϸ��
		this.setValue("INS_CODE", parmReturn.getValue("ORDER_CODE"));
	}

	/***
	 * ��ѯ��Ŀ�ֵ�����
	 */
	public void onRuleQuery() {
		TParm parm = new TParm();
		if (this.getValue("INS_CODE").toString().length() > 0) {
			parm.setData("XMBM", this.getValue("INS_CODE"));
		}
		TParm ruleParm = INSRuleTXTool.getInstance().selectINSRule(parm);
		if (ruleParm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			return;
		}
		ruleTable.setParmValue(ruleParm);
		// ( (TTable) getComponent("ruleTable")).setParmValue(ruleParm);
	}

	/**
	 * סԺҽ���޸�
	 */
	public void nhiSaveI() {
		tempSave("NHI_CODE_I");
	}

	/**
	 * ����ҽ���޸�
	 */
	public void nhiSaveO() {
		tempSave("NHI_CODE_O");
	}

	/**
	 * ����ҽ���޸�
	 */
	public void nhiSaveE() {
		tempSave("NHI_CODE_E");
	}

	private void tempSave(String nhiCode) {
		int ruleRow = ruleTable.getSelectedRow();
		int upRow = upTable.getSelectedRow();
		if (ruleRow < 0) {
			this.messageBox("��ѡ����Ŀ�ֵ������");
			return;
		}
		if (upRow < 0) {
			this.messageBox("��ѡ��Ҫ�޸ĵ�����");
			return;
		}
		TParm upParm = upTable.getParmValue();// ����޸ĵ�����
		TParm ruleParm = ruleTable.getParmValue();// �����Ŀ�ֵ�����
		upParm.setData(nhiCode, upRow, ruleParm.getValue("SFXMBM", ruleRow));
		upParm.setData("NHI_FEE_DESC", upRow, ruleParm
				.getValue("XMMC", ruleRow)); // ҽ������
		upParm.setData("NHI_PRICE", upRow, ruleParm.getValue("ZGXJ", ruleRow)); // ҽ���۸�
		upParm
				.setData("INSPAY_TYPE", upRow, ruleParm.getValue("XMLB",
						ruleRow)); // ҽ�����
		upTable.setParmValue(upParm);
		upTable.setSelectedRow(upRow);
	}

	/**
	 * �Ƴ�Ҫ�޸ĵ�ҽ������
	 */
	public void removeInsCode() {
		int upRow = upTable.getSelectedRow();
		if (upRow < 0) {
			this.messageBox("��ѡ��Ҫ�޸ĵ�����");
			return;
		}
		removeInsCode("NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;NHI_FEE_DESC;NHI_PRICE;INSPAY_TYPE", upRow);
	}

	/**
	 * �Ƴ�Ҫ�޸ĵ�ҽ������
	 * 
	 * @param code
	 * @param upRow
	 */
	private void removeInsCode(String  code, int upRow) {
		String [] name=code.split(";");
		TParm upParm = upTable.getParmValue();// ����޸ĵ�����
		for (int i = 0; i < name.length; i++) {
			upParm.setData(name[i], upRow, "");
		}
		upTable.setParmValue(upParm);
	}
	/**
	 * ����Ҫ�޸ĵ�����
	 */
	public void onSave(){
		if (upTable.getParmValue().getCount()<=0) {
			this.messageBox("û��Ҫ�޸ĵ�����");
			return ;
		}
		TParm parm =upTable.getParmValue();
		TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
		"updateINSToSysFee", parm);
		if (result.getErrCode()<0) {
			this.messageBox("E0005");//ִ��ʧ��
		}else{
			this.messageBox("P0005");//ִ�гɹ�
			onClear();
		}	
	}
	/**
	 * ��շ���
	 */
	public void onClear(){
		upTable.removeRowAll();
		ruleTable.removeRowAll();
		orderCode=null;
		this.clearValue("TXT_ORDER_CODE;INS_CODE");
	}
}
