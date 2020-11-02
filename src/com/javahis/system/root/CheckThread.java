package com.javahis.system.root;

import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: �����߳�</p>
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
public class CheckThread implements Runnable{
    /**
     * ����������
     */
    private RootClientListener listener;
    /**
     * �߳�
     */
    private static Thread thread;
    /**
     * ������
     * @param listener RootClientListener
     */
    public CheckThread(RootClientListener listener)
    {
        this.listener = listener;
    }
    /**
     * ����
     * @param listener RootClientListener
     */
    public static void start(RootClientListener listener)
    {
        thread = new Thread(new CheckThread(listener));
        thread.start();
    }
    /**
     * ����
     */
    public void sleep()
    {
        try{
            thread.sleep(300);
        }catch(Exception e)
        {

        }
    }
    /**
     * �õ����ݹ���
     * @return TJDODBTool
     */
    public static TJDODBTool getDBTool()
    {
        return TJDODBTool.getInstance();
    }
    /**
     * �Ƿ�ע��
     * @return boolean
     */
    public static boolean isRegister()
    {
        TParm parm = new TParm(getDBTool().select("SELECT COUNT(*)AS COUNT FROM SKT_USER WHERE ID='" + Operator.getID() + "'"));
        return parm.getInt("COUNT",0) > 0;
    }
    /**
     * �õ��ǳ�
     * @return String
     */
    public static String getName()
    {
        return getName(Operator.getID());
    }
    /**
     * �õ��ǳ�
     * @param id String
     * @return String
     */
    public static String getName(String id)
    {
        TParm parm = new TParm(getDBTool().select("SELECT NAME FROM SKT_USER WHERE ID='" + id + "'"));
        return parm.getValue("NAME",0);
    }
    /**
     * �õ�����
     * @return String
     */
    public static String getPassword()
    {
        TParm parm = new TParm(getDBTool().select("SELECT PASSWORD FROM SKT_USER WHERE ID='" + Operator.getID() + "'"));
        return parm.getValue("PASSWORD",0);
    }
    /**
     * �õ��û�����
     * @param userID String
     * @return String
     */
    public static String getUserPassword(String userID)
    {
        TParm parm = new TParm(getDBTool().select("SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID='" + userID + "'"));
        return StringTool.decrypt(parm.getValue("USER_PASSWORD",0));
    }
    /**
     * ��������
     * @param password String
     * @return boolean
     */
    public static boolean setPassword(String password)
    {
        TParm parm = new TParm(getDBTool().update("UPDATE SKT_USER SET PASSWORD='" + password + "' WHERE ID='" + Operator.getID() + "'"));
        return parm.getErrCode() == 0;
    }
    /**
     * �����Զ���¼
     * @param autoLogin boolean
     * @return boolean
     */
    public static boolean setAutoLogin(boolean autoLogin)
    {
        TParm parm = new TParm(getDBTool().update("UPDATE SKT_USER SET AUTO_LOGIN='" + (autoLogin?"Y":"N") + "' WHERE ID='" + Operator.getID() + "'"));
        return parm.getErrCode() == 0;
    }
    /**
     * �Ƿ��Զ���¼
     * @return boolean
     */
    public static boolean isAutoLogin()
    {
        TParm parm = new TParm(getDBTool().select("SELECT AUTO_LOGIN FROM SKT_USER WHERE ID='" + Operator.getID() + "'"));
        return parm.getBoolean("AUTO_LOGIN",0);
    }
    /**
     * ע��
     * @param id String
     * @param name String
     * @param password String
     * @param onlyOne boolean
     * @param autoLogin boolean
     * @param addType int 0 ���� 1 �ܾ� 2 ��Ҫȷ��
     * @return boolean
     */
    public static boolean register(String id,String name,String password,boolean onlyOne,boolean autoLogin,int addType)
    {
        TParm parm = new TParm(getDBTool().update("INSERT INTO SKT_USER (ID,NAME,PASSWORD,ONLY_ONE,AUTO_LOGIN,ADD_TYPE)" +
                                                  " VALUES('" + id + "','" + name + "','" + password + "','" + (onlyOne?"Y":"N") + "','" + (autoLogin?"Y":"N") + "','" + addType + "')"));
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return false;
        }
        createGroup(id,"01","�ҵĺ���",1);
        SocketLink.running("#System.Reset#","");
        return true;
    }
    /**
     * �õ���Ӻ�������
     * @return int
     */
    public static int getAddType()
    {
        return getAddType(Operator.getID());
    }
    /**
     * �õ���Ӻ�������
     * @param id String
     * @return int
     */
    public static int getAddType(String id)
    {
        TParm parm = new TParm(getDBTool().select("SELECT ADD_TYPE FROM SKT_USER WHERE ID='" + id + "'"));
        return parm.getInt("ADD_TYPE",0);
    }
    /**
     * ������
     * @param userID String
     * @param groupId String
     * @param groupName String
     * @param seq int
     * @return boolean
     */
    public static boolean createGroup(String userID,String groupId,String groupName,int seq)
    {
        TParm parm = new TParm(getDBTool().update("INSERT INTO SKT_GROUP_LIST (USER_ID,GROUP_ID,GROUP_NAME,SEQ)" +
                                                  " VALUES('" + userID + "','" + groupId + "','" + groupName + "','" + seq + "')"));
        return parm.getErrCode() == 0;
    }
    /**
     * �õ���
     * @return TParm
     */
    public static TParm getGroup()
    {
        return new TParm(getDBTool().select("SELECT GROUP_ID,GROUP_NAME FROM SKT_GROUP_LIST WHERE USER_ID='" + Operator.getID() + "' ORDER BY SEQ"));
    }
    /**
     * �������Ա
     * @param linkID String
     * @param name String
     * @param groupID String
     * @return boolean
     */
    public static boolean createGroupUser(String linkID,String name,String groupID)
    {
        return createGroupUser(Operator.getID(),linkID,name,groupID,getNewGroupUserSeq(groupID));
    }
    /**
     * �������Ա
     * @param userID String
     * @param linkID String
     * @param name String
     * @param groupID String
     * @param seq int
     * @return boolean
     */
    public static boolean createGroupUser(String userID,String linkID,String name,String groupID,int seq)
    {
        TParm parm = new TParm(getDBTool().update("INSERT INTO SKT_GROUP (ID,LINK_ID,NAME,GROUP_ID,SEQ)" +
                                                  " VALUES('" + userID + "','" + linkID + "','" + name + "','" + groupID + "','" + seq + "')"));
        return parm.getErrCode() == 0;
    }
    /**
     * �õ�����
     * @param groupID String
     * @return int
     */
    public static int getNewGroupUserSeq(String groupID)
    {
        TParm parm = new TParm(getDBTool().select("SELECT MAX(SEQ) AS SEQ FROM SKT_GROUP WHERE ID='" + Operator.getID() + "' AND GROUP_ID='" + groupID + "'"));
        int x = parm.getInt("SEQ",0);
        if(x < 0)
            x = 0;
        return x + 1;
    }
    /**
     * �õ����Ա
     * @param groupID String
     * @return TParm
     */
    public static TParm getGroupUser(String groupID)
    {
        return new TParm(getDBTool().select("SELECT LINK_ID,NAME FROM SKT_GROUP "+
                                            "WHERE ID='" + Operator.getID() + "' "+
                                            "AND GROUP_ID='" + groupID + "'"+
                                            "ORDER BY SEQ"));
    }
    /**
     * �Ƿ��Ǻ���
     * @param id String
     * @return boolean
     */
    public static boolean isFriend(String id)
    {
        TParm parm = new TParm(getDBTool().select("SELECT COUNT(*) AS COUNT FROM SKT_GROUP "+
                                            "WHERE ID='" + Operator.getID() + "' "+
                                            "AND LINK_ID='" + id + "'"));
        return parm.getInt("COUNT",0) > 0;
    }
    /**
     * ����
     * @param id String
     * @param name String
     * @return TParm
     */
    public static TParm findUser(String id,String name)
    {
        String sql = "SELECT ID,NAME FROM SKT_USER ";
        if(id != null && id.length() > 0)
            sql += "WHERE ID='" + id + "'";
        else if(name != null && name.length() > 0)
            sql += "WHERE NAME like '" + name + "%'";
        return new TParm(getDBTool().select(sql));
    }
    /**
     * ����
     */
    public void run()
    {
        sleep();
        //û��ע��
        if(!isRegister())
            return;
        //�����Զ���¼
        if(!isAutoLogin())
            return;
        //��ȡ����
        listener.setPassword(getPassword());
        //��¼
        listener.login();
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.initClient();
        System.out.println(CheckThread.getUserPassword("ehui"));
        //CheckThread.setPassword("123");
        //System.out.println(CheckThread.isRegister() + " " + CheckThread.getPassword());
        //CheckThread.register("123","abc","ppp",true,true);

    }
}
