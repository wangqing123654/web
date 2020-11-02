package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import jdo.sys.Operator;
import java.sql.Timestamp;
import java.util.Date;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SYSSQL;

/**
 * <p>
 * Title: ����ϵͳ=>��Ա����=>��Աְ��
 * </p>
 *
 * <p>
 * Description: ��Աְ��
 * </p>
 *
 * <p>
 * Copyright: Copyright JavaHis (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009-02-11
 * @version JavaHis 1.0
 */
public class SYSPositionControl
    extends TControl {

    public SYSPositionControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��������¼�
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");

        // ��ӵ����¼�
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClecked");
        // ����ɾ����ť����
        ( (TMenuItem) getComponent("delete")).setEnabled(false);

        TTable table = ( (TTable) getComponent("TABLE"));
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSPosition());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onTableChangeValue(Object obj) {
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
        // �ı�������
        String value = "" + node.getValue();
        int row = node.getRow();
        // ���Ƹı��ƴ��
        if ("POS_CHN_DESC".equals(columnName)) {
            if (value.length() == 0) {
                this.messageBox("ְ��˵������Ϊ��");
                return true;
            }
            // ��ƴ��
            String py = TMessage.getPy(value);
            node.getTable().setItem(row, "PY1", py);
            return false;
        }
        if ("POS_CODE".equals(columnName)) {
            if (value.length() == 0) {
                this.messageBox("ְ�ƴ��벻��Ϊ��");
                return true;
            }
            // �����ظ�
            if (node.getTable().getDataStore()
                .exist("POS_CODE='" + value + "'")) {
                messageBox_("���" + node.getValue() + "�ظ�!");
                return true;
            }
            return false;
        }
        if ("POS_TYPE".equals(columnName)) {
            if (value.length() == 0) {
                this.messageBox("ְ�������Ϊ��");
                return true;
            }
        }
        return false;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���POS_CODE��POS_CHN_DESC��POS_TYPE
        this.setValue("POS_CODE", "");
        this.setValue("POS_CHN_DESC", "");
        ( (TComboBox) getComponent("POS_TYPE")).setSelectedIndex(0);
        // ����ɾ����ť����
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * ��ѯ��ʼ���Զ�ִ���¼�
     */
    public void onQuery() {
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ��������
        String value = "";
        String str = getValueString("POS_CODE");
        if (str.length() != 0)
            value += "POS_CODE like '" + str + "%'";
        str = getValueString("POS_CHN_DESC");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += " POS_CHN_DESC like '" + str + "%'";
        }
        str = "" + callFunction("UI|POS_TYPE|getValue");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "POS_TYPE = '" + str + "'";
        }
        TTable table = (TTable) getComponent("TABLE");
        if (value.length() > 0) {
            table.setFilter(value);
            table.filter();
            return;
        }
        table.setFilter("");
        table.filter();
    }

    /**
     * ��������
     */
    public void onNew() {
        TTable table = (TTable) getComponent("TABLE");
        String maxCode = "";
        TDataStore dataStore = table.getDataStore();
        maxCode = getMaxCode(table.getDataStore(), "POS_CODE", dataStore
                             .isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
        // ��������ݵ�˳����
        int seq = getMaxSeq(table.getDataStore(), "SEQ");
        // ����ӵ��к�
        int row = table.addRow();
        // ��ǰѡ�е���
        table.setSelectedRow(row);
        // Ĭ��ְ�ƴ���
        table.setItem(row, "POS_CODE", maxCode);
        // Ĭ��˳����
        table.setItem(row, "SEQ", seq);
    }

    /**
     * ���淽��
     *
     * @return boolean
     */
    public boolean onSave() {
        Timestamp date = StringTool.getTimestamp(new Date());
        TTable table = (TTable) getComponent("TABLE");
        // �����ı�
        table.acceptText();
        TDataStore dataStore = table.getDataStore();

        // ���ݼ��
        for (int i = 0; i < dataStore.rowCount(); i++) {
            if ("".equals(dataStore.getItemString(i, "POS_CHN_DESC"))) {
                this.messageBox("ְ��˵������Ϊ��");
                return false;
            }
            else if ("".equals(dataStore.getItemString(i, "POS_TYPE"))) {
                this.messageBox("ְ�������Ϊ��");
                return false;
            }
        }

        // ���ȫ���Ķ����к�
        int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
        // ���̶�����������
        for (int i = 0; i < rows.length; i++) {
            //System.out.println(dataStore.getItemData(rows[i], 1));

            dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
            dataStore.setItem(rows[i], "OPT_DATE", date);
            dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
        }
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        table.setDSValue();
        return true;
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClecked() {
        TTable table = (TTable) getComponent("TABLE");
        // ���ѡ����
        int row = table.getSelectedRow();
        if (row < 0) {
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            return;
        }
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = (TTable) getComponent("TABLE");
        if (table.getTable().getSelectedRow() < 0)
            return;
        // ɾ��ָ����
        table.removeRow(table.getTable().getSelectedRow());
        if (table.getRowCount() > 0)
            table.setSelectedRow(0);
        else
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @param dbBuffer
     *            String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore, String columnName,
                             String dbBuffer) {
        if (dataStore == null)
            return "";
        // ����������
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // ��������
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = TCM_Transform.getString(dataStore.getItemData(i,
                columnName, dbBuffer));
            // �Ƚϳ���
            if (s.length() < value.length()) {
                s = value;
                continue;
            }
            // ��������жϴ�С
            if (s.length() == value.length()) {
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        String newStr = s;
        // ���ż�1
        s = StringTool.addString(s);
        if (newStr.equals(s)) {
            s = "1" + s;
            return s;
        }
        if (StringTool.compareTo(s, newStr) < 0)
            s = "1" + s;
        return s;
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
    public int getMaxSeq(TDataStore dataStore, String columnName) {
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
}
