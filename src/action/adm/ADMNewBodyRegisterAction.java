package action.adm;

import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMNewBodyRegisterTool;
import jdo.sys.SYSBedTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title:������ע��Action
 * </p>
 *
 * <p>
 * Description:������ע��Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: Javahis
 * </p>
 *
 * @author duzhw
 * @version 4.5
 */
public class ADMNewBodyRegisterAction extends TAction {

	/**
	 * ��Ժ�ǼǱ��淽�������ã�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertADMData(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		result = ADMInpTool.getInstance().insertdata(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ������ע�ᱣ������-�����ã�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		/*------------------------ ռ��ע�ǣ��鿴Ӥ�����Ƿ�ռ��------------------------*/
		result = ADMNewBodyRegisterTool.getInstance().checkBedAlloFlg(parm);
		if (result.getCount() > 0) {
			if ("Y".equals(result.getValue("ALLO_FLG", 0))) {
				err("ERR:�ô��ѱ�ռ�ã�");
				conn.rollback();
				conn.close();
				return result;
			}
		}

		/*------------------------ ע�������� sys_patinfo����------------------------*/
		result = ADMNewBodyRegisterTool.getInstance().insertSysPatInfo(parm,
				conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		/*------------------------ סԺ�ǼǱ���------------------------*/
		result = ADMInpTool.getInstance().insertADMData(parm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		/*------------------------ ���´�λ��------------------------*/
		TParm sysBed = new TParm();
		sysBed.setRowData(parm);
		// System.out.println("admInp------------------w13----------------:"+result);
		// ���´�λ��
		result = SYSBedTool.getInstance().upDate(sysBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		/*------------------------ ����adm_inpdiag������(����Ҫ�˲���)------------------------
		//��ѯĸ��סԺ���
		TParm queryAdmDiagParm = ADMNewBodyRegisterTool.getInstance().queryAdmDiag(parm);
		System.out.println("��ѯĸ��סԺ���-"+queryAdmDiagParm);
		//ȡ��һ�����
		TParm admDiagParm = queryAdmDiagParm.getRow(0);
		System.out.println("ȡ��һ�����-"+admDiagParm);
		admDiagParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		admDiagParm.setData("MR_NO", parm.getValue("MR_NO"));
		admDiagParm.setData("IPD_NO", parm.getValue("IPD_NO"));
		admDiagParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		admDiagParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		System.out.println("admDiagParm-"+admDiagParm);
		//����סԺ���
		result = ADMNewBodyRegisterTool.getInstance().insertAdmDiag(admDiagParm,conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		} */

		/*------------------------ ����mro_record������------------------------*/
		// ��ѯĸ�ײ�����Ϣ
		TParm queryMroRecrdParm = ADMNewBodyRegisterTool.getInstance()
				.queryMroRecrd(parm);
		// System.out.println("��ѯĸ�ײ�����Ϣ-"+queryMroRecrdParm);
		TParm mroRecrdParm = queryMroRecrdParm.getRow(0);
		mroRecrdParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		mroRecrdParm.setData("MR_NO", parm.getValue("MR_NO"));
		mroRecrdParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
		mroRecrdParm.setData("SEX", parm.getValue("SEX_CODE"));
		mroRecrdParm.setData("BIRTH_DATE", parm.getValue("BIRTH_DATE"));
		mroRecrdParm.setData("AGE", "1");
		mroRecrdParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		mroRecrdParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		// System.out.println("mroRecrdParm-"+mroRecrdParm);
		// ���벡����Ϣ
		result = ADMNewBodyRegisterTool.getInstance().insertMroRecrd(
				mroRecrdParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		/*------------------------ ����mro_record_diag������------------------------
		//��ѯĸ�ײ��������Ϣ
		TParm queryMroRecordDiagParm = ADMNewBodyRegisterTool.getInstance().queryMroRecordDiag(parm);
		System.out.println("��ѯĸ�ײ��������Ϣ-"+queryMroRecordDiagParm);
		TParm mroRecordDiagParm = queryMroRecordDiagParm.getRow(0);
		mroRecordDiagParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		mroRecordDiagParm.setData("MR_NO", parm.getValue("MR_NO"));
		mroRecordDiagParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		mroRecordDiagParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		System.out.println("mroRecordDiagParm-"+mroRecordDiagParm);
		
		//���벡�������Ϣ
		result = ADMNewBodyRegisterTool.getInstance().insertMroRecordDiag(mroRecordDiagParm,conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}  */

		/*------------------------ ����adm_trans_log������------------------------*/
		// ��ѯĸ��ת�Ƽ�¼
		// TParm queryTransLogParm =
		// ADMNewBodyRegisterTool.getInstance().queryTransLog(parm);
		// TParm transLogParm = queryTransLogParm.getRow(0);
		TParm transLogParm = new TParm();
		transLogParm = parm.getRow(0);

		transLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		transLogParm.setData("MR_NO", parm.getValue("MR_NO"));
		transLogParm.setData("IPD_NO", parm.getValue("IPD_NO"));
		transLogParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		transLogParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		transLogParm.setData("OUT_DATE", "");
		transLogParm.setData("OUT_DEPT_CODE", "");
		transLogParm.setData("OUT_STATION_CODE", "");
		transLogParm.setData("IN_DEPT_CODE", parm.getValue("DEPT_CODE"));
		transLogParm.setData("IN_STATION_CODE", parm.getValue("STATION_CODE"));
		transLogParm.setData("PSF_KIND", "");
		transLogParm.setData("DEPT_ATTR_FLG", "");

		// ����ת����־
		result = ADMNewBodyRegisterTool.getInstance().insertTransLog(
				transLogParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		/*------------------------ ����adm_chg��̬��¼������------------------------*/
		// ��ѯĸ�׶�̬��¼
		// TParm queryChgParm =
		// ADMNewBodyRegisterTool.getInstance().queryChg(parm);
		// TParm chgParm = queryChgParm.getRow(0);
		TParm chgParm = new TParm();
		chgParm = parm.getRow(0);
		int seq = getMaxSeq("SEQ_NO", "ADM_CHG", "CASE_NO",
				parm.getValue("CASE_NO"), "", "");

		chgParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		chgParm.setData("MR_NO", parm.getValue("MR_NO"));
		chgParm.setData("IPD_NO", parm.getValue("IPD_NO"));
		chgParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		chgParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		chgParm.setData("CANCEL_FLG", "N");
		chgParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
		chgParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
		chgParm.setData("BED_NO", parm.getValue("BED_NO"));
		chgParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		chgParm.setData("PSF_HOSP", "");
		chgParm.setData("CANCEL_DATE", "");
		chgParm.setData("CANCEL_USER", "");
		chgParm.setData("VS_CODE_CODE", "");
		chgParm.setData("ATTEND_DR_CODE", "");
		chgParm.setData("DIRECTOR_DR_CODE", "");

		chgParm.setData("SEQ_NO", seq + 1);
		chgParm.setData("PSF_KIND", "INDP");// ���
		result = ADMNewBodyRegisterTool.getInstance().insertChg(chgParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		chgParm.setData("SEQ_NO", seq + 2);
		chgParm.setData("PSF_KIND", "INBD");// �봲
		result = ADMNewBodyRegisterTool.getInstance().insertChg(chgParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ����-��������Ϣ�޸�
	 */
	public TParm onUpdateSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();

		result = ADMNewBodyRegisterTool.getInstance().updateSysPatInfo(parm,
				conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}

		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * �õ����ı�� +1
	 *
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(String maxValue, String tableName, String where1,
			String value1, String where2, String value2) {
		String sql = "SELECT MAX(" + maxValue + ") AS " + maxValue + " FROM "
				+ tableName + " WHERE 1=1 ";
		if (where1.trim().length() > 0) {
			sql += " AND " + where1 + " ='" + value1 + "'";
		}
		if (where2.trim().length() > 0) {
			sql += " AND " + where2 + " ='" + value2 + "'";
		}
		// System.out.println("���ı��sql="+sql);
		// ��������
		int max = 0;
		// ��ѯ������
		TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("seqParm="+seqParm);
		String seq = seqParm.getValue(maxValue, 0).toString().equals("") ? "0"
				: seqParm.getValue(maxValue, 0).toString();
		int value = Integer.parseInt(seq);
		// �������ֵ
		if (max < value) {
			max = value;
		}
		// ���ż�1
		max++;
		// System.out.println("�����ı�� +1="+max);
		return max;

	}

	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onNewCancel(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		try {
			// �������û�а�������סǰ�Ѵ˲���������λ��ԤԼ���ǰ���崲��סԺ�����Ŵ�λ��
			TParm clear = new TParm();
			clear.setData("CASE_NO", parm.getValue("CASE_NO"));
			result = SYSBedTool.getInstance().clearForadmin(clear, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			List<String> list = new ArrayList<String>();
			String patInfosql = "DELETE FROM SYS_PATINFO WHERE MR_NO='"
					+ parm.getValue("MR_NO") + "'";
			list.add(patInfosql);
			String admInpsql = "DELETE FROM ADM_INP WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "'";
			list.add(admInpsql);
			String admTranslogsql = "DELETE FROM ADM_TRANS_LOG WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "'";
			list.add(admTranslogsql);
			String mroRecordsql = "DELETE FROM MRO_RECORD WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "'";
			list.add(mroRecordsql);
			String mroRecordDiagsql = "DELETE FROM MRO_RECORD_DIAG WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "'";
			list.add(mroRecordDiagsql);
			int size = list.size();
			String[] array = (String[]) list.toArray(new String[size]);
			result = new TParm(TJDODBTool.getInstance().update(array, conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			conn.commit();
			conn.close();
			return result;
		} catch (Exception e) {
			// TODO: handle exception
			conn.rollback();
			conn.close();
			result.setErr(-1, "ȡ��ע���쳣");
			return result;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}
	/**
	 * ������������Ժ ���ɲ�����
	 */
	public TParm newPatInfo(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		TParm param=parm.getParm("NEWPARM");
		result = ADMNewBodyRegisterTool.getInstance().newPatInfo(param,
				conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		param=parm.getParm("OLDPARM");
		result = ADMNewBodyRegisterTool.getInstance().updatePatInfo(param, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
}
