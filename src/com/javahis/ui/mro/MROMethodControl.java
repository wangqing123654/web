package com.javahis.ui.mro;

import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import com.dongyang.data.TParm;

/**
 * <p>Title: �ʿط�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.4.27
 * @version 1.0
 */
public class MROMethodControl extends TControl{

    private String action = "save";
    // ������
    private TTable table;

    public MROMethodControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��Table
        table = getTable("TABLE");
        String sql = " SELECT METHOD_CODE, METHOD_DESC, MEDTHOD_ENG_DESC, "
            + " PY1, PY2, SEQ, DESCRIPTION, METHOD_TYPE_CODE, "
            + " OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM MRO_METHOD ORDER BY METHOD_CODE, SEQ";
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }
    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ��ʼ��Table
        table = getTable("TABLE");
        String sql = " SELECT METHOD_CODE, METHOD_DESC, MEDTHOD_ENG_DESC, "
            + " PY1, PY2, SEQ, DESCRIPTION, METHOD_TYPE_CODE, "
            + " OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM MRO_METHOD ORDER BY METHOD_CODE, SEQ";
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        String code = getValueString("METHOD_CODE");
        if (code.length() > 0) {
            String filterString = "METHOD_CODE = '" + code + "'";
            table.setFilter(filterString);
            table.filter();
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        if ("save".equals(action)) {
            TTextField combo = (TTextField) getComponent("METHOD_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }

            table.setItem(row, "METHOD_CODE",
                          getValueString("METHOD_CODE"));
            table.setItem(row, "METHOD_DESC",
                          getValueString("METHOD_DESC"));
            table.setItem(row, "MEDTHOD_ENG_DESC",
                          getValueString("MEDTHOD_ENG_DESC"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "METHOD_TYPE_CODE",
                          getValueString("METHOD_TYPE_CODE"));
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
    }

    /**
     * �������
     */
    private boolean CheckData() {
        if ("".equals(getValueString("METHOD_CODE"))) {
            this.messageBox("�������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("METHOD_DESC"))) {
            this.messageBox("�������Ʋ���Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.clearValue("METHOD_CODE;METHOD_DESC;MEDTHOD_ENG_DESC;"
                        + "PY1;PY2;SEQ;DESCRIPTION;METHOD_TYPE_CODE");
        table.setSelectionMode(0);
        ( (TTextField)this.getComponent("METHOD_CODE")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
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
     * �س��¼�
     */
    public void onDescAction(){
        String py = TMessage.getPy(this.getValueString("METHOD_DESC"));
        setValue("PY1", py);
        ( (TTextField) getComponent("MEDTHOD_ENG_DESC")).grabFocus();
    }

    /**
     * �����¼�
     */
    public void onTableClick() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "METHOD_CODE;METHOD_DESC;MEDTHOD_ENG_DESC;"
                + "PY1;PY2;SEQ;DESCRIPTION;METHOD_TYPE_CODE";
            this.setValueForParm(likeNames, parm);
            ( (TTextField) getComponent("METHOD_CODE")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
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
}
