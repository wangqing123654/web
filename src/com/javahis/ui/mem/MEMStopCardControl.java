package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.mem.MEMSQL;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 会员卡停卡
 * </p>
 * 
 * <p>
 * Description: 会员卡停卡
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
 * @author huangtt 20140424
 * @version 4.5
 */
public class MEMStopCardControl extends TControl {
	private TParm parmEKT; // 医疗卡信息
	private Pat pat; // 病患信息
	private TParm parmMEM; // 会员卡信息
	private TTable table;
	private TTable tableMem;
	private String removeFlg=""; // 停卡标记
	private int selectRow= -1;
	private String memGatherType = "";

	/**
	 * 初始化方法
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
		tableMem = (TTable) getComponent("MEM_TABLE");
		
		//支付方式
		memGatherType = EKTTool.getInstance().getPayTypeDefault();
    	setValue("MEM_GATHER_TYPE", memGatherType);
	}

	/**
	 * 读医疗卡
	 */
	public void onReadEKT() {
		// 读取医疗卡
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		this.onQuery();
	}

	public void onQuery() {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			return;
		}
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		parmMEM = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfoAll(mrNo)));
//		System.out.println("parmMEM==" + parmMEM);
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", pat.getName());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("FIRST_NAME", pat.getFirstName());
		setValue("LAST_NAME", pat.getLastName());
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("MEM_TYPE", parmMEM.getValue("MEM_CODE", 0));
		setValue("START_DATE",
				parmMEM.getValue("START_DATE", 0).equals("") ? "" : parmMEM
						.getValue("START_DATE", 0).replace("-", "/").substring(
								0, 10));
		setValue("END_DATE", parmMEM.getValue("END_DATE", 0).equals("") ? ""
				: parmMEM.getValue("END_DATE", 0).replace("-", "/").substring(
						0, 10));

		getTable();
	}

	public void getTable() {
		// MR_NO;PAT_NAME;MEM_CODE;MEM_FEE;GATHER_TYPE;START_DATE;END_DATE;DESCRIPTION;REMOVE_FLG
		TParm parmTrade = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getRevokeMemInfo(this.getValueString("MR_NO"))));
		TParm parmRevoke = new TParm(TJDODBTool.getInstance().select(MEMSQL.getRevoke(this.getValueString("MR_NO"))));
		for(int i=0;i<parmTrade.getCount();i++){
			String tradeNo = parmTrade.getValue("TRADE_NO", i);
			for(int j=0;j<parmRevoke.getCount();j++){
				if(tradeNo.equals(parmRevoke.getValue("RETURN_TRADE_NO", j))){
					parmTrade.setData("REVOKE_FEE", i, parmRevoke.getValue("MEM_FEE", j));
					parmTrade.setData("DESCRIPTION", i, parmRevoke.getValue("DESCRIPTION", j));
					parmTrade.setData("GATHER_TYPE", i, parmRevoke.getValue("GATHER_TYPE", j));
					parmTrade.setData("STOP_CARD_DESCRIPTION", i, parmRevoke.getValue("STOP_CARD_DESCRIPTION", j));
				}
			}
		}
		table.setParmValue(parmTrade);
	}

	public void onTableClicked() {
		table.acceptText();
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		Timestamp date = SystemTool.getInstance().getDate();
		String endDate = parm.getValue("END_DATE", row);
		removeFlg = parm.getValue("REMOVE_FLG", row);
		String status = parm.getValue("STATUS", row);
		
		tableMem.setParmValue(MEMSQL.getMemType(parm.getRow(row)));
		
		if (endDate.equals("") || status.equals("0")) {
			this.messageBox("该记录还没有售卡，不允许停卡，请重新选择！");
			return;
		}

		if (Timestamp.valueOf(endDate).before(date) && !removeFlg.equals("Y")) {
			this.messageBox("该记录不在有效期内，不允许停卡，重新选择！");
			return;
		}
		selectRow = row;
		if(parm.getBoolean("REMOVE_FLG", row)){
			this.setValue("STOP_REASON", parm.getValue("DESCRIPTION", row));
			this.setValue("STOP_CARD_DESCRIPTION", parm.getValue("STOP_CARD_DESCRIPTION", row));
			this.setValue("MEM_GATHER_TYPE", parm.getValue("GATHER_TYPE", row));
			this.setValue("FEEs", Math.abs(parm.getDouble("REVOKE_FEE", row)));
		}else{
			this.setValue("MEM_GATHER_TYPE", memGatherType);
			this.setValue("FEEs", 0);
			this.setValue("STOP_REASON", "");
			this.setValue("STOP_CARD_DESCRIPTION", "");
		}
		

	}
	
	

	/**
	 * 停卡保存
	 */
	public void onSave() {
		if(selectRow == -1){
			this.messageBox("请选择停卡数据！");
			return;
		}
		
		
		if (removeFlg.equals("Y")) {
			this.messageBox("该记录已停卡，请重新选择！");
			return;
		}
		if (this.getValueString("MEM_GATHER_TYPE").equals("")) {
			this.messageBox("支付方式不允许为空！");
			return;
		}
		
		
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		double feeS = this.getValueDouble("FEEs");
		double memFee =0.00;
		for (int i = 0; i < tableParm.getCount("MEM_CARD_NO"); i++) {
			if(tableParm.getValue("MEM_CARD_NO", selectRow).equals(tableParm.getValue("MEM_CARD_NO", i))){
				memFee += tableParm.getDouble("MEM_FEE", i);
			}
		}
		Timestamp dateNow = SystemTool.getInstance().getDate();
		Timestamp date =StringTool.getTimestamp(tableParm.getValue("START_DATE", selectRow), "yyyyMMddHHmmss") ;
		if(date.after(dateNow)){
			this.messageBox("开始时间大于今天，请选择正确数据");
			return;
		}
		if (this.getValueString("FEEs").equals("") ) {
			this.messageBox("退回剩余会费金额不允许为空！");
			return;
		}
		if (this.getValueString("STOP_REASON").equals("")) {
			this.messageBox("停卡原因不允许为空！");
			return;
		}
		if( feeS <= 0 || feeS >  memFee){
			this.messageBox("退回剩余会费金额不正确");
			return;
		}
		TParm payCashParm=null;
		if("WX".equals(this.getValue("MEM_GATHER_TYPE"))||"ZFB".equals(this.getValue("MEM_GATHER_TYPE"))){
			TParm checkCashTypeParm=new TParm();
			if("WX".equals(this.getValue("MEM_GATHER_TYPE"))){
				checkCashTypeParm.setData("WX_AMT",feeS);
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if("ZFB".equals(this.getValue("MEM_GATHER_TYPE"))){
				checkCashTypeParm.setData("ZFB_AMT", feeS);
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
			if(null!=checkCashTypeParm.getValue("WX_FLG")&&
					checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
					checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
				Object result = this.openDialog(
	    	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", checkCashTypeParm, false);
				if(null==result){
					return ;
				}
				payCashParm=(TParm)result;
			}
			if(null!=payCashParm){
				parm.setData("payCashParm",payCashParm.getData());
			}
		}
		TParm parmMemInfo = new TParm();
		TParm parmMemTrade = new TParm();
		parmMemInfo.setData("MR_NO", this.getValueString("MR_NO"));
		parmMemInfo.setData("END_DATE", TJDODBTool.getInstance().getDBTime());
		parmMemTrade.setData("MR_NO", this.getValueString("MR_NO"));
		parmMemTrade.setData("RETURN_USER", Operator.getID());
		parmMemTrade.setData("RETURN_DATE", TJDODBTool.getInstance().getDBTime());
		parmMemTrade.setData("OPT_USER", Operator.getID());
		parmMemTrade.setData("OPT_TERM", Operator.getIP());
		parmMemTrade.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		TTextFormat t = (TTextFormat) this.getComponent("STOP_REASON");
		parmMemTrade.setData("DESCRIPTION", t.getText());
		parmMemTrade.setData("STOP_CARD_DESCRIPTION", this.getValueString("STOP_CARD_DESCRIPTION"));
		parmMemTrade.setData("MEM_CODE", tableParm.getValue("MEM_CODE",
				selectRow));
		parmMemTrade.setData("MEM_FEE", 0 - this.getValueDouble("FEEs"));
		//===start====add by kangy	20160822====
		for(int i=1;i<11;i++){
			if(i<10){
			parmMemTrade.setData("PAY_TYPE0"+i,"");
			}else if(i>9){
				parmMemTrade.setData("PAY_TYPE"+i,"");
			}
		}
		if("C0".equals(this.getValueString("MEM_GATHER_TYPE"))){//现金
			parmMemTrade.setData("PAY_TYPE01", -feeS);
		}
		if("C1".equals(this.getValueString("MEM_GATHER_TYPE"))){//刷卡
			parmMemTrade.setData("PAY_TYPE02", -feeS);
		}
		if("T0".equals(this.getValueString("MEM_GATHER_TYPE"))){//支票
			parmMemTrade.setData("PAY_TYPE03", -feeS);
		}
		if("C4".equals(this.getValueString("MEM_GATHER_TYPE"))){//医院垫付
			parmMemTrade.setData("PAY_TYPE04", -feeS);
		}
		if("LPK".equals(this.getValueString("MEM_GATHER_TYPE"))){//礼品卡
			parmMemTrade.setData("PAY_TYPE05", -feeS);
		}
		if("XJZKQ".equals(this.getValueString("MEM_GATHER_TYPE"))){//代金券
			parmMemTrade.setData("PAY_TYPE06", -feeS);
		}
		if("TCJZ".equals(this.getValueString("MEM_GATHER_TYPE"))){//套餐结转
			parmMemTrade.setData("PAY_TYPE07", -feeS);
		}
		if("BXZF".equals(this.getValueString("MEM_GATHER_TYPE"))){//保险支付
			parmMemTrade.setData("PAY_TYPE08", -feeS);
		}
		if("WX".equals(this.getValueString("MEM_GATHER_TYPE"))){//微信
			parmMemTrade.setData("PAY_TYPE09", -feeS);
		}
		if("ZFB".equals(this.getValueString("MEM_GATHER_TYPE"))){//支付宝
			parmMemTrade.setData("PAY_TYPE10", -feeS);
		}
		//===end====add by kangy	20160822====
		
		
		
		parmMemTrade.setData("RETURN_TRADE_NO", tableParm.getValue("TRADE_NO",
				selectRow));
		parmMemTrade.setData("STATUS", "3");
		parmMemTrade.setData("REMOVE_FLG", "N");
		parmMemTrade.setData("TRADE_NO", getMEMTradeNo());
		parmMemTrade.setData("MEM_CARD_NO", tableParm.getValue("MEM_CARD_NO",
				selectRow));
		parmMemTrade.setData("GATHER_TYPE", this
				.getValueString("MEM_GATHER_TYPE"));

		String sDate = tableParm.getValue("START_DATE",selectRow).replace("-", "").replace("/", "").substring(0, 8)+"000000";
		String eDate = tableParm.getValue("END_DATE",selectRow).replace("-", "").replace("/", "").substring(0, 8)+"235959";
		parmMemTrade.setData("START_DATE", StringTool.getTimestamp(sDate,"yyyyMMddHHmmss"));
		parmMemTrade.setData("END_DATE", StringTool.getTimestamp(eDate,"yyyyMMddHHmmss"));
		
		parm.setData("parmMemInfo", parmMemInfo.getData());
		parm.setData("parmMemTrade", parmMemTrade.getData());
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction",
				"stopCardMemTrade", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("停卡失败！");
			return;
		}
		this.messageBox("停卡成功！");
		
		
		TParm parmSum = new TParm();
		parmSum.setData("MR_NO", this.getValueString("MR_NO"));
		parmSum.setData("NAME", this.getValueString("PAT_NAME"));
		parmSum.setData("GATHER_TYPE_NAME", this.getText("MEM_GATHER_TYPE"));
		parmSum.setData("BUSINESS_AMT", this.getValueDouble("FEEs"));
		parmSum.setData("SEX_TYPE", this.getValueString("SEX_CODE"));
		parmSum.setData("COPY", "");
		onPrint(parmSum);
		onQuery();
		this.clearValue("STOP_CARD_DESCRIPTION;STOP_REASON;FEEs;MEM_GATHER_TYPE");
		setValue("MEM_GATHER_TYPE", memGatherType);
		getTable();
		

	}

	/**
	 * 撤消停卡
	 */
	public void onRevoke() {
		if (!removeFlg.equals("Y")) {
			this.messageBox("该记录不是停卡记录，请重新选择！");
			return;
		}
		
		TParm parmTrade = new TParm();
		TParm tableParm = table.getParmValue();
		Timestamp dateNow = SystemTool.getInstance().getDate();
		Timestamp date =StringTool.getTimestamp(tableParm.getValue("START_DATE", selectRow), "yyyyMMddHHmmss") ;
		if(date.after(dateNow)){
			this.messageBox("开始时间大于今天，请选择正确数据");
			return;
		}
		
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfo(this.getValueString("MR_NO"))));
		if (parm1.getCount() > 0) {
			this.messageBox("已进行升级操作，不能撤销停卡!");
			return;
		} else {
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(
					MEMSQL.getMemTrade(this.getValueString("MR_NO"))));
			if (parm2.getCount() > 0) {
				this.messageBox("已进行升级操作，不能撤销停卡!");
				return;
			}
		}
		//add by huangtt 20140928 start
		
		
		double memFee =0.00;
		for (int i = 0; i < tableParm.getCount("MEM_CARD_NO"); i++) {
			if(tableParm.getValue("MEM_CARD_NO", selectRow).equals(tableParm.getValue("MEM_CARD_NO", i))){
				memFee += tableParm.getDouble("REVOKE_FEE", i);
			}
		}
		double removeFee = Math.abs(memFee);
//		double removeFee = -tableParm.getDouble("REVOKE_FEE", selectRow);
		
		if(removeFee > 0){
			this.messageBox("请收回停卡时所退的"+removeFee+"元会费");
		}
		//add by huangtt 20140928 end
		parmTrade.setData("MR_NO", this.getValueString("MR_NO"));
		parmTrade.setData("TRADE_NO", tableParm.getValue("TRADE_NO", selectRow));
		parmTrade.setData("MEM_CARD_NO", tableParm.getValue("MEM_CARD_NO", selectRow)); 
		String endDate = tableParm.getValue("END_DATE", selectRow).replace("-", "").substring(0, 8)+"235959";
		parmTrade.setData("END_DATE", StringTool.getTimestamp(endDate,"yyyyMMddHHmmss"));
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction",
				"revokeCardMemTrade", parmTrade);
		if (result.getErrCode() < 0) {
			this.messageBox("撤销停卡失败！");
			return;
		}
		this.messageBox("撤销停卡成功！");
		onQuery(); 
		this.clearValue("STOP_CARD_DESCRIPTION;STOP_REASON;FEEs;MEM_GATHER_TYPE");
		setValue("MEM_GATHER_TYPE", memGatherType);
		getTable();
	}
	
	public void onPrint(TParm parmSum){
		/**
		TParm parm = new TParm();
		 parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //病案号
	       parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //姓名
	       parm.setData("GATHER_NAME", "TEXT", "退"); //收款方式
	       parm.setData("GATHER_TYPE", "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //收款方式
	       parm.setData("AMT", "TEXT", StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //金额
	       parm.setData("SEX_TYPE", "TEXT", parmSum.getValue("SEX_TYPE").equals("1")?"男":"女"); //性别
	       parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); //大写金额
	       String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance().
	               getDBTime()), "yyyy/MM/dd"); //年月日
	       String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
					.getInstance().getDBTime()), "HH:mm"); //时分秒
	       parm.setData("DATE", "TEXT", yMd + "    " + hms); //日期
	       parm.setData("USER_NAME", "TEXT", Operator.getID()); //收款人
	       parm.setData("COPY", "TEXT", ""); //补印注记
	       parm.setData("O", "TEXT", "o"); 
	       this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEM_FEE.jhw", parm ,true);
	       */
		
		//add by sunqy 20140613 ----start----
		DecimalFormat df=new DecimalFormat("#0.00");
		TTable setTable = (TTable)callFunction("UI|TABLE|getThis");
		int selRow = setTable.getSelectedRow();
		String sql = "SELECT MEM_DESC CTZ_CODE FROM MEM_TRADE WHERE MEM_CODE = '" + 
							setTable.getValueAt(selRow, setTable.getColumnIndex("MEM_CODE")) +
							"' AND MR_NO = '"+ parmSum.getValue("MR_NO") +"' ORDER BY OPT_DATE DESC";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		result = result.getRow(0);
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "会员费停卡收据");
		parm.setData("TYPE", "TEXT", ""); //类别
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); //病案号
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); //姓名
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //票据号
		parm.setData("DEPT_CODE", "TEXT", "");// 科别
		parm.setData("MONEY", "TEXT", df.format(-parmSum.getDouble("BUSINESS_AMT"))+"元"); // 金额
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(-parmSum.getDouble("BUSINESS_AMT"))); // 大写金额
		parm.setData("ACOUNT_NO", "TEXT", "");// 账号
		String payType = "";
		payType += parmSum.getValue("GATHER_TYPE_NAME")+"："+df.format(-parmSum.getDouble("BUSINESS_AMT"))+"元";// 将得到的支付方式与支付金额合并
		parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
		parm.setData("CTZ_CODE", "TEXT", result.getData("CTZ_CODE"));// 产品
		parm.setData("REASON", "TEXT", this.getText("STOP_REASON"));// 折扣原因
		String date = StringTool.getTimestamp(new Date()).toString().substring(
				0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// 日期
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // 收款人
		parm.setData("RETURN", "TEXT", "退"); // 退
		parm.setData("o", "TEXT", "o");// 退
		parm.setData("COPY", "TEXT", parmSum.getValue("COPY")); // 补印注记
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "所有医院");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
//		this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMFeeReceiptV45.jhw",parm, true);
		this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMFeeReceiptV45.jhw"),
				IReportTool.getInstance().getReportParm("MEMFeeReceiptV45.class", parm));//合并报表
		//add by sunqy 20140613 ----end----
	}

	public void onClear() {
		this.clearValue("MR_NO;PAT_NAME;FIRST_NAME;LAST_NAME;CURRENT_ADDRESS;BIRTH_DATE;SEX_CODE;" +
				"MEM_TYPE;START_DATE;END_DATE;STOP_REASON;FEEs;MEM_GATHER_TYPE;STOP_CARD_DESCRIPTION");
		table.removeRowAll();
		tableMem.removeRowAll();
		selectRow = -1;
		parmEKT = null;
		parmMEM = null;
		pat = null;
		removeFlg="";


		//支付方式
    	setValue("MEM_GATHER_TYPE", memGatherType);
	}

	/**
	 * 取号原则:mem_trade
	 */
	public String getMEMTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "MEM_TRADE_NO",
				"MEM_TRADE_NO");
	}
	
	/**
	 * 补印　　====add by huangtt 20140928 
	 */
	public void onRePrint(){
		if (!removeFlg.equals("Y")) {
			this.messageBox("该记录不是停卡记录，请重新选择！");
			return;
		}
		TParm tableParm = table.getParmValue();
		String gatherType = tableParm.getValue("GATHER_TYPE", selectRow);
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'GATHER_TYPE' AND ID='"+gatherType+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String gatherTypeDesc = result.getValue("CHN_DESC", 0);
		TParm parmSum = new TParm();
		parmSum.setData("MR_NO", this.getValueString("MR_NO"));
		parmSum.setData("NAME", this.getValueString("PAT_NAME"));
		parmSum.setData("GATHER_TYPE_NAME", gatherTypeDesc);
		parmSum.setData("BUSINESS_AMT", Math.abs(tableParm.getDouble("REVOKE_FEE", selectRow)) );
		parmSum.setData("SEX_TYPE", this.getValueString("SEX_CODE"));
		parmSum.setData("COPY", "(COPY)");
		onPrint(parmSum);
		
	}
	
}
