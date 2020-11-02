package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 包干套餐维护
 * </p>
 * @author liling 20140508
 * 
 *
 */
public class MEMLumpworkTool extends TJDOTool {
    /**
     * 实例
     */
    private static MEMLumpworkTool instanceObject;
    /**
     * 得到实例
     * @return PanelGroupTool
     */
    public static MEMLumpworkTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMLumpworkTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public MEMLumpworkTool() {
        setModuleName("mem\\MEMLumpworkModule.x");
        onInit();
    }

    /**
     * 新增包干套餐
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String quegroupCode = parm.getValue("LUMPWORK_CODE");
        if (existsQueGroup(quegroupCode)) {
            result.setErr( -1, "包干套餐编号已存在!");
            return result;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新包干套餐
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询包干套餐
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除包干套餐
     * @param quegroupCode String
     * @return boolean
     */
    public TParm deletedata(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("LUMPWORK_CODE", quegroupCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 判断是否存在包干套餐
     * @param quegroupCode String 包干套餐代码
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsQueGroup(String quegroupCode) {
    	TParm parm = new TParm();
//        TParm parm = new TParm(TJDODBTool.getInstance().select(
//        		"SELECT COUNT(*) AS COUNT FROM MEM_LUMPWORK WHERE LUMPWORK_CODE='"+quegroupCode+"'"
//        ));
        parm.setData("LUMPWORK_CODE", quegroupCode);
//        parm.getValue("COUNT", 0);
       return getResultInt(query("existsQueGroup", parm), "COUNT") > 0;
//        return getResultInt(parm,"COUNT")> 0;
    }

}
