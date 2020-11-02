package com.javahis.ui.odo;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jdo.ins.INSTJTool;
import jdo.odo.OpdOrder;
import jdo.opd.ODOTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSAntibioticTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSSQL;
 
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
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
 * Title: 门诊医生工作站处方签接口西药处方签实现类
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站处方签接口西药处方签实现类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODORxMed implements IODORx{
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	ODOMainPat odoMainPat;
	
	public TTable table;
	public static final String TABLE_MED = "TABLEMED"; // 西成药表
	public final static String MED_RX = "MED_RX";
	public static final String MED = "1";
	public static final int MED_INT = 1;
	public static final int TABBEDPANE_INDEX = 2;
	public static final String ORDERCAT1TYPE = "PHA_W";
	public static final String AMT_TAG = "MED_AMT";
	
	public static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	public static final String URLPHAREDRUGMSG = "%ROOT%\\config\\pha\\PHAREDrugMsg.x";
	public static final String URLODOMAINDELETERX = "%ROOT%\\config\\odo\\ODOMainDeleteRx.x";
	
	private static final String NULLSTR = "";
	
	private Map<String, String> routeDoseMap;
	
	/**
	 * 初始化西药
	 */
	public void initMed() throws Exception{
		this.odoMainPat = odoMainControl.odoMainPat;  //add by huangtt 20141120
		boolean isInit = odoMainOpdOrder.isTableInit(TABLE_MED);
		String rxNo = NULLSTR;
		if (!isInit) {
			rxNo = odoMainOpdOrder.initRx(MED_RX, MED);
			odoMainOpdOrder.setTableInit(TABLE_MED, true);
		} else {
			rxNo = odoMainControl.getValueString(MED_RX);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_MED, isInit))
			odoMainControl.messageBox("E0028"); // 初始化西成药失败
		odoMainOpdOrder.onChangeRx(MED);
		routeDoseMap = ODOTool.getInstance().getRouteDoseMap();
	}

	@Override
	public void init() throws Exception{
		// TODO Auto-generated method stub
		initMed();
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
	public void onMedCreateEditComponent(Component com, int row, int column) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(TABLE_MED);
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		// 集合医嘱不可更新
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (odoMainOpdOrder.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		odoMainOpdOrder.tableName = TABLE_MED;
		odoMainOpdOrder.rxName = MED_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
		parm.setData("ORDER_DEPT_CODE", odoMainOpdOrder.orderDeptCode);
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ORDER", odoMainControl.getConfigParm().newConfig(
				URLSYSFEEPOPUP), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");
	}

	/**
	 * 西成药TABLE点击药品名称弹出合理化用药查询界面
	 */
	public void onQueryRsDrug() throws Exception{
		odoMainControl.messageBox("onQueryRsDrug无方法");
	}
	
	/**
	 * 西药值改变事件，算价钱
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean onMedValueChange(TTableNode tNode) throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLE_MED);
		int column = tNode.getColumn();
		int row = table.getSelectedRow();
		String columnName = table.getParmMap(column);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		//物联网修改
		TParm spcReturn=null;
		boolean flg=true;//管控物联网删除、修改医嘱操作
		if (Operator.getSpcFlg().equals("Y")) {
			TParm spcParm = new TParm();
			spcParm.setData("CASE_NO", odoMainControl.caseNo);
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm.setData("RX_NO",rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(//获得此医嘱的药品状态
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
			if (!this.checkDrugCanUpdate(order, "MED", row, flg,spcReturn)) { // 判断是否可以修改（有没有进行审,配,发）
				if(spcReturn.getValue("PhaRetnCode").length()>0)
					odoMainControl.messageBox("已经退药,请删除处方签操作");//===pangben 2013-7-17 修改物联网提示消息
				else
					odoMainControl.messageBox("E0189");
				return true;
			}
		}
		//物联网修改
		if (checkPha(order, row, "MED",flg))
			return true;
		if (!odoMainOpdOrder.deleteOrder(order, row, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {// pangben ====2012
			// 2-28//pangben2012 2-28
			return true;
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue(NULLSTR);
			return true;
		}
		if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			//皮试药品且用法是皮试用法时，清空批号和皮试结果,yanjing,20140404
			String orderCode = order.getRowParm(row).getValue("ORDER_CODE");
			String psSql = "SELECT SKINTEST_FLG FROM PHA_BASE WHERE ORDER_CODE = '"+orderCode+"'";
			TParm psResult = new TParm(TJDODBTool.getInstance().select(psSql));
			if(psResult.getValue("SKINTEST_FLG",0).equals("Y")){//皮试药品
				if(tNode.getValue().equals("PS")){//用法为皮试用法，清空批号和皮试结果
					order.setItem(row, "BATCH_NO", "");
					order.setItem(row, "SKINTEST_FLG", "");
				}else{
					//查询批号和皮试结果
					String psResultSql = "SELECT BATCH_NO,SKINTEST_FLG FROM OPD_ORDER WHERE CASE_NO = '"+odoMainControl.caseNo+"' " +
							"AND ORDER_CODE = '"+orderCode+"' AND ROUTE_CODE = 'PS' AND BATCH_NO IS NOT NULL " +
									"AND SKINTEST_FLG IS NOT NULL ORDER BY ORDER_DATE DESC";
					TParm psResultParm = new TParm(TJDODBTool.getInstance().select(psResultSql));
					String batchNo = psResultParm.getValue("BATCH_NO", 0);
					String skinTestFlg = psResultParm.getValue("SKINTEST_FLG", 0);
					order.setItem(row, "BATCH_NO", batchNo);
					order.setItem(row, "SKINTEST_FLG",skinTestFlg);
				}
				
			}
		}
		//========pangben 2012-7-18 备用药品已经收费不可以修改
		if ("RELEASE_FLG".equalsIgnoreCase(columnName)) {
			TParm parm=order.getRowParm(row);
			if (odoMainControl.odo.getOpdOrder().getOrderCodeIsBillFlg(parm)) {
				odoMainControl.messageBox("已收费,不可以修改备药操作");
				return true;	
			}
		}
		order.getRowParm(row);
		String orderCode = order.getItemString(row, "ORDER_CODE");
		double mediQty = TypeTool.getDouble(tNode.getValue());
		final int inRow = row;
//		final String execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
		final String execDept = order.getItemString(row, "EXEC_DEPT_CODE");
		final String orderCodeFinal = order.getItemString(row, "ORDER_CODE");
		final String columnNameFinal = columnName;
		// 日份 列
		if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			int day = SYSAntibioticTool.getInstance().getAntibioticTakeDays(
					orderCode);
			if (day > 0 && TypeTool.getInt(tNode.getValue()) > day) {
				if (odoMainControl.messageBox(
						"提示信息/Tip",
						"超过抗生素类天数限制,是否继续开立?\r\nThe days you ordered is more than the standard days of antiVirus.Do you proceed anyway?",
						odoMainControl.YES_NO_OPTION) == 1) {
					table.setDSValue(row);
					return true;
				}
			}
			
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("EXEC_DEPT_CODE".equalsIgnoreCase(columnName)) { // 执行科室 列
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				return true;
			}
			order.itemNow=false;
			final String execDeptFinal = TypeTool.getString(tNode.getValue());
			setOnMedValueChange(inRow, execDeptFinal, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("MEDI_QTY".equalsIgnoreCase(columnName)) { // 用量
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
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
			order.itemNow=false;
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("FREQ_CODE".equalsIgnoreCase(columnName)) { // 频次
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			if (!"Y".equals(order.getItemString(row, "LINKMAIN_FLG")) && order.getItemString(row, "LINK_NO").length() > 0) {
				tNode.setValue(tNode.getOldValue());
			}
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal, order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) { // 连接医嘱
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			
			String freqCode = order.getItemString(tNode.getRow(), "FREQ_CODE");
			String routeCode = order.getItemString(tNode.getRow(), "ROUTE_CODE");
			if ("Y".equals(tNode.getValue() + NULLSTR)) {
				String linkNO = order.getItemString(tNode.getRow(), "LINK_NO"); // 连结号
				String rxNo = order.getItemString(tNode.getRow(), "RX_NO"); // 处方签号
				// 循环查找子项
				for (int i = tNode.getRow(); i < order.rowCount(); i++) {
					if (linkNO.equals(order.getItemString(i, "LINK_NO"))
							&& rxNo.equals(order.getItemString(i, "RX_NO"))
							&& !"".equals(order.getItemString(i, "ORDER_CODE"))) {
						order.setItem(i, "FREQ_CODE", freqCode);
						order.setItem(i, "ROUTE_CODE", routeCode);
					}
				}
				table.setDSValue();
			}
			
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) { // 连结号
			int value = TypeTool.getInt(tNode.getValue());
			String link_main_flg = order.getItemString(row, "LINKMAIN_FLG");
			// if(oldValue==0&&value>0){
			// return true;
			// }
			if ("0".equals(tNode.getValue().toString())) {
				return true;
			}
			if ("Y".equals(link_main_flg) || value < 0) {
				return true;
			}

			if (!StringUtil
					.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				int linkNo = order.getMaxLinkNo();
				if (value >= 0 && value <= linkNo) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) { // 用法
//			if (StringUtil.isNullString(orderCode)) {
//				return true;
//			}
			String routeCode = tNode.getValue() + NULLSTR;
			
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				odoMainControl.messageBox("E0019"); // 取得用法失败
				return true;
			} else if (result != 1) {
				odoMainControl.messageBox("E0020"); // 西成药不能使用该用法
				return true;
			}
			// 集合医嘱子项与主项联动
			if ("Y".equals(order.getItemString(tNode.getRow(), "LINKMAIN_FLG"))) {
				String linkNO = order.getItemString(tNode.getRow(), "LINK_NO"); // 连结号
				String rxNo = order.getItemString(tNode.getRow(), "RX_NO"); // 处方签号
				// 循环查找子项
				for (int i = 0; i < order.rowCount(); i++) {
					if (linkNO.equals(order.getItemString(i, "LINK_NO"))
							&& rxNo.equals(order.getItemString(i, "RX_NO"))
							&& !NULLSTR.equals(order.getItemString(i, "ORDER_CODE"))
							&& !routeCode.equals(order.getItemString(i,
									"ROUTE_CODE"))) {
						order.setItem(i, "ROUTE_CODE", routeCode);
					}
				}
				table.setDSValue();
			}else{
				//待删除
//				if(order.getItemString(tNode.getRow(), "LINK_NO").length() > 0){
//					tNode.setValue(tNode.getOldValue());
//				}
			}
			return false;
		} else {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}
		odoMainOpdOrder.calculateCash(TABLE_MED, AMT_TAG);
		return false;
	}

	/**
	 * 检查药品剂型分类 是否可以开在同一处方签上
	 * 
	 * @param order
	 *            OpdOrder
	 * @param DOSE_TYPE
	 *            String
	 * @return boolean
	 */
	public boolean checkDOSE_TYPE(OpdOrder order, String DOSE_TYPE) throws Exception{
		boolean flg = true;
		// ORDER_CODE为空表示第一行是空医嘱 是新处方签的开始 所以返回true
		if (order.getItemString(0, "ORDER_CODE").length() <= 0) {
			return flg;
		}
		// ============pangben 2012-2-29 添加管控
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!odoMainOpdOrder.deleteOrder(order, i, ODOMainOpdOrder.MEGBILLED, ODOMainOpdOrder.MEGBILLED2)) {
				return false;
			}
		}
		// ============pangben 2012-2-29 stop
		// 口服处方签
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("O")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
		}
		// 针剂，点滴处方签
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("I")
				|| order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("F")) {
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
		}
		// 外用药处方签
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("E")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				odoMainControl.messageBox("E0194"); // 不同剂型分类的药品不可开立在同一张处方签上
				flg = false;
			}
		}
		return flg;
	}
	
	/**
	 * 判断是否检核库存
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	private boolean isCheckKC(String orderCode) throws Exception{
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0)) // 如果是药品备注那么就不检核库存
			return false;
		else
			// 不是药品备注的 要检核库存
			return true;
	}
	
	/**
	 * 校验这个处方签的全部药品是否已经发药
	 * 
	 * @param order
	 * @param row
	 * @return
	 */
	public boolean checkPha(OpdOrder order, int row, String name,
			boolean spcFlg) throws Exception{
//		if (null == odoMainControl.odoMainEkt.ektReadParm || odoMainControl.odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
//		} else {
//			if (order.getItemData(row, "BILL_FLG").equals("Y")) {
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if (order.getItemData(row, "RX_NO").equals(
					order.getItemData(i, "RX_NO")) && order.getItemData(row, "PRESRT_NO").equals(
							order.getItemData(i, "PRESRT_NO"))) {
				// 物联网修改
				if (Operator.getSpcFlg().equals("Y")) {// ====pangben 2013-4-17
														// 校验物联网注记
					if (!this.checkDrugCanUpdate(order, name, i, spcFlg, null)) { // 判断是否可以修改（有没有进行审,配,发）
						odoMainControl.messageBox("此处方签有药品已审核或发药,不能删除或修改医嘱!");
						return true;
					}
					// 物联网修改
				} else {
					TParm parm = new TParm();
					if (!order.checkDrugCanUpdate(name, i, parm, true)) { // 判断是否可以修改（有没有进行审,配,发）
						if (null != parm.getValue("MESSAGE_FLG")
								&& parm.getValue("MESSAGE_FLG").equals("Y")) {
							if (parm.getValue("MESSAGE_INDEX").equals("Y")) {
								odoMainControl.messageBox(parm.getValue("MESSAGE"));
							}else{
								odoMainControl.messageBox("此处方签有药品已计费,不可以修改或删除操作!");
							}
						} else {
							odoMainControl.messageBox("此处方签有药品已审核或发药,不能删除或修改医嘱!");
						}
						return true;
					}
				}
			}
		}
//			}
//		}
		return false;
	}

	/**
	 * 检核药品是否已经审配发 是否可以退药
	 * 
	 * @param type
	 *            String "EXA":西药 "CHN":中药
	 * @param row
	 *            int
	 *            flg =true检验当前操作是修改医嘱还是删除医嘱，修改医嘱操作退药不可以修改但是可以删除
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(OpdOrder order, String type, int row,
			boolean flg,TParm spcParm) throws Exception{
		return odoMainControl.odoMainSpc.checkDrugCanUpdate(order, type, row, flg, spcParm);
	}
	
	/**
	 * 判断是否是过敏史中的药品
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public boolean isAllergy(String orderCode) throws Exception{
		for (int i = 0; i < odoMainControl.odo.getDrugAllergy().rowCount(); i++) {
			if (orderCode.equalsIgnoreCase(odoMainControl.odo.getDrugAllergy().getItemString(
					i, "DRUGORINGRD_CODE"))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 新增西、成药
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOrderReturn(String tag, Object obj) throws Exception{
		TParm parm = (TParm) obj;
		// ============xueyf modify 20120331
		if (parm.getValue("CAT1_TYPE") != null
				&& !parm.getValue("CAT1_TYPE").equals("PHA")) {
			odoMainControl.messageBox("不允许开立非西成药医嘱。");
			return;
		}
		// add by lx 2012/05/07 门急诊适用校验
		// 门诊
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
		// $$===========add by lx 2012/05/07 门急诊适用校验
		String rxNo;
		TTabbedPane tTabbedPane = (TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		int index = tTabbedPane.getSelectedIndex();
		if (!TABLE_MED.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxCtrl.TABLE_CTRL
						.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxChnMed.TABLE_CHN
						.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxExa.TABLE_EXA
						.equalsIgnoreCase(odoMainOpdOrder.tableName)
				|| !ODORxOp.TABLE_OP
						.equalsIgnoreCase(odoMainOpdOrder.tableName)) {
			switch (index) {
			case TABBEDPANE_INDEX:
				odoMainOpdOrder.tableName = TABLE_MED;
				break;
			case ODORxCtrl.TABBEDPANE_INDEX:
				odoMainOpdOrder.tableName = ODORxCtrl.TABLE_CTRL;
				break;
			default:
				odoMainOpdOrder.tableName = ODORxOp.TABLE_OP;
				break;
			}
		}
		TTable table = (TTable) odoMainControl.getComponent(odoMainOpdOrder.tableName);
		table.acceptText();
		int column = table.getSelectedColumn();
		table.acceptText();
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		// ===========pangben 2012-6-15 start 注释 取消5个医嘱提示消息
		int row = order.rowCount() - 1;
		int oldRow = row;
		String amtTag;
		if (TABLE_MED.equalsIgnoreCase(odoMainOpdOrder.tableName)) {
			odoMainOpdOrder.rxType = MED;
			amtTag = AMT_TAG;
		} else if (ODORxCtrl.TABLE_CTRL.equalsIgnoreCase(odoMainOpdOrder.tableName)) {
			odoMainOpdOrder.rxType = ODORxCtrl.CTRL;
			amtTag = ODORxCtrl.AMT_TAG;
		} else {
			odoMainOpdOrder.rxType = ODORxOp.OP;
			amtTag = ODORxOp.AMT_TAG;
		}
		if (StringUtil.isNullString(odoMainOpdOrder.tableName)) {
			odoMainControl.messageBox("E0034"); // 取得数据错误
			return;
		}
		// 判断是否已经超过看诊时限
		if (!odoMainControl.odoMainReg.canEdit()) {
			table.setDSValue(row);
			odoMainControl.messageBox_("已超过看诊时间不可修改");
			return;
		}
		if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			odoMainControl.messageBox("E0040"); // 不能替代该数据，请重新新增或删除该数据
			table.setDSValue(row);
			return;
		}
		if (!OdoUtil.isHavingLiciense(parm.getValue("ORDER_CODE"), Operator
				.getID())) {
			odoMainControl.messageBox("E0051"); // 没有证照权限
			table.setValueAt(NULLSTR, row, column);
			return;
		}
		//=======================yanjing 门急诊皮试批号是否修改校验
		TParm opdResult = new TParm();
		opdResult = ismodifyBatchNo(odoMainControl.caseNo,parm);
		if(opdResult.getValue("SKINTEST_FLG", 0).equals("1")){
			 String batchNo = opdResult.getValue("BATCH_NO",0);
			 String order_desc = parm.getValue("ORDER_DESC");
			  if(odoMainControl.messageBox("提示信息/Tip",
						order_desc+ "的批号为:"+batchNo+"，皮试结果为:(+)阳性,继续开立?",0) != 0){
				  return;
			  }
	} 
		
		//====================yanjing 门急诊皮试批号是否修改校验
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (odoMainControl.messageBox("提示信息/Tip",
					"该医嘱已经开立，是否继续？\r\nThis order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt(NULLSTR, oldRow, column);
				return;
			}
		}
		if (!isAllergy(parm.getValue("ORDER_CODE"))) {
			if (odoMainControl.messageBox(
							"提示信息/Tip",
							"病人对该药品或成分过敏，是否继续开立？\r\nThe Pat is allergic to this order.Do you proceed anyway?",
							0) == 1) {
				table.setDSValue(row);
				return;
			}
		}
		//添加医保限制校验
		//yanjing 添加医保自费校验 20130719
		TParm insCheckParm=INSTJTool.getInstance().orderCheck(parm.getValue("ORDER_CODE"), odoMainControl.odoMainReg.reg.getCtz1Code(), odoMainControl.odoMainReg.admType, odoMainControl.odoMainReg.reg.getInsPatType());
		if (insCheckParm.getErrCode() < 0&&!(insCheckParm.getErrCode()==-6) &&!(insCheckParm.getErrCode()==-7)) {
			if(odoMainControl.messageBox("提示","此药品" + insCheckParm.getErrText()+",是否继续",0)== 1){
				table.setValueAt(NULLSTR, row, column);
				return;
			}
			}else if(insCheckParm.getErrCode()==-6||insCheckParm.getErrCode()==-7){//-6:医保自费标记 -7：医保自费增付标记
				odoMainControl.messageBox("此药品为"+insCheckParm.getErrText());		
		}
//		if (!ODORxOp.OP.equalsIgnoreCase(odoMainOpdOrder.rxType)
//				&& !order.isDrug(parm.getValue("ORDER_CODE"), odoMainOpdOrder.rxType)) {
		if (!order.isDrug(parm.getValue("ORDER_CODE"), odoMainOpdOrder.rxType)) {
			odoMainControl.messageBox("E0113");
			return;
		}
		if (ODORxCtrl.CTRL.equalsIgnoreCase(order.getItemString(row, "RX_TYPE"))) {
			if (!order.isSameDrug(parm.getValue("ORDER_CODE"), row)) {
				odoMainControl.messageBox("E0114");
				return;
			}
		}
		// 判断要是是否能开到该处方签上（是否是专用处方药品）
		int re = order.isPrnRx(order.getItemString(row, "RX_NO"), parm
				.getValue("ORDER_CODE"));
		if (re == 1) {
			odoMainControl.messageBox("E0190");
			return;
		} else if (re == 2) {
			odoMainControl.messageBox("E0191");
			return;
		} else if (re == 3) {
			odoMainControl.messageBox("E0192");
			return;
		}
		rxNo = (String) odoMainControl.getValue(odoMainOpdOrder.rxName);
		TParm parmBase = PhaBaseTool.getInstance().selectByOrderRoute(
				parm.getValue("ORDER_CODE")); //modify by huangtt 20141104 
		if (parmBase.getErrCode() < 0) {
			odoMainControl.messageBox("E0034");
			return;
		}
//		if (!checkDOSE_TYPE(order, parmBase.getValue("DOSE_TYPE", 0))) {
//			return;
//		}
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		int tabbedIndex = ((TTabbedPane) odoMainControl.getComponent("TTABPANELORDER"))
				.getSelectedIndex();
		switch (tabbedIndex) {
		case 1:
			if (StringUtil.isNullString(execDept)) {
				execDept = odoMainControl.getValueString("OP_EXEC_DEPT");
			}
			break;
		case 2:
			execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
			break;
		case 3:
			execDept = odoMainControl.getValueString("CHN_EXEC_DEPT_CODE");
			break;
		case 4:
			execDept = odoMainControl.getValueString("CTRL_RBORDER_DEPT_CODE");
			break;
		}
		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
				if (checkPha(order, row, "MED",true))//开立医嘱校验处方签是否可以开立
					return ;
//				if (isCheckKC(parm.getValue("ORDER_CODE"))) // 判断是否是“药品备注”
					// 物联网
//					if (!INDTool.getInstance().inspectIndStock(execDept,
//							parm.getValue("ORDER_CODE"), 0.0)) {
//						// $$==========add by lx 2012-06-19加入存库不足，替代药提示
//						TParm inParm = new TParm();
//						inParm
//								.setData("orderCode", parm
//										.getValue("ORDER_CODE"));
//						odoMainControl.openDialog(URLPHAREDRUGMSG,
//								inParm);
//						// $$==========add by lx 2012-06-19加入存库不足，替代药提示
//						order.setActive(row, false);
//						return;
//					}
			}else{
				if (!odoMainControl.odoMainSpc.checkSpcPha(order)) {
					return;
				}
			}
		}
		order.setActive(row, true);
		parm.setData("EXEC_DEPT_CODE", execDept);
		// 根据给入条件初始化一行order
//		System.out.println("parmBase is :"+parmBase);
		odoMainOpdOrder.initOrder(order, row, parm, parmBase);
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			order.setItem(row, "FREQ_CODE", "STAT");
		}
		boolean flgInf = false;
		String inf = "";
		int num = 0;
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(odoMainOpdOrder.rxType, rxNo)) {
			int newRow = order.newOrder(odoMainOpdOrder.rxType, rxNo);
			if (order.getItemInt(row, "LINK_NO") > 0) {
				// //连结医嘱 查看是否是 “医嘱备注” 如果是 则不计算总量
				order.itemNow = true;
				order.setItem(newRow, "LINK_NO", order.getItemInt(row,
						"LINK_NO"));
				TParm linkMainParm = order.getLinkMainParm(order.getItemInt(
						row, "LINK_NO"));
				order.itemNow = false;
				order.setItem(row, "TAKE_DAYS", linkMainParm
						.getData("TAKE_DAYS"));
				order.itemNow = false;
				order.setItem(row, "FREQ_CODE", linkMainParm
						.getData("FREQ_CODE"));
				order.setItem(row, "EXEC_DEPT_CODE", linkMainParm
						.getData("EXEC_DEPT_CODE"));
				order.setItem(row, "ROUTE_CODE", linkMainParm
						.getData("ROUTE_CODE"));
				if (null!=parm.getValue("SKINTEST_FLG")
						&&parm.getValue("SKINTEST_FLG").length()>0) {
					order.setItem(row, "EXEC_DATE", parm.getValue("EXEC_DATE"));//皮试执行时间add by huangjw 20141031
					order.setItem(row, "SKINTEST_FLG", parm.getValue("SKINTEST_FLG"));//皮试结果
					order.setItem(row, "BATCH_NO", parm.getValue("BATCH_NO"));//批号
				}
				order.itemNow = true;
				inf = linkMainParm.getValue("INFLUTION_RATE");
				num = row;
				flgInf =true;
			}
		}
		if(flgInf){
			TDataStore dst = table.getDataStore();
			dst.setItem(num, "INFLUTION_RATE", inf);
			table.setDSValue();
		}else{
			TDataStore dst = table.getDataStore();
			dst.setItem(table.getSelectedRow(), "INFLUTION_RATE", 0);
			table.setDSValue();
		}
//		odoMainControl.messageBox(flgInf+"");
//		odoMainControl.messageBox(inf+"");
		odoMainOpdOrder.initNoSetTable(rxNo, odoMainOpdOrder.tableName, false);
//		odoMainControl.messageBox("123456");
		
		
		
		
		odoMainOpdOrder.calculateCash(odoMainOpdOrder.tableName, amtTag);
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map inscolor = OdoUtil.getInsColor(odoMainControl.odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * 删除一行西成药医嘱
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowMed(OpdOrder order ,int row,TTable table)throws Exception{
		if (!odoMainOpdOrder.checkSendPah(order, row))// 检查已发药已到检
			return false;
		table.acceptText();//====pangben 2013-1-6 删除西药鼠标焦点在用量上面,打印的处方签下面的医嘱用量会变成删除的医嘱用量
		if(!odoMainOpdOrder.deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}
	
	/**
	 * 插入药品模板
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOrdPack(TParm parm,boolean onFechFlg) throws Exception{
		int count = parm.getCount("ORDER_CODE");
		if (count < 0) {
			return;
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String tableName = TABLE_MED;
		odoMainOpdOrder.rxType = MED;
		tableName = TABLE_MED;
		String rxNo = odoMainControl.getValueString(MED_RX);
		String execDept = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
		if (StringUtil.isNullString(rxNo)) {
			odoMainOpdOrder.initRx(MED_RX, MED);
		}
		rxNo = odoMainControl.getValueString(MED_RX);
		order.setFilter("RX_NO='" + rxNo + "'");
		if (!order.filter()) {
			return;
		}
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
				: (order.newOrder(MED, rxNo));		
		for (int i = 0; i < count; i++) {
			//yanjing20130428 传回校验，总量
			TParm parmBase = new TParm();
			if (onFechFlg) {
				parmBase = odoMainOpdOrder.initParmBase(parm, i);
			}else {
				parmBase = PhaBaseTool.getInstance().selectByOrderRoute(
						parm.getValue("ORDER_CODE", i));
			}	
			TParm parmRow = parm.getRow(i);
			boolean memPackageFlg = "Y".equals(parmRow.getValue("MEM_PACKAGE_FLG"));//套餐
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
			sysFee = sysFee.getRow(0);
			sysFee.setData("EXEC_DEPT_CODE", execDept);
			sysFee.setData("CAT1_TYPE", "PHA");
			odoMainOpdOrder.initOrder(order, row, sysFee, parmBase);			
			order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
			order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
			order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
			order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));			
			order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
			order.setItem(row, "DR_NOTE", parmRow.getValue("DESCRIPTION"));  //add by huangtt 20141114
			if(memPackageFlg){
				order.itemNow = true;
				order.setItem(row, "MEM_PACKAGE_FLG", "Y"); //add by huangtt 20150130
				
				//add by huangtt 20150410 start  用法和频次
				if(parmBase.getCount()>0){
					order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE",0));
					order.setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE",0));
					order.setItem(row, "MEDI_UNIT", parmBase.getValue("MEDI_UNIT",0));			
				}				
				//add by huangtt 20150410 end	
				
//				order.setItem(row, "MEDI_UNIT", ""); 
			    order.setItem(row, "MEDI_QTY", 0);
				order.setItem(row, "DOSAGE_UNIT", parmRow.getValue("MEDI_UNIT"));
				order.setItem(row, "DOSAGE_QTY", parmRow.getDouble("MEDI_QTY"));
				order.setItem(row, "AR_AMT", parmRow.getValue("AR_AMT"));	
				order.setItem(row, "MEM_PACKAGE_ID", parmRow.getValue("ID"));
				order.setItem(row, "GIVEBOX_FLG", "N");
				order.setItem(row, "DISPENSE_UNIT", parmRow.getValue("MEDI_UNIT"));
				order.setItem(row, "DISPENSE_QTY", parmRow.getDouble("MEDI_QTY"));
				
//				if(order.getItemString(row, "DOSAGE_UNIT").equals(order.getItemString(row, "DISPENSE_UNIT"))){
//					order.setItem(row, "DISPENSE_QTY", order.getItemData(row, "DOSAGE_QTY"));
//				}else{
//					TParm unitParm = new TParm(TJDODBTool.getInstance().select(
//							SYSSQL.getPhaTransUnit(parmRow.getValue("ORDER_CODE"))));
//					double stockTrans = unitParm.getDouble("DOSAGE_QTY", 0); // 发药单位对库存单位转换率  
//					int stockQty = Integer.parseInt(Math.round(order.getItemDouble(row, "DOSAGE_QTY") / stockTrans) + "");
//					order.setItem(row, "DISPENSE_QTY", stockQty == 0 ? 1:stockQty);
//
//				}
				double ownAmt = ODOTool.getInstance().roundAmt(order.getItemDouble(row, "OWN_PRICE")
						* order.getItemDouble(row, "DOSAGE_QTY"));
				order.setItem(row, "OWN_AMT", ownAmt);
				order.setItem(row, "PAYAMOUNT",ownAmt-ODOTool.getInstance().roundAmt(order.getItemDouble(row, "AR_AMT")));
				
				order.itemNow = false;
				
			}
			
			order.setItem(row, "LINKMAIN_FLG", parmRow.getValue("LINKMAIN_FLG"));
			order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));			
			order.setActive(row, true);
			row = order.newOrder(MED, rxNo);
		}		
		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			order.newOrder(MED, rxNo);
		}
		odoMainOpdOrder.initNoSetTable(rxNo, tableName, false);
		odoMainOpdOrder.calculateCash(tableName, AMT_TAG);
		order.itemNow = false;				
		Map inscolor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
	}

	@Override
	public void insertPack(TParm parm, boolean flg) throws Exception{
		// TODO Auto-generated method stub
		this.insertOrdPack(parm, flg);
	}
	
	@Override
	public void onInitEvent(ODOMainOpdOrder odoMainOpdOrder) throws Exception{
		// TODO Auto-generated method stub
		this.odoMainOpdOrder = odoMainOpdOrder;
		this.odoMainControl = odoMainOpdOrder.odoMainControl;
		// 西药TABLE
		odoMainOpdOrder.tblMed = (TTable) odoMainControl.getComponent(TABLE_MED);
		this.table = odoMainOpdOrder.tblMed;
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		table.addEventListener(TABLE_MED + "->" + TTableEvent.CHANGE_VALUE,
				this, "onMedValueChange");
		
		
	}
	
	/**
	 * 西药的checkBox事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj)throws Exception{
		return odoMainOpdOrder.onCheckBox_influne(obj);
	}

	@Override
	public void onChangeRx(String rxType) throws Exception{
		// TODO Auto-generated method stub
		String rxNo = NULLSTR;
		rxNo = odoMainControl.getValueString(MED_RX);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.odo.getOpdOrder().setFilter(
					"RX_TYPE='" + MED + "' AND ORDER_CODE <>''");
			odoMainControl.odo.getOpdOrder().filter();
			table.setDSValue();
			odoMainOpdOrder.calculateCash(TABLE_MED, AMT_TAG);
			return;
		}
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (!odoMainOpdOrder.initNoSetTable(rxNo, TABLE_MED, false))
			odoMainControl.messageBox("E0036"); // 显示西药失败
		odoMainControl.setValue("MED_RBORDER_DEPT_CODE", odoMainOpdOrder.phaCode);
		odoMainOpdOrder.calculateCash(TABLE_MED, AMT_TAG);
	}

	@Override
	public boolean medAppyCheckDate(OpdOrder order, int row) throws Exception{
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 西药值改变事件 共用
	 * @param inRow
	 * @param execDept
	 * @param orderCodeFinal
	 * @param columnNameFinal
	 */
	private void setOnMedValueChange(final int inRow,final String execDept,
			final String orderCodeFinal,final String columnNameFinal,final double oldDosageQty) throws Exception{
		
		if(!odoMainOpdOrder.indOrgIsExinvs.contains(execDept)){
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (!odoMainOpdOrder.checkStoreQty(inRow, execDept, orderCodeFinal,
								columnNameFinal,oldDosageQty)) {
							return ;
						}
					} catch (Exception e) {
					}
				}
			});
			
		}
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
		// 药品
		TParm orderResult = ((TParm) parm.getData("ORDER"));
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = odoMainControl.getValueString(MED_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "西成药没有初始化处方签";
			}
			if (!order.isExecutePrint(odoMainControl.odo.getCaseNo(), rxNo)) {
				return "已经打票的西成药处方签不可以添加医嘱";
			}
		}
		return null;
	}

	@Override
	public void getTempSaveParm() throws Exception{
		// TODO Auto-generated method stub
		odoMainControl.odo.getOpdOrder().setFilter(
						"RX_TYPE='"+ MED+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
		odoMainControl.odo.getOpdOrder().filter();
	}

	@Override
	public void setopdRecipeInfo(OpdOrder order) throws Exception{
		// TODO Auto-generated method stub
		order.setFilter("RX_TYPE='" + MED + "'");
		order.filter();
	}
	
	@Override
	public void onDeleteOrderList(int rxType) throws Exception{
		String rxNo = NULLSTR;
		String tableName = NULLSTR;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String oldfilter = order.getFilter();
		
		odoMainControl.openDialog(
				URLODOMAINDELETERX, odoMainControl, false);
		
		tableName = TABLE_MED;
		odoMainControl.setValue(AMT_TAG, NULLSTR);
		if (StringUtil.isNullString(tableName)) {
			odoMainControl.messageBox("E0034"); // 取得数据错误
			return;
		}
		table = (TTable) odoMainControl.getComponent(tableName);
		order.setFilter(oldfilter);
		order.filter();
		table.setDSValue();
	}

	@Override
	public boolean deleteRow(OpdOrder order, int row, TTable table)
			throws Exception {
		// TODO Auto-generated method stub
		return deleteRowMed(order, row, table);
	}

	@Override
	public void deleteorderAuto(int row) throws Exception {
		// TODO Auto-generated method stub
		TTable table = null;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = NULLSTR;
		table = (TTable) odoMainControl.getComponent(TABLE_MED);
		rx_no = odoMainControl.getValueString(MED_RX);
		order.deleteRow(row);
		order.setFilter("RX_TYPE='" + MED
				+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
		table.filter();
		table.setDSValue();
	}

	@Override
	public boolean check(OpdOrder order, int row, String name,
			boolean spcFlg) throws Exception {
		// TODO Auto-generated method stub
		return checkPha(order, row, name, spcFlg);
	}

	@Override
	public void setOnValueChange(int inRow, String execDept,
			String orderCodeFinal, String columnNameFinal,final double oldDosageQty) throws Exception {
		// TODO Auto-generated method stub
		this.setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal, oldDosageQty);
	}

	@Override
	public void insertExa(TParm parm, int row, int column) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * 判断药品批次是否修改
	 * yanjing 20140327
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
		private TParm ismodifyBatchNo(String caseNo,TParm parm) throws Exception{
		  String orderCode = parm.getValue("ORDER_CODE");
		  String order_desc = parm.getValue("ORDER_DESC");
		TParm opdResult = new TParm();
		//是否做过皮试========================//皮试执行时间add by huangjw 20141031
		String opdSql = "SELECT A.ORDER_DESC,A.EXEC_DATE, A.BATCH_NO,A.SKINTEST_FLG FROM OPD_ORDER A,PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE " +
		"AND A.CASE_NO = '"+caseNo+"' AND B.SKINTEST_FLG = 'Y' AND A.ROUTE_CODE = 'PS' AND A.ORDER_CODE='"+orderCode +
				"' AND A.CAT1_TYPE = 'PHA' AND A.SKINTEST_FLG IS NOT NULL ORDER BY A.OPT_DATE DESC" ;

		opdResult = new TParm(TJDODBTool.getInstance().select(opdSql));
		if (opdResult.getCount()>0) {
			parm.setData("BATCH_NO", opdResult.getValue("BATCH_NO",0));
			parm.setData("SKINTEST_FLG", opdResult.getValue("SKINTEST_FLG",0));
			parm.setData("EXEC_DATE",opdResult.getValue("NS_EXEC_DATE",0));//皮试执行时间add by huangjw 20141031
//			if(opdResult.getValue("SKINTEST_FLG", 0).equals("1")){
//				 String batchNo = opdResult.getValue("BATCH_NO",0);
//				  if(odoMainControl.messageBox("提示信息/Tip",
//							order_desc+ "的批号为:"+batchNo+"结果为:(+)阳性,继续开立?",0) != 0){
//					  return opdResult;
//				  }
//		} 
		}else{
			parm.setData("BATCH_NO","");
			parm.setData("SKINTEST_FLG", "");
			parm.setData("EXEC_DATE","");//皮试执行时间add by huangjw 20141031
		}
	   return opdResult;
	}

	@Override
	public void onSortRx(boolean flg) throws Exception {
		// TODO Auto-generated method stub
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String lastFilter = order.getFilter();
		order.setFilter("RX_TYPE='1'");
		order.filter();
		List<String> list = new ArrayList<String>();
		String rxNo;
		for (int i = 0; i < order.rowCount(); i++) {
			rxNo = order.getItemString(i, "RX_NO");
			if(!list.contains(rxNo)){
				list.add(rxNo);
			}
		}
		
		for (String string : list) {
			order.setFilter("RX_NO = '" + string + "'");
			order.filter();
			sortRx(flg, order);
		}
		
		order.setFilter(lastFilter);
		order.filter();
		
		if(flg){
			odoMainOpdOrder.onTempSave(null, 0);
			odoMainControl.messageBox("分方成功");
		}
	}
	
	/**
	 * 最终整理
	 * @param finalList
	 * @param pha_link_all
	 */
	private void sortRxFinally(List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> finalList, List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> pha_link_all ){
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> lltemp = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> ltemp;
		List<com.javahis.ui.testOpb.bean.OpdOrder> lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		for (List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_link : pha_link_all) {
			
			for (int i = 0; i < pha_link.size(); i++) {
				ltemp = pha_link.get(i);
				if(ltemp.size()+lf.size()<=5){
					for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : ltemp) {
						lf.add(opdOrder);
					}
				}else if(ltemp.size()<=5){
					lltemp.add(lf);
					lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
					for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : ltemp) {
						lf.add(opdOrder);
					}
				}else{
					for (int j = 0; j < ltemp.size(); j++) {
						com.javahis.ui.testOpb.bean.OpdOrder opdOrder = ltemp.get(j);
						if( j % 5 == 0){
							lltemp.add(lf);
							lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
						}
						lf.add(opdOrder);
						if(j == ltemp.size() - 1){
							lltemp.add(lf);
							lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
						}
					}
				}
			}
			lltemp.add(lf);
			lf = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		}
		finalList.add(lltemp);
	}
	
	/**
	 * 通过中西药整理
	 * @param order
	 * @param i
	 * @param pha
	 * @param pha_dept
	 * @param pha_link
	 */
	private void sortRxByCw(OpdOrder order, int i, List<com.javahis.ui.testOpb.bean.OpdOrder> pha, List<String> pha_dept, List<String> pha_link){
		com.javahis.ui.testOpb.bean.OpdOrder opdOrder = new com.javahis.ui.testOpb.bean.OpdOrder();
		opdOrder.caseNo = order.getItemString(i,"CASE_NO");
		opdOrder.rxNo = order.getItemString(i,"RX_NO");
		opdOrder.seqNo = new BigDecimal(order.getItemString(i,"SEQ_NO"));
		opdOrder.routeCode = order.getItemString(i,"ROUTE_CODE");
		opdOrder.doseType = routeDoseMap.get(opdOrder.routeCode);
		opdOrder.execDeptCode = order.getItemString(i,"EXEC_DEPT_CODE");
		if(order.getItemString(i,"LINK_NO").length() > 0){
			opdOrder.linkNo = order.getItemString(i,"LINK_NO");
		}else{
			opdOrder.linkNo = "#";
		}
		pha.add(opdOrder);
		if(!pha_dept.contains(opdOrder.execDeptCode)){
			pha_dept.add(opdOrder.execDeptCode);
		}
		if(!pha_link.contains(opdOrder.linkNo)){
			pha_link.add(opdOrder.linkNo);
		}
	}
	
	/**
	 * 排序通过OEIF
	 * @param pha
	 * @param pha_o
	 * @param pha_if
	 * @param pha_e
	 */
	private void sortRxByOEIF(List<com.javahis.ui.testOpb.bean.OpdOrder> pha, 
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_o, 
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_if,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_e){
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : pha) {
			if("O".equals(opdOrder.doseType)){
				pha_o.add(opdOrder);
			}else if("I".equals(opdOrder.doseType)){
				pha_if.add(opdOrder);
			}else if("F".equals(opdOrder.doseType)){
				pha_if.add(opdOrder);
			}else if("E".equals(opdOrder.doseType)){
				pha_e.add(opdOrder);
			}
		}
	}
	
	/**
	 * 依据各种类明细分类
	 * @param pha_dept
	 * @param pha_o
	 * @param pha_if
	 * @param pha_e
	 * @param pha_o_dept
	 * @param pha_if_dept
	 * @param pha_e_dept
	 */
	private void sortRxByKind(List<String> pha_dept,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_o,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_if,
			List<com.javahis.ui.testOpb.bean.OpdOrder> pha_e,
			List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_o_dept,
			List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_if_dept,
			List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_e_dept){
		List<com.javahis.ui.testOpb.bean.OpdOrder> lt;
		for (String execDeptCode : pha_dept) {
			lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
			for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder_o : pha_o) {
				if(execDeptCode.equals(opdOrder_o.execDeptCode)){
					lt.add(opdOrder_o);
				}
			}
			pha_o_dept.add(lt);
			lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
			for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder_if : pha_if) {
				if(execDeptCode.equals(opdOrder_if.execDeptCode)){
					lt.add(opdOrder_if);
				}
			}
			pha_if_dept.add(lt);
			lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
			for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder_e : pha_e) {
				if(execDeptCode.equals(opdOrder_e.execDeptCode)){
					lt.add(opdOrder_e);
				}
			}
			pha_e_dept.add(lt);
		}
	}
	
	/**
	 * 取得中西药OEIF连接号数组
	 * @param pha_dept
	 * @param pha_link
	 * @return
	 */
	private List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> getPhaCwOEIFLinkList(List<List<com.javahis.ui.testOpb.bean.OpdOrder>> pha_dept,
			List<String> pha_link){
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phacw_oeif_link = new ArrayList<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> lltemp;
		List<com.javahis.ui.testOpb.bean.OpdOrder> lt;
		for (List<com.javahis.ui.testOpb.bean.OpdOrder> pcd : pha_dept) {
			lltemp = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
			for (String linkNo : pha_link) {
				lt = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
				for (com.javahis.ui.testOpb.bean.OpdOrder opdOrderPcd : pcd) {
					if(linkNo.equals(opdOrderPcd.linkNo)){
						lt.add(opdOrderPcd);
					}
				}
				lltemp.add(lt);
			}
			phacw_oeif_link.add(lltemp);
		}
		return phacw_oeif_link;	
	}
	
	private void sortRx(boolean flg, OpdOrder order){
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac_o = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac_if = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phac_e = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw_o = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw_if = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<com.javahis.ui.testOpb.bean.OpdOrder> phaw_e = new ArrayList<com.javahis.ui.testOpb.bean.OpdOrder>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phac_o_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phac_if_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phac_e_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phaw_o_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phaw_if_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> phaw_e_dept = new ArrayList<List<com.javahis.ui.testOpb.bean.OpdOrder>>();
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phac_o_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phac_if_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phac_e_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phaw_o_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phaw_if_link;
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> phaw_e_link;
		
		List<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>> finalList = new ArrayList<List<List<com.javahis.ui.testOpb.bean.OpdOrder>>>();
		
		List<String> phac_dept = new ArrayList<String>();
		List<String> phaw_dept = new ArrayList<String>();
		List<String> phac_link = new ArrayList<String>();
		List<String> phaw_link = new ArrayList<String>();
		List<Integer> presrtNoList = new ArrayList<Integer>();
		
		int presrtNo = 0;
		
		boolean sortFlg = true;
		for (int i = 0; i < order.rowCount(); i++) {
			if(!StringUtil.isNullString(order.getItemString(i,
					 "ORDER_CODE"))){
				if(!flg){
					sortFlg = order.getItemString(i, "PRESRT_NO").length() == 0;
				}
				sortFlg = sortFlg && !"Y".equals(order.getItemString(i, "EXEC_FLG")) 
					&& order.getItemString(i, "PHA_CHECK_CODE").length() == 0
					&& order.getItemString(i, "PHA_DOSAGE_CODE").length() == 0
					&& order.getItemString(i, "PHA_DISPENSE_CODE").length() == 0
					&& order.getItemString(i, "PHA_RETN_CODE").length() == 0;
				if("PHA_C".equals(order.getItemString(i, "ORDER_CAT1_CODE")) && sortFlg){
					if(!presrtNoList.contains(order.getItemInt(i, "PRESRT_NO"))){
						presrtNoList.add(order.getItemInt(i, "PRESRT_NO"));
					}
					sortRxByCw(order, i, phac, phac_dept, phac_link);
				}else if("PHA_W".equals(order.getItemString(i, "ORDER_CAT1_CODE")) && sortFlg){
					if(!presrtNoList.contains(order.getItemInt(i, "PRESRT_NO"))){
						presrtNoList.add(order.getItemInt(i, "PRESRT_NO"));
					}
					sortRxByCw(order, i, phaw, phaw_dept, phaw_link);
				}
			}
			if(presrtNo < order.getItemInt(i, "PRESRT_NO")){
				presrtNo = order.getItemInt(i, "PRESRT_NO");
			}
		}
		
		if(flg){
			if(JOptionPane.showConfirmDialog(null, "重新分方将更改 " + presrtNoList.size() + " 个处方签\r\n是否继续", "是否重新分方", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return;
			}
		}
		
		sortRxByOEIF(phac, phac_o, phac_if, phac_e);
		sortRxByOEIF(phaw, phaw_o, phaw_if, phaw_e);
		
		sortRxByKind(phac_dept, phac_o, phac_if, phac_e, phac_o_dept, phac_if_dept, phac_e_dept);
		sortRxByKind(phaw_dept, phaw_o, phaw_if, phaw_e, phaw_o_dept, phaw_if_dept, phaw_e_dept);
		
		List<com.javahis.ui.testOpb.bean.OpdOrder> lt;
		List<List<com.javahis.ui.testOpb.bean.OpdOrder>> lltemp;
		
		phac_o_link = getPhaCwOEIFLinkList(phac_o_dept, phac_link);
		phac_if_link = getPhaCwOEIFLinkList(phac_if_dept, phac_link);
		phac_e_link = getPhaCwOEIFLinkList(phac_e_dept, phac_link);
		phaw_o_link = getPhaCwOEIFLinkList(phaw_o_dept, phaw_link);
		phaw_if_link = getPhaCwOEIFLinkList(phaw_if_dept, phaw_link);
		phaw_e_link = getPhaCwOEIFLinkList(phaw_e_dept, phaw_link);
		
		sortRxFinally(finalList, phac_o_link);
		sortRxFinally(finalList, phac_if_link);
		sortRxFinally(finalList, phac_e_link);
		sortRxFinally(finalList, phaw_o_link);
		sortRxFinally(finalList, phaw_if_link);
		sortRxFinally(finalList, phaw_e_link);
		
		List<com.javahis.ui.testOpb.bean.OpdOrder> temp;
		for (List<List<com.javahis.ui.testOpb.bean.OpdOrder>> ol : finalList) {
			for (int i = 0; i < ol.size(); i++) {
				presrtNo++;
				temp = ol.get(i);
				for (com.javahis.ui.testOpb.bean.OpdOrder opdOrderTemp : temp) {
					for (int j = 0; j < order.rowCount(); j++) {
						if(opdOrderTemp.caseNo.equals(order.getItemString(j,"CASE_NO"))
								&& opdOrderTemp.rxNo.equals(order.getItemString(j,"RX_NO"))
								&& opdOrderTemp.seqNo.compareTo(new BigDecimal(order.getItemString(j,"SEQ_NO"))) == 0){
							order.setItem(j, "PRESRT_NO", presrtNo);
						}
					}
				}
			}
		}
	}

	@Override
	public void onChnChange(String fieldName, String type) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
