package com.javahis.ui.ekt;

import java.sql.Timestamp;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 医疗卡充值
 * </p>
 * 
 * <p>
 * Description: 医疗卡充值
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20110930
 * @version 1.0
 */
public class EKTTopUpControl extends TControl {
	private Pat pat; // 病患信息
	private TParm parmEKT;
	private String flg;
	private boolean ektFlg = false;
	private boolean printBil = false;// 打印票据时使用
	private TParm parmSum;// 执行充值操作参数
	// zhangp 20111227
	private int row = -1;// 选中行

	public EKTTopUpControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// =====zhangp 20120224 支付方式 modify start
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm parm = (TParm) obj;
			flg = parm.getValue("FLG");
		}
		if ("Y".equals(flg)) {
			onReadEKT();
		}
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
		// =======zhangp 20120224 modify end

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
		// 卡片余额
		this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
		// 卡片序号
		this.setValue("SEQ", parmEKT.getValue("SEQ"));
		// callFunction("UI|CARD_CODE|setEnabled", false); //卡号不可编辑
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		onQuery();
		// 20111228 zhangp
		// if(this.getValueString("MR_NO")!=null&&!this.getValueString("MR_NO").equals("")){
		// onTable();
		// }
		// ====201202026 zhangp modify start
		grabFocus("TOP_UPFEE");
		// ====201202026 zhangp modify end
	}

	/**
	 * 保存
	 */
	public void onSave() {
		// TParm parm=new TParm();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox("读取医疗卡失败");
			parmEKT = null;
			return;

		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm = EKTTool.getInstance().selectEKTIssuelog(parm);
		if (parm.getCount() <= 0) {
			this.messageBox("此病患没有医疗卡信息,请重新制卡");
			return;
		}

		onFEE();
		// parm.setData("CARD_NO",
		// parmEKT.getValue("MR_NO")+parmEKT.getValue("SEQ"));
	}

	/**
	 * 查询病患信息
	 */
	public void onQuery() {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案信息");
			this.grabFocus("PAT_NAME");
			this.setValue("MR_NO", "");
			callFunction("UI|MR_NO|setEnabled", true); // 病案号可编辑
			return;
		}
		// 病案号
		this.setValue("MR_NO", pat.getMrNo());
		// 姓名
		this.setValue("PAT_NAME", pat.getName());
		// 出生日期
		setValue("BIRTH_DATE", pat.getBirthday());
		// 性别
		setValue("SEX_CODE", pat.getSexCode());
		callFunction("UI|MR_NO|setEnabled", false); // 病案号可编辑
		// ===zhangp 20120328 start
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		TParm EKTparm = EKTTool.getInstance().selectEKTIssuelog(parm);
		if (EKTparm.getCount() <= 0) {
			messageBox("无医疗卡");
			return;
		}
		this.setValue("OLD_EKTFEE", StringTool.round(EKTparm.getDouble(
				"CURRENT_BALANCE", 0), 2));
		if (this.getValueString("MR_NO") != null
				&& !this.getValueString("MR_NO").equals("")) {
			onTable();
		}
		// ===zhangp 20120328 end
	}

	/**
	 * 充值文本框回车事件
	 */
	public void addFee() {
		if (this.getValueDouble("TOP_UPFEE") < 0) {
			this.messageBox("充值金额不可以为负值");
			return;
		}
		this.setValue("SUM_EKTFEE", this.getValueDouble("TOP_UPFEE")
				+ this.getValueDouble("OLD_EKTFEE"));
	}

	/**
	 * 收费方法
	 */
	private void onFEE() {
		// if(!txEKT){
		// this.messageBox("请读取医疗卡信息");
		// return;
		// }
		
		if("WX".equals(this.getValue("GATHER_TYPE"))||"ZFB".equals(this.getValue("GATHER_TYPE"))){
			if(this.getValue("BIL_CODE").toString().length()<=0){
				this.messageBox("微信或支付宝需要在票据号码处添加交易号！");
				return;
			}
		}

		
		if (this.getValue("TOP_UPFEE").toString().length() <= 0) {
			this.messageBox("请输入要充值的金额");
			return;
		}
		if (this.getValueDouble("TOP_UPFEE") <= 0) {
			this.messageBox("充值金额不正确");
			return;
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
			this.messageBox("支付方式不可以为空值");
			return;
		}
		//如果支付方式为刷卡时，卡类型和卡号为必填项add by huangjw 20150115
		if("C1".equals(this.getValue("GATHER_TYPE"))){
			if("".equals(this.getValue("CARD_TYPE"))||this.getValue("CARD_TYPE")==null){
				this.messageBox("卡类型不可为空");
				return;
			}
			if("".equals(this.getValue("DESCRIPTION"))){
				this.messageBox("备注不可为空");
				return;
			}
		}
		// add by huangtt 20140228
		if (this.messageBox("充值", "是否充值" + this.getValueDouble("TOP_UPFEE")
				+ "元", 0) != 0) {
			return;
		}
		// 20120104 zhangp 注 充值不需要验证
		// if (this.messageBox("收费", "是否执行充值操作", 0) != 0) {
		// return;
		// }
		// 密码校验
		// if (!this.getValue("CARD_PWD").toString().trim().equals(parmEKT.
		// getValue("PASSWORD", 0).trim())) {
		// this.messageBox("密码不符,请重新输入");
		// this.setValue("CARD_PWD", "");
		// this.grabFocus("CARD_PWD");
		// return;
		// }
		// TTable table = (TTable)callFunction("UI|TABLE|getThis");//add by
		// sunqy 20140611
		// int selectrow = table.getSelectedRow();//add by sunqy 20140611
		TParm result = null;
		parmSum = new TParm();
		parmSum.setData("ACOUNT_NO", parmEKT.getValue("CARD_NO"));// add by
																	// sunqy
																	// 20140611
		parmSum.setData("CARD_NO", pat.getMrNo() + this.getValue("SEQ"));
		parmSum.setData("CURRENT_BALANCE", StringTool.round(parmEKT
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
		parmSum.setData("CASE_NO", "none");
		parmSum.setData("NAME", pat.getName());
		parmSum.setData("MR_NO", pat.getMrNo());
		parmSum.setData("ID_NO", null != pat.getIdNo()
				&& pat.getIdNo().length() > 0 ? pat.getIdNo() : "none");
		parmSum.setData("OPT_USER", Operator.getID());
		parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parmSum.setData("OPT_TERM", Operator.getIP());
		parmSum.setData("FLG", ektFlg);
		parmSum.setData("ISSUERSN_CODE", "充值"); // 发卡原因
		parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); // 支付方式
		parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); // 支付方式名称
		parmSum.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // 充值金额
		parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); // 性别
		//if ("C1".equals(this.getValue("GATHER_TYPE"))) {// add by sunqy 20140715
														// 若支付方式为刷卡
														// 将备注存入数据库(记录银行卡号)
			parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION"));// 备注

		//}
		if ("WX".equals(this.getValue("GATHER_TYPE"))) {
		
			parmSum.setData("PRINT_NO", this.getValue("BIL_CODE"));// 备注
			
		}
		if ("ZFB".equals(this.getValue("GATHER_TYPE"))) {
			parmSum.setData("PRINT_NO", this.getValue("BIL_CODE"));// 备注
			
		}
		parmSum.setData("CARD_TYPE", this.getValue("CARD_TYPE"));
		parmSum.setData("BIL_CODE", this.getValue("BIL_CODE"));// 票据号
		parmSum.setData("CREAT_USER", Operator.getID()); // 执行人员//=====yanjing
		// 明细表参数
		TParm feeParm = new TParm();
		feeParm.setData("ORIGINAL_BALANCE", StringTool.round(parmEKT
				.getDouble("CURRENT_BALANCE"), 2)); // 原金额
		feeParm.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // 充值金额
		feeParm.setData("CURRENT_BALANCE", StringTool.round(parmEKT
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
		// EKT_ACCNTDETAIL 数据
		parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm)
				.getData());
		// zhangp 20120109 EKT_BIL_PAY 加字段
		parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime()); // 售卡操作时间
		parmSum.setData("PROCEDURE_AMT", 0.00); // PROCEDURE_AMT
		// bil_pay 充值表数据
		parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
		// 更新余额
		result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"TXEKTonFee", parmSum);
		// System.out.println("***888 parmSum is:"+parmSum);
		if (result.getErrCode() < 0) {
			this.messageBox("医疗卡充值失败");
		} else {
			// add by huangtt 20131225 start
			/*TParm parmAmt = new TParm();
			parmAmt.setData("MR_NO", pat.getMrNo());
			parmAmt.setData("SEQ", this.getValue("SEQ"));
			parmAmt.setData("CURRENT_BALANCE", feeParm
					.getData("CURRENT_BALANCE"));
			try {
				EKTIO.getInstance().TXwriteEKTATM(parmAmt, pat.getMrNo());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				this.messageBox("医疗卡充值失败");
				e1.printStackTrace();
				return;
			}*/
			// add by huangtt 20131225 end
			this.messageBox("充值成功");
			onReadEKT();
			TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
			table.setSelectedRow(table.getRowCount() - 1);
			printBil = true;
			String bil_business_no = result.getValue("BIL_BUSINESS_NO");// 收据号
			try {
				onPrint(bil_business_no, "");
			} catch (Exception e) {
				this.messageBox("打印出现问题,请执行补印操作");
				// TODO: handle exception
			}

		}
		// }
		// zhangp 20120131 充值完成后重新查询充值记录
		onTable();
		onClear();
	}

	/**
	 * 医疗卡明细表插入数据
	 * 
	 * @param p
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBusinessParm(TParm p, TParm feeParm) {
		// 明细档数据
		TParm bilParm = new TParm();
		bilParm.setData("BUSINESS_SEQ", 0);
		bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
		bilParm.setData("MR_NO", pat.getMrNo());
		bilParm.setData("CASE_NO", "none");
		bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("SEQ_NO", 0);
		bilParm.setData("CHARGE_FLG", "3"); // 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
		bilParm.setData("ORIGINAL_BALANCE", feeParm
				.getValue("ORIGINAL_BALANCE")); // 收费前余额
		bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
		bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
		bilParm.setData("CASHIER_CODE", Operator.getID());
		bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
		// 1：交易执行完成
		// 2：双方确认完成
		bilParm.setData("BUSINESS_STATUS", "1");
		// 1：未对帐
		// 2：对账成功
		// 3：对账失败
		bilParm.setData("ACCNT_STATUS", "1");
		bilParm.setData("ACCNT_USER", new TNull(String.class));
		bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
		bilParm.setData("OPT_USER", Operator.getID());
		bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		bilParm.setData("OPT_TERM", Operator.getIP());
		// p.setData("bilParm",bilParm.getData());
		return bilParm;
	}

	/**
	 * 充值档添加数据参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBillParm(TParm parm, TParm feeParm) {
		TParm billParm = new TParm();
		billParm.setData("CARD_NO", parm.getValue("CARD_NO")); // 卡号
		billParm.setData("CURT_CARDSEQ", 0); // 序号
		billParm.setData("ACCNT_TYPE", "4"); // 明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
		billParm.setData("MR_NO", parm.getValue("MR_NO")); // 病案号
		billParm.setData("ID_NO", parm.getValue("ID_NO")); // 身份证号
		billParm.setData("NAME", parm.getValue("NAME")); // 病患名称
		billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); // 充值金额
		billParm.setData("CREAT_USER", Operator.getID()); // 执行人员
		billParm.setData("OPT_USER", Operator.getID()); // 操作人员
		billParm.setData("OPT_TERM", Operator.getIP()); // 执行ip
		billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); // 支付方式
		// zhangp 20120109
		billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
		billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
		return billParm;
	}

	/**
	 *清空
	 */
	public void onClear() {
		clearValue(" MR_NO;PAT_NAME;SEQ; " + " BIRTH_DATE;SEX_CODE; "
				+ " BIL_CODE;DESCRIPTION; "
				+ " OLD_EKTFEE;TOP_UPFEE;SUM_EKTFEE;CARD_TYPE");
		// 设置默认服务等级
		// txEKT = false; //泰心医疗卡写卡管控
		parmEKT = null; // 医疗卡读卡parm
		ektFlg = false;
		printBil = false;
		parmSum = null;// 执行退款操作参数
		// 解锁病患
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
		// ===zhangp 20120328 start
		callFunction("UI|MR_NO|setEnabled", true); // 病案号可编辑
		// ===zhangp 20120328 end

		TTable table = (TTable) getComponent("TABLE");
		table.removeRowAll();
	}

	/**
	 * 充值打印
	 */
	private void onPrint(String bil_business_no, String copy) {
		/**
		 * if (!printBil) { this.messageBox("进行医疗卡充值操作才可以打印"); return; } TParm
		 * parm = new TParm(); parm.setData("TITLE", "TEXT",
		 * (Operator.getRegion() != null && Operator.getRegion().length() > 0 ?
		 * Operator.getHospitalCHNFullName() : "所有医院")); parm.setData("MR_NO",
		 * "TEXT", parmSum.getValue("MR_NO")); //病案号 parm.setData("PAT_NAME",
		 * "TEXT", parmSum.getValue("NAME")); //姓名 parm.setData("GATHER_TYPE",
		 * "TEXT", parmSum.getValue("GATHER_TYPE_NAME")); //收款方式
		 * parm.setData("AMT", "TEXT",
		 * StringTool.round(parmSum.getDouble("BUSINESS_AMT"),2)); //金额
		 * //====zhangp 20120525 start // parm.setData("GATHER_NAME", "TEXT",
		 * "收 款"); //收款方式 parm.setData("GATHER_NAME", "TEXT", ""); //收款方式
		 * //====zhangp 20120525 end parm.setData("TYPE", "TEXT", "预 收");
		 * //文本预收金额 parm.setData("SEX_TYPE", "TEXT",
		 * parmSum.getValue("SEX_TYPE").equals("1")?"男":"女"); //性别
		 * parm.setData("AMT_AW", "TEXT",
		 * StringUtil.getInstance().numberToWord(parmSum
		 * .getDouble("BUSINESS_AMT"))); //大写金额 parm.setData("TOP1", "TEXT",
		 * "EKTRT001 FROM "+Operator.getID()); //台头一 String yMd =
		 * StringTool.getString
		 * (TypeTool.getTimestamp(TJDODBTool.getInstance().getDBTime()),
		 * "yyyyMMdd"); //年月日 String hms
		 * =StringTool.getString(TypeTool.getTimestamp
		 * (TJDODBTool.getInstance().getDBTime()), "hhmmss"); //时分秒
		 * parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); //台头二
		 * yMd =
		 * StringTool.getString(TypeTool.getTimestamp(TJDODBTool.getInstance
		 * ().getDBTime()), "yyyy/MM/dd"); //年月日 hms =
		 * StringTool.getString(TypeTool
		 * .getTimestamp(TJDODBTool.getInstance().getDBTime()), "HH:mm"); //时分秒
		 * parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION"));
		 * //备注 parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE"));
		 * //票据号 if(null == bil_business_no) bil_business_no=
		 * EKTTool.getInstance().getBillBusinessNo();//补印操作
		 * parm.setData("ONFEE_NO", "TEXT", bil_business_no); //收据号
		 * parm.setData("PRINT_DATE", "TEXT", yMd); //打印时间 parm.setData("DATE",
		 * "TEXT", yMd + "    " + hms); //日期 parm.setData("USER_NAME", "TEXT",
		 * Operator.getID()); //收款人 parm.setData("COPY", "TEXT", copy); //收款人
		 * //===zhangp 20120525 start parm.setData("O", "TEXT", ""); //
		 * this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw",
		 * parm,true);
		 * this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw",
		 * parm,true); //===zhangp 20120525 end
		 */
		
		
//		System.out.println("00----"+SystemTool.getInstance().getDate());
		// modify by sunqy 20140611 ----start----
		if (!printBil) {
			this.messageBox("进行医疗卡充值操作才可以打印");
			return;
		}
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		int row = table.getSelectedRow();// 得到选中行
		TParm result = getPrintData(table.getParmValue().getValue("BIL_BUSINESS_NO", row));
//		System.out.println("11----"+SystemTool.getInstance().getDate());
		// if(row<0){
		// this.messageBox("没有选中数据!");
		// return;
		// }
		// EKTReceiptPrint print = new EKTReceiptPrint();
		boolean flg = false;
		result.setData("TITLE", "门诊充值收据");
//		System.out.println("22----"+SystemTool.getInstance().getDate());
		EKTReceiptPrintControl.getInstance().onPrint(table, result, copy, row,
				pat, flg, this);
//		System.out.println("33----"+SystemTool.getInstance().getDate());
		// print.onPrint(table, parmSum, copy, row, pat,flg);
		// modify by sunqy 20140611 ----end----
		
	}

	/**
	 * 补印 20111228 zhangp
	 */
	public void onRePrint(String copy) {
		/**
		 * TTable table = (TTable) this.callFunction("UI|TABLE|getThis"); row =
		 * table.getSelectedRow(); if (row == -1) { messageBox("请选择要补印的记录");
		 * return; } else {
		 * 
		 * String bilBusinessNo = table.getValueAt(row, 1).toString(); String
		 * sql = "SELECT MR_NO FROM EKT_BIL_PAY WHERE BIL_BUSINESS_NO = '" +
		 * bilBusinessNo + "'"; TParm result = new
		 * TParm(TJDODBTool.getInstance().select(sql)); String mrNo =
		 * result.getData("MR_NO", 0).toString(); pat = Pat.onQueryByMrNo(mrNo);
		 * TParm parm = new TParm(); parm.setData("TITLE", "TEXT",
		 * (Operator.getRegion() != null && Operator.getRegion().length() > 0 ?
		 * Operator .getHospitalCHNFullName() : "所有医院")); parm.setData("MR_NO",
		 * "TEXT", mrNo); // 病案号 parm.setData("PAT_NAME", "TEXT",
		 * pat.getName()); // 姓名 // ====zhangp 20120525 start //
		 * parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
		 * parm.setData("GATHER_NAME", "TEXT", ""); // 收款方式 // ====zhangp
		 * 20120525 end parm.setData("TYPE", "TEXT", "预 收"); // 文本预收金额 String
		 * gatherType = table.getValueAt(row, 4).toString(); String sqlgt =" SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' AND ID = '"
		 * + gatherType + "' ORDER BY SEQ,ID "; TParm resultgt = new
		 * TParm(TJDODBTool.getInstance().select(sqlgt));
		 * parm.setData("GATHER_TYPE", "TEXT", resultgt.getData("NAME", 0)
		 * .toString()); // 收款方式 parm.setData("AMT", "TEXT",
		 * StringTool.round((Double) table .getValueAt(row, 3), 2)); // 金额
		 * parm.setData("SEX_TYPE", "TEXT", pat.getSexString()); // 性别
		 * parm.setData("AMT_AW", "TEXT", StringUtil.getInstance()
		 * .numberToWord((Double) table.getValueAt(row, 3))); // 大写金额
		 * parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //
		 * 台头一 String yMd =
		 * StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		 * .getInstance().getDBTime()), "yyyyMMdd"); // 年月日 String hms =
		 * StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		 * .getInstance().getDBTime()), "hhmmss"); // 时分秒 parm.setData("TOP2",
		 * "TEXT", "Send On " + yMd + " At " + hms); // 台头二 yMd =
		 * StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		 * .getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日 hms =
		 * StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		 * .getInstance().getDBTime()), "HH:mm"); // 时分秒
		 * parm.setData("DESCRIPTION", "TEXT", ""); // 备注
		 * parm.setData("BILL_NO", "TEXT", ""); // 票据号 parm.setData("ONFEE_NO",
		 * "TEXT", bilBusinessNo); // 收据号 parm.setData("PRINT_DATE", "TEXT",
		 * yMd); // 打印时间 parm.setData("DATE", "TEXT", yMd + "    " + hms); // 日期
		 * parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		 * parm.setData("COPY", "TEXT", "(copy)"); // 补印注记 // ===zhangp 20120525
		 * start parm.setData("O", "TEXT", ""); //
		 * this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw", //
		 * parm,true);
		 * this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm,
		 * true); // ===zhangp 20120525 end
		 */
		// add by sunqy 20140611 ----start----
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		int row = table.getSelectedRow();// 得到选中行
		if (row < 0) {
			this.messageBox("没有选中数据!");
			return;
		}
		copy = "(copy)";
		String bilBusinessNo = table.getValueAt(row, 1).toString();
//		String sql = "SELECT A.*,B.PAT_NAME NAME,C.EKT_CARD_NO ACOUNT_NO "
//				+ "FROM EKT_BIL_PAY A, SYS_PATINFO B,EKT_ISSUELOG C "
//				+ "WHERE A.BIL_BUSINESS_NO = '" + bilBusinessNo
//				+ "' AND A.MR_NO = B.MR_NO";
//		// this.messageBox("支付方式=="+table.getValueAt(row, 4));
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result = getPrintData(bilBusinessNo);
		// EKTReceiptPrint print = new EKTReceiptPrint();
		boolean flg = false;
		result.setData("TITLE", "门诊充值收据");
		EKTReceiptPrintControl.getInstance().onPrint(table, result, copy, row,
				pat, flg, this);
		// print.onPrint(table, result, copy, row, pat, flg);
		// add by sunqy 20140611 ----end----
		// }
	}
	
	/**
	 * 获取数据，保存和补打都调用此方法，add by huangjw 20150104
	 * @param bilBusinessNo
	 * @return
	 */
	public TParm getPrintData(String bilBusinessNo){
		TParm resultParm=new TParm();
		String sql = "SELECT A.*,B.PAT_NAME NAME,C.EKT_CARD_NO ACOUNT_NO "
			+ "FROM EKT_BIL_PAY A, SYS_PATINFO B,EKT_ISSUELOG C "
			+ "WHERE A.BIL_BUSINESS_NO = '" + bilBusinessNo
			+ "' AND A.MR_NO = B.MR_NO"
		    + " AND A.CARD_NO=C.CARD_NO ";
	
		resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		resultParm = resultParm.getRow(0);
		return resultParm;
	}
	
	/**
	 *医疗卡充值记录table zhangp 20111228
	 */
	public void onTable() {
		String mrNo = getValueString("MR_NO");
		StringBuilder sql = new StringBuilder();
		String select = "SELECT B.CARD_NO,A.BIL_BUSINESS_NO,"
				+ "A.OPT_DATE,A.AMT,A.GATHER_TYPE,A.CREAT_USER,A.ACCNT_TYPE,A.CARD_TYPE,A.DESCRIPTION FROM EKT_BIL_PAY A, "
				+ "EKT_ISSUELOG B where A.CARD_NO = B.CARD_NO AND "
				+ "A.ACCNT_TYPE = '4'";
		sql.append(select);
		if (!mrNo.equals("") && mrNo != null) {
			sql.append(" AND A.MR_NO = '" + mrNo + "'");
		}
		sql.append(" ORDER BY A.OPT_DATE");
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		((TTable) getComponent("TABLE")).setParmValue(result);
	}

	/**
	 * 查询病患名称 =====zhangp 20120328
	 */
	public void onQueryPatName() {
		TParm sendParm = new TParm();
		sendParm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onQuery();
		// ====201202026 zhangp modify start
		grabFocus("TOP_UPFEE");
	}

}
