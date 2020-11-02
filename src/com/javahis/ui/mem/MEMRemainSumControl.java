package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class MEMRemainSumControl extends TControl {
	TTable tableM, tableD;
	HashMap<String, String> map;
	Map<String, String> gatherMap;
	TParm gatherParm;
	String sDate;
	boolean isDebug = true;
	Pat pat;
	// 模板代码combo
	private TComboBox combo;

	public void onInit() {
		super.onInit();
		String date = StringTool.getString(
				TJDODBTool.getInstance().getDBTime(), "yyyy/MM/dd");
		this.setValue("START_DATE", date + " 00:00:00");
		this.setValue("END_DATE", date + " 23:59:59");
		tableM = (TTable) this.getComponent("TABLE_M");
		tableD = (TTable) this.getComponent("TABLE_D");

		map = new HashMap<String, String>();
		map.put("C0", "现金");
		map.put("C1", "刷卡");
		map.put("C2", "汇票");
		map.put("T0", "支票");
		map.put("C4", "应收款");
		map.put("LPK", "礼品卡");
		map.put("XJZKQ", "现金折扣券");
		map.put("BXZF", "保险支付");
		map.put("WX", "微信");
		map.put("ZFB", "支付宝");

		String gatherTypeSql = " SELECT GATHER_TYPE, PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE";
		gatherParm = new TParm(TJDODBTool.getInstance().select(gatherTypeSql));
		gatherMap = new HashMap<String, String>();
		for (int i = 0; i < gatherParm.getCount(); i++) {
			gatherMap.put(gatherParm.getValue("PAYTYPE", i), map.get(gatherParm
					.getValue("GATHER_TYPE", i)));
		}
		initComponent();
		initCombo();
	}

	/**
	 * 初始化主Combo
	 */
	public void initCombo() {
		// 取得combo用数据
		String sql = "SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'MEM_PACKAGE_CLASS' ORDER BY ID";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() != 0) {
			// this.messageBox_(result.getErrText());
			this.messageBox_("找不到数据");
			return;
		}
		combo.setParmValue(result);
		combo.onInit();
	}

	/**
	 * 初始化控件
	 */
	public void initComponent() {
		combo = (TComboBox) this.getComponent("PACKAGE_CLASS");

	}

	// SEQ_NO;BILL_DATE;PAT_NAME;MR_NO;PACKAGE_CODE;SECTION_BUY_COUNT;AR_AMT;SECTION_RETURN_COUNT;RETURN_AMT;PAY_TYPE;ACCOUNT_USER;REMAIN_AR_AMT
	/**
	 * 查询
	 */
	public void onQuery() {
		try {
			tableD.removeRowAll();// 查询时清空细表
			String sDate = StringTool.getString((Timestamp) this
					.getValue("START_DATE"), "yyyyMMddHHmmss");
			String eDate = StringTool.getString((Timestamp) this
					.getValue("END_DATE"), "yyyyMMddHHmmss");
			StringBuffer sb = new StringBuffer("");

			// AA表
			sb
					.append("WITH AA AS (SELECT DISTINCT A.TRADE_NO, "
							+ " A.BILL_DATE,B.PACKAGE_CODE,A.MR_NO,C.PAT_NAME,A.AR_AMT,A.OPT_USER ACCOUNT_USER,'C0' PAY_TYPE  "
							+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B,SYS_PATINFO C "
							+ " WHERE A.TRADE_NO = B.TRADE_NO "
							+ " AND B.MR_NO = C.MR_NO ");
			if (!sDate.equals("") && !eDate.equals("")) {
				sb.append("AND A.BILL_DATE BETWEEN TO_DATE('" + sDate
						+ "','yyyyMMdd HH24miss') AND TO_DATE('" + eDate
						+ "','yyyyMMdd HH24miss')");
			}
			if (this.getValue("PACKAGE_CODE") != null
					&& !"".equals(this.getValue("PACKAGE_CODE"))) {
				sb.append("AND B.PACKAGE_CODE = '"
						+ this.getValue("PACKAGE_CODE") + "' ");
			}

			if (this.getValue("MR_NO") != null
					&& !"".equals(this.getValue("MR_NO"))) {
				sb.append("AND A.MR_NO = '" + this.getValue("MR_NO") + "'");
			}

			// sb.append(" AND A.REST_FLAG = 'N' AND B.REST_TRADE_NO IS NULL )");
			sb.append(" AND A.REST_FLAG = 'N' )");
			// BB表
			sb.append(", BB AS ");
			sb.append("(SELECT SUM(AR_AMT) REMAIN_AR_AMT,TRADE_NO "
					+ " FROM MEM_PAT_PACKAGE_SECTION "
					+ " WHERE  USED_FLG = '0' AND REST_TRADE_NO IS NULL  ");
			if (this.getValue("PACKAGE_CODE") != null
					&& !this.getValue("PACKAGE_CODE").equals("")) {
				sb.append(" AND PACKAGE_CODE = '"
						+ this.getValue("PACKAGE_CODE") + "' ");
			}
			sb.append(" GROUP BY TRADE_NO )");
			// CC表
			sb
					.append(" ,CC AS (SELECT TRADE_NO,MR_NO,"
							+ " PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,"
							+ " PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,USED_FLG "
							+ " FROM MEM_PACKAGE_TRADE_M WHERE 1 = 1 ");
			if (!sDate.equals("") && !eDate.equals("")) {
				sb.append(" AND BILL_DATE BETWEEN TO_DATE('" + sDate
						+ "','yyyyMMdd HH24miss') AND TO_DATE('" + eDate
						+ "','yyyyMMdd HH24miss')");
			}
			sb.append(") ");
			// EE 表
			sb
					.append(" ,EE AS(SELECT A.TRADE_NO, COUNT (*) SECTION_RETURN_COUNT, SUM (B.AR_AMT) RETURN_AMT"
							+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B"
							+ " WHERE A.TRADE_NO = B.TRADE_NO AND B.REST_TRADE_NO IS NOT NULL"
							+ " GROUP BY A.TRADE_NO )");
			// GG表
			// sb
			// .append(" ,GG AS(SELECT A.TRADE_NO, COUNT (*) SECTION_RETURN_COUNT, SUM (B.AR_AMT) CUTT_AMT "
			// + " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B," +
			// "(SELECT C.CASE_NO FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, OPD_ORDER C "
			// +
			// "WHERE A.TRADE_NO = B.TRADE_NO AND B.CASE_NO = C.CASE_NO AND C.PRINT_FLG ='Y'  AND B.USED_FLG = '1'  GROUP BY C.CASE_NO) C"
			// + " WHERE A.TRADE_NO = B.TRADE_NO AND B.CASE_NO = C.CASE_NO "
			// + " AND B.USED_FLG = '1' GROUP BY A.TRADE_NO) ");
			sb
					.append(" ,GG AS(SELECT D.TRADE_NO,SUM (A.AR_AMT) CUTT_AMT FROM OPD_ORDER A, MEM_PAT_PACKAGE_SECTION_D D WHERE "
							+ " D.USED_FLG = '1' AND D.ID=A.MEM_PACKAGE_ID AND A.CASE_NO=D.CASE_NO AND A.PRINT_FLG ='Y' GROUP BY TRADE_NO) ");
			// HH表
			sb.append(" ,HH AS(SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE"
					+ " GROUP_ID = 'MEM_PACKAGE_CLASS' ORDER BY ID) ");

			// FF表
			sb.append(", FF AS ");
			sb.append("(SELECT TRADE_NO,COUNT(TRADE_NO) SECTION_BUY_COUNT "
					+ " FROM MEM_PAT_PACKAGE_SECTION " + " WHERE 1 = 1");
			if (this.getValue("PACKAGE_CODE") != null
					&& !this.getValue("PACKAGE_CODE").equals("")) {
				sb.append(" AND PACKAGE_CODE = '"
						+ this.getValue("PACKAGE_CODE") + "' ");
			}
			sb.append(" GROUP BY TRADE_NO ) ");

			sb
					.append("SELECT AA.*,BB.REMAIN_AR_AMT,CC.PAY_TYPE01,CC.PAY_TYPE02,CC.PAY_TYPE03,CC.PAY_TYPE04,CC.PAY_TYPE05,"
							+ " CC.PAY_TYPE06,CC.PAY_TYPE07,CC.PAY_TYPE08,CC.PAY_TYPE09,CC.PAY_TYPE10,CC.USED_FLG,DD.ADM_TYPE,"
							+ " FF.SECTION_BUY_COUNT,EE.SECTION_RETURN_COUNT,EE.RETURN_AMT,HH.CHN_DESC,"
							+ " NVL(GG.CUTT_AMT,0.00) CUTT_AMT "
							+ // 购买时程个数；退费时程个数；退费时程总金额
							" FROM AA,BB,CC,EE,FF,GG,HH,MEM_PACKAGE DD "
							+ " WHERE AA.TRADE_NO = BB.TRADE_NO(+) AND AA.TRADE_NO = CC.TRADE_NO AND DD.PACKAGE_CLASS =HH.ID "
							+ " AND AA.TRADE_NO = EE.TRADE_NO(+) AND AA.TRADE_NO = FF.TRADE_NO(+) AND AA.TRADE_NO = GG.TRADE_NO(+) "
							+ " AND AA.PACKAGE_CODE = DD.PACKAGE_CODE" + " ");

			if (this.getValue("STATUS") != null
					&& !"".equals(this.getValue("STATUS"))) {
				if (this.getValue("STATUS").equals("0")) {
					sb.append(" AND (AA.AR_AMT - NVL(CUTT_AMT,0.00) <= 0.00 )");
				} else {
					sb.append("  AND (AA.AR_AMT - NVL(CUTT_AMT,0.00) > 0.00 ) ");
				}
			}
			// 套餐类别 add by yanmm 2017/11/6
			if (this.getValue("PACKAGE_CLASS") != null
					&& !"".equals(this.getValue("PACKAGE_CLASS"))) {
				sb.append("AND DD.PACKAGE_CLASS = '"
						+ this.getValue("PACKAGE_CLASS") + "' ");
			}
			if (this.getRadioButton("tRadioButton_1").isSelected()) {
				sb.append(" AND DD.ADM_TYPE = 'O'");
			}

			if (this.getRadioButton("tRadioButton_2").isSelected()) {
				sb.append(" AND DD.ADM_TYPE = 'I'");
			}
			sb.append(" ORDER BY AA.BILL_DATE,AA.MR_NO");
		//	System.out.println("tttt:" + sb.toString());
			TParm result = new TParm(TJDODBTool.getInstance().select(
					sb.toString()));

			String key, value, keyStr;
			for (int i = 0; i < result.getCount("BILL_DATE"); i++) {
				String payType = "";
				// String paySum = "";
				for (int j = 0; j < gatherParm.getCount(); j++) {
					key = gatherParm.getValue("PAYTYPE", j);
					value = result.getValue(key, i);
					keyStr = gatherMap.get(key);
					payType += onReturnPayType(keyStr, value);
					// paySum += onReturnPay(value);
				}
				if (!"".equals(payType)) {
					payType = payType.substring(1, payType.length());
				}
				result.setData("PAY_TYPE", i, payType);
				result.setData("SEQ_NO", i, i + 1);
				result.setData("REMAIN_AMT", i,
						(result.getDouble("AR_AMT", i) - result.getDouble(
								"CUTT_AMT", i)));
				// result.setData("PAY_SUM", i, paySum);
			}
			if (result.getErrCode() < 0) {
				this.messageBox("" + result.getErrText());
				return;
			}
			if (result.getCount() <= 0) {
				this.messageBox("没有数据");
				onClear();
				return;
			}
			// 汇总统计 add by yanmm 2017/11/6
			int count = result.getCount("SEQ_NO");
			double sumUnArAmt = 0.00;
			double sumReturnAmt = 0.00;
			double sumUnRemainArAmt = 0.00;
			double sumCuttAmt = 0.00;
			// double sumPay = 0.00;
			int sumBuyCount = 0;
			int sumReturnCount = 0;
			for (int i = 0; i < count; i++) {
				double unArAmt = result.getDouble("AR_AMT", i);
				double unReturnAmt = result.getDouble("RETURN_AMT", i);
				double unRemainArAmt = result.getDouble("REMAIN_AMT", i);
				double unRemainCuttAmt = result.getDouble("CUTT_AMT", i);
				// double unSumPay =
				// Double.parseDouble(result.getValue("PAY_SUM", i));
				int unBuyCount = result.getInt("SECTION_BUY_COUNT", i);
				int unReturnCount = result.getInt("SECTION_RETURN_COUNT", i);
				sumUnArAmt += unArAmt;
				sumReturnAmt += unReturnAmt;
				sumUnRemainArAmt += unRemainArAmt;
				sumCuttAmt += unRemainCuttAmt;
				sumBuyCount += unBuyCount;
				sumReturnCount += unReturnCount;
				// sumPay += unSumPay;
			}
			result.setData("SEQ_NO", count, "");
			result.setData("BILL_DATE", count, "");
			result.setData("PAT_NAME", count, "");
			result.setData("MR_NO", count, "总计：");
			result.setData("PACKAGE_CODE", count, "");
			result.setData("SECTION_BUY_COUNT", count, sumBuyCount);
			result.setData("AR_AMT", count, StringTool.round(sumUnArAmt, 2));
			result.setData("SECTION_RETURN_COUNT", count, sumReturnCount);
			result.setData("RETURN_AMT", count, StringTool.round(sumReturnAmt,
					2));
			result.setData("PAY_TYPE", count, "");
			result.setData("ACCOUNT_USER", count, "");
			result.setData("REMAIN_AMT", count, StringTool.round(
					sumUnRemainArAmt, 2));
			result.setData("CHN_DESC", count, "");
			result.setData("CUTT_AMT", count, StringTool.round(sumCuttAmt, 2));

			tableM.setParmValue(result);
		} catch (Exception e) {
			if (isDebug) {
				System.out
						.println("come in class: MEMRemainSumControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
	}

	// 序1号,30;购2买日期,80,Timestamp,yyyy/MM/dd;姓3名,100;病案4号,100;套餐5名称,150,PACKAGE_CODE;时程购6买个数,80;购买总7金额,80,double,########0.00;时8程退个数,
	// 80;退费总9金额,80,double,########0.00;支付10方式,220,PAY_TYPE;销11售人,100,ACCOUNT_USER;套餐12余额,80,double,########0.00
	// 1SEQ_NO;2BILL_DATE;3PAT_NAME;4MR_NO;5PACKAGE_CODE;6SECTION_BUY_COUNT;7AR_AMT;8SECTION_RETURN_COUNT;9RETURN_AMT;10PAY_TYPE;11ACCOUNT_USER;12REMAIN_AR_AMT
	/**
	 * 根据传入参数组成支付方式add by sunqy 20140717
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private String onReturnPayType(String key, String value) {
		DecimalFormat format = new DecimalFormat("########0.00");
		String str = "";
		if (value.length() > 0 && Math.abs(Double.valueOf(value)) > 0) {
			str = ";" + key + ":" + format.format(Double.valueOf(value));
		}
		return str;
	}

	// private String onReturnPay(String value) {
	// DecimalFormat format = new DecimalFormat("########0.00");
	// String str = "";
	// if (value.length() > 0 && Math.abs(Double.valueOf(value)) > 0) {
	// str = format.format(Double.valueOf(value));
	// }
	// return str;
	// }

	/**
	 * 病案号回车
	 */
	public void onMrno() {
		pat = new Pat();
		String mrno = getValue("MR_NO").toString().trim();
		if (!this.queryPat(mrno)) {
			this.onClear();
			return;
		}
		pat = Pat.onQueryByMrNo(mrno);
		if (pat == null || "".equals(getValueString("MR_NO"))) {
			this.messageBox_("查无病患! ");
			this.onClear();
			return;
		}
		this.setValue("MR_NO", pat.getMrNo());
		this.onQuery();
	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean queryPat(String mrNo) {
		pat = new Pat();
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("E0081");
			return false;
		}
		String allMrNo = PatTool.getInstance().checkMrno(mrNo);
		if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
			messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
		}

		return true;
	}

	/**
	 * 主表点击事件
	 */
	public void onTableClick() {
		try {
			int row = tableM.getSelectedRow();
			TParm tableParm = tableM.getParmValue();
			String tradeNo = tableParm.getValue("TRADE_NO", row);
			String packageCode = tableParm.getValue("PACKAGE_CODE", row);
			String admType = tableParm.getValue("ADM_TYPE", row);
			String sql = "";
			if (admType.equals("O")) {
				sql = "SELECT * FROM (SELECT A.ID,A.SECTION_DESC,CASE A.USED_FLG WHEN '0' THEN '未使用' WHEN '1' THEN '使用' END USED_FLG,"
						+ "  A.AR_AMT ORIGINAL_PRICE,C.REALDR_CODE DR_CODE,C.REALDEPT_CODE DEPT_CODE,C.ADM_DATE "
						+ "  FROM MEM_PAT_PACKAGE_SECTION A,"
						+ "  ("
						+ "	SELECT A.ID, A.TRADE_NO,B.REALDR_CODE, B.REALDEPT_CODE, B.ADM_DATE "
						+ "	FROM MEM_PAT_PACKAGE_SECTION A,  REG_PATADM B"
						+ "	WHERE     A.CASE_NO = B.CASE_NO(+) "
						+ "  "
						+ "	) C "
						+ "  WHERE A.TRADE_NO = '"
						+ tradeNo
						+ "' "
						+ "  AND A.PACKAGE_CODE = '"
						+ packageCode
						+ "' "
						+ "  AND A.TRADE_NO = C.TRADE_NO(+)"
						+ "  AND  A.ID = C.ID "
						+ "  AND A.REST_TRADE_NO IS  NULL "
						+ "  UNION"
						+ "  SELECT A.ID, A.SECTION_DESC,'退费' USED_FLG,"
						+ "  A.AR_AMT ORIGINAL_PRICE,'' DR_CODE,'' DEPT_CODE,B.BILL_DATE ADM_DATE "
						+ "  FROM MEM_PAT_PACKAGE_SECTION A,MEM_PACKAGE_TRADE_M B"
						+ "  WHERE A.TRADE_NO = '"
						+ tradeNo
						+ "' "
						+ "  AND A.PACKAGE_CODE = '"
						+ packageCode
						+ "' "
						+ " AND A.REST_TRADE_NO = B.TRADE_NO(+) "
						+ " AND A.REST_TRADE_NO IS NOT NULL) "
						+ " ORDER BY ID ";
			} else {
				sql = "SELECT * FROM "
						+ " (SELECT A.ID,A.AR_AMT ORIGINAL_PRICE, A.SECTION_DESC,CASE A.USED_FLG WHEN '0' THEN '未使用' WHEN '1' THEN '使用' END USED_FLG,"
						+ "  C.VS_DR_CODE DR_CODE,C.IN_DEPT_CODE DEPT_CODE,C.ADM_DATE "
						+ "  FROM MEM_PAT_PACKAGE_SECTION A,"
						+ "  ("
						+ "  SELECT A.ID, A.TRADE_NO, B.IN_DEPT_CODE,B.VS_DR_CODE, B.ADM_DATE "
						+ "  FROM MEM_PAT_PACKAGE_SECTION A, ADM_INP B"
						+ "  WHERE     A.CASE_NO = B.CASE_NO(+) "
						+ "  AND A.REST_TRADE_NO IS NULL	) C "
						+ "  WHERE A.TRADE_NO = '"
						+ tradeNo
						+ "' "
						+ "  AND A.PACKAGE_CODE = '"
						+ packageCode
						+ "' "
						+ "  AND A.REST_TRADE_NO IS NULL "
						+ "  AND A.TRADE_NO = C.TRADE_NO(+)"
						+ "  AND A.ID = C.ID "
						+ "  UNION "
						+ "  SELECT A.ID,A.AR_AMT ORIGINAL_PRICE,A.SECTION_DESC,'退费' USED_FLG,"
						+ "  '' DR_CODE,'' DEPT_CODE,B.BILL_DATE ADM_DATE "
						+ "  FROM MEM_PAT_PACKAGE_SECTION A,MEM_PACKAGE_TRADE_M B"
						+ "  WHERE A.TRADE_NO = '" + tradeNo + "' "
						+ "  AND A.PACKAGE_CODE = '" + packageCode + "' "
						+ "  AND A.REST_TRADE_NO = B.TRADE_NO(+) "
						+ "  AND A.REST_TRADE_NO IS NOT NULL)"
						+ "  ORDER BY ID ";
				// System.out.println("1:::"+sql);
			}
			// System.out.println(""+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			tableD.setParmValue(result);
		} catch (Exception e) {
			if (isDebug) {
				System.out
						.println("come in class: MEMRemainSumControl ，method ：onTableClick");
				e.printStackTrace();
			}
		}
	}

	// 时程名称,150;时程金额,100,double,########0.00;使用/退费状态,100;就诊/退费日期,100,Timestamp,yyyy/MM/dd;开单医生,120,DR_CODE;开单科室,120,DEPT_CODE
	// SECTION_DESC;ORIGINAL_PRICE;USED_FLG;ADM_DATE;DR_CODE;DEPT_CODE
	// 0,left;1,right;2,left;3,left;4,left;5,left
	/**
	 * 打印
	 */
	public void onPrint() {
		TParm tableShowParm = tableM.getShowParmValue();
		TParm data = new TParm();

		tableShowParm.addData("SYSTEM", "COLUMNS", "SEQ_NO");
		tableShowParm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		tableShowParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PACKAGE_CODE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "SECTION_BUY_COUNT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "SECTION_RETURN_COUNT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "RETURN_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "PAY_TYPE");
		tableShowParm.addData("SYSTEM", "COLUMNS", "ACCOUNT_USER");
		tableShowParm.addData("SYSTEM", "COLUMNS", "REMAIN_AMT");
		tableShowParm.addData("SYSTEM", "COLUMNS", "CHN_DESC");
		tableShowParm.addData("SYSTEM", "COLUMNS", "CUTT_AMT");

		data.setData("TABLE", tableShowParm.getData());
		data.setData("PRINT_DATE", "TEXT", "打印日期："
				+ TJDODBTool.getInstance().getDBTime().toString().substring(0,
						19).replaceAll("-", "/"));
		data.setData("PRINT_USER", "TEXT", "打印人员："
				+ getUserName(Operator.getID()));
		data.setData("DATE", "TEXT", "购买日期："
				+ this.getValue("START_DATE").toString().substring(0, 19)
						.replaceAll("-", "/")
				+ " ~ "
				+ this.getValue("END_DATE").toString().substring(0, 19)
						.replaceAll("-", "/"));
		if (this.getValue("PACKAGE_CLASS") != null
				&& !"".equals(this.getValue("PACKAGE_CLASS"))) {
			data.setData("CHN_DESC", "TEXT", "套餐类型选择："
					+ tableShowParm.getValue("CHN_DESC", 0));
		}

		data.setData("PACKAGE_CLASS", "TEXT", "套餐类型："
				+ this.getValue("PACKAGE_CLASS"));
		if (this.getValue("STATUS") != null
				&& !"".equals(this.getValue("STATUS"))) {
			if (this.getValue("STATUS").equals("0")) {
				data.setData("STATUS", "TEXT", "收退状态：已完成");
			} else if (this.getValue("STATUS").equals("1")) {
				data.setData("STATUS", "TEXT", "收退状态：未完成");
			}
		}

		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMRemainSumPrint.jhw",
				data);
	}

	/**
	 * 获取名称
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserName(String userId) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" + userId
						+ "' "));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * 汇出
	 */
	public void onExport() {
		if (tableM.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(tableM, "预收套餐余额明细表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.onInit();
		this.clearValue("PACKAGE_CODE;STATUS;MR_NO;PACKAGE_CLASS");
		tableM.setParmValue(new TParm());
		tableD.setParmValue(new TParm());
		// tableM.removeRowAll();
		// tableD.removeRowAll();
	}

	/**
	 * 
	 * @return
	 */
	public TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) this.getComponent(tagName);
	}
}
