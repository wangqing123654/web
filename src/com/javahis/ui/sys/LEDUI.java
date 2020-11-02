package com.javahis.ui.sys;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.dongyang.data.TParm;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;

import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;
import com.dongyang.manager.TIOM_AppServer;
import java.sql.Timestamp;
import com.javahis.ui.odi.ODIMainControl;

/**
 * ����SOKET����
 * <p>Title: ����SOKET����</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author not attributable
 * @version add by Ningjf 20080117
 */
public class LEDUI extends JWindow{
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
    private URL url ;
    /**
     * ������������
     */
    private AudioClip audio ;
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
   ODIMainControl control;
   /**
    * �������
    */
   private int mainWidth;
   /**
    * ����
    */
   private String stationCode="";
    /**
     * ������
     * @param owner Frame
     */
    public LEDUI(Frame owner,Object control,Object parm) {
        super(owner);
        initTemp(owner,control,parm);
        jbInit();
    }
    /**
     * ���ش������
     * @param owner Frame
     * @param parm Object
     */
    private void initTemp(Frame owner,Object control, Object parm) {
        this.mainWidth = owner.getWidth();
//        System.out.println("��������:" + this.mainWidth);
        if (control instanceof ODIMainControl) {
            this.control = (ODIMainControl) control;
        }
        if (parm != null) {
            TParm ledParm = (TParm) parm;
            this.stationCode = ledParm.getValue("STATION_CODE");
            TParm ledListenerParm = new TParm();
            ledListenerParm.addListener("onListenerLed", this, "onListenerLed");
            ledParm.runListener("onSelStation", ledListenerParm);
        }

    }
    /**
     * ������
     * ��ʿվִ�в���
     * @param owner Frame
     * ========pangben 2011-11-10
     */
    public LEDUI(Frame owner,Object control,Object parm,boolean flg) {
        super(owner);
        initTemp(owner,control,parm);
        jbInitOne();
    }
    /**
     * ��ʿվִ�в���
     */
    private void jbInitOne(){
        //��������
          initNbwServerOne();
          mouseListenter();
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
     * LED��������
     * @param stationCode String
     */
    public void onListenerLed(String stationCode){
        this.stationCode = stationCode;
//        System.out.println("����:"+this.stationCode);
    }
    public LEDUI() {
        jbInit();
    }
  private void jbInit() {
      //��������
      initNbwServer();
     mouseListenter();
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
   * ˫����Ϣ���
   * @param e MouseEvent
   */
  private void doubleClickMsg(MouseEvent e) {
      //���˫��
      if(e.getButton()==1){
//          System.out.println("˫��");
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
      this.control.openInwCheckWindow(action);
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
   * ֹͣ����
   */
  private void stopSound() {
      if(audio!=null){
          audio.stop();
      }
  }
  /**
   * �򿪴���
   */
  public void openWindow(){
     this.setVisible(true);
  }
  private void initNbwServerOne(){
	//$$ ============  Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27 START==================$$//	
//      System.out.println("1");
     //client = SocketLink.running("", "INWSTATION", "inw");
      client = SocketLink.running("", this.stationCode, this.stationCode);
    //$$ ============  Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27 END==================$$//	
//      System.out.println("2");
      if (client.isClose()) {
          return;
      }
//      System.out.println("4");
      client.addEventListener("Message", this, "onMessageOne");
//      System.out.println("5");
      client.listener();
//      System.out.println("6");

  }
  /**
   * ��ʼ�������������
   */
  private void initNbwServer() {
	  //$$ ============  Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27 START==================$$//	
//      System.out.println("1");
      //client = SocketLink.running("", "INWSTATION", "inw");
      client = SocketLink.running("", this.stationCode, this.stationCode);
     
      //$$ ============  Modified by lx ҽ��������,��ʿ�������շ���Ϣ2012/02/27 END==================$$//	
//      System.out.println("2");
      if (client.isClose()) {
//          System.out.println("3");
          return;
      }
//      System.out.println("4");
      client.addEventListener("Message", this, "onMessage");
//      System.out.println("5");
      client.listener();
//      System.out.println("6");
  }
  public void onMessageOne(Timestamp time,String id,String message){
      action=new TParm() ;
//      System.out.println(time + " " + id + " " + message);
      String messages[] = message.split("\\|");
//      aa:
      for (String temp : messages) {
          String mes[] = temp.split(":");
//          if("CASE_NO".equals(mes[0])){
//              int rowCount = action.getCount("CASE_NO");
//              for(int i=0;i<rowCount;i++){
//
//                  if(action.getValue("CASE_NO",i).equals(mes[1]))
//                      break aa;
//              }
//          }
//
//          if("STATION_CODE".equals(mes[0])){
//              if (!this.stationCode.equals(mes[1]))
//                  return;
//          }
           //System.out.println("mes[1]:"+mes[1]);
           if(mes.length==1)
               action.addData(mes[0], "");
           else
               action.addData(mes[0], mes[1]);
      }
//      System.out.println("action"+action);
      action.setData("FLG","Y");//��ʿվִ�йܿ�
      magess = new StringBuffer();
      int rowAction = action.getCount("CASE_NO");
      magess.append("��������:");
      for(int i=0;i<rowAction;i++){
          magess.append(action.getValue("PAT_NAME",i)+" ");
      }
       magess.append(" ����δִ�е�ҽ��       ");
//      System.out.println("��Ϣ:"+magess.toString());
      //������ɫ�߳�
      changeTableRowColor();
      //���ù�������
      startMoveThread();
      //��������
      startSound();

  }
  /**
   * ������Ϣ
   * @param action String
   */
  public void onMessage(Timestamp time,String id,String message){	  
      String messages[] = message.split("\\|");
      aa:
      for (String temp : messages) {
          String mes[] = temp.split(":");
          if(mes.length==1){
        	  mes[1]="";
          }else if(mes.length==2&& mes[1].equals("null")){
        	  mes[1]="";
          }
         if("CASE_NO".equals(mes[0])){
              int rowCount = action.getCount("CASE_NO");
              for(int i=0;i<rowCount;i++){
                  if(action.getValue("CASE_NO",i).equals(mes[1]))
                      break aa;
              }
          }
/*          
          if("STATION_CODE".equals(mes[0])){
              if (!this.stationCode.equals(mes[1]))
                  return;
          }*/
          
          action.addData(mes[0], mes[1]);
          //System.out.println("action111111"+action);
      }
      //System.out.println("action222222"+action);
      magess = new StringBuffer();
      int rowAction = action.getCount("CASE_NO");
      for(int i=0;i<rowAction;i++){
          magess.append("��������:");
          magess.append(action.getValue("PAT_NAME",i));
          magess.append("        ");
      }
     System.out.println("��Ϣ:"+magess.toString());
      //������ɫ�߳�
      changeTableRowColor();
      //���ù�������
      startMoveThread();
      //��������
      startSound();
  }
  /**
   * ɾ����Ϣ����
   * @param parm TParm
   */
  public void removeMessage(TParm parm){
      int rowCount = action.getCount("CASE_NO");
      String caseNo=parm.getValue("CASE_NO");
      for(int i=rowCount-1;i>=0;i--){
          if(caseNo.equals(action.getValue("CASE_NO",i))){
              action.removeRow(i);
          }
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
     * �ı�TABLE��ɫ
     * @param row int
     */
    public void changeTableRowColor(){
        startColorThread();
    }

  /**
   * ֹͣ��ɫ�߳�
   */
  private void stopColorThread() {
    colorThread=null;
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
     * ��ʼ����
     */
    public void workText(){
        if(textX>=this.getWidth())
            textX=0;
        textX+=10;
        this.repaint();
    }
  /**
   * ֹͣ���������߳�
   */

  private void stopMoveThread() {
      textThread=null;
  }
}
