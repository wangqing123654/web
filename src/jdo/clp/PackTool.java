package jdo.clp;

import com.dongyang.jdo.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;
import java.util.Map;

/**
 * <p>Title: ���ݿ����������</p>
 *
 * <p>Description: ҽ���ײ� �ؼ������ײ� ����ƻ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PackTool
    extends TJDOTool {
    /**
     * ���췽��˽�л�������SQL��������ļ�
     */
    private PackTool() {
        this.setModuleName("clp\\CLPBscInfoModule.x");
        onInit();
    }

    /**
     * ������̬���ݿ⹤����ʵ��
     */
    private static PackTool instance = null;
    /**
     * �������ڸ�ʽ�������
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * ������̬���������ش����ʵ��
     * @return BscInfoTool
     */
    public static PackTool getInstance() {
        if (instance == null) {
            instance = new PackTool();
        }
        return instance;
    }

    /**
     * ORDER_FLG=Y:ҽ���ײ�
     * @param parm TParm
     * @return TParm
     */
    public TParm getPack01List(TParm parm) {
//    	System.out.println("99999999999999999999999");
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT '' AS SEL_FLG,");
        sqlBuf.append(" CP.CLNCPATH_CODE,");
        sqlBuf.append(" CP.SCHD_CODE,");
        sqlBuf.append(" CP.ORDER_TYPE,");
        sqlBuf.append(" CP.CHKTYPE_CODE,");
        sqlBuf.append(" CO2.TYPE_CHN_DESC,");
        sqlBuf.append(" SF.ORDER_DESC AS ORDER_CHN_DESC,");
        sqlBuf.append(" CP.DOSE,");
        sqlBuf.append(" CP.DOSE_UNIT,");
        sqlBuf.append(" CP.FREQ_CODE,");
        sqlBuf.append(" CP.ROUT_CODE,");
        sqlBuf.append(" CP.DOSE_DAYS,");
        sqlBuf.append(" CP.NOTE,");
        sqlBuf.append(" CP.CHKUSER_CODE,");
        sqlBuf.append(" CP.RBORDER_DEPT_CODE,");
        sqlBuf.append(" CP.URGENT_FLG,");
        sqlBuf.append(" CP.EXEC_FLG,");
        sqlBuf.append(" '' AS TOTAL,");
        sqlBuf.append(" '' AS FEES,");
        sqlBuf.append(" SF.OWN_PRICE AS OWN_PRICE_REAL ,");
        sqlBuf.append(" '' AS FEES_REAL,");
        sqlBuf.append(" CP.STANDARD,");
        sqlBuf.append(" CP.VERSION,");
        sqlBuf.append(" CP.ORDER_SEQ_NO,");
        sqlBuf.append(" CP.SEQ,");
        sqlBuf.append(" CP.ORDER_CODE,");
        sqlBuf.append(" CO1.ORDTYPE_CODE,");
        sqlBuf.append(" CP.START_DAY,CASE WHEN P.DOSAGE_QTY IS NULL THEN 0 ELSE P.DOSAGE_QTY END AS DOSAGE_QTY,SF.CAT1_TYPE, CP.DOSE AS MEDI_QTY,");
        sqlBuf.append(" P.DOSAGE_UNIT AS DISPENSE_UNIT,P.MEDI_UNIT,CP.DOSE_DAYS AS TAKE_DAYS,SF.ACTIVE_FLG,SF.ORDERSET_FLG,CP.PACK_CODE, ");
//        System.out.println("=====parm parm is ::"+parm);
//        System.out.println("2222=====parm parm is ::"+parm.getValue("ACTIVE_FLG"));
        if(parm.getValue("ACTIVE_FLG").equals("N")){//·��ͣ��
        	sqlBuf.append("CP.OWN_PRICE");
        }else{
        	sqlBuf.append("SF.OWN_PRICE");
        }
        sqlBuf.append(
            " FROM CLP_PACK CP, SYS_FEE SF, CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2,PHA_TRANSUNIT P ");
        sqlBuf.append(" WHERE CP.ORDER_CODE = SF.ORDER_CODE");
        sqlBuf.append(" AND CP.ORDER_CODE = CO1.ORDER_CODE");
        sqlBuf.append(" AND CP.ORDER_FLG = CO1.ORDER_FLG");
        sqlBuf.append(" AND CO1.ORDTYPE_CODE = CO2.TYPE_CODE");
        sqlBuf.append(" AND CP.ORDER_CODE = P.ORDER_CODE(+)");
        //20110630 ¬��ɾ�� CLP_ORDTYPE ��region����
//        sqlBuf.append(" AND CP.REGION_CODE = CO2.REGION_CODE");
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND (CP.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") +
                          "' OR CP.REGION_CODE IS NULL OR CP.REGION_CODE='')");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND CP.CLNCPATH_CODE = '");
            sqlBuf.append(parm.getValue("CLNCPATH_CODE") + "'");
        }
        if (parm.getValue("SCHD_CODE") != null &&
            !"".equals(parm.getValue("SCHD_CODE").trim())) {
            sqlBuf.append(" AND CP.SCHD_CODE = '");
            sqlBuf.append(parm.getValue("SCHD_CODE") + "'");
        }
        if (parm.getValue("ORDER_FLG") != null &&
            !"".equals(parm.getValue("ORDER_FLG").trim())) {
            sqlBuf.append(" AND CP.ORDER_FLG = '");
            sqlBuf.append(parm.getValue("ORDER_FLG") + "'");
        }
        if (parm.getValue("VERSION") != null &&
            !"".equals(parm.getValue("VERSION").trim())) {
            sqlBuf.append(" AND VERSION = '" + parm.getValue("VERSION") + "'");
        }
        if(parm.getValue("ACTIVE")!= null &&
            !"".equals(parm.getValue("ACTIVE").trim())){
        	 sqlBuf.append(" AND SF.ACTIVE_FLG IN  ('" + parm.getValue("ACTIVE") + "') ");
        }
        sqlBuf.append(
            " ORDER BY CP.CLNCPATH_CODE, CP.SCHD_CODE,CP.SEQ,CP.ORDER_TYPE");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ORDER_FLG=N:�ؼ�������Ŀ
     * @param parm TParm
     * @return TParm
     */
    public TParm getPack02List(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT '' AS SEL_FLG,");
        sqlBuf.append(" CP.ORDER_TYPE,");
        sqlBuf.append(" CP.CHKTYPE_CODE,");
        sqlBuf.append(" CO2.TYPE_CHN_DESC,");
        sqlBuf.append(" CC.CHKITEM_CHN_DESC AS ORDER_CHN_DESC,");
        sqlBuf.append(" CP.DOSE,");
        sqlBuf.append(" CP.DOSE_UNIT,");
        sqlBuf.append(" CP.FREQ_CODE,");
        sqlBuf.append(" CP.ROUT_CODE,");
        sqlBuf.append(" CP.DOSE_DAYS,");
        sqlBuf.append(" CP.NOTE,");
        sqlBuf.append(" CP.CHKUSER_CODE,");
        sqlBuf.append(" CP.RBORDER_DEPT_CODE,");
        sqlBuf.append(" CP.URGENT_FLG,");
        sqlBuf.append(" CP.EXEC_FLG,");
        sqlBuf.append(" '' AS TOTAL,");
        sqlBuf.append(" '' AS FEES,");
        sqlBuf.append(" CP.STANDARD,");
        sqlBuf.append(" CP.VERSION,");
        sqlBuf.append(" CP.ORDER_SEQ_NO,");
        sqlBuf.append(" CP.SEQ,");
        sqlBuf.append(" CP.ORDER_CODE,");
        sqlBuf.append(" CO1.ORDTYPE_CODE");
        sqlBuf.append(
            " FROM CLP_PACK CP, CLP_CHKITEM CC, CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2");
        sqlBuf.append(" WHERE CP.ORDER_CODE = CC.CHKITEM_CODE");
        sqlBuf.append(" AND CP.CHKTYPE_CODE = CC.CHKTYPE_CODE");
        sqlBuf.append(" AND CP.ORDER_CODE = CO1.ORDER_CODE");
        sqlBuf.append(" AND CP.ORDER_FLG = CO1.ORDER_FLG");
        sqlBuf.append(" AND CO1.ORDTYPE_CODE = CO2.TYPE_CODE");
//        sqlBuf.append(" AND CP.REGION_CODE = CO2.REGION_CODE"); //luhai delete 20110630
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND CP.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") +
                          "'");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND CP.CLNCPATH_CODE = '");
            sqlBuf.append(parm.getValue("CLNCPATH_CODE") + "'");
        }
        if (parm.getValue("SCHD_CODE") != null &&
            !"".equals(parm.getValue("SCHD_CODE").trim())) {
            sqlBuf.append(" AND CP.SCHD_CODE = '");
            sqlBuf.append(parm.getValue("SCHD_CODE") + "'");
        }
        if (parm.getValue("ORDER_FLG") != null &&
            !"".equals(parm.getValue("ORDER_FLG").trim())) {
            sqlBuf.append(" AND CP.ORDER_FLG = '");
            sqlBuf.append(parm.getValue("ORDER_FLG") + "'");
        }
        if (parm.getValue("VERSION") != null &&
            !"".equals(parm.getValue("VERSION").trim())) {
            sqlBuf.append(" AND VERSION = '" + parm.getValue("VERSION") + "'");
        }
        sqlBuf.append(
            " ORDER BY CP.CLNCPATH_CODE, CP.SCHD_CODE, CP.ORDER_SEQ_NO");
        //system.out.println("sql:"+sqlBuf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ORDER_FLG=O:����ƻ�
     * @param parm TParm
     * @return TParm
     */
    public TParm getPack03List(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT '' AS SEL_FLG,");
        sqlBuf.append(" CP.ORDER_TYPE,");
        sqlBuf.append(" CP.CHKTYPE_CODE,");
        sqlBuf.append(" CO2.TYPE_CHN_DESC,");
        sqlBuf.append(" CN.ORDER_CHN_DESC,");
        sqlBuf.append(" CP.DOSE,");
        sqlBuf.append(" CP.DOSE_UNIT,");
        sqlBuf.append(" CP.FREQ_CODE,");
        sqlBuf.append(" CP.ROUT_CODE,");
        sqlBuf.append(" CP.DOSE_DAYS,");
        sqlBuf.append(" CP.NOTE,");
        sqlBuf.append(" CP.CHKUSER_CODE,");
        sqlBuf.append(" CP.RBORDER_DEPT_CODE,");
        sqlBuf.append(" CP.URGENT_FLG,");
        sqlBuf.append(" CP.EXEC_FLG,");
        sqlBuf.append(" '' AS TOTAL,");
        sqlBuf.append(" '' AS FEES,");
        sqlBuf.append(" CP.STANDARD,");
        sqlBuf.append(" CP.VERSION,");
        sqlBuf.append(" CP.ORDER_SEQ_NO,");
        sqlBuf.append(" CP.SEQ,");
        sqlBuf.append(" CP.ORDER_CODE,");
        sqlBuf.append(" CO1.ORDTYPE_CODE");
        sqlBuf.append(
            " FROM CLP_PACK CP, CLP_NURSORDER CN, CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2");
        sqlBuf.append(" WHERE CP.ORDER_CODE = CN.ORDER_CODE");
        sqlBuf.append(" AND CP.ORDER_CODE = CO1.ORDER_CODE");
        sqlBuf.append(" AND CP.ORDER_FLG = CO1.ORDER_FLG");
        sqlBuf.append(" AND CO1.ORDTYPE_CODE = CO2.TYPE_CODE");
//        sqlBuf.append(" AND CP.REGION_CODE = CO2.REGION_CODE");//luhai delete 20110630
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND CP.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") +
                          "'");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND CP.CLNCPATH_CODE = '");
            sqlBuf.append(parm.getValue("CLNCPATH_CODE") + "'");
        }
        if (parm.getValue("SCHD_CODE") != null &&
            !"".equals(parm.getValue("SCHD_CODE").trim())) {
            sqlBuf.append(" AND CP.SCHD_CODE = '");
            sqlBuf.append(parm.getValue("SCHD_CODE") + "'");
        }
        if (parm.getValue("ORDER_FLG") != null &&
            !"".equals(parm.getValue("ORDER_FLG").trim())) {
            sqlBuf.append(" AND CP.ORDER_FLG = '");
            sqlBuf.append(parm.getValue("ORDER_FLG") + "'");
        }
        if (parm.getValue("VERSION") != null &&
            !"".equals(parm.getValue("VERSION").trim())) {
            sqlBuf.append(" AND VERSION = '" + parm.getValue("VERSION") + "'");
        }
        sqlBuf.append(
            " ORDER BY CP.CLNCPATH_CODE, CP.SCHD_CODE, CP.ORDER_SEQ_NO");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * �ٴ�·��
     * @param tableParm TParm �б���ȫ������
     * @param row int ��ǰ�к�
     * @param parm TParm  ���б�����
     * @return TParm result
     */
    public TParm parmFormatPackDetail(TParm tableParm, int row, TParm parm) {
        TParm detail = new TParm();
        // ���б�����
        detail.setData("CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
        detail.setData("SCHD_CODE", parm.getValue("SCHD_CODE"));
        detail.setData("REGION_CODE", parm.getValue("REGION_CODE"));
        detail.setData("OPT_USER", parm.getValue("OPT_USER"));
        detail.setData("OPT_DATE", parm.getValue("OPT_DATE"));
        detail.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        detail.setData("ORDER_FLG", parm.getValue("ORDER_FLG"));
        detail.setData("VERSION", parm.getValue("VERSION"));

        // �б���ʾ��Ŀ
        detail.setData("ORDER_TYPE", tableParm.getValue("ORDER_TYPE", row));
        detail.setData("ORDTYPE_CODE", tableParm.getValue("ORDTYPE_CODE", row));
        detail.setData("ORDER_CODE", tableParm.getValue("ORDER_CODE", row));
        detail.setData("CHKTYPE_CODE", tableParm.getValue("CHKTYPE_CODE", row));
        detail.setData("DOSE", tableParm.getValue("DOSE", row));
        detail.setData("DOSE_UNIT", tableParm.getValue("DOSE_UNIT", row));
        detail.setData("FREQ_CODE", tableParm.getValue("FREQ_CODE", row));
        detail.setData("ROUT_CODE", tableParm.getValue("ROUT_CODE", row));
        detail.setData("DOSE_DAYS", tableParm.getValue("DOSE_DAYS", row));
        detail.setData("NOTE", tableParm.getValue("NOTE", row));
        detail.setData("RBORDER_DEPT_CODE",
                       tableParm.getValue("RBORDER_DEPT_CODE", row));
        detail.setData("CHKUSER_CODE", tableParm.getValue("CHKUSER_CODE", row));
        detail.setData("URGENT_FLG", tableParm.getValue("URGENT_FLG", row));
        detail.setData("EXEC_FLG", tableParm.getValue("EXEC_FLG", row));
        detail.setData("STANDARD", tableParm.getValue("STANDARD", row));
        // �б�������Ŀ
        detail.setData("ORDER_SEQ_NO", tableParm.getValue("ORDER_SEQ_NO", row));
        detail.setData("SEQ", tableParm.getValue("SEQ", row));
        //============pangben 2012-6-19 start �ڼ����ֶ� ���۱���
        detail.setData("START_DAY", tableParm.getValue("START_DAY", row));
        //�Էѵ��� 2012-7-6 ����ֶ� 
        detail.setData("OWN_PRICE", tableParm.getDouble("OWN_PRICE", row));
        detail.setData("PACK_CODE", tableParm.getValue("PACK_CODE", row));//==pangben 2015-8-12 �ײʹ���
        return detail;
    }

    /**
     * �ַ����ǿ���֤
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(String str) {
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * ����ɾ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deletePackBatch(TParm parm, TConnection conn) {
        String sql = " DELETE FROM CLP_PACK WHERE"
            + " CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") + "'"
            + " AND SCHD_CODE = '" + parm.getValue("SCHD_CODE") + "'"
            + " AND ORDER_FLG = '" + parm.getValue("ORDER_FLG") + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������� ��ʷ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertPackHistoryBatch(TParm parm, TConnection conn) {
    	//��ɾ�������
    	String sql="DELETE FROM CLP_PACK_HISTORY WHERE CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") + "'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
    	if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        sql =
            " INSERT INTO CLP_PACK_HISTORY SELECT * FROM CLP_PACK WHERE"
            + " CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") + "'";
           // + " AND SCHD_CODE = '" + parm.getValue("SCHD_CODE") + "'";
            //ת����ʷ��ʱ��Ҫ�����е���Ϣȫ�������ɾ��orderFlg���޶�
//            + " AND ORDER_FLG = '" + parm.getValue("ORDER_FLG") + "'";
        result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
     * ҽ���ײ� ������� ���ݱ��浽��ʷ��
     * @param table TTable �б�
     * @param parm TParm ���б�����
     * liling 20140909 add
     */
    public TParm savePackHis(TParm parm, TConnection conn) {
    	 Map tableMap = (Map) parm.getData("CLP_PACK");
         TParm tableParm =  new TParm(tableMap);
         TParm result = new TParm();
         int num=0;
         for(int i=tableParm.getCount()-1;i>=0;i--){
        	 if(this.checkInputString(tableParm.getValue("SEQ",i))){
        		 num=tableParm.getInt("SEQ",i)+1;
//        		 System.out.println("num+++ i :"+i+"===="+num);
        		 break;
        	 }
         }
         // ��ȡ�б���ȫ������
         for (int i = 0; i < tableParm.getCount("SEL_FLG") - 1; i++) {
        	 String selFlg = tableParm.getValue("SEL_FLG", i);
        	 if("Y".equals(selFlg)) {
	             TParm detail = parmFormatPackDetail(tableParm, i, parm);
	             //¬���޸�
	             if(this.checkInputString(detail.getValue("ORDER_SEQ_NO"))){//update
	                 detail.setData("ORDER_SEQ_NO", detail.getValue("ORDER_SEQ_NO"));
//	                 System.out.println("update detail++"+detail.getData());
	                 result = this.update("updatePackHis", detail, conn);
//	                 result = this.update("updatePackHis", detail);
	             }else{//insert
	                 detail.setData("ORDER_SEQ_NO",tableParm.getInt("SEQ",i));
//	                 System.out.println("insert detail++"+detail.getData());
	                 result = this.update("insertPackHistory", detail, conn);	                
//	                 result = this.update("insertPackHistory", detail);	                
	             }  
             }        
             // �жϴ���ֵ
             if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
         }
         return result;
	}

    /**
     * �������� ��ǰ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertPackBatch(TParm parm, TConnection conn) {
        Map tableMap = (Map) parm.getData("CLP_PACK");
        TParm tableParm = new TParm(tableMap);
        TParm result = new TParm();
        // ��ȡ�б���ȫ������
        for (int i = 0; i < tableParm.getCount("SEL_FLG") - 1; i++) {
            TParm detail = parmFormatPackDetail(tableParm, i, parm);
            //¬���޸�
            if(this.checkInputString(detail.getValue("ORDER_SEQ_NO"))){
                detail.setData("ORDER_SEQ_NO", detail.getValue("ORDER_SEQ_NO"));
            }else{
                detail.setData("ORDER_SEQ_NO", i + 1);
            }
            result = this.update("insertPack", detail, conn);
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * �汾ͬ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm upgradeVersionForPack(TParm parm, TConnection conn) {
        String sql = " UPDATE CLP_PACK SET VERSION = '" + parm.getValue("VERSION") + "'"
            + " WHERE CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") + "'";
//            + " AND SCHD_CODE = '" + parm.getValue("SCHD_CODE") + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ѯ
     */
    public TParm getDefaultChkTypeByOrderCat1(TParm parm) {
        String sql =
            " SELECT CHKTYPE_CODE ,CHKTYPE_CHN_DESC FROM CLP_CHKTYPE WHERE "
            + " REGION_CODE = '" + parm.getValue("REGION_CODE") + "'"
            + " AND CHKTYPE_CODE = '" + parm.getValue("CHKTYPE_CODE") + "'"
            +" AND ORDER_CAT1='"+parm.getValue("ORDER_CAT1_CODE")+"'"
            + " ORDER BY SEQ";
        //System.out.println("++++++++++++++++++++++++++++ִ��sql:"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ��ҽ�� ���� ��λ Ƶ�� ;�����÷���
     * @param parm TParm
     * @return TParm
     */
    public TParm getDefaultPHABaseInfo(TParm parm) {
        String sql =//DEFAULT_TOTQTY DOSAGE_UNIT
            " SELECT A.MEDI_QTY,A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS,B.DOSAGE_QTY,B.DOSAGE_UNIT AS DISPENSE_UNIT FROM PHA_BASE A,PHA_TRANSUNIT B WHERE "
            + " A.ORDER_CODE=B.ORDER_CODE(+) AND A.ORDER_CODE = '" + parm.getValue("ORDER_CODE") + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����OrderCode�õ�ִ�п�����Ϣ
     * @return TParm
     */
    public TParm getExecDeptWithOrderCode(TParm parm){
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT EXEC_DEPT_CODE FROM SYS_FEE WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ٴ�·����Ŀ�����ȡĬ��ҽ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm getOrderByOrdTypeCode(TParm parm) {
        //Ĭ��ҽ��
        String sql = " SELECT CO.ORDTYPE_CODE, SF. *"
                     + " FROM CLP_ORDERTYPE CO, SYS_FEE SF"
                     + " WHERE CO.ORDER_CODE = SF.ORDER_CODE"
                     + " AND CO.ORDTYPE_CODE = '" + parm.getValue("ORDTYPE_CODE") +
                     "'"
                     + " AND CO.ORDER_FLG = '" + parm.getValue("ORDER_FLG") + "'";
        //¬������ؼ�����
        //�ؼ�����
        if("N".equals(parm.getValue("ORDER_FLG"))){
            sql = " SELECT CO.ORDTYPE_CODE, SF. *"
                         + " FROM CLP_ORDERTYPE CO, CLP_CHKITEM SF"
                         + " WHERE CO.ORDER_CODE = SF.CHKITEM_CODE"
                         + " AND CO.ORDTYPE_CODE = '" + parm.getValue("ORDTYPE_CODE") +
                         "'"
                     + " AND CO.ORDER_FLG = '" + parm.getValue("ORDER_FLG") + "'";
        }
        if (parm.getValue("DEFAULT_FLG") != null &&
            !"".equals(parm.getValue("DEFAULT_FLG").trim())) {
            sql = sql + " AND CO.DEFAULT_FLG = '" + parm.getValue("DEFAULT_FLG") +
                "'";
        }
        //System.out.println("sql:::::::::"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �õ�������Ļ�����Ϣ ��λ��Ƶ��
     * @param orderCode String
     * @return TParm
     */
    public TParm getCheckItemBasicInfo(String orderCode){
        StringBuffer sqlbf=new StringBuffer();
        sqlbf.append("SELECT CLP_UNIT,CLP_QTY FROM CLP_ORDERTYPE WHERE ORDER_CODE='"+orderCode+"'");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
    /**
     * delete
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("REGION_CODE"); i++) {
            result = this.update("deletePack", parm.getRow(i), conn);
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * �ؼ�������Ŀִ��
     * @param parm TParm
     * @return TParm
     */
    public TParm getPack02ListForManagerD(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT 'N' AS SEL_FLG,");
        sqlBuf.append(" CP.ORDER_TYPE,");
        sqlBuf.append(" CP.CHKTYPE_CODE,");
        sqlBuf.append(" CO2.TYPE_CHN_DESC,");
        sqlBuf.append(" CC.CHKITEM_CHN_DESC AS ORDER_CHN_DESC,");
        sqlBuf.append(" CP.DOSE,");
        sqlBuf.append(" CP.DOSE_UNIT,");
        sqlBuf.append(" CP.FREQ_CODE,");
        sqlBuf.append(" CP.ROUT_CODE,");
        sqlBuf.append(" CP.DOSE_DAYS,");
        sqlBuf.append(" CP.NOTE,");
        sqlBuf.append(" CP.CHKUSER_CODE,");
        sqlBuf.append(" CP.RBORDER_DEPT_CODE,");
        sqlBuf.append(" CP.URGENT_FLG,");
        sqlBuf.append(" CP.EXEC_FLG,");
        sqlBuf.append(" CP.STANDARD,");
        sqlBuf.append(" CP.VERSION,");
        sqlBuf.append(" CP.ORDER_SEQ_NO,");
        sqlBuf.append(" CP.SEQ,");
        sqlBuf.append(" CP.ORDER_CODE,");
        sqlBuf.append(" CO1.ORDTYPE_CODE");
        sqlBuf.append(
            " FROM CLP_PACK CP, CLP_CHKITEM CC, CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2");
        sqlBuf.append(" WHERE CP.ORDER_CODE = CC.CHKITEM_CODE");
        sqlBuf.append(" AND CP.CHKTYPE_CODE = CC.CHKTYPE_CODE");
        sqlBuf.append(" AND CP.ORDER_CODE = CO1.ORDER_CODE");
        sqlBuf.append(" AND CP.ORDER_FLG = CO1.ORDER_FLG");
        sqlBuf.append(" AND CO1.ORDTYPE_CODE = CO2.TYPE_CODE");
        sqlBuf.append(" AND CP.REGION_CODE = CO2.REGION_CODE");
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND CP.CLNCPATH_CODE = '");
            sqlBuf.append(parm.getValue("CLNCPATH_CODE") + "'");
        }
        if (parm.getValue("SCHD_CODE") != null &&
            !"".equals(parm.getValue("SCHD_CODE").trim())) {
            sqlBuf.append(" AND CP.SCHD_CODE = '");
            sqlBuf.append(parm.getValue("SCHD_CODE") + "'");
        }
        if (parm.getValue("ORDER_FLG") != null &&
            !"".equals(parm.getValue("ORDER_FLG").trim())) {
            sqlBuf.append(" AND CP.ORDER_FLG = '");
            sqlBuf.append(parm.getValue("ORDER_FLG") + "'");
        }
        if (parm.getValue("ORDER_TYPE") != null &&
                    !"".equals(parm.getValue("ORDER_TYPE").trim())) {
                    sqlBuf.append(" AND CP.ORDER_TYPE = '");
                    sqlBuf.append(parm.getValue("ORDER_TYPE") + "'");
        }
        if (parm.getValue("DEPT_CODE") != null &&
            !"".equals(parm.getValue("DEPT_CODE").trim())) {
            sqlBuf.append(" AND CP.RBORDER_DEPT_CODE = '");
            sqlBuf.append(parm.getValue("DEPT_CODE") + "'");
        }
        if (parm.getValue("CHKUSER_CODE") != null &&
            !"".equals(parm.getValue("CHKUSER_CODE").trim())) {
            sqlBuf.append(" AND CP.CHKUSER_CODE = '");
            sqlBuf.append(parm.getValue("CHKUSER_CODE") + "'");
        }
        sqlBuf.append(
            " ORDER BY CP.CLNCPATH_CODE, CP.SCHD_CODE, CP.ORDER_SEQ_NO");
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ʷ��ҽ����Ϣ
     * @param parm TParm
     * @return TParm
     * liling 20140825 add
     */
	public TParm getPackHistoryList(TParm parm) {
		 StringBuffer sqlBuf = new StringBuffer();
	        sqlBuf.append(" SELECT '' AS SEL_FLG,");
	        sqlBuf.append(" CP.CLNCPATH_CODE,");
	        sqlBuf.append(" CP.SCHD_CODE,");
	        sqlBuf.append(" CP.ORDER_TYPE,");
	        sqlBuf.append(" CP.CHKTYPE_CODE,");
	        sqlBuf.append(" CO2.TYPE_CHN_DESC,");
	        sqlBuf.append(" SF.ORDER_DESC AS ORDER_CHN_DESC,");
	        sqlBuf.append(" CP.DOSE,");
	        sqlBuf.append(" CP.DOSE_UNIT,");
	        sqlBuf.append(" CP.FREQ_CODE,");
	        sqlBuf.append(" CP.ROUT_CODE,");
	        sqlBuf.append(" CP.DOSE_DAYS,");
	        sqlBuf.append(" CP.NOTE,");
	        sqlBuf.append(" CP.CHKUSER_CODE,");
	        sqlBuf.append(" CP.RBORDER_DEPT_CODE,");
	        sqlBuf.append(" CP.URGENT_FLG,");
	        sqlBuf.append(" CP.EXEC_FLG,");
	        sqlBuf.append(" '' AS TOTAL,");
	        sqlBuf.append(" '' AS FEES,");
	        sqlBuf.append(" SF.OWN_PRICE AS OWN_PRICE_REAL,");
	        sqlBuf.append(" '' AS FEES_REAL,");
	        sqlBuf.append(" CP.STANDARD,");
	        sqlBuf.append(" CP.VERSION,");
	        sqlBuf.append(" CP.ORDER_SEQ_NO,");
	        sqlBuf.append(" CP.SEQ,");
	        sqlBuf.append(" CP.ORDER_CODE,");
	        sqlBuf.append(" CO1.ORDTYPE_CODE,");
	        sqlBuf.append(" CP.START_DAY,CASE WHEN P.DOSAGE_QTY IS NULL THEN 0 ELSE P.DOSAGE_QTY END AS DOSAGE_QTY,SF.CAT1_TYPE, CP.DOSE AS MEDI_QTY,");
	        sqlBuf.append(" P.DOSAGE_UNIT AS DISPENSE_UNIT,P.MEDI_UNIT,CP.DOSE_DAYS AS TAKE_DAYS,SF.ACTIVE_FLG,SF.ORDERSET_FLG,CP.PACK_CODE, ");
	        if(parm.getValue("ACTIVE_FLG").equals("N")){//·��ͣ��
	        	sqlBuf.append("CP.OWN_PRICE");
	        }else{
	        	sqlBuf.append("SF.OWN_PRICE");
	        }
	        sqlBuf.append(
	            " FROM CLP_PACK_HISTORY CP, SYS_FEE SF, CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2,PHA_TRANSUNIT P ");
	        sqlBuf.append(" WHERE CP.ORDER_CODE = SF.ORDER_CODE");
	        sqlBuf.append(" AND CP.ORDER_CODE = CO1.ORDER_CODE");
	        sqlBuf.append(" AND CP.ORDER_FLG = CO1.ORDER_FLG");
	        sqlBuf.append(" AND CO1.ORDTYPE_CODE = CO2.TYPE_CODE");
	        sqlBuf.append(" AND CP.ORDER_CODE = P.ORDER_CODE(+)");
	        //20110630 ¬��ɾ�� CLP_ORDTYPE ��region����
//	        sqlBuf.append(" AND CP.REGION_CODE = CO2.REGION_CODE");
	        if (parm.getValue("REGION_CODE") != null &&
	            !"".equals(parm.getValue("REGION_CODE").trim())) {
	            sqlBuf.append(" AND (CP.REGION_CODE = '" +
	                          parm.getValue("REGION_CODE") +
	                          "' OR CP.REGION_CODE IS NULL OR CP.REGION_CODE='')");
	        }
	        if (parm.getValue("CLNCPATH_CODE") != null &&
	            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
	            sqlBuf.append(" AND CP.CLNCPATH_CODE = '");
	            sqlBuf.append(parm.getValue("CLNCPATH_CODE") + "'");
	        }
	        if (parm.getValue("SCHD_CODE") != null &&
	            !"".equals(parm.getValue("SCHD_CODE").trim())) {
	            sqlBuf.append(" AND CP.SCHD_CODE = '");
	            sqlBuf.append(parm.getValue("SCHD_CODE") + "'");
	        }
	        if (parm.getValue("ORDER_FLG") != null &&
	            !"".equals(parm.getValue("ORDER_FLG").trim())) {
	            sqlBuf.append(" AND CP.ORDER_FLG = '");
	            sqlBuf.append(parm.getValue("ORDER_FLG") + "'");
	        }
	        if (parm.getValue("VERSION") != null &&
	            !"".equals(parm.getValue("VERSION").trim())) {
	            sqlBuf.append(" AND VERSION = '" + parm.getValue("VERSION") + "'");
	        }
	        sqlBuf.append(
	            " ORDER BY CP.CLNCPATH_CODE, CP.SCHD_CODE,CP.SEQ,CP.ORDER_TYPE");
//	        System.out.println("111sqlBuf.toString()  is ::"+sqlBuf. toString());
	        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
	            toString()));
	        // �жϴ���ֵ
	        if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
	        return result;
	}

	
}
