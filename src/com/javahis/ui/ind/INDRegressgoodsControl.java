package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndStockDTool;
import jdo.ind.IndVerifyinMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
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
import com.javahis.manager.IndRegressgoodsObserver;
import com.javahis.util.StringUtil;

import jdo.ind.IndVerifyinDTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: �˻�����Control
 * </p>
 *
 * <p>
 * Description: �˻�����Control
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
 * @author zhangy 2009.05.21
 * @version 1.0
 */
public class INDRegressgoodsControl
    extends TControl {
    /**
     * �������
     */
    private String action = "insert";

    // ϸ�����
    private int seq;

    private TParm resultParm;

    private Map map;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    // ��ӡ�����ʽ
    java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
    java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
    java.text.DecimalFormat df3 = new java.text.DecimalFormat(
        "##########0.000");
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");


    public INDRegressgoodsControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ���������
        TParm parm = new TParm();
        if (!"".equals(getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        }
        else {
            // û��ȫ������Ȩ��
            if (!dept_flg) {
                this.messageBox("��ѡ���ѯ����");
                return;
            }
        }
        if (!"".equals(getValueString("SUP_CODE"))) {
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        if (!"".equals(getValueString("REGRESSGOODS_NO"))) {
            parm.setData("REGRESSGOODS_NO", getValueString("REGRESSGOODS_NO"));
        }
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return;
        }
        // ���ؽ����
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction(
            "action.ind.INDRegressgoodsAction", "onQueryM", parm);

        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // �����
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if ("".equals(result.getValue("CHECK_USER", i))) {
                    result.removeRow(i);
                    result.setCount(result.getCount() - 1);
                }
            }
        }
        else {
            // δ���
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if (!"".equals(result.getValue("CHECK_USER", i))) {
                    result.removeRow(i);
                    result.setCount(result.getCount() - 1);
                }
            }
        }
        //this.messageBox_(result);
        if (result == null || result.getCount("REGRESSGOODS_NO") == 0) {
            getTable("TABLE_M").removeRowAll();
            getTable("TABLE_D").removeRowAll();
            this.messageBox("�޲�ѯ��Ϣ");
            return;
        }

        // �����TABLE_M
        TTable table_M = getTable("TABLE_M");
        table_M.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        getCheckBox("CHECK_FLG").setEnabled(false);
        // ��ջ�������
        String clearString =
            "REGRESSGOODS_DATE;ORG_CODE;REGRESSGOODS_NO;"
            + "REASON_CHN_DESC;DESCRIPTION;CHECK_FLG;START_DATE;END_DATE"
            + "SELECT_ALL;SUM_RETAIL_ATM;SUM_REG_ATM;PRICE_DIFFERENCE";
        clearValue(clearString);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(true);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        getComboBox("ORG_CODE").setEnabled(true);
        getTextFormat("SUP_CODE").setValue("");
        getTextFormat("SUP_CODE").setText("");
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextField("REGRESSGOODS_NO").setEnabled(true);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REGRESSGOODS_DATE", date);
        // ��ʼ��ȫ�ֲ���
        action = "insert";
        resultParm = null;
        // ��ʼ��ҳ��״̬������
        TTable table_M = getTable("TABLE_M");
        table_M.setSelectionMode(0);
        table_M.removeRowAll();
        TTable table_D = getTable("TABLE_D");
        table_D.setSelectionMode(0);
        table_D.removeRowAll();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        map = new HashMap();
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        Timestamp date = SystemTool.getInstance().getDate();
        // �����˻���Ϣ
        if ("insert".equals(action)) {
            //System.out.println("11111111111111");
            if (!CheckDataD()) {
                return;
            }
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
            parm.setData("REGRESSGOODS_DATE", getValue("REGRESSGOODS_DATE"));
            parm.setData("REGRESSGOODS_USER", Operator.getID());
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            //zhangyong20110517
            parm.setData("REGION_CODE", Operator.getRegion());
            // ���
            boolean check_flg = false;
            if ("Y".equals(getValueString("CHECK_FLG"))) {
                // �����Ա
                parm.setData("CHECK_USER", Operator.getID());
                // ���ʱ��
                parm.setData("CHECK_DATE", date);
                check_flg = true;
            }
            else {
                parm.setData("CHECK_USER", "");
                parm.setData("CHECK_DATE", tnull);
                check_flg = false;
            }
            // ϸ����
            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();

            /** ����Ƿ���춯״̬�ж� */
            String org_code = parm.getValue("ORG_CODE");
            if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
                this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
                return;
            }

            // ϸ����Ϣ
            // �˻����ⵥ��
            String reg_no = "";
            // ����������
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);

            // �˻�����
            double qty = 0.00;
            // ������
            double ver_qty = 0.00;
            // �����
            double stock_qty = 0.00;
            // ״̬
            String update_flg = "0";
            // ҩƷ����
            String order_code = "";
            // ���������ת����
            TParm getTRA = new TParm();
            // �����Ч��������
            TParm getSEQ = new TParm();
            // ����ת����
            double s_qty = 0.00;
            // ���ת����
            double d_qty = 0.00;
            // ����
            String batch_no = "";
            // �������
            int batch_seq = 1;
            // ��Ч��
            String valid_date = "";
            // ���ۼ۸�
            double retail = 0.00;
            // �˻�����
            double regprice = 0.00;
            // ���յ���
            String ver_no = "";
            // ���յ������
            int ver_no_seq = 0;
            // ���˻���
            double actual_qty = 0;
            int count = 0;

            for (int i = 0; i < newrows.length; i++) {
                //System.out.println("----1" + reg_no);
                // �жϸ��е�����״̬
                if (!dataStore.isActive(newrows[i])) {
                    continue;
                }
                //System.out.println("----2" + reg_no);
                // ���ݲ�ͬ�ķ�Ʊ�����ɲ�ͬ���˻����ⵥ��
                reg_no = getRegressgoodsNO(dataStore.getItemString(newrows[i],
                    "INVOICE_NO"));
                //System.out.println("----3" + reg_no);
                //System.out.println("----4" + seq);
                // ���ⵥ�ż���
                parm.setData("REGRESSGOODS", count, reg_no);
                // ҩƷ���뼯��
                order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
                parm.setData("ORDER_CODE", count, order_code);
                // ���������ת����
                getTRA = INDTool.getInstance().getTransunitByCode(order_code);
                if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                    this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
                    return;
                }
                // ����ת���ʼ���
                s_qty = getTRA.getDouble("STOCK_QTY", 0);
                parm.setData("STOCK_QTY", count, s_qty);
                // ���ת���ʼ���
                d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                parm.setData("DOSAGE_QTY", count, d_qty);
                // �����
                stock_qty = getStockQTY(org_code, order_code);
                parm.setData("SUM_QTY", count, stock_qty);
                // ���ż���
                batch_no = dataStore.getItemString(newrows[i], "BATCH_NO");
                parm.setData("BATCH_NO", count, batch_no);
                // ��Ч��
                valid_date = StringTool.getString(dataStore.getItemTimestamp(
                    newrows[i], "VALID_DATE"), "yyyy/MM/dd");
                parm.setData("VALID_DATE", count, valid_date);
                //**********************************************************************************
                //luhai modify 2012-01-11 batchSeq ������ʱ�Ѿ���¼��ɾ����������Ч�ڲ�ѯbatchSeq begin
                //**********************************************************************************
//                // �����Ч��������
//                getSEQ = onQueryStockBatchSeq(org_code, order_code, batch_no, valid_date);
//                if (getSEQ.getErrCode() < 0 || getSEQ.getCount("BATCH_SEQ") <= 0) {
//                    this.messageBox("ҩƷ" + order_code + "������Ŵ���");
//                    return;
//                }
//                batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
//                // �����Ч�������߼���
//                parm.setData("BATCH_SEQ", count, batch_seq);
                batch_seq =StringTool.getInt(dataStore.getItemString(newrows[i], "BATCH_SEQ"));
                parm.setData("BATCH_SEQ", count, batch_seq);
                //**********************************************************************************
                //luhai modify 2012-01-11 batchSeq ������ʱ�Ѿ���¼��ɾ����������Ч�ڲ�ѯbatchSeq end
                //**********************************************************************************
                // �˻�������
                qty = dataStore.getItemDouble(newrows[i], "QTY");
                parm.setData("QTY", count, qty);
                // ����������
                ver_qty = resultParm.getDouble("VERIFYIN_QTY", newrows[i]);
                parm.setData("VERIFYIN_QTY", count, ver_qty);
                actual_qty = resultParm.getDouble("ACTUAL_QTY", newrows[i]);
                ver_qty = ver_qty - actual_qty;

                // ���ۼ۸񼯺�
                retail = dataStore.getItemDouble(newrows[i], "RETAIL_PRICE");
                parm.setData("RETAIL_PRICE", count, retail);
                // �˻��۸񼯺�
                regprice = dataStore.getItemDouble(newrows[i], "UNIT_PRICE");
                parm.setData("UNIT_PRICE", count, regprice);
                // �˻���ż���
                parm.setData("SEQ_NO", count, seq);
                // ���յ��ż���
                ver_no = dataStore.getItemString(newrows[i], "VERIFYIN_NO");
                parm.setData("VERIFYIN_NO", count, ver_no);
                // ���յ�����ż���
                ver_no_seq = dataStore.getItemInt(newrows[i], "VERSEQ_NO");
                parm.setData("VERSEQ_NO", count, ver_no_seq);

                // ���յ���
                dataStore.setItem(newrows[i], "REGRESSGOODS_NO", reg_no);
                // ���
                dataStore.setItem(newrows[i], "SEQ_NO", seq);

                // ״̬
                if (check_flg && qty == ver_qty) {
                    update_flg = "3";
                }
                else if (check_flg && qty < ver_qty) {
                    update_flg = "1";
                }
                else if (!check_flg) {
                    update_flg = "0";
                }

                dataStore.setItem(newrows[i], "UPDATE_FLG", update_flg);
                // OPT_USER,OPT_DATE,OPT_TERM
                dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(newrows[i], "OPT_DATE", date);
                dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
                count++;
            }
            // �õ�dataStore�ĸ���SQL
            String updateSql[] = dataStore.getUpdateSQL();
            parm.setData("UPDATE_SQL", updateSql);
            // ִ����������
            //System.out.println("----"+parm);
            result = TIOM_AppServer.executeAction(
                "action.ind.INDRegressgoodsAction", "onInsert", parm);
            //dataStore.showDebug();
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            this.setValue("REGRESSGOODS_NO", parm.getValue("REGRESSGOODS", 0));
        }
        else if ("updateM".equals(action)) {
            //System.out.println("222222222222222222");
            // �����˻�������
            if (!CheckDataM()) {
                return;
            }
            // ����ֵȡ��
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));

            String check_flg = getValueString("CHECK_FLG");
            if ("Y".equals(check_flg)) {
                // �����Ա
                parm.setData("CHECK_USER", Operator.getID());
                // ���ʱ��
                parm.setData("CHECK_DATE", date);
                // ϸ����
                TTable table_D = getTable("TABLE_D");
                table_D.acceptText();
                TDataStore dataStore = table_D.getDataStore();

                /** ����Ƿ���춯״̬�ж� */
                String org_code = parm.getValue("ORG_CODE");
                if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
                    this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
                    return;
                }

                // ϸ����Ϣ
                // �˻�����
                double qty = 0.00;
                // ������
                double ver_qty = 0.00;
                // �����
                double stock_qty = 0.00;
                // ״̬
                String update_flg = "0";
                // ҩƷ����
                String order_code = "";
                // ���������ת����
                TParm getTRA = new TParm();
                // �����Ч��������
                TParm getSEQ = new TParm();
                // ����ת����
                double s_qty = 0.00;
                // ���ת����
                double d_qty = 0.00;
                // ����
                String batch_no = "";
                // �������
                int batch_seq = 1;
                // ��Ч��
                String valid_date = "";
                // ���ۼ۸�
                double retail = 0.00;
                // �˻�����
                double regprice = 0.00;
                // ���յ���
                String ver_no = "";
                // ���յ������
                int ver_no_seq = 0;
                // ���˻���
                double actual_qty = 0;
                // ���ⵥ��
                String reg_no = this.getValueString("REGRESSGOODS_NO");
                for (int i = 0; i < table_D.getRowCount(); i++) {
                    // ���ⵥ�ż���
                    parm.setData("REGRESSGOODS", i, reg_no);
                    // ҩƷ���뼯��
                    order_code = dataStore.getItemString(i, "ORDER_CODE");
                    parm.setData("ORDER_CODE", i, order_code);
                    // ���������ת����
                    getTRA = INDTool.getInstance().getTransunitByCode(
                        order_code);
                    if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                        this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
                        return;
                    }
                    // ����ת���ʼ���
                    s_qty = getTRA.getDouble("STOCK_QTY", 0);
                    parm.setData("STOCK_QTY", i, s_qty);
                    // ���ת���ʼ���
                    d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                    parm.setData("DOSAGE_QTY", i, d_qty);
                    // �����
                    stock_qty = getStockQTY(org_code, order_code);
                    parm.setData("SUM_QTY", i, stock_qty);
                    // ���ż���
                    batch_no = dataStore.getItemString(i, "BATCH_NO");
                    parm.setData("BATCH_NO", i, batch_no);

                    // ��Ч��
                    valid_date = StringTool.getString(dataStore
                        .getItemTimestamp(i, "VALID_DATE"), "yyyy/MM/dd");
                    parm.setData("VALID_DATE", i, valid_date);
//                    Vector vector = dataStore.getVector("BATCH_SEQ");
//                    String batchSeq = dataStore.getItemString(i, "BATCH_SEQ");
//                    System.out.println(batchSeq+"======================");
                    //luhai modify 2012-05-07 begin �ۿ����batchSeq ���д��� begin
//                    // �����Ч��������
//                    getSEQ = onQueryStockBatchSeq(org_code, order_code,
//                                                  batch_no, valid_date);
//                    if (getSEQ.getErrCode() < 0 ||
//                        getSEQ.getCount("BATCH_SEQ") <= 0) {
//                        this.messageBox("ҩƷ" + order_code + "������Ŵ���");
//                        return;
//                    }
//                    batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
//                    String batchSeqStr = dataStore.getItemString(i, "BATCH_SEQ");
//                    System.out.println("======"+batchSeqStr);
                    batch_seq =StringTool.getInt(dataStore.getItemString(i, "BATCH_SEQ"));
                  //luhai modify 2012-05-07 begin �ۿ����batchSeq ���д��� begin
                    // �����Ч�������߼���
                    parm.setData("BATCH_SEQ", i, batch_seq);
                    // �˻�������
                    qty = dataStore.getItemDouble(i, "QTY");
                    parm.setData("QTY", i, qty);
                    // ����������
                    TParm inparm = new TParm();
                    inparm.setData("VERIFYIN_NO",
                                   table_D.getItemString(i, "VERIFYIN_NO"));
                    inparm.setData("SEQ_NO", table_D.getItemInt(i, "VERSEQ_NO"));
                    inparm = IndVerifyinDTool.getInstance().onQuery(inparm);
                    //System.out.println("inparm" + inparm);
                    ver_qty = inparm.getDouble("VERIFYIN_QTY", 0);
                    actual_qty = inparm.getDouble("ACTUAL_QTY", 0);
                    ver_qty = ver_qty - actual_qty;
                    //System.out.println("ver_qty" + ver_qty);
                    //System.out.println("actual_qty" + actual_qty);
                    //parm.setData("VERIFYIN_QTY", i, ver_qty);
                    // ���ۼ۸񼯺�
                    //zhangyong20091015 ��� d_qty
                    retail = dataStore.getItemDouble(i, "RETAIL_PRICE") / d_qty;
                    parm.setData("RETAIL_PRICE", i, retail);
                    // �˻��۸񼯺�
                    regprice = dataStore.getItemDouble(i, "UNIT_PRICE");
                    parm.setData("UNIT_PRICE", i, regprice);
                    // �˻���ż���
                    //parm.setData("SEQ_NO", i, seq);
                    // ���յ��ż���
                    ver_no = dataStore.getItemString(i, "VERIFYIN_NO");
                    parm.setData("VERIFYIN_NO", i, ver_no);
                    // ���յ�����ż���
                    ver_no_seq = dataStore.getItemInt(i, "VERSEQ_NO");
                    parm.setData("VERSEQ_NO", i, ver_no_seq);

                    // ���ⵥ��
                    dataStore.setItem(i, "REGRESSGOODS_NO", reg_no);
                    // ���ⵥ���
                    //dataStore.setItem(i, "SEQ_NO", seq);

                    //System.out.println("1-" + qty);
                    //System.out.println("2-" + ver_qty);
                    // ״̬
                    if ("Y".equals(check_flg) && qty == ver_qty) {
                        update_flg = "3";
                    }
                    else if ("Y".equals(check_flg) && qty < ver_qty) {
                        update_flg = "1";
                    }
                    else if (!"Y".equals(check_flg)) {
                        update_flg = "0";
                    }
                    dataStore.setItem(i, "UPDATE_FLG", update_flg);
                    // OPT_USER,OPT_DATE,OPT_TERM
                    dataStore.setItem(i, "OPT_USER", Operator.getID());
                    dataStore.setItem(i, "OPT_DATE", date);
                    dataStore.setItem(i, "OPT_TERM", Operator.getIP());
                }
                parm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            }
            else {
                parm.setData("CHECK_USER", "");
                parm.setData("CHECK_DATE", tnull);
            }
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("REGRESSGOODS_NO", getValueString("REGRESSGOODS_NO"));

            // ִ�����ݸ���
            result = TIOM_AppServer.executeAction(
                "action.ind.INDRegressgoodsAction", "onUpdate", parm);
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            // ���ô�ӡ����
            onPrint();
            if ("Y".equals(check_flg)) {
                //�����˻������ж��˻��������������״̬
                updateIndRegressGoodsDUpdateFlg(getValueString(
                    "REGRESSGOODS_NO"));
            }
        }
        else if ("updateD".equals(action)) {
            //System.out.println("3333333333333333");
            // �����˻���ϸ��
            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();
            // ���resultParm
            TParm inparm = new TParm();
            inparm.setData("VERIFYIN_NO", dataStore.getItemString(0,
                "VERIFYIN_NO"));
            inparm.setData("SEQ_NO", dataStore.getItemInt(0, "VERSEQ_NO"));
            resultParm = IndVerifyinMTool.getInstance().onQueryDone(inparm);
            // ϸ����
            if (!CheckDataD()) {
                return;
            }
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // ���̶�����������
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            // ϸ����ж�
            if (!table_D.update()) {
                messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_D.setDSValue();
            return;
        }
        this.onClear();
        //System.out.println("44444444444444444");
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            this.messageBox("��������ɲ���ɾ��");
        }
        else {
            if (getTable("TABLE_M").getSelectedRow() > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ���˻���", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    TParm parm = new TParm();
                    // ϸ����Ϣ
                    if (table_D.getRowCount() > 0) {
                        table_D.removeRowAll();
                        String deleteSql[] = table_D.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // ������Ϣ
                    parm.setData("REGRESSGOODS_NO",
                                 getValueString("REGRESSGOODS_NO"));
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDRegressgoodsAction", "onDeleteM",
                        parm);
                    // ɾ���ж�
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    getTable("TABLE_M").removeRow(
                        getTable("TABLE_M").getSelectedRow());
                    this.messageBox("ɾ���ɹ�");
                }
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ���˻�ϸ��", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    int row = table_D.getSelectedRow();
                    table_D.removeRow(row);
                    // ϸ����ж�
                    if (!table_D.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_D.setDSValue();
                }
            }
        }
    }

    /**
     * �����յ�
     */
    public void onExport() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("�˻����Ų���Ϊ��");
            return;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("�˻����̲���Ϊ��");
            return;
        }
        TParm parm = new TParm();
        String orgCode = getValueString("ORG_CODE") ;
        parm.setData("ORG_CODE", orgCode);
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        Object result = openDialog("%ROOT%\\config\\ind\\INDUnRegressgoods.x",
                                   parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            resultParm = (TParm) result;
            TTable table = getTable("TABLE_D");
            double verifyin_qty = 0;
            double actual_qty = 0;
            double verifyin_price = 0;
            double retail_price = 0;
            //System.out.println("addParm"+addParm);
            String msg = "";
            for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
            	
            	
            	String orderCode = addParm.getValue("ORDER_CODE", i); 
            	String orderDesc = addParm.getValue("ORDER_DESC",i);
            	double stockQty = INDTool.getInstance().getStockQTY(orgCode, orderCode);
            	if(stockQty <= 0 ){
            		msg += orderCode+","+orderDesc+";" ;
            		continue ;
            	}
            	//99999999
                int row = table.addRow();
                // ���DATESTORE
                // ORDER_CODE
                table.getDataStore().setItem(i, "ORDER_CODE",
                                             orderCode );
                // ���TABLE_D����
                // �˻���
                verifyin_qty = addParm.getDouble("VERIFYIN_QTY", i);
                actual_qty = addParm.getDouble("ACTUAL_QTY", i);
                table.setItem(row, "QTY", verifyin_qty - actual_qty);
                // ��λ
                table.setItem(row, "BILL_UNIT", addParm
                              .getData("BILL_UNIT", i));
                // �˻�����
                verifyin_price = addParm.getDouble("VERIFYIN_PRICE", i);
                table.setItem(row, "UNIT_PRICE", verifyin_price);
                // �˻����
                table.setItem(row, "AMT",
                              StringTool.round( (verifyin_qty - actual_qty)
                                               * verifyin_price, 2));
                // ���ۼ�
                retail_price = addParm.getDouble("RETAIL_PRICE", i);
                table.setItem(row, "RETAIL_PRICE", retail_price);
                // ���յ���
                table.setItem(row, "VERIFYIN_NO", addParm.getData(
                    "VERIFYIN_NO", i));
                // ���յ������
                table.setItem(row, "VERSEQ_NO", addParm.getData("SEQ_NO", i));
//                // �����
//                double sum_qty = INDTool.getInstance().getStockQTY(
//                    this.getValueString("ORG_CODE"),
//                    addParm.getValue("ORDER_CODE", i));
//                System.out.println("----"+sum_qty);
//                if (sum_qty < 0) {
//                    sum_qty = 0;
//                }
//                table.setItem(row, "SUM_QTY", sum_qty);
                //table.getDataStore().setItem(i, "SUM_QTY", sum_qty);
                // ����
                table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
                // ��Ч��
                String valid_date = StringTool.getString(addParm.getTimestamp(
                    "VALID_DATE", i), "yyyy/MM/dd");
                table.setItem(row, "VALID_DATE", valid_date);
                //luhai 2012-01-11 add batchSeq begin
                table.setItem(row, "BATCH_SEQ", addParm.getValue("BATCH_SEQ", i));
                //luhai 2012-01-11 add batchSeq end
                //luhai 2012-01-11 add verifyInPrice begin
                table.setItem(row, "VERIFYIN_PRICE", addParm.getValue("VERIFYIN_PRICE", i));
                //luhai 2012-01-11 add verifyInPrice end
                
                // ��Ʊ����
                //table.setItem(row, "INVOICE_DATE", tnull);
                //luhai add 2012-2-12 ���뷢Ʊ�źͷ�Ʊ���begin
                table.setItem(row, "INVOICE_NO", addParm.getValue("INVOICE_NO", i));
                String invoiceDate = StringTool.getString(addParm.getTimestamp(
                        "INVOICE_DATE", i), "yyyy/MM/dd");
                table.setItem(row, "INVOICE_DATE", invoiceDate);
                //luhai add 2012-2-12 ���뷢Ʊ�źͷ�Ʊ���end
                // �趨����״̬
                table.getDataStore().setActive(row, false);
            }
            if(msg != null && msg.length() > 0 ){
            	this.messageBox(msg+"\r\nû�п�治���˻�");
            }
            action = "insert";
            getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            // getTextField("REGRESSGOODS_NO").setEnabled(false);
            table.setDSValue();
            //zhangyong20110517
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
    }

    /**
     * ��ӡ�˻���
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("REGRESSGOODS_NO"))) {
            this.messageBox("�������˻���");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "�˻���");
            date.setData("ORG_CODE", "TEXT", "�˻���λ: " +
                         this.getComboBox("ORG_CODE").getSelectedName());
            date.setData("SUP_CODE", "TEXT", "��������: " +
                         this.getTextFormat("SUP_CODE").getText());
            date.setData("DATE", "TEXT",
                         "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));
            date.setData("REG_NO", "TEXT",
                         "���ⵥ��: " + this.getValueString("REGRESSGOODS_NO"));
            date.setData("INVOICE_NO", "TEXT",
                         "��Ʊ��: " + table_d.getItemString(0, "INVOICE_NO"));
            date.setData("INVOICE_DATE", "TEXT",
                         "��Ʊ����: " +
                         table_d.getItemString(0, "INVOICE_DATE").
                         substring(0, 10).replace('-', '/'));
            date.setData("INVOICE_AMT", "TEXT",
                         "��Ʊ���: " + this.getValueString("SUM_REG_ATM"));

            // �������
            TParm parm = new TParm();
            String order_code = "";
            String order_desc = "";
            //luhai modify 2012-2-11 �������ۼ۵��ܶ�
            double ownAmt=0;
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
//                System.out.println(INDSQL.
//                    getOrderInfoByCode(order_code,
//                                       this.getValueString("SUP_CODE"), "REG"));
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code,
                                       this.getValueString("SUP_CODE"), "REG")));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return;
                }
                if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
                    order_desc = inparm.getValue("ORDER_DESC", 0);
                }
                else {
                    order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
                        inparm.getValue("GOODS_DESC", 0) + ")";
                }
                parm.addData("ORDER_DESC", order_desc);
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("QTY",
                             df3.format(table_d.getItemDouble(i, "QTY")));
                parm.addData("UNIT_PRICE",
                             df4.format(table_d.getItemDouble(i, "UNIT_PRICE")));
                parm.addData("AMT",
                             df2.format(StringTool.round(table_d.getItemDouble(
                                 i, "UNIT_PRICE") *
                                        table_d.getItemDouble(i, "QTY"), 2)));

                parm.addData("RETAIL_PRICE",
                             df4.format(table_d.getItemDouble(i, "RETAIL_PRICE")));
                parm.addData("SELL_SUM",
                             df2.format(StringTool.round(table_d.getItemDouble(
                                 i, "RETAIL_PRICE") *
                                        table_d.getItemDouble(i, "QTY"), 2)));
                //luhai 2012-2-11 add ���ۼ��ܶ�
                ownAmt+=Double.parseDouble(df2.format(StringTool.round(table_d.getItemDouble(
                        i, "RETAIL_PRICE") *
                        table_d.getItemDouble(i, "QTY"), 2)));
                parm.addData("DIFF_SUM",
                             df2.format((StringTool.round(table_d.getItemDouble(
                                 i, "RETAIL_PRICE") *
                                        table_d.getItemDouble(i, "QTY"), 2))-
(StringTool.round(table_d.getItemDouble(i, "UNIT_PRICE") * table_d.getItemDouble(i, "QTY"), 2))));

                parm.addData("BATCH_NO",
                             table_d.getItemString(i, "BATCH_NO"));
                parm.addData("VALID_DATE",
                             StringTool.getString(table_d.getItemTimestamp(i, "VALID_DATE"),"yyyy/MM/dd"));
                String phaTypeName = "";
//                this.messageBox_(inparm.getValue("PHA_TYPE",0));
                if("C".equals(inparm.getValue("PHA_TYPE",0))){
                    phaTypeName = "�г�ҩ";
                }
                if("W".equals(inparm.getValue("PHA_TYPE",0))){
                    phaTypeName = "��ҩ";
                }
                if("G".equals(inparm.getValue("PHA_TYPE",0))){
                    phaTypeName = "�в�ҩ";
                }
                //ҩƷ����
                parm.addData("PHA_TYPE",
                             phaTypeName);
                //��������
                parm.addData("MAN_CHN_DESC",
                             inparm.getValue("MAN_CHN_DESC",0));
            }
            if (parm.getCount("ORDER_DESC") == 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }

            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "RETAIL_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "SELL_SUM");
            //2012-1-23 luhai modify delete ��������
//            parm.addData("SYSTEM", "COLUMNS", "DIFF_SUM");
            //2012-1-23 luhai modify delete ��������end
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
//            parm.addData("SYSTEM","COLUMNS","PHA_TYPE");
//            parm.addData("SYSTEM","COLUMNS","MAN_CHN_DESC");
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());

            // ��β����
            date.setData("TOT", "TEXT", "" + df2.format(
                StringTool.round(Double.parseDouble(this.
                getValueString("SUM_REG_ATM")), 2)));
            //luhai 2012-2-10 �������ۼ��ܶ�
            date.setData("OWN_AMT", "TEXT", "" + df2.format(
            		StringTool.round(ownAmt, 2)));
            String format = df2.format(
            		StringTool.round(Double.parseDouble(this.
            				getValueString("SUM_REG_ATM")), 2));
            date.setData("TOT_DESC", "TEXT", StringUtil.getInstance().numberToWord(Double.parseDouble(format)));
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\RegressGoods.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }


    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            // ������Ϣ(TABLE��ȡ��)
            String org_code = table.getItemString(row, "ORG_CODE");
            setValue("ORG_CODE", org_code);
            String reg_no = table.getItemString(row, "REGRESSGOODS_NO");
            setValue("REGRESSGOODS_NO", reg_no);
            Timestamp reg_date = table.getItemTimestamp(row,
                "REGRESSGOODS_DATE");
            setValue("REGRESSGOODS_DATE", reg_date);
            String sup_code = table.getItemString(row, "SUP_CODE");
            setValue("SUP_CODE", sup_code);
            String reson_code = table.getItemString(row, "REASON_CHN_DESC");
            setValue("REASON_CHN_DESC", reson_code);
            String description = table.getItemString(row, "DESCRIPTION");
            setValue("DESCRIPTION", description);
            String check_user = table.getItemString(row, "CHECK_USER");
            if ("".equals(check_user)) {
                getCheckBox("CHECK_FLG").setSelected(false);
            }
            else {
                getCheckBox("CHECK_FLG").setSelected(true);
            }
            // �趨ҳ��״̬
            getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            getTextField("REGRESSGOODS_NO").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
            getCheckBox("CHECK_FLG").setEnabled(true);
            action = "updateM";

            // ��ϸ��Ϣ
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getINDRegressgoodsD(reg_no));
//            System.out.println("�˻�ϸ���ѯsql��"+INDSQL.getINDRegressgoodsD(reg_no));
            tds.retrieve();
            // �۲���
            IndRegressgoodsObserver obser = new IndRegressgoodsObserver(this.
                getValueString("ORG_CODE"));
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            if (tds.rowCount() == 0) {
                seq = 1;
                this.messageBox("û���˻���ϸ");
            }
            else {
                table_D.setDSValue();
                // ��ѯ�����յ�������ϸ����Ϣ
                TParm parm = new TParm();
                parm.setData("VERIFYIN_NO",
                             table_D.getItemString(0, "VERIFYIN_NO"));
                parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
                parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
                resultParm = IndVerifyinDTool.getInstance().onQueryVerifyinDone(
                    parm);
                getCheckBox("CHECK_FLG").setEnabled(true);
                this.setValue("SUM_RETAIL_ATM", getSumRetailMoney());
                this.setValue("SUM_REG_ATM", getSumRegMoney());
                this.setValue("PRICE_DIFFERENCE", StringTool.round(
                    getSumRetailMoney() - getSumRegMoney(), 2));
                //luhai add ���������ܶ� 2012-2-13 
                this.setValue("SUM_VERIFYIN_AMT", getSumVerifyPrice());
            }
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            // ������Ϣ
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            if (getTextField("REGRESSGOODS_NO").isEnabled()) {
                action = "insert";
            }
            else {
                action = "updateD";
            }
            // ȡ��SYS_FEE��Ϣ��������״̬����
            String order_code = table.getDataStore().getItemString(table.
                getSelectedRow(), "ORDER_CODE");
            if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
            }
            else {
                callFunction("UI|setSysStatus", "");
            }
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
        // Table������
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
        	String orgCode = getValueString("ORG_CODE");
        	String orderCode = table.getItemString(row, "ORDER_CODE");
        	String batchNo = table.getItemString(row, "BATCH_NO");
        	String validDate = table.getItemString(row,"VALID_DATE").substring(0,
                    10);
        	double stockQty = INDTool.getInstance().getStockQTY(orgCode, orderCode, batchNo,  validDate, "");
        	// ���������ת����
            TParm getTRA = INDTool.getInstance().getTransunitByCode(
                orderCode);
            double sQty = getTRA.getDouble("DOSAGE_QTY",0);
        	if(stockQty <= 0 ){
        		this.messageBox("��治�������˻�!");
        		return true;
        	}
            double qty = TypeTool.getDouble(node.getValue());
            
            if(qty*sQty > stockQty){
            	this.messageBox("�˻��������ڿ���������˻�!");
            	return true;
            }
            if (qty <= 0) {
                this.messageBox("�˻���������С�ڻ����0");
                return true;
            }
            double amt = StringTool.round(qty
                                          *
                                          table.getItemDouble(row, "UNIT_PRICE"),
                                          2);
            table.setItem(row, "AMT", amt);
            //
            table.getDataStore().setItem(row, "AMT", amt);
            return false;
        }
        if ("UNIT_PRICE".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("���յ��۲���С�ڻ����0");
                return true;
            }
            double amt = StringTool.round(
                qty * table.getItemDouble(row, "QTY"), 2);
            table.setItem(row, "AMT", amt);
            //
            table.getDataStore().setItem(row, "AMT", amt);
            return false;
        }
        // ��Ʊ����
        if ("INVOICE_NO".equals(columnName) && row == 0) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "INVOICE_NO", node.getValue());
            }
        }
        // ��Ʊ����
        if ("INVOICE_DATE".equals(columnName)) {
            if (row == 0) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.setItem(i, "INVOICE_DATE", node.getValue());
                }
            }
            return false;
        }
        return false;
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int column = tableDown.getSelectedColumn();
        // ���ѡ�е���
        int row = tableDown.getSelectedRow();
        if (column == 0) {
            if (tableDown.getDataStore().isActive(row)) {
                // ���������ܽ��
                double amount1 = tableDown.getItemDouble(row, "QTY");
                double sum = tableDown.getItemDouble(row, "RETAIL_PRICE")
                    * amount1;
                double sum_atm = this.getValueDouble("SUM_RETAIL_ATM");
                this.setValue("SUM_RETAIL_ATM", StringTool.round(sum + sum_atm,
                    2));
                // �����˻��ܽ��
                double reg_amt = this.getValueDouble("SUM_REG_ATM");
                double amt = tableDown.getItemDouble(row, "AMT");
                amt = StringTool.round(reg_amt + amt, 2);
                this.setValue("SUM_REG_ATM", amt);
            }
            else {
                // ���������ܽ��
                double amount1 = tableDown.getItemDouble(row, "QTY");
                double sum = tableDown.getItemDouble(row, "RETAIL_PRICE")
                    * amount1;
                double sum_atm = this.getValueDouble("SUM_RETAIL_ATM");
                if (sum_atm - sum < 0) {
                    this.setValue("SUM_RETAIL_ATM", 0);
                }
                else {
                    this.setValue("SUM_RETAIL_ATM", StringTool.round(sum_atm
                        - sum, 2));
                }
                // �����˻��ܽ��
                double reg_amt = this.getValueDouble("SUM_REG_ATM");
                double amt = tableDown.getItemDouble(row, "AMT");
                if (reg_amt - amt < 0) {
                    this.setValue("SUM_REG_ATM", 0);
                }
                else {
                    this.setValue("SUM_REG_ATM", StringTool.round(
                        reg_amt - amt, 2));
                }
            }
            // �������
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getValueDouble("SUM_RETAIL_ATM")
                - getValueDouble("SUM_REG_ATM"), 2));
        }
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE_D");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getDataStore().setActive(i, true);
                table.getDataStore().setItem(i, "UPDATE_FLG", "0");
                this.setValue("SUM_RETAIL_ATM", getSumRetailMoney());
                this.setValue("SUM_REG_ATM", getSumRegMoney());
                this.setValue("PRICE_DIFFERENCE", StringTool.round(
                    getSumRetailMoney() - getSumRegMoney(), 2));
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getDataStore().setActive(i, false);
                table.getDataStore().setItem(i, "UPDATE_FLG", "");
                this.setValue("SUM_RETAIL_ATM", 0);
                this.setValue("SUM_REG_ATM", 0);
                this.setValue("PRICE_DIFFERENCE", 0);
            }
        }
        table.setDSValue();
    }

    /**
     * ��ѡ��ı��¼�
     */
    public void onChangeRadioButton() {
        if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
        }
    }


    /**
     * ��ʼ��������
     */
    private void initPage() {
        /**
         * Ȩ�޿���
         * Ȩ��1:һ�����Ȩ��,ֻ��ʾ������������
         * Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ��
         */
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        // ��������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // ��ʼ��ҳ��״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REGRESSGOODS_DATE", date);
        action = "insert";
//        TTable table_D = getTable("TABLE_D");
//        table_D.removeRowAll();
//        table_D.setSelectionMode(0);
//        TDS tds = new TDS();
//        tds.setSQL(INDSQL.getINDRegressgoodsD(getValueString("REGRESSGOODS_NO")));
//        tds.retrieve();
//        if (tds.rowCount() == 0) {
//            seq = 1;
//        }
//        else {
//            seq = getMaxSeq(tds, "SEQ_NO");
//        }
//
//        // �۲���
//        IndRegressgoodsObserver obser = new IndRegressgoodsObserver(this.
//            getValueString("ORG_CODE"));
//        tds.addObserver(obser);
//        table_D.setDataStore(tds);
//        table_D.setDSValue();
        resultParm = new TParm();
    }

    /**
     * ȡ���˻�����
     *
     * @return String
     */
    private String getRegressgoodsNO(String invoiceNo) {
        String regNo = "";
        if (!map.isEmpty() && map.containsKey(invoiceNo)) {
            String result[] = ( (String) map.get(invoiceNo)).split("-");
            regNo = result[0];
            seq = Integer.parseInt(result[1]) + 1;
            map.put(invoiceNo, regNo + "-" + seq);
            //System.out.println("a"+result[0] + "--b"+result[1] + ((String) map.get(invoiceNo)));
        }
        else {
            regNo = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_REGRESSGOODS", "No");
            seq = 1;
            map.put(invoiceNo, regNo + "-" + seq);
        }
        //System.out.println("----" + regNo);
        return regNo;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("�˻����Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("�˻����̲���Ϊ��");
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
        TTable table = getTable("TABLE_D");
        table.acceptText();
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getDataStore().isActive(i)) {
                count++;
                // �ж�������ȷ��
                double qty = table.getItemDouble(i, "QTY");
                if (qty <= 0) {
                    this.messageBox("�˻���������С�ڻ����0");
                    return false;
                }
                double pur_qty = 0;
                double actual = 0;
                if ("insert".equals(action)) {
                    pur_qty = resultParm.getDouble("VERIFYIN_QTY", i);
                }
                else if ("updateD".equals(action)) {
                    TParm inparm = new TParm();
                    inparm.setData("VERIFYIN_NO",
                                   table.getItemString(i, "VERIFYIN_NO"));
                    inparm.setData("SEQ_NO", table.getItemInt(i, "VERSEQ_NO"));
                    inparm = IndVerifyinDTool.getInstance().onQuery(inparm);
                    //System.out.println("inparm" + inparm);
                    pur_qty = inparm.getDouble("VERIFYIN_QTY", 0);
                    actual = inparm.getDouble("ACTUAL_QTY", 0);
                    pur_qty = pur_qty - actual;
                }
                if (qty > pur_qty) {
                    this.messageBox("�˻��������ܴ�����������");
                    return false;
                }
                qty = table.getItemDouble(i, "UNIT_PRICE");
                if (qty <= 0) {
                    this.messageBox("�˻����۲���С�ڻ����0");
                    return false;
                }
                // ��Ʊ����
                if ("".equals(table.getItemString(i, "INVOICE_NO"))) {
                    this.messageBox("��Ʊ���벻��Ϊ��");
                    return false;
                }
                // ��Ʊ����
                if (table.getItemData(i, "INVOICE_DATE") == null) {
                    this.messageBox("��Ʊ���ڲ���Ϊ��");
                    return false;
                }
                // ����
                if ("".equals(table.getItemString(i, "BATCH_NO"))) {
                    this.messageBox("���Ų���Ϊ��");
                    return false;
                }
                // Ч��
                if (table.getItemData(i, "VALID_DATE") == null) {
                    this.messageBox("Ч�ڲ���Ϊ��");
                    return false;
                }
            }
        }
        if (count == 0) {
            this.messageBox("û��ִ������");
            return false;
        }
        return true;
    }

    /**
     * ���������ܽ��:SUM_RETAIL_ATM
     *
     * @return
     */
    private double getSumRetailMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                double amount1 = table.getItemDouble(i, "QTY");
                sum += table.getItemDouble(i, "RETAIL_PRICE") * amount1;
            }
        }
        return StringTool.round(sum, 2);
    }
    /**
     * ���������ܽ��:SUM_VERIFYIN_PRICE
     *
     * @return
     */
    private double getSumVerifyPrice() {
    	TTable table = getTable("TABLE_D");
    	TDataStore ds = table.getDataStore();
    	table.acceptText();
    	double sum = 0;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
    			double amount1 = table.getItemDouble(i, "QTY");
    			sum += table.getItemDouble(i, "VERIFYIN_PRICE") * amount1;
    		}
    	}
    	return StringTool.round(sum, 2);
    }

    /**
     * �����˻��ܽ��:SUM_REG_ATM
     *
     * @return
     */
    private double getSumRegMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                sum += table.getItemDouble(i, "AMT");
            }
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
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.rowCount();
        // ��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // �������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        // ���ż�1
        s++;
        return s;
    }

    /**
     * ҩ����ұ���¼�
     */
    public void onChangeOrgCode() {
        onInsertObserver();
    }

    /**
     * ��ӹ۲���
     */
    public void onInsertObserver() {
        TTable table_D = getTable("TABLE_D");
        table_D.removeRowAll();
        table_D.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getINDRegressgoodsD(getValueString("REGRESSGOODS_NO")));
        tds.retrieve();
        if (tds.rowCount() == 0) {
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }

        // �۲���
        IndRegressgoodsObserver obser = new IndRegressgoodsObserver(this.
            getValueString("ORG_CODE"));
        tds.addObserver(obser);
        table_D.setDataStore(tds);
        table_D.setDSValue();
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

    //�����˻������ж��˻��������������״̬
    private void updateIndRegressGoodsDUpdateFlg(String regressgoods_no) {
        String sql =
            "UPDATE IND_REGRESSGOODSD SET UPDATE_FLG = '3' WHERE REGRESSGOODS_NO = '" +
            regressgoods_no + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    }

    /**
     * ����ҩ����,ҩƷ����,����,��Ч�ڲ�ѯҩƷ���������
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public String getIndStockBatchSeq(String org_code,
                                      String order_code, String batch_no,
                                      String valid_date) {
        return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
            " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
            + org_code + "' AND ORDER_CODE = '" + order_code
            + "' AND BATCH_NO = '" + batch_no
            + "' AND VALID_DATE = TO_DATE('" + valid_date
            + "','yyyy-MM-dd') ";
    }

    /**
     * ����ҩ����,ҩƷ����,����,��Ч�ڲ�ѯҩƷ���������
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public TParm onQueryStockBatchSeq(String org_code, String order_code,
                                      String batch_no, String valid_date) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            getIndStockBatchSeq(org_code, order_code, batch_no,
                                       valid_date)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    public String getIndStockQty(String order_code, String org_code) {
        return "SELECT SUM(LAST_TOTSTOCK_QTY+ IN_QTY- OUT_QTY + "
            + "CHECKMODI_QTY) AS QTY FROM IND_STOCK WHERE ORG_CODE = '" +
            org_code + "' AND ORDER_CODE = '" + order_code + "'";
    }

    /**
     * ����ҩ���ż�ҩƷ�����ѯҩ������
     *
     * @param org_code
     *            ҩ����
     * @param order_code
     *            ҩƷ����
     * @return QTY �����
     */
    public double getStockQTY(String org_code, String order_code) {
        if ("".equals(org_code) || "".equals(order_code)) {
            return -1;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        TParm result = new TParm(TJDODBTool.getInstance().select(getIndStockQty(
            org_code, order_code)));
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return -1;
        }
        return result.getDouble("QTY", 0);
    }

}
