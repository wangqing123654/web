package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TNumberTextField;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TDataStore;
import java.util.Date;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import java.awt.Component;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import jdo.dev.DevPurorderDDataStore;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ��������</p> 
 *
 * <p>Description: ��������</p>  
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DevPurordermControl extends TControl {  
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
     * ���״̬
     */
    private boolean checkFlg=false;
    /**
     * Ȩ�޴���
     */
    private String popedemType="1";
    /**
     * ״̬��¼
     * û���빺��NOREQUEST���빺��HAVEREQUEST
     */
    private String type="NOREQUEST";
    /**
     * ��ʼ������
     */
    public void onInitParameter(){
        /**
         * 1��һ��Ȩ�ޣ�������Ա��:�������ҿ�����Ա,�빺����Ĭ��Ϊ����������������׼ GeneralPermissions
         * 2����ˣ��豸�Ƴ��� AuditPermissions
         */
//        //һ��Ȩ��
//        this.setPopedem("GeneralPermissions",true);
//        //�ϲ��빺��
//        this.setPopedem("ClerksPermissions",true);
        //���
//        this.setPopedem("AuditPermissions",true);
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
        this.setValue("QPURORDER_DEPT",Operator.getDept());
        this.setValue("QPURORDER_USER",Operator.getID());
        this.setValue("PURORDER_DEPT",Operator.getDept());
        this.setValue("PURORDER_USER",Operator.getID());
        //��������
        this.setValue("RATEOFPRO_CODE","C");
        this.setValue("QRATEOFPRO_CODE","C");
        //����
        Timestamp startDate = StringTool.getTimestamp(StringTool.getString(StringTool.getTimestamp(new Date()),"yyyy/MM/dd"),"yyyy/MM/dd");
        //����
        Timestamp endDate = StringTool.getTimestamp("9999/12/31","yyyy/MM/dd");
        //��ѯ������������
        this.setValue("QSTART_DATE",startDate);
        //��ѯ������������
        this.setValue("QEND_DATE",endDate);
        //��������
        this.setValue("PURORDER_DATE",startDate);
        //Ԥ������������
        this.setValue("QUSERSTART_DATE",startDate);
        //Ԥ������������
        this.setValue("QUSEEND_DATE",endDate);
        //Ԥ��������
        this.setValue("RES_DELIVERY_DATE",startDate);
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
     * �¼���ʼ��
     */
    public void initEven(){
        //����TABLE1�����¼�
        callFunction("UI|" + TABLE1 + "|addEventListener",
                    TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClicked");
        //ϸ��TABLE2ֵ�ı����
        addEventListener(TABLE2+"->"+TTableEvent.CHANGE_VALUE, this,
                                   "onChangeTableValue");

        //ϸ��TABLE2�����¼�
        getTTable(TABLE2).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                           "onCreateEditComoponent");
        getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
    }
    /**
     * ����¼�
     * @param obj Object
     */
    public void onCheckBoxValue(Object obj){
        TTable table = (TTable)obj; 
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = this.getTTable(TABLE2).getDataStoreColumnName(col);
        if("#ACTIVE#".equals(columnName)){
            double totAmt = this.getTotAmt(table.getDataStore());
            this.setValue("TOT_AMT",totAmt);
        }
    }
    /**
     * ֵ�ı��¼����� 
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
        if("#ACTIVE#".equals(columnName)){
            return false;
        }
        if ("UNIT_PRICE".equals(columnName)) {
            node.getTable().getDataStore().setItem(node.getRow(),"UNIT_PRICE",node.getValue());
            node.getTable().setDSValue(node.getRow());
        }
        if ("QTY".equals(columnName)) {
            node.getTable().getDataStore().setItem(node.getRow(),"QTY",node.getValue());
            node.getTable().setDSValue(node.getRow());
        }
        double totAmt = this.getTotAmt(node.getTable().getDataStore());
        this.setValue("TOT_AMT",totAmt);
        return false;
    }
    /**
     * ����¼�
     */
    public void onTableClicked(int row){
        TParm parm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
        callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
        //�빺����
        rateofproCode = parm.getValue("RATEOFPRO_CODE");
        if(parm.getData("CHK_DATE")!=null&&parm.getValue("CHK_USER").length()!=0){
            checkFlg = true;
        }else{
            checkFlg = false;
        }
        this.setValue("PURORDER_NO",parm.getData("PURORDER_NO"));
        this.setValue("REQUEST_NO",parm.getData("REQUEST_NO"));
        this.setValue("PURORDER_DEPT", parm.getData("PURORDER_DEPT"));
        this.setValue("PURORDER_USER", parm.getData("PURORDER_USER"));
        this.setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
        this.setValue("PURORDER_DATE", parm.getData("PURORDER_DATE"));
        this.setValue("RES_DELIVERY_DATE", parm.getData("RES_DELIVERY_DATE"));
        this.setValue("TOT_AMT", parm.getData("TOT_AMT"));
        this.setValue("SUP_CODE", parm.getData("SUP_CODE"));
        this.setValue("CHECK_FLG", checkFlg);
        this.setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
        this.setValue("PAYMENT_TERMS", parm.getData("PAYMENT_TERMS"));
        this.setValue("REMARK",parm.getData("REMARK"));
        //�ж��Ƿ��������µ���Ŀ
        if(rateofproCode.equals("D")||rateofproCode.equals("E")||checkFlg){
            this.getTTextFormat("PURORDER_DEPT").setEnabled(false);
            this.getTTextFormat("PURORDER_USER").setEnabled(false);
            this.getTTextFormat("PURORDER_DATE").setEnabled(false);
            this.getTTextFormat("RES_DELIVERY_DATE").setEnabled(false);
            this.getTNumberTextField("TOT_AMT").setEnabled(false);
            this.getTTextFormat("SUP_CODE").setEnabled(false);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(false);
            this.getTTextField("PAYMENT_TERMS").setEnabled(false);
        }else{
            this.getTTextFormat("PURORDER_DEPT").setEnabled(true);
            this.getTTextFormat("PURORDER_USER").setEnabled(true);
            this.getTTextFormat("PURORDER_DATE").setEnabled(true);
            this.getTTextFormat("RES_DELIVERY_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            if(!this.getValueString("REQUEST_NO").equals("FALSE")){
                this.getTTextFormat("SUP_CODE").setEnabled(false);
            }else{
                this.getTTextFormat("SUP_CODE").setEnabled(true);
            }
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTTextField("PAYMENT_TERMS").setEnabled(true);
        }
        //��ʼ��TABLE2
        this.getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("PURORDER_NO")));
        this.getTTable(TABLE2).setDSValue();
        //���һ��
        if(!checkFlg){
            insertRow(TABLE2);
            this.getTTable(TABLE2).setLockColumns("");
        }
        else{
            this.getTTable(TABLE2).setLockColumns("all");
        }
        this.getTTable(TABLE2).setDSValue();
        //״̬
        type="NOREQUEST";
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
        //��ʱҽ������
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
        if((rateofproCode.equals("D")||rateofproCode.equals("E"))){
            this.messageBox("�빺���빺����״̬���������");
            this.getTTable(TABLE2).getDataStore().setItem(rowNum,"DEV_CHN_DESC","");
            this.getTTable(TABLE2).setDSValue(rowNum);
            return;
        }
        if(checkFlg){
            this.messageBox("�빺���Ѿ���˲��������");
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
        this.insertRow(TABLE2);
        this.getTTable(TABLE2).setDSValue();
        this.getTTable(TABLE2).getTable().grabFocus();
        this.getTTable(TABLE2).setSelectedRow(rowNum);
        this.getTTable(TABLE2).setSelectedColumn(4);
        
        
       
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
     * ����
     * @return boolean
     */
    public boolean onSave(){
        //���ϸ��
        TParm checkParm = isCheckMItem();
        if(checkParm.getErrCode()<0){
            this.messageBox(checkParm.getErrText());
            return false;
        }
        //����
        if(this.getValueString("PURORDER_NO").length()==0){
            //�������
            if (!emptyTextCheck("PURORDER_DEPT,PURORDER_USER,RATEOFPRO_CODE,PURORDER_DATE,RES_DELIVERY_DATE,TOT_AMT,FUNDSOU_CODE,SUP_CODE"))
                return false;
            //��������
            String purorderNo = SystemTool.getInstance().getNo("ALL", "DEV",
                "PURORDER_NO", "PURORDER_NO");
            //��������
            TParm purorderMParm = this.getPurOrderM(purorderNo);
            String sqlRequestM[] = new String[]{this.creatRequestSQL(purorderMParm,"INSERT")};
            for(String temp:sqlRequestM){
                System.out.println("temp:"+temp);
            }  
            //TParm result=new TParm(TJDODBTool.getInstance().update(sql)); 
            //TParm parmM = new TParm(TJDODBTool.getInstance().update(creatRequestSQL(purorderMParm,"INSERT")));
            //����ϸ�� 
            Timestamp timestamp = StringTool.getTimestamp(new Date());
            TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
            int rowCount = dateStore.rowCount();
            int seqNo = 1;
            for(int i=0;i<rowCount;i++){
                if(!dateStore.isActive(i)) 
                    continue;
                dateStore.setItem(i,"PURORDER_NO",purorderNo);
                dateStore.setItem(i,"SEQ_NO",seqNo);
                dateStore.setItem(i,"OPT_USER",Operator.getID());
                dateStore.setItem(i,"OPT_DATE",timestamp);
                dateStore.setItem(i,"OPT_TERM",Operator.getIP());
                seqNo++;
            }  
            sqlRequestM = StringUtil.getInstance().copyArray(dateStore.getUpdateSQL(),sqlRequestM);
            if("HAVEREQUEST".equals(type)){  
                String[] updateSql = new String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='C' WHERE REQUEST_NO='"+this.getValueString("REQUEST_NO")+"'"};
                sqlRequestM = StringUtil.getInstance().copyArray(updateSql,sqlRequestM);
            }
            for(String sql:sqlRequestM){
                System.out.println("sql:"+sql);
            }
            TParm sqlParm = new TParm();
            sqlParm.setData("SQL",sqlRequestM); 
            TParm actionParm = TIOM_AppServer.executeAction(actionName,
               "saveDevRequest", sqlParm);
            System.out.println("actionParm"+actionParm); 
           if(actionParm.getErrCode()<0){  
               this.messageBox("����ʧ�ܣ�");
               return false;
           }
           this.messageBox("����ɹ���");
           this.setValue("PURORDER_NO",purorderNo);
        }else{
            //�������
            if ( (rateofproCode.equals("D") || rateofproCode.equals("E"))) {
                this.messageBox("�빺���빺����״̬�������޸ģ�");
                return false;
            }
            this.getTTable(TABLE2).acceptText();
            //�빺����
            TParm requestMParm = this.getPurOrderM(this.getValueString("PURORDER_NO"));
            String sqlRequestM[] = new String[]{this.creatRequestSQL(requestMParm,"UPDATE")};
            for(String temp:sqlRequestM){
                System.out.println("temp:"+temp);
            }
            //�빺ϸ��
            Timestamp timestamp = StringTool.getTimestamp(new Date());
            TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
            int newRow[] = dateStore.getNewRows();
            int rowCount = dateStore.rowCount();
            int seqNo = dateStore.rowCount()-newRow.length;
            for(int row:newRow){
                if(!dateStore.isActive(row))
                    continue;
                dateStore.setItem(row,"PURORDER_NO",this.getValueString("PURORDER_NO"));
                dateStore.setItem(row,"SEQ_NO",seqNo);
                dateStore.setItem(row,"OPT_USER",Operator.getID());
                dateStore.setItem(row,"OPT_DATE",timestamp);
                dateStore.setItem(row,"OPT_TERM",Operator.getIP());
                seqNo++;

            }
            //ɾ��
            for(int i=rowCount-1;i>=0;i--){
                if(!dateStore.isActive(i)&&dateStore.getItemString(i,"PURORDER_NO").length()!=0){
                    dateStore.setActive(i,true);
                    dateStore.deleteRow(i);
                }
            }
            sqlRequestM = StringUtil.getInstance().copyArray(dateStore.getUpdateSQL(),sqlRequestM);
            for(String sql:sqlRequestM){
                System.out.println("sql:"+sql);
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
    public TParm getPurOrderM(String purorderNo){
        TParm result = new TParm();
        //�빺����
        result.setData("REQUEST_NO",this.getValueString("REQUEST_NO").length()==0?"FALSE":this.getValueString("REQUEST_NO"));
        //��������
        result.setData("PURORDER_NO",purorderNo);
        //��������
        result.setData("PURORDER_DATE",this.getValue("PURORDER_DATE"));
        //��������
        result.setData("PURORDER_DEPT",this.getValue("PURORDER_DEPT"));
        //������Ա
        result.setData("PURORDER_USER",this.getValue("PURORDER_USER"));
        //��������
        result.setData("RATEOFPRO_CODE",this.getValue("RATEOFPRO_CODE"));
        //Ԥ��������
        result.setData("RES_DELIVERY_DATE",this.getValue("RES_DELIVERY_DATE"));
        //�����ܼ�
        result.setData("TOT_AMT",this.getValue("TOT_AMT"));
        //�ʽ���Դ
        result.setData("FUNDSOU_CODE",this.getValue("FUNDSOU_CODE"));
        //��Ӧ����
        result.setData("SUP_CODE",this.getValue("SUP_CODE"));
        //��������
        result.setData("PAYMENT_TERMS",this.getValue("PAYMENT_TERMS"));
        //��ע
        result.setData("REMARK",this.getValue("REMARK"));
        if(this.getTCheckBox("CHECK_FLG").isSelected()){
            //�����Ա
            result.setData("CHK_USER", Operator.getID());
            //���ʱ��
            result.setData("CHK_DATE", StringTool.getTimestamp(new Date()));
        }else{
            //�����Ա
            result.setData("CHK_USER", this.getValue("CHK_USER"));
            //���ʱ��
            result.setData("CHK_DATE", this.getValue("CHK_DATE"));
        }
        return result;
    }
    /**
     * ����DEV_PURORDERM�������� 
     * @param parm TParm
     * @return String
     */
    public String creatRequestSQL(TParm parm,String type){
        String sql = "";
        if("INSERT".equals(type)){
            sql = "INSERT INTO DEV_PURORDERM (PURORDER_NO,REQUEST_NO,PURORDER_DATE,RES_DELIVERY_DATE,PURORDER_DEPT,PURORDER_USER,FUNDSOU_CODE,RATEOFPRO_CODE,SUP_CODE,TOT_AMT,PAYMENT_TERMS,REMARK,CHK_USER,CHK_DATE,OPT_USER,OPT_DATE,OPT_TERM ) VALUES ( '"+parm.getValue("PURORDER_NO")+"', '"+parm.getValue("REQUEST_NO")+"', TO_DATE('"+StringTool.getString(parm.getTimestamp("PURORDER_DATE"),"yyyyMMddHHmmss")+"', 'YYYYMMDDHH24MISS'), TO_DATE('"+StringTool.getString(parm.getTimestamp("RES_DELIVERY_DATE"),"yyyyMMddHHmmss")+"', 'YYYYMMDDHH24MISS'), '"+parm.getValue("PURORDER_DEPT")+"', '"+parm.getValue("PURORDER_USER")+"', '"+parm.getValue("FUNDSOU_CODE")+"', '"+parm.getValue("RATEOFPRO_CODE")+"', '"+parm.getValue("SUP_CODE")+"', '"+parm.getValue("TOT_AMT")+"', '"+parm.getValue("PAYMENT_TERMS")+"', '"+parm.getValue("REMARK")+"', '"+parm.getValue("CHK_USER")+"', TO_DATE('"+StringTool.getString(parm.getTimestamp("CHK_DATE"),"yyyyMMddHHmmss")+"', 'YYYYMMDDHH24MISS'), '"+Operator.getID()+"',SYSDATE, '"+Operator.getIP()+"' ) ";
        }else{
            sql = "UPDATE DEV_PURORDERM SET REQUEST_NO='"+parm.getValue("REQUEST_NO")+"',PURORDER_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("PURORDER_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),RES_DELIVERY_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("RES_DELIVERY_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),PURORDER_DEPT='"+parm.getValue("PURORDER_DEPT")+"',PURORDER_USER='"+parm.getValue("PURORDER_USER")+"',FUNDSOU_CODE='"+parm.getValue("FUNDSOU_CODE")+"',RATEOFPRO_CODE='"+parm.getValue("RATEOFPRO_CODE")+"',SUP_CODE='"+parm.getValue("SUP_CODE")+"',TOT_AMT='"+parm.getValue("TOT_AMT")+"',PAYMENT_TERMS='"+parm.getValue("PAYMENT_TERMS")+"',REMARK='"+parm.getValue("REMARK")+"',CHK_USER='"+parm.getValue("CHK_USER")+"',CHK_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("CHK_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' WHERE PURORDER_NO='"+parm.getValue("PURORDER_NO")+"'";
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
        if (rowCount <= 0) {
            result.setErrCode( -1);
            result.setErrText("����д�豸��ϸ���ϣ�");
            return result;
        }

        for(int i=0;i<rowCount;i++){
            if(!dateStore.isActive(i))
                continue;
            if(dateStore.getItemDouble(i,"UNIT_PRICE")<=0){
                result.setErrCode(-1);
                result.setErrText("�豸��Ϊ:"+dateStore.getItemString(i,"DEV_CHN_DESC")+"����Ŀ����д�ο��۸�");
                return result;
            }
            if(dateStore.getItemInt(i,"QTY")<=0){
                result.setErrCode(-2);
                result.setErrText("�豸��Ϊ:"+dateStore.getItemString(i,"DEV_CHN_DESC")+"����Ŀ����д������");
                return result;
            }
        }
        return result;
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
     * ����TABLE������
     * @param tag String
     * @param queryParm TParm
     * @return TDataStore
     */
    public TDataStore getTableTDataStore(String tag){
        TDataStore dateStore = new TDataStore();
        if(tag.equals("TABLE1")){
            String sql="SELECT * FROM DEV_PURORDERM";
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
                    sql+=" PURORDER_DATE BETWEEN TO_DATE('"+queryParm.getValue("QSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("QEND_DATE")+"','YYYYMMDD')";
                    count++;
                    continue;
                }
                //Ԥ����������
                if(temp.equals("YSTART_DATE")){
                    if(count>0)
                        sql+=" AND ";
                    sql+=" RES_DELIVERY_DATE BETWEEN TO_DATE('"+queryParm.getValue("YSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("YEND_DATE")+"','YYYYMMDD')";
                    count++;
                     continue;
                }
                if(count>0)
                    sql+=" AND ";
                sql+=temp+"='"+queryParm.getValue(temp)+"' ";
                count++;
            }
            System.out.println("sql:"+sql);
            dateStore.setSQL(sql);
            dateStore.retrieve();
        }
        if(tag.equals("TABLE2")){
            String pororderNo = this.getValueString("QPURORDER_NO");
            DevPurorderDDataStore devPurOrderDataStore = new DevPurorderDDataStore();
            devPurOrderDataStore.setPurorderNo(pororderNo);
            devPurOrderDataStore.onQuery();
            //�ο��۸��ܼ۸�
            double totAmt =getTotAmt(devPurOrderDataStore);
            this.setValue("TOT_AMT",totAmt);
            return devPurOrderDataStore;
        }
        return dateStore;
    }
    /**
     * �õ���ϸ������
     * @param requestNo String
     * @return DevBaseDataStore
     */
    public DevPurorderDDataStore getRequestDData(String requestNo){
        String qrequestNo = requestNo;
        DevPurorderDDataStore devPurorderDataStore = new DevPurorderDDataStore();
        devPurorderDataStore.setPurorderNo(qrequestNo);
        devPurorderDataStore.onQuery();
        //�ο��۸��ܼ۸�
        double totAmt =getTotAmt(devPurorderDataStore);
        if(this.getValueDouble("TOT_AMT")==0){
            this.setValue("TOT_AMT",totAmt);
        }
        return devPurorderDataStore;
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
        String deptCode = this.getValueString("QPURORDER_DEPT");
        String operator = this.getValueString("QPURORDER_USER");
        String reteoptro = this.getValueString("QRATEOFPRO_CODE");
        String requestNo = this.getValueString("QPURORDER_NO");
        String ydDateStart = StringTool.getString((Timestamp)this.getValue("QUSERSTART_DATE"),"yyyyMMdd");
        String ydDateEnd = StringTool.getString((Timestamp)this.getValue("QUSEEND_DATE"),"yyyyMMdd");
        String fundsouCode = this.getValueString("QFUNDSOU_CODE");
        if(startDate.length()!=0)
            result.setData("QSTART_DATE",startDate);
        if(endDate.length()!=0)
            result.setData("QEND_DATE",endDate);
        if(ydDateStart.length()!=0)
            result.setData("YSTART_DATE",ydDateStart);
        if(ydDateEnd.length()!=0)
            result.setData("YEND_DATE",ydDateEnd);
        if(deptCode.length()!=0)
            result.setData("PURORDER_DEPT",deptCode);
        if(operator.length()!=0)
            result.setData("PURORDER_USER",operator);
        if(reteoptro.length()!=0)
            result.setData("RATEOFPRO_CODE",reteoptro);
        if(requestNo.length()!=0)
            result.setData("PURORDER_NO",requestNo);
        if(fundsouCode.length()!=0)
            result.setData("FUNDSOU_CODE",fundsouCode);
        return result;
    }
    /**
     * ���
     */
    public void onClear(){
        //�ж��Ƿ񱣴�
        //���
        this.clearValue("QPURORDER_NO;QFUNDSOU_CODE;PURORDER_NO;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;REMARK");
        /**
         * ��ʼ��ҳ��
         */
        onInitPage();
        /**
        * ��ʼ��Ȩ��
        */
       onInitPopeDem();
       /**
        * �빺��״̬
        */
       type="NOREQUEST";
       this.checkFlg = false;

    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        this.clearValue("PURORDER_NO;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;PAYMENT_TERMS;REMARK");
        //��ʼ��TABLE1
        this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
        this.getTTable(TABLE1).setDSValue();
        //��ʼ��TABLE2
        this.getTTable(TABLE2).setDataStore(getRequestDData(""));
        this.getTTable(TABLE2).setDSValue();
        //���һ��
        insertRow(TABLE2);
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
     * 1��һ��Ȩ�ޣ�������Ա��:�������ҿ�����Ա,�빺����Ĭ��Ϊ����������������׼ GeneralPermissions
     * 2���ϲ��빺�����豸��Ա�������ҿ�����Ա������׼ ClerksPermissions
     * 3����ˣ��豸�Ƴ��� AuditPermissions
     */
    public void onInitPopeDem(){
        //���
        if(this.getPopedem("AuditPermissions")){
            this.getTTextFormat("QPURORDER_DEPT").setEnabled(true);
            this.getTTextFormat("QPURORDER_USER").setEnabled(true);
            this.getTTextFormat("PURORDER_DEPT").setEnabled(true);
            this.getTTextFormat("PURORDER_USER").setEnabled(true);
            this.getTTextFormat("PURORDER_DATE").setEnabled(true);
            this.getTTextFormat("RES_DELIVERY_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTCheckBox("CHECK_FLG").setEnabled(true);
            this.getTTextFormat("SUP_CODE").setEnabled(true);
            this.getTTextField("PAYMENT_TERMS").setEnabled(true);
            popedemType = "2";
            return;
        }
        //һ��Ȩ��
        if(this.getPopedem("GeneralPermissions")){
            this.getTTextFormat("QPURORDER_DEPT").setEnabled(false);
            this.getTTextFormat("QPURORDER_USER").setEnabled(false);
            this.getTTextFormat("PURORDER_DEPT").setEnabled(false);
            this.getTTextFormat("PURORDER_USER").setEnabled(false);
            this.getTTextFormat("PURORDER_DATE").setEnabled(true);
            this.getTTextFormat("RES_DELIVERY_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTCheckBox("CHECK_FLG").setEnabled(false);
            this.getTTextFormat("SUP_CODE").setEnabled(true);
            this.getTTextField("PAYMENT_TERMS").setEnabled(true);
            popedemType = "1";
            return;
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
     * �õ�TTextField
     * @return TTextFormat
     */
    public TTextField getTTextField(String tag){
        return (TTextField)this.getComponent(tag);
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
        if((rateofproCode.equals("D")||rateofproCode.equals("E"))){
            this.messageBox("�빺���빺����״̬������ɾ����");
            return;
        }
        if(checkFlg){
            this.messageBox("�빺�����Ѿ���˲�����ɾ����");
            return;
        }
        if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ��?", this.YES_NO_OPTION) != 0)
            return;
        TParm requestParm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
        String requestNo = requestParm.getValue("REQUEST_NO");
        this.getTTable(TABLE1).getDataStore().deleteRow(row);
        int rowCount = this.getTTable(TABLE2).getDataStore().rowCount();
        for(int i=rowCount-1;i>=0;i--){
            if(!this.getTTable(TABLE2).getDataStore().isActive(i)&&this.getTTable(TABLE2).getDataStore().getItemString(i,"PURORDER_NO").length()==0){
                continue;
            }
            this.getTTable(TABLE2).getDataStore().deleteRow(i);
        }
        this.getTTable(TABLE1).setDSValue();
        this.getTTable(TABLE2).setDSValue();
        String arraySqlTable1[] = this.getTTable(TABLE1).getUpdateSQL();
        String arraySqlTable2[] = this.getTTable(TABLE2).getUpdateSQL();
        String arraySql[] = StringUtil.getInstance().copyArray(arraySqlTable1,arraySqlTable2);
        if(!requestNo.equals("FALSE")){ 
            String[] updateSql = new String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='B' WHERE REQUEST_NO='"+requestNo+"'"};
            arraySql = StringUtil.getInstance().copyArray(updateSql,arraySql);
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
     * ��ӡ�빺��
     */
    public void onPrint(){
        int row = this.getTTable(TABLE1).getSelectedRow();
        if(row<0){
            this.messageBox("��ѡ��Ҫ��ӡ�����ݣ�");
            return;
        }
        TParm parm = getPurOrderM(this.getValueString("PURORDER_NO"));
        parm.setData("TITLE_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        parm.setData("FORMS_NAME","TEXT","������");
        TParm printDataParm = new TParm();
        TParm tableParm = this.getTTable(TABLE2).getDataStore().getBuffer(TDataStore.PRIMARY);
        System.out.println("tableParm"+tableParm);
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
        parm.setData("DEV_PURORDER", printDataParm.getData());
        System.out.println("parm"+parm);
        this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevPurOrderForms.jhw",parm, false);
    }
    /**
     * ���ɶ�����
     */
    public void onCreatPuro(){
    	//fux modify
        if(this.checkFlg){
            this.messageBox("�Ѿ���˲��������ɶ�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("POPEDEM",popedemType);
        Object obj = this.openDialog("%ROOT%\\config\\dev\\PurordermUI.x",parm);
        if(obj==null){
            return;
        }
        TParm action = (TParm)obj;
        this.setValue("REQUEST_NO",action.getValue("REQUEST_NO"));
        this.setValue("SUP_CODE",action.getValue("SUP_CODE"));
        this.setValue("SUP_SALES1",action.getValue("SUP_SALES1"));
        this.setValue("SUP_SALES1_TEL",action.getValue("SUP_SALES1_TEL"));
        this.setValue("PAYMENT_TERMS",action.getValue("PAYMENT_TERMS"));
        this.getTTextFormat("SUP_CODE").setEnabled(false);
        TParm requestDParm = new TParm(this.getDBTool().select("SELECT * FROM DEV_PURCHASED WHERE REQUEST_NO='"+action.getValue("REQUEST_NO")+"'"));
        int rowCount = requestDParm.getCount();
        for(int i=0;i<rowCount;i++){
            TParm temp = requestDParm.getRow(i);
            this.popReturn(temp);
        }
        double totAmt = this.getTotAmt(this.getTTable(TABLE2).getDataStore());
        if(action.getDouble("TOT_AMT")!=totAmt){
            if(this.messageBox("��ʾ��Ϣ","���۸��붩���ܼ۸�����Ƿ�ȡ����",this.YES_NO_OPTION)!=0){
                this.setValue("TOT_AMT",totAmt);
            }else{
                this.setValue("TOT_AMT",action.getValue("TOT_AMT"));
            }
        }
        type="HAVEREQUEST";
    }
    /**
     * �õ��༭��
     * @return int
     */
    public int getEditRow(){
        int row = 0;
        int rowCount = this.getTTable(TABLE2).getDataStore().rowCount();
        for(int i=0;i<rowCount;i++){
            if(!this.getTTable(TABLE2).getDataStore().isActive(i))
                return i;
        }
        return row;
    }
    /**
     * ���ܷ���ֵ����
     * @param tag String
     * @param obj Object
     */
    public void popReturn(TParm action){
        this.getTTable(TABLE2).acceptText();
        int rowNum = getEditRow();
        //�ж��Ƿ��������µ���Ŀ
        if((rateofproCode.equals("D")||rateofproCode.equals("E"))){
            this.messageBox("�빺���빺����״̬���������");
            this.getTTable(TABLE2).getDataStore().setItem(rowNum,"DEV_CHN_DESC","");
            this.getTTable(TABLE2).setDSValue(rowNum);
            return;
        }
        if(checkFlg){
            this.messageBox("�빺���Ѿ���˲��������");
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
        this.insertRow(TABLE2);
        this.getTTable(TABLE2).setDSValue();
    }
}
