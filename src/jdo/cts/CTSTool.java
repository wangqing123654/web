package jdo.cts;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ��������������
 * </p>
 * 
 * <p>
 * Description: ��������������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp
 * @version 1.0
 */
public class CTSTool extends TJDOTool {

	/**
	 * ������
	 */
	public CTSTool() {
		setModuleName("cts\\CTSModule.x");
		onInit();
	}
	

	/**
	 * ʵ��
	 */
	private static CTSTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PESTool
	 */
	public static CTSTool getInstance() {
		if (instanceObject == null)
			instanceObject = new CTSTool();
		return instanceObject;
	}

	/**
	 * �õ����׺�
	 * 
	 * @return String
	 */
	public String getWashNo() {
		return SystemTool.getInstance().getNo("ALL", "INV", "WASH_NO",
				"No");
	}
	
	/**
	 * �õ��ּ��׺�
	 * 
	 * @return String
	 */
	public String getWashOutNo() {
		return SystemTool.getInstance().getNo("ALL", "INV", "WASH_NO",
				"outNo");
	}

	/**
	 * ��ѯ�·�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectCloth(TParm parm) {
		TParm result = query("selectCloth", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ����inv_stockdd���е�state
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateStockDD(TParm parm) {
		TParm result = update("updateStockDD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ���cts_ant
	 * @param parm
	 * @return
	 */
	public TParm insertCTSANT(TParm parm) {
		TParm result = update("insertCTSANT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����cts_ant
	 * @param parm
	 * @return
	 */
	public TParm updateCTSANT(TParm parm) {
		TParm result = update("updateCTSANT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ��cts_ant
	 * @param parm
	 * @return
	 */
	public TParm deleteCTSANT(TParm parm, TConnection connection) {
		TParm result = update("deleteCTSANT", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ���cts_ant_list
	 * @param parm
	 * @return
	 */
	public TParm insertCTSANTLIST(TParm parm) {
		TParm result = update("insertCTSANTLIST", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����cts_ant_list
	 * @param parm
	 * @return
	 */
	public TParm updateCTSANTLIST(TParm parm) {
		TParm result = update("updateCTSANTLIST", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ɾ��cts_ant_list
	 * @param parm
	 * @return
	 */
	public TParm deleteCTSANTLIST(TParm parm, TConnection connection) {
		TParm result = update("deleteCTSANTLIST", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

	/**
	 * ��rx_noȡPES_OPD������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectRxD(TParm parm) {
		TParm result = query("selectRxNoD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertCTSWASHM(TParm parm, TConnection connection) {
		TParm result = update("insertCTSWASHM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ����PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertCTSOUTM(TParm parm, TConnection connection) {
		TParm result = update("insertCTSOUTM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertCTSOUTD(TParm parm, TConnection connection) {
		TParm result = update("insertCTSOUTD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertCTSWASHD(TParm parm, TConnection connection) {
		TParm result = update("insertCTSWASHD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PESRESULT
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESRESULT(TParm parm) {
		TParm result = update("insertPESRESULT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectWASHM(TParm parm) {
		TParm result = query("selectWASHM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOUTM(TParm parm) {
		TParm result = query("selectOUTM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOUTD(TParm parm) {
		TParm result = query("selectOUTD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯOPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectWASHD(TParm parm) {
		TParm result = query("selectWASHD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateWASHM(TParm parm, TConnection connection) {
		TParm result = update("updateWASHM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateWASHD(TParm parm, TConnection connection) {
		TParm result = update("updateWASHD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����PESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateWASHMEndDate(TParm parm) {
		TParm result = update("updateWASHMEndDate", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectCTSMD(TParm parm) {
		TParm result = query("selectCTSMD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯOPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOUTMD(TParm parm) {
		TParm result = query("selectOUTMD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯPESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectClothByIn(TParm parm) {
		String sql =
//			" SELECT   A.CLOTH_NO, A.INV_CODE, A.OWNER, B.TO_ORG_CODE DEPT_CODE," +
//			" B.COST_CENTER_CODE STATION_CODE, A.PAT_FLG"  +
//			" FROM INV_DISPENSED A, INV_DISPENSEM B" +
//			" WHERE A.DISPENSE_NO = B.DISPENSE_NO AND CLOTH_NO IN (" + parm.getValue("CLOTHNOS") + ")" +
//			" ORDER BY CLOTH_NO";
			" SELECT   A.RFID CLOTH_NO, A.CTS_COST_CENTRE STATION_CODE, A.INV_CODE, B.INV_CHN_DESC," +
			" A.ACTIVE_FLG, A.PAT_FLG, A.OWNER, A.OWNER_CODE, 'N' NEW_FLG, A.TURN_POINT " +
			" FROM INV_STOCKDD A, INV_BASE B" +
			" WHERE A.INV_CODE = B.INV_CODE " +
			" AND A.RFID IN (" + parm.getValue("CLOTHNOS") + ")" +
			" AND A.STATE='1'" +
			" ORDER BY A.TURN_POINT,A.CTS_COST_CENTRE,A.RFID";
//		" SELECT CLOTH_NO, INV_CODE, OWNER, DEPT_CODE, STATION_CODE, ACTIVE_FLG, " +
//		" PAT_FLG, STATE " +
//		" FROM CTS_CLOTH " +
//		" WHERE ACTIVE_FLG = 'Y' " +
//		" AND CLOTH_NO IN (" + parm.getValue("CLOTHNOS") + ")" +
//		" ORDER BY CLOTH_NO ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 
	 * @param ip
	 * @return TParm {ANT_CHN_DESC=,ANT_ID=,RFID_URL=,MQ_DESC=,RFID_IP=,RFID_ID=}
	 */
	public TParm getRfidConfig(String ip){
		String sql =
			" SELECT CTS_ANT_IP, ANT_CHN_DESC, ANT_ID, RFID_URL, MQ_DESC, RFID_IP, RFID_ID, MAIN_FLG" +
			" FROM CTS_ANT_LIST" +
			" WHERE CTS_ANT_IP = '" + ip + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		if(result.getCount() < 0){
			result.setErrCode(-2);
			return result;
		}
		return result.getRow(0);
	}
	
	/**
	 * ȡ���ڷּ�ʱ������ϴ�µ�
	 * @return
	 */
	public TParm getNewWash(String station_code, String pat_flg, String turn_point){
		String sql="";
//		if(pat_flg.equals("")){
//			sql = 
//				" SELECT WASH_NO, STATION_CODE, END_DATE, PAT_FLG," +
//				" STATE, WASH_CODE, OPT_USER, OPT_DATE, OPT_TERM " +
//				" FROM CTS_OUTM" +
//				" WHERE END_DATE IS NULL " +
//				" AND STATION_CODE = '" + station_code + "' AND PAT_FLG IS NULL" ;
//		}else{
//		System.out.println("turn_point==="+turn_point);
//		
//		System.out.println("turn_point===a"+turn_point+"b");
			sql = 
				" SELECT WASH_NO, STATION_CODE, END_DATE, PAT_FLG," +
				" STATE, WASH_CODE, OPT_USER, OPT_DATE, OPT_TERM " +
				" FROM CTS_OUTM" +
				" WHERE END_DATE IS NULL " +
//				" AND STATION_CODE = '" + station_code + "'" +
//				" AND TURN_POINT = '" + turn_point + "'" +
				" AND PAT_FLG = '" + pat_flg + "'" ;
				
//		}
		if(turn_point.equals("")){
			sql += " AND TURN_POINT IS NULL " ;
		}else{
			sql += " AND TURN_POINT = '" + turn_point + "'" ;
		}
		
		
		
		System.out.println("newWash::sql==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ��ѯ��ⵥ�ͳ��ⵥ
	 * @param washNo
	 * @return
	 */
	public TParm selectInOutWashNo(String washNo){
		String sql = 
			" SELECT C.CLOTH_NO, B.WASH_NO IN_WASH_NO, C.WASH_NO OUT_WASH_NO, B.TURN_POINT" +
			" FROM INV_STOCKDD A, CTS_WASHD B, CTS_OUTD C" +
			" WHERE A.STATE = 0" +
			" AND A.RFID = B.CLOTH_NO" +
			" AND B.CLOTH_NO = C.CLOTH_NO" +
			" AND C.WASH_NO = '" + washNo + "'" +
			" ORDER BY C.CLOTH_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ������ⵥ��
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateInWashNo(TParm parm) {
		TParm result = update("updateInWashNo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ���³��ⵥ��
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateOutWashNo(TParm parm) {
		TParm result = update("updateOutWashNo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteOUTM(TParm parm, TConnection connection) {
		TParm result = update("deleteOUTM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm deleteOUTD(TParm parm, TConnection connection) {
		TParm result = update("deleteOUTD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm deleteWASHM(TParm parm, TConnection connection) {
		TParm result = update("deleteWASHM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	public TParm deleteWASHD(TParm parm, TConnection connection) {
		TParm result = update("deleteWASHD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	
}
