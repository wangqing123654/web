package jdo.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOObject;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.taglibs.standard.tag.el.sql.SetDataSourceTag;

import jdo.sys.SystemTool;

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
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2011-11-07
 * @version 1.0
 */
public class INSTJReg extends TJDODBTool {
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	public INSTJReg() {
	}

	/**
	 * ʵ��
	 */
	public static INSTJReg instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static INSTJReg getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJReg();
		return instanceObject;
	}

	/**
	 * ��ְ��ͨ
	 * 
	 * @param parm
	 *            TParm
	 */
	public Map cityStaffCommon(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// �ж��Ƿ������;����
//		if (!INSTJFlow.getInstance().queryRun(parm)) {
//			result.setErr(-1, "����;���ݲ�����ִ��");
//			// parm.setData("EXE_TYPE", "REG");
//			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
//			// result.setErr(-1, "ɾ����;��¼��Ϣ����������");
//			// return result;
//			// }
//			return result.getData();
//		}
		//System.out.println("--------��ְ��ͨ-------------------" + parm);

		// ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
		// ִ�з��÷ָ� ������DataDown_sp1 ���� B
		// ִ���ϴ���ϸ ����: ����DataUpload,��B������
		//TParm comminuteFeeParm = parm.getParm("comminuteFeeParm");// ���÷ָ� ����ҽ��
		
		////System.out.println("���÷ָ�:::" + comminuteFeeParm);
		// ���ý���-------��ó��� ִ��ҽ�����ָ����
		// ����DataDown_sp, ��������ϴ����ף�M������
		//TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		// ��������������
		if (!INSTJFlow.getInstance().insSpcUpload(parm)) {
			err(result.getErrText());
			return result.getData();
		}
		// ����ȷ�� ����DataDown_sp���������ȷ�Ͻ��ף�R������ �ɹ��޸�INS_OPD ��INSAMT_FLG���˱�־3
		result = INSTJFlow.getInstance()
				.insSettleConfirmChZPt(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result.getData();
		}
		return new TParm().getData();
	}
	
	/**
	 * ���������ֽ�֧��
	 * 
	 * @return
	 */
	public Map resultParm(TParm parm, String errName, TConnection connection) {
//		if (null != parm && null != parm.getValue("MESSAGE")
//				&& parm.getValue("MESSAGE").length() > 0) {
//			TParm returnParm = new TParm();
//			returnParm.setData("MESSAGE", parm.getValue("MESSAGE"));
//			if (null != parm.getValue("FLG")
//					&& parm.getValue("FLG").equals("Y")) {
//				returnParm.setData("FLG", "Y");
//			}
//			return returnParm.getData();
//		}
		TParm result = new TParm();
		result.setErr(-1, errName);
		connection.close();
		return result.getData();
	}

	/**
	 * ��ְ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public Map cityStaffClement(Map mapParm) {
		// TConnection connection = getConnection();
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// �ж��Ƿ������;����
//		if (!INSTJFlow.getInstance().queryRun(parm)) {
//			result.setErr(-1, "����;���ݲ�����ִ��");
//			// parm.setData("EXE_TYPE", "REG");
//			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
//			// result.setErr(-1, "ɾ����;��¼��Ϣ����������");
//			// return result;
//			// }
//			return result.getData();
//		}
		//System.out.println("----------��ְ����--------------------" + parm);
		
		// ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
		// ִ�з��÷ָ� ������DataDown_sp1 ���� G
		// ִ���ϴ���ϸ ����: ����DataUpload,��C������
		//TParm comminuteFeeParm = parm.getParm("comminuteFeeParm");// ���÷ָ� ����ҽ��
		// ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
		////System.out.println("���÷ָ�:::" + comminuteFeeParm);

		// ������Ϣ�ϴ�:�ҺŲ���ִ��������Ϣ�ϴ�---�����շ����
		if (!INSTJFlow.getInstance().insSpcUpload(parm)) {
			result.setErr(-1, "��ְ����,������Ϣ�ϴ�����");
			err(result.getErrText());
			return result.getData();
		}
		// ���ý��� ����DataDown_mts, ��������ϴ����ף�F������
		//TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
//		// ��ְ���ط��ز�����ͳ��֧��ȷ�Ͻ��ף�����DataDown_mts, ����ͳ��֧��ȷ�Ͻ��ף�G��
//		result = INSTJFlow.getInstance().insPayAccountChZMt(parm);
//		if (result.getErrCode() < 0) {
//			err(result.getErrText());
//			return result.getData();
//		}
		// ����ȷ��:��ְ���� ����DataDown_mts���������ȷ�Ͻ��ף�I������
		result = INSTJFlow.getInstance()
				.insSettleConfirmChZMt(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result.getData();
		}
		return new TParm().getData();
	}

	/**
	 * ִ��ҽ������֮�󷵻ز���
	 * 
	 * @return
	 */
	private Map returnParm(TParm parm, TParm comminuteFeeParm) {
		TParm result = new TParm();
		result.setData("INS_PARM", parm.getData());// ���ý���
		result.setData("comminuteFeeParm", comminuteFeeParm.getData());// ���÷ָ�ز�����������վݷָ���Ϣ
		return result.getData();
	}

	/**
	 * �Ǿ�����
	 * 
	 * @param parm
	 *            TParm
	 */
	public Map specialCityDenizen(Map mapParm) {
		// TConnection connection = getConnection();
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// �ж��Ƿ������;����
//		if (!INSTJFlow.getInstance().queryRun(parm)) {
//			result.setErr(-1, "����;���ݲ�����ִ��");
//			// parm.setData("EXE_TYPE", "REG");
//			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
//			// result.setErr(-1, "ɾ����;��¼��Ϣ����������");
//			// return result;
//			// }
//			return result.getData();
//		}
		
		// ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
		// ִ�з��÷ָ� ������DataDown_sp1 ���� G
		// ִ���ϴ���ϸ ����: ����DataUpload,��F������
	//	TParm comminuteFeeParm = parm.getParm("comminuteFeeParm");// ���÷ָ� ����ҽ��
		// ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
	//	//System.out.println("���÷ָ�:::" + comminuteFeeParm);
		// ���ý��� ����DataDown_cmts, ��������ϴ����ף�F������
		//TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// ���ý������
		// ��������������
		// ������Ϣ�ϴ�:�ҺŲ���ִ��������Ϣ�ϴ�---�����շ����
		if (!INSTJFlow.getInstance().insSpcUpload(parm)) {
			result.setErr(-1, "�Ǿ�����,������Ϣ�ϴ�����");
			err(result.getErrText());
			return result.getData();
		}
		// ����ȷ��:�Ǿ����� ����DataDown_cmts���������ȷ�Ͻ��ף�I������
		result = INSTJFlow.getInstance()
				.insSettleConfirmChJMt(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result.getData();
		}
		return new TParm().getData();

	}

	/**
	 * ˢ������:ִ�� ˢ�� ��������ʶ���ײ���
	 * 
	 * @param parm
	 *            �������
	 * @return
	 */
	public TParm insConfirmApply(TParm parm) {
		TParm readParm = INSTJTool.getInstance().DataDown_sp_U(
				parm.getValue("TEXT"));// U����
		if (!INSTJTool.getInstance().getErrParm(readParm)) {
			readParm.setErr(-1, "ˢ������������ ");
			return readParm;
		}
		// returnParm.setData("readParm", readParm.getData());
		parm.setData("CARD_NO",readParm.getValue("CARD_NO"));//ҽ������
		TParm result = INSTJTool.getInstance().DataDown_czys_A(parm);// A����
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result.setErr(-1, "ˢ������������");
			return result;
		}
		// returnParm.setData("result", result.getData());
		return readParm;
	}

	/**
	 * ��ְ��ͨ �˷Ѳ���
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public Map insUNCityStaffCommonChZPt(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		//System.out.println("��ְ��ͨparm::::::"+parm);
		// ��ѯҪ�˷Ѽ�¼��Ϣ
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			result.setErr(-1, "��ְ��ͨ,����;���ݲ�����ִ��");
			return result.getData();
			// parm.setData("EXE_TYPE", "REGT");
			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
			// result.setErr(-1, "ɾ����;��¼��Ϣ����������");
			// return result;
			// }
		}
		System.out.println("--------------��ְ��ͨ �˷Ѳ���----------------");
		TConnection connection = getConnection();
		// �˷���ϸ���ɱ��ض��˱�־��0
		TParm insOPDParm = INSTJFlow.getInstance().resetInsetOpdandOpdOrder(
				parm, connection);
		if (insOPDParm.getErrCode() < 0) {
			return resultParm(result, insOPDParm.getErrText(), connection);
		}
		// �����˷�
		TParm resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZPt(parm,
				insOPDParm);
		
		//System.out.println("------------------------��ְ��ͨ �˷Ѳ���:::::::"+resetInsOPDParm);
		if (resetInsOPDParm.getErrCode()<0) {
			// �˷�ȡ������
			if (!INSTJFlow.getInstance().resetConcelFee(parm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(result, "��ְ��ͨ,�˷�ȡ������ʧ��", connection);
			}
			return resultParm(result, resetInsOPDParm.getErrText(), connection);
		}
		//System.out.println("resetInsOPDParm::::::"+resetInsOPDParm);
		int flg = resetInsOPDParm.getInt("INSAMT_FLG");
		//System.out.println("flg::::::::::::::::::::::" + flg);
		if (flg <= 0) {
			// �˷�ȡ������
			if (!INSTJFlow.getInstance().resetConcelFee(parm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(result, "��ְ��ͨ,�˷�ȡ������ʧ��", connection);
			}
			return resultParm(result, "��ְ��ͨ,�����˷Ѳ���������", connection);
		}
		if (flg == 1) {
			// �޸ı��ض��˱�־Ϊ1
			//System.out.println("�޸ı��ض��˱�־Ϊ1parm::::::"+parm);
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(parm, "1",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "��ְ��ͨ,�˷�ȡ������ʧ��", connection);
			}
		}
		insOPDParm.setData("NHI_REGION_CODE",parm.getValue("REGION_CODE"));
		// �˷�ȷ�Ͻ���
		result = INSTJFlow.getInstance().resetFeeInsSettleChZPt(insOPDParm,
				resetInsOPDParm);
		if (result.getErrCode()<0) {
			return resultParm(result, "��ְ��ͨ,�˷�ȡ������ʧ��", connection);
		}
		//System.out.println("��ְ��ͨ,�˷Ѳ���result::::"+result);
		flg = result.getInt("INS_FLG");
		if (flg <= 0) {
			return resultParm(result, "��ְ��ͨ,�˷�ȡ������ʧ��", connection);
		}
		if (flg == 3) {
			// �޸ı��ض��˱�־Ϊ3
			//System.out.println("�޸ı��ض��˱�־Ϊparm::::::"+parm);
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(parm, "3",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "��ְ��ͨ,�˷�ȡ������ʧ��", connection);
			}
		}
		// ��;״̬���
		parm.setData("EXE_TYPE", parm.getValue("UNRECP_TYPE"));
		if (!INSTJFlow.getInstance().insertRun(parm, "DataDown_yb_D",connection)) {
			return resultParm(result, "��ְ��ͨ,��;״̬�������������", connection);
		}
		//System.out.println("���MMMMMMMMMMMMMMMMMMMMMMMM"+result);
		connection.commit();
		connection.close();
		return result.getData();

	}
	/**
	 * �˷�ʹ��
	 * ҽ���������
	 * 
	 * @param parm
	 * @param insOPDParm
	 */
	private boolean resetGetInsParm(TParm parm,TParm insOPDParm){
		//��ѯҽ��������Ϣ
		TParm mzParm= INSMZConfirmTool.getInstance().queryMZConfirmOne(parm);
		if (mzParm.getErrCode()<0) {
			return false;
		}
		insOPDParm.setData("DISEASE_CODE",mzParm.getValue("DISEASE_CODE",0));//���ز���
		insOPDParm.setData("PAY_KIND",mzParm.getValue("PAY_KIND",0));//֧�����
		return true;
	}
	/**
	 * ��ְ���� �˷Ѳ���
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public Map insUNCityStaffClementChZMt(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		// ��ѯҪ�˷Ѽ�¼��Ϣ
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			result.setErr(-1, "��ְ����,����;���ݲ�����ִ��");
			return result.getData();
			// parm.setData("EXE_TYPE", "REGT");
			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
			// result.setErr(-1, "ɾ����;��¼��Ϣ����������");
			// return result;
			// }
		}
		TConnection connection = getConnection();
		// �˷���ϸ���ɱ��ض��˱�־��0
		TParm insOPDParm = INSTJFlow.getInstance().resetInsetOpdandOpdOrder(
				parm, connection);
		if (insOPDParm.getErrCode() < 0) {
			return resultParm(result, insOPDParm.getErrText(), connection);
		}
		if (!resetGetInsParm(parm, insOPDParm)) {
			connection.close();
			result.setErr(-1, "��ְ����,ҽ�����ݲ�ѯ������");
			return result.getData();
		}
		
		// �����˷�
		TParm resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZMt(parm,
				insOPDParm);
		if (resetInsOPDParm.getErrCode()<0) {
			return resultParm(result, "��ְ����,�˷�ȡ������ʧ��", connection);
		}
		int flg = resetInsOPDParm.getInt("SOCIAL_FLG");
		//System.out.println("FLG:::::::::::::::::::::::::::::" + flg);
		if (flg <= 0) {
			// �˷�ȡ������
			if (!INSTJFlow.getInstance().resetConcelFee(parm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(result, "��ְ����,�˷�ȡ������ʧ��", connection);
			}
			return resultParm(result, "��ְ����,�����˷Ѳ���������", connection);
		}
		if (flg == 1) {
			// �޸ı��ض��˱�־Ϊ1
			//System.out.println("insOPDParm:::::::" + insOPDParm);
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "1",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "��ְ���أ��˷�ȡ������ʧ��", connection);
			}
		}
		insOPDParm.setData("NHI_REGION_CODE",parm.getValue("REGION_CODE"));
		// �˷�ȷ�Ͻ���
		result = INSTJFlow.getInstance().resetFeeInsSettleChZMt(insOPDParm);
		if (result.getErrCode()<0) {
			return resultParm(result, "��ְ���أ��˷�ȡ������ʧ��", connection);
		}
		flg = result.getInt("SOCIAL_FLG");
		if (flg <= 0) {
			return resultParm(result, "��ְ���أ��˷�ȡ������ʧ��", connection);
		}
		if (flg == 3) {
			// �޸ı��ض��˱�־Ϊ3
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "3",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "��ְ���أ��˷�ȡ������ʧ��", connection);
			}
		}
		// ��;״̬���
		parm.setData("EXE_TYPE", parm.getValue("UNRECP_TYPE"));
		if (!INSTJFlow.getInstance().insertRun(parm, "DataDown_mts_L",connection)) {
			return resultParm(result, "��ְ����,��;״̬�������������", connection);
		}
		connection.commit();
		connection.close();
		return result.getData();// ����ֵ��Ҫ�޸�
	}

	/**
	 * �Ǿ����� �˷Ѳ���
	 * 
	 * @param parm
	 * @param connection
	 */
	public Map insUNCityStaffClementChJMt(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		// ��ѯҪ�˷Ѽ�¼��Ϣ
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			result.setErr(-1, "�Ǿ�����,����;���ݲ�����ִ��");
			return result.getData();
		}
		TConnection connection = getConnection();
		// �˷���ϸ���ɱ��ض��˱�־��0
		TParm insOPDParm = INSTJFlow.getInstance().resetInsetOpdandOpdOrder(
				parm, connection);
		if (insOPDParm.getErrCode() < 0) {
			return resultParm(result, insOPDParm.getErrText(), connection);
		}
		if (!resetGetInsParm(parm, insOPDParm)) {
			connection.close();
			result.setErr(-1, "��ְ����,ҽ�����ݲ�ѯ������");
			return result.getData();
		}
		// �����˷�
		TParm resetInsOPDParm = INSTJFlow.getInstance().resetFeeChJMt(parm,
				insOPDParm);
		if (resetInsOPDParm.getErrCode()<0) {
			return resultParm(result, "�Ǿ�����,�˷�ȡ������ʧ��", connection);
		}
		int flg = resetInsOPDParm.getInt("SOCIAL_FLG");
		//System.out.println("FLG:::::::::::::::" + flg);
		if (flg <= 0) {
			// �˷�ȡ������
			if (!INSTJFlow.getInstance().resetConcelFee(insOPDParm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(resetInsOPDParm, "�Ǿ�����,�˷�ȡ������ʧ��", connection);
			}
			return resultParm(resetInsOPDParm, "�Ǿ�����,�����˷Ѳ���������", connection);
		}
		if (flg == 1) {
			// �޸ı��ض��˱�־Ϊ1
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "1",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(resetInsOPDParm, "�Ǿ�����,�˷�ȡ������ʧ��", connection);
			}
		}
		insOPDParm.setData("NHI_REGION_CODE",parm.getValue("REGION_CODE"));
		// �˷�ȷ�Ͻ���
		result = INSTJFlow.getInstance().resetFeeInsSettleChJMt(insOPDParm);
		if (result.getErrCode()<0) {
			return resultParm(result, "�Ǿ�����,�˷�ȡ������ʧ��", connection);
		}
		flg = result.getInt("SOCIAL_FLG");
		if (flg <= 0) {
			return resultParm(resetInsOPDParm, "�Ǿ�����,�˷�ȡ������ʧ��", connection);
		}
		if (flg == 3) {
			// �޸ı��ض��˱�־Ϊ3
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "3",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(resetInsOPDParm, "�Ǿ�����,�˷�ȡ������ʧ��", connection);
			}
		}
		// ��;״̬���
		parm.setData("EXE_TYPE", parm.getValue("UNRECP_TYPE"));
		if (!INSTJFlow.getInstance().insertRun(parm, "DataDown_cmts_L",connection)) {
			return resultParm(result, "�Ǿ�����,��;״̬�������������", connection);
		}
		connection.commit();
		connection.close();
		return result.getData();
	}

	/**
	 * �������� ִ�У�1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
	 * 
	 * @param mapPam
	 *            ���ýӿڷ��س��� U ���� A ���� 2011-11-25
	 * @return
	 */
	
	public TParm insCommFunction(Map mapPam) {
		TParm parm = new TParm(mapPam);
		int insType = parm.getInt("INS_TYPE"); // 1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����
		Map result = new HashMap();
		// TConnection connection = getConnection();
		switch (insType) {

		case 1:// ��ְ��ͨ
			result = cityStaffCommon(mapPam);
			break;
		case 2:// ��ְ����
			result = cityStaffClement(mapPam);
			break;
		case 3:// �Ǿ�����
			result = specialCityDenizen(mapPam);
			break;
		}
		return new TParm(result);
	}

	/**
	 * �����˷ѷ���1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
	 * 
	 * @param mapPam
	 *            ��þ������:CASE_NO ҽ������� CONFIRM_NO ��ҽ��� INS_TYPE
	 */
	public TParm insResetCommFunction(Map mapPam) {
		TParm parm = new TParm(mapPam);
		int insType = parm.getInt("INS_TYPE"); // 1.��ְ ��ͨ 2.��ְ���� 3.�Ǿ�����
		Map result = new HashMap();
		//System.out.println("mapPam::::"+mapPam);
		switch (insType) {
		case 1:// ��ְ��ͨ
			result = insUNCityStaffCommonChZPt(mapPam);
			break;
		case 2:// ��ְ����
			result = insUNCityStaffClementChZMt(mapPam);
			break;
		case 3:// �Ǿ�����
			result = insUNCityStaffClementChJMt(mapPam);
			break;
		}
		return new TParm(result);
	}

	/**
	 * �����Զ�����
	 * 
	 * @return
	 */
	public Map insOPBAutoAccount(Map mapPam) {
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		return INSTJFlow.getInstance().autoAccountComm(parm);
	}

	/**
	 * �����Զ����� ��ְ��ͨ
	 * 
	 * @param mapPam
	 * @return
	 */
//	public Map insCfCommAutoChZPt(Map mapPam) {
//		TParm parm = new TParm(mapPam);
//		if (isClientlink())
//			return (Map) callServerMethod(mapPam);
//		INSTJFlow.getInstance().autoAccountComm(parm);
//		return new HashMap();
//	}

	/**
	 * ���������
	 * 
	 * @param mapPam
	 * @return
	 */
	public Map insOpdAccount(Map mapPam) {
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);

		TParm result = INSTJFlow.getInstance().opdAccountComm(parm);
		if (result.getErrCode() < 0) {
			return result.getData();
		} else {
			return result.getData();
		}
	}

	/**
	 * ��������˱���
	 * 
	 * @param parm
	 * @return
	 */
	public Map insOpdAccountSave(Map mapPam) {
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm = new TParm(mapPam);
		TConnection connection = getConnection();
		// �޸� INS_OPD \INS_OPD_ORDER
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);// ���һ��Ҫ���˵�����
			if (!INSTJFlow.getInstance().updateInsAmtFlg(tempParm, "5",
					tempParm.getValue("RECP_TYPE"))) {
				tempParm.setErr(-1, "�޸����ݳ���");
				connection.close();
				return tempParm.getData();
			}
		}
		connection.commit();
		connection.close();
		return new HashMap();
	}
	/**
	 * �������ϸ�˱���
	 * 
	 * @param parm
	 * @return
	 */
	public Map insOpdOrderAccountSave(Map mapPam){
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm = new TParm(mapPam);
		TConnection connection = getConnection();
		// �޸� INS_OPD \INS_OPD_ORDER
		TParm result = null;
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);// ���һ��Ҫ���˵�����
			result=INSTJFlow.getInstance().updateINSOpdOrder(tempParm, "5",
					tempParm.getValue("RECP_TYPE"));
			if (result.getErrCode()<0) {
				tempParm.setErr(-1, "�޸����ݳ���");
				connection.close();
				return tempParm.getData();
			}
		}
		connection.commit();
		connection.close();
		return new HashMap();
	}
	/**
	 * ����ϸ��
	 * 
	 * @param mapPam
	 * @return
	 */
	private Map insOrderAccount(Map mapPam) {
		TParm parm = new TParm(mapPam);
//		if (isClientlink())
//			return (Map) callServerMethod(mapPam);
		TParm result = new TParm();
		String caseNo = "";
		String recpType = "";
		// for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
		TParm tempParm = new TParm();
		tempParm.setRowData(-1, parm, 0);
		caseNo = tempParm.getValue("CASE_NO");
		recpType = tempParm.getValue("RECP_TYPE");
		if (tempParm.getValue("RECP_TYPE").equals("REGT")
				|| tempParm.getValue("RECP_TYPE").equals("OPBT")) {
			// �˷Ѳ���
			tempParm.setData("UN_FLG", 1);
			result = INSTJFlow.getInstance()
					.opdOrderAccountComm(tempParm, parm);
		} else {
			// ����
			tempParm.setData("UN_FLG", 0);
			result = INSTJFlow.getInstance()
					.opdOrderAccountComm(tempParm, parm);
		}
		// ��������
		if (result.getErrCode() < 0) {
			return result.getData();
		}
		result.setData("CASE_NO", caseNo);
		result.setData("RECP_TYPE", recpType);
		int insType = parm.getInt("INS_TYPE");
		String pipeline = "";// INS_IO ��������
		String plotType = "";// INS_IO ��������
		TParm opdReturnParm = INSOpdTJTool.getInstance().selectResetFee(tempParm)
				.getRow(0);
		// TParm
		// opdOrderReturnParm=INSOpdOrderTJTool.getInstance().selectResetOpdOrder(parm).getRow(0);
		TParm mzConfirmParm = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				tempParm).getRow(0);
		TParm resultTwoParm = new TParm();// ��������
		switch (insType) {
		case 1:
			resultTwoParm = INSTJFlow.getInstance().getOpdOrderChZPtParm(
					result, opdReturnParm, mzConfirmParm);
			pipeline = "DataDown_rs";
			plotType = "M";
			break;
		case 2:
			resultTwoParm = INSTJFlow.getInstance().getOpdOrderChZMtParm(
					result, opdReturnParm, mzConfirmParm);
			pipeline = "DataDown_mtd";
			plotType = "E";
			break;
		case 3:
			resultTwoParm = INSTJFlow.getInstance().getOpdOrderChJMtParm(
					result, opdReturnParm, mzConfirmParm);
			pipeline = "DataDown_cmtd";
			plotType = "E";
			break;
		}
		result.setData("PIPELINE", pipeline);
		result.setData("PLOT_TYPE", plotType);
		result.setData("IN_OUT", "OUT");
		result.setData("ownParm", resultTwoParm.getData());// ��������
		return result.getData();
	}
	/**
	 * ����ϸ��
	 * 
	 * @param mapPam
	 * @return
	 */
	public TParm insOpdOrderAccount(Map mapPam){
		Map map=insOrderAccount(mapPam);
		return new TParm(map);
	}
	/**
	 * ���صǼǿ��߱������
	 * @param mapPam
	 * @return
	 */
	public Map onCommandButSave(Map mapPam){
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm = new TParm(mapPam);
		return getCommandButSave(parm).getData();
	}
	private TParm getCommandButSave(TParm parm){
		TParm result=new TParm();
		TParm loadDownParm=new TParm();//����������Ϣ��������
		TParm saveRegisterParm=new TParm();//��������
		TParm returnParm=new TParm();//��������
		TParm commParm =new TParm();//���صǼǹ�����Դ
		//���صǼǿ��߷�����Ϣ
		//System.out.println("�鿴�Ƿ�ִ�У���"+parm);
		parm.setData("CROWD_TYPE",parm.getValue("INS_CROWD_TYPE"));//��Ⱥ���
		result=getLockInfo(parm);
		if (result.getErrCode()<0) {//������Ϣʧ�ܷ���
			return result;
		}
		//System.out.println("result:::"+result);
		parm.setData("REGISTER_NO",result.getValue("REGISTER_NO"));//���صǼǱ��
		parm.setData("REGISTER_DATE","");//����ʱ��
	//	result.setData("CROWD_TYPE",parm.getValue("CROWD_TYPE"));
		//==============pangben 2012-4-9 start ҽ������ ��Ӽ���ʱ��
		String message="";
	    Timestamp nowTime = SystemTool.getInstance().getDate();//��ǰʱ��
	    if (null!=result.getValue("LAST_JUDGE_DATE") && result.getValue("LAST_JUDGE_DATE").length()>0) {
	    	Timestamp  oldTime= StringTool.rollDate(result.getTimestamp("LAST_JUDGE_DATE"),180);//�ϴμ���ʱ��+����
			if (nowTime.after(oldTime)) {//����6���¿���ִ��
				
			}else{
				message="�ϴμ���ʱ���Ѿ���δ��������,�뵽��һ�������ģ�һ����ҽԺ�������ٴμ���";
			}
		}
		
		//�ϴμ�������LAST_JUDGE_END:0.���϶�1�����϶�
	    String messageEnd="";
	    if (null!=result.getValue("LAST_JUDGE_END") &&result.getValue("LAST_JUDGE_END").length()>0) {	    	
			if (result.getValue("LAST_JUDGE_END").equals("1")) {
				messageEnd="�ϴμ�������:�����϶�";
			}
		}
		
		//==============pangben 2012-4-9 stop 
		if (result.getInt("REGISTER_SERIAL_NO")==1) {//1 �״� 2 ����3 �����״�
		}else if(result.getInt("REGISTER_SERIAL_NO")==2){//����
			//��ò�����������
			commParm=getCommpatInfo(parm);
			if (commParm.getErrCode()<0) {//�����������ݷ���
				return commParm;
			}
			//ҽԺ�����ϴ�������Ϣ
			parm.setData("BEGIN_DATE",result.getValue("BEGIN_DATE").replace("-", "").substring(0,8));
			loadDownParm=loadDownInfo(parm);//------????������ ����û������״̬��ô���
			if (loadDownParm.getErrCode()<0) {
				return loadDownParm;
			}
			loadDownParm.setData("MR_NO",parm.getValue("MR_NO"));
			loadDownParm.setData("REGION_CODE",parm.getValue("REGION_CODE"));
			loadDownParm.setData("CASE_NO",parm.getValue("CASE_NO"));
			loadDownParm.setData("NHI_REGION_CODE",parm.getValue("NHI_REGION_CODE"));//�������ҽԺ����
			loadDownParm.setData("PAY_KIND",parm.getValue("PAY_KIND"));//֧�����
			loadDownParm.setData("OPT_USER",parm.getValue("OPT_USER"));//������
			loadDownParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));//����ip	
			loadDownParm.setData("INS_CROWD_TYPE",parm.getValue("INS_CROWD_TYPE"));//��Ⱥ���
			//�绰
			loadDownParm.setData("PAT_PHONE",parm.getValue("PAT_PHONE"));
			loadDownParm.setData("PAT_ZIP_CODE",parm.getValue("PAT_ZIP_CODE"));
			loadDownParm.setData("PAT_ADDRESS",parm.getValue("PAT_ADDRESS"));
			loadDownParm.setData("OWN_NO",parm.getValue("OWN_NO"));
			
			//�������صǼ���Ϣ
			//getParm(parm, loadDownParm);
			returnParm.setData("commParm",commParm.getData());//��ò�����������
			returnParm.setData("MESSAGE","����������Ϣ");
			loadDownParm.setData("FLG","Y");
			parm=loadDownParm;
		}
		parm.setData("REGISTER_CENTER_USER",null==parm.getValue("REGISTER_CENTER_USER")?"":parm.getValue("REGISTER_CENTER_USER"));
		parm.setData("MED_HELP_COMPANY",null==parm.getValue("MED_HELP_COMPANY")?"":parm.getValue("MED_HELP_COMPANY"));		
		saveRegisterParm=saveRegister(parm);
		if (saveRegisterParm.getErrCode()<0) {
			return saveRegisterParm;
		}
		//��������
		parm.setData("STATUS_TYPE",0);//δ���״̬
		//System.out.println("ִ�в���������"+parm);
		//parm.setData("OWN_NO",loadDownParm.getValue("PERSONAL_NO"));//���˱���
		parm.setData("DR_CODE1",parm.getValue("OPT_USER"));//��ҽ��1
		parm.setData("DR_CODE2",parm.getValue("OPT_USER"));//ҽ��2
		parm.setData("AUDIT_CENTER_USER","");//
		parm.setData("UNPASS_REASON","");//��ͨ��ԭ��
		//===============pangben 2012-4-10 ҽ�����������
	    parm.setData("NUMBER_PAY_AMT",saveRegisterParm.getDouble("NUMBER_PAY_AMT"));//��ͷ����֧�����(����)
	    parm.setData("JUDGE_SEQ",saveRegisterParm.getValue("JUDGE_SEQ"));//����˳���(����)
	    parm.setData("LAST_JUDGE_END",result.getValue("LAST_JUDGE_END"));//
	    parm.setData("BEGIN_DATE",SystemTool.getInstance().getDateReplace(saveRegisterParm.getValue("BEGIN_DATE"), true));
	    parm.setData("END_DATE",SystemTool.getInstance().getDateReplace(saveRegisterParm.getValue("END_DATE"),true));
	    //===============pangben 2012-4-10 stop
		result=INSMTRegisterTool.getInstance().saveINSMTRegister(parm);
		if (result.getErrCode()<0) {	
			return result;
		}
		returnParm.setData("MTRegisterParm",result.getData());
		returnParm.setData("message",message);
		returnParm.setData("messageEnd",messageEnd);
		return returnParm;
	}
	/**
	 * У���Ƿ�û������
	 * @param parm
	 * @param commParm
	 */
	private void getParm(TParm parm,TParm commParm){
//		//�绰
		parm.setData("PAT_PHONE",getNameValue(parm.getValue("PAT_PHONE"),commParm.getValue("PAT_PHONE")));
		//ѡ��ҽ������ҽԺ(һ��)
		parm.setData("HOSP_CODE_LEVEL1",commParm.getValue("HOSP_CODE_LEVEL1"));
		//ѡ��ҽ������ҽԺ(����)
		parm.setData("HOSP_CODE_LEVEL2",commParm.getValue("HOSP_CODE_LEVEL2"));
		//ѡ��ҽ������ҽԺ(����)
		parm.setData("HOSP_CODE_LEVEL3",commParm.getValue("HOSP_CODE_LEVEL3"));
		//ѡ��ҽ������ҽԺ(����ר?
		parm.setData("HOSP_CODE_LEVEL3_PRO",commParm.getValue("HOSP_CODE_LEVEL3_PRO"));
		//ѡ��ҽ������ҽԺ(������
		parm.setData("DRUGSTORE_CODE",commParm.getValue("DRUGSTORE_CODE"));
		//�������
		parm.setData("ASSISTANT_EXAMINE",commParm.getValue("ASSISTANT_EXAMINE"));
		//�������ҽԺ����
		parm.setData("DIAG_HOSP_CODE",commParm.getValue("DIAG_HOSP_CODE"));
		//���صǼǱ���
		parm.setData("REGISTER_NO",commParm.getValue("MT_REGISTER_CODE"));	
		
		parm.setData("REGISTER_SERIAL_NO",commParm.getValue("REGISTER_SERIAL_NO"));//	���صǼ����		
		
		parm.setData("DISEASE_CODE",commParm.getValue("DISEASE_CODE"));//	���ز��ֱ���		
		parm.setData("PERSONAL_NO",commParm.getValue("PERSONAL_NO"));//���˱���		
		parm.setData("PAT_NAME",commParm.getValue("PAT_NAME"));//����	
		parm.setData("SEX_CODE",commParm.getValue("SEX_CODE"));	//�Ա�		1��2Ů		
		parm.setData("REGISTER_TYPE",commParm.getValue("REGISTER_TYPE"));	//���صǼ���� 1 �״� 2 ����	3 �����״�	
		parm.setData("DISEASE_HISTORY",commParm.getValue("DISEASE_HISTORY"));	//��ʷ
		parm.setData("DIAG_CODE",commParm.getValue("DIAG_CODE"));	//�ٴ����		
		parm.setData("DISEASE_HISTORY",commParm.getValue("DISEASE_HISTORY"));	//��ʷ
		parm.setData("BEGIN_DATE",SystemTool.getInstance().getDateReplace(commParm.getValue("BEGIN_DATE"), true));	//���صǼǿ�ʼʱ��	YYYY-MM-DD HH24:MI:SS		
		parm.setData("END_DATE",SystemTool.getInstance().getDateReplace(commParm.getValue("END_DATE"), true));	//���صǼǽ���ʱ��		YYYY-MM-DD HH24:MI:SS		
		parm.setData("MED_HISTORY",commParm.getValue("MED_HISTORY"));	//����ʷ(����)			
		parm.setData("ASSSISTANT_STUFF",commParm.getValue("ASSSISTANT_STUFF"));	//��������(����)			
		parm.setData("JUDGE_CONTER_I",commParm.getValue("JUDGE_CONTER_I"));//	�����������(����)
		parm.setData("JUDGE_END",commParm.getValue("JUDGE_END"));//	��������(����)
		
	}
	private String getNameValue(String name,String newName){
		if (null==name || name.length()<=0) {
			return null==newName || newName.length()<=0?"":newName;
		}
		return name;
	}
	/**
	 * ���صǼǿ��߷�����Ϣ
	 * @param parm
	 * @return
	 */
	private TParm getLockInfo(TParm parm){
		TParm result=new TParm();
		// ��Ⱥ���:1.��ְ 2.�Ǿ�
		if (parm.getInt("CROWD_TYPE")==1) {//��ְ
			result=INSTJTool.getInstance().DataDown_mts_A(parm);
		}else if (parm.getInt("CROWD_TYPE")==2) {//�Ǿ�
			result=INSTJTool.getInstance().DataDown_cmts_A(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			return result;
		}
		return result;
	}
	/**
	 * �������صǼ���Ϣ
	 * @return
	 */
	private TParm saveRegister(TParm parm){
		TParm result=new TParm();
		if (parm.getInt("CROWD_TYPE")==1) {//��ְ	
			result=INSTJTool.getInstance().DataDown_mts_B(parm);
		}else if (parm.getInt("CROWD_TYPE")==2) {//�Ǿ�
			result=INSTJTool.getInstance().DataDown_cmts_B(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			return result;
		}
		return result;
	}
	/**
	 * ��ò�����������
	 * @param parm
	 * @return
	 */
	private TParm getCommpatInfo(TParm parm){
		TParm result=new TParm();
		if (parm.getInt("CROWD_TYPE")==1) {//��ְ	
		result=INSTJTool.getInstance().DataDown_mtd_H(parm);
		}
		else if (parm.getInt("CROWD_TYPE")==2) {//�Ǿ�
			result=INSTJTool.getInstance().DataDown_cmtd_H(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			return result;
		}
		return result;
	}
	/**
	 * ������Ϣ
	 */
	public Map onShare(Map mapPam){
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		return getCommpatInfo(parm).getData();
	}
	/**
	 * ҽԺ�����ϴ�������Ϣ 
	 * @param parm
	 * @return
	 */
	private TParm loadDownInfo(TParm parm){
		TParm loadDownParm=new TParm();
		if (parm.getInt("CROWD_TYPE")==1) {//��ְ		
			loadDownParm=INSTJTool.getInstance().DataDown_mts_C(parm);
		}else if (parm.getInt("CROWD_TYPE")==2) {//�Ǿ�
			loadDownParm=INSTJTool.getInstance().DataDown_cmts_C(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(loadDownParm)) {
			return loadDownParm;
		}
		return loadDownParm;
	}
	/**
	 * ���ز���
	 * @param mapPam
	 * @return
	 */
	public Map onLoadDown(Map mapPam){
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm result=new TParm();
		//ҽԺ�����ϴ�������Ϣ
		result=loadDownInfo(parm);
		if (result.getErrCode()<0) {
		
			return result.getData();
		}
		result.setData("PAY_KIND",parm.getValue("PAY_KIND"));//֧�����
		result.setData("REGION_CODE",parm.getValue("REGION_CODE"));//����
		result.setData("CASE_NO",parm.getValue("CASE_NO"));
		result.setData("MR_NO",parm.getValue("MR_NO"));
		result.setData("REGION_CODE",parm.getValue("REGION_CODE"));
		result.setData("OWN_NO",result.getValue("PERSONAL_NO"));//���˱���
		result.setData("OPT_USER",parm.getValue("OPT_USER"));//ID
		result.setData("OPT_TERM",parm.getValue("OPT_TERM"));//IP
		result.setData("STATUS_TYPE",parm.getValue("STATUS_TYPE"));//�������״̬
		result.setData("NHI_REGION_CODE",parm.getValue("NHI_REGION_CODE"));
		result.setData("UNPASS_REASON",parm.getValue("UNPASS_REASON"));
		result.setData("DR_CODE1",parm.getValue("OPT_USER"));
		result.setData("DR_CODE2",parm.getValue("OPT_USER"));
		result.setData("LAST_JUDGE_END",result.getValue("JUDGE_END"));
		result.setData("AUDIT_CENTER_USER",result.getValue("REGISTER_CENTER_USER"));
		result.setData("INS_CROWD_TYPE","2");
		//��������
		result=INSMTRegisterTool.getInstance().saveINSMTRegister(result);
		if (result.getErrCode()<0) {	
			return result.getData();
		}
		return result.getData();
	} 
	public Map readINSCard(Map mapPam) {
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm=new TParm(mapPam);
		TParm result=INSTJFlow.getInstance().readINSCard(parm);

		return result.getData();
	}
	/**
	 * INSFEE.X������ʾ
	 * @param parm
	 * @param control
	 * @return
	 */
	public TParm onInsFee(TParm parm,TControl control){
		TParm r = (TParm) control.openDialog(
				"%ROOT%\\config\\ins\\INSFee.x", parm);
		return r;
	}
}
