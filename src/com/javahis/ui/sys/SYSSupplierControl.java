package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:��Ӧ����
 * </p>
 *
 * <p>
 * Description:��Ӧ����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.08
 * @version 1.0
 */
public class SYSSupplierControl extends TControl {

	private String action = "save";
	// ������
	private TTable table;

	public SYSSupplierControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());
		TDataStore dataStore = table.getDataStore();
		if ("save".equals(action)) {
			TTextField combo = getTextField("SUP_CODE");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				if (!CheckData())
					return;
				row = table.getSelectedRow();
			}
			// ������Ϣ
			dataStore.setItem(row, "SUP_CODE", getValueString("SUP_CODE"));
			String desc = getValueString("SUP_CHN_DESC");
			dataStore.setItem(row, "SUP_CHN_DESC", desc);
			desc = getValueString("SUP_ENG_DESC");
			dataStore.setItem(row, "SUP_ENG_DESC", desc);
			desc = getValueString("SUP_ABS_DESC");
			dataStore.setItem(row, "SUP_ABS_DESC", desc);
			dataStore.setItem(row, "PY1", getValueString("PY1"));
			dataStore.setItem(row, "PY2", getValueString("PY2"));
			dataStore.setItem(row, "SEQ", getValueInt("SEQ"));
			desc = getValueString("DESCRIPTION");
			dataStore.setItem(row, "DESCRIPTION", desc);
			desc = getValueString("SELL_DEPT_CODE");
			dataStore.setItem(row, "SELL_DEPT_CODE", getValueString("SELL_DEPT_CODE"));
			// �������
			dataStore.setItem(row, "PHA_FLG", getValueString("PHA_FLG"));
			dataStore.setItem(row, "MAT_FLG", getValueString("MAT_FLG"));
			dataStore.setItem(row, "DEV_FLG", getValueString("DEV_FLG"));
			dataStore.setItem(row, "OTHER_FLG", getValueString("OTHER_FLG"));
			// ͨѶ��Ϣ
			dataStore.setItem(row, "SUP_BOSSNAME",
					getValueString("SUP_BOSSNAME"));
			dataStore.setItem(row, "SUP_IDNO", getValueString("SUP_IDNO"));
			dataStore.setItem(row, "SUP_TEL", getValueString("SUP_TEL"));
			dataStore.setItem(row, "SUP_FAX", getValueString("SUP_FAX"));
			dataStore.setItem(row, "NATIONAL_CODE",
					getValueString("NATIONAL_CODE"));
			dataStore.setItem(row, "POST_CODE", getValueString("POST_CODE"));
			dataStore.setItem(row, "ADDRESS", getValueString("ADDRESS"));
			dataStore.setItem(row, "E_MAIL", getValueString("E_MAIL"));
			dataStore.setItem(row, "WEBSITE", getValueString("WEBSITE"));
			dataStore.setItem(row, "BANK_CODE", getValueString("BANK_CODE"));
			dataStore.setItem(row, "BANK_IDNO", getValueString("BANK_IDNO"));
			dataStore.setItem(row, "BANK_NAME", getValueString("BANK_NAME"));
			// ҵ��Ա
			dataStore.setItem(row, "SUP_SALES1", getValueString("SUP_SALES1"));
			dataStore.setItem(row, "SUP_SALES2", getValueString("SUP_SALES2"));
			dataStore.setItem(row, "SUP_SALES3", getValueString("SUP_SALES3"));
			dataStore.setItem(row, "SUP_SALES1_TEL",
					getValueString("SUP_SALES1_TEL"));
			dataStore.setItem(row, "SUP_SALES2_TEL",
					getValueString("SUP_SALES2_TEL"));
			dataStore.setItem(row, "SUP_SALES3_TEL",
					getValueString("SUP_SALES3_TEL"));
			dataStore.setItem(row, "SUP_SALES1_EMAIL",
					getValueString("SUP_SALES1_EMAIL"));
			dataStore.setItem(row, "SUP_SALES2_EMAIL",
					getValueString("SUP_SALES2_EMAIL"));
			dataStore.setItem(row, "SUP_SALES3_EMAIL",
					getValueString("SUP_SALES3_EMAIL"));
			// ͣ��
			dataStore.setItem(row, "SUP_STOP_FLG",
					getValueString("SUP_STOP_FLG"));
			dataStore.setItem(row, "SUP_STOP_DATE", getValue("SUP_STOP_DATE"));
			dataStore.setItem(row, "SUP_END_DATE", getValue("SUP_END_DATE"));
			// OPT
			dataStore.setItem(row, "OPT_USER", Operator.getID());
			dataStore.setItem(row, "OPT_DATE", date);
			dataStore.setItem(row, "OPT_TERM", Operator.getIP());
		}
		if (dataStore.isModified()) {
			table.acceptText();
			if (!table.update()) {
				messageBox("E0001");
				table.removeRow(row);
				table.setDSValue();
				onClear();
				return;
			}
			table.setDSValue();
		}
		messageBox("P0001");
		table.setDSValue();
		onClear();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;
		table.removeRow(row);
		table.setSelectionMode(0);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "delete";
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSSupplier());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		String code = getValueString("SUP_CODE");
		String desc = getValueString("SUP_CHN_DESC");
		String filterString = "";
		if (code.length() > 0 && desc.length() > 0)
			filterString += "SUP_CODE like '" + code
					+ "%' AND SUP_CHN_DESC like '" + desc + "%'";
		else if (code.length() > 0)
			filterString += "SUP_CODE like '" + code + "%'";
		else if (desc.length() > 0)
			filterString += "SUP_CHN_DESC like '" + desc + "%'";
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "SUP_CODE;SUP_CHN_DESC;SUP_ENG_DESC;SUP_ABS_DESC;PY1;"
				+ "PY2;SEQ;DESCRIPTION;PHA_FLG;MAT_FLG;"
				+ "DEV_FLG;OTHER_FLG;SUP_BOSSNAME;SUP_IDNO;SUP_TEL;"
				+ "SUP_FAX;NATIONAL_CODE;POST_CODE;ADDRESS;E_MAIL;"
				+ "WEBSITE;BANK_CODE;BANK_IDNO;BANK_NAME;SUP_SALES1;"
				+ "SUP_SALES1_TEL;SUP_SALES1_EMAIL;SUP_SALES2;SUP_SALES2_TEL;SUP_SALES2_EMAIL;"
				+ "SUP_SALES3;SUP_SALES3_TEL;SUP_SALES3_EMAIL;SUP_STOP_FLG;SUP_STOP_DATE;"
				+ "SUP_END_DATE;POST_P;POST_C;SELL_DEPT_CODE";
		clearValue(clearString);
		// ���
		TDataStore dataStroe = table.getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		table.setSelectionMode(0);
		getTextField("SUP_CODE").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "SUP_CODE;SUP_CHN_DESC;SUP_ENG_DESC;SUP_ABS_DESC;PY1;"
					+ "PY2;SEQ;DESCRIPTION;PHA_FLG;MAT_FLG;"
					+ "DEV_FLG;OTHER_FLG;SUP_BOSSNAME;SUP_IDNO;SUP_TEL;"
					+ "SUP_FAX;NATIONAL_CODE;POST_CODE;ADDRESS;E_MAIL;"
					+ "WEBSITE;BANK_CODE;BANK_IDNO;BANK_NAME;SUP_SALES1;"
					+ "SUP_SALES1_TEL;SUP_SALES1_EMAIL;SUP_SALES2;SUP_SALES2_TEL;SUP_SALES2_EMAIL;"
					+ "SUP_SALES3;SUP_SALES3_TEL;SUP_SALES3_EMAIL;SUP_STOP_FLG;SUP_STOP_DATE;"
					+ "SUP_END_DATE;SELL_DEPT_CODE";
			this.setValueForParm(likeNames, parm);
			getTextField("SUP_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
			onSupStopFlgAction();
			onPostClick();
		}
	}

	/**
	 * SupDesc�س��¼�
	 */
	public void onSupDescAction() {
		String py = TMessage.getPy(this.getValueString("SUP_CHN_DESC"));
		setValue("PY1", py);
		getTextField("SUP_ENG_DESC").grabFocus();
	}

	/**
	 * ͨ�����д�����������
	 */
	public void selectCode_1() {
		this.setValue("POST_CODE", this.getValue("POST_C"));
		this.onPost();
	}

	/**
	 * ͨ���ʱ�ĵõ�ʡ��
	 */
	public void onPost() {
		String post = getValueString("POST_CODE");
		if (post == null || "".equals(post)) {
			getTextField("ADDRESS").grabFocus();
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("POST_P", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("POST_C", parm.getData("POST_CODE", 0).toString());
		setValue("ADDRESS", parm.getData("STATE", 0).toString()
				+ parm.getData("CITY", 0));
		getTextField("ADDRESS").grabFocus();
	}

	/**
	 * ͨ���ʱ�ĵõ�ʡ��
	 */
	public void onPostClick() {
		String post = getValueString("POST_CODE");
		if (post == null || "".equals(post)) {
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("POST_P", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("POST_C", parm.getData("POST_CODE", 0).toString());
		setValue("ADDRESS", parm.getData("STATE", 0).toString()
				+ parm.getData("CITY", 0));
	}

	/**
	 * �õ�ʡ�д���
	 *
	 * @param post
	 *            String
	 * @return TParm
	 */
	public TParm getPOST_CODE(String post) {
		TParm result = SYSPostTool.getInstance().getProvinceCity(post);
		return result;
	}

	/**
	 * ֹͣ�ɹ�ע���¼�
	 */
	public void onSupStopFlgAction() {
		if ("Y".equals(getValueString("SUP_STOP_FLG"))) {
			getTextFormat("SUP_STOP_DATE").setEnabled(true);
			getTextFormat("SUP_END_DATE").setEnabled(true);
		} else {
			this.setValue("SUP_STOP_DATE", null);
			this.setValue("SUP_END_DATE", null);
			getTextFormat("SUP_STOP_DATE").setEnabled(false);
			getTextFormat("SUP_END_DATE").setEnabled(false);
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSSupplier());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		// ����+1(SEQ)
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
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
	 * �õ�ComboBox����
	 *
	 * @param tagName
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("SUP_CODE"))) {
			this.messageBox("���̴��벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("SUP_CHN_DESC"))) {
			this.messageBox("�������Ʋ���Ϊ��");
			return false;
		}
		if (!"".equals(getValueString("E_MAIL"))) {
			if (!StringTool.isEmail(getValueString("E_MAIL"))) {
				this.messageBox("�����ʽ����ȷ");
				return false;
			}
		}
		if (!"".equals(getValueString("SUP_SALES1_EMAIL"))) {
			if (!StringTool.isEmail(getValueString("SUP_SALES1_EMAIL"))) {
				this.messageBox("ҵ��Աһ�������ʽ����ȷ");
				return false;
			}
		}
		if (!"".equals(getValueString("SUP_SALES2_EMAIL"))) {
			if (!StringTool.isEmail(getValueString("SUP_SALES2_EMAIL"))) {
				this.messageBox("ҵ��Ա���������ʽ����ȷ");
				return false;
			}
		}
		if (!"".equals(getValueString("SUP_SALES3_EMAIL"))) {
			if (!StringTool.isEmail(getValueString("SUP_SALES3_EMAIL"))) {
				this.messageBox("ҵ��Ա���������ʽ����ȷ");
				return false;
			}
		}
                /*if (!"".equals(getValueString("BANK_CODE"))) {
                 if ("".equals(getValueString("BANK_IDNO"))) {
                  this.messageBox("����ʺŲ���Ϊ��");
                  return false;
                 }
                 if ("".equals(getValueString("BANK_NAME"))) {
                  this.messageBox("�����������Ϊ��");
                  return false;
                 }
                   }*/
		if (!"N".equals(getValueString("SUP_STOP_FLG"))) {
			if ("".equals(getValueString("SUP_STOP_DATE"))) {
				this.messageBox("ֹͣ�ɹ���ʼ���ڲ���Ϊ��");
				return false;
			}
			if ("".equals(getValueString("SUP_END_DATE"))) {
				this.messageBox("ֹͣ�ɹ���ֹ���ڲ���Ϊ��");
				return false;
			}
			Timestamp start = (Timestamp) getTextFormat("SUP_STOP_DATE")
					.getValue();
			Timestamp end = (Timestamp) getTextFormat("SUP_END_DATE")
					.getValue();
			if (start.compareTo(end) > 0) {
				this.messageBox("��ʼ���ڲ������ڽ�ֹ����");
				return false;
			}
		}
		return true;
	}

	/**
	 * �õ����ı�� +1
	 *
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName,
			String dbBuffer) {
		if (dataStore == null)
			return 0;
		// ����������
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// ��������
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// �������ֵ
			if (max < value) {
				max = value;
				continue;
			}
		}
		// ���ż�1
		max++;
		return max;
	}

}
