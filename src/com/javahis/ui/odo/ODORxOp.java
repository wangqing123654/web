package com.javahis.ui.odo;

import java.awt.Component;
import java.util.Map;

import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ����ǩ�ӿڴ��ô���ǩʵ����
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ����ǩ�ӿڴ��ô���ǩʵ����
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODORxOp implements IODORx{
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	ODOMainPat odoMainPat;
	TTable table;
	public static final String TABLE_OP = "TABLEOP"; // ������Ŀ��
	public final static String OP_RX = "OP_RX";
	public static final String OP = "4";
	public static final int OP_INT = 4;
	public static final int TABBEDPANE_INDEX = 1;
	public static final String ORDERCAT1TYPE = "TRT";
	public static final String AMT_TAG = "OP_AMT";
	
	public static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	private static final String NULLSTR = "";
	
	/**
	 * ��ʼ������
	 */
	private void initOp() throws Exception{
		boolean isInit = odoMainOpdOrder.isTableInit(TABLE_OP);
		String rxNo = NULLSTR;
		if (!isInit) {
			rxNo = odoMainOpdOrder.initRx(OP_RX, OP);
			odoMainOpdOrder.setTableInit(TABLE_OP, true);
		} else {
			rxNo = odoMainControl.getValueString(OP_RX);
		}
		// System.out.println("rxNo================="+rxNo);
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_OP, isInit))
			odoMainControl.messageBox("E0027"); // ��ʼ������ʧ��
		odoMainOpdOrder.onChangeRx(OP);
	}

	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initOp();
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
	public void onOpCreateEditComponent(Component com, int row, int column) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_OP);
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		odoMainOpdOrder.tableName = TABLE_OP;
		odoMainOpdOrder.rxName = OP_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 4);
		//add by lx 2014/03/17  ����ҽ��ר��   ��Ŀ
		parm.setData("USE_CAT", "1");
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOpReturn");
	}

	/**
	 * ����ֵ�ı��¼��������Ǯ
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onOpValueChange(TTableNode tNode) throws Exception{
		int row = tNode.getRow();
		TTable table = (TTable) odoMainControl.getComponent("TABLEOP");
		String columnName = table.getParmMap(tNode.getColumn());
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {// pangben ====2012 2-28
			return true;
		}

		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue(NULLSTR);
			return false;
		} else {
			String orderCode = odoMainControl.odo.getOpdOrder().getItemString(row,
					"ORDER_CODE");
			if ("LINK_NO".equalsIgnoreCase(columnName)) {
				int value = TypeTool.getInt(tNode.getValue());
				int oldValue = TypeTool.getInt(tNode.getOldValue());
				if (oldValue == 0 && value > 0) {
					return true;
				}
			}
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}
		// ����ҽ�� �����޸�ϸ��Ҫ��֮�޸�
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // ����
				"FREQ_CODE".equalsIgnoreCase(columnName) || // Ƶ��
				"TAKE_DAYS".equalsIgnoreCase(columnName))) { // �շ�
			// �жϸ��������Ƿ��Ǽ���ҽ������ ��������� ��ôѭ���޸�ϸ��
			if ("Y".equalsIgnoreCase(order.getItemString(row, "SETMAIN_FLG"))) {
				String rxNo = order.getItemString(row, "RX_NO");
				String ordersetCode = order.getItemString(row, "ORDER_CODE");
				String orderSetGroup = order.getItemString(row,
						"ORDERSET_GROUP_NO");
				for (int i = 0; i < order.rowCountFilter(); i++) {
					// �ж��Ƿ������ݸ������ϸ��
					if (rxNo.equals(order.getItemData(i, "RX_NO", order.FILTER))
							&& ordersetCode.equals(order.getItemData(i,
									"ORDERSET_CODE", order.FILTER))
							&& orderSetGroup.equals(TypeTool.getString(order
									.getItemData(i, "ORDERSET_GROUP_NO",
											order.FILTER)))) {
						if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
							order.setItem(i, "MEDI_QTY", tNode.getValue(),
									order.FILTER);
						}
						if ("FREQ_CODE".equalsIgnoreCase(columnName))
							order.setItem(i, "FREQ_CODE", tNode.getValue(),
									order.FILTER);

						if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
							// this.messageBox_(tNode.getValue());
							order.setItem(i, "TAKE_DAYS", tNode.getValue(),
									order.FILTER);
						}
					}
				}
			}
		}
		//ִ�п����޸�ϸ�� zhangp 20130930 start
		if ("EXEC_DEPT_CODE".equalsIgnoreCase(columnName)
				&& "Y".equalsIgnoreCase(order.getItemString(row,
								"SETMAIN_FLG"))) {
			String rxNo = order.getItemString(row, "RX_NO");
			String ordersetCode = order.getItemString(row, "ORDER_CODE");
			String orderSetGroup = order
					.getItemString(row, "ORDERSET_GROUP_NO");
			for (int i = 0; i < order.rowCountFilter(); i++) {
				if (rxNo.equals(order.getItemData(i, "RX_NO", order.FILTER))
						&& ordersetCode.equals(order.getItemData(i,
								"ORDERSET_CODE", order.FILTER))
						&& orderSetGroup.equals(TypeTool.getString(order
								.getItemData(i, "ORDERSET_GROUP_NO",
										order.FILTER)))) {
					order.setItem(i, "EXEC_DEPT_CODE", tNode.getValue(),
							order.FILTER);
				}
			}
		}
		//ִ�п����޸�ϸ�� zhangp 20130930 end
		odoMainOpdOrder.calculateCash(TABLE_OP, AMT_TAG);
		return false;
	}

	/**
	 * ��������
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOpReturn(String tag, Object obj) throws Exception{
		TParm sysFee = (TParm) obj;
		
		String rxNo;
		if (StringUtil.isNullString(odoMainOpdOrder.tableName)) {
			odoMainControl.messageBox("E0034");
			return;
		}
		if(!TABLE_OP.equals(odoMainOpdOrder.tableName)){
			odoMainOpdOrder.tableName = TABLE_OP;
		}
		TTable table = (TTable) odoMainControl.getComponent(odoMainOpdOrder.tableName);
		int row = table.getSelectedRow();
		
		int oldRow = row;
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!odoMainControl.odoMainReg.canEdit()) {
			table.setDSValue(row);
			odoMainControl.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (odoMainControl.odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE") != null
				&& odoMainControl.odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE").trim()
						.length() > 0) {
			odoMainControl.messageBox("E0045"); // �ѿ���ҽ�����ɱ������ɾ����ҽ�����¿���
			table.setDSValue(row);
			return;
		}
		int column = table.getSelectedColumn();
		String code = sysFee.getValue("ORDER_CODE");
		table.acceptText();
		rxNo = (String) odoMainControl.getValue(odoMainOpdOrder.rxName);
		odoMainOpdOrder.rxType = "4";
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		
		
		// ============pangben 2012-2-29 ��ӹܿ�
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
				return;
			}
		}
		// ============pangben 2012-2-29 stop
		if (order.isSameOrder(code)) {
			if (odoMainControl.messageBox("��ʾ��Ϣ/Tip",
					"��ҽ���Ѿ��������Ƿ������\r\n/This order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt(NULLSTR, oldRow, column);
				return;
			}
		}
		odoMainControl.odo.getOpdOrder().newOpOrder(rxNo, code, odoMainControl.odoMainPat.ctz, row, false, "", "");
		// odo.getOpdOrder().showDebug();
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(odoMainOpdOrder.rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(odoMainOpdOrder.rxType, rxNo);
		}		
		
		System.out.println("-----ORDER_CODE-------"+sysFee.getValue("ORDER_CODE"));
		System.out.println("-----CAT1_TYPE-------"+sysFee.getValue("CAT1_TYPE"));
		//���ó�  STAT
		//int row1 = order.rowCount() - 1;
		if ("TRT".equalsIgnoreCase(sysFee.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(sysFee.getValue("CAT1_TYPE"))) {
			order.setItem(row, "FREQ_CODE", "STAT");
		}
		//
		
		odoMainOpdOrder.calculateCash(TABLE_OP, AMT_TAG);
		odoMainOpdOrder.initNoSetTable(rxNo, odoMainOpdOrder.tableName, false);
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map insColor = OdoUtil.getInsColor(odoMainControl.odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		table.setRowTextColorMap(insColor);
	}

	/**
	 * ɾ��һ�д���ҽ��
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowOp(OpdOrder order ,int row,TTable table)throws Exception{
		//delete by huangtt 20150731
//		if (!odoMainOpdOrder.checkSendPah(order, row))// ����ѷ�ҩ�ѵ���
//			return false;
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
			return false;
		}
		
		String rxNo = odoMainControl.getValueString("OP_RX");
		String orderSetCode = table.getItemString(row, "ORDERSET_CODE");
		int groupNo = table.getItemInt(row, "ORDERSET_GROUP_NO");
		String orderCode = table.getItemString(row, "ORDER_CODE");
		order.deleteOrderSet(rxNo, orderSetCode, groupNo, orderCode, table
				.getItemString(row, "SEQ_NO"));
		table.setDSValue();
		Map insColor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		odoMainOpdOrder.calculateCash(odoMainOpdOrder.tableName, AMT_TAG);
		return true;
	}

	@Override
	public void insertPack(TParm parm, boolean flg) throws Exception{
		// TODO Auto-generated method stub
		insertOpPack(parm);
	}
	
	/**
	 * ���봦��ģ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOpPack(TParm parm) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_OP);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxNo = odoMainControl.getValueString(OP_RX); // ����ǩ��
		if (StringUtil.isNullString(rxNo)) {
			odoMainOpdOrder.initRx(OP_RX, OP);
		}
		rxNo = odoMainControl.getValueString("OP_RX");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		// ��ȡ�ش�ֵ�ĸ���
		int count = parm.getCount("ORDER_CODE");
		// ѭ������ÿ�� ������Ŀ
		for (int i = 0; i < count; i++) {
			boolean memPackageFlg = "Y".equals(parm.getValue("MEM_PACKAGE_FLG", i));//�ײ�
			int row = -1;
			if (!StringUtil.isNullString(order.getItemString(
					order.rowCount() - 1, "ORDER_CODE"))) {
				row = order.newOrder(OP, rxNo);
			} else {
				row = order.rowCount() - 1;
			}
			order.itemNow = true;
			order.sysFee.setFilter("ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i) + "'");
			order.sysFee.filter();
			TParm sysFeeParm = order.sysFee.getRowParm(0);
			String orderCode = sysFeeParm.getValue("ORDER_CODE");
			// �½�һ�� ������Ŀ
			order.newOpOrder(rxNo, orderCode, odoMainPat.ctz, row, memPackageFlg, parm.getValue("ID", i), parm.getValue("TRADE_NO", i));
			// �ж�ģ�崫�ص���Ϣ���Ƿ��� ִ�п���
			// �����ִ�п��� ��ô��ģ���е�ִ�п���
			String execDept = NULLSTR;
			if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parm.getValue("EXEC_DEPT_CODE", i);
			}
			// ���ģ����û��ִ�п��� ��ôʹ��sys_fee�е�ִ�п���
			else if (!StringUtil.isNullString(sysFeeParm
					.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
			} else { // ���sys_fee��Ҳû��ִ�п��� ��ôʹ�õ�ǰ�û������ڿ���
				execDept = Operator.getDept();
			}
			order.setItem(row, "EXEC_DEPT_CODE", execDept); // ִ�п���
			order.setItem(row, "ORDER_DESC", sysFeeParm.getValue("ORDER_DESC")
					.replaceFirst(
							"(" + sysFeeParm.getValue("SPECIFICATION") + ")",
							NULLSTR)); // ҽ������
			order.setItem(row, "CTZ1_CODE", odoMainPat.ctz[0]);
			order.setItem(row, "CTZ2_CODE", odoMainPat.ctz[1]);
			order.setItem(row, "CTZ3_CODE", odoMainPat.ctz[2]);
			order.itemNow = false; // ������������
			order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
			if(memPackageFlg){
				order.itemNow = true;
				order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
				order.setItem(row, "DOSAGE_QTY", parm.getValue("MEDI_QTY", i));
				order.setItem(row, "DISPENSE_QTY", parm.getValue("MEDI_QTY", i));
				order.itemNow = false;
			}
			order.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
			order.setItem(row, "TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
			order
					.setItem(row, "LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",
							i));
			order.setItem(row, "LINK_NO", parm.getValue("LINK_NO", i));
			if(memPackageFlg){
				order.setItem(row, "AR_AMT", parm.getValue("AR_AMT", i));
				order.setItem(row, "PAYAMOUNT",order.getItemDouble(row, "OWN_AMT")-ODOTool.getInstance().roundAmt(order.getItemDouble(row, "AR_AMT")));
				order.setItem(row, "MEM_PACKAGE_ID", parm.getValue("ID", i));
				order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130
			}
			order.setItem(row, "DR_NOTE", parm.getValue("DESCRIPTION",i));  //add by huangtt 20141114
			order.setActive(row, true);
		}
		order.newOrder(OP, rxNo);
		odoMainOpdOrder.initNoSetTable(rxNo, TABLE_OP, false);
		// table.getTable().grabFocus();
		Map insColor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		odoMainOpdOrder.calculateCash(TABLE_OP, "OP_AMT");
	}

	@Override
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception{
		// TODO Auto-generated method stub
		this.odoMainOpdOrder = odoMainOpdOrder;
		this.odoMainControl = odoMainOpdOrder.odoMainControl;
		this.odoMainPat = odoMainOpdOrder.odoMainPat;
		// ������ĿTABLE
		odoMainOpdOrder.tblOp = (TTable) odoMainControl.getComponent(TABLE_OP);
		this.table = odoMainOpdOrder.tblOp;
		table.addEventListener(TABLE_OP + "->" + TTableEvent.CHANGE_VALUE,
				this, "onOpValueChange");
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onOpCreateEditComponent");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
	}
	
	/**
	 * ���õ�checkBox�¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj)throws Exception{
		return odoMainOpdOrder.onCheckBox(obj);
	}

	@Override
	public void onChangeRx(String rxType) throws Exception{
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		rxNo = odoMainControl.getValueString(OP_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.odo.getOpdOrder().setFilter(
					"RX_TYPE='" + OP + "' AND ORDER_CODE <>''");
			odoMainControl.odo.getOpdOrder().filter();
			table.setDSValue();
			odoMainOpdOrder.calculateCash(TABLE_OP, "OP_AMT");
			return;
		}
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_OP, false))
			odoMainControl.messageBox("E0038"); // ��ʾ����ʧ��
		odoMainOpdOrder.calculateCash(TABLE_OP, "OP_AMT");
	}

	@Override
	public boolean medAppyCheckDate(OpdOrder order, int row) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TTable getTable() throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean initTable(String rxNo) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insertOrder(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCheckRxNoSum(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		TParm opResult = ((TParm) parm.getData("OP"));
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			String rxNo = odoMainControl.getValueString(OP_RX); // ����ǩ��
			if (null==rxNo || rxNo.length()<=0) {
				return "������Ŀû�г�ʼ������ǩ";
			}
			if (!order.isExecutePrint(odoMainControl.odo.getCaseNo(), rxNo)) {
				return "�Ѿ���Ʊ��������Ŀ����ǩ���������ҽ��";
			}
		}
		return null;
	}

	@Override
	public void getTempSaveParm() throws Exception{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setopdRecipeInfo(OpdOrder order) throws Exception{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteOrderList(int rxType) throws Exception {
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		String tableName = NULLSTR;
		int count = -1;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String oldfilter = order.getFilter();
		StringBuffer billFlg=new StringBuffer();//�ж��Ƿ����ɾ�� ��ͬһ�Ŵ���ǩ�е�״̬����ͬ����ɾ��
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		rxNo = (String) odoMainControl.getValue("OP_RX");
		tableName = TABLE_OP;
		odoMainControl.setValue("OP_AMT", NULLSTR);
		oldfilter = order.getFilter();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		count = order.rowCount();
		table = (TTable) odoMainControl.getComponent(TABLE_OP);
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
				order.setFilter(oldfilter);
				order.filter();
				table.setDSValue();
				return;
			} 
			if(!odoMainOpdOrder.deleteSumRxOrder(order, i, billFlg)){
				return;
			}
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			order.deleteRow(i);
		}
		order.setFilter(oldfilter);
		order.filter();
		table.setDSValue();
	}

	@Override
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception {
		// TODO Auto-generated method stub
		return deleteRowOp(order, row, table);
	}

	@Override
	public void deleteorderAuto(int row) throws Exception {
		// TODO Auto-generated method stub
		TTable table = null;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = NULLSTR;
		table = (TTable) odoMainControl.getComponent(TABLE_OP);
		rx_no = odoMainControl.getValueString(OP_RX);
		order.deleteRow(row, buff);
		table.setFilter("RX_TYPE='" + OP + "' AND HIDE_FLG='N' AND RX_NO='"
				+ rx_no + "'");
		table.filter();
		table.setDSValue();
	}

	@Override
	public boolean check(OpdOrder order, int row, String name,
			boolean spcFlg) throws Exception {
		// TODO Auto-generated method stub
		return false;
		
	}

	@Override
	public void setOnValueChange(int inRow, String execDept,
			String orderCodeFinal, String columnNameFinal,final double oldDosageQty) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertExa(TParm parm, int row, int column) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSortRx(boolean flg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChnChange(String fieldName, String type) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
