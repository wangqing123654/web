package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.INVSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTextFormat;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * <p>
 * Title: �Զ���������
 * </p>
 *
 * <p>
 * Description: �Զ���������
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

public class INVAssignorgControl
    extends TControl { 

    private String action = "save";

    public INVAssignorgControl() {
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
     * ���淽��
     */
    public void onSave() {  
        TTable table = getTable("TABLE"); 
        int row = 0; 
        if ("save".equals(action)) {  
            TTextFormat combo = this.getTextFormat("ORG_CODE");
            boolean flg = combo.isEnabled();
            if (flg) { 
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            } 
            table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
            table.setItem(row, "CYCLE_TYPE", getValueString("CYCLE_TYPE"));
            table.setItem(row, "OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
            String assignorg = accountAssignorg(getValueString("CYCLE_TYPE"));
            table.getDataStore().setItem(row, "ASSIGNED_DAY", assignorg);
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
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
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
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String org = getValueString("ORG_CODE");
        String cycle = getValueString("CYCLE_TYPE");
        String filterString = "";
        if (org.length() > 0 && cycle.length() > 0) {
            filterString = "ORG_CODE = '" + org + "' AND CYCLE_TYPE = '"
                + cycle + "'";
        }
        else if (org.length() > 0) {
            filterString = "ORG_CODE = '" + org + "'";
        }
        else if (cycle.length() > 0) {
            filterString = "CYCLE_TYPE = '" + cycle + "'";
        }
        else {
            filterString = "";
        }
        TTable table = getTable("TABLE");
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getTextFormat("ORG_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        String tags = "ORG_CODE;CYCLE_TYPE";
        clearValue(tags);
        onSetAssignorg("0", ""); 
        onSetAssignorg("1", "");
        TTable table = getTable("TABLE");
        table.setSelectionMode(0);
        action = "save";
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
            String likeNames = "ORG_CODE;CYCLE_TYPE";
            this.setValueForParm(likeNames, parm);
            getTextFormat("ORG_CODE").setEnabled(false);
            this.onChangeCycleType() ;
            onSetAssignorg(parm.getValue("CYCLE_TYPE"), parm
                           .getValue("ASSIGNED_DAY"));
            
            action = "save";
        }
    }

    /**
     * ѭ����ʽ�ı��¼�
     */
    public void onChangeCycleType() {
        if ("0".equals(getComboBox("CYCLE_TYPE").getValue())) {
            getPanel("Panel_W").setVisible(false);
            getPanel("Panel_M").setVisible(true);
        }
        else {
            getPanel("Panel_M").setVisible(false);
            getPanel("Panel_W").setVisible(true);
        }
        onSetAssignorg("0", "");
        onSetAssignorg("1", "");
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��Table
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INVSQL.getAllAssignorg());  
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * ͳ�Ʋ�����
     *
     * @param cycle_type
     * @return
     */
    private String accountAssignorg(String cycle_type) {
        int count = 7;
        String day = "W_";
        if ("0".equals(cycle_type)) {
            count = 31;
            day = "M_";
        }
        String result = "";
        for (int i = 1; i <= count; i++) {
            result += getValueString(day + i);
        }
        return result;
    }

    /**
     * ���ò�����״̬
     *
     * @param cycle_type
     * @param status
     */
    private void onSetAssignorg(String cycle_type, String status) {
        int count = 7;
        String day = "W_";
        if ("0".equals(cycle_type)) {
            count = 31;
            day = "M_";
        }
        boolean flg = false;
        for (int i = 1; i <= count; i++) {
            if (status.length() > 0) {
                flg = TypeTool.getBoolean(status.substring(i - 1, i));
            }
            getCheckBox(day+i).setSelected(flg);
        }
    }
    /**
     * �������
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("�������Ų���Ϊ��");
            return false;
        }
        String material_loc_code = getValueString("CYCLE_TYPE");
        if ("".equals(material_loc_code)) {
            this.messageBox("ѭ����ʽ����Ϊ��");
            return false;
        }  
        //��Ӧ����
        String sql = INVSQL.getAssignorg(org_code);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        if (dataStore.rowCount() > 0) {
            this.messageBox("�������Ŵ��ڣ��޷�����");
            return false;
        }
        return true;  
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
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

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
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
     * �õ�Panel����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
    }
}
