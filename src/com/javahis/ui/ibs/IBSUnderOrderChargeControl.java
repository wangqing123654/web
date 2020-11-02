package com.javahis.ui.ibs;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jdo.adm.ADMInpTool;
import jdo.bil.BIL;
import jdo.inw.InwOrderExecTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 住院补充计费 </p>
 * 
 * <p> Description: 住院补充计费</p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company:BlueCore </p>
 * 
 * @author wanglong 2013.12.09
 * @version 1.0
 */
public class IBSUnderOrderChargeControl
        extends TControl {

    TTable table;
    private String caseNo = "";// 就诊号
    private String mrNo = "";// 病案号
    private String ipdNo = "";// 住院号
    private String bedNo = "";// 床号
    private String deptCode = "";// 科室
    private String stationCode = "";// 病区
    private String serviceLevel = "";// 护理等级
    private String vsDrCode = "";// 经治医生
    private String clpCode = "";// 临床路径
    // private String orderCode = "";//医嘱代码
    private String orderNo = "";
    private int orderSeq = 1;//
    private int caseNoSeq = 1;
    private int seqNo = 1;
    private int tempGroupNo = 1;//临时组号
    private int orderSetGroupNo = 1;
    private String ctz1Code;// 身份一 wanglong add 20140522
    private String ctz2Code;// 身份二
    private String ctz3Code;// 身份三
    private double ownRate; // 自付比例
    
    private TParm inParm = new TParm();// 入参

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponent");
        this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, "onTableValueChange");
        inParm = (TParm) this.getParameter();
        caseNo = inParm.getValue("CASE_NO");
        mrNo = inParm.getValue("MR_NO");
        ipdNo = inParm.getValue("IPD_NO");
        bedNo = inParm.getValue("BED_NO");
        deptCode = inParm.getValue("DEPT_CODE");
        stationCode = inParm.getValue("STATION_CODE");
        serviceLevel = inParm.getValue("SERVICE_LEVEL");
        vsDrCode = inParm.getValue("VS_DR_CODE");
        clpCode = inParm.getValue("CLNCPATH_CODE");
        // orderCode = inParm.getValue("ORDER_CODE");
        orderNo = inParm.getValue("ORDER_NO");
        orderSeq = inParm.getInt("ORDER_SEQ");
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", caseNo);
        TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);//wanglong 20140522
        serviceLevel = selAdmParm.getValue("SERVICE_LEVEL", 0);
        ctz1Code = selAdmParm.getValue("CTZ1_CODE", 0);
        ctz2Code = selAdmParm.getValue("CTZ2_CODE", 0);
        ctz3Code = selAdmParm.getValue("CTZ3_CODE", 0);
        table.removeRowAll();
        newOrderRow(inParm);
    }

    /**
     * 保存
     */
    public void onSave() {
        boolean flag = false;
        if (table.getRowCount() > 0) {
            for (int i = 0; i < table.getRowCount(); i++) {
                if (table.getItemString(i, "ORDER_CODE").length() > 0) {
                    flag = true;
                    break;
                }
            }
        }
        if (flag) {
            saveOrders(getOrders());// 保存新增医嘱
        } else {
            this.messageBox("没有要保存的数据");
        }
    }

    /**
     * 护士套餐
     */
    public void onNursePack() {
        // 护士套餐
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "IBS");
        parm.setData("DEPT_CODE", Operator.getDept());
        parm.setData("USER_ID", Operator.getID());
        parm.setData("DEPT_OR_DR", 4);// 1,3科室，2,4 人员
        parm.setData("RULE_TYPE", 4);// 权限 0,临时 1，长期 2,出院带药 3,中药 ,4全部
        parm.addListener("INSERT_TABLE", this, "onReturnContent");
        TWindow window =
                (TWindow) this.openDialog("%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
        window.setVisible(true);
    }

    /**
     * 科室套餐
     */
    public void onDeptPack() {
        // 科室套餐
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "IBS");
        parm.setData("DEPT_CODE", Operator.getDept());
        parm.setData("USER_ID", Operator.getID());
        parm.setData("DEPT_OR_DR", 3);// 1,3科室，2,4 人员
        parm.setData("RULE_TYPE", 4);// 权限 0,临时 1，长期 2,出院带药 3,中药 ,4全部
        parm.addListener("INSERT_TABLE", this, "onReturnContent");
        TWindow window =
                (TWindow) this.openDialog("%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
        window.setVisible(true);
    }

    /**
     * 套餐医嘱传回
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onReturnContent(Object obj) {
        boolean falg = true;
        if (obj != null) {
            List orderList = (ArrayList) obj;
            Iterator iter = orderList.iterator();
            while (iter.hasNext()) {
                TParm temp = (TParm) iter.next();
                insertPackOrder(temp, temp.getDouble("MEDI_QTY"));
            }
        }
        return falg;
    }

    /**
     * 插入套餐医嘱
     * 
     * @param parm
     * @param dosageQty
     */
    public void insertPackOrder(TParm parm, double dosageQty) {
        table.acceptText();
        TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(parm);// 收据费用代码
        double ownPriceSingle = 0;
        if ("2".equals(serviceLevel)) {
            ownPriceSingle = parm.getDouble("OWN_PRICE2");
        } else if ("3".equals(serviceLevel)) {
            ownPriceSingle = parm.getDouble("OWN_PRICE3");
        } else {
            ownPriceSingle = parm.getDouble("OWN_PRICE");
        }
        int selRow = table.getRowCount() - 1;
        if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
            table.setItem(selRow, "ORDERSET_GROUP_NO", parm.getInt("ORDERSET_GROUP_NO"));
            table.setItem(selRow, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
        } else {
            table.setItem(selRow, "ORDERSET_GROUP_NO", "");
            table.setItem(selRow, "ORDERSET_CODE", "");
        }
        table.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
        table.setItem(selRow, "MEDI_QTY", 1);
        table.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
        table.setItem(selRow, "FREQ_CODE", "STAT");
        table.setItem(selRow, "DOSE_CODE", parm.getValue("DOSE_CODE"));
        table.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
        table.setItem(selRow, "TAKE_DAYS", 1);
        table.setItem(selRow, "DOSAGE_QTY", 1);// 总量
        table.setItem(selRow, "OWN_PRICE", ownPriceSingle);
        table.setItem(selRow, "TOT_AMT", ownPriceSingle);
        table.setItem(selRow, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        table.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE", 0));
        TParm param = new TParm();
        param.setData("VS_DR_CODE", vsDrCode);
        param.setData("STATION_CODE", stationCode);
        newOrderRow(param);
    }

    /**
     * 单元格值改变事件
     * @param obj
     */
    public void onTableValueChange(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node.getColumn() != 5 && node.getColumn() != 6 && node.getColumn() != 1) {
            return;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = this.getColumnName(col);
        int row = node.getRow();
        String tackDays = "" + node.getValue();// 取得改变值天数
        double Tdays = 0;// 天数
        double ownMat = 0;// 单价
        double medQty = 0;
        String Tmat = table.getItemString(row, "OWN_PRICE");// 单价
        String Mqty = table.getItemString(row, "MEDI_QTY");// 用量
        if ("TAKE_DAYS".equals(columnName)) {
            ownMat = Double.parseDouble(Tmat);
            Tdays = Double.parseDouble(tackDays);
            medQty = Double.parseDouble(Mqty);
            table.setItem(row, "TOT_AMT", df.format(ownMat * Tdays));// 改变总价
            table.setItem(row, "DOSAGE_QTY", (int) (Tdays * medQty));// 改变总量
        }
        double dqyt = 0;
        if ("DOSAGE_QTY".equals(columnName)) {
            String qty = "" + node.getValue();// 取得改变用量的值
            ownMat = Double.parseDouble(Tmat);
            dqyt = Double.parseDouble(qty);
            table.setItem(row, "TOT_AMT", df.format(ownMat * dqyt));// 改变总价
        }
        if ("MEDI_QTY".equals(columnName)) {
            String tdays = table.getItemString(row, "TAKE_DAYS");
            String mqty = "" + node.getValue();
            ownMat = Double.parseDouble(Tmat);
            dqyt = Double.parseDouble(mqty);
            int days = Integer.parseInt(tdays);
            table.setItem(row, "DOSAGE_QTY", (int) (days * dqyt));// 改变总量
            table.setItem(row, "TOT_AMT", df.format(ownMat * dqyt));// 改变总价
        }
    }

    /**
     * TABLE创建编辑控件
     * 
     * @param com
     * @param row
     * @param col
     */
    public void onCreateEditComoponent(Component com, int row, int col) {
        // 拿到列名
        String columnName = this.getColumnName(col);
        if (!columnName.contains("ORDER_CHN_DESC")) return;
        if (!(com instanceof TTextField)) return;
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("PACK", "DEPT", Operator.getDept());
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("ITEM",
                                         getConfigParm()
                                                 .newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                         parm);
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }

    /**
     * 返回值事件
     * 
     * @param tag
     * @param obj
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table.acceptText();
        int selRow = table.getSelectedRow();
        double ownPriceSingle = 0.00;
        if ("2".equals(serviceLevel)) {
            ownPriceSingle = parm.getDouble("OWN_PRICE2");
        } else if ("3".equals(serviceLevel)) {
            ownPriceSingle = parm.getDouble("OWN_PRICE3");
        } else ownPriceSingle = parm.getDouble("OWN_PRICE");
        if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {// 联合医嘱
            table.setItem(selRow, "ORDERSET_GROUP_NO", tempGroupNo++);
            table.setItem(selRow, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
        } else {// 非联合医嘱
            table.setItem(selRow, "ORDERSET_GROUP_NO", "");
            table.setItem(selRow, "ORDERSET_CODE", "");
        }
        table.setItem(selRow, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        table.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
        table.setItem(selRow, "MEDI_QTY", 1);
        table.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
        table.setItem(selRow, "FREQ_CODE", "STAT");
        if (parm.getValue("CAT1_TYPE").equals("PHA")) {
            table.setItem(selRow,
                          "ROUTE_CODE",
                          StringUtil.getDesc("PHA_BASE", "ROUTE_CODE",
                                             "ORDER_CODE='" + parm.getValue("ORDER_CODE") + "'"));
        } else table.setItem(selRow, "DOSE_CODE", "");
        table.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
        table.setItem(selRow, "TAKE_DAYS", 1);
        table.setItem(selRow, "DOSAGE_QTY", 1);// 总量
        table.setItem(selRow, "OWN_PRICE", ownPriceSingle);
        ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code, parm.getValue("ORDER_CODE"), serviceLevel);//wanglong add 20140522
        table.setItem(selRow, "TOT_AMT", StringTool.round(ownPriceSingle * ownRate, 2));
        TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(parm);
        table.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE", 0));
        TParm xparm = new TParm();
        xparm.setData("VS_DR_CODE", vsDrCode);
        xparm.setData("STATION_CODE", stationCode);
        newOrderRow(xparm);
    }

    /**
     * 新医嘱行
     * @param parm
     */
    private void newOrderRow(TParm parm) {
        table.acceptText();
        int selRow = table.addRow();
        table.setItem(selRow, "ORDER_CODE", "");
        table.setItem(selRow, "ORDER_CHN_DESC", "");
        table.setItem(selRow, "MEDI_QTY", "");
        table.setItem(selRow, "MEDI_UNIT", "");
        table.setItem(selRow, "FREQ_CODE", "");
        table.setItem(selRow, "DOSE_CODE", "");
        table.setItem(selRow, "TAKE_DAYS", "");
        table.setItem(selRow, "DOSAGE_QTY", "");
        table.setItem(selRow, "DOSAGE_UNIT", "");
        table.setItem(selRow, "OWN_PRICE", "");
        table.setItem(selRow, "TOT_AMT", "");
        table.setItem(selRow, "EXE_DEPT_CODE", Operator.getCostCenter());
        String station = parm.getValue("STATION_CODE");
        table.setItem(selRow, "EXE_STATION_CODE", station);
        table.setItem(selRow, "EXE_DR_CODE", Operator.getID());
        table.setItem(selRow, "DEPT_CODE", Operator.getDept());
        table.setItem(selRow, "STATION_CODE", station);
        table.setItem(selRow, "DR_CODE", vsDrCode);
        table.setItem(selRow, "SCHD_CODE", clpCode);
        table.setItem(selRow, "BILL_DATE", SystemTool.getInstance().getDate());
        table.setItem(selRow, "INV_CODE", "");
        table.setItem(selRow, "SCHD_CODE", "");
        table.setItem(selRow, "CLNCPATH_CODE", "");
        table.setItem(selRow, "ORDER_NO", orderNo);
    }

    /**
     * 得到表格里的医嘱
     */
    public TParm getOrders() {
        TParm orderSetGroupNoParm = this.getMaxOrderGroupNo(caseNo);
        if (orderSetGroupNoParm.getErrCode() < 0) {
            return orderSetGroupNoParm;
        }
        orderSetGroupNo = orderSetGroupNoParm.getInt("ORDERSET_GROUP_NO", 0);
        TParm parm = new TParm();
        TParm setResult = new TParm();
        String orderCode = "";
        String orderSetCode = "";
        double totAmt = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getItemString(i, "ORDER_CODE").length() > 0) {
                orderSetGroupNo++;
                orderCode = table.getItemString(i, "ORDER_CODE");
                setResult = getOrderInfo(orderCode);//详细信息
                parm.addData("ORDER_NO", "");
                parm.addData("ORDER_CODE", table.getItemString(i, "ORDER_CODE"));
                parm.addData("ORDER_CHN_DESC", table.getItemString(i, "ORDER_CHN_DESC"));
                parm.addData("ORDERSET_CODE", table.getItemString(i, "ORDERSET_CODE"));
                parm.addData("ORDERSET_GROUP_NO", orderSetGroupNo);
                parm.addData("DEPT_CODE", table.getItemString(i, "DEPT_CODE"));
                parm.addData("STATION_CODE", table.getItemString(i, "STATION_CODE"));
                parm.addData("MEDI_QTY", table.getItemString(i, "MEDI_QTY"));
                parm.addData("MEDI_UNIT", table.getItemDouble(i, "MEDI_UNIT"));
                parm.addData("FREQ_CODE", "STAT");
                parm.addData("DOSE_CODE", setResult.getValue("DOSE_CODE", 0));//wanglong modify 20140522
                parm.addData("TAKE_DAYS", table.getItemString(i, "TAKE_DAYS"));
                parm.addData("DOSAGE_QTY", table.getItemDouble(i, "DOSAGE_QTY"));
                parm.addData("DOSAGE_UNIT", table.getItemString(i, "DOSAGE_UNIT"));
                parm.addData("OWN_PRICE", table.getItemDouble(i, "OWN_PRICE"));
                ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code, orderCode, serviceLevel);
                parm.addData("OWN_RATE", ownRate);
                totAmt =
                        table.getItemDouble(i, "OWN_PRICE") * table.getItemDouble(i, "DOSAGE_QTY")
                                * ownRate;
                parm.addData("TOT_AMT", StringTool.round(totAmt, 2));//wanglong modify 20140522
                parm.addData("EXE_DEPT_CODE", table.getItemString(i, "EXE_DEPT_CODE"));
                parm.addData("COST_CENTER_CODE", table.getItemString(i, "EXE_DEPT_CODE"));//wanglong add 20140522
                parm.addData("EXE_STATION_CODE", table.getItemString(i, "EXE_STATION_CODE"));
                parm.addData("EXE_DR_CODE", table.getItemString(i, "EXE_DR_CODE"));
                parm.addData("DR_CODE", table.getItemString(i, "DR_CODE"));
                parm.addData("SCHD_CODE", table.getItemString(i, "SCHD_CODE"));
                parm.addData("INV_CODE", table.getItemString(i, "INV_CODE"));
                parm.addData("CLNCPATH_CODE", table.getItemString(i, "CLNCPATH_CODE"));
                parm.addData("REXP_CODE", table.getItemString(i, "REXP_CODE"));
                parm.addData("HEXP_CODE", setResult.getValue("CHARGE_HOSP_CODE", 0));
                parm.addData("CAT1_TYPE", setResult.getValue("CAT1_TYPE", 0));
                parm.addData("ORDER_CAT1_CODE", setResult.getValue("ORDER_CAT1_CODE", 0));
                orderSetCode = table.getItemString(i, "ORDERSET_CODE");
                if (orderSetCode.length() > 0) {
                    setResult = getOrderDetails(orderSetCode);//集合医嘱细项
                    for (int j = 0; j < setResult.getCount(); j++) {
                        parm.addData("ORDER_NO", "");
                        parm.addData("ORDER_CODE", setResult.getValue("ORDER_CODE", j));
                        parm.addData("ORDER_CHN_DESC", setResult.getValue("ORDER_DESC", j));
                        parm.addData("ORDERSET_CODE", table.getItemString(i, "ORDERSET_CODE"));
                        parm.addData("ORDERSET_GROUP_NO", orderSetGroupNo);
                        parm.addData("DEPT_CODE", table.getItemString(i, "DEPT_CODE"));
                        parm.addData("STATION_CODE", table.getItemString(i, "STATION_CODE"));
                        parm.addData("MEDI_QTY", setResult.getInt("DOSAGE_QTY", j));
                        parm.addData("MEDI_UNIT", setResult.getValue("UNIT_CODE", j));
                        parm.addData("FREQ_CODE", "STAT");
                        parm.addData("DOSE_CODE", "");
                        parm.addData("TAKE_DAYS", table.getItemString(i, "TAKE_DAYS"));
                        parm.addData("DOSAGE_QTY", setResult.getInt("DOSAGE_QTY", j));
                        parm.addData("DOSAGE_UNIT", setResult.getValue("UNIT_CODE,j"));
                        parm.addData("OWN_PRICE", setResult.getDouble("OWN_PRICE", j));
                        ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code, setResult.getValue("ORDER_CODE", j), serviceLevel);
                        parm.addData("OWN_RATE", ownRate);
                        totAmt =
                                setResult.getDouble("OWN_PRICE", j)
                                        * setResult.getInt("DOSAGE_QTY", j) * ownRate;
                        parm.addData("TOT_AMT", StringTool.round(totAmt, 2));//wanglong modify 20140522
                        parm.addData("EXE_DEPT_CODE", table.getItemString(i, "EXE_DEPT_CODE"));
                        parm.addData("COST_CENTER_CODE", table.getItemString(i, "EXE_DEPT_CODE"));//wanglong add 20140522
                        parm.addData("EXE_STATION_CODE", table.getItemString(i, "EXE_STATION_CODE"));
                        parm.addData("EXE_DR_CODE", table.getItemString(i, "EXE_DR_CODE"));
                        parm.addData("DR_CODE", table.getItemString(i, "DR_CODE"));
                        parm.addData("SCHD_CODE", table.getItemString(i, "SCHD_CODE"));
                        parm.addData("INV_CODE", table.getItemString(i, "INV_CODE"));
                        parm.addData("CLNCPATH_CODE", table.getItemString(i, "CLNCPATH_CODE"));
                        parm.addData("REXP_CODE", table.getItemString(i, "REXP_CODE"));
                        parm.addData("HEXP_CODE", setResult.getValue("CHARGE_HOSP_CODE", j));
                        parm.addData("CAT1_TYPE", setResult.getValue("CAT1_TYPE", j));//wanglong add 20140522
                        parm.addData("ORDER_CAT1_CODE", setResult.getValue("ORDER_CAT1_CODE", j));
                    }
                }
            }
        }
        return parm;
    }

    /**
     * 添加新添加的医嘱
     */
    public void saveOrders(TParm parm) {
        if (parm.getErrCode() < 0) {
            this.messageBox(parm.getErrText());
            return;
        }
        TParm checkParm = checkRtnQty(parm);// 校验退费数量是否大于已计费数量
        if (checkParm.getErrCode() < 0) {
            this.messageBox(checkParm.getErrText());
            return;
        }
        TParm caseNoSeqParm = this.getMaxCaseNoSeq(caseNo);
        if (caseNoSeqParm.getErrCode() < 0) {
            this.messageBox(caseNoSeqParm.getErrText());
            return;
        }
        caseNoSeq = caseNoSeqParm.getInt("CASE_NO_SEQ", 0) + 1;
        String[] sql = new String[]{getInsertIBS_ORDMSQL(caseNoSeq) };// 插入M表数据
        double totAmt = 0;
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            TParm tparm = new TParm();
            tparm.setData("CASE_NO", caseNo);
            tparm.setData("CASE_NO_SEQ", caseNoSeq);
            tparm.setData("SEQ_NO", seqNo++);
            tparm.setData("BILL_DATE", now);
            tparm.setData("ORDER_NO", orderNo);
            tparm.setData("ORDER_SEQ", orderSeq);
            tparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
            tparm.setData("ORDER_CHN_DESC", parm.getValue("ORDER_CHN_DESC", i));
            tparm.setData("CAT1_TYPE", parm.getValue("CAT1_TYPE", i));// wanglong add 20150522
            tparm.setData("ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE", i));
            tparm.setData("ORDERSET_GROUP_NO", parm.getValue("ORDERSET_GROUP_NO", i));
            tparm.setData("ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            tparm.setData("INV_CODE", parm.getValue("INV_CODE", i));
            tparm.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
            tparm.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
            tparm.setData("DR_CODE", parm.getValue("DR_CODE", i));
            tparm.setData("EXE_DEPT_CODE", parm.getValue("EXE_DEPT_CODE", i));
            tparm.setData("EXE_STATION_CODE", parm.getValue("EXE_STATION_CODE", i));
            tparm.setData("EXE_DR_CODE", parm.getValue("EXE_DR_CODE", i));
            tparm.setData("OWN_FLG", "Y");
            tparm.setData("BILL_FLG", "Y");
            tparm.setData("MEDI_QTY", parm.getData("MEDI_QTY", i));
            tparm.setData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
            tparm.setData("FREQ_CODE", parm.getValue("FREQ_CODE", i));
            tparm.setData("DOSE_CODE", parm.getValue("DOSE_CODE", i));
            tparm.setData("TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
            tparm.setData("DOSAGE_QTY", parm.getData("DOSAGE_QTY", i));
            tparm.setData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT", i));
            tparm.setData("OWN_PRICE", parm.getData("OWN_PRICE", i));
            tparm.setData("OWN_RATE", parm.getData("OWN_RATE", i));// wanglong add 20140522
            tparm.setData("TOT_AMT", parm.getData("TOT_AMT", i));
            totAmt += TypeTool.getDouble(parm.getDouble("TOT_AMT", i));
            tparm.setData("OWN_AMT",
                          StringTool.round(parm.getDouble("DOSAGE_QTY", i)
                                                   * parm.getDouble("OWN_PRICE", i), 2));
            tparm.setData("COST_CENTER_CODE", parm.getValue("COST_CENTER_CODE", i));// wanglong add
                                                                                    // 20140522
            tparm.setData("SCHD_CODE", parm.getValue("SCHD_CODE", i));
            tparm.setData("CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE", i));
            tparm.setData("REXP_CODE", parm.getValue("REXP_CODE", i));
            tparm.setData("HEXP_CODE", parm.getValue("HEXP_CODE", i));
            tparm.setData("BEGIN_DATE", now);
            tparm.setData("END_DATE", now);
            tparm.setData("OPT_USER", Operator.getID());
            tparm.setData("OPT_DATE", now);
            tparm.setData("OPT_TERM", Operator.getIP());
            sql = StringTool.copyArray(sql, new String[]{getInsertIBS_ORDDSQL(tparm) });
        }
        String oldSql = "SELECT TOTAL_AMT,CUR_AMT,DEPT_CODE FROM ADM_INP WHERE CASE_NO = '#'";
        oldSql = oldSql.replaceFirst("#", caseNo);
        TParm oldParm = new TParm(TJDODBTool.getInstance().select(oldSql));
        double oldTotalAmt = oldParm.getDouble("TOTAL_AMT", 0);
        double oldCurAmt = oldParm.getDouble("CUR_AMT", 0);
        double newTotalAmt = oldTotalAmt + totAmt;
        double newCurAmt = oldCurAmt - totAmt;
        String newSql = "UPDATE ADM_INP SET TOTAL_AMT = @, CUR_AMT = # WHERE CASE_NO = '&'";
        newSql = newSql.replaceFirst("@", StringTool.round(newTotalAmt, 2) + "");
        newSql = newSql.replaceFirst("#", StringTool.round(newCurAmt, 2) + "");
        newSql = newSql.replaceFirst("&", caseNo);
        sql = StringTool.copyArray(sql, new String[]{newSql });
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败 " + result.getErrText());
            return;
        }
        this.messageBox("P0001");
    }

    /**
     * 获得插入IBS_ORDM表SQL
     * 
     * @param caseNoSeq
     */
    public String getInsertIBS_ORDMSQL(int caseNoSeq) {
        String sql =
                " INSERT INTO IBS_ORDM (CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,MR_NO,DEPT_CODE,STATION_CODE,BED_NO,"
                        + "DATA_TYPE,BILL_NO,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,COST_CENTER_CODE) "
                        + " VALUES ('<CASE_NO>','<CASE_NO_SEQ>',SYSDATE,'<IPD_NO>','<MR_NO>','<DEPT_CODE>','<STATION_CODE>','<BED_NO>',"
                        + "'1','','<OPT_USER>',SYSDATE,'<OPT_TERM>','<REGION_CODE>','')";
        sql = sql.replaceFirst("<CASE_NO>", caseNo);
        sql = sql.replaceFirst("<CASE_NO_SEQ>", caseNoSeq + "");
        sql = sql.replaceFirst("<IPD_NO>", ipdNo);
        sql = sql.replaceFirst("<MR_NO>", mrNo);
        sql = sql.replaceFirst("<DEPT_CODE>", deptCode);
        sql = sql.replaceFirst("<STATION_CODE>", stationCode);
        sql = sql.replaceFirst("<BED_NO>", bedNo);
        sql = sql.replaceFirst("<OPT_USER>", Operator.getID());
        sql = sql.replaceFirst("<OPT_TERM>", Operator.getIP());
        sql = sql.replaceFirst("<REGION_CODE>", Operator.getRegion());
        return sql;
    }

    /**
     * 获得插入IBS_ORDD表SQL
     * 
     * @param parm
     */
    public String getInsertIBS_ORDDSQL(TParm parm) {
        String sql =
                "INSERT INTO IBS_ORDD(CASE_NO,CASE_NO_SEQ,SEQ_NO,OWN_FLG,BILL_FLG,CAT1_TYPE,OWN_RATE,"
                        + "ORDER_CHN_DESC,MEDI_QTY,MEDI_UNIT,FREQ_CODE,DOSE_CODE,ORDER_CAT1_CODE,"
                        + "TAKE_DAYS,DOSAGE_QTY,DOSAGE_UNIT,OWN_PRICE,TOT_AMT,COST_CENTER_CODE,"
                        + "EXE_DEPT_CODE,EXE_STATION_CODE,EXE_DR_CODE,DEPT_CODE,STATION_CODE,REXP_CODE,"
                        + "DR_CODE,SCHD_CODE,BILL_DATE,INV_CODE,ORDERSET_CODE,ORDERSET_GROUP_NO,OWN_AMT,"
                        + "CLNCPATH_CODE,ORDER_SEQ,ORDER_CODE,ORDER_NO,BEGIN_DATE,HEXP_CODE,END_DATE,"
                        + "OPT_USER,OPT_DATE,OPT_TERM)"
                        + "VALUES('<CASE_NO>',<CASE_NO_SEQ>,<SEQ_NO>,'<OWN_FLG>','<BILL_FLG>','<CAT1_TYPE>',<OWN_RATE>,"
                        + "'<ORDER_CHN_DESC>',<MEDI_QTY>,'<MEDI_UNIT>','<FREQ_CODE>','<DOSE_CODE>','<ORDER_CAT1_CODE>',"
                        + "<TAKE_DAYS>,<DOSAGE_QTY>,'<DOSAGE_UNIT>',<OWN_PRICE>,<TOT_AMT>,'<COST_CENTER_CODE>',"
                        + "'<EXE_DEPT_CODE>','<EXE_STATION_CODE>','<EXE_DR_CODE>','<DEPT_CODE>','<STATION_CODE>','<REXP_CODE>',"
                        + "'<DR_CODE>','<SCHD_CODE>',<BILL_DATE>,'<INV_CODE>','<ORDERSET_CODE>','<ORDERSET_GROUP_NO>',<OWN_AMT>,"
                        + "'<CLNCPATH_CODE>','<ORDER_SEQ>','<ORDER_CODE>','<ORDER_NO>',<BEGIN_DATE>,'<HEXP_CODE>',<END_DATE>,"
                        + "'<OPT_USER>',<OPT_DATE>,'<OPT_TERM>')";
        sql = sql.replaceFirst("<CASE_NO>", parm.getValue("CASE_NO"));
        sql = sql.replaceFirst("<CASE_NO_SEQ>", parm.getInt("CASE_NO_SEQ") + "");
        sql = sql.replaceFirst("<SEQ_NO>", parm.getInt("SEQ_NO") + "");
        sql = sql.replaceFirst("<OWN_FLG>", parm.getValue("OWN_FLG"));
        sql = sql.replaceFirst("<BILL_FLG>", parm.getValue("BILL_FLG"));
        sql = sql.replaceFirst("<CAT1_TYPE>", parm.getValue("CAT1_TYPE"));
        sql = sql.replaceFirst("<OWN_RATE>", parm.getDouble("OWN_RATE") + "");
        sql = sql.replaceFirst("<ORDER_CHN_DESC>", parm.getValue("ORDER_CHN_DESC"));
        sql = sql.replaceFirst("<MEDI_QTY>", parm.getDouble("MEDI_QTY") + "");
        sql = sql.replaceFirst("<MEDI_UNIT>", parm.getValue("MEDI_UNIT"));
        sql = sql.replaceFirst("<FREQ_CODE>", parm.getValue("FREQ_CODE"));
        sql = sql.replaceFirst("<DOSE_CODE>", parm.getValue("DOSE_CODE"));
        sql = sql.replaceFirst("<ORDER_CAT1_CODE>", parm.getValue("ORDER_CAT1_CODE"));
        sql = sql.replaceFirst("<TAKE_DAYS>", parm.getInt("TAKE_DAYS") + "");
        sql = sql.replaceFirst("<DOSAGE_QTY>", parm.getDouble("DOSAGE_QTY") + "");
        sql = sql.replaceFirst("<DOSAGE_UNIT>", parm.getValue("DOSAGE_UNIT"));
        sql = sql.replaceFirst("<OWN_PRICE>", parm.getDouble("OWN_PRICE") + "");
        sql = sql.replaceFirst("<TOT_AMT>", parm.getDouble("TOT_AMT") + "");
        sql = sql.replaceFirst("<COST_CENTER_CODE>", parm.getValue("COST_CENTER_CODE"));
        sql = sql.replaceFirst("<EXE_DEPT_CODE>", parm.getValue("EXE_DEPT_CODE"));
        sql = sql.replaceFirst("<EXE_STATION_CODE>", parm.getValue("EXE_STATION_CODE"));
        sql = sql.replaceFirst("<EXE_DR_CODE>", parm.getValue("EXE_DR_CODE"));
        sql = sql.replaceFirst("<DEPT_CODE>", parm.getValue("DEPT_CODE"));
        sql = sql.replaceFirst("<STATION_CODE>", parm.getValue("STATION_CODE"));
        sql = sql.replaceFirst("<REXP_CODE>", parm.getValue("REXP_CODE"));
        sql = sql.replaceFirst("<DR_CODE>", parm.getValue("DR_CODE"));
        sql = sql.replaceFirst("<SCHD_CODE>", parm.getValue("SCHD_CODE"));
        sql = sql.replaceFirst("<BILL_DATE>", "SYSDATE");
        sql = sql.replaceFirst("<INV_CODE>", parm.getValue("INV_CODE"));
        sql = sql.replaceFirst("<ORDERSET_CODE>", parm.getValue("ORDERSET_CODE"));
        sql = sql.replaceFirst("<ORDERSET_GROUP_NO>", parm.getValue("ORDERSET_GROUP_NO"));
        sql = sql.replaceFirst("<OWN_AMT>", parm.getDouble("OWN_AMT") + "");
        sql = sql.replaceFirst("<CLNCPATH_CODE>", parm.getValue("CLNCPATH_CODE"));
        sql = sql.replaceFirst("<ORDER_SEQ>", parm.getValue("ORDER_SEQ"));
        sql = sql.replaceFirst("<ORDER_CODE>", parm.getValue("ORDER_CODE"));
        sql = sql.replaceFirst("<ORDER_NO>", parm.getValue("ORDER_NO"));
        sql = sql.replaceFirst("<BEGIN_DATE>", "SYSDATE");
        sql = sql.replaceFirst("<HEXP_CODE>", parm.getValue("HEXP_CODE"));
        sql = sql.replaceFirst("<END_DATE>", "SYSDATE");
        sql = sql.replaceFirst("<OPT_USER>", parm.getValue("OPT_USER"));
        sql = sql.replaceFirst("<OPT_DATE>", "SYSDATE");
        sql = sql.replaceFirst("<OPT_TERM>", parm.getValue("OPT_TERM"));
        return sql;
    }

    /**
     * 校验退费数量是否大于已计费数量
     * 
     * @param parm
     * @return
     */
    public TParm checkRtnQty(TParm parm) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            // 增加退费数量管控
            String orderCode = parm.getValue("ORDER_CODE", i);
            double dosageQty = parm.getDouble("DOSAGE_QTY", i);
            if (dosageQty < 0) {
                String selQtySql =
                        " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
                                + "   FROM IBS_ORDD                  "
                                + "  WHERE ORDER_CODE = '@'          "
                                + "    AND CASE_NO = '#'             "
                                + " GROUP BY ORDER_CODE              ";
                selQtySql = selQtySql.replaceFirst("@", orderCode);
                selQtySql = selQtySql.replaceFirst("#", caseNo);
                // System.out.println("selQtyParmsql" + orderCodeSql);
                TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(selQtySql));
                double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
                if (Math.abs(dosageQty) > dosageQtyTot) {
                    result.setErr(-1, "退费数量超过合计数量,不能保存");
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 查询联合医嘱的细项
     * 
     * @param orderSetCode
     * @return
     */
    public TParm getOrderDetails(String orderSetCode) {
        TParm result = new TParm();
        String sql =
                "SELECT B.*, A.ORDERSET_CODE, A.DOSAGE_QTY, A.HIDE_FLG, (A.DOSAGE_QTY * B.OWN_PRICE) AS TOT_AMT,"
                        + "ROWNUM + 1 AS ORDERSET_GROUP_NO, C.DOSE_CODE "// wanglong modify 20140522
                        + " FROM SYS_ORDERSETDETAIL A,SYS_FEE B, PHA_BASE C "
                        + "WHERE A.ORDERSET_CODE = '#' AND A.ORDER_CODE = B.ORDER_CODE AND B.ORDER_CODE = C.ORDER_CODE(+)";
        sql = sql.replaceFirst("#", orderSetCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 查询医嘱信息
     * 
     * @param orderCode
     * @return
     */
    public TParm getOrderInfo(String orderCode) {// wanglong add 20140522
        TParm result = new TParm();
        String sql = "SELECT A.*,B.DOSE_CODE FROM SYS_FEE A, PHA_BASE B WHERE A.ORDER_CODE = '#' AND A.ORDER_CODE = B.ORDER_CODE(+) ";
        sql = sql.replaceFirst("#", orderCode);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 查询最大账务序号
     * 
     * @param caseNo
     *            String
     * @return TParm
     */
    public TParm getMaxCaseNoSeq(String caseNo) {
        String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '#' ";
        sql = sql.replaceFirst("#", caseNo);
        return new TParm(TJDODBTool.getInstance().select(sql));
    }

    /**
     * 查询
     * @param type
     * @return
     */
    public TParm getMaxOrderGroupNo(String caseNo) {
        String sql = "SELECT MAX(ORDERSET_GROUP_NO) ORDERSET_GROUP_NO FROM ODI_ORDER WHERE CASE_NO='#'";
        sql = sql.replaceFirst("#", caseNo);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        return parm;
    }
    
    /**
     * 获得列名
     * 
     * @param columnIndex
     * @return
     */
    public String getColumnName(int columnIndex) {
        return table.getDataStoreColumnName(table.getColumnModel().getColumnIndex(columnIndex));
    }
}
