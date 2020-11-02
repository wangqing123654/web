package com.javahis.ui.inv;

import com.dongyang.ui.event.TPopupMenuEvent;
import java.util.Map;
import com.dongyang.ui.TTable;
import java.util.HashMap;
import com.dongyang.data.TParm;
import jdo.inv.INV;
import com.dongyang.control.TControl;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import jdo.inv.INVOrgTool;
import com.dongyang.jdo.TDS;
import jdo.inv.INVPackStockDTool;
import jdo.inv.INVPublicTool;
import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;
import com.javahis.manager.INVPackOberver;

/**
 *
 * <p>Title:��������� </p>
 *
 * <p>Description: ���������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009--5-26
 * @version 1.0
 */
public class INVTearPackControl
    extends TControl {
    /**
     * ����
     */
    private TTable tableM;
    /**
     * ϸ��
     */
    private TTable tableD;
    /**
     * ������ķ���
     */
    private INV inv = new INV();
    /**
     * ��¼��������������
     */
    private TParm returnParm;
    /**
     * ��¼����������Ź���
     */
    private Map map = new HashMap();
    /**
     * �Ƿ�����ע��
     */
    private boolean isNew = false;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //���ʵ�������
        callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
                     "%ROOT%\\config\\inv\\INVChoose.x");
        //���ܻش�ֵ
        callFunction("UI|PACK_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //table���ɱ༭
        tableM = (TTable) getComponent("TABLEM");
        tableD = (TTable) getComponent("TABLED");
        //��ʼ��table
        initTable();
        //��ʼ�������ϵ�����
        initValue();

        initCombox();
        //��ӹ۲���
        observer();
    }

    /**
     * ��ӹ۲���
     */
    public void observer() {
        //��������ӹ۲���
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL("SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
        dataStore.retrieve();
        //��ϸ����ӹ۲���
        TDS tds = (TDS) tableD.getDataStore();
        tds.addObserver(new INVPackOberver());

        //��������ӹ۲���
        dataStore = new TDataStore();
        dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
        dataStore.retrieve();
        //��ϸ����ӹ۲���
        tds = (TDS) tableM.getDataStore();
        tds.addObserver(new INVPackTob(dataStore));

    }

    /**
     * ���ʿ���
     */
    public void initCombox() {
        TParm parm = INVOrgTool.getInstance().getDept();
        TComboBox comboBox = (TComboBox)this.getComponent("ORG_CODE");
        comboBox.setParmValue(parm);
        comboBox.updateUI();
        //Ĭ��������
        String toOrgCode = "";
        TParm toOrgParm = INVPublicTool.getInstance().getOrgCode("B");
        if (toOrgParm.getErrCode() >= 0) {
            toOrgCode = toOrgParm.getValue("ORG_CODE", 0);
            comboBox.setValue(toOrgCode);
        }

        TParm parmUser = INVPublicTool.getInstance().getInvOperator();
        comboBox = new TComboBox();
       comboBox.setParmMap("id:USER_ID;name:USER_NAME;py1:PY1");
       comboBox.setParmValue(parmUser);
       comboBox.setShowID(true);
       comboBox.setShowName(true);
       comboBox.setExpandWidth(30);
       comboBox.setTableShowList("name");
       tableM.addItem("OPERATOR", comboBox);
       tableD.addItem("OPERATOR", comboBox);
    }

    /**
     * ��ʼ��table
     */
    public void initTable() {
        //����
        retriveTable(tableM, INVSQL.getInitPackStockMSql());
        //ϸ��
        retriveTable(tableD, INVSQL.getInitPackStockDSql());
    }

    /**
     * ˢ��table
     * @param table TTable
     * @param sql String
     */
    public void retriveTable(TTable table, String sql) {
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * ��ʼ�������ϵ�����
     */
    public void initValue() {
        //״̬
        setValue("STATUS", "1");
    }

    /**
     * ��ѯPACK_CODE
     */
    public void onQuery() {
        //������
        String sql = INVSQL.getQueryStockMSql(getValueString("PACK_CODE"),
                                              getValueInt("PACK_SEQ_NO"));
        //ˢ��table
        retrieveTable(tableM, sql);
    }

    /**
     * ȡ�ý���������Ĳ�ѯ����
     * @return TParm
     */
    public TParm getQueryParm() {
        return this.getParmForTag("PACK_CODE;PACK_SEQ_NO;QTY;" +
                                  "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS");
    }

    /**
     * ����ĵ���¼�
     */
    public void onTableMClicked() {
        int row = tableM.getSelectedRow();
        if (row < 0)
            return;
        //�����Ϸ�
        setTextValue(tableM, row);
        //������ϸ
        String sql = INVSQL.getQueryStockDSql(getValueString("PACK_CODE"),
                                              getValueInt("PACK_SEQ_NO"));
        retrieveTable(tableD, sql);


    }

    /**
     * �����Ϸ�
     * @param table TTable
     * @param row int
     */
    public void setTextValue(TTable table, int row) {
        setValueForParm("PACK_CODE;PACK_SEQ_NO;QTY;" +
                        "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER;STATUS",
                        table.getDataStore().getRowParm(row));
    }

    /**
     * ˫���鿴��������ϸ
     */
    public void onTableMDoubleClick() {
        //����������ݱ��
        if (!this.checkValueChange())
            return;
        String sql = INVSQL.getQueryStockDSql(getValueString("PACK_CODE"),
                                              getValueInt("PACK_SEQ_NO"));
        retrieveTable(tableD, sql);
    }

    /**
     * ˢ��table
     * @param table TTable
     * @param sql String
     */
    public void retrieveTable(TTable table, String sql) {
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * ������ѡ�񷵻����ݴ���
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        if (obj == null)
            return;
        returnParm = (TParm) obj;
        //���ô��������ݵķ���
        onDealRetrunValue(returnParm);
        //��ѯ
        onQuery();
        //������
        int rowCount = tableM.getRowCount();
        if (rowCount == 0) {
            messageBox("������������!");
            return;
        }
        //�����Ϸ�
        setTextValue(tableM, 0);
    }

    /**
     * ����������ѡ�񷵻�����
     * @param parm TParm
     */
    public void onDealRetrunValue(TParm parm) {
        //����
        setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        //����
        setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        this.callFunction("UI|PACK_CODE|setEnabled", false);
    }

    /**
     * ɨ������
     */
    public void onScream() {
        String packCode = getValueString("SCREAM");
        if (packCode == null || packCode.length() == 0) {
            return;
        }
        setValue("PACK_CODE", packCode.substring(0, packCode.length() - 4));
        setValue("PACK_SEQ_NO",
                 packCode.substring( (packCode.length() - 4), packCode.length()));
        //���ҷָ����ֿ����ַ���
        this.onQuery();
    }


    /**
     * ���
     */
    public void onClear() {
        //������ݱ��
        if (!checkValueChange())
            return;
        //�������
        onClearText();
        //��ղ�������
        clearNoEnoughInv();
        //�ɱ༭״̬
        clearEnable(true);
        //��ʼ�������ϵ�����
        initValue();
    }

    /**
     * ���û��ѡ���㹻������
     */
    public void clearNoEnoughInv() {
        //�������
        onClearTable(tableM);
        //���ϸ��
        onClearTable(tableD);
        //�������״̬
        isNew = false;
    }

    /**
     * �ı�༭״̬
     * @param statuse boolean
     */
    public void clearEnable(boolean statuse) {
        //����
        this.callFunction("UI|ORG_CODE|setEnabled", statuse);
        //����
        this.callFunction("UI|PACK_CODE|setEnabled", statuse);
        //���
        this.callFunction("UI|PACK_SEQ_NO|setEnabled", statuse);
    }

    /**
     * ��ս���
     */
    public void onClearText() {
        //��չ�������
        clearValue("PACK_CODE;PACK_DESC;PACK_SEQ_NO;QTY;" +
                   "DISINFECTION_DATE;VALUE_DATE;DISINFECTION_USER");
    }

    /**
     * ���table
     * @param table TTable
     */
    public void onClearTable(TTable table) {
        //�������˸���
        table.acceptText();
        //���ѡ����
        table.clearSelection();
        //ɾ��������
        table.removeRowAll();
        //����޸ļ�¼
        table.resetModify();
    }

    /**
     * �������
     * @return boolean
     */
    public boolean onUpdate() {
        String orgCode = this.getValueString("ORG_CODE");
        if (orgCode == null || orgCode.length() == 0) {
            messageBox_("��ѡ�����!");
            return false;
        }
        int row = tableM.getSelectedRow();
        if (row < 0) {
            messageBox_("��ѡ��Ҫ���������!");
            return false;
        }
        TParm saveParm = getSaveParm(row);
        System.out.println("saveParm="+saveParm);
        if (saveParm == null)
            return false;
        //�ȴ�ӡ���ⵥ
        onPrint();
        //���ñ�������
        TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
            "saveTearPack", saveParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            //����ʧ��
            messageBox_("����ʧ��!");
            return false;
        }
        //����ɹ�
        messageBox_("����ɹ�!");
        //�������������
        resertSave(row);
        return true;
    }

    /**
     * ����������������
     * @param row int
     */
    public void resertSave(int row) {
        tableM.removeRow(row);
        //����������¼
        tableM.resetModify();
        //���ϸ�����¼
        tableD.removeRowAll();
        tableD.resetModify();
    }


    /**
     * ��ñ���parm
     * @param row int
     * @return TParm
     */
    public TParm getSaveParm(int row) {
        //�����
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        if (packSeqNo == 0) {
            messageBox("�������������ɲ��!");
            return null;
        }
        String status = tableM.getItemString(row, "STATUS");
        if (status.equals("1")) {
            messageBox("���������ڿ�!");
            return null;
        }
        //����
        String packCode = tableM.getItemString(row, "PACK_CODE");
        TParm result = INVPackStockDTool.getInstance().getPackDetial(packCode,
            packSeqNo);
        if (result.getErrCode() < 0) {
            messageBox("������ϸʧ��!");
            return null;
        }
        //Ҫ���������
        TParm saveParm = new TParm();
        //��Ź����
        saveParm.setData("STOCKDD", result.getData());
        //�������
        TParm stockMParm = this.getStockMParm(result);
        if (stockMParm == null)
            return null;
        saveParm.setData("STOCKM", stockMParm.getData());
        //�����ϸ��
        TParm stockDParm = this.getStockDParm(result);
        if (stockDParm == null)
            return null;
        saveParm.setData("STOCKD", stockDParm.getData());
        //��������浵
        TParm packStock = new TParm();
        packStock.setData("PACK_CODE", packCode);
        packStock.setData("PACK_SEQ_NO", packSeqNo);
        saveParm.setData("PACKSTOCK", packStock.getData());

        return saveParm;

    }

    /**
     * �õ����¿�����������ݰ�
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockMParm(TParm parm) {
        TParm stockMparm = new TParm();
        int rowCount = parm.getCount();
        Map invMap = new HashMap();

        TParm oneRow;
        for (int i = 0; i < rowCount; i++) {
            oneRow = parm.getRow(i);
            //���ʴ���
            String invCode = oneRow.getValue("INV_CODE");
            //���Ϊ��
            if (invMap.get(invCode) == null ||
                invMap.get(invCode).toString().length() == 0) {
                int row = stockMparm.insertRow();
                //��¼�洢�����ʴ���
                invMap.put(invCode, row + "");
                //����
                oneRow.setData("ORG_CODE", getValue("ORG_CODE"));
                //�����
                oneRow.setData("STOCK_QTY", oneRow.getData("QTY"));
                stockMparm.setRowData(row, oneRow);
                stockMparm.setCount(row + 1);
            } else {
                int row =  Integer.valueOf (invMap.get(invCode).toString());
                //��������
                stockMparm.setData("STOCK_QTY", row,(stockMparm.getDouble("STOCK_QTY", row) +
                                   oneRow.getDouble("QTY")));
            }
        }
        return stockMparm;
    }

    /**
     * �õ����¿�����������ݰ�
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockDParm(TParm parm) {
        TParm stockDparm = new TParm();
        int rowCount = parm.getCount();
        Map invMap = new HashMap();
        TParm oneRow;
        for (int i = 0; i < rowCount; i++) {
            oneRow = parm.getRow(i);
            String invCode = oneRow.getValue("INV_CODE");
            int bacchSeq = oneRow.getInt("BATCH_SEQ");
            //������ź����ʴ��빲ͬȷ��
            if (invMap.get(invCode + "|" + bacchSeq) == null) {
                int row = stockDparm.insertRow();
                //��¼�洢�����ʴ���
                invMap.put(invCode + "|" + bacchSeq, row);
                //����
                oneRow.setData("ORG_CODE", getValue("ORG_CODE"));
                //�����
                oneRow.setData("STOCK_QTY", oneRow.getData("QTY"));
                stockDparm.setRowData(row, oneRow);
                stockDparm.setCount(row + 1);
            }
            else {
                int row = Integer.parseInt(invMap.get(invCode + "|" + bacchSeq).
                                           toString());
                //��������
                stockDparm.setData("STOCK_QTY", row,
                                   stockDparm.getDouble("STOCK_QTY", row) +
                                   oneRow.getDouble("QTY"));
            }
        }
        return stockDparm;
    }

    /**
     * ����������Ƿ�����Ź���
     * @return boolean
     */
    public boolean onCheckSeqFlg() {
        String seqManFlg = returnParm.getValue("SEQ_FLG");
        if (seqManFlg == null || seqManFlg.length() == 0 ||
            seqManFlg.equals("0"))
            return false;
        return true;
    }


    /**
     * ������ݱ����ʾ��Ϣ�������ϸ��ʱ��ʾ���ݵı����
     * @return boolean
     */
    public boolean checkValueChange() {
        // ��������ݱ��
        if (checkData())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "���ݱ���Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!onUpdate())
                        return false;
                    return true;
                    //������
                case 1:
                    return true;
                    //����
                case 2:
                    return false;
            }
        return true;
    }

    /**
     * ������ݱ��
     * @return boolean
     */
    public boolean checkData() {
        //����
        if (tableM.isModified())
            return true;
        //ϸ��
        if (tableD.isModified())
            return true;
        return false;
    }

    /**
     * �Ƿ�رմ���(�������ݱ��ʱ��ʾ�Ƿ񱣴�)
     * @return boolean true �ر� false ���ر�
     */
    public boolean onClosing() {
        // ��������ݱ��
        if (checkData())
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
                //����
                case 0:
                    if (!onUpdate())
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
     * ��ӡ�����ⵥ
     */
    public void onPrint() {
        int row =tableM.getSelectedRow();
        if(row<0){
            messageBox_("��ѡ��������");
            return;
        }

        String packCode = tableM.getItemString(row, "PACK_CODE");
        int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
        //����
        TComboBox value;
        value = (TComboBox)this.getComponent("ORG_CODE");
        String orgDesc = value.getSelectedName();
        TParm parm = new TParm();
        parm.setData("PACK_CODE", packCode);
        parm.setData("PACK_SEQ_NO", packSeqNo);
        parm.setData("ORG_DESC", orgDesc);
        parm.setData("HOSP_AREA",getHospArea());
        String sql = INVSQL.getTearPackPrintSql(packCode, packSeqNo);
        parm.setData("T1", "SQL", sql);
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVTearPack.jhw", parm);
    }
    /**
     * �õ�ҽԺ���
     * @return String
     */
    public String getHospArea(){
        String hospChnAbn=Operator.getHospitalCHNShortName();
        return hospChnAbn;
    }

}
