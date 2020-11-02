package jdo.inf;

import java.util.HashMap;
import java.util.Map;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 感染控制感染病例筛选数据库工具类</p>
 *
 * <p>Description: 感染控制感染病例筛选数据库工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFCaseTool extends TJDOTool{

    /**
     * 构造器
     */
    public INFCaseTool() {
        setModuleName("inf\\INFCaseModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static INFCaseTool instanceObject;

    /**
     * 得到实例
     * @return INFCaseTool
     */
    public static INFCaseTool getInstance() {
        if (instanceObject == null) instanceObject = new INFCaseTool();
        return instanceObject;
    }

    /**
     * 感染病历登记病人资料
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterPatInfo(TParm parm) {
        TParm result = query("caseRegisterPatInfo", parm);
        return result;
    }

    /**
     * 感染病历登记诊断信息
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterDiag(TParm parm) {
        TParm result = query("caseRegisterDiag", parm);
        return result;
    }

    /**
     * 感染病历登记感染信息
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterCase(TParm parm) {
        TParm result = query("caseRegisterCase", parm);
        return result;
    }

    /**
     * 取得病患感染原因
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfReasonByCaseInfNo(TParm parm) {
        TParm result = query("selectInfReasonByCaseInfNo", parm);
        return result;
    }

    /**
     * 取得病患介入信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIntvoprecByCaseInfNo(TParm parm) {
        TParm result = query("selectInfIntvoprecByCaseInfNo", parm);
        return result;
    }

    /**
     * 取得病患试验结果信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfResultByCaseInfNo(TParm parm) {
        TParm result = query("selectInfResultByCaseInfNo", parm);
        return result;
    }

    /**
     * 取得病患感染部位诊断信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfICDPartByCaseInfNo(TParm parm) {// add by wanglong 20140217
        TParm result = query("selectInfICDPartByCaseInfNo", parm);
        return result;
    }

    /**
     * 取得病患侵入性操作信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIoByCaseInfNo(TParm parm) {// add by wanglong 20140217
        TParm result = query("selectInfIoByCaseInfNo", parm);
        return result;
    }

    /**
     * 从ODI_ORDER表取得病患侵入性操作信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIoFromOdiByCase(TParm parm) {// add by wanglong 20140217
        TParm result = query("selectInfIoFromOdiByCase", parm);
        return result;
    }

    /**
     * 写入感控记录信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfCase(TParm parm, TConnection connection) {
        TParm result = update("insertInfCase", parm, connection);
        return result;
    }

    /**
     * 更新感控记录信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCase(TParm parm, TConnection connection) {
        TParm result = update("updateInfCase", parm, connection);
        return result;
    }

    /**
     * 删除易感染因素信息
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInfInfreasrcd(TParm parm, TConnection connection) {
        TParm result = update("deleteInfInfreasrcd", parm, connection);
        return result;
    }

    /**
     * 写入易感染因素信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfInfreasrcd(TParm parm, TConnection connection) {
        TParm result = update("insertInfInfreasrcd", parm, connection);
        return result;
    }

    /**
     * 删除感控实验结果信息
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInfantibiotest(TParm parm, TConnection connection) {
        TParm result = update("deleteInfantibiotest", parm, connection);
        return result;
    }

    /**
     * 写入感染实验结果信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfantibiotest(TParm parm, TConnection connection) {
        TParm result = update("insertInfantibiotest", parm, connection);
        return result;
    }

    /**
     * 删除感染部位诊断信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteInfICDPart(TParm parm, TConnection conn) {// add by wanglong 20140217
        TParm result = update("deleteInfICDPart", parm, conn);
        return result;
    }

    /**
     * 写入感染部位诊断信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertInfICDPart(TParm parm, TConnection conn) {// add by wanglong 20140217
        TParm result = update("insertInfICDPart", parm, conn);
        return result;
    }

    /**
     * 删除侵入性操作信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteInfIO(TParm parm, TConnection conn) {// add by wanglong 20140217
        TParm result = update("deleteInfIO", parm, conn);
        return result;
    }

    /**
     * 写入侵入性操作信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertInfIO(TParm parm, TConnection conn) {// add by wanglong 20140217
        TParm result = update("insertInfIO", parm, conn);
        return result;
    }

    /**
     * 取得感控等级编号
     * @return String
     */
    public String getInfNo() {
        return SystemTool.getInstance().getNo("ALL", "INF", "INF_NO", "INF_NO");
    }

    /**
     * 取得最大感控序号
     * @param parm TParm
     * @return TParm
     */
    public TParm selectMaxInfNo(TParm parm) {
        TParm result = query("selectMaxInfNo", parm);
        return result;
    }

    /**
     * 删除抗生素信息
     * 
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteAntibiotrcd(TParm parm, TConnection connection) {
        TParm result = update("deleteAntibiotrcd", parm, connection);
        return result;
    }

    /**
     * 写入抗生素信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertAntibiotrcd(TParm parm, TConnection connection) {
        TParm result = update("insertAntibiotrcd", parm, connection);
        return result;
    }

    /**
     * 查询抗生素信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAntibiotrcd(TParm parm) {
        TParm result = query("selectAntibiotrcd", parm);
        return result;
    }

    /**
     * 查询介入操作信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInvOpt(TParm parm) {// add by wanglong 20131105
        TParm result = query("selectInvOpt", parm);
        return result;
    }

    /**
     * 查询最后一次住院记录
     * @param parm TParm
     * @return TParm
     */
    public TParm selectLastAdmCase(TParm parm) {
        TParm result = query("selectLastAdmCase", parm);
        return result;
    }

    /**
     * 查询上报感控信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfCaseReport(TParm parm) {
        TParm result = query("selectInfCaseReport", parm);
        return result;
    }

    /**
     * 更新感控上报信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCaseReport(TParm parm, TConnection connection) {
        TParm result = update("updateInfCaseReport", parm, connection);
        return result;
    }

    /**
     * 更行感控记录表取消注记
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateInfCaseCancelFlg(TParm parm, TConnection connection) {
        TParm result = update("updateInfCaseCancelFlg", parm, connection);
        return result;
    }

    /**
     * 取得感染病历报告卡信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfCaseCardInf(TParm parm) {
        TParm result = query("selectInfCaseCardInf", parm);
        return result;
    }

    /**
     * 取得病患感染病例报告卡感染原因信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfReasonForCard(TParm parm) {
        TParm result = query("selectInfReasonForCard", parm);
        return result;
    }

    /**
     * 更新病案感控诊断
     * @param parm TParm
     * @return TParm
     */
    public TParm updateMROINFDiag(TParm parm, TConnection connection) {
        TParm result = update("updateMROINFDiag", parm, connection);
        return result;
    }

    /**
     * 保存
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {// add by wanglong 20140224
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null) {
            return result;
        }
        if (sql.length < 1) {
            return result;
        }
        for (String tempSql : sql) {
            result = new TParm(TJDODBTool.getInstance().update(tempSql, conn));
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 导入感控中间表信息
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDailyRecData(TParm parmObj, TConnection conn) {
        TParm parm = parmObj.getParm("SQL"); // SQL参数
        // TParm dept = parmObj.getParm("DEPT"); //部门参数
        TParm result = new TParm();
        // 先删除原有数据
        result = this.deleteINF_Daily_Rec(parm.getValue("STADATE"), conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        result = this.insertINF_Daily_Rec(parmObj, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除感控中间档信息
     * @param POST_DATE
     * @param conn
     * @return
     */
    public TParm deleteINF_Daily_Rec(String POST_DATE, TConnection conn) {
        String sql = "DELETE FROM INF_DAILY_REC WHERE POST_DATE='#'".replaceFirst("#", POST_DATE);
        TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入感控中间档 数据
     * @param parmObj
     * @param conn
     * @return
     */
    public TParm insertINF_Daily_Rec(TParm parmObj, TConnection conn) {
        TParm parm = parmObj.getParm("SQL"); // SQL参数
        String staDate = parm.getValue("STADATE");
        String sql =
                "SELECT TO_CHAR( A.BILL_DATE, 'YYYYMMDD') POST_DATE, A.DEPT_CODE, A.STATION_CODE, A.CASE_NO, B.IPD_NO, B.MR_NO, B.BED_NO, "
                        + "       SUM(CASE WHEN C.ORD_SUPERVISION = '04' THEN 1 ELSE 0 END) VI_NUM, "
                        + "       SUM(CASE WHEN C.ORD_SUPERVISION = '03' THEN 1 ELSE 0 END) BM_NUM, "
                        + "       SUM(CASE WHEN C.ORD_SUPERVISION = '02' THEN 1 ELSE 0 END) LU_NUM, B.MAINDIAG, D.BORNWEIGHT "
                        + "  FROM IBS_ORDD A, ADM_INP B, SYS_FEE C, SUM_NEWARRIVALSIGN D "
                        + " WHERE A.CASE_NO = B.CASE_NO       "
                        + "   AND A.ORDER_CODE = C.ORDER_CODE "
                        + "   AND C.ORD_SUPERVISION IN ('02', '03', '04') "
                        + "   AND A.CASE_NO = D.CASE_NO(+) "
                        + "   AND A.BILL_DATE BETWEEN TO_DATE( '#', 'YYYYMMDD') AND TO_DATE( '#235959', 'YYYYMMDDHH24MISS') "
                        + " GROUP BY TO_CHAR(A.BILL_DATE,'YYYYMMDD'),A.DEPT_CODE,A.STATION_CODE,A.CASE_NO,B.IPD_NO,B.MR_NO,B.BED_NO,B.MAINDIAG,D.BORNWEIGHT";
        sql = sql.replaceAll("#", staDate);
        TParm data = new TParm(TJDODBTool.getInstance().select(sql));
        TDS dailyRec = new TDS();
        dailyRec.setSQL("SELECT * FROM INF_DAILY_REC");
        dailyRec.retrieve();
        for (int i = 0; i < data.getCount("POST_DATE"); i++) {
            int row = dailyRec.insertRow();
            dailyRec.setItem(row, "POST_DATE", data.getData("POST_DATE", i));
            dailyRec.setItem(row, "DEPT_CODE", data.getData("DEPT_CODE", i));
            dailyRec.setItem(row, "STATION_CODE", data.getData("STATION_CODE", i));
            dailyRec.setItem(row, "CASE_NO", data.getData("CASE_NO", i));
            dailyRec.setItem(row, "IPD_NO", data.getData("IPD_NO", i));
            dailyRec.setItem(row, "MR_NO", data.getData("MR_NO", i));
            dailyRec.setItem(row, "BED_NO", data.getData("BED_NO", i));
            dailyRec.setItem(row, "VI_NUM", data.getData("VI_NUM", i));
            dailyRec.setItem(row, "BM_NUM", data.getData("BM_NUM", i));
            dailyRec.setItem(row, "LU_NUM", data.getData("LU_NUM", i));
            dailyRec.setItem(row, "MAINDIAG", data.getData("MAINDIAG", i));
            dailyRec.setItem(row, "BORNWEIGHT", data.getData("BORNWEIGHT", i));
            dailyRec.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
            dailyRec.setItem(row, "OPT_DATE", dailyRec.getDBTime());
            dailyRec.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
            dailyRec.setActive(row, true);
        }
        String[] insertSql = dailyRec.getUpdateSQL();
        if (insertSql.length < 1) {
            return new TParm();
        }
        TParm inParm = new TParm();
        Map<String, String[]> inMap = new HashMap<String, String[]>();
        inMap.put("SQL", insertSql);
        inParm.setData("IN_MAP", inMap);
        TParm result = onSave(inParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
}
