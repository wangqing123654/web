package com.javahis.ui.ind;

import jdo.ind.IndVerifyinDTool;
import jdo.ind.IndVerifyinMTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: ����δ�˻���ϸControl
 * </p>
 *
 * <p>
 * Description: ����δ�˻���ϸControl
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

public class INDUnRegressgoodsControl
    extends TControl {

    // �������
    private TParm parm;
    // �������ݼ���
    private TParm resultParm;

    private String org_code;

    private String sup_code;

    public INDUnRegressgoodsControl() {
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
            org_code = parm.getValue("ORG_CODE");
            sup_code = parm.getValue("SUP_CODE");
        }
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ���������
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("SUP_CODE", sup_code);
        if (!"".equals(getValueString("VERIFYIN_NO"))) {
            parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
        }
        if (!"".equals(getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
        }
        if (!"".equals(getValueString("START_CHECK_DATE"))
            && !"".equals(getValueString("END_CHECK_DATE"))) {
            if (TypeTool.getTimestamp(getValue("START_CHECK_DATE")).compareTo(
                TypeTool.getTimestamp(getValue("END_CHECK_DATE"))) > 0) {
                this.messageBox("��ʼʱ�䲻�����ڽ���ʱ��");
                return;
            }
            parm.setData("START_CHECK_DATE", getValue("START_CHECK_DATE"));
            parm.setData("END_CHECK_DATE", getValue("END_CHECK_DATE"));
        }
        TParm result = new TParm();
        result = IndVerifyinDTool.getInstance().onQueryVerifyinDone(parm);
        if (result.getCount() == 0) {
            this.messageBox("��������");
            return;
        }

        TTable table = getTable("TABLE");
        table.setParmValue(result);
        resultParm = result;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr =
            "VERIFYIN_NO;ORDER_CODE;ORDER_DESC;SELECT_ALL;START_CHECK_DATE;"
            + "END_CHECK_DATE";
        this.clearValue(clearStr);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_CHECK_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_CHECK_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        getTable("TABLE").removeRowAll();
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
        TParm result = resultParm;
        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
                result.removeRow(i);
            }
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�д�������");
            return;
        }
        setReturnValue(result);
        this.closeWindow();
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
     * ��ʼ��������
     */
    private void initPage() {
        // ҳ�渳ֵ
        setValue("SUP_CODE", sup_code);
        // ���ݹ�Ӧ�̺Ϳ��Ҳ�ѯ��������յ���
        TParm parm = IndVerifyinMTool.getInstance().onQueryDoneVerByOrgAndSup(
            org_code, sup_code);
        getComboBox("VERIFYIN_NO").setParmValue(parm);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_CHECK_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_CHECK_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        TParm parmIn = new TParm();
        parmIn.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE")
            .setPopupMenuParameter(
                "UD",
                getConfigParm().newConfig(
                    "%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);

        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        resultParm = new TParm();
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

}
