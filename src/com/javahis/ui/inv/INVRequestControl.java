package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.javahis.system.textFormat.TextFormatINVReason;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import java.util.Date;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import com.dongyang.ui.TComboBox;
import com.javahis.util.StringUtil;
import java.util.Vector;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import jdo.inv.InvRequestDTool;

/**
 * <p>
 * Title: ����������ҵControl
 * </p>
 *
 * <p>
 * Description: ����������ҵControl
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
 * @author zhangy 2009.11.17
 * @version 1.0
 */

public class INVRequestControl
    extends TControl {
    public INVRequestControl() {
    }

    private TTable table_m;

    private TTable table_d;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        if (!dept_flg) {
            if ("".equals(this.getValueString("TO_ORG_CODE_Q"))) {
                this.messageBox("��ѡ���ѯ����");
                return;
            }
        }

        TParm parm = new TParm();
        // ����״̬
        if (this.getRadioButton("RadioButton1").isSelected()) {
            parm.setData("FINAL_FLG", "Y");
        }
        else {
            parm.setData("FINAL_FLG", "N");
        }
        // �������
        if (!"".equals(this.getValueString("REQUEST_TYPE_Q"))) {
            parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE_Q"));
        }
        // ���벿��
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
            parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE_Q"));
        }
        // ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        // ���뵥��
        if (!"".equals(this.getValueString("REQUEST_NO_Q"))) {
            parm.setData("REQUEST_NO", this.getValueString("REQUEST_NO_Q"));
        }
        TParm inparm = new TParm();
        inparm.setData("REQ_M", parm.getData());
        // ��ѯ
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVRequestAction", "onQueryM", inparm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            table_m.removeRowAll();
            return;
        }
        table_m.setParmValue(result);

    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        getRequestMData(parm); // ȡ�����뵥��������
        getRequestDData(parm); // ȡ�����뵥ϸ������
        TParm result = new TParm();
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            // �������뵥
            result = TIOM_AppServer.executeAction(
                "action.inv.INVRequestAction", "onInsert", parm);
        }
        else {
            if (!checkUpdateFlg()) {
                this.messageBox("���뵥���������ѳ��ⲻ�ɸ���");
                return;
            }
            // �������뵥
            result = TIOM_AppServer.executeAction(
                "action.inv.INVRequestAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ��շ���
     */
    public void onClear(){
        getRadioButton("RadioButton2").setSelected(true);
        getComboBox("REQUEST_TYPE").setEnabled(true);
        getTextFormat("TO_ORG_CODE").setEnabled(true);
        getTextFormat("FROM_ORG_CODE").setEnabled(true);
        String clearString =
            "START_DATE;END_DATE;REQUEST_TYPE_Q;TO_ORG_CODE_Q;REQUEST_NO_Q;"
            + "REQUEST_NO;REQUEST_DATE;TO_ORG_CODE;FROM_ORG_CODE;"
            + "REN_CODE;REMARK;FINAL_FLG;URGENT_FLG;REQUEST_TYPE";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REQUEST_DATE", date);
        // ����״̬
        //( (TMenuItem) getComponent("delete")).setEnabled(false);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        createNewRow();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        if (row_d >= 0) {
            // ɾ�����뵥ϸ��
            if ("".equals(this.getValueString("REQUEST_NO")) &&
                !"".equals(table_d.getItemString(row_d, "INV_CHN_DESC"))) {
                table_d.removeRow(row_d);
                return;
            }
            else if ("".equals(this.getValueString("REQUEST_NO")) &&
                     "".equals(table_d.getItemString(row_d, "INV_CHN_DESC"))) {
                return;
            }
            else if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥ϸ��", 2) == 0) {
                if (!checkUpdateFlg()) {
                    this.messageBox("���뵥�����������ѳ��ⲻ��ɾ��");
                    return;
                }
                table_d.removeRow(row_d);
                this.onSave();
                this.messageBox("ɾ���ɹ�");
            }
        }
        else if (row_m >= 0) {
            // ɾ�����뵥����
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥", 2) == 0) {
                if (!checkUpdateFlg()) {
                    this.messageBox("���뵥���������ѳ��ⲻ��ɾ��");
                    return;
                }
                result = TIOM_AppServer.executeAction(
                    "action.inv.INVRequestAction", "onDelete", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                table_m.removeRow(row_m);
                table_d.removeRowAll();
                this.messageBox("ɾ���ɹ�");
            }
        }
        else {
            this.messageBox("û��ѡ����");
            return;
        }
    }


    /**
     * ������뵥�Ƿ��г�������
     * @return boolean
     */
    private boolean checkUpdateFlg() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if (table_d.getItemDouble(i, "ACTUAL_QTY") > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            getComboBox("REQUEST_TYPE").setEnabled(false);
            getTextFormat("TO_ORG_CODE").setEnabled(false);
            getTextFormat("FROM_ORG_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table_d.setSelectionMode(0);
            // ������Ϣ(TABLE��ȡ��)
            setValue("REQUEST_DATE", table_m.getItemTimestamp(row,
                "REQUEST_DATE"));
            setValue("REQUEST_NO", table_m.getItemString(row, "REQUEST_NO"));
            setValue("REQUEST_TYPE", table_m.getItemString(row, "REQUEST_TYPE"));
            setValue("TO_ORG_CODE", table_m.getItemString(row, "TO_ORG_CODE"));
            setValue("FROM_ORG_CODE",
                     table_m.getItemString(row, "FROM_ORG_CODE"));
            setValue("REN_CODE",
                     table_m.getItemString(row, "REN_CODE"));
            setValue("REMARK", table_m.getItemString(row, "REMARK"));
            setValue("FINAL_FLG",
                     "Y".equals(table_m.getParmValue().getData("FINAL_FLG", row)) ? "���" :
                     "δ���");
            setValue("URGENT_FLG", table_m.getItemString(row, "URGENT_FLG"));

            // ��ϸ��Ϣ
            TParm parm = new TParm();
            parm.setData("REQUEST_NO",
                         table_m.getItemString(row, "REQUEST_NO"));

            TParm result = InvRequestDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û��������ϸ");
                return;
            }
            table_d.removeRowAll();
            table_d.setParmValue(result);
            createNewRow();
        }
    }


    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("REQUEST_TYPE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("TO_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if(!this.getValueString("REQUEST_TYPE").equals("WAS")){
        	 if ("".equals(this.getValueString("FROM_ORG_CODE"))) {
                 this.messageBox("���ܲ��Ų���Ϊ��");
                 return false;
             }	
        }
       
        if (table_d.getRowCount() < 1) {
            this.messageBox("û������ϸ����Ϣ");
            return false;
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if (table_d.getItemDouble(i, "QTY") <= 0) {
                    this.messageBox("������������С�ڻ����0");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ȡ�����뵥��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestMData(TParm parm) {
        TParm inparm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        // ���뵥��
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            inparm.setData("REQUEST_NO",
                           SystemTool.getInstance().getNo("ALL", "INV",
                "INV_REQUEST", "No"));
        }
        else {
            inparm.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        }
        // �������
        inparm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE"));
        // ��������
        inparm.setData("REQUEST_DATE", this.getValue("REQUEST_DATE"));
        // �������벿��
        inparm.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
        // ���벿��
        inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        // ����ԭ��
        inparm.setData("REN_CODE", this.getValueString("REN_CODE"));
        // ����ע��
        inparm.setData("URGENT_FLG", this.getValueString("URGENT_FLG"));
        // ��ע
        inparm.setData("REMARK", this.getValue("REMARK"));
        // ����״̬
        inparm.setData("FINAL_FLG", "N");
        // ʵ�ʽ��
        inparm.setData("ACTUAL_AMT", this.getValue("SUM_MONEY"));
        // OPT
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", date);
        inparm.setData("OPT_TERM", Operator.getIP());

        parm.setData("REQ_M", inparm.getData());
        return parm;
    }

    /**
     * ȡ�����뵥ϸ������
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestDData(TParm parm) {
        TParm inparm = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            // 1.���뵥��
            inparm.addData("REQUEST_NO",
                           parm.getParm("REQ_M").getValue("REQUEST_NO"));
            // 2.���
            inparm.addData("SEQ_NO", i+1);
            // 3.���ʴ���
            inparm.addData("INV_CODE",
                           table_d.getParmValue().getValue("INV_CODE", i));
            // 4.�������
            inparm.addData("INVSEQ_NO",
                           table_d.getParmValue().getInt("INVSEQ_NO", i));
            // 5.��������
            inparm.addData("QTY", table_d.getItemDouble(i, "QTY"));
            // 6.�ۼƳ�����
            inparm.addData("ACTUAL_QTY", table_d.getItemDouble(i, "ACTUAL_QTY"));
            // 10.����ע��
            inparm.addData("FINA_TYPE", table_d.getItemString(i, "FINA_TYPE"));
            // 11,12,13 OPT
            inparm.addData("OPT_USER", Operator.getID());
            inparm.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
            inparm.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("REQ_D", inparm.getData());
        return parm;
    }


    /**
     * ����������¼�
     */
    public void onChangeRequestType() {
        String request_type = this.getValueString("REQUEST_TYPE");
        this.setValue("TO_ORG_CODE", "");
        this.setValue("FROM_ORG_CODE", "");
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "FROM_ORG_CODE");
        TextFormatINVReason inv_ren = (TextFormatINVReason)this.getTextFormat(
            "REN_CODE");
        if ("REQ".equals(request_type)) {
            inv_org.setEnabled(true);
            inv_ren.setReqFlg("Y");
            inv_ren.setGifFlg("N");
            inv_ren.setRetFlg("N");
            inv_ren.setWasFlg("N");
        }
        else if ("GIF".equals(request_type)) {
            inv_org.setEnabled(true);
            inv_ren.setReqFlg("N");
            inv_ren.setGifFlg("Y");
            inv_ren.setRetFlg("N");
            inv_ren.setWasFlg("N");
        }
        else if ("RET".equals(request_type)) {
            inv_org.setEnabled(true);
            inv_ren.setReqFlg("N");
            inv_ren.setGifFlg("N");
            inv_ren.setRetFlg("Y");
            inv_ren.setWasFlg("N");

        }
        else if ("WAS".equals(request_type)) {
            inv_org.setEnabled(false);
            inv_ren.setReqFlg("N");
            inv_ren.setGifFlg("N");
            inv_ren.setRetFlg("N");
            inv_ren.setWasFlg("Y");
        }
    }

    /**
     * ���벿�ű���¼�
     */
    public void onChangeToOrgCode() {
        this.setValue("FROM_ORG_CODE", "");
        String request_type = this.getValueString("REQUEST_TYPE");
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "FROM_ORG_CODE");
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVOrg(this.
            getValueString("TO_ORG_CODE"))));
        String org_type = parm.getValue("ORG_TYPE", 0);    
        if ("REQ".equals(request_type)) {
            if ("A".equals(org_type)) {
                org_type = "-";
            }
            else if ("B".equals(org_type)) {
                org_type = "A";
            }
            else if ("C".equals(org_type)) {
                org_type = "B";
            }
            inv_org.setOrgType(org_type);
        }
        else if ("GIF".equals(request_type)) {
            if ("A".equals(org_type)) {
                org_type = "A";
            }
            else if ("B".equals(org_type)) {
                org_type = "B";
            }
            else if ("C".equals(org_type)) {
                org_type = "C";
            }
            inv_org.setOrgType(org_type);
        }
        else if ("RET".equals(request_type)) {
            if ("A".equals(org_type)) {
                org_type = "-";
            }
            else if ("B".equals(org_type)) {
                org_type = "A";
            }
            else if ("C".equals(org_type)) {
                org_type = "B";
            }
            inv_org.setOrgType(org_type);
        }
        else if ("WAS".equals(request_type)) {
            this.setValue("FROM_ORG_CODE", "");
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        /**
         * Ȩ�޿���
         * Ȩ��1:һ�����ֻ��ʾ������������
         * Ȩ��9:���Ȩ����ʾȫԺҩ�ⲿ��
         */
        // ��ʾȫԺҩ�ⲿ��
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "TO_ORG_CODE");
        TextFormatINVOrg inv_org_q = (TextFormatINVOrg)this.getTextFormat(
            "TO_ORG_CODE_Q");
        if (!this.getPopedem("deptAll")) {
            inv_org.setOperatorId(Operator.getID());
            inv_org_q.setOperatorId(Operator.getID());
            dept_flg = false;
        }
        else {
            inv_org.setOperatorId("");
            inv_org_q.setOperatorId("");
            dept_flg = true;
        }

        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        //( (TMenuItem) getComponent("delete")).setEnabled(false);
        setValue("REQUEST_DATE", date);

        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");

        // ��ʼ��TABLE_M��Parm
        TParm parmD = new TParm();
        String[] purD = {
            "INV_CODE", "INV_CHN_DESC", "DESCRIPTION", "PY1",
            "STOCK_QTY", "STOCK_UNIT", "INVSEQ_NO", "DISPENSE_UNIT",
            "COST_PRICE", "MAN_CODE",  "SEQMAN_FLG", "VALIDATE_FLG", "INVKIND_CODE",
            "SEQ_NO"};
        for (int i = 0; i < purD.length; i++) {
            parmD.setData(purD[i], new Vector());
        }
        table_d.setParmValue(parmD);

        // ע�ἤ��INDSupOrder�������¼�
        table_d.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                 this, "onCreateEditComoponentUD");
        // TABLE_Dֵ�ı��¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        createNewRow();
    }

    /**
     * ����ϸ��������
     * @return int
     */
    private int createNewRow() {
        int row = table_d.addRow();
        return row;
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
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
                if (qty <= 0) {
                    this.messageBox("������������С��0");
                    return true;
                }
                else {
                    // ������
                    table_d.setItem(row, "SUM_AMT", qty *
                                    table_d.getItemDouble(row, "COST_PRICE"));
                    return false;
                }
        }
        return false;
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
        if ("".equals(this.getValueString("REQUEST_TYPE"))) {
            this.messageBox("���������Ϊ��");
            return;
        }
        if ("".equals(this.getValueString("TO_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return;
        }
        if (!"WAS".equals(this.getValueString("REQUEST_TYPE"))
            && "".equals(this.getValueString("FROM_ORG_CODE"))) {
            this.messageBox("���ܲ��Ų���Ϊ��");
            return;
        }

        if (getTextFormat("TO_ORG_CODE").isEnabled()) {
            getTextFormat("TO_ORG_CODE").setEnabled(false);
        }
        if (getTextFormat("FROM_ORG_CODE").isEnabled()) {
            getTextFormat("FROM_ORG_CODE").setEnabled(false);
        }
        if (getComboBox("REQUEST_TYPE").isEnabled()) {
            getComboBox("REQUEST_TYPE").setEnabled(false);
        }

        TParm parm = new TParm();
        String org_code = "";
        if ("REQ".equals(this.getValueString("REQUEST_TYPE")) ||
            "GIF".equals(this.getValueString("REQUEST_TYPE"))) {
            org_code = this.getValueString("FROM_ORG_CODE");
        }
        else if ("RET".equals(this.getValueString("REQUEST_TYPE")) ||
                 "WAS".equals(this.getValueString("REQUEST_TYPE"))) {
            org_code = this.getValueString("TO_ORG_CODE");
        }
        parm.setData("ORG_CODE", org_code);
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
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
        String inv_desc = parm.getValue("INV_CHN_DESC");
        String inv_code = parm.getValue("INV_CODE");
        int batch_seq = 100000000;
        int invseq_no = parm.getInt("INVSEQ_NO");
        String seqmain_flg = parm.getValue("SEQMAN_FLG");
        if (!StringUtil.isNullString(inv_desc)) {
            // ������Ź���������Ƿ��ظ�
            if ("N".equals(seqmain_flg)) {
                for (int i = 0; i < table_d.getRowCount(); i++) {
                    if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                        continue;
                    }
                    else if (table_d.getSelectedRow() == i) {
                        continue;
                    }
                    else if ("Y".equals(table_d.getParmValue().getValue(
                        "SEQMAN_FLG", i))) {
                        continue;
                    }
                    else {
                        if (inv_code.equals(table_d.getParmValue().getValue(
                            "INV_CODE", i)) &&
                            batch_seq ==
                            table_d.getParmValue().getInt(" ", i)) {
                            this.messageBox("���������ظ�");
                            return;
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < table_d.getRowCount(); i++) {
                    if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                        continue;
                    }
                    else if (table_d.getSelectedRow() == i) {
                        continue;
                    }
                    else if ("N".equals(table_d.getParmValue().getValue(
                        "SEQMAN_FLG", i))) {
                        continue;
                    }
                    else {
                        if (inv_code.equals(table_d.getParmValue().getValue(
                            "INV_CODE", i)) &&
                            invseq_no ==
                            table_d.getParmValue().getInt("INVSEQ_NO", i)) {
                            this.messageBox("���������ظ�");
                            return;
                        }
                    }
                }
            }
              parm.setData("INVSEQ_NO", table_d.getSelectedRow()+1);
            setTableDValue(parm, table_d.getSelectedRow());
            if (table_d.getRowCount() == table_d.getSelectedRow() + 1) {
                createNewRow();
            }
        }
    }

    /**
     * ��ֵ��TABLE_D
     * @param parm TParm
     * @param row int
     */
    public void setTableDValue(TParm parm, int row) {
        table_d.setRowParmValue(row, parm);
        table_d.getParmValue().setRowData(row, parm);
        if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {
            table_d.setItem(row, "QTY", 1);
            table_d.setItem(row, "SUM_AMT", parm.getDouble("COST_PRICE"));
        }
        else {
            table_d.setItem(row, "QTY", 0);
            table_d.setItem(row, "SUM_AMT", 0);
        }
        table_d.setItem(row, "ACTUAL_QTY", 0);
        table_d.setItem(row, "FINA_TYPE", "0");
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
     * �õ�ComboBox����
     * @param tagName String
     * @return TComboBox
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
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
