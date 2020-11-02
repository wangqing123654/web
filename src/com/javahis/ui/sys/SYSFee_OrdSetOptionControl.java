package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.JavaHisDebug;
import java.util.Vector;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import java.util.Map;
import java.util.HashMap;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class SYSFee_OrdSetOptionControl extends TControl {

	String dept;
	TTextFormat DEPT;
	TTextFormat TYPE;
	double defaultQty = 0.0;
	TDS packDDS = new TDS();
	TParm parmDDS = new TParm();
	TNumberTextField TOT_FEE;
	TTable table;
	TCheckBox all;// caowl 20130305 add
	// 用于记录正真的TDS的行数（相对于table）--是否选中
	Map recordRealyRow = new HashMap();
	// 获得prefech本地数据
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	public SYSFee_OrdSetOptionControl() {
	}

	public void onInit() {
		initParmFromOutside();
		this.callFunction("UI|TABLE|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");
		myInitControler();
		all = (TCheckBox) this.getComponent("ALL");// caowl 20130305 add
	}

	/**
	 * 初始化界面参数caseNo/stationCode
	 */
	public void initParmFromOutside() {
		// 从住院护士执行界面拿到参数TParm
		TParm outsideParm = (TParm) this.getParameter();
		if (outsideParm != null) {
			// 设定初始化界面的参数
			setDept(outsideParm.getData("PACK", "DEPT").toString());
		}
	}

	public void onSelect() {
		String type = this.getValueString("TYPE");
		showByOutside(type);

	}

	/**
	 * 外部接口调用
	 * 
	 * @param orderByout
	 *            String
	 * @param fromFlg
	 *            String
	 */
	public void showByOutside(String orderCode) {

		table = (TTable) this.getComponent("TABLE");

		String exeSel = getSpl(orderCode);
//		packDDS.setSQL(exeSel);
//		packDDS.retrieve();
		parmDDS = new TParm(TJDODBTool.getInstance().select(exeSel));
		if(parmDDS.getCount()<=0){
			this.messageBox("没有查询到套餐模板数据或字典未录入项目");
			TParm parm = new TParm();
			table.setParmValue(parm);
			return;
		}
		// 得到取得数量的行数/2,显示的行数
		//int row = packDDS.rowCount() / 2+1;  //modify by huangtt 20140926  
		table.setParmValue(parmDDS);//modify by xiongwg 20150204
//		// 得到该table上自己带的TDS
//		TDS tds = (TDS) table.getDataStore();
//		// 设置该TDS的行为5行（空的）
//		tds.getBuffer(TDS.PRIMARY).setCount(row);
//		// 该TDS的列名也为空
//		tds.setColumns(new String[] {});
//		// 创建观察者
//		magicalObserverPackD s = new magicalObserverPackD();
//		// 设置该观察者需要改变的TDS
//		s.setDS(packDDS);
//		// 设置观察者需要观察的TDS（假的）
//		tds.addObserver(s);
//		table.setDSValue();
	}

	/**
	 * table上的checkBox注册监听
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		// 获得点击的table对象
		TTable table = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		table.acceptText();
		// 获得选中的列/行
		int col = table.getSelectedColumn();
		int row = table.getSelectedRow();
		// 如果选中的是第11列就激发执行动作--执行
		String columnName = table.getParmMap(col);
		if (columnName.equals("N_SEL")) {
			boolean exeFlg;
			// 获得点击时的值
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			table.setItem(row, "N_SEL", exeFlg);
			// 记录TDS中真正的行数是否有选中标记（true/false）
			//recordRealyRow.put(row * 2 + "", exeFlg);
//			recordRealyRow.put(row+ "", exeFlg);
			// 选择和可编辑相反标记
//			table.setLockCell(row, 2, !exeFlg);//选中后取消会锁住使用量单元格
			// 计算价格，数量是默认数量
			//String ordCode = packDDS.getItemString(row * 2, "ORDER_CODE");
			String ordCode = parmDDS.getValue("ORDER_CODE",row);
			double diffQty = 0.0; // 保存差值
			// 是否选择
			if (exeFlg) {
				// 选择的时候初始化
				//defaultQty = packDDS.getItemDouble(row * 2, "DOSAGE_QTY");
				defaultQty = parmDDS.getDouble("DOSAGE_QTY",row);
				// 相差的值就是默认值
				diffQty = TypeTool.getDouble(table.getValueAt(row, 2));
				table.setLockCell(row, 2, exeFlg);//选中后锁使用量
			} else {
				// 得到当前值
				double nowQty = TypeTool.getDouble(table.getValueAt(row, 2));
				// 取消选择的时候修改单元格的数据为默认
				//table.setItem(row, "N_DOSAGE_QTY", defaultQty); // 内在（变回原来，已被再次选择时使用）
//				table.setItem(row, "DOSAGE_QTY", defaultQty); // 内在（变回原来，已被再次选择时使用）
				// table.setValueAt(defaultQty+"",row,2);//表面
				// 默认值-现在值=差
				diffQty = 0 - nowQty;
				table.setLockCell(row, 2, exeFlg);//未选中后不锁使用量
			}
			// 修改计算总价
				countTotFee(ordCode, diffQty);
		} 
//		else if (columnName.equals("S_SEL")) {
//			boolean exeFlg;
//			// 获得点击时的值
//			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
//			table.setItem(row, "S_SEL", exeFlg);
//			// 记录TDS中真正的行数是否有选中标记（true/false）
//			//recordRealyRow.put(row * 2 + 1 + "", exeFlg);		
//			table.setLockCell(row, 7, !exeFlg);
//			// 计算价格，数量是默认数量
//			//String ordCode = packDDS.getItemString(row * 2 + 1, "ORDER_CODE");
//			String ordCode = packDDS.getItemString(row + 1, "ORDER_CODE");
//			double diffQty = 0.0; // 保存差值
//			// 是否选择
//			if (exeFlg) {
//				// 选择的时候初始化
//				//defaultQty = packDDS.getItemDouble(row * 2 + 1, "DOSAGE_QTY");
//				// 相差的值就是默认值
//				diffQty = TypeTool.getDouble(table.getValueAt(row, 7));
//			} else {
//				// 得到当前值
//				double nowQty = TypeTool.getDouble(table.getValueAt(row, 7));
//				// 取消选择的时候修改单元格的数据为默认
//				table.setItem(row, "S_DOSAGE_QTY", defaultQty); // 内在（变回原来，已被再次选择时使用）
//				// table.setValueAt(defaultQty+"",row,2);//表面
//				// 默认值-现在值=差
//				diffQty = 0 - nowQty;
//			}
//			// 修改计算总价
//			countTotFee(ordCode, diffQty);
//		}

	}

	/**
	 * 得到SQL语句
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getSpl(String packcode) {

		String sql = " SELECT '' AS N_SEL,A.ORDER_DESC,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.OWN_PRICE," +
				"B.SPECIFICATION,B.MAN_CODE,A.ORDER_CODE " +
				" FROM SYS_ORDER_PACKD A,SYS_FEE B " + " WHERE A.PACK_CODE='"
				+ packcode + "' AND A.ORDER_CODE=B.ORDER_CODE ORDER BY A.SEQ_NO ";
		return sql;

	}

	/**
	 * 确定方法
	 * 
	 * @param args
	 *            String[]
	 */
	public void onOK() {
		// 收集要返回的数据（code/数量）
		TParm retDate = gainRtnDate();
		// 返回给调用界面的数据
		this.setReturnValue(retDate);
		this.closeWindow();
	}

	/**
	 * 收集要返回的数据
	 */
	public TParm gainRtnDate() {
		TParm result = new TParm();
		// 循环捞取选中的项目
//		for (int i = 0; i < packDDS.rowCount(); i++) {
		for (int i = 0; i < parmDDS.getCount(); i++) {
			// 以行号为主键进行map查询返回该行是否有选中
//			if (TypeTool.getBoolean(recordRealyRow.get(i + ""))) {
			if (parmDDS.getValue("N_SEL",i).equals("Y")) {
//				String orderCode = packDDS.getItemString(i, "ORDER_CODE");
//				double qty = packDDS.getItemDouble(i, "DOSAGE_QTY");
				String orderCode = parmDDS.getValue("ORDER_CODE",i);
				double qty = parmDDS.getDouble("DOSAGE_QTY",i);
				result.addData("ORDER_CODE", orderCode);
				result.addData("DOSAGE_QTY", qty);
//				result.addData("DOSAGE_UNIT", packDDS.getItemString(i, "DOSAGE_UNIT"));//====pangben 2013-08-05 添加单位
				result.addData("DOSAGE_UNIT", parmDDS.getValue("DOSAGE_UNIT",i));//====pangben 2013-08-05 添加单位
			}
			continue;
		}

		return result;
	}

	/**
	 * 监控第5列，如有值的改变刷新总费用
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onChangeFee(TTableNode cell) {

		int row = cell.getRow();		
		if (cell.getColumn() == 2) {
			String oederCode = packDDS.getItemString(row * 2, "ORDER_CODE");
			double oldValue = TypeTool.getDouble(cell.getOldValue());
			double newValue = TypeTool.getDouble(cell.getValue());
			// 取得新老值得差
			double diffValue = newValue - oldValue;
			// 用该项目单价*数量差额+原来的总费用=现在的总费用
			countTotFee(oederCode, diffValue);
		}
//		if (cell.getColumn() == 7) {
//			String oederCode = packDDS.getItemString(row * 2 + 1, "ORDER_CODE");
//			double oldValue = TypeTool.getDouble(cell.getOldValue());
//			double newValue = TypeTool.getDouble(cell.getValue());
//			// 取得新老值得差
//			double diffValue = newValue - oldValue;
//			// 用该项目单价*数量差额+原来的总费用=现在的总费用
//			countTotFee(oederCode, diffValue);
//		}
	}

	/**
	 * 根据orderCode和修改的cell的差数计算新的总价
	 * 
	 * @param orderCode
	 *            String
	 * @param cell
	 *            TTableNode
	 */
	public void countTotFee(String orderCode, double diffValue) {

		// 得到单价
		double ownPrice = TypeTool.getDouble(getSysFeeValue(orderCode,
				"OWN_PRICE"));
		// 用该项目单价*数量差额+原来的总费用=现在的总费用
		double totFee = diffValue * ownPrice
				+ TypeTool.getDouble(TOT_FEE.getValue());
		TOT_FEE.setValue(totFee);
	}

	/**
	 * 首先得到所有UI的控件对象/注册相应的事件 设置
	 */
	public void myInitControler() {

		DEPT = (TTextFormat) this.getComponent("DEPT");
		TYPE = (TTextFormat) this.getComponent("TYPE");
		// 得到table控件
		table = (TTable) this.getComponent("TABLE");
		//屏蔽掉事件值改变-xiongwg20150205 start
//		table.addEventListener(
//				table.getTag() + "->" + TTableEvent.CHANGE_VALUE, this,
//				"onChangeFee");
		// PS:锁住某一行--可以将来打开某一个cell
		//屏蔽掉事件值改变-xiongwg20150205 end
		table.setLockCellColumn(2, true);
//		table.setLockCellColumn(7, true);
		TOT_FEE = (TNumberTextField) this.getComponent("TOT_FEE");

		DEPT.setValue(getDept());
	}

	/**
	 * 取消
	 * 
	 * @param args
	 *            String[]
	 */
	public void onCANCLE() {
		switch (messageBox("提示信息", "确定取消选择？", this.YES_NO_OPTION)) {
		case 0:
			this.closeWindow();
		case 1:
			break;
		}
		return;
	}

	// caowl 20130305 start
	/**
	 * 全选
	 */
	public void onSelAll() {
		table.acceptText();
		if (table == null) {
			return;
		}
		int row = table.getRowCount();
		double totFee = 0.0;// 总价
		boolean exeFlg = all.isSelected();
		if (exeFlg) {

			for (int i = 0; i < row; i++) {
				table.setItem(i, "N_SEL", true);
//				table.setItem(i, "S_SEL", true);
			}
			for (int i = 0; i < 2 * row; i++) {
//				String ordCode = packDDS.getItemString(i, "ORDER_CODE");
				String ordCode = parmDDS.getValue("ORDER_CODE",i);
				double ownPrice = TypeTool.getDouble(getSysFeeValue

				(ordCode, "OWN_PRICE"));
				// 选择的时候初始化
//				double qty = packDDS.getItemDouble(i, "DOSAGE_QTY");
				double qty = parmDDS.getDouble("DOSAGE_QTY",i);

				totFee += qty * ownPrice;
//				recordRealyRow.put(i + "", true);
			}
			table.setLockCellColumn(2, exeFlg);//选中后锁使用量
		} else {
			for (int i = 0; i < row; i++) {

				table.setItem(i, "N_SEL", false);
//				table.setItem(i, "S_SEL", false);
			}
			for (int i = 0; i < 2 * row; i++) {
//				recordRealyRow.put(i + "", false);
			}
			totFee = 0.0;
			table.setLockCellColumn(2, exeFlg);//未选中后不锁使用量
		}
		TOT_FEE.setValue(totFee);

	}

	// caowl 20130305 end

	/**
	 * 
	 * @param s
	 *            根据的ORDER_CODE
	 * @param colName
	 *            要查的列名
	 * @return String
	 */
	public String getSysFeeValue(String s, String colName) {
//		if (dataStore == null)
//			return s;
//		String bufferString = dataStore.isFilter() ? dataStore.FILTER
//				: dataStore.PRIMARY;
//		TParm parm = dataStore.getBuffer(bufferString);
//		Vector vKey = (Vector) parm.getData("ORDER_CODE");
//		Vector vOwnPrice = (Vector) parm.getData(colName);
		if(parmDDS.getCount()<=0)
			return s;	
		Vector vKey = (Vector) parmDDS.getData("ORDER_CODE");
		Vector vOwnPrice = (Vector) parmDDS.getData(colName);
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (s.equals(vKey.get(i)))
				return "" + vOwnPrice.get(i);
		}
		return s;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDSETOPTION.x");
	}

}
