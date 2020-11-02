package com.javahis.ui.odo;

import java.awt.Component;

import jdo.odo.OpdOrder;
import jdo.pha.PhaBaseTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSSQL;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站处方签接口管制药品处方签实现类
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站处方签接口管制药品处方签实现类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODORxCtrl implements IODORx{
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	ODOMainPat odoMainPat;
	
	TTable table;
	public static final String TABLE_CTRL = "TABLECTRL"; // 管制药品表
	public final static String CTRL_RX = "CTRL_RX";
	public static final String CTRL = "2";
	public static final int CTRL_INT = 2;
	public static final int TABBEDPANE_INDEX = 4;
	public static final String ORDERCAT1TYPE = "PHA_W";
	public static final String AMT_TAG = "CTRL_AMT";
	
	public static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	
	private static final String NULLSTR = "";
	/**
	 * 初始化管制药品
	 */
	private void initCtrl() throws Exception{
		boolean isInit = odoMainOpdOrder.isTableInit(TABLE_CTRL);
		String rxNo = NULLSTR;
		if (!isInit) {
			rxNo = odoMainOpdOrder.initRx(CTRL_RX, CTRL);
			odoMainOpdOrder.setTableInit(TABLE_CTRL, true);
		} else {
			rxNo = odoMainControl.getValueString(CTRL_RX);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_CTRL, isInit))
			odoMainControl.messageBox("E0026"); // 初始化检验检查失败
		odoMainOpdOrder.onChangeRx(CTRL);
	}

	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initCtrl();
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
	public void onCtrlCreateEditComponent(Component com, int row, int column) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_CTRL);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		odoMainOpdOrder.tableName = TABLE_CTRL;
		odoMainOpdOrder.rxName = CTRL_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 2);
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, odoMainOpdOrder.odoRxMed,
				"popOrderReturn");
	}

	/**
	 * 管制药品值改变事件，计算价钱
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onCtrlValueChange(TTableNode tNode) throws Exception{
		int column = tNode.getColumn();
		TTable table = (TTable) odoMainControl.getComponent(TABLE_CTRL);
		int row = tNode.getRow();
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		TParm parm=new TParm();
		if (!order.checkDrugCanUpdate("MED", row, parm ,true)) { // 判断是否可以修改（有没有进行审,配,发）
			odoMainControl.messageBox(parm.getValue("MESSAGE"));
			return true;
		}
		if (odoMainOpdOrder.odoRxMed.check(order, row, "MED",true)) {
			return true;
		}
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {// pangben ====2012 2-28
			return true;
		}
		String columnName = table.getParmMap(column);
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue(NULLSTR);
			return false;
		}
		// TParm parm = odo.getOpdOrder().getRowParm(table.getSelectedRow());
		String orderCode = odoMainControl.odo.getOpdOrder().getItemString(row, "ORDER_CODE");
		double mediQty = StringTool.getDouble(tNode.getValue() + NULLSTR);
//		final int inRow = row;
		final String execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
		final String orderCodeFinal = odoMainControl.odo.getOpdOrder().getItemString(row,
				"ORDER_CODE");
		final String columnNameFinal = columnName;
		if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				if (!SYSCtrlDrugClassTool.getInstance().getCtrOrderMaxDosage(
						orderCode, mediQty)) {
					if (odoMainControl.messageBox(
							"提示信息/Tip",
							"超过管制药品默认用量,是否继续开立?\r\nQty of this order is over-gived.Do you proceed anyway?",
							odoMainControl.YES_NO_OPTION) == 1) {
						table.setDSValue(row);
						return true;
					}
				}
			}
			odoMainOpdOrder.odoRxMed.setOnValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("TAKE_DAYS".equalsIgnoreCase(columnName) || "FREQ_CODE".equalsIgnoreCase(columnName)) {
			odoMainOpdOrder.odoRxMed.setOnValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) {
			int value = TypeTool.getInt(tNode.getValue());
			int oldValue = TypeTool.getInt(tNode.getOldValue());
			if (oldValue == 0 && value > 0) {
				return true;
			}
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				if (value > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			String routeCode = tNode.getValue() + NULLSTR;
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				odoMainControl.messageBox("E0019"); // 取得用法失败
				return true;
			} else if (result != 1) {
				odoMainControl.messageBox("E0022"); // 管制药品不能使用该用法
				return true;
			}
			return false;
		}
		// table.setItem(row,columnName,tNode.getValue());
		// 4,8,12,13,16,17,18,21,22
		odoMainOpdOrder.calculateCash(TABLE_CTRL, "CTRL_AMT");
		// table.setDSValue(table.getSelectedRow());
		return false;
	}

	/**
	 * 删除一行管制药品
	 * @param order
	 * @param row
	 * @param table
	 * =====pangben 2013-4-24
	 */
	private boolean deleteRowCtrl(OpdOrder order ,int row,TTable table)throws Exception{
		if (!odoMainOpdOrder.checkSendPah(order, row))// 检查已发药已到检
			return false;
		if(!odoMainOpdOrder.deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}

	@Override
	public void insertPack(TParm parm, boolean flg) throws Exception{
		// TODO Auto-generated method stub
		insertCtrlPack(parm);
	}
	
	/**
	 * 插入管制药品模板
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertCtrlPack(TParm parm) throws Exception{
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxNo = odoMainControl.getValueString(CTRL_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainOpdOrder.initRx(CTRL_RX, CTRL);
		}
		rxNo = odoMainControl.getValueString(CTRL_RX);
		String exec = odoMainControl.getValueString("CTRL_RBORDER_DEPT_CODE");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
				: (order.newOrder(CTRL, rxNo));
		int count = parm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
					parm.getValue("ORDER_CODE", i));
			TParm parmRow = parm.getRow(i);
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
			sysFee = sysFee.getRow(0);
			// 判断模板传回的信息中是否有 执行科室
			// 如果有执行科室 那么用模板中的执行科室
			String execDept = NULLSTR;
			if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parm.getValue("EXEC_DEPT_CODE", i);
			}
			// 如果模板中没有执行科室 那么使用sys_fee中的执行科室
			else if (!StringUtil
					.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFee.getValue("EXEC_DEPT_CODE");
			} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
				//execDept = Operator.getDept();
				execDept = odoMainControl.getValueString("CTRL_RBORDER_DEPT_CODE");
			}
			boolean memPackageFlg = "Y".equals(parmRow.getValue("MEM_PACKAGE_FLG"));//套餐
			sysFee.setData("EXEC_DEPT_CODE", execDept);
			odoMainOpdOrder.initOrder(order, row, sysFee, parmBase);
			order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
			order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
			order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
			order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));
			order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
			order
					.setItem(row, "LINKMAIN_FLG", parmRow
							.getValue("LINKMAIN_FLG"));
			order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));
			if(memPackageFlg){
				order.setItem(row, "AR_AMT", parmRow.getValue("AR_AMT"));
				order.setItem(row, "MEM_PACKAGE_ID", parmRow.getValue("ID"));
				order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130

			}
			order.setActive(row, true);
			row = order.newOrder(CTRL, rxNo);
		}
		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			order.newOrder(CTRL, rxNo);
		}
		odoMainOpdOrder.initNoSetTable(rxNo, odoMainOpdOrder.tableName, false);
		odoMainOpdOrder.calculateCash(odoMainOpdOrder.tableName, AMT_TAG);
		order.itemNow = false;
		
		//System.out.println("---odoMainPat.ctz1111----"+odoMainPat.ctz);
		//System.out.println("11"+OdoUtil==null);
		/*Map inscolor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);*/
	}

	@Override
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception{
		// TODO Auto-generated method stub
		this.odoMainOpdOrder = odoMainOpdOrder;
		this.odoMainControl = odoMainOpdOrder.odoMainControl;
		// 管制药品TABLE
		odoMainOpdOrder.tblCtrl = (TTable) odoMainControl.getComponent(TABLE_CTRL);
		this.table = odoMainOpdOrder.tblCtrl;
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCtrlCreateEditComponent");
		table.addEventListener(TABLE_CTRL + "->" + TTableEvent.CHANGE_VALUE,
				this, "onCtrlValueChange");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
	}
	
	/**
	 * 管制checkBox事件
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
		rxNo = odoMainControl.getValueString(CTRL_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.odo.getOpdOrder().setFilter(
					"RX_TYPE='" + CTRL + "' AND ORDER_CODE <>''");
			odoMainControl.odo.getOpdOrder().filter();
			table.setDSValue();
			odoMainOpdOrder.calculateCash(TABLE_CTRL, AMT_TAG);
			return;
		}
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_CTRL, false))
			odoMainControl.messageBox("E0037"); // 显示毒麻药失败
		odoMainControl.setValue("CTRL_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
		odoMainOpdOrder.calculateCash(TABLE_CTRL, AMT_TAG);
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
		return false;
	}

	@Override
	public void insertOrder(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCheckRxNoSum(TParm parm) throws Exception{
		// TODO Auto-generated method stub
		// 管制药品
		TParm orderResult = ((TParm) parm.getData("CTRL"));
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = odoMainControl.getValueString(CTRL_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "管制药品没有初始化处方签";
			}
			if (!order.isExecutePrint(odoMainControl.odo.getCaseNo(), rxNo)) {
				return "已经打票的管制药品处方签不可以添加医嘱";
			}
		}
		return null;
	}

	@Override
	public void getTempSaveParm() throws Exception{
		// TODO Auto-generated method stub
		odoMainControl.odo.getOpdOrder().setFilter("RX_TYPE='"+ CTRL
				+ "' AND ORDER_CODE <>'' AND #NEW#='Y' AND #ACTIVE#='Y'");
		odoMainControl.odo.getOpdOrder().filter();
	}

	@Override
	public void setopdRecipeInfo(OpdOrder order) throws Exception{
		// TODO Auto-generated method stub
		order.setFilter("RX_TYPE='" + ODORxCtrl.CTRL + "'");
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
		rxNo = (String) odoMainControl.getValue(CTRL_RX);
		tableName = TABLE_CTRL;
		odoMainControl.setValue(AMT_TAG, NULLSTR);
		if (StringUtil.isNullString(tableName)) {
			odoMainControl.messageBox("E0034"); // 取得数据错误
			return;
		}
		table = (TTable) odoMainControl.getComponent(tableName);
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		count = order.rowCount();
		TParm parm = new TParm();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			if (rxType == 1 || rxType == 2) {
				if (!order.checkDrugCanUpdate("MED", i,parm,false)) { // 判断是否可以修改（有没有进行审,配,发）
					odoMainControl.messageBox(parm.getValue("MESSAGE"));
					return;
				}
			}
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
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
		order.newOrder(rxType + NULLSTR, rxNo);
		order.setFilter(oldfilter);
		order.filter();
		table.setDSValue();
	}

	@Override
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception {
		// TODO Auto-generated method stub
		return deleteRowCtrl(order, row, table);
	}

	@Override
	public void deleteorderAuto(int row) throws Exception {
		// TODO Auto-generated method stub
		TTable table = null;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = NULLSTR;
		table = (TTable) odoMainControl.getComponent(TABLE_CTRL);
		rx_no = odoMainControl.getValueString(CTRL_RX);
		order.setFilter("RX_TYPE='" + CTRL + "'");
		order.deleteRow(row, buff);
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
