package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import jdo.sys.SYSSQL;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.util.TMessage;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>
 * Title: ����ҩƷ����
 * </p>
 *
 * <p>
 * Description:����ҩƷ����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author Zhangy
 * @version 1.0
 */

public class SYSCtrlDrugClassControl
    extends TControl {

    private String action = "save";

    private TTable table;

    public SYSCtrlDrugClassControl() {
    }

    public void onInit() {
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        int row = 0;
        Timestamp date = SystemTool.getInstance().getDate();
        if ("save".equals(action)) {
            TTextField combo = getTextField("CTRLDRUGCLASS_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "CTRLDRUGCLASS_CODE",
                          getValueString("CTRLDRUGCLASS_CODE"));
            table.setItem(row, "CTRLDRUGCLASS_CHN_DESC",
                          getValueString("CTRLDRUGCLASS_CHN_DESC"));
            table.setItem(row, "CTRLDRUGCLASS_ENG_DESC",
                          getValueString("CTRLDRUGCLASS_ENG_DESC"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "PRN_TYPE_CODE", getValueString("PRN_TYPE_CODE"));
            table.setItem(row, "PRN_TYPE_DESC", getValueString("PRN_TYPE_DESC"));
            table.setItem(row, "TAKE_DAYS", getValueInt("TAKE_DAYS"));
            table.setItem(row, "PRNSPCFORM_FLG",
                          getValueString("PRNSPCFORM_FLG"));
            table.setItem(row, "CTRL_FLG", getValueString("CTRL_FLG"));
            table.setItem(row, "NARCOTIC_FLG", getValueString("NARCOTIC_FLG"));
            table.setItem(row, "TOXICANT_FLG", getValueString("TOXICANT_FLG"));
            table.setItem(row, "PSYCHOPHA1_FLG", getValueString("PSYCHOPHA1_FLG"));
            table.setItem(row, "PSYCHOPHA2_FLG", getValueString("PSYCHOPHA2_FLG"));
            table.setItem(row, "RADIA_FLG", getValueString("RADIA_FLG"));
            table.setItem(row, "TEST_DRUG_FLG", getValueString("TEST_DRUG_FLG"));
            table.setItem(row, "ANTISEPTIC_FLG",
                          getValueString("ANTISEPTIC_FLG"));
            table.setItem(row, "ANTIBIOTIC_FLG",
                          getValueString("ANTIBIOTIC_FLG"));
            table.setItem(row, "TPN_FLG", getValueString("TPN_FLG"));
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
     * ��ѯ����
     */
    public void onQuery() {
        // ��ʼ��Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSCtrlDrugClass());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();

        String code = getValueString("CTRLDRUGCLASS_CODE");
        String filterString = "";
        if (code.length() > 0)
            filterString += "CTRLDRUGCLASS_CODE like '" + code + "%'";
        table.setFilter(filterString);
        table.filter();
    }


    /**
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������
        String clearString =
            "CTRLDRUGCLASS_CODE;CTRLDRUGCLASS_CHN_DESC;CTRLDRUGCLASS_ENG_DESC;"
            + "PY1;PY2;SEQ;DESCRIPTION;PRN_TYPE_CODE;PRN_TYPE_DESC;TAKE_DAYS;"
            + "PRNSPCFORM_FLG;CTRL_FLG;NARCOTIC_FLG;TOXICANT_FLG;"
            + "PSYCHOPHA1_FLG;PSYCHOPHA2_FLG;RADIA_FLG;TEST_DRUG_FLG;"
            + "ANTISEPTIC_FLG;ANTIBIOTIC_FLG;TPN_FLG";
        clearValue(clearString);

        table.setSelectionMode(0);
        getTextField("CTRLDRUGCLASS_CODE").setEnabled(true);
        //getTextField("ANTIBIOTIC_DESC").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
        // ���
        TDataStore dataStroe = table.getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
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
                "CTRLDRUGCLASS_CODE;CTRLDRUGCLASS_CHN_DESC;CTRLDRUGCLASS_ENG_DESC;"
                +
                "PY1;PY2;SEQ;DESCRIPTION;PRN_TYPE_CODE;PRN_TYPE_DESC;TAKE_DAYS;"
                +
                "PRNSPCFORM_FLG;CTRL_FLG;NARCOTIC_FLG;TOXICANT_FLG;PSYCHOPHA1_FLG;PSYCHOPHA2_FLG;"
                +
                "RADIA_FLG;TEST_DRUG_FLG;ANTISEPTIC_FLG;ANTIBIOTIC_FLG;TPN_FLG";
            this.setValueForParm(likeNames, parm);
            getTextField("CTRLDRUGCLASS_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
        }
    }

    /**
     * CTRLDRUGCLASS_CHN_DESC�س��¼�
     */
    public void onCtrldrugClassDescAction() {
        String py = TMessage.getPy(this.getValueString("CTRLDRUGCLASS_CHN_DESC"));
        setValue("PY1", py);
        //getTextField("PY1").grabFocus();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSCtrlDrugClass());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ����+1(SEQ)
        int seq = getMaxSeq(dataStore, "SEQ",
                            dataStore.isFilter() ? dataStore.FILTER :
                            dataStore.PRIMARY);
        setValue("SEQ", seq);

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
     * �������
     */
    private boolean CheckData() {
        if ("".equals(getValueString("CTRLDRUGCLASS_CODE"))) {
            this.messageBox("����ҩƷ������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("CTRLDRUGCLASS_CHN_DESC"))) {
            this.messageBox("����ҩƷ��������˵������Ϊ��");
            return false;
        }
        if (getValueDouble("TAKE_DAYS") <= 0) {
            this.messageBox("ʹ����������С�ڻ����O");
            return false;
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
