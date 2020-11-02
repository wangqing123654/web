package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.phl.PhlRegionTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextField;

/**
 * <p>
 * Title: ����������
 * </p>
 *
 * <p>
 * Description: ����������
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
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class PHLRegionControl
    extends TControl {

    private String action = "insert";

    private TTable table;

    public PHLRegionControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        parm.setData("REGION_DESC", getValueString("REGION_DESC"));
        parm.setData("PY1", getValueString("PY1"));
        parm.setData("PY2", getValueString("PY2"));
        parm.setData("START_IP", getValueString("START_IP"));
        parm.setData("END_IP", getValueString("END_IP"));
        parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
        parm.setData("OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        if ("insert".equals(action)) {
            result = PhlRegionTool.getInstance().onQuery(parm);
            if (result.getCount() > 0) {
                this.messageBox("�����Ѵ���");
                return;
            }
            result = PhlRegionTool.getInstance().onInsert(parm);
        }
        else {
            result = PhlRegionTool.getInstance().onUpdate(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        getTable("TABLE").setSelectionMode(0);
        this.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        String region_code = getValueString("REGION_CODE");
        if (!"".equals(region_code)) {
            parm.setData("REGION_CODE", region_code);
        }
        TParm result = PhlRegionTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
            TParm result = PhlRegionTool.getInstance().onDelete(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ��");
                return;
            }
            table.removeRow(row);
            table.setSelectionMode(0);
            this.messageBox("ɾ���ɹ�");
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
        action = "insert";
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "REGION_CODE;REGION_DESC;PY1;PY2;START_IP;END_IP;DESCRIPTION";
        this.clearValue(clear);
        getTable("TABLE").setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "insert";
    }

    /**
     * REGION_DESC�س��¼�
     */
    public void onRegionDescAction() {
        String py = TMessage.getPy(this.getValueString("REGION_DESC"));
        setValue("PY1", py);
        getTextField("PY1").grabFocus();
    }


    /**
     * ���(CLNDIAG_TABLE)�����¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            this.setValue("REGION_CODE", table.getItemString(row, "REGION_CODE"));
            this.setValue("REGION_DESC", table.getItemString(row, "REGION_DESC"));
            this.setValue("PY1", table.getItemString(row, "PY1"));
            this.setValue("PY2", table.getItemString(row, "PY2"));
            this.setValue("START_IP", table.getItemString(row, "START_IP"));
            this.setValue("END_IP", table.getItemString(row, "END_IP"));
            this.setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
            action = "update";
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        table = this.getTable("TABLE");
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
    private boolean CheckData() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("���������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("START_IP"))) {
            this.messageBox("��ʼIP����Ϊ��");
            return false;
        }
        if ("".equals(getValueString("END_IP"))) {
            this.messageBox("����IP����Ϊ��");
            return false;
        }
        if (!StringTool.isIP(getValueString("START_IP"))) {
            this.messageBox("��ʼIP����ȷ");
            return false;
        }
        if (!StringTool.isIP(getValueString("END_IP"))) {
            this.messageBox("����IP����ȷ");
            return false;
        }
        if (!StringTool.isIPSeq(getValueString("START_IP"),
                                getValueString("END_IP"))) {
            this.messageBox("��ʼIP���ܴ��ڽ���IP");
            return false;
        }
        TParm parm = new TParm();
        TParm result = PhlRegionTool.getInstance().onQuery(parm);
        String start_ip = "";
        String end_ip = "";
        if (result.getCount() > 0) {
            for (int i = 0; i < result.getCount(); i++) {
                if (this.getValueString("REGION_CODE").equals(result.getValue(
                    "REGION_CODE,i"))) {
                    continue;
                }
                start_ip = result.getValue("START_IP", i);
                end_ip = result.getValue("END_IP", i);
                if (!StringTool.isInteractIP(this.getValueString("START_IP"),
                                             this.getValueString("END_IP"),
                                             start_ip,
                                             end_ip)) {
                    this.messageBox("IP�����ص�");
                    return false;
                }
            }
        }
        return true;
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
