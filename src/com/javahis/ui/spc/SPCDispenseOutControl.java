package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCDispenseOutTool;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����װ�����
 * </p>
 *
 * <p>
 * Description: ����װ�����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author Yuanxm 2012.12.4
 * @version 1.0
 */
public class SPCDispenseOutControl extends TControl  {
	
	 // ������
    private TTable table_m;
    
    //δװ��
    TTable table_N;
    
    //��װ��
	TTable table_Y;
	
	//��ת��
	String boxEslId;
	
	//��λ���ӱ�ǩ
	String eletagCode;
	
	//���뵥��
    private String request_no;

    //���벿��
    private String app_org;

    //��������
    private String request_type;

    //���ؽ����
    private TParm resultParm;
    
    //ȫԺҩ�ⲿ����ҵ����
    private boolean request_all_flg = true;

    // ���ⲿ��
    private String out_org_code;

    // ��ⲿ��
    private String in_org_code;

    // �Ƿ����
    private boolean out_flg;

    // �Ƿ����
    private boolean in_flg;

    // ���ⵥ��
    private String dispense_no;
    
    // ʹ�õ�λ
    private String u_type;
    
    private int row_m  ;
    private int row_n ;
    private String status="resend" ;//���ͱ��
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
    "##########0.0000");
    
    // ҳ���Ϸ������б�
    private String[] pageItem = {
        "REQTYPE_CODE", "REQUEST_NO", "APP_ORG_CODE",
        "TO_ORG_CODE", "REASON_CHN_DESC", "REQUEST_DATE", "DISPENSE_NO",
        "DESCRIPTION", "URGENT_FLG"};

    
	/**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
        initData();
        
     // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_N|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        
    }
    
    /**
     * ��ʼ��������
     */
    private void initPage() {
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }

        // ��ʼ������ʱ��
        // ��������
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_N = getTable("TABLE_N");
        
        row_m = -1;
        row_n = -1;
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
      //  ( (TMenuItem) getComponent("cancle")).setEnabled(false);
        resultParm = new TParm();
        
        //����ҩƷ��
        setTypesOfDrug();
    }

    /**
     * ����ҩƷ����
     */
    public void setTypesOfDrug(){
    	TParm parm = getSysParm();
    	if(parm.getCount() > 0 ){
    		parm = parm.getRow(0);
    	}
    	String isSeparateReq = parm.getValue("IS_SEPARATE_REQ");
    	if(isSeparateReq != null && !isSeparateReq.equals("")){
    		if(isSeparateReq.equals("Y")){
    			getRadioButton("G_DRUGS").setSelected(true);
    		}else{
    			getRadioButton("ALL").setSelected(true);
    		}
    	}
    }
    
 
    /**
     * ��ʼ������
     */
    public void initData(){
    	
    	// ���������
        TParm parmIn = new TParm();
         
        // ��ѯ����
        parmIn.setData("REGION_CODE", Operator.getRegion());

        // ����״̬  δ����
        parmIn.setData("STATUS", "B");

        //ҩƷ����--��ҩ:1,�龫��2
       
        
        TParm sysParm = getSysParm();
    	if(sysParm.getCount() > 0 ){
    		sysParm = sysParm.getRow(0);
    	}
    	String isSeparateReq = sysParm.getValue("IS_SEPARATE_REQ");
    	if(isSeparateReq != null && !isSeparateReq.equals("")){
    		if(isSeparateReq.equals("Y")){
    			 parmIn.setData("DRUG_CATEGORY","1");
    		}else{
    			 parmIn.setData("DRUG_CATEGORY","3");
    		}
    	}
         
        //���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO
       
    	 TParm result = new TParm();
         result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                               "onQueryOutM", parmIn);
         
         // ȫԺҩ�ⲿ����ҵ����
         if (!request_all_flg) {
             TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                 getOperatorDept(Operator.getID())));
             String dept_code = "";
             for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                 boolean flg = true;
                 for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                     dept_code = parm.getValue("DEPT_CODE", j);
                     if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                         "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                         "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                         if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                             flg = false;
                             break;
                         }
                         else {
                             flg = true;
                         }
                     }
                     else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                              "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                         if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                             flg = false;
                             break;
                         }
                         else {
                             flg = true;
                         }
                     }
                 }
                 if (flg) {
                     result.removeRow(i);
                 }
             }
         }
         if (result.getCount() <= 0 || result.getCount("REQUEST_NO") == 0) {
             this.messageBox("�޲�ѯ���");
             return;
         }
         table_m.setParmValue(result);
         
         //�����ݣ�Ĭ��ѡ�е�һ��
         table_m.setSelectedRow(0);
         onTableMClicked();
         
    }
    
    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
    	table_N = getTable("TABLE_N");
        table_N.acceptText();
        // ���ѡ�е���
        int column = table_N.getSelectedColumn();
        int row_d = table_N.getSelectedRow();
        if (column == 0) {
        	
            if ("Y".equals(table_N.getItemString(row_d, column))) {
            	
            	double stock_qty = 0 ;
            	double qty = 0 ;
            	TParm parm = table_N.getParmValue();
            	TParm rowParm = parm.getRow(row_d);
            	 // ��治����Ϣ
			    stock_qty = rowParm.getDouble("STOCK_QTY");
			    qty = rowParm.getDouble("OUT_QTY");
			    
			    if(qty > stock_qty ){
			    	table_N.setItem(row_d,"OUT_QTY",stock_qty);
			    	qty = stock_qty ;
			    }
			    
			    if(stock_qty <= 0) {
			    	this.messageBox("ѡ�еļ�¼û�п��");
			    	table_N.setItem(row_d, "SELECT_FLG", "N");
			    	return ;
			    }
                table_N.setItem(row_d, "SELECT_FLG", "Y");
            } else {
                table_N.setItem(row_d, "SELECT_FLG", "N");
                
            }
        }
    }
    

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                              "onQueryOutM", onQueryParm());
        // ȫԺҩ�ⲿ����ҵ����
        if (!request_all_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getOperatorDept(Operator.getID())));
            String dept_code = "";
            for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                boolean flg = true;
                for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                    dept_code = parm.getValue("DEPT_CODE", j);
                    if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                    else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                             "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                }
                if (flg) {
                    result.removeRow(i);
                }
            }
        }
        if (result.getCount() <= 0 || result.getCount("REQUEST_NO") == 0) {
            this.messageBox("�޲�ѯ���");
            return;
        }
        table_m.setParmValue(result);
        
        //�����ݣ�Ĭ��ѡ�е�һ��
        table_m.setSelectedRow(1);
               
    }
    
    /**
     * 
     */
    public void onSave(){
    	
    	TParm parm = new TParm();
    	outStore(parm);
    	status = "save" ;
    	this.onCreate010Xml();
    }
    
    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // ������ѡ���������Ϸ�
            getTableSelectValue(table_m, row_m, pageItem);
            // ����ʱ��
            if (table_m.getItemData(row_m, "DISPENSE_DATE") != null) {
                this.setValue("DISPENSE_DATE", table_m.getItemData(row_m,
                    "DISPENSE_DATE"));
            }
            // �趨ҳ��״̬
            getComboBox("REQTYPE_CODE").setEnabled(false);
            getComboBox("APP_ORG_CODE").setEnabled(false);
            getTextField("REQUEST_NO").setEnabled(false);
            // �������
            request_type = getValueString("REQTYPE_CODE");
            // ���뵥��
            request_no = getValueString("REQUEST_NO");
            // ���ⵥ��
            dispense_no = getValueString("DISPENSE_NO");
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                // ��ϸ��Ϣ  δ����
                getTableDInfo(request_no);
                getTextField("BOX_ESL_ID").setEnabled(true);
                getTextField("ELETAG_CODE").setEnabled(true);
                setValue("BOX_ESL_ID", "");
                setValue("ELETAG_CODE", "");
            }else {
                // ��ϸ��Ϣ ����
                getTableDInfo2(dispense_no);
                getTextField("BOX_ESL_ID").setEnabled(false);
                getTextField("ELETAG_CODE").setEnabled(false);
            }
            row_n = -1;
        }
    }
    
    /**
     * ���ѡ���������Ϸ�
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }
    
    
    /**
     * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
    	
    	TParm parm = new TParm();
    	parm.setData("REQUEST_NO",req_no);
    	
    	//����ȥ�ǲ�����
    	parm.setData("UPDATE_FLG","3");
    	
    	parm.setData("REQTYPE_CODE",getValueString("REQTYPE_CODE"));
        // ȡ��δ��ɵ�ϸ����Ϣ
        TParm result = SPCDispenseOutTool.getInstance().onQuery(parm);
//        System.out.println("sql==="+INDSQL.getOutRequestDInfo(req_no, "3"));
//        System.out.println("result==" + result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_N.removeRowAll();
        table_N.setSelectionMode(0);
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }
        else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = false;
        }

        // �������
        resultParm = result;
        // ���TABLE_D
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_N.setParmValue(result);
    }

    /**
     * ���ݳ��ⵥ��ȡ��ϸ����Ϣ����ʾ��ϸ������
     *
     * @param dispense_no
     */
    private void getTableDInfo2(String dispense_no) {
    	
    	TParm parm = new TParm() ;
    	parm.setData("DISPENSE_NO",dispense_no);
    	 
        TParm result = SPCDispenseOutTool.getInstance().onQueryDispense(parm);
        //===zhangp 20120710 end
        //System.out.println("result----"+result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_N.removeRowAll();
        table_N.setSelectionMode(0);
        // �趨��λ����
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        // �趨����ⲿ��
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;

        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }

        resultParm = result;
        // ���TABLE_D
        //System.out.println("result---"+result);
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("û��������ϸ");
            return;
        }
        table_N.setParmValue(result);
    }
    
    
    /**
     * ���TABLE_D
     *
     * @param table
     * @param parm
     * @param args
     */
    private TParm setTableDValue(TParm result) {
        TParm parm = new TParm();
        double qty = 0;
        double actual_qty = 0;
        double stock_price = 0;
        double retail_price = 0;
        double atm = 0;
        String order_code = "";
        boolean flg = false;
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            flg = false;
        }
        else {
            flg = true;
        }
        for (int i = 0; i < result.getCount(); i++) {
            parm.setData("SELECT_FLG", i, flg);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            // �����(���ⲿ��)
            if (!"".equals(result.getValue("BATCH_NO", i))) {
                if ("0".equals(u_type)) {//0�����浥λ�Ǻ� ����stock_qty��Ƭ�� Ҫ���Թ��=�У�1����ҩ��λby liyh 20120910
                	if("RET".equals(request_type)){//�������ҩ����ѯ����� ���ø���batch_no,valid_date����ѯ��ֻ�����order_code��Ok by liyh 20120910
                        parm.setData("STOCK_QTY", i,
                                INDTool.getInstance().getStockQTY(
                                    out_org_code, order_code) /
                                result.getDouble("DOSAGE_QTY", i));
                	}else if("WAS".equals(request_type)){
                		//��������
                		parm.setData("STOCK_QTY", i,
                                INDTool.getInstance().getStockQTYAll(
                                    out_org_code, order_code,
                                    result.getValue("BATCH_NO", i),
                                    result.getValue("VALID_DATE",
                       i).substring(0, 10), Operator.getRegion()) /
                                result.getDouble("DOSAGE_QTY", i)
                       );
                	}else{
	                    parm.setData("STOCK_QTY", i,
	                                 INDTool.getInstance().getStockQTY(
	                                     out_org_code, order_code,
	                                     result.getValue("BATCH_NO", i),
	                                     result.getValue("VALID_DATE",
	                        i).substring(0, 10), Operator.getRegion()) /
	                                 result.getDouble("DOSAGE_QTY", i)
	                        );
                	}
                }else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code,
                                     result.getValue("BATCH_NO", i),
                                     result.getValue("VALID_DATE",
                        i).substring(0, 10), Operator.getRegion()));
                }
            }else {
                if ("0".equals(u_type)) {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code) /
                                 result.getDouble("DOSAGE_QTY", i));
                }else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code));
                }
            }
            
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                parm.setData("OUT_QTY", i, qty - actual_qty);
            }else {
                parm.setData("OUT_QTY", i, qty);
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
           
            /**
            // ʹ�õ�λ
            if ("0".equals(u_type)) {
                // ��浥λ
                stock_price = result.getDouble("STOCK_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
                retail_price = result.getDouble("RETAIL_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
            }
            else {
                // ��ҩ��λ
                stock_price = result.getDouble("STOCK_PRICE", i);
                retail_price = result.getDouble("RETAIL_PRICE", i);
            }*/
            stock_price = result.getDouble("STOCK_PRICE", i);
            parm.setData("STOCK_PRICE", i, stock_price);
            atm = StringTool.round(stock_price * qty, 2);
            parm.setData("STOCK_ATM", i, atm);
            parm.setData("RETAIL_PRICE", i, retail_price);
            atm = StringTool.round(retail_price * qty, 2);
            parm.setData("RETAIL_ATM", i, atm);
            atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
            parm.setData("DIFF_ATM", i, atm);
            parm.setData("BATCH_NO", i, result.getValue("BATCH_NO", i));
            parm.setData("VALID_DATE", i, result.getTimestamp("VALID_DATE", i));
            parm.setData("PHA_TYPE", i, result.getValue("PHA_TYPE", i));
            parm.setData("ORDER_CODE", i, order_code);
            parm.setData("REQUEST_SEQ",i,result.getInt("REQUEST_SEQ",i));
            parm.setData("BATCH_SEQ",i,result.getValue("BATCH_SEQ",i));
            parm.setData("UNIT_CHN_DESC",i,result.getValue("UNIT_CHN_DESC",i));
            parm.setData("MATERIAL_LOC_CODE",i,result.getValue("MATERIAL_LOC_CODE",i));
            parm.setData("ELETAG_CODE",i,result.getValue("ELETAG_CODE",i));
            parm.setData("SUP_CODE",i,result.getValue("SUP_CODE",i));
            
        }
        //System.out.println("------"+parm);
        return parm;
    }

    
    /**
     * ��ѯ��������
     *
     * @return
     */
    private TParm onQueryParm() {
        // ���������
        TParm parm = new TParm();
        // ���뵥��
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        // ���벿��
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        // �������
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        // ���ⵥ��
        if (!"".equals(getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        // ��ѯ����
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return parm;
        }
        // ����״̬
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            // δ����
            parm.setData("STATUS", "B");
        }
        else if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // ���
            parm.setData("STATUS", "A");
        }
        else {
            // ;��
            parm.setData("STATUS", "C");
        }
        
        //ҩƷ����--��ҩ:1,�龫��2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","2");
        }else if(getRadioButton("ALL").isSelected()){
        	parm.setData("DRUG_CATEGORY","3");
        }
        
        //���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO
        if(getRadioButton("APP_ALL").isSelected()){
        	
        }else if(getRadioButton("APP_ARTIFICIAL").isSelected()){
        	parm.setData("APPLY_TYPE","1");
        }else if(getRadioButton("APP_PLE").isSelected()){
        	parm.setData("APPLY_TYPE","2");
        }else if(getRadioButton("APP_AUTO").isSelected()){
        	parm.setData("APPLY_TYPE","3");
        }
        
        return parm;
    }
    
    /**
	 * ��ת��Ļس��¼�
	 * */
	public void onBoxEslIdClicked() {
		boxEslId = this.getValueString("BOX_ESL_ID");
	
		table_N.acceptText();
		table_N = getTable("TABLE_N");
		
		//String dispenseNo = getValueString("DISPENSE_NO");
		if(StringUtil.isNullString(boxEslId)){
			this.messageBox("��ת��Ϊ��!");
			return ;
		}
			
		int count = table_N.getRowCount() ;
		if(count <= 0 ){
			this.messageBox("û�г����¼�װ������");
			return ;
		}
		
		this.getTextField("ELETAG_CODE").grabFocus();
		
		//��ת����ʾ���ⵥ������ⲿ��
		//
		//String toOrgCode = (String)getValue("TO_ORG_CODE");
		TComboBox tcb = getComboBox("APP_ORG_CODE");
		String orgChnDesc = tcb.getSelectedName();
		
		/**
		 * ���õ��ӱ�ǩ
		 */
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("ProductName", orgChnDesc);
		
		String spec = "";

        //ҩƷ����--��ҩ:1,�龫��2
        if(getRadioButton("G_DRUGS").isSelected()){
        	spec = "��ҩ";
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	spec = "�龫";
        }
		map.put("SPECIFICATION",   spec);
		map.put("TagNo", boxEslId);
		map.put("Light", 1);
		String apRegion = getApRegion(out_org_code);
		if(apRegion == null || apRegion.equals("")){
			System.out.println("��ǩ����û�����ò��Ŵ��룺"+out_org_code);
		}
		map.put("APRegion", apRegion);
		//System.out.println("apRegion-----:"+apRegion);
		list.add(map);
		try{
			String url = Constant.LABELDATA_URL ;
			LabelControl.getInstance().sendLabelDate(list, url);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	/**
	 * ���ӱ�ǩ�Ļس��¼�
	 * */
	public void onEleTagClicked() {
		 
			String eletagCode=this.getValueString("ELETAG_CODE");
			boxEslId = this.getValueString("BOX_ESL_ID");
					
			if(StringUtil.isNullString(eletagCode) || StringUtil.isNullString(boxEslId)){
				return;
			}
			
			table_N = getTable("TABLE_N");
			table_N.acceptText();
			
			TParm parm = table_N.getParmValue();
			int count = table_N.getRowCount();
			
			//�ж��б����Ƿ���������ӱ�ǩ
			double stock_qty = 0;
			double qty = 0 ;
			String msgValidate = "";
			for (int i = 0; i < count; i++) {
				TParm rowParm = parm.getRow(i);
				String eleCode = rowParm.getValue("ELETAG_CODE");
				if(eletagCode.equals(eleCode)){
					
				    // ��治����Ϣ
				    stock_qty = rowParm.getDouble("STOCK_QTY");
				    qty = rowParm.getDouble("OUT_QTY");
				    
				    if(qty > stock_qty ){
				    	table_N.setItem(i,"OUT_QTY",stock_qty);
				    	qty = stock_qty ;
				    }
				    
				    if(stock_qty <= 0) {
				    	this.messageBox("��ҩƷû�п��");
				    	return ;
				    }

				    table_N.setItem(i, "SELECT_FLG", "Y");
					/**
					rowParm.setData("BOX_ESL_ID",boxEslId);
					productName = rowParm.getValue("ORDER_DESC");
					rowParm.setData("BOXED_USER",Operator.getID());
					rowParm.setData("IS_BOXED","Y");
					//����
					outStore(rowParm);
					*/
				   
				    
				    //��������������¸�ֵ��һ������
				    double validateQty = qty ;
				    String batchNo = "";
				    if ("0".equals(u_type)) {
				    	validateQty = validateQty * INDTool.getInstance().getPhaTransUnitQty(rowParm.getValue("ORDER_CODE"), "2");
					} 
				    // ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
					TParm stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(out_org_code, rowParm.getValue("ORDER_CODE"), "");
					for (int j = 0; j < stock_parm.getCount(); j++) {
						double stockQty = stock_parm.getDouble("QTY", j);
						batchNo = stock_parm.getValue("BATCH_NO",j) ;
						 
						if(batchNo != null && !batchNo.equals("") && batchNo.length() > 10){
							batchNo = batchNo.substring(0,10);
						}
						//������������С�ڿ����һ�γ���
						if (stockQty >= validateQty) {
							msgValidate += "["+batchNo+"]";
							break;
						}else{
							// ���³�����
							validateQty = validateQty - stockQty;
							msgValidate += "["+batchNo+"]/";
						}
					}
					
					//SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
				    String productName = rowParm.getValue("ORDER_DESC");
				    String spec = rowParm.getValue("SPECIFICATION");
				    double lastQty = stock_qty-qty ;
				    
				    
				    /**
					 * ���õ��ӱ�ǩ
					 */
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					map.put("ProductName", productName);
					map.put("SPECIFICATION",  spec+" "+lastQty);
					map.put("TagNo", eletagCode);
					map.put("Light", 1);
					
					String apRegion = getApRegion(out_org_code);
					if(apRegion == null || apRegion.equals("")){
						System.out.println("��ǩ����û�����ò��Ŵ��룺"+out_org_code);
					}
					map.put("APRegion", apRegion);
					
					list.add(map);
					try{
						String url = Constant.LABELDATA_URL ;
						LabelControl.getInstance().sendLabelDate(list, url);
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
				    	System.out.println("���õ��ӱ�ǩ����ʧ��");

					}
				}
			}

			this.setValue("ELETAG_CODE", "");
			this.setValue("VALID_DATE_STR", msgValidate);
			return;
		
	}
	
	/**
	 * ���⶯��
	 */
	public void outStore(TParm parm){
		if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            /** ��������(���ⵥ���ϸ��) */
            // ҩ�������Ϣ
            TParm sysParm = getSysParm();
            // �������ҵ״̬�ж�(1-���ȷ�ϣ�2-���⼴���)
            String dis_check = getDisCheckFlg(sysParm);
            // �Ƿ��д����۸�
            String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
            if ("1".equals(dis_check)) {
                // ���ⲿ�ſ���Ƿ���춯
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("���ⲿ�Ŵ����̵�״̬����治���춯");
                    return;
                }
                
                /**
                // �ж��Ƿ��п�棬����û�п���ҩƷȡ����ѡ
                String message = checkStockQty(parm);
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                    return ;
                }*/
                
                
                // ������;��ҵ/��������������ҵ(���⼴���)
                if (getDispenseOutOn(out_org_code)) {
                	
                    // ��ӡ���ⵥ
                    this.onPrint();
                    this.onClear();
                }
            } else if ("2".equals(dis_check)) {
                // ���ⲿ�ſ���Ƿ���춯
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("���ⲿ�Ŵ����̵�״̬����治���춯");
                    return;
                }
                // ��ⲿ�ſ���Ƿ���춯
                if (!"".equals(in_org_code) && !getOrgBatchFlg(in_org_code)) {
                    this.messageBox("��ⲿ�Ŵ����̵�״̬����治���춯");
                    return;
                }
                
                /**
                // �ж��Ƿ��п�棬����û�п���ҩƷȡ����ѡ
                String message = checkStockQty(parm);
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                    return ;
                }*/
                
                // ���⼴�����ҵ(����ⲿ�ž���Ϊ��)
                getDispenseOutIn(out_org_code, in_org_code, reuprice_flg,
                                 out_flg, in_flg,parm);
                
                //��ӡ���ⵥ
                this.onPrint();
                this.onClear();
            }
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            /** ��;���� */
            if (row_m != -1) {
                // ��������
                getUpdateDispenseMOutOn();
            }
            else {
                // ����ϸ��
                this.messageBox("���뵥�ѳ��⣬�����޸�");
            }
        }
        else {
            /** ��ɸ��� */
            this.messageBox("��������ɣ������޸�");
        }
	}
	
	/**
     * ��������(��;)
     */
    private void getUpdateDispenseMOutOn() {
        TParm parm = new TParm();
        // ������Ϣ
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "1").getParm("OUT_M");
        // ִ�����ݸ���
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onUpdateMOutOn", parm);
        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

	
	 /**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }

    /**
     * �������ҵ״̬�ж�
     *
     * @return
     */
    private String getDisCheckFlg(TParm parm) {
        // �������ҵ״̬�ж�
        if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
            && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // ��������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->��;״̬
            return "1";
        }
        else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
                 && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // ����������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->���⼴���
            return "2";
        }
        return "1";
    }

    /**
     * ����Ƿ���춯״̬�ж�
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // ����Ƿ���춯״̬�ж�
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            return false;
        }
        return true;
    }

    /**
     * ���������ж�: 1,��ָ�����ź�Ч��;2,ָ�����ź�Ч��
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }
    
    /**
     * �ж��Ƿ��п�棬����û�п���ҩƷȡ����ѡ
     * @return String
     */
    private String checkStockQty(TParm parm) {
        double stock_qty = 0;
        // ��治����Ϣ
        String message = "";
        stock_qty = parm.getDouble("STOCK_QTY");
        double qty = parm.getDouble("OUT_QTY");

        if (qty > stock_qty) {
        	message += "��治�㣡";
        }
        return message;
    }
    
    /**
     * ������;��ҵ/��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
     */
    private boolean getDispenseOutOn(String org_code) {
        
    	/**�ж�ϸ��ѡ��*/
    	if (!checkSelectRow()) {
            return false;
        }
    	
    	 //�ж��������
        if ("DEP".equals(request_type)) {
            String order_code = "";
            double out_qty = 0;
            String order_desc = "";
            for (int i = 0; i < table_N.getRowCount(); i++) {
                if ("Y".equals(table_N.getItemString(i, "SELECT_FLG"))) {
                    //�ж���ҩ������������
                    order_code = table_N.getParmValue().getValue("ORDER_CODE",
                        i);
                    out_qty = table_N.getItemDouble(i, "OUT_QTY");
                    String sql =
                        " SELECT A.STOCK_QTY / B.STOCK_QTY / B.DOSAGE_QTY AS "
                        + "STOCK_QTY,C.UNIT_CHN_DESC,A.BATCH_NO,A.VALID_DATE, "
                        + " A.ORDER_CODE, D.ORDER_DESC "
                        + " FROM IND_STOCK A, PHA_TRANSUNIT B, "
                        + " SYS_UNIT C, PHA_BASE D "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + " AND A.ORDER_CODE = D.ORDER_CODE "
                        + " AND B.ORDER_CODE = D.ORDER_CODE "
                        + " AND A.ORG_CODE = '" + org_code
                        + "' AND A.ORDER_CODE = '" + order_code
                        + "' AND A.STOCK_QTY > 0 AND A.VALID_DATE > SYSDATE "
                        + " AND A.ACTIVE_FLG = 'Y' AND D.PHA_TYPE = 'G'"
                        + " AND B.STOCK_UNIT = C.UNIT_CODE "
                        + " ORDER BY A.VALID_DATE DESC, A.BATCH_SEQ";
                    TParm checkParm = new TParm(TJDODBTool.getInstance().select(
                        sql));
                    if (checkParm.getCount("ORDER_CODE") > 0) {
                        if (out_qty > checkParm.getDouble("STOCK_QTY", 0)) {
                            order_desc = checkParm.getValue("ORDER_DESC", 0);
                            this.messageBox(order_desc + " " + order_code
                                            + " ������� " + out_qty + " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + ", ����:" +
                                            checkParm.getValue("BATCH_NO", 0) +
                                            ",Ч��:" +
                                            checkParm.getValue("VALID_DATE", 0).
                                            substring(0, 10) +
                                            ",��ǰ���Ϊ" +
                                            checkParm.getDouble("STOCK_QTY", 0) +
                                            " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + " ,�����Ƚ���ȫ������");
                            return false;
                        }
                    }
                }
            }
        }
            
        

        TParm parm = new TParm();
        // ������Ϣ
        if (!CheckDataM()) {
            return false;
        }
        parm = getDispenseMParm(parm, "1");
        
        /**
        // ϸ����Ϣ
        if (!CheckDataD()) {
            return false;
        }*/
        parm = getDispenseDParm(parm);
        // ʹ�õ�λ
        parm.setData("UNIT_TYPE", u_type);
        // ���뵥����
        parm.setData("REQTYPE_CODE", request_type);
        // ���ⲿ��
        parm.setData("ORG_CODE", org_code);
        
        //��ȡ�������ݷţ����ӱ�ǩ��ʾӦ��
        //TParm outD = parm.getParm("OUT_D");
        // ִ����������
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertOutOn", parm);

        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
        this.messageBox("P0001");
        this.setValue("DISPENSE_NO", dispense_no);
        
        /**
        //���ӱ�ǩ����
        for(int i = 0 ; i < outD.getCount("ORDER_CODE");  i++ ){
        	TParm inParm = new TParm();
        	String eleTagCode = outD.getValue("ELETAG_CODE",i) ;
			inParm.setData("ORDER_CODE",outD.getValue("ORDER_CODE",i));
			inParm.setData("ORG_CODE",getValue("TO_ORG_CODE"));
			inParm.setData("ELETAG_CODE",eleTagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			String spec = outParm.getValue("SPECIFICATION",0);
			EleTagControl.getInstance().login();
			EleTagControl.getInstance().sendEleTag(eleTagCode, outD.getValue("ORDER_DESC",i), spec, outParm.getValue("QTY",0), 0);
        }*/
        
        return true;
    }
    
    
    /**
     * ���⼴�����ҵ
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String reuprice_flg, boolean out_flg,
                                  boolean in_flg,TParm rowParm) {
        /**
    	if (!checkSelectRow()) {
            return;
        }*/
    	
        TParm parm = new TParm();
        // ������Ϣ(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        
        /**ˢ���ӱ�ǩʱ����
        parm = getDispenseMParm(parm, "3");
        // ϸ����Ϣ(OUT_D)
        if (!CheckDataD(rowParm)) {
            return;
        }*/
        
        parm = getDispenseDParm(parm);
        //System.out.println("PARM-> " + parm);

        // ʹ�õ�λ
        parm.setData("UNIT_TYPE", u_type);
        // ���뵥����
        parm.setData("REQTYPE_CODE", request_type);
        // ���ⲿ��
        parm.setData("OUT_ORG_CODE", out_org_code);
        // ��ⲿ��
        parm.setData("IN_ORG_CODE", in_org_code);
        // �Ƿ����(IN_FLG)
        parm.setData("IN_FLG", in_flg);
        // �ж��Ƿ��Զ����ɱ��۴��������
        parm.setData("REUPRICE_FLG", reuprice_flg);

        //TParm outD = parm.getParm("OUT_D");
        
        // ִ����������
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertOutIn", parm);
        // �����ж�
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
       
        /**
        for(int i = 0 ; i < outD.getCount("ORDER_CODE");  i++ ){
        	TParm inParm = new TParm();
        	String eleTagCode = outD.getValue("ELETAG_CODE",i) ;
			inParm.setData("ORDER_CODE",outD.getValue("ORDER_CODE",i));
			inParm.setData("ORG_CODE",getValue("TO_ORG_CODE"));
			inParm.setData("ELETAG_CODE",eleTagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			String spec = outParm.getValue("SPECIFICATION");
			EleTagControl.getInstance().login();
			EleTagControl.getInstance().sendEleTag(eleTagCode, outD.getValue("ORDER_DESC",i), spec, outParm.getValue("QTY",0), 0);
        }*/
        
        onClear();
    }
    
    /**
     * ���������Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg) {
        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        // ���ⵥ��
        dispense_no = "";
        if ("".equals(getValueString("DISPENSE_NO"))) {
            dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }
        else {
            dispense_no = getValueString("DISPENSE_NO");
        }
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", getValue("DISPENSE_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", date);
            parmM.setData("WAREHOUSING_USER", Operator.getIP());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        parmM.setData("REASON_CHN_DESC", getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", u_type);
        if ("WAS".equals(getValue("REQTYPE_CODE")) ||
            "THO".equals(getValue("REQTYPE_CODE")) ||
            "COS".equals(getValue("REQTYPE_CODE")) ||
            "EXM".equals(getValue("REQTYPE_CODE"))) {
            update_flg = "3";
        }
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parmM.setData("REGION_CODE", Operator.getRegion());

        //ҩƷ����--��ҩ:1,�龫��2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parmM.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parmM.setData("DRUG_CATEGORY","2");
        }else{
        	parmM.setData("DRUG_CATEGORY","3");
        }
        
        //���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO
        if(getRadioButton("APP_ALL").isSelected()){
        	
        }else if(getRadioButton("APP_ARTIFICIAL").isSelected()){
        	parmM.setData("APPLY_TYPE","1");
        }else if(getRadioButton("APP_PLE").isSelected()){
        	parmM.setData("APPLY_TYPE","2");
        }else if(getRadioButton("APP_AUTO").isSelected()){
        	parmM.setData("APPLY_TYPE","3");
        }
        
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        
       
        return parm;
    }
    
    /**
     * �����ϸ��Ϣ
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String batch_no = "";
        int count = 0;
        String order_code = "";
        String valid_date = "";
        TTable table_d = getTable("TABLE_N");
        TParm tableDParm = table_d.getParmValue() ;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmD.setData("DISPENSE_NO", count, dispense_no);
            parmD.setData("SEQ_NO", count, count);
            parmD.setData("REQUEST_SEQ", count, tableDParm.getValue( "REQUEST_SEQ",i));
            order_code = tableDParm.getValue("ORDER_CODE",i);
            parmD.setData("ORDER_CODE", count, order_code);
            parmD.setData("QTY", count, tableDParm.getDouble( "QTY",i));
            parmD.setData("UNIT_CODE", count, tableDParm.getValue(
                "UNIT_CODE",i));
            parmD.setData("RETAIL_PRICE", count,  tableDParm.getValue("RETAIL_PRICE",i));
            parmD.setData("STOCK_PRICE", count, tableDParm.getDouble(
                "STOCK_PRICE",i));
            parmD.setData("ACTUAL_QTY", count, tableDParm.getDouble(
                "OUT_QTY",i));
            parmD.setData("PHA_TYPE", count, tableDParm.getValue(
                "PHA_TYPE",i));
            //luahi modify 2012-1-16 batch_seq ��ֵ��Ҫ��Tparm��ȡ�ã���Ϊҳ���в�������batch_seq begin
//            parmD.setData("BATCH_SEQ", count, table_d.getItemData(i,
//                "BATCH_SEQ"));
              parmD.setData("BATCH_SEQ",count,tableDParm.getInt("BATCH_SEQ",i));
            //luahi modify 2012-1-16 batch_seq ��ֵ��Ҫ��Tparm��ȡ�ã���Ϊҳ���в�������batch_seq end
            
            batch_no = tableDParm.getValue( "BATCH_NO",i);
            parmD.setData("BATCH_NO", count, batch_no);
            valid_date = tableDParm.getValue("VALID_DATE",i);
            if ("".equals(valid_date)) {
                parmD.setData("VALID_DATE", count, tnull);
            }
            else {
                parmD.setData("VALID_DATE", count,
                              table_d.getItemTimestamp(i, "VALID_DATE"));
            }
            parmD.setData("DOSAGE_QTY", count, tableDParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            
            //�Ƿ��¼�
            parmD.setData("IS_BOXED",count,"Y");
            parmD.setData("BOXED_USER",count,Operator.getID());
            parmD.setData("BOX_ESL_ID",count,boxEslId);
            
            //���ӱ�ǩӦ��
            parmD.setData("ELETAG_CODE",count,tableDParm.getValue(
                "ELETAG_CODE", i));
            parmD.setData("ORDER_DESC",count,tableDParm.getValue("ORDER_DESC",i));
            
            parmD.setData("SUP_ORDER_CODE",count,tableDParm.getValue("SUP_ORDER_CODE",i));
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    
    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("REQUEST_NO"))) {
            this.messageBox("���뵥�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        return true;
    }
    
    /**
     * �ж�ϸ���Ƿ�ѡ��
     *
     * @return
     */
    private boolean checkSelectRow() {
        // �ж�ϸ���Ƿ�ѡ��
        boolean flg = true;
        for (int i = 0; i < table_N.getRowCount(); i++) {
            if ("Y".equals(table_N.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("û��ѡ�г���ҩƷ��ϸ");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD(TParm parm) {
       
        // �ж�������ȷ��
        double qty = parm.getDouble("OUT_QTY");
        if (qty <= 0) {
            this.messageBox("��������������С�ڻ����0");
            return false;
        }
        double price = parm.getDouble("STOCK_PRICE");
        if (price <= 0) {
            this.messageBox("�ɱ��۲���С�ڻ����0");
            return false;
        }
        
        return true;
    }

    /**
     * ��ӡ���ⵥ
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_N");
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("�����ڳ��ⵥ");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
           /* date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "���ⵥ");*/
            date.setData("TITLE", "TEXT","ҩƷ���ⵥ");
            //===============pangben modify 20110607 ��Ӽ���ע��
            if (null !=
                getValue("URGENT_FLG") && getValue("URGENT_FLG").equals("Y"))
                date.setData("URGENT", "TEXT", "��");
            else
                date.setData("URGENT", "TEXT", "");
            //===============pangben modify 20110607 stop
            date.setData("DISP_NO", "TEXT",
                         "���ⵥ��: " + this.getValueString("DISPENSE_NO"));
            date.setData("REQ_NO", "TEXT",
                         "���뵥��: " + this.getValueString("REQUEST_NO"));
            date.setData("OUT_DATE", "TEXT",
                         "��������: " + this.getValueString("DISPENSE_DATE").
                         substring(0, 10).replace('-', '/'));
            date.setData("REQ_TYPE", "TEXT", "�������: " +
                         this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("ORG_CODE_APP", "TEXT", "���ⲿ��: " +
            	    this.getComboBox("TO_ORG_CODE").getSelectedName());
            date.setData("ORG_CODE_FROM", "TEXT", "���ܲ���: " +
            		  this.getComboBox("APP_ORG_CODE").getSelectedName());
            date.setData("DATE", "TEXT",			   
                         "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // �������			
            TParm parm = new TParm();
            String order_code = "";
            String order_desc = "";
            double qty = 0;
            double sum_retail_price = 0;
            //luhai 2012-1-22 ����ɹ����ܺ�
            double sum_verifyin_price = 0;
			String orgCode =  getValueString("APP_ORG_CODE");
			String sql = "";
			if(getSpclTrtDept().equals(orgCode)){
				sql = " SELECT A.ORDER_CODE, "
					 +"        CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE B.ORDER_DESC END "
					 +"             AS ORDER_DESC, "
					 +"          B.SPECIFICATION, "
					 +"          C.UNIT_CHN_DESC, "
					 +"          CASE             "
					 +"             WHEN A.UNIT_CODE = D.STOCK_UNIT "
					 +"             THEN     "
					 +"                A.RETAIL_PRICE * D.DOSAGE_QTY "
					 +"             WHEN A.UNIT_CODE = D.DOSAGE_UNIT "
					 +"             THEN                             "
					 +"                A.RETAIL_PRICE                "
					 +"            ELSE                              "
					 +"               A.RETAIL_PRICE                 "
					 +"         END                                  "
					 +"             RETAIL_PRICE,                    "
					 +"          A.QTY AS OUT_QTY,                   "
					 +"          A.BATCH_NO,                         "
					 +"          A.VALID_DATE,                       "
					 +"          CASE                                "
					 +"             WHEN A.UNIT_CODE = D.STOCK_UNIT THEN A.INVENT_PRICE  "
					 +"            WHEN A.UNIT_CODE = D.DOSAGE_UNIT THEN A.VERIFYIN_PRICE "
					 +"            ELSE A.VERIFYIN_PRICE                                  "
					 +"          END                                                      "
					 +"             VERIFYIN_PRICE                                        "
					 +"     FROM IND_DISPENSED A,                                         "
					 +"         SYS_FEE B,                                                "
					 +"         SYS_UNIT C,                                               "
					 +"         PHA_TRANSUNIT D,                                          "
					 +"          (SELECT A.ORDER_CODE, A.OPMED_CODE                       "
					 +"             FROM SYS_ORDER_OPMEDD A                               "
					 +"           WHERE A.OPMED_CODE = (SELECT A.OPMED_CODE               "
					 +"                                    FROM (  SELECT A.OPMED_CODE,   "
					 +"                                                   COUNT (A.OPMED_CODE) NUM  "
					 +"                                              FROM SYS_ORDER_OPMEDD A,     "
					 +"                                                  SYS_ORDER_OPMEDM B       "
					 +"                                             WHERE     A.OPMED_CODE = B.OPMED_CODE  "
					 +"                                                  AND B.OP_FLG = 'Y'                 "
					 +"                                         GROUP BY A.OPMED_CODE                       "
					 +"                                          ORDER BY COUNT (A.OPMED_CODE) DESC) A      "
					 +"                                   WHERE ROWNUM = 1)) E                              "
					 +"   WHERE     A.ORDER_CODE = B.ORDER_CODE                                             "
					 +"          AND A.UNIT_CODE = C.UNIT_CODE                                              "
					 +"          AND A.ORDER_CODE = D.ORDER_CODE                                            "
					 +"          AND A.ORDER_CODE = E.ORDER_CODE(+)                                         "
					 +"         AND A.DISPENSE_NO = '"+this.getValueString("DISPENSE_NO")+"'  "
					 +"  ORDER BY E.SEQ_NO                                                          "	;   
			}else if("040108".equals(orgCode)||"040109".equals(orgCode)) {
				sql =
                    "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL "
                    + " THEN B.ORDER_DESC ELSE B.ORDER_DESC  "
                    + "  END AS ORDER_DESC, "
                    + " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
                    + " (A.QTY*D.DOSAGE_QTY) AS MIN_QTY,A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.VERIFYIN_PRICE "//ADD VERIFYIN_PRICE
                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C ,PHA_TRANSUNIT D "
                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + " AND A.Order_Code=D.Order_Code AND D.DOSAGE_UNIT=C.UNIT_CODE "
                    + " AND A.DISPENSE_NO = '" +
                    this.getValueString("DISPENSE_NO") + "' "						
                    + " ORDER BY A.SEQ_NO";  
			}else {
				   sql =
	                    "SELECT A.ORDER_CODE, " +
	                    "   CASE WHEN B.GOODS_DESC IS NULL "
	                    + "  THEN B.ORDER_DESC ELSE B.ORDER_DESC   END AS ORDER_DESC, "
	                    + "  B.SPECIFICATION, C.UNIT_CHN_DESC,  " 
	                    + " CASE  WHEN A.UNIT_CODE = D.STOCK_UNIT "
					    + "       THEN A.RETAIL_PRICE * D.DOSAGE_QTY  "
					    + "       WHEN A.UNIT_CODE = D.DOSAGE_UNIT "
					    + "       THEN A.RETAIL_PRICE "
					    + "       ELSE A.RETAIL_PRICE "
					    + "       END  RETAIL_PRICE, "
	                    + "  A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,"
	                    + "  CASE WHEN A.UNIT_CODE=D.STOCK_UNIT " 
	                    + "       THEN A.INVENT_PRICE "
	                    + "       WHEN A.UNIT_CODE=D.DOSAGE_UNIT THEN A.VERIFYIN_PRICE ELSE A.VERIFYIN_PRICE  END  VERIFYIN_PRICE "   //ADD VERIFYIN_PRICE
	                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C ,PHA_TRANSUNIT D "
	                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
	                    + " AND A.UNIT_CODE = C.UNIT_CODE "
	                    + " AND A.ORDER_CODE=D.ORDER_CODE "
	                    + " AND A.DISPENSE_NO = '" +
	                    this.getValueString("DISPENSE_NO") + "' "
	                    + " ORDER BY A.SEQ_NO";
			}
				
              //luhai modify ���ⵥɾ����Ʒ�� begin 
                TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
                for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
                    parm.addData("ORDER_DESC",
                                 printData.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 printData.getValue("SPECIFICATION", i));
                    parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
                    parm.addData("UNIT_PRICE",
                                 df4.format(printData.getDouble("RETAIL_PRICE",
                        i)));
                    parm.addData("VERIFYIN_PRICE",
                    		df4.format(printData.getDouble("VERIFYIN_PRICE",
                    				i)));
                    qty = printData.getDouble("OUT_QTY", i);
                    if("040108".equals(orgCode)||"040109".equals(orgCode)) {
                    	 
                    	 qty = printData.getDouble("MIN_QTY", i) ;
                    	 parm.addData("QTY", StringTool.round(qty,3));
                    }else {
                    	 parm.addData("QTY", StringTool.round(qty,3));				
                    }
                    parm.addData("AMT", StringTool.round(printData.getDouble(
                        "RETAIL_PRICE", i) * qty, 2));
                    parm.addData("AMT_VERIFYIN", StringTool.round(printData.getDouble(
                    		"VERIFYIN_PRICE", i) * qty, 2));
                    sum_retail_price += printData.getDouble("RETAIL_PRICE", i) *
                        qty;
                    sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i) *
                    qty;
                    parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
                    parm.addData("VALID_DATE", StringTool.getString(printData.
                        getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
                }

            
            if (parm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            //luhai 2012-1-22 modify ����ɹ����ɹ����� begin
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");			
         //   parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            															
            date.setData("TABLE", parm.getData());

            // ��β����
            //luhai 2012-1-22 ����ϼ�atm
            String atm = StringTool.round(sum_retail_price, 2)+"";
            String verifyinAtm=StringTool.round(sum_verifyin_price, 2)+"";
//            date.setData("TOT_CHN", "TEXT",
//                         "�ϼ�(��д): " + StringUtil.getInstance().numberToWord(atm));
            date.setData("BAR_CODE", "TEXT",this.getValueString("DISPENSE_NO"));
            date.setData("ATM", "TEXT",atm);
            date.setData("VERIFYIN_ATM", "TEXT",verifyinAtm);
            date.setData("VERIFYIN_ATM_DESC", "TEXT",StringUtil.getInstance().numberToWord(Double.parseDouble(verifyinAtm)));
            date.setData("TOT", "TEXT", "�ϼ�: ");
            date.setData("USER", "TEXT", Operator.getName());
            date.setData("BAR_CODE", "TEXT", this.getValueString("DISPENSE_NO"));
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DispenseOut.jhw", date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }
    
    
    /**
     * ȡ�����ⷽ��
     */
    public void onCancle() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("û��ȡ������");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ�ȡ������", 2) == 0) {
            String dispense_no = this.getValueString("DISPENSE_NO");
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("DISPENSE_NO", dispense_no);
            parm.setData("ORG_CODE", out_org_code);
            parm.setData("REQTYPE_CODE", this.getValueString("REQTYPE_CODE"));
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("PARM_D", table_N.getParmValue().getData());
            TParm result = TIOM_AppServer.executeAction(
                "action.spc.INDDispenseAction", "onUpdateDipenseCancel", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("ȡ��ʧ��");
                return;
            }
            this.messageBox("ȡ���ɹ�");
            this.onClear();
        }
    }
    
    /**
     * ��ֹ���ݷ���
     */
    public void onStop() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("û����ֹ����");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ���ֹ����", 2) == 0) {
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                "action.spc.INDRequestAction", "onUpdateFlg", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("��ֹʧ��");
                return;
            }
            this.messageBox("��ֹ�ɹ�");
            this.onClear();
        }
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        // ��ջ�������
        String clearString =
            "REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;DISPENSE_NO;"
            + "REQUEST_DATE;TO_ORG_CODE;REASON_CHN_DESC;START_DATE;END_DATE;"
            + "DESCRIPTION;URGENT_FLG;SELECT_ALL;"
            + "SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE;"
            + "BOX_ESL_ID;ELETAG_CODE;VALID_DATE_STR";
        clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        
        //Ĭ��ѡ����ҩ
        getRadioButton("G_DRUGS").setSelected(true);
        
        // ��ʼ��ҳ��״̬������
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_N.setSelectionMode(0);
        table_N.removeRowAll();
         
        getTextField("REQUEST_NO").setEnabled(true);
        getComboBox("APP_ORG_CODE").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
       // ( (TMenuItem) getComponent("cancle")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        resultParm = new TParm();
    }
    
    
    /**
     * ����״̬�ı��¼�
     */
    public void onChangeSelectAction() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_A").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
           // ( (TMenuItem) getComponent("cancle")).setEnabled(false);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
           // ( (TMenuItem) getComponent("cancle")).setEnabled(true);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(false);
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
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
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
    private TPanel getTPanel(String tagName){
    	return(TPanel)getComponent(tagName);
    }
    
    private String getApRegion( String orgCode) {
		TParm searchParm = new TParm () ;
		searchParm.setData("ORG_CODE",orgCode);
		TParm resulTParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode(searchParm);
		String apRegion = "";
		if(resulTParm != null ){
			apRegion = resulTParm.getValue("AP_REGION");
		}
		return apRegion;
	}
    
    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
    	table_N.acceptText();
        if (table_N.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        double stock_qty =  0 ;
        double qty =  0 ; 
        
    	TParm parm = table_N.getParmValue();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_N.getRowCount(); i++) {
                
                TParm rowParm = parm.getRow(i) ;
                // ��治����Ϣ
			    stock_qty = rowParm.getDouble("STOCK_QTY");
			    qty = rowParm.getDouble("OUT_QTY");
			    
			    if(qty > stock_qty ){
			    	table_N.setItem(i,"OUT_QTY",stock_qty);
			    	qty = stock_qty ;
			    }
			    
			    if(stock_qty <= 0) {
			    	this.messageBox("ѡ�еļ�¼û�п��");
			    	continue ;
			    }
                table_N.setItem(i, "SELECT_FLG", true);
            }
            
        }else {
            for (int i = 0; i < table_N.getRowCount(); i++) {
            	table_N.setItem(i, "SELECT_FLG", false);
            }
        }
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
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        // �ж�ϸ���Ƿ��б�ѡ����
    	table_N.acceptText();
        for (int i = 0; i < table_N.getRowCount(); i++) {
            // �ж�������ȷ��
            double qty = table_N.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("��������������С�ڻ����0");
                return false;
            }
            
            /**
            double price = table_N.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("�ɱ��۲���С�ڻ����0");
                return false;
            }*/
        }
        return true;
    }
    
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getSpclTrtDept(){
		 TConfig config = getProp() ;
		 String deptCode = config.getString("", "SPCL_TRT_DEPT");
		 return deptCode;
	 }
	 //==============================chenxi
  	 /**
  	  * ͬ��his������Ϣ
  	  */
  	 public void onCreate010Xml(){
  		 TConfig config = getProp() ;
		 String hisPath = config.getString("", "bsmHis.path");
		 if(hisPath.length()>0){
			 TParm parm = new TParm() ;
	  		 parm.setData("DISPENSE_NO", dispense_no) ;
	  		 System.out.println("DISPENSE_NO======"+dispense_no);
	  		TParm	 result = TIOM_AppServer.executeAction(
	                "action.spc.bsm.SPCBsmAction", "onDispenseOut", parm);  
	  		if(result.getErrCode()<0){
	  			this.messageBox("ͬ��HIS������Ϣʧ��") ;
	  			System.out.println("==err==="+result.getErrName());
	  			return  ;
	  			
	  		}
	  		if(status.equals("resend"))
	  		this.messageBox("���ͳɹ�") ;
	  		status = "resend" ;
	  		this.onClear() ;
	  		return  ;
		 }
  		return ;
	 }

}
