package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import jdo.sys.SYSSQL;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTextField;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:�������
 * </p>
 *
 * <p>
 * Description:�������
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
 * @author zhangy 2009.12.04
 * @version 1.0
 */
public class SYSBedTypeControl
    extends TControl {
    private String action = "save";
    // ������
    private TTable table;

    public SYSBedTypeControl() {
        super();
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
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSBedType());
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
     * ���淽��
     */
    public void onSave() {
        int row = 0;
        Timestamp date = SystemTool.getInstance().getDate();
        if ("save".equals(action)) {
            TTextField combo = getTextField("BED_TYPE_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "BED_TYPE_CODE",
                          getValue("BED_TYPE_CODE"));
            table.setItem(row, "BEDTYPE_DESC",
                          getValue("BEDTYPE_DESC"));
            table.setItem(row, "PY1", getValue("PY1"));
            table.setItem(row, "PY2", getValue("PY2"));
            table.setItem(row, "ENNAME", getValueString("ENNAME"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            table.setItem(row, "DESCRIPTION", getValue("DESCRIPTION"));
            table.setItem(row, "LAB_DISCNT_FLG",
                          getValueString("LAB_DISCNT_FLG"));
            table.setItem(row, "ISOLATION_FLG", getValueString("ISOLATION_FLG"));
            table.setItem(row, "BURN_FLG", getValueString("BURN_FLG"));
            table.setItem(row, "PEDIATRIC_FLG", getValueString("PEDIATRIC_FLG"));
            table.setItem(row, "OBSERVATION_FLG",
                          getValueString("OBSERVATION_FLG"));
            table.setItem(row, "TRANSPLANT_FLG",
                          getValueString("TRANSPLANT_FLG"));
            table.setItem(row, "ICU_FLG", getValueString("ICU_FLG"));
            table.setItem(row, "CCU_FLG", getValueString("CCU_FLG"));
            table.setItem(row, "BC_FLG", getValueString("BC_FLG"));
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
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������
        String clearString = "BED_TYPE_CODE;BEDTYPE_DESC;PY1;PY2;SEQ;"
            +
            "DESCRIPTION;LAB_DISCNT_FLG;ISOLATION_FLG;BURN_FLG;PEDIATRIC_FLG;"
            + "OBSERVATION_FLG;TRANSPLANT_FLG;ICU_FLG;CCU_FLG;BC_FLG;"
            + "OPT_USER;OPT_DATE;OPT_TERM;ENNAME";
        clearValue(clearString);
        // ���
        TDataStore dataStroe = table.getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        table.setSelectionMode(0);
        getTextField("BED_TYPE_CODE").setEnabled(true);
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
     * TABLE�����¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "BED_TYPE_CODE;BEDTYPE_DESC;PY1;PY2;SEQ;BURN_FLG;"
                + "DESCRIPTION;LAB_DISCNT_FLG;ISOLATION_FLG;PEDIATRIC_FLG;"
                + "OBSERVATION_FLG;TRANSPLANT_FLG;ICU_FLG;CCU_FLG;BC_FLG;ENNAME";
            this.setValueForParm(likeNames, parm);
            getTextField("BED_TYPE_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ��ʼ��Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSBedType());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();

        String type_code = getValueString("BED_TYPE_CODE");
        String filterString = "";
        if (type_code.length() > 0)
            filterString += "BED_TYPE_CODE like '" + type_code + "%'";
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * TypeDesc�س��¼�
     */
    public void onTypeDescAction() {
        String py = TMessage.getPy(this.getValueString("BEDTYPE_DESC"));
        setValue("PY1", py);
        getTextField("PY1").grabFocus();
    }


    /**
     * �������
     */
    private boolean CheckData() {
        if ("".equals(getValueString("BED_TYPE_CODE"))) {
            this.messageBox("�����벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("BEDTYPE_DESC"))) {
            this.messageBox("������Ʋ���Ϊ��");
            return false;
        }
        return true;
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
