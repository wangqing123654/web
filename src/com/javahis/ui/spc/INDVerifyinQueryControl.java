package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;   
import jdo.spc.IndVerifyinDTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ҩƷ�������ͳ��
 * </p>
 *
 * <p>
 * Description: ҩƷ�������ͳ��
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
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDVerifyinQueryControl  
    extends TControl {

    TTable table_m;

    TTable table_d;

    public INDVerifyinQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");

        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        this.setValue("ORG_CODE", Operator.getDept());
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ⲿ�Ų���Ϊ��");
            return;
        }
        table_m.removeRowAll();
        table_d.removeRowAll();
        this.setValue("SUM_COUNT", 0);
        this.setValue("SUM_QTY", 0);
        this.setValue("SUM_VER_AMT", 0);
        this.setValue("SUM_OWN_AMT", 0);
        this.setValue("SUM_DIFF_AMT", 0);

        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("START_DATE", formatString(this.getValueString("START_DATE")));
        parm.setData("END_DATE", formatString(this.getValueString("END_DATE")));
        if (!"".equals(this.getValueString("SUP_CODE"))) {
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        }
        System.out.println("type:"+this.getValueString("TYPE_CODE"));  
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("VERIFYIN_NO"))) {
            parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO"));
        }
        if (!"".equals(this.getValueString("INVOICE_NO"))) {
            parm.setData("INVOICE_NO", this.getValueString("INVOICE_NO"));
        }

        TParm resultM = new TParm();
        TParm resultD = new TParm();  
        if (this.getRadioButton("RadioButton1").isSelected()) {
            resultM = IndVerifyinDTool.getInstance().onQueryVerifyinBuyMaster(
                parm);
            resultD = IndVerifyinDTool.getInstance().onQueryVerifyinBuyDetail(
                parm);
        }
        else {
            resultM = IndVerifyinDTool.getInstance().onQueryVerifyinGiftMaster(
                parm);
            resultD = IndVerifyinDTool.getInstance().onQueryVerifyinGiftDetail(
                parm);
        }

        if (resultM == null || resultM.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }

        double sum_qty = 0;
        double sum_ver_amt = 0;
        double sum_own_amt = 0;

        for (int i = 0; i < resultM.getCount(); i++) {
            sum_qty += resultM.getDouble("QTY", i);
            sum_ver_amt += resultM.getDouble("VER_AMT", i);
            sum_own_amt += resultM.getDouble("OWN_AMT", i);
        }

        table_m.setParmValue(resultM);
        table_d.setParmValue(resultD);
        this.setValue("SUM_COUNT", resultM.getCount());
        this.setValue("SUM_QTY", StringTool.round(sum_qty, 3));
        this.setValue("SUM_VER_AMT", StringTool.round(sum_ver_amt, 2));
        this.setValue("SUM_OWN_AMT", StringTool.round(sum_own_amt, 2));
        this.setValue("SUM_DIFF_AMT",
                      StringTool.round(sum_own_amt - sum_ver_amt, 2));
    }

    /**
     * ��ʽ���ַ���(ʱ���ʽ)
     * @param arg String
     * @return String YYYYMMDDHHMMSS
     */
    private String formatString(String arg) {
        arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10) +
            arg.substring(11, 13) + arg.substring(14, 16) +
            arg.substring(17, 19);
        return arg;
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;TYPE_CODE;VERIFYIN_NO;"
            + "INVOICE_NO;SUM_COUNT;SUM_QTY;SUM_VER_AMT;SUM_OWN_AMT;"
            + "SUM_DIFF_AMT;SUP_CODE";
        this.clearValue(clearStr);

        //getTextFormat("SUP_CODE").setValue(null);
        //getTextFormat("SUP_CODE").setText("");

        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        getRadioButton("RadioButton1").setSelected(true);
        getRadioButton("RadioButton3").setSelected(true);
        table_m.removeRowAll();
        table_m.setVisible(true);
        table_d.removeRowAll();
        table_d.setVisible(false);
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (getRadioButton("RadioButton3").isSelected()) {
            //����
            if (table_m.getRowCount() <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "ҩƷ���������ܱ�");
            date.setData("ORG_CODE", "TEXT",
                         "ͳ�Ʋ���: " +
                         getComboBox("ORG_CODE").getSelectedName());
            String start_date = getValueString("START_DATE");
            String end_date = getValueString("END_DATE");
            date.setData("DATE_AREA", "TEXT",
                         "ͳ������: " +
                         start_date.substring(0, 4) + "/" +
                         start_date.substring(5, 7) + "/" +
                         start_date.substring(8, 10) + " " +
                         start_date.substring(11, 13) + ":" +
                         start_date.substring(14, 16) + ":" +
                         start_date.substring(17, 19) +
                         " ~ " +
                         end_date.substring(0, 4) + "/" +
                         end_date.substring(5, 7) + "/" +
                         end_date.substring(8, 10) + " " +
                         end_date.substring(11, 13) + ":" +
                         end_date.substring(14, 16) + ":" +
                         end_date.substring(17, 19) );
            date.setData("DATE", "TEXT",
                         "�Ʊ�ʱ��: " +
                         SystemTool.getInstance().getDate().toString().
                         substring(0, 10).
                         replace('-', '/'));
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // �������
            TParm parm = new TParm();
            TParm tableParm = table_m.getParmValue();
            for (int i = 0; i < table_m.getRowCount(); i++) {
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("VERIFYIN_PRICE",
                             tableParm.getValue("VERIFYIN_PRICE", i));
                parm.addData("VER_AMT", tableParm.getValue("VER_AMT", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_AMT", i));
                parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
                parm.addData("DIFF_AMT", tableParm.getValue("DIFF_AMT", i));
            }
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "VER_AMT");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("SUM_VER_AMT", "TEXT",
                         "�����ܽ� " + getValueDouble("SUM_VER_AMT"));
            date.setData("SUM_OWN_AMT", "TEXT",
                         "�����ܽ� " + getValueDouble("SUM_OWN_AMT"));
            date.setData("SUM_DIFF_AMT", "TEXT",
                         "��������ܽ� " + getValueDouble("SUM_DIFF_AMT"));
            // ���ô�ӡ����
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\IND\\INDVerfyinMQuery.jhw", date);
        }
        else {
            //��ϸ
            if (table_d.getRowCount() <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "ҩƷ���������ϸ��");
            date.setData("ORG_CODE", "TEXT",
                         "ͳ�Ʋ���: " +
                         getComboBox("ORG_CODE").getSelectedName());
            String start_date = getValueString("START_DATE");
            String end_date = getValueString("END_DATE");
            date.setData("DATE_AREA", "TEXT",
                         "ͳ������: " +
                         start_date.substring(0, 4) + "/" +
                         start_date.substring(5, 7) + "/" +
                         start_date.substring(8, 10) + " " +
                         start_date.substring(11, 13) + ":" +
                         start_date.substring(14, 16) + ":" +
                         start_date.substring(17, 19) + " ~ " +
                         end_date.substring(0, 4) + "/" +
                         end_date.substring(5, 7) + "/" +
                         end_date.substring(8, 10) + " " +
                         end_date.substring(11, 13) + ":" +
                         end_date.substring(14, 16) + ":" +
                         end_date.substring(17, 19));
            date.setData("DATE", "TEXT",
                         "�Ʊ�ʱ��: " +
                         SystemTool.getInstance().getDate().toString().
                         substring(0, 10).
                         replace('-', '/'));
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // �������
            TParm parm = new TParm();
            TParm tableParm = table_d.getParmValue();
            for (int i = 0; i < table_d.getRowCount(); i++) {
                parm.addData("VERIFYIN_NO", tableParm.getValue("VERIFYIN_NO", i));
                parm.addData("SUP_ABS_DESC",
                             tableParm.getValue("SUP_ABS_DESC", i));
                parm.addData("VERIFYIN_DATE",
                             tableParm.getValue("VERIFYIN_DATE", i).substring(0,10));
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("VERIFYIN_QTY", tableParm.getValue("VERIFYIN_QTY", i));
                parm.addData("VERIFYIN_PRICE", tableParm.getValue("VERIFYIN_PRICE", i));
                parm.addData("VER_AMT", tableParm.getValue("VER_AMT", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
                parm.addData("DIFF_AMT", tableParm.getValue("DIFF_AMT", i));
                parm.addData("INVOICE_NO", tableParm.getValue("INVOICE_NO", i));
                parm.addData("INVOICE_DATE",
                             tableParm.getValue("INVOICE_DATE", i).substring(0,
                    10));
            }
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_NO");
            parm.addData("SYSTEM", "COLUMNS", "SUP_ABS_DESC");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_DATE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "VER_AMT");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
            parm.addData("SYSTEM", "COLUMNS", "INVOICE_DATE");
            parm.addData("SYSTEM", "COLUMNS", "INVOICE_NO");

            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("SUM_VER_AMT", "TEXT",
                         "�����ܽ� " + getValueDouble("SUM_VER_AMT"));
            date.setData("SUM_OWN_AMT", "TEXT",
                         "�����ܽ� " + getValueDouble("SUM_OWN_AMT"));
            date.setData("SUM_DIFF_AMT", "TEXT",
                         "��������ܽ� " + getValueDouble("SUM_DIFF_AMT"));
            // ���ô�ӡ����
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\IND\\INDVerfyinDQuery.jhw", date);
        }
    }

    /**
     * ��ѯ���ѡ���¼�
     */
    public void onSelectTypeAction() {
        if (getRadioButton("RadioButton3").isSelected()) {
            table_m.setVisible(true);
            table_d.setVisible(false);
        }
        else {
            table_d.setVisible(true);
            table_m.setVisible(false);
        }
    }

    /**
     * ����EXCEL
     */
    public void onExport() {
        if (getRadioButton("RadioButton3").isSelected()) {
            if (table_m.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table_m, "ҩƷ�������ͳ�ƻ���");
        }
        else {
            if (table_d.getRowCount() <= 0) {
                this.messageBox("û�л������");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(table_d, "ҩƷ�������ͳ����ϸ");
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
