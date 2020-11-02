package jdo.mro;

import jdo.adm.ADMTransLogTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;

/**
 * <p>
 * Title: MRO�ӿڷ���(��̨Tool��ǰ��̨��Ҫ����)
 * </p>
 * 
 * <p>
 * Description: MRO�ӿڷ���(��̨Tool��ǰ��̨��Ҫ����)
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
 * @author zhangk 2009-4-28
 * @version 1.0
 */
public class MROTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static MROTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return SYSRegionTool
	 */
	public static MROTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROTool();
		return instanceObject;
	}

	public MROTool() {
		setModuleName("mro\\MROToolModule.x");
		onInit();
	}

	/**
	 * ת����ת����
	 * 
	 * @param parm
	 *            TParm ������Ϣ�� CASE_NO,OPT_USER,OPT_TERMΪ������������� ��ϲ�����ѡ ==>
	 *            TRANS_DEPT ת�ƿƱ� OUT_DATE ��Ժ���� OUT_DEPT ��Ժ�Ʊ� OUT_STATION ��Ժ����
	 *            OUT_ROOM_NO ��Ժ���� REAL_STAY_DAYS ʵ��סԺ����
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTransDept(TParm parm, TConnection conn) {
		TParm result = new TParm();
		TParm nursParm=this.getIbsNusParm(parm);
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("OUT_DATE") != null) // ��Ժ����
		{
			sql += " OUT_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("OUT_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS'),";
			sql += " BILCHK_FLG='Y',";
		}
		if (parm.getData("IN_ROOM_NO") != null) // ��Ժ����
			sql += " IN_ROOM_NO='" + parm.getValue("IN_ROOM_NO") + "',";
		if (parm.getData("OUT_DEPT") != null) // ��Ժ�Ʊ�
			sql += " OUT_DEPT='" + parm.getValue("OUT_DEPT") + "',";
		if (parm.getData("OUT_STATION") != null) // ��Ժ����
			sql += " OUT_STATION='" + parm.getValue("OUT_STATION") + "',";
		if (parm.getData("OUT_ROOM_NO") != null) // ��Ժ����
			sql += " OUT_ROOM_NO='" + parm.getValue("OUT_ROOM_NO") + "',";
		if (parm.getData("REAL_STAY_DAYS") != null) // ʵ��סԺ����
			sql += " REAL_STAY_DAYS='" + parm.getValue("REAL_STAY_DAYS") + "',";
		if (!parm.getValue("VS_NURSE_CODE").equals("")) // ���λ�ʿ
			sql += " VS_NURSE_CODE='" + parm.getValue("VS_NURSE_CODE") + "',";
		if (!parm.getValue("DIRECTOR_DR_CODE").equals("")) // ������
			sql += " DIRECTOR_DR_CODE='" + parm.getValue("DIRECTOR_DR_CODE")+ "',";
		if (!parm.getValue("ATTEND_DR_CODE").equals("")) // ����ҽʦ
			sql += " ATTEND_DR_CODE='" + parm.getValue("ATTEND_DR_CODE") + "',";
		if (!parm.getValue("VS_DR_CODE").equals("")) // ����ҽʦ
			sql += " VS_DR_CODE='" + parm.getValue("VS_DR_CODE") + "',";
		if (nursParm.getData("N0") != null) // �ؼ���������
			sql += " SPENURS_DAYS='" + nursParm.getValue("N0") + "',";
		if (nursParm.getData("N1") != null) // һ����������
			sql += " FIRNURS_DAYS='" + nursParm.getValue("N1") + "',";
		if (nursParm.getData("N2") != null) // ������������
			sql += " SECNURS_DAYS='" + nursParm.getValue("N2") + "',";
		if (nursParm.getData("N3") != null) // ������������
			sql += " THRNURS_DAYS='" + nursParm.getValue("N3") + "',";
		if (nursParm.getData("03") != null) // ������Сʱ
			sql += " VENTI_TIME=" + nursParm.getInt("03") + ",";
		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		if (parm.getData("CASE_NO") != null)
			sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateTransDept==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * �õ��Ʒѷ��и�����������
	 * 
	 * @return
	 */
	public TParm getIbsNusParm(TParm inparm) {
		TParm result = new TParm();
		//��ʼ��
		TParm parm=new TParm();
		String caseNo = inparm.getValue("CASE_NO");
		String sql = " SELECT  SUM(A.TAKE_DAYS*A.DOSAGE_QTY) AS QTY,B.ORD_SUPERVISION FROM IBS_ORDD A,SYS_FEE B "
				+ " WHERE   A.CASE_NO='"
				+ caseNo
				+ "'"
				+ " AND A.ORDER_CODE=B.ORDER_CODE "
				+ " AND  B.ORD_SUPERVISION IN ('N0','N1','N2','N3','03')"
				+ " GROUP BY B.ORD_SUPERVISION";
		result=new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return parm;
		}
		if(result.getCount()<0)
			return parm;
		for(int i=0;i<result.getCount();i++){
			if(result.getValue("ORD_SUPERVISION", i).equals("N0")){
				parm.setData("N0", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N1")){
				parm.setData("N1", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N2")){
				parm.setData("N2", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N3")){
				parm.setData("N3", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("03")){
				parm.setData("03", result.getInt("QTY", i));
			}
		}
		return parm;

	}
	/**
	 * �޸Ĳ������
	 * 
	 * @param parm
	 *            TParm ���������CASE_NO ����˵���� OE_DIAG_CODE:�ż������CODE
	 *            IN_DIAG_CODE����Ժ���CODE OUT_DIAG_CODE1����Ժ�����CODE
	 *            CODE1_REMARK����Ժ����ϱ�ע CODE1_STATUS����Ժ�����ת��״̬
	 *            OUT_DIAG_CODE2����Ժ���CODE CODE2_REMARK����Ժ��ϱ�ע
	 *            CODE2_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE3����Ժ���CODE
	 *            CODE3_REMARK����Ժ��ϱ�ע CODE3_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE4����Ժ���CODE CODE4_REMARK����Ժ��ϱ�ע
	 *            CODE4_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE5����Ժ���CODE
	 *            CODE5_REMARK����Ժ��ϱ�ע CODE5_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE6����Ժ���CODE CODE6_REMARK����Ժ��ϱ�ע
	 *            CODE6_STATUS����Ժ���ת��״̬ INTE_DIAG_CODE��Ժ�ڸ�Ⱦ���CODE
	 *            CASE_NO������case_no COMPLICATION_DIAG��Ժ�ڲ������CODE
	 * @return TParm
	 */
	public TParm updateMRODiag(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = MRORecordTool.getInstance().updateMRODiag(parm, conn);
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateMRODiag==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * �޸Ĳ������2 �����ٴ����ҳ���޸�����ͬ��������-duzhw add 20131017
	 * 
	 * @param parm
	 *            TParm ���������CASE_NO ����˵���� OE_DIAG_CODE:�ż������CODE
	 *            IN_DIAG_CODE����Ժ���CODE OUT_DIAG_CODE1����Ժ�����CODE
	 *            CODE1_REMARK����Ժ����ϱ�ע CODE1_STATUS����Ժ�����ת��״̬
	 *            OUT_DIAG_CODE2����Ժ���CODE CODE2_REMARK����Ժ��ϱ�ע
	 *            CODE2_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE3����Ժ���CODE
	 *            CODE3_REMARK����Ժ��ϱ�ע CODE3_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE4����Ժ���CODE CODE4_REMARK����Ժ��ϱ�ע
	 *            CODE4_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE5����Ժ���CODE
	 *            CODE5_REMARK����Ժ��ϱ�ע CODE5_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE6����Ժ���CODE CODE6_REMARK����Ժ��ϱ�ע
	 *            CODE6_STATUS����Ժ���ת��״̬ INTE_DIAG_CODE��Ժ�ڸ�Ⱦ���CODE
	 *            CASE_NO������case_no COMPLICATION_DIAG��Ժ�ڲ������CODE
	 * @return TParm
	 */
	public TParm updateMRODiag2(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = MRORecordTool.getInstance().updateMRODiag2(parm, conn);
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROTool.updateMRODiag2==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸� ҩƷ����
	 * 
	 * @param parm
	 *            TParm ������Ϣ�� CASE_NO,ALLEGIC,OPT_USER,OPT_TERMΪ�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateALLEGIC(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("ALLEGIC") != null)// ������¼
			sql += " ALLEGIC='" + parm.getValue("ALLEGIC") + "',";

		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateALLEGIC==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���� ������Ϣ
	 * 
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOP(TParm parm, TConnection conn) {
		TParm result;
		result = MRORecordTool.getInstance().insertOP(parm, conn);
		return result;
	}

	/**
	 * �޸� ������Ϣ
	 * 
	 * @param parm
	 *            TParm ������Ϣ�� CASE_NO,SEQ_NO,OPT_USER,OPT_TERMΪ�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOP(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm.getData("CASE_NO") == null || parm.getData("SEQ_NO") == null) {
			result.setErr(-1, "��Ҫ�������������ڣ�");
			return result;
		}
		String sql = "UPDATE MRO_RECORD_OP SET ";
		if (parm.getData("IPD_NO") != null)// סԺ��
			sql += " IPD_NO='" + parm.getValue("IPD_NO") + "',";
		if (parm.getData("MR_NO") != null)// ������
			sql += " MR_NO='" + parm.getValue("MR_NO") + "',";
		if (parm.getData("OP_CODE") != null)// ��������
			sql += " OP_CODE='" + parm.getValue("OP_CODE") + "',";
		if (parm.getData("OP_DESC") != null)// ��������
			sql += " OP_DESC='" + parm.getValue("OP_DESC") + "',";
		if (parm.getData("OP_REMARK") != null)// ������ע
			sql += " OP_REMARK='" + parm.getValue("OP_REMARK") + "',";
		if (parm.getData("OP_DATE") != null)// ����ʱ��
			sql += " OP_DATE=TO_DATE('" + parm.getValue("OP_DATE")
					+ "','YYYYMMDDHH24MISS'),";
		if (parm.getData("ANA_WAY") != null)// ����ʽ
			sql += " ANA_WAY='" + parm.getValue("ANA_WAY") + "',";
		if (parm.getData("ANA_DR") != null)// ����ҽʦ
			sql += " ANA_DR='" + parm.getValue("ANA_DR") + "',";
		if (parm.getData("MAIN_SUGEON") != null)// ����
			sql += " MAIN_SUGEON='" + parm.getValue("MAIN_SUGEON") + "',";
		if (parm.getData("AST_DR1") != null)// ����һ
			sql += " AST_DR1='" + parm.getValue("AST_DR1") + "',";
		if (parm.getData("AST_DR2") != null)// ������
			sql += " AST_DR2='" + parm.getValue("AST_DR2") + "',";
		if (parm.getData("HEALTH_LEVEL") != null)// �п����ϵȼ�
			sql += " HEALTH_LEVEL='" + parm.getValue("HEALTH_LEVEL") + "',";
		if (parm.getData("OP_LEVEL") != null)// �����ȼ�
			sql += " OP_LEVEL='" + parm.getValue("OP_LEVEL") + "',";
		if (parm.getData("OPT_USER") != null)
			sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',";
		if (parm.getData("OPT_TERM") != null)
			sql += " OPT_TERM='" + parm.getValue("OPT_TERM") + "',";
		sql += " OPT_DATE=SYSDATE "; // �޸�ʱ��
		sql += " WHERE CASE_NO = '" + parm.getValue("CASE_NO")
				+ "' AND SEQ_NO = '" + parm.getValue("SEQ_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸� ��Ѫ��Ϣ
	 * 
	 * @param parm
	 *            TParm CASE_NO,OPT_USER,OPT_TERMΪ�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBlood(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("BLOOD_TYPE") != null)// Ѫ��
			sql += " BLOOD_TYPE='" + parm.getValue("BLOOD_TYPE") + "',";
		if (parm.getData("RH_TYPE") != null)// RH
			sql += " RH_TYPE='" + parm.getValue("RH_TYPE") + "',";
		if (parm.getData("TRANS_REACTION") != null)// ��Ѫ��Ӧ
			sql += " TRANS_REACTION='" + parm.getValue("TRANS_REACTION") + "',";
		if (parm.getData("RBC") != null)// ��Ѫ��
			sql += " RBC='" + parm.getValue("RBC") + "',";
		if (parm.getData("PLATE") != null)// ѪС��
			sql += " PLATE='" + parm.getValue("PLATE") + "',";
		if (parm.getData("PLASMA") != null)// Ѫ��
			sql += " PLASMA='" + parm.getValue("PLASMA") + "',";
		if (parm.getData("WHOLE_BLOOD") != null)// ȫѪ
			sql += " WHOLE_BLOOD='" + parm.getValue("WHOLE_BLOOD") + "',";
		if (parm.getData("OTH_BLOOD") != null)// ����ѪƷ����
			sql += " OTH_BLOOD='" + parm.getValue("OTH_BLOOD") + "',";
		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�������Ϣ
	 * 
	 * @param parm
	 *            TParm CASE_NO,OPT_USER,OPT_TERMΪ������� ����������ѡ==> CHARGE_01
	 *            ��λ��(ס);CHARGE_02 �����(ס);CHARGE_03 ��ҩ��(ס);CHARGE_04
	 *            �г�ҩ��(ס);CHARGE_05 �в�ҩ��(ס); CHARGE_06 �����(ס);CHARGE_07
	 *            �����(ס);CHARGE_08 ������(ס); CHARGE_09 ��Ѫ��(ס);CHARGE_10
	 *            ���Ʒ�(ס);CHARGE_11 ������(ס); CHARGE_12 ������(ס);CHARGE_13
	 *            ����(ס);CHARGE_14 �Ҵ���(ס); CHARGE_15 �����(ס);CHARGE_16
	 *            Ӥ����(ס);CHARGE_17 ����; CHARGE_18 �����ޣ�;CHARGE_19 �����ޣ�;CHARGE_20
	 *            �����ޣ�; ���������Ǹ��� SYS_DICTIONARY ������ GROUP_ID='MRO_CHARGE'
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateCharge(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("CHARGE_01") != null)// ��λ��(ס)
			sql += " CHARGE_01='" + parm.getValue("CHARGE_01") + "',";
		if (parm.getData("CHARGE_02") != null)// �����(ס)
			sql += " CHARGE_02='" + parm.getValue("CHARGE_02") + "',";
		if (parm.getData("CHARGE_03") != null)// ��ҩ��(ס)
			sql += " CHARGE_03='" + parm.getValue("CHARGE_03") + "',";
		if (parm.getData("CHARGE_04") != null)// �г�ҩ��(ס)
			sql += " CHARGE_04='" + parm.getValue("CHARGE_04") + "',";
		if (parm.getData("CHARGE_05") != null)// �в�ҩ��(ס)
			sql += " CHARGE_05='" + parm.getValue("CHARGE_05") + "',";
		if (parm.getData("CHARGE_06") != null)// �����(ס)
			sql += " CHARGE_06='" + parm.getValue("CHARGE_06") + "',";
		if (parm.getData("CHARGE_07") != null)// �����(ס)
			sql += " CHARGE_07='" + parm.getValue("CHARGE_07") + "',";
		if (parm.getData("CHARGE_08") != null)// ������(ס)
			sql += " CHARGE_08='" + parm.getValue("CHARGE_08") + "',";
		if (parm.getData("CHARGE_09") != null)// ��Ѫ��(ס)
			sql += " CHARGE_09='" + parm.getValue("CHARGE_09") + "',";
		if (parm.getData("CHARGE_10") != null)// ���Ʒ�(ס)
			sql += " CHARGE_10='" + parm.getValue("CHARGE_10") + "',";
		if (parm.getData("CHARGE_11") != null)// ������(ס)
			sql += " CHARGE_11='" + parm.getValue("CHARGE_11") + "',";
		if (parm.getData("CHARGE_12") != null)// ������(ס)
			sql += " CHARGE_12='" + parm.getValue("CHARGE_12") + "',";
		if (parm.getData("CHARGE_13") != null)// ����(ס)
			sql += " CHARGE_13='" + parm.getValue("CHARGE_13") + "',";
		if (parm.getData("CHARGE_14") != null)// �Ҵ���(ס)
			sql += " CHARGE_14='" + parm.getValue("CHARGE_14") + "',";
		if (parm.getData("CHARGE_15") != null)// �����(ס)
			sql += " CHARGE_15='" + parm.getValue("CHARGE_15") + "',";
		if (parm.getData("CHARGE_16") != null)// Ӥ����(ס)
			sql += " CHARGE_16='" + parm.getValue("CHARGE_16") + "',";
		if (parm.getData("CHARGE_17") != null)// ����
			sql += " CHARGE_17='" + parm.getValue("CHARGE_17") + "',";
		if (parm.getData("CHARGE_18") != null)
			sql += " CHARGE_18='" + parm.getValue("CHARGE_18") + "',";
		if (parm.getData("CHARGE_19") != null)
			sql += " CHARGE_19='" + parm.getValue("CHARGE_19") + "',";
		if (parm.getData("CHARGE_20") != null)
			sql += " CHARGE_20='" + parm.getValue("CHARGE_20") + "',";

		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * סԺ�Ǽ�ʱ���벡����ҳ���¼
	 * 
	 * @param parm
	 *            TParm MR_NO,IPD_NO,CASE_NO IN_DATE ��Ժ��; IN_DEPT ��Ժ�Ʊ�;
	 *            IN_STATION ��Ժ����; IN_ROOM_NO ��Ժ����; OE_DIAG_CODE �ż������;
	 *            IN_CONDITION ��Ժ���; PG_OWNER ��ҳ������;
	 * @return TParm
	 */
	public TParm insertMRORecord(TParm p, TConnection conn) {
		TParm result = new TParm();
		TParm parm = new TParm();
		Pat pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		parm.setData("MR_NO", p.getValue("MR_NO"));
		parm.setData("IPD_NO", p.getValue("IPD_NO"));
		// Patinfoȡ�ò���
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("IDNO", pat.getIdNo());
		parm.setData("SEX", pat.getSexCode());
		parm.setData("BIRTH_DATE", pat.getBirthday());
		parm.setData("CASE_NO", p.getValue("CASE_NO"));
		parm.setData("MARRIGE", pat.getMarriageCode());
		parm.setData("AGE",
				OdoUtil.showAge(pat.getBirthday(), p.getTimestamp("IN_DATE")));// ��������
																				// �������պ���Ժʱ�����
		parm.setData("NATION", pat.getNationCode());
		parm.setData("FOLK", pat.getSpeciesCode());
		parm.setData("CTZ1_CODE", pat.getCtz1Code());
		parm.setData("H_TEL", pat.getTelHome());// ���ڵ绰 �õ� ��ͥ�绰
		parm.setData("H_ADDRESS", pat.getResidAddress());// ������ַ
		parm.setData("H_POSTNO", pat.getResidPostCode());// �����ʱ�
		parm.setData("OCCUPATION", pat.getOccCode());// ְҵ
		parm.setData("OFFICE", pat.getCompanyDesc());// ��λ
		parm.setData("O_TEL", pat.getTelCompany());// ��λ�绰
		parm.setData("O_ADDRESS", "");// ��λ��ַ
		parm.setData("O_POSTNO", "");// ��λ�绰
		parm.setData("CONTACTER", pat.getContactsName());// ��ϵ��
		parm.setData("RELATIONSHIP", pat.getRelationCode());// ��ϵ�˹�ϵ
		parm.setData("CONT_TEL", pat.getContactsTel());// ��ϵ�˵绰
		parm.setData("CONT_ADDRESS", pat.getContactsAddress());// ��ϵ�˵�ַ
		parm.setData("HOEMPLACE_CODE", pat.gethomePlaceCode());// �����ش���
		// סԺ�ǼǴ������
		parm.setData("IN_DATE", p.getData("IN_DATE"));// ��Ժ��
		parm.setData("IN_DEPT", p.getData("IN_DEPT"));// ��Ժ�Ʊ�
		parm.setData("IN_STATION", p.getData("IN_STATION"));// ��Ժ����
		parm.setData("IN_ROOM_NO", p.getData("IN_ROOM_NO"));// ��Ժ����
		parm.setData("OE_DIAG_CODE", p.getData("OE_DIAG_CODE"));// �ż������
		parm.setData("IN_CONDITION", p.getData("IN_CONDITION"));// ��Ժ���
		parm.setData("PG_OWNER", p.getData("PG_OWNER"));// ��ҳ������

		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		result = this.update("insertPatInfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ������ҳ��Ϣ
	 * 
	 * @param parm
	 *            TParm ������CASE_NO��MR_NO��OPT_USER,OPT_TERM;HOSP_ID
	 * @return TParm
	 */
	public TParm insertMRO(TParm parm) {
		TParm result = MRORecordTool.getInstance().insertMRO(parm);
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
		TParm result = MRORecordTool.getInstance().updateADMData(parm);
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
		TParm result = MRORecordTool.getInstance().updateMROPatInfo(p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸Ĳ�������Ժ����
	 * 
	 * @param roomCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateInRoom(String roomCode, String caseNo, TConnection conn) {
		String sql = "UPDATE MRO_RECORD SET IN_ROOM_NO = '" + roomCode
				+ "' WHERE CASE_NO='" + caseNo + "' AND IN_ROOM_NO IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �õ�ת�ƿ���
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm getTranDept(TParm parm) {
		// modified by wangqing 20180106
		TParm tranParm = new TParm();
		tranParm = ADMTransLogTool.getInstance().getTranHospFormro(parm);
		return tranParm;
	}

	/**
	 * �õ�����ҩ���������
	 * 
	 * @param mrNo
	 *            String
	 * @return TParm
	 */
	public TParm getDrugAllErgy(String mrNo) {
		TParm result = new TParm(
				this.getDBTool()
						.select(" SELECT DISTINCT A.DRUGORINGRD_CODE,"
								+ " B.ORDER_DESC,A.MR_NO "
								+ " FROM OPD_DRUGALLERGY A,SYS_FEE B"
								+ " WHERE A.DRUGORINGRD_CODE=B.ORDER_CODE(+) AND A.DRUG_TYPE='B' AND A.MR_NO='"
								+ mrNo + "'"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �õ���֢�໤��Ϣ
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getICUParm(String caseNo) {
		String sql = "SELECT TO_DATE(A.IN_DATE,'YYYY/MM/DD HH24:MI:SS') IN_DATE,A.OUT_DATE,A.IN_DEPT_CODE AS DEPT_CODE "
				+ " FROM ADM_TRANS_LOG A, SYS_DEPT B "
				+ " WHERE A.IN_DEPT_CODE=B.DEPT_CODE "
				+ " AND B.ICU_TYPE IS NOT NULL"
				+ " AND A.CASE_NO='"
				+ caseNo
				+ "' AND A.PSF_KIND IS NOT NULL  " + " ORDER BY A.IN_DATE ASC ";
		TParm result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
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
}
