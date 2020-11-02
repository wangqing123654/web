package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import jdo.sys.SYSSQL;

/**
 * <p>Title:���ô������ </p>
 *
 * <p>Dription:���ô������ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class SYSChargeHospCodeControl
    extends TControl {
    TTable table;
    /**
     * ��ʼ������
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked"); //table �������¼�
        //��Table��������¼�
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");

        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSChargeHosp());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
    }

    /**
     * ����������ѯ����
     * @return TParm
     */
    public void onQuery() {
        String filter = "";
        //Ժ�ڷ��ô���
        String value = getValueString("CHARGE_HOSP_CODE");
        if (value != null && value.length() > 0)
            filter = "CHARGE_HOSP_CODE ='" + value + "'";
        //�����վݷ���
        value = getValueString("OPD_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += "OPD_CHARGE_CODE ='" + value + "'";
        }
        //סԺ�վݷ���
        value = getValueString("IPD_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += "IPD_CHARGE_CODE ='" + value + "'";
        }
        //��ҳ����
        value = getValueString("MRO_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " MRO_CHARGE_CODE ='" + value + "'";
        }
        //ͳ�ƴ���
        value = getValueString("STA_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " STA_CHARGE_CODE ='" + value + "'";
        }
        //Ժ�ڷ�������
        value = getValueString("CHARGE_HOSP_DESC");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " CHARGE_HOSP_DESC like '" + value + "%'";
        }
        //Ӣ��
        value = getValueString("ENG_DESC");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " ENG_DESC like '" + value + "%'";
        }
       //����ί����
        value = getValueString("WJW_CHARGE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " WJW_CHARGE = '" + value + "'";
        }

        table.setFilter(filter);
        table.filter();
    }

    /**
     * table���
     */
    public void onTableCtzClicked() {
        int row = table.getSelectedRow();
        //�����Ϸ�
        TParm parm = table.getDataStore().getRowParm(row);
        setTextValue(parm);
        //��ݴ��벻����
        setTextEnabled(false);
    }

    /**
     * �����ϲ��ɱ༭�Ŀؼ�
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
        callFunction("UI|CHARGE_HOSP_CODE|setEnabled", boo);
    }

    /**
     * ���
     */
    public void onClear() {
        clearText();
        clearTable(table);
        setTextEnabled(true);
    }

    /**
     * �������
     */
    public void clearText() {
        clearText("CHARGE_HOSP_CODE;CHARGE_HOSP_DESC;ENG_DESC;WJW_CHARGE;"
                        + "OPD_CHARGE_CODE;IPD_CHARGE_CODE;MRO_CHARGE_CODE;"
                        + "STA_CHARGE_CODE;PY1;PY2;DESCRIPT");
    }

    /**
     * ��ձ�
     * @param table TTable
     */
    public void clearTable(TTable table) {
        table.retrieve();
        table.setDSValue();
    }

    /**
     * �����Ϸ�
     * @param parm TParm
     * @param row int
     */
    public void setTextValue(TParm parm) {
        setValueForParm("CHARGE_HOSP_CODE;CHARGE_HOSP_DESC;ENG_DESC;WJW_CHARGE;"
                        + "OPD_CHARGE_CODE;IPD_CHARGE_CODE;MRO_CHARGE_CODE;"
                        + "STA_CHARGE_CODE;PY1;PY2;DESCRIPT;"
                        + "OPT_DATE;OPT_USER;OPT_TERM",
                        parm);
    }

    /**
     * �������
     */
    public boolean onNew() { //�õ�table����
        int row = table.getSelectedRow();
        //ѡ�������
        if (row >= 0) {
            return updateData(row);
        }
        //ûѡ��������
        return newData();
    }

    /**
     * ���еĸ���
     * @param row int
     * @return boolean
     */
    public boolean updateData(int row) {
        //��������
        String chargeHospDesc = getValueString("CHARGE_HOSP_DESC").trim();
        String oldChargeHospDesc = table.getItemString(row, "CHARGE_HOSP_DESC");
        if (!oldChargeHospDesc.endsWith(chargeHospDesc)) {
            if (!checkChargeHospDesc(chargeHospDesc))
                return false;
            table.setItem(row, "CHARGE_HOSP_DESC", chargeHospDesc);
            table.setItem(row, "PY1", getValueString("PY1"));
        }

        //�վݷ��ò���Ϊ��
        String opdchargeCode = getValueString("OPD_CHARGE_CODE").trim();
        String oldopdChargeCode = table.getItemString(row, "OPD_CHARGE_CODE");
        if (!oldopdChargeCode.equals(opdchargeCode)) {
            //if (!checkChargeCode(opdchargeCode))
            //    return false;
            table.setItem(row, "OPD_CHARGE_CODE", opdchargeCode);
        }

        //�վݷ��ò���Ϊ��
        String ipdchargeCode = getValueString("IPD_CHARGE_CODE").trim();
        String oldipdChargeCode = table.getItemString(row, "IPD_CHARGE_CODE");
        if (!oldipdChargeCode.equals(ipdchargeCode)) {
            //if (!checkChargeCode(ipdchargeCode))
            //    return false;
            table.setItem(row, "IPD_CHARGE_CODE", ipdchargeCode);
        }

        //��ҳ����
        String mroChargeCode = getValueString("MRO_CHARGE_CODE").trim();
        String oldMroChargeCode = table.getItemString(row, "MRO_CHARGE_CODE");
        if (!oldMroChargeCode.equals(mroChargeCode)) {
            if (!checkChargeCode(mroChargeCode))
                return false;
            table.setItem(row, "MRO_CHARGE_CODE", mroChargeCode);
        }
        //ͳ�Ʒ���
        String staChargeCode = getValueString("STA_CHARGE_CODE").trim();
        String oldStaChargeCode = table.getItemString(row, "STA_CHARGE_CODE");
        if (!oldStaChargeCode.equals(staChargeCode)) {
            if (!checkChargeCode(staChargeCode))
                return false;
            table.setItem(row, "STA_CHARGE_CODE", staChargeCode);
        }
      //ͳ�Ʒ���
        String wjwChargeCode = getValueString("WJW_CHARGE").trim();
        String oldWjwChargeCode = table.getItemString(row, "WJW_CHARGE");
        if (!oldWjwChargeCode.equals(wjwChargeCode)) {
            if (!checkChargeCode(wjwChargeCode))
                return false;
            table.setItem(row, "WJW_CHARGE", wjwChargeCode);
        }

        table.setItem(row, "DESCRIPT", this.getValueString("DESCRIPT"));
        table.setItem(row, "PY1", this.getValueString("PY1"));
        table.setItem(row, "ENG_DESC", this.getValueString("ENG_DESC"));

        //���
        int seq = getValueInt("SEQ");
        int oldSeq = table.getItemInt(row, "SEQ");
        if (oldSeq != seq)
            table.setItem(row, "SEQ", seq);
        //������
        String py2 = getValueString("PY2");
        String oldPy2 = table.getItemString(row, "PY2");
        if (!oldPy2.equals(py2))
            table.setItem(row, "PY2", py2);
        return true;
    }

    /**
     * ���ͳ�Ʒ��ò���Ϊ��
     * @param staChargeCode String
     * @return boolean
     */
    public boolean checkStaChargeCode(String staChargeCode) {
        if (staChargeCode == null || staChargeCode.length() <= 0) {
            messageBox_("ͳ�Ʒ��ò���Ϊ��!");
            return false;
        }
        return true;
    }

    /**
     * �����ҳ����
     * @param mroChargeCode String
     * @return boolean
     */
    public boolean checkMroChargeCode(String mroChargeCode) {
        if (mroChargeCode == null || mroChargeCode.length() <= 0) {
            messageBox_("��ҳ���ò���Ϊ��!");
            return false;
        }
        return true;
    }

    /**
     * ����վݷ��ô���
     * @param chargeCode String
     * @return boolean
     */
    public boolean checkChargeCode(String chargeCode) {
        if (chargeCode == null || chargeCode.length() <= 0) {
            messageBox_("�վݷ��ò���Ϊ��!");
            return false;
        }
        return true;
    }

    /**
     * ��������Ƿ����ظ�
     * @param chargeHospDesc String
     * @return boolean
     */
    public boolean checkChargeHospDesc(String chargeHospDesc) {
        if (chargeHospDesc == null || chargeHospDesc.length() <= 0) {
            messageBox_("�������Ʋ���Ϊ��!");
            return false;
        }
        if (table.getDataStore().exist("CHARGE_HOSP_DESC='" + chargeHospDesc +
                                       "'")) {
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�����ظ��Ƿ����!", this.YES_NO_OPTION)) {
                //����
                case 0:
                    return true;
                    //������
                case 1:
                    return false;
            }
        }
        return true;
    }

    /**
     * ��˷��ô���
     * @param chargeHospCode String
     * @return boolean
     */
    public boolean checkChargeHospCode(String chargeHospCode) {
        if (chargeHospCode == null || chargeHospCode.length() <= 0) {
            messageBox_("���ô��벻��Ϊ��!");
            return false;
        }
        if (table.getDataStore().exist("CHARGE_HOSP_CODE='" + chargeHospCode +
                                       "'")) {
            messageBox_("����" + chargeHospCode + "�Ѵ���!");
            return false;
        }
        return true;
    }

    /**
     * ��������
     * @return boolean
     */
    public boolean newData() {
        //�����ı�
        table.acceptText();
        //���ô���
        String chargeHospCode = getValueString("CHARGE_HOSP_CODE").trim();
        if (!checkChargeHospCode(chargeHospCode))
            return false;
        //��������
        String chargeHospDesc = getValueString("CHARGE_HOSP_DESC").trim();
        if (!checkChargeHospDesc(chargeHospDesc))
            return false;

        //�����վݷ��ò���Ϊ��
        String opdchargeCode = getValueString("OPD_CHARGE_CODE").trim();
//        if (!checkChargeCode(opdchargeCode))
//            return false;

        //סԺ�վݷ��ò���Ϊ��
        String ipdchargeCode = getValueString("IPD_CHARGE_CODE").trim();
//        if (!checkChargeCode(ipdchargeCode))
//            return false;

        //��ҳ����
        String mroChargeCode = getValueString("MRO_CHARGE_CODE").trim();
        if (!checkChargeCode(mroChargeCode))
            return false;

        //ͳ�Ʒ���
        String staChargeCode = getValueString("STA_CHARGE_CODE").trim();
        if (!checkChargeCode(staChargeCode))
            return false;
        //�õ�����ӵ�table�����к�
        int row = table.addRow();
        table.setItem(row, "CHARGE_HOSP_CODE", chargeHospCode);
        table.setItem(row, "CHARGE_HOSP_DESC", chargeHospDesc);
        table.setItem(row, "OPD_CHARGE_CODE", opdchargeCode);
        table.setItem(row, "IPD_CHARGE_CODE", ipdchargeCode);
        table.setItem(row, "MRO_CHARGE_CODE", mroChargeCode);
        table.setItem(row, "STA_CHARGE_CODE", staChargeCode);
        table.setItem(row, "WJW_CHARGE", this.getValueString("WJW_CHARGE"));
        table.setItem(row, "DESCRIPT", this.getValueString("DESCRIPT"));
        table.setItem(row, "ENG_DESC", this.getValueString("ENG_DESC"));

        //���
        int seq = getValueInt("SEQ");
        if (seq <= 0)
            seq = getMaxSeq(table.getDataStore(), "SEQ");
        table.setItem(row, "SEQ", seq);
        table.setItem(row, "PY1", getValueString("PY1"));
        //������
        table.setItem(row, "PY2", getValueString("PY2"));
        //���õ�ǰѡ����Ϊ��ӵ���
        table.setSelectedRow(row);
        return true;
    }

    /**
     *ɾ��ѡ�е���
     * @param row int
     */
    public void onDelete() {
        switch (this.messageBox("��ʾ��Ϣ",
                                "ȷ��ɾ��!", this.YES_NO_OPTION)) {
            //����
            case 0:
                break;
                //������
            case 1:
                return;
        }
        //�����ı�
        table.acceptText();
        table.removeRow(table.getSelectedRow());
        saveData();
    }

    /**
     * �������
     */
    public boolean onUpdate() {
        if (!onNew())
            return false;
        return saveData();
    }

    /**
     * ��������
     * @return boolean
     */
    public boolean saveData() {
        //this.messageBox("");
        Timestamp date = SystemTool.getInstance().getDate();

        //�����ı�
        table.acceptText();
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isFilter()) {
            table.setFilter("");
            table.filter();
        }

        //���ȫ���Ķ����к�
        int rows[] = table.getModifiedRows();
        //���̶�����������
        for (int i = 0; i < rows.length; i++) {
            table.setItem(rows[i], "OPT_USER", Operator.getID());
            table.setItem(rows[i], "OPT_DATE", date);
            table.setItem(rows[i], "OPT_TERM", Operator.getIP());
        }
        table.getDataStore().setItem(table.getSelectedRow(), "DESCRIPT",
                                     this.getValueString("DESCRIPT"));
        table.getDataStore().showDebug();
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        afterSave();
        return true;
    }

    /**
     * ����������ý���
     */
    public void afterSave() {
        onClear();
    }

    /**
     * �Ƿ�رմ���
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing() {
        // ��������ݱ��
        if (CheckChange())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!saveData())
                        return false;
                    break;
                    //������
                case 1:
                    return true;
                    //����
                case 2:
                    return false;
            }
        //û�б��������
        return true;

    }

    /**
     * �õ��������
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        //����������
        int count = dataStore.rowCount();
        //��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            //�������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        //���ż�1
        s++;
        return s;
    }

    /**
     * ����Ƿ������ݱ��
     * @return boolean
     */
    public boolean CheckChange() {
        //������ݱ��
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.isModified())
            return true;
        return false;
    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
    }

}
