package com.javahis.ui.bms;

import java.sql.Timestamp;
import java.util.Date;

import jdo.bms.BMSBldCodeTool;
import jdo.bms.BMSSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 血液入库
 * </p>
 *
 * <p>
 * Description: 血液入库
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
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSBloodInControl
    extends TControl {

    private String action = "insert";

    private TTable table;

    public BMSBloodInControl() {

    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        TParm parm = new TParm();
        parm.setData("RH_FLG",
                     getRadioButton("RH_FLG_A").isSelected() ? "+" : "-");
        parm.setData("BLD_CODE", this.getValueString("BLD_CODE"));
        parm.setData("BLDRESU_CODE", this.getValueString("BLDRESU_CODE"));
        parm.setData("SUBCAT_CODE", this.getValueString("SUBCAT_CODE"));
        parm.setData("BLD_SUBCAT", this.getValueString("SUBCAT_CODE"));
        parm.setData("IN_DATE", date);
        parm.setData("BLD_TYPE", this.getValueString("BLD_TYPE"));
        parm.setData("SHIT_FLG", this.getValueString("SHIT_FLG"));
        parm.setData("END_DATE", this.getValue("VALID_DATE"));
        parm.setData("IN_PRICE", this.getValueDouble("IN_PRICE"));
        TParm inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
            getBMSBldVol(this.getValueString("BLD_CODE"),
                         this.getValueString("SUBCAT_CODE"))));
        parm.setData("BLOOD_VOL", inparm.getDouble("BLD_VOL", 0));
        parm.setData("ORG_BARCODE", this.getValueString("ORG_BARCODE"));
        parm.setData("STATE_CODE", "0");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();

        String myBloodNo = "" ;
        if ("insert".equals(action)) {
            String blood_no = SystemTool.getInstance().getNo("ALL",
                "BMS", "BMS_BLOOD", "No");
            parm.setData("BLOOD_NO", blood_no);
            myBloodNo = blood_no ;
            // 执行数据新增
            result = TIOM_AppServer.executeAction(
                "action.bms.BMSBloodAction", "onInsert", parm);
        }
        else if ("update".equals(action)) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
            
            myBloodNo = this.getValueString("BLOOD_NO") ;
            // 执行数据更新
            result = TIOM_AppServer.executeAction(
                "action.bms.BMSBloodAction", "onUpdate", parm);
        }
        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        //modify by lim 2012/05/06 begin
        Timestamp date1 = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",date1.toString().substring(0, 10).replace('-', '/') +" 23:59:59");
        this.setValue("START_DATE", date1.toString().substring(0, 10).replace('-', '/') + " 00:00:00");        
        //modify by lim 2012/05/06 end
        //onClear();
        this.clearValue("ORG_BARCODE;BLOOD_NO") ;
        onQuery();
        
        TParm data = new TParm();
        TParm parmData = new TParm();
        // 条码
        parmData.addData("BLOOD_NO", myBloodNo);
        // 血液
        parmData.addData("BLD_CODE",
                         "血液:" + this.getComboBox("BLD_CODE").getSelectedName());
        // 血型
        parmData.addData("BLD_TYPE",
                         "血型:" + this.getValueString("BLD_TYPE") +
                         " 型");
        // 来血单位
        parmData.addData("BLDRESU_CODE", "来源:" +
                         this.getComboBox("BLDRESU_CODE").getSelectedName());
        // 入库日期
        parmData.addData("IN_DATE", "入库日期:" +StringTool.getString(date,"yyyy/MM/dd"));
        // 失效日期
        parmData.addData("END_DATE",
                         "失效日期:" +
                         this.getValueString("VALID_DATE").
                         substring(0, 10).replace('-', '/'));

        parmData.setCount(1);
        parmData.addData("SYSTEM", "COLUMNS", "BAR_INFO");
        data.setData("TABLE", parmData.getData());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBarCode.jhw", data,true);
        
    }

    /**
     * 清空方法
     */
    public void onClear() {
//        String clearString =
//            "BLDRESU_CODE;ORG_BARCODE;BLOOD_NO;BLD_CODE;SUBCAT_CODE;" +
//            "BLD_TYPE;BLD_TYPE;SHIT_FLG;VALID_DATE;VALID_DAY;IN_PRICE";
//    	String clearString =
//            "ORG_BARCODE;BLOOD_NO;" +
//            "SHIT_FLG;VALID_DATE;VALID_DAY;IN_PRICE";
    	String clearString =
            "ORG_BARCODE;BLOOD_NO;" +
            "SHIT_FLG;VALID_DAY;IN_PRICE";    	
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        
        //modify by lim 2012/05/06 begin
//        this.setValue("START_DATE",
//                      StringTool.rollDate(date, -7).toString().substring(0, 10).
//                      replace('-', '/') + " 00:00:00");
        
        this.setValue("START_DATE",date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");        
        //modify by lim 2012/05/06 end
        
        getRadioButton("RH_FLG_A").setSelected(true);
        table.removeRowAll();
        table.setSelectionMode(0);
        action = "insert";
        this.getComboBox("BLD_CODE").setEnabled(true);
        this.getComboBox("SUBCAT_CODE").setEnabled(true);
        this.getComboBox("BLD_TYPE").setEnabled(true);
        this.grabFocus("ORG_BARCODE");
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        TParm date = new TParm();
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "血品入库明细单");
        // 表格数据
        TParm parm = new TParm();
        String blood_no = "";
        if (table.getSelectedRow() != -1) {
            blood_no = table.getItemString(table.getSelectedRow(), "BLOOD_NO");
            TParm bldInfoParm = new TParm(TJDODBTool.getInstance().select(
                BMSSQL.getBMSBldStockInfo(blood_no)));
            parm.addData("BLDCODE_DESC", bldInfoParm.getValue("BLDCODE_DESC", 0));
            parm.addData("SUBCAT_DESC", bldInfoParm.getValue("SUBCAT_DESC", 0));
            parm.addData("BLOOD_NO", bldInfoParm.getValue("BLOOD_NO", 0));
            parm.addData("ORG_BARCODE", bldInfoParm.getValue("ORG_BARCODE", 0));
            parm.addData("BLD_TYPE", bldInfoParm.getValue("BLD_TYPE", 0));
            parm.addData("RH_FLG", bldInfoParm.getValue("RH_FLG", 0));
            parm.addData("END_DATE",
                         bldInfoParm.getValue("END_DATE", 0).substring(0, 10).
                         replace('-', '/'));
            parm.addData("IN_DATE",
                         bldInfoParm.getValue("IN_DATE", 0).substring(0, 10).
                         replace('-', '/'));
            parm.addData("USER_NAME", bldInfoParm.getValue("USER_NAME", 0));
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                blood_no = table.getItemString(i, "BLOOD_NO");
                TParm bldInfoParm = new TParm(TJDODBTool.getInstance().select(
                    BMSSQL.getBMSBldStockInfo(blood_no)));
                parm.addData("BLDCODE_DESC",
                             bldInfoParm.getValue("BLDCODE_DESC", 0));
                parm.addData("SUBCAT_DESC",
                             bldInfoParm.getValue("SUBCAT_DESC", 0));
                parm.addData("BLOOD_NO", bldInfoParm.getValue("BLOOD_NO", 0));
                parm.addData("ORG_BARCODE",
                             bldInfoParm.getValue("ORG_BARCODE", 0));
                parm.addData("BLD_TYPE", bldInfoParm.getValue("BLD_TYPE", 0));
                parm.addData("RH_FLG", bldInfoParm.getValue("RH_FLG", 0));
                parm.addData("END_DATE",
                             bldInfoParm.getValue("END_DATE", 0).substring(0,
                    10).replace('-', '/'));
                parm.addData("IN_DATE",
                             bldInfoParm.getValue("IN_DATE", 0).substring(0, 10).
                             replace('-', '/'));
                parm.addData("USER_NAME", bldInfoParm.getValue("USER_NAME", 0));
            }
        }
        parm.setCount(parm.getCount("BLDCODE_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SUBCAT_DESC");
        parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
        parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
        parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
        parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
        parm.addData("SYSTEM", "COLUMNS", "END_DATE");
        parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
        parm.addData("SYSTEM", "COLUMNS", "USER_NAME");

        date.setData("TABLE", parm.getData());

        date.setData("OPT_USER", "TEXT", "制表人: " + Operator.getName());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodIn.jhw", date);
    }

    /**
     * 条码打印
     */
    public void onPrintNo() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        TParm data = new TParm();
        TParm parmData = new TParm();
        int row = table.getSelectedRow();
        // 条码
        parmData.addData("BLOOD_NO", this.getValueString("BLOOD_NO"));
        // 血液
        parmData.addData("BLD_CODE",
                         "血液:" + this.getComboBox("BLD_CODE").getSelectedName());
        // 血型
        parmData.addData("BLD_TYPE",
                         "血型:" + this.getComboBox("BLD_TYPE").getSelectedName() +
                         " 型");
        // 来血单位
        parmData.addData("BLDRESU_CODE", "来源:" +
                         this.getComboBox("BLDRESU_CODE").getSelectedName());
        // 入库日期
        parmData.addData("IN_DATE", "入库日期:" +
                         table.getItemString(row, "IN_DATE").substring(0, 10).
                         replace('-', '/'));
        // 失效日期
        parmData.addData("END_DATE",
                         "失效日期:" +
                         this.getValueString("VALID_DATE").
                         substring(0, 10).replace('-', '/'));

        parmData.setCount(1);
        parmData.addData("SYSTEM", "COLUMNS", "BAR_INFO");
        data.setData("TABLE", parmData.getData());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBarCode.jhw", data);
    }
    
    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("START_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
        }
        if (!"".equals(this.getValueString("END_DATE"))) {
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        if (!"".equals(this.getValueString("BLDRESU_CODE"))) {
            parm.setData("BLDRESU_CODE", this.getValue("BLDRESU_CODE"));
        }
        if (!"".equals(this.getValueString("BLOOD_NO"))) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
        }
        if (!"".equals(this.getValueString("BLD_CODE"))) {
            parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        }
        if (!"".equals(this.getValueString("SUBCAT_CODE"))) {
            parm.setData("SUBCAT_CODE", this.getValue("SUBCAT_CODE"));
        }
        if (!"".equals(this.getValueString("BLD_TYPE"))) {
            parm.setData("BLD_TYPE", this.getValue("BLD_TYPE"));
        }

        TParm result = TIOM_AppServer.executeAction(
            "action.bms.BMSBloodAction", "onQuery", parm);

        if (result.getCount("BLOOD_NO") == 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(result);
        this.grabFocus("ORG_BARCODE") ;
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            this.messageBox("没有删除项");
            return;
        }
        TParm parm = new TParm();
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("BLOOD_NO", this.getValueString("BLOOD_NO"));
        parm.setData("BLD_CODE", this.getValueString("BLD_CODE"));
        parm.setData("BLD_TYPE", this.getValueString("BLD_TYPE"));
        parm.setData("BLD_SUBCAT", this.getValueString("SUBCAT_CODE"));
        TParm inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
            getBMSBldVol(this.getValueString("BLD_CODE"),
                         this.getValueString("SUBCAT_CODE"))));
        parm.setData("BLOOD_VOL", inparm.getDouble("BLD_VOL", 0));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.bms.BMSBloodAction",
                                              "onDelete", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        table.removeRow(row);
        table.setSelectionMode(0);
        this.messageBox("删除成功");
        onClear();
        onQuery();
    }

    /**
     * 计算有效天数
     */
    public void onValiidDay() {
        Timestamp date = StringTool.getTimestamp(new Date());
        if ( ( (Timestamp)this.getValue("VALID_DATE")).compareTo(date) <= 0) {
            this.messageBox("效期不能早于当天");
            return;
        }
        int day = StringTool.getDateDiffer( (Timestamp)this.getValue(
            "VALID_DATE"), date);
        this.setValue("VALID_DAY", day + 1);
    }

    /**
     * 变更血品
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(
            bld_code);
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
        TParm parm = new TParm();
        parm.setData("BLD_CODE", bld_code);
        TParm result = BMSBldCodeTool.getInstance().onQuery(parm);
        this.setValue("VALID_DAY", result.getDouble("VALUE_DAYS", 0));
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("VALID_DATE",
                      StringTool.rollDate(date, result.getLong("VALUE_DAYS", 0)));
    }

    /**
     * 表格单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
        	//==================modify by  chenxi 20130307
        	String startDate = table.getItemData(row, "IN_DATE").toString().substring(0, 10).replace("-", "") ;
            this.setValue("START_DATE",StringTool.getTimestamp(startDate+"000000", "yyyyMMddHHmmss"));
            this.setValue("END_DATE", StringTool.getTimestamp(startDate+"235959", "yyyyMMddHHmmss"));
            this.setValue("BLDRESU_CODE", table.getItemData(row, "BLDRESU_CODE"));
            this.setValue("ORG_BARCODE", table.getItemData(row, "ORG_BARCODE"));
            this.setValue("BLOOD_NO", table.getItemData(row, "BLOOD_NO"));
            this.setValue("BLD_CODE", table.getItemData(row, "BLD_CODE"));
            this.setValue("SUBCAT_CODE", table.getItemData(row, "SUBCAT_CODE"));
            this.setValue("BLD_TYPE", table.getItemData(row, "BLD_TYPE"));
            if ("+".equals(table.getItemString(row, "RH_FLG"))) {
                this.getRadioButton("RH_FLG_A").setSelected(true);
            }
            else {
                this.getRadioButton("RH_FLG_B").setSelected(true);
            }
            this.setValue("IN_PRICE", table.getItemData(row, "IN_PRICE"));
            this.setValue("SHIT_FLG", table.getItemData(row, "SHIT_FLG"));
            this.setValue("VALID_DATE", table.getItemData(row, "END_DATE"));
            Timestamp date = StringTool.getTimestamp(new Date());
            int day = StringTool.getDateDiffer(table.getItemTimestamp(row,
                "END_DATE"), date);
            this.setValue("VALID_DAY", day + 1);
            action = "update";
//            this.getComboBox("BLD_CODE").setEnabled(false);
//            this.getComboBox("SUBCAT_CODE").setEnabled(false);
//            this.getComboBox("BLD_TYPE").setEnabled(false);
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // 初始化TABLE
        table = getTable("TABLE");
    }

    /**
     * 检核数据
     * @return boolean
     */
    private boolean CheckData() {
        if ("update".equals(action)) {
            if ("".equals(this.getValueString("BLOOD_NO"))) {
                this.messageBox("院内条形码不能为空");
                return false;
            }
        }
        if ("".equals(this.getValueString("BLDRESU_CODE"))) {
            this.messageBox("血品来源不能为空");
            return false;
        }
        if ("".equals(this.getValueString("BLD_CODE"))) {
            this.messageBox("血品代码不能为空");
            return false;
        }
        if ("".equals(this.getValueString("SUBCAT_CODE"))) {
            this.messageBox("血品规格不能为空");
            return false;
        }
        if ("".equals(this.getValueString("BLD_TYPE"))) {
            this.messageBox("血型不能为空");
            return false;
        }
//        if ("".equals(this.getValueString("IN_PRICE"))) {
//            this.messageBox("入库价格不能为空");
//            return false;
//        }
        if (this.getValueDouble("IN_PRICE") < 0) {
            this.messageBox("入库价格不能小于0");
            return false;
        }
        //===============modify by lim 2012/03/29 begin
//        if ("".equals(this.getValueString("SHIT_FLG"))) {
//            this.messageBox("抗体筛检不能为空");
//            return false;
//        }
        //===============modify by lim 2012/03/29 end
        if ("".equals(this.getValueString("VALID_DATE"))) {
            this.messageBox("效期不能为空");
            return false;
        }
        return true;
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
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
