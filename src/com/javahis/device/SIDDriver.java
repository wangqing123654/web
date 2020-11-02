package com.javahis.device;

import java.io.File;
import com.dongyang.util.FileTool;

/**
 *
 * <p>Title: �������֤�ӿ�</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SIDDriver {
    static {
      System.loadLibrary("sidDriver"); //����dll
    }
    public native static int init();
    public native static int initComm(int port);
    public native static int closeComm();
    public native static int authenticate();
    public native static int readContent(int active);
    public native static String getDir();
    public static String read()
    {
        if(init() != 1)
            return "ERR:û���ҵ�DLL�ļ�!";
        if(initComm(1001) != 1)
        {
            closeComm();
            return "ERR:�豸û��׼����!";
        }
        if(authenticate() != 1)
        {
            closeComm();
            return "ERR:�޿�!";
        }
        if(readContent(1) != 1)
        {
            closeComm();
            return "ERR:��Ƭ��Ч!";
        }

        return "OK";
    }
    public static void main(String args[])
    {
        System.out.println(read());
        System.out.println(getDir());
    }
}
