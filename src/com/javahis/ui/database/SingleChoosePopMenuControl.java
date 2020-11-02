package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import java.util.Vector;
import com.dongyang.ui.event.TKeyListener;
import java.awt.event.KeyEvent;

/**
 *
 * <p>Title: ��ѡ������ѡ��˵�������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.8.27
 * @version 1.0
 */
public class SingleChoosePopMenuControl extends TControl{
    /**
     * ��ѡ�ı�����
     */
    private ESingleChoose singleChoose;
    private TTable table;
    private TTextField value;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        singleChoose = (ESingleChoose) getParameter();
        if (singleChoose == null)
            return;
        table = (TTable)getComponent("TABLE");
        table.setValue(singleChoose.getData());
        String text = singleChoose.getText();
        value = (TTextField)getComponent("VALUE");
        value.setText(text);
        for(int i = 0;i < table.getRowCount();i++)
            if(text.equals(table.getValueAt(i,0)))
            {
                table.setSelectedRow(i);
                break;
            }
        table.setSelectedColumn(0);
        callFunction("UI|VALUE|addEventListener","VALUE->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
    }
    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            closeWindow();
            return;
        }
        int count = table.getRowCount();
        if(count <= 0)
            return;
        switch(e.getKeyCode()){
        case KeyEvent.VK_UP:
            int row = table.getSelectedRow() - 1;
            if(row < 0)
                row = 0;
            table.setSelectedRow(row);
            value.setText((String)table.getValueAt(row,0));
            value.selectAll();
            break;
        case KeyEvent.VK_DOWN:
            row = table.getSelectedRow() + 1;
            if(row >= count)
                row = count - 1;
            table.setSelectedRow(row);
            value.setText((String)table.getValueAt(row,0));
            value.selectAll();
            break;
        case KeyEvent.VK_ENTER:
            onSelect();
            break;
        default:
            table.clearSelection();
            break;
        }
    }
    /**
     * ѡ��
     */
    public void onSelect()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            singleChoose.setText(value.getText());
        else
            singleChoose.setText((String)table.getValueAt(row,0));
        singleChoose.getPM().getFocusManager().update();
        closeWindow();
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        onSelect();
    }
    /**
     * ȡ��
     */
    public void onCancel()
    {
        closeWindow();
    }
}
