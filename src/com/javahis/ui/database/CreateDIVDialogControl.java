package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.div.MV;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: ����DIV�Ի��������</p>
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
public class CreateDIVDialogControl extends TControl{
    private MV mv;
    private TTable table;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
        mv = (MV)getParameter();
        //��ʼ��Table
        initTable();
    }
    /**
     * ��ʼ��Table
     */
    public void initTable()
    {
        TParm parm = mv.getCreateTParm();
        table.setParmValue(parm);
        if(table.getRowCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        String name = getText("NAME");
        if(name.length() == 0)
        {
            messageBox_("����������!");
            grabFocus("NAME");
            return;
        }
        setReturnValue(new Object[]{table.getSelectedRow() + 1,name});
        closeWindow();
    }
    /**
     * ȡ��
     */
    public void onCancel()
    {
        closeWindow();
    }
}
