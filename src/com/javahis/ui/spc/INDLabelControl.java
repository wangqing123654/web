package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDLabelTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatINDOrgForWord;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:���ӱ�ǩ��վ��������ά��
 * </p>
 * 
 * <p>
 * Description:���ӱ�ǩ��վ��������ά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-05-21
 * @version 1.0
 */
public class INDLabelControl extends TControl {

	// ����ȫ��TTable�ؼ�
	private TTable table;

	public void onInit() {
		table = this.getTTable("TABLE");
	}

	/**
	 * ���TTable�ؼ�
	 * 
	 * @param tag
	 *            �ؼ�TAG
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ���TRadioButton�ؼ�
	 * 
	 * @param tag
	 *            �ؼ�TAG
	 * @return
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * ���TextFormatINDOrgForWord�ؼ�
	 * 
	 * @param tag
	 *            �ؼ�TAG
	 * @return
	 */
	public TextFormatINDOrgForWord getTextFormatINDOrgForWord(String tag) {
		return (TextFormatINDOrgForWord) getComponent(tag);
	}

	/**
	 * ���TextFormatSYSStation�ؼ�
	 * 
	 * @param tag
	 *            �ؼ�TAG
	 * @return
	 */
	public TextFormatSYSStation getTextFormatSYSStation(String tag) {
		return (TextFormatSYSStation) getComponent(tag);
	}

	/**
	 * ������ѡ���ʹ�ò�������,���ò�ͬ�ؼ�
	 */
	public void onClassifyChange() {
		if (getTRadioButton("CLASSIFY_B").isSelected()) {
			getTextFormatINDOrgForWord("LABEL_IDA").setVisible(false);
			getTextFormatSYSStation("LABEL_IDB").setVisible(true);
		} else if (getTRadioButton("CLASSIFY_A").isSelected()) {
			getTextFormatSYSStation("LABEL_IDB").setVisible(false);
			getTextFormatINDOrgForWord("LABEL_IDA").setVisible(true);
		}
	}

	/**
	 * ��ձ����ؼ�
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "LABEL_IDA;LABEL_IDB;AP_REGION";
		clearValue(tags);
		if (getTextFormatINDOrgForWord("LABEL_IDA").isVisible()) {
			getTextFormatINDOrgForWord("LABEL_IDA").setEnabled(true);
		} else {
			getTextFormatSYSStation("LABEL_IDB").setEnabled(true);
		}
	}

	/**
	 * ��񵥻�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		TParm selParm = table.getParmValue().getRow(row);
		if ("����".equals(selParm.getData("CLASSIFY")))
			getTRadioButton("CLASSIFY_A").setSelected(true);
		else
			getTRadioButton("CLASSIFY_B").setSelected(true);
		if (getTextFormatINDOrgForWord("LABEL_IDA").isVisible()) {
			setValue("LABEL_IDA", selParm.getData("LABEL_ID") + " "
					+ table.getItemData(row, "ORG_DESC"));
			getTextFormatINDOrgForWord("LABEL_IDA").setEnabled(false);
		} else {
			setValue("LABEL_IDB", selParm.getData("LABEL_ID") + " "
					+ table.getItemData(row, "ORG_DESC"));
			getTextFormatSYSStation("LABEL_IDB").setEnabled(false);
		}
		setValue("AP_REGION", selParm.getData("AP_REGION"));
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String LABEL_ID = (String) (getTextFormatINDOrgForWord("LABEL_IDA")
				.isVisible() ? getTextFormatINDOrgForWord("LABEL_IDA")
				.getValue() : getTextFormatSYSStation("LABEL_IDB").getValue());
		if (!StringUtil.isNullString(LABEL_ID)) {
			parm.setData("LABEL_ID", LABEL_ID);
		}
		String CLASSIFY = getTRadioButton("CLASSIFY_A").isSelected() ? "1"
				: "2";
		if (!StringUtil.isNullString(CLASSIFY)) {
			parm.setData("CLASSIFY", CLASSIFY);
		}
		TParm result = INDLabelTool.getInstance().queryInfo(parm);
		for (int i = 0; i < result.getCount(); i++) {
			if ("1".equals(result.getData("CLASSIFY", i)))
				result.setData("CLASSIFY", i, "����");
			else
				result.setData("CLASSIFY", i, "��ʿվ");
		}
		table.setParmValue(result);
	}

	/**
	 * ���������
	 */
	public void onSave() {
		if (!checkData())
			return;
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm parm = new TParm();
		parm.setData("LABEL_ID", getTextFormatINDOrgForWord("LABEL_IDA")
				.isVisible() ? getValueString("LABEL_IDA")
				: getValueString("LABEL_IDB"));
		parm.setData("AP_REGION", getValueString("AP_REGION"));
		parm.setData("CLASSIFY", getTRadioButton("CLASSIFY_A").isSelected() ? 1
				: 2);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		if (table.getSelectedRow() < 0) {
			TParm result = INDLabelTool.getInstance().insertInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				return;
			}
			messageBox("����ɹ�");
		} else {
			parm.setData("LABEL_ID", table.getParmValue().getRow(
					table.getSelectedRow()).getData("LABEL_ID"));
			TParm result = INDLabelTool.getInstance().updateInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				return;
			}
			messageBox("���³ɹ�");
		}
		onClear();
		onQuery();
	}

	/**
	 * ����У��
	 */
	public boolean checkData() {
		if (getTextFormatINDOrgForWord("LABEL_IDA").isVisible()) {
			if (getValueString("LABEL_IDA").length() == 0
					|| getValueString("LABEL_IDA") == "")
				return false;
		} else if (getTextFormatINDOrgForWord("LABEL_IDB").isVisible()) {
			if (getValueString("LABEL_IDB").length() == 0
					|| getValueString("LABEL_IDB") == "")
				return false;
		}
		if (getValueString("AP_REGION").length() == 0
				|| getValueString("AP_REGION") == "")
			return false;
		return true;
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫɾ��������");
			return;
		}
		TParm parm = new TParm();
		parm.setData("LABEL_ID", table.getItemData(row, "LABEL_ID"));
		TParm result = INDLabelTool.getInstance().deleteInfo(parm);
		if (result.getErrCode() < 0) {
			messageBox("ɾ��ʧ��");
			return;
		}
		messageBox("ɾ���ɹ�");
		onClear();
		onQuery();
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		table = getTTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ "���ӱ�ǩ��վ��������ͳ�Ʊ�");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// �������
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		// ��������е�Ԫ��
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("LABEL_ID", tableParm.getValue("LABEL_ID", i));
			parm.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i));
			parm.addData("CLASSIFY", tableParm.getValue("CLASSIFY", i));
			parm.addData("AP_REGION", tableParm.getValue("AP_REGION", i));
			parm.addData("OPT_USER", tableParm.getValue("OPT_USER", i));
			parm.addData("OPT_DATE", tableParm.getValue("OPT_DATE", i));
			parm.addData("OPT_TERM", tableParm.getValue("OPT_TERM", i));
		}
		// ������
		parm.setCount(parm.getCount("LABEL_ID"));
		parm.addData("SYSTEM", "COLUMNS", "LABEL_ID");
		parm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
		parm.addData("SYSTEM", "COLUMNS", "CLASSIFY");
		parm.addData("SYSTEM", "COLUMNS", "AP_REGION");
		parm.addData("SYSTEM", "COLUMNS", "OPT_USER");
		parm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		parm.addData("SYSTEM", "COLUMNS", "OPT_TERM");
		// �����ŵ�������
		System.out.println(parm.getData());
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\INDLabel.jhw", data);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "���ӱ�ǩ��վ���������ѯ");
	}

}
