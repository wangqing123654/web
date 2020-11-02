package jdo.reg;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;
import jdo.bil.BIL;
import jdo.sys.Operator;
import java.text.DecimalFormat;
import jdo.sys.SysFee;
import com.dongyang.util.TypeTool;
import jdo.util.Manager;
import java.text.SimpleDateFormat;

/**
 *
 * <p>Title:�Һ����������� </p>
 *
 * <p>Description:�Һ����������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.28
 * @version 1.0
 */
public class PatAdmTool extends TJDOTool {
    /**
     * ʵ��
     */
    private static PatAdmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PatAdmTool
     */
    public static PatAdmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PatAdmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PatAdmTool() {
        setModuleName("reg\\REGPatAdmModule.x");
        onInit();
    }

    /**
     * ��ѯ�Һ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {

        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ת�ղ�ѯ�Һ�������(��)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataNew(TParm parm) {
        String regionWhere = "";
        if (parm.getData("REGION_CODE") != null)
            regionWhere = " AND REGION_CODE = '" + parm.getData("REGION_CODE") +
                          "' ";
        String admTypeWhere = "";
        if (parm.getData("ADM_TYPE") != null)
            admTypeWhere = " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                           "' ";
        String roomWhere = "";
        if (parm.getData("CLINICROOM_NO") != null)
            roomWhere = " AND CLINICROOM_NO IN (" +
                        parm.getData("CLINICROOM_NO") + ") ";
        String sql =
            " SELECT ADM_TYPE, CASE_NO, MR_NO, REGION_CODE, ADM_DATE, REG_DATE,"+
            "        SESSION_CODE, CLINICAREA_CODE, CLINICROOM_NO, QUE_NO, REG_ADM_TIME,"+
            "        DEPT_CODE, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE, VISIT_CODE,"+
            "        REGMETHOD_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, TRANHOSP_CODE,"+
            "        TRIAGE_NO, CONTRACT_CODE, ARRIVE_FLG, REGCAN_USER, REGCAN_DATE,"+
            "        ADM_REGION, PREVENT_SCH_CODE, DRG_CODE, HEAT_FLG, ADM_STATUS,"+
            "        REPORT_STATUS, WEIGHT, HEIGHT, CLINICTYPE_CODE, VIP_FLG, OPT_USER,"+
            "        OPT_DATE, OPT_TERM, SERVICE_LEVEL "+
            "   FROM REG_PATADM "+
            "  WHERE ADM_DATE = TO_DATE('" +
            //===zhangp 20130225 start
//            StringTool.getString(parm.getTimestamp("ADM_DATE"), "YYYYMMDD") +
            parm.getTimestamp("ADM_DATE").toString().substring(0, 4) + parm.getTimestamp("ADM_DATE").toString().substring(5, 7) + parm.getTimestamp("ADM_DATE").toString().substring(8, 10) +
            //===zhangp 20130225 end
            "','yyyyMMdd') " +
            regionWhere+
            admTypeWhere+
            roomWhere;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = query("selDataNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�Һ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata_name(TParm parm) {

        TParm result = query("selectdata_name", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * �õ�������Ϣ
     * @param caseNo String
     * @return TParm
     */
    public TParm getInfoForCaseNo(String caseNo) {
        if (caseNo == null || caseNo.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        return query("getInfoForCaseNo", parm);
    }

    /**
     * �����Һ�
     * @param reg Reg �Һ�
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean newReg(Reg reg) {
        if (reg == null)
            return false;
        String newCaseNo = reg.caseNo();
        if (newCaseNo == null || newCaseNo.length() == 0)
            newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
                    "CASE_NO", "CASE_NO");
        String receiptNo = "";
        if ("N".equals(reg.getApptCode()))
            receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
                    "RECEIPT_NO",
                    "RECEIPT_NO");
        if (newCaseNo == null || newCaseNo.length() == 0) {
            err("-1 ȡ����Ŵ���!");
            return false;
        }
        reg.setCaseNo(newCaseNo); //1�������(REG)
        reg.getRegReceipt().setCaseNo(newCaseNo); //1�������(REG_RECEIPT)
        reg.getRegReceipt().setReceiptNo(receiptNo); //2�Һ��վ�(REG_RECEIPT)
        reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); //3Ʊ����ϸ���վݺ�(BIL_INVRCP)

        //�ż�������
        TParm regParm = reg.getParm();
        TParm saveParm = new TParm();
        saveParm.setData("REG", regParm.getData());
        saveParm.setData("ADM_TYPE", reg.getAdmType());
        //System.out.println("�Һ���������"+ regParm.getData());

        //�����վ�
        TParm regReceiptParm = reg.getRegReceipt().getParm();
        saveParm.setData("REG_RECEIPT", regReceiptParm.getData());

//        //Ʊ������  modify by huangtt 30140102
//        TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
//        saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

        //Ʊ����ϸ��
        TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
        bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
        saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
        saveParm.setData("PRINT_NO", reg.getRegReceipt().getPrintNo()); //===========pangben modify 20110819 �ж��Ƿ����,����Ϊ�ձ�ʾ����
        saveParm.setData("REASSURE_FLG",reg.getReassureFlg());//���ļ�
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "onNewReg", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * �����Һ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertInfo(TParm parm, TConnection connection) {
        TParm result = update("insertInfo", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �����Һ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     * ======pangben 2012-4-6 ���������ԤԼ�Һ�
     */
    public TParm insertInfoGreen(TParm parm, TConnection connection) {
        TParm result = update("insertInfoGreen", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    /**
     * ����Һ���Ϣ
     * @param reg Reg �ҺŶ���
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean onSave(Reg reg) {
        if (reg == null)
            return false;
        TParm parm = reg.getParm();
        if (!onSave(parm)) {
            return false;
        }
        return true;
    }

    /**
     * ����Һ���Ϣ
     * @param parm TParm
     * @return boolean
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
     * ����Һ���Ϣ(������,��̨ʹ��)
     * @param parm TParm
     * @param connection TConnection
     * @return boolean true �ɹ�  false ʧ��
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
     * ���ݲ���������ѯ�������
     * @param patName String
     * @return String
     */
    public String getCaseByPatName(String patName) {
        if (patName == null || patName.length() == 0)
            return null;
        TParm parm = new TParm();
        parm.setData("PAT_NAME", patName);
        TParm result = query("selcaseNoBypatName", parm);
        result.getValue("CASE_NO");
        return result.getValue("CASE_NO");
    }

    /**
     * ����ID�Ų�ѯʱ��β�����Ϣ
     * @param mrno String
     * @param StartTime Timestamp
     * @param endTime Timestamp
     * @param regionCode String
     * @return TParm
     */
    public TParm selDateByMrNo(String mrno, Timestamp StartTime,
                               Timestamp endTime, String regionCode) {
        TParm parm = new TParm();
        parm.setData("MR_NO", mrno);
        //=========pangben modify 20110421 start
        parm.setData("REGION_CODE", regionCode);
        //=========pangben modify 20110421 start
        parm.setData("STARTTIME", StringTool.setTime(StartTime, "00:00:00"));
        parm.setData("ENDTIME", StringTool.setTime(endTime, "23:59:59"));
//        parm.setData("ARRIVE_FLG", "Y");//===zhangp 20120816
        TParm result = query("selDateByMrNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����ID�Ų�ѯʱ��β�����Ϣ
     * @param mrno String
     * @param StartTime Timestamp
     * @param endTime Timestamp
     * @param admType String
     * @param regionCode String
     * @return TParm
     */
    public TParm selDateByMrNoAdm(String mrno, Timestamp StartTime,
                                  Timestamp endTime, String admType,
                                  String regionCode) {
        TParm parm = new TParm();
        parm.setData("MR_NO", mrno);
        parm.setData("STARTTIME", StringTool.setTime(StartTime, "00:00:00"));
        parm.setData("ENDTIME", StringTool.setTime(endTime, "23:59:59"));
        parm.setData("ADM_TYPE", admType);
        parm.setData("REGION_CODE", regionCode);
        TParm result = query("selDateByMrNoAdm", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����arrive_flg,adm_date,session_code,realdept_code,realdr_code��ѯ���첡����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForODO(TParm parm) {
        TParm result = query("selDateForODO", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�Һ�����(FOR OPD)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForODOByWait(TParm parm) {
        TParm regSysParm = REGSysParmTool.getInstance().selectdata();
        if (regSysParm.getBoolean("CHECKIN_FLG", 0)) {
            parm.setData("ARRIVE_FLG", "Y");
        }
        TParm result = query("selDateForODOByWait", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����ѯ�Һ�������FOR OPD��
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForODOEmgc(TParm parm) {
        TParm result = query("selDateForODOEmgc", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���ݾ������ڣ�ʱ�Σ��ż�ס��������ҽʦȡ�õ��պű��е���伯��
     * @param admDate String
     * @param sessionCode String
     * @param admType String
     * @param deptCode String
     * @param drCode String
     * @return TParm
     */
    public TParm getClinicRoomForODO(String admDate, String sessionCode,
                                     String admType, String deptCode,
                                     String drCode) {
        String sql =
                "SELECT DISTINCT A.CLINICROOM_NO AS ID,A.CLINICROOM_DESC AS NAME,ENG_DESC" +
                "		FROM REG_CLINICROOM A ,REG_PATADM B " +
                "		WHERE B.REALDR_CODE='" + drCode + "' " +
                "	  AND B.ADM_TYPE='" + admType + "'" +
                "	  AND B.ADM_DATE=TO_DATE('" + admDate + "','YYYY-MM-DD') " +
                "	  AND B.SESSION_CODE='" + sessionCode + "'" +
                " 	  AND A.CLINICROOM_NO=B.CLINICROOM_NO " +
                "     AND B.REALDEPT_CODE='" + deptCode + "'" +
                "     ORDER BY A.CLINICROOM_NO";
//        System.out.println("getClinicRoomForODO.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����������Ϣ��ѯ����
     * @param admDate String
     * @param sessionCode String
     * @param admType String
     * @param drCode String
     * @param dept String
     * @return TParm
     */
    public TParm getClinicRoomByRealDept(String admDate, String sessionCode,
                                         String admType, String drCode,
                                         String dept) {

        String sql =
                "SELECT DISTINCT A.CLINICROOM_NO AS ID,A.CLINICROOM_DESC AS NAME" +
                "		FROM REG_CLINICROOM A ,REG_PATADM B " +
                "		WHERE B.REALDR_CODE='" + drCode + "' " +
                "	  AND B.ADM_TYPE='" + admType + "'" +
                "	  AND B.ADM_DATE=TO_DATE('" + admDate + "','YYYY-MM-DD') " +
                "	  AND B.REALDEPT_CODE='" + dept + "'" +
                "	  AND B.SESSION_CODE='" + sessionCode + "'" +
                " 	  AND A.CLINICROOM_NO=B.CLINICROOM_NO  ORDER BY A.CLINICROOM_NO";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����������Ϣ��ѯ��������µ�ҽʦ
     * @param admType String
     * @param sessionCode String
     * @param admDate String
     * @param dept String
     * @return TParm
     */
    public TParm getInsteadDrByDept(String admType, String sessionCode,
                                    String admDate, String dept) {
        String sql =
                " SELECT DISTINCT B.USER_ID AS ID,B.USER_NAME AS NAME,B.PY1 AS PY1,USER_ENG_NAME "
                + "	FROM SYS_OPERATOR B,REG_PATADM A "
                + "	WHERE A.ADM_DATE=TO_DATE('" + admDate + "','YYYY-MM-DD') " +
                "		  AND A.REALDEPT_CODE='" + dept + "'" +
                "		  AND B.USER_ID=A.REALDR_CODE  ORDER BY ID,NAME,PY1";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����������Ϣ���Ҵ������
     * @param admType String
     * @param admDate String
     * @param sessionCode String
     * @param regionCode String
     * @return TParm
     */
    public TParm getInsteadDept(String admType, String admDate,
                                String sessionCode, String regionCode) {
        String whereAdmType = "";
        if (!"".equals(admType))
            whereAdmType = " AND A.ADM_TYPE = '" + admType + "' ";
        String whereSessionCode = "";
        if (!"".equals(sessionCode))
            whereSessionCode = " AND A.SESSION_CODE = '" + sessionCode + "' ";
        String whereRegionCode = "";
        if (!"".equals(regionCode))
            whereRegionCode = " AND A.REGION_CODE = '" + regionCode + "' ";
        String sql = "";
//    	System.out.println("language="+Operator.getLanguage());
        if ("zh".equalsIgnoreCase(Operator.getLanguage())) {
            sql =
                    " SELECT DISTINCT B.DEPT_CODE AS ID,B.DEPT_CHN_DESC AS NAME,B.PY1 AS PY1 " +
                    "   FROM SYS_DEPT B,REG_PATADM A " +
                    "  WHERE A.ADM_DATE=TO_DATE('" + admDate +
                    "','YYYY-MM-DD') " +
                    "    AND A.REGION_CODE = B.REGION_CODE " +
                    "    AND A.REALDEPT_CODE=B.DEPT_CODE  " +
                    whereAdmType +
                    whereSessionCode +
                    whereRegionCode +
                    "  ORDER BY ID,NAME,PY1";
        } else {
            sql =
                    " SELECT DISTINCT B.DEPT_CODE AS ID,B.DEPT_ENG_DESC AS NAME,B.PY1 AS PY1 " +
                    "   FROM SYS_DEPT B,REG_PATADM A " +
                    "  WHERE A.ADM_DATE=TO_DATE('" + admDate +
                    "','YYYY-MM-DD') " +
                    "    AND A.REGION_CODE = B.REGION_CODE " +
                    "    AND A.REALDEPT_CODE=B.DEPT_CODE  " +
                    whereAdmType +
                    whereSessionCode +
                    whereRegionCode +
                    "  ORDER BY ID,NAME,PY1";
        }
//    	System.out.println("getInsteadDept.sql="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ȡ�����������Ĵ���ҽʦ�Ĳ���
     * @param admDate String
     * @param sessionCode String
     * @param drCode String
     * @param admType String
     * @param deptCode String
     * @return TParm
     */
    public TParm getInsteadPatList(String admDate, String sessionCode,
                                   String drCode, String admType,
                                   String deptCode) {
        String sql = "";
        //=============pangben 2012-6-28 start ��ӳ�������ʾ�ֶ�
        //=============huangtt 20131224  ����������ֶ���ʾ   A.FIRST_FLG
      //=============sukk 20190421  ��Ӱ��ļ��ֶ���ʾ   A.REASSURE_FLG
        if ("O".equalsIgnoreCase(admType)) {
        	
        	//add by huangtt 20141029 start
        	String sessionWhere ="";
        	if(sessionCode.length()>0){
        		 sessionWhere = " AND A.SESSION_CODE ='"+sessionCode+"'";
        	}
        	//add by huangtt 20141029 end
        	
            sql = "SELECT A.QUE_NO AS QUE_NO, A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME,A.ADM_DATE, A.REALDEPT_CODE ,A.CLINICROOM_NO,A.REASSURE_FLG," +
                  "	   A.ADM_STATUS AS ADM_STATUS, A.REPORT_STATUS AS REPORT_STATUS," +
                  "	   A.CASE_NO AS CASE_NO ,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,B.BIRTH_DATE, A.REALDR_CODE," +
                  "	   B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,B.PREMATURE_FLG," +
                  "	   B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,B.BREASTFEED_ENDDATE,A.ADM_TYPE,B.PAT_NAME1,A.VISIT_CODE,A.FIRST_FLG " +
                  ",B.HIGHRISKMATERNAL_FLG " + //add by huangtt 20161008 ��Σ������ʶ�ֶ�
                  "	FROM REG_PATADM A, SYS_PATINFO B  " +
                  "	WHERE A.ADM_DATE = TO_DATE ('" + admDate +
                  "', 'YYYY-MM-DD') " +
                  "		  AND A.ARRIVE_FLG = 'Y'" +
                  "		  AND REALDR_CODE='" + drCode + "'" +
                  sessionWhere+  //add by huangtt 20141029
                  "		  AND B.MR_NO=A.MR_NO " +
                  "		  AND A.REGCAN_USER IS NULL ";
            if (deptCode.length() > 0) {
                sql += " AND REALDEPT_CODE='" + deptCode + "'";
            }
        } else {
            sql = "SELECT A.QUE_NO AS QUE_NO, A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME,A.ADM_DATE, A.REALDEPT_CODE ,A.CLINICROOM_NO," +
                  "	   A.ADM_STATUS AS ADM_STATUS, A.REPORT_STATUS AS REPORT_STATUS," +
                  "	   A.CASE_NO AS CASE_NO ,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,B.BIRTH_DATE, A.REALDR_CODE," +
                  "	   B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,B.PREMATURE_FLG," +
                  //"	   B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,B.BREASTFEED_ENDDATE,A.ADM_TYPE,B.PAT_NAME1,A.ERD_LEVEL,MAX(ORDER_DATE) AS ORDER_DATE ,D.TIME_LIMIT,B.PAT_NAME1,A.VISIT_CODE " +
                  "	   B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,B.BREASTFEED_ENDDATE,A.ADM_TYPE,B.PAT_NAME1" +
                  ",A.ERD_LEVEL,MAX(ORDER_DATE) AS ORDER_DATE ,D.TIME_LIMIT,B.PAT_NAME1,A.VISIT_CODE,A.DISE_CODE,A.FIRST_FLG " +
                  ",B.HIGHRISKMATERNAL_FLG " + //add by huangtt 20161008 ��Σ������ʶ�ֶ�
                  //modify by wanglong 20121025
                  "	FROM REG_PATADM A, SYS_PATINFO B ,OPD_ORDER C,REG_ERD_LEVEL D " +
                  "	WHERE A.ADM_DATE = TO_DATE ('" + admDate +
                  "', 'YYYY-MM-DD') " +
                  "		  AND A.ARRIVE_FLG = 'Y'";
            if (deptCode.length() > 0) {
                sql += " AND REALDEPT_CODE='" + deptCode + "'";
            }
            if (drCode.length() > 0) {
                sql += " AND REALDR_CODE='" + drCode + "'";
            }
          //add by huangtt 20141209
            if (admType.length() > 0) {
                sql += " AND A.ADM_TYPE='" + admType + "'";
            }
            sql += "		  AND B.MR_NO=A.MR_NO " +
                    "		  AND A.REGCAN_USER IS NULL " +
                    "   AND A.CASE_NO=C.CASE_NO (+) " +
                    "   AND A.ERD_LEVEL=D.LEVEL_CODE (+) " +
                    "   GROUP BY A.QUE_NO,A.MR_NO,B.PAT_NAME,A.REG_DATE,A.REALDEPT_CODE," +
                    "		     A.CLINICROOM_NO,A.REALDR_CODE,A.ADM_TYPE,A.ADM_STATUS,A.REPORT_STATUS," +
                    "		     A.REGION_CODE,A.CASE_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE," +
                    "		     B.BIRTH_DATE,B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT," +
                    "		     B.PREMATURE_FLG,B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE," +
                    "		     B.BREASTFEED_ENDDATE,A.ERD_LEVEL,D.TIME_LIMIT,B.PAT_NAME1,A.ADM_DATE,A.VISIT_CODE,A.DISE_CODE,A.FIRST_FLG " +
                    "			 ,B.HIGHRISKMATERNAL_FLG " +
                    
                    //"		     B.BREASTFEED_ENDDATE,A.ERD_LEVEL,D.TIME_LIMIT,B.PAT_NAME1,A.ADM_DATE,A.VISIT_CODE,A.DISE_CODE " +
                    //modify by wanglong 20121025
                    "		    ORDER BY A.CASE_NO ";
        }
      //=============pangben 2012-6-28 stop
//        System.out.println("sql============="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���¹Һ�����(FOR ONW)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateInfoForONW(TParm parm, TConnection conn) {
        TParm result = update("updateInfoForONW", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ID��,�������ڲ�ѯ����������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selPatInfoForREG(TParm parm) {
        TParm result = query("selPatInfoForREG", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �˹�д���˹���Ա,�˹�����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateForUnReg(TParm parm, TConnection connection) {
        TParm result = update("updateForUnReg", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���������ֶμ�������ҽ���
     * @param regionCode String
     * @param admType String
     * @param admDate String
     * @param sessionCode String
     * @param clinicRoomNo String
     * @return TParm
     */
    public TParm getWestMediFlg(String regionCode, String admType,
                                String admDate, String sessionCode,
                                String clinicRoomNo) {

        String sql = "SELECT WEST_MEDI_FLG " +
                     "	   FROM REG_SCHDAY " +
                     "	   WHERE REGION_CODE='" + regionCode + "' " +
                     "			 AND ADM_DATE='" + admDate + "' " +
                     "			 AND CLINICROOM_NO='" + clinicRoomNo + "'";
//        System.out.println("getWestMediFlg.sql-="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * �˹Ҳ���
     * @param parm TParm
     * @return TParm
     */
    public TParm onUnReg(TParm parm) {
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "onUnReg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���±������λ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateForArrive(TParm parm, TConnection conn) {
        TParm result = update("updateForArrive", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ʱ��β�ѯ�Һ�����ͳ�Ʊ�ƽ������(FOR MEDTIIS)
     * @param sTime String
     * @param eTime String
     * @return TParm
     */
    public TParm selSummaryPersonO(String sTime, String eTime) {
        TParm parm = new TParm();
        parm.setData("S_TIME", sTime);
        parm.setData("E_TIME", eTime);
        TParm result = query("selSummaryPersonO", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ʱ��β�ѯ�Һ�����ͳ�Ʊ�������(FOR MEDTIIS)
     * @param sTime String
     * @param eTime String
     * @return TParm
     */
    public TParm selSummaryPersonE(String sTime, String eTime) {
        TParm parm = new TParm();
        parm.setData("S_TIME", sTime);
        parm.setData("E_TIME", eTime);
        TParm result = query("selSummaryPersonE", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ʱ��β�ѯ�Һ�����ͳ�Ʊ��˷�����(FOR MEDTIIS)
     * @param sTime String
     * @param eTime String
     * @return TParm
     */
    public TParm selSummaryPersonReturn(String sTime, String eTime) {
        TParm parm = new TParm();
        parm.setData("S_TIME", sTime);
        parm.setData("E_TIME", eTime);
        TParm result = query("selSummaryPersonReturn", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ͨ��mr_no�õ����case_no
     * @param mrNo String
     * @param regionCode String
     * @return TParm
     */
    public TParm selMaxCaseNoByMrNo(String mrNo, String regionCode) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("MR_NO", mrNo);
        parm.setData("REGION_CODE", regionCode);
        result = this.query("selMaxCaseNoByMrNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���»�ʿվ���,����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateWHForONW(TParm parm) {
        TParm result = new TParm();
        result = this.query("updateWHForONW", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���������Ŀ��˹ҵ�ԤԼ�Һ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectUnAdmData(TParm parm) {
        String sessionWhere = " AND A.SESSION_CODE = '" +
                              parm.getValue("SESSION_CODE") + "' ";
        String clinicRoomWhere = "    AND A.CLINICROOM_NO = '" +
                                 parm.getValue("CLINICROOM") + "' ";
        String regionWhere = "";
        if (parm.getData("REGION") != null)
            regionWhere = " AND A.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") + "' ";
        String sql =
                " SELECT 'N' AS FLG,B.PAT_NAME,A.REGION_CODE,A.ADM_TYPE,A.ADM_DATE," +
                "        A.SESSION_CODE,A.CLINICROOM_NO,A.QUE_NO,A.CASE_NO,A.MR_NO,C.CLINICROOM_DESC " +
                " ,A.REG_ADM_TIME,A.REALDEPT_CODE,A.REALDR_CODE" +
                "   FROM REG_PATADM A, SYS_PATINFO B,REG_CLINICROOM C " +
                "  WHERE A.MR_NO = B.MR_NO " +
                "    AND A.APPT_CODE = 'Y' " +
                "    AND A.ARRIVE_FLG = 'N' " +
                "    AND A.SEE_DR_FLG = 'N' " +
                "    AND A.REGCAN_USER IS NULL " +
                "    AND A.CLINICROOM_NO = C.CLINICROOM_NO " +
                "    AND A.ADM_DATE = TO_DATE('" + parm.getValue("ADM_DATE") +
                "','yyyyMMdd')" +
                regionWhere;

        if (parm.getValue("SESSION_CODE").toString().length() != 0)
            sql = sql + sessionWhere;
        if (parm.getValue("CLINICROOM").toString().length() != 0)
            sql = sql + clinicRoomWhere;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ԤԼ�Һ� �˹� �޸�REG_PATADM��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateUnAdmREG_PATADM(TParm parm, TConnection conn) {
        String sql = " UPDATE REG_PATADM " +
                     "    SET REGCAN_USER  = '" + parm.getValue("REGCAN_USER") +
                     "', REGCAN_DATE = SYSDATE, " +
                     "  OPT_USER='" + parm.getValue("OPT_USER") + "'," +
                     "  OPT_TERM='" + parm.getValue("OPT_TERM") + "'," +
                     "  OPT_DATE=SYSDATE " +
                     "  WHERE  CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ԤԼ�Һ� �˹� �޸�REG_CLINICQUE
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateUnAdmREG_CLINICQUE(TParm parm, TConnection conn) {
        String sql = " UPDATE REG_CLINICQUE " +
                     "    SET QUE_STATUS = 'N', " +
                     "  OPT_USER='" + parm.getValue("OPT_USER") + "'," +
                     "  OPT_TERM='" + parm.getValue("OPT_TERM") + "'," +
                     "  OPT_DATE=SYSDATE " +
                     "  WHERE ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "' " +
                     "    AND ADM_DATE = '" + parm.getValue("ADM_DATE") + "'" +
                     "    AND SESSION_CODE = '" + parm.getValue("SESSION_CODE") +
                     "' " +
                     "    AND CLINICROOM_NO = '" +
                     parm.getValue("CLINICROOM_NO") + "' " +
                     "    AND QUE_NO = '" + parm.getValue("QUE_NO") + "' ";
//        System.out.println("sql:"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ҽ�ƿ��˹�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateUnAdmReg(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.updateUnAdmREG_PATADM(parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        result = this.updateUnAdmREG_CLINICQUE(parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�Һ��վݴ�ӡ����
     * @param CASE_NO String
     * @param COPY String
     * @return TParm
     */
    public TParm getRegPringDate(String CASE_NO, String COPY) {
        TParm result = new TParm();
        String sql =
                "SELECT  B.DEPT_CODE,A.CASE_NO,B.DEPT_CHN_DESC,A.QUE_NO,A.MR_NO,C.USER_NAME,ADM_DATE, " +
                " D.PAT_NAME,E.CHN_DESC AS SEX,D.BIRTH_DATE,F.CTZ_DESC,A.CLINICTYPE_CODE,A.ADM_TYPE, " +
                " A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.SERVICE_LEVEL,G.DESCRIPTION AS AREA_DESCRIPTION, " +
                " H.SESSION_DESC,I.CLINICROOM_DESC " +
                " FROM REG_PATADM A,SYS_DEPT B,SYS_OPERATOR C,SYS_PATINFO D,SYS_DICTIONARY E," +
                "      SYS_CTZ F,REG_CLINICAREA G,REG_SESSION H,REG_CLINICROOM I " +
                " WHERE A.DEPT_CODE=B.DEPT_CODE " +
                " AND A.DR_CODE=C.USER_ID " +
                " AND A.MR_NO=D.MR_NO " +
                " AND A.CLINICAREA_CODE = G.CLINICAREA_CODE(+) " +
                " AND A.ADM_TYPE = H.ADM_TYPE " +
                " AND A.CLINICROOM_NO = I.CLINICROOM_NO " +
                " AND A.SESSION_CODE = H.SESSION_CODE " +
                " AND E.GROUP_ID='SYS_SEX' " +
                " AND D.SEX_CODE=E.ID(+) " +
                " AND A.CTZ1_CODE=F.CTZ_CODE(+) " +
                " AND A.CASE_NO='" + CASE_NO + "' ";
        TParm reg = new TParm();
        reg.setData(TJDODBTool.getInstance().select(sql));
        String sessionDesc = reg.getValue("SESSION_DESC", 0);
        String adm_type = reg.getValue("ADM_TYPE", 0); //�ż�ס��
        String CLINICTYPE_CODE = reg.getValue("CLINICTYPE_CODE", 0); //�ű�
        String level = reg.getValue("SERVICE_LEVEL", 0); //����ȼ�
        String levelSql =
                " SELECT CHN_DESC AS NAME " +
                "   FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SERVICE_LEVEL' AND ID = '" +
                level + "' ";
        TParm levelParm = new TParm(TJDODBTool.getInstance().select(levelSql));
        String levelDesc = levelParm.getValue("NAME", 0);
        String roomDesc = reg.getValue("CLINICROOM_DESC", 0);
        //��ѯ����
//        double clinic_fee = BIL.getRegDetialFee(adm_type, CLINICTYPE_CODE,
//                                                "CLINIC_FEE", level);
        //��ѯ���ѵ�ORDER_CODE
//        TParm order1 = PanelTypeFeeTool.getInstance().getOrderCodeDetial(
//                adm_type, CLINICTYPE_CODE, "CLINIC_FEE");
        //��ѯ���ѵ�����ۿ�
//        double ownrate1 = BIL.getOwnRate(reg.getValue("CTZ1_CODE", 0),
//                                         reg.getValue("CTZ2_CODE", 0),
//                                         reg.getValue("CTZ3_CODE", 0),
//                                         SysFee.getChargeHospCode(order1.
//                getValue("ORDER_CODE", 0)));
        //��ѯ�Һŷ�
//        double reg_fee = BIL.getRegDetialFee(adm_type, CLINICTYPE_CODE,
//                                             "REG_FEE", level);
        //��ѯ�Һŷѵ�ORDER_CODE
//        TParm order2 = PanelTypeFeeTool.getInstance().getOrderCodeDetial(
//                adm_type, CLINICTYPE_CODE, "REG_FEE");
        //��ѯ�Һŷѵ�����ۿ�
//        double ownrate2 = BIL.getOwnRate(reg.getValue("CTZ1_CODE", 0),
//                                         reg.getValue("CTZ2_CODE", 0),
//                                         reg.getValue("CTZ3_CODE", 0),
//                                         SysFee.getChargeHospCode(order2.
//                getValue("ORDER_CODE", 0)));
        DecimalFormat df = new DecimalFormat("0.00");
        result.setData("CLINICROOM_DESC_OPB", roomDesc); //����ForOPB
        result.setData("REGFEE", df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level))); //�Һŷ�
        result.setData("CLINICFEE", df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level))); //����
        result.setData("DR_CODE_OPB", reg.getValue("USER_NAME", 0)); //ҽʦForOPB
        result.setData("QUE_NO_OPB", reg.getValue("QUE_NO", 0)); //���ForOPB
        result.setData("DEPT_CODE_OPB", reg.getValue("DEPT_CHN_DESC", 0)); //����ForOPB
        result.setData("DEPT", "TEXT",
                       reg.getValue("DEPT_CHN_DESC", 0) + " " + roomDesc); //����
        result.setData("CASE_NO", "TEXT", reg.getValue("CASE_NO", 0)); //�����
        result.setData("SEQ", "TEXT", reg.getValue("QUE_NO", 0)); //˳���
        result.setData("MR_NO", "TEXT", reg.getValue("MR_NO", 0)); //ID��
        result.setData("DR_CODE", "TEXT", reg.getValue("USER_NAME", 0)); //ҽʦ
        result.setData("ADM_DATE", "TEXT",
                       StringTool.getString(reg.getTimestamp("ADM_DATE", 0),
                                            "dd/MM/yyyy") + " " + sessionDesc); //�Һ�����
        result.setData("PAT_NAME", "TEXT", reg.getValue("PAT_NAME", 0)); //��������
        result.setData("SEX", "TEXT", reg.getValue("SEX", 0)); //�Ա�
        result.setData("AGE", "TEXT",
                       StringUtil.showAge(reg.getTimestamp("BIRTH_DATE", 0),
                                          reg.getTimestamp("ADM_DATE", 0))); //����
        result.setData("CTZ1_CODE", "TEXT", reg.getValue("CTZ_DESC", 0));
        result.setData("REG_FEE", "TEXT",
                       df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)) + "(Ԫ)"); //�Һŷ�
        result.setData("CLINIC_FEE", "TEXT",
                       df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)) + "(Ԫ)"); //����
        result.setData("TOTAL", "TEXT", df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level) +
                                                  BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)));
        result.setData("Overall_pay", "TEXT", "0.00");
        result.setData("Individual_pay", "TEXT",
                       df.format(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level) +
                                 BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level)));
        result.setData("cash", "TEXT", "0.00");
        result.setData("CASH", "TEXT", "" + StringUtil.getInstance().
                       numberToWord(StringTool.
                                    round(BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "REG_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level) +
                                          BIL.getRegDetialFee(adm_type,
                CLINICTYPE_CODE,
                "CLINIC_FEE",
                reg.getValue("CTZ1_CODE", 0),
                reg.getValue("CTZ2_CODE", 0),
                reg.getValue("CTZ3_CODE", 0), level), 2)));
        result.setData("REG_USER", "TEXT", Operator.getName());
        result.setData("PRINT_DATE", "TEXT",
                       StringTool.getString(SystemTool.getInstance().getDate(),
                                            "yyyy/MM/dd"));

        result.setData("PRINT_TIME", "TEXT",
                       StringTool.getString(SystemTool.getInstance().getDate(),
                                            "HH:mm:ss"));
        //����ȼ�
        result.setData("SERVICE_LEVEL", "TEXT", levelDesc);
        //������ע
        result.setData("AREA_DESCRIPTION", "TEXT",
                       reg.getValue("AREA_DESCRIPTION", 0));
        result.setData("REGION_CODE", "TEXT",
                       Manager.getOrganization().
                       getHospitalCHNFullName(Operator.getRegion()));
        result.setData("TITLE_ENGLISH", "TEXT",
                       Manager.getOrganization().
                       getHospitalENGFullName(Operator.getRegion()));
        result.setData("COPY", "TEXT", COPY);
        //System.out.println("result---"+result);
        return result;
    }

    /**
     * �����հ����Ϣ ��ѯÿ���հ�ҽʦ�ĹҺ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getSchDayRegInfo(TParm parm) {
        String sql =
                "SELECT CASE WHEN REGCAN_DATE IS NULL THEN 'N' ELSE 'Y' END AS REGCAN_FLG, " +
                " A.QUE_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.TEL_HOME,A.CASE_NO,A.REGION_CODE,C.SESSION_DESC " +
                " FROM REG_PATADM A,SYS_PATINFO B,REG_SESSION C " +
                " WHERE A.MR_NO=B.MR_NO " +
                " AND A.SESSION_CODE=C.SESSION_CODE " +
                " AND A.REGION_CODE='" + parm.getValue("REGION_CODE") + "' " +
                " AND A.ADM_TYPE='" + parm.getValue("ADM_TYPE") + "' " +
                " AND A.ADM_DATE=TO_DATE('" + parm.getValue("ADM_DATE") +
                "','YYYYMMDD') " +
                " AND A.SESSION_CODE='" + parm.getValue("SESSION_CODE") + "' " +
                " AND A.CLINICROOM_NO='" + parm.getValue("CLINICROOM_NO") +
                "' AND REGCAN_USER IS NULL OR REGCAN_USER=''" +
                " ORDER BY QUE_NO ";
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����Һ�(ҽ�ƿ�����ʹ��)
     * @param reg Reg �Һ�
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean newRegForEKT(Reg reg) {
        if (reg == null)
            return false;
        String newCaseNo = reg.caseNo();
        String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
                "RECEIPT_NO",
                "RECEIPT_NO");
        if (newCaseNo == null || newCaseNo.length() == 0) {
            err("-1 ȡ����Ŵ���!");
            return false;
        }
        reg.setCaseNo(newCaseNo); //1�������(REG)
        reg.getRegReceipt().setCaseNo(newCaseNo); //1�������(REG_RECEIPT)
        reg.getRegReceipt().setReceiptNo(receiptNo); //2�Һ��վ�(REG_RECEIPT)
        reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); //3Ʊ����ϸ���վݺ�(BIL_INVRCP)

        //�ż�������
        TParm regParm = reg.getParm();
        TParm saveParm = new TParm();
        saveParm.setData("REG", regParm.getData());

        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
                "onNewRegForEKT", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    //������
    public static void main(String args[]) {
        JavaHisDebug.initClient();
//        System.out.println("�������" +
//                           PatAdmTool.getInstance().getCaseByPatName("����"));
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", "O");
        parm.setData("ADM_DATE", "2008-10-16");
        parm.setData("SESSION_CODE", "2");
        parm.setData("DEPT_CODE", "201004");
        parm.setData("DR_CODE", "admin");
        parm.setData("SEE_DR", "Y");
        //System.out.println(PatAdmTool.getInstance().selDateForODOByWait(parm));

    }

    /**
     * ͨ��mr_no�õ�case_no pangben 20111009
     * @param parm TParm
     * @return TParm
     */
    public TParm selEKTByMrNo(TParm parm) {
        TParm result = new TParm();
        result = this.query("selEKTByMrNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �Һ�����ҽ�ƿ���ɫͨ������޸�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * =================pangben 20111009
     */
    public TParm updateEKTGreen(TParm parm, TConnection conn) {
        TParm result = update("updateEKTGreen", parm, conn);
        return result;
    }

    /**
     * ҽ�ƿ���ɫͨ���ۿ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * =================pangben 20111009
     */
    public TParm updateEKTGreen1(TParm parm, TConnection conn) {
        TParm result = update("updateEKTGreen1", parm, conn);
        return result;
    }
}
