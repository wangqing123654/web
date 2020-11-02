package jdo.hl7;

import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;

import jdo.bms.ws.BmsTool;
import jdo.odi.OdiMainTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.exception.HL7Exception;

public class LISJdo extends TJDOTool {
	private String bilpoint;// �Ʒѵ� 1��ʿִ�мƷ� 2ҽ������ִ��
	/**
	 * ʵ��
	 */
	public static LISJdo instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return TestJDO
	 */
	public static synchronized LISJdo getInstance() {
		if (instanceObject == null)
			instanceObject = new LISJdo();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public LISJdo() {
		setModuleName("lis\\LisJavaHisTQL.x");
		onInit();
	}

	/**
	 * �õ�ϵͳʱ��
	 * 
	 * @return Timestamp ��Ч����
	 */
	public Timestamp getDate() {
		TParm parm = new TParm();
		return getResultTimestamp(query("getDate", parm), "SYSDATE");
	}

	/**
	 * ����LIS��ҽ������״̬
	 * 
	 * @param labnumberNo
	 *            String �����
	 * @return boolean
	 */
	public boolean getUpDateNewLabStust(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		// Log.DEBUG = true;
		TParm action = this.update("getUpDateNewLabStust", parm);
		if (action.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * ����LIS��ҽ��ǩ��״̬
	 * 
	 * @param labnumberNo
	 *            String �����
	 * @return boolean
	 */
	public TParm upDateLisQS(String labnumberNo, String admType) throws HL7Exception{
		TParm parm = new TParm();
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
			return medData;
		}
		TConnection con = this.getConnection();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateLisQS", parm, con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "����ǩ��״̬ʧ��");
			con.rollback();
			con.close();
			return action;
		}
		if (admType.equals("I")) {
			parm.setData("EXEC_FLG", "Y");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "����סԺִ��״̬ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * ����LIS��ҽ��ȡ��ǩ��״̬
	 * 
	 * @param labnumberNo
	 *            String �����
	 * @return boolean
	 */
	public TParm upDateQXLisQS(String labnumberNo, String admType)throws HL7Exception {
		TParm parm = new TParm();
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
			return medData;
		}
		TConnection con = this.getConnection();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateQXLisQS", parm,con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "����ǩ��״̬ʧ��");
			con.rollback();
			con.close();
			return action;
		}
		if (admType.equals("I")) {
			parm.setData("EXEC_FLG", "N");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "����סԺִ��״̬ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * LIS����������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisDJ(String labnumberNo, String admType) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
			return medData;
		}
		TConnection con = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("EXEC_FLG", "Y");
		TParm action = this.update("upDateLisDJ", parm, con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "���µ���״̬ʧ��");
			con.rollback();
			con.close();
			return action;
		}
		if ("O".equals(admType)) {
			// action = BILJdo.getInstance().onOEBilCheck(parm, "LIS");
			// if (action.getErrCode() < 0) {
			// con.rollback();
			// con.close();
			// return action;
			// }
			// action = this.update("updateOpdOrderTable", parm, con);
			// if (action.getErrCode() < 0) {
			// action.setErr(-1, "��������ִ��״̬ʧ��");
			// con.rollback();
			// con.close();
			// return action;
			// }
		}
		if ("E".equals(admType)) {
			// action = BILJdo.getInstance().onOEBilCheck(parm, "LIS");
			// if (action.getErrCode() < 0) {
			// con.rollback();
			// con.close();
			// return action;
			// }
			// action = this.update("updateOpdOrderTable", parm, con);
			// if (action.getErrCode() < 0) {
			// action.setErr(-1, "���¼���ִ��״̬ʧ��");
			// con.rollback();
			// con.close();
			// return action;
			// }
		}
		if ("I".equals(admType)) {
			bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
					"BIL_POINT");
			if (bilpoint.equals("2")) {
				action = BILJdo.getInstance().onIBilOperate(con, parm, "LIS",
						"ADD");
				if (action.getErrCode() < 0) {
					con.rollback();
					con.close();
					return action;
				}
			}
		}
		if ("H".equals(admType)) {
			action = this.update("updateHrmOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "���½���ִ��״̬ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * LISȡ���������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisQXDJ(String labnumberNo, String admType) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
			return medData;
		}
		TConnection con = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("EXEC_FLG", "N");
		parm.setData("EXEC_DR_CODE", "");
		parm.setData("EXEC_DATE", new TNull(Timestamp.class));
		TParm action = this.update("upDateLisQXDJ", parm);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "ȡ������״̬����ʧ��");
			return action;
		}
		if ("O".equals(admType)) {
			action = this.update("updateOpdOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "����ִ��״̬����ʧ��");
				con.rollback();
				con.close();
				return action;
			}
			action = upDateLisPrint(labnumberNo, "N", con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "�����ӡ״̬����ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		if ("E".equals(admType)) {
			action = this.update("updateOpdOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "����ִ��״̬����ʧ��");
				con.rollback();
				con.close();
				return action;
			}
			action = upDateLisPrint(labnumberNo, "N", con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "�����ӡ״̬����ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		if ("I".equals(admType)) {
			bilpoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
					"BIL_POINT");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "סԺִ��״̬����ʧ��");
				con.rollback();
				con.close();
				return action;
			}
			if (bilpoint.equals("2")) {
				action = BILJdo.getInstance().onIBilOperate(con, parm, "LIS",
						"UNADD");
				if (action.getErrCode() < 0) {
					con.rollback();
					con.close();
					return action;
				}
			}
		}
		if ("H".equals(admType)) {
			action = this.update("updateHrmOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "����ִ��״̬����ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}

	/**
	 * LIS�������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisJSDJ(String labnumberNo,String admType) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
			return medData;
		}
		TConnection con = this.getConnection();
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateLisJSDJ", parm,con);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "�������״̬����ʧ��");
			con.rollback();
			con.close();
			return action;
		}
		if (admType.equals("I")) {
			parm.setData("EXEC_FLG", "N");
			action = this.update("updateOdiOrderTable", parm, con);
			if (action.getErrCode() < 0) {
				action.setErr(-1, "����סԺִ��״̬ʧ��");
				con.rollback();
				con.close();
				return action;
			}
		}
		con.commit();
		con.close();
		return action;
	}
	/**
	 * LIS�����ӡ
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateLisPrint(String labnumberNo,String printFlg,TConnection connection) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("PRINT_FLG", printFlg);
		TParm action = this.update("upDateLisPrint", parm,connection);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "�����ӡ����ʧ��");
			return action;
		}
		return action;
	}

	/**
	 * ���»�ʿ����վ״̬
	 * 
	 * @param labnumberNo
	 *            String
	 * @return TParm
	 */
	public TParm selectNBWStat(String labnumberNo) {
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.query("selectNBWStat", parm);
		return action;
	}

	/**
	 * ������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm upDateSHWC(String labnumberNo) throws HL7Exception{
		TParm medData = Hl7Tool.getInstance().getOrder(labnumberNo, "LIS","");
		if (medData.getCount() <= 0) {
			medData.setErr(-1, "δ��ѯ��ҽ��");
			return medData;
		}
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("upDateSHWC", parm);
		if (action.getErrCode() < 0) {
			action.setErr(-1, "���ʧ��");
			return action;
		}
		return action;
	}

	/**
	 * ɾ���ټ������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm delLAB_GENRPTDTLData(String labnumberNo) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		TParm action = this.update("delLAB_GENRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * ɾ��΢����LIS��������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm delLAB_ANTISENSTESTData(String labnumberNo,
			String SampleNo) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("SAMPLE_NO", SampleNo);
		TParm action = this.update("delLAB_ANTISENSTESTData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * ɾ��ϸ�����LIS��������
	 * 
	 * @param labnumberNo
	 *            String
	 * @return boolean
	 */
	public TParm delLAB_CULRPTDTLData(String labnumberNo,
			String SampleNo) throws HL7Exception{
		TParm parm = new TParm();
		parm.setData("LAB_NUMBER", labnumberNo);
		parm.setData("SAMPLE_NO", SampleNo);
		TParm action = this.update("delLAB_CULRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * �����ټ������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public TParm insertLAB_GENRPTDTLData(TParm parm) throws HL7Exception{
		TParm action = this.update("insertLAB_GENRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * �õ�orderNo����
	 * 
	 * @param admType
	 * @return
	 */
	public int getorderNolen(String admType) throws HL7Exception{
		int length = 0;
		TParm parm = new TParm();
		if ("O".equals(admType) || "E".equals(admType)) {
			TParm action = this.query("getOorderNO", parm);
			length = action.getInt("NO_LENGTH", 0);
		} else if (admType.equals("I")) {
			TParm action = this.query("getIorderNO", parm);
			length = action.getInt("NO_LENGTH", 0);
		} else if ("H".equals(admType)) {
			TParm action = this.query("getHorderNO", parm);
			length = action.getInt("NO_LENGTH", 0);
		}
		return length;
	}

	/**
	 * ����΢�������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public TParm insertLAB_ANTISENSTESTData(TParm parm) throws HL7Exception{
		TParm action = this.update("insertLAB_ANTISENSTESTData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
	}

	/**
	 * ����ϸ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public TParm insertLAB_CULRPTDTLData(TParm parm) throws HL7Exception{
		TParm action = this.update("insertLAB_CULRPTDTLData", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		return action;
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
	 * �������
	 * 
	 * @return boolean
	 */
	public TParm upDateBGEND(String labNumberNo, String rptDttm,
			String drUser, String shUser, String admType) throws HL7Exception{
		TParm parm = new TParm();
		String userdr[] = StringTool.parseLine(drUser, "^");
		String usersh[] = StringTool.parseLine(shUser, "^");
		parm.setData("LAB_NUMBER", labNumberNo);
		parm.setData("RPT_DTTM", rptDttm);
		String testUser="";
		if (userdr.length > 0) {
			parm.setData("DLVRYRPT_USER", userdr[0]);
			testUser=userdr[0];
		} else {
			parm.setData("DLVRYRPT_USER", "");
		}
		if (usersh.length > 0) {
			parm.setData("SH_USER", usersh[0]);
		} else {
			parm.setData("SH_USER", "");
		}
		TParm action = this.update("upDateBGEND", parm);
		if (action.getErrCode() < 0) {
			return action;
		}
		//===================================shibl add start=============================================================
		TParm medLog=Hl7Tool.getInstance().onInsertMedNodify(labNumberNo, "LIS");
		if(medLog.getErrCode()<0){
			System.out.println("����MedNodify����ִ���"+medLog);
		}
		TParm regParm = new TParm(this.getDBTool().select(
				"SELECT CASE_NO,MR_NO FROM MED_APPLY WHERE APPLICATION_NO='"
						+ labNumberNo + "' AND CAT1_TYPE='LIS'"));
		if (regParm.getCount() > 0) {
			String mrNo = regParm.getValue("MR_NO", 0);
			TParm patParm=new TParm();
			try {
				patParm = BmsTool.getInstance().onMedExeBld(labNumberNo, mrNo,testUser);
			} catch (Exception e) {
				e.printStackTrace();
				throw new HL7Exception("���²���Ѫ�Ͳ����쳣");
			}
			if(patParm.getErrCode()<0){
				System.out.println("jdo.hl7.LISJdo->upDateBGEND ����sys_patinfo��Ѫ����Ϣʧ��"+patParm);
			}
		}
		//=====================================shibl add end=============================================================
		if (regParm.getCount() > 0) {
			String caseNo = regParm.getValue("CASE_NO", 0);
			String mrNo = regParm.getValue("MR_NO", 0);
			regParm = new TParm(this.getDBTool().select(
					"SELECT STATUS FROM MED_APPLY WHERE CASE_NO='" + caseNo
							+ "' AND MR_NO='" + mrNo + "'"));
			Map map = new HashMap();
			map.put(7, "Y");
			map.put(8, "Y");
			int rowCount = regParm.getCount();
			int statCount = 0;
			if (rowCount > 0) {
				for (int i = 0; i < rowCount; i++) {
					TParm temp = regParm.getRow(i);
					if (map.get(temp.getInt("STATUS")) != null) {
						statCount++;
					}
				}
			}
			// ��ֵREG״̬
			if (statCount == (rowCount)) {
				// ȫ�����
				TParm p = new TParm();
				p.setData("REPORT_STATUS", "3");
				p.setData("CASE_NO", caseNo);
				// �ż�
				if ("O".equals(admType) || "E".equals(admType)){
					action = this.update("updateRegPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
				// �������
				if ("H".equals(admType)) {
					// �鿴������Ŀ
					TParm hrmParm = new TParm(
							this
									.getDBTool()
									.select(
											"SELECT CASE WHEN (A.EXEC_DR_CODE IS NULL OR A.EXEC_DR_CODE = '') THEN 'N' ELSE 'Y' END DONE FROM HRM_ORDER A    WHERE A.CASE_NO='"
													+ caseNo
													+ "' AND A.SETMAIN_FLG='Y' AND DEPT_ATTRIBUTE IS NOT NULL"));
					int hrmParmCount = hrmParm.getCount();
					for (int i = 0; i < hrmParmCount; i++) {
						TParm temp = hrmParm.getRow(i);
						if ("N".equals(temp.getValue("DONE"))) {
							p.setData("REPORT_STATUS", "2");
							break;
						}
					}
					action = this.update("updateHrmPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
			} else {
				// �������
				TParm p = new TParm();
				p.setData("REPORT_STATUS", "2");
				p.setData("CASE_NO", caseNo);
				// �ż�
				if ("O".equals(admType) || "E".equals(admType)){
					action = this.update("updateRegPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
				// �������
				if ("H".equals(admType)){
					action = this.update("updateHrmPatAdmStstus", p);
					if (action.getErrCode() < 0) {
						return action;
					}
				}
			}
		}
		return action;
	}

	/**
	 * ��ѯ֪ͨ��Ϣ���ݣ�
	 * 
	 * @param cat1Type
	 *            String
	 * @param applicationNo
	 *            String
	 * @param orderNo
	 *            String
	 * @param seqNo
	 *            String
	 * @return TParm
	 */
	public TParm selectMedApply(String cat1Type,
			String applicationNo, String orderNo, String seqNo) throws HL7Exception{
		StringBuffer sb = new StringBuffer();
		sb
				.append("SELECT A.MR_NO,A.CASE_NO,A.PAT_NAME,A.ORDER_DR_CODE,B.USER_NAME,B.E_MAIL ");
		sb
				.append("FROM MED_APPLY A LEFT JOIN SYS_OPERATOR B ON A.ORDER_DR_CODE=B.USER_ID ");
		sb.append("WHERE A.CAT1_TYPE='" + cat1Type + "' ");
		sb.append("AND A.APPLICATION_NO='" + applicationNo + "' ");
		sb.append("AND A.ORDER_NO='" + orderNo + "' ");
		sb.append("AND A.SEQ_NO='" + seqNo + "'");
		// MED_APPLY
		TParm parm = new TParm(this.getDBTool().select(sb.toString()));
		return parm;
	}

}
