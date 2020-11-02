package com.javahis.ui.bil;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:门诊票据减免
 * </p>
 * 
 * <p>
 * Description:门诊票据减免
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Bluecore
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */

public class BILOPDReduceControl extends TControl {

	private static final String actionName = "action.bil.BILOPBReduceAction";
	private TTabbedPane TabbedPane;
	private TComboBox type;
	private TTable tabled;
	private TTable tabledd;
	private TTable tablem;
	private TCheckBox alld;
	private TCheckBox alldd;
	private TParm oneReceiptParm;
	private Map rexpMap;
	String caseNo;
	private TParm initTableParm = new TParm();
	private TNumberTextField numberTextField;//===yanjing 20141219 失去焦点事件

	/**
	 * 初始化
	 * */
	public void onInit() {
		super.onInit();
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null || obj.equals("")) {
			return;
		}
		if (obj != null || obj != "") {
			initParm = (TParm) obj;
			caseNo = initParm.getData("CASE_NO") + "";
//			initTableParm = initParm.getValue("TableParm");
			// 初始化病患信息
			this.setValue("MR_NO", initParm.getData("MR_NO") + "");
			this.setValue("PAT_NAME", initParm.getData("PAT_NAME") + "");
			this.setValue("SEX_CODE", initParm.getData("SEX_CODE") + "");
			this.setValue("CTZ1_CODE", initParm.getData("CTZ1_CODE") + "");
			this.setValue("RECEIPT_NO", initParm.getData("RECEIPT_NO") + "");
			if (null==initParm.getValue("ADM_DATE")||initParm.getValue("ADM_DATE").length()<=5) {
			}else{
				this.setValue("ADM_DATE", initParm.getTimestamp("ADM_DATE"));
			}			
			this.setValue("CASE_NO", caseNo);
		}
		// 初始化控件
		initComponent();
		// 初始化监听事件
		initListener();
		numberTextField = (TNumberTextField) this.getComponent("REDUCE_AMT");
		numberTextField.addFocusListener(new FocusListener(){

				@Override
				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
//					getRate();

				}

				@Override
				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
					onShare();

				}
	    		
	    	});
		//门诊收费界面传过来的数据
		onChangeTableData(initParm);
		// 初始化总额减免页签
		onSelectM();
	}

	/**
	 * 初始化控件
	 * */
	public void initComponent() {
		TabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_1");
		TabbedPane.setSelectedIndex(0);
		type = (TComboBox) this.getComponent("TYPE");
		type.setValue("1");
		tabled = (TTable) this.getComponent("TABLED");
		tabledd = (TTable) this.getComponent("TABLEDD");
		TParm parm = new TParm();
		tabledd.setParmValue(parm);
		tablem = (TTable) this.getComponent("TABLEM");
		callFunction("UI|OWN_AMT|setEnabled", false);
		this.setValue("REDUCE_USER", Operator.getID());
		alld = (TCheckBox) this.getComponent("ALLD");
		alldd = (TCheckBox) this.getComponent("ALLDD");
		rexpMap = this.getRexpMap();
	}
/**
 * 整理初始化表格的数据
 */
	private void onChangeTableData(TParm initParm){
		int j = 0;
		for(int i = 0;i<initParm.getCount("ORDER_CODE");i++){
			if(initParm.getValue("CHARGE", i).equals("Y")){//选择时
				initTableParm.setData("FLG", j, false) ;
				initTableParm.setData("DATA_TYPE", j, type.getValue()) ;
				initTableParm.setData("ORDER_CODE", j, initParm.getValue("ORDER_CODE", i)) ;
				initTableParm.setData("ORDER_DESC", j, initParm.getValue("ORDER_DESC", i)) ;
				initTableParm.setData("SPECIFICATION", j, initParm.getValue("SPECIFICATION", i)) ;
				initTableParm.setData("OWN_PRICE", j, initParm.getValue("OWN_PRICE", i)) ;
				initTableParm.setData("AR_AMT", j, initParm.getValue("AR_AMT", i)) ;
				initTableParm.setData("REDUCE_AMT", j, 0.00) ;
				initTableParm.setData("TOT_AMT", j, initParm.getValue("AR_AMT", i)) ;
				initTableParm.setData("DOSAGE_QTY", j, initParm.getValue("DOSAGE_QTY", i)) ;
				initTableParm.setData("DOSAGE_UNIT", j, initParm.getValue("DOSAGE_UNIT", i)) ;
				initTableParm.setData("REXP_CODE", j, initParm.getValue("REXP_CODE", i)) ;
				initTableParm.setData("SETMAIN_FLG", j, initParm.getValue("SETMAIN_FLG", i)) ;
				j++;
			}
		}
		initTableParm.setCount(j);
	}
	/**
	 * 全选
	 */
	public void onSelAllD() {
		tabled.acceptText();
		TParm parm = tabled.getParmValue();
		if (parm.getCount() <= 0) {
			return;
		}
		// 全选勾选
		if (this.getValueBoolean("ALLD")) {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG", i, "Y");

			}
		} else {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG", i, "N");
			}

		}
		tabled.setParmValue(parm);
	}

	/**
	 * 全选
	 */
	public void onSelAllDD() {
		tabled.acceptText();
		TParm parm = tabledd.getParmValue();
		if (tabledd.getRowCount() <= 0) {
			return;
		}
		// 全选勾选
		if (alldd.isSelected()) {
			for (int i = 0; i < tabledd.getRowCount(); i++) {
				parm.setData("FLG", i, "Y");

			}
		} else {
			for (int i = 0; i < tabledd.getRowCount(); i++) {
				parm.setData("FLG", i, "N");
			}

		}

		tabledd.setParmValue(parm);

	}

	/**
	 * 增加监听事件
	 * */
	public void initListener() {

		// 使用标记改变事件
		tabled.addEventListener(tabled.getTag() + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onTableReduceAmt");

	}

	/**
	 * tabled减免金额监听
	 * */
	public void onTableReduceAmt(TTableNode cell) {
		tabled.acceptText();
		int col = tabled.getSelectedColumn();
		int row = tabled.getSelectedRow();
		String columnName = tabled.getParmMap(col);
		DecimalFormat sfd= new DecimalFormat("######0.00");
		if (columnName.equals("REDUCE_AMT")) {
			double oldValue = TypeTool.getDouble(cell.getOldValue());
			double reduceAmt = TypeTool.getDouble(cell.getValue());
			double arAmt = tabled.getParmValue().getDouble("AR_AMT", row);
			if (reduceAmt > arAmt) {
				this.messageBox("减免金额不能大于发生金额");
				cell.setValue(oldValue);
				return;
			}
			tabled.setItem(row, "TOT_AMT", sfd.format(arAmt-reduceAmt));
		}
	}

	/**
	 *切换页签
	 * */
	public void onSelect() {
		// 获取页签索引
		int selectedIndex = TabbedPane.getSelectedIndex();
		type.setValue("1");
		// 选中第一个页签 总额减免
		if (selectedIndex <= 0) {
			// 清空共用的部分
			this.setValue("OWN_AMT", "");
			this.setValue("REDUCE_AMT", "");
			this.setValue("REDUCE_USER", Operator.getID());
			this.setValue("REDUCE_NOTE", "");
			callFunction("UI|OWN_AMT|setEnabled", false);
			callFunction("UI|REDUCE_AMT|setEnabled", true);
			onSelectM();
			TParm parm = new TParm();
			tabled.setParmValue(parm);
			tabledd.setParmValue(parm);
			return;
		}

		// 选中第二个页签 细项减免
		if (selectedIndex == 1) {
			// 清空共用的部分
			this.setValue("OWN_AMT", "");
			this.setValue("REDUCE_AMT", "");
			this.setValue("REDUCE_USER", Operator.getID());
			this.setValue("REDUCE_NOTE", "");
			this.setValue("TYPE", "1");
			callFunction("UI|OWN_AMT|setEnabled", false);
			callFunction("UI|REDUCE_AMT|setEnabled", false);
			onChange();
			return;
		}
	}

	/**
	 * 总额减免页签查询
	 * */
	public void onSelectM() {
		String sql = "SELECT   'N' FLG,REXP_CODE,SUM(AR_AMT) AR_AMT,0 AS REDUCE_AMT,SUM(AR_AMT) TOT_AMT,'1' DATA_TYPE,REXP_CODE AS ORDER_CODE "
				+ " FROM OPD_ORDER "
				+ "  WHERE CASE_NO = '"
				+ this.getValue("CASE_NO")
				+ "' "
				+ " AND BILL_FLG='N' AND RECEIPT_NO IS NULL"//====yanjing 将BILL_FLG='Y'改为BILL_FLG='N'
				+ " GROUP BY REXP_CODE ORDER BY REXP_CODE";
//		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm selParm = new TParm();
		selParm=getRexpParm(selParm,"1");
		if (selParm.getCount() <= 0) {
			return;
		}
		// 给表格赋值
		callFunction("UI|TABLEM|setParmValue", selParm);
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < selParm.getCount(); i++) {
			BigDecimal sf = new BigDecimal(String.valueOf(selParm.getDouble(
					"AR_AMT", i)));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			sum = sum.add(data);
		}
		this.setValue("OWN_AMT", sum.doubleValue());
		this.setValue("TOT_AMT", sum.doubleValue());
//		this.grabFocus("tButton_0");
	}

	/**
	 * 总额减免分摊方法
	 * */
	public void onShare() {
		int selectedIndex = TabbedPane.getSelectedIndex();
		// 选中第一个页签 总额减免
		if (selectedIndex == 0) {
			double reduceAmt = this.getValueDouble("REDUCE_AMT");
			double arAmt = TypeTool.getDouble(getValue("OWN_AMT"));
			if (reduceAmt < 0) {
				this.messageBox("减免总额不能小于0！");
				this.setValue("REDUCE_AMT", 0.00);
				return;
			}
			if (reduceAmt > arAmt) {
				this.messageBox("减免总额不能大于费用发生总额！");
				this.setValue("REDUCE_AMT", 0.00);
				return;
			}
			DecimalFormat sfd= new DecimalFormat("######0.00");
			BigDecimal sum = new BigDecimal(0);
			BigDecimal reduceAmtBig = new BigDecimal(reduceAmt);
			tablem.acceptText();
			TParm tableParm = tablem.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				double ar_amt = tableParm.getDouble("AR_AMT", i);
				double reduceAmts = (ar_amt / arAmt) * reduceAmt;
				BigDecimal sf = new BigDecimal(String.valueOf(reduceAmts));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				if (i == tableParm.getCount() - 1) {
					data = reduceAmtBig.subtract(sum);
				} else {
					sum = sum.add(data);
				}
				tableParm.setData("REDUCE_AMT", i, sfd.format(data.doubleValue()));
				tableParm.setData("TOT_AMT", i, sfd.format(ar_amt-data.doubleValue()));
			}
			tablem.setParmValue(tableParm);
			this.setValue("TOT_AMT", sfd.format(arAmt-reduceAmt));
		}
	}

	/**
	 * 计算减免总额
	 * */
	public void onSumReduce() {
		this.tabledd.acceptText();
		TParm parm = tabledd.getParmValue();
		if (parm.getCount() <= 0)
			this.setValue("REDUCE_AMT", "");
		BigDecimal sum = new BigDecimal(0);
		for (int i = 0; i < parm.getCount(); i++) {
			BigDecimal sf = new BigDecimal(String.valueOf(parm.getDouble(
					"REDUCE_AMT", i)));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			sum = sum.add(data);
		}
		
		this.setValue("REDUCE_AMT", String.valueOf(sum.doubleValue()));
		this.setValue("TOT_AMT", String.valueOf(this.getValueDouble("OWN_AMT")-sum.doubleValue()));
	}

	/**
	 * 添加
	 * 
	 */
	public void onAdd() {
		tabled.acceptText();
		tabledd.acceptText();
		TParm parmd = tabled.getParmValue();
		if (parmd.getCount() < 0) {
			this.messageBox("无选择数据");
			return;
		}
		int count=0;
		TParm rowParm = new TParm();
		if (tabledd.getParmValue() != null)
			rowParm = tabledd.getParmValue();
		for (int i = 0; i < parmd.getCount(); i++) {
			if ("Y".equals(parmd.getData("FLG", i))) {
				TParm errorParm = this.onCheck(parmd.getRow(i), rowParm);
				if (errorParm.getErrCode() < 0) {
					this.messageBox(errorParm.getErrText());
					return;
				}
				count++;
			}
		}
		if(count<=0){
			this.messageBox("无可添加的数据");
			return;
		}
		for (int i = 0; i < parmd.getCount(); i++) {
			if ("Y".equals(parmd.getData("FLG", i))) {
				// 收据项目
				if (this.getTComboBox("TYPE").getSelectedID().equals("1")) {
					rowParm.addData("FLG", "Y");
					rowParm.addData("DATA_DESC", rexpMap.get(parmd.getData(
							"REXP_CODE", i)));
					rowParm.addData("SPECIFICATION", "");
					rowParm.addData("UNIT", "");
					rowParm.addData("OWN_PRICE", "");
					rowParm.addData("QTY", "1");
					rowParm.addData("AR_AMT", parmd.getData("AR_AMT", i));
					rowParm.addData("REDUCE_AMT", parmd
							.getData("REDUCE_AMT", i));
					rowParm.addData("TOT_AMT", String.valueOf(parmd.getDouble("AR_AMT", i)-parmd.getDouble("REDUCE_AMT", i)));//实收金额
					rowParm.addData("DATA_CODE", parmd.getData("REXP_CODE", i));
					rowParm.addData("DATA_TYPE", parmd.getData("DATA_TYPE", i));
					rowParm.addData("REXP_CODE", parmd.getData("REXP_CODE", i));

				}
				// 项目明细
				if (this.getTComboBox("TYPE").getSelectedID().equals("2")) {
					rowParm.addData("FLG", "Y");
					rowParm
							.addData("DATA_DESC", parmd
									.getData("ORDER_DESC", i));
					rowParm.addData("SPECIFICATION", parmd.getData(
							"SPECIFICATION", i));
					rowParm.addData("UNIT", parmd.getData("DOSAGE_UNIT", i));
					rowParm.addData("OWN_PRICE", parmd.getData("OWN_PRICE", i));
					rowParm.addData("QTY", parmd.getData("DOSAGE_QTY", i));
					rowParm.addData("AR_AMT", parmd.getData("AR_AMT", i));
					rowParm.addData("REDUCE_AMT", parmd
							.getData("REDUCE_AMT", i));
					rowParm
							.addData("DATA_CODE", parmd
									.getData("ORDER_CODE", i));
					rowParm.addData("TOT_AMT", String.valueOf(parmd.getDouble("AR_AMT", i)-parmd.getDouble("REDUCE_AMT", i)));//实收金额
					rowParm.addData("DATA_TYPE", parmd.getData("DATA_TYPE", i));
					rowParm.addData("REXP_CODE", parmd.getData("REXP_CODE", i));
				}
			}
		}
		rowParm.setCount(rowParm.getCount("DATA_CODE"));
		tabledd.setParmValue(rowParm);
		// 计算减免总金额
		onSumReduce();

	}

	/**
	 * 校验数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCheck(TParm parm, TParm tableParm) {
		this.tabledd.acceptText();
		TParm result = new TParm();
		String dataType = parm.getValue("DATA_TYPE");
		String dataCode = parm.getValue("ORDER_CODE");
		String rexpCode = parm.getValue("REXP_CODE");
		BigDecimal sum = new BigDecimal(parm.getDouble("REDUCE_AMT"));
		for (int i = 0; i < tableParm.getCount(); i++) {
			TParm parmRow = tableParm.getRow(i);
			if (dataType.equals("2")
					&& dataCode.equals(parmRow.getValue("DATA_CODE"))) {// 明细减免
				BigDecimal sf = new BigDecimal(String.valueOf(parmRow
						.getDouble("REDUCE_AMT")));
				BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
				sum = sum.add(data);
			}
		}
		if (dataType.equals("2")) {
			result = this.getOrderCodeSumAmt(parm, sum.doubleValue());
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		TParm parmValue = tabledd.getParmValue();
		if (parmValue == null) {
			return result;
		}
		if (parmValue.getCount() <= 0)
			return result;
		for (int i = 0; i < parmValue.getCount(); i++) {
			TParm parmRow = parmValue.getRow(i);
			if (parmRow.getValue("DATA_TYPE").equals(dataType)
					&& parmRow.getValue("DATA_CODE").equals(dataCode)) {
				result.setErr(-1, parmRow.getValue("DATA_DESC") + "已减免,请重新操作！");
				return result;
			}
			if ((!parmRow.getValue("DATA_TYPE").equals(dataType))
					&& parmRow.getValue("REXP_CODE").equals(rexpCode)) {
				result.setErr(-1, parmRow.getValue("DATA_DESC") + "已减免,请重新操作！");
				return result;
			}
		}
		return result;
	}

	/**
	 * 计算减免项目的总金额
	 * 
	 * @param parm
	 */
	public TParm getOrderCodeSumAmt(TParm parm, double reduceAmt) {
		TParm result = new TParm();
		if (parm.getValue("SETMAIN_FLG").equals("Y")) {
			String sql = "SELECT SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND ORDERSET_CODE='"
					+ parm.getValue("ORDER_CODE") + "' AND BILL_FLG='N' AND RECEIPT_NO IS NULL GROUP BY ORDERSET_CODE";
			TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
			if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
				result.setErr(-1, "医嘱:" + parm.getValue("ORDER_DESC")
						+ "总额减免后为负值,请重新填写减免金额！");
				return result;
			}
		} else {
			String sql = "SELECT SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
					+ this.caseNo
					+ "' AND ORDER_CODE='"
					+ parm.getValue("ORDER_CODE") + "' AND BILL_FLG='N' AND RECEIPT_NO IS NULL GROUP BY ORDER_CODE";
			TParm Reparm = new TParm(TJDODBTool.getInstance().select(sql));
			if (Reparm.getDouble("AR_AMT", 0) - reduceAmt < 0) {
				result.setErr(-1, "医嘱:" + parm.getValue("ORDER_DESC")
						+ "总额减免后为负值,请重新填写减免金额！");
				return result;
			}
		}
		return result;
	}

	/**
	 * 移除
	 * */
	public void onDelete() {
		tabledd.acceptText();
		TParm parmdd = tabledd.getParmValue();
		if (tabledd.getRowCount() <=0) {
			this.messageBox("没有可移除的数据");
			return;
		}
		int result = this.messageBox("移除确认", "确定要移除吗？", 1);
		// 确认删除
		if (result == 0) {
			for (int i = tabledd.getRowCount() - 1; i >= 0; i--) {
				if ("Y".equals(parmdd.getData("FLG", i))) {
					// 移除
					tabledd.removeRow(i);
				}
			}
		} else {
			return;
		}
		// 计算减免总额
		onSumReduce();

	}
	/**
	 * 
	* @Title: getRexpParm
	* @Description: TODO(收费类别减免，重新统计数据)
	* @author pangben 2016-1-18
	* @param selParm
	* @return
	* @throws
	 */
	private TParm getRexpParm(TParm selParm,String dataType){
		String rexpCode="";
		int index=0;
		//将数据根据REXP_CODE汇总==pangben 2016-1-18
		for (int i = 0; i < initTableParm.getCount("REXP_CODE"); i++) {
			rexpCode=initTableParm.getValue("REXP_CODE",i);
			if (selParm.getCount("REXP_CODE")<=0) {
				selParm.setData("REXP_CODE",index, rexpCode);
				selParm.setData("ORDER_CODE",index, rexpCode);
				selParm.setData("FLG",index, "N");
				selParm.setData("DATA_TYPE",index, dataType);
				selParm.setData("AR_AMT",index,StringTool.round(initTableParm.getDouble("AR_AMT",i),2));
				selParm.setData("REDUCE_AMT",index, 0);
				selParm.setData("TOT_AMT",index,StringTool.round(initTableParm.getDouble("AR_AMT",i),2));
				index++;
			}else{
				boolean flg=false;
				for (int j = 0; j < selParm.getCount("REXP_CODE"); j++) {
					if (rexpCode.equals(selParm.getValue("REXP_CODE",j))) {
						selParm.setData("AR_AMT",j,StringTool.round(selParm.getDouble("AR_AMT",j)+initTableParm.getDouble("AR_AMT",i), 2));
						selParm.setData("REDUCE_AMT",j, 0);
						selParm.setData("TOT_AMT",j,StringTool.round(selParm.getDouble("TOT_AMT",j)+initTableParm.getDouble("AR_AMT",i),2));
						flg=false;
						break;
					}else{
						flg=true;
					}
				}
				if (flg) {
					selParm.setData("REXP_CODE",index, rexpCode);
					selParm.setData("ORDER_CODE",index, rexpCode);
					selParm.setData("DATA_TYPE",index, dataType);
					selParm.setData("AR_AMT",index,StringTool.round(initTableParm.getDouble("AR_AMT",i),2));
					selParm.setData("REDUCE_AMT",index, 0);
					selParm.setData("FLG",index, "N");
					selParm.setData("TOT_AMT",index,StringTool.round(initTableParm.getDouble("AR_AMT",i),2));
					index++;
				}
			}
		}
		selParm.setCount(selParm.getCount("REXP_CODE"));
		return selParm;
	}
	/**
	 * combox事件
	 * */
	public void onChange() {
		if(initTableParm.getCount()>0){
			for(int i = 0;i<initTableParm.getCount();i++){
				initTableParm.setData("DATA_TYPE", i, type.getValue()) ;
			}
		}
		if ("1".equals(type.getValue())) {

			tabled
					.setHeader("选,30,boolean;收据类别,150,REXP_CODE;发生金额,100;减免金额,100,double,#########0.00;实收金额,100,double,#########0.00");
			tabled.setLockColumns("1,2,4");
			tabled.setParmMap("FLG;REXP_CODE;AR_AMT;REDUCE_AMT;TOT_AMT;ORDER_CODE");
			tabled.setItem("REXP_CODE");
			String sql = "SELECT   'N' FLG,REXP_CODE,SUM(AR_AMT) AR_AMT,0 AS REDUCE_AMT,SUM(AR_AMT) TOT_AMT,'1' DATA_TYPE,REXP_CODE AS ORDER_CODE "
					+ " FROM OPD_ORDER "
					+ "  WHERE CASE_NO = '"
					+ this.getValue("CASE_NO")
					+ "' "
					+ " AND BILL_FLG='N' AND RECEIPT_NO IS NULL"//====yanjing 20141128将BILL_FLG='Y'改成BILL_FLG='N'
					+ " GROUP BY REXP_CODE ORDER BY REXP_CODE";
			
//			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			TParm selParm = new TParm();
			selParm=getRexpParm(selParm,"1");
			if (selParm.getCount() <= 0) {
				return;
			}
			// 给表格赋值
			// FLG;CHARGE_HOSP_DESC;AR_AMT;REDUCE_AMT;HEXP_CODE
			tabled.setParmValue(selParm);
			//callFunction("UI|TABLED|setParmValue", selParm);
			tabled.setColumnHorizontalAlignmentData("1,left;2,right;3,right;4,right");
			double sum = 0;
			for (int i = 0; i < selParm.getCount(); i++) {
				sum += selParm.getDouble("AR_AMT", i);
			}
			this.setValue("OWN_AMT", sum);
			this.setValue("TOT_AMT", sum-this.getValueDouble("REDUCE_AMT"));
		}
		// 费用明细
		if ("2".equals(type.getValue())) {
			tabled
					.setHeader("选,30,boolean;数据名称,150;规格,80;单位,50,UNIT;单价,60;数量,40;发生金额,100;减免金额,100,double,#########0.00;实收金额,100,double,#########0.00");
			tabled.setLockColumns("1,2,3,4,5,6,8");
			tabled.setItem("UNIT");
			tabled
					.setParmMap("FLG;ORDER_DESC;SPECIFICATION;DOSAGE_UNIT;OWN_PRICE;DOSAGE_QTY;AR_AMT;REDUCE_AMT;TOT_AMT;ORDER_CODE;REXP_CODE");
			// 查询费用明细
			String sql = "SELECT 'N' AS FLG, ORDER_CODE, ORDER_DESC, SPECIFICATION, OWN_PRICE, SUM(AR_AMT) AS AR_AMT, "
					+ " 0 AS REDUCE_AMT,SUM(AR_AMT) AS TOT_AMT,SUM(DOSAGE_QTY) AS DOSAGE_QTY, DOSAGE_UNIT,REXP_CODE,'2' DATA_TYPE,SETMAIN_FLG"
					+ " FROM OPD_ORDER "
					+ " WHERE CASE_NO = '"
					+ this.getValue("CASE_NO")
					+ "'  AND BILL_FLG='N' AND RECEIPT_NO IS NULL " +//====yanjing 20141128将BILL_FLG='Y'改成BILL_FLG='N'
							"AND (HIDE_FLG='N' OR HIDE_FLG IS NULL) GROUP BY REXP_CODE,ORDER_CODE," +
							"OWN_PRICE,DOSAGE_UNIT,SPECIFICATION,ORDER_DESC,SETMAIN_FLG ORDER BY ORDER_CODE";
//			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			TParm selParm = initTableParm;
			if (selParm.getCount() < 0) {
				return;
			}
			for (int i = 0; i < selParm.getCount(); i++) {
				selParm.setData("DATA_TYPE",i,"2");
			}
			//DecimalFormat sfd= new DecimalFormat("######0.00");
			// 给表格赋值
			tabled
					.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,right;5,right;6,right;7,right;;8,right");
			double sum = 0;
			for (int i = 0; i < selParm.getCount(); i++) {
//				if (selParm.getValue("SETMAIN_FLG", i).equals("Y")) {
//					TParm parmRow = this.getOrderSetAmt(selParm.getRow(i));
//					selParm.setData("OWN_PRICE", i,sfd.format(parmRow
//							.getDouble("OWN_PRICE")/selParm.getDouble("DOSAGE_QTY",i)));
//					selParm.setData("AR_AMT", i, parmRow.getDouble("AR_AMT"));
//					selParm.setData("TOT_AMT", i, parmRow.getDouble("AR_AMT"));
//				}
				sum += selParm.getDouble("AR_AMT", i);
			}
			this.setValue("OWN_AMT", sum);
			this.setValue("TOT_AMT", sum-this.getValueDouble("REDUCE_AMT"));
			callFunction("UI|TABLED|setParmValue", selParm);
		}
	}

	/**
	 * 集合医嘱金额数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOrderSetAmt(TParm parm) {
		String orderSetCode = parm.getValue("ORDER_CODE");
		String orderSetGroupNo = parm.getValue("ORDERSET_GROUP_NO");
		//String orderSetGroupNo = parm.getValue("ORDERSET_GROUP_NO");
		//String rxNo = parm.getValue("RX_NO");
		String sql = "SELECT SUM(OWN_PRICE) AS OWN_PRICE ,SUM(AR_AMT) AR_AMT,SUM(AR_AMT) TOT_AMT" + " FROM OPD_ORDER "
				+ " WHERE CASE_NO = '"+caseNo+"' AND ORDERSET_CODE='" + orderSetCode+"'  " +
						"AND SETMAIN_FLG='N' AND BILL_FLG='N' AND RECEIPT_NO IS NULL";//===yanjing 20141201 将BILL_FLG='Y'改为BILL_FLG='N'
		TParm reparm = new TParm(TJDODBTool.getInstance().select(sql));
		return reparm.getRow(0);
	}

	/**
	 * 打印
	 * */
	public void onPrint() {
		TParm result = new TParm();
		result.setData("REDUCEFLG", "N");

		// 返回给调用界面的数据
		this.setReturnValue(result);
		this.closeWindow();

	}

	/**
	 * 减免打印
	 * */
	public void onReducePrint() {
		if (this.getValueDouble("REDUCE_AMT")<=0) {
			this.messageBox("减免金额小于或等于0，不可以操作打票");
			return;
		}
		if (this.getValueDouble("REDUCE_AMT")>this.getValueDouble("OWN_AMT")) {
			this.messageBox("减免金额不能大于费用总额");
			return;
		}
		String reduce_no = SystemTool.getInstance().getNo("ALL", "OPB",
				"REDUCE_NO", "REDUCE_NO");
		TParm result = new TParm();
		result.setData("REDUCEFLG", "Y");
		result.setData("REDUCE_AMT", this.getValueDouble("REDUCE_AMT"));
		result.setData("REDUCE_NO", reduce_no);
		TParm parmForReduceM = new TParm();
		parmForReduceM.setData("REDUCE_NO", reduce_no);
		parmForReduceM.setData("ADM_TYPE", "O");
		parmForReduceM.setData("CASE_NO", this.getValue("CASE_NO"));
		parmForReduceM.setData("MR_NO", this.getValue("MR_NO"));
		int selectedIndex = TabbedPane.getSelectedIndex();
		if (selectedIndex <= 0) {
			parmForReduceM.setData("REDUCE_TYPE", "1");
		}
		if (selectedIndex == 1) {
			parmForReduceM.setData("REDUCE_TYPE", "2");
		}
		parmForReduceM.setData("AR_AMT", this.getValueDouble("OWN_AMT"));
		parmForReduceM.setData("REDUCE_AMT", this.getValueDouble("REDUCE_AMT"));
		parmForReduceM.setData("REDUCE_USER", this.getValue("REDUCE_USER"));
		parmForReduceM.setData("REDUCE_NOTE", this.getValue("REDUCE_NOTE"));
		parmForReduceM.setData("REDUCE_FLG", "Y");
		parmForReduceM.setData("RESET_USER", "");
		parmForReduceM.setData("RESET_DATE", "");
		parmForReduceM.setData("OPT_USER", Operator.getID());
		parmForReduceM.setData("OPT_TERM", Operator.getIP());
		parmForReduceM.setData("RESET_REDUCE_NO", "");
		parmForReduceM.setData("BUSINESS_NO", "");
		parmForReduceM.setData("ACCOUNT_FLG", "");
		parmForReduceM.setData("ACCOUNT_SEQ", "");
		parmForReduceM.setData("ACCOUNT_USER", "");
		parmForReduceM.setData("ACCOUNT_DATE", "");
		result.setData("parmForReduceM", parmForReduceM.getData());

		TParm parmForReduceD = new TParm();
		int seq = 1;
		if (selectedIndex <= 0) {
			// 总额减免
			int count = tablem.getRowCount();
			for (int i = 0; i < count; i++) {
				parmForReduceD.addData("REDUCE_NO", reduce_no);
				parmForReduceD.addData("SEQ", seq);
				parmForReduceD.addData("REDUCE_TYPE", "1");
				parmForReduceD.addData("DATA_TYPE", "1");
				parmForReduceD.addData("DATA_NAME", this.rexpMap.get(tablem
						.getParmValue().getData("REXP_CODE", i)));
				parmForReduceD.addData("DATA_CODE", tablem.getParmValue()
						.getData("REXP_CODE", i));
				parmForReduceD.addData("DESCRIPTION", "");
				parmForReduceD.addData("UNIT_CODE", "");
				parmForReduceD.addData("OWN_PRICE", "");
				parmForReduceD.addData("QTY", "");
				parmForReduceD.addData("AR_AMT", tablem.getParmValue().getData(
						"AR_AMT", i));
				parmForReduceD.addData("REDUCE_AMT", tablem.getParmValue()
						.getData("REDUCE_AMT", i));
				parmForReduceD.addData("OPT_USER", Operator.getID());
				parmForReduceD.addData("OPT_TERM", Operator.getIP());
				parmForReduceD.addData("RESET_REDUCE_NO", "");
				parmForReduceD.addData("REXP_CODE", tablem.getParmValue()
						.getData("REXP_CODE", i));//收据类别
				seq++;
			}
		}
		if (selectedIndex == 1) {
			// 分项减免
			int count = tabledd.getRowCount();
			for (int i = 0; i < count; i++) {
				// <REDUCE_NO>,<SEQ>,<REDUCE_TYPE>,<DATA_TYPE>,<DATA_CODE>,
				// <DATA_NAME>,<DESCRIPTION>,<UNIT_CODE>,
				// <OWN_PRICE>,<QTY>,
				// <AR_AMT>,<REDUCE_AMT>,<RESET_REDUCE_NO>,<OPT_USER>,SYSDATE,<OPT_TERM>
				parmForReduceD.addData("REDUCE_NO", reduce_no);
				parmForReduceD.addData("SEQ", seq);
				parmForReduceD.addData("REDUCE_TYPE", "2");
				parmForReduceD.addData("DATA_TYPE", tabledd.getParmValue()
						.getData("DATA_TYPE", i));

				// DATA_DESC;SPECIFICATION;UNIT;OWN_PRICE;QTY;AR_AMT;REDUCE_AMT;DATA_CODE
				parmForReduceD.addData("DATA_NAME", tabledd.getParmValue()
						.getData("DATA_DESC", i));
				parmForReduceD.addData("DESCRIPTION", tabledd.getParmValue()
						.getData("SPECIFICATION", i));
				parmForReduceD.addData("UNIT_CODE", tabledd.getParmValue()
						.getData("UNIT", i));
				parmForReduceD.addData("OWN_PRICE", tabledd.getParmValue()
						.getData("OWN_PRICE", i));
				parmForReduceD.addData("QTY", tabledd.getParmValue().getData(
						"QTY", i));
				parmForReduceD.addData("AR_AMT", tabledd.getParmValue()
						.getData("AR_AMT", i));
				parmForReduceD.addData("REDUCE_AMT", tabledd.getParmValue()
						.getData("REDUCE_AMT", i));
				parmForReduceD.addData("OPT_USER", Operator.getID());
				parmForReduceD.addData("OPT_TERM", Operator.getIP());
				parmForReduceD.addData("DATA_CODE", tabledd.getParmValue()
						.getData("DATA_CODE", i));
				parmForReduceD.addData("REXP_CODE", tabledd.getParmValue()
						.getData("REXP_CODE", i));//收据类别
				parmForReduceD.addData("RESET_REDUCE_NO", "");
				seq++;
			}
		}
		result.setData("parmForReduceD", parmForReduceD.getData());
		TParm parmForEktTrade = new TParm();
		String TradeNo = EKTTool.getInstance().getTradeNo();// 得到医疗卡外部交易号
		parmForEktTrade.setData("TRADE_NO", TradeNo);
		TParm parmMaster = EKTpreDebtTool.getInstance().getEktCardAndAmt(this.getValue("MR_NO").toString());
		parmForEktTrade.setData("CARD_NO", parmMaster.getValue("CARD_NO",0));
		parmForEktTrade.setData("MR_NO", this.getValue("MR_NO"));
		parmForEktTrade.setData("CASE_NO", this.getValue("CASE_NO"));
		parmForEktTrade.setData("PAT_NAME", this.getValue("PAT_NAME"));
		parmForEktTrade.setData("OLD_AMT", parmMaster.getDouble(
				"CURRENT_BALANCE", 0));
		parmForEktTrade.setData("AMT", -getValueDouble("REDUCE_AMT"));
		parmForEktTrade.setData("STATE", "1");
		parmForEktTrade.setData("BUSINESS_TYPE", "OPBR");
		parmForEktTrade.setData("OPT_USER", Operator.getID());
		parmForEktTrade.setData("OPT_TERM", Operator.getIP());
		parmForEktTrade.setData("GREEN_BALANCE", "");
		parmForEktTrade.setData("GREEN_BUSINESS_AMT", "");
		parmForEktTrade.setData("RESET_TRADE_NO", "");
		parmForEktTrade.setData("PAY_OTHER3", "");
		parmForEktTrade.setData("PAY_OTHER4", "");
		result.setData("parmForEktTrade", parmForEktTrade.getData());
		TParm parmForEktMaster = new TParm();
		parmForEktMaster.setData("CARD_NO", parmMaster.getValue("CARD_NO",0));
		parmForEktMaster.setData("CURRENT_BALANCE", parmMaster.getDouble(
				"CURRENT_BALANCE", 0)
				+ this.getValueDouble("REDUCE_AMT"));// 现在医疗卡中剩余的金额
		parmForEktMaster.setData("MR_NO", this.getValue("MR_NO"));// 病案号
		parmForEktMaster.setData("ID_NO", "");
		parmForEktMaster.setData("NAME", this.getValue("PAT_NAME"));
		parmForEktMaster.setData("CREAT_USER", Operator.getID());
		parmForEktMaster.setData("OPT_USER", Operator.getID());
		parmForEktMaster
				.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parmForEktMaster.setData("OPT_TERM", Operator.getIP());
		result.setData("parmForEktMaster", parmForEktMaster.getData());
		result.addData("TOT_AMT", this.getValue("TOT_AMT"));
		this.setReturnValue(result);
		this.closeWindow();

	}

	/**
	 * 得到收据类列
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, String> getRexpMap() {
		Map<String, String> RexpMap = new HashMap<String, String>();
		String sql = "SELECT  ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			String id = parm.getValue("ID", i);
			String chn = parm.getValue("CHN_DESC", i);
			RexpMap.put(id, chn);
		}
		return RexpMap;
	}

	/**
	 * 取消
	 * */
	public void onCancel() {
//		switch (messageBox("提示信息", "确定取消选择,默认打印方式", this.YES_NO_OPTION)) {
//		case 0:
			TParm result = new TParm();
			result.setData("REDUCEFLG", "N");
			this.setReturnValue(result);
			this.closeWindow();
//		case 1:
//			break;
//		}
//		return;
	}

	/**
	 * 拿到TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}
	/**
	 * 传回功能
	 * yanjing 20141128 
	 */
	public void onSend(){
		onReducePrint();
	}
}
