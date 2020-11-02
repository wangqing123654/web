package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.dongyang.root.client.SocketLink;
import com.javahis.system.root.RootClientListener;
import jdo.sys.Operator;

/**
 *
 * <p>Title: ����ͨѶ��Ӻ�������Ի��������</p>
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
public class AddFriendSendControl extends TControl{
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
        setText("MESSAGE","��������" + Operator.getName() + "��");
    }
    /**
     * ȷ��
     */
    public void onOK()
    {
        closeWindow();
        RootClientListener.getInstance().getClient().sendCommand(id,SocketLink.ADD_FRIEND,getText("MESSAGE"));
    }
}
