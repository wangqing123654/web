package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: ��Ի���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.14
 * @version 1.0
 */
public class GroupDialogControl extends TControl{
    /**
     * ����Table
     */
    private TTable table;
    /**
     * ��ʾ��������
     */
    private TCheckBox showData;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
        showData = (TCheckBox)getComponent("SHOW_DATA");
        Object p[] = (Object[])getParameter();
        TParm parm = null;
        if(p != null)
        {
            parm = (TParm) p[0];
            showData.setSelected((Boolean)p[1]);
        }
        if(parm == null)
            parm = new TParm();
        table.setParmValue(parm);
    }
    /**
     * ���
     */
    public void onAdd()
    {
        TParm parm = new TParm();
        String newid = getNewID();
        parm.setData("ID",newid);
        parm.setData("NAME","��" + newid);
        parm.setData("TITLE",true);
        parm.setData("SUM",true);
        int row = table.addRow(-1,parm);
        table.setSelectedRow(row);
    }
    /**
     * �õ��±��
     * @return String
     */
    private String getNewID()
    {
        TParm parm = table.getParmValue();
        if(parm == null)
            return "0";
        int id = -1;
        for(int i = 0; i < parm.getCount("ID");i++)
            id = Math.max(id, TypeTool.getInt(parm.getData("ID", i)));
        return "" + (id + 1);
    }
    /**
     * ɾ��
     */
    public void onDelete()
    {
        int row = table.getSelectedRow();
        table.removeRow();
        if(table.getRowCount() == 0)
            return;
        if(row >= table.getRowCount())
            row = table.getRowCount() - 1;
        table.setSelectedRow(row);
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        table.acceptText();
        TParm parm = table.getParmValue();
        setReturnValue(new Object[]{parm,showData.isSelected()});
        closeWindow();
    }
    /**
     * ȡ��
     */
    public void onCancel()
    {
        closeWindow();
    }
    /**
     * ����������
     * @param args String[]
     */
    public static void main(String args[])
    {
        TParm parm = new TParm();
        parm.addData("ID","1");
        parm.addData("NAME","����");
        parm.addData("TITLE",true);
        parm.addData("SUM",true);
        parm.setCount(1);

        Object o[] = (Object[])com.javahis.util.JavaHisDebug.runDialog("database\\GroupDialog.x");//,new Object[]{parm,true});

        System.out.println(o[0]);
        System.out.println(o[1]);
    }
}
