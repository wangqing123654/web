package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.dongyang.root.client.SocketLink;
import com.javahis.system.root.RootClientListener;
import jdo.sys.Operator;
import com.javahis.system.root.CheckThread;

/**
 *
 * <p>Title: ����ͨѶİ���������Ϊ��������Ի��������</p>
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
public class ADDFriendLookControl extends TControl{
    private String id;
    private String name;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        String[] parm = (String[])getParameter();
        if(parm == null)
            return;
        id = parm[0];
        name = parm[1];
        setText("ID",id);
        setText("NAME",name);
        setText("MESSAGE",parm[2]);
    }
    /**
     * ����
     */
    public void onOK()
    {
        RootClientListener.getInstance().createGroupUser(id,name,"01");
        RootClientListener.getInstance().getClient().sendCommand(id,SocketLink.ADD_FRIEND_PASS,"");
        closeWindow();
    }
    /**
     * �ܾ�
     */
    public void onCancel()
    {
        RootClientListener.getInstance().getClient().sendCommand(id,SocketLink.ADD_FRIEND_DENY,"");
        closeWindow();
    }
}
