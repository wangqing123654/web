package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import java.util.Vector;
import jdo.inv.InvSupRequestDTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;
import jdo.inv.InvSupDispenseMTool;
import jdo.inv.InvSupDispenseDTool;
import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 供应室出库</p>
 *
 * <p>Description: 供应室出库</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.8
 * @version 1.0
 */
public class INVSupDispenseMControl
    extends TControl {

    // 出库主表
    private TTable tableM;
    // 耗材出库
    private TTable tableInv;
    // 手术包出库
    private TTable tablePack;
    // 序号管理物资
    private TTable tableDD;
    // 出库方式
    private String pack_mode;

    public INVSupDispenseMControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableInv = getTable("TABLED");
        tablePack = getTable("TABLED2");
        tableDD = getTable("TABLEDD");

        this.setValue("CHECK_USER", Operator.getID());
        this.setValue("CHECK_DATE", SystemTool.getInstance().getDate());

        // 出库日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

        // 初始化tableInv的Parm
        TParm parmInv = new TParm();
        String[] verInv = {
            "DISPENSE_NO", "SEQ_NO", "PACK_MODE", "INV_CODE",
            "INV_CHN_DESC", "INVSEQ_NO", "QTY", "STOCK_UNIT",
            "COST_PRICE", "REQUEST_SEQ", "BATCH_NO", "BATCH_SEQ",
            "VALID_DATE", "DISPOSAL_FLG", "OPT_USER", "OPT_DATE",
            "OPT_TERM"};
        for (int i = 0; i < verInv.length; i++) {
            parmInv.setData(verInv[i], new Vector());
        }
        tableInv.setParmValue(parmInv);

        // 初始化tablePack的Parm
        TParm parmPack = new TParm();
        for (int i = 0; i < verInv.length; i++) {
            parmInv.setData(verInv[i], new Vector());
        }
        tablePack.setParmValue(parmPack);

        // 添加侦听事件
        addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
                         "onTableInvChangeValue");
        // 添加侦听事件
        addEventListener("TABLED2->" + TTableEvent.CHANGE_VALUE,
                         "onTablePackChangeValue");
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TParm parm = new TParm();
        TParm result = new TParm();
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            // 新增出库保存
            if ("0".equals(pack_mode)) {
                // 检核一般物资的库存量
                if (!checkSupInv()) {
                    return;
                }
                /* 新增一般物资的出库单 */
                //取得INV_SUP_DISPENSEM数据
                getInvSupDispenseMData(parm);
                //取得INV_SUP_DISPENSED数据
                getInvSupDispenseDInvData(parm);
                //取得INV_STOCKM数据
                getInvStockMData(parm);
                //取得INV_STOCKD数据
                getInvStockDData(parm);
                //取得INV_STOCKDD数据
                getInvStockDDData(parm);
                //取得INV_SUPREQUESTD数据
                getInvSupRequestDData(parm);
                //取得INV_SUPREQUESTM数据
                getInvSupRequestMData(parm);
                //System.out.println("parm===="+parm);
                // 新增一般物资出库
                result = result = TIOM_AppServer.executeAction(
                    "action.inv.INVSupDispenseAction",
                    "onInsertInvSupDispenseByInv", parm);
            }
            else {
                // 检核手术包的库存量
                if (!checkSupPack()) {
                    return;
                }
                /* 新增手术包的出库单 */
                //取得INV_SUP_DISPENSEM数据
                getInvSupDispenseMData(parm);
                //取得INV_SUP_DISPENSED数据
                getInvSupDispenseDPackData(parm);
                //取得INV_SUP_DISPENSMDD数据(手术包出库)
                getInvSupDispenseDDData(parm);
                //取得INV_PACKSTOCKM数据
                getInvPackStockMData(parm);
                //取得INV_PACKSTOCKD数据
                getInvPackStockDData(parm);
                //取得INV_SUPREQUESTD数据
                getInvSupRequestDData(parm);
                //取得INV_SUPREQUESTM数据
                getInvSupRequestMData(parm);
                // 新增手术包出库
                result = result = TIOM_AppServer.executeAction(
                    "action.inv.INVSupDispenseAction",
                    "onInsertInvSupDispenseByPack", parm);
            }
        }
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
            return;
        }
        this.messageBox("保存成功");
        this.setValue("DISPENSE_NO",
                      parm.getParm("INV_SUP_DISPENSEM").getValue("DISPENSE_NO"));
        onPrint();
    }

    /**
     * 检核一般物资的库存量
     * @return boolean
     */
    private boolean checkSupInv() {
        TParm parm = tableInv.getParmValue();
        String org_code = this.getValueString("FROM_ORG_CODE");
        String inv_code = "";
        TParm parmStock = new TParm();
        for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
            inv_code = parm.getValue("INV_CODE", i);
            parmStock = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getINVStockQty(org_code, inv_code)));
            if (parmStock == null || parmStock.getCount() <= 0) {
                this.messageBox(parm.getValue("INV_CHN_DESC", i) + "无库存");
                return false;
            }
            if (parmStock.getDouble("STOCK_QTY", 0) < parm.getDouble("QTY", i)) {
                this.messageBox(parm.getValue("INV_CHN_DESC", i) + "库存不足");
                return false;
            }
        }
        return true;
    }

    /**
     * 检核手术包的库存量
     * @return boolean
     */
    private boolean checkSupPack() {
        TParm parm = tablePack.getParmValue();
        String org_code = this.getValueString("FROM_ORG_CODE");
        String pack_code = "";
        int pack_seq_no = 0;
        int batch_no = 0;
        TParm parmStock = new TParm();
        for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
            pack_code = parm.getValue("INV_CODE", i);
            pack_seq_no = parm.getInt("INVSEQ_NO", i);
            batch_no = parm.getInt("PACK_BATCH_NO", i);
            parmStock = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getINVPackStockQty(org_code, pack_code, pack_seq_no, batch_no)));
            if (parmStock == null || parmStock.getCount() <= 0) {
                this.messageBox(parm.getValue("INV_CHN_DESC", i) + "无库存");
                return false;
            }
            if (parmStock.getDouble("STOCK_QTY", 0) < parm.getDouble("QTY", i)) {
                this.messageBox(parm.getValue("INV_CHN_DESC", i) + "库存不足");
                return false;
            }
        }
        return true;
    }

    /**
     * 取得INV_SUP_DISPENSEM数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseMData(TParm parm) {
        TParm tableMParm = tableM.getParmValue().getRow(tableM.getSelectedRow());
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            String dispense_no = SystemTool.getInstance().getNo("ALL", "INV",
                "INV_SUPDISPENSE", "No");
            tableMParm.setData("DISPENSE_NO", dispense_no);
        }
        tableMParm.setData("FINA_FLG", "Y");
        if ("0".equals(pack_mode)) {
            tableMParm.setData("PACK_MODE", "0");
        }
        else {
            tableMParm.setData("PACK_MODE", "1");
        }
        parm.setData("INV_SUP_DISPENSEM", tableMParm.getData());
        return parm;
    }

    /**
     * 取得INV_SUP_DISPENSED数据(一般物资)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseDInvData(TParm parm) {
        TNull tnull = new TNull(Timestamp.class);
        String dispense_no = parm.getParm("INV_SUP_DISPENSEM").getValue(
            "DISPENSE_NO");
        TParm tableInvParm = tableInv.getParmValue();
        TParm inv_sup_dispensed = new TParm();
        String org_code = this.getValueString("FROM_ORG_CODE");
        String inv_code = "";
        // 出库量
        double qty = 0;
        // 库存量
        double stock_qty = 0;
        // 出库单细项序号
        int seq_no = 1;
        for (int i = 0; i < tableInvParm.getCount("INV_CODE"); i++) {
            inv_code = tableInvParm.getValue("INV_CODE", i);
            qty = tableInvParm.getDouble("QTY", i);
            TParm inv_stockd = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockD(org_code, inv_code)));
            //System.out.println("inv_stockd==="+inv_stockd);
            for (int j = 0; j < inv_stockd.getCount("INV_CODE"); j++) {
                stock_qty = inv_stockd.getDouble("STOCK_QTY", j);
                //System.out.println("stock_qty=="+stock_qty);
                if (qty > stock_qty) {
                    inv_sup_dispensed.addData("DISPENSE_NO", dispense_no);
                    inv_sup_dispensed.addData("SEQ_NO", seq_no);
                    inv_sup_dispensed.addData("PACK_MODE", tableInvParm.
                                              getValue("PACK_MODE", i));
                    inv_sup_dispensed.addData("INV_CODE", inv_code);
                    inv_sup_dispensed.addData("INVSEQ_NO",
                                              tableInvParm.getInt("INVSEQ_NO",
                        i));
                    inv_sup_dispensed.addData("QTY", stock_qty);
                    inv_sup_dispensed.addData("STOCK_UNIT", tableInvParm.
                                              getValue("STOCK_UNIT", i));
                    inv_sup_dispensed.addData("COST_PRICE", tableInvParm.
                                              getDouble("COST_PRICE", i));
                    inv_sup_dispensed.addData("REQUEST_SEQ", tableInvParm.
                                              getInt("REQUEST_SEQ", i));
                    inv_sup_dispensed.addData("BATCH_NO",
                                              inv_stockd.getValue("BATCH_NO", j));
                    inv_sup_dispensed.addData("BATCH_SEQ",
                                              inv_stockd.getInt("BATCH_SEQ", j));
                    inv_sup_dispensed.addData("VALID_DATE",
                                              inv_stockd.getData("VALID_DATE",
                        j) == null ? tnull : inv_stockd.getTimestamp(
                            "VALID_DATE", j));
                    inv_sup_dispensed.addData("DISPOSAL_FLG", tableInvParm.
                                              getValue("DISPOSAL_FLG", i));
                    inv_sup_dispensed.addData("OPT_USER",
                                              tableInvParm.getValue("OPT_USER",
                        i));
                    inv_sup_dispensed.addData("OPT_DATE", tableInvParm.
                                              getTimestamp("OPT_DATE", i));
                    inv_sup_dispensed.addData("OPT_TERM",
                                              tableInvParm.getValue("OPT_TERM",
                        i));
                    qty = qty - stock_qty;
                    seq_no++;
                }
                else {
                    inv_sup_dispensed.addData("DISPENSE_NO", dispense_no);
                    inv_sup_dispensed.addData("SEQ_NO", seq_no);
                    inv_sup_dispensed.addData("PACK_MODE", tableInvParm.
                                              getValue("PACK_MODE", i));
                    inv_sup_dispensed.addData("INV_CODE", inv_code);
                    inv_sup_dispensed.addData("INVSEQ_NO",
                                              tableInvParm.getInt("INVSEQ_NO",
                        i));
                    inv_sup_dispensed.addData("QTY", qty);
                    inv_sup_dispensed.addData("STOCK_UNIT", tableInvParm.
                                              getValue("STOCK_UNIT", i));
                    inv_sup_dispensed.addData("COST_PRICE", tableInvParm.
                                              getDouble("COST_PRICE", i));
                    inv_sup_dispensed.addData("REQUEST_SEQ", tableInvParm.
                                              getInt("REQUEST_SEQ", i));
                    inv_sup_dispensed.addData("BATCH_NO",
                                              inv_stockd.getValue("BATCH_NO", j));
                    inv_sup_dispensed.addData("BATCH_SEQ",
                                              inv_stockd.getInt("BATCH_SEQ", j));
                    inv_sup_dispensed.addData("VALID_DATE",
                                              inv_stockd.getData("VALID_DATE",
                        j) == null ? tnull : inv_stockd.getTimestamp(
                            "VALID_DATE", j));
                    inv_sup_dispensed.addData("DISPOSAL_FLG", tableInvParm.
                                              getValue("DISPOSAL_FLG", i));
                    inv_sup_dispensed.addData("OPT_USER",
                                              tableInvParm.getValue("OPT_USER",
                        i));
                    inv_sup_dispensed.addData("OPT_DATE", tableInvParm.
                                              getTimestamp("OPT_DATE", i));
                    inv_sup_dispensed.addData("OPT_TERM",
                                              tableInvParm.getValue("OPT_TERM",
                        i));
                    seq_no++;
                    break;
                }
            }
        }
        parm.setData("INV_SUP_DISPENSED", inv_sup_dispensed.getData());
        return parm;
    }

    /**
     * 取得INV_SUP_DISPENSED数据(手术包)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseDPackData(TParm parm) {
        String dispense_no = parm.getParm("INV_SUP_DISPENSEM").getValue(
            "DISPENSE_NO");
        TNull tnull = new TNull(Timestamp.class);
        TParm tablePackParm = tablePack.getParmValue();
        TParm inv_sup_dispensed = new TParm();
        for (int i = 0; i < tablePackParm.getCount("INV_CODE"); i++) {
            inv_sup_dispensed.addData("DISPENSE_NO", dispense_no);
            inv_sup_dispensed.addData("SEQ_NO", i + 1);
            inv_sup_dispensed.addData("PACK_MODE", tablePackParm.
                                      getValue("PACK_MODE", i));
            inv_sup_dispensed.addData("INV_CODE",
                                      tablePackParm.getValue("INV_CODE", i));
            inv_sup_dispensed.addData("INVSEQ_NO",
                                      tablePackParm.getInt("INVSEQ_NO", i));
            inv_sup_dispensed.addData("QTY", tablePackParm.getDouble("QTY", i));
            inv_sup_dispensed.addData("STOCK_UNIT", "");
            inv_sup_dispensed.addData("COST_PRICE", tablePackParm.
                                      getDouble("COST_PRICE", i));
            inv_sup_dispensed.addData("REQUEST_SEQ", tablePackParm.
                                      getInt("REQUEST_SEQ", i));
            inv_sup_dispensed.addData("BATCH_NO", "");
            inv_sup_dispensed.addData("BATCH_SEQ", "");
            inv_sup_dispensed.addData("VALID_DATE", tnull);
            inv_sup_dispensed.addData("DISPOSAL_FLG", tablePackParm.
                                      getValue("DISPOSAL_FLG", i));
            inv_sup_dispensed.addData("OPT_USER",
                                      tablePackParm.getValue("OPT_USER", i));
            inv_sup_dispensed.addData("OPT_DATE",
                                      tablePackParm.getTimestamp("OPT_DATE", i));
            inv_sup_dispensed.addData("OPT_TERM",
                                      tablePackParm.getValue("OPT_TERM", i));
            
            inv_sup_dispensed.addData("BARCODE",tablePackParm.getValue("BARCODE", i));
            inv_sup_dispensed.addData("PACK_BATCH_NO",tablePackParm.getInt("PACK_BATCH_NO", i));
        }
        parm.setData("INV_SUP_DISPENSED", inv_sup_dispensed.getData());
        return parm;
    }


    /**
     * 取得INV_SUP_DISPENSEDD数据(手术包出库)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupDispenseDDData(TParm parm) {
        TNull tnull = new TNull(Timestamp.class);
        String dispense_no = parm.getParm("INV_SUP_DISPENSEM").getValue(
            "DISPENSE_NO");
        TParm inv_sup_dispensed = parm.getParm("INV_SUP_DISPENSED");
        String org_code = this.getValueString("FROM_ORG_CODE");
        String pack_code = "";
        int pack_seq_no = 0;
        int seq_no = 1;
        int batch_no = 0;
        TParm inv_sup_dispensedd = new TParm();
        Timestamp datetime = SystemTool.getInstance().getDate();
        for (int i = 0; i < inv_sup_dispensed.getCount("DISPENSE_NO"); i++) {
            pack_code = inv_sup_dispensed.getValue("INV_CODE", i);
            pack_seq_no = inv_sup_dispensed.getInt("INVSEQ_NO",i);
            batch_no = inv_sup_dispensed.getInt("PACK_BATCH_NO",i);
            TParm supParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvSupDispenseDD(org_code, pack_code, pack_seq_no, batch_no)));
            for (int j = 0; j < supParm.getCount("INV_CODE"); j++) {
                inv_sup_dispensedd.addData("DISPENSE_NO", dispense_no);
                inv_sup_dispensedd.addData("SEQ_NO", seq_no);
                inv_sup_dispensedd.addData("PACK_MODE", "1");
                inv_sup_dispensedd.addData("PACK_CODE", pack_code);
                inv_sup_dispensedd.addData("PACK_SEQ_NO", pack_seq_no);
                inv_sup_dispensedd.addData("INV_CODE",
                                           supParm.getValue("INV_CODE", j));
                inv_sup_dispensedd.addData("INVSEQ_NO",
                                           supParm.getInt("INVSEQ_NO", j));
                inv_sup_dispensedd.addData("ONCE_USE_FLG",
                                           supParm.getValue("ONCE_USE_FLG", j));
                inv_sup_dispensedd.addData("QTY",
                                           supParm.getDouble("QTY", j) *
                                           inv_sup_dispensed.getDouble("QTY", i));
                inv_sup_dispensedd.addData("STOCK_UNIT",
                                           supParm.getValue("STOCK_UNIT", j));
                inv_sup_dispensedd.addData("COST_PRICE",
                                           supParm.getDouble("COST_PRICE", j));
                inv_sup_dispensedd.addData("REQUEST_SEQ",
                                           inv_sup_dispensed.
                                           getInt("REQUEST_NO", i));
                inv_sup_dispensedd.addData("BATCH_NO",
                                           supParm.getValue("BATCH_NO", j));
                inv_sup_dispensedd.addData("VALID_DATE",
                                           supParm.getData("VALID_DATE", j) == null ?
                                           tnull :
                                           supParm.getTimestamp("VALID_DATE", j));
                inv_sup_dispensedd.addData("DISPOSAL_FLG",
                                           inv_sup_dispensed.
                                           getValue("DISPOSAL_FLG", i));
                inv_sup_dispensedd.addData("OPT_USER", Operator.getID());
                inv_sup_dispensedd.addData("OPT_DATE", datetime);
                inv_sup_dispensedd.addData("OPT_TERM", Operator.getIP());
                
                inv_sup_dispensedd.addData("BARCODE", supParm.getValue("BARCODE", j));
                inv_sup_dispensedd.addData("PACK_BATCH_NO", supParm.getInt("PACK_BATCH_NO", j));
                seq_no++;
            }
        }
        parm.setData("INV_SUP_DISPENSEDD", inv_sup_dispensedd.getData());
        return parm;
    }

    /**
     * 取得INV_STOCKM数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvStockMData(TParm parm) {
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm tableInvParm = tableInv.getParmValue();
        for (int i = 0; i < tableInvParm.getCount("INV_CODE"); i++) {
            tableInvParm.addData("ORG_CODE", org_code);
        }
        parm.setData("INV_STOCKM", tableInvParm.getData());
        return parm;
    }

    /**
     * 取得INV_STOCKD数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvStockDData(TParm parm) {
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm inv_stockd = parm.getParm("INV_SUP_DISPENSED");
        //System.out.println("inv_stockd==111==" + inv_stockd);
        for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
            inv_stockd.addData("ORG_CODE", org_code);
        }
        //System.out.println("inv_stockd==222==" + inv_stockd);
//        System.out.println("inv_stockd==333==" +
//                           parm.getParm("INV_SUP_DISPENSED"));
        parm.setData("INV_STOCKD", inv_stockd.getData());
        return parm;
    }

    /**
     * 取得INV_STOCKDD数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvStockDDData(TParm parm) {
        TParm inv_stockd = parm.getParm("INV_STOCKD");
        TParm inv_stockdd = new TParm();
        String inv_code = "";
        String org_code = this.getValueString("TO_ORG_CODE");
        for (int i = 0; i < inv_stockd.getCount("INV_CODE"); i++) {
            inv_code = inv_stockd.getValue("INV_CODE", i);
            //System.out.println("sqlStr==="+sqlStr);
            TParm inv_base = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBase(inv_code)));
            if ("N".equals(inv_base.getValue("SEQMAN_FLG", 0))) {
                continue;
            }
            else {
                inv_stockdd.addData("ORG_CODE", org_code);
                inv_stockdd.addData("INV_CODE",
                                    inv_stockd.getValue("INV_CODE", i));
                inv_stockdd.addData("BATCH_NO",
                                    inv_stockd.getValue("BATCH_NO", i));
                inv_stockdd.addData("DISPENSE_NO",
                                    inv_stockd.getValue("DISPENSE_NO", i));
                inv_stockdd.addData("OPT_TERM",
                                    inv_stockd.getValue("OPT_TERM", i));
                inv_stockdd.addData("OPT_DATE",
                                    inv_stockd.getTimestamp("OPT_DATE", i));
                inv_stockdd.addData("REQUEST_SEQ",
                                    inv_stockd.getInt("REQUEST_SEQ", i));
                inv_stockdd.addData("SEQ_NO", inv_stockd.getInt("SEQ_NO", i));
                inv_stockdd.addData("OPT_USER",
                                    inv_stockd.getValue("OPT_USER", i));
                inv_stockdd.addData("INVSEQ_NO",
                                    inv_stockd.getInt("INVSEQ_NO", i));
                inv_stockdd.addData("STOCK_UNIT",
                                    inv_stockd.getValue("STOCK_UNIT", i));
                inv_stockdd.addData("BATCH_SEQ",
                                    inv_stockd.getInt("BATCH_SEQ", i));
                inv_stockdd.addData("VALID_DATE",
                                    inv_stockd.getData("VALID_DATE", i));
                inv_stockdd.addData("QTY", inv_stockd.getDouble("QTY", i));
                inv_stockdd.addData("PACK_MODE",
                                    inv_stockd.getValue("PACK_MODE", i));
                inv_stockdd.addData("DISPOSAL_FLG",
                                    inv_stockd.getValue("DISPOSAL_FLG", i));
                inv_stockdd.addData("COST_PRICE",
                                    inv_stockd.getDouble("COST_PRICE", i));
            }
        }
        parm.setData("INV_STOCKDD", inv_stockdd.getData());
        return parm;
    }

    /**
     * 取得INV_PACKSTOCKM数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPackStockMData(TParm parm) {
        TParm packParm = tablePack.getParmValue();
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm inv_packstockm = new TParm();
        Timestamp datetime = SystemTool.getInstance().getDate();
        for (int i = 0; i < packParm.getCount("INV_CODE"); i++) {
            inv_packstockm.addData("ORG_CODE", org_code);
            inv_packstockm.addData("PACK_CODE", packParm.getValue("INV_CODE", i));
            inv_packstockm.addData("PACK_SEQ_NO",
                                   packParm.getInt("INVSEQ_NO", i));
            inv_packstockm.addData("QTY", packParm.getDouble("QTY", i));
            if (packParm.getInt("INVSEQ_NO", i) == 0) {
                inv_packstockm.addData("STATUS", "0");
            }
            else {
                inv_packstockm.addData("STATUS", "1");
            }
            inv_packstockm.addData("OPT_USER", Operator.getID());
            inv_packstockm.addData("OPT_DATE", datetime);
            inv_packstockm.addData("OPT_TERM", Operator.getIP());
            
            inv_packstockm.addData("BARCODE", packParm.getValue("BARCODE", i));
            inv_packstockm.addData("PACK_BATCH_NO", packParm.getInt("PACK_BATCH_NO", i));
        }
        parm.setData("INV_PACKSTOCKM",inv_packstockm.getData());
        return parm;
    }

    /**
     * 取得INV_PACKSTOCKD数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPackStockDData(TParm parm) {
        TParm inv_sup_dispensedd = parm.getParm("INV_SUP_DISPENSEDD");
        String org_code = this.getValueString("FROM_ORG_CODE");
        TParm inv_packstockd = new TParm();
        for (int i = 0; i < inv_sup_dispensedd.getCount("INV_CODE"); i++) {
            inv_packstockd.addData("ORG_CODE", org_code);
            inv_packstockd.addData("PACK_CODE",
                                   inv_sup_dispensedd.getValue("PACK_CODE", i));
            inv_packstockd.addData("PACK_SEQ_NO",
                                   inv_sup_dispensedd.getInt("PACK_SEQ_NO", i));
            inv_packstockd.addData("INV_CODE",
                                   inv_sup_dispensedd.getValue("INV_CODE", i));
            inv_packstockd.addData("INVSEQ_NO",
                                   inv_sup_dispensedd.getInt("INVSEQ_NO", i));
            inv_packstockd.addData("QTY", inv_sup_dispensedd.getDouble("QTY", i));
            inv_packstockd.addData("OPT_USER",
                                   inv_sup_dispensedd.getValue("OPT_USER", i));
            inv_packstockd.addData("OPT_DATE",
                                   inv_sup_dispensedd.getTimestamp("OPT_DATE",
                i));
            inv_packstockd.addData("OPT_TERM",
                                   inv_sup_dispensedd.getValue("OPT_TERM", i));
            inv_packstockd.addData("ONCE_USE_FLG",
                                   inv_sup_dispensedd.getValue("ONCE_USE_FLG",
                i));
            
            inv_packstockd.addData("BARCODE", inv_sup_dispensedd.getValue("BARCODE", i));
            inv_packstockd.addData("PACK_BATCH_NO", inv_sup_dispensedd.getValue("PACK_BATCH_NO", i));
        }
        parm.setData("INV_PACKSTOCKD", inv_packstockd.getData());
        return parm;
    }

    /**
     * 取得INV_SUPREQUESTM数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupRequestMData(TParm parm) {
        TParm inv_requestd = parm.getParm("INV_SUPREQUESTD");
        TParm inv_requestm = new TParm();
        inv_requestm.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        String update_flg = "3";
        for (int i = 0; i < inv_requestd.getCount("REQUEST_NO"); i++) {
            if ("1".equals(inv_requestd.getValue("UPDATE_FLG", i))) {
                update_flg = "1";
                break;
            }
        }
        inv_requestm.setData("UPDATE_FLG", update_flg);
        inv_requestm.setData("OPT_USER", Operator.getID());
        inv_requestm.setData("OPT_DATE",
                             SystemTool.getInstance().getDate());
        inv_requestm.setData("OPT_TERM", Operator.getIP());
        parm.setData("INV_SUPREQUESTM", inv_requestm.getData());
        return parm;
    }

    /**
     * 取得INV_SUPREQUESTD数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvSupRequestDData(TParm parm) {
        TParm inv_requestd = new TParm();
        TParm requestParm = new TParm();
        String request_no = this.getValueString("REQUEST_NO");
        requestParm.setData("REQUEST_NO", request_no);
        requestParm = InvSupRequestDTool.getInstance().onQuery(requestParm);
        if ("0".equals(pack_mode)) {
            TParm inv_parm = tableInv.getParmValue();
            String update_flg = "";
            for (int i = 0; i < inv_parm.getCount("INV_CODE"); i++) {
                for (int j = 0; j < requestParm.getCount("INV_CODE"); j++) {
                    if (inv_parm.getInt("REQUEST_SEQ", i) ==
                        requestParm.getInt("SEQ_NO", j)) {
                        inv_requestd.addData("REQUEST_NO", request_no);
                        inv_requestd.addData("SEQ_NO",
                                             inv_parm.getInt("REQUEST_SEQ", i));
                        inv_requestd.addData("ACTUAL_QTY",
                                             inv_parm.getDouble("QTY", i));
                        if (inv_parm.getDouble("QTY", i) <
                            inv_requestd.getDouble("QTY", j)) {
                            update_flg = "1";
                        }
                        else {
                            update_flg = "3";
                        }
                        inv_requestd.addData("UPDATE_FLG", update_flg);
                        inv_requestd.addData("OPT_USER", Operator.getID());
                        inv_requestd.addData("OPT_DATE",
                                             SystemTool.getInstance().getDate());
                        inv_requestd.addData("OPT_TERM", Operator.getIP());
                        break;
                    }
                }
            }
        }
        else {
            TParm pack_parm = tablePack.getParmValue();
            String update_flg = "";
            for (int i = 0; i < pack_parm.getCount("INV_CODE"); i++) {
                for (int j = 0; j < requestParm.getCount("INV_CODE"); j++) {
                    if (pack_parm.getInt("REQUEST_SEQ", i) ==
                        requestParm.getInt("SEQ_NO", j)) {
                        inv_requestd.addData("REQUEST_NO", request_no);
                        inv_requestd.addData("SEQ_NO",
                                             pack_parm.getInt("REQUEST_SEQ", i));
                        inv_requestd.addData("ACTUAL_QTY",
                                             pack_parm.getDouble("QTY", i));
                        if (pack_parm.getDouble("QTY", i) <
                            inv_requestd.getDouble("QTY", j)) {
                            update_flg = "1";
                        }
                        else {
                            update_flg = "3";
                        }
                        inv_requestd.addData("UPDATE_FLG", update_flg);
                        inv_requestd.addData("OPT_USER", Operator.getID());
                        inv_requestd.addData("OPT_DATE",
                                             SystemTool.getInstance().getDate());
                        inv_requestd.addData("OPT_TERM", Operator.getIP());
                        break;
                    }
                }
            }
        }
        parm.setData("INV_SUPREQUESTD", inv_requestd.getData());
        return parm;
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (getRadioButton("UPDATE_A").isSelected()) {
            parm.setData("UPDATE_FLG_A", "Y");
        }
        else {
            parm.setData("UPDATE_FLG_B", "Y");
        }
        if (!"".equals(this.getValueString("PACK_MODE_Q"))) {
            parm.setData("PACK_MODE", getValueString("PACK_MODE_Q"));
        }
        if (!"".equals(this.getValueString("DISPENSE_NO_Q"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO_Q"));
        }
        if (!"".equals(this.getValueString("FROM_ORG_CODE_Q"))) {
            parm.setData("FROM_ORG_CODE", getValueString("FROM_ORG_CODE_Q"));
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
            parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE_Q"));
        }

        TParm result = InvSupDispenseMTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        tableM.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue("PACK_MODE_Q;DISPENSE_NO_Q;FROM_ORG_CODE_Q;"
                        +
                        "TO_ORG_CODE_Q;START_DATE;END_DATE;DISPENSE_NO;REQUEST_NO;"
                        +
                        "FROM_ORG_CODE;TO_ORG_CODE;REQUEST_TYPE;PACK_MODE;SUPTYPE_CODE;"
                        + "REN_CODE");
        ( (TRadioButton)this.getComponent("UPDATE_B")).setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("CHECK_DATE", date);
        tableM.removeRowAll();
        tableInv.removeRowAll();
        tablePack.removeRowAll();
        tableDD.removeRowAll();
        tableInv.setVisible(true);
        tablePack.setVisible(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("import")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int tableM_row = tableM.getSelectedRow();
        if ("0".equals(pack_mode)) {
            // 删除物资
            int tableInv_row = tableInv.getSelectedRow();
            if (tableInv_row > -1) {
                tableInv.removeRow(tableInv_row);
            }
            else {
                tableInv.removeRowAll();
                tableM.removeRow(tableM_row);
            }
        }
        else {
            // 删除手术包
            int tablePack_row = tablePack.getSelectedRow();
            if (tablePack_row > -1) {
                tablePack.removeRow(tablePack_row);
            }
            else {
                tablePack.removeRowAll();
                tableM.removeRow(tableM_row);
            }
        }
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("没有打印数据");
            return;
        }
        if ("0".equals(pack_mode)) {
            // 一般物资出库
            onPrintInv();
        }
        else {
            // 手术包出库
            onPrintPack();
        }
    }

    /**
     * 打印一般物资出库单
     */
    private void onPrintInv() {
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "供应室出库单");
        date.setData("DISPENSE_NO", "TEXT",
                     "出库单号: " + this.getValueString("DISPENSE_NO"));
        date.setData("DISPENSE_DATE", "TEXT",
                     "出库日期: " +
                     this.getValueString("CHECK_DATE").substring(0, 19).
                     replace("-", "/"));
        date.setData("DATE", "TEXT",
                     "制表日期: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 19).
                     replace("-", "/"));
        date.setData("FROM_ORG_CODE", "TEXT",
                     "出库部门: " + this.getTextFormat("FROM_ORG_CODE").getText());
        date.setData("TO_ORG_CODE", "TEXT",
                     "入库部门: " + this.getTextFormat("TO_ORG_CODE").getText());
        date.setData("PACK_MODE", "TEXT",
                     "出库方式: " + this.getComboBox("PACK_MODE").getSelectedName());
        date.setData("SUPTYPE_CODE", "TEXT",
                     "请领类别: " + this.getTextFormat("SUPTYPE_CODE").getText());
        //System.out.println("date===" + date);
        // 表格数据
        String dispense_no = this.getValueString("DISPENSE_NO");
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvSupDispenseInvForPrint(dispense_no)));
        parm.setCount(parm.getCount("INV_CHN_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "AMT");
        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        double sum_money = 0;
        for (int i = 0; i < parm.getCount("INV_CHN_DESC"); i++) {
            sum_money += parm.getDouble("AMT", i);
            if (parm.getData("VALID_DATE", i) == null ||
                "null".equals(parm.getValue("VALID_DATE", i))) {
                parm.setData("VALID_DATE", i, "");
            }
            else if (parm.getData("VALID_DATE", i) != null &&
                     parm.getValue("VALID_DATE", i).length() >= 10) {
                parm.setData("VALID_DATE", i,
                             parm.getValue("VALID_DATE", i).substring(0, 10).
                             replace("-", "/"));
            }
        }
        date.setData("TABLE", parm.getData());
        // 表尾数据
        date.setData("CHECK", "TEXT", "审核： ");
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());

        date.setData("TOT", "TEXT",
                     "总金额：" + StringTool.round(sum_money, 2));
        //System.out.println("date==="+date);
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupDispensetInv.jhw",
                             date);
    }

    
    /**
     * 打印手术包出库单
     */
    private void onPrintPack() {
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("SUPDNO", "TEXT", this.getValueString("DISPENSE_NO"));
        date.setData("DDATE", "TEXT",this.getValueString("CHECK_DATE").substring(0, 10).
                     replace("-", "/"));
        date.setData("CDATE", "TEXT",SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace("-", "/"));
        date.setData("DDEPT", "TEXT",this.getTextFormat("FROM_ORG_CODE").getText());
        date.setData("SDEPT", "TEXT",this.getTextFormat("TO_ORG_CODE").getText());
//        date.setData("PACK_MODE", "TEXT",
//                     "出库方式: " + this.getComboBox("PACK_MODE").getSelectedName());
//        date.setData("SUPTYPE_CODE", "TEXT",
//                     "请领类别: " + this.getTextFormat("SUPTYPE_CODE").getText());
        // 表格数据
      //表格数据
        TParm tableParm = new TParm();
		for(int i=0;i<tablePack.getRowCount();i++){
			tableParm.addData("BARCODE", tablePack.getItemString(i, "BARCODE"));
			tableParm.addData("INV_CHN_DESC", tablePack.getItemString(i, "INV_CHN_DESC"));
			tableParm.addData("PACK_BATCH_NO",  tablePack.getItemString(i, "PACK_BATCH_NO"));
			tableParm.addData("COST_PRICE", tablePack.getItemString(i, "COST_PRICE"));
			tableParm.addData("QTY", tablePack.getItemString(i, "QTY"));
			
			String sss = tablePack.getItemString(i, "COST_PRICE").toString();
			String mmm = tablePack.getItemString(i, "QTY").toString();
			
			double sumRow = Double.parseDouble(tablePack.getItemString(i, "COST_PRICE").toString())*Double.parseDouble(tablePack.getItemString(i, "QTY").toString());
			
			tableParm.addData("SUM", sumRow);
			
		}
		tableParm.setCount(tableParm.getCount("BARCODE"));
		tableParm.addData("SYSTEM", "COLUMNS", "BARCODE");
		tableParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "PACK_BATCH_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		tableParm.addData("SYSTEM", "COLUMNS", "QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "SUM");
        date.setData("TABLE", tableParm.getData());
        // 表尾数据
        date.setData("OPTUSER", "TEXT", Operator.getName());

        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupDispensetPackNew.jhw",
                             date);
    }
    
    
//    /**
//     * 打印手术包出库单
//     */
//    private void onPrintPack() {
//        // 打印数据
//        TParm date = new TParm();
//        // 表头数据
//        date.setData("TITLE", "TEXT", Manager.getOrganization().
//                     getHospitalCHNFullName(Operator.getRegion()) +
//                     "供应室出库单");
//        date.setData("DISPENSE_NO", "TEXT",
//                     "出库单号: " + this.getValueString("DISPENSE_NO"));
//        date.setData("DISPENSE_DATE", "TEXT",
//                     "出库日期: " +
//                     this.getValueString("CHECK_DATE").substring(0, 19).
//                     replace("-", "/"));
//        date.setData("DATE", "TEXT",
//                     "制表日期: " +
//                     SystemTool.getInstance().getDate().toString().substring(0, 19).
//                     replace("-", "/"));
//        date.setData("FROM_ORG_CODE", "TEXT",
//                     "出库部门: " + this.getTextFormat("FROM_ORG_CODE").getText());
//        date.setData("TO_ORG_CODE", "TEXT",
//                     "入库部门: " + this.getTextFormat("TO_ORG_CODE").getText());
//        date.setData("PACK_MODE", "TEXT",
//                     "出库方式: " + this.getComboBox("PACK_MODE").getSelectedName());
//        date.setData("SUPTYPE_CODE", "TEXT",
//                     "请领类别: " + this.getTextFormat("SUPTYPE_CODE").getText());
//        // 表格数据
//        String dispense_no = this.getValueString("DISPENSE_NO");
//        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
//            getInvSupDispensePackForPrint(dispense_no)));
//        parm.setCount(parm.getCount("PACK_DESC"));
//        parm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
//        parm.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
//        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
//        parm.addData("SYSTEM", "COLUMNS", "QTY");
//        parm.addData("SYSTEM", "COLUMNS", "AMT");
//        date.setData("TABLE", parm.getData());
//        // 表尾数据
//        date.setData("CHECK", "TEXT", "审核： ");
//        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
//        double sum_money = 0;
//        for (int i = 0; i < parm.getCount("PACK_DESC"); i++) {
//            sum_money += parm.getDouble("AMT", i);
//        }
//        date.setData("TOT", "TEXT",
//                     "总金额：" + StringTool.round(sum_money, 2));
//        //System.out.println("date==="+date);
//        // 调用打印方法
//        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupDispensetPack.jhw",
//                             date);
//    }

    /**
     * 主表单击事件
     */
    public void onTableMClicked() {
        pack_mode = tableM.getParmValue().getValue("PACK_MODE",
            tableM.getSelectedRow());
        tableInv.setSelectionMode(-1);
        tablePack.setSelectionMode(-1);
        if (!"".equals(tableM.getItemString(tableM.getSelectedRow(),
                                            "DISPENSE_NO"))) {
            TParm parm = tableM.getParmValue().getRow(tableM.getSelectedRow());
            this.setValueForParm(
                "DISPENSE_NO;CHECK_USER;REQUEST_NO;CHECK_DATE;"
                + "FROM_ORG_CODE;TO_ORG_CODE;REQUEST_TYPE;"
                + "PACK_MODE;SUPTYPE_CODE;REN_CODE", parm);
            if ("0".equals(pack_mode)) {
                tableInv.setVisible(true);
                tablePack.setVisible(false);
                TParm parmInv = InvSupDispenseDTool.getInstance().onQueryInv(
                    parm);
                tableInv.setParmValue(parmInv);
            }
            else {
                //System.out.println("-----------");
                tableInv.setVisible(false);
                tablePack.setVisible(true);
                TParm parmPack = InvSupDispenseDTool.getInstance().onQueryPack(
                    parm);
                tablePack.setParmValue(parmPack);
            }
        }
    }

    /**
     * 手术包表格单击事件
     */
    public void onTableD2clicked() {
        TParm parm = tablePack.getParmValue().getRow(tablePack.getSelectedRow());
        String org_code = this.getValueString("FROM_ORG_CODE");
        String pack_code = parm.getValue("INV_CODE");
        int pack_seq_no = parm.getInt("INVSEQ_NO");
        int batch_no = parm.getInt("PACK_BATCH_NO");
        double qty = parm.getDouble("QTY");
        TParm tableDDParm = new TParm();
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            tableDDParm = new TParm(TJDODBTool.getInstance().select(
                INVSQL.getINVPackStockDInfo(org_code, pack_code, pack_seq_no, qty, batch_no)));
        }
        else {
            tableDDParm = new TParm(TJDODBTool.getInstance().select(
                INVSQL.getINVSupDispenseDDInfo(getValueString("DISPENSE_NO"),
                                               pack_code,pack_seq_no)));
        }
        tableDD.setParmValue(tableDDParm);
    }

    /**
     * 引入请领单
     */
    public void onExport() {
        Object obj = this.openDialog(
            "%ROOT%\\config\\inv\\INVSuprequestChoose.x");
        if (obj == null)
            return;
        TParm parm = (TParm) obj;
        pack_mode = parm.getValue("PACK_MODE");
        // 处理出库单主项的数据
        onSetTableMData(parm);
        if ("0".equals(pack_mode)) {
            // 处理一般出库请领单返回的数据
            onSetTableInvData(parm);
        }
        else {
            // 处理手术包出库请领单返回的数据
            onSetTablePackData(parm);
        }
        TParm tableMParm = tableM.getParmValue().getRow(0);
        this.setValueForParm("DISPENSE_NO;TO_ORG_CODE;SUPTYPE_CODE;"
                             + "FROM_ORG_CODE;REQUEST_TYPE;CHECK_DATE"
                             + ";CHECK_USER;REQUEST_NO;PACK_MODE", tableMParm);
    }

    /**
     * 处理出库单主项的数据
     * @param parm TParm
     */
    private void onSetTableMData(TParm parm) {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TParm requestM = parm.getParm("REQUEST_M");
        TParm tableMParm = new TParm();
        tableMParm.addData("DISPENSE_NO", "");
        tableMParm.addData("PACK_MODE", parm.getValue("PACK_MODE"));
        tableMParm.addData("REQUEST_TYPE", "REQ");
        tableMParm.addData("REQUEST_NO", requestM.getValue("REQUEST_NO"));
        tableMParm.addData("REQUEST_DATE", requestM.getTimestamp("REQUEST_DATE"));
        tableMParm.addData("FROM_ORG_CODE", requestM.getValue("TO_ORG_CODE"));
        tableMParm.addData("TO_ORG_CODE", requestM.getValue("APP_ORG_CODE"));
        tableMParm.addData("DISPENSE_DATE", this.getValue("CHECK_DATE"));
        tableMParm.addData("DISPENSE_USER", this.getValueString("CHECK_USER"));
        tableMParm.addData("URGENT_FLG", requestM.getValue("URGENT_FLG"));
        tableMParm.addData("REMARK", requestM.getValue("DESCRIPTION"));
        tableMParm.addData("DISPOSAL_FLG", "N");
        tableMParm.addData("CHECK_DATE", this.getValue("CHECK_DATE"));
        tableMParm.addData("CHECK_USER", this.getValueString("CHECK_USER"));
        tableMParm.addData("REN_CODE", requestM.getValue("REASON_CHN_DESC"));
        tableMParm.addData("FINA_FLG", "N");
        tableMParm.addData("OPT_USER", Operator.getID());
        tableMParm.addData("OPT_DATE", datetime);
        tableMParm.addData("OPT_TERM", Operator.getIP());
        tableMParm.addData("SUPTYPE_CODE", requestM.getValue("SUPTYPE_CODE"));
        tableM.setParmValue(tableMParm);
        tableM.setSelectedRow(0);
    }

    /*
     * 处理一般出库请领单返回的数据
     * @param parm TParm
     */
    private void onSetTableInvData(TParm parm) {
        tablePack.setVisible(false);
        tableInv.setVisible(true);
        TParm requestD = parm.getParm("REQUEST_D");
        TParm tableParm = new TParm();
        TParm invParm = new TParm();
        int count = 1;
        for (int i = 0; i < requestD.getCount("INV_CODE"); i++) {
            invParm = requestD.getRow(i);
            invParm.setData("ORG_CODE",
                            parm.getParm("REQUEST_M").getValue("TO_ORG_CODE"));
            if ("Y".equals(requestD.getValue("SEQMAN_FLG", i))) {
                // 打开物资序号管理界面
                TParm resultParm = onOpenInvDilog(invParm);
                for (int j = 0; j < resultParm.getCount("QTY"); j++) {
                    resultParm.setData("SEQ_NO", j, invParm.getInt("SEQ_NO"));
                    insertNewRowTableInv(tableParm, resultParm.getRow(j), count);
                    count++;
                }
            }
            else {
                insertNewRowTableInv(tableParm, invParm, count);
                count++;
            }
        }
        //System.out.println("tableParm=="+tableParm);
        // 添加表格数据
        tableInv.setParmValue(tableParm);
    }

    /**
     * 打开物资序号管理选择界面
     * @param parm TParm
     * @return boolean
     */
    private TParm onOpenInvDilog(TParm parm) {
        Object result = openDialog("%ROOT%\\config\\inv\\INVSupInvStockDD.x",
                                   parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return null;
            }
            return addParm;
        }
        return null;
    }

    /**
     * 新增一行物资
     * @param parm TParm
     */
    private TParm insertNewRowTableInv(TParm tableParm, TParm invParm,
                                       int count) {
        tableParm.addData("DISPENSE_NO", "");
        tableParm.addData("SEQ_NO", count);
        tableParm.addData("PACK_MODE", "0");
        tableParm.addData("INV_CODE", invParm.getValue("INV_CODE"));
        tableParm.addData("INV_CHN_DESC", invParm.getValue("INV_CHN_DESC"));
        tableParm.addData("INVSEQ_NO", invParm.getInt("INVSEQ_NO"));
        tableParm.addData("QTY", invParm.getDouble("QTY"));
        tableParm.addData("STOCK_UNIT", invParm.getValue("STOCK_UNIT"));
        tableParm.addData("COST_PRICE", invParm.getDouble("COST_PRICE"));
        tableParm.addData("REQUEST_SEQ", invParm.getInt("SEQ_NO"));
        tableParm.addData("BATCH_NO", invParm.getValue("BATCH_NO"));
        tableParm.addData("BATCH_SEQ", "");
        tableParm.addData("VALID_DATE", invParm.getData("VALID_DATE"));
        tableParm.addData("DISPOSAL_FLG", "N");
        tableParm.addData("OPT_USER", Operator.getID());
        tableParm.addData("OPT_DATE", SystemTool.getInstance().getDate());
        tableParm.addData("OPT_TERM", Operator.getIP());
        return tableParm;
    }

    /*
     * 处理手术包出库请领单返回的数据
     * @param parm TParm
     */
    private void onSetTablePackData(TParm parm) {
        tableInv.setVisible(false);
        tablePack.setVisible(true);
        TParm requestD = parm.getParm("REQUEST_D");
        //System.out.println("requestD==="+requestD);
        TParm tableParm = new TParm();
        TParm packParm = new TParm();
        int count = 1;
        for (int i = 0; i < requestD.getCount("INV_CODE"); i++) {
            packParm = requestD.getRow(i);
            packParm.setData("ORG_CODE",
                             parm.getParm("REQUEST_M").getValue("TO_ORG_CODE"));
            if ("1".equals(requestD.getValue("SEQ_FLG", i))) {
                // 打开手术包序号管理界面
                TParm resultParm = onOpenPackDilog(packParm);
                for (int j = 0; j < resultParm.getCount("QTY"); j++) {
                    resultParm.setData("SEQ_NO", j, packParm.getInt("SEQ_NO"));
                    insertNewRowTablePack(tableParm, resultParm.getRow(j),
                                          count);
                    count++;
                }
            }
            else {
                TParm resultParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvPackCostPrice(parm.getParm("REQUEST_M").
                                               getValue("TO_ORG_CODE"),
                                               packParm.getValue("INV_CODE"))));
                if (resultParm == null || resultParm.getCount() <= 0) {
                    this.messageBox(packParm.getValue("INV_CHN_DESC") + "没有库存");
                    continue;
                }
                packParm.setData("COST_PRICE",
                                 resultParm.getDouble("COST_PRICE", 0));
                
                //查询该类型诊疗包的全部批次（数量>0）
                TParm condition = new TParm();
                condition.setData("ORG_CODE", parm.getParm("REQUEST_M").getValue("TO_ORG_CODE"));
                condition.setData("PACK_CODE", packParm.getValue("INV_CODE"));
                TParm tp = InvSupDispenseMTool.getInstance().queryPackByBatch(condition);
                if (tp.getErrCode() < 0) {
                    err("ERR:" + tp.getErrCode() + tp.getErrText()
                        + tp.getErrName());
                    return ;
                }
                int qty = packParm.getInt("QTY");
                //按批次扣库存
                for(int m=0;m<tp.getCount("BARCODE");m++){
                	if(qty>0){
                		if(tp.getInt("QTY",m)>=qty){
                			packParm.setData("BARCODE", tp.getData("BARCODE", m));
                        	packParm.setData("PACK_BATCH_NO", tp.getInt("PACK_BATCH_NO", m));
                        	packParm.setData("QTY",qty);
                        	insertNewRowTablePack(tableParm, packParm, count);
                            count++;
                            qty = 0;
                            break;
                		}else{
                			packParm.setData("BARCODE", tp.getData("BARCODE", m));
                        	packParm.setData("PACK_BATCH_NO", tp.getInt("PACK_BATCH_NO", m));
                        	packParm.setData("QTY",tp.getInt("QTY",m));
                        	insertNewRowTablePack(tableParm, packParm, count);
                        	count++;
                        	qty = qty - tp.getInt("QTY",m);
                		}
                	}
                }//for循环结束
                if(qty!=0){
                	this.messageBox(packParm.getValue("INV_CHN_DESC") + "库存不足，只能部分发货！");
                    continue;
                }
            }
        }
        // 添加表格数据
        tablePack.setParmValue(tableParm);
    }

    /**
     * 打开手术包序号管理选择界面
     * @param parm TParm
     * @return boolean
     */
    private TParm onOpenPackDilog(TParm parm) {
        Object result = openDialog("%ROOT%\\config\\inv\\INVSupPackStockDD.x",
                                   parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return null;
            }
            return addParm;
        }
        return null;
    }

    /**
     * 新增一行手术包
     * @param parm TParm
     */
    private TParm insertNewRowTablePack(TParm tableParm, TParm invParm,
                                        int count) {
        tableParm.addData("DISPENSE_NO", "");
        tableParm.addData("SEQ_NO", count);
        tableParm.addData("PACK_MODE", "1");
        tableParm.addData("INV_CODE", invParm.getValue("INV_CODE"));
        tableParm.addData("INV_CHN_DESC", invParm.getValue("INV_CHN_DESC"));
        tableParm.addData("INVSEQ_NO", invParm.getInt("INVSEQ_NO"));
        tableParm.addData("QTY", invParm.getDouble("QTY"));
        //tableParm.addData("STOCK_UNIT", "");
        tableParm.addData("COST_PRICE", invParm.getDouble("COST_PRICE"));
        tableParm.addData("REQUEST_SEQ", invParm.getInt("SEQ_NO"));
        //tableParm.addData("BATCH_NO", "");
        //tableParm.addData("BATCH_SEQ", "");
        //tableParm.addData("VALID_DATE", "");
        tableParm.addData("DISPOSAL_FLG", "N");
        tableParm.addData("OPT_USER", Operator.getID());
        tableParm.addData("OPT_DATE", SystemTool.getInstance().getDate());
        tableParm.addData("OPT_TERM", Operator.getIP());
        tableParm.addData("BARCODE", invParm.getValue("BARCODE"));
        tableParm.addData("PACK_BATCH_NO", invParm.getInt("PACK_BATCH_NO"));
        return tableParm;
    }

    /**
     * 变更单选按钮
     */
    public void onChangeRadioButton() {
        this.clearValue("PACK_MODE_Q;DISPENSE_NO_Q;FROM_ORG_CODE_Q;"
                        +
                        "TO_ORG_CODE_Q;START_DATE;END_DATE;DISPENSE_NO;REQUEST_NO;"
                        +
                        "FROM_ORG_CODE;TO_ORG_CODE;REQUEST_TYPE;PACK_MODE;SUPTYPE_CODE;"
                        + "REN_CODE");
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("CHECK_DATE", date);
        tableM.removeRowAll();
        tableInv.removeRowAll();
        tablePack.removeRowAll();
        tableDD.removeRowAll();
        tableInv.setVisible(true);
        tablePack.setVisible(false);

        if (getRadioButton("UPDATE_B").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("import")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("import")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableInvChangeValue(Object obj) {
        tableInv.acceptText();
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("出库数量不能小于或等于0");
                return true;
            }
            double invseq_no = tableInv.getItemDouble(tableInv.getSelectedRow(),
                                                "INVSEQ_NO");
            if (invseq_no != 0 && qty != 1) {
                this.messageBox("有序号物资数量不能大于1");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTablePackChangeValue(Object obj) {
        tablePack.acceptText();
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("出库数量不能小于或等于0");
                return true;
            }
            double invseq_no = tablePack.getItemDouble(tablePack.getSelectedRow(),
                "INVSEQ_NO");
            if (invseq_no != 0 && qty != 1) {
                this.messageBox("有序号手术包数量不能大于1");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TRadioButton对象
     * @param tagName String
     * @return TRadioButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

}
