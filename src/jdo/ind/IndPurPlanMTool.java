package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 采购计划主档Tool
 * </p>
 *
 * <p>
 * Description: 采购计划主档Tool
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
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class IndPurPlanMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndPurPlanMTool instanceObject;

    /**
     * 构造器
     */
    public IndPurPlanMTool() {
        setModuleName("ind\\INDPurPlanMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static IndPurPlanMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurPlanMTool();
        return instanceObject;
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPlanM", parm);
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
        TParm result = this.update("createNewPlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新采购计划单生成订购单状态
     *
     * @param parm
     * @return
     */
    public TParm onUpdateCreateFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateCreateFlg", parm, conn);
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
        TParm result = this.update("deletePlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 采购计划引用查询(按照药品查询)
     *
     * @param parm
     * @return
     */
    public TParm onQueryExcerptByOrder(TParm parm) {
        TParm result = this.query("queryExcerptByOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询上期采购量
     *
     * @param parm
     * @return
     */
    public TParm onQueryBuyQty(TParm parm) {
//        TParm result = this.query("queryBuyQty", parm);
    	//修改采购量算法，减去退货量 by guangl 20160714
    	String sql = "SELECT SUM(B.VERIFYIN_QTY - B.ACTUAL_QTY - NVL(C.QTY,0)) AS BUY_QTY " + 
    						"FROM (IND_VERIFYINM A LEFT JOIN IND_VERIFYIND B ON A.VERIFYIN_NO = B.VERIFYIN_NO) " +
    						"LEFT JOIN IND_REGRESSGOODSD C ON B.VERIFYIN_NO = C.VERIFYIN_NO AND B.ORDER_CODE = C.ORDER_CODE " +
    						"WHERE A.CHECK_DATE IS NOT NULL " +
    						"AND A.VERIFYIN_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE").substring(0,19) + "','yyyy-mm-dd hh24:mi:ss') " +
        									"AND TO_DATE('"  + parm.getValue("END_DATE").substring(0,19)+   "','yyyy-mm-dd hh24:mi:ss') " +
        									"AND A.ORG_CODE = '" + parm.getValue("ORG_CODE") + "' " +
        									"AND B.ORDER_CODE = '"+ parm.getValue("ORDER_CODE") +"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询上期销售量
     *
     * @param parm
     * @return
     */
    public TParm onQuerySellQty(TParm parm) {
//        TParm result = this.query("querySellQty", parm);
    	//更改上期销售量的sql语句 by guangl 20160701
    	String sql = "WITH A AS(SELECT DISPENSE_NO FROM IND_DISPENSEM WHERE   TO_ORG_CODE = '"+ parm.getValue("ORG_CODE")+"' " 
        					+ "AND REQTYPE_CODE <> 'RET' " +
        							"AND DISPENSE_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE").substring(0,19) + "','yyyy-mm-dd hh24:mi:ss') " +
        									"AND TO_DATE('"  + parm.getValue("END_DATE").substring(0,19)+   "','yyyy-mm-dd hh24:mi:ss')) "
        					+	"SELECT SUM(B.ACTUAL_QTY) AS SELL_QTY FROM A, IND_DISPENSED B "
        					+	"WHERE A.DISPENSE_NO = B.DISPENSE_NO AND B.ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询主库库存
     *
     * @param parm
     * @return
     */
    public TParm onQueryMainQty(TParm parm) {
        TParm result = this.query("queryMainQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询中库库存
     *
     * @param parm
     * @return
     */
    public TParm onQueryMiddQty(TParm parm) {
        TParm result = this.query("queryMiddQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
