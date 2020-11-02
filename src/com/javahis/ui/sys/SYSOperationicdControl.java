/**
 *
 */
package com.javahis.ui.sys;

import java.awt.Color;

import jdo.sys.Operator;
import jdo.sys.SYSOperationicdTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 * 
 * <p>
 * Title: ����ICD
 * </p>
 * 
 * <p>
 * Description:����ICD
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSOperationicdControl extends TControl {

	TParm data;
	int selectRow = -1;
	boolean flg = false;

	public void onInit() {
		super.onInit();

		callFunction("UI|save|setEnabled", false); // ���水ť�û�
		callFunction("UI|delete|setEnabled", false); // ɾ����ť�û�
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
	System.out.println("-----inInit-----");
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��ʼ�����棬��ѯ���е�����
	 * 
	 * @return TParm
	 */

	public void onQuery() {
		String OPERATION_ICD = getText("OPERATION_ICD");
		if (OPERATION_ICD == null || "".equals(OPERATION_ICD)) {
			// System.out.println("NULLLLLLLLLLLLLL");
			init();
		} else {
			data = SYSOperationicdTool.getInstance().selectdata(OPERATION_ICD + "%");
			// System.out.println("result:" + data);
			if (data.getErrCode() < 0) {
				messageBox(data.getErrText());
				return;
			}
			if (data.getCount() <= 0) {
				// this.messageBox("û������");
				this.callFunction("UI|TABLE|removeRowAll");
				return;
			}
			this.callFunction("UI|TABLE|setParmValue", data,
					"SEQ;OPERATION_ICD;OPT_CHN_DESC;PY1;PY2;DESCRIPTION;OPT_ENG_DESC;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;OPT_USER;OPT_DATE;PHA_PREVENCODE;DISABLE_FLG");
		}
		flg = false;
	}

	/**
	 * ������ť
	 */
	public void onNew() {
		this.setValue("OPERATION_ICD", "");
		this.grabFocus("OPERATION_ICD");
		callFunction("UI|save|setEnabled", true);
		callFunction("UI|query|setEnabled", false);
		this.getTextField("OPERATION_ICD").setEnabled(true);
		icdDescAction(true);
		flg = true;
		callFunction("UI|TABLE|removeRowAll");
	}

	/**
	 * У��icd �Ƿ��Ѿ�����
	 */
	public void checkIcd() {
		if (flg) {
			String operationIcd = getText("OPERATION_ICD");
			TParm result = SYSOperationicdTool.getInstance().selectdata("" + "%");
			for (int i = 0; i < result.getCount(); i++) {
				if (result.getValue("OPERATION_ICD", i).equals(operationIcd)) {
					this.messageBox("ICD���벻���ظ�");
					return;
				}
			}
		}
	}

	/**
	 * �����������ƿؼ�����ʾ
	 * 
	 * @param flg
	 */
	public void icdDescAction(boolean flg) {
		TTextField textfield = (TTextField) this.getTextField("ICD_DESC");
		TTextField OPT_CHN_DESC = (TTextField) getComponent("OPT_CHN_DESC");
		if (flg) {
			textfield.setVisible(false);
			OPT_CHN_DESC.setVisible(true);
			getLabel("tLabel_10").setColor("black");
		} else {
			textfield.setVisible(true);
			OPT_CHN_DESC.setVisible(false);
			getLabel("tLabel_10").setColor("blue");
		}
	}

	public void onTABLEClicked(int row) {
		callFunction("UI|save|setEnabled", true);
		callFunction("UI|delete|setEnabled", true);
		callFunction("UI|new|setEnabled", false);
		icdDescAction(true);
		// System.out.println("row=" + row);

		if (row < 0) {
			return;
		}
		setValueForParm(
				"OPERATION_ICD;OPT_CHN_DESC;OPT_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;PHA_PREVENCODE;DISABLE_FLG",
				data, row);

		selectRow = row;
		callFunction("UI|OPERATION_ICD|setEnabled", false);
		this.setValue("ICD_DESC", this.getValue("OPT_CHN_DESC"));
		flg = false;
	}

	/**
	 * ���
	 */
	public void onClear() {
		// System.out.println("new");

		this.clearValue(
				"OPERATION_ICD;ICD_DESC;OPT_CHN_DESC;OPT_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;PHA_PREVENCODE;DISABLE_FLG");
		this.clearValue("ICD_DESC");// add by huangjw 20150423
		// System.out.println("old");
		callFunction("UI|TABLE|removeRowAll");
		this.setValue("SEQ", "0");
		this.setValue("AVG_IPD_DAY", "0");
		this.setValue("AVG_OP_FEE", "0");
		selectRow = -1;
		callFunction("UI|OPERATION_ICD|setEnabled", true);
		callFunction("UI|save|setEnabled", false);
		callFunction("UI|delete|setEnabled", false);
		callFunction("UI|new|setEnabled", true);
		callFunction("UI|query|setEnabled", true);
		flg = false;
		icdDescAction(false);

	}

	/**
	 * ��ʼ�����棬��ѯ���е�����
	 * 
	 * @return TParm
	 */
	public void init() {
		data = SYSOperationicdTool.getInstance().selectall();
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", data,
				"SEQ;OPERATION_ICD;OPT_CHN_DESC;PY1;PY2;DESCRIPTION;OPT_ENG_DESC;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;PHA_PREVENCODE;OPT_USER;OPT_DATE;DISABLE_FLG");// add
	}

	/**
	 * �п�����ת������ add caoyong 2013830
	 */
	public String getType(String PHA_PREVENCODE) {
		String phatype = "";
		if ("001".equals(PHA_PREVENCODE)) {
			phatype = "I���п�";
		} else if ("002".equals(PHA_PREVENCODE)) {
			phatype = "II���п�";
		} else if ("003".equals(PHA_PREVENCODE)) {
			phatype = "III���п�";
		}
		return phatype;
	}

	/**
	 * ����
	 */
	public void onInsert() {
		if (!this.emptyTextCheck("OPERATION_ICD,OPT_CHN_DESC")) {
			return;
		}

		TParm parm = getParmForTag(
				"OPERATION_ICD;OPT_CHN_DESC;OPT_ENG_DESC;PY1;PY2;SEQ:int;DESCRIPTION;AVG_IPD_DAY:int;AVG_OP_FEE:int;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;PHA_PREVENCODE;DISABLE_FLG;");// add
		parm.setData("PHA_PREVENCODE", this.getValueString("PHA_PREVENCODE"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		SystemTool st = new SystemTool();
		parm.setData("OPT_DATE", st.getDate());
		// System.out.println("pram" + parm);
		data = SYSOperationicdTool.getInstance().insertdata(parm);
		if (data.getErrCode() < 0) {
			this.messageBox("E0002");
			onClear();
			init();
		} else {
			this.messageBox("P0002");
			onClear();
			init();
		}
	}

	/**
	 * ����
	 */
	public void onUpdate() {
		if (!this.emptyTextCheck("OPERATION_ICD,OPT_CHN_DESC")) {
			return;
		}
		TParm parm = getParmForTag(
				"SEQ:int;OPERATION_ICD;PY1;PY2;OPT_CHN_DESC;OPT_ENG_DESC;DESCRIPTION;AVG_IPD_DAY:int;AVG_OP_FEE:int;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;PHA_PREVENCODE;DISABLE_FLG;");// add
		// PHA_PREVENCODE
		// caoyong
		// 2013830
		// this.messageBox(String.valueOf(Operator.getData()));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		SystemTool st = new SystemTool();
		parm.setData("OPT_DATE", st.getDate());
		// System.out.println("pram" + parm);
		data = SYSOperationicdTool.getInstance().updatedata(parm);
		if (data.getErrCode() < 0) {
			this.messageBox("E0001");
			onClear();
			init();
			return;
		}
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0) {
			this.messageBox("E0001");
			onClear();
			init();
		} else {
			this.messageBox("P0001");
			onClear();
			init();
		}
		// ����ĩ��ĳ�е�ֵ
		// callFunction("UI|TABLE|setValueAt",getText("POS_DESC"),row,1);
		// callFunction("UI|TABLE|setValueAt",callFunction("UI|POS_TYPE|getSelectedID"),row,2);
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (selectRow == -1) {

			onInsert();
			return;
		}
		onUpdate();
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		// if(selectRow == -1)
		// return;
		if (!this.emptyTextCheck("OPERATION_ICD")) {
			return;
		}
		String poscode = getText("OPERATION_ICD");
		// if (this.messageBox("ȷ��ɾ��", "�Ƿ�ɾ��", 2) == 0) {
		data = SYSOperationicdTool.getInstance().deletedata(poscode);
		// } else {
		// return;
		// }

		if (data.getErrCode() < 0) {
			this.messageBox("E0003");
			onClear();
			init();
			return;
		}
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0) {
			this.messageBox("E0003");
			onClear();
			init();
		} else {
			this.messageBox("P0003");
			onClear();
			init();
		}
		// //ɾ��������ʾ
		// this.callFunction("UI|TABLE|removeRow",row);
		// this.callFunction("UI|TABLE|setSelectRow",row);

	}

	/**
	 * ���ݺ������ƴ������ĸ
	 */
	public void onCode() {
		String desc = this.getValueString("OPT_CHN_DESC");
		String py = TMessage.getPy(desc);
		this.setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
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
	 * �õ�TLabel����
	 * 
	 * @param tagName
	 * @return
	 */
	private TLabel getLabel(String tagName) {
		return (TLabel) getComponent(tagName);
	}

	/**
	 * ������������ OpICD
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponentOP() {

		TTextField textfield = (TTextField) this.getTextField("ICD_DESC");
		textfield.onInit();
		// ��table�ϵ���text����ICD10��������
		// textfield.setPopupMenuParameter("OP_ICD",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
		callFunction("UI|ICD_DESC|setPopupMenuParameter", "OP_ICD", "%ROOT%\\config\\sys\\SYSOpICD.x");// ҽ������
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "newOPOrder");
	}

	/**
	 * ȡ������ICD����ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOPOrder(String tag, Object obj) {
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("OPERATION_ICD");
		if ("en".equals(this.getLanguage())) {
			this.setValue("ICD_DESC", parm.getValue("OPT_ENG_DESC"));
		} else {
			this.setValue("ICD_DESC", parm.getValue("OPT_CHN_DESC"));
		}
		this.setValue("OPERATION_ICD", orderCode);
		this.onQuery();
		// TTable table=(TTable) this.getComponent("TABLE");
		/*
		 * table.setSelectedRow(0); this.onTABLEClicked(0);
		 */

	}
}
