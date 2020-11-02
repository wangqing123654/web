package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.SYSChargeHospCodeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSSQL;
import com.dongyang.manager.TIOM_AppServer;
import action.sys.SYSCTZAction;

/**
 * <p>Title:�����ϸ </p>
 *
 * <p>Description:�����ϸ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSCTZControl
    extends TControl {

    /**
     * ��ǰѡ�еı�
     */
    TTable tableNow = null;
    /**
     * ��ݱ�
     */
    TTable tableCtz;
    /**
     * ����ۿ۱�
     */
    TTable tableCharge;
    /**
     * �õ����е��վ����
     */
    TParm allCode = SYSChargeHospCodeTool.getInstance().selectalldata();

    /**
     * ��
     */
    TTree tree;

    private int row;

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //��Table��������¼�
        addEventListener("TABLEHOSPCHARGEDETIAL->" + TTableEvent.CHANGE_VALUE,
                         "onTableHspChargeChangeValue");
        //��ǰѡ�е�table
        tableCtz = (TTable)this.getComponent("TABLECTZ");

        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSCtz());
        dataStore.retrieve();
        tableCtz.setDataStore(dataStore);
        tableCtz.setDSValue();

        tableNow = tableCtz;
        tableCharge = (TTable)this.getComponent("TABLEHOSPCHARGEDETIAL");

        TDataStore dataStore2 = new TDataStore();
        dataStore2.setSQL(SYSSQL.getSYSChargeDetail());
        dataStore2.retrieve();
        tableCharge.setDataStore(dataStore2);
        tableCharge.setDSValue();

        //�������ݴ���
        equsDate();
        initCombox();
        this.tableClearValue(tableCharge);
    }

    /**
     * ��ݱ�ĵ��ʱ��
     */
    public void onTableCtzClicked() {
        row = tableCtz.getSelectedRow();
        //�����Ϸ�
        TParm tableCtzParm = tableCtz.getDataStore().getRowParm(row);
        setTextValue(tableCtzParm);
        //��ݴ��벻����
        setTextEnabled(false);
        //������ϸ
        String ctzCode = tableCtz.getItemString(row, "CTZ_CODE");
        String str = "CTZ_CODE='" + ctzCode + "'";
        tableCharge.setFilter(str);
        tableCharge.filter();
    }

    /**
     * �����ϲ��ɱ༭�Ŀؼ�
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
        callFunction("UI|CTZ_CODE|setEnabled", boo);
    }

    /**
     * ���
     */
    public void onClear() {
        clearQuery();
        clearText();
        clearTable(tableCtz);
        clearTable(tableCharge);
        tableClearValue(tableCharge);
        setTextEnabled(true);
        row = -1;
    }

    /**
     * ��ղ�ѯ����
     */
    public void clearQuery() {
        clearText("CTZCODE;COMPANYCODE");
        this.setValue("MAINCTZFLG", "N");
        this.setValue("NHICTZFLG", "N");
    }

    /**
     * �������
     */
    public void clearText() {
        clearText("CTZ_CODE;CTZ_DESC;NHI_COMPANY_CODE;PY1;SEQ;NHI_NO;DESCRIPT;MRO_CTZ");
        this.setValue("NHI_CTZ_FLG", "N");
        this.setValue("MAIN_CTZ_FLG", "N");
        this.setValue("MRCTZ_UPD_FLG", "N");
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
     * �����ʾ
     * @param table TTable
     */
    public void tableClearValue(TTable table) {
        table.setFilter("CTZ_CODE='" + null +"'");
        table.filter();
    }

    /**
     * �����Ϸ�
     * @param parm TParm
     */
    public void setTextValue(TParm parm) {
        setValueForParm("CTZ_CODE;CTZ_DESC;NHI_COMPANY_CODE;NHI_CTZ_FLG;MAIN_CTZ_FLG;MRCTZ_UPD_FLG;PY1;SEQ;NHI_NO;DESCRIPT;MRO_CTZ",
                        parm);
    }

    /**
     * ��ʼ�������ϵ�combox
     */
    public void initCombox() {
        TParm parm = new TParm();
        //��ʿվparm
        tableCtz.acceptText();
        TDataStore dateStore = tableCtz.getDataStore();
        String name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        parm = dateStore.getBuffer(name);
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:CTZ_CODE;name:CTZ_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableCharge.addItem("CTZ", comboBox);

        comboBox = (TComboBox)this.getComponent("CTZCODE");
        comboBox.setParmValue(parm);
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String filterSql = "";
        String value = getValueString("CTZ_CODE").trim();
        //��ݴ���
        if (value != null && value.length() > 0) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " CTZ_CODE='" + value + "' ";
        }
        //��λ
        value = getValueString("COMPANYCODE").trim();
        if (value != null && value.length() > 0) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " NHI_COMPANY_CODE='" + value + "' ";
        }
        //ҽ�����
        value = getValueString("NHICTZFLG").trim();
        if (value != null && value.equals("Y")) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " NHI_CTZ_FLG='" + value + "' ";
        }
        //�����
        value = getValueString("MAINCTZFLG").trim();
        if (value != null && value.equals("Y")) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " MAIN_CTZ_FLG='" + value + "' ";
        }
        tableCtz.setFilter(filterSql);
        tableCtz.filter();
        tableCtz.setDSValue();

    }

    /**
     * ��ʼ��ʱ��������������ȫ�����շ����
     */
    public void equsDate() {
        //ȡȫ������
        TDataStore dataStoreCtz = tableCtz.getDataStore();
        //ȥ��������
        int ctzCount = dataStoreCtz.rowCount();

        //�ۿ�����
        TDataStore dataStoreHospCharge = tableCharge.getDataStore();
        //�ۿ�������
        int hospChargeCount = dataStoreHospCharge.rowCount();
        //��������
        int addCount = hospChargeCount;
        //�շ����������
        int chargeCount = allCode.getCount();
        //��¼�Ƿ���
        String have = "N";
        for (int i = 0; i < ctzCount; i++) {
            //��ݴ���
            String ctzCodeCtz = dataStoreCtz.getItemString(i, "CTZ_CODE");

            for (int z = 0; z < chargeCount; z++) {
                String chargeHospCode = allCode.getValue("CHARGE_HOSP_CODE",
                    z);
                //��ݵ��е����+���ô���
                String strCtz = ctzCodeCtz + chargeHospCode;
                //��ձ��
                have = "N";
                for (int j = 0; j < hospChargeCount; j++) {
                    //�ۿ۵��е���ݴ���
                    String ctzCodeHosp = dataStoreHospCharge.getItemString(j,
                        "CTZ_CODE");
                    String chargeCode = dataStoreHospCharge.getItemString(j,
                        "CHARGE_HOSP_CODE");
                    //�ۿ۵��е����+����
                    String strHosp = ctzCodeHosp + chargeCode;

                    if (strCtz.equals(strHosp))
                        //��¼����
                        have = "Y";
                }
                //���û��
                if (have.equals("N")) {
                    //�õ�����ӵ�table�����к�
                    addCount = tableCharge.addRow();
                    //���õ�ǰѡ����Ϊ��ӵ���
                    tableCharge.setSelectedRow(addCount);
                    //Ĭ�����
                    tableCharge.setItem(addCount, "CTZ_CODE", ctzCodeCtz);
                    //Ĭ��Ժ�ڷ���
                    tableCharge.setItem(addCount, "CHARGE_HOSP_CODE",
                                        chargeHospCode);
                    //Ĭ���ۿ�
                    tableCharge.setItem(addCount, "DISCOUNT_RATE", 1.0);
                }
            }
        }
    }

    /**
     * �õ����������������
     * @return TParm
     */
    public TParm getTextParm() {
        TParm parm = getParmForTag(
            "CTZ_CODE;CTZ_DESC;NHI_COMPANY_CODE;NHI_CTZ_FLG;" +
            "MAIN_CTZ_FLG;MRCTZ_UPD_FLG;PY1;SEQ;NHI_NO;DESCRIPT;MRO_CTZ");
        return parm;

    }

    /**
     * �������
     * @param selectedRow int
     * @return boolean
     */
    public boolean updateCtz(int selectedRow) {
        //����������
        String ctzDesc = getValueString("CTZ_DESC").trim();
        String oldCtzdesc = tableCtz.getItemString(selectedRow, "CTZ_DESC");
        //�������ctzDesc
        if (!oldCtzdesc.equals(ctzDesc)) {
            if (!checkCtzDesc(ctzDesc))
                return false;
            tableCtz.setItem(selectedRow, "CTZ_DESC", ctzDesc);
            tableCtz.setItem(selectedRow, "PY1", getValueString("PY1"));
        }
        //�����λ����Ϊ��
        String companyCode = getValueString("NHI_COMPANY_CODE");
        String oldCompanyCode = tableCtz.getItemString(selectedRow,
            "NHI_COMPANY_CODE");
        //���������λ
        if (!oldCompanyCode.equals(companyCode)) {
            if (!checkCompanyCode(companyCode))
                return false;
            tableCtz.setItem(selectedRow, "NHI_COMPANY_CODE", companyCode);
        }
        //�õ������
        int seq = getValueInt("SEQ");
        int oldSeq = tableCtz.getItemInt(selectedRow, "SEQ");
        if (seq != oldSeq)
            tableCtz.setItem(selectedRow, "SEQ", seq);
        //ҽ������==pangben 2012-2-7
        String nhiNo = getValueString("NHI_NO");
        String oldNhiNo = tableCtz.getItemString(selectedRow, "NHI_NO");
        if (nhiNo != null && !nhiNo.equals(oldNhiNo))
            tableCtz.setItem(selectedRow, "NHI_NO", nhiNo);
        //��ע
        String descript = getValueString("DESCRIPT");
        String oldDescript = tableCtz.getItemString(selectedRow, "DESCRIPT");
        if (descript != null && !descript.equals(oldDescript))
            tableCtz.setItem(selectedRow, "DESCRIPT", descript);
        //��ҳ���
        String mroCtz = getValueString("MRO_CTZ");
        String oldmroCtz = tableCtz.getItemString(selectedRow, "MRO_CTZ");
        if (mroCtz != null && !mroCtz.equals(oldmroCtz))
            tableCtz.setItem(selectedRow, "MRO_CTZ", mroCtz);
        initCombox();
        return true;
    }

    /**
     * �������
     * @return boolean
     */
    public boolean addCtz() {

        String ctzCode = getValueString("CTZ_CODE").trim();
        //�����ݴ���
        if (!checkCtzCode(ctzCode))
            return false;
        //����������
        String ctzDesc = getValueString("CTZ_DESC").trim();
        if (!checkCtzDesc(ctzDesc))
            return false;
        //        //�����λ����Ϊ��
        //        String companyCode = this.getValueString("NHI_COMPANY_CODE");
        //        if (!checkCompanyCode(companyCode))
        //            return false;
        //�õ������
        int seq = this.getValueInt("SEQ");
        if (seq <= 0)
            seq = getMaxSeq(tableCtz.getDataStore(), "SEQ");
        //�õ�����ӵ�table�����к�
        int row = tableCtz.addRow();
        //���õ�ǰѡ����Ϊ��ӵ���
        tableCtz.setSelectedRow(row);
        //��ݴ���
        tableCtz.setItem(row, "CTZ_CODE", ctzCode);
        //�������
        tableCtz.setItem(row, "CTZ_DESC", ctzDesc);
        tableCtz.setItem(row, "PY1", getValueString("PY1"));
        //������
        tableCtz.setItem(row, "SEQ", seq);
        tableCtz.setItem(row, "NHI_COMPANY_CODE", this.getValue("NHI_COMPANY_CODE"));
        tableCtz.setItem(row, "NHI_NO", this.getValue("NHI_NO"));//ҽ������
        tableCtz.setItem(row, "DESCRIPT", this.getValue("DESCRIPT"));
        tableCtz.setItem(row, "MAIN_CTZ_FLG", this.getValue("MAIN_CTZ_FLG"));
        tableCtz.setItem(row, "NHI_CTZ_FLG", this.getValue("NHI_CTZ_FLG"));
        tableCtz.setItem(row, "MRCTZ_UPD_FLG", this.getValue("MRCTZ_UPD_FLG"));
        /****************************������ҳ������� shiblmodify20120105*******************************************/
        tableCtz.setItem(row, "MRO_CTZ", this.getValue("MRO_CTZ"));
        tableCtz.getDataStore().showDebug();

        for (int i = 0; i < allCode.getCount(); i++) {
            String hospChargeCode = allCode.getValue("CHARGE_HOSP_CODE", i);
            //�õ�����ӵ�table�����к�
            row = tableCharge.addRow();
            //���õ�ǰѡ����Ϊ��ӵ���
            tableCharge.setSelectedRow(row);
            //Ĭ�����
            tableCharge.setItem(row, "CTZ_CODE", ctzCode);
            //Ĭ��Ժ�ڷ���
            tableCharge.setItem(row, "CHARGE_HOSP_CODE", hospChargeCode);
            //Ĭ���ۿ�
            tableCharge.setItem(row, "DISCOUNT_RATE", 1.0);
        }
        initCombox();
        return true;
    }

    /**
     * �����ݲ���Ϊ��,Ҳ�����ظ�
     * @param ctzCode String
     * @return boolean
     */
    public boolean checkCtzCode(String ctzCode) {
        //����Ϊ��
        if (ctzCode.equals("") || ctzCode == null) {
            messageBox_("���벻��Ϊ��!");
            return false;
        }
        //�����ظ�
        if (tableCtz.getDataStore().exist("CTZ_CODE='" + ctzCode +
                                          "'")) {
            messageBox_("���" + ctzCode + "�ظ�!");
            return false;
        }
        return true;
    }

    /**
     * ����������
     * @param ctzDesc String
     * @return boolean
     */

    public boolean checkCtzDesc(String ctzDesc) {
        if (ctzDesc == null || ctzDesc.trim().length() == 0) {
            messageBox_("������Ʋ���Ϊ��!");
            return false;
        }
        return true;
    }

    /**
     * �����λ
     * @param companyCode String
     * @return boolean
     */
    public boolean checkCompanyCode(String companyCode) {
        if (companyCode == null || companyCode.length() == 0) {
            messageBox_("��λ����Ϊ��!");
            return false;
        }
        return true;
    }

    /**
     * ���������������
     * @return boolean
     */
    public boolean newCtz() {
        int selectedRow = tableCtz.getSelectedRow();
        //�����ѡ�����Ǹ������
        if (selectedRow < 0) {
            //���ûѡ������������������
            return addCtz();
        }
        return updateCtz(selectedRow);
    }


    /**
     * ɾ������
     */
    public void onDelete() {
        //�����ı�
        if (tableNow != null)
            tableNow.acceptText();
        int row = tableCtz.getSelectedRow();
        //���û��ѡ����
        if (row < 0) {
            messageBox("��ѡ�����!");
            return;
        }
        else {
            switch (messageBox("��ʾ��Ϣ", "ȷ��ɾ��", this.YES_NO_OPTION)) {
                //����
                case 0:
                    break;
                    //������
                case 1:
                    return;
            }

        }
        //ɾ���ۿ۱����ķ���
        deleteCtz(row);
        initCombox();
        this.onClear();
    }

    /**
     * ɾ���ۿ�
     * @param row int
     */
    public void deleteCtz(int row) {
        //�õ���ǰɾ�������
        String code = tableCtz.getItemString(row, 0);
        TDataStore dataStoreHospCharge = tableCharge.getDataStore();
        if (dataStoreHospCharge.isFilter()) {
            tableCharge.setFilter("");
            tableCharge.filter();
        }
        int countHospCharge = dataStoreHospCharge.rowCount();
        for (int i = countHospCharge - 1; i >= 0; i--) {
            //�õ���ݴ���
            String ctzCode = dataStoreHospCharge.getItemString(i, "CTZ_CODE");
            //����Ǳ����
            if (ctzCode.equals(code)) {
                dataStoreHospCharge.deleteRow(i);
            }
        }
        //ɾ��table�ϵĵ�ǰ��
        tableCtz.removeRow(row);
        //��������
        //this.saveData();
        TDataStore dataStoreCharge = tableCtz.getDataStore();
        String[] sql = dataStoreCharge.getUpdateSQL();
        TParm parm = new TParm();
        parm.setData("SQL_M", sql);
        parm.setData("CTZ_CODE", this.getValue("CTZ_CODE"));
        TParm result = TIOM_AppServer.executeAction("action.sys.SYSCTZAction",
                                                    "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        this.messageBox("ɾ���ɹ�");
    }

    /**
     * �����������
     * @return boolean
     */
    public boolean onUpdate() {
        //���ĵ�����
        if (!newCtz())
            return false;
        return saveData();

    }

    /**
     * ��������
     * @return boolean
     */
    public boolean saveData() {
        Timestamp date = SystemTool.getInstance().getDate();
        //�����ı�
        tableCharge.acceptText();
        tableCharge.setFilter("");
        tableCharge.filter();
        TDataStore dataStoreCharge = tableCharge.getDataStore();
        String name = dataStoreCharge.PRIMARY;
        //���ȫ���Ķ����к�
        int rowsCtz[] = dataStoreCharge.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowsCtz.length; i++) {
            dataStoreCharge.setItem(rowsCtz[i], "OPT_USER", Operator.getID());
            dataStoreCharge.setItem(rowsCtz[i], "OPT_DATE", date);
            dataStoreCharge.setItem(rowsCtz[i], "OPT_TERM", Operator.getIP());
        }

        //dataStoreCharge.showDebug();
        String[] hopCharge = dataStoreCharge.getUpdateSQL();

        //�����ı�
        tableCtz.acceptText();
        tableCtz.setFilter("");
        tableCtz.filter();
        TDataStore dataStore = tableCtz.getDataStore();
        name = dataStore.PRIMARY;
        //���ȫ���Ķ����к�
        int rowsHosp[] = dataStore.getModifiedRows(name);
        //���̶�����������
        for (int i = 0; i < rowsHosp.length; i++) {
            dataStore.setItem(rowsHosp[i], "OPT_USER", Operator.getID());
            dataStore.setItem(rowsHosp[i], "OPT_DATE", date);
            dataStore.setItem(rowsHosp[i], "OPT_TERM", Operator.getIP());
        }

        //dataStore.showDebug();
        dataStore.setItem(row, "CTZ_DESC", this.getValue("CTZ_DESC"));
        dataStore.setItem(row, "SEQ", this.getValue("SEQ"));
        dataStore.setItem(row, "NHI_COMPANY_CODE", this.getValue("NHI_COMPANY_CODE"));
        dataStore.setItem(row, "NHI_NO", this.getValue("NHI_NO"));//ҽ������==pangb 2012-2-7
        dataStore.setItem(row, "DESCRIPT", this.getValue("DESCRIPT"));
        dataStore.setItem(row, "MAIN_CTZ_FLG", this.getValue("MAIN_CTZ_FLG"));
        dataStore.setItem(row, "NHI_CTZ_FLG", this.getValue("NHI_CTZ_FLG"));
        dataStore.setItem(row, "MRCTZ_UPD_FLG", this.getValue("MRCTZ_UPD_FLG"));
        //
        dataStore.setItem(row, "MRO_CTZ", this.getValue("MRO_CTZ"));

        //dataStore.showDebug();
        String[] ctz = dataStore.getUpdateSQL();
        ctz = StringTool.copyArray(ctz, hopCharge);
        //�õ�����sql�Ķ���
        TJDODBTool dbTool = new TJDODBTool();
        //��������
        TParm result = new TParm(dbTool.update(ctz));
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        afterSave();
        return true;

    }

    /**
     * �������պۼ�
     */
    public void afterSave() {
        tableCtz.resetModify();
        tableCharge.resetModify();
        clearText();
        clearQuery();
        this.setTextEnabled(true);
        row = -1;
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
     * ����Ƿ������ݱ��
     * @return boolean
     */
    public boolean CheckChange() {
        //������
        TTable tableBed = (TTable) callFunction("UI|TABLECTZ|getThis");
        if (tableBed.isModified())
            return true;
        //����շ�
        TTable tableStation = (TTable) callFunction(
            "UI|TABLEHOSPCHARGEDETIAL|getThis");
        if (tableStation.isModified())
            return true;
        return false;
    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
//      Operator.setData("admin", "HIS", "127.0.0.1", "C00101");
    }

}
