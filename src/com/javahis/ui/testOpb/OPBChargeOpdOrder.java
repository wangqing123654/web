package com.javahis.ui.testOpb;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.bean.OpdOrderHistory;
import com.javahis.ui.testOpb.bean.OpdOrderReturn;
import com.javahis.ui.testOpb.tools.OrderTool;
import com.javahis.ui.testOpb.tools.QueryTool;
import com.javahis.ui.testOpb.tools.SqlTool;
import com.javahis.ui.testOpb.tools.TableTool;
import com.javahis.ui.testOpb.tools.Type;

/**
 * 补充计费医嘱
 * 
 * @author zhangp
 *
 */
public class OPBChargeOpdOrder {

	public OPBChargeControl control;
	private OPBChargeReg opbChargeReg;

	public TTable table;
	public TableTool tableTool;

	private List<OpdOrder> bakList = new ArrayList<OpdOrder>();

	private final static String[] SYNCFIELDSNAMES = { "freqCode", "execDeptCode" };
	private final static String[] MUTIPLYFIELDSNAMES = { "dosageQty" };

	public OPBChargeOpdOrder(OPBChargeControl opbChargeControl) {

		this.control = opbChargeControl;
		opbChargeReg = opbChargeControl.opbChargeReg;

		table = (TTable) control.getComponent(TAG_TABLE);
		tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
		table.addRow();
		table.setLockRows("0");

		control.setValue(TAG_COST_CENTER, Operator.getCostCenter());
		control.setValue(TAG_EXEC_DR_CODE, Operator.getID());

		control.callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST", URL_SYSFEEPOPUP);// 医嘱代码
		control.callFunction("UI|ORDER_CODE|addEventListener", TPopupMenuEvent.RETURN_VALUE, this, "popReturnOrder");
		// 加入表格排序 add by huangtt 20150714
		addListener(table);

	}

	/**
	 * 注册控件的事件
	 */
	public void onInitEvent() throws Exception {
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComponent");
		table.addEventListener(TAG_TABLE + "->" + TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
	}

	/**
	 * 下拉注册事件
	 * 
	 * @param com
	 * @param row
	 * @param column
	 * @throws Exception
	 */
	public void onCreateEditComponent(Component com, int row, int column) throws Exception {
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		if (!"orderDesc".equalsIgnoreCase(columnName)) {
			return;
		}

		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("ORDER_CAT1_CODE", control.getValueString("ORDER_CAT1_CODE"));
		parm.setData("USE_CAT", "2");// 护士专用
		if (!"".equals(Operator.getRegion())) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("SYSFEE", control.getConfigParm().newConfig(URL_SYSFEEPOPUP), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * 下拉框返回事件
	 * 
	 * @param tag
	 * @param obj
	 * @throws Exception
	 */
	public void popReturn(String tag, Object obj) throws Exception {
		TParm parm = (TParm) obj;
		parm.setData("HEXP_CODE", parm.getData("CHARGE_HOSP_CODE"));
		parm.setData("DOSAGE_UNIT", parm.getData("UNIT_CODE"));

		onReturn(parm);
	}

	public void popReturnOrder(String tag, Object obj) {
		TParm parm = (TParm) obj;
		control.setValue(TAG_ORDER_CODE, parm.getValue("ORDER_CODE"));
		control.setValue(TAG_ORDER_DESC, parm.getValue("ORDER_DESC"));
	}

	/**
	 * 值改变事件
	 * 
	 * @param tNode
	 * @throws Exception
	 */
	public void onTableChangeValue(TTableNode tNode) throws Exception {

		if (check()) {
			tNode.setValue(tNode.getOldValue());
			return;
		}

		int row = tNode.getRow();

		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);

		if ("subQty".equals(colName)) {

			double subQty = Double.valueOf("" + tNode.getValue());
			// double dosageQty = table.getItemDouble(row, "showQty");
			List<OpdOrder> list = tableTool.getList();
			double dosageQty = list.get(row).showQty.doubleValue();
			if (subQty > dosageQty) {
				control.messageBox(MSG_OVER_DOSAGE);
				return;
			}

			tNode.setColumn(table.getColumnIndex("dosageQty"));// dosageQty列
			tNode.setValue(dosageQty - subQty);
			onTableChangeValue(tNode);

			tNode.setValue(subQty);
			return;
		}

		List<OpdOrder> list = tableTool.getList();

		if ("dosageQty".equals(colName)) {
			double dosageQty = Double.valueOf("" + tNode.getValue());
			if (dosageQty < 0) {
				control.messageBox(MSG_OVER_DOSAGE);
				return;
			}

		}

		if ("orderDesc".equals(colName)) {
			if (tNode.getOldValue().toString().length() > 0) {
				tNode.setValue(tNode.getOldValue());
				return;
			}
		}

		backup(list, row);

		tableTool.changeValue(tNode);
		list = tableTool.getList();
		list = OrderTool.getInstance().changeValue(tNode, list);
		tableTool.setList(list);
		tableTool.show();
		setExecColor();
		control.opbChargeEkt.setMaster(list);
	}

	/**
	 * 删除行
	 * 
	 * @throws Exception
	 */
	public void deleteRow() throws Exception {
		if (check())
			return;
		tableTool.deleteRow();
		tableTool.show();
		setExecColor();
		List<OpdOrder> list = tableTool.getList();
		control.opbChargeEkt.setMaster(list);

		setLock("orderDesc");
		setLock("dosageQty");
		setLock("invCode");
	}

	/**
	 * 查询
	 * 
	 * @throws Exception
	 */
	public void onQuery() throws Exception {
		setLockFase("orderDesc");
		setLockFase("dosageQty");
		setLockFase("invCode");
		setExecColorNull();
		// add by huangtt 20141022 start
		String orderCode = control.getValueString(TAG_ORDER_CODE);
		String orderCat1Code = control.getValueString(TAG_ORDER_CAT1_CODE);
		String rexpCode = control.getValueString(TAG_REXP_CODE);
		String execDepdCode = control.getValueString(TAG_COST_CENTER_QUERY);
		String sql1 = "";
		if (orderCode.length() > 0) {
			sql1 = sql1 + " AND ORDER_CODE='" + orderCode + "'";
		}

		if (orderCat1Code.length() > 0) {
			sql1 = sql1 + " AND ORDER_CAT1_CODE='" + orderCat1Code + "'";

		}

		if (rexpCode.length() > 0) {
			sql1 = sql1 + " AND REXP_CODE='" + rexpCode + "'";
		}
		if (execDepdCode.length() > 0) {
			sql1 = sql1 + " AND EXEC_DEPT_CODE='" + execDepdCode + "'";
		}
		// String sql =
		// "select * from opd_order where case_no = '" + opbChargeReg.reg.caseNo() + "'
		// order by exec_flg desc,order_date";
		String sql = "SELECT   A.* FROM (SELECT *" + " FROM OPD_ORDER WHERE CASE_NO = '" + opbChargeReg.reg.caseNo()
				+ "'" + " AND ORDERSET_CODE IN (" + " SELECT ORDER_CODE FROM OPD_ORDER" + " WHERE CASE_NO = '"
				+ opbChargeReg.reg.caseNo() + "'" + sql1 + " AND SETMAIN_FLG = 'Y'" + " AND ORDERSET_CODE IS NOT NULL)"
				+ " UNION SELECT * FROM OPD_ORDER" + " WHERE CASE_NO = '" + opbChargeReg.reg.caseNo() + "'"
				+ " AND ORDERSET_CODE IS NULL" + sql1 + ") A ORDER BY A.EXEC_FLG DESC, A.ORDER_DATE";
		// add by huangtt 20141022 start
		this.sql = sql;
		OpdOrder opdOrder = new OpdOrder();
		List<OpdOrder> list = QueryTool.getInstance().queryBySql(sql, opdOrder);
		list = OrderTool.getInstance().initOrder(list);
		if (list.isEmpty()) {
			control.messageBox(MSG_QUERY);
			table.setParmValue(new TParm());
			tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
			table.addRow();

		} else {
			tableTool.setList(list);
			tableTool.show();
			control.opbChargeEkt.setMaster(list);
			setExecColor();
			setLock("orderDesc");
			setLock("dosageQty");
			// setLock("invCode");

		}

	}

	/**
	 * 保存
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean onSave() throws Exception {

		tableTool.stopTableCellEditing();

		// add by huangtt 20141217 start
		// 判断是否为高值耗材，如果是的，inv_code不能为空
		String message = checkInvCode();
		if (message.length() > 0) {
			control.messageBox(message);
			return false;
		}
		// add by huangtt 20141217 end

		message = checkCostCenter();
		if (message.length() > 0) {
			control.messageBox(message);
			return false;
		}

		boolean isChanged = false;
		isChanged = checkChanged();

		List<OpdOrder> list = tableTool.getList();

		message = control.pay.checkMaster(opbChargeReg.reg.caseNo(), control.opbChargePat.pat.getMrNo(), list);

		if (message.length() > 0) {

			if (JOptionPane.showConfirmDialog(null, message + "，是否继续", "是否继续",
					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				return false;
			}
		}

		TParm parm = getSql(isChanged);
		TParm result = TIOM_AppServer.executeAction("action.bil.BILAction", "onSaveTax", parm);
		if (result.getErrCode() < 0) {
			control.messageBox(MSG_FAIL);
			return false;
		}
		if (isChanged) {
			control.messageBox(MSG_SUCCESS2);
		} else {
			control.messageBox(MSG_SUCCESS);
		}
		return true;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		table.setParmValue(new TParm());
		tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
		table.addRow();
		table.setLockRows("0");
		bakList = new ArrayList<OpdOrder>();
		control.setValue(TAG_COST_CENTER, Operator.getCostCenter());
		control.setValue(TAG_EXEC_DR_CODE, Operator.getID());
		control.clearValue(TAG_ORDER_CAT1_CODE + ";" + TAG_REXP_CODE + ";" + TAG_ORDER_CODE + ";" + TAG_ORDER_DESC + ";"
				+ TAG_COST_CENTER_QUERY);
		this.sql = "";
		setExecColor();
	}

	/**
	 * 传回
	 * 
	 * @param parm
	 * @param dosageQty
	 * @throws Exception
	 */
	public void onReturn(TParm parm) throws Exception {

		List<OpdOrder> listo = tableTool.getList();
		OpdOrder opdOrder = new OpdOrder();
		opdOrder = tableTool.onNew(opdOrder, parm);
		opdOrder = OrderTool.getInstance().newOrder(opbChargeReg.reg, opdOrder, Operator.getID(), Operator.getIP(),
				control.getValueString(TAG_COST_CENTER));
		if (parm.getValue("ORDERSET_FLG").equals("Y")) {
			opdOrder = OrderTool.getInstance().queryOrderSet(opdOrder,
					control.getValueString(OPBChargeReg.TAG_DEPT_CODE), control.getValueString(TAG_COST_CENTER),
					opbChargeReg.reg, listo.size());
		}

		listo.add(opdOrder);
		tableTool.setList(listo);
		tableTool.show();
		setExecColor();
		table.getTable().grabFocus();
		control.opbChargeEkt.setMaster(listo);
	}

	/**
	 * 取得sql
	 * 
	 * @return
	 * @throws Exception
	 */
	private TParm getSql(boolean isChanged) throws Exception {

		List<OpdOrder> list = tableTool.getList();
		list = OrderTool.getInstance().deOrderSet(list);
		TParm parm = new TParm();
		OpdOrder newOpdOrder;
		String rxNo = SystemTool.getInstance().getNo("ALL", "ODO", "RX_NO", "RX_NO");
		for (int i = 0; i < list.size(); i++) {
			newOpdOrder = list.get(i);
			//
			newOpdOrder.dispenseQty = newOpdOrder.dosageQty;
			//
			if (newOpdOrder.modifyState == Type.INSERT) {
				newOpdOrder = OrderTool.getInstance().setPkey(newOpdOrder, i, rxNo);
			}
			if (!isChanged) {
				String sql = SqlTool.getInstance().getSql(newOpdOrder);
				if (sql.length() > 0) {
					parm.addData("SQL", sql);
				}

			} else if (newOpdOrder.modifyState == Type.INSERT) {
				String sql = SqlTool.getInstance().getSql(newOpdOrder);
				if (sql.length() > 0) {
					parm.addData("SQL", sql);
				}
			}
		}

		List<OpdOrder> deleteList = tableTool.getDeleteList();
		deleteList = OrderTool.getInstance().deOrderSet(deleteList);
		OpdOrder uOpdOrder;
		bakList = OrderTool.getInstance().deOrderSet(bakList);
		for (OpdOrder dOpdOrder : deleteList) {

			if (dOpdOrder.modifyState != Type.DELETE) {
				dOpdOrder.modifyState = Type.DELETE;
			}

			for (int i = 0; i < bakList.size(); i++) {
				uOpdOrder = bakList.get(i);
				if (uOpdOrder.caseNo.equals(dOpdOrder.caseNo) && uOpdOrder.rxNo.equals(dOpdOrder.rxNo)
						&& uOpdOrder.seqNo.compareTo(dOpdOrder.seqNo) == 0) {
					bakList.remove(i);
					i--;
				}
			}
			if (!isChanged) {
				String sql = SqlTool.getInstance().getSql(dOpdOrder);
				if (sql.length() > 0) {
					parm.addData("SQL", sql);
				}
			}
			OpdOrderHistory opdOrderHistory = new OpdOrderHistory();
			opdOrderHistory = QueryTool.getInstance().synClasses(dOpdOrder, opdOrderHistory);
			opdOrderHistory.dcOrderDate = OrderTool.getInstance().getSystemTime();
			opdOrderHistory.modifyState = Type.INSERT;
			if (!isChanged) {
				String sql2 = SqlTool.getInstance().getSql(opdOrderHistory);
				if (sql2.length() > 0) {
					parm.addData("SQL", sql2);
				}
			}
		}
		for (OpdOrder opdOrder : bakList) {
			OpdOrderHistory opdOrderHistory = new OpdOrderHistory();
			opdOrderHistory = QueryTool.getInstance().synClasses(opdOrder, opdOrderHistory);
			opdOrderHistory.dcOrderDate = OrderTool.getInstance().getSystemTime();
			opdOrderHistory.modifyState = Type.INSERT;
			if (!isChanged) {
				String sql = SqlTool.getInstance().getSql(opdOrderHistory);
				if (sql.length() > 0) {
					parm.addData("SQL", sql);
				}
				// add by huangtt 20150522 start 备份退费记录
				for (int i = 0; i < list.size(); i++) {
					newOpdOrder = list.get(i);
					if (opdOrder.caseNo.equals(newOpdOrder.caseNo) && opdOrder.rxNo.equals(newOpdOrder.rxNo)
							&& opdOrder.seqNo.compareTo(newOpdOrder.seqNo) == 0) {
						OpdOrderReturn opdOrderReturn = new OpdOrderReturn();
						opdOrderReturn = QueryTool.getInstance().synClasses(newOpdOrder, opdOrderReturn);
						opdOrderReturn.dcOrderDate = OrderTool.getInstance().getSystemTime();
						opdOrderReturn.optUser = Operator.getID();
						opdOrderReturn.optTerm = Operator.getIP();
						opdOrderReturn.optDate = OrderTool.getInstance().getSystemTime();
						opdOrderReturn.execDeptCode = Operator.getDept();
						opdOrderReturn.oldDosageQty = opdOrder.dosageQty;
						opdOrderReturn.modifyState = Type.INSERT;
						String sql2 = SqlTool.getInstance().getSql(opdOrderReturn);
						// System.out.println("sql111=="+sql2);
						if (sql2.length() > 0) {
							parm.addData("SQL", sql2);
						}
						break;
					}

				}
				// add by huangtt 20150522 end
			}
		}
		return parm;
	}

	/**
	 * 备份
	 * 
	 * @param row
	 */
	private void backup(List<OpdOrder> list, int row) {
		if (list.size() > row && row >= 0) {
			OpdOrder opdOrder = list.get(row);
			if (opdOrder.modifyState == Type.DEFAULT) {
				for (int i = 0; i < bakList.size(); i++) {
					OpdOrder tmpOrder = bakList.get(i);
					if (opdOrder.caseNo.equals(tmpOrder.caseNo) && opdOrder.rxNo.equals(tmpOrder.rxNo)
							&& opdOrder.seqNo.compareTo(tmpOrder.seqNo) == 0) {
						bakList.remove(i);
						break;
					}
				}
				OpdOrder bakOpdOrder = opdOrder.clone();
				bakList.add(bakOpdOrder);
			}
		}
	}

	/**
	 * 校验
	 * 
	 * @return
	 */
	private boolean check() {
		List<OpdOrder> list = tableTool.getList();
		int index = table.getSelectedRow();
		if (index < 0) {
			return false;
		}
		OpdOrder opdOrder = list.get(index);

		// 最高权限
		if (control.getPopedem("SYSDBA")) {
			return false;
		}
		// 角色权限
		if (control.getPopedem("SYSOPERATOR")) {
			if (!"CLINIC_FEE".equals(opdOrder.rxNo)) {
				if (!Operator.getCostCenter().equals(opdOrder.execDeptCode)) {
					control.messageBox(MSG_CCISDIF);
					return true;
				}

				if ("Y".equals(opdOrder.billFlg)) {
					control.messageBox(MSG_BILLED);
					return true;
				}
			}
			return false;
		}
		// 一般权限
		if (control.getPopedem("NORMAL")) {
			if (!Operator.getCostCenter().equals(opdOrder.execDeptCode)) {
				control.messageBox(MSG_CCISDIF);
				return true;
			}

			if ("Y".equals(opdOrder.billFlg)) {
				control.messageBox(MSG_BILLED);
				return true;
			}
			return false;
		}

		if (!Operator.getCostCenter().equals(opdOrder.execDeptCode)) {
			control.messageBox(MSG_CCISDIF);
			return true;
		}

		if ("Y".equals(opdOrder.billFlg)) {
			control.messageBox(MSG_BILLED);
			return true;
		}
		return false;
	}

	/**
	 * 设置已执行颜色
	 */
	private void setExecColor() {
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setRowColor(i, null);
			if ("Y".equals(table.getItemString(i, "execFlg"))) {
				table.setRowColor(i, Color.YELLOW);
			}
		}
	}

	/**
	 * 清空已执行颜色
	 */
	private void setExecColorNull() {
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setRowColor(i, null);

		}
	}

	/**
	 * 锁定某行某列
	 * 
	 * @param colName
	 */
	public void setLock(String colName) {
		table.acceptText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if ("Y".equals(table.getItemString(i, "execFlg"))) {
				table.setLockCell(i, colName, true);
			} else {
				table.setLockCell(i, colName, false);
			}
		}

	}

	/**
	 * 去掉锁定某行某列
	 * 
	 * @param colName
	 */
	public void setLockFase(String colName) {
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setLockCell(i, colName, false);
		}

	}

	private boolean checkChanged() throws Exception {
		boolean isChanged = false;
		List<OpdOrder> oList = tableTool.getList();
		List<OpdOrder> originList = new ArrayList<OpdOrder>();
		for (OpdOrder opdOrder : oList) {
			if (opdOrder.modifyState == Type.UPDATE) {
				for (OpdOrder backOpdOrder : bakList) {
					if (opdOrder.caseNo.equals(backOpdOrder.caseNo) && opdOrder.rxNo.equals(backOpdOrder.rxNo)
							&& opdOrder.seqNo.compareTo(backOpdOrder.seqNo) == 0) {
						originList.add(backOpdOrder);
					}
				}
			}
		}
		List<OpdOrder> deleteList = tableTool.getDeleteList();
		for (OpdOrder opdOrder : deleteList) {
			originList.add(opdOrder);
		}
		OpdOrder opdOrder = new OpdOrder();
		List<OpdOrder> nowList = QueryTool.getInstance().queryBySql(sql, opdOrder);
		nowList = OrderTool.getInstance().initOrder(nowList);

		List<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < originList.size(); i++) {
			opdOrder = originList.get(i);
			for (OpdOrder nowOpdOrder : nowList) {
				if (opdOrder.caseNo.equals(nowOpdOrder.caseNo) && opdOrder.rxNo.equals(nowOpdOrder.rxNo)
						&& opdOrder.seqNo.compareTo(nowOpdOrder.seqNo) == 0) {
					if (!temp.contains(i)) {
						temp.add(i);
					}
					if (!compareEachOher(opdOrder, nowOpdOrder)) {
						isChanged = true;
					}
				}
			}
		}
		for (int i = 0; i < originList.size(); i++) {
			if (!temp.contains(i)) {
				isChanged = true;
			}
		}
		return isChanged;
	}

	private boolean compareEachOher(OpdOrder aOpdOrder, OpdOrder bOpdOrder) {
		if (!aOpdOrder.orderCode.equals(bOpdOrder.orderCode) || aOpdOrder.mediQty.compareTo(bOpdOrder.mediQty) != 0
				|| !aOpdOrder.mediUnit.equals(bOpdOrder.mediUnit)
				|| aOpdOrder.dosageQty.compareTo(bOpdOrder.dosageQty) != 0
				|| !aOpdOrder.dosageUnit.equals(bOpdOrder.dosageUnit)
				|| aOpdOrder.dispenseQty.compareTo(bOpdOrder.dispenseQty) != 0
				|| aOpdOrder.arAmt.compareTo(bOpdOrder.arAmt) != 0 || !aOpdOrder.billFlg.equals(bOpdOrder.billFlg)
				|| !aOpdOrder.printFlg.equals(bOpdOrder.printFlg) || !aOpdOrder.execFlg.equals(bOpdOrder.execFlg)
				|| !aOpdOrder.phaCheckCode.equals(bOpdOrder.phaCheckCode)
				|| !aOpdOrder.phaDosageCode.equals(bOpdOrder.phaDosageCode)
				|| !aOpdOrder.phaDispenseCode.equals(bOpdOrder.phaDispenseCode)
				|| !aOpdOrder.requestFlg.equals(bOpdOrder.requestFlg)
				|| !aOpdOrder.requestNo.equals(bOpdOrder.requestNo)) {
			return false;

		}
		return true;
	}

	/**
	 * 检测插入的医嘱执行科室不能为空
	 * 
	 * @return
	 */
	public String checkCostCenter() {
		String message = "";
		List<OpdOrder> oList = tableTool.getList();
		for (OpdOrder opdOrder : oList) {
			if (opdOrder.execDeptCode.length() == 0) {
				message = opdOrder.orderDesc + " 的执行科室不能为空";
				return message;
			}
		}
		return message;
	}

	/**
	 * 检测插入的医嘱是否为高值，如果为高值的话，inv_code不能为空 add by huangtt
	 * 
	 * @return
	 */
	public String checkInvCode() {
		String message = "";
		List<OpdOrder> oList = tableTool.getList();
		for (OpdOrder opdOrder : oList) {
			if (opdOrder.modifyState == Type.INSERT) {
				String orderCode = opdOrder.orderCode;
				String sql = "SELECT ORDER_CODE,ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE = '" + orderCode
						+ "' AND  SUPPLIES_TYPE='1'";
				TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				if (parm.getCount() > 0) {
					System.out.println("opdOrder.invCode===" + opdOrder.invCode);
					if (opdOrder.invCode == null || opdOrder.invCode.trim().length() == 0) {
						message += opdOrder.orderDesc + "、";
					}
				}
			}
		}
		if (message.length() > 0) {
			message = message.substring(0, message.length() - 1) + "为高值耗材，材料序号不能为空";
		}

		return message;
	}

	/**
	 * 切换cat1code
	 * 
	 * @throws Exception
	 */
	public void onCat1CodeChange() throws Exception {
		tableTool.stopTableCellEditing();
		table.removeEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComponent");
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComponent");
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("===tableDate排序前==="+tableDate);

		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				String[] map = table.getParmMap().split(";");
				String method = map[j];
				if (j != 0) {
					return;
				}

				List<OpdOrder> list = tableTool.getList();
				if (row == 0) {
					tableTool.Sort(list, method, "sort");
					row = 1;
					// System.out.println("row=sort=="+row);
				} else {

					tableTool.Sort(list, method, "desc");
					row = 0;
					// System.out.println("row=desc=="+row);
				}

				try {
					tableTool.setList(list);
					tableTool.show();
					setExecColor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private final static String TAG_TABLE = "TABLE";
	private final static String TAG_COST_CENTER_QUERY = "COST_CENTER_QUERY";
	private final static String TAG_COST_CENTER = "COST_CENTER";
	private final static String TAG_EXEC_DR_CODE = "EXEC_DR_CODE";
	private final static String TAG_ORDER_CODE = "ORDER_CODE";
	private final static String TAG_ORDER_DESC = "ORDER_DESC";
	private final static String TAG_ORDER_CAT1_CODE = "ORDER_CAT1_CODE";
	private final static String TAG_REXP_CODE = "REXP_CODE";

	private final static String MSG_QUERY = "您选择的条件没有要查询的数据，请重新选择！";
	private final static String MSG_FAIL = "操作失败";
	private final static String MSG_SUCCESS = "操作成功";
	private final static String MSG_SUCCESS2 = "您修改或删除的医嘱已被他人修改\r\n您新增的医嘱已保存\r\n请重新修改医嘱";
	private final static String MSG_OVER_DOSAGE = "退费数量大于开立数量";
	private final static String MSG_BILLED = "已结算,不可更改";
	private final static String MSG_CCISDIF = "成本中心不同,不可更改";
	private final static String URL_SYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";

	private String sql = "";
	private int row = 0;// 表格排序

}
