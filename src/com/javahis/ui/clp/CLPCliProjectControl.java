package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;

import jdo.clp.CLPCliProjectTool;
import jdo.sys.Operator;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:�ٴ��о���Ŀ����
 * </p>
 * 
 * <p>
 * Description:�ٴ��о���Ŀ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wukai 2016.5.23
 * @version 1.0
 */
public class CLPCliProjectControl extends TControl {
	public CLPCliProjectControl() {
	}

	private static final String TABLE = "TABLE";
	private TTable table;
	private CLPCliProjectTool tool;

	@Override
	public void onInit() {
		super.onInit();
		tool = CLPCliProjectTool.getNewInstance();
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initPage() {
		table = getTable(TABLE);
		TParm parm = new TParm();
		Timestamp end = StringTool.getTimestamp(new Date());
		end = Timestamp.valueOf(end.toString().substring(0, 10) + " 23:59:59");
		Timestamp start = Timestamp.valueOf(end.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_TIME", start);
		this.setValue("END_TIME", end);
		parm.setData("END_TIME", end);
		parm.setData("START_TIME", start);
		table.setParmValue(tool.onQuery(parm));
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * ��Ŀ��ѯ
	 */
	public void onQuery() {
		// TODO
		TParm parm = new TParm();
		String code = getText("CLIPRO_CODE");
		if (null!=code && code.length()>0) {
			parm.setData("CLIPRO_CODE", "%" + code + "%");
		}
		String desc = getText("CLIPRO_DESC");
		if (null!=desc && desc.length()>0) {
			parm.setData("CLIPRO_DESC", "%" + desc + "%");
		}
		String charger = getText("CLIPRO_CHARGER");
		if (null!=charger && charger.length()>0) {
			parm.setData("CLIPRO_CHARGER", "%" + charger + "%");
		}

		Object classify = this.getValue("CLASSIFY_CODE");
		if (classify != null) {
			parm.setData("CLASSIFY_CODE", "%" + classify + "%");
		}

		Object startTime = this.getValue("START_TIME");
		if (startTime != null) {
			parm.setData(
					"START_TIME",
					Timestamp.valueOf(startTime.toString().substring(0, 10)
							+ " 00:00:00"));
		}

		Object endTime = this.getValue("END_TIME");
		if (endTime != null) {
			parm.setData(
					"END_TIME",
					Timestamp.valueOf(endTime.toString().substring(0, 10)
							+ " 23:59:59"));
		}

		String exp = this.getText("CLIPRO_EXP");
		if (null!=exp && exp.length()>0) {
			parm.setData("CLIPRO_EXP", "%" + exp + "%");
		}

		TParm res = tool.onQuery(parm);
		//System.out.println("query result:" + res);
		if(res == null || res.getCount() <= 0) {
			this.messageBox("��ѯ���޴�����!");
		}
		getTable(TABLE).setParmValue(res);
	}

	/**
	 * ɾ��һ����Ŀ
	 */
	public void onDelete() {
		boolean flg = getTextField("CLIPRO_CODE").isEnabled();
		if (!flg) { // ѡ��һ������ɾ��
			int res = this.messageBox("��ʾ", "ȷ��ɾ����", YES_NO_OPTION);
			if(res == YES_OPTION) {
				int row = table.getSelectedRow();
				if (row != -1) {
					String code = table.getParmValue().getValue("CLIPRO_CODE", row);
					TParm parm = new TParm();
					parm.setData("CLIPRO_CODE", code);
					if(tool.onDelete(parm)) {
						this.messageBox("ɾ���ɹ�");
						table.removeRow(row);
						onClear();
					}
				}
			}
		} else {
			this.messageBox("��ѡ��һ������ɾ��");
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		int row = 0;
		Timestamp start = (Timestamp) this.getValue("START_TIME");
		Timestamp end = (Timestamp) this.getValue("END_TIME");
		if (start != null && end != null) {
			if (start.getTime() > end.getTime()) {
				this.messageBox("ʱ�䷶Χ����ȷ��������ѡ��");
				return;
			}
		}
		if (start != null) {
			start = Timestamp.valueOf(start.toString().substring(0, 10)
					+ " 00:00:00");
		}
		if (end != null) {
			end = Timestamp.valueOf(end.toString().substring(0, 10)
					+ " 23:59:59");
		}
		TTextField code = getTextField("CLIPRO_CODE");
		boolean flg = code.isEnabled(); // �ܹ�����ȡ��
		TParm parm = new TParm();
		parm.setData("CLIPRO_CODE", getValueString("CLIPRO_CODE"));
		parm.setData("CLIPRO_DESC", getValueString("CLIPRO_DESC"));
		parm.setData("CLIPRO_CHARGER", getText("CLIPRO_CHARGER"));
		parm.setData("START_TIME", (start != null) ? start : "");
		parm.setData("END_TIME", (end != null) ? end : "");
		parm.setData("CLIPRO_EXP", getText("CLIPRO_EXP"));
		parm.setData("CLASSIFY_CODE", getValue("CLASSIFY_CODE"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TIME", StringTool.getTimestamp(new Date()));
		parm.setData("OPT_TERM", Operator.getIP());
		// this.messageBox(parm.toString());
		if (!flg) { // ��������
			if (!checkData()) {
				return;
			}
			if (!tool.onUpdate(parm)) {
				this.messageBox("����ʧ�ܣ��������ݵĺ�����");
				return;
			}
			row = table.getSelectedRow();
		} else { // ����������
			if (!checkData()) {
				return;
			}
			if(!checkCode(getValueString("CLIPRO_CODE"))) {
				return;
			}
			if (!tool.onSave(parm)) {
				this.messageBox("����ʧ�ܣ��������ݵĺ�����");
				return;
			}
			row = table.addRow();
		}
		table.setItem(row, "CLIPRO_CODE", parm.getData("CLIPRO_CODE"));
		table.setItem(row, "CLIPRO_DESC", parm.getData("CLIPRO_DESC"));
		table.setItem(row, "CLIPRO_CHARGER", parm.getData("CLIPRO_CHARGER"));
		table.setItem(row, "START_TIME", parm.getData("START_TIME"));
		table.setItem(row, "END_TIME", parm.getData("END_TIME"));
		table.setItem(row, "CLIPRO_EXP", parm.getData("CLIPRO_EXP"));
		table.setItem(row, "CLASSIFY_CODE", parm.getData("CLASSIFY_CODE"));
		table.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
		table.setItem(row, "OPT_TIME", parm.getData("OPT_TIME"));
		table.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
		this.messageBox("����ɹ�");
		onClear();
	}

	/**
	 * ����������ݣ�ɾ���û�
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "CLIPRO_CODE;CLIPRO_DESC;CLIPRO_CHARGER;CLIPRO_EXP;START_TIME;END_TIME;CLASSIFY_CODE";
		clearValue(clearString);
		table.setSelectionMode(0);
		getTextField("CLIPRO_CODE").setEnabled(true);
		getTextField("CLIPRO_DESC").setEnabled(true);
		Timestamp end = StringTool.getTimestamp(new Date());
		end = Timestamp.valueOf(end.toString().substring(0, 10) + " 23:59:59");
		Timestamp start = Timestamp.valueOf(end.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("START_TIME", start);
		this.setValue("END_TIME", end);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * �������������ݣ���������е�ֵ ɾ������
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row >= 0) {
			String linkedTag = "CLIPRO_CODE;CLIPRO_DESC;CLIPRO_CHARGER;START_TIME;END_TIME;CLASSIFY_CODE;CLIPRO_EXP";
			TParm parm = getTable(TABLE).getParmValue().getRow(row);
			this.setValueForParm(linkedTag, parm);

			getTextField("CLIPRO_CODE").setEnabled(false);
			getTextField("CLIPRO_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
		}
	}

	/**
	 * ��ȡTTextField
	 * 
	 * @param tag
	 *            : ��ǩ
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * ��ȡҳ����
	 * 
	 * @param table
	 * @return
	 */
	private TTable getTable(String table) {
		return (TTable) getComponent(table);
	}

	/**
	 * ��������Ƿ�Ϊ��
	 * 
	 * @return
	 */
	private boolean checkData() {
		if (null==getValueString("CLIPRO_CODE") || getValueString("CLIPRO_CODE").length()<=0) {
			this.messageBox("���벻��Ϊ��");
			return false;
		}

		if (null==getValueString("CLIPRO_DESC") || getValueString("CLIPRO_DESC").length()<=0) {
			this.messageBox("�������Ʋ���Ϊ��");
			return false;
		}

		if (null==getValueString("CLIPRO_CHARGER") || getValueString("CLIPRO_CHARGER").length()<=0) {
			this.messageBox("�����˲���Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * ��鵱ǰ��codeʱ���Ѿ�����
	 * 
	 * @return boolean false ��ʾ���ڴ�����������ʹ�ô˱��
	 */
	private boolean checkCode(String code) {
		TParm parm = tool.onQuery(new TParm());
		if (parm != null) {
			for(int i=0;i<parm.getCount();i++) {
				if(code.equals(parm.getData("CLIPRO_CODE", i))) {
					this.messageBox("�����Ѵ��ڣ�����ı���!");
					return false;
				}
			}
		}
		return true;
	}
}
