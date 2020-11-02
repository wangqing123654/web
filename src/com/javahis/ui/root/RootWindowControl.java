package com.javahis.ui.root;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.ui.TTree;
import com.javahis.system.root.RootClientListener;
import com.javahis.system.root.CheckThread;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: ����ͨѶ���ڿ�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.21
 * @version 1.0
 */
public class RootWindowControl extends TControl{
    TTree tree;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
        tree = (TTree)getComponent("TREE");
        showName();
        //��ʼ����
        initTree();
    }
    /**
     * ��ʾ����
     */
    public void showName()
    {
        setValue("NAME",CheckThread.getName() + "[" +
                 (RootClientListener.getInstance().isClient()?"��������":"����")
                 + "]");
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
    /**
     * �����¼�
     */
    public void onLoginEvent()
    {
        showName();
    }

    /**
     * �����¼�
     */
    public void onLogoutEvent()
    {
        showName();
    }
    /**
     * ��ʼ����
     */
    public void initTree()
    {
        TTreeNode root = tree.getRoot();
        root.setText("ͨѶ��");
        root.setType("Root");
        root.removeAllChildren();
        TParm parm = CheckThread.getGroup();
        for(int i = 0; i < parm.getCount();i++)
        {
            String id = parm.getValue("GROUP_ID",i);
            String name = parm.getValue("GROUP_NAME",i);
            TTreeNode node = new TTreeNode(name,"Group");
            node.setID(id);
            initGroup(node,id);
            root.add(node);
        }
        tree.update();
    }
    /**
     * ��ʼ����
     * @param root TTreeNode
     * @param groupID String
     */
    public void initGroup(TTreeNode root,String groupID)
    {
        TParm parm = CheckThread.getGroupUser(groupID);
        for(int i = 0; i < parm.getCount();i++)
        {
            String id = parm.getValue("LINK_ID",i);
            String name = parm.getValue("NAME",i);
            TTreeNode node = new TTreeNode(name,"User");
            node.setID(id);
            root.add(node);
        }
    }
    /**
     * ����
     */
    public void onFind()
    {
        RootClientListener.getInstance().openFindWindow();
    }
    /**
     * �����û���
     * @param id String
     * @param name String
     * @param groupID String
     */
    public void onCreateGroupUser(String id,String name,String groupID)
    {
        TTreeNode root = tree.getRoot();
        TTreeNode node = null;
        for(int i = 0;i < root.getChildCount();i++)
        {
            TTreeNode groupNode = (TTreeNode)root.getChildAt(i);
            if(groupNode.getID().equals(groupID))
            {
                node = new TTreeNode(name,"User");
                node.setID(id);
                groupNode.add(node);
                return;
            }
        }
        tree.update();
        if(node != null)
            tree.setSelectNode(node);
    }
    /**
     * ˫������Ŀ
     */
    public void onDoubleItemTree()
    {
        TTreeNode node = tree.getSelectionNode();
        if(node == null)
            return;
        RootClientListener.getInstance().openMessageWindow(node.getID());
    }
}
