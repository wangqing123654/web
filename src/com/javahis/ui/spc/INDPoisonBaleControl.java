package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Map;

import org.jaxen.function.FalseFunction;

import sun.reflect.generics.tree.Tree;

import jdo.ind.INDSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.manager.IndVerifyinObserver;
import com.javahis.util.StringUtil;
import com.sun.media.rtp.TrueRandom;

import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: ����ҩ���뼰���Control
 * </p>
 *
 * <p>
 * Description: ����ҩ���뼰���Control
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
 * @author liyh 2012.10.10
 * @version 1.0
 */

public class INDPoisonBaleControl
    extends TControl {



    public INDPoisonBaleControl() {
        super();
    }
    
    private TTable table_m;

    private TTable table_d;


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
    }

    /**
     * ��շ���
     */
    public void onClear() {
   /*     getRadioButton("UPDATE_FLG_B").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;IND_DISPENSE_NO;OPT_USER;FRID_CODE;FRID_DESC";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ����״̬
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(true);
        getTable("TABLE_M").removeRowAll();
        getTable("TABLE_D").removeRowAll();*/
    }

    /**
     * ���淽��
     */
    public void onSave() {
       String fridCode = getValueString("FRID_CODE");
       if(null == fridCode || "".equals(fridCode)){
    	   this.messageBox("��ѡ������");
       }else{
    	   this.messageBox("����ɹ�");
       }
    }




    /**
     * ��ʼ��������
     */
    private void initPage() {
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    	 // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            //((TextFormatINDOrg)this.getComponent("ORG_CODE")).o
            //getTextFormat("ORG_CODE")
//            if (parm.getCount("NAME") > 0) {
//                getComboBox("ORG_CODE").setSelectedIndex(1);
//            }
        }

        TParm parm = new TParm();
        // ���õ����˵�
        getTextField("FRID_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\ind\\INDPoisonBaleSelectFrid2.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("FRID_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        initParamenter();
    }
    
    /**
     * ����ģ��Ƭ��
     */
    public void onInsertTemplatePY() {
	    TParm inParm = new TParm();
	    inParm.setData("FRID_CODE",    getTextField("FRID_CODE"));
	    inParm.addListener( TPopupMenuEvent.RETURN_VALUE, this,
	    		 "popReturn");
	    TWindow window = (TWindow) this.openWindow(
	    		"%ROOT%\\config\\ind\\INDPoisonBaleSelectFrid.x", inParm,
	    true);
        // ������ܷ���ֵ����
        getTextField("FRID_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
	    window.setX(ImageTool.getScreenWidth() - window.getWidth());
	    window.setY(30);
	    window.setVisible(true);

    }
    
    /**
     * ��ʼ������
     */
    public void initParamenter() {
    	//SELECT_FLG;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;PRICE;VALID_DATE;BATCH_NO;BAR_CODE
        TParm tParm = new TParm();
        tParm.setData("SELECT_FLG", 0, false);
        tParm.setData("ORDER_DESC", 0, "�������ע��Һ");
        tParm.setData("SPECIFICATION", 0, "10mg");
        tParm.setData("DOSAGE_QTY", 0, "1");
        tParm.setData("UNIT_CHN_DESC", 0, "֧");
        tParm.setData("PRICE", 0, "100.1");
        tParm.setData("VALID_DATE", 0, "2013-10-10");
        tParm.setData("BATCH_NO", 0, "121010");
        tParm.setData("BAR_CODE", 0, "A00001");
        
        tParm.setData("SELECT_FLG", 1, false);
        tParm.setData("ORDER_DESC", 1, "�������̫��ע��Һ");
        tParm.setData("SPECIFICATION", 1, "2ml:0.1mg");
        tParm.setData("DOSAGE_QTY", 1, "1");
        tParm.setData("UNIT_CHN_DESC", 1, "֧");
        tParm.setData("PRICE", 1, "200");
        tParm.setData("VALID_DATE", 1, "2013-10-10");
        tParm.setData("BATCH_NO", 1, "121010");
        tParm.setData("BAR_CODE", 1, "A00001");

        tParm.setData("SELECT_FLG", 2, false);
        tParm.setData("ORDER_DESC", 2, "�������̫��ע��Һ");
        tParm.setData("SPECIFICATION", 2, "2ml:0.5mg");
        tParm.setData("DOSAGE_QTY", 2, "1");
        tParm.setData("UNIT_CHN_DESC", 2, "֧");
        tParm.setData("PRICE", 2, "200");
        tParm.setData("VALID_DATE", 2, "2013-10-10");
        tParm.setData("BATCH_NO", 2, "121010");
        tParm.setData("BAR_CODE", 2, "A00001");
        
        tParm.setData("SELECT_FLG", 3, false);
        tParm.setData("ORDER_DESC", 3, "���������ע��Һ");
        tParm.setData("SPECIFICATION", 3, "50mg");
        tParm.setData("DOSAGE_QTY", 3, "1");
        tParm.setData("UNIT_CHN_DESC", 3, "֧");
        tParm.setData("PRICE", 3, "300");
        tParm.setData("VALID_DATE", 3, "2013-10-10");
        tParm.setData("BATCH_NO", 3, "121010");
        tParm.setData("BAR_CODE", 3, "A00001");
        
        table_m.setParmValue(tParm);
        
    }
    /**
     * ��ʼ������
     */
    public void initParamenter2() {
    	
        //ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;PRICE;VALID_DATE;BATCH_NO;FRID_ID;FRID_CODE;FRID_DESC
        TParm tParm1 = new TParm();
        tParm1.setData("ORDER_DESC", 0, "�������ע��Һ");
        tParm1.setData("SPECIFICATION", 0, "10mg");
        tParm1.setData("DOSAGE_QTY", 0, "1");
        tParm1.setData("UNIT_CHN_DESC", 0, "֧");
        tParm1.setData("PRICE", 0, "100.1");
        tParm1.setData("VALID_DATE", 0, "2013-10-10");
        tParm1.setData("BATCH_NO", 0, "121010");
        tParm1.setData("FRID_ID", 0, "1");
        tParm1.setData("FRID_CODE", 0, "A00001");
        tParm1.setData("FRID_DESC", 0, "����1");
      
        tParm1.setData("ORDER_DESC", 1, "�������̫��ע��Һ");
        tParm1.setData("SPECIFICATION", 1, "2ml:0.1mg");
        tParm1.setData("DOSAGE_QTY", 1, "1");
        tParm1.setData("UNIT_CHN_DESC", 1, "֧");
        tParm1.setData("PRICE", 1, "200");
        tParm1.setData("VALID_DATE", 1, "2013-10-10");
        tParm1.setData("BATCH_NO", 1, "121010");
        tParm1.setData("FRID_ID", 1, "2");
        tParm1.setData("FRID_CODE", 1, "A00002");
        tParm1.setData("FRID_DESC", 1, "����2");

        tParm1.setData("ORDER_DESC", 2, "�������̫��ע��Һ");
        tParm1.setData("SPECIFICATION", 2, "2ml:0.5mg");
        tParm1.setData("DOSAGE_QTY", 2, "1");
        tParm1.setData("UNIT_CHN_DESC", 2, "֧");
        tParm1.setData("PRICE", 2, "200");
        tParm1.setData("VALID_DATE", 2, "2013-10-10");
        tParm1.setData("BATCH_NO", 2, "121010");
        tParm1.setData("FRID_ID", 2, "3");
        tParm1.setData("FRID_CODE", 2, "A00003");
        tParm1.setData("FRID_DESC", 2, "����3");

        table_d.setParmValue(tParm1);
    }
    
    public void onChangeTTabbedPane(){
    	  if (getPanel("tPanel_3").isShowing()){
    		  ( (TMenuItem) getComponent("save")).setEnabled(true);
    		  initParamenter();
    	  }    else if (getPanel("tPanel_5").isShowing()) {
    		  ( (TMenuItem) getComponent("save")).setEnabled(false);
    		  initParamenter2();
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
        System.out.println("----22222----parm:"+parm);
        String order_code = parm.getValue("FRID_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("FRID_CODE").setValue(order_code);
        String order_desc = parm.getValue("FRID_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("FRID_DESC").setValue(order_desc);

    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableMCheckBoxClicked(Object obj) {
       this.messageBox("123");
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
     * �õ�TPanel����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
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

   
   
}
