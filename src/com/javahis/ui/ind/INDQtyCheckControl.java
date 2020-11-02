package com.javahis.ui.ind;

import jdo.ind.INDSQL;
import jdo.ind.IndQtyCheckTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.ind.INDTool;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 * <p>
 * Title: �̵����
 * </p>
 *
 * <p>
 * Description: �̵����
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
 * @author zhangy 2009.06.10
 * @version 1.0
 */
public class INDQtyCheckControl
    extends TControl {

    private String org_code;

    private String checkreason;

    private String frozen_date;

    private String check_type;

    // ������
    private TTable table_m;

    // ϸ����
    private TTable table_d;

    private TParm resultParm;

    private TPanel panel_1;

    private TPanel panel_2;

    private String header = "����,40,boolean;ҩƷ����,120;ҩƷ����,150;Ч��,120;"
        + "����,80;��λ,80;ʵ,60,double,#####0.000;��,60,UNIT_1;"
        + "��,60,double,#####0.000;��,60,UNIT_2";

    private String lockColumns = "0,1,2,3,4,5,7,9";

    private String columnHorizontal = "1,left;2,left;3,left;4,left;5,left;"
        + "6,right;7,left;8,right;9,left";

    private String parmMap = "STOCK_FLG;ORDER_CODE;ORDER_DESC;VALID_DATE;"
        + "BATCH_NO;MATERIAL_LOC_CODE;ACTUAL_QTY_F;STOCK_UNIT_A;ACTUAL_QTY_M;"
        + "DOSAGE_UNIT_A";

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    // �̵㶳����
    private boolean frozen_flg = true;

    // �̵�������
    private boolean qty_check_flg = true;

    // ���������
    private boolean unfrozen_flg = true;

    private String lock_column = "";

    public INDQtyCheckControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ע�ἤ��SYSFeePopup�������¼�
        callFunction("UI|ORDER_CODE|addEventListener", "ORDER_CODE->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD");
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��ʼ��������
        initPage();

        lock_column = table_d.getLockColumns();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        onChangeOrgCode();
        if (panel_1.isShowing()) {
            table_m.removeRowAll();
            if (!CheckData()) {
                return;
            }
            String sort = this.getValueString("PRINT_SORT");
            if ("0".equals(checkreason)) {
                // 0-ȫ���̵�
                TParm parm = new TParm(TJDODBTool.getInstance().select(
                    INDSQL.getQtyCheck(org_code, sort)));
                if (CheckQty(parm)) {
                    table_m.setParmValue(parm);
                    resultParm = parm;
                }
            }
            else {
                // 1-�����̵�
                int type_a_1 = 0;
                int type_a_2 = 0;
                int type_a_3 = 0;
                String order_code = "";
                String valid_date = "";
                String material_code = "";
                if ("A".equals(check_type)) {
                    // �̵㷽ʽ:A-��ʽ�����̵�
                    type_a_1 = TypeTool.getInt(this.getValue("TYPE_A_1"));
                    type_a_2 = TypeTool.getInt(this.getValue("TYPE_A_2"));
                    type_a_3 = TypeTool.getInt(this.getValue("TYPE_A_3"));
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheck(org_code, sort)));
                    if (CheckQty(parm)) {
                        TParm inparm = new TParm();
                        for (int i = 0; i < type_a_3; i++) {
                            inparm.setRowData(i, parm.getRow(type_a_1 - 1));
                            type_a_1 = type_a_1 + type_a_2;
                        }
                        table_m.setParmValue(inparm);
                        resultParm = inparm;
                    }
                }
                else if ("B".equals(check_type)) {
                    // �̵㷽ʽ:B-�������
                    order_code = this.getValueString("TYPE_B").toUpperCase();
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheckTypeB(org_code, order_code, sort)));
                    if (CheckQty(parm)) {
                        table_m.setParmValue(parm);
                        resultParm = parm;
                    }
                }
                else if ("C".equals(check_type)) {
                    // �̵㷽ʽ:C-�������(����ҩƷ)
                    order_code = this.getValueString("ORDER_CODE");
                    valid_date = this.getValueString("VALID_DATE");
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheckTypeC(org_code, order_code,
                                                valid_date, sort)));
                    if (CheckQty(parm)) {
                        table_m.setParmValue(parm);
                        resultParm = parm;
                    }
                }
                else {
                    // �̵㷽ʽ:D-��λ�����̵�
                    material_code = this.getValueString("MATERIAL_LOC_CODE");
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheckTypeD(org_code, material_code, sort)));
                    if (CheckQty(parm)) {
                        table_m.setParmValue(parm);
                        resultParm = parm;
                    }
                }
            }
            // ����������Ƿ���������жϵڶ�ҳ�Ƿ�Ϊ��Ч
            if (table_m.getRowCount() > 0) {
                ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(
                    1, true);
            }
            else {
                ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(
                    1, false);
            }
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        table_d.acceptText();
        // �ж϶���ʱ��
        if ("".equals(this.getValueString("FROZEN_DATE_I"))) {
            this.messageBox("����ʱ�䲻��Ϊ��");
            return;
        }
        // ѭ���ж��̵�Grid���Ƿ���δ���������
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "STOCK_FLG"))) {
                this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                "�̵�������ǰ���ȶ���");
                return;
            }
        }
        // �ж��Ƿ���Ҫ��������Ч�ڵĲ�ѯ
        String valid_flg = this.getValueString("VALID_FLG_I");
        // ����ǰ���¼�������
        if (!checkQueData("SAVE", valid_flg)) {
            return;
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("FROZEN_DATE", frozen_date);
        parm.setData("ACTUAL_CHECKQTY_USER", Operator.getID());
        parm.setData("ACTUAL_CHECKQTY_DATE", date);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm parm_D = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            parm_D.setData("ORDER_CODE", i,
                           table_d.getParmValue().getData("ORDER_CODE", i));
            parm_D.setData("BATCH_SEQ", i,
                           table_d.getParmValue().getData("BATCH_SEQ", i));
            double qty = table_d.getItemDouble(i, "ACTUAL_QTY_F") *
                table_d.getParmValue().getDouble("DOSAGE_QTY", i) +
                table_d.getItemDouble(i, "ACTUAL_QTY_M");
            parm_D.setData("QTY", i, qty);
        }
        parm.setData("PARM_D", parm_D.getData());
        // �����̵�����
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDQtyCheckAction", "onUpdate", parm);
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("�̵㱣��ʧ��");
            return;
        }
        this.messageBox("�̵㱣��ɹ�\n��ǵý������");
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������Page1
        String clearStringPage1 =
            "ORG_CODE;PRINT_SORT;STOCK_PRICE_FLG;CHECKREASON_CODE;FROZEN_DATE;"
            + "UNFREEZE_DATE;CHECK_TYPE;TYPE_A_1;TYPE_A_2;TYPE_A_3;"
            + "TYPE_B;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE";
        clearValue(clearStringPage1);
        // ��ջ�������Page2
        String clearStringPage2 =
            "FROZEN_DATE_I;ORDER_CODE_I;ORDER_DESC_I;ACTIVE_FLG_I;UNFREEZE_DATE_I;"
            + "MATERIAL_LOC_CODE_I;VALID_FLG_I";
        clearValue(clearStringPage2);

        // ��ʼ��ҳ��״̬������
        table_m.removeRowAll();
        table_m.setSelectionMode(0);
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        getComboBox("CHECK_TYPE").setSelectedIndex(0);
        getComboBox("CHECK_TYPE").setEnabled(false);
        setCheckTypeStatus(false, false, false, false);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(0, true);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(1, false);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setSelectedIndex(0);
        ( (TMenuItem) getComponent("save")).setEnabled(false);
        ( (TMenuItem) getComponent("lock")).setEnabled(false);
        ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        ( (TMenuItem) getComponent("query")).setEnabled(true);
        ( (TMenuItem) getComponent("clear")).setEnabled(true);
    }

    /**
     * ���᷽��
     */
    public void onLock() {
        // ��鲿���Ƿ����ι���
        if (!getOrgBatchFlg(org_code)) {
            return;
        }
        // �ж��Ƿ����δ�ⶳ��ҩƷ
        TParm parm = table_m.getParmValue();
        boolean flg = false;
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            if ("Y".equals(parm.getValue("STOCK_FLG", i))) {
                flg = true;
                break;
            }
        }
        if (flg) {
            this.messageBox("����ǰ���Ƚ������");
            return;
        }
        // ����ҩƷ
        Timestamp date = StringTool.getTimestamp(new Date());
        String frozen_date = date.toString();
        frozen_date = frozen_date.substring(0, 4)
            + frozen_date.substring(5, 7)
            + frozen_date.substring(8, 10)
            + frozen_date.substring(11, 13)
            + frozen_date.substring(14, 16)
            + frozen_date.substring(17, 19);
        TParm inparm = new TParm();
        inparm.setData("ORG_CODE", org_code);
        inparm.setData("FROZEN_DATE", frozen_date);
        inparm.setData("ORDER", parm.getData());
        inparm.setData("CHECKREASON_CODE", checkreason);
        if (check_type == null) {
            check_type = "";
        }
        inparm.setData("CHECK_TYPE", check_type);
        inparm.setData("MODI_QTY", 0);
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", date);
        inparm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDQtyCheckAction",
            "onLock", inparm);
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        onQuery();
    }

    /**
     * �ⶳ����
     */
    public void onUnLock() {
        // ��˲����Ƿ������ι���״̬
        if (!getOrgBatchFlg(org_code)) {
            return;
        }
        // �ж϶���ʱ�䲻��Ϊ��
        if ("".equals(getValueString("FROZEN_DATE_I")) ||
            this.getValue("FROZEN_DATE_I") == null) {
            this.messageBox("����ʱ�䲻��Ϊ��");
            return;
        }
        // �ж��Ƿ���Խ������
        if (!"".equals(getValueString("UNFREEZE_DATE_I")) ||
            this.getValue("UNFREEZE_DATE_I") != null) {
            this.messageBox("�ñʶ��������Ѿ��������,�����ظ��������!");
            return;
        }
        // �ж��̵�����
        if (table_d.getRowCount() > 0) {
            Timestamp date = StringTool.getTimestamp(new Date());
            // �̵�ⶳ
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", org_code);
            inparm.setData("FROZEN_DATE", frozen_date);
            inparm.setData("VALID_FLG", this.getValueString("VALID_FLG_I"));
            inparm.setData("OPT_USER", Operator.getID());
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", Operator.getIP());
            TParm parm_D = new TParm();
            for (int i = 0; i < table_d.getRowCount(); i++) {
                parm_D.setData("ORDER_CODE", i,
                               table_d.getItemString(i, "ORDER_CODE"));
                parm_D.setData("BATCH_SEQ", i,
                               table_d.getParmValue().getInt("BATCH_SEQ", i));
            }
            inparm.setData("PARM_D", parm_D.getData());
            //System.out.println("parm--" + inparm);
            TParm result = TIOM_AppServer.executeAction(
                "action.ind.INDQtyCheckAction",
                "onUnLock", inparm);
            // �����ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("�������ʧ��");
                return;
            }
            this.messageBox("�������ɹ�");
            onClear();
        }
        else {
            this.messageBox("�޽���������ϵ�����");
            return;
        }
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ���̵㲿��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Object result = openWindow("%ROOT%\\config\\ind\\INDQtyCheckPrt.x",
                                   parm);
    }

    /**
     * ���Ÿı��¼�
     */
    public void onChangeOrgCode() {
        table_m.removeRowAll();
        org_code = this.getValueString("ORG_CODE");
        if (!"".equals(org_code)) {
            // ��ѯ����ʱ��
            TParm parm = new TParm();
            parm.setData("ORG_CODE", org_code);
            parm = IndQtyCheckTool.getInstance().onQueryFrozenDate(parm);
            String frozen_date = "";
            for (int i = 0; i < parm.getCount("FROZEN_DATE"); i++) {
                frozen_date = parm.getValue("FROZEN_DATE", i);
                frozen_date = frozen_date.substring(0, 4) + "/"
                    + frozen_date.substring(4, 6) + "/"
                    + frozen_date.substring(6, 8) + " "
                    + frozen_date.substring(8, 10) + ":"
                    + frozen_date.substring(10, 12) + ":"
                    + frozen_date.substring(12, 14);
                parm.setData("F_DATE", i, frozen_date);
            }
            getComboBox("FROZEN_DATE").setParmValue(parm);
            getComboBox("FROZEN_DATE").setSelectedIndex(0);
            getComboBox("FROZEN_DATE_I").setParmValue(parm);
            getComboBox("FROZEN_DATE_I").setSelectedIndex(0);
            this.setValue("UNFREEZE_DATE", null);
            // �趨��λ
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                setOrgCode(org_code);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).onQuery();
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE_I")).
                setOrgCode(org_code);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE_I")).
                onQuery();
        }
        else {
            getComboBox("FROZEN_DATE").removeAllItems();
            getComboBox("FROZEN_DATE_I").removeAllItems();
            this.setValue("UNFREEZE_DATE", null);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                removeAllItems();
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE_I")).
                removeAllItems();
        }
    }

    /**
     * �̵�ԭ��ı��¼�
     */
    public void onChangeCheckReason() {
        checkreason = this.getValueString("CHECKREASON_CODE");
        if (!"1".equals(checkreason)) {
            getComboBox("CHECK_TYPE").setSelectedIndex(0);
            getComboBox("CHECK_TYPE").setEnabled(false);
            if (getComboBox("FROZEN_DATE").rowCount() > 0) {
                getComboBox("FROZEN_DATE").setSelectedIndex(0);
                getComboBox("FROZEN_DATE_I").setSelectedIndex(0);
            }
            this.setValue("UNFREEZE_DATE", null);
            setCheckTypeStatus(false, false, false, false);
        }
        else {
            getComboBox("CHECK_TYPE").setEnabled(true);
            getComboBox("FROZEN_DATE").setSelectedIndex(0);
            getComboBox("FROZEN_DATE_I").setSelectedIndex(0);
            this.setValue("UNFREEZE_DATE", null);
        }
        check_type = getComboBox("CHECK_TYPE").getSelectedID();
    }

    /**
     * ����ʱ��ı��¼�
     */
    public void onChangeFrozenDate() {
        frozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (!"".equals(frozen_date)) {
            ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(1, true);
            //getComboBox("CHECK_TYPE").setSelectedIndex(0);
            //getComboBox("CHECK_TYPE").setEnabled(false);
            //getComboBox("CHECKREASON_CODE").setSelectedIndex(0);
            TParm parm = new TParm();
            parm.setData("ORG_CODE", org_code);
            parm.setData("FROZEN_DATE", frozen_date);
            //this.messageBox(getComboBox("FROZEN_DATE").getSelectedID());
            parm = IndQtyCheckTool.getInstance().onQuery(parm);
            this.setValue("FROZEN_DATE", frozen_date);
            this.setValue("UNFREEZE_DATE", parm.getData("UNFREEZE_DATE", 0));
            //this.messageBox(getComboBox("FROZEN_DATE").getSelectedID());
            // ��ѯ��������
            String sql = INDSQL.getQtyCheckHistoryInfo(org_code, frozen_date);
            parm = new TParm(TJDODBTool.getInstance().select(sql));
            table_m.setParmValue(parm);
        }
    }

    /**
     * ����ʱ��ı��¼�
     */
    public void onChangeFrozenDateI() {
        frozen_date = getComboBox("FROZEN_DATE_I").getSelectedID();
        //this.messageBox(frozen_date);
        if (!"".equals(frozen_date)) {
            TParm parm = new TParm();
            parm.setData("ORG_CODE", org_code);
            parm.setData("FROZEN_DATE", frozen_date);
            //System.out.println("parm---"+parm);
            parm = IndQtyCheckTool.getInstance().onQuery(parm);
            if ("".equals(parm.getValue("UNFREEZE_DATE", 0))) {
                this.setValue("UNFREEZE_DATE_I","");// by liyh 20120725 ���û�ⶳʱ�� �͸�ֵΪ�� 
                table_d.setLockColumns(lock_column);
            }
            else {
                this.setValue("UNFREEZE_DATE_I",
                              parm.getData("UNFREEZE_DATE", 0));
                table_d.setLockColumns("all");
            }
            // ��ѯ��������
            frozenDateSelect(frozen_date);
        }
    }

    /**
     * ��ʾ�������Ÿı��¼�
     */
    public void onCheckActiveFlg() {
        onChangeFrozenDateI();
    }

    /**
     * ��Ч�������̵�ı��¼�
     */
    public void onCheckValidFlg() {
        onChangeFrozenDateI();
    }

    /**
     * �̵㷽ʽ�ı��¼�
     */
    public void onChangeCheckType() {
        check_type = getComboBox("CHECK_TYPE").getSelectedID();
        if ("A".equals(check_type)) {
            setCheckTypeStatus(true, false, false, false);
        }
        else if ("B".equals(check_type)) {
            setCheckTypeStatus(false, true, false, false);
        }
        else if ("C".equals(check_type)) {
            setCheckTypeStatus(false, false, true, false);
        }
        else if ("D".equals(check_type)) {
            setCheckTypeStatus(false, false, false, true);
        }
        else {
            setCheckTypeStatus(false, false, false, false);
        }
    }

    /**
     * �������ҳ
     */
    public void onChangeTTabbedPane() {
        if ( ( ( (TTabbedPane)this.getComponent("TabbedPane_1"))).
            getSelectedIndex() == 0) {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("query")).setEnabled(true);
            ( (TMenuItem) getComponent("lock")).setEnabled(false);
            ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("query")).setEnabled(false);
            ( (TMenuItem) getComponent("lock")).setEnabled(false);
            if (unfrozen_flg) {
                ( (TMenuItem) getComponent("unlock")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("unlock")).setEnabled(false);
            }
        }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        int row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            table_d.setSelectionMode(0);
            // ȡ��SYS_FEE��Ϣ��������״̬����
            String order_code = table_m.getParmValue().getValue("ORDER_CODE",
                table_m.getSelectedRow());
            if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
            }
            else {
                callFunction("UI|setSysStatus", "");
            }
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        int row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            table_m.setSelectionMode(0);
            // ȡ��SYS_FEE��Ϣ��������״̬����
            String order_code = table_d.getParmValue().getValue("ORDER_CODE",
                table_d.getSelectedRow());
            if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
            }
            else {
                callFunction("UI|setSysStatus", "");
            }
        }
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
        // Table����
        int column = node.getColumn();
        int row = node.getRow();
        int column_stock = 9;
        int column_dosage = 11;
        if (qty_check_flg) {
            column_stock = 9;
            column_dosage = 11;
        }
        else {
            column_stock = 6;
            column_dosage = 8;
        }
        if (column == column_stock) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("��浥λ��������С��0");
                return true;
            }
            getStockModiQty(row, qty);
        }
        if (column == column_dosage) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("��ҩ��λ��������С��0");
                return true;
            }
            getDosageModiQty(row, qty);
        }
        return false;
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
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn_I(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_I").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_I").setValue(order_desc);
    }

    /**
     * ��λҩƷ����
     */
    public void onOrientationAction() {
        if ("".equals(this.getValueString("ORDER_CODE_I"))) {
            this.messageBox("�����붨λҩƷ");
            return;
        }
        boolean flg = false;
        TParm parm = table_d.getParmValue();
        String order_code = this.getValueString("ORDER_CODE_I");
        int row = table_d.getSelectedRow();
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
            table_d.setSelectedRow(row);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                  " AND B.ORG_TYPE IN ('A', 'B', 'C') ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        // �̵㶳����
        if (!this.getPopedem("FROZEN")) {
            frozen_flg = false;
        }

        // ���������
        if (!this.getPopedem("UNFROZEN")) {
            unfrozen_flg = false;
        }

        // �̵�������
        if (!this.getPopedem("QTY_CHECK")) {
            qty_check_flg = false;
            getTable("TABLE_D").setHeader(header);
            getTable("TABLE_D").setLockColumns(lockColumns);
            getTable("TABLE_D").setColumnHorizontalAlignmentData(
                columnHorizontal);
            getTable("TABLE_D").setParmMap(parmMap);
        }

        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        panel_1 = getPanel("TPanel_1");
        panel_2 = getPanel("TPanel_2");
        ( (TMenuItem) getComponent("save")).setEnabled(false);
        ( (TMenuItem) getComponent("lock")).setEnabled(false);
        ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        //resultParm = new TParm();
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(0, true);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(1, false);

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");


        // ���õ����˵�
        getTextField("ORDER_CODE_I").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE_I").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn_I");

    }

    /**
     * ���ݼ��
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("���Ŵ��벻��Ϊ�հ�");
            return false;
        }
        if ("".equals(this.getValueString("CHECKREASON_CODE"))) {
            this.messageBox("�̵�ԭ�򲻿�Ϊ�հ�");
            return false;
        }
        if ("1".equals(this.getValueString("CHECKREASON_CODE"))
            && "".equals(this.getValueString("CHECK_TYPE"))) {
            this.messageBox("�̵㷽ʽ����Ϊ�հ�");
            return false;
        }
        if ("1".equals(this.getValueString("CHECKREASON_CODE"))) {
            if ("B".equals(this.getValueString("CHECK_TYPE"))
                && "".equals(this.getValueString("TYPE_B"))) {
                this.messageBox("��ͷҩƷ���벻��Ϊ�հ�");
                return false;
            }
            if ("C".equals(this.getValueString("CHECK_TYPE"))
                && "".equals(this.getValueString("ORDER_CODE"))) {
                this.messageBox("ҩƷ���벻��Ϊ�հ�");
                return false;
            }
            if ("D".equals(this.getValueString("CHECK_TYPE"))
                && "".equals(this.getValueString("MATERIAL_LOC_CODE"))) {
                this.messageBox("��λ���벻��Ϊ�հ�");
                return false;
            }
        }
        return true;
    }

    /**
     * �̵��ѯ���
     * @param parm TParm
     */
    public boolean CheckQty(TParm parm) {
        //System.out.println(parm);
        if (parm != null && parm.getCount("ORDER_CODE") > 0) {
            if (frozen_flg) {
                ( (TMenuItem) getComponent("lock")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("lock")).setEnabled(false);
            }
            ( (TMenuItem) getComponent("unlock")).setEnabled(false);
            return true;
        }
        this.messageBox("û���̵�����");
        ( (TMenuItem) getComponent("lock")).setEnabled(false);
        ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        return false;
    }

    /**
     * ����Ƿ���춯״̬�ж�
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // ����Ƿ���춯״̬�ж�
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
            return false;
        }
        return true;
    }

    /**
     * ����ѡ��Ķ���ʱ���ѯ��������
     */
    private void frozenDateSelect(String frozen_date) {
        //ҩƷ����
        String order_code = "";
        //B:ҩƷ����Ĵ���
        if ("B".equals(check_type)) {
            order_code = this.getValueString("TYPE_B").toUpperCase();
        }
        else if ("C".equals(check_type)) {
            order_code = this.getValueString("ORDER_CODE");
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("CHECKREASON_CODE", this.getValueString("CHECKREASON_CODE"));
        parm.setData("CHECK_TYPE", check_type == null ? "" : check_type);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("VALID_DATE",
                     this.getValueString("VALID_DATE") == null ? "" :
                     this.getValueString("VALID_DATE"));
        parm.setData("MATERIAL_LOC_CODE",
                     this.getValueString("MATERIAL_LOC_CODE"));
        parm.setData("SORT", this.getValueString("PRINT_SORT"));
        parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG_I"));
        parm.setData("VALID_FLG", this.getValueString("VALID_FLG_I"));
        parm.setData("FROZEN_DATE", frozen_date);
        TParm result = IndQtyCheckTool.getInstance().onQueryQtyCheck(parm);
        table_d.setParmValue(result);
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
     * �õ�TNumberTextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TNumberTextField getNumberTextField(String tagName) {
        return (TNumberTextField) getComponent(tagName);
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
     * �趨�̵�����ַ�ʽ��״̬
     */
    private void setCheckTypeStatus(boolean type1, boolean type2,
                                    boolean type3, boolean type4) {
        String clearString = "TYPE_A_1;TYPE_A_2;TYPE_A_3;TYPE_B;ORDER_CODE;"
            + "ORDER_DESC;MATERIAL_LOC_CODE";
        clearValue(clearString);
        getNumberTextField("TYPE_A_1").setEnabled(type1);
        getNumberTextField("TYPE_A_2").setEnabled(type1);
        getNumberTextField("TYPE_A_3").setEnabled(type1);
        getTextField("TYPE_B").setEnabled(type2);
        getTextField("ORDER_CODE").setEnabled(type3);
        getComboBox("VALID_DATE").setEnabled(type3);
        getComboBox("MATERIAL_LOC_CODE").setEnabled(type4);
    }

    /**
     * ���ݼ��
     * @param type String
     * @param flg String
     * @return boolean
     */
    private boolean checkQueData(String type, String flg) {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // �ж�ҩƷת����
            if (table_d.getParmValue().getData("DOSAGE_QTY", i) == null) {
                this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                "ת���ʴ���");
                return false;
            }
            // �ж�ԭ�����
            if (table_d.getParmValue().getData("STOCK_QTY_F", i) == null ||
                table_d.getParmValue().getData("STOCK_QTY_M", i) == null) {
                this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                "ԭ�����Ϊ��");
                return false;
            }
            // �ж��������--�����������Ч����ʾ,batch_seq��Ϊ��,�ұ���ͽ������ʱ����ʹ��;����Ͳ���Ϊ��
            if ("N".equals(flg)) {
                if (table_d.getItemData(i, "BATCH_NO") == null ||
                    table_d.getItemData(i, "VALID_DATE") == null) {
                    this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                    "���Ż�Ч��Ϊ��");
                    return false;
                }
            }
            // ����Ǳ���,��Ҫȥ���ʵ��¼�������
            if ("SAVE".equals(type)) {
                // �ж�ʵ��¼���浥λ����
                if (table_d.getItemData(i, "ACTUAL_QTY_F") == null) {
                    this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                    "ʵ��¼���浥λ����Ϊ��");
                    return false;
                }
                // �ж�ʵ��¼�뷢ҩ��λ����
                if (table_d.getItemData(i, "ACTUAL_QTY_M") == null) {
                    this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                    "ʵ��¼�뷢ҩ��λ����Ϊ��");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ��������̲�
     *
     * @return
     */
    private void getStockModiQty(int row, double qty) {
        table_d.acceptText();
        double rate = table_d.getParmValue().getDouble("DOSAGE_QTY", row);
        //this.messageBox(rate+"");
        if (qty_check_flg) {
            double stock_qty = table_d.getItemDouble(row,
                "STOCK_QTY_F");
            double actual_qty_m = table_d.getItemDouble(row,
                "ACTUAL_QTY_M");
            table_d.setItem(row, "MODI_QTY",
                            qty * rate + actual_qty_m - stock_qty);
        }
        else {
            double stock_qty = table_d.getParmValue().getDouble(
                "STOCK_QTY_F", row);
            double actual_qty_m = table_d.getItemDouble(row,
                "ACTUAL_QTY_M");
            table_d.getParmValue().setData("MODI_QTY", row,
                                           qty * rate + actual_qty_m -
                                           stock_qty);
        }
    }

    /**
     * ��������̲�
     *
     * @return
     */
    private void getDosageModiQty(int row, double qty) {
        table_d.acceptText();
        double rate = table_d.getParmValue().getDouble("DOSAGE_QTY", row);
        //this.messageBox(rate+"");
        if (qty_check_flg) {
            double stock_qty = table_d.getItemDouble(row,
                "STOCK_QTY_F");
            double actual_qty_f = table_d.getItemDouble(row,
                "ACTUAL_QTY_F");
            table_d.setItem(row, "MODI_QTY",
                            qty + actual_qty_f * rate - stock_qty);
        }
        else {
            double stock_qty = table_d.getParmValue().getDouble(
                "STOCK_QTY_F", row);
            double actual_qty_f = table_d.getItemDouble(row,
                "ACTUAL_QTY_F");
            table_d.getParmValue().setData("MODI_QTY", row,
                                           qty + actual_qty_f * rate -
                                           stock_qty);
        }
    }

    /**
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }
}
