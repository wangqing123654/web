package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TNull;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SYSFeeReadjustMTool;
import jdo.sys.SYSFeeReadjustDTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.awt.Component;
import com.javahis.util.StringUtil;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTabbedPane;
import jdo.util.Manager;

/**
 * <p>
 * Title:���ۼƻ�
 * </p>
 *
 * <p>
 * Description:���ۼƻ�
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.9.18
 * @version 1.0
 */
public class SYSFeeReadjustControl
    extends TControl {

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    private TPanel panel_1;

    private String action = "insertM";

    // ���Ȩ��
    private boolean check_flg = true;


    public SYSFeeReadjustControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ע�ἤ��SYS_FEE�������¼�
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        // ��ʼ����������
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        TParm result = new TParm();
        //this.messageBox(action);
        if ("insertM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            TParm parm = new TParm();
            TNull tnull = new TNull(Timestamp.class);
            Timestamp date = SystemTool.getInstance().getDate();
            String rpp_code = SystemTool.getInstance().getNo("ALL", "PUB",
                "SYS_FEE_READJUST", "No");
            parm.setData("RPP_CODE", rpp_code);
            parm.setData("RPP_DESC", this.getValue("RPP_DESC"));
            parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
            parm.setData("RPP_USER", this.getValue("RPP_USER"));
            parm.setData("RPP_DATE", this.getValue("RPP_DATE"));
            parm.setData("CHK_USER", "");
            parm.setData("CHK_DATE", tnull);
            parm.setData("READJUST_DATE", this.getValue("READJUST_DATE"));
            parm.setData("READJUSTOP_DATE", tnull);
            parm.setData("RPP_STATUS", "0");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());

            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onInsertM", parm);
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            //onClear();
            this.onQuery();
        }
        else if ("updateM".equals(action)) {
            //this.messageBox("");
            if (!CheckDataM()) {
                return;
            }
            TParm parm = new TParm();
            TNull tnull = new TNull(Timestamp.class);
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
            parm.setData("RPP_DESC", this.getValue("RPP_DESC"));
            parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
            parm.setData("RPP_USER", this.getValue("RPP_USER"));
            parm.setData("RPP_DATE", this.getValue("RPP_DATE"));
            if (this.getCheckBox("CHECK_FLG").isSelected()) {
                parm.setData("CHK_USER", this.getValueString("CHK_USER"));
                parm.setData("CHK_DATE", date);
                parm.setData("RPP_STATUS", "1");
            }
            else {
                parm.setData("CHK_USER", "");
                parm.setData("CHK_DATE", tnull);
                parm.setData("RPP_STATUS", "0");
            }
            parm.setData("READJUST_DATE", this.getValue("READJUST_DATE"));
            parm.setData("READJUSTOP_DATE", tnull);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("START_DATE", this.getValue("READJUST_DATE"));
            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onUpdateM", parm);
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            //onClear();
            this.onQuery();
        }
        else if ("updateD".equals(action)) {
            TParm parm = new TParm();
            parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
            parm.setData("PARM_DATA", table_d.getParmValue().getData());
            parm.setData("OPT_USER", Operator.getID());
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onUpdateD", parm);
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            //onClear();
            this.onQuery();
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (this.getRadioButton("UPDATE_A").isSelected()) {
            parm.setData("RPP_STATUS", "0");
        }
        else if (this.getRadioButton("UPDATE_B").isSelected()) {
            parm.setData("RPP_STATUS", "1");
        }
        else if (this.getRadioButton("UPDATE_C").isSelected()) {
            parm.setData("RPP_STATUS", "2");
        }
        if (!"".equals(this.getValueString("RPP_CODE"))) {
            parm.setData("RPP_CODE", this.getValue("RPP_CODE"));
        }
        if (!"".equals(this.getValueString("RPP_DESC"))) {
            parm.setData("RPP_DESC", this.getValue("RPP_DESC"));
        }
//        if (!"".equals(this.getValueString("RPP_USER"))) {
//            parm.setData("RPP_USER", this.getValue("RPP_USER"));
//        }
//        if (!"".equals(this.getValueString("CHK_USER"))) {
//            parm.setData("CHK_USER", this.getValue("CHK_USER"));
//        }
        TParm result = SYSFeeReadjustMTool.getInstance().onQuery(parm);
        //System.out.println("result:" + result);
        if (result == null || result.getCount() == 0) {
            table_m.removeRowAll();
            table_m.setSelectionMode(0);
            table_d.removeRowAll();
            table_d.setSelectionMode(0);
            this.messageBox("û�в�ѯ����");
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������Page1
        String clearStringPage1 =
            "START_DATE;END_DATE;RPP_CODE;RPP_DESC;READJUST_DATE;"
            + "CHECK_FLG;RPP_DATE;CHK_DATE;DESCRIPTION";
        clearValue(clearStringPage1);
        // ��ʼ������ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.getRadioButton("UPDATE_A").setSelected(true);
        this.getTextField("RPP_CODE").setEnabled(true);
        this.setValue("READJUST_DATE",
                      StringTool.rollDate(date, 1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("RPP_DATE", date);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("create")).setEnabled(false);
        // ��ʼ��ҳ��״̬������
        table_m.removeRowAll();
        table_m.setSelectionMode(0);
        table_d.removeRowAll();
        table_d.setSelectionMode(0);

        ( (TTabbedPane)this.getComponent("tTabbedPane_0")).setSelectedIndex(0);
        setPanelStatus(true);
        if (check_flg) {
            this.setValue("CHK_DATE", date);
            //getComboBox("CHK_USER").setEnabled(true);
            this.getTextFormat("CHK_DATE").setEnabled(true);
        }
        else {
            getComboBox("CHK_USER").setEnabled(false);
            this.getTextFormat("CHK_DATE").setEnabled(false);
        }
        action = "insertM";
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (!this.getRadioButton("UPDATE_A").isSelected()) {
            this.messageBox("���ۼƻ�����˻�����ɣ�����ɾ��");
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
        if (panel_1.isShowing()) {
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����ۼƻ�", 2) == 0) {
                result = TIOM_AppServer.executeAction(
                    "action.sys.SYSFeeReadjustAction", "onDeleteM", parm);
                // �����ж�
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                this.messageBox("ɾ���ɹ�");
                this.onClear();
            }
        }
        else {
            int row = table_d.getSelectedRow();
            if (table_d.getParmValue().getDouble("SEQ_NO", row) == -1) {
                table_d.removeRow(row);
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����ۼƻ�ϸ��", 2) == 0) {
                    parm.setData("SEQ_NO",
                                 table_d.getParmValue().getDouble("SEQ_NO", row));
                    result = SYSFeeReadjustDTool.getInstance().onDelete(parm);
                    table_d.removeRow(row);
                    // ɾ���ж�
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    this.messageBox("ɾ���ɹ�");
                }
            }
        }
    }

    /**
     * ִ�з���
     */
    public void onCreate() {
        if (this.getRadioButton("UPDATE_A").isSelected()) {
            this.messageBox("�ƻ���δ���,�޷�ִ��");
            return;
        }
        else if (this.getRadioButton("UPDATE_C").isSelected()) {
            this.messageBox("�ƻ�����ִ��");
            return;
        }
        else if (this.getRadioButton("UPDATE_B").isSelected()) {
            Timestamp date = SystemTool.getInstance().getDate();
            if (TypeTool.getTimestamp(this.getValue("READJUST_DATE")).compareTo(
                date) <= 0) {
                this.messageBox("����ʱ�䲻��С�ڵ��ڵ�ǰʱ��");
                return;
            }

            TParm result = new TParm();
            TParm parm = new TParm();
            parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
            //System.out.println("---"+table_d.getParmValue().getData());
            parm.setData("PARM_DATA", table_d.getParmValue().getData());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onCreateSYSFeeReadjust",
                parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("ִ��ʧ��");
                return;
            }
            this.messageBox("ִ�гɹ�");
            onClear();
        }
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        TParm tableParm = table_d.getParmValue();
        if (tableParm == null) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "���ۼƻ���");
        date.setData("READJUST_DATE", "TEXT", "����ʱ��: " +
                     this.getValueString("READJUST_DATE").substring(0,
                     19).replace("-", "/"));
        // �������
        TParm parm = new TParm();

        for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
            if ("".equals(tableParm.getValue("ORDER_CODE", i))) {
                continue;
            }
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("START_DATE",
                         tableParm.getValue("START_DATE", i).substring(0, 19).
                         replace("-", "/"));
            parm.addData("END_DATE",
                         tableParm.getValue("END_DATE", i).substring(0, 19).
                         replace("-", "/"));
            parm.addData("OWN_PRICE_NEW", tableParm.getValue("OWN_PRICE_NEW", i));
            parm.addData("OWN_PRICE2_NEW", tableParm.getValue("OWN_PRICE2_NEW", i));
            parm.addData("OWN_PRICE3_NEW", tableParm.getValue("OWN_PRICE3_NEW", i));
            parm.addData("OWN_PRICE_OLD", tableParm.getValue("OWN_PRICE_OLD", i));
            parm.addData("OWN_PRICE2_OLD", tableParm.getValue("OWN_PRICE2_OLD", i));
            parm.addData("OWN_PRICE3_OLD", tableParm.getValue("OWN_PRICE3_OLD", i));
        }
        if (parm.getCount("ORDER_DESC") == 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "START_DATE");
        parm.addData("SYSTEM", "COLUMNS", "END_DATE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE_NEW");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE2_NEW");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE3_NEW");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE_OLD");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE2_OLD");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE3_OLD");
        date.setData("TABLE", parm.getData());
        // ��β����
        date.setData("PLAN_USER", "TEXT",
                     "�ƻ���Ա:" + this.getComboBox("RPP_USER").getSelectedName());
        date.setData("CHECK_USER", "TEXT",
                     "�����Ա:" + this.getComboBox("CHK_USER").getSelectedName());
        date.setData("USER", "TEXT", "�Ʊ���:" + Operator.getName());
        date.setData("DATE", "TEXT",
                     "��ӡʱ��:" +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace("-", "/"));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\SYS\\SYSFeeReadjust.jhw", date);
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        if (table_m.getSelectedRow() != -1) {
            action = "updateM";
            // ������ѡ���������Ϸ�
            this.setValue("RPP_CODE",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "RPP_CODE"));
            this.setValue("RPP_DESC",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "RPP_DESC"));
            this.setValue("RPP_USER",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "RPP_USER"));
            this.setValue("RPP_DATE",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "RPP_DATE"));
            this.setValue("CHK_USER",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "CHK_USER"));
            this.setValue("CHK_DATE",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "CHK_DATE"));
            this.setValue("READJUST_DATE",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "READJUST_DATE"));
            this.setValue("DESCRIPTION",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "DESCRIPTION"));

            if ("0".equals(table_m.getItemData(table_m.getSelectedRow(),
                                               "RPP_STATUS"))) {
                this.setValue("CHECK_FLG", "N");
            }
            else {
                this.setValue("CHECK_FLG", "Y");
            }
            getTextField("RPP_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        if (table_d.getSelectedRow() != -1) {
            table_m.setSelectionMode(0);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
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
        // Table����
        int column = node.getTable().getSelectedColumn();
        // Table����
        int row = node.getTable().getSelectedRow();
        if (column == 2) {
            Timestamp end_date = TypeTool.getTimestamp(node.getValue());
            if (end_date.compareTo(node.getTable().getItemTimestamp(row,
                "START_DATE")) <= 0) {
                this.messageBox("ʧЧ���ڲ���������Ч����");
                return true;
            }
        }
        if (column == 3) {
            double own_price = TypeTool.getDouble(node.getValue());
            if (own_price <= 0) {
                this.messageBox("���ԷѼ۲���С�ڻ����0");
                return true;
            }
            node.getTable().setItem(row, "OWN_PRICE2_NEW", own_price * 2);
            node.getTable().setItem(row, "OWN_PRICE3_NEW", own_price * 2.5);
        }
        if (column == 4) {
            double nhi_price = TypeTool.getDouble(node.getValue());
            if (nhi_price <= 0) {
                this.messageBox("�¹���۲���С�ڻ����0");
                return true;
            }
            if (nhi_price > node.getTable().getItemDouble(row, "OWN_PRICE3_NEW")) {
                this.messageBox("�¹���۲��ܴ��ڹ���ҽ�Ƽ�");
                return true;
            }
        }
        if (column == 5) {
            double gov_price = TypeTool.getDouble(node.getValue());
            if (gov_price <= 0) {
                this.messageBox("�¹���ҽ�Ƽ۲���С�ڻ����0");
                return true;
            }
        }
        return false;
    }

    /**
     * �������ҳ
     */
    public void onChangeTTabbedPane() {
        if ( ( ( (TTabbedPane)this.getComponent("tTabbedPane_0"))).getSelectedIndex() ==
            0) {
            if (table_m.getSelectedRow() != -1) {
                action = "updateM";
            }
            else {
                action = "insertM";
            }
            setPanelStatus(true);
            ( (TMenuItem) getComponent("create")).setEnabled(false);
        }
        else {
            action = "updateD";
            setPanelStatus(false);
            // ��ϸ��Ϣ
            table_d.setSelectionMode(0);
            String rpp_code = getValueString("RPP_CODE");
            TParm parm = new TParm();
            parm.setData("RPP_CODE", rpp_code);
            TParm result = SYSFeeReadjustDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() == 0) {
                this.messageBox("û��ϸ����Ϣ");
                ( (TMenuItem) getComponent("create")).setEnabled(false);
            }
            else {
                ( (TMenuItem) getComponent("create")).setEnabled(true);
            }
            table_d.setParmValue(result);
            int row = table_d.addRow();
            table_d.setItem(row, "START_DATE",
                            this.getTextFormat("READJUST_DATE").getValue());
            table_d.setItem(row, "END_DATE",
                            StringTool.getTimestamp("99991231235959",
                                                    "yyyyMMddHHmmss"));
            table_d.getParmValue().setData("ORDER_CODE", row, "");
            table_d.getParmValue().setData("SEQ_NO", row, -1);
        }
    }

    /**
     * ��TABLE�����༭�ؼ�ʱ����
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table_d.acceptText();
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            table_d.setItem(table_d.getSelectedRow(), "ORDER_DESC",
                            parm.getValue("ORDER_DESC"));
            table_d.getParmValue().setData("ORDER_CODE", table_d.getSelectedRow(),
                                           order_code);
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE_NEW",
                            parm.getDouble("OWN_PRICE"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE2_NEW",
                            parm.getDouble("OWN_PRICE2"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE3_NEW",
                            parm.getDouble("OWN_PRICE3"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE_OLD",
                            parm.getDouble("OWN_PRICE"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE2_OLD",
                            parm.getDouble("OWN_PRICE2"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE3_OLD",
                            parm.getDouble("OWN_PRICE3"));

            // �������
            int row = table_d.addRow();
            table_d.setItem(row, "START_DATE",
                            this.getTextFormat("READJUST_DATE").getValue());
            table_d.setItem(row, "END_DATE",
                            StringTool.getTimestamp("99991231235959",
                "yyyyMMddHHmmss"));
            table_d.getParmValue().setData("ORDER_CODE", row, "");
            table_d.getParmValue().setData("SEQ_NO", row, -1);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        ( (TMenuItem) getComponent("create")).setEnabled(false);
        // ��ʼ������ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        // ���Ȩ��
        if (!this.getPopedem("checkFlg")) {
            getCheckBox("CHECK_FLG").setEnabled(false);
            //getComboBox("CHK_USER").setEnabled(false);
            this.setValue("CHK_DATE", "");
            getTextFormat("CHK_DATE").setEnabled(false);
            check_flg = false;
        }
        else {
            getCheckBox("CHECK_FLG").setEnabled(true);
            //getComboBox("CHK_USER").setEnabled(true);
            getTextFormat("CHK_DATE").setEnabled(true);
            this.setValue("CHK_DATE", date);
            getComboBox("CHK_USER").setValue(Operator.getID());
        }
        getComboBox("RPP_USER").setValue(Operator.getID());
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("RPP_DATE", date);
        this.setValue("READJUST_DATE",
                      StringTool.rollDate(date, 1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        panel_1 = getPanel("tPanel_6");
    }

    /**
     * ����ҳ��״̬
     * @param flg boolean
     */
    private void setPanelStatus(boolean flg) {
        this.getTextFormat("START_DATE").setEnabled(flg);
        this.getTextFormat("END_DATE").setEnabled(flg);
        this.getRadioButton("UPDATE_A").setEnabled(flg);
        this.getRadioButton("UPDATE_B").setEnabled(flg);
        this.getRadioButton("UPDATE_C").setEnabled(flg);
        this.getTextField("RPP_CODE").setEnabled(flg);
        this.getTextField("RPP_DESC").setEnabled(flg);
        this.getTextFormat("READJUST_DATE").setEnabled(flg);
        //this.getComboBox("RPP_USER").setEnabled(flg);
        this.getTextFormat("RPP_DATE").setEnabled(flg);
        //this.getComboBox("CHK_USER").setEnabled(flg);
        this.getTextFormat("CHK_DATE").setEnabled(flg);
        this.getTextField("DESCRIPTION").setEnabled(flg);
        if (check_flg) {
            this.getCheckBox("CHECK_FLG").setEnabled(check_flg);
        }
        else {
            this.getCheckBox("CHECK_FLG").setEnabled(check_flg);
        }
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("RPP_DESC"))) {
            this.messageBox("�ƻ����Ʋ���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("READJUST_DATE"))) {
            this.messageBox("����ʱ�䲻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("RPP_USER"))) {
            this.messageBox("�ƻ���Ա����Ϊ��");
            return false;
        }
        if ("".equals(getValueString("RPP_DATE"))) {
            this.messageBox("�ƻ�ʱ�䲻��Ϊ��");
            return false;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        if (TypeTool.getTimestamp(this.getValue("READJUST_DATE")).compareTo(
            date) <= 0) {
            this.messageBox("����ʱ�䲻��С�ڵ��ڵ�ǰʱ��");
        }
        if (this.getCheckBox("CHECK_FLG").isSelected()) {
            if ("".equals(getValueString("CHK_USER"))) {
                this.messageBox("�����Ա����Ϊ��");
                return false;
            }
            if ("".equals(getValueString("CHK_DATE"))) {
                this.messageBox("���ʱ�䲻��Ϊ��");
                return false;
            }
        }
        return true;
    }

    /**
     * ��ѡ���ע��
     */
    public void onCheckFlgAction() {
        //TNull tnull = new TNull(Timestamp.class);
        Timestamp date = SystemTool.getInstance().getDate();
        if (this.getCheckBox("CHECK_FLG").isSelected()) {
            this.setValue("CHK_USER", Operator.getID());
            this.setValue("CHK_DATE",date);
        }
        else {
            this.setValue("CHK_USER", "");
            this.setValue("CHK_DATE", "");
        }
    }

    /**
     * �õ�TPanel����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
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
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

}
