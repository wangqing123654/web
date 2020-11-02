package com.javahis.ui.opd;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

/**
 * 
 * <p>
 * Title: 门诊医生工作站常用诊断调用下拉框
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站常用诊断调用下拉框
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author huangtt 20150310
 * @version 1.0
 */
public class OPDComDiagPopupControl extends TControl {
	private String oldText = "";
	private TTable table;
	private String icdType = "";
	private String deptOrDr = "";

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) callFunction("UI|TABLE|getThis");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				this.getSql("")));
		table.setParmValue(result);
		callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED,
				this, "onKeyReleased");
		callFunction("UI|EDIT|addEventListener", "EDIT->"
				+ TKeyListener.KEY_PRESSED, this, "onKeyPressed");
		table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
				"onDoubleClicked");

		initParamenter();

	}

	/**
	 * 重新加载
	 */
	public void onInitReset() {
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		icdType = parm.getValue("ICD_TYPE");
		deptOrDr = parm.getValue("DEPTORDR_CODE");
		String text = parm.getValue("TEXT");
		String oldText = (String) callFunction("UI|EDIT|getText");
		if (oldText.equals(text))
			return;
		setEditText(text);
	}

	/**
	 * 初始化参数
	 */
	public void initParamenter() {
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		icdType = parm.getValue("ICD_TYPE");
		deptOrDr = parm.getValue("deptOrDr");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				this.getSql("")));
		table.setParmValue(result);

		String text = parm.getValue("TEXT");
		setEditText(text);

	}

	/**
	 * 设置输入文字
	 * 
	 * @param s
	 *            String
	 */
	public void setEditText(String s) {
		callFunction("UI|EDIT|setText", s);
		int x = s.length();
		callFunction("UI|EDIT|select", x, x);
		onKeyReleased(s);
	}

	/**
	 * 按键事件
	 * 
	 * @param s
	 *            String
	 */
	public void onKeyReleased(String s) {
		s = s.toUpperCase();
		if (oldText.equals(s))
			return;
		oldText = s;
		filter();
		int count = table.getRowCount();
		if (count > 0)
			table.setSelectedRow(0);
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public void filter() {
		String text = getValueString("EDIT").toUpperCase();
		String sql = this.getSql(text);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
	}

	/**
	 * 按键事件
	 * 
	 * @param e
	 *            KeyEvent
	 */
	public void onKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			callFunction("UI|setVisible", false);
			return;
		}
		int count = (Integer) callFunction("UI|TABLE|getRowCount");
		if (count <= 0)
			return;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			int row = (Integer) callFunction("UI|TABLE|getSelectedRow") - 1;
			if (row < 0)
				row = 0;
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			// callFunction("UI|TABLE|setSelectedRow",row);
			break;
		case KeyEvent.VK_DOWN:
			row = (Integer) callFunction("UI|TABLE|getSelectedRow") + 1;
			if (row >= count)
				row = count - 1;
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			// callFunction("UI|TABLE|setSelectedRow",row);
			break;
		case KeyEvent.VK_ENTER:
			callFunction("UI|setVisible", false);
			onSelected();
			break;
		}
	}

	/**
	 * 行双击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onDoubleClicked(int row) {
		if (row < 0)
			return;
		callFunction("UI|setVisible", false);
		onSelected();
	}

	/**
	 * 选中
	 */
	public void onSelected() {
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0)
			return;
		TParm parm = table.getParmValue().getRow(row);
		setReturnValue(parm);
		this.closeWindow();
	}

	public String getSql(String text) {
		String sql = "SELECT  A.ICD_CODE AS ICD_CODE,B.ICD_CHN_DESC,B.ICD_ENG_DESC,"
				+ " A.SEQ AS SEQ,A.ICD_TYPE ICD_TYPE,B.PY1"
				+ " FROM OPD_COMDIAG A, SYS_DIAGNOSIS B"
				+ " WHERE B.ICD_CODE = A.ICD_CODE ";

		if (!"".equals(getValueString("EDIT").toUpperCase())) {
			sql += " AND (A.ICD_CODE LIKE '" + text + "%' "
					+ "OR B.ICD_CHN_DESC LIKE '%" + text + "%' "
					+ "OR B.ICD_ENG_DESC LIKE '%" + text + "%' "
					+ "OR B.PY1 LIKE '%" + text + "%' )";
		}

		if (deptOrDr.length() > 0) {
			sql = sql + " AND A.DEPTORDR_CODE = '" + deptOrDr + "'"
					+ " AND A.ICD_TYPE = '" + icdType + "'";
		}

		sql = sql + " ORDER BY A.SEQ, A.ICD_CODE";
		return sql;
	}

}
