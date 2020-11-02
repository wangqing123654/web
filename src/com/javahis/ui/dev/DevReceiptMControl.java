package com.javahis.ui.dev;

import com.dongyang.control.*;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import java.util.Date;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox; 

import java.awt.Component;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer; 
import jdo.sys.SystemTool;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import jdo.dev.DevReceiptDDataStore;
import com.dongyang.ui.TTextArea;

/**
 * <p>Title: ���չ���</p>
 *
 * <p>Description: ���չ������յ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0 
 */
public class DevReceiptMControl extends TControl {
   /**
    * ����������
    */ 
   private String actionName = "action.dev.DevAction";
   /**
    * ��
    */
   private static String TABLE1="TABLE1"; 
   /**
    * ϸ                
    */
   private static String TABLE2="TABLE2";
   /**
    * �빺����
    */
   private String rateofproCode="";
   /**
    * ��ʼ������
    */
   public void onInit(){
       /**
        * ��ʼ��ҳ��
        */
       onInitPage();
       /**
        * ��ʼ���¼�
        */
       initEven();
   }
   /**
    * ��ʼ��ҳ��
    */
   public void onInitPage(){ 
       //���ҡ���Ա
       this.setValue("QRECEIPT_DEPT",Operator.getDept());
       this.setValue("QRECEIPT_USER",Operator.getID()); 
       this.setValue("RECEIPT_DEPT",Operator.getDept());
       this.setValue("RECEIPT_USER",Operator.getID());
       //����
       Timestamp startDate = StringTool.getTimestamp(StringTool.getString(StringTool.getTimestamp(new Date()),"yyyy/MM/dd"),"yyyy/MM/dd");
       //����
       Timestamp endDate = StringTool.getTimestamp("9999/12/31","yyyy/MM/dd");
       //��ѯ������������
       this.setValue("QSTART_DATE",startDate);
       //��ѯ������������
       this.setValue("QEND_DATE",endDate);
       //��������
       this.setValue("RECEIPT_DATE",startDate);
       //Ԥ������������
       this.setValue("QUSERSTART_DATE",startDate);
       //Ԥ������������ 
       this.setValue("QUSEEND_DATE",endDate); 
       //Ԥ��������
       this.setValue("INVOICE_DATE",startDate);
       //��ʼ��TABLE1
       this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
       this.getTTable(TABLE1).setDSValue();
       //��ʼ��TABLE2
       this.getTTable(TABLE2).setDataStore(getTableTDataStore("TABLE2"));
       this.getTTable(TABLE2).setDSValue();
       //���һ�� 
       insertRow(TABLE2);  
       this.getTTable(TABLE2).setDSValue(); 
   }
   
   /**
    * ��TABLE�����༭�ؼ�ʱ��ʱ 
    * @param com Component
    * @param row int
    * @param column int
    */
   public void onChange(Component com,int row,int column){
       //�豸����
       String receiptQty = this.getTTable(TABLE2).getDataStore().getItemString(row,"RECEIPT_QTY");
       //״̬����ʾ
       callFunction("UI|setSysStatus","");
       //�õ�����
       String columnName = this.getFactColumnName(TABLE2,column);
       if(!(com instanceof TTextField))
           return;
       TTextField textFilter = (TTextField)com;
       textFilter.onInit();

       TParm parm = new TParm();
       parm.setData("RECEIPT_QTY",receiptQty);      
    
   }
   
   /**  
    * ���һ��
    * @param tag String
    * @return boolean
    */
   public int insertRow(String tag){
       int rowNum = -1;
       boolean falg = true;
       TTable table = this.getTTable(tag);
       TDataStore tabDataStore= table.getDataStore();
       int rowCount = tabDataStore.rowCount();
       for(int i=0;i<rowCount;i++){
           if(!tabDataStore.isActive(i)){
               falg = false;
               break;
           } 
       }
       if(falg){
           rowNum = tabDataStore.insertRow();
           tabDataStore.setActive(rowNum,false);
       }
       return rowNum;
   }
   
   /**
    * �����豸������ϸ������
    */
   public void addDRow(){   
	   //DEVPRO_CODE;DEV_CHN_DESC;SPECIFICATION;RECEIPT_QTY;SUM_QTY;QTY;UNIT_PRICE;XJ;REMARK
       String column = "DEVPRO_CODE;DEV_CHN_DESC;SPECIFICATION;RECEIPT_QTY;SUM_QTY;" +
       		           "QTY;UNIT_PRICE;XJ;REMARK";  
       String stringMap[] = StringTool.parseLine(column,";");
       TParm tableDParm = new TParm();  
       for(int i = 0;i<stringMap.length;i++){  
          if(stringMap[i].equals("DEVPRO_CODE"))    
             tableDParm.setData(stringMap[i],"A"); 
           else    
             tableDParm.setData(stringMap[i],"");        
          }  
       ((TTable)getComponent("TABLE2")).addRow(tableDParm);  
   } 
   
   
   
   /**
    * �¼���ʼ�� 
    */
   public void initEven(){
	   //TABLE2���¼�
       getTTable("TABLE2").addEventListener(
      		 TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
       //����TABLE1�����¼�
       callFunction("UI|" + TABLE1 + "|addEventListener",
                   TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClicked");
       //ϸ��TABLE2ֵ�ı����
       addEventListener(TABLE2+"->"+TTableEvent.CHANGE_VALUE, this,
                                  "onChangeTableValue");
       //TABLE2�س��¼�
       callFunction("UI|RECEIPT_QTY|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChange");
       //ϸ��TABLE2�����¼�  
       getTTable(TABLE2).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                              "");
       getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
       } 
   /**
    * ��TABLE�����༭�ؼ�ʱ��ʱ
    * @param com Component
    * @param row int 
    * @param column int
    */
   public void onCreateEditComoponent(Component com,int row,int column){
       //�豸����
       String devProCode = this.getTTable(TABLE2).getDataStore().getItemString(row,"DEVPRO_CODE");
       //״̬����ʾ
       callFunction("UI|setSysStatus","");
       //�õ�����
       String columnName = this.getFactColumnName(TABLE2,column);
       if(!"DEV_CHN_DESC".equals(columnName))
           return;
       if(!(com instanceof TTextField))
           return;
       TTextField textFilter = (TTextField)com;
       textFilter.onInit();
       TParm parm = new TParm();
       parm.setData("DEVPRO_CODE",devProCode);
       parm.setData("ACTIVE","Y");      
       //���õ����˵�
       textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
       //������ܷ���ֵ����
       textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
   }
     
   /**      
    * ���ܷ���ֵ����
    * @param tag String
    * @param obj Object 
    */
   public void popReturn(String tag,Object obj){
       //�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
       if (obj == null &&!(obj instanceof TParm)) {
           return ;
       }
       //����ת����TParm
       TParm action = (TParm)obj;
       callFunction("UI|setSysStatus",action.getValue("DEV_CODE")+":"+action.getValue("DEV_CHN_DESC")+action.getValue("SPECIFICATION"));

       this.getTTable(TABLE2).acceptText();
       int rowNum = this.getTTable(TABLE2).getSelectedRow();
       //�ж��Ƿ��������µ���Ŀ
       if((rateofproCode.equals("C")||rateofproCode.equals("D")||rateofproCode.equals("E"))){
           this.getTTable(TABLE2).getDataStore().setItem(rowNum,"DEV_CHN_DESC","");
           this.getTTable(TABLE2).setDSValue(rowNum);
           return; 
       }
       //�ж��Ƿ����ظ���
       if(this.isRepeatItem(action.getValue("DEV_CODE"),rowNum)){
           this.messageBox("����������ͬ�豸���������豸���޸�������");
           this.getTTable(TABLE2).getDataStore().setItem(rowNum,"DEV_CHN_DESC","");
           this.getTTable(TABLE2).setDSValue(rowNum);
           return;

       } 
       String columnArr[] = this.getTTable(TABLE2).getDataStore().getColumns();
       for(String temp:columnArr){
           if(action.getValue(temp).length()==0)
               continue;
           if("OPT_DATE".equals(temp))
               continue;
           if("OPT_USER".equals(temp))
               continue;
           if("OPT_TERM".equals(temp))
               continue; 
           this.getTTable(TABLE2).getDataStore().setItem(rowNum,temp,action.getData(temp));
       }
       this.getTTable(TABLE2).getDataStore().setActive(rowNum,true);  
       //fux modify 20130728 �������
       String devCode = action.getValue("DEV_CODE");
       TParm devBase = onDevBase(devCode);
       if(devBase.getCount()<=0){
    	   this.insertRow(TABLE2); 
           this.getTTable(TABLE2).setDSValue();
           this.getTTable(TABLE2).getTable().grabFocus();
           this.getTTable(TABLE2).setSelectedRow(rowNum);
           this.getTTable(TABLE2).setSelectedColumn(4); 
       }  
       else{  
    	     //��������������
    	     messageBox("��Ӹ���"); 
    	       this.insertRow(TABLE2);  
    	       this.getTTable(TABLE2).setDSValue(); 
    	       this.getTTable(TABLE2).getTable().grabFocus(); 
    	       this.getTTable(TABLE2).setSelectedRow(rowNum);
    	       this.getTTable(TABLE2).setSelectedColumn(4); 
    	     //������������parm 
    	     TParm tableParmSet = new TParm();  
             for(int i=0;i<devBase.getCount();i++){
            	 //addData������  setData���ַ���
      		 //parmStockM = getDevStock(devSetCode,this.getValueString("REQUESTOUT_DEPT"));
      		 //DEVPRO_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;SUM_QTY;RECEIPT_QTY;UNIT_PRICE;XJ;REMARK 
      	     tableParmSet.setData("DEVPRO_CODE",devBase.getData("DEVPRO_CODE",i)); 
      	     tableParmSet.setData("DEV_CODE",devBase.getData("DEV_CODE",i));
      	     tableParmSet.setData("DEV_CHN_DESC",devBase.getData("DEV_CHN_DESC",i));
      	     tableParmSet.setData("SPECIFICATION",devBase.getData("SPECIFICATION",i)); 
      	     tableParmSet.setData("QTY","0" );       
      	     tableParmSet.setData("SUM_QTY","0");          
      	     tableParmSet.setData("RECEIPT_QTY","0");    
      	     tableParmSet.setData("UNIT_PRICE",devBase.getData("UNIT_PRICE",i));  
      	     tableParmSet.setData("XJ",0);      
      	     tableParmSet.setData("REMARK",devBase.getData("DESCRIPTION",i));   
    	     //System.out.println("tableParmSet"+tableParmSet);    
    	       for(String temp:columnArr){ 
    	           if(tableParmSet.getValue(temp).length()==0)
    	               continue;
    	           if("OPT_DATE".equals(temp))
    	               continue;  
    	           if("OPT_USER".equals(temp))
    	               continue;
    	           if("OPT_TERM".equals(temp)) 
    	               continue; 
    	           this.getTTable(TABLE2).getDataStore().setItem(rowNum+i+1,temp,tableParmSet.getData(temp));
    	       } 
    	       this.getTTable(TABLE2).getDataStore().setActive(rowNum+i+1,true); 
    	       this.insertRow(TABLE2);  
    	       this.getTTable(TABLE2).setDSValue();   
    	       this.getTTable(TABLE2).getTable().grabFocus(); 
    	       this.getTTable(TABLE2).setSelectedRow(rowNum+i+1);
    	       this.getTTable(TABLE2).setSelectedColumn(4);  
           }       
       }
         
   }  
 
   /**           
    * ��ѯ��������DEV_BASE         
    * @return boolean
    */ 
   public TParm onDevBase(String devCode){
	   String sql = " SELECT * FROM DEV_BASE" +
	   		        " WHERE SETDEV_CODE = '"+devCode+"'"; 
	   TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	   return parm;
   } 
   
   /**            
    * �Ƿ����ظ���         
    * @return boolean
    */  
   public boolean isRepeatItem(String devOrder,int selCount){
       boolean falg = false;   
       TDataStore dataStore = this.getTTable(TABLE2).getDataStore();
       int rowCount = dataStore.rowCount();
       for(int i=0;i<rowCount;i++){
           if(!dataStore.isActive(i))
               continue;
           if(i==selCount)
               continue;
           if(devOrder.equals(dataStore.getItemString(i,"DEV_CODE")))
               falg = true;
       }
       return falg;
   } 
   
   
   /**
    * ��ʱҽ���޸��¼�����       
    * @param obj Object
    */
   public boolean onChangeTableValue(Object obj) {
       //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
       TTableNode node = (TTableNode) obj;
       if (node == null) 
           return true;
       //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
       if (node.getValue().equals(node.getOldValue()))
           return true;
       //�õ�table�ϵ�parmmap������  
       String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
       if ("UNIT_PRICE".equals(columnName)) {
           node.getTable().getDataStore().setItem(node.getRow(),"UNIT_PRICE",node.getValue());
           node.getTable().setDSValue(node.getRow());
       }    
       //fux need modify 20130808
       if ("QTY".equals(columnName)) { 
     	   double XJAmt = this.getXJAmt(node.getTable().getDataStore());  
           node.getTable().getDataStore().setItem(node.getRow(),"XJ",XJAmt);
           node.getTable().setDSValue(node.getRow());
       }   
       //ȥ��������С�ڶ�������У�飬ֱ�ӿ�������
       if ("RECEIPT_QTY".equals(columnName)) {
           TParm rowParm = node.getTable().getDataStore().getRowParm(node.getRow());
           //������>���������򱨴� 
//           if(Integer.parseInt(node.getValue().toString())>rowParm.getInt("QTY")-rowParm.getInt("SUM_QTY")||
//        		Integer.parseInt(node.getValue().toString())<0){ 
             if(Integer.parseInt(node.getValue().toString())>rowParm.getInt("QTY")||
            	Integer.parseInt(node.getValue().toString())<0){
               this.messageBox("����������������������룡");
               return true;
           } 
           node.getTable().getDataStore().setItem(node.getRow(),"RECEIPT_QTY",node.getValue());
           node.getTable().setDSValue(node.getRow());
       } 
       double totAmt = this.getXJAmt(node.getTable().getDataStore());
       this.setValue("INVOICE_AMT",totAmt);
       return false;  
   }
   
   /**
    * ����¼� 
    */
   public void onTableClicked(int row){       
       TParm parm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
       callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
       String purorderNo = parm.getValue("PURORDER_NO");
       TParm purParm = new TParm(this.getDBTool().select("SELECT RATEOFPRO_CODE,PAYMENT_TERMS FROM DEV_PURORDERM WHERE PURORDER_NO='"+purorderNo+"'"));
       //�빺���� 
       rateofproCode = purParm.getValue("RATEOFPRO_CODE",0);
       this.setValue("PURORDER_NO",parm.getData("PURORDER_NO"));
       this.setValue("RECEIPT_NO",parm.getData("RECEIPT_NO"));
       this.setValue("RECEIPT_DEPT", parm.getData("RECEIPT_DEPT"));
       this.setValue("RECEIPT_USER", parm.getData("RECEIPT_USER"));
       this.setValue("PURORDER_DATE", parm.getData("PURORDER_DATE"));
       this.setValue("RECEIPT_DATE", parm.getData("RECEIPT_DATE"));
       this.setValue("RES_DELIVERY_DATE", parm.getData("RES_DELIVERY_DATE"));
       this.setValue("INVOICE_AMT", parm.getData("INVOICE_AMT"));
       this.setValue("SUP_CODE", parm.getData("SUP_CODE"));
       this.setValue("INVOICE_NO", parm.getData("INVOICE_NO"));
       this.setValue("INVOICE_DATE", parm.getData("INVOICE_DATE"));
       this.setValue("RECEIPT_MINUTE", parm.getData("RECEIPT_MINUTE"));
       this.setValue("PAYMENT_TERMS",purParm.getValue("PAYMENT_TERMS",0));
       this.setValue("REMARK",parm.getData("REMARK"));
       //�ж��Ƿ��������µ���Ŀ
       if(rateofproCode.equals("E")){
           this.getTTextFormat("RECEIPT_DEPT").setEnabled(false);
           this.getTTextFormat("RECEIPT_USER").setEnabled(false);
           this.getTTextFormat("RECEIPT_DATE").setEnabled(false);
           this.getTNumberTextField("INVOICE_AMT").setEnabled(false);
           this.getTTextFormat("SUP_CODE").setEnabled(false);
           this.getTTextArea("RECEIPT_MINUTE").setEnabled(false);
           this.getTTextField("PAYMENT_TERMS").setEnabled(false);
           this.getTNumberTextField("INVOICE_AMT").setEnabled(false);
           this.getTTextField("INVOICE_NO").setEnabled(false);
           this.getTTextFormat("INVOICE_DATE").setEnabled(false);
       }else{  
           this.getTTextFormat("RECEIPT_DEPT").setEnabled(true);
           this.getTTextFormat("RECEIPT_USER").setEnabled(true);
           this.getTTextFormat("RECEIPT_DATE").setEnabled(true);
           this.getTNumberTextField("INVOICE_AMT").setEnabled(true);
           this.getTTextFormat("SUP_CODE").setEnabled(true);
           this.getTTextArea("RECEIPT_MINUTE").setEnabled(true);
           this.getTTextField("PAYMENT_TERMS").setEnabled(true);
           this.getTNumberTextField("INVOICE_AMT").setEnabled(true);
           this.getTTextField("INVOICE_NO").setEnabled(true);
           this.getTTextFormat("INVOICE_DATE").setEnabled(true);
       }
       //��ʼ��TABLE2
       this.getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("RECEIPT_NO")));
       this.getTTable(TABLE2).setDSValue();
       if(rateofproCode.equals("E")){
           this.getTTable(TABLE2).setLockColumns("all");
       }else{
           this.getTTable(TABLE2).setLockColumns("0,1,2,4,5,7");
       }
   }
      
   /**
    * ɨ�踳ֵ�¼�
    * fux modify
    * @param tag String
    * @return void     
    */
   public void onRead(){
	   TParm parm = new TParm();//��ɨ�赥�ϵõ�����
	   this.setValue("RECEIPT_NO",parm.getValue("RECEIPT_NO"));//��Ӧ����
	   this.setValue("RECEIPT_DATE",parm.getValue("RECEIPT_DATE"));//��������
	   this.setValue("PURORDER_NO",parm.getValue("PURORDER_NO"));//��������
	   this.setValue("SUP_CODE",parm.getValue("SUP_CODE"));//��Ӧ����   
	   this.setValue("SUP_SALES1",parm.getValue("SUP_SALES1"));//������Ա
	   this.setValue("SUP_SALES1_TEL",parm.getValue("SUP_SALES1_TEL"));//����绰
	   this.setValue("PAYMENT_TERMS",parm.getValue("PAYMENT_TERMS"));//��������
	   this.setValue("REMARK",parm.getValue("REMARK"));//��ע
	   this.setValue("RECEIPT_DEPT",parm.getValue("RECEIPT_DEPT"));//���տ���
	   this.setValue("RECEIPT_USER",parm.getValue("RECEIPT_USER"));//������Ա
	   this.setValue("INVOICE_AMT",parm.getValue("INVOICE_AMT"));//��Ʊ���
	   this.setValue("INVOICE_NO",parm.getValue("INVOICE_NO"));//��Ʊ����
	   this.setValue("RECEIPT_MINUTE",parm.getValue("RECEIPT_MINUTE"));//���ռ�¼
	   this.setValue("INVOICE_DATE",parm.getValue("INVOICE_DATE"));//��Ʊ����
   }
   
   /**
    * �õ����ֿؼ�
    * @param tag String
    * @return TNumberTextField
    */
   public TNumberTextField getTNumberTextField(String tag){
       return (TNumberTextField)this.getComponent(tag);
   }
   /**
    * �õ�����֮ǰ���к�
    * @param column int
    * @return int
    */
   public int getThisColumnIndex(int column){
       return this.getTTable(TABLE2).getColumnModel().getColumnIndex(column);
   }

   /**
    * ����ʵ������
    * @param column String
    * @param column int
    * @return String
    */
   public String getFactColumnName(String tableTag,int column){
       int col = this.getThisColumnIndex(column);
       return this.getTTable(tableTag).getDataStoreColumnName(col);
   }
   /**
    * ����
    * @return boolean
    */
   public boolean onSave(){  
	   //����һ���ۼ�������+String receiptQty = this.getTTable(TABLE2).getDataStore().getItemString(row,"RECEIPT_QTY");
       //���ϸ��   	    
       TParm checkParm = isCheckMItem();
       if(checkParm.getErrCode()<0){
           this.messageBox(checkParm.getErrText());
           return false; 
       }    
       //����
       if(this.getValueString("RECEIPT_NO").length()==0){
    	   if(this.getValueString("SUP_CODE").length()==0){
    		   this.messageBox("����д��Ӧ���̣�");
    		  return false; 
    	   } 
           //������� 
    	   //fux modify 20130728  ȥ�� У�鶩�����źͷ�Ʊ��
//           if (!emptyTextCheck("PURORDER_NO,INVOICE_NO"))
//               return false;
           //���յ���               
           String receiptNo = SystemTool.getInstance().getNo("ALL", "DEV",
               "RECEIPT_NO", "RECEIPT_NO"); 
           //��������
           TParm purorderMParm = this.getPurOrderM(receiptNo);
           String sqlRequestM[] = new String[]{this.creatRequestSQL(purorderMParm,"INSERT")};
           for(String temp:sqlRequestM){
           //    System.out.println("temp:"+temp);
           }
           //����ϸ�� 
           Timestamp timestamp = StringTool.getTimestamp(new Date());
           TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
           int rowCount = dateStore.rowCount();
           int seqNo = 1;
           //DEVPRO_CODE;DEV_CHN_DESC;SPECIFICATION;RECEIPT_QTY;SUM_QTY;QTY;
           //UNIT_PRICE;XJ;REMARK
           //fux modify 20130710 ����SUM_QTY�ۼ�����������
           for(int i=0;i<rowCount;i++){  
        	   String Qty = this.getTTable(TABLE2).getDataStore().getItemString(i,"QTY");
        	   String sumQty = this.getTTable(TABLE2).getDataStore().getItemString(i,"SUM_QTY");
               if(!dateStore.isActive(i))
                   continue;   
               dateStore.setItem(i,"RECEIPT_NO",receiptNo); 
               dateStore.setItem(i,"SUM_QTY",Qty);    
               dateStore.setItem(i,"SEQ_NO",seqNo);     
               dateStore.setItem(i,"OPT_USER",Operator.getID());
               dateStore.setItem(i,"OPT_DATE",timestamp);
               dateStore.setItem(i,"OPT_TERM",Operator.getIP());
               //δ��� 
               dateStore.setItem(i,"FINAL_FLG","0");    
               seqNo++; 
           }  
           sqlRequestM = StringUtil.getInstance().copyArray(dateStore.getUpdateSQL(),sqlRequestM);
           for(String sql:sqlRequestM){
           //    System.out.println("sql:"+sql);
           } 
           TParm otherParm = this.getOtherData(this.getValueString("PURORDER_NO"));
           String[] sqlPurOrder = new String[]{"UPDATE DEV_PURORDERM SET RATEOFPRO_CODE='D',CHK_USER='"+Operator.getID()+"',CHK_DATE=SYSDATE WHERE PURORDER_NO='"+this.getValueString("PURORDER_NO")+"'"};
           sqlRequestM = StringUtil.getInstance().copyArray(sqlPurOrder,sqlRequestM);
           if(!otherParm.getValue("REQUEST_NO").equals("FALSE")){
               String[] sqlRequest = new String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='D' WHERE REQUEST_NO='"+otherParm.getValue("REQUEST_NO")+"'"};
               sqlRequestM = StringUtil.getInstance().copyArray(sqlRequest,sqlRequestM);
           } 
           TParm sqlParm = new TParm();
           sqlParm.setData("SQL",sqlRequestM);
           TParm actionParm = TIOM_AppServer.executeAction(actionName,
              "saveDevRequest", sqlParm);  
           //System.out.println("actionParm"+actionParm);
          if(actionParm.getErrCode()<0){
              this.messageBox("����ʧ�ܣ�");
              return false;
          }   
          this.messageBox("����ɹ���"); 
          this.setValue("RECEIPT_NO",receiptNo);
       }else{
           //������� 
           if ( (rateofproCode.equals("E"))) {
               this.messageBox("����״̬�������޸ģ�");
               return false;
           }
           this.getTTable(TABLE2).acceptText();
           //�빺����
           TParm requestMParm = this.getPurOrderM(this.getValueString("RECEIPT_NO"));
           String sqlRequestM[] = new String[]{this.creatRequestSQL(requestMParm,"UPDATE")};
           for(String temp:sqlRequestM){
           //    System.out.println("temp:"+temp);
           } 
           //�빺ϸ�� 
           Timestamp timestamp = StringTool.getTimestamp(new Date());
           TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
           int newRow[] = dateStore.getNewRows();
           int rowCount = dateStore.rowCount(); 
           int seqNo = dateStore.rowCount()-newRow.length;
           for(int row:newRow){ 
        	   String receiptQty = this.getTTable(TABLE2).getDataStore().getItemString(row,"RECEIPT_QTY");
        	   String sumQty = this.getTTable(TABLE2).getDataStore().getItemString(row,"SUM_QTY");
               if(!dateStore.isActive(row))
                   continue; 
               dateStore.setItem(row,"RECEIPT_NO",this.getValueString("RECEIPT_NO"));
               dateStore.setItem(row,"SUM_QTY",receiptQty);
               dateStore.setItem(row,"SEQ_NO",seqNo);
               dateStore.setItem(row,"OPT_USER",Operator.getID());
               dateStore.setItem(row,"OPT_DATE",timestamp);
               dateStore.setItem(row,"OPT_TERM",Operator.getIP());
               seqNo++;

           }
           //ɾ��
           for(int i=rowCount-1;i>=0;i--){
               if(!dateStore.isActive(i)&&dateStore.getItemString(i,"RECEIPT_NO").length()!=0){
                   dateStore.setActive(i,true);
                   dateStore.deleteRow(i);
               }
           }
           sqlRequestM = StringUtil.getInstance().copyArray(dateStore.getUpdateSQL(),sqlRequestM);
           for(String sql:sqlRequestM){
           //    System.out.println("sql:"+sql);
           }
           TParm sqlParm = new TParm();
           sqlParm.setData("SQL",sqlRequestM);
           TParm actionParm = TIOM_AppServer.executeAction(actionName,
              "saveDevRequest", sqlParm);
          if(actionParm.getErrCode()<0){
              this.messageBox("����ʧ�ܣ�");
              return false; 
          } 
          this.messageBox("����ɹ���");
       }
       this.onClear();
       this.onQuery();
       return true;
   }

   /**
    * ��Ӧ���������¼�
    */
   public void onSupCodeChick() {
       String supCode = this.getValueString("SUP_CODE");
       TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1,SUP_SALES1_TEL,SUP_SALES1_EMAIL,ADDRESS FROM SYS_SUPPLIER WHERE SUP_CODE='" +
           supCode + "'"));
       if (parm.getCount() < 0)
           return;
       this.setValue("SUP_SALES1", parm.getData("SUP_SALES1", 0));
       this.setValue("SUP_SALES1_TEL", parm.getData("SUP_SALES1_TEL", 0));
   }
    /**
     * �������ݿ�������� 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
   /**
    * �õ��빺��������
    * @return TParm 
    */
   public TParm getPurOrderM(String receiptNo){
       TParm result = new TParm();
       //���յ���
       result.setData("RECEIPT_NO",receiptNo);
       //�������� 
       result.setData("PURORDER_NO",this.getValueString("PURORDER_NO"));
       //��������
       result.setData("RECEIPT_DATE",this.getValue("RECEIPT_DATE"));
       //���տ���
       result.setData("RECEIPT_DEPT",this.getValue("RECEIPT_DEPT"));
       //������Ա
       result.setData("RECEIPT_USER",this.getValue("RECEIPT_USER"));
       //��Ʊ����
       result.setData("INVOICE_DATE",this.getValue("INVOICE_DATE"));
       //��Ʊ��� 
       result.setData("INVOICE_AMT",this.getValue("INVOICE_AMT"));
       //��Ʊ����
       result.setData("INVOICE_NO",this.getValue("INVOICE_NO"));
       //��Ӧ���� 
       result.setData("SUP_CODE",this.getValue("SUP_CODE"));
       //���ռ�¼  
       result.setData("RECEIPT_MINUTE",this.getValue("RECEIPT_MINUTE"));
//       //�������� 
//       TParm temp = getOtherData(this.getValueString("PURORDER_NO"));
//       result.setData("PURORDER_DATE",temp.getData("PURORDER_DATE"));
//       //��������
//       result.setData("RES_DELIVERY_DATE",temp.getData("RES_DELIVERY_DATE"));
       //��ע
       result.setData("REMARK",this.getValue("REMARK"));
       result.setData("FINAL_FLG","0");    
       return result;   
   }
   /**
    * �õ��������ںͽ�������
    * @param purOrderNo String
    * @return TParm
    */
   public TParm getOtherData(String purOrderNo){
       TParm result = new TParm(this.getDBTool().select("SELECT * FROM DEV_PURORDERM WHERE PURORDER_NO = '"+purOrderNo+"'"));
       return result.getRow(0);
   }
   /**
    * �õ��빺������
    * @param purOrderNo String
    * @return TParm
    */
   public TParm getRequestData(String requestNo){
       TParm result = new TParm(this.getDBTool().select("SELECT * FROM DEV_PURCHASEM WHERE REQUEST_NO = '"+requestNo+"'"));
       return result.getRow(0);
   }

   /**
    * ����DEV_PURCHASEM��������
    * @param parm TParm
    * @return String
    */        
   public String creatRequestSQL(TParm parm,String type){
       String sql = ""; 
       //fux modify 20130806 ���������ɱ�� 
       if("INSERT".equals(type)){ 
           sql = "INSERT INTO DEV_RECEIPTM (RECEIPT_NO,PURORDER_NO,RECEIPT_DATE,SUP_CODE,REMARK,RECEIPT_DEPT,RECEIPT_USER,INVOICE_NO,INVOICE_DATE,INVOICE_AMT,RECEIPT_MINUTE,PURORDER_DATE,RES_DELIVERY_DATE,OPT_USER,OPT_DATE,OPT_TERM,FINAL_FLG ) VALUES ('"+parm.getValue("RECEIPT_NO")+"','"+parm.getValue("PURORDER_NO")+"',TO_DATE('"+StringTool.getString(parm.getTimestamp("RECEIPT_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),'"+parm.getValue("SUP_CODE")+"','"+parm.getValue("REMARK")+"','"+parm.getValue("RECEIPT_DEPT")+"','"+parm.getValue("RECEIPT_USER")+"','"+parm.getValue("INVOICE_NO")+"',TO_DATE('"+StringTool.getString(parm.getTimestamp("INVOICE_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),'"+parm.getValue("INVOICE_AMT")+"','"+parm.getValue("RECEIPT_MINUTE")+"',TO_DATE('"+StringTool.getString(parm.getTimestamp("PURORDER_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),TO_DATE('"+StringTool.getString(parm.getTimestamp("RES_DELIVERY_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),'"+Operator.getID()+"',SYSDATE,'"+Operator.getIP()+"','"+parm.getValue("FINAL_FLG")+"') ";
       }else{
           sql = "UPDATE DEV_RECEIPTM SET RECEIPT_NO='"+parm.getValue("RECEIPT_NO")+"',PURORDER_NO='"+parm.getValue("PURORDER_NO")+"',RECEIPT_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("RECEIPT_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),SUP_CODE='"+parm.getValue("SUP_CODE")+"',REMARK='"+parm.getValue("REMARK")+"',RECEIPT_DEPT='"+parm.getValue("RECEIPT_DEPT")+"',RECEIPT_USER='"+parm.getValue("RECEIPT_USER")+"',INVOICE_NO='"+parm.getValue("INVOICE_NO")+"',INVOICE_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("INVOICE_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),INVOICE_AMT='"+parm.getValue("INVOICE_AMT")+"',RECEIPT_MINUTE='"+parm.getValue("RECEIPT_MINUTE")+"',PURORDER_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("PURORDER_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),RES_DELIVERY_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("RES_DELIVERY_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' WHERE RECEIPT_NO='"+parm.getValue("RECEIPT_NO")+"'";
       }
       return sql; 
   } 
   /** 
    * ���ϸ���
    * @return TParm
    */ 
   public TParm isCheckMItem(){ 
       TParm result = new TParm();
       TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
       int rowCount = dateStore.rowCount();
       if(rowCount<=0){
           result.setErrCode(-1);
           result.setErrText("����д�豸��ϸ���ϣ�");
           return result;
       } 
       for(int i=0;i<rowCount;i++){ 
           if(!dateStore.isActive(i))   
               continue;        
           if(dateStore.getItemDouble(i,"UNIT_PRICE")<=0){
               result.setErrCode(-2);
               result.setErrText("�豸��Ϊ:"+dateStore.getItemString(i,"DEV_CHN_DESC")+"����Ŀ����д�ο��۸�");
               return result;
           }     
           if(dateStore.getItemInt(i,"QTY")<=0){ 
               result.setErrCode(-3);
               result.setErrText("�豸��Ϊ:"+dateStore.getItemString(i,"DEV_CHN_DESC")+"����Ŀ����д����������");
               return result; 
           }
       }
       return result;
   }
   /**
    * ����TABLE������
    * @param tag String
    * @param queryParm TParm
    * @return TDataStore
    */
   public TDataStore getTableTDataStore(String tag){
       TDataStore dateStore = new TDataStore();
       if(tag.equals("TABLE1")){
           String sql="SELECT * FROM DEV_RECEIPTM";
           TParm queryParm = this.getTable1QueryParm();
           String columnName[] = queryParm.getNames();
           if(columnName.length>0)
               sql+=" WHERE ";
           int count=0;
           for(String temp:columnName){
               if(temp.equals("QEND_DATE"))
                   continue;
               if(temp.equals("YEND_DATE"))
                   continue;
               //��������
               if(temp.equals("QSTART_DATE")){
                   if(count>0)
                       sql+=" AND ";
                   sql+=" RECEIPT_DATE BETWEEN TO_DATE('"+queryParm.getValue("QSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("QEND_DATE")+"','YYYYMMDD')";
                   count++;
                   continue;
               }  
//               //Ԥ����������(��Ʊ����)
//               if(temp.equals("YSTART_DATE")){
//                   if(count>0)
//                       sql+=" AND ";
//                   sql+=" RES_DELIVERY_DATE BETWEEN TO_DATE('"+queryParm.getValue("YSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("YEND_DATE")+"','YYYYMMDD')";
//                   count++;
//                    continue;
//               }
               if(count>0) 
                   sql+=" AND "; 
               sql+=temp+"='"+queryParm.getValue(temp)+"' ";
               count++;
           }
          // System.out.println("sql:"+sql);  
           dateStore.setSQL(sql);
           dateStore.retrieve(); 
       }
       if(tag.equals("TABLE2")){
           String receiptNo = this.getValueString("QRECEIPT_NO");
           DevReceiptDDataStore devReceiptDataStore = new DevReceiptDDataStore();
           devReceiptDataStore.setReceiptNo(receiptNo);
           devReceiptDataStore.onQuery();
           //�ο��۸��ܼ۸�
           double totAmt =getTotAmt(devReceiptDataStore);
           this.setValue("INVOICE_AMT",totAmt);
           return devReceiptDataStore;
       }
       return dateStore;
   }
   /**
    * �õ���ϸ������
    * @param requestNo String
    * @return DevBaseDataStore
    */
   public DevReceiptDDataStore getRequestDData(String receiptNo){
       DevReceiptDDataStore devReceiptDataStore = new DevReceiptDDataStore();
       devReceiptDataStore.setReceiptNo(receiptNo);
       devReceiptDataStore.onQuery();
       //�ο��۸��ܼ۸�
       double totAmt =getTotAmt(devReceiptDataStore);
       if(this.getValueDouble("INVOICE_AMT")==0){
           this.setValue("INVOICE_AMT",totAmt);
       }
       return devReceiptDataStore;
   }
   /**
    * ���㷢Ʊ�ܼ۸�SUM_QTYȡ����
    * @param devBaseDataStore TDataStore
    * @return double
    */ 
   public double getTotAmt(TDataStore devBaseDataStore){
       int rowCount = devBaseDataStore.rowCount();
       double totAmt = 0;
       for (int i = 0; i < rowCount; i++) {
           if(!devBaseDataStore.isActive(i)&&!(Boolean)devBaseDataStore.getItemData(i,"#NEW#"))
               continue;  
           totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE")*devBaseDataStore.getItemDouble(i, "SUM_QTY");
       }
       return totAmt;
   } 
   /**
    * ���㵥���ܼ�
    * @param devBaseDataStore TDataStore
    * @return double
    */
   public double getXJAmt(TDataStore devBaseDataStore){
       int rowCount = devBaseDataStore.rowCount();
       double totAmt = 0;
       for (int i = 0; i < rowCount; i++) {
           if(!devBaseDataStore.isActive(i)&&!(Boolean)devBaseDataStore.getItemData(i,"#NEW#"))
               continue; 
           totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE")*devBaseDataStore.getItemDouble(i, "QTY");
       }
       return totAmt;
   }
   
   
   
   /**
    * �õ�TABLE1�Ĳ�ѯ����
    * @return TParm
    */
   public TParm getTable1QueryParm(){
       TParm result = new TParm();
       String startDate = StringTool.getString((Timestamp)this.getValue("QSTART_DATE"),"yyyyMMdd");
       String endDate = StringTool.getString((Timestamp)this.getValue("QEND_DATE"),"yyyyMMdd");
       String deptCode = this.getValueString("QRECEIPT_DEPT");
       String operator = this.getValueString("QRECEIPT_USER");
       String requestNo = this.getValueString("QPURORDER_NO");
       String receiptNo = this.getValueString("QRECEIPT_NO");
       String supCode = this.getValueString("QSUP_CODE");
       String ydDateStart = StringTool.getString((Timestamp)this.getValue("QUSERSTART_DATE"),"yyyyMMdd");
       String ydDateEnd = StringTool.getString((Timestamp)this.getValue("QUSEEND_DATE"),"yyyyMMdd");
       if(startDate.length()!=0)
           result.setData("QSTART_DATE",startDate);
       if(endDate.length()!=0)
           result.setData("QEND_DATE",endDate); 
//       if(ydDateStart.length()!=0)
//           result.setData("YSTART_DATE",ydDateStart);
       if(ydDateEnd.length()!=0)
           result.setData("YEND_DATE",ydDateEnd);
       if(deptCode.length()!=0)
           result.setData("RECEIPT_DEPT",deptCode);
       if(operator.length()!=0)
           result.setData("RECEIPT_USER",operator);
       if(receiptNo.length()!=0)
           result.setData("RECEIPT_NO",receiptNo);
       if(requestNo.length()!=0)
           result.setData("PURORDER_NO",requestNo);
       if(supCode.length()!=0)
           result.setData("SUP_CODE",supCode);
       return result;
   }
   /**
    * ���
    */
   public void onClear(){
       //�ж��Ƿ񱣴�
       //���
       this.clearValue("QRECEIPT_NO;QPURORDER_NO;QSUP_CODE;PURORDER_NO;RECEIPT_NO;INVOICE_AMT;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;INVOICE_NO;RECEIPT_MINUTE;REMARK");
       /**
        * ��ʼ��ҳ��
        */
       onInitPage();    
       this.getTTable(TABLE2).setLockColumns("0,2,4,5,7");
   }
   /**
    * ��ѯ
    */
   public void onQuery(){
       this.clearValue("PURORDER_NO;RECEIPT_NO;INVOICE_AMT;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;INVOICE_NO;RECEIPT_MINUTE;REMARK");
       //��ʼ��TABLE1
       this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
       this.getTTable(TABLE1).setDSValue();
       //��ʼ��TABLE2
       this.getTTable(TABLE2).setDataStore(getRequestDData(""));
       this.getTTable(TABLE2).setDSValue();
   }
   /**
    * �ر��¼�
    * @return boolean
    */
   public boolean onClosing(){
       //�ж��Ƿ񱣴�
       return true;
   }
   /**
    * �õ�TTextFormat
    * @return TTextFormat
    */
   public TTextFormat getTTextFormat(String tag){
       return (TTextFormat)this.getComponent(tag);
   }
   /**
    * �õ�TTextField
    * @return TTextFormat
    */
   public TTextField getTTextField(String tag){
       return (TTextField)this.getComponent(tag);
   }
   /**
    * �õ�TTextArea
    * @param tag String
    * @return TTextArea
    */
   public TTextArea getTTextArea(String tag){
       return (TTextArea)this.getComponent(tag);
   }
   /**
    * �õ�TComboBox
    * @param tag String
    * @return TComboBox
    */
   public TComboBox getTComboBox(String tag){
       return (TComboBox)this.getComponent(tag);
   }
   /**
    * �õ�TCheckBox
    * @param tag String
    * @return TCheckBox
    */
   public TCheckBox getTCheckBox(String tag){
       return (TCheckBox)this.getComponent(tag);
   }
   /**
    * �õ�TTable
    * @param tag String
    * @return TTable
    */
   public TTable getTTable(String tag){
       return (TTable)this.getComponent(tag);
   }
   /**
    * ɾ��
    */
   public void onDelete(){
       int row = this.getTTable(TABLE1).getSelectedRow();
       if(row<0){
           this.messageBox("��ѡ��Ҫɾ�������ݣ�");
           return;
       }
       if((rateofproCode.equals("E"))){
           this.messageBox("����״̬������ɾ����");
           return;
       }
       if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ��?", this.YES_NO_OPTION) != 0)
           return;
       this.getTTable(TABLE1).getDataStore().deleteRow(row);
       int rowCount = this.getTTable(TABLE2).getDataStore().rowCount();
       for(int i=rowCount-1;i>=0;i--){
           if(!this.getTTable(TABLE2).getDataStore().isActive(i)&&this.getTTable(TABLE2).getDataStore().getItemString(i,"RECEIPT_NO").length()==0){
               continue;
           }
           this.getTTable(TABLE2).getDataStore().deleteRow(i);
       }
       this.getTTable(TABLE1).setDSValue();
       this.getTTable(TABLE2).setDSValue();
       String arraySqlTable1[] = this.getTTable(TABLE1).getUpdateSQL();
       String arraySqlTable2[] = this.getTTable(TABLE2).getUpdateSQL();
       String arraySql[] = StringUtil.getInstance().copyArray(arraySqlTable1,arraySqlTable2);
       TParm requestParm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
       String purOrderNo = requestParm.getValue("PURORDER_NO");
       TParm otherParm = this.getOtherData(purOrderNo);
       String[] sqlPurOrder = new String[]{"UPDATE DEV_PURORDERM SET RATEOFPRO_CODE='C',CHK_USER='"+Operator.getID()+"',CHK_DATE=SYSDATE WHERE PURORDER_NO='"+this.getValueString("PURORDER_NO")+"'"};
       arraySql = StringUtil.getInstance().copyArray(sqlPurOrder,arraySql);
       if(!otherParm.getValue("REQUEST_NO").equals("FALSE")){ 
           String[] sqlRequest = new String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='C' WHERE REQUEST_NO='"+otherParm.getValue("REQUEST_NO")+"'"};
           arraySql = StringUtil.getInstance().copyArray(sqlRequest,arraySql);
       }
       TParm sqlParm = new TParm();
       sqlParm.setData("SQL",arraySql);
       TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
       if (actionParm.getErrCode() < 0) {
           this.messageBox("ɾ��ʧ�ܣ�");
           return;
       }
       this.messageBox("ɾ���ɹ���");
       this.onClear();
   }
   /**
    * ��ӡ���յ�
    */
   public void onPrint(){
       int row = this.getTTable(TABLE1).getSelectedRow();
       if(row<0){
           this.messageBox("��ѡ��Ҫ��ӡ�����ݣ�");
           return;
       }
       TParm parm = getPurOrderM(this.getValueString("RECEIPT_NO"));
       parm.setData("TITLE_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
       parm.setData("FORMS_NAME","TEXT","���յ�");
       TParm printDataParm = new TParm();
       TParm tableParm = this.getTTable(TABLE2).getDataStore().getBuffer(TDataStore.PRIMARY);
       //System.out.println("tableParm"+tableParm);
       int rowCount = tableParm.getCount();
       for(int i=0;i<rowCount;i++){
           if(!tableParm.getBoolean("#ACTIVE#",i))
               continue;
           printDataParm.addRowData(tableParm,i,"DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;RECEIPT_QTY;REMARK;QTY");
       }
       //printDataParm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       //printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
       printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");  
       printDataParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       printDataParm.addData("SYSTEM", "COLUMNS", "QTY");  
       printDataParm.addData("SYSTEM", "COLUMNS", "RECEIPT_QTY");
       printDataParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
       Double tot_amt = 0.00;  
       printDataParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE*RECEIPT_QTY");  
       printDataParm.setCount(printDataParm.getCount("DEV_CODE"));
       parm.setData("DEV_RECEIPTD", printDataParm.getData());
       //System.out.println("parm"+parm);
       this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevReceiptForms.jhw",parm, false);
   }
   /**
    * ���ɶ�����
    */
   public void onCreatPuro(){

       TParm parm = new TParm();

       Object obj = this.openDialog("%ROOT%\\config\\dev\\ReceiptMUI.x");
       if(obj==null){
           return;
       }
       TParm action = (TParm)obj;
       this.setValue("RECEIPT_NO","");
       this.setValue("PURORDER_NO",action.getValue("PURORDER_NO"));
       this.setValue("SUP_CODE",action.getValue("SUP_CODE"));
       this.setValue("SUP_SALES1",action.getValue("SUP_SALES1"));
       this.setValue("SUP_SALES1_TEL",action.getValue("SUP_SALES1_TEL"));
       this.setValue("PAYMENT_TERMS",action.getValue("PAYMENT_TERMS"));
       this.getTTextFormat("SUP_CODE").setEnabled(false);
       TParm purOrderDParm = new TParm(this.getDBTool().select("SELECT * FROM DEV_PURORDERD WHERE PURORDER_NO='"+action.getValue("PURORDER_NO")+"'"));
       int rowCount = purOrderDParm.getCount();
       for(int i=0;i<rowCount;i++){
           TParm temp = purOrderDParm.getRow(i);
           this.popReturn(temp);  
       }
       this.getTTable(TABLE2).setDSValue();
       double totAmt = this.getTotAmt(this.getTTable(TABLE2).getDataStore());
       if(action.getDouble("INVOICE_AMT")!=totAmt){
           if(this.messageBox("��ʾ��Ϣ","���۸��붩���ܼ۸�����Ƿ�ȡ����",this.YES_NO_OPTION)!=0){
               this.setValue("INVOICE_AMT",totAmt);
           }else{
               this.setValue("INVOICE_AMT",action.getValue("TOT_AMT"));
           }
       }
   }
   

   
   /**
    * ���ܷ���ֵ����
    * @param tag String
    * @param obj Object
    */
   public void popReturn(TParm action){ 
       this.getTTable(TABLE2).acceptText();  
       int rowNum =this.getTTable(TABLE2).getDataStore().insertRow(); 
       //�ж��Ƿ��������µ���Ŀ 
       if((rateofproCode.equals("E"))){
           this.messageBox("����״̬���������");
           this.getTTable(TABLE2).getDataStore().setItem(rowNum,"DEV_CHN_DESC","");
           this.getTTable(TABLE2).setDSValue(rowNum);
           return;
       } 
       String columnArr[] = this.getTTable(TABLE2).getDataStore().getColumns();
       for(String temp:columnArr){
           if(action.getValue(temp).length()==0&&!"SET_PURORDER_NO".equals(temp)&&!"SET_SEQ_NO".equals(temp))
               continue;
           if("OPT_DATE".equals(temp)) 
               continue;
           if("OPT_USER".equals(temp))
               continue;
           if("OPT_TERM".equals(temp))
               continue;
           if("SET_PURORDER_NO".equals(temp)){
               this.getTTable(TABLE2).getDataStore().setItem(rowNum,temp,action.getData("PURORDER_NO"));
               continue;
           }
           if("SET_SEQ_NO".equals(temp)){
               this.getTTable(TABLE2).getDataStore().setItem(rowNum,temp,action.getData("SEQ_NO"));
               continue;
           }   
           //fux modify   
           this.getTTable(TABLE2).getDataStore().setItem(rowNum,temp,action.getData(temp));
       }
   }
 
}
