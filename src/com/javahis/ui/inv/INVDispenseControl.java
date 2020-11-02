package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import jdo.inv.InvRequestDTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

import jdo.inv.InvStockDTool;
import com.dongyang.data.TNull;
import jdo.inv.InvStockDDTool;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.dongyang.jdo.TJDODBTool;

import jdo.inv.INVDispenseDDTool;
import jdo.inv.INVSQL;
import jdo.inv.InvDispenseDTool;
import jdo.inv.InvStockMTool;
import jdo.inv.InvVerifyinDDTool;

/** 
 * <p>
 * Title: ��������������Control(Ĭ��ȫѡ-ȫ�����⣬����֧�ֵ���ɨ�衣����) 
 * </p>   δ��ɣ����쵥               ��;������״̬0           ��ɣ�����״̬1
 *       
 * <p>    ����޷���ȷ������Ϊ�ظ�����request�������Ϊ0
 *        �ˡ�������ACTUCL_QTY-REQUEST_QTY��
 * Description: ��������������Control   
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013 
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fux 2013.05.02
 * @version 1.0
 */
public class INVDispenseControl 
    extends TControl { 
    public INVDispenseControl() {  
    }

    private TTable table_m;//���뵥

    private TTable table_d;//����ϸ��
    

    // ȫԺҩ�ⲿ����ҵ����
    @SuppressWarnings("unused")
	private boolean request_all_flg = true;

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }
   
    /**
     * ��շ���   
     */
    public void onClear() { 
    	//URGENT_FLG������ע��
        String clearString =   
            "START_DATE;END_DATE;REQUEST_TYPE_Q;TO_ORG_CODE_Q;REQUEST_NO_Q;"
            + "DISPENSE_NO_Q;DISPENSE_NO;REQUEST_NO;REQUEST_TYPE;DISPENSE_DATE;"
            + "TO_ORG_CODE;FROM_ORG_CODE;REN_CODE;DISPENSE_USER;REMARK;"
            + "FINA_FLG;CHECK_DATE;CHECK_DATE;SELECT_ALL;URGENT_FLG";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate(); 
        this.setValue("END_DATE",  
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE", 
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        table_m.setSelectionMode(0);  
        table_m.removeRowAll(); 
        table_d.setSelectionMode(0);   
        table_d.removeRowAll();     
    }


    /**  
     * ���淽��
     */
    public void onSave() {   
        if (!checkData()) {
            return;
        } 
     
        TParm parm = new TParm();
        // ���ⵥ������Ϣ 
        getDispenseMData(parm);
       
        // ���ⵥϸ����Ϣ
        getDispenseDData(parm);
    
        // ���ⵥ��Ź�����Ϣ
       // getDispenseDDData(parm); 
       
//        // �����������
       getInvStockMData(parm);
//        // �����ϸ���� 
      getInvStockDData(parm);   
        // ���¿����Ź�������     
        //getInvStockDDData(parm); 
     
        // ���뵥ϸ��״̬
        getRequestDData(parm);
        // ���뵥����״̬
        getRequestMData(parm); 

        // ��ѯ���ⷽʽ��N.���ȷ��ע�ǣ�Y.���⼴��⣩
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvSysParm()));
        //δ���
        ////��;----������;�ı���(DISCHECK_FLG,'Y') 
        if (this.getRadioButton("RadioButton3").isSelected()) {
        if (sysParm.getCount() > 0) { 
            TParm discheck_flg = new TParm(); 
            discheck_flg.setData("DISCHECK_FLG","N");
            parm.setData("DISCHECK_FLG", discheck_flg.getData());
            //�������ⵥ->N.���ȷ��ע�ǣ���;���ƣ�����Y.���⼴���->
            TParm result = TIOM_AppServer.executeAction(
                "action.inv.INVDispenseAction", "onInsert", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }  
            this.messageBox("P0001");
            onClear();
        }
        else {
            this.messageBox("û���趨���ʲ�����");
            return;
        }
             }
        //��;----������;�ı���(DISCHECK_FLG,'N') 
        if (this.getRadioButton("RadioButton2").isSelected()) {
        	   if (sysParm.getCount() > 0) { 
                   TParm discheck_flg = new TParm(); 
                   discheck_flg.setData("DISCHECK_FLG",
                                        sysParm.getValue("DISCHECK_FLG", 0));
                   parm.setData("DISCHECK_FLG", discheck_flg.getData());
                   //�������ⵥ->N.���ȷ��ע�ǣ�Y.���⼴���->
                   TParm result = TIOM_AppServer.executeAction(
                       "action.inv.INVDispenseAction", "onUpdate", parm);
                   if (result == null || result.getErrCode() < 0) {
                       this.messageBox("E0001");
                       return;
                   }  
                   this.messageBox("P0001");
                   onClear();
               }
               else {
                   this.messageBox("û���趨���ʲ�����");
                   return;
               }
        }
       
    }
 
    /**
     * ȡ�ó��ⵥ��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getDispenseMData(TParm parm) {
        TParm dispenseM = new TParm();
        // ���ⵥ��
        dispenseM.setData("DISPENSE_NO",
                          SystemTool.getInstance().getNo("ALL", "INV",
            "DISPENSE_NO", "No"));
        // �������
        dispenseM.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE"));
        // ���뵥��
        dispenseM.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        // ��������
        dispenseM.setData("REQUEST_DATE",
                          table_m.getParmValue().getTimestamp("REQUEST_DATE",
            table_m.getSelectedRow()));
        // �������벿��
        dispenseM.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
        // ���벿��
        dispenseM.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        // ��������
        dispenseM.setData("DISPENSE_DATE", this.getValue("DISPENSE_DATE"));
        // ������Ա     
        dispenseM.setData("DISPENSE_USER", Operator.getID());
        // ����ע��
        dispenseM.setData("URGENT_FLG", this.getValueString("URGENT_FLG"));
        // ��ע
        dispenseM.setData("REMARK", this.getValueString("REMARK"));
        // ȡ������
        dispenseM.setData("DISPOSAL_FLG", "N");
        // ���ȷ������
        dispenseM.setData("CHECK_DATE", SystemTool.getInstance().getDate());
        // ���ȷ����Ա
        dispenseM.setData("CHECK_USER", Operator.getID());
        // ����ԭ��
        dispenseM.setData("REN_CODE", this.getValueString("REN_CODE"));
        // �����ע��
        dispenseM.setData("FINA_FLG",
                          "WAS".equals(this.getValueString("REQUEST_TYPE")) ?
                          "1" : "0");  
        // OPT
        dispenseM.setData("OPT_USER", Operator.getID());
        dispenseM.setData("OPT_DATE", SystemTool.getInstance().getDate());
        dispenseM.setData("OPT_TERM", Operator.getIP());
        // ������� 2������
        dispenseM.setData("IO_FLG", "2");   
        System.out.println("DISPENSE_M"+dispenseM);   
        parm.setData("DISPENSE_M", dispenseM.getData());
        return parm;
    }

    /**
     * ȡ�ó��ⵥϸ������
     * @param parm TParm   
     * @return TParm
     */
    private TParm getDispenseDData(TParm parm) {
        TParm dispenseD = new TParm();
        int count = 0;
        TNull tnull = new TNull(Timestamp.class);
        System.out.println("����  "+table_d.getRowCount());
        System.out.println("");   
        for (int i = 0; i < table_d.getRowCount(); i++) { 
        	System.out.println("i"+i);     
        	System.out.println("ȡ�ó��ⵥϸ������SELECT_FLG"+table_d.getItemString(i,"SELECT_FLG" ));
        	System.out.println("ȡ�ó��ⵥϸ������INV_DESC_CHN"+table_d.getItemString(i,"INV_CHN_DESC" ));
        	  if ("N".equals(table_d.getItemString(i,"SELECT_FLG"))) {
                  continue; 
              }          
            // ���ⵥ��    
            dispenseD.addData("DISPENSE_NO",
                              parm.getParm("DISPENSE_M").getValue("DISPENSE_NO"));
            // ���ⵥ���
            dispenseD.addData("SEQ_NO", count);
            count++;
            // �������    
            dispenseD.addData("BATCH_SEQ",  
                              table_d.getParmValue().getInt("BATCH_SEQ", i));
            // ���ʴ���
            dispenseD.addData("INV_CODE",
                              table_d.getParmValue().getValue("INV_CODE", i));
            // �������
            dispenseD.addData("INVSEQ_NO",
                              table_d.getParmValue().getInt("INVSEQ_NO", i));
            // ��Ź���ע��
            dispenseD.addData("SEQMAN_FLG",
                              table_d.getParmValue().getValue("SEQMAN_FLG", i));
            // ����
            dispenseD.addData("QTY", table_d.getItemDouble(i, "QTY"));
            // ��λ
            dispenseD.addData("DISPENSE_UNIT", 
                              table_d.getParmValue().getValue("DISPENSE_UNIT",
                i));
            // �ɱ���
            dispenseD.addData("COST_PRICE",
                              table_d.getItemDouble(i, "COST_PRICE"));
            // �������
            dispenseD.addData("REQUEST_SEQ",
                              table_d.getParmValue().getInt("REQUEST_SEQ", i));
            // ����
            dispenseD.addData("BATCH_NO",
                              table_d.getParmValue().getValue("BATCH_NO", i));
            // Ч�� VALID_DATE
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                dispenseD.addData("VALID_DATE", tnull);
            }
            else { 
                dispenseD.addData("VALID_DATE",
                                  TypeTool.getTimestamp(table_d.getParmValue().
                    getTimestamp("VALID_DATE", i)));
            }
            // ȡ������ 
            dispenseD.addData("DISPOSAL_FLG", "N");
            // OPT
            dispenseD.addData("OPT_USER", Operator.getID());
            dispenseD.addData("OPT_DATE", SystemTool.getInstance().getDate());
            dispenseD.addData("OPT_TERM", Operator.getIP());
            // ������� 2������
            dispenseD.addData("IO_FLG", "2");  
        }
        System.out.println("dispenseD"+dispenseD); 
        parm.setData("DISPENSE_D", dispenseD.getData());
        return parm;
    } 

    /**
     * ���ⵥ��Ź�����Ϣ(���Ҫ����Ź���ͷ����)  
     * @param parm TParm
     * @return TParm 
     */

    /**
     * ȡ�ÿ����������  
     * @param parm TParm
     * @return TParm
     */ 
    public TParm getInvStockMData(TParm parm) {
        TParm stockOutM = new TParm();
        //TParm stockInM = new TParm();
        // ���ⲿ�� 
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = "";
//        REQUEST_NO
//        REQUEST_DATE
//        REQUEST_TYPE
        String request_type = this.getValueString("REQUEST_TYPE");
        if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
            // ����,����
            out_org_code = this.getValueString("FROM_ORG_CODE");
            in_org_code = this.getValueString("TO_ORG_CODE");
        }
        else if ("RET".equals(request_type) ||
                 "WAS".equals(request_type)) {
            // �˿�,����
            out_org_code = this.getValueString("TO_ORG_CODE");
            in_org_code = this.getValueString("FROM_ORG_CODE");
        }
        double qty = 0;
        Map map = new HashMap(); 
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            else    
            	if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            else {
                inv_code = table_d.getParmValue().getValue("INV_CODE", i);
                qty = table_d.getItemDouble(i, "QTY");
                if (map.isEmpty()) {
                    map.put(inv_code, qty);
                }
                else {
                    if (map.containsKey(inv_code)) { 
                        qty += TypeTool.getDouble(map.get(inv_code));
                        map.put(inv_code, qty);
                    }
                    else {
                        map.put(inv_code, qty);
                    }
                }
            }
        }

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            stockOutM.addData("ORG_CODE", out_org_code);
            inv_code = TypeTool.getString(iterator.next());
            stockOutM.addData("INV_CODE", inv_code);
            stockOutM.addData("STOCK_QTY", TypeTool.getDouble(map.get(inv_code)));
            stockOutM.addData("OPT_USER", Operator.getID());
            stockOutM.addData("OPT_DATE", SystemTool.getInstance().getDate());
            stockOutM.addData("OPT_TERM", Operator.getIP());
//            if (!"WAS".equals(request_type)) {
//                TParm stockParm = new TParm(TJDODBTool.getInstance().select(
//                    INVSQL.getInvStockM(in_org_code, inv_code)));
//                TParm baseParm = new TParm(TJDODBTool.getInstance().select(
//                    INVSQL.getInvBase(inv_code)));
//                if (stockParm == null || stockParm.getCount() <= 0) {
//                    stockInM.addData("TYPE", "INSERT"); //��������
//                    stockInM.addData("ORG_CODE", in_org_code);
//                    stockInM.addData("INV_CODE", inv_code);
//                    stockInM.addData("REGION_CODE", Operator.getRegion());
//                    stockInM.addData("DISPENSE_FLG", "N");
//                    stockInM.addData("DISPENSE_ORG_CODE", "");
//                    stockInM.addData("STOCK_FLG", "N");
//                    stockInM.addData("MATERIAL_LOC_CODE", "");
//                    stockInM.addData("SAFE_QTY", 0);
//                    stockInM.addData("MIN_QTY", 0);
//                    stockInM.addData("MAX_QTY", 0);
//                    stockInM.addData("ECONOMICBUY_QTY", 0);
//                    stockInM.addData("STOCK_QTY",
//                                     TypeTool.getDouble(map.get(inv_code)));
//                    stockInM.addData("MM_USE_QTY", 0);
//                    stockInM.addData("AVERAGE_DAYUSE_QTY", 0);
//                    stockInM.addData("STOCK_UNIT",
//                                     baseParm.getValue("DISPENSE_UNIT",0));//========pangben modify 2011829
//                    stockInM.addData("OPT_USER", Operator.getID());
//                    stockInM.addData("OPT_DATE",
//                                     SystemTool.getInstance().getDate());
//                    stockInM.addData("OPT_TERM", Operator.getIP());
//                }
//                else {
//                    stockInM.addData("TYPE", "UPDATE"); //��������
//                    stockInM.addData("ORG_CODE", in_org_code);
//                    stockInM.addData("INV_CODE", inv_code);
//                    stockInM.addData("STOCK_QTY",
//                                     TypeTool.getDouble(map.get(inv_code)));
//                    stockInM.addData("OPT_USER", Operator.getID());
//                    stockInM.addData("OPT_DATE",
//                                     SystemTool.getInstance().getDate());
//                    stockInM.addData("OPT_TERM", Operator.getIP());
//                }
//            }
        }
        parm.setData("STOCK_OUT_M", stockOutM.getData());
        //parm.setData("STOCK_IN_M", stockInM.getData());
        return parm;
    }

    /**
     * ȡ�ÿ����ϸ����
     * @param parm TParm  
     * @return TParm
     */
    public TParm getInvStockDData(TParm parm) {
        TParm stockOutD = new TParm();
        //TParm stockInD = new TParm();
        // ���ⲿ��
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = ""; 
        String request_type = this.getValueString("REQUEST_TYPE");
        if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
            // ����,����
            out_org_code = this.getValueString("FROM_ORG_CODE");
            in_org_code = this.getValueString("TO_ORG_CODE");
        }
        else if ("RET".equals(request_type) ||
                 "WAS".equals(request_type)) {
            // �˿�,����
            out_org_code = this.getValueString("TO_ORG_CODE");
            in_org_code = this.getValueString("FROM_ORG_CODE");
        } 

        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
                inv_code = table_d.getParmValue().getValue("INV_CODE", i);
                    stockOutD.addData("ORG_CODE", out_org_code);
                    stockOutD.addData("INV_CODE", inv_code);
                    stockOutD.addData("BATCH_SEQ",
                                      table_d.getParmValue().
                                      getInt("BATCH_SEQ", i));
                    stockOutD.addData("STOCK_QTY",
                                      table_d.getItemDouble(i, "QTY"));
                    stockOutD.addData("OPT_USER", Operator.getID());
                    stockOutD.addData("OPT_DATE",
                                      SystemTool.getInstance().getDate());
                    stockOutD.addData("OPT_TERM", Operator.getIP());
            }
 
//        if (!"WAS".equals(request_type)) {
//            int batch_seq = 0;
//            for (int i = 0; i < stockOutD.getCount("INV_CODE"); i++) {
//                inv_code = stockOutD.getValue("INV_CODE", i);
//                batch_seq = stockOutD.getInt("BATCH_SEQ", i);
//                TParm stockDParm = new TParm(TJDODBTool.getInstance().select(
//                    INVSQL.getInvStockD(in_org_code, inv_code, batch_seq)));
//                if (stockDParm == null || stockDParm.getCount("INV_CODE") <= 0) {
//                    TParm baseParm = new TParm(TJDODBTool.getInstance().select(
//                        INVSQL.getInvBase(inv_code)));
//                    stockInD.addData("TYPE", "INSERT"); //��������
//                    stockInD.addData("ORG_CODE", in_org_code);
//                    stockInD.addData("INV_CODE", inv_code);
//                    stockInD.addData("BATCH_SEQ", batch_seq);
//                    stockInD.addData("REGION_CODE", Operator.getRegion());
//                    stockInD.addData("BATCH_NO",
//                                     table_d.getParmValue().
//                                     getValue("BATCH_NO", i));
//                    stockInD.addData("VALID_DATE",
//                                     table_d.getParmValue().
//                                     getTimestamp("VALID_DATE", i));
//                    stockInD.addData("STOCK_QTY",
//                                     stockOutD.getDouble("STOCK_QTY", i));
//                    stockInD.addData("LASTDAY_TOLSTOCK_QTY", 0);
//                    stockInD.addData("DAYIN_QTY",
//                                     stockOutD.getDouble("STOCK_QTY", i));
//                    stockInD.addData("DAYOUT_QTY", 0);
//                    stockInD.addData("DAY_CHECKMODI_QTY", 0);
//                    stockInD.addData("DAY_VERIFYIN_QTY", 0);
//                    stockInD.addData("DAY_VERIFYIN_AMT", 0);
//                    stockInD.addData("GIFTIN_QTY", 0);
//                    stockInD.addData("DAY_REGRESSGOODS_QTY", 0);
//                    stockInD.addData("DAY_REGRESSGOODS_AMT", 0);
//                    if ("REQ".equals(request_type)) {
//                        stockInD.addData("DAY_REQUESTIN_QTY",
//                                         stockOutD.getDouble("STOCK_QTY", i));
//                    }
//                    else {
//                        stockInD.addData("DAY_REQUESTIN_QTY", 0);
//                    }
//                    stockInD.addData("DAY_REQUESTOUT_QTY", 0);
//                    if ("GIF".equals(request_type)) {
//                        stockInD.addData("DAY_CHANGEIN_QTY",
//                                         stockOutD.getDouble("STOCK_QTY", i));
//                    }
//                    else {
//                        stockInD.addData("DAY_CHANGEIN_QTY", 0);
//                    }
//                    stockInD.addData("DAY_CHANGEOUT_QTY", 0);
//                    if ("RET".equals(request_type)) {
//                        stockInD.addData("DAY_TRANSMITIN_QTY",
//                                         stockOutD.getDouble("STOCK_QTY", i));
//                    }
//                    else {
//                        stockInD.addData("DAY_TRANSMITIN_QTY", 0);
//                    }
//                    stockInD.addData("DAY_TRANSMITOUT_QTY", 0);
//                    stockInD.addData("DAY_WASTE_QTY", 0);
//                    stockInD.addData("DAY_DISPENSE_QTY", 0);
//                    stockInD.addData("DAY_REGRESS_QTY", 0);
//                    stockInD.addData("FREEZE_TOT", 0);
//                    stockInD.addData("UNIT_PRICE",
//                                     baseParm.getDouble("COST_PRICE", 0));
//                    stockInD.addData("STOCK_UNIT",
//                                     baseParm.getValue("DISPENSE_UNIT", 0));
//
//                    stockInD.addData("OPT_USER", Operator.getID());
//                    stockInD.addData("OPT_DATE",
//                                     SystemTool.getInstance().getDate());
//                    stockInD.addData("OPT_TERM", Operator.getIP());
//                }
//                else {
//                    stockInD.addData("TYPE", "UPDATE"); //��������
//                    stockInD.addData("ORG_CODE", in_org_code);
//                    stockInD.addData("INV_CODE", inv_code);
//                    stockInD.addData("BATCH_SEQ", batch_seq);
//                    stockInD.addData("STOCK_QTY",
//                                     stockOutD.getDouble("STOCK_QTY", i));
//                    stockInD.addData("DAYIN_QTY",
//                                     stockOutD.getDouble("STOCK_QTY", i));
//                    if ("REQ".equals(request_type)) {
//                        stockInD.addData("DAY_REQUESTIN_QTY",
//                                         stockOutD.getDouble("STOCK_QTY", i));
//                    }
//                    else {
//                        stockInD.addData("DAY_REQUESTIN_QTY", 0);
//                    }
//                    if ("GIF".equals(request_type)) {
//                        stockInD.addData("DAY_CHANGEIN_QTY",
//                                         stockOutD.getDouble("STOCK_QTY", i));
//                    }
//                    else {
//                        stockInD.addData("DAY_CHANGEIN_QTY", 0);
//                    }
//                    if ("RET".equals(request_type)) {
//                        stockInD.addData("DAY_TRANSMITIN_QTY",
//                                         stockOutD.getDouble("STOCK_QTY", i));
//                    }
//                    else {
//                        stockInD.addData("DAY_TRANSMITIN_QTY", 0);
//                    }
//
//                    stockInD.addData("OPT_USER", Operator.getID());
//                    stockInD.addData("OPT_DATE",
//                                     SystemTool.getInstance().getDate());
//                    stockInD.addData("OPT_TERM", Operator.getIP());
//                }
        parm.setData("STOCK_OUT_D", stockOutD.getData());
//       parm.setData("STOCK_IN_D", stockInD.getData());
        return parm; 
    }


    /**
     * 
     * ���뵥ϸ������
     * @param parm TParm
     * @return TParm
     */
    public TParm getRequestDData(TParm parm) {
        TParm requestD = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            else if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            else { 
                requestD.addData("REQUEST_NO", this.getValueString("REQUEST_NO"));
                requestD.addData("SEQ_NO",
                                 table_d.getParmValue().getInt("REQUEST_SEQ", i));
                requestD.addData("ACTUAL_QTY", table_d.getItemDouble(i, "QTY"));
                if (table_d.getItemDouble(i, "QTY") +
                    table_d.getParmValue().getDouble("ACTUAL_QTY", i) ==
                    table_d.getParmValue().getDouble("REQUEST_QTY", i)) {
                    requestD.addData("FINA_TYPE", "3");
                }
                else {
                    requestD.addData("FINA_TYPE", "1"); 
                }
                requestD.addData("OPT_USER", Operator.getID());
                requestD.addData("OPT_DATE",
                                 SystemTool.getInstance().getDate());
                requestD.addData("OPT_TERM", Operator.getIP());
            }
        }
        parm.setData("REQUEST_D", requestD.getData());
        return parm;
    }

    /**
     * ���뵥��������
     * @param parm TParm
     * @return TParmonsave
     *
     */
    public TParm getRequestMData(TParm parm) {
        TParm requestM = new TParm();
        requestM.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        boolean flg = true;
        TParm requestD = parm.getParm("REQUEST_D");
        for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
            if ("N".equals(requestD.getValue("FINA_TYPE", i))) {
                flg = false;
                break;
            }
        }
        if (flg) {
            requestM.setData("FINAL_FLG", "Y");
        } 
        else {
            requestM.setData("FINAL_FLG", "N");
        }
        requestM.setData("OPT_USER", Operator.getID());
        requestM.setData("OPT_DATE",
                         SystemTool.getInstance().getDate());
        requestM.setData("OPT_TERM", Operator.getIP());
        requestM.setData("REN_CODE", this.getValueString("REN_CODE"));
        parm.setData("REQUEST_M", requestM.getData());
        return parm;
    }

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() { 
    	//ֱ�ӽ����ı����ӹ���¼�
        table_d.acceptText();
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            this.messageBox("���뵥�Ų���Ϊ��");
            return false;
        }
        if (table_d.getRowCount() < 1) { 
            this.messageBox("û�г�����Ϣ");  
            return false;
        }
        
        
        boolean flg = false;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    flg = true;
                    break;
                }
            }
        }
        if (!flg) {
            this.messageBox("û�г�����Ϣ");
            return false;
        }
        String request_type = this.getValueString("REQUEST_TYPE");
        // ���ⲿ��
        String org_code = "";
        // ��������
        String inv_code = "";

        TParm result = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
        	
            inv_code = table_d.getParmValue().getValue("INV_CODE", i);
            if (!"".equals(inv_code)) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }  
                if (table_d.getItemDouble(i, "QTY") >0) {
                   
                     
                }else {
                	this.messageBox("������������С�ڻ����0");
				}
                
                
                
         
                if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
                    // ����,����
                    org_code = this.getValueString("FROM_ORG_CODE");
                }
                else if ("RET".equals(request_type) ||
                         "WAS".equals(request_type)) {
                    // �˿�,����
                    org_code = this.getValueString("TO_ORG_CODE");
                }  
                TParm stockParm = new TParm(); 
                //������Ź�����D��У��
                if ("N".equals(table_d.getParmValue().getValue("SEQMAN_FLG", i))) {
                    // ��ѯ���
                    stockParm.setData("ORG_CODE", org_code);
                    stockParm.setData("INV_CODE", inv_code);  
//                    stockParm.setData("BATCH_SEQ",
//                                      table_d.getItemData(i, "BATCH_SEQ"));
//                    result = InvStockDTool.getInstance().onQueryStockQty(
//                        stockParm);
                    result =  InvStockMTool.getInstance().getStockQty(stockParm);
                    if (result == null || result.getCount() <= 0 ||
                        result.getDouble("SUM(STOCK_QTY)", 0) <
                        table_d.getItemDouble(i, "QTY")) {
                        this.messageBox("����:" +
                                        table_d.getItemString(i, "INV_CHN_DESC") +
                                        "��治��, ��ǰ�����Ϊ" +
                                        result.getDouble("STOCK_QTY", 0));
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();  
        //����״̬ 
        //���뵥��δ��� 
        if (this.getRadioButton("RadioButton3").isSelected()) {
            parm.setData("TYPE","REQUEST");
        }
        //0����;  
        if (this.getRadioButton("RadioButton2").isSelected()) {
        	parm.setData("TYPE","DISPENSE_OUT");
            parm.setData("FINA_FLG", "0");
        }
        //1:���  
        else if (this.getRadioButton("RadioButton1").isSelected()) {
        	parm.setData("TYPE","DISPENSE_OUT");
            parm.setData("FINA_FLG", "1");   
        }
        // ��ѯʱ��    
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        // �������
        if (!"".equals(this.getValueString("REQUEST_TYPE_Q"))) {
            parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE_Q"));
        }
        // ���벿��
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
            parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE_Q"));
        }
        // ���뵥��
        if (!"".equals(this.getValueString("REQUEST_NO_Q"))) {
            parm.setData("REQUEST_NO", this.getValueString("REQUEST_NO_Q"));
        }
        // ���ⵥ��
        if (!"".equals(this.getValueString("DISPENSE_NO_Q"))) {
            parm.setData("DISPENSE_NO", this.getValueString("DISPENSE_NO_Q"));
        }

        TParm inparm = new TParm();
        inparm.setData("DISPENSE_M", parm.getData());
        // ��ѯ
        TParm result = TIOM_AppServer.executeAction( 
            "action.inv.INVDispenseAction", "onQueryMOut", inparm);
        if (result == null || result.getCount() <= 0) {   
            this.messageBox("û�в�ѯ����");
            table_m.removeRowAll();
            return;
        } 
        // ȫԺҩ�ⲿ����ҵ����
//        if (!request_all_flg) { 
//
//        } 

        //System.out.println("---" + result);
        table_m.setParmValue(result);
    }
    
    /** 
     * ɾ������
     */
    public void onDelete() {
    	//δ���״̬����ɾ��������ɾ������ɶ��
    	//������;��ʱ�����ɾ�������δ���״̬
    	if (this.getRadioButton("RadioButton3").isSelected()) {
             	
		//this.messageBox("ɾ���ɹ���"); 
    	  }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {  
        int row = table_m.getSelectedRow();
        if (row != -1) {
            table_d.setSelectionMode(0);  
            // ������Ϣ(TABLE��ȡ��)
            setValue("DISPENSE_NO", table_m.getItemString(row, "DISPENSE_NO"));
            setValue("REQUEST_NO", table_m.getItemString(row, "REQUEST_NO"));
            setValue("REQUEST_TYPE", table_m.getItemString(row, "REQUEST_TYPE"));
            setValue("REN_CODE", table_m.getParmValue().getValue("REN_CODE", row));
            setValue("FINA_FLG", table_m.getParmValue().getValue("FINA_FLG", row));
            Timestamp date = SystemTool.getInstance().getDate();
            if (getRadioButton("RadioButton3").isSelected()) {
                setValue("DISPENSE_DATE", date);
            }  
            else {    
                setValue("DISPENSE_DATE",       
                         table_m.getItemTimestamp(row, "DISPENSE_DATE"));
            }
            setValue("TO_ORG_CODE", table_m.getItemString(row, "TO_ORG_CODE"));
            setValue("FROM_ORG_CODE",
                     table_m.getItemString(row, "FROM_ORG_CODE"));
            setValue("DISPENSE_USER",
                     table_m.getItemString(row, "DISPENSE_USER"));
            setValue("REMARK", table_m.getItemString(row, "REMARK"));
            setValue("CHECK_DATE", table_m.getItemTimestamp(row, "CHECK_DATE"));
            setValue("URGENT_FLG", table_m.getItemString(row, "URGENT_FLG"));
            setValue("CHECK_USER", table_m.getItemString(row, "CHECK_USER"));

            // ��ϸ��Ϣ
            TParm parm = new TParm();
            TParm result = new TParm();   
            if (getRadioButton("RadioButton3").isSelected()) {
                parm.setData("REQUEST_NO", 
                             table_m.getItemString(row, "REQUEST_NO"));
                result = InvRequestDTool.getInstance().onQueryRequestDOut(parm);
                table_d.removeRowAll();
                table_d.setParmValue(result);
            }  
            else { 
                parm.setData("DISPENSE_NO",
                             table_m.getItemString(row, "DISPENSE_NO"));
                if (getRadioButton("RadioButton2").isSelected()) {
                    parm.setData("FINA_FLG", "0");
                }
                else if (getRadioButton("RadioButton1").isSelected()) {
                    parm.setData("FINA_FLG", "1");  
                }
                result = InvDispenseDTool.getInstance().onQueryDispenseDOut(
                    parm);
                table_d.removeRowAll();
                table_d.setParmValue(result); 
            } 
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û��������ϸ"); 
                return; 
            }

        }
    }  
   

    /**
     * ȫѡ�¼� 
     */
    public void onSelectAll() {
        String flg = "Y";
        if (getCheckBox("SELECT_ALL").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        //D��
        for (int i = 0; i < table_d.getRowCount(); i++) {
            table_d.setItem(i, "SELECT_FLG", flg);
        }  
  
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
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if ("Y".equals(table_d.getParmValue().getValue("SEQMAN_FLG", row))) {
                this.messageBox("�����޸���Ź������ʵ�����");
                return true;
            }
            else {
                if (qty <= 0) {
                    this.messageBox("������������С�ڻ����0");
                    return true;
                }
                else if (qty >
                         (table_d.getItemDouble(row, "REQUEST_QTY") -
                          table_d.getItemDouble(row, "ACTUAL_QTY"))) {
                    this.messageBox("�����������ܴ�����������");
                    return true;
                } 
                else {
                    // ������
                    table_d.setItem(row, "SUM_AMT", qty *
                                    table_d.getItemDouble(row, "COST_PRICE"));
                    return false;
                }
            }
        }         
        return false;
    }

    /**
     * ����״̬����¼�
     */
    public void onChangeFinaFlg() { 
    	//δ���ʱ�ܵ�����ⵥ��
        TTextField dispense_no = this.getTextField("DISPENSE_NO_Q");
        if (getRadioButton("RadioButton3").isSelected()) { 
            dispense_no.setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);  
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            this.onClear();   
        }                    
        //��;ʱ���ܵ�����ⵥ��
        else if (getRadioButton("RadioButton2").isSelected()) {
            dispense_no.setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false); 
            this.onClear();
        }             
        //���ʱ���ܵ�����ⵥ��  
        else if (getRadioButton("RadioButton1").isSelected()) {
            dispense_no.setEnabled(false); 
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false); 
            this.onClear();
        }
         
    }
    private boolean flg = false;// �жϴ˲����Ƿ�֮ǰ�����ݹ�ѡȥ��

    

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ȫԺҩ�ⲿ����ҵ����
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }
        else {  
            request_all_flg = true;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
      
        table_m = getTable("TABLE_M");    
        table_d = getTable("TABLE_D"); 
 
        // TABLE_Dֵ�ı��¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue"); 
        //����״̬����¼�
         onChangeFinaFlg();   
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
     * �õ�TCheckBox����
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TTextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }


}
