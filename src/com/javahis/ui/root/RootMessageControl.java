package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.dongyang.ui.TRootPanel;
import com.javahis.system.root.CheckThread;
import com.javahis.system.root.RootClientListener;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTextArea;

/**
 *
 * <p>Title: ����ͨѶ��Ϣ���ڿ�����</p>
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
public class RootMessageControl extends TControl{
    private String id;
    private String name;
    private TRootPanel rootPanel;
    private TTextArea messageArea;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        rootPanel = (TRootPanel)getComponent("Root");
        messageArea = (TTextArea)getComponent("MESSAGE");
        String[] parm = (String[])getParameter();
        if(parm == null)
            return;
        id = parm[0];
        name = CheckThread.getName(id);
        rootPanel.setTitle(name);
    }
    /**
     * �õ�������Ϣ
     * @return String
     */
    public String getSendMessage()
    {
        return getText("SENDM_ESSAGE");
    }
    /**
     * ������Ϣ
     */
    public void onSend()
    {
        String message = getSendMessage();
        inMessage(new Date(),Operator.getName(),message);
        RootClientListener.getInstance().getClient().sendMessage(id,message);
        setText("SENDM_ESSAGE","");
    }
    /**
     * �ر�
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setMessageWindow(id,null);
        return true;
    }
    /**
     * ������Ϣ
     * @param time Timestamp
     * @param name String
     * @param message String
     */
    public void inMessage(Timestamp time,String name,String message)
    {
        inMessage(new Date(time.getTime()),name,message);
    }
    /**
     * ������Ϣ
     * @param date Date
     * @param name String
     * @param message String
     */
    public void inMessage(Date date,String name,String message)
    {
        StringBuffer s = new StringBuffer();
        s.append(getText("MESSAGE"));
        if(s.length() > 0)
            s.append("\n");
        s.append(StringTool.getString(date,"HH:mm:ss") + " " + name + "\n");
        s.append(message);
        messageArea.setText(s.toString());
        messageArea.getTextArea().setSelectionStart(s.length());
        messageArea.getTextArea().setSelectionEnd(s.length());
    }
}
