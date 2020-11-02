package com.javahis.ui.inv;

import com.dongyang.ui.event.TPopupMenuEvent;

import jdo.inv.INVOrgTool;
import jdo.inv.INVSQL;
import com.dongyang.data.TNull;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import jdo.inv.INVPackMTool;
import com.dongyang.control.TControl;
import jdo.inv.INVPackStockMTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import jdo.inv.INVPublicTool;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.manager.INVPackOberver;

/**
 * <p>
 * Title:灭菌打包
 * </p>
 * 
 * <p>
 * Description:灭菌打包
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw
 * @version 1.0
 */
public class INVSterilizationControl extends TControl {
	/**
	 * 手术包
	 */
	private TTable tableM;
	/**
	 * 物资明细
	 */
	private TTable tableD;
	/**
	 * 记录主表当先被选中的行
	 */
	private String initStatus = "";
	/**
	 * 消毒效期
	 */
	private int valueDate = 0;

	/**
	 * 初始化
	 */
	public void onInit() {
		// 物资入库主表
		tableM = (TTable) getComponent("TABLEM");
		// 物资入库明细
		tableD = (TTable) getComponent("TABLED");
		// tableD值改变事件
		this.addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
				"onTableDChargeValue");
		// 物资弹出窗口
		callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
				"%ROOT%\\config\\inv\\INVChoose.x");
		// 接受回传值
		callFunction("UI|PACK_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		iniAppOrgCombo();
		initAppOrgCode();
		// 初始化table
		onInitTable();
		// 添加观察者
		observer();
	}
	/**
	 * 初始化供应部门
	 */
	public void iniAppOrgCombo() {
		// 查询条件
		TParm parm = INVOrgTool.getInstance().getDept();
		TComboBox comboBox = (TComboBox) this.getComponent("ORG_CODE");
		comboBox.setParmValue(parm);
		comboBox.updateUI();
		// 表中数据
		comboBox = new TComboBox();
		comboBox.setParmMap("id:ORG_CODE;name:DEPT_CHN_DESC");
		comboBox.setParmValue(parm);
		comboBox.setShowID(true);
		comboBox.setShowName(true);
		comboBox.setExpandWidth(30);
		comboBox.setTableShowList("name");
	}
	/**
	 * 得到默认供应科室
	 * 
	 * @return String
	 */
	public void initAppOrgCode() {
		String appOrgCode = "";
		TParm fromOrgParm = INVPublicTool.getInstance().getOrgCode("B");
		if (fromOrgParm.getErrCode() >= 0) {
			appOrgCode = fromOrgParm.getValue("ORG_CODE", 0);
		}
		this.setValue("ORG_CODE", appOrgCode);
	}

	/**
	 * 利用主键为空先刷新三个表的datasotre
	 */
	public void onInitTable() {
		// 主表
		tableM.setSQL(INVSQL.getInitPackStockMSql());
		tableM.retrieve();
		// 细表
		tableD.setSQL(INVSQL.getInitPackStockDSql());
		tableD.retrieve();
	}

	/**
	 * 添加观察者
	 */
	public void observer() {
		// 给主表添加观察者
		TDataStore dataStore = new TDataStore();
		dataStore
				.setSQL("SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
		dataStore.retrieve();
		// 明细表添加观察者
		TDS tds = (TDS) tableD.getDataStore();
		tds.addObserver(new INVPackOberver());

		// 给主表添加观察者
		dataStore = new TDataStore();
		dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
		dataStore.retrieve();
		// 明细表添加观察者
		tds = (TDS) tableM.getDataStore();
		tds.addObserver(new INVPackTob(dataStore));

	}

	/**
	 * 手术包选择返回数据处理
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		// 调用处理返回数据的方法
		// 包号
		setValue("PACK_CODE", parm.getValue("PACK_CODE"));
		// 包名
		setValue("PACK_DESC", parm.getValue("PACK_DESC"));
		// 灭菌效期
		valueDate = parm.getInt("VALUE_DATE");
		onQuery();
	}

	/**
	 * 查询入口
	 */
	public void onQuery() {
		// 检核数据变更
		if (!checkValueChange())
			return;
		// 拿到查询sql
		String packCode = getValueString("PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("请输入手术包号!");
			return;
		}
		getDetialValue();
		// 得到默认回收和消毒的选择
		setCheckBoxValue();
		// 可以保存
		this.callFunction("UI|save|setEnabled", true);
		// 不可以撤销
		this.callFunction("UI|cancel|setEnabled", false);
		// 记录查询出数据的状态
		initStatus = tableM.getItemString(0, "STATUS");
	}

	/**
	 * 扫描条码
	 */
	public void onScream() {
		String packCode = getValueString("SCREAM");
		if (packCode == null || packCode.length() == 0) {
			return;
		}
		setValue("PACK_CODE", packCode.substring(0, packCode.length() - 4));
		setValue("PACK_SEQ_NO",
				packCode.substring((packCode.length() - 4), packCode.length()));
		this.onQuery();
	}
	/**
	 * 灭菌选择事件
	 */
	public void onSterilizationSelected() {
		if (tableM.getRowCount() <= 0)
			return;
		// 如果是不灭菌
		String selected = this.getValueString("STERILIZATION");
		if (selected.equals("N"))
			return;

		Timestamp sterilization_date = tableM.getItemTimestamp(0, "STERILIZATION_DATE");// 得到灭菌日期
		// 如果要勾选清洗就要检核是否在库
		String status = tableM.getItemString(0, "STATUS");
		// 出库状态 不可清洗
		if (status.equals("1")) {
			messageBox("该物资不可以灭菌!");
			setValue("STERILIZATION", "N");
		} else if (null != sterilization_date) {
			if (this.messageBox("提示", "该物资已灭菌!是否再次灭菌?", 2) != 0) {
				setValue("STERILIZATION", "N");
				return;
			}
		}
	}
	/**打包选择事件*/
	public void onUnPackSelected(){
		if (tableM.getRowCount() <= 0)
			return;
		// 如果是不灭菌
		String selected = this.getValueString("UNPACK");
		if (selected.equals("N"))
			return;

		Timestamp pack_date = tableM.getItemTimestamp(0, "PACK_DATE");// 得到灭菌日期
		// 如果要勾选清洗就要检核是否在库
		String status = tableM.getItemString(0, "STATUS");
		// 出库状态 不可清洗
		if (status.equals("1")) {
			messageBox("该物资不可以打包!");
			setValue("STERILIZATION", "N");
		} else if (null != pack_date) {
			if (this.messageBox("提示", "该物资已打包!是否再次打包?", 2) != 0) {
				setValue("UNPACK", "N");
				return;
			}
		}
	}

	/**
	 * 得到明细数据
	 */
	public void getDetialValue() {
		// 拿到查询sql
		String packCode = getValueString("PACK_CODE");
		int packSeqNo = getValueInt("PACK_SEQ_NO");
		// 得到查询手术包的sql
		String sql = INVSQL.getQueryStockMSql(packCode, packSeqNo);
		// 刷新主表
		tableRetrive(tableM, sql);
		// 明细表
		sql = INVSQL.getQueryStockDSql(packCode, packSeqNo);
		// 刷新细表
		tableRetriveD(tableD, sql);
		// 得到默认回收和消毒的选择
		setCheckBoxValue();
		if (tableM.getRowCount() <= 0)
			return;
		tableM.setSelectedRow(0);
		// 找到默认消毒数据
		setDisnfectionData(packCode);
	}

	/**
	 * 设置消毒默认数据
	 * 
	 * @param packCode
	 *            String
	 */
	public void setDisnfectionData(String packCode) {
		String sql = INVSQL.getPackMValueDateSql(packCode);
		if (sql == null || sql.equals(""))
			return;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			System.out.println("" + result.getErrText());
			return;
		}
		// 消毒效期
		int value = result.getInt("VALUE_DATE", 0);
		// 当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		// 日期
		Timestamp valueDate = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), value);
		// 选中的行
		int selectRow = tableM.getSelectedRow();
		// 消毒日期
		tableM.setItem(selectRow, "DISINFECTION_DATE", date);
		// 默认效期
		tableM.setItem(selectRow, "VALUE_DATE", valueDate);
	}

	/**
	 * 处理勾选事件
	 */
	public void setCheckBoxValue() {
		if (tableM.getRowCount() <= 0)
			return;
		String status = tableM.getItemString(0, "STATUS");
		// 状态 0 在库，1 出库, 2 已回收 3 已清洗 4 已消毒 5 已灭菌 6 已打包 7:维修中
		Timestamp sterilization_date = tableM.getItemTimestamp(0,
				"STERILIZATION_DATE");// 得到清洗日期
		// 如果状态不为1 并且清洗日期不为null
		if (!status.equals("1") && null == sterilization_date)
			setValue("STERILIZATION", "Y");
		else
			setValue("STERILIZATION", "N");

		Timestamp pack_date = tableM.getItemTimestamp(0, "PACK_DATE");// 得到清洗日期
		// 如果状态不为1 并且清洗日期不为null
		if (!status.equals("1") && null == pack_date)
			setValue("UNPACK", "Y");
		else
			setValue("UNPACK", "N");
	}

	/**
	 * 主表点击事件
	 */
	public void onTableMClecked() {
		// 点击的行
		int row = tableM.getSelectedRow();
		// 拿到主表点击时要产生的查询明细表的sql
		String packCode = tableM.getItemString(row, "PACK_CODE");
		int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
		// 明细表
		String sql = INVSQL.getQueryStockDSql(packCode, packSeqNo);
		// 刷新细表
		tableRetriveD(tableD, sql);
		TParm tableCtzParm = tableM.getDataStore().getRowParm(row);
		setTextValue(tableCtzParm);
	}

	/**
	 * 重新刷新表
	 * 
	 * @param table
	 *            TTable
	 * @param sql
	 *            String
	 */
	public void tableRetrive(TTable table, String sql) {
		table.setSQL(sql);
		table.retrieve();
		table.setDSValue();
	}
	/**
	 * 重新刷新细表
	 * 
	 * @param table
	 *            TTable
	 * @param sql
	 *            String
	 */
	public void tableRetriveD(TTable table, String sql) {
		TDS tds = new TDS();
		tds.setSQL(sql);
        tds.retrieve();
		INVPackOberver obser = new INVPackOberver();
		tds.addObserver(obser);
		table.setDataStore(tds);
		table.setDSValue();
	}

	/**
	 * 细表值改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableDChargeValue(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return false;
		// 拿到table上的parmmap的列名
		String columnName = tableD.getDataStoreColumnName(node.getColumn());
		// 赠量改变
		if ("RECOUNT_TIME".equals(columnName)) {
			// 拿到当前改变后的数据
			int value = TCM_Transform.getInt(node.getValue());
			if (value < 0) {
				messageBox("折损次数不可小于0!");
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 主表的点击事件
	 */
	public void onTableMClicked() {
		int row = tableM.getSelectedRow();
		// 数据上翻
		TParm tableCtzParm = tableM.getDataStore().getRowParm(row);
		setTextValue(tableCtzParm);
	}

	/**
	 * 数据上翻
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setTextValue(TParm parm) {
		setValueForParm("PACK_CODE;PACK_SEQ_NO", parm);
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		// 检核数据变更
		if (!checkValueChange())
			return;
		// 清空属性
		clearText();
		// 清空主表
		clearTable(tableM);
		// 清空明细表
		clearTable(tableD);
		// 可以保存
		this.callFunction("UI|save|setEnabled", true);
		// 不可以撤销
		this.callFunction("UI|cancel|setEnabled", false);
	}

	/**
	 * 清空属性
	 */
	public void clearText() {
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM");
	}

	/**
	 * 清空datastore
	 * 
	 * @param table
	 *            TTable
	 */
	public void clearTable(TTable table) {
		// 接收最后一次修改
		table.acceptText();
		// 清空选中
		table.clearSelection();
		// 删除所有行
		table.removeRowAll();
		// 清空修改记录
		table.resetModify();
	}

	/**
	 * 取消
	 * 
	 * @return boolean
	 */
	public boolean onCancel() {
		int rowCount = tableM.getRowCount();
		if (rowCount <= 0) {
			messageBox("没有要保存的数据!");
			return false;
		}
		// 取得保存数据
		TParm saveParm = getSaveParm(false);
		if (saveParm == null)
			return false;
		// 调用保存事务
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"savePackAndSterilization", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("保存失败");
			return false;
		}
		messageBox("保存成功");
		// 保存后刷新界面
		dealAaterSaveData();
		// 可以保存
		this.callFunction("UI|save|setEnabled", true);
		// 不可以撤销
		this.callFunction("UI|cancel|setEnabled", false);

		return true;
	}

	/**
	 * 保存入口
	 * 
	 * @return boolean
	 */
	public boolean onUpdate() {

		int rowCount = tableM.getRowCount();
		if (rowCount <= 0) {
			messageBox("没有要保存的数据!");
			return false;
		}
		// 取得保存数据
		TParm saveParm = getSaveParm(true);
		if (saveParm == null)
			return false;
		// 调用保存事务
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"savePackAndSterilization", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("保存失败!");
			return false;
		}
		messageBox("保存成功!");
		// 保存后刷新界面
		dealAaterSaveData();
		// 不可以保存
		// this.callFunction("UI|save|setEnabled", false);
		// 可以撤销
		this.callFunction("UI|cancel|setEnabled", true);
		return true;
	}

	/**
	 * 处理保存后的数据
	 */
	public void dealAaterSaveData() {
		tableM.setDSValue();
		tableM.resetModify();
		tableD.setDSValue();
		tableD.resetModify();
		// 得到回收和消毒的选择
		setCheckBoxValue();
	}

	/**
	 * 取得保存数据
	 * 
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getSaveParm(boolean action) {
		TParm saveParm = new TParm();
		// 灭菌
		String sterilization = this.getValueString("STERILIZATION");
		// 打包
		String pack = this.getValueString("UNPACK");
		if (sterilization.equals("N") && pack.equals("N")) {
			messageBox("无要保存的数据!");
			return null;
		}
		// 取系统时间
		Timestamp date = SystemTool.getInstance().getDate();
		// 0 在库，1 出库, 2 已回收 3 已清洗 4 已消毒 5 已灭菌 6 已打包 7:维修中
		if (sterilization.equals("Y")) {
			// 状态更改为已灭菌
			tableM.setItem(0, "STATUS", "5");
			tableM.setItem(0, "STERILIZATION_DATE", date);
			tableM.setItem(0, "STERILIZATION_VALID_DATE", date);
			tableM.setItem(0, "STERILIZATION_USER", Operator.getID());
		}
		if (pack.equals("Y")) {
			// 状态更改为已打包
			tableM.setItem(0, "STATUS", "6");
			tableM.setItem(0, "PACK_DATE", date);
		}

		// 得到灭菌的数据包
		TParm sterilizationParm = getSterilization(sterilization, pack, action);
		saveParm.setData("STERILIZATION", sterilizationParm.getData());
		// 取更新数据
		String[] saveSql = getsaveSql();
		saveParm.setData("SAVESQL", saveSql);
		return saveParm;
	}

	/**
	 * 获得消毒记录数据..同时给数据添加消毒和回收的数据
	 * 
	 * @param disinfection
	 *            String
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getSterilization(String sterilization, String pack,
			boolean action) {
		TParm sterilizationParm = new TParm();
		if (action) { // 处理保存
			sterilizationParm = dealSaveParm(sterilization, pack);
		} else { // 处理取消
			sterilizationParm = dealCancelParm();
		}
		return sterilizationParm;
	}

	/**
	 * 处理保存的数据
	 * 
	 * @param disinfection
	 *            String
	 * @return TParm
	 */
	public TParm dealSaveParm(String sterilization, String pack) {
		tableM.acceptText();
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		TParm sterilizationParm = tableM.getDataStore().getRowParm(0);
		sterilizationParm.setData("ORG_CODE", this.getValue("ORG_CODE"));// 得到供应室部门
		// 0 在库，1 出库, 2 已回收 3 已清洗 4 已消毒 5 已灭菌 6 已打包 7:维修中
		if (sterilization.equals("Y")) {
			// 灭菌日期
			sterilizationParm.setData("DISINFECTION_DATE",
					TCM_Transform.getString(date));
			// 设置效期 VALID_DATE
			Timestamp sterilization_valid_date = sterilizationParm
					.getTimestamp("STERILIZATION_VALID_DATE");
			sterilizationParm.setData("STERILIZATION_VALID_DATE",
					sterilization_valid_date);

			sterilizationParm.setData("STERILIZATION_USER", Operator.getID());// 灭菌人员
			sterilizationParm.setData("STERILIZATION_OPERATIONSTAFF",
					Operator.getID());// 灭菌操作人员
			// 数量
			sterilizationParm.setData("QTY", 1);
			sterilizationParm.setCount(1);
		}
		if (pack.equals("Y")) {
			sterilizationParm.setData("PACK_DATE", date);
			sterilizationParm.setCount(1);
		}
		// 操作人
		sterilizationParm.setData("OPT_USER", Operator.getID());
		// 时间
		sterilizationParm.setData("OPT_DATE", date);
		// 操作IP
		sterilizationParm.setData("OPT_TERM", Operator.getIP());

		sterilizationParm.setData("ACTIONMODE", "SAVE");
		return sterilizationParm;

	}

	/**
	 * 处理取消的保存parm
	 * 
	 * @return TParm
	 */
	public TParm dealCancelParm() {
		// 取系统事件
		TParm dispenfectionParm = new TParm();
		// 0 在库，1 出库, 2 已回收 3 已清洗 4 已消毒 5 已灭菌 6 已打包 7:维修中
		// 灭菌记录
		dispenfectionParm = tableM.getDataStore().getRowParm(0);
		String sterilizationDate = dispenfectionParm
				.getValue("STERILIZATION_DATE");
		sterilizationDate = sterilizationDate.substring(0, 4)
				+ sterilizationDate.substring(5, 7)
				+ sterilizationDate.substring(8, 10)
				+ sterilizationDate.substring(11, 13)
				+ sterilizationDate.substring(14, 16)
				+ sterilizationDate.substring(17, 19);
		// 消毒时间
		dispenfectionParm.setData("STERILIZATION_DATE", sterilizationDate);
		// 状态更改为已经消毒
		tableM.setItem(0, "STATUS", initStatus);
		// 灭菌日期
		tableM.setItem(0, "STERILIZATION_DATE", "");
		// 灭菌人员
		tableM.setItem(0, "STERILIZATION_USER", "");
		// 灭菌效期
		tableM.setItem(0, "STERILIZATION_VALID_DATE", "");
		// 打包日期
		tableM.setItem(0, "PACK_DATE", "");
		dispenfectionParm.setCount(1);
		dispenfectionParm.setData("ACTIONMODE", "CANCEL");
		return dispenfectionParm;

	}

	/**
	 * 得到要保存的数据
	 * 
	 * @return String[]
	 */
	public String[] getsaveSql() {
		// 取出主表sql
		String[] sqlM = tableM.getUpdateSQL();
		// 取出细表sql
		String[] sqlD = tableD.getUpdateSQL();
		// 把两组sql合并
		return StringTool.copyArray(sqlM, sqlD);
	}

	/**
	 * 检核数据变更提示信息（变更主细表时提示数据的变更）
	 * 
	 * @return boolean
	 */
	public boolean checkValueChange() {
		// 如果有数据变更
		if (checkData())
			switch (this.messageBox("提示信息", "数据变更是否保存",
					this.YES_NO_CANCEL_OPTION)) {
			// 保存
			case 0:
				if (!onUpdate())
					return false;
				return true;
				// 不保存
			case 1:
				return true;
				// 撤销
			case 2:
				return false;
			}
		return true;
	}

	/**
	 * 检核数据变更
	 * 
	 * @return boolean
	 */
	public boolean checkData() {
		// 主表
		if (tableM.isModified())
			return true;
		// 细表
		if (tableD.isModified())
			return true;
		return false;
	}

	/**
	 * 是否关闭窗口(当有数据变更时提示是否保存)
	 * 
	 * @return boolean true 关闭 false 不关闭
	 */
	public boolean onClosing() {
		// 如果有数据变更
		if (checkData())
			switch (this.messageBox("提示信息", "是否保存", this.YES_NO_CANCEL_OPTION)) {
			// 保存
			case 0:
				if (!onUpdate())
					return false;
				break;
			// 不保存
			case 1:
				return true;
				// 撤销
			case 2:
				return false;
			}
		// 没有变更的数据
		return true;
	}

	/**
	 * 打印条码
	 */
	public void onPrint() {
		TParm parm = new TParm();
		int row = tableM.getSelectedRow();
		if (row < 0)
			row = 0;
		String packCode = tableM.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("手术包选择错误!");
			return;
		}
		// 手术包序号:
		int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
		String packCodeSeq = "" + packSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;
		TParm orgParm = INVPublicTool.getInstance().getOrgDesc();

		String deptDesc = orgParm.getValue("DEPT_CHN_DESC", 0);

		// 调用条码
		TParm packCodeSeqParm = new TParm();
		TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
		String packDesc = packParm.getValue("PACK_DESC", row);
		String packDept = deptDesc;
		// 灭菌日期
		String orgCode=this.getValueString("ORG_CODE");
		String sql="SELECT STERILIZATION_DATE,STERILIZATION_VALID_DATE,STERILIZATION_USER,OPT_USER FROM INV_PACKSTOCKM " +
					"WHERE ORG_CODE='"+orgCode+"' AND PACK_CODE='"+packCode+"' AND PACK_SEQ_NO='"+packSeqNo+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		String sterilization_date = StringTool.getString(
				(Timestamp) result.getData("STERILIZATION_DATE", 0),
				"yyyy/MM/dd");
		String valueDate = StringTool.getString(
				(Timestamp) result.getData("STERILIZATION_VALID_DATE", 0), "yyyy/MM/dd");
		String optUser = result.getValue("OPT_USER", 0);
		String sterilization_user = result.getValue("STERILIZATION_USER", 0);

		packCodeSeqParm.addData("PACK_CODE_SEQ", packCode + packCodeSeq);
		packCodeSeqParm.addData("PACK_DESC", packDesc);
		packCodeSeqParm.addData("PACK_DEPT", "(" + packDept + ")");
		packCodeSeqParm.addData("DISINFECTION_DATE", sterilization_date);
		packCodeSeqParm.addData("VALUE_DATE", valueDate);
		packCodeSeqParm.addData("OPT_USER", optUser);
		packCodeSeqParm.addData("DISINFECTION_USER", sterilization_user);
		packCodeSeqParm.setCount(1);
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_CODE_SEQ");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_DEPT");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_DATE");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "VALUE_DATE");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "OPT_USER");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_USER");

		parm = new TParm();
		parm.setData("T1", packCodeSeqParm.getData());

		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackSeq.jhw", parm);
	}

}
