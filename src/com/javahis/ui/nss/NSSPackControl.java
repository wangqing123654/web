package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.data.TParm;
import jdo.nss.NSSPackMTool;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.nss.NSSMealTool;
import jdo.nss.NSSPackDTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TMessage;
import jdo.nss.NSSMenuTool;
import jdo.nss.NSSPackDDTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSSQL;

/**
 * <p>Title: �ײ��ֵ�</p>
 *
 * <p>Description: �ײ��ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSPackControl
    extends TControl {
    public NSSPackControl() {
        super();
    }

    private TTable table_1;

    private TTable table_2;

    private TTable table_3;

    // ��ǰѡ�е�ҳǩ
    private int page = 0;

    /**
     * ��ʼ������
     */
    public void onInit() {
        table_1 = getTable("TABLE_1");
        table_2 = getTable("TABLE_2");
        table_3 = getTable("TABLE_3");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
        // ���õ����˵�
        getTextField("ORDER_CODE_2").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE_2").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        TParm result = new TParm();
        if (page == 0) {
            // �ײ��ֵ�ҳǩ
            String pack_code = this.getValueString("PACK_CODE_1");
            if (pack_code != null && pack_code.length() > 0) {
                parm.setData("PACK_CODE", pack_code);
            }
            result = NSSPackMTool.getInstance().onQueryNSSPackM(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û�в�ѯ����");
                table_1.removeRowAll();
            }
            else {
                table_1.setParmValue(result);
            }
        }
        else if (page == 1) {
            // �ײͲ͵�ҳǩ
            String pack_code = this.getValueString("PACK_CODE_2");
            parm.setData("PACK_CODE", pack_code);
            String meal_code = this.getValueString("MEAL_CODE_2");
            if (meal_code != null && meal_code.length() > 0) {
                parm.setData("MEAL_CODE", meal_code);
            }
            result = NSSPackDTool.getInstance().onQueryNSSPackD(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û�в�ѯ����");
                table_2.removeRowAll();
            }
            else {
                table_2.setParmValue(result);
            }
        }
        else {
            // �ײͲ���ҳǩ
            String pack_code = this.getValueString("PACK_CODE_3");
            parm.setData("PACK_CODE", pack_code);
            String meal_code = this.getValueString("MEAL_CODE_3");
            parm.setData("MEAL_CODE", meal_code);
            String menu_code = this.getValueString("MENU_CODE_3");
            if (menu_code != null && menu_code.length() > 0) {
                parm.setData("MENU_CODE", menu_code);
            }
            result = NSSPackDDTool.getInstance().onQueryNSSPackDD(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û�в�ѯ����");
                table_3.removeRowAll();
            }
            else {
                table_3.setParmValue(result);
            }
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        TParm parm = new TParm();
        TParm result = new TParm();
        if (page == 0) {
            // �ײ��ֵ�ҳǩ
            if (!checkDataPackM()) {
                return;
            }
            parm.setData("PACK_CODE", this.getValueString("PACK_CODE_1"));
            parm.setData("PACK_CHN_DESC", this.getValueString("PACK_CHN_DESC_1"));
            parm.setData("PACK_ENG_DESC", this.getValueString("PACK_ENG_DESC_1"));
            parm.setData("PY1", this.getValueString("PY1_1"));
            parm.setData("PY2", this.getValueString("PY2_1"));
            parm.setData("SEQ", this.getValueInt("SEQ_1"));
            parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION_1"));
            parm.setData("MEAL_COUNT", this.getValueInt("MEAL_COUNT_1"));
            parm.setData("DIET_TYPE", this.getValueString("DIET_TYPE_1"));
            parm.setData("DIET_KIND", this.getValueString("DIET_KIND_1"));
            parm.setData("PRICE", this.getValueDouble("PRICE_1"));
            parm.setData("CALORIES", this.getValueDouble("CALORIES_1"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            if ( ( (TTextField) getComponent("PACK_CODE_1")).isEnabled()) {
                // ��������
                result = NSSPackMTool.getInstance().onInsertNSSPackM(parm);
            }
            else {
                // ��������
                result = NSSPackMTool.getInstance().onUpdateNSSPackM(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                onQuery();
            }
        }
        else if (page == 1) {
            // �ײͲ͵�ҳǩ
            if (!checkDataPackD()) {
                return;
            }
            parm.setData("PACK_CODE", this.getValueString("PACK_CODE_2"));
            parm.setData("MEAL_CODE", this.getValueString("MEAL_CODE_2"));
            parm.setData("MEAL_CHN_DESC", this.getValueString("MEAL_CHN_DESC_2"));
            parm.setData("MEAL_ENG_DESC", this.getValueString("MEAL_ENG_DESC_2"));
            parm.setData("PY1", this.getValueString("PY1_2"));
            parm.setData("PY2", this.getValueString("PY2_2"));
            parm.setData("SEQ", this.getValueInt("SEQ_2"));
            parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION_2"));
            parm.setData("ORDER_PRICE", this.getValueDouble("ORDER_PRICE_2"));
            parm.setData("PACK_PRICE", this.getValueDouble("PACK_PRICE_2"));
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE_2"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            if ( ( (TTextFormat) getComponent("MEAL_CODE_2")).isEnabled()) {
                // ��������
                result = NSSPackDTool.getInstance().onInsertNSSPackD(parm);
            }
            else {
                // ��������
                result = NSSPackDTool.getInstance().onUpdateNSSPackD(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                onQuery();
                // �����ײ��ֵ�ļ۸�Ϳ�·��
                String sql = onUpdatePackMPriceAndCalories(getValueString(
                    "PACK_CODE_2"));
                TParm parmD = new TParm(TJDODBTool.getInstance().update(sql));
            }
        }
        else {
            // �ײͲ���ҳǩ
            if (!checkDataPackDD()) {
                return;
            }
            parm.setData("PACK_CODE", this.getValueString("PACK_CODE_3"));
            parm.setData("MEAL_CODE", this.getValueString("MEAL_CODE_3"));
            parm.setData("MENU_CODE", this.getValueString("MENU_CODE_3"));
            parm.setData("MENU_CHN_DESC", this.getValueString("MENU_CHN_DESC_3"));
            parm.setData("MENU_ENG_DESC", this.getValueString("MENU_ENG_DESC_3"));
            parm.setData("MEAL_TYPE", this.getValueString("MEAL_TYPE_3"));
            parm.setData("PY1", this.getValueString("PY1_3"));
            parm.setData("PY2", this.getValueString("PY2_3"));
            parm.setData("SEQ", this.getValueInt("SEQ_3"));
            parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION_3"));
            parm.setData("ORDER_PRICE", this.getValueDouble("ORDER_PRICE_3"));
            parm.setData("PACK_PRICE", this.getValueDouble("PACK_PRICE_3"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
            parm.setData("OPT_TERM", Operator.getIP());
            if ( ( (TTextFormat) getComponent("MENU_CODE_3")).isEnabled()) {
                // ��������
                result = NSSPackDDTool.getInstance().onInsertNSSPackDD(parm);
            }
            else {
                // ��������
                result = NSSPackDDTool.getInstance().onUpdateNSSPackDD(parm);
            }
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
            }
            else {
                this.messageBox("P0001");
                onQuery();
                // �����ײ��ֵ�ļ۸�
                String sql = onUpdatePackDPrice(getValueString("PACK_CODE_3"),
                                                getValueString("MEAL_CODE_3"));
                TParm parmDD = new TParm(TJDODBTool.getInstance().update(sql));
            }
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (page == 0) {
            // �ײ��ֵ�ҳǩ
            int row = table_1.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ��ɾ������");
                return;
            }
            TParm parm = table_1.getParmValue().getRow(row);
            if (this.messageBox("��ʾ", "�Ƿ�ɾ�����ײ�", 2) == 0) {
                TParm result = TIOM_AppServer.executeAction(
                    "action.nss.NSSPackAction", "onDeleteNSSPack", parm);
                if (result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                }
                else {
                    this.messageBox("ɾ���ɹ�");
                    table_1.removeRow(row);
                }
            }
        }
        else if (page == 1) {
            // �ײͲ͵�ҳǩ
            int row = table_2.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ��ɾ������");
                return;
            }
            TParm parm = table_2.getParmValue().getRow(row);
            if (this.messageBox("��ʾ", "�Ƿ�ɾ���ò͵�", 2) == 0) {
                TParm result = TIOM_AppServer.executeAction(
                    "action.nss.NSSPackAction", "onDeleteNSSPackD", parm);
                if (result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                }
                else {
                    this.messageBox("ɾ���ɹ�");
                    table_2.removeRow(row);
                }
            }
        }
        else {
            // �ײͲ���ҳǩ
            int row = table_3.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ��ɾ������");
                return;
            }
            TParm parm = table_3.getParmValue().getRow(row);
            if (this.messageBox("��ʾ", "�Ƿ�ɾ���ò�Ʒ", 2) == 0) {
                TParm result = NSSPackDDTool.getInstance().onDeleteNSSPackDD(
                    parm);
                if (result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                }
                else {
                    this.messageBox("ɾ���ɹ�");
                    table_3.removeRow(row);
                }
            }
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        if (page == 0) {
            // �ײ��ֵ�ҳǩ
            this.clearValue(
                "PACK_CODE_1;PACK_CHN_DESC_1;PACK_ENG_DESC_1;PY1_1;"
                + "PY2_1;SEQ_1;DESCRIPTION_1;MEAL_COUNT_1;"
                + "DIET_TYPE_1;DIET_KIND_1;PRICE_1;CALORIES_1");
            table_1.removeRowAll();
            ( (TTextField) getComponent("PACK_CODE_1")).setEnabled(true);
        }
        else if (page == 1) {
            // �ײͲ͵�ҳǩ
            this.clearValue(
                "MEAL_CODE_2;MEAL_CHN_DESC_2;MEAL_ENG_DESC_2;PY1_2;"
                + "PY2_2;SEQ_2;DESCRIPTION_2;ORDER_PRICE_2;PACK_PRICE_2;"
                + "ORDER_CODE_2;ORDER_DESC_2");
            table_2.removeRowAll();
            ( (TTextFormat) getComponent("MEAL_CODE_2")).setEnabled(true);
        }
        else {
            // �ײͲ���ҳǩ
            this.clearValue(
                "MENU_CODE_3;MENU_CHN_DESC_3;MENU_ENG_DESC_3;MEAL_TYPE_3;PY1_3;"
                + "PY2_3;SEQ_3;DESCRIPTION_3;ORDER_PRICE_3;PACK_PRICE_3");
            table_3.removeRowAll();
            ( (TTextFormat) getComponent("MENU_CODE_3")).setEnabled(true);
        }
    }

    /**
     * �������ҳ
     */
    public void onChangeTTabbedPane() {
        TTabbedPane tabbedPane = ( (TTabbedPane)this.getComponent(
            "tTabbedPane_0"));
        if (tabbedPane.getSelectedIndex() == 0) {
            page = 0;
        }
        else if (tabbedPane.getSelectedIndex() == 1) {
            page = 1;
            int row = table_1.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ���ײ�");
                page = 0;
                tabbedPane.setSelectedIndex(page);
                return;
            }
            else {
                TParm parm = table_1.getParmValue().getRow(row);
                this.setValue("PACK_CODE_2", parm.getValue("PACK_CODE"));
                this.setValue("PACK_CHN_DESC_2",
                              parm.getValue("PACK_CHN_DESC"));
                onClear();
                onQuery();
            }
        }
        else {
            page = 2;
            int row = table_2.getSelectedRow();
            if (row < 0) {
                this.messageBox("��ѡ���ײͲ͵�");
                page = 1;
                tabbedPane.setSelectedIndex(page);
                return;
            }
            else {
                TParm parm = table_2.getParmValue().getRow(row);
                this.setValue("PACK_CODE_3", parm.getValue("PACK_CODE"));
                this.setValue("MEAL_CODE_3", parm.getValue("MEAL_CODE"));
                onClear();
                onQuery();
            }
        }
    }

    /**
     * �ʹδ������¼�
     */
    public void onChangeNSSMeal() {
        String meal_code = this.getValueString("MEAL_CODE_2");
        String meal_chn_desc = "";
        String meal_eng_desc = "";
        String py1 = "";
        String py2 = "";
        if (meal_code != null && meal_code.length() > 0) {
            TParm parm = new TParm();
            parm.setData("MEAL_CODE", meal_code);
            TParm result = NSSMealTool.getInstance().onQueryNSSMeal(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("�ʹδ����趨����");
                return;
            }
            meal_chn_desc = result.getValue("MEAL_CHN_DESC", 0);
            meal_eng_desc = result.getValue("MEAL_ENG_DESC", 0);
            py1 = result.getValue("PY1", 0);
            py2 = result.getValue("PY2", 0);
        }
        this.setValue("MEAL_CHN_DESC_2", meal_chn_desc);
        this.setValue("MEAL_ENG_DESC_2", meal_eng_desc);
        this.setValue("PY1_2", py1);
        this.setValue("PY2_2", py2);
    }

    /**
     * ��Ʒ�������¼�
     */
    public void onChangeNSSMenu() {
        String menu_code = this.getValueString("MENU_CODE_3");
        String menu_chn_desc = "";
        String menu_eng_desc = "";
        String py1 = "";
        String py2 = "";
        double order_price = 0;
        if (menu_code != null && menu_code.length() > 0) {
            TParm parm = new TParm();
            parm.setData("MENU_CODE", menu_code);
            TParm result = NSSMenuTool.getInstance().onQueryNSSMenu(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("��Ʒ�����趨����");
                return;
            }
            menu_chn_desc = result.getValue("MENU_CHN_DESC", 0);
            menu_eng_desc = result.getValue("MENU_ENG_DESC", 0);
            py1 = result.getValue("PY1", 0);
            py2 = result.getValue("PY2", 0);
            order_price = result.getDouble("PRICE", 0);
        }
        this.setValue("MENU_CHN_DESC_3", menu_chn_desc);
        this.setValue("MENU_ENG_DESC_3", menu_eng_desc);
        this.setValue("PY1_3", py1);
        this.setValue("PY2_3", py2);
        this.setValue("ORDER_PRICE_3", order_price);
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableMClick() {
        TParm parm = table_1.getParmValue().getRow(table_1.getSelectedRow());
        this.setValue("PACK_CODE_1", parm.getValue("PACK_CODE"));
        this.setValue("PACK_CHN_DESC_1", parm.getValue("PACK_CHN_DESC"));
        this.setValue("PACK_ENG_DESC_1", parm.getValue("PACK_ENG_DESC"));
        this.setValue("PY1_1", parm.getValue("PY1"));
        this.setValue("PY2_1", parm.getValue("PY2"));
        this.setValue("SEQ_1", parm.getValue("SEQ"));
        this.setValue("DESCRIPTION_1", parm.getValue("DESCRIPTION"));
        this.setValue("MEAL_COUNT_1", parm.getValue("MEAL_COUNT"));
        this.setValue("DIET_TYPE_1", parm.getValue("DIET_TYPE"));
        this.setValue("DIET_KIND_1", parm.getValue("DIET_KIND"));
        this.setValue("PRICE_1", parm.getValue("PRICE"));
        this.setValue("CALORIES_1", parm.getValue("CALORIES"));
        ( (TTextField) getComponent("PACK_CODE_1")).setEnabled(false);
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableDClick() {
        TParm parm = table_2.getParmValue().getRow(table_2.getSelectedRow());
        this.setValue("MEAL_CODE_2", parm.getValue("MEAL_CODE"));
        this.setValue("MEAL_CHN_DESC_2", parm.getValue("MEAL_CHN_DESC"));
        this.setValue("MEAL_ENG_DESC_2", parm.getValue("MEAL_ENG_DESC"));
        this.setValue("PY1_2", parm.getValue("PY1"));
        this.setValue("PY2_2", parm.getValue("PY2"));
        this.setValue("SEQ_2", parm.getValue("SEQ"));
        this.setValue("DESCRIPTION_2", parm.getValue("DESCRIPTION"));
        this.setValue("ORDER_PRICE_2", parm.getValue("ORDER_PRICE"));
        this.setValue("PACK_PRICE_2", parm.getValue("PACK_PRICE"));
        this.setValue("ORDER_CODE_2", parm.getValue("ORDER_CODE"));

        TParm sysFee = new TParm(TJDODBTool.getInstance().select(SYSSQL.getSYSFee(
            parm.getValue("ORDER_CODE"))));
        this.setValue("ORDER_DESC_2", sysFee.getValue("ORDER_DESC", 0));
        ( (TTextFormat) getComponent("MEAL_CODE_2")).setEnabled(false);
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableDDClick() {
        TParm parm = table_3.getParmValue().getRow(table_3.getSelectedRow());
        this.setValue("MENU_CODE_3", parm.getValue("MENU_CODE"));
        this.setValue("MENU_CHN_DESC_3", parm.getValue("MENU_CHN_DESC"));
        this.setValue("MENU_ENG_DESC_3", parm.getValue("MENU_ENG_DESC"));
        this.setValue("MEAL_TYPE_3", parm.getValue("MEAL_TYPE"));
        this.setValue("PY1_3", parm.getValue("PY1"));
        this.setValue("PY2_3", parm.getValue("PY2"));
        this.setValue("SEQ_3", parm.getValue("SEQ"));
        this.setValue("DESCRIPTION_3", parm.getValue("DESCRIPTION"));
        this.setValue("ORDER_PRICE_3", parm.getValue("ORDER_PRICE"));
        this.setValue("PACK_PRICE_3", parm.getValue("PACK_PRICE"));
        ( (TTextFormat) getComponent("MENU_CODE_3")).setEnabled(false);
    }

    /**
     * �ײ��������س��¼�
     */
    public void onPackDescAction() {
        String py = TMessage.getPy(this.getValueString("PACK_CHN_DESC_1"));
        setValue("PY1_1", py);
    }

    /**
     * �ʹ��������س��¼�
     */
    public void onMealDescAction() {
        String py = TMessage.getPy(this.getValueString("MEAL_CHN_DESC_2"));
        setValue("PY1_2", py);
    }

    /**
     * ��Ʒ�������س��¼�
     */
    public void onMenuDescAction() {
        String py = TMessage.getPy(this.getValueString("MENU_CHN_DESC_3"));
        setValue("PY1_3", py);
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_2").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_2").setValue(order_desc);
    }

    /**
     * ���ݼ��PackM
     * @return boolean
     */
    private boolean checkDataPackM() {
        String pack_code = this.getValueString("PACK_CODE_1");
        if (pack_code == null || pack_code.length() <= 0) {
            this.messageBox("�ײʹ��벻��Ϊ��");
            return false;
        }
        String pack_chn_desc = this.getValueString("PACK_CHN_DESC_1");
        if (pack_chn_desc == null || pack_chn_desc.length() <= 0) {
            this.messageBox("�ײ�����������Ϊ��");
            return false;
        }
        String diet_type = this.getValueString("DIET_TYPE_1");
        if (diet_type == null || diet_type.length() <= 0) {
            this.messageBox("��ʳ���ֲ���Ϊ��");
            return false;
        }
        String diet_kind = this.getValueString("DIET_KIND_1");
        if (diet_kind == null || diet_kind.length() <= 0) {
            this.messageBox("��ʳ�����Ϊ��");
            return false;
        }
        String price = this.getValueString("PRICE_1");
        if (price == null || price.length() <= 0) {
            this.messageBox("�۸���Ϊ��");
            return false;
        }
        if (StringTool.getDouble(price) <= 0) {
            this.messageBox("�۸���С�ڻ����0");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ��PackD
     * @return boolean
     */
    private boolean checkDataPackD() {
        String meal_code = this.getValueString("MEAL_CODE_2");
        if (meal_code == null || meal_code.length() <= 0) {
            this.messageBox("�ʹδ��벻��Ϊ��");
            return false;
        }
        String meal_chn_desc = this.getValueString("MEAL_CHN_DESC_2");
        if (meal_chn_desc == null || meal_chn_desc.length() <= 0) {
            this.messageBox("�ʹ�����������Ϊ��");
            return false;
        }
        String order_price = this.getValueString("ORDER_PRICE_2");
        if (order_price == null || order_price.length() <= 0) {
            this.messageBox("���ʹζ��۲���Ϊ��");
            return false;
        }
        if (StringTool.getDouble(order_price) <= 0) {
            this.messageBox("���ʹζ��۲���С�ڻ����0");
            return false;
        }
        String pack_price = this.getValueString("PACK_PRICE_2");
        if (pack_price == null || pack_price.length() <= 0) {
            this.messageBox("�ײͲʹμ۸���Ϊ��");
            return false;
        }
        if (StringTool.getDouble(pack_price) <= 0) {
            this.messageBox("�ײͲʹμ۸���С�ڻ����0");
            return false;
        }
        String order_desc = this.getValueString("ORDER_DESC_2");
        if (order_desc == null || order_desc.length() <= 0) {
            this.messageBox("�շ���Ŀ����Ϊ��");
            return false;
        }

        return true;
    }

    /**
     * ���ݼ��PackDD
     * @return boolean
     */
    private boolean checkDataPackDD() {
        String menu_code = this.getValueString("MENU_CODE_3");
        if (menu_code == null || menu_code.length() <= 0) {
            this.messageBox("��Ʒ���벻��Ϊ��");
            return false;
        }
        String menu_chn_desc = this.getValueString("MENU_CHN_DESC_3");
        if (menu_chn_desc == null || menu_chn_desc.length() <= 0) {
            this.messageBox("��Ʒ����������Ϊ��");
            return false;
        }
        String pack_price = this.getValueString("PACK_PRICE_3");
        if (pack_price == null || pack_price.length() <= 0) {
            this.messageBox("�ײ͵�Ʒ�۸���Ϊ��");
            return false;
        }
        if (StringTool.getDouble(pack_price) <= 0) {
            this.messageBox("�ײ͵�Ʒ�۸���С�ڻ����0");
            return false;
        }
        return true;
    }

    /**
     * �����ײ��ֵ�ļ۸�
     */
    private String onUpdatePackMPriceAndCalories(String pack_code) {
        String sql = "SELECT SUM(PACK_PRICE) AS PACK_PRICE FROM NSS_PACKD "
            + " WHERE PACK_CODE = '" + pack_code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        return "UPDATE NSS_PACKM SET PRICE = " + parm.getDouble("PACK_PRICE", 0) +
            " WHERE PACK_CODE = '" + pack_code + "'";
    }

    /**
     * �����ײͲ͵�ļ۸�Ϳ�·��
     */
    private String onUpdatePackDPrice(String pack_code, String meal_code) {
        String sql = "SELECT SUM(PACK_PRICE) AS PACK_PRICE "
            + " FROM NSS_PACKDD WHERE PACK_CODE = '" +
            pack_code + "' AND MEAL_CODE = '" + meal_code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        return "UPDATE NSS_PACKD SET PRICE = " + parm.getDouble("PACK_PRICE", 0) +
            " WHERE PACK_CODE = '" + pack_code + "' AND MEAL_CODE = '" +
            meal_code + "'";
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

}
