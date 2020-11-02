package jdo.opb;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.opd.OrderTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

import jdo.adm.ADMInpTool;
import jdo.bil.BILPrintTool;
import jdo.opd.OrderList;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILReduceTool;

import com.dongyang.data.TNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.PortableServer.ForwardRequestHelper;

import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.bil.BILContractRecordTool;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;

/**
 * <p>
 * Title: �������
 * </p>
 * 
 * <p>
 * Description:�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw 20090709
 * @version 1.0
 */
public class OPBTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static OPBTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INVVerifyinTool
	 */
	public static OPBTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OPBTool();
		return instanceObject;
	}

	private String[] chargName = { "CHARGE01", "CHARGE02", "CHARGE03",
			"CHARGE04", "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
			"CHARGE09", "CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13",
			"CHARGE14", "CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18",
			"CHARGE19", "CHARGE20", "CHARGE21", "CHARGE22", "CHARGE23",
			"CHARGE24", "CHARGE25", "CHARGE26", "CHARGE27", "CHARGE28",
			"CHARGE29", "CHARGE30" };
	// ҽ�ƿ� ɾ��ҽ������ ��ѡȡ����ť ִ�г���ɾ��ҽ������������ҽ��վʹ��==pangben 2013-5-2
	public static final String[] orderName = { "RX_NO", "SEQ_NO", "PRESRT_NO",
			"REGION_CODE", "MR_NO", "ADM_TYPE", "RX_TYPE", "TEMPORARY_FLG",
			"RELEASE_FLG", "LINKMAIN_FLG", "LINK_NO", "SPECIFICATION",
			"GOODS_DESC", "ORDER_CAT1_CODE", "MEDI_QTY", "MEDI_UNIT",
			"FREQ_CODE", "ROUTE_CODE", "TAKE_DAYS", "DOSAGE_QTY",
			"DISPENSE_QTY", "DISPENSE_UNIT", "GIVEBOX_FLG", "OWN_PRICE",
			"NHI_PRICE", "DISCOUNT_RATE", "OWN_AMT", "AR_AMT", "DR_NOTE",
			"NS_NOTE", "DR_CODE", "ORDER_DATE", "DEPT_CODE", "DC_DR_CODE",
			"DC_ORDER_DATE", "DC_DEPT_CODE", "EXEC_DEPT_CODE", "SETMAIN_FLG",
			"ORDERSET_GROUP_NO", "ORDERSET_CODE", "HIDE_FLG", "RPTTYPE_CODE",
			"OPTITEM_CODE", "DEV_CODE", "MR_CODE", "FILE_NO", "DEGREE_CODE",
			"URGENT_FLG", "INSPAY_TYPE", "PHA_TYPE", "DOSE_TYPE",
			"PRINTTYPEFLG_INFANT", "EXPENSIVE_FLG", "CTRLDRUGCLASS_CODE",
			"PRESCRIPT_NO", "ATC_FLG", "SENDATC_DATE", "RECEIPT_NO",
			"BILL_USER", "PRINT_FLG", "REXP_CODE", "HEXP_CODE",
			"CONTRACT_CODE", "CTZ1_CODE", "CTZ2_CODE", "CTZ3_CODE",
			"PHA_CHECK_CODE", "PHA_CHECK_DATE", "PHA_DOSAGE_CODE",
			"PHA_DOSAGE_DATE", "PHA_DISPENSE_CODE", "PHA_DISPENSE_DATE",
			"NS_EXEC_CODE", "NS_EXEC_DATE", "NS_EXEC_DEPT", "DCTAGENT_CODE",
			"DCTEXCEP_CODE", "DCT_TAKE_QTY", "MED_APPLY_NO", "DOSAGE_UNIT",
			"PACKAGE_TOT", "AGENCY_ORG_CODE", "DCTAGENT_FLG", "DECOCT_CODE",
			"EXEC_FLG", "RECEIPT_FLG", "BILL_TYPE", "CAT1_TYPE",
			"COST_CENTER_CODE", "EXEC_DR_CODE", "BUSINESS_NO", "PRINT_NO",
			"TRADE_ENG_DESC" };
	// ===pangben 2013-5-2 ����ҽ��վ��ղ��� ��ÿؼ�����
	public static final String[] controlName = { "QUE_NO", "PAT_NAME", "MR_NO",
			"SEX_CODE", "AGE", "WEIGHT", "PRE_WEEK", "CTZ1_CODE", "CTZ2_CODE",
			"CTZ3_CODE", "PAT1_CODE", "PAT2_CODE", "PAT3_CODE", "DRG_CODE",
			"LMP_DATE", "EXA_RX", "OP_RX", "MED_RX", "CHN_RX", "CTRL_RX",
			"OP_EXEC_DEPT", "MED_RBORDER_DEPT_CODE", "CHN_EXEC_DEPT_CODE",
			"CTRL_RBORDER_DEPT_CODE", "DR_NOTE", "CHN_FREQ_CODE",
			"CHN_ROUTE_CODE", "DCTAGENT_CODE", "DR_NOTE", "PACKAGE_TOT",
			"CHN_AMT", "BREASTFEED_STARTDATE", "BREASTFEED_ENDDATE" };
	public static final String[] controlNameAmt = { "EXA_CARD", "OP_CARD",
			"MED_CARD", "CHN_CARD", "CTRL_CARD", "EXA_AMT", "OP_AMT",
			"MED_AMT", "CTRL_AMT", "CHN_AMT", "PACKAGE_TOT" };// ҽ��վ���ؼ�
																// ===pangben
																// 2013-5-2
	public static final String[] controlNameCombo = { "EXA_RX", "OP_RX",
			"MED_RX", "CHN_RX", "CTRL_RX" };
	// ҽ��վ���ؼ� ===pangben 2013-5-2
	public static final String[] controlNameTable = { "TABLEEXA", "TABLEOP",
			"TABLEMED", "TABLECHN", "TABLECTRL", "TABLEDIAGNOSIS",
			"TABLEMEDHISTORY", "TABLEALLERGY" };

	/**
	 * ����case��ѯҽ��,���ڳ�ʼ������ǩ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm opbQuery(TParm parm) {
		TParm result = new TParm();
		// �õ������
		String caseNo = parm.getValue("CASE_NO");
		if (caseNo.length() == 0)
			return err(-1, "CASE_NO is null");
		// �õ�����order
		TParm orderresult = OrderTool.getInstance().query(caseNo);
		if (orderresult.getErrCode() != 0) {
			return result.newErrParm(-1, "��ȡҽ������!");
		}
		// ����order
		result.setData("ORDER", orderresult.getData());
		return result;

	}

	/**
	 * �����շѱ���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm opbCharge(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ����ҽ��
		TParm orderparm = parm.getParm("ORDER");
		orderparm.setData("FLG", parm.getData("FLG"));
		result = OrderTool.getInstance().onSaveOPB(orderparm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * ��ҽ�����Ʊ����ˮ��
	 * 
	 * @param parm
	 *            TParm
	 * @param receiptNo
	 *            String
	 */
	public void dealOrderReceiptNo(TParm parm, String receiptNo) {
		TParm newOrderParm = parm.getParm(OrderList.NEW);
		int row = newOrderParm.getCount();
		for (int i = 0; i < row; i++) {
			newOrderParm.setData("RECEIPT_NO", i, receiptNo);
		}
		TParm modifyOrderParm = parm.getParm(OrderList.MODIFIED);
		row = modifyOrderParm.getCount();
		for (int i = 0; i < row; i++) {
			modifyOrderParm.setData("RECEIPT_NO", i, receiptNo);
		}

	}

	/**
	 * �����˷�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm backReceipt(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// �õ��˷Ѳ��븺����Ʊ��
		TParm receiptBaceParm = parm.getParm("INRECEIPT");
		// ����Ʊ�ݵ�
		result = OPBReceiptTool.getInstance().insertBackReceipt(
				receiptBaceParm, false, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		// ��̨ȡ��ԭ��ȡ���Ĵ�Ʊ��
		String receiptNo = result.getData("RECEIPT_NO").toString();
		// ���µ��˷�Ʊ��
		TParm upReceiptParm = parm.getParm("UPRECEIPT");
		upReceiptParm.setData("RESET_RECEIPT_NO", receiptNo);
		result = OPBReceiptTool.getInstance().updateBackReceipt(upReceiptParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		// ����Ʊ��
		TParm bilInvrcptParm = parm.getParm("BILINVRCPT");
		result = BILInvrcptTool.getInstance().updataData(bilInvrcptParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		// ����ҽ��
		TParm orderListParm = parm.getParm("ORDER");
		orderListParm.setData("FLG", "N");
		result = OrderTool.getInstance().onUpdate(orderListParm, connection);
		if (result.getErrCode() < 0)
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		return result;

	}

	/**
	 * �����շѲ���Ʊ��:ҽ�ƿ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */

	public TParm saveOPBEKTRePrint(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = updateBilinvoice(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * �޸�ҽ������Ʊ�ݺ���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateInsInvNo(TParm parm, TConnection connection) {
		TParm tempParm = new TParm();
		// System.out.println("---------------ҽ���˷�-------------:" + opbParm);
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		tempParm.setData("INV_NO", parm.getValue("PRINT_NO"));
		tempParm.setData("RECP_TYPE", "OPB"); // �շ�����

		// ��ѯ�Ƿ�ҽ�� �˷�
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(tempParm);
		if (result.getErrCode() < 0) {
			return result;
		}
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			tempParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			tempParm.setData("INV_NO", parm.getValue("NEW_PRINT_NO")); // �µ�Ʊ�ݺ���
			// �޸�ҽ������Ʊ�ݺ���
			result = INSOpdTJTool.getInstance().updateInsOpdInvNo(tempParm,
					connection);
			if (result.getErrCode() < 0) {
				return result;
			}
			// �޸�ҽ��Ʊ�ݺ���
			result = INSOpdOrderTJTool.getInstance().updateInsOpdOrderInvNo(
					tempParm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * ����Ʊ���������ֽ��ҽ�ƿ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateBilinvoice(TParm parm, TConnection connection) {

		String caseNo = parm.getValue("CASE_NO");
		TParm selInvoice = new TParm();
		selInvoice.setData("STATUS", "0");
		selInvoice.setData("RECP_TYPE", "OPB");
		int ektCount = parm.getInt("COUNT"); // �ж��Ƿ�ִ��ҽ�ƿ���ӡ����
		selInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		selInvoice.setData("TERM_IP", parm.getData("OPT_TERM"));
		// System.out.println("Ʊ����������" + selInvoice);
		TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
				selInvoice);
		String invNo = invoice.getValue("UPDATE_NO", 0);
		// System.out.println("Ʊ����������" + invoice);
		TParm prtInvoice = new TParm();
		prtInvoice.setData("UPDATE_NO", StringTool.addString(invNo));
		prtInvoice.setData("RECP_TYPE", "OPB");
		prtInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		prtInvoice.setData("STATUS", "0");
		prtInvoice.setData("START_INVNO", invoice.getData("START_INVNO", 0));
		// ======pangben 2012-3-19
		parm.setData("NEW_PRINT_NO", invNo);
		TParm result = updateInsInvNo(parm, connection); // �޸�ҽ��Ʊ�ݺ���
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// System.out.println("Ʊ����������All======"+prtInvoice);
		// ���ø���Ʊ��
		result = BILInvoiceTool.getInstance().updateDatePrint(prtInvoice,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ������ú�Ʊ�ݲ���
		TParm opbReceipt = new TParm();
		opbReceipt.setData("OPT_USER", parm.getData("OPT_USER"));
		opbReceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
		opbReceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// ����ԭ��Ʊ�ݲ���
		TParm selInvrcp = new TParm();
		selInvrcp.setData("INV_NO", parm.getData("PRINT_NO"));
		// ����ԭ��Ʊ�ݲ���
		TParm invRcpParm = new TParm();
		invRcpParm.setData("CANCEL_FLG", "3");
		invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
		invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

		// Ʊ����ϸ������һ��������
		TParm insertInvrcp = new TParm();
		if (ektCount > 0) { // ҽ�ƿ���ӡ
			opbReceipt.setData("CASE_NO", caseNo);
			opbReceipt.setData("NEWPRINT_NO", invNo);
			opbReceipt.setData("PRINT_NO", parm.getValue("PRINT_NO"));
			selInvrcp.setData("RECP_TYPE", "OPB");
			invRcpParm.setData("RECP_TYPE", "OPB");
			insertInvrcp.setData("RECP_TYPE", "OPB");
			result = OPBReceiptTool.getInstance().updateEKTPrintNO(opbReceipt,
					connection);
		} else { // ����
			opbReceipt.setData("CASE_NO", caseNo);
			opbReceipt.setData("PRINT_NO", invNo);
			opbReceipt.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
			selInvrcp.setData("RECP_TYPE", "OPB");
			invRcpParm.setData("RECP_TYPE", "OPB");
			insertInvrcp.setData("RECP_TYPE", "OPB");
			// ����Ʊ�ݺ�
			result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
					connection);
		}

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		// ��ѯԭƱ����ϸ��
		TParm oneInvParm = BILInvrcptTool.getInstance().getOneInv(selInvrcp);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		invRcpParm.setData("INV_NO", oneInvParm.getData("INV_NO", 0));
		// ��ӡ,����Ʊ����ϸ��Ʊ��״̬
		result = BILInvrcptTool.getInstance()
				.updataData(invRcpParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		insertInvrcp.setData("INV_NO", invNo);
		insertInvrcp.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
		insertInvrcp.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		insertInvrcp.setData("AR_AMT", oneInvParm.getValue("AR_AMT", 0));
		insertInvrcp.setData("CANCEL_FLG", "0");
		insertInvrcp.setData("STATUS", "0");
		insertInvrcp.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		insertInvrcp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		insertInvrcp.setData("OPT_USER", parm.getData("OPT_USER"));
		result = BILInvrcptTool.getInstance().insertData(insertInvrcp,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		result.setData("PRINT_NO", invNo);
		result.setData("OLDPRINT_NO", parm.getValue("PRINT_NO"));
		return result;
	}

	/**
	 * �����շѲ���Ʊ��:�ֽ𲹴�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm saveOPBRePrint(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = updateBilinvoice(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * ������
	 * 
	 * @param parm
	 *            TParm
	 * @param chargeDouble
	 *            double[]
	 * @return TParm
	 */
	private TParm getInsertAmt(TParm parm, double[] chargeDouble) {
		TParm opbreceipt = new TParm();
		opbreceipt.setData("CASE_NO", parm.getData("CASE_NO"));
		opbreceipt.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		opbreceipt.setData("REGION_CODE", parm.getData("REGION_CODE"));
		opbreceipt.setData("MR_NO", parm.getData("MR_NO"));
		opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
		opbreceipt.setData("PRINT_NO", parm.getData("INV_NO"));
		opbreceipt.setData("BILL_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("PRINT_DATE", SystemTool.getInstance().getDate());
		int index = 1;
		//��ѯ��������� yanjing 20141215
		String reduceNo=parm.getParm("parmReduce").getParm("parmForReduceM").getValue("REDUCE_NO");
		double[] reduceDouble = new double[30]; 
		if(null!=reduceNo&&!reduceNo.equals("")){//�м���ʱ��ȥ����Ĳ���
			reduceDouble = this.selectReducedForAmt(reduceNo);
			// д������
			for (int i = 0; i < chargeDouble.length; i++) {
				String chargeTemp = "CHARGE";
				
				if (i < 9) {
					opbreceipt.setData(chargeTemp + "0" + index, StringTool.round(chargeDouble[i]-reduceDouble[i], 2));
				} else {
					opbreceipt.setData(chargeTemp + index, StringTool.round(chargeDouble[i]-reduceDouble[i], 2));
				}
				index++;
			}
			
		}else{
			// д������
			for (int i = 0; i < chargeDouble.length; i++) {
				String chargeTemp = "CHARGE";
				if (i < 9) {
					opbreceipt.setData(chargeTemp + "0" + index, chargeDouble[i]);
				} else {
					opbreceipt.setData(chargeTemp + index, chargeDouble[i]);
				}
				index++;
			}
		}
		return opbreceipt;
	}
	/**
	 * ����reduceNo��ѯbil_reduceD���еļ�����
	 * yanjing 20141215 add
	 */
	private double[] selectReducedForAmt(String reduceNo){
		double[] chargeDouble = new double[30]; 
		String sql = "SELECT REXP_CODE,SUM(REDUCE_AMT) AS REDUCE_AMT FROM BIL_REDUCED WHERE REDUCE_NO IS NOT NULL AND REDUCE_NO = '"+reduceNo+"' GROUP BY REXP_CODE,REDUCE_AMT ";
		TParm result = new TParm (TJDODBTool.getInstance().select(sql));
		 sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
		TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		int chargeCount = sysChargeParm.getCount("ID");
		String[] chargeName = new String[30];
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
		for (int i = 0; i < chargeCount; i++) {
			String sysChargeId = sysChargeParm.getData("ID", i).toString();
			for (int j = 0; j < chargeName.length; j++) {
				if (sysChargeId.equals(chargeName[j])) {
					p.setData("CHARGE", i, j);
					p.setData("ID", i, sysChargeParm.getData("ID", i));
					p.setData("CHN_DESC", i, sysChargeParm.getData("CHN_DESC",
							i));
					break;
				}
			}
		}
		for(int i = 0;i<result.getCount("REXP_CODE");i++){
			String rexpCode = result.getValue("REXP_CODE", i);
			for (int j = 0; j < p.getCount("ID"); j++) {
				String idCharge = p.getData("ID", j).toString();
				int charge = p.getInt("CHARGE", j);
				if (rexpCode.equals(idCharge)){
					
					chargeDouble[charge] = StringTool.round(chargeDouble[charge]+result.getDouble("REDUCE_AMT", i),2);
			}	
		}
		}
		return chargeDouble;
	}

	/**
	 * ����ҽ��վ�޸�ҽ������ ���һ����ֵ�վ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm reSetInsertOpbRecp(TParm parm, TConnection connection) {
		TParm temp = parm.getRow(0);
		temp.setData("OPT_USER_T", temp.getValue("OPT_USER"));
		temp.setData("OPT_TERM_T", temp.getValue("OPT_TERM"));
		TParm result = OPBReceiptTool.getInstance().insertBackReceipt(
				getOpbreceiptTemp(temp, false), false, connection);
		return result;
	}

	/**
	 * �ײʹ�Ʊ
	 */
	public TParm onOpbMemPackPrint(TParm parm, TConnection connection){
		TParm bilRecpParm = null;
		TParm exeOrderParm = null;
		TParm bilInvrcpParm = null;
		TParm result = null;
		String sql ="";
		TParm bilinvoiceParm = parm.getParm("bilinvoiceParm");
		TParm exeOrderList =  parm.getParm("exeOrderList");
		TParm bilInvrcpList =  parm.getParm("bilInvrcpList");
		TParm bilOpbRecpList =  parm.getParm("bilOpbRecpList");
		for(int i= 0; i< bilOpbRecpList.getCount("bilRecpParm");i++){
			bilRecpParm = bilOpbRecpList.getParm("bilRecpParm",i);
			exeOrderParm = exeOrderList.getParm("exeOrderParm",i);
			bilInvrcpParm = bilInvrcpList.getParm("bilInvrcpParm",i);
			result = OPBReceiptTool.getInstance().insertReceiptMemPrint(bilRecpParm,
						connection);
			if(result.getErrCode()<0){
				return result;
			}
			result = BILInvrcptTool.getInstance().insertData(bilInvrcpParm,connection);
			if(result.getErrCode()<0){
				return result;
			}
			for(int j= 0; j< exeOrderParm.getCount("CASE_NO");j++){
				sql=" UPDATE OPD_ORDER  SET PRINT_FLG = 'Y', RECEIPT_NO = '" + bilRecpParm.getValue("RECEIPT_NO") + "'" +
						" WHERE CASE_NO = '" + exeOrderParm.getValue("CASE_NO",j) 
						+ "' AND RX_NO = '" + exeOrderParm.getValue("RX_NO",j)  + "' AND SEQ_NO = " + exeOrderParm.getValue("SEQ_NO",j);
				result = new TParm(TJDODBTool.getInstance().update(sql, connection));
				if(result.getErrCode()<0){
					return result;
				}
			}
		}
		result = BILInvoiceTool.getInstance().updateDatePrint(bilinvoiceParm,connection);
		if(result.getErrCode()<0){
			return result;
		}
		return result;
	}
	/**
	 * �����շ�ҽ�ƿ���Ʊ
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm =============pangben 201110224 �޸�ҽ�ƿ���Ʊ�������վݺ�
	 */
	public TParm onOPBEktprint(TParm parm, TConnection connection) {
		// double insAmt=getInsAmt(parm);//���ҽ���ۿ���
		TParm parmReduce = parm.getParm("parmReduce");
		TParm opbParm = parm.getParm("opdParm"); // �����Ҫ���ܵ�����
		String caseNo = parm.getValue("CASE_NO");
		  //ȡ��ԭ��õ�Ʊ�ݺ�
        String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO",
                "RECEIPT_NO");
		if ("Y".equals(parmReduce.getData("REDUCEFLG"))) {
//			parmReduce.setData("REDUCELAST_AMT",reduceLastAmt);
//			parmReduce.setData("REDUCELAST_AMT",opbreceipt.getDouble("PAY_MEDICAL_CARD")-
//					opbreceipt.getDouble("REDUCE_AMT")>=0?0:opbreceipt.getDouble("REDUCE_AMT")
//							-opbreceipt.getDouble("PAY_MEDICAL_CARD")-reduceLastAmt);//====pangben ������ʣ����
			parmReduce.setData("RECEIPT_NO", receiptNo);
			parmReduce.setData("OPT_USER", parm.getData("OPT_USER"));
			parmReduce.setData("OPT_TERM", parm.getData("OPT_TERM"));
			parmReduce.setData("MR_NO", parm.getData("MR_NO"));
//			parmReduce.setData("OPB_RECEIPT_PARM",opbreceipt.getData());
			parmReduce.setData("OPB_EXE_BILPRINT","Y");//�����̲�����Ʊ���̣���������س����Ʒ�����ֽ��ۿ�ȯ
//			System.out.println("�������������parmReduce is����"+parmReduce);
			
			TParm BILReduceParm=BILReduceTool.getInstance().onInsertOPDBilReduce(parmReduce, connection);
			if (BILReduceParm.getErrCode() < 0) {
				err("ERR:" + BILReduceParm.getErrCode() + BILReduceParm.getErrText()
						+ BILReduceParm.getErrName());
				return BILReduceParm;
			}
			// �����޸�caowl..........end.............
		}
		double[] chargeDouble = new double[30]; // ������
		double[] sumAmt = chargeDouble(opbParm, chargeDouble, 1, caseNo);
		Map<String, Double> map = getPayOthers(opbParm);

		TParm opbreceipt = getInsertAmt(parm, chargeDouble);

		// System.out.println("��ӡ�վ����:::::::"+opbParm);
		TParm result = new TParm();
		double accountAmt = parm.getDouble("ACCOUNT_AMT"); // ҽ�����
		TParm bilInvricpt = new TParm();
		bilInvricpt.setData("RECP_TYPE", "OPB"); // ҽ�ƿ���Ʊ
		bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
		bilInvricpt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		bilInvricpt.setData("OPT_USER", parm.getData("OPT_USER"));
		bilInvricpt.setData("INV_NO", parm.getData("INV_NO"));
		bilInvricpt.setData("OPT_TERM", parm.getData("OPT_TERM"));
		bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
		bilInvricpt.setData("TOT_AMT", sumAmt[3]);

		bilInvricpt.setData("AR_AMT", sumAmt[0]);
		// bilInvricpt.setData("INS_AMT", insAmt);//ҽ���ۿ���
		TParm bilInvoice = new TParm();
		bilInvoice.setData("RECP_TYPE", "OPB");
		bilInvoice.setData("STATUS", "0");
		bilInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
		bilInvoice.setData("UPDATE_NO", StringTool.addString(parm.getData(
				"INV_NO").toString()));
		TParm actionParm = new TParm();
		actionParm.setData("BILINVRICPT", bilInvricpt.getData());
		actionParm.setData("BILINVOICE", bilInvoice.getData());
		actionParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); // ===pangben
																	// 2012-3-19
		actionParm.setData("RECEIPT_NO", receiptNo);
		result = BILPrintTool.getInstance().saveEktOpb(actionParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		String recpNo = result.getValue("RECEIPT_NO", 0); // ���µ��վݺ���
//		TParm parmReduce = parm.getParm("parmReduce");
		double reduceAmt=0.0;
		if ("Y".equals(parmReduce.getData("REDUCEFLG"))) {
			parmReduce.setData("RECEIPT_NO", recpNo);
			reduceAmt=parmReduce.getDouble("REDUCE_AMT");
			opbreceipt.setData("AR_AMT", TypeTool.getDouble(sumAmt[0])-reduceAmt); // �ܽ��
			opbreceipt.setData("REDUCE_AMT",reduceAmt);
			opbreceipt.setData("REDUCE_NO", parmReduce.getValue("REDUCE_NO"));
			opbreceipt.setData("REDUCE_DATE",SystemTool.getInstance().getDate());

		}else{
			opbreceipt.setData("AR_AMT", sumAmt[0]); // �ܽ��
			opbreceipt.setData("REDUCE_AMT", 0.00);	
			opbreceipt.setData("REDUCE_NO", new TNull(String.class));
			opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
		}
		opbreceipt.setData("TOT_AMT", sumAmt[3]); // �ܽ��
		opbreceipt.setData("RECEIPT_NO", recpNo);
		opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
		opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
		opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
		opbreceipt.setData("PAY_CASH", 0.00);
		double reduceLastAmt=0.00;//����ʣ����===pangben 2014-7-17
		double medicalCard=0.00;
		if (sumAmt[2] > 0) { // sumAmt[2]);��ɫͨ�����
			// �˴β���ʹ����ɫͨ��
			// �ж�ʹ����ɫͨ���Ľ���Ƿ����ҽ�����
			if (sumAmt[2] >= accountAmt) {
				opbreceipt.setData("PAY_OTHER1", sumAmt[2] - accountAmt); // ��ɫͨ�����
				//=====yanjing ע start
//				if (reduceAmt>0) {//�������ʱ������˴β�������Ʒ�����ֽ��ۿ�ȯ�Ľ��ͻ����ҽ�ƿ����С�ڼ����� �����ҽ�ƿ�����и�ֵ
//					if (sumAmt[1]
//							- map.get(EKTpreDebtTool.PAY_TOHER3)
//							- map.get(EKTpreDebtTool.PAY_TOHER4)-reduceAmt<0) {
//						//ҽ�ƿ����Ϊ0 
//						//�������ʣ����ӵ���Ʒ�������ֽ��ۿ�ȯ��
//						reduceLastAmt=reduceAmt-sumAmt[1]+map.get(EKTpreDebtTool.PAY_TOHER3)+map.get(EKTpreDebtTool.PAY_TOHER4);
//					}else{
//						medicalCard=sumAmt[1]
//							                 - map.get(EKTpreDebtTool.PAY_TOHER3)
//							                 - map.get(EKTpreDebtTool.PAY_TOHER4);
////						-reduceAmt;// �۳�ҽ�����//yanjing 20141211 ע
//						//opbreceipt.setData("PAY_MEDICAL_CARD", ); 
//					}
//				}else{
						medicalCard=sumAmt[1]- map.get(EKTpreDebtTool.PAY_TOHER3)
											- map.get(EKTpreDebtTool.PAY_TOHER4);
//						-reduceAmt;// �۳�ҽ�����//yanjing 20141211 ע
					//opbreceipt.setData("PAY_MEDICAL_CARD", ); 
//				}
						//=====yanjing ע end
			} else {
				//=====yanjing ע start
//				if (reduceAmt>0) {
//					if (sumAmt[1] - sumAmt[2]
//								+ accountAmt - map.get(EKTpreDebtTool.PAY_TOHER3)
//								- map.get(EKTpreDebtTool.PAY_TOHER4)-reduceAmt<0) {
//							//opbreceipt.setData("PAY_MEDICAL_CARD",0);//ҽ�ƿ����Ϊ0 
//							//�������ʣ����ӵ���Ʒ�������ֽ��ۿ�ȯ��
//							reduceLastAmt=reduceAmt-sumAmt[1]+sumAmt[2]+map.get(EKTpreDebtTool.PAY_TOHER3)+map.get(EKTpreDebtTool.PAY_TOHER4)-accountAmt;
//						}else{
//							medicalCard=sumAmt[1]
//					                 - map.get(EKTpreDebtTool.PAY_TOHER3)
//					                 - map.get(EKTpreDebtTool.PAY_TOHER4);
////					                 -reduceAmt;//yanjing 20141211 ע
//							//opbreceipt.setData("PAY_MEDICAL_CARD", ); // �۳�ҽ�����
//						}
//					opbreceipt.setData("PAY_OTHER1", 0.00); // ��ɫͨ�����
//				}else{
				//=====yanjing ע end
					// ʹ����ɫͨ�����С��ҽ�����
					opbreceipt.setData("PAY_OTHER1", 0.00); // ��ɫͨ�����
					medicalCard=sumAmt[1] - sumAmt[2]
					   							+ accountAmt - map.get(EKTpreDebtTool.PAY_TOHER3)
												- map.get(EKTpreDebtTool.PAY_TOHER4);
//												-reduceAmt;//yanjing 20141211 ע
					//opbreceipt.setData("PAY_MEDICAL_CARD", ); // �۳�ҽ�����
//				}
			}
		} else {
			//=====yanjing ע start
//			if (reduceAmt>0) {
//				if (sumAmt[1] - accountAmt
//						- map.get(EKTpreDebtTool.PAY_TOHER3)
//						- map.get(EKTpreDebtTool.PAY_TOHER4)-reduceAmt<0) {
//					//opbreceipt.setData("PAY_MEDICAL_CARD",0);//ҽ�ƿ����Ϊ0 
//					//�������ʣ����ӵ���Ʒ�������ֽ��ۿ�ȯ��
//					reduceLastAmt=reduceAmt-sumAmt[1]+accountAmt+map.get(EKTpreDebtTool.PAY_TOHER3)+map.get(EKTpreDebtTool.PAY_TOHER4);
//				}else{
//					medicalCard=sumAmt[1] - accountAmt
//						- map.get(EKTpreDebtTool.PAY_TOHER3)
//						- map.get(EKTpreDebtTool.PAY_TOHER4);
////						-reduceAmt;//yanjing 20141211 ע
//								//opbreceipt.setData("PAY_MEDICAL_CARD", ); // �۳�ҽ�����
//			   }
//			}else{
			//=====yanjing ע end
				medicalCard=sumAmt[1] - accountAmt
					- map.get(EKTpreDebtTool.PAY_TOHER3)
					- map.get(EKTpreDebtTool.PAY_TOHER4);
//				-reduceAmt;//yanjing 20141211 ע
				//opbreceipt.setData("PAY_MEDICAL_CARD", ); // �۳�ҽ�����
//			}
			// û��ʹ����ɫͨ��
			opbreceipt.setData("PAY_OTHER1", 0.00); // ��ɫͨ�����
		}
		opbreceipt.setData("PAY_MEDICAL_CARD", medicalCard);//=====pangben 2014-7-17 ҽ�ƿ�����޸�
		opbreceipt.setData("PAY_BANK_CARD", 0.00);
		opbreceipt.setData("PAY_INS_CARD", accountAmt);
		opbreceipt.setData("PAY_CHECK", 0.00);
		opbreceipt.setData("PAY_DEBIT", 0.00);
		opbreceipt.setData("PAY_BILPAY", 0.00);
		opbreceipt.setData("PAY_INS", 0.00);
		opbreceipt.setData("PAY_OTHER2", 0.00);
		opbreceipt.setData("PAY_REMARK", new TNull(String.class));
		opbreceipt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		opbreceipt.setData("OPT_USER", parm.getData("OPT_USER"));
		opbreceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
		opbreceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
		opbreceipt.setData("FLG", "Y");
		//====yanjing 20141224 ע start
		// System.out.println("���һ����������opbreceipt:::"+opbreceipt);
//		if (reduceLastAmt>0) {
//			if (reduceLastAmt>map.get(EKTpreDebtTool.PAY_TOHER3)) {//����ʣ�����ۼƵ���Ʒ�����ֽ��ۿ�ȯ===pangben 2014-7-17
//				opbreceipt.setData("PAY_OTHER3", 0);
//				opbreceipt.setData("PAY_OTHER4", map.get(EKTpreDebtTool.PAY_TOHER4)-reduceLastAmt+map.get(EKTpreDebtTool.PAY_TOHER3));
//			}else{
//				opbreceipt.setData("PAY_OTHER3", map.get(EKTpreDebtTool.PAY_TOHER3)-reduceLastAmt);
//				opbreceipt.setData("PAY_OTHER4", map.get(EKTpreDebtTool.PAY_TOHER4));
//			}
//		}else{
		//====yanjing 20141224 ע end
			opbreceipt.setData("PAY_OTHER3", map.get(EKTpreDebtTool.PAY_TOHER3));
			opbreceipt.setData("PAY_OTHER4", map.get(EKTpreDebtTool.PAY_TOHER4));
//		}
		if (parm.getValue("TAX_FLG").length() > 0&&parm.getValue("TAX_FLG").equals("Y")) {
			opbreceipt.setData("TAX_FLG","Y");
			opbreceipt.setData("TAX_DATE",SystemTool.getInstance().getDate());
			opbreceipt.setData("TAX_USER",parm.getData("OPT_USER"));
		}else{
			opbreceipt.setData("TAX_FLG","N");
			opbreceipt.setData("TAX_DATE",new TNull(Timestamp.class));
			opbreceipt.setData("TAX_USER","");
		}
		// ���һ����������
		result = OPBReceiptTool.getInstance().initReceipt(opbreceipt,
				connection);
//		System.out.println("result=="+result); 
		//add by huangtt 20141216 start
		//���Ϊ�ײʹ�Ʊ������bil_opb_recp����mem_pack_flgΪY
		boolean memPackFlg = true;
		for (int i = 0; i < opbParm.getCount("ORDER_CODE"); i++) {
//			System.out.println("MEM_PACKAGE_ID==="+opbParm.getValue("MEM_PACKAGE_ID", i));
			if( "".equals(opbParm.getValue("MEM_PACKAGE_ID", i))){
				memPackFlg = false;
			}
		}
//		System.out.println("caseNo1=="+opbreceipt.getValue("CASE_NO"));
//		System.out.println("caseNo2=="+caseNo);
//		System.out.println("memPackFlg=="+memPackFlg);
//		System.out.println("RECEIPT_NO=="+opbreceipt.getData("RECEIPT_NO"));
		if(memPackFlg){
			TParm memPackParm = new TParm();
			memPackParm.setData("RECEIPT_NO", opbreceipt.getData("RECEIPT_NO")); 
			memPackParm.setData("CASE_NO", caseNo);
			TParm resultPack = OPBReceiptTool.getInstance().updateReceiptMemPackFlg(memPackParm, connection);
			if (resultPack.getErrCode() < 0) {
				err("ERR:" + resultPack.getErrCode() + resultPack.getErrText()
						+ resultPack.getErrName());
				return resultPack;
			}
		}
		//add by huangtt 20141216 end
		
		
//		TParm updateBilOpbRecp = new TParm(TJDODBTool.getInstance().update(sql,
//				connection));
		//======pangben 2014-7-17 ���������Ʒ�����ֽ��ۿ�
		opbreceipt.setData("PAY_OTHER3_TEMP", map.get(EKTpreDebtTool.PAY_TOHER3));
		opbreceipt.setData("PAY_OTHER4_TEMP", map.get(EKTpreDebtTool.PAY_TOHER4));
//		if (updateBilOpbRecp.getErrCode() < 0) {
//			err("ERR:" + updateBilOpbRecp.getErrCode()
//					+ updateBilOpbRecp.getErrText()
//					+ updateBilOpbRecp.getErrName());
//			return updateBilOpbRecp;
//		}
		TParm upOpdParm = new TParm();
		// upOpdParm.setData("RECEIPT_NO",recpNo);
		upOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));
		upOpdParm.setData("RECEIPT_NO", recpNo); // ���µ��վݺ���
		upOpdParm.setData("PRINT_NO", parm.getData("INV_NO")); // �޸�ҽ�ƿ���ƱƱ�ݺ�
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// �޸�ҽ��
		//zhangp
//		TParm upOpd = OrderTool.getInstance().updateForOPBEKT(upOpdParm,
//				connection);
//		if (upOpd.getErrCode() < 0) {
//			err("ERR:" + upOpd.getErrCode() + upOpd.getErrText()
//					+ upOpd.getErrName());
//			return upOpd;
//		}
		TParm upOpd;
		String rxNo;
		int seqNo;
		for (int i = 0; i < opbParm.getCount("CASE_NO"); i++) {
			rxNo = opbParm.getValue("RX_NO", i);
			seqNo = opbParm.getInt("SEQ_NO", i);
			upOpd = updateOpdOrder(caseNo, rxNo, seqNo, recpNo, connection);
			if (upOpd.getErrCode() < 0) {
				err("ERR:" + upOpd.getErrCode() + upOpd.getErrText()
						+ upOpd.getErrName());
				return upOpd;
			}
		}
		// �����޸�caowl..........start...........
//		if ("Y".equals(parmReduce.getData("REDUCEFLG"))) {
////			parmReduce.setData("REDUCELAST_AMT",reduceLastAmt);
////			parmReduce.setData("REDUCELAST_AMT",opbreceipt.getDouble("PAY_MEDICAL_CARD")-
////					opbreceipt.getDouble("REDUCE_AMT")>=0?0:opbreceipt.getDouble("REDUCE_AMT")
////							-opbreceipt.getDouble("PAY_MEDICAL_CARD")-reduceLastAmt);//====pangben ������ʣ����
//			parmReduce.setData("OPT_USER", parm.getData("OPT_USER"));
//			parmReduce.setData("OPT_TERM", parm.getData("OPT_TERM"));
//			parmReduce.setData("MR_NO", parm.getData("MR_NO"));
////			parmReduce.setData("OPB_RECEIPT_PARM",opbreceipt.getData());
//			parmReduce.setData("OPB_EXE_BILPRINT","Y");//�����̲�����Ʊ���̣���������س����Ʒ�����ֽ��ۿ�ȯ
//			System.out.println("�������������parmReduce is����"+parmReduce);
//			
//			TParm BILReduceParm=BILReduceTool.getInstance().onInsertOPDBilReduce(parmReduce, connection);
//			if (BILReduceParm.getErrCode() < 0) {
//				err("ERR:" + BILReduceParm.getErrCode() + BILReduceParm.getErrText()
//						+ BILReduceParm.getErrName());
//				return BILReduceParm;
//			}
//			// �����޸�caowl..........end.............
//		}
		result = new TParm();
		result.setData("PRINT_NO", parm.getData("PRINT_NO"));
		result.addData("RECEIPT_NO", recpNo);
		result.setData("DATA_CODE", "");
		result.setData("DATA_DESC", "");
		return result;
	}
	
	private TParm updateOpdOrder(String caseNo, String rxNo, int seqNo, String recpNo, TConnection connection){
		String sql =
			" UPDATE OPD_ORDER" +
			" SET PRINT_FLG = 'Y', RECEIPT_NO = '" + recpNo + "'" +
			" WHERE CASE_NO = '" + caseNo + "' AND RX_NO = '" + rxNo + "' AND SEQ_NO = " + seqNo;
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		return result;
	}

	/**
	 * ���ҽ���ۿ���
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	private double getInsAmt(TParm parm) {
		double insAmt = 0.00;
		String insFlg = parm.getValue("INS_FLG");
		TParm insParm = parm.getParm("INS_RESULT");
		if (null != insFlg && insFlg.equals("Y")) {
			insAmt = insParm.getDouble("ACCOUNT_AMT"); // ҽ���ۿ���
		}
		return insAmt;
	}
	/**
	 * �ֽ��Ʊ������У���Ƿ����֧������΢�Ž��
	 * @param payTypeParm
	 * @return
	 */
	public TParm checkCashType(TParm payTypeParm){
		TParm checkCashTypeParm=new TParm();
		for (int i = 0; i < payTypeParm.getCount("AMT"); i++) {
			String sql = "SELECT GATHER_TYPE PAY_TYPE FROM BIL_GATHERTYPE_PAYTYPE WHERE PAYTYPE='"+payTypeParm.getValue("PAY_TYPE",i)+"'";
			TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
			dataParm=dataParm.getRow(0);
			if(dataParm.getValue("PAY_TYPE").equals("WX")&&payTypeParm.getDouble("AMT", i)>0){
				checkCashTypeParm.setData("WX_AMT", payTypeParm.getDouble("AMT", i));
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if(dataParm.getValue("PAY_TYPE").equals("ZFB")&&payTypeParm.getDouble("AMT", i)>0){
				checkCashTypeParm.setData("ZFB_AMT", payTypeParm.getDouble("AMT", i));
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
		}
		return checkCashTypeParm;
	}
	/**
	 * �ֽ��Ʊ������У���Ƿ����֧������΢�Ž��
	 * @param payTypeParm
	 * @return
	 */
	public TParm checkCashTypeOther(TParm payTypeParm){
		TParm checkCashTypeParm=new TParm();
		for (int i = 0; i < payTypeParm.getCount("AMT"); i++) {
			if(payTypeParm.getValue("PAY_TYPE",i).equals("WX")&&payTypeParm.getDouble("AMT", i)>0){
				checkCashTypeParm.setData("WX_AMT", payTypeParm.getDouble("AMT", i));
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if(payTypeParm.getValue("PAY_TYPE",i).equals("ZFB")&&payTypeParm.getDouble("AMT", i)>0){
				checkCashTypeParm.setData("ZFB_AMT", payTypeParm.getDouble("AMT", i));
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
		}
		return checkCashTypeParm;
	}
	/**
	 * У���Ƿ����֧������΢�Ž��.��Ա���˷�ʹ��
	 * @param payTypeParm
	 * @return
	 */
	public TParm checkCashTypeReOther(TParm payTypeParm){
		TParm checkCashTypeParm=new TParm();
		for (int i = 0; i < payTypeParm.getCount("AMT"); i++) {
			if(payTypeParm.getValue("PAY_TYPE",i).equals("WX")&&payTypeParm.getDouble("AMT", i)<0){
				checkCashTypeParm.setData("WX_AMT", -payTypeParm.getDouble("AMT", i));
				checkCashTypeParm.setData("WX_FLG", "Y");
			}
			if(payTypeParm.getValue("PAY_TYPE",i).equals("ZFB")&&payTypeParm.getDouble("AMT", i)<0){
				checkCashTypeParm.setData("ZFB_AMT", -payTypeParm.getDouble("AMT", i));
				checkCashTypeParm.setData("ZFB_FLG", "Y");
			}
		}
		return checkCashTypeParm;
	}
	/**
	 * �����շ��ֽ��Ʊ
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm onOPBCashprint(TParm parm, TConnection connection) {
		TParm result = new TParm();
		TParm selOpdParm = new TParm();
		double insAmt = getInsAmt(parm); // ���ҽ�����
		selOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
		if (opdParm.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		TParm payCashParm=parm.getParm("payCashParm");//==pangben 2016-8-8 ΢��֧����ʹ����ӽ��׺�
		//Ʊ�ݺ�
		 // ȡ��ԭ��õ�Ʊ�ݺ�
        String recpNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO", "RECEIPT_NO");
		String reduceFlg=parm.getValue("REDUCEFLG");
		double reduceAmtSum=0.00;
		if (null!=reduceFlg&&reduceFlg.equals("Y")) {//�ֽ�������====pangben 2014-8-21
			reduceAmtSum=parm.getDouble("REDUCE_AMT_SUM");
		}
		
		if (null!=reduceFlg&&reduceFlg.equals("Y")) {//�ֽ�������====pangben 2014-8-21
			reduceAmtSum=parm.getDouble("REDUCE_AMT_SUM");
			TParm parmReduce = parm.getParm("parmReduce");
			parmReduce.setData("RECEIPT_NO", recpNo);
//			parmReduce.setData("REDUCELAST_AMT",reduceAmtSum);
//			parmReduce.setData("REDUCELAST_AMT",opbreceipt.getDouble("PAY_MEDICAL_CARD")-
//					opbreceipt.getDouble("REDUCE_AMT")>=0?0:opbreceipt.getDouble("REDUCE_AMT")
//							-opbreceipt.getDouble("PAY_MEDICAL_CARD")-reduceLastAmt);//====pangben ������ʣ����
			parmReduce.setData("OPT_USER", parm.getData("OPT_USER"));
			parmReduce.setData("OPT_TERM", parm.getData("OPT_TERM"));
			parmReduce.setData("MR_NO", parm.getData("MR_NO"));
//			parmReduce.setData("OPB_RECEIPT_PARM",opbreceipt.getData());
			parmReduce.setData("OPB_EXE_BILPRINT","Y");//�����̲�����Ʊ���̣���������س����Ʒ�����ֽ��ۿ�ȯ
			TParm BILReduceParm=BILReduceTool.getInstance().onInsertOPDBilReduceCash(parmReduce, connection);
			if (BILReduceParm.getErrCode() < 0) {
				err("ERR:" + BILReduceParm.getErrCode() + BILReduceParm.getErrText()
						+ BILReduceParm.getErrName());
				return BILReduceParm;
			}
		}
		double[] chargeDouble = new double[30]; // ������
		double[] sumAmt = chargeDouble(opdParm, chargeDouble, 2, parm
				.getValue("CASchargeDoubleE_NO"));
		double allArAmt = sumAmt[0]; // �ܽ��
		TParm opbreceipt = new TParm();
		opbreceipt.setData("CASE_NO", parm.getData("CASE_NO"));
		opbreceipt.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		opbreceipt.setData("REGION_CODE", parm.getData("REGION_CODE"));
		opbreceipt.setData("MR_NO", parm.getData("MR_NO"));
		opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
		opbreceipt.setData("PRINT_NO", parm.getData("INV_NO"));
		opbreceipt.setData("BILL_DATE", parm.getData("OPT_DATE"));
		opbreceipt.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		opbreceipt.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		opbreceipt.setData("FLG", "Y");//====yanjing 20141216 add
		opbreceipt.setData("RECEIPT_NO", recpNo);//====yanjing 20141216 add
		int index = 1;
		double[] reduceDouble = new double[30]; 
		if (null!=reduceFlg&&reduceFlg.equals("Y")){//�м���ʱ��ȥ����Ĳ���
			reduceDouble = this.selectReducedForAmt(parm.getParm("parmReduce").getParm("parmForReduceM").getValue("REDUCE_NO"));
			// д������
			for (int i = 0; i < chargeDouble.length; i++) {
				String chargeTemp = "CHARGE";
				if (i < 9) {
					opbreceipt.setData(chargeTemp + "0" + index, StringTool.round(chargeDouble[i]-reduceDouble[i],2));
				} else {
					opbreceipt.setData(chargeTemp + index, StringTool.round(chargeDouble[i]-reduceDouble[i],2));
				}
				index++;
			}
		}else{
			// д������
			for (int i = 0; i < chargeDouble.length; i++) {
				String chargeTemp = "CHARGE";
				if (i < 9) {
					opbreceipt.setData(chargeTemp + "0" + index, chargeDouble[i]);
				} else {
					opbreceipt.setData(chargeTemp + index, chargeDouble[i]);
				}
				index++;
			}	
		}
			
//		String reduceFlg=parm.getValue("REDUCEFLG");
//		double reduceAmtSum=0.00;
//		if (null!=reduceFlg&&reduceFlg.equals("Y")) {//�ֽ�������====pangben 2014-8-21
//			reduceAmtSum=parm.getDouble("REDUCE_AMT_SUM");
//		}
		opbreceipt.setData("TOT_AMT", allArAmt);
		opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
		opbreceipt.setData("REDUCE_AMT", reduceAmtSum);
		opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
		opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
		opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
		opbreceipt.setData("AR_AMT", allArAmt-reduceAmtSum);//�۳�������
		opbreceipt.setData("PAY_CASH", allArAmt - insAmt-reduceAmtSum); // �۳�ҽ�����===pangben 2014-8-21 �۳�������
		opbreceipt.setData("PAY_MEDICAL_CARD", 0.00);
		opbreceipt.setData("PAY_BANK_CARD", 0.00);
		opbreceipt.setData("PAY_INS_CARD", insAmt);
		opbreceipt.setData("PAY_CHECK", 0.00);
		opbreceipt.setData("PAY_DEBIT", 0.00);
		opbreceipt.setData("PAY_BILPAY", 0.00);
		opbreceipt.setData("PAY_INS", 0.00);
		opbreceipt.setData("PAY_OTHER1", 0.00);
		opbreceipt.setData("PAY_OTHER2", 0.00);
		opbreceipt.setData("PAY_REMARK", new TNull(String.class));
		opbreceipt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		opbreceipt.setData("OPT_USER", parm.getData("OPT_USER"));
		opbreceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
		opbreceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
		opbreceipt.setData("PRINT_USER", parm.getData("OPT_USER"));
		if (null!=reduceFlg&&reduceFlg.equals("Y")) {//�ֽ�������====pangben 2014-8-21
			String reduceNo=parm.getParm("parmReduce").getParm("parmForReduceM").getValue("REDUCE_NO");
			opbreceipt.setData("REDUCE_NO",reduceNo);
		}else{
			opbreceipt.setData("REDUCE_NO", new TNull(String.class));
		}
		opbreceipt.setData("PAY_OTHER3", 0);
		opbreceipt.setData("PAY_OTHER4", 0);
		if (parm.getValue("TAX_FLG").length() > 0&&parm.getValue("TAX_FLG").equals("Y")) {
			opbreceipt.setData("TAX_FLG","Y");
			opbreceipt.setData("TAX_DATE",SystemTool.getInstance().getDate());
			opbreceipt.setData("TAX_USER",parm.getData("OPT_USER"));
		}else{
			opbreceipt.setData("TAX_FLG","N");
			opbreceipt.setData("TAX_DATE",new TNull(Timestamp.class));
			opbreceipt.setData("TAX_USER","");
		}
		
		String payTypeSql = 
			" UPDATE BIL_OPB_RECP SET ";
		
		String key;
		String remarkKey;//ˢ���շѣ���ӿ����ͼ�����add by huangjw 20141230 
		double sumPay=0; //֧����ʽ�ܺ� add by huangtt 20160128
		for (int i = 1; i < 12; i++) {
			if(i < 10){
				key = "PAY_TYPE0" + i;
				remarkKey="REMARK0"+i;
			}else{
				key = "PAY_TYPE" + i;
				remarkKey="REMARK"+i;
			}
			//ˢ���շѣ���ӿ����ͼ�����add by huangjw 20141230 
			payTypeSql += key + " = " + parm.getDouble(key) + "," + remarkKey + " = '" + parm.getData(remarkKey) + "',";
			sumPay += parm.getDouble(key); //��֧����ʽ�ܺ� add by huangtt 20160128
		}
		
		payTypeSql = payTypeSql.substring(0, payTypeSql.length() - 1);
		TParm bilInvricpt = new TParm();
		bilInvricpt.setData("RECP_TYPE", "OPB");
		bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
		bilInvricpt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		bilInvricpt.setData("OPT_USER", parm.getData("OPT_USER"));
		bilInvricpt.setData("INV_NO", parm.getData("INV_NO"));
		bilInvricpt.setData("OPT_TERM", parm.getData("OPT_TERM"));
		bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
		bilInvricpt.setData("TOT_AMT", allArAmt);
		bilInvricpt.setData("AR_AMT", allArAmt);

		TParm bilInvoice = new TParm();
		bilInvoice.setData("RECP_TYPE", "OPB");
		bilInvoice.setData("STATUS", "0");
		bilInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
		bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
		bilInvoice.setData("UPDATE_NO", StringTool.addString(parm.getData(
				"INV_NO").toString()));
		TParm actionParm = new TParm();
		actionParm.setData("BILINVRICPT", bilInvricpt.getData());
		actionParm.setData("BILINVOICE", bilInvoice.getData());
		actionParm.setData("OPBRECEIPT", opbreceipt.getData());
		actionParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		actionParm.setData("billFlg", parm.getValue("billFlg")); // ==========pangben
		// modify
		// 20110818 ���˲���
		// ������:Y ����:N
		actionParm.setData("CONTRACT_CODE", parm.getValue("CONTRACT_CODE")); // ==========pangben
		
		//�ж϶���֧����ʽ�ܺ���ar_amt�Ƿ���ͬ�������ͬ�Ͳ����б��� add by huangtt 20160128 start
		BigDecimal ar_amt_bd = new BigDecimal(opbreceipt.getDouble("AR_AMT")).setScale(2, RoundingMode.HALF_UP); 
		BigDecimal sumPay_bd = new BigDecimal(sumPay).setScale(2, RoundingMode.HALF_UP);
		if(ar_amt_bd.compareTo(sumPay_bd) != 0){
			//add by huangtt 20150525 start Ʊ�����ʧ�ܺ�ظ�OPD_ORDER��bill_flg,bill_date,bill_user
			TParm upOpdReParm = new TParm();
			upOpdReParm.setData("CASE_NO", parm.getData("CASE_NO"));
			TParm upOpdRe = OrderTool.getInstance().updateForOPBCashRe(upOpdReParm);
			//add by huangtt 20150525 end
			result = new TParm();
			result.setErrCode(-1);
			result.setErrText("����֧����ʽ�ܺ���ar_amt����");
			System.out.println("ERR:"+parm.getData("CASE_NO")+" ʱ��:"+ SystemTool.getInstance().getDate()
					+"       ����֧����ʽ�ܺ�Ϊ"+sumPay_bd+"��ar_amtΪ"+ar_amt_bd);
			
			return result;
		}
		//add by huangtt 20160128 end
		
		// modify
		// 20110818
		// ���˵�λ
		result = BILPrintTool.getInstance().saveOpb(actionParm, connection);
		if (result.getErrCode() < 0) {
			//add by huangtt 20150525 start Ʊ�����ʧ�ܺ�ظ�OPD_ORDER��bill_flg,bill_date,bill_user
			TParm upOpdReParm = new TParm();
			upOpdReParm.setData("CASE_NO", parm.getData("CASE_NO"));
			TParm upOpdRe = OrderTool.getInstance().updateForOPBCashRe(upOpdReParm);
			//add by huangtt 20150525 end
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// =========�˾��ﲡ����ִ�м��� OPD_ORDER �� PRINT_FLG �и���ΪN ��ִ�д�Ʊ������PRINT_NO=""
		TParm upOpdParm = new TParm();
		if ("N".equals(parm.getValue("billFlg"))) {
			upOpdParm.setData("PRINT_FLG", "N");
		} else
			upOpdParm.setData("PRINT_FLG", "Y");
//		 recpNo = result.getValue("RECEIPT_NO", 0);//====20141217 yanjing ע
		upOpdParm.setData("RECEIPT_NO", recpNo);
		upOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));

		TParm upOpd = OrderTool.getInstance().updateForOPBCash(upOpdParm,
				connection);
		if (upOpd.getErrCode() < 0) {
			err("ERR:" + upOpd.getErrCode() + upOpd.getErrText()
					+ upOpd.getErrName());
			return upOpd;
		}
		if(null!=payCashParm){//====pangben 2016-8-8 ���΢��֧�������׺�����ʾ
			if(null!=payCashParm.getValue("WX")&&payCashParm.getValue("WX").length()>0){
				payTypeSql+=", WX_BUSINESS_NO='"+payCashParm.getValue("WX")+"'";
			}
			if(null!=payCashParm.getValue("ZFB")&&payCashParm.getValue("ZFB").length()>0){
				payTypeSql+=", ZFB_BUSINESS_NO='"+payCashParm.getValue("ZFB")+"'";
			}
		}
		payTypeSql += " WHERE RECEIPT_NO = '" + recpNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(payTypeSql, connection));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		if (null!=reduceFlg&&reduceFlg.equals("Y")) {//�ֽ�������====pangben 2014-8-21
//			reduceAmtSum=parm.getDouble("REDUCE_AMT_SUM");
//			TParm parmReduce = parm.getParm("parmReduce");
//			parmReduce.setData("RECEIPT_NO", recpNo);
//			parmReduce.setData("REDUCELAST_AMT",reduceAmtSum);
////			parmReduce.setData("REDUCELAST_AMT",opbreceipt.getDouble("PAY_MEDICAL_CARD")-
////					opbreceipt.getDouble("REDUCE_AMT")>=0?0:opbreceipt.getDouble("REDUCE_AMT")
////							-opbreceipt.getDouble("PAY_MEDICAL_CARD")-reduceLastAmt);//====pangben ������ʣ����
//			parmReduce.setData("OPT_USER", parm.getData("OPT_USER"));
//			parmReduce.setData("OPT_TERM", parm.getData("OPT_TERM"));
//			parmReduce.setData("MR_NO", parm.getData("MR_NO"));
//			parmReduce.setData("OPB_RECEIPT_PARM",opbreceipt.getData());
//			parmReduce.setData("OPB_EXE_BILPRINT","Y");//�����̲�����Ʊ���̣���������س����Ʒ�����ֽ��ۿ�ȯ
//			TParm BILReduceParm=BILReduceTool.getInstance().onInsertOPDBilReduceCash(parmReduce, connection);
//			if (BILReduceParm.getErrCode() < 0) {
//				err("ERR:" + BILReduceParm.getErrCode() + BILReduceParm.getErrText()
//						+ BILReduceParm.getErrName());
//				return BILReduceParm;
//			}
//		}
		// System.out.println("OPBTOOL�����շѷ���ֵ"+result);
		result = new TParm();
		result.setData("PRINT_NO", parm.getData("PRINT_NO"));
		result.addData("RECEIPT_NO", recpNo);
		result.setData("DATA_CODE", "");
		result.setData("DATA_DESC", "");
//		System.out.println("result result result is ::"+result);
		return result;
	}

	/**
	 * ��ý���������
	 * 
	 * @param opdParm
	 *            TParm
	 * @param chargeDouble
	 *            double[]
	 * @param type
	 *            int 1 .ҽ�ƿ����� 2.�ֽ����
	 * @return double
	 */
	private double[] chargeDouble(TParm opdParm, double[] chargeDouble,
			int type, String caseNo) {
		int opdCount = opdParm.getCount("ORDER_CODE");
		String rexpCode = "";
		double arAmt = 0.00;
		double[] sumAmt = new double[4]; // ����ܽ�� ҽ�ƿ���� ��ɫͨ�����
		double allArAmt = 0.00;
		double allTotAmt = 0.00;
		// ========20120220 zhangp modify start
		String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
		TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		// �����ʷ��¼��ѯ�˲�������δ��Ʊ�������ܽ��
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// TParm opbrecParm=OPBReceiptTool.getInstance().getReceipt(caseNo);
		int chargeCount = sysChargeParm.getCount("ID");
		String[] chargeName = new String[30];
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
		for (int i = 0; i < chargeCount; i++) {
			String sysChargeId = sysChargeParm.getData("ID", i).toString();
			for (int j = 0; j < chargeName.length; j++) {
				if (sysChargeId.equals(chargeName[j])) {
					p.setData("CHARGE", i, j);
					p.setData("ID", i, sysChargeParm.getData("ID", i));
					p.setData("CHN_DESC", i, sysChargeParm.getData("CHN_DESC",
							i));
					break;
				}
			}
		}
		String idCharge = "";
		int charge = 0;
		StringBuffer tradeNo = new StringBuffer();// ���ܴ˴β�����TRADE_NO
		for (int i = 0; i < opdCount; i++) {
			rexpCode = opdParm.getValue("REXP_CODE", i);
			//modify by huangtt 20141210 start
//			arAmt = opdParm.getDouble("AR_AMT", i); 
//			System.out.println("MEM_PACKAGE_ID=="+opdParm.getValue("MEM_PACKAGE_ID", i));
			if( "".equals(opdParm.getValue("MEM_PACKAGE_ID", i))){
				arAmt = opdParm.getDouble("AR_AMT", i); 
			}else{
				arAmt = 0; 
			}
			
			//modify by huangtt 20141210 end
			
			allArAmt = allArAmt + arAmt;
			//add by huangtt 20141211 start 
			arAmt = opdParm.getDouble("AR_AMT", i); 
			allTotAmt = allTotAmt + arAmt;
			//add by huangtt 20141211 end
			if (!tradeNo.toString()
					.contains(opdParm.getValue("BUSINESS_NO", i))) {
				tradeNo.append("'").append(opdParm.getValue("BUSINESS_NO", i))
						.append("',");// UPDATE EKT_TRADE ��ʹ�� �޸��Ѿ��ۿ������ �帺ʹ��
			}
			for (int j = 0; j < p.getCount("ID"); j++) {
				idCharge = p.getData("ID", j).toString();
				charge = p.getInt("CHARGE", j);
				if (rexpCode.equals(idCharge))
					chargeDouble[charge] = chargeDouble[charge] + arAmt;
				// ========20120220 zhangp modify end
			}
		}
		parm.setData("TRADE_SUM_NO",
				null == tradeNo || tradeNo.length() <= 0 ? "''" : tradeNo
						.toString().substring(0,
								tradeNo.toString().lastIndexOf(",")));
		TParm ektSumTradeParm = EKTNewTool.getInstance()
				.selectEktSumTrade(parm);
		sumAmt[0] = allArAmt;
		if (null != opdParm.getValue("INS_FLG")
				&& opdParm.getValue("INS_FLG").equals("Y")) {
			sumAmt[1] = allArAmt; // ҽ�ƿ����
		} else {
			sumAmt[1] = ektSumTradeParm.getDouble("AMT", 0);// -opbrecParm.getDouble("PAY_MEDICAL_CARD",0);
															// //ҽ�ƿ����
		}
		sumAmt[2] = ektSumTradeParm.getDouble("GREEN_BUSINESS_AMT", 0);// -opbrecParm.getDouble("PAY_OTHER1",0);
																		// //��ɫͨ�����
		// ===zhangp 20120719 start
		sumAmt[3]= allTotAmt;
		sumAmt = reGreenAmt(sumAmt, opdParm, caseNo);
		return sumAmt;
	}

	/**
	 * ��ɫͨ����Ʊ�Ժ��ٳ�Ǯ��Ʊ =======zhangp 20120719
	 * 
	 * @param sumAmt
	 * @param opbParm
	 * @return
	 */
	private double[] reGreenAmt(double[] sumAmt, TParm opbParm, String caseNo) {
		// String mr_no = opbParm.getData("MR_NO", 0).toString();
		String case_no = caseNo;
		double allArAmt = sumAmt[0];
		double ektAmt = sumAmt[1]; // ҽ�ƿ����
		double greenAmt = sumAmt[2]; // ��ɫͨ�����
		double allTotAmt = sumAmt[3]; //add by huangtt 20141211
		if (greenAmt > 0) {
			String sql = " SELECT GREEN_BALANCE, GREEN_PATH_TOTAL,MR_NO"
					+ " FROM REG_PATADM" + " WHERE CASE_NO = '" + case_no + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			double green_balance = result.getDouble("GREEN_BALANCE", 0);
			double green_path_total = result.getDouble("GREEN_PATH_TOTAL", 0);
			sql = " SELECT A.CARD_NO,B.CURRENT_BALANCE "
					+ " FROM EKT_ISSUELOG A, EKT_MASTER B "
					+ " WHERE A.MR_NO = '" + result.getValue("MR_NO", 0)
					+ "' AND A.WRITE_FLG = 'Y' AND A.CARD_NO = B.CARD_NO ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			String card_no = result.getValue("CARD_NO", 0);
			double current_balance = result.getDouble("CURRENT_BALANCE", 0);
			if (current_balance > 0) {
				if (greenAmt <= current_balance) {
					green_balance = green_balance + allArAmt;// update
																// reg_patadm.green_balance
					current_balance = current_balance - greenAmt;// update
																	// ekt_master.current_balance
					greenAmt = 0;
					ektAmt = allArAmt;
				} else if (greenAmt > current_balance) {
					green_balance = green_balance + current_balance;// update
																	// reg_patadm.green_balance
					greenAmt = greenAmt - current_balance;
					ektAmt = ektAmt + current_balance;
					current_balance = 0;// update ekt_master.current_balance
				}
				updateGreenAmt(green_balance, current_balance, case_no, card_no);
			}
			sumAmt[0] = allArAmt;
			sumAmt[1] = ektAmt; // ҽ�ƿ����
			sumAmt[2] = greenAmt; // ��ɫͨ�����
			sumAmt[3] = allTotAmt;  //add by huangtt 20141211
		}
		return sumAmt;
	}

	/**
	 * ����ɫͨ���󣬲����ֽ𣬸���Ǯ ======zhangp 20120719
	 * 
	 * @param green_balance
	 * @param current_balance
	 * @param case_no
	 * @param card_no
	 * @return
	 */
	private TParm updateGreenAmt(double green_balance, double current_balance,
			String case_no, String card_no) {
		String sql = " UPDATE REG_PATADM" + " SET GREEN_BALANCE = "
				+ green_balance + " WHERE CASE_NO = '" + case_no + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		sql = " UPDATE EKT_MASTER" + " SET CURRENT_BALANCE = "
				+ current_balance + " WHERE CARD_NO = '" + card_no + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}

	/**
	 * �޸ļ��˱������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm ==================pangben modify 20110823
	 */
	private TParm updateRecode(TParm parm, TConnection connection) {
		// �޸����״̬
		TParm result = new TParm();
		TParm parmOne = new TParm();
		parmOne.setData("BIL_STATUS", "3"); // �����˷�
		parmOne.setData("" + "RECEIPT_FLG", "2"); // �˷�
		parmOne.setData("OPT_USER", parm.getValue("OPT_USER"));
		parmOne.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		parmOne.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
		parmOne.setData("RECEIPT_TYPE", "OPB");
		result = BILContractRecordTool.getInstance().updateRecode(parmOne,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * �ֽ��˷�����Ʊ��:���˲�����û��ִ�н����Ʊ�ݣ���ִ���޸�BIL_INVRCP��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm ==================pangben modify 20110822
	 */
	public TParm backOPBRecpStatus(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// �޸ļ��˱������
		result = updateRecode(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		// �˷�����Ʊ��:���˲����Ͳ����˲������õĲ���
		result = backOPBRecpTemp(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		return result;
	}

	/**
	 * �ֽ��˷�����Ʊ��:���˲����Ͳ����˲������õĲ���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	private TParm backOPBRecpTemp(TParm parm, TConnection connection) {
		TParm result = new TParm();
		//add by huangtt 20141222 start
		String sql = "SELECT * FROM BIL_OPB_RECP WHERE RECEIPT_NO='"+parm.getValue("RECEIPT_NO")+"' AND CASE_NO ='"+parm.getData("CASE_NO")+"'";
		TParm backReceipt = new TParm(TJDODBTool.getInstance().select(sql)).getRow(0);
//		TParm opbreceipt = getOpbreceiptTemp(parm, false);
		backReceipt.setData("OPT_USER_T", parm.getData("OPT_USER_T"));
		backReceipt.setData("OPT_TERM_T", parm.getData("OPT_TERM_T"));
		TParm opbreceipt = getOpbreceiptTemp(backReceipt, false);
		//add by huangtt 20141222 end
		
		// ����Ʊ�ݵ�
		result = OPBReceiptTool.getInstance().insertBackReceipt(opbreceipt,
				false, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		//add by huangtt 20141216 start
		//���Ϊ�ײʹ�Ʊ������bil_opb_recp����mem_pack_flgΪY
//		System.out.println("memPack==="+parm.getBoolean("MEM_PACK_FLG"));
		if(parm.getBoolean("MEM_PACK_FLG")){
			TParm memPackParm = new TParm();
			memPackParm.setData("RECEIPT_NO", result.getData("RECEIPT_NO"));
			memPackParm.setData("CASE_NO", parm.getData("CASE_NO"));
			TParm resultPack = OPBReceiptTool.getInstance().updateReceiptMemPackFlg(memPackParm, connection);
			if (resultPack.getErrCode() < 0) {
				err("ERR:" + resultPack.getErrCode() + resultPack.getErrText()
						+ resultPack.getErrName());
				return resultPack;
			}
		}
		
		//add by huangtt 20141216 end
		
		// ��̨ȡ��ԭ��ȡ���Ĵ�Ʊ��
		String receiptNo = result.getData("RECEIPT_NO").toString();
		  //=====pangben 2016-6-27 ���΢�š�֧������Ʊ¼�뽻�׺�����
        if(null!=parm.getData("PAY_TYPE_FLG")&&parm.getValue("PAY_TYPE_FLG").equals("Y")){
        	TParm payTypeParm=parm.getParm("PAY_TYPE_PARM");
        	if(null!=payTypeParm){
        		TParm bilTypeParm = new TParm(TJDODBTool.getInstance()
        				.select("SELECT GATHER_TYPE,PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE WHERE GATHER_TYPE IN('WX','ZFB')"));
        		String wx="";
        		String zfb="";
        		for (int i = 0; i < bilTypeParm.getCount(); i++) {//��ñ�ע��λ��
					if(null!=payTypeParm.getValue("WX") && payTypeParm.getValue("WX").length()>0 &&
							bilTypeParm.getValue("GATHER_TYPE",i).equals("WX")){
						wx="WX_BUSINESS_NO='"+payTypeParm.getValue("WX")+"' ";
					}
					if(null!=payTypeParm.getValue("ZFB") && payTypeParm.getValue("ZFB").length()>0&&
							bilTypeParm.getValue("GATHER_TYPE",i).equals("ZFB")){
						zfb="ZFB_BUSINESS_NO='"+payTypeParm.getValue("ZFB")+"' ";;
					}
				}
				String typeSql="UPDATE BIL_OPB_RECP SET ";
				if(wx.length()>0&&zfb.length()>0){
					typeSql+=wx+","+zfb;
				}else if(wx.length()>0&&zfb.length()<=0){
					typeSql+=wx;
				}else if(zfb.length()>0&&wx.length()<=0){
					typeSql+=zfb;
				}
				//�����޸�sql���
				TParm resultParm = new TParm(TJDODBTool.getInstance().update(typeSql+" WHERE CASE_NO='"
						+parm.getData("CASE_NO")+"' AND RECEIPT_NO='"+receiptNo+"'",connection));
				if (resultParm.getErrCode() < 0) {
		            err(resultParm.getErrName() + " " + resultParm.getErrText());
		            return resultParm;
		        }
        	}
        }
		//add by huangtt 20140902 start
		//���¶���֧����ʽǮ��
		opbreceipt.setData("RECEIPT_NO", receiptNo);
		result = OPBReceiptTool.getInstance().updateReceipt(opbreceipt, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		//add by huangtt 20140922 end
		// ���µ��˷�Ʊ��
		TParm upReceiptParm = updateOpbRept(parm, receiptNo);
		if (parm.getValue("RECP_TYPE").equals("EKT")) {
			result = OPBReceiptTool.getInstance().updateBackReceiptOne(
					upReceiptParm, connection);
		} else {
			result = OPBReceiptTool.getInstance().updateBackReceipt(
					upReceiptParm, connection);
		}
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		// ����ҽ��
		TParm orderListParm = new TParm();
		orderListParm.setData("CASE_NO", parm.getData("CASE_NO"));
		orderListParm.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
		// ===================pangben 2012-3-28 ҽ��ȫ����������ʱ,ִ���ֽ��˷Ѳ����޸�ҽ��״̬
		if (null != parm.getValue("INS_UN_FLG")
				&& parm.getValue("INS_UN_FLG").equals("Y")) {
			OrderTool.getInstance()
					.upForOPBEKTReturn(orderListParm, connection);
		} else {
			result = OrderTool.getInstance().upForOPBReturn(orderListParm,
					connection);
		}
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �˷�
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm getOpbreceiptTemp(TParm parm, boolean flg) {

		// �õ��˷Ѳ��븺����Ʊ��
		TParm opbreceipt = new TParm();
		opbreceipt.setData("CASE_NO", parm.getData("CASE_NO"));
		opbreceipt.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
		opbreceipt.setData("REGION_CODE", parm.getData("REGION_CODE"));
		opbreceipt.setData("MR_NO", parm.getData("MR_NO"));
		opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
		if (flg) {
			opbreceipt.setData("PRINT_NO", "");
			opbreceipt.setData("PRINT_DATE", "");
		} else {
			opbreceipt.setData("PRINT_NO",
					null == parm.getData("PRINT_NO") ? new TNull(String.class)
							: parm.getData("PRINT_NO"));
			opbreceipt
					.setData("PRINT_DATE",
							null == parm.getData("PRINT_DATE") ? new TNull(
									String.class) : parm.getData("PRINT_DATE"));
		}

		opbreceipt.setData("BILL_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
		for (int i = 0; i < chargName.length; i++) {
			opbreceipt.setData(chargName[i], getResetAmt(parm
					.getDouble(chargName[i]), flg));

		}
		opbreceipt.setData("TOT_AMT", getResetAmt(parm.getDouble("TOT_AMT"),
				flg));
		opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
		opbreceipt.setData("REDUCE_AMT",  getResetAmt(parm.getDouble("REDUCE_AMT"),
				flg));
		opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
		opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
		opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
		opbreceipt
				.setData("AR_AMT", getResetAmt(parm.getDouble("AR_AMT"), flg));
		opbreceipt.setData("PAY_CASH", getResetAmt(parm.getDouble("PAY_CASH"),
				flg));
		opbreceipt.setData("PAY_MEDICAL_CARD", getResetAmt(parm
				.getDouble("PAY_MEDICAL_CARD"), flg));
		opbreceipt.setData("PAY_BANK_CARD", getResetAmt(parm
				.getDouble("PAY_BANK_CARD"), flg));
		opbreceipt.setData("PAY_INS_CARD", getResetAmt(parm
				.getDouble("PAY_INS_CARD"), flg));
		opbreceipt.setData("PAY_CHECK", getResetAmt(
				parm.getDouble("PAY_CHECK"), flg));
		opbreceipt.setData("PAY_DEBIT", getResetAmt(
				parm.getDouble("PAY_DEBIT"), flg));
		opbreceipt.setData("PAY_BILPAY", getResetAmt(parm
				.getDouble("PAY_BILPAY"), flg));
		opbreceipt.setData("PAY_INS", getResetAmt(parm.getDouble("PAY_INS"),
				flg));
		opbreceipt.setData("PAY_OTHER1", getResetAmt(parm
				.getDouble("PAY_OTHER1"), flg));
		opbreceipt.setData("PAY_OTHER2", getResetAmt(parm
				.getDouble("PAY_OTHER2"), flg));
		opbreceipt.setData("PAY_OTHER3", getResetAmt(parm
				.getDouble("PAY_OTHER3"), flg));
		opbreceipt.setData("PAY_OTHER4", getResetAmt(parm
				.getDouble("PAY_OTHER4"), flg));
		opbreceipt.setData("PAY_REMARK", new TNull(String.class));
		opbreceipt.setData("CASHIER_CODE", parm.getData("OPT_USER_T"));
		opbreceipt.setData("OPT_USER", parm.getData("OPT_USER_T"));
		opbreceipt.setData("OPT_DATE", SystemTool.getInstance().getDate());
		opbreceipt.setData("OPT_TERM", parm.getData("OPT_TERM_T"));
		opbreceipt.setData("REDUCE_NO",new TNull(String.class));
		opbreceipt.setData("TAX_FLG","N");
		opbreceipt.setData("TAX_DATE",new TNull(String.class));
		opbreceipt.setData("TAX_USER","");
		//add by huangtt 20140902
		opbreceipt.setData("PAY_TYPE01",
				null == parm.getData("PAY_TYPE01") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE01"), flg));
		opbreceipt.setData("PAY_TYPE02",
				null == parm.getData("PAY_TYPE02") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE02"), flg));
		opbreceipt.setData("PAY_TYPE03",
				null == parm.getData("PAY_TYPE03") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE03"), flg));
		opbreceipt.setData("PAY_TYPE04",
				null == parm.getData("PAY_TYPE04") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE04"), flg));
		opbreceipt.setData("PAY_TYPE05",
				null == parm.getData("PAY_TYPE05") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE05"), flg));
		opbreceipt.setData("PAY_TYPE06",
				null == parm.getData("PAY_TYPE06") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE06"), flg));
		opbreceipt.setData("PAY_TYPE07",
				null == parm.getData("PAY_TYPE07") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE07"), flg));
		opbreceipt.setData("PAY_TYPE08",
				null == parm.getData("PAY_TYPE08") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE08"), flg));
		opbreceipt.setData("PAY_TYPE09",
				null == parm.getData("PAY_TYPE09") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE09"), flg));
		opbreceipt.setData("PAY_TYPE10",
				null == parm.getData("PAY_TYPE10") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE10"), flg));
		opbreceipt.setData("PAY_TYPE11",
				null == parm.getData("PAY_TYPE11") ? 0
						: getResetAmt(parm.getDouble("PAY_TYPE11"), flg));
		return opbreceipt;
	}

	/**
	 * ��ý��
	 * 
	 * @param amt
	 *            double
	 * @param flg
	 *            boolean
	 * @return double
	 */
	private double getResetAmt(double amt, boolean flg) {
		if (flg) {
			return amt;
		} else {
			return -amt;
		}
	}

	/**
	 * ҽ�ƿ��˷��޸��վݱ�
	 * 
	 * @param parm
	 *            TParm
	 * @param receiptNo
	 *            String
	 * @return TParm
	 */
	private TParm updateOpbRept(TParm parm, String receiptNo) {
		TParm upReceiptParm = new TParm();
		upReceiptParm.setData("CASE_NO", parm.getData("CASE_NO"));
		upReceiptParm.setData("RESET_RECEIPT_NO", receiptNo);
		upReceiptParm.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
		upReceiptParm.setData("PRINT_NO", parm.getData("PRINT_NO"));
		upReceiptParm.setData("OPT_USER", parm.getData("OPT_USER_T"));
		upReceiptParm.setData("OPT_DATE", parm.getData("OPT_DATE_T"));
		upReceiptParm.setData("OPT_TERM", parm.getData("OPT_TERM_T"));
		return upReceiptParm;
	}

	/**
	 * ҽ�ƿ��˷�����Ʊ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */

	public TParm backEKTOPBRecp(TParm parm, TConnection connection) {
		TParm result = null;
		TParm reduceParm = new TParm();//yanjing 201412224 add
		// ����Ʊ��
		result = updateBilInvrcpt(parm, connection, "OPB");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		TParm opbParm = OPBReceiptTool.getInstance().getOneReceipt(
				parm.getValue("RECEIPT_NO")).getRow(0); // ��ѯ���д�Ʊ�ݵ��վ�
		String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
				"RECEIPT_NO", "RECEIPT_NO");
		if(null!=opbParm.getValue("REDUCE_NO") &&!opbParm.getValue("REDUCE_NO").equals("")){//Ʊ�ݺ��м�������   shibl add  20140516
			opbParm.setData("NEWRECEIPT_NO", receiptNo);
			opbParm.setData("REDUCELAST_AMT", parm.getValue("REDUCELAST_AMT"));
//			opbParm.setData("");
			reduceParm=BILReduceTool.getInstance().onBackOPDBilReduce(opbParm, parm, connection);
			if (reduceParm.getErrCode() < 0) {
				return reduceParm;
			}
		}
		TParm printParm = new TParm();
		printParm = updateOpbRept(parm, receiptNo);
		// ҽ�ƿ��˷��彫�վ�ִ������״̬
		result=OPBReceiptTool.getInstance().updateUnPrintNo(printParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ���һ���˷��վ���Ϣ
		opbParm.setData("CASE_NO", parm.getData("CASE_NO"));
		opbParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
		opbParm.setData("REGION_CODE", parm.getData("REGION_CODE"));
		opbParm.setData("MR_NO", parm.getData("MR_NO"));
		opbParm.setData("PRINT_NO", parm.getData("PRINT_NO"));
		opbParm.setData("OPT_USER_T", parm.getValue("OPT_USER_T"));
		opbParm.setData("OPT_TERM_T", parm.getValue("OPT_TERM_T"));
		// �����е��վݻ������һ���վ�,���´�Ʊ״̬
		TParm opbreceipt = getOpbreceiptTemp(opbParm, false);
		opbreceipt.setData("RECEIPT_NO", receiptNo);
		result = OPBReceiptTool.getInstance().insertBackReceipt(opbreceipt,
				false, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if(!reduceParm.getBoolean("CASHfULL_FLG",0)){//����ҽ�ƿ�����ʱ
			
			//����opd_order��
			TParm orderListParm = new TParm();
			orderListParm.setData("CASE_NO", parm.getData("CASE_NO"));
			orderListParm.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
			result = this.updateOpdOrderForReduce(orderListParm, connection);
			if(result.getErrCode()<0){
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.rollback();
				connection.close();
				return result;
			}
			
		}else {
			// ����ҽ��
			TParm orderListParm = new TParm();
			orderListParm.setData("CASE_NO", parm.getData("CASE_NO"));
			orderListParm.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
			result = OrderTool.getInstance().upForOPBEKTReturn(orderListParm,
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}
	
	/**
//	 * ����ҽ����
//	 * yanjing 20141208
//	 */
	private TParm updateOpdOrderForReduce(TParm inParm, TConnection connection){
		TParm result = new TParm();
		String receptNo = inParm.getValue("RECEIPT_NO");
		String caseNo = inParm.getValue("CASE_NO");
		String opdSql = "UPDATE OPD_ORDER SET RECEIPT_NO='',PRINT_FLG = 'N' " // delete BILL_FLG = 'N',BILL_DATE = '',BILL_USER = '',
				+" WHERE CASE_NO = '"+caseNo+"' AND RECEIPT_NO = '"+receptNo+"' ";
		result = new TParm(TJDODBTool.getInstance().update(opdSql, connection));
		if(result.getErrCode()<0){
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
		
	}

	/**
	 * �ֽ��˷�����Ʊ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm backOPBRecp(TParm parm, TConnection connection) {
		String sql = "SELECT * FROM BIL_CONTRACT_RECODE WHERE RECEIPT_NO='"
				+ parm.getData("RECEIPT_NO") + "'";
		TParm contractParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm result = null;
		if (null != contractParm && contractParm.getCount() > 0) {
			result = updateRecode(parm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}
		result = backOPBRecpTemp(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		if (null!=parm.getValue("REDUCE_NO") &&!parm.getValue("REDUCE_NO").equals("")) {// Ʊ�ݺ��м������� 
			parm.setData("NEWRECEIPT_NO",parm.getValue("RECEIPT_NO"));//�ֽ����
			result = BILReduceTool.getInstance().onBackOpdBilReduceCash(
					parm, parm, connection);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		result = updateBilInvrcpt(parm, connection, "OPB");
		return result;

	}

	/**
	 * ����Ʊ������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @param recpType
	 *            String
	 * @return TParm
	 */
	private TParm updateBilInvrcpt(TParm parm, TConnection connection,
			String recpType) {
		// ����Ʊ��
		TParm bilInvrcptParm = new TParm();
		bilInvrcptParm.setData("CANCEL_FLG", "1");
		bilInvrcptParm.setData("CANCEL_USER", parm.getData("OPT_USER_T"));
		bilInvrcptParm.setData("OPT_USER", parm.getData("OPT_USER_T"));
		bilInvrcptParm.setData("OPT_TERM", parm.getData("OPT_TERM_T"));
		bilInvrcptParm.setData("RECP_TYPE", recpType);
		bilInvrcptParm.setData("INV_NO", parm.getData("PRINT_NO"));

		TParm result = BILInvrcptTool.getInstance().updataData(bilInvrcptParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ȡ����ϸ���վ��ã� =======zhangp 20120224
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getReceiptDetail(TParm parm) {
		DecimalFormat df = new DecimalFormat("########0.00");
		String caseNo = parm.getValue("CASE_NO");
		String no = "";
		String sql = "";
		no = parm.getValue("NO");
		sql = " SELECT A.ORDER_DESC || A.SPECIFICATION AS ORDER_DESC, D.ADDPAY_RATE, A.DOSAGE_QTY, "
				+ "        A.OWN_PRICE, A.AR_AMT, A.REXP_CODE,C.CHN_DESC "
				+ "   FROM OPD_ORDER A,BIL_OPB_RECP B,SYS_DICTIONARY C,SYS_FEE D "
				+ "  WHERE A.RECEIPT_NO = B.RECEIPT_NO "
				+ "    AND A.REXP_CODE = C.ID  "
				+ "    AND C.GROUP_ID = 'SYS_CHARGE' "
				+ "    AND A.SETMAIN_FLG = 'N'"
				+ "    AND A.ORDER_CODE = D.ORDER_CODE "
				+ "    AND A.CASE_NO = '"
				+ caseNo
				+ "' "
				+ "    AND A.CASE_NO = B.CASE_NO"
				+ "    AND A.ADM_TYPE = '"
				+ parm.getValue("ADM_TYPE")
				+ "' "
				+ "    AND A.RECEIPT_NO = '"
				+ no + "' " + "  ORDER BY A.REXP_CODE ";
		// System.out.println("ȡ����ϸ����sql:" + sql);

		TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("ȡ����ϸ����:" + orderParm);
		String rexpCode = orderParm.getValue("REXP_CODE", 0);
		TParm orderParmf = new TParm();
		double amt = 0.00;
		for (int i = 0; i < orderParm.getCount(); i++) {
			if (rexpCode.equals(orderParm.getData("REXP_CODE", i).toString())) {
				orderParmf.addData("ORDER_DESC", orderParm.getData(
						"ORDER_DESC", i));
				orderParmf.addData("ADDPAY_RATE", orderParm.getData(
						"ADDPAY_RATE", i));
				orderParmf.addData("DOSAGE_QTY", orderParm.getData(
						"DOSAGE_QTY", i));
				orderParmf.addData("OWN_PRICE", orderParm.getData("OWN_PRICE",
						i));
				orderParmf.addData("AR_AMT", df.format(StringTool.round(
						orderParm.getDouble("AR_AMT", i), 2)));
				amt = amt + orderParm.getDouble("AR_AMT", i);
			} else {
				orderParmf.addData("ORDER_DESC", "");
				orderParmf.addData("ADDPAY_RATE", "");
				orderParmf.addData("DOSAGE_QTY", "");
				orderParmf.addData("OWN_PRICE", orderParm.getData("CHN_DESC",
						i - 1));
				orderParmf.addData("AR_AMT", df
						.format(StringTool.round(amt, 2)));

				orderParmf.addData("ORDER_DESC", orderParm.getData(
						"ORDER_DESC", i));
				orderParmf.addData("ADDPAY_RATE", orderParm.getData(
						"ADDPAY_RATE", i));
				orderParmf.addData("DOSAGE_QTY", orderParm.getData(
						"DOSAGE_QTY", i));
				// =====zhangp 20120301 modify start
				orderParmf.addData("OWN_PRICE", StringTool.round(orderParm
						.getDouble("OWN_PRICE", i), 2));
				// =====zhangp 20120301 modify end
				orderParmf.addData("AR_AMT", df.format(StringTool.round(
						orderParm.getDouble("AR_AMT", i), 2)));
				amt = orderParm.getDouble("AR_AMT", i);
			}
			rexpCode = orderParm.getData("REXP_CODE", i).toString();
		}
		orderParmf.addData("ORDER_DESC", "");
		orderParmf.addData("ADDPAY_RATE", "");
		orderParmf.addData("DOSAGE_QTY", "");
		orderParmf.addData("OWN_PRICE", orderParm.getData("CHN_DESC", orderParm
				.getCount() - 1));
		orderParmf.addData("AR_AMT", df.format(StringTool.round(amt, 2)));
		TParm tableresultparm = new TParm();
		for (int i = 0; i < 10; i++) {
			tableresultparm.addData("ORDER_DESC", orderParmf.getData(
					"ORDER_DESC", i));
			tableresultparm.addData("ADDPAY_RATE", orderParmf.getData(
					"ADDPAY_RATE", i));
			tableresultparm.addData("DOSAGE_QTY", orderParmf.getData(
					"DOSAGE_QTY", i));
			tableresultparm.addData("OWN_PRICE", orderParmf.getData(
					"OWN_PRICE", i));
			// =====zhangp 20120227 modify start
			tableresultparm.addData("AR_AMT", orderParmf.getData("AR_AMT", i));
			// =====zhangp 20120227 modify end
		}
		tableresultparm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		tableresultparm.addData("SYSTEM", "COLUMNS", "ADDPAY_RATE");
		tableresultparm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		tableresultparm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		tableresultparm.addData("SYSTEM", "COLUMNS", "AR_AMT");
		tableresultparm.setCount(tableresultparm.getCount("ORDER_DESC"));
		return tableresultparm;
	}

	/**
	 * �õ�ҽ�Ʊ�������ר���վݱ�����Ϣ ===xueyf 20120225
	 * 
	 * @param printParm
	 *            TParm
	 * @return TParm
	 */
	public TParm getReceiptTparm(TParm printParm) {
		String receiptNo = (String) printParm.getValue("RECEIPT_NO");
		if (receiptNo != null) {
			// TParm parm=getChargeData(receiptNo);
			// printParm.setData(parm.getData());
		}
		Timestamp printDate = (SystemTool.getInstance().getDate());
		TParm selAdmInp = new TParm();
		selAdmInp.setData("CASE_NO", printParm.getValue("CASE_NO"));
		TParm admInpParm = ADMInpTool.getInstance().selectall(selAdmInp);
		Timestamp inDataOut = admInpParm.getTimestamp("IN_DATE", 0);
		Timestamp outDataOut;
		if (admInpParm.getData("DS_DATE", 0) != null)
			outDataOut = admInpParm.getTimestamp("DS_DATE", 0);
		else
			outDataOut = printDate;
		String deptCode = admInpParm.getValue("DEPT_CODE", 0);
		String inDate = StringTool.getString(inDataOut, "yyyyMMdd");
		String outDate = StringTool.getString(outDataOut, "yyyyMMdd");
		String pDate = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		String sYear = inDate.substring(0, 4);
		String sMonth = inDate.substring(4, 6);
		String sDate = inDate.substring(6, 8);
		String eYear = outDate.substring(0, 4);
		String eMonth = outDate.substring(4, 6);
		String eDate = outDate.substring(6, 8);
		String pYear = pDate.substring(0, 4);
		String pMonth = pDate.substring(4, 6);
		String pDay = pDate.substring(6, 8);
		// int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) ==
		// 0 ? 1 : StringTool.getDateDiffer(outDataOut, inDataOut);
		int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) + 1;
		printParm.setData("COPY", "TEXT", "COPY");
		printParm.setData("sYear", "TEXT", sYear);
		printParm.setData("sMonth", "TEXT", sMonth);
		printParm.setData("sDate", "TEXT", sDate);
		printParm.setData("eYear", "TEXT", eYear);
		printParm.setData("eMonth", "TEXT", eMonth);
		printParm.setData("eDate", "TEXT", eDate);
		printParm.setData("pYear", "TEXT", pYear);
		printParm.setData("pMonth", "TEXT", pMonth);
		printParm.setData("pDay", "TEXT", pDay);
		printParm.setData("rollDate", "TEXT", rollDate);
		printParm.setData("CASHIER_CODE", "TEXT", Operator.getName());
		// printParm.setData("MR_NO","TEXT", this.getValueString("MR_NO"));
		printParm.setData("BILL_DATE", "TEXT", outDataOut);
		printParm.setData("CHARGE_DATE", "TEXT", outDataOut);
		// printParm.setData("DEPT_CODE","TEXT", getDept(deptCode));
		printParm.setData("OPT_USER", "TEXT", Operator.getName());
		printParm.setData("OPT_DATE", "TEXT", printDate);
		String printDateC = StringTool.getString(printDate, "yyyy��MM��dd��");
		printParm.setData("DATE", "TEXT", printDateC);
		printParm.setData("NO1", "TEXT", receiptNo);
		// printParm.setData("NO2", "TEXT", printNo);
		printParm.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());
		return printParm;
	}

	/**
	 * ���HL7���ݼ��� ����ҽ��վ �����շ�ʹ��
	 * 
	 * @param hl7ParmEnd
	 * @param checkParm
	 * @param i
	 */
	public void setHl7TParm(TParm hl7ParmEnd, TParm checkParm, int i,
			String billFlg) {
		// HL7���ݼ��� ������� �ļ���ҽ������ ���ͽӿ�ʹ��
		if ((checkParm.getValue("CAT1_TYPE", i).equals("RIS") || checkParm
				.getValue("CAT1_TYPE", i).equals("LIS"))
				&& checkParm.getBoolean("SETMAIN_FLG", i)
				&& checkParm.getValue("ORDERSET_CODE", i).equals(
						checkParm.getValue("ORDER_CODE", i))) {
			hl7ParmEnd.addData("ORDER_CAT1_CODE", checkParm.getData(
					"ORDER_CAT1_CODE", i));
			hl7ParmEnd.addData("TEMPORARY_FLG", checkParm.getData(
					"TEMPORARY_FLG", i));
			hl7ParmEnd.addData("ADM_TYPE", checkParm.getData("ADM_TYPE", i));
			hl7ParmEnd.addData("RX_NO", checkParm.getData("RX_NO", i));
			hl7ParmEnd.addData("SEQ_NO", checkParm.getData("SEQ_NO", i));
			hl7ParmEnd.addData("MED_APPLY_NO", checkParm.getData(
					"MED_APPLY_NO", i));
			hl7ParmEnd.addData("CAT1_TYPE", checkParm.getData("CAT1_TYPE", i));
			hl7ParmEnd.addData("BILL_FLG", billFlg);
		}
	}

	/**
	 * �˴ν�����Ҫ������ҽ�����ϸ�ֵ
	 */
	public void setNewParm(TParm newParm, TParm parm, int i, String billlFlg,
			String billType) {
		newParm.addData("RX_NO", parm.getValue("RX_NO", i));
		newParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
		newParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
		newParm.addData("AMT", parm.getDouble("AR_AMT", i));
		newParm.addData("BUSINESS_NO", parm.getValue("BUSINESS_NO", i));
		newParm.addData("BILL_FLG", billlFlg);
		newParm.addData("BILL_TYPE", billType);
	}

	/**
	 * �޸�ҽ�ƿ�����OPD_ORDER ҽ���Ľ��׺���BUSINESS_NO flg true �����շѲ��� false ����ҽ��վ����
	 * 
	 * @param parm
	 *            TParm
	 * @param orderParm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOpdOrderEkt(TParm parm, TParm orderParm,
			TConnection connection) {
		// �շ�
		String sql = "";
		TParm result = new TParm();
		// String unFlg = parm.getValue("UN_FLG");
		TParm hl7Parm = parm.getParm("hl7Parm");// HL7���ͽӿڼ���
		String tradeNo = "";
		String billDate = "";
		String billUser = "";
		// �˴β�����ҽ��
		for (int i = 0; i < orderParm.getCount("RX_NO"); i++) {
			if (null != orderParm.getValue("BILL_FLG", i)
					&& orderParm.getValue("BILL_FLG", i).equals("N")) {
				tradeNo = "";
				billDate = "''";
				billUser = "";
			} else {
				tradeNo = parm.getValue("TRADE_NO");
				billDate = "SYSDATE";
				billUser = parm.getValue("OPT_USER");
			}
			sql = "UPDATE OPD_ORDER SET BUSINESS_NO='" + tradeNo
					+ "', BILL_FLG='" + orderParm.getValue("BILL_FLG", i)
					+ "' " + " , BILL_DATE=" + billDate + ",BILL_USER='"
					+ billUser + "',BILL_TYPE='"
					+ orderParm.getValue("BILL_TYPE", i) + "' WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
					+ orderParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
					+ orderParm.getValue("SEQ_NO", i) + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));

			if (result.getErrCode() < 0) {
				return result;
			}
		}
		result = exeHl7ParmBillFlg(hl7Parm, parm, connection);
		return result;
	}

	public TParm onHl7ExeBillFlg(TParm parm, TConnection connection) {
		TParm hl7Parm = parm.getParm("hl7Parm");// HL7���ͽӿڼ���
		String tradeNo = parm.getValue("TRADE_NO");
		TParm billParm = parm.getParm("parmBill");
		TParm result = new TParm();
		String sql = "";
		// �������δ�շѵ�ҽ����Ҫ�޸�״̬
		for (int i = 0; i < billParm.getCount("RX_NO"); i++) {
			sql = "UPDATE OPD_ORDER SET BUSINESS_NO='" + tradeNo
					+ "', BILL_FLG='" + billParm.getValue("BILL_FLG", i) + "' "
					+ " , BILL_DATE=SYSDATE,BILL_USER='"
					+ parm.getValue("OPT_USER") + "',BILL_TYPE='"
					+ billParm.getValue("BILL_TYPE", i) + "' WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
					+ billParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
					+ billParm.getValue("SEQ_NO", i) + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		result = exeHl7ParmBillFlg(hl7Parm, parm, connection);
		return result;
	}

	/**
	 * ����HL7�ӿ�ִ���շ�״̬
	 * 
	 * @param hl7Parm
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm exeHl7ParmBillFlg(TParm hl7Parm, TParm parm,
			TConnection connection) {
		TParm result = new TParm();
		String sql = "";
		if (null == hl7Parm)
			return new TParm();
		for (int i = 0; i < hl7Parm.getCount("RX_NO"); i++) {
			// �޸ļ������շ�״̬
			sql = "UPDATE MED_APPLY SET BILL_FLG='"
					+ hl7Parm.getValue("BILL_FLG", i)
					+ "' WHERE APPLICATION_NO='"
					+ hl7Parm.getValue("MED_APPLY_NO", i) + "' AND CASE_NO='"
					+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"
					+ hl7Parm.getValue("RX_NO", i) + "' AND SEQ_NO='"
					+ hl7Parm.getValue("SEQ_NO", i) + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				System.out.println("result:" + result.getErrText());
				return result;
			}
		}
		return result;
	}

	/**
	 * ����HL7
	 */
	public TParm sendHL7Mes(TParm hl7ParmEnd, String patName,
			boolean EKTmessage, String caseNo) {
		/**
		 * ����HL7��Ϣ
		 * 
		 * @param admType
		 *            String �ż�ס��
		 * @param catType
		 *            ҽ�����
		 * @param patName
		 *            ��������
		 * @param caseNo
		 *            String �����
		 * @param applictionNo
		 *            String �����
		 * @param flg
		 *            String ״̬(0,����1,ȡ��)
		 */
		// int count = 0;
		// if (null != hl7ParmEnd && null != hl7ParmEnd.getData("ADM_TYPE"))
		// count = ((Vector) hl7ParmEnd.getData("ADM_TYPE")).size();
		if (hl7ParmEnd.getCount("ADM_TYPE") == 0) {
			return new TParm();
		}
		List list = new ArrayList();
		// String patName = getValue("PAT_NAME").toString();
		for (int i = 0; i < hl7ParmEnd.getCount("ADM_TYPE"); i++) {
			TParm temp = hl7ParmEnd.getRow(i);
			if (temp.getValue("TEMPORARY_FLG").length() == 0) {
				continue;
			}
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", temp.getValue("ADM_TYPE"));
			if (EKTmessage) {
				parm.setData("FLG", 1);// �˷�
			} else {
				parm.setData("FLG", 0);
			}
			parm.setData("CASE_NO", caseNo);
			parm.setData("LAB_NO", temp.getValue("MED_APPLY_NO"));
			parm.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			parm.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm);
		}
		// ���ýӿ�
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		return resultParm;

	}

	/**
	 * ���ݹҺŲ�������Ч����У���Ƿ���Ծ�����ҽ������ ============pangben 2013-4-28
	 * 
	 * @return
	 */
	public boolean canEdit(Reg reg, TParm regParm) {
		Timestamp admDate = reg.getAdmDate(); // �Һ�����
		Timestamp now = SystemTool.getInstance().getDate(); // ��ǰʱ��
		// ��ȡ�ҺŲ������С���Ч��������23��59��59���ʱ��
		// ����3/5 ���ϹҺţ�һֱ�� 3/6 ȫ�춼��Ϊ��Ч
		int effDays = regParm.getInt("EFFECT_DAYS", 0);
		if (effDays == 0) { // �������
			effDays++;
		}
		Timestamp time = StringTool.getTimestamp(StringTool.getString(
				StringTool.rollDate(admDate, effDays), "yyyyMMdd")
				+ "235959", "yyyyMMddHHmmss");
		// ��ǰʱ�����ڹҺ��޶�ʱ�� �򲻿����޸�
		if (now.getTime() > time.getTime()) {
			return false;
		}
		return true;
	}

	private Map<String, Double> getPayOthers(TParm parm) {
		String businessNos = "";
		for (int i = 0; i < parm.getCount("BUSINESS_NO"); i++) {
			businessNos += "'" + parm.getValue("BUSINESS_NO", i) + "',";
		}
		businessNos = businessNos.substring(0, businessNos.length() - 1);
		String sql = " SELECT SUM (PAY_OTHER3) PAY_OTHER3, SUM (PAY_OTHER4) PAY_OTHER4"
				+ " FROM EKT_TRADE"
				+ " WHERE TRADE_NO IN ("
				+ businessNos
				+ ")";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Map<String, Double> map = new HashMap<String, Double>();
		map.put(EKTpreDebtTool.PAY_TOHER3, result.getDouble("PAY_OTHER3", 0));
		map.put(EKTpreDebtTool.PAY_TOHER4, result.getDouble("PAY_OTHER4", 0));
		return map;
	}
public TParm reduceBackFee(TParm parm,TConnection connection ){
	TParm result = new TParm();
	result = EKTTool.getInstance().updataEktData(parm,
			connection);
	return result;
}

}
