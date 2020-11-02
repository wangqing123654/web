package com.javahis.ui.inp;


import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 *
 * <p>Title: �����Ű�</p>
 *
 * <p>Description: �������Ű�</p>
 *
 * <p>Copyright: Copyright (c) caoyong 2013908</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author caoyong
 * @version 1.0
 */
public class INPSchedluControl extends TControl{
	
      private TTable table ;
    
    /**
     * ��ʼ��
     */
    public void onInit(){
    	//ȫ����ѯ
    	 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//�����¼�
    	 //this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,"onROOMTABLEChargeValue");//�س�����
    	 this.setValue("REGION_CODE",Operator.getRegion());
		  TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
	        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
	                getValueString("REGION_CODE")));
         
    	 String sql="SELECT REGION_CODE,ADM_TYPE,DEPT_CODE, " +
    	 		    "DR_CODE1,DR_TEL1,DR_CODE2,DR_TEL2, " +
    	 		    "DR_CODE3,DR_TEL3,DR_CODE4,DR_TEL4, " +
    	 		    "DR_CODE5,DR_TEL5,DR_CODE6,DR_TEL6  " +
    	 		    "FROM INP_SCHDAY ";
         TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
       
		  table=this.getTable("TABLE");
		  table.setParmValue(parm);
		  
    
	}
    
    /**
     * �õ�TABLE����
     * @param tagName
     * @return
     */
     private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
   
   /* *//**
     * �绰�Ÿı��¼�
     *//*
    public boolean onROOMTABLEChargeValue(Object obj) {
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
    	
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        
        //�õ�table
        TTable table = node.getTable();
        //�õ��ı����
        int row = node.getRow();
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        //��������Ƹı���ƴ��1�Զ�����,���ҿ������Ʋ���Ϊ��
        System.out.println("ҽ���ĵ绰����ֵ"+value);
      if ("DR_CODE2".equals(columnName)) {
            
            //Ĭ�ϼ��
            table.setItem(row, "DR_CODE2", value);
            String tel=this.getTle(value);
            table.setItem(row, "DR_TEL2", tel);
            //�����пɱ���
            table.getDataStore().setActive(row, true);

        }
      if ("DR_CODE3".equals(columnName)) {
    	  
    	  //Ĭ�ϼ��
    	  table.setItem(row, "DR_CODE2", value);
    	  String tel=this.getTle(value);
    	  table.setItem(row, "DR_TEL3", tel);
    	  //�����пɱ���
    	  table.getDataStore().setActive(row, true);
    	  
      }
      if ("DR_CODE4".equals(columnName)) {
    	  
    	  //Ĭ�ϼ��
    	  table.setItem(row, "DR_CODE2", value);
    	  String tel=this.getTle(value);
    	  table.setItem(row, "DR_TEL4", tel);
    	  //�����пɱ���
    	  table.getDataStore().setActive(row, true);
    	  
      }
        return false;
    }*/

    /**
     * ����
     */
    public void onSave(){
    	
    	String region_code=this.getValueString("REGION_CODE");
    	
    	if("".equals(region_code)){
    		this.messageBox("������Ϊ��");
    		this.grabFocus("REGION_CODE");
    		return;
    	}
    	String adm_type=this.getValueString("ADM_TYPE");
    	
    	if("".equals(adm_type)){
    		this.messageBox("�ż�ס����Ϊ��");
    		this.grabFocus("ADM_TYPE");
    		return;
    	}
    	String dept_code=this.getValueString("DEPT_CODE");
    	if("".equals(dept_code)){
    		this.messageBox("�Ʊ���Ϊ��");
    		this.grabFocus("DEPT_CODE");
    		return;
    	 }
    	 String dr_code=this.getValueString("DR_CODE1");
    	 if("".equals(dr_code)){
     		this.messageBox("ҽ������Ϊ��");
     		this.grabFocus("DR_CODE1");
     		return;
     	 }
    	 //����֮ǰ��֤�������ݿ����Ƿ��Ѿ�����
    	 String sqlq="SELECT REGION_CODE,ADM_TYPE,DEPT_CODE,DR_CODE1,DR_TEL1,DR_CODE2,DR_TEL2,DR_CODE3,DR_TEL3,DR_CODE4,DR_TEL4 FROM INP_SCHDAY"+
		             " WHERE REGION_CODE='"+getValue("REGION_CODE")+"' AND ADM_TYPE='"+getValue("ADM_TYPE")+"'"+
		             " AND DEPT_CODE='"+getValue("DEPT_CODE")+"' AND DR_CODE1='"+getValue("DR_CODE1")+"'";

         TParm parmq = new TParm(TJDODBTool.getInstance().select(sqlq));
		    if (parmq.getErrCode() < 0) {
			this.messageBox("��ѯ����");
			return;
            }
		   if(parmq.getCount()>0){
			  //this.messageBox("���ݿ����Ѿ����ڴ�����¼");
			//  return ;
			   // int row=table.getSelectedRow();
				//TParm tparm = table.getParmValue().getRow(row);
				
				
				
			   String sqlw="UPDATE INP_SCHDAY SET DR_TEL1='"+this.getValue("DR_TEL1")+"', " +
			   		       "DR_CODE2='"+this.getValue("DR_CODE2")+"',DR_TEL2='"+this.getValue("DR_TEL2")+"', " +
			   		       "DR_CODE3='"+this.getValue("DR_CODE3")+"',DR_TEL3='"+this.getValue("DR_TEL3")+"', " +
			   		       "DR_CODE4='"+this.getValue("DR_CODE4")+"',DR_TEL4='"+this.getValue("DR_TEL4")+"', " +
			   		       "DR_CODE5='"+this.getValue("DR_CODE5")+"',DR_TEL5='"+this.getValue("DR_TEL5")+"', " +
			   		       "DR_CODE6='"+this.getValue("DR_CODE6")+"',DR_TEL6='"+this.getValue("DR_TEL6")+"', " +
			   		       "OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"',OPT_USER='"+Operator.getID()+"' "+
			               "WHERE " +
			               "REGION_CODE='"+this.getValue("REGION_CODE")+"' AND " +
			               "ADM_TYPE='"+this.getValue("ADM_TYPE")+"' AND " +
			               "DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND " +
			               "DR_CODE1='"+this.getValue("DR_CODE1")+"'";
			         
			          TParm dparm = new TParm(TJDODBTool.getInstance().update(sqlw));
			          if(dparm.getErrCode()<0){
			     		 this.messageBox("�޸�ʧ��");
			     		 this.onClear();
			     		 return ;
			     	 }
			     	
			     		 this.messageBox("�޸ĳɹ�");
			     		 this.onClear();
			     	     this.onInit();
		   }else{
			     //String sqlt="SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+this.getValueString("DR_CODE1")+"'";//����Ĭ��ҽ��1�ĵ绰
			   ///  TParm parmt = new TParm(TJDODBTool.getInstance().select(sqlt));
		    	 String sql="INSERT INTO INP_SCHDAY (REGION_CODE,ADM_TYPE,DEPT_CODE," +
		    	 		    "DR_CODE1,DR_TEL1,DR_CODE2,DR_TEL2,DR_CODE3," +
		    	 		    "DR_TEL3,DR_CODE4,DR_TEL4,DR_CODE5,DR_TEL5,DR_CODE6,DR_TEL6,OPT_DATE,OPT_TERM,OPT_USER) "+
		    	            "VALUES('"+region_code+"','"+adm_type+"','"+dept_code+"','"+dr_code+"','"+	this.getValueString("DR_TEL1")+"', " +
		    	 		    "'"+this.getValueString("DR_CODE2")+"','"+this.getValueString("DR_TEL2")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE3")+"','"+this.getValueString("DR_TEL3")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE4")+"','"+this.getValueString("DR_TEL4")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE5")+"','"+this.getValueString("DR_TEL5")+"', "+
		    	 		    "'"+this.getValueString("DR_CODE6")+"','"+this.getValueString("DR_TEL6")+"', "+
		    	            " SYSDATE,'"+Operator.getIP()+"','"+Operator.getID()+"')";
		    	 TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		    	 if(parm.getErrCode()<0){
		    		 this.messageBox("����ʧ��");
		    		 this.onClear();
		    		 return ;
		    	 }
		    	
		    		 this.messageBox("����ɹ�");
		    		 this.onClear();
		    	     this.onInit();
				 }
    	 
	}
    /**
     * �绰�ŵ���¼�
     */
    public void getDtel(){
    	if(!"".equals(getValueString("DR_CODE1"))){//ȡ�õ�ҽ��1�绰
          this.setValue("DR_TEL1", this.getTel(getValueString("DR_CODE1")));
    	}
    	if(!"".equals(getValueString("DR_CODE2"))){//ȡ�õ�ҽ2���绰
    		this.setValue("DR_TEL2", this.getTel(getValueString("DR_CODE2")));
    	}
    	if(!"".equals(getValueString("DR_CODE3"))){//ȡ�õ�ҽ��3�绰
    		this.setValue("DR_TEL3", this.getTel(getValueString("DR_CODE3")));
    	}
    	if(!"".equals(getValueString("DR_CODE4"))){//ȡ�õ�ҽ��4�绰
    		this.setValue("DR_TEL4", this.getTel(getValueString("DR_CODE4")));
    	}
    	if(!"".equals(getValueString("DR_CODE5"))){//ȡ�õ�ҽ��5�绰
    		this.setValue("DR_TEL5", this.getTel(getValueString("DR_CODE5")));
    	}
    	if(!"".equals(getValueString("DR_CODE6"))){//ȡ�õ�ҽ��6�绰
    		this.setValue("DR_TEL6", this.getTel(getValueString("DR_CODE6")));
    	}
    	
    		
    }
    /**
     * ��ѯҽ������Ӧ�ĵ绰����
     */
    public String getTel(String value){
    	
    	if(!"".equals(value)){
    		String sqlt="SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+value+"'";
        	TParm parmt = new TParm(TJDODBTool.getInstance().select(sqlt));
        	return parmt.getValue("TEL1", 0)!=null?parmt.getValue("TEL1", 0):"";
    	}else{
    		return "";
    	}
    	
    	
    }
    /**
     * ��ѯ����
     */
	public void onQuery() {
    	/*if("".equals(getValueString("REGION_CODE"))){
    		this.messageBox("������Ϊ��");
    		this.grabFocus("REGION_CODE");
    		return;
    	}
    	if("".equals(getValueString("ADM_TYPE"))){
    		this.messageBox("�ż�ס����Ϊ��");
    		this.grabFocus("ADM_TYPE");
    		return;
    	}
    	if("".equals(getValueString("DEPT_CODE"))){
    		this.messageBox("�Ʊ���Ϊ��");
    		this.grabFocus("DEPT_CODE");
    		return;
    	 }
    	 if("".equals(getValueString("DR_CODE1"))){
     		this.messageBox("ҽ������Ϊ��");
     		this.grabFocus("DR_CODE1");
     		return;
     	 }*/
    	 
		String sql="SELECT REGION_CODE, ADM_TYPE, DEPT_CODE, DR_CODE1,DR_TEL1,DR_CODE2,"+
    	           "DR_TEL2,DR_CODE3,DR_TEL3,DR_CODE4,DR_TEL4,DR_CODE5,DR_TEL5,DR_CODE6,DR_TEL6 "+
    	           "FROM INP_SCHDAY "+
    	           "WHERE REGION_CODE='"+getValue("REGION_CODE")+"' ";
		
		if(!"".equals(getValueString("ADM_TYPE"))){
			sql+="AND ADM_TYPE='"+getValue("ADM_TYPE")+"' ";
		}
		
		if(!"".equals(getValueString("DEPT_CODE"))){
			sql+="AND DEPT_CODE='"+getValue("DEPT_CODE")+"' ";
		}
		
		if(!"".equals(getValueString("DR_CODE1"))){
			sql+="AND DR_CODE1='"+getValue("DR_CODE1")+"'";
		}
		           TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				    if (parm.getErrCode() < 0) {
					this.messageBox("��ѯ����");
					return;
		           }
				   if(parm.getCount()<=0){
					   this.messageBox("û�в�ѯ����");
					   this.onClear();
					   return ;
				   }
				      table=this.getTable("TABLE");
				      table.setParmValue(parm);
	}
	/**
	 * *���Ӷ�Table�ļ���
	 */
	public void onTABLEClicked(int row){
	 TParm tparm = table.getParmValue().getRow(row);
	  this.setValue("REGION_CODE", tparm.getValue("REGION_CODE"));
	  this.setValue("ADM_TYPE",  tparm.getValue("ADM_TYPE"));
	  this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
	  this.setValue("DR_CODE1",  tparm.getValue("DR_CODE1"));
	  this.setValue("DR_TEL1",   !"".equals(tparm.getValue("DR_TEL1"))?tparm.getValue("DR_TEL1"):"");
	  this.setValue("DR_CODE2",  tparm.getValue("DR_CODE2"));
	  this.setValue("DR_TEL2",   !"".equals(tparm.getValue("DR_TEL2"))?tparm.getValue("DR_TEL2"):"");
	  this.setValue("DR_CODE3",  tparm.getValue("DR_CODE3"));
	  this.setValue("DR_TEL3",   !"".equals(tparm.getValue("DR_TEL3"))?tparm.getValue("DR_TEL3"):"");
	  this.setValue("DR_CODE4",  tparm.getValue("DR_CODE4"));
	  this.setValue("DR_TEL4",   !"".equals(tparm.getValue("DR_TEL4"))?tparm.getValue("DR_TEL4"):"");
	  this.setValue("DR_CODE5",  tparm.getValue("DR_CODE5"));
	  this.setValue("DR_TEL5",   !"".equals(tparm.getValue("DR_TEL5"))?tparm.getValue("DR_TEL5"):"");
	  this.setValue("DR_CODE6",  tparm.getValue("DR_CODE6"));
	  this.setValue("DR_TEL6",   !"".equals(tparm.getValue("DR_TEL6"))?tparm.getValue("DR_TEL6"):"");
	  this.onLock(false);
	}
	/**
	 * ���ؼ�
	 */
	public void onLock(boolean flag){
		    callFunction("UI|REGION_CODE|setEnabled", flag);//������
			this.grabFocus("REGION_CODE");
		    callFunction("UI|ADM_TYPE|setEnabled", flag);//���ż�ס��
			this.grabFocus("ADM_TYPE");
			callFunction("UI|DEPT_CODE|setEnabled", flag);//������
			this.grabFocus("DEPT_CODE");
			callFunction("UI|DR_CODE1|setEnabled", flag);//��ҽ��1
			this.grabFocus("DR_CODE1");
	}
	/**
	 * ���
	 */
	public void onClear() {
		String clearString="REGION_CODE;ADM_TYPE;DEPT_CODE;DR_CODE1;" +
				            "DR_TEL1;DR_CODE2;DR_TEL2;DR_CODE3;DR_TEL3;DR_CODE4;DR_TEL4;DR_CODE5;DR_TEL5;DR_CODE6;DR_TEL6;";
		clearValue(clearString);
		table.removeRowAll();
		this.setValue("REGION_CODE",Operator.getRegion());
		this.onLock(true);
	}
	/**
	 * ɾ������
	 */
	
	public void onDelete(){
		TParm parmM = table.getParmValue();
		if (parmM.getCount() <= 0) {
			this.messageBox("û�б��������");
			return;
		}
		int row=table.getSelectedRow();
		
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;
		}
		if (this.messageBox("�Ƿ�ɾ��", "ȷ��Ҫɾ����", 2) == 0) {
			TParm tparm = table.getParmValue().getRow(row);
		    String sql="DELETE  FROM INP_SCHDAY "+
		               "WHERE REGION_CODE='"+tparm.getValue("REGION_CODE")+"' AND ADM_TYPE='"+tparm.getValue("ADM_TYPE")+"' "+
		               "AND DEPT_CODE='"+tparm.getValue("DEPT_CODE")+"' AND DR_CODE1='"+tparm.getValue("DR_CODE1")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		    if(parm.getErrCode()<0){
		    this.messageBox("ɾ��ʧ��");
		    
		    return;
		    }
		    this.messageBox("ɾ���ɹ�");
		    this.onClear();
		    onInit();
	}
	}
}
