package action.ins;

import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ҽ������
 * </p>
 * 
 * <p>
 * Description: �������� ִ�У�1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author pangben 2011-11-25
 * @version 1.0
 */
public class INSTJAction extends TAction {

//	/**
//	 * �������� ִ�У�1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
//	 * 
//	 * @param parm
//	 *            ���ýӿڷ��س��� U ���� A ���� 2011-11-25
//	 * @return
//	 */
//	public TParm insCommFunction(TParm parm) {
//		int insType = parm.getInt("INS_TYPE"); // 1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����
//		TParm result = new TParm();
//		TConnection connection = getConnection();
//		switch (insType) {
//
//		case 1:// ��ְ��ͨ
//			result = cityStaffCommon(parm, connection);
//			break;
//		case 2:// ��ְ����
//			result = cityStaffClement(parm, connection);
//			break;
//		case 3://�Ǿ�����
//			result = specialCityDenizen(parm, connection);
//			break;
//		}
//		if (result.getErrCode() < 0) {
//			err(result.getErrCode() + " " + result.getErrText());
//			connection.close();
//			return result;
//		}
//		connection.commit();
//		connection.close();
//		return result;
//	}

//	/**
//	 * ��ְ����
//	 * 
//	 * @param parm
//	 * @param connection
//	 * @return
//	 */
//	public TParm cityStaffClement(TParm parm, TConnection connection) {
//		return INSTJReg.getInstance().cityStaffClement(parm, connection);
//	}
//
//	/**
//	 * �Ǿ�����
//	 * 
//	 * @param parm
//	 * @param connection
//	 * @return
//	 */
//	public TParm specialCityDenizen(TParm parm, TConnection connection) {
//		return INSTJReg.getInstance().specialCityDenizen(parm, connection);
//	}
//
//	/**
//	 * ��ְ��ͨ
//	 * 
//	 * @param parm
//	 * @param connection
//	 * @return
//	 */
//	public TParm cityStaffCommon(TParm parm, TConnection connection) {
//
//		return INSTJReg.getInstance().cityStaffCommon(parm, connection);
//	}

	/**
	 * ɾ�������ݣ�INS_OPD_ORDER �� INS_OPD ���е�����
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteOldData(TParm parm) {
		TConnection connection = getConnection();
		TParm result = INSOpdTJTool.getInstance()
				.deleteINSOpd(parm, connection);// INSOpd ��
		if (result.getErrCode() < 0) {
			result.setErr(-1, "deleteOldData����:" + result.getErrName() + ":"
					+ result.getErrText());
			connection.close();
			return result;
		}
		result = INSOpdOrderTJTool.getInstance().deleteINSOpdOrder(parm,
				connection);// INSOpdOrder ��
		if (result.getErrCode() < 0) {
			result.setErr(-1, "deleteOldData����:" + result.getErrName() + ":"
					+ result.getErrText());
			connection.close();
			return result;
		}
		if (null == parm.getValue("OPB_FLG")) {
			result = INSMZConfirmTool.getInstance().deleteInsMZConfirm(parm,
					connection);// INSMZConfirm ��
			if (result.getErrCode() < 0) {
				result.setErr(-1, "deleteOldData����:" + result.getErrName()
						+ ":" + result.getErrText());
				connection.close();
				return result;
			}
		}
		//ɾ��ҽ����;״̬
		result=INSRunTool.getInstance().deleteInsRun(parm, connection);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "deleteOldData����:" + result.getErrName() + ":"
					+ result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm comm(TParm parm) {

		TConnection connection = getConnection();
		TParm invOrderParm = new TParm();
		// ִ�����INS_OPD_ORDER ������
		invOrderParm.setData("REGION_CODE", "1");
		invOrderParm.setData("CASE_NO", "1");
		invOrderParm.setData("SEQ_NO", "1"); // ���
		invOrderParm.setData("CONFIRM_NO", "1"); // �ʸ�ȷ�Ϻ�
		invOrderParm.setData("CHARGE_DATE", new TNull(String.class)); // �Ƽ�����
		// ҽ������----������
		invOrderParm.setData("NHI_ORDER_CODE", "1");
		// ҽ������
		invOrderParm.setData("ORDER_CODE", "1");
		// ҽ������
		invOrderParm.setData("ORDER_DESC", "1");
		invOrderParm.setData("OWN_RATE", "1"); // �Ը�����
		invOrderParm.setData("DOSE_CODE", "");
		invOrderParm.setData("STANDARD", ""); // �𸶱�׼
		invOrderParm.setData("PRICE", "1"); // ����
		invOrderParm.setData("QTY", "1"); // ����
		invOrderParm.setData("TOTAL_AMT", "1"); // �ܽ��-----������
		invOrderParm.setData("TOTAL_NHI_AMT", "1"); // ҽ���ܽ��-----������
		// REG_PARM.getDouble("OWN_PRICE", j) * REG_PARM.getDouble("QTY", j)
		invOrderParm.setData("OWN_AMT", "1"); // �Էѽ��-------����������
		invOrderParm.setData("ADDPAY_AMT", "1"); // �������
		invOrderParm.setData("REFUSE_AMT", 0.00); // �ܸ����
		invOrderParm.setData("REFUSE_REASON_CODE", new TNull(String.class)); // �ܸ�ԭ�����
		invOrderParm.setData("REFUSE_REASON_NOTE", new TNull(String.class)); // �ܸ�ԭ��ע��
		invOrderParm.setData("OP_FLG", "N"); // ����ע�ǣ�Y��������N����������:�Һ�
		invOrderParm.setData("CARRY_FLG", "N"); // ��Ժ��ҩע�ǣ�Y����ҩ��N������ҩ��
		invOrderParm.setData("PHAADD_FLG", "N"); // ����ע�ǣ�Y��������N����������
		invOrderParm.setData("ADDPAY_FLG", "N"); // �ۼ�����ע�ǣ�Y���ۼ�������N�����ۼ�������
		invOrderParm.setData("NHI_ORD_CLASS_CODE", new TNull(String.class));
		invOrderParm.setData("INSAMT_FLG", 0);// ���˱�־Ϊ0
		invOrderParm.setData("RX_SEQNO", new TNull(String.class));// ������
		invOrderParm.setData("OPB_SEQNO", new TNull(String.class));// �����
		invOrderParm.setData("REG_OPB_FLG", new TNull(String.class));// ����Һű��
		invOrderParm.setData("TAKE_QTY", new TNull(String.class));// ��ȡ����
		invOrderParm.setData("ROUTE", new TNull(String.class));
		invOrderParm.setData("HYGIENE_TRADE_CODE", new TNull(String.class));
		invOrderParm.setData("INS_CROWD_TYPE", new TNull(String.class));
		invOrderParm.setData("INS_PAT_TYPE", new TNull(String.class));
		invOrderParm.setData("ORIGSEQ_NO", new TNull(String.class));
		invOrderParm.setData("TAKE_DAYS", new TNull(String.class));
		invOrderParm.setData("INV_NO", new TNull(String.class)); // ��������ʱ��---???
		invOrderParm.setData("OPT_USER", "1"); // ��������ʱ��---???
		invOrderParm.setData("OPT_TERM", "1"); // // ҽ�����վݺ�
		TParm result = INSOpdOrderTJTool.getInstance().insertINSOpdOrder(
				invOrderParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		TParm invParm = new TParm();
		invParm.setData("REGION_CODE", "1"); // ҽԺ����
		invParm.setData("CASE_NO", "1");
		invParm.setData("CONFIRM_NO", "1"); // �ʸ�ȷ�Ϻ�
		invParm.setData("MR_NO", "1"); // ������
		invParm.setData("PHA_AMT", "1"); // ҩƷ�ѷ������
		invParm.setData("PHA_NHI_AMT", "1"); // ҽ��ҩƷ��
		invParm.setData("EXM_AMT", "1"); // ����
		invParm.setData("EXM_NHI_AMT", "1"); // ҽ������
		invParm.setData("TREAT_AMT", "1"); // ���Ʒѣ����Ʒѷ�����
		invParm.setData("TREAT_NHI_AMT", "1"); // ҽ�����Ʒѣ����Ʒ��걨��
		invParm.setData("OP_AMT", "1"); // �����ѷ������
		invParm.setData("OP_NHI_AMT", "1"); // �������걨���
		invParm.setData("BED_AMT", "1"); // ��λ��(��λ�ѷ������)
		invParm.setData("BED_NHI_AMT", "1"); // ҽ����λ��
		invParm.setData("MATERIAL_AMT", "1"); // ���Ϸ�
		invParm.setData("MATERIAL_NHI_AMT", "1"); // ҽ�����Ϸ�
		invParm.setData("OTHER_AMT", "1"); // ��������
		invParm.setData("OTHER_NHI_AMT", "1"); // ҽ����������
		invParm.setData("BLOODALL_AMT", "1"); // ��ȫѪ�������
		invParm.setData("BLOODALL_NHI_AMT", "1"); // ��ȫѪҽ�����
		invParm.setData("BLOOD_AMT", "1"); // �ɷ���Ѫ�������
		invParm.setData("BLOOD_NHI_AMT", "1"); // �ɷ���Ѫ�걨���
		invParm.setData("TOT_AMT", "1"); // �������----------���������������ܽ�
		invParm.setData("NHI_AMT", "1"); // ҽ�����----��������(�걨���)
		invParm.setData("START_STANDARD_AMTS", "1"); // �𸶱�׼ʣ���
		invParm.setData("START_STANDARD_AMT", "1"); // �𸶱�׼ʣ���
		invParm.setData("OTOT_PAY_AMT", "1"); // ר�����֧��
		invParm.setData("ADD_AMT", "1"); // �������
		invParm.setData("OWN_AMT", "1"); // �Էѽ��
		invParm.setData("INS_STANDARD_AMT", "1"); // �𸶱�׼���
		invParm.setData("TRANBLOOD_OWN_AMT", "1"); // ��Ѫ�Ը����
		invParm.setData("APPLY_AMT", "1"); // �걨���
		invParm.setData("OTOT_AMT", "1"); // ר�����֧���޶�
		invParm.setData("OINSTOT_AMT", "1"); //
		invParm.setData("INS_DATE", new TNull(String.class)); // ҽ������ʱ��
		invParm.setData("INSAMT_FLG", 1); // �籣���ʱ�־INS_OPD��INSAMT_FLG���˱�־Ϊ1
		invParm.setData("ACCOUNT_PAY_AMT", new TNull(String.class)); //
		invParm.setData("UNACCOUNT_PAY_AMT", new TNull(String.class)); //
		invParm.setData("ACCOUNT_LEFT_AMT", new TNull(String.class)); //
		invParm.setData("REFUSE_AMT", new TNull(String.class)); //
		invParm.setData("REFUSE_ACCOUNT_PAY_AMT", new TNull(String.class)); //
		invParm.setData("REFUSE_DESC", new TNull(String.class)); //
		invParm.setData("CONFIRM_F_USER", new TNull(String.class)); //
		invParm.setData("CONFIRM_F_DATE", new TNull(String.class)); //
		invParm.setData("CONFIRM_S_USER", new TNull(String.class)); //
		invParm.setData("CONFIRM_S_DATE", new TNull(String.class)); //
		invParm.setData("SLOW_DATE", new TNull(String.class)); //
		invParm.setData("SLOW_PAY_DATE", new TNull(String.class)); //
		invParm.setData("REFUSE_CODE", new TNull(String.class)); //
		invParm.setData("REFUSE_OTOT_AMT", new TNull(String.class)); //
		invParm.setData("RECEIPT_NO", new TNull(String.class)); //
		invParm.setData("MZ_DIAG", new TNull(String.class)); //
		invParm.setData("DRUG_DAYS", new TNull(String.class)); //
		invParm.setData("INS_STD_AMT", new TNull(String.class)); //
		invParm.setData("INS_CROWD_TYPE", new TNull(String.class)); //
		invParm.setData("INS_PAT_TYPE", new TNull(String.class)); //
		invParm.setData("REG_OPB_FLG", new TNull(String.class)); //
		invParm.setData("OWEN_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("REDUCE_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("REAL_OWEN_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("SALVA_PAY_SCALE", new TNull(String.class)); //
		invParm.setData("BASEMED_BALANCE", new TNull(String.class)); //
		invParm.setData("INS_BALANCE", new TNull(String.class)); //
		invParm.setData("ISSUE", new TNull(String.class)); //
		invParm.setData("BCSSQF_STANDRD_AMT", new TNull(String.class)); //
		invParm.setData("PERCOPAYMENT_RATE_AMT", new TNull(String.class)); //
		invParm.setData("INS_HIGHLIMIT_AMT", new TNull(String.class)); //
		invParm.setData("TOTAL_AGENT_AMT", new TNull(String.class)); //
		invParm.setData("FLG_AGENT_AMT", new TNull(String.class)); //
		invParm.setData("APPLY_AMT_R", new TNull(String.class)); //
		invParm.setData("FLG_AGENT_AMT_R", new TNull(String.class)); //
		invParm.setData("REFUSE_DATE", new TNull(String.class)); //
		invParm.setData("REAL_PAY_LEVEL", new TNull(String.class)); //
		invParm.setData("VIOLATION_REASON_CODE", new TNull(String.class)); //
		invParm.setData("ARMY_AI_AMT", new TNull(String.class)); //
		invParm.setData("SERVANT_AMT", new TNull(String.class)); //
		invParm.setData("INS_PAY_AMT", new TNull(String.class)); //
		invParm.setData("UNREIM_AMT", new TNull(String.class)); //
		invParm.setData("REIM_TYPE", new TNull(String.class)); //
		invParm.setData("OPT_USER", "1"); //
		invParm.setData("OPT_TERM", "1"); //
		result = INSOpdTJTool.getInstance().insertINSOpd(invParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		TParm p = new TParm();
		p.setData("REGION_CODE", "1");
		p.setData("CASE_NO", "1");
		p.setData("CONFIRM_NO", "1");
		result = INSOpdTJTool.getInstance().updateINSOpd(p, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * ִ��ҽ���������޸��վ�ֵ
	 * @param parm
	 * @return
	 */
	public TParm updateINSReceiptNo(TParm parm){
		TConnection connection = getConnection();
		TParm result = INSRunTool.getInstance().deleteInsRun(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		result=INSOpdTJTool.getInstance().updateINSReceiptNo(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * ִ��ҽ���������޸��վ�ֵ
	 * @param parm
	 * @return
	 */
	public TParm updateINSPrintNo(TParm parm){
		TConnection connection = getConnection();
		TParm result = INSRunTool.getInstance().deleteInsRun(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		result=INSTJFlow.getInstance().updateInsAmtFlgPrint(parm, parm.getValue("RECP_TYPE"),connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
		}
		connection.commit();
		connection.close();
		return result;
	}
}
