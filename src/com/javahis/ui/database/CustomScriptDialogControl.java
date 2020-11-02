package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextArea;

/**
 *
 * <p>Title: ���Զ���ű��Ի��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2009.4.16
 * @version 1.0
 */
public class CustomScriptDialogControl extends TControl{
    private TTextArea functionScript;
    private TTextArea importScript;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        functionScript = (TTextArea)getComponent("FUNCTION");
        importScript = (TTextArea)getComponent("IMPORT");
        Object obj[] = (Object[])getParameter();
        if(obj == null)
            return;
        importScript.setText((String)obj[0]);
        functionScript.setText((String)obj[1]);
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        setReturnValue(new Object[]{importScript.getValue(),
                       functionScript.getValue()});
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
