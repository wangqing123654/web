package jdo.bms;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 血液信息
 * </p>
 *
 * <p>
 * Description: 血液信息
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSBloodTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BMSBloodTool instanceObject;

    /**
     * 得到实例
     *
     * @return BMSBloodTool
     */
    public static BMSBloodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSBloodTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSBloodTool() {
        setModuleName("bms\\BMSBloodModule.x");
        onInit();
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 入库更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 交叉配学查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodCross(TParm parm) {
//        TParm result = this.query("queryBloodCross", parm);
    	TParm result = this.query("queryBloodCrossExceptOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 库存查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodStock(TParm parm) {
        TParm result = this.query("queryBloodStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据就诊序号,病案号和住院号获得病患输血信息(红细胞,血小板,血浆,全血)
     *
     * @param parm
     * @return
     */
    public TParm getApplyInfo(TParm parm) {
        TParm result = this.query("getApplyInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新备血单信息(交叉配血)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodCross(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdateBloodCross", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新备血单信息(血品出库)
     *
     * @param parm
     * @return
     */
    public TParm onUpdateBloodOut(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdateBloodOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询 (配血量 + 出库量)
     *
     * @param parm
     * @return
     */
    public TParm getSumTot(TParm parm) {
        TParm result = this.query("getSumTot", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 库存状态查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodStockStatus(TParm parm) {
        TParm result = this.query("queryBloodStockStatus", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 血品库存查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodQtyStock(TParm parm) {
        TParm result = this.query("queryBloodQtyStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 血库用血月报表
     *
     * @param parm
     * @return
     */
    public TParm onQueryMonthStock(TParm parm) {
        TParm result = this.query("queryMonthStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 交叉配学查询（已出库的不再显示在交叉配血界面【TABLE_M】中）
     *
     * @param parm
     * @return
     */
    public TParm onQueryBloodCrossExceptOut(TParm parm) {
        TParm result = this.query("queryBloodCrossExceptOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }    

}
