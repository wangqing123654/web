package com.javahis.ui.sys;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Random;

import javax.swing.JWindow;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.javahis.ui.pha.PhaMainControl;

/**
 * ����SOKET����:����ҩ������
 * <p>
 * Title: ����SOKET����
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author not attributable
 * @version add by chenxi 20140326
 */
@SuppressWarnings("serial")
public class LEDMEDImage extends TLabel {
	
	/**
	 * Socket����
	 */
	private SocketLink client;
	private   Thread threadMove = null ;
	private  TLabel label ;
	private  TParm  ledParm ;
	private  Point location ;
	/**
	 * URL
	 */
	private URL url;
	/**
	 * ������������
	 */
	private AudioClip audio;
	 
	/**
	 * ������
	 * 
	 * @param owner
	 *            Frame
	 */
	public LEDMEDImage(TLabel owner, Object control, Object parm) {
		super();        
		initTemp(owner, control, parm) ;
		jbInit();
	}
	private void jbInit() {
		// ��������
		initNbwServer();
	}
	/**
	 * ���ش������
	 * 
	 * @param owner
	 *            Frame
	 * @param parm
	 *            Object
	 */
	private void initTemp(TLabel owner, Object control, Object parm) {
		    label = owner ;
			 ledParm = (TParm) parm;
		
	}
	/**
	 * ��ʼ�������������
	 */
	private void initNbwServer() {
		
		client = SocketLink.running(ledParm.getValue("ID"), ledParm.getValue("PASSWORD"));
		if (client.isClose()) {
			return;
			
		}
		client.addEventListener("Message", this, "onMessage");
		client.listener();
	}
	  /**
     * ͼƬ��˸
     */

  public void onMessage(Timestamp time, String id, String message){
//	  System.out.println(threadMove);  
//	  if (threadMove != null)
//			return;
//	   threadMove = new Thread(){
//	 public void run() {
//			while (threadMove != null) {
//				try {
//					 label.setIconName("%ROOT%\\image\\ImageIcon\\move.gif");
//					 label.setText("��������Ϣ") ;
//					Thread.sleep(1);
//					
//				} catch (InterruptedException ex) {
//				}
//			}
//		
//		}
//	};
//	 threadMove.start() ;
	  
	  label.setIconName("%ROOT%\\image\\ImageIcon\\move.gif");
	  label.setText("��������Ϣ") ;
	//��������  
       startSound();
	 
}
  public void onMoveImage(){
	  final long startTime = System.currentTimeMillis();
	   final int amplitude = 6; // �����ķ��� 	
	   final long _times = 20 * 1000; // ������ʱ��  20�� 
	   int startX = location.x - amplitude/2, startY = location.y - amplitude/2;  
		 while (System.currentTimeMillis() - startTime <= _times) { 
			 final int rx = random(startX, startX + amplitude); 
			 final int ry = random(startY, startY + amplitude); 
			 label.setText("��������Ϣ") ;
			 label.setLocation(rx, ry);     
			 try {				
				 Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }  
				 label.setLocation(location); 
				 label.setText("") ;
  }
 /**
  * ��ȡĳһ����Χ�ڵ������
  * @param from
  * @param to
  * @return
  */
 public static int random(int from, int to) {  
	 final Random random = new Random();  
     return from + random.nextInt(to - from);  
 }  

 /**
  * ͼƬֹͣ��˸
  */
	public  boolean onStopImage(){ 
	 label.setIconName("%ROOT%\\image\\ImageIcon\\sound.gif");
	 label.setText("");
	 //ֹͣ����
     stopSound();
 	boolean flg = true ;
 	return flg ;	
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

}
