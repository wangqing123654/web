package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ���ʹ�Ӧ���� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVAgentControl extends TControl{
    public INVAgentControl() {
    }

    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("SUP_CODE", "");
        // ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        table = getTable("TABLE");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm == null){
            return;
        }
        String inv_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(inv_code))
            getTextField("INV_CODE").setValue(inv_code);
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc))
            getTextField("INV_CHN_DESC").setValue(inv_desc);
        if (!StringUtil.isNullString("STOCK_UNIT"))
            setValue("STOCK_UNIT", parm.getValue("STOCK_UNIT"));
        if (!StringUtil.isNullString("PURCH_UNIT"))
            setValue("BILL_UNIT", parm.getValue("PURCH_UNIT"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("INV_CODE")) &&
            "".equals(this.getValueString("SUP_CODE"))) {
            this.messageBox("��ѡ���ѯ��Ŀ");
            return;
        }
        table.removeRowAll();
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvAgentBySupOrInv(this.getValueString("SUP_CODE"),
                                  this.getValueString("INV_CODE"))));
        //System.out.println("parm=="+parm);
        if (parm.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        // INV_AGENT��������
        TParm parmAgent = new TParm();
        parmAgent = getParmAgent(parmAgent);
        TParm result = new TParm();
        if (table.getSelectedRow() < 0) {
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVAgentAction", "onInsert", parmAgent);
        }
        else {
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVAgentAction", "onUpdate", parmAgent);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        this.onClear();
    }

    /**
     * INV_AGENT��������
     * @param parm TParm
     * @return TParm
     */
    public TParm getParmAgent(TParm parm) {
        parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        parm.setData("GIFT_RATE", this.getValueDouble("GIFT_RATE"));
        parm.setData("GIFT_QTY", this.getValueDouble("GIFT_QTY"));
        parm.setData("DISCOUNT_RATE", this.getValueDouble("DISCOUNT_RATE"));
        parm.setData("CONTRACT_PRICE", this.getValueDouble("CONTRACT_PRICE"));
        parm.setData("STOCK_UNIT", this.getValueString("STOCK_UNIT"));
        parm.setData("BILL_UNIT", this.getValueString("BILL_UNIT"));
        parm.setData("CONTRACT_NO", this.getValueString("CONTRACT_NO"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
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
        if ("".equals(getValueString("INV_CODE"))) {
            this.messageBox("���ʴ��벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("INV_CHN_DESC"))) {
            this.messageBox("�������Ʋ���ȷ");
            return false;
        }
        if (getValueDouble("CONTRACT_PRICE") <= 0) {
            this.messageBox("�������۲���С�ڻ����0");
            return false;
        }
        return true;
    }


    /**
     * ɾ������
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ɾ����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("INV_CODE", this.getValueString("INV_CODE"));
        parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVAgentAction", "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        this.messageBox("ɾ���ɹ�");
        this.onClear();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "SUP_CODE;INV_CODE;INV_CHN_DESC;GIFT_QTY;GIFT_RATE;"
            + "DISCOUNT_RATE;CONTRACT_NO;CONTRACT_PRICE;BILL_UNIT;STOCK_UNIT;"
            + "LAST_ORDER_DATE;LAST_ORDER_QTY;LAST_ORDER_PRICE;LAST_ORDER_NO;"
            + "LAST_VERIFY_DATE;LAST_VERIFY_PRICE";
        this.clearValue(clearStr);
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextField("INV_CODE").setEnabled(true);
        table.removeRowAll();
        this.setValue("DISCOUNT_RATE", 0);
    }

    /**
     * ���(CLNDIAG_TABLE)�����¼�
     */
    public void onTableClicked() {
        getTextFormat("SUP_CODE").setEnabled(false);
        getTextField("INV_CODE").setEnabled(false);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        String textStr = "SUP_CODE;INV_CODE;INV_CHN_DESC;GIFT_QTY;GIFT_RATE;"
            + "DISCOUNT_RATE;CONTRACT_NO;CONTRACT_PRICE;BILL_UNIT;STOCK_UNIT;"
            + "LAST_ORDER_DATE;LAST_ORDER_QTY;LAST_ORDER_PRICE;LAST_ORDER_NO;"
            + "LAST_VERIFY_DATE;LAST_VERIFY_PRICE";
        this.setValueForParm(textStr, parm);
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
}
