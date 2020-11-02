package com.javahis.ui.dev;

import jdo.dev.DevRFIDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;


/**
 * <p>Title: RFID�豸�Ǽ�</p>
 *
 * <p>Description: RFID�豸�Ǽ�</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BLUECORE</p>
 *
 * @author WangLong 20121203
 * @version 1.0
 */
public class DevRFIDRegControl extends TControl {
	TMenuItem saveMenu;// ����
	TMenuItem updateMenu;// ����
	TCheckBox in1;// �������-1��
	TCheckBox in2;// �������-2��
	TCheckBox in3;// �������-3��
	TCheckBox in4;// �������-4��
	TCheckBox out1;// ��������-1��
	TCheckBox out2;// ��������-2��
	TCheckBox out3;// ��������-3��
	TCheckBox out4;// ��������-4��
	
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        saveMenu = (TMenuItem) this.getComponent("save");// ����
        updateMenu = (TMenuItem) this.getComponent("update");// ����
		in1 = ((TCheckBox) this.getComponent("IN_1"));// �������-1��
		in2 = ((TCheckBox) this.getComponent("IN_2"));// �������-2��
		in3 = ((TCheckBox) this.getComponent("IN_3"));// �������-3��
		in4 = ((TCheckBox) this.getComponent("IN_4"));// �������-4��
		out1 = ((TCheckBox) this.getComponent("OUT_1"));// ��������-1��
		out2 = ((TCheckBox) this.getComponent("OUT_2"));// ��������-2��
		out3 = ((TCheckBox) this.getComponent("OUT_3"));// ��������-3��
		out4 = ((TCheckBox) this.getComponent("OUT_4"));// ��������-4��
        saveMenu.setVisible(true);
        updateMenu.setVisible(false);
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
		if (row < 0)
			return;
		clearQueryData();
		TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
		setValueForParm("RFID_CODE;IP_ADDRESS;RFID_MODEL;SN;RFID_STATUS;" +
				"DEPT_CODE;STATION_CODE;RFID_POSE;RFID_DESC", data, row);
		String[] inAntenna=data.getValue("IN_ANTENNA", row).split(",");
		for (int i = 0; i < inAntenna.length; i++) {
			((TCheckBox) this.getComponent("IN_" + inAntenna[i])).setSelected(true);
		}
		String[] outAntenna = data.getValue("OUT_ANTENNA", row).split(",");
		for (int i = 0; i < outAntenna.length; i++) {
			((TCheckBox) this.getComponent("OUT_" + outAntenna[i])).setSelected(true);
		}
		saveMenu.setVisible(false);
	    updateMenu.setVisible(true);
    }

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if ((this.getValue("RFID_CODE") != null)
				&& !this.getValue("RFID_CODE").equals("")) {
			parm.setData("RFID_CODE", this.getValueString("RFID_CODE"));// RFID����
		}
		if(!this.getValueString("IP_ADDRESS").trim().equals("")){
//			if (this.getValueString("IP_ADDRESS")
//					.matches("(([123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//									+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//									+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//									+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))")) {
			if(StringTool.isIP(this.getValueString("IP_ADDRESS"))){
				parm.setData("IP_ADDRESS", this.getValueString("IP_ADDRESS"));// ip��ַ
			}else{
				messageBox("IP��ַ��ʽ����");
				return;
			}
		}
		TParm result = DevRFIDTool.getInstance().selectRFIDDevice(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "" + result.getErrText());
		}
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		if (result.getCount() <= 0) {
			messageBox("E0008");// ��������
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", result);
	}
    
    /**
     * ����
     */
	public void onSave() {
		TTable table = (TTable) this.getComponent("TABLE");
		int selRow = table.getSelectedRow();
		if (selRow >= 0) {
			return;
		}
		TParm parm = getParmForTag("RFID_CODE;IP_ADDRESS;RFID_MODEL;SN;RFID_STATUS;" +
    	   		"DEPT_CODE;STATION_CODE;RFID_POSE;RFID_DESC");
		if (parm.getValue("RFID_CODE").equals("")) {
			messageBox("����дRFID����");
			return;
		}
		if (parm.getValue("IP_ADDRESS").equals("")) {
			messageBox("����дIP��ַ");
			return;
		} else 
//			if (!parm.getValue("IP_ADDRESS")
//				.matches("(([123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//								+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//								+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//								+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))")) {
			if(!StringTool.isIP(parm.getValue("IP_ADDRESS"))){
			messageBox("IP��ַ��ʽ����");
			return;
		}
		String inAntennaList = "";//�����������
		String outAntennaList = "";//������������
		if (in1.isSelected() && in1.isEnabled()) {
			inAntennaList += "1,";
		}
		if (in2.isSelected() && in2.isEnabled()) {
			inAntennaList += "2,";
		}
		if (in3.isSelected() && in3.isEnabled()) {
			inAntennaList += "3,";
		}
		if (in4.isSelected() && in4.isEnabled()) {
			inAntennaList += "4,";
		}
		if (out1.isSelected() && out1.isEnabled()) {
			outAntennaList += "1,";
		}
		if (out2.isSelected() && out2.isEnabled()) {
			outAntennaList += "2,";
		}
		if (out3.isSelected() && out3.isEnabled()) {
			outAntennaList += "3,";
		}
		if (out4.isSelected() && out4.isEnabled()) {
			outAntennaList += "4,";
		}
		if(inAntennaList.length()>1){
			inAntennaList = inAntennaList.substring(0, inAntennaList.length() - 1);	
		}
		if(outAntennaList.length()>1){
			outAntennaList = outAntennaList.substring(0,outAntennaList.length() - 1);	
		}
		parm.setData("IN_ANTENNA", inAntennaList);
		parm.setData("OUT_ANTENNA", outAntennaList);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = DevRFIDTool.getInstance().insertRFIDDevice(parm);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		} else {
			clearQueryData();
			this.messageBox("P0002");//�����ɹ�
		}
    }

    /**
     * �����������ߺ��뻥��
     */
    public void onCheckAntennaBox(String inAntennaTag,String outAntennaTag) {
    	TCheckBox in= ((TCheckBox) this.getComponent(inAntennaTag));// ���ߺ���
    	TCheckBox out= ((TCheckBox) this.getComponent(outAntennaTag));// ���ߺ���
    	if(in.isSelected()){
    		out.setEnabled(false);
    	}else{
    		out.setEnabled(true);
    	}
    }
    
    /**
     * ����
     */
    public void onUpdate() {
		TTable table = (TTable) this.getComponent("TABLE");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			return;
		}
		TParm data = table.getParmValue();
		TParm parm = getParmForTag("RFID_CODE;IP_ADDRESS;RFID_MODEL;SN;RFID_STATUS;" +
    	   		//"IN_1;IN_2;IN_3;IN_4;OUT_1;OUT_2;OUT_3;OUT_4;" +
    	   		"DEPT_CODE;STATION_CODE;RFID_POSE;RFID_DESC");
		if (parm.getValue("RFID_CODE").equals("")) {
			messageBox("����дRFID����");
			return;
		}
		if (parm.getValue("IP_ADDRESS").equals("")) {
			messageBox("����дIP��ַ");
			return;
		} else 
//			if (!parm.getValue("IP_ADDRESS")
//				.matches("(([123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//								+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//								+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))."
//								+ "(([0123456789])|([123456789][0123456789])|(1[0123456789][0123456789])|(2[01234][0123456789])|(25[012345]))")) {
			if(!StringTool.isIP(parm.getValue("IP_ADDRESS"))){
			messageBox("IP��ַ��ʽ����");
			return;
		}
		String inAntennaList = "";//�����������
		String outAntennaList = "";//������������
		if (in1.isSelected() && in1.isEnabled()) {
			inAntennaList += "1,";
		}
		if (in2.isSelected() && in2.isEnabled()) {
			inAntennaList += "2,";
		}
		if (in3.isSelected() && in3.isEnabled()) {
			inAntennaList += "3,";
		}
		if (in4.isSelected() && in4.isEnabled()) {
			inAntennaList += "4,";
		}
		if (out1.isSelected() && out1.isEnabled()) {
			outAntennaList += "1,";
		}
		if (out2.isSelected() && out2.isEnabled()) {
			outAntennaList += "2,";
		}
		if (out3.isSelected() && out3.isEnabled()) {
			outAntennaList += "3,";
		}
		if (out4.isSelected() && out4.isEnabled()) {
			outAntennaList += "4,";
		}
		if(inAntennaList.length()>1){
			inAntennaList = inAntennaList.substring(0, inAntennaList.length() - 1);	
		}
		if(outAntennaList.length()>1){
			outAntennaList = outAntennaList.substring(0,outAntennaList.length() - 1);	
		}
		parm.setData("IN_ANTENNA", inAntennaList);
		parm.setData("OUT_ANTENNA", outAntennaList);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = DevRFIDTool.getInstance().updateRFIDDevice(parm);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		data.setRowData(selRow, parm);
		callFunction("UI|TABLE|setParmValue", data);
		callFunction("UI|TABLE|setSelectedRow", selRow);
		this.messageBox("���³ɹ�");
	}
   
    /**
     * ɾ��
     */
    public void onDelete() {
		TTable table = (TTable) this.getComponent("TABLE");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			messageBox("��ѡ��һ����¼");
			return;
		}
		TParm data = table.getParmValue();
		if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 2) == 0) {
			String rfidCode = data.getValue("RFID_CODE", selRow);
			TParm result = DevRFIDTool.getInstance().deleteRFIDDevice(rfidCode);
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			}
			this.callFunction("UI|TABLE|removeRow", selRow);
			clearQueryData();
			table.clearSelection();
			saveMenu.setVisible(true);
			updateMenu.setVisible(false);
			this.messageBox("P0003");// ɾ���ɹ�
		}
    }
    
    /**
     * ����ѡ���¼�
     */
    public void onDEPT() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }

    /**
     * ���
     */
    public void onClear() {
    	clearQueryData();
		callFunction("UI|TABLE|setParmValue", new TParm());
        saveMenu.setVisible(true);
        updateMenu.setVisible(false);
	}
    
    /**
     * ��ղ�ѯ����
     */
    public void clearQueryData() {
    	this.clearValue("RFID_CODE;IP_ADDRESS;RFID_MODEL;SN;RFID_STATUS;" +
    	   		"DEPT_CODE;STATION_CODE;RFID_POSE;RFID_DESC");
		in1.setEnabled(true);
		in2.setEnabled(true);
		in3.setEnabled(true);
		in4.setEnabled(true);
		out1.setEnabled(true);
		out2.setEnabled(true);
		out3.setEnabled(true);
		out4.setEnabled(true);
		in1.setSelected(false);
		in2.setSelected(false);
		in3.setSelected(false);
		in4.setSelected(false);
		out1.setSelected(false);
		out2.setSelected(false);
		out3.setSelected(false);
		out4.setSelected(false);
	}
}
