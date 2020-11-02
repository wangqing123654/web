package com.javahis.system.root;

import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TComponent;
import jdo.sys.Operator;
import javax.swing.JOptionPane;
import com.dongyang.ui.TFrame;
import com.dongyang.manager.TCM_Transform;
import java.awt.Component;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TWindow;
import com.dongyang.config.TConfigParm;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * <p>Title: ����ͨѶ�ͻ��˳���</p>
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
public class RootClientListener {
    /**
     * �ͻ���
     */
    private SocketLink client;
    /**
     * �õ��û���
     */
    private String userID;
    /**
     * �û�����
     */
    private String password;
    /**
     * ϵͳ�����ڿؼ�
     */
    private TComponent sysFrame;
    /**
     * ��������
     */
    private List listenerFrame;
    /**
     * �ر�״̬
     */
    private boolean isClose;
    /**
     * ͨѶ������
     */
    private TWindow mainWindow;
    /**
     * ���Ҵ���
     */
    private TWindow findWindow;
    /**
     * ������Ϣ�����б�
     */
    private Map messageWindows;
    /**
     * ʵ��
     */
    public static RootClientListener instanceObject;
    /**
     * �õ�ʵ��
     * @return RootClientListener
     */
    public static RootClientListener getInstance() {
        if (instanceObject == null)
            instanceObject = new RootClientListener();
        return instanceObject;
    }
    /**
     * ������
     */
    public RootClientListener()
    {
        //�����û���
        setUserID(Operator.getID());
        messageWindows = new HashMap();
        listenerFrame = new ArrayList();
    }
    /**
     * ���Ӽ���
     * @param com TComponent
     */
    public void addListener(TComponent com)
    {
        listenerFrame.add(com);
    }
    /**
     * ɾ������
     * @param com TComponent
     */
    public void removeListener(TComponent com)
    {
        listenerFrame.remove(com);
    }

    /**
     * ���ÿͻ���
     * @param client SocketLink
     */
    public void setClient(SocketLink client)
    {
        this.client = client;
    }
    /**
     * �õ��ͻ���
     * @return SocketLink
     */
    public SocketLink getClient()
    {
        return client;
    }
    /**
     * �Ƿ�����
     * @return boolean
     */
    public boolean isClient()
    {
        if(getClient() == null)
            return false;
        return !getClient().isClose();
    }
    /**
     * �����û�ID
     * @param ID String
     */
    public void setUserID(String ID)
    {
        this.userID = ID;
    }
    /**
     * �õ��û�ID
     * @return String
     */
    public String getUserID()
    {
        return userID;
    }
    /**
     * ���õ�¼����
     * @param password String
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    /**
     * �õ���¼����
     * @return String
     */
    public String getPassword()
    {
        return password;
    }
    /**
     * ����ϵͳ������
     * @param sysFrame TComponent
     */
    public void setSysFrame(TComponent sysFrame)
    {
        this.sysFrame = sysFrame;
    }
    /**
     * �õ�ϵͳ������
     * @return TComponent
     */
    public TComponent getSysFrame()
    {
        return sysFrame;
    }
    /**
     * ���ò��Ҵ���
     * @param findWindow TWindow
     */
    public void setFindWindow(TWindow findWindow)
    {
        this.findWindow = findWindow;
    }
    /**
     * �õ����Ҵ���
     * @return TWindow
     */
    public TWindow getFindWindow()
    {
        return findWindow;
    }
    /**
     * ����״̬��Ϣ
     * @param message String
     */
    public void setSysStatus(String message)
    {
        TComponent com = getSysFrame();
        if(com == null)
            return;
        com.callFunction("setSysStatus",message);
    }
    /**
     * ���ùر�״̬
     * @param isClose boolean
     */
    public void setIsClose(boolean isClose)
    {
        this.isClose = isClose;
    }
    /**
     * �Ƿ�ر�
     * @return boolean
     */
    public boolean isClose()
    {
        return isClose;
    }
    /**
     * ��¼
     */
    public void login()
    {
        //��¼�߳�
        LoginThread.start(this);
    }
    /**
     * ��ʼ��
     */
    public void init()
    {
        //�����߳�
        CheckThread.start(this);
    }
    /**
     * ����
     */
    public void listener()
    {
        getClient().addEventListener("close",this,"onLogoutEvent");
        getClient().addEventListener("Command",this,"onCommand");
        getClient().addEventListener("Message",this,"onMessage");
        getClient().listener();
        setSysStatus("����ͨѶ��¼�ɹ�!");
        if(mainWindow != null)
            mainWindow.callFunction("onLoginEvent");
    }
    /**
     * �ر�
     */
    public void close()
    {
        setIsClose(true);
        if(getClient() != null)
            getClient().close();
    }
    /**
     * �Ͽ��¼�
     */
    public void onLogoutEvent()
    {
        setSysStatus("����ͨ������");
        if(mainWindow != null)
            mainWindow.callFunction("onLogoutEvent");
        if(!isClose())
            login();
    }
    /**
     * ִ������
     * @param id String ���Ʊ��
     * @param action int ��������
     * @param obj Object ���Ʋ���
     */
    public void onCommand(String id,int action,Object obj)
    {
        switch(action)
        {
            case SocketLink.ADD_FRIEND:
                addFriendRequest(id,obj);
                return;
            case SocketLink.ADD_FRIEND_DENY:
                messageBox(CheckThread.getName() + "�ܾ���Ϊ����");
                return;
            case SocketLink.ADD_FRIEND_PASS:
                String name = CheckThread.getName(id);
                CheckThread.createGroupUser(id,name,"01");
                if(mainWindow != null)
                    mainWindow.callFunction("onCreateGroupUser",id,name,"01");
                messageBox(name + "��������Ϊ����");
                return;
        }
    }
    /**
     * ������Ϣ
     * @param time Timestamp
     * @param id String
     * @param message String
     */
    public void onMessage(Timestamp time,String id,String message)
    {
        if(message.startsWith("#ACTION#"))
        {
            onAction(id,message.substring(8));
            return;
        }
        //System.out.println("-----id111-----"+id);
        //System.out.println("-----message222-----"+message);
        //Message:off ϵͳ�����Ĳ�����
        if(!message.equalsIgnoreCase("Message:off")){       
        	//System.out.println("-----not Message:off-----");
	        TWindow window = openMessageWindow(id);
	        if(window == null)
	            return;
	        window.callFunction("inMessage",time,CheckThread.getName(id),message);
        }
    }
    /**
     * ����
     * @param id String
     * @param action String
     */
    public void onAction(String id,String action)
    {
        if(action.startsWith("UI|"))
        {
            for(int i = 0;i < listenerFrame.size();i++)
            {
                TComponent com = (TComponent)listenerFrame.get(i);
                Object v = com.callMessage(action);
                if(v != null)
                    return;
            }
            getSysFrame().callMessage(action);
            return;
        }
        if(action.equalsIgnoreCase("onClose"))
        {
            getSysFrame().callFunction("onClose");
            return;
        }
        if(action.equalsIgnoreCase("Exit"))
        {
            System.exit(0);
            return;
        }
    }
    /**
     * �������û�
     * @param id String
     * @param name String
     * @param groupID String
     */
    public void createGroupUser(String id,String name,String groupID)
    {
        CheckThread.createGroupUser(id,name,groupID);
        if(mainWindow != null)
            mainWindow.callFunction("onCreateGroupUser",id,name,groupID);
    }
    /**
     * ��Ϊ��������
     * @param id String
     * @param obj Object
     */
    public void addFriendRequest(String id,Object obj)
    {
        switch(CheckThread.getAddType())
        {
            case 0:
                createGroupUser(id,CheckThread.getName(id),"01");
                getClient().sendCommand(id,SocketLink.ADD_FRIEND_PASS,"");
                return;
            case 1:
                getClient().sendCommand(id,SocketLink.ADD_FRIEND_DENY,"");
                return;
            case 2:
                openAddFindLook(id,CheckThread.getName(id),TypeTool.getString(obj));
                return;
        }
    }
    /**
     * ����ͨѶ������
     * @param window TWindow
     */
    public void setMainWindow(TWindow window)
    {
        mainWindow = window;
    }
    /**
     * �õ�ͨѶ������
     * @return TWindow
     */
    public TWindow getMainWindow()
    {
        return mainWindow;
    }
    /**
     * ��ͨѶ������
     */
    public void openMainWindow()
    {
        if(mainWindow != null)
            return;
        mainWindow = (TWindow)openWindow("%ROOT%\\config\\root\\RootWindow.x",true);
        mainWindow.setVisible(true);
    }
    /**
     * ��ע�ᴰ��
     */
    public void openRegisterWindow()
    {
        if(mainWindow != null)
            return;
        mainWindow = (TWindow)openWindow("%ROOT%\\config\\root\\RootRegister.x",true);
        mainWindow.setVisible(true);
    }
    /**
     * �򿪲��Ҵ���
     */
    public void openFindWindow()
    {
        if(findWindow != null)
            return;
        findWindow = (TWindow)openWindow("%ROOT%\\config\\root\\RootFind.x",true);
        findWindow.setVisible(true);
    }
    /**
     * ����Ӻ�������Ի���
     * @param id String
     * @param name String
     */
    public void openAddFindSend(String id,String name)
    {
        TWindow w = (TWindow)openWindow("%ROOT%\\config\\root\\AddFriendSend.x",new String[]{id,name},true);
        w.setVisible(true);
    }
    /**
     * ��İ������Ӻ�������Ի���
     * @param id String
     * @param name String
     * @param message String
     */
    public void openAddFindLook(String id,String name,String message)
    {
        TWindow w = (TWindow)openWindow("%ROOT%\\config\\root\\AddFriendLook.x",new String[]{id,name,message},true);
        w.setVisible(true);
    }
    /**
     * ����Ϣ����
     * @param id String
     * @return TWindow
     */
    public TWindow openMessageWindow(String id)
    {
        TWindow window = getMessageWindow(id);
        if(window != null)
        {
            window.setVisible(true);
            return window;
        }
        window = (TWindow)openWindow("%ROOT%\\config\\root\\RootMessage.x",new String[]{id},true);
        window.setVisible(true);
        setMessageWindow(id,window);
        return window;
    }
    /**
     * ���ͼ��
     */
    public void onClickedIcon()
    {
        if(isClient())
        {
            openMainWindow();
            return;
        }
        if(!CheckThread.isRegister())
        {
            openRegisterWindow();
            return;
        }
    }
    /**
     * �õ���Ϣ����
     * @param id String
     * @return TWindow
     */
    public TWindow getMessageWindow(String id)
    {
        return (TWindow)messageWindows.get(id);
    }
    /**
     * ������Ϣ����
     * @param id String
     * @param window TWindow
     */
    public void setMessageWindow(String id,TWindow window)
    {
        if(window == null)
        {
            messageWindows.remove(id);
            return;
        }
        messageWindows.put(id,window);
    }
    /**
     * �õ����ƶ���
     * @return TConfigParm
     */
    public TConfigParm getConfigParm()
    {
        return (TConfigParm)sysFrame.callFunction("getConfigParm");
    }
    /**
     * �򿪴���
     * @param config String �����ļ���
     * @return TComponent
     */
    public TComponent openWindow(String config)
    {
        return openWindow(config,null);
    }
    /**
     * �򿪴���
     * @param config String
     * @param isTop boolean true �������� false ��ͨ����
     * @return TComponent
     */
    public TComponent openWindow(String config, boolean isTop)
    {
        return openWindow(config,null,isTop);
    }
    /**
     * �򿪴���
     * @param config String �����ļ���
     * @param parameter Object ����
     * @return TComponent
     */
    public TComponent openWindow(String config, Object parameter)
    {
        return openWindow(config,parameter,false);
    }
    /**
     * �򿪴���
     * @param config String
     * @param parameter Object
     * @param isTop boolean true �������� false ��ͨ����
     * @return TComponent
     */
    public TComponent openWindow(String config, Object parameter,boolean isTop)
    {
        if(isTop)
        {
            return TWindow.openWindow(getConfigParm().newConfig(config),sysFrame,parameter);
        }
        return TFrame.openWindow(getConfigParm().newConfig(config),parameter);
    }
    /**
     * ����Ϣ����
     * @param config String �����ļ���
     * @return Object ����ֵ
     */
    public Object openDialog(String config)
    {
        return openDialog(config,null);
    }
    /**
     * ����Ϣ����
     * @param config String �����ļ���
     * @param parameter Object ����
     * @return Object ����ֵ
     */
    public Object openDialog(String config, Object parameter)
    {
        return openDialog(config,parameter,true);
    }
    /**
     * ����Ϣ����
     * @param config String �����ļ���
     * @param parameter Object ����
     * @param flg boolean true ��̬���� false ��̬����
     * @return Object
     */
    public Object openDialog(String config, Object parameter,boolean flg)
    {
        return TDialog.openWindow(getConfigParm().newConfig(config,flg),parameter);
    }
    /**
     * �����Ի�����ʾ��Ϣ
     * @param message Object
     */
    public void messageBox(Object message){
        JOptionPane.showMessageDialog((Component)sysFrame,TCM_Transform.getString(message));
    }
    /**
     * ������Ϣ
     * @param id String
     * @param s String
     * @return boolean
     */
    public boolean sendMessage(String id, String s)
    {
        if(!isClient())
            return false;
        return getClient().sendMessage(id,s);
    }
}
