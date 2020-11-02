package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.IndPurPlanDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndPurPlanObserver;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import jdo.util.Manager;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextFormat;
import jdo.spc.INDTool;

/**
 * <p>
 * Title: �ɹ��ƻ�Control
 * </p>
 *
 * <p>
 * Description: �ɹ��ƻ�Control
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
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class INDPurPlanControl
    extends TControl {

    /**
     * ������ǣ�1.insertM�������ɹ��ƻ������� 2.updateM�����²ɹ��ƻ�������
     */
    private String action = "insertM";

    // ���ѡ���к�
    private int selectRow_M = -1;

    // ��ѯ��������
    private TParm parm_M;

    // ϸ�����
    private int seq;

    // ȫ������Ȩ��
    private boolean dept_popedom = true;

    // �������ɲɹ��ƻ���Ȩ��
    private boolean excerpt_popedom = true;

    // �����޸�������Ȩ��
    private boolean plan_qty_popedom = true;

    // ɾ���ƻ���Ȩ��
    private boolean delete_popedom = true;

    // ��ѡ�ƻ��༭���ע��Ȩ��
    private boolean plan_flg_popedom = true;

    // �����޸���Ȩ��
    private boolean pur_qty_popedom = true;

    // ��ѡ�ɹ���ȷ��ע��Ȩ��
    private boolean pur_flg_popedom = true;

    // ¼��ȷ����Ȩ��
    private boolean check_qty_popedom = true;

    // ��ѡ������Ȩ��
    private boolean check_flg_popedom = true;

    // ��ѡ�ƻ����Ȩ��
    private boolean planend_flg_popedom = true;

    public INDPurPlanControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("�ƻ����Ų���Ϊ��");
            return;
        }
        if ("".equals(getValueString("PLAN_MONTH"))) {
            this.messageBox("�ƻ��·ݲ���Ϊ��");
            return;
        }
        // ���������
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Timestamp plan_month = (Timestamp) getValue("PLAN_MONTH");
        parm.setData("PLAN_MONTH", StringTool.getString(plan_month, "yyyy/MM"));
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        String plan_no = getValueString("PLAN_NO");
        if (!"".equals(plan_no)) {
            parm.setData("PLAN_NO", plan_no);
        }
        String plan_desc = getValueString("PLAN_DESC");
        if (!"".equals(plan_desc)) {
            parm.setData("PLAN_DESC", plan_desc);
        }

        // ���ؽ����
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDPurPlanAction",
                                              "onQueryM", parm);
        if (result == null || result.getCount() == 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        // ������������ж���״̬
        result = CheckStatus(result);
        TTable table_M = getTable("TABLE_M");
        table_M.setParmValue(result);
        // �����ݱ��浽�����
        parm_M = result;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������
        String clearString =
            "ORG_CODE;PLAN_MONTH;PLAN_NO;PLAN_DESC;SUM_TOTIL;SUM_TOTIL;"
            +
            "PLAN_FLG;PUR_FLG;CHECK_FLG;PLANEND_FLG;DESCRIPTION;ORDER_CODE;ORDER_DESC";
        clearValue(clearString);
        // ��ʼ��ҳ��״̬������
        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");
        TTable table_M = getTable("TABLE_M");
        table_M.setSelectionMode(0);
        table_M.removeRowAll();
        TTable table_D = getTable("TABLE_D");
        table_D.setSelectionMode(0);
        table_D.removeRowAll();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        ( (TMenuItem) getComponent("make")).setEnabled(false);
        getComboBox("ORG_CODE").setEnabled(true);
        getTextField("PLAN_NO").setEnabled(true);
        Timestamp date = SystemTool.getInstance().getDate();;
        setValue("PLAN_MONTH", date);
        // ��ʼ��ȫ�ֲ���
        action = "insertM";
        selectRow_M = -1;

        // Ȩ�޿���
        check_dept_popedom();
        check_excerpt_popedom();
        check_qty_popedom();
        check_select_flg_popedom();
        check_delete_popedom();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();

        Timestamp date = SystemTool.getInstance().getDate();;
        // �����ɹ��ƻ���
        if ("insertM".equals(action)) {
            if (!CheckData()) {
                return;
            }
            // ���ô洢�����õ�PLAN_NO
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            String plan_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_PURPLANM", "No");
            parm.setData("PLAN_NO", plan_no);
            parm.setData("PLAN_DESC", getValueString("PLAN_DESC"));
            Timestamp plan_month = (Timestamp) getValue("PLAN_MONTH");
            parm.setData("PLAN_MONTH", StringTool.getString(plan_month,
                "yyyy/MM"));
            TNull tnull = new TNull(Timestamp.class);
            parm.setData("PLAN_DATE", tnull);
            parm.setData("PLAN_USER", Operator.getID());
            parm.setData("PUR_USER", "");
            parm.setData("PUR_DATE", tnull);
            parm.setData("CHECK_USER", "");
            parm.setData("CHECK_DATE", tnull);
            parm.setData("PLANEND_USER", "");
            parm.setData("PLANEND_DATE", tnull);
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            parm.setData("CREATE_FLG", "N");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            //zhangyong20110517
            parm.setData("REGION_CODE", Operator.getRegion());
            // ִ����������
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurPlanAction", "onInsertM", parm);
            parm.setData("PLAN_STATUS", "�ƻ���");
            parm.setData("PURORDER_NO", "N");
        }
        else if ("updateM".equals(action)) {
            // ���²ɹ��ƻ���
            if (!CheckData()) {
                return;
            }
            // �Ӳ�ѯ�����ȡ������
            parm = parm_M.getRow(selectRow_M);
            TNull tnull = new TNull(Timestamp.class);
            if (parm.getData("PLAN_DATE") == null) {
                parm.setData("PLAN_DATE", tnull);
            }
            if (parm.getData("PUR_DATE") == null) {
                parm.setData("PUR_DATE", tnull);
            }
            if (parm.getData("PUR_USER") == null) {
                parm.setData("PUR_USER", "");
            }
            if (parm.getData("CHECK_DATE") == null) {
                parm.setData("CHECK_DATE", tnull);
            }
            if (parm.getData("CHECK_USER") == null) {
                parm.setData("CHECK_USER", "");
            }
            if (parm.getData("PLANEND_DATE") == null) {
                parm.setData("PLANEND_DATE", tnull);
            }
            if (parm.getData("PLANEND_USER") == null) {
                parm.setData("PLANEND_USER", "");
            }
            // ����ֵȡ��
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            parm.setData("PLAN_NO", getValueString("PLAN_NO"));
            parm.setData("PLAN_DESC", getValueString("PLAN_DESC"));
            Timestamp plan_month = (Timestamp) getValue("PLAN_MONTH");
            parm.setData("PLAN_MONTH", StringTool.getString(plan_month,
                "yyyy/MM"));
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            // ����״̬ѡ�����parm
            if ("Y".equals(getValueString("PLANEND_FLG"))) {
                parm.setData("PLANEND_USER", Operator.getID());
                parm.setData("PLANEND_DATE", date);
                parm.setData("PLAN_STATUS", "�ƻ����");
            }
            else if ("Y".equals(getValueString("CHECK_FLG"))) {
                parm.setData("CHECK_USER", Operator.getID());
                parm.setData("CHECK_DATE", date);
                parm.setData("PLAN_STATUS", "���ȷ��");
                // �жϲɹ���ȷ���Ƿ����
                if (parm.getData("PUR_DATE") == null) {
                    parm.setData("PUR_USER", Operator.getID());
                    parm.setData("PUR_DATE", date);
                }
            }
            else if ("Y".equals(getValueString("PUR_FLG"))) {
                parm.setData("PUR_USER", Operator.getID());
                parm.setData("PUR_DATE", date);
                parm.setData("PLAN_STATUS", "�ɹ������");
            }
            else if ("Y".equals(getValueString("PLAN_FLG"))) {
                parm.setData("PLAN_USER", Operator.getID());
                parm.setData("PLAN_DATE", date);
                parm.setData("PLAN_STATUS", "�ƻ��༭���");
            }
            else {
                parm.setData("PLAN_STATUS", "�ƻ���");
            }
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());

            // ִ�����ݸ���
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurPlanAction", "onUpdateM", parm);
        }
        else {
            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // ���̶�����������
            for (int i = 0; i < rows.length; i++) {
                dataStore
                    .setItem(rows[i], "PLAN_NO", getValueString("PLAN_NO"));
                dataStore.setItem(rows[i], "ORG_CODE",
                                  getValueString("ORG_CODE"));
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

        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onQuery();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getTable("TABLE_M").getSelectedRow() > -1) {
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ���ɹ��ƻ���", 2) == 0) {
                TTable table_D = getTable("TABLE_D");
                table_D.removeRowAll();
                TParm parm = new TParm();
                String plan_no = getValueString("PLAN_NO");
                parm.setData("PLAN_NO", plan_no);
                String org_code = getValueString("ORG_CODE");
                parm.setData("ORG_CODE", org_code);
                // ��ѯ����ϸ��
                TParm result = new TParm();
                result = IndPurPlanDTool.getInstance().onQuery(parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                if (result.getCount() > 0) {
                    // ɾ�����ϸ��
                    parm.setData("DELETE_SQL", table_D.getDataStore()
                                 .getUpdateSQL());
                }
                result = TIOM_AppServer.executeAction(
                    "action.spc.INDPurPlanAction", "onDeleteM", parm);
                // ɾ���ж�
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                getTable("TABLE_M").removeRow(
                    getTable("TABLE_M").getSelectedRow());
                this.messageBox("ɾ���ɹ�");
                this.onClear();
            }
        }
        else {
            TTable table_D = getTable("TABLE_D");
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ���ɹ��ƻ���ϸ��", 2) == 0) {
                table_D.removeRow(table_D.getSelectedRow());
                // ϸ����ж�
                if (!table_D.update()) {
                    messageBox("E0001");
                    return;
                }
                messageBox("P0001");
                table_D.setDSValue();
                return;
            }
        }
    }

    /**
     * �����õ�
     */
    public void onExport() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("PLAN_MONTH", getValueString("PLAN_MONTH"));
        parm.setData("PLAN_NO", getValueString("PLAN_NO"));
        parm.setData("CONTROL", this);
        this.openWindow("%ROOT%\\config\\spc\\INDPurplanExcerpt.x",
                        parm);
    }

    /**
     * ���ɶ�����
     */
    public void onMake() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("PLAN_MONTH", getValueString("PLAN_MONTH"));
        parm.setData("PLAN_NO", getValueString("PLAN_NO"));
        Object result = openDialog("%ROOT%\\config\\spc\\INDOrderForm.x",
                                   parm);
    }

    /**
     * ��ӡ�ƻ���
     */
    public void onPlan() {
        Timestamp datetime = SystemTool.getInstance().getDate();;
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("PLAN_NO"))) {
            this.messageBox("�����ڼƻ���");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "�ɹ��ƻ���");
            date.setData("ORG_CODE", "TEXT",
                         "�ƻ�����: " + getComboBox("ORG_CODE").getSelectedName());
            date.setData("PLAN_NO", "TEXT", "�ƻ�����: " + getValueString("PLAN_NO"));
            date.setData("DATE", "TEXT", "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // �������
            TParm parm = new TParm();
            String order_code = "";
            String sup_code = "";
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                sup_code = table_d.getDataStore().getItemString(i,
                    "SUP_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(
                    INDSQL.getOrderInfoByCode(order_code, sup_code, "PLAN")));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return;
                }
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("QTY", table_d.getItemDouble(i, "STOCK_QTY"));
                parm.addData("PLAN_QTY",
                             table_d.getItemDouble(i, "CHECK_QTY"));
                parm.addData("PRUCH_PRICE",
                             inparm.getDouble("CONTRACT_PRICE", 0));
                parm.addData("AMT",
                             StringTool.round(inparm.getDouble(
                                 "CONTRACT_PRICE",
                                 0) * table_d.getItemDouble(i, "PLAN_QTY"), 2));
                parm.addData("OWN_PRICE",
                             StringTool.round(inparm.getDouble("OWN_PRICE", 0) *
                                              inparm.getDouble("DOSAGE_QTY", 0),
                                              4));
                parm.addData("SUP_CODE",
                             inparm.getValue("SUP_CHN_DESC", 0));
                parm.addData("MAN_CODE",
                             inparm.getValue("MAN_CHN_DESC", 0));
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
            parm.addData("SYSTEM", "COLUMNS", "PLAN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "PRUCH_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
            parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");

            date.setData("TABLE", parm.getData());

            //��β����
            date.setData("OPT", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\PurPlan.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }

    /**
     * ҩƷ�������
     */
    public void onAnalyse() {
        Timestamp datetime = SystemTool.getInstance().getDate();;
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("PLAN_NO"))) {
            this.messageBox("�����ڼƻ���");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "ҩƷ���������");
            // �������
            TParm parm = new TParm();
            // �ƻ�����
            String plan_no = "";
            // �ƻ����ϼ�
            double sum_plan_amt = 0;
            // �����ϼ�
            double sum_in_amt = 0;
            // ������ϼ�
            double sum_diff_amt = 0;
            plan_no = this.getValueString("PLAN_NO");
            TParm inparm = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getOrderInfoByPlan(plan_no)));
            if (inparm == null || inparm.getErrCode() < 0) {
                this.messageBox("ҩƷ��Ϣ����");
                return;
            }
            if (inparm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            for (int i = 0; i < inparm.getCount("ORDER_DESC"); i++) {
                parm.addData("SUP_CODE", inparm.getValue("SUP_CHN_DESC", i));
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", i));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", i));
                parm.addData("PLAN_QTY", inparm.getDouble("PLAN_QTY", i));
                parm.addData("PLAN_PRICE", inparm.getDouble("PLAN_PRICE", i));
                parm.addData("IN_QTY", inparm.getDouble("IN_QTY", i));
                parm.addData("IN_PRICE", inparm.getDouble("IN_PRICE", i));
                parm.addData("PLAN_AMT", inparm.getDouble("PLAN_AMT", i));
                sum_plan_amt += inparm.getDouble("PLAN_AMT", i);
                parm.addData("IN_AMT", inparm.getDouble("IN_AMT", i));
                sum_in_amt += inparm.getDouble("IN_AMT", i);
                parm.addData("DIFF_QTY", inparm.getDouble("DIFF_QTY", i));
                parm.addData("DIFF_AMT", inparm.getDouble("DIFF_AMT", i));
                sum_diff_amt += inparm.getDouble("DIFF_AMT", i);
            }

            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "IN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "IN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "IN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "DIFF_QTY");
            parm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("TOTLE_1", "TEXT",
                         "�ƻ����ϼ�: " + sum_plan_amt);
            date.setData("TOTLE_2", "TEXT",
                         "�����ϼ�: " + sum_in_amt);
            date.setData("TOTLE_3", "TEXT",
                         "������ϼ�: " + sum_diff_amt);
            date.setData("USER", "TEXT",
                         "�Ʊ���: " + Operator.getName());
            date.setData("DATE", "TEXT",
                         "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\DiffOrder.jhw",
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
            ( (TMenuItem) getComponent("make")).setEnabled(false);
            // ������Ϣ(TABLE��ȡ��)
            String org_code = table.getItemString(row, "ORG_CODE");
            setValue("ORG_CODE", org_code);
            String plan_no = table.getItemString(row, "PLAN_NO");
            setValue("PLAN_NO", plan_no);
            String plan_month = table.getItemString(row, "PLAN_MONTH");
            setValue("PLAN_MONTH", plan_month);
            String plan_status = table.getItemString(row, "PLAN_STATUS");
            // ����״̬��ѡ״̬��ѡ��
            setPlanStatus(plan_status);
            // ������Ϣ(tparm��ѯ�������ȡ��)
            String plan_desc = parm_M.getValue("PLAN_DESC", row);
            setValue("PLAN_DESC", plan_desc);
            String description = parm_M.getValue("DESCRIPTION", row);
            setValue("DESCRIPTION", description);
            // �趨ҳ��״̬
            getComboBox("ORG_CODE").setEnabled(false);
            getTextField("PLAN_NO").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
            action = "updateM";
            selectRow_M = row;
            // ��ϸ��Ϣ
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getINDPurPlandD(org_code, plan_no));
            tds.retrieve();
            if (tds.rowCount() == 0) {
                seq = 1;
                this.messageBox("û�мƻ���ϸ");
            }
            else {
                seq = getMaxSeq(tds, "SEQ");
            }
            // �۲���
            IndPurPlanObserver obser = new IndPurPlanObserver();
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            table_D.setDSValue();

            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);

            // Ȩ�޿���
            check_excerpt_popedom();
            check_qty_popedom();
            check_select_flg_popedom();
            check_delete_popedom();
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            action = "updateD";
            // ������Ϣ
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            // ȡ��SYS_FEE��Ϣ��������״̬����
            /*
             String order_code = table.getDataStore().getItemString(table.
                getSelectedRow(), "ORDER_CODE");
                         this.setSysStatus(order_code);
             */
        }
    }

    /**
     * �ƻ��༭���FLG
     */
    public void onPlanAction() {
        if ("Y".equals(getValueString("PLAN_FLG"))) {
            TTable table = getTable("TABLE_D");
            if (table.getRowCount() == 0) {
                this.messageBox("û�мƻ���ϸ�����Ա༭��ɣ�");
                this.setValue("PLAN_FLG", "N");
            }
        }
    }

    /**
     * �ɹ���ȷ�����FLG
     */
    public void onPurAction() {
        if ("Y".equals(getValueString("PUR_FLG"))) {
            TTable table_D = getTable("TABLE_D");
            if (table_D.getRowCount() == 0) {
                this.messageBox("û�мƻ���ϸ�����Ա༭��ɣ�");
                this.setValue("PUR_FLG", "N");
                return;
            }
            TTable table_M = getTable("TABLE_M");
            Timestamp plan_date = table_M.getItemTimestamp(selectRow_M,
                "PLAN_DATE");
            if (plan_date == null) {
                this.messageBox("�ƻ��༭δ��ɣ�");
                this.setValue("PUR_FLG", "N");
            }
        }
    }

    /**
     * ���ȷ�����FLG
     */
    public void onCheckAction() {
        if ("Y".equals(getValueString("CHECK_FLG"))) {
            TTable table_D = getTable("TABLE_D");
            if (table_D.getRowCount() == 0) {
                this.messageBox("û�мƻ���ϸ�����Ա༭��ɣ�");
                this.setValue("CHECK_FLG", "N");
                return;
            }
            TTable table_M = getTable("TABLE_M");
            Timestamp plan_date = table_M.getItemTimestamp(selectRow_M,
                "PLAN_DATE");
            if (plan_date == null) {
                this.messageBox("�ƻ��༭δ��ɣ�");
                this.setValue("CHECK_FLG", "N");
            }
        }
    }

    /**
     * �ƻ����FLG
     */
    public void onPlanendAction() {
        if ("Y".equals(getValueString("PLANEND_FLG"))) {
            TTable table_D = getTable("TABLE_D");
            if (table_D.getRowCount() == 0) {
                this.messageBox("û�мƻ���ϸ�����Ա༭��ɣ�");
                this.setValue("PLANEND_FLG", "N");
                return;
            }
            TTable table_M = getTable("TABLE_M");
            Timestamp check_date = table_M.getItemTimestamp(selectRow_M,
                "CHECK_DATE");
            if (check_date == null) {
                this.messageBox("�ɹ����������δ��ɣ�");
                this.setValue("PLANEND_FLG", "N");
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
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("PLAN_QTY".equals(columnName)) {
            if ("Y".equals(this.getValue("PLAN_FLG"))) {
                this.messageBox("�ƻ��༭���,�����޸�");
                return true;
            }
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("�ƻ�������������С�ڵ���0");
                return true;
            }
            node.getTable().setItem(row, "PUR_QTY", qty);
            node.getTable().setItem(row, "CHECK_QTY", qty);
            node.getTable().getDataStore().setItem(row, "PUR_QTY", qty);
            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);
            return false;
        }
        if ("PUR_QTY".equals(columnName)) {
            if ("Y".equals(this.getValue("PUR_FLG"))) {
                this.messageBox("�ɹ���ȷ�����,�����޸�");
                return true;
            }
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("�޸���������С�ڵ���0");
                return true;
            }
            node.getTable().setItem(row, "CHECK_QTY", qty);
            node.getTable().getDataStore().setItem(row, "PUR_QTY", qty);
            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);
            return false;
        }
        if ("CHECK_QTY".equals(columnName)) {
            if ("Y".equals(this.getValue("PLANEND_FLG"))) {
                this.messageBox("�ƻ����,�����޸�");
                return true;
            }
            else if ("Y".equals(this.getValue("CHECK_FLG"))) {
                this.messageBox("���ȷ�����,�����޸�");
                return true;
            }
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("ȷ����������С�ڵ���0");
                return true;
            }
        }
        if ("STOCK_PRICE".equals(columnName)) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("�ɹ����۲�����С�ڵ���0");
                return true;
            }
            double qty = node.getTable().getItemDouble(row, "CHECK_QTY");
            node.getTable().getDataStore().setItem(row, "PLAN_SUM",
                StringTool.round(price * qty, 2));
            node.getTable().getDataStore().setItem(row, "STOCK_PRICE", price);
            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);
            return false;
        }
        return false;
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     *
     * @param parm
     */
    public void appendTableRow(Object obj) {
        TParm result = new TParm( (Map) obj);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            Object object = new Object();
            TTable table = getTable("TABLE_D");
            for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
                int row = table.addRow();
                table.setItem(row, "ORDER_CODE", addParm.getData("ORDER_CODE",
                    i));
                table.setItem(row, "SPECIFICATION", addParm.getData(
                    "SPECIFICATION", i));
                table.setItem(row, "PURCH_UNIT", addParm.getData("PURCH_UNIT",
                    i));
                object = addParm.getData("BUY_QTY", i);
                table.setItem(row, "LASTPUR_QTY", object);
                object = addParm.getData("SELL_QTY", i);
                table.setItem(row, "LASTCON_QTY", object);
                object = addParm.getData("STOCK_QTY", i);
                table.setItem(row, "STOCK_QTY",
                              addParm.getDouble("STOCK_QTY", i));
                table.setItem(row, "PLAN_QTY", addParm.getData("E_QTY", i));
                table.setItem(row, "PUR_QTY", addParm.getData("E_QTY", i));
                table.setItem(row, "CHECK_QTY", addParm.getData("E_QTY", i));
                table.setItem(row, "STOCK_PRICE", addParm.getData(
                    "CONTRACT_PRICE", i));
                table.setItem(row, "SUP_CODE", addParm.getData("SUP_CODE", i));
                table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
                table.setItem(row, "SAFE_QTY", addParm.getData("SAFE_QTY", i));
                table.setItem(row, "MAX_QTY", addParm.getData("MAX_QTY", i));
                table.setItem(row, "BUY_UNRECEIVE_QTY", addParm.getData(
                    "BUY_UNRECEIVE_QTY", i));
                table.setItem(row, "START_DATE", addParm.getData("START_DATE",
                    i));
                table.setItem(row, "END_DATE", addParm.getData("END_DATE", i));
                table.getDataStore().setItem(row, "SEQ",
                                             getMaxSeq(table.getDataStore(),
                    "SEQ"));
            }
            table.setDSValue();
            table.setSelectedRow(0);
            onTableDClicked();
        }
        double sum_money = getSumMoney();
        this.setValue("SUM_TOTIL", sum_money);
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ȡ��Ȩ��
        if (!this.getPopedem("deptAll")) {
            dept_popedom = false;
        }
        if (!this.getPopedem("EXCERPT")) {
            excerpt_popedom = false;
        }
        if (!this.getPopedem("PLAN_QTY")) {
            plan_qty_popedom = false;
        }
        if (!this.getPopedem("DELETE")) {
            delete_popedom = false;
        }
        if (!this.getPopedem("PLAN_FLG")) {
            plan_flg_popedom = false;
        }
        if (!this.getPopedem("PUR_QTY")) {
            pur_qty_popedom = false;
        }
        if (!this.getPopedem("PUR_FLG")) {
            pur_flg_popedom = false;
        }
        if (!this.getPopedem("CHECK_QTY")) {
            check_qty_popedom = false;
        }
        if (!this.getPopedem("CHECK_FLG")) {
            check_flg_popedom = false;
        }
        if (!this.getPopedem("PLANEND_FLG")) {
            planend_flg_popedom = false;
        }

        // ��ʼ��ҳ��״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        ( (TMenuItem) getComponent("make")).setEnabled(false);
        Timestamp date = SystemTool.getInstance().getDate();
        setValue("PLAN_MONTH", date);
        parm_M = new TParm();
        action = "insertM";

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");

        // Ȩ�޿���
        check_dept_popedom();
        check_excerpt_popedom();
        check_qty_popedom();
        check_select_flg_popedom();
        check_delete_popedom();
    }

    /**
     * ������Ȩ��
     */
    private void check_dept_popedom() {
        // û��ȫԺҩ�ⲿ��Ȩ�ޣ�ֻ��ʾ��������
        if (!dept_popedom) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
        }
    }

    /**
     * �������Ȩ��
     */
    private void check_excerpt_popedom() {
        // û���������ɲɹ��ƻ���Ȩ��
        if (!excerpt_popedom) {
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
    }

    /**
     * ���������Ȩ��
     */
    private void check_qty_popedom() {
        // ����������Ȩ��,�����޸���Ȩ��,ȷ����Ȩ��
        if (plan_qty_popedom && pur_qty_popedom && check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,10,11,12,13,14,15,16,17,18");
        }
        // ����������Ȩ��,�����޸���Ȩ��
        else if (plan_qty_popedom && pur_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,9,10,11,12,13,14,15,16,17,18");
        }
        // ����������Ȩ��,ȷ����Ȩ��
        else if (plan_qty_popedom && check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,8,10,11,12,13,14,15,16,17,18");
        }
        // �����޸���Ȩ��,ȷ����Ȩ��
        else if (pur_qty_popedom && check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,7,10,11,12,13,14,15,16,17,18");
        } // ����������Ȩ��
        else if (plan_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18");
        }
        // �����޸���Ȩ��
        else if (pur_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,7,9,10,11,12,13,14,15,16,17,18");
        }
        // ȷ����Ȩ��
        else if (check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18");
        }
        // ȫû��
        else {
            getTable("TABLE_D").setLockColumns("all");
        }
    }

    /**
     * ��鹴ѡȨ��
     */
    private void check_select_flg_popedom() {
        if (!plan_flg_popedom) {
            getCheckBox("PLAN_FLG").setEnabled(false);
        }
        if (!pur_flg_popedom) {
            getCheckBox("PUR_FLG").setEnabled(false);
        }
        if (!check_flg_popedom) {
            getCheckBox("CHECK_FLG").setEnabled(false);
        }
        if (!planend_flg_popedom) {
            getCheckBox("PLANEND_FLG").setEnabled(false);
        }
    }

    /**
     * ���ɾ��Ȩ��
     */
    private void check_delete_popedom() {
        // û��ɾ���ƻ���Ȩ��
        if (!delete_popedom) {
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("�ƻ����Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("PLAN_MONTH"))) {
            this.messageBox("�ƻ��·ݲ���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("PLAN_DESC"))) {
            this.messageBox("�ƻ����Ʋ���Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ������������ж���״̬
     *
     * @param parm
     * @return
     */
    private TParm CheckStatus(TParm parm) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (!"".equals(parm.getValue("PLANEND_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "�ƻ����");
            }
            else if (!"".equals(parm.getValue("CHECK_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "���ȷ��");
            }
            else if (!"".equals(parm.getValue("PUR_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "�ɹ������");
            }
            else if (!"".equals(parm.getValue("PLAN_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "�ƻ��༭���");
            }
            else {
                parm.setData("PLAN_STATUS", i, "�ƻ���");
            }
        }
        return parm;
    }

    /**
     * ����״̬��ѡ״̬��ѡ��(CHECKBOX)
     *
     * @param status
     */
    private void setPlanStatus(String plan_status) {
        int status = 0;
        ( (TMenuItem) getComponent("make")).setEnabled(false);
        if ("�ƻ����".equals(plan_status)) {
            status = 0;
            ( (TMenuItem) getComponent("make")).setEnabled(true);
        }
        else if ("���ȷ��".equals(plan_status)) {
            status = 1;
        }
        else if ("�ɹ������".equals(plan_status)) {
            status = 2;
        }
        else if ("�ƻ��༭���".equals(plan_status)) {
            status = 3;
        }
        else {
            status = 4;
        }
        // ���״̬
        clearValue("PLANEND_FLG;CHECK_FLG;PUR_FLG;PLAN_FLG");
        // ����״̬
        switch (status) {
            case 0:
                setValue("PLANEND_FLG", "Y");
            case 1:
                setValue("CHECK_FLG", "Y");
            case 2:
                setValue("PUR_FLG", "Y");
            case 3:
                setValue("PLAN_FLG", "Y");
            default:
                break;
        }
    }

    /**
     * �����ܽ��
     *
     * @return
     */
    private double getSumMoney() {
        TTable table = getTable("TABLE_D");
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            sum += table.getItemDouble(i, "PUR_QTY")
                * table.getItemDouble(i, "STOCK_PRICE");
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
}
