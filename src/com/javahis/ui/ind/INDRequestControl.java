package com.javahis.ui.ind;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.DateTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndRequestObserver;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �������Control
 * </p>
 *
 * <p>
 * Description: �������Control
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
 * @author zhangy 2009.05.23
 * @version 1.0
 */

public class INDRequestControl
    extends TControl {

    // �������
    private String action;
    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    // ������ѡ����
    private int row_m;

    // ϸ����ѡ����
    private int row_d;

    // ҳ���Ϸ������б�
    private String[] pageItem = {
        "REQUEST_DATE", "REQUEST_NO", "APP_ORG_CODE",
        "REQTYPE_CODE", "TO_ORG_CODE", "REASON_CHN_DESC", "DESCRIPTION",
        "URGENT_FLG", "UNIT_TYPE"};

    // ϸ�����
    private int seq;

    // ���뵥��
    private String request_no;

    // ���벿��
    private String app_org;

    // ��������
    private String request_type;

    // ϸ���ͷ
    private String table_header;

    // ϸ���Ӧ�ֶ�
    private String table_parmMap;

    // ϸ��������
    private String table_lockColumn;

    // ϸ����뷽ʽ
    private String table_align;

    // ��λ����
    private String u_type;

    public INDRequestControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ע�ἤ��SYS_FEE�������¼�
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        // ��������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��ʼ����������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ������ѯ
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDRequestAction", "onQueryM", onQueryParm());
        // ����״̬��������
        result = onFilterQueryByStatus(result);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("�޲�ѯ���");
        }
        else {
            table_m.setParmValue(result);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;REQUEST_DATE;APP_ORG_CODE;REQUEST_NO;"
            +
            "REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;ORDER_DESC_FLG"
            +
            "URGENT_FLG;CHECK_FLG;STOCK_PRICE_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
            + "SUM_VERIFYIN_PRICE";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        setValue("REQUEST_DATE", date);
        // ����״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        getTextField("REQUEST_NO").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        getComboBox("APP_ORG_CODE").setEnabled(true);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        action = "insertM";
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        if (!"updateD".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // ���������Ϣ
            parm = getInsertMParm(parm);
            if ("insertM".equals(action)) {
                // ���쵥��
                String request_no = SystemTool.getInstance().getNo("ALL",
                    "IND", "IND_REQUEST", "No");
                parm.setData("REQUEST_NO", request_no);
                // ִ����������
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDRequestAction", "onInsertM", parm);
            }
            else {
                parm.setData("REQUEST_NO", request_no);
                // ִ�����ݸ���
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDRequestAction", "onUpdateM", parm);
            }
            // ������ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            onClear();
            setValue("REQUEST_NO", parm.getValue("REQUEST_NO"));
            this.messageBox("P0001");
            onQuery();
        }
        else {
            if (!CheckDataD()) {
                return;
            }
            Timestamp date = StringTool.getTimestamp(new Date());
            table_d.acceptText();
            TDataStore dataStore = table_d.getDataStore();
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // ���ȫ����������
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
            // ���̶�����������
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "REQUEST_NO",
                                  getValueString("REQUEST_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
            }
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            // ���ⲿ�ſ�漰ҩƷ�������ж�
            if (!getOrgStockCheck()) {
                table_d.removeRowAll();
                return;
            }
            // ϸ����ж�
            if (!table_d.update()) {
                messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_d.setDSValue();
            return;
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            this.messageBox("��������ɲ���ɾ��");
        }
        else {
            if (row_m > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥", 2) == 0) {
                    TParm parm = new TParm();
                    // ϸ����Ϣ
                    if (table_d.getRowCount() > 0) {
                        table_d.removeRowAll();
                        String deleteSql[] = table_d.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // ������Ϣ
                    parm.setData("REQTYPE_CODE", request_no);
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDRequestAction", "onDeleteM", parm);
                    // ɾ���ж�
                    if (result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    table_m.removeRow(row_m);
                    this.messageBox("ɾ���ɹ�");
                    onClear();
                }
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥ϸ��", 2) == 0) {
                    table_d.removeRow(row_d);
                    // ϸ����ж�
                    if (!table_d.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_d.setDSValue();
                    onQuery();
                }
            }
        }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // ������ѡ���������Ϸ�
            getTableSelectValue(table_m, row_m, pageItem);
            // �趨ҳ��״̬
            getTextField("REQUEST_NO").setEnabled(false);
            getComboBox("APP_ORG_CODE").setEnabled(false);
            getComboBox("REQTYPE_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "updateM";
            // �������
            request_type = getValueString("REQTYPE_CODE");
            // ���ݵ�������ж����κ���Ч���Ƿ���ʾ
            changeTableDHeader(request_type);
            // ���뵥��
            request_no = getValueString("REQUEST_NO");
            // ��ϸ��Ϣ
            getTableDInfo(request_no);
            // ���һ��ϸ��
            onAddRow();
            table_d.setSelectionMode(0);
            row_d = -1;
            if ("TEC".equals(request_type) || "EXM".equals(request_type)
                || "COS".equals(request_type)) {
                u_type = "2";
            }
            else {
                u_type = "1";
            }
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            action = "updateD";
            table_m.setSelectionMode(0);
            row_m = -1;
        }
    }

    /**
     * ���벿�ű���¼�
     */
    public void onChangeAppOrg() {
        app_org = getValueString("APP_ORG_CODE");
        // ������ѡ���������趨�������
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getINDOrgType(app_org)));
        String type = parm.getValue("ORG_TYPE", 0);
        String request_type = "";
        getComboBox("REQTYPE_CODE").setSelectedIndex( -1);
        if ("A".equals(type)) {
            // ����
            request_type = "[[id,name],[,],[WAS,���],[THO,��������],"
                + "[THI,�������]]";
        }
        else if ("B".equals(type)) {
            // �п�
            request_type = "[[id,name],[,],[DEP,��������],[GIF,ҩ������],"
                + "[RET,�˿�],[WAS,���],[THO,��������],[THI,�������]]";
        }
        else {
            // С��
            request_type = "[[id,name],[,],[TEC,��ҩ����],[EXM,����Ʒ�],"
                + "[COS,���Ĳ�����]]";
        }
        getComboBox("REQTYPE_CODE").setStringData(request_type);
        getComboBox("REQTYPE_CODE").onQuery();
    }

    /**
     * ����������¼�
     */
    public void onChangeRequestType() {
        String request_type = getValueString("REQTYPE_CODE");
        TComboOrgCode org_code = (TComboOrgCode) getComboBox("TO_ORG_CODE");
        org_code.setEnabled(true);
        org_code.setSelectedIndex( -1);
        // ������ѡ��������趨���ܲ���
        if ("DEP".equals(request_type))
            org_code.setOrgType("A");
        else if ("GIF".equals(request_type))
            org_code.setOrgType("B");
        else if ("RET".equals(request_type))
            org_code.setOrgType("A");
        else if ("WAS".equals(request_type))
            org_code.setEnabled(false);
        else if ("THO".equals(request_type))
            org_code.setEnabled(false);
        else if ("THI".equals(request_type))
            org_code.setEnabled(false);
        else if ("TEC".equals(request_type))
            org_code.setOrgType("B");
        else if ("EXM".equals(request_type))
            org_code.setOrgType("B");
        else if ("COS".equals(request_type))
            org_code.setOrgType("B");
        org_code.onQuery();
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
        parm.setData("ODI_ORDER_TYPE", "A");
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
        if (!StringUtil.isNullString(order_code))
            table_d.getDataStore().setItem(row_d, "ORDER_CODE", order_code);
        String sql = INDSQL.getPHAInfoByOrder(order_code);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            this.messageBox("ҩƷ��Ϣ����");
            return;
        }

        double stock_price = 0;
        double retail_price = 0;
        if ("1".equals(getValueString("UNIT_TYPE"))) {
            // ��浥λ
            table_d.setItem(row_d, "UNIT_CODE", result
                            .getValue("STOCK_UNIT", 0));
            stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
                                           * result.getDouble("DOSAGE_QTY", 0),
                                           2);
            retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
                                            * result.getDouble("DOSAGE_QTY", 0),
                                            2);
        }
        else {
            // ��ҩ��λ
            table_d.setItem(row_d, "UNIT_CODE", result.getValue("DOSAGE_UNIT",
                0));
            stock_price = result.getDouble("STOCK_PRICE", 0);
            retail_price = result.getDouble("RETAIL_PRICE", 0);
        }
        // �ɱ���
        table_d.setItem(row_d, "STOCK_PRICE", stock_price);
        // ���ۼ�
        table_d.setItem(row_d, "RETAIL_PRICE", retail_price);
        // �趨ѡ���е���Ч��
        table_d.getDataStore().setActive(row_d, true);

        onAddRow();
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
        // Table������
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            table.getDataStore().setItem(row, "QTY", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            this.setValue("SUM_VERIFYIN_PRICE", getSumStockPrice());
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getSumRetailPrice() - getSumStockPrice(), 2));
            return false;
        }
        if ("STOCK_PRICE".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("�ɱ��ۼ۲���С�ڻ����0");
                return true;
            }
            table.getDataStore().setItem(row, "STOCK_PRICE", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            this.setValue("SUM_VERIFYIN_PRICE", getSumStockPrice());
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getSumRetailPrice() - getSumStockPrice(), 2));
            return false;
        }
        // �������
        if ("BATCH_NO".equals(columnName)) {
            String value = TypeTool.getString(node.getValue());
            if ("".equals(value)) {
                this.messageBox("������Ų���Ϊ��");
                return true;
            }
            return false;
        }
        // ��Ч��
        if ("VALID_DATE".equals(columnName)) {
            String value = TypeTool.getString(node.getValue());
            if (!DateTool.checkDate(value, "yyyy/MM/dd")) {
                this.messageBox("ʱ���ʽ����ȷ");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ������ʱ��
        Timestamp date = StringTool.getTimestamp(new Date());
        setValue("REQUEST_DATE", date);
        // ��ʼ������״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 0;
        action = "insertM";
        table_header = table_d.getHeader();
        table_parmMap = table_d.getParmMap();
        table_lockColumn = table_d.getLockColumns();
        table_align = "0,left;1,left;2,right;3,left;4,right;"
            + "5,right;6,right;7,right;8,right;9,left;"
            + "10,left;11,right";
    }

    /**
     * ���һ��ϸ��
     */
    private void onAddRow() {
        // ��δ�༭��ʱ����
        if (!this.isNewRow())
            return;
        int row = table_d.addRow();
        TParm parm = new TParm();
        parm.setData("ACTIVE", false);
        table_d.getDataStore().setActive(row, false);
    }

    /**
     * �Ƿ���δ�༭��
     *
     * @return boolean
     */
    private boolean isNewRow() {
        Boolean falg = false;
        TParm parmBuff = table_d.getDataStore().getBuffer(
            table_d.getDataStore().PRIMARY);
        int lastRow = parmBuff.getCount("#ACTIVE#");
        Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
        if (obj != null) {
            falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
        }
        else {
            falg = true;
        }
        return falg;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        TComboOrgCode org_code = (TComboOrgCode) getComboBox("TO_ORG_CODE");
        if (org_code.isEnabled()) {
            if ("".equals(getValueString("TO_ORG_CODE"))) {
                this.messageBox("���ܲ��Ų���Ϊ��");
                return false;
            }
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            if (table_d.getItemDouble(i, "QTY") <= 0) {
                this.messageBox("������������С�ڻ����0");
                return false;
            }
            if (table_d.getItemDouble(i, "STOCK_PRICE") <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return false;
            }
            if (table_d.getParmMap().indexOf("BATCH_NO") > 0) {
                if ("".equals(table_d.getItemString(i, "BATCH_NO"))) {
                    this.messageBox("���β���Ϊ��");
                    return false;
                }
                if ("".equals(table_d.getItemString(i, "VALID_DATE"))) {
                    this.messageBox("Ч�ڲ���Ϊ��");
                    return false;
                }
            }
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
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.rowCount();
        // ��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // �������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        // ���ż�1
        s++;
        return s;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailPrice() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            double amount1 = table_d.getItemDouble(i, "QTY");
            sum += table_d.getItemDouble(i, "RETAIL_PRICE") * amount1;
        }
        return StringTool.round(sum, 2);
    }

    /**
     * �����ܽ��
     *
     * @return
     */
    private double getSumStockPrice() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            double amount1 = table_d.getItemDouble(i, "QTY");
            sum += table_d.getItemDouble(i, "STOCK_PRICE") * amount1;
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ��ѯ��������
     *
     * @return
     */
    private TParm onQueryParm() {
        TParm parm = new TParm();
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        if (parm == null) {
            return parm;
        }
        return parm;
    }

    /**
     * ���ݶ���״̬���˲�ѯ���
     *
     * @param parm
     * @return
     */
    private TParm onFilterQueryByStatus(TParm parm) {
        String update_flg = "0";
        boolean flg = false;
        TDataStore ds = new TDataStore();
        for (int i = parm.getCount("REQUEST_NO") - 1; i >= 0; i--) {
            ds.setSQL(INDSQL.getRequestDByNo(parm.getValue("REQUEST_NO", i)));
            ds.retrieve();
            if (ds.rowCount() == 0) {
                flg = false;
            }
            else {
                flg = true;
                for (int j = 0; j < ds.rowCount(); j++) {
                    update_flg = ds.getItemString(j, "UPDATE_FLG");
                    if ("0".equals(update_flg) || "1".equals(update_flg)) {
                        // δ���
                        flg = false;
                        break;
                    }
                }
            }
            // ����״̬
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                // δ���
                if (flg) {
                    parm.removeRow(i);
                }
            }
            else {
                // ���
                if (!flg) {
                    parm.removeRow(i);
                }
            }
        }
        return parm;
    }

    /**
     * ���ѡ���������Ϸ�
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }

    /**
     * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        // ��ϸ��Ϣ
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getRequestDByNo(req_no));
        tds.retrieve();
        if (tds.rowCount() == 0) {
            this.messageBox("û��������ϸ");
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }

        // �۲���
        IndRequestObserver obser = new IndRequestObserver();
        tds.addObserver(obser);
        table_d.setDataStore(tds);
        table_d.setDSValue();
    }

    /**
     * ���ݵ������ı���ϸ��ı�ͷ
     *
     * @param req_type
     */
    private void changeTableDHeader(String req_type) {
        // �����κ���Ч�ڵı�ͷ
        String table_header1 = "ҩƷ����,120;���,120;��������,80,double,#####0;"
            + "��λ,40,UNIT;�ɱ���,60,double,#####0.00;�ɱ����,80,double,#####0.00;"
            + "���ۼ�,60,double,#####0.00;���۽��,80,double,#####0.00;"
            + "�������,80,double,#####0.00;�ۼ������,100,double,#####0;"
            + "��ֹ,40,boolean";
        // �����κ���Ч�ڵĶ�Ӧ�ֶ�
        String table_parmMap1 =
            "ORDER;SPECIFICATION;QTY;UNIT_CODE;STOCK_PRICE;"
            +
            "SUM_STOCK_PRICE;RETAIL_PRICE;SUM_RETAIL_PRICE;DIFF_SUM;ACTUAL_QTY;"
            + "END_FLG";
        // �����κ���Ч�ڵ�������
        String table_lockColumn1 = "1,3,5,6,7,8,9,10";
        // �����κ���Ч�ڵĶ��뷽ʽ
        String table_align1 = "0,left;1,left;2,right;3,left;4,right;"
            + "5,right;6,right;7,right;8,right;9,right";
        String header = "";
        String parmMap = "";
        String lockColumns = "";
        String align = "";
        // ������ѡ��������趨���ܲ���
        if ("DEP".equals(req_type) || "TEC".equals(req_type)
            || "EXM".equals(req_type) || "GIF".equals(req_type)
            || "COS".equals(req_type)) {
            header = table_header1;
            parmMap = table_parmMap1;
            lockColumns = table_lockColumn1;
            align = table_align1;
        }
        else {
            header = table_header;
            parmMap = table_parmMap;
            lockColumns = table_lockColumn;
            align = table_align;
        }
        table_d.setHeader(header);
        table_d.setParmMap(parmMap);
        table_d.setLockColumns(lockColumns);
        table_d.setColumnHorizontalAlignmentData(align);
    }

    /**
     * ����������������PARM
     *
     * @param parm
     * @return
     */
    private TParm getInsertMParm(TParm parm) {
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        parm.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parm.setData("REQUEST_USER", Operator.getID());
        parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
        parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
        String unit_type = "1";
        if ("TEC".equals(getValueString("REQTYPE_CODE"))
            || "EXM".equals(getValueString("REQTYPE_CODE"))
            || "COS".equals(getValueString("REQTYPE_CODE"))) {
            unit_type = "2";
        }
        parm.setData("UNIT_TYPE", unit_type);
        parm.setData("URGENT_FLG", getValueString("URGENT_FLG"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * ���ⲿ�ſ�漰ҩƷ�������ж�
     */
    private boolean getOrgStockCheck() {
        /** �������벿��ҩƷ¼��ܿ� */
        String org_code = "";
        String order_code = "";
        double stock_qty = 0;
        double qty = 0;
        if ("DEP".equals(request_type) || "EXM".equals(request_type)
            || "TEC".equals(request_type) || "COS".equals(request_type))
            org_code = getValueString("TO_ORG_CODE");
        else if ("GIF".equals(request_type) || "RET".equals(request_type)
                 || "WAS".equals(request_type) || "THO".equals(request_type))
            org_code = getValueString("APP_ORG_CODE");
        else
            org_code = "";
        if (!"".equals(org_code)) {
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                qty = table_d.getDataStore().getItemDouble(i, "QTY");
                TParm resultParm = new TParm();
                if ("1".equals(getValueString("UNIT_TYPE"))) {
                    String sql = INDSQL.getPHAInfoByOrder(order_code);
                    resultParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (resultParm.getErrCode() < 0) {
                        this.messageBox("ҩƷ��Ϣ����");
                        return false;
                    }
                    qty = StringTool.round(qty
                                           // * resultParm.getDouble("STOCK_QTY", 0)
                                           *
                                           resultParm.getDouble("DOSAGE_QTY", 0),
                                           2);
                }
                TParm inparm = new TParm();
                inparm.setData("ORG_CODE", org_code);
                inparm.setData("ORDER_CODE", order_code);
                TParm resultQTY = INDTool.getInstance().getStockQTY(inparm);
                if (resultQTY.getDouble("QTY", 0) <= 0) {
                    this.messageBox(org_code + "�����޴�ҩƷ");
                    return false;
                }
                // qty = table_d.getDataStore().getItemDouble(i, "QTY");
                stock_qty = resultQTY.getDouble("QTY", 0);
                // if ("1".equals(u_type)) {
                // if (qty * resultParm.getDouble("DOSAGE_QTY", i) > stock_qty)
                // {
                // this.messageBox(org_code + "���ſ����������������Ϊ" + stock_qty);
                // return false;
                // }
                // } else {
                if (qty > stock_qty) {
                    this.messageBox(org_code + "���ſ����������������Ϊ" + stock_qty);
                    return false;
                }
                // }
            }
        }
        return true;
    }
}
