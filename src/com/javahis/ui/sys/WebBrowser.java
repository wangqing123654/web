package com.javahis.ui.sys;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
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
public class WebBrowser extends JFrame{
    JPanel contentPane;
 BorderLayout borderLayoutAll=new BorderLayout();
 JLabel jLabelPrompt=new JLabel(); //״̬��ʾ��
 BorderLayout borderLayoutMain=new BorderLayout();
 JPanel jPanelMain=new JPanel();
 JTextField textFieldURL=new JTextField(); //URL�����
 JEditorPane jEditorPane=new JEditorPane(); //��ʾ��ҳ����

 /**
  * ���캯��
  * @param args
  */
 public WebBrowser(){

   try {
    Init();
   } catch (Exception e) {
    // TODO �Զ����� catch ��
    e.printStackTrace();
   }

 }
 private void Init() throws Exception{
  contentPane=(JPanel) getContentPane();
  contentPane.setLayout(borderLayoutAll);
  jPanelMain.setLayout(borderLayoutMain);
  jLabelPrompt.setText("������URL");
  textFieldURL.setText("");
  textFieldURL.addActionListener(new java.awt.event.ActionListener(){
   public void actionPerformed(ActionEvent e){
    textFieldURL_actionPerformed(e);
   }
  });
  jEditorPane.setEditable(false);
  jEditorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener(){
   public void hyperlinkUpdate(HyperlinkEvent e){
    jEditorPane_hyperlinkUpdate(e);
   }
  });
  JScrollPane scrollPane=new JScrollPane();
  scrollPane.getViewport().add(jEditorPane);
  jPanelMain.add(textFieldURL,"North");
  jPanelMain.add(scrollPane,"Center");
  contentPane.add(jLabelPrompt,"North");
  contentPane.add(jPanelMain,"Center");
  enableEvents(AWTEvent.WINDOW_EVENT_MASK);
  this.setSize(new Dimension(600,500));
  this.setTitle("����IE");
  this.setVisible(true);
 }
 void textFieldURL_actionPerformed(ActionEvent e){
  try {
   jEditorPane.setPage(textFieldURL.getText());//��ʾURL
  } catch (IOException e1) {
   JOptionPane msg=new JOptionPane();
   JOptionPane.showMessageDialog(this, "URL��ַ����ȷ��"+textFieldURL.getText(),"���벻��ȷ",0);
  }

 }
 void jEditorPane_hyperlinkUpdate(HyperlinkEvent e){
  if(e.getEventType()==javax.swing.event.HyperlinkEvent.EventType.ACTIVATED){
   try {
    URL url=e.getURL();
    jEditorPane.setPage(url);
    textFieldURL.setText(url.toString());
   } catch (Exception io) {
    // TODO: handle exception
    JOptionPane msg=new JOptionPane();
    JOptionPane.showMessageDialog(this,"�򿪸�����ʧ�ܣ�","���벻��ȷ",0);
   }

  }
 }
 protected void processWindowEvent (WindowEvent e){
  super.processWindowEvent(e);
  if(e.getID()==WindowEvent.WINDOW_CLOSING){
   System.exit(0);
  }
 }
 public static void main(String[] args) {

  new WebBrowser();
 }

}
