package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.javahis.system.root.RootClientListener;
import com.dongyang.ui.TTable;
import com.javahis.system.root.CheckThread;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import com.dongyang.root.client.SocketLink;

/**
 *
 * <p>Title: ����ͨѶ���Һ���</p>
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
public class RootFindControl extends TControl{
    private TTable table;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
    }
    /**
     * �õ�����ID
     * @return String
     */
    public String getID()
    {
        return getText("ID");
    }
    /**
     * �õ������ǳ�
     * @return String
     */
    public String getName()
    {
        return getText("NAME");
    }
    /**
     * �ر�
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setFindWindow(null);
        return true;
    }
    /**
     * ����
     */
    public void onFind()
    {
        TParm parm = CheckThread.findUser(getID(),getName());
        if(parm.getErrCode() < 0)
            return;
        table.setParmValue(parm);
        if(parm.getCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * ��Ӻ���
     */
    public void onADD()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        String id = TypeTool.getString(table.getValueAt(row,0));
        String name = TypeTool.getString(table.getValueAt(row,1));
        if(id == null)
            return;
        if(id.equals(Operator.getID()))
        {
            messageBox_("���ܽ��Լ���λ����!");
            return;
        }
        if(CheckThread.isFriend(id))
        {
            messageBox_(name + "�Ѿ������ĺ�����!");
            return;
        }
        int addType = CheckThread.getAddType(id);
        if(addType == 1)
        {
            messageBox_("�Է��ܾ���Ϊ����!");
            return;
        }
        RootClientListener.getInstance().openAddFindSend(id,name);
    }
}
