package jdo.bil;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * 支付工具
 * @author zhangp
 *
 */
public class PaymentTool {
	
	public final static String TAGTABLE = "PAY_TABLE";
	public final static String TAGTABLE_CARDTYPE = "CARD_TYPE";
	
	public TTable table;
	
	private double amt = 0;
	
	TControl tControl;
	
	private boolean hasAmt = true;
	
	private final String SQL = "SELECT * FROM BIL_GATHERTYPE_PAYTYPE";
	
	TParm bilParm;
	
	Map<String, String> pm;
	
	List<String> list=new ArrayList<String>();
	
	/**
	 * 传入TControl,和支付模块的TPanel
	 * <p>请先在TPanel中添加TextFormatEKTGatherType控件(路径在其他),并设为可见,tag随意</p>
	 * @param p
	 * @param tControl
	 */
	public PaymentTool(TPanel p, TControl tControl) throws Exception {
		this.tControl = tControl;
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
		table.setLockColumns("");
		table.setAutoHeight(true);
		table.setAutoWidth(true);
		table.setHeader("支付方式,80," + tag + ";支付金额,70,double,########0.00;卡类型,80," + TAGTABLE_CARDTYPE + ";备注,100");
		table.setParmMap("PAY_TYPE;AMT;CARD_TYPE;REMARKS");
		table.setItem(tag);
		table.setTag(TAGTABLE);
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,left;3,left");
		table.setFocusIndexJump(true);
		table.setFocusIndexList("0,1,2,3");
		
		TComboBox box = new TComboBox();
		box.onInit();
		box.init(this.tControl.getConfigParm());
		box.initListeners();
		box.setTableShowList("name");
		box.setParmMap("id:ID;name:NAME");
		box.setShowName(true);
		String sql = "SELECT id,CHN_DESC name from sys_dictionary where group_id = 'SYS_CARDTYPE'";
		TParm v = new TParm(TJDODBTool.getInstance().select(sql));
		box.setParmValue(v);
		table.addItem(TAGTABLE_CARDTYPE, box);
		
		TParm parm = new TParm();
		parm.addData("PAY_TYPE", "C0");
		parm.addData("AMT", "0.00");
		parm.addData("CARD_TYPE", "");
		parm.addData("REMARKS", "");
		table.setParmValue(parm);
		
		p.add(table);
		table.addEventListener(TAGTABLE + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onTableChangeValue");
		table.onInit();
		
		bilParm = new TParm(TJDODBTool.getInstance().select(SQL));
		
		pm = new HashMap<String, String>();
		for (int i = 0; i < bilParm.getCount(); i++) {
			if(bilParm.getValue("SON_ID", i).length() > 0){
				pm.put(bilParm.getValue("GATHER_TYPE", i), bilParm.getValue("SON_ID", i));
			}
		}
	}
	
	public void onTableChangeValue (TTableNode tNode) throws Exception {
		table.acceptText();
		TParm tParm = table.getParmValue();
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);
		int row = tNode.getRow();
		int rowCount = table.getRowCount();
		if(hasAmt && "AMT".equals(colName)){
			if(tParm.getValue("PAY_TYPE", row).length() == 0){
				tNode.setValue(tNode.getOldValue());
				return;
			}
			String dStr = tNode.getOldValue()+"";
			if(dStr.length() == 0){
				dStr = "0";
			}
			double d = getParmAmt(Double.valueOf(dStr));
			boolean x = (d + Double.valueOf(""+tNode.getValue())) * amt >= 0;
			if(x){
				boolean y = false;
				y = pm.containsKey(tParm.getValue("PAY_TYPE", row));
				if(Math.abs(d + Double.valueOf(""+tNode.getValue())) > Math.abs(amt)){
					if(y){
						Set<String> set = pm.keySet();
						Iterator<String> it = set.iterator();
						String key;
						String son;
						while (it.hasNext()) {
							key = it.next();
							if(tParm.getValue("PAY_TYPE", row).equals(key)){
								double cp = 0;
								for (int i = 0; i < rowCount; i++) {
									if(tParm.getValue("PAY_TYPE", i).equals(key)){
										cp += tParm.getDouble("AMT", i);
									}
								}
								if(cp + Double.valueOf(""+tNode.getValue()) >= (d + Double.valueOf(""+tNode.getValue()) - amt)){
									son = pm.get(key);
									table.addRow();
									table.setItem(table.getRowCount() - 1, "PAY_TYPE", son);
									table.setItem(table.getRowCount() - 1, "AMT", -(d + Double.valueOf(""+tNode.getValue()) - amt));
								}else{
									tControl.messageBox("超出可找零金额");
									return;
								}
							}
						}
					}else{
						DecimalFormat df = new DecimalFormat("##########0.00");
						if(tParm.getValue("PAY_TYPE", row).equals("C0")){
							tControl.messageBox("找零"+(Double.parseDouble(tNode.getValue().toString())-amt)+"元");
							double v = StringTool.round(amt - d, 2);
							tNode.setValue(df.format(v));
							return;
						}
						double v = StringTool.round(amt - d, 2);
						//DecimalFormat df = new DecimalFormat("##########0.00");
						tNode.setValue(df.format(v));
						tControl.messageBox("超出应收金额");
						return;
					}
				}
			}else{ 
				tControl.messageBox("请正确输入金额的正负");
				tNode.setValue("0.00");
				return;
			}
		}
		if("PAY_TYPE".equals(colName)){
		/*	if(tParm.getDouble("AMT", row) != 0){
				tNode.setValue(tNode.getOldValue());
				tControl.messageBox("改行金额为0才可修改");
				return;
			}*/
			tParm.setData("AMT", row,0.00);
			table.setValueAt("0.00",row,1);
		}
		table.acceptText();
		if(("AMT".equals(colName) || "REMARKS".equals(colName)) && row == rowCount - 1 && tParm.getValue("PAY_TYPE", row).length() != 0){
			table.addRow();
			table.setItem(table.getRowCount() - 1, "AMT", "0.00");
		}
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
	
	public TParm getAmts() throws Exception {
		table.acceptText();
		TParm parm1 = table.getParmValue();
		double d = getParmAmt(0);
		
		TParm parm = new TParm();
		for (int i = 0; i < parm1.getCount("PAY_TYPE"); i++) {
			parm.addData("PAY_TYPE", parm1.getData("PAY_TYPE", i));
			parm.addData("AMT", parm1.getData("AMT", i));
			parm.addData("CARD_TYPE", parm1.getData("CARD_TYPE", i));
			parm.addData("REMARKS", parm1.getData("REMARKS", i));
		}
		
		if(hasAmt && StringTool.round(Math.abs(d), 2) < StringTool.round(Math.abs(amt), 2)){
			throw new Exception("低于应收金额");
		}
		if(hasAmt && StringTool.round(Math.abs(d), 2) > StringTool.round(Math.abs(amt), 2)){
			throw new Exception("高于应收金额");
		}
		for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
			if(parm.getValue("PAY_TYPE", i).length() == 0 || parm.getDouble("AMT", i) == 0){
				parm.removeRow(i);
			}
		}
		Map<String, String> map = getPayTypeMap();
		for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
			parm.setData("PAY_TYPE", i, map.get(parm.getValue("PAY_TYPE", i)));
		}
		
		TParm p = new TParm();
		p.addData("PAY_TYPE", "");
		p.addData("AMT", "");
		p.addData("CARD_TYPE", "");
		p.addData("REMARKS", "");
		
		for (int i = 0; i < parm.getCount("PAY_TYPE"); i++) {
			int q = hasKey(p, parm.getValue("PAY_TYPE", i));
			if(q == -1){
				p.addData("PAY_TYPE", parm.getValue("PAY_TYPE", i));
				p.addData("AMT", parm.getDouble("AMT", i));
				p.addData("CARD_TYPE", parm.getValue("CARD_TYPE", i));
				p.addData("REMARKS", parm.getValue("REMARKS", i));
			}else{
				p.setData("PAY_TYPE", q, parm.getValue("PAY_TYPE", i));
				p.setData("AMT", q, p.getDouble("AMT", q) + parm.getDouble("AMT", i));
				p.setData("CARD_TYPE", q, p.getValue("CARD_TYPE", q) + ";" + parm.getValue("CARD_TYPE", i));
				p.setData("REMARKS", q, p.getValue("REMARKS", q) + ";" + parm.getValue("REMARKS", i));
			}
		}
		
		p.removeRow(0);
		return p;
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
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < bilParm.getCount(); i++) {
			map.put(bilParm.getValue("GATHER_TYPE", i), bilParm.getValue("PAYTYPE", i));
		}
		return map;
	}
	
	/**
	 * 总金额
	 * @param amt
	 */
	public void setAmt(double amt) {
		this.amt = StringTool.round(amt, 2);
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		amt = 0;
		TParm ps = new TParm();
		ps.addData("PAY_TYPE", "C0");
		ps.addData("AMT", "0.00");
		ps.addData("CARD_TYPE", "");
		ps.addData("REMARKS", "");
		table.setParmValue(ps);
	}
	/**
	 * 支付方式校验是否填写交易号
	 * @return
	 */
	public boolean onChecked(TParm tableParm){
		if(tableParm.getValue("PAY_TYPE").equals("WX")||tableParm.getValue("PAY_TYPE").equals("ZFB")){
			if(tableParm.getValue("REMARKS").length()==0||tableParm.getValue("REMARKS")==null){
				return false;
			}
		}
		return true;
	}
	/**
	 * 支付方式重复校验
	 * @return
	 */
	public boolean onCheckPayType(TParm tableParm){
		for(int i=0;i<tableParm.getCount("PAY_TYPE");i++){
			for(int j=i+1;j<tableParm.getCount("PAY_TYPE");j++){
				if(tableParm.getValue("PAY_TYPE",i).equals(tableParm.getValue("PAY_TYPE",j))){
					return false;
				}
			}
		}
	
		return true;
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
