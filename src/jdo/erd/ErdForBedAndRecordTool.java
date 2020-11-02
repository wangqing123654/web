package jdo.erd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 记录留观床位和动态记录</p>
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
public class ErdForBedAndRecordTool
    extends TJDOTool {

    /**
     * 实例
     */
    private static ErdForBedAndRecordTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static ErdForBedAndRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdForBedAndRecordTool();
        return instanceObject;
    }

    public ErdForBedAndRecordTool() {

        //加载Module文件
        this.setModuleName("erd\\ERDMainModule.x");
        onInit();
    }

    /**
     * 根据查询条件查询REG_PATADM表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selPat(TParm parm) {
        TParm result = new TParm();
        result = query("selPat", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据查询条件查询REG_BED表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selBed(TParm parm) {
        TParm result = new TParm();
        result = query("selBed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 插入一条新记录ERD_RECORD
     * @param parm TParm
     * @return TParm
     */
    public TParm insertErdRecord(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("insertErdRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdBed(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("updateErdBed", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateAdmStatus(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("updateAdmStauts", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdRecord(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("updateErdRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据查询条件查询急诊护士需要执行的医嘱
     * @param parm TParm
     * @return TParm
     */
    public TParm selOrderExec(TParm parm) {
        TParm result = new TParm();
        result = query("selOrderExec", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 急诊护士执行更新OPD_ORDER
     * @param parm TParm
     * @return TParm
     */
    public TParm updateExec(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("updateExec", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据就诊序号得到患者急诊留观诊区床号
     * @param parm TParm
     * @return TParm
     */
    public TParm selERDRegionBedByPat(TParm parm){
        TParm result = query("selERDRegionBedByPat",parm);
        return result;
    }
}
