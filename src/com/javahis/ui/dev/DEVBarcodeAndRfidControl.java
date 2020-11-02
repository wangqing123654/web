package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTextFieldEvent;

/**
 * <p>
 * Title: �������RFID Control
 * </p>
 *
 * <p>
 * Description: �������RFID  Control
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
 * @author lit 2013.4.19
 * @version 1.0
 */
public class DEVBarcodeAndRfidControl
    extends TControl {

    // �������ݼ���
    private TParm resultParm;

    private TParm parm;

    public DEVBarcodeAndRfidControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
        } 

        // ��ʼ��������
        initPage(); 
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
    	// Ӧ�ü���δ������ⵥ��ѯ������ֻ�ܲ�ѯ�Ѿ�������DEV_INWAREHOUSEM�ģ�
        // ��ʼ��TABLE            
    	TParm dParm=new TParm();
    	for (int i = 0; i <parm.getCount("RFID"); i++) {
    		dParm.addData("DEVSEQ_NO", i+1); 
    		dParm.addData("DEV_CHN_DESC", parm.getValue("DEV_CHN_DESC", i));
    		dParm.addData("ORGIN_CODE",parm.getValue("ORGIN_CODE", i));
    		dParm.addData("RFID", parm.getValue("RFID", i)); 
		}
    	getTable("TABLE").setParmValue(dParm); 
    	//getTextField("BARCODE").grabFocus();
    	callFunction("UI|BARCODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChange1");
    	callFunction("UI|RFID|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChangeRFID");
    	//getTextField("RFID").grabFocus();
    }
    public void onChange1(){
    	if (getValueString("BARCODE")!=null&&getValueString("BARCODE").trim()!=null&&getValueString("BARCODE").trim().length()>0) {
    		getTextField("BARCODE").setEditable(false);
        	getTextField("RFID").grabFocus();
		}
    }
    public void onSave(){
    	TParm c=new TParm();
    	TTable table=getTable("TABLE");
    	for (int i = 0; i <table.getRowCount(); i++) {
    		c.addData("RFID", table.getItemString(i,"RFID").trim());
    		c.addData("ORGIN_CODE", table.getItemString(i,"ORGIN_CODE").trim());
    		if ("".equals(table.getItemString(i,"ORGIN_CODE"))||"".equals( table.getItemString(i,"ORGIN_CODE").trim())) {
    			messageBox("��"+(i+1)+"������û�и��룬�����¸���");
    			return;
				
			}
    		//continue;
			
		}
    	System.out.println("****************"+c);
       TParm result = TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onUpdateBarCode", c);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    	
    }
    
    
    public void onChangeRFID(){
    	//���� rfid  �ӿ�  ɨ��  rfid
    	TParm dParm=getTable("TABLE").getParmValue();
    	boolean flg=false;
    	for (int i = 0; i < dParm.getCount("RFID"); i++) {
    		String cString=dParm.getData("RFID", i).toString().trim();
    		if (cString.equals(getValueString("RFID").trim())) {
    			getTable("TABLE").setItem(i,"ORGIN_CODE", getValueString("BARCODE"));
    			flg=true;
				break;
			}
		} 
    	if (flg==false) {
			messageBox("�����RFID��");
			this.clearValue("BARCODE");
	    	this.clearValue("RFID");
	    	getTextField("BARCODE").setEditable(true);
	    	getTextField("BARCODE").grabFocus();
			return;
		}
    	this.clearValue("BARCODE");
    	this.clearValue("RFID");
    	getTextField("BARCODE").setEditable(true);
    	getTextField("BARCODE").grabFocus();
    }



    /**
     * ���ط���
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        setReturnValue(table.getParmValue());
        this.closeWindow();
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
