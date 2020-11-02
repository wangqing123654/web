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
 * 监听SOKET窗口:门诊药房监听
 * <p>
 * Title: 监听SOKET窗口
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
	 * Socket工具
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
	 * 调用声音对象
	 */
	private AudioClip audio;
	 
	/**
	 * 构造器
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
		// 启动监听
		initNbwServer();
	}
	/**
	 * 加载窗体监听
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
	 * 初始监听服务服务器
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
     * 图片闪烁
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
//					 label.setText("您有新消息") ;
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
	  label.setText("您有新消息") ;
	//启动音乐  
       startSound();
	 
}
  public void onMoveImage(){
	  final long startTime = System.currentTimeMillis();
	   final int amplitude = 6; // 抖动的幅度 	
	   final long _times = 20 * 1000; // 抖动的时间  20秒 
	   int startX = location.x - amplitude/2, startY = location.y - amplitude/2;  
		 while (System.currentTimeMillis() - startTime <= _times) { 
			 final int rx = random(startX, startX + amplitude); 
			 final int ry = random(startY, startY + amplitude); 
			 label.setText("您有新消息") ;
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
  * 获取某一个范围内的随机数
  * @param from
  * @param to
  * @return
  */
 public static int random(int from, int to) {  
	 final Random random = new Random();  
     return from + random.nextInt(to - from);  
 }  

 /**
  * 图片停止闪烁
  */
	public  boolean onStopImage(){ 
	 label.setIconName("%ROOT%\\image\\ImageIcon\\sound.gif");
	 label.setText("");
	 //停止声音
     stopSound();
 	boolean flg = true ;
 	return flg ;	
 }
	
	 /**
     * 停止音乐
     */
    private void stopSound() {
        if(audio!=null){
            audio.stop();
        }
    }
    
    /**
     * 启动声音
     */
    private void startSound(){
        //调用音乐
        try {
            url = new URL(TIOM_AppServer.SOCKET.getServletPath("wav/ring.wav"));
            audio = Applet.newAudioClip(url);
            //放音乐(循环)
            audio.loop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
