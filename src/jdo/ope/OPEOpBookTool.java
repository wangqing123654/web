package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 手术申请Tool</p>
 *
 * <p>Description: 手术申请Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-24
 * @version 1.0
 */
public class OPEOpBookTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPEOpBookTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static OPEOpBookTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEOpBookTool();
        return instanceObject;
    }

    public OPEOpBookTool() {
        this.setModuleName("ope\\OPEOpBookModule.x");
        this.onInit();
    }
    /**
     * 插入手术申请信息
     * @return TParm
     */
    public TParm insertOpBook(TParm parm){
        TParm result = this.update("insertOpBook",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询手术申请信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOpBook(TParm parm){
        TParm result = this.query("selectOpBook",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改手术申请信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOpBook(TParm parm){
        TParm result = this.update("updateOpBook",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改手术申请的排程部分信息(手术排程)
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOpBookForPersonnel(TParm parm){
        TParm result = this.update("updateOpBookForPersonnel",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 取消申请
     * @return TParm
     */
    public TParm cancelOpBook(String OPBOOK_SEQ){
        TParm parm = new TParm();
        parm.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        TParm result = this.update("cancelOpBook",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改手术预约状态  0 申请， 1 排程完毕 ，2手术完成
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOPEState(TParm parm,TConnection conn){
        TParm result = this.update("updateOPEState",parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
