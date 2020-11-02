package com.javahis.ui.mem;


import jdo.mem.MEMMembershipCardTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
/**
*
* <p>Title:��Ա�������趨</p>
*
* <p>Description: ��Ա�������趨</p>
*
* <p>Copyright: Copyright (c) caoyong 20131225/p>
*
* <p>Company: BlueCore</p>
*
* @author caoyong
* @version 1.0
*/
public class MEMMembershipCardControl extends TControl{
	private TTable table;
	private String oper = "";//oper="INSERT"���� oper="UPDATE"�޸�
	
//	private String oldMemCode = "";
//	private String oldValidDays = "";
//	private String oldMemInReason = "";
	
	
	/**
	 * �õ�TABLE����
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	
	  /**
     * ��ʼ��
     */
    public void onInit(){
    	table=this.getTable("TABLE");
    	callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//�����¼�
//    	Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
//		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-','/'));
//		this.setValue("START_DATE", StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/'));
//		this.setValue("VALID_DAYS",getDate());
        this.inPage();
    	
    }
    /**
     * ���
     */
    
    public void onSave(){
    	  TParm parm=new TParm();
    	  TParm result=new TParm();
    	  if("".equals(getValueString("MEM_CODE"))&&getValueString("MEM_CODE")!=null){
    		  this.messageBox("�������Ա�����룡");
    		  this.grabFocus("MEM_CODE");
    		  return;
    	  }
    	  if("".equals(getValueString("MEM_CARD"))&&getValueString("MEM_CARD")!=null){
    		  this.messageBox("�������Ա�����룡");
    		  this.grabFocus("MEM_CARD");
    		  return;
    	  }
    	  if("".equals(getValueString("VALID_DAYS"))&&getValueString("VALID_DAYS")!=null){
    		  this.messageBox("������쿨���ޣ�");
    		  this.grabFocus("VALID_DAYS");
    		  return;
    	  }
    	  if("".equals(getValueString("REASON"))&&getValueString("REASON")!=null){
    		  this.messageBox("������쿨��ʽ��");
    		  this.grabFocus("REASON");
    		  return;
    	  }
    	  //�����Ѳ���Ϊ����
    	  double fee = this.getValueDouble("MEM_FEE");
    	  if(fee<0){
    		  this.messageBox("��Ѳ���Ϊ����");
    		  this.grabFocus("MEM_FEE");
    		  return;
    	  }
    	  //�ж����������޸�
    	  //boolean flag = this.getTextFormat("MEM_CODE").isEnabled();
    	  TTextField memCode = (TTextField)this.getComponent("MEM_CODE");
      	  boolean flag = memCode.isEnabled();
      	  if(flag){
      		  oper = "INSERT";
      	  }else{
      		  oper = "UPDATE";
      	  }
      	  //System.out.println("--------->oper="+oper);
    	  
      	  
    	   //Timestamp startdate = StringTool.getTimestamp(this.getValueString("START_DATE").replace("-",""),"yyyyMMdd");
    	   //Timestamp enddate = StringTool.getTimestamp(this.getValueString("END_DATE").replace("-",""),"yyyyMMdd");
      	   parm.setData("MEM_CODE",this.getValueString("MEM_CODE"));//���
    	   parm.setData("MEM_CARD",this.getValueString("MEM_CARD"));//��Ա������
    	   parm.setData("MEM_DESC",this.getTextFormat("MEM_CARD").getText());//��Ա����
    	   parm.setData("MEM_ENG_DESC", "" );//Ӣ��
    	   parm.setData("PY1", this.getValueString("PY1"));
    	   parm.setData("SEQ", String.valueOf(getMaxSeq()+1) );
    	   parm.setData("MEM_FEE",this.getValueString("MEM_FEE") );
    	   parm.setData("VALID_DAYS",this.getValueString("VALID_DAYS") );
    	   parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION") );
    	   parm.setData("OVERDRAFT","" );
    	   parm.setData("MEM_IN_REASON",this.getValueString("REASON") );
    	   parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
    	   parm.setData("OPT_USER",Operator.getID() );
    	   parm.setData("OPT_TERM",Operator.getIP() );
//    	   parm.setData("OLD_MEM_CODE", oldMemCode );
//    	   parm.setData("OLD_VALID_DAYS", oldValidDays );
//    	   parm.setData("OLD_MEM_IN_REASON", oldMemInReason );
    	   
    	   //System.out.println("parm--------"+parm);
    	   
    	   if("INSERT".equals(oper)){
    		   //�жϸ������Ƿ����
        	   if(!checkMemData(parm)){
        		   this.messageBox("�������Ѵ��ڣ�������������");
        		   return;
        	   }
    		   result= MEMMembershipCardTool.getInstance().insertCard(parm);
			   if(result.getErrCode()<0){
		   		 	this.messageBox("���ʧ��");
		   		 	return ;
		   	   }
		   	   this.messageBox("��ӳɹ�");
		   	   onClear();
		   	   inPage();
    	   	
    	   }else if("UPDATE".equals(oper)){
    		   //�жϸ������Ƿ����
//        	   if(!checkMemData(parm)){
//        		   this.messageBox("�������Ѵ��ڣ��������޸ģ�");
//        		   return;
//        	   }
    		   result=MEMMembershipCardTool.getInstance().updatedata(parm);//�޸�
    		   if(result.getErrCode()<0){
           		 	this.messageBox("�޸�ʧ��");
           		 	return ;
    		   }
    		   this.messageBox("�޸ĳɹ�");
      		   inPage();
    	   }
    	   
    	   /**
    	   TParm sreslt= MEMMembershipCardTool.getInstance().selectCard(this.getValueString("MEM_CODE"));
    	   if(sreslt.getCount()>0){
    		 result=MEMMembershipCardTool.getInstance().updatedata(parm);//�޸�
       		 if(result.getErrCode()<0){
             		 this.messageBox("�޸�ʧ��");
             		 return ;
             }
             		 this.messageBox("�޸ĳɹ�");
             		  inPage();
             		 
    		   
    	   }else{
    	   
    	    result= MEMMembershipCardTool.getInstance().insertCard(parm);
				   if(result.getErrCode()<0){
			   		 this.messageBox("���ʧ��");
			   		 return ;
			   	     }
			   		 this.messageBox("��ӳɹ�");
			   		 onClear();
			   		 inPage();
    	   }  **/
    	   
    	   
    	   
    }
    /**
	 * *���Ӷ�Table�ļ���
	 */
	public void onTABLEClicked(int row){
		
		
	   TParm tparm = table.getParmValue().getRow(row);
	   callFunction("UI|MEM_CODE|setEnabled", false);
	   //callFunction("UI|MEM_CARD|setEnabled", false);
	   this.setValue("MEM_CODE",tparm.getValue("MEM_CODE"));
	   this.setValue("MEM_CARD",tparm.getValue("MEM_CARD"));
	   //this.setValue("MEM_DESC",tparm.getValue("MEM_DESC"));
	   //this.setValue("MEM_ENG_DESC",tparm.getValue("MEM_ENG_DESC") );
	   this.setValue("PY1",tparm.getValue("PY1") );
	   //this.setValue("SEQ",tparm.getValue("SEQ") );
	   this.setValue("MEM_FEE",tparm.getValue("MEM_FEE") );
	   this.setValue("VALID_DAYS",tparm.getValue("VALID_DAYS") );
	   this.setValue("DESCRIPTION",tparm.getValue("DESCRIPTION") );
	   //this.setValue("OVERDRAFT",tparm.getValue("OVERDRAFT") );
	   this.setValue("REASON",tparm.getValue("MEM_IN_REASON") );
	   this.setValue("SEQ",tparm.getValue("SEQ") );
	   
//	   oldMemCode = tparm.getValue("MEM_CARD");
//	   oldValidDays = tparm.getValue("VALID_DAYS");
//	   oldMemInReason = tparm.getValue("MEM_IN_REASON");
	}
//	/**
//	 * �ı��ֹʱ���¼�
//	 */
//    public void onEnddate(){
//    	this.setValue("VALID_DAYS",getDate());
//    }
//    /**
//     * �ı俪ʼʱ���¼�
//     */
//    public void onStartDate(){
//    	this.setValue("VALID_DAYS",getDate());
//    }
    
   /* *//**
	 * ����
	 *//*
	public void onNew() {
		this.onClear();
		String packCodestr = SystemTool.getInstance().getNo("ALL", "ODI",
				"ODIPACK_NO", "ODIPACK_NO");
		this.setValue("MEM_CODE", packCodestr);
	}*/
	/**
	 * ��ʼ��
	 */
	public void inPage(){
		
		
		
		TParm selectCardall=MEMMembershipCardTool.getInstance().selectCardall();
		//this.setValue("SEQ", String.valueOf(getMaxSeq()+1));	//��Ÿ�ֵ
		table.setParmValue(selectCardall);
	}
//	/**
//	 * �����ڲ������
//	 * @return
//	 */
//	public String  getDate(){
//		
//		String starttime=this.getValueString("START_DATE").substring(0,10);
//		String endtime=this.getValueString("END_DATE").substring(0,10);
//		/*if(Integer.parseInt(starttime)-Integer.parseInt(endtime)>0){
//			this.messageBox("��ʼʱ�䲻�����ڽ�ֹʱ��");
//			onInit();
//            return "";
//        }*/
//		String days=getTwoDay(endtime,starttime);
//		return days;
//		
//	}
	/**
	 * ���ƻس��¼�
	 */
	public void onUserPY1() {
		String userName = getValueString("MEM_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
//		((TTextField) getComponent("MEM_ENG_DESC")).grabFocus();
		this.grabFocus("MEM_ENG_DESC");
	}
//	/**
//	 * ���ƻس��¼�
//	 */
//	public void onUserPY2() {
//		String userName = getValueString("MEM_ENG_DESC");
//		String py = TMessage.getPy(userName);
//		setValue("PY2", py);
//		((TTextField) getComponent("PY2")).grabFocus();
//	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		 TParm parm=new TParm();
		 
		 parm.setData("MEM_CARD",this.getValueString("MEM_CARD"));//��Ա����
  	     //parm.setData("MEM_DESC",this.getValueString("MEM_DESC"));//��Ա����
  	     //parm.setData("MEM_ENG_DESC",this.getValueString("MEM_ENG_DESC") );//Ӣ��
		 parm.setData("VALID_DAYS",this.getValueString("VALID_DAYS"));
		 parm.setData("REASON",this.getValueString("REASON"));
		 TParm result=MEMMembershipCardTool.getInstance().selectdata(parm);
		 if(result.getErrCode()<0){
			 this.messageBox("��ѯ����");
			 return;
		 }
		 if(result.getCount()<=0){
			 this.messageBox("û�в�ѯ����");
			 table.removeRowAll();
			 return;
		 }
		 table.setParmValue(result);
	}
	/**
	 * ɾ��
	 */
	public void onDelete(){
		 TParm result =new TParm();
		
		 TParm parm=table.getParmValue();
		 if(parm.getCount()<=0){
			 
			this.messageBox("û�б�������");
			return;
		 }
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox("��ѡ��Ҫɾ��������");
			return ;
		}
		String memCode=table.getItemString(row, "MEM_CODE");
		String sql = "SELECT COUNT(MEM_CODE) COUNT FROM MEM_TRADE WHERE MEM_CODE='"+memCode+"'";
		TParm parmC = new TParm(TJDODBTool.getInstance().select(sql));
		if(parmC.getDouble("COUNT", 0)>0){
			this.messageBox("�û�Ա���������в������򣬲�����ɾ����");
			return;
		}
		
		if (this.messageBox("�Ƿ�ɾ��", "ȷ��Ҫɾ����", 2) == 0) {
		  TParm tparm=table.getParmValue().getRow(row);
		  TParm dparm=new TParm();
		  dparm.setData("MEM_CODE", tparm.getValue("MEM_CODE"));
		  
		  result=MEMMembershipCardTool.getInstance().deletedata(dparm);
		
		  if(result.getErrCode()<0){
			  this.messageBox(" ɾ��ʧ��");
			  return;
		  }
		       this.messageBox("ɾ���ɹ�");
		       onClear();
		       this.inPage();
		  }
	 
	}
	
	
	  /**
	 * ���
	 */
	public void onClear() {
		callFunction("UI|MEM_CODE|setEnabled", true);
		String clearString="MEM_CODE;MEM_CARD;MEM_FEE;VALID_DAYS;SEQ; " +
				           "DESCRIPTION;OVERDRAFT;REASON;PY1";
		clearValue(clearString);
		table.removeRowAll();
		inPage();
		//this.onInit();
		
	}
	
//	/**
//	 * ������֮�������
//	 * @param endtime
//	 * @param starttime
//	 * @return
//	 */
//	  public  String getTwoDay(String endtime, String starttime) {   
//	        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");   
//	        long day = 0;   
//	        try {   
//	         Date date = myFormatter.parse(endtime);   
//	         Date mydate = myFormatter.parse(starttime);   
//	         day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);   
//	        } catch (Exception e) {   
//	         return "";   
//	        }   
//	        return day + "";   
//	    }   
	  /**
		 * ȡ��������ֵ
		 * @return
		 */
		public int getMaxSeq(){
			String sql="SELECT MAX(SEQ) AS SEQ FROM MEM_MEMBERSHIP_INFO ";
			
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			return result.getInt("SEQ",0);
		}
		
		/**
	     * �õ�TextFormat����
	     * @param tagName String
	     * @return TTextFormat
	     */
	    private TTextFormat getTextFormat(String tagName) {
	        return (TTextFormat) getComponent(tagName);
	    }
	    
	    /**
	     * У��������Ƿ����
	     * @return
	     */
	    public boolean checkMemData(TParm parm){
	    	boolean flg = true;
	    	TParm result = new TParm();
	    	result= MEMMembershipCardTool.getInstance().selectMemCard(parm);
	    	if(result.getCount()>0){
	    		flg = false;
	    	}else{
	    		flg = true;
	    	}
	    	return flg;
	    }
	    
	    /**
	     * ��ע����
	     */
	    public void setDesc(){
	    	//boolean flg = false;
	    	if(this.getValueString("MEM_CARD").length() >0 &&
	    			this.getValueString("VALID_DAYS").length() > 0 &&
	    			this.getValueString("REASON").length() > 0){
	    		String memDesc = this.getTextFormat("MEM_CARD").getText();
	    		String reasonDesc = this.getTextFormat("REASON").getText();
	    		String desc = memDesc+reasonDesc+this.getValueString("VALID_DAYS")+"��";
	    		this.setValue("DESCRIPTION", desc);
	    		//��ȡPY1
		        String py =SYSHzpyTool.getInstance().charToCode(desc);
		        this.setValue("PY1", py);
		        //System.out.println("��עƴ��---��"+py);
		        //�������
		        this.setValue("SEQ", String.valueOf(getMaxSeq()+1));
		        
		         
	    	}
	    }
	    
}
