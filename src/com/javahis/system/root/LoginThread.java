package com.javahis.system.root;

import com.dongyang.root.client.SocketLink;

/**
 *
 * <p>Title: ��¼�߳�</p>
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
public class LoginThread implements Runnable{
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
    public LoginThread(RootClientListener listener)
    {
        this.listener = listener;
    }
    /**
     * ����
     * @param listener RootClientListener
     */
    public static void start(RootClientListener listener)
    {
        thread = new Thread(new LoginThread(listener));
        thread.start();
    }
    /**
     * ����
     */
    public void sleep()
    {
        try{
            thread.sleep(5000);
        }catch(Exception e)
        {

        }
    }
    /**
     * ����
     */
    public void run()
    {
        while(!listener.isClose())
        {
            listener.setSysStatus("����ͨѶ������...");
            listener.setClient(SocketLink.running(listener.getUserID(),
                                                  listener.getPassword()));
            if (!listener.getClient().isClose()) {
                listener.listener();
                return;
            }
            listener.setSysStatus(listener.getClient().getErrText());
            sleep();
        }
    }
}
