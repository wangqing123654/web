package com.javahis.ui.mem;


import jdo.mem.MEMEcouponTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

public class MEMEcouponControl extends TControl{
	/**
	*
	* <p>Title:�����Ż�ȯ�趨</p>
	*
	* <p>Description: �����Ż�ȯ�趨</p>
	*
	* <p>Copyright: Copyright (c) caoyong 20131225/p>
	*
	* <p>Company: BlueCore</p>
	*
	* @author caoyong
	* @version 1.0
	*/
private TTable table;
	
	
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
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
        this.inPage();
    	
    }
    /**
     * ���
     */
    
    public void onSave(){
    	  TParm parm=new TParm();
    	  TParm result=new TParm();
    	  if("".equals(getValueString("MEM_CODE"))&&getValueString("MEM_CODE")!=null){
    		  this.messageBox("�������Ż�ȯ��");
    		  this.grabFocus("MEM_CODE");
    		  return;
    	  }
    	  
    	   parm.setData("MEM_CODE",this.getValueString("MEM_CODE"));//
    	   parm.setData("MEM_DESC",this.getValueString("MEM_DESC"));
    	   parm.setData("MEM_ENG_DESC",this.getValueString("MEM_ENG_DESC") );//
    	   parm.setData("PY1",this.getValueString("PY1") );
    	   parm.setData("MEM_TYPE",2 );
    	   parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION") );
    	   parm.setData("VALID_DAYS",this.getValueString("VALID_DAYS") );
    	   parm.setData("OPT_DATE",SystemTool.getInstance().getDate());
    	   parm.setData("OPT_USER",Operator.getID() );
    	   parm.setData("OPT_TERM",Operator.getIP() );
    	   TParm sreslt= MEMEcouponTool.getInstance().selectCard(this.getValueString("MEM_CODE"));
    	   if(sreslt.getCount()>0){
    		 result=MEMEcouponTool.getInstance().updatedata(parm);//�޸�
       		 if(result.getErrCode()<0){
             		 this.messageBox("�޸�ʧ��");
             		 return ;
             	     }
             		 this.messageBox("�޸ĳɹ�");
             		  inPage();
             		 
    		   
    	   }else{
    	   
    	    result= MEMEcouponTool.getInstance().insertCard(parm);
				   if(result.getErrCode()<0){
			   		 this.messageBox("���ʧ��");
			   		 return ;
			   	     }
			   		 this.messageBox("��ӳɹ�");
			   		 inPage();
    	   }
    	   
    }
    /**
	 * *���Ӷ�Table�ļ���
	 */
	public void onTABLEClicked(int row){
		
		
	   TParm tparm = table.getParmValue().getRow(row);
	   callFunction("UI|MEM_CODE|setEnabled", false);
	   this.setValue("MEM_CODE",tparm.getValue("MEM_CODE"));
	   this.setValue("MEM_DESC",tparm.getValue("MEM_DESC"));
	   this.setValue("MEM_ENG_DESC",tparm.getValue("MEM_ENG_DESC") );
	   this.setValue("PY1",tparm.getValue("PY1") );
	   this.setValue("DESCRIPTION",tparm.getValue("DESCRIPTION") );
	}
	/**
	 * ��ʼ��
	 */
	public void inPage(){
		
		
		
		TParm selectCardall=MEMEcouponTool.getInstance().selectCardall();
		table.setParmValue(selectCardall);
	}
	/**
	 * ���ƻس��¼�
	 */
	public void onUserPY1() {
		String userName = getValueString("MEM_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	/**
	 * ���ƻس��¼�
	 */
	public void onUserPY2() {
		String userName = getValueString("MEM_ENG_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		 TParm parm=new TParm();
		 
		 parm.setData("MEM_CODE",this.getValueString("MEM_CODE"));//��Ա����
  	     parm.setData("MEM_DESC",this.getValueString("MEM_DESC"));//��Ա����
  	     parm.setData("MEM_ENG_DESC",this.getValueString("MEM_ENG_DESC") );//Ӣ��
		 TParm result=MEMEcouponTool.getInstance().selectdata(parm);
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
		if (this.messageBox("�Ƿ�ɾ��", "ȷ��Ҫɾ����", 2) == 0) {
		  TParm tparm=table.getParmValue().getRow(row);
		  TParm dparm=new TParm();
		  dparm.setData("MEM_CODE", tparm.getValue("MEM_CODE"));
		  result=MEMEcouponTool.getInstance().deletedata(dparm);
		
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
		String clearString="MEM_CODE;MEM_DESC;MEM_ENG_DESC;PY1;MEM_TYPE;DESCRIPTION;VALID_DAYS;";
		clearValue(clearString);
		table.removeRowAll();
		//this.onInit();
		
	}
	
	
	  

}
