package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.ui.opb.Objects;

/**
 *
 * <p>
 * Title: 医生站引用套餐
 * </p>
 *
 * <p>
 * Description: 医生站引用套餐
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author duzhw 20140227
 * @version 4.5
 */
public class MEMDoctorPackageControl extends TControl {

	// 套餐交易表;时程套餐表;套餐明细表
	private TTable tradeTable, sectionTable, detailTable;
	private TParm acceptData;
	Pat pat;

	List<String> list;

	/**
	 * 初始化
	 */
	public void onInit() { // 初始化程序
		super.onInit();
		initComponent();
		initData();

	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		tradeTable = getTable("TRADE_TABLE");
		sectionTable = getTable("SECTION_TABLE");
		detailTable = getTable("DETAIL_TABLE");
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 查询时间默认当天
		Timestamp now = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", now.toString().substring(0, 10).replace('-', '/'));
		this.setValue("END_DATE", now.toString().substring(0, 10).replace('-', '/'));
		// 病案号置有效
		callFunction("UI|MR_NO|setEnabled", true); // 病案号
		// 传进参数
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			String mrNo = acceptData.getData("MR_NO").toString();
			list = (List<String>) acceptData.getData("IDS");
			this.setValue("MR_NO", mrNo);
			this.onMrno();
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 数据检查--病案号置灰并且有数据
		if (!checkData()) {
			return;
		}
		String mrNo = this.getValue("MR_NO").toString();
		String startDate = this.getValueString("START_DATE");
		if (startDate.length() > 0) {
			startDate = startDate.toString().replaceAll("-", "/").substring(0, 10);
		}
		String endDate = this.getValueString("END_DATE");
		if (endDate.length() > 0) {
			endDate = endDate.toString().replaceAll("-", "/").substring(0, 10);
		}

		String sql = "SELECT ROWNUM AS ID, A.*, B.PACKAGE_CODE FROM MEM_PACKAGE_TRADE_M A,"
				+ " (SELECT TRADE_NO,PACKAGE_CODE FROM MEM_PAT_PACKAGE_SECTION GROUP BY TRADE_NO ,PACKAGE_CODE) B "
				+ " WHERE A.TRADE_NO=B.TRADE_NO AND A.MR_NO = '" + mrNo + "'";
		// ====start=====add by kangy 20160831=====住院医生站传入数据时====
		if (null != acceptData) {
			if (!"MZYSZ".equals(acceptData.getData("TYPE"))) {
				sql += " AND A.CASE_NO='" + acceptData.getData("CASE_NO") + "'";
			}
		}
		// ====end=====add by kangy 20160831
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		tradeTable.setParmValue(result);

	}

	/**
	 * 购买套餐表（交易表）单击事件
	 */
	public void onClickTradeTable() {
		int selectedIndx = tradeTable.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		// 清空明细表
		TParm parm = new TParm();
		detailTable.setParmValue(parm);

		TParm tradeTableParm = tradeTable.getParmValue();
		String tradeNO = tradeTableParm.getValue("TRADE_NO", selectedIndx);
		String packageCode = tradeTableParm.getValue("PACKAGE_CODE", selectedIndx);
		String mrNo = tradeTableParm.getValue("MR_NO", selectedIndx);
		// 页面交易信息赋值
		String sqlTrade = "SELECT * FROM MEM_PACKAGE_TRADE_M WHERE TRADE_NO = '" + tradeNO + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sqlTrade));
		if (result.getCount() > 0) {
			// 页面信息
			setDataForTrade(result);
		}
		// 查询时程套餐sql
		String sql = "SELECT 'N' AS EXEC,CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END MEM_USED_FLG,ROWNUM AS ID1,"
				+ " A.* FROM MEM_PAT_PACKAGE_SECTION A " + " WHERE  A.TRADE_NO = '" + tradeNO + "' AND A.MR_NO = '"
				+ mrNo + "' " + " AND A.PACKAGE_CODE = '" + packageCode + "' " + " AND A.REST_TRADE_NO IS NULL";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		sectionTable.setParmValue(result);

	}

	/**
	 * 套餐时程表单击事件
	 */
	public void onClickSectionTable() {
		int selectedIndx = sectionTable.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		TParm sectionTableParm = sectionTable.getParmValue();
		String tradeNO = sectionTableParm.getValue("TRADE_NO", selectedIndx);
		String packageCode = sectionTableParm.getValue("PACKAGE_CODE", selectedIndx);
		String sectionCode = sectionTableParm.getValue("SECTION_CODE", selectedIndx);
		String mrNo = sectionTableParm.getValue("MR_NO", selectedIndx);


		// 查询明细套餐sql
		String sql = "SELECT (CASE B.ACTIVE_FLG WHEN 'N' THEN 'Y' ELSE 'N' END ) AS ACTIVE_FLG2,'N' AS EXEC,CASE USED_FLG WHEN '0' THEN 'N' WHEN '1' THEN 'Y' END MEM_USED_FLG,ROWNUM AS ID1,B.ORDER_CAT1_CODE,B.CTRL_FLG,B.CAT1_TYPE,B.ORDERSET_FLG,"
				+ " A.* FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_FEE B " + " WHERE A.TRADE_NO = '" + tradeNO
				+ "' AND A.PACKAGE_CODE = '" + packageCode + "' " + " AND A.SECTION_CODE = '" + sectionCode
				+ "' AND A.MR_NO = '" + mrNo + "' AND A.HIDE_FLG = 'N'" + " AND A.ORDER_CODE = B.ORDER_CODE(+) ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));


		if (list != null) {
			for (int i = 0; i < result.getCount("EXEC"); i++) {
				for (int j = 0; j < list.size(); j++) {
					if (result.getValue("ID", i).equals(list.get(j))) {
						result.setData("MEM_USED_FLG", i, "Y");
						result.setData("USED_FLG", i, "Y");
					}
				}
			}
		}
		detailTable.setParmValue(result);
	}

	/**
	 * 清除
	 */
	public void onClear() {
		// 病案号置有效
		callFunction("UI|MR_NO|setEnabled", true); // 病案号
		this.clearValue("MR_NO;PAT_NAME;INTRODUCER1;INTRODUCER2;INTRODUCER3;DISCOUNT_REASON;"
				+ "DISCOUNT_APPROVER;DISCOUNT_TYPE;ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;DESCRIPTION");
		// 开始结束时间
		Timestamp now = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", now.toString().substring(0, 10).replace('-', '/'));
		this.setValue("END_DATE", now.toString().substring(0, 10).replace('-', '/'));
		// 清空表
		tradeTable.removeRowAll();
		sectionTable.removeRowAll();
		detailTable.removeRowAll();
	}

	/**
	 * 病案号回车查询事件
	 */
	public void onMrno() {

		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno))
			return;
		pat = pat.onQueryByMrNo(mrno);
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("查无病患! ");
			this.onClear(); // 清空
			return;
		} else {
			callFunction("UI|MR_NO|setEnabled", false); // 病案号
			// MR_NO = pat.getMrNo();
		}
		this.setPatForUI(pat);

	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		// this.setMenu(false); //MENU 显示控制
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			// this.setMenu(false); //MENU 显示控制
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			// ============xueyf modify 20120307 start
			messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
			// ============xueyf modify 20120307 stop
		}

		return true;
	}

	/**
	 * 病患信息赋值
	 *
	 * @param patInfo
	 *            Pat
	 */
	public void setPatForUI(Pat patInfo) {
		// 病案号,姓名
		this.setValueForParm("MR_NO;PAT_NAME", patInfo.getParm());
		// 调用查询方法
		this.onQuery();
	}

	/**
	 * 数据检查
	 */
	public boolean checkData() {
		TTextField mrNo = (TTextField) this.getComponent("MR_NO");
		boolean flag = mrNo.isEnabled();
		String mrNo2 = this.getValue("MR_NO").toString();
		if (mrNo2.length() == 0) {
			this.messageBox("病案号不能为空！");
			this.grabFocus("MR_NO");
			return false;
		}
		if (flag) {
			this.messageBox("输入病案号有误！");
			this.grabFocus("MR_NO");
			return false;
		}

		return true;
	}

	/**
	 * 勾选数据检查
	 */
	public boolean checkDetailData() {
		// System.out.println("123");
		// 检验是否选中数据
		TParm parm = detailTable.getParmValue();
		int count = parm.getCount();
		if (count <= 0) {
			this.messageBox("没有细项套餐数据，无法传回！");
			return false;
		} else {
			boolean flg = false;
			for (int i = 0; i < count; i++) {
				boolean exec = parm.getBoolean("EXEC", i);
				if (exec) {
					flg = true;
				}
			}
			if (!flg) {
				this.messageBox("没有选中细项套餐！");
				return false;
			}
		}
		return true;
	}

	/**
	 * 设置页面交易信息
	 */
	public void setDataForTrade(TParm parm) {
		this.setValue("INTRODUCER1", parm.getValue("INTRODUCER1", 0));
		this.setValue("INTRODUCER2", parm.getValue("INTRODUCER2", 0));
		this.setValue("INTRODUCER3", parm.getValue("INTRODUCER3", 0));
		this.setValue("DISCOUNT_REASON", parm.getValue("DISCOUNT_REASON", 0));
		this.setValue("DISCOUNT_APPROVER", parm.getValue("DISCOUNT_APPROVER", 0));
		this.setValue("DISCOUNT_TYPE", parm.getValue("DISCOUNT_TYPE", 0));
		this.setValue("ORIGINAL_PRICE", parm.getValue("ORIGINAL_PRICE", 0));
		this.setValue("RETAIL_PRICE", parm.getValue("RETAIL_PRICE", 0));
		this.setValue("AR_AMT", parm.getValue("AR_AMT", 0));
		this.setValue("DESCRIPTION", parm.getValue("DESCRIPTION", 0));
	}

	/**
	 * 细项表点击事件
	 */
	public void onMainTableClick() {
		int selectedIndx = detailTable.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		if (detailTable.getSelectedColumn() == 0) {// 第一列"选"
			detailTable.acceptText();
			int row = detailTable.getSelectedRow();
			TParm parm = detailTable.getParmValue();
			parm.setData("EXEC", row, parm.getValue("EXEC", row).equals("Y") ? "N" : "Y");
			detailTable.setParmValue(parm);
			detailTable.setSelectedRow(row);
			detailTable.acceptText();

		}
	}

	/**
	 * 得到页面中Table对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

	// /**
	// * 获取主细项sql
	// */
	// public String getSendBackSql(String tradeNo, String orderCode) {
	// String sql = "SELECT
	// B.ORDER_CAT1_CODE,B.CTRL_FLG,B.CAT1_TYPE,B.ORDERSET_FLG,A.* " +
	// " FROM MEM_PAT_PACKAGE_SECTION_D A,SYS_FEE B " +
	// " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.TRADE_NO = '"+tradeNo+"' AND
	// (A.ORDER_CODE = '"+orderCode+"' " +
	// " OR A.ORDERSET_CODE = '"+orderCode +"') AND B.ACTIVE_FLG = 'Y' ORDER BY
	// A.SEQ ";
	// System.out.println("sql="+sql);
	// return sql;
	// }

	public void onSendBack() {
		detailTable.acceptText();
		TParm parm = detailTable.getParmValue();

		int selectedRow = detailTable.getSelectedRow();
//		if (selectedRow >= 0) {//
			for (int i = 0; i < detailTable.getParmValue().getCount("ACTIVE_FLG2"); i++) {
				if (detailTable.getParmValue().getValue("EXEC", i).equals("Y")
						&& detailTable.getParmValue().getValue("ACTIVE_FLG2", i).equals("Y")) {
					detailTable.getParmValue().removeRow(i);
				}
			}
//		}
		TParm result = new TParm();
		TParm parmChn = new TParm();// CHN
		TParm parmOp = new TParm();// OP
		TParm parmCtrl = new TParm();// CTRL
		TParm parmOrder = new TParm();// ORDER
		TParm parmExa = new TParm();// EXA
		TParm tmp;
		for (int i = 0; i < parm.getCount("EXEC"); i++) {
			tmp = parm.getRow(i);
			if (tmp.getBoolean("EXEC")) {
				if (!("ZYYSZ".equals(acceptData.getData("TYPE")) || "ZYJJ".equals(acceptData.getData("TYPE")))) {// add
																													// by
																													// kangy
					// 入参为住院医生站不进行校验
					if (tmp.getBoolean("MEM_USED_FLG")) {
						messageBox("存在医嘱已使用");
						return;
					}
				}
				if (tmp.getBoolean("SETMAIN_FLG")) {
					tmp.setData("RETAIL_PRICE", 0);
				}
				String cat1Code = tmp.getValue("ORDER_CAT1_CODE");
				String cat1Type = tmp.getValue("CAT1_TYPE");
				String setMain = tmp.getValue("ORDERSET_FLG");
				if (cat1Code.equalsIgnoreCase("PHA_W") || cat1Code.equalsIgnoreCase("PHA_C")) {
					this.setParm(parmOrder, tmp, 1);
					continue;
				}
				if (tmp.getBoolean("CTRL_FLG")) {
					this.setParm(parmCtrl, tmp, 2);
					continue;
				}
				if (cat1Code.equalsIgnoreCase("PHA_G")) {
					this.setParm(parmChn, tmp, 3);
					continue;
				}
				if (cat1Type.equalsIgnoreCase("TRT") || cat1Type.equalsIgnoreCase("PLN")
						|| cat1Type.equalsIgnoreCase("OTH")) {
					this.setParm(parmOp, tmp, 4);
					continue;
				}
				if (("Y".equalsIgnoreCase(setMain)
						&& (cat1Type.equalsIgnoreCase("LIS") || cat1Type.equalsIgnoreCase("RIS")))) {
					this.setParm(parmExa, tmp, 5);
					continue;
				}
				if (("N".equalsIgnoreCase(setMain) && cat1Code.equalsIgnoreCase("EXA"))) {
					this.setParm(parmExa, tmp, 6);
					continue;
				}
			}
		}
	
		result.setData("CHN", parmChn.getData());
		result.setData("OP", parmOp);
		result.setData("CTRL", parmCtrl);
		result.setData("ORDER", parmOrder);
		result.setData("EXA", parmExa);
		this.setReturnValue(result);
		this.closeWindow();
	}

	private void setParm(TParm parm, TParm tmp, int rxType) {
		parm.addData("ID", tmp.getValue("ID"));
		parm.addData("TRADE_NO", tmp.getValue("TRADE_NO"));
		parm.addData("ORDER_CODE", tmp.getValue("ORDER_CODE"));
		parm.addData("OWN_PRICE", tmp.getDouble("UNIT_PRICE"));
		parm.addData("MEDI_UNIT", tmp.getValue("UNIT_CODE"));
		parm.addData("AR_AMT", tmp.getDouble("RETAIL_PRICE"));
		// System.out.println("ss "+tmp.getValue("ORDER_CODE") +"
		// "+tmp.getDouble("RETAIL_PRICE"));
		parm.addData("MEDI_QTY", tmp.getValue("ORDER_NUM"));
		parm.addData("TAKE_DAYS", 1);
		parm.addData("RX_TYPE", rxType);
		parm.addData("MEM_PACKAGE_FLG", "Y");
	}

	public void onAll() {
		if (this.getValueBoolean("all")) {
			for (int i = 0; i < detailTable.getRowCount(); i++) {
				detailTable.setItem(i, "EXEC", "Y");
			}
		} else {
			for (int i = 0; i < detailTable.getRowCount(); i++) {
				detailTable.setItem(i, "EXEC", "N");
			}
		}
	}
}
