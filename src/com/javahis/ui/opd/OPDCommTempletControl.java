package com.javahis.ui.opd;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.system.textFormat.TextFormatEMRTemplet;

/**
 * <p>
 * Title: ����ģ��
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class OPDCommTempletControl extends TControl {
	//���Կ���
	private boolean isDebug = false;

	private String action = "save";
	// ������
	private TTable table;

	private String type = "2";

	private String m_param;

	private String templatecat;

	private TextFormatEMRTemplet textFormatemr;

	public OPDCommTempletControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		m_param = (String) this.getParameter();
		type = m_param.split(";")[0];
		templatecat = m_param.split(";")[1];
		if (isDebug) {
			System.out.println("---type--" + type);
			System.out.println("---m_templatecat--" + templatecat);
		}

		// ��ʼ��Table
		table = getTable("TABLE");// tTable_0 TABLE

		textFormatemr = (TextFormatEMRTemplet) this
				.getComponent("SUBCLASS_CODE");
		String subClassCode = "";

		if (templatecat.equals("SUB")) {
			subClassCode = TConfig
					.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");

		} else if (templatecat.equals("PHY")) {
			subClassCode = TConfig.getSystemValue("ONWEmrMRCODE");
		}

		if (isDebug) {
			System.out.println("---����ģ��Ϊ---" + subClassCode);
		}

		textFormatemr.setSubClassCode(subClassCode);

		// SUBCLASS_CODE||'_'||SEQ
		String sql = "SELECT SUBCLASS_CODE||'_'||SEQ SUBCLASS_CODE,"
				+ " SEQ, MAIN_FLG, OPT_USER, OPT_DATE, OPT_TERM,DEPT_OR_DR, DEPTORDR_CODE "
				+ " FROM OPD_COMTEMPLET " + " WHERE DEPT_OR_DR = '" + type
				+ "' ";
		// + " WHERE DEPT_OR_DR = '2' ";

		// SUBCLASS_CODE;SEQ;MAIN_FLG;OPT_USER;OPT_DATE;OPT_TERM;DEPT_OR_DR;DEPTORDR_CODE
		String where = "";
		// 1������ģ�壻2�� ҽ��ģ��
		if ("1".equals(type)) {
			where = " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
			((TTextFormat) this.getComponent("DEPT_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("DR_CODE")).setVisible(false);
			this.setValue("DEPT_CODE", Operator.getDept());
			this.setValue("LAB_TEXT", "���ң�");
		} else if ("2".equals(type)) {
			where = " AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
			((TTextFormat) this.getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("DR_CODE")).setVisible(true);
			this.setValue("DR_CODE", Operator.getID());
			this.setValue("LAB_TEXT", "ҽ����");
		} else if ("3".equals(type)) {
			where = " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
			((TTextFormat) this.getComponent("DEPT_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("DR_CODE")).setVisible(false);
			this.setValue("DEPT_CODE", Operator.getDept());
			this.setValue("LAB_TEXT", "���ң�");

		} else if ("4".equals(type)) {
			where = " AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
			((TTextFormat) this.getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("DR_CODE")).setVisible(true);
			this.setValue("DR_CODE", Operator.getID());
			this.setValue("LAB_TEXT", "��ʿ��");
		}

		//
		String code = getValueString("SUBCLASS_CODE");
		if (code != null && code.length() > 0) {

			where += " AND SUBCLASS_CODE='" + code.split("_")[0] + "'";
			where += " AND SEQ='" + code.split("_")[1] + "'";
		}

		String order_by = " ORDER BY DEPTORDR_CODE, MAIN_FLG DESC, SUBCLASS_CODE, SEQ ";

		String strSQL = sql + where + order_by;
		/*
		 * TDataStore dataStore = new TDataStore();
		 * 
		 * System.out.println("------sql111------"+sql + where + order_by);
		 * dataStore.setSQL(sql + where + order_by); dataStore.retrieve();
		 * table.setDataStore(dataStore); table.setDSValue();
		 */
		if (isDebug) {
			System.out.println("------SQL------" + strSQL);
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(strSQL));

		table.setParmValue(result);

		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());

		String main_sql = "";
		// ���� ����
		if ("1".equals(type)) {
			main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
					+ "WHERE DEPT_OR_DR = '1' AND DEPTORDR_CODE = '"
					+ getValueString("DEPT_CODE") + "' AND MAIN_FLG = 'Y'";
			// ҽ�� ����
		} else if ("2".equals(type)) {
			main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
					+ "WHERE DEPT_OR_DR = '2' AND DEPTORDR_CODE = '"
					+ getValueString("DR_CODE") + "' AND MAIN_FLG = 'Y' ";
			// ���� �����ɼ�
		} else if ("3".equals(type)) {
			main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
					+ "WHERE DEPT_OR_DR = '3' AND DEPTORDR_CODE = '"
					+ getValueString("DEPT_CODE") + "' AND MAIN_FLG = 'Y'";

			// ��ʿ �����ɼ�
		} else if ("4".equals(type)) {
			main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
					+ "WHERE DEPT_OR_DR = '4' AND DEPTORDR_CODE = '"
					+ getValueString("DR_CODE") + "' AND MAIN_FLG = 'Y' ";

		}

		if ("save".equals(action)) {
			TParm result = new TParm(TJDODBTool.getInstance().select(main_sql));
			if (result != null && result.getCount() > 0
					&& "Y".equals(this.getValueString("MAIN_FLG"))) {
				this.messageBox("�Ѵ�����ģ�壬������ѡ��");
				return;
			}

			String deptOrDR = "";
			String deptOrDRCode = "";
			if ("1".equals(type)) {
				deptOrDR = "1";
				deptOrDRCode = getValueString("DEPT_CODE");
			} else if ("2".equals(type)) {
				deptOrDR = "2";
				deptOrDRCode = getValueString("DR_CODE");
			} else if ("3".equals(type)) {
				deptOrDR = "3";
				deptOrDRCode = getValueString("DEPT_CODE");
			} else if ("4".equals(type)) {
				deptOrDR = "4";
				deptOrDRCode = getValueString("DR_CODE");
			}
			if (isDebug) {
				System.out.println("++SUBCLASS_CODE++----"
						+ getValueString("SUBCLASS_CODE"));
			}
			String strSubClassCode = getValueString("SUBCLASS_CODE").split("_")[0];
			String strSeq = getValueString("SUBCLASS_CODE").split("_")[1];
			String strMainFlg = getValueString("MAIN_FLG");
			TTextFormat combo = getTextFormat("SUBCLASS_CODE");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				// row = table.addRow();
			} else {
				row = table.getSelectedRow();
				return;
			}

			String insSQL = "INSERT INTO OPD_COMTEMPLET VALUES(";
			insSQL += "'" + deptOrDR + "',";
			insSQL += "'" + deptOrDRCode + "',";
			insSQL += "'" + strSubClassCode + "',";
			insSQL += "'" + strSeq + "',";
			insSQL += "'" + Operator.getID() + "',";
			insSQL += "SYSDATE,";
			insSQL += "'" + Operator.getIP() + "',";
			insSQL += "'" + strMainFlg + "'";
			insSQL += ")";
			if (isDebug) {
				System.out.println("-----insSQL-----" + insSQL);
			}
			TParm parm = new TParm(TJDODBTool.getInstance().update(insSQL));
			// ����ģ���Ӧ������
			// ����
			if ("3".equals(type)) {
				/*String updateSQL = "UPDATE EMR_TEMPLET SET DEPT_CODE='"
				+ deptOrDRCode + "'";*/
				String updateSQL = "UPDATE EMR_TEMPLET SET DEPT_CODE='"
					+ "" + "'";//dept_code,����ֶβ�����modify by huangjw 20150108
				updateSQL += " WHERE SUBCLASS_CODE='" + strSubClassCode + "'";
				updateSQL += " AND SEQ='" + strSeq + "'";
				if (isDebug) {
					System.out
							.println("+++++++userUpdateSQL++++++" + updateSQL);
				}
				//
				TParm parm1 = new TParm(TJDODBTool.getInstance().update(
						updateSQL));
				if (parm1.getErrCode() != 0) {
					messageBox("E0001");
					return;
				}
				// ��ʿ
			} else if ("4".equals(type)) {
				String updateSQL = "UPDATE EMR_TEMPLET SET USER_ID='"
						+ deptOrDRCode + "'";
				updateSQL += " WHERE SUBCLASS_CODE='" + strSubClassCode + "'";
				updateSQL += " AND SEQ='" + strSeq + "'";
				if (isDebug) {
					System.out
							.println("+++++++deptUpdateSQL++++++" + updateSQL);
				}
				//
				TParm parm1 = new TParm(TJDODBTool.getInstance().update(
						updateSQL));
				if (parm1.getErrCode() != 0) {
					messageBox("E0001");
					return;
				}

			}
			if (parm.getErrCode() != 0) {
				messageBox("E0001");
				return;
			}

			messageBox("P0001");
			this.onQuery();
			/*
			 * table.setItem(row, "SUBCLASS_CODE",
			 * getValueString("SUBCLASS_CODE").split("_")[0]); String sql =
			 * "SELECT SEQ FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '" +
			 * getValueString("SUBCLASS_CODE") + "' AND OPD_FLG = 'Y' "; TParm
			 * parm = new TParm(TJDODBTool.getInstance().select(sql));
			 * 
			 * table.setItem(row, "SEQ",
			 * getValueString("SUBCLASS_CODE").split("_"
			 * )[1]);//parm.getInt("SEQ",0) table.setItem(row, "MAIN_FLG",
			 * getValueString("MAIN_FLG")); table.setItem(row, "OPT_USER",
			 * Operator.getID()); table.setItem(row, "OPT_DATE", date);
			 * table.setItem(row, "OPT_TERM", Operator.getIP());
			 */
		}

		/*
		 * TDataStore dataStore = table.getDataStore(); if
		 * (dataStore.isModified()) { table.acceptText(); if (!table.update()) {
		 * messageBox("E0001"); table.removeRow(row); table.setDSValue();
		 * onClear(); return; } table.setDSValue(); } messageBox("P0001");
		 * table.setDSValue();
		 */
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getParmValue().getRow(row);
			String likeNames = "SUBCLASS_CODE;MAIN_FLG";
			this.setValueForParm(likeNames, parm);
			getTextFormat("SUBCLASS_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		initPage();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.setValue("SUBCLASS_CODE", "");
		this.setValue("MAIN_FLG", "N");
		table.setSelectionMode(0);
		((TTextFormat) this.getComponent("SUBCLASS_CODE")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
		onQuery();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;

		TParm parm = table.getParmValue().getRow(row);
		String delSQL = "DELETE FROM OPD_COMTEMPLET";
		delSQL += " where DEPT_OR_DR='" + parm.getValue("DEPT_OR_DR") + "'";
		delSQL += " AND DEPTORDR_CODE='" + parm.getValue("DEPTORDR_CODE") + "'";
		delSQL += " AND SUBCLASS_CODE='"
				+ parm.getValue("SUBCLASS_CODE").split("_")[0] + "'";
		delSQL += " AND SEQ='" + parm.getValue("SEQ") + "'";
		if (isDebug) {
			System.out.println("-----delSQL-----" + delSQL);
		}
		TParm parm1 = new TParm(TJDODBTool.getInstance().update(delSQL));
		if (parm1.getErrCode() != 0) {
			messageBox("E0001");
			return;
		}
		messageBox("P0001");
		this.onQuery();

		//
		// table.removeRow(row);
		// table.setSelectionMode(0);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "delete";

	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("SUBCLASS_CODE"))) {
			this.messageBox("ģ���Ų���Ϊ��");
			return false;
		}
		return true;
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
