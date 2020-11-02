package com.javahis.ui.root;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.javahis.system.root.RootClientListener;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.javahis.system.root.CheckThread;

/**
 *
 * <p>Title: ����ͨѶע�ᴰ�ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.22
 * @version 1.0
 */
public class RootRegisterControl extends TControl{
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        setValue("ID",Operator.getID());
        setValue("NAME",Operator.getName());
        setValue("LNAME",Operator.getName());
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        String name = TypeTool.getString(getValue("LNAME"));
        if(name.length() == 0)
        {
            messageBox_("�������ǳ�!");
            grabFocus("LNAME");
            return;
        }
        String password = TypeTool.getString(getValue("PASSWORD"));
        String rpassword = TypeTool.getString(getValue("RPASSWORD"));
        if(password.length() == 0)
        {
            messageBox_("����������!");
            grabFocus("PASSWORD");
            return;
        }
        if(!password.equals(rpassword))
        {
            messageBox_("�����ȷ�����벻һ��!");
            grabFocus("PASSWORD");
            return;
        }
        boolean autoLogin = ((TCheckBox)getComponent("AUTO_LOGIN")).isSelected();
        boolean onlyOne = ((TCheckBox)getComponent("ONLY_ONE")).isSelected();
        int addType = 0;
        if(((TRadioButton)getComponent("R2")).isSelected())
            addType = 1;
        if(((TRadioButton)getComponent("R3")).isSelected())
            addType = 2;
        if(!CheckThread.register(Operator.getID(),name,password,onlyOne,autoLogin,addType))
        {
            messageBox_("ע��ʧ��!");
            return;
        }
        boolean loginNow = ((TCheckBox)getComponent("LOGIN_NOW")).isSelected();
        messageBox_("ע��ɹ�!");
        closeWindow();
        if(loginNow)
        {
            RootClientListener.getInstance().setPassword(password);
            RootClientListener.getInstance().login();
        }
    }
    /**
     * �ر�
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setMainWindow(null);
        return true;
    }
}
