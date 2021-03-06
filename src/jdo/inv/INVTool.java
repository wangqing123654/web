package jdo.inv;

import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import java.util.HashMap;

import jdo.spc.INDSQL;
import jdo.spc.IndStockDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */ 
public class INVTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INVTool instanceObject;

    /**
     * 得到实例
     *
     * @return INDTool
     */
    public static INVTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INVTool() {
        onInit();
    }

    /**
     * 查询订购单主项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryPurOrderM(TParm parm) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("PUR_M");
        result = InvPurorderMTool.getInstance().onQuery(parmM);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 新增订购单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onCreatePurOrder(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("PUR_M");
        // 新增订购单主项
        result = InvPurorderMTool.getInstance().onInsert(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm parmD = parm.getParm("PUR_D");
        // 新增订购单细项
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            result = InvPurorderDTool.getInstance().onInsert(parmD.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 更新订购单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdatePurOrder(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("PUR_M");
        // 更新订购单主项
        result = InvPurorderMTool.getInstance().onUpdate(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除订购单细项
        result = InvPurorderDTool.getInstance().onDeleteAll(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm parmD = parm.getParm("PUR_D");
        // 新增订购单细项
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            result = InvPurorderDTool.getInstance().onInsert(parmD.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }
    
    
    /**
     * 审核订购单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdatePurOrderCheckFlg(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("PUR_M");
        // 更新订购单主项审核
        result = InvPurorderMTool.getInstance().onUpdateCheck(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
       
        return result;
    }

    /**
     * 删除订购单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeletePurOrder(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        // 删除订购单细项
        result = InvPurorderDTool.getInstance().onDeleteAll(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除订购单主项
        result = InvPurorderMTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 查询验收入库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryVerifyinM(TParm parm) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("VER_M");
        result = InvVerifyinMTool.getInstance().onQuery(parmM);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 新增验收入库单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onCreateVerifyin(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();

        // 验收单主项
        TParm ver_M = parm.getParm("VER_M");
        result = InvVerifyinMTool.getInstance().onInsert(ver_M, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        

        // 验收单细项
        TParm ver_D = parm.getParm("VER_D");
        for (int i = 0; i < ver_D.getCount("VERIFYIN_NO"); i++) {
            result = InvVerifyinDTool.getInstance().onInsert(ver_D.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        // 判断是否审核入库
       
            // 验收序号管理细项数据
            TParm ver_DD = parm.getParm("VER_DD");
            System.out.println(ver_DD);
            for (int i = 0; i < ver_DD.getCount("VERIFYIN_NO"); i++) {
                result = InvVerifyinDDTool.getInstance().onInsert(ver_DD.getRow(
                    i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 物资字典更新移动加权平均
//            TParm inv_base = parm.getParm("BASE");
//            for (int i = 0; i < inv_base.getCount("INV_CODE"); i++) {
//                result = InvBaseTool.getInstance().onUpdateCostPrice(inv_base.
//                    getRow(i), conn);
//                if (result.getErrCode() < 0) {
//                    return result;
//                }
//            }

            // 库存主档数据
            TParm inv_stockm = parm.getParm("STOCK_M");
            for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
                result = InvStockMTool.getInstance().onUpdateStockQty(
                    inv_stockm.getRow(
                        i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 库存明细数据
            TParm inv_stockd = parm.getParm("STOCK_D");
            for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
                if ("INSERT".equals(inv_stockd.getValue("FLG", i))) {
                    result = InvStockDTool.getInstance().onInsert(
                        inv_stockd.getRow(i), conn);
                }
                else {
                    result = InvStockDTool.getInstance().onUpdate(
                        inv_stockd.getRow(i), conn);
                }
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 库存序号管理细项数据
            TParm inv_stockdd = parm.getParm("STOCK_DD");
            for (int i = 0; i < inv_stockdd.getCount("INV_CODE"); i++) {
                result = InvStockDDTool.getInstance().onInsert(
                    inv_stockdd.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 订购单细项数据(更新订购单的入库数量累计)
            TParm inv_purorderd = parm.getParm("PUR_D");
            for (int i = 0; i < inv_purorderd.getCount("PURORDER_NO"); i++) {
                result = InvPurorderDTool.getInstance().onUpdateInQty(
                    inv_purorderd.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 更新入库单细项的单据状态
            result = InvPurorderDTool.getInstance().onUpdateFlg(inv_purorderd.
                getRow(0), conn);
            if (result.getErrCode() < 0) {
                return result;
            }

            // 更新订购单主档状态
            TParm inv_purorderm = parm.getParm("PUR_M");
            String sql = INVSQL.getInvPurorderMInQty(inv_purorderm.
                getValue("PURORDER_NO"));
            inv_purorderm = new TParm(TJDODBTool.getInstance().select(sql));
            if (inv_purorderm.getInt("SUM_COUNT") == 0) {
                result = InvPurorderMTool.getInstance().onUpdateFinalFlg(
                    parm.getParm("PUR_M"), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }
        

        return result;

    }

    /**
     * 更新验收入库单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateVerifyin(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();

        // 验收主项
        TParm ver_M = parm.getParm("VER_M");
        result = InvVerifyinMTool.getInstance().onUpdate(ver_M, conn);
        if (result.getErrCode() < 0) {
            return result;
        }

        // 验收单细项
        TParm ver_D = parm.getParm("VER_D");
        for (int i = 0; i < ver_D.getCount("VERIFYIN_NO"); i++) {
            result = InvVerifyinDTool.getInstance().onUpdate(ver_D.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        // 判断是否审核入库
        if ("Y".equals(ver_M.getValue("CHECK_FLG"))) {
            // 验收序号管理细项数据
            TParm ver_DD = parm.getParm("VER_DD");
            for (int i = 0; i < ver_DD.getCount("VERIFYIN_NO"); i++) {
                result = InvVerifyinDDTool.getInstance().onInsert(ver_DD.getRow(
                    i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 物资字典更新移动加权平均
//            TParm inv_base = parm.getParm("BASE");
//            for (int i = 0; i < inv_base.getCount("INV_CODE"); i++) {
//                result = InvBaseTool.getInstance().onUpdateCostPrice(inv_base.
//                    getRow(i), conn);
//                if (result.getErrCode() < 0) {
//                    return result;
//                }
//            }

            // 库存主档数据
            TParm inv_stockm = parm.getParm("STOCK_M");
            for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
                result = InvStockMTool.getInstance().onUpdateStockQty(
                    inv_stockm.getRow(
                        i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 库存明细数据
            TParm inv_stockd = parm.getParm("STOCK_D");
            for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
                if ("INSERT".equals(inv_stockd.getValue("FLG", i))) {
                    result = InvStockDTool.getInstance().onInsert(
                        inv_stockd.getRow(i), conn);
                }
                else {
                    result = InvStockDTool.getInstance().onUpdate(
                        inv_stockd.getRow(i), conn);
                }
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 库存序号管理细项数据
            TParm inv_stockdd = parm.getParm("STOCK_DD");
            for (int i = 0; i < inv_stockdd.getCount("INV_CODE"); i++) {
                result = InvStockDDTool.getInstance().onInsert(
                    inv_stockdd.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 订购单细项数据(更新订购单的入库数量累计)
            TParm inv_purorderd = parm.getParm("PUR_D");
            for (int i = 0; i < inv_purorderd.getCount("PURORDER_NO"); i++) {
                result = InvPurorderDTool.getInstance().onUpdateInQty(
                    inv_purorderd.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }

            // 更新入库单细项的单据状态
            result = InvPurorderDTool.getInstance().onUpdateFlg(inv_purorderd.
                getRow(0), conn);
            if (result.getErrCode() < 0) {
                return result;
            }

            // 更新订购单主档状态
            TParm inv_purorderm = parm.getParm("PUR_M");
            String sql = INVSQL.getInvPurorderMInQty(inv_purorderm.
                getValue("PURORDER_NO"));
            inv_purorderm = new TParm(TJDODBTool.getInstance().select(sql));
            if (inv_purorderm.getInt("SUM_COUNT") == 0) {
                result = InvPurorderMTool.getInstance().onUpdateFinalFlg(
                    parm.getParm("PUR_M"), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 删除验收单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeleteVerifyin(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        // 删除验收细项
        result = InvVerifyinDTool.getInstance().onDeleteAll(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除验收单主项
        result = InvVerifyinMTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 查询申请单主项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryRequestM(TParm parm) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("REQ_M");
        result = InvRequestMTool.getInstance().onQuery(parmM);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 新增申请单
     *
     * @param parm 
     * @param conn
     * @return
     */
    public TParm onCreateRequest(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("REQ_M");
        // 新增申请单主项
        result = InvRequestMTool.getInstance().onInsert(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm parmD = parm.getParm("REQ_D");
        // 新增申请单细项
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            result = InvRequestDTool.getInstance().onInsert(parmD.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }


    /**
     * 更新申请单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateRequest(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("REQ_M");
        // 更新申请单主项
        result = InvRequestMTool.getInstance().onUpdate(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除申请单细项
        result = InvRequestDTool.getInstance().onDeleteAll(parmM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm parmD = parm.getParm("REQ_D");
        // 新增申请单主项
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            result = InvRequestDTool.getInstance().onInsert(parmD.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 删除申请单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeleteRequest(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        // 删除申请单细项
        result = InvRequestDTool.getInstance().onDeleteAll(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除申请单主项
        result = InvRequestMTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 查询出库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseM(TParm parm) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集
        TParm result = new TParm();
        TParm parmM = parm.getParm("DISPENSE_M");
        if ("0".equals(parmM.getValue("FINA_FLG"))) {
            // 查询需要出库的申请单
            result = InvRequestMTool.getInstance().onQueryRequestMOut(parmM);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        else {
            // 查询出库单
            result = InvDispenseMTool.getInstance().onQuery(parmM);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 插入物资出入库
     * @return TParm
     */
    public TParm onCreateDispenseOutorIn(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null) 
            return null;
        // 返回结果集
        TParm result = new TParm();
        if ("N".equals(parm.getParm("DISCHECK_FLG").getValue("DISCHECK_FLG"))) {
            // 出库在途
            result = onDipenseOut(parm, conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        else {
            // 出库即入库（现在修改为出库和入库分开，但是表是一张，字段区分）
            result = onDipenseOutIn(parm, conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    
    
    
    
    /**
     * 更新物资出库？？
     * @return TParm
     */
    public TParm onUpdateDispenseOut(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null) 
            return null;
        // 返回结果集
        TParm result = new TParm();  
        if ("N".equals(parm.getParm("DISCHECK_FLG").getValue("DISCHECK_FLG"))) {
            // 出库在途
            result = onDipenseOut(parm, conn);    
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }
    
    /**
     * 出库在途作业
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDipenseOut(TParm parm, TConnection conn) {
        TParm result = new TParm();
        // 出库单主项信息
        TParm dispenseM = parm.getParm("DISPENSE_M");
        result = InvDispenseMTool.getInstance().onInsertOut(dispenseM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 出库单细项信息
        TParm dispenseD = parm.getParm("DISPENSE_D");
        for (int i = 0; i < dispenseD.getCount("DISPENSE_NO"); i++) {
            result = InvDispenseDTool.getInstance().onInsert(dispenseD.getRow(
                i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        // 库存主项数据
        // 1.出库部门库存主档
        TParm stockOutM = parm.getParm("STOCK_OUT_M");
        for (int i = 0; i < stockOutM.getCount("INV_CODE"); i++) {
            result = InvStockMTool.getInstance().onUpdateStockQtyOut(stockOutM.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        // 库存明细数据
        // 1.出库部门库存明细
        TParm stockOutD = parm.getParm("STOCK_OUT_D");
        for (int i = 0; i < stockOutD.getCount("INV_CODE"); i++) {
            result = InvStockDTool.getInstance().onUpdateQtyOut(stockOutD.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        // 库存序号管理数据(出库在途)
        TParm stockDD = parm.getParm("STOCK_DD");
        for (int i = 0; i < stockDD.getCount("INV_CODE"); i++) {
            result = InvStockDDTool.getInstance().onUpdateQtyOut(stockDD.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        // 申请单细项状态(更新申请单细项出入库累积量和完成状态)
        TParm requestD = parm.getParm("REQUEST_D");
        for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
            result = InvRequestDTool.getInstance().onUpdateActualQtyAndType(
                requestD.getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        // 申请单主项状态(更新申请单主项状态)
        TParm requestM = parm.getParm("REQUEST_M");
        result = InvRequestMTool.getInstance().onUpdateFinalFlg(requestM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 出库即入库作业
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDipenseOutIn(TParm parm, TConnection conn) {
        TParm result = new TParm();
        System.out.println(" 出库单主项信息");
        // 出库单主项信息(不管标记位)  &&&&
        TParm dispenseM = parm.getParm("DISPENSE_M");
        System.out.println("dispenseM==="+dispenseM);  
        String request_type = dispenseM.getValue("REQUEST_TYPE");
        result = InvDispenseMTool.getInstance().onInsertOutIn(dispenseM, conn);
        if (result.getErrCode() < 0) {
            return result; 
        }            
        System.out.println(" 出库单细项信息");
        // 出库单细项信息    &&&&
        TParm dispenseD = parm.getParm("DISPENSE_D");
        for (int i = 0; i < dispenseD.getCount("DISPENSE_NO"); i++) {
            result = InvDispenseDTool.getInstance().onInsert(dispenseD.getRow(
                i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }  
        System.out.println(" 出库单序号管理信息");
        // 出库单序号管理信息       &&&&&  
        TParm dispenseDD = parm.getParm("DISPENSE_DD"); 
        System.out.println("出库单序号管理信息dispenseDD"+dispenseDD);
        System.out.println("出库单序号管理信息====="+dispenseDD.getCount("DISPENSE_NO"));   
        for (int i = 0; i < dispenseDD.getCount("DISPENSE_NO"); i++) {
        	System.out.println("出库单序号管理信息");
            result = INVDispenseDDTool.getInstance().onInsert(dispenseDD.getRow(
                i), conn);  
            if (result.getErrCode() < 0) { 
                return result;
            }
        }
        //modify start
        System.out.println("1.出库部门库存主档(出库调用)");
        // 库存主项数据
        // 1.出库部门库存主档(出库调用)
        TParm stockOutM = parm.getParm("STOCK_OUT_M");
        for (int i = 0; i < stockOutM.getCount("INV_CODE"); i++) {
            result = InvStockMTool.getInstance().onUpdateStockQtyOut(stockOutM.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        } 
        System.out.println("2.入库部门库存主档  （入库调用）");
        // 2.入库部门库存主档  （入库调用）
        if (!"WAS".equals(request_type)) {
            TParm stockInM = parm.getParm("STOCK_IN_M"); 
            System.out.println("stockInM==="+stockInM); 
            System.out.println("2.ltltltltlttltltltltlt入库部门库存主档  （入库调用）");
            for (int i = 0; i < stockInM.getCount("INV_CODE"); i++) {
            	System.out.println("TYPE"+stockInM.getValue("TYPE", i));
                if ("INSERT".equals(stockInM.getValue("TYPE", i))) {
                    result = InvStockMTool.getInstance().onInsert(stockInM.
                        getRow(i), conn);
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
                else {
                    result = InvStockMTool.getInstance().onUpdateStockQty(
                        stockInM.getRow(i), conn);
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
        }
        System.out.println(" 1.出库部门库存明细  判断IO_FLG = 2");
        // 库存明细数据
        // 1.出库部门库存明细  判断IO_FLG = 2
        TParm stockOutD = parm.getParm("STOCK_OUT_D");
//        
//        
//        
//        for (int i = 0; i < stockOutD.getCount("INV_CODE"); i++) {
//            result = InvStockDTool.getInstance().onUpdateQtyOut(stockOutD.
//                getRow(i), conn);
//            if (result.getErrCode() < 0) {
//                return result;
//            }
//        } 
        result = this.updateStockDOut(stockOutD, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        System.out.println("2.入库部门库存明细（判断IO_FLG = 1）");
        // 2.入库部门库存明细（判断IO_FLG = 1）
        if (!"WAS".equals(request_type)) {
            TParm stockInD = parm.getParm("STOCK_IN_D");
            for (int i = 0; i < stockInD.getCount("INV_CODE"); i++) {
                if ("INSERT".equals(stockInD.getValue("TYPE", i))) {
                    result = InvStockDTool.getInstance().onInsert(stockInD.
                        getRow(i), conn);
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
                else {
                    result = InvStockDTool.getInstance().onUpdateQtyIn(
                        stockInD.getRow(i), conn);
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
        }    
      //modify end
        System.out.println("库存序号管理数据(出库即入库)");
        // 库存序号管理数据(出库即入库) 
        TParm stockDD = parm.getParm("STOCK_DD");
        System.out.println("stockDD管理数据parm"+stockDD);    
        System.out.println("stockDDgetCount"+stockDD.getCount("INV_CODE"));
        for (int i = 0; i < stockDD.getCount("INV_CODE"); i++) {
        	System.out.println("STOCKDDDDDDDDDDDDSTOCK");
            result = InvStockDDTool.getInstance().onUpdateQtyOutIn(stockDD.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }      
        }         
        System.out.println("INV_TOOL----1111 IO_FLG"+dispenseM.getData("IO_FLG"));
        if("2".equals(dispenseM.getData("IO_FLG"))){ 
        	System.out.println("申请单细项状态(更新申请单细项出入库累积量和完成状态)");
        	   // 申请单细项状态(更新申请单细项出入库累积量和完成状态)
            TParm requestD = parm.getParm("REQUEST_D");
            for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
                result = InvRequestDTool.getInstance().onUpdateActualQtyAndType(
                    requestD.getRow(i), conn);
                System.out.println("requestD===result"+result);
                if (result.getErrCode() < 0) {
                    return result;
                }       
            }     
            // 申请单主项状态(更新申请单主项状态)
            TParm requestM = parm.getParm("REQUEST_M");
            result = InvRequestMTool.getInstance().onUpdateFinalFlg(requestM, conn);
            System.out.println("requestM===result"+result);
            if (result.getErrCode() < 0) {
                return result;
            }    
        }    
        else if("1".equals(dispenseM.getData("IO_FLG"))){
        	System.out.println("出库单主项状态(更新出库单主项状态) ");
        	//DISPENSE_D 表无累计完成用量     
            // 出库单主项状态(更新出库单主项状态)   
        	System.out.println("更新出库单主项状态"+parm.getParm("DISPENSE_M_UPDATE"));
            TParm dispense_M_update = parm.getParm("DISPENSE_M_UPDATE");
            System.out.println("dispense_M_update"+dispense_M_update);  
            result = InvDispenseMTool.getInstance().onUpdateFinaFlg(dispense_M_update, conn);
            if (result.getErrCode() < 0) {
                return result; 
            }
        }                
        return result; 
    }  
    
    private TParm updateStockDOut(TParm parm ,TConnection conn) {
    	TParm result = new TParm();
    	for (int j = 0; j < parm.getCount("INV_CODE"); j++) {
    		double qty=parm.getDouble("STOCK_QTY", j);
    		TParm queryTParm =new TParm();
    		String invcode=parm.getValue("INV_CODE",j);
    		String orgcode=parm.getValue("ORG_CODE",j);
    		String user=parm.getValue("OPT_USER",j);
    		String term=parm.getValue("OPT_TERM",j);
    		queryTParm.setData("INV_CODE", invcode);
    		queryTParm.setData("ORG_CODE", orgcode);

			TParm oldParm=InvStockDTool.getInstance().onQueryStockQty(queryTParm);
			System.out.println("oldParm====="+oldParm);
			int c=0;
    		while (qty>0) {
    			
    			TParm queryoldParm=oldParm.getRow(c);
    			double stockDQty=queryoldParm.getDouble("STOCK_QTY");
    			System.out.println("111ooooooooooooooooo====="+stockDQty);
    			String batchseq=queryoldParm.getValue("BATCH_SEQ");
    			System.out.println("BATCH_SEQ====="+batchseq);
    			if (stockDQty>=qty) {
    				
    				TParm stockOutD=new TParm();
    		    	  stockOutD.setData("ORG_CODE", orgcode);
    		          stockOutD.setData("INV_CODE", invcode);
    		          stockOutD.setData("BATCH_SEQ",
    		        		  batchseq);
    		          stockOutD.setData("STOCK_QTY",
    		        		  qty);
    		          stockOutD.setData("OPT_USER", user);
    		          stockOutD.setData("OPT_DATE",
    		                            SystemTool.getInstance().getDate());
    		          stockOutD.setData("OPT_TERM",term);
    				
    		          result= InvStockDTool.getInstance().onUpdateQtyOut(stockOutD, conn);
    				if (result.getErrCode() < 0) {
                        return result;
                    } 
    				qty=0;
				} else {
					
					TParm stockOutD=new TParm();
  		    	  stockOutD.setData("ORG_CODE", orgcode);
  		          stockOutD.setData("INV_CODE", invcode);
  		          stockOutD.setData("BATCH_SEQ",
  		        		  batchseq);
  		          stockOutD.setData("STOCK_QTY",
  		        		stockDQty);
  		          stockOutD.setData("OPT_USER",user);
  		          stockOutD.setData("OPT_DATE",
  		                            SystemTool.getInstance().getDate());
  		          stockOutD.setData("OPT_TERM", term);
  				
  		        result=  InvStockDTool.getInstance().onUpdateQtyOut(stockOutD, conn);
  		      if (result.getErrCode() < 0) {
                  return result;
              } 
					
					qty=qty-stockDQty;

				}
    			c++;
				
			}
			
		}
    	return result;
		
	}
  
//    /**
//     * 入库作业
//     * @param parm TParm
//     * @param conn TConnection
//     * @return TParm
//     */
//    public TParm onDipenseIn(TParm parm, TConnection conn) {
//    	  TParm result = new TParm();
//          // 入库单主项信息
//          TParm dispenseM = parm.getParm("DISPENSE_M");
//          String request_type = dispenseM.getValue("REQUEST_TYPE");
//          //FINA_FLG
//          dispenseM.setData("FINA_FLG", "3");
//          result = InvDispenseMTool.getInstance().onInsertOutIn(dispenseM, conn);
//          if (result.getErrCode() < 0) {
//              return result;
//          }
//          // 入库单细项信息
//          TParm dispenseD = parm.getParm("DISPENSE_D");
//          for (int i = 0; i < dispenseD.getCount("DISPENSE_NO"); i++) {
//              result = InvDispenseDTool.getInstance().onInsert(dispenseD.getRow(
//                  i), conn);
//              if (result.getErrCode() < 0) {
//                  return result;
//              }
//          } 
//          
//          // 入库单序号管理信息      
//          TParm dispenseDD = parm.getParm("DISPENSE_DD");   
//          System.out.println("出库单序号管理信息====="+dispenseDD.getCount("DISPENSE_NO"));   
//          for (int i = 0; i < dispenseDD.getCount("DISPENSE_NO"); i++) {
//          	System.out.println("出库单序号管理信息");
//              result = INVDispenseDDTool.getInstance().onInsert(dispenseDD.getRow(
//                  i), conn);  
//              if (result.getErrCode() < 0) { 
//                  return result;
//              }
//          }
//          // 库存主项数据
//          // 1.出库部门库存主档
//          TParm stockOutM = parm.getParm("STOCK_OUT_M");
//          for (int i = 0; i < stockOutM.getCount("INV_CODE"); i++) {
//              result = InvStockMTool.getInstance().onUpdateStockQtyOut(stockOutM.
//                  getRow(i), conn);
//              if (result.getErrCode() < 0) {
//                  return result;
//              }
//          }
////          // 2.入库部门库存主档
////          if (!"WAS".equals(request_type)) {
////              TParm stockInM = parm.getParm("STOCK_IN_M");
////              for (int i = 0; i < stockInM.getCount("INV_CODE"); i++) {
////                  if ("INSERT".equals(stockInM.getValue("TYPE", i))) {
////                      result = InvStockMTool.getInstance().onInsert(stockInM.
////                          getRow(i), conn);
////                      if (result.getErrCode() < 0) {
////                          return result;
////                      }
////                  }
////                  else {
////                      result = InvStockMTool.getInstance().onUpdateStockQty(
////                          stockInM.getRow(i), conn);
////                      if (result.getErrCode() < 0) {
////                          return result;
////                      }
////                  }
////              }
////          }
//          // 库存明细数据
//          // 1.出库部门库存明细
//          TParm stockOutD = parm.getParm("STOCK_OUT_D");
//          for (int i = 0; i < stockOutD.getCount("INV_CODE"); i++) {
//              result = InvStockDTool.getInstance().onUpdateQtyOut(stockOutD.
//                  getRow(i), conn);
//              if (result.getErrCode() < 0) {
//                  return result;
//              }
//          }
////          // 2.入库部门库存明细
////          if (!"WAS".equals(request_type)) {
////              TParm stockInD = parm.getParm("STOCK_IN_D");
////              for (int i = 0; i < stockInD.getCount("INV_CODE"); i++) {
////                  if ("INSERT".equals(stockInD.getValue("TYPE", i))) {
////                      result = InvStockDTool.getInstance().onInsert(stockInD.
////                          getRow(i), conn);
////                      if (result.getErrCode() < 0) {
////                          return result;
////                      }
////                  }
////                  else {
////                      result = InvStockDTool.getInstance().onUpdateQtyIn(
////                          stockInD.getRow(i), conn);
////                      if (result.getErrCode() < 0) {
////                          return result;
////                      }
////                  }
////              }
////          }
//          // 库存序号管理数据(出库即入库)
//          TParm stockDD = parm.getParm("STOCK_DD");
//          for (int i = 0; i < stockDD.getCount("INV_CODE"); i++) {
//              result = InvStockDDTool.getInstance().onUpdateQtyOutIn(stockDD.
//                  getRow(i), conn);
//              if (result.getErrCode() < 0) {
//                  return result;
//              }
//          } 
//          System.out.println("IO_FLG"+stockOutM.getData("IO_FLG"));
//          if(stockOutM.getData("IO_FLG")=="2"){
//          	   // 申请单细项状态(更新申请单细项出入库累积量和完成状态)
//              TParm requestD = parm.getParm("REQUEST_D");
//              for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
//                  result = InvRequestDTool.getInstance().onUpdateActualQtyAndType(
//                      requestD.getRow(i), conn);
//                  if (result.getErrCode() < 0) {
//                      return result;
//                  } 
//              }    
//              // 申请单主项状态(更新申请单主项状态)
//              TParm requestM = parm.getParm("REQUEST_M");
//              result = InvRequestMTool.getInstance().onUpdateFinalFlg(requestM, conn);
//              if (result.getErrCode() < 0) {
//                  return result;
//              } 
//          } 
////          else{
////       	   // 出库单细项状态(更新出库单细项出入库累积量和完成状态)
////              TParm dispenseDOut = parm.getParm("DISPENSE_D");
////              for (int i = 0; i < dispenseDOut.getCount("REQUEST_NO"); i++) {
////                  result = InvRequestDTool.getInstance().onUpdateActualQtyAndType(
////                  		dispenseDOut.getRow(i), conn);
////                  if (result.getErrCode() < 0) {
////                      return result;
////                  } 
////              }
////              // 出库单主项状态(更新出库单主项状态) 
////              TParm requestM = parm.getParm("DISPENSE_M");
////              result = InvRequestMTool.getInstance().onUpdateFinalFlg(requestM, conn);
////              if (result.getErrCode() < 0) {
////                  return result;
////              }
////          }  
//       
//          return result;
//    }
    
    
    /**
     * 查询出库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseMOut(TParm parm) {
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集 
        TParm result = new TParm();
        TParm parmM = parm.getParm("DISPENSE_M");
        if ("REQUEST".equals(parmM.getValue("TYPE"))) {
            // 查询需要出库的申请单 
            result = InvRequestMTool.getInstance().onQueryRequestMOut(parmM);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        else if ("DISPENSE_OUT".equals(parmM.getValue("TYPE"))){
            // 查询出库单  
            result = InvDispenseMTool.getInstance().onQuery(parmM);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }
    
    
    /**
     * 查询入库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseMIn(TParm parm) { 
    	System.out.println("INVTOOL ---  查询入库单开始");
        // 数据检核
        if (parm == null)
            return null;
        // 返回结果集 
        TParm result = new TParm();
        TParm parmM = parm.getParm("DISPENSE_M");
        System.out.println("parmM"+parmM);  
        System.out.println("IN_FINA_FLG"+parmM.getValue("FINA_FLG"));
        //出库完成的出库单，入库       
        if ("DISPENSE_OUT".equals(parmM.getValue("TYPE"))) {
        	System.out.println("查询需要入库的出库单");  
            // 查询需要入库的出库单
            result = InvDispenseMTool.getInstance().onQueryOI(parmM);
            System.out.println("0==="+result); 
            if (result.getErrCode() < 0) {   
                return result;  
            }     
        }    
        else if("DISPENSE_IN".equals(parmM.getValue("TYPE"))) {  
            // 查询入库单
            result = InvDispenseMTool.getInstance().onQueryIn(parmM);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    } 
    
    
    /**
     * 删除出库单(MODULE SQL 未实现)
     *   
     * @param parm
     * @param conn 
     * @return 
     */
    public TParm onDeleteDispense(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null; 
        // 返回结果集 
        TParm result = new TParm();   
        
        // 删除出库单序号DD项 （这里应该分离）  
        result = INVDispenseDDTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            return result;  
        }  
        // 删除出库单细项 （这里应该分离）   
        result = InvDispenseDTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            return result; 
        }  
        // 删除出库单主项
        result = InvDispenseMTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 手术包打包
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInvPackage(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //更新库存主项数据INV_STOCKM
        if (parm.existData("INV_STOCKM")) {
            TParm invStockMParm = parm.getParm("INV_STOCKM");
            for (int i = 0; i < invStockMParm.getCount("INV_CODE"); i++) {
                result = InvStockMTool.getInstance().onUpdateStockQtyByPack(
                    invStockMParm.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }
        }
        //更新库存细项数据INV_STOCKD
        if (parm.existData("INV_STOCKD")) {
            TParm invStockDParm = parm.getParm("INV_STOCKD");
            for (int i = 0; i < invStockDParm.getCount("INV_CODE"); i++) {
                result = InvStockDTool.getInstance().onUpdateStockQtyByPack(
                    invStockDParm.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }
        }
        //更新库存明细项数据INV_STOCKDD
        if (parm.existData("INV_STOCKDD")) {
            TParm invStockDDParm = parm.getParm("INV_STOCKDD");
            for (int i = 0; i < invStockDDParm.getCount("INV_CODE"); i++) {
                result = InvStockDDTool.getInstance().onUpdateStockQtyByPack(
                    invStockDDParm.getRow(i), conn);
                if (result.getErrCode() < 0) {
                    return result;
                }
            }
        }
        //更新手术包库存主项数据INV_PACKSTOCKM
        if (parm.existData("INV_PACKSTOCKM")) {
            TParm invPackStockMParm = parm.getParm("INV_PACKSTOCKM");
            if ("I".equals(invPackStockMParm.getValue("UPDATE"))) {
                result = InvPackStockMTool.getInstance().onInsertStockQtyByPack(
                    invPackStockMParm, conn);
            }
            else {
                result = InvPackStockMTool.getInstance().onUpdateStockQtyByPack(
                    invPackStockMParm, conn);
            }
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //更新手术包库存细项数据INV_PACKSTOCKD
        if (parm.existData("INV_PACKSTOCKD")) {
            TParm invPackStockDParm = parm.getParm("INV_PACKSTOCKD");
            for (int i = 0; i < invPackStockDParm.getCount("PACK_CODE"); i++) {
                if ("I".equals(invPackStockDParm.getValue("UPDATE", i))) {
                    result = InvPackStockDTool.getInstance().
                        onInsertStockQtyByPack(
                            invPackStockDParm.getRow(i), conn);
                }
                else {
                    result = InvPackStockDTool.getInstance().
                        onUpdateStockQtyByPack(
                            invPackStockDParm.getRow(i), conn);
                }
                if (result.getErrCode() < 0) {
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 新增供应室请领单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onCreateSupRequest(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (!parm.existData("INV_SUPREQUESTM")) {
            result.setErr( -1, "缺少请领单主项信息");
            return result;
        }
        if (!parm.existData("INV_SUPREQUESTD")) {
            result.setErr( -1, "缺少请领单明细信息");
            return result;
        }
        // 请领单主项信息
        TParm supRequestM = parm.getParm("INV_SUPREQUESTM");
        result = InvSupRequestMTool.getInstance().onInsert(supRequestM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 请领单明细信息
        TParm supRequestD = parm.getParm("INV_SUPREQUESTD");
        for (int i = 0; i < supRequestD.getCount("REQUEST_NO"); i++) {
            result = InvSupRequestDTool.getInstance().onInsert(supRequestD.
                getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 更新供应室请领单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateSupRequest(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (!parm.existData("INV_SUPREQUESTM")) {
            result.setErr( -1, "缺少请领单主项信息");
            return result;
        }
        if (!parm.existData("INV_SUPREQUESTD")) {
            result.setErr( -1, "缺少请领单明细信息");
            return result;
        }
        // 请领单主项信息
        TParm supRequestM = parm.getParm("INV_SUPREQUESTM");
        result = InvSupRequestMTool.getInstance().onUpdate(supRequestM, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 请领单明细信息
        TParm supRequestD = parm.getParm("INV_SUPREQUESTD");
        for (int i = 0; i < supRequestD.getCount("REQUEST_NO"); i++) {
            result = InvSupRequestDTool.getInstance().onUpdate(supRequestD.
                getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 删除供应室请领单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteSupRequest(TParm parm, TConnection conn) {
        TParm result = new TParm();
        // 删除申请单细项
        result = InvSupRequestDTool.getInstance().onDeleteAll(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // 删除申请单主项
        result = InvSupRequestMTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 新增一般物资出库单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertInvSupDispenseByInv(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //INV_SUP_DISPENSEM
        TParm inv_sup_dispensem = parm.getParm("INV_SUP_DISPENSEM");
        result = InvSupDispenseMTool.getInstance().onInsert(inv_sup_dispensem,
            conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        //INV_SUP_DISPENSED
        TParm inv_sup_dispensed = parm.getParm("INV_SUP_DISPENSED");
        for (int i = 0; i < inv_sup_dispensed.getCount("DISPENSE_NO"); i++) {
            result = InvSupDispenseDTool.getInstance().onInsert(
                inv_sup_dispensed.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_STOCKM
        TParm inv_stockm = parm.getParm("INV_STOCKM");
        for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
            result = InvStockMTool.getInstance().onUpdateStockQtyByReq(
                inv_stockm.getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_STOCKD
        TParm inv_stockd = parm.getParm("INV_STOCKD");
        for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
            result = InvStockDTool.getInstance().onUpdateStockQtyByReq(
                inv_stockd.getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_STOCKDD
        TParm inv_stockdd = parm.getParm("INV_STOCKDD");
        for (int i = 0; i < inv_stockdd.getCount("INV_CODE"); i++) {
            result = InvStockDDTool.getInstance().onUpdateStockQtyByReq(
                inv_stockdd.getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_SUPREQUESTD
        TParm inv_suprequestd = parm.getParm("INV_SUPREQUESTD");
        for (int i = 0; i < inv_suprequestd.getCount("REQUEST_NO"); i++) {
            result = InvSupRequestDTool.getInstance().
                onUpdateFlgAndQtyBySupDispense(inv_suprequestd.getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_SUPREQUESTM
        TParm inv_suprequestm = parm.getParm("INV_SUPREQUESTM");
        result = InvSupRequestMTool.getInstance().onUpdateFlgBySupDispense(
            inv_suprequestm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 新增手术包出库单
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertInvSupDispenseByPack(TParm parm, TConnection conn) {
        TParm result = new TParm();
        //INV_SUP_DISPENSEM
        TParm inv_sup_dispensem = parm.getParm("INV_SUP_DISPENSEM");
//        System.out.println("-------主档inv_sup_dispensem--------"+inv_sup_dispensem);
        result = InvSupDispenseMTool.getInstance().onInsert(inv_sup_dispensem,
            conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        //INV_SUP_DISPENSED
        TParm inv_sup_dispensed = parm.getParm("INV_SUP_DISPENSED");
        for (int i = 0; i < inv_sup_dispensed.getCount("DISPENSE_NO"); i++) {
            result = InvSupDispenseDTool.getInstance().onInsert(
                inv_sup_dispensed.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_SUP_DISPENSEDD
        TParm inv_sup_dispensedd = parm.getParm("INV_SUP_DISPENSEDD");
        for (int i = 0; i < inv_sup_dispensedd.getCount("INV_CODE"); i++) {
            result = InvSupDispenseDDTool.getInstance().onInsert(
                inv_sup_dispensedd.
                getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_PACKSTOCKM
        TParm inv_packstockm = parm.getParm("INV_PACKSTOCKM");
        for (int i = 0; i < inv_packstockm.getCount("PACK_CODE"); i++) {
            if (inv_packstockm.getInt("PACK_SEQ_NO", i) == 0) {
                //没有序号管理的手术包,扣除主库中手术包的库存量
                result = InvPackStockMTool.getInstance().onUpdateQtyBySupReq(
                    inv_packstockm.getRow(i), conn);
            }
            else {
                //有序号管理的手术包,更新手术包的状态为出库
                result = InvPackStockMTool.getInstance().onUpdateStatusBySupReq(
                    inv_packstockm.getRow(i), conn);
            }
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_PACKSTOCKD
        TParm inv_packstockd = parm.getParm("INV_PACKSTOCKD");
        for (int i = 0; i < inv_packstockd.getCount("PACK_CODE"); i++) {
            if (inv_packstockd.getInt("PACK_SEQ_NO", i) == 0) {
                //没有序号管理的手术包,扣除手术包库存明细中的库存量
                result = InvPackStockDTool.getInstance().onUpdateQtyBySupReq(
                    inv_packstockd.getRow(i), conn);
            }
            else {
                //有序号管理的手术包,删除手术包明细中的一次性物资
            	
                if ("Y".equals(inv_packstockd.getValue("ONCE_USE_FLG", i))) {
                	//且为一次性高值物品加标记位
                	if(inv_packstockd.getInt("PACK_SEQ_NO", i) != 0){
                		result = InvPackStockDTool.getInstance().changeHOStatus(inv_packstockd.getRow(i),
                                conn);
                		if (result.getErrCode() < 0) {
                            return result;
                        }
                	}
                    result = InvPackStockDTool.getInstance().
                        onDeleteOnceUserInvBySupReq(inv_packstockd.getRow(i),
                        conn);
                }
            }
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_SUPREQUESTD
        TParm inv_suprequestd = parm.getParm("INV_SUPREQUESTD");
        for (int i = 0; i < inv_suprequestd.getCount("REQUEST_NO"); i++) {
            result = InvSupRequestDTool.getInstance().
                onUpdateFlgAndQtyBySupDispense(inv_suprequestd.getRow(i), conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        //INV_SUPREQUESTM
        TParm inv_suprequestm = parm.getParm("INV_SUPREQUESTM");
        result = InvSupRequestMTool.getInstance().onUpdateFlgBySupDispense(
            inv_suprequestm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * 消毒回收记录
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onINvBackPackAndDisnfection(TParm parm, TConnection conn) {
        TParm result = new TParm();
//        TParm disinfectionParm = parm.getParm("INV_DISINFECTION");
//        Map map = (Map) parm.getData("MAP");
//        //System.out.println("map===" + map);
//        String org_code = "";
//        String pack_code = "";
//        int pack_seq_no = 0;
//        String inv_code = "";
//        double stock_qty = 0;
//        int batch_seq = 0;
//        int recount_no = 0;
//        String seq_flg = "";
//        String sql = "";
//        //INV_STOCKM
//        List inv_stockm_list = new ArrayList();
//        //INV_STOCKD
//        List inv_stockd_list = new ArrayList();
//        //INV_PACKSTOCKD
//        List inv_packstockd_list = new ArrayList();
//        Map stockMap = new HashMap();
//        for (int i = 0; i < disinfectionParm.getCount("PACK_CODE"); i++) {
//            org_code = disinfectionParm.getValue("ORG_CODE", i);
//            pack_code = disinfectionParm.getValue("PACK_CODE", i);
//            pack_seq_no = disinfectionParm.getInt("PACK_SEQ_NO", i);
//            if (pack_seq_no == 0) {
//                seq_flg = "N";
//            }
//            else {
//                seq_flg = "Y";
//            }
//            sql = INVSQL.getINVPackStockDInfo(org_code, pack_code, pack_seq_no,
//                                              disinfectionParm.getDouble("QTY",
//                i));
//            TParm stockParm = new TParm(TJDODBTool.getInstance().select(sql));
//            //System.out.println("stockParm==" + stockParm);
//            for (int j = 0; j < stockParm.getCount("INV_CODE"); j++) {
//                inv_code = stockParm.getValue("INV_CODE", j);
//                stock_qty = stockParm.getDouble("QTY", j);
//                String value = org_code + "|" + pack_code + "|" +
//                    pack_seq_no + "|" + inv_code;
//                recount_no = TypeTool.getInt(map.get(value));
//                if ("N".equals(seq_flg)) {
//                    inv_packstockd_list.add(INVSQL.updateInvPackStockD(org_code,
//                        pack_code, pack_seq_no, inv_code, stock_qty, recount_no));
//                }
//                else {
//                    inv_packstockd_list.add(INVSQL.updateInvPackStockD(org_code,
//                        pack_code, pack_seq_no, inv_code, 0, recount_no));
//                }
//                if ("N".equals(stockParm.getValue("ONCE_USE_FLG", j))) {
//                    continue;
//                }
//                // 更新相同项的数量
//                if (stockMap.containsKey(org_code + "|" + inv_code)) {
//                    stockMap.remove(org_code + "|" + inv_code);
//                    stockMap.put(org_code + "|" + inv_code,
//                                 org_code + "|" + inv_code + "|" + stock_qty);
//                }
//                else {
//                    stockMap.put(org_code + "|" + inv_code,
//                                 org_code + "|" + inv_code + "|" + stock_qty);
//                }
//            }
//        }
//
//        for (int i = 0; i < stockMap.size(); i++) {
//            String[] mapValue = stockMap.get(i).toString().split("|");
//            org_code = mapValue[0];
//            inv_code = mapValue[1];
//            stock_qty = TypeTool.getDouble(mapValue[2]);
//            inv_stockm_list.add(INVSQL.updateInvStockM(org_code, inv_code,
//                stock_qty));
//            TParm stockd = new TParm(TJDODBTool.getInstance().select(INVSQL.
//                getINVStockQtyOrderBySEQ(org_code, inv_code)));
//            for (int j = 0; j < stockd.getCount("STOCK_QTY"); j++) {
//                batch_seq = stockd.getInt("BATCH_SEQ", j);
//                if (stock_qty > stockd.getDouble("STOCK_QTY", j)) {
//                    inv_stockd_list.add(INVSQL.updateInvStockD(org_code,
//                        inv_code, batch_seq,
//                        stockd.getDouble("STOCK_QTY", j)));
//                    stock_qty = stock_qty - stockd.getDouble("STOCK_QTY", j);
//                }
//                else {
//                    inv_stockd_list.add(INVSQL.updateInvStockD(org_code,
//                        inv_code, batch_seq, stock_qty));
//                    break;
//                }
//            }
//        }
//
//        for (int i = 0; i < disinfectionParm.getCount("PACK_CODE"); i++) {
//            TParm rowParm = disinfectionParm.getRow(i);
//            //INV_PACKSTOCKM
//            result = InvPackStockMTool.getInstance().onUpdateQtyAndStatus(
//                rowParm, conn);
//            if (result.getErrCode() < 0) {
//                return result;
//            }
//
//            //INV_DISINFECTION
//            result = InvDisinfectionTool.getInstance().onInsert(rowParm, conn);
//            if (result.getErrCode() < 0) {
//                return result;
//            }
//        }
//        for (int i = 0; i < inv_stockm_list.size(); i++) {
//            //INV_STOCKM
//            //System.out.println("INV_STOCKM===" +
//            //                   inv_stockm_list.get(i).toString());
//            result = new TParm(TJDODBTool.getInstance().update(inv_stockm_list.
//                get(i).toString(), conn));
//            if (result.getErrCode() < 0) {
//                return result;
//            }
//        }
//        for (int i = 0; i < inv_stockd_list.size(); i++) {
//            //INV_STOCKD
//            //System.out.println("INV_STOCKD===" +
//            //                   inv_stockd_list.get(i).toString());
//            result = new TParm(TJDODBTool.getInstance().update(inv_stockd_list.
//                get(i).toString(), conn));
//            if (result.getErrCode() < 0) {
//                return result;
//            }
//        }
//        for (int i = 0; i < inv_packstockd_list.size(); i++) {
//            //INV_PACKSTOCKD
//            //System.out.println("INV_PACKSTOCKD===" +
//            //                   inv_packstockd_list.get(i).toString());
//            result = new TParm(TJDODBTool.getInstance().update(
//                inv_packstockd_list.
//                get(i).toString(), conn));
//            if (result.getErrCode() < 0) {
//                return result;
//            }
//        }
        return result;
    }

   

  
    
    /**
	 * 自动拨补-新增申请主档
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 * @author fux
	 * @date 20130527
	 */
	public TParm onSaveRequestMAuto(TParm parmM, TParm parmD, TConnection conn) {
		/************************************ 保存请领主档 start ****************************/
		TParm result = new TParm(); 
		String orgCode = (String) parmM.getValue("FROM_ORG_CODE");
		String toOrgCode = (String) parmM.getValue("TO_ORG_CODE");
		String requestNo = (String) parmM.getValue("REQUEST_NO");
		String fixedType = (String) parmM.getValue("FIXEDAMOUNT_FLG");
		String autoFillType = (String) parmM.getValue("AUTO_FILL_TYPE");
		//获得新增主项数据Parm
		TParm parm = getInsertMParm(requestNo, orgCode, toOrgCode); 
		//主表自动拨补信息
		result = new TParm(TJDODBTool.getInstance().update
				(INVSQL.saveRequestMAuto(parm), conn));  
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result; 
		}
		/************************************ 保存请领主档 end ****************************/

		/************************************ 保存请领明细档 start ************************/
		int n = 1  ;
		for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
			String invCode = parmD.getValue("INV_CODE", i);
			// 最高库存量
			double maxQty = parmD.getDouble("MAX_QTY", i);
			// 最低库存量 
			double minQty = parmD.getDouble("MIN_QTY", i);
			// 安全库存量
			double safeQty = parmD.getDouble("SAFE_QTY", i);
			// 经济补充量
			double ecoBuyQty = parmD.getDouble("ECONOMICBUY_QTY", i);
			// 当前库存量 
			double stockQty = parmD.getDouble("STOCK_QTY", i);
			//在途量 
			TParm onWayQty = new TParm(TJDODBTool.getInstance().select(INVSQL.getOnWayRequest(invCode))) ;
			double buyQty =onWayQty.getDouble("QTY", 0) ;
			double fixQty = getOrderCodePrice(maxQty, minQty, safeQty, ecoBuyQty, stockQty, buyQty, fixedType, autoFillType);
			if(fixQty<=0)  continue   ;
			n++  ;
			//组装明细表数据    
			TParm deParm = getRequestDParm(requestNo, i + "", invCode, fixQty);
			//细表自动拨补信息   
			String sqlD = INVSQL.saveRequestDAuto(deParm);  
			result = new TParm(TJDODBTool.getInstance().update(sqlD, conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
				return result; 
				
			}
		}
		/************************************ 保存请领明细档 end ************************/
		if(n==1){
			result = new TParm(TJDODBTool.getInstance().update
					(INVSQL.deleteRequestMAuto(parm), conn));  
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
				return result; 
			}	
		}
		return result;
	}
	
	/**
	 * 获得新增主项的数据PARM
	 * 
	 * @param parm  
	 * @return
	 * @author fux modify
	 * @date 20130527 
	 */
	private TParm getInsertMParm(String requestNo, String orgCode, String toOrgCode) {
		TParm parm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("REQUEST_TYPE", "ATO");
		parm.setData("FROM_ORG_CODE", orgCode);
		parm.setData("TO_ORG_CODE", toOrgCode);
		parm.setData("REQUEST_DATE", SystemTool.getInstance().getDate());
		parm.setData("REQUEST_USER", "AUTO");
		parm.setData("REN_CODE", "");
		parm.setData("REMARK", "");  
		parm.setData("URGENT_FLG", "N");
		parm.setData("FINAL_FLG", "N");
		parm.setData("OPT_USER", Operator.getID());   
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

	/**
	 *获得申请细项的数据PARM
	 * 
	 * @param requestNo
	 * @param seqNo
	 * @param orderCode
	 * @param fixedType
	 *            拨补方式
	 * @param fixedQty
	 *            拨补数量方式
	 * @return
	 * @author fux
	 * @date 20130527
	 */
	private TParm getRequestDParm(String requestNo, String seqNo, String invCode, double fixQty) {
		TParm parm = new TParm();
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("INVSEQ_NO",seqNo);
		parm.setData("INV_CODE", invCode); 
		parm.setData("QTY", fixQty);
		parm.setData("ACTUAL_QTY",fixQty); 
		parm.setData("BATCH_SEQ","");  
		parm.setData("BATCH_NO","");      
		parm.setData("VALID_DATE",""); 
		parm.setData("FINA_TYPE", "0");   
		
		parm.setData("OPT_USER",Operator.getName());
		parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM",Operator.getIP());
		return parm;
	}
	
	
	/**
	 *药品库存数量装换-从dosageQty变成stockQty 
	 * @param orderCode
	 * @param qty
	 * @return
	 */
	public int getStockQtyOfOrderCodeFromate(String orderCode,double qty){
		int stockQty = (int) qty;
		TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHAInfoByOrder(orderCode)));
		if(null != parm){
			int dosageQty = parm.getInt("DOSAGE_QTY", 0);
			stockQty = getStockQtyInt(qty, dosageQty);
		}
		return stockQty;
	}
	  public int getStockQtyInt(double fixQty, double dosageQty) {
		    int result = 0;
		    if (dosageQty > 0) { 
		      result = (int)(fixQty / dosageQty) + (fixQty % dosageQty == 0 ? 0 : 1);
		    }
		    else {
		      result = (int)fixQty;
		    }
		    return result;
		  }
	  
	  
		/**
		 * 获得药品拨补数量
		 * 
		 * @param orgCode
		 * @param orderCode
		 * @param maxQty
		 *            最大库存量
		 * @param mixQty
		 *            最低库存量
		 * @param safeQty
		 *            安全库存量
		 * @param ecoBuyQty
		 *            经济补充量
		 * @param fixedType
		 *            拨补方式：0:每日(周)定量拨补, 1:依最大库存量(低于安全库存时) 2:依最小库存量(低于安全库存时)
		 * @param autoFillType
		 *            自动拨补数量：1:定量；2拨补到最大库存量
		 * @return
		 * @author fux
		 * @date 20130527
		 */
		private double getOrderCodePrice(double maxQty, double mixQty, double safeQty, double ecoBuyQty, double stockQty, double stockQtyIng, String fixedType,
				String autoFillType) {
			double fixedQty = 0.0;
				if(autoFillType.equals("1")){
					if(maxQty- stockQty - stockQtyIng<fixedQty)
						fixedQty = maxQty- stockQty - stockQtyIng; 
					else 
					fixedQty = ecoBuyQty;
				}
			
				else  fixedQty = maxQty- stockQty - stockQtyIng; 
			return fixedQty;
		}
		
		/**
		 *获得订购/
		 * 
		 * @param requestNo
		 * @param seqNo
		 * @param orderCode
		 * @param fixedType
		 *            拨补方式
		 * @param fixedQty
		 *            拨补数量方式
		 * @return
		 * @author fux
		 * @date 20130527
		 */
		private TParm getPurOrderDParm(String purOrderNo, String seqNo, String invCode, double fixQty) {
			TParm parm = new TParm();
			// 得到物资的价格  
			TParm priceParm = getOrderCodePrice(invCode);
			parm.setData("PURORDER_NO", purOrderNo);
			parm.setData("SEQ_NO", seqNo);
			parm.setData("PURORDER_QTY", fixQty);
			parm.setData("INV_CODE",invCode);   
			// 1库存单位；2配药单位             
			parm.setData("BILL_UNIT", priceParm.getValue("BILL_UNIT").replace('[', ' ')
					.replace(']', ' ').trim());         
			parm.setData("PURORDER_PRICE", priceParm.getValue("PURORDER_PRICE").replace('[', ' ')
					.replace(']', ' ').trim());        
			//parm.setData("PURORDER_AMT", purOrderPrice*fixQty);   
			parm.setData("OPT_USER", Operator.getID()); 
			parm.setData("OPT_DATE", StringTool.getTimestamp(new Date())); 
			parm.setData("OPT_TERM", Operator.getIP()); 
			return parm;
		}    
		
		/**
		 * 获得物资的价格 
		 * 
		 * @param parm
		 * @return   
		 * @author fux
		 * @date 20130527
		 */
		public  TParm getOrderCodePrice(String invCode) {
			TParm parm = new TParm();
			double pur_price = 0;
			double stock_price = 0;   
			double retail_price = 0;
			// 查詢物资單位和價格    
			String sql = INVSQL.getINVInfoByOrder(invCode); 
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			System.out.println("result"+result);  
			// 订购单位               
			String billUnit = result.getValue("PURCH_UNIT");     
			System.out.println("billUnit"+billUnit);   
			// 库存单位        
			String stockUnit = result.getValue("STOCK_UNIT");   
			// 发药单位   
			String dispenseUnit = result.getValue("DISPENSE_UNIT");
			// 库存单位     
			// 单价          
			String Cost = result.getValue("COST_PRICE"); 
			System.out.println("Cost"+Cost);     
			parm.setData("BILL_UNIT", billUnit);       
			parm.setData("PURORDER_PRICE", Cost);     
			System.out.println("组装parm"+parm); 
			return parm;  
		}  
		/**
		 * 自动拨补-新增订购/移货主档  
		 * 
		 * @param parm
		 * @param conn 
		 * @return         
		 * @author fux
		 * @date 20130527
		 */
		public TParm onSavePurOrderMAuto(TParm parmM, TParm parmD, TConnection conn) {
			/************************************ 保存订购/移货主档 start ****************************/
			TParm result = new TParm(); 
			String purOrderNo = (String) parmM.getValue("PURORDER_NO");
			String fixedType = (String) parmM.getValue("FIXEDAMOUNT_FLG");
			String autoFillType = (String) parmM.getValue("AUTO_FILL_TYPE");
			result = new TParm(TJDODBTool.getInstance().update(INVSQL.savePurOrderMAuto(parmM), conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
				return result;
		}
			/************************************ 保存订购/移货主档 end ****************************/

			/************************************ 保存订购/移货明细档 start ************************/
			int n = 1;
			for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
				
				String invCode = parmD.getValue("INV_CODE", i);
				// 最高库存量              
				double maxQty = parmD.getDouble("MAX_QTY", i);
				// 最低库存量
				double minQty = parmD.getDouble("MIN_QTY", i);
				// 安全库存量
				double safeQty = parmD.getDouble("SAFE_QTY", i);
				//经济补充量
				double ecoBuyQty = parmD.getDouble("ECONOMICBUY_QTY", i);
				//当前库存量
				double stockQty = parmD.getDouble("STOCK_QTY", i);
				stockQty = getStockQtyOfOrderCodeFromate(invCode,stockQty);
				//==========得到在途量
				TParm onWayQty = new TParm(TJDODBTool.getInstance().select(INVSQL.getOnWayQuety(invCode))) ;
				double buyQty = onWayQty.getDouble("PURORDER_QTY", 0);
				//得到药品转换率  
				double fixQty = getOrderCodePrice(maxQty, minQty, safeQty, ecoBuyQty, stockQty, buyQty, fixedType, autoFillType);
				if(fixQty<=0)  continue  ;
				//组装明细表数据   
				n++  ;
				TParm deParm = getPurOrderDParm(purOrderNo, i + "", invCode, fixQty);
				String sqlD = INVSQL.savePurOrderDAuto(deParm); 
				result = new TParm(TJDODBTool.getInstance().update(sqlD, conn));
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					return result;
				}			   
			}   	
			/************************************ 保存订购/移货明细档 end ************************/
			if(n==1){
				result = new TParm(TJDODBTool.getInstance().update(INVSQL.deletePurOrderMAuto(parmM), conn));
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					return result;
			}
			}
			return result;
		}
		
		
		/**
		 * 根据物资编号及物资代码查询物资库存量(注意在途状态的库存)
		 * 
		 * @param parm
		 * @return   
		 */
		public TParm getStockQTY(TParm parm) {
			TParm result = InvStockDTool.getInstance().onQueryStockQTY(parm);
			if (result == null || result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
				return result;
			}
			return result;
		}
		
		private String code;
		/**
		 * 拿到SYS_FEE简称
		 * 
		 * @param orderCode
		 *            String
		 * @return String
		 */
		public synchronized TParm getSysFeeOrder(String invCode) {
			TDataStore d = com.dongyang.manager.TIOM_Database.getLocalTable("SYS_FEE");
			this.code = invCode;    
			d.filterObject(this, "filter");
			if (d.rowCount() == 0)
				return null;
			return d.getRowParm(0);
		}


	    
		  //----------------供应室start---------------------
		    
		    /**
		     * 手术包拆包（序号管理）
		     * @param parm TParm
		     * @param conn TConnection
		     * @return TParm
		     */
		    public TParm onINVTearPackA(TParm parm, TConnection conn) {
		   	
		        TParm result = new TParm();
		        TParm inv_packstockm = parm.getParm("INV_PACKSTOCKM");
		        
		        result = InvPackStockDTool.getInstance().onDeleteAll(inv_packstockm, conn);
		        if (result.getErrCode() < 0) {
		            return result;
		        }
		        result = InvPackStockMTool.getInstance().onDelete(inv_packstockm, conn);
		        if (result.getErrCode() < 0) {
		            return result;
		        }
		        TParm inv_stockm = parm.getParm("INV_STOCKM");
		        for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
		            result = InvStockMTool.getInstance().onUpdateStockQty(inv_stockm.getRow(
		                i), conn);
		            if (result.getErrCode() < 0) {
		                return result;
		            }
		        }
		        String org_code = "";
		        String inv_code = "";
		        TParm inv_stockd = new TParm();
		        for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
		            inv_stockd = inv_stockm.getRow(i);
		            org_code = inv_stockd.getValue("ORG_CODE");
		            inv_code = inv_stockd.getValue("INV_CODE");
		            TParm batchSeq = new TParm(TJDODBTool.getInstance().select(INVSQL.
		                getInvStockMaxBatchSeq(org_code, inv_code)));
		            inv_stockd.setData("BATCH_SEQ", batchSeq.getInt("BATCH_SEQ", 0));
		            result = InvStockDTool.getInstance().onUpdateStockQtyByTear(inv_stockd,
		                conn);
		            if (result.getErrCode() < 0) {
		                return result;
		            }
		        }
		        TParm inv_stockdd = parm.getParm("INV_STOCKDD");
		        for (int i = 0; i < inv_stockdd.getCount("INV_CODE"); i++) {
		            result = InvStockDDTool.getInstance().onUpdateStockQtyByPack(
		                inv_stockdd.getRow(i), conn);
		            if (result.getErrCode() < 0) {
		                return result;
		            }
		        }
		        return result;
		    }

		    /**
		     * 诊疗包拆包（非序号管理）
		     * @param parm TParm
		     * @param conn TConnection
		     * @return TParm
		     */
		    public TParm onINVTearPackB(TParm parm, TConnection conn) {
		        TParm result = new TParm();
		        TParm inv_packstockm = parm.getParm("INV_PACKSTOCKM");
		        result = InvPackStockMTool.getInstance().onUpdateQtyBySupReq(inv_packstockm,
		            conn);
		        if (result.getErrCode() < 0) {
		            return result;
		        }
		        TParm inv_packstockd = parm.getParm("INV_PACKSTOCKD");
		        for (int i = 0; i < inv_packstockd.getCount("INV_CODE"); i++) {
		            result = InvPackStockDTool.getInstance().onUpdateQtyBySupReq(
		                inv_packstockd.getRow(i), conn);
		            if (result.getErrCode() < 0) {
		                return result;
		            }
		        }
		        TParm inv_stockm = parm.getParm("INV_STOCKM");
		        for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
		            result = InvStockMTool.getInstance().onUpdateStockQty(inv_stockm.getRow(
		                i), conn);
		            if (result.getErrCode() < 0) {
		                return result;
		            }
		        }
		        String org_code = "";
		        String inv_code = "";
		        TParm inv_stockd = new TParm();
		        for (int i = 0; i < inv_stockm.getCount("INV_CODE"); i++) {
		            inv_stockd = inv_stockm.getRow(i);
		            org_code = inv_stockd.getValue("ORG_CODE");
		            inv_code = inv_stockd.getValue("INV_CODE");
		            TParm batchSeq = new TParm(TJDODBTool.getInstance().select(INVSQL.
		                getInvStockMaxBatchSeq(org_code, inv_code)));
		            inv_stockd.setData("BATCH_SEQ", batchSeq.getInt("BATCH_SEQ", 0));
		            result = InvStockDTool.getInstance().onUpdateStockQtyByTear(inv_stockd,
		                conn);
		            if (result.getErrCode() < 0) {
		                return result;
		            }
		        }
		        return result;
		    }
		    
		    
		    
		    
		    
		    /**
		     * 处理手术包打包入库保存
		     * @param parm TParm
		     * @param connection TConnection
		     * @return TParm
		     */
		    public TParm onSavePackAgeGYS(TParm parm, TConnection connection) {
//		    	System.out.println("-----onSavePackAgeGYS------");
		        TParm result = new TParm();
		        //扣库数据
		        TParm stockSaveParm = parm.getParm("SAVEPARM");
		        TParm stockMParm = stockSaveParm.getParm("STOCKM");
		        int stockMCount = stockMParm.getCount();
		        for (int i = 0; i < stockMCount; i++) {
		            TParm oneParm = stockMParm.getRow(i);
		            //出库处理
		            oneParm.setData("STOCK_QTY", oneParm.getDouble("STOCK_QTY") * -1);
		            result = InvStockMTool.getInstance().updateStockMQty(oneParm,
		                connection);    // 2013-06-04  INVStockMtool.
		            if (result.getErrCode() < 0) {
		                err(result.getErrCode() + " " + result.getErrText());
		                return result;
		            }
		        }
		        //处理库存明细档
		        TParm stockD = stockSaveParm.getParm("STOCKD");
		        int stockDCount = stockD.getCount();
		        for (int i = 0; i < stockDCount; i++) {
		            TParm oneParm = stockD.getRow(i);
		            //出库处理
		            oneParm.setData("STOCK_QTY", oneParm.getDouble("STOCK_QTY") * -1);
		            result = InvStockDTool.getInstance().updateStockQty(oneParm,
		                connection);  // 2013-06-04   INVStockDtool.
		            if (result.getErrCode() < 0) {
		                err(result.getErrCode() + " " + result.getErrText());
		                return result;
		            }
		        }
		        //物资库存序号管理档
		        TParm stockDD = stockSaveParm.getParm("STOCKDD");
		        int stockDDCount = stockDD.getCount();
		        for (int i = 0; i < stockDDCount; i++) {
		            TParm oneParm = stockDD.getRow(i);
		            //保存序号管理表
		            result = InvStockDDTool.getInstance().updatePackAge(oneParm,
		                connection);   
		            if (result.getErrCode() < 0) {
		                err(result.getErrCode() + " " + result.getErrText());
		                return result;
		            }
		        }
		        //处理手术包库存档
		        result = INVPackStockTool.getInstance().dealSavePackStock(parm,
		            connection);      
		        if (result == null || result.getErrCode() < 0) {
		            return result;
		        }

		        return result;
		    }
		    
		    
		    /**
		     * 保存回收和消毒记录
		     * @param parm TParm
		     * @param connection TConnection
		     * @return TParm
		     */
		    public TParm saveBackPackAndDisnfection(TParm parm, TConnection connection) {
		        TParm result = new TParm();
		        if (parm == null)
		            return result.newErrParm( -1, "参数错误");
		        //处理
		        String[] str = (String[]) parm.getData("SAVESQL");
		        if (str.length != 0) {
		            int rowCount = str.length;
		            //循环所有sql
		            for (int i = 0; i < rowCount; i++) {
		                //执行一个sql
		                result = new TParm(TJDODBTool.getInstance().update(str[i],
		                    connection));
		                if (result.getErrCode() < 0)
		                    return result;
		            }
		        }
		        TParm disinfectionParm = parm.getParm("DISINFECTIONPARM");
		        if (disinfectionParm == null)
		            return result.newErrParm( -1, "参数错误");
		        if (disinfectionParm.getCount() != 0) {
		            //保存模式
		            String action = disinfectionParm.getValue("ACTIONMODE");
		            if (action.equals("SAVE")) {
		                //消毒保存
		                result = InvDisinfectionTool.getInstance().insertValue(
		                    disinfectionParm,
		                    connection);   //wm2013-05-31   INVDisinfectionTool.
		                if (result == null || result.getErrCode() < 0)
		                    out(result.getErrText());
		                return result;
		            }
		            else {
		                //取消删除记录
		                result = InvDisinfectionTool.getInstance().deleteValue(
		                    disinfectionParm, connection);			//wm2013-05-31  INVDisinfectionTool.
		                if (result == null || result.getErrCode() < 0)
		                    out(result.getErrText());
		                return result;
		            }
		        }
		        return result;
		    }
		    
		    /**
		     * 新增手术包出库单 （不引用出库单）
		     * @param parm TParm
		     * @param conn TConnection
		     * @return TParm
		     */
		    public TParm onInsertInvSupDispenseOnlyPack(TParm parm, TConnection conn) {
		    	System.out.println("-------inv--------jdo--------inv--------");
		        TParm result = new TParm();
		        //INV_SUP_DISPENSEM
		        TParm inv_sup_dispensem = parm.getParm("INV_SUP_DISPENSEM");
		        System.out.println("-------主档inv_sup_dispensem--------"+inv_sup_dispensem);
		        result = InvSupDispenseMTool.getInstance().onInsert(inv_sup_dispensem,
		            conn);
		        if (result.getErrCode() < 0) {
		        	System.out.println("-------主档保存失败--------");
		            return result;
		        }
		        //INV_SUP_DISPENSED
		        TParm inv_sup_dispensed = parm.getParm("INV_SUP_DISPENSED");
		        for (int i = 0; i < inv_sup_dispensed.getCount("DISPENSE_NO"); i++) {
		            result = InvSupDispenseDTool.getInstance().onInsert(
		                inv_sup_dispensed.
		                getRow(i), conn);
		            if (result.getErrCode() < 0) {
		            	System.out.println("-------INV_SUP_DISPENSED--------");
		                return result;
		            }
		        }
		        //INV_SUP_DISPENSEDD
		        TParm inv_sup_dispensedd = parm.getParm("INV_SUP_DISPENSEDD");
		        for (int i = 0; i < inv_sup_dispensedd.getCount("INV_CODE"); i++) {
		            result = InvSupDispenseDDTool.getInstance().onInsert(
		                inv_sup_dispensedd.
		                getRow(i), conn);
		            if (result.getErrCode() < 0) {
		            	System.out.println("-------INV_SUP_DISPENSEDD--------");
		                return result;
		            }
		        }
		        //INV_PACKSTOCKM
		        TParm inv_packstockm = parm.getParm("INV_PACKSTOCKM");
		        for (int i = 0; i < inv_packstockm.getCount("PACK_CODE"); i++) {
		            if (inv_packstockm.getInt("PACK_SEQ_NO", i) == 0) {
		                //没有序号管理的手术包,扣除主库中手术包的库存量
		                result = InvPackStockMTool.getInstance().onUpdateQtyBySupReq(
		                    inv_packstockm.getRow(i), conn);
		            }
		            else {
		                //有序号管理的手术包,更新手术包的状态为出库
		                result = InvPackStockMTool.getInstance().onUpdateStatusBySupReq(
		                    inv_packstockm.getRow(i), conn);
		            }
		            if (result.getErrCode() < 0) {
		            	System.out.println("-------INV_PACKSTOCKM--------");
		                return result;
		            }
		        }
		        //INV_PACKSTOCKD
		        TParm inv_packstockd = parm.getParm("INV_PACKSTOCKD");
		        for (int i = 0; i < inv_packstockd.getCount("PACK_CODE"); i++) {
		            if (inv_packstockd.getInt("PACK_SEQ_NO", i) == 0) {
		                //没有序号管理的手术包,扣除手术包库存明细中的库存量
		                result = InvPackStockDTool.getInstance().onUpdateQtyBySupReq(
		                    inv_packstockd.getRow(i), conn);
		            }
		            else {
		                //有序号管理的手术包,删除手术包明细中的一次性物资
		                if ("Y".equals(inv_packstockd.getValue("ONCE_USE_FLG", i))) {
		                    result = InvPackStockDTool.getInstance().
		                        onDeleteOnceUserInvBySupReq(inv_packstockd.getRow(i),
		                        conn);
		                }
		            }
		            if (result.getErrCode() < 0) {
		            	System.out.println("-------INV_PACKSTOCKD--------");
		                return result;
		            }
		        }
//		        //INV_SUPREQUESTD
//		        TParm inv_suprequestd = parm.getParm("INV_SUPREQUESTD");
//		        for (int i = 0; i < inv_suprequestd.getCount("REQUEST_NO"); i++) {
//		            result = InvSupRequestDTool.getInstance().
//		                onUpdateFlgAndQtyBySupDispense(inv_suprequestd.getRow(i), conn);
//		            if (result.getErrCode() < 0) {
//		                return result;
//		            }
//		        }
//		        //INV_SUPREQUESTM
//		        TParm inv_suprequestm = parm.getParm("INV_SUPREQUESTM");
//		        result = InvSupRequestMTool.getInstance().onUpdateFlgBySupDispense(
//		            inv_suprequestm, conn);
//		        if (result.getErrCode() < 0) {
//		            return result;
//		        }
		        return result;
		    }
		    //----------------供应室end---------------------
		
}
