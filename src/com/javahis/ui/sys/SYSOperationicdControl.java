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
 * Title: 手术ICD
 * </p>
 * 
 * <p>
 * Description:手术ICD
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

		callFunction("UI|save|setEnabled", false); // 保存按钮置灰
		callFunction("UI|delete|setEnabled", false); // 删除按钮置灰
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
	System.out.println("-----inInit-----");
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 初始化界面，查询所有的数据
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
				// this.messageBox("没有数据");
				this.callFunction("UI|TABLE|removeRowAll");
				return;
			}
			this.callFunction("UI|TABLE|setParmValue", data,
					"SEQ;OPERATION_ICD;OPT_CHN_DESC;PY1;PY2;DESCRIPTION;OPT_ENG_DESC;AVG_IPD_DAY;AVG_OP_FEE;OPE_LEVEL;STA1_CODE;STA1_DESC;OPERATION_TYPE;OPT_USER;OPT_DATE;PHA_PREVENCODE;DISABLE_FLG");
		}
		flg = false;
	}

	/**
	 * 新增按钮
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
	 * 校验icd 是否已经存在
	 */
	public void checkIcd() {
		if (flg) {
			String operationIcd = getText("OPERATION_ICD");
			TParm result = SYSOperationicdTool.getInstance().selectdata("" + "%");
			for (int i = 0; i < result.getCount(); i++) {
				if (result.getValue("OPERATION_ICD", i).equals(operationIcd)) {
					this.messageBox("ICD代码不可重复");
					return;
				}
			}
		}
	}

	/**
	 * 处理中文名称控件的显示
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
	 * 清空
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
	 * 初始化界面，查询所有的数据
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
	 * 切口类型转换汉文 add caoyong 2013830
	 */
	public String getType(String PHA_PREVENCODE) {
		String phatype = "";
		if ("001".equals(PHA_PREVENCODE)) {
			phatype = "I类切口";
		} else if ("002".equals(PHA_PREVENCODE)) {
			phatype = "II类切口";
		} else if ("003".equals(PHA_PREVENCODE)) {
			phatype = "III类切口";
		}
		return phatype;
	}

	/**
	 * 新增
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
	 * 更新
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
		// 设置末行某列的值
		// callFunction("UI|TABLE|setValueAt",getText("POS_DESC"),row,1);
		// callFunction("UI|TABLE|setValueAt",callFunction("UI|POS_TYPE|getSelectedID"),row,2);
	}

	/**
	 * 保存
	 */
	public void onSave() {
		if (selectRow == -1) {

			onInsert();
			return;
		}
		onUpdate();
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		// if(selectRow == -1)
		// return;
		if (!this.emptyTextCheck("OPERATION_ICD")) {
			return;
		}
		String poscode = getText("OPERATION_ICD");
		// if (this.messageBox("确定删除", "是否删除", 2) == 0) {
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
		// //删除单行显示
		// this.callFunction("UI|TABLE|removeRow",row);
		// this.callFunction("UI|TABLE|setSelectRow",row);

	}

	/**
	 * 根据汉字输出拼音首字母
	 */
	public void onCode() {
		String desc = this.getValueString("OPT_CHN_DESC");
		String py = TMessage.getPy(desc);
		this.setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}

	/**
	 * 得到TextField对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TLabel对象
	 * 
	 * @param tagName
	 * @return
	 */
	private TLabel getLabel(String tagName) {
		return (TLabel) getComponent(tagName);
	}

	/**
	 * 手术弹出界面 OpICD
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponentOP() {

		TTextField textfield = (TTextField) this.getTextField("ICD_DESC");
		textfield.onInit();
		// 给table上的新text增加ICD10弹出窗口
		// textfield.setPopupMenuParameter("OP_ICD",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
		callFunction("UI|ICD_DESC|setPopupMenuParameter", "OP_ICD", "%ROOT%\\config\\sys\\SYSOpICD.x");// 医嘱代码
		// 给新text增加接受ICD10弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "newOPOrder");
	}

	/**
	 * 取得手术ICD返回值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOPOrder(String tag, Object obj) {
		// sysfee返回的数据包
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
