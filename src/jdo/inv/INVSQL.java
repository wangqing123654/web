package jdo.inv;

import com.dongyang.data.TParm;

/**
 * <p>
 * Title: ����SQL��װ
 * </p>
 *
 * <p>
 * Description: ����SQL��װ
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
 * @author zhangy 2009.10.29
 * @version 1.0
 */

public class INVSQL {
    public INVSQL() {
    }
    
    /* **************************��������*************************************** */
    /**
     * �Զ���������
     * 
     * @return String
     */ 
    public static String getAssignorg(String orgCode) {
        return
            "SELECT ORG_CODE , CYCLE_TYPE , ASSIGNED_DAY , OPT_USER , OPT_DATE ,"
            + " OPT_TERM " +
              " FROM INV_ASSIGNORG " +
              " WHERE ORG_CODE = '"+ orgCode +"'" +
              " ORDER BY ORG_CODE"; 
    }
    
    
    /**
     * ȫ���Զ���������
     * 
     * @return String 
     */ 
    public static String getAllAssignorg() {
        return
            "SELECT ORG_CODE , CYCLE_TYPE , ASSIGNED_DAY , OPT_USER , OPT_DATE ,"
            + " OPT_TERM " +
              " FROM INV_ASSIGNORG " +
              " ORDER BY ORG_CODE";
    }
    /* **************************��������*************************************** */
    /**
     * ȫ�����ʲ���
     *  
     * @return String
     */
    public static String getinvOrg() {
        return  
            "SELECT * " 
            + "  FROM INV_ORG ORDER BY ORG_CODE";
    }
    
    /**
     * ���ʿ������趨  fux modify 20130522
     *
     * @return String
     */                    
    public static String getINVSysParm() {
        return
//        " SELECT ADD_STOCK_FLG,FIXEDAMOUNT_FLG,REUPRICE_FLG,HISTORY_DAYS, " +
//        " DISCHECK_FLG,MM_DAY,OPT_USER,OPT_DATE,OPT_TERM,HIGH_VALUE_INV " +
//        " FROM INV_SYSPARM "; 
        " SELECT FIXEDAMOUNT_FLG,REUPRICE_FLG," + 
        " DISCHECK_FLG,AUTO_FILL_TYPE,OPT_USER,OPT_DATE,OPT_TERM " +
        " FROM INV_SYSPARM ";   
    }  
    
    /**
     * ��ѯ������Ϣ-�Զ�����
     * @param orgCode
     * @return
     * @author fux
     * @date 20130527 
     */
    public static String queryOrgCodeAuto(){
    	//���ԣ�inv_code = '08.02.0271274' 
    	String sql = " SELECT  A.ORG_CODE,NVL(A.DISPENSE_ORG_CODE,'-1') AS DISPENSE_ORG_CODE ,B.ORG_TYPE "
    		       + " FROM INV_STOCKM A,INV_ORG B  "
    		       + " WHERE A.DISPENSE_FLG='Y'  AND A.ORG_CODE=B.ORG_CODE  "
    		       + " GROUP BY A.ORG_CODE,A.DISPENSE_ORG_CODE  ,B.ORG_TYPE ";
    	return sql;
    }
    
    /* **************************���д���*************************************** */

    /**
     * ��ѯ��������������Ϣ
     *
     * @param sup_code
     *            ��������
     * @return String
     */
    public static String getSupInv(String sup_code, String inv_code) {
        if ("".equals(sup_code)) {
            return "";
        }
        String sql = "SELECT A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION,E.MAN_CHN_DESC,D.SUP_CHN_DESC, "
            + " A.BILL_UNIT, A.CONTRACT_PRICE, A.STOCK_UNIT, "
            + " A.GIFT_QTY, A.GIFT_RATE, A.DISCOUNT_RATE, B.MAN_CODE, "
            + " C.PURCH_QTY, C.STOCK_QTY "
            + " FROM INV_AGENT A, INV_BASE B, INV_TRANSUNIT C ,SYS_SUPPLIER D,SYS_MANUFACTURER E"
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.INV_CODE = C.INV_CODE "
            + " AND B.MAN_CODE = E.MAN_CODE "
            + " AND B.UP_SUP_CODE = D.SUP_CODE"
            + " AND A.SUP_CODE='"
            + sup_code + "' ";
        if (!"".equals(inv_code)) {
            sql += "AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR B.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + "OR B.PY1 LIKE '" + inv_code + "%' )";
        }
        return sql;
    }
    
    

    /**
     * ��ѯ��������������Ϣ
     *
     * @param sup_code
     *            ��������
     * @return String
     */
    public static String getSupBaseInv(String sup_code, String inv_code) {
        String sql = "SELECT A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, "
            + " A.BILL_UNIT, A.CONTRACT_PRICE, A.STOCK_UNIT, "
            + " A.GIFT_QTY, A.GIFT_RATE, A.DISCOUNT_RATE, B.MAN_CODE, "
            + " C.PURCH_QTY, C.STOCK_QTY "
            + " FROM INV_AGENT A, INV_BASE B, INV_TRANSUNIT C "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.INV_CODE = C.INV_CODE ";
        if (!"".equals(inv_code)) {
            sql += "AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR B.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + "OR B.PY1 LIKE '" + inv_code + "%' )";
        }
        return sql;   
    }

    /**
     * ��ѯ����������Ϣ
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getOrgInv(String org_code, String inv_code) {
        String inv_info = "";
        if (!"".equals(inv_code)) {
            inv_info = " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + " OR A.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + " OR A.PY1 LIKE '" + inv_code + "%' )";
        }

        String sql = "SELECT A.INV_CODE, A.INV_CHN_DESC, "
            + " A.DESCRIPTION, A.PY1, B.STOCK_QTY, A.STOCK_UNIT, "
            + " B.INVSEQ_NO, A.SEQMAN_FLG, A.DISPENSE_UNIT, "
            + " A.COST_PRICE, A.MAN_CODE, B.BATCH_NO, B.VALID_DATE, "
            + " B.BATCH_SEQ, A.VALIDATE_FLG, A.INVKIND_CODE "
            + " FROM INV_BASE A, INV_STOCKDD B "
            + " WHERE A.INV_CODE = B.INV_CODE AND"
            + " A.SEQMAN_FLG = 'Y' AND A.ACTIVE_FLG = 'Y' AND B.ORG_CODE = '"
            + org_code + "' "
            + inv_info
            + " UNION "
            + " SELECT A.INV_CODE, A.INV_CHN_DESC, A.DESCRIPTION, "
            + " A.PY1,B.STOCK_QTY,A.STOCK_UNIT,0 AS INVSEQ_NO, A.SEQMAN_FLG, "
            + " A.DISPENSE_UNIT, A.COST_PRICE , A.MAN_CODE, B.BATCH_NO, "
            + " B.VALID_DATE, B.BATCH_SEQ, A.VALIDATE_FLG, A.INVKIND_CODE "
            + " FROM INV_BASE A, INV_STOCKD B "
            + " WHERE A.INV_CODE = B.INV_CODE AND"
            + " A.SEQMAN_FLG = 'N' AND A.ACTIVE_FLG = 'Y' AND B.ORG_CODE = '"
            + org_code + "' "
            + inv_info;
        return sql;
    }

    /**
     * δ��ɵĶ�������
     * @param org_code String
     * @return String
     */
    public static String getInvPurOrderNo(String org_code) {
        return "SELECT * FROM INV_PURORDERM WHERE ORG_CODE = '" + org_code +
            "' AND FINAL_FLG = 'N' AND CHECK_FLG = 'Y' ORDER BY PURORDER_NO DESC";
    }

    /**
     * δ��ɵĶ�������
     * @param org_code String
     * @return String
     */
    public static String getInvPurOrderNo(String org_code, String sup_code) {
        return "SELECT * FROM INV_PURORDERM WHERE ORG_CODE = '" + org_code +
            "' AND SUP_CODE = '" + sup_code +
            "' AND FINAL_FLG = 'N' ORDER BY PURORDER_NO DESC";
    }

    /**
     * INV_BASE����
     * @return String
     */
    public static String getInvBasePopup(String inv_code) {
        String sql =
            "SELECT A.INV_CODE, A.INVTYPE_CODE, A.INVKIND_CODE, A.INV_CHN_DESC, "
            + "A.INV_ENG_DESC, A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, "
            +
            "A.BUYWAY_CODE, A.USE_DEADLINE, A.COST_PRICE, A.ORDER_CODE, A.STOCK_UNIT, "
            +
            "A.DISPENSE_UNIT,A.HYGIENE_TRADE_CODE,A.STOPBUY_FLG,A.NORMALDRUG_FLG, "
            +
            "A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, B.PURCH_UNIT "
            + "FROM INV_BASE A, INV_TRANSUNIT B "
            + "WHERE A.INV_CODE = B.INV_CODE AND ACTIVE_FLG = 'Y' ";
        if (!"".equals(inv_code)) {
            sql += " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR A.INV_CHN_DESC LIKE '%" + inv_code + "%' "
                + "OR A.PY1 LIKE '%" + inv_code + "%' )";
        }
        return sql;
    }
    
    
    /**
     * INV_BASE���ݣ�������ܣ�
     * @return String
     */
    public static String getInvBaseNotSeqPopup(String inv_code) {
        String sql =
            "SELECT A.INV_CODE, A.INVTYPE_CODE, A.INVKIND_CODE, A.INV_CHN_DESC, "
            + "A.INV_ENG_DESC, A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, "
            +
            "A.BUYWAY_CODE, A.USE_DEADLINE, A.COST_PRICE, A.ORDER_CODE, A.STOCK_UNIT, "
            +
            "A.DISPENSE_UNIT,A.HYGIENE_TRADE_CODE,A.STOPBUY_FLG,A.NORMALDRUG_FLG, "
            +
            "A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, B.PURCH_UNIT "
            + "FROM INV_BASE A, INV_TRANSUNIT B "
            + "WHERE A.INV_CODE = B.INV_CODE AND ACTIVE_FLG = 'Y' AND A.SEQMAN_FLG = 'N' ";
        if (!"".equals(inv_code)) {
            sql += " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR A.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + "OR A.PY1 LIKE '" + inv_code + "%' )";
        }
        return sql;
    }

    /**
     * INV_BASE����
     * @return String
     */
    public static String getInvPackPopup(String pack_code, String seq_flg) {
        String sql = "SELECT * FROM INV_PACKM ";
        if (!"".equals(pack_code)) {
            sql += " WHERE (PACK_CODE LIKE '" + pack_code + "%' "
                + "OR PACK_DESC LIKE '" + pack_code + "%' "
                + "OR PY1 LIKE '" + pack_code + "%' )";
        }
        if (!"".equals(pack_code) && !"".equals(seq_flg)) {
            sql += " AND SEQ_FLG = '" + seq_flg + "'";
        }
        else if (!"".equals(seq_flg)) {
            sql += " WHERE SEQ_FLG = '" + seq_flg + "'";
        }
        return sql;
    }

    /**
     *
     * @param pack_code String
     * @return String
     */
    public static String getInvPackStockMPopup(String org_code,
                                               String pack_code) {
        String sql = "SELECT A.PACK_CODE, B.PACK_DESC, A.PACK_SEQ_NO, "
            + " B.SEQ_FLG, B.DESCRIPTION "
            + " FROM INV_PACKSTOCKM A, INV_PACKM B "
            + " WHERE A.PACK_CODE = B.PACK_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' ";
        if (!"".equals(pack_code)) {
            sql += " AND (B.PACK_CODE LIKE '" + pack_code + "%' "
                + "OR B.PACK_DESC LIKE '" + pack_code + "%' "
                + "OR B.PY1 LIKE '" + pack_code + "%' )";
        }
        sql += " ORDER BY A.PACK_CODE,A.PACK_SEQ_NO ";
        return sql;
    }

    /**
     * INV_BASE ����
     * @param inv_code String
     * @return String
     */
    public static String getInvBase(String inv_code) {
        return "SELECT INV_CODE, INVTYPE_CODE, INVKIND_CODE, INV_CHN_DESC, "
            + "INV_ENG_DESC, INV_ABS_DESC, DESCRIPTION, MAN_CODE, "
            + "BUYWAY_CODE, USE_DEADLINE, COST_PRICE, ORDER_CODE, STOCK_UNIT, "
            + "DISPENSE_UNIT,HYGIENE_TRADE_CODE,STOPBUY_FLG,NORMALDRUG_FLG, "
            + "REQUEST_FLG, SEQMAN_FLG, VALIDATE_FLG, EXPENSIVE_FLG "
            + "FROM INV_BASE WHERE ACTIVE_FLG = 'Y' AND INV_CODE = '" +
            inv_code + "'";
    }

    /**
     * INV_TRANSUNIT ����
     * @param inv_code String
     * @return String
     */
    public static String getInvTransUnit(String inv_code) {
        return "SELECT * FROM INV_TRANSUNIT WHERE INV_CODE = '" + inv_code +
            "'";
    }

    /**
     * ȡ�ÿ������
     * @param inv_code String
     * @return String
     */
    public static String getInvStockSumQty(String inv_code) {
        return " SELECT SUM(STOCK_QTY) AS SUM_QTY FROM INV_STOCKM "
            + " WHERE INV_CODE = '" + inv_code + "'";
    }

    /**
     * �����û�ID��ѯ��������
     * @param user_id String
     * @param org_type String
     * @param station_flg String
     * @param mat_flg String
     * @param inv_flg String
     * @return String
     */
    public static String getOperatorInvOrg(String user_id, String org_type,
                                           String station_flg, String mat_flg,
                                           String inv_flg) {
        String sql =
            "SELECT A.ORG_CODE AS ID, A.ORG_DESC AS NAME, A.PY1, A.PY2"
            + " FROM INV_ORG A,SYS_OPERATOR_DEPT B "
            + " WHERE A.ORG_CODE=B.DEPT_CODE AND B.USER_ID = '" + user_id + "'";

        if (!"".equals(org_type)) {
            sql += " AND A.ORG_TYPE = '" + org_type + "'";
        }
        if (!"".equals(station_flg)) {
            sql += " AND A.STATION_FLG = '" + station_flg + "'";
        }
        if (!"".equals(mat_flg)) {
            sql += " AND A.MAT_FLG = '" + mat_flg + "'";
        }
        if (!"".equals(inv_flg)) {
            sql += " AND A.INV_FLG = '" + inv_flg + "'";
        }
        return sql;
    }

    /**
     * �������ź�Ч��ȡ�����ʵ��������
     * @param batch_no String
     * @param valid_date String
     * @return String
     */
    public static String getInvBatchSeq(String org_code, String inv_code,
                                        String batch_no, String valid_date) {
        if (!"".equals(valid_date)) {
            return "SELECT BATCH_SEQ FROM INV_STOCKD "
                + "WHERE ORG_CODE = '" + org_code
                + "' AND INV_CODE = '" + inv_code
                + "' AND BATCH_NO = '" + batch_no
                + "' AND VALID_DATE = TO_DATE('" + valid_date
                + "','YYYYMMDDHH24MISS')";
        }
        else {
            return "SELECT BATCH_SEQ FROM INV_STOCKD "
                + "WHERE ORG_CODE = '" + org_code
                + "' AND INV_CODE = '" + inv_code
                + "' AND BATCH_NO = '" + batch_no + "'";
        }
    }

    /**
     * ���ݲ��ź����ʴ���ȡ������������
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockMaxBatchSeq(String org_code,
                                                String inv_code) {
        return "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM INV_STOCKD "
            + "WHERE ORG_CODE = '" + org_code + "' AND INV_CODE = '" + inv_code +
            "'";
    }

    /**
     * ȡ�����������ϸ���е�����������
     * @param inv_code String
     * @return String
     */
    public static String getInvMaxInvSeqNo(String inv_code) {
        return "SELECT MAX(INVSEQ_NO) AS INVSEQ_NO FROM INV_VERIFYINDD "
            + "WHERE INV_CODE = '" + inv_code + "'";
    }

    /**
     * ȡ�ÿ��������Ϣ
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockM(String org_code, String inv_code) {
        return "SELECT ORG_CODE, INV_CODE, REGION_CODE, DISPENSE_FLG, "
            + " DISPENSE_ORG_CODE, STOCK_FLG, MATERIAL_LOC_CODE, SAFE_QTY, "
            + " MIN_QTY, MAX_QTY, ECONOMICBUY_QTY, STOCK_QTY, MM_USE_QTY, "
            + " AVERAGE_DAYUSE_QTY, STOCK_UNIT, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM INV_STOCKM WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code + "'";
    }

    /**
     * ȡ�ÿ��������Ϣ
     * @param org_code String
     * @param inv_code String
     * @param materail_loc_code String
     * @return String
     */
    public static String getInvStockM(String org_code, String inv_code,
                                      String materail_loc_code) {
        String where = "";
        if (!"".equals(inv_code)) {
            where += " AND A.INV_CODE = '" + inv_code + "' ";
        }
        if (!"".equals(materail_loc_code)) {
            where += " AND A.MATERIAL_LOC_CODE = '" + materail_loc_code + "' ";
        }

        return "SELECT A.INV_CODE, B.INV_CHN_DESC, A.MATERIAL_LOC_CODE, "
            + " A.DISPENSE_FLG,A.DISPENSE_ORG_CODE, A.STOCK_UNIT, A.SAFE_QTY,  "
            + " A.MAX_QTY, A.MIN_QTY,A.ECONOMICBUY_QTY, A.STOCK_QTY,  "
            + " A.AVERAGE_DAYUSE_QTY, A.MM_USE_QTY,A.REGION_CODE,A.BASE_QTY "
            + " FROM INV_STOCKM A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE AND A.ORG_CODE = '" + org_code + "' " +
            where;
    }


    /**
     * ȡ�ÿ����ϸ��Ϣ
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockD(String org_code, String inv_code) {
        return "SELECT ORG_CODE,INV_CODE,BATCH_SEQ,REGION_CODE,BATCH_NO, " +
            " VALID_DATE, STOCK_QTY, LASTDAY_TOLSTOCK_QTY, DAYIN_QTY, " +
            " DAYOUT_QTY, DAY_CHECKMODI_QTY, DAY_VERIFYIN_QTY, " +
            " DAY_VERIFYIN_AMT, GIFTIN_QTY, DAY_REGRESSGOODS_QTY, " +
            " DAY_REGRESSGOODS_AMT, DAY_REQUESTIN_QTY, DAY_REQUESTOUT_QTY, " +
            " DAY_CHANGEIN_QTY, DAY_CHANGEOUT_QTY, DAY_TRANSMITIN_QTY, " +
            " DAY_TRANSMITOUT_QTY, DAY_WASTE_QTY, DAY_DISPENSE_QTY, " +
            " DAY_REGRESS_QTY, FREEZE_TOT, UNIT_PRICE, STOCK_UNIT " +
            " FROM INV_STOCKD WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code + "' ORDER BY BATCH_SEQ ";
    }

    /**
     * ȡ�ÿ����ϸ��Ϣ
     * @param org_code String
     * @param inv_code String
     * @param batch_seq int
     * @return String
     */
    public static String getInvStockD(String org_code, String inv_code,
                                      int batch_seq) {
        return "SELECT ORG_CODE,INV_CODE,BATCH_SEQ,REGION_CODE,BATCH_NO, " +
            " VALID_DATE, STOCK_QTY, LASTDAY_TOLSTOCK_QTY, DAYIN_QTY, " +
            " DAYOUT_QTY, DAY_CHECKMODI_QTY, DAY_VERIFYIN_QTY, " +
            " DAY_VERIFYIN_AMT, GIFTIN_QTY, DAY_REGRESSGOODS_QTY, " +
            " DAY_REGRESSGOODS_AMT, DAY_REQUESTIN_QTY, DAY_REQUESTOUT_QTY, " +
            " DAY_CHANGEIN_QTY, DAY_CHANGEOUT_QTY, DAY_TRANSMITIN_QTY, " +
            " DAY_TRANSMITOUT_QTY, DAY_WASTE_QTY, DAY_DISPENSE_QTY, " +
            " DAY_REGRESS_QTY, FREEZE_TOT, UNIT_PRICE STOCK_UNIT " +
            " FROM INV_STOCKD WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code + "' AND BATCH_SEQ = " + batch_seq;
    }

    /**
     * �жϵ����Ƿ�ȫ�����
     * @return String
     */
    public static String getInvPurorderMInQty(String purorder_no) {
        return "SELECT COUNT(*) AS SUM_COUNT FROM INV_PURORDERD " +
            " WHERE PURORDER_NO = '" + purorder_no +
            "' AND PROCESS_TYPE IN ('0','1')";
    }

    /**
     * ���ݲ�������ȡ�ò�����Ϣ
     * @param org_code String
     * @return String
     */
    public static String getInvOrg(String org_code) {
        return "SELECT * FROM INV_ORG WHERE ORG_CODE = '" + org_code + "'";
    }

    /**
     * ���ʲ�����
     * @return String
     */
    public static String getInvSysParm() {
        return "SELECT * FROM INV_SYSPARM ";
    }

    /**
     * ���ʲ��Ų�����Ϣ
     * @return String
     */
    public static String getINVOrg(String org_code) {
        return "SELECT ORG_CODE, ORG_TYPE, STATION_FLG, STOCK_ORG_CODE, "
            + " REMARK, REGION_CODE, MAT_FLG, INV_FLG FROM INV_ORG "
            + " WHERE ORG_CODE = '" + org_code + "'";
    }

    /**
     *
     * @return String
     */
    public static String getInvBaseRule() {
        return "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5, "
            + "SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE "
            + "FROM SYS_RULE WHERE RULE_TYPE = 'INV_BASE'";
    }

    /**
     *
     * @return String
     */
    public static String getInvBaseCategory() {
        return "SELECT CATEGORY_CODE,CATEGORY_CHN_DESC,DETAIL_FLG "
            + "FROM SYS_CATEGORY WHERE RULE_TYPE = 'INV_BASE' ORDER BY SEQ";
    }

    /**
     *
     * @return String
     */
    public static String getInvBaseCategoryLength() {
        return "SELECT DISTINCT LENGTH(CATEGORY_CODE) CATEGORY_LENGTH "
            + "FROM SYS_CATEGORY WHERE  RULE_TYPE = 'INV_BASE' "
            + "ORDER BY CATEGORY_LENGTH ";
    }

    /**
     *
     * @return String
     */
    public static String getInvRule() {
        return "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5, "
            + "SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE "
            + "FROM SYS_RULE WHERE RULE_TYPE = 'INV_BASE' ";
    }

    /**
     *
     * @param type_code String
     * @return String
     */
    public static String getInvMaxSerialNumber(String invtype_code) {
        return "SELECT MAX(INV_CODE) AS INV_CODE FROM INV_BASE "
            + "WHERE INVTYPE_CODE  = '" + invtype_code + "'";
    }

    /**
     *
     * @param invtype_code String
     * @return String
     */
    public static String getInvBaseByTypeCode(String invtype_code) {
        return "SELECT * FROM INV_BASE WHERE INVTYPE_CODE = '" + invtype_code +
            "'";
    }

    /**
     *
     * @param invtype_code String
     * @return String
     */
    public static String getInvBaseByCodeOrDesc(String inv_code,
                                                String inv_desc) {
        String sql = "";
        if (!"".equals(inv_code) && !"".equals(inv_desc)) {
            sql = " INV_CODE LIKE '" + inv_code + "%' AND INV_CHN_DESC LIKE '" +
                inv_desc + "%'";
        }
        else if (!"".equals(inv_code)) {
            sql = " INV_CODE LIKE '" + inv_code + "%' ";
        }
        else if ("".equals(inv_desc)) {
            sql = " INV_CHN_DESC LIKE '" + inv_desc + "%' ";
        }
        return "SELECT * FROM INV_BASE WHERE " + sql;
    }


    /**
     * ȡ���ֵ�ȫ����Ϣ
     * @param inv_code String
     * @return String
     */
    public static String getInvBaseInfo(String inv_code) {
        return "SELECT A.INV_CODE, A.ACTIVE_FLG, A.INVTYPE_CODE, "
            + " A.INVKIND_CODE, A.INV_CHN_DESC, A.PY1, A.INV_ENG_DESC, "
            + " A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, A.MAN_NATION, "
            + " A.BUYWAY_CODE, A.USE_DEADLINE, A.COST_PRICE, A.ORDER_CODE, " 
            + " A.HYGIENE_TRADE_CODE, A.STOPBUY_FLG, A.NORMALDRUG_FLG, "
            + " A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, "
            + " A.PY2, B.PURCH_QTY, B.PURCH_UNIT, B.STOCK_QTY, B.STOCK_UNIT,"
            + " B.DISPENSE_QTY, B.DISPENSE_UNIT, C.ORDER_DESC,A.CONSIGN_FLG,A.CONSIGN_MAN_CODE, " +
              " A.SUP_CODE,A.UP_SUP_CODE,A.INV_KIND KIND,A.INV_ACCOUNT ACCOUNT "    
            + " FROM INV_BASE A, INV_TRANSUNIT B, SYS_FEE C "
            + " WHERE A.INV_CODE = B.INV_CODE AND "  
            + " A.ORDER_CODE = C.ORDER_CODE(+) "   
            + "AND A.INV_CODE = '" + inv_code + "'";
    }

    
    
    
    /**
     *
     * @param sup_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvAgentBySupOrInv(String sup_code, String inv_code) {
        String sql = "";
        if (!"".equals(sup_code) && !"".equals(inv_code)) {
            sql = "AND A.INV_CODE = '" + inv_code + "' AND A.SUP_CODE = '" +
                sup_code + "'";
        }
        else if (!"".equals(inv_code)) {
            sql = "AND A.INV_CODE = '" + inv_code + "' ";
        }
        else if ("".equals(sup_code)) {
            sql = "AND A.SUP_CODE = '" + sup_code + "' ";
        }
        return "SELECT A.SUP_CODE, A.INV_CODE, B.INV_CHN_DESC,"
            + " A.CONTRACT_PRICE, A.BILL_UNIT, A.STOCK_UNIT, "
            + " A.GIFT_QTY, A.GIFT_RATE, A.DISCOUNT_RATE, A.LAST_ORDER_DATE,"
            + " A.LAST_ORDER_QTY, A.LAST_ORDER_PRICE, A.LAST_ORDER_NO,"
            + " A.LAST_VERIFY_DATE, A.LAST_VERIFY_PRICE, A.CONTRACT_NO "
            + " FROM INV_AGENT A, INV_BASE B WHERE A.INV_CODE = B.INV_CODE " +
            sql;
    }

    /**
     * ��ѯִ���Զ������Ŀ���
     * @return String
     */ 
  //fux modify 20130522
    public static String getINVORG(String orgCode,String regionCode) {
        return " SELECT * FROM INV_ORG where org_Code=' "+orgCode+"' and region_Code='"+regionCode+"'";
    }
    
    
    //fux modify 20130522
    public static String getorg() {
        return " SELECT * FROM INV_ORG ";
    }

    /**
     * ���ݿⷿ����ڿ��ұ��в�ѯҩ��
     *
     * @return String
     */
    public static String getOrgCodeByOrgType() {
        return "SELECT COST_CENTER_CODE AS ID , COST_CENTER_CHN_DESC AS NAME , "
            + "PY1, PY2 FROM SYS_COST_CENTER WHERE ACTIVE_FLG = 'Y'";
    }

    /**
     * ���ݿⷿ���ͻ�ʿվ��ѯҩ��
     *
     * @param org_type
     *            �ⷿ���
     * @param station_flg
     *            ��ʿվ
     * @return
     */
    public static String getOrgCodeByTypeAndStation() {
        return
            "SELECT STATION_CODE AS ID , STATION_DESC AS NAME, PY1, PY2 FROM SYS_STATION";
    }

    /**
     * ���ʲ�����λ
     * @return String
     */
    public static String getMaterialloc() {
        return "SELECT * FROM INV_MATERIALLOC";
    }

    /**
     * ���ʲ�����λ
     * @param org_code String
     * @param material_loc_code String
     * @return String
     */
    public static String getMaterialloc(String org_code,
                                        String material_loc_code) {
        return "SELECT * FROM INV_MATERIALLOC WHERE ORG_CODE = '" + org_code +
            "' AND MATERIAL_LOC_CODE = '" + material_loc_code + "'";
    }

    /**
     * ȡ���������ֵ�ı������
     * @return String
     */
    public static String getInvPackTree() {
        return "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='INV_PACK'";
    }

    /**
     * ȡ���������ֵ�ϸ��
     * @param pack_code String
     * @return String
     */
    public static String getInvPackD(String pack_code) {
        return "SELECT * FROM INV_PACKD WHERE PACK_CODE= '" + pack_code + "'";
    }

    /**
     * ȡ���������ֵ�����
     * @param pack_code String
     * @return String
     */
    public static String getInvPackM(String pack_code) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE = '" + pack_code + "'";
    }

    /**
     * ȡ���������ֵ����ģ����ѯ�ã�
     * @param pack_code String
     * @return String
     */
    public static String getInvPackMQuery(String pack_code) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE like '" +
            pack_code + "%'";
    }

    /**
     * ���ݹ�����������INV_PACKM������
     * @param filter String
     * @return String
     */
    public static String getInvPackMByFilter(String filter) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE IS NOT NULL" + filter;
    }

    /**
     * ���ݽ��͹�����������INV_PACKM������
     * @param filter String
     * @return String
     */
    public static String getInvPackMByFilter(String parentId, String filter) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE LIKE '" + parentId +
            "%' " + filter;
    }

    /**
     * ȡ����������������
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getPackMaxSeq(String org_code, String pack_code) {
        return "SELECT MAX(PACK_SEQ_NO) AS PACK_SEQ_NO FROM INV_PACKSTOCKM "
            + "WHERE ORG_CODE = '" + org_code + "' AND PACK_CODE = '" +
            pack_code + "'";
    }

    /**
     * ȡ����������ϸ�ļ۸�
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getINVPackDByCostPrice(String pack_code) {
        return "SELECT B.COST_PRICE, A.QTY, A.PACK_TYPE FROM INV_PACKD A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE AND A.PACK_CODE = '" + pack_code +
            "'";
    }

    /**
     *
     * @param pack_code String
     * @return String
     */
    public static String getINVPackDInfo(String pack_code) {
        return "SELECT A.INV_CODE, B.INV_CHN_DESC, B.STOCK_UNIT, "
            + " A.QTY, B.COST_PRICE, B.SEQMAN_FLG, '0' AS RECOUNT_TIME, "
            + "'N' AS ONCE_USE_FLG "
            + " FROM INV_PACKD A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.PACK_CODE = '" + pack_code + "'";
    }

    /**
     *
     * @param pack_code String
     * @return String
     */
    public static String getINVPackDInfo(String pack_code, int pack_seq_no) {
        return "SELECT A.INV_CODE,B.INV_CHN_DESC,B.STOCK_UNIT,C.QTY,"
            + "B.COST_PRICE, B.SEQMAN_FLG, C.RECOUNT_TIME, C.ONCE_USE_FLG, "
            + "C.INVSEQ_NO "
            + " FROM INV_PACKD A, INV_BASE B, INV_PACKSTOCKD C "
            + "WHERE A.INV_CODE = B.INV_CODE "
            + "AND A.INV_CODE = C.INV_CODE "
            + "AND A.PACK_CODE = C.PACK_CODE "
            + "AND B.INV_CODE = C.INV_CODE "
            + "AND A.PACK_CODE = '" + pack_code +
            "' AND C.PACK_SEQ_NO = " + pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getINVStockDDNotByPack(String org_code,
                                                String inv_code) {
        return "SELECT * FROM INV_STOCKDD WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code +
            "' AND PACK_FLG = 'N' ORDER BY INVSEQ_NO ";
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getINVStockQty(String org_code,
                                        String inv_code) {
        return "SELECT SUM(A.STOCK_QTY) AS STOCK_QTY, B.INV_CHN_DESC "
            + " FROM INV_STOCKD A, INV_BASE B "
            + " WHERE A.ORG_CODE = '" + org_code + "' AND A.INV_CODE = '" +
            inv_code + "' AND A.INV_CODE = B.INV_CODE GROUP BY B.INV_CHN_DESC";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getINVPackStockQty(String org_code, String pack_code,
                                            int pack_seq_no, int batch_no) {
        return "SELECT QTY AS STOCK_QTY FROM INV_PACKSTOCKM "
            + " WHERE ORG_CODE = '" + org_code + "' AND PACK_CODE = '" + 
            pack_code + "' AND PACK_SEQ_NO = " + pack_seq_no +
            " AND STATUS = 0 AND PACK_BATCH_NO = "+batch_no;		//STATUS <> 1
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     */
    public static String getINVPackStockMQty(String org_code, String pack_code,
                                             int pack_seq_no) {
        return "SELECT QTY AS STOCK_QTY FROM INV_PACKSTOCKM "
            + " WHERE ORG_CODE = '" + org_code + "' AND PACK_CODE = '" +
            pack_code + "' AND PACK_SEQ_NO = " + pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getINVStockQtyOrderBySEQ(String org_code,
                                                  String inv_code) {
        return "SELECT STOCK_QTY FROM INV_STOCKD" + " WHERE ORG_CODE = '" +
            org_code + "' AND INV_CODE = '" +
            inv_code + "' ORDER BY BATCH_SEQ";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @return String
     */
    public static String getINVPackStockM(String org_code, String pack_code,
                                          int pack_seq_no) {
        return "SELECT * FROM INV_PACKSTOCKM WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO =" +
            pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param inv_code String
     * @return String
     */
    public static String getINVPackStockD(String org_code, String pack_code,
                                          int pack_seq_no) {
        return "SELECT * FROM INV_PACKSTOCKD WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO =" +
            pack_seq_no + "'";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param inv_code String
     * @return String
     */
    public static String getINVPackStockD(String org_code, String pack_code,
                                          int pack_seq_no, String inv_code) {
        return "SELECT * FROM INV_PACKSTOCKD WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO =" +
            pack_seq_no + " AND INV_CODE = '" + inv_code + "'";
    }

    /**
     *
     * @param sup_type_code String
     * @return String
     */
    public static String getINVSupType(String sup_type_code) {
        return "SELECT * FROM INV_SUPTYPE WHERE SUPTYPE_CODE = '" +
            sup_type_code + "'";
    }

    /**
     *
     * @param request_no String
     * @return String
     */
    public static String getINVSupRequestD(String request_no) {
        return "SELECT * FROM INV_SUPREQUESTD WHERE REQUEST_NO = '" +
            request_no + "'";
    }

    /**
     *
     * @param request_no String
     * @return String
     */
    public static String getInvSupRequestDInv(String request_no) {
        return "SELECT 'Y' AS SELECT_FLG,A.SEQ_NO,A.INV_CODE,B.INV_CHN_DESC, "
            + " A.QTY - A.ACTUAL_QTY AS QTY, A.STOCK_UNIT, B.SEQMAN_FLG,  "
            + " B.COST_PRICE FROM INV_SUPREQUESTD A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE AND A.UPDATE_FLG IN ('0', '1') "
            + " AND A.REQUEST_NO = '" + request_no + "'";
    }

    /**
     *
     * @param request_no String
     * @return String
     */
    public static String getInvSupRequestDPack(String request_no) {
        return "SELECT 'Y' AS SELECT_FLG, A.SEQ_NO, A.INV_CODE, "
            + " B.PACK_DESC AS INV_CHN_DESC, A.QTY - A.ACTUAL_QTY AS QTY, "
            + " A.STOCK_UNIT, B.SEQ_FLG FROM INV_SUPREQUESTD A, INV_PACKM B "
            + " WHERE A.INV_CODE = B.PACK_CODE AND A.UPDATE_FLG IN ('0', '1') "
            + " AND A.REQUEST_NO = '" + request_no + "'";
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvSupInvChoose(String org_code, String inv_code) {
        return "SELECT 'N' AS SELECT_FLG, A.INVSEQ_NO, B.COST_PRICE, "
            + " A.ORG_CODE, A.BATCH_NO, A.VALID_DATE, A.BATCH_SEQ "
            + " FROM INV_STOCKDD A, INV_BASE B "
            + "  WHERE A.INV_CODE = B.INV_CODE AND A.ORG_CODE = '" + org_code +
            "' AND A.INV_CODE = '" + inv_code + "' ORDER BY A.INVSEQ_NO";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getINvSupPackChoose(String org_code, String pack_code) {
        return "SELECT 'N' AS SELECT_FLG, A.PACK_SEQ_NO, "
            + " A.USE_COST + A.ONCE_USE_COST AS COST_PRICE, A.ORG_CODE, A.BARCODE, A.PACK_BATCH_NO "
            + " FROM INV_PACKSTOCKM A WHERE A.ORG_CODE = '" + org_code +
            "' AND A.PACK_CODE = '" + pack_code + "' AND A.STATUS = '0' "
            + " ORDER BY A.PACK_SEQ_NO";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getInvPackCostPrice(String org_code, String pack_code) {
        return "SELECT USE_COST + ONCE_USE_COST AS COST_PRICE "
            + " FROM INV_PACKSTOCKM WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO = 0";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param qty double
     * @return String
     */
    public static String getINVPackStockDInfo(String org_code, String pack_code,
                                              int pack_seq_no, double qty, int batch_no) {
        return "SELECT B.INV_CHN_DESC, A.INVSEQ_NO, C.QTY * "
            + qty + " AS QTY, A.STOCK_UNIT, A.COST_PRICE, A.ONCE_USE_FLG, "
            + " '' AS BATCH_NO, '' AS VALID_DATE, A.INV_CODE, A.RECOUNT_TIME "
            + " FROM INV_PACKSTOCKD A, INV_BASE B, INV_PACKD C "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.INV_CODE = C.INV_CODE "
            + " AND A.PACK_CODE = C.PACK_CODE "
            + " AND B.INV_CODE = C.INV_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' AND A.PACK_CODE = '" +
            pack_code + "' AND A.PACK_SEQ_NO =" + pack_seq_no + " AND A.PACK_BATCH_NO = " + batch_no;
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getINVSupDispenseDDInfo(String dispense_no,
                                                 String pack_code,
                                                 int pack_seq_no) {
        return "SELECT B.INV_CHN_DESC, A.INVSEQ_NO, A.QTY, A.STOCK_UNIT, "
            + " A.COST_PRICE, A.ONCE_USE_FLG, A.BATCH_NO, A.VALID_DATE, "
            + " A.INV_CODE FROM INV_SUP_DISPENSEDD A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' "
            + " AND A.PACK_CODE = '" + pack_code + "' "
            + " AND A.PACK_SEQ_NO = " + pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @return String
     */
    public static String getInvSupDispenseDD(String org_code, String pack_code,
                                             int pack_seq_no, int batch_no) {
        return "SELECT A.PACK_CODE, A.PACK_SEQ_NO, A.INV_CODE, "
            + " A.INVSEQ_NO, A.ONCE_USE_FLG, B.QTY, A.STOCK_UNIT, "
            + " A.COST_PRICE, A.BATCH_SEQ, C.BATCH_NO, C.VALID_DATE, A.BARCODE, A.PACK_BATCH_NO "
            + " FROM INV_PACKSTOCKD A, INV_PACKD B, INV_STOCKD C "
            + " WHERE A.PACK_CODE = B.PACK_CODE "
            + " AND A.INV_CODE = B.INV_CODE "
            + " AND A.ORG_CODE = C.ORG_CODE "
            + " AND A.INV_CODE = C.INV_CODE "
            + " AND A.BATCH_SEQ = C.BATCH_SEQ "
            + " AND B.INV_CODE = C.INV_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' AND A.PACK_CODE = '" +
            pack_code + "' AND A.PACK_SEQ_NO = " + pack_seq_no + " AND A.PACK_BATCH_NO = " + batch_no;
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getInvSupDispenseInvForPrint(String dispense_no) {
        return "SELECT B.INV_CHN_DESC, A.INVSEQ_NO, B.DESCRIPTION, "
            + " A.COST_PRICE, A.QTY, C.UNIT_CHN_DESC, "
            + " A.QTY * A.COST_PRICE AS AMT, A.BATCH_NO, A.VALID_DATE "
            + " FROM INV_SUP_DISPENSED A, INV_BASE B, SYS_UNIT C "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.STOCK_UNIT = C.UNIT_CODE "
            + " AND B.STOCK_UNIT = C.UNIT_CODE "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' ";
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getInvSupDispensePackForPrint(String dispense_no) {
        return "SELECT B.PACK_DESC, A.INVSEQ_NO, A.COST_PRICE, A.QTY, "
            + " A.COST_PRICE * A.QTY AS AMT "
            + " FROM INV_SUP_DISPENSED A, INV_PACKM B "
            + " WHERE A.INV_CODE = B.PACK_CODE AND A.DISPENSE_NO = '" +
            dispense_no + "' ";
    }

    /**
     * ����INV_STOCKM�����
     * @param org_code String
     * @param inv_code String
     * @param qty double
     * @return String
     */
    public static String updateInvStockM(String org_code, String inv_code,
                                         double qty) {
        return "UPDATE INV_STOCKM SET STOCK_QTY = STOCK_QTY - " + qty +
            " WHERE ORG_CODE = '" + org_code + "' AND INV_CODE = '" + inv_code +
            "'";
    }

    /**
     * ����INV_STOCKD�����
     * @param org_code String
     * @param inv_code String
     * @param batch_seq int
     * @param qty double
     * @return String
     */
    public static String updateInvStockD(String org_code, String inv_code,
                                         int batch_seq, double qty) {
        return "UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY - " + qty +
            " WHERE ORG_CODE = '" + org_code + "' AND INV_CODE = '" + inv_code +
            "' AND BATCH_SEQ = " + batch_seq;
    }

    /**
     * ����INV_PACKSTOCKD�����
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param inv_code String
     * @param qty double
     * @return String
     */
    public static String updateInvPackStockD(String org_code, String pack_code,
                                             int pack_seq_no, String inv_code,
                                             double qty, int recount_time) {
        String sql = "";
        if (recount_time != 0) {
            sql = ", RECOUNT_TIME = " + recount_time;
        }
        return "UPDATE INV_PACKSTOCKD SET QTY = QTY + " + qty +
            sql + " WHERE ORG_CODE = '" +
            org_code + "' AND PACK_CODE = '" +
            pack_code + "' AND PACK_SEQ_NO = " + pack_seq_no +
            " AND INV_CODE = '" + inv_code + "'";
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getIncSupDispenseChoose(String dispense_no,
                                                 String org_code) {
        return "SELECT 'Y' AS SELECT_FLG, A.INVSEQ_NO AS PACK_SEQ_NO, "
            + " A.INV_CODE AS PACK_CODE, B.PACK_DESC, A.QTY, C.USE_COST, "
            + " C.ONCE_USE_COST, C.STATUS "
            + " FROM INV_SUP_DISPENSED A, INV_PACKM B, INV_PACKSTOCKM C "
            + " WHERE A.INV_CODE = B.PACK_CODE "
            + " AND A.INV_CODE = C.PACK_CODE "
            + " AND B.PACK_CODE = C.PACK_CODE "
            + " AND A.INVSEQ_NO = C.PACK_SEQ_NO "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' "
            + " AND C.ORG_CODE = '" + org_code + "' ";
    }

    /**
     * �õ���ѯ�Ʒ���Ŀ�ֵ��sql
     * @param parm TParm
     * @return String
     */
    public static String getQuerySuptitemSql(TParm parm) {
        String sql = "SELECT * FROM INV_SUPTITEM ";
        String filter = "";
        //���˱��
        String value = parm.getValue("SUPITEM_CODE");
        if (value != null && value.length() != 0) {
            if (filter.length() != 0)
                filter += " AND ";
            filter += "SUPITEM_CODE LIKE '" + value + "%'";
        }

        //�������
        value = parm.getValue("SUPITEM_DESC");
        if (value != null && value.length() != 0) {
            if (filter.length() != 0)
                filter += " AND ";
            filter += " SUPITEM_DESC like '" + value + "%'";
        }
        value = parm.getValue("PY1");
        if (value != null && value.length() != 0) {
            if (filter.length() != 0)
                filter += " AND ";
            filter += " PY1 like '" + value + "%'";
        }

        if (filter.length() > 0)
            sql = sql + " WHERE " + filter;
        sql = sql + " ORDER BY SUPITEM_CODE ";
        //System.out.println("dsfdf=" + sql);
        return sql;
    }

    /**
     * ����ԭ��
     * @return String
     */
    public static String getReason(){
        return "SELECT * FROM INV_REASON ";
    }

    /**
     * ����ԭ��
     * @return String
     */
    public static String getReason(String reason_code) {
        return "SELECT * FROM INV_REASON WHERE REN_CODE = '" + reason_code +
            "'";
    }

    /**
     * ���ݲ��Ų�ѯ���������Ϣ-�Զ�����
     * @param orgCode
     * @return
     * @author fux   INV_STOCKM ��INV_STOCKD������
     * @date 20130522
     */
    public static String queryStockM(String orgCode,String toOrgCode,String fixedType){
    	String condition = " ";
    	String wheresql= "" ;
    	String wheresql1 ="" ;
    	if("1".equals(fixedType)){//
    		condition = "   HAVING (SUM(A.STOCK_QTY)/C.STOCK_QTY) < A.SAFE_QTY ";
    	}else if("2".equals(fixedType)){
    		condition = "   HAVING (SUM(A.STOCK_QTY)/C.STOCK_QTY) < A.MIN_QTY ";
    	}
//    	 if(orgCode.equals("011201")){
//    		 wheresql = " AND A.INV_CODE  NOT LIKE '08%' "  ;  //�����ֵ�ĲĲ��ܶ�������ʱ����д��
//    	 } 
    	 if(toOrgCode.length()>0)   wheresql1 = " AND A.DISPENSE_ORG_CODE = '"+toOrgCode+"'"  ;
    	String sql =  " SELECT A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY, "+          
    	              " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY,A.STOCK_QTY,"+    
    	              " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY,A.STOCK_UNIT "+  
    	              " FROM INV_STOCKM A,INV_BASE B,INV_TRANSUNIT C"+  
    	              " WHERE A.ORG_CODE='" + orgCode + "'  "+ 
    	              wheresql+ " "+
    	              wheresql1+ " "+
    	              " AND A.DISPENSE_FLG='Y'    "+  
                      " AND A.INV_CODE=B.INV_CODE "+  
    	              " AND A.INV_CODE=C.INV_CODE "      
                       + condition + " "+   
    	              " GROUP BY A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY,"+            
    	              " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY,A.STOCK_QTY," +
    	              " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY, A.STOCK_UNIT,C.STOCK_QTY  ";
    	return sql;
    }
    
    
	/**
     * ������������-�Զ�����
     * @param orgCode
     * @return
     * @author fux
     * @date 20130527 
     */   
    public static String saveRequestMAuto(TParm parm ){ 
    	String sql = " INSERT INTO INV_REQUESTM(REQUEST_NO,REQUEST_TYPE, REQUEST_DATE," +
    			     " FROM_ORG_CODE,TO_ORG_CODE,REN_CODE,URGENT_FLG, REMARK," +
                     " FINAL_FLG , OPT_USER, OPT_DATE, OPT_TERM) "
    		       + " VALUES("  
    		       + " '" + parm.getValue("REQUEST_NO")+ "', 'ATO', TO_DATE('" + parm.getValue("REQUEST_DATE").substring(0, 19) + "','YYYY-MM-DD HH24:MI:SS'),"
    		       + " '" + parm.getValue("FROM_ORG_CODE")+ "','" + parm.getValue("TO_ORG_CODE")+ "','" + parm.getValue("REN_CODE")+ "'," +
    		       	 " '" + parm.getValue("URGENT_FLG")+ "','" + parm.getValue("REMARK")+ "',"
    		       + " '" + parm.getValue("FINAL_FLG")+ "', 'OPTUSER', sysdate,'OPTID'"  
    		       + " ) ";   
    	return sql;  
    }   
    public static String deleteRequestMAuto(TParm parm ){ 
    	String sql = "DELETE FROM  INV_REQUESTM  WHERE REQUEST_NO = '"+parm.getValue("REQUEST_NO")+"' "  ;
    	return sql;  
    }   
       
    /**
     * ����������ϸ��-�Զ�����
     * @param orgCode
     * @return
     * @author fux
     * @date 20130527  
     */  
    public static String saveRequestDAuto(TParm parm ){  
    	//fux need modify
    	String sql = " INSERT INTO INV_REQUESTD(REQUEST_NO, SEQ_NO, INV_CODE, " +
    			     " INVSEQ_NO, QTY, ACTUAL_QTY, " +
    			     " BATCH_SEQ,BATCH_NO,VALID_DATE,FINA_TYPE," +
    			     " OPT_USER, OPT_DATE, OPT_TERM )"   
    		       + " VALUES("                              
    		       + " '" + parm.getValue("REQUEST_NO")+ "', " + parm.getValue("SEQ_NO")+ ", '" + parm.getValue("INV_CODE")+ "',"
    		       + "  " + parm.getValue("INVSEQ_NO")+ "," + parm.getValue("QTY")+ ",0,"
    		       + " '" + parm.getValue("BATCH_NO")+ "','" + parm.getValue("BATCH_NO")+ "','" + parm.getValue("VALID_DATE")+ "'," +
    		       	 " '" + parm.getValue("FINA_TYPE")+ "','OPTUSER', sysdate,'OPTIP' "
    		       + " ) ";    
    	return sql;   
    }   
    
    
    /**
     * ���涩��/�ƻ�����-�Զ�����
     * @param orgCode
     * @return
     * @author fux 
     * @date 20130527    
     */  
    public static String savePurOrderMAuto(TParm parm ){
    	String sql = " INSERT INTO INV_PURORDERM (PURORDER_NO, PURORDER_DATE,FINAL_FLG, " +
    			     " ORG_CODE, SUP_CODE, RES_DELIVERY_DATE,  " 
    			   + " OPT_USER, OPT_DATE, OPT_TERM,REN_CODE,CHECK_FLG,CHECK_DATE,CHECK_USER," +
    			   		" FROM_TYPE,CON_ORG,CON_FLG) "
    		       + " VALUES("                       
    		       + " '" + parm.getValue("PURORDER_NO")+ "',sysdate,'N' ," +
    		       		" '" + parm.getValue("ORG_CODE")+ "'," + " '" + parm.getValue("SUP_CODE")
    		       + "',null,'OPTUSER', sysdate,'OPTIP','R06','Y',sysdate,'OPTUSER','0'," +
    		       		" '" + parm.getValue("CON_ORG")+ "','" + parm.getValue("CON_FLG")+ "' "   
    		       + " ) ";
    	return sql;  
    } 
    /**
     * ɾ������
     * @param parm
     * @return
     */
    public static String deletePurOrderMAuto(TParm parm ){
    	String sql = "DELETE FROM INV_PURORDERM WHERE PURORDER_NO ='"+parm.getValue("PURORDER_NO")+"' "  ;
    	return sql;  
    } 
   
    /**
     * ���涩��/�ƻ���ϸ��-�Զ�����
     * @param orgCode
     * @return  
     * @author fux  
     * @date 20130527    
     */  
    public static String savePurOrderDAuto(TParm parm){ 
    	String sql = " INSERT INTO INV_PURORDERD (PURORDER_NO, SEQ_NO, " +
    			     " INV_CODE, PURORDER_QTY, GIFT_QTY, BILL_UNIT, " 
    			   + " PURORDER_PRICE,PURORDER_AMT,STOCKIN_SUM_QTY, " +   
    			   	 " OPT_USER, OPT_DATE, OPT_TERM) "  
    		       + " VALUES("     
    		       + " '" + parm.getValue("PURORDER_NO")+ "', " + parm.getValue("SEQ_NO")+ ", '" + parm.getValue("INV_CODE")+ "',"
    		       + "  " + parm.getValue("PURORDER_QTY")+ ",0,'" + parm.getValue("BILL_UNIT")+ "','" + parm.getValue("PURORDER_PRICE")+ "',"
    		       + "  '" + parm.getValue("PURORDER_AMT")+ "',0,'OPTUSER', sysdate,'OPTIP' " 
    		       + " ) ";   
    	return sql;   
    } 
    
    /**
     * ��ȡ������;��     ����
     */
    public static String  getOnWayQuety(String invCode){
    	String sql = " SELECT  D.PURORDER_QTY  FROM  INV_PURORDERM M ,INV_PURORDERD D  WHERE " +
    			     " M.PURORDER_NO = D.PURORDER_NO " +
    			     " AND M.FINAL_FLG = 'N' " +
    			     " AND D.INV_CODE = '"+invCode+"'"   ;
    	return  sql ;
    }
    /**
     * ��ȡ������;��   ������
     */
    public static String  getOnWayRequest(String invCode){
    	String sql = " SELECT  D.QTY  FROM  INV_REQUESTM M ,INV_REQUESTD D  WHERE " +
	     " M.REQUEST_NO= D.REQUEST_NO " +
	     " AND M.FINAL_FLG = 'N' " +
	     " AND D.INV_CODE = '"+invCode+"'"   ;
     return  sql ;
    }
    /**
     * �������ʴ�����������Ϣ
     *
     * @param invcode 
     * @return   
     */ 
    public static String getINVInfoByOrder(String invcode) {    
        return " SELECT  A.INV_CODE,A.COST_PRICE,B.STOCK_UNIT, B.STOCK_QTY," +
        	   " B.PURCH_QTY, B.PURCH_UNIT,B.DISPENSE_UNIT,B.DISPENSE_QTY "+
               " FROM INV_BASE A, INV_TRANSUNIT B "+
               " WHERE A.INV_CODE = B.INV_CODE "+
               " AND A.INV_CODE = '"
               + invcode + "'";    
    }
//------------��Ӧ����������start---------------
    
    /**
     * ��ѯ�����������sql
     * @param packCode String (����)
     * @param packSeqNo int (���)
     * @return String
     */
    public static String getQueryStockDSql(String packCode, int packSeqNo) {
        String sql = "select * from INV_PACKSTOCKD";
        if (packCode != null && packCode.length() != 0) {
            sql += " WHERE PACK_CODE ='" + packCode + "'";
            if (packSeqNo >= 0)
                sql += " AND PACK_SEQ_NO=" + packSeqNo;
        }
        return sql;
    }
    
    /**
     * ��ʼ���������������datastore GYSUsed
     * @return String
     */
    public static String getInitPackStockMSql() {
        return "select * from INV_PACKSTOCKM WHERE PACK_CODE IS NULL";
    }

    /**
     * ��ʼ�������������ϸ��sql  GYSUsed
     * @return String
     */
    public static String getInitPackStockDSql() {
        return "select * from INV_PACKSTOCKD WHERE PACK_CODE IS NULL";
    }
    
    
    /**
     * ȡ��ҽԺ����sql
     * @return String
     */
    public static String getHospArea() {
        String sql = "select HOSP_CHN_DESC,HOSP_CHN_ABN from SYS_HOSP_AREA";
        return sql;
    }
    
    /**
     * ȡ���������ƺ͵�λ
     * @param inv_code String
     * @return String
     */
    public static String getInvInfo(String inv_code) {
        return "SELECT  "
            + " A.INV_CHN_DESC, S.UNIT_CHN_DESC "   
            + " FROM INV_BASE A, INV_TRANSUNIT B, SYS_UNIT S "
            + " WHERE A.INV_CODE = B.INV_CODE AND S.UNIT_CODE = A.STOCK_UNIT "  
            + "AND A.INV_CODE = '" + inv_code + "'";
    }
    
    //------------��Ӧ����������end---------------

      
}
