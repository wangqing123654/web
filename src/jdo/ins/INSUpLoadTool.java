package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ҽ���걨������</p>
 *
 * <p>Description: ҽ���걨������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.10
 * @version 1.0
 */
public class INSUpLoadTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static INSUpLoadTool instanceObject;
    /**
     * �õ�ʵ��
     * @return INSUpLoadTool
     */
    public static INSUpLoadTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSUpLoadTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSUpLoadTool() {
        onInit();
    }

    /**
     * �õ���������
     * @param parm TParm
     * @return TParm
     */
    public TParm getIBSData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT ADM_SEQ, CONFIRM_SRC, IDNO, HOSP_NHI_NO, INSBRANCH_CODE," +
                "        CTZ1_CODE,ADM_CATEGORY, TO_CHAR (IN_DATE, 'YYYYMMDD') AS IN_DATE," +
                "        TO_CHAR (DS_DATE, 'YYYYMMDD') AS DS_DATE, DIAG_CODE, DIAG_DESC, DIAG_DESC2," +
                "        SOURCE_CODE, OWN_RATE, DECREASE_RATE, REALOWN_RATE, INSOWN_RATE," +
                "        SUBSTR (CASE_NO, 1, 6) || SUBSTR (CASE_NO, 8) AS CASE_NO, STATION_DESC, BED_NO," +
                "        A.DEPT_DESC, BASEMED_BALANCE, INS_BALANCE, START_STANDARD_AMT," +
                "        YEAR_MON, PHA_AMT, PHA_NHI_AMT, EXM_AMT, EXM_NHI_AMT, TREAT_AMT," +
                "        TREAT_NHI_AMT, OP_AMT, OP_NHI_AMT, BED_AMT, BED_NHI_AMT, MATERIAL_AMT," +
                "        MATERIAL_NHI_AMT, OTHER_AMT, OTHER_NHI_AMT, BLOODALL_AMT," +
                "        BLOODALL_NHI_AMT, BLOOD_AMT, BLOOD_NHI_AMT, RESTART_STANDARD_AMT," +
                "        STARTPAY_OWN_AMT, OWN_AMT, PERCOPAYMENT_RATE_AMT, ADD_AMT," +
                "        INS_HIGHLIMIT_AMT, TRANBLOOD_OWN_AMT, NHI_PAY, NHI_COMMENT," +
                "        B.DEPT_CODE, CHEMICAL_DESC, ADM_PRJ, SPEDRS_CODE,A.CONFIRM_NO, " +
                "        A.SINGLE_NHI_AMT, A.SINGLE_STANDARD_OWN_AMT, A.SINGLE_SUPPLYING_AMT,A.ARMYAI_AMT,A.BLOODALL_OWN_AMT, " +
                "        A.PUBMANAI_AMT,A.BED_SINGLE_AMT,A.MATERIAL_SINGLE_AMT " +
                "   FROM INS_IBS A, SYS_DEPT B" +
                "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND DS_DATE <= TO_DATE('" + parm.getData("DS_DATE") + "','YYYYMMDD')" +
                "    AND (UPLOAD_FLG = 'N' OR UPLOAD_FLG = 'R' OR UPLOAD_FLG = 'F' " +
                "          OR STATUS = 'N' ) " +
                "    AND A.REGION_CODE = B.REGION_CODE " +
                "    AND A.DEPT_CODE = B.DEPT_CODE ";

//            System.out.println("�õ���ְ��������>>>>>>>>>>>>>>" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ��ϴ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getIBSUploadData(TParm parm) {
        //System.out.println("�õ��ϴ�����>>>>>>>>��̨���"+parm);
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT A.ADM_SEQ, B.NEWADM_SEQ, B.INSBRANCH_CODE," +
                "        TO_CHAR (A.CHARGE_DATE, 'yyyy-mm-dd HH:mm:ss') AS CHARGE_DATE, A.SEQ_NO, B.HOSP_NHI_NO," +
                "        A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, D.JX, D.GG, A.PRICE, QTY," +
                "        A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG," +
                "        A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG," +
                "        D.PZWH,B.CONFIRM_NO " +
                "   FROM INS_IBS B, INS_IBS_UPLOAD A, INS_RULE D " +
                "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' " +
                "    AND A.ADM_SEQ = B.ADM_SEQ " +
                "    AND A.QTY <> 0 " +
                "    AND B.YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND B.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND A.NHI_ORDER_CODE = D.SFXMBM " +
                "    AND A.CHARGE_DATE BETWEEN D.KSSJ AND D.JSSJ " +
                "  ORDER BY A.ADM_SEQ ";
        //System.out.println("�õ��ϴ�����" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ��������������2
     * @param parm TParm
     * @return TParm
     */
    public TParm getIBSHelpAmt(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT CASE WHEN ARMYAI_AMT IS NULL THEN 0 ELSE ARMYAI_AMT END AS ARMYAI_AMT," +
                "        CASE WHEN TOT_PUBMANADD_AMT IS NULL THEN 0 ELSE TOT_PUBMANADD_AMT END AS TOT_PUBMANADD_AMT " +
                "   FROM INS_IBS " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("�õ���������" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ�ҽʦ֤�պ�
     * @param parm TParm
     * @return TParm
     */
    public TParm getDrQualifyCode(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT C.DR_QUALIFY_CODE As DRQUALIFYCODE" +
                "   FROM ADM_INP A, SYS_OPERATOR C " +
                "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' " +
                "    AND A.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND A.VS_DR_CODE = C.USER_ID ";
        //System.out.println("�õ�ҽʦ֤�պ�" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ��ϴ�������ҳ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROUploadData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT L_TIMES, M_TIMES, S_TIMES, FP_NOTE, DS_SUMMARY " +
                "   FROM INS_IBS " +
                "  WHERE CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("�õ��ϴ�������ҳ��Ϣ" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ������ַ��÷ָ��в�����ҳ������
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROAllData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT (SELECT COUNT (*) " +
                "           FROM ADM_INP " +
                "          WHERE ADM_INP.MR_NO = M.MR_NO " +
                "            AND ADM_INP.CASE_NO <= M.CASE_NO " +
                "            AND ADM_INP.CANCEL_FLG = 'N') AS IN_TIMES, " +
                "        M.OFFICE, M.O_ADDRESS, M.O_TEL, M.H_ADDRESS, M.H_TEL, M.CONTACTER, " +
                "        (SELECT R.CHN_DESC " +
                "           FROM SYS_DICTIONARY R " +
                "          WHERE R.ID = M.RELATIONSHIP AND GROUP_ID='SYS_RELATIONSHIP') AS RELATION_DESC, M.CONT_TEL, M.CONT_ADDRESS, " +
                "        (SELECT SYS_DEPT.DEPT_ABS_DESC " +
                "           FROM SYS_DEPT " +
                "          WHERE SYS_DEPT.DEPT_CODE = M.IN_DEPT) AS IN_DEPT, M.IN_ROOM_NO, " +
                "        (SELECT SYS_DEPT.DEPT_ABS_DESC " +
                "           FROM SYS_DEPT " +
                "          WHERE SYS_DEPT.DEPT_CODE = M.TRANS_DEPT) AS TRANS_DEPT, " +
                "        (SELECT SYS_DEPT.DEPT_ABS_DESC " +
                "           FROM SYS_DEPT " +
                "          WHERE SYS_DEPT.DEPT_CODE = M.OUT_DEPT) AS OUT_DEPT, M.OUT_ROOM_NO, " +
                "        M.IN_CONDITION,M.OE_DIAG_CODE,M.IN_DIAG_CODE,M.OUT_DIAG_CODE1,M.OUT_DIAG_CODE2,M.OUT_DIAG_CODE3,M.OUT_DIAG_CODE4,M.OUT_DIAG_CODE5,M.GET_TIMES,M.VS_DR_CODE,M.ATTEND_DR_CODE,M.PROF_DR_CODE,M.DIRECTOR_DR_CODE, M.PATHOLOGY_DIAG,M.INTE_DIAG_CODE, M.EX_RSN, " +
                "        (SELECT SYS_OPERATOR.DR_QUALIFY_CODE " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.ATTEND_DR_CODE) AS DR_QUALIFY_CODE, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.ATTEND_DR_CODE) AS USER_NAME, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.VS_DR_CODE) AS VS_DR_NAME1, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.VS_DR_CODE) AS VS_DR_NAME2, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.PROF_DR_CODE) AS PROF_DR_NAME, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.DIRECTOR_DR_CODE) AS DIRECTOR_DR_NAME, " +
                "        (SELECT A.ADM_SEQ " +
                "           FROM INS_ADM_CONFIRM A, ADM_INP B " +
                "          WHERE A.CASE_NO = B.CASE_NO AND B.CASE_NO = '" +
                parm.getData("CASE_NO") + "') AS ADM_SEQ,M.MR_NO " +
                "   FROM MRO_RECORD M " +
                "  WHERE M.MR_NO = '" + parm.getData("MR_NO") + "' " +
                "    AND M.CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("�õ������ַ��÷ָ��в�����ҳ������" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ������ַ��÷ָ��в�����ҳ֮�������ϵ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROOpData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT TO_CHAR (O.OP_DATE, 'YYYYMMDD') AS OP_DATE, O.OP_DESC AS NAME, " +
                "        (SELECT A.USER_NAME " +
                "           FROM SYS_OPERATOR A " +
                "          WHERE A.USER_ID = O.MAIN_SUGEON) AS DOCT_NAME, (SELECT A.USER_NAME " +
                "                                               FROM SYS_OPERATOR A " +
                "                                              WHERE A.USER_ID = O.AST_DR1) AS ASSISTANT_NAME, " +
                "        (SELECT S.CHN_DESC " +
                "           FROM SYS_DICTIONARY S " +
                "          WHERE S.ID = O.ANA_WAY AND GROUP_ID = 'OPE_ANAMETHOD') AS MAZUI_MOD, (SELECT A.USER_NAME " +
                "                                                                     FROM SYS_OPERATOR A " +
                "                                                                    WHERE A.USER_ID = O.ANA_DR) AS MAZUI_DOCT, " +
                " (SELECT S.CHN_DESC  "+  
                "        FROM SYS_DICTIONARY S "+  
                "       WHERE S.ID = O.HEALTH_LEVEL  AND GROUP_ID = 'MRO_HEALTHLEVEL' "+
                "                 ) AS HEAL_LEV, O.SEQ_NO AS SEQ " +
                "   FROM MRO_RECORD_OP O " +
                "  WHERE O.CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("�õ������ַ��÷ָ��в�����ҳ֮�������ϵ�����" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ������ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�������Ϣ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleIBSData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT SINGLE_NHI_AMT, SINGLE_STANDARD_OWN_AMT, SINGLE_SUPPLYING_AMT, " +
                "        STARTPAY_OWN_AMT, PERCOPAYMENT_RATE_AMT, BED_SINGLE_AMT,MATERIAL_SINGLE_AMT, B.SDISEASE_CODE " +
                "   FROM INS_IBS A , INS_ADM_CONFIRM B " +
                "  WHERE A.CASE_NO = '" + parm.getData("CASE_NO") + "' "+
                "  AND A.CASE_NO  =  B.CASE_NO ";
        //System.out.println("�õ������ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�������Ϣ��ѯ" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ������ֱ���
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleCode(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
        String sql =
                " SELECT A.SDISEASE_CODE " +
                "   FROM INS_ADM_CONFIRM A, ADM_INP B " +
                "  WHERE B.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND A.CASE_NO = B.CASE_NO ";
        //System.out.println("�õ������ֱ���" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ҽ���걨����½����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbs(TParm parm, TConnection connection) {
//        System.out.println("��ְҽ���걨����½����======��Ρ�������"+parm);
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS " +
                "    SET UPLOAD_FLG = 'S', " +
                "        STATUS = 'S', " +
                "        UPLOAD_DATE = SYSDATE, " +
                "        NEWADM_SEQ = '" + parm.getData("NEW_CONFIRM_NO") + "', " +
                "        ACCOUNT_PAY_AMT = " + parm.getData("ACCOUNT_PAY_AMT") +
                ", " +
                "        PERSON_ACCOUNT_AMT = " +
                parm.getData("PERSON_ACCOUNT_AMT") + " " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";
//        System.out.println("ҽ���걨����½����sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ҽ���걨����·�����ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbsOrder(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS_ORDER " +
                "    SET NEWADM_SEQ = '" + parm.getData("NEWADM_SEQ") + "' " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ҽ���걨������ʸ�ȷ�����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbsAdmConfirm(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
            " UPDATE INS_ADM_CONFIRM " +
            "    SET IN_STATUS = '2'," +
            "        OPT_USER = '" + parm.getData("OPT_USER") + "'," +
            "        OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
            "        OPT_DATE = SYSDATE," +
            "        AUD_DATE = SYSDATE " +
            "  WHERE CONFIRM_NO IN (SELECT CONFIRM_NO FROM INS_IBS WHERE NEWADM_SEQ = '" +
                parm.getValue("NEW_CONFIRM_NO") + "')";
//        System.out.println("��ְҽ���걨������ʸ�ȷ�����sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * ҽ���걨������ϴ���ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbsUpload(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS_UPLOAD " +
                "    SET INVNO = '" + parm.getData("INVNO") + "' " +
                "  WHERE ADM_SEQ = '" + parm.getData("ADM_SEQ") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * �����걨����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsForCJ(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                "  UPDATE INS_IBS " +
                "  SET UPLOAD_FLG = 'N'," +
                "   OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "   OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "   OPT_DATE = SYSDATE " +
                " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����걨����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsInsAdmConfirmForCJ(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_ADM_CONFIRM " +
                " SET IN_STATUS = '1'," +
                "   OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "   OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "   OPT_DATE = SYSDATE " +
                " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ�ҽ��״̬
     * @param parm TParm
     * @return TParm
     */
    public TParm getInsStatusForCJ(TParm parm) {

        TParm result = new TParm();
        String sql =
                "SELECT IN_STATUS" +
                " FROM INS_ADM_CONFIRM " +
                " WHERE ADM_SEQ = '" + parm.getData("ADM_SEQ") + "'";
        //System.out.println("sql" + sql);

        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����ʸ�ȷ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsInsAdmConfirmForCheckCJ(TParm parm,
                                                TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_ADM_CONFIRM " +
                " SET IN_STATUS = '7'," +
                "  OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "  OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "  OPT_DATE = SYSDATE " +
//        	     ",  DOWN_DATE = TO_DATE ('"+parm.getData("")+"', 'YYYYMMDDHH24MISS')"+                //TODO:��֪��������Դ
                " WHERE ADM_SEQ = '" + parm.getData("ADM_SEQ") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���ҽ���걨��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getINSMedAppInfo(TParm parm) {
        //System.out.println("���ҽ���걨��Ϣ>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:��������ΪNULL");
            return result;
        }
//            df.format(new Date(parm.getValue("DS_DATE")));
        String sql =
                " SELECT A.ADM_SEQ, A.CONFIRM_SRC, A.IDNO, A.HOSP_NHI_NO, A.INSBRANCH_CODE," +
                "A.CTZ1_CODE, A.ADM_CATEGORY, TO_CHAR (A.IN_DATE, 'YYYYMMDD') AS IN_DATE," +
                "TO_CHAR (A.DS_DATE, 'YYYYMMDD') AS DS_DATE, A.DIAG_CODE, A.DIAG_DESC," +
                "A.DIAG_DESC2, A.SOURCE_CODE, A.OWN_RATE, A.DECREASE_RATE," +
                "A.REALOWN_RATE, A.INSOWN_RATE," +
                "SUBSTR (A.CASE_NO, 1, 6) || SUBSTR (A.CASE_NO, 8) AS CASE_NO, A.STATION_DESC," +
                "A.BED_NO, A.DEPT_DESC, A.BASEMED_BALANCE, A.INS_BALANCE," +
                "A.START_STANDARD_AMT, A.YEAR_MON, A.PHA_AMT, A.PHA_NHI_AMT, A.EXM_AMT," +
                "A.EXM_NHI_AMT, A.TREAT_AMT, A.TREAT_NHI_AMT, A.OP_AMT, A.OP_NHI_AMT," +
                "A.BED_AMT, A.BED_NHI_AMT, A.MATERIAL_AMT, A.MATERIAL_NHI_AMT," +
                "A.OTHER_AMT, A.OTHER_NHI_AMT, A.BLOODALL_AMT, A.BLOODALL_NHI_AMT," +
                "A.BLOOD_AMT, A.BLOOD_NHI_AMT, A.RESTART_STANDARD_AMT," +
                "A.STARTPAY_OWN_AMT, A.OWN_AMT, A.PERCOPAYMENT_RATE_AMT, A.ADD_AMT," +
                "A.INS_HIGHLIMIT_AMT, A.TRANBLOOD_OWN_AMT, A.NHI_PAY, NHI_COMMENT," +
                "E.DEPT_CODE, A.CHEMICAL_DESC, A.ADM_PRJ, A.SPEDRS_CODE," +
                "B.BEARING_OPERATIONS_TYPE, D.DR_QUALIFY_CODE AS LCS_NO, '', F.NHI_CTZ_FLG,A.SINGLE_STANDARD_OWN_AMT," +
                "A.SINGLE_SUPPLYING_AMT,A.ARMYAI_AMT,B.SDISEASE_CODE,A.PUBMANAI_AMT,A.BED_SINGLE_AMT,A.MATERIAL_SINGLE_AMT " +
                " FROM INS_IBS A," +
                " INS_ADM_CONFIRM B," +
                " ADM_INP C," +
                " SYS_OPERATOR D," +
                " SYS_DEPT E," +
                " SYS_CTZ F" +
                " WHERE A.REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
                " AND A.YEAR_MON = '" + parm.getValue("YEAR_MON") + "'" +
                " AND A.CASE_NO = '" + parm.getValue("CASE_NO") + "'" +
                " AND TO_CHAR(A.DS_DATE,'yyyymmdd')<= '" +
                parm.getData("DS_DATE") + "'" +
                " AND (   A.UPLOAD_FLG = 'N'  OR A.UPLOAD_FLG = 'R' OR A.UPLOAD_FLG = 'F' OR A.STATUS = 'N')" +
                " AND A.CONFIRM_NO = B.CONFIRM_NO" +
                " AND A.REGION_CODE = C.REGION_CODE" +
                " AND A.CASE_NO = C.CASE_NO" +
                " AND C.REGION_CODE = D.REGION_CODE" +
                " AND C.VS_DR_CODE = D.USER_ID" +
                " AND A.REGION_CODE = E.REGION_CODE" +
                " AND A.DEPT_CODE = E.DEPT_CODE" +
                " AND B.CTZ1_CODE = F.CTZ_CODE";
        //System.out.println("ҽ���걨��Ϣ" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 6�����µľ���˳���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsAdmSeq(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS " +
                " SET NEWADM_SEQ = '" + parm.getData("NEWADM_SEQ") + "'," +
                " OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                " OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                " OPT_DATE = SYSDATE " +
                " WHERE REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' AND YEAR_MON = '" + parm.getData("YEAR_MON") +
                "' AND CASE_NO = '" + parm.getData("CASE_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 7����INS_IBS_ORDER�µľ���˳���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsOrderAdmSeq(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS_ORDER " +
                " SET NEWADM_SEQ = '" + parm.getData("NEWADM_SEQ") + "'," +
                " OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                " OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                " OPT_DATE = SYSDATE " +
                " WHERE REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' AND YEAR_MON = '" + parm.getData("YEAR_MON") +
                "' AND CASE_NO = '" + parm.getData("CASE_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����ϴ���ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm getInsMedInfo(TParm parm) {
        TParm result = new TParm();
        String sql =
                " SELECT   A.ADM_SEQ, B.NEWADM_SEQ, B.INSBRANCH_CODE," +
                " TO_CHAR (A.CHARGE_DATE, 'YYYYMMDDHH24MISS') AS CHARGE_DATE, A.SEQ_NO, B.HOSP_NHI_NO," +
                " A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, D.JX, D.GG, A.PRICE, QTY," +
                " A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG," +
                " A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG," +
                " D.PZWH " +
                " FROM INS_IBS B, INS_IBS_UPLOAD A, INS_RULE D" +
                " WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "'" +
                " AND A.ADM_SEQ = B.ADM_SEQ" +
                " AND A.QTY <> 0" +
                " AND B.YEAR_MON = '" + parm.getData("YEAR_MON") + "'" +
                " AND B.CASE_NO = '" + parm.getData("CASE_NO") + "'" +
                " AND A.NHI_ORDER_CODE = D.SFXMBM" +
                " AND A.CHARGE_DATE BETWEEN D.KSSJ AND D.JSSJ" +
                " ORDER BY A.ADM_SEQ";
        //System.out.println("�����ϴ���ϸsql" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ������ϸ�ϴ���д
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsBack(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS " +
                "    SET UPLOAD_FLG = 'S'," +
                "        STATUS = 'S'," +
                "        UPLOAD_DATE = SYSDATE," +
                "        OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "        OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "        OPT_DATE = SYSDATE " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") +
                "'   AND YEAR_MON = '" + parm.getData("YEAR_MON") +
                "'   AND CASE_NO = '" + parm.getData("CASE_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ������ϸ�ϴ���дINS_ADM_CONFIRM
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsAdmConfirmBack(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_ADM_CONFIRM " +
                "    SET IN_STATUS = '2'," +
                "        OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "        OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "        OPT_DATE = SYSDATE," +
                "        AUD_DATE = SYSDATE " +
                "  WHERE CONFIRM_NO IN (SELECT CONFIRM_NO FROM INS_IBS WHERE NEWADM_SEQ = '" +
                parm.getValue("NEWADM_SEQ") + "')";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getPatInfo(TParm parm) {
        TParm result = new TParm();
        String sql =
                " SELECT 'N' FLG, O.YEAR_MON, O.CONFIRM_NO, O.MR_NO, O.PAT_NAME," +
                "        S.CHN_DESC AS SEX_DESC, C.CTZ_DESC, O.IN_DATE, O.DS_DATE, O.CASE_NO," +
                "        A.PERSONAL_NO,A.ADM_SEQ " +
                "   FROM INS_IBS O,SYS_DICTIONARY S,SYS_CTZ C,SYS_PATINFO P,INS_ADM_CONFIRM A " +
                "  WHERE A.HIS_CTZ_CODE = C.CTZ_CODE " +
                "    AND O.CTZ1_CODE = A.CTZ1_CODE "+
                "    AND C.NHI_CTZ_FLG = 'Y' " +
                "    AND SUBSTR (A.HIS_CTZ_CODE, 0, 1) = '"+parm.getValue("INS_PAT_TYPE")+"' " +
//                "    AND SUBSTR (C.CTZ_CODE, 0, 1) = '1' " +
                "    AND P.MR_NO = O.MR_NO " +
                "    AND O.CONFIRM_NO = A.CONFIRM_NO " +
                "    AND A.IN_STATUS = '1' " +
                "    AND P.SEX_CODE = S.ID " +
                "    AND S.GROUP_ID = 'SYS_SEX' " +
                "    AND O.CASE_NO = '" + parm.getData("CASE_NO") + "' ";
//            System.out.println("�õ��걨������Ϣsql" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �õ�ҽ��ҽԺ����
     * @param hospCode String
     * @return TParm
     */
    public TParm getNhiHospCode(String hospCode) {
        TParm result = new TParm();
        String sql =
                " SELECT NHI_NO FROM SYS_REGION " +
                "  WHERE REGION_CODE = '" + hospCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * �õ�����ҽ����ݴ���
     * @param ctzCode String
     * @return TParm
     */
    public TParm getNhiCtzCode(String ctzCode) {
        TParm result = new TParm();
        String sql =
                " SELECT NHI_NO FROM SYS_CTZ " +
                "  WHERE CTZ_CODE = '" + ctzCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
}
