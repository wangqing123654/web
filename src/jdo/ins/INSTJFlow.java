package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import jdo.opd.DiagRecTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: �Һ�ҽ������
 * </p>
 * 
 * <p>
 * Description: �Һ�ҽ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2011-11-07
 * @version 1.0
 */
public class INSTJFlow extends TJDOTool {
	// private TParm ruleParm;// ��Ŀ�ֵ�
	DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private String message = "�ֽ�֧��";
	/**
	 * ʵ��
	 */
	public static INSTJFlow instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSTJFlow
	 */
	public static INSTJFlow getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJFlow();
		return instanceObject;
	}

	// У���Ƿ�Ϊ��
	String[] insMZConfirm = { "INSCARD_NO", "INSCARD_PASSWORD", "OWN_NO",
			"BANK_NO", "IDNO", "PAT_NAME", "SEX_CODE", "PAT_AGE", "CTZ_CODE",
			"UNIT_NO", "UNIT_CODE", "UNIT_DESC", "INS_CODE", "TOT_AMT",
			"OTOT_AMT", "SPC_MEMO", "PAY_KIND", "DISEASE_CODE",
			"INS_CROWD_TYPE", "INS_PAT_TYPE", "MED_SALVA_FLG",
			"INS_SURPLUS_AMT", "SALVA_SURPLUS_AMT", " INS_PAY_LEVEL",
			"REAL_PAY_LEVEL", "OWEN_PAY_SCALE", "REDUCE_PAY_SCALE",
			"REAL_OWEN_PAY_SCALE", "SALVA_PAY_SCALE", "MED_HELP_COMPANY",
			"ENTERPRISES_TYPE", "SPECIAL_PAT", "OWN_PAY_RATE",
			"PERSON_ACCOUNT_AMT", "SP_PRESON_MEMO", "AUTO_FLG" };
	String[] insOpdOrder = { "SEQ_NO", "CHARGE_DATE", "NHI_ORDER_CODE",
			"OWN_RATE", "DOSE_CODE", "STANDARD", "PRICE", "QTY", "TOTAL_AMT",
			"TOTAL_NHI_AMT", "OWN_AMT", "ADDPAY_AMT", "REFUSE_AMT",
			"REFUSE_REASON_CODE", "REFUSE_REASON_NOTE", "OP_FLG", "CARRY_FLG",
			"PHAADD_FLG", "ADDPAY_FLG", "NHI_ORD_CLASS_CODE", "INSAMT_FLG",
			"RX_SEQNO", "OPB_SEQNO", "TAKE_QTY", "ROUTE", "HYGIENE_TRADE_CODE",
			"INS_CROWD_TYPE", "INS_PAT_TYPE", "ORIGSEQ_NO", "TAKE_DAYS",
			"INV_NO", "RECP_TYPE", "SPECIAL_CASE_DESC" };

	public INSTJFlow() {
		super();
		this.onInit();
		// String sql = "SELECT * FROM INS_RULE";
		// ruleParm = new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ������;״̬ ִ���Զ����˲���
	 */
	private TParm accountComm(TParm parm) {
		// ��Ҫ�Զ����˵�����
		TParm insOpdParm = INSOpdTJTool.getInstance().selectAutoAccount(parm);
		if (insOpdParm.getErrCode() < 0) {
			// exeProgress=true;
			return insOpdParm;
		}
		if (insOpdParm.getCount() <= 0) {
			// exeProgress=true;
			return insOpdParm;
		}
		insOpdParm.setData("INS_TYPE", parm.getValue("INS_TYPE"));// 1.��ְ��ͨ
		// 2.��ְ����
		// 3.�Ǿ�����
		insOpdParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		insOpdParm.setData("ACCOUNT_DATE", df1.format(SystemTool.getInstance()
				.getDate()));// ����ʱ��
		return new TParm(autoAccountComm(insOpdParm));
	}

	/**
	 * ���÷ָ���÷ָ���ins_opd_order ����ϸ�ϴ����� ִ�з��÷ָ� ��ְ��������DataDown_sp1 ���� B
	 * �Ǿӣ�������DataDown_sp1 ���� G ִ���ϴ���ϸ ��ְ��ͨ������: ����DataUpload,��B������
	 * ��ְ���أ�����DataUpload, ���� C �Ǿ����أ�����DataDown_cmts, ���� F ��ְ �Ǿ� ���ý��� ���ý���
	 * ���㡢�˻�ȷ�ϲ���
	 * 
	 * @param connection
	 * @return
	 */
	public TParm comminuteFeeAndInsOrder(TParm parm) {
		// TParm ruleParm = parm.getParm("ruleParm");
		// �ж��Ƿ������;����
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			if(null!=parm.getValue("OPB_RECP_TYPE") && parm.getValue("OPB_RECP_TYPE").equals("Y")){
				//�����շ���;״̬��������ִ�к���Ľӿڵ��ã���������ظ��շѵ��߼�
				result.setData("MESSAGE", "������;��Ϣ,��ִ���Զ����˲���");
				result.setData("NO_EXE_FLG","Y");
				return result;
			}
			// ִ���Զ����˲���
			result = accountComm(parm);
			// System.out.println("ִ���Զ�����");
			// System.out.println(result.getErrCode()+":"+result.getErrText());
			TParm runParm = new TParm();
			runParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			runParm.setData("EXE_USER", parm.getValue("OPT_USER"));
			runParm.setData("EXE_TERM", parm.getValue("OPT_TERM"));
			runParm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));
			result = INSRunTool.getInstance().deleteInsRun(runParm);// ɾ����;����
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
		}
		// �����;����
		parm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));// ���
		if (!INSTJFlow.getInstance().insertRun(parm, "test")) {
			err(result.getErrText());
			return result;
		}
		int insReadType = parm.getInt("CROWD_TYPE");// ҽ����������
		TParm opbReadCardParm = parm.getParm("opbReadCardParm");// L��������
		// ��� ���޸� INS_MZ_COMFIRM ������
		result = INSTJFlow.getInstance().onSaveInsMZConfirm(opbReadCardParm,
				parm, parm.getInt("INS_TYPE"));
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result;
		}
		int type = parm.getInt("INS_TYPE");// 1 .��ְ��ͨ 2. ��ְ���� 3. �Ǿ�����
		//System.out.println("-----------readINSParm---------" + parm);
		// System.out.println("type == :::"+type );// ��ְ��ͨ��ҪУ��
		if (type == 1) {
			if (opbReadCardParm.getDouble("TOT_AMT") <= 0
					&& opbReadCardParm.getDouble("OTOT_AMT") <= 0) {
				result.setData("MESSAGE", "ר�����ʣ���Ϊ0,�ֽ�֧��");
				return result;
			}
		} else if (type == 2 || type == 3) {// ��ְ���Ǿ�����У��
			if (opbReadCardParm.getValue("MED_SALVA_FLG").equals("0")) {
				System.out.println("������ҽ�ƾ���");
				// result.setData("MESSAGE", "������ҽ�ƾ���,�Է�֧��");
				// return result;
			}
		}

		// ���÷ָ�
		TParm comminuteFeeParm = INSTJTool.getInstance()
				.comminuteFeeAndInsOrder(parm, opbReadCardParm, insReadType,
						type);
		if (comminuteFeeParm.getErrCode() < 0) {
			err(comminuteFeeParm.getErrCode() + " "
					+ comminuteFeeParm.getErrText());
			// connection.close();
			return comminuteFeeParm;
		}
		// double addAmt = 0.00;
		// for (int i = 0; i < comminuteFeeParm.getCount("ADD_AMT"); i++) {
		// if (null != comminuteFeeParm.getValue("ADDPAY_FLG", i)
		// && comminuteFeeParm.getInt("ADDPAY_FLG", i) == 1) {
		// addAmt += comminuteFeeParm.getDouble("ADD_AMT", i);//
		// �ۼ�����������ֵ,����ۼ��������
		// }
		//
		// }
		// System.out.println("�ۼ�������������" + addAmt);
		TParm spParm = new TParm();
		if (parm.getValue("RECP_TYPE").equals("OPB")) {
			// �ۼ���������
			spParm = addExe(parm, comminuteFeeParm);
			if (spParm.getErrCode() < 0) {
				return spParm;

			}

		}

		// �����ϴ���ϸ
		spParm = detailUpLoad(parm, comminuteFeeParm, type);
		if (spParm.getErrCode() < 0) {
			return spParm;
		}
		TParm settlementDetailsParm = new TParm();
		//TParm REG_PARM = parm.getParm("REG_PARM");// ��ùҺ���������������
		// ��������ҩ����
		int day = 0;
		String sqlMax = "SELECT MAX(TAKE_DAYS) AS TAKE_DAYS FROM OPD_ORDER WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RECEIPT_NO IS NULL";
		TParm maxParm = new TParm(TJDODBTool.getInstance().select(sqlMax));
		if(maxParm.getErrCode()<0){
			err(maxParm.getErrText());
			return maxParm;
		}
		if(null!=maxParm.getValue("TAKE_DAYS",0)){
			day=maxParm.getInt("TAKE_DAYS",0);
		}
		parm.setData("TAKE_DAYS", day);
		// ���ҽ������ ��� ҽ������ ���Ʊ����
		TParm diagRecparm = DiagRecTool.getInstance().queryInsData(parm);
		String sql = "SELECT A.INS_DEPT_CODE,B.DR_QUALIFY_CODE FROM SYS_DEPT A ,SYS_OPERATOR B,REG_PATADM C WHERE "
				+ "A.DEPT_CODE=C.REALDEPT_CODE AND B.USER_ID =C.REALDR_CODE AND B.USER_ID='"
				+ parm.getValue("DR_CODE") + "'";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(sql));
		parm.setData("diagRecparm", diagRecparm.getData());
		parm.setData("deptParm", deptParm.getRow(0).getData());
		// System.out.println("���ҽ������ ��� ҽ������ ���Ʊ����deptParm::::::::"+deptParm);
		switch (type) {
		case 1:
			// ���ý���-------��ó��� ִ��ҽ�����ָ����
			// ����DataDown_sp, ��������ϴ����ף�M������
			settlementDetailsParm = insFeeSettleChZPt(parm);// ���ý������
			if (settlementDetailsParm.getErrCode() < 0) {
				err(settlementDetailsParm.getErrCode() + " "
						+ settlementDetailsParm.getErrText());
				return settlementDetailsParm;
			}
			// �ʻ�֧��ȷ�Ͻ��׷��ز���
			// ����DataDown_rs����R����
			parm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJFlow.getInstance().insPayAccountChZPt(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
			break;
		case 2:
			// ���ý��� ����DataDown_mts, ��������ϴ����ף�F������

			settlementDetailsParm = insFeeSettleChZMtAndChJMt(parm);
			if (settlementDetailsParm.getErrCode() < 0) {
				err(settlementDetailsParm.getErrCode() + " "
						+ settlementDetailsParm.getErrText());
				return settlementDetailsParm;
			}
			// ͳ��֧��ȷ�Ͻ���:����DataDown_mts, ����ͳ��֧��ȷ�Ͻ��ף�G��
			parm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJFlow.getInstance().insPayAccountChZMt(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
			break;
		case 3:
			// ���ý��� ����DataDown_cmts, ��������ϴ����ף�F������
			settlementDetailsParm = insFeeSettleChZMtAndChJMt(parm);
			if (settlementDetailsParm.getErrCode() < 0) {
				err(settlementDetailsParm.getErrCode() + " "
						+ settlementDetailsParm.getErrText());
				return settlementDetailsParm;
			}
			// ͳ��֧��ȷ�Ͻ���:����DataDown_cmts, ����ͳ��֧��ȷ�Ͻ��ף�G��
			parm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJFlow.getInstance().insPayAccountChJMt(parm);
			if (result.getErrCode() < 0) {
				err(result.getErrText());
				return result;
			}
			break;
		}

		// �޸�INS_OPD �� ����
		if (!updateINSopdSettle(parm)) {
			result.setErr(-1, "ִ���޸�INS_OPD�����ݴ���");
			// cancelBalance(parm);
			return result;
		}
		// System.out.println("�ʻ�֧��ȷ�Ͻ��׷��ز���");
		result.setData("comminuteFeeParm", comminuteFeeParm.getData());// ���÷ָ�����
		result
				.setData("settlementDetailsParm", settlementDetailsParm
						.getData());// ���ý���

		return result;
	}

	/**
	 * �ۼ�����ִ�в���
	 * 
	 * @return
	 */
	private TParm addExe(TParm parm, TParm comminuteFeeParm) {
		TParm spParm = new TParm();
		// �ۼ�������ѯ���н��
		TParm result = new TParm();
		TParm addParm = INSOpdOrderTJTool.getInstance().queryAddInsOpdOrder(
				parm);
		TParm seqParm = INSOpdOrderTJTool.getInstance().selectMAXSeqNo(parm);
		if (addParm.getErrCode() < 0) {
			return addParm;
		}
		if (addParm.getDouble("TOTAL_AMT", 0) <= 0) {
			return new TParm();
		}
		spParm.setData("ADDPAY_ADD", addParm.getDouble("TOTAL_AMT", 0));// �ۼ����������ܶ�
		spParm.setData("HOSP_START_DATE", df1.format(SystemTool.getInstance()
				.getDate()));
		int type = parm.getInt("CROWD_TYPE");// ҽ����������
		if (type == 1)// ��ְ
			result = INSTJTool.getInstance().DataDown_sp1_C(spParm);
		else {
			result = INSTJTool.getInstance().DataDown_sp1_H(spParm);
		}
		// ��ӳ�����Ϣ
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// result.setData("MESSAGE", "�ֽ�֧��");
			return result;
		}
		// ����ۼ�����
		spParm = INSTJTool.getInstance().exeAdd(parm, addParm, result, seqParm);
		if (spParm.getErrCode() < 0) {
			return spParm;
		}
		parm.setData("TOT_AMT", addParm.getDouble("TOTAL_AMT", 0));
		return spParm;
	}

	/**
	 * ��ϸ�ϴ�
	 * 
	 * @param parm
	 * @return
	 */
	private TParm detailUpLoad(TParm parm, TParm comminuteFeeParm,
			int insOrderType) {
		TParm opdOrderParm = new TParm();
		opdOrderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		opdOrderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));
		String nhiRegionCode = parm.getValue("REGION_CODE");
		opdOrderParm = INSOpdOrderTJTool.getInstance().selectOpdOrder(
				opdOrderParm);// ���÷ָ���ѯ��ִ���ϴ�����
		if (opdOrderParm.getErrCode() < 0) {
			return opdOrderParm;
		}
		TParm result = new TParm();
		TParm interFaceParm = new TParm();// �ӿڻز�
		TParm tempParm = null;// �м�����
		TParm sumUpLoadParm = new TParm();// �ϴ�����
		boolean flg = true;
		for (int i = 0; i < opdOrderParm.getCount(); i++) {
			tempParm = opdOrderParm.getRow(i);
			// System.out.println("�ϴ���ϸ:::"+tempParm);
			flg = INSTJTool.getInstance().upInterfaceINSOrderParm(tempParm,
					sumUpLoadParm, nhiRegionCode);
			// ��ӳ�����Ϣ
			if (!flg) {
				result.setErr(-1, "��ϸ�ϴ��ӿڵ��ó��ִ���");
				// result.setData("MESSAGE", "�ֽ�֧��");
				return result;
			}

		}
		// System.out.println("insOrderType::::"+insOrderType);
		switch (insOrderType) {
		case 1:// ��ְ��ͨ
			interFaceParm = INSTJTool.getInstance().DataUpload_B(sumUpLoadParm);
			break;
		case 2:// ��ְ����
			interFaceParm = INSTJTool.getInstance().DataUpload_C(sumUpLoadParm);
			break;
		case 3:// �Ǿ�����
			// System.out.println("�Ǿ����ؽ���---------------------");
			interFaceParm = INSTJTool.getInstance().DataUpload_F(sumUpLoadParm);
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(interFaceParm)) {
			// result.setData("MESSAGE", "�ֽ�֧��");
			return interFaceParm;
		}
		// ɾ����������
		result = INSOpdTJTool.getInstance().deleteINSOpd(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ����INS_opd(comminuteFeeParm)
		if (!INSTJTool.getInstance().insterInsOpdParm(comminuteFeeParm, parm)) {
			result.setErr(-1, "���÷ָ�ӿڵ��ó��ִ���");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ��ͨ ���ý��� ����DataDown_sp, ��������ϴ����ף�M������
	 */
	public TParm insFeeSettleChZPt(TParm parm) {
		TParm settlementDetailsParm = INSTJTool.getInstance().DataDown_sp_M(
				parm, parm.getValue("REG_TYPE"));
		if (!INSTJTool.getInstance().getErrParm(settlementDetailsParm)) {
			// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
			cancelBalance(parm);
			return settlementDetailsParm;
		}
		return settlementDetailsParm;
	}

	/**
	 * ��ְ\�Ǿ����� ���ý��� ����DataDown_mts, ��������ϴ����ף�F������
	 */
	public TParm insFeeSettleChZMtAndChJMt(TParm parm) {
		TParm identificationParm = parm.getParm("opbReadCardParm");// DataDown_mts_E
		// AND
		// DataDown_cmts_E
		// ��������
		TParm settlementDetailsParm = null;
		// System.out.println("�������:::"+parm);
		if (parm.getInt("CROWD_TYPE") == 1) {

			settlementDetailsParm = INSTJTool.getInstance().DataDown_mts_F(
					parm, identificationParm, parm.getValue("REG_TYPE"));
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			settlementDetailsParm = INSTJTool.getInstance().DataDown_cmts_F(
					parm, identificationParm, parm.getValue("REG_TYPE"));
		}

		if (!INSTJTool.getInstance().getErrParm(settlementDetailsParm)) {
			// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
			cancelBalance(parm);
			return settlementDetailsParm;
		}

		return settlementDetailsParm;
	}

	/**
	 * �˻�֧�� ����DataDown_rs����R����
	 * 
	 * @return
	 */
	public TParm insPayAccountChZPt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		TParm accountPayParm = INSTJTool.getInstance().DataDown_rs_R(parm,
				settlementDetailsParm);
		if (!INSTJTool.getInstance().getErrParm(accountPayParm)) {
			cancelBalance(parm);
			return accountPayParm;
		}
		// �˻�֧��ȷ�Ϻ�,�޸Ķ��˱�־ ����1
		if (accountPayParm.getInt("INSAMT_FLG") == 1) {
			// �޸�INS_OPD_ORDER����INSAMT_FLG���˱�־Ϊ1
			if (!updateInsAmtFlg(parm, "1", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				accountPayParm.setErr(-1, "�ʻ�֧��ȷ�Ͻ��׽ӿڵ��ó��ִ���");
				return accountPayParm;
			}
		}
		return accountPayParm;
	}

	/**
	 * �޸�INS_OPD �� ����
	 * 
	 * @param connection
	 * @return
	 */
	public boolean updateINSopdSettle(TParm parm) {
		// INS_OPD ������������
		// System.out.println("------readINSParm---------" + parm);
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		// System.out.println("------interFaceParm---------"
		// + settlementDetailsParm);
		TParm result = INSTJTool.getInstance().balanceParm(parm,
				settlementDetailsParm); // ��ӽ��㵵����
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			cancelBalance(parm);// ����ȡ��
			return false;
		}
		return true;
	}

	/**
	 * ��ְ���� ͳ��֧��ȷ�Ͻ��ף�����DataDown_mts, ����ͳ��֧��ȷ�Ͻ��ף�G��
	 * 
	 * @return
	 */
	public TParm insPayAccountChZMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		TParm overallPayParm = INSTJTool.getInstance().DataDown_mts_G(parm,
				settlementDetailsParm);
		if (!INSTJTool.getInstance().getErrParm(overallPayParm)) {
			// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
			// System.out.println("11111111111111111111111111111:::ͳ��֧��ȷ�Ͻ���");
			cancelBalance(parm);
			overallPayParm.setData("MESSAGE", "�ֽ�֧��");
			return overallPayParm;
		}
		// ͳ��֧��ȷ�Ͻ��׺��޸�INS_OPD �� INS_OPD_ORDER����INSAMT_FLG���˱�־Ϊ1
		if (overallPayParm.getInt("SOCIAL_FLG") == 1) {
			// �޸�INS_OPD �� ����
			// �޸�INS_OPD_ORDER����INSAMT_FLG���˱�־Ϊ1
			if (!updateInsAmtFlg(parm, "1", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				overallPayParm.setData("MESSAGE", "�ֽ�֧��");
				overallPayParm.setErr(-1, "ͳ��֧��ȷ�Ͻ��׽ӿڵ��ó��ִ���");
				return overallPayParm;
			}
		}
		return overallPayParm;
	}

	/**
	 * �Ǿ����� ͳ��֧��ȷ�Ͻ���:����DataDown_cmts, ����ͳ��֧��ȷ�Ͻ��ף�G��
	 * 
	 * @param connection
	 * @return
	 */
	public TParm insPayAccountChJMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		TParm overallPayParm = INSTJTool.getInstance().DataDown_cmts_G(parm,
				settlementDetailsParm);
		if (!INSTJTool.getInstance().getErrParm(overallPayParm)) {
			// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
			cancelBalance(parm);
			overallPayParm.setData("MESSAGE", "�ֽ�֧��");
			return overallPayParm;
		}
		if (overallPayParm.getInt("SOCIAL_FLG") == 1) {
			// �޸�INS_OPD �� ����
			// �޸�INS_OPD ����INSAMT_FLG���˱�־Ϊ1
			if (!updateInsAmtFlg(parm, "1", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				overallPayParm.setData("MESSAGE", "�ֽ�֧��");
				overallPayParm.setErr(-1, "ͳ��֧��ȷ�Ͻ��׽ӿڵ��ó��ִ���");
				return overallPayParm;
			}
		}
		return overallPayParm;
	}

	/**
	 * ����ȡ��
	 * 
	 * @return
	 */
	public TParm cancelBalance(TParm parm) {
		// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������
		TParm opbReadCardParm = parm.getParm("opbReadCardParm");// DataDown_sp_L\DataDown_mts_E\
		// DataDown_cmts_E
		TParm result1 = null;
		switch (parm.getInt("INS_TYPE")) {
		case 1:
			result1 = INSTJTool.getInstance().DataDown_sp_S(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// ȡ��
			break;
		case 2:
			result1 = INSTJTool.getInstance().DataDown_mts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// ȡ��
			break;
		case 3:
			result1 = INSTJTool.getInstance().DataDown_cmts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// ȡ��
			break;
		}
		// System.out.println(" ����ȡ��result1::::"+result1);
		if (!INSTJTool.getInstance().getErrParm(result1)) {
			return result1;
		}
		return result1;
	}

	/**
	 * ����ȡ��
	 * 
	 * @return
	 */
	public TParm cancelBalance(TParm parm, int type) {
		// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������
		TParm opbReadCardParm = parm.getParm("opbReadCardParm");// DataDown_sp_L\DataDown_mts_E\
		// DataDown_cmts_E
		TParm result1 = null;
		switch (type) {
		case 1:
			result1 = INSTJTool.getInstance().DataDown_sp_S(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// ȡ��
			break;
		case 2:
			result1 = INSTJTool.getInstance().DataDown_mts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// ȡ��
			break;
		case 3:
			result1 = INSTJTool.getInstance().DataDown_cmts_J(
					opbReadCardParm.getValue("CONFIRM_NO"),
					parm.getValue("REGION_CODE"), "30");// ȡ��
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(result1)) {
			return result1;
		}
		// ִ��ɾ��INS_OPD \INS_OPD_ORDER\INS_RUN����
		result1 = delInsOpdAndOpdOrder(parm);
		return result1;
	}
	/**
	 * ҽ������״̬INSAMT_FLG=1������ ����Ʊ�ݵ����ݣ�ִ��ȷ�Ͻӿ�
	 * @param parm
	 * @param type
	 * @return
	 * =============pangben 2013-7-29
	 */
	public TParm confirmBalance(TParm parm,TParm mzConfirmParm,int type){
		// ���ѳ�Ʊ���������ѣ���Ʊ��ʱ����˱�־������1 ����Ϊ3ʱ��Ʊ
		// ����������ִ�д�Ʊ�������� ���˱�־�� 3ʱ�ſ��Դ�Ʊ
		// ���� ����Ϊ1 ִ�н��㽻�׽ӿ�
		TParm interFaceParmOne=new TParm();
		if (parm.getInt("INSAMT_FLG") == 1) {
			// ���㽻��
			switch (type) {
			case 1:
				interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(parm);
				break;
			case 2:
				interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(parm,
						mzConfirmParm);
				break;
			case 3:
				interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(
						parm, mzConfirmParm);
				break;
			}
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				return interFaceParmOne;
			} else {
				if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
					cancelBalance(parm);
					interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
					return interFaceParmOne;
				}
				//======pangben 2013-3-13 �����;ɾ��
				TParm result =INSRunTool.getInstance().deleteInsRunConcel(parm);//ȡ������ɾ����;״̬
				if (result.getErrCode() < 0) {
					// System.out.println("err:" + parm.getErrText());
					return result;
				}
			}
		}
		return interFaceParmOne;
	}
	/**
	 * ��ְ��ͨ ����ȷ�Ϸ��ز���:����DataDown_sp���������ȷ�Ͻ��ף�R������
	 * 
	 * @return
	 */
	public TParm insSettleConfirmChZPt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		TParm interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(parm,
				settlementDetailsParm);
		// IFʧ�ܣ��ٴε��ý���ȷ�Ͻ��ף��������ʧ�ܣ������ԭ������籣Э�̽��
		if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
			interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(parm,
					settlementDetailsParm);
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
				// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
				cancelBalance(parm);
				interFaceParmOne.setData("MESSAGE", "���ý���ȷ�Ͻ���ʧ��,�����ԭ������籣Э�̽��");
				interFaceParmOne.setData("FLG", "Y");// ����ȷ��ʧ�� ������ִ������ĳ���
				return interFaceParmOne;
			}
		}
		if (interFaceParmOne.getInt("INSAMT_FLG") == 3) {
			// ����ȷ�Ϻ��޸�INS_OPD �� INS_OPD_ORDER����INSAMT_FLG���˱�־Ϊ3
			if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
				return interFaceParmOne;
			}
		}
		return interFaceParmOne;
	}

	/**
	 * ��ְ���� ����ȷ�Ϸ��ز���:����DataDown_mts���������ȷ�Ͻ��ף�I������
	 * 
	 * @return
	 */
	public TParm insSettleConfirmChZMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		TParm identificationParm = parm.getParm("opbReadCardParm");// ˢ������
		TParm interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(parm,
				settlementDetailsParm, identificationParm);
		// IFʧ�ܣ��ٴε��ý���ȷ�Ͻ��ף��������ʧ�ܣ������ԭ������籣Э�̽��
		if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
			interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(parm,
					settlementDetailsParm, identificationParm);
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
				interFaceParmOne.setData("MESSAGE", "���ý���ȷ�Ͻ���ʧ��,�����ԭ������籣Э�̽��");
				interFaceParmOne.setData("FLG", "Y");// ����ȷ��ʧ�� ������ִ������ĳ���
				// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
				cancelBalance(parm);
				return interFaceParmOne;
			}
		}
		// ����ȷ�Ϻ�,�޸Ķ��˱�־ ����3
		if (interFaceParmOne.getInt("SOCIAL_FLG") == 3) {
			// ����ȷ�Ϻ��޸�INS_OPD �� INS_OPD_ORDER����INSAMT_FLG���˱�־Ϊ3
			if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
				interFaceParmOne.setData("FLG", "Y");// ����ȷ��ʧ�� ������ִ������ĳ���
				return interFaceParmOne;
			}
		}
		return interFaceParmOne;
	}

	/**
	 * �Ǿ����� ����ȷ�Ϸ��ز���:����DataDown_cmts���������ȷ�Ͻ��ף�I������
	 * 
	 * @return
	 */
	public TParm insSettleConfirmChJMt(TParm parm) {
		TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		TParm identificationParm = parm.getParm("opbReadCardParm");// ˢ������
		TParm interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(parm,
				settlementDetailsParm, identificationParm);
		// IFʧ�ܣ��ٴε��ý���ȷ�Ͻ��ף��������ʧ�ܣ������ԭ������籣Э�̽��
		if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
			interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(parm,
					settlementDetailsParm, identificationParm);
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
				interFaceParmOne.setData("MESSAGE", "���ý���ȷ�Ͻ���ʧ��,�����ԭ������籣Э�̽��");
				interFaceParmOne.setData("FLG", "Y");// ����ȷ��ʧ�� ������ִ������ĳ���
				// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������ ----ֻ���շ�û���˷�
				cancelBalance(parm);
				return interFaceParmOne;
			}
		}
		// ����ȷ�Ϻ��޸�INS_OPD ����INSAMT_FLG���˱�־Ϊ3
		if (interFaceParmOne.getInt("SOCIAL_FLG") == 3) {
			// ����ȷ�Ϻ��޸�INS_OPD �� INS_OPD_ORDER����INSAMT_FLG���˱�־Ϊ3
			if (!updateInsAmtFlg(parm, "3", parm.getValue("RECP_TYPE"))) {
				cancelBalance(parm);
				interFaceParmOne.setErr(-1, "�ٴε��ý���ȷ�Ͻ���ʧ��");
				interFaceParmOne.setData("MESSAGE", "���ý���ȷ�Ͻ���ʧ��,�����ԭ������籣Э�̽��");
				interFaceParmOne.setData("FLG", "Y");// ����ȷ��ʧ�� ������ִ������ĳ���
				return interFaceParmOne;
			}
		}
		return interFaceParmOne;
	}

	/**
	 * �޸�INS_OPD ������
	 */
	private boolean updateInsOpds(TParm parm, String status, String type) {
		TParm result = updateInsOpd(parm, status, type);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// ��ӽ���ӿ�ʧ��ʵ�ֽ���ȡ������
			cancelBalance(parm);
			return false;
		}
		return true;
	}

	/**
	 * �޸�INS_OPD ������
	 */
	public TParm updateInsOpd(TParm readINSParm, String status, String type) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		// ����INSAMT_FLG���˱�־Ϊ3
		TParm result = INSOpdTJTool.getInstance().updateINSOpd(parm);// �޸�INS_OPD
		if (result.getErrCode() < 0) {
			err("updateInsOpd �޸Ķ��˱�־ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * �޸�INS_OPD ������
	 */
	public TParm updateInsOpd(TParm readINSParm, String status, String type,
			TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		// ����INSAMT_FLG���˱�־Ϊ3
		TParm result = INSOpdTJTool.getInstance()
				.updateINSOpd(parm, connection);// �޸�INS_OPD
		if (result.getErrCode() < 0) {
			err("updateInsOpd �޸Ķ��˱�־ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * NSAMT_FLG���˱�־��Ʊ�ݺ�
	 * 
	 * @param readINSParm
	 * @param status
	 * @param type
	 * @param connection
	 * @return
	 */
	private TParm updateINSOpdPrint(TParm readINSParm, String type,
			TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, "", type);

		TParm result = INSOpdTJTool.getInstance().updateINSOpdPrint(parm,
				connection);// �޸�INS_OPD ����INSAMT_FLG���˱�־Ϊ3
		return result;
	}

	private TParm getUpdateParm(TParm readINSParm, String status, String type) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", readINSParm.getData("CASE_NO"));
		parm.setData("RECP_TYPE", type);
		parm.setData("CONFIRM_NO", readINSParm.getData("CONFIRM_NO"));
		parm.setData("INSAMT_FLG", status);// ����״̬
		parm.setData("INV_NO", readINSParm.getValue("PRINT_NO"));// ����״̬
		return parm;
	}

	/**
	 * �˷Ѳ����޸�INS_OPD ������
	 */
	public boolean resetUpdateInsOpd(TParm insOPDParm, String status,
			String type, int insType, TConnection connection) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", insOPDParm.getData("CASE_NO"));
		parm.setData("CONFIRM_NO", "*" + insOPDParm.getData("CONFIRM_NO"));
		parm.setData("INV_NO", insOPDParm.getValue("INV_NO"));// ����״̬
		// �޸�INS_OPD ����INSAMT_FLG���˱�־Ϊ3
		if (!updateInsAmtFlg(parm, status, type, connection)) {
			resetConcelFee(insOPDParm, insType);
			return false;
		}
		return true;
	}

	/**
	 * �˷���ϸ���ɱ��ض��˱�־��0
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm resetInsetOpdandOpdOrder(TParm parm, TConnection connection) {
		TParm opdParm = selectResetFee(parm);
		TParm result = new TParm();
		if (opdParm.getErrCode() < 0) {
			result.setErr(-1, "��ְ��ͨ,��ѯҪ�˷Ѽ�¼��Ϣ����������");
			return result;
		}
		TParm opdOrderParm = selectResetOpdOrderFee(parm);
		if (opdOrderParm.getErrCode() < 0) {
			result.setErr(-1, "��ְ��ͨ,��ѯҪ�˷Ѽ�¼��Ϣ����������");
			return result;
		}
		// System.out.println("RESULT:::::::::::::::::::::::::" + opdOrderParm);

		// �˷���ϸ���ɱ��ض��˱�־��0
		TParm opdNewParm = opdParm;
		opdNewParm.setData("CONFIRM_NO", "*" + opdParm.getValue("CONFIRM_NO"));// Υ��Լ��

		TParm insOPDParm = insertReSetInsOpd(opdNewParm, parm
				.getValue("UNRECP_TYPE"), connection);
		if (insOPDParm.getErrCode() < 0) {
			result.setErr(-1, "��ְ��ͨ,�˷���ϸ���ɱ��ض��˲���������");
			return result;
		}
		TParm insOpdOrderParm = insertReSetInsOpdOrder(opdOrderParm, parm
				.getValue("UNRECP_TYPE"), connection);
		if (insOpdOrderParm.getErrCode() < 0) {
			result.setErr(-1, "��ְ��ͨ,�˷���ϸ���ɱ��ض��˲���������");
			return result;
		}
		opdParm = selectResetFee(parm);
		return opdParm;
	}

	/**
	 * ���÷ָ���ϸ�޸Ķ��˱�־
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm readINSParm, String status, String type) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		TParm result = INSOpdOrderTJTool.getInstance().updateINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder�޸Ķ��˱�־ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * ���÷ָ���ϸ�޸Ķ��˱�־
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm readINSParm, String status,
			String type, TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, status, type);
		TParm result = INSOpdOrderTJTool.getInstance().updateINSOpdOrder(parm,
				connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder�޸Ķ��˱�־ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * INSAMT_FLG���˱�־��Ʊ�ݺ�
	 * 
	 * @param readINSParm
	 * @param status
	 * @param type
	 * @param connection
	 * @return
	 */
	private TParm updateINSOpdOrderPrint(TParm readINSParm, String type,
			TConnection connection) {
		TParm parm = getUpdateParm(readINSParm, "", type);
		TParm result = INSOpdOrderTJTool.getInstance().updateINSOpdOrderPrint(
				parm, connection);
		return result;
	}

	/**
	 * �޸Ķ��˱�־ ������ʹ��
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public boolean updateInsAmtFlg(TParm parm, String status, String type) {
		TParm result = updateInsOpd(parm, status, type);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd�޸Ķ��˱�־ʧ��");
			return false;
		}
		result = updateINSOpdOrder(parm, status, type);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder�޸Ķ��˱�־ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * �޸Ķ��˱�־ ������ʹ��
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public boolean updateInsAmtFlg(TParm parm, String status, String type,
			TConnection connection) {
		TParm result = updateInsOpd(parm, status, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd�޸Ķ��˱�־ʧ��");
			return false;
		}
		result = updateINSOpdOrder(parm, status, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder�޸Ķ��˱�־ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * ����ʹ��
	 * 
	 * @param parm
	 * @return
	 */
	private boolean updateInsAmtFlg(TParm parm, String flg) {
		parm.setData("INSAMT_FLG", flg);
		TParm result = INSOpdTJTool.getInstance().updateINSOpd(parm);// �޸�INS_OPD
		// ����INSAMT_FLG���˱�־Ϊ3
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd�޸Ķ��˱�־ʧ��");
			return false;
		}
		result = INSOpdOrderTJTool.getInstance().updateINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder�޸Ķ��˱�־ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * �޸Ķ��˱�־��Ʊ�ݺ��� ������ʹ��
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public TParm updateInsAmtFlgPrint(TParm parm, String type,
			TConnection connection) {
		TParm result = updateINSOpdPrint(parm, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateInsOpd�޸Ķ��˱�־ʧ��");
			return result;
		}
		result = updateINSOpdOrderPrint(parm, type, connection);
		if (result.getErrCode() < 0) {
			err("updateInsAmtFlg updateINSOpdOrder�޸Ķ��˱�־ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * �޸Ķ��˱��
	 * 
	 * @param status
	 * @param connection
	 * @return
	 */
	public boolean updateINSOpdOrderOne(TParm parm, String status, String type) {
		TParm result = updateINSOpdOrder(parm, status, type);
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * ˢ������ ˢ�� ִ�к�����DataDown_sp, ���� U ��������ʶ���� ����DataDown_czys,���� A
	 * 
	 * @return
	 */
	public TParm readINSCard(TParm parm) {
		TParm readParm = INSTJTool.getInstance().DataDown_sp_U(
				parm.getValue("TEXT"));// U������ȡ����
		TParm readINSParm = new TParm();
		parm.setData("CARD_NO", readParm.getValue("CARD_NO"));// ҽ������
		parm.setData("TYPE", 1);// ��������:1�籣������2 ���֤���� ���̶�ֵ 1
		readINSParm = INSTJTool.getInstance().DataDown_czys_A(parm);// A��������Ⱥ�����Ϣ
		// System.out.println("ˢ������ ˢ�� ���� readINSParm::::"+readINSParm);
		if (!INSTJTool.getInstance().getErrParm(readINSParm)) {
			return readINSParm;
		}
		readINSParm.setData("CARD_NO", readParm.getValue("CARD_NO"));// ����
		readINSParm.setData("PERSONAL_NO", readINSParm.getValue("PERSONAL_NO"));// ���˱���
		readINSParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		readINSParm.setData("REGION_CODE", parm.getValue("NHI_REGION_CODE"));// ҽ���������
		readINSParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		readINSParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		return readINSParm;
	}

	/**
	 * ���ʶ�� ��DataDown_sp����ˢ����L�� ��ְ��ͨ
	 * 
	 * @return
	 */
	public TParm insIdentificationChZPt(TParm readINSParm) {
		// System.out.println("��ִ̨��");
		if (null == readINSParm || readINSParm.getErrCode() < 0) {
			readINSParm.setErr(-1, "ˢ������������ ");
			return readINSParm;
		}
		// System.out.println("-------readINSParm--------" + readINSParm);
		TParm identificationParm = INSTJTool.getInstance().DataDown_sp_L(
				readINSParm);// L
		// System.out.println("---------------parm-------------"
		// + identificationParm);
		if (!INSTJTool.getInstance().getErrParm(identificationParm)) {
			return identificationParm;
		}

		// ����
		return identificationParm;
	}

	/**
	 * ��ְ���� ˢ�����ز�����DataDown_mts����ˢ�����ף�E�� �õ�������Ϣ
	 * 
	 * @return
	 */
	public TParm insCreditCardChZMt(TParm readINSParm) {
		TParm identificationParm = INSTJTool.getInstance().DataDown_mts_E(
				readINSParm);// ��ְ����ˢ�����ز���
		if (!INSTJTool.getInstance().getErrParm(identificationParm)) {
			return identificationParm;
		}
		return identificationParm;
	}

	/**
	 * �Ǿ����� ˢ�����ز���,�õ�������Ϣ DataDown_cmts����ˢ�����ף�E�� �õ�������Ϣ
	 * 
	 * @return
	 */
	public TParm insCreditCardChJMt(TParm readINSParm) {
		TParm identificationParm = INSTJTool.getInstance().DataDown_cmts_E(
				readINSParm);
		if (!INSTJTool.getInstance().getErrParm(identificationParm)) {
			return identificationParm;
		}
		return identificationParm;
	}

	/**
	 * 
	 * ��ְ���������������������DataDown_sp��������������ϴ����ף�H��
	 * �Ǿ����������������������DataDown_cmts��������������ϴ����ף�H��
	 * 
	 * @return
	 */
	public boolean insSpcUpload(TParm parm) {
		// System.out.println("----------��ְ���������������----------");
		int type = parm.getInt("INS_TYPE");// 1 .��ְ��ͨ 2. ��ְ���� 3. �Ǿ�����
		TParm result = INSTJTool.getInstance()
				.specialCaseCommReturn(parm, type);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result.setErr(-1, result.getValue("PROGRAM_MESSAGE"));
			return false;
		}
		return true;
	}

	/**
	 * ��ְ��ͨ ��ӻ��޸�InsMZConfirm������
	 * 
	 * @return
	 */
	private void onSaveInsMZConfirmChZPt(TParm identificationParm,
			TParm readINSParm) {
		identificationParm.setData("PAT_NAME", identificationParm
				.getValue("NAME"));// ��������
		identificationParm.setData("OWN_NO", readINSParm
				.getValue("PERSONAL_NO"));// ���˱���
		identificationParm.setData("IDNO", identificationParm.getValue("SID"));// ���֤��
		identificationParm.setData("SEX_CODE", identificationParm
				.getValue("SEX"));// �Ա�
		identificationParm.setData("BIRTH_DATE", identificationParm
				.getData("BIRTHDAY"));// ��������

		identificationParm.setData("OWN_PAY_RATE", identificationParm
				.getValue("ZFBL"));// �Ը�����
		identificationParm.setData("ENTERPRISES_TYPE", identificationParm
				.getValue("COM_TYPE"));// ��ҵ���
		identificationParm.setData("SPECIAL_PAT", identificationParm
				.getValue("SP_PRESON_TYPE"));// ������Ա��� SPECIAL_PAT ���ֶ�
		identificationParm.setData("PAY_KIND", identificationParm
				.getData("PAY_KIND"));// ��������
	}

	/**
	 * ���INS_MZ_COMFIRM ������
	 * 
	 * @param identificationParm
	 *            ���ʶ�� ��DataDown_sp����ˢ�� L ��������
	 * @return
	 */
	public TParm onSaveInsMZConfirm(TParm identificationParm,
			TParm readINSParm, int insType) {
		// if (readINSParm.getValue("RECP_TYPE").equals("OPB")) {
		// //
		// identificationParm.setData("BIRTH_DATE",df.format(identificationParm
		// // .getValue("BIRTH_DATE")) );// ��������
		// } else if (readINSParm.getValue("RECP_TYPE").equals("REG")) {
		// System.out.println("readINSParm::" + readINSParm);
		identificationParm.setData("REGION_CODE", readINSParm
				.getValue("NEW_REGION_CODE"));
		identificationParm.setData("CASE_NO", readINSParm.getValue("CASE_NO"));
		identificationParm.setData("MR_NO", readINSParm.getValue("MR_NO"));
		identificationParm.setData("INSCARD_NO", readINSParm
				.getValue("CARD_NO"));// ҽ����
		identificationParm.setData("INSCARD_PASSWORD", readINSParm
				.getValue("PASSWORD"));// ҽ������

		identificationParm.setData("INS_PAT_TYPE", readINSParm
				.getValue("INS_PAT_TYPE"));// ��ҽ���
		identificationParm.setData("INS_CROWD_TYPE", readINSParm
				.getValue("CROWD_TYPE"));// ������

		identificationParm.setData("AUTO_FLG", "Y");//
		readINSParm.setData("CONFIRM_NO", identificationParm
				.getValue("CONFIRM_NO"));// �ʸ�ȷ����
		identificationParm.setData("DISEASE_CODE", readINSParm
				.getValue("DISEASE_CODE"));// �������
		// }
		identificationParm
				.setData("OPT_USER", readINSParm.getValue("OPT_USER"));
		identificationParm
				.setData("OPT_TERM", readINSParm.getValue("OPT_TERM"));
		switch (insType) {
		case 1:
			onSaveInsMZConfirmChZPt(identificationParm, readINSParm);
			break;
		case 2:
			onSaveInsMZConfirmChZMt(identificationParm, readINSParm);
			break;
		case 3:
			onSaveInsMZConfirmChJMt(identificationParm, readINSParm);
		}
		checkOutParm(identificationParm, insMZConfirm);
		// System.out.println("���---------------------" + identificationParm);
		TParm result = INSMZConfirmTool.getInstance().onSaveInsMZConfirm(
				identificationParm);
		if (result.getErrCode() < 0) {
			// System.out.println("ִ��INSTJFlow.insIdentification����������");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ���� ��ӻ��޸�InsMZConfirm������
	 * 
	 * @param identificationParm
	 * @param readINSParm
	 */
	private void onSaveInsMZConfirmChZMt(TParm identificationParm,
			TParm readINSParm) {
		identificationParm.setData("BANK_NO", null == identificationParm
				.getValue("BANK_NO") ? "" : identificationParm
				.getValue("BANK_NO"));//
		identificationParm.setData("OWN_NO", readINSParm
				.getValue("PERSONAL_NO"));// ���˱���
		identificationParm.setData("IDNO", identificationParm.getValue("SID"));// ���֤��
		identificationParm.setData("CTZ_CODE", identificationParm
				.getValue("PAT_TYPE"));// ��Ա���
		identificationParm.setData("UNIT_NO", identificationParm
				.getValue("COMPANY_NO"));// ��λ����
		identificationParm.setData("UNIT_CODE", identificationParm
				.getValue("COMPANY_CODE"));// ��λ����
		identificationParm.setData("UNIT_DESC", identificationParm
				.getValue("COMPANY_DESC"));// ��λ����
		identificationParm.setData("INS_CODE", identificationParm
				.getValue("BRANCH_CODE"));// �����籣����
		identificationParm.setData("TOT_AMT", identificationParm
				.getDouble("PERSON_ACCOUNT_AMT"));// �˻�������
		identificationParm.setData("OTOT_AMT", 0.00);// ����ר�����ʣ��
		identificationParm.setData("ENTERPRISES_TYPE", identificationParm
				.getValue("COM_TYPE"));// ��ҵ���
		identificationParm.setData("OWN_PAY_RATE", identificationParm
				.getValue("OWEN_PAY_SCALE"));// �Ը�����
		identificationParm.setData("SPECIAL_PAT", identificationParm
				.getValue("SP_PRESON_TYPE"));// ������Ա���
		identificationParm.setData("BIRTH_DATE", identificationParm
				.getData("BIRTH_DATE"));// ��������

		// SP_PERSON_MEMO
	}

	/**
	 * �Ǿ����� ��ӻ��޸�InsMZConfirm������
	 * 
	 * @param identificationParm
	 * @param readINSParm
	 */
	private void onSaveInsMZConfirmChJMt(TParm identificationParm,
			TParm readINSParm) {
		onSaveInsMZConfirmChZMt(identificationParm, readINSParm);
	}

	/**
	 * ��ѯ�˷ѵ���Ϣ �Һ�/�����շ� ��ѯ��INS_OPD ��ѯ���� CASE_NO RECP_TYPE REGION_CODE CONFIRM_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectResetFee(TParm parm) {
		// TParm result=INSMZConfirmTool.getInstance().queryMZConfirm(parm);
		// if (result.getErrCode() < 0) {
		// //System.out.println("ִ��INSTJFlow.queryMZConfirm����������");
		// return false;
		// }
		// if (result.getCount()<=0) {
		// //System.out.println("û�в�ѯ������");
		// return false;
		// }
		// parm.setData("CONFIRM_NO",result.getValue("CONFIRM_NO",0));//�ʸ�ȷ�����
		TParm insOPDParm = INSOpdTJTool.getInstance().selectResetFee(parm);
		if (insOPDParm.getErrCode() < 0) {
			insOPDParm.setErr(-1, "ִ��INSTJFlow.selectResetFee����������");
			return insOPDParm;
		}
		insOPDParm = insOPDParm.getRow(0);
		return insOPDParm;
	}

	/**
	 * �˷�ʹ�ò�ѯ��Ҫ��ӵ��˷�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectResetOpdOrderFee(TParm parm) {
		TParm result = INSOpdOrderTJTool.getInstance()
				.selectResetOpdOrder(parm);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "ִ��selectResetOpdOrderFee����������");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ��ͨ�˷ѽӿ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeChZPt(TParm parm, TParm insOPDParm) {
		insOPDParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		insOPDParm.setData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER"));
		TParm resetInsOPDParm = INSTJTool.getInstance().DataDown_yb_C(
				insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
			// System.out.println("��ְ��ͨ�˷Ѳ���ʧ��");
			return resetInsOPDParm;
		}

		// .getInt("INSAMT_FLG")
		return resetInsOPDParm;
	}

	/**
	 * ��ְ�����˷ѽӿ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeChZMt(TParm parm, TParm insOPDParm) {
		insOPDParm.setData("HOSP_NHI_NO", parm.getValue("REGION_CODE"));
		insOPDParm.setData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER"));
		insOPDParm.setData("PAT_TYPE", parm.getValue("PAT_TYPE"));
		TParm resetInsOPDParm = INSTJTool.getInstance().DataDown_mts_K(
				insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
			// System.out.println("��ְ��ͨ�˷Ѳ���ʧ��");
			return resetInsOPDParm;
		}
		// .getInt("INSAMT_FLG")
		return resetInsOPDParm;
	}

	/**
	 * �Ǿ������˷ѽӿ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeChJMt(TParm parm, TParm insOPDParm) {
		insOPDParm.setData("HOSP_NHI_NO", parm.getValue("REGION_CODE"));
		insOPDParm.setData("HOSP_OPT_USER_CODE", parm.getValue("OPT_USER"));
		insOPDParm.setData("PAT_TYPE", parm.getValue("PAT_TYPE"));
		TParm resetInsOPDParm = INSTJTool.getInstance().DataDown_cmts_K(
				insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
			// System.out.println("��ְ��ͨ�˷Ѳ���ʧ��");
			return resetInsOPDParm;
		}
		// .getInt("INSAMT_FLG")
		return resetInsOPDParm;
	}

	/**
	 * �˷�ȡ������
	 * 
	 * @return
	 */
	public boolean resetConcelFee(TParm insOPDParm, int insType) {
		TParm result = null;
		switch (insType) {// 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		case 1:
			result = INSTJTool.getInstance().DataDown_sp_S(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// �˷�ȡ��
			break;
		case 2:
			result = INSTJTool.getInstance().DataDown_mts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// �˷�ȡ��
			break;
		case 3:
			result = INSTJTool.getInstance().DataDown_cmts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// �˷�ȡ��
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("�˷�ȡ������ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * �˷�ȡ������ ������Աִ��ȡ������
	 * 
	 * @return
	 */
	public TParm resetConcelFee(TParm insOPDParm) {
		TParm result = null;
		int insType = insOPDParm.getInt("INS_TYPE");
		switch (insType) {// 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		case 1:
			result = INSTJTool.getInstance().DataDown_sp_S(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// �˷�ȡ��
			break;
		case 2:
			result = INSTJTool.getInstance().DataDown_mts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// �˷�ȡ��
			break;
		case 3:
			result = INSTJTool.getInstance().DataDown_cmts_J(
					insOPDParm.getValue("CONFIRM_NO"),
					insOPDParm.getValue("REGION_CODE"), "31");// �˷�ȡ��
			break;
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("�˷�ȡ������ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ��ͨ�˷�ȷ�� ִ�����һ���˷���Ϣ INS_OPD ����� ����DataDown_yb (D)����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeInsSettleChZPt(TParm insOPDParm, TParm resetInsOPDParm) {

		resetInsOPDParm
				.setData("CONFIRM_NO", insOPDParm.getValue("CONFIRM_NO"));
		resetInsOPDParm.setData("REGION_CODE", insOPDParm
				.getValue("NHI_REGION_CODE"));
		resetInsOPDParm.setData("ACCOUNT_PAY_AMT", insOPDParm
				.getDouble("ACCOUNT_PAY_AMT"));
		// System.out.println("resetInsOPDParm:::::" + resetInsOPDParm);
		TParm result = INSTJTool.getInstance().DataDown_yb_D(resetInsOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result = INSTJTool.getInstance().DataDown_yb_D(resetInsOPDParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				// System.out.println("�ٴε��ý���ȷ�Ͻ���,����ʧ��");
				return result;
			}
		}
		// .getInt("INS_FLG")
		return result;
	}

	/**
	 * ��ְ�����˷�ȷ�� ִ�����һ���˷���Ϣ INS_OPD ����� ����DataDown_mts(L)����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeInsSettleChZMt(TParm insOPDParm) {

		TParm result = INSTJTool.getInstance().DataDown_mts_L(insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result = INSTJTool.getInstance().DataDown_mts_L(insOPDParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				// System.out.println("�ٴε��ý���ȷ�Ͻ���,����ʧ��");
				return result;
			}
		}
		// .getInt("INS_FLG")
		return result;
	}

	/**
	 * �Ǿ������˷�ȷ�� ִ�����һ���˷���Ϣ INS_OPD ����� ����DataDown_cmts(L)����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm resetFeeInsSettleChJMt(TParm insOPDParm) {
		TParm result = INSTJTool.getInstance().DataDown_cmts_L(insOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result = INSTJTool.getInstance().DataDown_cmts_L(insOPDParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				// System.out.println("�ٴε��ý���ȷ�Ͻ���,����ʧ��");
				return result;
			}
		}
		// .getInt("INS_FLG")
		return result;
	}

	/**
	 * ��;״̬
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public boolean insertRun(TParm parm, String wayName) {
		TParm runParm = runTempParm(parm);
		runParm.setData("STUTS", "1");// 1.��; 2.�ɹ�
		runParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		runParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		runParm.setData("EXE_WAY", wayName);
		TParm result = INSRunTool.getInstance().insertInsRun(runParm);
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * ��;״̬
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public boolean insertRun(TParm parm, String wayName, TConnection connection) {
		TParm runParm = runTempParm(parm);
		runParm.setData("STUTS", "1");// 1.��; 2.�ɹ�
		runParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		runParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		runParm.setData("EXE_WAY", wayName);
		TParm result = INSRunTool.getInstance().insertInsRun(runParm,
				connection);
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * �ж��Ƿ������;״̬��������ڲ�ִ�� STUTS="1"
	 * 
	 * @param parm
	 * @return
	 */
	public boolean queryRun(TParm parm) {
		TParm runParm = runTempParm(parm);
		runParm.setData("STUTS", "1");
		runParm = INSRunTool.getInstance().queryInsRun(runParm);
		if (runParm.getErrCode() < 0) {
			return false;
		}
		for (int i = 0; i < runParm.getCount(); i++) {
			if (runParm.getInt("STUTS", i) == 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ���ҽ���˷����� �˷���ϸ���ɱ��ض��˱�־��0
	 * 
	 * @param connection
	 * @return
	 */
	public TParm insertReSetInsOpd(TParm insOPDParm, String unRecpType,
			TConnection connection) {
		// insOPDParm.setData("INSAMT_FLG", 0);
		insOPDParm.setData("UNRECP_TYPE", unRecpType);// �˷�����
		INSTJTool.getInstance().balanceParm(insOPDParm);
		TParm result = INSOpdTJTool.getInstance().insertResetINSOpd(insOPDParm,
				connection);
		// System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// connection.close();
			return result;
		}
		result = INSOpdTJTool.getInstance().selectResetFee(insOPDParm);
		return insOPDParm;
	}

	/**
	 * ����˷�INS_OPD_ORDER����
	 * 
	 * @param insOpdOrderParm
	 * @param unRecpType
	 * @param connection
	 * @return
	 */
	public TParm insertReSetInsOpdOrder(TParm insOpdOrderParm,
			String unRecpType, TConnection connection) {
		TParm result = new TParm();
		// System.out.println("insOpdOrderParm::::" + insOpdOrderParm);
		if (insOpdOrderParm.getCount() <= 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		TParm seqParm = new TParm();
		seqParm.setData("CASE_NO", insOpdOrderParm.getValue("CASE_NO", 0));
		seqParm.setData("CONFIRM_NO", "*"
				+ insOpdOrderParm.getValue("CONFIRM_NO", 0));
		seqParm.setData("RECP_TYPE", insOpdOrderParm.getValue("RECP_TYPE", 0));
		TParm maxSeqOpdOrderParm = INSOpdOrderTJTool.getInstance()
				.selectMAXSeqNo(seqParm);
		int maxSeq = 0;// ������
		if (null != maxSeqOpdOrderParm.getValue("SEQ_NO", 0)
				&& maxSeqOpdOrderParm.getInt("SEQ_NO", 0) > 0) {
			maxSeq = maxSeqOpdOrderParm.getInt("SEQ_NO", 0);
		}
		for (int i = 0; i < insOpdOrderParm.getCount("CASE_NO"); i++) {
			TParm inParm = new TParm();
			inParm.setRowData(-1, insOpdOrderParm, i);
			inParm.setData("RECP_TYPE", unRecpType);// �˷�
			inParm.setData("INSAMT_FLG", 0);// δ���˲���
			inParm.setData("OWN_AMT", -inParm.getDouble("OWN_AMT"));// �Է�
			inParm.setData("TOTAL_AMT", -inParm.getDouble("TOTAL_AMT"));// �������
			inParm.setData("TOTAL_NHI_AMT", -inParm.getDouble("TOTAL_NHI_AMT"));// ҽ�����
			inParm.setData("ADDPAY_AMT", -inParm.getDouble("ADDPAY_AMT"));//
			inParm.setData("SEQ_NO", maxSeq + i + 1);
			inParm.setData("CONFIRM_NO", "*" + inParm.getValue("CONFIRM_NO"));
			checkOutParm(inParm, insOpdOrder);
			result = INSOpdOrderTJTool.getInstance().insertINSOpdOrder(inParm);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				// connection.close();
				return result;
			}
		}
		return result;
	}

	/**
	 * ɾ��ҽ����;״̬d\
	 * 
	 * @return
	 */
	public boolean deleteInsRun(TParm runParm) {
		TParm parm = runTempParm(runParm);
		parm = INSRunTool.getInstance().deleteInsRun(parm);
		if (parm.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * ҽ����;��������
	 * 
	 * @param runParm
	 */
	private TParm runTempParm(TParm runParm) {
		TParm parm = new TParm();
		if (null!=runParm.getValue("CASE_NO_SUM") && runParm.getValue("CASE_NO_SUM").length()>0) {
			parm.setData("CASE_NO", runParm.getValue("CASE_NO_SUM"));
		}else{
			parm.setData("CASE_NO", runParm.getValue("CASE_NO"));
		}
		parm.setData("EXE_USER", runParm.getValue("OPT_USER"));
		parm.setData("EXE_TERM", runParm.getValue("OPT_TERM"));
		parm.setData("EXE_TYPE", runParm.getValue("EXE_TYPE"));
		return parm;
	}

	/**
	 * У���Ƿ�Ϊ��
	 * 
	 * @param parm
	 */
	private void checkOutParm(TParm parm, String[] data) {
		for (int i = 0; i < data.length; i++) {
			if (null == parm.getValue(data[i])
					|| parm.getValue(data[i]).trim().length() <= 0) {
				parm.setData(data[i], "");
			}

		}
	}

	/**
	 * ����ʹ�� ɾ������
	 * 
	 * @param parm
	 */
	private TParm delInsOpdAndOpdOrder(TParm parm) {
		TParm result = new TParm();
		result = INSOpdTJTool.getInstance().deleteINSOpd(parm);
		if (result.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return result;
		}
		result = INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(parm);
		if (result.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return result;
		}
		//======pangben 2013-3-13 �����;ɾ��
		if (null!=parm.getValue("CHOOSE_FLG")&&parm.getValue("CHOOSE_FLG").equals("Y")) {
			result =INSRunTool.getInstance().deleteInsRunConcel(parm);//ȡ������ɾ����;״̬�����˽������
		}else{
			result =INSRunTool.getInstance().deleteInsRun(parm);//ȡ������ɾ����;״̬
		}
		if (result.getErrCode() < 0) {
			// System.out.println("err:" + parm.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * �����Զ�����
	 * 
	 * @param parm
	 * @return
	 */
	public Map autoAccountComm(TParm parm) {
		TParm returnParm = new TParm();// �ۼƳ��ִ��������
		StringBuffer buffer = new StringBuffer();
		boolean errFlg = false;// �Ƿ���ִ���
		buffer.append("ҽ�������:");
		// TParm interFaceParmOne = new TParm();
		int insType = parm.getInt("INS_TYPE");
		// System.out.println("�Զ���������parm::::" + parm);
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);// ���һ��Ҫ���˵�����
			tempParm.setData("NHI_REGION_CODE", parm
					.getValue("NHI_REGION_CODE"));

			// ���ҽ��������Ϣ
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirmOne(tempParm);
			// �˷�
			if (tempParm.getValue("RECP_TYPE").equals("REGT")
					|| tempParm.getValue("RECP_TYPE").equals("OPBT")) {
				returnParm = reSetAutoAccount(tempParm, insType);
				if (returnParm.getErrCode() < 0) {
					errFlg = true;
				}
			} else {
				// ������
				returnParm = autoAccount(tempParm, insType, mzConfirmParm,
						buffer);
				if (returnParm.getErrCode() < 0) {
					errFlg = true;
				}
			}

		}
		buffer.append("�Զ�����ʧ��");
		if (errFlg)
			returnParm.setErr(-1, buffer.toString());
		return returnParm.getData();
	}

	/**
	 * �˷��Զ�����
	 * 
	 * @return
	 */
	public TParm reSetAutoAccount(TParm tempParm, int insType) {
		TParm result = new TParm();
		TParm interFaceParmOne = new TParm();
		// ȡ�����㽻��

		// ���˱�־Ϊ0����ȡ������
		if (tempParm.getInt("INSAMT_FLG") == 0) {
			switch (insType) {
			case 1:
				result = INSTJTool.getInstance().DataDown_sp_S(tempParm, "31");
				break;
			case 2:
				result = INSTJTool.getInstance().DataDown_mts_J(tempParm, "31");
				break;
			case 3:
				result = INSTJTool.getInstance()
						.DataDown_cmts_J(tempParm, "31");
				break;

			}
			if (!INSTJTool.getInstance().getErrParm(result)) {
				return result;
			}
			// ִ��ɾ��INS_OPD \INS_OPD_ORDER����
			result = delInsOpdAndOpdOrder(tempParm);
			// System.out.println("�˷��Զ�����ȡ�����㽻��:"+result.getValue("PROGRAM_MESSAGE"));
		} else if (tempParm.getInt("INSAMT_FLG") == 1) {
			switch (insType) {
			case 1:
				interFaceParmOne = INSTJTool.getInstance().DataDown_yb_D(
						tempParm);
				break;
			case 2:
				interFaceParmOne = INSTJTool.getInstance().DataDown_mts_L(
						tempParm);
				break;
			case 3:
				interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_L(
						tempParm);
				break;
			}
			if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
				return interFaceParmOne;
			} else {
				if (interFaceParmOne.getInt("INSAMT_FLG") == 3) {
					// ִ���޸�INS_OPD \INS_OPD_ORDER����
					if (!updateInsAmtFlg(tempParm, "3")) {
					}
					result =INSRunTool.getInstance().deleteInsRunConcel(tempParm);//ȡ������ɾ����;״̬�����˽������
				}
			}
		}
		
		return result;
	}

	/**
	 * ������ �Զ�����
	 * 
	 * @param tempParm
	 * @param insType
	 *            1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
	 * @param mzConfirmParm
	 *            ������Ϣ
	 * @return
	 */
	private TParm autoAccount(TParm tempParm, int insType, TParm mzConfirmParm,
			StringBuffer buffer) {
		// ��δ��Ʊ���Ҷ��˱�־Ϊ3���µ��������Ѽ�¼
		TParm returnParm = new TParm();// �ۼƳ��ִ��������
		boolean errFlg = false;// �Ƿ���ִ���
		TParm result = new TParm();
		TParm interFaceParmOne = new TParm();
		// System.out.println("Ʊ�ݺ���:::::" + tempParm);
		if (null == tempParm.getValue("INV_NO")
				|| tempParm.getValue("INV_NO").length() <= 0) {
			// ȡ�����㽻��
			switch (insType) {
			case 1:
				result = INSTJTool.getInstance().DataDown_sp_S(tempParm, "30");
				break;
			case 2:
				result = INSTJTool.getInstance().DataDown_mts_J(tempParm, "30");
				break;
			case 3:
				result = INSTJTool.getInstance()
						.DataDown_cmts_J(tempParm, "30");
				break;

			}
			if (!INSTJTool.getInstance().getErrParm(result)) {
				errFlg = true;
				buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
			} else {
				// ִ��ɾ��INS_OPD \INS_OPD_ORDER����
				result = delInsOpdAndOpdOrder(tempParm);
				if (result.getErrCode() < 0) {
					errFlg = true;
					buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
				}
			}
		} else {
			// ���ѳ�Ʊ���������ѣ���Ʊ��ʱ����˱�־������1 ����Ϊ3ʱ��Ʊ
			// ����������ִ�д�Ʊ�������� ���˱�־�� 3ʱ�ſ��Դ�Ʊ
			// ���� ����Ϊ1 ִ�н��㽻�׽ӿ�

			if (tempParm.getInt("INSAMT_FLG") == 1) {
				// ���㽻��
				switch (insType) {
				case 1:
					interFaceParmOne = INSTJTool.getInstance().DataDown_sp_R(
							tempParm);
					break;
				case 2:
					interFaceParmOne = INSTJTool.getInstance().DataDown_mts_I(
							tempParm, mzConfirmParm.getRow(0));
					break;
				case 3:
					interFaceParmOne = INSTJTool.getInstance().DataDown_cmts_I(
							tempParm, mzConfirmParm.getRow(0));
					break;
				}
				if (!INSTJTool.getInstance().getErrParm(interFaceParmOne)) {
					errFlg = true;
					buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
				} else {
					if (interFaceParmOne.getInt("INSAMT_FLG") == 3) {
						// ִ���޸�INS_OPD \INS_OPD_ORDER����
						if (!updateInsAmtFlg(tempParm, "3")) {
							errFlg = true;
							buffer.append(tempParm.getValue("CONFIRM_NO")
									+ ",\n");
						}
					}
				}
			} else {
				// INSAMT_FLG ==0
				// ִ��ɾ��INS_OPD \INS_OPD_ORDER����
				// result = INSTJTool.getInstance().DataDown_sp_S(parm);
				// if (!INSTJTool.getInstance().getErrParm(result)) {
				// result.setErr(-1, "�ٴε��ý���ȷ�Ͻ���,����ʧ��");
				// return result;
				// }
				result = delInsOpdAndOpdOrder(tempParm);
				if (result.getErrCode() < 0) {
					errFlg = true;
					buffer.append(tempParm.getValue("CONFIRM_NO") + ",\n");
				}
			}
		}
		if (errFlg) {
			returnParm.setErr(-1, "����");
		}
		return returnParm;
	}

	/**
	 * �����˹��÷���
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm opdAccountComm(TParm parm) {
		// buffer.append("ҽ�������:");
		// �����ܶ��˻�������
		TParm restult = sumOpdParm(parm);
		if (!INSTJTool.getInstance().getErrParm(restult)) {
			// restult.setErr(-1, "������ʧ��");
			return restult;
		}
		return restult;
	}

	/**
	 * ����ϸ�ʹ��÷���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm opdOrderAccountComm(TParm opdOrderParm, TParm parm) {
		TParm result = new TParm();
		int insType = parm.getInt("INS_TYPE");// 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		opdOrderParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		opdOrderParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		// opdOrderParm.setData("PAT_TYPE", parm.getValue("PAT_TYPE"));// ֧�����
		opdOrderParm.setData("ACCOUNT_DATE", parm.getValue("ACCOUNT_DATE"));// ����ʱ��
		// opdOrderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//
		// ��Ա���
		switch (insType) {
		case 1:
			result = sumOpdOrderChZPt(opdOrderParm);
			break;
		case 2:
			result = sumOpdOrderChZMt(opdOrderParm);
			break;
		case 3:
			result = sumOpdOrderChJMt(opdOrderParm);
			break;
		}
		return result;
	}

	/**
	 * ��������˻�������
	 * 
	 * @param insOpdParm
	 * @return
	 */
	private TParm sumOpdParm(TParm insOpdParm) {
		TParm result = new TParm();
		int insType = insOpdParm.getInt("INS_TYPE");// 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		switch (insType) {
		case 1:
			result = sumOpdChZPt(insOpdParm);
			break;
		case 2:
			result = sumOpdChZMtAndChJMt(insOpdParm);
			break;
		case 3:
			result = sumOpdChZMtAndChJMt(insOpdParm);
			break;
		}
		return result;
	}

	/**
	 * ��ְ��ͨ ���������
	 * 
	 * @param insOpdParm
	 * @return
	 */
	private TParm sumOpdChZPt(TParm insOpdParm) {
		TParm parm = new TParm();
		String regionCode = insOpdParm.getValue("REGION_CODE");// 1 HOSP_NHI_NO
		// ҽԺ����
		String hospOptUserCode = insOpdParm.getValue("USER_ID");// 2
		// HOSP_OPT_USER_CODE
		// ҽԺ����Ա����
		String ownNo = insOpdParm.getValue("OWN_NO", 0);// 3 OWN_NO ���˱���
		String collateAccountTime = insOpdParm.getValue("ACCOUNT_DATE");// 4
		// COLLATE_ACCOUNT_TIME
		// ����ʱ��
		String payKind = insOpdParm.getValue("PAY_KIND");// 5 PAY_TYPE ֧�����
		String patType = insOpdParm.getValue("PAT_TYPE");// PAT_TYPE ��Ա���
		TParm specialParm = insOpdParm.getParm("specialParm");// ������Ա�����
		// -----?????
		double totalAmt = 0.00;// 6 TOTAL_AMT �������
		double nhiAmt = 0.00;// 7 NHI_AMT �걨���
		double ownAmt = 0.00;// 8 OWN_AMT ȫ�Էѽ��
		double addpayAmt = 0.00;// 9 ADDPAY_AMT �������
		double ototAmt = 0.00;// 10 OTOT_AMT ר������籣֧��
		double accountPayAmt = 0.00;// 11 ACCOUNT_PAY_AMT �����ʻ�ʵ��֧�����
		int allTime = 0;// 12 ALL_TIME ���˴�
		double ototOutAmt = 0.00;// 13 OTOT_OUT_AMT ר������籣֧��(�˷�)
		double accountPayAmtExit = 0.00;// 14 ACCOUNT_PAY_AMT_EXIT
		// �����ʻ�ʵ��֧�����(�˷�)
		int allTimeExit = 0;// 15 ALL_TIME_EXIT ���˴�(�˷�)
		double agentAmt = 0.00;// 16 AGENT_AMT ���������������
		double agentAmtOut = 0.00;// 17 AGENT_AMT_OUT ���������������(�˷�)
		double fyAgentAmt = 0.00;// 18 FY_AGENT_AMT �Ÿ����������
		double fyAgentAmtB = 0.00;// 19 FY_AGENT_AMT_B �Ÿ����������(�˷�)
		double fdAgentAmt = 0.00;// 20 FD_AGENT_AMT �ǵ����֢�������
		double fdAgentAmtB = 0.00;// 21 FD_AGENT_AMT_B �ǵ����֢��������˷�)
		double unReimAmt = 0.00;// 22 UNREIM_AMT ����δ������
		double unReimAmtB = 0.00;// 23 UNREIM_AMT_B ����δ�������˷ѣ�
		double armyAiAmt = 0.00;// �������ARMY_AI_AMT
		for (int i = 0; i < insOpdParm.getCount(); i++) {
			// ������Ա���SPECIAL_PAT:04 �˲о���06 ����Ա07 ����������Ա08 �Ÿ�����09 �ǵ����֢
			// �˷�����
			if (insOpdParm.getValue("RECP_TYPE", i).equals("REGT")
					|| insOpdParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				ototOutAmt += insOpdParm.getDouble("OTOT_AMT", i);
				accountPayAmtExit += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);
				unReimAmtB += insOpdParm.getDouble("UNREIM_AMT", i);
				allTimeExit++;
			} else {
				totalAmt += insOpdParm.getDouble("TOT_AMT", i);
				nhiAmt += insOpdParm.getDouble("NHI_AMT", i);
				ownAmt += insOpdParm.getDouble("OWN_AMT", i);
				addpayAmt += insOpdParm.getDouble("ADD_AMT", i);

				ototAmt += insOpdParm.getDouble("OTOT_AMT", i);
				accountPayAmt += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);
				unReimAmt += insOpdParm.getDouble("UNREIM_AMT", i);
				allTime++;
			}
		}
		for (int i = 0; i < specialParm.getCount(); i++) {
			armyAiAmt = specialParm.getDouble("ARMY_AI_AMT", i);
			if (specialParm.getValue("RECP_TYPE", i).equals("REGT")
					|| specialParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				// �˷Ѳ��� ���������Ա����ۼƽ��
				String special_pat = "";
				for (int j = 0; j < specialParm.getCount(); j++) {
					if (specialParm.getValue("CONFIRM_NO", i).equals(
							"*" + specialParm.getValue("CONFIRM_NO", j))) {
						special_pat = specialParm.getValue("SPECIAL_PAT", j);
						break;
					}
				}
				if (null != special_pat && special_pat.length() > 0) {
					if (special_pat.equals("04")) {// 04
						// �˲о���

					} else if (special_pat.equals("06")) {// 06
						// ����Ա

					} else if (special_pat.equals("07")) {// 07
						// ����������Ա
						agentAmtOut += armyAiAmt;
					} else if (special_pat.equals("08")) {// 08
						// �Ÿ�����
						fyAgentAmtB += armyAiAmt;
					} else if (special_pat.equals("09")) {// 09
						// �ǵ����֢
						fdAgentAmtB += armyAiAmt;
					}
				}
			} else {
				if (null != specialParm.getValue("SPECIAL_PAT", i)) {
					if (specialParm.getValue("SPECIAL_PAT", i).equals("04")) {// 04
						// �˲о���

					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"06")) {// 06
						// ����Ա

					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"07")) {// 07
						// ����������Ա
						agentAmt += armyAiAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"08")) {// 08
						// �Ÿ�����
						fyAgentAmt += armyAiAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"09")) {// 09
						// �ǵ����֢
						fdAgentAmt += armyAiAmt;
					}
				}
			}
		}
		parm.setData("REGION_CODE", regionCode);
		parm.setData("OPT_USER", hospOptUserCode);
		parm.setData("ACCOUNT_DATE", collateAccountTime);
		parm.setData("PAY_KIND", payKind);
		parm.setData("OWN_NO", ownNo);
		parm.setData("TOTAL_AMT", getDoubleValue(totalAmt));
		parm.setData("NHI_AMT", getDoubleValue(nhiAmt));
		parm.setData("OWN_AMT", getDoubleValue(ownAmt));
		parm.setData("ADDPAY_AMT", getDoubleValue(addpayAmt));
		parm.setData("OTOT_AMT", getDoubleValue(ototAmt));
		parm.setData("ACCOUNT_PAY_AMT", getDoubleValue(accountPayAmt));
		parm.setData("ALL_TIME", allTime);
		parm.setData("OTOT_OUT_AMT", getDoubleValue(Math.abs(ototOutAmt)));
		parm.setData("ACCOUNT_PAY_AMT_EXIT", getDoubleValue(Math
				.abs(accountPayAmtExit)));
		parm.setData("ALL_TIME_EXIT", allTimeExit);
		parm.setData("AGENT_AMT", getDoubleValue(agentAmt));
		parm.setData("AGENT_AMT_OUT", getDoubleValue(Math.abs(agentAmtOut)));
		parm.setData("FY_AGENT_AMT", getDoubleValue(fyAgentAmt));
		parm.setData("FY_AGENT_AMT_B", getDoubleValue(Math.abs(fyAgentAmtB)));
		parm.setData("FD_AGENT_AMT", getDoubleValue(fdAgentAmt));
		parm.setData("FD_AGENT_AMT_B", getDoubleValue(Math.abs(fdAgentAmtB)));
		parm.setData("UNREIM_AMT", getDoubleValue(unReimAmt));
		parm.setData("UNREIM_AMT_B", getDoubleValue(Math.abs(unReimAmtB)));
		// System.out.println("���������ز���������ݣ���������"+parm);
		TParm result = INSTJTool.getInstance().DataDown_sp_O(parm);
		// if (!INSTJTool.getInstance().getErrParm(result)) {
		// // System.out.println("������ʧ��");
		// return result;
		// }
		return result;
	}

	/**
	 * ��ְ���� �����˲���
	 * 
	 * @param insOpdParm
	 * @return
	 */
	private TParm sumOpdChZMtAndChJMt(TParm insOpdParm) {
		TParm parm = new TParm();
		String regionCode = insOpdParm.getValue("REGION_CODE");// 1 HOSP_NHI_NO
		// ҽԺ����
		String optUser = insOpdParm.getValue("USER_ID");// 2 OPT_USER���ز���Ա����
		String ownNo = "";// 3 OWN_NO ���˱���
		String collateAccountTime = insOpdParm.getValue("ACCOUNT_DATE");// 4
		// COLLATE_ACCOUNT_TIME
		TParm specialParm = insOpdParm.getParm("specialParm");// ������Ա�����
		// ����ʱ��
		double totalAmt = 0.00;// 5 TOTAL_AMT �������(��������)
		double applyAmt = 0.00;// 6 APPLY_AMT ͳ������걨���(��������)
		double flgAgentAmt = 0.00;// 7 TOTAL_AGENT_AMT ���������걨���(��������)
		double ownAmt = 0.00;// 8 OWN_AMT ȫ�Էѽ��(��������)
		double addAmt = 0.00;// 9 ADD_AMT �������(��������)
		int sumPertime = 0;// 10 SUM_PERTIME ���˴�(��������)
		double applyAmtB = 0.00;// 11 APPLY_AMT_B ͳ������籣֧�����˷ѣ�
		double flgAgentAmtB = 0.00;// 12 TOTAL_AGENT_AMT ҽ�ƾ���֧�����˷ѣ�
		int sumPertimeB = 0;// 13 SUM_PERTIME_B ���˴Σ��˷ѣ�
		double armyAiAmt = 0.00;// 14 ARMY_AI_AMT ���в������(��������) -----
		double armyAiAmtB = 0.00;// 15 ARMY_AI_AMT_B ���в������(�����˷�) ------
		double servantAmt = 0.00;// 16 SERVANT_AMT ����Ա�������(��������) ----
		double servantAmtB = 0.00;// 17 SERVANT_AMT_B ����Ա�������(�����˷�) -----
		double accountPayAmt = 0.00;// 18 ACCOUNT_PAY_AMT �����ʻ�ʵ��֧�����(��������) ----
		double accountPayAmtB = 0.00;// 19 ACCOUNT_PAY_AMT_B �����ʻ�ʵ��֧�����(�����˷�)
		// ----
		double mzAgentAmt = 0.00;// 20 MZ_AGENT_AMT ���������������
		double mzAgentAmtB = 0.00;// 21 MZ_AGENT_AMT_B ���������������(�˷�)
		double fyAgentAmt = 0.00;// 22 FY_AGENT_AMT �Ÿ���������� NUMBER
		double fyAgentAmtB = 0.00;// 23 FY_AGENT_AMT_B �Ÿ����������(�˷�)
		double fdAgentAmt = 0.00;// 24 FD_AGENT_AMT �ǵ����֢�������
		double fdAgentAmtB = 0.00;// 25 FD_AGENT_AMT_B �ǵ����֢��������˷�)
		double unReimAmt = 0.00;// 26 UNREIM_AMT ����δ�������
		double unReimAmtB = 0.00;// 27 UNREIM_AMT_B ����δ�������˷ѣ�
		double tempAmt = 0.00;// �м����
		for (int i = 0; i < insOpdParm.getCount(); i++) {
			tempAmt = insOpdParm.getDouble("ARMY_AI_AMT", i);
			// �˷�����
			if (insOpdParm.getValue("RECP_TYPE", i).equals("REGT")
					|| insOpdParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				// ototOutAmt+=insOpdParm.getDouble("OTOT_AMT",i);
				accountPayAmtB += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);
				applyAmtB += insOpdParm.getDouble("TOTAL_AGENT_AMT", i);
				// agentAmtOut=+insOpdParm.getDouble("OTOT_AMT");
				flgAgentAmtB += insOpdParm.getDouble("FLG_AGENT_AMT", i);

				// servantAmtB += insOpdParm.getDouble("SERVANT_AMT", i);
				unReimAmtB += insOpdParm.getDouble("UNREIM_AMT", i);
				sumPertimeB++;

			} else {
				totalAmt += insOpdParm.getDouble("TOT_AMT", i);
				ownAmt += insOpdParm.getDouble("OWN_AMT", i);

				addAmt += insOpdParm.getDouble("ADD_AMT", i);
				// ototAmt+=insOpdParm.getDouble("OTOT_AMT",i);
				flgAgentAmt += insOpdParm.getDouble("FLG_AGENT_AMT", i);
				applyAmt += insOpdParm.getDouble("TOTAL_AGENT_AMT", i);
				accountPayAmt += insOpdParm.getDouble("ACCOUNT_PAY_AMT", i);

				// servantAmt += insOpdParm.getDouble("SERVANT_AMT", i);
				unReimAmt += insOpdParm.getDouble("UNREIM_AMT", i);
				sumPertime++;

			}
		}
		for (int i = 0; i < specialParm.getCount(); i++) {
			tempAmt = specialParm.getDouble("ARMY_AI_AMT", i);
			// �˷�����
			if (specialParm.getValue("RECP_TYPE", i).equals("REGT")
					|| specialParm.getValue("RECP_TYPE", i).equals("OPBT")) {
				String special_pat = "";
				// �˷Ѳ��� ���������Ա����ۼƽ��
				for (int j = 0; j < specialParm.getCount(); j++) {
					if (specialParm.getValue("CONFIRM_NO", i).equals(
							"*" + specialParm.getValue("CONFIRM_NO", j))) {
						special_pat = specialParm.getValue("SPECIAL_PAT", j);
						break;
					}

				}
				if (null != special_pat && special_pat.length() > 0) {
					if (special_pat.equals("04")) {// 04
						armyAiAmtB += tempAmt;// �˲о���

					} else if (special_pat.equals("06")) {// 06
						// ����Ա
						servantAmtB += tempAmt;
					} else if (special_pat.equals("07")) {// 07
						// ����������Ա
						mzAgentAmtB += tempAmt;
					} else if (special_pat.equals("08")) {// 08
						// �Ÿ�����
						fyAgentAmtB += tempAmt;
					} else if (special_pat.equals("09")) {// 09
						// �ǵ����֢
						fdAgentAmtB += tempAmt;
					}
				}
			} else {
				if (null != specialParm.getValue("SPECIAL_PAT", i)) {
					if (specialParm.getValue("SPECIAL_PAT", i).equals("04")) {// 04
						armyAiAmt += tempAmt; // �˲о���

					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"06")) {// 06
						// ����Ա
						servantAmt += tempAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"07")) {// 07
						// ����������Ա
						mzAgentAmt += tempAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"08")) {// 08
						// �Ÿ�����
						fyAgentAmt += tempAmt;
					} else if (specialParm.getValue("SPECIAL_PAT", i).equals(
							"09")) {// 09
						// �ǵ����֢
						fdAgentAmt += tempAmt;
					}
				}
			}
		}

		parm.setData("REGION_CODE", regionCode);
		parm.setData("OPT_USER", optUser);
		parm.setData("OWN_NO", ownNo);
		parm.setData("ACCOUNT_DATE", collateAccountTime);
		parm.setData("TOTAL_AMT", getDoubleValue(totalAmt));
		parm.setData("APPLY_AMT", getDoubleValue(applyAmt));
		parm.setData("FLG_AGENT_AMT", getDoubleValue(flgAgentAmt));
		parm.setData("OWN_AMT", getDoubleValue(ownAmt));
		parm.setData("ADD_AMT", getDoubleValue(addAmt));
		parm.setData("SUM_PERTIME", sumPertime);
		parm.setData("APPLY_AMT_B", getDoubleValue(Math.abs(applyAmtB)));
		parm.setData("FLG_AGENT_AMT_B", getDoubleValue(Math.abs(flgAgentAmtB)));
		parm.setData("SUM_PERTIME_B", getDoubleValue(Math.abs(sumPertimeB)));
		parm.setData("ARMY_AI_AMT", getDoubleValue(armyAiAmt));
		parm.setData("ARMY_AI_AMT_B", getDoubleValue(Math.abs(armyAiAmtB)));
		parm.setData("SERVANT_AMT", getDoubleValue(servantAmt));
		parm.setData("SERVANT_AMT_B", getDoubleValue(Math.abs(servantAmtB)));
		parm.setData("ACCOUNT_PAY_AMT", getDoubleValue(accountPayAmt));
		parm.setData("ACCOUNT_PAY_AMT_B", getDoubleValue(Math
				.abs(accountPayAmtB)));
		parm.setData("MZ_AGENT_AMT", getDoubleValue(mzAgentAmt));
		parm.setData("MZ_AGENT_AMT_B", getDoubleValue(Math.abs(mzAgentAmtB)));
		parm.setData("FY_AGENT_AMT", getDoubleValue(fyAgentAmt));
		parm.setData("FY_AGENT_AMT_B", getDoubleValue(Math.abs(fyAgentAmtB)));
		parm.setData("FD_AGENT_AMT", getDoubleValue(fdAgentAmt));
		parm.setData("FD_AGENT_AMT_B", getDoubleValue(Math.abs(fdAgentAmtB)));
		parm.setData("UNREIM_AMT", getDoubleValue(unReimAmt));
		parm.setData("UNREIM_AMT_B", getDoubleValue(Math.abs(unReimAmtB)));
		TParm result = null;
		if (insOpdParm.getInt("INS_TYPE") == 2) {
			result = INSTJTool.getInstance().DataDown_mts_M(parm);
		} else if (insOpdParm.getInt("INS_TYPE") == 3) {
			result = INSTJTool.getInstance().DataDown_cmts_M(parm);
		}
		// if (!INSTJTool.getInstance().getErrParm(result)) {
		// // System.out.println("������ʧ��");
		// return result;
		// }
		return result;

	}

	private double getDoubleValue(double amt) {
		return StringTool.round(amt, 2);
	}

	/**
	 * ��ְ��ͨ ��ϸ����
	 * 
	 * @param parm
	 * @return
	 */
	private TParm sumOpdOrderChZPt(TParm parm) {
		TParm result = INSTJTool.getInstance().DataDown_rs_M(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("����ϸ��ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ���� ��ϸ����
	 * 
	 * @param parm
	 * @return
	 */
	private TParm sumOpdOrderChZMt(TParm parm) {
		TParm result = INSTJTool.getInstance().DataDown_mtd_E(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("����ϸ��ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * ��ְ���� ��ϸ����
	 * 
	 * @param parm
	 * @return
	 */
	private TParm sumOpdOrderChJMt(TParm parm) {
		TParm result = INSTJTool.getInstance().DataDown_cmtd_E(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			// System.out.println("����ϸ��ʧ��");
			return result;
		}
		return result;
	}

	/**
	 * �����ϸ���������� ��ְ��ͨ
	 * 
	 * @return
	 */
	public TParm getOpdOrderChZPtParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm chZPtParm = opdOrderCommParm(parm, opdReturnParm, mzConfirmParm);
		chZPtParm.setData("TOTAL_AMT", opdReturnParm.getDouble("TOT_AMT"));// 42
		// TOTAL_AMT
		// �������
		// ����
		chZPtParm.setData("OTOT_AMT", opdReturnParm.getDouble("OTOT_AMT"));// 46
		// OTOT_AMT
		// ר������籣֧��
		// -----

		// ------
		chZPtParm.setData("HOSP_OPT_USER_CODE", parm.getData("OPT_USER"));// 48
		// HOSP_OPT_USER_CODE
		// ҽԺ����Ա���
		// -----
		return chZPtParm;
	}

	/**
	 * ��ϸ���� �������ݹ��ò���
	 * 
	 * @param parm
	 * @return
	 */
	private TParm opdOrderCommParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm insParm = new TParm();
		insParm.setData("CONFIRM_NO", opdReturnParm.getValue("CONFIRM_NO"));// 8
		// CONFIRM_NO
		// ����˳���
		// ������
		insParm.setData("HOSP_NHI_NO", parm.getValue("HOSP_NHI_NO"));// 9
		// HOSP_NHI_NO
		insParm.setData("NHI_AMT", opdReturnParm.getDouble("NHI_AMT"));// 43
		// NHI_AMT
		// �걨���
		// -----
		insParm.setData("ARMY_AI_AMT", opdReturnParm.getDouble("ARMY_AI_AMT"));// 47
																				// �������

		insParm.setData("REIM_TYPE", opdReturnParm.getValue("REIM_TYPE"));// 47
																			// �������
		insParm.setData("SPECIAL_PAT", mzConfirmParm.getValue("SPECIAL_PAT"));// 47
																				// ������Ա

		insParm.setData("ACCOUNT_PAY_AMT", opdReturnParm
				.getDouble("ACCOUNT_PAY_AMT"));// 47 ACCOUNT_PAY_AMT �����ʻ�ʵ��֧�����

		insParm.setData("CONTER_DATE", parm.getData("INS_DATE"));// 49// ���ý���ʱ��
		insParm.setData("PAY_KIND", mzConfirmParm.getData("PAY_KIND"));// 50//
																		// PAY_KIND
		// ֧�����---??��ְ��ͨ
		insParm.setData("INSAMT_FLG", opdReturnParm.getValue("INSAMT_FLG"));// 50
		insParm.setData("OWN_AMT", opdReturnParm.getDouble("OWN_AMT"));// 45�Էѽ��
		insParm.setData("PAT_NAME", mzConfirmParm.getValue("PAT_NAME"));// 14
																		// NAME
		// ����
		// ----
		insParm.setData("SEX_CODE", mzConfirmParm.getValue("SEX_CODE"));// 15
																		// SEX
		// OWN_AMT
		// �Է���Ŀ���
		// ������
		if (parm.getValue("RECP_TYPE").equals("REGT")
				|| parm.getValue("RECP_TYPE").equals("OPBT")) {
			insParm.setData("PAY_BACK_FLG", 1);// 51 PAY_BACK_FLG �˷ѱ�־ ������ 0/1 0
			// ���� 1 �˷�
		} else {
			insParm.setData("PAY_BACK_FLG", 0);// 51 PAY_BACK_FLG �˷ѱ�־ ������ 0/1 0
			// ���� 1 �˷�
		}
		insParm.setData("PAT_TYPE", mzConfirmParm.getValue("CTZ_CODE"));// 18
		// CTZ_CODE
		// ��Ա���
		// ---11/21/51
		// 11
		// ��ְ
		// 21
		// ����
		// 51
		// ����ǰ�Ϲ���
		insParm.setData("UNIT_DESC", mzConfirmParm.getValue("UNIT_DESC"));// 21
		// UNIT_DESC
		// ��λ����
		// ----

		insParm.setData("ADD_AMT", opdReturnParm.getDouble("ADD_AMT"));// 45
		// ADDPAY_AMT
		// �������
		// ------
		return insParm;
	}

	/**
	 * �����ϸ����������
	 * 
	 * �Ǿ�����
	 * 
	 * @return
	 */
	public TParm getOpdOrderChJMtParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm chJMtParm = opdOrderCommParm(parm, opdReturnParm, mzConfirmParm);
		chJMtParm.setData("DISEASE_CODE", mzConfirmParm
				.getValue("DISEASE_CODE"));// 7 DISEASE_CODE ������� 1 ��͸�� 2 ����ֲ������
		// 3 ��֢�Ż��� 21 Ѫ�Ѳ� 22 ����ֲ������������ 4
		// ���� 5 ���Ĳ� 6 ����Ǵ� 7 ���� 8 ƫ̱
		chJMtParm.setData("SUM_BATCH", "SUM_BATCH");// 36��������
		// ҽ�ƾ�������޶����Ͻ�� =====
		chJMtParm.setData("BCSSQF_STANDRD_AMT", opdReturnParm
				.getDouble("BCSSQF_STANDRD_AMT"));// ����ʵ���𸶱�׼���
		// ======
		chJMtParm.setData("INS_STANDARD_AMT", opdReturnParm
				.getDouble("INS_STANDARD_AMT"));// �𸶱�׼�����Ը��������
		// =====
		chJMtParm.setData("OPT_USER", opdReturnParm.getValue("OPT_USER"));// ���ز���Ա
		chJMtParm.setData("TRANBLOOD_OWN_AMT", opdReturnParm
				.getDouble("TRANBLOOD_OWN_AMT"));// ҽ�ƾ�������޶����Ͻ��
		chJMtParm.setData("PERCOPAYMENT_RATE_AMT", opdReturnParm
				.getDouble("PERCOPAYMENT_RATE_AMT"));// ҽ�ƾ������˰������������
		chJMtParm.setData("INS_HIGHLIMIT_AMT", opdReturnParm
				.getDouble("INS_HIGHLIMIT_AMT"));// ҽ�ƾ�������޶����Ͻ��
		chJMtParm.setData("TOTAL_AGENT_AMT", opdReturnParm
				.getDouble("TOTAL_AGENT_AMT"));// ͳ�����֧��ҽԺ����
		return chJMtParm;
	}

	/**
	 * �����ϸ���������� ��ְ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOpdOrderChZMtParm(TParm parm, TParm opdReturnParm,
			TParm mzConfirmParm) {
		TParm chJMtParm = getOpdOrderChJMtParm(parm, opdReturnParm,
				mzConfirmParm);
		chJMtParm.setData("ARMY_AI_AMT1", opdReturnParm
				.getDouble("ARMY_AI_AMT1"));// �������1
		return chJMtParm;
	}
}
