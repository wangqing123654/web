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
import com.javahis.ui.pha.PhaMainControl;

/**
 * ����SOKET����:����ҩ������
 * <p>
 * Title: ����SOKET����
 * </p>
 * <p>
 * Description: ����ҩƷ�շѲ���ʱ��������ʵʱ����
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author not attributable
 * @version add by pangben 20130509
 */
public class LEDMEDUI extends JWindow {
	private int otherX;
	private int otherY;
	private int textX;
	/**
	 * ��Ϣ�б�
	 */
	TParm action = new TParm();
	/**
	 * ��Ϣ����ַ���
	 */
	StringBuffer magess = new StringBuffer();
	/**
	 * Socket���ͻ�ʿվ����
	 */
	private SocketLink client;
	/**
	 * URL
	 */
	private URL url;
	/**
	 * ������������
	 */
	private AudioClip audio;
	/**
	 * ��ɫ����
	 */
	Thread colorThread;
	/**
	 * ����״̬
	 */
	boolean colorRowState;
	/**
	 * ���ֹ���
	 */
	Thread textThread;
	/**
	 * ������
	 */
	PhaMainControl control;
	/**
	 * �������
	 */
	private int mainWidth;
	/**
	 * ҩƷ����
	 */
	private String phaCode ="";

	/**
	 * ������
	 * 
	 * @param owner
	 *            Frame
	 */
	public LEDMEDUI(Frame owner, Object control, Object parm) {
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
		// System.out.println("��������:" + this.mainWidth);
		if (control instanceof PhaMainControl) {
			this.control = (PhaMainControl) control;
		} 
		if (parm != null) {
			TParm ledParm = (TParm) parm;
			this.phaCode =  ledParm.getValue("PHA_CODE");
			TParm ledListenerParm = new TParm();
			ledListenerParm.addListener("onListenerLed", this, "onListenerLed");
			ledParm.runListener("onSelStation", ledListenerParm);
		}
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
		// this.setLocation(880,20);
		this.setBackground(new Color(51, 51, 153));
		this.setFont(new Font("����_GB2312", Font.BOLD, 14));
		this.setSize(400, 40);
		// System.out.println("7");

	}

	/**
	 * LED��������
	 * 
	 * @param stationCode
	 *            String
	 */
	public void onListenerLedMed(String phaCode) {
		this.phaCode = phaCode;
	}

	public LEDMEDUI() {
		jbInit();
	}

	private void jbInit() {
		// ��������
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
	 * ˫����Ϣ���
	 * 
	 * @param e
	 *            MouseEvent
	 */
	private void doubleClickMsg(MouseEvent e) {
		// ���˫��
		if (e.getButton() == 1) {
			// System.out.println("˫��");
			// ˫���¼�
			doubleClick();
		}
	}

	/**
	 * ˫���¼�
	 */
	public void doubleClick() {
		this.control.openInwCheckWindow(action.getValue("RX_NO",0));
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
		if (!client.isClose())
			client.close();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ֹͣ����
	 */
	private void stopSound() {
		if (audio != null) {
			audio.stop();
		}
	}

	/**
	 * �򿪴���
	 */
	public void openWindow() {
		this.setVisible(true);

	}
	/**
	 * ��ʼ�������������
	 */
	private void initNbwServer() {
		client = SocketLink.running("", this.phaCode, this.phaCode);
		// END==================$$//
		// System.out.println("2");
		if (client.isClose()) {
			// System.out.println("3");
			return;
		}
		client.addEventListener("Message", this, "onMessage");
		// System.out.println("5");
		client.listener();
		// System.out.println("6");
	}

	/**
	 * ������Ϣ
	 * 
	 * @param action
	 *            String
	 */
	public void onMessage(Timestamp time, String id, String message) {
		// System.out.println(time + " " + id + " " + message);
		String messages[] = message.split("\\|");
		aa: for (String temp : messages) {
			String mes[] = temp.split(":");
			if ("RX_NO".equals(mes[0])) {
				int rowCount = action.getCount("RX_NO");
				for (int i = 0; i < rowCount; i++) {
					if (action.getValue("RX_NO", i).equals(mes[1]))
						break aa;
				}
			}
			action.addData(mes[0], mes[1]);
		}
		// System.out.println("action"+action);
		getMessage();
		// System.out.println("��Ϣ:"+magess.toString());
		// ������ɫ�߳�
		changeTableRowColor();
		// ���ù�������
		startMoveThread();
		// ��������
		startSound();
	}
	/**
	 * ��Ϣ������ʾ
	 */
	private void getMessage(){
		magess = new StringBuffer();
		int rowAction = action.getCount("RX_NO");
		for (int i = 0; i < rowAction; i++) {
			magess.append(action.getValue("PAT_NAME", i));
			magess.append(":"+action.getValue("RX_NO", i));
			magess.append("     ");
		}
	}
	/**
	 * ɾ����Ϣ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void removeMessage(TParm parm) {
		int rowCount = action.getCount("RX_NO");
		//String rxNo = parm.getValue("RX_NO");
		for (int i = rowCount - 1; i >= 0; i--) {
			//if (rxNo.equals(action.getValue("RX_NO", i))) {
				action.removeRow(i);
			//}
		}
		if (action.getCount("RX_NO") <= 0) {
			// ֹͣ������ɫ
			stopColorThread();
			// ֹͣ������Ϣ
			stopMoveThread();
			// ֹͣ����
			stopSound();
		}else{
			getMessage();
		}
		textX = 0;//���ò�����ɽ���Ϣ���¹���
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
	 * ������ɫ�߳�
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
	 * ��ʼ����
	 */
	public void workColor() {
		colorRowState = !colorRowState;
		this.setBackground(colorRowState ? new Color(255, 0, 0) : new Color(0,
				0, 0));
		this.repaint();
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
	 * ֹͣ��ɫ�߳�
	 */
	private void stopColorThread() {
		colorThread = null;
	}

	/**
	 * �������������߳�
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
	 * ��ʼ����
	 */
	public void workText() {
		if (textX >= this.getWidth())
			textX = 0;
		textX += 10;
		this.repaint();
	}

	/**
	 * ֹͣ���������߳�
	 */

	private void stopMoveThread() {
		textThread = null;
	}
}
