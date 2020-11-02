package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inv.INVBackwashingTool;
import jdo.inv.INVOrgTool;
import jdo.inv.INVPublicTool;
import jdo.inv.INVReSterilizationTool;
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
 * Title: �����������ͳ��
 * </p>
 * 
 * <p>
 * Description: �����������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Date: 2013.8.10
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author sdr
 * @version 1.0
 */
public class INVReSterilizationCountControl extends TControl {

	private TTable tableRS;

	

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
		setValue("RESTERILIZATION_DATE_START", date);
		setValue("RESTERILIZATION_DATE_END", date);
	}


	/**
	 * ��ձ��
	 */
	public void onClear() {
		tableRS.removeRowAll();
		this.clearControls();
	}
	
	private void clearControls(){
		setValue("PACK_CODE", "");
		setValue("PACK_DESC", "");
		//Ϊ���ڿؼ�����ǰ����ֵ
		Timestamp date = StringTool.getTimestamp(new Date());
		setValue("RESTERILIZATION_DATE_START", date);
		setValue("RESTERILIZATION_DATE_END", date);
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
//		int row = tableBS.getSelectedRow();
//		if (row >= 0) {
//			isSave = false;
//		}
//		TParm selParm = tableBS.getParmValue().getRow(row);
//
//		setValue("PACK_CODE", selParm.getData("PACK_CODE"));
//		setValue("PACK_CHN_DESC", selParm.getData("PACK_CHN_DESC"));
//		setValue("BACKWASHING_REASON", selParm.getData("BACKWASHING_REASON"));
//		setValue("BACKWASHING_DATE", selParm.getValue("BACKWASHING_DATE").toString().substring(0,19).replaceAll("-", "/"));
//		setValue("POT_SEQ", selParm.getData("POT_SEQ"));
//		setValue("PROGRAM", selParm.getData("PROGRAM"));
//		setValue("OPERATIONSTAFF", selParm.getData("OPERATIONSTAFF"));
//		setValue("REMARK", selParm.getData("REMARK"));
//		setValue("ORG_CODE", selParm.getData("ORG_CODE"));
//		setValue("SCREAM", selParm.getData("BARCODE"));
//		
//		this.changeStatus(false);
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		
//		if (null == this.getValueString("BACKWASHING_DATE_START")
//				|| this.getValueString("BACKWASHING_DATE_START").length() == 0 || null == this.getValueString("BACKWASHING_DATE_END") || this.getValueString("BACKWASHING_DATE_END").length() == 0) {
//			messageBox("��ϴ���ڲ���Ϊ�գ�");
//			return;
//		}
		if(!checkConditions()){
			return;
		}
		
		TParm conditions = new TParm();
		
		conditions.setData("RESTERILIZATION_DATE_START", getValueString("RESTERILIZATION_DATE_START").toString().substring(0,10) + " 00:00:00");
		conditions.setData("RESTERILIZATION_DATE_END", getValueString("RESTERILIZATION_DATE_END").toString().substring(0,10) + " 23:59:59");
		
		if(getValueString("PACK_CODE") != "" && getValueString("PACK_CODE").length() != 0){
			conditions.setData("PACK_CODE", getValueString("PACK_CODE"));
		}

		TParm result = INVReSterilizationTool.getInstance().queryRSCount(conditions);
		tableRS.setParmValue(result);
		
	}
	

	/**
	 * У������
	 * 
	 * @return
	 */
	private boolean checkConditions() {
		
		if (null == this.getValueString("RESTERILIZATION_DATE_START")
				|| this.getValueString("RESTERILIZATION_DATE_START").length() == 0) {
			messageBox("��ϴ���ڲ���Ϊ�գ�");
			return false;
		}
		if (null == this.getValueString("RESTERILIZATION_DATE_END")
				|| this.getValueString("RESTERILIZATION_DATE_END").length() == 0) {
			messageBox("��ϴ���ڲ���Ϊ�գ�");
			return false;
		}
		return true;
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
