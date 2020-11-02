package com.javahis.ui.sys;

import java.awt.event.KeyEvent;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TSystem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;


/**
 * <p> Title: 药品不良反应下拉选择框 </p>
 *
 * <p> Description: 药品不良反应下拉选择框 </p>
 *
 * <p> Copyright: Copyright (c) 2013 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author WangLong 2013.11.21
 * @version 1.0
 */
public class SYSADRPopupControl extends TControl {

    private String oldText = "";
    private TTable table;
    private int page = 0;
    private int index = 0;

	/**
	 * 初始化
	 */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener", "EDIT->" + TKeyListener.KEY_PRESSED, this,
                     "onKeyPressed");// 按键按下
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");// 按键弹起
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onTableDoubleClicked");// 行双击
        TDataStore tds = new TDataStore();
        tds.setSQL("SELECT * FROM ACI_ADRNAME ORDER BY ADR_CODE, ORGAN_CODE1 ");
        tds.retrieve();
        table.setDataStore(tds);
        table.setDSValue();
        initParamenter();
    }

	/**
	 * 初始化参数
	 */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null) {
            return;
        }
        if (!(obj instanceof TParm)) {
            return;
        }
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
    }

	/**
	 * 重新加载
	 */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null) {
            return;
        }
        if (!(obj instanceof TParm)) {
            return;
        }
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }
        setEditText(text);
    }

    /**
     * 设置输入文字
     * @param s
     */
    public void setEditText(String s) {
        page = 0;
        index = 0;
        setValue("CURRENT_PAGE", "" + (page + 1));
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

	/**
	 * 按键弹起事件
	 * @param s
	 */
    public void onKeyReleased(String s) {
        page = 0;
        index = 0;
        setValue("CURRENT_PAGE", "" + (page + 1));
        s = s.toUpperCase();
        if (oldText.equals(s)) {
            return;
        }
        oldText = s.toUpperCase();
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0) {
            table.setSelectedRow(0);
        }
    }

	/**
	 * 过滤方法
	 * @param parm
	 * @param row
	 * @return
	 */
    public boolean filter(TParm parm, int row) {
        boolean bool = false;
        if (!"en".equals((String) TSystem.getObject("Language"))) {
            bool =
                    parm.getValue("ADR_CODE", row).toUpperCase().startsWith(oldText)
                            || parm.getValue("ADR_DESC", row).toUpperCase().indexOf(oldText) != -1
                            || parm.getValue("PY", row).toUpperCase().indexOf(oldText) != -1;
        } else {
            bool =
                    parm.getValue("ADR_CODE", row).toUpperCase().startsWith(oldText)
                            || parm.getValue("ADR_ENG_DESC", row).toUpperCase().indexOf(oldText) != -1
                            || parm.getValue("PY", row).toUpperCase().indexOf(oldText) != -1;
        }
        if (bool) {
            index++;
            if (index < (page) * 19 || index > (page + 1) * 19){
                return false;
            } 
        }
        return bool;
    }

	/**
	 * 按键按下事件
	 * @param e
	 */
	public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            callFunction("UI|setVisible", false);
            return;
        }
        int count = (Integer) callFunction("UI|TABLE|getRowCount");
        if (count <= 0) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = (Integer) callFunction("UI|TABLE|getSelectedRow") - 1;
                if (row < 0) row = 0;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_DOWN:
                row = (Integer) callFunction("UI|TABLE|getSelectedRow") + 1;
                if (row >= count) row = count - 1;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                onSelected();
                break;
        }
    }

	/**
	 * 行双击事件
	 * @param row
	 */
    public void onTableDoubleClicked(int row) {
        if (row < 0) {
            return;
        }
        callFunction("UI|setVisible", false);
        onSelected();
    }

	/**
	 * 选中带回
	 */
    public void onSelected() {
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0) {
            return;
        }
        TDataStore dataStore = (TDataStore) callFunction("UI|TABLE|getDataStore");
        String adrID = dataStore.getItemString(row, "ADR_ID");
        String sql = "SELECT * FROM ACI_ADRNAME WHERE ADR_ID='#' ORDER BY ADR_CODE, ORGAN_CODE1 ";
        sql = sql.replaceFirst("#", adrID);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getErrCode() < 0 || parm.getCount() <= 0) {
            return;
        }
        parm = parm.getRow(0);
        setReturnValue(parm);
    }
    
	/**
	 * 向上翻页
	 */
	public void onUp() {
		page--;
		index = 0;
		if (page < 0) {
			page = 0;
			return;
		}
		setValue("CURRENT_PAGE", "" + (page + 1));
		table.filterObject(this, "filter");
		int count = table.getRowCount();
		if (count > 0)
			table.setSelectedRow(0);
	}

	/**
	 * 向下翻页
	 */
	public void onDown() {
		page++;
		index = 0;
		setValue("CURRENT_PAGE", "" + (page + 1));
		table.filterObject(this, "filter");
		int count = table.getRowCount();
		if (count > 0)
			table.setSelectedRow(0);
	}

}
