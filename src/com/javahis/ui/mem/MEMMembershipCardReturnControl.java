package com.javahis.ui.mem;

import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO;
import jdo.mem.MEMSQL;
import jdo.mem.MEMTool;
import jdo.opb.OPBTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;

/**
 * 
 * <p>
 * Title:会员卡退卡
 * </p>
 * 
 * <p>
 * Description: 会员卡退卡
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) /p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author huangtt 2013140107
 * @version 1.0
 */

public class MEMMembershipCardReturnControl extends TControl {

	private Pat pat;// 病患信息
	private TParm parmMEM;// 会员卡集合
	private static TTable table;
	private static TTable tableMem;
	PaymentTool paymentTool;
	private double LPK=0;
	private double XJZKQ=0;

	/**
	 * 初始化
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
		tableMem = (TTable) getComponent("MEM_TABLE");
		// String id = EKTTool.getInstance().getPayTypeDefault();
		// setValue("GATHER_TYPE", id);
		TPanel p = (TPanel) getComponent("tPanel_1");
		try {
			paymentTool = new PaymentTool(p, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 读会员卡
	 */
	public void onMEMcard() {
		// 读取医疗卡
		parmMEM = EKTIO.getInstance().TXreadEKT();
		if (null == parmMEM || parmMEM.getErrCode() < 0
				|| parmMEM.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmMEM.getErrText());
			parmMEM = null;
			return;
		}
		// 卡片余额
		// this.setValue("SUM_EKTFEE", parmMEM.getDouble("CURRENT_BALANCE"));
		// 卡片类型
		// this.setValue("MEM_CODE", parmMEM.getDouble("CARD_TYPE"));
		// 卡片号码
		this.setValue("CARD_CODE", parmMEM.getValue("MR_NO")
				+ parmMEM.getValue("SEQ"));
		this.setValue("MR_NO", parmMEM.getValue("MR_NO"));
		onQueryNO(false);
		onQueryTable();
	}
	/**
	 * 查询表格中的数据
	 */
	public void onQueryTable() {
		
		if (this.getValueString("MR_NO").equals("")) {
			return;
		}
//		if (this.getValueString("MEM_CODE").length() <= 0) {
//			this.messageBox("请选择会员卡类型！");
//			return;
//		}
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select("SELECT CTZ_CODE FROM SYS_CTZ WHERE MEM_TYPE='1'"));
		if (ctzParm.getCount() < 0) {
			this.messageBox("没有要查询的数据！");
			return;
		}
		String ctzs = "";
		for (int i = 0; i < ctzParm.getCount(); i++) {
			ctzs = ctzs + "'" + ctzParm.getValue("CTZ_CODE", i) + "',";
		}
		ctzs = ctzs.substring(0, ctzs.length() - 1);
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getOpdorderList(this.getValueString("MR_NO"), ctzs)));
		if (parm.getCount() < 0) {
			this.messageBox("没有要结算的费用！");
		} else {
			table.setParmValue(parm);
		}
//		if (this.getValueString("CARD_CODE").length() <= 0) {
//			TParm parmEkt = new TParm();
//			parmEkt.setData("MR_NO", this.getValueString("MR_NO"));
//			TParm result = EKTTool.getInstance().selectEKTIssuelog(parmEkt);
//			this.setValue("CARD_CODE", result.getValue("CARD_NO", 0));
//		}
		addFee();
		paymentTool.setAmt(this.getValueDouble("UN_FEE"));

	}

	public void onSave() {
		TParm payTypeTParm=paymentTool.table.getParmValue();
		TParm parm = null;
		try {
			parm = paymentTool.getAmts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messageBox(e.getMessage());
			return;
		}

		table.acceptText();
		// 查询自费身份
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getOwnPrice()));
		String ctz = ctzParm.getValue("CTZ_CODE", 0);
		TParm patInfoCtz = new TParm(); // 更新病患信息表
		TParm regPatadm = new TParm(); // 更新挂号表身份
		TParm opdOrder = new TParm();
		// TParm opdOrderCtz = new TParm(); //更新opd_order表中身份和差价
		TParm all = new TParm();
		patInfoCtz.setData("MR_NO", this.getValueString("MR_NO"));
		patInfoCtz.setData("CTZ1_CODE", ctz);
		patInfoCtz.setData("CTZ2_CODE", "");
		patInfoCtz.setData("CTZ3_CODE", "");
		patInfoCtz.setData("OPT_USER", Operator.getID());
		patInfoCtz.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		patInfoCtz.setData("OPT_TERM", Operator.getIP());
		opdOrder = table.getParmValue();
		if (opdOrder != null) {
			for (int i = 0; i < opdOrder.getCount("CASE_NO") - 1; i++) {
				if (!opdOrder.getValue("CASE_NO", i).equals(
						opdOrder.getValue("CASE_NO", i + 1))) {
					regPatadm.addData("CASE_NO", opdOrder
							.getValue("CASE_NO", i));
					regPatadm.addData("MR_NO", opdOrder.getValue("MR_NO", i));
					regPatadm.addData("CTZ1_CODE", ctz);
					regPatadm.addData("CTZ2_CODE", "");
					regPatadm.addData("CTZ3_CODE", "");
				}

				// opdOrderCtz.addData("CASE_NO",opdOrder.getValue("CASE_NO",
				// i));
				// opdOrderCtz.addData("MR_NO",opdOrder.getValue("MR_NO", i));
				// opdOrderCtz.addData("ORDER_CODE",opdOrder.getValue("ORDER_CODE",
				// i));
				// opdOrderCtz.addData("CTZ1_CODE",ctz);
				// opdOrderCtz.addData("CTZ2_CODE","");
				// opdOrderCtz.addData("CTZ3_CODE","");
			}
			regPatadm.addData("CASE_NO", opdOrder.getValue("CASE_NO", opdOrder
					.getCount("CASE_NO") - 1));
			regPatadm.addData("MR_NO", opdOrder.getValue("MR_NO", opdOrder
					.getCount("CASE_NO") - 1));
			regPatadm.addData("CTZ1_CODE", ctz);
			regPatadm.addData("CTZ2_CODE", "");
			regPatadm.addData("CTZ3_CODE", "");

			// opdOrderCtz.addData("CASE_NO",opdOrder.getValue("CASE_NO",
			// opdOrder.getCount("CASE_NO")-1));
			// opdOrderCtz.addData("MR_NO",opdOrder.getValue("MR_NO",
			// opdOrder.getCount("CASE_NO")-1));
			// opdOrderCtz.addData("ORDER_CODE",opdOrder.getValue("ORDER_CODE",
			// opdOrder.getCount("CASE_NO")-1));
			// opdOrderCtz.addData("CTZ1_CODE",ctz);
			// opdOrderCtz.addData("CTZ2_CODE","");
			// opdOrderCtz.addData("CTZ3_CODE","");
		}

		// 插入会员卡交易表
		TParm parmMem = new TParm();
		parmMem.setData("TRADE_NO", MEMTool.getInstance().getMEMTradeNo());
		parmMem.setData("STATUS", 2);
		parmMem.setData("MR_NO", this.getValueString("MR_NO"));
		parmMem.setData("MEM_CODE", this.getValue("MEM_CODE1"));
		parmMem.setData("GATHER_TYPE", "");
		TTextFormat memDesc = (TTextFormat) this.getComponent("MEM_CODE1");
		parmMem.setData("MEM_DESC", memDesc.getText());
		// String sql =
		// "SELECT SEQ FROM MEM_MEMBERSHIP_INFO WHERE MEM_CODE='"+this.getValue("MEM_CODE")+"'";
		// TParm parmSeq = new TParm(TJDODBTool.getInstance().select(sql));
		parmMem.setData("MEM_CARD_NO", this.getValueString("CARD_CODE"));
		parmMem.setData("MEM_FEE", this.getValueDouble("UN_FEE"));
		parmMem.setData("START_DATE", TJDODBTool.getInstance().getDBTime());
		parmMem.setData("END_DATE", TJDODBTool.getInstance().getDBTime());
		parmMem.setData("REMOVE_FLG", "N");
		parmMem.setData("DESCRIPTION", "");
		parmMem.setData("OPT_USER", Operator.getID());
		parmMem.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parmMem.setData("OPT_TERM", Operator.getIP());
		for (int i = 1; i < 11; i++) {//退费插入卡号add by huangjw 20141231
			parmMem.setData("MEMO"+i, "");
		}
//		for (int j = 0; j < paymentTool.table.getRowCount(); j++) {//退费插入卡号add by huangjw 20141231
//			if("C1".equals(paymentTool.table.getItemData(j, "PAY_TYPE"))){
//				parmMem.setData("MEMO"+(j+1), paymentTool.table.getItemData(j, "REMARKS"));
//			}
//		}
//		String cardType = "";//add by sunqy 20140729用于记录各个卡类型
		for (int j = 1; j < 11; j++) {
			if (j < 10) {
				parmMem.setData("PAY_TYPE0" + j, "");
			} else {
				parmMem.setData("PAY_TYPE" + j, "");
			}
		}
//		for (int i = 0; i < parm.getCount(); i++) {
//			parmMem.setData(parm.getValue("PAY_TYPE", i), parm.getData("AMT", i));
//			if(!"".equals(parm.getValue("CARD_TYPE", i))){
//				cardType += ","+parm.getValue("CARD_TYPE", i);
//			}
//		}
//		if(cardType.length()>0){
//			cardType = cardType.substring(1, cardType.length());
//		}
		parmMem.setData("CARD_TYPE", "");
		String cardType;//将卡类型和卡号存到一个字段中 modify by huangjw 20150104
		String cardTypeKey;
		String v;
		boolean flg2=paymentTool.onCheckPayType(payTypeTParm);
	    if (flg2) {
	    } else {
			this.messageBox("不允许出现相同的支付方式！");
			return;
		}
		for(int j=1;j<11;j++){
			cardTypeKey="MEMO"+j;
			if(j<10){
				v="PAY_TYPE0"+j;
			}else{
				v="PAY_TYPE"+j;
			}
			cardType = "";
			for(int i=0;i<parm.getCount("PAY_TYPE");i++){
				if(v.equals(parm.getValue("PAY_TYPE", i))){
					parmMem.setData(parm.getValue("PAY_TYPE", i), parm.getData("AMT", i));
					if("PAY_TYPE02".equals(parm.getValue("PAY_TYPE", i))){
						cardType = parm.getValue("CARD_TYPE", i)+"#"+parm.getValue("REMARKS",i);
					}else{
						cardType = parm.getValue("REMARKS",i);
					}
					break;
				}
			}
			parmMem.setData(cardTypeKey, cardType);
		}
		// 更新会员卡交易表中结束时间
		TParm updateMemTradeEndDate = new TParm();
		updateMemTradeEndDate.setData("MR_NO", this.getValueString("MR_NO"));
		updateMemTradeEndDate.setData("MEM_CARD_NO", this
				.getValueString("CARD_CODE"));
		updateMemTradeEndDate.setData("END_DATE", TJDODBTool.getInstance()
				.getDBTime());
		updateMemTradeEndDate.setData("RETURN_USER", Operator.getID());
		updateMemTradeEndDate.setData("RETURN_DATE", TJDODBTool.getInstance()
				.getDBTime());
		updateMemTradeEndDate.setData("OPT_USER", Operator.getID());
		updateMemTradeEndDate.setData("OPT_TERM", Operator.getIP());
		updateMemTradeEndDate.setData("OPT_DATE", TJDODBTool.getInstance()
				.getDBTime());

		// 更新会员卡表中结束时间
		TParm updateMemPatInfoEndDate = new TParm();
		updateMemPatInfoEndDate.setData("MR_NO", this.getValueString("MR_NO"));
		updateMemPatInfoEndDate.setData("END_DATE", TJDODBTool.getInstance()
				.getDBTime());

		// 查询会员卡折旧记录
		TParm parmFee = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemFeeDepreciation(this.getValueString("MR_NO"), this
						.getValueString("CARD_CODE"))));
		TParm parmSDate = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemFeeDepreciationSdate(this.getValueString("MR_NO"),
						this.getValueString("CARD_CODE"))));
		TParm parmEDate = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemFeeDepreciationEdate(this.getValueString("MR_NO"),
						this.getValueString("CARD_CODE"))));
		TParm feeDepreciation = new TParm();
		feeDepreciation.setData("MR_NO", this.getValueString("MR_NO"));
		feeDepreciation
				.setData("MEM_CARD_NO", this.getValueString("CARD_CODE"));
		feeDepreciation.setData("DEPRECIATION_FEE", -parmFee.getDouble(
				"DEPRECIATION_FEE", 0)); // 折旧费
		feeDepreciation.setData("DEPRECIATION_START_DATE", parmSDate.getData(
				"DEPRECIATION_START_DATE", 0)); // 折旧区间开始时间
		feeDepreciation.setData("DEPRECIATION_END_DATE", parmEDate.getData(
				"DEPRECIATION_END_DATE", 0)); // 折旧区间结束时间
		feeDepreciation.setData("REMAINING_AMOUNT", 0); // 该会员剩余会费
		feeDepreciation.setData("BEFORE_AMOUNT", 0); // 折旧前会费
		feeDepreciation.setData("STATUS", 1); // 状态：1折旧，2折旧抵冲
		feeDepreciation.setData("OPT_DATE", TJDODBTool.getInstance()
				.getDBTime());
		feeDepreciation.setData("OPT_USER", Operator.getID());
		feeDepreciation.setData("OPT_TERM", Operator.getIP());

		all.setData("patInfoCtz", patInfoCtz.getData());
		all.setData("regPatadm", regPatadm.getData());
		// all.setData("opdOrder", opdOrderCtz.getData());
		all.setData("parmMem", parmMem.getData());
		all.setData("feeDepreciation", feeDepreciation.getData());
		all.setData("updateMemTradeEndDate", updateMemTradeEndDate.getData());
		all.setData("updateMemPatInfoEndDate", updateMemPatInfoEndDate
				.getData());
		//add by sunqy 20140710 判断是否有未填写支付方式的金额----start----
		TParm payParm = paymentTool.table.getParmValue();
    	int payCount = payParm.getCount();
    	String payType = "";
    	double amt = 0.00;
    	for (int i = 0; i <= payCount; i++) {
    		payType = paymentTool.table.getItemString(i, "PAY_TYPE");
			amt = paymentTool.table.getItemDouble(i, "AMT");
			if(amt < 0 && ("".equals(payType)||payType==null)){
				this.messageBox("存在未设定支付方式的金额,请填写！");
				return;
			}
		}
    	//现金打票操作，校验是否存在支付宝或微信金额
		TParm checkCashTypeParm=OPBTool.getInstance().checkCashTypeReOther(payTypeTParm);
		TParm payCashParm=null;
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
			all.setData("payCashParm",payCashParm.getData());
		}
    	//add by sunqy 20140710 判断是否有未填写支付方式的金额----end----
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMAction",
				"updateCTZS", all);
		if (result.getErrCode() < 0) {
			this.messageBox("退卡失败！");
			return;
		}
		this.messageBox("退卡成功！");
		//add by sunqy 20140613 ----start----
		int row = 0;
		String copy = "";
		boolean flg = true;
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValueString("MR_NO"));
		sendParm.setData("NAME", this.getValueString("PAT_NAME"));
		sendParm.setData("BUSINESS_AMT", this.getValueDouble("UN_FEE"));
		sendParm.setData("TITLE", "会员费退卡收据");
		MEMFeeReceiptPrintControl.getInstance().onPrint(paymentTool.table, sendParm, copy, row, pat, flg, this, paymentTool,"2");//1是购卡票据，2是退卡票据，因为购卡票据和退卡票据的支付方式、卡类型、卡号都不一样
		//add by sunqy 20140613 ----end----
		onClear();
		

	}

	/**
	 * 查询
	 * 
	 */
	public void onQuery() {
		onQueryNO(true);
		onQueryTable();
	}

	/**
	 * 查询方法
	 */
	public void onQueryNO(boolean flg) {
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		parmMEM = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfo(mrNo)));
		if (parmMEM.getCount() < 0) {
			this.messageBox("该病患没有需要退会员卡的记录,请重新输入！");
			this.clearValue("MR_NO;CARD_CODE");
			return;
		}
		this.setValue("MEM_CODE", parmMEM.getData("MEM_CODE", 0));
		TParm parmGT = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemTrade1(mrNo)));
		this.setValue("MEM_CODE1", parmGT.getValue("MEM_CODE", 0));
		if (flg) {
			setValue("CARD_CODE", parmGT.getValue("MEM_CARD_NO", 0));
			
		}
		onHTable(parmGT);
		// callFunction("UI|GATHER_TYPE|setEnabled", false);

		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.grabFocus("PAT_NAME");
			if (!flg) {
				this.setValue("MR_NO", "");
				callFunction("UI|MR_NO|setEnabled", false); // 病案号可编辑
			}
			return;
		}
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", pat.getName());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("IDNO", pat.getIdNo());
		setValue("ID_TYPE", pat.getIdType());
		setValue("TEL_HOME", pat.getCellPhone());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("CTZ3_CODE", pat.getCtz3Code());
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
		setValue("REMARKS", pat.getRemarks());
		callFunction("UI|MR_NO|setEnabled", false); // 病案号不可编辑

		// if(this.getValueString("CARD_CODE").length()<0){
		// TParm parmEktIssuelog = new
		// TParm(TJDODBTool.getInstance().select(MEMSQL.getMemEktIssuelog(getValueString("MR_NO"))));
		// System.out.println(parmEktIssuelog+"");
		// this.setValue("CARD_CODE", parmEktIssuelog.getValue("CARD_NO",0));
		// }

		onQueryMemFee();

	}

	/**
	 * 根据卡片类型，得到会费
	 */
	public void onQueryMemFee() {
		// String memCode = this.getValueString("MEM_CODE1");
		// String mrNo = this.getValueString("MR_NO");
		String cardNo = this.getValueString("CARD_CODE");
		String sql = "SELECT SUM(MEM_FEE) MEM_FEE FROM MEM_TRADE WHERE  MEM_CARD_NO='"
				+ cardNo + "' AND END_DATE > SYSDATE AND REMOVE_FLG='N' ";
		// TParm parm = new
		// TParm(TJDODBTool.getInstance().select(MEMSQL.getMemFee(memCode,mrNo)));
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		this.setValue("MEM_FEE", parm.getValue("MEM_FEE", 0));
		LPK = this.getPayTypeFee("LPK", cardNo);
		XJZKQ = this.getPayTypeFee("XJZKQ", cardNo);
		this.setValue("RE_MEM_FEE", parm.getDouble("MEM_FEE", 0)-LPK-XJZKQ);
	}

	public void onClear() {
		clearValue(" MR_NO;PAT_NAME;IDNO;ID_TYPE;BIRTH_DATE;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;CARD_CODE;MEM_CODE; "
				+ "CURRENT_ADDRESS;REMARKS;TEL_HOME;MEM_FEE;GATHER_TYPE;UN_FEE;SUM_EKTFEE;FEE;MEM_CODE1;RE_MEM_FEE");
		callFunction("UI|MR_NO|setEnabled", true); // 病案号可编辑
		table.removeRowAll();
		tableMem.removeRowAll();
		parmMEM = null;
		callFunction("UI|GATHER_TYPE|setEnabled", true);
		// String id = EKTTool.getInstance().getPayTypeDefault();
		// setValue("GATHER_TYPE", id);
		LPK=0;
		XJZKQ=0;
		paymentTool.onClear();

	}

	/**
	 * 就诊差额
	 */
	public void addFee() {
		table.acceptText();
		double difFee = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			difFee += table.getItemDouble(i, "DIFFERENT_PRICE");
		}
		this.setValue("FEE", difFee);
		unFee();
	}

	public void unFee() {
		double fee = this.getValueDouble("FEE");
		double memFee = this.getValueDouble("MEM_FEE");
		double unFee = fee - memFee;
		double reMemFee = this.getValueDouble("RE_MEM_FEE");
		if(unFee < 0){
//			if((unFee+LPK+XJZKQ) < 0){
//				this.setValue("UN_FEE", unFee+LPK+XJZKQ);
//			}else{
//				this.setValue("UN_FEE", 0);
//			}
		
			if((fee - LPK-XJZKQ)>0){
				this.setValue("UN_FEE", unFee);
			}else{
				this.setValue("UN_FEE", 0-reMemFee);
			}
			
		}else{
			this.setValue("UN_FEE", unFee);
		}
	
	}
	
	/**
	 * 得到购卡时某种支付方式的钱数
	 * @param gatherType
	 * @param memCardNo
	 * @return
	 */
	public double getPayTypeFee(String gatherType,String memCardNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getPayType(gatherType)));
		String payType = parm.getValue("PAYTYPE", 0);
		parm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getPayTypeFee(payType, memCardNo)));
		return parm.getDouble("PAY_TYPE", 0);
	}
	
	/**
	 * 得到售卡时的支付方式
	 * @param parm
	 */
	public void onHTable(TParm parm){
		TParm changeType = new TParm();	
		double [] payType = new double[11];
		for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 1; j < payType.length; j++) {
				String type = j < 10 ? "PAY_TYPE0"+j : "PAY_TYPE"+j;
				payType[j] += parm.getDouble(type, i);
			}		
		}
		for (int j = 1; j < payType.length; j++) {
			String type = j < 10 ? "PAY_TYPE0"+j : "PAY_TYPE"+j;
			changeType.setData(type, payType[j]);
		}

		TParm tableParm = MEMSQL.getMemType(changeType);		
		tableMem.setParmValue(tableParm);
		
	}

}
