package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import java.util.Vector;
import jdo.inv.INVSQL;
import com.javahis.system.combo.TComboOrgCode;
import com.dongyang.util.TMessage;
import jdo.sys.SystemTool;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ���ʿⷿ�趨</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class INVOrgControl
    extends TControl {
    public INVOrgControl() {
    }

    private Vector vct;

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
       dataStroe.setSQL(INVSQL.getorg());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();

        // ��½����������
        TComboBox region = getComboBox("REGION_CODE");
        region.setSelectedID(Operator.getRegion());

        // ����+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        // ��ѯ���е����пⷿ
        vct = dataStroe.getVector("ORG_CODE");
        // �ⷿ���͸ı��¼�
        onChangeOrgType();
    }

    /**
     * �ⷿ���͸ı��¼�
     */
    public void onChangeOrgType() {
        // ����ҩ��SUP_ORG_CODE
        TextFormatINVOrg sup_code = (TextFormatINVOrg)this.getComponent(
            "SUP_ORG_CODE");
        // ORG_TYPE_A:���� ��ORG_TYPE_B���п� ��ORG_TYPE_C��С��
        if (getRadioButton("ORG_TYPE_A").isSelected()) {
            onSetStatus(false);
            sup_code.setOrgType("");
            this.getCheckBox("STATION_FLG").setEnabled(false);
        }
        else if (getRadioButton("ORG_TYPE_B").isSelected()) {
            onSetStatus(true);
            sup_code.setOrgType("A");
            this.getCheckBox("STATION_FLG").setEnabled(false);
        }
        else {
            onSetStatus(true);
            sup_code.setOrgType("B");
            this.getCheckBox("STATION_FLG").setEnabled(true);
        }
        this.setValue("STATION_FLG", "N");
        onSetOrgCodeByOrgType();
        sup_code.onQuery();
    }

    /**
     * �趨ҳ��״̬
     *
     * @param parm
     *            ״̬����
     */
    private void onSetStatus(boolean flg) {
        // �����ⷿ
        TextFormatINVOrg sup_org = (TextFormatINVOrg)this.getComponent(
            "SUP_ORG_CODE");
        sup_org.setValue("");
        sup_org.setEnabled(flg);
    }

    /**
     * ���ݿⷿ����������ʲ����б�(ORG_CODE)
     *
     * @param org_type
     */
    private void onSetOrgCodeByOrgType() {
        this.getTextFormat("ORG_CODE").setPopupMenuSQL(
            INVSQL.getOrgCodeByOrgType());
        this.getTextFormat("ORG_CODE").onQuery();
        this.getTextFormat("ORG_CODE").setText("");
    }

    /**
     * ��ʿվ(CheckBox)�ı��¼�
     */
    public void onSelectStation() {
        // ���ݿⷿ���ͻ�ʿվ����ҩ���б�(ORG_CODE)
        onSetOrgCodeByTypeAndStation();
    }

    /**
     * ���ݿⷿ���ͻ�ʿվ����ҩ���б�(ORG_CODE)
     *
     * @param org_type
     * @param station_flg
     */
    private void onSetOrgCodeByTypeAndStation() {
        this.getTextFormat("ORG_CODE").setPopupMenuSQL(INVSQL.
            getOrgCodeByTypeAndStation());
        this.getTextFormat("ORG_CODE").onQuery();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        int row = 0;
        if (table.getSelectedRow() < 0) {
            // ��������
            if (!CheckData())
                return;
            row = table.addRow();
        }
        else {
            row = table.getSelectedRow();
        }
        table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
        TTextFormat org_code = this.getTextFormat("ORG_CODE");
        table.setItem(row, "ORG_DESC", org_code.getText());
        String py = TMessage.getPy(org_code.getName());
        table.getDataStore().setItem(row, "PY1", py);
        table.setItem(row, "SEQ", getValueString("SEQ"));
        String org_type = "C";
        if (getRadioButton("ORG_TYPE_A").isSelected())
            org_type = "A";
        else if (getRadioButton("ORG_TYPE_B").isSelected())
            org_type = "B";
        else
            org_type = "C";
        table.setItem(row, "ORG_TYPE", org_type);
        table.setItem(row, "STOCK_ORG_CODE", getValue("SUP_ORG_CODE"));
        table.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
        table.setItem(row, "STATION_FLG", getValueString("STATION_FLG"));
        table.setItem(row, "MAT_FLG", getValueString("MAT_FLG"));
        table.setItem(row, "INV_FLG", getValueString("INV_FLG"));
        table.setItem(row, "REMARK", getValueString("REMARK"));
        table.setItem(row, "AUTO_FILL_TYPE", getValueString("AUTO_FILL_TYPE"));
        table.setItem(row, "FIXEDAMOUNT_FLG", getValueString("FIXEDAMOUNT_FLG"));
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        table.setItem(row, "OPT_TERM", Operator.getIP());
        table.getDataStore().setItem(row, "ORG_FLG", "Y");
        if (!table.getDataStore().update()) {
            messageBox("E0001");
            return;
        }
        messageBox("P0001");
        table.setDSValue();
        vct = table.getDataStore().getVector("ORG_CODE");
        onClear();
    }

    /**
     * �������
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("�ⷿ���벻��Ϊ��");
            return false;
        }
        TextFormatINVOrg sup_org = (TextFormatINVOrg)this.getComponent(
            "SUP_ORG_CODE");
        if (sup_org.isEnabled() && "".equals(getValueString("SUP_ORG_CODE"))) {
            this.messageBox("��С�ⷿ�Ĺ���ҩ������Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("������벻��Ϊ��");
            return false;
        }
        for (int i = 0; i < vct.size(); i++) {
            if (org_code.equals( ( (Vector) vct.get(i)).get(0).toString())) {
                this.messageBox("�ⷿ�Ѵ���");
                return false;
            }
        }
        return true;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ҩ������
        TTextFormat org_code = getTextFormat("ORG_CODE");
        org_code.setEnabled(true);
        org_code.setValue("");

        // �ⷿ����
        getRadioButton("ORG_TYPE_C").setSelected(true);
        onChangeOrgType();

        // ��½����������
        TComboBox region = getComboBox("REGION_CODE");
        region.setSelectedID(Operator.getRegion());

        // ���
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);

        // ��ע
        setValue("REMARK", "");
        setValue("FIXEDAMOUNT_FLG", "");
        setValue("AUTO_FILL_TYPE", "");
        setValue("REMARK", "") ;

        // TABLE
        getTable("TABLE").setSelectionMode(0);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String region = getValueString("REGION_CODE");
        String type = "";
        if (getRadioButton("ORG_TYPE_A").isSelected())
            type = "A";
        else if (getRadioButton("ORG_TYPE_B").isSelected())
            type = "B";
        else
            type = "C";
        String org = getValueString("ORG_CODE");
        String filterString = "ORG_TYPE = '" + type + "'";
        if (region.length() > 0)
            filterString += " AND REGION_CODE = '" + region + "'";
        if (org.length() > 0)
            filterString += " AND ORG_CODE = '" + org + "'";
        TTable table = getTable("TABLE");
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * TABLE�����¼�
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "ORG_CODE;REGION_CODE;SEQ;REMARK";
            this.setValueForParm(likeNames, parm);
            String org_type = parm.getValue("ORG_TYPE");
            if ("A".equals(org_type))
                getRadioButton("ORG_TYPE_A").setSelected(true);
            else if ("B".equals(org_type))
                getRadioButton("ORG_TYPE_B").setSelected(true);
            else
                getRadioButton("ORG_TYPE_C").setSelected(true);
            onChangeOrgType();
            getCheckBox("STATION_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("STATION_FLG")));
            getCheckBox("MAT_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("MAT_FLG")));
            getCheckBox("INV_FLG").setSelected(
                TypeTool.getBoolean(parm.getValue("INV_FLG")));

            // ���ݿⷿ���ͻ�ʿվ����ҩ���б�(ORG_CODE)
            //onSetOrgCodeByTypeAndStation();
            this.getTextFormat("ORG_CODE").setValue(parm.getValue("ORG_CODE"));
           this.setValue("SUP_ORG_CODE", parm.getValue("STOCK_ORG_CODE"));
           this.setValue("AUTO_FILL_TYPE", parm.getValue("AUTO_FILL_TYPE"));
           this.setValue("FIXEDAMOUNT_FLG", parm.getValue("FIXEDAMOUNT_FLG"));

        }
    }

    /**
     * ɾ������
     */
    public void onDelete(){
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ɾ����");
            return;
        }
        table.removeRow(row);
        if (!table.getDataStore().update()) {
            messageBox("ɾ��ʧ��");
            return;
        }
        messageBox("ɾ���ɹ�");
        table.setDSValue();
        vct = table.getDataStore().getVector("ORG_CODE");
        //onClear();
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
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
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
