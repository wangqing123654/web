package jdo.bil;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatEKTGatherType;
/**
 * <p>
 * Title:支付工具
 * </p>
 * 
 * <p>
 * Description:门诊票据减免:现金打票减免使用
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
 * @author pangben
 * @version 1.0
 */
public class PaymentCashTool {

	public final static String TAGTABLE = "PAY_TABLE";
	public final static String TAGTABLE_CARDTYPE = "CARD_TYPE";
	
	public TTable table;
	
	private double amt = 0;
	
	TControl tControl;
	public double getReduceAmt() {
		return reduceAmt;
	}

	public void setReduceAmt(double reduceAmt) {
		this.reduceAmt = reduceAmt;
	}

	private boolean hasAmt = true;
	private double reduceAmt;//减免总金额

	/**
	 * 传入TControl,和支付模块的TPanel
	 * <p>请先在TPanel中添加TextFormatEKTGatherType控件(路径在其他),并设为可见,tag随意</p>
	 * @param p
	 * @param tControl
	 */
	public PaymentCashTool(TPanel p, TControl tControl,TParm parm) throws Exception {
		this.tControl = tControl;
		//reduceAmt=parm.getDouble("REDUCE_AMT");
		String tag = "";
		Component[] cs = p.getComponents();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i] instanceof TextFormatEKTGatherType){
				TTextFormat gatherType = (TTextFormat) cs[i];
				
				tag = gatherType.getTag();
				gatherType.setX(-50);
				gatherType.setY(-50);
			}
		}
		
		int h = p.getHeight();
		int w = p.getWidth();
		
		table = new TTable();
		table.setConfigParm(this.tControl.getConfigParm());
		table.setBounds(10, 20, w-20, h-30);
		table.setRowHeight(20);
		//table.setLockColumns("0,1");
		table.setAutoHeight(true);
		table.setAutoWidth(true);
		table.setHeader("支付方式,80," + tag + ";支付金额,80,double,########0.00;减免金额,80,double,########0.00");
		table.setParmMap("PAY_TYPE;AMT;REDUCE_AMT");
		table.setItem(tag);
		table.setTag(TAGTABLE);
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right");
		table.setFocusIndexJump(true);
		table.setFocusIndexList("2");
		
//		TComboBox box = new TComboBox();
//		box.onInit();
//		box.init(this.tControl.getConfigParm());
//		box.initListeners();
//		box.setTableShowList("name");
//		box.setParmMap("id:ID;name:NAME");
//		box.setShowName(true);
		//String sql = "SELECT id,CHN_DESC name from sys_dictionary where group_id = 'SYS_CARDTYPE'";
		//TParm v = new TParm(TJDODBTool.getInstance().select(sql));
		//box.setParmValue(v);
		//table.addItem(TAGTABLE_CARDTYPE, box);
		TParm pValue=new TParm();
		
//		pValue.addData("PAY_TYPE", "");
//		pValue.addData("AMT", 0.00);
//		pValue.addData("REDUCE_AMT", 0.00);
//		parm.addData("REMARKS", "");
		//double reduceAmt=0.00;
		if (parm!=null) {
			if (parm.getCount("PAY_TYPE")>0) {
				pValue.addData("PAY_TYPE", parm.getValue("PAY_TYPE", 0));
				pValue.addData("AMT", 0.00);
				pValue.addData("REDUCE_AMT", 0.00);
				//parm.addData("REMARKS", "");
				//double reduceAmt=0.00;
			}
			for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
				if (parm.getValue("PAY_TYPE", i).equals("JM")) {//减免金额不写入
					continue;
				}
				int key=hasKey(pValue, parm.getValue("PAY_TYPE", i));
				if (key==-1) {
					pValue.addData("PAY_TYPE", parm.getValue("PAY_TYPE", i));
					pValue.addData("AMT", parm.getDouble("AMT", i));
					pValue.addData("REDUCE_AMT", 0.00);
				}else{
					pValue.setData("PAY_TYPE", key, parm.getValue("PAY_TYPE", i));
					pValue.setData("AMT",key, pValue.getDouble("AMT", key) + parm.getDouble("AMT", i));
					pValue.setData("REDUCE_AMT",key, 0.00);
				}
			}
		}
		pValue.setCount(pValue.getCount("PAY_TYPE"));
		table.setParmValue(pValue);
		p.add(table);
		table.addEventListener(TAGTABLE + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
		table.onInit();
		
	}
	
	public void onTableChangeValue (TTableNode tNode) throws Exception {
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);
		int row = tNode.getRow();
		if (reduceAmt<=0) {
			tControl.messageBox("减免金额不正确,不可以操作");
			return;
		}
		int rowCount = table.getRowCount();
		if ("AMT".equals(colName)) {
			String dStr = tNode.getOldValue()+"";
			if(dStr.length() == 0){
				dStr = "0";
			}
			double d = getParmAmt(Double.valueOf(dStr));
			boolean x = (d + Double.valueOf(""+tNode.getValue())) * amt >= 0;
			if(x){
				if(Math.abs(d + Double.valueOf(""+tNode.getValue())) > Math.abs(amt)){
					tNode.setValue(StringTool.round(amt - d, 2));
					tControl.messageBox("支付金额超出总金额");
					return;
				}
			}else{
				tControl.messageBox("请正确输入金额的正负");
				tNode.setValue(0);
				return;
			}
		}
		if(hasAmt && "REDUCE_AMT".equals(colName)){
			String dStr = tNode.getOldValue()+"";
			if(dStr.length() == 0){
				dStr = "0";
			}
			double d = getParmReduceAmt(Double.valueOf(dStr));
			boolean x = Double.valueOf(""+tNode.getValue()) >= 0;
			if(x){
				if (Double.valueOf(""+tNode.getValue())>reduceAmt) {
					tControl.messageBox("此支付方式的减免金额超出总减免金额");
					tNode.setValue(StringTool.round(reduceAmt - d, 2));
					return;
				}
				if(Double.valueOf(""+tNode.getValue())>table.getParmValue().getDouble("AMT",row)){
					tNode.setValue(0.00);
					tControl.messageBox("超出支付金额");
					return;
				}
				if(Math.abs(d + Double.valueOf(""+tNode.getValue())) > Math.abs(reduceAmt)){
					tNode.setValue(StringTool.round(reduceAmt - d, 2));
					tControl.messageBox("减免金额超出总减免金额");
					return;
				}
			}else{
				tControl.messageBox("请正确输入金额的正负");
				tNode.setValue(0);
				return;
			}
		}
		table.acceptText();
		TParm parm = table.getParmValue();
		if(("AMT".equals(colName) || "REDUCE_AMT".equals(colName)) && row == rowCount - 1 && parm.getValue("PAY_TYPE", row).length() != 0){
			table.addRow();
		}
	}
	
	private double getParmReduceAmt(double oldAmt){
		table.acceptText();
		TParm parm = table.getParmValue();
		double d = 0;
		for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
			d += parm.getDouble("REDUCE_AMT", i);
		}
		return d - oldAmt;
	}
	private double getParmAmt(double oldAmt){
		table.acceptText();
		TParm parm = table.getParmValue();
		double d = 0;
		for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
			d += parm.getDouble("AMT", i);
		}
		return d - oldAmt;
	}
	public void getAmts(){
		table.acceptText();
		TParm parm = table.getParmValue();
		for (int i = 0; i <parm.getCount(); i++) {
			
			parm.setData("REDUCE_AMT",i,0);
		}
		double reAmt=reduceAmt;
		for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
			if (reAmt>0) {
				if (parm.getDouble("AMT", i)>=reAmt) {
					parm.setData("REDUCE_AMT",i,reAmt);
					reAmt=0;
					break;
				}else{
					reAmt=reAmt-parm.getDouble("AMT", i);
					parm.setData("REDUCE_AMT",i,parm.getDouble("AMT", i));
				}	
			}
		}
		table.setParmValue(parm);
	}
	
	
	@SuppressWarnings("unchecked")
	private static int hasKey(TParm parm, String key){
		Vector<String> v = (Vector<String>) parm.getData("PAY_TYPE");
		if(v.contains(key)){
			return v.indexOf(key);
		}
		return -1;
	}
	
	
	private Map<String, String> getPayTypeMap() throws Exception {
		String sql =
			"SELECT * FROM BIL_GATHERTYPE_PAYTYPE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < parm.getCount(); i++) {
			map.put(parm.getValue("PAYTYPE", i), parm.getValue("GATHER_TYPE", i));
		}
		return map;
	}
	/**
	 * 总金额
	 * @param amt
	 */
	public void setAmt(double amt) {
		this.amt = amt;
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		amt = 0;
		TParm ps = new TParm();
		ps.addData("PAY_TYPE", "C0");
		ps.addData("AMT", "0");
//		ps.addData("CARD_TYPE", "");
//		ps.addData("REMARKS", "");
		table.setParmValue(ps);
	}

	/**
	 * 是否有总金额
	 * @return
	 */
	public boolean isHasAmt() {
		return hasAmt;
	}

	/**
	 * 设置是否有总金额
	 * @param hasAmt
	 */
	public void setHasAmt(boolean hasAmt) {
		this.hasAmt = hasAmt;
	}
}
