package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.ind.INDSQL;
import jdo.inv.INVSQL;
import jdo.inv.InvVerifyinDDTool;
import jdo.inv.InvVerifyinDTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.javahis.util.RFIDPrintUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 物资验收管理Control
 * </p>
 *
 * <p>
 * Description: 物资验收管理Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lit 2013.6.5
 * @version 1.0
 */

public class INVVerifyinControl
    extends TControl {
    public INVVerifyinControl() {
    }

    private TTable table_m;

    private TTable table_d;

    private TTable table_dd;
    Map<String, String> map;
    
    
    Map<String, String> kindmap;

    // 赠与权限
    private boolean gift_flg = true;

    // 全部部门权限
    private boolean dept_flg = true;

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 打开未验收明细
     */
    public void onExport() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("验收部门不能为空");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Object result = openDialog("%ROOT%\\config\\inv\\INVUnVerifyin.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            // 供应厂商
            this.setValue("SUP_CODE", addParm.getValue("SUP_CODE", 0));
            // 计划单号
            this.setValue("STATIO_NO", addParm.getValue("STATIO_NO", 0));

            //System.out.println("-----" + addParm);
            double purorder_qty = 0;
            double actual_qty = 0;
            double puroder_price = 0;
            String cString=  SystemTool.getInstance().getNo("ALL", "INV","RFID", "No");
            for (int i = 0; i < addParm.getCount("INV_CODE"); i++) {
                // 验收量
                purorder_qty = addParm.getDouble("PURORDER_QTY", i);
                actual_qty = addParm.getDouble("STOCKIN_SUM_QTY", i);

                int row = table_d.addRow();
                // 选择
                table_d.setItem(row, "SELECT_FLG", "N");
                // 物资名称
                table_d.setItem(row, "INV_CHN_DESC",
                                addParm.getValue("INV_CHN_DESC", i));
                // 规格
                table_d.setItem(row, "DESCRIPTION",
                                addParm.getValue("DESCRIPTION", i));
                // 验收数量
                table_d.setItem(row, "VERIFIN_QTY", purorder_qty - actual_qty);
                // 赠与数
                table_d.setItem(row, "GIFT_QTY", addParm.getData("GIFT_QTY", i));
                // 进货单位
                table_d.setItem(row, "BILL_UNIT",
                                addParm.getValue("BILL_UNIT", i));
                // 验收单价
                puroder_price = addParm.getDouble("PURORDER_PRICE", i);
                table_d.setItem(row, "UNIT_PRICE", puroder_price);
                // 小计
                table_d.setItem(row, "VERIFYIN_AMT", StringTool.round(
                    puroder_price * (purorder_qty - actual_qty), 2));
                // 入库数量
                table_d.setItem(row, "IN_QTY", StringTool.round(
                    addParm.getDouble("STOCK_QTY", i) *
                    addParm.getDouble("DISPENSE_QTY", i) /
                    addParm.getDouble("PURCH_QTY", i) *
                    (purorder_qty - actual_qty), 1));
                // 入库单位
                table_d.setItem(row, "STOCK_UNIT",
                                addParm.getValue("DISPENSE_UNIT", i));
                // 入库单位
                table_d.setItem(row, "BATCH_NO",
                		cString);
                // 生产厂商
                table_d.setItem(row, "MAN_CODE",
                                addParm.getValue("MAN_CODE", i));
                // 序号管理
                table_d.setItem(row, "SEQMAN_FLG",
                                addParm.getValue("SEQMAN_FLG", i));
                // 效期管理
                table_d.setItem(row, "VALIDATE_FLG",
                                addParm.getValue("VALIDATE_FLG", i));
                // 物资代码
                table_d.getParmValue().setData("INV_CODE", row,
                                               addParm.getValue("INV_CODE", i));
                // 订购单序号
                table_d.getParmValue().setData("STESEQ_NO", row,
                                               addParm.getInt("SEQ_NO", i));
                // 订购单号
                table_d.getParmValue().setData("PURORDER_NO", row,
                                               addParm.
                                               getValue("PURORDER_NO", i));
                // 订购数量
                table_d.getParmValue().setData("PURORDER_QTY", row,
                                               purorder_qty);
                // 库存转换量
                table_d.getParmValue().setData("STOCK_QTY", row,
                                               addParm.getDouble("STOCK_QTY", i));
                // 出库转换量
                table_d.getParmValue().setData("DISPENSE_QTY", row,
                                               addParm.getDouble("DISPENSE_QTY",
                    i));

            }
            this.getTextFormat("ORG_CODE").setEnabled(false);
            this.getTextFormat("SUP_CODE").setEnabled(false);
        }
    }
    
    
    
    
    /**
     * 打开赋码
     */
    public void onAddRfid() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("验收部门不能为空");
            return;
        }
        table_dd.acceptText();
        TParm parm = table_dd.getShowParmValue();
        Object result = openDialog("%ROOT%\\config\\inv\\INVBarcodeAndRFID.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            Map<String, String> map=new HashMap<String, String>();
            for (int i = 0; i < addParm.getCount("RFID"); i++) {
                // 验收量
            	String rfid=addParm.getValue("RFID", i);
            	String code=addParm.getValue("ORGIN_CODE", i);
            	map.put(rfid, code);

            }
            for (int i = 0; i < table_dd.getRowCount(); i++) {
            	
            	String rfid=table_dd.getItemData(i, "RFID").toString();
            	table_dd.setItem(i, "ORGIN_CODE", map.get(rfid));
            	
				
			}
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if (!dept_flg) {
            if ("".equals(this.getValueString("ORG_CODE_Q"))) {
                this.messageBox("请选择查询部门");
                return;
            }
        }

        TParm parm = new TParm();
        // 验收状态
        if (this.getRadioButton("UPDATE_FLG_B").isSelected()) {
            parm.setData("CHECK_FLG", "N");
        }
        else if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
            parm.setData("CHECK_FLG", "Y");
        }
        // 验收单号
        if (!"".equals(this.getValueString("VERIFYIN_NO").trim())) {
            parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO_Q"));
        }
        // 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        // 验收部门
        if (!"".equals(this.getValueString("ORG_CODE_Q"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE_Q"));
        }
        // 供货厂商
        if (!"".equals(this.getValueString("SUP_CODE_Q"))) {
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE_Q"));
        }

        TParm inparm = new TParm();
        inparm.setData("VER_M", parm.getData());
        // 查询
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVVerifyinAction", "onQueryM", inparm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            table_m.removeRowAll();
            return;
        }
        //System.out.println("---" + result);
        table_m.setParmValue(result);
    }
    
    /**
     * 全选事件
     */
    public void onSelectAllDown() {
        String flg = "Y";
        if (getCheckBox("SELECT_ALL_DOWN").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        for (int i = 0; i < table_dd.getRowCount(); i++) {
            table_dd.setItem(i, "FLG", flg);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        onChangeCheckFlg();
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextFormat("ORG_CODE").setEnabled(true);

        String clearString =
            "START_DATE;END_DATE;ORG_CODE_Q;SUP_CODE_Q;VERIFYIN_NO_Q;"
            + "VERIFYIN_NO;VERIFYIN_DATE;ORG_CODE;SUP_CODE;INVOICE_NO;"
            + "INVOICE_AMT;STATIO_NO;SELECT_ALL;";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("VERIFYIN_DATE", date);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        table_dd.setSelectionMode(0);
        table_dd.removeRowAll();
        
    }

    /**
     * 保存方法
     */
    public void onSave() {
        //数据检核
        if (!checkData()) {
            return;
        }

        TParm parm = new TParm();

        TParm result = new TParm();
        if (table_m.getSelectedRow() < 0) {
            //1.取得验收主表数据(TABLE_M)
            getInsertTableMData(parm);
            //2.取得验收明细数据(TABLE_D)
            getInsertTableDData(parm);
                //3.取得验收序号管理细项数据(TABLE_DD)
                getInsertTableDDData(parm);
                //4.物资字典更新移动加权平均(INV_BASE)
                getInvBaseData(parm);
                //5.取得库存主档数据(INV_STOCKM)
                result = getUpdateInvStockMData(parm);
                if (result == null) {
                    return;
                }
                //6.取得库存明细档数据(INV_STOCKD)
                getInsertInvStockDData(parm);
                //7.取得库存序号管理细项数据(INV_STOCKDD)
                getInsertInvStockDDData(parm);
                //8.取得订购单细项数据
                getInvPuroderDData(parm);
                //9.取得订购单主项数据
                getInvPurorderMData(parm);
            // 新增数据
            result = TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onInsert", parm);
        }
        else {
            //1.取得验收主表数据(TABLE_M)
            getUpdateTableMData(parm);
            //2.取得验收明细数据(TABLE_D)
            getUpdateTableDData(parm);
                //3.取得验收序号管理细项数据(TABLE_DD)
                getInsertTableDDData(parm);
                //4.物资字典更新移动加权平均(INV_BASE)
                getInvBaseData(parm);
                //5.取得库存主档数据(INV_STOCKM)
                result = getUpdateInvStockMData(parm);
                if (result == null) {
                    return;
                }
                //6.取得库存明细档数据(INV_STOCKD)
                getInsertInvStockDData(parm);
                //7.取得库存序号管理细项数据(INV_STOCKDD)
                getInsertInvStockDDData(parm);
                //8.取得订购单细项数据
                getInvPuroderDData(parm);
                //9.取得订购单主项数据
                getInvPurorderMData(parm);
            //System.out.println("parm---" + parm);
            // 更新数据
            result = TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO"));
        if (row_d >= 0) {
            // 删除验收单细项
            if ("".equals(this.getValueString("VERIFYIN_NO"))) {
                table_d.removeRow(row_d);
                return;
            }
            else if (this.messageBox("删除", "确定是否删除验收细项", 2) == 0) {
                parm.setData("SEQ_NO",
                             table_d.getParmValue().getInt("SEQ_NO", row_d));
                result = InvVerifyinDTool.getInstance().onDelete(parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                table_d.removeRow(row_d);
                this.messageBox("删除成功");
            }
        }
        else if (row_m >= 0) {
            // 删除验收单主项
            if (this.messageBox("删除", "确定是否删除验收单", 2) == 0) {
                result = TIOM_AppServer.executeAction(
                    "action.inv.INVVerifyinAction", "onDelete", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                table_m.removeRow(row_m);
                table_d.removeRowAll();
                this.messageBox("删除成功");
            }
        }
        else {
            this.messageBox("没有选中项");
            return;
        }
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
    // 打印输出格式
    java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
    java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
    java.text.DecimalFormat df3 = new java.text.DecimalFormat(
        "##########0.000");
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");
    //luhai 2012-2-28 加入一位的格式化
    java.text.DecimalFormat df5 = new java.text.DecimalFormat(
    "##########0");

    /**
     * 打印入库单
     */
    public void onPrint() {
    	 Timestamp datetime = SystemTool.getInstance().getDate();
         TTable table_d = getTable("TABLE_D");
         if ("".equals(this.getValueString("VERIFYIN_NO"))) {
             this.messageBox("不存在验收单");
             return;
         }
         if (table_d.getRowCount() > 0) {
             // 打印数据
             TParm date = new TParm();
             // 表头数据
             date.setData("TITLE", "TEXT",
                          Manager.getOrganization().
                          getHospitalCHNFullName(Operator.getRegion()) +
                          "验收入库单");
             date.setData("VER_NO", "TEXT",
                          "入库单号: " + this.getValueString("VERIFYIN_NO"));
             date.setData("SUP_CODE", "TEXT",
                          "供货厂商: " +
                          this.getTextFormat("SUP_CODE").getText());
             date.setData("ORG_CODE", "TEXT",
                          "验收部门: " +
                          this.getComboBox("ORG_CODE").getSelectedName());
             date.setData("DATE", "TEXT",
                          "制表日期: " +
                          datetime.toString().substring(0, 10).replace('-', '/'));
             // 表格数据
             TParm parm = new TParm();
             String order_code = "";
             double ver_sum = 0;
             double own_sum = 0;
             double diff_sum = 0;
             for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                 if (!table_d.getDataStore().isActive(i)) {
                     continue;
                 }
                 if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                     continue;
                 }
                 parm.addData("INVOICE_NO",
                              "");
                 String invoidDate ="";
                 order_code = "";
                 parm.addData("ORDER_DESC", "");
                 parm.addData("SPECIFICATION",
                              "");
                 parm.addData("UNIT","");
                 //luhai 2012-2-28 modify 验收数量取1位 begin
//                 parm.addData("VERIFYIN_QTY", df3.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
                 parm.addData("VERIFYIN_QTY", df5.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
                 //luhai 2012-2-28 modify 验收数量取1位 end
                 parm.addData("VERIFYIN_PRICE",
                              df4.format(StringTool.round(table_d.getItemDouble(
                                  i, "VERIFYIN_PRICE"), 4)));
                 double ver_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") *
                     table_d.getItemDouble(i, "VERIFYIN_PRICE");
                 parm.addData("VER_AMT", df2.format(StringTool.round(ver_atm, 2)));
                 ver_sum += ver_atm;
                 parm.addData("OWN_PRICE",
                              df4.format(StringTool.round(table_d.
                     getItemDouble(i, "RETAIL_PRICE"), 4)));
                 double own_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") *
                     table_d.getItemDouble(i, "RETAIL_PRICE");
                 parm.addData("OWN_AMT", df2.format(StringTool.round(own_atm, 2)));
                 own_sum += own_atm;
                 parm.addData("DIFF_AMT", df2.format(StringTool.round(own_atm - ver_atm, 2)));
                 diff_sum += own_atm - ver_atm;
                 parm.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
                 parm.addData("VALID_DATE",
                              table_d.getItemString(i, "VALID_DATE").substring(0,
                     10).replace('-', '/'));
                 String pha_type = "";
                 parm.addData("PHA_TYPE", "");
                 //生产厂商
                 TParm manparm = new TParm(TJDODBTool.getInstance().select(
                     SYSSQL.getSYSManufacturer(table_d.getItemString(i,
                     "MAN_CODE"))));
//                 System.out.println(""+SYSSQL.getSYSManufacturer(table_d.getItemString(i,
//                     "MAN_CODE")));
                 parm.addData("MAN_CODE", manparm.getValue("MAN_CHN_DESC", 0));
                 parm.addData("BID_FLG", "是");
             }
             if (parm.getCount("ORDER_DESC") == 0) {
                 this.messageBox("没有打印数据");
                 return;
             }
             int row = parm.getCount("ORDER_DESC");
             parm.setCount(parm.getCount("ORDER_DESC"));
             parm.addData("SYSTEM", "COLUMNS", "INVOICE_NO");
             parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
             parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
             parm.addData("SYSTEM", "COLUMNS", "UNIT");
             parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_QTY");
             parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
             parm.addData("SYSTEM", "COLUMNS", "VER_AMT");
             parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
             parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
             date.setData("TABLE", parm.getData());
             // 表尾数据
             date.setData("USER", "TEXT", "制表人: " + Operator.getName());
             //luhai 2012-3-7 begin 加入合计部分
             date.setData("VER_AMT", "TEXT", df2.format(StringTool.round(ver_sum, 2)));//验收价格
             date.setData("OWN_AMT", "TEXT", df2.format(StringTool.round(own_sum, 2)));//零售价格
             date.setData("VER_AMT_CHN", "TEXT","合计（大写）："+StringUtil.getInstance().numberToWord( Double.parseDouble(df2.format(StringTool.round(ver_sum, 2)))));//验收大写
             //luhai 2012-3-7 end
             // 调用打印方法
             this.openPrintWindow("%ROOT%\\config\\prt\\IND\\VerifyIn.jhw",
                                  date);
         }
         else {
             this.messageBox("没有打印数据");
             return;
         }

    }

    /**
     * 打印条码
     */
    public void onPrintBarcode() {
    	if (getTable("TABLE_DD").getRowCount()<=0) {
			messageBox("请选中明细项");
		}
    	TParm parm=getTable("TABLE_DD").getParmValue();
    	
    	for (int i = 0; i < getTable("TABLE_DD").getRowCount(); i++) {
    		TParm newParm=new TParm();
    		if (!"Y".equals(table_dd.getItemData(i, "FLG"))) {
				continue;
			}
    		newParm.setData(RFIDPrintUtils.PARM_CODE, parm.getData("RFID", i).toString().trim());
    		newParm.setData(RFIDPrintUtils.PARM_NAME,  parm.getData("INV_CHN_DESC", i));
    		newParm.setData(RFIDPrintUtils.PARM_PRFID, parm.getData("RFID", i).toString().trim());
    		String cString="";
    		if ( parm.getData("VALID_DATE", i)!=null&&!parm.getData("VALID_DATE", i).toString().equals("")) {
    			cString=parm.getData("VALID_DATE", i).toString();
    			cString=cString.substring(0,10);
			}
    		newParm.setData(RFIDPrintUtils.PARM_VALID_DATE, cString);
    		newParm.setData(RFIDPrintUtils.PARM_SPEC, parm.getData("DESCRIPTION", i));
    		RFIDPrintUtils.send2LPT(newParm);
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	

    }
    
    
    
    /**
     * 更改效期
     */
    public void onChangeData() {
    	table_dd.acceptText();
    	TParm result=new TParm();
    	TParm parm= new TParm();
    	for (int i = 0; i <table_dd.getRowCount(); i++) {
    		
    		parm.addData("RFID", table_dd.getItemData(i, "RFID"));
    		parm.addData("VALID_DATE",table_dd.getItemData(i, "VALID_DATE"));
    		
		}
    	
    	result=TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onUpdateValData", parm);
    	 if (result == null || result.getErrCode() < 0) {
             this.messageBox("效期更新失败！");
             return;
         }
    	
    	this.messageBox("效期更新成功！");
    	

    }

    /**
     * 全选方法
     */
    public void onSelectAll() {
        String flg = "Y";
        if (getCheckBox("SELECT_ALL").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
            table_d.setItem(i, "SELECT_FLG", flg);
        }
        //计算总金额
        this.setValue("INVOICE_AMT", getSumAMT());
    }

    /**
     * 审核选项变更事件
     */
    public void onChangeCheckFlg() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
        }
    }

    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            getTextFormat("SUP_CODE").setEnabled(false);
            getTextFormat("ORG_CODE").setEnabled(false);
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                ( (TMenuItem) getComponent("delete")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("delete")).setEnabled(false);
            }
            table_d.setSelectionMode(0);
            // 主项信息(TABLE中取得)
            setValue("VERIFYIN_NO", table_m.getItemString(row, "VERIFYIN_NO"));
            setValue("VERIFYIN_DATE", table_m.getItemTimestamp(row,
                "VERIFYIN_DATE"));
            setValue("ORG_CODE", table_m.getItemString(row, "VERIFYIN_DEPT"));
            setValue("SUP_CODE", table_m.getItemString(row, "SUP_CODE"));
            setValue("INVOICE_NO", table_m.getItemString(row, "INVOICE_NO"));
            setValue("INVOICE_AMT", table_m.getItemDouble(row, "INVOICE_AMT"));
            setValue("STATIO_NO", table_m.getItemString(row, "STATIO_NO"));
            setValue("CHECK_FLG", table_m.getItemString(row, "CHECK_FLG"));
            setValue("CON_FLG", table_m.getItemString(row, "CON_FLG"));
            setValue("CON_ORG", table_m.getItemString(row, "CON_ORG"));

            // 明细信息
            TParm parm = new TParm();
            parm.setData("VERIFYIN_NO",
                         table_m.getItemString(row, "VERIFYIN_NO"));
            TParm result = InvVerifyinDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有验收明细");
                return;
            }
            table_d.removeRowAll();
            table_dd.removeRowAll();
            table_d.setParmValue(result);

            //计算总金额
            this.setValue("INVOICE_AMT", getSumAMT());
        }
    }

    /**
     * 细项表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        int row = table_d.getSelectedRow();
        if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
            if ("Y".equals(table_d.getItemString(row, "SEQMAN_FLG"))) {
                TParm parm = new TParm();
                parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO"));
                parm.setData("SEQ_NO",
                             table_d.getParmValue().getValue("SEQ_NO", row));
                TParm result = InvVerifyinDDTool.getInstance().onQuery(parm);
//                for (int i = 0; i < result.getCount("RIFD"); i++) {
//                	 result.setData("FLG", "Y");
//				}
               
                
                if (result == null || result.getCount("VERIFYIN_NO") <= 0) {
                    this.messageBox("没有管理细项数据");
                    return;
                }
                table_dd.setParmValue(result);
            }
            else {
                table_dd.removeRowAll();
            }
        }
       
    }


    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        // 主项信息检核
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("验收部门不能为空");
            return false;
        }
        if ("".equals(this.getValueString("SUP_CODE"))) {
            this.messageBox("供货商不能为空");
            return false;
        }

        // 细项信息检核
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
                break;
            }
        }
        if (flg) {
            this.messageBox("没有选中的细项");
            return false;
        }

        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (table_d.getItemDouble(i, "VERIFIN_QTY") <= 0) {
                this.messageBox("验收数量不能小于或等于0");
                return false;
            }
            if (table_d.getItemDouble(i, "GIFT_QTY") < 0) {
                this.messageBox("赠送数量不能小于0");
                return false;
            }
            if (table_d.getItemDouble(i, "UNIT_PRICE") <= 0) {
                this.messageBox("验收单价不能小于或等于0");
                return false;
            }
            if ("".equals(table_d.getItemString(i, "BATCH_NO"))) {
                this.messageBox("批号不能为空");
                return false;
            }
            if ("Y".equals(table_d.getItemString(i, "VALIDATE_FLG")) &&
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                this.messageBox("有效期管理的物资,效期不能为空");
                return false;
            }
        }
        return true;
    }

    /**
     * 取得验收主表数据(TABLE_M)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertTableMData(TParm parm) {
        TParm parm_M = new TParm();
        // 新增数据
        String verifyin_no = SystemTool.getInstance().getNo("ALL", "INV",
            "INV_VERIFYIN", "No");
        parm_M.setData("VERIFYIN_NO", verifyin_no);
        parm.setData("VERIFYIN_NO", verifyin_no);
        Timestamp date = SystemTool.getInstance().getDate();
        parm_M.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        parm_M.setData("VERIFYIN_DATE", this.getValue("VERIFYIN_DATE"));
        parm_M.setData("VERIFYIN_USER", Operator.getID());
        parm_M.setData("VERIFYIN_DEPT", this.getValueString("ORG_CODE"));
        parm_M.setData("INVOICE_NO", this.getValueString("INVOICE_NO"));
        parm_M.setData("INVOICE_DATE", date);
        parm_M.setData("INVOICE_AMT", this.getValueDouble("INVOICE_AMT"));
        parm_M.setData("INVOICE_AMT", this.getValueDouble("INVOICE_AMT"));
        parm_M.setData("STATIO_NO", this.getValueString("STATIO_NO"));
        parm_M.setData("CHECK_FLG",
                       "Y".equals(this.getValueString("CHECK_FLG")) ? "Y" : "N");
        parm_M.setData("OPT_USER", Operator.getID());
        parm_M.setData("OPT_DATE", date);
        parm_M.setData("OPT_TERM", Operator.getIP());
        if (getCheckBox("CON_FLG").isSelected()) {
        	parm_M.setData("CON_FLG", "Y");
        	parm_M.setData("CON_ORG", this.getValueString("CON_ORG"));
		}else {
			parm_M.setData("CON_FLG", "N");
        	parm_M.setData("CON_ORG", "");
		}
        parm.setData("VER_M", parm_M.getData());
        return parm;
    }

    /**
     * 取得验收主表数据(TABLE_M)
     * @param parm TParm
     * @return TParm
     */
    private TParm getUpdateTableMData(TParm parm) {
        TParm parm_M = new TParm();
        // 更新数据
        String verifyin_no = this.getValueString("VERIFYIN_NO");
        parm_M.setData("VERIFYIN_NO", verifyin_no);
        parm.setData("VERIFYIN_NO", verifyin_no);
        Timestamp date = SystemTool.getInstance().getDate();
        parm_M.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        parm_M.setData("VERIFYIN_DATE", this.getValue("VERIFYIN_DATE"));
        parm_M.setData("VERIFYIN_USER", Operator.getID());
        parm_M.setData("VERIFYIN_DEPT", this.getValueString("ORG_CODE"));
        parm_M.setData("INVOICE_NO", this.getValueString("INVOICE_NO"));
        parm_M.setData("INVOICE_DATE", date);
        parm_M.setData("INVOICE_AMT", this.getValueDouble("INVOICE_AMT"));
        parm_M.setData("STATIO_NO", this.getValueString("STATIO_NO"));
        parm_M.setData("CHECK_FLG",
                       "Y".equals(this.getValueString("CHECK_FLG")) ? "Y" : "N");
        parm_M.setData("OPT_USER", Operator.getID());
        parm_M.setData("OPT_DATE", date);
        parm_M.setData("OPT_TERM", Operator.getIP());
        if (getCheckBox("CON_FLG").isSelected()) {
        	parm_M.setData("CON_FLG", "Y");
        	parm_M.setData("CON_ORG", this.getValueString("CON_ORG"));
		}else {
			parm_M.setData("CON_FLG", "N");
        	parm_M.setData("CON_ORG", "");
		}
        parm.setData("VER_M", parm_M.getData());
        return parm;
    }


    /**
     * 取得验收明细数据(TABLE_D)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertTableDData(TParm parm) {
        table_d.acceptText();
        //System.out.println(table_d.getParmValue());
        TParm parm_D = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parm_D.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
            parm_D.addData("SEQ_NO", count);
            count++;
            parm_D.addData("INV_CODE", 
                           table_d.getParmValue().getValue("INV_CODE", i));
            String sql="select INV_KIND from INV_BASE where INV_CODE='"+table_d.getParmValue().getValue("INV_CODE", i)+"'";
            TParm parm2=new TParm(TJDODBTool.getInstance().select(sql));
            String c=parm2.getData("INV_KIND",0).toString();
            parm_D.addData("INV_KIND", c);
            parm_D.addData("QTY", table_d.getItemDouble(i, "VERIFIN_QTY"));
            parm_D.addData("GIFT_QTY", table_d.getItemDouble(i, "GIFT_QTY"));
            parm_D.addData("BILL_UNIT", table_d.getItemString(i, "BILL_UNIT"));
            parm_D.addData("IN_QTY", table_d.getItemDouble(i, "IN_QTY"));
            parm_D.addData("STOCK_UNIT", table_d.getItemString(i, "STOCK_UNIT"));
            parm_D.addData("UNIT_PRICE",
                           table_d.getItemDouble(i, "UNIT_PRICE"));
            parm_D.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                parm_D.addData("VALID_DATE", tnull);
            }
            else {
                parm_D.addData("VALID_DATE", TypeTool.getTimestamp(table_d.
                    getItemTimestamp(i, "VALID_DATE")));
            }
            parm_D.addData("PURORDER_NO",
                           table_d.getParmValue().getValue("PURORDER_NO", i));
            parm_D.addData("STESEQ_NO",
                           table_d.getParmValue().getInt("STESEQ_NO", i));
            parm_D.addData("REN_CODE", table_d.getItemString(i, "REN_CODE"));
            parm_D.addData("QUALITY_DEDUCT_AMT",
                           table_d.getItemDouble(i, "QUALITY_DEDUCT_AMT"));
            parm_D.addData("OPT_USER", Operator.getID());
            parm_D.addData("OPT_DATE", date);
            parm_D.addData("OPT_TERM", Operator.getIP());
            parm_D.addData("SEQMAN_FLG",
                           table_d.getItemString(i, "SEQMAN_FLG"));
        }
        parm.setData("VER_D", parm_D.getData());
        return parm;
    }

    /**
     * 取得验收明细数据(TABLE_D)
     * @param parm TParm
     * @return TParm
     */
    private TParm getUpdateTableDData(TParm parm) {
        table_d.acceptText();
        TParm parm_D = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parm_D.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
            parm_D.addData("SEQ_NO", table_d.getParmValue().getInt("SEQ_NO", i));
            parm_D.addData("INV_CODE",
                           table_d.getParmValue().getValue("INV_CODE", i));
            parm_D.addData("QTY", table_d.getItemDouble(i, "VERIFIN_QTY"));
            parm_D.addData("GIFT_QTY", table_d.getItemDouble(i, "GIFT_QTY"));
            parm_D.addData("BILL_UNIT", table_d.getItemString(i, "BILL_UNIT"));
            parm_D.addData("IN_QTY", table_d.getItemDouble(i, "IN_QTY"));
            parm_D.addData("STOCK_UNIT", table_d.getItemString(i, "STOCK_UNIT"));
            parm_D.addData("UNIT_PRICE",
                           table_d.getItemDouble(i, "UNIT_PRICE"));
            parm_D.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                parm_D.addData("VALID_DATE", tnull);
            }
            else {
                parm_D.addData("VALID_DATE", TypeTool.getTimestamp(table_d.
                    getItemTimestamp(i, "VALID_DATE")));
            }
            parm_D.addData("PURORDER_NO",
                           table_d.getParmValue().getValue("PURORDER_NO", i));
            parm_D.addData("STESEQ_NO",
                           table_d.getParmValue().getInt("STESEQ_NO", i));
            parm_D.addData("REN_CODE", table_d.getItemString(i, "REN_CODE"));
            parm_D.addData("QUALITY_DEDUCT_AMT",
                           table_d.getItemDouble(i, "QUALITY_DEDUCT_AMT"));
            parm_D.addData("OPT_USER", Operator.getID());
            parm_D.addData("OPT_DATE", date);
            parm_D.addData("OPT_TERM", Operator.getIP());
            parm_D.addData("SEQMAN_FLG",
                           table_d.getItemString(i, "SEQMAN_FLG"));
        }
        parm.setData("VER_D", parm_D.getData());
        return parm;
    }


    /**
     * 取得验收序号管理细项数据(TABLE_DD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertTableDDData(TParm parm) {
        TParm parm_DD = new TParm();
        TParm parm_D = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        System.out.println(parm_D);
        String sql = "";
        String valid_date = "";
        for (int i = 0; i < parm_D.getCount("INV_CODE"); i++) {
            if ("Y".equals(parm_D.getValue("SEQMAN_FLG", i))) {
                //INVSEQ_NO 抓取最大号+1
                TParm invSeqNoParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvMaxInvSeqNo(parm_D.getValue("INV_CODE", i))));
                int invseq_no = 1;
                if (invSeqNoParm.getCount() > 0) {
                    invseq_no = invSeqNoParm.getInt("INVSEQ_NO", 0) + 1;
                }
                //根据批号和效期取得BATCH_SEQ
                valid_date = TypeTool.getString(parm_D.getValue("VALID_DATE", i));
                if (!"".equals(valid_date) && valid_date.length() > 18) {
                    valid_date = parm_D.getValue("VALID_DATE", i).substring(0,
                        4) + parm_D.getValue("VALID_DATE", i).substring(5, 6)
                        + parm_D.getValue("VALID_DATE", i).substring(7, 8)
                        + parm_D.getValue("VALID_DATE", i).substring(9, 10)
                        + parm_D.getValue("VALID_DATE", i).substring(11, 13)
                        + parm_D.getValue("VALID_DATE", i).substring(14, 16);
                } 
                sql = INVSQL.getInvBatchSeq(getValueString("ORG_CODE"),
                                            parm_D.getValue("INV_CODE", i),
                                            parm_D.getValue("BATCH_NO", i),
                                            valid_date);
                TParm stockDParm = new TParm(TJDODBTool.getInstance().
                                               select(sql));
                
                int batch_seq = 1;
                if (stockDParm.getCount("BATCH_SEQ") > 0) {
                    batch_seq = stockDParm.getInt("BATCH_SEQ", 0);
                }
                else {
                    // 抓取最大BATCH_SEQ+1
                    TParm batchSeqParm = new TParm(TJDODBTool.getInstance().
                        select(INVSQL.getInvStockMaxBatchSeq(getValueString(
                        "ORG_CODE"), parm_D.getValue("INV_CODE", i))));
                    if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
                        batch_seq = 1;
                    }
                    else {
                        batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
                    }
                }
                String kind= parm_D.getValue("INV_KIND", i);
                String xString=kindmap.get(kind);
                
                for (int j = 0; j < parm_D.getDouble("QTY", i); j++) {
                    parm_DD.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
                    parm_DD.addData("SEQ_NO", parm_D.getInt("SEQ_NO", i));
                    parm_DD.addData("DDSEQ_NO", j);
                    parm_DD.addData("INV_CODE", parm_D.getValue("INV_CODE", i));
                    System.out.println("++++++++++++++++++++++++++"+invseq_no);
                    parm_DD.addData("INVSEQ_NO", invseq_no);
                    invseq_no++;
                    parm_DD.addData("BATCH_SEQ", batch_seq);
                    parm_DD.addData("BATCH_NO", parm_D.getValue("BATCH_NO", i));
                    if (parm_D.getData("VALID_DATE", i) == null ||
                        "".equals(parm_D.getData("VALID_DATE", i))) {
                        parm_DD.addData("VALID_DATE", tnull);
                    }
                    else {
                        parm_DD.addData("VALID_DATE", TypeTool.getTimestamp(
                            parm_D.getData("VALID_DATE", i)));
                    } 
                    parm_DD.addData("STOCK_UNIT",
                                    parm_D.getValue("STOCK_UNIT", i));
                    parm_DD.addData("UNIT_PRICE",
                                    parm_D.getDouble("UNIT_PRICE", i));
                    parm_DD.addData("OPT_USER", Operator.getID());
                    parm_DD.addData("OPT_DATE", date);
                    parm_DD.addData("OPT_TERM", Operator.getIP());
                  String cString=  SystemTool.getInstance().getNo("ALL", "INV","RFID", "No");
                  
                    parm_DD.addData("RFID",xString+cString);

                    
                }
            }
        }
        parm.setData("VER_DD", parm_DD.getData());
       // System.out.println("========"+ parm_DD.getData());
        return parm;
    }

    /**
     * 取得库存主档数据(INV_STOCKM)
     * @param parm TParm
     * @return TParm
     */
    private TParm getUpdateInvStockMData(TParm parm) {
        TParm stockM = new TParm();
        TParm parmD = parm.getParm("VER_D");
        String org_code ="";
        if (getCheckBox("CON_FLG").isSelected()) {
        	 org_code = this.getValueString("CON_ORG");
		}else {
			 org_code = this.getValueString("ORG_CODE");
		}
    
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            TParm stockMParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockM(org_code, inv_code)));
            if (stockMParm == null || stockMParm.getCount("INV_CODE") <= 0) {
                this.messageBox("没有设定库存主档");
                return null;
            }
            stockM.addData("ORG_CODE", org_code);
            stockM.addData("INV_CODE", inv_code);
            stockM.addData("STOCK_QTY", parmD.getDouble("QTY", i));
            stockM.addData("OPT_USER", Operator.getID());
            stockM.addData("OPT_DATE", date);
            stockM.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("STOCK_M", stockM.getData());
        return parm;
    }

    /**
     * 取得库存明细档数据(INV_STOCKD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertInvStockDData(TParm parm) {
        TParm stockD = new TParm();
        TParm parmD = parm.getParm("VER_D");
        String org_code ="";
        if (getCheckBox("CON_FLG").isSelected()) {
        	 org_code = this.getValueString("CON_ORG");
		}else {
			 org_code = this.getValueString("ORG_CODE");
		}
    
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        String batch_no = "";
        String valid_date = "";
        int batch_seq = 0;
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            batch_no = parmD.getValue("BATCH_NO", i);
            valid_date = parmD.getValue("VALID_DATE", i);
            TParm stockDParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBatchSeq(org_code, inv_code, batch_no, valid_date)));
            if (stockDParm.getCount("BATCH_SEQ") > 0) {
                stockD.addData("FLG", "UPDATE");
                batch_seq = stockDParm.getInt("BATCH_SEQ", i);
            }
            else {
                stockD.addData("FLG", "INSERT");
                // 抓取最大BATCH_SEQ+1
                TParm batchSeqParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvStockMaxBatchSeq(org_code, inv_code)));
                System.out.println("===========bat====="+org_code+"00"+inv_code);
                System.out.println("===========bat====="+batchSeqParm);
                if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
                    batch_seq = 1;
                }
                else {
                    batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
                }
            }
            stockD.addData("ORG_CODE", org_code);
            stockD.addData("INV_CODE", inv_code);
            stockD.addData("BATCH_SEQ", batch_seq);
            stockD.addData("REGION_CODE", Operator.getRegion());
            stockD.addData("BATCH_NO", parmD.getValue("BATCH_NO", i));
            stockD.addData("VALID_DATE", parmD.getData("VALID_DATE", i));
          //  stockD.addData("STOCK_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("STOCK_QTY", parmD.getDouble("QTY", i));
            stockD.addData("LASTDAY_TOLSTOCK_QTY", 0);
           // stockD.addData("DAYIN_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("DAYIN_QTY", parmD.getDouble("QTY", i));
            stockD.addData("DAYOUT_QTY", 0);
            stockD.addData("DAY_CHECKMODI_QTY", 0);
            stockD.addData("DAY_VERIFYIN_QTY", parmD.getDouble("QTY", i));
            stockD.addData("DAY_VERIFYIN_AMT",
                           parmD.getDouble("QTY", i) *
                           parmD.getDouble("UNIT_PRICE", i));
            stockD.addData("GIFTIN_QTY", parmD.getDouble("GIFT_QTY", i));
            stockD.addData("DAY_REGRESSGOODS_QTY", 0);
            stockD.addData("DAY_REGRESSGOODS_AMT", 0);
            stockD.addData("DAY_REQUESTIN_QTY", 0);
            stockD.addData("DAY_REQUESTOUT_QTY", 0);
            stockD.addData("DAY_CHANGEIN_QTY", 0);
            stockD.addData("DAY_CHANGEOUT_QTY", 0);
            stockD.addData("DAY_TRANSMITIN_QTY", 0);
            stockD.addData("DAY_TRANSMITOUT_QTY", 0);
            stockD.addData("DAY_WASTE_QTY", 0);
            stockD.addData("DAY_DISPENSE_QTY", 0);
            stockD.addData("DAY_REGRESS_QTY", 0);
            stockD.addData("FREEZE_TOT", 0);
            stockD.addData("UNIT_PRICE", parmD.getDouble("UNIT_PRICE", i));
            stockD.addData("STOCK_UNIT", parmD.getValue("STOCK_UNIT", i));
            stockD.addData("OPT_USER", Operator.getID());
            stockD.addData("OPT_DATE", date);
            stockD.addData("OPT_TERM", Operator.getIP());
        }
        System.out.println("stockD.getData()=========="+stockD.getData());
        parm.setData("STOCK_D", stockD.getData());
        return parm;
    }

    /**
     * 取得库存序号管理细项数据(INV_STOCKDD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertInvStockDDData(TParm parm) {
        TParm stockDD = new TParm();
        TParm parmDD = parm.getParm("VER_DD");
        String org_code ="";
        if (getCheckBox("CON_FLG").isSelected()) {
        	 org_code = this.getValueString("CON_ORG");
		}else {
			 org_code = this.getValueString("ORG_CODE");
		}
    
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmDD.getCount("INV_CODE"); i++) {
            stockDD.addData("INV_CODE", parmDD.getValue("INV_CODE", i));
            stockDD.addData("INVSEQ_NO", parmDD.getValue("INVSEQ_NO", i));
            stockDD.addData("REGION_CODE", Operator.getRegion());
            stockDD.addData("BATCH_SEQ", parmDD.getInt("BATCH_SEQ", i));
            stockDD.addData("ORG_CODE", org_code);
            stockDD.addData("BATCH_NO", parmDD.getValue("BATCH_NO", i));
            stockDD.addData("VALID_DATE", parmDD.getData("VALID_DATE", i));
            stockDD.addData("STOCK_QTY", 1);
            stockDD.addData("UNIT_PRICE", parmDD.getDouble("UNIT_PRICE", i));
            stockDD.addData("STOCK_UNIT", parmDD.getValue("STOCK_UNIT", i));
            stockDD.addData("CHECKTOLOSE_FLG", "N");
            stockDD.addData("WAST_FLG", "N");
            stockDD.addData("VERIFYIN_DATE", date);
            stockDD.addData("PACK_FLG", "N");
            stockDD.addData("ACTIVE_FLG", "");
            stockDD.addData("CABINET_ID", "");
            stockDD.addData("OPT_USER", Operator.getID());
            stockDD.addData("OPT_DATE", date);
            stockDD.addData("RFID", parmDD.getValue("RFID", i));
            stockDD.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("STOCK_DD", stockDD.getData());
        return parm;
    }

    /**
     * 取得订购单细项数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPuroderDData(TParm parm) {
        TParm purorderD = new TParm();
        TParm parmD = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            purorderD.addData("PURORDER_NO", parmD.getValue("PURORDER_NO", i));
            purorderD.addData("SEQ_NO", parmD.getInt("STESEQ_NO", i));
            purorderD.addData("STOCKIN_SUM_QTY", parmD.getDouble("QTY", i));
            purorderD.addData("UNDELIVERY_QTY", parmD.getDouble("QTY", i));
            purorderD.addData("OPT_USER", Operator.getID());
            purorderD.addData("OPT_DATE", date);
            purorderD.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("PUR_D", purorderD.getData());
        return parm;
    }

    /**
     * 取得订购单主项数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPurorderMData(TParm parm) {
        TParm purorderM = new TParm();
        TParm parmD = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        purorderM.setData("PURORDER_NO", parmD.getValue("PURORDER_NO", 0));
        purorderM.setData("OPT_USER", Operator.getID());
        purorderM.setData("OPT_DATE", date);
        purorderM.setData("OPT_TERM", Operator.getIP());

        parm.setData("PUR_M", purorderM.getData());
        return parm;
    }

    /**
     * 物资字典更新移动加权平均(INV_BASE)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvBaseData(TParm parm) {
        TParm invbase = new TParm();
        TParm parmD = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        double sum_qty = 0;
        double cost_price = 0;
        double in_qty = 0;
        double verifyin_qty = 0;
        double unit_price = 0;
        double gift_qty = 0;
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            invbase.addData("INV_CODE", inv_code);
            // Sum(库存量INV_STOCKM.STOCK_QTY
            TParm stockQty = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockSumQty(inv_code)));
            if (stockQty.getCount() > 0) {
                sum_qty = stockQty.getDouble("SUM_QTY", 0);
            }
            // 加权平均成本价
            TParm costPrice = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBase(inv_code)));
            // 转换率
            TParm rateParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvTransUnit(inv_code)));
            cost_price = costPrice.getDouble("COST_PRICE", 0);
            // 入库数量
            in_qty = parmD.getDouble("QTY", i);
            // 验收数量
            verifyin_qty = parmD.getDouble("QTY", i) *
                rateParm.getDouble("STOCK_QTY", 0) *
                rateParm.getDouble("DISPENSE_QTY", 0);
            // 单价
            unit_price = parmD.getDouble("UNIT_PRICE", i);
            // 赠与数
            gift_qty = parmD.getDouble("GIFT_QTY", i) *
                rateParm.getDouble("STOCK_QTY", 0) *
                rateParm.getDouble("DISPENSE_QTY", 0);

            cost_price = (sum_qty * cost_price +
                          in_qty *
                          ( (verifyin_qty * unit_price /
                             (verifyin_qty + gift_qty)))) /
                (sum_qty + in_qty);
            //System.out.println(i + "---" + cost_price);
            invbase.addData("COST_PRICE", cost_price);
            invbase.addData("OPT_USER", Operator.getID());
            invbase.addData("OPT_DATE", date);
            invbase.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("BASE", invbase.getData());
        return parm;
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的列
        int column = tableDown.getSelectedColumn();
        if (column == 0) {
            //计算总金额
            this.setValue("INVOICE_AMT", getSumAMT());
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        /**
         * 权限控制
         * 权限1:一般个人无赠与权限,只显示自已所属科室;无赠与录入功能
         * 权限2:一般个人赠与权限,只显示自已所属科室;包含赠与录入功能
         * 权限9:最大权限,显示全院药库部门包含赠与录入功能
         */
        // 赠与权限
        if (!this.getPopedem("giftEnabled")) {
            TTable table_d = getTable("TABLE_D");
            table_d.setLockColumns("1,2,4,5,7,12,13,14,15,16");
            gift_flg = false;
        }
        // 显示全院药库部门
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "ORG_CODE");
        TextFormatINVOrg inv_org_q = (TextFormatINVOrg)this.getTextFormat(
            "ORG_CODE_Q");
        if (!this.getPopedem("deptAll")) {
            inv_org.setOperatorId(Operator.getID());
            inv_org_q.setOperatorId(Operator.getID());
            dept_flg = false;
        }
        else {
            inv_org.setOperatorId("");
            inv_org_q.setOperatorId("");
            dept_flg = true;
        }
        //kindmap初始化分类 rfid编码规则
        kindmap=new HashMap<String, String>();
        kindmap.put("01", "A");
        kindmap.put("02", "A");
        kindmap.put("03", "A");
        kindmap.put("04", "A");
        kindmap.put("05", "A");
        kindmap.put("06", "A");
        kindmap.put("07", "A");
        kindmap.put("08", "C");
     
        

        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        setValue("VERIFYIN_DATE", date);
        setValue("SUP_CODE", "19");
        setValue("ORG_CODE", "011201");
        setValue("ORG_CODE_Q", "011201");
        

        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        table_dd = getTable("TABLE_DD");

        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");


        // 初始化TABLE_D的Parm
        TParm parmD = new TParm();
        String[] verD = {
            "SELECT_FLG", "PURORDER_NO", "PURORDER_DATE", "INV_CHN_DESC",
            "PURORDER_QTY", "GIFT_QTY", "BILL_UNIT", "PURORDER_PRICE",
            "STOCKIN_SUM_QTY", "INV_CODE", "DESCRIPTION", "MAN_CODE",
            "VALIDATE_FLG", "SEQMAN_FLG", "PURCH_QTY", "STOCK_QTY",
            "SUP_CODE", "STATIO_NO", "STOCK_UNIT", "SEQ_NO", "VALID_DATE",
            "BATCH_NO", "DISPENSE_UNIT", "DISPENSE_QTY"};
        for (int i = 0; i < verD.length; i++) {
            parmD.setData(verD[i], new Vector());
        }
        table_d.setParmValue(parmD);
        getCheckBox("CHECK_FLG").setSelected(true);
        getTextFormat("CON_ORG").setEnabled(false);
    }
	
		public void onConChange() {
			// TODO Auto-generated method stub
			if (getCheckBox("CON_FLG").isSelected()) {
				   getTextFormat("CON_ORG").setValue(Operator.getDept());
				   
			}else {
				 getTextFormat("CON_ORG").setValue("");
			}
			
			
		
		}
		
    /**
     * 计算总金额
     * @return double
     */
    private double getSumAMT() {
        TParm parm = table_d.getParmValue();
        double sum_amt = 0;
        for (int i = 0; i < 10; i++) {
            if (!"Y".equals(parm.getValue("SELECT_FLG", i))) {
                continue;
            }
            sum_amt +=
                (parm.getDouble("VERIFYIN_AMT", i) -
                 parm.getDouble("QUALITY_DEDUCT_AMT", i));
        }
        return sum_amt;
    }
    
    
//    
//    /**
//	 * 通过excel导入员工信息,默认EXCEL的格式为第一行为表头，
//	 * 各列顺序形如：序号，姓名，身份证号，性别，套餐代码，外国注记，出生日期，工号，增项团体，电话，邮编，地址，预检时间
//	 * 各列的顺序不能改变 并且默认为信息是在excel的第一个sheet页。
//	 */
//	public void onInsertPatByExl() {
//		if ("".equals(getValueString("ORG_CODE"))) {
//			this.messageBox("验收部门不能为空");
//			return;
//		}
//		if ("".equals(getValueString("SUP_CODE"))) {
//			this.messageBox("供应厂商不能为空");
//			return;
//		}		
//		TParm parm = new TParm();
//		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
//		String supCode = getValueString("SUP_CODE");
//		if (getRadioButton("GEN_DRUG").isSelected()) {// 非麻精
//			parm.setData("DROG_TYPE", "N");
//		} else {// 麻精
//			parm.setData("DROG_TYPE", "Y");
//		}
//		
// 		JFileChooser fileChooser = new JFileChooser();
//		int option = fileChooser.showOpenDialog(null);
//	
//		if (option == JFileChooser.APPROVE_OPTION) {
//			File file = fileChooser.getSelectedFile();
//			String filePath = file.getPath();
//			System.out.println("----------filePaht:"+filePath);
//			if (filePath != null) {
//				TParm addParm = new TParm();
//				try {
////					addParm = (TParm) FileUtils.readXMLFileP(filePath);
//					addParm = (TParm) FileParseExcel.getInstance().readXls(filePath);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				resultParm = (TParm) addParm;
//				TTable table = getTable("TABLE_D");
//				table.removeRowAll();
//				double purorder_qty = 0;
//				double actual_qty = 0;
//				double puroder_price = 0;
//				double retail_price = 0;
//				// 供应厂商
////				getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
//				// 计划单号
//				this.setValue("PLAN_NO", "");
//
//				getRadioButton("GEN_DRUG").setEnabled(false);
//				getRadioButton("TOXIC_DRUG").setEnabled(false);
//				int rowCount = 0 ;
//				//检查药品是否有供应商信息
///*				String message = checkOrderCodeInAgent(supCode,addParm);
//				if (null != message && message.length()>0) {//如果没有先手动维护
//					this.messageBox("没有以下药品的供应商和价格信息："+message);
//					return;
//				}*/
//				for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
//					String erpId = addParm.getInt("ERP_PACKING_ID", i)+"";
//					//先判断ERP_ID是否已经存在 true存在
//					boolean flg = isImpERPInfo("","","",erpId);
//					if(flg){//如果存在 进行下一个循环
//						continue;
//					}
//					int row = table.addRow();
//					// ORDER_CODE
//					String orderCode = addParm.getValue("ORDER_CODE", i);
//					TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
////					System.out.println("phaParm:"+phaParm);
//					// 填充DATESTORE
//					resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
//					table.getDataStore().setItem(row, "ORDER_CODE",orderCode);
//					table.getDataStore().setItem(row, "ORDER_DESC", phaParm.getValue("ORDER_DESC", 0));
//					// 出入库数量转换率
//					TParm getTRA = INDTool.getInstance().getTransunitByCode(orderCode);
//					if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
//						this.messageBox("药品" + orderCode + "转换率错误");
//						return;
//					}
//					// 填充TABLE_D数据
//					// 验收量
//					purorder_qty = addParm.getDouble("PURORDER_QTY", i);
//					int stockQty = getTRA.getInt("STOCK_QTY", 0);
//					//中包装
//					int conversionTraio = phaParm.getInt("CONVERSION_RATIO", 0);
//					conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
////					System.out.println("--------stockQty: "+stockQty+",conversionTraio:"+conversionTraio+",--purorder_qty:"+purorder_qty);
//					purorder_qty = purorder_qty * stockQty * conversionTraio;
////					System.out.println("--------purorder_qty: "+purorder_qty);
//					table.setItem(row, "VERIFYIN_QTY", purorder_qty);
//					// 赠送量
//					table.setItem(row, "GIFT_QTY", 0);
//					// 进货单位
//					// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
//					// 0));
//					table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));
//
//					// 验收单价
//					puroder_price = addParm.getDouble("PURORDER_PRICE", i);
////					System.out.println(i+"--------------SPCSQL.getPriceOfSupCode: "+SPCSQL.getPriceOfSupCode("18", orderCode));
//					/******************验收价格 取 ind_agent 改为pha_base  by liyh 20130313 start*****************************/
//					//查询 供应商的价格
//					TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
//					if(null != agentParm && agentParm.getCount()>0 ){
//						double verifyPrice =  agentParm.getDouble("LAST_VERIFY_PRICE", 0);
//						verifyPrice =  agentParm.getDouble("CONTRACT_PRICE", 0);
//						table.setItem(row, "VERIFYIN_PRICE", StringTool.round(verifyPrice,4));
//						// 进货金额
//						table.setItem(row, "INVOICE_AMT", StringTool.round(verifyPrice*purorder_qty,2));
//			
//					}
//					/*				else{//代理商的药品信息不能自动维护
//						//如果次供应商没有代理这个药品则保村
//						onSaveAgentInfo(orderCode,StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4),supCode);
//						table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
//						// 进货金额
//						table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));
//					}
//					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
//					// 进货金额
//					table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));*/
//					/******************验收价格 取 ind_agent 改为pha_base  by liyh 20130313  end *****************************/
//					// 零售价
//					retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
//					table.setItem(row, "RETAIL_PRICE", StringTool.round(phaParm.getDouble("RETAIL_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
//						
//					// 订购单号
//					table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
//					// 订购单号序号
//					table.setItem(row, "PURSEQ_NO", addParm.getData("PURSEQ_NO", i));
//					// 累计验收数
//					table.setItem(row, "ACTUAL", 0);
//
//					String invoiceDate = addParm.getData("INVOICE_DATE", i) + "";
//					invoiceDate = invoiceDate.replaceAll("-", "/");
//					// 发票日期
//					table.setItem(row, "INVOICE_DATE", invoiceDate);
//					String validDate = addParm.getData("VALID_DATE", i) + "";
//					validDate = validDate.replaceAll("-", "/");
//					table.setItem(row, "REASON_CHN_DESC", "VER01");
//					// 效期
//					table.setItem(row, "VALID_DATE", validDate);
//					// 生产厂商
//					table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
//					// 发票号
//					table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
//					// 批号
//					table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
//					// 批号
//					table.setItem(row, "ERP_PACKING_ID", addParm.getData("ERP_PACKING_ID", i));				
//					// 装箱单号
//					String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
//					table.setItem(row, "SPC_BOX_BARCODE",boxCode);
//					table.getDataStore().setItem(i, "UPDATE_FLG", "0");
//					table.getDataStore().setActive(row, false);
//				}
//				table.setDSValue();
//				getComboBox("ORG_CODE").setEnabled(false);
//				getTextFormat("SUP_CODE").setEnabled(false);
//				action = "insert";
//				this.setCheckFlgStatus(action);
//				getCheckBox("SELECT_ALL").setSelected(true);
//				onCheckSelectAll();
//			}
//		}
//		//onPackage();
//
//	}	

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
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
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
     * 得到TCheckBox对象
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
    }

}
