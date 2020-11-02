package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import java.awt.event.KeyEvent;
import jdo.inv.INVSQL;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class INVSupInvControl
    extends TControl {

    public INVSupInvControl() {
    }

    private String oldText = "";
    private TTable table;
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
     * ���¼���
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text))
            return;
        setEditText(text);
    }

    /**
     * ��ʼ������
     */
    public void initParamenter() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        sup_code = parm.getValue("SUP_CODE");
        // ��ʼ��TABLE
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INVSQL.getSupInv(sup_code, "")));
        table.setParmValue(result);
        String text = parm.getValue("TEXT");
        setEditText(text);
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
     * ���˷���
     *
     * @param parm
     *            TParm
     * @param row
     *            int
     * @return boolean
     */
    public void filter() {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INVSQL.getSupInv(sup_code, getValueString("EDIT")
                               .toUpperCase())));
        table.setParmValue(result);
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
