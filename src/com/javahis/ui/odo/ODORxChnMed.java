package com.javahis.ui.odo;

import java.awt.Component;

import javax.swing.SwingUtilities;

import jdo.bil.BIL;
import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站处方签接口中药处方签实现类
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站处方签接口中药处方签实现类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODORxChnMed implements IODORx{
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	ODOMainPat odoMainPat;
	
	public TTable table;
	public static final String TABLE_CHN = "TABLECHN"; // 中药饮片表
	public final static String CHN_RX = "CHN_RX";
	public static final String CHN = "3";
	public static final int CHN_INT = 3;
	public static final int TABBEDPANE_INDEX = 3;
	public static final String ORDERCAT1TYPE = "PHA_G";
	public static final String AMT_TAG = "CHN_AMT";
	
	private static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	private static final String URLPHAREDRUGMSG = "%ROOT%\\config\\pha\\PHAREDrugMsg.x";
	
	private static final String NULLSTR = "";
	
	/**
	 * 初始化中药
	 */
	private void initChnMed() throws Exception{
		boolean isInit = odoMainOpdOrder.isTableInit(TABLE_CHN);
		// System.out.println("isInit-----"+isInit);
		odoMainOpdOrder.rxType = CHN;
		String rxNo = odoMainControl.getValueString(CHN_RX);
		// System.out.println("CHN_RX-----"+CHN_RX);
		if (!isInit) {
			rxNo = odoMainOpdOrder.initRx(CHN_RX, CHN);
			odoMainOpdOrder.setTableInit(TABLE_CHN, true);

		} else {
			rxNo = odoMainControl.getValueString(CHN_RX);

		}
		if (StringUtil.isNullString(odoMainControl.odo.getOpdOrder().getItemString(0,
				"ORDER_CODE"))) {
			odoMainControl.setValue("DCT_TAKE_DAYS", odoMainOpdOrder.dctTakeDays);
			odoMainControl.setValue("DCT_TAKE_QTY", odoMainOpdOrder.dctMediQty);
			odoMainControl.setValue("CHN_FREQ_CODE", odoMainOpdOrder.dctFreqCode);
			odoMainControl.setValue("CHN_ROUTE_CODE", odoMainOpdOrder.dctRouteCode);
			odoMainControl.setValue("DCTAGENT_CODE", odoMainOpdOrder.dctAgentCode);
			odoMainControl.setValue("DR_NOTE", NULLSTR);
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", odoMainOpdOrder.phaCode);
			odoMainControl.setValue("URGENT_FLG", NULLSTR);
			odoMainControl.setValue("RELEASE_FLG", NULLSTR);
		} else {
			odoMainControl.setValue("DCT_TAKE_DAYS", odoMainControl.odo.getOpdOrder().getItemInt(0,
					"TAKE_DAYS"));
			odoMainControl.setValue("DCT_TAKE_QTY", odoMainControl.odo.getOpdOrder().getItemDouble(0,
					"DCT_TAKE_QTY"));
			odoMainControl.setValue("CHN_FREQ_CODE", odoMainControl.odo.getOpdOrder().getItemString(0,
					"FREQ_CODE"));
			odoMainControl.setValue("CHN_ROUTE_CODE", odoMainControl.odo.getOpdOrder().getItemString(0,
					"ROUTE_CODE"));
			odoMainControl.setValue("DCTAGENT_CODE", odoMainControl.odo.getOpdOrder().getItemString(0,
					"DCTAGENT_CODE"));
			odoMainControl.setValue("DR_NOTE", odoMainControl.odo.getOpdOrder().getItemString(0,
					"DR_NOTE"));
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", odoMainControl.odo.getOpdOrder()
					.getItemString(0, "EXEC_DEPT_CODE"));
			odoMainControl.setValue("URGENT_FLG", odoMainControl.odo.getOpdOrder().getItemString(0,
					"URGENT_FLG"));
			odoMainControl.setValue("RELEASE_FLG", odoMainControl.odo.getOpdOrder().getItemString(0,
					"RELEASE_FLG"));
		}
		this.initChnTable(rxNo);
		odoMainOpdOrder.onChangeRx(CHN);
	}

	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initChnMed();
	}
	
	/**
	 * 初始化中药处方签
	 * 
	 * @param rxNo
	 *            String
	 * @return boolean
	 */
	public boolean initChnTable(String rxNo) throws Exception{
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.messageBox("E0029"); // 没有处方签
			return false;
		}
		TTable table = (TTable) odoMainControl.getComponent(TABLE_CHN);
		
		String filter = "RX_NO='" + rxNo + "'";
		
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		
		
		order.setFilter(filter);
		if (!order.filter()) {
			odoMainControl.messageBox("E0030"); // 无此药品
			return false;
		}
		int totRow = order.rowCount();
		
		if (!StringUtil.isNullString(order.getItemString(totRow - 1,
				"ORDER_CODE"))
				|| totRow % 4 != 0 || totRow < 1) {
			for (int i = 0; i < 4 - totRow % 4; i++) {
				if (order.newOrder("3", rxNo) == -1) {
					odoMainControl.messageBox("E0031"); // 显示中药失败
					return false;
				}
				order.setItem(i, "PHA_TYPE", "G");
			}
		}
		if (!order.filter()) {
			odoMainControl.messageBox("E0031"); // 显示中药失败
			return false;
		}
		TParm parm = odoMainControl.odo.getOpdOrder().getBuffer(order.PRIMARY);
		
		
		TParm tableParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			int idx = i % 4 + 1;
			tableParm.addData("ORDER_DESC" + idx, parm
					.getValue("ORDER_DESC", i));
			tableParm.addData("MEDI_QTY" + idx, parm.getDouble("MEDI_QTY", i));
			tableParm.addData("DCTEXCEP_CODE" + idx, parm.getValue(
					"DCTEXCEP_CODE", i));
		}
		
		this.table.setParmValue(tableParm);
		
		if (!StringUtil.isNullString(parm.getValue("ORDER_CODE", 0))) {
			odoMainControl.setValue("DCT_TAKE_DAYS", parm.getDouble("TAKE_DAYS", 0));
			odoMainControl.setValue("DCT_TAKE_QTY", parm.getDouble("DCT_TAKE_QTY", 0));
			odoMainControl.setValue("CHN_FREQ_CODE", parm.getValue("FREQ_CODE", 0));
			odoMainControl.setValue("CHN_ROUTE_CODE", parm.getValue("ROUTE_CODE", 0));
			odoMainControl.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE", 0));
			odoMainControl.setValue("DR_NOTE", parm.getValue("DR_NOTE", 0));
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",
					0));
			odoMainControl.setValue("URGENT_FLG", parm.getValue("URGENT_FLG", 0));
			odoMainControl.setValue("RELEASE_FLG", parm.getValue("RELEASE_FLG", 0));
		} else {
			odoMainControl.setValue("DCT_TAKE_DAYS", odoMainOpdOrder.dctTakeDays);
			odoMainControl.setValue("DCT_TAKE_QTY", odoMainOpdOrder.dctMediQty);
			odoMainControl.setValue("CHN_FREQ_CODE", odoMainOpdOrder.dctFreqCode);
			odoMainControl.setValue("CHN_ROUTE_CODE", odoMainOpdOrder.dctRouteCode);
			odoMainControl.setValue("DCTAGENT_CODE", odoMainOpdOrder.dctAgentCode);
			odoMainControl.setValue("DR_NOTE", NULLSTR);
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", odoMainOpdOrder.phaCode);
			odoMainControl.setValue("URGENT_FLG", NULLSTR);
			odoMainControl.setValue("RELEASE_FLG", NULLSTR);
		}
		setChnPckTot();
		odoMainOpdOrder.calculateChnCash(rxNo);
		return true;
	}
	
	
	/**
	 * 设置中药显示的总克数
	 */
	public void setChnPckTot() throws Exception{
		long amt = 0;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		for (int i = 0; i < order.rowCount(); i++) {
			amt += order.getItemDouble(i, "MEDI_QTY");
		}
		
		for (int i = 0; i < order.rowCount(); i++) {
			order.setItem(i, "PACKAGE_TOT", amt);
		}
		
		odoMainControl.setValue("PACKAGE_TOT", amt);	
	}
	
	/**
	 * 添加SYS_FEE弹出窗口
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onChnCreateEditComponent(Component com, int row, int column) throws Exception{
		if (column != 0 && column != 3 && column != 6 && column != 9) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		odoMainOpdOrder.tableName = TABLE_CHN;
		odoMainOpdOrder.rxName = CHN_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 3);
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popChnOrderReturn");
	}

	/**
	 * 中医TABLE值赋值操作
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	private boolean setOnChnValueChange(TTableNode tNode, OpdOrder order,
			int realrow, int row, final String execDept,
			final String columnNameFinal,final double oldDosageQty) throws Exception{
		if (chaCheck(order, realrow, row)) {
			return true;
		}
		final int inRow = realrow;
		final String orderCodeFinal = order.getItemString(realrow,
				"ORDER_CODE");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					String orgCode = odoMainControl.getValueString("CHN_EXEC_DEPT_CODE");
					if(ODOTool.getInstance().getPhaStockFlg(orgCode)){
						if (!odoMainOpdOrder.checkStoreQty(inRow, execDept, orderCodeFinal,
								columnNameFinal,oldDosageQty)) {
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		order.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
				.getValue()));
		return false;
	}
	
	/**
	 * 中医TABLE值赋值操作
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	private boolean setOnchnValueChange(TTableNode tNode, OpdOrder order,
			int realrow, int row) throws Exception{
		if (chaCheck(order, realrow, row)) {
			return true;
		}
		String orderCode = order.getItemString(realrow, "ORDER_CODE");
		if (StringUtil.isNullString(orderCode)) {
			return true;
		}
		order.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform
				.getString(tNode.getValue()));
		return false;
	}
	
	/**
	 * 中医TABLE值改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChnValueChange(TTableNode tNode) throws Exception{
		int column = tNode.getColumn();
		int row = tNode.getRow();
		String colName = tNode.getTable().getParmMap(column);
		TTable table = (TTable) odoMainControl.getComponent(TABLE_CHN);
		// ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		if ("ORDER_DESC1".equalsIgnoreCase(colName)
				|| "ORDER_DESC2".equalsIgnoreCase(colName)
				|| "ORDER_DESC3".equalsIgnoreCase(colName)
				|| "ORDER_DESC4".equalsIgnoreCase(colName)) {
			tNode.setValue(NULLSTR);
			return true;
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxNo = odoMainControl.getValueString(CHN_RX);
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int realrow;
		final String execDept = odoMainControl.getValueString("CHN_EXEC_DEPT_CODE");
		final String columnNameFinal = "MEDI_QTY";
		switch (column) {
		case 1:
			realrow = row * 4 + 0;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 4:
			realrow = row * 4 + 1;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 7:
			realrow = row * 4 + 2;
			if (chaCheck(order, realrow, row)) {
				return true;
			}
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 10:
			realrow = row * 4 + 3;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 2:
			realrow = row * 4 + 0;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 5:
			realrow = row * 4 + 1;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 8:
			realrow = row * 4 + 2;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 11:
			realrow = row * 4 + 3;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		}
		setChnPckTot();
		odoMainOpdOrder.calculateChnCash(rxNo);
		this.initChnTable(rxNo);
//		TTable table = (TTable) odoMainControl.getComponent("TABLECHN");
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		int nextColumn = -1;
		if (column < 3 && column > -1) {
			nextColumn = 2;
		} else if (column < 6 && column > 2) {
			nextColumn = 5;
		} else if (column < 9 && column > 5) {
			nextColumn = 8;
		} else {
			nextColumn = 11;
		}
		table.setSelectedColumn(column);
		
		return false;
	}
	
	/**
	 * 中医一张处方签的所有医嘱的给定字段的值改变事件
	 * 
	 * @param fieldName
	 *            String 要改变的字段名
	 * @param type
	 *            String
	 */
	public void onChnChange(String fieldName, String type) throws Exception{
		if (odoMainControl.odo == null)
			return;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxNo = odoMainControl.getValueString(CHN_RX);

		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		String value = odoMainControl.getValueString(fieldName);
		if ("CHN_FREQ_CODE".equalsIgnoreCase(fieldName)) {
			fieldName = "FREQ_CODE";
		}
		if ("CHN_ROUTE_CODE".equalsIgnoreCase(fieldName)) {
			TComboBox t = (TComboBox) odoMainControl.getComponent(fieldName);
			value = t.getSelectedID();
			fieldName = "ROUTE_CODE";
		}
		if ("DCT_TAKE_DAYS".equalsIgnoreCase(fieldName)) {
			fieldName = "TAKE_DAYS";
		}
		// 判断煎药方式是否填写 不可为空
		if ("DCTAGENT_CODE".equals(fieldName)) {
			if (odoMainControl.getValueString("DCTAGENT_CODE").length() == 0) {
				odoMainControl.messageBox("E0190");
				odoMainControl.setValue("DCTAGENT_CODE", odoMainOpdOrder.dctAgentCode);
				return;
			}
		}
		int count = order.rowCount();
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC"))) {
				continue;
			}
			if ("E".equals(order.getItemString(i, "BILL_TYPE"))
					&& (StringTool.getBoolean(order
							.getItemString(i, "EXEC_FLG"))
							|| StringTool.getBoolean(order.getItemString(i,
									"PRINT_FLG")) || StringTool
							.getBoolean(order.getItemString(i, "BILL_FLG")))) {
				odoMainControl.messageBox("E0055"); // 已计费医嘱不能删除
				return;
			} else {
				if (StringTool.getBoolean(order.getItemString(i, "BILL_FLG"))
						&& !"E".equals(order.getItemString(i, "BILL_TYPE"))) {
					odoMainControl.messageBox("E0055"); // 已计费医嘱不能删除
					return;
				}
			}
			if ("string".equalsIgnoreCase(type)
					|| StringUtil.isNullString(type)) {
				order.setItem(i, fieldName, value);
			} else {
				order.setItem(i, fieldName, value);
			}
		}
	}

	/**
	 * 中药医嘱校验
	 * 
	 * @param order
	 * @param realrow
	 * @param row
	 * @return
	 */
	private boolean chaCheck(OpdOrder order, int realrow, int row) throws Exception{
		TParm parm=new TParm();
		if (!order.checkDrugCanUpdate("CHN", realrow,parm,true)) { // 判断是否可以修改（有没有进行审,配,发）
			odoMainControl.messageBox(parm.getValue("MESSAGE"));
			return true;
		}
		if (odoMainOpdOrder.odoRxMed.check(order, row, "CHN",true)) {
			return true;
		}
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {// pangben ====2012
			// 2-28//pangben
			// ====2012 2-28
			return true;
		}
		return false;
	}

	/**
	 * 新增中医
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popChnOrderReturn(String tag, Object obj) throws Exception{
		// 判断是否已经超过看诊时限
		if (!odoMainControl.odoMainReg.canEdit()) {
			odoMainControl.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if(!TABLE_CHN.equals(odoMainOpdOrder.tableName)){
			odoMainOpdOrder.tableName = TABLE_CHN;
		}
		TTable table = (TTable) odoMainControl.getComponent(odoMainOpdOrder.tableName);
		TParm parm = (TParm) obj;
		//=====pangben 2015-4-1 添加中草药医生站开立校验
		if (TABLE_CHN.equals(odoMainOpdOrder.tableName)) {
			if (ODOMainReg.O.equalsIgnoreCase(odoMainControl.odoMainReg.admType)) {
				// 判断是否住院适用医嘱
				if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
					// 不是门诊适用医嘱！
					odoMainControl.messageBox("不是门诊适用医嘱。");
					return;
				}
			}
			// 急诊
			if (ODOMainReg.E.equalsIgnoreCase(odoMainControl.odoMainReg.admType)) {
				if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
					// 不是门诊适用医嘱！
					odoMainControl.messageBox("不是急诊适用医嘱。");
					return;
				}
			}
		}
		String rxNo;
		if (StringUtil.isNullString(odoMainOpdOrder.tableName)) {
			odoMainControl.messageBox("E0034");
			return;
		}
		if (StringUtil.isNullString(odoMainControl.getValueString("CHN_EXEC_DEPT_CODE"))) {
			odoMainControl.messageBox("E0053");
			TTextFormat t = (TTextFormat) odoMainControl
					.getComponent("CHN_EXEC_DEPT_CODE");
			t.grabFocus();
			return;
		}
		if (StringUtil.isNullString(odoMainControl.getValueString("CHN_FREQ_CODE"))) {
			odoMainControl.messageBox("E0054");
			return;
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
//			//物联网
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
//			if (odoMainOpdOrder.isCheckKC(parm.getValue("ORDER_CODE"))) // 判断是否是“药品备注”
//				if (!INDTool.getInstance().inspectIndStock(
//						odoMainControl.getValueString("CHN_EXEC_DEPT_CODE"),
//						parm.getValue("ORDER_CODE"), 0.0)) {
//					// this.messageBox("E0052");
//					// $$==========add by lx 2012-06-19加入存库不足，替代药提示
//					TParm inParm = new TParm();
//					inParm.setData("orderCode", parm.getValue("ORDER_CODE"));
//					odoMainControl.openDialog(URLPHAREDRUGMSG,
//							inParm);
//					// $$==========add by lx 2012-06-19加入存库不足，替代药提示
//					return;
//				}
		}
		// ============pangben 2012-2-29 添加管控
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
		int row = table.getSelectedRow();
		// int row = order.rowCount() / 4 - 1;
		int oldRow = row;
		int column = table.getSelectedColumn();
		String code = (String) table.getValueAt(row, column);

		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (odoMainControl.messageBox("提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\n/This order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt(code, oldRow, column);
				return;
			}
		}
		table.acceptText();
		int realColumn = 0;
		switch (column) {
		case 0:
			realColumn = 0;
			break;
		case 3:
			realColumn = 1;
			break;
		case 6:
			realColumn = 2;
			break;
		case 9:
			realColumn = 3;
			break;
		}
		int realrow = row * 4 + realColumn;
		rxNo = (String) odoMainControl.getValue(odoMainOpdOrder.rxName);
		// System.out.println("-111111---"+order.getItemString(realrow,
		// "ORDER_CODE"));
		if (!StringUtil
				.isNullString(order.getItemString(realrow, "ORDER_CODE"))) {
			odoMainControl.messageBox("E0040");
			return;
		}
		order.setItem(realrow, "PHA_TYPE", "G");
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			odoMainControl.messageBox("E0034");
			return;
		}
		// System.out.println("realrow----"+realrow);
		order.setActive(realrow, true);
		order.setItem(realrow, "EXEC_DEPT_CODE", odoMainControl
				.getValue("CHN_EXEC_DEPT_CODE"));
		parm.setData("EXEC_DEPT_CODE", odoMainControl.getValue("CHN_EXEC_DEPT_CODE"));
		odoMainOpdOrder.initOrder(order, realrow, parm, parmBase);
		
		setChnPckTot();
		order.setItem(realrow, "RELEASE_FLG", odoMainControl.getValue("RELEASE_FLG"));
		order.setItem(realrow, "DCT_TAKE_QTY", odoMainControl
				.getValueDouble("DCT_TAKE_QTY"));
		order.setItem(realrow, "TAKE_DAYS", odoMainControl
				.getValueDouble("DCT_TAKE_DAYS"));
		order.setItem(realrow, "FREQ_CODE", odoMainControl.getValue("CHN_FREQ_CODE"));
		order.setItem(realrow, "ROUTE_CODE", odoMainControl.getValue("CHN_ROUTE_CODE"));
		order.setItem(realrow, "DCTAGENT_CODE", odoMainControl.getValue("DCTAGENT_CODE"));
		order.setItem(realrow, "DR_NOTE", odoMainControl.getValue("DR_NOTE"));
		//
		order.setItem(realrow, "PRESRT_NO", "1");// 分方号
		//
		odoMainOpdOrder.calculateChnCash(rxNo);
		table.setValueAt(parm.getValue("ORDER_DESC"), table.getSelectedRow(),
				table.getSelectedColumn());
		table.setValueAt(order.getItemDouble(realrow, "MEDI_QTY"), row,
				column + 1);
		table.getTable().grabFocus();
		if (column == 9) {
			String oldFilter = order.getFilter();
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			String laseOrderCode = order.getItemString(order.rowCount() - 1,
					"ORDER_CODE");
			order.setFilter(oldFilter);
			order.filter();
			if (order.getItemString(realrow, "ORDER_CODE").length() > 0
					&& laseOrderCode.length() > 0)
				addChnRow(rxNo, row);
		}
		initChnTable(rxNo);
		table.setSelectedRow(row);
		table.setSelectedColumn(column + 1);
	}
	
	/**
	 * 为中药TABLE新增一行
	 * 
	 * @param rxNo
	 *            处方号
	 * @param row
	 *            行号
	 */
	public void addChnRow(String rxNo, int row) throws Exception{
		if (StringUtil.isNullString(rxNo))
			return;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		int realrow = row * 4;
		for (int i = realrow; i < realrow + 4; i++) {
			order.newOrder(CHN, rxNo);
			order.setItem(i, "PHA_TYPE", "G");
		}
	}

	/**
	 * 删除一行中药医嘱
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowChn(OpdOrder order ,int row,TTable table) throws Exception{
		odoMainOpdOrder.rxType = CHN;
		int column = table.getSelectedColumn();
		int realColumn = 0;
		if (column >= 0 && column <= 2) {
			realColumn = 0;
		} else if (column >= 3 && column <= 5) {
			realColumn = 1;
		} else if (column >= 6 && column <= 8) {
			realColumn = 2;
		} else {
			realColumn = 3;
		}
		String rxNo = odoMainControl.getValueString(CHN_RX);
		int realRow = row * 4 + realColumn;
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
			return false;
		} 
		TParm parm=new TParm();
		if (!order.checkDrugCanUpdate("CHN", realRow,parm,true)) { // 判断是否可以修改（有没有进行审,配,发）
			odoMainControl.messageBox(parm.getValue("MESSAGE"));
			return false;
		}
		if (realRow < order.rowCount()) {
			order.deleteRow(realRow);
		} else {
			return false;
		}
		this.initChnTable(rxNo);	
		return true;
	}

	@Override
	public void insertPack(TParm parm, boolean flg) throws Exception{
		// TODO Auto-generated method stub
		insertChnPack(parm);
	}
	
	/**
	 * 插入中医的模板数据
	 * 
	 * @param parmChn
	 *            TParm
	 */
	private void insertChnPack(TParm parmChn) throws Exception{
		if (parmChn == null) {
			return;
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		// 获取处方签号
		String rxNo = odoMainControl.getValueString(CHN_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainOpdOrder.initRx(CHN_RX, CHN);
		}
		rxNo = odoMainControl.getValueString(CHN_RX);
		odoMainOpdOrder.rxType = CHN;
		int count = parmChn.getCount();
		for (int i = 0; i < count; i++) {
			boolean memPackageFlg = "Y".equals(parmChn.getValue("MEM_PACKAGE_FLG", i));//套餐
			String orderCode = parmChn.getValue("ORDER_CODE", i);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(orderCode)));
			TParm sysFeeParm = sysFee.getRow(0);
			// 判断模板传回的信息中是否有 执行科室
			
			// 如果有执行科室 那么用模板中的执行科室
			String execDept = NULLSTR;
			
			
//			if (!StringUtil.isNullString(parmChn.getValue("EXEC_DEPT_CODE", i))) {
//				execDept = parmChn.getValue("EXEC_DEPT_CODE", i);
//			}
//			// 如果模板中没有执行科室 那么使用sys_fee中的执行科室
//			else if (!StringUtil.isNullString(sysFeeParm
//					.getValue("EXEC_DEPT_CODE"))) {
//				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
//			} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
//				execDept = Operator.getDept();
//			}
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			int row = 0;
			for (int h = 0; h < order.rowCount(); h++) {
				if (!StringUtil.isNullString(order.getItemString(h,
						"ORDER_CODE"))) {
					row = h + 1;
				}
			}
			
			sysFeeParm.setData("EXEC_DEPT_CODE", odoMainControl.getValueString("CHN_EXEC_DEPT_CODE"));
			
//			System.out.println("order::::::::"+order);
//			System.out.println("sysFeeParm::::::::"+sysFeeParm);
//			System.out.println("phaBase::::::::"+phaBase);
			
			odoMainOpdOrder.initOrder(order, row, sysFeeParm, phaBase);
			
			
//			System.out.println("222222222222222222");
			
			
			TParm price = OdoUtil.getPrice(order.getItemString(row,
					"ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			double ownAmt = ODOTool.getInstance().roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = ODOTool.getInstance().roundAmt(BIL.chargeTotCTZ(odoMainPat.ctz[0], odoMainPat.ctz[1], odoMainPat.ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					odoMainControl.serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(odoMainPat.ctz[0], odoMainPat.ctz[1],
					odoMainPat.ctz[2], price.getValue("CHARGE_HOSP_CODE")));
			order.setItem(row, "OWN_AMT", ownAmt);
			
			if(memPackageFlg){
				order.setItem(row, "AR_AMT", parmChn.getValue("AR_AMT", i));
				order.setItem(row, "MEM_PACKAGE_ID", parmChn.getValue("ID"));
				order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130

			}else{
				order.setItem(row, "AR_AMT", arAmt);
			}
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);

			order.setItem(row, "EXEC_DEPT_CODE", odoMainControl.getValueString("CHN_EXEC_DEPT_CODE"));
			order.setItem(row, "ORDER_CAT1_CODE", ORDERCAT1TYPE);
			order.setItem(row, "DCT_TAKE_QTY", parmChn.getValue("DCT_TAKE_QTY",
					i));
			order.itemNow = false;
			order.setItem(row, "TAKE_DAYS", parmChn.getValue("TAKE_DAYS", i));
			order.itemNow = false;
			order.setItem(row, "FREQ_CODE", parmChn.getValue("FREQ_CODE", i));
			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", parmChn.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", parmChn.getValue("MEDI_UNIT", i));
			order.setItem(row, "ROUTE_CODE", parmChn.getValue("ROUTE_CODE", i));
			order.setItem(row, "DCTAGENT_CODE", parmChn.getValue(
					"DCTAGENT_CODE", i));
			order.setItem(row, "DR_NOTE", parmChn.getValue("DR_NOTE", i));
			order.setActive(row, true);
			order.newOrder(CHN, rxNo);
		}
		initChnTable(rxNo);
	}

	@Override
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder)throws Exception {
		// TODO Auto-generated method stub
		this.odoMainOpdOrder = odoMainOpdOrder;
		this.odoMainControl = odoMainOpdOrder.odoMainControl;
		this.odoMainPat = odoMainOpdOrder.odoMainPat;
		// 中药TABLE
		odoMainOpdOrder.tblChn = (TTable) odoMainControl.getComponent(TABLE_CHN);
		this.table = odoMainOpdOrder.tblChn;
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onChnCreateEditComponent");
		table.addEventListener(TABLE_CHN + "->" + TTableEvent.CHANGE_VALUE,
				this, "onChnValueChange");
	}

	@Override
	public void onChangeRx(String rxType) throws Exception{
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		rxNo = odoMainControl.getValueString(CHN_RX);
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		initChnTable(rxNo);
		odoMainOpdOrder.calculateChnCash(rxNo);
		calculatePackageTot(rxNo);
	}
	
	/**
	 * 计算并设置OpdOrder服总量
	 * 
	 * @param rxNo
	 *            String
	 */
	public void calculatePackageTot(String rxNo) throws Exception{
		//TODO 公用
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		long tot = 0;
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			tot += order.getItemDouble(i, "MEDI_QTY");
		}
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			order.setItem(i, "PACKAGE_TOT", tot);
		}
		odoMainControl.setValue("PACKAGE_TOT", tot);
	}

	@Override
	public boolean medAppyCheckDate(OpdOrder order, int row) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TTable getTable() throws Exception{
		// TODO Auto-generated method stub
		return table;
	}

	@Override
	public boolean initTable(String rxNo) throws Exception{
		// TODO Auto-generated method stub
		return initChnTable(rxNo);
	}

	@Override
	public void insertOrder(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		insertChnOrder(parm);
	}
	
	/**
	 * 常用医嘱插入中医医嘱
	 * 
	 * @param parm
	 *            TParm
	 */
	public void insertChnOrder(TParm parm) throws Exception{
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxNo = odoMainControl.getValueString(CHN_RX);

		String deptCode = odoMainControl.getValueString("CHN_EXEC_DEPT_CODE");
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			order.deleteRow(i);
		}

		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.messageBox("E0069");
			return;
		}
		if (parm == null) {
			odoMainControl.messageBox("E0070");
			return;
		}
		count = parm.getCount("ORDER_CODE");
		if (count < 1) {
			odoMainControl.messageBox("E0070");
			return;
		}
		int row = -1;
		for (int i = 0; i < count; i++) {
			row = order.newOrder(ODORxChnMed.CHN, rxNo);
			String orderCode = parm.getValue("ORDER_CODE", i);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			odoMainOpdOrder.initOrder(order, row, parm.getRow(i), phaBase);

			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));

			TParm price = OdoUtil.getPrice(order.getItemString(row,
					"ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			double ownAmt = ODOTool.getInstance().roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = ODOTool.getInstance().roundAmt(BIL.chargeTotCTZ(odoMainPat.ctz[0], odoMainPat.ctz[1], odoMainPat.ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					odoMainControl.serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(odoMainPat.ctz[0], odoMainPat.ctz[1],
					odoMainPat.ctz[2], price.getValue("CHARGE_HOSP_CODE")));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
			order.setItem(row, "EXEC_DEPT_CODE", deptCode);
			order.setItem(row, "ORDER_CAT1_CODE", ORDERCAT1TYPE);
			order.setItem(row, "CAT1_TYPE", "PHA");

			order.setItem(row, "DCT_TAKE_QTY", odoMainControl
					.getValueDouble("DCT_TAKE_QTY"));
			order.setItem(row, "TAKE_DAYS", odoMainControl
					.getValueDouble("DCT_TAKE_DAYS"));
			order.setItem(row, "FREQ_CODE", odoMainControl.getValue("CHN_FREQ_CODE"));
			order.setItem(row, "ROUTE_CODE", odoMainControl.getValue("CHN_ROUTE_CODE"));
			order.setItem(row, "DCTAGENT_CODE", odoMainControl.getValue("DCTAGENT_CODE"));
			order.setItem(row, "DR_NOTE", odoMainControl.getValue("DR_NOTE"));
			order.setActive(row, true);
		}
		odoMainOpdOrder.calculateChnCash(rxNo);
		if ((row + 1) % 4 == 0) {
			addChnRow(rxNo, row / 4);
		}
		this.initChnTable(rxNo);
	}

	@Override
	public String getCheckRxNoSum(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getTempSaveParm() throws Exception{
		// TODO Auto-generated method stub
		odoMainControl.odo.getOpdOrder().setFilter("RX_TYPE='"+ CHN
				+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
		odoMainControl.odo.getOpdOrder().filter();
	}

	@Override
	public void setopdRecipeInfo(OpdOrder order) throws Exception{
		// TODO Auto-generated method stub
		order.setFilter("RX_TYPE='" + ODORxChnMed.CHN + "'");
		order.filter();
	}

	@Override
	public void onDeleteOrderList(int rxType) throws Exception {
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		String tableName = NULLSTR;
		int count = -1;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String oldfilter = order.getFilter();
		StringBuffer billFlg=new StringBuffer();//判断是否可以删除 ，同一张处方签中的状态不相同不能删除
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		rxNo = (String) odoMainControl.getValue(CHN_RX);
		tableName = TABLE_CHN;
		table = (TTable) odoMainControl.getComponent(TABLE_CHN);
		odoMainControl.setValue(AMT_TAG, NULLSTR);
		odoMainControl.setValue("PACKAGE_TOT", NULLSTR);
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		count = order.rowCount();
		TParm parm=new TParm();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!order.checkDrugCanUpdate("CHN", i,parm,false)) { // 判断是否可以修改（有没有进行审,配,发）
				odoMainControl.messageBox(parm.getValue("MESSAGE"));
				return;
			}
			if (!odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
				return;
			} 
			if(!odoMainOpdOrder.deleteSumRxOrder(order, i, billFlg)){
				return;
			}
		}
		for (int i = count - 1; i > -1; i--) {
			order.deleteRow(i);
		}
		order.setFilter(oldfilter);
		order.filter();
		table.setDSValue();
		this.initChnTable(rxNo);
	}

	@Override
	public boolean deleteRow(OpdOrder order, int row, TTable table) throws Exception {
		// TODO Auto-generated method stub
		return deleteRowChn(order, row, table);
	}

	@Override
	public void deleteorderAuto(int row) throws Exception {
		// TODO Auto-generated method stub
		TTable table = null;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = NULLSTR;
		table = (TTable) odoMainControl.getComponent(TABLE_CHN);
		rx_no = odoMainControl.getValueString(CHN_RX);
		order.setFilter("RX_TYPE='" + CHN + "'");
		order.filter();
		order.deleteRow(row);
		order.newOrder(CHN, rx_no);
		int totRow = order.rowCount();
		if (!StringUtil.isNullString(order.getItemString(totRow - 1,
				"ORDER_CODE"))
				|| totRow % 4 != 0 || totRow < 1) {
			for (int a = 0; a < 4 - totRow % 4; a++) {
				order.setItem(a, "PHA_TYPE", "G");
			}
		}
		TParm parm = odoMainControl.odo.getOpdOrder().getBuffer(order.PRIMARY);
		TParm tableParm = new TParm();
		for (int j = 0; j < parm.getCount(); j++) {
			int idx = j % 4 + 1;
			tableParm.setData("ORDER_DESC" + idx, 1, parm.getValue(
					"ORDER_DESC", j));
			tableParm.setData("MEDI_QTY" + idx, 1, parm.getDouble(
					"MEDI_QTY", j));
			tableParm.setData("DCTEXCEP_CODE" + idx, 1, parm.getValue(
					"DCTEXCEP_CODE", j));
		}
		tableParm.setCount(1);
		odoMainControl.callFunction("UI|TABLECHN|setParmValue", tableParm);
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
	
	/**
	 * 西药，管制药品，处置的checkBox事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) throws Exception{
		TTable table = (TTable) obj;
		table.acceptText();
		table.setDSValue();
		return false;
	}

	@Override
	public void onSortRx(boolean flg) {
		// TODO Auto-generated method stub
		
	}

}
