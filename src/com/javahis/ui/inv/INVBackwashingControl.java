package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.INVBackwashingTool;
import jdo.inv.INVOrgTool;
import jdo.inv.INVPublicTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��ϴ�ʵǼ�
 * </p>
 * 
 * <p>
 * Description: ��ϴ�ʵǼ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Date: 2013.4.23
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author sdr
 * @version 1.0
 */
public class INVBackwashingControl extends TControl {

	private TTable tableBS;
	private boolean isSave = true;

	

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.tableBS = this.getTable("tableBS");
		
		TParm parm = new TParm();
        // ���õ����˵�
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        

		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("BACKWASHING_DATE", date);
	}


	/**
	 * ��ձ��
	 */
	public void onClear() {
		tableBS.removeRowAll();
		this.clearControls();
	}
	
	private void clearControls(){
		setValue("ORG_CODE", "");
		setValue("PACK_CODE", "");
		setValue("PACK_DESC", "");
		setValue("SCREAM", "");
		setValue("BACKWASHING_REASON", "");
		setValue("BACKWASHING_DATE", "");
		setValue("POT_SEQ", "");
		setValue("PROGRAM", "");
		setValue("OPERATIONSTAFF", "");
		setValue("REMARK", "");
		isSave = true;
		this.changeStatus(true);
		//Ϊ���ڿؼ�����ǰ����ֵ
		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("BACKWASHING_DATE", date);
	}

	/**
	 * ������ѡ�񷵻����ݴ���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		// ����
		setValue("PACK_CODE", parm.getValue("PACK_CODE"));
		// ����
		setValue("PACK_DESC", parm.getValue("PACK_DESC"));
	}

	/**
	 * ��񵥻��¼� �����ж�������������Ǹ��²���
	 */
	public void onTableClicked() {
		int row = tableBS.getSelectedRow();
		if (row >= 0) {
			isSave = false;
		}
		TParm selParm = tableBS.getParmValue().getRow(row);

		setValue("PACK_CODE", selParm.getData("PACK_CODE"));
		setValue("PACK_CHN_DESC", selParm.getData("PACK_CHN_DESC"));
		setValue("BACKWASHING_REASON", selParm.getData("BACKWASHING_REASON"));
		setValue("BACKWASHING_DATE", selParm.getValue("BACKWASHING_DATE").toString().substring(0,19).replaceAll("-", "/"));
		setValue("POT_SEQ", selParm.getData("POT_SEQ"));
		setValue("PROGRAM", selParm.getData("PROGRAM"));
		setValue("OPERATIONSTAFF", selParm.getData("OPERATIONSTAFF"));
		setValue("REMARK", selParm.getData("REMARK"));
		setValue("ORG_CODE", selParm.getData("ORG_CODE"));
		setValue("SCREAM", selParm.getData("BARCODE"));
		
		this.changeStatus(false);
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		
		if (null == this.getValueString("BACKWASHING_DATE")
				|| this.getValueString("BACKWASHING_DATE").length() == 0) {
			messageBox("��ϴ���ڲ���Ϊ�գ�");
			return;
		}
		
		TParm conditions = new TParm();
		
		if(getValueString("BACKWASHING_DATE") != "" && getValueString("BACKWASHING_DATE").length() != 0){
			conditions.setData("BACKWASHING_DATE_BEGIN", getValueString("BACKWASHING_DATE").toString().substring(0,10) + " 00:00:00");
			conditions.setData("BACKWASHING_DATE_END", getValueString("BACKWASHING_DATE").toString().substring(0,10) + " 23:59:59");
		}
		if(getValueString("PACK_CODE") != "" && getValueString("PACK_CODE").length() != 0){
			conditions.setData("PACK_CODE", getValueString("PACK_CODE"));
		}
		if(getValueString("ORG_CODE") != "" && getValueString("ORG_CODE").length() != 0){
			conditions.setData("ORG_CODE", getValueString("ORG_CODE"));
		}
		if(getValueString("SCREAM") != "" && getValueString("SCREAM").length() != 0){
			conditions.setData("BARCODE", getValueString("SCREAM"));
		}

		TParm result = INVBackwashingTool.getInstance().queryBSInfo(conditions);
		tableBS.setParmValue(result);
		
		isSave = true;
		this.changeStatus(true);
	}
	
	/**
	 * ���������
	 */
	public void onSave() {
		if (!this.checkConditions())
			return;
		Timestamp date = StringTool.getTimestamp(new Date());
		
		TParm conditions = new TParm();
		conditions.setData("BARCODE", getValueString("SCREAM"));
		
		TParm result = INVBackwashingTool.getInstance().queryPackMByBarcode(conditions);
		
		if(result.getCount("PACK_CODE") == -1){
			result = INVBackwashingTool.getInstance().queryPackRByBarcode(conditions);
		}
		
		TParm parm = new TParm();
		parm.setData("PACK_CODE", result.getValue("PACK_CODE", 0));
		parm.setData("PACK_SEQ_NO", result.getValue("PACK_SEQ_NO", 0));
		
		parm.setData("BARCODE", getValueString("SCREAM"));
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		parm.setData("BACKWASHING_REASON",getValueString("BACKWASHING_REASON"));
		parm.setData("BACKWASHING_DATE", getValueString("BACKWASHING_DATE").substring(0, 19));
		parm.setData("POT_SEQ", getValueString("POT_SEQ"));
		parm.setData("PROGRAM", getValueString("PROGRAM"));
		parm.setData("OPERATIONSTAFF", getValueString("OPERATIONSTAFF"));
		parm.setData("REMARK", getValueString("REMARK"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date.toString().substring(0, 19));
		parm.setData("OPT_TERM", Operator.getIP());
		result = new TParm();
		if (isSave) {
			result = INVBackwashingTool.getInstance().insertBSInfo(parm);
			if (result.getErrCode() < 0) {
				return;
			}
			messageBox("����ɹ���");
			onQuery();
			clearControls();
		} else if (!isSave) {
			
			int row = tableBS.getSelectedRow();
			TParm tp = tableBS.getParmValue().getRow(row);
			parm.setData("ID", tp.getValue("ID"));
			
			result = INVBackwashingTool.getInstance().updateBSInfo(parm);
			if (result.getErrCode() < 0) {
				return;
			}
			messageBox("���³ɹ���");
			onQuery();
			clearControls();	
		}
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		if (tableBS.getSelectedRow() < 0) {
			this.messageBox("��ѡ��Ҫɾ���������У�");
			return;
		}
		int row = tableBS.getSelectedRow();
		TParm tp = tableBS.getParmValue().getRow(row);
		
		TParm parm = new TParm();
		parm.setData("ID",tp.getValue("ID"));
		TParm result = INVBackwashingTool.getInstance().deleteBSInfo(parm);
		if (result.getErrCode() < 0) {
			return;
		}
		messageBox("ɾ���ɹ���");
		onQuery();
		clearControls();
		
	}

	/**
	 * У������
	 * 
	 * @return
	 */
	private boolean checkConditions() {
		if (null == this.getValueString("ORG_CODE")
				|| this.getValueString("ORG_CODE").length() == 0) {
			messageBox("���Ų���Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("SCREAM")
				|| this.getValueString("SCREAM").length() == 0) {
			messageBox("���벻��Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("OPERATIONSTAFF")
				|| this.getValueString("OPERATIONSTAFF").length() == 0) {
			messageBox("��Ա����Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("BACKWASHING_REASON")
				|| this.getValueString("BACKWASHING_REASON").length() == 0) {
			messageBox("��ϴԭ����Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("BACKWASHING_DATE")
				|| this.getValueString("BACKWASHING_DATE").length() == 0) {
			messageBox("��ϴ���ڲ���Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("POT_SEQ")
				|| this.getValueString("POT_SEQ").length() == 0) {
			messageBox("���β���Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("PROGRAM")
				|| this.getValueString("PROGRAM").length() == 0) {
			messageBox("������Ϊ�գ�");
			return false;
		}
		return true;
	}
	
	private void changeStatus(boolean tag){
		getTextField("SCREAM").setEnabled(tag);
		getTextField("PACK_CODE").setEnabled(tag);
	}

	/**
	 * ��ȡ���
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	/**
	 * �õ�TextField
	 * 
	 * @param tag
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}
	
}
