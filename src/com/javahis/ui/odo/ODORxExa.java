package com.javahis.ui.odo;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

import jdo.bil.BIL;
import jdo.odo.MedApply;
import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.sys.Operator;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SYSSQL;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站处方签接口检验检查处方签实现类
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站处方签接口检验检查处方签实现类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODORxExa implements IODORx{
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	TTable table;
	public static final String TABLE_EXA = "TABLEEXA"; // 检验检查表
	public final static String EXA_RX = "EXA_RX";
	public static final String EXA = "5";
	public static final int EXA_INT = 5;
	public static final int TABBEDPANE_INDEX = 0;
	public static final String ORDERCAT1TYPE = "EXA";
	public static final String AMT_TAG = "EXA_AMT";
	
	public static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	
	private static final String NULLSTR = "";
	
	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initExa();
	}
	
	/**
	 * 初始化检验检查
	 */
	private void initExa() throws Exception{
		boolean isInit = odoMainOpdOrder.isTableInit(TABLE_EXA);
		String rxNo = NULLSTR;
		if (!isInit) {
			rxNo = odoMainOpdOrder.initRx(EXA_RX, EXA);
			if (StringUtil.isNullString(rxNo)) {
				odoMainControl.messageBox("E0026"); // 初始化检验检查失败
				return;
			}
			odoMainOpdOrder.setTableInit(TABLE_EXA, true);
		} else {
			rxNo = odoMainControl.getValueString(EXA_RX);
		}
		if (!odoMainOpdOrder.initSetTable(TABLE_EXA, isInit))
			odoMainControl.messageBox("E0026"); // 初始化检验检查失败
		odoMainOpdOrder.onChangeRx(EXA);
	}
	
	/**
	 * 添加SYS_FEE弹出窗口(检验检查窗口)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_EXA);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (odoMainOpdOrder.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		odoMainOpdOrder.tableName = TABLE_EXA;
		odoMainOpdOrder.rxName = EXA_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 5); // 检验检查 CAT1_TYPE = LIS/RIS 
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popExaReturn");
	}

	/**
	 * 校验 检验检查已经登记的数据不能删除操作
	 * STATUS=2 已登记
	 * @param row
	 * @return
	 * ===========pangben 2013-1-29
	 */
	@Override
	public boolean medAppyCheckDate(OpdOrder order, int row)throws Exception{
		if(!order.isRemoveMedAppCheckDate(row)){
			return false;
		}
		return true;
	}
	
	/**
	 * 检验检查值改变事件
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean onExaValueChange(TTableNode tNode) throws Exception {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		String colName = tNode.getTable().getParmMap(column);
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(colName)) {
			tNode.setValue(NULLSTR);
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		if (order == null) {
			return true;
		}
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {// pangben ====2012 2-28
			return true;
		}
		if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			return true;
		}
		if(!checkExa(order, row)){//===pangben 2013-4-28删除勾选操作，校验是否到检
			return true;
		}
//		if (!checkExaIsSave(order, row)){//====预开检查是否保存校验
//			return true;
//		}
		
//		System.out.println("111111RX_NO RX_NO is ::"+order.getItemData(row, "RX_NO"));
//		System.out.println("222222CASE_NO CASE_NO is ::"+order.getItemData(row, "CASE_NO"));
//		System.out.println("333333SEQ_NO SEQ_NO is ::"+order.getItemData(row, "SEQ_NO"));
		
		//斯巴达
		//预开检查判断 yanjing 20131211
		if ("IS_PRE_ORDER".equalsIgnoreCase(colName)) {//是否为预开检查
	   //修改细项的预开检查状态及预开时间
			OpdOrder orderPre = odoMainControl.odo.getOpdOrder();
			String lastFilter = orderPre.getFilter();
			odoMainOpdOrder.tblExa = (TTable) odoMainControl.getComponent(TABLE_EXA);
			String rxNo = "";
				//修改细项的预开检查状态及预开时间
				rxNo = odoMainOpdOrder.tblExa.getItemString(row, "RX_NO");
//				orderPre.setFilter("RX_NO = '" + rxNo + "'");
//				orderPre.filter();
				String orderSetCode = odoMainOpdOrder.tblExa.getItemString(row, "ORDERSET_CODE");//主项的医嘱代码
				String groupNo = odoMainOpdOrder.tblExa.getItemString(row, "ORDERSET_GROUP_NO");//主项的医嘱代码
				// 循环查找细项
			if(tNode.getValue().equals("Y")){
				for (int i = 0; i < orderPre.rowCount(); i++) {
					orderPre.setFilter("RX_NO = '" + rxNo + "'");
					orderPre.filter();
					if (orderPre.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
						 && orderPre.getItemString(i, "ORDERSET_GROUP_NO").equals(groupNo)
						 &&orderPre.getItemString(i, "RX_NO").equals(rxNo)) {
						orderPre.setItem(i, "IS_PRE_ORDER", "Y");
					}
				}
				odoMainOpdOrder.tblExa.setLockCell(row, "PRE_DATE", false);
				orderPre.setFilter(lastFilter);
				orderPre.filter();
				if (!odoMainOpdOrder.initSetTable(TABLE_EXA, true))
					odoMainControl.messageBox("E0026"); // 初始化检验检查失败
			}else{//当有预开检查状态变为非预开检查状态时
				for (int i = 0; i < orderPre.rowCount(); i++) {
					orderPre.setFilter("RX_NO = '" + rxNo + "'");
					orderPre.filter();
					if (orderPre.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
						 && orderPre.getItemString(i, "ORDERSET_GROUP_NO").equals(groupNo)
						 &&orderPre.getItemString(i, "RX_NO").equals(rxNo)) {
						orderPre.setItem(i, "IS_PRE_ORDER", "N");
						orderPre.setItem(i, "PRE_DATE", "");
					}
				}
				odoMainOpdOrder.tblExa.setLockCell(row, "PRE_DATE", true);
				if (!odoMainOpdOrder.initSetTable(TABLE_EXA, true))
					odoMainControl.messageBox("E0026"); // 初始化检验检查失败
				odoMainOpdOrder.tblExa.setItem(row, "PRE_DATE", null);
			}
		}
		if ("PRE_DATE".equalsIgnoreCase(colName)) {//预执行时间
			if (!checkExaIsSave(order, row)){//====预开检查是否保存校验
				return true;
			}
			//修改细项的预开检查状预开时间
			odoMainOpdOrder.tblExa = (TTable) odoMainControl.getComponent(TABLE_EXA);
			OpdOrder orderPre = odoMainControl.odo.getOpdOrder();
			String lastFilter = orderPre.getFilter();
			String rxNo = "";
//			if(!(tNode.getValue().equals(null)&&"".equals(tNode.getValue()))){
				//修改细项的预开检查状态及预开时间
				rxNo = odoMainOpdOrder.tblExa.getItemString(row, "RX_NO");
				TTable tblExa1 = (TTable) odoMainControl.getComponent(TABLE_EXA);
				String orderSetCode = tblExa1.getItemString(row, "ORDERSET_CODE");//主项的医嘱代码
				String groupNo = tblExa1.getItemString(row, "ORDERSET_GROUP_NO");//主项的医嘱代码
				orderPre.setFilter("RX_NO = '" + rxNo + "'");
				orderPre.filter();
				// 循环查找细
				for (int i = 0; i < orderPre.rowCount(); i++) {
					if (orderPre.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
						 && orderPre.getItemString(i, "ORDERSET_GROUP_NO").equals(groupNo)
						 &&orderPre.getItemString(i, "RX_NO").equals(rxNo)) {
						orderPre.setItem(i, "PRE_DATE", tNode.getValue());
					}
				}
//			}
			orderPre.setFilter(lastFilter);
			orderPre.filter();
			if (!odoMainOpdOrder.initSetTable(TABLE_EXA, true))
				odoMainControl.messageBox("E0026"); // 初始化检验检查失败
		}
		//预开检查判断 yanjing 20131211  end 
		//-----start 20130111 caoyong 改变 MED_APPLY表中 URGENT_FLG的值
		if("URGENT_FLG".equalsIgnoreCase(colName)){
			OpdOrder opdO= odoMainControl.odo.getOpdOrder();
			String  strCatType=opdO.getItemString(row, "CAT1_TYPE");
			String  strApplicationNo=opdO.getItemString(row, "MED_APPLY_NO");
			String  strOrderNo=opdO.getItemString(row, "RX_NO");
			String  strSEQNo=opdO.getItemString(row, "SEQ_NO");
			opdO.getMedApply().setFilter("CAT1_TYPE='" + strCatType
					+ "' AND APPLICATION_NO='"+strApplicationNo
			        + "' AND ORDER_NO='"+strOrderNo	
			        + "' AND SEQ_NO='"+strSEQNo
			);
			opdO.getMedApply().filter();
			opdO.getMedApply().setItem(0, "URGENT_FLG", tNode.getValue());
		}
		//执行科室变化，MED_APPLAY同步数据
		if("EXEC_DEPT_CODE".equalsIgnoreCase(colName)){
			OpdOrder opdO= odoMainControl.odo.getOpdOrder();
			String  strCatType=opdO.getItemString(row, "CAT1_TYPE");
			String  strApplicationNo=opdO.getItemString(row, "MED_APPLY_NO");
			String  strOrderNo=opdO.getItemString(row, "RX_NO");
			String  strSEQNo=opdO.getItemString(row, "SEQ_NO");
			opdO.getMedApply().setFilter("CAT1_TYPE='" + strCatType
					+ "' AND APPLICATION_NO='"+strApplicationNo
			        + "' AND ORDER_NO='"+strOrderNo	
			        + "' AND SEQ_NO='"+strSEQNo
			);
			opdO.getMedApply().filter();
			opdO.getMedApply().setItem(0, "EXEC_DEPT_CODE", tNode.getValue());
		}		
		//--end--start 20130111 caoyong 
		return false;
	}
	
	/**
	 * 检验检查校验
	 * 
	 * @param order
	 * @param row
	 * @param name
	 * @return
	 */
	private boolean checkExa(OpdOrder order, int row) throws Exception{
//		if (null == odoMainControl.odoMainEkt.ektReadParm || odoMainControl.odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
//		} else {
//			if (order.getItemData(row, "BILL_FLG").equals("Y")) {
				for (int i = 0; i < order.rowCount(); i++) {
					if (StringUtil.isNullString(order.getItemString(i,
							"ORDER_CODE"))) {
						continue;
					}
					if (order.getItemData(row, "RX_NO").equals(
							order.getItemData(i, "RX_NO"))) {
						if (!"PHA".equals(order.getItemData(row, "CAT1_TYPE"))
								&& "Y".equals(order.getItemData(row, "EXEC_FLG"))) {
							odoMainControl.messageBox("已到检,不能修改或删除操作!");
							return false;
						}
					}
				}
//			}
//		}
		return true;
	}
	/**
	 * 预开检查是否保存校验
	 * 
	 * @param order
	 * @param row
	 * @param name
	 * @return
	 */
	private boolean checkExaIsSave(OpdOrder order, int row) throws Exception{
		String sql = "SELECT * FROM OPD_ORDER WHERE CASE_NO = '"+order.getItemData(row, "CASE_NO")+"' " +
		"AND RX_NO = '"+order.getItemData(row, "RX_NO")+"' AND SEQ_NO = '"+order.getItemData(row, "SEQ_NO")+"'";
        TParm parm = new TParm (TJDODBTool.getInstance().select(sql));
         if(parm.getCount()>0){
	          odoMainControl.messageBox("该预开检查已经保存，不可修改!");
	           return false;
          }
         return true;
	}

	/**
	 * 新增检验检查
	 * 
	 * @param parm
	 *            SysFee
	 * @param row
	 *            TABLE 选中行
	 * @param column
	 *            TABLE 选中列
	 */
	@Override
	public void insertExa(TParm parm, int row, int column) throws Exception{
		int oldRow = row;
		TTable table = (TTable) odoMainControl.getComponent(TABLE_EXA);
		OpdOrder order = (OpdOrder) table.getDataStore();
		// add by yanj 2013/07/17 门急诊适用校验
		// 门诊
		if (ODOMainReg.O.equalsIgnoreCase(odoMainControl.odoMainReg.admType)) {
			// 判断是否住院适用医嘱
			if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
				// 不是门诊适用医嘱！
				odoMainControl.messageBox("不是门诊适用检验检查。");
				return;
			}
		}
		// 急诊
		if (ODOMainReg.E.equalsIgnoreCase(odoMainControl.odoMainReg.admType)) {
			if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
				// 不是门诊适用医嘱！
				odoMainControl.messageBox("不是急诊适用检验检查。");
				return;
			}
		}
		// $$===========add by yanj 2013/07/17 门急诊适用校验
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (odoMainControl.messageBox(
							"提示信息/Tip",
							"该医嘱已经开立，是否继续？\r\n/This order exists,Do you proceed it again?",
							0) == 1) {
				table.setDSValue(row);
				return;
			}
		}
		String[] rxNos = order.getRx(EXA);
		if (rxNos == null || rxNos.length < 1) {
			return;
		}

		String rxNo = odoMainControl.getValueString(EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.messageBox("E0029"); // 没有处方签
			return;
		}

		//zhangp
//		int groupNo = order.getMaxGroupNo();
		String newFilter = "(";
		for (int i = 0; i < rxNos.length; i++) {
			newFilter += "RX_NO='" + rxNos[i].split(",")[0] + "'";
			if(i + 1 < rxNos.length){
				newFilter += " OR ";
			}else{
				newFilter += ") AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')";
			}
		}
		String oldFilter = odoMainControl.odo.getOpdOrder().getFilter();
		odoMainControl.odo.getOpdOrder().setFilter(newFilter);
		odoMainControl.odo.getOpdOrder().filter();
		int groupNo = odoMainControl.odo.getOpdOrder().getMaxGroupNo();
		odoMainControl.odo.getOpdOrder().setFilter(oldFilter);
		odoMainControl.odo.getOpdOrder().filter();
		
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = Operator.getDept();
		}
		// Date d1=new Date();
		odoMainOpdOrder.initOrder(order, row, parm, null);
		String orderCode = parm.getValue("ORDER_CODE");
		order.setItem(row, "ORDERSET_CODE", orderCode);
		order.setItem(row, "SETMAIN_FLG", "Y");
		order.setItem(row, "HIDE_FLG", "N");
		order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		order.setItem(row, "OWN_PRICE", 0.0);
		order.setItem(row, "DR_NOTE", parm.getValue("DESCRIPTION"));
		String labNo = order.getLabNo(row, odoMainControl.odo);
		if (StringUtil.isNullString(labNo)) {
			odoMainControl.messageBox("E0049"); // 取得检验号失败
			order.deleteRow(row);
			order.newOrder(EXA, rxNo);
			table.setDSValue();
			return;
		}
		order.setItem(row, "MED_APPLY_NO", labNo);
		order.setActive(row, true);
		TParm parmDetail = SYSOrderSetDetailTool.getInstance()
				.selectByOrderSetCode(parm.getValue("ORDER_CODE"));
		if (parmDetail.getErrCode() != 0) {
			System.out.println(parmDetail.getErrText());
		}
		if (parmDetail.getErrCode() != 0) {
			odoMainControl.messageBox("E0050"); // 取得细相数据错误
			return;
		}
		odoMainOpdOrder.rxType = "5";
		int count = parmDetail.getCount();
		for (int i = 0; i < count; i++) {
			row = order.newOrder(odoMainOpdOrder.rxType, rxNo);
			odoMainOpdOrder.initOrder(order, row, parmDetail.getRow(i), null);
			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			// zhangyong20110616
			order.setItem(row, "COST_CENTER_CODE", odoMainOpdOrder.getCostCenter(execDept));
			order.setItem(row, "HIDE_FLG", parmDetail.getValue("HIDE_FLG", i));
			order.setItem(row, "MED_APPLY_NO", labNo);
			order.setItem(row, "ORDERSET_CODE", orderCode);
			order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			double qty = TypeTool.getDouble(parmDetail.getData("TOTQTY", i));
			order.setItem(row, "DOSAGE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "DISPENSE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "TAKE_DAYS", 1);
			order.setItem(row, "MEDI_UNIT", parmDetail.getValue("UNIT_CODE",
							i));
			order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
					"UNIT_CODE", i));
			order.setItem(row, "AR_AMT", ODOTool.getInstance().roundAmt(BIL.chargeTotCTZ(odoMainControl.odoMainPat.ctz[0],
					odoMainControl.odoMainPat.ctz[1], odoMainControl.odoMainPat.ctz[2], order.getItemString(row, "ORDER_CODE"),
					order.getItemDouble(row, "DOSAGE_QTY"), odoMainControl.serviceLevel)));
			order.setItem(row, "IS_PRE_ORDER", "N");//caowl 20131118
			order.setItem(row, "PRE_DATE", "");//caowl 20131118
			order.setActive(row, true);
			table.setLockCell(row-1, "PRE_DATE", true);//yanjing 20140121
		}
		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			odoMainControl.odo.getOpdOrder().newOrder(odoMainOpdOrder.rxType, rxNo);
		}
		odoMainOpdOrder.initSetTable(TABLE_EXA, true);
		order.itemNow = false;
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(table.getColumnIndex("EXEC_DEPT_CODE"));
		table.setLockCell(row-1, "PRE_DATE", true);//yanjing 20140121
		Map insColor = OdoUtil.getInsColor(odoMainControl.odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		table.setRowTextColorMap(insColor);
	}
	
	/**
	 * 新增检验检查
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) throws Exception{
		TParm parm = (TParm) obj;
		if(!TABLE_EXA.equals(odoMainOpdOrder.tableName)){
			odoMainOpdOrder.tableName = TABLE_EXA;
		}
		TTable table = (TTable) odoMainControl.getComponent(TABLE_EXA);

		int column = table.getSelectedColumn();
		table.acceptText();

		if (StringUtil.isNullString(odoMainOpdOrder.tableName)) {
			odoMainControl.messageBox("E0034"); // 取得数据错误
			return;
		}
		if ("N".equalsIgnoreCase(parm.getValue("ORDERSET_FLG"))) {
			odoMainControl.messageBox("E0044"); // 该医嘱不是集合医嘱主项
			return;
		}

		OpdOrder order = (OpdOrder) table.getDataStore();
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
		int row = order.rowCount() - 1;
		String orderCode = order.getItemString(row, "ORDER_CODE");
		// 判断是否已经超过看诊时限
		if (!odoMainControl.odoMainReg.canEdit()) {
			table.setDSValue(row);
			odoMainControl.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (orderCode != null && orderCode.trim().length() > 0) {
			odoMainControl.messageBox("E0045"); // 已开立医嘱不可变更，请删除此医嘱或新开立
			table.setDSValue(row);
			return;
		}
		insertExa(parm, row, column);
	}

	/**
	 * 删除一行检验检查
	 * ==========pangben 2013-4-24
	 */
	private boolean deleteRowExa(OpdOrder order ,int row,TTable table)throws Exception{
		if (!odoMainOpdOrder.checkSendPah(order, row))// 检查已发药已到检
			return false;
		String rxNo = odoMainControl.getValueString(EXA_RX);
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
			return false;
		} 
		String orderSetCode = order.getItemString(row, "ORDERSET_CODE");//集合医嘱代码
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");//集合医嘱组号
		String orderCode = order.getItemString(row, "ORDER_CODE");//代码
		order.deleteOrderSet(rxNo, orderSetCode, groupNo, orderCode, order
				.getItemString(row, "SEQ_NO"));
		table.setDSValue();
		Map insColor = OdoUtil.getInsColor(odoMainControl.odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		//exaFLg = true;// 删除显示检验检查单
		odoMainOpdOrder.calculateCash(odoMainOpdOrder.tableName, AMT_TAG);
		//斯巴达
		ODOTool.getInstance().deleteReg(order, row, rxNo, orderCode);
		return true;
	}

	@Override
	public void insertPack(TParm parm, boolean flg)throws Exception {
		// TODO Auto-generated method stub
		insertExaPack(parm);
	}
	
	/**
	 * 插入组套数据
	 * 
	 * @param parmExa
	 *            TParm
	 */
	private void insertExaPack(TParm parmExa)throws Exception {
		odoMainOpdOrder.rxType = EXA;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		TComboBox rxExa = (TComboBox) odoMainControl.getComponent(EXA_RX);
		TTable table = (TTable) odoMainControl.getComponent(TABLE_EXA);
		String rxNo = rxExa.getSelectedID();
		// this.messageBox_(rxNo);
		String orderCode = parmExa.getValue("ORDER_CODE");
		// System.out.println("orderCode---"+orderCode);
		boolean memPackageFlg = "Y".equals(parmExa.getValue("MEM_PACKAGE_FLG"));//套餐
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(
				SYSSQL.getSYSFee(orderCode)));
		if (sysFee == null || sysFee.getErrCode() != 0) {
			odoMainControl.messageBox("E0034");
			return;
		}
		// 填充主项数据
		sysFee = sysFee.getRow(0);
		int row = table.getShowParmValue().getCount() - 1;
		order.setFilter("RX_NO='" + rxNo
				+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
		order.filter();
		// order.showDebug();
		order.setItem(row, "RX_NO", rxNo);
		odoMainOpdOrder.initOrder(order, row, sysFee, null);
		// System.out.println("111111-----"+order.getRowParm(row));
		order.setItem(row, "DR_NOTE", parmExa.getValue("DESCRIPTION"));
		String labNo = order.getLabNo(row, odoMainControl.odo);
		if (StringUtil.isNullString(labNo)) {
			odoMainControl.messageBox("E0049");
			order.deleteRow(row);
			order.newOrder(EXA, rxNo);
			table.setDSValue();
			return;
		}
		order.setItem(row, "MED_APPLY_NO", labNo);
		order.setActive(row, true);
		String execDept = sysFee.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = Operator.getDept();
		}
		order.setItem(row, "ORDERSET_CODE", orderCode);
		order.setItem(row, "SETMAIN_FLG", "Y");
		order.setItem(row, "HIDE_FLG", "N");
		int groupNo = order.getMaxGroupNo();
		order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		order.setItem(row, "OWN_PRICE", 0.0);
		if(memPackageFlg){
			order.setItem(row, "MEM_PACKAGE_ID", parmExa.getValue("ID"));
			order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130  
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", parmExa.getDouble("MEDI_QTY"));
			order.setItem(row, "DOSAGE_QTY", parmExa.getDouble("MEDI_QTY"));
			order.setItem(row, "DISPENSE_QTY", parmExa.getDouble("MEDI_QTY"));
			order.itemNow = false;
		}
		order.setActive(row, true);
		// 查询细相数据，填充细相
		TParm parmDetail = SYSOrderSetDetailTool.getInstance()
				.selectByOrderSetCode(orderCode);
		if (parmDetail.getErrCode() != 0) {
			odoMainControl.messageBox("E0050");
			return;
		}
		int count = parmDetail.getCount();
		TParm memPackgeParm = new TParm();
		if(memPackageFlg){
			memPackgeParm = ODOTool.getInstance().selectByOrderSetCodeMemPackage(parmExa.getValue("ID"), parmExa.getValue("TRADE_NO"));
		}
		for (int i = 0; i < count; i++) {
			row = order.newOrder(EXA, rxNo);
			// System.out.println("row----"+row);
			odoMainOpdOrder.initOrder(order, row, parmDetail.getRow(i), null);
			order.setItem(row, "MED_APPLY_NO", labNo);
			order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
					"OPTITEM_CODE", i));
			order
					.setItem(row, "CAT1_TYPE", parmDetail.getValue("CAT1_TYPE",
							i));
			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
					"INSPAY_TYPE", i));
			order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
					"RPTTYPE_CODE", i));
			order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
					"DEGREE_CODE", i));
			order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
					"CHARGE_HOSP_CODE", i));
			order.setItem(row, "HIDE_FLG", parmDetail.getValue("HIDE_FLG", i));
			order.setItem(row, "ORDERSET_CODE", orderCode);
			order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			double ownPrice = parmDetail.getDouble("OWN_PRICE", i);
			order.setItem(row, "OWN_PRICE", ownPrice);
			order.itemNow = true;
			double qty = parmDetail.getDouble("TOTQTY", i);
			order.setItem(row, "DOSAGE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "DISPENSE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "TAKE_DAYS", 1);
			order.itemNow = false;
			order
					.setItem(row, "MEDI_UNIT", parmDetail.getValue("UNIT_CODE",
							i));
			order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
					"UNIT_CODE", i));
			if(memPackageFlg){
				for (int j = 0; j < memPackgeParm.getCount(); j++) {
					if(parmDetail.getValue(
							"ORDER_CODE", i).equals(memPackgeParm.getValue("ORDER_CODE", j))){
						order.setItem(row, "AR_AMT", memPackgeParm.getDouble("RETAIL_PRICE", j));	
						order.setItem(row, "MEM_PACKAGE_ID", parmExa.getValue("ID"));	
						order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130
						order.itemNow = true;
						order.setItem(row, "MEDI_QTY", memPackgeParm.getDouble("ORDER_NUM", j));	
						order.setItem(row, "DOSAGE_QTY", memPackgeParm.getDouble("ORDER_NUM", j));	
						order.setItem(row, "DISPENSE_QTY", memPackgeParm.getDouble("ORDER_NUM", j));	
						order.itemNow = false;
					}
				}
			}else{
				order.setItem(row, "AR_AMT", ODOTool.getInstance().roundAmt(BIL.chargeTotCTZ(order
						.getItemString(row, "CTZ1_CODE"), order.getItemString(row,
						"CTZ2_CODE"), order.getItemString(row, "CTZ3_CODE"), order
						.getItemString(row, "ORDER_CODE"), order.getItemDouble(row,
						"DOSAGE_QTY"), odoMainControl.serviceLevel)));	
			}
			order.setItem(row, "PAYAMOUNT",order.getItemDouble(row, "OWN_AMT")-ODOTool.getInstance().roundAmt(order.getItemDouble(row, "AR_AMT")));  //add by huangtt 20150422
			order.setItem(row, "IS_PRE_ORDER", order.getItemData(row, "IS_PRE_ORDER"));//caowl 20131117
			order.setItem(row, "PRE_DATE", order.getItemData(row, "PRE_DATE"));//caowl 20131117
			order.setActive(row, true);
		}
		order.newOrder(EXA, rxNo);
		odoMainOpdOrder.initSetTable(TABLE_EXA, true);
		order.itemNow = false;
	}

	@Override
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception{
		// TODO Auto-generated method stub
		// 医嘱的事件
		this.odoMainControl = odoMainOpdOrder.odoMainControl;
		this.odoMainOpdOrder = odoMainOpdOrder;
		this.odoMainOpdOrder.tblExa = (TTable) odoMainControl.getComponent(TABLE_EXA);
		this.table = this.odoMainOpdOrder.tblExa;
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");
		table.addEventListener(TABLE_EXA + "->" + TTableEvent.CHANGE_VALUE,
				this, "onExaValueChange");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
	}

	@Override
	public void onChangeRx(String rxType)throws Exception {
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		rxNo = odoMainControl.getValueString(EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.odo.getOpdOrder().setFilter(
					"RX_TYPE='" + EXA + "' AND SETMAIN_FLG='Y'");
			odoMainControl.odo.getOpdOrder().filter();
			table.setDSValue();
			odoMainOpdOrder.calculateCash(TABLE_EXA, AMT_TAG);
			return;
		}
		odoMainControl.odo.getOpdOrder().setFilter(
				"RX_NO='" + rxNo
						+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
		odoMainControl.odo.getOpdOrder().filter();
		if (!StringUtil.isNullString(odoMainControl.odo.getOpdOrder().getItemString(
				odoMainControl.odo.getOpdOrder().rowCount() - 1, "ORDER_CODE"))) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}

		if (!odoMainOpdOrder.initSetTable(TABLE_EXA, false))
			odoMainControl.messageBox("E0039"); // 显示检验检查失败
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
		// 检验检查
		TParm exaResult = ((TParm) parm.getData("EXA"));
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			TComboBox rxExa = (TComboBox) odoMainControl.getComponent(EXA_RX);
			String rxNo = rxExa.getSelectedID();
			if (null==rxNo || rxNo.length()<=0) {
				return "检验检查没有初始化处方签";
			}
			if (!order.isExecutePrint(odoMainControl.odo.getCaseNo(), rxNo)) {
				return "已经打票的检验检查处方签不可以添加医嘱";
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
//		String tableName = NULLSTR;
		int count = -1;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String oldfilter = order.getFilter();
		StringBuffer billFlg=new StringBuffer();//判断是否可以删除 ，同一张处方签中的状态不相同不能删除
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		rxNo = (String) odoMainControl.getValue(EXA_RX);
//		tableName = TABLE_EXA;
		odoMainControl.setValue(AMT_TAG, NULLSTR);
		String preCaseNo = ODOTool.getInstance().queryCaseNoByRxNo(rxNo);
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		count = order.rowCount();
		table = (TTable) odoMainControl.getComponent(TABLE_EXA);
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			//======pangben 2012-8-10 添加已到检
			if (!odoMainOpdOrder.checkSendPah(order, i))// 检查已发药已到检
				return;
			//=========pangben 2013-1-29
			if (!odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED3)) {
				order.setFilter(oldfilter);
				order.filter();
				return;
			} 
			if(!odoMainOpdOrder.deleteSumRxOrder(order, i, billFlg)){
				return;
			}
		}
		// 提取出已有的所有 med_apply信息的 APPLICATION_NO
		MedApply med = odoMainControl.odo.getOpdOrder().getMedApply();
		Map appNo = new HashMap();
		for (int i = 0; i < med.rowCount(); i++) {
			String key = med.getItemString(i, "ORDER_NO")
					+ med.getItemString(i, "SEQ_NO")
					+ med.getItemString(i, "CAT1_TYPE");
			appNo.put(key, med.getItemString(i, "APPLICATION_NO"));
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			// 删除med_apply的对应信息
			// 判断是检验检查医嘱的主项 med_apply记录的是主项 删除主项
			if (("LIS".equals(order.getItemString(i, "CAT1_TYPE")) || "RIS"
					.equals(order.getItemString(i, "CAT1_TYPE")))
					&& "Y".equals(order.getItemString(i, "SETMAIN_FLG"))) {
				String labMapKey = order.getItemString(i, "RX_NO")
						+ order.getItemString(i, "SEQ_NO")
						+ order.getItemString(i, "CAT1_TYPE");
				med.deleteRowBy((String) appNo.get(labMapKey), order
						.getItemString(i, "RX_NO"), order.getItemInt(i,
						"SEQ_NO"), order.getItemString(i, "CAT1_TYPE"));
			}
			order.deleteRow(i);
		}
		order.setFilter(oldfilter);
		order.filter();
		table.setDSValue();
		//斯巴达
		String[] data = odoMainControl.odo.getOpdOrder().getRx(odoMainOpdOrder.rxType);//yanjing 20131212 检验检查处方签个数
		ODOTool.getInstance().deleteReg(preCaseNo, rxNo, data);//预开检查删除挂号信息校验
	}

	@Override
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception {
		// TODO Auto-generated method stub
		return deleteRowExa(order, row, table);
	}

	@Override
	public void deleteorderAuto(int row) throws Exception {
		// TODO Auto-generated method stub
		TTable table = null;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = NULLSTR;
		table = (TTable) odoMainControl.getComponent(TABLE_EXA);
		rx_no = odoMainControl.getValueString(EXA_RX);
		order.deleteRow(row, buff);
		table.setFilter("RX_TYPE='" + EXA
				+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
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

	@Override
	public void onChnChange(String fieldName, String type) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
