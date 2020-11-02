package com.javahis.ui.ins;

import java.util.Vector;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import com.tiis.ui.TiFrame;
//import publics.Comm_Operator;
import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
//import publics.Comm_System;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import com.javahis.ui.ins.INSDownloadPayControl;

/**
 * <p>Title:ҽ��֧����Ϣ����</p>
 * <p>Description: ���ط�����ϸ��ť</p>
 * <p>Copyright: Copyright (c) 20060920</p>
 * @version 4.0
 */
public class INSDriveBatches implements Runnable {
  private String fileName;//�ļ���
  private BufferedWriter file;//�ļ���
  private TParm patList;//�����б�
  private TTable tab2Table;
  private TButton button;
  private TTextField patsum;
  private Thread thread;//�߳�
  private TiFrame frame;//����
  private TParm Parm;
  private TParm Parm1;
  private TParm Parm2;
  private boolean err;//����״̬
  private Thread threadServer;//�߳�
  private String passelNo;//��������
  private TParm regionParm;

  /**
   * @alias ������
   * @param frame TiFrame
   */
  public INSDriveBatches(TParm Parm) {
//    safe = new Safe();
	this.Parm = Parm;

    regionParm = SYSRegionTool.getInstance().selectdata(
			Operator.getRegion());// ���ҽ���������
    
  }

  /**
   * @alias �õ��ļ���
   * @return String
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @alias �����ļ���
   * @param name String
   */
  public void setFileName(String name) {
    fileName = name;
  }

  /**
   * @alias �õ���������
   * @return String
   */
  public String getPasselNo()
  {
    return passelNo;
  }

  /**
   * @alias ���ñ�������
   * @param passelNo String
   */
  public void setPasselNo(String passelNo)
  {
    this.passelNo = passelNo;
  }

  /**
   * @alias �õ���������
   * @return String
   */
  public TParm getPatList()
  {
    return patList;
  }

  /**
   * @alias ���ñ�������
   * @param passelNo String
   */
  public void setPatList(TParm patList)
  {
    this.patList = patList;
  }

  /**
   * @alias �õ�tab2Table������
   * @return String
   */
  public TTable gettab2Table()
  {
    return tab2Table;
  }

  /**
   * @alias ����tab2Table������
   * @param passelNo String
   */
  public void settab2Table(TTable tab2Table)
  {
    this.tab2Table = tab2Table;
  }
  /**
   * @alias �õ�TTextField(PAT_NUM)������
   * @return String
   */
  public TTextField getpatsum()
  {
    return patsum;
  }

  /**
   * @alias ����TTextField(PAT_NUM)������
   * @param passelNo String
   */
  public void setpatsum(TTextField patsum)
  {
    this.patsum = patsum;
  }
  /**
   * @alias �õ�tButton_3������
   * @return String
   */
  public TButton getbutton()
  {
    return button;
  }

  /**
   * @alias ����tButton_3������
   * @param passelNo String
   */
  public void setbutton(TButton button)
  {
    this.button = button;
  }
  /**
   * @alias ���ļ�
   * <p>
   * ���ļ�
   * <p>
   * @return boolean
   * @version 1.0
   */
  private boolean openFile() {
    if (getFileName() == null || getFileName().length() == 0)
      return false;
    try {
      file = new BufferedWriter(new FileWriter(getFileName()));
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * @alias ȡ���ļ�����
   * <p>
   * ȡ���ļ�����
   * <p>
   * @return String
   * @version 1.0
   */
  public String getData()
  {
    String s = "";
    try{
      File f = new File(this.getFileName());
      byte data[] = new byte[(int)f.length()];

      FileInputStream fi = new FileInputStream(f);
      fi.read(data,0,data.length);
      s = new String(data).trim();
      fi.close();
    }catch(Exception e)
    {
      e.printStackTrace();
    }
    return s;
  }

  /**
   * @alias �ر��ļ�
   * <p>
   * �ر��ļ�
   * <p>
   * @return boolean
   * @version 1.0
   */
  private boolean closeFile() {
    if (file == null)
      return false;
    try {
      file.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * @alias д�ļ�
   * <p>
   * д�ļ�
   * <p>
   * @param data String
   * @version 1.0
   */
  private void write(String data)
  {
//	 System.out.println("�߳�==========11");  
    try{
      file.write(data);
      file.newLine();
    }catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * @alias ����
   * <p>
   * ����
   * <p>
   * @version 1.0
   */
  public void run() {
// System.out.println("�߳�==========7");
    err = false;
    //���ļ�
    openFile();
// System.out.println("�߳�==========8");
    //����ȫ��������Ϣ
    downAllPat(patList);
    //�ر��ļ�
    closeFile();
    if(!this.isRun())
      err = true;
    if(err)
    {
      thread = null;
      return;
    }
    threadServer = new Thread(new ClientSocket());
    threadServer.start();
//    System.out.println("�߳�==========31");
    if(!upload())
    {
      thread = null;
      messagebox("����ʧ��!");
      this.getbutton().setEnabled(true);
      return;
    }
//    System.out.println("�߳�==========49");
    messagebox("���سɹ�!");
    this.getbutton().setEnabled(true);
    removeMrTable(this.gettab2Table().getParmValue());
    thread = null;
  }
  /**
	 * �Ƴ������б�����
	 */
	private void removeMrTable(TParm parm){
		int row=patList.getCount("CONFIRM_NO");
		for (int i = 0; i < row; i++) {
			if (parm.getCount()>=1) {
				parm.removeRow(0);
			}
		}
		if(parm.getCount("CONFIRM_NO")>=row){
		}else{
			parm.setCount(0);
		}
		this.gettab2Table().setParmValue(parm);
		this.getpatsum().setValue(parm.getCount()+"");
	}

  /**
   * @alias ��ʼ
   */
  public void start()
  {
    thread = new Thread(this); 
    thread.start();
  }

  /**
   * @alias ֹͣ
   */
  public void stop()
  {
    messagebox("���ر��û���ֹ");
    thread = null;
  }

  /**
   * @alias �ж��Ƿ�������
   * @return boolean
   */
  public boolean isRun()
  {
    return thread != null;
  }
  /**
   * @alias  ���ز���ȫ����Ϣ
   * <p>
   * ���ز���ȫ����Ϣ
   * <p>
   * @param patList Vector
   * @version 1.0
   */
  private void downAllPat(TParm patList)
  {
//	  System.out.println("�߳�==========9"+patList);  
    if(patList == null)
      return;
    if(!"0".equals(patList.getData("TYPE")))
    {
      messagebox("������Ϣ����ʧ��");
      err = true;
      return;
    }
    TParm patListdata=new TParm();
    patListdata.setData("CONFIRM_NO",patList.getData("CONFIRM_NO"));
//    System.out.println("�߳�==========9patListdata"+patListdata);  
    int size = patListdata.getCount("CONFIRM_NO");
//    System.out.println("�߳�==========10"+size);  
    write("" + size);
//    System.out.println("�߳�==========12");
    TParm data = new TParm();
    for(int i = 0;i<size;i++)
    {
//    System.out.println("�߳�==========13");  	
      if(!isRun())
      {
        err = true;
        return;
      }
     String seqNumber = (String)patList.getData("CONFIRM_NO",i);
//     System.out.println("�߳�==========14"+seqNumber); 
     Parm1 = getCommDownload(seqNumber, "DataDown_sp", "G");
//     System.out.println("�߳�==========22");        
     Parm2 = detailCZ(seqNumber);
     
    }     
  }
  /**
   * @alias ���ؽ�����Ϣ
   * <p>
   * ���ؽ�����Ϣ
   * <p>
   * @param patNo String ����˳���
   * @return String
   * @version 1.0
   */
  public TParm getCommDownload(String seqNumber, String pipeline,
			String plotType) {
//	  System.out.println("�߳�==========15"); 
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", pipeline);
		parm.setData("PLOT_TYPE", plotType);
		TParm tab4Download_G_J = InsManager.getInstance().safe(parm, "");
//		System.out.println("�߳�==========16"+tab4Download_G_J); 
		if (tab4Download_G_J == null || tab4Download_G_J.getErrCode() < 0) {
			return tab4Download_G_J;
		}
		//д���ļ���
		write(VtoStringspg(tab4Download_G_J));
//		System.out.println("�߳�==========21"); 
		return tab4Download_G_J;
	}
  /**
	 * ��ȡ��ְ������ϸ�;ܸ���Ϣ
	 * 
	 * @return
	 */
	private TParm detailCZ(String seqNumber) {
//		System.out.println("�߳�==========23"); 
		TParm parm = new TParm();
		parm.addData("CONFIRM_NO", seqNumber);
		parm.addData("PARM_COUNT", 1);
		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "I");
		TParm tab4Download_I_E = InsManager.getInstance().safe(parm, "");
//		System.out.println("�߳�==========24"+tab4Download_I_E);
		write("" + tab4Download_I_E.getCount("CONFIRM_NO"));
		//д���ļ���
		VtoStringrsi(tab4Download_I_E);	
//		System.out.println("�߳�==========30");
		return tab4Download_I_E;
	}

  /**
	 * ��ι���
	 * 
	 * @param seqNumber
	 * @return
	 */
	private TParm getDownLoadParm(String seqNumber) {
		TParm actionParm = new TParm();
		String hospital = regionParm.getData("NHI_NO", 0).toString();
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("NHI_HOSP_CODE", hospital);
		actionParm.setData("ADM_SEQ", seqNumber);
		actionParm.setData("REPORT_CODE", this.getPasselNo());
		return actionParm;
	}

  /**
   * @alias ��VectorתString
   * <p>
   * ��VectorתString
   * <p>
   * @param data Vector
   * @return String
   * @version 1.0
   */
  private String VtoStringspg(TParm data)
  {
//	  System.out.println("�߳�==========17"+data);
	  Vector vct = new Vector();
    StringBuffer sb = new StringBuffer();
    for(int i = 0;i < data.getCount("NAME");i++)
    {
    	vct.add(data.getRowVector(i, "INSBRANCH_CODE").get(0));
    	vct.add(data.getRowVector(i, "ADM_SEQ").get(0));
    	vct.add(data.getRowVector(i, "ISSUE").get(0));
    	vct.add(data.getRowVector(i, "NAME").get(0));
    	vct.add(data.getRowVector(i, "CTZ1_CODE").get(0));
    	vct.add(data.getRowVector(i, "PAY_TYPE").get(0));
    	vct.add(data.getRowVector(i, "UNIT_CODE1").get(0));
    	vct.add(data.getRowVector(i, "UNIT_GENRE").get(0));
    	vct.add(data.getRowVector(i, "ECONOMY_GENRE").get(0));
    	vct.add(data.getRowVector(i, "HOSP_NHI_NO").get(0));
    	vct.add(data.getRowVector(i, "HOSP_CODE").get(0));
    	vct.add(data.getRowVector(i, "HOSP_CATE").get(0));
    	vct.add(data.getRowVector(i, "PHA_AMT").get(0));
    	vct.add(data.getRowVector(i, "PHA_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "EXM_AMT").get(0));
    	vct.add(data.getRowVector(i, "EXM_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "TREAT_AMT").get(0));
    	vct.add(data.getRowVector(i, "TREAT_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "OP_AMT").get(0));
    	vct.add(data.getRowVector(i, "OP_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "BED_AMT").get(0));
    	vct.add(data.getRowVector(i, "BED_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "MATERIAL_AMT").get(0));
    	vct.add(data.getRowVector(i, "MATERIAL_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "ELSE_AMT").get(0));
    	vct.add(data.getRowVector(i, "ELSE_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOODALL_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOODALL_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOOD_AMT").get(0));
    	vct.add(data.getRowVector(i, "BLOOD_NHI_AMT").get(0));
    	vct.add(data.getRowVector(i, "REFUSE_ADD_AMT").get(0));
    	vct.add(data.getRowVector(i, "NUM_AMT").get(0));
    	vct.add(data.getRowVector(i, "BCSSQF_STANDRD_AMT").get(0));
    	vct.add(data.getRowVector(i, "INS_STANDARD_AMT").get(0));
    	vct.add(data.getRowVector(i, "OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "PERCOPAYMENT_RATE_AMT").get(0));
    	vct.add(data.getRowVector(i, "ADD_AMT").get(0));
    	vct.add(data.getRowVector(i, "INS_HIGHLIMIT_AMT").get(0));
    	vct.add(data.getRowVector(i, "TRANBLOOD_OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "APPLY_AMT").get(0));
    	vct.add(data.getRowVector(i, "APPLY_AGENT_AMT").get(0));
    	vct.add(data.getRowVector(i, "HOSP_APPLY_AMT").get(0));
    	vct.add(data.getRowVector(i, "TOTAL_NHI_AMT_ASSIST_PAY_CONFIRM").get(0));
    	vct.add(data.getRowVector(i, "REPORT_CODE").get(0));
    	vct.add(data.getRowVector(i, "NUM_DATE").get(0));
    	vct.add(data.getRowVector(i, "ARMYAI_AMT").get(0));
    	vct.add(data.getRowVector(i, "SERVANT_AMT").get(0));
    	vct.add(data.getRowVector(i, "ACCOUNT_PAY_AMT").get(0));
    	vct.add(data.getRowVector(i, "SIN_DISEASE_CODE").get(0));
    	vct.add(data.getRowVector(i, "NHI_OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "EXT_OWN_AMT").get(0));
    	vct.add(data.getRowVector(i, "COMP_AMT").get(0));
    	vct.add(data.getRowVector(i, "SPEC_NEED_AMT").get(0));
    }
//    System.out.println("�߳�==========18"+vct);
//    System.out.println("�߳�==========size"+vct.size());
    for(int i = 0;i < vct.size();i++)
    {
//    System.out.println("�߳�==========19");	
      String s =(String)vct.get(i);
      s = s.replaceAll("\r","");
      s = s.replaceAll("\n","");
      sb.append(s);
      sb.append("|");
    }
//    System.out.println("�߳�==========20"+sb);	
    return sb.toString();
  }
  /**
   * @alias ��VectorתString
   * <p>
   * ��VectorתString
   * <p>
   * @param data Vector
   * @return String
   * @version 1.0
   */
  private String VtoStringrsi(TParm data)
  {   
//	  System.out.println("�߳�==========25"+data);
	  
    Vector vctdata = new Vector();
	  StringBuffer sb = new StringBuffer();
//	  System.out.println("�߳�StringBuffer");
	  for(int i = 0;i < data.getCount("CONFIRM_NO");i++)
	    {
		  Vector vct = new Vector();
		  vct.add(data.getRowVector(i, "CONFIRM_NO").get(0));
		  vct.add(data.getRowVector(i, "CHARGE_DATE").get(0));
		  vct.add(data.getRowVector(i, "SEQ_NO").get(0));
		  vct.add(data.getRowVector(i, "HOSP_NHI_NO").get(0));
		  vct.add(data.getRowVector(i, "WHYFW_ITEM_SEQ_NO").get(0));
		  vct.add(data.getRowVector(i, "ORDER_DESC").get(0));
		  vct.add(data.getRowVector(i, "OWN_RATE").get(0));
		  vct.add(data.getRowVector(i, "DOSE_CODE").get(0));
		  vct.add(data.getRowVector(i, "STANDARD").get(0));
		  vct.add(data.getRowVector(i, "DEGREE").get(0));
		  vct.add(data.getRowVector(i, "PRICE").get(0));
		  vct.add(data.getRowVector(i, "QTY").get(0));
		  vct.add(data.getRowVector(i, "TOTAL_AMT").get(0));
		  vct.add(data.getRowVector(i, "TOTAL_NHI_AMT").get(0));
		  vct.add(data.getRowVector(i, "OWN_AMT").get(0));
		  vct.add(data.getRowVector(i, "ADDPAY_AMT").get(0));
		  vct.add(data.getRowVector(i, "OP_FLG").get(0));
		  vct.add(data.getRowVector(i, "ADDPAY_FLG").get(0));
		  vct.add(data.getRowVector(i, "NHI_ORD_CLASS_CODE").get(0));
		  vct.add(data.getRowVector(i, "PHAADD_FLG").get(0));
		  vct.add(data.getRowVector(i, "CARRY_FLG").get(0));
		  vct.add(data.getRowVector(i, "REFUSE_AMT").get(0));
		  vct.add(data.getRowVector(i, "REFUSE_CODE").get(0));
		  vct.add(data.getRowVector(i, "REFUSE_DESC").get(0));
		  vct.add(data.getRowVector(i, "PZWH").get(0));
//		  System.out.println("�߳�==========26"+vct);
		  vctdata.add(vct);		  
	    }
//	  System.out.println("�߳�==========27"+vctdata);
	  for(int i = 0;i < vctdata.size();i++)
	    {
		  write(VtoString((Vector)vctdata.get(i))); 
	    }
	  return sb.toString();
  }
  
  private String VtoString(Vector data)
  {
    StringBuffer sb = new StringBuffer();
    for(int i = 0;i < data.size();i++)
    {
      String s = (String)data.get(i);
      s = s.replaceAll("\r","");
      s = s.replaceAll("\n","");
      sb.append(s);
      sb.append("|");
    }
    return sb.toString();
  }

  /**
   * @alias �ϴ���������
   * <p>
   * �ϴ���������
   * <p>
   * @return boolean
   * @version 1.0
   */
  private boolean upload()
	{	 
//	  System.out.println("�߳�==========32");
	  TParm actionParm = new TParm();
	     
	     Vector vct = new Vector();
		 vct.add(getData());
//		 System.out.println("�߳�==========33"+vct);  
		  //��Socket
		    Socket client = null;
		    BufferedWriter write = null;
		    try{
//		    	 System.out.println("�߳�==========34");  	
		      client = new Socket(Operator.getIP(), 8100);
		      write = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		    }catch(Exception e)
		    {
		      e.printStackTrace();
		    }
		    try{ 
//		    	 System.out.println("�߳�==========35");  
		    //ȡ���ļ�����
		      BufferedReader sr = new BufferedReader(new StringReader((String)vct.get(0)));
//		      System.out.println("�߳�==========36"+sr);  
		      //����������
		      int count = Integer.parseInt(sr.readLine());
//		      System.out.println("�߳�==========37"+count);  
		      /**
		       * ѭ��ȡ��ÿһ����������Ϣд�����ݿ�
		       */
		      for(int i = 0;i < count;i ++)
		      {   
		    	  TParm actionParm1 = new TParm();
//		    	  System.out.println("�߳�==========38");    
		    	  //��������Ϣ
		          String pat = sr.readLine();
//		          System.out.println("�߳�==========39"+pat);    
		          //ת����Vector
		          Vector patvct=stringToVector(pat);
		          //��ø������ݿ�����
		          actionParm = getDownLoadParm(""+patvct.get(1));
//		          System.out.println("�߳�==========40"+patvct); 
		          actionParm1.setData("INSBRANCH_CODE", patvct.get(0));
		          actionParm1.setData("CONFIRM_NO", patvct.get(1)); 
		          actionParm1.setData("ISSUE", patvct.get(2));
		          actionParm1.setData("NAME", patvct.get(3));
		          actionParm1.setData("CTZ1_CODE", patvct.get(4));
		          actionParm1.setData("PAY_TYPE", patvct.get(5));
		          actionParm1.setData("UNIT_CODE1", patvct.get(6));
		          actionParm1.setData("UNIT_GENRE", patvct.get(7));
		          actionParm1.setData("ECONOMY_GENRE", patvct.get(8));
		          actionParm1.setData("HOSP_NHI_NO", patvct.get(9));
		          actionParm1.setData("HOSP_CODE", patvct.get(10));
		          actionParm1.setData("HOSP_CATE", patvct.get(11));
		          actionParm1.setData("PHA_AMT", patvct.get(12));
		          actionParm1.setData("PHA_NHI_AMT", patvct.get(13));
		          actionParm1.setData("EXM_AMT", patvct.get(14));
		          actionParm1.setData("EXM_NHI_AMT", patvct.get(15));
		          actionParm1.setData("TREAT_AMT", patvct.get(16));
		          actionParm1.setData("TREAT_NHI_AMT", patvct.get(17));
		          actionParm1.setData("OP_AMT", patvct.get(18));
		          actionParm1.setData("OP_NHI_AMT", patvct.get(19));
		          actionParm1.setData("BED_AMT", patvct.get(20));
		          actionParm1.setData("BED_NHI_AMT", patvct.get(21));
		          actionParm1.setData("MATERIAL_AMT", patvct.get(22));
		          actionParm1.setData("MATERIAL_NHI_AMT", patvct.get(23));
		          actionParm1.setData("ELSE_AMT", patvct.get(24));
		          actionParm1.setData("ELSE_NHI_AMT", patvct.get(25));
		          actionParm1.setData("BLOODALL_AMT", patvct.get(26));
		          actionParm1.setData("BLOODALL_NHI_AMT", patvct.get(27));
		          actionParm1.setData("BLOOD_AMT", patvct.get(28));
		          actionParm1.setData("BLOOD_NHI_AMT", patvct.get(29));
		          actionParm1.setData("REFUSE_ADD_AMT", patvct.get(30));
		          actionParm1.setData("NUM_AMT", patvct.get(31));
		          actionParm1.setData("BCSSQF_STANDRD_AMT", patvct.get(32));
		          actionParm1.setData("INS_STANDARD_AMT", patvct.get(33));
		          actionParm1.setData("OWN_AMT", patvct.get(34));
		          actionParm1.setData("PERCOPAYMENT_RATE_AMT", patvct.get(35));
		          actionParm1.setData("ADD_AMT", patvct.get(36));
		          actionParm1.setData("INS_HIGHLIMIT_AMT", patvct.get(37));
		          actionParm1.setData("TRANBLOOD_OWN_AMT", patvct.get(38));
		          actionParm1.setData("APPLY_AMT", patvct.get(39));
		          actionParm1.setData("APPLY_AGENT_AMT", patvct.get(40));
		          actionParm1.setData("HOSP_APPLY_AMT", patvct.get(41));
		          actionParm1.setData("TOTAL_NHI_AMT_ASSIST_PAY_CONFIRM", patvct.get(42));
		          actionParm1.setData("REPORT_CODE", patvct.get(43));
		          actionParm1.setData("NUM_DATE", patvct.get(44));
		          actionParm1.setData("ARMYAI_AMT", patvct.get(45));
		          actionParm1.setData("SERVANT_AMT", patvct.get(46));
		          actionParm1.setData("ACCOUNT_PAY_AMT", patvct.get(47));
		          actionParm1.setData("SIN_DISEASE_CODE", patvct.get(48));
		          actionParm1.setData("NHI_OWN_AMT", patvct.get(49));
		          actionParm1.setData("EXT_OWN_AMT", patvct.get(50));
		          actionParm1.setData("COMP_AMT", patvct.get(51));
			      actionParm1.setData("SPEC_NEED_AMT", patvct.get(52));
//			      System.out.println("�߳�==========41"+actionParm1);
			      actionParm.setData("tab4Download_G_J", actionParm1.getData());			  	  
			  	  TParm adm_Confirm = new TParm();
				  adm_Confirm.setData("ADM_SEQ", actionParm1.getData("CONFIRM_NO"));
				  actionParm.setData("adm_Confirm", adm_Confirm.getData());
		          //��ÿ���������˷�����ϸ�;ܸ���Ϣ
			        int dataCount = Integer.parseInt(sr.readLine());
//			        System.out.println("�߳�==========42"+dataCount);
			        TParm actionParm2 = new TParm();
			        for(int j = 0;j < dataCount;j++)
				        {			        	
//			        	 System.out.println("�߳�==========43");
				          //��һ����Ϣ
				          String inf = sr.readLine();
//				          System.out.println("�߳�==========44"+inf);   
				          //��String��Ϣת����Vector
				          Vector infvct=stringToVector(inf);
//				          System.out.println("�߳�==========45"+infvct);   
				          actionParm2.addData("CONFIRM_NO", infvct.get(0));
				          actionParm2.addData("CHARGE_DATE", infvct.get(1));
				          actionParm2.addData("SEQ_NO", infvct.get(2));
				          actionParm2.addData("HOSP_NHI_NO", infvct.get(3));
				          actionParm2.addData("WHYFW_ITEM_SEQ_NO", infvct.get(4));
				          actionParm2.addData("ORDER_DESC", infvct.get(5));
				          actionParm2.addData("OWN_RATE", infvct.get(6));
				          actionParm2.addData("DOSE_CODE", infvct.get(7));
				          actionParm2.addData("STANDARD", infvct.get(8));
				          actionParm2.addData("DEGREE", infvct.get(9));
				          actionParm2.addData("PRICE", infvct.get(10));
				          actionParm2.addData("QTY", infvct.get(11));
				          actionParm2.addData("TOTAL_AMT", infvct.get(12));
				          actionParm2.addData("TOTAL_NHI_AMT", infvct.get(13));
				          actionParm2.addData("OWN_AMT", infvct.get(14));
				          actionParm2.addData("ADDPAY_AMT", infvct.get(15));
				          actionParm2.addData("OP_FLG", infvct.get(16));
				          actionParm2.addData("ADDPAY_FLG", infvct.get(17));
				          actionParm2.addData("NHI_ORD_CLASS_CODE", infvct.get(18));
				          actionParm2.addData("PHAADD_FLG", infvct.get(19));
				          actionParm2.addData("CARRY_FLG", infvct.get(20));
				          actionParm2.addData("REFUSE_AMT", infvct.get(21));
				          actionParm2.addData("REFUSE_CODE", infvct.get(22));
				          actionParm2.addData("REFUSE_DESC", infvct.get(23));
				          actionParm2.addData("PZWH", infvct.get(24));
//				          System.out.println("�߳�==========46"+actionParm2);     
				        }
			        actionParm.setData("tab4Download_I_E", actionParm2.getData());
//			        System.out.println("�߳�==========47"+actionParm); 
			        TParm result = TIOM_AppServer.executeAction(
							"action.ins.INSDownloadPayAction", "saveData", actionParm);
//			        System.out.println("�߳�==========48"+result);
			        if (result.getErrCode() < 0) {
						return false;
					}    
		      }
		      
	    }catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	 
    return true;
  }
  /**
   * @alias ��Stringת����Vector
   * <p>
   * ��Stringת����Vector
   * <p>
   * @param s String
   * @return Vector
   * @author Lizk
   * @version 1.0
   */
  private Vector stringToVector(String s)
  {
    Vector data = new Vector();
    StringTokenizer stk = new StringTokenizer(s,"|");
    while(stk.hasMoreTokens())
      data.add(stk.nextToken().trim());
    return data;
  }
  /**
   * @alias ��װ
   * <p>
   * ��װ�������
   * <p>
   * @param message String
   * @version 1.0
   */
  private void messagebox(String message)
  {
    JOptionPane.showMessageDialog(frame,message);
  }

  /**
   * @alias ���ñ��������
   * <p>Description: ���ñ��������</p>
   * <p>Copyright: Copyright (c) 2006</p>
   * <p>Company: DongYou</p>
   * @version 1.0
   */
  class ClientSocket implements Runnable
  {
    public void run()
    {
      try{
        ServerSocket server = new ServerSocket(8100);
        Socket socket = server.accept();
        BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String s = read.readLine();
        while(s!= null)
        {
          StringTokenizer stk = new StringTokenizer(s,"|");      
          s = read.readLine();
        }
      }catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
