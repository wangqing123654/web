package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:��������
 * </p>
 *
 * <p>
 * Description:��������
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
 * @author zhangy 2009.6.08
 * @version 1.0
 */
public class SYSMannfacturerControl
    extends TControl {

    private String action = "save";
    // ������
    private TTable table;

    public SYSMannfacturerControl() {
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
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        if ("save".equals(action)) {
            TTextField combo = getTextField("MAN_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                if (!CheckData())
                    return;
                row = table.getSelectedRow();
            }
            table.setItem(row, "MAN_CODE", getValueString("MAN_CODE"));
            String desc = getValueString("MAN_CHN_DESC");
            table.setItem(row, "MAN_CHN_DESC", desc);
            desc = getValueString("MAN_ENG_DESC");
            table.setItem(row, "MAN_ENG_DESC", desc);
            desc = getValueString("MAN_ABS_DESC");
            table.setItem(row, "MAN_ABS_DESC", desc);
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            desc = getValueString("NATIONAL_CODE");
            table.setItem(row, "NATIONAL_CODE", desc);
            table.setItem(row, "POST_CODE", getValueString("POST_CODE"));
            table.setItem(row, "ADDRESS", getValueString("ADDRESS"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "TEL", getValueString("TEL"));
            table.setItem(row, "FAX", getValueString("FAX"));
            table.setItem(row, "WEBSITE", getValueString("WEBSITE"));
            table.setItem(row, "E_MAIL", getValueString("E_MAIL"));
            table.setItem(row, "PHA_FLG", getValueString("PHA_FLG"));
            table.setItem(row, "MAT_FLG", getValueString("MAT_FLG"));
            table.setItem(row, "DEV_FLG", getValueString("DEV_FLG"));
            table.setItem(row, "OTHER_FLG", getValueString("OTHER_FLG"));
            table.setItem(row, "OPT_USER", Operator.getID());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                table.removeRow(row);
                table.setDSValue();
                onClear();
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
        onClear();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ��ʼ��Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSManufacturer());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();

        String code = getValueString("MAN_CODE");
        String desc = getValueString("MAN_CHN_DESC");
        String filterString = "";
        if (code.length() > 0 && desc.length() > 0)
            filterString += "MAN_CODE like '" + code
                + "%' AND MAN_CHN_DESC like '" + desc + "%'";
        else if (code.length() > 0)
            filterString += "MAN_CODE like '" + code + "%'";
        else if (desc.length() > 0)
            filterString += "MAN_CHN_DESC like '" + desc + "%'";
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������
        String clearString =
            "MAN_CODE;MAN_CHN_DESC;MAN_ENG_DESC;MAN_ABS_DESC;PY1;"
            + "PY2;SEQ;DESCRIPTION;NATIONAL_CODE;POST_CODE;"
            + "ADDRESS;TEL;FAX;WEBSITE;E_MAIL;"
            + "PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG;POST_P;POST_C";
        clearValue(clearString);
        // ���
        TDataStore dataStroe = table.getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        table.setSelectionMode(0);
        getTextField("MAN_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
    }

    /**
     * TABLE�����¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "MAN_CODE;MAN_CHN_DESC;MAN_ENG_DESC;MAN_ABS_DESC;PY1;"
                + "PY2;SEQ;DESCRIPTION;NATIONAL_CODE;POST_CODE;"
                + "ADDRESS;TEL;FAX;WEBSITE;E_MAIL;"
                + "PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG";
            this.setValueForParm(likeNames, parm);
            getTextField("MAN_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
            onPostClick();
        }
    }

    /**
     * ManDesc�س��¼�
     */
    public void onManDescAction() {
        String py = TMessage.getPy(this.getValueString("MAN_CHN_DESC"));
        setValue("PY1", py);
        getTextField("MAN_ENG_DESC").grabFocus();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSManufacturer());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();

        // ����+1(SEQ)
        int seq = getMaxSeq(dataStore, "SEQ",
                            dataStore.isFilter() ? dataStore.FILTER :
                            dataStore.PRIMARY);
        setValue("SEQ", seq);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * ͨ�����д�����������
     */
    public void selectCode_1() {
        this.setValue("POST_CODE", this.getValueString("POST_P"));
        this.onPost();
    }

    /**
     * ͨ���ʱ�ĵõ�ʡ��
     */
    public void onPost() {
        String post = getValueString("POST_CODE");
        if (post == null || "".equals(post)) {
            getTextField("ADDRESS").grabFocus();
            return;
        }
        TParm parm = this.getPOST_CODE(post);
        setValue("POST_C", parm.getData("POST_CODE", 0).toString().substring(0,
            2));
        setValue("POST_P", parm.getData("POST_CODE", 0).toString());
        setValue("ADDRESS", parm.getData("STATE", 0).toString()
                 + parm.getData("CITY", 0));
        getTextField("ADDRESS").grabFocus();
    }

    /**
     * ͨ���ʱ�ĵõ�ʡ��
     */
    public void onPostClick() {
        String post = getValueString("POST_CODE");
        if (post == null || "".equals(post)) {
            return;
        }
        TParm parm = this.getPOST_CODE(post);
        setValue("POST_C", parm.getData("POST_CODE", 0).toString().substring(0,
            2));
        setValue("POST_P", parm.getData("POST_CODE", 0).toString());
        setValue("ADDRESS", parm.getData("STATE", 0).toString()
                 + parm.getData("CITY", 0));
    }

    /**
     * �õ�ʡ�д���
     *
     * @param post
     *            String
     * @return TParm
     */
    public TParm getPOST_CODE(String post) {
        TParm result = SYSPostTool.getInstance().getProvinceCity(post);
        return result;
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
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �������
     */
    private boolean CheckData() {
        if ("".equals(getValueString("MAN_CODE"))) {
            this.messageBox("���̴��벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("MAN_CHN_DESC"))) {
            this.messageBox("�������Ʋ���Ϊ��");
            return false;
        }
        if (!"".equals(getValueString("E_MAIL"))) {
            if (!StringTool.isEmail(getValueString("E_MAIL"))) {
                this.messageBox("�����ʽ����ȷ");
                return false;
            }
        }
        return true;
    }

    /**
     * �õ����ı�� +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName,
                         String dbBuffer) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // ��������
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // �������ֵ
            if (max < value) {
                max = value;
                continue;
            }
        }
        // ���ż�1
        max++;
        return max;
    }

}
