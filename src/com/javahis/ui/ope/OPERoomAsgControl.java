package com.javahis.ui.ope;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.ope.OPEOpBookTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.LEDOPEAsgUI;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 手术排程
 * </p>
 *
 * <p>
 * Description: 手术排程
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: Javahis
 * </p>
 *
 * @author zhangk 2009-9-27
 * @version 4.0
 */
public class OPERoomAsgControl extends TControl {

	TParm DATA;
	// ===========排序功能==================add by wanglong 20121212
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	/**
	 * 手术排程跑马灯
	 */
	private LEDOPEAsgUI ledUI;// wanglong add 20150113

	/**
	 * 
	 */
	public void onInit() {
		super.onInit();
		TableInit();
		// 获取当前时间
		String date = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd");
		this.setValue("OP_DATE_S", StringTool.getTimestamp(date + "000000", "yyyyMMddhhmmss"));
		this.setValue("OP_DATE_E", StringTool.getTimestamp(date + "235959", "yyyyMMddhhmmss"));
		this.clearValue("OP_DEPT_CODE");
		// 权限为妇科进入
		if (this.getPopedem("WOMEN")) {// wanglong add 20140929
			this.setValue("TYPE_CODE", "3");
			this.callFunction("UI|TYPE_CODE|setEnabled", false);
			this.onSelectOpType();
		}
		// 权限为儿科进入
		else if (this.getPopedem("CHILDREN")) {
			this.setValue("TYPE_CODE", "4");
			this.callFunction("UI|TYPE_CODE|setEnabled", false);
			this.onSelectOpType();
		}
		/**
		 * 显示跑马灯
		 */
		openLEDUI();// wanglong add 20150113
	}

	/**
	 * 表格初始化
	 */
	private void TableInit() {
		TTable table = (TTable) this.getComponent("Table");
		addSortListener(table);// ===表格加排序监听=====add by wanglong 20121212
		OrderList orderList = new OrderList();
		table.addItem("OrderList", orderList);
		OpList opList = new OpList();
		table.addItem("OpList", opList);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if (this.getValueString("ADM_TYPE").length() > 0) {
			parm.setData("ADM_TYPE", this.getValue("ADM_TYPE"));
		}
		if (this.getValueString("OP_DATE_S").length() > 0 && this.getValueString("OP_DATE_E").length() > 0) {
			parm.setData("OP_DATE_S", StringTool.getString((Timestamp) this.getValue("OP_DATE_S"), "yyyyMMddHHmmss"));
			parm.setData("OP_DATE_E", StringTool.getString((Timestamp) this.getValue("OP_DATE_E"), "yyyyMMddHHmmss"));
		}
		if (this.getValueString("TYPE_CODE").length() > 0) {
			parm.setData("TYPE_CODE", this.getValue("TYPE_CODE"));
		}
		if (this.getValueString("URGBLADE_FLG2").equals("Y")) {
			parm.setData("URGBLADE_FLG", "Y");
		}
		if (this.getValueString("URGBLADE_FLG3").equals("Y")) {
			parm.setData("URGBLADE_FLG", "N");
		}
		if (this.getValueString("STATE2").equals("Y")) {// 申请 add by huangjw 20140903
			parm.setData("STATE", "0");
		}
		if (this.getValueString("STATE3").equals("Y")) {// 已排程 add by huangjw 20140903
			parm.setData("STATE", "1");
		}
		if (this.getValueString("STATE4").equals("Y")) {// 手术完成 add by huangjw 20140903
			parm.setData("STATE", "2");
		}
		if (this.getValueString("OP_DEPT_CODE").length() > 0) {
			parm.setData("OP_DEPT_CODE", this.getValue("OP_DEPT_CODE"));
		}
		if (this.getValueString("OP_STATION_CODE").length() > 0) {
			parm.setData("OP_STATION_CODE", this.getValue("OP_STATION_CODE"));
		}
		if (this.getValueString("BOOK_DR_CODE").length() > 0) {
			parm.setData("BOOK_DR_CODE", this.getValue("BOOK_DR_CODE"));
		}
		if (this.getValueString("ROOM_NO").length() > 0) {
			parm.setData("ROOM_NO", this.getValue("ROOM_NO"));
		}
		parm.setData("CANCEL_FLG", "N");// 查询没有被取消的预约
		if (this.getValueString("STERILE_FLG").equals("Y")) {// 无菌 add by huangjw 20141017
			parm.setData("STERILE_FLG", "Y");
		}
		if (this.getValueString("MIRROR_FLG").equals("Y")) {// 腔镜 add by huangjw 20141017
			parm.setData("MIRROR_FLG", "Y");
		}

		// add by yangjj 20150528
		if (this.getValueString("ISO_FLG").equals("Y")) {
			parm.setData("ISO_FLG", "Y");
		}

		// ===============pangben modify 20110630 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// =============pangben modify 20110630 stop
		TTable table = (TTable) this.getComponent("Table");
		DATA = OPEOpBookTool.getInstance().selectOpBook(parm);
		if (DATA.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		} else {
			for (int i = 0; i < DATA.getCount(); i++) {
				TParm p = DATA.getRow(i);
				if (p.getValue("ADM_TYPE").equals("E") && p.getValue("STATE").equals("1")) {
					// 红色
					table.setRowTextColor(i, new Color(255, 0, 0));
				} else {
					table.setRowTextColor(i, new Color(0, 0, 0));
				}
				//
				DATA.setData("OP_CODE1", i, DATA.getValue("OP_CODE1", i) + ";" + DATA.getValue("OP_CODE2", i));
			}
		}
		// System.out.println("--------DATA--------");
		// System.out.println(DATA);
		table.setParmValue(DATA);
	}

	/**
	 * 手术排程
	 */
	public void onAsg() {
		TTable table = (TTable) this.getComponent("Table");
		int index = table.getSelectedRow();// 选中行
		if (index < 0) {
			return;
		}
		TParm data = table.getParmValue();
		String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ", index);
		TParm parm = new TParm();// wanglong add 20150113
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		parm.addListener("removeMessage", this, "removeLEDUIMessage");
		this.openDialog("%ROOT%/config/ope/OPEPersonnel.x", parm);
		// onQuery();
	}

	/**
	 * 手术信息
	 */
	public void onOpInfo() {
		TTable table = (TTable) this.getComponent("Table");
		int index = table.getSelectedRow();// 选中行
		DATA = table.getParmValue();// add by wanglong 20121212
		String OPBOOK_SEQ = DATA.getValue("OPBOOK_SEQ", index);
		TParm parm = new TParm();
		parm.setData("FLG", "update");
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		parm.setData("ADM_TYPE", DATA.getValue("ADM_TYPE", index));
		this.openDialog("%ROOT%/config/ope/OPEOpBook.x", parm);
	}

	/**
	 * 手术记录
	 */
	public void onOpRecord() {
		TTable table = (TTable) this.getComponent("Table");
		int index = table.getSelectedRow();// 选中行
		if (index < 0) {
			return;
		}
		TParm parm = new TParm();
		DATA = table.getParmValue();// add by wanglong 20121212
		String OPBOOK_SEQ = DATA.getValue("OPBOOK_SEQ", index);
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		parm.setData("MR_NO", DATA.getValue("MR_NO", index));
		parm.setData("ADM_TYPE", DATA.getValue("ADM_TYPE", index));
		this.openDialog("%ROOT%/config/ope/OPEOpDetail.x", parm);
	}

	/**
	 * 诊断CODE替换中文 模糊查询（内部类）
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * 手术CODE替换中文 模糊查询（内部类）
	 */
	public class OpList extends TLabel {
		TDataStore dataStore = new TDataStore();

		public OpList() {
			dataStore.setSQL("select * from SYS_OPERATIONICD");
			dataStore.retrieve();
		}

		/**
		 * @author qing.wang 20180905 手术排程及清单要求显示所申请全部手术信息
		 * @param s
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("OPERATION_ICD");
			Vector d = (Vector) parm.getData("OPT_CHN_DESC");
			//
			Map opMap = new HashMap();
			for (int i = 0; i < v.size(); i++) {
				opMap.put(v.get(i), d.get(i));
			}
			String[] arr = s.split(";");
			String result = "";
			for (int i = 0; i < arr.length; i++) {
				if (result.length() == 0) {
					result += opMap.get(arr[i]);
				} else {
					result += ";" + opMap.get(arr[i]);
				}
			}
			return result;
		}
	}

	/**
	 * “手术类型”与“手术间”联动
	 */
	public void onSelectOpType() {// wanglong add 20140929
		String typeCode = this.getValueString("TYPE_CODE");
		TComboBox roomNo = (TComboBox) this.getComponent("ROOM_NO");
		String sql = "SELECT B.ID,B.CHN_DESC AS NAME FROM OPE_IPROOM A,SYS_DICTIONARY B "
				+ " WHERE B.GROUP_ID='OPE_OPROOM' AND A.ROOM_NO=B.ID # ORDER BY B.SEQ,B.ID";
		if (!StringUtil.isNullString(typeCode)) {
			sql = sql.replaceFirst("#", " AND A.TYPE_CODE = '" + typeCode + "' ");
			this.setValue("ROOM_NO", "");
		} else {
			sql = "SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='OPE_OPROOM' ORDER BY SEQ,ID";
		}
		TParm roomParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (roomParm.getErrCode() < 0) {
			this.messageBox_("取得术间信息失败");
			return;
		}
		roomNo.setParmValue(roomParm);
		roomNo.setParmMap("id:ID;name:NAME");
	}

	/**
	 * 跑马灯显示
	 */
	public void openLEDUI() {// wanglong add 20150113
		Component com = (Component) getComponent();
		while ((com != null) && (!(com instanceof Frame))) {
			com = com.getParent();
		}
		TParm parm = new TParm();
		parm.setData("userID", "ASG");
		parm.setData("password", "OPE");
		// parm.addListener("getLEDListener", this, "getLEDListener");
		ledUI = new LEDOPEAsgUI((Frame) com, this, parm);
		ledUI.openWindow();
	}

	/**
	 * 窗口关闭
	 */
	public boolean onClosing() {// wanglong add 20150113
		if (null != ledUI) {
			ledUI.close();
		}
		return true;
	}

	/**
	 * 跑马灯双击操作
	 * 
	 * @param parm
	 */
	public void onDoubleClickLEDUI(TParm parm) {// wanglong add 20150113
		TTable table = (TTable) this.getComponent("Table");
		TParm DATA = queryBookInfo(parm);
		if (DATA.getErrCode() < 0) {
			this.messageBox("执行失败 " + DATA.getErrText());
			return;
		} else {
			for (int i = 0; i < DATA.getCount(); i++) {
				TParm p = DATA.getRow(i);
				if (p.getValue("ADM_TYPE").equals("E") && p.getValue("STATE").equals("1")) {
					// 红色
					table.setRowTextColor(i, new Color(255, 0, 0));
				} else {
					table.setRowTextColor(i, new Color(0, 0, 0));
				}
			}
		}
		table.setParmValue(DATA);
		removeLEDUIMessage("");
	}

	/**
	 * 查询手术申请信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryBookInfo(TParm parm) {// wanglong add 20150113
		if (parm.getCount("MR_NO") < 1) {
			return TParm.newErrParm(-1, "入参错误");
		}
		String sql = "SELECT A.OPBOOK_SEQ, A.ADM_TYPE, A.MR_NO, A.IPD_NO, A.CASE_NO, A.BED_NO, A.URGBLADE_FLG, A.OP_DATE, "
				+ "       A.TF_FLG, A.TIME_NEED, A.ROOM_NO, A.TYPE_CODE, A.ANA_CODE, A.OP_DEPT_CODE, A.OP_STATION_CODE, "
				+ "       A.DIAG_CODE1, A.DIAG_CODE2, A.DIAG_CODE3, A.BOOK_DEPT_CODE, A.OP_CODE1, A.OP_CODE2, "
				+ "       A.BOOK_DR_CODE, A.MAIN_SURGEON, A.BOOK_AST_1, A.BOOK_AST_2, A.BOOK_AST_3, A.BOOK_AST_4, "
				+ "       A.CIRCULE_USER1, A.CIRCULE_USER2, A.CIRCULE_USER3, A.CIRCULE_USER4, A.SCRUB_USER1, "
				+ "       A.SCRUB_USER2, A.SCRUB_USER3, A.SCRUB_USER4, A.ANA_USER1, A.ANA_USER2, A.EXTRA_USER1, "
				+ "       A.EXTRA_USER2, A.PRE_NO, A.REMARK, A.STATE, A.APROVE_DATE, A.APROVE_USER, A.OPT_USER, "
				+ "       A.OPT_DATE, A.OPT_TERM, B.PAT_NAME, A.CANCEL_FLG, A.PART_CODE, A.ISO_FLG, A.OPERATION_REMARK "
				+ "  FROM OPE_OPBOOK A, SYS_PATINFO B, OPE_OPDETAIL C " + " WHERE A.MR_NO = B.MR_NO  #  "
				+ "   AND A.OPBOOK_SEQ = C.OPBOOK_NO(+) " + "ORDER BY A.OP_DATE DESC, A.URGBLADE_FLG DESC";
		String opBookSeqList = "";
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			opBookSeqList += "'" + parm.getValue("OPBOOK_SEQ", i) + "',";
		}
		opBookSeqList = opBookSeqList.substring(0, opBookSeqList.length() - 1);
		if (opBookSeqList.length() > 0) {
			sql = sql.replaceFirst("#", " AND A.OPBOOK_SEQ IN (#) ".replaceFirst("#", opBookSeqList));
		} else {
			return TParm.newErrParm(-1, "入参错误");
		}
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 移除跑马灯数据
	 */
	public void removeLEDUIMessage(String opBookSeq) {// wanglong add 20150113
		if (ledUI != null) {
			TParm action = ledUI.getMessage();
			TParm result = queryBookInfo(action);
			if (result.getErrCode() < 0) {
				System.out.println("移除跑马灯数据失败 " + result.getErrText());
				return;
			}
			TParm parm = new TParm();
			for (int i = 0; i < result.getCount(); i++) {
				if (!result.getValue("STATE", i).equals("")) {
					parm.addData("OPBOOK_SEQ", result.getValue("OPBOOK_SEQ", i));
				}
			}
			ledUI.removeMessage(parm);
		}
	}

	// ====================排序功能begin======================add by wanglong 20121212
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 返回指定列在列名数组中的index
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * 
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================排序功能end======================
}
