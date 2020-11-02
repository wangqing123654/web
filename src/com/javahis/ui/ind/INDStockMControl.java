package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import jdo.sys.SystemTool;
import jdo.ind.IndStockMTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import jdo.ind.IndStockDTool;
import com.dongyang.jdo.TDS;
import com.javahis.manager.IndMainBatchValidObserver;
import java.sql.Timestamp;

import jdo.ind.INDSQL;

/**
 * <p>Title: ҩ��������</p>
 *
 * <p>Description: ҩ��������</p>
 *
 * <p>Copyright: Copyright (c) �����к� 2011</p>
 *
 * <p>Company: �����к� </p>
 *
 * @author Zy ���� ZhenQin
 * @version 4.0
 */
public class INDStockMControl
    extends TControl {

    private String action = "insertM";

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;


    public INDStockMControl() {

    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        String region_code = Operator.getRegion();
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        parm.setData("REGION_CODE", region_code);
        parm.setData("MATERIAL_LOC_CODE",
                     this.getValueString("MATERIAL_LOC_CODE"));
        parm.setData("DISPENSE_FLG", this.getValueString("DISPENSE_FLG"));
        parm.setData("DISPENSE_ORG_CODE",
                     this.getValueString("DISPENSE_ORG_CODE"));
        parm.setData("QTY_TYPE", this.getValueString("QTY_TYPE"));
        parm.setData("MM_USE_QTY", this.getValueDouble("MM_USE_QTY"));
        parm.setData("DD_USE_QTY", this.getValueDouble("DD_USE_QTY"));
        parm.setData("MAX_QTY", this.getValueDouble("MAX_QTY"));
        parm.setData("SAFE_QTY", this.getValueDouble("SAFE_QTY"));
        parm.setData("MIN_QTY", this.getValueDouble("MIN_QTY"));
        parm.setData("ECONOMICBUY_QTY", this.getValueDouble("ECONOMICBUY_QTY"));
        parm.setData("BUY_UNRECEIVE_QTY",
                     this.getValueDouble("BUY_UNRECEIVE_QTY"));
        parm.setData("STANDING_QTY", this.getValueDouble("STANDING_QTY"));
        parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        if ("insertM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            result = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getINDStockM(this.getValueString("ORG_CODE"),
                             this.getValueString("ORDER_CODE"))));
            if (result.getCount() > 0) {
                this.messageBox("��ҩƷ�Ѵ���");
                return;
            }
            result = IndStockMTool.getInstance().onInsertIndStockM(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
        }
        else if ("updateM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // ִ����������
            result = TIOM_AppServer.executeAction(
                "action.ind.INDStockMAction", "onUpdate", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
        }
        else {
            TDataStore dataStore = table_d.getDataStore();
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            if (!table_d.update()) {
                messageBox("E0001");
                return;
            }
            table_d.setDSValue();
            messageBox("P0001");
        }
        onQuery();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        table_m.removeRowAll();
        table_d.removeRowAll();
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("���Ŵ��벻��Ϊ��");
            return;
        }
        if ("Y".equals(this.getValueString("SAFE_FLG")) &&
            "Y".equals(this.getValueString("MAX_FLG"))) {
            this.messageBox("����ͬʱѡ����ڰ�ȫ������͸�����ߴ���");
            return;
        }
        String sql = INDSQL.getIndStockMInfo(org_code,
                                             getValueString("ORDER_CODE"),
                                             getValueString("MATERIAL_LOC_CODE"));
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("---"+sql);
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        TParm result = new TParm();
        double qty = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            TParm stock_qty = new TParm();
            stock_qty.setData("ORG_CODE", this.getValueString("ORG_CODE"));
            stock_qty.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
            result = IndStockDTool.getInstance().onQueryStockQTY(stock_qty);
            qty = result.getDouble("QTY", 0);
            if (qty < 0) {
                qty = 0;
            }
            parm.setData("STOCK_QTY", i, qty);
        }

        if ("Y".equals(getValueString("SAFE_FLG"))) {
            for (int i = parm.getCount() - 1; i >= 0; i--) {
                if (parm.getDouble("STOCK_QTY", i) <
                    parm.getDouble("SAFE_QTY", i)) {
                    parm.removeRow(i);
                }
            }
        }
        if ("Y".equals(getValueString("MAX_FLG"))) {
            for (int i = parm.getCount() - 1; i >= 0; i--) {
                if (parm.getDouble("STOCK_QTY", i) >
                    parm.getDouble("MAX_QTY", i)) {
                    parm.removeRow(i);
                }
            }
        }
        table_m.setParmValue(parm);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE;DISPENSE_FLG;"
            + "DISPENSE_ORG_CODE;MM_USE_QTY;DD_USE_QTY;MAX_QTY;SAFE_QTY;"
            + "QTY_TYPE;MIN_QTY;ECONOMICBUY_QTY;BUY_UNRECEIVE_QTY;"
            + "STANDING_QTY;ACTIVE_FLG;SAFE_FLG;MAX_FLG";
        this.clearValue(clear);
        table_m.removeRowAll();
        table_m.setSelectionMode(0);
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).setEnabled(false);
        action = "insertM";
    }

    /**
     * TABLE_M�����¼�
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            this.setValue("ORDER_CODE", table_m.getItemData(row, "ORDER_CODE"));
            this.setValue("ORDER_DESC", table_m.getItemData(row, "ORDER_DESC"));
            this.setValue("MATERIAL_LOC_CODE",
                          table_m.getItemData(row, "MATERIAL_LOC_CODE"));
            this.setValue("DISPENSE_FLG",
                          table_m.getItemData(row, "DISPENSE_FLG"));
            this.setValue("DISPENSE_ORG_CODE",
                          table_m.getItemData(row, "DISPENSE_ORG_CODE"));
            this.setValue("MM_USE_QTY", table_m.getItemData(row, "MM_USE_QTY"));
            this.setValue("DD_USE_QTY", table_m.getItemData(row, "DD_USE_QTY"));
            this.setValue("MAX_QTY", table_m.getItemData(row, "MAX_QTY"));
            this.setValue("SAFE_QTY", table_m.getItemData(row, "SAFE_QTY"));
            this.setValue("MIN_QTY", table_m.getItemData(row, "MIN_QTY"));
            this.setValue("ECONOMICBUY_QTY",
                          table_m.getItemData(row, "ECONOMICBUY_QTY"));
            this.setValue("BUY_UNRECEIVE_QTY",
                          table_m.getItemData(row, "BUY_UNRECEIVE_QTY"));
            this.setValue("STANDING_QTY",
                          table_m.getItemData(row, "STANDING_QTY"));
            this.setValue("QTY_TYPE", table_m.getItemData(row, "QTY_TYPE"));
            this.setValue("ACTIVE_FLG", table_m.getItemData(row, "ACTIVE_FLG"));
            if ("Y".equals(table_m.getItemData(row, "DISPENSE_FLG"))) {
                ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).
                    setEnabled(true);
            }
            else {
                ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).
                    setEnabled(false);
            }
            action = "updateM";

            // ��ϸ��Ϣ
            table_d.removeRowAll();
            table_d.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getINDStockAll(this.getValueString("ORG_CODE"),
                                             this.getValueString("ORDER_CODE"),
                                             Operator.getRegion()));
            tds.retrieve();
            if (tds.rowCount() == 0) {
                this.messageBox("û����ϸ��Ϣ");
            }

            // �۲���
            IndMainBatchValidObserver obser = new IndMainBatchValidObserver();
            tds.addObserver(obser);
            table_d.setDataStore(tds);
            table_d.setDSValue();
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        int row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            action = "updateD";
            table_m.setSelectionMode(0);
        }
    }


    /**
     * ���Ÿı��¼�
     */
    public void onChangeOrgCode() {
        this.setValue("MATERIAL_LOC_CODE", "");
        String org_code = this.getValueString("ORG_CODE");
        if (!"".equals(org_code)) {
            // �趨��λ
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                setOrgCode(org_code);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).onQuery();
        }

        TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getINDOrgType(org_code)));
        String org_type = "";
        this.setValue("DISPENSE_ORG_CODE", "");
        if ("C".equals(parm.getValue("ORG_TYPE", 0))) {
            org_type = "B";
        }
        else if ("B".equals(parm.getValue("ORG_TYPE", 0))) {
            org_type = "A";
        }
        else {
            org_type = "-";
        }
        ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).setOrgType(
            org_type);
        ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).onQuery();
    }

    /**
     * �����ı��¼�
     */
    public void onDispenseFlgAction() {
        if ("Y".equals(this.getValueString("DISPENSE_FLG"))) {
            ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).setEnabled(true);
        }
        else {
            ( (TComboOrgCode)this.getComponent("DISPENSE_ORG_CODE")).setEnabled(false);
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
        if (!StringUtil.isNullString(order_code)) {
            getTextField("ORDER_CODE").setValue(order_code);
        }
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("ORDER_DESC").setValue(order_desc);
        }
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
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("��沿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("ORDER_CODE"))) {
            this.messageBox("ҩƷ���Ʋ���Ϊ��");
            return false;
        }

        return true;
    }

}
