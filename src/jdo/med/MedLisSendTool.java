package jdo.med;

import java.util.HashMap;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;


/**
 * <p>Title: 送检清单工具类 </p>
 *
 * <p>Description: 送检清单工具类 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: BLueCore</p>
 *
 * @author wanglong 2014.04.22
 * @version 1.0
 */
public class MedLisSendTool extends TJDOTool {
	
    public static MedLisSendTool instanceObject;// 实例
    
    /**
     * 得到实例
     * @return MedLisSendTool
     */
    public static MedLisSendTool getInstance() {
        if (instanceObject == null) instanceObject = new MedLisSendTool();
        return instanceObject;
    }
  
    /**
     * 查询送检清单（门急诊）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectLisDetailByOE(TParm parm) {
        String sql =
                "SELECT DISTINCT 'Y' AS FLG, D.PAT_NAME, A.MR_NO, A.ORDER_DESC, A.MED_APPLY_NO,"
                        + "        B.BLOOD_DATE, A.DR_NOTE, B.LIS_RE_DATE, B.LIS_RE_USER,B.SEND_USER, "
                        + "        A.CASE_NO, A.RX_NO, A.SEQ_NO, A.CAT1_TYPE "
                        + "   FROM OPD_ORDER A, MED_APPLY B, REG_PATADM C, SYS_PATINFO D "
                        + "  WHERE A.CAT1_TYPE = 'LIS'                "
                        + "    AND A.HIDE_FLG = 'N'                   "
                        + "    AND A.EXEC_FLG = 'Y'                   "
                        + "    AND A.CASE_NO = B.CASE_NO              "
                        + "    AND A.MED_APPLY_NO = B.APPLICATION_NO  " 
                        + "    AND A.SEQ_NO = B.SEQ_NO                "
                        + "    AND A.CASE_NO = C.CASE_NO              "
                        + "    AND C.MR_NO = D.MR_NO   #  @  !  &     "
                        + " ORDER BY A.MED_APPLY_NO, A.SEQ_NO";
        if (parm.getBoolean("RE_FLG") == true) {
            sql = sql.replaceFirst("#", " AND B.LIS_RE_DATE IS NOT NULL # ");
            if (!StringUtil.isNullString(parm.getValue("RE_START_DATE"))) {
                sql =
                        sql.replaceFirst("#",
                                         " AND B.LIS_RE_DATE BETWEEN TO_DATE(!,'YYYYMMDDHH24MISS') AND TO_DATE(@,'YYYYMMDDHH24MISS') "
                                                 .replaceFirst("!", parm.getValue("RE_START_DATE"))
                                                 .replaceFirst("@", parm.getValue("RE_END_DATE")));
            } else {
                sql = sql.replaceFirst("#", "");
            }
        } else {
            sql = sql.replaceFirst("#", " AND B.LIS_RE_DATE IS NULL # ");
            if (!StringUtil.isNullString(parm.getValue("START_DATE"))) {
                sql =
                        sql.replaceFirst("#",
                                         " AND A.ORDER_DATE BETWEEN TO_DATE(!,'YYYYMMDDHH24MISS') AND TO_DATE(@,'YYYYMMDDHH24MISS') "
                                                 .replaceFirst("!", parm.getValue("START_DATE"))
                                                 .replaceFirst("@", parm.getValue("END_DATE")));
            } else {
                sql = sql.replaceFirst("#", "");
            }
        }
        if (!StringUtil.isNullString(parm.getValue("CLINICAREA_CODE"))) {
            sql =
                    sql.replaceFirst("@", " AND C.CLINICAREA_CODE = '#' ".replaceFirst("#", parm
                            .getValue("CLINICAREA_CODE")));
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("LIS_RE_USER"))) {
            sql =
                    sql.replaceFirst("!", " AND B.LIS_RE_USER = '#' ".replaceFirst("#", parm
                            .getValue("LIS_RE_USER")));
        } else {
            sql = sql.replaceFirst("!", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MED_APPLY_NO"))) {
            sql =
                    sql.replaceFirst("&", " AND B.APPLICATION_NO = '#' ".replaceFirst("#", parm
                            .getValue("MED_APPLY_NO")));
        } else {
            sql = sql.replaceFirst("&", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 查询送检清单（住院）
     * @param parm TParm
     * @return TParm
     */
	public TParm selectLisDetailByI(TParm parm) {
        String sql =
                "SELECT DISTINCT 'Y' AS FLG, C.BED_NO_DESC AS BED_NO, D.PAT_NAME, A.MR_NO, A.ORDER_DESC, B.MED_APPLY_NO,"
                        
        				//modify by yangjj 20150529
        				+ "      E.NS_EXEC_DATE_REAL AS NS_EXEC_DATE, "
        				//+ "      A.NS_EXEC_DATE, " 
                       
        				+ "      B.DR_NOTE, A.LIS_RE_DATE, A.LIS_RE_USER,A.SEND_USER, "
                        + "      A.CASE_NO, A.ORDER_NO, A.ORDER_SEQ, A.START_DTTM, A.CAT1_TYPE "
                        + " FROM ODI_DSPNM A, ODI_ORDER B, SYS_BED C, SYS_PATINFO D "
                        
                        //modify by yangjj 20150529
                        + " ,ODI_DSPND E "
                        
                        + " WHERE A.CASE_NO = B.CASE_NO                    "
                        + " AND A.ORDER_NO = B.ORDER_NO                    "
                        + " AND A.ORDER_SEQ = B.ORDER_SEQ                  "
                        
                        //modify by yangjj 20150529
                        + " AND A.CASE_NO = E.CASE_NO                          "
                        + " AND A.ORDER_NO = E.ORDER_NO                    "
                        + " AND A.ORDER_SEQ = E.ORDER_SEQ                  "
                        
                        + " AND A.BED_NO = C.BED_NO                        "
                        + " AND A.MR_NO = D.MR_NO                          "
                        + " AND A.CAT1_TYPE = 'LIS'                        "
                        + " AND A.HIDE_FLG = 'N'                           "
                        + " AND A.NS_EXEC_CODE IS NOT NULL  #  @  !  &  ~  "
                        + "ORDER BY B.MED_APPLY_NO";
        if (parm.getBoolean("RE_FLG") == true) {
            sql = sql.replaceFirst("#", " AND A.LIS_RE_DATE IS NOT NULL # ");
            if (!StringUtil.isNullString(parm.getValue("RE_START_DATE"))) {
                sql =
                        sql.replaceFirst("#",
                                         " AND A.LIS_RE_DATE BETWEEN TO_DATE(!,'YYYYMMDDHH24MISS') AND TO_DATE(@,'YYYYMMDDHH24MISS') "
                                                 .replaceFirst("!", parm.getValue("RE_START_DATE"))
                                                 .replaceFirst("@", parm.getValue("RE_END_DATE")));
            } else {
                sql = sql.replaceFirst("#", "");
            }
        } else {
            sql = sql.replaceFirst("#", " AND A.LIS_RE_DATE IS NULL # ");
            if (!StringUtil.isNullString(parm.getValue("START_DATE"))) {
                sql =
                        sql.replaceFirst("#",
                                         " AND A.ORDER_DATE BETWEEN TO_DATE(!,'YYYYMMDDHH24MISS') AND TO_DATE(@,'YYYYMMDDHH24MISS') "
                                                 .replaceFirst("!", parm.getValue("START_DATE"))
                                                 .replaceFirst("@", parm.getValue("END_DATE")));
            } else {
                sql = sql.replaceFirst("#", "");
            }
        }
        if (!StringUtil.isNullString(parm.getValue("STATION_CODE"))) {
            sql =
                    sql.replaceFirst("@", " AND B.STATION_CODE = '#' ".replaceFirst("#", parm
                            .getValue("STATION_CODE")));
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("LIS_RE_USER"))) {
            sql =
                    sql.replaceFirst("!", " AND A.LIS_RE_USER = '#' ".replaceFirst("#", parm
                            .getValue("LIS_RE_USER")));
        } else {
            sql = sql.replaceFirst("!", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MED_APPLY_NO"))) {
            sql =
                    sql.replaceFirst("&", " AND B.MED_APPLY_NO = '#' ".replaceFirst("#", parm
                            .getValue("MED_APPLY_NO")));
        } else {
            sql = sql.replaceFirst("&", "");
        }
        if(!StringUtil.isNullString(parm.getValue("SEND_USER"))){
        	sql = sql.replaceFirst("~", "AND A.SEND_USER='#' ".replaceFirst("#",parm.getValue("SEND_USER")));
        }else{
        	sql = sql.replaceFirst("~", "");
        }
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    
	/**
	 * 更新MED_APLLY表的接收人员信息
	 * @param parm
	 */
    public TParm updateMedApplyLisData(TParm parm) {
        TParm result = new TParm();
        if (parm.getCount() <= 0) {
            result.setErr(-1, "没有要保存的数据");
            return result;
        }
        String[] sql = new String[]{};
        for (int i = 0; i < parm.getCount(); i++) {
            String updateSql =
                    "UPDATE MED_APPLY SET LIS_RE_DATE = SYSDATE, LIS_RE_USER = '@', OPT_USER = '!', OPT_DATE = SYSDATE, OPT_TERM = '#' ,SEND_USER='~' "
                            + " WHERE APPLICATION_NO = '&'";
            updateSql = updateSql.replaceFirst("@", parm.getValue("LIS_RE_USER", i));
            updateSql = updateSql.replaceFirst("!", parm.getValue("OPT_USER", i));
            updateSql = updateSql.replaceFirst("#", parm.getValue("OPT_TERM", i));
            updateSql = updateSql.replaceFirst("&", parm.getValue("MED_APPLY_NO", i));
            updateSql = updateSql.replaceFirst("~", parm.getValue("SEND_USER", i));
            sql = StringTool.copyArray(sql, new String[]{updateSql });
        }
        if (sql.length <= 0) {
            result.setErr(-1, "没有需要操作的数据");
            return result;
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        result = TIOM_AppServer.executeAction("action.med.MedAction", "onSave", inParm);
        return result;
    }

    /**
     * 更新ODI_DSPNM表的接收人员信息
     * @param parm
     */
    public TParm updateOdidspnmLisData(TParm parm) {
        TParm result = new TParm();
        if (parm.getCount() <= 0) {
            result.setErr(-1, "没有要保存的数据");
            return result;
        }
        String[] sql = new String[]{};
        for (int i = 0; i < parm.getCount(); i++) {
            String updateSql =
                    "UPDATE ODI_DSPNM SET LIS_RE_DATE = SYSDATE, LIS_RE_USER = '@', OPT_USER = '!', OPT_DATE = SYSDATE, OPT_TERM = '#' , SEND_USER='^' "
                            + " WHERE CASE_NO = '&' AND ORDER_NO = '~' AND ORDER_SEQ = $ AND START_DTTM = '?'";
            updateSql = updateSql.replaceFirst("@", parm.getValue("LIS_RE_USER", i));
            updateSql = updateSql.replaceFirst("!", parm.getValue("OPT_USER", i));
            updateSql = updateSql.replaceFirst("#", parm.getValue("OPT_TERM", i));
            updateSql = updateSql.replaceFirst("\\^", parm.getValue("SEND_USER", i));
            updateSql = updateSql.replaceFirst("&", parm.getValue("CASE_NO", i));
            updateSql = updateSql.replaceFirst("~", parm.getValue("ORDER_NO", i));
            updateSql = updateSql.replaceFirst("\\$", parm.getValue("ORDER_SEQ", i));
            updateSql = updateSql.replaceFirst("\\?", parm.getValue("START_DTTM", i));
            sql = StringTool.copyArray(sql, new String[]{updateSql });
        }
        if (sql.length <= 0) {
            result.setErr(-1, "没有需要操作的数据");
            return result;
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        result = TIOM_AppServer.executeAction("action.med.MedAction", "onSave", inParm);
        return result;
    }

}
