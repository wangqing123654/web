package com.javahis.ui.spc;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;

/**
 *
 * <p>Title: SYS Fee ����ѡ����������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author liyh 20121011
 * @version 1.0
 */
public class INDPoisonBaleSelectFrid2Control
    extends TControl {
	private TTable table;
	private String oldText = "";
	private String sup_code;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) callFunction("UI|TABLE|getThis");
		callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED,
				this, "onKeyReleased");
		callFunction("UI|EDIT|addEventListener", "EDIT->"
				+ TKeyListener.KEY_PRESSED, this, "onKeyPressed");
		table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
				"onDoubleClicked");
		initParamenter();
	}

	/**
	 * ��ʼ������
	 */
	public void initParamenter() {
		Object obj = getParameter();
/*		String sql = "SELECT ORDER_CAT1_CODE,ORDER_CAT1_DESC FROM SYS_ORDER_CAT1 ";
		// ��ʼ��TABLE
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);*/
		 TParm tParm = new TParm();
	        tParm.setData("ORG_NAME", 0, "סԺҩ��");
	        tParm.setData("FRID_ID", 0, "1");
	        tParm.setData("FRID_CODE", 0, "A0001");
	        tParm.setData("FRID_DESC", 0, "����1");
	        tParm.setData("ALL_COUNT", 0, "0");
	        tParm.setData("CURRENT_COUNT", 0, "10");
	        
	        tParm.setData("ORG_NAME", 1, "סԺҩ��");
	        tParm.setData("FRID_ID", 1, "2");
	        tParm.setData("FRID_CODE", 1, "A0002");
	        tParm.setData("FRID_DESC", 1, "����2");
	        tParm.setData("ALL_COUNT", 1, "0");
	        tParm.setData("CURRENT_COUNT", 1, "10");

	        tParm.setData("ORG_NAME", 2, "סԺҩ��");
	        tParm.setData("FRID_ID", 2, "3");
	        tParm.setData("FRID_CODE", 2, "A0003");
	        tParm.setData("FRID_DESC", 2, "����3");
	        tParm.setData("ALL_COUNT", 2, "0");
	        tParm.setData("CURRENT_COUNT", 2, "10");
	        
	        table.setParmValue(tParm);
	}

	/**
	 * ������������
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
	 * �����¼�
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
	 * �����¼�
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
			callFunction("UI|TABLE|setSelectedRow", row);
			break;
		case KeyEvent.VK_DOWN:
			row = (Integer) callFunction("UI|TABLE|getSelectedRow") + 1;
			if (row >= count)
				row = count - 1;
			callFunction("UI|TABLE|setSelectedRow", row);
			break;
		case KeyEvent.VK_ENTER:
			callFunction("UI|setVisible", false);
			onSelected();
			break;
		}
	}

	/**
	 * ���˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public void filter() {
		String sql = "SELECT ORDER_CAT1_CODE,ORDER_CAT1_DESC,PY1 FROM SYS_ORDER_CAT1 WHERE PY1 LIKE '"
				+ this.getValueString("EDIT").toUpperCase() + "%'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
	}

	/**
	 * ��˫���¼�
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
	 * ѡ��
	 */
	public void onSelected() {
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0)
			return;
		TParm parm = table.getParmValue().getRow(row);
		setReturnValue(parm);
		this.closeWindow();
	}

}
