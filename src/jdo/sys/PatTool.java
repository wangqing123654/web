package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TDebug;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * <p>
 * Title:����������
 * </p>
 * 
 * <p>
 * Description:����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author lzk 2008.09.19
 * @version 1.0
 */
public class PatTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	private static PatTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static PatTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PatTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public PatTool() {
		setModuleName("sys\\SYSPatInfoModule.x");
		onInit();
	}

	/**
	 * ���ݲ����Ų�ѯ����
	 * 
	 * @param mrno
	 *            String ������
	 * @return String ����
	 */
	public String getNameForMrno(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return "";
		TParm parm = new TParm();
		parm.setData("MR_NO", checkMrno(mrno));
		return getResultString(query("getNameForMrno", parm), "PAT_NAME");
	}

	/**
	 * ���ݲ����Ų�ѯ���֤��
	 * 
	 * @param mrno
	 *            String ������
	 * @return String ���֤��
	 */
	public String getIdnoForMrno(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return "";
		TParm parm = new TParm();
		parm.setData("MR_NO", checkMrno(mrno));
		return getResultString(query("getIdnoForMrno", parm), "IDNO");
	}

	/**
	 * ���ݲ����Ų�ѯ������Ϣ
	 * 
	 * @param mrno
	 *            String ������
	 * @return TParm <PAT_NAME>,<IDNO>
	 */
	public TParm getInfoForMrno(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return new TParm();
		// ����ϲ�������
		mrno = getMergeMrno(mrno);
		TParm parm = new TParm();
		parm.setData("MR_NO", checkMrno(mrno));
		TParm result = query("getInfoForMrno", parm);
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}

	/**
	 * ��ⲡ���ų���
	 * 
	 * @param mrno
	 *            String
	 * @return String
	 */
	public String checkMrno(String mrno) {
		int mrnoLength = getMrNoLength();
		if (mrnoLength <= 0) {
			mrnoLength = mrno.length();
		}
		// add by wangb 2016/3/29 ����������д���-�鲻�����ݵ����� START
		String mrNo = "";
		if (mrno.contains("-")) {
			mrNo = StringTool.fill0(mrno, mrnoLength + mrno.length()
					- mrno.indexOf("-"));
		} else {
			mrNo = StringTool.fill0(mrno, mrnoLength);
		}
		return mrNo;
		// add by wangb 2016/3/29 ����������д���-�鲻�����ݵ����� END
	}

	/**
	 * �õ������ŵĳ���
	 * 
	 * @return int
	 */
	public int getMrNoLength() {
		return getResultInt(query("getMrNoLength"), "MRNO_LENGTH");
	}

	/**
	 * ���סԺ�ų���
	 * 
	 * @param ipdno
	 *            String
	 * @return String
	 */
	public String checkIpdno(String ipdno) {
		int ipdnoLength = getMrNoLength();
		if (ipdnoLength <= 0)
			ipdnoLength = ipdno.length();
		return StringTool.fill("0", ipdnoLength - ipdno.length()) + ipdno;
	}

	/**
	 * �õ�סԺ�ų���
	 * 
	 * @return int
	 */
	public int getIpdNoLength() {
		return getResultInt(query("getIpdNoLength"), "IPDNO_LENGTH");
	}

	/**
	 * ��������
	 * 
	 * @param pat
	 *            Pat ��������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean newPat(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = pat.getParm();
		String newMrNo = SystemTool.getInstance().getMrNo();
		if (newMrNo == null || newMrNo.length() == 0) {
			err("-1 ȡ�����Ŵ���!");
			return false;
		}
		parm.setData("MR_NO", newMrNo);
		TParm result = update("insertInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		pat.setMrNo(newMrNo);
		return true;
	}

	/**
	 * ���没����Ϣ
	 * 
	 * @param pat
	 *            Pat ��������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onSave(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = pat.getParm();
		if (!onSave(parm)) {
			return false;
		}
		return true;
	}

	/**
	 * ���没����Ϣ
	 * 
	 * @param parm
	 *            TParm ��������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onSave(TParm parm) {
		if (parm == null)
			return false;
		TParm result = update("updateInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * ���²�����Ϣ
	 * 
	 * @param parm
	 *            TParm ����
	 * @param connection
	 *            TConnection ����
	 * @return TParm ���
	 */
	public TParm updatePat(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "��������");
			return result;
		}
		// ִ�б���
		result = update("updateInfo", parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ���没����Ϣ(������,��̨ʹ��)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return boolean
	 */
	public boolean onSave(TParm parm, TConnection connection) {
		if (parm == null)
			return false;
		TParm result = update("updateInfo", parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * ɾ��������Ϣ
	 * 
	 * @param pat
	 *            Pat ��������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onDelete(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = new TParm();
		if (pat.getMrNo() == null || pat.getMrNo().trim().length() == 0)
			return false;
		parm.setData("MR_NO", pat.getMrNo());
		TParm result = update("deletePat", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * ɾ��������Ϣ(����ɾ��)
	 * 
	 * @param pat
	 *            Pat ��������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean onDelete$(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = new TParm();
		if (pat.getMrNo() == null || pat.getMrNo().trim().length() == 0)
			return false;
		parm.setData("MR_NO", pat.getMrNo());
		TParm result = update("deletePat$", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * �õ��ϲ��Ĳ�����
	 * 
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public String getMergeMrno(String mrNo) {
		if (mrNo == null || mrNo.trim().length() == 0)
			return "";
		mrNo = checkMrno(mrNo);
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		String newMrNo = getResultString(query("getMergeMrno", parm),
				"MERGE_TOMRNO");
		if (newMrNo.length() == 0)
			return mrNo;
		return getMergeMrno(newMrNo);
	}

	/**
	 * �����Ƿ����
	 * 
	 * @param mrNo
	 *            String ������
	 * @return boolean true ���� false ������
	 */
	public boolean existsPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		mrNo = checkMrno(mrNo);
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		return getResultInt(query("existsPat", parm), "COUNT") > 0;
	}

	/**
	 * ��������(ǰ̨����)
	 * 
	 * @param mrNo
	 *            String ������
	 * @param program
	 *            String ������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean lockPat(String mrNo, String program) {
		return lockPat(mrNo, program, Operator.getID(), Operator.getIP());
	}

	/**
	 * ��������(��̨����)
	 * 
	 * @param mrNo
	 *            String ������
	 * @param program
	 *            String ������
	 * @param userId
	 *            String ������Ա
	 * @param userIp
	 *            String �����ն�
	 * @return boolean
	 */
	public boolean lockPat(String mrNo, String program, String userId,
			String userIp) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		TParm parm = new TParm();
		mrNo = checkMrno(mrNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("TERM_IP", userIp);
		parm.setData("OPT_USER", userId);
		parm.setData("PRG_ID", program);
		TParm result = update("lockPat", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * �����Ƿ��Ѿ�����
	 * 
	 * @param mrNo
	 *            String ������
	 * @return boolean true ���� false û������
	 */
	public boolean isLockPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		TParm result = getLockPat(mrNo);
		// System.out.println("result=="+result);
		if (result == null)
			return false;
		return result.getCount() > 0;
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @param mrNo
	 *            String ������
	 * @return TParm OPT_USER;TERM_IP;LOCK_TIME;PRG_ID
	 */
	public TParm getLockPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return null;
		TParm parm = new TParm();
		mrNo = checkMrno(mrNo);
		parm.setData("MR_NO", mrNo);
		return query("getLockPat", parm);
	}

	/**
	 * ��������
	 * 
	 * @param mrNo
	 *            String ������
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean unLockPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		TParm parm = new TParm();
		mrNo = checkMrno(mrNo);
		parm.setData("MR_NO", mrNo);
		TParm result = update("unLockPat", parm);
		if (result.getErrCode() < 0)
			return false;
		return true;
	}

	/**
	 * �õ�������Ϣ
	 * 
	 * @param mrNo
	 *            String ������
	 * @return String
	 */
	public String getLockParmString(String mrNo) {
		TParm result = getLockPat(mrNo);
		if (result == null)
			return "";
		if (result.getCount() == 0)
			return "";
		String userName = OperatorTool.getInstance().getOperatorName(
				result.getValue("OPT_USER", 0));
		String time = StringTool.getString(result.getTimestamp("OPT_DATE", 0),
				"yyyy��MM��dd��HH:mm:ss");
		String name = getNameForMrno(mrNo);
		String ip = result.getValue("OPT_TERM", 0);
		String program = result.getValue("PRG_ID", 0);
		return userName + "��" + time + "��������" + name + "," + ip + program;
	}

	/**
	 * ���²�����Ϣ(For REG)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm upDateForReg(TParm parm) {
		TParm result = new TParm();
		if (parm == null)
			return result;
		result = update("upDateForReg", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * ���²�����Ϣ(For REG)�ҺŽ���ʹ�ã�add by huangjw 20150720
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 *//*
	public TParm modifyDateForReg(TParm parm) {
		TParm result = new TParm();
		if (parm == null)
			return result;
		result = update("modifyDateForReg", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}*/
	/**
	 * adm������ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryPat(TParm parm) {
		TParm result = new TParm();
		if(null==parm.getData("PAT_NAME")){
			result = query("queryAdmPat", parm);
		}else{  //yuml   s  20141104
			String sql1="";
			String sql2="";
			String sql3="";
			if(!"".equals(String.valueOf(parm.getData("PAT_NAME")))){
				sql1 = " PAT_NAME = '" + parm.getData("PAT_NAME") + "' ";
			}
			if(!"".equals(String.valueOf(parm.getData("SEX_CODE")))&&null!=parm.getData("SEX_CODE")){
				sql2 = " AND SEX_CODE = '" + parm.getData("SEX_CODE") + "' ";
			}
			if(null!=parm.getData("BIRTH_DATE")){
				DateFormat from_type = new SimpleDateFormat("yyyy-MM-dd");
				sql3 = " AND BIRTH_DATE = TO_DATE('"+from_type.format(parm.getData("BIRTH_DATE"))+"','yyyy-mm-dd') ";
			}
	        String selPat =
	            " SELECT MR_NO,IPD_NO,PAT_NAME,IDNO,TEL_HOME,BIRTH_DATE " +
	            "   FROM SYS_PATINFO  WHERE " +sql1+sql2+sql3+
	            "  ORDER BY MR_NO ";
	       result = new TParm(TJDODBTool.getInstance().select(selPat));
	       //System.out.println("kjdfgfhtr"+selPat);
		}		
          //yuml   e  20141104
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * adm������ѯadd by huangjw /��������Ҫ��Ϊģ����ѯ�����һ��������ط����ô˷�����Ϊ�˲�Ӱ�������ط���������дһ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryPatInfo(TParm parm) {
		TParm result = new TParm();
		if(null==parm.getData("PAT_NAME")){
			result = query("queryAdmPat", parm);
		}else{  //yuml   s  20141104
			String sql1="";
			String sql2="";
			String sql3="";
			if(!"".equals(String.valueOf(parm.getData("PAT_NAME")))){
				sql1 = " PAT_NAME like '%" + parm.getData("PAT_NAME") + "%' ";
			}
			if(!"".equals(String.valueOf(parm.getData("SEX_CODE")))&&null!=parm.getData("SEX_CODE")){
				sql2 = " AND SEX_CODE = '" + parm.getData("SEX_CODE") + "' ";
			}
			if(null!=parm.getData("BIRTH_DATE")){
				DateFormat from_type = new SimpleDateFormat("yyyy-MM-dd");
				sql3 = " AND BIRTH_DATE = TO_DATE('"+from_type.format(parm.getData("BIRTH_DATE"))+"','yyyy-mm-dd') ";
			}
	        String selPat =
	            " SELECT MR_NO,IPD_NO,PAT_NAME,IDNO,TEL_HOME,BIRTH_DATE " +
	            "   FROM SYS_PATINFO  WHERE " +sql1+sql2+sql3+
	            "  ORDER BY MR_NO ";
	       result = new TParm(TJDODBTool.getInstance().select(selPat));
	       //System.out.println("kjdfgfhtr"+selPat);
		}		
          //yuml   e  20141104
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * �������������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upLatestDeptDate(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null)
			return result;
		String admType = parm.getValue("ADM_TYPE");
		String visitCode = parm.getValue("VISIT_CODE");
		String deptCode = parm.getValue("REALDEPT_CODE");
		String mrNo = parm.getValue("MR_NO");
		String sql = "";
		String visitWhere = "";
		if ("0".equals(visitCode)) {
			visitWhere = " FIRST_ADM_DATE = SYSDATE, ";
		}
		if ("O".equals(admType)) {
			sql = "UPDATE SYS_PATINFO " + "   SET " + visitWhere
					+ "       RCNT_OPD_DATE = SYSDATE,"
					+ "       RCNT_OPD_DEPT = '" + deptCode + "' "
					+ " WHERE MR_NO = '" + mrNo + "' ";
		}
		if ("E".equals(admType)) {
			sql = "UPDATE SYS_PATINFO " + "   SET " + visitWhere
					+ "       RCNT_EMG_DATE = SYSDATE,"
					+ "       RCNT_EMG_DEPT = '" + deptCode + "' "
					+ " WHERE MR_NO = '" + mrNo + "' ";
		}
		if ("I".equals(admType)) {
			sql = "UPDATE SYS_PATINFO " + "   SET RCNT_IPD_DATE = SYSDATE,"
					+ "       RCNT_IPD_DEPT = '" + deptCode + "' "
					+ " WHERE MR_NO = '" + mrNo + "' ";
		}
		// System.out.println("sql:"+sql);
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		return result;
	}

	/**
	 * �������֤�����ѯ������Ϣ סԺҽ���ʸ�ȷ���鿪������ʹ��
	 * 
	 * @param parm IDNO
	 * ======pangben 20120127
	 * @return TParm 
	 */
	public TParm getInfoForIdNo(TParm parm) {
		TParm result = query("getInfoForIdNo", parm);
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 
	 * ���²���Ѫ��.
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePatBldType(TParm parm,TConnection conn){
        TParm result = this.update("updatePatBldType", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}

	public static void main(String args[]) {
		TDebug.initClient();
		// TDebug.initServer();
		PatTool pat = new PatTool();
		// System.out.println(pat.lockPat("16","UddRtnRgstPat"));
		// System.out.println(pat.getLockParmString("27"));
		// System.out.println(pat.getNameForMrno("308"));
		// pat.setModuleName("sys\\SYSOperatorModule.x");
		/*
		 * pat.onInit(); TParm parm = new TParm();
		 * parm.setData("FINAL_FLG","Y"); parm.setData("CLASSIFY","9");
		 * System.out.println(pat.query("selectByWhere",parm));
		 */
	}
	
}
