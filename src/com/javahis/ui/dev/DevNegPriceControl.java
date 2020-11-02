package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TNumberTextField;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TDataStore;
import java.util.Date;
import com.dongyang.ui.TTextFormat;
import jdo.dev.DevBaseDataStore;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import com.dongyang.ui.TTabbedPane;
import javax.swing.SwingUtilities;
import jdo.dev.DevNegpriceDataStore;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:ѯ�۹��� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class DevNegPriceControl extends TControl {
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
      * ϸ
      */
     private static String TABLE3 = "TABLE3";
     /**
      * �빺����
      */
     private String rateofproCode="";
     /**
      * ���״̬
      */
     private boolean checkFlg=false;
     /**
      * ������
      */
     private boolean checkFlgN=false;
     /**
      * �༭״̬
      */
     private String type="INSERT";
     /**
      * ҳǩ
      */
     private static String TABLEPANE = "TABLEPANE";
     /**
      * ��ʼ������
      */
     public void onInitParameter(){
         /**
          * 1��һ��Ȩ�ޣ�������Ա��:�������ҿ�����Ա,�빺����Ĭ��Ϊ����������������׼ GeneralPermissions
          * 2���ϲ��빺�����豸��Ա�������ҿ�����Ա������׼ ClerksPermissions
          * 3����ˣ��豸�Ƴ��� AuditPermissions
          */
//        //һ��Ȩ��
//        this.setPopedem("GeneralPermissions",true);
//        //�ϲ��빺��
//        this.setPopedem("ClerksPermissions",true);
         //���
//         this.setPopedem("AuditPermissions",true);
     }
     /**
      * ��ʼ������
      */
     public void onInit(){
         /**
          * ��ʼ��ҳ��
          */
         onInitPage();
         /**
          * ��ʼ��Ȩ��
          */
         onInitPopeDem();
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
         this.setValue("QDEPT_CODE",Operator.getDept());
         this.setValue("QOPERATOR",Operator.getID());
         this.setValue("REQUEST_DEPT",Operator.getDept());
         this.setValue("REQUEST_USER",Operator.getID());
         //�빺����
         this.setValue("RATEOFPRO_CODE","A");
         this.setValue("QRATEOFPRO_CODE","A");
         //����
         Timestamp startDate = StringTool.getTimestamp(StringTool.getString(StringTool.getTimestamp(new Date()),"yyyy/MM/dd"),"yyyy/MM/dd");
         //����
         Timestamp endDate = StringTool.getTimestamp("9999/12/31","yyyy/MM/dd");
         //��ѯ�빺��������
         this.setValue("QSTART_DATE",startDate);
         //��ѯ�빺��������
         this.setValue("QEND_DATE",endDate);
         //�빺����
         this.setValue("REQUEST_DATE",startDate);
         //Ԥ��ʹ��������
         this.setValue("QUSERSTART_DATE",startDate);
         //Ԥ��ʹ��������
         this.setValue("QUSEEND_DATE",endDate);
         //Ԥ��ʹ����
         this.setValue("USE_DATE",startDate);
         //��ʼ��TABLE1
         this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
         this.getTTable(TABLE1).setDSValue();
         //��ʼ��TABLE2
         this.getTTable(TABLE2).setDataStore(getTableTDataStore("TABLE2"));
         this.getTTable(TABLE2).setDSValue();
         //��ʼ��TABLE3
         this.getTTable(TABLE3).setDataStore(getTableTDataStore("TABLE3"));
         this.getTTable(TABLE3).setDSValue();
         //�������û�
         callFunction("UI|save|setEnabled", false);
         //ɾ�����û�
         callFunction("UI|delete|setEnabled", false);
         //��ӡ���û�
         callFunction("UI|print|setEnabled", false);
     }
     /**
      * �¼���ʼ��
      */
     public void initEven(){
         //����TABLE1�����¼�
         callFunction("UI|" + TABLE1 + "|addEventListener",
                     TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClickedTable1");
         //ϸ��TABLE1�����¼�
         callFunction("UI|" + TABLE3 + "|addEventListener",
                      TABLE3 + "->" + TTableEvent.CLICKED, this, "onTableClickedTable3");

     }
     /**
      * ��ϸ����¼�
      * @param row int
      */
     public void onTableClickedTable3(int row){
         TParm parm = this.getTTable(TABLE3).getDataStore().getRowParm(row);
         if(parm.getData("CHK_DATE")!=null&&parm.getValue("CHK_USER").length()!=0){
             checkFlgN = true;
         }else{
             checkFlgN = false;
         }
         this.setValue("SUP_CODE",parm.getData("SUP_CODE"));
         onSupCodeChick();
         this.setValue("MAN_CODE",parm.getData("MAN_CODE"));
         this.setValue("MAN_NATION",parm.getData("MAN_NATION"));
         this.setValue("NEGTOT_AMT",parm.getData("TOT_AMT"));
         this.setValue("CURRENCY",parm.getData("CURRENCY"));
         this.setValue("RMBTOT_AMT",parm.getData("RMBTOT_AMT"));
         this.setValue("CHOOSE_FLG",parm.getBoolean("CHOOSE_FLG"));
         this.setValue("PAYMENT_TERMS",parm.getData("PAYMENT_TERMS"));
         this.setValue("REMARK",parm.getData("REMARK"));
         this.setValue("CHECKNeg_FLG",checkFlgN);
         type = "EDIT";
     }
     /**
      * ����¼�
      */
     public void onTableClickedTable1(int row){
         TParm parm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
         //�빺����
         rateofproCode = parm.getValue("RATEOFPRO_CODE");
         if(parm.getData("CHK_DATE")!=null&&parm.getValue("CHK_USER").length()!=0){
             checkFlg = true;
         }else{
             checkFlg = false;
         }
         this.setValue("REQUEST_NO",parm.getData("REQUEST_NO"));
         this.setValue("REQUEST_DEPT", parm.getData("REQUEST_DEPT"));
         this.setValue("REQUEST_USER", parm.getData("REQUEST_USER"));
         this.setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
         this.setValue("REQUEST_DATE", parm.getData("REQUEST_DATE"));
         this.setValue("USE_DATE", parm.getData("USE_DATE"));
         this.setValue("TOT_AMT", parm.getData("TOT_AMT"));
         this.setValue("CHECK_FLG", checkFlg);
         this.setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
         this.setValue("PURTYPE_CODE", parm.getData("PURTYPE_CODE"));
         this.setValue("DEVUSE_CODE", parm.getData("DEVUSE_CODE"));
         //��ʼ��TABLE2
         this.getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("REQUEST_NO")));
         this.getTTable(TABLE2).setDSValue();
         //��ʼ�������ϸ
         this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(this.getValueString("REQUEST_NO")));
         this.getTTable(TABLE3).setDSValue();
         type = "INSERT";
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
      *
      */
     public void onChangeTable() {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 try {
                     onChangeTableD();
                 }
                 catch (Exception e) {
                 }

             }
         });
     }
     /**
      * ҳǩ�л��¼�
      */
     public void onChangeTableD(){
         int row = this.getTTable(TABLE1).getSelectedRow();
         if(this.getTTabbedPane(TABLEPANE).getSelectedIndex()==1){
             if (row < 0) {
                 this.messageBox("��ѡ���빺����");
                 this.getTTabbedPane(TABLEPANE).setSelectedIndex(0);
                 return;
             }
             //if (!this.checkFlg) {
             if (false) {
                 this.messageBox("�빺û����˲����Զ��������ϸ��");
                 this.getTTabbedPane(TABLEPANE).setSelectedIndex(0);
                 return;
             }
             this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
             //��ʼ�������ϸ
             this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(this.getValueString("REQUEST_NO")));
             this.getTTable(TABLE3).setDSValue();
             //�������û�
             callFunction("UI|save|setEnabled", true);
             //ɾ�����û�
             callFunction("UI|delete|setEnabled", true);
             //��ӡ���û�
             callFunction("UI|print|setEnabled", true);
         }else{
             //�������û�
             callFunction("UI|save|setEnabled", false);
             //ɾ�����û�
             callFunction("UI|delete|setEnabled", false);
             //��ӡ���û�
             callFunction("UI|print|setEnabled", false);
         }
         type = "INSERT";
     }
     /**
      * ��Ӧ���������¼�
      */
     public void onSupCodeChick(){
         String supCode = this.getValueString("SUP_CODE");
         TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1,SUP_SALES1_TEL,SUP_SALES1_EMAIL,ADDRESS FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         if(parm.getCount()<0)
             return;
         this.setValue("SUP_SALES1",parm.getData("SUP_SALES1",0));
         this.setValue("SUP_SALES1_TEL",parm.getData("SUP_SALES1_TEL",0));
         this.setValue("SUP_SALES1_EMAIL",parm.getData("SUP_SALES1_EMAIL",0));
         this.setValue("ADDRESS",parm.getData("ADDRESS",0));

     }
     /**
      * �������ݿ��������
      * @return TJDODBTool
      */
     public TJDODBTool getDBTool() {
         return TJDODBTool.getInstance();
     }

     /**
      * �õ�TabbedPane
      * @param tag String
      * @return TTabbedPane
      */
     public TTabbedPane getTTabbedPane(String tag){
         return (TTabbedPane)this.getComponent(tag);
     }
     /**
      * ����
      * @return boolean
      */
     public boolean onSave(){
         if("INSERT".equals(type)){
             if(!this.emptyTextCheck("SUP_CODE,MAN_CODE,MAN_NATION,CURRENCY"))
                 return false;
             if (this.getValueDouble("NEGTOT_AMT") <= 0) {
                 this.messageBox("����д���ۣ�");
                 return false;
             }
             if (this.getValueDouble("RMBTOT_AMT") <= 0) {
                 this.messageBox("����д�ۺ�����ң�");
                 return false;
             }
             DevNegpriceDataStore devNegpriceData = (DevNegpriceDataStore)this.getTTable(TABLE3).getDataStore();
             if(devNegpriceData.rowCount()>0){
                  String sql[] = devNegpriceData.insertRow(getInsertParm());
                  TParm sqlParm = new TParm();
                  sqlParm.setData("SQL", sql);
                  TParm actionParm = TIOM_AppServer.executeAction(actionName,
                      "saveDevRequest", sqlParm);
                  if (actionParm.getErrCode() < 0) {
                      this.messageBox("����ʧ�ܣ�");
                      return false;
                  }
             }else{
                 String sql[] = devNegpriceData.insertRow(getInsertParm());
                 String arraySql[] = StringUtil.getInstance().copyArray(sql,
                     getUpdateRequestMSQL("B"));
                 TParm sqlParm = new TParm();
                 sqlParm.setData("SQL", arraySql);
                 TParm actionParm = TIOM_AppServer.executeAction(actionName,
                     "saveDevRequest", sqlParm);
                 if (actionParm.getErrCode() < 0) {
                     this.messageBox("����ʧ�ܣ�");
                     return false;
                 }
             }
         }else{
             //�������
            if ( (rateofproCode.equals("C")||rateofproCode.equals("D") || rateofproCode.equals("E"))) {
                this.messageBox("�빺���빺����״̬�������޸ģ�");
                return false;
            }
             int row = this.getTTable(TABLE3).getSelectedRow();
             DevNegpriceDataStore devNegpriceData = (DevNegpriceDataStore)this.getTTable(TABLE3).getDataStore();
             if (!devNegpriceData.updateRow(row,getInsertParm())) {
                 this.messageBox("����ʧ�ܣ�");
                 return false;
             }
             type = "INSERT";
         }
         this.messageBox("����ɹ���");
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         //��ʼ�������ϸ
         this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(this.getValueString("REQUEST_NO")));
         this.getTTable(TABLE3).setDSValue();
         return true;
     }
     /**
      * �����빺����
      * @return String[]
      */  
     public String [] getUpdateRequestMSQL(String type){
         return new String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='"+type+"' WHERE REQUEST_NO='"+this.getValueString("REQUEST_NO")+"'"};
     }
     /**
      * �õ���������
      * @return TParm
      */
     public TParm getInsertParm(){
         TParm result = new TParm();
         result.setData("REQUEST_NO",this.getValue("REQUEST_NO"));
         result.setData("SUP_CODE",this.getValue("SUP_CODE"));
         result.setData("MAN_CODE",this.getValue("MAN_CODE"));
         result.setData("MAN_NATION",this.getValue("MAN_NATION"));
         result.setData("TOT_AMT",this.getValueDouble("NEGTOT_AMT"));
         result.setData("CURRENCY",this.getValue("CURRENCY"));
         result.setData("RMBTOT_AMT",this.getValueDouble("RMBTOT_AMT"));
         result.setData("PAYMENT_TERMS",this.getValue("PAYMENT_TERMS"));
         result.setData("REMARK",this.getValue("REMARK"));
         result.setData("REMARK",this.getValue("REMARK"));
         result.setData("CHOOSE_FLG",this.getTCheckBox("CHOOSE_FLG").isSelected()?"Y":"N");
         if(this.getTCheckBox("CHECKNeg_FLG").isSelected()){
             result.setData("CHK_USER",Operator.getID());
             result.setData("CHK_DATE",StringTool.getTimestamp(new Date()));
         }else{
             result.setData("CHK_USER","");
             result.setData("CHK_DATE","");
         }
         result.setData("OPT_USER",Operator.getID());
         result.setData("OPT_DATE",StringTool.getTimestamp(new Date()));
         result.setData("OPT_TERM",Operator.getIP());
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
             String sql="SELECT * FROM DEV_PURCHASEM"; 
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
                 //�빺����
                 if(temp.equals("QSTART_DATE")){
                     if(count>0)
                         sql+=" AND ";
                     sql+=" REQUEST_DATE BETWEEN TO_DATE('"+queryParm.getValue("QSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("QEND_DATE")+"','YYYYMMDD')";
                     count++;
                     continue;
                 }
                 //Ԥʹ�ö�����
                 if(temp.equals("YSTART_DATE")){
                     if(count>0)
                         sql+=" AND ";
                     sql+=" USE_DATE BETWEEN TO_DATE('"+queryParm.getValue("YSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("YEND_DATE")+"','YYYYMMDD')";
                     count++;
                      continue;
                 }
                 if(count>0)
                     sql+=" AND ";
                 sql+=temp+"='"+queryParm.getValue(temp)+"' ";
                 count++;
             } 
             System.out.println("sql::::::::"+sql);
             dateStore.setSQL(sql);
             dateStore.retrieve();
         } 
         if(tag.equals("TABLE2")){
             String qrequestNo = this.getValueString("QREQUEST_NO");
             DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
             devBaseDataStore.setRequestNo(qrequestNo);
             devBaseDataStore.onQuery();
             //�ο��۸��ܼ۸�
             double totAmt =getTotAmt(devBaseDataStore);
             this.setValue("TOT_AMT",totAmt);
             return devBaseDataStore;
         }
         if(tag.equals("TABLE3")){
             String qrequestNo = this.getValueString("REQUEST_NO");
             DevNegpriceDataStore devNegPriceDataStore = new DevNegpriceDataStore();
             devNegPriceDataStore.setRequestNo(qrequestNo);
             devNegPriceDataStore.onQuery();
             return devNegPriceDataStore;
         }

         return dateStore;
     }
     /**
      * �õ���ϸ������
      * @param requestNo String
      * @return DevBaseDataStore
      */
     public DevBaseDataStore getRequestDData(String requestNo){
         String qrequestNo = requestNo;
         DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
         devBaseDataStore.setRequestNo(qrequestNo);
         devBaseDataStore.onQuery();
         //�ο��۸��ܼ۸�
         double totAmt =getTotAmt(devBaseDataStore);
         this.setValue("TOT_AMT",totAmt);
         return devBaseDataStore;
     }
     /**
      * �õ���ϸ������
      * @param requestNo String
      * @return DevBaseDataStore
      */
     public DevNegpriceDataStore getDev_NegPriceData(String requestNo){
         String qrequestNo = requestNo;
         DevNegpriceDataStore devNegPriceDataStore = new DevNegpriceDataStore();
         devNegPriceDataStore.setRequestNo(qrequestNo);
         devNegPriceDataStore.onQuery();
         //�ο��۸��ܼ۸�
         return devNegPriceDataStore;
     }

     /**
      * �����ܼ۸�
      * @param devBaseDataStore TDataStore
      * @return double
      */
     public double getTotAmt(TDataStore devBaseDataStore){
         int rowCount = devBaseDataStore.rowCount();
         double totAmt = 0;
         for (int i = 0; i < rowCount; i++) {
             if(!devBaseDataStore.isActive(i))
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
         String deptCode = this.getValueString("QDEPT_CODE");
         String operator = this.getValueString("QOPERATOR");
         String reteoptro = this.getValueString("QRATEOFPRO_CODE");
         String requestNo = this.getValueString("QREQUEST_NO");
         String ydDateStart = StringTool.getString((Timestamp)this.getValue("QUSERSTART_DATE"),"yyyyMMdd");
         String ydDateEnd = StringTool.getString((Timestamp)this.getValue("QUSEEND_DATE"),"yyyyMMdd");
         String fundsouCode = this.getValueString("QFUNDSOU_CODE");
         String purtypeCode = this.getValueString("QPURTYPE_CODE");
         String devUseCode = this.getValueString("QDEVUSE_CODE");
         if(startDate.length()!=0)
             result.setData("QSTART_DATE",startDate);
         if(endDate.length()!=0)
             result.setData("QEND_DATE",endDate);
         if(ydDateStart.length()!=0)
             result.setData("YSTART_DATE",ydDateStart);
         if(ydDateEnd.length()!=0)
             result.setData("YEND_DATE",ydDateEnd);
         if(deptCode.length()!=0)
             result.setData("REQUEST_DEPT",deptCode);
         if(operator.length()!=0)
             result.setData("REQUEST_USER",operator);
         if(reteoptro.length()!=0)
             result.setData("RATEOFPRO_CODE",reteoptro);
         if(requestNo.length()!=0)
             result.setData("REQUEST_NO",requestNo);
         if(fundsouCode.length()!=0)
             result.setData("FUNDSOU_CODE",fundsouCode);
         if(purtypeCode.length()!=0)
             result.setData("PURTYPE_CODE",purtypeCode);
         if(devUseCode.length()!=0)
             result.setData("DEVUSE_CODE",devUseCode);
         return result;
     }
     /**
      * ���
      */
     public void onClear(){
         //���
         this.clearValue("QREQUEST_NO;QFUNDSOU_CODE;QPURTYPE_CODE;QDEVUSE_CODE;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         /**
          * ��ʼ��ҳ��
          */
         onInitPage();
         /**
         * ��ʼ��Ȩ��
         */
        onInitPopeDem();
        //�л�����
        this.getTTabbedPane(TABLEPANE).setSelectedIndex(0);
        type = "INSERT";
        this.checkFlg = false;
     }
     /**
      * ��ѯ
      */
     public void onQuery(){
         this.clearValue("REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         //��ʼ��TABLE1
         this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
         this.getTTable(TABLE1).setDSValue();
         //��ʼ��TABLE2
         this.getTTable(TABLE2).setDataStore(getRequestDData(""));
         this.getTTable(TABLE2).setDSValue();
         //��ʼ��TABLE3
         this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(""));
         this.getTTable(TABLE3).setDSValue();
     }
     /**
      * �ر��¼�
      * @return boolean
      */
     public boolean onClosing(){
         return true;
     }
     /**
      * 1��һ��Ȩ�ޣ�������Ա��:�������ҿ�����Ա,�빺����Ĭ��Ϊ����������������׼ GeneralPermissions
      * 2���ϲ��빺�����豸��Ա�������ҿ�����Ա������׼ ClerksPermissions
      * 3����ˣ��豸�Ƴ��� AuditPermissions
      */
     public void onInitPopeDem(){
         //���
         if(this.getPopedem("AuditPermissions")){
             this.getTCheckBox("CHOOSE_FLG").setEnabled(true);
             this.getTCheckBox("CHECKNeg_FLG").setEnabled(true);
         }
         //һ��Ȩ��
         if(this.getPopedem("GeneralPermissions")){
             this.getTCheckBox("CHOOSE_FLG").setEnabled(false);
             this.getTCheckBox("CHECKNeg_FLG").setEnabled(false);
         }
     }
     /**
      * �õ�TTextFormat
      * @return TTextFormat
      */
     public TTextFormat getTTextFormat(String tag){
         return (TTextFormat)this.getComponent(tag);
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
         int row = this.getTTable(TABLE3).getSelectedRow();
         DevNegpriceDataStore devNegpriceData = (DevNegpriceDataStore)this.getTTable(TABLE3).getDataStore();
         int rowCount = devNegpriceData.rowCount();
         if(row<0){
             this.messageBox("��ѡ��Ҫɾ������Ϣ��");
             return;
         }
         if(checkFlgN){
             this.messageBox("�Ѿ���˲�����ɾ����");
             return;
         }
         if(this.getTCheckBox("CHOOSE_FLG").isSelected()){
             this.messageBox("�Ѿ��б겻����ɾ����");
             return;
         }
         if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ����", this.YES_NO_OPTION) != 0) {
             return;
         }
         if(rowCount>1){
             String sql[] = devNegpriceData.deleteRows(row);
             TParm sqlParm = new TParm();
             sqlParm.setData("SQL",sql);
             TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
             if (actionParm.getErrCode() < 0) {
                 this.messageBox("����ʧ�ܣ�");
                 return;
             }
             this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
             this.getTTable(TABLE3).setDSValue();
             this.messageBox("����ɹ���");
         }else{
             String sql[] = devNegpriceData.deleteRows(row);
             String arraySql[] = StringUtil.getInstance().copyArray(sql,getUpdateRequestMSQL("A"));
             for(String temp:arraySql){
                 System.out.println("SQL:"+temp);
             }
             TParm sqlParm = new TParm();
             sqlParm.setData("SQL",arraySql);
             TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
             if (actionParm.getErrCode() < 0) {
                 this.messageBox("����ʧ�ܣ�");
                 return;
             }
             this.messageBox("����ɹ���");
             this.onClear();
         }
     }

     /**
      * �õ��빺��������
      * @return TParm
      */
     public TParm getRequestM(String requestNo) {
         TParm result = new TParm();
         //�빺����
         result.setData("REQUEST_NO", requestNo);
         //�빺����
         result.setData("REQUEST_DATE", this.getValue("REQUEST_DATE"));
         //�빺����
         result.setData("REQUEST_DEPT", this.getValue("REQUEST_DEPT"));
         //�빺��Ա
         result.setData("REQUEST_USER", this.getValue("REQUEST_USER"));
         //�빺����
         result.setData("RATEOFPRO_CODE", this.getValue("RATEOFPRO_CODE"));
         //Ԥ��ʹ����
         result.setData("USE_DATE", this.getValue("USE_DATE"));
         //�빺�ܼ�
         result.setData("TOT_AMT", this.getValue("TOT_AMT"));
         //�ʽ���Դ
         result.setData("FUNDSOU_CODE", this.getValue("FUNDSOU_CODE"));
         //�빺���
         result.setData("PURTYPE_CODE", this.getValue("PURTYPE_CODE"));
         //�豸��;
         result.setData("DEVUSE_CODE", this.getValue("DEVUSE_CODE"));
         return result;
     }
     /**
      * ��ӡ�빺��
      */
     public void onPrint(){
         int row = this.getTTable(TABLE1).getSelectedRow();
        if(row<0){
            this.messageBox("��ѡ��Ҫ��ӡ�����ݣ�");
            return;
        }
        TParm parm = getRequestM(this.getValueString("REQUEST_NO"));
        parm.setData("TITLE_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        parm.setData("FORMS_NAME","TEXT","��۷�����");
        TParm printDataParm = new TParm();
        TParm tableParm = this.getTTable(TABLE2).getDataStore().getBuffer(TDataStore.PRIMARY);
        int rowCount = tableParm.getCount();
        for(int i=0;i<rowCount;i++){
            if(!tableParm.getBoolean("#ACTIVE#",i))
                continue;
            printDataParm.addRowData(tableParm,i,"DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;QTY;REMARK");
        }
        printDataParm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
        printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
        printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
        printDataParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        printDataParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
        printDataParm.addData("SYSTEM", "COLUMNS", "QTY");
        printDataParm.addData("SYSTEM", "COLUMNS", "REMARK");
        printDataParm.setCount(printDataParm.getCount("DEV_CODE"));
        parm.setData("DEV_PURCHASED", printDataParm.getData());  
        TParm printDataThreeParm = new TParm();
        TParm tableThreeParm = this.getTTable(TABLE3).getDataStore().getBuffer(TDataStore.PRIMARY);
        int rowCountThree = tableThreeParm.getCount();
        for(int i=0;i<rowCountThree;i++){
            if(!tableThreeParm.getBoolean("#ACTIVE#",i))
                continue;
            printDataThreeParm.addRowData(tableThreeParm,i,"SEQ_NO;SUP_CODE;TOT_AMT;MAN_CODE;REMARK");
        }
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "SEQ_NO");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "REMARK");
        printDataThreeParm.setCount(printDataThreeParm.getCount("SEQ_NO"));
        parm.setData("DEV_NEGPRICE", printDataThreeParm.getData());
        this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevNegPrice.jhw",parm, false);
     }
}
