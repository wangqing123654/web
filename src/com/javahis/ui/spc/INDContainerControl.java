package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��������COntrol
 * </p>
 *
 * <p>
 * Description: ��������COntrol
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: bluecore
 * </p>
 *
 * @author:��liyh 20121015
 * @version 1.0
 */

public class INDContainerControl
    extends TControl {

    private String action = "save";

    // ������
    private TTable table;

    
    public INDContainerControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
    }
    
    
    /**
     * �رշ���
     */
    public void onClose() {
    	this.messageBox("1");
    }
    
    /**
     * ����ID�س��¼�
     */
    public void onContainId(){
    	initParm();
    }
    
    public void onFridId(){
    	String  a = getValueString("FRID_ID");
    	setValue("FRID_ID", a);
    }
    
    /**
     * �������¼�
     */
    public void  onTableClicked(){
        int row = table.getTable().getSelectedRow();
        if (row != -1) {//CONTAINER_CODE;CONTAINER_NAME;ORDER_CODE;ORDER_DESC;FRID_ID;ORG_CODE;ORG_NAME;COUNT
        	setValue("CONTAINER_CODE", table.getItemString(row, "CONTAINER_CODE"));
        	setValue("CONTAINER_NAME", table.getItemString(row, "CONTAINER_NAME"));
        	setValue("FRID_ID", table.getItemString(row, "FRID_ID"));
        	setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
        	setValue("ORDER_CODE", table.getItemString(row, "ORDER_CODE"));
        	setValue("ORDER_DESC", table.getItemString(row, "ORDER_DESC"));
        	setValue("COUNT", table.getItemString(row, "COUNT"));
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
    	initParm();
    }

    /**
     * ��շ���
     */
    public void onClear() {
       table.removeRowAll();
        String tags =
            "ORG_CODE;CONTAINER_CODE;CONTAINER_NAME;FRID_ID;ORDER_CODE;ORDER_DESC;COUNT";
        clearValue(tags);

    }

   

    /**
     * ��λ���ƻس��¼�
     */
    public void onMaterialAction() {
        String name = getValueString("MATERIAL_CHN_DESC");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
        ( (TTextField) getComponent("MATERIAL_ENG_DESC")).grabFocus();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
    	table = getTable("TABLE");
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

    }
    
    /**
     * ��ʼ������
     */
    public void initParm(){
        TParm tParm = new TParm();
        tParm.setData("CONTAINER_CODE", 0, "A001");
        tParm.setData("CONTAINER_NAME", 0, "��001");
        tParm.setData("ORDER_CODE", 0, "2N020009");
        tParm.setData("ORDER_DESC", 0, "���������̫��ע��Һ");
        tParm.setData("FRID_ID", 0, "");
        tParm.setData("ORG_CODE", 0, "040103");
        tParm.setData("ORG_NAME", 0, "ҩ��");
        tParm.setData("COUNT", 0, "10");
        tParm.setData("CONTAINER_CODE", 1, "A002");
        tParm.setData("CONTAINER_NAME", 1, "��002");
        tParm.setData("ORDER_CODE", 1, "2N020004");
        tParm.setData("ORDER_DESC", 1, "�������̫��ע��Һ");
        tParm.setData("FRID_ID", 1, "");
        tParm.setData("ORG_CODE", 1, "040103");
        tParm.setData("ORG_NAME", 1, "ҩ��");
        tParm.setData("COUNT", 1, "10");
        tParm.setData("CONTAINER_CODE", 2, "A003");
        tParm.setData("CONTAINER_NAME", 2, "��003");
        tParm.setData("ORDER_CODE", 2, "2N020006");
        tParm.setData("ORDER_DESC", 2, "���������ע��Һ");
        tParm.setData("FRID_ID", 2, "");
        tParm.setData("ORG_CODE", 2, "040103");
        tParm.setData("ORG_NAME", 2, "ҩ��");
        tParm.setData("COUNT", 2, "10");
        table.setParmValue(tParm);
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
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("ORDER_CODE").setValue(order_code);
        }
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("ORDER_DESC").setValue(order_desc);
        }
    }

}
