package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: �ʱ�ά��
 * </p>
 *
 * <p>
 * Description:�ʱ�ά��
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
 * @author ehui 200800905
 * @version 1.0
 */
public class SYSPostControl
    extends TControl {
    int selectRow = -1;
    private static final String TBL = "TABLE";
    TTable table;
    TDS tds;

    public void onInit() {
        super.onInit();
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSPostCode(""));
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();

        // ����+1(SEQ)
        int seq = getMaxSeq(dataStore, "SEQ",
                            dataStore.isFilter() ? dataStore.FILTER :
                            dataStore.PRIMARY);
        setValue("SEQ", seq);
    }

    /**
     * ��ʼ�����棬��ѯ���е�����
     *
     * @return TParm
     */
    public void onQuery() {
        StringBuffer sb = new StringBuffer();
        String state = this.getValueString("STATE");
        String city = this.getValueString("CITY");
        String postCode = this.getValueString("POST_CODE");
        if (!StringUtil.isNullString(state)) {
            sb.append("STATE='" + state + "' AND ");
            this.setValue("STATE_PY", getPY(state));
        }
        if (!StringUtil.isNullString(city)) {
            sb.append("CITY='" + city + "' AND ");
            this.setValue("CITY_PY", getPY(city));
        }
        if (!StringUtil.isNullString(postCode)) {
            sb.append("POST_CODE='" + postCode + "' AND ");
        }
        String filter = sb.toString();
        if (filter.endsWith(" AND ")) {
            filter = filter.substring(0, filter.lastIndexOf(" AND "));
        }
        // this.messageBox(filter);
        table.setFilter(filter);
        table.filter();
    }

    public void onClick() {
        int row = table.getSelectedRow();
        // this.messageBox_(row);
        if (row < 0) {
            return;
        }
        setFormValue(row);
    }

    /**
     * Ϊ�����ϵĿؼ���ֵ
     *
     * @param row
     *            ѡ������
     */
    public void setFormValue(int row) {
        TDataStore dataStore = table.getDataStore();
        int seq = dataStore.getItemInt(row, "SEQ");
        String state = dataStore.getItemString(row, "STATE");
        String statePy = dataStore.getItemString(row, "STATE_PY");
        String city = dataStore.getItemString(row, "CITY");
        String cityPy = dataStore.getItemString(row, "CITY_PY");
        String postCode = dataStore.getItemString(row, "POST_CODE");
        boolean isD = StringTool.getBoolean(dataStore.getItemString(row,
            "D_CITY_FLG"));
        this.setValue("SEQ", seq);
        this.setValue("STATE", state);
        this.setValue("STATE_PY", statePy);
        this.setValue("CITY", city);
        this.setValue("CITY_PY", cityPy);
        this.setValue("POST_CODE", postCode);
        this.setValue("D_CITY_FLG", isD);
        this.setValue("ENNAME", dataStore.getItemString(row, "ENNAME"));
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("POST_CODE;STATE;STATE_PY;CITY;CITY_PY;D_CITY_FLG;SEQ;ENNAME");
        table.setFilter("");
        table.filter();
    }

    /**
     * ����
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        int row = -1;
        int seq = getValueInt("SEQ");
        String state = getValueString("STATE");
        String statePy = getValueString("STATE_PY");
        String city = getValueString("CITY");
        String cityPy = getValueString("CITY_PY");
        String postCode = getValueString("POST_CODE");
        //boolean isD = StringTool.getBoolean(getValueString("D_CITY_FLG"));
        String isD = getValueString("D_CITY_FLG");

        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SYSSQL.getSYSPostCode(postCode)));
        int exist = parm.getCount("POST_CODE");
        TDataStore dataStore = table.getDataStore();
        if (exist < 0) {
            row = table.getDataStore().insertRow();
            dataStore.setItem(row, "SEQ", seq);
        }
        else {
            table = getTable("TABLE");
            table.removeRowAll();
            dataStore.setSQL(SYSSQL.getSYSPostCode(""));
            dataStore.retrieve();
            table.setDataStore(dataStore);
            table.setDSValue();
            String filter = "POST_CODE = '" + postCode + "'";
            table.setFilter(filter);
            table.filter();
            row = 0;
            seq = dataStore.getItemInt(row, "SEQ");
            dataStore.setItem(row, "SEQ", seq);
        }
        dataStore.setItem(row, "POST_CODE", postCode);
        dataStore.setItem(row, "STATE", state);
        dataStore.setItem(row, "STATE_PY", statePy);
        dataStore.setItem(row, "CITY", city);
        dataStore.setItem(row, "CITY_PY", cityPy);
        dataStore.setItem(row, "D_CITY_FLG", isD);
        dataStore.setItem(row, "ENNAME", this.getValueString("ENNAME"));
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", TJDODBTool.getInstance()
                          .getDBTime());
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());

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
     * ɾ��
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox_("�޿�ɾ������");
            return;
        }
        if (table.getDataStore().deleteRow(row)) {
            if (table.getDataStore().isModified()) {
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
            this.messageBox("P0003");
        }
        else {
            this.messageBox("E0003");
        }
        onClear();
    }

    /**
     * ����Code����ƴ��
     *
     * @param code
     * @return PY String
     */
    public String getPY(String code) {
        return TMessage.getPy(code);
    }

    public void onState() {
        this.setValue("STATE_PY", getPY(this.getValueString("STATE")));
        TTextField city = (TTextField)this.getComponent("CITY");
        city.grabFocus();
    }

    public void onCity() {
        getPY(this.getValueString("CITY"));
        this.setValue("CITY_PY", getPY(this.getValueString("CITY")));
        TTextField post = (TTextField)this.getComponent("POST_CODE");
        post.grabFocus();
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

    /**
     * �������
     */
    private boolean CheckData() {
        if ("".equals(getValueString("POST_CODE"))) {
            this.messageBox("�������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("STATE"))) {
            this.messageBox("ʡ��ֱϽ�в���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("CITY"))) {
            this.messageBox("���в���Ϊ��");
            return false;
        }
        return true;
    }

}
