package com.javahis.ui.sys;

import java.awt.event.KeyEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ҽ��ģ��������</p>
 *
 * <p>Description: ҽ��ģ��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.27
 * @version 1.0
 */
public class SYSOpdComOrderControl
    extends TControl {
    private String oldText = "";
    private TTable table;
    private int page = 0;
    private int index = 0;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        initParamenter();
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
        String text = parm.getValue("TEXT");
        setEditText(text);
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
//        System.out.println("���"+parm);
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }

        setEditText(text);
    }

    /**
     * ������������
     * @param s String
     */
    public void setEditText(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE", "" + (page + 1));
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * �����¼�
     * @param s String
     */
    public void onKeyReleased(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE", "" + (page + 1));
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm, int row) {
        boolean result =
            (parm.getValue("ORDER_CODE", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             parm.getValue("ORDER_DESC", row).toUpperCase().indexOf(oldText) !=
             -1);

        if (result) {
            index++;
            if (index < (page) * 19 || index > (page + 1) * 19)
                return false;
        }
        return result;
    }

    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            callFunction("UI|setVisible", false);
            return;
        }
        int count = table.getRowCount();
        if (count <= 0)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0)
                    row = 0;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count)
                    row = count - 1;
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
     * ��˫���¼�
     * @param row int
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
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        TDataStore dataStore = table.getDataStore();
        String orderCode = dataStore.getItemString(row, "ORDER_CODE");
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            " SELECT * FROM OPD_COMORDER WHERE ORDER_CODE='" + orderCode + "' "));
        if (parm.getErrCode() < 0 || parm.getCount() <= 0)
            return;
        parm = parm.getRow(0);
        setReturnValue(parm);
    }

    public void onUp() {
        page--;
        index = 0;
        if (page < 0) {
            page = 0;
            return;
        }
        setValue("L_PAGE", "" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    public void onDown() {
        page++;
        index = 0;
        setValue("L_PAGE", "" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

//    /**
//     * �һ�MENU�����¼�
//     * @param tableName
//     */
//    public void onShowPopMenu() {
//        TTable table = (TTable)this.getComponent("TABLE");
//        table.setPopupMenuSyntax(
//            "��ʾ����ҽ��ϸ��|Show LIS/RIS Detail Items,onOrderSetShow");
//    }
//
//    /**
//     * �һ�MENU��ʾ����ҽ���¼�
//     */
//    public void onOrderSetShow(){
//        TTable table = (TTable)this.getComponent("TABLE");
//        int row = table.getSelectedRow();
//        String order_code = table.getDataStore().getItemString(row, "ORDER_CODE");
//        String sql = "SELECT A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY, "
//            + " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY "
//            + " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE "
//            + " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
//            + " WHERE A.ORDER_CODE = B.ORDERSET_CODE "
//            + " AND B.ORDERSET_CODE = '" + order_code + "' "
//            + " ORDER BY B.ORDERSET_CODE, B.ORDER_CODE";
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        this.openDialog("%ROOT%\\config\\Zodo\\OPDOrderSetShow.x", result);
//
//    }

}
