package jdo.udd;

import java.util.ArrayList;
import java.util.List;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: ����ҩ���ٴ�Ӧ�ü��</p>
 *
 * <p>Description:����ҩ���ٴ�Ӧ�ü�� </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class UDDAntibioticsMonitTool  extends TJDOTool {
    
     /**
     * ʵ��
     */
    public static UDDAntibioticsMonitTool instanceObject;
    /**
     * �õ�ʵ��
     * @return 
     */
    public static UDDAntibioticsMonitTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new UDDAntibioticsMonitTool();
        }
        return instanceObject;
    }
    
    
    public TParm onQuery(TParm parm){
        String startDate = parm.getValue("START_DATE");
        String endDate = parm.getValue("END_DATE");
        startDate = startDate.substring(0, startDate.length() - 2);
        endDate = endDate.substring(0, endDate.length() - 2);
        
        String conditionSql = "";
        //������
        if(!StringUtil.isNullString(parm.getValue("MR_NO"))){
            conditionSql += " AND A.MR_NO='"+parm.getValue("MR_NO")+"' ";
        }
        //��Ժ����
        String deptCode = parm.getValue("DEPT_CODE");
        if(!StringUtil.isNullString(deptCode)){
            conditionSql += " AND A.OUT_DEPT='"+deptCode+"' ";
        }
        if (!parm.getValue("ALL").equals("Y")) {
            // ����
            String surgery = parm.getValue("SURGERY");
            String dead = parm.getValue("DEAD");
            if (!StringUtil.isNullString(surgery) && surgery.equals("Y")) {
                conditionSql += " AND A.OP_CODE IS NOT NULL ";// ����
                if (!StringUtil.isNullString(dead) && dead.equals("N")) {
                    conditionSql += "  AND A.OUT_TYPE<> '4' ";// ������
                } else if (!StringUtil.isNullString(dead) && dead.equals("Y")) {
                    conditionSql += "  AND A.OUT_TYPE = '4' ";// ����
                }
            }
            // ������
            String unSurgery = parm.getValue("UNSURGERY");
            if (!StringUtil.isNullString(unSurgery) && unSurgery.equals("Y")) {
                conditionSql += " AND A.OP_CODE IS NULL ";// ������
                if (!StringUtil.isNullString(dead) && dead.equals("N")) {
                    conditionSql += "  AND A.OUT_TYPE<> '4' ";// ������
                } else if (!StringUtil.isNullString(dead) && dead.equals("Y")) {
                    conditionSql += "  AND A.OUT_TYPE = '4' ";// ����
                }
            }
        }
        //�пڵȼ� add by wanglong 20130802
        String healLevel=parm.getValue("HEAL_LEVEL");
        if(!healLevel.equals("")){
            if(healLevel.equals("01")){
                conditionSql +="  AND A.HEAL_LV IN ('11','12','13','14') ";
            }else if(healLevel.equals("02")){
                conditionSql +="  AND A.HEAL_LV IN ('21','22','23','24') ";
            }else if(healLevel.equals("03")){
                conditionSql +="  AND A.HEAL_LV IN ('31','32','33','34') ";
            }
        }

        // �Ƿ�ʹ�ÿ���ҩ�� add by wanglong 20130802
        String useAntibiotic = parm.getValue("USE_ANTIBIOTIC");
        String antibioticWayCondition = "";
        String antibioticWay = parm.getValue("ANTIBIOTIC_WAY"); // ������ʶ
        if (useAntibiotic.equals("Y")) {
            //conditionSql += " AND A.CHARGE_16 > 0 ";
            // ������ʶ
            antibioticWayCondition += " AND C.ANTIBIOTIC_CODE IS NOT NULL AND B.ANTIBIOTIC_WAY ='" + antibioticWay + "' ";
        } else {
        	antibioticWayCondition += " AND C.ANTIBIOTIC_CODE IS NULL AND B.ANTIBIOTIC_WAY IS NULL ";//Ϊȷ���鲻�����ݡ���Ϊ��ʹ�ÿ���ҩ��ʱ��Ԥ��ʹ�ö���N
        }
        // ==========add by wanglong 20130807
        String caseNoSql =
                "SELECT A.CASE_NO FROM MRO_RECORD A,ODI_ORDER B,PHA_BASE C WHERE A.CASE_NO=B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE " +
                "  AND A.OUT_DATE BETWEEN TO_DATE('#','yyyy-MM-dd HH24:MI:SS') "
                        + "  AND TO_DATE('#','yyyy-MM-dd HH24:MI:SS') "+conditionSql
                        + antibioticWayCondition+" GROUP BY A.CASE_NO";
        caseNoSql = caseNoSql.replaceFirst("#", startDate);
        caseNoSql = caseNoSql.replaceFirst("#", endDate);
        TParm caseNoResult = new TParm(TJDODBTool.getInstance().select(caseNoSql));
        if (caseNoResult.getErrCode() < 0 || caseNoResult.getCount() < 1) {
            return caseNoResult;
        }
        // ==========add end
        //String caseNoWhere1 = getInStatement("B.CASE_NO",caseNoResult);
        String caseNoWhere2 = getInStatement("A.CASE_NO",caseNoResult);
        String sql = "SELECT '' CHOOSE, A.IN_DATE, A.OUT_DATE, A.OUT_DEPT, A.MR_NO, A.PAT_NAME,A.SEX, A.AGE, "+  
                    "   (SELECT ( CASE WHEN G.WEIGHT > 0 THEN TO_CHAR(G.WEIGHT) ELSE ' ' END ) " +
                    "   FROM ADM_INP G WHERE A.CASE_NO=G.CASE_NO AND  A.MR_NO=G.MR_NO) NB_WEIGHT, "+//����
                    "(SELECT A.INTE_DIAG_CODE || ' ' || B.ICD_CHN_DESC FROM SYS_DIAGNOSIS B " +
                    "  WHERE B.ICD_CODE=A.INTE_DIAG_CODE AND B.ICD_TYPE='W') INTE_DIAG_CODE, "+  //���1
                    "(SELECT A.OUT_DIAG_CODE1 || ' ' || C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C " +
                    "  WHERE C.ICD_CODE=A.OUT_DIAG_CODE1 AND C.ICD_TYPE='W') OUT_DIAG_CODE1, "+  //���2
                    "(SELECT A.OUT_DIAG_CODE2 || ' ' || D.ICD_CHN_DESC FROM SYS_DIAGNOSIS D " +
                    "  WHERE D.ICD_CODE=A.OUT_DIAG_CODE2 AND D.ICD_TYPE='W') OUT_DIAG_CODE2, "+  //���3
                    "(SELECT A.OUT_DIAG_CODE3 || ' ' || E.ICD_CHN_DESC FROM SYS_DIAGNOSIS E " +
                    "  WHERE E.ICD_CODE=A.OUT_DIAG_CODE3 AND E.ICD_TYPE='W' ) OUT_DIAG_CODE3, "+  //���4
                    "(SELECT A.OUT_DIAG_CODE4 || ' ' || F.ICD_CHN_DESC FROM SYS_DIAGNOSIS F " +
                    "  WHERE F.ICD_CODE=A.OUT_DIAG_CODE4 AND F.ICD_TYPE='W') OUT_DIAG_CODE4, "+  //���5
                    " A.ALLEGIC, " +//����ʷ
                    "A.SUM_TOT," +//סԺ�ܷ���
                    "(A.CHARGE_03 + A.CHARGE_04 + A.CHARGE_05 + A.CHARGE_06) MEDICAL_TOT," +//ҩƷ�ܷ���
                    "TO_CHAR(A.CHARGE_03,'FM999990.09') CHARGE_03, "+  //�������ܷ���
                    "(SELECT A.OP_CODE || ' ' || H.OPT_CHN_DESC FROM SYS_OPERATIONICD H " +
                    "  WHERE A.OP_CODE=H.OPERATION_ICD) OP_CODE," +//��������
                    "(SELECT A.HEAL_LV || ' ' || I.CHN_DESC FROM SYS_DICTIONARY I " +
                    "  WHERE I.ID=A.HEAL_LV  AND I.GROUP_ID='MRO_HEALTHLEVEL') HEAL_LV, " +//�п����
                    //modify by wanglong 20130801
                    " 0.00 DDD_SUM," +//DDD�ۻ���
                    " A.CASE_NO,A.REAL_STAY_DAYS," +
//                    " 0 DDD, " +//������ǿ��
//                    " (SELECT CASE WHEN SUM(C.CO_USE) > 0 THEN 'Y' ELSE 'N' END " +
//                    "   FROM (SELECT B.CASE_NO, CASE WHEN COUNT(DISTINCT ORDER_CODE) > 1 THEN 1 ELSE 0 END CO_USE " +
//                    "           FROM ODI_DSPNM B " +
//                    "          WHERE B.CAT1_TYPE = 'PHA' " +
//                    "            AND B.ANTIBIOTIC_CODE IS NOT NULL " +
//                    "            AND (#) " +
//                    "            AND NS_EXEC_DATE IS NOT NULL " +
//                    "       GROUP BY CASE_NO, TRUNC(B.NS_EXEC_DATE)) C " +
//                    "  WHERE A.CASE_NO = C.CASE_NO) CO_USE," +//����ʹ��
                    " 'N' CO_USE,'N' PRE_USE ,"+
                    " CASE WHEN A.RT_FLG = 'Y' THEN 'Y' ELSE 'N' END RT_FLG," +//ʱ������
                    " CASE WHEN A.RC_FLG = 'Y' THEN 'Y' ELSE 'N' END RC_FLG "+//��������
                    " FROM MRO_RECORD A " +
                    " WHERE  A.OUT_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd HH24:MI:SS') " +
                    "                      AND TO_DATE('"+endDate+"','yyyy-MM-dd HH24:MI:SS') AND (#) "+conditionSql +
                    " ORDER BY A.IN_DATE ";//������Ժ��������
        sql = sql.replaceFirst("#", caseNoWhere2);
        //System.out.println("============queryPatTable====SSSSSSSSSSS====="+sql);
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() < 1) {
            return result;
        }
        String coSql =
                "SELECT B.CASE_NO FROM ODI_DSPNM B WHERE B.CAT1_TYPE = 'PHA' "
                        + " AND B.ANTIBIOTIC_CODE IS NOT NULL AND B.CASE_NO = '#' AND NS_EXEC_DATE IS NOT NULL "
                        + " GROUP BY CASE_NO, TRUNC(B.NS_EXEC_DATE) HAVING COUNT(DISTINCT ORDER_CODE) > 1";
        for (int i = 0; i < caseNoResult.getCount(); i++) {
            TParm coResult =
                    new TParm(TJDODBTool.getInstance().select(coSql.replaceFirst("#", caseNoResult.getValue("CASE_NO", i))));
            if (coResult.getErrCode() < 0) {
                return coResult;
            }
            result.setData("CO_USE", i, coResult.getCount() > 0 ? "Y" : "N");
        }
        return result;
    }
    
    /**
     * ����
     * @return
     */
    public TParm onSave(TParm parm) {//add by wanglong 20130802
        String updateSql = " UPDATE MRO_RECORD SET RT_FLG = '#', RC_FLG = '#' WHERE CASE_NO = '#' ";
        String realSql = "";
        String[] sql = new String[parm.getCount("MR_NO")];
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            realSql = updateSql.replaceFirst("#", parm.getValue("RT_FLG", i));
            realSql = realSql.replaceFirst("#", parm.getValue("RC_FLG", i));
            realSql = realSql.replaceFirst("#", parm.getValue("CASE_NO", i));
            sql[i] = realSql;
        }
        return new TParm(TJDODBTool.getInstance().update(sql));
    }
    
    /**
     * �ڶ�ҳǩ����CASE_NO��ѯ
     * @param parm
     * @return
     */
    public TParm onQueryMedical(TParm parm ){
        String caseNoCondition = "";
        caseNoCondition = getInStatement("A.CASE_NO", parm);
        String sql = "SELECT A.MR_NO, B.PAT_NAME PAT_NAME,A.ORDER_DESC,C.ANTIBIOTIC_DESC ANTIBIOTIC_CODE,TO_CHAR(A.MEDI_QTY,'FM999990.09')||D.UNIT_CHN_DESC MEDI_QTY," +
                            "A.FREQ_CODE,A.ROUTE_CODE,TO_CHAR(A.ACUMMEDI_QTY,'FM999990.09')||D.UNIT_CHN_DESC ACUMMEDI_QTY," +
                            "(CASE WHEN TO_CHAR(A.DC_DATE,'YYYY/MM/DD HH:MM') <> ' ' THEN (TO_CHAR(A.EFF_DATE,'YYYY/MM/DD HH:MM')||'-'||TO_CHAR(A.DC_DATE,'YYYY/MM/DD HH:MM')) ELSE TO_CHAR(A.EFF_DATE,'YYYY/MM/DD HH:MM') END )   EDDATE, "+
                            "( SELECT O.ORDER_DESC FROM ODI_ORDER O WHERE O.CASE_NO= A.CASE_NO AND  O.LINK_NO=A.LINK_NO AND O.LINKMAIN_FLG='Y' AND ROWNUM=1 ) ORDER_DESC_Y," +
                            "CASE WHEN A.ANTIBIOTIC_WAY='01' THEN 'Ԥ��' WHEN A.ANTIBIOTIC_WAY='02' THEN '����' ELSE '' END ANTIBIOTIC_WAY "+
                     "FROM ODI_ORDER A,SYS_PATINFO B,SYS_ANTIBIOTIC C,SYS_UNIT D "+
                     "WHERE  "+caseNoCondition+ "  AND   A.CAT1_TYPE='PHA' AND A.ANTIBIOTIC_CODE IS NOT NULL AND " +
                           "A.MR_NO= B.MR_NO AND "+
                           "A.ANTIBIOTIC_CODE=C.ANTIBIOTIC_CODE(+) AND  "+
                           "A.MEDI_UNIT=D.UNIT_CODE(+) ";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * ���ɳ���1000Ԫ�ص�CASE_NO IN()���
     * @param name
     * @param Nos
     * @return
     */
    public static String getInStatement(String name, TParm Nos) {//modify by wanglong 20130806
        if (Nos.getCount("CASE_NO") < 1) {
            return " 1=1 ";
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(name + " IN ('");
        for (int i = 0; i < Nos.getCount("CASE_NO") ; i++) {
            inStr.append(Nos.getValue("CASE_NO", i));
            if ((i + 1) != Nos.getCount("CASE_NO")) {
                if ((i + 1) % 999 != 0) {
                    inStr.append("','");
                } else if (((i + 1) % 999 == 0)) {
                    inStr.append("') OR " + name + " IN ('");
                }
            }
        }
        inStr.append("')");
        return inStr.toString();
    }


}
