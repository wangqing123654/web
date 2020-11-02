package com.javahis.ui.ibs;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ibs.IBSReturnTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 住院计价退费
 * </p>
 * 
 * <p>
 * Description:住院计价退费
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014.05.12
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author cao yong
 * @version 1.0
 */
public class IBSReturnControl extends TControl {
	private TTable table;
	private String caseno;
	private TTabbedPane tablePan;
	private String inDate;

	/**
	 * 初始化
	 */
	public void onInit() {

		Object obj = getParameter();
		if (obj != null) {
			caseno = ((TParm) obj).getValue("CASE_NO");
			inDate =((TParm) obj).getValue("ADM_DATE");
		}
		
		inPage();
	}

	/**
	 * 初始化
	 */
	public void inPage() {
		// Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("E_DATE", date.toString().substring(0, 19).replace('-',
				'/'));
		this.setValue("S_DATE", inDate.toString().substring(0, 10).replace('-',
				'/')
				+ " 00:00:00");
		callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
				"%ROOT%\\config\\sys\\SYSFeePopup.x");// 医嘱代码
		callFunction("UI|ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.tablePan = (TTabbedPane) this.getComponent("TABLEPANE");// 页签切换
		this.setValue("COST_CENTER_CODE", Operator.getDept());
	}

	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);

	}

	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
	}

	/**
	 * 查询
	 */
	public void onQuery() {

		int selectedIndex = tablePan.getSelectedIndex();
		if (selectedIndex == 0) {
			table = this.getTable("TABLED");
			selecttabled(true);
		}
		if (selectedIndex == 1) {
			table = this.getTable("TABLES");
			this.addEventListener("TABLES->" + TTableEvent.CHANGE_VALUE,
					"onROOMTABLEChargeValue");// 回车监
			selecttabled(false);
		}
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");// 复选框监听
	}

	/**
	 * 查询
	 * 
	 * @param flag
	 */
	public void selecttabled(boolean flag) {
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
		String eDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
		TParm rexpParm = new TParm();
		rexpParm.setData("CASE_NO", caseno);
		rexpParm.setData("S_DATE", sDate);// 查询开始起时间
		rexpParm.setData("E_DATE", eDate);// 查询结束时间
		if (!"".equals(this.getValue("ORDER_CODE"))
				&& this.getValue("ORDER_CODE") != null) {// 医嘱查询
			rexpParm.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
		}
		if (!"".equals(this.getValueString("COST_CENTER_CODE"))
				&& this.getValueString("COST_CENTER_CODE") != null) {// 成本中心查询
			rexpParm.setData("COST_CENTER_CODE", this
					.getValue("COST_CENTER_CODE"));
		}
		if (!"".equals(this.getValue("REXP_TYPE"))
				&& this.getValue("REXP_TYPE") != null) {// 医嘱查询
			rexpParm.setData("REXP_CODE", this.getValue("REXP_TYPE"));
		}
		TParm selectparm = new TParm();
		if (flag) {// 详细查询
			rexpParm.setData("DOSAGE_QTY_Y","Y");
			selectparm = IBSReturnTool.getInstance().selectdataAll(rexpParm);
			if (selectparm.getCount() <= 0) {
				this.messageBox("查无数据");
				table.setParmValue(selectparm);
				return;
			}
			rexpParm.setData("DOSAGE_QTY_Y",null);
			rexpParm.setData("DOSAGE_QTY_N","Y");
			TParm selectUnParm = IBSReturnTool.getInstance().selectdataAll(
					rexpParm);
			String orderDetailSql = "SELECT SUM(TOT_AMT) TOT_AMT ,SUM(OWN_AMT) OWN_AMT FROM IBS_ORDD WHERE CASE_NO='"
					+ caseno + "'";
			String orderSql = "";
			TParm orderParm = null;
			// 集合医嘱汇总总金额
			for (int i = 0; i < selectparm.getCount("ORDER_CODE"); i++) {
				if (selectparm.getValue("ORDER_CODE", i).equals(
						selectparm.getValue("ORDERSET_CODE", i))
						&& selectparm.getValue("INDV_FLG", i).equals("N")) {

					orderSql = orderDetailSql + " AND CASE_NO_SEQ='"
							+ selectparm.getValue("CASE_NO_SEQ", i)
							+ "' AND ORDERSET_CODE='"
							+ selectparm.getValue("ORDERSET_CODE", i)
							+ "' AND ORDERSET_GROUP_NO='"
							+ selectparm.getValue("ORDERSET_GROUP_NO", i) + "'";
					orderParm = new TParm(TJDODBTool.getInstance().select(
							orderSql));
					selectparm.setData("TOT_AMT", i, orderParm.getDouble(
							"TOT_AMT", 0));
					selectparm.setData("OWN_PRICE", i, orderParm.getDouble(
							"OWN_AMT", 0)
							/ selectparm.getDouble("DOSAGE_QTY", i));
					selectparm.setData("OWN_AMT", i, orderParm.getDouble(
							"OWN_AMT", 0)
							/ selectparm.getDouble("DOSAGE_QTY", i));
				}
			}

			// 集合医嘱汇总总金额
			for (int i = 0; i < selectUnParm.getCount("BILL_DATE"); i++) {
				if (selectUnParm.getValue("ORDER_CODE", i).equals(
						selectUnParm.getValue("ORDERSET_CODE", i))
						&& selectUnParm.getValue("INDV_FLG", i).equals("N")) {
					orderSql = orderDetailSql + " AND CASE_NO_SEQ='"
							+ selectUnParm.getValue("CASE_NO_SEQ", i)
							+ "' AND ORDERSET_CODE='"
							+ selectUnParm.getValue("ORDERSET_CODE", i)
							+ "' AND ORDERSET_GROUP_NO='"
							+ selectUnParm.getValue("ORDERSET_GROUP_NO", i)
							+ "'";
					orderParm = new TParm(TJDODBTool.getInstance().select(
							orderSql));
					selectUnParm.setData("TOT_AMT", i, orderParm.getDouble(
							"TOT_AMT", 0));
					selectUnParm
							.setData("OWN_PRICE", i, orderParm.getDouble(
									"OWN_AMT", 0)
									/ Math.abs(selectUnParm.getDouble(
											"DOSAGE_QTY", i)));
				}
			}
			for (int i = selectUnParm.getCount("BILL_DATE"); i >=0; i--) {
				for (int j = selectparm.getCount("BILL_DATE")-1; j >=0; j--) {
					if (selectUnParm.getValue("ORDER_CODE",i)
							.equals(selectparm.getValue("ORDER_CODE",j))&&
								Math.abs(selectUnParm.getDouble("TOT_AMT",i))==
									selectparm.getDouble("TOT_AMT",j)&&
									Math.abs(selectUnParm.getDouble("OWN_PRICE",i))==
										selectparm.getDouble("OWN_PRICE",j)&&
										selectparm.getValue("INCLUDE_FLG",j).
										equals(selectUnParm.getValue("INCLUDE_FLG",i))&&
										Math.abs(selectUnParm.getDouble("OWN_RATE",i))==
											selectparm.getDouble("OWN_RATE",j)) {
						selectparm.removeRow(j);
						selectUnParm.removeRow(i);
						break;
					}
				}
			}
			for (int i = 0; i < selectUnParm.getCount("ORDER_CODE"); i++) {
				selectparm.addRowData(selectUnParm, i);
			}
		} else {// 汇总时间
			selectparm = IBSReturnTool.getInstance().selMergeFee(rexpParm);
		}
		if (selectparm.getCount() <= 0) {
			this.messageBox("查无数据");
		}
		table.setParmValue(selectparm);
	}

	/**
	 * 清空内容
	 */
	public void onClear() {
		String clearString = "ORDER_CODE;ORDER_DESC;COST_CENTER_CODE;REXP_TYPE";
		clearValue(clearString);
		int selectedIndex = tablePan.getSelectedIndex();
		if (selectedIndex == 0) {
			table = this.getTable("TABLED");
		}
		if (selectedIndex == 1) {
			table = this.getTable("TABLES");
		}
		table.removeRowAll();
		inPage();
	}

	/**
	 * 传回补充计价界面数据
	 */
	public void onRreturn() {
		int selectedIndex = tablePan.getSelectedIndex();
		String flg = "";
		if (selectedIndex == 0) {
			table = this.getTable("TABLED");
			flg = "N";
		}
		if (selectedIndex == 1) {
			table = this.getTable("TABLES");
			flg = "Y";
		}
		TParm parm = table.getParmValue();
		TParm result = new TParm();
		if (parm != null && parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				if ("Y".equals(parm.getValue("FLG", i))) {
					if (selectedIndex == 1) {
						if (parm.getDouble("RETURN_SUM", i) == 0) {
							this.messageBox("退的数量不能为零");
							return;
						}
					}
					result.addRowData(parm, i);
				}
			}
			if (result.getCount("ORDER_CODE") <= 0) {
				this.messageBox("请勾选要退费的数据");
				return;
			}
		} else {
			this.messageBox("没有要退费的数据");
			return;
		}
		result.setData("RETURN_FLG", flg);// 根据页签区分返回的数据操作不同的逻辑
		setReturnValue(result);
		this.closeWindow();
	}

	/**
	 * 选择退回数据
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable tables = (TTable) obj;
		tables.acceptText();
		int row = tables.getSelectedRow();
		TParm resultParm = table.getParmValue().getRow(row);
		int col = tables.getSelectedColumn();
		TParm tableParm =table.getParmValue();
		String columnName = tables.getDataStoreColumnName(col);
		int selType = tablePan.getSelectedIndex();
		double tableQty = 0;
		// double totAmt=0.00;
		if ("FLG".equals(columnName)) {
			if (!tables.getParmValue().getBoolean("FLG", row)) {
				// tables.getParmValue().setData("FLG",row,"N");
			} else {
				
				if (resultParm.getDouble("DOSAGE_QTY") < 0) {// 细项总量小于等于0校验
					this.messageBox("该数据不可退费");
					tableParm.setData("FLG", row, "N");
					table.setParmValue(tableParm);
					return false;
				} else {//总量大于0校验
					if (selType == 0) {// 明细
						TParm newTableParm = new TParm();
						newTableParm = table.getParmValue();
						String orderCode = resultParm.getValue("ORDER_CODE");
						String includeFlg= resultParm.getValue("INCLUDE_FLG");
						double ownRate=resultParm.getDouble("OWN_RATE");
						double ownPrice=resultParm.getDouble("OWN_PRICE");
						for (int i = 0; i < newTableParm.getCount(); i++) {
							if (newTableParm.getBoolean("FLG", i)
									&& orderCode.equals(newTableParm.getValue(
											"ORDER_CODE", i))&&
											includeFlg.equals(newTableParm.getValue(
													"INCLUDE_FLG", i))&&
													ownRate==newTableParm.getDouble(
															"OWN_RATE", i)&&ownPrice
															==newTableParm.getDouble(
																	"OWN_PRICE", i)) {
								tableQty += newTableParm.getDouble("DOSAGE_QTY", i);
							}
						}
						//明细校验===pangben 2016-1-28
						if (IBSReturnTool.getInstance().returnCheckDetail(caseno, resultParm, tableQty)) {
							tableParm.setData("FLG", row, "Y");
						} else {
							this.messageBox("退费数量超过合计数量");
							table.setItem(row, "FLG", "N");
							tableParm.setData("FLG", row, "N");
						}
					} else if (selType == 1) {// 汇总
						tableQty = resultParm.getDouble("RETURN_SUM");
						if (tableQty == 0) {
							this.messageBox("退费数量不可以为0");
							tableParm.setData("FLG", row, "N");
							table.setParmValue(tableParm);
							return false;
						}
						if (IBSReturnTool.getInstance().returnCheckSum(caseno, resultParm, tableQty)) {
							tableParm.setData("FLG", row, "Y");
						} else {
							this.messageBox("退费数量超过合计数量");
							table.setItem(row, "FLG", "N");
							tableParm.setData("FLG", row, "N");
						}
					}
					table.setParmValue(tableParm);
					// System.out.println("-----tableQty tableQty is ::"+tableQty);
				}
				// tables.getParmValue().setData("FLG",row,"Y");
			}
			
			// System.out.println("++==+++tables.getParmValue()is ::"+tables.getParmValue().getBoolean("FLG",row));
			
		}
		return true;

	}

	/**
	 * 退费数量校验
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onROOMTABLEChargeValue(Object obj) {

		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return false;
		}

		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}

		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		int row = node.getRow();
		String value = "" + node.getValue();
		Double dnum = table.getItemDouble(row, "DOSAGE_QTY");// 总量
		if ("RETURN_SUM".equals(columnName)) {
			if (table.getParmValue().getBoolean("FLG", row)) {
				this.messageBox("已经勾选不可修改数量");
				table.acceptText();
				return true;
			}else{
				if ("RETURN_SUM".equals(columnName)) {
					if (Double.parseDouble(value) > dnum) {
						this.messageBox("退的数量不能大于总量");
						table.acceptText();
						return true;
					} else if (Double.parseDouble(value) < 0) {
						this.messageBox("退的数量不能为负数");
						table.acceptText();
						return true;
		
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 细项退费校验
	 * 
	 * @param orderCode
	 * @param caseNo
	 * @param dosageQty
	 * @return
	 */
	private boolean returnCheck(TParm resultParm, String caseNo,
			double dosageQty, int row,int index) {
		String selQtySql="";
		TParm selQtyParm=null;
		//returnCheckNew(caseNo, resultParm, index);
		if (index==0) {
			if (resultParm.getValue("ORDER_CODE").equals(resultParm.getValue("ORDERSET_CODE"))&&
					resultParm.getValue("INDV_FLG").equals("N")) {
				if (resultParm.getDouble("DOSAGE_QTY")==1) {
					selQtySql="SELECT DOSAGE_QTY,ORDER_CODE FROM ( SELECT SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,A.ORDER_CODE," +
					"CASE WHEN SUM (A.DOSAGE_QTY)=0 THEN 0 ELSE SUM(C.TOT_AMT)/SUM (A.DOSAGE_QTY) END TOT_AMT FROM IBS_ORDD A ," +
					"(SELECT A.ORDERSET_GROUP_NO,SUM(A.OWN_AMT)/"+resultParm.getDouble("DOSAGE_QTY")+" TOT_AMT,A.ORDERSET_CODE,A.CASE_NO,COUNT(A.ORDER_CODE) NUM " +
					"FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) " +
					" AND CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
					" AND A.INCLUDE_FLG='"
					+resultParm.getValue("INCLUDE_FLG")+"' GROUP BY  A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.CASE_NO ) C WHERE A.CASE_NO=C.CASE_NO " +
						"AND A.ORDERSET_GROUP_NO=C.ORDERSET_GROUP_NO AND " +
						"A.ORDER_CODE=C.ORDERSET_CODE AND  A.CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
						" AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
						" AND A.INCLUDE_FLG='"
						+resultParm.getValue("INCLUDE_FLG")+"'  GROUP BY ORDER_CODE,C.NUM) WHERE TOT_AMT="+resultParm.getDouble("OWN_PRICE");
				}else{
					selQtySql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY ,ORDER_CODE  FROM IBS_ORDD A ," +
					"(SELECT A.ORDERSET_GROUP_NO,SUM(A.OWN_AMT)/C.DOSAGE_QTY TOT_AMT,A.ORDERSET_CODE,A.CASE_NO,COUNT(A.ORDER_CODE) NUM " +
					"FROM IBS_ORDD A,SYS_FEE B,(SELECT A.DOSAGE_QTY,A.CASE_NO,A.ORDERSET_GROUP_NO ,A.CASE_NO_SEQ FROM IBS_ORDD A WHERE CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
					" AND A.INDV_FLG = 'N' AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
					" AND A.INCLUDE_FLG='"
					+resultParm.getValue("INCLUDE_FLG")+"') C WHERE A.CASE_NO=C.CASE_NO AND A.CASE_NO_SEQ=C.CASE_NO_SEQ AND A.ORDERSET_GROUP_NO=C.ORDERSET_GROUP_NO AND  A.ORDER_CODE=B.ORDER_CODE(+) " +
					" AND A.CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
					" AND A.INCLUDE_FLG='"
					+resultParm.getValue("INCLUDE_FLG")+"' GROUP BY  A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.CASE_NO,C.DOSAGE_QTY ) C WHERE A.CASE_NO=C.CASE_NO " +
						"AND A.ORDERSET_GROUP_NO=C.ORDERSET_GROUP_NO AND " +
						"A.ORDER_CODE=C.ORDERSET_CODE AND  A.CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
						" AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
						" AND A.INCLUDE_FLG='"
						+resultParm.getValue("INCLUDE_FLG")+"' AND C.TOT_AMT="+resultParm.getDouble("OWN_PRICE")+" GROUP BY ORDER_CODE";
				}
			}else{
				selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
					+ "  FROM IBS_ORDD  WHERE ORDER_CODE = '"
					+ resultParm.getValue("ORDER_CODE") + "' "
					+ "    AND CASE_NO = '" + caseNo + "' AND INCLUDE_FLG='"+resultParm.getValue("INCLUDE_FLG")+"' AND OWN_RATE="
					+ resultParm.getDouble("OWN_RATE") + " AND OWN_PRICE="+resultParm.getDouble("OWN_PRICE")+"   GROUP BY ORDER_CODE ";
			}
			selQtyParm = new TParm(TJDODBTool.getInstance().select(selQtySql));
		}else{
			if (null!=resultParm.getValue("CAT_FLG")&&resultParm.getValue("CAT_FLG").equals("Y")) {
				String checkSql="SELECT DOSAGE_QTY,ORDER_CODE FROM IBS_ORDD A WHERE CASE_NO='"+caseNo+
				"' AND  (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' AND INCLUDE_FLG='"
				+resultParm.getValue("INCLUDE_FLG")+"' AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+" AND DOSAGE_QTY>1";
				//单条集合医嘱总量大于1
				TParm checkQtyParm = new TParm(TJDODBTool.getInstance().select(checkSql));
				if (checkQtyParm.getCount()>0) {
					selQtySql="SELECT A.ORDERSET_GROUP_NO,SUM(A.OWN_AMT) TOT_AMT,A.ORDERSET_CODE,A.CASE_NO,A.DOSAGE_QTY " +
					"FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) " +
					" AND CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
					" AND A.INCLUDE_FLG='"
						+resultParm.getValue("INCLUDE_FLG")+"' GROUP BY  A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.CASE_NO,A.DOSAGE_QTY  ";
					double dosageQtyDemo=0.00;
					selQtyParm = new TParm(TJDODBTool.getInstance().select(selQtySql));
					for (int i = 0; i < selQtyParm.getCount(); i++) {
						if (resultParm.getDouble("OWN_PRICE")==
							selQtyParm.getDouble("TOT_AMT",i)/selQtyParm.getDouble("DOSAGE_QTY",i)) {
							dosageQtyDemo+=selQtyParm.getDouble("DOSAGE_QTY",i);
						}
					}
					if (Math.abs(dosageQty) > dosageQtyDemo) {
						this.messageBox("退费数量超过合计数量");
						table.setItem(row, "FLG", "N");
						return false;
					}
					return true;
				} else{
					selQtySql="SELECT DOSAGE_QTY,ORDER_CODE FROM ( SELECT SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,A.ORDER_CODE," +
					"CASE WHEN SUM (A.DOSAGE_QTY)=0 THEN 0 ELSE SUM(C.TOT_AMT)/SUM (A.DOSAGE_QTY) END TOT_AMT FROM IBS_ORDD A ," +
					"(SELECT A.ORDERSET_GROUP_NO,SUM(A.OWN_AMT)/"+resultParm.getDouble("DOSAGE_QTY")+" TOT_AMT,A.ORDERSET_CODE,A.CASE_NO,COUNT(A.ORDER_CODE) NUM " +
					"FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE(+) " +
					" AND CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
					" AND A.INDV_FLG = 'Y' AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
					" AND A.INCLUDE_FLG='"
						+resultParm.getValue("INCLUDE_FLG")+"' GROUP BY  A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.CASE_NO ) C WHERE A.CASE_NO=C.CASE_NO " +
								"AND A.ORDERSET_GROUP_NO=C.ORDERSET_GROUP_NO AND " +
								"A.ORDER_CODE=C.ORDERSET_CODE AND  A.CASE_NO='"+caseNo+"' AND A.ORDERSET_CODE='"+resultParm.getValue("ORDER_CODE")+"' "+
					" AND (A.INDV_FLG = 'N' OR A.INDV_FLG IS NULL) AND A.OWN_RATE= "+resultParm.getDouble("OWN_RATE")+
					" AND A.INCLUDE_FLG='"
					+resultParm.getValue("INCLUDE_FLG")+"'  GROUP BY ORDER_CODE,C.NUM) WHERE TOT_AMT="+resultParm.getDouble("OWN_PRICE");
					
				}
			}else{
				selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
					+ "  FROM IBS_ORDD  WHERE ORDER_CODE = '"
					+ resultParm.getValue("ORDER_CODE") + "' "
					+ "    AND CASE_NO = '" + caseNo + "' AND INCLUDE_FLG='"+resultParm.getValue("INCLUDE_FLG")+"' AND OWN_RATE="
					+ resultParm.getDouble("OWN_RATE") + " AND OWN_PRICE="+resultParm.getDouble("OWN_PRICE")+"   GROUP BY ORDER_CODE ";

			}
			selQtyParm = new TParm(TJDODBTool.getInstance().select(selQtySql));
		}

		//System.out.println("selQtyParm：：：：：："+selQtyParm);
		double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
		if (Math.abs(dosageQty) > dosageQtyTot) {
			this.messageBox("退费数量超过合计数量");
			table.setItem(row, "FLG", "N");
			return false;
		}

		return true;
	}
}
