package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ������</p>
 *
 * <p>Description:������ </p> 
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PurordermControl extends TControl {
    /**
     * TABLE
     */
    private static String TABLE = "TABLE";
    /**
     * Ȩ�޴���
     */
    private String popedemType="1";
    public void onInit() {
        this.initPage();
        this.initPopedem();
        this.initEven();
    }
    public void initPopedem(){
        Object obj = this.getParameter();
        if(obj!=null){
            popedemType = ((TParm)obj).getValue("POPEDEM");
        }
    }
    /**
     * ��ʼ���¼�
     */
    public void initEven(){
        callFunction("UI|" + TABLE + "|addEventListener",
                     TABLE + "->" + TTableEvent.CLICKED, this, "onTableClicked");
    }
    /**
     * ����¼�
     * @param row int
     */
    public void onTableClicked(int row){
        TParm parm = this.getTTable(TABLE).getDataStore().getRowParm(row);
        this.setValue("REQUEST_NO",parm.getData("REQUEST_NO"));
        this.setValue("REQUEST_DEPT", parm.getData("REQUEST_DEPT"));
        this.setValue("REQUEST_USER", parm.getData("REQUEST_USER"));
        this.setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
        this.setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
        this.setValue("PURTYPE_CODE", parm.getData("PURTYPE_CODE"));
        this.setValue("DEVUSE_CODE", parm.getData("DEVUSE_CODE"));
    }
    public void initPage() {
        //���ҡ���Ա
        this.setValue("REQUEST_DEPT", Operator.getDept());
        this.setValue("REQUEST_USER", Operator.getID());
        //�빺����
        this.setValue("RATEOFPRO_CODE", "B");
        //����
        Timestamp startDate = StringTool.getTimestamp(StringTool.getString(
            StringTool.getTimestamp(new Date()), "yyyy/MM/dd"), "yyyy/MM/dd");
        //����
        Timestamp endDate = StringTool.getTimestamp("9999/12/31", "yyyy/MM/dd");
        //��ѯ�빺��������
        this.setValue("QSTART_DATE", startDate);
        //��ѯ�빺��������
        this.setValue("QEND_DATE", endDate);
        //Ԥ��ʹ��������
        this.setValue("QUSERSTART_DATE", startDate);
        //Ԥ��ʹ��������
        this.setValue("QUSEEND_DATE", endDate);
        //��ʼ��TABLE1
       this.getTTable(TABLE).setDataStore(getTableTDataStore());
       this.getTTable(TABLE).setDSValue();
    }

    /**
     * ����TABLE������
     * @param tag String
     * @param queryParm TParm
     * @return TDataStore
     */
    public TDataStore getTableTDataStore() {
        TDataStore dateStore = new TDataStore();  
        String sql = "SELECT * FROM DEV_PURCHASEM";  
        TParm queryParm = this.getTable1QueryParm();
        String columnName[] = queryParm.getNames();
        if (columnName.length > 0)
            sql += " WHERE ";
        int count = 0;
        for (String temp : columnName) {
            if (temp.equals("QEND_DATE"))
                continue;
            if (temp.equals("YEND_DATE"))
                continue;
            //�빺����
            if (temp.equals("QSTART_DATE")) {
                if (count > 0)
                    sql += " AND ";
                sql += " REQUEST_DATE BETWEEN TO_DATE('" +
                    queryParm.getValue("QSTART_DATE") +
                    "','YYYYMMDD') AND TO_DATE('" +
                    queryParm.getValue("QEND_DATE") + "','YYYYMMDD')";
                count++;
                continue;
            }
            //Ԥʹ�ö�����
            if (temp.equals("YSTART_DATE")) {
                if (count > 0)
                    sql += " AND ";
                sql += " USE_DATE BETWEEN TO_DATE('" +
                    queryParm.getValue("YSTART_DATE") +
                    "','YYYYMMDD') AND TO_DATE('" +
                    queryParm.getValue("YEND_DATE") + "','YYYYMMDD')";
                count++;
                continue;
            }
            if (count > 0)
                sql += " AND ";
            sql += temp + "='" + queryParm.getValue(temp) + "' ";
            count++;
        }
        System.out.println("sql:"+sql);
        dateStore.setSQL(sql);
        dateStore.retrieve();
        return dateStore;
    }

    /**
     * �õ�TABLE1�Ĳ�ѯ����
     * @return TParm
     */
    public TParm getTable1QueryParm() {
        TParm result = new TParm();
        String startDate = StringTool.getString( (Timestamp)this.getValue(
            "QSTART_DATE"), "yyyyMMdd");
        String endDate = StringTool.getString( (Timestamp)this.getValue(
            "QEND_DATE"), "yyyyMMdd");
        String deptCode = this.getValueString("REQUEST_DEPT");
        String operator = this.getValueString("REQUEST_USER");
        String reteoptro = this.getValueString("RATEOFPRO_CODE");
        String requestNo = this.getValueString("REQUEST_NO");
        String ydDateStart = StringTool.getString( (Timestamp)this.getValue(
            "QUSERSTART_DATE"), "yyyyMMdd");
        String ydDateEnd = StringTool.getString( (Timestamp)this.getValue(
            "QUSEEND_DATE"), "yyyyMMdd");
        String fundsouCode = this.getValueString("FUNDSOU_CODE");
        String purtypeCode = this.getValueString("PURTYPE_CODE");
        String devUseCode = this.getValueString("DEVUSE_CODE");
        if (startDate.length() != 0)
            result.setData("QSTART_DATE", startDate);
        if (endDate.length() != 0)
            result.setData("QEND_DATE", endDate);
        if (ydDateStart.length() != 0)
            result.setData("YSTART_DATE", ydDateStart);
        if (ydDateEnd.length() != 0)
            result.setData("YEND_DATE", ydDateEnd);
        if (deptCode.length() != 0)
            result.setData("REQUEST_DEPT", deptCode);
        if (operator.length() != 0)
            result.setData("REQUEST_USER", operator);
        if (reteoptro.length() != 0)
            result.setData("RATEOFPRO_CODE", reteoptro);
        if (requestNo.length() != 0)
            result.setData("REQUEST_NO", requestNo);
        if (fundsouCode.length() != 0)
            result.setData("FUNDSOU_CODE", fundsouCode);
        if (purtypeCode.length() != 0)
            result.setData("PURTYPE_CODE", purtypeCode);
        if (devUseCode.length() != 0)
            result.setData("DEVUSE_CODE", devUseCode);
        return result;
    }
    public void onClear(){
        this.clearValue("REQUEST_NO;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE");
        this.onInit();
    }
    /**
     * ���ɶ�����  
     */
    public void onGenerateReceipt(){
        int row = this.getTTable(TABLE).getSelectedRow();
        if(row<0){
            this.messageBox("��ѡ���빺����");
            return;
        }
        TParm parm = this.getTTable(TABLE).getDataStore().getRowParm(row);
        String requestNo = parm.getValue("REQUEST_NO");
        //������Ϣ
        TParm result = getNegPriceData(requestNo);
        if(result.getErrCode()==-1){
            this.messageBox(parm.getErrText());
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        if(result.getErrCode()==-2){
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        if(result==null){
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        parm.setData("SUP_CODE",result.getData("SUP_CODE"));
        parm.setData("SUP_SALES1",result.getData("SUP_SALES1"));
        parm.setData("SUP_SALES1_TEL",result.getData("SUP_SALES1_TEL"));
        parm.setData("TOT_AMT",result.getData("TOT_AMT"));
        parm.setData("PAYMENT_TERMS",result.getData("PAYMENT_TERMS"));
        this.setReturnValue(parm);
        this.closeWindow();
    }
    /**
     * �õ������ϸ
     * @param requestNo String
     * @return TParm
     */
    public TParm getNegPriceData(String requestNo){
        TParm parm = new TParm(this.getDBTool().select("SELECT * FROM DEV_NEGPRICE WHERE REQUEST_NO='"+requestNo+"'"));
        if(parm.getCount()<=0){
           parm.setErrCode(-1);
           parm.setErrText("���빺��û�������ϸ��");
           return parm;
        }
        parm.setData("POPEDEM",popedemType);
        Object obj = this.openDialog("%ROOT%\\config\\dev\\SUPUI.x",parm);
        TParm returnParm = new TParm();
        if(obj!=null){
            returnParm = (TParm)obj;
            String supCode = returnParm.getValue("SUP_CODE");
            TParm action = onSupCodeChick(supCode);
            String supSale = action.getValue("SUP_SALES1",0);
            String supSaleTel = action.getValue("SUP_SALES1_TEL",0);
            returnParm.setData("SUP_CODE",supCode);
            returnParm.setData("SUP_SALES1",supSale);
            returnParm.setData("SUP_SALES1_TEL",supSaleTel);
        }
        if(obj==null){
            returnParm.setErrCode(-2);
        }
        return returnParm;
    }
    /**
     * ��Ӧ���������¼�
     */
    public TParm onSupCodeChick(String supCode) {
        TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1,SUP_SALES1_TEL,SUP_SALES1_EMAIL,ADDRESS FROM SYS_SUPPLIER WHERE SUP_CODE='" +
            supCode + "'"));
        return parm;
    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * ��ѯ
     */
    public void onQuery(){
        //��ʼ��TABLE1
        this.getTTable(TABLE).setDataStore(getTableTDataStore());
        this.getTTable(TABLE).setDSValue();
    }

    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
}
