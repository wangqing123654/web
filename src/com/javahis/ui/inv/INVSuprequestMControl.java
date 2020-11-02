package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.TRadioButton;
import java.util.Vector;
import java.awt.Component;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import jdo.inv.InvSupRequestMTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.inv.InvSupRequestDTool;
import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TMenuItem;

/**
 * <p>Title: ������ҵ</p>
 *
 * <p>Description: ������ҵ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2013.5.16
 * @version 1.0
 */
public class INVSuprequestMControl
    extends TControl {

    // ��������
    private TTable tableM;
    // �Ĳ�����
    private TTable tableInv;
    // ����������
    private TTable tablePack;
    // ���ư�ע��
    private String type_flg;
    // ���ⷽʽ
    private String pack_mode;

    public INVSuprequestMControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableInv = getTable("TABLEINV");
        tablePack = getTable("TABLEPACK");

        // ��ʼ��TABLE_M��Parm
        TParm reqParm = new TParm();
        String[] reqM = {
            "REQUEST_NO", "SUPTYPE_CODE", "APP_ORG_CODE", "TO_ORG_CODE",
            "REQUEST_USER", "REQUEST_DATE", "REASON_CHN_DESC", "URGENT_FLG",
            "UPDATE_FLG", "DESCRIPTION"};
        for (int i = 0; i < reqM.length; i++) {
            reqParm.setData(reqM[i], new Vector());
        }
        tableM.setParmValue(reqParm);

        // ��ʼ��TABLE_D��Parm
        TParm invParm = new TParm();
        String[] reqInvD = {
            "REQUEST_NO", "SEQ_NO", "PACK_MODE", "SUPTYPE_CODE", "INV_CODE",
            "QTY", "STOCK_UNIT", "ACTUAL_QTY", "UPDATE_FLG", "OPT_USER",
            "OPT_DATE", "OPT_TERM", "INV_CHN_DESC", "SEQ_FLG"};
        for (int i = 0; i < reqInvD.length; i++) {
            invParm.setData(reqInvD[i], new Vector());
        }
        tableInv.setParmValue(invParm);
        TParm packParm = new TParm();
        String[] reqPackD = {
            "REQUEST_NO", "SEQ_NO", "PACK_MODE", "SUPTYPE_CODE", "INV_CODE",
            "QTY", "STOCK_UNIT", "ACTUAL_QTY", "UPDATE_FLG", "OPT_USER",
            "OPT_DATE", "OPT_TERM", "PACK_DESC", "SEQ_FLG"};
        for (int i = 0; i < reqPackD.length; i++) {
            packParm.setData(reqPackD[i], new Vector());
        }
        tablePack.setParmValue(packParm);

        // �������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REQUEST_DATE", date);
        this.setValue("REQUEST_USER", Operator.getID());

        //һ�����
        callFunction("UI|TABLEINV|addEventListener",
                     TTableEvent.CREATE_EDIT_COMPONENT, this,
                     "onCreateEditComponentInv");
        //����������
        callFunction("UI|TABLEPACK|addEventListener",
                     TTableEvent.CREATE_EDIT_COMPONENT, this,
                     "onCreateEditComponentPack");

        // ��������¼�
        addEventListener("TABLEINV->" + TTableEvent.CHANGE_VALUE,
                         "onTableINVChangeValue");
        // ��������¼�
        addEventListener("TABLEPACK->" + TTableEvent.CHANGE_VALUE,
                         "onTablePACKChangeValue");
    }

    /**
     * �������ʵ�������(һ�����ʳ���)
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComponentInv(Component com, int row, int column) {
        //����INV_BASE�Ի������
        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����INV_BASE��������
        textfield.setPopupMenuParameter("INVBASE", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVBasePopup.x"));
        //����text���ӽ���sys_fee�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newInv");
    }

    /**
     * һ������������ϸ
     * @param tag String
     * @param obj Object
     */
    public void newInv(String tag, Object obj) {
        if (obj == null)
            return;
        //invBase���ص����ݰ�
        TParm parm = (TParm) obj;
        //System.out.println("parm==="+parm);
        String invCode = parm.getValue("INV_CODE");
        TParm invParm = tableInv.getParmValue();
        int row = tableInv.getSelectedRow();
        for (int i = 0; i < invParm.getCount("INV_CODE"); i++) {
            if (i == row) {
                continue;
            }
            if (invCode.equals(invParm.getValue("INV_CODE", i))) {
                this.messageBox("������ͬ����!");
                tableInv.setItem(row, "INV_CODE", "");
                tableInv.setItem(row, "INV_CHN_DESC", "");
                return;
            }
        }
        tableInv.setItem(row, "REQUEST_NO", this.getValueString("REQUEST_NO"));
        tableInv.setItem(row, "INV_CODE", invCode);
        tableInv.setItem(row, "INV_CHN_DESC", parm.getValue("INV_CHN_DESC"));
        tableInv.setItem(row, "STOCK_UNIT", parm.getValue("STOCK_UNIT"));
        tableInv.setItem(row, "QTY", 1);
        tableInv.setItem(row, "OPT_USER", Operator.getID());
        tableInv.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        tableInv.setItem(row, "OPT_TERM", Operator.getIP());
        tableInv.setItem(row, "SEQ_FLG", parm.getValue("SEQMAN_FLG"));
        tableInv.acceptText();
        //System.out.println(tableInv.getParmValue());
    }

    /**
     * �������ʵ�������(������)
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComponentPack(Component com, int row, int column) {
        //����INV_PACK�Ի������
        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����INV_PACK��������
        TParm parm = new TParm();
        parm.setData("SEQ_FLG", "Y".equals(type_flg) ? "0" : "1");
        textfield.setPopupMenuParameter("PACK", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        //����text���ӽ���INV_PACK�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newPack");
    }

    /**
     * ������ϸ
     * @param tag String
     * @param obj Object
     */
    public void newPack(String tag, Object obj) {
        if (obj == null)
            return;
        //sysfee���ص����ݰ�
        TParm parm = (TParm) obj;
        //System.out.println("parm==="+parm);
        String invCode = parm.getValue("PACK_CODE");
        TParm packParm = tablePack.getParmValue();
        int row = tablePack.getSelectedRow();
        for (int i = 0; i < packParm.getCount("PACK_CODE"); i++) {
            if (i == row) {
                continue;
            }
            if (invCode.equals(packParm.getValue("PACK_CODE", i))) {
                this.messageBox("������ͬ������!");
                tablePack.setItem(row, "INV_CODE", "");
                tablePack.setItem(row, "PACK_DESC", "");
                return;
            }
        }
        tablePack.setItem(row, "REQUEST_NO", this.getValueString("REQUEST_NO"));
        tablePack.setItem(row, "INV_CODE", invCode);
        tablePack.setItem(row, "PACK_DESC", parm.getValue("PACK_DESC"));
        tablePack.setItem(row, "QTY", 1);
        tablePack.setItem(row, "OPT_USER", Operator.getID());
        tablePack.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        tablePack.setItem(row, "OPT_TERM", Operator.getIP());
        tablePack.setItem(row, "SEQ_FLG",
                          "0".equals(parm.getValue("SEQ_FLG")) ? "Y" : "N");
        tablePack.acceptText();
        //System.out.println(tablePack.getParmValue());
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableINVChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("��������С�ڻ����0");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTablePACKChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 2) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("��������С�ڻ����0");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (getRadioButton("UPDATE_A").isSelected()) {
            parm.setData("UPDATE_FLG_A", "Y");
        }
        else {
            parm.setData("UPDATE_FLG_B", "Y");
        }
        if (!"".equals(this.getValueString("SUPTYPE_CODE_Q"))) {
            parm.setData("SUPTYPE_CODE", getValueString("SUPTYPE_CODE_Q"));
        }
        if (!"".equals(this.getValueString("REQUEST_NO_Q"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO_Q"));
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
            parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE_Q"));
        }
        if (!"".equals(this.getValueString("APP_ORG_CODE_Q"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE_Q"));
        }

        TParm result = InvSupRequestMTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            tableM.removeRowAll();		//2013-8-1 ���
            tablePack.removeRowAll();	//2013-8-1 ���
            tableInv.removeRowAll();	//2013-8-1 ���
            return;
        }
        tableM.setParmValue(result);
        tablePack.removeRowAll();	//2013-8-1 ���
        tableInv.removeRowAll();	//2013-8-1 ���
    }

    /**
     * ��������
     */
    public void onNew() {
        if (tableM.getSelectedRow() < 0) {
            // �������쵥����
            if (!checkDataM()) {
                return;
            }
            //2013-08-01���
            if(tableM.getRowCount()>0){
            	return;
            }
            onNewRequestM();
        }
        else {
            // �������쵥ϸ��
            onNewRequestD();
        }
    }

    /**
     * �������쵥����
     */
    public void onNewRequestM() {
        //������
        int row = tableM.addRow();
        tableM.setItem(row, "REQUEST_NO", getValueString("REQUEST_NO"));
        tableM.setItem(row, "SUPTYPE_CODE", getValueString("SUPTYPE_CODE"));
        tableM.setItem(row, "TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        tableM.setItem(row, "APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        tableM.setItem(row, "REQUEST_USER", getValueString("REQUEST_USER"));
        tableM.setItem(row, "REQUEST_DATE", getValue("REQUEST_DATE"));
        tableM.setItem(row, "REASON_CHN_DESC", getValueString("REASON_CODE"));
        tableM.setItem(row, "URGENT_FLG",
                       "Y".equals(getValueString("URGENT_FLG")) ? "Y" : "N");
        tableM.setItem(row, "UPDATE_FLG", "0");
        tableM.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
        
        tableM.setSelectedRow(row);
    }

    /**
     * �������쵥ϸ��
     */
    public void onNewRequestD() {
        if ("0".equals(pack_mode)) {
            int row = tableInv.addRow();
            tableInv.setItem(row, "SEQ_NO", row + 1);
            tableInv.setItem(row, "PACK_MODE", pack_mode);
            tableInv.setItem(row, "SUPTYPE_CODE", getValueString("SUPTYPE_CODE"));
            tableInv.setItem(row, "UPDATE_FLG", "0");
        }
        else {
            int row = tablePack.addRow();
            tablePack.setItem(row, "SEQ_NO", row + 1);
            tablePack.setItem(row, "PACK_MODE", pack_mode);
            tablePack.setItem(row, "SUPTYPE_CODE",
                              getValueString("SUPTYPE_CODE"));
            tablePack.setItem(row, "UPDATE_FLG", "0");
        }
    }

    /**
     * �������ݼ��
     * @return boolean
     */
    private boolean checkDataM() {
        if ("".equals(this.getValueString("TO_ORG_CODE"))) {
            this.messageBox("��Ӧ���Ų���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("APP_ORG_CODE"))) {
            this.messageBox("���첿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("SUPTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        clearValue("SUPTYPE_CODE_Q;REQUEST_NO_Q;TO_ORG_CODE_Q;APP_ORG_CODE_Q;"
                   + "REQUEST_NO;TO_ORG_CODE;APP_ORG_CODE;REASON_CODE;"
                   + "SUPTYPE_CODE;PACK_MODE;URGENT_FLG");
        ( (TRadioButton)this.getComponent("UPDATE_B")).setSelected(true);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REQUEST_DATE", date);

        tableM.removeRowAll();
        tableInv.removeRowAll();
        tablePack.removeRowAll();
        tableInv.setVisible(true);
        tablePack.setVisible(false);
        onChangeRadioButton();
    }


    /**
     * ���淽��
     */
    public void onSave() {
        // ���ݼ��
        if (!checkDataM()) {
            return;
        }
        if ("0".equals(pack_mode)) {
            if (!checkDataINV()) {
                return;
            }
        }
        else {
            if (!checkDataPACK()) {
                return;
            }
        }
        TParm result = new TParm();
        TParm parm = new TParm();
        TParm tableMParm = new TParm();
        // ȡ�������������Ϣ
//2013-07-05ע��        getTableMData(tableMParm);
        tableMParm = getTableMData(tableMParm);		//07-05��
        // ȡ��ϸ���������Ϣ
        TParm tableDParm = new TParm();
//2013-07-05ע��        getTableDData(tableDParm);
        tableDParm = getTableDData(tableDParm);    //07-05��
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            // ��������
            String request_no = SystemTool.getInstance().getNo("ALL", "INV",
                "INV_SUPREQUEST", "No");
            tableMParm.setData("REQUEST_NO", request_no);
            parm.setData("INV_SUPREQUESTM", tableMParm.getData());
            for (int i = 0; i < tableDParm.getCount("SEQ_NO"); i++) {
                tableDParm.addData("REQUEST_NO", request_no);
            }
            parm.setData("INV_SUPREQUESTD", tableDParm.getData());
            // �������뵥
            result = TIOM_AppServer.executeAction(
                "action.inv.INVSupRequestAction", "onInsert", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.setValue("REQUEST_NO", request_no);
        }
        else {
            // ���±���
            String request_no = this.getValueString("REQUEST_NO");
            tableMParm.setData("REQUEST_NO", request_no);
            parm.setData("INV_SUPREQUESTM", tableMParm.getData());
            for (int i = 0; i < tableDParm.getCount("SEQ_NO"); i++) {
                tableDParm.addData("REQUEST_NO", request_no);
            }
            parm.setData("INV_SUPREQUESTD", tableDParm.getData());
            // �������뵥
            result = TIOM_AppServer.executeAction(
                "action.inv.INVSupRequestAction", "onUpdate", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
        }
        this.messageBox("P0001");
        onPrint();
        onClear();
    }

    /**
     * ȡ�������������Ϣ
     * @param parm TParm
     * @return TParm
     */
    private TParm getTableMData(TParm parm) {
        int row = tableM.getSelectedRow();
        parm.setData("SUPTYPE_CODE", tableM.getItemString(row, "SUPTYPE_CODE"));
        parm.setData("APP_ORG_CODE", tableM.getItemString(row, "APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", tableM.getItemString(row, "TO_ORG_CODE"));
        parm.setData("REQUEST_DATE",
                     tableM.getItemTimestamp(row, "REQUEST_DATE"));
        parm.setData("REQUEST_USER", tableM.getItemString(row, "REQUEST_USER"));
        parm.setData("REASON_CHN_DESC",
                     tableM.getItemString(row, "REASON_CHN_DESC"));
        parm.setData("DESCRIPTION", tableM.getItemString(row, "DESCRIPTION"));
        parm.setData("URGENT_FLG", tableM.getItemString(row, "URGENT_FLG"));
        parm.setData("UPDATE_FLG", tableM.getItemString(row, "UPDATE_FLG"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());  
        return parm;
    }

    /**
     * ȡ��ϸ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    private TParm getTableDData(TParm parm) {
        Timestamp datetime = SystemTool.getInstance().getDate();
        if ("0".equals(pack_mode)) {
            tableInv.acceptText();
            for (int i = 0; i < tableInv.getRowCount(); i++) {
                parm.addData("SEQ_NO", tableInv.getItemData(i, "SEQ_NO"));
                parm.addData("PACK_MODE", tableInv.getItemString(i, "PACK_MODE"));
                parm.addData("SUPTYPE_CODE",
                             tableInv.getItemString(i, "SUPTYPE_CODE"));
                parm.addData("INV_CODE",
                             tableInv.getParmValue().getValue("INV_CODE", i));
                parm.addData("QTY", tableInv.getItemDouble(i, "QTY"));
                parm.addData("STOCK_UNIT",
                             tableInv.getItemString(i, "STOCK_UNIT"));
                parm.addData("ACTUAL_QTY", 0);
                parm.addData("UPDATE_FLG",
                             tableInv.getItemString(i, "UPDATE_FLG"));
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", datetime);
                parm.addData("OPT_TERM", Operator.getIP());
            }
        }
        else {
            tablePack.acceptText();
            for (int i = 0; i < tablePack.getRowCount(); i++) {
                parm.addData("SEQ_NO", tablePack.getItemData(i, "SEQ_NO"));
                parm.addData("PACK_MODE",
                             tablePack.getItemString(i, "PACK_MODE"));
                parm.addData("SUPTYPE_CODE",
                             tablePack.getItemString(i, "SUPTYPE_CODE"));
                parm.addData("INV_CODE",
                             tablePack.getParmValue().getValue("INV_CODE", i));
                parm.addData("QTY", tablePack.getItemDouble(i, "QTY"));
                parm.addData("STOCK_UNIT", "");
                parm.addData("ACTUAL_QTY", 0);
                parm.addData("UPDATE_FLG",
                             tablePack.getItemString(i, "UPDATE_FLG"));
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", datetime);
                parm.addData("OPT_TERM", Operator.getIP());
            }  
        }
        return parm;
    }

    /**
     * ����ϸ��
     * @return boolean
     */
    private boolean checkDataINV() {
        tableInv.acceptText();
        TParm parmInv = tableInv.getParmValue();
        for (int i = 0; i < parmInv.getCount("INV_CODE"); i++) {
            if ("".equals(parmInv.getValue("INV_CODE", i))) {
                this.messageBox("�������ʲ���Ϊ��");
                return false;
            }
            if (parmInv.getDouble("QTY", i) <= 0) {
                this.messageBox("������������С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * ����ϸ��
     * @return boolean
     */
    private boolean checkDataPACK() {
        tablePack.acceptText();
        TParm parmPack = tablePack.getParmValue();
        for (int i = 0; i < parmPack.getCount("INV_CODE"); i++) {
            if ("".equals(parmPack.getValue("INV_CODE", i))) {
                this.messageBox("�������ʲ���Ϊ��");
                return false;
            }
            if (parmPack.getDouble("QTY", i) <= 0) {
                this.messageBox("������������С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int tableM_row = tableM.getSelectedRow();
        int tableInv_row = tableInv.getSelectedRow();
        int tablePack_row = tablePack.getSelectedRow();
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            // ֱ��ɾ��������������
            if ("0".equals(pack_mode)) {
                if (tableInv_row > 0) {
                    // ɾ��ϸ��
                    tableInv.removeRow(tableInv_row);
                }
                else {
                    // ɾ������
                    tableM.removeRow(tableM_row);
                    tableInv.removeRowAll();  //2013-8-1 ���
                }
            }
            else {
                if (tablePack_row > 0) {
                    // ɾ��ϸ��
                    tablePack.removeRow(tablePack_row);
                }
                else {
                    // ɾ������
                    tableM.removeRow(tableM_row);
                    tablePack.removeRowAll();	//2013-8-1 ���
                }
            }
        }
        else {
            TParm result = new TParm();
            // ɾ�����Ҹ�������
            if ("0".equals(pack_mode)) {
                if (tableInv_row > 0) {
                    // ɾ��ϸ��
                    result = InvSupRequestDTool.getInstance().onDelete(tableInv.
                        getParmValue().getRow(tableInv_row));
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    tableInv.removeRow(tableInv_row);
                    this.messageBox("ɾ���ɹ�");
                }
                else {
                    // ɾ������
                    if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������", 2) == 0) {
                        TParm parm = new TParm();
                        parm.setData("REQUEST_NO",
                                     this.getValueString("REQUEST_NO"));
                        result = TIOM_AppServer.executeAction(
                            "action.inv.INVSupRequestAction", "onDelete", parm);
                        if (result == null || result.getErrCode() < 0) {
                            this.messageBox("ɾ��ʧ��");
                            return;
                        }
                        tableM.removeRow(tableM_row);
                        this.messageBox("ɾ���ɹ�");
                        onClear();
                    }
                }
            }
            else {
                if (tablePack_row > 0) {
                    // ɾ��ϸ��
                    result = InvSupRequestDTool.getInstance().onDelete(
                        tablePack.getParmValue().getRow(tablePack_row));
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    tablePack.removeRow(tableInv_row);
                    this.messageBox("ɾ���ɹ�");
                }
                else {
                    // ɾ������
                    if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������", 2) == 0) {
                        TParm parm = new TParm();
                        parm.setData("REQUEST_NO",
                                     this.getValueString("REQUEST_NO"));
                        result = TIOM_AppServer.executeAction(
                            "action.inv.INVSupRequestAction", "onDelete", parm);
                        if (result == null || result.getErrCode() < 0) {
                            this.messageBox("ɾ��ʧ��");
                            return;
                        }
                        tableM.removeRow(tableM_row);
                        this.messageBox("ɾ���ɹ�");
                        onClear();
                    }
                }
            }
        }
    }

    /**
     * ��ӡ���쵥
     */
    public void onPrint() {
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "��Ӧ�����쵥");
        date.setData("REQUEST_NO", "TEXT",
                     "���쵥��: " + this.getValueString("REQUEST_NO"));
        date.setData("REQUEST_DATE", "TEXT", "��������: " +
                     this.getValueString("REQUEST_DATE").substring(0, 19).
                     replace('-', '/'));
        date.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 19).
                     replace('-', '/'));
        date.setData("TO_ORG_CODE", "TEXT", "��Ӧ����: " +
                     this.getTextFormat("TO_ORG_CODE").getText());
        date.setData("APP_ORG_CODE", "TEXT",
                     "���첿��: " + this.getTextFormat("APP_ORG_CODE").getText());
        date.setData("SUPTYPE_CODE", "TEXT",
                     "��Ӧ���: " + this.getTextFormat("SUPTYPE_CODE").getText());
        date.setData("PACK_MODE", "TEXT",
                     "���ⷽʽ: " + this.getComboBox("PACK_MODE").getSelectedName());
        // �������
        TParm parm = new TParm();
        if ("0".equals(pack_mode)) {
            // һ�����ʳ���
            for (int i = 0; i < tableInv.getRowCount(); i++) {
                parm.addData("INV_CHN_DESC",
                             tableInv.getItemString(i, "INV_CHN_DESC"));
                parm.addData("QTY", tableInv.getItemDouble(i, "QTY"));
                TParm stock_unit = new TParm(TJDODBTool.getInstance().select(
                    SYSSQL.getSYSUnit(tableInv.getItemString(i, "STOCK_UNIT"))));
                parm.addData("UNIT", stock_unit.getValue("UNIT_CHN_DESC", 0));
            }
            parm.setCount(parm.getCount("INV_CHN_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("CHECK", "TEXT", "��ˣ� ");
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupRequestInv.jhw",
                                 date);
        }
        else {
            // ����������
            for (int i = 0; i < tablePack.getRowCount(); i++) {
                parm.addData("PACK_DESC",
                             tablePack.getItemString(i, "PACK_DESC"));
                parm.addData("QTY", tablePack.getItemDouble(i, "QTY"));
            }
            parm.setCount(parm.getCount("PACK_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("CHECK", "TEXT", "��ˣ� ");
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupRequestPack.jhw",
                                 date);
        }
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableMClicked() {
        TParm tableMParm = tableM.getParmValue().getRow(tableM.getSelectedRow());
        if ("".equals(tableMParm.getValue("REQUEST_NO"))) {
            return;
        }
        this.setValueForParm("REQUEST_NO;REQUEST_USER;REQUEST_DATE;"
                             + "TO_ORG_CODE;APP_ORG_CODE;REASON_CODE;"
                             + "SUPTYPE_CODE;URGENT_FLG", tableMParm);
        onSupTypeSelected();
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        if ("0".equals(pack_mode)) {
            result = InvSupRequestDTool.getInstance().onQueryInv(parm);
            tableInv.setParmValue(result);
        }
        else {
            result = InvSupRequestDTool.getInstance().onQueryPack(parm);
            tablePack.setParmValue(result);
        }
    }

    /**
     * ��Ӧ���ı��¼�
     */
    public void onSupTypeSelected() {
        //��Ӧ���
        String sup_type = this.getValueString("SUPTYPE_CODE");
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getINVSupType(sup_type)));
        pack_mode = parm.getValue("PACK_MODE", 0);
        this.setValue("PACK_MODE", pack_mode);
        if ("0".equals(pack_mode)) {
            tableInv.setVisible(true);
            tablePack.setVisible(false);
        }
        else {
            tableInv.setVisible(false);
            tablePack.setVisible(true);
        }
        type_flg = parm.getValue("TYPE_FLG", 0);
    }

    /**
     * �����ѡ��ť
     */
    public void onChangeRadioButton() {
        if (getRadioButton("UPDATE_B").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("new")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("new")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
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
     * �õ�TRadioButton����
     * @param tagName String
     * @return TRadioButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
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
