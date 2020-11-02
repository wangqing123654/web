package jdo.device;

import java.io.*;
import java.net.*;

import com.dongyang.config.*;
import com.javahis.util.*;
import com.javahis.xml.*;

/**
 * <p>Title: </p>
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
public class LAB_Service {
    /**
     * ����������
     */
    private String host;
    /**
     * �������
     */
    private String hostH;
    /**
     * ����IP
     */
    private String hostE;
    /**
     * סԺ
     */
    private String hostI;
    /**
     * �˿�
     */
    private int port;
    /**
     * ��ȷ�ķ���
     */
    private String success;
    /**
     * ��ʼ��
     */
    public boolean init() {
        if ("N".equals(TConfig.getSystemValue("ServerLabIsReady")))
            return false;
        setHost(TConfig.getSystemValue("CallLabHostO"));
        setHostH(TConfig.getSystemValue("CallLabHostH"));
        setHostE(TConfig.getSystemValue("CallLabHostE"));
        setHostI(TConfig.getSystemValue("CallLabHostI"));
        int port = 80;
        try {
            port = Integer.parseInt(TConfig.getSystemValue("CallLabPort"));
        }
        catch (Exception e) {
            return false;
        }
        setPort(port);
        return true;
    }

    /**
     * ����������
     * @param host String
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * �õ�������
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
     * ���ö˿�
     * @param port String
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * �õ��˿�
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * ���óɹ�
     * @param success String
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    public void setHostE(String hostE) {
        this.hostE = hostE;
    }

    public void setHostH(String hostH) {
        this.hostH = hostH;
    }

    public void setHostI(String hostI) {
        this.hostI = hostI;
    }

    /**
     * �õ��ɹ�
     * @return String
     */
    public String getSuccess() {
        return success;
    }

    public String getHostE() {
        return hostE;
    }

    public String getHostH() {
        return hostH;
    }

    public String getHostI() {
        return hostI;
    }

    /**
     *
     * @param socket Socket
     * @return String
     */
    public static String getRequestData(Socket socket) throws IOException {
        byte[] l = new byte[8129];
        byte p = (byte) socket.getInputStream().read();
        int k = 0;
        while (p != '\n') {
            l[k] = p;
            p = (byte) socket.getInputStream().read();
            k++;
        }
        String str = new String(l, 0, k);
        return str;
    }

    public static void main(String args[]) {
        JavaHisDebug.initClient();
        LAB_Service call = new LAB_Service();
        call.init();
        System.out.println("" + call.getHost());
        System.out.println("" + call.getPort());
        Job job = new Job(2);
        String s = job.toString();
        new DynamecMassage(call.getHost(), call.getPort(), s.getBytes());
    }

    public static class DynamecMassage
        implements Runnable {
        String host;
        int port;
        byte[] xmlData;
        public DynamecMassage(String host, int port, byte[] xmlData) {
            this.host = host;
            this.port = port;
            this.xmlData = xmlData;
            new Thread(this).start();
        }

        String result;
        public void run() {
            try {
                System.out.println("IP��ַ:"+host);
                System.out.println("�˿�:"+port);
                Socket socket = new Socket(host, port);
                OutputStream out = socket.getOutputStream();
                String s = new String(xmlData);
                System.out.println("������Ϣ:"+s);
                out.write(xmlData);
                result = getRequestData(socket);
                System.out.println("" + result);
                out.close();
                socket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
