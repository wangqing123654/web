package action.adm;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.adm.ADMAutoBillTool;
import jdo.adm.ADMChgTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.bil.BILTool;
import jdo.ibs.IBSBillmTool;
import jdo.ibs.IBSLumpWorkBatchNewTool;
import jdo.ibs.IBSLumpWorkBatchTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSOrdermTool;
import jdo.mro.MROLendTool;
import jdo.mro.MROQueueTool;
import jdo.mro.MRORecordTool;
import jdo.mro.MROTool;
import jdo.odi.OdiMainTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ���ת���� Action
 * </p>
 * 
 * <p>
 * Description: ���ת���� Action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author JiaoY
 * @version 1.0
 */
public class ADMWaitTransAction extends TAction {
	/**
	 * ��Ʊ���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @throws Throwable 
	 */
	public TParm onInSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		// ��ѯchgLog
		TParm occuFlg = new TParm();
		occuFlg.setRowData(parm);
		// ɾ��ת�Ʊ��¼
		TParm wait = new TParm();
		wait.setRowData(parm);
		// ���´�λ��
		TParm sysBed = new TParm();
		sysBed.setRowData(parm);
		// ������Ժ������Ϣ
		TParm admInp = new TParm();
		admInp.setRowData(parm);
		// ��ѯtransWait
		TParm transWait = new TParm();
		transWait.setRowData(parm);
		// ��ѯchgLog
		TParm chgLog = new TParm();
		chgLog.setRowData(parm);
		// ����translog
		TParm transLog = new TParm();
		// ��ѯ�������ڲ�����Ϣ
		TParm bed = ADMInpTool.getInstance().selectRoomInfo(
				parm.getValue("BED_NO"));
		// System.out.println("bed------------------w1----------------:"+bed);
		// ɾ��ת�Ƶ���¼
//		String[] ds = (String[]) wait.getData("UPDATE");
		// �жϸò����Ƿ���й����� OCCU_FLG��ǰ̨����ı�ʾ�����Ƿ����������
		if ("Y".equals(occuFlg.getData("OCCU_FLG"))) {
			// ������������� ��ôҪ�жϲ����Ƿ���ס��ָ���Ĵ�λ
			if ("Y".equals(occuFlg.getData("CHANGE_FLG"))) {
				// CHANGE_FLG=Y��ʾ��������ѡ����������λ ��ôҪ���ԭ�еİ�����Ϣ
				TParm clear = new TParm();
				clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
				result = SYSBedTool.getInstance().clearAllForadmin(occuFlg,
						conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		} else {
			// �������û�а�������סǰ�Ѵ˲���������λ��ԤԼ���ǰ���崲��סԺ�����Ŵ�λ��
			TParm clear = new TParm();
			clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
			result = SYSBedTool.getInstance().clearForadmin(clear, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// System.out.println("admInp------------------w13----------------:"+result);
		// ���´�λ��
		result = SYSBedTool.getInstance().upDate(sysBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		
		// ������Ժ������λ��
		admInp.setData("RED_SIGN", bed.getData("RED_SIGN", 0));
		admInp.setData("YELLOW_SIGN", bed.getData("YELLOW_SIGN", 0));
		// System.out.println("result---upDate(sysBed, conn);----------:"+result);
		result = ADMInpTool.getInstance().updateForWait(admInp, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// �޸Ĳ�����ҳ��Ŀǰ���ڿ���,����,�����ֶ�
		TParm mro = new TParm();
		mro.setData("OUT_DEPT", parm.getData("DEPT_CODE")); // ��ǰ���ڿ���
		mro.setData("OUT_STATION", parm.getData("STATION_CODE")); // ��ǰ���ڲ���
		mro.setData("OUT_ROOM_NO", bed.getValue("ROOM_CODE", 0)); // ��ǰ���ڲ���
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		mro.setData("CASE_NO", parm.getValue("CASE_NO"));
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// System.out.println("ADMWaitTransAction:" + mro);
		result = MROTool.getInstance().updateTransDept(mro, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		// �޸���ҳ����Ժ�����ֶ�
		result = MROTool.getInstance().updateInRoom(
				bed.getValue("ROOM_CODE", 0), parm.getValue("CASE_NO"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();// shibl 20130104 add
			return result;
		}
		// ��ѯtransWait
		TParm transrs = ADMWaitTransTool.getInstance().queryDate(transWait);
		if (transrs.getErrCode() < 0) {
			conn.rollback();
			conn.close();// shibl 20130104 add
			return result;
		}
		// ��ѯtransLOG
		TParm tranLogDept = ADMTransLogTool.getInstance().getTranDeptData(
				transWait);
		if (tranLogDept.getErrCode() < 0) {
			System.out.println("��ѯADMTransLogTool���¿������ݴ���");
			conn.rollback();
			conn.close();
			return result;
		}
		// ֮ǰ����ȡ��ת�Ʋ�������ɾ���ٲ��룩
		if (tranLogDept.getValue("PSF_KIND", 0).equals("CANCEL")
				|| tranLogDept.getValue("PSF_KIND", 0).equals("OUT")) {
			// ����translog
			transLog.setData("CASE_NO", transrs.getData("CASE_NO", 0));
			transLog.setData("MR_NO", transrs.getData("MR_NO", 0));
			transLog.setData("IPD_NO", transrs.getData("IPD_NO", 0));
			transLog.setData("IN_DATE", tranLogDept.getData("IN_DATE", 0));
			transLog.setData("OUT_DEPT_CODE", "");
			transLog.setData("OUT_STATION_CODE", "");
			transLog.setData("OUT_DATE", "");
			transLog.setData("IN_DEPT_CODE",
					transrs.getData("IN_DEPT_CODE", 0) == null ? "" : transrs
							.getData("IN_DEPT_CODE", 0));
			transLog.setData("IN_STATION_CODE", transrs.getData(
					"IN_STATION_CODE", 0) == null ? "" : transrs.getData(
					"IN_STATION_CODE", 0));
			transLog.setData("PSF_KIND", "");
			transLog.setData("OPT_USER", parm.getData("OPT_USER"));
			transLog.setData("OPT_TERM", parm.getData("OPT_TERM"));
			TParm cancelParm = new TParm();
			cancelParm.setData("CASE_NO", transrs.getData("CASE_NO", 0));
			cancelParm.setData("IN_DATE", tranLogDept.getData("IN_DATE", 0));
			result = ADMTransLogTool.getInstance().deleteAdmData(cancelParm,
					conn); // ɾ��ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			result = ADMTransLogTool.getInstance().insertDateForCancel(
					transLog, conn); // ����ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}

		} else {
			// ����translog
			transLog.setData("CASE_NO", transrs.getData("CASE_NO", 0));
			transLog.setData("MR_NO", transrs.getData("MR_NO", 0));
			transLog.setData("IPD_NO", transrs.getData("IPD_NO", 0));
			transLog.setData("OUT_DEPT_CODE", "");
			transLog.setData("OUT_STATION_CODE", "");
			transLog.setData("OUT_DATE", "");
			transLog.setData("IN_DEPT_CODE",
					transrs.getData("IN_DEPT_CODE", 0) == null ? "" : transrs
							.getData("IN_DEPT_CODE", 0));
			transLog.setData("IN_STATION_CODE", transrs.getData(
					"IN_STATION_CODE", 0) == null ? "" : transrs.getData(
					"IN_STATION_CODE", 0));
			transLog.setData("OPT_USER", parm.getData("OPT_USER"));
			transLog.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = ADMTransLogTool.getInstance().insertDate(transLog, conn); // ����ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// System.out.println("-------------result--------1---------------------"+result);
		// ����chgLog
		chgLog.setData("PSF_KIND", "INBD");
		TParm seqParm = new TParm();
		seqParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm seqMax = ADMChgTool.getInstance().ADMQuerySeq(seqParm);
		int SEQ = 0;
		if (seqMax.getErrCode() < 0) {
			SEQ = 0;
		} else {
			if (seqMax.getValue("SEQ_NO", 0).trim().length() > 0) {
				SEQ = seqMax.getInt("SEQ_NO", 0);
			}
		}
		chgLog.setData("SEQ_NO", SEQ);
		// =============pangben modify 20110617
		chgLog.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result = ADMChgTool.getInstance()
				.insertAdmChg(loadAdmChg(chgLog), conn); // �������chgLog
		// System.out.println("-------------result-2----------------------------"+result);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
       //-----------------shibl add 20140805-----------------------------------------
		
		String sql="DELETE FROM ADM_WAIT_TRANS WHERE CASE_NO='"+parm.getData("CASE_NO")+"'";
		
		// ɾ��adm_trans_Wait
		TParm badParm = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (badParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// ���� ODI_ORDER �г������� Bed_NO
		TParm changeOdiOrderBed = new TParm();
		changeOdiOrderBed.setData("CASE_NO", parm.getValue("CASE_NO"));
		changeOdiOrderBed.setData("BED_NO", parm.getValue("BED_NO"));
		result = OdiMainTool.getInstance()
				.modifBedNoUD(changeOdiOrderBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
        // ���Ϊͬһ���Ҳ�ͬ����ת�� wanglong add 20140728
        if (parm.getValue("OUT_DEPT_CODE").equals(parm.getValue("DEPT_CODE"))
                && !parm.getValue("OUT_STATION_CODE").equals(parm.getValue("STATION_CODE"))) {
            // wanglong add 20140728
            // ����ODI_ORDER����δͣ��ҽ����ͣ��ʱ�����ڵ�ǰʱ��ҽ���Ĳ����ʹ���
            TParm odiOrderParm = new TParm();
            odiOrderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
            odiOrderParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
            odiOrderParm.setData("BED_NO", parm.getValue("BED_NO"));
            result = OdiMainTool.getInstance().modifyOdiOrderStationAndBed(odiOrderParm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            // wanglong add 20140728
            // ����ODI_DSPNMδִ��ҽ���Ĳ����ʹ���
            TParm odiDspnmParm = new TParm();
            odiDspnmParm.setData("CASE_NO", parm.getValue("CASE_NO"));
            odiDspnmParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
            odiDspnmParm.setData("BED_NO", parm.getValue("BED_NO"));
            result = OdiMainTool.getInstance().modifyOdiDspnmStationAndBed(odiDspnmParm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
        }
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ת�Ʊ���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInOutSave(TParm parm) {
		TParm result = new TParm();
		TConnection conn = getConnection();
		result = ADMTool.getInstance().ADM_OUT_DEPT(parm, conn);
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
	 * ��Ժ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm outAdmSave(TParm parm) {
		// System.out.println("�ٴγ�Ժ��̨�Ӳ�" + parm);
		// ������Ժ������Ϣ
		TParm admInp = new TParm();
		// ���´�λ��
		TParm sysBed = new TParm();
		TConnection conn = getConnection();
		TParm result = new TParm();
		parm.setData("DATE", parm.getData("OUT_DATE"));
		//===zhangp 20130105 start
		TParm orderm = new TParm();
		
		if(parm.getValue("ADMS").length() == 0){
			orderm = IBSOrderdTool.getInstance().selectdataAll(parm);
			if (orderm.getErrCode() < 0) {
				err(orderm.getErrText());
				
				// modified by WangQing at 20170217 -start
				conn.rollback();
				conn.close();
				// modified by WangQing at 20170217 -end
				
				return orderm;
			}
		}else{
			String sql = 
				" SELECT   D.CASE_NO, D.REXP_CODE, ROUND (SUM (D.OWN_AMT), 2) AS OWN_AMT," +
				" SUM (D.TOT_AMT) AS AR_AMT, M.BED_NO" +
				" FROM IBS_ORDM M, IBS_ORDD D" +
				" WHERE M.CASE_NO = D.CASE_NO" +
				" AND M.CASE_NO_SEQ = D.CASE_NO_SEQ" +
				" AND M.MR_NO = '" + parm.getValue("MR_NO") + "'" +
				" AND D.CASE_NO = '" + parm.getValue("CASE_NO") + "'" +
				" AND M.IPD_NO = '" + parm.getValue("IPD_NO") + "'" +
				" GROUP BY D.CASE_NO, D.REXP_CODE, M.BED_NO" +
				" ORDER BY D.CASE_NO, D.REXP_CODE, M.BED_NO";
			orderm = new TParm(TJDODBTool.getInstance().select(sql));
			if (orderm.getErrCode() < 0) {
				err(orderm.getErrText());
				
				// modified by WangQing at 20170217 -start
				conn.rollback();
				conn.close();
				// modified by WangQing at 20170217 -end
				
				return orderm;
			}
		}
		
		if (orderm.getCount() > 0 && parm.getValue("ADMS").length() > 0) {
			// ��Ϊ��Ժδ�ɷ�״̬
			result = ADMTool.getInstance().updateBillStatus("2",
					parm.getValue("CASE_NO"), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();// shibl add 20130104 ��֤����ͬ��
				return result;
			}
		}
		
		//===zhangp 20130105 end
		TParm backBillParm = IBSBillmTool.getInstance().selBackBill(parm);
		if (backBillParm.getErrCode() < 0) {
			err(backBillParm.getErrText());
			
			// modified by WangQing at 20170217 -start
			conn.rollback();
			conn.close();
			// modified by WangQing at 20170217 -end
			
			return backBillParm;
		}
		
		if (orderm.getCount() <= 0) {//zhangp
			// ��Ϊ��Ժ���˵�״̬
			result = ADMTool.getInstance().updateBillStatus("1",
					parm.getValue("CASE_NO"), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();// shibl add 20130104 ��֤����ͬ��
				return result;
			}
		}
		
		//===zhangp 20130110 start
		//===caowl 20130122 start
		Timestamp date = StringTool.getTimestamp(new Date());
		if(parm.getData("BILL_DATE")==null || parm.getData("BILL_DATE").equals("")){
			parm.setData("BILL_DATE",date);
		}
		
		//===caowl 20130122 end
		TParm ordermm = IBSOrdermTool.getInstance().selectdate(parm);
		if(ordermm.getCount()<=0){
			// ��Ϊ��Ժ���˵�״̬
			result = ADMTool.getInstance().updateBillStatus("1",
					parm.getValue("CASE_NO"), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();// shibl add 20130104 ��֤����ͬ��
				return result;
			}
		}
		
		//===zhangp 20130110 end
		int backBillCount = backBillParm.getCount("CASE_NO");
		TParm admInpParm = new TParm();
		admInpParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm admParm = ADMInpTool.getInstance().selectall(admInpParm);
		if (admParm.getErrCode() < 0) {
			err(admParm.getErrText());
			conn.rollback();
			conn.close();// shibl add 20130104 ��֤����ͬ��
			return admParm;
		}
		
		Timestamp today = SystemTool.getInstance().getDate();
		int dayConut = StringTool.getDateDiffer(admParm.getTimestamp("IN_DATE",
				0), today);
		Timestamp yesterday = StringTool.rollDate(today, -1);
		String yesDayStr = StringTool.getString(yesterday, "yyyyMMdd");
		String todayStr = StringTool.getString(today, "yyyyMMdd");
		String countDaySql = " SELECT SUM(D.TOT_AMT) AS TOT_AMT "
				+ "   FROM IBS_ORDM M,IBS_ORDD D " + "  WHERE D.CASE_NO = '"
				+ parm.getData("CASE_NO") + "' "
				+ "    AND M.CASE_NO = D.CASE_NO "
				+ "    AND M.DATA_TYPE = '0' "
				+ "    AND D.BILL_DATE BETWEEN TO_DATE('" + yesDayStr
				+ "000000','YYYYMMDDHH24MISS') " + "                      AND TO_DATE('"
				+ todayStr + "235959','YYYYMMDDHH24MISS') ";
		TParm countDayParm = new TParm(TJDODBTool.getInstance().select(
				countDaySql));
		double autoFee = 0.00;
		if (countDayParm.getErrCode() < 0) {
			err(-1, countDayParm.getErrName() + countDayParm.getErrText());
			conn.rollback();
			conn.close();// shibl add 20130104 ��֤����ͬ��
			return countDayParm;
		}
		
		if (countDayParm.getCount() > 0)
			autoFee = countDayParm.getDouble("TOT_AMT", 0);
		
		//�����룬����������
		if (parm.getInt("IN_DAYS") <= 0 && backBillCount <= 0 && autoFee == 0) {
			//
			String todayFlg = "Y";   
			parm.setData("TODAY", todayFlg);
			//
			// ������λ����			
			result = ADMAutoBillTool.getInstance()
					.postAutoBillOfMen(parm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				System.out.println("result::::::"+result);
				return result;
			}
			 conn.commit();// shibl 20130104 modify
		}
		
    	//У��˲����Ƿ��ײͻ���====pangben 2015-6-18
		TParm admLumpworkParm=ADMInpTool.getInstance().onCheckLumWorkNew(parm.getValue("CASE_NO"));
		if (admLumpworkParm.getCount()>0) {
			//�ײͲ������������߼�
			String sql="SELECT A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG,A.LUMPWORK_RATE FROM ADM_INP A,IBS_ORDD B WHERE A.CASE_NO=B.CASE_NO" +
			" AND A.LUMPWORK_CODE IS NOT NULL AND B.INCLUDE_EXEC_FLG='N' AND A.CANCEL_FLG ='N' AND A.CASE_NO='"+parm.getValue("CASE_NO")+
			"' GROUP BY A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG,A.LUMPWORK_RATE";
			admParm=new TParm(TJDODBTool.getInstance().select(sql));
			if (admParm.getCount()<=0) {
			}else{
				result=IBSLumpWorkBatchNewTool.getInstance().onLumpWorkBatch(admParm.getRow(0), conn);
		    	if (result.getErrCode()<0) {
		    		conn.rollback();
		    		conn.close();
					return result;
				}
		    	conn.commit();	
			}
			
			//=====�ٴγ�Ժ����,�˷�ҽ��û������������������ pangben 2016-1-11
			TParm newBobyParm=admLumpworkParm.getParm("newBobyParm");
	    	if (null!=newBobyParm && newBobyParm.getValue("NEW_BORN_FLG",0).equals("Y")) {//��ǰ������Ӥ����ִ�в������߼�
			}else{
				//====pangben 2015-6-18 ����ײͲ������ɲ������
				result=ADMInpTool.getInstance().onSaveOutLumpworkDiffAmt(admLumpworkParm,parm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
	    	
	    	if (null!=result&&null!=result.getValue("DIFF_FLG")&&result.getValue("DIFF_FLG").equals("Y")) {
	    		//System.out.println("result::::"+result);
			}else{
				conn.commit();
			}
		}
		
		// ����ADM_INP
		admInp.setData("DS_DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admInp.setData("DS_STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admInp.setData("DS_DATE", parm.getData("OUT_DATE"));
		admInp.setData("CASE_NO", parm.getData("CASE_NO"));
		admInp.setData("OPT_USER", parm.getData("OPT_USER"));
		admInp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = ADMInpTool.getInstance().outAdmInp(admInp, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		
		// System.out.println("����adm_inp");
		// �崲
		sysBed.setData("CASE_NO", parm.getData("CASE_NO"));
		sysBed.setData("OPT_USER", parm.getData("OPT_USER"));
		sysBed.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = SYSBedTool.getInstance().clearForAdm(sysBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		
		// System.out.println("�崲");
		// ����ADM_CHG������̬��
		TParm adm_chg = new TParm();
		// ��ѯ���SEQ
		TParm seqQuery = new TParm();
		seqQuery.setData("CASE_NO", parm.getValue("CASE_NO"));
		TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(seqQuery);
		String seq = seqParm.getData("SEQ_NO", 0).toString();
		adm_chg.setData("CASE_NO", parm.getValue("CASE_NO"));
		adm_chg.setData("SEQ_NO", seq);
		adm_chg.setData("IPD_NO", parm.getValue("IPD_NO"));
		adm_chg.setData("MR_NO", parm.getValue("MR_NO"));
		adm_chg.setData("PSF_KIND", "DSCH"); // ��ʿ��Ժ�崲
		adm_chg.setData("PSF_HOSP", "");
		adm_chg.setData("CANCEL_FLG", "N");
		adm_chg.setData("CANCEL_DATE", "");
		adm_chg.setData("CANCEL_USER", "");
		adm_chg.setData("DEPT_CODE", parm.getValue("OUT_DEPT_CODE"));
		adm_chg.setData("STATION_CODE", parm.getValue("OUT_STATION_CODE"));
		adm_chg.setData("BED_NO", parm.getValue("BED_NO"));
		adm_chg.setData("VS_CODE_CODE", "");
		adm_chg.setData("ATTEND_DR_CODE", "");
		adm_chg.setData("DIRECTOR_DR_CODE", "");
		adm_chg.setData("OPT_USER", parm.getValue("OPT_USER"));
		adm_chg.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		// ==========pangben modify 20110617 start
		adm_chg.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(adm_chg, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();// shibl add 20130104 ��֤����ͬ��
			return result;
		}
		
		// ����ADM_TRANS_LOG
		TParm transLogParm = ADMTransLogTool.getInstance()
				.getTranDeptData(parm);
		TParm updatetransLogParm = new TParm();
		updatetransLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		updatetransLogParm.setData("IN_DATE", transLogParm.getValue("IN_DATE",
				0));
		updatetransLogParm.setData("OUT_DATE", StringTool.getTimestamp(parm
				.getValue("OUT_DATE").substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
		updatetransLogParm.setData("OUT_DEPT_CODE", parm
				.getValue("OUT_DEPT_CODE"));
		updatetransLogParm.setData("OUT_STATION_CODE", parm
				.getValue("OUT_STATION_CODE"));
		updatetransLogParm.setData("PSF_KIND", "OUT");
		updatetransLogParm.setData("DEPT_ATTR_FLG", parm
				.getValue("DEPT_ATTR_FLG"));
		updatetransLogParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		updatetransLogParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result = ADMTransLogTool.getInstance().updateAdm(updatetransLogParm,conn); // ����ADM_Trans_Log
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();// shibl add 20130104 ��֤����ͬ��
			return result;
		}
		
		// System.out.println("��̬��¼������������������");
		// System.out.println("���˵����"+parm);
		// ���ɳ�Ժ�˵�
		result = ADMInpTool.getInstance().insertIBSBiilData(parm, "Y", conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		
		// System.out.println("�ٴγ�Ժ���������������ɳ�Ժ�˵���������");
		// ��ѯ�������ڵĲ���
		TParm bedForRoom = new TParm();
		bedForRoom.setData("BED_NO", parm.getData("BED_NO"));
		TParm room = SYSBedTool.getInstance().queryAll(parm);
		String roomCode = room.getValue("ROOM_CODE", 0);
		// ���²�����ҳ��Ϣ
		TParm mro = new TParm();
		mro.setData("OUT_DEPT", parm.getData("OUT_DEPT_CODE"));
		mro.setData("OUT_STATION", parm.getData("OUT_STATION_CODE"));
		mro.setData("OUT_DATE", parm.getData("OUT_DATE"));
		mro.setData("OUT_ROOM_NO", roomCode);
		mro.setData("REAL_STAY_DAYS", parm.getData("IN_DAYS")); // סԺ����
		mro.setData("VS_NURSE_CODE", parm.getValue("VS_NURSE_CODE")); // ���λ�ʿ
		mro.setData("DIRECTOR_DR_CODE", parm.getValue("DIRECTOR_DR_CODE")); // ������
		mro.setData("ATTEND_DR_CODE", parm.getValue("ATTEND_DR_CODE")); // ����ҽʦ
		mro.setData("VS_DR_CODE", parm.getValue("VS_DR_CODE")); // ����ҽʦ
		mro.setData("CASE_NO", parm.getData("CASE_NO"));
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = MROTool.getInstance().updateTransDept(mro, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		
		// System.out.println("����MRO>>>>>>>");
		
		// add by wangbin 20140909 ��Ժ��ظ��������ı�Ĺ黹ʱ�� START
		this.updateMroQueueRtnDate(parm);
		// add by wangbin 20140909 ��Ժ��ظ��������ı�Ĺ黹ʱ�� END
		
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ��������ҽʦ����ʳ״�� ת��λ ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm changeDcBed(TParm parm) {
//		System.out.println("dsadad");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// ԭʼ����
		TParm oldParm = new TParm();
		oldParm = parm.getParm("OLD_DATA");
		// ׼�����µ�����
		TParm newParm = new TParm();
		newParm = parm.getParm("NEW_DATA");
		//==========   chenxi add 20130222= bigin====��������δ�ύǰ����������bug
		TParm sysbedParm = new TParm() ;
		sysbedParm.setRowData(newParm) ;
		if ("Y".equals(sysbedParm.getData("BED"))) {
			sysbedParm.setData("BED_NO", sysbedParm.getData("TRAN_BED"));
			result = SYSBedTool.getInstance().changeLockBed(sysbedParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			conn.commit();
		}
		//==========   chenxi add 20130222= end====��������δ�ύǰ����������bug
		// ��������ҽʦ����ʳ״�� ���㻤���� ����ADM_INP����
		TParm doc = new TParm();
		doc.setRowData(newParm);
		result = ADMInpTool.getInstance().updateForWaitPat(doc, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// ͬʱ������ҳ������ֶ�
		TParm mroParm = new TParm();
		mroParm.setData("IN_CONDITION", newParm.getValue("PATIENT_CONDITION")); // ��Ժ���
		mroParm.setData("ATTEND_DR_CODE", newParm.getValue("ATTEND_DR_CODE")); // ����ҽʦ
		mroParm.setData("VS_DR_CODE", newParm.getValue("VS_DR_CODE")); // ����ҽʦ
		mroParm.setData("DIRECTOR_DR_CODE", newParm
				.getValue("DIRECTOR_DR_CODE")); // ������
		mroParm.setData("CASE_NO", newParm.getValue("CASE_NO"));
		result = MRORecordTool.getInstance().updateForWaitPat(mroParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// ��ѯseq
		int seq = 0;
		TParm queryseqParm = new TParm();
		queryseqParm.setRowData(newParm);
		TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(queryseqParm);
		if (seqParm.getData("SEQ_NO", 0) == null
				|| "".equals(seqParm.getData("SEQ_NO", 0)))
			seq = 0;
		else
			seq = seqParm.getInt("SEQ_NO", 0) - 1; // ��Ϊ�����л�ȡ������Ѿ��Զ���1�ˣ�����Ҫ��ȥ1����ֹ����ĳ���++�������
		// ����ADM_CHG ����ҽʦ
		if (!(oldParm.getValue("VS_DR_CODE", 0).equals(newParm
				.getValue("VS_DR_CODE")))) {
			// ת��ҽʦ OUDR
			if (oldParm.getValue("VS_DR_CODE", 0).length() > 0) {
				seq++;
				TParm insertvsInDrData = new TParm();
				insertvsInDrData.setRowData(oldParm.getRow(0));
				insertvsInDrData.setData("PSF_KIND", "OUDR");
				insertvsInDrData
						.setData("BED_NO", oldParm.getData("BED_NO", 0));
				insertvsInDrData.setData("SEQ_NO", seq);
				// ============pangben modify 20110617 start
				insertvsInDrData.setData("REGION_CODE", parm
						.getValue("REGION_CODE"));
				TParm vsInDrParm = loadAdmChg(insertvsInDrData);
				result = ADMChgTool.getInstance()
						.insertAdmChg(vsInDrParm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// ת��ҽʦ INDR
			seq++;
			TParm insertvsInDrData = new TParm();
			insertvsInDrData.setRowData(newParm);
			insertvsInDrData.setData("PSF_KIND", "INDR");
			insertvsInDrData.setData("BED_NO", newParm.getData("BED_NO"));
			insertvsInDrData.setData("SEQ_NO", seq);
			// ============pangben modify 20110617 start
			insertvsInDrData.setData("REGION_CODE", parm
					.getValue("REGION_CODE"));
			TParm vsInDrParm = loadAdmChg(insertvsInDrData);
			result = ADMChgTool.getInstance().insertAdmChg(vsInDrParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// ����ADM_CHG ����ҽʦ(�ϼ�ҽʦ)
		if (!(oldParm.getValue("ATTEND_DR_CODE", 0).equals(newParm
				.getValue("ATTEND_DR_CODE")))) {
			// ת������ҽʦ(�ϼ�ҽʦ) OUDR1
			if (oldParm.getValue("VS_DR_CODE", 0).length() > 0) {
				seq++;
				TParm insertvsInDrData = new TParm();
				insertvsInDrData.setRowData(oldParm.getRow(0));
				insertvsInDrData.setData("PSF_KIND", "OUDR1");
				insertvsInDrData
						.setData("BED_NO", oldParm.getData("BED_NO", 0));
				insertvsInDrData.setData("SEQ_NO", seq);
				// ============pangben modify 20110617 start
				insertvsInDrData.setData("REGION_CODE", parm
						.getValue("REGION_CODE"));
				TParm vsInDrParm = loadAdmChg(insertvsInDrData);
				result = ADMChgTool.getInstance()
						.insertAdmChg(vsInDrParm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// ת������ҽʦ(�ϼ�ҽʦ) INDR1
			seq++;
			TParm insertvsInDrData = new TParm();
			insertvsInDrData.setRowData(newParm);
			insertvsInDrData.setData("PSF_KIND", "INDR1");
			insertvsInDrData.setData("BED_NO", newParm.getData("BED_NO"));
			insertvsInDrData.setData("SEQ_NO", seq);
			// ============pangben modify 20110617 start
			insertvsInDrData.setData("REGION_CODE", parm
					.getValue("REGION_CODE"));
			TParm vsInDrParm = loadAdmChg(insertvsInDrData);
			result = ADMChgTool.getInstance().insertAdmChg(vsInDrParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// ����ADM_CHG ����ҽʦ
		if (!(oldParm.getValue("DIRECTOR_DR_CODE", 0).equals(newParm
				.getValue("DIRECTOR_DR_CODE")))) {
			// ת������ҽʦ OUDR2
			if (oldParm.getValue("VS_DR_CODE", 0).length() > 0) {
				seq++;
				TParm insertvsInDrData = new TParm();
				insertvsInDrData.setRowData(oldParm.getRow(0));
				insertvsInDrData.setData("PSF_KIND", "OUDR2");
				insertvsInDrData
						.setData("BED_NO", oldParm.getData("BED_NO", 0));
				insertvsInDrData.setData("SEQ_NO", seq);
				// ============pangben modify 20110617 start
				insertvsInDrData.setData("REGION_CODE", parm
						.getValue("REGION_CODE"));
				TParm vsInDrParm = loadAdmChg(insertvsInDrData);
				result = ADMChgTool.getInstance()
						.insertAdmChg(vsInDrParm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// ת������ҽʦ INDR2
			seq++;
			TParm insertvsInDrData = new TParm();
			insertvsInDrData.setRowData(newParm);
			insertvsInDrData.setData("PSF_KIND", "INDR2");
			insertvsInDrData.setData("BED_NO", newParm.getData("BED_NO"));
			insertvsInDrData.setData("SEQ_NO", seq);
			// ============pangben modify 20110617 start
			insertvsInDrData.setData("REGION_CODE", parm
					.getValue("REGION_CODE"));
			TParm vsInDrParm = loadAdmChg(insertvsInDrData);
			result = ADMChgTool.getInstance().insertAdmChg(vsInDrParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// ת��
		TParm changeBed = new TParm();
		changeBed.setRowData(newParm);
		if ("Y".equals(changeBed.getData("BED"))) {
			// ת������
			TParm bed = new TParm();
			bed.setRowData(changeBed);
			bed.setData("APPT_FLG", "N");
			bed.setData("ALLO_FLG", "Y");
			bed.setData("BED_STATUS", "1");
			// �޸Ĵ�λ��
			bed.setData("BED_NO", changeBed.getData("TRAN_BED"));
			result = SYSBedTool.getInstance().changBed(bed, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// �޸�סԺ��Ϣ
			TParm inpParm = new TParm();
			// ��ѯ�������ڲ�����Ϣ
			TParm room = ADMInpTool.getInstance().selectRoomInfo(
					changeBed.getValue("TRAN_BED"));
			inpParm.setRowData(changeBed);
			inpParm.setData("BED_NO", changeBed.getData("TRAN_BED"));
			inpParm.setData("RED_SIGN", room.getData("RED_SIGN", 0));
			inpParm.setData("YELLOW_SIGN", room.getData("YELLOW_SIGN", 0));
			result = ADMInpTool.getInstance().updateForWait(inpParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// ���붯̬�� (ת����)
			seq = seq + 1;
			TParm chgOutParm = new TParm();
			chgOutParm.setRowData(changeBed);
			chgOutParm.setData("PSF_KIND", "OUBD");
			chgOutParm.setData("SEQ_NO", seq);
			// ========pangben modify 20110617
			chgOutParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			result = ADMChgTool.getInstance().insertChgDr(
					loadAdmChg(chgOutParm), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// ���붯̬�� (ת�봲)
			seq = seq + 1;
			TParm chgInParm = new TParm();
			chgInParm.setRowData(changeBed);
			chgInParm.setData("PSF_KIND", "INBD");
			chgInParm.setData("SEQ_NO", seq);
			chgInParm.setData("BED_NO", changeBed.getData("TRAN_BED"));
			// ========pangben modify 20110617
			chgInParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			result = ADMChgTool.getInstance().insertChgDr(
					loadAdmChg(chgInParm), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// ���� ODI_ORDER �г������� Bed_NO
			// �����͵Ľӿ�
			TParm changeOdiOrderBed = new TParm();
			changeOdiOrderBed.setRowData(changeBed);
			changeOdiOrderBed.setData("BED_NO", changeBed.getData("TRAN_BED"));
			result = OdiMainTool.getInstance().modifBedNoUD(changeOdiOrderBed,
					conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * Ϊadm_chg׼������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm loadAdmChg(TParm parm) {
		TParm result = new TParm();
		result.setData("CASE_NO", parm.getData("CASE_NO"));
		result.setData("SEQ_NO", parm.getData("SEQ_NO"));
		result.setData("IPD_NO", parm.getData("IPD_NO"));
		result.setData("MR_NO", parm.getData("MR_NO"));
		result.setData("CHG_DATE", parm.getData("DATE"));
		result.setData("PSF_KIND", parm.getData("PSF_KIND"));
		result.setData("PSF_HOSP", "");
		result.setData("CANCEL_FLG", "N");
		result.setData("CANCEL_DATE", "");
		result.setData("CANCEL_USER", "");
		result.setData("BED_NO", parm.getData("BED_NO") == null ? "" : parm
				.getData("BED_NO"));
		result.setData("DEPT_CODE", parm.getData("DEPT_CODE") == null ? ""
				: parm.getData("DEPT_CODE"));
		result.setData("STATION_CODE", parm.getData("STATION_CODE"));
		result.setData("VS_CODE_CODE", parm.getData("VS_DR_CODE") == null ? ""
				: parm.getData("VS_DR_CODE"));
		result.setData("ATTEND_DR_CODE",
				parm.getData("ATTEND_DR_CODE") == null ? "" : parm
						.getData("ATTEND_DR_CODE"));
		result.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE") == null ? "" : parm
						.getData("DIRECTOR_DR_CODE"));
		result.setData("OPT_USER", parm.getData("OPT_USER"));
		result.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// =========pangben modify 20110617
		result.setData("REGION_CODE", parm.getData("REGION_CODE"));
		return result;
	}

	/**
	 * סԺ���˵��ٻر���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm admReturn(TParm parm) {
		TParm result = new TParm();
		TParm actionParm = parm.getParm("DATA");
		TConnection conn = getConnection();
		// System.out.println("סԺ���˵��ٻر�����Ρ���������" + actionParm);
		result = BILTool.getInstance().admReturn(
				actionParm.getValue("CASE_NO"),
				actionParm.getValue("OPT_USER"),
				actionParm.getValue("OPT_TERM"), conn);
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
	 * ȡ��ת��Action
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdateTransAndLog(TParm parm) {
		TParm searchParm = new TParm();
		searchParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TConnection conn = getConnection();
		TParm searchResult = ADMWaitTransTool.getInstance().queryDate(
				searchParm);
		TParm result = new TParm();
		if (searchResult.getCount() > 0) {
			TParm deleteParm = new TParm();
			deleteParm.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			result = ADMWaitTransTool.getInstance().deleteIn(deleteParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			TParm insertInfo = new TParm();
			insertInfo.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			insertInfo.setData("MR_NO", searchResult.getData("MR_NO", 0));
			insertInfo.setData("IPD_NO", searchResult.getData("IPD_NO", 0));
			insertInfo.setData("DEPT_CODE", searchResult.getData(
					"OUT_DEPT_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_DEPT_CODE", 0));
			insertInfo.setData("STATION_CODE", searchResult.getData(
					"OUT_STATION_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_STATION_CODE", 0));
			insertInfo.setData("OPT_USER", parm.getData("OPT_USER"));
			insertInfo.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = ADMWaitTransTool.getInstance().saveForCancelTrans(
					insertInfo, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// ����ADM_TRANS_LOG
			TParm transdeptParm = ADMTransLogTool.getInstance()
					.getTranDeptData(parm);
			if (transdeptParm.getErrCode() < 0) {
				System.out.println("��ѯADMTransLogToolת�����ݴ���");
				conn.rollback();
				conn.close();
				return result;
			}
			// TParm updatetransLogParm = new TParm();
			// updatetransLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			// updatetransLogParm.setData("IN_DATE",
			// transLogParm.getValue("IN_DATE", 0));
			// updatetransLogParm.setData("OUT_DATE","");
			// updatetransLogParm.setData("OUT_DEPT_CODE","");
			// updatetransLogParm.setData("OUT_STATION_CODE","");
			// updatetransLogParm.setData("PSF_KIND", "");
			// updatetransLogParm.setData("DEPT_ATTR_FLG","");
			// updatetransLogParm.setData("OPT_USER",
			// parm.getValue("OPT_USER"));
			// updatetransLogParm.setData("OPT_TERM",
			// parm.getValue("OPT_TERM"));
			TParm transLogParm = new TParm();
			transLogParm.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			transLogParm
					.setData("IN_DATE", transdeptParm.getData("IN_DATE", 0));
			transLogParm.setData("MR_NO", searchResult.getData("MR_NO", 0));
			transLogParm.setData("IPD_NO", searchResult.getData("IPD_NO", 0));
			transLogParm.setData("OUT_DEPT_CODE", searchResult.getData(
					"IN_DEPT_CODE", 0) == null ? "" : searchResult.getData(
					"IN_DEPT_CODE", 0));
			transLogParm.setData("OUT_STATION_CODE", searchResult.getData(
					"IN_STATION_CODE", 0) == null ? "" : searchResult.getData(
					"IN_STATION_CODE", 0));
			transLogParm
					.setData("OUT_DATE", SystemTool.getInstance().getDate());
			transLogParm.setData("IN_DEPT_CODE", searchResult.getData(
					"OUT_DEPT_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_DEPT_CODE", 0));
			transLogParm.setData("IN_STATION_CODE", searchResult.getData(
					"OUT_STATION_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_STATION_CODE", 0));
			transLogParm.setData("OPT_USER", parm.getData("OPT_USER"));
			transLogParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			transLogParm.setData("PSF_KIND", "CANCEL");
			transLogParm.setData("DEPT_ATTR_FLG", "");
			result = ADMTransLogTool.getInstance()
					.updateAdm(transLogParm, conn); // ����ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			TParm queryMaxSeq = new TParm();
			queryMaxSeq.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			result = ADMChgTool.getInstance().ADMQuerySeq(queryMaxSeq);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			TParm admChgParm = new TParm();
			admChgParm.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			admChgParm.setData("SEQ_NO", result.getInt("SEQ_NO", 0));
			admChgParm.setData("IPD_NO", searchResult.getData("IPD_NO", 0));
			admChgParm.setData("MR_NO", searchResult.getData("MR_NO", 0));
			admChgParm.setData("DATE", parm.getData("DATE"));
			admChgParm.setData("PSF_KIND", "CANCEL");
			admChgParm.setData("OPT_USER", parm.getData("OPT_USER"));
			admChgParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			admChgParm.setData("DEPT_CODE", searchResult.getData(
					"IN_DEPT_CODE", 0));
			admChgParm.setData("STATION_CODE", searchResult.getData(
					"IN_STATION_CODE", 0));
			admChgParm.setData("REGION_CODE", "H01");
			result = ADMChgTool.getInstance().insertAdmChg(
					loadAdmChg(admChgParm), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			conn.commit();
			conn.close();
			return result;
		} else {
			TParm parm1 = new TParm();
			parm1.setErrCode(-1);
			// modified by WangQing at 2017/02/09  -start
			// ����connection��commit��close����������connection��Դ
			conn.commit();
			conn.close();
			// modified by WangQing at 2017/02/09  -end
			return parm1;
		}
	}
	
	/**
	 * ��Ժ��ظ��������ı�Ĺ黹ʱ��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private void updateMroQueueRtnDate(TParm parm) {
		TParm queryParm = new TParm();
		queryParm.setData("LEND_TYPE", "I");
		
		// ��ѯ�����黹ʱ��
		TParm result = MROLendTool.getInstance().selectdata(queryParm);
		
		if (result.getErrCode() < 0) {
			err("��ѯ�����ֵ����:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() <= 0) {
			err("�޶�Ӧ�Ľ����ֵ�����:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		// �������
		int intervalDay = result.getInt("LEND_DAY", 0);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, intervalDay);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		// ����Ӧ�黹����
		String returnDate = sdf.format(calendar.getTime());
		
		// Ӧ�黹����,���ճ�������趨
		queryParm.setData("RTN_DATE", returnDate);
		// Ӧ�����
		queryParm.setData("DUE_DATE", returnDate);
		queryParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		queryParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		queryParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		
		// �ظ��������Ĺ黹����
		result = MROQueueTool.getInstance().updateMroQueueRntDate(queryParm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
	}
	
	/**
	 * ȡ�����
	 * @param parm
	 * @return
	 * add by yangjj 20150526
	 */
	public TParm onCancleInDP(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		// ��ѯchgLog
		TParm occuFlg = new TParm();
		occuFlg.setRowData(parm);
		// ���´�λ��
		TParm sysBed = new TParm();
		sysBed.setRowData(parm);
		// �жϸò����Ƿ���й����� OCCU_FLG��ǰ̨����ı�ʾ�����Ƿ����������
		if ("Y".equals(occuFlg.getData("OCCU_FLG"))) {
			// ������������� ��ôҪ�жϲ����Ƿ���ס��ָ���Ĵ�λ
			if ("Y".equals(occuFlg.getData("CHANGE_FLG"))) {
				// CHANGE_FLG=Y��ʾ��������ѡ����������λ ��ôҪ���ԭ�еİ�����Ϣ
				TParm clear = new TParm();
				clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
				result = SYSBedTool.getInstance().clearAllForadmin(occuFlg,
						conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		} else {
			// �������û�а�������סǰ�Ѵ˲���������λ��ԤԼ���ǰ���崲��סԺ�����Ŵ�λ��
			TParm clear = new TParm();
			clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
			result = SYSBedTool.getInstance().clearForadmin(clear, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		String caseNo=sysBed.getValue("CASE_NO");
		String chgOUDPsql="SELECT DEPT_CODE,STATION_CODE FROM ADM_CHG WHERE CASE_NO='"+caseNo+"'" +
				" AND PSF_KIND='OUDP'  ORDER BY CHG_DATE DESC ";
		String chgINDPsql="SELECT DEPT_CODE,STATION_CODE FROM ADM_CHG WHERE CASE_NO='"+caseNo+"'" +
		" AND PSF_KIND='INBD'  ORDER BY CHG_DATE DESC ";
		TParm OUDPparm=new TParm(TJDODBTool.getInstance().select(chgOUDPsql));
		TParm INDPparm=new TParm(TJDODBTool.getInstance().select(chgINDPsql));
		if(INDPparm.getCount()<=0){
			conn.rollback();
			conn.close();
			result.setErr(-1, "δ��ѯ�����µĶ�̬��¼");
			return result;
		}
		TParm insertInfo = new TParm();
		insertInfo.setData("CASE_NO", parm.getData("CASE_NO"));
		insertInfo.setData("MR_NO", parm.getData("MR_NO"));
		insertInfo.setData("IPD_NO", parm.getData("IPD_NO"));
		insertInfo.setData("OUT_DEPT_CODE", OUDPparm.getData(
				"DEPT_CODE", 0) == null ? "" : OUDPparm.getData(
				"DEPT_CODE", 0));
		insertInfo.setData("OUT_STATION_CODE", OUDPparm.getData(
				"STATION_CODE", 0) == null ? "" : OUDPparm.getData(
				"STATION_CODE", 0));
		insertInfo.setData("IN_DEPT_CODE", INDPparm.getData(
				"DEPT_CODE", 0) == null ? "" : INDPparm.getData(
				"DEPT_CODE", 0));
		insertInfo.setData("IN_STATION_CODE", INDPparm.getData(
				"STATION_CODE", 0) == null ? "" : INDPparm.getData(
				"STATION_CODE", 0));
		insertInfo.setData("OPT_USER", parm.getData("OPT_USER"));
		insertInfo.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result=ADMWaitTransTool.getInstance().saveForInOutDept(insertInfo, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		String translogsql="DELETE FROM ADM_TRANS_LOG WHERE CASE_NO='"+caseNo+"' AND OUT_DEPT_CODE IS NULL";
		result=new TParm(TJDODBTool.getInstance().update(translogsql, conn));
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		TParm queryMaxSeq = new TParm();
		queryMaxSeq.setData("CASE_NO", parm.getData("CASE_NO"));
		result = ADMChgTool.getInstance().ADMQuerySeq(queryMaxSeq);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		TParm admChgParm = new TParm();
		admChgParm.setData("CASE_NO", parm.getData("CASE_NO"));
		admChgParm.setData("SEQ_NO", result.getInt("SEQ_NO", 0));
		admChgParm.setData("IPD_NO", parm.getData("IPD_NO"));
		admChgParm.setData("MR_NO", parm.getData("MR_NO"));
		admChgParm.setData("DATE", SystemTool.getInstance().getDate());
		admChgParm.setData("PSF_KIND", "CANCELINDP");
		admChgParm.setData("OPT_USER", parm.getData("OPT_USER"));
		admChgParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		admChgParm.setData("DEPT_CODE", INDPparm.getData(
				"DEPT_CODE", 0) == null ? "" : INDPparm.getData(
						"DEPT_CODE", 0));
		admChgParm.setData("STATION_CODE",INDPparm.getData(
				"STATION_CODE", 0) == null ? "" : INDPparm.getData(
						"STATION_CODE", 0));
		admChgParm.setData("REGION_CODE", "H01");
		result = ADMChgTool.getInstance().insertAdmChg(
				loadAdmChg(admChgParm), conn);
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