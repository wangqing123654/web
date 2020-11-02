package com.javahis.ui.spc;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.pha.PhaTransUnitTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: �����̴����Ʒ�趨
 * </p>
 *
 * <p>
 * Description: �����̴����Ʒ�趨
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class INDAgentControl
    extends TControl {

    private String action = "insert";

    public INDAgentControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ע�ἤ��SYSFeePopup�������¼�
        callFunction("UI|ORDER_CODE|addEventListener", "ORDER_CODE->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD");
        // ��ʼ��������					
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        String sup = getValueString("SUP_CODE");
        String order = getValueString("ORDER_CODE");
        if ("".equals(sup) && "".equals(order)) {
            this.messageBox("��ѡ���ѯ���̻�ҩƷ");
            return;
        }
        if (sup.length() > 0) {
            parm.setData("SUP_CODE", sup);
        }
        if (order.length() > 0) {
            parm.setData("ORDER_CODE", order);
        }
        TParm result = TIOM_AppServer.executeAction(
            "action.spc.INDAgentAction", "onQuery", parm);
        getTable("TABLE").setParmValue(result);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
        parm.setData("MAIN_FLG", getValueString("MAIN_FLG"));
        parm.setData("CONTRACT_NO", getValueString("CONTRACT_NO"));
        parm.setData("CONTRACT_PRICE", getValueDouble("CONTRACT_PRICE"));
        TParm dosageQtyResult = PhaTransUnitTool.getInstance().queryForAmount(parm);   
        double dosageQty = dosageQtyResult.getDouble("DOSAGE_QTY",0);				         
        double lastVerifyinPrice = getValueDouble("CONTRACT_PRICE")/dosageQty;      
        DecimalFormat df = new DecimalFormat("0.0000"); 
        double num=0.0000;						
        parm.setData("LAST_VERIFY_PRICE",Double.parseDouble(df.format(lastVerifyinPrice)));
        parm.setData("OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        if ("insert".equals(action)) {
            onQuery();
            if (getTable("TABLE").getRowCount() > 0) {
                this.messageBox("�����Ѵ���,��������");
                return;
            }
            result = TIOM_AppServer.executeAction("action.spc.INDAgentAction",
                                                  "onInsert", parm);
        }
        else {
            result = TIOM_AppServer.executeAction("action.spc.INDAgentAction",
                                                  "onUpdate", parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        getTable("TABLE").setSelectionMode(0);
       ((TNumberTextField)this.getComponent("LAST_VERIFY_PRICE")).setValue(df.format(lastVerifyinPrice));
        onQuery();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("SUP_CODE", TypeTool.getString(table
                .getValueAt(row, 0)));
            parm.setData("ORDER_CODE", TypeTool.getString(table.getValueAt(row,
                1)));
            TParm result = new TParm();
            result = TIOM_AppServer.executeAction("action.ind.INDAgentAction",
                                                  "onDelete", parm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ��");
                return;
            }
            table.removeRow(row);
            table.setSelectionMode(0);
            this.messageBox("ɾ���ɹ�");
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
        action = "insert";
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "SUP_CODE;ORDER_CODE;ORDER_DESC;MAIN_FLG;CONTRACT_NO;CONTRACT_PRICE;PURCH_UNIT;"
            + "LAST_ORDER_DATE;LAST_ORDER_QTY;LAST_ORDER_NO;LAST_VERIFY_DATE;LAST_VERIFY_PRICE";
        this.clearValue(clear);
        // ��Ӧ����
        TTextFormat sup_code = this.getTextFormat("SUP_CODE");
        sup_code.setEnabled(true);
        // ҩƷ����
        TTextField order_code = getTextField("ORDER_CODE");
        order_code.setEnabled(true);
        // ����ҳ��״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        getTable("TABLE").setSelectionMode(0);
        action = "insert";
    }

    /**
     * ���(CLNDIAG_TABLE)�����¼�
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            // SUP_CODE
            String sup_code = TypeTool.getString(table.getValueAt(row, 0));
            getTextFormat("SUP_CODE").setValue(sup_code);
            getTextFormat("SUP_CODE").setEnabled(false);
            // ORDER_CODE
            String order_code = TypeTool.getString(table.getValueAt(row, 1));
            getTextField("ORDER_CODE").setValue(order_code);
            getTextField("ORDER_CODE").setEnabled(false);
            // ORDER_DESC
            String order_desc = TypeTool.getString(table.getValueAt(row, 2));
            getTextField("ORDER_DESC").setValue(order_desc);
            // MAIN_FLG
            String main_flg = TypeTool.getString(table.getValueAt(row, 3));
            this.setValue("MAIN_FLG", main_flg);
            // CONTRACT_NO
            String contract_no = TypeTool.getString(table.getValueAt(row, 4));
            this.setValue("CONTRACT_NO", contract_no);
            // CONTRACT_PRICE
            double contract_price = TypeTool
                .getDouble(table.getValueAt(row, 5));
            this.setValue("CONTRACT_PRICE", contract_price);
            // PURCH_UNIT
            String purch_unit = TypeTool.getString(table.getValueAt(row, 6));
            this.setValue("PURCH_UNIT", purch_unit);
            // LAST_ORDER_DATE
            Timestamp last_order_date = TypeTool.getTimestamp(table.getValueAt(
                row, 7));
            this.setValue("LAST_ORDER_DATE", last_order_date);
            // LAST_ORDER_QTY
            int last_order_qty = TypeTool.getInt(table.getValueAt(row, 8));
            this.setValue("LAST_ORDER_QTY", last_order_qty);
            // LAST_ORDER_PRICE
            double lasr_order__price = TypeTool.getDouble(table.getValueAt(row,
                9));
            this.setValue("LAST_ORDER_PRICE", lasr_order__price);
            // LAST_ORDER_NO
            String lasr_order_no = TypeTool
                .getString(table.getValueAt(row, 10));
            this.setValue("LAST_ORDER_NO", lasr_order_no);
            // LAST_VERIFY_DATE
            Timestamp last_verify_date = TypeTool.getTimestamp(table
                .getValueAt(row, 11));
            this.setValue("LAST_VERIFY_DATE", last_verify_date);
            // LAST_VERIFY_PRICE
            double lasr_verify__price = TypeTool.getDouble(table.getValueAt(
                row, 12)); //by liyh 20120914 ���һ�����յ��� ȡ�ĵ�Ԫ�񲻶�
            this.setValue("LAST_VERIFY_PRICE", lasr_verify__price);
            action = "update";
        }
    }

    /**
     * ҩƷ����س��¼�
     */
    public void onOrderCodeAction() {
        String order = getValueString("ORDER_CODE");
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", order);
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDAgentAction", "onQueryUnit", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("��ҩƷ�����ڹ��뵥λ");
            return;
        }
        this.setValue("PURCH_UNIT", result.getValue("UNIT_CHN_DESC", 0));
    }

    /**
     * ��TextField�����༭�ؼ�ʱ����
     *
     * @param com
     */
    public void onCreateEditComoponentUD(KeyEvent obj) {
        TTextField textFilter = getTextField("ORDER_CODE");
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
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
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
        onOrderCodeAction();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // Ȩ�޿���

    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("��Ӧ�̴��벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("ORDER_CODE"))) {
            this.messageBox("ҩƷ���벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("ORDER_DESC"))) {
            this.messageBox("ҩƷ���Ʋ���ȷ");
            return false;
        }
        return true;
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
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

}
