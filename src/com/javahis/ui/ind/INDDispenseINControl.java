package com.javahis.ui.ind;

import java.sql.Timestamp;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

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
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TTextFormat;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: ������
 * </p>
 *
 * <p>
 * Description: ������
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
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class INDDispenseINControl
    extends TControl {

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    // ������ѡ����
    private int row_m;

    // ϸ����ѡ����
    private int row_d;

    // ϸ�����
    private int seq;

    // ���ؽ����
    private TParm resultParm;

    // ҳ���Ϸ������б�
    private String[] pageItem = {
        "REQTYPE_CODE", "REQUEST_NO", "APP_ORG_CODE", "TO_ORG_CODE",
        "REASON_CHN_DESC", "REQUEST_DATE", "DISPENSE_NO", "DESCRIPTION",
        "URGENT_FLG"};

    // ��������
    private String request_type;

    // ���뵥��
    private String request_no;

    // ʹ�õ�λ
    private String u_type;

    // ���ⲿ��
    private String out_org_code;

    // ��ⲿ��
    private String in_org_code;

    // �Ƿ����
    private boolean out_flg;

    // �Ƿ����
    private boolean in_flg;

    // ��ⵥ��
    private String dispense_no;

    // ȫԺҩ�ⲿ����ҵ����
    private boolean request_all_flg = true;

    public INDDispenseINControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                              "onQueryInM", onQueryParm());
        // ȫԺҩ�ⲿ����ҵ����
        if (!request_all_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getOperatorDept(Operator.getID())));
            String dept_code = "";
            //System.out.println("parm---"+parm);
            //System.out.println("result---"+result);
            for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                boolean flg = true;
                for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                    dept_code = parm.getValue("DEPT_CODE", j);
                    if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                    else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                             "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                }
                if (flg) {
                    result.removeRow(i);
                }
            }
        }
        //System.out.println("result==="+result);
        if (result == null || result.getCount("REQUEST_NO") <= 0) {
            this.messageBox("�޲�ѯ���");
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        // ��ջ�������
        String clearString =
            "REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;DISPENSE_NO;"
            + "REQUEST_DATE;TO_ORG_CODE;REASON_CHN_DESC;START_DATE;END_DATE;"
            + "DESCRIPTION;URGENT_FLG;SELECT_ALL;"
            + "SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
        clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("WAREHOUSING_DATE", date);
        // ��ʼ��ҳ��״̬������
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        row_m = -1;
        row_d = -1;

        getTextField("REQUEST_NO").setEnabled(true);
        getTextFormat("APP_ORG_CODE").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("stop")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        resultParm = new TParm();
        seq = 1;
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            /** ���±���(��ⵥ���ϸ��) */
            // 2.���������ж�
            String batchvalid = getBatchValid(request_type);
            // 3.��ⲿ�ſ���Ƿ���춯
            if (!getOrgBatchFlg(in_org_code)) {
                return;
            }
            if (!"THI".equals(request_type)) {
                // �����ҵ
                getDispenseOutIn(out_org_code, in_org_code, batchvalid,
                                 out_flg, in_flg);
            }
            else {
                // ���������ҵ
                getDispenseOtherIn(in_org_code, in_flg);
            }
        }
        else {
            // ����������
            this.messageBox("��������⣬�����޸�");
        }
    }

    /**
     * ��ֹ���ݷ���
     */
    public void onStop() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("û����ֹ����");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ���ֹ����", 2) == 0) {
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                "action.ind.INDRequestAction", "onUpdateFlg", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("��ֹʧ��");
                return;
            }
            this.messageBox("��ֹ�ɹ�");
            this.onClear();
        }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // ������ѡ���������Ϸ�
            getTableSelectValue(table_m, row_m, pageItem);
            // ����ʱ��
            if (table_m.getItemData(row_m, "DISPENSE_DATE") != null) {
                this.setValue("DISPENSE_DATE", table_m.getItemData(row_m,
                    "DISPENSE_DATE"));
            }
            // �趨ҳ��״̬
            getComboBox("REQTYPE_CODE").setEnabled(false);
            getTextFormat("APP_ORG_CODE").setEnabled(false);
            getTextField("REQUEST_NO").setEnabled(false);
            // �������
            request_type = getValueString("REQTYPE_CODE");
            // ���뵥��
            request_no = getValueString("REQUEST_NO");
            // ��ⵥ��
            dispense_no = getValueString("DISPENSE_NO");
            // ��ϸ��Ϣ
            getTableDInfo(request_no);
            table_d.setSelectionMode(0);
            row_d = -1;

        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            table_m.setSelectionMode(0);
            row_m = -1;
            // ȡ��SYS_FEE��Ϣ��������״̬����
            /*
             String order_code = table_d.getParmValue().getValue("ORDER_CODE",
                table_d.getSelectedRow());
                         if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
                         }
                         else {
                callFunction("UI|setSysStatus", "");
                         }
             */
        }
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        table_d.acceptText();
        if (table_d.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if (!CheckDataD()) {
                    getCheckBox("SELECT_ALL").setSelected(false);
                    return;
                }
                table_d.setItem(i, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                table_d.setItem(i, "SELECT_FLG", false);
            }
            setValue("SUM_RETAIL_PRICE", 0);
            setValue("SUM_VERIFYIN_PRICE", 0);
            setValue("PRICE_DIFFERENCE", 0);
        }
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 6) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("�������������С�ڻ����0");
                return true;
            }
//            double stock_qty = table_d.getItemDouble(row_d, "STOCK_QTY");
//            if (qty > stock_qty) {
//                this.messageBox("�������������ܴ��ڿ������");
//                return true;
//            }
            double dep_qty = table_d.getItemDouble(row_d, "QTY");
            if (qty > dep_qty) {
                this.messageBox("������������ܴ�����������");
                return true;
            }
            double amt1 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "STOCK_PRICE"), 2);
            double amt2 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "RETAIL_PRICE"), 2);
            table_d.setItem(row_d, "STOCK_ATM", amt1);
            table_d.setItem(row_d, "RETAIL_ATM", amt2);
            table_d.setItem(row_d, "DIFF_ATM", amt2 - amt1);
            return false;
        }
        if (column == 8) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return true;
            }
            double out_qty = table_d.getItemDouble(row_d, "OUT_QTY");
            table_d.setItem(row_d, "STOCK_ATM", StringTool.round(price
                * out_qty, 2));
            double atm = table_d.getItemDouble(row_d, "RETAIL_ATM");
            table_d.setItem(row_d, "DIFF_ATM", StringTool.round(atm - price
                * out_qty, 2));
            return false;
        }
        return true;
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table_d.acceptText();
        // ���ѡ�е���
        int column = table_d.getSelectedColumn();
        if (column == 0) {
            if ("Y".equals(table_d.getItemString(row_d, column))) {
                table_d.setItem(row_d, "SELECT_FLG", false);
            }
            else {
                table_d.setItem(row_d, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
    }

    /**
     * ���״̬�ı��¼�
     */
    public void onChangeSelectAction() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_A").setSelected(true);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else {
            onClear();
            getRadioButton("UPDATE_FLG_B").setSelected(false);
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }

        // �������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("WAREHOUSING_DATE", date);
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 1;
        resultParm = new TParm();
        ( (TMenuItem) getComponent("stop")).setEnabled(false);
    }

    /**
     * ��ѯ��������
     *
     * @return
     */
    private TParm onQueryParm() {
        // ���������
        TParm parm = new TParm();
        // ���뵥��
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        // ���벿��
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        // �������
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        // ��ⵥ��
        if (!"".equals(getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        // ��ѯ����
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return parm;
        }
        // ���״̬
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            // δ���
            parm.setData("STATUS", "B");
        }
        else {
            // ���
            parm.setData("STATUS", "A");
        }
        return parm;
    }

    /**
     * ����Ƿ���춯״̬�ж�
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // ����Ƿ���춯״̬�ж�
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
            return false;
        }
        return true;
    }

    /**
     * ���������ж�: 1,��ָ�����ź�Ч��;2,ָ�����ź�Ч��
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

    /**
     * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        TParm result = new TParm();
        if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
            result = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getInDispenseDInfo(dispense_no)));
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
        }
        else {
            result = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getOutRequestDInfo(req_no)));
//            System.out.println("sql:"+INDSQL.getOutRequestDInfo(req_no));
            if (this.getRadioButton("UPDATE_FLG_B").isSelected()) {
                ( (TMenuItem) getComponent("stop")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("stop")).setEnabled(false);
            }
        }
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("û�������ϸ");
            return;
        }
        table_d.setSelectionMode(0);
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = false;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }
        else if ("THI".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = false;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        // �������
        resultParm = result;
        // ���TABLE_D
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_d.setParmValue(result);

        setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
        setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
        setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
            - getSumRegMoney(), 2));
    }

    /**
     * ���TABLE_D
     *
     * @param table
     * @param parm
     * @param args
     */
    private TParm setTableDValue(TParm result) {
        TParm parm = new TParm();
        double qty = 0;
        double actual_qty = 0;
        double stock_price = 0;
        double retail_price = 0;
        double atm = 0;
        String order_code = "";
        for (int i = 0; i < result.getCount(); i++) {
            parm.setData("SELECT_FLG", i, true);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            // �����(��ⲿ��)
            double stock_qty = INDTool.getInstance().getStockQTY(in_org_code,
                order_code);
            if (stock_qty < 0) {
                stock_qty = 0;
            }
            if ("0".equals(u_type)) {
                parm.setData("STOCK_QTY", i,
                             stock_qty / result.getDouble("DOSAGE_QTY", i));
            }
            else {
                parm.setData("STOCK_QTY", i, stock_qty);
            }
            if ("THI".equals(request_type)) {
                parm.setData("OUT_QTY", i, qty);
            }
            else {
                parm.setData("OUT_QTY", i, result.getDouble("OUT_QTY", i));
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
            // ʹ�õ�λ
            if ("0".equals(u_type)) {
                // ��浥λ
                stock_price = result.getDouble("STOCK_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
                retail_price = result.getDouble("RETAIL_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
            }
            else {
                // ��ҩ��λ
                stock_price = result.getDouble("STOCK_PRICE", i);
                retail_price = result.getDouble("RETAIL_PRICE", i);
            }
            parm.setData("STOCK_PRICE", i, stock_price);
            atm = StringTool.round(stock_price * qty, 2);
            parm.setData("STOCK_ATM", i, atm);
            parm.setData("RETAIL_PRICE", i, retail_price);
            atm = StringTool.round(retail_price * qty, 2);
            parm.setData("RETAIL_ATM", i, atm);
            atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
            parm.setData("DIFF_ATM", i, atm);
            parm.setData("BATCH_NO", i, result.getValue("BATCH_NO", i));
            //luhai 2012-1-13 add batch_seq 
            parm.setData("BATCH_SEQ", i, result.getValue("BATCH_SEQ", i));
            parm.setData("VALID_DATE", i, result.getTimestamp("VALID_DATE", i));
            parm.setData("PHA_TYPE", i, result.getValue("PHA_TYPE", i));
            parm.setData("ORDER_CODE", i, order_code);
        }
        return parm;
    }

    /**
     * ���ѡ���������Ϸ�
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("REQUEST_NO"))) {
            this.messageBox("���뵥�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        // �ж�ϸ���Ƿ��б�ѡ����
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // �ж�������ȷ��
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("�������������С�ڻ����0");
                return false;
            }
            double price = table_d.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * ���������Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg) {
        // ҩ�������Ϣ
        TParm sysParm = getSysParm();
        // �Ƿ��д����۸�
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        // ��ⵥ��
        dispense_no = "";
        if ("".equals(getValueString("DISPENSE_NO"))) {
            dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }
        else {
            dispense_no = getValueString("DISPENSE_NO");
        }
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", getValue("WAREHOUSING_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", getValue("WAREHOUSING_DATE"));
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        parmM.setData("REASON_CHN_DESC", getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", u_type);
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //luhai 2012-01-13 add region_code begin
        parmM.setData("REGION_CODE", Operator.getRegion());
        //luhai 2012-01-13 add region_code end
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        return parm;
    }

    /**
     * �����ϸ��Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmD.setData("DISPENSE_NO", count, dispense_no);
            if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
                parmD.setData("SEQ_NO", count, resultParm.getInt("SEQ_NO", i));
            }
            else {
                parmD.setData("SEQ_NO", count, seq + count);
            }
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            parmD.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count, table_d.getItemDouble(i,
                "RETAIL_PRICE"));
            parmD.setData("STOCK_PRICE", count, table_d.getItemDouble(i,
                "STOCK_PRICE"));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getItemString(i,
                "PHA_TYPE"));
            parmD.setData("BATCH_SEQ", count, table_d.getParmValue().getValue(
                "BATCH_SEQ",i));
            parmD.setData("BATCH_NO", count,
                          table_d.getItemString(i, "BATCH_NO"));
            parmD.setData("VALID_DATE", count, table_d.getItemData(i,
                "VALID_DATE"));
            parmD.setData("DOSAGE_QTY", count, resultParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("STOCK_QTY", count, resultParm.getDouble(
                "STOCK_QTY", i));
            parmD.setData("REGION_CODE", count, Operator.getRegion());
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }

    /**
     * �ж��Ƿ�����в�ҩ
     *
     * @return
     */
    private boolean checkPhaType() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if ("C".equals(table_d.getItemString(i, "PHA_TYPE"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * ��ҩ�Զ�ά���շѱ�׼
     *
     * @param parm
     * @param gown_costrate
     * @param gnhi_costrate
     * @param gov_costrate
     * @return
     */
    private TParm getGrpriceC(TParm parm, double gown_costrate,
                              double gnhi_costrate, double gov_costrate,
                              boolean flg) {
        String order_code = "";
        TParm parmC = new TParm();
        int count = 0;
        int index = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            order_code = resultParm.getValue("ORDER_CODE", i);
            TParm result = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDStock(in_org_code, order_code, Operator.getRegion())));
            double new_gown_price = result.getDouble("VERIFYIN_PRICE", 0)
                * gown_costrate;
            double new_gnhi_price = result.getDouble("VERIFYIN_PRICE", 0)
                * gnhi_costrate;
            double new_gov_price = result.getDouble("VERIFYIN_PRICE", 0)
                * gov_costrate;
            // ����ҽ�������ѯ�ԷѼۡ�ҽ������������߼�
            result = SYSFeeTool.getInstance().getPrice(order_code);
            double old_gown_price = result.getDouble("OWN_PRICE", 0);
            double old_gnhi_price = result.getDouble("NHI_PRICE", 0);
            double old_gov_price = result.getDouble("GOV_PRICE", 0);

            if (new_gown_price != old_gown_price
                || new_gnhi_price != old_gnhi_price
                || new_gov_price != old_gov_price) {
                parmC.setData("ORDER_CODE", index, order_code);
                parmC.setData("OWN_PRICE", index, new_gown_price);
                parmC.setData("NHI_PRICE", index, new_gnhi_price);
                parmC.setData("GOV_PRICE", index, new_gov_price);
                parmC.setData("OLD_OWN_PRICE", index, old_gown_price);
                if (flg) {
                    parmC.setData("RETAIL_PRICE", index, new_gown_price);
                    parmC.setData("ORG_CODE", index, in_org_code);
                }
                index++;
            }
            count++;
        }
        if (parmC.getCount("ORDER_CODE") > 0) {
            if (this.messageBox("��ʾ", "ȷ���Ƿ����ҩƷ���ۼ�,ҽ������������߼�", 2) == 0) {
                // ִ�в�ҩ�Զ�ά��
                parm.setData("PARM_C", parmC.getData());
            }
        }
        return parm;
    }

    /**
     * ���������뵥����
     *
     * @param parm
     * @return
     */
    private TParm getRequestParm(TParm parm) {
        TParm parmR = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        double out_qty = 0;
        double req_qty = 0;
        String update_flg = "1";
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmR.setData("REQUEST_NO", count, request_no);
            parmR.setData("SEQ_NO", count, resultParm.getInt("REQUEST_SEQ", i));
            out_qty = table_d.getItemDouble(i, "OUT_QTY");
            req_qty = table_d.getItemDouble(i, "QTY");
            parmR.setData("ACTUAL_QTY", count, out_qty);
            // �ж����뵥״̬
            if (out_qty == req_qty) {
                update_flg = "3";
            }
            parmR.setData("UPDATE_FLG", count, update_flg);
            // OPT
            parmR.setData("OPT_USER", count, Operator.getID());
            parmR.setData("OPT_DATE", count, date);
            parmR.setData("OPT_TERM", count, Operator.getIP());
        }
        parm.setData("PARM_R", parmR.getData());
        return parm;
    }

    /**
     * �����ҵ
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg) {
        if (!checkSelectRow()) {
            return;
        }
        TParm parm = new TParm();
        // ������Ϣ(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "3");
        // ϸ����Ϣ(OUT_D)
        if (!CheckDataD()) {
            return;
        }
        parm = getDispenseDParm(parm);
        /*
                 // �ж��Ƿ�����в�ҩ
                 if (checkPhaType()) {
            TParm result = IndSysParmTool.getInstance().onQuery();
            // �ж�ҩƷ�Ƿ�Ҫ�Զ�ά���շѱ�׼
            if ("Y".equals(result.getValue("GPRICE_FLG"))) {
                // ��ҩ�ɱ��ӳɱ���(���ۼ�)
                double gown_costrate = result.getDouble("GOWN_COSTRATE");
                // ��ҩ�ɱ��ӳɱ���(ҽ����)
                double gnhi_costrate = result.getDouble("GNHI_COSTRATE");
                // ��ҩ�ɱ��ӳɱ���(������߼�)
                double gov_costrate = result.getDouble("GOV_COSTRATE");
                boolean flg = false;
                if ("Y".equals(result.getValue("UPDATE_RETAIL_FLG"))) {
                    flg = true;
                }
                // ��ҩ�Զ�ά���շѱ�׼����������(PARM_C)
                parm = getGrpriceC(parm, gown_costrate, gnhi_costrate,
                                   gov_costrate, flg);
            }
                 }
         */

        if (!"".equals(in_org_code)) {
            // ��ⲿ��(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // �Ƿ����(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // ִ����������
        TParm result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertIn", parm);

        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        // ��������ҩ�Զ�ά���շ�
        if ("DEP".equals(this.getValueString("REQTYPE_CODE"))) {
            // �ж�ҩƷ����ע��
            boolean pha_price_flg = false;
            TParm parmFlg = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDSysParm()));
            if (parmFlg.getCount() > 0) {
                pha_price_flg = parmFlg.getBoolean("PHA_PRICE_FLG", 0);
            }
            updateGrpricePrice(this.getValueString("DISPENSE_NO"),
                               pha_price_flg);
        }
        onClear();
    }

    /**
     * ���������ҵ
     * @param in_org_code String
     * @param flg boolean
     */
    private void getDispenseOtherIn(String in_org_code, boolean in_flg) {
        if (!checkSelectRow()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", in_org_code);
        // ʹ�õ�λ
        parm.setData("UNIT_TYPE", u_type);
        // ���뵥����
        parm.setData("REQTYPE_CODE", request_type);
        // ������Ϣ(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "3");
        // ϸ����Ϣ(OUT_D)
        if (!CheckDataD()) {
            return;
        }
        parm = getDispenseDParm(parm);
//        // �ж��Ƿ�����в�ҩ
//        if (checkPhaType()) {
//            TParm result = IndSysParmTool.getInstance().onQuery();
//            // �ж�ҩƷ�Ƿ�Ҫ�Զ�ά���շѱ�׼
//            if ("Y".equals(result.getValue("GPRICE_FLG"))) {
//                // ��ҩ�ɱ��ӳɱ���(���ۼ�)
//                double gown_costrate = result.getDouble("GOWN_COSTRATE");
//                // ��ҩ�ɱ��ӳɱ���(ҽ����)
//                double gnhi_costrate = result.getDouble("GNHI_COSTRATE");
//                // ��ҩ�ɱ��ӳɱ���(������߼�)
//                double gov_costrate = result.getDouble("GOV_COSTRATE");
//                boolean flg = false;
//                if ("Y".equals(result.getValue("UPDATE_RETAIL_FLG"))) {
//                    flg = true;
//                }
//                // ��ҩ�Զ�ά���շѱ�׼����������(PARM_C)
//                parm = getGrpriceC(parm, gown_costrate, gnhi_costrate,
//                                   gov_costrate, flg);
//            }
//        }

        if (!"".equals(in_org_code)) {
            // ��ⲿ��(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // �Ƿ����(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // ִ����������
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertOtherIn", parm);
        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * �ж�ϸ���Ƿ�ѡ��
     *
     * @return
     */
    private boolean checkSelectRow() {
        // �ж�ϸ���Ƿ�ѡ��
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("û��ѡ��");
            return false;
        }
        return true;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "RETAIL_ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ����ɱ��ܽ��
     *
     * @return
     */
    private double getSumRegMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "STOCK_ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }

    /**
     * ��������ҩ�Զ�ά���շ�(ҩƷ���۳���)
     * @param verifyin_no String
     * @param pha_price_flg boolean
     */
    private void updateGrpricePrice(String dispense_no, boolean pha_price_flg){
        String sql =
            " SELECT A.ORDER_CODE, A.RETAIL_PRICE / D.STOCK_QTY / D.DOSAGE_QTY AS RETAIL_PRICE, B.OWN_PRICE , B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE "
            + " FROM IND_DISPENSED A, SYS_FEE B, PHA_BASE C, PHA_TRANSUNIT D "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND B.ORDER_CODE = C.ORDER_CODE "
            + " AND A.ORDER_CODE = D.ORDER_CODE "
            + " AND B.ORDER_CODE = D.ORDER_CODE "
            + " AND C.ORDER_CODE = D.ORDER_CODE "
            + " AND A.RETAIL_PRICE <> B.OWN_PRICE * D.STOCK_QTY * D.DOSAGE_QTY "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' ";
        // ҩƷ����ע��
        if (!pha_price_flg) {
            sql += " AND C.PHA_TYPE = 'G' ";
        }

        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
            return;
        }
        //��ҩ�����ۼ�
        double old_own_price = 0;
        double new_own_price = 0;
        String order_code = "";
        String order_desc = "";
        String batch_no = "";
        String valid_date = "";
        String message = "";
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            old_own_price = parm.getDouble("OWN_PRICE", i);
            new_own_price = parm.getDouble("RETAIL_PRICE", i);
            order_code = parm.getValue("ORDER_CODE", i);
            order_desc = parm.getValue("ORDER_DESC", i);
            batch_no = parm.getValue("BATCH_NO", i);
            valid_date = StringTool.getString(parm.getTimestamp("VALID_DATE", i),
                                              "yyyyMMdd");
            message = message + order_desc + "(" + order_code + ")��ԭ���ۼۣ�" +
                old_own_price + "�������ۼ�:" + new_own_price + "\n";
            result.addData("ORDER_CODE", order_code);
            result.addData("ORDER_DESC", order_desc);
            result.addData("OWN_PRICE", new_own_price);
            result.addData("OLD_OWN_PRICE", old_own_price);
            result.addData("BATCH_NO", batch_no);
            result.addData("VALID_DATE", valid_date);
        }
 /*       if (result.getCount("ORDER_CODE") > 0) {
            if (this.messageBox("��ʾ", message + "ȷ���Ƿ����ҩƷ���ۼ�?", 2) == 0) {
                // ִ�в�ҩ�Զ�ά��
                TParm resultParm = new TParm();
                resultParm.setData("ORG_CODE",
                                   this.getValueString("APP_ORG_CODE"));
                resultParm.setData("DATA", result.getData());
                resultParm.setData("OPT_USER", Operator.getID());
                resultParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
                resultParm.setData("OPT_TERM", Operator.getIP());
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDDispenseAction", "onUpdateGrpricePrice",
                    resultParm);
                // �����ж�
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("����ʧ��");
                    return;
                }
                this.messageBox("���³ɹ�");
            }
        }*/
    }

}
