package com.javahis.ui.inv;

import com.dongyang.ui.TMenuItem;
import jdo.inv.INVSQL;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.control.TControl;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTextField;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.util.TMessage;

/**
 * <p>Title: ����ԭ����</p>
 *
 * <p>Description: ����ԭ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.04.23
 * @version 1.0
 */
public class INVReasonControl
    extends TControl {
    private String action = "save";

    public INVReasonControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��Table
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INVSQL.getReason());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();

        // ����+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        int row = 0;
        if ("save".equals(action)) {
            TTextField combo = (TTextField) getComponent("REN_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "REN_CODE", getValueString("REN_CODE"));
            table.setItem(row, "REN_DESC", getValueString("REN_DESC"));
            table.setItem(row, "ENNAME", getValueString("ENNAME"));
            table.setItem(row, "DESCRIPTION",
                          getValueString("DESCRIPTION"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueString("SEQ"));
            table.setItem(row, "PUR_FLG", getValueString("PUR_FLG"));
            table.setItem(row, "VER_FLG", getValueString("VER_FLG"));
            table.setItem(row, "REG_FLG", getValueString("REG_FLG"));
            table.setItem(row, "REQ_FLG", getValueString("REQ_FLG"));
            table.setItem(row, "GIF_FLG", getValueString("GIF_FLG"));
            table.setItem(row, "RET_FLG", getValueString("RET_FLG"));
            table.setItem(row, "WAS_FLG", getValueString("WAS_FLG"));
            table.setItem(row, "DEL_FLG", getValueString("DEL_FLG"));
            table.setItem(row, "OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
        onClear();
    }

    /**
     * �������
     */
    private boolean CheckData() {
        String reason_code = getValueString("REN_CODE");
        if ("".equals(reason_code)) {
            this.messageBox("ԭ����벻��Ϊ��");
            return false;
        }
        String reason_desc = getValueString("REN_DESC");
        if ("".equals(reason_desc)) {
            this.messageBox("ԭ��˵������Ϊ��");
            return false;
        }
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(INVSQL.getReason(reason_code));
        dataStore.retrieve();
        if (dataStore.rowCount() > 0) {
            this.messageBox("ԭ������ظ����޷�����");
            return false;
        }
        return true;
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String code = getValueString("REN_CODE");
        String filterString = "REN_CODE like '" + code + "%'";
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INVSQL.getReason());
        dataStroe.retrieve();
        TTable table = getTable("TABLE");
        table.setDataStore(dataStroe);
        table.setDSValue();
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getTextField("REN_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        String tags =
            "REN_CODE;REN_DESC;ENNAME;DESCRIPTION;PY1;PY2;SEQ;PUR_FLG;"
            + "VER_FLG;REG_FLG;REQ_FLG;GIF_FLG;RET_FLG;WAS_FLG;DEL_FLG";
        clearValue(tags);
        // ����+1(SEQ)
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        TTable table = getTable("TABLE");
        table.setSelectionMode(0);
        action = "save";
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
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
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "REN_CODE;REN_DESC;ENNAME;DESCRIPTION;PY1;PY2;SEQ;PUR_FLG;"
                + "VER_FLG;REG_FLG;REQ_FLG;GIF_FLG;RET_FLG;WAS_FLG;DEL_FLG";
            this.setValueForParm(likeNames, parm);
            getTextField("REN_CODE").setEnabled(false);
            action = "save";
        }
    }

    /**
     * ԭ��˵���س��¼�
     */
    public void onReasonDescAction() {
        String name = getValueString("REN_DESC");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
        ( (TTextField) getComponent("ENNAME")).grabFocus();
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
    private int getMaxSeq(TDataStore dataStore, String columnName,
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
