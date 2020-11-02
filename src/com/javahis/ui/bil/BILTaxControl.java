package com.javahis.ui.bil;

import java.sql.Timestamp;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title:发票打印</p>
 *
 * <p>Description:发票打印 </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author zhangp
 * @version 1.0
 */
public class BILTaxControl extends TControl {

	private final String OPBRECP = "com.javahis.ui.bil.OpbRecpImpl";
	private final String URL = "com.javahis.ui.bil.";
	private IRecpType recp;
	private TTable recpTable;
	private TCheckBox all;
	private TRadioButton unCompelet, compelet;
	private TParm saveParmInvNo = new TParm();
	
	/**
	 * 初始化
	 */
	public void onInit() {
		try {
			recp = (IRecpType) Class.forName(OPBRECP).newInstance();
			recpTable = (TTable) getComponent("RECP_TABLE");
			// 值改变事件
			recpTable.addEventListener("RECP_TABLE->"+TTableEvent.CHANGE_VALUE, this,
					"onTableChangeValue");
			all = (TCheckBox) getComponent("tCheckBox_1");
			unCompelet = (TRadioButton) getComponent("tRadioButton_5");
			compelet = (TRadioButton) getComponent("tRadioButton_6");
			initForm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化界面值
	 * @throws Exception
	 */
	private void initForm() throws Exception{
		recpTable.setHeader(recp.getHeader());
		recpTable.setParmMap(recp.getParmMap());
		recpTable.setColumnHorizontalAlignmentData(recp.getColumnHorizontalAlignmentData());
		recpTable.setLockColumns(recp.getLockColumns());
		Timestamp today = SystemTool.getInstance().getDate();
		setValue("RECP_ST_DATE", today);
		setValue("RECP_ED_DATE", today);
		setValue("TAX_ST_DATE", today);
		setValue("TAX_ED_DATE", today);
		onSel();
	}
	
	/**
	 * 查询
	 * @throws Exception
	 */
	public void onQuery() throws Exception {
		all.setSelected(false);
    	String recpStDate = this.getValueString("RECP_ST_DATE");
    	if(recpStDate.length() == 0){
    		messageBox("时间不能为空");
    	}
    	String recpEdDate = this.getValueString("RECP_ED_DATE");
    	if(recpEdDate.length() == 0){
    		messageBox("时间不能为空");
    	}
    	String taxStDate = this.getValueString("TAX_ST_DATE");
    	if(taxStDate.length() == 0){
    		messageBox("时间不能为空");
    	}
    	String taxEdDate = this.getValueString("TAX_ED_DATE");
    	if(taxEdDate.length() == 0){
    		messageBox("时间不能为空");
    	}
    	recpStDate = getDate(recpStDate, true);
    	recpEdDate = getDate(recpEdDate, false);
    	taxStDate = getDate(taxStDate, true);
    	taxEdDate = getDate(taxEdDate, false);
    	String mrNo = getValueString("MR_NO");
    	String patName = getValueString("PAT_NAME");
    	TParm recpParm = new TParm();
    	if(unCompelet.isSelected()){
    		recpParm = recp.onQuery(mrNo, patName, recpStDate, recpEdDate);
    	}
    	if(compelet.isSelected()){
    		recpParm = recp.onQueryCompeleted(mrNo, patName, taxStDate, taxEdDate);
    	}
    	if(recpParm.getErrCode()<0){
    		messageBox(recpParm.getErrText());
    		return;
    	}
    	if(recpParm.getCount() < 0){
    		messageBox("无数据");
    		return;
    	}
    	recpTable.setParmValue(recpParm);
	}
	
	/**
	 * 获得时间
	 * @param date
	 * @param st
	 * @return
	 */
	private String getDate(String date, boolean st){
    	if(date.length() == 0){
    		messageBox("时间不能为空");
    	}
    	date = date.substring(0, 10);
    	date = date.substring(0, 4) + date.substring(5, 7) +
    		date.substring(8, 10);
    	if(st){
    		date += "000000";
    	}else{
    		date += "235959";
    	}
		return date;
	}
	
	/**
	 * 病案号回车
	 */
	public void onMrNo(){
		String mrNo = getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		setValue("MR_NO", pat.getMrNo());
		setValue("PAT_NAME", pat.getName());
	}
	
	/**
	 * 保存
	 * @throws Exception 
	 */
	public void onSave() throws Exception{
		recpTable.acceptText();
		TParm parm = recpTable.getParmValue();
		for (int i = 0; i < parm.getCount("FLG"); i++) {
			if(!parm.getBoolean("FLG", i)){
				parm.removeRow(i);
				i--;
			}
		}
		TParm savParm = recp.onSave(parm, Operator.getID());
		TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
				"onSaveTax", savParm);
		if(result.getErrCode() < 0){
			messageBox("操作失败");
			return;
		}
		messageBox("操作成功");
		onClear();
	}
	
	/**
	 * 保存票据号
	 * @throws Exception 
	 */
	public void onSaveInvNo() throws Exception{
		recpTable.acceptText();

//		System.out.println("saveParm==="+saveParmInvNo);
		TParm savParm = recp.onSaveInvNo(saveParmInvNo);
		TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
				"onSaveTax", savParm);
		if(result.getErrCode() < 0){
			messageBox("保存失败");
			return;
		}
		messageBox("保存成功");
		onClear();
	}
	
	/**
	 * 全选
	 */
	public void onAll(){
		recpTable.acceptText();
		TParm parm = recpTable.getParmValue();
		String flg = "N";
		if(all.isSelected()){
			flg = "Y";
		}
		for (int i = 0; i < parm.getCount("FLG"); i++) {
			parm.setData("FLG", i, flg);
		}
		recpTable.setParmValue(parm);
	}
	
	public void onChangeRecpType(String className) throws Exception{
		recp = (IRecpType) Class.forName(URL + className).newInstance();
		initForm();
//		recpTable.removeRowAll();
		recpTable.setParmValue(new TParm());

	}
	
	/**
	 * 清空
	 * @throws Exception
	 */
	public void onClear() throws Exception{
		saveParmInvNo = new TParm();
		TRadioButton r = (TRadioButton) getComponent("tRadioButton_0");
		r.setSelected(true);
		unCompelet.setSelected(true);
		onChangeRecpType("OpbRecpImpl");	
		this.clearValue("MR_NO;PAT_NAME;TAX_INV_NO");
		onSel();
	}
	
	/**
	 * 读医疗卡
	 * @throws Exception 
	 */
	public void onReadEKT() throws Exception {
		// 读取医疗卡
		TParm parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		this.onMrNo();
		this.onQuery();
	}
	
	/**
	 * 取消完成
	 * @throws Exception 
	 */
	public void onCancle() throws Exception{
		recpTable.acceptText();
		TParm parm = recpTable.getParmValue();
		for (int i = 0; i < parm.getCount("FLG"); i++) {
			if(!parm.getBoolean("FLG", i)){
				parm.removeRow(i);
				i--;
			}
		}
		TParm savParm = recp.onCancle(parm);
		TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
				"onSaveTax", savParm);
		if(result.getErrCode() < 0){
			messageBox("操作失败");
			return;
		}
		messageBox("操作成功");
		onClear();
	}
	
	public void onSel(){
		
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_5"))){
			callFunction("UI|cancel|setEnabled", false);
			callFunction("UI|save|setEnabled", true);
		}
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_6"))){
			callFunction("UI|cancel|setEnabled", true);
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|TAX_INV_NO|setEnabled", false);
		}
		
	}
	
	public void onTableChangeValue(TTableNode tNode) throws Exception{
		recpTable.acceptText();
		int colunm =tNode.getColumn();
		int row = tNode.getRow();
		String[] parmMap = recp.getParmMap().split(";");	
		TParm parm = recpTable.getParmValue();
		
		if("TAX_INV_NO".equals(parmMap[colunm])){
			parm.setData("TAX_INV_NO", row, tNode.getValue());
			saveParmInvNo.addRowData(parm, row);
		}
	
		
	}
	
}
