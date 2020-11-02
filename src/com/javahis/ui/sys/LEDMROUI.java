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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JWindow;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.javahis.ui.mro.MROReadyOutControl;

/**
 * 监听SOKET窗口:病案借阅监听
 * <p>
 * Title: 监听SOKET窗口
 * </p>
 * <p>
 * Description: 门诊挂号时，物联网实时监听
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author wangbin 2014.09.02
 * @version 4.0
 */
public class LEDMROUI extends JWindow {
	private int otherX;
	private int otherY;
	private int textX;
	/**
	 * 消息列表
	 */
	TParm action = new TParm();
	/**
	 * 消息类表字符串
	 */
	StringBuffer magess = new StringBuffer();
	/**
	 * Socket传送护士站工具
	 */
	private SocketLink client;
	/**
	 * URL
	 */
	private URL url;
	/**
	 * 调用声音对象
	 */
	private AudioClip audio;
	/**
	 * 颜色闪动
	 */
	Thread colorThread;
	/**
	 * 闪动状态
	 */
	boolean colorRowState;
	/**
	 * 文字滚动
	 */
	Thread textThread;
	/**
	 * 控制类
	 */
	MROReadyOutControl control;
	/**
	 * 主窗体宽
	 */
	private int mainWidth;
	/**
	 * 病案借阅
	 */
	private String mroCode;

	/**
	 * 构造器
	 */
	public LEDMROUI() {
		jbInit();
	}
	
	/**
	 * 构造器
	 * 
	 * @param owner
	 *            Frame
	 */
	public LEDMROUI(Frame owner, Object control, Object parm) {
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
		if (control instanceof MROReadyOutControl) {
			this.control = (MROReadyOutControl) control;
		} 
		if (parm != null) {
			TParm ledParm = (TParm) parm;
			this.mroCode =  ledParm.getValue("MRO");
			TParm ledListenerParm = new TParm();
			ledListenerParm.addListener("onListenerLed", this, "onListenerLed");
			ledParm.runListener("onSelStation", ledListenerParm);
		}
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
	 * LED监听方法
	 * 
	 * @param stationCode
	 *            String
	 */
	public void onListenerLed(String mroCode) {
		this.mroCode = mroCode;
	}

	/**
	 * 监听方法
	 */
	private void jbInit() {
		// 启动监听
		initNbwServer();
		mouseListenter();
	}

	public void paint(Graphics g) {
		g.setColor(this.getBackground());
		g.fill3DRect(0, 0, 400, 40, true);
		g.setColor(new Color(255, 153, 204));
		g.drawString(magess.toString(), textX, 26);
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
		this.control.openInwCheckWindow(action.getValue("MRO_REGNO"));
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
		if (!client.isClose())
			client.close();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止音乐
	 */
	private void stopSound() {
		if (audio != null) {
			audio.stop();
		}
	}

	/**
	 * 打开窗口
	 */
	public void openWindow() {
		this.setVisible(true);

	}
	
	/**
	 * 初始监听服务服务器
	 */
	private void initNbwServer() {
		client = SocketLink.running("", this.mroCode, this.mroCode);
		if (client.isClose()) {
			System.out.println("初始监听服务服务器错误");
			return;
		}
		client.addEventListener("Message", this, "onMessage");
		client.listener();
	}

	/**
	 * 监听消息
	 * 
	 * @param action
	 *            String
	 */
	public void onMessage(Timestamp time, String id, String message) {
		String messages[] = message.split("\\|");
		aa: for (String temp : messages) {
			String mes[] = temp.split(":");
			if ("MRO_REGNO".equals(mes[0])) {
				int rowCount = action.getCount("MRO_REGNO");
				for (int i = 0; i < rowCount; i++) {
					if (action.getValue("MRO_REGNO", i).equals(mes[1]))
						break aa;
				}
			}
			action.addData(mes[0], mes[1]);
		}
		getMessage();
		// 启动颜色线程
		changeTableRowColor();
		// 启用滚动文字
		startMoveThread();
		// 启动音乐
		startSound();
	}
	
	/**
	 * 消息滚动显示
	 */
	private void getMessage(){
		magess = new StringBuffer();
		int rowAction = action.getCount("MRO_REGNO");
		for (int i = 0; i < rowAction; i++) {
			// modify by wangbin 2015/2/4 需求分析 #860 跑马灯提示病案号+病患姓名
			magess.append(action.getValue("MR_NO", i));
			magess.append(":"+action.getValue("PAT_NAME", i));
			magess.append("     ");
		}
	}
	
	/**
	 * 删除消息队列
	 * 
	 * @param parm
	 *            TParm
	 */
	public void removeMessage(TParm parm) {
		int rowCount = action.getCount("MRO_REGNO");
		String mroRegNo = parm.getValue("MRO_REGNO");
		for (int i = rowCount - 1; i >= 0; i--) {
			if (mroRegNo.equals(action.getValue("MRO_REGNO", i))) {
				action.removeRow(i);
			}
		}
		if (action.getCount("MRO_REGNO") <= 0) {
			// 停止闪动颜色
			stopColorThread();
			// 停止滚动消息
			stopMoveThread();
			// 停止声音
			stopSound();
		}else{
			getMessage();
		}
		textX = 0;//设置操作完成将消息重新滚动
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
	 * 启动颜色线程
	 */
	private void startColorThread() {
		if (colorThread != null)
			return;
		colorThread = new Thread() {
			public void run() {
				while (colorThread != null) {
					try {
						Thread.sleep(500);
						workColor();
					} catch (InterruptedException ex) {
					}
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
		this.setBackground(colorRowState ? new Color(255, 0, 0) : new Color(0,
				0, 0));
		this.repaint();
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
	 * 停止颜色线程
	 */
	private void stopColorThread() {
		colorThread = null;
	}

	/**
	 * 启动滚动文字线程
	 */
	private void startMoveThread() {
		if (textThread != null)
			return;
		textThread = new Thread() {
			public void run() {
				while (textThread != null) {
					try {
						Thread.sleep(500);
						workText();
					} catch (InterruptedException ex) {
					}
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
		if (textX >= this.getWidth())
			textX = 0;
		textX += 10;
		this.repaint();
	}

	/**
	 * 停止滚动文字线程
	 */

	private void stopMoveThread() {
		textThread = null;
	}
	
	/**
	 * 用于获取当前面板上所有数据的方法
	 */
	public List<String> getAllLEDData() {
		String[] mroRegNoarray = action.getValue("MRO_REGNO").replace("[", "").replace("]", "").split(",");
		List<String> mroRegNoList = new ArrayList<String>();
		for (int i = 0; i < mroRegNoarray.length; i++) {
			mroRegNoList.add(mroRegNoarray[i].trim());
		}
		
		return mroRegNoList;
	}
}
