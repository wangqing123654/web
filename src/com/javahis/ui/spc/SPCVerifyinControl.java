package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndAgentTool;
import jdo.spc.IndPurorderMTool;
import jdo.spc.IndStockDTool;   
import jdo.spc.IndSysParmTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndVerifyinObserver;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �������Control
 * </p>
 *
 * <p>
 * Description: �������Control
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */

public class SPCVerifyinControl
    extends TControl {

    // �������
    private String action = "insert";
    // ϸ�����
    private int seq;

    private TParm resultParm;

    private Map map;

    // ����Ȩ��
    private boolean gift_flg = true;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    // ������Ȩ��
    private boolean check_flg = true;

    // ��ӡ�����ʽ
    java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
    java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
    java.text.DecimalFormat df3 = new java.text.DecimalFormat(
        "##########0.000");
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");
    //luhai 2012-2-28 ����һλ�ĸ�ʽ��
    java.text.DecimalFormat df5 = new java.text.DecimalFormat(
    "##########0");

    public SPCVerifyinControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
        //tableʧȥ����ʱȡ���༭״̬
        this.getTable("TABLE_D").getTable().putClientProperty("terminateEditOnFocusLost",
																	Boolean.TRUE);
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
        //this.messageBox(getValueString("SUP_CODE"));
        if (!"".equals(getValueString("SUP_CODE"))) {
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        if (!"".equals(getValueString("VERIFYIN_NO"))) {
            parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
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
        result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction",
                                              "onQueryM", parm);
//        System.out.println("------------query: "+result);
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // �����
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if ("".equals(result.getValue("CHECK_USER", i))) {
                    result.removeRow(i);
                }
            }
        }
        else {
            // δ���
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if (!"".equals(result.getValue("CHECK_USER", i)) ||
                    result.getData("CHECK_USER", i) != null) {
                    result.removeRow(i);
                }
            }
        }
        if (result == null || result.getCount("VERIFYIN_NO") <= 0) {
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
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;VERIFYIN_DATE;VERIFYIN_NO;"
            + "PLAN_NO;REASON_CHN_DESC;CHECK_FLG;DESCRIPTION;"
            + "SELECT_ALL;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("VERIFYIN_DATE", date);
        // ����״̬
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("onExport")).setEnabled(true);
        getComboBox("ORG_CODE").setEnabled(true);
        getTextFormat("SUP_CODE").setEnabled(true);
        // getCheckBox("CHECK_FLG").setEnabled(false);
        getTextField("VERIFYIN_NO").setEnabled(true);
        getTable("TABLE_M").setSelectionMode(0);
        getTable("TABLE_M").removeRowAll();
        getTable("TABLE_D").setSelectionMode(0);
        getTable("TABLE_D").removeRowAll();
        action = "insert";
        this.setCheckFlgStatus(action);
        resultParm = null;
        TTable table_D = getTable("TABLE_D");
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
        tds.retrieve();
        // �۲���
        IndVerifyinObserver obser = new IndVerifyinObserver();
        tds.addObserver(obser);
        table_D.setDataStore(tds);
        table_D.setDSValue();
        getTextFormat("SUP_CODE").setText("");
        getTextFormat("SUP_CODE").setValue("");
        this.setValue("REASON_CODE", "");
        table_D.setLockColumns("1,2,5,7,9,17,18,19");//20150612 wangjc add
    }

    /**
     * ���淽��
     */
    public void onSave() {
	    if (getRadioButton("UPDATE_FLG_B").isSelected()) {//20150528 wangjc add
	        map = new HashMap();
	        // ���������
	        TParm parm = new TParm();
	        // ���ؽ����
	        TParm result = new TParm();
	        TNull tnull = new TNull(Timestamp.class);
	        Timestamp date = SystemTool.getInstance().getDate();
	        if ("insert".equals(action)) {
	            if (!CheckDataD()) {
	                return;
	            }
	            // ������Ϣ
	            // ��������
	            parm.setData("VERIFYIN_DATE", getValue("VERIFYIN_DATE"));
	            // ������Ա
	            parm.setData("VERIFYIN_USER", Operator.getID());
	            // ���ղ���
	            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
	            // ��Ӧ����
	            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
	            // ��ע
	            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
	            // �ݻ�ԭ��
	            parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
	            // �ƻ�����
	            parm.setData("PLAN_NO", getValueString("PLAN_NO"));
	            // OPT_USER,OPT_DATE,OPT_TERM
	            parm.setData("OPT_USER", Operator.getID());
	            parm.setData("OPT_DATE", date);
	            parm.setData("OPT_TERM", Operator.getIP());
	            //zhangyong20110517
	            parm.setData("REGION_CODE", Operator.getRegion());
	            parm.setData("APPLY_TYPE", "1");
				parm.setData("DRUG_CATEGORY", "1");
	            // ���
	            boolean check_flg = false;
	            if ("Y".equals(getValueString("CHECK_FLG"))) {
	                // �����Ա
	                parm.setData("CHECK_USER", Operator.getID());
	                // ���ʱ��
	                parm.setData("CHECK_DATE", date);
	                check_flg = true;
	            }
	            else {
	                parm.setData("CHECK_USER", "");
	                parm.setData("CHECK_DATE", tnull);
	                check_flg = false;
	            }
	            // ϸ����
	            TTable table_D = getTable("TABLE_D");
	            table_D.acceptText();
	            TDataStore dataStore = table_D.getDataStore();
	
	            // ����Ƿ���춯״̬�ж�
	            String org_code = parm.getValue("ORG_CODE");
	            if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
	                this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
	                return;
	            }
	
	            // ϸ����Ϣ
	            // ������ⵥ��
	            String verifyin_no = "";
	            // ����������
	            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
	            // ������
	            double qty = 0.00;
	            // ������
	            double g_qty = 0.00;
	            // ������
	            double pur_qty = 0.00;
	            // �����
	            double stock_qty = 0.00;
	            // ״̬
	            String update_flg = "0";
	            // ҩƷ����
	            String order_code = "";
	            // ���������ת����
	            TParm getTRA = new TParm();
	            // �����Ч��������
	            TParm getSEQ = new TParm();
	            // ����ת����
	            double s_qty = 0.00;
	            // ���ת����
	            double d_qty = 0.00;
	            // ����
	            String batch_no = "";
	            // �������
	            int batch_seq = 1;
	            // ��Ч��
	            String valid_date = "";
	            
	            // ��������
	            String man_date = "";
	            
	            // ���»�����IND_STOCK��FLG->U:����;I,����
	            String flg = "";
	            // ���ۼ۸�
	            double retail = 0.00;
	            // ���յ���
	            double verprice = 0.00;
	            // �ۼ������ۿ���
	            double deduct_atm = 0.00;
	            // ���ƽ����
	            double stock_price = 0.00;
	            // ��������
	            String pur_no = "";
	            // �����������
	            int pur_no_seq = 0;
	            // �õ�IND_SYSPARM��Ϣ
	            result = IndSysParmTool.getInstance().onQuery();
	            // ���뵥���Զ�ά��
	            boolean reuprice_flg = result.getBoolean("REUPRICE_FLG", 0);
	            if (reuprice_flg) {
	                // �ж����յ����Ƿ����Լ������ͬ
	                boolean agent_flg = false;
	                for (int i = 0; i < newrows.length; i++) {
	                    // �жϸ��е�����״̬
	                    if (!dataStore.isActive(newrows[i])) {
	                        continue;
	                    }
	                     
	                    order_code = dataStore.getItemString(newrows[i],
	                        "ORDER_CODE");
	                    verprice = dataStore.getItemDouble(newrows[i],
	                        "VERIFYIN_PRICE");
	                    String sup_code = this.getValueString("SUP_CODE");
	                    TParm inparm = new TParm();
	                    inparm.setData("SUP_CODE", sup_code);
	                    inparm.setData("ORDER_CODE", order_code);
	                    TParm agent_parm = IndAgentTool.getInstance().onQuery(
	                        inparm);
	                    if (agent_parm.getDouble("CONTRACT_PRICE", 0) != verprice) {
	                        agent_flg = true;
	                    }
	                }
	                if (agent_flg) {
	                    if (this.messageBox("��ʾ", "�Ƿ��Զ�ά�����뵥��", 2) == 0) {
	                        parm.setData("REUPRICE_FLG", "Y");
	                    }
	                    else {
	                        parm.setData("REUPRICE_FLG", "N");
	                    }
	                }
	                else {
	                    parm.setData("REUPRICE_FLG", "N");
	                }
	            }
	            else {
	                parm.setData("REUPRICE_FLG", "N");
	            }
	            // �ж��Ƿ��Զ������һ��������ⵥ��ά����ҩƷ��������������
	            parm.setData("UPDATE_TRADE_PRICE", reuprice_flg ? "Y" : "N");
	
	            // ϸ����Ϣ
	            int count = 0;
	            // ������ⵥ��
	            verifyin_no = SystemTool.getInstance().getNo("ALL", "IND",
	                "IND_VERIFYIN", "No");
	            seq = 1;
	            String material_loc_code = "";
	            for (int i = 0; i < newrows.length; i++) {
	                // �жϸ��е�����״̬
	                if (!dataStore.isActive(newrows[i])) {
	                    continue;
	                }
	//                // ���ݲ�ͬ�ķ�Ʊ�����ɲ�ͬ����ⵥ��
	//                verifyin_no = getVerifyinNO(dataStore.getItemString(newrows[i],
	//                    "INVOICE_NO"));
	                // ��ⵥ�ż���
	                parm.setData("VERIFYIN", count, verifyin_no);
	                // ҩƷ���뼯��
	                order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
	                parm.setData("ORDER_CODE", count, order_code);  
	                // ���������ת����
	                getTRA = INDTool.getInstance().getTransunitByCode(order_code);
	                if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
	                    this.messageBox("ҩƷ" + order_code + "ת���ʴ���");   
	                    return;
	                }
	                TParm searchParm = new TParm();
	                searchParm.setData("SUP_CODE",getValueString("SUP_CODE"));
	                searchParm.setData("ORDER_CODE",order_code);
	                TParm supParm = INDTool.getInstance().getSupCode(searchParm);
	                if(supParm.getCount()<=0) {	
	                	parm.setData("SUP_ORDER_CODE", count, "");
	                	 // ���յ���
	                    dataStore.setItem(newrows[i], "SUP_ORDER_CODE", "");
	                }
	                if(supParm.getCount()>0) {
	                	 dataStore.setItem(newrows[i], "SUP_ORDER_CODE", supParm.getData("SUP_ORDER_CODE",0));
	                	 parm.setData("SUP_ORDER_CODE", count, supParm.getData("SUP_ORDER_CODE",0));
	                }			
	                // ����ת���ʼ���
	                s_qty = getTRA.getDouble("STOCK_QTY", 0);
	                parm.setData("STOCK_QTY", count, s_qty);
	                // ���ת���ʼ���
	                d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
	                parm.setData("DOSAGE_QTY", count, d_qty);
	                // �����
	                stock_qty = INDTool.getInstance().getStockQTY(org_code,
	                    order_code);
	                parm.setData("QTY", count, stock_qty);
	                // ���ż���
	                batch_no = dataStore.getItemString(newrows[i], "BATCH_NO");
	                //20150414 wangjingchun add start
	                if(batch_no.equals("")){
	                	if (this.messageBox("��ʾ��Ϣ Tips",
								"����Ϊ�գ��Ƿ������",
								this.YES_NO_OPTION) != 0){
	                		return;
	                	}
	                	batch_no = "x";
	                }
	                //20150414 wangjingchun add end
	                parm.setData("BATCH_NO", count, batch_no);
	                // ��Ч��
	                valid_date = StringTool.getString(dataStore.getItemTimestamp(
	                    newrows[i], "VALID_DATE"), "yyyy/MM/dd");
	                Date sys_date = new Date();
	                Date v_date = dataStore.getItemTimestamp(newrows[i], "VALID_DATE");
	                parm.setData("VALID_DATE", count, StringTool.getTimestamp(
	                    valid_date, "yyyy/MM/dd"));
	                // fux modify 20150810  
	                // ��������
	                man_date = StringTool.getString(dataStore.getItemTimestamp(
	                    newrows[i], "MAN_DATE"), "yyyy/MM/dd");
	                Date m_date = dataStore.getItemTimestamp(newrows[i], "MAN_DATE");
	                parm.setData("MAN_DATE", count, StringTool.getTimestamp(
	                		man_date, "yyyy/MM/dd"));                
	                
	                // �����Ч��������                                   
	                // fux modify 20141030  ����
	                // double verifyinPrice = dataStore.getItemDouble(newrows[i], "VERIFYIN_PRICE");
	                verprice = StringTool.round(verprice,4);      
					String verpriceDStr = String.valueOf(verprice);    
	                //searchParm.getValue("SUP_ORDER_CODE",0) Ϊ��     
					//fux  modify Ч�ڷ���  ��Ӧ���̲�ѯ������searchParm.getValue("SUP_ORDER_CODE",0) ��ind_stock��δд�� �Ƿ���ȷ���Ƿ�У����ֶ�
	                //�ֽ׶ν����������ȥ��  
	                getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeqBy(      
	                    org_code, order_code, batch_no, valid_date,verpriceDStr,getValueString("SUP_CODE"),searchParm.getValue("SUP_ORDER_CODE",0));
	                if (getSEQ.getErrCode() < 0) {
	                    this.messageBox("ҩƷ" + order_code + "������Ŵ���");
	                    return;  
	                }
	                if (getSEQ.getCount("BATCH_SEQ") > 0) {  
	                    flg = "U";
	                    // ������ҩƷ����
	                    batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
	                }
	                else {
	                    flg = "I";
	                    // ������ҩƷ������,ץȡ�ÿ��ҩƷ���+1�����������  
	                    getSEQ = IndStockDTool.getInstance()
	                        .onQueryStockMaxBatchSeq(org_code, order_code);
	                    if (getSEQ.getErrCode() < 0) {
	                        this.messageBox("ҩƷ" + order_code + "������Ŵ���");
	                        return;
	                    }
	                    // ���+1�������
	                    batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
	                    material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
	                    //this.messageBox(material_loc_code);
	                }
	                // �����Ч�������߼���
	                parm.setData("BATCH_SEQ", count, batch_seq);
	                // ��λ
	                parm.setData("MATERIAL_LOC_CODE", count, material_loc_code);
	                // ���»�����IND_STOCK��FLG����
	                parm.setData("UI_FLG", count, flg);
	                // ����������
	                qty = dataStore.getItemDouble(newrows[i], "VERIFYIN_QTY");
	                parm.setData("VERIFYIN_QTY", count, qty);
	                // ����������
	                g_qty = dataStore.getItemDouble(newrows[i], "GIFT_QTY");
	                parm.setData("GIFT_QTY", count, g_qty);
	                // �ƻ�������
	                pur_qty = resultParm.getDouble("PURORDER_QTY", newrows[i]);
	                parm.setData("PURORDER_QTY", count, pur_qty);
	                // ���ۼ۸񼯺�
	                retail = dataStore.getItemDouble(newrows[i], "RETAIL_PRICE");
	                parm.setData("RETAIL_PRICE", count, retail);
	                // ���ռ۸񼯺�
	                //zhangyong20091014 begin �޸����ռ۸�ĵ�λΪ��ҩ��λ  ��Ϊ���м۸�
	                verprice = dataStore.getItemDouble(newrows[i], "VERIFYIN_PRICE") ;
	                parm.setData("VERIFYIN_PRICE", count,
	                             StringTool.round(verprice, 4));                     
	                //zhangyong20091014 end              
	                // ������ż���                       
	                parm.setData("SEQ_NO", count, seq + count);
	                // �ۼ������ۿ����
	                deduct_atm = dataStore.getItemDouble(newrows[i],
	                    "QUALITY_DEDUCT_AMT");
	                parm.setData("QUALITY_DEDUCT_AMT", count, deduct_atm);
	                // �������ż���
	                pur_no = dataStore.getItemString(newrows[i], "PURORDER_NO");
	                parm.setData("PURORDER_NO", count, pur_no);
	                // ����������ż���
	                pur_no_seq = dataStore.getItemInt(newrows[i], "PURSEQ_NO");
	                parm.setData("PURSEQ_NO", count, pur_no_seq);
	                // ���ƽ���ۼ���
	                stock_price = resultParm.getDouble("STOCK_PRICE", newrows[i]);
	                parm.setData("STOCK_PRICE", count, stock_price);
	                // �ۼ�������
	                parm.setData("ACTUAL_QTY", count, resultParm.getDouble(
	                    "ACTUAL_QTY", newrows[i]));
	
	                // ���յ���
	                dataStore.setItem(newrows[i], "VERIFYIN_NO", verifyin_no);
	                // ���
	                dataStore.setItem(newrows[i], "SEQ_NO", seq + count);
	
	                // ״̬
	                if (check_flg && qty == pur_qty) {
	                    update_flg = "3";
	                }
	                else if (check_flg && qty < pur_qty) {
	                    update_flg = "1";
	                }
	                else if (!check_flg) {
	                    update_flg = "0";
	                }
	                dataStore.setItem(newrows[i], "UPDATE_FLG", update_flg);
	                //System.out.println("I_UPDATE_FLG:" + update_flg);
	                // OPT_USER,OPT_DATE,OPT_TERM
	                dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
	                dataStore.setItem(newrows[i], "OPT_DATE", date);
	                dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
	                dataStore.setItem(newrows[i], "BATCH_SEQ", batch_seq);// add by liyh 20120613
	                count++;
	            }
	            // �õ�dataStore�ĸ���SQL
	            String updateSql[] = dataStore.getUpdateSQL();
//	              for (int i = 0; i < updateSql.length; i++) {
//	                System.out.println(i + ":" + updateSql[i]);
//	            }     
	            parm.setData("UPDATE_SQL", updateSql);
	            // ִ����������(�������������ϸ��)
	            result = TIOM_AppServer.executeAction(
	                "action.spc.INDVerifyinAction", "onInsert", parm);
	
	            // �����ж�
	            if (result == null || result.getErrCode() < 0) {
	                this.messageBox("E0001");
	                return;
	            }
	            this.messageBox("P0001");
	
	            this.setValue("VERIFYIN_NO", verifyin_no);
	
	            //�������յ����ж�����������������״̬
	            updateIndVerifinDUpdateFlg(verifyin_no);
	
	            // add by wangbin 2014/12/1 ���ӱ�����Զ���ӡ���յ����ص��ж� START
	            String previewSwitch = IReportTool.getInstance().getPrintSwitch("VerifyInSave.prtSwitch");
	            if(StringUtils.equals(previewSwitch, IReportTool.ON)){
	                // ��ӡ������ⵥ
	                this.onPrint();
	            }
	            // add by wangbin 2014/12/1 ���ӱ�����Զ���ӡ���յ����ص��ж� END
	        }
	        else if ("updateM".equals(action)) {
	            // ����������Ϣ
	            if (!CheckDataM()) {
	                return;
	            }
	            // ����ֵȡ��
	            parm.setData("APPLY_TYPE", "1");
				parm.setData("DRUG_CATEGORY", "1");
	            String supCode = getValueString("SUP_CODE"); 
	            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
	            parm.setData("SUP_CODE", supCode);
	            parm.setData("PLAN_NO", getValue("PLAN_NO"));
	            parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
	            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
	            String check_flg = getValueString("CHECK_FLG");
	            if ("Y".equals(check_flg)) {
	                // �����Ա
	                parm.setData("CHECK_USER", Operator.getID());
	                // ���ʱ��
	                parm.setData("CHECK_DATE", date);
	                // ϸ����
	                TTable table_D = getTable("TABLE_D");
	                table_D.acceptText();
	                TDataStore dataStore = table_D.getDataStore();
	
	                // ����Ƿ���춯״̬�ж�
	                String org_code = parm.getValue("ORG_CODE");
	                if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
	                    this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
	                    return;
	                }
	
	                // ������
	                double qty = 0.00;
	                // ������
	                double g_qty = 0.00;
	                // ������
	                double pur_qty = 0.00;
	                // �����
	                double stock_qty = 0.00;
	                // ҩƷ����
	                String order_code = "";
	                // ���������ת����
	                TParm getTRA = new TParm();
	                // �����Ч��������
	                TParm getSEQ = new TParm();
	                // ����ת����
	                double s_qty = 0.00;
	                // ���ת����
	                double d_qty = 0.00;
	                // ����
	                String batch_no = "";
	                // �������
	                int batch_seq = 1;
	                // ��Ч��
	                String valid_date = "";
	                // ���»�����IND_STOCK��FLG->U:����;I,����
	                String flg = "";
	                // ���ۼ۸�
	                double retail = 0.00;
	                // ���յ���
	                double verprice = 0.00;
	                // �ۼ������ۿ���
	                double deduct_atm = 0.00;
	                // ���ƽ����
	                double stock_price = 0.00;
	                // ��������
	                String pur_no = "";
	                // �����������
	                int pur_no_seq = 0;
	                // ���յ���
	                String verifyin_no = this.getValueString("VERIFYIN_NO");
	
	                // �õ�IND_SYSPARM��Ϣ
	                result = IndSysParmTool.getInstance().onQuery();
	                // ���뵥���Զ�ά��
	                boolean reuprice_flg = result.getBoolean("REUPRICE_FLG", 0);
	                if (reuprice_flg) {
	                    // �ж����յ����Ƿ����Լ������ͬ
	                    boolean agent_flg = false;
	                    for (int i = 0; i < table_D.getRowCount(); i++) {
	                        // �жϸ��е�����״̬
	                        if (!dataStore.isActive(i)) {
	                            continue;
	                        }
	                        order_code = dataStore.getItemString(i, "ORDER_CODE");
	                        verprice = dataStore.getItemDouble(i, "VERIFYIN_PRICE");
	                        String sup_code = this.getValueString("SUP_CODE");
	                        TParm inparm = new TParm();
	                        inparm.setData("SUP_CODE", sup_code);
	                        inparm.setData("ORDER_CODE", order_code);
	                        TParm agent_parm = IndAgentTool.getInstance().onQuery(
	                            inparm);
	                        if (agent_parm.getDouble("CONTRACT_PRICE", 0) !=
	                            verprice) {
	                            agent_flg = true;
	                        }
	                    }
	                    if (agent_flg) {
	                        if (this.messageBox("��ʾ", "�Ƿ��Զ�ά�����뵥��", 2) == 0) {
	                            parm.setData("REUPRICE_FLG", "Y");
	                        }
	                        else {
	                            parm.setData("REUPRICE_FLG", "N");
	                        }
	                    }
	                    else {
	                        parm.setData("REUPRICE_FLG", "N");
	                    }
	                }
	                else {
	                    parm.setData("REUPRICE_FLG", "N");
	                }
	
	                // �ж��Ƿ��Զ������һ��������ⵥ��ά����ҩƷ��������������
	                parm.setData("UPDATE_TRADE_PRICE", reuprice_flg ? "Y" : "N");
	
	                // ϸ����Ϣ
	                String material_loc_code = "";
	                for (int i = 0; i < table_D.getRowCount(); i++) {
	                    // ��ⵥ�ż���
	                    parm.setData("VERIFYIN", i, verifyin_no);
	                    // ҩƷ���뼯��
	                    order_code = dataStore.getItemString(i, "ORDER_CODE");
	                    parm.setData("ORDER_CODE", i, order_code);
	                    // ���������ת����
	                    getTRA = INDTool.getInstance().getTransunitByCode(
	                        order_code);
	                    if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
	                        this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
	                        return;
	                    }
	                    
		                TParm searchParm = new TParm();
						searchParm.setData("SUP_CODE",getValueString("SUP_CODE"));
						searchParm.setData("ORDER_CODE",order_code);
						TParm supParm = INDTool.getInstance().getSupCode(searchParm);
						if(supParm.getCount()<=0) {	
							parm.setData("SUP_ORDER_CODE", i, "");
							 // ���յ���
						}
						if(supParm.getCount()>0) {
							 parm.setData("SUP_ORDER_CODE", i, supParm.getData("SUP_ORDER_CODE",0));
						}			
	                    				
	                    // ����ת���ʼ���
	                    s_qty = getTRA.getDouble("STOCK_QTY", 0);
	                    parm.setData("STOCK_QTY", i, s_qty);
	                    // ���ת���ʼ���
	                    d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
	                    parm.setData("DOSAGE_QTY", i, d_qty);
	                    // �����
	                    stock_qty = INDTool.getInstance().getStockQTY(org_code,
	                        order_code);
	                    parm.setData("QTY", i, stock_qty);
	                    // ���ż���
	                    batch_no = dataStore.getItemString(i, "BATCH_NO");
	                  //20150414 wangjingchun add start
	                    if(batch_no.equals("")){
	                    	if (this.messageBox("��ʾ��Ϣ Tips",
	    							"����Ϊ�գ��Ƿ������",
	    							this.YES_NO_OPTION) != 0){
	                    		return;
	                    	}
	                    	batch_no = "x";
	                    }
	                    //20150414 wangjingchun add end
	                    parm.setData("BATCH_NO", i, batch_no);
	                    // ��Ч��
	                    valid_date = StringTool.getString(dataStore
	                        .getItemTimestamp(i, "VALID_DATE"), "yyyy/MM/dd");
	                    //System.out.println("0000" + valid_date);
	                    parm.setData("VALID_DATE", i, valid_date);
	                    //*************************************************************
	                    //luahi 2012-01-10 add �����������ѡȡ�����������ռ۸�����begin
	                    //*************************************************************
	                    // �����Ч��������
	//                    getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeq(
	//                        org_code, order_code, batch_no, valid_date);
	                    //add by liyh 20120614 ��IND_STOCK ���ѯҩƷ���ռ۸�ʱ��Ӧ����Ƭ/֧�ļ۸���С��λ�ļ۸� start
	                    double verifyinPrice = dataStore.getItemDouble(i, "VERIFYIN_PRICE") ;
	                    double verpriceD = verifyinPrice /d_qty;										
	                    verpriceD =  StringTool.round(verpriceD, 4);
	                    String verpriceDStr = String.valueOf(verpriceD);
	                    //String verifyInPrice = dataStore.getItemString(i, "VERIFYIN_PRICE");
	                    //add by liyh 20120614 ��IND_STOCK ���ѯҩƷ���ռ۸�ʱ��Ӧ����Ƭ/֧�ļ۸���С��λ�ļ۸� end
	                     //�����Ч��������
	                    getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeqBy(
	                        org_code, order_code, batch_no, valid_date,verpriceDStr,supCode,supParm.getValue("SUP_ORDER_CODE",0));//
	                    //*************************************************************
	                    //luahi 2012-01-10 add �����������ѡȡ�����������ռ۸�����end
	                    //*************************************************************
	                    if (getSEQ.getErrCode() < 0) {
	                        this.messageBox("ҩƷ" + order_code + "������Ŵ���");
	                        return;
	                    }
	                    if (getSEQ.getCount("BATCH_SEQ") > 0) {
	                        flg = "U";
	                        // ������ҩƷ����
	                        batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
	                    }
	                    else {  
	                        flg = "I";
	                        // ������ҩƷ������,ץȡ�ÿ��ҩƷ���+1�����������
	                        getSEQ = IndStockDTool.getInstance()
	                            .onQueryStockMaxBatchSeq(org_code, order_code);
	                        if (getSEQ.getErrCode() < 0) {
	                            this.messageBox("ҩƷ" + order_code + "������Ŵ���");
	                            return;  
	                        }
	                        // ���+1�������
	//                        System.out.println("SEQ:"
	//                                           + getSEQ.getInt("BATCH_SEQ", 0));
	                        //System.out.println("getSEQ:" + getSEQ);
	                        batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
	                        material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE",
	                            0);
	                    }
	                    // �����Ч�������߼���
	                    parm.setData("BATCH_SEQ", i, batch_seq);
	                    // ��λ
	                    parm.setData("MATERIAL_LOC_CODE", i, material_loc_code);
	                    // ���»�����IND_STOCK��FLG����
	                    parm.setData("UI_FLG", i, flg);
	                    // ����������
	                    qty = dataStore.getItemDouble(i, "VERIFYIN_QTY");
	                    parm.setData("VERIFYIN_QTY", i, qty);
	                    // ����������
	                    g_qty = dataStore.getItemDouble(i, "GIFT_QTY");
	                    parm.setData("GIFT_QTY", i, g_qty);
	
	                    // ���ۼ۸񼯺�
	                    retail = dataStore.getItemDouble(i, "RETAIL_PRICE");
	                    parm.setData("RETAIL_PRICE", i, retail);
	                    // ���ռ۸񼯺�
	                    //zhangyong20091014 begin �޸����ռ۸�ĵ�λΪ��ҩ��λ
	                    parm.setData("VERIFYIN_PRICE", i,
	                                 StringTool.round(verpriceD, 4));
	                    
	                	//���ϼ۸�
	            		parm.setData("INVENT_PRICE",i,verifyinPrice);
	                    //zhangyong20091014 end
	                    // ������ż���
	                    int seq_no = dataStore.getItemInt(i, "SEQ_NO");
	                    parm.setData("SEQ_NO", i, seq_no);
	                    // �ۼ������ۿ����
	                    deduct_atm = dataStore.getItemDouble(i,
	                        "QUALITY_DEDUCT_AMT");
	                    parm.setData("QUALITY_DEDUCT_AMT", i, deduct_atm);
	                    // �������ż���
	                    pur_no = dataStore.getItemString(i, "PURORDER_NO");
	                    parm.setData("PURORDER_NO", i, pur_no);
	                    // ����������ż���
	                    pur_no_seq = dataStore.getItemInt(i, "PURSEQ_NO");
	                    parm.setData("PURSEQ_NO", i, pur_no_seq);
	
	                    TParm inparm = new TParm();
	                    inparm.setData("PURORDER_NO", pur_no);
	                    inparm.setData("SEQ_NO", pur_no_seq);
	                    result = IndPurorderMTool.getInstance().onQueryDone(inparm);
	                    if (result.getCount() == 0 || result.getErrCode() < 0) {
	                        return;
	                    }
	                    // ����������                      
	                    pur_qty = result.getDouble("PURORDER_QTY", 0);
	                    parm.setData("PURORDER_QTY", i, pur_qty);
	                    // ���ƽ���ۼ���
	                    stock_price = result.getDouble("STOCK_PRICE", 0);
	                    parm.setData("STOCK_PRICE", i, stock_price);     
	                    // �ۼ�������
	                    parm.setData("ACTUAL_QTY", i, result.getDouble(
	                        "ACTUAL_QTY", i));
	                    // ״̬
	                    String update_flg = "0";
	                    if ("Y".equals(check_flg) && qty == pur_qty) {
	                        update_flg = "3";
	                    }
	                    else if ("Y".equals(check_flg) && qty < pur_qty) {
	                        update_flg = "1";
	                    }
	                    else if (!"Y".equals(check_flg)) {
	                        update_flg = "0";
	                    }
	                    //System.out.println("U_UPDATE_FLG:" + update_flg);
	                    dataStore.setItem(i, "UPDATE_FLG", update_flg);
	                    // OPT_USER,OPT_DATE,OPT_TERM
	                    dataStore.setItem(i, "OPT_USER", Operator.getID());
	                    dataStore.setItem(i, "OPT_DATE", date);
	                    dataStore.setItem(i, "OPT_TERM", Operator.getIP());
	                    //luhai 2012-01-10 modify ����������ʱ��¼BATCH_SEQ begin
	                    dataStore.setItem(i, "BATCH_SEQ", batch_seq);
	                    //luhai 2012-01-10 modify ����������ʱ��¼BATCH_SEQ end
	                }
	                parm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
	            }
	            else {
	                parm.setData("CHECK_USER", "");
	                parm.setData("CHECK_DATE", tnull);
	            }
	            parm.setData("OPT_USER", Operator.getID());
	            parm.setData("OPT_DATE", date);
	            parm.setData("OPT_TERM", Operator.getIP());
	            parm.setData("REGION_CODE", Operator.getRegion());
	            parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
	            
	            // ִ�����ݸ���
	            result = TIOM_AppServer.executeAction(
	                "action.spc.INDVerifyinAction", "onUpdateInd", parm);
	            // �����ж�
	            if (result == null || result.getErrCode() < 0) {
	                this.messageBox("E0001");
	                return;
	            }
	            this.messageBox("P0001");
	
	            //�������յ����ж�����������������״̬
	            updateIndVerifinDUpdateFlg(getValueString("VERIFYIN_NO"));
	            //������ҩƷΪ��ҩʱ�ж��Ƿ������������
	            updateManCode(getValueString("VERIFYIN_NO"));
	            //�ж��в�ҩ����޸����ۼ�
	            //updateGrpricePrice(getValueString("VERIFYIN_NO"));
	
	        }
	        else if ("updateD".equals(action)) {
	            // ������ϸ��Ϣ
	            TTable table_D = getTable("TABLE_D");
	            table_D.acceptText();
	            TDataStore dataStore = table_D.getDataStore();
	            // ���resultParm
	            TParm inparm = new TParm();
	            inparm.setData("PURORDER_NO", dataStore.getItemString(0,
	                "PURORDER_NO"));
	            inparm.setData("SEQ_NO", dataStore.getItemInt(0, "PURSEQ_NO"));
	            resultParm = IndPurorderMTool.getInstance().onQueryDone(inparm);
	            // ϸ����
	            // if (!CheckDataD()) {
	            // return;
	            // }
	            // ���ȫ���Ķ����к�
	            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
	            // ���̶�����������
	            for (int i = 0; i < rows.length; i++) {
	                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
	                dataStore.setItem(rows[i], "OPT_DATE", date);
	                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
	            }
	            // ϸ����ж�
	            if (!table_D.update()) {
	                messageBox("E0001");
	                return;
	            }
	            messageBox("P0001");
	            table_D.setDSValue();
	            return;
	        }
	        this.onClear();
		}else{
	    	TTable table_D = getTable("TABLE_D");
	    	TParm parm = new TParm();
	    	TParm result = new TParm();
	    	int n=1;
	    	for(int i=0;i<table_D.getRowCount();i++){
//	    		System.out.println(table_D.getDataStore().isActive(i));
	    		if(table_D.getDataStore().isActive(i)){
	    			parm.addData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO"));
	    			parm.addData("ORDER_CODE", table_D.getDataStore().getRowParm(i).getValue("ORDER_CODE"));
	    			parm.addData("BATCH_NO", table_D.getDataStore().getRowParm(i).getValue("BATCH_NO"));
	    			parm.addData("VALID_DATE", table_D.getDataStore().getRowParm(i).getValue("VALID_DATE").substring(0, 10).replace("-", "/"));
	    			parm.addData("INVOICE_NO", table_D.getDataStore().getRowParm(i).getValue("INVOICE_NO"));
	    			parm.addData("INVOICE_DATE", table_D.getDataStore().getRowParm(i).getValue("INVOICE_DATE").substring(0, 10).replace("-", "/"));
	    			parm.addData("OPT_USER", Operator.getID());
	    			parm.addData("OPT_TERM", Operator.getIP());
	    			parm.setCount(n);
	    			n++;
	    		}
	    	}
	    	if(parm == null || parm.getCount()<=0){
	    		this.messageBox("û��Ҫ���ĵ����ݣ�");
	    		return;
	    	}
	    	// ִ�����ݸ���
	        result = TIOM_AppServer.executeAction(
	            "action.spc.INDVerifyinAction", "onUpdateInvoiceNo", parm);
	        // �����ж�
	        if (result == null || result.getErrCode() < 0) {
	            this.messageBox("E0001");
	            return;
	        }
	        this.messageBox("P0001");
	        table_D.setParmValue(new TParm());
	    }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            this.messageBox("��������ɲ���ɾ��");  
        }
        else {
            if (getTable("TABLE_M").getSelectedRow() > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����յ�", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    TParm parm = new TParm();
                    // ϸ����Ϣ
                    if (table_D.getRowCount() > 0) {
                        table_D.removeRowAll();
                        String deleteSql[] = table_D.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // ������Ϣ
                    parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.spc.INDVerifyinAction", "onDeleteM", parm);
                    // ɾ���ж�              
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    getTable("TABLE_M").removeRow(
                        getTable("TABLE_M").getSelectedRow());
                    this.messageBox("ɾ���ɹ�");
                } 
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    int row = table_D.getSelectedRow();  
                    table_D.removeRow(row);
                    // ϸ����ж�
                    if (!table_D.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_D.setDSValue();
                }
            }
        }
    }

    /**
     * ��δ������ϸ
     */
    public void onExport() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("���ղ��Ų���Ϊ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Object result = openDialog("%ROOT%\\config\\spc\\INDUnVerifyin.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            resultParm = (TParm) result;
            //System.out.println("RESULT" + resultParm);
            TTable table = getTable("TABLE_D");
            table.removeRowAll();
            double purorder_qty = 0;
            double actual_qty = 0;
            double puroder_price = 0;
            double retail_price = 0;
            TNull tnull = new TNull(Timestamp.class);
            // ��Ӧ����
            getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
            // �ƻ�����
            this.setValue("PLAN_NO", addParm.getValue("PLAN_NO", 0));
            for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
                int row = table.addRow();
                // ���DATESTORE
                
                String orderCode =  addParm.getValue("ORDER_CODE", i);
                // ORDER_CODE
                table.getDataStore().setItem(i, "ORDER_CODE",
                                             addParm.getValue("ORDER_CODE", i));
                
                
                TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
                
                //�а�װ
				int conversionTraio = phaParm.getInt("CONVERSION_RATIO", 0);
				conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
                
                // ���TABLE_D����
                // ������
                purorder_qty = addParm.getDouble("PURORDER_QTY", i);
                actual_qty = addParm.getDouble("ACTUAL_QTY", i);
                
                //����*�а�
                purorder_qty = purorder_qty * conversionTraio ;
                actual_qty = actual_qty * conversionTraio ;
                
                
                table.setItem(row, "VERIFYIN_QTY", purorder_qty - actual_qty);
                // ������
                table.setItem(row, "GIFT_QTY", addParm.getData("GIFT_QTY", i));
                // ������λ
                table.setItem(row, "BILL_UNIT", addParm
                              .getValue("BILL_UNIT", i));
                // ���յ���
                puroder_price = addParm.getDouble("PURORDER_PRICE", i);
                table.setItem(row, "VERIFYIN_PRICE", puroder_price);
                // �������
                table.setItem(row, "INVOICE_AMT", StringTool.round(
                    puroder_price * (purorder_qty - actual_qty), 2));
                // ���ۼ�
                retail_price = addParm.getDouble("RETAIL_PRICE", i);
                table.setItem(row, "RETAIL_PRICE", retail_price);
                // ��������
                table.setItem(row, "PURORDER_NO", addParm.getData(
                    "PURORDER_NO", i));
                // �����������
                table.setItem(row, "PURSEQ_NO", addParm.getData("SEQ_NO", i));
                // �ۼ�������
                table.setItem(row, "ACTUAL", addParm.getData("ACTUAL_QTY", i));
                // ��Ʊ����
                table.setItem(row, "INVOICE_DATE", tnull);
                // �ж�ҩƷ����:�вݣ��гɣ���ҩ
                TParm pha_parm = new TParm(TJDODBTool.getInstance().select(
                    INDSQL.getPHAInfoByOrder(addParm.getValue("ORDER_CODE",
                    i))));
                // ����ҩƷ���͸���Ч��
                if ("G".equals(pha_parm.getValue("PHA_TYPE", 0))) {
                    table.setItem(row, "VALID_DATE", "9999/12/31");
                }
                else {
                    table.setItem(row, "VALID_DATE", tnull);
                }
                //��������
                table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
                table.getDataStore().setActive(row, false);
            }
            table.setDSValue();
            table.setLockColumns("1,2,5,7,9,17,18,19");//20150612 wangjc add
            getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            action = "insert";
            this.setCheckFlgStatus(action);
            //zhangyong20110517
            ( (TMenuItem) getComponent("onExport")).setEnabled(false);
        }
    }
    
    
    /**
     * �����ҩ������
     * @date 20120828
     * @author liyh
     */
    public void onImpXML(){
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("���ղ��Ų���Ϊ��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);
		
		if (result != null) {
            TParm fileParm = (TParm) result;
            if (fileParm == null) {
                return;
            }
            String filePath = (String) fileParm.getData("PATH", 0);
            TParm addParm = new TParm();
			try {
				addParm = (TParm) FileUtils.readXMLFileP(filePath);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultParm = (TParm) addParm;
            TTable table = getTable("TABLE_D");
            table.removeRowAll();
            double purorder_qty = 0;
            double actual_qty = 0;
            double puroder_price = 0;
            double retail_price = 0;
            // ��Ӧ����
            getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
            // �ƻ�����
            this.setValue("PLAN_NO", "");
            for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
                int row = table.addRow();
                
               
                TParm phaParm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getPHABaseInfo(addParm.getValue("ORDER_CODE",
                        i))));
//                System.out.println("--phaParm:"+phaParm);
                // ���DATESTORE
                resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
                // ORDER_CODE
                table.getDataStore().setItem(i, "ORDER_CODE",
                                             addParm.getValue("ORDER_CODE", i));
                // ���TABLE_D����
                // ������
                purorder_qty = addParm.getDouble("PURORDER_QTY", i);
                table.setItem(row, "VERIFYIN_QTY", purorder_qty);
                // ������
                table.setItem(row, "GIFT_QTY", 0);
                // ������λ
//                System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT", 0));
                table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));
                // ���յ���
                puroder_price = addParm.getDouble("PURORDER_PRICE", i);
                table.setItem(row, "VERIFYIN_PRICE", puroder_price);
                // �������
                table.setItem(row, "INVOICE_AMT", StringTool.round(puroder_price * purorder_qty, 2));
                // ���ۼ�
                retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
                table.setItem(row, "RETAIL_PRICE", retail_price);
                // ��������
                table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
                // �����������
                table.setItem(row, "PURSEQ_NO",addParm.getData("PURSEQ_NO", i));
                // �ۼ�������
                table.setItem(row, "ACTUAL", 0);
      
                String time1 = addParm.getData("INVOICE_DATE", i)+"";
                time1 = time1.replaceAll("-", "/");
                // ��Ʊ����
                table.setItem(row, "INVOICE_DATE",time1);
                String validDate = addParm.getData("VALID_DATE", i)+"";
                validDate = validDate.replaceAll("-", "/");
                table.setItem(row, "REASON_CHN_DESC", "VER01");
                //Ч��
                table.setItem(row, "VALID_DATE", validDate);
                //��������
                table.setItem(row, "MAN_CODE", phaParm.getData("MAN_CODE", 0));
                //��Ʊ��
                table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
                //����
                table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
                
                table.getDataStore().setActive(row, false);
            }
            table.setDSValue();
            getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            action = "insert";
            this.setCheckFlgStatus(action);
           
            //zhangyong20110517
//            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }		
    }

    /**
     * ��ӡ������ⵥ
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("VERIFYIN_NO"))) {
            this.messageBox("���������յ�");
            return;
        }
        if (table_d.getRowCount() > 0) {  
            // ��ӡ����
            TParm date = new TParm(); 
            // ��ͷ����
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "������ⵥ");
            date.setData("VER_NO", "TEXT",
                         "��ⵥ��: " + this.getValueString("VERIFYIN_NO"));
            date.setData("SUP_CODE", "TEXT",
                         "��������: " +
                         this.getTextFormat("SUP_CODE").getText());
            date.setData("ORG_CODE", "TEXT",
                         "���ղ���: " +
                         this.getComboBox("ORG_CODE").getSelectedName());
            
            // modify by wangbin 20141128 �������û���Զ���ӡ��ⵥ START
			String verifyinDate = this.getValueString("VERIFYIN_DATE");
			if (StringUtils.isNotEmpty(verifyinDate)) {
				verifyinDate = verifyinDate.substring(0, 10).replace('-', '/');
			}
			date.setData("DATE", "TEXT", "�����������: " + verifyinDate);
            // modify by wangbin 20141128 �������û���Զ���ӡ��ⵥ END
            // �������
            TParm parm = new TParm();
            String order_code = "";
            double ver_sum = 0;
            double own_sum = 0;
            double diff_sum = 0;
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                parm.addData("INVOICE_NO",
                             table_d.getItemString(i, "INVOICE_NO"));
                String invoidDate = table_d.getItemString(i,"INVOICE_DATE");
                if(null != invoidDate && invoidDate.length()>=10){
                	parm.addData("INVOICE_DATE", table_d.getItemString(i,"INVOICE_DATE").substring(0, 10).replace('-', '/'));
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code,
                                       this.getValueString("SUP_CODE"), "VER")));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return;
                }  
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                //luhai 2012-2-28 modify ��������ȡ1λ begin
//                parm.addData("VERIFYIN_QTY", df3.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
                parm.addData("VERIFYIN_QTY", df5.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
                //luhai 2012-2-28 modify ��������ȡ1λ end             
                parm.addData("VERIFYIN_PRICE",
                             df4.format(StringTool.round(table_d.getItemDouble(
                                 i, "VERIFYIN_PRICE"), 4)));
                double ver_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") *
                    table_d.getItemDouble(i, "VERIFYIN_PRICE");
                parm.addData("VER_AMT", df2.format(StringTool.round(ver_atm, 2)));
                ver_sum += ver_atm;
                parm.addData("OWN_PRICE",
                             df4.format(StringTool.round(table_d.
                    getItemDouble(i, "RETAIL_PRICE"), 4)));
                double own_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") *
                    table_d.getItemDouble(i, "RETAIL_PRICE");
                parm.addData("OWN_AMT", df2.format(StringTool.round(own_atm, 2)));
                own_sum += own_atm;
                parm.addData("DIFF_AMT", df2.format(StringTool.round(own_atm - ver_atm, 2)));
                diff_sum += own_atm - ver_atm;
                parm.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
                parm.addData("VALID_DATE",
                             table_d.getItemString(i, "VALID_DATE").substring(0,
                    10).replace('-', '/'));
                String pha_type = inparm.getValue("PHA_TYPE", 0);
                if ("W".equals(pha_type)) {
                    parm.addData("PHA_TYPE", "��ҩ");
                }
                else if ("C".equals(pha_type)) {
                    parm.addData("PHA_TYPE", "�г�ҩ");
                }
                else if ("G".equals(pha_type)) {
                    parm.addData("PHA_TYPE", "�в�ҩ");
                }
                //��������
                TParm manparm = new TParm(TJDODBTool.getInstance().select(
                    SYSSQL.getSYSManufacturer(table_d.getItemString(i,
                    "MAN_CODE"))));
                parm.addData("MAN_CODE", table_d.getItemString(i,
        		"MAN_CODE"));			
                if ("Y".equals(inparm.getValue("BID_FLG", 0))) {
                    parm.addData("BID_FLG", "��");
                }
                else {
                    parm.addData("BID_FLG", "��");
                }
            }
            if (parm.getCount("ORDER_DESC") == 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            int row = parm.getCount("ORDER_DESC");
            //luhai 2012-3-7 ɾ���ϼƲ��ָ���ͼ��ʵ�� begin
//            // �ϼƲ���
//            parm.addData("INVOICE_NO", "�ϼ�");
//            parm.addData("INVOICE_DATE", "");
//            parm.addData("ORDER_DESC", "������" + row);
//            parm.addData("INVOICE_NO", "�ϼ�");
//            parm.addData("SPECIFICATION", "");
//            parm.addData("UNIT", "");
//            parm.addData("VERIFYIN_QTY", "");
//            parm.addData("VERIFYIN_PRICE", "�ɹ����");
//            parm.addData("VER_AMT", df2.format(StringTool.round(ver_sum, 2)));
//            parm.addData("OWN_PRICE", "���۽��");
//            parm.addData("OWN_AMT", df2.format(StringTool.round(own_sum, 2)));
//            //luhai 2012-01-23 modify ɾ��������� begin
////            parm.addData("DIFF_AMT", "�������");
////            parm.addData("BATCH_NO", df2.format(StringTool.round(diff_sum, 2)));
//            parm.addData("DIFF_AMT", "");
//            parm.addData("BATCH_NO", "");
//            //luhai 2012-01-23 modify ɾ��������� end
//            parm.addData("VALID_DATE", "");
//            parm.addData("PHA_TYPE", "");
//            parm.addData("MAN_CODE", "");
//            parm.addData("BID_FLG", "");

          //luhai 2012-3-7 ɾ���ϼƲ��ָ���ͼ��ʵ�� begin
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "INVOICE_NO");
            //luhai delete 2012-2-11 begin
//            parm.addData("SYSTEM", "COLUMNS", "INVOICE_DATE");
            //luhai delete 2012-2-11 end
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            //fux modify 20141030
//            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");   
//            parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//            parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_QTY");     
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "VER_AMT");  
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");  
            parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
            //luhai 2012-01-11 modify ɾ���������� begin
//            parm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
            //luhai 2012-01-11 modify ɾ���������� end  
            //luhai 2012-01-11 delete begin
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");//20150210 wangjingchun modify
            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");//20150210 wangjingchun modify
//            parm.addData("SYSTEM", "COLUMNS", "PHA_TYPE");
//            parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
//            parm.addData("SYSTEM", "COLUMNS", "BID_FLG");  
            //luhai 2012-01-11 delete end
//            System.out.println("PARM---" + parm);    
            date.setData("TABLE", parm.getData());
            // ��β����   
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            //luhai 2012-3-7 begin ����ϼƲ���
            date.setData("VER_AMT", "TEXT", df2.format(StringTool.round(ver_sum, 2)));//���ռ۸�
            date.setData("OWN_AMT", "TEXT", df2.format(StringTool.round(own_sum, 2)));//���ۼ۸�
            
            //modify by yangjj 20150415�����ϼƣ���д�����ĳɡ��ɹ����ϼƣ���д����
            date.setData("VER_AMT_CHN", "TEXT","�ɹ����ϼƣ���д����"+StringUtil.getInstance().numberToWord( Double.parseDouble(df2.format(StringTool.round(ver_sum, 2)))));//���մ�д
            //luhai 2012-3-7 end
            
            // add by wangbin 20140929 ���󵥺�936 START
            //modify by yangjj 20150415���������Ρ��ĳɡ����Ρ�
            date.setData("ATTN", "TEXT", "����: " );       
            // add by wangbin 20140929 ���󵥺�936 END  
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\VerifyIn.jhw",
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
            setValue("VERIFYIN_DATE", table.getItemTimestamp(row,
                "VERIFYIN_DATE"));
            setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
            setValue("VERIFYIN_NO", table.getItemString(row, "VERIFYIN_NO"));
            setValue("PLAN_NO", table.getItemString(row, "PLAN_NO"));
            setValue("SUP_CODE", table.getItemString(row, "SUP_CODE"));
            setValue("REASON_CODE", table.getItemString(row, "REASON_CHN_DESC"));
            setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
            String checkFlg = table.getItemString(row, "CHECK_USER");
            if (!"".equals(checkFlg)) {
                setValue("CHECK_FLG", "Y");
            }
            else {
                setValue("CHECK_FLG", "N");
                
            }
            // �趨ҳ��״̬  
            getTextField("VERIFYIN_NO").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("onExport")).setEnabled(false);
            action = "updateM";
            this.setCheckFlgStatus(action);

            // ��ѯѡ�����յ�������Ӧ��ϸ��
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            String sql = INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")) ;
            tds.setSQL(sql);
            tds.retrieve();
            if (tds.rowCount() == 0) {
                this.messageBox("û�ж�����ϸ");
            }
            // �۲���
            IndVerifyinObserver obser = new IndVerifyinObserver();
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            table_D.setDSValue();
            //20150612 wngjc add start
            if(getRadioButton("UPDATE_FLG_B").isSelected()){
            	//fux modify 20150810  
            	//table_D.setLockColumns("1,2,5,7,9,17,18,19");
            	table_D.setLockColumns("1,2,5,7,9,18,19,20");  
            }else{
//            	table_D.setLockColumns("1,2,3,4,5,6,7,8,9,10,12,13,14,15,"
//            			+ "16,17,18,19,20");
//            	table_D.setLockColumns("1,2,3,4,5,6,7,8,9,10,12,13,14,15,"  
//            			+ "16,17,18,19,20,21");  
            	table_D.setLockColumns("1,2,3,4,5,6,7,8,9,10,13,14,15,"  
            			+ "16,17,18,19,20,21");//modify by wangjc �����޸ķ�Ʊ��
            }
            //20150612 wngjc add end
            this.setValue("SUM_VERIFYIN_PRICE", getSumVerifinMoney());
            this.setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            this.setValue("PRICE_DIFFERENCE",
                          getSumRetailMoney() - getSumVerifinMoney());
        }
    }

    /**
     * ��ϸ���(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            // ������Ϣ
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            if (getTextField("VERIFYIN_NO").isEnabled()) {
                action = "insert";
            }
            else {
                action = "updateD";
            }
            this.setCheckFlgStatus(action);

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
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
    	if (getRadioButton("UPDATE_FLG_B").isSelected()) {
	        // ֵ�ı�ĵ�Ԫ��
	        TTableNode node = (TTableNode) obj;
	        if (node == null)
	            return false;
	        // �ж����ݸı�
	        if (node.getValue() != null && node.getOldValue() != null &&
	            node.getValue().equals(node.getOldValue()))
	            return true;
	        // Table������
	        TTable table = node.getTable();
	        String columnName = table.getDataStoreColumnName(node.getColumn());
	        int row = node.getRow();
	        if ("VERIFYIN_QTY".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            if (qty <= 0) {
	                this.messageBox("������������С�ڻ����0");
	                return true;
	            }
	            double amt = StringTool.round(qty
	                                          *
	                                          table.getItemDouble(row, "VERIFYIN_PRICE"),
	                                          2);
	            table.setItem(row, "INVOICE_AMT", amt);
	            return false;
	        }
	        if ("GIFT_QTY".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            if (qty < 0) {
	                this.messageBox("����������С��0");
	                return true;
	            }
	            return false;
	        }
	        if ("VERIFYIN_PRICE".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            
	            //modify by yangjj 20150306 ȥ������0����
	            if (qty < 0) {
	                this.messageBox("���յ��۲���С��0");
	                return true;
	            }
	            double amt = StringTool.round(qty
	                                          *
	                                          table.getItemDouble(row, "VERIFYIN_QTY"),
	                                          2);
	            table.setItem(row, "INVOICE_AMT", amt);
	            return false;
	        }
	        if ("QUALITY_DEDUCT_AMT".equals(columnName)) {
	            double qty = TypeTool.getDouble(node.getValue());
	            if (qty < 0) {
	                this.messageBox("Ʒ�ʿۿ��С��0");
	                return true;
	            }
	        }
	        // ��Ʊ����
	        if ("INVOICE_NO".equals(columnName) && row == 0
	        		&& getRadioButton("UPDATE_FLG_B").isSelected()) {//20150528 wangjc modify
	            for (int i = 0; i < table.getRowCount(); i++) {
	                table.setItem(i, "INVOICE_NO", node.getValue());
	            }
	        }
	        // ��Ʊ����
	        if ("INVOICE_DATE".equals(columnName) && row == 0
	        		&& getRadioButton("UPDATE_FLG_B").isSelected()) {//20150528 wangjc modify
	            for (int i = 0; i < table.getRowCount(); i++) {
	                table.setItem(i, "INVOICE_DATE", node.getValue());
	            }
	            return false;
	        }
	        String order_code = table.getItemString(row, "ORDER_CODE");
	        String sql = "SELECT PHA_TYPE FROM PHA_BASE WHERE ORDER_CODE = '" +
	            order_code + "' ";
	       
	        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	        String pha_type = parm.getValue("PHA_TYPE", 0);
	        // ��������
	        if ("MAN_CODE".equals(columnName)) {
	            if (!"G".equals(pha_type)) {
	                this.messageBox("��ҩƷ���в�ҩ�������޸���������!");
	                return true;
	            }
	        }
	        // ���ۼ�
	        if ("RETAIL_PRICE".equals(columnName)) {
	            if (!"G".equals(pha_type)) {
	                this.messageBox("��ҩƷ���в�ҩ�������޸����ۼ۸�!");
	                return true;
	            }
	            else {
	                double own_price = TypeTool.getDouble(node.getValue());
	                if (own_price <= 0) {
	                    this.messageBox("���۵��۲���С�ڻ����0");
	                    return true;
	                }
	                double amt = StringTool.round(own_price *
	                                              (table.getItemDouble(row,
	                    "VERIFYIN_QTY") + table.getItemDouble(row,
	                    "GIFT_QTY")), 2);
	                table.setItem(row, "SELL_SUM", amt);
	                return false;
	            }
	        }
	        // Ч��
	        Date date = StringTool.getDate((Timestamp)node.getValue());
	        Date sys_date = StringTool.getTimestamp(new Date());
	        if ("VALID_DATE".equals(columnName)) {
				if (sys_date.after(date)) {
					messageBox("��Ч���ѹ���");
					return true;
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
        // ���ѡ�е���
        int row = tableDown.getSelectedRow();
        if (column == 0) {
            if (tableDown.getDataStore().isActive(row)) {
                // ���������ܽ��
                double amount1 = tableDown.getItemDouble(row, "VERIFYIN_QTY");
                double amount2 = tableDown.getItemDouble(row, "GIFT_QTY");
                double sum = tableDown.getItemDouble(row, "RETAIL_PRICE")
                    * (amount1 + amount2);
                double sum_atm = this.getValueDouble("SUM_RETAIL_PRICE");
                this.setValue("SUM_RETAIL_PRICE", StringTool.round(sum
                    + sum_atm, 2));
                // ��������ܽ��
                double ver_amt = this.getValueDouble("SUM_VERIFYIN_PRICE");
                double amt = tableDown.getItemDouble(row, "INVOICE_AMT");
                this.setValue("SUM_VERIFYIN_PRICE", StringTool.round(ver_amt
                    + amt, 2));
            }
            else {
                // ���������ܽ��
                double amount1 = tableDown.getItemDouble(row, "VERIFYIN_QTY");
                double amount2 = tableDown.getItemDouble(row, "GIFT_QTY");
                double sum = tableDown.getItemDouble(row, "RETAIL_PRICE")
                    * (amount1 + amount2);
                double sum_atm = this.getValueDouble("SUM_RETAIL_PRICE");
                if (sum_atm - sum < 0) {
                    this.setValue("SUM_RETAIL_PRICE", 0);
                }
                else {
                    this.setValue("SUM_RETAIL_PRICE", StringTool.round(sum_atm
                        - sum, 2));
                }
                // ��������ܽ��
                double ver_amt = this.getValueDouble("SUM_VERIFYIN_PRICE");
                double amt = tableDown.getItemDouble(row, "INVOICE_AMT");
                if (ver_amt - amt < 0) {
                    this.setValue("SUM_VERIFYIN_PRICE", 0);
                }
                else {
                    this.setValue("SUM_VERIFYIN_PRICE", StringTool.round(
                        ver_amt - amt, 2));
                }
            }
            // �������
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getValueDouble("SUM_RETAIL_PRICE")
                - getValueDouble("SUM_VERIFYIN_PRICE"), 2));
        }
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
                table.getDataStore().setActive(i, true);
                table.getDataStore().setItem(i, "UPDATE_FLG", "0");
                this.setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
                this.setValue("SUM_VERIFYIN_PRICE", getSumVerifinMoney());
                this.setValue("PRICE_DIFFERENCE", StringTool.round(
                    getSumRetailMoney() - getSumVerifinMoney(), 2));
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getDataStore().setActive(i, false);
                table.getDataStore().setItem(i, "UPDATE_FLG", "");
                this.setValue("SUM_RETAIL_PRICE", 0);
                this.setValue("SUM_VERIFYIN_PRICE", 0);
                this.setValue("PRICE_DIFFERENCE", 0);
            }
        }
        table.setDSValue();
    }

    /**
     * ��ѡ��ı��¼�
     */
    public void onChangeRadioButton() {
        if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
//            ( (TMenuItem) getComponent("save")).setEnabled(false);
        	( (TMenuItem) getComponent("save")).setEnabled(true);//add by wangjc ����˵����ݿ����޸ķ�Ʊ�źͷ�Ʊ����
            ( (TMenuItem) getComponent("onExport")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("onExport")).setEnabled(true);
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
            table_d.setLockColumns("1,2,4,5,7,9,10,18,19,20");
            gift_flg = false;
        }
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }
        // ������Ȩ��
        if (!this.getPopedem("checkEnabled")) {
            getCheckBox("CHECK_FLG").setEnabled(false);
            check_flg = false;
        }

        // ��������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // ��ʼ������ʱ��
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("VERIFYIN_DATE", date);
        // ��ʼ������״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ��ʼ��TABLE_D
        TTable table_D = getTable("TABLE_D");
        table_D.removeRowAll();
        table_D.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
        tds.retrieve();
        if (tds.rowCount() == 0) {
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }
        IndVerifyinObserver obser = new IndVerifyinObserver();
        tds.addObserver(obser);
        table_D.setDataStore(tds);
        table_D.setDSValue();
        getComboBox("ORG_CODE").setSelectedIndex(1);
     
        action = "insert";
        this.setCheckFlgStatus(action);
        resultParm = new TParm();
    }

    /**
     * �趨�������״̬
     * @param action String
     */
    private void setCheckFlgStatus(String action) {
        if ("insert".equals(action)) {
            if (check_flg) {
                TTable table_M = getTable("TABLE_M");
                if (table_M.getSelectedRow() >= 0) {
                    getCheckBox("CHECK_FLG").setEnabled(true);
                }
                else {
                    getCheckBox("CHECK_FLG").setEnabled(false);
                }
            }
            else {
                getCheckBox("CHECK_FLG").setEnabled(false);
            }
        }
        else if ("updateM".equals(action)) {
            if (check_flg) {
                getCheckBox("CHECK_FLG").setEnabled(true);
            }
            else {
                getCheckBox("CHECK_FLG").setEnabled(false);
            }
        }
        else if ("updateD".equals(action)) {
            getCheckBox("CHECK_FLG").setEnabled(false);
        }
    }

    /** 
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("���ղ��Ų���Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        // �ж�ϸ���Ƿ��б�ѡ����
        TTable table = getTable("TABLE_D");
        table.acceptText();
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getDataStore().isActive(i)) {
                count++;
                // �ж�������ȷ��
                double qty = table.getItemDouble(i, "VERIFYIN_QTY");
                if (qty <= 0) {
                    this.messageBox("������������С�ڻ����0");
                    return false;
                }
                double pur_qty = resultParm.getDouble("PURORDER_QTY", i);
                String orderCode = resultParm.getValue("ORDER_CODE",i);
                TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
                
                //�а�װ
				int conversionTraio = phaParm.getInt("CONVERSION_RATIO", 0);
				conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
				
				pur_qty = pur_qty * conversionTraio ;
                
                if (qty > pur_qty) {
                    this.messageBox("�����������ܴ��ڶ�����");
                    return false;
                }
                qty = table.getItemDouble(i, "GIFT_QTY");
                if (qty < 0) {
                    this.messageBox("����������С��0");
                    return false;
                }
                qty = table.getItemDouble(i, "VERIFYIN_PRICE");
                
                //modify by yangjj 20150316 ȥ������С��0����
                if (qty < 0) {
                    this.messageBox("���յ��۲���С��0");
                    return false;
                }
                // ��Ʊ����
//                if ("".equals(table.getItemString(i, "INVOICE_NO"))) {
//                    this.messageBox("��Ʊ���벻��Ϊ��");
//                    return false;
//                }
                // ��Ʊ����
                if (table.getItemTimestamp(i, "INVOICE_DATE") == null||table.getItemTimestamp(i, "INVOICE_DATE").equals(new TNull(Timestamp.class))) {
                    this.messageBox("��Ʊ���ڲ���Ϊ��");
                    return false;
                }
                
                // fux modify 20150810 ��������
                if (table.getItemTimestamp(i, "MAN_DATE") == null||table.getItemTimestamp(i, "MAN_DATE").equals(new TNull(Timestamp.class))) {
                    this.messageBox("�������ڲ���Ϊ��");                      
                    return false;
                }
                
                // ����,��Ʊ����
                if ("".equals(table.getItemString(i, "BATCH_NO"))) {
//                    this.messageBox("���Ų���Ϊ��");
                  //20150414 wangjingchun add start
                    if (this.messageBox("��ʾ��Ϣ Tips",
    							"����δ��д���Ƿ����?",
    							this.YES_NO_OPTION) != 0){
                    	return false;
                    }else{
                    	table.setItem(i, "BATCH_NO", "x");
                    }
                    //20150414 wangjingchun add end
//                    return false;
                }
                // Ч��
                if (table.getItemTimestamp(i, "VALID_DATE") == null ||
                    table.getItemTimestamp(i, "VALID_DATE").equals(new TNull(Timestamp.class))) {
                    this.messageBox("Ч�ڲ���Ϊ��");
                    return false;
                }
                // Ʒ�ʿۿ�
                qty = table.getItemDouble(i, "QUALITY_DEDUCT_AMT");
                if (qty < 0) {
                    this.messageBox("Ʒ�ʿۿ��С��0");
                    return false;
                }
            }
        }
        if (count == 0) {
            this.messageBox("û��ִ������");
            return false;
        }
        return true;
    }

    /**
     * ȡ�����յ���
     *
     * @return String
     */
    private String getVerifyinNO(String invoiceNo) {
        String verifyinNo = "";
        if (map.containsKey(invoiceNo)) {
            verifyinNo = (String) map.get(invoiceNo);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getVerifyinDByNo(verifyinNo));
            tds.retrieve();
            seq++;
        }
        else {
            verifyinNo = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_VERIFYIN", "No");
            map.put(invoiceNo, verifyinNo);
            seq = 1;
        }
        return verifyinNo;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                double amount1 = table.getItemDouble(i, "VERIFYIN_QTY");
                double amount2 = table.getItemDouble(i, "GIFT_QTY");
                sum += table.getItemDouble(i, "RETAIL_PRICE")
                    * (amount1 + amount2);
            }
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ��������ܽ��
     *
     * @return
     */
    private double getSumVerifinMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                sum += table.getItemDouble(i, "INVOICE_AMT");
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

    //�������յ����ж�����������������״̬
    private void updateIndVerifinDUpdateFlg(String verifyin_no) {
        String sql = "SELECT A.PURORDER_QTY + A.GIFT_QTY AS QTY, "
            + " B.VERIFYIN_QTY, B.VERIFYIN_NO, B.SEQ_NO "
            + " FROM IND_PURORDERD A, IND_VERIFYIND B, IND_VERIFYINM C "
            + " WHERE A.PURORDER_NO = B.PURORDER_NO "
            + " AND A.SEQ_NO = B.PURSEQ_NO "
            + " AND B.VERIFYIN_NO = C.VERIFYIN_NO "
            + " AND A.PURORDER_QTY = B.VERIFYIN_QTY "
            + " AND C.CHECK_DATE IS NOT NULL "
            + " AND B.VERIFYIN_NO = '" + verifyin_no + "' ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        String update_sql = "UPDATE IND_VERIFYIND SET UPDATE_FLG = '";
        String update_where = "' WHERE VERIFYIN_NO = '" + verifyin_no +
            "' AND SEQ_NO = ";
        String update_flg = "1";
        String sql_list = "";
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
            if (parm.getDouble("VERIFYIN_QTY", i) >= parm.getDouble("QTY", i)) {
                update_flg = "3";
            }
            else {
                update_flg = "1";
            }
            sql_list = update_sql + update_flg + update_where +
                parm.getInt("SEQ_NO", i);
            result = new TParm(TJDODBTool.getInstance().update(sql_list));
        }
    }

    /**
     * ������ҩƷΪ��ҩʱ�ж��Ƿ������������
     * @param verifyin_no String
     */
    private void updateManCode(String verifyin_no) {
        String sql = " SELECT A.ORDER_CODE, A.MAN_CODE , B.MAN_CHN_DESC "
            + " FROM IND_VERIFYIND A, PHA_BASE B "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND B.PHA_TYPE = 'G' AND A.VERIFYIN_NO = '" + verifyin_no +
            "' ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
            return;
        }
        List list = new ArrayList();
        String man_code = "";
        String order_code = "";
        String sql_1 = "";
        String sql_2 = "";
        String sql_3 = "";
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            if (parm.getValue("MAN_CHN_DESC").equals(parm.getValue("MAN_CODE", i))) {
                continue;
            }
            else {
                order_code = parm.getValue("ORDER_CODE", i);
                man_code = parm.getValue("MAN_CODE", i);
                sql_1 = "UPDATE PHA_BASE SET MAN_CHN_DESC = '" + man_code +
                    "' WHERE ORDER_CODE = '" + order_code + "' ";
                sql_2 = "UPDATE SYS_FEE SET MAN_CODE = '" + man_code +
                    "' WHERE ORDER_CODE = '" + order_code + "' ";
                sql_3 = "UPDATE SYS_FEE_HISTORY SET MAN_CODE = '" + man_code +
                    "' WHERE ORDER_CODE = '" + order_code +
                    "' AND ACTIVE_FLG = 'Y'";
                list.add(sql_1);
                list.add(sql_2);
                list.add(sql_3);
            }
        }
        if (list == null || list.size() <= 0) {
            return;
        }
        else {
            String[] sql_list = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                sql_list[i] = list.get(i).toString();
            }
            TParm result = new TParm(TJDODBTool.getInstance().update(sql_list));
            if (result == null || result.getErrCode() < 0) {
                return;
            }
            else {
                TIOM_Database.logTableAction("SYS_FEE");
            }
        }
    }
}
