package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 住院费用档工具类</p>
*
* <p>Description: 住院费用档工具类</p>
*
* <p>Copyright: Copyright (c)bluecore</p>
*
* <p>Company: bluecore</p>
*
* @author caowl
* @version 1.0
*/
public class IBSOrdmTool extends TJDOTool{
	  /**
     * 实例
     */
    public static IBSOrdmTool instanceObject;
    /**
     * 得到实例
     * @return IBSBilldTool
     */
    public static IBSOrdmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSOrdmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IBSOrdmTool() {
        setModuleName("ibs\\IBSOrdmCTZModule.x");
        onInit();
    }
    
    /**
     * 新增费用主档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdataM(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdata",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm insertdataMOne(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdataOne",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 新增费用主档 添加BILL_EXE_FLG
     * pangben 2016-7-25
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdataLumpworkDExe(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertdataLumpworkDExe",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 新增费用明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdataD(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertDdata",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 
    * @Title: insertdataLumpworkD
    * @Description: TODO(包干套餐添加插入IBS_ORDD表功能)
    * @author pangben
    * @param parm
    * @param connection
    * @return
    * @throws
     */
    public TParm insertdataLumpworkD(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertdataLumpworkD",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 新增费用明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIbsOrddData(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertIbsorddData",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}


