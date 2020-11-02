package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import java.util.Date;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import jdo.dev.DevBaseDataStore;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import com.dongyang.ui.TCheckBox;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TNumberTextField;
import jdo.util.Manager;

/**
 * <p>Title: �빺��ҵ</p>
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
public class DevRequestMControl extends TControl {
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
    public void onCheckBoxValue(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = this.getTTable(TABLE2).getDataStoreColumnName(col);
        if ("#ACTIVE#".equals(columnName)) {
            double totAmt = this.getTotAmt(table.getDataStore());
            this.setValue("TOT_AMT", totAmt);
        }
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
        this.setValue("DETAILED_USE", parm.getData("DETAILED_USE"));
        this.setValue("REQUEST_REASON", parm.getData("REQUEST_REASON"));
        this.setValue("BENEFIT_PROVE",parm.getData("BENEFIT_PROVE"));
        this.setValue("REMARK",parm.getData("REMARK"));
        //�ж��Ƿ��������µ���Ŀ
        if(rateofproCode.equals("C")||rateofproCode.equals("D")||rateofproCode.equals("E")||checkFlg){
            this.getTTextFormat("REQUEST_DEPT").setEnabled(false);
            this.getTTextFormat("REQUEST_USER").setEnabled(false);
            this.getTTextFormat("REQUEST_DATE").setEnabled(false);
            this.getTTextFormat("USE_DATE").setEnabled(false);
            this.getTNumberTextField("TOT_AMT").setEnabled(false);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(false);
            this.getTComboBox("PURTYPE_CODE").setEnabled(false);
            this.getTComboBox("DEVUSE_CODE").setEnabled(false);
        }else{
            this.getTTextFormat("REQUEST_DEPT").setEnabled(true);
            this.getTTextFormat("REQUEST_USER").setEnabled(true);
            this.getTTextFormat("REQUEST_DATE").setEnabled(true);
            this.getTTextFormat("USE_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTComboBox("PURTYPE_CODE").setEnabled(true);
            this.getTComboBox("DEVUSE_CODE").setEnabled(true);
        }
        //��ʼ��TABLE2
        this.getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("REQUEST_NO")));
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
        if((rateofproCode.equals("C")||rateofproCode.equals("D")||rateofproCode.equals("E"))){
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
        //�������
        if (!emptyTextCheck("REQUEST_DEPT,REQUEST_USER,RATEOFPRO_CODE,REQUEST_DATE,USE_DATE,TOT_AMT,FUNDSOU_CODE,PURTYPE_CODE,DEVUSE_CODE"))
            return false;
        //���ϸ��
        TParm checkParm = isCheckMItem();
        if(checkParm.getErrCode()<0){
            this.messageBox(checkParm.getErrText());
            return false;
        }
        //����
        if(this.getValueString("REQUEST_NO").length()==0){
            //�빺����
            String requestNo = SystemTool.getInstance().getNo("ALL", "DEV",
                "REQUEST_NO", "REQUEST_NO");
            //�빺����
            TParm requestMParm = this.getRequestM(requestNo);
            String sqlRequestM[] = new String[]{this.creatRequestSQL(requestMParm,"INSERT")};
            for(String temp:sqlRequestM){
                System.out.println("temp:"+temp);
            }
            //�빺ϸ��
            Timestamp timestamp = StringTool.getTimestamp(new Date());
            TDataStore dateStore = this.getTTable(TABLE2).getDataStore();
            int rowCount = dateStore.rowCount();
            int seqNo = 1;
            for(int i=0;i<rowCount;i++){
                if(!dateStore.isActive(i))
                    continue;
                dateStore.setItem(i,"REQUEST_NO",requestNo);
                dateStore.setItem(i,"SEQ_NO",seqNo);
                dateStore.setItem(i,"OPT_USER",Operator.getID());
                dateStore.setItem(i,"OPT_DATE",timestamp);
                dateStore.setItem(i,"OPT_TERM",Operator.getIP());
                seqNo++;
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
           this.setValue("REQUEST_NO",requestNo);
        }else{
            //�������
            if ( (rateofproCode.equals("B") || rateofproCode.equals("C") ||
                  rateofproCode.equals("D") || rateofproCode.equals("E"))) {
                this.messageBox("�빺���빺����״̬�������޸ģ�");
                return false;
            }
            this.getTTable(TABLE2).acceptText();
            //�빺����
            TParm requestMParm = this.getRequestM(this.getValueString("REQUEST_NO"));
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
                dateStore.setItem(row,"REQUEST_NO",this.getValueString("REQUEST_NO"));
                dateStore.setItem(row,"SEQ_NO",seqNo);
                dateStore.setItem(row,"OPT_USER",Operator.getID());
                dateStore.setItem(row,"OPT_DATE",timestamp);
                dateStore.setItem(row,"OPT_TERM",Operator.getIP());
                seqNo++;

            }
            //ɾ��
            for(int i=rowCount-1;i>=0;i--){
                if(!dateStore.isActive(i)&&dateStore.getItemString(i,"REQUEST_NO").length()!=0){
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
     * �õ��빺��������
     * @return TParm
     */
    public TParm getRequestM(String requestNo){
        TParm result = new TParm();
        //�빺����
        result.setData("REQUEST_NO",requestNo);
        //�빺����
        result.setData("REQUEST_DATE",this.getValue("REQUEST_DATE"));
        //�빺����
        result.setData("REQUEST_DEPT",this.getValue("REQUEST_DEPT"));
        //�빺��Ա
        result.setData("REQUEST_USER",this.getValue("REQUEST_USER"));
        //�빺����
        result.setData("RATEOFPRO_CODE",this.getValue("RATEOFPRO_CODE"));
        //Ԥ��ʹ����
        result.setData("USE_DATE",this.getValue("USE_DATE"));
        //�빺�ܼ�
        result.setData("TOT_AMT",this.getValue("TOT_AMT"));
        //�ʽ���Դ
        result.setData("FUNDSOU_CODE",this.getValue("FUNDSOU_CODE"));
        //�빺���
        result.setData("PURTYPE_CODE",this.getValue("PURTYPE_CODE"));
        //�豸��;
        result.setData("DEVUSE_CODE",this.getValue("DEVUSE_CODE"));
        //��ϸ��;
        result.setData("DETAILED_USE",this.getValue("DETAILED_USE"));
        //��������
        result.setData("REQUEST_REASON",this.getValue("REQUEST_REASON"));
        //Ч����֤
        result.setData("BENEFIT_PROVE",this.getValue("BENEFIT_PROVE"));
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
     * ����DEV_REQUESTM��������
     * @param parm TParm
     * @return String
     */
    public String creatRequestSQL(TParm parm,String type){
        String sql = "";
        if("INSERT".equals(type)){
            sql = "INSERT INTO DEV_REQUESTM(REQUEST_NO,REQUEST_DATE,REQUEST_DEPT,REQUEST_USER,RATEOFPRO_CODE,USE_DATE,TOT_AMT,FUNDSOU_CODE,PURTYPE_CODE,DEVUSE_CODE,DETAILED_USE,REQUEST_REASON,BENEFIT_PROVE,REMARK,CHK_USER,CHK_DATE,OPT_USER,OPT_DATE,OPT_TERM)VALUES('"+parm.getData("REQUEST_NO")+"',TO_DATE('"+StringTool.getString(parm.getTimestamp("REQUEST_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),'"+parm.getData("REQUEST_DEPT")+"','"+parm.getData("REQUEST_USER")+"','"+parm.getData("RATEOFPRO_CODE")+"',TO_DATE('"+StringTool.getString(parm.getTimestamp("USE_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),'"+parm.getData("TOT_AMT")+"','"+parm.getData("FUNDSOU_CODE")+"','"+parm.getData("PURTYPE_CODE")+"','"+parm.getData("DEVUSE_CODE")+"','"+parm.getData("DETAILED_USE")+"','"+parm.getData("REQUEST_REASON")+"','"+parm.getData("BENEFIT_PROVE")+"','"+parm.getData("REMARK")+"','','','"+Operator.getID()+"',SYSDATE,'"+Operator.getIP()+"')";
        }else{
            sql = "UPDATE DEV_REQUESTM SET REQUEST_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("REQUEST_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),REQUEST_DEPT='"+parm.getValue("REQUEST_DEPT")+"',REQUEST_USER='"+parm.getValue("REQUEST_USER")+"',RATEOFPRO_CODE='"+parm.getValue("RATEOFPRO_CODE")+"',USE_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("USE_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),TOT_AMT='"+parm.getValue("TOT_AMT")+"',FUNDSOU_CODE='"+parm.getValue("FUNDSOU_CODE")+"',PURTYPE_CODE='"+parm.getValue("PURTYPE_CODE")+"',DEVUSE_CODE='"+parm.getValue("DEVUSE_CODE")+"',DETAILED_USE='"+parm.getValue("DETAILED_USE")+"',REQUEST_REASON='"+parm.getValue("REQUEST_REASON")+"',BENEFIT_PROVE='"+parm.getValue("BENEFIT_PROVE")+"',REMARK='"+parm.getValue("REMARK")+"',CHK_USER='"+parm.getValue("CHK_USER")+"',CHK_DATE=TO_DATE('"+StringTool.getString(parm.getTimestamp("CHK_DATE"),"yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS'),OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' WHERE REQUEST_NO='"+parm.getValue("REQUEST_NO")+"'";
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
                result.setErrCode(-2);
                result.setErrText("�豸��Ϊ:"+dateStore.getItemString(i,"DEV_CHN_DESC")+"����Ŀ����д�ο��۸�");
                return result;
            }
            if(dateStore.getItemInt(i,"QTY")<=0){
                result.setErrCode(-3);
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
            String sql="SELECT * FROM DEV_REQUESTM";
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
            System.out.println("sql:"+sql);
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
        //�ж��Ƿ񱣴�
        //���
        this.clearValue("QREQUEST_NO;QFUNDSOU_CODE;QPURTYPE_CODE;QDEVUSE_CODE;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
        /**
         * ��ʼ��ҳ��
         */
        onInitPage();
        /**
        * ��ʼ��Ȩ��
        */
       onInitPopeDem();
       this.checkFlg = false;

    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        this.clearValue("REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
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
            this.getTTextFormat("QDEPT_CODE").setEnabled(true);
            this.getTTextFormat("QOPERATOR").setEnabled(true);
            this.getTTextFormat("REQUEST_DEPT").setEnabled(true);
            this.getTTextFormat("REQUEST_USER").setEnabled(true);
            this.getTTextFormat("REQUEST_DATE").setEnabled(true);
            this.getTTextFormat("USE_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTComboBox("PURTYPE_CODE").setEnabled(true);
            this.getTComboBox("DEVUSE_CODE").setEnabled(true);
            this.getTCheckBox("CHECK_FLG").setEnabled(true);
            return;
        }
        //һ��Ȩ��
        if(this.getPopedem("GeneralPermissions")){
            this.getTTextFormat("QDEPT_CODE").setEnabled(false);
            this.getTTextFormat("QOPERATOR").setEnabled(false);
            this.getTTextFormat("REQUEST_DEPT").setEnabled(false);
            this.getTTextFormat("REQUEST_USER").setEnabled(false);
            this.getTTextFormat("REQUEST_DATE").setEnabled(true);
            this.getTTextFormat("USE_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTComboBox("PURTYPE_CODE").setEnabled(true);
            this.getTComboBox("DEVUSE_CODE").setEnabled(true);
            this.getTCheckBox("CHECK_FLG").setEnabled(false);
        }
        //�ϲ��빺��
        if(this.getPopedem("ClerksPermissions")){
            this.getTTextFormat("REQUEST_DEPT").setEnabled(false);
            this.getTTextFormat("REQUEST_USER").setEnabled(false);
            this.getTTextFormat("REQUEST_DATE").setEnabled(true);
            this.getTTextFormat("USE_DATE").setEnabled(true);
            this.getTNumberTextField("TOT_AMT").setEnabled(true);
            this.getTComboBox("FUNDSOU_CODE").setEnabled(true);
            this.getTComboBox("PURTYPE_CODE").setEnabled(true);
            this.getTComboBox("DEVUSE_CODE").setEnabled(true);
            this.getTCheckBox("CHECK_FLG").setEnabled(false);
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
        int row = this.getTTable(TABLE1).getSelectedRow();
        if(row<0){
            this.messageBox("��ѡ��Ҫɾ�������ݣ�");
            return;
        }
        if((rateofproCode.equals("B")||rateofproCode.equals("C")||rateofproCode.equals("D")||rateofproCode.equals("E"))){
            this.messageBox("�빺���빺����״̬������ɾ����");
            return;
        }
        if(checkFlg){
            this.messageBox("�빺�����Ѿ���˲�����ɾ����");
            return;
        }
        if (messageBox("��ʾ��Ϣ", "�Ƿ�ɾ��?", this.YES_NO_OPTION) != 0)
            return;
        this.getTTable(TABLE1).getDataStore().deleteRow(row);
        int rowCount = this.getTTable(TABLE2).getDataStore().rowCount();
        for(int i=rowCount-1;i>=0;i--){
            if(!this.getTTable(TABLE2).getDataStore().isActive(i)&&this.getTTable(TABLE2).getDataStore().getItemString(i,"REQUEST_NO").length()==0){
                continue;
            }
            this.getTTable(TABLE2).getDataStore().deleteRow(i);
        }
        this.getTTable(TABLE1).setDSValue();
        this.getTTable(TABLE2).setDSValue();
        String arraySqlTable1[] = this.getTTable(TABLE1).getUpdateSQL();
        String arraySqlTable2[] = this.getTTable(TABLE2).getUpdateSQL();
        String arraySql[] = StringUtil.getInstance().copyArray(arraySqlTable1,arraySqlTable2);
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
        TParm parm = getRequestM(this.getValueString("REQUEST_NO"));
        parm.setData("TITLE_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        parm.setData("FORMS_NAME","TEXT","�豸�빺��");
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
        parm.setData("DEV_REQUESTD", printDataParm.getData());
        System.out.println("parm"+parm);
        this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevRequestForms.jhw",parm, false);
    }
}
