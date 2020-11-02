package com.javahis.ui.bil;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import jdo.bil.BILAccountTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import java.util.Vector;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPGE;

import jdo.bil.BILSysParmTool;
import jdo.ins.TJINSRecpTool;
import jdo.sys.SYSOperatorTool;

/**
 * <p> 
 * Title: סԺ�ս������
 * </p>
 *
 * <p>
 * Description: סԺ�ս������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author wangl 2009.09.19
 * @version 1.0
 */
public class BILAccountDailyControl extends TControl {
	String accountSeq = "";
	TParm accountSeqParm = new TParm();
	double reduceAmt=0;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// table���������¼�
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		TTable table = (TTable) this.getComponent("Table");
		// table����checkBox�¼�
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableComponent");
		initPage();
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		// ��ʼ��Ժ��
		setValue("REGION_CODE", Operator.getRegion());
		// ��ʼ����ѯ��ʱ,��ʱ
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		Timestamp today = SystemTool.getInstance().getDate();
		setValue("S_DATE", yesterday);
		setValue("E_DATE", today);
		setValue("ACCOUNT_DATE", today);
		String todayTime = StringTool.getString(today, "HH:mm:ss");
		String accountTime = todayTime;
		if (getAccountDate().length() != 0) {
			accountTime = getAccountDate();
			accountTime = accountTime.substring(0, 2) + ":"
					+ accountTime.substring(2, 4) + ":"
					+ accountTime.substring(4, 6);
		}
		// ========pangben modify 20120320 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20120320 stop
		setValue("ACCOUNT_TIME", accountTime);
		setValue("S_TIME", accountTime);
		setValue("E_TIME", accountTime);
		setValue("CASHIER_CODE", Operator.getID());

		// ���սᰴťΪ��
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		//===zhangp 201200502 start
//		callFunction("UI|ACCOUNT_TIME|setEnabled", true);
//		callFunction("UI|ACCOUNT_DATE|setEnabled", false);
		//===zhangp 201200502 end

	}

	public String getTime(String name) {
		String time = getText(name);
		if (time.length() != 8)
			return "";
		try {
			if (!checkTime(time.substring(0, 2), 23))
				return "";
			if (!checkTime(time.substring(3, 5), 59))
				return "";
			if (!checkTime(time.substring(6), 59))
				return "";
		} catch (Exception e) {
			return "";
		}
		return time.substring(0, 2) + time.substring(3, 5) + time.substring(6);
	}

	public boolean checkTime(String s, int max) {
		if (s.substring(0, 1).equals("0"))
			s = s.substring(1);
		int x = Integer.parseInt(s);
		return x >= 0 && x <= max;
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {

		String start = getTime("S_TIME");
		if (start.length() == 0) {
			messageBox("�����ѯ���յ�ʱ�䲻��ȷ!");
			return;
		}
		String end = getTime("E_TIME");
		if (end.length() == 0) {
			messageBox("�����ѯ���յ�ʱ�䲻��ȷ!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		TParm result = new TParm();
		TParm selAccountData = new TParm();
		selAccountData.setData("ACCOUNT_TYPE", "IBS");
		if (getValue("CASHIER_CODE").toString().length() > 0)
			selAccountData.setData("ACCOUNT_USER", getValue("CASHIER_CODE")
					.toString());
		selAccountData.setData("S_TIME", startTime + start);
		selAccountData.setData("E_TIME", endTime + end);
		selAccountData.setData("REGION_CODE", Operator.getRegion());
		result = BILAccountTool.getInstance().accountQuery(selAccountData);
		this.callFunction("UI|Table|setParmValue", result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();
		accountSeq = new String();
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (accountSeqParm.getCount("ACCOUNT_SEQ") <= 0) {
			messageBox("��ѡ���ӡ����!");
			return; 
		}
		if ("N".equals(getValue("TOGEDER_FLG"))) {
			int count = accountSeqParm.getCount("ACCOUNT_SEQ");
			TParm optNameParm = new TParm();
			String optName = "";
			for (int i = 0; i < count; i++) {
				optNameParm = SYSOperatorTool.getInstance().selectdata(
						accountSeqParm.getValue("ACCOUNT_USER", i));
				optName = optNameParm.getValue("USER_NAME", 0);
				//donglt  2016-3-30 modify
				print(accountSeqParm.getValue("ACCOUNT_SEQ", i),accountSeqParm.getValue("ACCOUNT_DATE", i), optName);
				//print(accountSeqParm.getValue("ACCOUNT_DATE",i), optName);
			}
			return;
		}
		if ("Y".equals(getValue("TOGEDER_FLG"))) {
			togederPrint();
			return;
		}
	}

	/**
	 * �ս�
	 */
	public void onSave() {
		//===zhangp 20120502 start
		TTextFormat text = (TTextFormat) getComponent("ACCOUNT_DATE");
		Timestamp time = (Timestamp) text.getValue();
		//===zhangp 20120502 end
		TParm parm = new TParm();
		parm.setData("CASHIER_CODE", getValue("CASHIER_CODE"));
		parm.setData("REGION_CODE", getValue("REGION_CODE"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		//===zhangp 20120502 start
		String date = StringTool.getString((Timestamp)text.getValue(), "yyyyMMdd")+getValueString("ACCOUNT_TIME").substring(0, 2)+
			getValueString("ACCOUNT_TIME").substring(3, 5)+getValueString("ACCOUNT_TIME").substring(6, 8);
		parm.setData("ACCOUNT_DATE", date);
		String accountTime = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyyMMddHHmmss");
		parm.setData("ACCOUNT_DATA", accountTime);
		//===zhangp 20120502 end
		TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
				"onSaveAcctionBIL", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			// �ս�ʧ��
//			this.messageBox("E0005");
			this.messageBox("���ս�����");
			return;
		}
		// �ս�ɹ�
		this.messageBox("P0005");
	}

	/**
	 * ���ñ����ӡԤ������
	 *
	 * @param accountSeq
	 *            String
	 * @param accountUser
	 *            String
	 */
	
	 public void print(String accountSeq, String date, String accountUser)
	  {
	    TParm result = new TParm();
	    DecimalFormat formatObject = new DecimalFormat("###########0.00");
	    String sysDate = StringTool.getString(SystemTool.getInstance()
	      .getDate(), "yyyy/MM/dd");
	    String sDate = 
	      StringTool.getString((Timestamp)getValue("S_DATE"), "yyyy/MM/dd") + 
	      " " + getValueString("S_TIME");

	    String eDate = 
	      StringTool.getString((Timestamp)getValue("E_DATE"), "yyyy/MM/dd") + 
	      " " + getValueString("E_TIME");

	    String printNoSQL = "select INV_NO from bil_invrcp where account_seq = '" + 
	      accountSeq + 
	      "' " + 
	      "and STATUS='0' AND RECP_TYPE = 'IBS' ORDER BY INV_NO";

	    TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
	      printNoSQL));

	    if (printNoParm.getErrCode() < 0) {
	      System.out.println("BILAccountDailyControl.print Err:" + 
	        printNoParm.getErrText());
	      return;
	    }

	    String selReceiptNo = " SELECT RECEIPT_NO,CHARGE_DATE,CASE_NO    FROM BIL_IBS_RECPM   WHERE ACCOUNT_SEQ IN (" + 
	      accountSeq + ") " + 
	      "  ORDER BY RECEIPT_NO";

	    TParm selReceiptNoParm = new TParm(TJDODBTool.getInstance().select(
	      selReceiptNo));
	    int receiptNoCount = selReceiptNoParm.getCount("RECEIPT_NO");
	    StringBuffer allReceiptNo = new StringBuffer();
	    StringBuffer allCaseNo = new StringBuffer();

	    for (int j = 0; j < receiptNoCount; j++) {
	      String seq = "";
	      seq = selReceiptNoParm.getValue("RECEIPT_NO", j);
	      if (!allReceiptNo.toString().contains(seq)) {
	        if (allReceiptNo.length() > 0)
	          allReceiptNo.append(",");
	        allReceiptNo.append("'").append(seq).append("'");
	      }

	      if (!allCaseNo.toString().contains(
	        selReceiptNoParm.getValue("CASE_NO", j))) {
	        if (allCaseNo.length() > 0)
	          allCaseNo.append(",");
	        allCaseNo.append("'").append(selReceiptNoParm.getValue("CASE_NO", j)).append("'");
	      }
	    }
	    String allReceiptNoStr = allReceiptNo.toString();
	    String selRecpD = " SELECT REXP_CODE,SUM(WRT_OFF_AMT) WRT_OFF_AMT   FROM BIL_IBS_RECPD   WHERE RECEIPT_NO IN (" + 
	      allReceiptNoStr + ") GROUP BY REXP_CODE";

	    TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
	      .select(selRecpD));
	    TParm chargeParm = chargeDouble(selRecpDParm, formatObject);
	    TParm charge = new TParm();
	    charge.setData("accountSeq", accountSeq);

	    charge.setData("ACCOUNT_DATE", date);

	    String stardate = selReceiptNoParm.getData("CHARGE_DATE", 0).toString();
	    String enddate = selReceiptNoParm.getData("CHARGE_DATE", 
	      selReceiptNoParm.getCount() - 1).toString();
	    stardate = stardate.substring(0, 19).replaceAll("-", "/");
	    enddate = enddate.substring(0, 19).replaceAll("-", "/");
	    charge.setData("S_DATE", stardate + " �� " + enddate);

	    charge.setData("PRINT_DATE", sysDate);
	    charge.setData("CASHIER_CODE", accountUser);

	    charge.setData("patCount", Integer.valueOf(getPatCount(accountSeq)));

	    charge.setData("writeOffFee", formatObject
	      .format(-getPayCX(accountSeq)));
	    charge
	      .setData("backWashFee", formatObject
	      .format(getPayHC(accountSeq)));
	    charge
	      .setData("fillFee", formatObject
	      .format(getPatFee(accountSeq)[0]));
	    charge.setData("returnFee", formatObject
	      .format(getPatFee(accountSeq)[1]));

	    charge.setData("fillFeeCash", formatObject
	      .format(getPatFee(accountSeq)[3]));
	    charge.setData("fillFeeCheck", formatObject
	      .format(getPatFee(accountSeq)[4]));
	    charge.setData("returnFeeCash", formatObject
	      .format(getPatFee(accountSeq)[5]));
	    charge.setData("returnFeeCheck", formatObject.format(getPatFee(accountSeq)[6]));
	    charge.setData("fillCard", formatObject.format(getPatFee(accountSeq)[7]));
	    charge.setData("fillDebit", formatObject.format(getPatFee(accountSeq)[8]));
	    charge.setData("fillGift", formatObject.format(getPatFee(accountSeq)[9]));
	    charge.setData("fillDiscont", formatObject.format(getPatFee(accountSeq)[10]));
	    charge.setData("returnCard", formatObject.format(getPatFee(accountSeq)[11]));
	    charge.setData("returnDebit", formatObject.format(getPatFee(accountSeq)[12]));
	    charge.setData("returnGift", formatObject.format(getPatFee(accountSeq)[13]));
	    charge.setData("returnDiscont", formatObject.format(getPatFee(accountSeq)[14]));
	    charge.setData("fillBxzf", formatObject.format(getPatFee(accountSeq)[15]));
	    charge.setData("returnBxzf", formatObject.format(getPatFee(accountSeq)[16]));
	    charge.setData("fillWx", formatObject.format(getPatFee(accountSeq)[17]));
	    charge.setData("returnWx", formatObject.format(getPatFee(accountSeq)[18]));
	    charge.setData("fillZfb", formatObject.format(getPatFee(accountSeq)[19]));
	    charge.setData("returnZfb", formatObject.format(getPatFee(accountSeq)[20]));
	    charge.setData("fillMedical", formatObject.format(getPatFee(accountSeq)[21]));
	    charge.setData("returnMedical", formatObject.format(getPatFee(accountSeq)[22]));

	    charge.setData("debitFee", "0.00");
	    charge.setData("ownFee", formatObject.format(getPatFee(accountSeq)[2]));
	    charge.setData("arAmt", formatObject.format(chargeParm.getDouble("AR_AMT")));
	    String tot = formatObject.format(chargeParm.getDouble("AR_AMT"));
	    String tmp = StringUtil.getInstance().numberToWord(StringTool.getDouble(tot));
	    if (tmp.lastIndexOf("��") > 0) {
	      tmp = tmp.substring(0, tmp.lastIndexOf("��") + 1);
	    }

	    charge.setData("zhAmt", tmp);
	    charge.setData("OPT_USER", Operator.getName());

	    TParm insParm = getInsParm(accountSeq);
	    if (insParm.getErrCode() < 0) {
	      return;
	    }

	    double armyAi_amt = insParm.getDouble("ARMYAI_AMT");
	    double account_pay_amt = insParm.getDouble("ACCOUNT_PAY_AMT");
	    double nhi_comment = insParm.getDouble("NHI_COMMENT");
	    double nhi_pay = insParm.getDouble("NHI_PAY");
	    double ins = armyAi_amt + nhi_comment + nhi_pay;
	    double pay_cash = insParm.getDouble("OWN_PAY");
	    charge.setData("nhiComment", Double.valueOf(StringTool.round(nhi_comment, 2)));

	    charge.setData("accountPayAmt", Double.valueOf(StringTool.round(account_pay_amt, 2)));

	    charge.setData("debitFee", Double.valueOf(StringTool.round(nhi_pay, 2)));

	    String selRecpNo = "SELECT START_INVNO,END_INVNO FROM BIL_INVOICE WHERE RECP_TYPE = 'IBS'";
	    TParm invNoParm = new TParm(TJDODBTool.getInstance().select(selRecpNo));
	    String recp_no = "";
	    for (int i = 0; i < invNoParm.getCount(); i++) {
	      TParm p = new TParm();
	      String startNo = invNoParm.getData("START_INVNO", i).toString();
	      String endNo = invNoParm.getData("END_INVNO", i).toString();

	      for (int j = 0; j < printNoParm.getCount("INV_NO"); j++) {
	        String invNo = printNoParm.getData("INV_NO", j).toString();
	        if ((invNo.compareTo(startNo) >= 0) && 
	          (invNo.compareTo(endNo) <= 0)) {
	          p.addData("INV_NO", invNo);
	        }
	      }
	      if (p.getCount("INV_NO") > 1)
	      {
	        if (!recp_no.contains(p.getValue("INV_NO", 0)))
	        {
	          recp_no = recp_no + "," + p.getData("INV_NO", 0) + " ~ " + 
	            p.getData("INV_NO", p.getCount("INV_NO") - 1);
	        }
	      } else if ((p.getCount("INV_NO") > 0) && (p.getCount("INV_NO") <= 1) && 
	        (!recp_no.contains(p.getValue("INV_NO", 0))))
	      {
	        recp_no = recp_no + "," + p.getData("INV_NO", 0);
	      }

	    }

	    if (recp_no.length() > 0) {
	      recp_no = recp_no.substring(1, recp_no.length());
	    }

	    charge.setData("PRINT_NO", recp_no);

	    TParm print = new TParm();

	    String sql = " SELECT SUM(PAY_CASH) PAY_CASH    FROM BIL_IBS_RECPM   WHERE ACCOUNT_SEQ IN (" + 
	      accountSeq + ") " + 
	      "    AND AR_AMT <> 0 ";
	    TParm payWayParm = new TParm(TJDODBTool.getInstance().select(sql));
	    print.setData("fillFeeCash", "TEXT", "�ֽ�: " + 
	      formatObject.format(payWayParm.getDouble("PAY_CASH", 0)));

	    TParm pCharge = onPrintPackData(accountSeq, allReceiptNoStr);
	    TParm printCharge = new TParm();
	    for (int i = 1; i < 24; i++) {
	      if (i < 10) {
	        printCharge.setData("CHARGE0" + i, chargeParm
	          .getData("CHARGE0" + i) == null ? Double.valueOf(0.0D) : formatObject
	          .format(chargeParm.getDouble("CHARGE0" + i)));

	        charge.setData("PCHARGE0" + i, pCharge
	          .getData("CHARGE0" + i) == null ? Double.valueOf(0.0D) : formatObject
	          .format(pCharge.getDouble("CHARGE0" + i)));
	      } else {
	        printCharge.setData("CHARGE" + i, chargeParm.getData("CHARGE" + 
	          i) == null ? Double.valueOf(0.0D) : formatObject.format(chargeParm
	          .getDouble("CHARGE" + i)));

	        charge.setData("PCHARGE" + i, pCharge.getData("CHARGE" + 
	          i) == null ? Double.valueOf(0.0D) : formatObject.format(pCharge
	          .getDouble("CHARGE" + i)));
	      }

	    }

	    printCharge.setData("CHARGE00", formatObject.format(chargeParm
	      .getDouble("CHARGE03") + 
	      chargeParm.getDouble("CHARGE04")));

	    charge.setData("PCHARGE03AND04", formatObject.format(pCharge
	      .getDouble("CHARGE03") + 
	      pCharge.getDouble("CHARGE04")));
	    charge.setData("PARAMT", formatObject.format(pCharge
	      .getDouble("PARAMT")));
	    charge.setData("PARAMTBACK", formatObject.format(pCharge
	      .getDouble("PARAMTBACK")));
	    charge.setData("PARAMTTOT", formatObject.format(pCharge
	      .getDouble("PARAMTTOT")));
	    charge.setData("PCHARGEAMT", formatObject.format(pCharge
	      .getDouble("AR_AMT")));

	    printCharge.setData("totAmt", formatObject.format(chargeParm.getDouble("AR_AMT")));

	    String sqlSum = "SELECT SUM (B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) TOT_AMT FROM BIL_IBS_RECPM B  WHERE B.AR_AMT<>0  AND B.ACCOUNT_SEQ IN (" + 
	      accountSeq + ")";
	    String sqlLUMP = "SELECT SUM (LUMPWORK_AMT) LUMPWORK_AMT,SUM (LUMPWORK_OUT_AMT) LUMPWORK_OUT_AMT,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM WHERE ACCOUNT_SEQ IN (" + 
	      accountSeq + ") ";

	    String sqlAR = "SELECT SUM (AR_AMT) AR_AMT,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM WHERE AR_AMT <> 0 AND (LUMPWORK_AMT = 0.00 OR LUMPWORK_AMT IS NULL ) AND ACCOUNT_SEQ IN (" + 
	      accountSeq + ") ";
	    String sqlLump = "SELECT SUM (LUMPWORK_AMT) + SUM (LUMPWORK_OUT_AMT) LUMP,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM WHERE AR_AMT <> 0 AND LUMPWORK_AMT <> 0  AND ACCOUNT_SEQ IN (" + 
	      accountSeq + ") ";
	    TParm dataAR = new TParm(TJDODBTool.getInstance().select(sqlAR));
	    TParm dataLump = new TParm(TJDODBTool.getInstance().select(sqlLump));
	    double lumReduceAmt = 0.0D;
	    if ((dataLump.getCount() > 0) && (dataLump.getData("REDUCE_AMT", 0) != null)) {
	      lumReduceAmt = dataLump.getDouble("REDUCE_AMT", 0);
	    }
	    printCharge.setData("totAmt", formatObject.format(
	      dataAR.getDouble("AR_AMT", 0) - dataAR.getDouble("REDUCE_AMT", 0) + 
	      dataLump.getDouble("LUMP", 0) - lumReduceAmt));
	    if ((dataAR.getDouble("REDUCE_AMT", 0) != 0.0D) || (lumReduceAmt != 0.0D)) {
	      printCharge.setData("reduceAmt", "������:" + formatObject.format(
	        dataAR.getDouble("REDUCE_AMT", 0) + lumReduceAmt) + "Ԫ");
	      this.reduceAmt = (dataAR.getDouble("REDUCE_AMT", 0) + lumReduceAmt);
	    }

	    TParm dataSum = new TParm(TJDODBTool.getInstance().select(sqlSum));
	    TParm lumpParm = new TParm(TJDODBTool.getInstance().select(sqlLUMP));
	    String lumpWork = "";
	    if ((lumpParm.getDouble("LUMPWORK_AMT", 0) != 0.0D) && 
	      (lumpParm.getCount("LUMPWORK_AMT") > 0)) {
	      lumpWork = lumpWork + "���ڽ�ת:" + formatObject.format(lumpParm
	        .getDouble("LUMPWORK_AMT", 0)) + "Ԫ";

	      if (this.reduceAmt != 0.0D)
	        lumpWork = lumpWork + ";�����Ը�:" + formatObject.format(lumpParm
	          .getDouble("LUMPWORK_OUT_AMT", 0) - (dataAR.getDouble("REDUCE_AMT", 0) + lumReduceAmt)) + "Ԫ";
	      else {
	        lumpWork = lumpWork + ";�����Ը�:" + formatObject.format(lumpParm
	          .getDouble("LUMPWORK_OUT_AMT", 0)) + "Ԫ";
	      }

	      if (lumpParm.getDouble("REDUCE_AMT", 0) > 0.0D) {
	        lumpWork = lumpWork + ";�ײͼ���:" + formatObject.format(lumpParm
	          .getDouble("REDUCE_AMT", 0)) + "Ԫ";
	      }

	    }

	    charge.setData("lumpworkFee", lumpWork);
	    printCharge.setData("initAmt", formatObject.format(
	      dataSum.getDouble("TOT_AMT", 0)));

	    print.setData("CHARGE", charge.getData());
	    print.setData("PRINT_CHARGE", printCharge.getData());

	    TParm printCancleDate = getPrintCancelTableDate("'" + accountSeq + "'");
	    TParm printReturnDate = getPrintReturnTableDate("'" + accountSeq + "'");
	    TParm printChangeDate = getChangeTableDate("'" + accountSeq + "'");
	    print.setData("cancelTable", printCancleDate.getData());
	    print.setData("returnFeeTable", printReturnDate.getData());
	    print.setData("changeTable", printChangeDate.getData());

	    openPrintWindow(IReportTool.getInstance().getReportPath("BILAccount_V45.jhw"), 
	      IReportTool.getInstance().getReportParm("BILAccount.class", print));
	  }
	/**
	 * ��ӡԤ��(�ս�֮ǰ��Ԥ������)
	 * yanjing 20141022
	 */
	public void onPrintReview(){
		 TParm returnParm = new TParm();
		 String recpType = "IBS";
		 String cashierCode = getValueString("CASHIER_CODE");
		 returnParm.setData("PRINTUSERS", cashierCode);
		 String admType = "I";
		 TTextFormat text = (TTextFormat) getComponent("ACCOUNT_DATE");
			Timestamp time = (Timestamp) text.getValue();
			String chargeDate = StringTool.getString((Timestamp)text.getValue(), "yyyyMMdd")+getValueString("ACCOUNT_TIME").substring(0, 2)+
			getValueString("ACCOUNT_TIME").substring(3, 5)+getValueString("ACCOUNT_TIME").substring(6, 8);
		 DecimalFormat formatObject = new DecimalFormat("###########0.00");
			String sysDate = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyy/MM/dd");
			String printNoSQL = "SELECT INV_NO FROM BIL_INVRCP "+ 
				 " WHERE RECP_TYPE = '"+recpType+"' " +
				 "AND CASHIER_CODE='"+cashierCode+"'" +
				 "AND ADM_TYPE = '"+admType+"'" +
				 "AND PRINT_DATE<TO_DATE('"+chargeDate+"','YYYYMMDDHH24MISS')" +
				 "AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) "+
				 "AND STATUS='0' AND RECP_TYPE = 'IBS' ORDER BY INV_NO";
		// ===zhangp 20120305 modify end
		TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
				printNoSQL));
//		System.out.println("printNoParm==="+printNoParm);
		if (printNoParm.getErrCode() < 0) {
			System.out.println("BILAccountDailyControl.print Err:"
					+ printNoParm.getErrText());
			return;
		}
		String selReceiptNo = " SELECT RECEIPT_NO,CHARGE_DATE,CASE_NO "
			+ "   FROM BIL_IBS_RECPM " + "  WHERE  CASHIER_CODE = '"+cashierCode+"'" +
					"AND  ACCOUNT_FLG IS NULL" +
					" AND  CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')"+
			// "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
			// +
			"  ORDER BY RECEIPT_NO";
//		System.out.println("++++++selReceiptNo selReceiptNo is ::"+selReceiptNo);
		
	// �վݺ���
	TParm selReceiptNoParm = new TParm(TJDODBTool.getInstance().select(
			selReceiptNo));
	if(selReceiptNoParm.getCount()<=0){
		this.messageBox("û��Ԥ��������");
		return;
	}
	int receiptNoCount = selReceiptNoParm.getCount("RECEIPT_NO");
	StringBuffer allReceiptNo = new StringBuffer();
	StringBuffer allCaseNo = new StringBuffer();// ��ѯCASE_NO pangben
												// 2012-3-19
	for (int j = 0; j < receiptNoCount; j++) {
		String seq = "";
		seq = selReceiptNoParm.getValue("RECEIPT_NO", j);
		if (allReceiptNo.length() > 0)
			allReceiptNo.append(",");
		allReceiptNo.append(seq);
		// ���case_no pangben 2012-3-19
		if (!allCaseNo.toString().contains(
				selReceiptNoParm.getValue("CASE_NO", j))) {
			if (allCaseNo.length() > 0)
				allCaseNo.append(",");
			allCaseNo.append(selReceiptNoParm.getValue("CASE_NO", j));
		}
	}
	String allReceiptNoStr = allReceiptNo.toString();
	String selRecpD = " SELECT REXP_CODE,SUM(WRT_OFF_AMT) WRT_OFF_AMT"
			+ "   FROM BIL_IBS_RECPD " + "  WHERE RECEIPT_NO IN ("
			+ allReceiptNoStr + ") GROUP BY REXP_CODE";
	// ��ѯ��֧ͬ����ʽ������(�ս���)
	TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
			.select(selRecpD));
	TParm chargeParm =chargeDouble(selRecpDParm, formatObject);
	TParm charge = new TParm();
//	charge.setData("accountSeq", accountSeq);
	// =======zhangp 20120302 modify start
	String stardate = selReceiptNoParm.getData("CHARGE_DATE", 0).toString();
	String enddate = selReceiptNoParm.getData("CHARGE_DATE",
			selReceiptNoParm.getCount() - 1).toString();
	stardate = stardate.substring(0, 19).replaceAll("-", "/");//==liling 20140710 modify
	enddate = enddate.substring(0, 19).replaceAll("-", "/");//==liling 20140710 modify
	charge.setData("S_DATE", stardate + " �� " + enddate);
	// charge.setData("E_DATE", eDate);
	// ======zhangp 20120302 modify end
	charge.setData("PRINT_DATE", sysDate);
	charge.setData("CASHIER_CODE", cashierCode);
	// System.out.println("��������"+accountSeq);
	charge.setData("patCount", getRePatCount(cashierCode,chargeDate));
	// System.out.println("�������"+getPayCX(accountSeq));
	charge.setData("writeOffFee", formatObject
			.format(-getRePayCX(cashierCode,chargeDate)));
	charge
			.setData("backWashFee", formatObject
					.format(getRePayHC(cashierCode,chargeDate)));
	charge
			.setData("fillFee", formatObject
					.format(getRePatFee(cashierCode,chargeDate)[0]));
	charge.setData("returnFee", formatObject
			.format(getRePatFee(cashierCode,chargeDate)[1]));
	//====zhangp 20120514 start
	charge.setData("fillFeeCash", formatObject
			.format(getRePatFee(cashierCode,chargeDate)[3]));
	charge.setData("fillFeeCheck", formatObject
			.format(getRePatFee(cashierCode,chargeDate)[4]));
	charge.setData("returnFeeCash", formatObject
			.format(getRePatFee(cashierCode,chargeDate)[5]));
	charge.setData("returnFeeCheck", formatObject.format(getRePatFee(cashierCode,chargeDate)[6]));
	charge.setData("fillCard",formatObject.format(getRePatFee(cashierCode,chargeDate)[7]));
	charge.setData("fillDebit",formatObject.format(getRePatFee(cashierCode,chargeDate)[8]));
	charge.setData("fillGift",formatObject.format(getRePatFee(cashierCode,chargeDate)[9]));
	charge.setData("fillDiscont",formatObject.format(getRePatFee(cashierCode,chargeDate)[10]));
	charge.setData("returnCard",formatObject.format(getRePatFee(cashierCode,chargeDate)[11]));
	charge.setData("returnDebit",formatObject.format(getRePatFee(cashierCode,chargeDate)[12]));
	charge.setData("returnGift",formatObject.format(getRePatFee(cashierCode,chargeDate)[13]));
	charge.setData("returnDiscont",formatObject.format(getRePatFee(cashierCode,chargeDate)[14]));
	charge.setData("fillBxzf",formatObject.format(getRePatFee(cashierCode,chargeDate)[15]));  //add by huangtt 20150519
	charge.setData("returnBxzf",formatObject.format(getRePatFee(cashierCode,chargeDate)[16])); //add by huangtt 20150519
	charge.setData("fillWx",formatObject.format(getRePatFee(cashierCode,chargeDate)[17]));  //add by kangyue 20160828
	charge.setData("returnWx",formatObject.format(getRePatFee(cashierCode,chargeDate)[18])); //add by kangyue 20160828
	charge.setData("fillZfb",formatObject.format(getRePatFee(cashierCode,chargeDate)[19]));  //add by kangyue 20160828
	charge.setData("returnZfb",formatObject.format(getRePatFee(cashierCode,chargeDate)[20])); //add by kangyue 20160828
	charge.setData("fillMedical",formatObject.format(getRePatFee(cashierCode,chargeDate)[21]));  //add by kangyue 20171205
	charge.setData("returnMedical",formatObject.format(getRePatFee(cashierCode,chargeDate)[22])); //add by kangyue 20171205
	
	//====zhangp 20120514 end
	charge.setData("debitFee", "0.00");
	charge.setData("ownFee", formatObject.format(getRePatFee(cashierCode,chargeDate)[2]));
	charge.setData("arAmt", formatObject.format(chargeParm.getDouble("AR_AMT")));
	String tot=formatObject.format(chargeParm.getDouble("AR_AMT"));
   String tmp=StringUtil.getInstance().numberToWord(StringTool.getDouble(tot));
   if(tmp.lastIndexOf("��")>0){//�з�ȥ������
	   tmp=tmp.substring(0,tmp.lastIndexOf("��")+1);
	  
   }
	charge.setData("zhAmt", tmp);//modify by caoyong 2013722
	charge.setData("OPT_USER", Operator.getName());
	
	// ========pangben 2012-3-19 stop
	String selRecpNo = "SELECT START_INVNO,END_INVNO FROM BIL_INVOICE WHERE RECP_TYPE = 'IBS'";
	TParm invNoParm = new TParm(TJDODBTool.getInstance().select(selRecpNo));
	String recp_no = "";
	for (int i = 0; i < invNoParm.getCount(); i++) {
		TParm p = new TParm();
		String startNo = invNoParm.getData("START_INVNO", i).toString();
		String endNo = invNoParm.getData("END_INVNO", i).toString();
		// System.out.println(startNo+"                   "+endNo);
		for (int j = 0; j < printNoParm.getCount("INV_NO"); j++) {
			String invNo = printNoParm.getData("INV_NO", j).toString();
			if (invNo.compareTo(startNo) >= 0
					&& invNo.compareTo(endNo) <= 0) {
				p.addData("INV_NO", invNo);
			}
		}
		if (p.getCount("INV_NO") > 1) {
			//int n=p.getData("INV_NO", 0).toString().length();
			//if(recp_no.length()>n+1&&recp_no.substring(1,n+1 ).equals(p.getData("INV_NO", 0)))continue;
			if (recp_no.contains(p.getValue("INV_NO", 0))) {
				continue;
			}
			recp_no += "," + p.getData("INV_NO", 0) + " ~ "
					+ p.getData("INV_NO", p.getCount("INV_NO") - 1);
		}
		if (p.getCount("INV_NO") > 0 && p.getCount("INV_NO") <= 1) {
			if (recp_no.contains(p.getValue("INV_NO", 0))) {
				continue;
			}
			recp_no += "," + p.getData("INV_NO", 0);//==liling 20140710 add +=
//			System.out.println("recp_no=qqq=="+recp_no);
		}
//		System.out.println("===p====="+i+"===="+p);
	}
	if (recp_no.length() > 0) {
		recp_no = recp_no.substring(1, recp_no.length());
	}
//	System.out.println("recp_no==="+recp_no);
	
	charge.setData("PRINT_NO", recp_no);
	// charge.setData("PRINT_NO",
	// getPrintNO( (Vector) printNoParm.getData("INV_NO")));
	// ===zhangp 20120305 modify end
	// System.out.println("�ս�����"+charge);
	TParm print = new TParm();
	// ===zhangp 20120308 modify start
	String sql = " SELECT SUM(PAY_CASH) PAY_CASH "
			+ "   FROM BIL_IBS_RECPM " + "  WHERE CASHIER_CODE = '"+cashierCode+"'" +
					"AND  ACCOUNT_FLG IS NULL" +
					" AND  CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')"+
			// "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
			// +
			"    AND REFUND_DATE IS NULL "
			+ "    AND AR_AMT <>0 " +
					"ORDER BY RECEIPT_NO";
	TParm payWayParm = new TParm(TJDODBTool.getInstance().select(sql));
	print.setData("fillFeeCash", "TEXT", "�ֽ�: "
			+ formatObject.format(payWayParm.getDouble("PAY_CASH", 0)));
	TParm PCharge = onPrintPackDataPreview(cashierCode,chargeDate);//�ײ���Ŀ��ϸ-xiongwg20150713
	TParm printCharge=new TParm();
	for (int i = 1; i < 24; i++) {
		if (i < 10) {
			printCharge.setData("CHARGE0" + i, chargeParm
					.getData("CHARGE0" + i) == null ? 0.00 : formatObject
					.format(chargeParm.getDouble("CHARGE0" + i)));
			//�ײ�ʹ��
			charge.setData("PCHARGE0" + i, PCharge
					.getData("CHARGE0" + i) == null ? 0.00 : formatObject
							.format(PCharge.getDouble("CHARGE0" + i)));
		} else {
			printCharge.setData("CHARGE" + i, chargeParm.getData("CHARGE"
					+ i) == null ? 0.00 : formatObject.format(chargeParm
					.getDouble("CHARGE" + i)));
			//�ײ�ʹ��
			charge.setData("PCHARGE" + i, PCharge
					.getData("CHARGE" + i) == null ? 0.00 : formatObject
							.format(PCharge.getDouble("CHARGE" + i)));
		}
	}
//	print.setData("CHARGE07","TEXT",formatObject.format(chargeParm
//			.getDouble("CHARGE07")+ chargeParm.getDouble("CHARGE16")));//����
	printCharge.setData("CHARGE00", formatObject.format(chargeParm
			.getDouble("CHARGE03")
			+ chargeParm.getDouble("CHARGE04")));
	//�ײ�ʹ��
	charge.setData("PCHARGE03AND04", formatObject.format(PCharge
			.getDouble("CHARGE03")
			+ PCharge.getDouble("CHARGE04")));
	charge.setData("PARAMT", formatObject.format(PCharge
			.getDouble("PARAMT")));
	charge.setData("PARAMTBACK", formatObject.format(PCharge
			.getDouble("PARAMTBACK")));
	charge.setData("PARAMTTOT", formatObject.format(PCharge
			.getDouble("PARAMTTOT")));
	charge.setData("PCHARGEAMT", formatObject.format(PCharge
			.getDouble("AR_AMT")));
	
	printCharge.setData("totAmt", formatObject.format(chargeParm.getDouble("AR_AMT")));
	// ===zhangp 20120308 modify end
	
		// ����Ӧ�ս������ڽ�ת�������Ը�--xiongwg20150320 start
		String sqlSum = "SELECT SUM (B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) TOT_AMT "
				+" FROM BIL_IBS_RECPM B "
				+ " WHERE B.AR_AMT<>0 " 
				+ " AND B.CASHIER_CODE = '"
				+ cashierCode
				+ "' "
				+ " AND  B.ACCOUNT_FLG IS NULL"
				+ " AND  B.CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')";
		String sqlLUMP = "SELECT SUM (LUMPWORK_AMT) LUMPWORK_AMT,SUM (LUMPWORK_OUT_AMT) LUMPWORK_OUT_AMT,SUM(REDUCE_AMT) REDUCE_AMT " +
				" FROM BIL_IBS_RECPM WHERE CASHIER_CODE = '"
				+ cashierCode
				+ "'"
				+ "AND  ACCOUNT_FLG IS NULL"
				+ " AND  CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND REFUND_DATE IS NULL ";
		
		TParm dataSum = new TParm(TJDODBTool.getInstance().select(sqlSum));
		TParm lumpParm = new TParm(TJDODBTool.getInstance().select(sqlLUMP));
		
		
		// ʵ�ս����� start
		String sqlAR = "SELECT SUM (AR_AMT) AR_AMT,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM WHERE AR_AMT <> 0 AND (LUMPWORK_AMT = 0.00 OR LUMPWORK_AMT IS NULL )"
				+ " AND CASHIER_CODE = '"
				+ cashierCode
				+ "'"
				+ "AND  ACCOUNT_FLG IS NULL"
				+ " AND  CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND REFUND_DATE IS NULL ";
		String sqlLump = "SELECT SUM (LUMPWORK_AMT) + SUM (LUMPWORK_OUT_AMT) LUMP ,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM"
				+ " WHERE AR_AMT <> 0 AND LUMPWORK_AMT <> 0 "
				+ " AND CASHIER_CODE = '"
				+ cashierCode
				+ "'"
				+ "AND  ACCOUNT_FLG IS NULL"
				+ " AND  CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND REFUND_DATE IS NULL ";
		TParm dataAR = new TParm(TJDODBTool.getInstance().select(sqlAR));
		TParm dataLump = new TParm(TJDODBTool.getInstance().select(sqlLump));	
		double lumReduceAmt=0.00;
		if(dataLump.getCount()>0&&null!=dataLump.getData("REDUCE_AMT", 0)){
			lumReduceAmt=dataLump.getDouble("REDUCE_AMT", 0);
		}
		printCharge.setData("totAmt", formatObject.format(
				dataAR.getDouble("AR_AMT",0)-dataAR.getDouble("REDUCE_AMT",0)+
						dataLump.getDouble("LUMP", 0)-lumReduceAmt));//ʵ�ս��
		if (dataAR.getDouble("REDUCE_AMT",0)!=0||lumReduceAmt!=0) {
			printCharge.setData("reduceAmt", "������:"+formatObject.format(
					dataAR.getDouble("REDUCE_AMT",0)+lumReduceAmt)+"Ԫ");//������
		}
		// ʵ�ս����� end
		printCharge.setData("initAmt", formatObject.format(dataSum.getDouble(
				"TOT_AMT", 0)));
		
		
		
		
		String lumpWork = "";
//		System.out.println("lumpParm::"+lumpParm);
		if (lumpParm.getDouble("LUMPWORK_AMT", 0) != 0
				&& lumpParm.getCount("LUMPWORK_AMT") > 0) {
			lumpWork += "���ڽ�ת:"
					+ formatObject
							.format(lumpParm.getDouble("LUMPWORK_AMT", 0))
					+ "Ԫ";// ���ڽ�ת���ײͽ��
			//=== start== modify by kangy 20161009 �����Ը��Ľ��ӦΪ��ȥ����������ֵ
			if(reduceAmt!=0){
				lumpWork +=";�����Ը�:"+formatObject.format(lumpParm.
						getDouble("LUMPWORK_OUT_AMT",0)-(dataAR.getDouble("REDUCE_AMT",0)+lumReduceAmt))+"Ԫ";
			}else{
				lumpWork +=";�����Ը�:"+formatObject.format(lumpParm.
						getDouble("LUMPWORK_OUT_AMT",0))+"Ԫ";
			}
			//=== end== modify by kangy 20161009 �����Ը��Ľ��ӦΪ��ȥ����������ֵ
			/*lumpWork += ";�����Ը�:"
					+ formatObject.format(lumpParm.getDouble(
							"LUMPWORK_OUT_AMT", 0)) + "Ԫ";// ��ѯ��������ܽ��
*/			if(lumpParm.getDouble("REDUCE_AMT",0)>0){
				lumpWork += ";�ײͼ���:"+ formatObject.format(lumpParm.getDouble(
							"REDUCE_AMT", 0)) + "Ԫ";
			}
		}
		// print.setData("lumpworkFee", lumpWork);
		// lumpWork += "==================================";
		charge.setData("lumpworkFee", lumpWork);
		// ����Ӧ�ս������ڽ�ת�������Ը�--xiongwg20150320 end
		
	
		
		
		
	
	print.setData("CHARGE", charge.getData());
	print.setData("PRINT_CHARGE", printCharge.getData());
	//=====pangben 2015-8-25 ���Ʊ����ʾ�˷ѡ����ϡ�����Ʊ������
	TParm printCancleDate = this.getPrintCancelTableDateReview(cashierCode,chargeDate);
    TParm printReturnDate = this.getPrintReturnTableDateReview(cashierCode,chargeDate);
    TParm printChangeDate = this.getChangeTableDateReview(cashierCode,chargeDate);
    print.setData("cancelTable", printCancleDate.getData());
	print.setData("returnFeeTable", printReturnDate.getData());
	print.setData("changeTable", printChangeDate.getData());
	// System.out.println("print======="+print);
//	this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILAccount.jhw", print);
//	System.out.println("dayin print======"+print);
	this.openPrintWindow(IReportTool.getInstance().getReportPath("BILAccount_V45.jhw"),
                         IReportTool.getInstance().getReportParm("BILAccount.class", print));//����ϲ�modify by wanglong 20130
		 
		 
		
	}
        /**
         * ���ҽ������ ===============pangben 2012-3-19
         * @param accountSeq String
         * @return TParm
         */
        private TParm getInsParm(String accountSeq) {
//		String todayTime = StringTool.getString(SystemTool.getInstance()
//				.getDate(), "yyyyMM");
//		//====zhangp 20120424 start
////		String sql = "SELECT SUM(NHI_COMMENT) AS NHI_COMMENT,SUM(ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT "
////				+ "FROM  INS_IBS WHERE  YEAR_MON='"
////				+ todayTime
////				+ "' AND "
////				+ " CASE_NO IN (" + allCaseNo + ")";
//		//===zhangp 20120606 start
////		String sql = "SELECT CONFIRM_NO,CASE_NO FROM INS_ADM_CONFIRM WHERE CASE_NO IN (" + allCaseNo + ") " +
//		String sql = "SELECT CONFIRM_NO,CASE_NO FROM INS_ADM_CONFIRM WHERE CASE_NO IN " +
//				"(" +
//				" SELECT CASE_NO FROM BIL_IBS_RECPM WHERE CASE_NO IN (" + allCaseNo + ") AND REFUND_FLG IS NULL " +
//						" AND AR_AMT > 0" +
//						")" +
//						" AND CONFIRM_NO NOT LIKE 'KN%'" +
//		//===zhangp 20120606 end
//		//===ZHANGP 20120530 start
//				" AND IN_STATUS <> '5'";
//		//===ZHANGP 20120530 end
//        TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if(confirmParm.getErrCode()<0){
//        	return confirmParm;
//        }
//        if(confirmParm.getCount()<0){
//        	return confirmParm;
//        }
//        String confirmnos = "";
////        (a.confirm_no ='08081204055209' and b.case_no = '120405000137') or
//        for (int i = 0; i < confirmParm.getCount(); i++) {
//        	confirmnos += " (A.CONFIRM_NO ='"+confirmParm.getData("CONFIRM_NO", i)+"' AND " +
//        			"B.CASE_NO = '"+confirmParm.getData("CASE_NO", i)+"') OR";
//		}
//        confirmnos = confirmnos.substring(0, confirmnos.length()-2);
//        System.out.println(confirmnos);
//        sql = 
//        	"SELECT SUM (INS.INSBASE_LIMIT_BALANCE) INSBASE_LIMIT_BALANCE," +
//        	" SUM (INS.INS_LIMIT_BALANCE) INS_LIMIT_BALANCE," +
//        	" SUM (INS.RESTART_STANDARD_AMT) RESTART_STANDARD_AMT," +
//        	" SUM (INS.REALOWN_RATE) REALOWN_RATE, SUM (INS.INSOWN_RATE) INSOWN_RATE," +
//        	" SUM (INS.ARMYAI_AMT) ARMYAI_AMT, SUM (INS.NHI_PAY) NHI_PAY," +
//        	" SUM (INS.NHI_COMMENT) NHI_COMMENT, SUM (INS.OWN_PAY) OWN_PAY," +
//        	" SUM (INS.AR_AMT) AR_AMT, SUM (INS.ACCOUNT_PAY_AMT) ACCOUNT_PAY_AMT" +
//        	" FROM (SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
//        	" B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE," +
//        	" B.ARMYAI_AMT," +
//        	" (  CASE" +
//        	" WHEN ARMYAI_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE ARMYAI_AMT" +
//        	" END" +
//        	" + CASE" +
//        	" WHEN TOT_PUBMANADD_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE TOT_PUBMANADD_AMT" +
//        	" END" +
//        	" + B.NHI_PAY" +
//        	" + CASE" +
//            " WHEN A.IN_STATUS = '4'" +
//            " THEN 0" +
//            " ELSE CASE" +
//            " WHEN B.REFUSE_TOTAL_AMT IS NULL" +
//            " THEN 0" +
//            " ELSE B.REFUSE_TOTAL_AMT" +
//            " END" +
//            " END" +
//        	" ) NHI_PAY," +
//        	" B.NHI_COMMENT," +
//        	" (  B.OWN_AMT" +
//        	" + B.ADD_AMT" +
//        	" + B.RESTART_STANDARD_AMT" +
//        	" + B.STARTPAY_OWN_AMT" +
//        	" + B.PERCOPAYMENT_RATE_AMT" +
//        	" + B.INS_HIGHLIMIT_AMT" +
//        	" - CASE" +
//        	" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE B.ACCOUNT_PAY_AMT" +
//        	" END" +
//        	" - CASE" +
//        	" WHEN ARMYAI_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE ARMYAI_AMT" +
//        	" END" +
//        	" - CASE" +
//        	" WHEN TOT_PUBMANADD_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE TOT_PUBMANADD_AMT" +
//        	" END" +
//        	" ) OWN_PAY," +
//        	" (  B.NHI_COMMENT" +
//        	" + B.NHI_PAY" +
//        	" + B.OWN_AMT" +
//        	" + B.ADD_AMT" +
//        	" + B.RESTART_STANDARD_AMT" +
//        	" + B.STARTPAY_OWN_AMT" +
//        	" + B.PERCOPAYMENT_RATE_AMT" +
//        	" + B.INS_HIGHLIMIT_AMT" +
//        	" + CASE" +
//            " WHEN A.IN_STATUS = '4'" +
//            " THEN 0" +
//            " ELSE CASE" +
//            " WHEN B.REFUSE_TOTAL_AMT IS NULL" +
//            " THEN 0" +
//            " ELSE B.REFUSE_TOTAL_AMT" +
//            " END" +
//            " END" +
//        	" ) AR_AMT," +
//        	" (CASE" +
//        	" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE B.ACCOUNT_PAY_AMT" +
//        	" END" +
//        	" ) ACCOUNT_PAY_AMT" +
//        	" FROM INS_ADM_CONFIRM A, INS_IBS B" +
//        	" WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
//        	" AND " +confirmnos+
//        	//===zhangp 20120604 start
////        	" AND B.YEAR_MON = '"+todayTime+"'" +
//        	//===zhangp 20120604 end 
//        	" AND A.IN_STATUS IN ('1', '2', '3', '4')) INS";
//        System.out.println(sql);
        //===zhangp 20130717 start
        TParm tjIns2Word = TJINSRecpTool.getInstance().tjins2Word();
        //===zhangp 20130717 end
//        String sql = 
//        	" SELECT SUM (PAY_INS_CARD) ACCOUNT_PAY_AMT, SUM (PAY_INS) NHI_PAY, " +
//        	" 0 ARMYAI_AMT, 0 NHI_COMMENT, 0 OWN_PAY" +
//        	" FROM BIL_IBS_RECPM" +
//        	" WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
        String sql = 
    			" SELECT SUM(TJINS01) " + tjIns2Word.getValue("TJINS01") + ", " +
    			" SUM(TJINS02) " + tjIns2Word.getValue("TJINS02") + ", " +
    			" SUM(TJINS03) " + tjIns2Word.getValue("TJINS03") + ", " +
    			" SUM(TJINS04) " + tjIns2Word.getValue("TJINS04") + ", " +
    			" SUM(TJINS05) " + tjIns2Word.getValue("TJINS05") + ", " +
    			" SUM(TJINS06) " + tjIns2Word.getValue("TJINS06") + ", " +
    			" SUM(TJINS07) " + tjIns2Word.getValue("TJINS07") + ", " +
    			" SUM(TJINS08) " + tjIns2Word.getValue("TJINS08") + ", " +
    			" SUM(TJINS09) " + tjIns2Word.getValue("TJINS09") + ", " +
    			" SUM(TJINS10) " + tjIns2Word.getValue("TJINS10") + "" +
    			" FROM BIL_IBS_RECPM" +
    			" WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
//        System.out.println(sql);
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println(insParm);
        if(insParm.getErrCode()<0){
        	return insParm;
        }
        if(insParm.getCount()<0){
        	return insParm;
        }
        TParm result = new TParm();
        result = insParm.getRow(0);
        result.setCount(1);
    	return result;
	}

	public String getPrintNO(Vector printNo) {
		if (printNo == null)
			return "";
		String s1 = "";
		String s2 = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < printNo.size(); i++) {
			String t = (String) printNo.get(i);
			if (s2.length() == 0) {
				s1 = t;
				s2 = s1;
				continue;
			}
			if (StringTool.addString(s2).equals(t))
				s2 = t;
			else {
				if (sb.length() > 0)
					sb.append(",");
				if (s1.equals(s2)) {
					sb.append(s1);
				} else
					sb.append(s1 + "-" + s2);
				s1 = t;
				s2 = s1;
			}
		}
		if (sb.length() > 0)
			sb.append(",");
		if (s1.equals(s2)) {
			sb.append(s1);
		} else
			sb.append(s1 + "-" + s2);
		return sb.toString();
	}
	/**
	 * ����շ������
	 * @param opdParm
	 * @param formatObject
	 * @return
	 * =========pangben 2014-7-29
	 */
	private TParm chargeDouble(TParm opdParm,DecimalFormat formatObject) {
		int opdCount = opdParm.getCount("REXP_CODE");
		String rexpCode = "";
		double arAmt = 0.00;
		double allArAmt = 0.00;
		//double[] chargeDouble=new double[30];
		String[] chargeName =new String[30];
		String sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='I'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		// �����ʷ��¼��ѯ�˲�������δ��Ʊ�������ܽ��
		int index = 1;
		for (int i = 0; i < 30; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				chargeName[i] = bilRecpParm
						.getData(chargeTemp + "0" + index, 0).toString();
			} else {
				chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0)
						.toString();
			}
			index++;
		}
//		TParm p = new TParm();
//		for (int i = 0; i < chargeCount; i++) {
//			String sysChargeId = sysChargeParm.getData("ID", i).toString();
//			for (int j = 0; j < chargeName.length; j++) {
//				if (sysChargeId.equals(chargeName[j])) {
//					p.setData("CHARGE", i, j);
//					p.setData("ID", i, sysChargeParm.getData("ID", i));
//					p.setData("CHN_DESC", i, sysChargeParm.getData("CHN_DESC",
//							i));
//					break;
//				}
//			}
//		}
		TParm p = new TParm();
		String idCharge="";
		for (int i = 0; i < opdCount; i++) {
			rexpCode = opdParm.getValue("REXP_CODE", i);
			arAmt = opdParm.getDouble("WRT_OFF_AMT", i);
			allArAmt = allArAmt + arAmt;
			for (int j = 0; j < chargeName.length; j++) {
				idCharge =chargeName[j];
				if (rexpCode.equals(idCharge)){
					if (j < 9) {
						p.setData("CHARGE0"+(j+1), formatObject.format(arAmt));
					}else{
						p.setData("CHARGE"+(j+1), formatObject.format(arAmt));
					}
					break;
				}
			}
		}
		p.setData("AR_AMT",allArAmt);
		return p;
	}
	/**
	 * �ϲ���ӡ
	 */
	 public void togederPrint()
	  {
	    DecimalFormat formatObject = new DecimalFormat("###########0.00");
	    String sysDate = StringTool.getString(SystemTool.getInstance()
	      .getDate(), "yyyy/MM/dd");
	    String sDate = 
	      StringTool.getString((Timestamp)getValue("S_DATE"), "yyyy/MM/dd") + 
	      " " + getValueString("S_TIME");

	    String eDate = 
	      StringTool.getString((Timestamp)getValue("E_DATE"), "yyyy/MM/dd") + 
	      " " + getValueString("E_TIME");

	    String selReceiptNo = " SELECT RECEIPT_NO,CHARGE_DATE,CASE_NO    FROM BIL_IBS_RECPM   WHERE ACCOUNT_SEQ IN (" + 
	      this.accountSeq + ") " + 
	      "  ORDER BY RECEIPT_NO,CHARGE_DATE";

	    TParm selReceiptNoParm = new TParm(TJDODBTool.getInstance().select(
	      selReceiptNo));
	    int receiptNoCount = selReceiptNoParm.getCount("RECEIPT_NO");
	    StringBuffer allReceiptNo = new StringBuffer();
	    StringBuffer allCaseNo = new StringBuffer();
	    for (int j = 0; j < receiptNoCount; j++) {
	      String seq = "";
	      seq = selReceiptNoParm.getValue("RECEIPT_NO", j);
	      if (!allReceiptNo.toString().contains(seq)) {
	        if (allReceiptNo.length() > 0)
	          allReceiptNo.append(",");
	        allReceiptNo.append("'").append(seq).append("'");
	      }

	      if (!allCaseNo.toString().contains(
	        selReceiptNoParm.getValue("CASE_NO", j))) {
	        if (allCaseNo.length() > 0)
	          allCaseNo.append(",");
	        allCaseNo.append("'").append(selReceiptNoParm.getValue("CASE_NO", j)).append("'");
	      }
	    }
	    String allReceiptNoStr = allReceiptNo.toString();
	    String selRecpD = " SELECT REXP_CODE,SUM(WRT_OFF_AMT) WRT_OFF_AMT    FROM BIL_IBS_RECPD A   WHERE RECEIPT_NO IN (" + 
	      allReceiptNoStr + ") GROUP BY REXP_CODE";

	    TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
	      .select(selRecpD));
	    TParm chargeParm = chargeDouble(selRecpDParm, formatObject);

	    TParm charge = new TParm();

	    String accountUser = "";
	    Vector au = new Vector();
	    int count = this.accountSeqParm.getCount("ACCOUNT_USER");
	    for (int i = 0; i < count; i++) {
	      String s = this.accountSeqParm.getValue("ACCOUNT_USER", i);
	      if (au.indexOf(s) == -1)
	      {
	        au.add(s);
	      }
	    }
	    for (int i = 0; i < au.size(); i++) {
	      TParm optNameParm = SYSOperatorTool.getInstance().selectdata(
	        (String)au.get(i));
	      String s = optNameParm.getValue("USER_NAME", 0);
	      if (accountUser.length() > 0)
	        accountUser = accountUser + ",";
	      accountUser = accountUser + s;
	    }
	    charge.setData("accountSeq", this.accountSeq.replace("'", ""));

	    String stardate = selReceiptNoParm.getData("CHARGE_DATE", 0).toString();
	    String enddate = selReceiptNoParm.getData("CHARGE_DATE", 
	      selReceiptNoParm.getCount() - 1).toString();
	    stardate = stardate.substring(0, 19).replaceAll("-", "/");
	    enddate = enddate.substring(0, 19).replaceAll("-", "/");
	    charge.setData("S_DATE", stardate + " �� " + enddate);

	    charge.setData("PRINT_DATE", sysDate);
	    charge.setData("CASHIER_CODE", accountUser);

	    charge.setData("patCount", Integer.valueOf(getPatCount(this.accountSeq)));
	    charge.setData("writeOffFee", formatObject
	      .format(-getPayCX(this.accountSeq)));
	    charge
	      .setData("backWashFee", formatObject
	      .format(getPayHC(this.accountSeq)));
	    charge
	      .setData("fillFee", formatObject
	      .format(getPatFee(this.accountSeq)[0]));
	    charge.setData("returnFee", formatObject
	      .format(getPatFee(this.accountSeq)[1]));

	    charge.setData("fillFeeCash", formatObject
	      .format(getPatFee(this.accountSeq)[3]));
	    charge.setData("fillFeeCheck", formatObject
	      .format(getPatFee(this.accountSeq)[4]));
	    charge.setData("returnFeeCash", formatObject
	      .format(getPatFee(this.accountSeq)[5]));
	    charge.setData("returnFeeCheck", formatObject
	      .format(getPatFee(this.accountSeq)[6]));
	    charge.setData("fillCard", formatObject.format(getPatFee(this.accountSeq)[7]));
	    charge.setData("fillDebit", formatObject.format(getPatFee(this.accountSeq)[8]));
	    charge.setData("fillGift", formatObject.format(getPatFee(this.accountSeq)[9]));
	    charge.setData("fillDiscont", formatObject.format(getPatFee(this.accountSeq)[10]));
	    charge.setData("returnCard", formatObject.format(getPatFee(this.accountSeq)[11]));
	    charge.setData("returnDebit", formatObject.format(getPatFee(this.accountSeq)[12]));
	    charge.setData("returnGift", formatObject.format(getPatFee(this.accountSeq)[13]));
	    charge.setData("returnDiscont", formatObject.format(getPatFee(this.accountSeq)[14]));
	    charge.setData("fillBxzf", formatObject.format(getPatFee(this.accountSeq)[15]));
	    charge.setData("returnBxzf", formatObject.format(getPatFee(this.accountSeq)[16]));
	    charge.setData("fillWx", formatObject.format(getPatFee(this.accountSeq)[17]));
	    charge.setData("returnWx", formatObject.format(getPatFee(this.accountSeq)[18]));
	    charge.setData("fillZfb", formatObject.format(getPatFee(this.accountSeq)[19]));
	    charge.setData("returnZfb", formatObject.format(getPatFee(this.accountSeq)[20]));
	    charge.setData("fillMedical", formatObject.format(getPatFee(this.accountSeq)[21]));
	    charge.setData("returnMedical", formatObject.format(getPatFee(this.accountSeq)[22]));

	    charge.setData("debitFee", "0.00");
	    charge.setData("ownFee", formatObject.format(getPatFee(this.accountSeq)[2]));
	    charge.setData("arAmt", formatObject.format(chargeParm.getDouble("AR_AMT")));
	    String tmp = StringUtil.getInstance().numberToWord(StringTool.getDouble(formatObject.format(chargeParm.getDouble("AR_AMT"))));
	    if (tmp.lastIndexOf("��") > 0) {
	      tmp = tmp.substring(0, tmp.lastIndexOf("��") + 1);
	    }
	    charge.setData("zhAmt", tmp);

	    charge.setData("OPT_USER", Operator.getName());

	    TParm insParm = getInsParm(this.accountSeq);
	    if (insParm.getErrCode() < 0) {
	      return;
	    }

	    double armyAi_amt = insParm.getDouble("ARMYAI_AMT");
	    double account_pay_amt = insParm.getDouble("ACCOUNT_PAY_AMT");
	    double nhi_comment = insParm.getDouble("NHI_COMMENT");
	    double nhi_pay = insParm.getDouble("NHI_PAY");
	    double ins = armyAi_amt + nhi_comment + nhi_pay;
	    double pay_cash = insParm.getDouble("OWN_PAY");
	    charge.setData("nhiComment", Double.valueOf(StringTool.round(nhi_comment, 2)));

	    charge.setData("accountPayAmt", Double.valueOf(StringTool.round(account_pay_amt, 2)));

	    charge.setData("debitFee", Double.valueOf(StringTool.round(nhi_pay, 2)));

	    TParm print = new TParm();
	    TParm printCharge = new TParm();

	    String sql = " SELECT SUM(PAY_CASH) PAY_CASH ,SUM(PAY_INS_CARD) PAY_INS_CARD   FROM BIL_IBS_RECPM   WHERE ACCOUNT_SEQ IN (" + 
	      this.accountSeq + 
	      ") " + 
	      "   AND AR_AMT <> 0 ";
	    TParm payWayParm = new TParm(TJDODBTool.getInstance().select(sql));
	    print.setData("fillFeeCash", "TEXT", "�ֽ�: " + 
	      formatObject.format(payWayParm.getDouble("PAY_CASH", 0)));
	    print.setData("PAY_INS_CARD", "TEXT", "�ֽ�: " + 
	      formatObject.format(payWayParm.getDouble("PAY_INS_CARD", 0)));
	    TParm PCharge = onPrintPackData(this.accountSeq, allReceiptNoStr);
	    for (int i = 1; i < 24; i++) {
	      if (i < 10) {
	        printCharge.setData("CHARGE0" + i, chargeParm
	          .getData("CHARGE0" + i) == null ? Double.valueOf(0.0D) : formatObject
	          .format(chargeParm.getDouble("CHARGE0" + i)));

	        charge.setData("PCHARGE0" + i, 
	          PCharge.getData("CHARGE0" + i) == null ? Double.valueOf(0.0D) : 
	          formatObject.format(PCharge
	          .getDouble("CHARGE0" + i)));
	      } else {
	        printCharge.setData("CHARGE" + i, chargeParm.getData("CHARGE" + 
	          i) == null ? Double.valueOf(0.0D) : formatObject.format(chargeParm
	          .getDouble("CHARGE" + i)));

	        charge.setData("PCHARGE" + i, 
	          PCharge.getData("CHARGE" + i) == null ? Double.valueOf(0.0D) : 
	          formatObject.format(PCharge
	          .getDouble("CHARGE" + i)));
	      }

	    }

	    printCharge.setData("CHARGE00", formatObject.format(chargeParm
	      .getDouble("CHARGE03") + 
	      chargeParm.getDouble("CHARGE04")));

	    charge.setData("PCHARGE03AND04", formatObject.format(PCharge
	      .getDouble("CHARGE03") + 
	      PCharge.getDouble("CHARGE04")));
	    charge.setData("PARAMT", formatObject.format(PCharge
	      .getDouble("PARAMT")));
	    charge.setData("PARAMTBACK", formatObject.format(PCharge
	      .getDouble("PARAMTBACK")));
	    charge.setData("PARAMTTOT", formatObject.format(PCharge
	      .getDouble("PARAMTTOT")));
	    charge.setData("PCHARGEAMT", formatObject.format(PCharge
	      .getDouble("AR_AMT")));

	    printCharge.setData("totAmt", formatObject.format(chargeParm.getDouble("AR_AMT")));

	    String sqlSum = "SELECT SUM (B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) TOT_AMT FROM BIL_IBS_RECPM B  WHERE B.AR_AMT<>0  AND B.ACCOUNT_SEQ IN (" + 
	      this.accountSeq + ") ";
	    String sqlLUMP = "SELECT SUM (LUMPWORK_AMT) LUMPWORK_AMT,SUM (LUMPWORK_OUT_AMT) LUMPWORK_OUT_AMT,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM WHERE ACCOUNT_SEQ IN (" + 
	      this.accountSeq + ") ";

	    String sqlAR = "SELECT SUM(AR_AMT) AR_AMT,SUM(REDUCE_AMT) REDUCE_AMT FROM BIL_IBS_RECPM WHERE AR_AMT <> 0 AND (LUMPWORK_AMT = 0.00 OR LUMPWORK_AMT IS NULL ) AND ACCOUNT_SEQ IN (" + 
	      this.accountSeq + ") ";
	    String sqlLump = "SELECT SUM (LUMPWORK_AMT) + SUM (LUMPWORK_OUT_AMT) LUMP,SUM(REDUCE_AMT) REDUCE_AMT  FROM BIL_IBS_RECPM WHERE AR_AMT <> 0 AND LUMPWORK_AMT <> 0  AND ACCOUNT_SEQ IN (" + 
	      this.accountSeq + ") ";
	    TParm dataAR = new TParm(TJDODBTool.getInstance().select(sqlAR));
	    TParm dataLump = new TParm(TJDODBTool.getInstance().select(sqlLump));
	    double lumReduceAmt = 0.0D;
	    if ((dataLump.getCount() > 0) && (dataLump.getData("REDUCE_AMT", 0) != null)) {
	      lumReduceAmt = dataLump.getDouble("REDUCE_AMT", 0);
	    }
	    printCharge.setData("totAmt", formatObject.format(
	      dataAR.getDouble("AR_AMT", 0) - dataAR.getDouble("REDUCE_AMT", 0) + 
	      dataLump.getDouble("LUMP", 0) - lumReduceAmt));
	    if ((dataAR.getDouble("REDUCE_AMT", 0) != 0.0D) || (lumReduceAmt != 0.0D)) {
	      printCharge.setData("reduceAmt", "������:" + formatObject.format(
	        dataAR.getDouble("REDUCE_AMT", 0) + lumReduceAmt) + "Ԫ");
	    }

	    TParm dataSum = new TParm(TJDODBTool.getInstance().select(sqlSum));
	    TParm lumpParm = new TParm(TJDODBTool.getInstance().select(sqlLUMP));
	    String lumpWork = "";
	    if ((lumpParm.getDouble("LUMPWORK_AMT", 0) != 0.0D) && 
	      (lumpParm.getCount("LUMPWORK_AMT") > 0)) {
	      lumpWork = lumpWork + "���ڽ�ת:" + formatObject.format(lumpParm
	        .getDouble("LUMPWORK_AMT", 0)) + "Ԫ";

	      if (this.reduceAmt != 0.0D)
	        lumpWork = lumpWork + ";�����Ը�:" + formatObject.format(lumpParm
	          .getDouble("LUMPWORK_OUT_AMT", 0) - (dataAR.getDouble("REDUCE_AMT", 0) + lumReduceAmt)) + "Ԫ";
	      else {
	        lumpWork = lumpWork + ";�����Ը�:" + formatObject.format(lumpParm
	          .getDouble("LUMPWORK_OUT_AMT", 0)) + "Ԫ";
	      }

	      if (lumpParm.getDouble("REDUCE_AMT", 0) > 0.0D) {
	        lumpWork = lumpWork + ";�ײͼ���:" + formatObject.format(lumpParm
	          .getDouble("REDUCE_AMT", 0)) + "Ԫ";
	      }

	    }

	    charge.setData("lumpworkFee", lumpWork);
	    printCharge.setData("initAmt", formatObject.format(
	      dataSum.getDouble("TOT_AMT", 0)));

	    TParm printCancleDate = getPrintCancelTableDate(this.accountSeq);
	    TParm printReturnDate = getPrintReturnTableDate(this.accountSeq);
	    TParm printChangeDate = getChangeTableDate(this.accountSeq);
	    print.setData("cancelTable", printCancleDate.getData());
	    print.setData("returnFeeTable", printReturnDate.getData());
	    print.setData("changeTable", printChangeDate.getData());
	    print.setData("CHARGE", charge.getData());
	    print.setData("PRINT_CHARGE", printCharge.getData());

	    openPrintWindow(IReportTool.getInstance().getReportPath("BILAccountTot_V45.jhw"), 
	      IReportTool.getInstance().getReportParm("BILAccountTot.class", print));
	  }

	/**
	 * �������ϱ��ӡ����
	 *
	 * @param accountSeq
	 *            String
	 * @return TParm
	 */
	private TParm getPrintCancelTableDate(String accountSeq) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm parmData = new TParm();
		// parmData =
		String selMrNo = " SELECT INV_NO,AR_AMT FROM BIL_INVRCP WHERE RECP_TYPE = 'IBS' AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") AND CANCEL_FLG = '3' ";
		parmData = new TParm(TJDODBTool.getInstance().select(selMrNo));
		int count = parmData.getCount("INV_NO");
		TParm aparm = new TParm();
		// ��������ʾ�㷨
		int row = 0;
		if (count>0) {
			row = 1;
		}
		
		int column = 0;
		for (int i = 0; i < count; i++) {

			aparm.addData("INV_NO_" + column, parmData.getData(
					"INV_NO", i));
			aparm.addData("AR_AMT_" + column, df.format(parmData.getDouble(
					"AR_AMT", i)));
			column++;
			if (column == 2) {
				column = 0;
				row++;
			}
		}
		aparm.setCount(row);
		// this.messageBox_("��������"+aparm);
		TParm printData = new TParm(); // ��ӡ����
		printData.setCount(row);
		printData = aparm;
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
		// System.out.println("��ӡ2����"+printData);
		return printData;
	}

	/**
	 * �����˷Ѵ�ӡ����
	 *
	 * @param accountSeq
	 *            String
	 * @return TParm
	 */
	private TParm getPrintReturnTableDate(String accountSeq) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm parmData = new TParm();
		// parmData =
		String selMrNo = " SELECT INV_NO,AR_AMT FROM BIL_INVRCP WHERE RECP_TYPE = 'IBS' AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") AND CANCEL_FLG = '1' ";
		parmData = new TParm(TJDODBTool.getInstance().select(selMrNo));
		int count = parmData.getCount("INV_NO");
		TParm aparm = new TParm();
		// ��������ʾ�㷨
		int row = 0;
		if (count>0) {
			row = 1;
		}
		int column = 0;
		for (int i = 0; i < count; i++) {

			aparm.addData("INV_NO_" + column, parmData.getData(
					"INV_NO", i));
			aparm.addData("AR_AMT_" + column, df.format(parmData.getDouble(
					"AR_AMT", i)));
			column++;
			if (column == 2) {
				column = 0;
				row++;
			}
		}
		aparm.setCount(row);
		// this.messageBox_("�˷�����"+aparm);
		TParm printData = new TParm(); // ��ӡ����
		printData.setCount(row);
		printData = aparm;
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
		// System.out.println("��ӡ1����"+printData);
		return printData;
	}

	/**
	 * �õ�����ʱ���
	 *
	 * @return String
	 */
	public String getAccountDate() {
		String accountDate = "";
		TParm accountDateParm = new TParm();
		accountDateParm = BILSysParmTool.getInstance().getDayCycle("I");
		accountDate = accountDateParm.getValue("DAY_CYCLE", 0);
		return accountDate;
	}

	/**
	 * table����checkBox�¼�
	 *
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableComponent(Object obj) {
		accountSeq = new String();
		accountSeqParm = new TParm();
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int allRow = table.getRowCount();
		StringBuffer allSeq = new StringBuffer();
		String cashierCode = "";
		String accountDate = "";
		for (int i = 0; i < allRow; i++) {
			String seq = "";
			if ("Y".equals(tableParm.getValue("FLG", i))) {
				seq = tableParm.getValue("ACCOUNT_SEQ", i);
				cashierCode = tableParm.getValue("ACCOUNT_USER", i);
				//donglt  2016-3-30 modify
				accountDate = tableParm.getValue("ACCOUNT_DATE", i);
				if (allSeq.length() > 0)
					allSeq.append(",");
				allSeq.append("'").append(seq).append("'");
				accountSeqParm.addData("ACCOUNT_SEQ", seq);
				accountSeqParm.addData("ACCOUNT_USER", cashierCode);
				//donglt  2016-3-30 modify
				accountSeqParm.addData("ACCOUNT_DATE", accountDate);
			}
		}
		accountSeq = allSeq.toString();
		//this.messageBox_("�ս��" + accountSeq);
		return true;
	}

	/**
	 * ȫѡ�¼�
	 */
	public void setAllFlg() {
		accountSeq = new String();
		accountSeqParm = new TParm();
		TTable table = (TTable) this.getComponent("Table");
		String select = getValueString("ALL_FLG");
		TParm parm = table.getParmValue();
		if (parm != null) {
			int count = parm.getCount("FLG");
			StringBuffer allSeq = new StringBuffer();
			String cashierCode = "";
			for (int i = 0; i < count; i++) {
				String seq = "";
				parm.setData("FLG", i, select);
				if ("Y".equals(select)) {
					seq = parm.getValue("ACCOUNT_SEQ", i);
					cashierCode = parm.getValue("ACCOUNT_USER", i);
					if (allSeq.length() > 0)
						allSeq.append(",");
					allSeq.append("'").append(seq).append("'");
					accountSeqParm.addData("ACCOUNT_SEQ", seq);
					accountSeqParm.addData("ACCOUNT_USER", cashierCode);
				}
			}
			accountSeq = allSeq.toString();
			table.setParmValue(parm);

		}
	}

	/**
	 * �ж�Ʊ������
	 *
	 * @param vote
	 *            Vector
	 * @return Vector @ othour fudw
	 */
	public Vector seats(Vector vote) {
		long[] voteno = new long[vote.size()];
		long p;
		int delete = 0;
		for (int t = 0; t < vote.size(); t++) {
			String invNo = (String) vote.get(t);
			if (invNo == null || invNo.length() <= 0) {
				delete++;
				continue;
			}
			p = Long.parseLong((String) vote.get(t));
			voteno[t] = p;
		}
		// ����
		long temp;
		for (int i = 0; i < voteno.length; i++) {
			for (int j = 0; j < voteno.length - 1; j++) {
				if (voteno[j + 1] < voteno[j]) {
					temp = voteno[j + 1];
					voteno[j + 1] = voteno[j];
					voteno[j] = temp;
				}
			}
		}
		// ������
		Vector result = new Vector();
		// ѭ����������
		for (int i = 0; i < vote.size();) {
			// ������ʼƱ��voteNO[0]�ͽ���Ʊ��voteNO[1]
			String[] voteNO = new String[2];
			// �õ���ǰƱ��
			long no = voteno[i];
			// �洢��ʼƱ��
			voteNO[0] = "" + no;
			for (int s = i + 1; s < vote.size(); s++, i++) {
				no++;
				if (voteno[s] == no)
					continue;
				no--;
				break;
			}
			i++;
			// �洢����Ʊ��
			voteNO[1] = "" + no;
			result.add(voteNO);
		}
		return result;
	}

	/**
	 * �õ���������
	 *
	 * @param accountSeq
	 *            String
	 * @return int
	 */
	public int getPatCount(String accountSeq) {
		String selRecp = " SELECT DISTINCT(CASE_NO) COUNT "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		int count = selRecpParm.getCount("COUNT");
		return count;
	}
	/**
	 * �õ���������(��ӡԤ��ר��)
	 *
	 * @param accountSeq
	 *            String
	 * @return int
	 */
	public int getRePatCount(String cashierCode,String chargeDate) {
		String selRecp = " SELECT DISTINCT(CASE_NO) COUNT "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE  CASHIER_CODE = '"+cashierCode+"'" +
					"AND  ACCOUNT_FLG IS NULL" +
					" AND  CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')";
			// "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
			// +
//			"  ORDER BY RECEIPT_NO"; 
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		int count = selRecpParm.getCount("COUNT");
		return count;
	}

	/**
	 * �õ����������ܼ�
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPatFeeB(String accountSeq) {
		String selRecp = " SELECT SUM(PAY_CASH) PAY_CASH "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") " + "    AND REFUND_DATE IS NULL "
				+ "    AND PAY_CASH >0 ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double bFee = selRecpParm.getDouble("PAY_CASH", 0);
		return bFee;
	}

	/**
	 * �õ������˷��ܼ�
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPatFeeT(String accountSeq) {
		String selRecp = " SELECT SUM(PAY_CASH) PAY_CASH "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") " + "    AND REFUND_DATE IS NULL "
				+ "    AND PAY_CASH <0 ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PAY_CASH", 0);
		return tFee;
	}

	/**
	 * �õ�Ԥ����������
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPayCX(String accountSeq) {
		String selRecp =
                          " SELECT SUM(PRE_AMT) AS PRE_AMT FROM BIL_PAY "+
                          "  WHERE RESET_RECP_NO IN( SELECT RECEIPT_NO  "+
                          "                            FROM BIL_IBS_RECPM "+
                          "                           WHERE ACCOUNT_SEQ IN (" + accountSeq + ") " +
//                          "                                 AND REFUND_DATE IS NULL) "+
                          "                                 ) "+
                          "    AND TRANSACT_TYPE = '03' ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PRE_AMT", 0);
		return tFee;
	}
	
	/**
	 * �õ�Ԥ����������(��ӡԤ��ר��)
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getRePayCX(String cashierCode,String chargeDate) {
		String selRecp =
                          " SELECT SUM(PRE_AMT) AS PRE_AMT FROM BIL_PAY "+
                          "  WHERE RESET_RECP_NO IN( SELECT RECEIPT_NO  "+
                          "                            FROM BIL_IBS_RECPM "+
                          "                           WHERE  CASHIER_CODE = '"+cashierCode+"'" +
					"AND  ACCOUNT_FLG IS NULL" +
					" AND  CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS'))"+
					 "    AND TRANSACT_TYPE = '03' ";
			// "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
			// +
//			"  ORDER BY RECEIPT_NO"; 
		// ��ѯ��֧ͬ����ʽ������(�ս���)
//		System.out.println("Ԥ��������ʾ  is����"+selRecp);
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PRE_AMT", 0);
		return tFee;
	}

	/**
	 * �õ�Ԥ����س���
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPayHC(String accountSeq) {
		String selRecp =
                          " SELECT SUM(PRE_AMT) AS PRE_AMT FROM BIL_PAY " +
                          "  WHERE RESET_RECP_NO IN( SELECT RECEIPT_NO "+
                          "                            FROM BIL_IBS_RECPM "+
                          "                           WHERE ACCOUNT_SEQ IN ("+ accountSeq+ ") "+
//                          "                             AND REFUND_DATE IS NULL) "+
                          "                             ) "+
                          "    AND TRANSACT_TYPE = '04' ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PRE_AMT", 0);
		return tFee;
	}

	/**
	 * �õ�Ԥ����س���(��ӡԤ��ר��)
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getRePayHC(String cashierCode,String chargeDate) {
		String selRecp =
                          " SELECT SUM(PRE_AMT) AS PRE_AMT FROM BIL_PAY " +
                          "  WHERE RESET_RECP_NO IN( SELECT RECEIPT_NO "+
                          "                            FROM BIL_IBS_RECPM "+
                          "                            WHERE  CASHIER_CODE = '"+cashierCode+"'" +
					"AND  ACCOUNT_FLG IS NULL" +
					" AND  CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')"+
			// "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
			// +
					 "    AND TRANSACT_TYPE = '04' ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PRE_AMT", 0);
		return tFee;
	}
	
	/**
	 * �õ����˿���Ϣ
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double[] getPatFee(String accountSeq) {
		String selRecpAr =
		//$-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--start
		"SELECT SUM (  CASE" +
		" WHEN PAY_CASH IS NULL" +
		" THEN 0" +
		" ELSE PAY_CASH" +
		" END" +
		" + CASE" +
		" WHEN PAY_CHECK IS NULL" +
		" THEN 0" +
		" ELSE PAY_CHECK" +
		" END" +
		" + CASE" +
		" WHEN PAY_BANK_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_BANK_CARD" +
		" END" +
		" + CASE" +
		" WHEN PAY_GIFT_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_GIFT_CARD" +
		" END" +
		" + CASE" +
		" WHEN PAY_DISCNT_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_DISCNT_CARD" +
		" END" +
		" + CASE" +
		" WHEN PAY_DEBIT IS NULL" +
		" THEN 0" +
		" ELSE PAY_DEBIT" +
		" END" +
		" + CASE"   //add by huangtt 20150519 start
		+ " WHEN PAY_BXZF IS NULL"  
		+ " THEN 0"
		+ " ELSE PAY_BXZF"
		+ " END" +    //add by huangtt 20150519 end
		" + CASE"   //add by kangyue 20160628 start
		+ " WHEN PAY_TYPE09 IS NULL"  
		+ " THEN 0"
		+ " ELSE PAY_TYPE09"
		+ " END" +    
		" + CASE"   
		+ " WHEN PAY_TYPE10 IS NULL"  
		+ " THEN 0"
		+ " ELSE PAY_TYPE10"
		+ " END" +    //add by kangyue 20160628 end
		" + CASE"   
		+ " WHEN PAY_MEDICAL_CARD IS NULL"  
		+ " THEN 0"
		+ " ELSE PAY_MEDICAL_CARD"
		+ " END" +    //add by kangyue 20171205
		" ) AS SUM " +
		//$-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--end
		
		//$-------�ֽ�--start
		", SUM( CASE" +
		" WHEN PAY_CASH IS NULL" +
		" THEN 0" +
		" ELSE PAY_CASH" +
		" END ) PAY_CASH " +
		//$-------�ֽ�--end
		
		//$-------֧Ʊ--start
		", SUM( CASE" +
		" WHEN PAY_CHECK IS NULL" +
		" THEN 0" +
		" ELSE PAY_CHECK" +
		" END ) PAY_CHECK" +
		//$-------֧Ʊ--end
		
		//$-------ˢ��--start
		", SUM( CASE" +
		" WHEN PAY_BANK_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_BANK_CARD " +
		" END ) PAY_BANK_CARD " +
		//$-------ˢ��--end
		
		//$-------��Ʒ��--start
		", SUM( CASE" +
		" WHEN PAY_GIFT_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_GIFT_CARD " +
		" END ) PAY_GIFT_CARD " +
		//$-------��Ʒ��--end
		
		//$-------�ֽ��ۿ�ȯ--start
		", SUM( CASE" +
		" WHEN PAY_DISCNT_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_DISCNT_CARD " +
		" END ) PAY_DISCNT_CARD " +
		//$-------�ֽ��ۿ�ȯ--end
		
		//$-------����ֱ��--start-----huangtt 20150519
		", SUM( CASE" +
		" WHEN PAY_BXZF IS NULL" +
		" THEN 0" +
		" ELSE PAY_BXZF " +
		" END ) PAY_BXZF " +
		//$-------����ֱ��--end--- huangtt 20150519 
		
		//$-------΢��֧��--start-----kangyue 20160628
		", SUM( CASE" +
		" WHEN PAY_TYPE09 IS NULL" +
		" THEN 0" +
		" ELSE PAY_TYPE09 " +
		" END ) PAY_TYPE09 " +
		//$-------΢��֧��--end--- huangtt 20160628
		
		//$-------֧����--start-----kangyue 20160628
		", SUM( CASE" +
		" WHEN PAY_TYPE10 IS NULL" +
		" THEN 0" +
		" ELSE PAY_TYPE10 " +
		" END ) PAY_TYPE10 " +
		//$-------֧����--end--- kangy 20160628
		
		//$-------ҽ�ƿ�--start-----kangyue 20160628
		", SUM( CASE" +
		" WHEN PAY_MEDICAL_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_MEDICAL_CARD " +
		" END ) PAY_MEDICAL_CARD " +
		//$-------ҽ�ƿ�--end--- kangy 20170
		
		//$-------Ӧ�տ�--start
		", SUM( CASE" +
		" WHEN PAY_DEBIT IS NULL" +
		" THEN 0" +
		" ELSE PAY_DEBIT " +
		" END ) PAY_DEBIT " +
		//$-------Ӧ�տ�--end
		
		" FROM BIL_IBS_RECPM" +
		" WHERE AR_AMT >= 0" +
		" AND   CASE" +
		" WHEN OWN_AMT IS NULL" +
		" THEN 0" +
		" ELSE OWN_AMT" +
		" END" +
		" - CASE" +
		" WHEN PAY_BILPAY IS NULL" +
		" THEN 0" +
		" ELSE PAY_BILPAY" +
		" END >= 0" +
		" AND CASE" +
		" WHEN PAY_BILPAY IS NULL" +
		" THEN 0" +
		" ELSE PAY_BILPAY" +
		" END >= 0" +
		//" AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) " +
		" AND (REFUND_DATE>= TO_DATE(TO_CHAR(CHARGE_DATE,'yyyyMMdd') " +
		" ||'235959','yyyyMMddHH24miss') OR REFUND_DATE IS NULL) " +
		" AND ACCOUNT_SEQ IN ("+accountSeq+")";
//		System.out.println("selRecpAr::"+selRecpAr);
		TParm selRecpArParm = new TParm(TJDODBTool.getInstance().select(
				selRecpAr));
		double aFee = selRecpArParm.getDouble("SUM", 0);
		double acsFee = selRecpArParm.getDouble("PAY_CASH", 0);
		double ackFee = selRecpArParm.getDouble("PAY_CHECK", 0);
		double acardFee = selRecpArParm.getDouble("PAY_BANK_CARD", 0);
		double agiftFee = selRecpArParm.getDouble("PAY_GIFT_CARD", 0);
		double adisFee = selRecpArParm.getDouble("PAY_DISCNT_CARD", 0);
		double adebitFee = selRecpArParm.getDouble("PAY_DEBIT", 0);
		double abxzfFee = selRecpArParm.getDouble("PAY_BXZF", 0);   //add by huangtt 20150519
		double awxFee = selRecpArParm.getDouble("PAY_TYPE09", 0);   //add by kangyue 20160628
		double azfbFee = selRecpArParm.getDouble("PAY_TYPE10", 0);   //add by kangyue 20160628
		double amedicalFee = selRecpArParm.getDouble("PAY_MEDICAL_CARD", 0);   //add by kangyue 20171205
		
		String selRecpPPay =
			//$-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--start
			"SELECT SUM (  CASE" +
			" WHEN PAY_CASH IS NULL" +
			" THEN 0" +
			" ELSE PAY_CASH" +
			" END" +
			" + CASE" +
			" WHEN PAY_CHECK IS NULL" +
			" THEN 0" +
			" ELSE PAY_CHECK" +
			" END" +
			" + CASE" +
			" WHEN PAY_BANK_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_BANK_CARD" +
			" END" +
			" + CASE" +
			" WHEN PAY_GIFT_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_GIFT_CARD" +
			" END" +
			" + CASE" +
			" WHEN PAY_DISCNT_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_DISCNT_CARD" +
			" END" +
			" + CASE" +
			" WHEN PAY_DEBIT IS NULL" +
			" THEN 0" +
			" ELSE PAY_DEBIT" +
			" END" +
			" + CASE"   //add by huangtt 20150519 start
			+ " WHEN PAY_BXZF IS NULL"  
			+ " THEN 0"
			+ " ELSE PAY_BXZF"
			+ " END" +    //add by huangtt 20150519 end
			" + CASE"   //add by kangyue 20160628 start
			+ " WHEN PAY_TYPE09 IS NULL"  
			+ " THEN 0"
			+ " ELSE PAY_TYPE09"
			+ " END" +   
			" + CASE"   
			+ " WHEN PAY_TYPE10 IS NULL"  
			+ " THEN 0"
			+ " ELSE PAY_TYPE10"
			+ " END" +    //add by kangyue 20160628 end
			" + CASE"   
			+ " WHEN PAY_MEDICAL_CARD IS NULL"  
			+ " THEN 0"
			+ " ELSE PAY_MEDICAL_CARD"
			+ " END" +    //add by kangyue 20171205
			" ) AS SUM " +
			//$-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--end
			
			//$-------�ֽ�--start
			", SUM( CASE" +
			" WHEN PAY_CASH IS NULL" +
			" THEN 0" +
			" ELSE PAY_CASH" +
			" END ) PAY_CASH " +
			//$-------�ֽ�--end
			
			//$-------֧Ʊ--start
			", SUM( CASE" +
			" WHEN PAY_CHECK IS NULL" +
			" THEN 0" +
			" ELSE PAY_CHECK" +
			" END ) PAY_CHECK" +
			//$-------֧Ʊ--end
			
			//$-------ˢ��--start
			", SUM( CASE" +
			" WHEN PAY_BANK_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_BANK_CARD " +
			" END ) PAY_BANK_CARD " +
			//$-------ˢ��--end
			
			//$-------��Ʒ��--start
			", SUM( CASE" +
			" WHEN PAY_GIFT_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_GIFT_CARD " +
			" END ) PAY_GIFT_CARD " +
			//$-------��Ʒ��--end
			
			//$-------�ֽ��ۿ�ȯ--start
			", SUM( CASE" +
			" WHEN PAY_DISCNT_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_DISCNT_CARD " +
			" END ) PAY_DISCNT_CARD " +
			//$-------�ֽ��ۿ�ȯ--end
			
			//$-------����ֱ��--start-----huangtt 20150519
			", SUM( CASE" +
			" WHEN PAY_BXZF IS NULL" +
			" THEN 0" +
			" ELSE PAY_BXZF " +
			" END ) PAY_BXZF " +
			//$-------����ֱ��--end--- huangtt 20150519 
			
			//$-------΢��֧��--start-----kangyue 20160628
			", SUM( CASE" +
			" WHEN PAY_TYPE09 IS NULL" +
			" THEN 0" +
			" ELSE PAY_TYPE09 " +
			" END ) PAY_TYPE09 " +
			//$-------΢��֧��--end--- kangyue 20160628
			
			//$-------֧����--start-----kangyue 20160628
			", SUM( CASE" +
			" WHEN PAY_TYPE10 IS NULL" +
			" THEN 0" +
			" ELSE PAY_TYPE10 " +
			" END ) PAY_TYPE10 " +
			//$-------֧����--end--- kangyue 20160628
			
			//$-------ҽ�ƿ�--start-----kangyue 20171205
			", SUM( CASE" +
			" WHEN PAY_MEDICAL_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_MEDICAL_CARD " +
			" END ) PAY_MEDICAL_CARD " +
			//$-------ҽ�ƿ�--end--- kangyue 20171205
			
			//$-------Ӧ�տ�--start
			", SUM( CASE" +
			" WHEN PAY_DEBIT IS NULL" +
			" THEN 0" +
			" ELSE PAY_DEBIT " +
			" END ) PAY_DEBIT " +
			//$-------Ӧ�տ�--end
			
			" FROM BIL_IBS_RECPM" +
			" WHERE AR_AMT >= 0" +
			" AND   CASE" +
			" WHEN OWN_AMT IS NULL" +
			" THEN 0" +
			" ELSE OWN_AMT" +
			" END" +
			" - CASE" +
			" WHEN PAY_BILPAY IS NULL" +
			" THEN 0" +
			" ELSE PAY_BILPAY" +
			" END < 0" +
			" AND CASE" +
			" WHEN PAY_BILPAY IS NULL" +
			" THEN 0" +
			" ELSE PAY_BILPAY" +
			" END >= 0" +
			//" AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) " +
			" AND (REFUND_DATE>= TO_DATE(TO_CHAR(CHARGE_DATE,'yyyyMMdd') " +
			" ||'235959','yyyyMMddHH24miss') OR REFUND_DATE IS NULL) " +
			" AND ACCOUNT_SEQ IN ("+accountSeq+")";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
//		System.out.println(selRecpPPay);
		TParm selRecpPayPParm = new TParm(TJDODBTool.getInstance().select(
				selRecpPPay));
		double pFee = selRecpPayPParm.getDouble("SUM", 0);
		double pcsFee = selRecpPayPParm.getDouble("PAY_CASH", 0);
		double pckFee = selRecpPayPParm.getDouble("PAY_CHECK", 0);
//		double pcardFee = selRecpArParm.getDouble("PAY_BANK_CARD", 0);
//		double pgiftFee = selRecpArParm.getDouble("PAY_GIFT_CARD", 0);
//		double pdisFee = selRecpArParm.getDouble("PAY_DISCNT_CARD", 0);
//		double pdebitFee = selRecpArParm.getDouble("PAY_DEBIT", 0);
		double pcardFee = selRecpPayPParm.getDouble("PAY_BANK_CARD", 0);
		double pgiftFee = selRecpPayPParm.getDouble("PAY_GIFT_CARD", 0);
		double pdisFee = selRecpPayPParm.getDouble("PAY_DISCNT_CARD", 0);
		double pdebitFee = selRecpPayPParm.getDouble("PAY_DEBIT", 0);
		double pbxzfFee = selRecpPayPParm.getDouble("PAY_BXZF", 0);  //add by huangtt 20150519
		double pwxFee = selRecpPayPParm.getDouble("PAY_TYPE09", 0);  //add by kangyue 20160628
		double pzfbFee = selRecpPayPParm.getDouble("PAY_TYPE10", 0);  //add by kangyue 20160628
		double pmedicalFee = selRecpPayPParm.getDouble("PAY_MEDICAL_CARD", 0);  //add by kangyue 20171205
		
		double[] bFee = new double[23];
		bFee[0] = aFee;
		bFee[1] = pFee;
		String ownSql =
			"SELECT SUM(OWN_AMT) OWN_AMT FROM BIL_IBS_RECPM WHERE ACCOUNT_SEQ IN (" + accountSeq + ")" +
			" AND (REFUND_DATE>= TO_DATE(TO_CHAR(CHARGE_DATE,'yyyyMMdd') " +
			" ||'235959','yyyyMMddHH24miss') OR REFUND_DATE IS NULL) " ;
		TParm ownParm = new TParm(TJDODBTool.getInstance().select(ownSql));
		double oFee = ownParm.getDouble("OWN_AMT", 0);
		bFee[2] = oFee;
		bFee[3] = acsFee;
		bFee[4] = ackFee;
		bFee[5] = pcsFee;
		bFee[6] = pckFee;
		bFee[7] = acardFee; 
		bFee[8] = agiftFee ;
		bFee[9] = adisFee ;
		bFee[10] =adebitFee;
		bFee[11] = pcardFee; 
		bFee[12] = pgiftFee ;
		bFee[13] = pdisFee ; 
		bFee[14] = pdebitFee;
		bFee[15] = abxzfFee;  //add by huangtt 20150519
		bFee[16] = pbxzfFee; //add by huangtt 20150519
		bFee[17] = awxFee;  //add by kangyue 20160628
		bFee[18] = pwxFee; //add by kangyue 20160628
		bFee[19] = azfbFee;  //add by kangyue 20160628
		bFee[20] = pzfbFee; //add by kangyue 20160628
		bFee[21] = amedicalFee;  //add by kangyue 20171205
		bFee[22] = pmedicalFee; //add by kangyue 20171205
		
		return bFee;
	}
	
	/**
	 * �õ����˿���Ϣ(��ӡԤ��ר��)
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double[] getRePatFee(String cashierCode,String chargeDate) {
		String selRecpAr =
		// $-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--start
		"SELECT SUM (  CASE"
				+ " WHEN PAY_CASH IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CASH"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_CHECK IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CHECK"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_BANK_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BANK_CARD"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_GIFT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_GIFT_CARD"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_DISCNT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DISCNT_CARD"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_DEBIT IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DEBIT"
				+ " END"
				+ " + CASE"   //add by huangtt 20150519 start
				+ " WHEN PAY_BXZF IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_BXZF"
				+ " END"     //add by huangtt 20150519 end
				+ " + CASE"   //add by kangyue 20160628 start
				+ " WHEN PAY_TYPE09 IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_TYPE09"
				+ " END"     
				+ " + CASE"  
				+ " WHEN PAY_TYPE10 IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_TYPE10"
				+ " END"     //add by kangyue 20160628 end
				+ " + CASE"  
				+ " WHEN PAY_MEDICAL_CARD IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_MEDICAL_CARD"
				+ " END"     //add by kangyue 20171205 
				+ " ) AS SUM "
				+
				// $-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--end

				// $-------�ֽ�--start
				", SUM( CASE"
				+ " WHEN PAY_CASH IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CASH"
				+ " END ) PAY_CASH "
				+
				// $-------�ֽ�--end

				// $-------֧Ʊ--start
				", SUM( CASE"
				+ " WHEN PAY_CHECK IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CHECK"
				+ " END ) PAY_CHECK"
				+
				// $-------֧Ʊ--end

				// $-------ˢ��--start
				", SUM( CASE"
				+ " WHEN PAY_BANK_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BANK_CARD "
				+ " END ) PAY_BANK_CARD "
				+
				// $-------ˢ��--end

				// $-------��Ʒ��--start
				", SUM( CASE"
				+ " WHEN PAY_GIFT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_GIFT_CARD "
				+ " END ) PAY_GIFT_CARD "
				+
				// $-------��Ʒ��--end

				// $-------�ֽ��ۿ�ȯ--start
				", SUM( CASE"
				+ " WHEN PAY_DISCNT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DISCNT_CARD "
				+ " END ) PAY_DISCNT_CARD "
				+
				// $-------�ֽ��ۿ�ȯ--end
				
				//$-------����ֱ��--start-----huangtt 20150519
				", SUM( CASE" +
				" WHEN PAY_BXZF IS NULL" +
				" THEN 0" +
				" ELSE PAY_BXZF " +
				" END ) PAY_BXZF " +
				//$-------����ֱ��--end--- huangtt 20150519 
				
				//$-------΢��֧��--start-----kangyue 20160628
				", SUM( CASE" +
				" WHEN PAY_TYPE09 IS NULL" +
				" THEN 0" +
				" ELSE PAY_TYPE09 " +
				" END ) PAY_type09 " +
				//$-------΢��֧��--end--- kangyue 20160628
				
				//$-------֧����--start-----kangyue 20160628
				", SUM( CASE" +
				" WHEN PAY_TYPE10 IS NULL" +
				" THEN 0" +
				" ELSE PAY_TYPE10 " +
				" END ) PAY_type10 " +
				//$-------֧����--end--- kangyue 20160628
				
				//$-------ҽ�ƿ�--start-----kangyue 20171205
				", SUM( CASE" +
				" WHEN PAY_MEDICAL_CARD IS NULL" +
				" THEN 0" +
				" ELSE PAY_MEDICAL_CARD " +
				" END ) PAY_MEDICAL_CARD " +
				//$-------ҽ�ƿ�--end--- kangyue 20171205

				// $-------Ӧ�տ�--start
				", SUM( CASE"
				+ " WHEN PAY_DEBIT IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DEBIT "
				+ " END ) PAY_DEBIT "
				+
				// $-------Ӧ�տ�--end

				" FROM BIL_IBS_RECPM"
				+ " WHERE AR_AMT >= 0"
				+ " AND   CASE"
				+ " WHEN OWN_AMT IS NULL"
				+ " THEN 0"
				+ " ELSE OWN_AMT"
				+ " END"
				+ " - CASE"
				+ " WHEN PAY_BILPAY IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BILPAY"
				+ " END >= 0"
				+ " AND CASE"
				+ " WHEN PAY_BILPAY IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BILPAY"
				+ " END >= 0"
				+ " AND CASHIER_CODE = '"
				+ cashierCode
				+ "'"
				+ "AND  ACCOUNT_FLG IS NULL"
				+ " AND  CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')"
				//+ "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
				+ " AND (REFUND_DATE>= TO_DATE(TO_CHAR(CHARGE_DATE,'yyyyMMdd') " +
				" ||'235959','yyyyMMddHH24miss') OR REFUND_DATE IS NULL) " 
				+ "  ORDER BY RECEIPT_NO";
		TParm selRecpArParm = new TParm(TJDODBTool.getInstance().select(
				selRecpAr));
		double aFee = selRecpArParm.getDouble("SUM", 0);
		double acsFee = selRecpArParm.getDouble("PAY_CASH", 0);
		double ackFee = selRecpArParm.getDouble("PAY_CHECK", 0);
		double acardFee = selRecpArParm.getDouble("PAY_BANK_CARD", 0);
		double agiftFee = selRecpArParm.getDouble("PAY_GIFT_CARD", 0);
		double adisFee = selRecpArParm.getDouble("PAY_DISCNT_CARD", 0);
		double adebitFee = selRecpArParm.getDouble("PAY_DEBIT", 0);
		double abxzfFee = selRecpArParm.getDouble("PAY_BXZF", 0);
		double awxFee = selRecpArParm.getDouble("PAY_TYPE09", 0);
		double azfbFee = selRecpArParm.getDouble("PAY_TYPE10", 0);
		double amedicalFee = selRecpArParm.getDouble("PAY_MEDICAL_CARD", 0);
		
		String selRecpPPay =
			//$-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--start
		"SELECT SUM (  CASE"
				+ " WHEN PAY_CASH IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CASH"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_CHECK IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CHECK"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_BANK_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BANK_CARD"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_GIFT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_GIFT_CARD"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_DISCNT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DISCNT_CARD"
				+ " END"
				+ " + CASE"
				+ " WHEN PAY_DEBIT IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DEBIT"
				+ " END"
				+ " + CASE"   //add by huangtt 20150519 start
				+ " WHEN PAY_BXZF IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_BXZF"
				+ " END"     //add by huangtt 20150519 end
				+ " + CASE"   //add by kangyue 20160628 start
				+ " WHEN PAY_type09 IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_type09"
				+ " END"     
				+ " + CASE"  
				+ " WHEN PAY_type10 IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_type10"//add by kangyue 20160628 end
				+ " END"     
				+ " END"     
				+ " + CASE"  
				+ " WHEN PAY_MEDICAL_CARD IS NULL"  
				+ " THEN 0"
				+ " ELSE PAY_MEDICAL_CARD"//add by kangyue 201712-5
				+ " END"     
				
				+ " ) AS SUM "
				+
				// $-------�ܽ��=�ֽ�+֧Ʊ+ˢ��+�ֽ��ۿ�ȯ+��Ա��--end

				// $-------�ֽ�--start
				", SUM( CASE"
				+ " WHEN PAY_CASH IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CASH"
				+ " END ) PAY_CASH "
				+
				// $-------�ֽ�--end

				// $-------֧Ʊ--start
				", SUM( CASE"
				+ " WHEN PAY_CHECK IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_CHECK"
				+ " END ) PAY_CHECK"
				+
				// $-------֧Ʊ--end

				// $-------ˢ��--start
				", SUM( CASE"
				+ " WHEN PAY_BANK_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BANK_CARD "
				+ " END ) PAY_BANK_CARD "
				+
				// $-------ˢ��--end

				// $-------��Ʒ��--start
				", SUM( CASE"
				+ " WHEN PAY_GIFT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_GIFT_CARD "
				+ " END ) PAY_GIFT_CARD "
				+
				// $-------��Ʒ��--end

				// $-------�ֽ��ۿ�ȯ--start
				", SUM( CASE"
				+ " WHEN PAY_DISCNT_CARD IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DISCNT_CARD "
				+ " END ) PAY_DISCNT_CARD "
				+
				// $-------�ֽ��ۿ�ȯ--end
				//$-------����ֱ��--start-----huangtt 20150519
				", SUM( CASE" +
				" WHEN PAY_BXZF IS NULL" +
				" THEN 0" +
				" ELSE PAY_BXZF " +
				" END ) PAY_BXZF " +
				//$-------����ֱ��--end--- huangtt 20150519 
				
				//$-------΢��֧��--start-----huangtt 20150519
				", SUM( CASE" +
				" WHEN PAY_TYPE09 IS NULL" +
				" THEN 0" +
				" ELSE PAY_TYPE09 " +
				" END ) PAY_TYPE09 " +
				//$-------΢��֧��--end--- kangyue 20150519 
				//$-------֧����--start-----kangyue 20160628
				", SUM( CASE" +
				" WHEN PAY_TYPE10 IS NULL" +
				" THEN 0" +
				" ELSE PAY_TYPE10 " +
				" END ) PAY_TYPE10 " +
				//$-------֧����--end--- kangyue 20150519 60628
				//$-------ҽ�ƿ�--start-----kangyue 20171205
				", SUM( CASE" +
				" WHEN PAY_MEDICAL_CARD IS NULL" +
				" THEN 0" +
				" ELSE PAY_MEDICAL_CARD " +
				" END ) PAY_MEDICAL_CARD " +
				//$-------ҽ�ƿ�--end--- kangyue 20171205

				// $-------Ӧ�տ�--start
				", SUM( CASE"
				+ " WHEN PAY_DEBIT IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_DEBIT "
				+ " END ) PAY_DEBIT "
				+
				// $-------Ӧ�տ�--end

				" FROM BIL_IBS_RECPM"
				+ " WHERE AR_AMT >= 0"
				+ " AND   CASE"
				+ " WHEN OWN_AMT IS NULL"
				+ " THEN 0"
				+ " ELSE OWN_AMT"
				+ " END"
				+ " - CASE"
				+ " WHEN PAY_BILPAY IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BILPAY"
				+ " END < 0"
				+ " AND CASE"
				+ " WHEN PAY_BILPAY IS NULL"
				+ " THEN 0"
				+ " ELSE PAY_BILPAY"
				+ " END >= 0"
				+ " AND CASHIER_CODE = '"
				+ cashierCode
				+ "'"
				+ "AND  ACCOUNT_FLG IS NULL"
				+ " AND  CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')"
				//+ "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
				+ " AND (REFUND_DATE>= TO_DATE(TO_CHAR(CHARGE_DATE,'yyyyMMdd') " 
				+ " ||'235959','yyyyMMddHH24miss') OR REFUND_DATE IS NULL) " 
				+ "  ORDER BY RECEIPT_NO";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpPayPParm = new TParm(TJDODBTool.getInstance().select(
				selRecpPPay));
		double pFee = selRecpPayPParm.getDouble("SUM", 0);
		double pcsFee = selRecpPayPParm.getDouble("PAY_CASH", 0);
		double pckFee = selRecpPayPParm.getDouble("PAY_CHECK", 0);
//		double pcardFee = selRecpArParm.getDouble("PAY_BANK_CARD", 0);
//		double pgiftFee = selRecpArParm.getDouble("PAY_GIFT_CARD", 0);
//		double pdisFee = selRecpArParm.getDouble("PAY_DISCNT_CARD", 0);
//		double pdebitFee = selRecpArParm.getDouble("PAY_DEBIT", 0);
		double pcardFee = selRecpPayPParm.getDouble("PAY_BANK_CARD", 0);
		double pgiftFee = selRecpPayPParm.getDouble("PAY_GIFT_CARD", 0);
		double pdisFee = selRecpPayPParm.getDouble("PAY_DISCNT_CARD", 0);
		double pdebitFee = selRecpPayPParm.getDouble("PAY_DEBIT", 0);
		double pbxzfFee = selRecpPayPParm.getDouble("PAY_BXZF", 0);
		double pwxFee = selRecpPayPParm.getDouble("PAY_TYPE09", 0);
		double pzfbFee = selRecpPayPParm.getDouble("PAY_TYPE10", 0);
		double pmedicalFee = selRecpPayPParm.getDouble("PAY_MEDICAL_CARD", 0);
		
		double[] bFee = new double[23];
		bFee[0] = aFee;
		bFee[1] = pFee; 
		String ownSql = "SELECT SUM(OWN_AMT) OWN_AMT FROM BIL_IBS_RECPM WHERE CASHIER_CODE = '"
				+ cashierCode
				+ "'"
				+ "AND  ACCOUNT_FLG IS NULL"
				+ " AND  CHARGE_DATE < TO_DATE ('"
				+ chargeDate
				+ "', 'YYYYMMDDHH24MISS')"
				//+ "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
				+ " AND (REFUND_DATE>= TO_DATE(TO_CHAR(CHARGE_DATE,'yyyyMMdd') " 
				+ " ||'235959','yyyyMMddHH24miss') OR REFUND_DATE IS NULL) " 
				+ "  ORDER BY RECEIPT_NO";
		TParm ownParm = new TParm(TJDODBTool.getInstance().select(ownSql));
		double oFee = ownParm.getDouble("OWN_AMT", 0);
		bFee[2] = oFee;
		bFee[3] = acsFee;
		bFee[4] = ackFee;
		bFee[5] = pcsFee;
		bFee[6] = pckFee;
		bFee[7] = acardFee; 
		bFee[8] = agiftFee ;
		bFee[9] = adisFee ;
		bFee[10] =adebitFee;
		bFee[11] = pcardFee; 
		bFee[12] = pgiftFee ;
		bFee[13] = pdisFee ; 
		bFee[14] = pdebitFee;
		bFee[15] = abxzfFee;
		bFee[16] = pbxzfFee;
		bFee[17] = awxFee;
		bFee[18] = pwxFee;
		bFee[19] = azfbFee;
		bFee[20] = pzfbFee;
		bFee[21] = amedicalFee;
		bFee[22] = pmedicalFee;
		
		return bFee;
	}

	/**
	 * �ײʹ�ӡ���ݲ�ѯ��������ӡԤ��ר�ã�cashierCode���շ�Ա��chargeDate���������ڣ�
	 * @author xiongwg 20150713
	 * @param cashierCode
	 *            String
	 * @param chargeDate
	 *            String
	 * @return PCharge TParm
	 */
	public TParm onPrintPackDataPreview(String cashierCode,
			String chargeDate) {

		DecimalFormat df = new DecimalFormat("##########0.00");
		String sql = " SELECT A.REXP_CODE,SUM(A.WRT_OFF_AMT) TOT_AMT"
			    + "   FROM BIL_IBS_RECPD A,BIL_IBS_RECPM B  WHERE A.RECEIPT_NO = B.RECEIPT_NO "
			    + "  AND B.AR_AMT <> 0 AND B.LUMPWORK_AMT<>0 "
				+ " AND B.ACCOUNT_FLG IS NULL "
				+ " AND B.CASHIER_CODE = '"+cashierCode+"'"
				+ " AND B.CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')"
				+ " AND B.AR_AMT <> 0 AND B.LUMPWORK_AMT<>0  "
				+ " GROUP BY A.REXP_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm PCharge = chargeDoubleP(parm, df);
		String where ="";
		String sqlSUM = "SELECT SUM(A.LUMPWORK_AMT) AR_AMT FROM BIL_IBS_RECPM A,ADM_INP B "
		        + " WHERE A.CASE_NO=B.CASE_NO AND B.LUMPWORK_CODE IS NOT NULL "
				+ where
//				+ " AND A.AR_AMT > =0 "
				+ " AND A.ACCOUNT_FLG IS NULL "
				+ " AND A.CASHIER_CODE = '"+cashierCode+"'"
				+ " AND A.CHARGE_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')";
		
		where =" AND A.AR_AMT > =0 ";
		parm = new TParm(TJDODBTool.getInstance().select(sqlSUM+where));
		PCharge.setData("PARAMT", df.format(parm.getDouble("AR_AMT",0)));//�տ���
		where =" AND A.AR_AMT <0 ";
		parm = new TParm(TJDODBTool.getInstance().select(sqlSUM+where));
		PCharge.setData("PARAMTBACK", df.format(parm.getDouble("AR_AMT",0)));//�˿���
		where ="";
		parm = new TParm(TJDODBTool.getInstance().select(sqlSUM));
		PCharge.setData("PARAMTTOT", df.format(parm.getDouble("AR_AMT",0)));//ʵ�ս��
		// System.out.println("==="+PCharge);
		return PCharge;
	}

	/**
	 * �ײʹ�ӡ���ݲ�ѯ��������ӡר�� ��accountSeq���ս�ţ�
	 * @author xiongwg 20150713
	 * @param accountSeq
	 *            String
	 * @return PCharge TParm
	 */
	public TParm onPrintPackData(String accountSeq,String allReceiptNoStr) {
		DecimalFormat df = new DecimalFormat("##########0.00");
//		String sql = " SELECT A.REXP_CODE, SUM (A.TOT_AMT) AS TOT_AMT "
//				+ " FROM IBS_ORDD A WHERE BILL_NO IN ( SELECT C.BILL_NO "
//				+ " FROM BIL_IBS_RECPM B, IBS_BILLM C "
//				+ " WHERE B.RECEIPT_NO = C.RECEIPT_NO "
//				+ " AND B.RESET_RECEIPT_NO IS NULL "
//				+ " AND C.REFUND_FLG = 'N' AND B.AR_AMT <> 0 AND B.LUMPWORK_AMT<>0 "
//				+ " AND B.ACCOUNT_SEQ IN (" + accountSeq
//				+ ") GROUP BY C.BILL_NO) "
//				+" GROUP BY A.REXP_CODE";/
//		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String selRecpD = " SELECT A.REXP_CODE,SUM(A.WRT_OFF_AMT) TOT_AMT"
					+ "   FROM BIL_IBS_RECPD A,BIL_IBS_RECPM B  WHERE A.RECEIPT_NO = B.RECEIPT_NO "
					+ "  AND B.AR_AMT <> 0 AND B.LUMPWORK_AMT<>0  AND A.RECEIPT_NO IN ("
					+ allReceiptNoStr + ") GROUP BY A.REXP_CODE";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm parm= new TParm(TJDODBTool.getInstance()
				.select(selRecpD));	
		TParm PCharge = chargeDoubleP(parm, df);
		String where ="";
		String sqlSUM = "SELECT SUM(A.LUMPWORK_AMT) AR_AMT FROM BIL_IBS_RECPM A,ADM_INP B "
				+ " WHERE A.CASE_NO=B.CASE_NO AND B.LUMPWORK_CODE IS NOT NULL "
				+ where
//				+ " AND A.AR_AMT > =0 "
				+ "AND A.ACCOUNT_SEQ IN ("
				+ accountSeq
				+ ")";
		
		where =" AND A.AR_AMT > =0 ";
		parm = new TParm(TJDODBTool.getInstance().select(sqlSUM+where));
		PCharge.setData("PARAMT", df.format(parm.getDouble("AR_AMT",0)));//�տ���
		where =" AND A.AR_AMT <0 ";
		parm = new TParm(TJDODBTool.getInstance().select(sqlSUM+where));
		PCharge.setData("PARAMTBACK", df.format(parm.getDouble("AR_AMT",0)));//�˿���
		where ="";
		parm = new TParm(TJDODBTool.getInstance().select(sqlSUM+where));
		PCharge.setData("PARAMTTOT", df.format(parm.getDouble("AR_AMT",0)));//ʵ�ս��
		// System.out.println("==="+PCharge);
		return PCharge;

	}
	/**
	 * ����ײ��շ������ϸ���
	 * @param opdParm
	 * @param formatObject
	 * @return
	 * =========xiongwg 2015-7-13
	 */
	private TParm chargeDoubleP(TParm opdParm,DecimalFormat formatObject) {
		int opdCount = opdParm.getCount("REXP_CODE");
		String rexpCode = "";
		double arAmt = 0.00;
		double allArAmt = 0.00;
		//double[] chargeDouble=new double[30];
		String[] chargeName =new String[30];
		String sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='I'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		// �����ʷ��¼��ѯ�˲�������δ��Ʊ�������ܽ��
		int index = 1;
		for (int i = 0; i < 30; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				chargeName[i] = bilRecpParm
						.getData(chargeTemp + "0" + index, 0).toString();
			} else {
				chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0)
						.toString();
			}
			index++;
		}
		TParm p = new TParm();
		String idCharge="";
		for (int i = 0; i < opdCount; i++) {
			rexpCode = opdParm.getValue("REXP_CODE", i);
			arAmt = opdParm.getDouble("TOT_AMT", i);
			allArAmt = allArAmt + arAmt;
			for (int j = 0; j < chargeName.length; j++) {
				idCharge =chargeName[j];
				if (rexpCode.equals(idCharge)){
					if (j < 9) {
						p.setData("CHARGE0"+(j+1), formatObject.format(arAmt));
					}else{
						p.setData("CHARGE"+(j+1), formatObject.format(arAmt));
					}
					break;
				}
			}
		}
		p.setData("AR_AMT",allArAmt);
		return p;
	}
	 /**
     * �������Ʊ�Ŵ�ӡ����
     * ===zhangp 20120328
     * @param accountSeq String
     * @return TParm
     */
    private TParm getChangeTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'IBS' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND CANCEL_FLG = '2' ";
        //selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 ���˵�12λ�Ľ��л�����Ʊ�ݺ�
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // ��������ʾ�㷨
        int row = 0;
        if (count>0) {
        	row = 1;
		}
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //��ӡ����
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }
    private TParm getPrintCancelTableDateReview(String cashierCode,String chargeDate){
    	DecimalFormat df = new DecimalFormat("##########0.00");
		TParm parmData = new TParm();
		// parmData =
		String selMrNo = " SELECT INV_NO,AR_AMT FROM BIL_INVRCP WHERE RECP_TYPE = 'IBS' " +
				"AND ACCOUNT_SEQ IS NULL AND CANCEL_FLG = '3' AND CANCEL_USER="+cashierCode+" AND CANCEL_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')";
		parmData = new TParm(TJDODBTool.getInstance().select(selMrNo));
		int count = parmData.getCount("INV_NO");
		TParm aparm = new TParm();
		// ��������ʾ�㷨
		int row = 0;
		if (count>0) {
			row = 1;
		}
		
		int column = 0;
		for (int i = 0; i < count; i++) {

			aparm.addData("INV_NO_" + column, parmData.getData(
					"INV_NO", i));
			aparm.addData("AR_AMT_" + column, df.format(parmData.getDouble(
					"AR_AMT", i)));
			column++;
			if (column == 2) {
				column = 0;
				row++;
			}
		}
		aparm.setCount(row);
		// this.messageBox_("��������"+aparm);
		TParm printData = new TParm(); // ��ӡ����
		printData.setCount(row);
		printData = aparm;
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
		// System.out.println("��ӡ2����"+printData);
		return printData;
    }
    private TParm getPrintReturnTableDateReview(String cashierCode,String chargeDate){
    	DecimalFormat df = new DecimalFormat("##########0.00");
		TParm parmData = new TParm();
		// parmData =
		String selMrNo = " SELECT INV_NO,AR_AMT FROM BIL_INVRCP WHERE RECP_TYPE = 'IBS' " +
				"AND ACCOUNT_SEQ IS NULL  AND CANCEL_FLG = '1' AND CANCEL_DATE="+cashierCode+" AND PRINT_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')";
		parmData = new TParm(TJDODBTool.getInstance().select(selMrNo));
		int count = parmData.getCount("INV_NO");
		TParm aparm = new TParm();
		// ��������ʾ�㷨
		int row = 0;
		if (count>0) {
			row = 1;
		}
		int column = 0;
		for (int i = 0; i < count; i++) {

			aparm.addData("INV_NO_" + column, parmData.getData(
					"INV_NO", i));
			aparm.addData("AR_AMT_" + column, df.format(parmData.getDouble(
					"AR_AMT", i)));
			column++;
			if (column == 2) {
				column = 0;
				row++;
			}
		}
		aparm.setCount(row);
		// this.messageBox_("�˷�����"+aparm);
		TParm printData = new TParm(); // ��ӡ����
		printData.setCount(row);
		printData = aparm;
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
		printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
		// System.out.println("��ӡ1����"+printData);
		return printData;
    }
    private TParm getChangeTableDateReview(String cashierCode,String chargeDate){
    	 DecimalFormat df = new DecimalFormat("##########0.00");
         TParm parmData = new TParm();
         String selMrNo =
             " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
             "  WHERE RECP_TYPE = 'IBS' "+
             "    AND ACCOUNT_SEQ IS NULL "+
             "    AND CANCEL_FLG = '2' AND CANCEL_USER="+cashierCode+" AND CANCEL_DATE < TO_DATE ('"+chargeDate+"', 'YYYYMMDDHH24MISS')";
         //selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 ���˵�12λ�Ľ��л�����Ʊ�ݺ�
         parmData = new TParm(TJDODBTool.getInstance().select(
             selMrNo));
         int count = parmData.getCount("INV_NO");
         TParm aparm = new TParm();
         // ��������ʾ�㷨
         int row = 0;
         if (count>0) {
         	row = 1;
 		}
         int column = 0;
         for (int i = 0; i < count; i++) {

             aparm.addData("INV_NO_" + column,
                           parmData.getData("INV_NO", i));
             aparm.addData("AR_AMT_" + column,
                           df.format(parmData.getDouble("AR_AMT", i)));
             column++;
             if (column == 2) {
                 column = 0;
             }
         }
         if(count % 2 == 1){
             row = count / 2 + 1;
             aparm.addData("INV_NO_1", "");
             aparm.addData("AR_AMT_1", "");
         }else
             row = count/2;
         aparm.setCount(row);
         TParm printData = new TParm(); //��ӡ����
         printData.setCount(row);
         printData = aparm;
         printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
         printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
         printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
         printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
         return printData;
    }
    /**
     * ����Excel���
     */
    public void onExport() {
        TTable table = (TTable) callFunction("UI|Table|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("�޵�������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "סԺ�սᱨ��");
    }
}
