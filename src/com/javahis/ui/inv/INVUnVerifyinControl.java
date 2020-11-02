package com.javahis.ui.inv;

import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import jdo.inv.InvPurorderMTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: ���ö���Control
 * </p>
 *
 * <p>
 * Description: ���ö���Control
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */
public class INVUnVerifyinControl
    extends TControl {

    // �������ݼ���
    private TParm resultParm;

    private TParm parm;

    public INVUnVerifyinControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
        }

        // ��ʼ��������
        initPage();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��������ComboBox
        TParm parmNo = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvPurOrderNo(parm.getValue("ORG_CODE"))));
        getComboBox("PURORDER_NO").setParmValue(parmNo);

        TParm parmIn = new TParm();
        parmIn.setData("SUP_CODE", "");
        // ���õ����˵�
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parmIn);
        // ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        resultParm = new TParm();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr =
            "PURORDER_NO;INV_CODE;INV_DESC;SELECT_ALL;SUP_CODE";
        this.clearValue(clearStr);
        getTable("TABLE").removeRowAll();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm inParm = new TParm();
        if ("".equals(getValueString("SUP_CODE"))
            && "".equals(getComboBox("PURORDER_NO").getSelectedID())) {
            this.messageBox("��ѡ��Ӧ���̺Ͷ�����");
            return;
        }
        if (!"".equals(getValueString("SUP_CODE"))
            && "".equals(getComboBox("PURORDER_NO").getSelectedID())) {
            this.messageBox("��ѡ�񶩹���");
            return;
        }
        // ��������
        if (!"".equals(getValueString("SUP_CODE"))) {
            inParm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        // ��������
        if (!"".equals(getComboBox("PURORDER_NO").getSelectedID())) {
            inParm.setData("PURORDER_NO", getComboBox("PURORDER_NO")
                           .getSelectedID());
        }
        // ҩƷ����
        if (!"".equals(getValueString("ORDER_CODE"))) {
            inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
        }
        TParm result = new TParm();
        result = InvPurorderMTool.getInstance().onQueryUnDone(inParm);
        if (result.getCount() == 0) {
            this.messageBox("��������");
            return;
        }
        this.getTable("TABLE").setParmValue(result);
        getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
        resultParm = result;
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
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
        }
    }

    /**
     * ��Ӧ���̸ı��¼�
     */
    public void onSupCodeChange() {
        if (!"".equals(this.getValueString("SUP_CODE"))) {
            TParm parmNo = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvPurOrderNo(parm.getValue("ORG_CODE"),
                                 getValueString("SUP_CODE"))));
            getComboBox("PURORDER_NO").setParmValue(parmNo);
            getComboBox("PURORDER_NO").setSelectedIndex( -1);
        }
    }

    /**
     * �������ı��¼�
     */
    public void onPurOrderChange() {
        if ("".equals(getValueString("SUP_CODE"))) {
            TParm parm = new TParm();
            parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
            TParm result = InvPurorderMTool.getInstance().onQuery(parm);
            getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
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
        String inv_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(inv_code))
            getTextField("INV_CODE").setValue(inv_code);
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc))
            getTextField("INV_DESC").setValue(inv_desc);
    }

    /**
     * ���ط���
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            this.messageBox("û�д�������");
            return;
        }
        TParm result = resultParm;
        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
                result.removeRow(i);
                result.setCount(result.getCount() - 1);
            }
        }
        if (result == null || result.getCount("PURORDER_NO") <= 0) {
            this.messageBox("û�д�������");
            return;
        }
        setReturnValue(result);
        System.out.println("��ѯ����: " + result);
        this.closeWindow();
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

}
