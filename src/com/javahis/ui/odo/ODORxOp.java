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
 * Title: 门诊医生工作站处方签接口处置处方签实现类
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站处方签接口处置处方签实现类
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
	public static final String TABLE_OP = "TABLEOP"; // 诊疗项目表
	public final static String OP_RX = "OP_RX";
	public static final String OP = "4";
	public static final int OP_INT = 4;
	public static final int TABBEDPANE_INDEX = 1;
	public static final String ORDERCAT1TYPE = "TRT";
	public static final String AMT_TAG = "OP_AMT";
	
	public static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	private static final String NULLSTR = "";
	
	/**
	 * 初始化处置
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
			odoMainControl.messageBox("E0027"); // 初始化处置失败
		odoMainOpdOrder.onChangeRx(OP);
	}

	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initOp();
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
	public void onOpCreateEditComponent(Component com, int row, int column) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_OP);
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
		odoMainOpdOrder.tableName = TABLE_OP;
		odoMainOpdOrder.rxName = OP_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 4);
		//add by lx 2014/03/17  加入医生专用   项目
		parm.setData("USE_CAT", "1");
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOpReturn");
	}

	/**
	 * 处置值改变事件，计算价钱
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
		// 集合医嘱 主项修改细项要随之修改
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // 用量
				"FREQ_CODE".equalsIgnoreCase(columnName) || // 频次
				"TAKE_DAYS".equalsIgnoreCase(columnName))) { // 日份
			// 判断该行数据是否是集合医嘱主项 如果是主项 那么循环修改细项
			if ("Y".equalsIgnoreCase(order.getItemString(row, "SETMAIN_FLG"))) {
				String rxNo = order.getItemString(row, "RX_NO");
				String ordersetCode = order.getItemString(row, "ORDER_CODE");
				String orderSetGroup = order.getItemString(row,
						"ORDERSET_GROUP_NO");
				for (int i = 0; i < order.rowCountFilter(); i++) {
					// 判断是否是数据改主项的细项
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
		//执行科室修改细项 zhangp 20130930 start
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
		//执行科室修改细项 zhangp 20130930 end
		odoMainOpdOrder.calculateCash(TABLE_OP, AMT_TAG);
		return false;
	}

	/**
	 * 新增处置
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
		// 判断是否已经超过看诊时限
		if (!odoMainControl.odoMainReg.canEdit()) {
			table.setDSValue(row);
			odoMainControl.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (odoMainControl.odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE") != null
				&& odoMainControl.odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE").trim()
						.length() > 0) {
			odoMainControl.messageBox("E0045"); // 已开立医嘱不可变更，请删除此医嘱或新开立
			table.setDSValue(row);
			return;
		}
		int column = table.getSelectedColumn();
		String code = sysFee.getValue("ORDER_CODE");
		table.acceptText();
		rxNo = (String) odoMainControl.getValue(odoMainOpdOrder.rxName);
		odoMainOpdOrder.rxType = "4";
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		
		
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
		if (order.isSameOrder(code)) {
			if (odoMainControl.messageBox("提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\n/This order exist,Do you give it again?",
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
		//设置成  STAT
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
	 * 删除一行处置医嘱
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowOp(OpdOrder order ,int row,TTable table)throws Exception{
		//delete by huangtt 20150731
//		if (!odoMainOpdOrder.checkSendPah(order, row))// 检查已发药已到检
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
	 * 插入处置模板
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOpPack(TParm parm) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_OP);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxNo = odoMainControl.getValueString(OP_RX); // 处方签号
		if (StringUtil.isNullString(rxNo)) {
			odoMainOpdOrder.initRx(OP_RX, OP);
		}
		rxNo = odoMainControl.getValueString("OP_RX");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		// 获取回传值的个数
		int count = parm.getCount("ORDER_CODE");
		// 循环处理每条 诊疗项目
		for (int i = 0; i < count; i++) {
			boolean memPackageFlg = "Y".equals(parm.getValue("MEM_PACKAGE_FLG", i));//套餐
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
			// 新建一行 诊疗项目
			order.newOpOrder(rxNo, orderCode, odoMainPat.ctz, row, memPackageFlg, parm.getValue("ID", i), parm.getValue("TRADE_NO", i));
			// 判断模板传回的信息中是否有 执行科室
			// 如果有执行科室 那么用模板中的执行科室
			String execDept = NULLSTR;
			if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parm.getValue("EXEC_DEPT_CODE", i);
			}
			// 如果模板中没有执行科室 那么使用sys_fee中的执行科室
			else if (!StringUtil.isNullString(sysFeeParm
					.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
			} else { // 如果sys_fee中也没有执行科室 那么使用当前用户的算在科室
				execDept = Operator.getDept();
			}
			order.setItem(row, "EXEC_DEPT_CODE", execDept); // 执行科室
			order.setItem(row, "ORDER_DESC", sysFeeParm.getValue("ORDER_DESC")
					.replaceFirst(
							"(" + sysFeeParm.getValue("SPECIFICATION") + ")",
							NULLSTR)); // 医嘱名称
			order.setItem(row, "CTZ1_CODE", odoMainPat.ctz[0]);
			order.setItem(row, "CTZ2_CODE", odoMainPat.ctz[1]);
			order.setItem(row, "CTZ3_CODE", odoMainPat.ctz[2]);
			order.itemNow = false; // 开启总量计算
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
		// 诊疗项目TABLE
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
	 * 处置的checkBox事件
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
			odoMainControl.messageBox("E0038"); // 显示处置失败
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
			String rxNo = odoMainControl.getValueString(OP_RX); // 处方签号
			if (null==rxNo || rxNo.length()<=0) {
				return "诊疗项目没有初始化处方签";
			}
			if (!order.isExecutePrint(odoMainControl.odo.getCaseNo(), rxNo)) {
				return "已经打票的诊疗项目处方签不可以添加医嘱";
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
		StringBuffer billFlg=new StringBuffer();//判断是否可以删除 ，同一张处方签中的状态不相同不能删除
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
