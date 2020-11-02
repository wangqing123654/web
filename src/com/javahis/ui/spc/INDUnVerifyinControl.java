package com.javahis.ui.spc;

import jdo.ind.INDTool;
import jdo.ind.IndPurorderDTool;
import jdo.ind.IndPurorderMTool;
import jdo.ind.IndVerifyinDTool;
    
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
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

public class INDUnVerifyinControl
    extends TControl {

    // �������ݼ���
    private TParm resultParm;

    // ��������
    private String org_code;

    public INDUnVerifyinControl() {
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
        result = IndPurorderMTool.getInstance().onQueryUnDone(inParm);
        if (result.getCount() == 0) {
            this.messageBox("��������");
            return;
        }
        for (int i = result.getCount("ORDER_CODE") - 1; i >= 0; i--) {
            TParm inparm = new TParm();
            inparm.setData("PURORDER_NO", result.getValue("PURORDER_NO", i));
            inparm.setData("PURSEQ_NO", result.getInt("SEQ_NO", i));
            TParm inresult = IndVerifyinDTool.getInstance().onQuery(inparm);
            if (inresult.getCount() > 0) {
                // �ж�UPDATE_FLG״̬
                String update_flg = inresult.getValue("UPDATE_FLG", i);
                if ("3".equals(update_flg) || "2".equals(update_flg)) {
                    result.removeRow(i);
                    result.setCount(result.getCount() - 1);
                }
            }
        }
        // �������ۼ�,���۽��,�������
        for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
            String order_code = result.getValue("ORDER_CODE", i);
            double dosage_qty = INDTool.getInstance().getPhaTransUnitQty(
                order_code, "2");
            double stock_qty = INDTool.getInstance().getPhaTransUnitQty(
                order_code, "1");
            double retail_price = result.getDouble("RETAIL_PRICE", i)
                * dosage_qty * stock_qty;
            result.setData("RETAIL_PRICE", i, retail_price);

        }
        if (result.getCount() == 0) {
            this.messageBox("��������");
            return;
        }
        getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
        TTable table = getTable("TABLE");
        table.setParmValue(result);
        resultParm = result;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr =
            "PURORDER_NO;ORDER_CODE;ORDER_DESC;SELECT_ALL";
        this.clearValue(clearStr);
        getTextFormat("SUP_CODE").setText("");
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
                result.setCount(result.getCount() - 1);
            }
        }
        if (result == null) {
            this.messageBox("û�д�������");
            return;
        }
        setReturnValue(result);
        //System.out.println("��ѯ����: " + result);
        this.closeWindow();
    }

    /**
     * ��Ӧ���̸ı��¼�
     */
    public void onSupCodeChange() {
        String sup_code = getValueString("SUP_CODE");
        TParm parm = IndPurorderDTool.getInstance().onQueryUnDonePurorderNo(
            org_code, sup_code);
        getComboBox("PURORDER_NO").setParmValue(parm);
        getComboBox("PURORDER_NO").setSelectedIndex( -1);
    }

    /**
     * �������ı��¼�
     */
    public void onPurOrderChange() {
        if ("".equals(getValueString("SUP_CODE"))) {
            TParm parm = new TParm();
            parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
            TParm result = IndPurorderMTool.getInstance().onQuery(parm);
            getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
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
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
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
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ����������ORG_CODE
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parmOrg = (TParm) obj;
        org_code = parmOrg.getValue("ORG_CODE");
        // ��ʼ��������ComboBox
        String sup_code = "";
        TParm parm = IndPurorderDTool.getInstance().onQueryUnDonePurorderNo(
            org_code, sup_code);
        getComboBox("PURORDER_NO").setParmValue(parm);

        TParm parmIn = new TParm();
        parmIn.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        resultParm = new TParm();
    }
    
    /**
     * ��λҩƷ����
     */
    public void onOrientationAction() {
    	 TTable table = getTable("TABLE");
        if ("".equals(this.getValueString("ORDER_CODE"))) {
            this.messageBox("�����붨λҩƷ");
            return;
        }  
        boolean flg = false;  
        TParm parm = table.getParmValue();
        String order_code = this.getValueString("ORDER_CODE");  
        int row = table.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("ORDER_CODE"); i++) {
            if (order_code.equals(parm.getValue("ORDER_CODE", i))) {
                row = i;
                flg = true;
                break;
            }  
        }
        if (!flg) {
            this.messageBox("δ�ҵ���λҩƷ");
        }
        else {
        	table.setSelectedRow(row);
        }
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
