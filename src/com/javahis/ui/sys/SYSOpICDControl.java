package com.javahis.ui.sys;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import java.awt.event.KeyEvent;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import jdo.sys.Operator;

/**
 * <p>Title: ����ICD</p>
 *
 * <p>Description: ����ICD</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-8
 * @version 1.0
 */
public class SYSOpICDControl extends TControl{
    private String oldText = "";
    private TTable table;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        super.onInit();
        table = (TTable)callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener",TTextFieldEvent.KEY_RELEASED,this,"onKeyReleased");
        callFunction("UI|EDIT|addEventListener","EDIT->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED,this,"onDoubleClicked");
        initParamenter();
    }
    /**
     * ��ʼ������
     */
    public void initParamenter()
    {
        Object obj = getParameter();
        if(obj == null)
            return;
        if(!(obj instanceof TParm))
            return;
        TParm parm = (TParm)obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
    }
    /**
     * ���¼���
     */
    public void onInitReset()
    {
        Object obj = getParameter();
        if(obj == null)
            return;
        if(!(obj instanceof TParm))
            return;
        TParm parm = (TParm)obj;
        String text = parm.getValue("TEXT");
        String oldText = (String)callFunction("UI|EDIT|getText");
        if(oldText.equals(text))
            return;
        setEditText(text);
    }
    /**
     * ������������
     * @param s String
     */
    public void setEditText(String s)
    {
        callFunction("UI|EDIT|setText",s);
        int x = s.length();
        callFunction("UI|EDIT|select",x,x);
        onKeyReleased(s);
    }
    /**
     * �����¼�
     * @param s String
     */
    public void onKeyReleased(String s)
    {
        s = s.toUpperCase();
        if(oldText.equals(s))
            return;
        oldText = s.toUpperCase();
        filter();
        int count = table.getRowCount();
        if(count > 0)
            table.setSelectedRow(0);
    }
    /**
     * ���˷���
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public void filter()
    {
        String sql = "OPERATION_ICD like '" + getValueString("EDIT") +
            "%' OR OPT_CHN_DESC like '" + getValueString("EDIT") +
            "%' OR PY1 like '" + getValueString("EDIT").toUpperCase() + "%'";
        table.setFilter(sql);
        table.filter();
    }
    /**
     * �����¼�
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            callFunction("UI|setVisible",false);
            return;
        }
        int count = (Integer)callFunction("UI|TABLE|getRowCount");
        if(count <= 0)
            return;
        switch(e.getKeyCode()){
        case KeyEvent.VK_UP:
            int row = (Integer)callFunction("UI|TABLE|getSelectedRow") - 1;
            if(row < 0)
                row = 0;
            callFunction("UI|TABLE|setSelectedRow",row);
            break;
        case KeyEvent.VK_DOWN:
            row = (Integer)callFunction("UI|TABLE|getSelectedRow") + 1;
            if(row >= count)
                row = count - 1;
            callFunction("UI|TABLE|setSelectedRow",row);
            break;
        case KeyEvent.VK_ENTER:
            callFunction("UI|setVisible",false);
            onSelected();
            break;
        }
    }
    /**
     * ��˫���¼�
     * @param row int
     */
    public void onDoubleClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|setVisible",false);
        onSelected();
    }
    /**
     * ѡ��
     */
    public void onSelected()
    {
        int row = (Integer)callFunction("UI|TABLE|getSelectedRow");
        if(row < 0)
            return;
        TDataStore dataStore = (TDataStore)callFunction("UI|TABLE|getDataStore");
        TParm parm = dataStore.getRowParm(row);
        setReturnValue(parm);
    }
    /**
      *
      * @param args String[]
      */
//     public static void main(String args[]) {
//         com.javahis.util.JavaHisDebug.TBuilder();
//         Operator.setData("admin", "HIS", "127.0.0.1", "C00101");
//     }


}
