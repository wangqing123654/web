package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

import java.util.Map;
import java.util.HashMap;

import jdo.spc.IndQtyCheckTool;
import jdo.util.Manager;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ��ӯ��ͳ�Ʊ�
 * </p>
 *
 * <p>
 * Description: ��ӯ��ͳ�Ʊ�
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDQtyCheckQueryControl
    extends TControl {

    TTable table_m;

    TTable table_d;


    public INDQtyCheckQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");

        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("ͳ�Ʋ��Ų���Ϊ��");
            return;
        }
        if (this.getValueDouble("OWN_PRICE_A") >
            this.getValueDouble("OWN_PRICE_B")) {
            this.messageBox("�ɹ��۷�Χ����ȷ");
            return;
        }
        if (this.getValueDouble("CHECK_QTY_A") >
            this.getValueDouble("CHECK_QTY_B")) {
            this.messageBox("ӯ������Χ����ȷ");
            return;
        }
        if (this.getValueDouble("CHECK_AMT_A") >
            this.getValueDouble("CHECK_AMT_B")) {
            this.messageBox("ӯ���Χ����ȷ");
            return;
        }

        table_m.removeRowAll();
        table_d.removeRowAll();
        this.setValue("SUM_COUNT", 0);
        this.setValue("SUM_CHECK_QTY", 0);
        this.setValue("SUM_CHECK_AMT", 0);

        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("BATCH_NO"))) {
            parm.setData("BATCH_NO", this.getValueString("BATCH_NO"));
        }
//        if (this.getValueDouble("OWN_PRICE_A") > 0) {
        parm.setData("OWN_PRICE_A", this.getValue("OWN_PRICE_A"));
        parm.setData("OWN_PRICE_B", this.getValue("OWN_PRICE_B"));
//        }
//        if (this.getValueDouble("CHECK_QTY_A") > 0) {
        parm.setData("CHECK_QTY_A", this.getValue("CHECK_QTY_A"));
        parm.setData("CHECK_QTY_B", this.getValue("CHECK_QTY_B"));
//        }
//        if (this.getValueDouble("CHECK_AMT_A") > 0) {
        parm.setData("CHECK_AMT_A", this.getValue("CHECK_AMT_A"));
        parm.setData("CHECK_AMT_B", this.getValue("CHECK_AMT_B"));
//        }
        if ("".equals(this.getValue("OWN_PRICE_A"))) 
        	parm.setData("OWN_PRICE_A", null);
        if ("".equals(this.getValue("OWN_PRICE_B"))) 
        	parm.setData("OWN_PRICE_B", null);
        if ("".equals(this.getValue("CHECK_QTY_A"))) 
        	parm.setData("CHECK_QTY_A", null);
        if ("".equals(this.getValue("CHECK_QTY_B"))) 
        	parm.setData("CHECK_QTY_B", null);
        if ("".equals(this.getValue("CHECK_AMT_A"))) 
        	parm.setData("CHECK_AMT_A", null);
        if ("".equals(this.getValue("CHECK_AMT_B")))
        	parm.setData("CHECK_AMT_B", null);

        TParm resultM = new TParm();
        TParm resultD = new TParm();
        resultM = IndQtyCheckTool.getInstance().onQueryQtyCheckMaster(
            parm);
        resultD = IndQtyCheckTool.getInstance().onQueryQtyCheckDetail(parm);

        if (resultM == null || resultM.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }

        double sum_check_qty = 0;
        double sum_check_amt = 0;

        String sql = "SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_PHATYPE' ORDER BY ID";
        TParm phaType = new TParm(TJDODBTool.getInstance().select(sql));
        Map map = new HashMap();
        for (int i = 0; i < phaType.getCount("ID"); i++) {
            map.put(phaType.getValue("ID", i), phaType.getValue("CHN_DESC", i));
        }
        for (int i = 0; i < resultM.getCount(); i++) {
            resultM.setData("TYPE_CODE", i,
                            map.get(resultM.getData("TYPE_CODE", i)));
        }
        
        for (int i = 0; i < resultD.getCount(); i++) {
            resultD.setData("TYPE_CODE", i,
                            map.get(resultD.getData("TYPE_CODE", i)));
        }
        table_m.setParmValue(resultM);
        table_d.setParmValue(resultD);
        if (getRadioButton("RadioButton3").isSelected()) {
        	for (int i = 0; i < resultM.getCount(); i++) {
                sum_check_qty += resultM.getDouble("CHECK_QTY", i);
                sum_check_amt += resultM.getDouble("OWN_AMT", i);
            }
            this.setValue("SUM_COUNT", resultM.getCount());
        }
        else {
        	for (int i = 0; i < resultD.getCount(); i++) {
                sum_check_qty += resultD.getDouble("CHECK_QTY", i);
                sum_check_amt += resultD.getDouble("OWN_AMT", i);
            }
        	this.setValue("SUM_COUNT", resultD.getCount());
        }
        
        this.setValue("SUM_CHECK_QTY", StringTool.round(sum_check_qty, 3));
        this.setValue("SUM_CHECK_AMT", StringTool.round(sum_check_amt, 2));
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;TYPE_CODE;BATCH_NO;"
            + "OWN_PRICE_A;OWN_PRICE_B;CHECK_QTY_A;CHECK_QTY_B;CHECK_AMT_A;"
            + "CHECK_AMT_B;SUM_COUNT;SUM_CHECK_QTY;SUM_CHECK_AMT";
        this.clearValue(clearStr);
        clearText(clearStr);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        getRadioButton("RadioButton3").setSelected(true);
        table_m.removeRowAll();
        table_m.setVisible(true);
        table_d.removeRowAll();
        table_d.setVisible(false);
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
    	TParm tableParm = new TParm();
    	DecimalFormat df1 = new DecimalFormat("##########0.000");
    	DecimalFormat df2 = new DecimalFormat("##########0.00");
        // ��ӡ����
        TParm date = new TParm();
        if (getRadioButton("RadioButton3").isSelected()) {
            if (table_m.getRowCount() <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            // ��ͷ����
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "��ӯ��ͳ�ƻ��ܱ�");
            tableParm = table_m.getShowParmValue();
            date.setData("ORG_CODE", "TEXT",
                    "ͳ�Ʋ���: " +  
                    ("".equals(this.getText("ORG_CODE")) ? "ȫ��" :
                    	((TComboBox)this.getComponent("ORG_CODE")).getSelectedName()));
//                     this.getText("ORG_CODE")));
       String start_date = getValueString("START_DATE");
       String end_date = getValueString("END_DATE");
       date.setData("DATE_AREA", "TEXT",
                    "ͳ������: " +
                    start_date.substring(0, 4) + "/" +
                    start_date.substring(5, 7) + "/" +
                    start_date.substring(8, 10) + " " +
                    start_date.substring(11, 13) + ":" +
                    start_date.substring(14, 16) + ":" +
                    start_date.substring(17, 19) +
                    " ~ " +
                    end_date.substring(0, 4) + "/" +
                    end_date.substring(5, 7) + "/" +
                    end_date.substring(8, 10) + " " +  
                    end_date.substring(11, 13) + ":" +
                    end_date.substring(14, 16) + ":" +
                    end_date.substring(17, 19));
       date.setData("DATE", "TEXT",  
                    "�Ʊ�ʱ��: " +
                    SystemTool.getInstance().getDate().toString().
                    substring(0, 10).
                    replace('-', '/'));  
       date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
       date.setData("SUM_COUNT", "TEXT", "�ϼƱ�����" + this.getValueString("SUM_COUNT"));
       date.setData("SUM_CHECK_QTY", "TEXT", "ӯ������" + df1.format(this.getValueDouble("SUM_CHECK_QTY")));
       date.setData("SUM_CHECK_AMT", "TEXT", "ӯ���ܽ�" + df2.format(this.getValueDouble("SUM_CHECK_AMT")));
      //TYPE_CODE;ORDER_DESC;SPECIFICATION;CHECK_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT;RETAIL_AMT
       tableParm.setCount(tableParm.getCount("TYPE_CODE"));
       tableParm.addData("SYSTEM", "COLUMNS", "TYPE_CODE");
       tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
       tableParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       tableParm.addData("SYSTEM", "COLUMNS", "CHECK_QTY");
       tableParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
       tableParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
       tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
       tableParm.addData("SYSTEM", "COLUMNS", "RETAIL_AMT");
       date.setData("TABLE", tableParm.getData());     

       //System.out.println("date----" + date);
       // ���ô�ӡ����
    	        this.openPrintWindow(
    	                "%ROOT%\\config\\prt\\spc\\INDDispenseCostQueryDept_A.jhw", date);
           //"%ROOT%\\config\\prt\\spc\\INDQtyCheckQueryMain.jhw", date);
            
        }
        else {
            if (table_d.getRowCount() <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }
            // ��ͷ����
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().  
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "��ӯ��ͳ����ϸ��");
             tableParm = table_d.getShowParmValue();
             date.setData("ORG_CODE", "TEXT",
                     "ͳ�Ʋ���: " +
                     ("".equals(this.getText("ORG_CODE")) ? "ȫ��" :
                    	 ((TComboBox)this.getComponent("ORG_CODE")).getSelectedName()));
//                      this.getText("ORG_CODE")));
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT",
                     "ͳ������: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) +
                     " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19));
        date.setData("DATE", "TEXT",
                     "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        date.setData("SUM_COUNT", "TEXT", "�ϼƱ�����" + this.getValueString("SUM_COUNT"));
        date.setData("SUM_CHECK_QTY", "TEXT", "ӯ������" + df1.format(this.getValueDouble("SUM_CHECK_QTY")));
        date.setData("SUM_CHECK_AMT", "TEXT", "ӯ���ܽ�" + df2.format(this.getValueDouble("SUM_CHECK_AMT")));
        for(int i=0;i<tableParm.getCount("UNFREEZE_DATE");i++){
        	tableParm.setData("VALID_DATE", i, tableParm.getValue("VALID_DATE", i).toString().substring(0, 10));
        }

       //UNFREEZE_DATE;TYPE_CODE;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;BATCH_NO;VALID_DATE;STOCK_QTY;ACTUAL_CHECK_QTY;CHECK_QTY;OWN_PRICE;OWN_AMT
        tableParm.setCount(tableParm.getCount("UNFREEZE_DATE"));
//        tableParm.addData("SYSTEM", "COLUMNS", "UNFREEZE_DATE");
//        tableParm.addData("SYSTEM", "COLUMNS", "TYPE_CODE");
        tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        tableParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        tableParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        tableParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        tableParm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        tableParm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        tableParm.addData("SYSTEM", "COLUMNS", "ACTUAL_CHECK_QTY");
        tableParm.addData("SYSTEM", "COLUMNS", "CHECK_QTY");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        date.setData("TABLE", tableParm.getData());

        //System.out.println("date----" + date);
        // ���ô�ӡ����
        this.openPrintWindow(
            "%ROOT%\\config\\prt\\spc\\INDQtyCheckQueryDetail.jhw", date);
        }  


    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * ��ѯ���ѡ���¼�
     */
    public void onSelectTypeAction() {
    	double sum_check_qty = 0;
        double sum_check_amt = 0;
        
        if (getRadioButton("RadioButton3").isSelected()) {
            table_m.setVisible(true);
            table_d.setVisible(false);
            TParm resultM = table_m.getParmValue();
            for (int i = 0; i < resultM.getCount(); i++) {
                sum_check_qty += resultM.getDouble("CHECK_QTY", i);
                sum_check_amt += resultM.getDouble("OWN_AMT", i);
            }
            this.setValue("SUM_COUNT", table_m.getParmValue().getCount());
        }
        else {
            table_d.setVisible(true);
            table_m.setVisible(false);
            TParm resultD = table_d.getParmValue();
            for (int i = 0; i < resultD.getCount(); i++) {
                sum_check_qty += resultD.getDouble("CHECK_QTY", i);
                sum_check_amt += resultD.getDouble("OWN_AMT", i);
            }
            this.setValue("SUM_COUNT", table_d.getParmValue().getCount());
        }
        this.setValue("SUM_CHECK_QTY", StringTool.round(sum_check_qty, 3));
        this.setValue("SUM_CHECK_AMT", StringTool.round(sum_check_amt, 2));
    }
    
    /**
     * ����EXCEL
     * @author shendr 20131122
     */
    public void onExport(){
    	if (table_m.isVisible()) {
    		if (table_m.getRowCount() <= 0) {
    			this.messageBox("û�л������");
    			return;
    		}
    		ExportExcelUtil.getInstance().exportExcel(table_m, "��ӯ��ͳ�ƻ���");
		} else {
			if (table_d.getRowCount() <= 0) {
    			this.messageBox("û�л������");
    			return;
    		}
			ExportExcelUtil.getInstance().exportExcel(table_d, "��ӯ��ͳ�ƻ���");
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
