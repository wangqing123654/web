package jdo.erd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;

/**
 * <p>Title: 住院留观动态记录Tool</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ErdDynamicRcdTool
    extends TJDOTool {

    /**
     * 实例
     */
    private static ErdDynamicRcdTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static ErdDynamicRcdTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdDynamicRcdTool();
        return instanceObject;
    }

    public ErdDynamicRcdTool() {
    }

    /**
     * 转床
     * @param parm
     * @return TParm
     */
    public TParm onTransfer(TParm parm, TConnection conn) {

        TParm result = new TParm();
        //先做取消--转出
        TParm canselData = new TParm();
        canselData.setData("ERD_REGION_CODE", parm.getData("fromRegion"));
        canselData.setData("BED_NO", parm.getData("fromBed"));
        canselData.setData("CASE_NO", new TNull(String.class));
        canselData.setData("MR_NO", new TNull(String.class));
        canselData.setData("OCCUPY_FLG", "N");
        canselData.setData("OPT_USER", parm.getData("OPT_USER"));
        canselData.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = ErdForBedAndRecordTool.getInstance().updateErdBed(canselData,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        //在做更新--转入
        TParm inData = new TParm();
        inData.setData("ERD_REGION_CODE", parm.getData("toRegion"));
        inData.setData("BED_NO", parm.getData("toBed"));
        inData.setData("CASE_NO", parm.getData("CASE_NO"));
        inData.setData("MR_NO", parm.getData("MR_NO"));
        inData.setData("OCCUPY_FLG", "Y");
        inData.setData("OPT_USER", parm.getData("OPT_USER"));
        inData.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = ErdForBedAndRecordTool.getInstance().updateErdBed(inData, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 设置留观床
     * @param parm
     * @return TParm
     */
    public TParm setErdBed(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //先做取消--转出
        TParm inData = new TParm();
        inData.setData("ERD_REGION_CODE", parm.getData("ERD_REGION_CODE"));
        inData.setData("BED_NO", parm.getData("BED_NO"));
        inData.setData("CASE_NO", parm.getData("CASE_NO"));
        inData.setData("MR_NO", parm.getData("MR_NO"));
        inData.setData("OCCUPY_FLG", parm.getData("OCCUPY_FLG"));
        inData.setData("OPT_USER", parm.getData("OPT_USER"));
        inData.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = ErdForBedAndRecordTool.getInstance().updateErdBed(inData,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }
    /**
     * 清空留观病床
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm clearErdBed(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //先做取消--转出
        TParm inData = new TParm();
        inData.setData("ERD_REGION_CODE", parm.getData("ERD_REGION_CODE"));
        inData.setData("BED_NO", parm.getData("BED_NO"));
        inData.setData("CASE_NO", new TNull(String.class));
        inData.setData("MR_NO", new TNull(String.class));
        inData.setData("OCCUPY_FLG", "N");
        inData.setData("OPT_USER", parm.getData("OPT_USER"));
        inData.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = ErdForBedAndRecordTool.getInstance().updateErdBed(inData,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }


    /**
     * 设置动态记录
     * @param parm
     * @return TParm
     */
    public TParm setErdRecord(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = ErdForBedAndRecordTool.getInstance().insertErdRecord(parm,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 更新病人动态记录
     * @param parm
     * @return TParm
     */
    public TParm updateErdRecord(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = ErdForBedAndRecordTool.getInstance().updateErdRecord(parm,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }


    /**
     * 更新病人状态--6（留观）
     * @param parm
     * @return TParm
     */
    public TParm updateAdmStatus(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = ErdForBedAndRecordTool.getInstance().updateAdmStatus(parm,
            conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

}
