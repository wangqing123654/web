package jdo.adm;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;
import com.dongyang.jdo.TJDODBTool;

import jdo.mem.MEMTool;
import jdo.mro.MROTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import jdo.mro.MRORecordTool;
import jdo.mro.MROQueueTool;

/**
 * <p>
 * Title: ADM ҵ��tool
 * </p>
 *
 * <p>
 * Description: ADM ҵ��tool
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
 * @author JiaoY 2009.05.14
 * @version 1.0
 */
public class ADMTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static ADMTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return SchWeekTool
	 */
	public static ADMTool getInstance() {
		if (instanceObject == null)
			instanceObject = new ADMTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public ADMTool() {
		setModuleName("");
		onInit();
	}

	/**
	 * ����
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm ADM_OUT_DEPT(TParm parm, TConnection conn) {
		TParm result = new TParm();
		// ���Ʊ���
		result = this.ADM_OUT_DEPT_SAVE(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setData("CHECK", "T");
			return result;
		}
		// �޸Ĳ�����ҳ��ת�ƿ����ֶ�
		TParm mro = new TParm();
		// mro.setData("TRANS_DEPT",parm.getData("IN_DEPT_CODE"));
		mro.setData("OUT_DEPT", parm.getData("IN_DEPT_CODE"));// ��ǰ���ڿ���
		mro.setData("OUT_STATION", parm.getData("IN_STATION_CODE"));// ��ǰ���ڲ���
		mro.setData("OUT_ROOM_NO", "");// ��ǰ���ڲ���(��Ϊ��֪��ת�뵽�ĸ����������Բ������޸�Ϊ��)
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		mro.setData("CASE_NO", parm.getData("CASE_NO"));
		result = MROTool.getInstance().updateTransDept(mro, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setData("CHECK", "T");
			return result;
		}
		result.setData("CHECK", "T");
		return result;
	}

	/**
	 * ���Ʊ���
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm ADM_OUT_DEPT_SAVE(TParm parm, TConnection conn) {
		TParm result = new TParm();

		// ������Ժ������Ϣ
		TParm admInp = new TParm();
		// �崲
		TParm clearBed = new TParm();
		// ����ת�Ʊ��¼
		TParm tranWait = new TParm();
		
		//���벡����̬��   ����
		TParm admChg0 = new TParm();
		// ���벡����̬�� ����
		TParm admChg1 = new TParm();
		// ���벡����̬�� ���
		TParm admChg2 = new TParm();

		// ����ADM_INP
		admInp.setData("DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admInp.setData("STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admInp.setData("CASE_NO", parm.getData("CASE_NO"));
		admInp.setData("OPT_USER", parm.getData("OPT_USER"));
		admInp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = ADMInpTool.getInstance().updateForOutDept(admInp, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �봲
		clearBed.setData("CASE_NO", parm.getData("CASE_NO"));
		result = SYSBedTool.getInstance().clearAllForadmin(clearBed, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
				
		// ����ADM_CHG(������̬��)
		TParm seqMax = new TParm();
		seqMax.setData("CASE_NO", parm.getData("CASE_NO"));
		// ��ȡ����ŵ�����
		TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(seqMax);
		String seq = seqParm.getData("SEQ_NO", 0).toString();
		
		//����ADM_CHG(������̬��) ����
		admChg0.setData("CASE_NO", parm.getData("CASE_NO"));
		admChg0.setData("SEQ_NO", seq);
		admChg0.setData("IPD_NO", parm.getData("IPD_NO"));
		admChg0.setData("MR_NO", parm.getData("MR_NO"));
		admChg0.setData("PSF_KIND", "OUBD");// ת����
		admChg0.setData("PSF_HOSP", "");
		admChg0.setData("CANCEL_FLG", "N");
		admChg0.setData("CANCEL_DATE", "");
		admChg0.setData("CANCEL_USER", "");
		admChg0.setData("DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admChg0.setData("STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admChg0.setData("BED_NO", parm.getData("BED_NO"));
		admChg0.setData("VS_CODE_CODE", "");
		admChg0.setData("ATTEND_DR_CODE", "");
		admChg0.setData("DIRECTOR_DR_CODE", "");
		admChg0.setData("OPT_USER", parm.getData("OPT_USER"));
		admChg0.setData("OPT_TERM", parm.getData("OPT_TERM"));
		admChg0.setData("REGION_CODE", parm.getData("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(admChg0, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		
		// ������Ƽ�¼
		seq = (Integer.parseInt(seq) + 1) + "";
		admChg1.setData("CASE_NO", parm.getData("CASE_NO"));
		admChg1.setData("SEQ_NO", seq);
		admChg1.setData("IPD_NO", parm.getData("IPD_NO"));
		admChg1.setData("MR_NO", parm.getData("MR_NO"));
		admChg1.setData("PSF_KIND", "OUDP");// ת����
		admChg1.setData("PSF_HOSP", "");
		admChg1.setData("CANCEL_FLG", "N");
		admChg1.setData("CANCEL_DATE", "");
		admChg1.setData("CANCEL_USER", "");
		admChg1.setData("DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admChg1.setData("STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admChg1.setData("BED_NO", parm.getData("BED_NO"));
		admChg1.setData("VS_CODE_CODE", "");
		admChg1.setData("ATTEND_DR_CODE", "");
		admChg1.setData("DIRECTOR_DR_CODE", "");
		admChg1.setData("OPT_USER", parm.getData("OPT_USER"));
		admChg1.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// ===========pangben modify 20110617 start
		admChg1.setData("REGION_CODE", parm.getData("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(admChg1, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���� ��Ƽ�¼
		// ���»�ȡ����
		seq = (Integer.parseInt(seq) + 1) + "";
		admChg2.setData("CASE_NO", parm.getData("CASE_NO"));
		admChg2.setData("SEQ_NO", seq);
		admChg2.setData("IPD_NO", parm.getData("IPD_NO"));
		admChg2.setData("MR_NO", parm.getData("MR_NO"));
		admChg2.setData("PSF_KIND", "INDP");// ת����
		admChg2.setData("PSF_HOSP", "");
		admChg2.setData("CANCEL_FLG", "N");
		admChg2.setData("CANCEL_DATE", "");
		admChg2.setData("CANCEL_USER", "");
		admChg2.setData("DEPT_CODE", parm.getData("IN_DEPT_CODE"));
		admChg2.setData("STATION_CODE", parm.getData("IN_STATION_CODE"));
		admChg2.setData("BED_NO", "");
		admChg2.setData("VS_CODE_CODE", "");
		admChg2.setData("ATTEND_DR_CODE", "");
		admChg2.setData("DIRECTOR_DR_CODE", "");
		admChg2.setData("OPT_USER", parm.getData("OPT_USER"));
		admChg2.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// ===========pangben modify 20110617 start
		admChg2.setData("REGION_CODE", parm.getData("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(admChg2, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ����ADM_TRANS_LOG
		TParm transLogParm = ADMTransLogTool.getInstance()
				.getTranDeptData(parm);
		TParm updatetransLogParm = new TParm();
		updatetransLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		updatetransLogParm.setData("IN_DATE",
				transLogParm.getValue("IN_DATE", 0));
		updatetransLogParm.setData("OUT_DATE", StringTool.getTimestamp(parm
				.getValue("OUT_DATE").substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
		updatetransLogParm.setData("OUT_DEPT_CODE", parm.getValue("IN_DEPT_CODE"));
		updatetransLogParm.setData("OUT_STATION_CODE", parm.getValue("IN_STATION_CODE"));
		updatetransLogParm.setData("PSF_KIND", "OUDP");
		updatetransLogParm.setData("DEPT_ATTR_FLG", parm.getValue("DEPT_ATTR_FLG"));
		updatetransLogParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		updatetransLogParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result = ADMTransLogTool.getInstance().updateAdm(updatetransLogParm,
				conn); // ����ADM_Trans_Log
		if (result.getErrCode() < 0) {
			return result;
		}
		// ����ADM_TRANSWAIT
		tranWait.setRowData(parm);
		result = ADMWaitTransTool.getInstance()
				.saveForInOutDept(tranWait, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �����н��˵�
		result = ADMInpTool.getInstance().insertIBSBiilData(parm, "N", conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ȡ��סԺ
	 *
	 * @param parm
	 *            TParm ���� case_no;PSF_KIND
	 *            ;OPT_USER;OPT_TERM;PSF_HOSP;CANCEL_FLG;CANCEL_DATE;CANCEL_USER
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm ADM_CANCEL_INP(TParm parm, TConnection conn) {
		TParm clearBed = new TParm();
		clearBed.setData("CASE_NO", parm.getValue("CASE_NO"));
		clearBed.setData("OPT_USER", parm.getValue("OPT_USER"));
		clearBed.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		TParm result = new TParm();
		// ����ADM_INP
		TParm inpData = new TParm();
		inpData.setRowData(parm);
		result = ADMInpTool.getInstance().updateForCancel(inpData, conn);
		if (result.getErrCode() < 0) {
			return result;
		}		
		//�����ײ�������ʱ�̺���ϸҽ�����ײ�ʹ��״̬used_flg--xiongwg20150703
		
       
       	if (null!= parm.getParm("parmL")&&null!=parm.getParm("parmL").getValue("CASE_NO") &&
       			parm.getParm("parmL").getValue("CASE_NO").length()>0) {
       		TParm memParm = parm.getParm("parmL");
       		memParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			memParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
	       	result = MEMTool.getInstance().upMemUsedFlg(memParm, conn);
	       	if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
		}
		// ����ADM_CHG
		TParm chgData = new TParm();
		chgData.setRowData(parm);
		// =========pangben modify 20110617 start ����������
		chgData.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result = ADMChgTool.getInstance().insertChg(chgData, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ɾ��ADM_WAIT_TRANS
		TParm transData = new TParm();
		transData.setRowData(parm);
		result = ADMWaitTransTool.getInstance().deleteIn(transData, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �����λ��Ϣ
		result = SYSBedTool.getInstance().clearForAdm(clearBed, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���ת����Ϣ
		result = ADMTransLogTool.getInstance().deleteAdmData(clearBed, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ɾ��������ҳ��Ϣ
		result = MRORecordTool.getInstance().deleteMRO(
				parm.getValue("CASE_NO"), conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ȡ����CASE_NO�Ľ��Ĳ���
		TParm queueParm = new TParm();
		queueParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		queueParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		queueParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		result = MROQueueTool.getInstance().cancelQueueByCASE_NO(queueParm,
				conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ���봲�ռ�¼�� ���� CASE_NO,MR_NO,IPD_NO,DEPT_CODE,STATION_CODE,BED_NO,VS_DR_CODE,
	 * NURSING_CLASS,PATIENT_CONDITITION
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	// public TParm INSERT_ADM_DAILY_REC(TParm parm,TConnection conn){
	// TParm result = new TParm();
	// //����ǰɾ���������еļ�¼
	// TParm checkRec = new TParm();
	// checkRec.setRowData(parm);
	// result = ADMDailyRecTool.getInstance().delDailRec(checkRec,conn);
	// if(result.getErrCode()<0){
	// conn.close();
	// return result ;
	// }
	// //�����¼��
	// TParm insertRec = new TParm();
	// insertRec.setRowData(parm);
	// result = ADMChgTool.getInstance().MROQueryChgLog(parm);
	// if (result.getErrCode() < 0) {
	// conn.close();
	// return result;
	// }
	// return result;
	// }
	/**
	 * ��CASE_NO��ѯסԺ��
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm QUERY_ADM_DAYS(TParm parm) {
		TParm result = new TParm();
		TParm data = ADMInpTool.getInstance().selectall(parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		result.setData("ADM_DAYS", data.getData("ADM_DAYS", 0));
		return result;
	}

	/**
	 * ����CASE_NO��ѯһ��סԺ������Ϣ
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getADM_INFO(TParm parm) {
		TParm result = ADMInpTool.getInstance().selectall(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����CASE_NO��øò�����ʵ��סԺ����
	 *
	 * @param CASE_NO
	 *            String
	 * @return int
	 */
	public int getAdmDays(String CASE_NO) {
		int days = 0;
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = ADMInpTool.getInstance().selectall(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return 0;
		}
		// �޴˲�����Ϣ ����0
		if (result.getCount("CASE_NO") <= 0) {
			return 0;
		}
		// ע�� סԺ�������벻��� ��Ժ���첻��סԺ��
		// ��������Ѿ���Ժ�ó�Ժ����-��Ժ����
		if (result.getData("DS_DATE", 0) != null) {
			days = StringTool.getDateDiffer(StringTool.getTimestampDate(result
					.getTimestamp("DS_DATE", 0)), StringTool
					.getTimestampDate(result.getTimestamp("IN_DATE", 0)));
			days = days == 0 ? 1 : days;
		}
		// ���������Ժ ��ǰ����-��Ժ����
		else {
			days = StringTool.getDateDiffer(StringTool
					.getTimestampDate(SystemTool.getInstance().getDate()),
					StringTool.getTimestampDate(result.getTimestamp("IN_DATE",
							0)));
			days = days == 0 ? 1 : days;
		}
		return days;
	}

	/**
	 * �޸Ĳ������ ���� ���� CASE_NO,WEIGHT,HEIGHT,OPT_USER,OPT_ITEM
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm UPDATE_HIGH_WEIGHT(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "�����쳣!");
			return result;
		}
		result = ADMInpTool.getInstance().upDateWeightHigh(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		return result;
	}

	/**
	 * ����Ƿ�����ȡ��סԺ TRUE ����ȡ�� false ������
	 *
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean checkCancelOutInp(TParm parm) {
		boolean check = true;
		// ����Ƿ��������
		// TParm checkBill = new TParm();
		// checkBill.setRowData(parm);
		// check=IBSOrdermTool.getInstance().existFee(checkBill);
		// if(!check){//��������
		// return false��
		// }
		// ��˲����Ƿ��Ѿ���ס������
		TParm checkBed = new TParm();
		checkBed.setRowData(parm);
		TParm result = SYSBedTool.getInstance().checkInBed(checkBed);
		int count = result.getCount("BED_STATUS");
		if (count == -1 || count == 0)
			return true;
		// ����Ѿ���ס��������ô������ȡ��סԺ ��Ϊһ����ס�����Ͼ�Ӧ���ڳ�Ժʱ������λ����
		for (int i = 0; i < count; i++) {
			if (result.getData("BED_STATUS", i) == null
					|| "".equals(result.getData("BED_STATUS", i))
					|| "0".equals(result.getData("BED_STATUS", i))) {
				check = true;
			} else {
				check = false;
				break;
			}
		}

		return check;
	}

	/**
	 * סԺ�Ǽ��޸�
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataAdmInp(TParm parm, TConnection conn) {
		TParm result = new TParm();
		// �޸Ĵ�λ��
		if ("Y".equals(parm.getData("UPDATE_BED"))) {
			// ɾ����ת��
			TParm waitDelParm = new TParm();
			waitDelParm.setRowData(parm);
			result = ADMWaitTransTool.getInstance().deleteIn(waitDelParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}

			// �����ת������
			TParm waitParm = new TParm();
			waitParm.setRowData(parm);
			result = ADMWaitTransTool.getInstance().saveForInp(waitParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// �崲
			TParm clearBed = new TParm();
			clearBed.setRowData(parm);
			result = SYSBedTool.getInstance().clearAllForadmin(clearBed, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// ���´�λ��
			TParm bedParm = new TParm();
			bedParm.setRowData(parm);

			if (!bedParm.getData("BED_NO").equals("")) {
				bedParm.setData("APPT_FLG", "Y");
				bedParm.setData("ALLO_FLG", "Y");
				bedParm.setData("BED_STATUS", "0");
				result = SYSBedTool.getInstance().upDate(bedParm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		// �޸Ļ�ɫ���䣬��ɫ����,��Ժ���ڣ�סԺ����,��λ��
		TParm updata = new TParm();
		updata.setRowData(parm);
		result = ADMInpTool.getInstance().updateForAdmInp(updata, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ����Ӥ����CASE_NO
	 *
	 * @param parm
	 *            TParm ������� CASE_NO(Ӥ��CASE_NO)
	 * @return TParm ����ĸ�׵�CASE_NO��IPD_NO��MR_NO��PAT_NAME
	 */
	public TParm getMotherInfo(TParm parm) {
		String sql = "SELECT A.CASE_NO, A.IPD_NO, A.MR_NO, B.PAT_NAME "
				+ " FROM ADM_INP A, SYS_PATINFO B "
				+ " WHERE CASE_NO = (SELECT M_CASE_NO " + " FROM ADM_INP "
				+ " WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "') "
				+ " AND A.MR_NO = B.MR_NO";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����CASE_NO��ѯĳһ���˵Ķ�̬��¼��MRO������ҳʹ�ã�
	 *
	 * @param parm
	 *            TParm ���������CASE_NO
	 * @return TParm
	 */
	public TParm queryChgForMRO(TParm parm) {
		TParm result = ADMChgTool.getInstance().queryChgForMRO(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����CASE_NO��ѯĳһ���������������Ϣ��������ҳʹ�ã�
	 *
	 * @param parm
	 *            TParm ���������CASE_NO
	 * @return TParm
	 */
	public TParm queryDiagForMro(TParm parm) {
		TParm result = ADMDiagTool.getInstance().queryDiagForMro(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�adm_inp�������
	 *
	 * @param parm
	 *            TParm �����б� MAINDIAG:������� ����Ժ�����>��Ժ�����>�ż�������ϣ� CASE_NO:�������
	 *            OPT_USER:����Ա OPT_TERM:�����ն�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateNewDaily(TParm parm, TConnection conn) {
		TParm result = ADMInpTool.getInstance().updateNewDaily(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ���ȫ�ֶ�
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryDailyData(TParm parm) {
		TParm result = ADMDiagTool.getInstance().queryData(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�ҽ���ܽ��
	 *
	 * @param TOTAL
	 *            String ��� ������λС��
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTOTAL_AMT(String TOTAL, String CASE_NO, TConnection conn) {
		String sql = "UPDATE ADM_INP SET TOTAL_AMT='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
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
	 * �޸�Ԥ�����ֶ�
	 *
	 * @param TOTAL
	 *            String ��� ������λС��
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTOTAL_BILPAY(String TOTAL, String CASE_NO,
			TConnection conn) {
		String sql = "UPDATE ADM_INP SET TOTAL_BILPAY='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
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
	 * �޸ġ�Ŀǰ���ֶ�
	 *
	 * @param TOTAL
	 *            String ��� ������λС��
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateCUR_AMT(String TOTAL, String CASE_NO, TConnection conn) {
		String sql = "UPDATE ADM_INP SET CUR_AMT='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
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
	 * �޸ġ���ɫͨ�����ֶ�
	 *
	 * @param TOTAL
	 *            String ��� ������λС��
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateGREENPATH_VALUE(String TOTAL, String CASE_NO,
			TConnection conn) {
		String sql = "UPDATE ADM_INP SET GREENPATH_VALUE='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
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
	 * �޸Ĳ���״̬
	 *
	 * @param status
	 *            String ����״̬: 1 ��Ժ���˵� 2 ��Ժδ�ɷ� 3 ҽ������� 4 ��Ժ�ѽ���
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillStatus(String status, String CASE_NO,
			TConnection conn) {
		String sql = "UPDATE ADM_INP SET BILL_STATUS='" + status
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
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
	 * �޸�ֹͣ���ñ��
	 *
	 * @param FLG
	 *            String ��Y��ֹͣ���ã�N����ֹͣ���ã�
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm updateStopBillFlg(String FLG, String CASE_NO) {
		String sql = "UPDATE ADM_INP SET STOP_BILL_FLG='" + FLG
				+ "' WHERE CASE_NO='" + CASE_NO + "' OR M_CASE_NO = '" + CASE_NO + "' ";
//		System.out.println("�޸�ֹͣ���ñ��" + sql);
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * У��ֹͣ����
	 *
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm checkStopFee(String caseNo) { 
		//====pangben 2015-4-29 ���ֹͣ���۹ܿ�
		TParm parm = new TParm(TJDODBTool.getInstance().select("SELECT STOP_BILL_FLG FROM ODI_SYSPARM"));
		if (parm.getValue("STOP_BILL_FLG",0).equals("N")) {
			return new TParm();
		}
		TParm result = new TParm();
		TParm inSelADMAllData = new TParm();
		inSelADMAllData.setData("CASE_NO", caseNo);
		TParm selADMAllData = ADMInpTool.getInstance().selectall(
				inSelADMAllData);
//		System.out.println("ֹͣ����ADM���ݡ���������" + selADMAllData);
		double redSign = selADMAllData.getData("RED_SIGN", 0) == null ? 0.00
				: selADMAllData.getDouble("RED_SIGN", 0); // ��ɫ����
		double curAmt = selADMAllData.getData("CUR_AMT", 0) == null ? 0.00
				: selADMAllData.getDouble("CUR_AMT", 0); // Ŀǰ���
		double greenPath = selADMAllData.getData("GREENPATH_VALUE", 0) == null ? 0.00
				: selADMAllData.getDouble("GREENPATH_VALUE", 0); // ��ɫͨ��
		if (selADMAllData.getValue("NEW_BORN_FLG",0).equals("Y")) {//������У��ĸ�׵Ľ���Ƿ���㣬������㲻ִ��ֹͣ���۲���
			String sql="SELECT CUR_AMT,CASE_NO,DS_DATE FROM ADM_INP WHERE NEW_BORN_FLG<>'Y' AND IPD_NO='"+
			selADMAllData.getValue("IPD_NO",0)+"' ORDER BY DS_DATE DESC";
			TParm admParm=new TParm(TJDODBTool.getInstance().select(sql));
			TParm billParm=null;
			double mumCurAmt=0.00;
			for (int i = 0; i < admParm.getCount(); i++) {
				if (null==admParm.getValue("DS_DATE",i)
						||admParm.getValue("DS_DATE",i).length()<=0) {//û�г�Ժ
					mumCurAmt=admParm.getDouble("CUR_AMT", i);//��ѯ��ǰ���
					break;
				}else{
					//û�д�Ʊ�Ĳ���
					sql="SELECT CASE_NO FROM IBS_BILLM WHERE CASE_NO='"+admParm.getValue("CASE_NO",i)+"' AND RECEIPT_NO IS NULL";
					billParm=new TParm(TJDODBTool.getInstance().select(sql));
					if (billParm.getCount()>0) {
						mumCurAmt=admParm.getDouble("CUR_AMT", i);//��ѯ��ǰ���
						break;
					}
				}
			}
			if (mumCurAmt+greenPath + curAmt <= redSign){//===pangben 2014-4-15��ɫ����ֹͣ���� ĸ�׵ĵ�ǰ���+�˲����ĵ�ǰ���
	             result = this.updateStopBillFlg("Y", caseNo); // ����ֹͣ����ע��
	        }else
				result = this.updateStopBillFlg("N", caseNo); // ����ֹͣ����ע��
		}else{
			if (greenPath + curAmt <= redSign){//===pangben 2014-4-15��ɫ����ֹͣ����
	             result = this.updateStopBillFlg("Y", caseNo); // ����ֹͣ����ע��
	        }else
				result = this.updateStopBillFlg("N", caseNo); // ����ֹͣ����ע��
		}

		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ʿִ�и����������� ���¡�����ȼ����ֶ�(סԺ��ʿվʹ��)
	 *
	 * @param parm
	 *            TParm ������NURSING_CLASS:����ȼ� CASE_NO:�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateNURSING_CLASS(TParm parm, TConnection conn) {
		TParm result = ADMInpTool.getInstance().updateNURSING_CLASS(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ʿִ�и����������� ���¡�����״̬���ֶ�(סԺ��ʿվʹ��)
	 *
	 * @param parm
	 *            TParm ������PATIENT_STATUS:����ȼ� CASE_NO:�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updatePATIENT_STATUS(TParm parm, TConnection conn) {
		TParm result = ADMInpTool.getInstance()
				.updatePATIENT_STATUS(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���Ƹ���ADM_INP��bill_date
	 *
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillDate(String CASE_NO, TConnection conn) {
		String sql = "UPDATE ADM_INP SET BILL_DATE= SYSDATE WHERE CASE_NO='"
				+ CASE_NO + "'";
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
	 * ���Ƹ���ADM_INP��charge_date
	 *
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
        public TParm updateChargeDate(String CASE_NO, TConnection conn) {
                String sql = "UPDATE ADM_INP SET CHARGE_DATE=SYSDATE WHERE CASE_NO='"
                                + CASE_NO + "'";
                TParm result = new TParm();
                result.setData(TJDODBTool.getInstance().update(sql, conn));
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                        return result;
                }
                return result;
	}
}
