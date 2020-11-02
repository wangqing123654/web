package com.javahis.ui.ope;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import jdo.bil.BILComparator;
import jdo.ope.OPEPersonnelQueryTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

import com.javahis.ui.ope.OPERoomAsgControl.OpList;
import com.javahis.ui.ope.OPERoomAsgControl.OrderList;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.ExportExcelUtil2;
import com.javahis.util.StringUtil;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: 手术排程查询
 * </p>
 *
 * <p>
 * Description: 手术排程查询
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
 * @author zhangk 2009-12-09
 * @version 4.0
 */
public class OPEPersonnelQueryControl extends TControl {
	private TParm data;// 记录查询结果
	private TTable TABLE;
	TDataStore dataStore_Dept = new TDataStore();// 部门
	TDataStore dataStore_User = new TDataStore();// 人员
	TDataStore DICTIONARY = new TDataStore();// 大字典
	private String BOOK_DEPT = "";// 预约科室

	// ===========排序功能==================add by wanglong 20121212
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		TABLE = (TTable) this.getComponent("Table");
		TableInit();
		addSortListener(TABLE);// ===表格加排序监听=====add by wanglong 20121212
		dataStore_Dept.setSQL("SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT");
		dataStore_Dept.retrieve();
		dataStore_User.setSQL("SELECT USER_NAME,USER_ID FROM SYS_OPERATOR");
		dataStore_User.retrieve();
		DICTIONARY = TIOM_Database.getLocalTable("SYS_DICTIONARY");
		// 获取当前时间
		this.setValue("DATE_S", SystemTool.getInstance().getDate());
		this.setValue("DATE_E", SystemTool.getInstance().getDate());
		callFunction("UI|Table|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onClickBox");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		// 时间段
		if (this.getValue("DATE_S") != null && this.getValue("DATE_E") != null) {
			parm.setData("DATE_S", this.getText("DATE_S").replace("/", ""));
			parm.setData("DATE_E", this.getText("DATE_E").replace("/", "") + "235959");
		}
		// 病案号
		if (this.getValueString("MR_NO").length() > 0) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// 手术类型
		if (this.getValueString("TYPE_CODE").length() > 0) {
			parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
		}
		// 门急住别
		if (this.getValueString("ADM_TYPE").length() > 0) {
			parm.setData("ADM_TYPE", this.getValueString("ADM_TYPE"));
		}
		// 就诊科室
		if (this.getValueString("REALDEPT_CODE").length() > 0) {
			parm.setData("REALDEPT_CODE", this.getValueString("REALDEPT_CODE"));
		}
		if (this.getValueString("REALDR_CODE").length() > 0) {
			parm.setData("REALDR_CODE", this.getValueString("REALDR_CODE"));
		}
		// 预约 科室
		if (this.getValueString("BOOK_DEPT_CODE").length() > 0) {
			parm.setData("BOOK_DEPT_CODE", this.getValueString("BOOK_DEPT_CODE"));
			BOOK_DEPT = this.getText("BOOK_DEPT_CODE");
		} else {
			BOOK_DEPT = "全院";
		}
		// 手术安排状态
		if ("Y".equals(this.getValueString("STATE2"))) {
			parm.setData("STATE", "0");// 未排程0
		} /*
			 * else if ("Y".equals(this.getValueString("STATE3"))) {
			 * parm.setData("STATE_BEYOND", "0");//已排程1,2手术已完成wanglong modify 20140422 }
			 */

		if ("Y".equals(this.getValueString("STATE3"))) {
			// parm.setData("STATE", "1");//已排成1 modify by huangjw 20140903
			parm.setData("REV_STATE", "0");
		}
		if ("Y".equals(this.getValueString("STATE4"))) {
			parm.setData("STATE", "2");// 手术完成2 modify by huangjw 20140903
		}
		if ("Y".equals(this.getValueString("STERILE_FLG"))) {// 无菌 add by huangjw 20141020
			parm.setData("STERILE_FLG", "Y");
		}
		if ("Y".equals(this.getValueString("MIRROR_FLG"))) {// 腔镜 add by huangjw 20141020
			parm.setData("MIRROR_FLG", "Y");
		}
		if ("Y".equals(this.getValueString("ISO_FLG"))) {// 传染 add by huangjw 20141020
			parm.setData("ISO_FLG", "Y");
		}
		// ===============pangben modify 20110630 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// =============pangben modify 20110630 stop
		data = OPEPersonnelQueryTool.getInstance().selectData(parm);
		if (data.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (data.getCount() <= 0) {
			this.messageBox("E0008");// 查无资料
			TABLE.removeRowAll();
			return;
		}
		this.setValue("selectALL", false);
		for (int i = 0; i < data.getCount(); i++) {
			if ("1".equals(data.getValue("STATE", i)) || "2".equals(data.getValue("STATE", i))) {
				data.setData("STATE", i, "Y");
			} else {
				data.setData("STATE", i, "N");
			}
			data.setData("SEQ", i, i + 1);// 增加序号，add by huangjw 20140904
			// 界面显示年龄 一助 二助 三助 四助 麻醉者 麻醉 巡回护士 器械护士 体外add by sunqy 20140722 ----start----
			data.addData("AGE",
					StringUtil.showAge(data.getTimestamp("BIRTH_DATE", i), data.getTimestamp("OP_DATE", i)));// 年龄
			data.addData("AST_1", this.getUserName(data.getValue("BOOK_AST_1", i)));
			data.addData("AST_2", this.getUserName(data.getValue("BOOK_AST_2", i)));
			data.addData("AST_3", this.getUserName(data.getValue("BOOK_AST_3", i)));
			data.addData("AST_4", this.getUserName(data.getValue("BOOK_AST_4", i)));
			data.addData("ANA_USER", this.getUserName(data.getValue("ANA_USER1", i)) + " "
					+ this.getUserName(data.getValue("ANA_USER2", i)));
			data.addData("ANA_CODE1", getDICTIONARY("OPE_ANAMETHOD", data.getValue("ANA_CODE", i)));

			// modify by yangjj 20150527 巡回护士取第一与第二个护士
			data.addData("CIRCULE_USER", this.getUserName(data.getValue("CIRCULE_USER1", i)) + " "
					+ this.getUserName(data.getValue("CIRCULE_USER2", i)));

			data.addData("SCRUB_USER", this.getUserName(data.getValue("SCRUB_USER1", i)));
			data.addData("EXTRA_USER", this.getUserName(data.getValue("EXTRA_USER1", i)));
			// 界面显示年龄 一助 二助 三助 四助 麻醉者 麻醉 巡回护士 器械护士 体外add by sunqy 20140722 ----end----
			// System.out.println("----OPBOOK_SEQ----"+data.getValue("OPBOOK_SEQ",i));
			//
			data.setData("OPT_CHN_DESC", i, data.getValue("OP_CODE1", i) + ";" + data.getValue("OP_CODE2", i));

		}
		TABLE.setParmValue(data);
	}

	/**
	 * 显示手术详细信息
	 */
	public void onInfo() {
		int row = TABLE.getSelectedRow();
		if (row < 0) {
			this.messageBox_("请选择一条信息！");
			return;
		}
		data = TABLE.getParmValue();// add by wanglong 20121212
		String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ", row);
		TParm parm = new TParm();
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		parm.setData("MR_NO", data.getValue("MR_NO", row));
		parm.setData("ADM_TYPE", data.getValue("ADM_TYPE", row));// add by wanglong 20121219
		parm.setData("EDITABLE", "FALSE");// add by wanglong 20121219
		this.openDialog("%ROOT%/config/ope/OPEOpDetail.x", parm);// modify by wanglong 20121219
	}

	/**
	 * 手术记录 ============pangben modify 20110701
	 */
	public void onOpRecord() {
		TTable table = (TTable) this.getComponent("Table");
		int index = table.getSelectedRow();// 选中行
		if (index < 0) {
			return;
		}
		TParm parm = new TParm();
		data = table.getParmValue();// add by wanglong 20121212
		String OPBOOK_SEQ = data.getValue("OPBOOK_SEQ", index);
		parm.setData("OPBOOK_SEQ", OPBOOK_SEQ);
		parm.setData("MR_NO", data.getValue("MR_NO", index));
		parm.setData("ADM_TYPE", data.getValue("ADM_TYPE", index));
		this.openDialog("%ROOT%/config/ope/OPEOpDetail.x", parm);
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		TABLE.acceptText();
		Map rows = new HashMap();
		int key = 0;
		for (int i = 0; i < TABLE.getRowCount(); i++) {
			if (TABLE.getItemString(i, 0).equals("Y")) {
				rows.put(key, i);
				key++;
			}
		}

		if (rows.size() < 1) {// add by wanglong 20140415
			this.messageBox("请勾选一行");
			return;
		}
		TParm data_1 = TABLE.getShowParmValue();
		TParm T1 = new TParm();// 报表表格数据

		for (int i = 0, j = 1; i < rows.size(); i++, j++) {// modify by guoy 20150714 增加j变量控制打印条数
			int row = Integer.valueOf(rows.get(i).toString());

			// add by guoy 20150713 增加申请科室
			String getDept_chn_descSql = " select " + " A.DEPT_CHN_DESC " + " from " + " SYS_DEPT A, "
					+ " OPE_OPBOOK B " + " WHERE " + " A.DEPT_CODE = B.BOOK_DEPT_CODE " + " AND B.OPBOOK_SEQ = '"
					+ data.getValue("OPBOOK_SEQ", row) + "' ";
			TParm dept_chn_descParm = new TParm(TJDODBTool.getInstance().select(getDept_chn_descSql));
			T1.addData("DEPT_CHN_DESC", dept_chn_descParm.getValue("DEPT_CHN_DESC", 0));

			T1.addData("OP_ROOM", data.getValue("OP_ROOM", row));// 手术室
			if (data.getBoolean("URGBLADE_FLG", row)) {// 急做
				T1.addData("URGBLADE_FLG", "√");
			} else {
				T1.addData("URGBLADE_FLG", "");
			}

			T1.addData("OP_DATE", StringTool.getString(data.getTimestamp("OP_DATE", row), "HH:mm"));// 手术时间 wanglong
																									// modify 20150226
			T1.addData("PAT_NAME", data.getValue("PAT_NAME", row));// 姓名
			T1.addData("DEPT_CHN_DESC", data.getValue("DEPT_CHN_DESC", row));// 就诊科室
			// 如果是住院手术 那么显示病区 否则显示诊区
			if ("I".equals(data.getValue("ADM_TYPE", row)))// 病区
				T1.addData("STATION_DESC", data.getValue("STATION_DESC", row));
			else
				T1.addData("STATION_DESC", data.getValue("CLINIC_DESC", row));
			T1.addData("BED_NO_DESC", data.getValue("BED_NO_DESC", row));// 床号
			T1.addData("SEX", data.getValue("SEX", row));// 性别
			T1.addData("AGE",
					StringUtil.showAge(data.getTimestamp("BIRTH_DATE", row), data.getTimestamp("OP_DATE", row)));// 年龄
			T1.addData("HEIGHT", data.getValue("HEIGHT", row));// 身高
			T1.addData("WEIGHT", data.getValue("WEIGHT", row));// 体重
			T1.addData("MR_NO", data.getValue("MR_NO", row));// 病案号

			// add by yangjj 20150528 增加房号
			String getRoomSql = " SELECT " + " A.ROOM_DESC " + " FROM " + " SYS_ROOM A, " + " SYS_BED B " + " WHERE "
					+ " A.ROOM_CODE = B.ROOM_CODE " + " AND B.BED_NO = '" + data.getValue("BED_NO", row) + "' ";
			TParm roomParm = new TParm(TJDODBTool.getInstance().select(getRoomSql));
			T1.addData("ROOM_DESC", roomParm.getValue("ROOM_DESC", 0));

			T1.addData("ICD_CHN_DESC", data.getValue("ICD_CHN_DESC", row));// 术前诊断
			// T1.addData("OPT_CHN_DESC", data.getValue("OPT_CHN_DESC", row));// 拟行手术
			T1.addData("OPT_CHN_DESC", data_1.getValue("OPT_CHN_DESC", row));

			T1.addData("ANA_CODE", getDICTIONARY("OPE_ANAMETHOD", data.getValue("ANA_CODE", row)));// 麻醉 20150113
																									// wangjingchun
																									// modify
			T1.addData("MAIN_SURGEON", this.getUserName(data.getValue("MAIN_SURGEON", row)));// 术者
			T1.addData("BOOK_AST_1", this.getUserName(data.getValue("BOOK_AST_1", row)));// 一助
			T1.addData("BOOK_AST_2", this.getUserName(data.getValue("BOOK_AST_2", row)));// 二助
			// T1.addData("BOOK_AST_3",this.getUserName(data.getValue("BOOK_AST_3",row)));//三助
			// 20150113 wangjingchun modify
			// T1.addData("BOOK_AST_4",this.getUserName(data.getValue("BOOK_AST_4",row)));//四助
			// 20150113 wangjingchun modify
			// T1.addData("ANA_USER",this.spellName(this.getUserName(data.getValue("ANA_USER1",row)),
			// this.getUserName(data.getValue("ANA_USER2",row))));//麻醉者 20150113
			// wangjingchun add
			T1.addData("ANA_USER", this.getUserName(data.getValue("ANA_USER1", row)) + "   "
					+ this.getUserName(data.getValue("ANA_USER2", row)));// 麻醉者
			// T1.addData("ANA_USER1",this.getUserName(data.getValue("ANA_USER1",row)));//麻醉者1
			// 20150113 wangjingchun modify
			// T1.addData("ANA_USER1",this.getUserName(data.getValue("ANA_USER2",row)));//麻醉者2
			// 20150113 wangjingchun add

			// modify by yangjj 20150527
			T1.addData("CIRCULE_USER1", this.getUserName(data.getValue("CIRCULE_USER1", row)) + "   "
					+ this.getUserName(data.getValue("CIRCULE_USER2", row)));// 巡回护士

			T1.addData("SCRUB_USER1", this.getUserName(data.getValue("SCRUB_USER1", row)));// 器械护士
			T1.addData("EXTRA_USER1", this.getUserName(data.getValue("EXTRA_USER1", row)));// 体外
			if (data.getValue("STERILE_FLG", row).equals("Y")) {// 有菌 20150113 wangjingchun add
				T1.addData("STERILE_FLG", "√");
			} else {
				T1.addData("STERILE_FLG", "");
			}
			// T1.addData("STERILE_FLG",data.getValue("STERILE_FLG",row));//有菌 20150113
			// wangjingchun add
			if (data.getValue("MIRROR_FLG", row).equals("Y")) {// 腔镜 20150113 wangjingchun add
				T1.addData("MIRROR_FLG", "√");
			} else {
				T1.addData("MIRROR_FLG", "");
			}
			// T1.addData("MIRROR_FLG",data.getValue("MIRROR_FLG",row));//腔镜 20150113
			// wangjingchun add

			// add by yangjj 20150515
			if (data.getBoolean("ISO_FLG", row)) {// 隔离手术
				T1.addData("ISO_FLG", "√");
			} else {
				T1.addData("ISO_FLG", "");
			}

			T1.addData("REMARK", data.getValue("REMARK", row));// 备注

			T1.setCount(j);// modify by guoy 20150714 设置报表的条数

			T1.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");// 申请科室 add by guoy 20150713

			T1.addData("SYSTEM", "COLUMNS", "OP_ROOM");// 手术室
			T1.addData("SYSTEM", "COLUMNS", "URGBLADE_FLG");// 急做
			T1.addData("SYSTEM", "COLUMNS", "OP_DATE");// 手术时间
			T1.addData("SYSTEM", "COLUMNS", "PAT_NAME");// 姓名
			// T1.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");//就诊科室 wanglong delete
			// 20150226
			T1.addData("SYSTEM", "COLUMNS", "STATION_DESC");// 病区

			// add by yangjj 20150528
			T1.addData("SYSTEM", "COLUMNS", "ROOM_DESC");// 病房号

			// T1.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");//床号 wanglong delete 20150226
			T1.addData("SYSTEM", "COLUMNS", "SEX");// 性别
			T1.addData("SYSTEM", "COLUMNS", "AGE");// 年龄
			// T1.addData("SYSTEM", "COLUMNS", "HEIGHT");//身高 wanglong delete 20150226
			T1.addData("SYSTEM", "COLUMNS", "WEIGHT");// 体重
			T1.addData("SYSTEM", "COLUMNS", "MR_NO");// 病案号
			T1.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");// 术前诊断
			T1.addData("SYSTEM", "COLUMNS", "OPT_CHN_DESC");// 拟行手术

			// modify by yangjj 20150528 去掉术后诊断和实施手术
			// T1.addData("SYSTEM", "COLUMNS", "ICD_DESC");//术后诊断 xiongwg modify 20150318
			// T1.addData("SYSTEM", "COLUMNS", "REAL_OPT_DESC");//实施手术 xiongwg modify
			// 20150318
			// T1.addData("SYSTEM", "COLUMNS", "REAL_OPT_DESC");//实施手术 wanglong add 20150226
			T1.addData("SYSTEM", "COLUMNS", "ANA_CODE");// 麻醉 20150113 wangjingchun modify
			T1.addData("SYSTEM", "COLUMNS", "MAIN_SURGEON");// 术者
			T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_1");// 一助
			T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_2");// 二助
			// T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_3");//三助 20150113 wangjingchun
			// modify
			// T1.addData("SYSTEM", "COLUMNS", "BOOK_AST_4");//四助 20150113 wangjingchun
			// modify
			T1.addData("SYSTEM", "COLUMNS", "ANA_USER");// 麻醉者 20150113 wangjingchun add
			// T1.addData("SYSTEM", "COLUMNS", "ANA_USER1");//麻醉者1 20150113 wangjingchun
			// modify
			// T1.addData("SYSTEM", "COLUMNS", "ANA_USER2");//麻醉者2 20150113 wangjingchun add

			T1.addData("SYSTEM", "COLUMNS", "SCRUB_USER1");// 器械护士
			T1.addData("SYSTEM", "COLUMNS", "CIRCULE_USER1");// 巡回护士
			// T1.addData("SYSTEM", "COLUMNS", "EXTRA_USER1");//体外
			T1.addData("SYSTEM", "COLUMNS", "STERILE_FLG");// 有菌 20150113 wangjingchun add
			T1.addData("SYSTEM", "COLUMNS", "MIRROR_FLG");// 腔镜 20150113 wangjingchun add
			// T1.addData("SYSTEM", "COLUMNS", "ICD_DESC");//术后诊断 wanglong add 20150226

			// add by yangjj 20150515
			T1.addData("SYSTEM", "COLUMNS", "ISO_FLG");// 隔离手术

			T1.addData("SYSTEM", "COLUMNS", "REMARK");// 备注

			// add by yangjj 20150514
			if ((i == rows.size() - 1) || (!(StringTool.getString(data.getTimestamp("OP_DATE", row), "yyyy/MM/dd")
					.equals(StringTool.getString(
							data.getTimestamp("OP_DATE", Integer.valueOf(rows.get(i + 1).toString())),
							"yyyy/MM/dd"))))) {

				// 报表总体数据
				TParm printData = new TParm();
				printData.setData("T1", T1.getData());
				printData.setData("title", "TEXT", "手术安排清单");
				printData.setData("BOOK_DEPT", "TEXT", BOOK_DEPT);

				// modify by yangjj 20150514
				printData.setData("OP_DATE", "TEXT",
						StringTool.getString(data.getTimestamp("OP_DATE", row), "yyyy年MM月dd日"));
				printData.setData("SYS_DATE", "TEXT",
						StringTool.getString(SystemTool.getInstance().getDate(), "yyyy年MM月dd日"));

				// printData.setData("OP_DATE","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
				this.openPrintWindow("%ROOT%\\config\\prt\\OPE\\OPEPersonnelPrint.jhw", printData);
				j = 0;// add by guoy 20150714 另起一页打印报表将j重置
				// add by yangjj 20150514
				T1 = new TParm();
			}
		}
	}

	/**
	 * 用于姓名竖排显示
	 * 
	 * @param n1
	 *            String 姓名1
	 * @param n2
	 *            String 姓名2 20150113 wangjingchun add
	 */
	public String spellName(String n1, String n2) {
		String[] n1c = n1.split("");
		String[] n2c = n2.split("");
		int l1 = n1c.length;
		int l2 = n2c.length;
		StringBuilder sb = new StringBuilder();
		if (l1 >= l2) {
			for (int i = 0; i < l1; i++) {
				if (i <= l2 - 1) {
					sb.append(n1c[i] + n2c[i]);
				} else {
					sb.append(n1c[i] + " ");
				}
			}
		} else {
			for (int i = 0; i < l2; i++) {
				if (i <= l1 - 1) {
					sb.append(n1c[i] + n2c[i]);
				} else {
					sb.append(" " + n2c[i]);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("DATE_S;DATE_E;MR_NO;TYPE_CODE;ADM_TYPE;REALDEPT_CODE;REALDR_CODE;BOOK_DEPT_CODE");
		this.setValue("STATE1", true);
		TABLE.removeRowAll();
	}

	/**
	 * 替换科室中午
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String s) {
		if (dataStore_Dept == null)
			return s;
		String bufferString = dataStore_Dept.isFilter() ? dataStore_Dept.FILTER : dataStore_Dept.PRIMARY;
		TParm parm = dataStore_Dept.getBuffer(bufferString);
		Vector v = (Vector) parm.getData("DEPT_CODE");
		Vector d = (Vector) parm.getData("DEPT_CHN_DESC");
		int count = v.size();
		for (int i = 0; i < count; i++) {
			if (s.equals(v.get(i)))
				return "" + d.get(i);
		}
		return s;
	}

	/**
	 * 替换人员姓名
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public String getUserName(String s) {
		if (dataStore_User == null)
			return s;
		String bufferString = dataStore_User.isFilter() ? dataStore_Dept.FILTER : dataStore_Dept.PRIMARY;
		TParm parm = dataStore_User.getBuffer(bufferString);
		Vector v = (Vector) parm.getData("USER_ID");
		Vector d = (Vector) parm.getData("USER_NAME");
		int count = v.size();
		for (int i = 0; i < count; i++) {
			if (s.equals(v.get(i)))
				return "" + d.get(i);
		}
		return s;
	}

	/**
	 * 替换大字典中的中文
	 * 
	 * @param group_ID
	 *            String
	 * @param ID
	 *            String
	 * @return String
	 */
	private String getDICTIONARY(String group_ID, String ID) {
		DICTIONARY.setFilter(" GROUP_ID='" + group_ID + "' AND ID='" + ID + "'");
		DICTIONARY.filter();
		return DICTIONARY.getItemString(0, "CHN_DESC");
	}

	/**
	 * 全选事件
	 */
	public void selectALL() {
		String flg = "N";
		if ("Y".equals(this.getValueString("selectALL"))) {
			flg = "Y";
		} else
			flg = "N";
		for (int i = 0; i < TABLE.getRowCount(); i++) {
			// 如果是全选状态 要判断 选中的信息是不是已经排程的信息
			if ("Y".equals(flg)) {
				if ("Y".equals(TABLE.getItemString(i, TABLE.getColumnIndex("STATE")))) {// 判断是否已排成--xiongwg20150327
					TABLE.setItem(i, 0, flg);
				}
			} else {
				TABLE.setItem(i, 0, flg);
			}
		}
	}

	/**
	 * table 中 checkbox 事件
	 * 
	 * @param object
	 *            Object
	 */
	public boolean onClickBox(Object object) {
		TTable obj = (TTable) object;
		obj.acceptText();
		int row = obj.getSelectedRow();
		if (obj.getItemString(row, "FLG").equals("Y")) {
			// 判断选中行信息是否已经完成了 排程
			if (obj.getItemString(row, 1).equals("N")) {
				this.messageBox_("此条手术申请还没有进行排程，不可打印");
				obj.setItem(row, "FLG", "N");
				return true;
			}
		}
		return false;
	}

	/**
	 * 导出excel
	 */
	public void onExport() {// add by wanglong 20130718
		if (TABLE.getRowCount() <= 0) {
			this.messageBox("没有数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(TABLE, "手术排程报表");
	}

	/**
	 * 病案号查询
	 */
	public void onMrNo() {// wanglong add 20140422
		String mrNo = this.getValueString("MR_NO");
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO", mrNo);
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

	/**
	 * JCI核查表单打印，导出EXCEL
	 * 
	 * @author wangqing 20171117
	 */
	public void onExport22() {
		if (TABLE == null) {
			this.messageBox("TABLE初始化失败");
			return;
		}
		if (TABLE.getRowCount() <= 0) {
			this.messageBox("没有数据");
			return;
		}
		// 组装一个TParm，用来存放TABLE2的数据
		TParm tblParm = TABLE.getShowParmValue();
		TTable TABLE2 = (TTable) this.getComponent("TABLE2");
		if (TABLE2 == null) {
			this.messageBox("TABLE2初始化失败");
			return;
		}
		TParm tblParm2 = new TParm();
		int count = 0;
		for (int i = 0; i < TABLE.getRowCount(); i++) {
			tblParm2.setData("ARG_0", i, tblParm.getValue("SEQ", i));// 序号
			tblParm2.setData("ARG_1", i, tblParm.getValue("OP_DATE", i));// 手术日期
			tblParm2.setData("ARG_2", i, tblParm.getValue("MR_NO", i));// 病案号
			tblParm2.setData("ARG_3", i, tblParm.getValue("PAT_NAME", i));// 患者姓名
			tblParm2.setData("ARG_4", i, "");
			tblParm2.setData("ARG_5", i, "");
			tblParm2.setData("ARG_6", i, "");
			tblParm2.setData("ARG_7", i, "");
			tblParm2.setData("ARG_8", i, "");
			tblParm2.setData("ARG_9", i, "");
			tblParm2.setData("ARG_10", i, "");
			tblParm2.setData("ARG_11", i, "");
			tblParm2.setData("ARG_12", i, "");
			tblParm2.setData("ARG_13", i, "");
			tblParm2.setData("ARG_14", i, "");
			tblParm2.setData("ARG_15", i, "");
			tblParm2.setData("ARG_16", i, "");
			tblParm2.setData("ARG_17", i, "");
			count++;
		}
		tblParm2.setCount(count);
		// System.out.println("@test by wangqing@---tblParm2="+tblParm2);
		TABLE2.setParmValue(tblParm2);
		ExportExcelUtil2.getInstance().exportExcel(TABLE2, "JCI核查表单");
	}

	/**
	 * JCI核查表单打印，导出报表
	 */
	public void onExport2() {
		if (TABLE == null) {
			this.messageBox("TABLE初始化失败");
			return;
		}
		if (TABLE.getRowCount() <= 0) {
			this.messageBox("没有数据");
			return;
		}
		TParm tblParm = TABLE.getShowParmValue();
		TParm data = new TParm();
		TParm body = new TParm();
		int count = 0;
		int seq = 1;
		// 只打印勾选行
		for (int i = 0; i < TABLE.getRowCount(); i++) {
			if (!tblParm.getBoolean("FLG", i)) {
				continue;
			}
			body.addData("SEQ", seq);
			body.addData("OP_DATE", tblParm.getValue("OP_DATE", i));
			body.addData("MR_NO", tblParm.getValue("MR_NO", i));
			body.addData("PAT_NAME", tblParm.getValue("PAT_NAME", i));
			seq++;
			count++;
		}
		if (count == 0) {
			this.messageBox("没有勾选行");
			return;
		}
		body.setCount(count);
		body.addData("SYSTEM", "COLUMNS", "SEQ");
		body.addData("SYSTEM", "COLUMNS", "OP_DATE");
		body.addData("SYSTEM", "COLUMNS", "MR_NO");
		body.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		data.setData("TABLE", body.getData());

		data.setData("PRINT_USER", Operator.getName());// 打印人
		data.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));// 打印日期
		data.setData("PRINT_YEAR", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy"));// 打印年
		// data.setData("PRINT_MONTH",
		// StringTool.getString(SystemTool.getInstance().getDate(), "MM"));// 打印月
		openPrintWindow("%ROOT%\\config\\prt\\OPE\\OPEPersonnelQuery.jhw", data, false);
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
	 * 表格初始化
	 */
	private void TableInit() {
		TTable table = (TTable) this.getComponent("Table");
		OpList opList = new OpList();
		table.addItem("OpList", opList);
	}

}
