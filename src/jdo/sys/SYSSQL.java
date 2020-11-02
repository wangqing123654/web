package jdo.sys;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: SYS����SQL��װ 
 * </p>
 *
 * <p> 
 * Description: SYS����SQL��װ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.06.04
 * @version 1.0
 */
public class SYSSQL {
    /**
     * ȡ�� ��תԺ���ֵ�
     * @return String
     */
    public static String getSYSTrnHosp() {
        return "SELECT HOSP_CODE,HOSP_DESC,PY1,PY2,"
                + "SEQ,DESCRIPTION,HOSP_TYPE,"
                + "OPT_USER,OPT_DATE,OPT_TERM FROM SYS_TRN_HOSP ORDER BY SEQ ,HOSP_CODE ";
    }

    /**
     * ȡ�ô�λ�����ֵ�
     * @return String
     */
    public static String getSYSBedFee() {
        return "SELECT BED_CLASS_CODE,ORDER_CODE,CODE_CALC_KIND,PY1,PY2,"
                + "SEQ,DESCRIPTION,CHK_OUT_TIME,START_DATE,END_DATE,"
                + "OPT_USER,OPT_DATE,OPT_TERM,BED_OCCU_FLG FROM SYS_BEDFEE";
    }

    /**
     * ȡ�ô�λ������Ϣ(���:�����ȼ�)
     * @param bed_class_code String
     * @return String
     */
    public static String getSYSBedFee(String bed_class_code) {
        return "SELECT A.BED_CLASS_CODE, A.ORDER_CODE, B.OWN_PRICE, A.CHK_OUT_TIME,A.CODE_CALC_KIND "
                + "FROM SYS_BEDFEE A, SYS_FEE B "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE "
//            + "AND SYSDATE BETWEEN TO_DATE(A.START_DATE , 'yyyy/MM/dd') AND TO_DATE(A.END_DATE , 'yyyy/MM/dd') "
                + "AND A.BED_CLASS_CODE = '" + bed_class_code + "'";
    }
    /**
     * ȡ�ô�λ������Ϣ(���:�����ȼ�,ռ��ע��)
     * @param bedClassCode String
     * @param bedOccuFlg String
     * @return String
     */
    public static String getSYSBedFeeOccu(String bedClassCode,
                                          String bedOccuFlg) {
        return
                " SELECT A.BED_CLASS_CODE, A.ORDER_CODE, B.OWN_PRICE, A.CHK_OUT_TIME,A.CODE_CALC_KIND " +
                "   FROM SYS_BEDFEE A, SYS_FEE B " +
                "  WHERE A.ORDER_CODE = B.ORDER_CODE " +
                "    AND A.BED_CLASS_CODE = '" + bedClassCode + "'" +
                "    AND A.BED_OCCU_FLG = '" + bedOccuFlg + "'";
    }

    /**
     * ����ҩƷ����ȡ��SYS_FEE�ֵ�
     * @param order_code String
     * @return String
     */
    public static String getSYSFee(String order_code) {
        return "SELECT ORDER_CODE, ORDER_DESC, PY1, PY2, SEQ, "
                +
                "DESCRIPTION, TRADE_ENG_DESC, GOODS_DESC, GOODS_PYCODE, ALIAS_DESC, "
                +
                "ALIAS_PYCODE, SPECIFICATION, NHI_FEE_DESC, HABITAT_TYPE, MAN_CODE, "
                +
                "HYGIENE_TRADE_CODE, ORDER_CAT1_CODE, CHARGE_HOSP_CODE, OWN_PRICE, NHI_PRICE, "
                +
                "GOV_PRICE, UNIT_CODE, LET_KEYIN_FLG, DISCOUNT_FLG, EXPENSIVE_FLG, "
                +
                "OPD_FIT_FLG, EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, "
                +
                "INTV_ORDER_FLG, LCS_CLASS_CODE, TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, "
                +
                "EXEC_ORDER_FLG, EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, ADDPAY_AMT, "
                +
                "NHI_CODE_O, NHI_CODE_E, NHI_CODE_I, CTRL_FLG, CLPGROUP_CODE, "
                +
                "ORDERSET_FLG, INDV_FLG, SUB_SYSTEM_CODE, RPTTYPE_CODE, DEV_CODE, CAT1_TYPE, "
                + "OPTITEM_CODE, MR_CODE, DEGREE_CODE, CIS_FLG, OWN_PRICE2, OWN_PRICE3, TUBE_TYPE, ATC_FLG, IS_REMARK, ACTION_CODE "
                + "FROM SYS_FEE WHERE ORDER_CODE = '" + order_code + "'";
    }

    /**
     * ȡ�ô�Ⱦ������ֵ�
     * @return String
     */
    public static String getSYSDiseaseType() {
        return
                "SELECT DISEASETYPE_CODE, DISEASETYPE_DESC, ENG_DESC, PY1, PY2, SEQ, "
                +
                "DEADLINE, DEADLINE_DESC, LEGALINFECT_FLG, DESCRIPTION, OPT_USER, "
                + "OPT_DATE, OPT_TERM FROM SYS_DISEASETYPE";
    }

    /**
     * ȡ�ÿ����صȼ��ֵ�
     * @return String
     */
    public static String getSYSAntibiotic() {
        return
                "SELECT ANTIBIOTIC_CODE, ANTIBIOTIC_DESC,ENG_DESC, PY1, PY2, TAKE_DAYS, "
                + "MR_CODE, DESCRIPTION, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_ANTIBIOTIC" +
                  "ORDER BY ANTIBIOTIC_CODE ";
    }

    /**
     * ҽ����λ
     * @return String
     */
    public static String getSYSNhiCpmpany() {
        return "SELECT NHI_COMPANY_CODE, NHI_COMPANY_DESC, PY1, PY2, SEQ, "
                + "CONTACT_PERSON, TEL, E_MAIL, ADDRESS, INS_VIEW, "
                + "DESCRIPTION, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_NHI_COMPANY";
    }

    /**
     * �ն˻�
     * @return String
     */
    public static String getSYSTerminal() { 
        return "SELECT TERM_NO, TERM_CHN_DESC, TERM_ENG_DESC, PY1, PY2, "
                + "SEQ, TERM_IP, TEL_EXT, LOC_CODE, DESCRIPTION, "
                + "PRINTER_NO, DESK_NO, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_TERMINAL";
    }

    /**
     * ��������(�������)
     * @return String
     */
    public static String getSYSManufacturer() {
        return
                "SELECT MAN_CODE, MAN_CHN_DESC, MAN_ENG_DESC, MAN_ABS_DESC, PY1, "
                + "PY2, SEQ, DESCRIPTION, NATIONAL_CODE, POST_CODE, "
                + "ADDRESS, TEL, FAX, WEBSITE, E_MAIL, "
                + "PHA_FLG, MAT_FLG, DEV_FLG, OTHER_FLG, OPT_USER, "
                +
                "OPT_DATE, OPT_TERM FROM SYS_MANUFACTURER ORDER BY MAN_CODE ,SEQ";
    }

    /**
     * ��������(�����)
     * @param man_code String
     * @return String
     */
    public static String getSYSManufacturer(String man_code) {
        return
                "SELECT MAN_CODE, MAN_CHN_DESC, MAN_ENG_DESC, MAN_ABS_DESC, PY1, "
                + "PY2, SEQ, DESCRIPTION, NATIONAL_CODE, POST_CODE, "
                + "ADDRESS, TEL, FAX, WEBSITE, E_MAIL, "
                + "PHA_FLG, MAT_FLG, DEV_FLG, OTHER_FLG, OPT_USER, "
                + "OPT_DATE, OPT_TERM FROM SYS_MANUFACTURER "
                + "WHERE MAN_CODE = '" + man_code
                + "' ORDER BY MAN_CODE ,SEQ";
    }

    /**
     * ��Ӧ����
     * @return String  
     */
    public static String getSYSSupplier() {
        return  "SELECT SUP_CODE, SUP_CHN_DESC, SUP_ENG_DESC, SUP_ABS_DESC, PY1, "
                + "PY2, SEQ, DESCRIPTION, PHA_FLG, MAT_FLG, "
                + "DEV_FLG, OTHER_FLG, SUP_BOSSNAME, SUP_IDNO, SUP_TEL, "
                + "SUP_FAX, NATIONAL_CODE, POST_CODE, ADDRESS, E_MAIL, "
                + "WEBSITE, BANK_CODE, BANK_IDNO, BANK_NAME, SUP_SALES1, "
                + "SUP_SALES1_TEL, SUP_SALES1_EMAIL, SUP_SALES2, SUP_SALES2_TEL, SUP_SALES2_EMAIL, "
                +"SUP_SALES3, SUP_SALES3_TEL, SUP_SALES3_EMAIL, SUP_STOP_FLG, SUP_STOP_DATE, "
                + "SUP_END_DATE, OPT_USER, OPT_DATE, OPT_TERM, SELL_DEPT_CODE "
                + "FROM SYS_SUPPLIER ORDER BY SUP_CODE , SEQ";
    }

    /**
     * ������
     * @return String
     */
    public static String getSYSHomeplace() {
        return "SELECT HOMEPLACE_CODE, HOMEPLACE_DESC, PY1, PY2, DESCRIPTION, "
                + "SEQ, OPT_USER, OPT_DATE, OPT_TERM "  
                + "FROM SYS_HOMEPLACE ORDER BY HOMEPLACE_CODE,SEQ  ";
    }

    /** 
     * ����������
     * @param regionCode String
     * @return String
     */
    public static String getSYSEmrIndex(String regionCode) {
        //===========pangben modify 20110609 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " WHERE REGION_CODE='" + regionCode + "' ";
        //===========pangben modify 20110609 stop
        return
                "SELECT CASE_NO, ADM_TYPE, REGION_CODE, IPD_NO, MR_NO, ADM_DATE, DEPT_CODE, "
                + "DR_CODE, DS_DATE, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_EMR_INDEX" + region;
    }

    /**
     * �û�����
     * @param regionCode String
     * @return String
     */
    public static String getSYSOperator(String regionCode) {
        //==================pangben modify 20110524 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " WHERE REGION_CODE='" + regionCode + "' " +
            		 " ORDER BY SEQ,USER_ID ";
        return        
                "SELECT USER_ID, USER_NAME, PY1, PY2, USER_ENG_NAME, SEQ, DESCRIPTION, ID_NO, SEX_CODE, "
                +
                "USER_PASSWORD, POS_CODE, ROLE_ID, ACTIVE_DATE, END_DATE, PUB_FUNCTION, "
                +
                "E_MAIL, LCS_NO, EFF_LCS_DATE, END_LCS_DATE, FULLTIME_FLG, CTRL_FLG, "
                +
                "REGION_CODE, RCNT_LOGIN_DATE, RCNT_LOGOUT_DATE, RCNT_IP, OPT_USER, "
                + "OPT_DATE, OPT_TERM, FOREIGNER_FLG, ABNORMAL_TIMES,PWD_ENDDATE,PWD_STARTDATE,UKEY_FLG,COST_CENTER_CODE,TEL1,TEL2,IS_OUT_FLG "
                + "FROM SYS_OPERATOR" + region;
        //==================pangben modify 20110524 stop  
    }

    /**
     * ����������Ϣ
     * @param patch_type String
     * @return String
     */
    public static String getSYSPatch(String patch_type) {
        String sql = "";
        if (!"".equals(patch_type)) {
            sql = "WHERE " + patch_type;
        }
        return
                "SELECT PATCH_CODE, PATCH_DESC, PATCH_SRC,PATCH_TYPE, PATCH_DATE, "
                + "PATCH_REOMIT_COUNT,PATCH_REOMIT_INTERVAL, PATCH_REOMIT_POINT, STATUS, END_DATE, "
                + "OPT_USER, OPT_DATE, OPT_TERM FROM SYS_PATCH " + sql +
                " ORDER BY PATCH_CODE DESC ";
    }

    /**
     * ���β�����Ϣ
     * @param patch_code String ���δ���
     * @return String
     */
    public static String getSYSPatchParm(String patch_code) {
        return "SELECT PATCH_CODE, PATCH_PARM_NAME, PATCH_PARM_VALUE, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_PATCH_PARM WHERE PATCH_CODE ='" + patch_code + "'";
    }

    /**
     * ������ʷ��Ϣ
     * @param patch_code String ���δ���
     * @return String
     */
    public static String getSYSPatchLog(String patch_code) {
    	//$$===modified by lx 2012/08/08   ��������=====$$//
        return
                "SELECT PATCH_CODE, PATCH_START_DATE, PATCH_DESC, PATCH_SRC, PATCH_TYPE, "
                + "PATCH_DATE, PATCH_REOMIT_COUNT, PATCH_REOMIT_INTERVAL, PATCH_REOMIT_POINT, PATCH_REOMIT_INDEX, "
                +
                "PATCH_END_DATE, PATCH_STATUS, PATCH_MESSAGE, SERVER_IP, OPT_USER, "
                + "OPT_DATE, OPT_TERM "
                + "FROM SYS_PATCH_LOG WHERE PATCH_CODE ='" + patch_code + "'"
                +" ORDER BY OPT_DATE DESC";
    }

    /**
     * �õ��ʱ�
     * @param post_code String
     * @return String
     */
    public static String getSYSPostCode(String post_code) {
        String sql = "";
        if (post_code.length() > 0) {
            sql = "WHERE POST_CODE = '" + post_code + "'";
        }
        return "SELECT POST_CODE, D_CITY_FLG, STATE, STATE_PY, CITY, "
                + "CITY_PY, SEQ, OPT_USER, OPT_DATE, OPT_TERM, ENNAME "
                + "FROM SYS_POSTCODE " + sql + " ORDER BY POST_CODE,STATE,CITY";
    }

    /**
     * ��ѯ���������ʹ������
     * @param order_code String
     * @return String
     */
    public static String getAntibioticTakeDays(String order_code) {
        return "SELECT B.TAKE_DAYS FROM PHA_BASE A , SYS_ANTIBIOTIC B "
                + " WHERE A.ANTIBIOTIC_CODE = B.ANTIBIOTIC_CODE "
                + " AND A.ORDER_CODE = '" + order_code + "'";
    }

    /**
     * �ж�ҩƷ�Ƿ�Ϊ����ҩ
     * @param order_code String
     * @return String
     */
    public static String getOrderCtrFlg(String order_code) {
        return "SELECT A.ORDER_CODE, B.CTRLDRUGCLASS_CODE "
                + " FROM PHA_BASE A, SYS_CTRLDRUGCLASS B "
                +
                " WHERE A.CTRLDRUGCLASS_CODE = B.CTRLDRUGCLASS_CODE AND B.CTRL_FLG = 'Y' "
                + " AND A.ORDER_CODE = '" + order_code + "'";
    }

    /**
     * ȡ��ҩƷת����
     * @param order_code String
     * @return String
     */
    public static String getPhaTransUnit(String order_code) {
        return
                "SELECT ORDER_CODE, PURCH_UNIT, PURCH_QTY, STOCK_UNIT, STOCK_QTY, DOSAGE_UNIT, " +
                " DOSAGE_QTY, MEDI_UNIT, MEDI_QTY FROM PHA_TRANSUNIT WHERE ORDER_CODE = '" +
                order_code + "'";
    }

    /**
     * �û�ID���ڿ���
     * @return String
     */
    public static String getOperatorDept() {
        return "SELECT USER_ID, DEPT_CODE, MAIN_FLG, OPT_USER, OPT_DATE, "
                + "OPT_TERM FROM SYS_OPERATOR_DEPT ORDER BY MAIN_FLG DESC";
    }

    /**
     * �û�ID���ڿ���
     * @param user_id String
     * @return String
     */
    public static String getOperatorDeptByUserId(String user_id) {
        return "SELECT USER_ID, DEPT_CODE, MAIN_FLG, OPT_USER, OPT_DATE, "
                + "OPT_TERM FROM SYS_OPERATOR_DEPT WHERE USER_ID = '"
                + user_id + "' ORDER BY MAIN_FLG DESC";
    }

    /**
     * �����û�IDȡ�������ڿ���
     * @param user_id String
     * @return String
     */
    public static String getOperatorDept(String user_id) {
        return
                "SELECT DEPT_CODE, DEPT_ABS_DESC FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y' "
                +
                " AND DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID = '" +
                user_id + "')";
    }

    /**
     * �����û�IDȡ��֤����Ϣ
     * @param user_id String
     * @return String
     */
    public static String getSYSLicenseDetail(String user_id) {
        return "SELECT USER_ID, LCS_CLASS_CODE, LCS_NO, EFF_LCS_DATE, "
                + "END_LCS_DATE, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
                + user_id + "'";
    }

    /**
     * ʹ������������
     * @param type String
     * @return String
     */
    public static String getSYSOperatorStation(String type) {
        return "SELECT USER_ID, TYPE, STATION_CLINIC_CODE, MAIN_FLG, OPT_USER,"
                + "OPT_DATE, OPT_TERM "
                + "FROM SYS_OPERATOR_STATION "
                + "WHERE TYPE = '" + type + "' ORDER BY MAIN_FLG DESC";
    }

    /**
     * ʹ������������
     * @param type String
     * @param user_id String
     * @return String
     */
    public static String getSYSOperatorStation(String type, String user_id) {
        return "SELECT USER_ID, TYPE, STATION_CLINIC_CODE, MAIN_FLG, OPT_USER,"
                + "OPT_DATE, OPT_TERM "
                + "FROM SYS_OPERATOR_STATION "
                + "WHERE TYPE = '" + type + "' AND USER_ID = '" + user_id +
                "' ORDER BY MAIN_FLG DESC";
    }


    /**
     * ����ҩƷ���ƺ�����ʱ��ȡ��ҩƷ��Ϣ
     * @param order_code String
     * @return String
     */
    public static String getSYSFeeHistory(String order_code) {
        return
                "SELECT ORDER_CODE, START_DATE, END_DATE, ORDER_DESC, ACTIVE_FLG, "
                + " LAST_FLG, PY1, PY2, SEQ, DESCRIPTION, "
                +
                " TRADE_ENG_DESC, GOODS_DESC, GOODS_PYCODE, ALIAS_DESC, ALIAS_PYCODE, "
                +
                " SPECIFICATION, NHI_FEE_DESC, HABITAT_TYPE, MAN_CODE, HYGIENE_TRADE_CODE, "
                +
                " ORDER_CAT1_CODE, CHARGE_HOSP_CODE, OWN_PRICE, NHI_PRICE, GOV_PRICE, "
                +
                " UNIT_CODE, LET_KEYIN_FLG, DISCOUNT_FLG, EXPENSIVE_FLG, OPD_FIT_FLG, "
                +
                " EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, INTV_ORDER_FLG, "
                + " LCS_CLASS_CODE, TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, EXEC_ORDER_FLG, "
                +
                " EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, ADDPAY_AMT, NHI_CODE_O, "
                +
                " NHI_CODE_E, NHI_CODE_I, CTRL_FLG, CLPGROUP_CODE, ORDERSET_FLG, "
                +
                " INDV_FLG, SUB_SYSTEM_CODE, RPTTYPE_CODE, DEV_CODE, OPTITEM_CODE, "
                +
                " MR_CODE, DEGREE_CODE, CIS_FLG, RPP_CODE, ATC_FLG, OPT_USER , "
                + "OPT_DATE , OPT_TERM , OWN_PRICE2, OWN_PRICE3, TUBE_TYPE, IS_REMARK, ACTION_CODE "
                + " FROM SYS_FEE_HISTORY WHERE ORDER_CODE = '" +
                order_code + "' ORDER BY START_DATE DESC";
    }

    /**
     * ����ҩƷ���ƺ�����״̬ȡ��ҩƷ��Ϣ
     * @param order_code String
     * @param flg boolean
     * @return String
     */
    public static String getSYSFeeHistory(String order_code, boolean flg) {
        String active_flg = "N";
        if (flg) {
            active_flg = "Y";
        } else {
            active_flg = "N";
        }
        return
                "SELECT * FROM SYS_FEE_HISTORY WHERE ORDER_CODE = '" +
                order_code + "' AND ACTIVE_FLG = '" + active_flg +
                "' ORDER BY START_DATE DESC";
    }

    /**
     * ����ҩƷ���ƺ�����ʱ��ȡ��ҩƷ��Ϣ
     * @param order_code String
     * @param start_date String
     * @return String
     */
    public static String getSYSFeeHistory(String order_code, String start_date) {
        return
                "SELECT ORDER_CODE, START_DATE, END_DATE, ORDER_DESC, ACTIVE_FLG, "
                + " LAST_FLG, PY1, PY2, SEQ, DESCRIPTION, "
                +
                " TRADE_ENG_DESC, GOODS_DESC, GOODS_PYCODE, ALIAS_DESC, ALIAS_PYCODE, "
                +
                " SPECIFICATION, NHI_FEE_DESC, HABITAT_TYPE, MAN_CODE, HYGIENE_TRADE_CODE, "
                +
                " ORDER_CAT1_CODE, CHARGE_HOSP_CODE, OWN_PRICE, NHI_PRICE, GOV_PRICE, "
                +
                " UNIT_CODE, LET_KEYIN_FLG, DISCOUNT_FLG, EXPENSIVE_FLG, OPD_FIT_FLG, "
                +
                " EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, INTV_ORDER_FLG, "
                + " LCS_CLASS_CODE, TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, EXEC_ORDER_FLG, "
                +
                " EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, ADDPAY_AMT, NHI_CODE_O, "
                +
                " NHI_CODE_E, NHI_CODE_I, CTRL_FLG, CLPGROUP_CODE, ORDERSET_FLG, "
                +
                " INDV_FLG, SUB_SYSTEM_CODE, RPTTYPE_CODE, DEV_CODE, OPTITEM_CODE, "
                +
                " MR_CODE, DEGREE_CODE, CIS_FLG, RPP_CODE, ATC_FLG, OPT_USER , "
                + "OPT_DATE , OPT_TERM, OWN_PRICE2, OWN_PRICE3, TUBE_TYPE, IS_REMARK, ACTION_CODE  "
                + " FROM SYS_FEE_HISTORY WHERE ORDER_CODE = '" +
                order_code + "' AND START_DATE = '" + start_date +
                "' ORDER BY START_DATE DESC";
    }

    /**
     * ����ҩƷ���ƺ�����ʱ�����ҩƷ���ۼƻ�����
     * @param order_code String
     * @param start_date String
     * @param rpp_code String
     * @return String
     */
    public static String updateSYSFeeHistoryRppCode(String order_code,
            String start_date, String rpp_code) {
        return "UPDATE SYS_FEE_HISTORY SET RPP_CODE = '" + rpp_code +
                "' WHERE ORDER_CODE = '" + order_code + "' AND START_DATE = '" +
                start_date + "'";
    }

    /**
     * ��ѯҽ������
     * @param order_cat1_code String
     * @return String
     */
    public static String getSysOrderCat1(String order_cat1_code) {
        return " SELECT ORDER_CAT1_CODE, ORDER_CAT1_DESC, PY1, PY2, SEQ, "
                + " DESCRIPTION, CAT1_TYPE, DEAL_SYSTEM FROM SYS_ORDER_CAT1 "
                + " WHERE ORDER_CAT1_CODE = '" + order_cat1_code + "'";
    }

    /**
     * ���ݿ��һ��ҽ���б�
     * @param dept_code String
     * @return String
     */
    public static String getDoctorByDept(String dept_code) {
        String dept = "";
        if (!"".equals(dept_code)) {
            dept = " AND C.DEPT_CODE = '" + dept_code + "' ";
        }
        return "SELECT   A.USER_ID AS ID, B.USER_NAME AS NAME "
                +
                " FROM SYS_LICENSE_DETAIL A, SYS_OPERATOR B, SYS_OPERATOR_DEPT C, "
                +
                " SYS_POSITION D WHERE A.USER_ID = B.USER_ID AND A.USER_ID = C.USER_ID "
                + " AND B.POS_CODE = D.POS_CODE AND B.USER_ID = C.USER_ID "
                +
                " AND SYSDATE BETWEEN B.ACTIVE_DATE AND B.END_DATE " + dept +
                " ORDER BY A.USER_ID";
    }

    /**
     *
     * @param icd_code String
     * @return String
     */
    public static String getSYSIcdByCode(String icd_code) {
        return
                "SELECT ICD_CODE, ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = '" +
                icd_code + "'";
    }

    /**
     *
     * @return String
     */
    public static String getBillRecpparm() {
        return "SELECT ADM_TYPE, RECP_TYPE, PY1, PY2, SEQ,"
                + " DESCRIPTION, CHARGE01, CHARGE02, CHARGE03, CHARGE04,"
                + " CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09,"
                + " CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,"
                + " CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
                + " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24,"
                + " CHARGE25, CHARGE26, CHARGE27, CHARGE28, CHARGE29,"
                + " CHARGE30, OPT_USER, OPT_DATE, OPT_TERM "
                + " FROM BIL_RECPPARM";
    }

    /**
     *
     * @return String
     */
    public static String getSYSPosition() {
        return "SELECT POS_CODE,POS_CHN_DESC,POS_ENG_DESC,PY1,PY2,"
                + "POS_TYPE,SEQ,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM"
                + " FROM SYS_POSITION ORDER BY POS_CODE,SEQ";
    }

    /**
     *
     * @return String
     * ===pangben ���ҽ������NHI_NO 2012-2-7
     */
    public static String getSYSCtz() {
        return "SELECT CTZ_CODE, CTZ_DESC, PY1, PY2, SEQ, NHI_NO, "
                + "DESCRIPT, NHI_COMPANY_CODE, MAIN_CTZ_FLG, NHI_CTZ_FLG, "
                + "MRCTZ_UPD_FLG,MRO_CTZ, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_CTZ ORDER BY CTZ_CODE";
    }

    /**
     *
     * @return String
     */
    public static String getSYSChargeDetail() {
        return "SELECT CTZ_CODE, CHARGE_HOSP_CODE, DISCOUNT_RATE, OPT_USER, "
                + "OPT_DATE, OPT_TERM FROM SYS_CHARGE_DETAIL";
    }

    /**
     *
     * @param ctz_code String
     * @return String
     */
    public static String deleteSYSChargeDetail(String ctz_code) {
        return "DELETE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE ='" + ctz_code +
                "'";
    }

    /**
     * ������Ϣ
     * @param regionCode String
     * @return String
     */
    public static String getSYSStation(String regionCode) {
        return "SELECT STATION_CODE, STATION_DESC,ENG_DESC, PY1, PY2, SEQ, "
                + "DESCRIPTION, DEPT_CODE, REGION_CODE, ORG_CODE, LOC_CODE, "
                + "PRINTER_NO,TEL_EXT,OPT_USER,OPT_DATE,OPT_TERM,COST_CENTER_CODE,MACHINENO,ATC_TYPE"
                + "FROM SYS_STATION " + regionSQL(regionCode); //===========pangben modify 20110422
    }

    /**
     * ������Ϣ
     * @param regionCode String
     * @return String
     */
    public static String getSYSRoom(String regionCode) {
        return "SELECT ROOM_CODE, ROOM_DESC,ENG_DESC, PY1, PY2, SEQ, "
                +
                "DESCRIPT, STATION_CODE, REGION_CODE, SEX_LIMIT_FLG, RED_SIGN, "
                + "YELLOW_SIGN, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_ROOM " + regionSQL(regionCode); //===========pangben modify 20110422
    }

    /**
     * ��λ��Ϣ
     * @param regionCode String
     * @return String
     */
    public static String getSYSBed(String regionCode) {
        return "SELECT BED_NO, BED_NO_DESC, PY1, PY2, SEQ, "
                +
                "DESCRIPTION, ROOM_CODE,STATION_CODE,REGION_CODE,BED_CLASS_CODE,"
                +
                "BED_TYPE_CODE, ACTIVE_FLG, APPT_FLG, ALLO_FLG, BED_OCCU_FLG,"
                +
                "RESERVE_BED_FLG, SEX_CODE, OCCU_RATE_FLG, DR_APPROVE_FLG, BABY_BED_FLG ,"
                + "HTL_FLG, ADM_TYPE, MR_NO, CASE_NO, IPD_NO, "
                +
                "DEPT_CODE, BED_STATUS , OPT_USER, OPT_DATE, OPT_TERM, ENG_DESC "
                + "FROM SYS_BED " + regionSQL(regionCode); //===========pangben modify 20110422
    }

    /**
     *
     * @return String
     */
    public static String getSYSChargeHosp() {
        return "SELECT CHARGE_HOSP_CODE, CHARGE_HOSP_DESC, PY1, WJW_CHARGE, "
                + "PY2, SEQ, DESCRIPT, OPD_CHARGE_CODE, MRO_CHARGE_CODE, "
                + "STA_CHARGE_CODE, ENG_DESC, IPD_CHARGE_CODE, "
                + "OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_CHARGE_HOSP";
    }

    /**
     *
     * @return String
     */
    public static String getSYSCtrlDrugClass() {
        return "SELECT CTRLDRUGCLASS_CODE, CTRLDRUGCLASS_CHN_DESC, "
                + "CTRLDRUGCLASS_ENG_DESC, PY1, PY2, SEQ, DESCRIPTION, "
                + "PRN_TYPE_CODE, PRN_TYPE_DESC, TAKE_DAYS, PRNSPCFORM_FLG, "
                + "CTRL_FLG, NARCOTIC_FLG, TOXICANT_FLG, "
                + " PSYCHOPHA1_FLG, PSYCHOPHA2_FLG, RADIA_FLG, "
                + "TEST_DRUG_FLG, ANTISEPTIC_FLG, ANTIBIOTIC_FLG, TPN_FLG, "
                + "OPT_USER, OPT_DATE, OPT_TERM FROM SYS_CTRLDRUGCLASS ";
    }

    /**
     * ���������
     * @return String
     */
    public static String getSYSBedType() {
        return "SELECT BED_TYPE_CODE, BEDTYPE_DESC, ENNAME, PY1, PY2, SEQ, "
                + " DESCRIPTION, LAB_DISCNT_FLG, ISOLATION_FLG, BURN_FLG, "
                + " PEDIATRIC_FLG, OBSERVATION_FLG, TRANSPLANT_FLG, ICU_FLG, "
                + " CCU_FLG, BC_FLG, OPT_USER, OPT_DATE, OPT_TERM "
                + " FROM SYS_BED_TYPE ";
    }

    /**
     * ��ѯ�û�������
     * @param user_id String
     * @return String
     */
    public static String getSYSOperatorMainDept(String user_id) {
        return "SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID = '" +
                user_id + "' AND MAIN_FLG = 'Y'";
    }

    /**
     * ȡ�õ�λ��Ϣ
     * @param unit_code String
     * @return String
     */
    public static String getSYSUnit(String unit_code) {
        return "SELECT * FROM SYS_UNIT WHERE UNIT_CODE = '" + unit_code + "'";
    }

    /**
     * ���region�����ڲ�ѯSQL��������
     * @param regionCode String
     * @return String  
     */
    public static String regionSQL(String regionCode) {
        if (null != regionCode && !"".equals(regionCode))
            return " WHERE REGION_CODE='" + regionCode + "' ";
        else
            return "";
    }

}
