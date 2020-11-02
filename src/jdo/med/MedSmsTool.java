package jdo.med;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.DateUtil;

public class MedSmsTool extends TJDOTool {

	public MedSmsTool() {
		setModuleName("med\\MEDSmsModule.x");
		onInit();
	}

	String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 实例
	 */
	public static MedSmsTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static MedSmsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MedSmsTool();
		return instanceObject;
	}

	/**
	 * 新增短信1
	 * 
	 * @param parm
	 * @param con
	 * @return
	 */
	public TParm insertMedSms(TParm parm, TConnection con) {
		parm.setData("SMS_CODE", getSmsCode());
//		TParm result = this.update("insertdata", parm);
		TParm result = this.update("insertdata", parm, con);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 新增短信2
	 * 
	 * @param parm
	 * @param con
	 * @return
	 */
	public TParm insertMedSms(TParm parm) {
		parm.setData("SMS_CODE", getSmsCode());
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public static String getSmsCode() {
		return SystemTool.getInstance().getNo("ALL", "PUB", "SMS_CODE",
				"SMS_CODE");
	}

	public TParm onQuery(TParm parm) {
		return new TParm(TJDODBTool.getInstance().select(selectSql(parm)));
	}

	/**
	 * 定时查询发送时间大于等于30分钟的短信
	 * 
	 * @param con
	 * @return
	 */
	public TParm getMinute() {
		/**
		 * TParm parm = new TParm(); parm.setData("CURRENT_DATE",
		 * DateUtil.getNowTime(TIME_FORMAT)); TParm result =
		 * this.query("selectminute", parm); if (result.getErrCode() < 0) {
		 * err("ERR:" + result.getErrCode() + result.getErrText() +
		 * result.getErrName()); return result; } return result;
		 */

		String date = DateUtil.getNowTime(TIME_FORMAT);
		String sql = " SELECT PATIENT_NAME, MR_NO,SMS_CONTENT,CASE_NO, (TO_CHAR(SEND_TIME,'yyyy-MM-dd HH24:MI:SS')) as SEND_TIME, "
				+ " ADM_TYPE,DEPT_CODE,SMS_CODE "
				+ " FROM MED_SMS  "
				+ " WHERE STATE !='9' AND (( TO_DATE('"
				+ date
				+ "','yyyy-MM-dd HH24:MI:SS')-SEND_TIME)*24*60 >= 1 ) ";
		return new TParm(TJDODBTool.getInstance().select(sql));

	}

	/**
	 * 构造新增MED_SMS表SQL语句
	 * 
	 * @param parm
	 * @return
	 */
	public static String createSql(TParm parm) {
		System.out.println("111111111111111111111111111111");
		String sql = "INSERT INTO MED_SMS"
				+ " ( SMS_CODE,PATIENT_NAME,CASE_NO,MR_NO,STATION_CODE,"
				+ "  BED_NO,IPD_NO,DEPT_CODE,BILLING_DOCTORS,APPLICATION_NO, "
				+ "  TESTITEM_CODE,TESTITEM_CHN_DESC,TEST_VALUE,CRTCLLWLMT,STATE,"
				+ "  SEND_TIME,SMS_CONTENT,OPT_USER,OPT_DATE,OPT_TERM,"
				+ "  ADM_TYPE,REPOTR_TIME ) " + " VALUES " + "('"
				+ getSmsCode()
				+ "', '"
				+ parm.getValue("PATIENT_NAME")
				+ "', '"
				+ parm.getValue("CASE_NO")
				+ "', '"
				+ parm.getValue("MR_NO")
				+ "', '"
				+ parm.getValue("STATION_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("BED_NO")
				+ "', '"
				+ parm.getValue("IPD_NO")
				+ "', '"
				+ parm.getValue("DEPT_CODE")
				+ "', '"
				+ parm.getValue("BILLING_DOCTORS")
				+ "', '"
				+ parm.getValue("APPLICATION_NO")
				+ "',"
				+ " '"
				+ parm.getValue("TESTITEM_CODE")
				+ "', '"
				+ parm.getValue("TESTITEM_CHN_DESC")
				+ "', '"
				+ parm.getValue("TEST_VALUE")
				+ "', '"
				+ parm.getValue("CRTCLLWLMT")
				+ "', '9', "
				+ " SYSDATE,'"
				+ parm.getValue("SMS_CONTENT")
				+ "', '"
				+ parm.getValue("OPT_USER")
				+ "', SYSDATE,'"
				+ parm.getValue("OPT_TERM")
				+ "',"
				+ " '"
				+ parm.getValue("ADM_TYPE")
				+"',"
				+parm.getValue("REPOTR_TIME")
				+ "'  )";
		// System.out.println("SQL:"+sql);
		return sql;
	}

	public TParm getRealdrCode(String admType, String caseNo) {
		String sql = " SELECT s.TEL1,r.REALDR_CODE,r.DEPT_CODE,r.MR_NO "
				+ " FROM REG_PATADM r,SYS_OPERATOR s "
				+ " WHERE r.REALDR_CODE=s.USER_ID AND r.ADM_TYPE='" + admType
				+ "' AND r.CASE_NO='" + caseNo + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 根据申请号得到对应的case_no,adm_type
	 * 
	 * @param applicationNo
	 * @return
	 */
	public TParm getMedApply(String applicationNo) {
		String sql = " SELECT m.CASE_NO,m.ADM_TYPE,m.IPD_NO,STATION_CODE,BED_NO "
				+ " FROM MED_APPLY m "
				+ " WHERE m.APPLICATION_NO='"
				+ applicationNo + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm getVsDrCode(String caseNo) {
		String sql = " SELECT s.TEL1,a.VS_DR_CODE ,a.DEPT_CODE,a.MR_NO"
				+ " FROM ADM_INP a,SYS_OPERATOR s "
				+ " WHERE a.VS_DR_CODE =s.USER_ID AND  a.CASE_NO='" + caseNo
				+ "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 取住院主任电话号码
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getDirectorTel(String caseNo) {

		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = this.query("selectdirectortel", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 得到门诊主任电话号码或医务科主管或者主管院长的电话号码
	 * 
	 * @param deptCode
	 * @param competentType
	 * @return
	 */
	public TParm getDeanOrCompementTel(String deptCode, String competentType) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", deptCode);
		parm.setData("COMPETENT_TYPE", competentType);
		TParm result = this.query("selectdeanorcompementtel", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据科室得到值班电话
	 * 
	 * @param deptCode
	 * @param competentType
	 * @return
	 */
	public TParm getDutyTel(TParm parm) {

		String deptCode = parm.getValue("DEPT_CODE");
		if (deptCode == null || deptCode.equals("")) {
			return new TParm();
		}
		String sql = " SELECT A.DUTY_TEL,A.DEPT_CHN_DESC FROM SYS_DEPT A WHERE A.DEPT_CODE='"
				+ deptCode + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新短信表状态为9 代表处理
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMedSms(TParm parm) {
		parm.setData("STATE", "9");
		parm.setData("HANDLE_SUGGEST_USER", Operator.getID() + "");
		TParm result = this.update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm updateMedSmsTime(String smsCode, String filed) {

		/**
		 * TParm parm = new TParm(); parm.setData("SMS_CODE",smsCode);
		 * parm.setData("SMS_TIME",filed); TParm result =
		 * this.update("updatedatatime", parm); if (result.getErrCode() < 0) {
		 * err("ERR:" + result.getErrCode() + result.getErrText() +
		 * result.getErrName()); return result; } return result;
		 */

		String sql = " UPDATE MED_SMS SET " + filed + "=" + "SYSDATE "
				+ " WHERE SMS_CODE='" + smsCode + "' ";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	public String selectSql(TParm parm) {
		System.out.println("666666666666666666666666");
		String sql = "SELECT ADM_TYPE,MR_NO,PATIENT_NAME,TESTITEM_CODE,TEST_VALUE,CRTCLLWLMT, "
				+ "STATE,BILLING_DOCTORS,NOTIFY_DOCTORS_TIME,DIRECTOR_DR_CODE,NOTIFY_COMPETENT_TIME, "
				+ "NOTIFY_DIRECTOR_DR_TIME,NOTIFY_DEAN_TIME,HANDLE_USER,HANDLE_TIME,SMS_CODE, "
				+ "HANDLE_OPINION,SEND_TIME,SMS_CONTENT,STATION_CODE, "
				+ "CASE_NO,OPT_USER,OPT_DATE,OPT_TERM,DEAN_CODE,  "
				+ "COMPETENT_CODE,TESTITEM_CHN_DESC,DEPT_CODE,HANDLE_SUGGEST,HANDLE_SUGGEST_USER,HANDLE_SUGGEST_TIME,BED_NO,IPD_NO, "
				+ "SEX_CODE,BIRTH_DAY,REPOTR_TIME "
				
				+ "FROM MED_SMS A  " + "WHERE 1=1 ";
		String deptCode = parm.getValue("DEPT_CODE");
		if (deptCode != null && !deptCode.equals("")) {
			sql += " AND DEPT_CODE='" + parm.getValue("DEPT_CODE") + "' ";
		}
		String reportTime = parm.getValue("REPOTR_TIME");
		if (reportTime != null && !reportTime.equals("")) {
			reportTime=reportTime.substring(0,reportTime.length()-2);
			sql += " AND REPOTR_TIME =" + " TO_DATE('" + reportTime
					+ "','yyyy-MM-dd HH24:MI:SS')";
			System.out.println("canshu:<<<<<<<<<<<<<<<"+reportTime);
		}
		

		String stationCODE = parm.getValue("STATION_CODE");
		if (stationCODE != null && !stationCODE.equals("")) {
			sql += " AND STATION_CODE='" + stationCODE + "' ";
		}

		String smsState = parm.getValue("SMS_STATE");
		if (smsState != null && !smsState.equals("")) {
			if (smsState.equals("0")) {
				sql += " AND STATE != '9' ";
			} else {
				sql += " AND STATE='" + smsState + "' ";
			}
		}

		String mrNo = parm.getValue("MR_NO");
		if (mrNo != null && !mrNo.equals("")) {
			sql += " AND MR_NO='" + mrNo + "' ";
		}

		String begionTime = parm.getValue("BEGIN_TIME");
		if (begionTime != null && !begionTime.equals("")) {
			begionTime = begionTime.substring(0, begionTime.length() - 2);
			sql += " AND HANDLE_TIME >=" + " TO_DATE('" + begionTime
					+ "','yyyy-MM-dd HH24:MI:SS')";
		}

		String endTime = parm.getValue("END_TIME");
		if (endTime != null && !endTime.equals("")) {
			endTime = endTime.substring(0, endTime.length() - 2);
			sql += " AND HANDLE_TIME <=" + " TO_DATE('" + endTime
					+ "','yyyy-MM-dd HH24:MI:SS')";
		}
		
		String admType = parm.getValue("ADM_TYPE");
		if(admType != null && !admType.equals("")){
			sql += " AND ADM_TYPE = '" + admType + "'";
		}
		
		sql += "  ORDER BY HANDLE_TIME DESC  ";
//		System.out.println("sql==================:"+sql);
		return sql;
	}

	public TParm onQueryMedSms(TParm parm) {
		String caseNo = parm.getValue("CASE_NO");
		String applicationNo = parm.getValue("APPLICATION_NO");
		String testitemCode = parm.getValue("TESTITEM_CODE");
		String sql = " SELECT A.SMS_CODE FROM MED_SMS A "
				+ " WHERE A.CASE_NO='" + caseNo + "' "
				+ "      AND A.APPLICATION_NO='" + applicationNo + "' "
				+ "      AND A.TESTITEM_CODE='" + testitemCode + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public static void main(String[] args) {
		TParm parm = new TParm();
		parm.setData("SMS_CODE", "1");
		parm.setData("CASE_NO", "120401000345");
		parm.setData("NAME", "宋学英");
		parm.setData("MR_NO", "000000251379");
		parm.setData("STATION_CODE", "5");
		parm.setData("BED_NO", "6");
		parm.setData("IP_NO", "7");
		parm.setData("DEPT_CODE", "8");
		parm.setData("BILLING_DOCTORS", "9");
		parm.setData("APPLICATION_NO", "10");
		parm.setData("TESTITEM_CODE", "11");
		parm.setData("TESTITEM_CHN_DESC", "12");
		parm.setData("TEST_VALUE", "13");
		parm.setData("CRTCLLWLMT", "14");
		parm.setData("STATE", "15");
		parm.setData("SEND_TIME", "20120921");
		parm.setData("SMS_CONTENT", "17");
		parm.setData("OPT_USER", "18");
		parm.setData("OPT_DATE", "19");
		parm.setData("OPT_TERM", "20");
		parm.setData("ADM_TYPE", "21");
		// String sql = createSql(parm);

		String begionTime = "2012-07-10 00:00:00.0";
		begionTime = begionTime.substring(0, begionTime.length() - 2);
	}
	
	/**
	 * 通过病案号查询病人信息
	 * @param parm 装有病案号的TParm
	 * @return
	 */
	public TParm getPatInfoByMrNo(TParm parm){
		String admType = parm.getValue("ADM_TYPE");
		String mrNo = parm.getValue("MR_NO");
		String tableName = "REG_PATADM";
		//门诊 急诊
		String sql = " SELECT CASE_NO,CLINICAREA_CODE,DEPT_CODE FROM " + tableName + " WHERE MR_NO = '" + mrNo + "' AND ADM_TYPE ='"+admType+"' ORDER BY REG_DATE DESC";
		if(admType.equals("I")){
			//住院
			tableName = "ADM_INP";
			sql = " SELECT CASE_NO,IPD_NO,BED_NO,STATION_CODE,DEPT_CODE FROM " + tableName + 
				  " WHERE MR_NO = '" + mrNo + "' ORDER BY IN_DATE DESC" ; 
		}
		else if(admType.equals("H")){
			//健检
			tableName = "HRM_PATADM";
			sql = " SELECT CASE_NO FROM " + tableName + 
				  " WHERE MR_NO = '" + mrNo + "'" ;
		}
		System.out.println("mrno::::::::"+sql);	
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
		return result;
	}
	
	/**
	 * 查询健检医师
	 * @param caseNo
	 * @return
	 */
	public TParm getHrmDrCode(String caseNo) {
		String sql = " SELECT s.TEL1,r.DR_CODE "
				+ " FROM HRM_ORDER r,SYS_OPERATOR s "
				+ " WHERE r.DR_CODE=s.USER_ID " 
				+ " AND r.CASE_NO='" + caseNo + "'"
				+ " GROUP BY s.TEL1,r.DR_CODE";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询病人姓名
	 * @param caseNo
	 * @return
	 */
	public TParm getPatName(String mrNo) {
		String sql = " SELECT PAT_NAME FROM SYS_PATINFO "
				+ " WHERE MR_NO= '" + mrNo + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	public TParm getPat(String mrNo){
		String sql = " SELECT * FROM SYS_PATINFO "
			+ " WHERE MR_NO= '" + mrNo + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	public void writeXml(TParm parmRow, TParm telParm, String content) {
		//写文件
		 TParm xmlParm = new TParm();
		
		 xmlParm.setData("Content",content);
		 xmlParm.setData("MrNo",telParm.getValue("MR_NO",0));
		 
		 //得到科室,门急住类别
		 String deptChnCode =  parmRow.getValue("DEPT_CHN_DESC");
		 String admType = parmRow.getValue("ADM_TYPE");
		 String admTypeChn = "";
		 admTypeChn = getAdmType(admType);
		 
		 xmlParm.setData("Name",parmRow.getValue("PAT_NAME")+","+deptChnCode+","+admTypeChn);
		// System.out.println("xmlParm:"+xmlParm);
		 //System.out.println("telParm:"+telParm);
		 XmlUtil.createSmsFile(xmlParm, telParm);
	}
	
	private String getAdmType(String admType) {
		String admTypeChn = "";
		if(admType != null ){
			 if(admType.equals("O")){
				 admTypeChn = "门诊";
			 }else if(admType.equals("I")) {
				 admTypeChn = "住院";
			}else if(admType.equals("E")){
				 admTypeChn = "急诊";
			}else if(admType.equals("H")){
				 admTypeChn = "健康检查";
			}
		 }
		return admTypeChn;
	}

	/**
	 * 通过UserId得到医师电话号码
	 * @param billDoc 医师的UserId
	 * @return
	 */
	public TParm getDrPhone(String userId) {
		String sql = "SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID = '" + userId + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	

	/**
	 * 更新MED_SMS
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMedSms(TParm parm, TConnection conn){
		// 科室、病区、开单医生、检查项目、检查值、通知事项、登记人、登记时间
		String REPOTR_TIME = parm.getValue("REPOTR_TIME").substring(0,19);
		String sql = " UPDATE MED_SMS SET DEPT_CODE='"+parm.getValue("DEPT_CODE")+"', "
				+ "STATION_CODE='"+parm.getValue("STATION_CODE")+"', "
				+ "BILLING_DOCTORS='"+parm.getValue("BILLING_DOCTORS")+"', "
				+ "TESTITEM_CHN_DESC='"+parm.getValue("TESTITEM_CHN_DESC")+"', "
				+ "TEST_VALUE='"+parm.getValue("TEST_VALUE")+"', "
				+ "HANDLE_OPINION='"+parm.getValue("HANDLE_OPINION")+"', "
				+ "HANDLE_USER='"+parm.getValue("HANDLE_USER")+"', "
				+ "HANDLE_TIME=SYSDATE ,"
				+ "REPOTR_TIME = to_date('"+REPOTR_TIME+"' , 'yyyy-mm-dd hh24:mi:ss') "//20180907  yanglu   新增报告时间字段
				+ "WHERE SMS_CODE='"+parm.getValue("SMS_CODE")+"' ";		
		TParm result  = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			return result;
		}
		return result;
	}
}
