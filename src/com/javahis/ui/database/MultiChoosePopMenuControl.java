package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import java.awt.event.KeyEvent;
import com.dongyang.tui.text.EMultiChoose;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import java.util.Vector;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ��ѡ������ѡ��˵�������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author lzk 2009.8.28
 * @version 1.0
 */
public class MultiChoosePopMenuControl extends TControl{
    /**
     * ��ѡ�ı�����
     */
    private EMultiChoose multiChoose;
    private TTable table;
    private TTextField value;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        multiChoose = (EMultiChoose) getParameter();
        if (multiChoose == null)
            return;
        table = (TTable)getComponent("TABLE");
        String text = multiChoose.getText();
        table.setValue(newVector(text,multiChoose.getData()));
        value = (TTextField)getComponent("VALUE");
        value.setText(text);
        table.setSelectedColumn(0);
        value.addEventListener("VALUE->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onChoose");
        addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE,"onTableChangeValue");
    }
    public void onChoose(Object obj)
    {
        table.acceptText();
    }
    public void onTableChangeValue(Object obj)
    {
        TTableNode node=(TTableNode)obj;
        String text = value.getText();
        //this.messageBox("==text=="+text);
        //String text ="";
        if("Y".equals(node.getValue()))
        {
            if(text.length() > 0)
                text += "��";
            text += table.getValueAt(node.getRow(),1);
        }else
        {
            String s = (String)table.getValueAt(node.getRow(),1);
            String s1[] = StringTool.parseLine(text,"��");
            StringBuffer sb = new StringBuffer();
            for(int i = 0;i < s1.length;i++)
            {
                if (s.equals(s1[i]))
                    continue;
                if(sb.length() > 0)
                    sb.append("��");
                sb.append(s1[i]);
            }
            text = sb.toString();
        }
        //ȥ��ע��˵���ı�
        if(!text.equals("")){
            String s1[] = StringTool.parseLine(text,"��");
            if(!isExistInTable(s1[0])){
                //this.messageBox("������");
               text=text.substring(text.indexOf("��")+1,text.length());
            }
        }
        value.setText(text);
    }
    /**
     * Vector ��¡
     * @param s String
     * @param v Vector
     * @return Vector
     */
    public Vector newVector(String s,Vector v)
    {
        Vector data = new Vector();
        String s1[] = StringTool.parseLine(s,"��");
        for(int i = 0;i < v.size();i++)
        {
            Vector t = (Vector)v.get(i);
            Vector t1 = new Vector();
            data.add(t1);
            String name = (String)t.get(0);
            if(inString(name,s1))
                t1.add("Y");
            else
                t1.add("N");
            t1.add(name);
        }
        return data;
    }
    public boolean inString(String s,String s1[])
    {
        for(int i = 0;i < s1.length;i++)
            if(s.equals(s1[i]))
                return true;
        return false;
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
        multiChoose.setText(value.getText());
        multiChoose.getPM().getFocusManager().update();
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

    /**
     * �ı��Ƿ��ڱ���д���;
     * @param str String text�е��ı�
     * @return boolean �Ƿ����;
     */
    private boolean isExistInTable(String str){
        //this.messageBox("ע��str"+str);
        boolean flg=false;
        int count = table.getRowCount();
        for(int i=0;i<count;i++){
             String s = (String)table.getValueAt(i,1);
            // this.messageBox("table s==="+s);
             if(str.equalsIgnoreCase(s)){
                 flg=true;
                 break;
             }
        }
        return flg;
    }
}
