package com.javahis.ui.inv;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jdo.inv.INVSQL;
import jdo.inv.InvPurorderDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
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
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ���ʶ�������Control
 * </p>
 *
 * <p>
 * Description: ���ʶ�������Control
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

public class INVPurorderControl
    extends TControl {
    public INVPurorderControl() {
    }

    private TTable table_m;

    private TTable table_d;
    
    //��ʽ��������
    java.text.DecimalFormat dfInt = new java.text.DecimalFormat("##########0");

    // ����Ȩ��
    private boolean gift_flg = true;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        if (!dept_flg) {
            if("".equals(this.getValueString("ORG_CODE_Q"))){
                this.messageBox("��ѡ���ѯ����");
                return;
            }
        }

        TParm parm = new TParm();
        // ������Դ
        if (this.getRadioButton("STATIC_NO_B").isSelected()) {
            parm.setData("FROM_TYPE", "1");
        }
        else if (this.getRadioButton("STATIC_NO_C").isSelected()) {
            parm.setData("FROM_TYPE", "0");
        }
        // ����״̬
        if (this.getRadioButton("UPDATE_FLG_B").isSelected()) {
            parm.setData("FINAL_FLG", "Y");
        }
        else {
            parm.setData("FINAL_FLG", "N");
        }
        // ��������
        if (!"".equals(this.getValueString("ORG_CODE_Q"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE_Q"));
        }
        // ��������
        if (!"".equals(this.getValueString("SUP_CODE_Q"))) {
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE_Q"));
        }
        // ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        // ��������
        if (!"".equals(this.getValueString("PURORDER_NO_Q"))) {
            parm.setData("PURORDER_NO", this.getValueString("PURORDER_NO_Q"));
        }
        TParm inparm = new TParm();
        inparm.setData("PUR_M", parm.getData());
        // ��ѯ
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVPurorderAction", "onQueryM", inparm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            table_m.removeRowAll();
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        getPurOrderMData(parm); // ȡ�ö�������������
        getPurOrderDData(parm); // ȡ�ö�����ϸ������
        if (flg) {
        	messageBox("���ظ���  ���飡��");
        	return;
			
		}
        flg=false;
        TParm result = new TParm();
        if ("".equals(this.getValueString("PURORDER_NO"))) {
            // ����������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVPurorderAction", "onInsert", parm);
        }
        else {
            if (!checkUpdateFlg()) {
                this.messageBox("�������������������ղ��ɸ���");
                return;
            }
            // ���¶�����
            result = TIOM_AppServer.executeAction(
                "action.inv.INVPurorderAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ��շ���
     */
    public void onClear() {
    	
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextFormat("ORG_CODE").setEnabled(true);
        String clearString =
            "START_DATE;END_DATE;ORG_CODE_Q;SUP_CODE_Q;PURORDER_NO_Q;"
            + "PURORDER_DATE;SUP_CODE;PURORDER_NO;ORG_CODE;REASON_CODE;"
            + "RES_DELIVERY_DATE;STATIO_NO;DESCRIPTION;SELECT_ALL;SUM_MONEY;CON_ORG;RADIO_CHECK";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
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
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        createNewRow();
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
        if (row_d >= 0) {
            // ɾ��������ϸ��
            if ("".equals(this.getValueString("PURORDER_NO"))) {
                table_d.removeRow(row_d);
                this.setValue("SUM_MONEY", getSumMoney());
                return;
            }
            else if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
                if (!checkUpdateFlg()) {
                    this.messageBox("�������������������ղ���ɾ��");
                    return;
                }
                table_d.removeRow(row_d);
                this.setValue("SUM_MONEY", getSumMoney());
                this.onSave();
                this.messageBox("ɾ���ɹ�");
            }
        }
        else if (row_m >= 0) {
            // ɾ������������
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������", 2) == 0) {
                if (!checkUpdateFlg()) {
                    this.messageBox("�������������������ղ���ɾ��");
                    return;
                }
                result = TIOM_AppServer.executeAction(
                    "action.inv.INVPurorderAction", "onDelete", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                table_m.removeRow(row_m);
                table_d.removeRowAll();
                this.messageBox("ɾ���ɹ�");
            }
        }
        else {
            this.messageBox("û��ѡ����");
            return;
        }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            getTextFormat("SUP_CODE").setEnabled(false);
            getTextFormat("ORG_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table_d.setSelectionMode(0);
            // ������Ϣ(TABLE��ȡ��)
            setValue("PURORDER_DATE", table_m.getItemTimestamp(row,
                "PURORDER_DATE"));
            setValue("SUP_CODE", table_m.getItemString(row, "SUP_CODE"));
            setValue("PURORDER_NO", table_m.getItemString(row, "PURORDER_NO"));
            setValue("ORG_CODE", table_m.getItemString(row, "ORG_CODE"));
            setValue("REASON_CODE",
                     table_m.getItemString(row, "REN_CODE"));
            setValue("RES_DELIVERY_DATE", table_m.getItemTimestamp(row,
                "RES_DELIVERY_DATE"));
            setValue("STATIO_NO", table_m.getItemString(row, "STATIO_NO"));
            setValue("DESCRIPTION", table_m.getItemString(row, "DESCRIPTION"));
            setValue("SUM_MONEY",
                     table_m.getParmValue().getDouble("ACTUAL_AMT", row));
            setValue("CON_ORG", table_m.getParmValue().getValue("CON_ORG", row));
            if(table_m.getParmValue().getValue("CON_ORG", row).length()>0){
            	   this.getCheckBox("RADIO_CHECK").setSelected(true) ;
            }
         
            // ��ϸ��Ϣ
            TParm parm = new TParm();
            parm.setData("PURORDER_NO",
                         table_m.getItemString(row, "PURORDER_NO"));

            TParm result = InvPurorderDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û�ж�����ϸ");
                return;
            }
            double allMoney = 0.00 ;
            double money = 0.00 ;
            for(int i=0;i<result.getCount();i++){
            	money = result.getDouble("CONTRACT_PRICE", i)*result.getDouble("PURORDER_QTY", i) ;
            	result.setData("PURORDER_AMT", i, money) ;
            	allMoney += result.getDouble("PURORDER_AMT", i) ;
            }
            this.setValue("SUM_MONEY", allMoney) ;
            table_d.removeRowAll();
            table_d.setParmValue(result);
            this.createNewRow();
        }
    }

    /**
     * ������(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        int row = table_d.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
    }

    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        table_d.acceptText();
        if (table_d.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }

        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                    continue;
                }
                table_d.setItem(i, "IERMINATE_FLG", "Y");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                    continue;
                }
                table_d.setItem(i, "IERMINATE_FLG", "N");
                this.setValue("SUM_MONEY", 0);
            }
        }
    }

    /**
     * ȡ�ö�������������
     * @param parm TParm
     * @return TParm
     * 
     * 
     * 
     */
    private TParm getPurOrderMData(TParm parm) {
        TParm inparm = new TParm();
        // ��������
        if ("".equals(this.getValueString("PURORDER_NO"))) {
            inparm.setData("PURORDER_NO",
                           SystemTool.getInstance().getNo("ALL", "INV",
                "INV_PURORDER", "No"));
        }
        else {
            inparm.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
        }
        // ��������
        inparm.setData("ORG_CODE", this.getValue("ORG_CODE"));
        // ��������
        inparm.setData("SUP_CODE", this.getValue("SUP_CODE"));
        // ��������
        inparm.setData("PURORDER_DATE", this.getValue("PURORDER_DATE"));
        // �������
        inparm.setData("PURORDER_AMT", this.getValue("SUM_MONEY"));
        // ����ԭ��
        inparm.setData("REN_CODE", this.getValueString("REASON_CODE"));
        // ��ע
        inparm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
        // ��������
        if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
            inparm.setData("RES_DELIVERY_DATE", getValue("RES_DELIVERY_DATE"));
        }
        else {
            inparm.setData("RES_DELIVERY_DATE", new TNull(Timestamp.class));
        }
        // ����״̬
        inparm.setData("FINAL_FLG", "N");
        // ������Դ
        	inparm.setData("FROM_TYPE", "1");

        //ʵ����ⲿ��
        if (this.getCheckBox("RADIO_CHECK").isSelected()) {
        	inparm.setData("CON_FLG", "Y");
        }
        else   if (!this.getCheckBox("RADIO_CHECK").isSelected()) {
        	inparm.setData("CON_FLG", "N");
        }
        inparm.setData("CON_ORG", this.getValueString("CON_ORG"));
        // �ƻ�����
        inparm.setData("STATIO_NO", this.getValueString("STATIO_NO"));
        // Ʒ�ʿۿ�
        inparm.setData("QUALITY_DEDUCT_AMT", 0);
        // ������
        inparm.setData("FORFEIT_AMT", 0);
        // ʵ�ʽ��
        inparm.setData("ACTUAL_AMT", this.getValue("SUM_MONEY"));
        // OPT
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
        inparm.setData("OPT_TERM", Operator.getIP());

        parm.setData("PUR_M", inparm.getData());
        return parm;
    }
boolean flg=false;
    /**
     * ȡ�ö�����ϸ������
     * @param parm TParm
     * @return TParm
     */
    private TParm getPurOrderDData(TParm parm) {
        TParm inparm = new TParm();
        Map<String,String>  map=new HashMap<String, String>();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            // 1.��������
            inparm.addData("PURORDER_NO",
                           parm.getParm("PUR_M").getValue("PURORDER_NO"));
            // 2.���
            inparm.addData("SEQ_NO", i);
            // 3.���ʴ���
           String cString= table_d.getParmValue().getValue("INV_CODE", i);
            inparm.addData("INV_CODE",
            		cString);
            if (map.containsKey(cString)) {
				flg=true;
			}else {
				map.put( cString, "1");
			}
            
            // 4.��������
            inparm.addData("PURORDER_QTY",
                           table_d.getItemDouble(i, "PURORDER_QTY"));
            // 5.��������
            inparm.addData("GIFT_QTY",
                           table_d.getItemDouble(i, "GIFT_QTY"));
            // 6.������λ
            inparm.addData("BILL_UNIT",
                           table_d.getItemString(i, "BILL_UNIT"));
            // 7.�����۸�
            inparm.addData("PURORDER_PRICE",
                           table_d.getItemDouble(i, "CONTRACT_PRICE"));
            // 8.�������
            inparm.addData("PURORDER_AMT",
                           table_d.getItemDouble(i, "PURORDER_AMT"));
            // 9.��������ۼ�
            inparm.addData("STOCKIN_SUM_QTY",
                           table_d.getItemDouble(i, "STOCKIN_SUM_QTY"));
            // 10.δ����
            inparm.addData("UNDELIVERY_QTY",
                           table_d.getItemDouble(i, "UNDELIVERY_QTY"));
            // 11.�ۿ���
            inparm.addData("DISCOUNT_RATE",
                           table_d.getItemDouble(i, "DISCOUNT_RATE"));
            // 12.���ͱ���
            inparm.addData("GIFT_RATE",
                           table_d.getItemDouble(i, "GIFT_RATE"));
            // 13.Ʒ�ʿۿ�
            inparm.addData("QUALITY_DEDUCT_AMT",
                           table_d.getItemDouble(i, "QUALITY_DEDUCT_AMT"));
            // 14.����ע��
            inparm.addData("GIFT_FLG",
                           table_d.getItemDouble(i, "GIFT_QTY") > 0 ? "Y" : "N");
            // 15.����״̬
            inparm.addData("PROCESS_TYPE",
                           "Y".equals(table_d.getItemString(i, "IERMINATE_FLG")) ?
                           "3" : "0");
            // 16.��ֹע��
            inparm.addData("IERMINATE_FLG",
                           "Y".equals(table_d.getItemString(i, "IERMINATE_FLG")) ?
                           "Y" : "N");
            // 17,18,19 OPT
            inparm.addData("OPT_USER", Operator.getID());
            inparm.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
            inparm.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("PUR_D", inparm.getData());
        return parm;
    }


    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("SUP_CODE"))) {
            this.messageBox("�������̲���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("�������Ų���Ϊ��");
            return false;
        }
        if (table_d.getRowCount() < 1) {
            this.messageBox("û�ж���ϸ����Ϣ");
            return false;
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if (table_d.getItemDouble(i, "PURORDER_QTY") <= 0) {
                    this.messageBox("������������С�ڻ����0");
                    return false;
                }
            }
        }

        return true;
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
            table_d.setLockColumns("2,4,5,6,7,8,9,10,11,12,13,14,15,16");
            gift_flg = false;
        }
        // ��ʾȫԺҩ�ⲿ��
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "ORG_CODE");   
        TextFormatINVOrg inv_org_q = (TextFormatINVOrg)this.getTextFormat(
            "ORG_CODE_Q");
        if (!this.getPopedem("deptAll")) {
            inv_org.setOperatorId(Operator.getID());
            inv_org_q.setOperatorId(Operator.getID());
            dept_flg = false;
        }
        else {
            inv_org.setOperatorId("");
            inv_org_q.setOperatorId("");
            dept_flg = true;
        }

        Timestamp date = StringTool.getTimestamp(new Date());
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

        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        // ��ʼ��TABLE_M��Parm
        TParm parmD = new TParm();
        String[] purD = {
            "PURORDER_NO", "SEQ_NO", "INV_CODE", "PURORDER_QTY",
            "GIFT_QTY", "BILL_UNIT", "PURORDER_PRICE", "PURORDER_AMT",
            "STOCKIN_SUM_QTY", "UNDELIVERY_QTY", "DISCOUNT_RATE", "GIFT_RATE",
            "QUALITY_DEDUCT_AMT", "GIFT_FLG", "PROCESS_TYPE", "IERMINATE_FLG", };
        for (int i = 0; i < purD.length; i++) {
            parmD.setData(purD[i], new Vector());
        }
        table_d.setParmValue(parmD);
        // ע�ἤ��INDSupOrder�������¼�
        table_d.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                 this, "onCreateEditComoponentUD");
        // TABLE_Dֵ�ı��¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");

        createNewRow();
    }
    /**
     * Radio �ĸı��¼�
     */
   public void onAutoDeptCode(){
	   TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
       "ORG_CODE");   
   TextFormatINVOrg inv_org_q = (TextFormatINVOrg)this.getTextFormat(
       "ORG_CODE_Q");
	   if (this.getRadioButton("STATIC_NO_B").isSelected()) {
		   inv_org.setOrgType("A") ;
		   inv_org_q.setOrgType("A") ;
       }
	   else if(this.getRadioButton("STATIC_NO_C").isSelected()){   
		   inv_org.setOrgType("") ;
		   inv_org_q.setOrgType("") ;
	   }
   }
   public void CheckFlg(){
	   if (this.getCheckBox("RADIO_CHECK").isSelected()) {
		  this.setValue("CON_ORG", Operator.getDept()) ;
       }
	   else if (!this.getCheckBox("RADIO_CHECK").isSelected()) {
			  this.setValue("CON_ORG", "") ;
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
        if ("".equals(this.getValueString("SUP_CODE"))) {
            this.messageBox("�������̲���Ϊ��");
            return;
        }
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("�������Ų���Ϊ��");
            return;
        }
        if (getTextFormat("SUP_CODE").isEnabled()) {
            getTextFormat("SUP_CODE").setEnabled(false);
        }
        if (getTextFormat("ORG_CODE").isEnabled()) {
            getTextFormat("ORG_CODE").setEnabled(false);
        }

        TParm parm = new TParm();
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVSupInv.x"), parm);
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
        table_d.acceptText();
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc)) {
            // ���������ѯ
            String org_code = this.getValueString("ORG_CODE");
            String inv_code = parm.getValue("INV_CODE");
            TParm stockMParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockM(org_code, inv_code)));
            if (stockMParm == null || stockMParm.getCount("INV_CODE") <= 0) {
                this.messageBox("û���趨���������Ϣ");
                return;
            }
            setTableDValue(parm, table_d.getSelectedRow());
            if (table_d.getRowCount() == table_d.getSelectedRow() + 1) {
                createNewRow();
            }
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
        if ("PURORDER_QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("������������С�ڻ����0");
                return true;
            }
            else {
                // ������
                table_d.setItem(row, "GIFT_QTY",
                                qty * table_d.getItemDouble(row, "GIFT_RATE"));
                // ���С��
                table_d.setItem(row, "PURORDER_AMT", qty *
                                table_d.getItemDouble(row, "CONTRACT_PRICE") *
                                (table_d.getItemDouble(row, "DISCOUNT_RATE")==0?1:table_d.getItemDouble(row, "DISCOUNT_RATE")) -
                                table_d.getItemDouble(row, "QUALITY_DEDUCT_AMT"));
                // δ������
                table_d.setItem(row, "UNDELIVERY_QTY", qty);
                // �������
               /* table_d.setItem(row, "IN_QTY",
                                (qty + table_d.getItemDouble(row, "GIFT_QTY")) *
                                table_d.getParmValue().getDouble("STOCK_QTY",
                    row));*/
                table_d.setItem(row, "IN_QTY",
                        (qty + table_d.getItemDouble(row, "GIFT_QTY")));
                table_d.setItem(row, "PURORDER_QTY", qty);
                this.setValue("SUM_MONEY", getSumMoney());
                return false;
            }
        }
        if ("GIFT_QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("������������С��0");
                return true;
            }
            else {
                // �������
                table_d.setItem(row, "IN_QTY",
                                (qty +
                                 table_d.getItemDouble(row, "PURORDER_QTY")));
                return false;
            }
        }
        if ("QUALITY_DEDUCT_AMT".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("Ʒ�ʿۿ��С��0");
                return true;
            }
            else {
                // ���С��
                table_d.setItem(row, "PURORDER_AMT",
                                table_d.getItemDouble(row, "PURORDER_AMT") -
                                qty);

                table_d.setItem(row, "QUALITY_DEDUCT_AMT", qty);
                this.setValue("SUM_MONEY", getSumMoney());
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
            this.setValue("SUM_MONEY", this.getSumMoney());
        }
    }

    /**
     * �����ܽ��
     *
     * @return
     */
    private double getSumMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
                sum += table_d.getItemDouble(i, "PURORDER_QTY")
                    * table_d.getItemDouble(i, "CONTRACT_PRICE");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ��ⶩ�����Ƿ�����������
     * @return boolean
     */
    private boolean checkUpdateFlg() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if (table_d.getItemDouble(i, "STOCKIN_SUM_QTY") > 0) {
                    return false;
                }
            }
        }
        return true;
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
                         "���ʶ�����");
            date.setData("SUP_CODE", "TEXT",
                         "���ʳ���: " +
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
            if (table_d.getRowCount() == 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            for (int i = 0; i < table_d.getRowCount(); i++) {
            	
            	
                parm.addData("INV_CHN_DESC", table_d.getItemData(i,"INV_CHN_DESC"));
                parm.addData("SPECIFICATION",
                		table_d.getItemData(i,"SPECIFICATION"));
                parm.addData("UNIT", table_d.getItemData(i,"UNIT_CHN_DESC"));
                parm.addData("MAN_CODE", table_d.getItemData(i,"MAN_CODE"));
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
                    table_d.getItemDouble(i, "CONTRACT_PRICE");
                parm.addData("AMT", StringTool.round(amt, 2));
                sum_money += amt;
            }
            
            parm.setCount(parm.getCount("INV_CHN_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
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
            this.openPrintWindow("%ROOT%\\config\\prt\\INV\\Purorder.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }



    /**
     * ��ֵ��TABLE_D
     * @param parm TParm
     * @param row int
     */
    public void setTableDValue(TParm parm, int row) {
        table_d.setRowParmValue(row, parm);
        table_d.getParmValue().setRowData(row, parm);
        table_d.setItem(row, "SUP_CODE", this.getValue("SUP_CODE"));
    }

    /**
     * ����ϸ��������
     * @return int
     */
    private int createNewRow() {
        int row = table_d.addRow();
        return row;
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
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

}
