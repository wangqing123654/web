package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;
import jdo.ins.InsManager;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ���ҽ�����̷���
 * </p>
 * 
 * <p>
 * Description: ���ҽ�����̷���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore 2011-12-23
 * </p>
 * 
 * @author pangben 2011-11-23
 * @version 1.0
 */
public class INSTJTool extends TJDOTool {
	// private TParm ruleParm;// ��Ŀ�ֵ�
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	// double[] sum = { 3, 3.5, 3, 3.5 };
	/**
	 * ʵ��
	 */
	public static INSTJTool instanceObject;
	// ������ݲ���У���Ƿ�Ϊ��
	private String[] insOpd = { "REGION_CODE", "CASE_NO", "CONFIRM_NO",
			"MR_NO", "PHA_AMT", "PHA_NHI_AMT", "EXM_AMT", "EXM_NHI_AMT",
			"TREAT_AMT", "TREAT_NHI_AMT", "OP_AMT", "OP_NHI_AMT", "BED_AMT",
			"BED_NHI_AMT", "MATERIAL_AMT", "MATERIAL_NHI_AMT", "OTHER_AMT",
			"OTHER_NHI_AMT", "BLOODALL_AMT", "BLOODALL_NHI_AMT", "BLOOD_AMT",
			"BLOOD_NHI_AMT", "TOT_AMT", "NHI_AMT", "START_STANDARD_AMTS",
			"OTOT_PAY_AMT", "START_STANDARD_AMT", "ADD_AMT", "OWN_AMT",
			"INS_STANDARD_AMT", "TRANBLOOD_OWN_AMT", "APPLY_AMT", "OTOT_AMT",
			"OINSTOT_AMT", "INS_DATE", "INSAMT_FLG", "OPT_USER", "OPT_DATE",
			"OPT_TERM", "ACCOUNT_PAY_AMT", "UNACCOUNT_PAY_AMT",
			"ACCOUNT_LEFT_AMT", "REFUSE_AMT", "REFUSE_ACCOUNT_PAY_AMT",
			"REFUSE_DESC", "CONFIRM_F_USER", "CONFIRM_F_DATE",
			"CONFIRM_S_USER", "CONFIRM_S_DATE", "SLOW_DATE", "SLOW_PAY_DATE",
			"REFUSE_CODE", "REFUSE_OTOT_AMT", "MZ_DIAG", "DRUG_DAYS",
			"INS_STD_AMT", "INS_CROWD_TYPE", "INS_PAT_TYPE", "OWEN_PAY_SCALE",
			"REDUCE_PAY_SCALE", "REAL_OWEN_PAY_SCALE", "SALVA_PAY_SCALE",
			"BASEMED_BALANCE", "INS_BALANCE", "ISSUE", "BCSSQF_STANDRD_AMT",
			"PERCOPAYMENT_RATE_AMT", "INS_HIGHLIMIT_AMT", "TOTAL_AGENT_AMT",
			"FLG_AGENT_AMT", "APPLY_AMT_R", "FLG_AGENT_AMT_R", "REFUSE_DATE",
			"REAL_PAY_LEVEL", "VIOLATION_REASON_CODE", "ARMY_AI_AMT",
			"SERVANT_AMT", "INS_PAY_AMT", "UNREIM_AMT", "REIM_TYPE",
			"RECP_TYPE", "INV_NO" };

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSTJTool
	 */
	public static INSTJTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJTool();
		return instanceObject;
	}

	/**
	 * ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            int
	 * @param insOrderType
	 *            int
	 * @return TParm
	 */
	public TParm comminuteFeeAndInsOrder(TParm parm, TParm tempParm, int type,
			int insOrderType) {
		TParm REG_PARM = parm.getParm("REG_PARM"); // ��ùҺŷ����������
		String regionCode = parm.getValue("REGION_CODE");
		// ����ҽ�ƿ��շ�ע��----�ۿ��Ʊʱʹ���������� �վݱ� BIL_OPB_RECP ҽ������޸�
		String opbEktFeeFlg = parm.getValue("OPBEKTFEE_FLG");
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		String printNo = parm.getValue("PRINT_NO"); // Ʊ�ݺ�
		TParm comminuteFeeParm = new TParm(); // ���÷ָ�ز���
		TParm interFaceParm = null; // �ӿڷ�������
		TParm result = new TParm();
		// ������ţ���ֹ������ͻ
		// TParm SeqParm = parm.getParm("opbReadCardParm");
		tempParm.setData("RECP_TYPE", parm.getValue("RECP_TYPE")); // �������� ��REG
		TParm maxSeqOpdOrderParm = INSOpdOrderTJTool.getInstance()
				.selectMAXSeqNo(tempParm);
		tempParm.setData("TYPE", type); // 1.��ְ 2.�Ǿ�
		tempParm.setData("EREG_FLG", parm.getValue("EREG_FLG")); // �ż��� 0.��ͨ
		// 1.����
		int maxSeq = 0; // ������
		if (null != maxSeqOpdOrderParm.getValue("SEQ_NO", 0)
				&& maxSeqOpdOrderParm.getInt("SEQ_NO", 0) > 0) {
			maxSeq = maxSeqOpdOrderParm.getInt("SEQ_NO", 0);
		}
		//
		result = INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ����ҽ��
		for (int j = 0; j < REG_PARM.getCount("ORDER_CODE"); j++) {
			// ���÷ָ�����
			if (type == 1) // ��ְ
				interFaceParm = DataDown_sp1_B(REG_PARM, tempParm, regionCode,
						j);
			else
				// �Ǿ�
				interFaceParm = DataDown_sp1_G(REG_PARM, tempParm, regionCode,
						j);
			if (!getErrParm(interFaceParm)) {
				return interFaceParm;
			}
			interFaceParm.setData("ORDER_CODE", REG_PARM.getValue("ORDER_CODE",
					j)); // ��Ҫͨ��ORDER_CODE �ȽϽ��
			if (null != REG_PARM.getValue("RECEIPT_TYPE", j)) { // �Һ�ʹ���жϷ�������
				interFaceParm.setData("RECEIPT_TYPE", REG_PARM.getValue(
						"RECEIPT_TYPE", j));
			}
			// comminuteFeeParm.addRowData(interFaceParm,j);
			// ���÷ָ����ݱ���
			// System.out.println("comminuteFeeParm:" + comminuteFeeParm);
			// ���INS_OPD_ORDER ������DataDown_sp1
			comminuteFeeParm(comminuteFeeParm, interFaceParm, j, opbEktFeeFlg);
			result = insOrderTemp(parm, REG_PARM, interFaceParm, maxSeq, j,
					printNo);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				// connection.close();
				return result;
			}
		}
		return comminuteFeeParm; // ���÷ָ�ز�
	}

	/**
	 * ִ���ۼ���������
	 * 
	 * @param parm
	 *            TParm
	 * @param addParm
	 *            TParm
	 * @param result
	 *            TParm
	 * @param seqParm
	 *            TParm
	 * @return TParm
	 */
	public TParm exeAdd(TParm parm, TParm addParm, TParm result, TParm seqParm) {
		TParm tempParm = new TParm();
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO")); // �����
		tempParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // �ʸ�ȷ�����
		tempParm.setData("REGION_CODE", parm.getValue("NEW_REGION_CODE"));
		tempParm.setData("SEQ_NO", seqParm.getInt("SEQ_NO", 0) + 1);
		tempParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // ����ʱ��
		tempParm.setData("NHI_ORDER_CODE", "***018"); // ҽ������
		tempParm.setData("ORDER_CODE", "***018"); // ҽ������
		tempParm.setData("ORDER_DESC", "�ۼ�����"); // ����
		tempParm.setData("OWN_RATE", 1); // �Ը���־
		tempParm.setData("PRICE", 0); // ����
		tempParm.setData("QTY", 0); // ����
		tempParm.setData("TOTAL_AMT", result.getDouble("NHI_AMT")
				+ result.getDouble("ADDPAY_AMT")); // �������
		tempParm.setData("TOTAL_NHI_AMT", result.getDouble("NHI_AMT")); // �걨���
		tempParm.setData("ADDPAY_AMT", result.getDouble("ADDPAY_AMT")); // �ۼ��������
		tempParm.setData("ADDPAY_FLG", "Y"); // �ۼ����� ��־
		tempParm.setData("NHI_ORD_CLASS_CODE", "06"); // ͳ�ƴ���
		tempParm.setData("INV_NO", seqParm.getValue("INV_NO", 0)); // Ʊ��
		tempParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		tempParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		tempParm.setData("RECP_TYPE", parm.getValue("RECP_TYPE"));
		TParm resultParm = INSOpdOrderTJTool.getInstance()
				.deleteAddINSOpdOrder(tempParm);
		if (resultParm.getErrCode() < 0) {
			return resultParm;
		}
		resultParm = INSOpdOrderTJTool.getInstance()
				.insertAddOpdOpder(tempParm);
		if (resultParm.getErrCode() < 0) {
			return resultParm;
		}
		return resultParm;
	}

	/**
	 * ���INS_OPD����(���÷ָ�����;A ���� ���κͽ�������;L �������� ���CONFIRM_NO)
	 * 
	 * @param parm
	 *            TParm
	 * @param insParm
	 *            TParm
	 * @return boolean
	 */
	public boolean insterInsOpdParm(TParm parm, TParm insParm) {
		TParm result = new TParm();
		double ownAmt = 0.00; // �Էѽ��
		double addAmt = 0.00; // �������
		double applyAmt = 0.00; // �걨���
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			ownAmt += parm.getDouble("OWN_AMT"); // �Էѽ��
			addAmt += parm.getDouble("ADD_AMT"); // �������
			applyAmt += parm.getDouble("APPLY_AMT"); // �걨���
		}
		result.setData("REGION_CODE", insParm.getValue("NEW_REGION_CODE")); // ҽ���������
		result.setData("CASE_NO", insParm.getValue("CASE_NO")); // �������
		result.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // ҽ���������
		result.setData("MR_NO", insParm.getValue("MR_NO")); // ������
		result.setData("OWN_AMT", ownAmt);
		result.setData("ADD_AMT", addAmt);
		result.setData("APPLY_AMT", applyAmt);
		result.setData("INS_CROWD_TYPE", insParm.getValue("CROWD_TYPE")); // ҽ��������
		result.setData("INS_PAT_TYPE", insParm.getValue("INS_PAT_TYPE")); // ��������
		result.setData("INSAMT_FLG", 0); // 36�籣���ʱ�־INS_OPD��INSAMT_FLG���˱�־Ϊ0
		result.setData("RECP_TYPE", insParm.getValue("RECP_TYPE")); // ����
		result.setData("OPT_USER", insParm.getValue("OPT_USER"));
		result.setData("OPT_TERM", insParm.getValue("OPT_TERM"));
		// result.setData("INV_NO", insParm.getValue("PRINT_NO")); // Ʊ��
		// result.setData("SEQ_NO", maxSeq + 1); // ���
		// ��REG/OPB
		result = INSOpdTJTool.getInstance().insertINSOpd(result);
		if (result.getErrCode() < 0) {
			// System.out.println("����");
			return false;
		}
		return true;
	}

	/**
	 * ���÷ָ����ݱ���
	 * 
	 * @param comminuteFeeParm
	 *            TParm
	 * @param insOrderParm
	 *            TParm
	 * @param i
	 *            int
	 * @param opbEktFeeFlg
	 *            String
	 */
	private void comminuteFeeParm(TParm comminuteFeeParm, TParm insOrderParm,
			int i, String opbEktFeeFlg) {
		comminuteFeeParm.addData("OWN_AMT", StringTool.round(insOrderParm
				.getDouble("OWN_AMT"), 2)); // ȫ�Էѽ��(ALL_OWN_ATM)
		comminuteFeeParm.addData("ADD_AMT", StringTool.round(insOrderParm
				.getDouble("ADDPAY_AMT"), 2)); // �������(ADDN_AMT)
		comminuteFeeParm.addData("APPLY_AMT", StringTool.round(insOrderParm
				.getDouble("NHI_AMT"), 2)); // �걨���(APPLY_AMT)
		comminuteFeeParm.addData("OWN_RATE", insOrderParm.getValue("OWN_RATE")); // �Ը�������OWN_RATE��
		comminuteFeeParm.addData("ADDPAY_FLG", insOrderParm
				.getInt("ADDPAY_FLG")); // �ۼ�������־(GRAND_ADDN_FLG)
		comminuteFeeParm.addData("NHI_ORD_CLASS_CODE", insOrderParm
				.getValue("NHI_ORD_CLASS_CODE")); // ͳ�ƴ���(STATS_CODE)
		comminuteFeeParm.addData("ORDER_CODE", insOrderParm
				.getValue("ORDER_CODE")); // ҽ������
		comminuteFeeParm.addData("RECEIPT_TYPE", insOrderParm
				.getValue("RECEIPT_TYPE")); // ���ͣ��Һ�ʹ��
	}

	/**
	 * ��ְ���÷ָ������:����DataDown_sp1��B������
	 * 
	 * @param REG_PARM
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param regionCode
	 *            String
	 * @param j
	 *            int
	 * @return TParm
	 */
	public TParm DataDown_sp1_B(TParm REG_PARM, TParm tempParm,
			String regionCode, int j) {
		TParm parm = DataDown_sp1_B_OR_G(REG_PARM, tempParm, regionCode, j);
		parm.addData("PARM_COUNT", 9);
		TParm result = commInterFace("DataDown_sp1", "B", parm);
		// System.out.println("��ְ���÷ָ������:::::::" + result);
		return result;
	}

	/**
	 * У��ҽ������
	 * 
	 * @param tempParm
	 *            TParm
	 * @param REG_PARM
	 *            TParm
	 * @param i
	 *            int
	 * @param insOrderParm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private void getParmValue(TParm tempParm, TParm REG_PARM, int i,
			TParm insOrderParm, boolean flg) {
		//TParm orderParm = new TParm();
		if (flg) {
			if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 0) { // ����
				insOrderParm.setData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_O", i)); // 5
				//orderParm = checkCode(REG_PARM.getValue("NHI_CODE_O", i));
			} else if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 1) { // ����
				insOrderParm.setData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_E", i)); // 5
				//orderParm = checkCode(REG_PARM.getValue("NHI_CODE_E", i));
			}
		} else {
			if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 0) { // ����
				insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_O", i)); // 5
			//	orderParm = checkCode(REG_PARM.getValue("NHI_CODE_O", i));
			} else if (null != tempParm.getValue("EREG_FLG")
					&& tempParm.getInt("EREG_FLG") == 1) { // ����
				insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
						"NHI_CODE_E", i)); // 5
			//	orderParm = checkCode(REG_PARM.getValue("NHI_CODE_E", i));
			}
		}
	}

	/**
	 * ����ҩƷ��־��ֵ
	 * 
	 * @param tempParm
	 *            TParm
	 * @param insOrderParm
	 *            TParm
	 * @param regParm
	 *            TParm
	 * @param i
	 *            int
	 * @param flg
	 *            boolean
	 */
	private void getPhaAdd(TParm tempParm, TParm insOrderParm, TParm regParm,
			int i, boolean flg) {
		if (flg) {
			//TParm orderParm = checkCode(insOrderParm.getValue("NHI_ORDER_CODE"));
			if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("REG")) {
				insOrderParm.setData("PHAADD_FLG", 0); // 6 ����ҩƷ��־

			} else if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("OPB")) {
				insOrderParm.setData("PHAADD_FLG", null != regParm
						.getValue("ZFBL1")
						&& regParm.getDouble("ZFBL1") > 0 ? "1" : "0"); // 6
				// ����ҩƷ��־-
			}
		} else {
//			TParm orderParm = checkCode(insOrderParm.getValue("NHI_ORDER_CODE",
//					i));
			if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("REG")) {
				insOrderParm.addData("PHAADD_FLG", 0); // 6 ����ҩƷ��־

			} else if (null != tempParm.getValue("RECP_TYPE")
					&& tempParm.getValue("RECP_TYPE").equals("OPB")) {
				insOrderParm.addData("PHAADD_FLG", null != regParm
						.getValue("ZFBL1")
						&& regParm.getDouble("ZFBL1") > 0 ? "1" : "0"); // 6
				// ����ҩƷ��־----
			}
		}

	}

	/**
	 * ���÷ָ�����ݹ��÷���(��Ŀ�ֵ�;A �����е�ҽ����Ϣ;L OR E ��������)
	 * 
	 * @param REG_PARM
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param regionCode
	 *            String
	 * @param i
	 *            int
	 * @return TParm
	 */
	private TParm DataDown_sp1_B_OR_G(TParm REG_PARM, TParm tempParm,
			String regionCode, int i) {
		TParm insOrderParm = new TParm();
		// ���ý��G����ִ�з��÷ָ�

		// System.out.println("REG_PARMREG_PARMREG_PARM::::::::" + REG_PARM);
		if (null != tempParm.getValue("EREG_FLG")
				&& tempParm.getInt("EREG_FLG") == 1) { // ����
			insOrderParm.addData("FULL_OWN_FLG", null != REG_PARM.getValue(
					"NHI_CODE_E", i) ? "0" : "1"); // 7 ȫ�Էѱ�־
			insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
					"NHI_CODE_E", i)); // 1�շ���Ŀ����
		} else if (null != tempParm.getValue("EREG_FLG")
				&& tempParm.getInt("EREG_FLG") == 0) { // ����
			insOrderParm.addData("NHI_ORDER_CODE", REG_PARM.getValue(
					"NHI_CODE_O", i)); // 1�շ���Ŀ����
			insOrderParm.addData("FULL_OWN_FLG", null != REG_PARM.getValue(
					"NHI_CODE_O", i) ? "0" : "1"); // 7 ȫ�Էѱ�־
		}
		// System.out.println("insOrderParm:::::"+insOrderParm);
		getPhaAdd(tempParm, insOrderParm, REG_PARM, i, false);
		if (null != tempParm.getValue("TYPE") && tempParm.getInt("TYPE") == 1) { // ��ְ
			insOrderParm.addData("CTZ1_CODE", tempParm.getValue("CTZ_CODE")); // 2��Ա���
		} else if (null != tempParm.getValue("TYPE")
				&& tempParm.getInt("TYPE") == 2) { // �Ǿ�
			insOrderParm.addData("CTZ1_CODE", tempParm.getValue("PAT_TYPE")); // 2��Ա���
		}
		// L������CTZ_CODE
		// ��Ա���
		// A����
		// ���� �Һ�����:1
		insOrderParm.addData("QTY", REG_PARM.getDouble("DOSAGE_QTY", i)); // 3
		insOrderParm.addData("TOTAL_AMT", REG_PARM.getDouble("AR_AMT", i)); // 4
																			// �������-
		insOrderParm.addData("TIPTOP_BED_AMT", StringTool.round(tempParm
				.getDouble("BED_FEE"), 3)); // 5

		// ��ߴ�λ��
		// SYS_REGION
		// �л��

		insOrderParm.addData("HOSP_NHI_NO", regionCode); // 8 ҽԺ����
		insOrderParm.addData("CHARGE_DATE", df1.format(SystemTool.getInstance()
				.getDate())); // 9
		// System.out.println("���÷ָ�󷵻�:DataDown_sp1_B_OR_D:" + insOrderParm);
		return insOrderParm;
	}

	/**
	 * �Ǿӷ��÷ָ������:����DataDown_sp1��G������
	 * 
	 * @param REG_PARM
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param regionCode
	 *            String
	 * @param i
	 *            int
	 * @return TParm
	 */
	public TParm DataDown_sp1_G(TParm REG_PARM, TParm tempParm,
			String regionCode, int i) {
		TParm parm = DataDown_sp1_B_OR_G(REG_PARM, tempParm, regionCode, i);
		parm.addData("PARM_COUNT", 9);
		TParm result = commInterFace("DataDown_sp1", "G", parm);
		return result;
	}

	/**
	 * ���ʶ��DataDown_sp����ˢ����L�� ��ְ��ͨ
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_L(TParm insParm) {

		// TParm result = new TParm();
		TParm parmL = new TParm();
		// parm.addData("TEXT", text);
		// System.out.println("-------insParm--------" + insParm);
		parmL.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // ���ţ���A ��������
		parmL.addData("PASSWORD", insParm.getValue("PASSWORD")); // ����
		parmL.addData("HOSP_NHI_CODE", insParm.getValue("REGION_CODE")); // ҽԺ����
		parmL.addData("COM_CODE", null); // ��������
		parmL.addData("CHECK_CODES", insParm.getValue("CHECK_CODES")); // ˢ����֤��
		parmL.addData("PARM_COUNT", 5); // ��������
		TParm result = commInterFace("DataDown_sp", "L", parmL); // ����21
		return result;
	}

	/**
	 * ������ϸ���ɣ�����DataDown_sp, ��������ϴ����ף�M������
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_M(TParm insParm, String type) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1����˳���
		// L��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 ҽԺ����
		parm.addData("ODO_FLG", 1); // 3 ��������־д��
		// TParm opbReadCardParm = insParm.getParm("opbReadCardParm");// L��������
		parm.addData("PAY_TYPE", "11"); // 4
		// ֧�����:11���21סԺ��
		// 31ҩ��
		TParm diagRecparm = insParm.getParm("diagRecparm"); // �������
		TParm deptParm = insParm.getParm("deptParm"); // ҽ�����Ҵ���
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("ODO_DIAG", "�Һ�"); // 5
			// parm.addData("DOCTOR_SID", "�Һ�");// 7ҽ������
		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {
			StringBuffer diagString = new StringBuffer();
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				diagString.append(diagRecparm.getValue("ICD_CODE", i)
						+ diagRecparm.getValue("ICD_CHN_DESC", i));
			}
			parm.addData("ODO_DIAG", diagString.toString()); // 5
		}
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("DOCTOR_SID", "�Һ�"); // 7ҽ������
			// ���������Ҫ���շ�����
			parm.addData("DRUG_DAYS", 1); // 6 ��ҩ����
		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {
			parm.addData("DOCTOR_SID", deptParm.getValue("DR_QUALIFY_CODE")); // 7ҽ������
			// ���������Ҫ���շ�����
			parm.addData("DRUG_DAYS", insParm.getInt("TAKE_DAYS")); // 6 ��ҩ����
		}
		// ҽʦ����----û���������*****************ҽʦ���֤����
		parm.addData("OPT_USER_CODE", insParm.getValue("OPT_USER")); // 8 ����Ա���
		// parm.addData("OPT_USER_CODE", "000484"); // 8 ����Ա���
		parm.addData("REG_FLG", type); // 9 �Һű�־:1 �Һ�0 �ǹҺ�
		parm.addData("DEPT_CODE", deptParm.getValue("INS_DEPT_CODE")); // 10
		// �Ʊ����
		parm.addData("COMMUNITY_NO", null); // 11 ��������
		parm.addData("ADM_TYPE", insParm.getValue("EREG_FLG")); // 12
		//===============pangben 2012-4-13 ��Ӵ���ʱ��
		parm.addData("RX_DATE",df.format(SystemTool.getInstance().getDate())); // 13 ����ʱ��
		//===============pangben 2012-4-13 stop
		// �Ƿ���:1����0��ͨ
		parm.addData("PARM_COUNT", 13);
		TParm result = commInterFace("DataDown_sp", "M", parm);
		result.setData("diagRecparm", diagRecparm.getData());
		// System.out.println("������ϸ����settlementDetailsParm::" + result);
		return result;
	}

	/**
	 * ��ְ���ؽ�����ϸ���ɣ� ���ؽ����ϴ����ף�F������
	 * 
	 * @param insParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_mts_F(TParm insParm, TParm tempParm, String type) {
		TParm parm = DataDown_mtsORcmts_F(insParm, tempParm, type);
		TParm result = commInterFace("DataDown_mts", "F", parm);
		return result;
	}

	/**
	 * ��ְ\�Ǿ����ؽ�����ϸ����
	 * 
	 * @param insParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	private TParm DataDown_mtsORcmts_F(TParm insParm, TParm tempParm,
			String type) {
		TParm parm = new TParm();
		// System.out.println("insParm::::::::"+insParm);
		parm.addData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO")); // 1����˳��� E
		// ��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2ҽԺ����
		parm.addData("PAT_TYPE", tempParm.getValue("CTZ_CODE")); // 3��Ա���11��ְ21����51����ǰ�Ϲ���
		parm.addData("PAY_KIND", tempParm.getValue("PAY_KIND")); // 4
		// ֧�����:11���ҩ��21סԺ//֧�����12
		// �����������ⲡ13 �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		TParm diagRecparm = insParm.getParm("diagRecparm");
		TParm deptParm = insParm.getParm("deptParm"); // ҽ�����Ҵ���
		// ��ѯҽ��������ϡ� �Ʊ���� ��ҽʦ����
		if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("REG")) {
			parm.addData("DIAG_CODE", "�Һ�"); // 5
			parm.addData("DR_CODE", "�Һ�"); // 7ҽ������

		} else if (null != insParm.getValue("RECP_TYPE")
				&& insParm.getValue("RECP_TYPE").equals("OPB")) {

			StringBuffer diagString = new StringBuffer();
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				diagString.append(diagRecparm.getValue("ICD_CODE", i)
						+ diagRecparm.getValue("ICD_CHN_DESC", i));
			}
			parm.addData("DIAG_CODE", diagString.toString()); // 5
			// parm.addData("DR_CODE", diagRecparm.getValue("DR_QUALIFY_CODE",
			// 0));// 7ҽ������
			parm.addData("DR_CODE", deptParm.getValue("DR_QUALIFY_CODE")); // 7ҽ������
		}

		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 5�������--??
		parm.addData("DRUG_DAYS", insParm.getInt("TAKE_DAYS")); // 6 ��ҩ���� �����ҩ����
		parm.addData("DEPT_CODE", deptParm.getValue("INS_DEPT_CODE")); // 8
		// �Ʊ����
		parm.addData("OPT_USER", insParm.getValue("OPT_USER")); // 10
		// ����Ա���--------��������
		parm.addData("REG_FLG", type); // 11 �Һű�־:1 �Һ�0 �ǹҺ�
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 12
		// ����ҽԺ:�ϴ�����ҽԺ���룬�����б���İ�������ҽԺ�����ϴ�������û�еĴ�����ҽԺ����(����)----??????
		parm.addData("COMMUNITY_NO", null); // 13 ��������
		//===============pangben 2012-4-13 stop ����ʱ��
		parm.addData("RX_DATE", df.format(SystemTool.getInstance().getDate())); // 14 ��������
		//===============pangben 2012-4-13
		parm.addData("PARM_COUNT", 14);
		// System.out.println("DataDown_mtsORcmts_F����������parm��"+parm);
		return parm;
	}

	/**
	 * ��ϸ�ϴ�����:��������
	 * 
	 * @param insParm
	 *            TParm
	 * @param result
	 *            TParm
	 * @param nhiCode
	 *            String
	 * @return boolean
	 */
	public boolean upInterfaceINSOrderParm(TParm insParm, TParm result,
			String nhiCode) {
		// ��ѯҽ������
		//TParm orderParm = checkCode(insParm.getValue("NHI_ORDER_CODE"));
//		if (orderParm.getErrCode() < 0) {
//			return false;
//		}
		result.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1��ҽ˳��ţ�L�������λ�E��������
		// �����л�õĲ���
		result.addData("CHARGE_DATE", df1.format(SystemTool.getInstance()
				.getDate())); // 2 ��ϸ����ʱ��
		result.addData("SEQ_NO", insParm.getValue("SEQ_NO")); // 3 ���
		result.addData("HOSP_NHI_NO", nhiCode); // 4ҽԺ����
		result.addData("NHI_ORDER_CODE", insParm.getValue("NHI_ORDER_CODE")); // 5���ҽ����Ŀ�ֵ�����
		result.addData("PHAADD_FLG", null != insParm.getValue("PHAADD_FLG")
				&& insParm.getValue("PHAADD_FLG").equals("Y") ? "1" : "0"); // 19
		result.addData("USAGE", insParm.getValue("ROUTE")); // 21 �÷�---ҽ�����
		result.addData("DOSAGE", insParm.getValue("TAKE_QTY")); // 20 ����----ҽ�����
		result.addData("TAKE_DAYS", insParm.getValue("TAKE_DAYS")); // 23 ��ҩ����

		// �շ���Ŀ����

		result.addData("ORDER_DESC", insParm.getValue("ORDER_DESC")); // 6ҽԺ������Ŀ����
		result.addData("OWN_RATE", insParm.getValue("OWN_RATE")); // 7
		// result.addData("OWN_RATE", 1.00); // 7
		// �Ը�����:��Ŀ�ֵ��Ը�����
		result.addData("DOSE_CODE", insParm.getValue("DOSE_CODE")); // 8 ����
		result.addData("STANDARD", insParm.getValue("STANDARD")); // 9 ���
		result.addData("PRICE", insParm.getDouble("PRICE")); // 10
		// ����:������ü۸�
		result.addData("QTY", insParm.getDouble("QTY")); // 11 ����
		result.addData("TOTAL_AMT", StringTool.round(insParm
				.getDouble("TOTAL_AMT"), 2)); // 12
		// �������
		result.addData("TOTAL_NHI_AMT", StringTool.round(insParm
				.getDouble("TOTAL_NHI_AMT"), 2)); // 13
		// �걨���
		result.addData("OWN_AMT", StringTool.round(
				insParm.getDouble("OWN_AMT"), 2)); // 14
		// ȫ�Էѽ��
		result.addData("ADDPAY_AMT", StringTool.round(insParm
				.getDouble("ADDPAY_AMT"), 2)); // 15�������
		result.addData("OP_FLG", "0"); // 16 �������ñ�־:1�ǡ�0��
		result.addData("ADDPAY_FLG", null != insParm.getValue("ADDPAY_FLG")
				&& insParm.getValue("ADDPAY_FLG").equals("Y") ? "1" : "0"); // 17�ۼ�������־
		result.addData("NHI_ORD_CLASS_CODE", insParm
				.getValue("NHI_ORD_CLASS_CODE")); // 18 ͳ�ƴ���

		result.addData("CONFIRM_ID", insParm.getValue("HYGIENE_TRADE_CODE")); // 22
		result.addData("SPECIAL_CASE_DESC", insParm
				.getValue("SPECIAL_CASE_DESC")); // �������˵��--ҽ����ע
		// ��׼�ĺ�:��Ҫ���

		result.addData("INV_NO", insParm.getValue("INV_NO")); // 24 ҽ��ר��Ʊ�ݺ�
		// System.out.println("��ϸ�ϴ�:" + result);
		return true;
	}

	/**
	 * ��ְ��ͨ��ϸ�ϴ�����:����DataUpload,��B������
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataUpload_B(TParm insParm) {
		insParm.addData("PARM_COUNT", 24); // ��������
		TParm result = commInterFace("DataUpload", "B", insParm);
		return result;
	}

	/**
	 * �Ǿ�������ϸ�ϴ�����:����DataDown_cmts,��F������
	 * 
	 * @param insParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_cmts_F(TParm insParm, TParm tempParm, String type) {
		TParm parm = DataDown_mtsORcmts_F(insParm, tempParm, type);
		TParm result = commInterFace("DataDown_cmts", "F", parm);
		return result;
	}

	/**
	 * ��ְ������ϸ�ϴ�����:����DataUpload,��C������
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataUpload_C(TParm insParm) {
		insParm.addData("PARM_COUNT", 25); // ��������
		TParm result = commInterFace("DataUpload", "C", insParm);
		return result;
	}

	/**
	 * �Ǿ�������ϸ�ϴ�����:����DataUpload,��F������
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataUpload_F(TParm insParm) {
		insParm.addData("PARM_COUNT", 25); // ��������
		TParm result = commInterFace("DataUpload", "F", insParm);
		return result;
	}

	/**
	 * �˷Ѳ������һ����ֵ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void balanceParm(TParm parm) {
		// TParm maxSeqOpdParm =
		// INSOpdTJTool.getInstance().selectMAXSeqNo(parm);
		// int maxSeq = 0;// ������
		// if (null != maxSeqOpdParm.getValue("SEQ_NO", 0)
		// && maxSeqOpdParm.getInt("SEQ_NO", 0) > 0) {
		// maxSeq = maxSeqOpdParm.getInt("SEQ_NO", 0);
		// }
		if (null == parm) {
			return;
		}
		checkOutParm(parm);
		parm.setData("RECP_TYPE", parm.getValue("UNRECP_TYPE")); // �˷�
		parm.setData("INSAMT_FLG", 0); // δ���˲���
		// �������
		parm.setData("TOT_AMT", parm.getDouble("TOT_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("TOT_AMT")); // ��ֵ
		// �걨���
		parm.setData("NHI_AMT", parm.getDouble("NHI_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("NHI_AMT")); // ��ֵ
		// �������
		parm.setData("ADD_AMT", parm.getDouble("ADD_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("ADD_AMT")); // ��ֵ
		// �Էѽ��
		parm.setData("OWN_AMT", parm.getDouble("OWN_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("OWN_AMT")); // ��ֵ
		// ר������籣֧��
		parm.setData("OTOT_AMT", parm.getDouble("OTOT_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("OTOT_AMT")); // ��ֵ
		// ҽ��֧�����
		parm.setData("INS_PAY_AMT", parm.getDouble("INS_PAY_AMT") == 0 ? "0"
				: "-" + parm.getDouble("INS_PAY_AMT")); // ��ֵ
		// ͳ������걨���
		parm.setData("APPLY_AMT", parm.getDouble("APPLY_AMT") == 0 ? "0" : "-"
				+ parm.getDouble("APPLY_AMT")); // ��ֵ
		// �����ʻ�ʵ��֧�����
		parm.setData("ACCOUNT_PAY_AMT",
				parm.getDouble("ACCOUNT_PAY_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("ACCOUNT_PAY_AMT")); // ��ֵ
		//����δ�������
		parm.setData("UNREIM_AMT",
				parm.getDouble("UNREIM_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("UNREIM_AMT")); // ��ֵ
		// ���˻�֧��
		parm.setData("UACCOUNT_PAY_AMT",
				parm.getDouble("UACCOUNT_PAY_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("UACCOUNT_PAY_AMT")); // ��ֵ
		// �����ʻ��������
		parm.setData("OINSTOT_AMT", parm.getDouble("OINSTOT_AMT") == 0 ? "0"
				: "-" + parm.getDouble("OINSTOT_AMT")); // ��ֵ
		// ͳ��֧�����
		parm.setData("TOTAL_AGENT_AMT",
				parm.getDouble("TOTAL_AGENT_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("TOTAL_AGENT_AMT")); // ��ֵ

		parm.setData("ARMY_AI_AMT", parm.getDouble("ARMY_AI_AMT") == 0 ? "0"
				: "-" + parm.getDouble("ARMY_AI_AMT")); // ��ֵ
		parm.setData("SERVANT_AMT", parm.getDouble("SERVANT_AMT") == 0 ? "0"
				: "-" + parm.getDouble("SERVANT_AMT")); // ��ֵ
		parm.setData("FLG_AGENT_AMT",
				parm.getDouble("FLG_AGENT_AMT") == 0 ? "0" : "-"
						+ parm.getDouble("FLG_AGENT_AMT")); // ��ֵ
		String[] type = { "PHA_", "EXM_", "TREAT_", "OP_", "BED_", "MATERIAL_",
				"OTHER_", "BLOOD_", "BLOODALL_" };
		for (int i = 0; i < type.length; i++) {
			parm.setData(type[i] + "AMT",
					parm.getDouble(type[i] + "AMT") == 0 ? "0" : "-"
							+ parm.getDouble(type[i] + "AMT"));
			parm.setData(type[i] + "NHI_AMT", parm.getDouble(type[i]
					+ "NHI_AMT") == 0 ? "0" : "-"
					+ parm.getDouble(type[i] + "NHI_AMT"));
		}
		// parm.setData("SEQ_NO", maxSeq + 1);// 2 ���

	}

	/**
	 * У���Ƿ�Ϊ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void checkOutParm(TParm parm) {
		for (int i = 0; i < insOpd.length; i++) {
			if (null == parm.getValue(insOpd[i])
					|| parm.getValue(insOpd[i]).length() <= 0) {
				parm.setData(insOpd[i], "");
			}

		}

	}

	/**
	 * ����������޸�INS_OPD����---��Ҫȷ���ֶ����ƣ���������
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm balanceParm(TParm insParm, TParm interFaceParm) {
		// System.out.println("�������::::::::parm::" + insParm);
		String[] type = { "PHA_", "EXM_", "TREAT_", "OP_", "BED_", "MATERIAL_",
				"OTHER_", "BLOOD_", "BLOODALL_" };
		TParm result = null;
		TParm inParm = new TParm();
		TParm diagRecparm = insParm.getParm("diagRecparm"); // ҽ������
		// �����˻�ʵ��֧�����=�������ʻ��������͡������ʻ����ıȽϣ�
		// ����������ʻ������� ���� �������ʻ��� �򡰸����ʻ�ʵ��֧����ֵΪ�������ʻ���
		// �������ʻ�֧������ֵΪ ���������ʻ������� ���������ʻ����� �� ���Էѽ�
		// ��֮ �������ʻ�ʵ��֧���� Ϊֵ �������ʻ��������������ʻ�֧������ֵΪ���Էѽ���
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"TOT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		inParm.setData("REGION_CODE", insParm.getValue("REGION_CODE")); // 1
		// ҽԺ����
		inParm.setData("CASE_NO", insParm.getValue("CASE_NO")); // 2
		inParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 3�ʸ�ȷ�Ϻ�
		inParm.setData("MR_NO", insParm.getValue("MR_NO")); // 4 ������
		double NHI_AMT = 0.00;
		for (int i = 0; i < type.length; i++) {
			NHI_AMT += interFaceParm.getDouble(type[i] + "NHI_AMT");
		}
		inParm.setData("PHA_AMT", interFaceParm.getDouble("PHA_AMT")); // 5
		// ҩƷ�ѷ������
		inParm.setData("PHA_NHI_AMT", interFaceParm.getDouble("PHA_NHI_AMT")); // 6
		// ҽ��ҩƷ��
		inParm.setData("EXM_AMT", interFaceParm.getDouble("EXM_AMT")); // 7 ����
		inParm.setData("EXM_NHI_AMT", interFaceParm.getDouble("EXM_NHI_AMT")); // 8
		// ҽ������
		inParm.setData("TREAT_AMT", interFaceParm.getDouble("TREAT_AMT")); // 9
		// ���Ʒѣ����Ʒѷ�����
		inParm.setData("TREAT_NHI_AMT", interFaceParm
				.getDouble("TREAT_NHI_AMT")); // 10 ҽ�����Ʒѣ����Ʒ��걨��
		inParm.setData("OP_AMT", interFaceParm.getDouble("OP_AMT")); // 11
		// �����ѷ������
		inParm.setData("OP_NHI_AMT", interFaceParm.getDouble("OP_NHI_AMT")); // 12�������걨���
		inParm.setData("BED_AMT", interFaceParm.getValue("BED_AMT")); // 13��λ��(��λ�ѷ������)
		inParm.setData("BED_NHI_AMT", interFaceParm.getDouble("BED_NHI_AMT")); // 14
		// ҽ����λ��
		inParm.setData("MATERIAL_AMT", interFaceParm.getDouble("MATERIAL_AMT")); // 15
		// ���Ϸ�
		inParm.setData("MATERIAL_NHI_AMT", interFaceParm
				.getDouble("MATERIAL_NHI_AMT")); // 16 ҽ�����Ϸ�
		inParm.setData("OTHER_AMT", interFaceParm.getDouble("OTHER_AMT")); // 17
																			// ��������
		inParm.setData("OTHER_NHI_AMT", interFaceParm
				.getDouble("OTHER_NHI_AMT")); // 18 ҽ����������
		inParm.setData("BLOODALL_AMT", interFaceParm.getDouble("BLOODALL_AMT")); // 19
		// ��ȫѪ�������
		inParm.setData("BLOODALL_NHI_AMT", interFaceParm
				.getDouble("BLOODALL_NHI_AMT")); // 20��ȫѪҽ�����
		inParm.setData("BLOOD_AMT", interFaceParm.getDouble("BLOOD_AMT")); // 21
		// �ɷ���Ѫ�������
		inParm.setData("BLOOD_NHI_AMT", interFaceParm
				.getDouble("BLOOD_NHI_AMT")); // 22 �ɷ���Ѫ�걨���
		inParm.setData("TOT_AMT", insParm.getDouble("FeeY")); // 23
		// �������---------���ܽ�
		inParm.setData("NHI_AMT", NHI_AMT); // 24
		// (�걨���)
		inParm.setData("START_STANDARD_AMTS", interFaceParm
				.getDouble("START_STANDARD_AMTS")); // 25 �𸶱�׼ʣ���
		inParm.setData("OTOT_PAY_AMT", interFaceParm.getDouble("OTOT_PAY_AMT")); // 26
		// ר�����֧��
		inParm.setData("START_STANDARD_AMT", interFaceParm
				.getDouble("START_STANDARD_AMT")); // 27 �𸶱�׼
		inParm.setData("ADD_AMT", interFaceParm.getDouble("ADD_AMT")); // 28
		// �������
		inParm.setData("OWN_AMT", interFaceParm.getDouble("OWN_AMT")); // 29
		// �Էѽ��
		inParm.setData("INS_STANDARD_AMT", interFaceParm
				.getDouble("INS_STANDARD_AMT")); // 30 �𸶱�׼���
		inParm.setData("TRANBLOOD_OWN_AMT", interFaceParm
				.getDouble("TRANBLOOD_OWN_AMT")); // 31 ��Ѫ�Ը����
		inParm.setData("APPLY_AMT", interFaceParm.getDouble("APPLY_AMT")); // 32
		// ����޶����Ͻ��
		inParm.setData("OTOT_AMT", interFaceParm.getDouble("OTOT_AMT")); // 33
		// ר�����֧���޶�
		inParm.setData("OINSTOT_AMT", interFaceParm.getDouble("OINSTOT_AMT")); // 34�����ʻ��������
		// System.out.println("***********************************************");
		// //System.out.println("INS_DATE:::" +
		// interFaceParm.getData("INS_DATE"));
		inParm.setData("INSAMT_FLG", 1); // 36�籣���ʱ�־INS_OPD��INSAMT_FLG���˱�־Ϊ0
		inParm.setData("OPT_USER", insParm.getValue("OPT_USER")); // 37
		inParm.setData("OPT_TERM", insParm.getValue("OPT_TERM")); // 38 39
		inParm.setData("UNACCOUNT_PAY_AMT", uaccountPayAmt); // 40���˻�֧�����
		inParm.setData("ACCOUNT_PAY_AMT", accountPayAmt); // 41 �˻�֧�����
		inParm.setData("ACCOUNT_LEFT_AMT", 0.00); // 42�˻����
		inParm.setData("REFUSE_AMT", 0.00); // 43//�ܸ����
		inParm.setData("REFUSE_ACCOUNT_PAY_AMT", 0.00); // 44
		inParm.setData("REFUSE_DESC", ""); // 45//�ܸ�ԭ��
		inParm.setData("CONFIRM_F_USER", ""); // 46������
		inParm.setData("CONFIRM_F_DATE", new TNull(String.class)); // 47����ʱ��
		inParm.setData("CONFIRM_S_USER", ""); // 48������
		inParm.setData("CONFIRM_S_DATE", new TNull(String.class)); // 49����ʱ��
		inParm.setData("SLOW_DATE", new TNull(String.class)); // 50��֮ʱ��
		inParm.setData("SLOW_PAY_DATE", new TNull(String.class)); // 51��֧֮��ʱ��
		inParm.setData("REFUSE_CODE", ""); // 52�ܸ�ԭ�����
		inParm.setData("REFUSE_OTOT_AMT", 0.00); // 53�ܸ���ר�����֧�����

		inParm.setData("INV_NO", insParm.getValue("PRINT_NO")); // 54 Ʊ�ݺ�
		if (insParm.getValue("RECP_TYPE").equals("REG")) {
			inParm.setData("MZ_DIAG", "�Һ�"); // 55
		} else {
			StringBuffer diagString = new StringBuffer();
			for (int i = 0; i < diagRecparm.getCount(); i++) {
				diagString.append(diagRecparm.getValue("ICD_CODE", i)
						+ diagRecparm.getValue("ICD_CHN_DESC", i));
			}
			inParm.setData("MZ_DIAG", diagString.toString()); // 55
		}

		// ������
		// ҽ����������
		inParm.setData("DRUG_DAYS", insParm.getInt("TAKE_DAYS")); // 56 ��ҩ����
		inParm.setData("INS_STD_AMT", interFaceParm.getDouble("INS_STD_AMT")); // 57����𸶱�׼
		inParm.setData("INS_CROWD_TYPE", insParm.getValue("CROWD_TYPE")); // 58��ݱ�1
		// ��ְ2
		// �Ǿ�
		// ҽ��������
		inParm.setData("INS_PAT_TYPE", insParm.getValue("INS_PAT_TYPE")); // 59��ݱ�1
		// ��ͨ2
		// ����
		inParm.setData("RECP_TYPE", insParm.getValue("RECP_TYPE")); // 60�Һ��շ����
		inParm.setData("OWEN_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("OWEN_PAY_SCALE"))); // 61�Ը�����
		inParm.setData("REDUCE_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("REDUCE_PAY_SCALE"))); // 62��������
		inParm.setData("REAL_OWEN_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("REAL_OWEN_PAY_SCALE"))); // 63ʵ���Ը�����
		inParm.setData("SALVA_PAY_SCALE", checkOutIsNull(interFaceParm
				.getValue("SALVA_PAY_SCALE"))); // 64ҽ�ƾ����Ը�����
		inParm.setData("BASEMED_BALANCE", checkOutIsNull(interFaceParm
				.getValue("BASEMED_BALANCE"))); // 65����ҽ��ʣ���
		inParm.setData("INS_BALANCE", checkOutIsNull(interFaceParm
				.getValue("INS_BALANCE"))); // 66 ҽ�ƾ���ʣ���
		inParm.setData("ISSUE", interFaceParm.getValue("ISSUE")); // 67�ں�
		inParm.setData("BCSSQF_STANDRD_AMT", checkOutIsNull(interFaceParm
				.getValue("BCSSQF_STANDRD_AMT"))); // 68����ʵ���𸶱�׼���
		inParm.setData("PERCOPAYMENT_RATE_AMT", checkOutIsNull(interFaceParm
				.getValue("PERCOPAYMENT_RATE_AMT"))); // 69ҽ�ƾ������˰������������
		inParm.setData("INS_HIGHLIMIT_AMT", checkOutIsNull(interFaceParm
				.getValue("INS_HIGHLIMIT_AMT"))); // 70ҽ�ƾ�������޶����Ͻ��
		inParm.setData("TOTAL_AGENT_AMT", interFaceParm
				.getDouble("TOTAL_AGENT_AMT")); // 71����ҽ���籣������TOTAL_AGENT_AMT
		inParm.setData("FLG_AGENT_AMT", checkOutIsNull(interFaceParm
				.getValue("FLG_AGENT_AMT"))); // 72���������걨���(��������)FLG_AGENT_AMT
		inParm.setData("APPLY_AMT_R", 0.00); // 73ͳ�����ܸ����
		inParm.setData("FLG_AGENT_AMT_R", 0.00); // 74�����ܸ����
		inParm.setData("REFUSE_DATE", new TNull(String.class)); // 75�ܸ�ʱ��

		inParm.setData("REAL_PAY_LEVEL", checkOutIsNull(interFaceParm
				.getValue("BCSSQF_STANDRD_AMT"))); // 76����ʵ���𸶱�׼
		inParm.setData("VIOLATION_REASON_CODE", ""); // 77ҽ����֧��Ϣ
		if (insParm.getInt("INS_TYPE") == 1) {
			inParm.setData("INS_DATE", interFaceParm.getData("INS_DATE")); // 35ҽ������ʱ��-���Ľ���ʱ��
			inParm.setData("ARMY_AI_AMT", interFaceParm.getDouble("AGENT_AMT")); // 78���в������(��������)
		} else if (insParm.getInt("INS_TYPE") == 2) {
			inParm.setData("INS_DATE", interFaceParm.getData("INSBRANCH_TIME")); // 35ҽ������ʱ��-���Ľ���ʱ��
			inParm.setData("ARMY_AI_AMT", interFaceParm
					.getDouble("ARMY_AI_AMT")); // 78���в������(��������)
		} else if (insParm.getInt("INS_TYPE") == 3) {
			inParm.setData("INS_DATE", interFaceParm.getData("INSBRANCH_TIME")); // 35ҽ������ʱ��-���Ľ���ʱ��
			inParm.setData("ARMY_AI_AMT", interFaceParm.getDouble("AGENT_AMT")); // 78���в������(��������)
		}
		inParm.setData("SERVANT_AMT", checkOutIsNull(interFaceParm
				.getValue("SERVANT_AMT"))); // 79����Ա�������(��������)
		inParm.setData("INS_PAY_AMT", interFaceParm.getDouble("INS_PAY_AMT")); // 80ҽ��֧�����(INS_PAY_AMT)
		inParm.setData("UNREIM_AMT", interFaceParm.getDouble("UNREIM_AMT")); // 81UNREIM_AMT
		// ����δ�������REIM_TYPE
		inParm.setData("REIM_TYPE", interFaceParm.getInt("REIM_TYPE")); // 82������������0
		// ��������1
		// �����渶
		// inParm.setData("RECEIPT_NO", insParm.getValue("RECEIPT_NO")); //83�վݺ�
		// ��REG/OPB
		result = INSOpdTJTool.getInstance().updateINSopdSettle(inParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		return result;
	}

	/**
	 * У���Ƿ�Ϊ��
	 * 
	 * @param name
	 *            String
	 * @return double
	 */
	private double checkOutIsNull(String name) {
		return null == name || name.trim().length() <= 0 ? 0.00 : Double
				.parseDouble(name);
	}

	/**
	 * ��ӷ��÷ָ����ݣ����INS_OPD_ORDER ��ϸ
	 * 
	 * @param parm
	 *            TParm
	 * @param REG_PARM
	 *            TParm
	 * @param interfaceParm
	 *            TParm
	 * @param maxSeq
	 *            int
	 * @param j
	 *            int
	 * @param printNo
	 *            String
	 * @return TParm
	 */
	private TParm insOrderTemp(TParm parm, TParm REG_PARM, TParm interfaceParm,
			int maxSeq, int j, String printNo) {
		TParm result = null;
		TParm insOrderParm = new TParm();
		// ִ�����INS_OPD_ORDER ������
		insOrderParm.setData("REGION_CODE", parm.getValue("NEW_REGION_CODE"));
		insOrderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		// System.out.println("maxSeq::::" + maxSeq);
		insOrderParm.setData("SEQ_NO", maxSeq + j + 1); // ���
		insOrderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // �ʸ�ȷ�Ϻ�
		insOrderParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // �Ƽ�����
		// ҽ������----������
		getParmValue(parm, REG_PARM, j, insOrderParm, true);
		// ҽ������
		insOrderParm.setData("ORDER_CODE", REG_PARM.getValue("ORDER_CODE", j));
		// ҽ������
		insOrderParm.setData("ORDER_DESC", REG_PARM.getValue("ORDER_DESC", j));
		insOrderParm.setData("OWN_RATE", interfaceParm.getValue("OWN_RATE")); // �Ը�����
		insOrderParm.setData("DOSE_CODE", REG_PARM.getValue("DOSE_CODE", j)); // -----���ʹ���
		insOrderParm.setData("STANDARD", REG_PARM.getValue("SPECIFICATION", j)); // �𸶱�׼---????
		insOrderParm.setData("PRICE", REG_PARM.getDouble("OWN_PRICE", j)); // ����
		insOrderParm.setData("QTY", REG_PARM.getDouble("DOSAGE_QTY", j)); // ����
		// insOrderParm.setData("TOTAL_AMT", REG_PARM.getDouble("DOSAGE_QTY", j)
		// * REG_PARM.getDouble("OWN_PRICE", j)); // �ܽ��
		insOrderParm.setData("TOTAL_AMT", REG_PARM.getDouble("AR_AMT", j)); // �ܽ��
		insOrderParm.setData("TOTAL_NHI_AMT", interfaceParm
				.getDouble("NHI_AMT")); // ҽ���ܽ��-----���������÷ָ���ε��걨���
		// REG_PARM.getDouble("OWN_PRICE", j) * REG_PARM.getDouble("QTY", j)
		insOrderParm.setData("OWN_AMT", interfaceParm.getDouble("OWN_AMT")); // �Էѽ��-------�������������÷ָ���ε�ȫ�Էѽ��
		insOrderParm.setData("ADDPAY_AMT", interfaceParm
				.getDouble("ADDPAY_AMT")); // ���÷ָ���ε��������
		insOrderParm.setData("REFUSE_AMT", 0.00); // �ܸ����-----����������
		insOrderParm.setData("REFUSE_REASON_CODE", new TNull(String.class)); // �ܸ�ԭ�����
		insOrderParm.setData("REFUSE_REASON_NOTE", new TNull(String.class)); // �ܸ�ԭ��ע��
		insOrderParm.setData("OP_FLG", "N"); // ����ע�ǣ�Y��������N����������:�Һ�
		insOrderParm.setData("CARRY_FLG", "N"); // ��Ժ��ҩע�ǣ�Y����ҩ��N������ҩ��
		getPhaAdd(parm, insOrderParm, REG_PARM, j, true);
		insOrderParm.setData("ADDPAY_FLG",null != interfaceParm.getValue("")
								&& interfaceParm.getValue("ADDPAY_FLG").equals(
										"1") ? "Y" : "N"); // �ۼ�����ע�ǣ�Y���ۼ�������N�����ۼ�������
		insOrderParm.setData("NHI_ORD_CLASS_CODE", interfaceParm
				.getValue("NHI_ORD_CLASS_CODE"));
		insOrderParm.setData("INSAMT_FLG", 0); // ���˱�־Ϊ0
		insOrderParm.setData("RX_SEQNO", REG_PARM.getValue("RX_NO", j)); // ������
		insOrderParm.setData("OPB_SEQNO", REG_PARM.getValue("SEQ_NO", j)); // �����
		insOrderParm.setData("TAKE_QTY", REG_PARM.getDouble("DOSAGE_QTY", j)); // ��ȡ����
		insOrderParm.setData("ROUTE", REG_PARM.getValue("YF",j)); // �÷�
		insOrderParm.setData("HYGIENE_TRADE_CODE",REG_PARM.getValue("PZWH", j));
		insOrderParm.setData("INS_CROWD_TYPE", parm.getValue("CROWD_TYPE"));
		insOrderParm.setData("INS_PAT_TYPE", parm.getValue("INS_PAT_TYPE"));
		insOrderParm.setData("ORIGSEQ_NO", new TNull(String.class));
		insOrderParm.setData("TAKE_DAYS", REG_PARM.getValue("TAKE_DAYS", j));
		insOrderParm.setData("INV_NO", printNo); // ��������ʱ��---???ҽ�����վݺ�
		insOrderParm.setData("OPT_USER", parm.getValue("OPT_USER")); // ����Ա
		insOrderParm.setData("OPT_TERM", parm.getValue("OPT_TERM")); // IP
		insOrderParm.setData("RECP_TYPE", parm.getValue("RECP_TYPE")); // ����
		// insOrderParm.setData("AGENT_AMT",
		// interfaceParm.getDouble("AGENT_AMT")); // ����
		insOrderParm.setData("SPECIAL_CASE_DESC", REG_PARM.getValue("DR_NOTE",
				j));
		// System.out.println("��ӷ��÷ָ�����insOrderParm::::" + insOrderParm);
		// ��REG/OPB
		result = INSOpdOrderTJTool.getInstance()
				.insertINSOpdOrder(insOrderParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		return result;
	}

    /**
     * ���ýӿڷ������ִ���
     *
     * @param parm
     *            TParm
     * @return boolean
     */
    public boolean getErrParm(TParm parm) {
        // System.out.println("���ýӿڷ���:::"+parm);
        if (null == parm) {
            parm = new TParm();
            parm.setErr( -1, "���ýӿ�ʧ��");
            return false;
        }
        if (parm.getErrCode()<0) {
        	return false;
		}
        if (null != parm.getValue("PROGRAM_STATE")
            && (parm.getInt("PROGRAM_STATE") == 0 || parm
                .getInt("PROGRAM_STATE") == 1 || parm
                .getInt("PROGRAM_STATE") == 2) ) {
            return true;

		} else {
			parm.setErr(-1, parm.getValue("PROGRAM_MESSAGE"));
			return false;
		}

	}

	/**
	 * ��ְ��ͨ �ʻ�֧��ȷ�Ͻ��׷���:����DataDown_rs����R����
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_rs_R(TParm insParm, TParm interFaceParm) {
		// �����˻�ʵ��֧�����=�������ʻ��������͡������ʻ����ıȽϣ�
		// ����������ʻ������� ���� �������ʻ��� �򡰸����ʻ�ʵ��֧����ֵΪ�������ʻ���
		// �������ʻ�֧������ֵΪ ���������ʻ������� ���������ʻ����� �� ���Էѽ�
		// ��֮ �������ʻ�ʵ��֧���� Ϊֵ �������ʻ��������������ʻ�֧������ֵΪ���Էѽ���
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"TOT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		TParm parm = new TParm();
		// parm.addData("CASE_NO", insParm.getValue("CASE_NO"));// 1����� A �����еĲ���
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1����˳���:L��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 ҽԺ����
		parm.addData("UNACCOUNT_PAY_AMT", StringTool.round(uaccountPayAmt, 2)); // 3���˻�֧�����
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); // 4
																				// �����ʻ�ʵ��֧�����
		parm.addData("INSCARD_NO", insParm.getValue("PERSONAL_NO")); // 5 ���˱���
		// A��������
		parm.addData("INSCARD_PASSWORD", insParm.getValue("PASSWORD")); // 6 ����
		parm.addData("OTOT_AMT", StringTool.round(interFaceParm
				.getDouble("OTOT_AMT"), 2)); // 7ר�����֧�����
		parm.addData("AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("AGENT_AMT"), 2)); // 8
		// �������
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_rs", "R", parm);
		// System.out.println("�ʻ�֧��ȷ�Ͻ���accPaymentSureParm::" + result);
		// result.setData("PROGRAM_STATE", "Y");// ����ִ��״̬PROEXE_FLG)
		// result.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		// result.setData("ACCOUNT_BALANCE_AMT", interFaceParm
		// .getValue("ACCOUNT_BALANCE_AMT"));// �ʻ����(ACCOUNT_BALANCE_AMT)
		// result.setData("OTOT_AMT", interFaceParm.getValue("OTOT_AMT"));//
		// ר�����֧���޶�(OTOT_AMT)
		// result.setData("GRANT_AMT", interFaceParm.getValue("GRANT_AMT"));//
		// ���ʱ�־

		return result;
	}

	/**
	 * ��ְ��ͨ����ȷ�Ϸ��ز���:����DataDown_sp���������ȷ�Ͻ��ף�R������
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_R(TParm insParm, TParm interFaceParm) {
		TParm parm = new TParm();
		// parm.addData("CASE_NO", insParm.getValue("CASE_NO"));// 1����� A �����еĲ���
		// �����˻�ʵ��֧�����=�������ʻ��������͡������ʻ����ıȽϣ�
		// ����������ʻ������� ���� �������ʻ��� �򡰸����ʻ�ʵ��֧����ֵΪ�������ʻ���
		// �������ʻ�֧������ֵΪ ���������ʻ������� ���������ʻ����� �� ���Էѽ�
		// ��֮ �������ʻ�ʵ��֧���� Ϊֵ �������ʻ��������������ʻ�֧������ֵΪ���Էѽ���
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"TOT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 2 ����˳���
		// L��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 3 ҽԺ����
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); //
		// �ʻ�֧�����
		parm.addData("INS_PAY_AMT", StringTool.round(interFaceParm
				.getDouble("INS_PAY_AMT"), 2)); // 5
		// ҽ��֧�����
		parm.addData("CASH_AMT", StringTool.round(uaccountPayAmt
				+ interFaceParm.getDouble("UNREIM_AMT"), 2)); // 6
																// �ֽ�֧�����UNREIM_AMT
		parm.addData("UNREIM_AMT", StringTool.round(interFaceParm
				.getDouble("UNREIM_AMT"), 2)); // 7
		// ����δ�������
		parm.addData("PARM_COUNT", 6);
		// System.out
		// .println("��ְ��ͨ����ȷ��::cityStaffCommonBalanceSureParm:���" + parm);
		TParm result = commInterFace("DataDown_sp", "R", parm);
		// System.out
		// .println("��ְ��ͨ����ȷ��::cityStaffCommonBalanceSureParm:" + result);
		// result.setData("PROGRAM_STATE", "Y");// ����ִ��״̬PROEXE_FLG)
		// result.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		// result.setData("R_FLG", "Y");// ���ʱ�־
		return result;

	}

	/**
	 * ��ְ��ͨ����ȷ�Ϸ��ز���:����DataDown_sp���������ȷ�Ͻ��ף�R������ �����Զ����˲���
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_R(TParm insParm) {
		TParm parm = new TParm();
		// parm.addData("CASE_NO", insParm.getValue("CASE_NO"));// 1����� A �����еĲ���
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 2 ����˳���
		// L��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 3 ҽԺ����
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(insParm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 4
		// �ʻ�֧�����-----�����ʻ��������---???????????
		parm.addData("INS_PAY_AMT", StringTool.round(insParm
				.getDouble("INS_PAY_AMT"), 2)); // 5
		// ҽ��֧�����
		parm.addData("CASH_AMT", StringTool.round(insParm
				.getDouble("UACCOUNT_PAY_AMT")
				+ insParm.getDouble("UNREIM_AMT"), 2)); // 6 �ֽ�֧�����
		parm.addData("UNREIM_AMT", StringTool.round(insParm
				.getDouble("UNREIM_AMT"), 2)); // 7
		// ����δ�������
		parm.addData("PARM_COUNT", 6);
		// System.out.println("��ְ��ͨ����ȷ��:�����Զ��������" + parm);
		TParm result = commInterFace("DataDown_sp", "R", parm);
		// System.out.println("��ְ��ͨ����ȷ��::�����Զ�����:" + result);
		// result.setData("PROGRAM_STATE", "Y");// ����ִ��״̬PROEXE_FLG)
		// result.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		// result.setData("R_FLG", "Y");// ���ʱ�־
		return result;
	}

	/**
	 * ��ְ����ˢ�����ز�����DataDown_mts����ˢ�����ף�E��
	 * 
	 * @param insParm
	 *            TParm A ��������
	 * @return TParm
	 */
	public TParm DataDown_mts_E(TParm insParm) {
		TParm parm = new TParm();
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // ���˱���
		// A��������
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // ����
		parm.addData("PAY_KIND", insParm.getValue("PAY_KIND")); // ֧�����12�����������ⲡ13
		// �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE"));
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // ҽԺ(ҩ��)����
		parm.addData("CHECK_CODES", insParm.getValue("CHECK_CODES")); // ˢ����֤��
		parm.addData("PARM_COUNT", 6);
		// System.out.println("��ְ����ˢ����Σ�����" + parm);
		TParm result = commInterFace("DataDown_mts", "E", parm);
		// System.out.println("��ְ����ˢ������::result::" + result);

		return result;
	}

	/**
	 * ��ְ���ط��ز�����ͳ��֧��ȷ�Ͻ��ף�����DataDown_mts, ����ͳ��֧��ȷ�Ͻ��ף�G��
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_G(TParm insParm, TParm interFaceParm) {
		TParm parm = new TParm();

		// �����˻�ʵ��֧�����=�������ʻ��������͡������ʻ����ıȽϣ�
		// ����������ʻ������� ���� �������ʻ��� �򡰸����ʻ�ʵ��֧����ֵΪ�������ʻ���
		// �������ʻ�֧������ֵΪ ���������ʻ������� ���������ʻ����� �� ���Էѽ�
		// ��֮ �������ʻ�ʵ��֧���� Ϊֵ �������ʻ��������������ʻ�֧������ֵΪ���Էѽ���
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		TParm accountParm = getAccountAmt(interFaceParm, opbReadCardParm,
				"PERSON_ACCOUNT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1
		// ���ؾ�ҽ˳���E��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2
		// ҽԺ(��ҩ��)����
		parm.addData("TOTAL_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("TOTAL_AGENT_AMT"), 2)); // 3 ����ҽ���籣������
		parm.addData("FLG_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("FLG_AGENT_AMT"), 2)); // 4
		// ҽ�ƾ����籣������
		// ���˱���
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 5 ����
		// A��������
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 6����
		parm.addData("UNACCOUNT_PAY_AMT", StringTool.round(uaccountPayAmt, 2)); // 7
		// ���˻�֧�����
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); // 8
																				// �����ʻ�ʵ��֧�����
		parm.addData("AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("ARMY_AI_AMT"), 2)); // 9
		// �������
		parm.addData("AGENT_AMT2", StringTool.round(interFaceParm
				.getDouble("SERVANT_AMT"), 2)); // 10
		// �������2
		parm.addData("PARM_COUNT", 10);
		// System.out.println("ͳ��֧��ȷ�Ͻ��ף�����DataDown_mts, ����ͳ��֧��ȷ�Ͻ��ף�G�����:" +
		// parm);
		TParm result = commInterFace("DataDown_mts", "G", parm);
		// System.out.println("ͳ��֧��ȷ�Ͻ��ף�����DataDown_mts, ����ͳ��֧��ȷ�Ͻ��ף�G��:" +
		// result);
		return result;
	}

	/**
	 * ��ְ���ؽ���ȷ�Ϸ��ز���:����DataDown_mts���������ȷ�Ͻ��ף�I������
	 * 
	 * @param insParm
	 *            parm A ��������
	 * @param interFaceParm
	 *            parm ���ý������
	 * @param tempParm
	 *            parm ��ְ����ˢ������
	 * @return TParm
	 */
	public TParm DataDown_mts_I(TParm insParm, TParm interFaceParm,
			TParm tempParm) {
		TParm parm = new TParm();
		TParm accountParm = getAccountAmt(interFaceParm, tempParm,
				"PERSON_ACCOUNT_AMT");
		double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		parm.addData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO")); // 1 ����˳���
		// E
		// ��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 ҽԺ����
		parm.addData("PAT_TYPE", tempParm.getValue("PAT_TYPE")); // 3
		// ��Ա���11��ְ21����51����ǰ�Ϲ���
		parm.addData("PAY_KIND", tempParm.getValue("PAY_KIND")); // ֧�����12�����������ⲡ13
		// �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 4������
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(accountPayAmt, 2)); // 5�ʻ�֧�����
		parm.addData("INS_PAY_AMT", StringTool.round(interFaceParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// ҽ��֧�����
		parm.addData("CASH_AMT", StringTool.round(uaccountPayAmt
				+ interFaceParm.getDouble("UNREIM_AMT"), 2)); // 7// �ֽ�֧�����
		parm.addData("UNREIM_AMT", StringTool.round(interFaceParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// ����δ�������
		parm.addData("PARM_COUNT", 9);
		// System.out.println("��ְ���غ���DataDown_mts���������ȷ�Ͻ��ף�I�����:" + parm);
		TParm result = commInterFace("DataDown_mts", "I", parm);
		// System.out.println("��ְ���غ���DataDown_mts���������ȷ�Ͻ��ף�I������:" + result);
		return result;
	}

	/**
	 * ��ø����˻�֧����� ����˻����
	 * 
	 * @param interFaceParm
	 *            TParm
	 * @param tempParm
	 *            TParm
	 * @param amtName
	 *            String
	 * @return TParm
	 */
	public TParm getAccountAmt(TParm interFaceParm, TParm tempParm,
			String amtName) {
		// �����˻�ʵ��֧�����=�������ʻ��������͡������ʻ����ıȽϣ�
		// ����������ʻ������� ���� �������ʻ��� �򡰸����ʻ�ʵ��֧����ֵΪ�������ʻ���
		// �������ʻ�֧������ֵΪ ���������ʻ������� ���������ʻ����� �� ���Էѽ�
		// ��֮ �������ʻ�ʵ��֧���� Ϊֵ �������ʻ��������������ʻ�֧������ֵΪ���Էѽ���
		double accountPayAmt = 0.00;
		double uaccountPayAmt = 0.00;
		if (interFaceParm.getDouble("OINSTOT_AMT") > tempParm
				.getDouble(amtName)) {
			accountPayAmt = tempParm.getDouble(amtName); // �����ʻ����
			// �����ʻ��������-�ʻ����+�Էѽ��
			uaccountPayAmt = interFaceParm.getDouble("OINSTOT_AMT")
					- accountPayAmt + interFaceParm.getDouble("OWN_AMT");
		} else {
			accountPayAmt = interFaceParm.getDouble("OINSTOT_AMT"); // �����ʻ��������
			uaccountPayAmt = interFaceParm.getDouble("OWN_AMT"); // �Էѽ��
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", StringTool.round(accountPayAmt, 2));
		parm.setData("UACCOUNT_AMT", StringTool.round(uaccountPayAmt, 2));
		return parm;
	}

	/**
	 * ��ְ���ؽ���ȷ�Ϸ��ز���:����DataDown_mts���������ȷ�Ͻ��ף�I������ �������ʹ��
	 * 
	 * @param insParm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_I(TParm insParm, TParm mzConfirmParm) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1 ����˳��� E
		// ��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 ҽԺ����
		parm.addData("PAT_TYPE", mzConfirmParm.getValue("PAT_TYPE")); // 3
		// ��Ա���11��ְ21����51����ǰ�Ϲ���
		parm.addData("PAY_KIND", mzConfirmParm.getValue("PAY_KIND")); // ֧�����12�����������ⲡ13
		// �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		parm.addData("DISEASE_CODE", mzConfirmParm.getValue("DISEASE_CODE")); // 4
		parm.addData("ACCOUNT_PAY_AMT", StringTool.round(insParm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 5�ʻ�֧�����-------?????
		parm.addData("INS_PAY_AMT", StringTool.round(insParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// ҽ��֧�����
		parm.addData("CASH_AMT", StringTool.round(insParm
				.getDouble("UACCOUNT_PAY_AMT")
				+ insParm.getDouble("UNREIM_AMT"), 2)); // 7// �ֽ�֧�����
		parm.addData("UNREIM_AMT", StringTool.round(insParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// ����δ�������
		parm.addData("PARM_COUNT", 9);
		// System.out.println("��ְ���غ���DataDown_mts���������ȷ�Ͻ��ף�I�����:" + parm);
		TParm result = commInterFace("DataDown_mts", "I", parm);
		// System.out.println("��ְ���غ���DataDown_mts���������ȷ�Ͻ��ף�I������:" + result);
		return result;
	}

	/**
	 * �Ǿ�����ˢ������DataDown_cmts, ����ˢ�����ף�E��,�õ�������Ϣ
	 * 
	 * @param insParm
	 *            TParm A ��������
	 * @return TParm
	 */
	public TParm DataDown_cmts_E(TParm insParm) {
		TParm parm = new TParm();
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 1 ���˱���
		// A��������
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 2 ����
		parm.addData("PAY_KIND", insParm.getValue("PAY_KIND")); // 3
		// ֧�����:11���ҩ��21סԺ//֧�����12
		// �����������ⲡ13 �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // ����������?
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 5
		// ҽԺ(ҩ��)����
		parm.addData("COMU_NO", null); // 6 ��������
		parm.addData("CHECK_CODES", insParm.getValue("CHECK_CODES")); // 7 ˢ����֤��
		parm.addData("PARM_COUNT", 7);
		TParm result = commInterFace("DataDown_cmts", "E", parm);
		return result;
	}

	/**
	 * �Ǿ�����:ͳ��֧��ȷ�Ͻ���:����DataDown_cmts, ����ͳ��֧��ȷ�Ͻ��ף�G��
	 * 
	 * @param insParm
	 *            TParm
	 * @param interFaceParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_G(TParm insParm, TParm interFaceParm) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1
		// ���ؾ�ҽ˳���E��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2
		// ҽԺ(��ҩ��)����
		parm.addData("TOTAL_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("TOTAL_AGENT_AMT"), 2)); // 3 ����ҽ���籣������
		parm.addData("FLG_AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("FLG_AGENT_AMT"), 2)); // 4
		// ҽ�ƾ����籣������
		// ���˱���-
		parm.addData("CARD_NO", insParm.getValue("PERSONAL_NO")); // 5����
		// A��������
		parm.addData("PASSWORD", insParm.getValue("PASSWORD")); // 6 ����
		parm.addData("AGENT_AMT", StringTool.round(interFaceParm
				.getDouble("AGENT_AMT"), 2)); // 7
		// �������
		parm.addData("PARM_COUNT", 7);
		TParm result = commInterFace("DataDown_cmts", "G", parm);
		return result;

	}

	/**
	 * �Ǿ����ؽ���ȷ�Ϸ��ز���:����DataDown_cmts, ���ؽ���ȷ�Ͻ��ף�I��
	 * 
	 * @param insParm
	 *            parm A ��������
	 * @param interFaceParm
	 *            parm ���ý������
	 * @param tempParm
	 *            parm �Ǿ�����ˢ������
	 * @return TParm
	 */
	public TParm DataDown_cmts_I(TParm insParm, TParm interFaceParm,
			TParm tempParm) {
		// double totAmt = insParm.getDouble("TOT_AMT");
		// // ҽ��֧�����
		// double accountAmt = interFaceParm.getDouble("TOTAL_AGENT_AMT")
		// + interFaceParm.getDouble("FLG_AGENT_AMT")
		// // + interFaceParm.getDouble("AGENT_AMT");
		// TParm accountParm = getAccountAmt(interFaceParm, tempParm,
		// "PERSON_ACCOUNT_AMT");
		// double accountPayAmt = accountParm.getDouble("ACCOUNT_AMT");
		// double uaccountPayAmt = accountParm.getDouble("UACCOUNT_AMT");
		TParm parm = new TParm();
		// System.out.println("accountPayAmt::::::"+accountParm);
		parm.addData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO")); // 1 ����˳���
		// E
		// ��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 ҽԺ����
		parm.addData("PAT_TYPE", tempParm.getValue("PAT_TYPE")); // 3
		// ��Ա���11��ְ21����51����ǰ�Ϲ���
		parm.addData("PAY_KIND", tempParm.getValue("PAY_KIND")); // 4
		// ֧�����12�����������ⲡ13
		// �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		parm.addData("DISEASE_CODE", insParm.getValue("DISEASE_CODE")); // 5
		// result.setData("ACCOUNT_PAY_AMT",
		// insParm.getDouble("TOT_ATM"));//�ʻ�֧�����-------?????
		//
		parm.addData("INS_PAY_AMT", StringTool.round(interFaceParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// ҽ��֧�����
		// �ֽ�֧��

		parm.addData("CASH_AMT", StringTool.round(insParm.getDouble("FeeY")
				- interFaceParm.getDouble("INS_PAY_AMT"), 2)); // 7
		// �ֽ�֧�����--------???????
		parm.addData("UNREIM_AMT", StringTool.round(interFaceParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// ����δ�������
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_cmts", "I", parm);
		return result;
	}

	/**
	 * �Ǿ����ؽ���ȷ�Ϸ��ز���:����DataDown_cmts, ���ؽ���ȷ�Ͻ��ף�I�� �������ʹ��
	 * 
	 * @param insParm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_I(TParm insParm, TParm mzConfirmParm) {
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 1 ����˳��� E
		// ��������
		parm.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE")); // 2 ҽԺ����
		parm.addData("PAT_TYPE", mzConfirmParm.getValue("CTZ_CODE")); // 3
		// ��Ա���11��ְ21����51����ǰ�Ϲ���
		parm.addData("PAY_KIND", mzConfirmParm.getValue("PAY_KIND")); // 4
		// ֧�����12�����������ⲡ13
		// �����������ⲡ
		// �������:1��͸��2����ֲ������3��֢�Ż���4 ����5���Ĳ�6����Ǵ�7����8ƫ̱9���10 �����ϰ���ƶѪ11
		// ����ѪС����������21Ѫ�Ѳ�22����ֲ������������
		parm.addData("DISEASE_CODE", mzConfirmParm.getValue("DISEASE_CODE")); // 5
		// result.setData("ACCOUNT_PAY_AMT",
		// insParm.getDouble("TOT_ATM"));//�ʻ�֧�����-------?????
		//
		parm.addData("INS_PAY_AMT", StringTool.round(insParm
				.getDouble("INS_PAY_AMT"), 2)); // 6
		// ҽ��֧�����
		parm.addData("CASH_AMT", StringTool.round(insParm
				.getDouble("UACCOUNT_PAY_AMT")
				+ insParm.getDouble("UNREIM_AMT"), 2)); // 7
		// �ֽ�֧�����--------???????
		parm.addData("UNREIM_AMT", StringTool.round(insParm
				.getDouble("UNREIM_AMT"), 2)); // 8
		// ����δ�������
		parm.addData("PARM_COUNT", 8);
		TParm result = commInterFace("DataDown_cmts", "I", parm);
		return result;
	}

	/**
	 * ȡ�����׹�������
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm cancelDealClear(String confirmNO, String regionCode,
			String type) {
		TParm result = new TParm();
		result.addData("CONFIRM_NO", confirmNO); // ����˳��� E
		// ��
		// L��������
		// result.addData("CASE_NO", insParm.getValue("CASE_NO"));// ����� A
		// �����еĲ���
		result.addData("HOSP_NHI_NO", regionCode); // ҽԺ����
		// ���Ϊ�����˷ѷ��ó�������ô�������͡�31��
		return result;
	}

	/**
	 * ��ְ��ͨȡ�����׷���:���� DataDown_sp (S)����
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_S(String confirmNO, String regionCode, String type) {
		TParm parm = cancelDealClear(confirmNO, regionCode, type);
		parm.addData("DIS_GENRE", type); // �����������Ϊ�����������ó�������ô�������͡�30����
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_sp", "S", parm);
		return result;
	}

	/**
	 * �Զ�����ʹ�� ��ְ��ͨȡ�����׷���:���� DataDown_sp (S)����
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_S(TParm insParm, String type) {
		TParm parm = cancelDealClear(insParm.getValue("CONFIRM_NO"), insParm
				.getValue("NHI_REGION_CODE"), "");
		parm.addData("DIS_GENRE", type); // �����������Ϊ�����������ó�������ô�������͡�30����
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_sp", "S", parm);
		return result;
	}

	/**
	 * �Ǿ�����ȡ�����׷���:����DataDown_cmts (J)����
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_cmts_J(String confirmNO, String regionCode,
			String type) {
		TParm parm = cancelDealClear(confirmNO, regionCode, type);
		parm.addData("CANCEL_TYPE", type); // �����������Ϊ�����������ó�������ô�������͡�30����
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_cmts", "J", parm);
		return result;
	}

	/**
	 * �Ǿ�����ȡ�����׷���:����DataDown_cmts (J)���� �������
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_cmts_J(TParm insParm, String type) {
		TParm parm = cancelDealClear(insParm.getValue("CONFIRM_NO"), insParm
				.getValue("REGION_CODE"), type);
		parm.addData("CANCEL_TYPE", type); // �����������Ϊ�����������ó�������ô�������͡�30����
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_cmts", "J", parm);
		return result;
	}

	/**
	 * ��ְ����ȡ�����׷���:����DataDown_mts (J)����
	 * 
	 * @param confirmNO
	 *            String
	 * @param regionCode
	 *            String
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_mts_J(String confirmNO, String regionCode, String type) {
		TParm parm = cancelDealClear(confirmNO, regionCode, type);
		parm.addData("CANCEL_TYPE", type); // �����������Ϊ�����������ó�������ô�������͡�30����
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_mts", "J", parm);
		return result;
	}

	/**
	 * ��ְ����ȡ�����׷���:����DataDown_mts (J)���� ����ʹ��
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_mts_J(TParm insParm, String type) {
		TParm parm = cancelDealClear(insParm.getValue("CONFIRM_NO"), insParm
				.getValue("REGION_CODE"), type);
		parm.addData("CANCEL_TYPE", type); // �����������Ϊ�����������ó�������ô�������͡�30����
		parm.addData("PARM_COUNT", 3);
		TParm result = commInterFace("DataDown_mts", "J", parm);
		return result;
	}

	/**
	 * ���������������
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm specialCase(TParm insParm) {
		// ��ѯ�Ƿ���Դ����������:�Һ�ҽ�����ز�������ʹ��
		TParm secipalParm = new TParm();
		// secipalParm.setData("REGION_CODE", insParm.getValue("REGION_CODE"));
		secipalParm.setData("CASE_NO", insParm.getValue("CASE_NO"));
		secipalParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO"));
		TParm result = INSMZConfirmTool.getInstance()
				.selectSpcMemo(secipalParm);
		if (result.getErrCode() < 0) {
			err("�������" + result.getNames() + ":" + result.getErrText());
			return result;
		}
		if (result.getCount() <= 0
				|| result.getValue("SPC_MEMO", 0).length() <= 0) {
			result.setData("MESSAGE", "�������������");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ��ͨ�����������������DataDown_sp ������������ϴ���Y��
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_Y(TParm insParm) {
		TParm parm = specialCase(insParm);
		// �������������
		if (null != parm.getValue("MESSAGE")
				&& parm.getValue("MESSAGE").length() > 0) {
			return parm;
		}
		TParm inparm = new TParm();
		inparm.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO", 0)); // 1����˳���
		// E
		// ��
		// L��������
		inparm.addData("SPC_MEMO", parm.getValue("SPC_MEMO", 0)); // 2�������˵��INS_MZ_CONFIRM��SPC_MEMO
		inparm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_sp", "Y", parm);
		return result;
	}

	/**
	 * �Ǿ����������������������DataDown_cmts��������������ϴ����ף�H��
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_H(TParm insParm) {
		TParm parm = specialCase(insParm);
		if (null != parm.getValue("MESSAGE")
				&& parm.getValue("MESSAGE").length() > 0) {
			return parm;
		}
		TParm inparm = new TParm();
		inparm.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO", 0)); // 1����˳���
		// E
		// ��
		// L��������
		inparm.addData("SPECIAL_REMARK", parm.getValue("SPC_MEMO", 0)); // 2�������˵��INS_MZ_CONFIRM��SPC_MEMO
		parm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_cmts", "H", inparm);
		return result;
	}

	/**
	 * ��ְ���������������������DataDown_sp��������������ϴ����ף�H��
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_H(TParm insParm) {
		TParm parm = specialCase(insParm);
		if (null != parm.getValue("MESSAGE")
				&& parm.getValue("MESSAGE").length() > 0) {
			return parm;
		}
		TParm inparm = new TParm();
		inparm.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO", 0)); // 1����˳���
		// E
		// ��
		// L��������
		inparm.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE", 0)); // 2�������˵��INS_MZ_CONFIRM��REGION_CODE
		inparm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_sp", "H", inparm);
		return result;
	}

	/**
	 * �������(���� :1.��ְ��ͨ ��2.��ְ���ء�3.�Ǿ�����)
	 * 
	 * @param insParm
	 *            TParm
	 * @param type
	 *            int
	 * @return TParm
	 */
	public TParm specialCaseCommReturn(TParm insParm, int type) {
		TParm result = new TParm();
		// System.out.println("type--------------------------------------" +
		// type);
		switch (type) {
		case 1:
			result = DataDown_sp_Y(insParm);
			break;
		case 2:
			result = DataDown_sp_H(insParm);
			break;
		case 3:
			result = DataDown_cmts_H(insParm);
			break;
		}
		if (result.getErrCode() < 0) {
			result.setErr(-1, "������Ϣ�ϴ����ִ���");
			return result;
		}
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			// System.out.println(result.getValue("MESSAGE"));
		}
		return result;
	}

	/**
	 * ˢ������ ����DataDown_sp,ȡ���ţ�U��
	 * 
	 * @param text
	 *            String
	 * @return TParm
	 */
	public TParm DataDown_sp_U(String text) {
		// System.out.println("�������");
		TParm parm = new TParm();
		parm.addData("TEXT", text);
		parm.addData("PARM_COUNT", 1); // ��������
		// TParm result =new TParm();
		// result.setData("NHI_NO","622331206241731001");
		TParm result = commInterFace("DataDown_sp", "U", parm);
		// System.out.println("------------U����----------------" + result);
		// String cardNo = result.getValue("CARD_NO");
		// result.setData("CARD_NO",text);// ҽ������
		// result.setData("CARD_NO", "609120106281322899");// ҽ������
		return result;
	}

	/**
	 * ��������ʶ���� ����DataDown_czys, ��ȡ��Ⱥ�����Ϣ��A��
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_A(TParm insParm) {
		TParm parmA = new TParm();
		parmA.addData("CARD_NO", insParm.getValue("CARD_NO")); // ҽ������
		parmA.addData("TYPE", insParm.getValue("TYPE")); // ��������:1�籣������2 ���֤����
		// ���̶�ֵ 1
		parmA.addData("NHI_HOSPCODE", insParm.getValue("NHI_REGION_CODE")); // ҽԺ����
		parmA.addData("COMU_NO", null); // ��������
		parmA.addData("PARM_COUNT", 4); // ��������
		// parmA.setData("PROGRAM_STATE", "Y");// ����ִ��״̬
		// parmA.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		// ���ز���
		// parm.addData("TEXT", text);
		// System.out.println("ERRORRRSSSSSS");
		TParm result = commInterFace("DataDown_czys", "A", parmA);
		// System.out.println("---------------a����---------------" + result);
		// result.setData("SID", "130323197812152627");// ��ݺ���
		// result.setData("PAT_NAME", "��˼");// ����
		// result.setData("PAT_AGE", "21");// ����
		// result.setData("SEX_CODE", "1");// �Ա�
		// result.setData("COMPANY_DESC", "�����");// ��λ����
		// result.setData("OWN_NO", "1011231111");// ���˱���
		// result.setData("CROWD_TYPE", "1");// ��Ⱥ���1 ����ְ��(������ְ������������)2
		// // ������񵱲�������,�������������֧�ж�
		// result.setData("CHECK_CODES", "12232312321");// ˢ����֤��
		// result.setData("CARD_NO", parm.getValue("CARD_NO"));// ҽ������
		// result.setData("PROGRAM_STATE", "Y");// ����ִ��״̬
		// result.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		return result;
	}

	/**
	 * סԺ�Ǿ� �ʸ�ȷ���鿪������ ��ѯ�籣������Ϣ ����DataDown_czyd,��A������
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czyd_A(TParm insParm) {
		TParm parmA = new TParm();
		parmA.addData("SID", insParm.getValue("IDNO1")); // ���֤��
		parmA.addData("NAME", insParm.getValue("PAT_NAME1")); // ����
		parmA.addData("PARM_COUNT", 2); // ��������
		// parmA.setData("PROGRAM_STATE", "Y");// ����ִ��״̬
		// parmA.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		// ���ز���

		// parm.addData("TEXT", text);
		TParm result = commInterFace("DataDown_czyd", "A", parmA);
		// System.out.println("---------------a����---------------" + result);
		// result.setData("SID", "130323197812152627");// ��ݺ���
		// result.setData("PAT_NAME", "��˼");// ����
		// result.setData("PAT_AGE", "21");// ����
		// result.setData("SEX_CODE", "1");// �Ա�
		// result.setData("COMPANY_DESC", "�����");// ��λ����
		// result.setData("OWN_NO", "1011231111");// ���˱���
		// result.setData("CROWD_TYPE", "1");// ��Ⱥ���1 ����ְ��(������ְ������������)2
		// // ������񵱲�������,�������������֧�ж�
		// result.setData("CHECK_CODES", "12232312321");// ˢ����֤��
		// result.setData("CARD_NO", parm.getValue("CARD_NO"));// ҽ������
		// result.setData("PROGRAM_STATE", "Y");// ����ִ��״̬
		// result.setData("PROGRAM_MESSAGE", "ִ�гɹ�");// ����ִ����Ϣ
		return result;
	}

	/**
	 * ���ù����ӿڷ���
	 * 
	 * @param object
	 *            String
	 * @param function
	 *            String
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm commInterFace(String object, String function, TParm parm) {
		// return ()callServerMethod(object,function,function);
		parm.setData("PIPELINE", object);
		parm.setData("PLOT_TYPE", function);
		parm.setData("HOSP_AREA", "HIS");
		// System.out.println("-----------commInterFace--------------" + parm);
		// INSInterface Interface=new INSInterface();
		TParm result = InsManager.getInstance().safe(parm);
		// result = testDataTool.getInstance().testOut(parm);
		// System.out.println("-----------commInterFace--------------" +result);
		// if(result.getErrCode()<0){
		// //System.out.println("���ι����ӿڴ���:"+result.getErrText());
		// }
		// parm.setData("PIPELINE",object);
		// parm.setData("PLOT_TYPE",function);
		// parm.setData("HOSP_AREA","HIS");
		// TParm result=insDatatestTool.getInstance().safe(parm);
		// result=testDataTool.getInstance().testOut(parm);
		return result;
	}

	/**
	 * סԺ��ְ �ۼ��������� ���� DataDown_sp1 (C)����
	 * 
	 * @return TParm
	 */
	public TParm DataDown_sp1_C() {
		TParm parm = new TParm();
		parm.addData("ADDPAY_ADD", ""); // �ۼ����������ܶ�:�ڷ�����ϸ�ָ�󷵻ص������ۼ�������־Ϊ1��������ϸ�ķ������֮��
		parm.addData("HOSP_START_DATE", ""); // סԺ��ʼʱ��
		parm.addData("PARM_COUNT", 2);
		TParm result = commInterFace("DataDown_sp1", "C", parm);
		return result;
	}

	// /**
	// * �Ǿ� �������˷� ���� DataDown_cmzs ��F������
	// *
	// * @return
	// */
	// public TParm DataDown_cmzs_F(TParm parm) {
	// TParm parmF = new TParm();
	// parmF.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE"));// ҽԺ����
	// parmF.addData("COMU_NO", null);// ��������
	// parmF.addData("PARM_COUNT", 4);// ��������
	// parmF.addData("PARM_COUNT", 4);// ����˳���
	// parmF.addData("PARM_COUNT", 4);// ҽԺ����
	// parmF.addData("PARM_COUNT", 4);// ҽԺ����Ա����
	// parmF.addData("PARM_COUNT", 4);// �������
	// parmF.addData("PARM_COUNT", 4);// �걨���
	// parmF.addData("PARM_COUNT", 4);// ȫ�Էѽ��
	// parmF.addData("PARM_COUNT", 4);// �������
	// parmF.addData("PARM_COUNT", 4);// ר������籣֧��
	// parmF.addData("PARM_COUNT", 4);// �������
	// TParm result = commInterFace("DataDown_cmzs", "F", parm);
	// //System.out.println("��ְ��ͨ �����˷�   ���� DataDown_cmzs ��F��:" + parmF);
	// // ����ִ��״̬
	// // ����ִ����Ϣ
	// // �籣���ʱ�־
	// // ���Ľ���ʱ��
	// return result;
	// }
	/**
	 * ��ְ��ͨ �����˷� ���� DataDown_yb ��C������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_yb_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 ˳���
		parmC.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 2 ҽԺ����
		parmC
				.addData("HOSP_OPT_USER_CODE", parm
						.getValue("HOSP_OPT_USER_CODE")); // 3 ҽԺ����Ա����
		parmC.addData("TOTAL_AMT", StringTool.round(Math.abs(parm
				.getDouble("TOT_AMT")), 2)); // 4
		// �������
		parmC.addData("TOTAL_NHI_AMT", StringTool.round(Math.abs(parm
				.getDouble("NHI_AMT")), 2)); // 5
		// �걨���
		parmC.addData("OWN_AMT", StringTool.round(Math.abs(parm
				.getDouble("OWN_AMT")), 2)); // 6
		// ȫ�Էѽ��
		parmC.addData("ADDPAY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ADD_AMT")), 2)); // 7
		// �������
		parmC.addData("ACCOUNT_PAY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ACCOUNT_PAY_AMT")), 2)); // 8
		// ר������籣֧��---������
		parmC.addData("OTOT_AMT", StringTool.round(Math.abs(parm
				.getDouble("OTOT_AMT")), 2)); // 9
		// �����ʻ�ʵ��֧�����
		parmC.addData("AGENT_AMT", StringTool.round(Math.abs(parm
				.getDouble("AGENT_AI_AMT")), 2)); // 10
		// �������
		parmC.addData("PARM_COUNT", 10); // PARM_COUNT
		// System.out.println("parmC::::::::::" + parmC);
		TParm result = commInterFace("DataDown_yb", "C", parmC);
		return result;
	}

	/**
	 * ��ְ��ͨ �˷�ȷ�Ͻ��� DataDown_yb ���� ��D������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_yb_D(TParm parm) {
		TParm parmD = new TParm();
		parmD.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // ˳���
		parmD.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // ҽԺ����
		parmD.addData("OTOT_AMT", parm.getDouble("OTOT_AMT")); // ר������籣֧��
		parmD.addData("ACCOUNT_PAY_AMT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // �����ʻ�ʵ��֧�����----?????
		parmD.addData("PARM_COUNT", 4);
		// System.out.println("��ְ��ͨ �˷�ȷ�Ͻ��� DataDown_yb ����  ��D���������:" + parmD);
		TParm result = commInterFace("DataDown_yb", "D", parmD);
		return result;
	}

	/**
	 * ��ְ���� �� �����˷ѽ��� DataDown_mts ���� ��K��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_K(TParm parm) {
		TParm parmK = DataDown_cmtsOrmts_K(parm);
		parmK.addData("SERVANT_AMT", 0.00); // �������2
		parmK.addData("ACCOUNT_PAY_AMT", StringTool.round(Math.abs(parm
				.getDouble("ACCOUNT_PAY_AMT")), 2)); // �����ʻ�ʵ��֧�����
		parmK.addData("PARM_COUNT", 17);
		TParm result = commInterFace("DataDown_mts", "K", parmK);
		// System.out.println("��ְ���� �������˷ѽ��� DataDown_mts ���� ��K��:" + result);
		return result;
	}

	/**
	 * ��ְ���� �� �˷�ȷ�Ͻ��� DataDown_mts ���� ��L��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_L(TParm parm) {
		TParm parmL = DataDown_mtsOrcmts_L(parm);
		parmL.addData("ACCOUNT_PAY_AMT", null == parm
				.getValue("ACCOUNT_PAY_AMT") ? 0.00 : parm
				.getDouble("ACCOUNT_PAY_AMT")); // �����ʻ�ʵ��֧�����
		parmL.addData("PARM_COUNT", 5);
		// System.out.println("��ְ���� ���˷�ȷ�Ͻ��� DataDown_mts ���� (L)���:" + parmL);
		TParm result = commInterFace("DataDown_mts", "L", parmL);
		// System.out.println("��ְ���� ���˷�ȷ�Ͻ��� DataDown_mts ���� ��L��:" + result);
		return result;
	}

	/**
	 * �Ǿ����� �˷�ȷ�Ͻ��� DataDown_cmts ���� ��K��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_K(TParm parm) {
		TParm parmK = DataDown_cmtsOrmts_K(parm);
		parmK.addData("PARM_COUNT", 15);
		// System.out.println("�Ǿ����� �˷�ȷ�Ͻ��� DataDown_cmts ���� ��K�����:" + parmK);
		TParm result = commInterFace("DataDown_cmts", "K", parmK);
		// System.out.println("�Ǿ����� �˷�ȷ�Ͻ��� DataDown_cmts ���� ��K��:" + result);
		return result;
	}

	/**
	 * ��ְ����/�Ǿ����� �Ǿ�����: �˷�ȷ�Ͻ��� DataDown_cmts ���� ��K�� ��ְ����: �����˷ѽ��� DataDown_mts ����
	 * K����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_cmtsOrmts_K(TParm parm) {
		TParm parmK = new TParm();
		parmK.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 ���ؾ�ҽ˳���
		parmK.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // 2
		// ҽԺ(��ҩ��)����
		parmK.addData("OPT_USER", parm.getValue("OPT_USER")); // 3 ���ز���Ա����
		String sql = "SELECT NHI_NO FROM SYS_CTZ WHERE CTZ_CODE='"
				+ parm.getValue("PAT_TYPE") + "'";
		// System.out.println("SEL::::::::"+sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		parmK.addData("PAT_TYPE", ctzParm.getValue("NHI_NO", 0)); // 4
		// ��Ա���--������
		parmK.addData("PAY_KIND", parm.getValue("PAY_KIND")); // 5
		// ֧�����--������INS_PAT_TYPE
		parmK.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // 6
		// �������--������
		parmK.addData("BCSSQF_STANDRD_AMT", StringTool.round(Math.abs(parm
				.getDouble("BCSSQF_STANDRD_AMT")), 2)); // 7
		// ����ʵ���𸶱�׼���
		parmK.addData("INS_STANDARD_AMT", StringTool.round(Math.abs(parm
				.getDouble("INS_STANDARD_AMT")), 2)); // 8
		// �𸶱�׼�����Ը��������
		parmK.addData("OWN_AMT", Math.abs(parm.getDouble("OWN_AMT"))); // 9
		// �Է���Ŀ���
		parmK.addData("PERCOPAYMENT_RATE_AMT", StringTool.round(Math.abs(parm
				.getDouble("PERCOPAYMENT_RATE_AMT")), 2)); // 10 ҽ�ƾ������˰������������
		parmK.addData("ADD_AMT", StringTool.round(Math.abs(parm
				.getDouble("ADD_AMT")), 2)); // 11
		// ������Ŀ���
		parmK.addData("INS_HIGHLIMIT_AMT", StringTool.round(Math.abs(parm
				.getDouble("INS_HIGHLIMIT_AMT")), 2)); // 12
		// ҽ�ƾ�������޶����Ͻ��
		parmK.addData("TOTAL_AGENT_AMT", StringTool.round(Math.abs(parm
				.getDouble("TOTAL_AGENT_AMT")), 2)); // 13
		// ����ҽ���籣������
		parmK.addData("FLG_AGENT_AMT", StringTool.round(Math.abs(parm
				.getDouble("FLG_AGENT_AMT")), 2)); // 14
		// ҽ�ƾ����籣������
		parmK.addData("ARMY_AI_AMT", StringTool.round(Math.abs(parm
				.getDouble("ARMY_AI_AMT")), 2)); // 15
		// �������
		return parmK;
	}

	/**
	 * �Ǿ����� �� �˷�ȷ�Ͻ��� DataDown_cmts ���� ��L��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_L(TParm parm) {
		TParm parmL = DataDown_mtsOrcmts_L(parm);
		parmL.addData("PARM_COUNT", 4);
		// System.out.println("�Ǿ����� ���˷�ȷ�Ͻ��� DataDown_cmts ���� ��L�����:" + parmL);
		TParm result = commInterFace("DataDown_cmts", "L", parmL);
		// System.out.println("�Ǿ����� ���˷�ȷ�Ͻ��� DataDown_cmts ���� ��L��:" + result);
		return result;
	}

	/**
	 * ��ְ����/�Ǿ����� �Ǿ����� �� �˷�ȷ�Ͻ��� DataDown_cmts ���� ��L�� ��ְ����: �˷�ȷ�Ͻ��� DataDown_mts
	 * ����L����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mtsOrcmts_L(TParm parm) {
		TParm parmL = new TParm();
		parmL.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 ���ؾ�ҽ˳���
		parmL.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ(��ҩ��)����
		parmL.addData("TOTAL_AGENT_AMT", StringTool.round(parm
				.getDouble("TOTAL_AGENT_AMT"), 2)); // 3
		// ����ҽ���籣������
		parmL.addData("FLG_AGENT_AMT", StringTool.round(parm
				.getDouble("FLG_AGENT_AMT"), 2)); // 4
		// ҽ�ƾ����籣������
		return parmL;
	}

	/**
	 * ��������˲��� ��ְ��ͨ ��DataDown_sp���� O ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_O(TParm parm) {
		TParm parmO = new TParm();
		parmO.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 1
		// HOSP_NHI_NO
		// ҽԺ����
		parmO.addData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER")); // 2
		// HOSP_OPT_USER_CODE
		// ҽԺ����Ա����
		parmO.addData("OWN_NO", parm.getValue("OWN_NO")); // 3 OWN_NO ���˱���
		// -----?????
		parmO.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 4
		// COLLATE_ACCOUNT_TIME
		// ����ʱ��
		parmO.addData("PAY_TYPE", parm.getValue("PAY_KIND")); // 5 PAY_TYPE ֧�����
		parmO.addData("TOTAL_AMT", StringTool.round(
				parm.getDouble("TOTAL_AMT"), 2)); // 6 TOTAL_AMT
		// �������
		parmO
				.addData("NHI_AMT", StringTool.round(parm.getDouble("NHI_AMT"),
						2)); // 7 NHI_AMT �걨���
		parmO
				.addData("OWN_AMT", StringTool.round(parm.getDouble("OWN_AMT"),
						2)); // 8 OWN_AMT ȫ�Էѽ��
		parmO.addData("ADDPAY_AMT", StringTool.round(parm
				.getDouble("ADDPAY_AMT"), 2)); // 9
		// ADDPAY_AMT
		// �������
		parmO.addData("OTOT_AMT", StringTool.round(parm.getDouble("OTOT_AMT"),
				2)); // 10 OTOT_AMT
		// ר������籣֧��
		parmO.addData("ACCOUNT_PAY_AMT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 11
		// ACCOUNT_PAY_AMT
		// �����ʻ�ʵ��֧�����
		parmO.addData("ALL_TIME", parm.getInt("ALL_TIME")); // 12 ALL_TIME ���˴�
		parmO.addData("OTOT_OUT_AMT", parm.getDouble("OTOT_OUT_AMT")); // 13
		// OTOT_OUT_AMT
		// ר������籣֧��(�˷�)
		parmO.addData("ACCOUNT_PAY_AMT_EXIT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT_EXIT"), 2)); // 14
															// ACCOUNT_PAY_AMT_EXIT
		// �����ʻ�ʵ��֧�����(�˷�)
		parmO.addData("ALL_TIME_EXIT", parm.getInt("ALL_TIME_EXIT")); // 15
		// ALL_TIME_EXIT
		// ���˴�(�˷�)
		parmO.addData("AGENT_AMT", StringTool.round(
				parm.getDouble("AGENT_AMT"), 2)); // 16 AGENT_AMT
		// ���������������
		parmO.addData("AGENT_AMT_OUT", StringTool.round(parm
				.getDouble("AGENT_AMT_OUT"), 2)); // 17
		// AGENT_AMT_OUT
		// ���������������(�˷�)
		parmO.addData("FY_AGENT_AMT", StringTool.round(parm
				.getDouble("FY_AGENT_AMT"), 2)); // 18
		// FY_AGENT_AMT
		// �Ÿ����������
		parmO.addData("FY_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FY_AGENT_AMT_B"), 2)); // 19
		// FY_AGENT_AMT_B
		// �Ÿ����������(�˷�)
		parmO.addData("FD_AGENT_AMT", StringTool.round(parm
				.getDouble("FD_AGENT_AMT"), 2)); // 20
		// FD_AGENT_AMT
		// �ǵ����֢�������
		parmO.addData("FD_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FD_AGENT_AMT_B"), 2)); // 21
		// FD_AGENT_AMT_B
		// �ǵ����֢��������˷�)
		parmO.addData("UNREIM_AMT", StringTool.round(parm
				.getDouble("UNREIM_AMT"), 2)); // 22
		// UNREIM_AMT
		// ����δ������
		parmO.addData("UNREIM_AMT_B", StringTool.round(parm
				.getDouble("UNREIM_AMT_B"), 2)); // 23
		// UNREIM_AMT_B
		// ����δ�������˷ѣ�
		parmO.addData("PARM_COUNT", 23);
		// System.out.println("��ְ��ͨ ��DataDown_sp���� O �������:" + parmO);
		TParm result = commInterFace("DataDown_sp", "O", parmO);
		// System.out.println("��ְ��ͨ ��DataDown_sp���� O ����:" + result);
		return result;
	}

	/**
	 * ��������˲��� ��ְ���� ��DataDown_mts���� M ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_M(TParm parm) {
		TParm parmM = DataDown_mtsAndcmtsUp(parm);
		parmM.addData("ARMY_AI_AMT", StringTool.round(parm
				.getDouble("ARMY_AI_AMT"), 2)); // 14
		// ARMY_AI_AMT
		// ���в������(��������)
		// -----
		parmM.addData("ARMY_AI_AMT_B", StringTool.round(parm
				.getDouble("ARMY_AI_AMT_B"), 2)); // 15
		// ARMY_AI_AMT_B
		// ���в������(�����˷�)
		// ------
		parmM.addData("SERVANT_AMT", StringTool.round(parm
				.getDouble("SERVANT_AMT"), 2)); // 16
		// SERVANT_AMT
		// ����Ա�������(��������)
		// ----
		parmM.addData("SERVANT_AMT_B", StringTool.round(parm
				.getDouble("SERVANT_AMT_B"), 2)); // 17
		// SERVANT_AMT_B
		// ����Ա�������(�����˷�)
		// -----
		parmM.addData("ACCOUNT_PAY_AMT", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT"), 2)); // 18
		// ACCOUNT_PAY_AMT
		// �����ʻ�ʵ��֧�����(��������)
		// ----
		parmM.addData("ACCOUNT_PAY_AMT_B", StringTool.round(parm
				.getDouble("ACCOUNT_PAY_AMT_B"), 2)); // 19
		// ACCOUNT_PAY_AMT_B
		// �����ʻ�ʵ��֧�����(�����˷�)
		// ----
		parmM = DataDown_mtsAndcmtsDown(parmM);
		parmM.addData("PARM_COUNT", 27);
		// System.out.println("��ְ���� ��DataDown_mts���� M �������:" + parmM);
		TParm result = commInterFace("DataDown_mts", "M", parmM);
		// System.out.println("��ְ���� ��DataDown_mts���� M����:" + result);
		return result;
	}

	/**
	 * ��������˲��� �Ǿ����� ��DataDown_cmts���� M ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_M(TParm parm) {
		TParm parmM = DataDown_mtsAndcmtsDown(DataDown_mtsAndcmtsUp(parm));
		parmM.addData("PARM_COUNT", 21);
		// System.out.println("�Ǿ����� ��DataDown_cmts���� O �������:" + parmM);
		TParm result = commInterFace("DataDown_cmts", "M", parmM);
		// System.out.println("�Ǿ����� ��DataDown_cmts���� M����:" + result);
		return result;
	}

	/**
	 * ������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmtsUp(TParm parm) {
		TParm parmM = parm;
		parmM.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // 1
		// HOSP_NHI_NO
		// ҽԺ����
		parmM.addData("OPT_USER", parm.getValue("OPT_USER")); // 2 OPT_USER
		// ���ز���Ա����
		parmM.addData("OWN_NO", parm.getValue("OWN_NO")); // 3 OWN_NO ���˱���
		// VARCHAR2
		parmM.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // 4
		// COLLATE_ACCOUNT_TIME
		// ����ʱ��
		parmM.addData("TOTAL_AMT", parm.getDouble("TOTAL_AMT")); // 5 TOTAL_AMT
		// �������(��������)
		parmM.addData("APPLY_AMT", parm.getDouble("APPLY_AMT")); // 6 APPLY_AMT
		// ͳ������걨���(��������)
		parmM.addData("FLG_AGENT_AMT", parm.getDouble("FLG_AGENT_AMT")); // 7
		// FLG_AGENT_AMT
		// ���������걨���(��������)
		parmM.addData("OWN_AMT", parm.getDouble("OWN_AMT")); // 8 OWN_AMT
		// ȫ�Էѽ��(��������)
		parmM.addData("ADD_AMT", parm.getDouble("ADD_AMT")); // 9 ADD_AMT
		// �������(��������)
		parmM.addData("SUM_PERTIME", parm.getInt("SUM_PERTIME")); // 10
		// SUM_PERTIME
		// ���˴�(��������)
		parmM.addData("APPLY_AMT_B", parm.getDouble("APPLY_AMT_B")); // 11
		// APPLY_AMT_B
		// ͳ������籣֧�����˷ѣ�
		parmM.addData("FLG_AGENT_AMT_B", parm.getDouble("FLG_AGENT_AMT_B")); // 12
		// FLG_AGENT_AMT_B
		// ҽ�ƾ���֧�����˷ѣ�
		parmM.addData("SUM_PERTIME_B", parm.getDouble("SUM_PERTIME_B")); // 13
		// SUM_PERTIME_B
		// ���˴Σ��˷ѣ�
		return parmM;
	}

	/**
	 * ������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmtsDown(TParm parm) {
		TParm parmM = parm;
		parmM.addData("MZ_AGENT_AMT", StringTool.round(parm
				.getDouble("MZ_AGENT_AMT"), 2)); // MZ_AGENT_AMT
		// ���������������
		parmM.addData("MZ_AGENT_AMT_B", StringTool.round(parm
				.getDouble("MZ_AGENT_AMT_B"), 2)); // MZ_AGENT_AMT_B
		// ���������������(�˷�)
		parmM.addData("FY_AGENT_AMT", StringTool.round(parm
				.getDouble("FY_AGENT_AMT"), 2)); // FY_AGENT_AMT
		// �Ÿ����������
		parmM.addData("FY_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FY_AGENT_AMT_B"), 2)); // FY_AGENT_AMT_B
		// �Ÿ����������(�˷�)
		parmM.addData("FD_AGENT_AMT", StringTool.round(parm
				.getDouble("FD_AGENT_AMT"), 2)); // FD_AGENT_AMT
		// �ǵ����֢�������
		parmM.addData("FD_AGENT_AMT_B", StringTool.round(parm
				.getDouble("FD_AGENT_AMT_B"), 2)); // FD_AGENT_AMT_B
		// �ǵ����֢��������˷�)
		parmM.addData("UNREIM_AMT", StringTool.round(parm
				.getDouble("UNREIM_AMT"), 2)); // UNREIM_AMT
		// ����δ�������
		parmM.addData("UNREIM_AMT_B", StringTool.round(parm
				.getDouble("UNREIM_AMT_B"), 2)); // UNREIM_AMT_B
		// ����δ�������˷ѣ�
		return parmM;
	}

	/**
	 * ��ְ��ͨ ��ϸ���� ����DataDown_rs (M) ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_rs_M(TParm parm) {
		TParm parmM = new TParm();
		// System.out.println("parm:::::" + parm);
		parmM.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // ҽԺ����HOSP_NHI_NO
		parmM.addData("OPT_USER_CODE", parm.getValue("OPT_USER")); // ����Ա���OPT_USER_CODE
		parmM.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// ���˱���
		parmM.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // ����ʱ��COLLATE_ACCOUNT_TIME
		// ���ؾ�ҽ˳���
		// �˷ѱ�־
		parmM.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // ���ؾ�ҽ˳���
		parmM.addData("UN_FLG", parm.getValue("UN_FLG")); // �˷ѱ�־
		// ������ͬ
		parmM.addData("PARM_COUNT", 6);
		// System.out.println("��ְ��ͨ��DataDown_rs_M���� M �������:" + parmM);
		TParm result = commInterFace("DataDown_rs", "M", parmM);
		// System.out.println("��ְ��ͨ ��DataDown_rs���� M����:" + result);
		return result;
	}

	/**
	 * ��ְ���� ��ϸ���� ����DataDown_mtd ��E������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mtd_E(TParm parm) {
		TParm parmE = new TParm();
		parmE.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // ҽԺ����HOSP_NHI_NO
		parmE.addData("OPT_USER_CODE", parm.getValue("OPT_USER")); // ����Ա���OPT_USER_CODE
		parmE.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// ���˱���
		parmE.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // ����ʱ��COLLATE_ACCOUNT_TIME
		// ���ؾ�ҽ˳���
		// �˷ѱ�־
		parmE.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // ���ؾ�ҽ˳���
		parmE.addData("UN_FLG", parm.getValue("UN_FLG")); // �˷ѱ�־
		// ������ͬ
		parmE.addData("PARM_COUNT", 6);
		// System.out.println("��ְ���� ��DataDown_mtd_E���� M �������:" + parmE);
		TParm result = commInterFace("DataDown_mtd", "E", parmE);
		// System.out.println("��ְ���� ��DataDown_mtd_E���� E����:" + result);
		return result;
	}

	/**
	 * �Ǿ����� ��ϸ���� ����DataDown_cmtd ��E������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmtd_E(TParm parm) {
		TParm parmE = new TParm();
		parmE.addData("HOSP_NHI_NO", parm.getValue("REGION_CODE")); // ҽԺ����HOSP_NHI_NO
		parmE.addData("OPT_USER_CODE", parm.getValue("OPT_USER")); // ����Ա���OPT_USER_CODE
		parmE.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// ���˱���
		parmE.addData("COLLATE_ACCOUNT_TIME", parm.getData("ACCOUNT_DATE")); // ����ʱ��COLLATE_ACCOUNT_TIME
		// ���ؾ�ҽ˳���
		// �˷ѱ�־
		parmE.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // ���ؾ�ҽ˳���
		parmE.addData("UN_FLG", parm.getValue("UN_FLG")); // �˷ѱ�־
		// ������ͬ
		parmE.addData("PARM_COUNT", 6);
		// System.out.println("�Ǿ����� ��DataDown_cmtd_E���� M �������:" + parmE);
		TParm result = commInterFace("DataDown_cmtd", "E", parmE);
		// System.out.println("�Ǿ����� ��DataDown_cmtd_E���� E����:" + result);
		return result;
	}

	/**
	 * ��ְ ���� ���صǼ������� ���� DataDown_mts ��A������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_A(TParm parm) {
		// System.out.println("��ְ ���� ���صǼ�������::"+parm);
		TParm parmA = DataDown_mtsAndcmts_A(parm);
		// System.out.println("��ְ���أ�DataDown_mts���� A �������:" + parmA);
		TParm result = commInterFace("DataDown_mts", "A", parmA);
		// System.out.println("��ְ ���أ�DataDown_mts���� A����:" + result);
		return result;
	}

	/**
	 * �Ǿ����� ���صǼ������� ���� DataDown_cmts ��A������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_A(TParm parm) {
		TParm parmA = DataDown_mtsAndcmts_A(parm);
		// System.out.println("�Ǿ� ���أ�DataDown_cmts���� A �������:" + parmA);
		TParm result = commInterFace("DataDown_cmts", "A", parmA);
		// System.out.println("�Ǿ����� ��DataDown_cmts���� A����:" + result);
		return result;
	}

	/**
	 * ���صǼ������� ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_A(TParm parm) {
		TParm parmA = new TParm();
		parmA.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // �籣����CARD_NO
		parmA.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // ���ز��ֱ���DISEASE_CODE
		parmA.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // ҽԺ(��ҩ��)����HOSP_NHI_NO----?????
		parmA.addData("PARM_COUNT", 3);
		return parmA;
	}

	/**
	 * ��ְ���� ҽԺ�����ϴ�������Ϣ ����DataDown_mts ��C������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_C(TParm parm) {
		TParm parmC = DataDown_mtsAndcmts_C(parm);
		// System.out.println("��ְ���أ�DataDown_mts���� C �������:" + parmC);
		TParm result = commInterFace("DataDown_mts", "C", parmC);
		// System.out.println("��ְ���� ��DataDown_mts���� C����:" + result);
		return result;
	}

	/**
	 * �Ǿ����� ҽԺ�����ϴ�������Ϣ ����DataDown_cmts ��C������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_C(TParm parm) {
		TParm parmC = DataDown_mtsAndcmts_C(parm);
		// System.out.println("�Ǿ����أ�DataDown_cmts���� C �������:" + parmC);
		TParm result = commInterFace("DataDown_cmts", "C", parmC);
		// System.out.println("�Ǿ����� ��DataDown_cmts���� C����:" + result);
		return result;
	}

	/**
	 * ҽԺ�����ϴ�������Ϣ ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // ���صǼǱ��
		parmC.addData("BEGIN_DATE", parm.getValue("BEGIN_DATE")); // ���ؿ�ʼʱ��
		parmC.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // ҽԺ����---��������
		parmC.addData("PARM_COUNT", 3);
		return parmC;
	}

	/**
	 * ��ְ���� �������صǼ���Ϣ ����DataDown_mts ��B������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_B(TParm parm) {
		TParm parmB = DataDown_mtsAndcmts_B(parm);
		// System.out.println("��ְ���أ�DataDown_mts���� B �������:" + parmB);
		TParm result = commInterFace("DataDown_mts", "B", parmB);
		// System.out.println("��ְ���� ��DataDown_mts���� B����:" + result);
		return result;
	}

	/**
	 * �Ǿ����� �������صǼ���Ϣ ����DataDown_cmts��B������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_B(TParm parm) {
		TParm parmB = DataDown_mtsAndcmts_B(parm);
		// System.out.println("�Ǿ����أ�DataDown_cmts���� B �������:" + parmB);
		TParm result = commInterFace("DataDown_cmts", "B", parmB);
		// System.out.println("�Ǿ����� ��DataDown_cmts���� B����:" + result);
		return result;
	}

	/**
	 * �������صǼ���Ϣ ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_B(TParm parm) {
		TParm parmB = new TParm();

		parmB.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO")); // 1
		// PERSONAL_NO
		// ���˱���
		parmB.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // 2
		// REGISTER_NO
		// ���صǼǱ��
		parmB.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // 3
		// DISEASE_CODE
		// ���ز��ֱ���
		parmB.addData("DIAG_HOSP_CODE", parm.getValue("DIAG_HOSP_CODE")); // 4
		// DIAG_HOSP_CODE
		// �������ҽԺ����
		parmB.addData("REGISTER_DR_CODE1", parm.getValue("REGISTER_DR_CODE1")); // 5
		// REGISTER_DR_CODE1
		// ���صǼ�ҽʦ����
		parmB.addData("REGISTER_DR_CODE2", parm.getValue("REGISTER_DR_CODE2")); // 6
		// REGISTER_DR_CODE2
		// ���صǼ�ҽʦ����1
		parmB.addData("PAT_PHONE", parm.getValue("PAT_PHONE")); // 7 PAT_PHONE
		// ������ϵ�绰
		parmB.addData("PAT_ZIP_CODE", parm.getValue("PAT_ZIP_CODE")); // 8
		// PAT_ZIP_CODE
		// ��������
		parmB.addData("PAT_ADDRESS", parm.getValue("PAT_ADDRESS")); // 9
		// PAT_ADDRESS
		// ��ͥ��ϸסַ
		parmB.addData("DISEASE_HISTORY", parm.getValue("DISEASE_HISTORY")); // 10
		// DISEASE_HISTORY
		// ��ʷ
		parmB.addData("ASSISTANT_EXAMINE", parm.getValue("ASSISTANT_EXAMINE")); // 11
		// ASSISTANT_EXAMINE
		// �������
		parmB.addData("DIAG_CODE", parm.getValue("DIAG_CODE")); // 12 DIAG_CODE
		// �ٴ����
		parmB.addData("HOSP_CODE_LEVEL1", parm.getValue("HOSP_CODE_LEVEL1")); // 13
		// HOSP_CODE_LEVEL1
		// ѡ��ҽ������ҽԺ(һ��)
		parmB.addData("HOSP_CODE_LEVEL2", parm.getValue("HOSP_CODE_LEVEL2")); // 14
		// HOSP_CODE_LEVEL2
		// ѡ��ҽ������ҽԺ(����)
		parmB.addData("HOSP_CODE_LEVEL3", parm.getValue("HOSP_CODE_LEVEL3")); // 15
		// HOSP_CODE_LEVEL3
		// ѡ��ҽ������ҽԺ(����)
		parmB.addData("HOSP_CODE_LEVEL3_PRO", parm
				.getValue("HOSP_CODE_LEVEL3_PRO")); // 16 HOSP_CODE_LEVEL3_PRO
		// ѡ��ҽ������ҽԺ(����ר��)
		parmB.addData("DRUGSTORE_CODE", parm.getValue("DRUGSTORE_CODE")); // 17
		// DRUGSTORE_CODE
		// ѡ��ҽ������ҽԺ(��������ҩ��)
		parmB.addData("REGISTER_USER", parm.getValue("REGISTER_USER")); // 18
		// REGISTER_USER
		// ���صǼǾ�����
		parmB.addData("REGISTER_RESPONSIBLE", parm
				.getValue("REGISTER_RESPONSIBLE")); // 19 REGISTER_RESPONSIBLE
		parmB.addData("MED_HISTORY", parm.getValue("MED_HISTORY")); // 20����ʷ(����)
		
		if (null != parm.getValue("FLG") && parm.getValue("FLG").equals("Y")) {
			parmB.addData("ASSISTANT_STUFF", parm.getValue("ASSSISTANT_STUFF")); // 21
																					// ��������(����)
			parmB.addData("JUDGE_CONTER_I", parm.getValue("JUDGE_CONTER_I")); // 22
																				// �����������(����)
			parmB.addData("JUDGE_END", parm.getValue("JUDGE_END")); // 23
																	// ��������(����)
			parmB.addData("THE_JUDGE_START_DATE", parm
					.getValue("THE_JUDGE_START_DATE")); // 24 ���μ�����ʼʱ��(����)
			parmB.addData("THE_JUDGE_END_DATE", parm
					.getValue("THE_JUDGE_END_DATE")); // 25 ���μ�������ʱ��(����)
			parmB.addData("THE_JUDGE_TOT_AMT", parm
					.getDouble("THE_JUDGE_TOT_AMT")); // 26 ���μ����������ϼ�(����)
			parmB.addData("THE_JUDGE_APPLY_AMT", parm
					.getDouble("THE_JUDGE_APPLY_AMT")); // 27 ���μ����걨���ϼ�(����)
		} else {
			parmB.addData("ASSISTANT_STUFF", ""); // 21 ��������(����)
			parmB.addData("JUDGE_CONTER_I", ""); // 22 �����������(����)
			parmB.addData("JUDGE_END", "1"); // 23 ��������(����)
			parmB.addData("THE_JUDGE_START_DATE", ""); // 24 ���μ�����ʼʱ��(����)
			parmB.addData("THE_JUDGE_END_DATE", ""); // 25 ���μ�������ʱ��(����)
			parmB.addData("THE_JUDGE_TOT_AMT", 0.00); // 26 ���μ����������ϼ�(����)
			parmB.addData("THE_JUDGE_APPLY_AMT", 0.00); // 27 ���μ����걨���ϼ�(����)
		}

		// ���صǼǸ�����
		parmB.addData("PARM_COUNT", 27);
		return parmB;
	}

	/**
	 * �Ǿ����� ���صǼǹ�����Դ���ؽ��� DataDown_cmtd ��H������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmtd_H(TParm parm) {
		TParm parmH = DataDown_mtdAndcmtd_H(parm);
		// System.out.println("�Ǿ����أ�DataDown_cmtd���� H �������:" + parmH);
		TParm result = commInterFace("DataDown_cmtd", "H", parmH);
		// System.out.println("�Ǿ����� ��DataDown_cmtd���� H����:" + result);
		return result;
	}

	/**
	 * ��ְ���� ���صǼǹ�����Դ���ؽ��� DataDown_mtd ��H������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mtd_H(TParm parm) {
		TParm parmH = DataDown_mtdAndcmtd_H(parm);
		// System.out.println("��ְ���أ�DataDown_mtd���� H �������:" + parmH);
		TParm result = commInterFace("DataDown_mtd", "H", parmH);
		// System.out.println("��ְ���� ��DataDown_mtd���� H����:" + result);
		return result;
	}

	/**
	 * ���صǼǹ�����Դ���ؽ��� ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtdAndcmtd_H(TParm parm) {
		TParm parmH = new TParm();
		parmH.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // �籣����CARD_NO
		parmH.addData("MT_DISEASE_CODE", parm.getValue("DISEASE_CODE")); // ���ز��ֱ���MT_DISEASE_CODE
		parmH.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // ҽ�ƻ�������
		parmH.addData("PARM_COUNT", 3);
		return parmH;
	}

	/**
	 * �Ǿ�������˲�ѯ ����DataDown_cmts ��D�� ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_cmts_D(TParm parm) {
		TParm parmD = DataDown_mtsAndcmts_D(parm);
		// System.out.println("�Ǿ����أ�DataDown_cmts���� D �������:" + parmD);
		TParm result = commInterFace("DataDown_cmts", "D", parmD);
		// System.out.println("�Ǿ����� ��DataDown_cmts���� D����:" + result);
		return result;
	}

	/**
	 * ��ְ������˲�ѯ ����DataDown_mts ��D�� ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_mts_D(TParm parm) {
		TParm parmD = DataDown_mtsAndcmts_D(parm);
		// System.out.println("��ְ���أ�DataDown_mts���� D �������:" + parmD);
		TParm result = commInterFace("DataDown_mts", "D", parmD);
		// System.out.println("��ְ���� ��DataDown_mts����D����:" + result);
		return result;
	}

	/**
	 * ��ְ���صǼǳ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm DataDown_mts_P(TParm parm){
		TParm parmP = DataDown_mtsAndcmts_P(parm);
		TParm result = commInterFace("DataDown_mts", "P", parmP);
		return result;
    }

	/**
	 * �Ǿ����صǼǳ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm DataDown_cmts_P(TParm parm) {
		TParm parmP = DataDown_mtsAndcmts_P(parm);
		TParm result = commInterFace("DataDown_cmts", "P", parmP);
		return result;
	}

	/**
	 * ���صǼǳ������׹���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm DataDown_mtsAndcmts_P(TParm parm) {
		TParm parmP = new TParm();
		parmP.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // REGISTER_NO
		// ���صǼǱ��
		parmP.addData("DISEASE_CODE", parm.getValue("DISEASE_CODE")); // DISEASE_CODE
		// ���ز��ֱ���
		parmP.addData("BEGIN_DATE", parm.getValue("BEGIN_DATE")); // BEGIN_DATE
		// ���ؿ�ʼʱ��
		parmP.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // HOSP_NHI_NO
		// ҽԺ����
		parmP.addData("PARM_COUNT", 3);
		return parmP;
	}

	/**
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_mtsAndcmts_D(TParm parm) {
		TParm parmD = new TParm();
		parmD.addData("REGISTER_NO", parm.getValue("REGISTER_NO")); // REGISTER_NO
		// ���صǼǱ��
		parmD.addData("BEGIN_DATE", parm.getValue("BEGIN_DATE")); // BEGIN_DATE
		// ���ؿ�ʼʱ��
		parmD.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // HOSP_NHI_NO
		// ҽԺ����
		parmD.addData("PARM_COUNT", 3);
		return parmD;
	}

	/**
	 * סԺ�Ǿ� ��ø��˷�����Ϣ ���� DataDown_czys B����(DataDown_czys A ��������)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_B(TParm parm) {
		TParm parmB = new TParm();
		parmB.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 1 CARD_NO
		// �籣����(����˱���)
		parmB.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// HOSP_NHI_CODE
		// ҽԺ����
		parmB.addData("HAPPEN_DATE", parm.getValue("APP_DATE")); // 3
		// HAPPEN_DATE
		// ��������
		// =====��������
		parmB.addData("CHECK_CODES", parm.getValue("CHECK_CODES")); // 4
		// CHECK_CODES
		// ˢ����֤��
		parmB.addData("PARM_COUNT", 4);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys����B �������:" + parmB);
		TParm result = commInterFace("DataDown_czys", "B", parmB);
		// System.out.println("סԺ�Ǿ� ��DataDown_czys���� B����:" + result);
		return result;
	}

	/**
	 * סԺ�Ǿ� �����ʸ�ȷ���� ����DataDown_czys (C)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("OWN_NO", parm.getValue("PERSONAL_NO")); // 1 OWN_NO ���˱���
		parmC.addData("SFBEST_TRANHOSP", parm.getValue("SFBEST_TRANHOSP")); // 2
		// SFBEST_TRANHOSP
		// �Ƿ�����סԺ
		parmC.addData("ADM_CATEGORY", parm.getValue("ADM_CATEGORY1")); // 3
		// ADM_CATEGORY
		// ��ҽ���
		// 15
		// ��������תסԺ21
		// ��ͨסԺ23�����Ҵ�39������������40����סԺ41�������ⲡ42
		// A��43
		// AB��
		parmC.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 4
		// HOSP_NHI_NO
		// ҽԺ����
		parmC.addData("DEPT", parm.getValue("DEPT_DESC")); // 5 DEPT סԺ�Ʊ�
		parmC.addData("HOSP_DIAGNOSE",
				null != parm.getValue("DIAG_DESC1") ? parm.getValue(
						"DIAG_DESC1").length() > 127 ? parm.getValue(
						"DIAG_DESC1").substring(0, 127) : parm
						.getValue("DIAG_DESC1") : ""); // 6 HOSP_DIAGNOSE סԺ����
		parmC.addData("HOME_DIAGNOSE_NO", parm.getValue("HOMEDIAG_DESC1")); // 7
		// HOME_DIAGNOSE_NO
		// �Ҵ����ֱ���
		parmC.addData("HOME_DIAGNOSE_NAME", parm.getValue("HOMEDIAG_DESC1")); // 8
		// HOME_DIAGNOSE_NAME
		// �Ҵ���������
		parmC.addData("HOSP_START_DATE", parm.getValue("IN_DATE")); // 9
		// HOSP_START_DATE
		// סԺ��ʼʱ��YYYYMMDD
		// parmC.addData("SF_EMERGENCY", null != parm.getValue("EMG_FLG")
		// && parm.getValue("EMG_FLG").equals("Y") ? "1" : "0"); // 10
		parmC.addData("SF_EMERGENCY", parm.getValue("EMG_FLG")); // 10
		// SF_EMERGENCY
		// �Ƿ���
		parmC.addData("TRANHOSP_NUM_NO", parm.getValue("TRAN_NUM1")); // 11
		// TRANHOSP_NUM_NO
		// ת��תԺ������
		parmC.addData("CONFIRM_ITEM", parm.getValue("ADM_PRJ1")); // 12
		// CONFIRM_ITEM
		// ��ҽ��Ŀ �ǿ�
		parmC.addData("SPEDRS_CODE", parm.getValue("SPEDRS_CODE1")); // 13
		// SPEDRS_CODE
		// �������
		parmC.addData("BEARING_OPERATIONS_TYPE", parm
				.getValue("BEARING_OPERATIONS_TYPE")); // 14
		// BEARING_OPERATIONS_TYPE
		// ����������� 01���ù��ڽ�����
		// 02Ůְ�������� 03��ְ��������
		// 04����ȡ�����ڽ�����
		// 05�˹��������ϲ����ù��ڽ�����
		// 06ȡ�����ڽ������ϲ��˹�������
		// 07�������ڽ����� 08����
		// 09��Σ�˹����� 10����
		parmC.addData("TRAMA_ATTEST", parm.getValue("TRAMA_ATTEST")); // 15
		// TRAMA_ATTEST
		// ����֤��
		parmC.addData("OUT_HOSP_NO", parm.getValue("OUT_HOSP_NO")); // 16
		// OUT_HOSP_NO
		// ת��ҽԺ����
		parmC.addData("SPECIAL_DISEASE", parm.getValue("SPE_DISEASE")); // 17
		// SPECIAL_DISEASE
		// ר�Ƽ���
		// 01����02��Ⱦ��03����ר�Ƽ���
		parmC.addData("ZHUNSHENG_NO", parm.getValue("ZHUNSHENG_NO")); // 18
		// ZHUNSHENG_NO
		// ׼��֤��
		parmC.addData("PARM_COUNT", 18);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys����C �������:" + parmC);
		TParm result = commInterFace("DataDown_czys", "C", parmC);
		// System.out.println("סԺ�Ǿ� ��DataDown_czys���� C����:" + result);
		return result;
	}

	/**
	 * סԺ�Ǿ� ��;������Ϣ��ѯ ���� DataDown_czys (N)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_N(TParm parm) {
		TParm parmN = new TParm();
		parmN.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 1�籣����(����˱���)
		parmN.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2�������
		parmN.addData("PARM_COUNT", 2);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys����N �������:" + parmN);
		TParm result = commInterFace("DataDown_czys", "N", parmN);
		// System.out.println("סԺ�Ǿ� ��DataDown_czys���� N����:" + result);
		return result;
	}

	/**
	 * סԺ�Ǿ���;������Ϣ���� ���� DataDown_czys (N)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_O(TParm parm) {
		TParm parmO = new TParm();
		parmO.addData("CARD_NO", parm.getValue("PERSONAL_NO")); // 1�籣����(����˱���)
		parmO.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2�������
		parmO.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ ����˳���
		parmO.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// HOSP_NHI_CODE
		// ҽԺ����
		parmO.addData("TOTAL_AMT", parm.getValue("ADM_SEQ")); // 3 TOTAL_AMT
		// �������
		parmO.addData("OWN_AMT", parm.getValue("ADM_SEQ")); // 4 OWN_AMT �Էѽ��
		parmO.addData("ADDPAY_AMT", parm.getValue("ADM_SEQ")); // 5 ADDPAY_AMT
		// �������
		parmO.addData("TRANBLOOD_OWN_AMT", parm.getValue("ADM_SEQ")); // 6
		// TRANBLOOD_OWN_AMT
		// ��Ѫ�Ը�
		parmO.addData("DS_DATE", parm.getValue("ADM_SEQ")); // 7 DS_DATE ��Ժʱ��
		// DATE
		parmO.addData("USER_CODE", parm.getValue("OPT_USER")); // 8 USER_CODE
		// ҽԺ�������Ա
		parmO.addData("PARM_COUNT", 2);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys����N �������:" + parmO);
		TParm result = commInterFace("DataDown_czys", "N", parmO);
		// System.out.println("סԺ�Ǿ� ��DataDown_czys���� N����:" + result);
		return result;
	}

	/**
	 * סԺ��ְ ��ø��˷�����Ϣ ���� DataDown_sp (D)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_D(TParm parm) {
		TParm parmD = new TParm();
		parmD.addData("OWN_NO", parm.getValue("PERSONAL_NO")); // 1 OWN_NO
		// ����ֹ������ʸ�ȷ���飬�����Ϊ"�����籣��*ҽԺ����"�����ˢ�������ʸ�ȷ���飬�����Ϊ"�籣����*ҽԺ
		parmD.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// HOSP_NHI_CODE
		// ҽԺ����
		parmD.addData("HAPPEN_DATE", parm.getValue("APP_DATE")); // 3
		// HAPPEN_DATE
		// ��������
		// =====��������
		parmD.addData("CHECK_CODES", parm.getValue("CHECK_CODES")); // 4
		// CHECK_CODES
		// ˢ����֤��
		parmD.addData("PARM_COUNT", 4);
		// System.out.println("סԺ�Ǿӣ�DataDown_sp����D �������:" + parmD);
		TParm result = commInterFace("DataDown_sp", "D", parmD);
		// System.out.println("סԺ�Ǿ� ��DataDown_sp���� D����:" + result);
		return result;
	}

	/**
	 * סԺ�Ǿ� �����ʸ�ȷ���� ����DataDown_sp (B)����
	 * 
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_B(TParm insParm) {
		TParm parmB = new TParm();
		parmB.addData("OWN_NO", insParm.getValue("PERSONAL_NO")); // 1 OWN_NO
		// ���˱���
		parmB.addData("SFBEST_TRANHOSP", insParm.getValue("SFBEST_TRANHOSP")); // 2
		// SFBEST_TRANHOSP
		// �Ƿ�����סԺ
		parmB.addData("ADM_CATEGORY", insParm.getValue("ADM_CATEGORY1")); // 3
		// ADM_CATEGORY
		// ��ҽ���
		parmB.addData("HOSP_NHI_NO", insParm.getValue("NHI_REGION_CODE")); // 4
		// HOSP_NHI_NO
		// ҽԺ����
		parmB.addData("DEPT", insParm.getValue("DEPT_DESC")); // 5 DEPT סԺ�Ʊ�
		parmB.addData("HOSP_DIAGNOSE",
				null != insParm.getValue("DIAG_DESC1") ? insParm.getValue(
						"DIAG_DESC1").length() > 127 ? insParm.getValue(
						"DIAG_DESC1").substring(0, 127) : insParm
						.getValue("DIAG_DESC1") : ""); // 6
		// HOSP_DIAGNOSE
		// סԺ����
		// ����ò��˲��ǼҴ�סԺ����סԺ���ִ���"סԺ����"������ò��˼Ҵ�סԺ����סԺ���ִ���"סԺ����@�Ҵ����ֱ���@�Ҵ���������"
		parmB.addData("HOSP_START_DATE", insParm.getValue("IN_DATE")); // 7
		// HOSP_START_DATE
		// סԺ��ʼʱ��
		// YYYYMMDD
		parmB.addData("SF_EMERGENCY", insParm.getValue("EMG_FLG")); // 8
		// SF_EMERGENCY
		// �Ƿ���
		parmB.addData("TRANHOSP_NUM_NO", insParm.getValue("TRAN_NUM1")); // 9
		// TRANHOSP_NUM_NO
		// ת��תԺ������
		parmB.addData("INPAT_ITEM", insParm.getValue("ADM_PRJ1")); // 10
		// INPAT_ITEM
		// ��ҽ��Ŀ
		parmB.addData("MT_DISEASE_CODE", insParm.getValue("SPEDRS_CODE1")); // 11
		// MT_DISEASE_CODE
		// �������
		parmB.addData("GS_CONFIRM_NO", insParm.getValue("GS_CONFIRM_NO")); // 12
		// GS_CONFIRM_NO
		// �����ʸ�ȷ������
		parmB.addData("PRE_CONFIRM_NO", insParm.getValue("PRE_CONFIRM_NO")); // 13
		// PRE_CONFIRM_NO
		// �ϴ��ʸ�ȷ������
		// ת��תԺʱ����
		parmB.addData("PRE_OWN_AMT", StringTool.round(insParm
				.getDouble("PRE_OWN_AMT"), 2)); // 14
		// PRE_OWN_AMT
		// �ϴ��Է���Ŀ���
		// ת��תԺʱ����
		parmB.addData("PRE_ADD_AMT", StringTool.round(insParm
				.getDouble("PRE_ADD_AMT"), 2)); // 15
		// PRE_ADD_AMT
		// �ϴ�������Ŀ���
		// ת��תԺʱ����
		parmB.addData("PRE_NHI_AMT", StringTool.round(insParm
				.getDouble("PRE_NHI_AMT"), 2)); // 16
		// PRE_NHI_AMT
		// �ϴ��걨���
		// ת��תԺʱ����
		parmB.addData("PRE_OUT_TIME", insParm.getValue("PRE_OUT_TIME")
				.toString().replace("/", "")); // 17
		// PRE_OUT_TIME
		// �ϴγ�Ժʱ��
		// ת��תԺʱ����YYYYMMDD
		parmB.addData("SPE_DISEASE", insParm.getValue("SPE_DISEASE")); // 18
		// SPE_DISEASE
		// ר�Ƽ���
		// �����Ÿ���ԱסԺ01����02��Ⱦ��03����ר�Ƽ���
		parmB.addData("PARM_COUNT", 18);
		// System.out.println("סԺ�Ǿӣ�DataDown_sp����B �������:" + parmB);
		TParm result = commInterFace("DataDown_sp", "B", parmB);
		// System.out.println("סԺ�Ǿ� ��DataDown_sp���� B����:" + result);
		return result;
	}

	/**
	 * סԺ��ְ �ʸ�ȷ�������ز��� �ʸ�ȷ�����ѯ����˳��� ���� DataDown_rs (B)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_rs_B(TParm parm) {
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 �ʸ�ȷ������
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmB.addData("IDNO", parm.getValue("IDNO")); // 3 ���֤��
		parmB.addData("PARM_COUNT", 3);
		// System.out.println("סԺ��ְ��DataDown_rs����B �������:" + parmB);
		TParm result = commInterFace("DataDown_rs", "B", parmB);
		// System.out.println("סԺ��ְ��DataDown_rs���� B����:" + result);
		return result;
	}
	/**
	 * סԺ��ְ �ʸ�ȷ�������ز��� �ʸ�ȷ�����ѯ����˳��� ���� DataDown_rs (B)����
	 * ����
	 * @param parm
	 *            TParm
	 * @return TParm
	 * ================pangben 2012-7-12
	 */
	public TParm DataDown_rs_B(TParm parm,String str){
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 �ʸ�ȷ������
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmB.addData("IDNO", parm.getValue("IDNO")); // 3 ���֤��
		parmB.addData("PARM_COUNT", 3);
		parmB.setData("PIPELINE", "DataDown_rs");
		parmB.setData("PLOT_TYPE", "B");
		//parm.setData("HOSP_AREA", "HIS");
		// System.out.println("-----------commInterFace--------------" + parm);
		// INSInterface Interface=new INSInterface();
		TParm result = InsManager.getInstance().safe(parmB,str);
		// System.out.println("סԺ��ְ��DataDown_rs����B �������:" + parmB);
		//TParm result = commInterFace("DataDown_rs", "B", parmB);
		// System.out.println("סԺ��ְ��DataDown_rs���� B����:" + result);
		return result;
	}
	/**
	 * סԺ�Ǿ� �ʸ�ȷ�������ز��� �ʸ�ȷ�����ѯ����˳��� ���� DataDown_czyd (B)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czyd_B(TParm parm) {
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 �ʸ�ȷ������
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmB.addData("SID", parm.getValue("IDNO")); // 3 ���֤��
		parmB.addData("PARM_COUNT", 3);
		// System.out.println("סԺ�Ǿӣ�DataDown_czyd����B �������:" + parmB);
		TParm result = commInterFace("DataDown_czyd", "B", parmB);
		// System.out.println("סԺ�Ǿӣ�DataDown_czyd���� B����:" + result);
		return result;
	}
	/**
	 * סԺ�Ǿ� �ʸ�ȷ�������ز��� �ʸ�ȷ�����ѯ����˳��� ���� DataDown_czyd (B)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czyd_B(TParm parm,String str) {
		TParm parmB = new TParm();
		parmB.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 �ʸ�ȷ������
		parmB.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmB.addData("SID", parm.getValue("IDNO")); // 3 ���֤��
		parmB.addData("PARM_COUNT", 3);
		parmB.setData("PIPELINE", "DataDown_czyd");
		parmB.setData("PLOT_TYPE", "B");
		// System.out.println("סԺ�Ǿӣ�DataDown_czyd����B �������:" + parmB);
		TParm result = InsManager.getInstance().safe(parmB,str);
		// System.out.println("סԺ�Ǿӣ�DataDown_czyd���� B����:" + result);
		return result;
	}
	/**
	 * סԺ�Ǿ� �ʸ�ȷ�������� ���� DataDown_czys (E)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_E(TParm parm) {
		TParm parmE = new TParm();
		parmE.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ˳���
		parmE.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmE.addData("PARM_COUNT", 2);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys����E �������:" + parmE);
		TParm result = commInterFace("DataDown_czys", "E", parmE);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys���� E����:" + result);
		return result;
	}

	/**
	 * סԺ�Ǿ� �ʸ�ȷ�������س������� ���� DataDown_czys (F)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_F(TParm parm) {
		TParm parmF = new TParm();
		parmF.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ˳���
		parmF.addData("HOSP_NHI_CODE", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmF.addData("PARM_COUNT", 2);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys����F �������:" + parmF);
		TParm result = commInterFace("DataDown_czys", "F", parmF);
		// System.out.println("סԺ�Ǿӣ�DataDown_czys���� F����:" + result);
		return result;
	}

	/**
	 * סԺ��ְ �ʸ�ȷ�������� ���� DataDown_sp (A)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_A(TParm parm) {
		TParm parmA = new TParm();
		parmA.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ˳���
		parmA.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmA.addData("PARM_COUNT", 2);
		// System.out.println("סԺ�Ǿӣ�DataDown_sp����A �������:" + parmA);
		TParm result = commInterFace("DataDown_sp", "A", parmA);
		// System.out.println("סԺ�Ǿӣ�DataDown_sp���� A����:" + result);
		return result;
	}

	/**
	 * סԺ��ְ �ʸ�ȷ�������س������� ���� DataDown_sp (C)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_C(TParm parm) {
		TParm parmC = new TParm();
		parmC.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 1 ˳���
		parmC.addData("HOSP_NHI_NO", parm.getValue("NHI_REGION_CODE")); // 2
		// ҽԺ����
		parmC.addData("PARM_COUNT", 2);
		// System.out.println("סԺ�Ǿӣ�DataDown_sp����C �������:" + parmC);
		TParm result = commInterFace("DataDown_sp", "C", parmC);
		// System.out.println("סԺ�Ǿӣ�DataDown_sp���� C����:" + result);
		return result;
	}

	/**
	 * �ʸ�ȷ���� �ӳ��걨
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_yb_I(TParm parm) {
		TParm parmI = new TParm();
		parmI.addData("PERSONAL_NO", parm.getValue("PERSONAL_NO")); // 1���˱���
		parmI.addData("HOSPITAL_SIS", parm.getValue("HOSPITAL_SIS")); // 2סԺ���
		parmI.addData("HOSPITAL_DATE", parm.getValue("HOSPITAL_DATE")); // 3סԺ����
		parmI.addData("HOSPITAL_NO", parm.getValue("HOSPITAL_NO")); // 4ҽԺ����
		parmI.addData("DELAY_REASON", parm.getValue("DELAY_REASON")); // 5�ӳ���Ϣ
		parmI.addData("INSURANCE_TYPE", parm.getValue("INSURANCE_TYPE")); // 6��������
		parmI.addData("PARM_COUNT", 6);
		// System.out.println("�ӳ��걨��DataDown_yb����I �������:" + parmI);
		TParm result = commInterFace("DataDown_yb", "I", parmI);
		// System.out.println("�ӳ��걨��DataDown_yb���� I����:" + result);
		return result;
	}

	/**
	 * ��ְ סԺ������ϸ�ָ� ����DataDown_sp1 (B)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_B(TParm parm) {
		TParm parmB = DataDown_sp1_BAndG(parm);
		parmB.addData("PARM_COUNT", 9);
		// System.out.println("��ְ סԺ������ϸ�ָDataDown_sp1����B �������:" + parmB);
		TParm result = commInterFace("DataDown_sp1", "B", parmB);
		// System.out.println("��ְ סԺ������ϸ�ָDataDown_sp1���� B����:" + result);
		return result;
	}

	/**
	 * ��ְ סԺ�ۼ��������� ����DataDown_sp1 (C)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_C(TParm parm) {
		TParm parmC = DataDown_sp1_CAndH(parm);
		parmC.addData("PARM_COUNT", 2);
		// System.out.println("��ְסԺ�ۼ��������㣺DataDown_sp1����C �������:" + parmC);
		TParm result = commInterFace("DataDown_sp1", "C", parmC);
		// System.out.println("��ְסԺ�ۼ��������㣺DataDown_sp1���� C����:" + result);
		return result;
	}

	/**
	 * �Ǿ� סԺ������ϸ�ָ� ����DataDown_sp1 (G)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_G(TParm parm) {
		TParm parmG = DataDown_sp1_BAndG(parm);
		parmG.addData("PARM_COUNT", 9);
		// System.out.println("�Ǿ� סԺ������ϸ�ָDataDown_sp1����G �������:" + parmG);
		TParm result = commInterFace("DataDown_sp1", "G", parmG);
		// System.out.println("�Ǿ� סԺ������ϸ�ָDataDown_sp1����G����:" + result);
		return result;
	}

	/**
	 * ��ְ and �Ǿ� סԺ������ϸ�ָ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_BAndG(TParm parm) {
		TParm temp = new TParm();
		temp.addData("NHI_ORDER_CODE", parm.getValue("NHI_ORDER_CODE")); // 1
		// NHI_ORDER_CODE
		// �շ���Ŀ����
		// ��λ��
		temp.addData("CTZ1_CODE", parm.getValue("CTZ1_CODE")); // 2 CTZ1_CODE
		// ��Ա���
		temp.addData("QTY", parm.getValue("QTY")); // 3 QTY ����
		temp.addData("TOTAL_AMT", StringTool.round(parm.getDouble("TOTAL_AMT"),
				2)); // 4 TOTAL_AMT
		// �������
		temp.addData("TIPTOP_BED_AMT", StringTool.round(parm
				.getDouble("TIPTOP_BED_AMT"), 2)); // 5
		// TIPTOP_BED_AMT
		// ��ߴ�λ��
		temp.addData("PHAADD_FLG", parm.getValue("PHAADD_FLG")); // 6
		// PHAADD_FLG
		// ����ҩƷ��־
		temp.addData("FULL_OWN_FLG", parm.getValue("FULL_OWN_FLG")); // 7
		// FULL_OWN_FLG
		// ȫ�Էѱ�־
		temp.addData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO")); // 8
		// HOSP_NHI_NO
		// ҽԺ����
		temp.addData("CHARGE_DATE", parm.getValue("CHARGE_DATE")); // 9
		// CHARGE_DATE���÷���ʱ��
		// YYYYMMDD
		return temp;
	}

	/**
	 * �Ǿ� סԺ�ۼ��������� ����DataDown_sp1 (H)����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp1_H(TParm parm) {
		TParm parmH = DataDown_sp1_CAndH(parm);
		parmH.addData("PARM_COUNT", 2);
		// System.out.println("��ְסԺ�ۼ��������㣺DataDown_sp1����H �������:" + parmH);
		TParm result = commInterFace("DataDown_sp1", "H", parmH);
		// System.out.println("��ְסԺ�ۼ��������㣺DataDown_sp1���� H����:" + result);
		return result;
	}

	/**
	 * ��ְ סԺ�ۼ���������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm DataDown_sp1_CAndH(TParm parm) {
		TParm temp = new TParm();
		temp.addData("ADDPAY_ADD", StringTool.round(parm
				.getDouble("ADDPAY_ADD"), 2)); // 1
		// ADDPAY_ADD�ۼ����������ܶ�
		// �ڷ�����ϸ�ָ�󷵻ص������ۼ�������־Ϊ1��������ϸ�ķ������֮��
		temp.addData("HOSP_START_DATE", parm.getValue("HOSP_START_DATE")); // 2HOSP_START_DATE
		// סԺ��ʼʱ��
		return temp; // YYYYMMDD
	}

	/**
	 * ��ְ���ý��� ����DataDown_sp(I) ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_I(TParm parm) {
		TParm parmI = new TParm();
		parmI.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ ����˳���
		parmI
				.addData("TOT_AMT", StringTool.round(parm.getDouble("TOT_AMT"),
						2)); // 2 TOT_AMT �������
		parmI
				.addData("OWN_AMT", StringTool.round(parm.getDouble("OWN_AMT"),
						2)); // 3 OWN_AMT �Էѽ��
		parmI
				.addData("ADD_AMT", StringTool.round(parm.getDouble("ADD_AMT"),
						2)); // 4 ADD_AMT �������
		parmI
				.addData("NHI_AMT", StringTool.round(parm.getDouble("NHI_AMT"),
						2)); // 5 NHI_AMT �걨���
		parmI.addData("PARM_COUNT", 5);
		// System.out.println("��ְ���ý��㣺DataDown_sp����I �������:" + parmI);
		TParm result = commInterFace("DataDown_sp", "I", parmI);
		// System.out.println("��ְ���ý��㣺DataDown_sp���� I����:" + result);
		return result;
	}

	/**
	 * ������ ��ְ���ý��� ����DataDown_sp(I1) ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_sp_I1(TParm parm) {
		TParm parmI1 = new TParm();
		parmI1.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ ����˳���
		parmI1.addData("TOT_AMT", StringTool
				.round(parm.getDouble("TOT_AMT"), 2)); // 2 TOT_AMT �������
		parmI1.addData("OWN_AMT", StringTool
				.round(parm.getDouble("OWN_AMT"), 2)); // 3 OWN_AMT �Էѽ��
		parmI1.addData("ADD_AMT", StringTool
				.round(parm.getDouble("ADD_AMT"), 2)); // 4 ADD_AMT �������
		parmI1.addData("NHI_AMT", StringTool
				.round(parm.getDouble("NHI_AMT"), 2)); // 5 NHI_AMT
		// �����걨���
		parmI1.addData("SIN_DISEASE_CODE", parm.getValue("SIN_DISEASE_CODE")); // 6
		// SIN_DISEASE_CODE
		// �����ֱ���
		parmI1.addData("IN_DAY", parm.getValue("IN_DAY")); // 7 IN_DAY סԺ����
		parmI1.addData("OUT_TIME", parm.getValue("OUT_TIME")); // 8 OUT_TIME
		// ��Ժʱ��yyyymmdd
		parmI1.addData("SPECIAL_AMT", StringTool.round(parm
				.getDouble("SPECIAL_AMT"), 2)); // 9
		// SPECIAL_AMT
		// ������Ŀ���
		parmI1.addData("PARM_COUNT", 9);
		// System.out.println("�����ֳ�ְ���ý��㣺DataDown_sp����I1 �������:" + parmI1);
		TParm result = commInterFace("DataDown_sp", "I1", parmI1);
		// System.out.println("�����ֳ�ְ���ý��㣺DataDown_sp���� I1����:" + result);
		return result;
	}
    /**
     * ҽ��ҽ���ܿ�
     * @param orderCode String
     * @param ctzCode String
     * @param admType String
     * @param insPatType String
     * @return TParm
     */
	
    public TParm orderCheck(String orderCode, String ctzCode, String admType,
                            String insPatType) {
        TParm result = new TParm();
        String ctzSql =
                " SELECT NHI_CTZ_FLG " + "   FROM SYS_CTZ " +
                "  WHERE CTZ_CODE = '" + ctzCode + "' ";
        TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
        if (!ctzParm.getBoolean("NHI_CTZ_FLG", 0)) {
//            ctzParm.setErr( -1, "����ҽ�����");
            return ctzParm;
        }
        String nhiOrderSql =
                " SELECT  ORDER_CODE, NHI_CODE_O, NHI_CODE_E, NHI_CODE_I,ORDERSET_FLG " +
                "   FROM SYS_FEE " +
                "  WHERE ORDER_CODE = '" + orderCode + "' " +
                "    AND (NHI_CODE_O IS NOT NULL " +
                "     OR NHI_CODE_E IS NOT NULL " +
                "     OR NHI_CODE_I IS NOT NULL )";
        TParm nhiOrderParm = new TParm(TJDODBTool.getInstance().select(
                nhiOrderSql));
//        System.out.println("ҽ�����롷����"+nhiOrderParm.getValue("ORDER_CODE",0)+"======nhiOrderParm>>>>>>>"+nhiOrderParm);
        if (nhiOrderParm.getData("ORDER_CODE", 0) == null ||
            nhiOrderParm.getValue("ORDER_CODE", 0).length() > 0 &&
            "Y".equals(nhiOrderParm.getValue("ORDERSET_FLG", 0))) {
            return nhiOrderParm;
        }
        if (nhiOrderParm.getData("ORDER_CODE", 0) == null
                || nhiOrderParm.getValue("ORDER_CODE", 0).length() <= 0) {
//                nhiOrderParm.setErr( -2, "����ĿΪ�Է���Ŀ");
                return nhiOrderParm;
            }
        //�����Ӧҽ������
        String nhiCodeO = nhiOrderParm.getValue("NHI_CODE_O", 0);
        //�����Ӧҽ������   yanj 20130719 
        String nhiCodeE = nhiOrderParm.getValue("NHI_CODE_E", 0);
        //סԺ��Ӧҽ������
        String nhiCodeI = nhiOrderParm.getValue("NHI_CODE_I", 0);
      //20130719 yanj ���ҽ���Է�ҩ��У�� 
    	String nhi_ownfee_codes[] = {"005306","005307","005308","005309","005310",
    			"005311","005312","005322","005323"};//ҽ���Է���
    	String nhi_addpay_codes[] = {"005333","005338","005351"};//ҽ���Է�������
    	
        // MZYYBZ ����
        // ETYYBZ ��ͯ
        // YKD242 סԺ
        String orderTypeSql = "";
        TParm orderTypeParm = new TParm();
        if ("O".equals(admType)||"E".equals(admType)) {
        	
            orderTypeSql =
                    " SELECT MZYYBZ, YKD242, ETYYBZ " +
                    "   FROM INS_RULE " +
                    "  WHERE SFXMBM = '" + nhiCodeI + "' ";
            orderTypeParm = new TParm(TJDODBTool.getInstance().select(
                    orderTypeSql));
            if (orderTypeParm.getData("MZYYBZ", 0) != null
                && "1".equals(orderTypeParm.getValue("MZYYBZ", 0))) {
                orderTypeParm.setErr( -3, "סԺ��ҩ");
                return orderTypeParm;
            }
            //22 �Ǿ�ѧ����ͯ
            if (!"22".equals(ctzCode)) {
                if (orderTypeParm.getData("ETYYBZ", 0) != null
                    && "1".equals(orderTypeParm.getValue("ETYYBZ", 0))) {
                    orderTypeParm.setErr( -5, "��ͯ��ҩ");
                    return orderTypeParm;
                }
            }
          //yanjing 20130719 ���ﲿ��ҽ���Է�У�� start
            for(int i = 0;i<nhi_ownfee_codes.length;i++){
            	String ins_code = nhi_ownfee_codes[i];
            	if(nhiCodeO.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -6,"ҽ���Է�ҩƷ");
            		return orderTypeParm;
            	}
            }
            for(int j = 0;j<nhi_addpay_codes.length;j++){
            	String ins_code = nhi_addpay_codes[j];
            	if(nhiCodeO.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -7,"ҽ���Է�����ҩƷ");
            		return orderTypeParm;
            	}
            }
          //yanjing 20130719 ���ﲿ��ҽ���Է�У�� end
        }
        if ("I".equals(admType)) {
            orderTypeSql =
                    " SELECT MZYYBZ, YKD242, ETYYBZ " +
                    "   FROM INS_RULE " +
                    "  WHERE SFXMBM = '" + nhiCodeO + "' ";
            orderTypeParm = new TParm(TJDODBTool.getInstance().select(
                    orderTypeSql));
            if (orderTypeParm.getData("YKD242", 0) != null
                && "1".equals(orderTypeParm.getValue("YKD242", 0))) {
                orderTypeParm.setErr( -4, "������ҩ");
                return orderTypeParm;
            }
            //22 �Ǿ�ѧ����ͯ
            if (!"22".equals(ctzCode)) {
                if (orderTypeParm.getData("ETYYBZ", 0) != null
                    && "1".equals(orderTypeParm.getValue("ETYYBZ", 0))) {
                    orderTypeParm.setErr( -5, "��ͯ��ҩ");
                    return orderTypeParm;
                }
            }
            //yanjing 20130719 סԺ����ҽ���Է�У�� start
            for(int i = 0;i<nhi_ownfee_codes.length;i++){
            	String ins_code = nhi_ownfee_codes[i];
            	if(nhiCodeI.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -6,"ҽ���Է�ҩƷ");
            		return orderTypeParm;
            	}
            }
            for(int j = 0;j<nhi_addpay_codes.length;j++){
            	String ins_code = nhi_addpay_codes[j];
            	if(nhiCodeI.equals(ins_code)||nhiCodeE.equals(ins_code)){
            		orderTypeParm.setErr( -7,"ҽ���Է�����ҩƷ");
            		return orderTypeParm;
            	}
            }
            //yanjing 20130719 סԺ����ҽ���Է�У�� end
            //������ݲ�ȥУ�� INS_PAT_TYPE ='2' �������
            if ("2".equals(insPatType)) {
                String mtOrderSql =
                        " SELECT YKD241 " + "   FROM INS_RULE " +
                        "  WHERE YKD241 LIKE '%D%' " +
                        "    AND SFXMBM = '" + nhiCodeO + "' ";
                TParm mtOrderParm = new TParm(TJDODBTool.getInstance().select(
                        mtOrderSql));
                if (mtOrderParm.getData("YKD241", 0) == null
                    || mtOrderParm.getValue("YKD241", 0).length() < 0) {
                    mtOrderParm.setErr( -6, "��������ҩ");
                    return mtOrderParm;
                }
            }
        }
        
        return result;
    }

	/**
	 * �Ǿӷ��ý��� ����DataDown_czys(G) ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_G(TParm parm) {
		TParm parmG = new TParm();
		parmG.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ ����˳���
		parmG.addData("FACT_PAYMENT_SCALE", StringTool.round(parm
				.getDouble("FACT_PAYMENT_SCALE") / 100, 2)); // 2
																// FACT_PAYMENT_SCALE
		// ʵ��֧������
		// ---����
		parmG.addData("SALVATION_PAYMENT_SCALE", StringTool.round(parm
				.getDouble("SALVATION_PAYMENT_SCALE") / 100, 2)); // 3
		// SALVATION_PAYMENT_SCALE
		// ����֧������--����
		parmG.addData("RESTART_STANDARD_AMT", StringTool.round(parm
				.getDouble("RESTART_STANDARD_AMT"), 2)); // 4
															// RESTART_STANDARD_AMT
		// ����ʵ���𸶱�׼
		parmG.addData("TOTAL_PAYMENT_AMT", StringTool.round(parm
				.getDouble("TOTAL_PAYMENT_AMT"), 2)); // 5
		// TOTAL_PAYMENT_AMT
		// ����ҽ��֧���޶�
		parmG.addData("APPLY1_AMT", StringTool.round(parm
				.getDouble("APPLY1_AMT"), 2)); // 6
		// APPLY1_AMT
		// ҽ�ƾ���֧���޶�
		parmG
				.addData("NHI_AMT", StringTool.round(parm.getDouble("NHI_AMT"),
						2)); // 7 NHI_AMT �걨���
		// BEARING_OPERATIONS_TYPE ����������� 01���ù��ڽ����� 02Ůְ�������� 03��ְ��������
		// 04����ȡ�����ڽ����� 05�˹��������ϲ����ù��ڽ����� 06ȡ�����ڽ������ϲ��˹�������
		// 07�������ڽ����� 08���� 09��Σ�˹����� 10����
		parmG.addData("BEARING_OPERATIONS_TYPE", parm
				.getValue("BEARING_OPERATIONS_TYPE")); // 8
		// BEARING_OPERATIONS_TYPE
		parmG
				.addData("TOT_AMT", StringTool.round(parm.getDouble("TOT_AMT"),
						2)); // 9 TOT_AMT
		parmG
				.addData("OWN_AMT", StringTool.round(parm.getDouble("OWN_AMT"),
						2)); // 10 OWN_AMT
		parmG
				.addData("ADD_AMT", StringTool.round(parm.getDouble("ADD_AMT"),
						2)); // 11 ADD_AMT �������
		parmG.addData("BIRTH_TYPE", parm.getValue("BIRTH_TYPE")); // 12
		// BIRTH_TYPE
		// ������ʽ
		// 11�ʹ���12��Ȼ����13��ֹ����
		parmG.addData("BABY_NO", 0); // 13 BABY_NO ����̥������
		parmG.addData("PARM_COUNT", 13);
		// System.out.println("�Ǿӷ��ý��㣺DataDown_czys����G �������:" + parmG);
		TParm result = commInterFace("DataDown_czys", "G", parmG);
		// System.out.println("�Ǿӷ��ý��㣺DataDown_czys���� G����:" + result);
		return result;
	}

	/**
	 * ������ �Ǿӷ��ý��� ����DataDown_czys(G1) ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm DataDown_czys_G1(TParm parm) {
		TParm parmG1 = new TParm();
		parmG1.addData("ADM_SEQ", parm.getValue("ADM_SEQ")); // 1 ADM_SEQ ����˳���
		parmG1.addData("TOT_AMT", StringTool
				.round(parm.getDouble("TOT_AMT"), 2)); // 2 TOT_AMT �������
		parmG1.addData("OWN_AMT", StringTool
				.round(parm.getDouble("OWN_AMT"), 2)); // 3 OWN_AMT �Էѽ��
		parmG1.addData("ADD_AMT", StringTool
				.round(parm.getDouble("ADD_AMT"), 2)); // 4 ADD_AMT �������
		parmG1.addData("NHI_AMT", StringTool
				.round(parm.getDouble("NHI_AMT"), 2)); // 5 NHI_AMT
		// �����걨���
		parmG1.addData("SIN_DISEASE_CODE", parm.getValue("SIN_DISEASE_CODE")); // 6
		// SIN_DISEASE_CODE
		// �����ֱ���
		parmG1.addData("IN_DAY", parm.getValue("IN_DAY")); // 7 IN_DAY סԺ����
		parmG1.addData("OUT_TIME", parm.getValue("OUT_TIME")); // 8 OUT_TIME
		// ��Ժʱ��yyyymmdd
		parmG1.addData("SPECIAL_AMT", StringTool.round(parm
				.getDouble("SPECIAL_AMT"), 2)); // 9
		// SPECIAL_AMT
		// ������Ŀ���
		parmG1.addData("PARM_COUNT", 9);
		// System.out.println("�Ǿӷ��ý��㣺DataDown_czys����G1 �������:" + parmG1);
		TParm result = commInterFace("DataDown_czys", "G1", parmG1);
		// System.out.println("�Ǿӷ��ý��㣺DataDown_czys���� G1����:" + result);
		return result;
	}

	/**
	 * �����Ŀ�ֵ�����Ҫ��ѯ������
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	private TParm checkCode(String orderCode) {
		String sql = "SELECT SFXMBM, JX, GG, DW, YF, "
				+ "YL, SL, PZWH, BZJG, ZFBL1,"
				+ "KSSJ, JSSJ, YYSMBM, SPMC, FLZB1, LJZFBZ,"
				+ "XMLB, TXBZ, XMRJ FROM INS_RULE WHERE  SFXMBM='" + orderCode
				+ "'";
		// System.out.println("sql::::::" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getRow(0);
	}

	/**
	 * ҽ�������
	 * 
	 * @param icdCode
	 *            String
	 * @return TParm
	 */
	public String selInsICDCode(String icdCode) {
		TParm result = new TParm();
		int count = icdCode.length();
		String insIcdCode = "";
		for (int i = 0; i < count; i++) {
			icdCode = icdCode.substring(0, count - i);
			// System.out.println("ҽ�������"+icdCode);
			String selIcdCode = " SELECT ICD_CODE, ICD_CHN_DESC "
					+ "   FROM INS_DIAGNOSIS " + "  WHERE ICD_CODE = '"
					+ icdCode + "' ";
			// System.out.println("�����sql = "+selIcdCode);
			result = new TParm(TJDODBTool.getInstance().select(selIcdCode));
			if (result.getErrCode() < 0) {
				return "";
			}
			if (result.getCount() <= 0) {
				continue;
			} else {
				insIcdCode = result.getValue("ICD_CODE", 0);
				return insIcdCode;
			}
		}
		return insIcdCode;

	}
}
