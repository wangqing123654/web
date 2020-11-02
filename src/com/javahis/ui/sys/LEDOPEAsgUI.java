package com.javahis.ui.sys;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.sql.Timestamp;
import javax.swing.JWindow;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.javahis.ui.ope.OPERoomAsgControl;

/**
 * 
 * <p> Title: ����SOKET���� </p>
 * 
 * <p> Description: �����ų������UI </p>
 * 
 * <p> Copyright: Copyright (c) 2015 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 2015.01.13
 * @version 4.0
 */
public class LEDOPEAsgUI extends JWindow {
	private int otherX;
    private int otherY;
    private int textX;
    /**
     * ��������
     */
    private int mainWidth;
    /**
     * ��Ϣ�б�
     */
    private TParm action = new TParm();
    /**
     * ��Ϣ����ַ���
     */
    private StringBuffer magess = new StringBuffer();
    /**
     * Socket���ͻ�ʿվ����
     */
    private SocketLink client;
    /**
     * ͨ���õ��û���
     */
    private String userID;
    /**
     * ͨ���õ�����
     */
    private String password;
    /**
     * �����ļ���URL
     */
    private URL url;
    /**
     * ������������
     */
    private AudioClip audio;
    /**
     * ��ɫ�����߳�
     */
    private Thread colorThread;
    /**
     * ����״̬
     */
    private boolean colorRowState;
    /**
     * ���ֹ����߳�
     */
    private Thread textThread;
    /**
     * ������
     */
    OPERoomAsgControl control;// �����ų̿�����

    /**
     * ������
     */
    public LEDOPEAsgUI() {
        jbInit();
    }

    /**
     * ������
     * @param owner
     * @param control
     * @param parm
     */
    public LEDOPEAsgUI(Frame owner, Object control, Object parm) {
        super(owner);
        initTemp(owner, control, parm);
        jbInit();
    }

    /**
     * ���ش������
     * 
     * @param owner
     *            Frame
     * @param parm
     *            Object
     */
    private void initTemp(Frame owner, Object control, Object parm) {
        this.mainWidth = owner.getWidth();
        if (control instanceof OPERoomAsgControl) {
            this.control = (OPERoomAsgControl) control;
        }
        if (parm != null) {
            TParm ledParm = (TParm) parm;
            this.setRegisterInfo(ledParm.getValue("userID"), ledParm.getValue("password"));
//            TParm ledListenerParm = new TParm();
//            ledListenerParm.addListener("onRegisterLed", this, "setRegisterInfo");
//            ledParm.runListener("getLEDListener", ledListenerParm);
        }
    }

    /**
     * ��������Ʒ����id��password
     * @param userID
     * @param password
     */
    public void setRegisterInfo(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    /**
     * ��������
     */
    private void jbInit() {
        // ��������
        initServer();
        mouseListenter();
    }

    /**
     * ��ʼ�������������
     */
    private void initServer() {
        client = SocketLink.running("", this.userID, this.password);
        if (client.isClose()) {
            System.out.println("��ʼ�����������������");
            return;
        }
        client.addEventListener("Message", this, "onMessage");
        client.listener();
    }
    
    /**
     * �򿪴���
     */
    public void openWindow() {
        this.setVisible(true);
    }

    /**
     * ������
     */
    private void mouseListenter() {
        this.addMouseListener(new MouseAdapter() {

            // ����
            public void mousePressed(MouseEvent e) {
                otherX = e.getX();
                otherY = e.getY();
            }

            public void mouseClicked(MouseEvent e) {
                // ˫��
                if (e.getClickCount() == 2) {
                    doubleClickMsg(e);
                }
            }
        });
        // ����¼�
        this.addMouseMotionListener(new MouseMotionAdapter() {

            // ����϶�
            public void mouseDragged(MouseEvent e) {
                mouseDraggedEvent(e);
            }
        });
        this.setLocation(this.mainWidth - 500, 30);
        this.setBackground(new Color(51, 51, 153));
        this.setFont(new Font("����_GB2312", Font.BOLD, 14));
        this.setSize(400, 40);
    }

    /**
     * ˫����Ϣ���
     * 
     * @param e
     *            MouseEvent
     */
    private void doubleClickMsg(MouseEvent e) {
        // ���˫��
        if (e.getButton() == 1) {
            // ˫���¼�
            doubleClick();
        }
    }

    /**
     * ˫���¼�
     */
    public void doubleClick() {
        // ˫����������е�����
        this.control.onDoubleClickLEDUI(action);
    }

    /**
     * ����϶�
     * 
     * @param e
     *            MouseEvent
     */
    private void mouseDraggedEvent(MouseEvent e) {
        int x = e.getX() - otherX + this.getX();
        int y = e.getY() - otherY + this.getY();
        this.setLocation(x, y);
    }

    /**
     * ������Ϣ
     * 
     * @param action
     *            String
     */
    public void onMessage(Timestamp time, String id, String message) {
        String messages[] = message.split("\\|");
        for (String temp : messages) {
            String msg[] = temp.split(":");
            if (msg.length == 1) action.addData(msg[0], "");
            else action.addData(msg[0], msg[1]);
        }
        getMessageWords();
        // ������ɫ�߳�
        changeTableRowColor();
        // ���ù�������
        startMoveThread();
        // ��������
        startSound();
    }

    /**
     * ���ɹ�������
     */
    private void getMessageWords() {
        magess = new StringBuffer();
        for (int i = 0; i < action.getCount("PAT_NAME"); i++) {
            magess.append("�����µ����뵥");
            magess.append(" " + action.getValue("MR_NO", i));
            magess.append(" " + action.getValue("PAT_NAME", i));
            magess.append("     ");
        }
    }

    /**
     * ������Ϣ����
     */
    public void setMessage(TParm parm) {
        this.action = parm;
    }

    /**
     * ������Ϣ����
     */
    public TParm getMessage() {
        return action;
    }

    /**
     * �ı�TABLE��ɫ
     * 
     * @param row
     *            int
     */
    public void changeTableRowColor() {
        startColorThread();
    }

    /**
     * ������ɫ�߳�
     */
    private void startColorThread() {
        if (colorThread != null) return;
        colorThread = new Thread() {

            public void run() {
                while (colorThread != null) {
                    try {
                        Thread.sleep(500);
                        workColor();
                    }
                    catch (InterruptedException ex) {}
                }
                setBackground(new Color(0, 51, 102));
                repaint();
            }
        };
        colorThread.start();
    }

    /**
     * ��ʼ����
     */
    public void workColor() {
        colorRowState = !colorRowState;
        this.setBackground(colorRowState ? new Color(255, 0, 0) : new Color(0, 0, 0));
        this.repaint();
    }

    /**
     * �������������߳�
     */
    private void startMoveThread() {
        if (textThread != null) return;
        textThread = new Thread() {

            public void run() {
                while (textThread != null) {
                    try {
                        Thread.sleep(500);
                        workText();
                    }
                    catch (InterruptedException ex) {}
                }
                magess = new StringBuffer();
                repaint();
            }
        };
        textThread.start();
    }

    /**
     * ��ʼ����
     */
    public void workText() {
        if (textX >= this.getWidth()) textX = 0;
        textX += 10;
        this.repaint();
    }

    /**
     * ��������
     */
    private void startSound() {
        // ��������
        try {
            url = new URL(TIOM_AppServer.SOCKET.getServletPath("wav/ring.wav"));
            audio = Applet.newAudioClip(url);
            // ������(ѭ��)
            audio.loop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ɾ����Ϣ
     * 
     * @param parm
     *            TParm
     */
    public void removeMessage(TParm parm) {
        int rowCount = action.getCount("OPBOOK_SEQ");
        for (int i = 0; i < parm.getCount("OPBOOK_SEQ"); i++) {
            String opBookSeq = parm.getValue("OPBOOK_SEQ", i);
            for (int j = rowCount - 1; j >= 0; j--) {
                if (opBookSeq.equals(action.getValue("OPBOOK_SEQ", j))) {
                    action.removeRow(j);
                }
            }
        }
        if (action.getCount("OPBOOK_SEQ") <= 0) {
            // ֹͣ������ɫ
            stopColorThread();
            // ֹͣ������Ϣ
            stopMoveThread();
            // ֹͣ����
            stopSound();
        } else {
            getMessageWords();
        }
        textX = 0;// ���ò�����ɽ���Ϣ���¹���
    }

    /**
     * �ر�
     */
    public void close() {
        this.stopColorThread();
        this.stopMoveThread();
        this.stopSound();
        this.closeSocket();
        this.setVisible(false);
    }

    /**
     * �ر�Socket
     */
    public void closeSocket() {
        if (!client.isClose()) client.close();
    }

    /**
     * ֹͣ��ɫ�߳�
     */
    private void stopColorThread() {
        colorThread = null;
    }

    /**
     * ֹͣ���������߳�
     */
    private void stopMoveThread() {
        textThread = null;
    }

    /**
     * ֹͣ����
     */
    private void stopSound() {
        if (audio != null) {
            audio.stop();
        }
    }

    public void paint(Graphics g) {
        g.setColor(this.getBackground());
        g.fill3DRect(0, 0, 400, 40, true);
        g.setColor(new Color(255, 153, 204));
        g.drawString(magess.toString(), textX, 26);
    }

}
