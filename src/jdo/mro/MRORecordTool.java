package jdo.mro;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.adm.ADMTool;
import jdo.adm.ADMTransLogTool;
import jdo.ibs.IBSOrderdTool;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;

/**
 * <p>
 * Title: ������ҳ
 * </p>
 * 
 * <p>
 * Description: ������ҳ
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
 * @author ZhangK 2009-3-24
 * @version 4.0
 */
public class MRORecordTool extends TJDOTool {
	public MRORecordTool() {
		setModuleName("mro\\MRORecordModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	public static MRORecordTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return SYSRegionTool
	 */
	public static MRORecordTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MRORecordTool();
		return instanceObject;
	}

	/**
	 * ����Ժ���������Ż�ȡ����סԺ�Ĵ�����ÿ����Ժ������
	 * 
	 * @param parm
	 *            TParm ������ʽ��{REGION_CODE:REGION_CODEֵ;MR_NO:MR_NOֵ}
	 * @return TParm
	 */
	public TParm getCountOfInHosp(TParm parm) {
		TParm result = this.query("selectCountOfInHosp", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���ݲ�����MR_NO��סԺ��CASE_NO��ѯ����ĳһ��סԺ��Ϣ
	 * 
	 * @param parm
	 *            TParm ������ʽ��{CASE_NO:CASE_NOֵ}
	 * @return TParm
	 */
	public TParm getInHospInfo(TParm parm) {
		TParm result = this.query("selectInHospInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ȡĳһ��������ҳ��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMRO_RecordInfo(TParm parm) {
		TParm result = new TParm();
		// ��ȡmro_record����Ϣ
		TParm record = this.getInHospInfo(parm);
		if (record.getErrCode() < 0) {
			err("ERR:" + record.getErrCode() + record.getErrText()
					+ record.getErrName());
			return record;
		}
		TParm adm_tran = ADMTransLogTool.getInstance().getTranHospFormro(parm);
		if (adm_tran.getErrCode() < 0) {
			err("ERR:" + adm_tran.getErrCode() + adm_tran.getErrText()
					+ adm_tran.getErrName());
			return adm_tran;
		}
		result.setData("RECORD", record.getData());
		result.setData("ADMTRAN", adm_tran.getData());
		return result;
	}

	/**
	 * ��ȡ��������շѵ���������
	 * 
	 * @return TParm
	 */
	public Map getChargeName() {
		TParm result = this.query("selectChargeName");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return null;
		}
		Map map = new HashMap();
		for (int i = 0; i < result.getCount(); i++) {
			map
					.put(result.getData("ID", i) + "", result.getData(
							"CHN_DESC", i));
		}
		return map;
	}

	/**
	 * �����һҳǩ������(סԺ�Ǽ�)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm saveFirst(TParm parm, TConnection conn) {
		TParm result = this.update("updateFirstPage", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ������Ϣ�޸ı���ͬ�����²�����ҳ
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm savePatToMro(TParm parm, TConnection conn) {
		TParm result = this.update("updatePatToMro", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

	/**
	 * ����ڶ�ҳǩ������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm saveSecend(TParm parm, TConnection conn) {
		TParm result = this.update("updateSecendPage", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �������ҳǩ������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm saveFour(TParm parm, TConnection conn) {
		TParm result = this.update("updateFourPage", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	//add by yangjj 20150701 ����������
	public TParm saveChildBirth(TParm parm , TConnection conn){
		TParm result = this.update("insertChildBirth" , parm , conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	//add by yangjj 20150701 ����������
	public TParm selectChildBirth(TParm parm , TConnection conn){
		TParm result = this.update("selectChildBirth" , parm , conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TDataStore
	 */
	public TDataStore getOPInfo(TParm parm) {
		TDataStore a = new TDataStore();
		a.setItem(1, 1, 1, "1");
		TParm b = new TParm();
		return a;
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOP(TParm parm, TConnection conn) {
		TParm result = this.update("insertOP", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ��������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteOP(TParm parm, TConnection conn) {
		TParm result = this.update("deleteOP", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����������Ϣ
	 * 
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm saveOP(TParm parm, TConnection conn) {
		TParm delParm = new TParm();
		delParm.setData("CASE_NO", parm.getValue("CASE_NO",0));
		TParm result = this.update("deleteOP", delParm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
			result = this.update("insertOP", parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ִ�� ������ DataStore���ɵ�SQL���
	 * 
	 * @param sql
	 *            String[]
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm executeOPSql(String[] sql, TConnection conn) {
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸���ҳ����������Ϣ��סԺ����ӿڣ�
	 * 
	 * @param parm
	 *            TParm �������룺MR_NO��CASE_NO��OPT_USER��OPT_TERM
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateMROPatInfo(TParm p) {
		TParm parm = new TParm();
		TParm result = new TParm();
		Pat pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		// Patinfoȡ�ò���
		parm.setData("CASE_NO", p.getValue("CASE_NO"));
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("IDNO", pat.getIdNo());
		parm.setData("SEX", pat.getSexCode());
		parm.setData("BIRTH_DATE", StringTool.getString(pat.getBirthday(),
				"yyyyMMdd"));
		parm.setData("MARRIGE", pat.getMarriageCode());
		parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(), p
				.getTimestamp("IN_DATE")));// ��������
		// �������պ���Ժʱ�����
		parm.setData("NATION", pat.getNationCode());
		parm.setData("FOLK", pat.getSpeciesCode());
		parm.setData("CTZ1_CODE", pat.getCtz1Code());
		parm.setData("H_TEL", "");// ���ڵ绰 �õ� ��ͥ�绰
		parm.setData("H_ADDRESS", pat.getResidAddress());// ������ַ
		parm.setData("H_POSTNO", pat.getResidPostCode());// �����ʱ�
		parm.setData("OCCUPATION", pat.getOccCode());// ְҵ
		parm.setData("OFFICE", pat.getCompanyDesc());// ��λ
		parm.setData("O_TEL", pat.getTelCompany());// ��λ�绰
		parm.setData("O_ADDRESS", pat.getCompanyAddress());// ��λ��ַ
		parm.setData("O_POSTNO", pat.getCompanyPost());// ��λ�绰
		parm.setData("CONTACTER", pat.getContactsName());// ��ϵ��
		parm.setData("RELATIONSHIP", pat.getRelationCode());// ��ϵ�˹�ϵ
		parm.setData("CONT_TEL", pat.getContactsTel());// ��ϵ�˵绰
		parm.setData("CONT_ADDRESS", pat.getContactsAddress());// ��ϵ�˵�ַ
		parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());// �����ش���
		// ----------------shibl modify 20120512
		parm.setData("BLOOD_TYPE", getBloodTypeTran(pat.getBloodType()));// Ѫ��
		parm.setData("RH_TYPE", getBloodRHType(pat.getBloodRHType()));// RH����
		// add
		String mroCtz = "";
		TParm ctzParm = CTZTool.getInstance().getMroCtz(pat.getCtz1Code());
		if (ctzParm.getCount() > 0) {
			mroCtz = ctzParm.getValue("MRO_CTZ", 0);
		}
		parm.setData("MRO_CTZ", mroCtz);// ������ҳ���
		parm.setData("BIRTHPLACE", pat.getBirthPlace());// ����
		parm.setData("ADDRESS", pat.getAddress());// ͨ�ŵ�ַ
		parm.setData("POST_NO", pat.getPostCode());// ͨ���ʱ�
		parm.setData("TEL", pat.getTelHome());// �绰
		parm.setData("NHI_NO", pat.getNhiNo()); // ҽ������
		parm.setData("NHICARD_NO", pat.getNhicardNo()); // ��������
		parm.setData("OPT_USER", p.getValue("OPT_USER"));
		parm.setData("OPT_TERM", p.getValue("OPT_TERM"));
		result = this.update("updateMROPatInfo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 1.A 2.B 3.O 4.AB 5.���� 6.δ��
	 * 
	 * @param type
	 * @return
	 */
	private String getBloodTypeTran(String type) {
		String tranType = "";
		if (type.equalsIgnoreCase("A")) {
			tranType = "1";
		} else if (type.equalsIgnoreCase("B")) {
			tranType = "2";
		} else if (type.equalsIgnoreCase("O")) {
			tranType = "3";
		} else if (type.equalsIgnoreCase("AB")) {
			tranType = "4";
		} else if (type.equalsIgnoreCase("T")) {
			tranType = "5";
		} else {
			tranType = "6";
		}
		return tranType;
	}

	/**
	 * 1.�� 2.�� 3.���� 4.δ��
	 * 
	 * @param type
	 * @return
	 */
	private String getBloodRHType(String type) {
		String tranType = "";
		if (type.equals("+")) {
			tranType = "1";
		} else if (type.equals("-")) {
			tranType = "2";
		} else {
			tranType = "4";
		}
		return tranType;
	}

	/**
	 * ������ҳ��Ϣ��סԺ�Ǽǽӿڣ�
	 * 
	 * @param parm
	 *            TParm ������CASE_NO��MR_NO��OPT_USER,OPT_TERM;HOSP_ID
	 * @return TParm
	 */
	public TParm insertMRO(TParm parm) {
		TParm result = this.update("creatMRO", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�סԺ��Ϣ��סԺ�Ǽǽӿڣ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateADMData(TParm parm) {
		TParm result = this.update("updateADMData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * �޸Ĳ������
	 * 
	 * @param parm
	 *            TParm ���������CASE_NO ����˵���� OE_DIAG_CODE:�ż������CODE
	 *            OE_DIAG_CODE2:�ż������2CODE OE_DIAG_CODE3:�ż������3CODE
	 *            IN_DIAG_CODE����Ժ���CODE IN_DIAG_CODE2����Ժ���2CODE
	 *            IN_DIAG_CODE3����Ժ���3CODE OUT_DIAG_CODE1����Ժ�����CODE
	 *            CODE1_REMARK����Ժ����ϱ�ע CODE1_STATUS����Ժ�����ת��״̬
	 *            OUT_DIAG_CODE2����Ժ���CODE CODE2_REMARK����Ժ��ϱ�ע
	 *            CODE2_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE3����Ժ���CODE
	 *            CODE3_REMARK����Ժ��ϱ�ע CODE3_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE4����Ժ���CODE CODE4_REMARK����Ժ��ϱ�ע
	 *            CODE4_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE5����Ժ���CODE
	 *            CODE5_REMARK����Ժ��ϱ�ע CODE5_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE6����Ժ���CODE CODE6_REMARK����Ժ��ϱ�ע
	 *            CODE6_STATUS����Ժ���ת��״̬ INTE_DIAG_CODE��Ժ�ڸ�Ⱦ���CODE
	 *            CASE_NO������case_no
	 * @return TParm
	 */
	public TParm updateMRODiag(TParm parm, TConnection conn) {
		TParm result = new TParm();
		TParm insertParm = new TParm();
		TParm updateParm = new TParm();
		String new_seq_no = "";
		new_seq_no = getMaxSeqNo(parm.getValue("CASE_NO", 0));
		int seqCount = Integer.parseInt(new_seq_no);
		// ������� ���ô˷�����ʱ�����CASE_NO������д �����ֶο��Բ���д
		// TParm p = new TParm();
		// p.setData("OE_DIAG_CODE", parm.getValue("OE_DIAG_CODE"));
		// p.setData("OE_DIAG_CODE2", parm.getValue("OE_DIAG_CODE2"));
		// p.setData("OE_DIAG_CODE3", parm.getValue("OE_DIAG_CODE3"));
		// p.setData("IN_DIAG_CODE", parm.getValue("IN_DIAG_CODE"));
		// p.setData("IN_DIAG_CODE2", parm.getValue("IN_DIAG_CODE2"));
		// p.setData("IN_DIAG_CODE3", parm.getValue("IN_DIAG_CODE3"));
		// p.setData("OUT_DIAG_CODE1", parm.getValue("OUT_DIAG_CODE1"));
		// p.setData("CODE1_REMARK", parm.getValue("CODE1_REMARK"));
		// p.setData("CODE1_STATUS", parm.getValue("CODE1_STATUS"));
		// p.setData("OUT_DIAG_CODE2", parm.getValue("OUT_DIAG_CODE2"));
		// p.setData("CODE2_REMARK", parm.getValue("CODE2_REMARK"));
		// p.setData("CODE2_STATUS", parm.getValue("CODE2_STATUS"));
		// p.setData("OUT_DIAG_CODE3", parm.getValue("OUT_DIAG_CODE3"));
		// p.setData("CODE3_REMARK", parm.getValue("CODE3_REMARK"));
		// p.setData("CODE3_STATUS", parm.getValue("CODE3_STATUS"));
		// p.setData("OUT_DIAG_CODE4", parm.getValue("OUT_DIAG_CODE4"));
		// p.setData("CODE4_REMARK", parm.getValue("CODE4_REMARK"));
		// p.setData("CODE4_STATUS", parm.getValue("CODE4_STATUS"));
		// p.setData("OUT_DIAG_CODE5", parm.getValue("OUT_DIAG_CODE5"));
		// p.setData("CODE5_REMARK", parm.getValue("CODE5_REMARK"));
		// p.setData("CODE5_STATUS", parm.getValue("CODE5_STATUS"));
		// p.setData("OUT_DIAG_CODE6", parm.getValue("OUT_DIAG_CODE6"));
		// p.setData("CODE6_REMARK", parm.getValue("CODE6_REMARK"));
		// p.setData("CODE6_STATUS", parm.getValue("CODE6_STATUS"));
		// p.setData("INTE_DIAG_CODE", parm.getValue("INTE_DIAG_CODE"));
		// p.setData("INTE_DIAG_STATUS", parm.getValue("INTE_DIAG_STATUS"));
		// p.setData("ADDITIONAL_CODE1", parm.getValue("ADDITIONAL_CODE1"));
		// p.setData("ADDITIONAL_CODE2", parm.getValue("ADDITIONAL_CODE2"));
		// p.setData("ADDITIONAL_CODE3", parm.getValue("ADDITIONAL_CODE3"));
		// p.setData("ADDITIONAL_CODE4", parm.getValue("ADDITIONAL_CODE4"));
		// p.setData("ADDITIONAL_CODE5", parm.getValue("ADDITIONAL_CODE5"));
		// p.setData("ADDITIONAL_CODE6", parm.getValue("ADDITIONAL_CODE6"));
		// //add
		// p.setData("INTE_DIAG_CONDITION",
		// parm.getValue("INTE_DIAG_CONDITION"));
		// p.setData("COMPLICATION_DIAG", parm.getValue("COMPLICATION_DIAG"));
		// p.setData("COMPLICATION_STATUS",
		// parm.getValue("COMPLICATION_STATUS"));
		// p.setData("COMPLICATION_DIAG_CONDITION",
		// parm.getValue("COMPLICATION_DIAG_CONDITION"));
		// p.setData("OUT_DIAG_CONDITION1",
		// parm.getValue("OUT_DIAG_CONDITION1"));
		// p.setData("OUT_DIAG_CONDITION2",
		// parm.getValue("OUT_DIAG_CONDITION2"));
		// p.setData("OUT_DIAG_CONDITION3",
		// parm.getValue("OUT_DIAG_CONDITION3"));
		// p.setData("OUT_DIAG_CONDITION4",
		// parm.getValue("OUT_DIAG_CONDITION4"));
		// p.setData("OUT_DIAG_CONDITION5",
		// parm.getValue("OUT_DIAG_CONDITION5"));
		// p.setData("OUT_DIAG_CONDITION6",
		// parm.getValue("OUT_DIAG_CONDITION6"));
		// p.setData("CASE_NO", parm.getValue("CASE_NO"));
		// result = this.update("updateMRODiag", p, conn);
		// if (result.getErrCode() < 0) {
		// err("ERR:" + result.getErrCode() + result.getErrText()
		// + result.getErrName());
		// return result;
		// }
		// if (!parm.getValue("IN_DIAG_CODE").equals("")) {
		// result = this.updateCONFIRM_DATE(parm.getValue("CASE_NO"), conn);
		// if (result.getErrCode() < 0) {
		// err("ERR:" + result.getErrCode() + result.getErrText()
		// + result.getErrName());
		// return result;
		// }
		// }
//		TParm delParm = new TParm(this.getDBTool().update(
//				"DELETE FROM MRO_RECORD_DIAG WHERE CASE_NO='"
//						+ parm.getValue("CASE_NO", 0) + "'"));
//		if (delParm.getErrCode() < 0) {
//			err("ERR:" + delParm.getErrCode() + delParm.getErrText()
//					+ delParm.getErrName());
//			return delParm;
//		}
		TParm inParm = new TParm();
		inParm.setData("OE_DIAG_CODE", "");
		inParm.setData("IN_DIAG_CODE", "");
		inParm.setData("OUT_DIAG_CODE1", "");
		inParm.setData("CODE1_STATUS", "");
		inParm.setData("OUT_DIAG_CONDITION1", "");
		inParm.setData("INTE_DIAG_CODE", "");
		inParm.setData("INTE_DIAG_STATUS", "");
		inParm.setData("INTE_DIAG_CONDITION", "");
		inParm.setData("COMPLICATION_DIAG", "");
		inParm.setData("COMPLICATION_STATUS", "");
		inParm.setData("COMPLICATION_DIAG_CONDITION", "");
		
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			String exec = parm.getValue("EXEC", i);
			
			//�������ݴ���result��
			result.setData("CASE_NO", parm.getValue("CASE_NO", i));// CASE_NO�������
			inParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			result.setData("MR_NO", parm.getValue("MR_NO", i));// MR_NO�������
			result.setData("IPD_NO", parm.getValue("IPD_NO", i));// IPD_NO�������
			result.setData("IO_TYPE", parm.getValue("IO_TYPE", i));// ����
			result.setData("ICD_KIND", parm.getValue("ICD_KIND", i));// ����
			result.setData("MAIN_FLG", parm.getValue("MAIN_FLG", i));// ��\
			result.setData("ICD_CODE", parm.getValue("ICD_CODE", i));// ��ϱ���
			result.setData("SEQ_NO", parm.getInt("SEQ_NO", i));// ��ϱ���
			result.setData("ICD_DESC", parm.getValue("ICD_DESC", i));// �������
			result.setData("ICD_REMARK", parm.getValue("ICD_REMARK", i));// ��ע
			result.setData("ICD_STATUS", parm.getValue("ICD_STATUS", i));// ת��
			result.setData("ADDITIONAL_CODE", parm.getValue("ADDITIONAL_CODE",
					i));// ������
			result.setData("ADDITIONAL_DESC", parm.getValue("ADDITIONAL_DESC",
					i));// �����������
			result.setData("IN_PAT_CONDITION", parm.getValue(
					"IN_PAT_CONDITION", i));// ��Ժ����
			result.setData("OPT_USER", parm.getValue("OPT_USER", i));
			result.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
			if (parm.getValue("MAIN_FLG", i).equals("Y")) {
				if (parm.getValue("IO_TYPE", i).equals("I"))
					inParm.setData("OE_DIAG_CODE", parm.getValue("ICD_CODE",
									i));
				if (parm.getValue("IO_TYPE", i).equals("M"))
					inParm.setData("IN_DIAG_CODE", parm.getValue("ICD_CODE",
									i));
				if (parm.getValue("IO_TYPE", i).equals("O")) {
					inParm.setData("OUT_DIAG_CODE1", parm.getValue("ICD_CODE",
							i));
					inParm.setData("CODE1_STATUS", parm.getValue("ICD_STATUS",
							i));
					inParm.setData("OUT_DIAG_CONDITION1", parm.getValue(
							"IN_PAT_CONDITION", i));
				}
				if (parm.getValue("IO_TYPE", i).equals("Q")) {
					inParm.setData("INTE_DIAG_CODE", parm.getValue("ICD_CODE",
							i));
					inParm.setData("INTE_DIAG_STATUS", parm.getValue(
							"ICD_STATUS", i));
					inParm.setData("INTE_DIAG_CONDITION", parm.getValue(
							"IN_PAT_CONDITION", i));
				}
				if (parm.getValue("IO_TYPE", i).equals("W")) {
					inParm.setData("COMPLICATION_DIAG", parm.getValue(
							"ICD_CODE", i));
					inParm.setData("COMPLICATION_STATUS", parm.getValue(
							"ICD_STATUS", i));
					inParm.setData("COMPLICATION_DIAG_CONDITION", parm
							.getValue("IN_PAT_CONDITION", i));
				}
			}
			
			if("Y".equals(exec)){//EXEC='Y' ������-�޸Ĳ���
				updateParm.setData("CASE_NO", parm.getValue("CASE_NO", i));// CASE_NO�������
				//inParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
				updateParm.setData("MR_NO", parm.getValue("MR_NO", i));// MR_NO�������
				updateParm.setData("IPD_NO", parm.getValue("IPD_NO", i));// IPD_NO�������
				updateParm.setData("IO_TYPE", parm.getValue("IO_TYPE", i));// ����
				updateParm.setData("ICD_KIND", parm.getValue("ICD_KIND", i));// ����
				updateParm.setData("MAIN_FLG", parm.getValue("MAIN_FLG", i));// ��\
				updateParm.setData("ICD_CODE", parm.getValue("ICD_CODE", i));// ��ϱ���
				updateParm.setData("SEQ_NO", parm.getInt("SEQ_NO", i));// ��ϱ���
				updateParm.setData("ICD_DESC", parm.getValue("ICD_DESC", i));// �������
				updateParm.setData("ICD_REMARK", parm.getValue("ICD_REMARK", i));// ��ע
				updateParm.setData("ICD_STATUS", parm.getValue("ICD_STATUS", i));// ת��
				updateParm.setData("ADDITIONAL_CODE", parm.getValue("ADDITIONAL_CODE",
						i));// ������
				updateParm.setData("ADDITIONAL_DESC", parm.getValue("ADDITIONAL_DESC",
						i));// �����������
				updateParm.setData("IN_PAT_CONDITION", parm.getValue(
						"IN_PAT_CONDITION", i));// ��Ժ����
				updateParm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				updateParm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				
				updateParm = this.update("updateMRODiag2", updateParm, conn);
				if (updateParm.getErrCode() < 0) {
					err("ERR:" + updateParm.getErrCode() + updateParm.getErrText()
							+ updateParm.getErrName());
					return updateParm;
				}
			}else{//��������
				
				insertParm.setData("CASE_NO", parm.getValue("CASE_NO", i));// CASE_NO�������
				//inParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
				insertParm.setData("MR_NO", parm.getValue("MR_NO", i));// MR_NO�������
				insertParm.setData("IPD_NO", parm.getValue("IPD_NO", i));// IPD_NO�������
				insertParm.setData("IO_TYPE", parm.getValue("IO_TYPE", i));// ����
				insertParm.setData("ICD_KIND", parm.getValue("ICD_KIND", i));// ����
				insertParm.setData("MAIN_FLG", parm.getValue("MAIN_FLG", i));// ��\
				insertParm.setData("ICD_CODE", parm.getValue("ICD_CODE", i));// ��ϱ���
				if("Z".equals(exec)){//ת������-���ȡԭ����seq_no
					insertParm.setData("SEQ_NO", parm.getInt("SEQ_NO", i));// ��ϱ���
				}else{//��������
					//new_seq_no = getMaxSeqNo(parm.getValue("CASE_NO", i));
					insertParm.setData("SEQ_NO", seqCount);// ��ϱ���
					seqCount++;
				}
				insertParm.setData("ICD_DESC", parm.getValue("ICD_DESC", i));// �������
				insertParm.setData("ICD_REMARK", parm.getValue("ICD_REMARK", i));// ��ע
				insertParm.setData("ICD_STATUS", parm.getValue("ICD_STATUS", i));// ת��
				insertParm.setData("ADDITIONAL_CODE", parm.getValue("ADDITIONAL_CODE",
						i));// ������
				insertParm.setData("ADDITIONAL_DESC", parm.getValue("ADDITIONAL_DESC",
						i));// �����������
				insertParm.setData("IN_PAT_CONDITION", parm.getValue(
						"IN_PAT_CONDITION", i));// ��Ժ����
				insertParm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				insertParm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				
				insertParm = this.update("insertMRODiag", insertParm, conn);
				if (insertParm.getErrCode() < 0) {
					err("ERR:" + insertParm.getErrCode() + insertParm.getErrText()
							+ insertParm.getErrName());
					return insertParm;
				}
			}
			

//			//��ѯ��ϱ�-��Ӧ��ͬ���ݵ�seq_no ����ά��
//			String sql = " SELECT t.SEQ_NO FROM ADM_INPDIAG t WHERE t.CASE_NO ='"+parm.getValue("CASE_NO", i)+
//				"' AND t.MR_NO='"+parm.getValue("MR_NO", i)+"' AND t.ICD_CODE='"+parm.getValue("ICD_CODE", i)+"'";
//			System.out.println("sql="+sql);
//			err("info sql="+sql);
//			TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
//			
//			if(seqParm.getCount("SEQ_NO") > 0){
//				System.out.println("i = "+i+" SEQ_NO="+seqParm.getValue("SEQ_NO", 0));
//				err("info seq_no="+seqParm.getValue("SEQ_NO", 0));
//				result.setData("SEQ_NO", seqParm.getValue("SEQ_NO", 0));// ��ϱ���
//			}else{
//				result.setData("SEQ_NO", parm.getInt("SEQ_NO", i));// ��ϱ���
//			}
			
			
		}
		if (parm.getCount("CASE_NO") > 0) {
			result = this.update("updatenewMRODiag", inParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			if (!inParm.getValue("IN_DIAG_CODE").equals("")) {
				result = this.updateCONFIRM_DATE(parm.getValue("CASE_NO"), conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * �޸Ĳ������2-�ٴ����ҳ���޸������Ҫͬ�����ݵ������� duzhw add 20131017
	 * 
	 * @param parm
	 *            TParm ���������CASE_NO ����˵���� OE_DIAG_CODE:�ż������CODE
	 *            OE_DIAG_CODE2:�ż������2CODE OE_DIAG_CODE3:�ż������3CODE
	 *            IN_DIAG_CODE����Ժ���CODE IN_DIAG_CODE2����Ժ���2CODE
	 *            IN_DIAG_CODE3����Ժ���3CODE OUT_DIAG_CODE1����Ժ�����CODE
	 *            CODE1_REMARK����Ժ����ϱ�ע CODE1_STATUS����Ժ�����ת��״̬
	 *            OUT_DIAG_CODE2����Ժ���CODE CODE2_REMARK����Ժ��ϱ�ע
	 *            CODE2_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE3����Ժ���CODE
	 *            CODE3_REMARK����Ժ��ϱ�ע CODE3_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE4����Ժ���CODE CODE4_REMARK����Ժ��ϱ�ע
	 *            CODE4_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE5����Ժ���CODE
	 *            CODE5_REMARK����Ժ��ϱ�ע CODE5_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE6����Ժ���CODE CODE6_REMARK����Ժ��ϱ�ע
	 *            CODE6_STATUS����Ժ���ת��״̬ INTE_DIAG_CODE��Ժ�ڸ�Ⱦ���CODE
	 *            CASE_NO������case_no
	 * @return TParm
	 */
	public TParm updateMRODiag2(TParm parm, TConnection conn) {
		TParm result = new TParm();

		TParm delParm1 = parm.getParm("DELDATA");
		TParm inParm1 = parm.getParm("DIAGDATA");
		//ͬ��ѭ��ɾ��MRO_RECORD_DIAG������
		TParm delParm = new TParm();
		for (int i = 0; i < delParm1.getCount("CASE_NO"); i++) {
			TParm delParm2=delParm1.getRow(i);
			delParm = new TParm(this.getDBTool().update(
					"DELETE FROM MRO_RECORD_DIAG WHERE CASE_NO='"
							+ delParm2.getValue("CASE_NO") + "' AND SEQ_NO = '"+
							delParm2.getValue("SEQ_NO") + "'"));
		}
		if (delParm.getErrCode() < 0) {
			err("ERR:" + delParm.getErrCode() + delParm.getErrText()
					+ delParm.getErrName());
			return delParm;
		}

		TParm inParm = new TParm();
		inParm.setData("OE_DIAG_CODE", "");
		inParm.setData("IN_DIAG_CODE", "");
		inParm.setData("OUT_DIAG_CODE1", "");
		inParm.setData("CODE1_STATUS", "");
		inParm.setData("OUT_DIAG_CONDITION1", "");
		inParm.setData("INTE_DIAG_CODE", "");
		inParm.setData("INTE_DIAG_STATUS", "");
		inParm.setData("INTE_DIAG_CONDITION", "");
		inParm.setData("COMPLICATION_DIAG", "");
		inParm.setData("COMPLICATION_STATUS", "");
		inParm.setData("COMPLICATION_DIAG_CONDITION", "");
        for (int i = inParm1.getCount("CASE_NO") - 1; i >= 0; i--) {
            if (inParm1.getValue("IO_TYPE", i).equals("Z")) {// ����Z�����벡����ҳ modify by wanglong 20140410
                inParm1.removeRow(i);
            }
        }
		for (int i = 0; i < inParm1.getCount("CASE_NO"); i++) {
			result.setData("CASE_NO", inParm1.getValue("CASE_NO", i));// CASE_NO�������
			inParm.setData("CASE_NO", inParm1.getValue("CASE_NO", i));
			result.setData("MR_NO", inParm1.getValue("MR_NO", i));// MR_NO�������
			result.setData("IPD_NO", inParm1.getValue("IPD_NO", i));// IPD_NO�������
			result.setData("IO_TYPE", inParm1.getValue("IO_TYPE", i));// ����
			result.setData("ICD_KIND", inParm1.getValue("ICD_KIND", i));// ����
			result.setData("MAIN_FLG", inParm1.getValue("MAIN_FLG", i));// ��\
			result.setData("ICD_CODE", inParm1.getValue("ICD_CODE", i));// ��ϱ���
			result.setData("SEQ_NO", inParm1.getInt("SEQ_NO", i));// ��ϱ���
			result.setData("ICD_DESC", inParm1.getValue("ICD_DESC", i));// �������
			result.setData("ICD_REMARK", inParm1.getValue("ICD_REMARK", i));// ��ע
			result.setData("ICD_STATUS", inParm1.getValue("ICD_STATUS", i));// ת��
			result.setData("ADDITIONAL_CODE", inParm1.getValue("ADDITIONAL_CODE",
					i));// ������
			result.setData("ADDITIONAL_DESC", inParm1.getValue("ADDITIONAL_DESC",
					i));// �����������
			result.setData("IN_PAT_CONDITION", inParm1.getValue(
					"IN_PAT_CONDITION", i));// ��Ժ����
			result.setData("OPT_USER", inParm1.getValue("OPT_USER", i));
			result.setData("OPT_TERM", inParm1.getValue("OPT_TERM", i));
			if (inParm1.getValue("MAIN_FLG", i).equals("Y")) {
				if (inParm1.getValue("IO_TYPE", i).equals("I"))
					inParm.setData("OE_DIAG_CODE", inParm1.getValue("ICD_CODE",
									i));
				if (inParm1.getValue("IO_TYPE", i).equals("M"))
					inParm.setData("IN_DIAG_CODE", inParm1.getValue("ICD_CODE",
									i));
				if (inParm1.getValue("IO_TYPE", i).equals("O")) {
					inParm.setData("OUT_DIAG_CODE1", inParm1.getValue("ICD_CODE",
							i));
					inParm.setData("CODE1_STATUS", inParm1.getValue("ICD_STATUS",
							i));
					inParm.setData("OUT_DIAG_CONDITION1", inParm1.getValue(
							"IN_PAT_CONDITION", i));
				}
				if (inParm1.getValue("IO_TYPE", i).equals("Q")) {
					inParm.setData("INTE_DIAG_CODE", inParm1.getValue("ICD_CODE",
							i));
					inParm.setData("INTE_DIAG_STATUS", inParm1.getValue(
							"ICD_STATUS", i));
					inParm.setData("INTE_DIAG_CONDITION", inParm1.getValue(
							"IN_PAT_CONDITION", i));
				}
				if (inParm1.getValue("IO_TYPE", i).equals("W")) {
					inParm.setData("COMPLICATION_DIAG", inParm1.getValue(
							"ICD_CODE", i));
					inParm.setData("COMPLICATION_STATUS", inParm1.getValue(
							"ICD_STATUS", i));
					inParm.setData("COMPLICATION_DIAG_CONDITION", inParm1
							.getValue("IN_PAT_CONDITION", i));
				}
			}
			result = this.update("insertMRODiag", result, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		
		if (inParm1.getCount("CASE_NO") > 0) {
			result = this.update("updatenewMRODiag", inParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			if (!inParm.getValue("IN_DIAG_CODE").equals("")) {
				result = this.updateCONFIRM_DATE(inParm1.getValue("CASE_NO"), conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		
		
		return result;
	}

	/**
	 * ������ҳת�빦�����ݲ�ѯ����
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm intoData(String CASE_NO) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		// ����סԺ������Ϣ adm_inp
		TParm inHospInfo = ADMTool.getInstance().getADM_INFO(parm);
		if (inHospInfo.getErrCode() < 0) {
			err("ERR:" + inHospInfo.getErrCode() + inHospInfo.getErrText()
					+ inHospInfo.getErrName());
			return inHospInfo;
		}
		// ������̬��Ϣ
		TParm admTran = ADMTransLogTool.getInstance().getTranHospFormro(parm);
		if (admTran.getErrCode() < 0) {
			err("ERR:" + admTran.getErrCode() + admTran.getErrText()
					+ admTran.getErrName());
			return admTran;
		}
		// // ���������Ϣ
		// TParm admDiag = ADMTool.getInstance().queryDiagForMro(parm);
		// if (admDiag.getErrCode() < 0) {
		// err("ERR:" + admDiag.getErrCode() + admDiag.getErrText()
		// + admDiag.getErrName());
		// return admDiag;
		// }
		TParm result = new TParm();
		result.setData("ADMINP", inHospInfo.getData());// סԺ��Ϣ
		result.setData("ADMTRAN", admTran.getData());// ��̬��Ϣ
		// result.setData("ADMDIAG", admDiag.getData());// �������
		return result;
	}

	/**
	 * ����IBSϵͳ���صĲ������� ������ҳ����
	 * 
	 * @param Case_no
	 *            String
	 * @return TParm
	 */
	public TParm updateMROIbsForIBS(String Case_no) {
		TParm ibs = IBSOrderdTool.getInstance().selRexpCodePatFee(Case_no);
		if (ibs.getErrCode() < 0) {
			err("ERR:" + ibs.getErrCode() + ibs.getErrText() + ibs.getErrName());
			return ibs;
		}
		// �Լ�ֵ�Ե���ʽ�洢��������
		Map charge = new HashMap();
		double sumTot = 0;
		double ownTot = 0;
		Map MrofeeCode = getMROChargeName();
		for (int i = 0; i < ibs.getCount(); i++) {
			if (ibs.getValue("MRO_CHARGE_CODE", i).length() > 0) {
				charge.put(ibs.getValue("MRO_CHARGE_CODE", i), ibs.getDouble(
						"TOT_AMT", i));
			}
			sumTot += ibs.getDouble("TOT_AMT", i);
			ownTot += ibs.getDouble("OWN_AMT", i);
		}
		String seq = "";
		String c_name = "";
		String c_name1 = "";
		TParm parm = new TParm();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE_";
			c_name1 = "CHARGE";
			if (i < 10)// IС��10 ����
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			c_name1 += seq;
			parm.setData(c_name,
					charge.get(MrofeeCode.get(c_name1)) == null ? 0 : charge
							.get(MrofeeCode.get(c_name1)));
		}
		parm.setData("SUM_TOT", sumTot);
		parm.setData("OWN_TOT", sumTot);// ����OWN_AMT���������ݶ���TOT_AMT��ֵ��OWN_TOT
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", Case_no);
		TParm result = this.update("updateMROIBS", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ҳ���ô�����MROChargeName����
	 * 
	 * @return TParm
	 */
	public Map getMROChargeName() {
		TParm result = this.query("getMroChargeName");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return null;
		}
		String seq = "";
		String c_name = "";
		Map map = new HashMap();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE";
			if (i < 10)// IС��10 ����
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			map.put(c_name, result.getData(c_name, 0));
		}
		return map;
	}

	/**
	 * ͬ������ADM_INP INS_ADM_CONFIRM�ĳ�Ժ���� shibl 20130108 add
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm modifydsDate(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String caseNo = parm.getValue("CASE_NO");
		String dsDate = "";
		if(!parm.getValue("OUT_DATE").equals(String.valueOf(new TNull(Timestamp.class))))
			dsDate=StringTool.getString(parm.getTimestamp("OUT_DATE"),"yyyyMMddHHmmss");
		String ctzCode = parm.getValue("CTZ1_CODE");
		String selSql = "SELECT DS_DATE FROM ADM_INP WHERE CASE_NO='" + caseNo
				+ "'";
		result = new TParm(TJDODBTool.getInstance().select(selSql));
		if (!result.getValue("DS_DATE", 0).equals("")) {//�ж�������Ѿ���Ժ�Ÿ�������
			String admSql = "UPDATE ADM_INP SET DS_DATE=TO_DATE('" + dsDate
					+ "','YYYYMMDDHH24MISS') WHERE CASE_NO='" + caseNo + "'";
			result = new TParm(TJDODBTool.getInstance().update(admSql, conn));
			if (result.getErrCode() < 0) {
				conn.rollback();
				return result;
			}
		}
		String ctzSql = "SELECT NHI_CTZ_FLG FROM SYS_CTZ WHERE CTZ_CODE='"+ ctzCode + "'";
		result = new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (result.getCount() > 0) {
			if (result.getValue("NHI_CTZ_FLG", 0).equals("Y")) {// ����ҽ�����
				String insSelectSql = "SELECT IN_STATUS FROM INS_ADM_CONFIRM WHERE CASE_NO='"
						+ caseNo + "'";
				result = new TParm(TJDODBTool.getInstance()
						.select(insSelectSql));
				if (result.getCount() > 0) {
					if (result.getValue("IN_STATUS", 0).equals("2")) {// ҽ���ύ״̬
						result.setErrCode(-100);
						result.setErrText("ҽ���������ϴ��ύ,�����޸ĳ�Ժ����");
						conn.rollback();
						return result;
					} else {
						String insSql = "UPDATE INS_ADM_CONFIRM SET DS_DATE=TO_DATE('"
								+ dsDate
								+ "','YYYYMMDDHH24MISS') WHERE CASE_NO='"
								+ caseNo + "'";
						result = new TParm(TJDODBTool.getInstance().update(
								insSql, conn));
						if (result.getErrCode() < 0) {
							conn.rollback();
							return result;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * ��ѯĳһ������������Ϣ
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm queryOP_Info(String CASE_NO) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = this.query("queryOP_Info", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯĳһ������������Ϣ����ӡ��
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm queryPrintOP(String CASE_NO) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = this.query("queryPrintOP", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸���ҳ��MRO_RECORD���������ֶ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateMRORecordOp(TParm parm) {
		TParm result = this.query("updateMRORecordOp", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * סԺ�ٻ� ��ղ�����Ժ����
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm clearOUT_DATE(String CASE_NO, TConnection conn) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = this.update("clearOUT_DATE", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸Ĳ������״̬
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateMRO_CHAT_FLG(TParm parm, TConnection conn) {
		TParm result = this.update("updateMRO_CHAT_FLG", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸Ĳ������״̬
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateMRO_CHAT_FLG(TParm parm) {
		TParm result = this.update("updateMRO_CHAT_FLG", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ����ҳ��Ϣ MRO_RECORD������
	 * 
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteMRO(String CASE_NO, TConnection conn) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = this.update("deleteMRO", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�ȷ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateCONFIRM_DATE(String CASE_NO, TConnection conn) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = this.update("updateCONFIRM_DATE", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���������޸���ҳ����ֶ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateForWaitPat(TParm parm, TConnection conn) {
		TParm result = this.update("updateForWaitPat", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �Ƿ����סԺ�����
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm existMDiagCode(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = this.query("existMDiagCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * �޸ķ���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======pangben 20110801
	 */
	public TParm updateSCODE(TParm parm, TConnection conn) {
		TParm result = this.update("updateSCODE", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * �޸�״̬�ͷ���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======pangben 20110801
	 */
	public TParm updateTYPERESULT(TParm parm, TConnection conn) {
		TParm result = this.update("updateTYPERESULT", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * ������ע��
	 * 
	 * @param parm
	 * @return
	 */
	public boolean getNewBornFlg(TParm parm) {
		boolean flg = false;
		TParm result = this.query("getNewBornFlg", parm);
		if (result.getErrCode() < 0) {
			System.out.println("ERR:" + result.getErrCode()
					+ result.getErrText() + result.getErrName());
			return false;
		}
		if (result.getBoolean("NEW_BORN_FLG", 0)) {
			flg = true;
		}
		return flg;
	}

	/**
	 * ������ҽ��ȡ�������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getDiagForIns(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "Err:��������ΪNULL");
			return result;
		}
		String sql = " SELECT ICD_CODE, ICD_DESC , ICD_STATUS ,IO_TYPE , B.CHN_DESC "
				+ " FROM MRO_RECORD_DIAG A LEFT JOIN   SYS_DICTIONARY  B    "
				+ " ON  A.ICD_STATUS   =  B.ID  AND B.GROUP_ID   = 'ADM_RETURN' "
				+ " WHERE CASE_NO = '"
				+ parm.getData("CASE_NO")
				+ "'  "
				+ " AND IO_TYPE = '"
				+ parm.getData("IO_TYPE")
				+ "' "
				+ " ORDER BY A.MAIN_FLG DESC ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * ��ѯҽʦ�Ƿ�Ϊ��Ժҽʦ
	 * @param userId ҽʦUSER_ID
	 * @return
	 */
	public TParm isOutDoc(String userId){
		TParm result = new TParm();
		if(userId == null || userId.length() <= 0){
			result.setErr(-1, "Err:��������ΪNULL");
			return result;
		}
		String sql = "SELECT IS_OUT_FLG FROM SYS_OPERATOR " +
					 "WHERE USER_ID = '" + userId + "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * ���ݷ����Ų�ѯ��������
	 * @param roomCode ������
	 * @return
	 */
	public String getRoomDesc(String deptCode){
		String deptDesc = "";
		if(deptCode == null){
			return "";
		}
		String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT " +
		 "WHERE DEPT_CODE = '" + deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		deptDesc = result.getValue("DEPT_CHN_DESC", 0).toString();
		return deptDesc;
	}

		/**
	 * ȡMRO_RECORD_DIAG���ADM_INPDIAG��������seq_no��1 duzhw 20131129
	 * @return
	 */
	public String getMaxSeqNo(String caseNo){
		int seqNo1 = 0;
		int seqNo2 = 0;
		int maxSeqNo = 0;
		String seqNoStr1 = "";
		String seqNoStr2 = "";
		boolean flag = false;
		String sql1 = "select max(seq_no) as seq_no from ADM_INPDIAG where case_no = '"+caseNo+"'";
		String sql2 = "select max(seq_no) as seq_no from MRO_RECORD_DIAG where case_no = '"+caseNo+"'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		seqNoStr1 = result1.getValue("SEQ_NO", 0);
		seqNoStr2 = result2.getValue("SEQ_NO", 0);
		if(!"".equals(seqNoStr1) && seqNoStr1!=null){
			seqNo1 = Integer.parseInt(seqNoStr1);
		}
		if(!"".equals(seqNoStr2) || seqNoStr2!=null){
			seqNo2 = Integer.parseInt(seqNoStr2);
		}
		//�жϱȽ�
		if(seqNo1 >= seqNo2){
			maxSeqNo = seqNo1;
		}else {
			maxSeqNo = seqNo2;
		}
		
		return  Integer.toString(maxSeqNo+1);
		
	}
	
    /**
     * ���������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertMRODiag(TParm parm, TConnection conn) {//add by wanglong 20140410
        TParm result = this.update("insertMRODiag", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
}
