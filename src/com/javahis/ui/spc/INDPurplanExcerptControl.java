package com.javahis.ui.spc;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.RunClass;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: �ɹ��ƻ�����Control
 * </p>
 *
 * <p>
 * Description: �ɹ��ƻ�����Control
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
 * @author zhangy 2009.05.08
 * @version 1.0
 */

public class INDPurplanExcerptControl
    extends TControl {

    private TParm parm;
    private String org_code;
    private Object plan_control;

    public INDPurplanExcerptControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            Object control = parm.getData("CONTROL");
            if (control != null) {
                plan_control = control;
            }
            org_code = parm.getValue("ORG_CODE");
        }
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        getTable("TABLE").removeRowAll();
        TParm inParm = new TParm();
        inParm.setData("START_DATE", getValue("START_DATE"));
        inParm.setData("END_DATE", getValue("END_DATE"));
        inParm.setData("ORG_CODE", org_code);
        TParm result = new TParm();
        boolean flg = getCheckBox("CHECK_SAFE").isSelected();
        String order_code = getValueString("ORDER_CODE");
        // ҩƷ����
        if (!"".equals(getValueString("ORDER_TYPE"))) {
            inParm.setData("TYPE_CODE", getValueString("ORDER_TYPE"));
        }
        // ��λ
        if (!"".equals(getValueString("MATERIAL_LOC_CODE"))) {
            inParm.setData("MATERIAL_LOC_CODE",
                           getValueString("MATERIAL_LOC_CODE"));
        }
        // ����ҩƷ��ѯ
        if (getRadioButton("GROUP_ORDER").isSelected()) {
            if (flg && !"".equals(order_code)) {
                // ��ȫ���������ָ��ҩƷ
                inParm.setData("CHECK_SAFE", "Y");
                inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
            }
            else if (flg) {
                // ��ȫ�����
                inParm.setData("CHECK_SAFE", "Y");
            }
            else if (!"".equals(order_code)) {
                // ָ��ҩƷ
                inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
            }
        }
        else {
            // �����������̲�ѯ
            inParm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        result = TIOM_AppServer.executeAction("action.ind.INDPurPlanAction",
                                              "onQueryExcerptByOrder", inParm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        TTable table = getTable("TABLE");
        table.setParmValue(result);
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        TParm result_table = table.getParmValue();
        TParm result = new TParm();
        for (int i = 0; i < result_table.getCount(); i++) {
            result.setRowData(i, result_table.getRow(i));
            result.setData("START_DATE", i, getValue("START_DATE"));
            result.setData("END_DATE", i, getValue("END_DATE"));
        }
        for (int i = result.getCount("ORDER_CODE") - 1; i >= 0; i--) {
            if ("N".equals(result.getValue("SELECT_FLG", i))) {
                result.removeRow(i);
                continue;
            }
        }
        if (result == null) {
            this.messageBox("û�д�������");
            return;
        }
        String[] method = new String[] {
            "appendTableRow"};
        Object[] value = new Object[] {
            result.getData()};
        RunClass.invokeMethodT(plan_control, method, value);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ��ʼ��ͳ������
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��(���µĵ�һ��)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" +
            TypeTool.getString(date).
            substring(5, 7) +
            "/01 00:00:00",
            "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // ��ʼʱ��(�ϸ��µ�һ��)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
        getRadioButton("GROUP_ORDER").setSelected(true);
        getCheckBox("CHECK_SAFE").setSelected(false);
        getCheckBox("CHECK_SAFE").setEnabled(true);
        getCheckBox("CHECK_ORDER").setSelected(false);
        getCheckBox("CHECK_ORDER").setEnabled(true);
        setValue("ORDER_CODE", "");
        getTextField("ORDER_CODE").setEnabled(false);
        setValue("ORDER_DESC", "");
        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");
        getTextFormat("SUP_CODE").setEnabled(false);
        getCheckBox("SELECT_ALL").setSelected(false);
        getComboBox("ORDER_TYPE").setSelectedIndex( -1);
        getComboBox("MATERIAL_LOC_CODE").setSelectedIndex( -1);
        setValue("SUM_MONEY", 0);
        getTable("TABLE").removeRowAll();
    }

    /**
     * ���(TABLE)ֵ�ı��¼�
     */
    public boolean onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        TTable table = node.getTable();
        int row = node.getRow();
        int column = node.getColumn();
        if (column == 9) {
            double count = TypeTool.getDouble(node.getValue());
            double price = TypeTool.getDouble(table.getValueAt(row, 10));
            table.setItem(row, "TOT_MONEY", StringTool.round(count * price, 2));
            if ("Y".equals(table.getItemString(row, "SELECT_FLG"))) {
                if (count <= 0) {
                    table.setItem(row, "SELECT_FLG", "N");
                }
                // �����ܼ�Ǯ
                double sum = getSumMoney();
                this.setValue("SUM_MONEY", sum);
            }
            return false;
        }
        if (column == 10) {
            double price = TypeTool.getDouble(node.getValue());
            double count = TypeTool.getDouble(table.getValueAt(row, 9));
            table.setItem(row, "TOT_MONEY", StringTool.round(count * price, 2));
            if ("Y".equals(table.getItemString(row, "SELECT_FLG"))) {
                if (price <= 0) {
                    table.setItem(row, "SELECT_FLG", "N");
                }
                // �����ܼ�Ǯ
                double sum = getSumMoney();
                this.setValue("SUM_MONEY", sum);
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
        int row = tableDown.getSelectedRow();
        // ��鱻ѡ����
//        if (tableDown.getItemDouble(row, "E_QTY") <= 0) {
//            tableDown.setItem(row, "SELECT_FLG", "N");
//            this.messageBox("�ƻ�����������С��0");
//            return;
//        }
        if (tableDown.getItemDouble(row, "CONTRACT_PRICE") <= 0) {
            tableDown.setItem(row, "SELECT_FLG", "N");
            this.messageBox("�۸���С�ڵ���0");
            return;
        }
        this.setValue("SUM_MONEY", getSumMoney());
    }

    /**
     * ��ѡ��ť�ı��¼�
     */
    public void onRadioButtonChange() {
        TRadioButton group_order = getRadioButton("GROUP_ORDER");
        if (group_order.isSelected()) {
            getCheckBox("CHECK_SAFE").setSelected(false);
            getCheckBox("CHECK_SAFE").setEnabled(true);
            getCheckBox("CHECK_ORDER").setSelected(false);
            getCheckBox("CHECK_ORDER").setEnabled(true);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
            getTextFormat("SUP_CODE").setValue(null);
            getTextFormat("SUP_CODE").setText("");
            getTextFormat("SUP_CODE").setEnabled(false);
        }
        else {
            getCheckBox("CHECK_SAFE").setSelected(false);
            getCheckBox("CHECK_SAFE").setEnabled(false);
            getCheckBox("CHECK_ORDER").setSelected(false);
            getCheckBox("CHECK_ORDER").setEnabled(false);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
            getTextFormat("SUP_CODE").setValue(null);
            getTextFormat("SUP_CODE").setText("");
            getTextFormat("SUP_CODE").setEnabled(true);
            getTextField("ORDER_CODE").setEnabled(false);
        }
    }

    /**
     * ָ��ҩƷ��ѡ��ѡ���¼�
     */
    public void onCheckOrderSelect() {
        if (getCheckBox("CHECK_ORDER").isSelected()) {
            getTextField("ORDER_CODE").setEnabled(true);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
        }
        else {
            getTextField("ORDER_CODE").setEnabled(false);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
        }
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (!getCheckData()) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "Y");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "N");
                this.setValue("SUM_MONEY", 0);
            }
        }
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
     * TABLE˫���¼�
     */
    public void onTableDoubleClicked() {
        TTable table = getTable("TABLE");
        int column = table.getSelectedColumn();
        if (column == 11) {
            // �򿪹����̲�ѯ����
            // Object parm = openDialog("%ROOT%\\config\\ind\\INDMacValid.x",
            // getValueString("ORG_CODE"));
            // if (parm != null) {
            // parm = (String) parm;
            // if ("".equals(parm)) {
            // return;
            // }
            // int row = table.getSelectedRow();
            // table.setItem(row, column, parm);
            // }
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��������¼�
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter(
            "UD", getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        // ��ʼ��ͳ������
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��(���µĵ�һ��)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" +
            TypeTool.getString(date).
            substring(5, 7) +
            "/01 00:00:00",
            "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // ��ʼʱ��(�ϸ��µ�һ��)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
        // ��ʼ����λ
        TComboINDMaterialloc mac = (TComboINDMaterialloc) getComponent(
            "MATERIAL_LOC_CODE");
        mac.setOrgCode(org_code);
        mac.onQuery();
    }

    /**
     * �����ܼ�Ǯ
     *
     * @return
     */
    private double getSumMoney() {
        TTable table = getTable("TABLE");
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                sum += table.getItemDouble(i, "TOT_MONEY");
            }
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ���������ȷ��
     *
     * @return
     */
    private boolean getCheckData() {
        TTable table = getTable("TABLE");
        table.acceptText();
        for (int i = 0; i < table.getRowCount(); i++) {
//            if (table.getItemDouble(i, "E_QTY") <= 0) {
//                this.messageBox("�ƻ�����������С��0");
//                return false;
//            }
            if (table.getItemDouble(i, "CONTRACT_PRICE") <= 0) {
                this.messageBox("�۸���С�ڵ���0");
                return false;
            }
        }
        return true;
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
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
