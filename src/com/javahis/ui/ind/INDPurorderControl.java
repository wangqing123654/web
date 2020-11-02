package com.javahis.ui.ind;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndPurorderDTool;
import jdo.ind.IndStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.dom4j.Document;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndPurOrderObserver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��������Control
 * </p>
 *
 * <p>
 * Description: ��������Control
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
 * @author zhangy 2009.05.12
 * @version 1.0
 */

public class INDPurorderControl
    extends TControl {

    /**
     * ������ǣ�1.insertM�����������ƻ������� 2.updateM�����¶����ƻ�������
     */
    private String action = "insertM";

    // ϸ�����
    private int seq;

    // ����Ȩ��
    private boolean gift_flg = true;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    public INDPurorderControl() {
        super();
    }
    //��ʽ��������
    java.text.DecimalFormat dfInt = new java.text.DecimalFormat("##########0");
    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ���������
        TParm parm = new TParm();
        if (!"".equals(getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        }
        else {
            // û��ȫ������Ȩ��
            if (!dept_flg) {
                this.messageBox("��ѡ���ѯ����");
                return;
            }
        }
        if (!"".equals(getValueString("SUP_CODE"))) {
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        if (!"".equals(getValueString("PURORDER_NO"))) {
            parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
        }
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        if (parm == null) {
            return;
        }
        // ���ؽ����
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.ind.INDPurorderAction",
                                              "onQueryM", parm);

        // ������Դ
        if (getRadioButton("STATIC_NO_B").isSelected()) {
            // �ֹ�����
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if (!"".equals(result.getValue("PLAN_NO", i))) {
                    result.removeRow(i);
                }
            }
        }
        else if (getRadioButton("STATIC_NO_C").isSelected()) {
            // �ɹ��ƻ�
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if ("".equals(result.getValue("PLAN_NO", i))) {
                    result.removeRow(i);
                }
            }
        }
        // ��ѯϸ���״̬
        String update = "0";
        boolean flg = false;
        TDataStore ds = new TDataStore();
        for (int i = result.getCount() - 1; i >= 0; i--) {
            ds.setSQL(INDSQL
                      .getPurOrderDByNo(result.getValue("PURORDER_NO", i)));
            ds.retrieve();
            if (ds.rowCount() == 0) {
                flg = false;
            }
            else {
                flg = true;
                for (int j = 0; j < ds.rowCount(); j++) {
                    update = ds.getItemString(j, "UPDATE_FLG");
                    if ("0".equals(update) || "1".equals(update)) {
                        // δ���
                        flg = false;
                        break;
                    }
                }
            }
            // ����״̬
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                // δ���
                if (flg) {
                    result.removeRow(i);
                }
            }
            else {
                // ���
                if (!flg) {
                    result.removeRow(i);
                }
            }
        }

        if (result == null || result.getCount("PURORDER_NO") == 0 ||
            result.getCount() <= 0) {
            getTable("TABLE_M").removeRowAll();
            getTable("TABLE_D").removeRowAll();
            this.messageBox("û�в�ѯ����");
            return;
        }

        // �����TABLE_M
        TTable table_M = getTable("TABLE_M");
        table_M.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("STATIC_NO_A").setSelected(true);
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;PURORDER_DATE;ORG_CODE;PURORDER_NO;"
            + "RES_DELIVERY_DATE;PLAN_NO;REASON_CODE;DESCRIPTION";
        this.clearValue(clearString);
        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("PURORDER_DATE", date);
        // ����״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        getTextField("PURORDER_NO").setEnabled(true);
        getTextFormat("SUP_CODE").setEnabled(true);
        getTable("TABLE_M").setSelectionMode(0);
        getTable("TABLE_D").setSelectionMode(0);
        action = "insertM";
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        if ("insertM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            parm.setData("PURORDER_DATE", getValue("PURORDER_DATE"));
            String purorder_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_PURORDER", "No");
            //System.out.println("Purorder_No:" + purorder_no);
            parm.setData("PURORDER_NO", purorder_no);
            parm.setData("ORG_CODE", getValue("ORG_CODE"));
            parm.setData("SUP_CODE", getValue("SUP_CODE"));
            if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
                parm.setData("RES_DELIVERY_DATE", getValue("RES_DELIVERY_DATE"));
            }
            else {
                parm.setData("RES_DELIVERY_DATE", tnull);
            }
            parm.setData("REASON_CHN_DESC", getValue("REASON_CODE"));
            parm.setData("DESCRIPTION", getValue("DESCRIPTION"));
            //System.out.println("PLAN_NO" + getValueString("PLAN_NO"));
            parm.setData("PLAN_NO", getValueString("PLAN_NO"));
            //zhangyong20110517
            parm.setData("REGION_CODE", Operator.getRegion());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            // ִ����������
            result = TIOM_AppServer.executeAction(
                "action.ind.INDPurorderAction", "onInsertM", parm);
        }
        else if ("updateM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // ����ֵȡ��
            parm.setData("PURORDER_DATE", getValue("PURORDER_DATE"));
            parm.setData("ORG_CODE", getValue("ORG_CODE"));
            parm.setData("SUP_CODE", getValue("SUP_CODE"));
            if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
                parm.setData("RES_DELIVERY_DATE", getValue("RES_DELIVERY_DATE"));
            }
            else {
                parm.setData("RES_DELIVERY_DATE", tnull);
            }
            parm.setData("REASON_CHN_DESC", getValue("REASON_CODE"));
            parm.setData("DESCRIPTION", getValue("DESCRIPTION"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("PURORDER_NO", getValue("PURORDER_NO"));
            // ִ�����ݸ���
            result = TIOM_AppServer.executeAction(
                "action.ind.INDPurorderAction", "onUpdateM", parm);
        }
        else {
            // ϸ����
            if (!CheckDataD()) {
                return;
            }

            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // ���ȫ����������
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);

            // ���̶�����������
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "PURORDER_NO",
                                  getValueString("PURORDER_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
            }
            TParm inParm = new TParm();
            if ("insert".equals(action)) {
                inParm.setData("PLAN_NO", this.getValueString("PLAN_NO"));
                inParm.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
            }
            TParm getTRA = new TParm();
            TParm getQTY = new TParm();
            String order_code = "";
            String org_code = getValueString("ORG_CODE");
            double s_qty = 0;
            double d_qty = 0;
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
                order_code = dataStore.getItemString(rows[i], "ORDER_CODE");
                inParm.setData("ORDER_CODE", i, order_code);
                inParm.setData("PURORDER_QTY", i, dataStore.getItemDouble(
                    rows[i], "PURORDER_QTY"));
                // ���������ת����
                getTRA = INDTool.getInstance().getTransunitByCode(order_code);
                if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                    this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
                    return;
                }
                s_qty = getTRA.getDouble("STOCK_QTY", 0);
                inParm.setData("STOCK_QTY", i, s_qty);
                d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                inParm.setData("DOSAGE_QTY", i, d_qty);
                // ��������趨�ж�
                TParm qtyParm = new TParm();
                qtyParm.setData("ORG_CODE", org_code);
                qtyParm.setData("ORDER_CODE", order_code);
                //System.out.println("ORG_CODE" + org_code);
                //System.out.println("ORDER_CODE" + order_code);
                getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
                //System.out.println("getQTY" + getQTY);
                if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
                    this.messageBox("ҩƷ" + order_code + "δ�趨���������Ϣ");
                    return;
                }
            }
            inParm.setData("ORG_CODE", org_code);
            inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            result = TIOM_AppServer.executeAction(
                "action.ind.INDPurorderAction", "onSavePurOrder", inParm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            // ��ӡ����
            onPrint();
            return;
        }

        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        onClear();
        setValue("PURORDER_NO", parm.getValue("PURORDER_NO"));
        setValue("ORG_CODE", parm.getValue("ORG_CODE"));
        setValue("SUP_CODE", parm.getValue("SUP_CODE"));
        setValue("REASON_CODE", parm.getValue("REASON_CHN_DESC"));
        setValue("DESCRIPTION", parm.getValue("DESCRIPTION"));
        if (!"".equals(parm.getValue("RES_DELIVERY_DATE")) ||
            parm.getTimestamp("RES_DELIVERY_DATE").equals(tnull)) {
            setValue("RES_DELIVERY_DATE", parm.getValue("RES_DELIVERY_DATE"));
        }
        this.messageBox("P0001");
        onQuery();
    }

    /**
     * �򿪲ɽ���
     */
    public void onExport() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ�񶩹�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        Object result = openDialog("%ROOT%\\config\\ind\\INDPurAdvice.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            //System.out.println("addParm:" + addParm);
            TTable table = getTable("TABLE_D");
            // ��Ӧ����
            getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
            // �ƻ�����
            this.setValue("PLAN_NO", addParm.getValue("PLAN_NO", 0));

            table.removeRow(0);
            for (int i = 0; i < addParm.getCount(); i++) {
                int row = table.addRow();
                // ���DATESTORE
                // ORDER_CODE
                table.getDataStore().setItem(i, "ORDER_CODE",
                                             addParm.getValue("ORDER_CODE", i));
                // ������λ
                String unit = addParm.getValue("PURCH_UNIT", i);
                table.setItem(i, "BILL_UNIT", unit);
                // ������
                table.setItem(i, "PURORDER_QTY", addParm.getValue("CHECK_QTY",
                    i));
                // ������
                table.setItem(i, "GIFT_QTY", 0);
                // ��������
                table.setItem(i, "PURORDER_PRICE", addParm.getValue(
                    "STOCK_PRICE", i));
                // �������
                table.setItem(i, "TOT_MONEY", addParm.getValue("ATM", i));
                table.setItem(i, "ACTUAL_QTY", 0);
                table.setItem(i, "QUALITY_DEDUCT_AMT", 0);
            }
            table.setDSValue();
            //getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            action = "insert";
            // �����ܼ�Ǯ
            this.setValue("SUM_MONEY", getSumMoney());
            //zhangyong20110517
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
    }
    
    /**
     * �����ɹ���ΪXML�ļ�
     */
    public void onExportXml() {
    	if (getTable("TABLE_M").getSelectedRow() > -1) {
    		
    		//��������
    		String pur_order = getValueString("PURORDER_NO");
    		
    		//�õ���Ӧ�̴���
    		String  supCode = getValueString("SUP_CODE");
    		
    		//Ҫ��������ϸ������
    	    TParm  parm =   new TParm(TJDODBTool.getInstance().select(INDSQL.getPurOrderDSqlByNo(pur_order)));
    		List list = new ArrayList();
    		
    		TParm  xmlParm = new TParm();
    		for(int i = 0 ; i < parm.getCount() ; i++ ){
    			TParm t = (TParm)parm.getRow(i);
//    			System.out.println("Parm:"+parm);
    			Map<String, String> map = new LinkedHashMap();
    			map.put("warehouse", "K932290201");
        		map.put("deliverycode", "932290201");
        		map.put("cstcode", t.getValue("SUP_CODE"));
        		map.put("goods", t.getValue("ORDER_CODE"));
        		map.put("goodname", t.getValue("ORDER_DESC"));
        		map.put("spec", t.getValue("SPECIFICATION"));
        		map.put("msunitno", t.getValue("UNIT_CHN_DESC"));
        		map.put("producer", t.getValue("MAN_CODE"));
        		map.put("billqty", t.getValue("PURORDER_QTY"));
        		map.put("prc", t.getValue("PURORDER_PRICE"));
        		
        		//��������
        		String  purorder_no = t.getValue("PURORDER_NO");
        		String seq = t.getValue("SEQ_NO");
        		map.put("purchaseid", purorder_no+"-"+seq);
        		list.add(map);
    		}
    		Document doc = ExportXmlUtil.createXml(list);
    		ExportXmlUtil.exeSaveXml(doc, pur_order+".xml");
    		
    	}else{
    		this.messageBox("��ѡ��Ҫ���ɵĲɹ���!");
    		return ;
    	}
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            this.messageBox("��������ɲ���ɾ��");
        }
        else {
            if (getTable("TABLE_M").getSelectedRow() > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    table_D.removeRowAll();
                    TParm parm = new TParm();
                    String pur_order = getValueString("PURORDER_NO");
                    parm.setData("PURORDER_NO", pur_order);
                    String org_code = getValueString("ORG_CODE");
                    parm.setData("ORG_CODE", org_code);
                    // ��ѯ����ϸ��
                    TParm result = new TParm();
                    result = IndPurorderDTool.getInstance().onQuery(parm);
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    if (result.getCount() > 0) {
                        // ɾ�����ϸ��
                        String order_code = "";
                        parm.setData("DELETE_SQL", table_D.getDataStore()
                                     .getUpdateSQL());
                        TParm getTRA = new TParm();
                        for (int i = 0; i < result.getCount(); i++) {
                            // ���������ת����
                            order_code = result.getValue("ORDER_CODE", i);
                            getTRA = INDTool.getInstance().getTransunitByCode(
                                order_code);
                            if (getTRA.getCount() == 0
                                || getTRA.getErrCode() < 0) {
                                this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
                                return;
                            }
                            parm.setData("ORDER_CODE", i, order_code);
                            parm.setData("PURORDER_QTY", i, result.getDouble(
                                "PURORDER_QTY", i));
                            double s_qty = getTRA.getDouble("STOCK_QTY", 0);
                            parm.setData("STOCK_QTY", i, s_qty);
                            double d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                            parm.setData("DOSAGE_QTY", i, d_qty);
                        }
                    }
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDPurorderAction", "onDeleteM", parm);
                    // ɾ���ж�
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    getTable("TABLE_M").removeRow(
                        getTable("TABLE_M").getSelectedRow());
                    this.messageBox("ɾ���ɹ�");
                    this.onClear();
                }
            }
            else {
                TParm inparm = new TParm();
                TTable table_D = getTable("TABLE_D");
                int row = table_D.getSelectedRow();
                String purorder_no = getValueString("PURORDER_NO");
                String org_code = getValueString("ORG_CODE");
                String order_code = table_D.getItemString(row, "ORDER_CODE");
                double seq_no = table_D.getDataStore().getItemDouble(row,
                    "SEQ_NO");
                inparm.setData("PURORDER_NO", purorder_no);
                inparm.setData("ORDER_CODE", order_code);
                inparm.setData("SEQ_NO", seq_no);
                // ��ѯϸ��
                TParm result = IndPurorderDTool.getInstance().onQuery(inparm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                if (result.getCount() == 0) {
                    table_D.removeRow(table_D.getSelectedRow());
                }
                else {
                    if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
                        double pur_qty = result.getDouble("PURORDER_QTY", 0);
                        inparm.setData("PURORDER_QTY", pur_qty);
                        inparm.setData("ORG_CODE", org_code);
                        table_D.removeRow(table_D.getSelectedRow());
                        String[] sql = table_D.getDataStore().getUpdateSQL();
                        inparm.setData("UPDATE_SQL", sql);
                        // ���������ת����
                        TParm getTRA = INDTool.getInstance()
                            .getTransunitByCode(order_code);
                        if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                            this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
                            return;
                        }
                        double s_qty = getTRA.getDouble("STOCK_QTY", 0);
                        inparm.setData("STOCK_QTY", s_qty);
                        double d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                        inparm.setData("DOSAGE_QTY", d_qty);
                        result = TIOM_AppServer.executeAction(
                            "action.ind.INDPurorderAction", "onDeleteD",
                            inparm);
                        // ɾ���ж�
                        if (result == null || result.getErrCode() < 0) {
                            this.messageBox("ɾ��ʧ��");
                            return;
                        }
                        this.messageBox("ɾ���ɹ�");
                    }
                }
            }
        }
    }

    /**
     * ��ӡ������
     */
    public void onPrint() {
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("PURORDER_NO"))) {
            this.messageBox("�����ڶ�����");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "ҩƷ������");
            date.setData("SUP_CODE", "TEXT",
                         "ҩƷ����: " +
                         this.getTextFormat("SUP_CODE").getText());
            date.setData("PUR_NO", "TEXT",
                         "��������: " + this.getValueString("PURORDER_NO"));
            date.setData("DATE", "TEXT",
                         "��������: " +
                         this.getValueString("PURORDER_DATE").substring(0, 10).
                         replace('-', '/'));
            // �������
            TParm parm = new TParm();
            String order_code = "";
            double sum_money = 0;
            double amt = 0;
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                if ("Y".equals(table_d.getItemString(i, "END_FLG"))) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code,
                                       this.getValueString("SUP_CODE"), "PUR")));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return;
                }
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("MAN_CODE", inparm.getValue("MAN_CHN_DESC", 0));
//                parm.addData("QTY",
//                             StringTool.round(table_d.getItemDouble(i, "PURORDER_QTY"),
//                                              3));
                //luhai modify 2012-3-7 begin
//                this.messageBox(dfInt.format(table_d.getItemDouble(i, "PURORDER_QTY"))+"");
                parm.addData("QTY",
                		dfInt.format(table_d.getItemDouble(i, "PURORDER_QTY")));
              //luhai modify 2012-3-7 end
                parm.addData("PRICE", StringTool.round(table_d.getItemDouble(i,
                    "PURORDER_PRICE"), 4));
                amt = table_d.getItemDouble(i, "PURORDER_QTY") *
                    table_d.getItemDouble(i, "PURORDER_PRICE");
                parm.addData("AMT", StringTool.round(amt, 2));
                sum_money += amt;
            }
            if (parm.getCount("ORDER_DESC") == 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("CHECK", "TEXT", "��ˣ� ");
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            date.setData("TOT", "TEXT",
                         "�ܽ�" + StringTool.round(sum_money, 2));

            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\Purorder.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            // ������Ϣ(TABLE��ȡ��)
            setValue("PURORDER_DATE", table.getItemTimestamp(row,
                "PURORDER_DATE"));
            setValue("PURORDER_NO", table.getItemString(row, "PURORDER_NO"));
            setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
            setValue("PLAN_NO", table.getItemString(row, "PLAN_NO"));
            setValue("SUP_CODE", table.getItemString(row, "SUP_CODE"));
            setValue("RES_DELIVERY_DATE", table.getItemTimestamp(row,
                "RES_DELIVERY_DATE"));
            setValue("REASON_CODE", table.getItemString(row, "REASON_CHN_DESC"));
            setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
            // �趨ҳ��״̬
            getTextField("PURORDER_NO").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
            action = "updateM";

            // ��ϸ��Ϣ
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getPurOrderDByNo(getValueString("PURORDER_NO")));
            tds.retrieve();
            if (tds.rowCount() == 0) {
                //this.messageBox("û�ж�����ϸ");
                seq = 1;
            }
            else {
                seq = getMaxSeq(tds, "SEQ_NO");
            }

            // �۲���
            IndPurOrderObserver obser = new IndPurOrderObserver();
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            table_D.setDSValue();

            // �����ܼ�Ǯ
            double sum = getSumMoney();
            this.setValue("SUM_MONEY", sum);

            // ���һ��ϸ��
            onAddRow();
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            action = "updateD";
            // ������Ϣ
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            // ȡ��SYS_FEE��Ϣ��������״̬����
            /*
             String order_code = table.getDataStore().getItemString(table.
                getSelectedRow(), "ORDER_CODE");
                         if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
                         }
                         else {
                callFunction("UI|setSysStatus", "");
                         }
             */
        }
    }

    /**
     * ��TABLE�����༭�ؼ�ʱ����
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\ind\\INDSupOrder.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        TTable table = getTable("TABLE_D");
        table.acceptText();
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            table.getDataStore().setItem(table.getSelectedRow(), "ORDER_CODE",
                                         order_code);
        String specification = parm.getValue("SPECIFICATION");
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(specification))
            table.setItem(table.getSelectedRow(), "SPECIFICATION",
                          specification);
        String purch_unit = parm.getValue("PURCH_UNIT");
        if (!StringUtil.isNullString(purch_unit))
            table.setItem(table.getSelectedRow(), "BILL_UNIT", purch_unit);
        
        String supCode = this.getValueString("SUP_CODE");

        double price = parm.getDouble("CONTRACT_PRICE");
        table.setItem(table.getSelectedRow(), "PURORDER_PRICE", price);
        table.getDataStore().setActive(table.getSelectedRow(), true);
        // ���������
        TParm qtyParm = new TParm();
        qtyParm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        qtyParm.setData("ORDER_CODE", order_code);
        TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
        //System.out.println("getQTY" + getQTY);
        if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
            this.messageBox("ҩƷ:" + order_desc + "(" + order_code + ") δ�趨���������Ϣ");
            onAddRow();
            table.removeRow(table.getSelectedRow());
            return;
        }
        // �ж��Ƿ����ظ�����
        for (int i = 0; i < table.getDataStore().rowCount(); i++) {
            if (i == table.getSelectedRow()) {
                continue;
            }
            if (order_code.equals(table.getDataStore().getItemData(i, "ORDER_CODE"))) {
                this.messageBox("ҩƷ:" + order_desc + "(" + order_code + ") �Ѵ���");
                onAddRow();
                table.removeRow(table.getSelectedRow());
                return;
            }
        }
        onAddRow();
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE_D");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                if (!table.getDataStore().isActive(i)) {
                    continue;
                }
                table.getDataStore().setItem(i, "UPDATE_FLG", "2");
                this.setValue("SUM_MONEY", 0);
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                if (!table.getDataStore().isActive(i)) {
                    continue;
                }
                table.getDataStore().setItem(i, "UPDATE_FLG", "0");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        table.setDSValue();
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table������
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("PURORDER_QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            else {
                // �����ܼ�Ǯ
                if (!"2".equals(node.getTable().getDataStore().getItemString(
                    row, "UPDATE_FLG"))) {
                    node.getTable().getDataStore().setItem(row, "PURORDER_QTY",
                        qty);
                    this.setValue("SUM_MONEY", getSumMoney());
                }
            }
        }
        if ("GIFT_QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("������������С��0");
                return true;
            }
        }
        if ("PURORDER_PRICE".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("���۲���С�ڻ����0");
                return true;
            }
            else {
                // �����ܼ�Ǯ
                if (!"2".equals(node.getTable().getDataStore().getItemString(
                    row, "UPDATE_FLG"))) {
                    node.getTable().getDataStore().setItem(row,
                        "PURORDER_PRICE", qty);
                    this.setValue("SUM_MONEY", getSumMoney());
                }
            }
        }
        return false;
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int column = tableDown.getSelectedColumn();
        if (column == 0) {
            //this.messageBox("");
            String flg = tableDown.getDataStore().getItemString(tableDown.
                getSelectedRow(), "UPDATE_FLG");
            double amt = tableDown.getItemDouble(tableDown.getSelectedRow(),
                                                 "PURORDER_QTY") *
                tableDown.getItemDouble(tableDown.getSelectedRow(),
                                        "PURORDER_PRICE");
            double sum_amt = this.getValueDouble("SUM_MONEY");
            if ("2".equals(flg)) {
                sum_amt = sum_amt - amt;
            }
            else {
                sum_amt = sum_amt + amt;
            }
            this.setValue("SUM_MONEY", sum_amt);
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        /**
         * Ȩ�޿���
         * Ȩ��1:һ�����������Ȩ��,ֻ��ʾ������������;������¼�빦��
         * Ȩ��2:һ���������Ȩ��,ֻ��ʾ������������;��������¼�빦��
         * Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ�Ű�������¼�빦��
         */
        // ����Ȩ��
        if (!this.getPopedem("giftEnabled")) {
            TTable table_d = getTable("TABLE_D");
            table_d.setLockColumns("2,3,5,7,8,9");
            gift_flg = false;
        }
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("PURORDER_DATE", date);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        action = "insertM";

        // ע�ἤ��INDSupOrder�������¼�
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
    }

    /**
     * ���һ��ϸ��
     */
    private void onAddRow() {
        // ��δ�༭��ʱ����
        if (!this.isNewRow())
            return;
        TTable table_D = getTable("TABLE_D");
        int row = table_D.addRow();
        TParm parm = new TParm();
        parm.setData("ACTIVE", false);
        table_D.getDataStore().setActive(row, false);
    }

    /**
     * �Ƿ���δ�༭��
     *
     * @return boolean
     */
    private boolean isNewRow() {
        Boolean falg = false;
        TParm parmBuff = getTable("TABLE_D").getDataStore().getBuffer(
            getTable("TABLE_D").getDataStore().PRIMARY);
        int lastRow = parmBuff.getCount("#ACTIVE#");
        Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
        if (obj != null) {
            falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
        }
        else {
            falg = true;
        }
        return falg;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("�������Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("��Ӧ���̲���Ϊ��");
            return false;
        }
        if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
            if (TypeTool.getTimestamp(getValue("RES_DELIVERY_DATE")).compareTo(
                TypeTool.getTimestamp(getValue("PURORDER_DATE"))) < 0) {
                this.messageBox("�������ڲ������ڶ�������");
                return false;
            }
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        TTable table_D = getTable("TABLE_D");
        table_D.acceptText();
        for (int i = 0; i < table_D.getRowCount(); i++) {
            if (!table_D.getDataStore().isActive(i)) {
                continue;
            }
            if (table_D.getItemDouble(i, "PURORDER_QTY") <= 0) {
                this.messageBox("������������С�ڻ����0");
                return false;
            }
            if (table_D.getItemDouble(i, "GIFT_QTY") < 0) {
                this.messageBox("������������С��0");
                return false;
            }
            if (table_D.getItemDouble(i, "PURORDER_PRICE") <= 0) {
                this.messageBox("���۲���С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * �����ܽ��
     *
     * @return
     */
    private double getSumMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"2".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                sum += table.getItemDouble(i, "PURORDER_QTY")
                    * table.getItemDouble(i, "PURORDER_PRICE");
            }
        }
        return StringTool.round(sum, 2);
    }

    /**
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.rowCount();
        // ��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // �������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        // ���ż�1
        s++;
        return s;
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
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }

}
