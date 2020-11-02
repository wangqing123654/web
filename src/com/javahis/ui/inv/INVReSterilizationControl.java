package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.INVBackwashingTool;
import jdo.inv.INVReSterilizationTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ���ʧ�ܵǼ�
 * </p>
 * 
 * <p>
 * Description: ���ʧ�ܵǼ�
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
public class INVReSterilizationControl extends TControl {

	private TTable tableRS;
	private boolean isSave = true;

	

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.tableRS = this.getTable("tableRS");
		
		TParm parm = new TParm();
        // ���õ����˵�
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        

		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("RESTERILIZATION_DATE", date);
	}


	/**
	 * ��ձ��
	 */
	public void onClear() {
		tableRS.removeRowAll();
		this.clearControls();
	}
	
	private void clearControls(){
		setValue("ORG_CODE", "");
		setValue("PACK_CODE", "");
		setValue("PACK_DESC", "");
		setValue("SCREAM", "");
		setValue("RESTERILIZATION_REASON", "");
		setValue("RESTERILIZATION_DATE", "");
		setValue("POT_SEQ", "");
		setValue("PROGRAM", "");
		setValue("OPERATIONSTAFF", "");
		setValue("REMARK", "");
		isSave = true;
		this.changeStatus(true);
		//Ϊ���ڿؼ�����ǰ����ֵ
		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("RESTERILIZATION_DATE", date);
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
		int row = tableRS.getSelectedRow();
		if (row >= 0) {
			isSave = false;
		}
		TParm selParm = tableRS.getParmValue().getRow(row);

		setValue("PACK_CODE", selParm.getData("PACK_CODE"));
		setValue("PACK_CHN_DESC", selParm.getData("PACK_CHN_DESC"));
		setValue("RESTERILIZATION_REASON", selParm.getData("RESTERILIZATION_REASON"));
		setValue("RESTERILIZATION_DATE", selParm.getValue("RESTERILIZATION_DATE").toString().substring(0,19).replaceAll("-", "/"));
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
		
		if (null == this.getValueString("RESTERILIZATION_DATE")
				|| this.getValueString("RESTERILIZATION_DATE").length() == 0) {
			messageBox("������ڲ���Ϊ�գ�");
			return;
		}
		
		TParm conditions = new TParm();
		
		if(getValueString("RESTERILIZATION_DATE") != "" && getValueString("RESTERILIZATION_DATE").length() != 0){
			conditions.setData("RESTERILIZATION_DATE_BEGIN", getValueString("RESTERILIZATION_DATE").toString().substring(0,10) + " 00:00:00");
			conditions.setData("RESTERILIZATION_DATE_END", getValueString("RESTERILIZATION_DATE").toString().substring(0,10) + " 23:59:59");
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

		
		TParm result = INVReSterilizationTool.getInstance().queryRSInfo(conditions);
		tableRS.setParmValue(result);
		
		
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
		
		TParm result = INVReSterilizationTool.getInstance().queryPackMByBarcode(conditions);
		
		if(result.getCount("PACK_CODE") == -1){
			result = INVReSterilizationTool.getInstance().queryPackRByBarcode(conditions);
		}
		
		TParm parm = new TParm();
		parm.setData("PACK_CODE", result.getValue("PACK_CODE", 0));
		parm.setData("PACK_SEQ_NO", result.getValue("PACK_SEQ_NO", 0));
		
		parm.setData("BARCODE", getValueString("SCREAM"));
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		parm.setData("RESTERILIZATION_REASON",getValueString("RESTERILIZATION_REASON"));
		parm.setData("RESTERILIZATION_DATE", getValueString("RESTERILIZATION_DATE").substring(0, 19));
		parm.setData("POT_SEQ", getValueString("POT_SEQ"));
		parm.setData("PROGRAM", getValueString("PROGRAM"));
		parm.setData("OPERATIONSTAFF", getValueString("OPERATIONSTAFF"));
		parm.setData("REMARK", getValueString("REMARK"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date.toString().substring(0, 19));
		parm.setData("OPT_TERM", Operator.getIP());
		result = new TParm();
		if (isSave) {
			result = INVReSterilizationTool.getInstance().insertRSInfo(parm);
			if (result.getErrCode() < 0) {
				return;
			}
			result = INVReSterilizationTool.getInstance().updatePackageStatus(parm);
			if (result.getErrCode() < 0) {
				return;
			}
			messageBox("����ɹ���");
			onQuery();
			clearControls();
		} else if (!isSave) {
			
			int row = tableRS.getSelectedRow();
			TParm tp = tableRS.getParmValue().getRow(row);
			parm.setData("ID", tp.getValue("ID"));
			
			result = INVReSterilizationTool.getInstance().updateRSInfo(parm);
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
		if (tableRS.getSelectedRow() < 0) {
			this.messageBox("��ѡ��Ҫɾ���������У�");
			return;
		}
		int row = tableRS.getSelectedRow();
		TParm tp = tableRS.getParmValue().getRow(row);
		
		TParm parm = new TParm();
		parm.setData("ID",tp.getValue("ID"));
		TParm result = INVReSterilizationTool.getInstance().deleteRSInfo(parm);
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
		if (null == this.getValueString("RESTERILIZATION_REASON")
				|| this.getValueString("RESTERILIZATION_REASON").length() == 0) {
			messageBox("���ʧ��ԭ����Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("RESTERILIZATION_DATE")
				|| this.getValueString("RESTERILIZATION_DATE").length() == 0) {
			messageBox("������ڲ���Ϊ�գ�");
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
