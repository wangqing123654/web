/**
 * ����SOKET����:���ﻤʿվ����
 * <p>
 * Title: ����SOKET����
 * </p>
 * <p>
 * Description: ���ﻤʿվ����ʱ��ʵʱ����
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author not attributable
 * @version add by yanjing 20140402
 */
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
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.javahis.ui.onw.ONWRegNurseStationControl;
//import com.javahis.ui.pha.PhaMainControl;

public class LEDONWUI extends JWindow {
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
	ONWRegNurseStationControl control;
	/**
	 * �������
	 */
	private int mainWidth;
	
	/**
	    * ����
	    */
    private String stationCode="";
    private String onwCode="";
	
	/**
	 * ������
	 * 
	 * @param owner
	 *            Frame
	 */
	public LEDONWUI(Frame owner, Object control, Object parm) {
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
		if (control instanceof ONWRegNurseStationControl) {
			this.control = (ONWRegNurseStationControl) control;
		} 
		if (parm != null) {
			TParm ledParm = (TParm) parm;
			 this.stationCode = ledParm.getValue("STATION_CODE");
			 this.onwCode = ledParm.getValue("ONW_CODE");
//			this.phaCode =  ledParm.getValue("PHA_CODE");  yanjing ע
			TParm ledListenerParm = new TParm();
			ledListenerParm.addListener("onListenerLed", this, "onListenerLed");
			ledParm.runListener("onSelStation", ledListenerParm);
		}
	}
//	/**
//     * LED��������
//     * @param stationCode String
//     */
//    public void onListenerLed(String onwCode,String stationCode){
//        this.onwCode = onwCode;
//        this.stationCode = stationCode;
//        System.out.println("����:"+this.stationCode);
//    }
    /**
     * LED��������
     * @param stationCode String
     */
    public void onListenerLed(String onwCode){
        this.onwCode = onwCode;
//        this.stationCode = stationCode;
    }
    
    private void jbInit() {
        //��������
        initNbwServer();
       mouseListenter();
    }

    /**
     * ��ʼ�������������
     */
    private void initNbwServer() {
  	  //$$ ============  Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27 START==================$$//	
        //client = SocketLink.running("", "INWSTATION", "inw");
        client = SocketLink.running("", this.onwCode, this.onwCode);
//        client = SocketLink.running("", this.stationCode, this.stationCode);
       
        //$$ ============  Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27 END==================$$//	
//        System.out.println("2");
        if (client.isClose()) {
            return;
        }
        client.addEventListener("Message", this, "onMessage");
        client.listener();
//        System.out.println("6");
    }
    
    /**
     * ������
     */
    private void mouseListenter(){
        this.addMouseListener(new MouseAdapter() {
                 //����
                 public void mousePressed(MouseEvent e) {
                     otherX = e.getX();
                     otherY = e.getY();
                 }
                 public void mouseClicked(MouseEvent e) {
                     //˫��
                     if (e.getClickCount() == 2) {
                         doubleClickMsg(e);
                     }
                 }
             });
             //����¼�
             this.addMouseMotionListener(new MouseMotionAdapter() {
                 //����϶�
                 public void mouseDragged(MouseEvent e) {
                     mouseDraggedEvent(e);
                 }
             });
             this.setLocation(this.mainWidth-500,30);
//      this.setLocation(880,20);
             this.setBackground(new Color(51,51,153));
             this.setFont(new Font("����_GB2312",Font.BOLD,14));
             this.setSize(400, 40);
//             System.out.println("7");

    }
    /**
     * ˫����Ϣ���
     * @param e MouseEvent
     */
    private void doubleClickMsg(MouseEvent e) {
        //���˫��
        if(e.getButton()==1){
//            System.out.println("˫��");
            //˫���¼�
            doubleClick();
        }
    }
    /**
     * ˫���¼�
     */
    public void doubleClick(){
        //ֹͣ������ɫ
        stopColorThread();
        //ֹͣ������Ϣ
        stopMoveThread();
        //ֹͣ����
        stopSound();
        
        this.control.openInwCheckWindow(action);//yanjing ����
    }
    /**
     * ֹͣ��ɫ�߳�
     */
    private void stopColorThread() {
      colorThread=null;
    }
    /**
     * ֹͣ���������߳�
     */

    private void stopMoveThread() {
        textThread=null;
    }
   
    /**
     * ֹͣ����
     */
    private void stopSound() {
        if(audio!=null){
            audio.stop();
        }
    }
    
    /**
     * ����϶�
     * @param e MouseEvent
     */
    private void mouseDraggedEvent(MouseEvent e) {
        int x = e.getX()-otherX+this.getX();
        int y = e.getY()-otherY+this.getY();
        this.setLocation(x,y);
    }
    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.initClient();
        LEDUI ui = new LEDUI();
        ui.openWindow();

    }
     public void paint(Graphics g) {
         g.setColor(this.getBackground());
         g.fill3DRect(0,0,400,40,true);
         g.setColor(new Color(255,153,204));
         g.drawString(magess.toString(),textX,26);
     }
     /**
      * �ر�
      */
     public void close(){
       this.stopColorThread();
       this.stopMoveThread();
       this.stopSound();
       this.closeSocket();
       this.setVisible(false);
     }
     /**
      * �ر�Socket
      */
     public void closeSocket(){
         if(!client.isClose())
             client.close();
     }
     /**
      * ��������
      */
     private void startSound(){
         //��������
         try {
             url = new URL(TIOM_AppServer.SOCKET.getServletPath("wav/ring.wav"));
             audio = Applet.newAudioClip(url);
             //������(ѭ��)
             audio.loop();
         }
         catch (Exception e) {
             e.printStackTrace();
         }
     }
     /**
      * �򿪴���
      */
     public void openWindow(){
        this.setVisible(true);
     }

     /**
      * ������Ϣ
      * @param action String
      */
     public void onMessage(Timestamp time,String id,String message){
         String messages[] = message.split("\\|");
         for (String temp : messages) {
             String mes[] = temp.split(":");
             action.addData(mes[0], mes[1]);
             action.setCount(messages.length);
         }
         magess = new StringBuffer();
         int rowAction = action.getCount("DR_CODE");
         for(int i=0;i<rowAction;i++){
             if(stationCode.equals(action.getValue("CLINICAREA_CODE",i))){
            	 String clinicRoomNo = "SELECT A.CLINICROOM_DESC FROM REG_CLINICROOM A WHERE  " +
     	 		"  IP = '"+action.getValue("IP",i)+"'";
     	       String drDesc = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+action.getValue("DR_CODE",i)+"'";
     	       TParm roomParm = new TParm(TJDODBTool.getInstance().select(clinicRoomNo));
     	       TParm drParm = new TParm(TJDODBTool.getInstance().select(drDesc));
     	       magess.append("����һλ���˵�  "+roomParm.getValue("CLINICROOM_DESC",0)+drParm.getValue("USER_NAME",0)+" ҽ��������");
     	      //������ɫ�߳�
     	         changeTableRowColor();
     	         //���ù�������
     	         startMoveThread();
     	         //��������
     	         startSound();
             }else{
            	 //ֹͣ������ɫ
                 stopColorThread();
                 //ֹͣ������Ϣ
                 stopMoveThread();
                 //ֹͣ����
                 stopSound();
             }
        	
         }
        
     }
     /**
      * ɾ����Ϣ����
      * @param parm TParm
      */
     public void removeMessage(TParm parm){
         int rowCount = action.getCount("QUE_NO");
         String queNo=parm.getValue("QUE_NO").toString();
         for(int i=rowCount-1;i>=0;i--){
             if(queNo.equals(action.getValue("QUE_NO",i))){
                 action.removeRow(i);
             }
         }
     }
     /**
      * �ı�TABLE��ɫ
      * @param row int
      */
     public void changeTableRowColor(){
         startColorThread();
     }

   /**
    * �������������߳�
    */
   private void startMoveThread(){
       if(textThread!=null)
             return;
         textThread = new Thread() {
             public void run() {
                 while(textThread!=null){
                     try {
                         Thread.sleep(500);
                         workText();
                     }
                     catch (InterruptedException ex) {
                     }
                 }
                 magess = new StringBuffer();
                 repaint();
             }
         };
         textThread.start();
   }
   /**
    * ������ɫ�߳�
    */
   private void startColorThread() {
       if(colorThread!=null)
            return;
        colorThread = new Thread() {
            public void run() {
                while(colorThread!=null){
                    try {
                        Thread.sleep(500);
                        workColor();
                    }
                    catch (InterruptedException ex) {
                    }
                }
                setBackground(new Color(0,51,102));
                repaint();
            }
        };
        colorThread.start();
   }
   /**
    * ��ʼ����
    */
   public void workColor(){
       colorRowState=!colorRowState;
       this.setBackground(colorRowState?new Color(255,0,0):new Color(0,0,0));
       this.repaint();
   }
   /**
    * ��ʼ����
    */
   public void workText(){
       if(textX>=this.getWidth())
           textX=0;
       textX+=10;
       this.repaint();
   }
}