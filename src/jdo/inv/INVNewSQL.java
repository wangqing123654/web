package jdo.inv;

import java.sql.Timestamp;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ������SQL��װ
 * </p>
 *
 * <p>
 * Description: ������SQL��װ
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
 * @author zhangy 2009.05.07
 * @version 1.0
 */

public class INVNewSQL {  

    /**
     * ���ʲ����趨
     *
     * @return String
     */
    public static String getINDSysParm() {
        return
            "SELECT FIXEDAMOUNT_FLG,REUPRICE_FLG,DISCHECK_FLG,GPRICE_FLG,UNIT_TYPE,"
            +
            "GOWN_COSTRATE,GNHI_COSTRATE,GOV_COSTRATE,UPDATE_GRETAIL_FLG,WOWN_COSTRATE,"
            +
            "WNHI_COSTRATE,WGOV_COSTRATE,UPDATE_WRETAIL_FLG,MANUAL_TYPE,OPT_USER,AUTO_FILL_TYPE,TOXIC_STORAGE_ORG,"
            + "OPT_DATE,OPT_TERM, PHA_PRICE_FLG FROM IND_SYSPARM";
    }

    /**
     * ��ѯҩ���������
     *
     * @param batch_flg
     * @return String
     */
    public static String getINDBatchLockORG(String batch_flg) {
        return "SELECT ORG_CODE,ORG_CHN_DESC,BATCH_FLG,OPT_USER,OPT_DATE"
            + ",OPT_TERM FROM IND_ORG WHERE BATCH_FLG = '" + batch_flg
            + "' ORDER BY SEQ,ORG_CODE";
    }

//    /**
//     * У������
//     *
//     * @param REQUEST_NO  ���쵥��
//     * @return String
//     */
//    public static String checkData(String  REQUEST_NO)
//    {
//    	String sql="SELECT REQUEST_NO FROM IND_REQUESTD GROUP BY REQUEST_NO";
//    	return sql;
//    	
//    }

    /**
     * ����ҩ����(����)
     */
    public static String getDrugNoM(TParm parm,String sql)
    {
    	return "SELECT 'Y' AS SELECT_FLG, A.REQUEST_NO, A.ORDER_CODE, C.ORDER_DESC," +
    			"C.SPECIFICATION, A.QTY AS DOSAGE_QTY, D.UNIT_CHN_DESC, A.STOCK_PRICE," +
    			"A.STOCK_PRICE * A.QTY AS STOCK_AMT, A.RETAIL_PRICE AS OWN_PRICE," +
    			"A.RETAIL_PRICE * A.QTY AS OWN_AMT," +
    			"A.RETAIL_PRICE * A.QTY - A.STOCK_PRICE * A.QTY AS DIFF_AMT " +
    			"FROM IND_REQUESTD A, IND_REQUESTM B, SYS_FEE C, SYS_UNIT D,PHA_BASE E " +
    			"WHERE A.REQUEST_NO = B.REQUEST_NO " +
    			"AND A.ORDER_CODE = C.ORDER_CODE " +
    			"AND A.UNIT_CODE = D.UNIT_CODE " +  			
    			"AND B.APP_ORG_CODE = "+parm.getData("APP_ORG_CODE") +
    			"AND B.REQUEST_DATE BETWEEN TO_DATE ("+parm.getData("START_DATE")+", 'YYYYMMDDHH24MISS') " +
    			"AND TO_DATE ("+parm.getData("END_DATE")+", 'YYYYMMDDHH24MISS') " +sql+
    			"ORDER BY A.REQUEST_NO, A.SEQ_NO";
    	}
    
    /**
     * ����ҩ����(��ϸ)
     */
    public static String getDrugNoD(TParm parm,String sql)
    {
    	return "SELECT 'Y' AS SELECT_FLG,F.NAME,B.OPT_DATE, A.ORDER_CODE, C.ORDER_DESC," +
    			"C.SPECIFICATION, A.QTY AS DOSAGE_QTY, D.UNIT_CHN_DESC, A.STOCK_PRICE," +
    			"A.STOCK_PRICE * A.QTY AS STOCK_AMT, A.RETAIL_PRICE AS OWN_PRICE," +
    			"A.RETAIL_PRICE * A.QTY AS OWN_AMT," +
    			"A.RETAIL_PRICE * A.QTY - A.STOCK_PRICE * A.QTY AS DIFF_AMT " +
    			"FROM IND_REQUESTD A, IND_REQUESTM B, SYS_FEE C, SYS_UNIT D,PHA_BASE E ,SKT_USER F" +
    			"WHERE A.REQUEST_NO = B.REQUEST_NO " +
    			"AND F.ID=B.OPT_USER" +
    			"AND A.ORDER_CODE = C.ORDER_CODE " +
    			"AND A.UNIT_CODE = D.UNIT_CODE " +
    			"AND B.TO_ORG_CODE="+parm.getData("TO_ORG_CODE")+    			
    			"AND B.APP_ORG_CODE = "+parm.getData("APP_ORG_CODE") +
    			"AND B.REQUEST_DATE BETWEEN TO_DATE ("+parm.getData("START_DATE")+", 'YYYYMMDDHH24MISS') " +
    			"AND TO_DATE ("+parm.getData("END_DATE")+", 'YYYYMMDDHH24MISS') " +sql+
    			"ORDER BY A.REQUEST_NO, A.SEQ_NO";
    	}
    
	/**
     * ��ѯҩ�ⲿ����Ϣ
     * <br>
     * �Ѿ������˶�Ժ��(region)
     * @return String
     */
    public static String getINDORG(String region_code) {
        return "SELECT ORG_CODE , ORG_CHN_DESC , PY1 , PY2 , SEQ , "
            + "DESCRIPTION , ORG_FLG , ORG_TYPE ,SUP_ORG_CODE , EXINV_FLG ,"
            +
            " REGION_CODE , STATION_FLG , INJ_ORG_FLG ,DECOCT_CODE , ATC_FLG ,FIXEDAMOUNT_FLG,AUTO_FILL_TYPE, "
            +
            "OPT_USER ,OPT_DATE , OPT_TERM FROM IND_ORG WHERE REGION_CODE = '"
            + region_code + "' ORDER BY ORG_CODE, SEQ ";
    }

    /**
     * ��ѯҩ�ⲿ����Ϣ
     * @param org_code String
     * @return String
     */
    public static String getINDORG(String org_code, String region_code) {
        return "SELECT ORG_CODE , ORG_CHN_DESC , PY1 , PY2 , SEQ , "
            + "DESCRIPTION , ORG_FLG , ORG_TYPE ,SUP_ORG_CODE , EXINV_FLG ,"
            +
            " REGION_CODE , STATION_FLG , INJ_ORG_FLG ,DECOCT_CODE , ATC_FLG  ,FIXEDAMOUNT_FLG,AUTO_FILL_TYPE, "
            +
            "OPT_USER ,OPT_DATE , OPT_TERM FROM IND_ORG WHERE ORG_CODE ='" +
            org_code + "' AND REGION_CODE = '" + region_code + "' ORDER BY SEQ, ORG_CODE ";
    }


    /**
     * �����������ҩ�ⲿ���б�
     * @param condition String
     * @param flg String
     * @return String
     */
    public static String getIndOrgComobo(String condition, String flg, String region_code) {
        String type = "";
        if (!"".equals(condition)) {
            type = " WHERE ORG_TYPE " + condition;
        }
        if (!"".equals(flg)) {
            type += " STATION_FLG = '" + flg + "' ";
        }
        return
            "SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME FROM IND_ORG " + type
            + " AND REGION_CODE = '" + region_code + "'  "
            + " ORDER BY ORG_CODE,SEQ";
    }

    /**
     * ��ѯҩ�ⲿ�����
     *
     * @param org_code
     *            ҩ�����
     * @return String
     */
    public static String getINDOrgType(String org_code) {
        return "SELECT ORG_TYPE FROM IND_ORG WHERE ORG_CODE = '" + org_code
            + "'";
    }

    /**
     * ���ݿⷿ����ڿ��ұ��в�ѯҩ��
     * ����˶�Ժ������,region_code
     * @param org_type
     *            �ⷿ���
     * @return String
     */
    public static String getOrgCodeByOrgType(String org_type, String region_code) {
        String sql =
            "SELECT DEPT_CODE AS ID , DEPT_CHN_DESC AS NAME , PY1, PY2 FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y'";
        if ("C".equals(org_type)) {
            sql += " AND CLASSIFY != '2' AND REGION_CODE='" + region_code + "' ORDER BY SEQ , DEPT_CODE";
        }
        else {
            sql += " AND CLASSIFY = '2' AND REGION_CODE='" + region_code + "' ORDER BY SEQ , DEPT_CODE";
        }
        return sql;
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
    public static String getOrgCodeByTypeAndStation(String org_type,
        boolean station_flg, String region_code) {
        String sql = "";
        if (!"C".equals(org_type)) {
            sql =
                "SELECT DEPT_CODE AS ID , DEPT_CHN_DESC AS NAME, PY1, PY2 FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y'"
                + " AND CLASSIFY = '2'  AND REGION_CODE='" + region_code + "'  ORDER BY SEQ , DEPT_CODE";
        }
        else if (station_flg) {
            sql =
                "SELECT STATION_CODE AS ID , STATION_DESC AS NAME, PY1, PY2 FROM SYS_STATION WHERE REGION_CODE='" + region_code + "'";
        }
        else {
            sql =
                "SELECT DEPT_CODE AS ID , DEPT_CHN_DESC AS NAME, PY1, PY2 FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y'"
                + " AND CLASSIFY != '2'  AND REGION_CODE='" + region_code + "' ORDER BY SEQ , DEPT_CODE";
        }
        return sql;
    }

    /**
     * ��ѯ��λ
     *
     * @return String
     */
    public static String getMaterialloc() {
        return "SELECT ORG_CODE , MATERIAL_LOC_CODE , MATERIAL_CHN_DESC , MATERIAL_ENG_DESC ,PY1 ,"
            + " PY2 ,SEQ , DESCRIPTION , OPT_USER , OPT_DATE ,ORDER_CODE,ORDER_DESC,ELETAG_CODE,"
            +
            " OPT_TERM FROM IND_MATERIALLOC ORDER BY SEQ , ORG_CODE , MATERIAL_LOC_CODE";
    }

    /**
     * ������λ
     * @param parm
     * @return String 
     * @author liyh 
     * @date  20121019
     */
    public static String saveMaterialLoc(TParm parm){
        String sql = " INSERT INTO JAVAHIS.IND_MATERIALLOC (ORG_CODE, MATERIAL_LOC_CODE, MATERIAL_CHN_DESC, MATERIAL_ENG_DESC, PY1, PY2, SEQ, " 
        		   + " DESCRIPTION, OPT_USER, OPT_DATE, OPT_TERM, ORDER_CODE, ELETAG_CODE, ORDER_DESC) VALUES " 
        		   + " ('" + parm.getValue("ORG_CODE")+ "', '" + parm.getValue("MATERIAL_LOC_CODE")+ "', '" + parm.getValue("MATERIAL_CHN_DESC")+ "', "
        		   + " '" + parm.getValue("MATERIAL_ENG_DESC")+ "', '" + parm.getValue("PY1")+ "', '" + parm.getValue("PY2")+ "', "
        		   + " " + parm.getValue("SEQ")+ ", '" + parm.getValue("DESCRIPTION")+ "', '" + parm.getValue("OPT_USER")+ "', " 
        		   + " sysdate, '" + parm.getValue("OPT_TERM")+ "', '" + parm.getValue("ORDER_CODE")+ "'," 
        		   + " '" + parm.getValue("ELETAG_CODE")+ "', '" + parm.getValue("ORDER_DESC")+ "') ";
        return sql;
    }
    /**
     * ����ҩ�ⲿ�ź���λ�����ѯ
     *
     * @param org_code
     *            ҩ�ⲿ�Ŵ���
     * @param material_loc_code
     *            ��λ����
     * @return String
     */
    public static String getMaterialloc(String org_code,
                                        String material_loc_code) {
        return "SELECT ORG_CODE , MATERIAL_LOC_CODE FROM IND_MATERIALLOC"
            + " WHERE ORG_CODE = '" + org_code
            + "' AND MATERIAL_LOC_CODE = '" + material_loc_code + "'";
    }
    
    /**
     * ��ѯ��λ������
     *
     * @return String
     * @author liyh 
     * @date  20121019
     */
    public static String getMaxSeqByMaterialLoc() {
        return " SELECT MAX(NVL(SEQ,0)) AS SEQ FROM IND_MATERIALLOC ";
    }

    /**
     * ����ҩ�ⲿ�Ų�ѯ��λ
     *
     * @param org_code
     *            ҩ�ⲿ��
     * @return String
     */
    public static String getMaterialloc(String org_code) {
        return
            "SELECT MATERIAL_LOC_CODE , MATERIAL_CHN_DESC FROM IND_MATERIALLOC"
            + " WHERE ORG_CODE ='" + org_code + "' ORDER BY SEQ";
    }

    /**
     * �Զ���������
     *
     * @return String
     */
    public static String getAssignorg() {
        return
            "SELECT ORG_CODE , CYCLE_TYPE , ASSIGNED_DAY , OPT_USER , OPT_DATE ,"
            + " OPT_TERM FROM INV_ASSIGNORG ORDER BY ORG_CODE";
    }

    /**
     * �Զ���������
     *
     * @param org_code
     *            ���Ŵ���
     * @return String
     */
    public static String getAssignorg(String org_code) {
        return "SELECT ORG_CODE , CYCLE_TYPE FROM INV_ASSIGNORG "
            + "WHERE ORG_CODE = '" + org_code + "'";
    }

    /**
     * ҩ��ԭ��
     *
     * @return String
     */
    public static String getReason() {
        return
            "SELECT REASON_CODE , REASON_TYPE , REASON_CHN_DESC , REASON_ENG_DESC ,PY1 ,"
            + " PY2 ,SEQ , DESCRIPTION , OPT_USER , OPT_DATE ,"
            + " OPT_TERM FROM IND_REASON ORDER BY SEQ , REASON_CODE";
    }

    /**
     * ҩ��ԭ��
     *
     * @param reason_code
     *            ԭ�����
     * @return String
     */
    public static String getReason(String reason_code) {
        return "SELECT REASON_CODE FROM IND_REASON WHERE REASON_CODE = '"
            + reason_code + "'";
    }

    /**
     * ҩ��ԭ��
     *
     * @param reason_type
     *            ԭ������
     * @return String
     */
    public static String getReasonByType(String reason_type) {
        return
            "SELECT REASON_CODE, REASON_CHN_DESC FROM IND_REASON WHERE REASON_TYPE = '"
            + reason_type + "' ORDER BY SEQ";
    }

    /**
     * ��ѯҩ��������
     *
     * @return String
     */
    public static String getINDStockM() {
        return "SELECT ORG_CODE , ORDER_CODE , REGION_CODE , DISPENSE_FLG , "
            +
            " DISPENSE_ORG_CODE , QTY_TYPE , MM_USE_QTY , DD_USE_QTY , MATERIAL_LOC_CODE , "
            +
            " MAX_QTY , SAFE_QTY ,MIN_QTY , ECONOMICBUY_QTY , BUY_UNRECEIVE_QTY , "
            + " STANDING_QTY , ACTIVE_FLG ,OPT_USER , OPT_DATE , OPT_TERM "
            + " FROM IND_STOCKM WHERE ORDER BY ORG_CODE , ORDER_CODE";
    }

    /**
     * ��ѯҩ��������
     *
     * @param org_code
     *            ҩ�����
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStockM(String org_code, String order_code) {
        return "SELECT ORG_CODE , ORDER_CODE , REGION_CODE , DISPENSE_FLG , "
            +
            " DISPENSE_ORG_CODE , QTY_TYPE , MM_USE_QTY , DD_USE_QTY , MATERIAL_LOC_CODE , "
            +
            " MAX_QTY , SAFE_QTY ,MIN_QTY , ECONOMICBUY_QTY , BUY_UNRECEIVE_QTY , "
            + " STANDING_QTY , ACTIVE_FLG ,OPT_USER , OPT_DATE , OPT_TERM "
            + " FROM IND_STOCKM WHERE ORG_CODE = '" + org_code
            + "' AND ORDER_CODE = '" + order_code + "' ";
    }

    /**
     * ��ѯҩ������ϸ
     *
     * @param org_code
     *            ҩ�����
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStock(String org_code, String order_code, String region_code) {
        return
            "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
            +
            "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
            + "OPT_DATE , OPT_TERM "
            + "FROM IND_STOCK "
            + "WHERE ORG_CODE = '"
            + org_code
            + "' AND ORDER_CODE = '"
            + order_code + "' AND ACTIVE_FLG='Y' AND REGION_CODE = '" + region_code + "'  ORDER BY VALID_DATE";
    }

    /**
     *
     * @param org_code String
     * @param ordr_code String
     * @param pha_type String
     * @return String
     */
    public static String getINDStock(String org_code, String ordr_code,
                                     int batch_seq, String pha_type, String region_code) {
        return
            " SELECT A.ORDER_CODE, A.VERIFYIN_PRICE, C.OWN_PRICE, C.ORDER_DESC "
            + " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' "
            + " AND A.ORDER_CODE = '" + ordr_code + "' "
            + " AND A.BATCH_SEQ = " + batch_seq
            + " AND B.PHA_TYPE = '" + pha_type + "'  AND A.REGION_CODE = '" + region_code + "'  ";
    }

    /**
     * ��ѯҩ������ϸ
     * @param org_code String
     * @param order_code String
     * @param batch_no String
     * @param valid_date Timestamp
     * @return String
     */
    public static String getINDStock(String org_code, String order_code,
                                     String batch_no, String valid_date) {
        return "SELECT * FROM IND_STOCK WHERE ORG_CODE = '" + org_code +
            "' AND ORDER_CODE = '" + order_code + "' AND BATCH_NO = '" +
            batch_no + "' AND VALID_DATE = TO_DATE('" + valid_date +
            "','YYYY-MM-DD') AND ACTIVE_FLG = 'Y'  ";
    }


    /**
     * ��ѯҩ����ȫ����ϸ
     *
     * @param org_code
     *            ҩ�����
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStockAll(String org_code, String order_code, String region_code) {
        return
            "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
            +
            "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
            + "OPT_DATE , OPT_TERM "
            + "FROM IND_STOCK "
            + "WHERE ORG_CODE = '"
            + org_code
            + "' AND ORDER_CODE = '"
            + order_code + "'  AND REGION_CODE = '" + region_code + "' ORDER BY VALID_DATE";
    }


    /**
     * ��ѯҩ������ϸ
     *
     * @param org_code
     *            ҩ�����
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStock(String org_code, String order_code,
                                     int batch_seq,  String region_code) {
        return
            "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
            +
            "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
            + "OPT_DATE , OPT_TERM "
            + "FROM IND_STOCK "
            + "WHERE ORG_CODE = '"
            + org_code
            + "' AND ORDER_CODE = '"
            + order_code
            + "' AND BATCH_SEQ="
            + batch_seq
            + " AND ACTIVE_FLG='Y' AND REGION_CODE = '" + region_code + "' ";
    }
    /**
     * ��ѯҩ������ϸ
     *
     * @param org_code
     *            ҩ�����
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStock(String org_code, String order_code,
    		int batch_seq) {
    	return
    	"SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
    	+
    	"MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
    	+ "OPT_DATE , OPT_TERM "
    	+ "FROM IND_STOCK "
    	+ "WHERE ORG_CODE = '"
    	+ org_code
    	+ "' AND ORDER_CODE = '"
    	+ order_code
    	+ "' AND BATCH_SEQ="
    	+ batch_seq
    	+ " AND ACTIVE_FLG='Y'  ";
    }

    /**
     * ��ѯҩ������ϸ
     *
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStock(String order_code, String region_code) {
        String where = "";
        if (!"".equals(region_code)) {
            where = " AND REGION_CODE = '" + region_code + "' ";
        }
        return
            "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
            + "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , "
            + "OPT_USER ,OPT_DATE , OPT_TERM "
            + "FROM IND_STOCK "
            + "WHERE ORDER_CODE = '"
            + order_code
            + "' AND ACTIVE_FLG='Y' " + where;
    }

    /**
     * ��ѯҩ������ϸ
     *
     * @param org_code
     *            ҩ�����
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getINDStockOrderBySeq(String org_code,
                                               String order_code) {
        return
            "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
            +
            "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
            + "OPT_DATE , OPT_TERM "
            + "FROM IND_STOCK "
            + "WHERE ORG_CODE = '"
            + org_code
            + "' AND ORDER_CODE = '"
            + order_code + "' AND ACTIVE_FLG='Y'  ORDER BY BATCH_SEQ";
    }


    /**
     * ��ѯ�ɹ��ƻ���ϸ
     *
     * @param org_code
     *            ҩ�����
     * @param plan_no
     *            �ƻ�����
     * @return String
     */
    public static String getINDPurPlandD(String org_code, String plan_no) {
        return "SELECT ORG_CODE, PLAN_NO, SEQ, ORDER_CODE, PLAN_QTY,"
            + "PUR_QTY, ACTUAL_QTY, CHECK_QTY, PURCH_UNIT, LASTPUR_QTY,"
            + "LASTCON_QTY, STOCK_QTY, STOCK_PRICE, SAFE_QTY, MAX_QTY,"
            + "BUY_UNRECEIVE_QTY, SUP_CODE, START_DATE, END_DATE, OPT_USER,"
            + "OPT_DATE, OPT_TERM " + "FROM IND_PURPLAND "
            + "WHERE ORG_CODE = '" + org_code + "' AND PLAN_NO = '"
            + plan_no + "' ORDER BY SEQ";
    }

    /**
     * ��ѯ�˻���ϸ
     *
     * @param org_code
     * @param reg_no
     * @return
     */
    public static String getINDRegressgoodsD(String reg_no) {
        return
            " SELECT REGRESSGOODS_NO, SEQ_NO, VERIFYIN_NO, VERSEQ_NO, ORDER_CODE,"
            + "QTY, BILL_UNIT, UNIT_PRICE, AMT, RETAIL_PRICE,"
            + "ACTUAL_QTY, BATCH_NO, VALID_DATE, INVOICE_NO, INVOICE_DATE,"
            + "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE "//luhai modify 2012-1-11 ����batchSeq VERIFYIN_PRICE
            + "FROM IND_REGRESSGOODSD "
            + "WHERE REGRESSGOODS_NO = '"
            + reg_no + "' ORDER BY REGRESSGOODS_NO , SEQ_NO";
    }

    /**
     * �ڶ��������в�ѯ�ƻ�����
     *
     * @param plan_no
     *            �ƻ�����
     * @return String
     */
    public static String getPlanNoInPurorder(String plan_no) {
        if ("".equals(plan_no)) {
            return "";
        }
        return "SELECT PLAN_NO FROM IND_PURORDERM WHERE PLAN_NO = '" + plan_no
            + "'";
    }

    /**
     * ��������������в�ѯ�ƻ�����
     *
     * @param plan_no
     *            �ƻ�����
     * @return String
     */
    public static String getPlanNoInVerifyin(String plan_no) {
        if ("".equals(plan_no)) {
            return "";
        }
        return "SELECT PLAN_NO FROM IND_VERIFYINM WHERE PLAN_NO = '" + plan_no
            + "'";
    }

    /**
     * ��ѯ��������ҩƷ��Ϣ
     *
     * @param sup_code
     *            ��������
     * @return String
     */
    public static String getSupOrder(String sup_code, String order_code) {
        if ("".equals(sup_code)) {
            return "";
        }
        String sql = "SELECT A.ORDER_CODE , B.ORDER_DESC ,B.SPECIFICATION ,B.PURCH_UNIT , A.CONTRACT_PRICE "
            + "FROM IND_AGENT A,PHA_BASE B,SYS_FEE C "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.SUP_CODE='"
            + sup_code + "' ";
        //ҩƷ�Ĳ�ѯ�����ƴ�ͻ�ѧ���� luhai 2012-2-28 begin 
//        if (!"".equals(order_code)) {
//            sql += "AND (A.ORDER_CODE LIKE '%" + order_code + "%' "
//                + "OR B.ORDER_DESC LIKE '%" + order_code + "%' "
//                + "OR C.PY1 LIKE '%" + order_code + "%' )";
//        }
        if (!"".equals(order_code)) {
            sql += "AND (A.ORDER_CODE LIKE '%" + order_code + "%' "
                + "OR B.ORDER_DESC LIKE '%" + order_code + "%' "
                + "OR C.GOODS_PYCODE LIKE '%" + order_code + "%' "
                + "OR C.ALIAS_PYCODE LIKE '%" + order_code + "%' "
                + "OR C.PY1 LIKE '%" + order_code + "%' )";
        }
        //ҩƷ�Ĳ�ѯ�����ƴ�ͻ�ѧ���� luhai 2012-2-28 end 
        
        return sql;
    }

    /**
     * ��ѯ����������ϸ
     *
     * @return
     */
    public static String getPurOrderD() {
        return
            "SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, "
            +
            "BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, "
            + "OPT_USER, OPT_DATE, OPT_TERM FROM IND_PURORDERD ";
    }

    /**
     * ��ѯ����������ϸ
     *
     * @param purorder_no
     *            ��������
     * @return
     */
    public static String getPurOrderDByNo(String purorder_no) {
        if ("".equals(purorder_no)) {
            return "";
        }
        return
            "SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, "
            +
            "BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, "
            + "OPT_USER, OPT_DATE, OPT_TERM "
            + "FROM IND_PURORDERD "
            + "WHERE PURORDER_NO='"
            + purorder_no
            + "' "
            + "ORDER BY SEQ_NO";
    }

    /**
     * ��ѯ���������ϸ
     *
     * @param request_no
     * @return
     */
    public static String getRequestDByNo(String request_no) {
        if ("".equals(request_no)) {
            return "";
        }
        return "SELECT REQUEST_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, "
            + "QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, "
            + "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE "//luhai 2012-01-12 add batchSeq 
            + "FROM IND_REQUESTD WHERE REQUEST_NO='" + request_no + "' "
            + "ORDER BY SEQ_NO";
    }

    /**
     * ��ѯҩ�ⲿ�����ι���ע��
     *
     * @param org_code
     *            ҩ�ⲿ��
     * @return String
     */
    public static String getIndOrgBatchFlg(String org_code) {
        return "SELECT BATCH_FLG FROM IND_ORG WHERE ORG_CODE='" + org_code
            + "'";
    }

    /**
     * �ж��Ƿ��ҩƷ���ڽ��е���
     *
     * @param org_code
     *            ҩ�ⲿ��
     * @param order_code
     *            ҩƷ����
     * @return String
     */
    public static String getIndStockReadjustpFlg(String org_code,
                                                 String order_code) {
        return "SELECT READJUSTP_FLG FROM IND_STOCK WHERE ORG_CODE='"
            + org_code + "' AND ORDER_CODE='" + order_code + "'";
    }

    /**
     * ����ҩ���ż�ҩƷ�����ѯ���������0��ҩƷ��������š��������ۼ۲�����Ч�ڽ�������
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return String
     */
    public static String getIndStockBatchAndQty(String org_code,
                                                String order_code, String sort) {
        return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,"
            + " A.STOCK_QTY AS QTY,"
            + " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3, "
            + " B.PHA_TYPE, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE "
            + " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND B.ORDER_CODE = C.ORDER_CODE "
            + " AND A.ORG_CODE = '"
            + org_code
            + "' AND A.ORDER_CODE = '"
            + order_code
            + "' AND A.ACTIVE_FLG='Y' "
            + " AND SYSDATE < A.VALID_DATE "
            + " AND A.STOCK_QTY  > 0 "
            + " ORDER BY A.VALID_DATE " + sort + " , A.BATCH_SEQ";
    }

    /**
     * ����ҩ���ż�ҩƷ�����ѯ�������ۼ۲�����Ч�ڽ�������
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return String
     */
    public static String getIndStockQty(String org_code, String order_code,
                                        String sort) {
        return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,"
            + " A.STOCK_QTY  AS QTY,"
            + " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3, "
            + " B.PHA_TYPE, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE "
            + " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND B.ORDER_CODE = C.ORDER_CODE "
            + " AND A.ORG_CODE = '"
            + org_code
            + "' AND A.ORDER_CODE = '"
            + order_code
            + "' AND A.ACTIVE_FLG='Y' "
            + " ORDER BY A.VALID_DATE " + sort + " , A.BATCH_SEQ";
    }
    
    /**
     * ����ҩ���ż�ҩƷ�����ѯҩƷ�����VALI_DATE���п���batch_seq
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return String
     * @date 20120806
     * @author liyh 
     */
    public static String getIndStockQtyTwo(String org_code, String order_code,
                                        String sort) {
        return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,"
            + " A.STOCK_QTY  AS QTY,"
            + " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3, "
            + " B.PHA_TYPE, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE "
            + " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND B.ORDER_CODE = C.ORDER_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' "
            + " AND A.ORDER_CODE = '" + order_code + "' "
            + " AND A.ACTIVE_FLG='Y' AND A.STOCK_QTY > 0"
            + " ORDER BY A.VALID_DATE DESC , A.BATCH_SEQ DESC ";
    }


    /**
     * ȡ��IND_STOCK����������
     *
     * @param org_code
     * @param order_code
     * @return
     */
    public static String getIndStockMaxBatchSeq(String org_code,
                                                String order_code) {
        return
            "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ, MATERIAL_LOC_CODE "
            + "FROM IND_STOCK WHERE ORG_CODE = '" + org_code +
            "' AND ORDER_CODE = '" + order_code +
            "' GROUP BY MATERIAL_LOC_CODE ORDER BY BATCH_SEQ DESC ";
    }

    /**
     * ����ҩ����,ҩƷ����,����,��Ч�ڲ�ѯҩƷ���������
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public static String getIndStockBatchSeq(String org_code,
                                             String order_code, String batch_no,
                                             String valid_date) {
        return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
            " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
            + org_code + "' AND ORDER_CODE = '" + order_code
            + "' AND BATCH_NO = '" + batch_no
            + "' AND VALID_DATE = TO_DATE('" + valid_date
            + "','yyyy-MM-dd') "
            + "AND ACTIVE_FLG = 'Y' ";
    }
    /**
     * luhai modify 2012-1-30 ����ownPrice�������Ϣ
     * ����ҩ����,ҩƷ����,������Ų�ѯҩƷ���������
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public static String getIndStockBatchSeq(String org_code,String order_code,
    		String batch_seq) {
    	//luhai modify 2012-1-30 ����ownPrice�������Ϣ begin 
//    	return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
//    	" VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
//    	+ org_code + "' AND ORDER_CODE = '" + order_code
//    	+ "' AND BATCH_SEQ = " + batch_seq
//    	+ " AND ACTIVE_FLG = 'Y' ";
    	return "SELECT A.BATCH_SEQ, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
    	" A.VERIFYIN_PRICE,C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3 FROM IND_STOCK A , SYS_FEE C " + "WHERE A.ORG_CODE = '"
    	+ org_code + "' AND A.ORDER_CODE = '" + order_code
    	+ "' AND A.BATCH_SEQ = " + batch_seq
    	+ " AND A.ACTIVE_FLG = 'Y' "
    	+" AND A.ORDER_CODE = C.ORDER_CODE ";
    	//luhai modify 2012-1-30 ����ownPrice�������Ϣ begin 
    }
    /**
     * 
     * ����ҩ����,ҩƷ����,����,��Ч��,���ռ۸��ѯҩƷ���������
     *
     *luhai 2012-01-10 add ��ҩ����������
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public static String getIndStockBatchSeq(String org_code,
    		String order_code, String batch_no,
    		String valid_date,String verifyInPrice) {
    	return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
    	" VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
    	+ org_code + "' AND ORDER_CODE = '" + order_code
    	+ "' AND BATCH_NO = '" + batch_no
    	+ "' AND VALID_DATE = TO_DATE('" + valid_date
    	+ "','yyyy-MM-dd') "
    	+ " AND ACTIVE_FLG = 'Y' "
    	+" AND VERIFYIN_PRICE="+verifyInPrice ;
    }
    /**
     * 
     * luhai 2012-1-16 modify
     * �õ�IND_STOCK��Ϣ
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @return
     */
    public static String getIndStock(String org_code,
    		String order_code,String batch_seq) {
    	return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
    	" VERIFYIN_PRICE FROM IND_STOCK " + " WHERE ORG_CODE = '"
    	+ org_code + "' AND ORDER_CODE = '" + order_code
    	+ "' AND BATCH_SEQ="+batch_seq
    	+ " AND ACTIVE_FLG = 'Y' ";
    }

    /**
     * ��ѯ����δ��ɵĶ�������
     *
     * @return String
     */
    public static String getUnDonePurorderNo(String org_code, String sup_code) {
        String sql = "SELECT DISTINCT (A.PURORDER_NO) "
            + "FROM IND_PURORDERM A , IND_PURORDERD B "
            + "WHERE A.PURORDER_NO = B.PURORDER_NO "
            + "AND A.ORG_CODE = '" + org_code + "' "
            + "AND (B.UPDATE_FLG = '0' OR B.UPDATE_FLG = '1') ";
        if (!"".equals(sup_code))
            sql += "AND A.SUP_CODE = '" + sup_code + "' ";
        sql += "ORDER BY A.PURORDER_NO DESC ";
        return sql;
    }

    /**
     * �õ�������ϸ
     *
     * @param verifyin_no
     *            ���յ���
     * @return String
     */
    public static String getVerifyinDByNo(String verifyin_no) {
        return
            "SELECT VERIFYIN_NO, SEQ_NO, PURORDER_NO, PURSEQ_NO, ORDER_CODE, "
            +
            "VERIFYIN_QTY, GIFT_QTY, BILL_UNIT, VERIFYIN_PRICE, INVOICE_AMT, "
            +
            "INVOICE_NO, INVOICE_DATE, BATCH_NO, VALID_DATE, REASON_CHN_DESC, "
            +
            "QUALITY_DEDUCT_AMT, RETAIL_PRICE, ACTUAL_QTY, UPDATE_FLG, OPT_USER, "
            + "OPT_DATE, OPT_TERM, MAN_CODE,BATCH_SEQ,SPC_BOX_BARCODE "//��������ʱ������е�BATCH_SEQ luahi 2012-01-01 modify
            + "FROM IND_VERIFYIND WHERE VERIFYIN_NO='" + verifyin_no + "'";
    }

    /**
     * ���ݹ�Ӧ�̺Ϳ��Ҳ�ѯ��������յ���
     *
     * @param org_code
     * @param sup_code
     * @return
     */
    public static String getDoneVerifyinByOrgAndSup(String org_code,
        String sup_code) {
        return "SELECT DISTINCT (A.VERIFYIN_NO) "
            + "FROM IND_VERIFYINM A , IND_VERIFYIND B "
            + "WHERE A.VERIFYIN_NO = B.VERIFYIN_NO "
            + "AND (B.UPDATE_FLG = '1' OR B.UPDATE_FLG = '3') "
            //+ "AND B.ACTUAL_QTY < B.VERIFYIN_QTY "
            + "AND A.ORG_CODE='"
            + org_code + "' AND A.SUP_CODE='" + sup_code
            + "' ORDER BY A.VERIFYIN_NO DESC ";
    }

    /**
     * ���������ת����
     *
     * @param order_code
     * @return
     */
    public static String getTransunitByCode(String order_code) {
        return "SELECT STOCK_QTY, DOSAGE_QTY, PURCH_UNIT FROM PHA_TRANSUNIT"
            + " WHERE ORDER_CODE='" + order_code + "'";
    }

    /**
     * ����ҩƷ������PHA��Ϣ
     *
     * @param order_code
     * @return
     */
    public static String getPHAInfoByOrder(String order_code) {
        return "SELECT A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, A.RETAIL_PRICE, A.TRADE_PRICE, "
            +
            "A.STOCK_PRICE, B.PURCH_QTY, B.STOCK_QTY, B.DOSAGE_QTY, A.PHA_TYPE, B.MEDI_UNIT, B.MEDI_QTY "
            + "FROM PHA_BASE A, PHA_TRANSUNIT B "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = '"
            + order_code + "'";
    }

    /**
     * �������뵥��ȡ�ó���ⵥ��Ϣ
     *
     * @param request_no
     * @return
     */
    public static String getDispenseByReqNo(String request_no) {
        return
            "SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "
            +
            "TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, "
            +
            "WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, "
            + "OPT_USER, OPT_DATE, OPT_TERM "
            + "FROM IND_DISPENSEM "
            + "WHERE REQUEST_NO = '" + request_no + "' ";
    }

    /**
     * ͨ�����뵥ȡ�ó��ⵥ��ϸ
     * @param request_no String
     * @return String
     */
    public static String getDispenseDByReqNo(String request_no) {
        return
            "SELECT B.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, B.QTY, B.BATCH_NO, "
            +
            "B.VALID_DATE, B.UNIT_CODE, B.RETAIL_PRICE, B.STOCK_PRICE, B.ACTUAL_QTY, "
            + "B.RETAIL_PRICE * B.QTY AS SUM_RETAIL_PRICE, "
            + "B.STOCK_PRICE * B.QTY AS SUM_STOCK_PRICE, "
            + "B.RETAIL_PRICE * B.QTY - B.STOCK_PRICE * B.QTY AS DIFF_SUM ,E.UPDATE_FLG AS END_FLG, A.DISPENSE_NO "
            + "FROM IND_DISPENSEM A, IND_DISPENSED B, IND_REQUESTM C, PHA_BASE D , IND_REQUESTD E "
            +
            "WHERE A.DISPENSE_NO = B.DISPENSE_NO AND A.REQUEST_NO = C.REQUEST_NO "
            +
            "AND A.REQTYPE_CODE = C.REQTYPE_CODE AND B.ORDER_CODE = D.ORDER_CODE "
            + "AND A.REQUEST_NO = E.REQUEST_NO AND B.REQUEST_SEQ = E.SEQ_NO "
            + "AND C.REQUEST_NO = E.REQUEST_NO AND D.ORDER_CODE = E.ORDER_CODE AND C.REQUEST_NO = '" +
            request_no + "'";
    }


    /**
     * ���ݳ���ⵥ��ȡ�ó���ⵥ��Ϣ
     *
     * @param dispense_no
     * @return
     */
    public static String getDispenseByDisNo(String dispense_no) {
        return
            "SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "
            +
            "TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, "
            +
            "WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, "
            + "OPT_USER, OPT_DATE, OPT_TERM "
            + "FROM IND_DISPENSEM "
            + "WHERE DISPENSE_NO = '" + dispense_no + "' ";
    }

    /**
     * �������뵥��ȡ�����뵥��ϸ��Ϣ��ȡ��δ��ɵ�ϸ����Ϣ��
     *
     * @param request_no
     * @return
     */
    public static String getOutRequestDInfo(String request_no,
                                            String update_flg) {
        String sql = "";
        if (!"".equals(update_flg)) {
            sql = "AND UPDATE_FLG <> '"+update_flg+"' ";
        }
//        luhai modify 2012-2-12 ��stockPrice�ĳ�verifyinPrice begin
        return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
            + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
            + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
            + "B.STOCK_PRICE, B.RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
            + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
            + "B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ,A.VERIFYIN_PRICE "//luhai 2012-1-12 ����verifyin_price batch_seq 
            + "FROM IND_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.REQUEST_NO = '"
            + request_no + "' " + sql;
//        return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
//        + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
//        + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
//        + "A.VERIFYIN_PRICE AS STOCK_PRICE, B.RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
//        + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
//        + "B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ,A.VERIFYIN_PRICE "//luhai 2012-1-12 ����verifyin_price batch_seq 
//        + "FROM IND_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C "
//        + "WHERE A.ORDER_CODE = B.ORDER_CODE "
//        + "AND A.ORDER_CODE = C.ORDER_CODE "
//        + "AND A.REQUEST_NO = '"
//        + request_no + "' " + sql;
      //lluhai modify 2012-2-12 ��stockPrice�ĳ�verifyinPrice end
    }

    /**
     * �������뵥��ȡ�����뵥��ϸ��Ϣ
     *
     * luhai 2012-1-16 ������ind_stock ��batch_seq �Ĺ���
     * @param request_no
     * @return
     */
    public static String getOutRequestDInfo(String request_no) {
        return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
            + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
            + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
            + "E.VERIFYIN_PRICE AS STOCK_PRICE, "
            + "A.RETAIL_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS RETAIL_PRICE,"
            +" A.BATCH_NO, "
            + "A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, "
            + "C.DOSAGE_QTY, B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ "//luhai 2012-01-13add batch_seq
            + "FROM IND_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C, "
            + "IND_REQUESTM D, IND_STOCK E "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.REQUEST_NO = D.REQUEST_NO "
            + "AND D.APP_ORG_CODE = E.ORG_CODE "
            + "AND A.ORDER_CODE = E.ORDER_CODE "
            + "AND A.BATCH_NO = E.BATCH_NO "
            + "AND A.VALID_DATE = E.VALID_DATE "
            //luhai 2012-01-16  ����batch_seq �Ĺ��� ҩƷ����
            +" AND A.BATCH_SEQ=E.BATCH_SEQ  "
            + "AND A.REQUEST_NO = '"
            + request_no + "' ";
    }

    /**
     * ���ݳ��ⵥ��ȡ�ó��ⵥ��ϸ��Ϣ
     *
     * @param dispense_no
     * @return
     */
    public static String getIndDispenseDByNo(String dispense_no) {
//        String sql =
//            "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
//            + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
//            + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
//            + "A.STOCK_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS STOCK_PRICE, "
//            + "A.RETAIL_PRICE / C.STOCK_QTY / C.DOSAGE_QTY "
//            + "AS RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
//            + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
//            + "B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ "
//            + "FROM IND_DISPENSED A , PHA_BASE B , PHA_TRANSUNIT C "
//            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
//            + "AND A.ORDER_CODE = C.ORDER_CODE "
//            + "AND A.DISPENSE_NO = '"
//            + dispense_no + "'";
        String sql =
            "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
            + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
            + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
            + "A.VERIFYIN_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS STOCK_PRICE, "
            + "A.RETAIL_PRICE / C.STOCK_QTY / C.DOSAGE_QTY "
            + "AS RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
            + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
            + "B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ "
            + "FROM IND_DISPENSED A , PHA_BASE B , PHA_TRANSUNIT C "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.DISPENSE_NO = '"
            + dispense_no + "'";
        return sql;
    }

    /**
     * ���ݳ��ⵥ��ȡ����ⵥ��ϸ��Ϣ
     *
     * @param dispense_no
     * @return
     */
    public static String getInDispenseDInfo(String dispense_no) {
        return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
            + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
            + "B.SPECIFICATION, A.QTY, E.ACTUAL_QTY, A.ACTUAL_QTY AS OUT_QTY, "
            + "A.UNIT_CODE, A.STOCK_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS STOCK_PRICE ,"
            +" A.RETAIL_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS RETAIL_PRICE, A.BATCH_NO, "
            + "A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, "
            +
            "C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ, A.BATCH_SEQ "
            + "FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C, IND_DISPENSEM D, IND_REQUESTD E "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.DISPENSE_NO = D.DISPENSE_NO "
            + "AND A.REQUEST_SEQ = E.SEQ_NO "
            + "AND D.REQUEST_NO = E.REQUEST_NO AND A.DISPENSE_NO = '"
            + dispense_no + "'";
    }

    /**
     * �̵��ѯ(ȫ���̵�)
     *
     * @param org_code
     * @param sort
     * @return
     */
    public static String getQtyCheck(String org_code, String sort) {
        String sql = "";
        if ("1".equals(sort)) {
            // ��ҩƷ��������
            sql = "ORDER BY B.TYPE_CODE";
        }
        else if ("2".equals(sort)) {
            // ����ҩƷ��������
            sql = "ORDER BY B.DOSE_CODE";
        }
        else if ("3".equals(sort)) {
            // ����ҩƷ��λ����
            sql = "ORDER BY C.MATERIAL_LOC_CODE";
        }
        else if ("4".equals(sort)) {
            // ����ҩƷƴ������
            sql = "ORDER BY D.PY1";
        }
        return
            "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
            + "A.BATCH_SEQ, B.STOCK_UNIT , A.STOCK_QTY AS STOCK_QTY, "
            + "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
            + "FROM IND_STOCK A, PHA_BASE B , IND_MATERIALLOC C , SYS_FEE D "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORG_CODE = C.ORG_CODE(+) "
            + "AND A.MATERIAL_LOC_CODE = C.MATERIAL_LOC_CODE(+) "
            + "AND A.ORDER_CODE = D.ORDER_CODE "
            + "AND B.ORDER_CODE = D.ORDER_CODE "
            //+ "AND A.ACTIVE_FLG = 'Y' "
            + "AND A.ORG_CODE='"
            + org_code
            + "' " + sql;
    }

    /**
     * �����̵�ʱ���ѯ(ȫ���̵�)
     * @param org_code String
     * @param frozen_date String
     * @param sort String
     * @return String
     */
    public static String getQtyCheck(String org_code, String frozen_date,
                                     String sort) {
        String sql = "";
        if ("1".equals(sort)) {
            // ��ҩƷ��������
            sql = "ORDER BY B.TYPE_CODE";
        }
        else if ("2".equals(sort)) {
            // ����ҩƷ��������
            sql = "ORDER BY B.DOSE_CODE";
        }
        else if ("3".equals(sort)) {
            // ����ҩƷ��λ����
            sql = "ORDER BY C.MATERIAL_LOC_CODE";
        }
        else if ("4".equals(sort)) {
            // ����ҩƷƴ������
            sql = "ORDER BY D.PY1";
        }
        return
            "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
            +
            "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
            + "A.BATCH_SEQ, B.STOCK_UNIT, A.STOCK_QTY "
            + "FROM IND_QTYCHECK A, PHA_BASE B, IND_MATERIALLOC C, SYS_FEE D "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORG_CODE = C.ORG_CODE(+) "
            + "AND A.ORDER_CODE = D.ORDER_CODE "
            + "AND B.ORDER_CODE = D.ORDER_CODE "
            + "AND A.ORG_CODE = '" + org_code
            + "' AND A.FROZEN_DATE = '" + frozen_date + "' " + sql;
    }

    /**
     * �̵��ѯ(�����̵�--��������̵�)
     *
     * @param order_code
     * @param sort
     * @return
     */
    public static String getQtyCheckTypeB(String org_code, String order_code, String sort) {
        String sql = "";
        if ("1".equals(sort)) {
            // ��ҩƷ��������
            sql = "ORDER BY B.TYPE_CODE";
        }
        else if ("2".equals(sort)) {
            // ����ҩƷ��������
            sql = "ORDER BY B.DOSE_CODE";
        }
        else if ("3".equals(sort)) {
            // ����ҩƷ��λ����
            sql = "ORDER BY C.MATERIAL_LOC_CODE";
        }
        else if ("4".equals(sort)) {
            // ����ҩƷƴ������
            sql = "ORDER BY D.PY1";
        }
        return
            "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
            + "A.BATCH_SEQ, B.STOCK_UNIT , "
            +
            "(A.LAST_TOTSTOCK_QTY + A.IN_QTY - A.OUT_QTY + A.CHECKMODI_QTY) AS STOCK_QTY, "
            + "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
            + "FROM IND_STOCK A, PHA_BASE B , IND_MATERIALLOC C , SYS_FEE D "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORG_CODE = C.ORG_CODE(+) "
            + "AND A.MATERIAL_LOC_CODE = C.MATERIAL_LOC_CODE(+) "
            + "AND A.ORDER_CODE = D.ORDER_CODE "
            + "AND B.ORDER_CODE = D.ORDER_CODE "
            //+ "AND A.ACTIVE_FLG = 'Y' "
            + "AND A.ORG_CODE = '" + org_code + "' "
            + "AND A.ORDER_CODE LIKE '"
            + order_code + "%' " + sql;
    }

    /**
     *
     * @param order_code
     * @param vaild_date
     * @param sort
     * @return
     */
    public static String getQtyCheckTypeC(String org_code, String order_code, String valid_date,
                                          String sort) {
        String sql = "";
        if (!"".equals(valid_date)) {
            sql = "AND A.VALID_DATE = TO_DATE(" + valid_date
                + ",'yyyy/MM/dd') ";
        }
        if ("1".equals(sort)) {
            // ��ҩƷ��������
            sql = "ORDER BY B.TYPE_CODE";
        }
        else if ("2".equals(sort)) {
            // ����ҩƷ��������
            sql = "ORDER BY B.DOSE_CODE";
        }
        else if ("3".equals(sort)) {
            // ����ҩƷ��λ����
            sql = "ORDER BY C.MATERIAL_LOC_CODE";
        }
        else if ("4".equals(sort)) {
            // ����ҩƷƴ������
            sql = "ORDER BY D.PY1";
        }
        return
            "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
            + "A.BATCH_SEQ, B.STOCK_UNIT , "
            +
            "(A.LAST_TOTSTOCK_QTY + A.IN_QTY - A.OUT_QTY + A.CHECKMODI_QTY) AS STOCK_QTY, "
            + "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
            + "FROM IND_STOCK A, PHA_BASE B , IND_MATERIALLOC C , SYS_FEE D "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORG_CODE = C.ORG_CODE(+) "
            + "AND A.MATERIAL_LOC_CODE = C.MATERIAL_LOC_CODE(+) "
            + "AND A.ORDER_CODE = D.ORDER_CODE "
            + "AND B.ORDER_CODE = D.ORDER_CODE "
            //+ "AND A.ACTIVE_FLG = 'Y' "
            + "AND A.ORG_CODE = '" + org_code + "' "
            + "AND A.ORDER_CODE = '"
            + order_code + "' " + sql;
    }

    /**
     * �̵��ѯ(�����̵�--��������̵�)
     *
     * @param mac_code
     * @param sort
     * @return
     */
    public static String getQtyCheckTypeD(String org_code, String mac_code, String sort) {
        String sql = "";
        if ("1".equals(sort)) {
            // ��ҩƷ��������
            sql = "ORDER BY B.TYPE_CODE";
        }
        else if ("2".equals(sort)) {
            // ����ҩƷ��������
            sql = "ORDER BY B.DOSE_CODE";
        }
        else if ("3".equals(sort)) {
            // ����ҩƷ��λ����
            sql = "ORDER BY C.MATERIAL_LOC_CODE";
        }
        else if ("4".equals(sort)) {
            // ����ҩƷƴ������
            sql = "ORDER BY D.PY1";
        }
        return
            "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
            + "A.BATCH_SEQ, B.STOCK_UNIT , "
            +
            "(A.LAST_TOTSTOCK_QTY + A.IN_QTY - A.OUT_QTY + A.CHECKMODI_QTY) AS STOCK_QTY, "
            + "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
            + "FROM IND_STOCK A, PHA_BASE B , IND_MATERIALLOC C , SYS_FEE D "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORG_CODE = C.ORG_CODE(+) "
            + "AND A.MATERIAL_LOC_CODE = C.MATERIAL_LOC_CODE(+) "
            + "AND A.ORDER_CODE = D.ORDER_CODE "
            + "AND B.ORDER_CODE = D.ORDER_CODE "
            //+ "AND A.ACTIVE_FLG = 'Y' "
            + "AND A.ORG_CODE = '" + org_code + "' "
            + "AND C.MATERIAL_LOC_CODE = '"
            + mac_code + "' " + sql;
    }

    /**
     * ����ҩ�ⲿ�źͶ���ʱ���ѯ�̵���ʷ��Ϣ
     * @param org_code String
     * @param frozen_date String
     * @return String
     */
    public static String getQtyCheckHistoryInfo(String org_code,
                                                String frozen_date) {
        return
            "SELECT CASE WHEN Q.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
            + "END AS STOCK_FLG, Q.ORDER_CODE, S.ORDER_DESC, Q.VALID_DATE, "
            + "Q.BATCH_NO, Q.BATCH_SEQ, M.MATERIAL_LOC_CODE "
            + "FROM IND_QTYCHECK Q, SYS_FEE S, IND_STOCK K, IND_MATERIALLOC M "
            + "WHERE Q.ORDER_CODE = S.ORDER_CODE "
            + "AND Q.ORDER_CODE = K.ORDER_CODE "
            + "AND Q.ORG_CODE = K.ORG_CODE "
            + "AND Q.BATCH_SEQ = K.BATCH_SEQ "
            + "AND S.ORDER_CODE = K.ORDER_CODE "
            + "AND K.ORG_CODE = M.ORG_CODE(+) "
            + "AND K.MATERIAL_LOC_CODE = M.MATERIAL_LOC_CODE(+) "
            + "AND Q.ORG_CODE = '" + org_code + "' "
            + "AND Q.FROZEN_DATE = '" + frozen_date + "' "
            + "ORDER BY ORDER_CODE";
    }

    /**
     * ȡ�òɽ���
     *
     * @param org_code
     * @param sup_code
     * @param plan_no
     * @return
     */
    public static String getIndPlanAdvice(String org_code, String sup_code,
                                          String plan_no) {
        return
            "SELECT 'N' AS SELECT_FLG, A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION, "
            + "A.CHECK_QTY, A.PURCH_UNIT, A.STOCK_PRICE, A.CHECK_QTY * A.STOCK_PRICE AS ATM, A.SUP_CODE,"
            + "A.PLAN_NO FROM IND_PURPLAND A, PHA_BASE B "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = '"
            + org_code
            + "' AND A.SUP_CODE = '"
            + sup_code
            + "' AND A.PLAN_NO = '" + plan_no + "'";
    }

    /**
     * �������뵥״̬Ϊ���
     * @param request_no String
     * @return String
     */
    public static String onUpdateRequestFlg(String request_no) {
        return "UPDATE IND_REQUESTD SET UPDATE_FLG = '3' WHERE REQUEST_NO = '" +
            request_no + "' AND ACTUAL_QTY = QTY";
    }

    /**
     * ȫ���̵�
     * @param org_code String
     * @param frozen_date String
     * @param active_flg String
     * @param valid_date String
     * @param sort String
     * @return String
     */
    public static String getQtyCheckDataByType0(String org_code,
                                                String frozen_date,
                                                String active_flg,
                                                String valid_flg, String sort) {
    	//luhai modify 2012-04-25 begin  ҩƷ���Ƽ����� begin 
//        //System.out.println("---");
//        String sql = "";
//        String group = "";
//        if ("N".equals(valid_flg)) {
//            sql =
//                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//                +
//                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
//                +
//                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
//                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
//                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
//                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//                +
//                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
//                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//                +
//                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//                + "AND A.ORG_CODE = '" + org_code
//                + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
//        }
//        else {
//            sql =
//                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//                +
//                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
//                +
//                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
//                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
//                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
//                + "|| F.UNIT_CHN_DESC "
//                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
//                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
//                +
//                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//                +
//                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
//                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//                +
//                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//                + "AND A.ORG_CODE = '" + org_code
//                + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
//            group =
//                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
//                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
//        }
//        String where = "";
//        if ("Y".equals(active_flg)) {
//            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
//                "','YYYYMMDDHH24MISS')  ";
//        }
//        String order = "";
//        if ("1".equals(sort)) {
//            order = " ORDER BY B.TYPE_CODE ";
//            group += " , B.TYPE_CODE ";
//        }
//        else if ("2".equals(sort)) {
//            order = " ORDER BY B.DOSE_CODE ";
//            group += " , B.DOSE_CODE ";
//        }
//        else if ("3".equals(sort)) {
//            order = " ORDER BY C.MATERIAL_LOC_CODE ";
//        }
//        else if ("4".equals(sort)) {
//            order = " ORDER BY D.PY1 ";
//            group += " , D.PY1 ";
//        }
//        //System.out.println("---"+sql + where + group + order);
//        return sql + where + group + order;
        //System.out.println("---");
        String sql = "";
        String group = "";
        if ("N".equals(valid_flg)) {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
                +
                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
        }
        else {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
                +
                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
                + "|| F.UNIT_CHN_DESC "
                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
                +
                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
            group =
                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
        }
        String where = "";
        if ("Y".equals(active_flg)) {
            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
                "','YYYYMMDDHH24MISS')  ";
        }
        String order = "";
        if ("1".equals(sort)) {
            order = " ORDER BY B.TYPE_CODE ";
            group += " , B.TYPE_CODE ";
        }
        else if ("2".equals(sort)) {
            order = " ORDER BY B.DOSE_CODE ";
            group += " , B.DOSE_CODE ";
        }
        else if ("3".equals(sort)) {
            order = " ORDER BY C.MATERIAL_LOC_CODE ";
        }
        else if ("4".equals(sort)) {
            order = " ORDER BY D.PY1 ";
            group += " , D.PY1 ";
        }
        //System.out.println("---"+sql + where + group + order);
        return sql + where + group + order;
        //luhai modify 2012-04-25 begin  ҩƷ���Ƽ����� end 
    }

    /**
     * �̵��ѯ(�����̵�--��������̵�)
     * @param org_code String
     * @param frozen_date String
     * @param active_flg String
     * @param valid_date String
     * @param sort String
     * @param order_code String
     * @return String
     */
    public static String getQtyCheckDataByTypeB(String org_code,
                                                String frozen_date,
                                                String active_flg,
                                                String valid_flg,
                                                String sort,
                                                String order_code) {
    	//luhai modify 2012-05-25 ҩƷ���ƺ������ begin
        String sql = "";
        String group = "";
        if ("N".equals(valid_flg)) {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
                +
                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date +
                "' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
        }
        else {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
                +
                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
                + "|| F.UNIT_CHN_DESC "
                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
                +
                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date +
                "' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
            group =
                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
        }
        String where = "";
        if ("Y".equals(active_flg)) {
            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
                "','YYYYMMDDHH24MISS') ";
        }
        String order = "";
        if ("1".equals(sort)) {
            order = " ORDER BY B.TYPE_CODE ";
            group += " , B.TYPE_CODE ";
        }
        else if ("2".equals(sort)) {
            order = " ORDER BY B.DOSE_CODE ";
            group += " , B.DOSE_CODE ";
        }
        else if ("3".equals(sort)) {
            order = " ORDER BY C.MATERIAL_LOC_CODE ";
        }
        else if ("4".equals(sort)) {
            order = " ORDER BY D.PY1 ";
            group += " , D.PY1 ";
        }
        return sql + where + group + order;
//        String sql = "";
//        String group = "";
//        if ("N".equals(valid_flg)) {
//        	sql =
//        		"SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//        		+
//        		"END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
//        		+
//        		"C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
//        		+ "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
//        		+ "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
//        		+ "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//        		+
//        		"A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
//        		+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//        		+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//        		+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//        		+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//        		+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//        		+
//        		"AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//        		+ "AND A.ORG_CODE = '" + org_code
//        		+ "' AND A.FROZEN_DATE = '" + frozen_date +
//        		"' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
//        }
//        else {
//        	sql =
//        		"SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//        		+
//        		"END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
//        		+
//        		"'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
//        		+ "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
//        		+ "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
//        		+ "|| F.UNIT_CHN_DESC "
//        		+ "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
//        		+ "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
//        		+
//        		"FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//        		+
//        		"A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
//        		+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//        		+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//        		+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//        		+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//        		+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//        		+
//        		"AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//        		+ "AND A.ORG_CODE = '" + org_code
//        		+ "' AND A.FROZEN_DATE = '" + frozen_date +
//        		"' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
//        	group =
//        		" GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
//        		+ "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
//        }
//        String where = "";
//        if ("Y".equals(active_flg)) {
//        	where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
//        	"','YYYYMMDDHH24MISS') ";
//        }
//        String order = "";
//        if ("1".equals(sort)) {
//        	order = " ORDER BY B.TYPE_CODE ";
//        	group += " , B.TYPE_CODE ";
//        }
//        else if ("2".equals(sort)) {
//        	order = " ORDER BY B.DOSE_CODE ";
//        	group += " , B.DOSE_CODE ";
//        }
//        else if ("3".equals(sort)) {
//        	order = " ORDER BY C.MATERIAL_LOC_CODE ";
//        }
//        else if ("4".equals(sort)) {
//        	order = " ORDER BY D.PY1 ";
//        	group += " , D.PY1 ";
//        }
//        return sql + where + group + order;
      //luhai modify 2012-05-25 ҩƷ���ƺ������ end
    }

    /**
     * �̵��ѯ(�����̵�--��������̵�)
     * @param org_code String
     * @param frozen_date String
     * @param active_flg String
     * @param valid_flg String
     * @param sort String
     * @param order_code String
     * @param valid_date String
     * @return String
     */
    public static String getQtyCheckDataByTypeC(String org_code,
                                                String frozen_date,
                                                String active_flg,
                                                String valid_flg,
                                                String sort,
                                                String order_code,
                                                String valid_date) {
    	//luhai modify ������ 2012-04-24 begin 
//        String sql = "";
//        String group = "";
//        if ("N".equals(valid_flg)) {
//            sql =
//                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//                +
//                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
//                +
//                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
//                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
//                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
//                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//                +
//                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
//                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//                +
//                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//                + "AND A.ORG_CODE = '" + org_code
//                + "' AND A.FROZEN_DATE = '" + frozen_date +
//                "' AND A.ORDER_CODE = '" + order_code + "' ";
//        }
//        else {
//            sql =
//                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//                +
//                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
//                +
//                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
//                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
//                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
//                + "|| F.UNIT_CHN_DESC "
//                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
//                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
//                +
//                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//                +
//                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
//                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//                +
//                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//                + "AND A.ORG_CODE = '" + org_code
//                + "' AND A.FROZEN_DATE = '" + frozen_date +
//                "' AND A.ORDER_CODE = '" + order_code + "' ";
//            group =
//                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
//                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
//
//        }
//        String where = "";
//        if ("Y".equals(active_flg)) {
//            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
//                "','YYYYMMDDHH24MISS') ";
//        }
//        if (!"".equals(valid_date)) {
//            where += " AND A.VALID_DATE=TO_DATE('" + valid_date +
//                "','YYYYMMDDHH24MISS') ";
//        }
//        String order = "";
//        if ("1".equals(sort)) {
//            order = " ORDER BY B.TYPE_CODE ";
//            group += " , B.TYPE_CODE ";
//        }
//        else if ("2".equals(sort)) {
//            order = " ORDER BY B.DOSE_CODE ";
//            group += " , B.DOSE_CODE ";
//        }
//        else if ("3".equals(sort)) {
//            order = " ORDER BY C.MATERIAL_LOC_CODE ";
//        }
//        else if ("4".equals(sort)) {
//            order = " ORDER BY D.PY1 ";
//            group += " , D.PY1 ";
//        }
//        return sql + where + group + order;
        String sql = "";
        String group = "";
        if ("N".equals(valid_flg)) {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
                +
                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date +
                "' AND A.ORDER_CODE = '" + order_code + "' ";
        }
        else {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE,B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
                +
                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
                + "|| F.UNIT_CHN_DESC "
                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
                +
                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date +
                "' AND A.ORDER_CODE = '" + order_code + "' ";
            group =
                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";

        }
        String where = "";
        if ("Y".equals(active_flg)) {
            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
                "','YYYYMMDDHH24MISS') ";
        }
        if (!"".equals(valid_date)) {
            where += " AND A.VALID_DATE=TO_DATE('" + valid_date +
                "','YYYYMMDDHH24MISS') ";
        }
        String order = "";
        if ("1".equals(sort)) {
            order = " ORDER BY B.TYPE_CODE ";
            group += " , B.TYPE_CODE ";
        }
        else if ("2".equals(sort)) {
            order = " ORDER BY B.DOSE_CODE ";
            group += " , B.DOSE_CODE ";
        }
        else if ("3".equals(sort)) {
            order = " ORDER BY C.MATERIAL_LOC_CODE ";
        }
        else if ("4".equals(sort)) {
            order = " ORDER BY D.PY1 ";
            group += " , D.PY1 ";
        }
        return sql + where + group + order;
        //luhai modify ������ 2012-04-24 begin 
    }

    /**
     * �̵��ѯ(�����̵�--��λ�����̵�)
     * @param org_code String
     * @param frozen_date String
     * @param active_flg String
     * @param valid_flg String
     * @param sort String
     * @param mat_code String
     * @return String
     */
    public static String getQtyCheckDataByTypeD(String org_code,
                                                String frozen_date,
                                                String active_flg,
                                                String valid_flg,
                                                String sort,
                                                String mat_code) {
    	//luhai modify 2012-04-25 ������ begin 
//        String sql = "";
//        String group = "";
//        if ("N".equals(valid_flg)) {
//            sql =
//                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//                +
//                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
//                +
//                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
//                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
//                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
//                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//                +
//                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
//                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//                +
//                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//                + "AND A.ORG_CODE = '" + org_code
//                + "' AND A.FROZEN_DATE = '" + frozen_date +
//                "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
//        }
//        else {
//            sql =
//                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
//                +
//                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
//                +
//                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
//                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
//                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
//                + "|| F.UNIT_CHN_DESC "
//                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
//                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
//                +
//                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
//                +
//                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
//                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
//                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
//                +
//                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//                + "AND A.ORG_CODE = '" + org_code
//                + "' AND A.FROZEN_DATE = '" + frozen_date +
//                "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
//            group =
//                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
//                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
//
//        }
//        String where = "";
//        if ("Y".equals(active_flg)) {
//            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
//                "','YYYYMMDDHH24MISS') ";
//        }
//        String order = "";
//        if ("1".equals(sort)) {
//            order = " ORDER BY B.TYPE_CODE ";
//            group += " , B.TYPE_CODE ";
//        }
//        else if ("2".equals(sort)) {
//            order = " ORDER BY B.DOSE_CODE ";
//            group += " , B.DOSE_CODE ";
//        }
//        else if ("3".equals(sort)) {
//            order = " ORDER BY C.MATERIAL_LOC_CODE ";
//        }
//        else if ("4".equals(sort)) {
//            order = " ORDER BY D.PY1 ";
//            group += " , D.PY1 ";
//        }
//        return sql + where + group + order;
        String sql = "";
        String group = "";
        if ("N".equals(valid_flg)) {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
                +
                "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
                + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
                + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
                + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date +
                "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
        }
        else {
            sql =
                "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
                +
                "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
                +
                "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
                + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
                + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
                + "|| F.UNIT_CHN_DESC "
                + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
                + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
                +
                "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
                +
                "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
                + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
                + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
                + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
                +
                "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
                + "AND A.ORG_CODE = '" + org_code
                + "' AND A.FROZEN_DATE = '" + frozen_date +
                "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
            group =
                " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
                + "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";

        }
        String where = "";
        if ("Y".equals(active_flg)) {
            where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
                "','YYYYMMDDHH24MISS') ";
        }
        String order = "";
        if ("1".equals(sort)) {
            order = " ORDER BY B.TYPE_CODE ";
            group += " , B.TYPE_CODE ";
        }
        else if ("2".equals(sort)) {
            order = " ORDER BY B.DOSE_CODE ";
            group += " , B.DOSE_CODE ";
        }
        else if ("3".equals(sort)) {
            order = " ORDER BY C.MATERIAL_LOC_CODE ";
        }
        else if ("4".equals(sort)) {
            order = " ORDER BY D.PY1 ";
            group += " , D.PY1 ";
        }
        return sql + where + group + order;
        //luhai modify 2012-04-25 ������ end
    }

    /**
     * ���෽ʽ
     * @param org_code String
     * @param frozen_date String
     * @return String
     */
    public static String getQtyCheckDataByTypeOther(String org_code,
        String frozen_date) {
        return
            "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
            +
            "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
            +
            "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
            + "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
            + "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR (A.STOCK_QTY / E.DOSAGE_QTY + A.MODI_QTY / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
            + "A.STOCK_UNIT AS STOCK_UNIT_A, MOD (A.STOCK_QTY + A.MODI_QTY, E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
            +
            "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
            + "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
            + "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
            + "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
            + "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
            + "AND A.ORG_CODE = '" + org_code
            + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
    }

    /**
     * ȡ�̵���ҵ��ָ��ʱ�䣬ָ�����ţ�ָ��ҩƷ������������
     * @param org_code String
     * @param order_code String
     * @param frozen_date String
     * @return String
     */
    public static String getMaxSeqByQtyCheck(String org_code, String order_code,
                                             String frozen_date) {
        return
            "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_QTYCHECK WHERE ORG_CODE='"
            + org_code + "' AND ORDER_CODE='" + order_code +
            "' AND FROZEN_DATE='" + frozen_date + "'";
    }

    /**
     * ȡ�̵���ҵ��ָ��ʱ�䣬ָ�����ţ�ָ��ҩƷ��������ź͵�����
     * @param org_code String
     * @param order_code String
     * @param frozen_date String
     * @return String
     */
    public static String getQtyCheckBatchSeqAndModi(String org_code,
        String order_code, String frozen_date) {
        return
            "SELECT A.ORDER_CODE, A.BATCH_SEQ, A.MODI_QTY , A.MODI_QTY * B.RETAIL_PRICE AS MODI_ATM "
            + "FROM IND_QTYCHECK A, PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE='"
            + org_code + "' AND A.ORDER_CODE='" + order_code +
            "' AND A.FROZEN_DATE='" + frozen_date + "'";
    }

    /**
     * ȡ�̵���ҵ��ָ��ʱ�䣬ָ�����ţ�ָ��ҩƷ��ָ��������ŵĵ�����,�������
     * @param org_code String
     * @param order_code String
     * @param frozen_date String
     * @return String
     */
    public static String getQtyCheckBatchSeqAndModi(String org_code,
        String order_code, String frozen_date, int batch_seq) {
        return
            "SELECT A.ORDER_CODE, A.MODI_QTY , A.MODI_QTY * B.RETAIL_PRICE AS MODI_ATM "
            + "FROM IND_QTYCHECK A, PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE='"
            + org_code + "' AND A.ORDER_CODE='" + order_code +
            "' AND A.FROZEN_DATE='" + frozen_date + "' AND A.BATCH_SEQ=" +
            batch_seq;
    }

    /**
     * ����ҩƷ����ȡ��ҩƷ��Ϣ�����ڴ�ӡ
     * @return String
     */
    public static String getOrderInfoByCode(String order_code, String sup_code,
                                            String type) {
        if ("PUR".equals(type) || "REG".equals(type)) {
        	//luhai modify 2012-04-26 begin 
//        	// ��ӡ���������˻���
//        	return "SELECT A.ORDER_CODE, "
//        	+ "CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
//        	+
//        	"D.SPECIFICATION, C.UNIT_CHN_DESC, A.PHA_TYPE , E.MAN_CHN_DESC , D.GOODS_DESC "
//        	+
//        	"FROM PHA_BASE A, IND_AGENT B, SYS_UNIT C ,SYS_FEE D, SYS_MANUFACTURER E "
//        	+
//        	"WHERE A.ORDER_CODE = B.ORDER_CODE AND A.PURCH_UNIT = C.UNIT_CODE "
//        	+
//        	"AND A.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) "
//        	+ "AND A.ORDER_CODE ='" + order_code
//        	+ "' AND B.SUP_CODE='" + sup_code + "'";
            // ��ӡ���������˻���
            return "SELECT A.ORDER_CODE, "
                + " D.ORDER_DESC  AS ORDER_DESC, "
                +
                //===zhangp 20120706 start
//                "D.SPECIFICATION, C.UNIT_CHN_DESC, A.PHA_TYPE , E.MAN_CHN_DESC , D.GOODS_DESC "
                "D.SPECIFICATION, C.UNIT_CHN_DESC, A.PHA_TYPE , D.MAN_CODE MAN_CHN_DESC, D.GOODS_DESC "
                +
//                "FROM PHA_BASE A, IND_AGENT B, SYS_UNIT C ,SYS_FEE D, SYS_MANUFACTURER E "
                "FROM PHA_BASE A, IND_AGENT B, SYS_UNIT C ,SYS_FEE D "
                //===zhangp 20120706 end
                +
                "WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.PURCH_UNIT = C.UNIT_CODE "
                +
                //===zhangp 20120706 start
//                "AND A.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) "
                "AND A.ORDER_CODE = D.ORDER_CODE "
                //===zhangp 20120706 end
                + "AND A.ORDER_CODE ='" + order_code +"' " ;//ȥ�� sup_code��ѯ���� ind_agent������ by liyh20121026
          //luhai modify 2012-04-26 end
        }
        else if ("VER".equals(type)) {
            // ��ӡ������ⵥ
        	//luhai modify 2012-04-27 begin  ������ⵥɾ����Ʒ��
//            return
//                "SELECT A.ORDER_CODE, "
//                + "CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
//                + "D.SPECIFICATION, B.UNIT_CHN_DESC, "
//                +
//                "D.OWN_PRICE * C.STOCK_QTY AS OWN_PRICE, A.PHA_TYPE, E.MAN_CHN_DESC, A.BID_FLG , D.GOODS_DESC "
//                +
//                "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, SYS_MANUFACTURER E ,IND_AGENT F "
//                +
//                "WHERE A.PURCH_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = D.ORDER_CODE "
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) AND A.ORDER_CODE = F.ORDER_CODE "
//                + "AND C.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE AND A.ORDER_CODE ='"
//                + order_code + "' AND F.SUP_CODE='" + sup_code + "'";
            return
                "SELECT A.ORDER_CODE, "
                + " D.ORDER_DESC  AS ORDER_DESC, " 
                + "D.SPECIFICATION, B.UNIT_CHN_DESC, "
                +
                //===zhangp 20120706 start
//                "D.OWN_PRICE * C.STOCK_QTY AS OWN_PRICE, A.PHA_TYPE, E.MAN_CHN_DESC, A.BID_FLG , D.GOODS_DESC "
                "D.OWN_PRICE * C.STOCK_QTY AS OWN_PRICE, A.PHA_TYPE, D.MAN_CODE MAN_CHN_DESC, A.BID_FLG , D.GOODS_DESC "
                +
//                "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, SYS_MANUFACTURER E ,IND_AGENT F "
                "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, IND_AGENT F "
                //===zhangp 20120706 end
                +
                "WHERE A.PURCH_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = D.ORDER_CODE "
                //===zhangp 20120706 start
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) AND A.ORDER_CODE = F.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = F.ORDER_CODE "
                //===zhangp 20120706 end
                + "AND C.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE AND A.ORDER_CODE ='"
                + order_code + "' AND F.SUP_CODE='" + sup_code + "'";
          //luhai modify 2012-04-27 end
        }
        else if ("PLAN".equals(type)) {
            // ��ӡ�ɹ��ƻ���
            return
                "SELECT A.ORDER_CODE, "
                + "CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
                + "D.SPECIFICATION, B.UNIT_CHN_DESC, "
                //===zhangp 20120706 start
//                + "F.CONTRACT_PRICE,D.OWN_PRICE,G.SUP_CHN_DESC,E.MAN_CHN_DESC,"
                + "F.CONTRACT_PRICE,D.OWN_PRICE,G.SUP_CHN_DESC,D.MAN_CODE MAN_CHN_DESC,"
                //===zhangp 20120706 end
                + "C.DOSAGE_QTY , D.GOODS_DESC "
                //===zhangp 20120706 start
//                + "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, SYS_MANUFACTURER E, IND_AGENT F, SYS_SUPPLIER G "
                + "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, IND_AGENT F, SYS_SUPPLIER G "
                //===zhangp 20120706 end
                + "WHERE A.PURCH_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = D.ORDER_CODE "
                //===zhangp 20120706 start
//                + "AND C.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) AND A.ORDER_CODE = F.ORDER_CODE "
                + "AND C.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = F.ORDER_CODE "
                //===zhangp 20120706 end
                + "AND C.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE AND A.SUP_CODE = G.SUP_CODE "
                + "AND A.ORDER_CODE ='" + order_code + "' AND F.SUP_CODE='" +
                sup_code + "'";
        }
        return "";
    }

    /**
     * ����ҩƷ����ȡ��ҩƷ��Ϣ�����ڴ�ӡ
     * @return String
     */
    public static String getOrderInfoByCode(String order_code, String unit_type) {
        // ��ӡ���뵥,���ⵥ
        String unit = "";
        if ("0".equals(unit_type)) {
            unit = "A.STOCK_UNIT";
        }
        else {
            unit = "A.DOSAGE_UNIT";
        }
        return "SELECT A.ORDER_CODE , A.ORDER_DESC , A.SPECIFICATION , B.UNIT_CHN_DESC , A.GOODS_DESC FROM PHA_BASE A,SYS_UNIT B "
            + "WHERE " + unit + " = B.UNIT_CODE AND A.ORDER_CODE ='" +
            order_code + "'";
    }

    /**
     * ҩƷ�������
     * @param plan_no String
     * @return String
     */
    public static String getOrderInfoByPlan(String plan_no) {
        return " SELECT C.SUP_CHN_DESC, "
            + " CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE "
            + " D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
            + " D.SPECIFICATION, E.UNIT_CHN_DESC,"
            + " A.CHECK_QTY AS PLAN_QTY, A.STOCK_PRICE AS PLAN_PRICE,"
            + " NVL (H.VERIFYIN_QTY, 0) AS IN_QTY,"
            + " NVL (H.VERIFYIN_PRICE, 0) AS IN_PRICE,"
            + " A.CHECK_QTY * A.STOCK_PRICE AS PLAN_AMT,"
            + " NVL (H.VERIFYIN_QTY * H.VERIFYIN_PRICE, 0) AS IN_AMT,"
            + " NVL (H.VERIFYIN_QTY - A.CHECK_QTY, 0) AS DIFF_QTY,"
            + " NVL (H.VERIFYIN_QTY * H.VERIFYIN_PRICE - "
            + " A.CHECK_QTY * A.STOCK_PRICE,0) AS DIFF_AMT"
            + " FROM IND_PURPLAND A,IND_PURPLANM B,SYS_SUPPLIER C,SYS_FEE D, "
            + " SYS_UNIT E, IND_PURORDERM F,IND_PURORDERD G, IND_VERIFYIND H"
            + " WHERE A.PLAN_NO = B.PLAN_NO"
            + " AND A.SUP_CODE = C.SUP_CODE"
            + " AND A.ORDER_CODE = D.ORDER_CODE"
            + " AND A.PURCH_UNIT = E.UNIT_CODE"
            + " AND A.PLAN_NO = F.PLAN_NO"
            + " AND A.ORDER_CODE = G.ORDER_CODE"
            + " AND B.PLAN_NO = F.PLAN_NO"
            + " AND F.PURORDER_NO = G.PURORDER_NO"
            + " AND G.PURORDER_NO = H.PURORDER_NO(+)"
            + " AND G.SEQ_NO = H.PURSEQ_NO(+)"
            + " AND A.PLAN_NO = '" + plan_no + "'";
    }

    /**
     * ��ѯҩƷ�����κ�Ч��
     * @param org_code String
     * @param order_code String
     * @param unit_type String
     * @return String
     */
    public static String getOrderBatchNoValid(String org_code,
                                              String order_code,
                                              String unit_type) {
    	//luhai modify 2012-3-16 ����unittype���д���
//        return
//            "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
//            + "A.BATCH_SEQ, A.STOCK_QTY, B.STOCK_UNIT, "
//            +
//            "A.RETAIL_PRICE * C.STOCK_QTY * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE "
//            + "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
//            + "WHERE A.ORDER_CODE = B.ORDER_CODE " +
//            "AND A.ORDER_CODE = C.ORDER_CODE "
//            + "AND B.ORDER_CODE = C.ORDER_CODE "
//            + "AND A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '" +
//            order_code + "' AND A.ACTIVE_FLG = 'Y'";
    	if("0".equals(unit_type)){
    		return
    		"SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
    		+ "A.BATCH_SEQ, A.STOCK_QTY/C.DOSAGE_QTY AS STOCK_QTY, B.STOCK_UNIT, "
    		+
    		"A.RETAIL_PRICE * C.STOCK_QTY * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE* C.STOCK_QTY * C.DOSAGE_QTY AS VERIFYIN_PRICE "
    		+ "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
    		+ "WHERE A.ORDER_CODE = B.ORDER_CODE " +
    		"AND A.ORDER_CODE = C.ORDER_CODE "
    		+ "AND B.ORDER_CODE = C.ORDER_CODE "
    		+ "AND A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '" +
    		order_code + "' AND A.ACTIVE_FLG = 'Y'";    		
    		
    	}else{
	        return
	            "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
	            + "A.BATCH_SEQ, A.STOCK_QTY, B.DOSAGE_UNIT, "
	            +
	            "A.RETAIL_PRICE  AS OWN_PRICE, A.VERIFYIN_PRICE "
	            + "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
	            + "WHERE A.ORDER_CODE = B.ORDER_CODE " +
	            "AND A.ORDER_CODE = C.ORDER_CODE "
	            + "AND B.ORDER_CODE = C.ORDER_CODE "
	            + "AND A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '" +
	            order_code + "' AND A.ACTIVE_FLG = 'Y'";
    	}
    	//luhai modify 2012-3-16 ����unittype���д���end
    }

    /**
     * �����û�ID��ѯ����ҩ�����
     * @param user_id String
     * @return String
     */
    public static String getIndOrgByUserId(String user_id, String region_code) {
        return "SELECT B.ORG_CODE AS ID, B.ORG_CHN_DESC AS NAME, B.PY1, B.PY2 "
            + "FROM SYS_OPERATOR_DEPT A, IND_ORG B "
            + "WHERE A.DEPT_CODE = B.ORG_CODE AND A.USER_ID = '" + user_id
            + "' AND " + " B.REGION_CODE = '" + region_code
            + "' ORDER BY A.MAIN_FLG DESC";
    }

    /**
     * �����û�ID��ѯ����ҩ�����
     * @param user_id String
     * @param where String
     * @return String
     */
    public static String getIndOrgByUserId(String user_id, String region_code, String where) {
        return "SELECT B.ORG_CODE AS ID, B.ORG_CHN_DESC AS NAME, B.PY1, B.PY2 "
            + " FROM SYS_OPERATOR_DEPT A, IND_ORG B "
            + " WHERE A.DEPT_CODE = B.ORG_CODE AND A.USER_ID = '"
            + user_id + "' AND B.REGION_CODE = '" + region_code +
            "' " + where + " ORDER BY A.MAIN_FLG DESC";
    }

    /**
     * �����û�ID��ѯ��������
     * @param user_id String
     * @param where String
     * @return String
     */
    public static String initTextFormatSysDept(String region_code) {
        return "SELECT DEPT_CODE AS ID, DEPT_ABS_DESC AS NAME, PY1, PY2 "
            + "FROM SYS_DEPT "
            + "WHERE FINAL_FLG = 'Y' AND DEPT_GRADE = '3' "
            + " AND REGION_CODE = '" + region_code + "'  "
            + " ORDER BY DEPT_CODE";
    }

    /**
     * �����û�ID��ѯ��������
     * @param user_id String
     * @param where String
     * @return String
     */
    public static String initTextFormatSysDept(String user_id, String region_code) {
        return
            "SELECT B.DEPT_CODE AS ID, B.DEPT_ABS_DESC AS NAME,B.PY1, B.PY2 "
            + "FROM SYS_OPERATOR_DEPT A, SYS_DEPT B "
            + "WHERE A.DEPT_CODE = B.DEPT_CODE AND A.USER_ID = '" + user_id
            + "' AND B.FINAL_FLG = 'Y' AND B.DEPT_GRADE = '3' "
            + " AND B.REGION_CODE = '" + region_code + "' "
            + " ORDER BY A.MAIN_FLG DESC";
    }
    /**
     * ����region��ѯ����
     * @param user_id String
     * @param where String
     * @return String
     * luhai 2012-04-07
     */
    public static String initTextFormatSysDeptWithRegion(String region_code) {
    	return
    	"SELECT B.DEPT_CODE AS ID, B.DEPT_ABS_DESC AS NAME,B.PY1, B.PY2 "
    	+ "FROM SYS_DEPT B "
    	+ "WHERE "
    	+ " B.FINAL_FLG = 'Y' AND B.DEPT_GRADE = '3' "
    	+ " AND B.REGION_CODE = '" + region_code + "' "
    	+ " ORDER BY B.DEPT_CODE ";
    }


    /**
     * ��ʼ��TextFormat
     * @return String
     */
    public static String initTextFormatIndOrg(String org_type, String region_code) {
        return "SELECT ORG_CODE AS ID, ORG_CHN_DESC AS NAME, PY1, PY2 "
            + "FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = '" + org_type +
            "' AND REGION_CODE = '" + region_code + "' ";
    }

    /**
     * ��ʼ��TextFormat
     * @return String
     */
    public static String initTextFormatIndOrg(String org_type, String exinvflg, String region_code) {
        return "SELECT ORG_CODE AS ID, ORG_CHN_DESC AS NAME, PY1, PY2 "
            + "FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = '" + org_type +
            "' AND EXINV_FLG = '" + exinvflg + "'";
    }

    /**
     * �������ι��˲�ѯ
     * @param org_code String
     * @param tran_date String
     * @return String
     */
    public static String getStockBatchByOrgCode(String org_code,
                                                String tran_date) {
        return "SELECT COUNT(*) AS NUM FROM IND_DDSTOCK WHERE ORG_CODE = '" +
            org_code +
            "' AND TRANDATE = '" + tran_date + "'";
    }

    /**
     * ��ѯҩƷ����ǰ�ļ۸�
     * @param order_code String
     * @param date String
     * @return String
     */
    public static String getOrderSysFeeHistoryPrice(String order_code,
        String yesterday) {
        return
            "SELECT ORDER_CODE , OWN_PRICE FROM SYS_FEE_HISTORY WHERE ORDER_CODE = '" +
            order_code + "' AND START_DATE >= '" + yesterday + "000000" +
            "' AND START_DATE <= '" + yesterday + "2359595" + "'";
    }

    /**
     * ҩ���ս��в�ѯҩ���ɷ�ҩ��ҩƷ����
     * @param org_code String
     * @return String
     */
    public static String getIndDDStockOrderTypeCode(String org_code) {
        return "SELECT B.TYPE_CODE , C.CHN_DESC "
            + " FROM IND_DDSTOCK A, PHA_BASE B , SYS_DICTIONARY C "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND B.TYPE_CODE = C.ID "
            + " AND A.ORG_CODE = '" + org_code
            + "' AND C.GROUP_ID='SYS_PHATYPE' "
            + " GROUP BY B.TYPE_CODE,C.CHN_DESC ORDER BY B.TYPE_CODE";
    }

    /**
     * ��ѯ��ҩ��/ҩ����ҩƷ�ĸ���
     * @param org_code String
     * @param type_code String
     * @return String
     */
    public static String getIndDDStockOrderTypeCode(String org_code,
        String type_code) {
        return "SELECT DISTINCT A.ORDER_CODE FROM IND_DDSTOCK A, PHA_BASE B " +
            "WHERE A.ORDER_CODE = B.ORDER_CODE " +
            "AND A.ORG_CODE = '" + org_code + "' AND B.TYPE_CODE = '" +
            type_code + "' GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE ";
    }

    /**
     * ���ڽ��ɱ����
     * @param trandate String
     * @param org_code String
     * @param order_code String
     * @param stock_price double
     * @return String
     */
    public static String getIndDDStockLastStockAMT(String trandate,
        String org_code, String order_code, double stock_price) {
        return "SELECT SUM(LAST_TOTSTOCK_QTY * VERIFYIN_PRICE ) " +
            " AS LAST_STOCK_AMT FROM IND_DDSTOCK WHERE TRANDATE='" + trandate +
            "' AND ORG_CODE='" + org_code + "' AND ORDER_CODE ='" + order_code +
            "'";
    }

    /**
     * ���ڽ�����۽��
     * @param trandate String
     * @param org_code String
     * @param order_code String
     * @param stock_price double
     * @return String
     */
    public static String getIndDDStockLastOwnAMT(String trandate,
                                                 String org_code,
                                                 String order_code) {
        return "SELECT SUM(LAST_TOTSTOCK_QTY * RETAIL_PRICE) " +
            " AS LAST_OWN_AMT FROM IND_DDSTOCK WHERE TRANDATE='" + trandate +
            "' AND ORG_CODE='" + org_code + "' AND ORDER_CODE ='" + order_code +
            "'";
    }

    /**
     * �̵��
     * @param org_code String
     * @param frozen_date String
     * @return String
     */
    public static String getIndQtyCheckM(String org_code, String frozen_date) {
        return "SELECT C.DEPT_CHN_DESC, CASE WHEN (D.GOODS_DESC IS NULL) "
            + "THEN D.ORDER_DESC ELSE D.ORDER_DESC || ' ('|| D.GOODS_DESC||')'"
            + "END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
            + "FLOOR (B.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
            + "|| MOD (B.STOCK_QTY, E.DOSAGE_QTY) "
            + "|| G.UNIT_CHN_DESC AS STOCK_QTY, "
            + "FLOOR (A.ACTUAL_CHECK_QTY/E.DOSAGE_QTY)||F.UNIT_CHN_DESC "
            + "|| MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
            + "|| G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, H.MATERIAL_CHN_DESC, "
            + " A.VALID_DATE , A.BATCH_NO "
            + "FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "
            + "PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J, "
            + "IND_MATERIALLOC H WHERE A.ORG_CODE = B.ORG_CODE "
            + "AND A.ORDER_CODE = B.ORDER_CODE AND A.BATCH_SEQ = B.BATCH_SEQ "
            + "AND A.ORG_CODE = C.DEPT_CODE AND A.ORDER_CODE = D.ORDER_CODE "
            + "AND A.ORDER_CODE = E.ORDER_CODE AND A.STOCK_UNIT = F.UNIT_CODE "
            + "AND A.DOSAGE_UNIT =G.UNIT_CODE AND A.ORDER_CODE = J.ORDER_CODE "
            + "AND B.MATERIAL_LOC_CODE = H.MATERIAL_LOC_CODE(+) "
            + "AND B.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
            + "AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '" +
            frozen_date + "' ORDER BY A.ORDER_CODE";
    }

    /**
     * �̵���ϸ��
     * @param org_code String
     * @param frozen_date String
     * @return String
     */
    public static String getIndQtyCheckD(String org_code, String frozen_date) {
    	//luhai modify 2012-1-23 ���ɱ��۸������ռ۸� begin
//        return "SELECT C.DEPT_CHN_DESC, "
//            + "CASE WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
//            + " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
//            + " END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
//            + " FLOOR (A.STOCK_QTY / E.DOSAGE_QTY) "
//            + " || F.UNIT_CHN_DESC || MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
//            + " || G.UNIT_CHN_DESC AS STOCK_QTY, "
//            + "A.STOCK_PRICE  * E.DOSAGE_QTY  AS CONTRACT_PRICE "
//            + ", A.RETAIL_PRICE * E.DOSAGE_QTY AS OWN_PRICE, "
//            + " A.STOCK_PRICE * A.STOCK_QTY AS STOCK_AMT,"
//            +
//            " A.RETAIL_PRICE * A.STOCK_QTY AS OWM_AMT, A.VALID_DATE, A.BATCH_NO "
//            + " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "
//            +
//            " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J "
//            + " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE "
//            + " AND A.BATCH_SEQ = B.BATCH_SEQ AND A.ORG_CODE = C.DEPT_CODE "
//            +
//            " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//            +
//            " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//            + " AND A.ORDER_CODE = J.ORDER_CODE AND A.STOCK_QTY > 0 "
//            + " AND A.ORG_CODE = '" + org_code +
//            "'  AND A.FROZEN_DATE = '" + frozen_date +
//            "' ORDER BY A.ORDER_CODE";
        return "SELECT C.DEPT_CHN_DESC, "
            + "CASE WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
            + " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
            + " END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
            + " FLOOR (A.ACTUAL_CHECK_QTY / E.DOSAGE_QTY) "
            + " || F.UNIT_CHN_DESC || MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
            + " || G.UNIT_CHN_DESC AS STOCK_QTY, "
            + "A.VERIFYIN_PRICE  * E.DOSAGE_QTY  AS CONTRACT_PRICE "
            + ", A.RETAIL_PRICE * E.DOSAGE_QTY AS OWN_PRICE, "
            + " A.VERIFYIN_PRICE * A.ACTUAL_CHECK_QTY AS STOCK_AMT,"
            +
            " A.RETAIL_PRICE * A.ACTUAL_CHECK_QTY AS OWM_AMT, A.VALID_DATE, A.BATCH_NO "
            + " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "
            +
            " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J "
            //luhai modify 2012-05-09 begin  �̵���ӡʱindstock ʹ������ begin 
            + " WHERE A.ORG_CODE = B.ORG_CODE(+) AND A.ORDER_CODE = B.ORDER_CODE(+) "
            + " AND A.BATCH_SEQ = B.BATCH_SEQ(+) AND A.ORG_CODE = C.DEPT_CODE "
            +
            //luhai modify 2012-05-09 begin  �̵���ӡʱindstock ʹ������  end 
            " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
            +
            " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
            + " AND A.ORDER_CODE = J.ORDER_CODE AND A.STOCK_QTY > 0  AND A.ACTUAL_CHECK_QTY > 0"
            + " AND A.ORG_CODE = '" + org_code +
            "'  AND A.FROZEN_DATE = '" + frozen_date +
            "' ORDER BY A.ORDER_CODE";
      //luhai modify 2012-1-23 ���ɱ��۸������ռ۸�  end
    }

    /**
     * ӯ����
     * @param org_code String
     * @param frozen_date String
     * @return String
     */
    public static String getIndQtyProfitLoss(String org_code,
                                             String frozen_date) {
    	//luhai modify 2012-2-13 verifyinPrice begin
//        return "SELECT C.DEPT_CHN_DESC, CASE "
//            + " WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
//            + " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
//            + " END AS ORDER_DESC, D.SPECIFICATION, A.VALID_DATE, A.BATCH_NO, "
//            + " FLOOR (A.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
//            +
//            " || MOD (A.STOCK_QTY, E.DOSAGE_QTY)||G.UNIT_CHN_DESC AS STOCK_QTY, "
//            + " FLOOR (A.ACTUAL_CHECK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
//            + " || MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
//            + " || G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, "
//            + " FLOOR (A.MODI_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
//            + " || MOD (A.MODI_QTY, E.DOSAGE_QTY) "
//            + " || G.UNIT_CHN_DESC AS MODI_QTY,A.RETAIL_PRICE AS OWN_PRICE, "
//            + " A.MODI_QTY * A.RETAIL_PRICE AS FREEZE_AMT, "
//            + " A.RETAIL_PRICE * A.ACTUAL_CHECK_QTY AS OWN_AMT,A.UNFREEZE_DATE"
//            + " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D,  "
//            + " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
//            + " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE "
//            + " AND A.BATCH_SEQ = B.BATCH_SEQ AND A.ORG_CODE = C.DEPT_CODE "
//            +
//            " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
//            +
//            " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
//            + " AND A.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
//            + " AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '" +
//            frozen_date + "' ORDER BY A.ORDER_CODE";
        return "SELECT C.DEPT_CHN_DESC, CASE "
            + " WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
            + " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
            + " END AS ORDER_DESC, D.SPECIFICATION, A.VALID_DATE, A.BATCH_NO, "
            + " FLOOR (A.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
            +
            " || MOD (A.STOCK_QTY, E.DOSAGE_QTY)||G.UNIT_CHN_DESC AS STOCK_QTY, "
            + " FLOOR (A.ACTUAL_CHECK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
            + " || MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
            + " || G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, "
            + " FLOOR (A.MODI_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
            + " || MOD (A.MODI_QTY, E.DOSAGE_QTY) "
            + " || G.UNIT_CHN_DESC AS MODI_QTY,A.VERIFYIN_PRICE AS OWN_PRICE, "
            + " A.MODI_QTY * A.VERIFYIN_PRICE AS FREEZE_AMT, "
            + " A.VERIFYIN_PRICE * A.ACTUAL_CHECK_QTY AS OWN_AMT,A.UNFREEZE_DATE"
            + " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D,  "
            + " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
          //luhai modify 2012-05-09 begin  �̵���ӡʱindstock ʹ������ begin 
            + " WHERE A.ORG_CODE = B.ORG_CODE(+) AND A.ORDER_CODE = B.ORDER_CODE(+) "
            + " AND A.BATCH_SEQ = B.BATCH_SEQ(+) AND A.ORG_CODE = C.DEPT_CODE "
          //luhai modify 2012-05-09 begin  �̵���ӡʱindstock ʹ������ end
            +
            " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
            +
            " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
            + " AND A.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
            + " AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '" +
            frozen_date + "' ORDER BY A.ORDER_CODE";
      //luhai modify 2012-2-13 verifyinPrice end
    }

    /**
     * ȡ�ÿ��������Ϣ
     * @return String
     */
    public static String getIndStockMInfo(String org_code, String order_code,
                                          String material_loc_code) {
        String sql =
            "SELECT A.ORDER_CODE, C.ORDER_DESC, A.MATERIAL_LOC_CODE,A.MATERIAL_LOC_DESC,A.ELETAG_CODE, A.DISPENSE_FLG, "
            + "A.DISPENSE_ORG_CODE, C.UNIT_CODE, A.SAFE_QTY, A.MAX_QTY, "
            +
            "A.MIN_QTY, A.ECONOMICBUY_QTY, A.BUY_UNRECEIVE_QTY, A.MM_USE_QTY, "
            + "0 AS STOCK_QTY, A.DD_USE_QTY, A.STANDING_QTY,A.MATERIAL_LOC_SEQ,SUP_CODE, "
            + "A.QTY_TYPE, A.ACTIVE_FLG FROM IND_STOCKM A, SYS_FEE C "
            + "WHERE A.ORDER_CODE = C.ORDER_CODE "
            + "AND A.ORG_CODE = '" + org_code + "'";
        if (!"".equals(order_code)) {
            sql += " AND A.ORDER_CODE = '" + order_code + "'";
        }
        if (!"".equals(material_loc_code)) {
            sql += " AND A.MATERIAL_LOC_CODE = '" + material_loc_code + "'";
        }
        sql += " ORDER BY A.ORDER_CODE ";
        return sql;
    }

    /**
     * ����IND_STOCK����λ
     * @param org_code String
     * @param order_code String
     * @return String
     */
    public static String onUpdateIndStcokMaterialLocCode(String org_code,
        String order_code, String material_loc_code) {
        return "UPDATE IND_STOCK SET MATERIAL_LOC_CODE ='" + material_loc_code +
            "' WHERE ORG_CODE = '" + org_code + "' AND ORDER_CODE = '" +
            order_code + "'";
    }

    /**
     * ҩƷ��ϸ�˻��ܲ�ѯ
     * @return String
     */
    public static String getINDPhaDetailMQuery(String org_code,
                                               String start_date,
                                               String end_date,
                                               String order_code) {
    	//luhai modify 2012-1-24 �����ۼ۸ĳɲɹ�����ʾ begin
//        String sql =
//            "SELECT CASE WHEN (B.GOODS_DESC IS NULL) THEN B.ORDER_DESC "
//            + " ELSE B.ORDER_DESC ||'('|| B.GOODS_DESC ||')' "
//            + " END AS ORDER_DESC, "
//            + " B.SPECIFICATION, "
//            + " SUM(A.LAST_TOTSTOCK_QTY) AS LAST_TOTSTOCK_QTY,C.UNIT_CHN_DESC,"
//            + " SUM (A.LAST_TOTSTOCK_QTY * A.RETAIL_PRICE) AS LAST_TOTSTOCK_AMT, "
//            + " SUM (A.IN_QTY) AS STOCKIN_QTY, "
//            + " SUM (A.IN_QTY * A.RETAIL_PRICE) AS STOCKIN_AMT, "
//            + " SUM (A.OUT_QTY) AS STOCKOUT_QTY, "
//            + " SUM (A.OUT_QTY * A.RETAIL_PRICE) AS STOCKOUT_AMT, "
//            + " SUM (A.CHECKMODI_QTY) AS CHECKMODI_QTY, "
//            + " SUM (A.CHECKMODI_QTY * A.RETAIL_PRICE) AS CHECKMODI_AMT, "
//            + " SUM (A.STOCK_QTY) AS STOCK_QTY, "
//            + " SUM (A.STOCK_QTY * A.RETAIL_PRICE) AS STOCK_AMT, A.ORDER_CODE, "
//            + " A.ORG_CODE, A.RETAIL_PRICE AS OWN_PRICE, D.ORG_CHN_DESC "
//            + " FROM IND_DDSTOCK A, SYS_FEE B, SYS_UNIT C, IND_ORG D "
//            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
//            + " AND B.UNIT_CODE = C.UNIT_CODE "
//            + " AND A.ORG_CODE = D.ORG_CODE "
//            + " AND A.ORG_CODE = '" + org_code +
//            "' AND TO_DATE(A.TRANDATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(" +
//            start_date + ", 'YYYYMMDDHH24MISS') AND TO_DATE(" + end_date +
//            ", 'YYYYMMDDHH24MISS') ";
//        String where = "";
//        if (!"".equals(order_code)) {
//            where = " AND A.ORDER_CODE = '" + order_code + "'";
//        }
//        return sql + where + " GROUP BY B.ORDER_DESC, B.SPECIFICATION, "
//            + " C.UNIT_CHN_DESC, A.ORDER_CODE, A.ORG_CODE, A.RETAIL_PRICE, "
//            + " D.ORG_CHN_DESC,B.GOODS_DESC ORDER BY A.ORDER_CODE ";
        String sql =
            "SELECT CASE WHEN (B.GOODS_DESC IS NULL) THEN B.ORDER_DESC "
            + " ELSE B.ORDER_DESC ||'('|| B.GOODS_DESC ||')' "
            + " END AS ORDER_DESC, "
            + " B.SPECIFICATION, "
            + " SUM(A.LAST_TOTSTOCK_QTY) AS LAST_TOTSTOCK_QTY,C.UNIT_CHN_DESC,"
            + " SUM (A.LAST_TOTSTOCK_QTY * A.VERIFYIN_PRICE) AS LAST_TOTSTOCK_AMT, "
            + " SUM (A.IN_QTY) AS STOCKIN_QTY, "
            + " SUM (A.IN_QTY *  A.VERIFYIN_PRICE) AS STOCKIN_AMT, "
            + " SUM (A.OUT_QTY) AS STOCKOUT_QTY, "
            + " SUM (A.OUT_QTY *  A.VERIFYIN_PRICE) AS STOCKOUT_AMT, "
            + " SUM (A.CHECKMODI_QTY) AS CHECKMODI_QTY, "
            + " SUM (A.CHECKMODI_QTY *  A.VERIFYIN_PRICE) AS CHECKMODI_AMT, "
            + " SUM (A.STOCK_QTY) AS STOCK_QTY, "
            + " SUM (A.STOCK_QTY *  A.VERIFYIN_PRICE) AS STOCK_AMT, A.ORDER_CODE, "
            + " A.ORG_CODE,  A.VERIFYIN_PRICE AS OWN_PRICE, D.ORG_CHN_DESC "
            + " FROM IND_DDSTOCK A, SYS_FEE B, SYS_UNIT C, IND_ORG D "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND B.UNIT_CODE = C.UNIT_CODE "
            + " AND A.ORG_CODE = D.ORG_CODE "
            + " AND A.ORG_CODE = '" + org_code +
            "' AND TO_DATE(A.TRANDATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(" +
            start_date + ", 'YYYYMMDDHH24MISS') AND TO_DATE(" + end_date +
            ", 'YYYYMMDDHH24MISS') ";
        String where = "";
        if (!"".equals(order_code)) {
            where = " AND A.ORDER_CODE = '" + order_code + "'";
        }
        return sql + where + " GROUP BY B.ORDER_DESC, B.SPECIFICATION, "
            + " C.UNIT_CHN_DESC, A.ORDER_CODE, A.ORG_CODE, A.VERIFYIN_PRICE, "
            + " D.ORG_CHN_DESC,B.GOODS_DESC ORDER BY A.ORDER_CODE ";
    	//luhai modify 2012-1-24 �����ۼ۸ĳɲɹ�����ʾ end
    }

    /**
     * ҩƷ��ϸ����ϸ��ѯ(����)
     * @return String
     */
    public static String getINDPhaDetailDQueryA(String org_code,
                                                String start_date,
                                                String end_date,
                                                String qty_in,
                                                String qty_out,
                                                String qty_check,
                                                String order_code) {
    	//luhai modify 2012-1-24 �����ۼ۸ĳɲɹ��� begin
//        String order_code_sql = "";
//        // ��ҩƷ��ѯ
//        if (!"".equals(order_code)) {
//            order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
//        }
//        String sql_in = "";
//        String sql_out = "";
//        String sql_check = "";
//        // ��ⲿ��
//        if ("Y".equals(qty_in)) {
//            // ������ⲿ��
//            sql_in += "SELECT A.CHECK_DATE, 'VER' AS STATUS, C.ORDER_DESC, "
//                + "C.SPECIFICATION,SUM (B.VERIFYIN_QTY) AS QTY, "
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE AS OWN_PRICE, "
//                + " SUM (B.VERIFYIN_QTY * B.RETAIL_PRICE) AS AMT, "
//                + " F.ORG_CHN_DESC "
//                + " FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_FEE C, "
//                + " SYS_UNIT E, IND_ORG F "
//                + " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.BILL_UNIT = E.UNIT_CODE "
//                + " AND A.ORG_CODE = F.ORG_CODE "
//                + " AND A.CHECK_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.CHECK_DATE IS NOT NULL "
//                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
//                + " GROUP BY A.CHECK_DATE, C.ORDER_DESC, C.SPECIFICATION, "
//                + " E.UNIT_CHN_DESC, F.ORG_CHN_DESC, B.RETAIL_PRICE "
//                + " UNION "
//                // �˿ⲿ��
//                + " SELECT A.WAREHOUSING_DATE, 'RET' AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
//                + " B.RETAIL_PRICE AS OWN_PRICE, SUM (B.RETAIL_PRICE * "
//                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.TO_ORG_CODE = F.ORG_CODE "
//                + " AND A.REQTYPE_CODE = 'RET' "
//                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.WAREHOUSING_DATE IS NOT NULL "
//                + " AND A.UPDATE_FLG <> '2' "
//                + " AND A.TO_ORG_CODE = '" + org_code + "' "+ order_code_sql
//                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC, C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE, F.ORG_CHN_DESC "
//                + " UNION "
//                // ������ⲿ��
//                + " SELECT A.WAREHOUSING_DATE, 'THI' AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
//                + " B.RETAIL_PRICE AS OWN_PRICE, SUM (B.RETAIL_PRICE * "
//                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.APP_ORG_CODE = F.ORG_CODE "
//                + " AND A.REQTYPE_CODE = 'THI' "
//                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.WAREHOUSING_DATE IS NOT NULL "
//                + " AND A.UPDATE_FLG <> '2' "
//                + " AND A.APP_ORG_CODE = '" + org_code + "' "+ order_code_sql
//                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE, F.ORG_CHN_DESC ";
//        }
//        // ���ⲿ��
//        if ("Y".equals(qty_out)) {
//            if (!"".equals(sql_in))
//                sql_out = sql_out + " UNION ";
//            sql_out = sql_out
//                // ������ⲿ��
//                + " SELECT A.DISPENSE_DATE, A.REQTYPE_CODE AS STATUS, "
//                + " C.ORDER_DESC, C.SPECIFICATION, "
//                + " SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
//                + " B.RETAIL_PRICE AS OWN_PRICE, SUM (B.RETAIL_PRICE * "
//                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.TO_ORG_CODE = F.ORG_CODE "
//                + " AND A.REQTYPE_CODE = 'DEP' "
//                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.DISPENSE_DATE IS NOT NULL "
//                + " AND A.UPDATE_FLG <> '2' "
//                + " AND A.TO_ORG_CODE = '" + org_code + "' " +
//                order_code_sql
//                + " GROUP BY A.DISPENSE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE, F.ORG_CHN_DESC, "
//                + " A.REQTYPE_CODE "
//                + " UNION "
//                // ����,�����������ⲿ��
//                + " SELECT A.DISPENSE_DATE, A.REQTYPE_CODE AS STATUS, "
//                + " C.ORDER_DESC, C.SPECIFICATION, "
//                + " SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
//                + " B.RETAIL_PRICE AS OWN_PRICE, SUM (B.RETAIL_PRICE * "
//                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.APP_ORG_CODE = F.ORG_CODE "
//                + " AND A.REQTYPE_CODE IN ('GIF', 'WAS', 'THO') "
//                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.DISPENSE_DATE IS NOT NULL "
//                + " AND A.UPDATE_FLG <> '2' "
//                + " AND A.APP_ORG_CODE = '" + org_code + "' " +
//                order_code_sql
//                + " GROUP BY A.DISPENSE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE, F.ORG_CHN_DESC, "
//                + " A.REQTYPE_CODE "
//                + " UNION "
//                // �˻����ⲿ��
//                + " SELECT A.CHECK_DATE, 'REG' AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION, SUM (B.QTY) AS QTY, E.UNIT_CHN_DESC, "
//                + " B.UNIT_PRICE AS OWN_PRICE, "
//                + " SUM (B.UNIT_PRICE * B.QTY) AS AMT, F.ORG_CHN_DESC "
//                + " FROM IND_REGRESSGOODSM A, IND_REGRESSGOODSD B, "
//                + " SYS_FEE C,  PHA_TRANSUNIT D,SYS_UNIT E, IND_ORG F "
//                + " WHERE A.REGRESSGOODS_NO = B.REGRESSGOODS_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.BILL_UNIT = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.ORG_CODE = F.ORG_CODE "
//                + " AND A.CHECK_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.CHECK_DATE IS NOT NULL "
//                + " AND B.UPDATE_FLG <> '2' "
//                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
//                + " GROUP BY A.CHECK_DATE, C.ORDER_DESC, C.SPECIFICATION, "
//                + " E.UNIT_CHN_DESC, B.UNIT_PRICE, F.ORG_CHN_DESC ";
//        }
//        // �̵㲿��
//        if ("Y".equals(qty_check)) {
//            if (!"".equals(sql_out) || !"".equals(qty_in))
//                sql_check = sql_check + " UNION ";
//            // �̵���ҵ
//            sql_check = sql_check
//                + " SELECT TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
//                + " AS FROZEN_DATE, 'FRO' AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION, SUM (A.MODI_QTY) AS QTY, "
//                + " E.UNIT_CHN_DESC, A.RETAIL_PRICE AS OWN_PRICE, "
//                + " SUM (A.RETAIL_PRICE * A.MODI_QTY) AS AMT, "
//                + " F.ORG_CHN_DESC "
//                + " FROM IND_QTYCHECK A, SYS_FEE C, PHA_TRANSUNIT D, "
//                + " SYS_UNIT E, IND_ORG F "
//                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
//                + " AND A.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.DOSAGE_UNIT = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.ORG_CODE = F.ORG_CODE "
//                + " AND TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
//                + " BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.UNFREEZE_DATE IS NOT NULL "
//                + " AND A.MODI_QTY <> 0 "
//                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
//                + " GROUP BY A.FROZEN_DATE,C.ORDER_DESC,C.SPECIFICATION, "
//                + " E.UNIT_CHN_DESC, A.RETAIL_PRICE, F.ORG_CHN_DESC ";
//        }
//        String sql = sql_in + sql_out + sql_check;
//        return sql;
        String order_code_sql = "";
        // ��ҩƷ��ѯ
        if (!"".equals(order_code)) {
            order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
        }
        String sql_in = "";
        String sql_out = "";
        String sql_check = "";
        // ��ⲿ��
        if ("Y".equals(qty_in)) {
            // ������ⲿ��
            sql_in += "SELECT A.CHECK_DATE, 'VER' AS STATUS, C.ORDER_DESC, "
                + "C.SPECIFICATION,SUM (B.VERIFYIN_QTY) AS QTY, "
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE AS OWN_PRICE, "
                + " SUM (B.VERIFYIN_QTY * B.VERIFYIN_PRICE) AS AMT, "
                + " F.ORG_CHN_DESC "
                + " FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_FEE C, "
                + " SYS_UNIT E, IND_ORG F "
                + " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.BILL_UNIT = E.UNIT_CODE "
                + " AND A.ORG_CODE = F.ORG_CODE "
                + " AND A.CHECK_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.CHECK_DATE IS NOT NULL "
                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
                + " GROUP BY A.CHECK_DATE, C.ORDER_DESC, C.SPECIFICATION, "
                + " E.UNIT_CHN_DESC, F.ORG_CHN_DESC, B.VERIFYIN_PRICE "
                + " UNION "
                // �˿ⲿ��
                + " SELECT A.WAREHOUSING_DATE, 'RET' AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
                + " B.VERIFYIN_PRICE AS OWN_PRICE, SUM (B.VERIFYIN_PRICE * "
                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.TO_ORG_CODE = F.ORG_CODE "
                + " AND A.REQTYPE_CODE = 'RET' "
                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.WAREHOUSING_DATE IS NOT NULL "
                + " AND A.UPDATE_FLG <> '2' "
                + " AND A.TO_ORG_CODE = '" + org_code + "' "+ order_code_sql
                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC, C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC "
                + " UNION "
                // ������ⲿ��
                + " SELECT A.WAREHOUSING_DATE, 'THI' AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
                + " B.VERIFYIN_PRICE AS OWN_PRICE, SUM (B.VERIFYIN_PRICE * "
                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.APP_ORG_CODE = F.ORG_CODE "
                + " AND A.REQTYPE_CODE = 'THI' "
                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.WAREHOUSING_DATE IS NOT NULL "
                + " AND A.UPDATE_FLG <> '2' "
                + " AND A.APP_ORG_CODE = '" + org_code + "' "+ order_code_sql
                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC ";
        }
        // ���ⲿ��
        if ("Y".equals(qty_out)) {
            if (!"".equals(sql_in))
                sql_out = sql_out + " UNION ";
            sql_out = sql_out
                // ������ⲿ��
                + " SELECT A.DISPENSE_DATE, A.REQTYPE_CODE AS STATUS, "
                + " C.ORDER_DESC, C.SPECIFICATION, "
                + " SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
                + " B.VERIFYIN_PRICE AS OWN_PRICE, SUM (B.VERIFYIN_PRICE * "
                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.TO_ORG_CODE = F.ORG_CODE "
                + " AND A.REQTYPE_CODE = 'DEP' "
                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.DISPENSE_DATE IS NOT NULL "
                + " AND A.UPDATE_FLG <> '2' "
                + " AND A.TO_ORG_CODE = '" + org_code + "' " +
                order_code_sql
                + " GROUP BY A.DISPENSE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC, "
                + " A.REQTYPE_CODE "
                + " UNION "
                // ����,�����������ⲿ��
                + " SELECT A.DISPENSE_DATE, A.REQTYPE_CODE AS STATUS, "
                + " C.ORDER_DESC, C.SPECIFICATION, "
                + " SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
                + " B.VERIFYIN_PRICE AS OWN_PRICE, SUM (B.VERIFYIN_PRICE * "
                + " B.ACTUAL_QTY) AS AMT, F.ORG_CHN_DESC "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, IND_ORG F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.APP_ORG_CODE = F.ORG_CODE "
                + " AND A.REQTYPE_CODE IN ('GIF', 'WAS', 'THO') "
                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.DISPENSE_DATE IS NOT NULL "
                + " AND A.UPDATE_FLG <> '2' "
                + " AND A.APP_ORG_CODE = '" + org_code + "' " +
                order_code_sql
                + " GROUP BY A.DISPENSE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC, "
                + " A.REQTYPE_CODE "
                + " UNION "
                // �˻����ⲿ��
                + " SELECT A.CHECK_DATE, 'REG' AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION, SUM (B.QTY) AS QTY, E.UNIT_CHN_DESC, "
                + " B.UNIT_PRICE AS OWN_PRICE, "
                + " SUM (B.UNIT_PRICE * B.QTY) AS AMT, F.ORG_CHN_DESC "
                + " FROM IND_REGRESSGOODSM A, IND_REGRESSGOODSD B, "
                + " SYS_FEE C,  PHA_TRANSUNIT D,SYS_UNIT E, IND_ORG F "
                + " WHERE A.REGRESSGOODS_NO = B.REGRESSGOODS_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.BILL_UNIT = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.ORG_CODE = F.ORG_CODE "
                + " AND A.CHECK_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.CHECK_DATE IS NOT NULL "
                + " AND B.UPDATE_FLG <> '2' "
                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
                + " GROUP BY A.CHECK_DATE, C.ORDER_DESC, C.SPECIFICATION, "
                + " E.UNIT_CHN_DESC, B.UNIT_PRICE, F.ORG_CHN_DESC ";
        }
        // �̵㲿��
        if ("Y".equals(qty_check)) {
            if (!"".equals(sql_out) || !"".equals(qty_in))
                sql_check = sql_check + " UNION ";
            // �̵���ҵ
            sql_check = sql_check
                + " SELECT TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
                + " AS FROZEN_DATE, 'FRO' AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION, SUM (A.MODI_QTY) AS QTY, "
                + " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE AS OWN_PRICE, "
                + " SUM (A.VERIFYIN_PRICE * A.MODI_QTY) AS AMT, "
                + " F.ORG_CHN_DESC "
                + " FROM IND_QTYCHECK A, SYS_FEE C, PHA_TRANSUNIT D, "
                + " SYS_UNIT E, IND_ORG F "
                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND A.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.ORG_CODE = F.ORG_CODE "
                + " AND TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
                + " BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.UNFREEZE_DATE IS NOT NULL "
                + " AND A.MODI_QTY <> 0 "
                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
                + " GROUP BY A.FROZEN_DATE,C.ORDER_DESC,C.SPECIFICATION, "
                + " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE, F.ORG_CHN_DESC ";
        }
        String sql = sql_in + sql_out + sql_check;
        return sql;
      //luhai modify 2012-1-24 �����ۼ۸ĳɲɹ��� end
    }

    /**
     * ҩƷ��ϸ����ϸ��ѯ(�п�)
     * @return String
     */
    public static String getINDPhaDetailDQueryB(String org_code,
                                                String start_date,
                                                String end_date,
                                                String qty_in,
                                                String qty_out,
                                                String qty_check,
                                                String order_code) {
    	//***************************************************
    	//luhai modify 2012-1-24 �����ۼ۸�ĳɲɹ��۸�begin
    	//***************************************************
//        String order_code_sql = "";
//        // ��ҩƷ��ѯ
//        if (!"".equals(order_code)) {
//            order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
//        }
//        String sql_in = "";
//        String sql_out = "";
//        String sql_check = "";
//        // ��ⲿ��
//        if ("Y".equals(qty_in)) {
//            // �������,������ⲿ��
//            sql_in += " SELECT A.WAREHOUSING_DATE AS CHECK_DATE, "
//                + "  A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, C.SPECIFICATION, "
//                + " SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
//                + " B.RETAIL_PRICE AS OWN_PRICE, SUM (B.RETAIL_PRICE * "
//                + " B.ACTUAL_QTY) AS AMT, F.DEPT_CHN_DESC, "
//                + " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.APP_ORG_CODE = F.DEPT_CODE "
//                + " AND A.REQTYPE_CODE IN ('DEP','THI') "
//                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.WAREHOUSING_DATE IS NOT NULL "
//                + " AND A.UPDATE_FLG <> '2' "
//                + " AND A.APP_ORG_CODE = '" + org_code + "' " + order_code_sql
//                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE, F.DEPT_CHN_DESC, "
//                + " A.REQTYPE_CODE "
//                + " UNION "
//                // ��ҩ���,������ⲿ��
//                + " SELECT A.WAREHOUSING_DATE, A.REQTYPE_CODE AS STATUS, "
//                + " C.ORDER_DESC, "
//                + " C.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
//                + " B.RETAIL_PRICE AS OWN_PRICE, SUM (B.RETAIL_PRICE * "
//                + " B.ACTUAL_QTY) AS AMT, F.DEPT_CHN_DESC, "
//                + " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.TO_ORG_CODE = F.DEPT_CODE "
//                + " AND A.REQTYPE_CODE IN ('RET', 'GIF') "
//                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.WAREHOUSING_DATE IS NOT NULL "
//                + " AND A.UPDATE_FLG <> '2' "
//                + " AND A.TO_ORG_CODE = '" + org_code + "' " + order_code_sql
//                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE, F.DEPT_CHN_DESC, "
//                + " A.REQTYPE_CODE "
//                + " UNION "
//                // �ż�����ҩ��ⲿ��
//                + " SELECT A.PHA_RETN_DATE , A.ADM_TYPE||'_RET' AS STATUS, "
//                + " C.ORDER_DESC, C.SPECIFICATION, SUM (A.DOSAGE_QTY) AS QTY, "
//                + " E.UNIT_CHN_DESC, A.OWN_PRICE, "
//                + " SUM (A.OWN_PRICE * A.DOSAGE_QTY) AS AMT, "
//                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
//                + " FROM OPD_ORDER A,SYS_FEE C,PHA_TRANSUNIT D,SYS_UNIT E, "
//                + " SYS_DEPT F, SYS_PATINFO G "
//                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
//                + " AND A.ORDER_CODE = D.ORDER_CODE "
//                + " AND D.DOSAGE_UNIT = E.UNIT_CODE "
//                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.MR_NO = G.MR_NO "
//                + " AND A.PHA_RETN_DATE IS NOT NULL "
//                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
//                + " AND A.PHA_RETN_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//                order_code_sql
//                + " GROUP BY A.PHA_RETN_DATE, C.ORDER_DESC, C.SPECIFICATION, "
//                + " E.UNIT_CHN_DESC, A.OWN_PRICE, F.DEPT_CHN_DESC,"
//                + " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE "
//                + " UNION "
//                // סԺ��ҩ��ⲿ��
//                + " SELECT A.PHA_RETN_DATE, 'I_RET' AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION, SUM (A.RTN_DOSAGE_QTY) -  "
//                + " NVL (SUM (A.CANCEL_DOSAGE_QTY), 0) AS QTY, "
//                + " E.UNIT_CHN_DESC, A.OWN_PRICE, "
//                + " SUM (A.OWN_PRICE * A.RTN_DOSAGE_QTY) "
//                + " - NVL(SUM (A.OWN_PRICE * A.CANCEL_DOSAGE_QTY), 0) AS AMT, "
//                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
//                + " FROM ODI_DSPNM A, SYS_FEE C, PHA_TRANSUNIT D, "
//                + " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
//                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
//                + " AND A.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.RTN_DOSAGE_UNIT = E.UNIT_CODE "
//                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.MR_NO = G.MR_NO "
//                + " AND A.PHA_RETN_DATE IS NOT NULL "
//                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
//                + " AND A.PHA_RETN_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//                order_code_sql
//                + " GROUP BY A.PHA_RETN_DATE, C.ORDER_DESC, C.SPECIFICATION, "
//                + " E.UNIT_CHN_DESC, A.OWN_PRICE, F.DEPT_CHN_DESC, A.MR_NO, "
//                + " G.PAT_NAME, A.CASE_NO ";
//        }
//        // ���ⲿ��
//        if ("Y".equals(qty_out)) {
//            if (!"".equals(sql_in))
//                sql_out = sql_out + " UNION ";
//            sql_out = sql_out
//                // ��ҩ�������,���ұ�ҩ����,���Ĳĳ��ⲿ��
//                + " SELECT A.DISPENSE_DATE AS CHECK_DATE, "
//                + " A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION, SUM (B.ACTUAL_QTY) AS QTY,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE AS OWN_PRICE, "
//                + " SUM (B.RETAIL_PRICE * B.ACTUAL_QTY) AS AMT, "
//                + " F.DEPT_CHN_DESC, '' AS MR_NO, '' AS PAT_NAME, "
//                + " '' AS CASE_NO "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.TO_ORG_CODE = F.DEPT_CODE "
//                + " AND A.REQTYPE_CODE IN ('EXM', 'TEC', 'COS') "
//                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.TO_ORG_CODE = '" + org_code + "' " +
//                order_code_sql + " AND A.UPDATE_FLG <> '2' "
//                + " GROUP BY A.DISPENSE_DATE, C.ORDER_DESC, "
//                + " C.SPECIFICATION, E.UNIT_CHN_DESC, B.RETAIL_PRICE, "
//                + " F.DEPT_CHN_DESC, A.REQTYPE_CODE "
//                + " UNION "
//                // ��������,��ĳ���,��������,�˿���ⲿ��
//                + " SELECT A.DISPENSE_DATE AS CHECK_DATE, "
//                + " A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION, SUM (B.ACTUAL_QTY) AS QTY,"
//                + " E.UNIT_CHN_DESC, B.RETAIL_PRICE AS OWN_PRICE, "
//                + " SUM (B.RETAIL_PRICE * B.ACTUAL_QTY) AS AMT, "
//                + " F.DEPT_CHN_DESC, '' AS MR_NO, '' AS PAT_NAME, "
//                + " '' AS CASE_NO "
//                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
//                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
//                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
//                + " AND B.ORDER_CODE = C.ORDER_CODE "
//                + " AND B.ORDER_CODE = D.ORDER_CODE "
//                + " AND B.UNIT_CODE = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.APP_ORG_CODE = F.DEPT_CODE "
//                + " AND A.REQTYPE_CODE IN ('GIF', 'THO', 'WAS' , 'RET') "
//                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.APP_ORG_CODE = '" + org_code + "' " +
//                order_code_sql + " AND A.UPDATE_FLG <> '2' "
//                + " GROUP BY A.DISPENSE_DATE, C.ORDER_DESC, "
//                + " C.SPECIFICATION, E.UNIT_CHN_DESC, B.RETAIL_PRICE, "
//                + " F.DEPT_CHN_DESC, A.REQTYPE_CODE "
//                + " UNION "
//                // �ż��﷢ҩ���ⲿ��
//                + " SELECT A.PHA_DOSAGE_DATE,A.ADM_TYPE||'_DPN' AS STATUS,"
//                + " C.ORDER_DESC,C.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,"
//                + " E.UNIT_CHN_DESC, A.OWN_PRICE, "
//                + " SUM (A.OWN_PRICE * A.DOSAGE_QTY) AS AMT, "
//                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
//                + " FROM OPD_ORDER A,SYS_FEE C,PHA_TRANSUNIT D,SYS_UNIT E,"
//                + " SYS_DEPT F, SYS_PATINFO G "
//                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
//                + " AND A.ORDER_CODE = D.ORDER_CODE "
//                + " AND D.DOSAGE_UNIT = E.UNIT_CODE "
//                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.MR_NO = G.MR_NO "
//                + " AND A.PHA_DOSAGE_DATE IS NOT NULL "
//                + " AND A.PHA_RETN_CODE IS NULL "
//                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
//                + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//                order_code_sql
//                + " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC, A.OWN_PRICE, F.DEPT_CHN_DESC,"
//                + " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE "
//                + " UNION "
//                // סԺ��ҩ���ⲿ��
//                + " SELECT A.PHA_DOSAGE_DATE, 'I_DPN' AS STATUS, "
//                + " C.ORDER_DESC, C.SPECIFICATION, SUM(A.DOSAGE_QTY) "
//                + " AS QTY, E.UNIT_CHN_DESC, A.OWN_PRICE, "
//                + " SUM (A.OWN_PRICE * A.DOSAGE_QTY) AS AMT, "
//                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
//                + " FROM ODI_DSPNM A, SYS_FEE C, PHA_TRANSUNIT D, "
//                + " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
//                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
//                + " AND A.ORDER_CODE = D.ORDER_CODE "
//                + " AND D.DOSAGE_UNIT = E.UNIT_CODE "
//                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.MR_NO = G.MR_NO "
//                + " AND A.PHA_DOSAGE_DATE IS NOT NULL "
//                + " AND A.PHA_RETN_DATE IS NULL "
//                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
//                + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
//                order_code_sql
//                + " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
//                + " E.UNIT_CHN_DESC,A.OWN_PRICE,F.DEPT_CHN_DESC,A.MR_NO,"
//                + " G.PAT_NAME, A.CASE_NO ";
//        }
//        // �̵㲿��
//        if ("Y".equals(qty_check)) {
//            if (!"".equals(sql_out) || !"".equals(qty_in))
//                sql_check = sql_check + " UNION ";
//            // �̵���ҵ
//            sql_check = sql_check
//                + " SELECT TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
//                + " AS CHECK_DATE, 'FRO' AS STATUS, C.ORDER_DESC, "
//                + " C.SPECIFICATION, SUM (A.MODI_QTY) AS QTY, "
//                + " E.UNIT_CHN_DESC, A.RETAIL_PRICE AS OWN_PRICE, "
//                + " SUM (A.RETAIL_PRICE * A.MODI_QTY) AS AMT, "
//                + " F.ORG_CHN_DESC, "
//                + " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
//                + " FROM IND_QTYCHECK A, SYS_FEE C, PHA_TRANSUNIT D, "
//                + " SYS_UNIT E, IND_ORG F "
//                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
//                + " AND A.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.DOSAGE_UNIT = E.UNIT_CODE "
//                + " AND C.ORDER_CODE = D.ORDER_CODE "
//                + " AND A.ORG_CODE = F.ORG_CODE "
//                + " AND TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
//                + " BETWEEN TO_DATE('" + start_date +
//                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
//                "','YYYYMMDDHH24MISS') "
//                + " AND A.UNFREEZE_DATE IS NOT NULL "
//                + " AND A.MODI_QTY <> 0 "
//                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
//                + " GROUP BY A.FROZEN_DATE,C.ORDER_DESC,C.SPECIFICATION, "
//                + " E.UNIT_CHN_DESC, A.RETAIL_PRICE, F.ORG_CHN_DESC ";
//        }
//        String sql = sql_in + sql_out + sql_check;
//        return sql;
        String order_code_sql = "";
        // ��ҩƷ��ѯ
        if (!"".equals(order_code)) {
            order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
        }
        String sql_in = "";
        String sql_out = "";
        String sql_check = "";
        // ��ⲿ��
        if ("Y".equals(qty_in)) {
            // �������,������ⲿ��
            sql_in += " SELECT A.WAREHOUSING_DATE AS CHECK_DATE, "
                + "  A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, C.SPECIFICATION, "
                + " SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
                + " B.VERIFYIN_PRICE AS OWN_PRICE, SUM (B.VERIFYIN_PRICE * "
                + " B.ACTUAL_QTY) AS AMT, F.DEPT_CHN_DESC, "
                + " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.APP_ORG_CODE = F.DEPT_CODE "
                + " AND A.REQTYPE_CODE IN ('DEP','THI') "
                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.WAREHOUSING_DATE IS NOT NULL "
                + " AND A.UPDATE_FLG <> '2' "
                + " AND A.APP_ORG_CODE = '" + org_code + "' " + order_code_sql
                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.DEPT_CHN_DESC, "
                + " A.REQTYPE_CODE "
                + " UNION "
                // ��ҩ���,������ⲿ��
                + " SELECT A.WAREHOUSING_DATE, A.REQTYPE_CODE AS STATUS, "
                + " C.ORDER_DESC, "
                + " C.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,E.UNIT_CHN_DESC, "
                + " B.VERIFYIN_PRICE AS OWN_PRICE, SUM (B.VERIFYIN_PRICE * "
                + " B.ACTUAL_QTY) AS AMT, F.DEPT_CHN_DESC, "
                + " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.TO_ORG_CODE = F.DEPT_CODE "
                + " AND A.REQTYPE_CODE IN ('RET', 'GIF') "
                + " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.WAREHOUSING_DATE IS NOT NULL "
                + " AND A.UPDATE_FLG <> '2' "
                + " AND A.TO_ORG_CODE = '" + org_code + "' " + order_code_sql
                + " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.DEPT_CHN_DESC, "
                + " A.REQTYPE_CODE "
                + " UNION "
                // �ż�����ҩ��ⲿ��
                + " SELECT A.PHA_RETN_DATE , A.ADM_TYPE||'_RET' AS STATUS, "
                + " C.ORDER_DESC, C.SPECIFICATION, SUM (A.DOSAGE_QTY) AS QTY, "
                + " E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY2), "
                + " SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY2) * A.DOSAGE_QTY) AS AMT, "
                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
                + " FROM OPD_ORDER A,SYS_FEE C,PHA_TRANSUNIT D,SYS_UNIT E, "
                + " SYS_DEPT F, SYS_PATINFO G "
                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND D.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.MR_NO = G.MR_NO "
                + " AND A.PHA_RETN_DATE IS NOT NULL "
                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
                + " AND A.PHA_RETN_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
                order_code_sql
                + " GROUP BY A.PHA_RETN_DATE, C.ORDER_DESC, C.SPECIFICATION, "
                + " E.UNIT_CHN_DESC,F.DEPT_CHN_DESC,"
                + " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3 "
                + " UNION "
                // סԺ��ҩ��ⲿ��--�޸� By liyh 20120815 ����Ϊ0 ��
                + " SELECT A.PHA_RETN_DATE, 'I_RET' AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION, SUM (A.RTN_DOSAGE_QTY) -  "
                + " NVL (SUM (A.CANCEL_DOSAGE_QTY), 0) AS QTY, "
                + " E.UNIT_CHN_DESC, A.OWN_PRICE AS OWN_PRICE, "
                + " SUM(A.OWN_AMT) AS AMT, "
                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
                + " FROM ODI_DSPNM A, SYS_FEE C, PHA_TRANSUNIT D, "
                + " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND A.RTN_DOSAGE_UNIT = E.UNIT_CODE "
                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.MR_NO = G.MR_NO "
                + " AND A.PHA_RETN_DATE IS NOT NULL "
                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
                + " AND A.PHA_RETN_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
                order_code_sql
                + " GROUP BY A.PHA_RETN_DATE, C.ORDER_DESC, C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, A.OWN_PRICE, F.DEPT_CHN_DESC, A.MR_NO, "
                + " G.PAT_NAME, A.CASE_NO,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3 ";
        }
        // ���ⲿ��
        if ("Y".equals(qty_out)) {
            if (!"".equals(sql_in))
                sql_out = sql_out + " UNION ";
            sql_out = sql_out
                // ��ҩ�������,���ұ�ҩ����,���Ĳĳ��ⲿ��
                + " SELECT A.DISPENSE_DATE AS CHECK_DATE, "
                + " A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION, SUM (B.ACTUAL_QTY) AS QTY,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE AS OWN_PRICE, "
                + " SUM (VERIFYIN_PRICE * B.ACTUAL_QTY) AS AMT, "
                + " F.DEPT_CHN_DESC, '' AS MR_NO, '' AS PAT_NAME, "
                + " '' AS CASE_NO "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.TO_ORG_CODE = F.DEPT_CODE "
                + " AND A.REQTYPE_CODE IN ('EXM', 'TEC', 'COS') "
                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.TO_ORG_CODE = '" + org_code + "' " +
                order_code_sql + " AND A.UPDATE_FLG <> '2' "
                + " GROUP BY A.DISPENSE_DATE, C.ORDER_DESC, "
                + " C.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, "
                + " F.DEPT_CHN_DESC, A.REQTYPE_CODE "
                + " UNION "
                // ��������,��ĳ���,��������,�˿���ⲿ��
                + " SELECT A.DISPENSE_DATE AS CHECK_DATE, "
                + " A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION, SUM (B.ACTUAL_QTY) AS QTY,"
                + " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE AS OWN_PRICE, "
                + " SUM (B.VERIFYIN_PRICE * B.ACTUAL_QTY) AS AMT, "
                + " F.DEPT_CHN_DESC, '' AS MR_NO, '' AS PAT_NAME, "
                + " '' AS CASE_NO "
                + " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, "
                + " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F "
                + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
                + " AND B.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = D.ORDER_CODE "
                + " AND B.UNIT_CODE = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.APP_ORG_CODE = F.DEPT_CODE "
                + " AND A.REQTYPE_CODE IN ('GIF', 'THO', 'WAS' , 'RET') "
                + " AND A.DISPENSE_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.APP_ORG_CODE = '" + org_code + "' " +
                order_code_sql + " AND A.UPDATE_FLG <> '2' "
                + " GROUP BY A.DISPENSE_DATE, C.ORDER_DESC, "
                + " C.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, "
                + " F.DEPT_CHN_DESC, A.REQTYPE_CODE "
                + " UNION "
                // �ż��﷢ҩ���ⲿ��
                + " SELECT A.PHA_DOSAGE_DATE,A.ADM_TYPE||'_DPN' AS STATUS,"
                + " C.ORDER_DESC,C.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,"
                + " E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY3) AS OWN_PRICE, "
                + " SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY2) * A.DOSAGE_QTY) AS AMT, "
                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
                + " FROM OPD_ORDER A,SYS_FEE C,PHA_TRANSUNIT D,SYS_UNIT E,"
                + " SYS_DEPT F, SYS_PATINFO G "
                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND D.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.MR_NO = G.MR_NO "
                + " AND A.PHA_DOSAGE_DATE IS NOT NULL "
                + " AND A.PHA_RETN_CODE IS NULL "
                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
                + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
                order_code_sql
                + " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC, F.DEPT_CHN_DESC,"
                + " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3 "
                + " UNION "
                // סԺ��ҩ���ⲿ��
                + " SELECT A.PHA_DOSAGE_DATE, 'I_DPN' AS STATUS, "
                + " C.ORDER_DESC, C.SPECIFICATION, SUM(A.DOSAGE_QTY) "
                + " AS QTY, E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY3) AS OWN_PRICE, "
                + " SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY3)* A.DOSAGE_QTY) AS AMT, "
                + " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
                + " FROM ODI_DSPNM A, SYS_FEE C, PHA_TRANSUNIT D, "
                + " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND D.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.MR_NO = G.MR_NO "
                + " AND A.PHA_DOSAGE_DATE IS NOT NULL "
                + " AND A.PHA_RETN_DATE IS NULL "
                + " AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C', 'PHA_G') "
                + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
                order_code_sql
                + " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
                + " E.UNIT_CHN_DESC,A.OWN_PRICE,F.DEPT_CHN_DESC,A.MR_NO,"
                + " G.PAT_NAME, A.CASE_NO,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3 ";
        }
        // �̵㲿��
        if ("Y".equals(qty_check)) {
            if (!"".equals(sql_out) || !"".equals(qty_in))
                sql_check = sql_check + " UNION ";
            // �̵���ҵ
            sql_check = sql_check
                + " SELECT TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
                + " AS CHECK_DATE, 'FRO' AS STATUS, C.ORDER_DESC, "
                + " C.SPECIFICATION, SUM (A.MODI_QTY) AS QTY, "
                + " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE AS OWN_PRICE, "
                + " SUM (A.VERIFYIN_PRICE * A.MODI_QTY) AS AMT, "
                + " F.ORG_CHN_DESC, "
                + " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
                + " FROM IND_QTYCHECK A, SYS_FEE C, PHA_TRANSUNIT D, "
                + " SYS_UNIT E, IND_ORG F "
                + " WHERE A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND A.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND C.ORDER_CODE = D.ORDER_CODE "
                + " AND A.ORG_CODE = F.ORG_CODE "
                + " AND TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
                + " BETWEEN TO_DATE('" + start_date +
                "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS') "
                + " AND A.UNFREEZE_DATE IS NOT NULL "
                + " AND A.MODI_QTY <> 0 "
                + " AND A.ORG_CODE = '" + org_code + "' " + order_code_sql
                + " GROUP BY A.FROZEN_DATE,C.ORDER_DESC,C.SPECIFICATION, "
                + " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE, F.ORG_CHN_DESC ";
        }
        String sql = sql_in + sql_out + sql_check;
//        System.out.println("��ϸ�˲�ѯsql:"+sql);
        return sql;
      //************************************************
      //luhai modify 2012-1-24 �����ۼ۸�ĳɲɹ��۸�end
        //************************************************
    }

    /**
     * ������������ҩ��ִ��SQL
     * @return String
     */
    public static String getUpdateReduceIndStockSql(String org_code,
        String order_code, int batch_seq, double out_qty, double out_amt,
        String opt_user, String opt_date, String opt_term) {
        return "UPDATE IND_STOCK SET OUT_QTY=OUT_QTY+" + out_qty
            + ", OUT_AMT=OUT_AMT+" + out_amt + ", STOCK_QTY=STOCK_QTY-" +
            out_qty + ", DOSEAGE_QTY=DOSEAGE_QTY+" + out_qty
            + ", DOSAGE_AMT=DOSAGE_AMT+" + out_amt
            + ", OPT_USER='" + opt_user + "', OPT_DATE=SYSDATE, OPT_TERM='"
            + opt_term + "' WHERE ORG_CODE='" + org_code +
            "' AND ORDER_CODE='" + order_code +
            "' AND BATCH_SEQ = " + batch_seq;
    }
    /**
     * ����ҩƷorderCode batchSeq �õ�ҩƷ�Ŀ�浥λ�ɹ���
     * @return
     */
    public static String getIndVerifyInPrice(String orderCode,int batchSeq){
    	StringBuffer sqlbf = new StringBuffer ();
    	sqlbf.append(" SELECT  A.VERIFYIN_PRICE/B.PURCH_QTY AS VERIFYIN_PRICE ");
    	sqlbf.append(" FROM  IND_VERIFYIND A,PHA_TRANSUNIT B  ");
    	sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE ");
    	sqlbf.append(" AND A.ORDER_CODE='"+orderCode+"' ");
    	sqlbf.append(" AND A.BATCH_SEQ="+batchSeq+" ");
    	return sqlbf.toString();
    }
    
    /**
     * �õ�ҩƷ�Ŀ�浥λ�ɹ���
     * @param orgCode
     * @param orderCode
     * @param batchSeq
     * @return
     * @author liyh
     * @date 20120801
     */
    public static String getIndVerifyInPrice(String orgCode,String orderCode,int batchSeq){
    	String sqlbf = " SELECT  A.VERIFYIN_PRICE*B.DOSAGE_QTY AS VERIFYIN_PRICE " +
    	 			   " FROM    IND_STOCK A, PHA_TRANSUNIT B  " + 
    	               " WHERE   A.ORDER_CODE=B.ORDER_CODE  AND A.ORG_CODE='" + orderCode +"' AND " +
    	               "         A.ORDER_CODE='" + orderCode + "'  AND A.BATCH_SEQ=" + batchSeq + " ";
    	return sqlbf;
    }
    
    //����
    /**
     * ҽԺҩƷ����ͳ�Ʋ�ѯ����SQL
     * @param orgCode ���ű���  �����סԺ������Ƽۣ�
     * @param deptCode ���ұ���
     * @param startDate ��ʼʱ��
     * @param endDate   ��ֹ����
     * @param orderCode ҩƷ����
     * @param antibioticCode �����ر���
     * @param ctrldrugclassCode ����ҩƷ���루����ҩ��
     * @param sysGrugCalss ҩ�����ࣨ���Ҳ���....��
     * @param typeCode ҩƷ����
     * @parm  dspnKind   סԺҩƷ������ʽ( ST ��ʱ��UD���ڡ�DS ��Ժ��ҩ��RT ��ҩ)
     * @parm  inspayType  �������A��ҽ�� B������C���Էѣ�
     * @parm  dsDateFlag  �Ƿ��Ժ��0��ȫ����1����Ժ��2����Ժ��
     * @return
     */
    public static String getStatisticsSQLM(String orgCode,String deptCode ,String startDate ,String endDate , String orderCode, String 	antibioticCode,String ctrldrugclassCode,String sysGrugCalss,
            String typeCode,String dspnKind,String inspayType, String dsDateFlag){
				//ִ�п��Ҳ�ѯ����0���ż��� 1������Ƽۣ�2 סԺ
				String[] deptCodeArr = getDeptCodeArr(deptCode);
				//�õ�ҩƷ��ѯ����
				String[] orderCodeArr = getOrderCodeArr(orderCode);
				//�õ�������ҩƷ��ѯ����
				String[] antibioticCodeArr = getAntibioticCode(antibioticCode);
				//�õ�����ҩƷ��ѯ����
				String[] ctrldrugclassCodeArr = getCtrldrugclassCode(ctrldrugclassCode);
				//�õ� ҩ������ ��ѯ����
				String[] sysGrugClassArr = getSysGrugClass(sysGrugCalss);
				//�õ�  ҩƷ����  ��ѯ����
				String[] typeCodeArr  = getTypeCode(typeCode);
				//�õ� סԺҩƷ������ʽ  ��ѯ����
				String   dspnKindStr =  getDspnKind(dspnKind);
				//�õ� �������  ��ѯ����
				String[] inspayTypeArr =  getInspayType(inspayType);
				//�õ�  �Ƿ��Ժ  ��ѯ����
				String   dsDateStr  = getDsDate(dsDateFlag);
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT abc.region_chn_abn as REGION_CHN_DESC,abc.exec_dept_code,abc.dept_chn_desc,abc.order_code,abc.order_desc,abc.specification,abc.own_price,abc.unit_chn_desc,sum(abc.tot_qty) as sm_qty,sum(abc.tot_amt) as sum_amt,'' as udd_numb  ")
					.append(" from ")
					.append(" ( ");
				if("".equals(orgCode))
				{
				
					/***************************�ż���ҩƷ����ͳ��****************************/
				sb.append("  SELECT e.region_chn_abn,a.dept_code as exec_dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc , b.SPECIFICATION , a.own_price , D.UNIT_CHN_DESC ,sum(a.dosage_qty) AS tot_qty,sum(a.own_amt) AS tot_amt ")
					.append("  FROM opd_order a,pha_base b,SYS_DEPT C,SYS_UNIT D,sys_region e ,sys_fee f ")
					.append("  where a.order_code=b.order_code  AND  A.Dept_Code=C.DEPT_CODE  and  a.region_code=e.region_code ")
					.append("  AND  A.DISPENSE_UNIT=D.UNIT_CODE and  a.order_code=f.order_code  and  a.adm_type in('O','E')  ")
					.append(deptCodeArr[0])
					.append(orderCodeArr[0])
					.append(antibioticCodeArr[0])
					.append(ctrldrugclassCodeArr[0])
					.append(sysGrugClassArr[0])
					.append(typeCodeArr[0])
					.append(inspayTypeArr[0])
					.append("  and  a.order_cat1_code in ('PHA_C','PHA_W')  ")
					.append("  and  a.bill_date between TO_DATE ('").append(startDate).append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('").append(endDate).append("', 'yyyymmdd hh24:mi:ss')  " )
					.append("  group by e.region_chn_abn,a.dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc, b.SPECIFICATION,a.own_price, D.UNIT_CHN_DESC   ")
				
				/***************************����Ʒ�ҩƷ����ͳ��************************/
					.append("  union all  ")
					.append("   select g.region_chn_abn AS region_chn_abn,a.dept_code as exec_dept_code,d.dept_chn_desc,a.order_code, b.ORDER_DESC , b.SPECIFICATION ,a.OWN_PRICE,c.unit_chn_desc,  sum(a.dosage_qty) AS tot_qty, SUM (tot_amt) AS tot_amt  ")     
					.append("   from ibs_ordd a,pha_base b ,sys_unit c,sys_dept d ,sys_fee e ,ibs_ordm f,sys_region g ")
					.append("	  where a.order_cat1_code in ('PHA_C','PHA_W') and  a.order_code=b.order_code and  a.dosage_unit=c.unit_code and a.dept_code=d.dept_code and a.order_code=e.order_code and a.case_no=f.case_no  ")
					.append("   and a.case_no_seq=f.case_no_seq and f.region_code=g.region_code ")
					.append(deptCodeArr[1])
					.append(orderCodeArr[1])
					.append(antibioticCodeArr[1])
					.append(ctrldrugclassCodeArr[1])
					.append(sysGrugClassArr[1])
					.append(typeCodeArr[1])
					.append(inspayTypeArr[1])
					.append("  and  a.bill_date between TO_DATE ('").append(startDate).append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('").append(endDate).append("', 'yyyymmdd hh24:mi:ss')  " )
					.append("   group by g.region_chn_abn,d.dept_chn_desc,a.dept_code,a.order_code, b.ORDER_DESC, b.SPECIFICATION ,a.OWN_PRICE ,c.unit_chn_desc ")
				
				/***************************סԺҩƷ����ͳ��*************************/
					.append("  union all  ")
					.append("  select e.region_chn_abn,a.dept_code as exec_dept_code,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc,sum(a.dosage_qty) as tot_qty,sum(a.own_amt) as tot_amt  ")                 
					.append("  from odi_dspnm a,sys_dept b, pha_base c,sys_unit d,sys_region e ,sys_fee f,adm_inp g ")
					.append("  where a.dept_code=b.dept_code and   a.order_code=c.order_code and   a.dosage_unit=d.unit_code and   a.region_code=e.region_code  ")
					.append("  and   a.order_code=f.order_code and   a.case_no=g.case_no  ")
					.append(deptCodeArr[2])
					.append(orderCodeArr[2])
					.append(antibioticCodeArr[2])
					.append(ctrldrugclassCodeArr[2])
					.append(sysGrugClassArr[2])
					.append(typeCodeArr[2])
					.append(inspayTypeArr[2])
					.append(dspnKindStr)
					.append(dsDateStr)
					.append("  and  a.dspn_date between TO_DATE ('").append(startDate).append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('").append(endDate).append("', 'yyyymmdd hh24:mi:ss')  " )
					.append("  group by e.region_chn_abn,a.dept_code ,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc ");
				
				}else
				{
					if("040103".equals(orgCode))
					{
						sb.append(getOrgCodeB(orgCode, deptCodeArr, orderCodeArr, antibioticCodeArr, ctrldrugclassCodeArr, sysGrugClassArr, typeCodeArr, inspayTypeArr, startDate, endDate))
						  .append("  union all  ")
						  .append(getOrgCodeZ(orgCode, deptCodeArr, orderCodeArr, antibioticCodeArr, ctrldrugclassCodeArr, sysGrugClassArr, typeCodeArr, inspayTypeArr, startDate, endDate, dspnKindStr, dsDateStr));
						  					  
					}
					if("040104".equals(orgCode) || "040102".equals(orgCode))
					{
						sb.append(getOrgCodeM(orgCode, deptCodeArr, orderCodeArr, antibioticCodeArr, ctrldrugclassCodeArr, sysGrugClassArr, typeCodeArr, inspayTypeArr, startDate, endDate));
					}
				}
				sb.append(" ) abc ")
					.append(" group by abc.region_chn_abn,abc.exec_dept_code ,abc.dept_chn_desc,abc.order_code,abc.order_desc,abc.specification,abc.own_price,abc.unit_chn_desc " )
						.append(" order  by abc.region_chn_abn,abc.exec_dept_code ,abc.dept_chn_desc,abc.order_code,abc.order_desc,abc.specification,abc.own_price,abc.unit_chn_desc ");
				
				
				
			return sb.toString();
}
    /**
     * סԺҩ����ѯ����
     * @param orgCode ���ű���  �����סԺ������Ƽۣ�
     * @param deptCode ���ұ���
     * @param startDate ��ʼʱ��
     * @param endDate   ��ֹ����
     * @param orderCode ҩƷ����
     * @param antibioticCode �����ر���
     * @param ctrldrugclassCode ����ҩƷ���루����ҩ��
     * @param sysGrugCalss ҩ�����ࣨ���Ҳ���....��
     * @param typeCode ҩƷ����
     * @parm  dspnKind   סԺҩƷ������ʽ( ST ��ʱ��UD���ڡ�DS ��Ժ��ҩ��RT ��ҩ)
     * @parm  inspayType  �������A��ҽ�� B������C���Էѣ�
     * @parm  dsDateFlag  �Ƿ��Ժ��0��ȫ����1����Ժ��2����Ժ��     * 
     */
    private static String getOrgCodeZ(String orgCode,String[] deptCodeArr,String[] orderCodeArr,String[] antibioticCodeArr,String[] ctrldrugclassCodeArr,
    		String[] sysGrugClassArr,String[] typeCodeArr,String[] inspayTypeArr,String startDate,String endDate,String dspnKindStr,String dsDateStr)
    {
    	StringBuffer sb = new StringBuffer();
    	/***************************סԺҩƷ����ͳ��*************************/
		sb.append("  select e.region_chn_abn,a.dept_code as exec_dept_code,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc,sum(a.dosage_qty) as tot_qty,sum(a.own_amt) as tot_amt  ")                 
		.append("  from odi_dspnm a,sys_dept b, pha_base c,sys_unit d,sys_region e ,sys_fee f,adm_inp g ")
		.append("  where a.dept_code=b.dept_code and   a.order_code=c.order_code and   a.dosage_unit=d.unit_code and   a.region_code=e.region_code  ")
		.append("  and   a.order_code=f.order_code and   a.case_no=g.case_no  ")
		.append(deptCodeArr[2])
		.append(orderCodeArr[2])
		.append(antibioticCodeArr[2])
		.append(ctrldrugclassCodeArr[2])
		.append(sysGrugClassArr[2])
		.append(typeCodeArr[2])
		.append(inspayTypeArr[2])
		.append(dspnKindStr)
		.append(dsDateStr)
		.append("  and  a.dspn_date between TO_DATE ('").append(startDate).append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('").append(endDate).append("', 'yyyymmdd hh24:mi:ss')  " )
		.append("  group by e.region_chn_abn,a.dept_code ,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc ");
	
		return sb.toString();
    	
    }
    
    /**
     * ����Ƽ۲�ѯ����
     * @param orgCode ���ű���  �����סԺ������Ƽۣ�
     * @param deptCode ���ұ���
     * @param startDate ��ʼʱ��
     * @param endDate   ��ֹ����
     * @param orderCode ҩƷ����
     * @param antibioticCode �����ر���
     * @param ctrldrugclassCode ����ҩƷ���루����ҩ��
     * @param sysGrugCalss ҩ�����ࣨ���Ҳ���....��
     * @param typeCode ҩƷ����
     * @parm  dspnKind   סԺҩƷ������ʽ( ST ��ʱ��UD���ڡ�DS ��Ժ��ҩ��RT ��ҩ)
     * @parm  inspayType  �������A��ҽ�� B������C���Էѣ�
     * @parm  dsDateFlag  �Ƿ��Ժ��0��ȫ����1����Ժ��2����Ժ��     * 
     */
    private static String getOrgCodeB(String orgCode,String[] deptCodeArr,String[] orderCodeArr,String[] antibioticCodeArr,String[] ctrldrugclassCodeArr,
    		String[] sysGrugClassArr,String[] typeCodeArr,String[] inspayTypeArr,String startDate,String endDate)
    {
    	StringBuffer sb = new StringBuffer();
    		  /***************************����Ʒ�ҩƷ����ͳ��************************/
      	  sb.append("   select g.region_chn_abn AS region_chn_abn,a.dept_code as exec_dept_code,d.dept_chn_desc,a.order_code, b.ORDER_DESC , b.SPECIFICATION ,a.OWN_PRICE,c.unit_chn_desc,  sum(a.dosage_qty) AS tot_qty, SUM (tot_amt) AS tot_amt  ")     
      	  .append("   from ibs_ordd a,pha_base b ,sys_unit c,sys_dept d ,sys_fee e ,ibs_ordm f,sys_region g ")
      	  .append("	  where a.order_cat1_code in ('PHA_C','PHA_W') and  a.order_code=b.order_code and  a.dosage_unit=c.unit_code and a.dept_code=d.dept_code and a.order_code=e.order_code and a.case_no=f.case_no  ")
      	  .append("   and a.case_no_seq=f.case_no_seq and f.region_code=g.region_code ")
      	  .append(deptCodeArr[1])
      	  .append(orderCodeArr[1])
      	  .append(antibioticCodeArr[1])
      	  .append(ctrldrugclassCodeArr[1])
      	  .append(sysGrugClassArr[1])
      	  .append(typeCodeArr[1])
      	  .append(inspayTypeArr[1])
      	  .append("  and  a.bill_date between TO_DATE ('").append(startDate).append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('").append(endDate).append("', 'yyyymmdd hh24:mi:ss')  " )
      	  .append("   group by g.region_chn_abn,d.dept_chn_desc,a.dept_code,a.order_code, b.ORDER_DESC, b.SPECIFICATION ,a.OWN_PRICE ,c.unit_chn_desc ");

		return sb.toString();
    	
    }
    
    
    /**
     * �ż���ҩ����ѯ����
     * @param orgCode ���ű���  �����סԺ������Ƽۣ�
     * @param deptCode ���ұ���
     * @param startDate ��ʼʱ��
     * @param endDate   ��ֹ����
     * @param orderCode ҩƷ����
     * @param antibioticCode �����ر���
     * @param ctrldrugclassCode ����ҩƷ���루����ҩ��
     * @param sysGrugCalss ҩ�����ࣨ���Ҳ���....��
     * @param typeCode ҩƷ����
     * @parm  dspnKind   סԺҩƷ������ʽ( ST ��ʱ��UD���ڡ�DS ��Ժ��ҩ��RT ��ҩ)
     * @parm  inspayType  �������A��ҽ�� B������C���Էѣ�
     * @parm  dsDateFlag  �Ƿ��Ժ��0��ȫ����1����Ժ��2����Ժ��
     */
    private static String getOrgCodeM(String orgCode,String[] deptCodeArr,String[] orderCodeArr,String[] antibioticCodeArr,String[] ctrldrugclassCodeArr,
    		String[] sysGrugClassArr,String[] typeCodeArr,String[] inspayTypeArr,String startDate,String endDate)
    {
    	StringBuffer sb = new StringBuffer();
    		/***************************�ż���ҩƷ����ͳ��****************************/
    	  	  sb.append("  SELECT e.region_chn_abn,a.dept_code as exec_dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc , b.SPECIFICATION , a.own_price , D.UNIT_CHN_DESC ,sum(a.dosage_qty) AS tot_qty,sum(a.own_amt) AS tot_amt ")
    	  	  .append("  FROM opd_order a,pha_base b,SYS_DEPT C,SYS_UNIT D,sys_region e ,sys_fee f,reg_patadm g")
    	  	  .append("  where a.order_code=b.order_code  AND  A.Dept_Code=C.DEPT_CODE  and  a.region_code=e.region_code ")
    	  	  .append("  AND  A.DISPENSE_UNIT=D.UNIT_CODE and  a.order_code=f.order_code  AND  a.case_no = g.case_no  ");
    	  	  if("040102".equals(orgCode))
    	  	  {
    	  		sb.append("  and a.adm_type in('O','E') and g.realdept_code != '020103'  "); 
    	  	  }else if("040104".equals(orgCode))
    	  	  {
    	  		  // and a.adm_type='O'
    	  		  sb.append("  and g.realdept_code = '020103' ");
    	  	  }
    	  	  sb.append(deptCodeArr[0])
    	  	  .append(orderCodeArr[0])
    	  	  .append(antibioticCodeArr[0])
    	  	  .append(ctrldrugclassCodeArr[0])
    	  	  .append(sysGrugClassArr[0])
    	  	  .append(typeCodeArr[0])
    	  	  .append(inspayTypeArr[0])
    	  	  .append("  and  a.order_cat1_code in ('PHA_C','PHA_W')  ")
    	  	  .append("  and  a.bill_date between TO_DATE ('").append(startDate).append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('").append(endDate).append("', 'yyyymmdd hh24:mi:ss')  " )
    	  	  .append("  group by e.region_chn_abn,a.dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc, b.SPECIFICATION,a.own_price, D.UNIT_CHN_DESC  ");

    	return sb.toString();
    }
  
    //ϸ��    
    /**
     * ҽԺҩƷ����ͳ�Ʋ�ѯ��ϸ��SQL
     * @param deptCode  ���ű���
     * @param orderCode ҩƷ����
     * @param startDate ��ʼʱ��
     * @param endDate   ����ʱ��
     * @param endDate2 
     * @param startDate2 
     * @param smQty 
     * @param specification 
     * @param endDate 
     * @param startDate 
     * @param smQty2 
     * @return
     */
    public static String getStatisticsSQLD(String deptCode, String startDate, String endDate){	
    	StringBuffer sb = new StringBuffer();
    	sb.append(" SELECT to_char(A.OPT_DATE,'yyyy-MM-dd') as OPT_DATE,G.ORDER_DESC,F.MR_NO,B.PAT_NAME,C.SPECIFICATION AS  SPECIFICATION,  ")  // LIRUI 20120613    ��ҩƷ����
    	.append(" D.UNIT_CHN_DESC AS UNIT_CHN_DESC,G.RETAIL_PRICE AS RETAIL_PRICE,CASE   M.RX_KIND  WHEN 'ST' THEN '��ʱ' WHEN 'UD' THEN '����' WHEN 'DS' THEN '��Ժ��ҩ' ELSE '��ҩ' END   AS DSPN_KIND, ")
    	.append(" E.FREQ_CHN_DESC AS  FREQ_CHN_DESC,A.DOSAGE_QTY as DOSAGE_QTY , a.TOT_AMT AS TOT_AMT,A.OWN_AMT,'' AS UDD_NUMB,  H.USER_NAME  ")    // LIRUI 20120613    ȥ���˲���id
    	  //.append("        CASE   M.RX_KIND  WHEN 'ST' THEN '��ʱ' WHEN 'UD' THEN '����' WHEN 'DS' THEN '��Ժ��ҩ' ELSE '��ҩ' END   AS DSPN_KIND, E.FREQ_CHN_DESC AS  FREQ_CHN_DESC, ")
    	 // .append("  	   A.DOSAGE_QTY as DOSAGE_QTY , a.TOT_AMT AS TOT_AMT,A.OWN_AMT,G.UDD_NUMB,  H.USER_NAME ")
    	  .append(" FROM IBS_ORDD A, SYS_PATINFO B, SYS_FEE C,  SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M  ,PHA_BASE G,SYS_OPERATOR H  ")
    	  .append(" WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE AND C.UNIT_CODE = D.UNIT_CODE ")
    	  .append("       AND A.ORDER_CODE=G.ORDER_CODE AND M.ORDER_DR_CODE=H.USER_ID  ")
    	  .append("       AND A.FREQ_CODE = E.FREQ_CODE  AND A.ORDER_CAT1_CODE IN ('PHA_C','PHA_W')  AND A.DOSAGE_QTY>0 AND A.ORDER_NO IS NOT NULL  ")
//    	  .append("C.REGION_CODE='").append(regionDesec)
    	  .append(" 	  AND A.BILL_DATE BETWEEN  TO_DATE ('").append(startDate).append("', 'YYYYMMDDHH24MISS')  AND TO_DATE ('").append(endDate).append("', 'YYYYMMDDHH24MISS')  ")
//    	  .append("       AND A.ORDER_CODE = '").append(orderCode).append("'")
    	  .append("   AND A.DEPT_CODE= '").append(deptCode).append("'  AND A.CASE_NO=M.CASE_NO(+) AND A.ORDER_SEQ=M.ORDER_SEQ(+) AND A.ORDER_NO=M.ORDER_NO(+) ")
    	  .append(" UNION ALL  ")
    	  .append(" SELECT to_char(A.OPT_DATE,'yyyy-MM-dd') AS OPT_DATE,C.ORDER_DESC, E.MR_NO,  B.PAT_NAME,C.SPECIFICATION AS SPECIFICATION,D.UNIT_CHN_DESC,C.RETAIL_PRICE, ")        // LIRUI 20120613    ��ҩƷ����
    	  .append("        '' AS DSPN_KIND, '' AS FREQ_CHN_DESC,A.DOSAGE_QTY AS DOSAGE_QTY , A.TOT_AMT AS TOT_AMT,A.OWN_AMT,'' AS UDD_NUMB,F.USER_NAME ")               // LIRUI 20120613    ȥ���˲���id
    	  .append(" FROM IBS_ORDD A, SYS_PATINFO B, PHA_BASE C,  SYS_UNIT D,ADM_INP E ,SYS_OPERATOR F,ODI_ORDER M   ")
    	  .append(" WHERE A.CASE_NO=E.CASE_NO(+) AND E.MR_NO = B.MR_NO  AND A.ORDER_CODE = C.ORDER_CODE  AND C.DOSAGE_UNIT = D.UNIT_CODE ")
    	  .append("       AND M.ORDER_DR_CODE=F.USER_ID AND A.ORDER_CAT1_CODE IN ('PHA_C','PHA_W') AND A.ORDER_NO IS NOT NULL AND A.DOSAGE_QTY < 0  ")
    	  .append("   AND A.DEPT_CODE = '").append(deptCode).append("'  AND A.CASE_NO=M.CASE_NO(+) AND A.ORDER_SEQ=M.ORDER_SEQ(+) AND A.ORDER_NO=M.ORDER_NO(+)  ")
    	  .append("       AND A.BILL_DATE  BETWEEN TO_DATE ('").append(startDate).append("', 'YYYYMMDDHH24MISS')  AND TO_DATE ('").append(endDate).append("', 'YYYYMMDDHH24MISS') ")
//    	  .append("       AND A.ORDER_CODE = '").append(orderCode).append("'")
    	  
    	;
    	return sb.toString();
    }
    
  //lirui 2012-6-7 end ��ѯҩƷ����ͳ�����
    /**
     * ��װ ִ�п��� ��ѯ����
     * @param deptCode
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getDeptCodeArr(String deptCode){
    	String[] strArr = new String[3];
    	if(null != deptCode && !"".equals(deptCode)){
	    	strArr[0] = " and a.dept_code='" + deptCode + "' ";
	    	strArr[1] = " and a.dept_code='" + deptCode + "' ";
	    	strArr[2] = " and a.dept_code='" + deptCode + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }
    
    /**
     * ��װҩƷ�����ѯ����
     * @param orderCode ҩƷ����
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getOrderCodeArr(String orderCode){
    	String[] strArr = new String[3];
    	if(null != orderCode && !"".equals(orderCode)){
	    	strArr[0] = " and a.order_code='" + orderCode + "' ";
	    	strArr[1] = " and a.order_code='" + orderCode + "' ";
	    	strArr[2] = " and a.order_code='" + orderCode + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }
    
    /**
     * ��װ������ҩƷ��ѯ����
     * @param antibioticCode �����ر���
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getAntibioticCode(String antibioticCode){
    	String[] strArr = new String[3];
    	if(null != antibioticCode && !"".equals(antibioticCode)){
	    	strArr[0] = " and b.antibiotic_code='" + antibioticCode + "' ";
	    	strArr[1] = " and b.antibiotic_code='" + antibioticCode + "' ";
	    	strArr[2] = " and c.antibiotic_code='" + antibioticCode + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }
    
    /**
     * ��װ����(����ҩ)ҩƷ��ѯ����
     * @param orgCode ����ҩƷ����
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getCtrldrugclassCode(String ctrldrugclassCode){
    	String[] strArr = new String[3];
    	if(null != ctrldrugclassCode && !"".equals(ctrldrugclassCode)){
	    	strArr[0] = " and b.ctrldrugclass_code='" + ctrldrugclassCode + "' ";
	    	strArr[1] = " and b.ctrldrugclass_code='" + ctrldrugclassCode + "' ";
	    	strArr[2] = " and c.ctrldrugclass_code='" + ctrldrugclassCode + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }
    
    /**
     * ��װ ҩ������ ��ѯ���������һ���ҩƷ���Բ�ҩƷ��
     * @param orgCode ҩ���������
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getSysGrugClass(String sysGrugClass){
    	String[] strArr = new String[3];
    	if(null != sysGrugClass && !"".equals(sysGrugClass)){
	    	strArr[0] = " and f.sys_grug_class='" + sysGrugClass + "' ";
	    	strArr[1] = " and e.sys_grug_class='" + sysGrugClass + "' ";
	    	strArr[2] = " and f.sys_grug_class='" + sysGrugClass + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }
    
    /**
     * ��װ ҩƷ�����ѯ����(��ҩ����ҩ���г�ҩ)
     * @param orgCode ҩ��������
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getTypeCode(String typeCode){
    	String[] strArr = new String[3];
    	if(null != typeCode && !"".equals(typeCode)){
	    	strArr[0] = " and b.type_code='" + typeCode + "' ";
	    	strArr[1] = " and b.type_code='" + typeCode + "' ";
	    	strArr[2] = " and c.type_code='" + typeCode + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }  
    
    /**
     * ��װ סԺҩƷ������ʽ ��ѯ����(��� סԺ)
     * ��ODI_DSPNM.DSPN_KIND(ST ��ʱ��UD���ڡ�DS ��Ժ��ҩ��RT ��ҩ
     * @param dspn_kind ������ʽ����
     * @return str 
     */
    private static String getDspnKind(String dspnKind){
    	String str = " ";
    	if(null != dspnKind && !"".equals(dspnKind)){
	    	str = " and a.dspn_kind='" + dspnKind + "' ";
    	}
    	return str;
    } 
    
    /**
     * ��װ  ������� ��ѯ����
     * ҽ���������A��ҽ�� B������C���Է�
     * @param inspayType ����������
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String[] getInspayType(String inspayType){
    	String[] strArr = new String[3];
    	if(null != inspayType && !"".equals(inspayType)){
	    	strArr[0] = " and f.inspay_type='" + inspayType + "' ";
	    	strArr[1] = " and e.inspay_type='" + inspayType + "' ";
	    	strArr[2] = " and f.inspay_type='" + inspayType + "' ";
    	}else{
    		strArr[0] = "   ";
	    	strArr[1] = "   ";
	    	strArr[2] = "   ";
    	}
    	return strArr;
    }    
    
    /**
     * ��װ �Ƿ��Ժ ��ѯ����
     * @param orgCode 0/null:ȫ����1:��Ժ��2����Ժ
     * @return str[0,1,2] �ֱ��Ӧ �������Ƽۣ�סԺ
     */
    private static String getDsDate(String dsDateFlag){
    	String str = " ";
    	if(null != dsDateFlag && !"".equals(dsDateFlag)){
    		str = "  and g.ds_date is not null  and  CANCEL_FLG ='N' ";
    	}else {// if{(null != dsDateFlag && !"".equals(dsDateFlag)){
    		str = "  and g.ds_date is  null  and  CANCEL_FLG ='N'  ";
    	}    	
    	return str;
    }
    
    /**
     * ���ſ���ѯ-����ʾ�������
     * @param parm
     * @return
     * @author liyh
     * @date  20120808
     */
    public static String getOrgStockQueryNotBatchSQL(TParm  parm){
    	String orgCode = parm.getValue("ORG_CODE");
    	String orderCode = parm.getValue("ORDER_CODE");
    	String batchNo = parm.getValue("BATCH_NO");
    	String matCode = parm.getValue("MATERIAL_LOC_CODE");
    	String typeCode = parm.getValue("TYPE_CODE");
    	String safeQty = parm.getValue("SAFE_QTY");
    	String stockQty = parm.getValue("STOCK_QTY");
    	String phaType = parm.getValue("PHA_TYPE");
    	//���ڲ�ѯ��ȫ�����
    	String childSql = " ";
    	String sql = " SELECT A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, H.CHN_DESC,SUM(A.STOCK_QTY) AS STOCK_QTY, G.UNIT_CHN_DESC, " +
    				 "        FLOOR(SUM(A.STOCK_QTY) / F.DOSAGE_QTY) || E.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY), F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY, " +
    				 "        A.RETAIL_PRICE * F.DOSAGE_QTY || '/' || E.UNIT_CHN_DESC || ';' ||   A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, " +
    				 "        A.VERIFYIN_PRICE AS STOCK_PRICE, SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE AS STOCK_AMT, SUM(A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, " +
    				 "        (SUM(A.STOCK_QTY) * A.RETAIL_PRICE - SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,'' AS BATCH_NO,'' AS VALID_DATE, " +
    				 "		   A.STOCK_FLG,B.SAFE_QTY, D.PHA_TYPE, I.MATERIAL_CHN_DESC,A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG " +
    				 " FROM   IND_STOCK  A, IND_STOCKM  B,  SYS_FEE  C,  PHA_BASE  D, SYS_UNIT  E,PHA_TRANSUNIT   F, " +
    				 "		  SYS_UNIT   G,SYS_DICTIONARY  H,IND_MATERIALLOC I " +
    				 " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE  AND A.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = C.ORDER_CODE  " +
    				 "		 AND A.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = D.ORDER_CODE AND D.STOCK_UNIT = E.UNIT_CODE " +
    				 "       AND A.ORDER_CODE = F.ORDER_CODE AND B.ORDER_CODE = F.ORDER_CODE  AND C.ORDER_CODE = F.ORDER_CODE " +
    				 "       AND D.ORDER_CODE = F.ORDER_CODE AND D.DOSAGE_UNIT = G.UNIT_CODE  AND H.GROUP_ID = 'SYS_PHATYPE' AND D.TYPE_CODE = H.ID " +
                     "  AND A.ORG_CODE = I.ORG_CODE(+)  AND A.MATERIAL_LOC_CODE = I.MATERIAL_LOC_CODE(+) AND A.ORG_CODE='" + orgCode + "' " 
                     ;
    	
    	if(null != orderCode && !"".equals(orderCode)){
    		sql += " AND A.ORDER_CODE='" + orderCode + "' ";
    		childSql = " AND G.ORDER_CODE='" + orderCode + "' ";
    	}
    	if(null != batchNo && !"".equals(batchNo)){
    		sql += " AND A.BATCH_NO=='" + batchNo + "' ";
    	}
    	if(null != matCode && !"".equals(matCode)){
    		sql += " AND A.MATERIAL_LOC_CODE=='" + matCode + "' ";
    	}
    	if(null != typeCode && !"".equals(typeCode)){
    		sql += " AND D.TYPE_CODE='" + typeCode + "' ";
    	}
    	if(null != safeQty && !"".equals(safeQty)){
    		sql += " AND A.ORDER_CODE in ( " +
    									" SELECT  NVL(G.ORDER_CODE ,0) AS ORDER_CODE " +
										" FROM IND_STOCK G,IND_STOCKM K " +
    									" WHERE   G.ORG_CODE=K.ORG_CODE AND G.ORDER_CODE=K.ORDER_CODE AND " +
    									"         G.ORG_CODE='"+ orgCode +"' " +  childSql + " " + 
    									" GROUP BY G.ORG_CODE,G.ORDER_CODE,K.SAFE_QTY " +
    									" HAVING SUM(G.STOCK_QTY)<K.SAFE_QTY   " + 
    									" ) ";
    	}
    	if(null != stockQty && !"".equals(stockQty)){
    		sql += " AND A.STOCK_QTY>0 ";
    	}    	
    	sql += "  GROUP BY A.ORDER_CODE,C.ORDER_DESC, C.SPECIFICATION,H.CHN_DESC,E.UNIT_CHN_DESC,F.DOSAGE_QTY,G.UNIT_CHN_DESC,A.RETAIL_PRICE, "+
               " A.VERIFYIN_PRICE,  A.STOCK_FLG,  B.SAFE_QTY,  D.PHA_TYPE,  I.MATERIAL_CHN_DESC, A.ACTIVE_FLG  ORDER BY A.ORDER_CODE ";
//    	System.out.println("----------sql: "+sql);
    	return sql;
    }
    
    /**
     * ��ѯҩƷ��Ϣ
     *
     * @param orderCode ҩƷ����
     * @return String
     */
    public static String getPHABaseInfo(String orderCode) {
        return " SELECT A.ORDER_CODE,A.SPECIFICATION,A.RETAIL_PRICE,A.STOCK_PRICE,A.PURCH_UNIT,B.MAN_CODE "
             + " FROM PHA_BASE A, SYS_FEE B "
             + " WHERE A.ORDER_CODE = '" + orderCode + "' AND A.ORDER_CODE=B.ORDER_CODE ";
    }
    
    
    /**
     * ���ݶ����ź�ҩƷ��ѯ����������ϸ
     *
     * @param purorder_no
     *            ��������
     * @return
     */
    public static String getPurOrderDByNoAndOrder(String purorder_no,String orderCode) {
        if (null == purorder_no || null == orderCode ||"".equals(purorder_no) || "".equals(orderCode)) {
            return "";
        }
		return "SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, "
				+ "BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM "
				+ "FROM IND_PURORDERD " + "WHERE PURORDER_NO='" + purorder_no + "' AND ORDER_CODE='" + orderCode + "' " + "ORDER BY SEQ_NO";
	}
    
    /**
     * �޸Ĵ�������ϢSQL
     * @param parm
     * @return
     * @author liyh
     * @date 20120907
     */
    public static String updateAgent(TParm parm){
    	String sql = " UPDATE IND_AGENT " +
    	 			 " SET CONTRACT_PRICE=" + parm.getValue("CONTRACT_PRICE") + ",LAST_ORDER_DATE=TO_DATE('" + parm.getValue("LAST_ORDER_DATE") + "','yyyy-MM-dd hh24:mi:ss'), " + 
    	 			 " LAST_ORDER_QTY=" + parm.getValue("LAST_ORDER_QTY") + ",LAST_ORDER_PRICE=" + parm.getValue("LAST_ORDER_PRICE") + ", " +
    	 			 " LAST_ORDER_NO='" + parm.getValue("LAST_ORDER_NO") + "',LAST_VERIFY_DATE=SYSDATE, " +
    	 			 " LAST_VERIFY_PRICE=" + parm.getValue("LAST_VERIFY_PRICE") + ",OPT_USER='" + parm.getValue("OPT_USER") + "', " +
    	 			 " OPT_DATE=SYSDATE,OPT_TERM='" + parm.getValue("OPT_TERM") + "' " + 
    	 			 " WHERE SUP_CODE='" + parm.getValue("SUP_CODE") + "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE") + "' ";
    	return sql;
    }
    
    /**
     * ��ѯ������Ϣ
     * @param parm
     * @return
     * @author yuanxm
     * @date 20120911
     */
    public static String getPurOrderDSqlByNo(String purorder_no)
    {
      String sql = " SELECT A.SUP_CODE,B.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, D.UNIT_CHN_DESC, " +
      		       " B.PURORDER_QTY,B.PURORDER_PRICE ,C.MAN_CODE, A.PURORDER_NO ,B.SEQ_NO ,E.SPC_MEDICINE_CODE " +
      		       " FROM IND_PURORDERM A,IND_PURORDERD B, SYS_FEE C,SYS_UNIT D, PHA_BASE E  " +
      		       "WHERE A.PURORDER_NO='" + purorder_no + "' AND A.PURORDER_NO=B.PURORDER_NO AND B.ORDER_CODE=C.ORDER_CODE AND " +
      		       "B.BILL_UNIT=D.UNIT_CODE AND B.ORDER_CODE=E.ORDER_CODE ";
      return sql;
    }
    
    /**
     * ����ҩ���������ӱ�ǩID
     * @param eleTagCode
     * @param orgCode
     * @param orderCode
     * @return sql
     * @author liyh
     * @date 20121013
     */
    public static String  updateEleTagCode(String eleTagCode, String orgCode,String orderCode) {
		String sql = "  UPDATE IND_STOCKM SET ELETAG_CODE='" + eleTagCode + "' WHERE ORG_CODE='" + orgCode + "' AND ORDER_CODE='" + orderCode + "' ";
		return sql;
	}
    

    /**
     * ����������ѯ�ⲿ��
     * @param condition
     * @param noCondition
     * @param region_code
     * @return
     * @author liyh
     * @date 20121019
     */
    public static String queryOrgInfo(String condition, String noCondition, String region_code) {
        String type = "WHERE REGION_CODE='" + region_code + "' ";
        if (!"".equals(condition)) {
            type +=  condition;
        }
        return "SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME FROM IND_ORG " + type  + " ORDER BY ORG_CODE,SEQ";
    }    
    
    /**
     * ���ݲ��Ų�ѯ���������Ϣ-�Զ�����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121019
     */
    public static String queryStockM(String orgCode,String fixedType){
    	String condition = " ";
    	if("1".equals(fixedType)){//
    		condition = "   HAVING SUM(B.STOCK_QTY) < A.SAFE_QTY ";
    	}else if("2".equals(fixedType)){
    		condition = "   HAVING SUM(B.STOCK_QTY) < A.MIN_QTY ";
    	}
    	
    	String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,SUM(B.STOCK_QTY) AS STOCK_QTY "
    		       + " FROM IND_STOCKM A,IND_STOCK B,PHA_BASE C "
    		       + " WHERE A.ORG_CODE='" + orgCode + "' AND A.DISPENSE_FLG='Y' AND A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE "
    		       + "  AND A.ORDER_CODE=C.ORDER_CODE AND C.CTRLDRUGCLASS_CODE IS NULL " 
    		       + condition + " "
    		       + " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY ";
    	return sql;
    }
    
    /**
     * ���ݲ��Ų�ѯ���������Ϣ-�Զ�����-����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121019
     */
    public static String queryStockMDrug(String orgCode,String fixedType){
    	String condition = " ";
    	if("1".equals(fixedType)){//
    		condition = "   HAVING SUM(B.STOCK_QTY) < A.SAFE_QTY ";
    	}else if("2".equals(fixedType)){
    		condition = "   HAVING SUM(B.STOCK_QTY) < A.MIN_QTY ";
    	}
    	
    	String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,SUM(B.STOCK_QTY) AS STOCK_QTY "
    		       + " FROM IND_STOCKM A,IND_STOCK B ,PHA_BASE C,SYS_CTRLDRUGCLASS D "
    		       + " WHERE A.ORG_CODE='" + orgCode + "' AND A.DISPENSE_FLG='Y' AND A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE "
    		       + "  AND A.ORDER_CODE=C.ORDER_CODE AND C.CTRLDRUGCLASS_CODE IS NOT NULL AND C.CTRLDRUGCLASS_CODE=D.CTRLDRUGCLASS_CODE  AND D.CTRL_FLG='Y' "
    		       + condition + " "
    		       + " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY ";
    	return sql;
    }    
    
    /**
     * ��ѯ������Ϣ-�Զ�����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121022
     */
    public static String queryOrgCodeAuto(){
    	String sql = " SELECT  A.ORG_CODE,NVL(A.DISPENSE_ORG_CODE,'-1') AS DISPENSE_ORG_CODE ,B.ORG_TYPE "
    		       + " FROM IND_STOCKM A,IND_ORG B  "
    		       + " WHERE A.DISPENSE_FLG='Y'  AND A.ORG_CODE=B.ORG_CODE  "
    		       + " GROUP BY A.ORG_CODE,A.DISPENSE_ORG_CODE  ,B.ORG_TYPE ";
    	return sql;
    }
    
    /**
     * ������������-�Զ�����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121022
     */
    public static String saveRequestMAuto(TParm parm ){
    	String sql = " INSERT INTO IND_REQUESTM(REQUEST_NO, REQTYPE_CODE, APP_ORG_CODE, TO_ORG_CODE, REQUEST_DATE, REQUEST_USER, REASON_CHN_DESC, " 
    			   + " UNIT_TYPE, URGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE,DRUG_CATEGORY,APPLY_TYPE) "
    		       + " VALUES("
    		       + " '" + parm.getValue("REQUEST_NO")+ "', 'ATO', '" + parm.getValue("APP_ORG_CODE")+ "',"
    		       + " '" + parm.getValue("TO_ORG_CODE")+ "',sysdate,'" + parm.getValue("REQUEST_USER")+ "','" + parm.getValue("REASON_CHN_DESC")+ "',"
    		       + " '" + parm.getValue("UNIT_TYPE")+ "', '" + parm.getValue("URGENT_FLG")+ "', 'OPTUSER', sysdate," 
    		       + " 'OPTID','" +  parm.getValue("REGION_CODE") + "','" + parm.getValue("DRUG_CATEGORY") + "','3' "
    		       + " ) ";
    	return sql;
    }
    
    /**
     * ����������ϸ��-�Զ�����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121022
     */
    public static String saveRequestDAuto(TParm parm ){
    	String sql = " INSERT INTO IND_REQUESTD(REQUEST_NO, SEQ_NO, ORDER_CODE, QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, UPDATE_FLG, " 
    			   + "  OPT_USER, OPT_DATE, OPT_TERM, VERIFYIN_PRICE, BATCH_SEQ)  "
    		       + " VALUES("
    		       + " '" + parm.getValue("REQUEST_NO")+ "', " + parm.getValue("SEQ_NO")+ ", '" + parm.getValue("ORDER_CODE")+ "',"
    		       + "  " + parm.getValue("QTY")+ ",'" + parm.getValue("UNIT_CODE")+ "'," + parm.getValue("RETAIL_PRICE")+ ","
    		       + "  " + parm.getValue("STOCK_PRICE")+ ",0, '0','OPTUSER', sysdate,'OPTIP',0,0 "
    		       + " ) ";
    	return sql;
    }
    
    
    /**
     * ���涩��/�ƻ�����-�Զ�����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121022
     */
    public static String savePurOrderMAuto(TParm parm ){
    	String sql = " INSERT INTO IND_PURORDERM (PURORDER_NO, PURORDER_DATE, ORG_CODE, SUP_CODE, RES_DELIVERY_DATE, REASON_CHN_DESC " 
    			   + "  , OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE,DRUG_CATEGORY,APPLY_TYPE) "
    		       + " VALUES("
    		       + " '" + parm.getValue("PURORDER_NO")+ "',sysdate, '" + parm.getValue("ORG_CODE")+ "'," + " '" + parm.getValue("SUP_CODE")
    		       + "',null,'" +parm.getValue("REASON_CHN_DESC")+ "','OPTUSER', sysdate,'OPTIP','" + parm.getValue("REGION_CODE")+ "','" + parm.getValue("DRUG_CATEGORY") + "','3' "
    		       + " ) ";
    	return sql;
    }
    
    /**
     * ���涩��/�ƻ���ϸ��-�Զ�����
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121022
     */
    public static String savePurOrderDAuto(TParm parm ){
    	String sql = " INSERT INTO IND_PURORDERD (PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, BILL_UNIT, " 
    			   + "  PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM) "
    		       + " VALUES("
    		       + " '" + parm.getValue("PURORDER_NO")+ "', " + parm.getValue("SEQ_NO")+ ", '" + parm.getValue("ORDER_CODE")+ "',"
    		       + "  " + parm.getValue("PURORDER_QTY")+ ",0,'" + parm.getValue("UNIT_CODE")+ "'," + parm.getValue("PURCH_PRICE")+ ","
    		       + "  0, 0,'0','OPTUSER', sysdate,'OPTIP' "
    		       + " ) ";
    	return sql;
    }
    
    /**
     * ��ѯҩ�ⲿ�����
     *
     * @param org_code
     *            ҩ�����
     * @return String
     */
    public static String getSubOrgInfo(String org_code) {
        return "SELECT ORG_CODE,SUP_ORG_CODE FROM IND_ORG WHERE ORG_CODE = '" + org_code
            + "'";
    }
    
    /**
     * ����IND_STOCK����λ
     * @param org_code String
     * @param order_code String
     * @return String
     * @author liyh
     * @date 20121022
     */
    public static String onUpdateStcokMaterialLocCode(String org_code,
        String order_code, String material_loc_code) {
        return "UPDATE IND_STOCKM SET MATERIAL_LOC_CODE ='" + material_loc_code +
            "' WHERE ORG_CODE = '" + org_code + "' AND ORDER_CODE = '" +
            order_code + "'";
    }
    
    /**
     *�ж�ҩƷ���Ƿ����龫
     * @param org_code String
     * @param order_code String
     * @return 
     * @author liyh
     * @date 20121025
     */
    public static String isDrug(String orderCode) {
        return " SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='" +orderCode+ "' AND CTRL_FLG='Y' " ;
    }   
    
    /**
     * �����������ĵ��ӱ�ǩ����λ
     * @param parm
     * @return String 
     * @author liyh 
     * @date  20121026
     */
    public static String onSaveMatLocStockM(TParm parm){
        String sql = " INSERT INTO IND_STOCKM "
        		   + " (ORG_CODE, ORDER_CODE, REGION_CODE, DISPENSE_FLG, MM_USE_QTY, DD_USE_QTY, MAX_QTY, SAFE_QTY, MIN_QTY, ECONOMICBUY_QTY, "
        		   + "  BUY_UNRECEIVE_QTY, STANDING_QTY, ACTIVE_FLG, OPT_USER, OPT_DATE, OPT_TERM, MATERIAL_LOC_CODE,MATERIAL_LOC_DESC,ELETAG_CODE ) "
        		   + " VALUES " 
        		   + " ('" + parm.getValue("ORG_CODE")+ "', '" + parm.getValue("ORDER_CODE")+ "', '" + parm.getValue("REGION_CODE")+ "', "
        		   + " 'N', 0,0,0,0,0,0,0,0,'N','" + parm.getValue("OPT_USER")+ "',sysdate,'" + parm.getValue("OPT_TERM")+ "', " 
        		   + " '" + parm.getValue("MATERIAL_LOC_CODE")+ "', '" + parm.getValue("MATERIAL_LOC_DESC")+ "', '" + parm.getValue("ELETAG_CODE")+ "' " 
        		   + " ) ";
        return sql;
    }    
    
    /**
     * ���¿�������ĵ��ӱ�ǩ����λ
     * @param parm
     * @return String 
     * @author liyh 
     * @date  20121026
     */
    public static String onUpdateMatLocStockM(TParm parm){
        String sql = " UPDATE  IND_STOCKM "
        		   + " SET MATERIAL_LOC_CODE='" + parm.getValue("MATERIAL_LOC_CODE") + "', MATERIAL_LOC_DESC='" + parm.getValue("MATERIAL_LOC_DESC") + "',"
        		   + " ELETAG_CODE = '" + parm.getValue("ELETAG_CODE")+ "' " 
        		   + " WHERE ORG_CODE='"+ parm.getValue("ORG_CODE")+ "' AND ORDER_CODE = '" + parm.getValue("ORDER_CODE")+"' ";
        return sql;
    }        
    
	/**
	 * �޸��ƻ�����״̬-���
	 * @return
	 * @date 20121029
	 * @author liyh
	 */
	public static String upDateStatusINDPourOrder(TParm parm ) {
	/*	return  " UPDATE IND_PURORDERM SET CHECK_DATE=SYSDATE(),CHECK_USER='" + parm.getValue("OPT_USER") + "', "
		      + " OPT_TERM='" + parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE,OPT_USER='" + parm.getValue("OPT_USER") + "' "
			  + " WHERE PURORDER_NO='" + parm.getValue("PURORDER_NO") + "' ";  */
		return " UPDATE IND_PURORDERM SET OPT_USER='D005'  WHERE PURORDER_NO='121026000001' ";
	}
	
	  /**
     * ����ҩƷ������PHA��Ϣ
     *
     * @param order_code
     * @return
     */
    public static String getPHAInfoByOrderSpc(String order_code) {
        return "SELECT A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, A.RETAIL_PRICE, A.TRADE_PRICE, "
            +
            "A.STOCK_PRICE, B.PURCH_QTY, B.STOCK_QTY, B.DOSAGE_QTY, A.PHA_TYPE, B.MEDI_UNIT, B.MEDI_QTY,A.SPECIFICATION,A.ORDER_DESC "
            + "FROM PHA_BASE A, PHA_TRANSUNIT B "
            + "WHERE A.ORDER_CODE = B.ORDER_CODE "
            + "AND A.ORDER_CODE = '"
            + order_code + "'";
    }
    /**
	 * ��ѯ�Ž�
	 * @return
	 */
	public static String getCabinetGuard(String cabinetId) {
		if ("".equals(cabinetId)) {
            return "";
        }
		return "SELECT GUARD_ID,GUARD_DESC,IS_TOXIC_GUARD,OPT_USER,OPT_DATE,OPT_TERM,CABINET_ID " +
				"FROM IND_CABINET_GUARD WHERE CABINET_ID='"+cabinetId+"' ORDER BY GUARD_ID";
	}
	/**
     * ͳ�Ʋ�ѯ����ҩƷ����-��ϸ
     * @return
     */
    public static String getStaticDrugQtyInContainerD(TParm parm){
    	return "   SELECT B.CONTAINER_DESC,A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,A.TOXIC_ID,D.UNIT_CHN_DESC,A.BATCH_NO,A.VALID_DATE,A.VERIFYIN_PRICE "
    		 + "   FROM IND_CONTAINERD A ,IND_CONTAINERM B,PHA_BASE C,SYS_UNIT D "
    		 + "   WHERE A.CABINET_ID='" + parm.getValue("CABINET_ID") + "' AND A.CONTAINER_ID=B.CONTAINER_ID AND A.ORDER_CODE=C.ORDER_CODE AND A.UNIT_CODE=D.UNIT_CODE " 
    		 + "   ORDER BY  B.CONTAINER_DESC,A.ORDER_CODE";
    }
    
    /**
     * ͳ�Ʋ�ѯ������ҩҩƷ����-��ϸ
     * @return
     */
    public static String getStaticNromalQtyInContainerD(TParm parm){
    	return "   SELECT   A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION,A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,A.VERIFYIN_PRICE, C.UNIT_CHN_DESC,SUM(STOCK_QTY) AS STOCK_QTY "
    		 + "   FROM IND_CBNSTOCK A ,PHA_BASE B,SYS_UNIT C "
    		 + "   WHERE A.CABINET_ID='" + parm.getValue("CABINET_ID") + "' AND A.ORDER_CODE=B.ORDER_CODE AND A.STOCK_UNIT=C.UNIT_CODE " 
    		 + "   GROUP BY  A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION,A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,A.VERIFYIN_PRICE, C.UNIT_CHN_DESC " ;
    } 
	
	/**
     * ͳ�Ʋ�ѯ������ҩҩƷ����-�ϼ�
     * @return
     */
    public static String getStaticNromalQtyInContainerM(TParm parm){
    	return "   SELECT A.ORDER_CODE,B.ORDER_DESC,C.UNIT_CHN_DESC,SUM(STOCK_QTY) AS TOT_QTY"
    		 + "   FROM INV_CBNSTOCK A ,PHA_BASE B,SYS_UNIT C "
    		 + "   WHERE A.CABINET_ID='" + parm.getValue("CABINET_ID") + "' AND A.ORDER_CODE=B.ORDER_CODE AND A.STOCK_UNIT=C.UNIT_CODE " 
    		 + "   GROUP BY A.ORDER_CODE,B.ORDER_DESC,C.UNIT_CHN_DESC " ;
    }   
	
	/**
     * ͳ�Ʋ�ѯ�����龫ҩƷ����-�ϼ�
     * @return
     */
    public static String getStaticDrugQtyInContainerM(TParm parm){
    	return "   SELECT A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC,COUNT(A.CONTAINER_ID) AS CONTAINER_QTY,SUM(A.TOT_QTY) AS TOT_QTY "
    		 + "   FROM( "
    		 + "        SELECT B.ORDER_CODE,D.ORDER_DESC,B.CONTAINER_ID,C.UNIT_CHN_DESC,COUNT(B.CONTAINER_ID) AS TOT_QTY " 
    		 + "  		FROM INV_CONTAINERD B ,SYS_UNIT C, PHA_BASE D"
    		 + "		WHERE  CABINET_ID='" + parm.getValue("CABINET_ID") + "' AND B.ORDER_CODE=D.ORDER_CODE  AND B.UNIT_CODE=C.UNIT_CODE"
    		 + "		GROUP BY B.ORDER_CODE,D.ORDER_DESC,CONTAINER_ID,C.UNIT_CHN_DESC "
    		 + " 		) A "
    		 + "   GROUP BY  A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC ";
    }
}

