package com.javahis.ui.iva;

import java.sql.Timestamp;
import java.util.Date;

import jdo.iva.IVAConfigruationTimeTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:������Һ����ʱ���趨������
 * </p>
 * 
 * <p>
 * Description:������Һ����ʱ���趨������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2013.6.4
 * @version 1.0
 */
public class IVAConfigruationTimeControl extends TControl {

	// ����Table�ؼ�
	private TTable table;
	private String action = "save";

	/**
	 * ��ʼ������
	 */

	public void onInit() {
		super.onInit();

		table = this.getTTable("TABLE");
	}

	/**
	 * ���TTable�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ���TCheckBox�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) getComponent(tag);
	}

	/**
	 * ���TComboBox�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) getComponent(tag);
	}

	/**
	 * ��ս�������ؼ���ֵ
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "BATCH_CODE;BATCH_CHN_DESC;IVA_TIME;DCCHECK_TIME;"
				+ "BATCH_CHN_DESC;TREAT_START_TIME;"
				+ "TREAT_END_TIME;REMARK;FIRST_BATCH;LAST_BATCH;"
				+ "ST_FLG;F_FLG;UD_FLG;TPN_FLG;OP_FLG";//��������ҽ��
		clearValue(tags);
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		// ��ȡtable����
		TParm selParm = table.getParmValue().getRow(row);
		// �ؼ���ֵ
		setValue("BATCH_CODE", selParm.getData("BATCH_CODE"));
		setValue("BATCH_CHN_DESC", selParm.getData("BATCH_CHN_DESC"));
		setValue("DCCHECK_TIME", selParm.getData("DCCHECK_TIME"));
		setValue("IVA_TIME", selParm.getData("IVA_TIME"));
		setValue("TREAT_START_TIME", selParm.getData("TREAT_START_TIME"));
		setValue("TREAT_END_TIME", selParm.getData("TREAT_END_TIME"));
		if(!selParm.getData("STAT_FLG").equals("")){
			this.setValue("ST_FLG", "Y");
		}else{
			this.setValue("ST_FLG", "N");
		}
		if(!selParm.getData("FIRST_FLG").equals("")){
			this.setValue("F_FLG", "Y");
		}else{
			this.setValue("F_FLG", "N");
		}
		if(!selParm.getData("UD_FLG").equals("")){
			this.setValue("UD_FLG", "Y");
		}else{
			this.setValue("UD_FLG", "N");
		}
		//20151110 wangjc add
		if(!selParm.getData("OP_FLG").equals("")){
			this.setValue("OP_FLG", "Y");
		}else{
			this.setValue("OP_FLG", "N");
		}
		setValue("TPN_FLG", selParm.getData("PN_FLG"));
		setValue("REMARK", selParm.getData("REMARK"));
		if (row == -1) {
			// δѡ����Ϊ����
			action = "save";
		} else {
			action = "update";
		}

	}

	/**
	 * ����У��
	 * 
	 * @return
	 */
	public boolean checkData() {
		if (getValueString("BATCH_CODE").length() == 0
				|| getValueString("BATCH_CODE") == "") {
			messageBox("����д���δ���");
			return false;
		}
		if (getValueString("BATCH_CHN_DESC").length() == 0
				|| getValueString("BATCH_CHN_DESC") == "") {
			messageBox("����д����");
			return false;
		}
		if (getValueString("IVA_TIME").length() == 0
				|| getValueString("IVA_TIME") == "") {
			messageBox("����д��Һʱ��");
			return false;
		}
		if (getValueString("DCCHECK_TIME").length() == 0
				|| getValueString("DCCHECK_TIME") == "") {
			messageBox("����дͣ��ҽ��ȷ��ʱ��");
			return false;
		}
		if (getValueString("TREAT_START_TIME").length() == 0
				|| getValueString("TREAT_START_TIME") == "") {
			messageBox("����д��������");
			return false;
		}
		if (getValueString("TREAT_END_TIME").length() == 0
				|| getValueString("TREAT_END_TIME") == "") {
			messageBox("����д��������");
			return false;
		}
		if(this.getValueString("ST_FLG").equals("N") 
				&& this.getValueString("F_FLG").equals("N") 
				&& this.getValueString("UD_FLG").equals("N") 
				&& this.getValueString("OP_FLG").equals("N")){
			this.messageBox("��ʱ���������ͳ���ѡ������ѡ��һ��");
			return false;
		}
		return true;
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ����У��
		if (!checkData())
			return;
		// ��ȡϵͳʱ��
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ȡ�������
		TParm parm = new TParm();
		parm.setData("BATCH_CODE", getValueString("BATCH_CODE"));
		parm.setData("BATCH_CHN_DESC", getValueString("BATCH_CHN_DESC"));
		parm.setData("DCCHECK_TIME", getValueString("DCCHECK_TIME").substring(
				11, 16).replace(":", ""));
		parm.setData("IVA_TIME", getValueString("IVA_TIME").substring(11, 16).replace(":", ""));
		parm.setData("TREAT_START_TIME", getValueString("TREAT_START_TIME")
				.substring(11, 16).replace(":", ""));
		parm.setData("TREAT_END_TIME", getValueString("TREAT_END_TIME")
				.substring(11, 16).replace(":", ""));
		if(this.getValueString("ST_FLG").equals("Y")){
			parm.setData("STAT_FLG", "ST");
		}else{
			parm.setData("STAT_FLG", "");
		}
		if(this.getValueString("F_FLG").equals("Y")){
			parm.setData("FIRST_FLG", "F");
		}else{
			parm.setData("FIRST_FLG", "");
		}
		if(this.getValueString("UD_FLG").equals("Y")){
			parm.setData("UD_FLG", "UD");
		}else{
			parm.setData("UD_FLG", "");
		}
		//20151110 WANGJC ADD����ҽ��
		if(this.getValueString("OP_FLG").equals("Y")){
			parm.setData("OP_FLG", "OP");
		}else{
			parm.setData("OP_FLG", "");
		}
		parm.setData("PN_FLG", getValueString("TPN_FLG"));
		parm.setData("REMARK", getValueString("REMARK"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date.toString().substring(0, 19));
		parm.setData("OPT_TERM", Operator.getIP());
		// �ж�table��û��ѡ����,�����������滹�Ǹ��²���
		if (table.getSelectedRow() >= 0) {
			TParm result = IVAConfigruationTimeTool.getInstance().updateInfo(
					parm);
			if (result.getErrCode() < 0) {
				messageBox("E0001");
				return;
			}
			messageBox("���³ɹ�");
			// ���浽���ݿ���

		} else if (table.getSelectedRow() == -1) {
			// ���µ����ݿ���
			TParm result = IVAConfigruationTimeTool.getInstance().insertInfo(
					parm);
			if (result.getErrCode() < 0) {
				messageBox("E0002");
				return;
			}
			messageBox("P0002");
		}
		onClear();
		onQuery();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫɾ��������");
			return;
		}
		int i = this.messageBox("��ʾ", "�Ƿ�ɾ��", 2);
		if (i == 0) {
			TParm parm = new TParm();
			// �����жϲ�Ϊ��ʱ�ڴ���
			String BATCH_CODE = table.getItemData(row, "BATCH_CODE").toString();
			if (!StringUtil.isNullString(BATCH_CODE)) {
				parm.setData("BATCH_CODE", BATCH_CODE);
			}
			TParm result = IVAConfigruationTimeTool.getInstance().deleteInfo(
					parm);
			if (result.getErrCode() < 0) {
				messageBox("E0003");
				return;
			}
			messageBox("P0003");
			onClear();
			onQuery();
		}
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// ���table
		table.removeRowAll();
		TParm parm = new TParm();
		// ���δ���(��ѯ����)
		String BATCH_CODE = getValueString("BATCH_CODE");
		// �жϲ�Ϊ��ʱ�ڴ���
		if (!StringUtil.isNullString(BATCH_CODE)) {
			parm.setData("BATCH_CODE", BATCH_CODE);
		}
		TParm result = IVAConfigruationTimeTool.getInstance().queryInfo(parm);
		if(result.getCount() <= 0){
			table.removeRowAll();
			this.messageBox("δ��ѯ������");
			return;
		}
		for(int i=0;i<result.getCount("BATCH_CODE");i++){
			result.setData("IVA_TIME", i, result.getValue("IVA_TIME", i).substring(0, 2)
								+":"+result.getValue("IVA_TIME", i).substring(2, 4));
			result.setData("TREAT_START_TIME", i, result.getValue("TREAT_START_TIME", i).substring(0, 2)
					+":"+result.getValue("TREAT_START_TIME", i).substring(2, 4));
			result.setData("TREAT_END_TIME", i, result.getValue("TREAT_END_TIME", i).substring(0, 2)
					+":"+result.getValue("TREAT_END_TIME", i).substring(2, 4));
			result.setData("DCCHECK_TIME", i, result.getValue("DCCHECK_TIME", i).substring(0, 2)
					+":"+result.getValue("DCCHECK_TIME", i).substring(2, 4));
			result.setData("OPT_DATE", i, result.getValue("OPT_DATE", i).substring(0, 19));
		}
//		System.out.println("result===="+result);
		table.setParmValue(result);
	}

}
