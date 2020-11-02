package action.med;

import com.dongyang.action.*;

import jdo.adm.ADMInpTool;
import jdo.hl7.BILJdo;
import jdo.hrm.HRMCompanyReportTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import jdo.med.MEDApplyTool;
import jdo.med.MedToLedTool;
import jdo.odi.OdiMainTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class MedAction extends TAction {
    /**
     * 设备请购保存
     * @param parm TParm
     * @return TParm
     */
    public TParm saveMedApply(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = MEDApplyTool.getInstance().saveMedApply(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    public TParm saveMedApply1(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = MEDApplyTool.getInstance().saveMedApply(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        result = MEDApplyTool.getInstance().saveMedApplyPrtFlg(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 更新
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {// add by wanglong 20131112
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("参数错误");
            return result;
        }
        TConnection conn = getConnection(); // 取得链接
        result = MedToLedTool.getInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        String bilPoint = (String) OdiMainTool.getInstance().getOdiSysParmData("BIL_POINT");//add by wanglong 20140123
        if (parm.getValue("ADM_TYPE").equals("I") && bilPoint.equals("2")) {// add by wanglong 20131209
            TParm orderParm = parm.getParm("ORDER");
            String medApplyNo = "";
            for (int i = 0; i < orderParm.getCount(); i++) {
                medApplyNo += "'" + orderParm.getValue("APPLICATION_NO", i) + "',";
            }
            medApplyNo = medApplyNo.substring(0, medApplyNo.length() - 1);
            String sql =
                    "SELECT B.* FROM ODI_ORDER A,ODI_DSPNM B    "
                            + " WHERE A.CASE_NO = B.CASE_NO     "
                            + "   AND A.ORDER_NO = B.ORDER_NO   "
                            + "   AND A.ORDER_SEQ = B.ORDER_SEQ "
                            + "   AND A.CASE_NO = '#' AND A.MED_APPLY_NO in (#) ";
            sql = sql.replaceFirst("#", orderParm.getValue("CASE_NO", 0));
            sql = sql.replaceFirst("#", medApplyNo);
            TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (dataParm.getErrCode() != 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            if (dataParm.getCount() <= 0) {
                conn.rollback();
                conn.close();
                result.setErr(-2, "没有查询到计费医嘱");
                return result;
            }
            for (int i = 0; i < dataParm.getCount(); i++) {
                dataParm.setData("OPT_USER", i, orderParm.getValue("OPT_USER", 0));
                dataParm.setData("OPT_TERM", i, orderParm.getValue("OPT_TERM", 0));
            }
            dataParm.setData("MED_APPLY_LUMP_FLG",null!=parm.getValue("MED_APPLY_LUMP_FLG")&&parm.getValue("MED_APPLY_LUMP_FLG").length()>0?
            		parm.getValue("MED_APPLY_LUMP_FLG"):"");
            result = BILJdo.getInstance().onIBilFee(conn, dataParm, parm.getValue("TYPE"));// add by wanglong 20131209
            if (result.getErrCode() != 0) {
                conn.rollback();
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * 保存
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm) {//wanglong add 20140422
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("参数错误");
            return result;
        }
        TConnection conn = getConnection(); // 取得链接
        result = MedToLedTool.getInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
