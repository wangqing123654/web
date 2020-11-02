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
 * <p> Title: 监听SOKET窗口 </p>
 * 
 * <p> Description: 手术排程跑马灯UI </p>
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
     * 主窗体宽度
     */
    private int mainWidth;
    /**
     * 消息列表
     */
    private TParm action = new TParm();
    /**
     * 消息类表字符串
     */
    private StringBuffer magess = new StringBuffer();
    /**
     * Socket传送护士站工具
     */
    private SocketLink client;
    /**
     * 通信用的用户名
     */
    private String userID;
    /**
     * 通信用的密码
     */
    private String password;
    /**
     * 声音文件的URL
     */
    private URL url;
    /**
     * 调用声音对象
     */
    private AudioClip audio;
    /**
     * 颜色闪动线程
     */
    private Thread colorThread;
    /**
     * 闪动状态
     */
    private boolean colorRowState;
    /**
     * 文字滚动线程
     */
    private Thread textThread;
    /**
     * 控制类
     */
    OPERoomAsgControl control;// 手术排程控制类

    /**
     * 构造器
     */
    public LEDOPEAsgUI() {
        jbInit();
    }

    /**
     * 构造器
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
     * 加载窗体监听
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
     * 设置跑马灯服务的id和password
     * @param userID
     * @param password
     */
    public void setRegisterInfo(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    /**
     * 监听方法
     */
    private void jbInit() {
        // 启动监听
        initServer();
        mouseListenter();
    }

    /**
     * 初始监听服务服务器
     */
    private void initServer() {
        client = SocketLink.running("", this.userID, this.password);
        if (client.isClose()) {
            System.out.println("初始监听服务服务器错误");
            return;
        }
        client.addEventListener("Message", this, "onMessage");
        client.listener();
    }
    
    /**
     * 打开窗口
     */
    public void openWindow() {
        this.setVisible(true);
    }

    /**
     * 鼠标操作
     */
    private void mouseListenter() {
        this.addMouseListener(new MouseAdapter() {

            // 按下
            public void mousePressed(MouseEvent e) {
                otherX = e.getX();
                otherY = e.getY();
            }

            public void mouseClicked(MouseEvent e) {
                // 双击
                if (e.getClickCount() == 2) {
                    doubleClickMsg(e);
                }
            }
        });
        // 鼠标事件
        this.addMouseMotionListener(new MouseMotionAdapter() {

            // 鼠标拖动
            public void mouseDragged(MouseEvent e) {
                mouseDraggedEvent(e);
            }
        });
        this.setLocation(this.mainWidth - 500, 30);
        this.setBackground(new Color(51, 51, 153));
        this.setFont(new Font("楷体_GB2312", Font.BOLD, 14));
        this.setSize(400, 40);
    }

    /**
     * 双击消息面板
     * 
     * @param e
     *            MouseEvent
     */
    private void doubleClickMsg(MouseEvent e) {
        // 左键双击
        if (e.getButton() == 1) {
            // 双击事件
            doubleClick();
        }
    }

    /**
     * 双击事件
     */
    public void doubleClick() {
        // 双击面板获得所有的数据
        this.control.onDoubleClickLEDUI(action);
    }

    /**
     * 鼠标拖动
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
     * 监听消息
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
        // 启动颜色线程
        changeTableRowColor();
        // 启用滚动文字
        startMoveThread();
        // 启动音乐
        startSound();
    }

    /**
     * 生成滚动文字
     */
    private void getMessageWords() {
        magess = new StringBuffer();
        for (int i = 0; i < action.getCount("PAT_NAME"); i++) {
            magess.append("您有新的申请单");
            magess.append(" " + action.getValue("MR_NO", i));
            magess.append(" " + action.getValue("PAT_NAME", i));
            magess.append("     ");
        }
    }

    /**
     * 设置消息队列
     */
    public void setMessage(TParm parm) {
        this.action = parm;
    }

    /**
     * 返回消息队列
     */
    public TParm getMessage() {
        return action;
    }

    /**
     * 改变TABLE颜色
     * 
     * @param row
     *            int
     */
    public void changeTableRowColor() {
        startColorThread();
    }

    /**
     * 启动颜色线程
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
     * 开始工作
     */
    public void workColor() {
        colorRowState = !colorRowState;
        this.setBackground(colorRowState ? new Color(255, 0, 0) : new Color(0, 0, 0));
        this.repaint();
    }

    /**
     * 启动滚动文字线程
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
     * 开始工作
     */
    public void workText() {
        if (textX >= this.getWidth()) textX = 0;
        textX += 10;
        this.repaint();
    }

    /**
     * 启动声音
     */
    private void startSound() {
        // 调用音乐
        try {
            url = new URL(TIOM_AppServer.SOCKET.getServletPath("wav/ring.wav"));
            audio = Applet.newAudioClip(url);
            // 放音乐(循环)
            audio.loop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除消息
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
            // 停止闪动颜色
            stopColorThread();
            // 停止滚动消息
            stopMoveThread();
            // 停止声音
            stopSound();
        } else {
            getMessageWords();
        }
        textX = 0;// 设置操作完成将消息重新滚动
    }

    /**
     * 关闭
     */
    public void close() {
        this.stopColorThread();
        this.stopMoveThread();
        this.stopSound();
        this.closeSocket();
        this.setVisible(false);
    }

    /**
     * 关闭Socket
     */
    public void closeSocket() {
        if (!client.isClose()) client.close();
    }

    /**
     * 停止颜色线程
     */
    private void stopColorThread() {
        colorThread = null;
    }

    /**
     * 停止滚动文字线程
     */
    private void stopMoveThread() {
        textThread = null;
    }

    /**
     * 停止音乐
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
