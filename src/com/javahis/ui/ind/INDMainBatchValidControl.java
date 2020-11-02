package com.javahis.ui.ind;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.manager.IndMainBatchValidObserver;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҩ�ⲿ�Ű�ȫ������趨
 * </p>
 *
 * <p>
 * Description: ҩ�ⲿ�Ű�ȫ������趨
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class INDMainBatchValidControl
    extends TControl {

    public static String headName = "";
    public static String org_type = "";

    public INDMainBatchValidControl() {
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
        TTable table = getTable("TABLE_M");
        table.removeRowAll();

        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("���Ŵ��벻��Ϊ��");
            return;
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getINDOrgType(org_code)));
        org_type = result.getValue("ORG_TYPE", 0);
        String lockColumns = "0,1,4,5,10,11,15,16,17";

        // �õ�ͷ�ļ�
        String tablehead = headName;
        if ("A".equals(org_type)) {
            lockColumns = "0,1,2,3,4,5,10,11,15,16,17";
            table.setHeader(tablehead);
        }
        else if ("B".equals(org_type)) {
            table.setHeader(tablehead);
        }
        else {
            tablehead =
                "ҩƷ����,80;ҩƷ����,120;����,40,boolean;��Ӧ����,100,DISPENSE_ORG_CODE_B;"
                +
                "��λ,80,MATERIAL_LOC_CODE;��λ,60,DOSAGE_UNIT;��ȫ����,80,double,#####0;"
                +
                "��ߴ���,80,double,#####0;��ʹ���,80,double,#####0;����(�ɹ�)��,100,double,#####0;"
                +
                "��;��,80,double,#####0;�����,80,double,#####0;ƽ���պ�����,100,double,#####0;"
                +
                "������,80,double,#####0;���㷽ʽ,100,QTY_TYPE;������Ա,80;����ʱ��,100;����IP,80";
            table.setHeader(tablehead);
        }
        // ����TABLE_M������
        table.setLockColumns(lockColumns);
        String filterString = "ORG_CODE = '" + org_code + "'";
        String order_code = getValueString("ORDER_CODE");
        if (order_code.length() > 0)
            filterString += " AND ORDER_CODE = '" + order_code + "'";
        String material_code = getValueString("MATERIAL_LOC_CODE");
        if (material_code.length() > 0)
            filterString += " AND MATERIAL_LOC_CODE = '" + material_code + "'";

        TDS dataStroe = new TDS();
        dataStroe.setSQL(INDSQL.getINDStockM());
        dataStroe.retrieve();
        // �۲���
        IndMainBatchValidObserver obser = new IndMainBatchValidObserver();
        dataStroe.addObserver(obser);
        table.setDataStore(dataStroe);
        table.setFilter(filterString);
        table.filter();
        table.setDSValue();
        table.acceptText();
        // ���ڰ�ȫ�����CHECKBOX
        if ("Y".equals(getValueString("STOCK_QTY"))) {
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                if (table.getItemDouble(i, "SUM_STOCK_QTY") >= table
                    .getItemDouble(i, "SAFE_QTY")) {
                    table.removeRow(i);
                }
            }
        }
        // ������ߴ���CHECKBOX
        if ("Y".equals(getValueString("MAX_QTY"))) {
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                if (table.getItemDouble(i, "SUM_STOCK_QTY") <= table
                    .getItemDouble(i, "MAX_QTY")) {
                    table.removeRow(i);
                }
            }
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE;STOCK_QTY;MAX_QTY";
        this.clearValue(clear);
        getTable("TABLE_M").setSelectionMode(0);
        getTable("TABLE_D").setSelectionMode(0);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // ����
        TTable tableM = (TTable) getComponent("TABLE_M");
        // �����ı�
        tableM.acceptText();
        TDataStore dataStoreM = tableM.getDataStore();
        // ���ȫ���Ķ����к�
        int rowsM[] = dataStoreM.getModifiedRows(dataStoreM.PRIMARY);
        String dis_flg = "";
        String dis_code = "";
        // ���̶�����������
        for (int i = 0; i < rowsM.length; i++) {
            // ���ݼ��
            dis_flg = dataStoreM.getItemString(rowsM[i], "DISPENSE_FLG");
            dis_code = dataStoreM.getItemString(rowsM[i], "DISPENSE_ORG_CODE");
            if ("Y".equals(dis_flg) && "".equals(dis_code)) {
                this.messageBox("������ѡʱ����ѡ�񲦲�����");
                return;
            }
            if ("N".equals(dis_flg) && !"".equals(dis_code)) {
                this.messageBox("ѡ�񲦲�����ʱ���빴ѡ����");
                return;
            }
            if ("1".equals(dataStoreM.getItemString(rowsM[i], "QTY_TYPE"))) {
                double dd_use_qty = dataStoreM.getItemDouble(rowsM[i],
                    "DD_USE_QTY");
                if (dd_use_qty <= 0) {
                    this.messageBox("�������ʱƽ���պ������������0");
                    return;
                }
            }
            if ("C".equals(org_type)) {
                double standing_qty = dataStoreM.getItemDouble(rowsM[i],
                    "STANDING_QTY");
                if (standing_qty <= 0) {
                    this.messageBox("С�ⳣ�����������0");
                    return;
                }
            }
            dataStoreM.setItem(rowsM[i], "OPT_USER", Operator.getID());
            dataStoreM.setItem(rowsM[i], "OPT_DATE", date);
            dataStoreM.setItem(rowsM[i], "OPT_TERM", Operator.getIP());
        }
        if (!tableM.update()) {
            messageBox("E0001");
            return;
        }
        tableM.setDSValue();

        // ��ϸ
        TTable tableD = (TTable) getComponent("TABLE_D");
        // �����ı�
        tableD.acceptText();
        TDataStore dataStoreD = tableD.getDataStore();
        // ���ȫ���Ķ����к�
        int rowsD[] = dataStoreD.getModifiedRows(dataStoreD.PRIMARY);
        // ���̶�����������
        for (int i = 0; i < rowsD.length; i++) {
            // ���ݼ��
            if ("Y".equals(dataStoreD.getItemString(rowsD[i], "ACTIVE_FLG"))) {
                Timestamp valid_date = dataStoreD.getItemTimestamp(rowsD[i],
                    "VALID_DATE");
                if (valid_date.compareTo(date) >= 0) {
                    this.messageBox("δ����Ч�ڲ��ɹ�ѡ����ע��");
                    return;
                }
            }
            dataStoreD.setItem(rowsD[i], "OPT_USER", Operator.getID());
            dataStoreD.setItem(rowsD[i], "OPT_DATE", date);
            dataStoreD.setItem(rowsD[i], "OPT_TERM", Operator.getIP());
        }
        if (!tableD.update()) {
            messageBox("E0001");
            return;
        }

        tableD.setDSValue();
        messageBox("P0001");
    }

    /**
     * TABLE_M�����¼�
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String order_code = parm.getValue("ORDER_CODE");
            String org_code = parm.getValue("ORG_CODE");
            TTable tableD = getTable("TABLE_D");
            TDS dataStroe = new TDS();
            dataStroe.setSQL(INDSQL.getINDStock(org_code, order_code, Operator.getRegion()));
            dataStroe.retrieve();
            // �۲���
            IndMainBatchValidObserver obser = new IndMainBatchValidObserver();
            dataStroe.addObserver(obser);
            tableD.setDataStore(dataStroe);
            tableD.setDSValue();
        }
    }

    /**
     * TABLE_D�����¼�
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            TTable tableM = getTable("TABLE_M");
            tableM.setSelectionMode(0);
        }
    }

    /**
     * TABLE_M˫���¼�
     */
    public void onTableMDoubleClicked() {
        TTable table = getTable("TABLE_M");
        int column = table.getSelectedColumn();
        if (column == 4) {
            // ����λ��ѯ����
            Object parm = openDialog("%ROOT%\\config\\ind\\INDMacValid.x",
                                     getValueString("ORG_CODE"));
            if (parm != null) {
                parm = (String) parm;
                if ("".equals(parm)) {
                    return;
                }
                int row = table.getSelectedRow();
                table.setItem(row, column, parm);
            }
        }
    }

    /**
     * TABLE_D˫���¼�
     */
    public void onTableDDoubleClicked() {
        TTable table = getTable("TABLE_D");
        int column = table.getSelectedColumn();
        if (column == 3) {
            // ����λ��ѯ����
            Object parm = openDialog("%ROOT%\\config\\ind\\INDMacValid.x",
                                     getValueString("ORG_CODE"));
            if (parm != null) {
                parm = (String) parm;
                if ("".equals(parm)) {
                    return;
                }
                int row = table.getSelectedRow();
                table.setItem(row, column, parm);
            }
        }
    }

    /**
     * CHECKBOX(STOCK_QTY)�ı��¼�
     */
    public void onChangeSTOCK_QTYSelect() {
        getCheckBox("MAX_QTY").setSelected(false);
    }

    /**
     * CHECKBOX(MAX_QTY)�ı��¼�
     */
    public void onChangeMAX_QTYSelect() {
        getCheckBox("STOCK_QTY").setSelected(false);
    }

    /**
     * ��沿�ű���¼�
     */
    public void onChangeORG_CODE() {
        // ��λCombo
        TComboINDMaterialloc mac = (TComboINDMaterialloc) getComponent(
            "MATERIAL_LOC_CODE");
        mac.setOrgCode(getValueString("ORG_CODE"));
        mac.onQuery();
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
        parm.setData("ODI_ORDER_TYPE", "A");
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
     * ҩƷ����س��¼�
     */
    public void onOrderCodeAction() {
        ( (TComboBox) getComponent("MATERIAL_LOC_CODE")).grabFocus();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��ҳ��״̬
        headName = getTable("TABLE_M").getHeader();
        // ע�ἤ��SYSFeePopup�������¼�
        callFunction("UI|ORDER_CODE|addEventListener", "ORDER_CODE->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD");
        // Ȩ�޹ܿ�

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
}
