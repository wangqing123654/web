package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: סԺҽ������
 * </p>
 * 
 * <p>
 * Description: סԺҽ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-1-27
 * @version 1.0
 */
public class INSTJAdm extends TJDODBTool {

	public INSTJAdm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * ʵ��
	 */
	public static INSTJAdm instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static INSTJAdm getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJAdm();
		return instanceObject;
	}

	/**
	 * סԺ�ʸ�ȷ���鿪������
	 * 
	 * @param mapParm
	 * @return
	 */
	public Map onAdmConfirmOpen(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		TParm blockadeMessParm = new TParm();// ����������Ϣ����
		if (parm.getInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			// סԺ��ְ ��ø��˷�����Ϣ ����
			blockadeMessParm = INSTJTool.getInstance().DataDown_sp_D(parm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			// סԺ�Ǿ� ��ø��˷�����Ϣ ����
			blockadeMessParm = INSTJTool.getInstance().DataDown_czys_B(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(blockadeMessParm)) {
			return blockadeMessParm.getData();
		}
		// �ж��Ƿ��ֽ�֧��
		StringBuffer message = new StringBuffer();
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(0,
				1))
				&& "0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE")
						.substring(1, 2))
				&& "0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE")
						.substring(2, 3))
				&& "0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE")
						.substring(3, 4))) {
			message.append("�����ܻ���ҽ�Ʊ���\n������ҽ�ƾ���\n������������\n�����ܸ����˻�֧��\n,�ֽ�֧��");// �����ܻ���ҽ�Ʊ���\n������ҽ�ƾ���\n������������\n�����ܸ����˻�֧��\n
			result.setData("MESSAGE", message.toString());
			result.setData("FLG", "Y");// ��Ϣ�����������ִ�в���
			return result.getData();
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(0,
				1))) {
			message.append("�����ܻ���ҽ�Ʊ���\n");
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(1,
				2))) {
			message.append("������ҽ�ƾ���\n");
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(2,
				3))) {
			message.append("������������\n");
		}
		if ("0".equals(blockadeMessParm.getValue("BLOCKAGE_GENRE").substring(3,
				4))) {
			message.append("�����ܸ����˻�֧��\n");
		}
		if ("01".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("����һ�����");
		}
		if ("02".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("���ܶ������");
		}
		if ("03".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("�����������");
		}
		if ("04".equals(blockadeMessParm.getValue("TREATMENT_CLASS"))) {
			message.append("�����������");
		}
		TParm exeParm = new TParm();// ��;��ѯ�ز�
		if (parm.getInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			// סԺ��ְ �����ʸ�ȷ����
			exeParm = INSTJTool.getInstance().DataDown_sp_B(parm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			// סԺ�Ǿ� �����ʸ�ȷ����
			exeParm = INSTJTool.getInstance().DataDown_czys_C(parm);

		}
		if (!INSTJTool.getInstance().getErrParm(exeParm)) {
			onDownConcel(parm);
			return exeParm.getData();
		}
		exeParm.setData("CROWD_TYPE", parm.getValue("CROWD_TYPE"));// ��Ⱥ���
		// // �Ǿ���;��ѯ
		// if (parm.getInt("CROWD_TYPE") == 2) {
		// boolean exeFlg = true;// �ж��Ƿ������;
		// runParm = INSTJTool.getInstance().DataDown_czys_N(parm);
		// if (!INSTJTool.getInstance().getErrParm(runParm)) {
		// exeFlg = false;
		// }
		// // ������;
		// if (exeFlg) {
		// // �����ڳ�Ժ�������--��������
		// }
		// parm.setData("HOSP_NHI_DESC", runParm.getValue("HOSP_NHI_DESC"));//
		// ҽ��ҽԺ����
		// }
		TParm tempParm = new TParm();
		// parm.setData("CANCEL_FLG","N");
		TParm queryParm = queryParm(exeParm);
		tempParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
		if (tempParm.getCount() > 0) {
			tempParm.setData("MESSAGE", "���ʸ�ȷ���������ػ�����");
			return tempParm.getData();
		}
		tempParm = newParm(exeParm, parm);
		System.out.println("tempParm:"+tempParm);
		tempParm.setData("SPECIAL_PAT",blockadeMessParm.getValue("SP_PRESON_TYPE"));//������Ա���
		result = insertData(tempParm, exeParm, parm);
		if (result.getErrCode() < 0) {
			// onDownConcel(exeParm);
			result.setErr(-1, "�����������ʧ��");
			return result.getData();
		}
		result.setData("NEWMESSAGE", message.toString());// ��ʾ�����Ϣ��
		return result.getData();
	}

	/**
	 * ִ������޸ı����
	 * 
	 * @param tempParm
	 * @param exeParm
	 * @param parm
	 * @return
	 */
	private TParm insertData(TParm tempParm, TParm exeParm, TParm parm) {
		TParm result = new TParm();
		TConnection connection = getConnection();
		// System.out.println("���insertConfirmApply���ݣ�" + tempParm);
		// ����������INS_ADM_CONFIRM ��
		result = INSADMConfirmTool.getInstance().insertConfirmApply(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			// onDownConcel(exeParm);
			return result;
		}
		tempParm.setData("RESV_NO", parm.getValue("RESV_NO"));// ԤԼ����
		result = onSaveAdm(tempParm, parm.getInt("CROWD_TYPE"), tempParm
				.getValue("HIS_CTZ_CODE"), connection, true);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �ڿ����ʸ�ȷ�����ʱ��update Ins_OrdM Confirm_NO=xxx Adm_seq= xxx where Case_No=
		// xx And year_mon=xx
		// result=INSIbsTool.getInstance().updateINSIbsConfirmNo(tempParm,
		// connection);
		// if (result.getErrCode() < 0) {
		// connection.close();
		// return result.getData();
		// }
		// insert into ibs_ctz(HOSP_AREA, CASE_NO, BEGIN_DATE,MR_NO, BED_NO,
		// CTZ_CODE,CTZ_CODE1, CTZ_CODE2,OPT_USER,OPT_TERM, OPT_DATE)
		// TParm ibsParm=new TParm();
		// ibsParm.setData("REGION_CODE",parm.getValue("REGION_CODE"));//ҽԺ����
		// ibsParm.setData("CASE_NO",tempParm.getValue("CASE_NO"));//�����
		// ibsParm.setData("BEGIN_DATE",tempParm.getValue("IN_HOSP_NO"));//��ʼʱ��
		// ibsParm.setData("BED_NO",tempParm.getValue("BED_NO"));//������
		// ibsParm.setData("MR_NO",tempParm.getValue("MR_NO"));//������
		// ibsParm.setData("CTZ_CODE",tempParm.getValue("CTZ1_CODE"));//���
		// ibsParm.setData("CTZ_CODE1","");//���1
		// ibsParm.setData("CTZ_CODE2","");//���2
		// ibsParm.setData("OPT_USER",tempParm.getValue("OPT_USER"));//ID
		// ibsParm.setData("OPT_TERM",tempParm.getValue("OPT_TERM"));//IP
		// result=IBSCtzTool.getInstance().insertIBSCtz(queryParm, connection);
		// if (result.getErrCode() < 0) {
		// connection.close();
		// onDownConcel(exeParm);
		// return result.getData();
		// }
		connection.commit();
		connection.close();
		TParm queryParm = queryParm(tempParm);
		result = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);// ���²�ѯ����
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * �޸� ADM_INP \ADM_RESV ���� CONFIRM_NO \CTZ1_CODE
	 * 
	 * @param tempParm
	 * @param parm
	 * @param exeParm
	 * @param connection
	 * @return
	 */
	private TParm onSaveAdm(TParm tempParm, int crowdType, String ctz_code,
			TConnection connection, boolean flg) {
		// ��ѯ���
		tempParm.setData("CTZ1_CODE", ctz_code);
		// Update Adm_Resv Set Confirm_No=xxx,CTZ1_CODE =xxx
		// ������ԤԼ����,�ʸ�ȷ������
		TParm result = ADMResvTool.getInstance().updateResvConfirmNo(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			// onDownConcel(exeParm);
			return result;
		}
		// �޸� Adm_Inp Confirm_NO=xxx ,CTZ1_CODE =xxx where case_no
		// ���������,�ʸ�ȷ������,CASE_NO
		result = ADMInpTool.getInstance().updateINPConfirmNo(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			// onDownConcel(exeParm);
			return result;
		}
		return result;
	}

	/**
	 * ͨ��ҽ�����β�ѯ������Ա������
	 * 
	 * @param crowType
	 * @param exeParm
	 * @return
	 */
	private String getCtzCode(int crowType, TParm exeParm) {
		String ctz_code = "";
		String sql = "";
		if (crowType == 1) {// 1 ��ְ 2.�Ǿ�
			ctz_code = exeParm.getValue("CTZ_CODE");
			sql = " AND CTZ_CODE IN('11','12','13')";
		} else if (crowType == 2) {// 1 ��ְ 2.�Ǿ�
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('21','22','23')";
		}
		StringBuffer messSql = new StringBuffer();
		messSql.append(
				"SELECT CTZ_CODE,NHI_NO FROM SYS_CTZ WHERE NHI_NO='" + ctz_code
						+ "' AND NHI_CTZ_FLG='Y' ").append(sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				messSql.toString()));
		if (ctzParm.getErrCode() < 0) {
			return "";
		}
		return ctzParm.getValue("CTZ_CODE", 0);
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param tempParm
	 * @return
	 */
	private TParm queryParm(TParm tempParm) {
		TParm queryParm = new TParm();
		if (null != tempParm.getValue("INSCASE_NO")
				&& tempParm.getValue("INSCASE_NO").length() > 0)
			queryParm.setData("INSCASE_NO", tempParm.getValue("INSCASE_NO"));// �������
		queryParm.setData("CONFIRM_NO", tempParm.getValue("CONFIRM_NO"));// �ʸ�ȷ�������
		if (null != tempParm.getValue("CANCEL_FLG")
				&& tempParm.getValue("CANCEL_FLG").length() > 0) {
			queryParm.setData("CANCEL_FLG", tempParm.getValue("CANCEL_FLG"));// ȡ��ע��
		}
		return queryParm;
	}

	/**
	 * ��ѯҽ��������Ϣ ������ҽ�������� ��Ҫͨ�����֤���������ֽ�������
	 */
	public Map getOwnInfo(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		// ��Ⱥ���Ϊ�� û��ִ��ˢ������ ��Ҫ�ж��Ƿ���IDNO ͨ��IDNO����һ��ҽ������
		// ִ�� �������ֽ��׺������� DataDown_czyd ��A��-----��������û��ҽ������
		// TParm insParm = INSTJTool.getInstance().DataDown_czyd_A(parm);
		// if (!INSTJTool.getInstance().getErrParm(insParm)) {
		// insParm.setErr(-1, "�������ֽ���ʧ��");
		// return insParm.getData();
		// }
		parm.setData("CARD_NO", parm.getValue("IDNO"));// ���֤��
		// insParm.setData("NHI_REGION_CODE", parm.getValue("NHI_REGION_CODE"));
		parm.setData("TYPE", 2);// ��������:1�籣������2 ���֤����
		TParm insParm = INSTJTool.getInstance().DataDown_czys_A(parm);// �������ֻ�ò���ҽ����Ϣ
		// System.out.println("insParm:::" + insParm);
		if (!INSTJTool.getInstance().getErrParm(insParm)) {
			return insParm.getData();
		}
		insParm.setData("CARD_NO", parm.getValue("IDNO"));// ����
		insParm.setData("REGION_CODE", parm.getValue("NHI_REGION_CODE"));// ҽ���������
		return insParm.getData();
	}

	/**
	 * ���INS_ADM_CONFRIM ����
	 * 
	 * @param parm
	 * @param insParm
	 * @return
	 */
	private TParm newParm(TParm parm, TParm insParm) {
		TParm newParm = new TParm();

		newParm.setData("IN_HOSP_NO", insParm.getData("NHI_REGION_CODE"));// ҽ��ҽԺ����

		// newParm.setData("ADM_CATEGORY",
		// insParm.getData("INS_ADM_CATEGORY1"));// ��ҽ���
		// newParm.setData("ADM_CATEGORY",
		// insParm.getData("INS_ADM_CATEGORY1"));// ��ҽ���
		// newParm.setData("ADM_CATEGORY",
		// insParm.getData("INS_ADM_CATEGORY1"));// ��ҽ���
		newParm.setData("HOSP_CLASS_CODE", insParm.getData("HOSP_CLASS_CODE"));// ҽԺ�ȼ�
		// newParm.setData("CTZ1_CODE",
		// blockadeMessParm.getData("CTZ1_CODE"));// ���---��ѯADM_INP
		newParm.setData("TRAN_CLASS", "");// ת��ҽԺ�ȼ�
		// newParm.setData("DS_DATE", insParm.getData("DS_DATE"));//
		// ��Ժʱ��----????
		// newParm.setData("DSDIAG_CODE", insParm.getData("DSDIAG_CODE"));//
		// ��Ժ���----????
		// newParm.setData("DSDIAG_DESC", insParm.getData("DSDIAG_DESC"));//
		// ��Ժ�������----????
		// newParm.setData("DSDIAG_DESC2", insParm.getData("DSDIAG_DESC2"));//
		// ��Ժ�������----????
		newParm.setData("ADM_CATEGORY", insParm.getData("ADM_CATEGORY1"));// ��ҽ���
		newParm.setData("ADM_PRJ", insParm.getData("ADM_PRJ1"));// ��ҽ��Ŀ
		newParm.setData("SPEDRS_CODE", insParm.getData("SPEDRS_CODE1") );// �������

		newParm.setData("MR_NO", insParm.getData("MR_NO"));// ������
		newParm.setData("OVERINP_FLG", null != insParm
				.getData("SFBEST_TRANHOSP")
				&& insParm.getData("SFBEST_TRANHOSP").equals("1") ? "Y" : "N");// �Ƿ����
		// 0
		// OR
		// 1//�����סԺ
		newParm.setData("OPEN_FLG", "Y");// ��Ժ����ע��
		newParm.setData("SOURCE_CODE", "");// ��Ժ���
		newParm.setData("CANCEL_FLG", "N");// ȡ��ע�ǣ�Y��ȡ����N��δȡ����
		newParm.setData("CONFIRM_SRC", "");// �ʸ�ȷ������Դ
		newParm.setData("OPT_USER", insParm.getData("OPT_USER"));// ������Ա

		newParm.setData("OPT_TERM", insParm.getData("OPT_TERM"));// ������ĩ

		newParm.setData("IN_STATUS", insParm.getData("IN_STATUS"));// ��Ժ״̬
		// ��ѯ
		// ADM_INP
		// .PATIENT_CONDITION
		newParm.setData("UP_DATE", null);// ҽ���ϴ�����---��������
		newParm.setData("DOWN_DATE", null);// ҽ����������---��������

		newParm.setData("CASE_NO", insParm.getData("CASE_NO"));// �����
		newParm.setData("DEPT_CODE", insParm.getData("DEPT_CODE1"));// �Ʊ����

		newParm.setData("INSBRANCH_CODE", getValueName(parm
				.getValue("INSBRANCH_CODE")));// ������
		newParm.setData("CONFIRM_NO", parm.getData("CONFIRM_NO"));// �ʸ�ȷ������
		newParm.setData("NHIHOSP_NO",
				getValueName(parm.getValue("HOSP_NHI_NO")));// ҽԺ����
		newParm.setData("HOSP_CATE", getValueName(parm.getValue("HOSP_CATE")));// ҽԺ���HOSP_CATE---���ش���
		newParm.setData("PAY_TYPE", getValueName(parm.getValue("PAY_TYPE")));// ֧�����---���ش���
		newParm.setData("PERSONAL_NO", getValueName(parm.getValue("OWN_NO")));// ���˱���
		newParm.setData("IDNO", getValueName(parm.getValue("SID")));// ���֤��
		newParm.setData("PAT_NAME", getValueName(parm.getValue("NAME")));// ����
		newParm.setData("SEX_CODE", getValueName(parm.getValue("SEX")));// �Ա�1��2Ů
		newParm.setData("BIRTH_DATE", SystemTool.getInstance().getDateReplace(
				parm.getValue("BIRTH_DATE"), true));// ��������
		newParm.setData("PAT_AGE", parm.getData("FAT_AGE"));// ʵ������

		newParm.setData("INS_UNIT", getValueName(parm.getValue("INS_UNIT")));// �����籣����
		newParm.setData("UNIT_CODE", getValueName(parm.getValue("UNIT_CODE")));// ��λ����
		newParm.setData("UNIT_DESC", getValueName(parm.getValue("UNIT_DESC")));// ��λ����
		newParm.setData("DEPT_DESC", getValueName(parm.getValue("DEPT")));// �Ʊ�����
		newParm.setData("DIAG_DESC", getValueName(parm
				.getValue("HOSP_DIAGNOSE")));// סԺ����
		newParm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(
				parm.getValue("HOSP_START_DATE"), true));// סԺ��ʼʱ��
		newParm.setData("EMG_FLG", getValueName(parm.getValue("SF_EMERGENCY")));// �Ƿ��0����1���ǣ�
		newParm.setData("INP_TIME", parm.getData("INP_TIME"));// ��סԺ����
		newParm.setData("INS_FLG", getValueName(parm
				.getValue("SF_FEAST_SALVATION")));// ����ҽ�ƾ���
		// ��0��
		// ��1���ǣ�
		newParm.setData("TRANHOSP_NO", getValueName(parm
				.getValue("TRANHOSP_NO")));// ת��ҽԺ����
		newParm.setData("TRANHOSP_DESC", getValueName(parm
				.getValue("TRANHOSP_DESC")));// ת��ҽԺ����
		newParm.setData("TRAN_NUM", getValueName(parm
				.getValue("TRANHOSP_NUM_NO")));// ת��תԺ������
		newParm.setData("HOMEBED_TYPE", getValueName(parm
				.getValue("HOMEDIAG_CODE")));// �Ҵ��������
		newParm.setData("HOMEDIAG_DESC", getValueName(parm
				.getValue("HOMEDIAG_DESC")));// �Ҵ�����
		newParm.setData("HOMEBED_TIME", parm.getData("HOMEBED_TIME"));// ��Ҵ�����
		newParm.setData("HOMEBED_DAYS", parm.getData("HOMEBED_DAYS"));// ��Ҵ��ۼ�����
		newParm.setData("ADDINS_AMT", parm.getData("ADDFEE_AMT"));// �ۼƷ���ҽ�Ʒ��ý��
		newParm.setData("ADDOWN_AMT", parm.getData("ADDOWN_AMT"));// �ۼ��Է���Ŀ���
		newParm.setData("ADDPAY_AMT", parm.getData("ADDADD_AMT"));// �ۼ�������Ŀ���
		newParm.setData("ADDNUM_AMT", parm.getData("ADDNUM_AMT"));// �ۼ��������
		newParm.setData("INSBASE_LIMIT_BALANCE", parm
				.getData("INSBASE_LIMIT_BALANCE"));// �����ҽ�Ʊ������֧���޶�ʣ���
		newParm.setData("INS_LIMIT_BALANCE", parm.getData("INS_LIMIT_BALANCE"));// ��ҽ�ƾ������֧���޶�ʣ
		newParm.setData("START_STANDARD_AMT", parm.getDouble("STANDARD"));// �𸶱�׼
		newParm.setData("RESTART_STANDARD_AMT", parm
				.getData("RESTART_STANDARD_AMT"));// ����ʵ���𸶱�׼
		newParm.setData("OWN_RATE", getRateDouble(parm.getDouble("OWN_RATE")));// �Ը�����
		newParm.setData("DECREASE_RATE", getRateDouble(parm
				.getDouble("DECREASE_RATE")));// ��������
		newParm.setData("REALOWN_RATE", getRateDouble(parm
				.getDouble("REALOWN_RATE")));// ʵ���Ը�����
		newParm.setData("INSOWN_RATE", getRateDouble(parm
				.getDouble("INSOWN_RATE")));// ҽ�ƾ����Ը�����
		if (insParm.getInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			newParm.setData("INSCASE_NO",
					getValueName(parm.getValue("CASE_NO")));// סԺ��
			newParm.setData("CTZ1_CODE", parm.getData("CTZ_CODE"));// ��Ա���
			String ctzCode = getCtzCode(insParm.getInt("CROWD_TYPE"), parm);// ���ҽԺ��Ա������
			newParm.setData("HIS_CTZ_CODE", ctzCode);// ��Ժ��Ա���
			newParm.setData("UNIT_NO", parm.getData("COM_CODE"));// ��λ����
			newParm.setData("ZFBL2", getRateDouble(parm.getDouble("ZFBL2")));// ZFBL2
			// �Ը�����2
			newParm.setData("IN_HOSP_NAME", "");// ҽ��ҽԺ����
		} else if (insParm.getInt("CROWD_TYPE") == 2) {// 1 ��ְ 2.�Ǿ�
			newParm.setData("INSCASE_NO", getValueName(parm
					.getValue("INSCASE_NO")));// סԺ��
			newParm.setData("CTZ1_CODE",
					getValueName(parm.getValue("PAT_TYPE")));// ��Ա���
			String ctzCode = getCtzCode(insParm.getInt("CROWD_TYPE"), parm);// ���ҽԺ��Ա������
			newParm.setData("HIS_CTZ_CODE", ctzCode);// ��Ժ��Ա���
			newParm.setData("UNIT_NO",
					getValueName(parm.getValue("UNIT_CODE1")));// ��λ����
			newParm.setData("ZFBL2", 0);// ZFBL2 �Ը�����2
			newParm.setData("IN_HOSP_NAME", insParm.getData("HOSP_NHI_DESC"));// ҽ��ҽԺ����
		}
		newParm.setData("INSOCC_CODE", insParm.getData("INSOCC_CODE1"));// ҽ�ƾ�����λ
		newParm.setData("ADM_SEQ", getValueName(parm.getValue("ADM_SEQ")));// ����˳���
		newParm.setData("STATION_DESC", getValueName(parm
				.getValue("INHOSP_AREA")));// סԺ����
		newParm.setData("BED_NO", getValueName(parm.getValue("INHOSP_BED_NO")));// סԺ��λ��
		newParm.setData("TRANHOSP_RESTANDARD_AMT", parm
				.getData("TRANHOSP_RESTART_STANDARD_AMT"));// ת��ҽԺʵ���𸶱�׼���
		newParm.setData("TRANHOSP_DAYS", getValueName(parm
				.getValue("TRANHOSP_DAY")));// תԺסԺ����
		newParm.setData("INLIMIT_DATE", SystemTool.getInstance()
				.getDateReplace(parm.getValue("BCTRANHOSP_AVA_DATE"), true));// ����סԺ��Чʱ��
		// newParm.setData("BIRTH_DATE", SystemTool.getInstance().getDate());
		// newParm.setData("INLIMIT_DATE",SystemTool.getInstance().getDate());
		return newParm;

	}

	/**
	 * ������ʾ����
	 * 
	 * @param rate
	 * @return
	 */
	public double getRateDouble(double rate) {
		return StringTool.round(rate * 100, 2);
	}

	/**
	 * У��Ϊ��ֵ
	 * 
	 * @param name
	 * @return
	 */
	private String getValueName(String name) {
		if (name.equals("null")) {
			return "";
		}
		if (null != name && name.length() > 0) {
			return name;
		}
		return "";
	}

	/**
	 * סԺ�ʸ�ȷ��������
	 * 
	 * @param mapParm
	 * @return
	 */
	public Map onAdmConfirmDown(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		TParm rsParm = new TParm();// �ʸ�ȷ�����Ų�ѯ����
		TParm admConfirmParm = parm.getParm("admConfirmParm");// ������ݿ���ʸ�ȷ���������
		// �����ʸ�ȷ�����Ų�ѯ����˳���
		// ===========pangben 2012-7-16 ҽ�����������Ƕ��� ��Ҫ�жϴ�����¼�Ƿ��Ѿ�ִ��
		if (parm.getInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			rsParm = INSTJTool.getInstance().DataDown_rs_B(parm, "");

		} else if (parm.getInt("CROWD_TYPE") == 2) {// 1 ��ְ 2.�Ǿ�
			rsParm = INSTJTool.getInstance().DataDown_czyd_B(parm, "");
		}
		if (!INSTJTool.getInstance().getErrParm(rsParm)) {
			return rsParm.getData();
		}
		int index = -1;
		// =====pangben 2012-7-16 �ж�����������û��ִ�в�������
		for (int i = 0; i < rsParm.getCount("ADM_SEQ"); i++) {
			String sql = "SELECT CONFIRM_NO FROM INS_ADM_CONFIRM WHERE CONFIRM_NO='"
					+ rsParm.getValue("CONFIRM_NO", i)
					+ "' AND IN_STATUS<>'5' AND (DS_DATE IS NULL OR DS_DATE='')";
			TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (insParm.getCount("CONFIRM_NO") > 0) {
				index = i;
				break;
			}
		}
		//���ݲ����ڽ������¼����ʸ�ȷ�������ͬ�����ݲ��뵽INS_AMD_CONFIRM ����
		if (index < 0) {
			for (int i = 0; i < rsParm.getCount("ADM_SEQ"); i++) {
				if (parm.getValue("CONFIRM_NO").equals(rsParm.getValue("CONFIRM_NO", i))) {
					index=i;
					break;
				}
			}
//			rsParm.setErr(-1, "û�л��ҽ���ʸ�ȷ�������");
//			return rsParm.getData();
		}
		if (index < 0) {
			rsParm.setErr(-1, "û�л��ҽ���ʸ�ȷ�������");
			return rsParm.getData();
		}
		parm.setData("ADM_SEQ", rsParm.getValue("ADM_SEQ", index));
		rsParm.setData("NHI_REGION_CODE", index, parm
				.getValue("NHI_REGION_CODE"));
		TParm resultParm = rsParm.getRow(index);// ��Ҫ����������
		resultParm.setData("NHI_REGION_CODE", parm.getValue("NHI_REGION_CODE"));
		if (parm.getInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			// ======pangben 2012-7-16�޸����
			result = INSTJTool.getInstance().DataDown_sp_A(resultParm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// 1 ��ְ 2.�Ǿ�
			result = INSTJTool.getInstance().DataDown_czys_E(resultParm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			//onDownConcel(parm);
			return result.getData();
		}
		// У���Ƿ��������
		TParm checkParm = INSADMConfirmTool.getInstance().queryCheckAdmComfirm(
				result);
		if (checkParm.getErrCode() < 0) {
			return checkParm.getData();
		}
		TParm confirmParm = result;
		parm.setData("ADM_SEQ", result.getValue("ADM_SEQ"));
		// ���ݿⲻ����ҽԺ���HOSP_CATE��֧�����PAY_TYPE����Ա���CTZ_CODE,ת�����TRANHOSP_TYPE
		// ������Ա���SP_PRESON_TYPE����Ա���PRESON_TYPE���ϴ��ʸ�ȷ������SCZGQRSBH
		// �ϴ��Է���Ŀ���SCZFXMJE,�ϴ�������Ŀ���SCZFXMJE,�ϴ��걨���SCSBJE,�ϴγ�Ժʱ��SCCYSJ,ר�Ƽ���ZKJB
		confirmParm.setData("NHIHOSP_NO", result.getValue("HOSP_NHI_NO"));// ҽԺ����
		confirmParm.setData("PERSONAL_NO", result.getValue("OWN_NO"));// ���˱���
		// ��ѯ���
		String ctz_code = getCtzCode(parm.getInt("CROWD_TYPE"), result);
		if (parm.getInt("CROWD_TYPE") == 1) {
			confirmParm.setData("CTZ1_CODE", result.getValue("CTZ_CODE"));// ��Ա���
		} else if (parm.getInt("CROWD_TYPE") == 2) {
			confirmParm.setData("CTZ1_CODE", result.getValue("PAT_TYPE"));// ��Ա���
		}
		confirmParm.setData("HIS_CTZ_CODE", ctz_code);// ��Ա���
		confirmParm.setData("IDNO", result.getValue("SID")); // ���֤��
		confirmParm.setData("PAT_NAME", result.getValue("NAME")); // ����
		confirmParm.setData("SEX_CODE", result.getValue("SEX")); // �Ա�
		confirmParm.setData("BIRTH_DATE", SystemTool.getInstance()
				.getDateReplace(result.getValue("BIRTH_DATE"), true)); // ��������
		confirmParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		confirmParm.setData("PAT_AGE", result.getValue("FAT_AGE")); // ����
		confirmParm.setData("UNIT_NO", result.getValue("COM_CODE")); // ��λ����
		confirmParm.setData("DEPT_DESC", result.getValue("DEPT")); // סԺ�Ʊ�
		confirmParm.setData("DIAG_DESC", result.getValue("HOSP_DIAGNOSE")); // סԺ����(��Ժ���)
		confirmParm.setData("IN_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("HOSP_START_DATE"), true)); // סԺ��ʼʱ��
		confirmParm.setData("EMG_FLG", null != result.getValue("SF_EMERGENCY")
				&& result.getValue("SF_EMERGENCY").equals("1") ? "Y" : "N");// �Ƿ���
		confirmParm.setData("INS_FLG", null != result
				.getValue("SF_FEAST_SALVATION")
				&& result.getValue("SF_FEAST_SALVATION").equals("1") ? "Y"
				: "N");// �Ƿ�����ҽ�ƾ���
		confirmParm.setData("TRAN_NUM", result.getValue("TRANHOSP_NUM_NO"));// ת��תԺ������
		confirmParm.setData("HOMEBED_TYPE", result.getValue("HOMEDIAG_CODE"));// �Ҵ��������
		confirmParm.setData("ADDINS_AMT", result.getDouble("ADDFEE_AMT"));// �ۼƷ���ҽ�Ʒ��ý��
		confirmParm.setData("ADDPAY_AMT", result.getDouble("ADDADD_AMT"));// �ۼ�������Ŀ���
		confirmParm.setData("START_STANDARD_AMT", result.getDouble("STANDARD"));// �𸶱�׼
		confirmParm.setData("STATION_DESC", result.getValue("INHOSP_AREA"));// סԺ����
		confirmParm.setData("BED_NO", result.getValue("INHOSP_BED_NO"));// סԺ��λ��
		confirmParm.setData("TRANHOSP_RESTANDARD_AMT", result
				.getDouble("TRANHOSP_RESTART_STANDARD_AMT"));// ת��ҽԺʵ���𸶱�׼���
		confirmParm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("DS_DATE"), true));// ��Ժʱ��
		confirmParm.setData("DSDIAG_CODE", result.getValue("DIAG_CODE"));// ��Ժ���
		confirmParm.setData("DSDIAG_DESC", result.getValue("DIAG_DESC"));// ��Ժ�������
		confirmParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ID
		confirmParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
		confirmParm.setData("DSDIAG_DESC2", result.getValue("DIAG_DESC2"));// ��Ժ�������
		confirmParm.setData("TRANHOSP_DAYS", result.getInt("TRANHOSP_DAY"));// תԺסԺ����
		confirmParm.setData("INLIMIT_DATE", SystemTool.getInstance()
				.getDateReplace(result.getValue("BCTRANHOSP_AVA_DATE"), true));// ����סԺ��Чʱ��
		confirmParm.setData("HOSP_CATE", getValueName(result
				.getValue("HOSP_CATE")));// ҽԺ���HOSP_CATE
		confirmParm.setData("PAY_TYPE", getValueName(result
				.getValue("PAY_TYPE")));// ֧�����--
		confirmParm.setData("ADM_PRJ", result.getValue("INPAT_ITEM"));// ��ҽ��Ŀ
		confirmParm.setData("SPEDRS_CODE", result.getValue("MT_DISEASE_CODE"));// ���ز���----���ݿ����ƣ��������
		confirmParm.setData("INSOCC_CODE", result.getValue("HELP_UNIT"));// ҽ�ƾ�����λ
		confirmParm.setData("DOWN_DATE", SystemTool.getInstance().getDate());// ҽ����������
		confirmParm.setData("PAT_CLASS", result.getValue("PRESON_TYPE"));// ��Ա���
		confirmParm.setData("IN_HOSP_NO", result.getValue("HOSP_NHI_NO"));// ��Ժ����
		confirmParm.setData("ENTERPRISES_TYPE", result.getValue("COM_TYPE"));// ��ҵ���
		confirmParm.setData("INJURY_CONFIRM_NO", result.getValue("GSZGQRSBH")); // �����ʸ�ȷ������
		confirmParm.setData("INSCASE_NO", result.getValue("CASE_NO"));// סԺҽ�������
		confirmParm.setData("OWN_RATE", getRateDouble(result
				.getDouble("OWN_RATE")));// �Ը�����
		confirmParm.setData("ZFBL2",
				getRateDouble(result.getDouble("OWN_RATE")));// �Ը�����
		confirmParm.setData("HOSP_CLASS_CODE",
				parm.getValue("HOSP_CLASS_CODE"));// ҽԺ�ȼ�===pangben 2012-8-14
		
		confirmParm.setData("DECREASE_RATE", getRateDouble(result
				.getDouble("DECREASE_RATE")));// ��������
		confirmParm.setData("REALOWN_RATE", getRateDouble(result
				.getDouble("REALOWN_RATE")));// ʵ���Ը�����
		confirmParm.setData("INSOWN_RATE", getRateDouble(result
				.getDouble("INSOWN_RATE")));// ҽ�ƾ����Ը�����
		confirmParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
		confirmParm.setData("ADM_CATEGORY", parm.getValue("ADM_CATEGORY"));// ��ҽ���
		confirmParm.setData("SPECIAL_PAT", result.getValue("SP_PRESON_TYPE"));// ������Ա���
		resultParm = new TParm();
		TConnection conn = getConnection();
		if (checkParm.getCount() > 0) {
			// �޸Ĳ���
			resultParm = INSADMConfirmTool.getInstance().updateConfirmApply(
					confirmParm, conn);
		} else {
			// ��Ժ״̬ :0-�ʸ�ȷ����¼�� 1-�����ѽ��� 2-�������ϴ� 3-��������� 4-������֧�� 5-����ȷ����
			// 6-�����ʸ�ȷ����ʧ��
			// 7-�ʸ�ȷ���������		
			confirmParm.setData("IN_STATUS", "0");// / ��Ժ״̬==pangben 2012-8-14
			// ��Ӳ���
			if (null != admConfirmParm
					&& null != admConfirmParm.getValue("CONFIRM_NO")
					&& admConfirmParm.getValue("CONFIRM_NO").length() > 0) {
				// �����ݿ��������¸�ֵ
				confirmParm.setData("HOSP_CLASS_CODE", admConfirmParm
						.getValue("HOSP_CLASS_CODE"));// ҽԺ�ȼ�
				confirmParm.setData("ADM_CATEGORY", admConfirmParm
						.getValue("ADM_CATEGORY"));// ��ҽ���
				confirmParm.setData("EMG_FLG", admConfirmParm
						.getValue("EMG_FLG"));// ����ע��
				confirmParm.setData("TRAN_CLASS", admConfirmParm
						.getValue("TRAN_CLASS"));// תԺ�ȼ�
				confirmParm.setData("OVERINP_FLG", "Y");// ����ע�� 0
				confirmParm.setData("CONFIRM_SRC", admConfirmParm
						.getValue("CONFIRM_SRC"));// �ʸ�ȷ������Դ
			} else {
				// û���������¸���ֵ
				String[] oldName = { "HOSP_CLASS_CODE", "ADM_CATEGORY",
						"EMG_FLG", "TRAN_CLASS", "CONFIRM_SRC", "IN_STATUS" };
				for (int i = 0; i < oldName.length; i++) {
					if (confirmParm.getValue(oldName[i]).equals("")) {
						confirmParm.setData(oldName[i], "");
					}

				}
			}
			confirmParm.setData("OVERINP_FLG", "N");// ����ע�� 0
			confirmParm.setData("OPEN_FLG", "Y");// ����ע��
			confirmParm.setData("CANCEL_FLG", "N");// ȡ��ע��
			confirmParm.setData("UP_DATE", SystemTool.getInstance().getDate());// �ϴ�ʱ��
			resultParm = INSADMConfirmTool.getInstance()
					.insertDownLoadAdmConfirm(confirmParm, conn);
		}

		if (resultParm.getErrCode() < 0) {
			// onDownConcel(parm);
			conn.close();
			return resultParm.getData();
		}
		TParm tempParm = new TParm();
		tempParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO"));
		tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		tempParm.setData("RESV_NO", parm.getValue("RESV_NO"));
		result = onSaveAdm(tempParm, parm.getInt("CROWD_TYPE"), ctz_code, conn,
				false);
		if (result.getErrCode() < 0) {
			return result.getData();
		}
		conn.commit();
		conn.close();
		return result.getData();
	}

	/**
	 * �ʸ�ȷ�������س�������
	 * 
	 * @param parm
	 */
	private void onDownConcel(TParm parm) {
		TParm concelParm = null;
		if (parm.getInt("CROWD_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			concelParm = INSTJTool.getInstance().DataDown_sp_C(parm);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// 1 ��ְ 2.�Ǿ�
			concelParm = INSTJTool.getInstance().DataDown_czys_F(parm);
		}
	}

	/**
	 * ���÷ָ� ���ý������
	 * 
	 * @param mapParm
	 * @return
	 */
	public Map onSettlement(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// ���÷ָ�
		TParm insAmtParm = INSIbsTool.getInstance().queryRetrunInsAmt(parm);
		// System.out.println("����ʹ�ò�ѯҽ����������"+insAmtParm);
		// ����ʹ�ò�ѯҽ����������
		if (insAmtParm.getErrCode() < 0) {
			return insAmtParm.getData();
		}
		// ���÷ָ� �������ȫ���ݲ�ѯ
		TParm allSumAmtParm = INSIbsUpLoadTool.getInstance().queryAllSumAmt(
				parm);
		// System.out.println("���÷ָ� �������ȫ���ݲ�ѯ"+allSumAmtParm);
		if (allSumAmtParm.getErrCode() < 0) {
			return allSumAmtParm.getData();
		}
		String insAdmSql = " SELECT INSOWN_RATE FROM INS_ADM_CONFIRM "
				+ "  WHERE CONFIRM_NO = '" + parm.getValue("CONFIRM_NO") + "' ";
		// System.out.println("insAdmSql>>>>>>>>>"+insAdmSql);
		TParm insAdmConfirm = new TParm(TJDODBTool.getInstance().select(
				insAdmSql));
		double salvationPaymentScale = 0.00;
		salvationPaymentScale = insAdmConfirm.getDouble("INSOWN_RATE", 0);
		// System.out.println("����֧������" + salvationPaymentScale);
		/**
		 * ��ѯADM_SEQ
		 */
		TParm seqParm = INSIbsTool.getInstance().queryAdmSeq(parm);
		// System.out.println("��ѯADM_SEQ"+seqParm);
		if (seqParm.getErrCode() < 0) {
			return seqParm.getData();
		}
		// �����������ʾ:1.ҽ���걨��� 2.�Էѽ�� 3.������� 4.�������
		String[] nameAmt = { "NHI_AMT", "OWN_AMT", "ADD_AMT", "AMT" };
		// 01.ҩƷ�ѣ�02.���ѣ�03.���Ʒѣ�04.�����ѣ�05.��λ�ѣ�06.���Ϸѣ�07.�����ѣ�08.ȫѪ�ѣ�09.�ɷ�Ѫ��
		String[] nameType = { "PHA_", "EXM_", "TREAT_", "OP_", "BED_",
				"MATERIAL_", "OTHER_", "BLOODALL_", "BLOOD_" };// �շѽ������
		TParm saveAmtParm = new TParm();
		int index = 0;// �м����ݲ���
		for (int j = 0; j < nameType.length; j++) {// ������� ѭ��nameType �о�������
			// Ҫ����insAmtParm���ݲ�ѯ��������ͬ
			for (int i = index; i < allSumAmtParm.getCount();) {
				TParm tempParm = allSumAmtParm.getRow(i);
				for (int k = 0; k < nameAmt.length; k++) {// ��ý����� ѭ��nameAmt
					// �����ֽ�� Ҫ����insAmtParm���ݿ��ѯ��������ͬ
					saveAmtParm.setData(nameType[j] + nameAmt[k], tempParm
							.getDouble(nameAmt[k]));
				}
				index++;
				break;
			}

		}
		double totAmt = 0.00;// �ϼ� �������
		double totOwnAmt = 0.00;// �ϼ��Էѽ��
		double totAddAmt = 0.00;// �ϼ� �������
		double totNhiAmt = 0.00;// �ϼ��걨���
		for (int j = 0; j < nameType.length; j++) 
		{
			totNhiAmt += saveAmtParm.getDouble(nameType[j] + nameAmt[0]);
			totOwnAmt += saveAmtParm.getDouble(nameType[j] + nameAmt[1]);
			totAddAmt += saveAmtParm.getDouble(nameType[j] + nameAmt[2]);
		}
		totAmt =  StringTool.round(totNhiAmt + totOwnAmt + totAddAmt, 2); 
		double materialAmt = 0.00;
		materialAmt = StringTool.round(saveAmtParm.getDouble("MATERIAL_OWN_AMT")+saveAmtParm.getDouble("MATERIAL_ADD_AMT")+saveAmtParm.getDouble("MATERIAL_NHI_AMT"), 2);
		saveAmtParm.setData("MATERIAL_AMT", materialAmt);
		String admSeq = seqParm.getValue("ADM_SEQ", 0);// ����˳���
		// INS_CROWD_TYPE :1.��ְ 2.�Ǿ�
		TParm settlement = new TParm();
		parm.setData("ADM_SEQ", admSeq);// ����˳���
		parm.setData("TOT_AMT", totAmt);
		parm.setData("OWN_AMT", totOwnAmt);
		parm.setData("ADD_AMT", totAddAmt);
		parm.setData("NHI_AMT", totNhiAmt);
		// System.out.println("���ý���parm:::" + parm);
		TParm amtParm = new TParm();
		amtParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));
		// ���ý���ʹ�� ���ҽ�� ���� ����INS_IBS ����
		amtParm = INSIbsTool.getInstance().queryInsAmt(amtParm);
		// System.out.println("���ý���ʹ�� ���ҽ�� ���� ����INS_IBS ����"+amtParm);
		if (amtParm.getErrCode() < 0) {
			return amtParm.getData();
		}
		// �����ֽ�������ӿ�
		if (null != parm.getValue("TYPE")
				&& parm.getValue("TYPE").equals("SINGLE")) {
			TParm tempParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT M.SDISEASE_CODE FROM ADM_INP P, INS_ADM_CONFIRM M "
					+ "WHERE P.CASE_NO= '" + parm.getValue("CASE_NO")
					+ "' " + "AND P.CONFIRM_NO=M.CONFIRM_NO"));
			if (tempParm.getErrCode() < 0) {
				return tempParm.getData();
			}
			parm.setData("SIN_DISEASE_CODE", tempParm.getValue("SDISEASE_CODE",
					0));// �����ֱ���
			parm.setData("IN_DAY", parm.getInt("ADM_DAYS")); // סԺ���� ������
			parm.setData("OUT_TIME", parm.getValue("DS_DATE"));// ��Ժʱ�� ������
			tempParm = new TParm(
					TJDODBTool
							.getInstance()
							.select(
									"SELECT I.SINGLE_NHI_AMT,I.SINGLE_STANDARD_OWN_AMT,I.SINGLE_SUPPLYING_AMT"
											+ ",I.STARTPAY_OWN_AMT,I.PERCOPAYMENT_RATE_AMT,I.BED_SINGLE_AMT,I.MATERIAL_SINGLE_AMT "
											+ " FROM INS_IBS I "
											+ " WHERE I.CASE_NO = '"
											+ parm.getValue("CASE_NO")
											+ "' AND I.YEAR_MON = '"
											+ parm.getValue("YEAR_MON") + "'"));
			if (tempParm.getErrCode() < 0) {
				return tempParm.getData();
			}
			// ��λ�������� �� ҽ�ò��Ϸ�������
			double sum = tempParm.getDouble("BED_SINGLE_AMT", 0)
					+ tempParm.getDouble("MATERIAL_SINGLE_AMT", 0);
			// ������Ŀ���
			parm.setData("SPECIAL_AMT", sum);
			//��ְ
			if (parm.getInt("INS_CROWD_TYPE") == 1) 
			   settlement = INSTJTool.getInstance().DataDown_sp_I1(parm);
			else 
			   settlement = INSTJTool.getInstance().DataDown_czys_G1(parm);
		
		} else {
			// ������������ӿ�
			if (parm.getInt("INS_CROWD_TYPE") == 1) {// ��ְ
				// System.out.println("��ְ�������"+parm);
				settlement = INSTJTool.getInstance().DataDown_sp_I(parm);
			} else if (parm.getInt("INS_CROWD_TYPE") == 2) {// �Ǿ�
				parm.setData("RESTART_STANDARD_AMT", insAmtParm.getDouble(
						"RESTART_STANDARD_AMT", 0));// ����ʵ���𸶱�׼
				parm.setData("FACT_PAYMENT_SCALE", parm
						.getDouble("REALOWN_RATE"));// ����ʵ���Ը����� ������
				parm.setData("SALVATION_PAYMENT_SCALE", salvationPaymentScale);// ����֧������
				parm.setData("APPLY1_AMT", insAmtParm.getDouble(
						"INS_LIMIT_BALANCE", 0));// ��ҽ�ƾ������֧���޶�ʣ���
				parm.setData("TOTAL_PAYMENT_AMT", insAmtParm.getDouble(
						"INSBASE_LIMIT_BALANCE", 0));// �����ҽ�Ʊ������֧���޶�ʣ���
				// System.out.println("�Ǿӽ������"+parm);
				settlement = INSTJTool.getInstance().DataDown_czys_G(parm);
			}
		}
		if (!INSTJTool.getInstance().getErrParm(settlement)) {
			result.setErr(-1, "���ý���ʧ��");
			return result.getData();
		}
		// �ۼ�����
		TConnection conn = getConnection();
		// �����ֲ�����������
		if (null != parm.getValue("TYPE")
				&& parm.getValue("TYPE").equals("SINGLE")) 
		{
			getInsIbsSingleParm(parm, amtParm, settlement, saveAmtParm);
			//System.out.println("saveAmtParm:"+saveAmtParm);
			result = INSIbsTool.getInstance().updateInsIbsSingleAmt(
					saveAmtParm, conn);
			//System.out.println("result:"+result);
			
		} else {
			getInsIbsParm(settlement, parm, saveAmtParm);
			// ����ʵ���𸶱�׼���
			saveAmtParm.setData("RESTART_STANDARD_AMT", settlement
					.getDouble("BCSSQF_STANDRD"));
			// ���÷ָ������ ���ý�������޸� ҽ���ز����ݱ���
			// System.out.println("����������"+saveAmtParm);
//			saveAmtParm.setData("MATERIAL_AMT", saveAmtParm
//					.getDouble("MATERIAL_AMT")
//					- parm.getDouble("ADDAMT"));
			result = INSIbsTool.getInstance()
					.updateInsIbsAmt(saveAmtParm, conn);
		}
		if (result.getErrCode() < 0) {
			conn.close();
			return result.getData();
		}
		// �޸���Ժ״̬ 0-�ʸ�ȷ����¼��1-�����ѽ�2-�������ϴ�3-���������4-������֧��5-����ȷ���� 6-�����ʸ�ȷ����ʧ��
		// 7-�ʸ�ȷ���������
		parm.setData("IN_STATUS", "1");//
		result = INSADMConfirmTool.getInstance().updateAdmConfrimForInStatus(
				parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result.getData();
		}
		conn.commit();
		conn.close();
		return result.getData();
	}

	/**
	 * ����޸�INS_IBS ����
	 * 
	 * @param settlement
	 *            ҽ���ӿڳ���
	 * @param parm
	 *            ��������
	 * @param saveAmtParm
	 *            ��Ҫ��õ�����
	 */
	private void getInsIbsParm(TParm settlement, TParm parm, TParm saveAmtParm) {
		saveAmtParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));// ����
		saveAmtParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		saveAmtParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		saveAmtParm.setData("YEAR_MON", parm.getValue("YEAR_MON"));// �ں�
		saveAmtParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
		saveAmtParm.setData("CHEMICAL_DESC", parm.getValue("CHEMICAL_DESC"));// ����˵��
		saveAmtParm.setData("DS_DATE", parm.getValue("DS_DATE"));// ��Ժʱ��
		saveAmtParm.setData("UPLOAD_FLG", parm.getValue("UPLOAD_FLG"));// �ϴ�ע��
		saveAmtParm.setData("STARTPAY_OWN_AMT", settlement
				.getDouble("TOTAL_OWN_AMT"));// �𸶱�׼�����Ը��������
		saveAmtParm.setData("INS_HIGHLIMIT_AMT", settlement
				.getDouble("EXCEED_OWN_AMT"));// �����Ը����
		saveAmtParm.setData("PERCOPAYMENT_RATE_AMT", settlement
				.getDouble("APPLY1_OWN_AMT"));// ҽ�ƾ����Ը����----ҽ�ƾ������˰������������
		saveAmtParm.setData("OWN_AMT", parm.getDouble("OWN_AMT"));// �Ը����
		saveAmtParm.setData("APPLY_AMT", 0.00);// ͳ�����֧��ҽԺ����
		saveAmtParm.setData("HOSP_APPLY_AMT", 0.00);// ҽ�ƾ����籣������
		saveAmtParm.setData("ADD_AMT", parm.getDouble("ADD_AMT"));// ������Ŀ���
		saveAmtParm.setData("NHI_PAY", settlement.getDouble("TOTAL_AGENT_AMT"));// ����ҽ���籣������
		saveAmtParm.setData("NHI_PAY_REAL", settlement
				.getDouble("TOTAL_AGENT_AMT"));// ����ҽ���籣������
		saveAmtParm.setData("NHI_COMMENT", settlement
				.getDouble("FLG_AGENT_AMT"));// ҽ�ƾ����籣֧�����
		saveAmtParm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));
		saveAmtParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));// �ʸ�ȷ������
		saveAmtParm.setData("ARMYAI_AMT", settlement.getDouble("AGENT_AMT"));// ���в������
	}

	/**
	 * �����ֲ��� ����޸�INS_IBS����
	 * 
	 * @param parm
	 * @param saveAmtParm
	 */
	private void getInsIbsSingleParm(TParm parm, TParm amtParm,
			TParm settlement, TParm saveAmtParm) 
	{
		// ����ʵ���𸶱�׼���
		saveAmtParm.setData("RESTART_STANDARD_AMT", amtParm.getDouble("RESTART_STANDARD_AMT", 0));
		// �����걨���
		saveAmtParm.setData("SINGLE_NHI_AMT", settlement.getDouble("NHI_OWN_AMT"));
		// �����Ը����
		saveAmtParm.setData("SINGLE_STANDARD_AMT", settlement.getDouble("OWN_AMT_AMT"));
		// ����ҽ�Ʊ��ղ�����
		saveAmtParm.setData("SINGLE_SUPPLYING_AMT", settlement.getDouble("COMP_AMT"));
		// ���β��ָ��ѱ�׼
		saveAmtParm.setData("SINGLE_STANDARD_AMT_T", settlement.getDouble("PAY_AMT_STD"));
		// ���β����Ը���׼
		saveAmtParm.setData("SINGLE_STANDARD_OWN_AMT_T", settlement.getDouble("OWN_AMT_STD"));
		// ҽԺ�����ֱ�׼�Ը����
		saveAmtParm.setData("SINGLE_STANDARD_OWN_AMT", settlement.getDouble("EXT_OWN_AMT"));
		//ͳ������Ը����
		saveAmtParm.setData("STARTPAY_OWN_AMT",settlement.getDouble("TOTAL_OWN_AMT"));
		//ҽ�ƾ����Ը����
		saveAmtParm.setData("PERCOPAYMENT_RATE_AMT",settlement.getDouble("APPLY1_OWN_AMT"));
		//�����Ը����
		saveAmtParm.setData("INS_HIGHLIMIT_AMT",settlement.getDouble("EXCEED_OWN_AMT"));
		//����ҽ���籣������
		saveAmtParm.setData("NHI_PAY", settlement.getDouble("TOTAL_AGENT_AMT"));
		saveAmtParm.setData("NHI_PAY_REAL", settlement.getDouble("TOTAL_AGENT_AMT"));
		//ҽ�ƾ����籣֧�����
		saveAmtParm.setData("NHI_COMMENT", settlement.getDouble("FLG_AGENT_AMT"));
		//�������
		saveAmtParm.setData("ARMYAI_AMT", settlement.getDouble("AGENT_AMT"));
		//����
		saveAmtParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		saveAmtParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		saveAmtParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		//�ں�
		saveAmtParm.setData("YEAR_MON", parm.getValue("YEAR_MON"));
		//����˵��
		saveAmtParm.setData("CHEMICAL_DESC", parm.getValue("CHEMICAL_DESC"));
		//��Ժʱ��
		saveAmtParm.setData("DS_DATE", parm.getValue("DS_DATE"));
		//�ϴ�ע��
		saveAmtParm.setData("UPLOAD_FLG", parm.getValue("UPLOAD_FLG"));
		//�����
		saveAmtParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		//�Ը����
		saveAmtParm.setData("OWN_AMT", parm.getDouble("OWN_AMT"));
		//������Ŀ���
		saveAmtParm.setData("ADD_AMT", parm.getDouble("ADD_AMT"));
		//����˳���
		saveAmtParm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));
		//�ʸ�ȷ������
		saveAmtParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));
		
	}
}
