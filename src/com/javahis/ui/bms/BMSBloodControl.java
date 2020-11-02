package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.jdo.TDataStore;
import jdo.bms.BMSSQL;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer; 

/**
 * <p>
 * Title: ѪƷ���
 * </p>
 *
 * <p>
 * Description: ѪƷ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.12.02
 * @version 1.0
 */
public class BMSBloodControl
    extends TControl {

    private TTable table_m;

    private TTable table_d;

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String code = getValueString("BLD_CODE");
        String filterString = "";
        if (code.length() > 0)
            filterString += "BLD_CODE like '" + code + "%'";
        table_m.setFilter(filterString);
        table_m.filter();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ��ջ�������
        String clearString =
            "BLD_CODE;BLDCODE_DESC;PY1;PY2;DESCRIPTION;FRONTPG_TYPE;UNIT_CODE;"
            + "VALUE_DAYS;BLD_CODE;SUBCAT_CODE;SUBCAT_DESC;BLD_VOL;UNIT_CODE_2";
        clearValue(clearString);
        table_m.setSelectionMode(0);
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        getTextField("BLD_CODE").setEnabled(true);
        getTextField("SUBCAT_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();

        Timestamp date = SystemTool.getInstance().getDate();
        if (row_m < 0 && row_d < 0) {
            // ����ѪƷ
            if (!checkDataM()) {
                return;
            }
            int row = table_m.addRow();
            table_m.getDataStore().setItem(row, "BLD_CODE",
                                           this.getValueString("BLD_CODE"));
            table_m.getDataStore().setItem(row, "BLDCODE_DESC",
                                           this.getValueString("BLDCODE_DESC"));
            table_m.getDataStore().setItem(row, "PY1",
                                           this.getValueString("PY1"));
            table_m.getDataStore().setItem(row, "PY2",
                                           this.getValueString("PY2"));
            table_m.getDataStore().setItem(row, "DESCRIPTION",
                                           this.getValueString("DESCRIPTION"));
            table_m.getDataStore().setItem(row, "FRONTPG_TYPE",
                                           this.getValueString("FRONTPG_TYPE"));
            table_m.getDataStore().setItem(row, "UNIT_CODE",
                                           this.getValueString("UNIT_CODE"));
            table_m.getDataStore().setItem(row, "VALUE_DAYS",
                                           this.getValueDouble("VALUE_DAYS"));
            table_m.getDataStore().setItem(row, "OPT_USER", Operator.getID());
            table_m.getDataStore().setItem(row, "OPT_DATE", date);
            table_m.getDataStore().setItem(row, "OPT_TERM", Operator.getIP());
            if (table_m.getDataStore().isModified()) {
                table_m.acceptText();
                if (!table_m.update()) {
                    messageBox("E0001");
                    table_m.removeRow(row);
                    table_m.setDSValue();
                    onClear();
                    return;
                }
                table_m.setDSValue();
            }
            messageBox("P0001");
            table_m.setDSValue();
            onClear();
        }
        else if (row_m >= 0 && row_d < 0) {
            // ����ѪƷ��������ѪƷ���
            if (!checkDataM()) {
                return;
            }
            if (!checkDataD(true)) {
                return;
            }
            TParm parm = new TParm();
            table_m.getDataStore().setItem(row_m, "BLD_CODE",
                                           this.getValueString("BLD_CODE"));
            table_m.getDataStore().setItem(row_m, "BLDCODE_DESC",
                                           this.getValueString("BLDCODE_DESC"));
            table_m.getDataStore().setItem(row_m, "PY1",
                                           this.getValueString("PY1"));
            table_m.getDataStore().setItem(row_m, "PY2",
                                           this.getValueString("PY2"));
            table_m.getDataStore().setItem(row_m, "DESCRIPTION",
                                           this.getValueString("DESCRIPTION"));
            table_m.getDataStore().setItem(row_m, "FRONTPG_TYPE",
                                           this.getValueString("FRONTPG_TYPE"));
            table_m.getDataStore().setItem(row_m, "UNIT_CODE",
                                           this.getValueString("UNIT_CODE"));
            table_m.getDataStore().setItem(row_m, "VALUE_DAYS",
                                           this.getValueDouble("VALUE_DAYS"));
            table_m.getDataStore().setItem(row_m, "OPT_USER", Operator.getID());
            table_m.getDataStore().setItem(row_m, "OPT_DATE", date);
            table_m.getDataStore().setItem(row_m, "OPT_TERM", Operator.getIP());
            parm.setData("TABLE_M", table_m.getDataStore().getUpdateSQL());
            if (!"".equals(getValueString("SUBCAT_CODE"))
                || !"".equals(getValueString("SUBCAT_DESC"))) {
                int row = table_d.addRow();
                table_d.getDataStore().setItem(row, "BLD_CODE",
                                               this.getValueString("BLD_CODE"));
                table_d.getDataStore().setItem(row, "SUBCAT_CODE",
                                               this.
                                               getValueString("SUBCAT_CODE"));
                table_d.getDataStore().setItem(row, "SUBCAT_DESC",
                                               this.
                                               getValueString("SUBCAT_DESC"));
                table_d.getDataStore().setItem(row, "BLD_VOL",
                                               this.getValueDouble("BLD_VOL"));
                table_d.getDataStore().setItem(row, "UNIT_CODE",
                                               this.
                                               getValueString("UNIT_CODE_2"));
                table_d.getDataStore().setItem(row, "OPT_USER", Operator.getID());
                table_d.getDataStore().setItem(row, "OPT_DATE", date);
                table_d.getDataStore().setItem(row, "OPT_TERM", Operator.getIP());
                parm.setData("TABLE_D", table_d.getDataStore().getUpdateSQL());
            }
            TParm result = TIOM_AppServer.executeAction(
                "action.bms.BMSBloodAction", "onSaveBldSubcat", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            table_m.setDSValue();
            table_d.setDSValue();
            this.messageBox("P0001");
        }
        else if (row_d >= 0) {
            // ����ѪƷ���
            if (!checkDataD(false)) {
                return;
            }
            
            TParm parm = new TParm();
            if (!"".equals(getValueString("SUBCAT_CODE"))
                    || !"".equals(getValueString("SUBCAT_DESC"))) {
                    int row = table_d.getSelectedRow();
                    table_d.getDataStore().setItem(row, "BLD_CODE",
                                                   this.getValueString("BLD_CODE"));
                    table_d.getDataStore().setItem(row, "SUBCAT_CODE",
                                                   this.
                                                   getValueString("SUBCAT_CODE"));
                    table_d.getDataStore().setItem(row, "SUBCAT_DESC",
                                                   this.
                                                   getValueString("SUBCAT_DESC"));
                    table_d.getDataStore().setItem(row, "BLD_VOL",
                                                   this.getValueDouble("BLD_VOL"));
                    table_d.getDataStore().setItem(row, "UNIT_CODE",
                                                   this.
                                                   getValueString("UNIT_CODE_2"));
                    table_d.getDataStore().setItem(row, "OPT_USER", Operator.getID());
                    table_d.getDataStore().setItem(row, "OPT_DATE", date);
                    table_d.getDataStore().setItem(row, "OPT_TERM", Operator.getIP());
                    parm.setData("TABLE_D", table_d.getDataStore().getUpdateSQL());
                    
                    TParm result = TIOM_AppServer.executeAction(
                            "action.bms.BMSBloodAction", "onUpdateBldSubcat", parm);
                    
                        if (result == null || result.getErrCode() < 0) {
                            this.messageBox("E0001");
                            return;
                        }
                        table_d.setDSValue();
                        this.messageBox("P0001");                    
                }            
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        if (row_m != -1) {
            //ɾ�������ϸ��
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ��ѪƷ", 2) == 0) {
                TParm parm = new TParm();
                table_d.removeRowAll();
                parm.setData("TABLE_D", table_d.getDataStore().getUpdateSQL());
                table_m.removeRow(row_m);
                parm.setData("TABLE_M", table_m.getDataStore().getUpdateSQL());
                TParm result = TIOM_AppServer.executeAction(
                    "action.bms.BMSBloodAction", "onDeleteBldSubcat", parm);
                if (result == null || result.getErrCode() < 0) {
                    messageBox("ɾ��ʧ��");
                    return;
                }
                table_m.setDSValue();
                table_d.setDSValue();
                messageBox("ɾ���ɹ�");
            }
        }
        else if (row_d != -1) {
            //ɾ��ϸ��
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ��ѪƷ���", 2) == 0) {
                table_d.removeRow(row_d);
                if (table_d.getDataStore().isModified()) {
                    table_d.acceptText();
                    if (!table_d.update()) {
                        messageBox("ɾ��ʧ��");
                        table_d.setDSValue();
                        onClear();
                        return;
                    }
                    table_d.setDSValue();
                }
                messageBox("ɾ���ɹ�");
            }
        }
    }

    /**
     * �����������
     * @return boolean
     */
    private boolean checkDataM() {
        if ("".equals(getValueString("BLD_CODE"))) {
            this.messageBox("ѪƷ���벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("BLDCODE_DESC"))) {
            this.messageBox("����˵������Ϊ��");
            return false;
        }
        if ("".equals(getValueString("UNIT_CODE"))) {
            this.messageBox("ѪƷ��λ����Ϊ��");
            return false;
        }
        if (getValueDouble("VALUE_DAYS") <= 0) {
            this.messageBox("���ʹ����������С�ڻ����O");
            return false;
        }
        return true;
    }

    /**
     * ���ϸ������
     * @return boolean
     */
    private boolean checkDataD(boolean flg) {
        if (flg) {
            if ("".equals(getValueString("SUBCAT_CODE"))
                && "".equals(getValueString("SUBCAT_DESC"))
                && "".equals(getValueString("UNIT_CODE_2"))) {
                return true;
            }
            if ("".equals(getValueString("SUBCAT_CODE"))) {
                this.messageBox("ѪƷ�����Ϊ��");
                return false;
            }
            if ("".equals(getValueString("SUBCAT_DESC"))) {
                this.messageBox("���˵������Ϊ��");
                return false;
            }
            if ("".equals(getValueString("UNIT_CODE_2"))) {
                this.messageBox("���λ����Ϊ��");
                return false;
            }
            if (getValueDouble("BLD_VOL") <= 0) {
                this.messageBox("���������С�ڻ����O");
                return false;
            }
            return true;
        }
        else {
            if ("".equals(getValueString("SUBCAT_CODE"))) {
                this.messageBox("ѪƷ�����Ϊ��");
                return false;
            }
            if ("".equals(getValueString("SUBCAT_DESC"))) {
                this.messageBox("���˵������Ϊ��");
                return false;
            }
            if ("".equals(getValueString("UNIT_CODE_2"))) {
                this.messageBox("��λ����Ϊ��");
                return false;
            }
            if (getValueDouble("BLD_VOL") <= 0) {
                this.messageBox("���������С�ڻ����O");
                return false;
            }
            return true;
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��Table
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        table_m.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(BMSSQL.getBMSBloodCode(""));
        dataStore.retrieve();
        table_m.setDataStore(dataStore);
        table_m.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * TABLE_M�����¼�
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            TParm parm = table_m.getDataStore().getRowParm(row);
            String likeNames =
                "BLD_CODE;BLDCODE_DESC;PY1;PY2;DESCRIPTION;FRONTPG_TYPE;"
                + "UNIT_CODE;VALUE_DAYS;SUBCAT_CODE;SUBCAT_DESC;BLD_VOL;UNIT_CODE";
            this.clearValue("UNIT_CODE_2") ;
            this.setValueForParm(likeNames, parm);
            getTextField("BLD_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table_d.setSelectionMode(0);
            table_d.removeRowAll();
            TDataStore dataStore = new TDataStore();
            dataStore.setSQL(BMSSQL.getBMSBldVol(parm.getValue("BLD_CODE")));
            dataStore.retrieve();
            table_d.setDataStore(dataStore);
            table_d.setDSValue();
        }
    }

    /**
     * TABLE_D�����¼�
     */
    public void onTableDClicked() {
        int row = table_d.getSelectedRow();
        if (row != -1) {
            TParm parm = table_d.getDataStore().getRowParm(row);
            String likeNames =
                "BLD_CODE;SUBCAT_CODE;SUBCAT_DESC;BLD_VOL";
            this.setValueForParm(likeNames, parm);
            String unitCode2 = parm.getValue("UNIT_CODE") ;
            this.setValue("UNIT_CODE_2", unitCode2) ;
            getTextField("SUBCAT_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table_m.setSelectionMode(0);
        }
    }

    /**
     * BldCodeDesc�س��¼�
     */
    public void onBldCodeDescAction() {
        String py = TMessage.getPy(this.getValueString("BLDCODE_DESC"));
        setValue("PY1", py);
        getTextField("PY1").grabFocus();
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

}
