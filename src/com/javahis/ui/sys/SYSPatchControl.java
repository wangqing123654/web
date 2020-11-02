package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSPatchParmTool;
import jdo.sys.SYSPatchTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_PatchServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;

/**
 * <p>
 * Title:���γ���
 * </p>
 *
 * <p>
 * Description:���γ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.7.20
 * @version 1.0
 */
public class SYSPatchControl
    extends TControl {

    private String action;
    // TABLE_M
    private TTable table_m;
    // TABLE_PARM
    private TTable table_parm;
    // TABLE_INFO
    private TTable table_info;

    public SYSPatchControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (checkPatchData()) {
            TParm parm = new TParm();
            parm.setData("PATCH_DESC", this.getValue("PATCH_DESC"));
            parm.setData("PATCH_SRC", this.getValue("PATCH_SRC"));
            parm.setData("PATCH_TYPE", this.getValue("PATCH_TYPE"));
            // ��������ʱ��
            String patch_date = this.getPatchDateValue(this
                .getValueString("PATCH_TYPE"));
            parm.setData("PATCH_DATE", patch_date);
            parm.setData("PATCH_REOMIT_COUNT", this
                         .getValueInt("PATCH_REOMIT_COUNT"));
            // ���ͼ��ʱ��
            String patch_remoit_interval = this.getPatchReomitInterval();
            parm.setData("PATCH_REOMIT_INTERVAL", patch_remoit_interval);
            parm.setData("PATCH_REOMIT_POINT", this
                         .getValue("PATCH_REOMIT_POINT"));
            parm.setData("STATUS", this.getValue("STATUS"));
            TNull tnull = new TNull(Timestamp.class);
            if ("".equals(this.getValueString("END_DATE"))) {
                parm.setData("END_DATE", tnull);
            }
            else {
                parm.setData("END_DATE", this.getValue("END_DATE"));
            }
            parm.setData("OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            // ��������
            TParm resultParm = new TParm();
            if ("update".equals(action)) {
                // ���ݸ���
                parm.setData("PATCH_CODE", this.getValue("PATCH_CODE"));
                //System.out.println("UPDATE-PARM:" + parm);
                resultParm = SYSPatchTool.getInstance().onUpdate(parm);
            }
            else {
                // ȡ��ԭ��
                String patch_code = SystemTool.getInstance().getNo("ALL",
                    "PUB", "PATCH_CODE", "PATCH_CODE");
                // ��������
                boolean flg = false;
                for (int i = 0; i < table_m.getRowCount(); i++) {
                    if (patch_code.equals(table_m.getItemData(i, "PATCH_CODE"))) {
                        flg = true;
                    }
                }
                if (flg) {
                    this.messageBox("���γ����Ѵ���");
                    return;
                }
                parm.setData("PATCH_CODE", patch_code);
                //System.out.println("INSERT-PARM:" + parm);
                resultParm = SYSPatchTool.getInstance().onInsert(parm);
            }
            //System.out.println("RESULT-PARM:" + resultParm);
            // �����ж�
            if (resultParm.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            else {
                this.messageBox("P0001");
                if (TIOM_PatchServer.isStart()) {
                    if ("update".equals(action)) {
                        TIOM_PatchServer.popJob(this
                                                .getValueString("PATCH_CODE"));
                    }
                    TIOM_PatchServer.putJob(this.getValueString("PATCH_CODE"));
                }
                onClear();
                return;
            }
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (table_parm.getRowCount() > 0) {
            this.messageBox("�������β���������ɾ��");
            return;
        }
        else {
            if (this.messageBox("��ʾ", "�Ƿ�ɾ������", 2) == 0) {
                TParm parm = new TParm();
                parm.setData("PATCH_CODE", this.getValueString("PATCH_CODE"));
                TParm result = new TParm();
                result = SYSPatchTool.getInstance().onDelete(parm);
                if (result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                else {
                    this.messageBox("ɾ���ɹ�");
                    if (TIOM_PatchServer.isStart()) {
                        TIOM_PatchServer.popJob(this
                                                .getValueString("PATCH_CODE"));
                    }
                    onClear();
                    return;
                }
            }
        }
    }

    /**
     * ��������
     */
    public void onStart() {
        if (TIOM_PatchServer.isStart()) {
            TIOM_PatchServer.stop();
            TIOM_PatchServer.start();
        }
        else {
            TIOM_PatchServer.start();
        }
        ( (TMenuItem) getComponent("start")).setEnabled(false);
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
    }

    /**
     * ֹͣ����
     */
    public void onStop() {
        TIOM_PatchServer.stop();
        ( (TMenuItem) getComponent("start")).setEnabled(true);
        ( (TMenuItem) getComponent("stop")).setEnabled(false);
    }

    /**
     * ��ѡ��ť�ı��¼�
     */
    public void onChangeRadioButton() {
        TRadioButton rb = (TRadioButton)this.getComponent("PATCH_A");
        boolean flg = rb.isSelected();
        if (flg) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            table_m.getDataStore().setSQL(SYSSQL.getSYSPatch(
                "PATCH_DESC not like '���ۼƻ�%'"));
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            table_m.getDataStore().setSQL(SYSSQL.getSYSPatch(
                "PATCH_DESC like '���ۼƻ�%'"));
        }
        table_m.getDataStore().retrieve();
        table_m.setDSValue();

    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearString =
            "PATCH_CODE;PATCH_DESC;PATCH_SRC;PATCH_REOMIT_COUNT;HH;"
            + "MM;SS;PATCH_REOMIT_POINT;STATUS;END_DATE";
        this.clearValue(clearString);
        //this.getTextField("PATCH_CODE").setEnabled(true);
        // ��ʼ����������
        this.getComboBox("PATCH_TYPE").setSelectedIndex(0);
        // ��������ʱ��
        setPatchTypeStatus("1");
        this.setValue("PATCH_DATE_1", "");
        // ��ʼ��TABLE_M
        table_m.getDataStore().setSQL(SYSSQL.getSYSPatch(
            "PATCH_DESC not like '���ۼƻ�%'"));
        table_m.getDataStore().retrieve();
        table_m.setDSValue();
        table_m.setSelectionMode(0);
        // ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "insert";
    }

    /**
     * ����ִ�����γ���
     */
    public void onServerAction() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ִ������");
            return;
        }
        TParm patchParm = new TParm();
        patchParm.setData("PATCH_CODE", this.getValueString("PATCH_CODE"));
        patchParm.setData("PATCH_SRC", this.getValueString("PATCH_SRC"));
        TParm result = TIOM_AppServer.executeAction(
            "action.sys.SYSPatchAction", "onServerAction", patchParm);
        if (result == null) {
            this.messageBox("ִ��ʧ��");
            return;
        }
        if(result.getErrCode()<0){
            this.messageBox(result.getValue("MESSAGE"));
            return;
        }
        this.messageBox("ִ�гɹ�");
    }

    /**
     * ���÷�����
     */
    public void onServer() {
        Object result = openDialog("%ROOT%\\config\\sys\\SYSPatchServer.x");
    }

    /**
     * �������β���
     */
    public void onSaveParm() {
        if (checkPatchParmData()) {
            TParm parm = new TParm();
            parm.setData("PATCH_CODE", this.getValue("PATCH_CODE"));
            parm.setData("PATCH_PARM_VALUE", this.getValue("PATCH_PARM_VALUE"));
            parm.setData("OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            // ��������
            TParm resultParm = new TParm();
            if (!this.getTextField("PATCH_PARM_NAME").isEnabled()) {
                // ���ݸ���
                parm.setData("PATCH_PARM_NAME", this
                             .getValue("PATCH_PARM_NAME"));
                //System.out.println("UPDATE:" + parm);
                resultParm = SYSPatchParmTool.getInstance().onUpdate(parm);
            }
            else {
                // ��������
                boolean flg = false;
                for (int i = 0; i < table_parm.getRowCount(); i++) {
                    if (this.getValueString("PATCH_PARM_NAME").equals(
                        table_parm.getItemData(i, "PATCH_PARM_NAME"))) {
                        flg = true;
                    }
                }
                if (flg) {
                    this.messageBox("�����Ѵ���");
                    return;
                }
                parm.setData("PATCH_PARM_NAME", this
                             .getValue("PATCH_PARM_NAME"));
                //System.out.println("INSERT:" + parm);
                resultParm = SYSPatchParmTool.getInstance().onInsert(parm);
            }
            //System.out.println("RESULT:" + resultParm);
            // �����ж�
            if (resultParm.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            else {
                this.messageBox("P0001");
                onClearParm();
                return;
            }
        }
    }

    /**
     * ������β���
     */
    public void onClearParm() {
        String clearString = "PATCH_PARM_NAME;PATCH_PARM_VALUE";
        this.clearValue(clearString);
        this.getTextField("PATCH_PARM_NAME").setEnabled(true);
        // ���β�����Ϣ
        table_parm.getDataStore().setSQL(
            SYSSQL.getSYSPatchParm(this.getValueString("PATCH_CODE")));
        table_parm.getDataStore().retrieve();
        table_parm.setDSValue();
        // ����ɾ����ť��״̬
        ( (TButton) getComponent("DELETE_PARM")).setEnabled(false);
    }

    /**
     * ɾ�����β���
     */
    public void onDeleteParm() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ������", 2) == 0) {
            TParm parm = new TParm();
            parm.setData("PATCH_CODE", this.getValueString("PATCH_CODE"));
            parm.setData("PATCH_PARM_NAME", this
                         .getValueString("PATCH_PARM_NAME"));
            TParm result = new TParm();
            result = SYSPatchParmTool.getInstance().onDelete(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ��");
                return;
            }
            else {
                this.messageBox("ɾ���ɹ�");
                onClearParm();
                return;
            }
        }
    }

    /**
     * ѡ����������
     */
    public void onPatchTypeAction() {
        setPatchTypeStatus(getValueString("PATCH_TYPE"));
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        int row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            TParm parm = table_m.getDataStore().getRowParm(row_m);
            String linkStr =
                "PATCH_CODE;PATCH_DESC;PATCH_SRC;PATCH_TYPE;PATCH_REOMIT_COUNT;"
                + "PATCH_REOMIT_POINT;STATUS;END_DATE";
            this.setValueForParm(linkStr, parm);
            //this.getTextField("PATCH_CODE").setEnabled(false);
            String patch_type = parm.getValue("PATCH_TYPE");
            // �������������趨��������ʱ��״̬
            setPatchTypeStatus(patch_type);
            String value = parm.getValue("PATCH_DATE");
            // �������������趨��������ʱ��ֵ
            setPatchDateValue(patch_type, value);
            value = parm.getValue("PATCH_REOMIT_INTERVAL");
            // �趨���ͼ��ʱ��
            setPatchReomitInterval(value);

            // ���β�����Ϣ
            table_parm.getDataStore().setSQL(
                SYSSQL.getSYSPatchParm(this.getValueString("PATCH_CODE")));
            table_parm.getDataStore().retrieve();
            table_parm.setDSValue();

            // ������ʷ��Ϣ
            table_info.getDataStore().setSQL(
                SYSSQL.getSYSPatchLog(this.getValueString("PATCH_CODE")));
            table_info.getDataStore().retrieve();
            table_info.setDSValue();
            // ��ʽ��ʱ���ʽ
            for (int i = 0; i < table_info.getRowCount(); i++) {
                if (!"".equals(table_info.getItemString(i, "PATCH_START_DATE"))) {
                    value = table_info.getItemString(i, "PATCH_START_DATE");
                    value = value.substring(0, 4) + "/" + value.substring(4, 6)
                        + "/" + value.substring(6, 8) + " "
                        + value.substring(8, 10) + ":"
                        + value.substring(10, 12) + ":"
                        + value.substring(12, 14);
                    table_info.setItem(i, "PATCH_START_DATE", value);
                }
                if (!"".equals(table_info.getItemString(i, "PATCH_END_DATE"))) {
                    value = table_info.getItemString(i, "PATCH_END_DATE");
                    value = value.substring(0, 4) + "/" + value.substring(4, 6)
                        + "/" + value.substring(6, 8) + " "
                        + value.substring(8, 10) + ":"
                        + value.substring(10, 12) + ":"
                        + value.substring(12, 14);
                    table_info.setItem(i, "PATCH_END_DATE", value);
                }
            }

            // ɾ����ť״̬
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "update";
        }
    }

    /**
     * �������(TABLE_PARM)�����¼�
     */
    public void onTableParmClicked() {
        int row_parm = table_parm.getSelectedRow();
        if (row_parm != -1) {
            TParm parm = table_parm.getDataStore().getRowParm(row_parm);
            String linkStr = "PATCH_PARM_NAME;PATCH_PARM_VALUE";
            this.setValueForParm(linkStr, parm);
            this.getTextField("PATCH_PARM_NAME").setEnabled(false);
            // ����ɾ����ť��״̬
            ( (TButton) getComponent("DELETE_PARM")).setEnabled(true);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ����������
        this.getComboBox("PATCH_TYPE").setSelectedIndex(0);
        // ��������ʱ��
        setPatchTypeStatus("1");
        // ��������TABLE
        table_m = this.getTable("TABLE_M");
        // ���β���TABLE
        table_parm = this.getTable("TABLE_PARM");
        // ������ʷ��ϢTABLE
        table_info = this.getTable("TABLE_INFO");
        // ��ʼ����������TABLE
        table_m.getDataStore().setSQL(SYSSQL.getSYSPatch(
            "PATCH_DESC not like '���ۼƻ�%'"));
        table_m.getDataStore().retrieve();
        table_m.setDSValue();

        // �������γ�������״̬���ð�ť״̬
        if (TIOM_PatchServer.isStart()) {
            ( (TMenuItem) getComponent("start")).setEnabled(false);
            ( (TMenuItem) getComponent("stop")).setEnabled(true);
        }
        else {
            ( (TMenuItem) getComponent("start")).setEnabled(true);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
        }

        // ɾ����ť״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "insert";
    }

    /**
     * �������������趨��������ʱ��״̬
     *
     * @param type
     *            ��������
     */
    private void setPatchTypeStatus(String type) {
        if ("1".equals(type)) {
            this.setPatchDateStatus("YNNNN");
        }
        else if ("2".equals(type)) {
            this.setPatchDateStatus("NYNNN");
        }
        else if ("3".equals(type)) {
            this.setPatchDateStatus("NNYNN");
        }
        else if ("4".equals(type)) {
            this.setPatchDateStatus("NNNYN");
        }
        else {
            this.setPatchDateStatus("NNNNY");
        }
    }

    /**
     * �趨��������ʱ��״̬
     *
     * @param status
     */
    private void setPatchDateStatus(String status) {
        char[] flg = status.toCharArray();
        // һ����ִ��
        getTextFormat("PATCH_DATE_1").setVisible(flg[0] == 'Y' ? true : false);
        // ÿ��ִ��
        getTextFormat("PATCH_DATE_2").setVisible(flg[1] == 'Y' ? true : false);
        // ÿ��ִ��
        getComboBox("PATCH_DATE_3_1").setVisible(flg[2] == 'Y' ? true : false);
        getTextFormat("PATCH_DATE_3_2")
            .setVisible(flg[2] == 'Y' ? true : false);
        // ÿ��ִ��
        getComboBox("PATCH_DATE_4_1").setVisible(flg[3] == 'Y' ? true : false);
        getTextFormat("PATCH_DATE_4_2")
            .setVisible(flg[3] == 'Y' ? true : false);
        // ÿ��ִ��
        getComboBox("PATCH_DATE_5_1").setVisible(flg[4] == 'Y' ? true : false);
        getComboBox("PATCH_DATE_5_2").setVisible(flg[4] == 'Y' ? true : false);
        getTextFormat("PATCH_DATE_5_3")
            .setVisible(flg[4] == 'Y' ? true : false);
    }

    /**
     * �������������趨��������ʱ��ֵ
     *
     * @param type
     *            ��������
     * @param value
     *            ��������ʱ��
     */
    private void setPatchDateValue(String type, String value) {
        if ("1".equals(type)) {
            // һ����ִ��
            value = value.substring(0, 4) + "/" + value.substring(4, 6) + "/"
                + value.substring(6, 8) + " " + value.substring(8, 10)
                + ":" + value.substring(10, 12) + ":"
                + value.substring(12, 14);
            this.setValue("PATCH_DATE_1", value);
        }
        else if ("2".equals(type)) {
            // ÿ��ִ��
            value = value.substring(0, 2) + ":" + value.substring(2, 4) + ":"
                + value.substring(4, 6);
            this.setValue("PATCH_DATE_2", value);
        }
        else if ("3".equals(type)) {
            // ÿ��ִ��
            this.setValue("PATCH_DATE_3_1", value.substring(0, 1));
            value = value.substring(1, 3) + ":" + value.substring(3, 5) + ":"
                + value.substring(5, 7);
            this.setValue("PATCH_DATE_3_2", value.substring(0, 2));
        }
        else if ("4".equals(type)) {
            // ÿ��ִ��
            this.setValue("PATCH_DATE_4_1", value.substring(0, 2));
            value = value.substring(2, 4) + ":" + value.substring(4, 6) + ":"
                + value.substring(6, 8);
            this.setValue("PATCH_DATE_4_2", value.substring(0, 2));
        }
        else {
            // ÿ��ִ��
            this.setValue("PATCH_DATE_5_1", value.substring(0, 2));
            this.setValue("PATCH_DATE_5_2", value.substring(2, 4));
            value = value.substring(4, 6) + ":" + value.substring(6, 8) + ":"
                + value.substring(8, 10);
            this.setValue("PATCH_DATE_5_3", value);
        }
        // this.messageBox(value);
    }

    /**
     * �������ͼ��ʱ��ֵ
     *
     * @param value
     */
    private void setPatchReomitInterval(String value) {
        if (!"".equals(value)) {
            if (value.length() == 6) {
                this.setValue("HH", value.substring(0, 2));
                this.setValue("MM", value.substring(2, 4));
                this.setValue("SS", value.substring(4, 6));
            }
            else if (value.length() == 4) {
                this.setValue("MM", value.substring(0, 2));
                this.setValue("SS", value.substring(2, 4));
            }
            else if (value.length() == 2) {
                this.setValue("SS", value);
            }
            else {
                this.setValue("HH", 0);
                this.setValue("MM", 0);
                this.setValue("SS", 0);
            }
        }
        else {
            this.setValue("HH", 0);
            this.setValue("MM", 0);
            this.setValue("SS", 0);
        }
    }

    /**
     * ������������ȡ����������ʱ��ֵ
     *
     * @param type
     * @return
     */
    private String getPatchDateValue(String type) {
        String returnStr = "";
        if ("1".equals(type)) {
            returnStr = this.getValueString("PATCH_DATE_1");
            returnStr = returnStr.substring(0, 4) + returnStr.substring(5, 7)
                + returnStr.substring(8, 10) + returnStr.substring(11, 13)
                + returnStr.substring(14, 16) + returnStr.substring(17, 19);
        }
        else if ("2".equals(type)) {
            returnStr = this.getValueString("PATCH_DATE_2");
            returnStr = returnStr.substring(11, 13)
                + returnStr.substring(14, 16) + returnStr.substring(17, 19);
        }
        else if ("3".equals(type)) {
            returnStr = this.getValueString("PATCH_DATE_3_2");
            returnStr = this.getValueString("PATCH_DATE_3_1")
                + returnStr.substring(11, 13) + returnStr.substring(14, 16)
                + returnStr.substring(17, 19);
        }
        else if ("4".equals(type)) {
            returnStr = this.getValueString("PATCH_DATE_4_2");
            returnStr = this.getValueString("PATCH_DATE_4_1")
                + returnStr.substring(11, 13) + returnStr.substring(14, 16)
                + returnStr.substring(17, 19);
        }
        else {
            returnStr = this.getValueString("PATCH_DATE_5_3");
            returnStr = this.getValueString("PATCH_DATE_5_1")
                + this.getValueString("PATCH_DATE_5_2")
                + returnStr.substring(11, 13) + returnStr.substring(14, 16)
                + returnStr.substring(17, 19);
        }
        // this.messageBox(returnStr);
        return returnStr;
    }

    /**
     * ȡ�����ͼ��ʱ��ֵ
     *
     * @return
     */
    private String getPatchReomitInterval() {
        String returnStr = "";
        if (this.getValueDouble("HH") != 0) {
            returnStr += this.getValueInt("HH") < 10 ? "0"
                + this.getValueInt("HH") : this.getValueString("HH");
            returnStr += this.getValueInt("MM") < 10 ? "0"
                + this.getValueInt("MM") : this.getValueString("MM");
            returnStr += this.getValueInt("SS") < 10 ? "0"
                + this.getValueInt("SS") : this.getValueString("SS");
        }
        else if (this.getValueDouble("MM") != 0) {
            returnStr += this.getValueInt("MM") < 10 ? "0"
                + this.getValueInt("MM") : this.getValueString("MM");
            returnStr += this.getValueInt("SS") < 10 ? "0"
                + this.getValueString("SS") : this.getValueString("SS");
        }
        else if (this.getValueDouble("SS") != 0) {
            returnStr += this.getValueInt("SS") < 10 ? "0"
                + this.getValueInt("SS") : this.getValueString("SS");
        }
        // this.messageBox(returnStr);
        return returnStr;
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

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
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
     * ���ݼ��� SYS_PATCH
     *
     * @return
     */
    private boolean checkPatchData() {
        // if ("".equals(this.getValueString("PATCH_CODE"))) {
        // this.messageBox("���α�Ų���Ϊ��");
        // return false;
        // }
        if ("".equals(this.getValueString("PATCH_DESC"))) {
            this.messageBox("�������Ʋ���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("PATCH_SRC"))) {
            this.messageBox("���γ�����Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("PATCH_TYPE"))) {
            this.messageBox("�������Ͳ���Ϊ��");
            return false;
        }
        String datevalue = getPatchDateValue(this.getValueString("PATCH_TYPE"));
        if ("".equals(datevalue)) {
            this.messageBox("��������ʱ�䲻��Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ��� SYS_PATCH_PARM
     *
     * @return
     */
    private boolean checkPatchParmData() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("��ѡ�����γ���");
            return false;
        }
        if ("".equals(this.getValueString("PATCH_PARM_NAME"))) {
            this.messageBox("�������Ʋ���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("PATCH_PARM_VALUE"))) {
            this.messageBox("����ֵ����Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ����
     *
     * @param args
     */
    public static void main(String[] args) {
        com.dongyang.util.TDebug.initClient();
        TIOM_PatchServer.stop();
        TIOM_PatchServer.start();
        TIOM_PatchServer.popJob("01");
        TIOM_PatchServer.putJob("01");
        String[] a = TIOM_PatchServer.getList();
        //System.out.println(StringTool.getString(a));
    }
}
