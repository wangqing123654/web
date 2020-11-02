package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;  
import java.util.Map;     

import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestMTool;  
import jdo.dev.DevOutRequestDTool;  
import jdo.dev.DevOutStorageTool;  
import jdo.dev.DevPurChaseTool;
import jdo.dev.DevTypeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.system.textFormat.TextFormatDEVOrg;
import com.javahis.util.StringUtil;



/**  
 * <p>Title: ������ҵ�����쵥��</p>  
 *
 * <p>DESCRIPTION: ������ҵ</p>   
 *
 * <p>Copyright: Copyright (c) 2009</p>
 * 
 * <p>Company: javahis</p>
 *
 * @author  fux
 * @version 1.0      
 */ 
public class DevRequestControl extends TControl {  
    //������ϸ������ 
    /**
     * ��ʼ������ 
     */
    public void onInit() { 
         super.onInit();  
         addEventListener("DEV_REQUEST->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
         getTTable("DEV_REQUEST").addEventListener(
        		 TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
         initTableD();   
         onInitComponent();    
         onInitOperatorDept();  
         //���״̬�²��ɱ���,ɾ��
         if (getRadioButton("UPDATE_FLG_B").isSelected()) {
             ( (TMenuItem) getComponent("delete")).setEnabled(false);  
             ( (TMenuItem) getComponent("save")).setEnabled(false);
         } 
    }
     
    /** 
     * ���ÿ���������Ȩ��   
     */
    private void onInitOperatorDept(){  
        // ��ʾȫԺ�豸����
        if (getPopedem("deptAll"))   
            return;   
//        ((TextFormatDEVOrg)getComponent("OUT_DEPT")).setOperatorId(Operator.getID());
//        ((TextFormatDEVOrg)getComponent("REQUESTOUT_DEPT")).setOperatorId(Operator.getID());
    }
    /**  
     * ��ʼ������Ĭ��ֵ
     */ 
    public void onInitComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("REQUEST_DATE_BEGIN",timestamp);//���쿪ʼʱ��
        setValue("REQUEST_DATE_END",timestamp);//�������ʱ��
        setValue("OUT_DEPT",Operator.getDept());//��Ӧ����
        setValue("IN_DEPT","");//�������
        setValue("REQUEST_NO","");//���뵥��        
            
        setValue("REQUESTOUT_DEPT",Operator.getDept());//��Ӧ����
        setValue("INREQUEST_DEPT","");//������� 
        setValue("FLG",'N');//������ǳ�ʼΪN
        setValue("REQUEST_REASON","");//����ԭ��
        setValue("REMARK","");//��ע
        setValue("REQUEST_STATUE","");//����״̬  
        setValue("OPERATOR",Operator.getName());//������Ա  
        //�������
        addDRow();
    }          
    /**
     * ��շ���
     */
    public void onClear(){
        onInitComponent();
        //ȷ��PARMMAP����
        initTableD();   
        addDRow(); 
        ((TTable)getComponent("REQUEST_TABLE")).removeRowAll();
    }     
    /**  
     * ��ʼ���豸���쵥���  
     */
    public void initTableD(){   
        String column = "DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;" +
        		        "DEV_CODE;DEV_CHN_DESC;" +
        		        "SPECIFICATION;QTY;STORGE_QTY;" + 
        		        "SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE";  
        String stringMap[] = StringTool.parseLine(column,";");  
        TParm tableDParm = new TParm();  
        for(int i = 0;i<stringMap.length;i++){      
            tableDParm.addData(stringMap[i],"");
        }     
        ((TTable)getComponent("DEV_REQUEST")).setParmValue(tableDParm);   
        ((TTable)getComponent("DEV_REQUEST")).removeRow(0);           
        //���޸�  
//        ((TTable)getComponent("DEV_REQUEST")).setLockColumns("1,2,3,4," +
//                                                             "5,6,7,8,9,"+
//                                                             "10,11,12"); 
    }
    /**
     * ��ѯ����
     */   
    public void onQuery(){    
        TParm parm = new TParm(); 
      //δ���
      if (getRadioButton("UPDATE_FLG_A").isSelected()) {
    	  parm.setData("FINAL_FLG", "N");
      }
      //����� 
      else if(getRadioButton("UPDATE_FLG_B").isSelected()){
    	  parm.setData("FINAL_FLG", "Y");
      }  
      if (getValueString("REQUEST_NO").length() != 0)
        parm.setData("REQUEST_NO", getValue("REQUEST_NO"));
      if (getValueString("REQUEST_DATE_BEGIN").length() != 0)  
        parm.setData("REQUEST_DATE_BEGIN", getValue("REQUEST_DATE_BEGIN"));
      if (getValueString("REQUEST_DATE_END").length() != 0)
        parm.setData("REQUEST_DATE_END", getValue("REQUEST_DATE_END"));
      if (getValueString("OUT_DEPT").length() != 0)
        parm.setData("APP_ORG_CODE", getValue("OUT_DEPT"));  
      if (getValueString("IN_DEPT").length() != 0)
        parm.setData("TO_ORG_CODE", getValue("IN_DEPT"));
      if (parm.getNames().length == 0)   
         {                   
      	this.messageBox("�޲�ѯ���ݣ�");  
      	return;  
      }	            
      parm = DevOutRequestMTool.getInstance().queryRequestM(parm);
      if (parm.getErrCode() < 0){   
      	this.messageBox("��ѯ��������");  
          return; 
      }  
      ((TTable)getComponent("REQUEST_TABLE")).setParmValue(parm);
  	  }
       
    /**
     * ����ɨ��س��¼���������Ҫ�����ӡ��
     */
    public void onBarcode(){
      if(getValueString("EXWAREHOUSE_DEPT").length() == 0){
          messageBox("������Ҳ���Ϊ��");
          return;    
      }
      if(getValueString("EXWAREHOUSE_USER").length() == 0){
          messageBox("������Ա����Ϊ��");
          return;
      }
       String barcode = getValueString("SCAN_BARCODE");
       int devCodeLength = getDevCodeLength();
       if(barcode.length() < devCodeLength){
           messageBox("¼�����������");
           return;
       }
       //��Ź��������豸��ź���ˮ�Ų�ѯ�豸
       TParm parm = new TParm();
       if(barcode.length() > devCodeLength){
           parm.setData("SEQMAN_FLG","Y");
           parm.setData("DEV_CODE",barcode.substring(0,devCodeLength));
       }
       //������Ź����豸�����豸��Ų�ѯ
       else{
           parm.setData("DEV_CODE",barcode);
           parm.setData("SEQMAN_FLG", "N");
       }
       //��ѯ��Χ�ڱ�����
       parm.setData("DEPT_CODE",getValue("EXWAREHOUSE_DEPT"));
       parm = DevOutStorageTool.getInstance().getExStorgeInf(parm);
       if(parm.getErrCode()<0)
           return;
       if(parm.getCount("DEV_CODE") <= 0){
           messageBox("���޴��豸,������ȷ�������Ƿ���ȷ");
           return;
       }
       //ɾ������
       ((TTable)getComponent("DEV_EXWAREHOUSED")).removeRow(((TTable)getComponent("DEV_EXWAREHOUSED")).getRowCount() - 1);
       //����豸�Ƿ����¼��
       for(int i = 0;i < parm.getCount("DEV_CODE");i++){
           boolean have = false;
           for(int j = 0;j < ((TTable)getComponent("DEV_EXWAREHOUSED")).getRowCount();j++){
               if(parm.getData("DEV_CODE",i).equals(((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(j,3))&&
                  parm.getData("BATCH_SEQ",i).equals(((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(j,4))&&
                  parm.getData("DEVSEQ_NO",i).equals(((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(j,5)))
                   have = true;
           }
           if(have)    
               continue;
           parm.setData("INWAREHOUSE_DEPT", i, getValue("INWAREHOUSE_DEPT"));
           ((TTable)getComponent("DEV_EXWAREHOUSED")).addRow(parm.getRow(i));
       }
       ((TTable)getComponent("DEV_EXWAREHOUSED")).setLockColumns("1,4,5,6,7,"+
                                                                  "9,14,15,16,"+
                                                                  "17,18,19,20,23");
       //�ӿ���    
       addDRow();
       ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false);
    }
    /**
     * �õ��豸���볤��
     * @return int
     */
    private int getDevCodeLength(){
        TParm parm = DevTypeTool.getInstance().getDevRule();
        return parm.getInt("TOT_NUMBER",0);
    } 
    /**
     * ����Ƿ������Ҫ��������
     * @return boolean
     */
    private boolean onSaveCheck(){
        int rowCount = 0;
        for(int i = 0;i<((TTable)getComponent("DEV_REQUEST")).getRowCount();i++){
            if(("" + ((TTable)getComponent("DEV_REQUEST")).getValueAt(i,0)).equals("N")&&
               ("" + ((TTable)getComponent("DEV_REQUEST")).getValueAt(i,4)).length()!=0)
                rowCount++;     
        }
        if(((TTable)getComponent("REQUEST_TABLE")).getSelectedRow() < 0 &&
           rowCount == 0){
            messageBox("�ޱ�����Ϣ");
            return true;
        }    
        return false;
   }
    /**
     * ɾ������
     */
    public void onDelete(){      	
    	TTable tableM = ((TTable)getComponent("REQUEST_TABLE")); 
    	TTable tableD = ((TTable)getComponent("DEV_REQUEST")); 
    	//parm = DevOutRequestMTool.getInstance().deletedevrequestmm(parm);
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            this.messageBox("�������ⲻ��ɾ��");
        }
  
        else {
            if (tableM.getSelectedRow() > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����쵥", 2) == 0) {
                    TParm parm = new TParm();
                    parm = tableM.getParmValue(); 
                    // ϸ����Ϣ
                    if (tableD.getRowCount() > 0) {
                    	tableD.removeRowAll();
                        String deleteSql[] = tableD.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // ������Ϣ
                    // ���쵥��  
                    parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDVerifyinAction", "onDeleteM", parm);
                    // ɾ���ж�
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    tableM.removeRow(
                    tableM.getSelectedRow());
                    this.messageBox("ɾ���ɹ�");
                }
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
                    int row = tableD.getSelectedRow();
                    tableD.removeRow(row);
                    // ϸ����ж�        
                    if (!tableD.update()) {
                        messageBox("E0001");  
                        return;
                    }
                    messageBox("P0001");
                    tableD.setDSValue();
                }
            }
        }
    	//ɾ�����������
    }
    
   /**
    * ���涯��
    */
   public void onSave(){
        getTTable("REQUEST_TABLE").acceptText();
        getTTable("DEV_REQUEST").acceptText();
        if(onSaveCheck())
            return;
//        if(getValueString("REQUEST_REASON").length() == 0){
//            messageBox("����ԭ�򲻿�Ϊ��");
//            return;
//        }  
        if(getValueString("INREQUEST_DEPT").length() == 0){
            messageBox("������Ҳ���Ϊ��");
            return;  
        }   
        TTable tableD = (TTable)getComponent("DEV_REQUEST");
        for(int i = 0;i < tableD.getRowCount()-1;i++){     
        	if((tableD.getValueAt(i,3) + "").length() == 0){
                messageBox("��"+(i+1)+"���豸���벻��Ϊ��");
                return;
            } 
            if((tableD.getValueAt(i,6) + "").length() == 0){
                messageBox("��"+(i+1)+"��������������Ϊ��");  
                return;    
            }  
            if(Integer.parseInt ("" + tableD.getValueAt(i,6))>Integer.parseInt ("" + tableD.getValueAt(i,7))){
            	 messageBox("��"+(i+1)+"�������������ɴ��ڿ����");  
                 return;   
            } 
        }     
       //�ж����������޸�
       //TTable table = (TTable)getComponent("EXWAREHOUSE_TABLE");
       TTable table = (TTable)getComponent("REQUEST_TABLE");
       if(table.getSelectedRow() < 0)
    	   //��TABLE������������
           onNew();
       else
           onUpdate();  
   }
   /**
    * �����޸Ķ���(fux modify)
    */           
   public void onUpdate(){
	    //TParm parmD = new TParm(); 
	    boolean flg = this.getValueBoolean("FLG"); 
	 //fux modify 
        TTable tableM = ((TTable)getComponent("REQUEST_TABLE"));
        int row = tableM.getSelectedRow();
        if(row < 0)  
            return;
        TTable dTable = ((TTable)getComponent("DEV_REQUEST"));
        if(dTable.getRowCount() <= 0)
            return;
        TParm parm = dTable.getParmValue();  
        System.out.println("parm"+parm);    
        TParm parmDTransport = new TParm();  
        Timestamp timestamp = SystemTool.getInstance().getDate();    
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");   
        //ɾ��ϵ����ϸ��    
        for (int i = 0; i < dTable.getRowCount(); i++) {
            if("Y".equals(dTable.getItemString(i, "DEL_FLG"))){  
         	   boolean flgtiis = parm.getBoolean("DEL_FLG");
         	   boolean fig = StringTool.getBoolean(parm.getValue("DEL_FLG", i));
         	   System.out.println("fig"+fig); //true
         	   System.out.println("flgtiis"+flgtiis); //false      
         	   System.out.println("ɾ��ϸ��");              
         	   parm = DevOutRequestDTool.getInstance().deletedevrequestdd(parm);
         	   messageBox("ɾ���ɹ�");
         	   return;     
                }           
            }      
        for(int i = 0;i<parm.getCount("DEV_CODE");i++){  
//            if(compareTo(parmD,parm,i))                
//                continue;                 
            cloneTParm(parm,parmDTransport,i);
            TParm devBase = getDevBase("" + dTable.getItemData(i, "DEV_CODE"));
            TParm devStorge = getDevSTORGE("" + dTable.getItemData(i, "DEV_CODE"));
            parmDTransport.addData("SEQMAN_FLG", devBase.getData("SEQMAN_FLG",0)); 
            parmDTransport.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE")); 
            parmDTransport.addData("DEV_CODE ",dTable.getItemData(i, "DEV_CODE"));  
            parmDTransport.addData("DEV_CHN_DESC",dTable.getItemData(i, "DEV_CHN_DESC")); 
            parmDTransport.addData("SPECIFICATION",dTable.getItemData(i, "SPECIFICATION"));
            //��Ҫ���Ǳ���ʱУ��QTY<STORGE_QTY     
            //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
            //SPECIFICATION;QTY;STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
            parmDTransport.addData("QTY",dTable.getItemData(i, "QTY"));   
            parmDTransport.addData("STORGE_QTY",devStorge.getData("QTY",0));
            parmDTransport.addData("SETDEV_CODE",dTable.getItemData(i, "SETDEV_CODE"));
            parmDTransport.addData("UNIT_CODE",dTable.getItemData(i, "UNIT_CODE")); 
            parmDTransport.addData("UNIT_PRICE",dTable.getItemData(i, "UNIT_PRICE"));
            parmDTransport.addData("TOT_VALUE",dTable.getItemData(i, "TOT_VALUE"));  
            parmDTransport.addData("UPDATE_FLG",flg);  
            parmDTransport.addData("OPT_USER",Operator.getID()); 
            parmDTransport.addData("OPT_DATE",timestamp);  
            parmDTransport.addData("OPT_TERM",Operator.getIP());  
        }    
          
        TParm actionParm = TIOM_AppServer.executeAction("action.dev.DevAction",
                           "UpdateRequest", parmDTransport);    
        System.out.println("actionParmUpdateRequest==="+actionParm);  
        if(actionParm.getErrCode() < 0){
        	messageBox("E0001");  
            return;      
        }
        messageBox("P0001"); 
        //tableM.getValueAt(row,1)�����쵥��
        onPrintAction(""+tableM.getValueAt(row,1));
        TParm parmD = new TParm();  
        onTableMClick();
    } 
   /**
    * ������������
    */
   private void onNew(){   
	    TParm parmD = new TParm();  
	    boolean flg = this.getValueBoolean("FLG"); 
       String requestoutNo = DevOutRequestMTool.getInstance().getRequestNo();
       Timestamp timestamp = SystemTool.getInstance().getDate();
       String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
       "yyyyMMdd");  
       TParm dParm = new TParm();   
       TParm mParm = new TParm();
       TTable dTable = ((TTable)getComponent("DEV_REQUEST"));  
       System.out.println("i"+dTable.getRowCount());
       //dTable:(����TABLEֵ�ı��¼���������Parm����TTable����) 
       for(int i = 0;i<dTable.getRowCount();i++){
    	   System.out.println("i"+i); 
    	   //�豸��� 
           if(("" + dTable.getValueAt(i,3)).length() == 0)
               continue;
           //ɾ�����ΪY  
           if(("" + dTable.getValueAt(i,0)).equals("Y")) 
               continue;
           //�����豸�����ϸ��Ϣ  
           //dTable.getValueAt(i,2) == dev_code     
           TParm devBase = getDevBase("" + dTable.getItemData(i, "DEV_CODE")); 
           TParm devStorge = getDevSTORGE("" + dTable.getItemData(i, "DEV_CODE"));
           dParm.addData("REQUEST_NO",requestoutNo); 
           dParm.addData("SEQMAN_FLG",dTable.getItemData(i, "SEQMAN_FLG")); 
           dParm.addData("DEVPRO_CODE",dTable.getItemData(i, "DEVPRO_CODE"));
           dParm.addData("DEV_CODE",dTable.getItemData(i, "DEV_CODE"));        
           dParm.addData("DEV_CHN_DESC",dTable.getItemData(i, "DEV_CHN_DESC")); 
           dParm.addData("SPECIFICATION",dTable.getItemData(i, "SPECIFICATION")); 
           //��Ҫ���Ǳ���ʱУ��QTY<STORGE_QTY   
           dParm.addData("QTY",dTable.getItemData(i, "QTY"));  
           //fux modify     
           dParm.addData("STORGE_QTY",dTable.getItemData(i, "STORGE_QTY"));
           dParm.addData("SETDEV_CODE",dTable.getItemData(i, "SETDEV_CODE")); 
           //dParm.addData("SETDEV_CODE",dTable.getValueAt(i,8));
           dParm.addData("UNIT_CODE",dTable.getItemData(i, "UNIT_CODE"));
           dParm.addData("UNIT_PRICE",dTable.getItemData(i, "UNIT_PRICE")); 
           //fux modify 
           dParm.addData("TOT_VALUE", 1000); 
           //dParm.addData("TOT_VALUE", dTable.getItemData(i, "TOT_VALUE"));  
           dParm.addData("UPDATE_FLG",flg);   
           dParm.addData("OPT_USER",Operator.getID());                 
           dParm.addData("OPT_DATE",timestamp); 
           dParm.addData("OPT_TERM",Operator.getIP());    
           //0δ���  1δ��ȫ��� 3��ȫ���
           dParm.addData("FINA_TYPE","0"); 

       } 

       //������ⵥ������Ϣ       
       mParm.addData("REQUEST_NO",requestoutNo);        
       mParm.addData("REQUEST_DATE",StringTool.getTimestampDate(timestamp));    
       mParm.addData("APP_ORG_CODE",getValue("REQUESTOUT_DEPT"));
       mParm.addData("TO_ORG_CODE",getValue("INREQUEST_DEPT"));  
       mParm.addData("REQUEST_USER",getValue("OPERATOR"));//������  
       mParm.addData("REQUEST_REASON",getValue("REQUEST_REASON"));
       mParm.addData("OPT_USER",Operator.getID());  
       mParm.addData("OPT_DATE",timestamp);    
       mParm.addData("OPT_TERM",Operator.getIP());       
       mParm.addData("REGION_CODE",Operator.getRegion()); 
       //N δ��� Y�����  
       mParm.addData("FINAL_FLG","N"); 
       System.out.println("mParm"+mParm);   
       System.out.println("dParm"+dParm);     
       TParm parm = new TParm();    
       parm.setData("DEV_REQUESTMM",mParm.getData());   
       parm.setData("DEV_REQUESTDD",dParm.getData());
       TParm actionParm = TIOM_AppServer.executeAction(        
           "action.dev.DevAction","InsertRequest", parm);     
       System.out.println("actionParmInsertRequest==="+actionParm); 
     if(actionParm.getErrCode() < 0){
    	 messageBox("E0001");    
           return;   
      }
     else if(mParm.getValue("REQUEST_USER").equals("")){
    	   messageBox("����д������Ա");   
           return;   
     }
         messageBox("P0001");  
     for(int i = 0;i < mParm.getCount("REQUEST_NO");i++){
           onPrintAction(mParm.getValue("REQUEST_NO",i));
      }
       onClear();
       onQuery();
   }  

   private TParm getDevSTORGE(String devCode) {
	   String SQL="SELECT DEPT_CODE,DEV_CODE,BATCH_SEQ,QTY FROM DEV_STOCKM WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;  
}
   private TParm getDevDSTORGE(String devCode) {
	   String SQL="SELECT DEPT_CODE,DEV_CODE,BATCH_SEQ,QTY FROM DEV_STOCKDWHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
}
   /**
    * �õ��豸����������Ϣ
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBase(String devCode){
       String SQL=" SELECT DEV_CODE,ACTIVE_FLG,DEVKIND_CODE,DEVTYPE_CODE," +
       		" DEVPRO_CODE,DEV_CHN_DESC,PY1,PY2,SEQ,SPECIFICATION,DEV_ENG_DESC," +
       		" DEV_ABS_DESC,UNIT_CODE,BUYWAY_CODE,SEQMAN_FLG," +
       		" DEPR_METHOD,MEASURE_FLG,MEASURE_ITEMDESC,MEASURE_FREQ," +
       		" USE_DEADLINE,BENEFIT_FLG,DEV_CLASS,SETDEV_CODE," +
       		" OPT_USER,OPT_DATE,OPT_TERM,MAN_CODE,MAN_NATION,UNIT_PRICE FROM DEV_BASE " +
       		" WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
    } 
   /**
    * �õ��豸����������Ϣ(����ȡ�ø�����Ϣ)
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBaseSet(String devCode){
       String SQL=" SELECT DEV_CODE,ACTIVE_FLG,DEVKIND_CODE,DEVTYPE_CODE," +
       		" DEVPRO_CODE,DEV_CHN_DESC,PY1,PY2,SEQ,SPECIFICATION,DEV_ENG_DESC," +
       		" DEV_ABS_DESC,UNIT_CODE,BUYWAY_CODE,SEQMAN_FLG," +
       		" DEPR_METHOD,MEASURE_FLG,MEASURE_ITEMDESC,MEASURE_FREQ," +
       		" USE_DEADLINE,BENEFIT_FLG,DEV_CLASS,SETDEV_CODE," + 
       		" OPT_USER,OPT_DATE,OPT_TERM,MAN_CODE,MAN_NATION,UNIT_PRICE FROM DEV_BASE "+
       		" WHERE SETDEV_CODE = '"+devCode+"'"; 
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
    } 
   
   /**
    * �õ��豸����������Ϣ(����ȡ�ø�����Ϣ)
    * @param devCode String
    * @return TParm
    */
   public TParm getDevStock(String devCode,String deptCode){
       String SQL=" SELECT DEPT_CODE,DEV_CODE,QTY FROM DEV_STOCKM "+
       		" WHERE DEV_CODE = '"+devCode+"'" +
       	    " AND DEPT_CODE = '"+deptCode+"'"; 
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm;
    }
   /**
    * �õ��豸���������Ϣ
    * @param code String
    * @return String
    */
   private String getDevTypeCode(String code){
      TParm parm = DevTypeTool.getInstance().getDevRule();
      int classify1 = parm.getInt("CLASSIFY1",0);
      return code.substring(0,classify1);
   }
    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from,TParm to,int row){
        String names[] = from.getNames();
        for(int i = 0;i < names.length;i++){
            Object obj = from.getData(names[i],row);
            if(obj == null)
                obj = "";
            to.addData(names[i],obj);
        }
     }

     /**
      * �Ƚ���ͬ��TParm��ֵ�Ƿ�ı�
      * @param parmA TParm
      * @param parmB TParm
      * @param row int
      * @return boolean
      */
     private boolean compareTo(TParm parmA,TParm parmB,int row){
        String names[] = parmA.getNames();
        for(int i = 0;i < names.length;i++){
            if(parmA.getValue(names[i],row).equals(parmB.getValue(names[i],row)))
                continue;
            return false;
        }
        return true;
    }
     /**
      * �豸���ⵥ�������¼�(������)
      */
     public void onTableMClick(){        
 	    TParm parmD = new TParm();    
	    boolean flg = this.getValueBoolean("FLG"); 
         TTable tableM = ((TTable)getComponent("REQUEST_TABLE"));
         int row = tableM.getSelectedRow();  
         TParm tableMParm = tableM.getParmValue();
         setValue("REQUEST_NUMBLE",tableMParm.getData("REQUEST_NO",row));
         System.out.println("resqus_no=="+tableMParm.getData("REQUEST_NO",row));
//         setValue("TO_ORG_CODE",tableMParm.getData("TO_ORG_CODE",row));
//         setValue("APP_ORG_CODE",tableMParm.getData("APP_ORG_CODE",row));
//         ((TTextFormat)getComponent("REQUEST_NUMBLE")).setEnabled(false);
//         ((TTextFormat)getComponent("REQUESTOUT_DEPT")).setEnabled(false);
//         ((TTextFormat)getComponent("INREQUEST_DEPT")).setEnabled(false); 
         TParm result = DevOutRequestDTool.getInstance().queryRequestD(tableMParm.getValue("REQUEST_NO",row));
         if(result.getErrCode() < 0)     
             return;        
         ((TTable)getComponent("DEV_REQUEST")).setParmValue(result);
//         ((TTable)getComponent("DEV_REQUEST")).setLockColumns("0,1,2,3,4,5,6,7,"+
//                                                                   "8,9,10,14,15,16,"+
//                                                                      "17,18,19,20,23");
         for(int i = 0;i<result.getCount();i++)
            cloneTParm(result,parmD,i);  
     }


    /**
     * �豸������ϸ���༭�¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        //fux modify 
        //�����������༭�¼�
        if(onTableQty(node))
            return true;
        //�豸����༭�¼�
        if(onDevCode(node))
            return true;
        //�豸���Ա༭�¼�
        if(onDevProCode(node))
            return true;  
//        //���ÿ��ұ༭�¼�
//        if(onInExwarehouseDept(node))
//            return true;
        return false;
   }
//   /**
//    * ���ÿ��ұ༭�¼�
//    * @param node TTableNode
//    * @return boolean
//    */
//   public boolean onInExwarehouseDept(TTableNode node){
//       if(node.getColumn() != 10)
//           return false;
//       if(node.getValue().equals(getValue("EXWAREHOUSE_DEPT")))
//           return true;
//       return false;
//   }
   /**
    * �豸����༭�¼�
    * @param node TTableNode
    * @return boolean
    */
   public boolean onDevCode(TTableNode node){
       if(node.getColumn() != 4)
            return false;
        TTable table = (TTable)getComponent("DEV_REQUEST");
        if(("" + table.getValueAt(node.getRow(),5)).length() != 0)
            return true;
        return false;
   }
   /**
    * �豸���Ա༭�¼�
    * @param node TTableNode
    * @return boolean
    */
   public boolean onDevProCode(TTableNode node){
       if(node.getColumn() != 3)  
            return false;
        TTable table = (TTable)getComponent("DEV_REQUEST");
        if(("" + table.getValueAt(node.getRow(),5)).length() != 0)
            return true;
        return false;
   }
   /** 
    * �豸������༭�¼�
    * @param node TTableNode
    * @return boolean
    */
   public boolean onTableQty(TTableNode node){
       if(node.getColumn() != 7)
            return false;    
        TTable table = (TTable)getComponent("DEV_REQUEST");
        if(Integer.parseInt(node.getValue() + "") == 0){
            messageBox("������������");
            return true; 
        }    
        if(Integer.parseInt(node.getValue() + "") > 
           Integer.parseInt("" +table.getValueAt(node.getRow(),9))){
            messageBox("���������ɴ��ڿ����");
            return true;
        }
        //String tableTag,int row,int column,Object obj
        //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
        //SPECIFICATION;QTY;STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
        updateTableData("DEV_REQUEST",node.getRow(),17,   
                        Integer.parseInt(node.getValue() + "") *
                        Double.parseDouble("" +table.getValueAt(node.getRow(),16)));
        return false;
   }

   /**
    * ���±������
    * @param tableTag String
    * @param row int
    * @param column int
    * @param obj Object
    */
   public void updateTableData(String tableTag,int row,int column,Object obj){
       ((TTable)getComponent(tableTag)).setValueAt(obj,row,column);
       ((TTable)getComponent(tableTag)).getParmValue().setData(getFactColumnName(tableTag,column),row,obj);
   }
   /**
    * �����豸������ϸ������
    */
   public void addDRow(){   
	   //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;STORGE_QTY;INREQUEST_DEPT;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
       String column = "DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
       		           "SPECIFICATION;QTY;STORGE_QTY;INREQUEST_DEPT;" +
       		           "SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE";
       String stringMap[] = StringTool.parseLine(column,";");
       TParm tableDParm = new TParm();  
       for(int i = 0;i<stringMap.length;i++){  
          if(stringMap[i].equals("DEVPRO_CODE"))   
             tableDParm.setData(stringMap[i],"A"); 
           else  
             tableDParm.setData(stringMap[i],"");        
          } 
       ((TTable)getComponent("DEV_REQUEST")).addRow(tableDParm);  
   }   
   /**
    * �豸���ⵥ��ӡ
    */
   public void onPrint(){   
       TTable table = ((TTable)getComponent("REQUEST_TABLE"));
       if(table.getSelectedRow() < 0)
           return;
       onPrintAction("" + table.getValueAt(table.getSelectedRow(),0));
   }  
   /**
    * ��ӡ����
    * @param exWarehoseNo String
    */
   public void onPrintAction(String requestoutNo){  
       if(requestoutNo == null ||
    		   requestoutNo.length() == 0)
           return; 
       TParm printParm = new TParm();   
  
        TParm  parm = DevOutRequestMTool.getInstance().queryRequestM(requestoutNo);   
       if(parm.getErrCode() < 0)
           return;
       if(parm.getCount("REQUEST_NUMBLE") <= 0){
           messageBox("�޴�ӡ����");
           return;  
       }
       //���¶�δʵ��
       //�����ֵ(Ϊ��ʱ�ſգ�������������˵��)
         clearNullAndCode(parm);
       //���ñ�ͷ  
       printParm.setData("HOSP_NAME",Operator.getHospitalCHNShortName());
       printParm.setData("REQUEST_NO",parm.getValue("REQUEST_NO",0));
       printParm.setData("REQUEST_DATE",parm.getValue("REQUEST_DATE",0).substring(0,10).replace('-','/'));
       printParm.setData("APP_ORG_CODE",getDeptDesc(parm.getValue("APP_ORG_CODE",0)));
       printParm.setData("REQUEST_USER",getOperatorName(parm.getValue("REQUEST_USER",0)));
       printParm.setData("TO_ORG_CODE",getDeptDesc(parm.getValue("TO_ORG_CODE",0)));
       //���ñ����Ϣ
       //DEV_REQUESTMM   
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_NO");
       parm.addData("SYSTEM", "COLUMNS", "APP_ORG_CODE");
       parm.addData("SYSTEM", "COLUMNS", "TO_ORG_CODE");
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_DATE");
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_USER");
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_REASON");
       //DEV_REQUESTDD
       parm.addData("SYSTEM", "COLUMNS", "REQUEST_NO");
       parm.addData("SYSTEM", "COLUMNS", "SEQMAN_FLG");
       parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       parm.addData("SYSTEM", "COLUMNS", "QTY");
       parm.addData("SYSTEM", "COLUMNS", "STORGE_QTY");
       parm.addData("SYSTEM", "COLUMNS", "SETDEV_CODE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");     
       printParm.setData("RECEIPT",parm.getData());  
       //openPrintWindow("%ROOT%\\config\\prt\\dev\\DevRequest.jhw",printParm);
  }  
  
  /**
   * ȡ���豸������������
   * @param devProCode String
   * @return String
   */
  public String getDevProDesc(String devProCode){
       TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                 " WHERE GROUP_ID = 'DEVPRO_CODE'"+
                                                 " AND   ID = '"+devProCode+"'"));
       return parm.getValue("CHN_DESC",0);
    }

    /**
     * ȡ�����ݿ������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }
    /**
     * �õ�������Ա����
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
       TParm parm = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
       if(parm.getCount() <= 0)
           return "";
       return parm.getValue("USER_NAME",0);
    }
    /**
     * �õ��豸��������
     * @param devCode String
     * @return String
     */
    public String getDevDesc(String devCode){
        String SQL="SELECT DEV_CHN_DESC FROM DEV_BASE WHERE DEV_CODE = '"+devCode+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getCount() <= 0)
            return "";
        return parm.getValue("DEV_CHN_DESC",0);
    }
    /**
     * �õ�������������
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * �����ֵ
     * @param parm TParm
     */
    private void clearNullAndCode(TParm parm){
      String names[] = parm.getNames();
      for(int i = 0;i < names.length;i++){
          for(int j = 0 ; j < parm.getCount(names[i]) ; j++){
              if(parm.getData(names[i],j) == null)
                  parm.setData(names[i],j,"");
              if("DEVPRO_CODE".equals(names[i]))
                  parm.setData(names[i],j,getDevProDesc(parm.getValue(names[i],j)));
          }
      }
   } 
    /**
     * �豸¼���¼�
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        //�豸����
        String devProCode = ""+getTTable("DEV_REQUEST").getValueAt(row,1);
        //״̬����ʾ
        callFunction("UI|setSysStatus","");             
        //�õ�����
        String columnName = getFactColumnName("DEV_REQUEST",column);
        if(!"DEV_CODE".equals(columnName))
            return;
        if(!(com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        if(("" +getTTable("DEV_REQUEST").getValueAt(row,column)).length() != 0)
            return;
        TParm parm = new TParm(); 
        parm.setData("DEVPRO_CODE",devProCode);
        parm.setData("ACTIVE","Y");
        //���õ����˵�
        textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
        //������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    } 
 /**
  * �����豸¼�뷵�ز���   
  * @param tag String
  * @param obj Object  
  */                 
    public void popReturn(String tag,Object obj){
     //�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
       if (obj == null && !(obj instanceof TParm)) {
           return;  
     }     
       
     //����ת����TParm     
     TParm parm = (TParm)obj; 
     callFunction("UI|setSysStatus", new Object[] { parm.getValue("DEV_CODE") + ":" + parm.getValue("DEV_CHN_DESC") + parm.getValue("SPECIFICATION") });
     getTTable("DEV_REQUEST").acceptText();  
     TParm tableParm = new TParm(); 
     //������������parm
     TParm tableParmSet = new TParm(); 
     //�ж��ǲ���  ������������  
     System.out.println("---�豸���----"+parm.getValue("DEVPRO_CODE"));
     String devCode = parm.getValue("DEV_CODE").replace("[", "").replace("]", "");
     //����parm
     TParm parmSet = getDevBaseSet(devCode);
    
     //��Ӧ���ҵĿ����(���ݹ�Ӧ���Һ�DEV_CODE���QTY)
     TParm parmStockM = getDevStock(devCode,this.getValueString("REQUESTOUT_DEPT"));
     //������Ϣ   1��
     parm = getDevBase(devCode); 
         //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;
         //STORGE_QTY;SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE
     if(parmStockM.getValue("QTY",0)==null ||"".equals(parmStockM.getValue("QTY",0))){
    	 this.messageBox("�����ʿ�治�㣡");  
    	 return;
     } 
         tableParm.setData("DEL_FLG","N");     
         tableParm.setData("SEQMAN_FLG",parm.getValue("SEQMAN_FLG",0)); 
         tableParm.setData("DEVPRO_CODE",parm.getValue("DEVPRO_CODE",0));
         tableParm.setData("DEV_CODE",devCode);   
         tableParm.setData("DEV_CHN_DESC",parm.getValue("DEV_CHN_DESC",0));
         tableParm.setData("SPECIFICATION",parm.getValue("SPECIFICATION",0)); 
         tableParm.setData("QTY",parm.getValue("QTY",0));             
         tableParm.setData("STORGE_QTY", parmStockM.getValue("QTY",0)); 
         //�Ǹ��������豸 
         tableParm.setData("SETDEV_CODE","");     
         tableParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
         tableParm.setData("UNIT_PRICE",  parm.getDouble("UNIT_PRICE",0));
         //����һ���ܶ�           
//         int qty = Integer.parseInt ("" + parm.getData("QTY"));
//         System.out.println("qty"+qty); 
//         System.out.println("price"+price);    
         //fux need modify  
         System.out.println("tableParm"+tableParm); 
         
//         tableParm.setData("TOT_VALUE",Integer.parseInt ("" + tableParm.getData("STORGE_QTY")) * 
//                                      Double.parseDouble("" + parm.getDouble("UNIT_PRICE",0)));
         getTTable("DEV_REQUEST").removeRow(getTTable("DEV_REQUEST").getSelectedRow());
         getTTable("DEV_REQUEST").addRow(tableParm); 
//         ((TTable)getComponent("DEV_REQUEST")).setLockColumns("1,4,5,6,7,"9,14,15,16,17");   "+  
    if("".equals(parmSet.getValue("DEV_CODE"))){                                                           
         addDRow();  
     }   
   //����Ǵ���������������Ҫ�Ѹ������������Ҹ�������Ҫ�������������� 
     else{  
    	 for(int i=0; i<parmSet.getCount() ;i++){ 
    		 //�������
    		 String devSetCode = parmSet.getValue("DEV_CODE",i).replace("[", "").replace("]", "");
    		 parmStockM = getDevStock(devSetCode,this.getValueString("REQUESTOUT_DEPT"));
    	     tableParmSet.setData("DEL_FLG","N");    
    	     tableParmSet.setData("SEQMAN_FLG",parmSet.getValue("SEQMAN_FLG",i));  
    	     tableParmSet.setData("DEVPRO_CODE",parmSet.getValue("DEVPRO_CODE",i));
    	     tableParmSet.setData("DEV_CODE",devSetCode);    
    	     tableParmSet.setData("DEV_CHN_DESC",parmSet.getValue("DEV_CHN_DESC",i));
    	     tableParmSet.setData("SPECIFICATION",parmSet.getValue("SPECIFICATION",i)); 
    	     tableParmSet.setData("QTY",parmStockM.getValue("QTY",i));  
    	     tableParmSet.setData("STORGE_QTY", parmStockM.getValue("QTY",i) );     
    	     tableParmSet.setData("SETDEV_CODE",parmSet.getValue("SETDEV_CODE",i));  
    	     tableParmSet.setData("UNIT_CODE",parmSet.getValue("UNIT_CODE",i));  
    	     tableParmSet.setData("UNIT_PRICE",parmSet.getValue("UNIT_PRICE",i)); 
    	     tableParmSet.setData("TOT_VALUE",Integer.parseInt ("" + parmSet.getData("STORGE_QTY")) * 
                     Double.parseDouble("" + parmSet.getDouble("UNIT_PRICE",0)));  
    	     tableParmSet.setData("PARM",tableParmSet.getData());
    	     //tableParmSet.getData("PARM", i); 
    	     TParm tableParmM = (TParm)tableParmSet.getData("PARM",i);
    	     getTTable("DEV_REQUEST").addRow(tableParmM);   
    	 }    
    	     addDRow();   
     }
     
      
} 
      
   /**
    * �õ������λ��
    * @param tableTag String
    * @param column int
    * @return String
    */
   public String getFactColumnName(String tableTag,int column){
        int col = getThisColumnIndex(column);
        return getTTable(tableTag).getDataStoreColumnName(col);
    }
    /**
     * �õ������λ����
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column){  
        return getTTable("DEV_REQUEST").getColumnModel().getColumnIndex(column);
    }
    /**
     * �õ�TTable
     * @param tag String 
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)getComponent(tag);
     }
    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
 
}
