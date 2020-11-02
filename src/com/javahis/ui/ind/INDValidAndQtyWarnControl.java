package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import java.awt.event.KeyEvent;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Calendar;
import java.util.Date;
import com.dongyang.util.TypeTool;
import jdo.ind.IndValidAndQtyWarnTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

/**
 * <p>Title: ��Ч�ڼ��������ʾ</p>
 *
 * <p>Description: ��Ч�ڼ��������ʾ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.10.28
 * @version 1.0
 */
public class INDValidAndQtyWarnControl
    extends TControl {

    private TPanel panel_0;

    private TTable table_a;

    private TTable table_b;

    public INDValidAndQtyWarnControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ע�ἤ��SYSFeePopup�������¼�
        callFunction("UI|ORDER_CODE_A|addEventListener", "ORDER_CODE_A->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD_A");
        // ע�ἤ��SYSFeePopup�������¼�
        callFunction("UI|ORDER_CODE_B|addEventListener", "ORDER_CODE_B->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD_B");
        panel_0 = getPanel("TPanel_0");
        table_a = getTable("TABLE_A");
        table_b = getTable("TABLE_B");
        String dept_code = Operator.getDept();
        this.setValue("ORG_CODE_A", dept_code);
        this.setValue("ORG_CODE_B", dept_code);
        //onQuery(); by liyh 20120810 ȥ����ʼ����ѯ
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        TParm result = new TParm();
        String org_code = "";
        String order_code = "";

        if (panel_0.isShowing()) {
            //���Ŵ���
            org_code = getValueString("ORG_CODE_A");
            if (org_code == null || org_code.length() <= 0) {
                this.messageBox("��ѡ���ѯ����");
                return;
            }
            parm.setData("ORG_CODE", org_code);
            String date = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMdd");
            //��������
            if (getRadioButton("VALID_DATE_A").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 3));
            }
            else if (getRadioButton("VALID_DATE_B").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 6));
            }
            else {
                String valid_date = getValueString("VALID_DATE");
                parm.setData("VALID_DATE", valid_date.substring(0, 4) +
                             valid_date.substring(5, 7) + valid_date.substring(8, 10));
            }
            //ҩƷ����
            order_code = getValueString("ORDER_CODE_A");
            if (order_code != null && order_code.length() > 0) {
                parm.setData("ORDER_CODE", order_code);
            }
            //��Ч�ڲ�ѯ
            result = IndValidAndQtyWarnTool.getInstance().onQueryValid(parm);
            if (result == null || result.getCount("ORDER_CODE") <= 0) {
                this.messageBox("û�в�ѯ����");
                table_a.removeRowAll();
                return;
            }
            table_a.setParmValue(result);
        }
        else {
            //���Ŵ���
            org_code = getValueString("ORG_CODE_B");
            if (org_code == null || org_code.length() <= 0) {
                this.messageBox("��ѡ���ѯ����");
                return;
            }
            parm.setData("ORG_CODE", org_code);
            //ҩƷ����
            order_code = getValueString("ORDER_CODE_B");
            if (order_code != null && order_code.length() > 0) {
                parm.setData("ORDER_CODE", order_code);
            }
            //�����
            if (getRadioButton("STOCK_QTY_A").isSelected()) {
                parm.setData("STOCK_QTY_A", "STOCK_QTY_A");
            }
            else if (getRadioButton("STOCK_QTY_B").isSelected()) {
                parm.setData("STOCK_QTY_B", "STOCK_QTY_B");
            }
            else {
                parm.setData("STOCK_QTY_C", getValue("STOCK_QTY_C"));
            }
            //�������ѯ
            result = IndValidAndQtyWarnTool.getInstance().onQueryQty(parm);
            if (result == null || result.getCount("ORDER_CODE") <= 0) {
                this.messageBox("û�в�ѯ����");
                table_b.removeRowAll();
                return;
            }
            table_b.setParmValue(result);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        if (panel_0.isShowing()) {
            getRadioButton("VALID_DATE_A").setSelected(true);
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE;ORG_CODE_A;ORDER_CODE_A;ORDER_DESC_A");
            table_a.removeRowAll();
        }
        else {
            getRadioButton("STOCK_QTY_C").setSelected(true);
            this.clearValue("ORG_CODE_B;ORDER_CODE_B;ORDER_DESC_B");
            table_b.removeRowAll();
        }
    }

    /**
     * �����ѡ��
     */
    public void onChangeRadioButton() {
        if (getRadioButton("VALID_DATE_C").isSelected()) {
            getTextFormat("VALID_DATE").setEnabled(true);
        }
        else {
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE");
        }
    }

    /**
     * ��TextField�����༭�ؼ�ʱ����
     *
     * @param com
     */
    public void onCreateEditComoponentUD_A(KeyEvent obj) {
        TTextField textFilter = getTextField("ORDER_CODE_A");
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn_A");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn_A(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_A").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_A").setValue(order_desc);
    }

    /**
     * ��TextField�����༭�ؼ�ʱ����
     *
     * @param com
     */
    public void onCreateEditComoponentUD_B(KeyEvent obj) {
        TTextField textFilter = getTextField("ORDER_CODE_B");
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn_B");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn_B(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_B").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_B").setValue(order_desc);
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
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
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
     * ����ָ�����·ݺ������Ӽ�������Ҫ���·ݺ�����
     * @param Month String �ƶ��·� ��ʽ:yyyyMM
     * @param Day String �ƶ��·� ��ʽ:dd
     * @param num String �Ӽ������� ����Ϊ��λ
     * @return String
     */
    public String rollMonth(String Month, String Day,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�num
        cal.add(cal.MONTH, num);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month, "yyyyMM");
        String lastDayOfMonth = getLastDayOfMonth(result);
        if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
            result += lastDayOfMonth;
        }
        else {
            result += Day;
        }
        return result;
    }

    /**
     * ��ȡָ���·ݵ����һ�������
     * @param date String ��ʽ YYYYMM
     * @return Timestamp
     */
    public String getLastDayOfMonth(String date) {
        if (date.trim().length() <= 0) {
            return "";
        }
        Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�1�����¸���
        cal.add(cal.MONTH, 1);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        // �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return StringTool.getString(result, "dd");
    }

}
