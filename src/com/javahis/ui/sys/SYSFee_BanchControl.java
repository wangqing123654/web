package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TComboBox;

/**
 * <p>Title: SYS_FEE���ν���</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS 1.0 (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 */
public class SYSFee_BanchControl
    extends TControl {

    TTextFormat date;
    TTable table;

    //ȫ��ִ��
    TCheckBox exeAll;

    TRadioButton unpro;
    TRadioButton proed;
    TButton buttonAdd;
    TParm parmType = new TParm();

    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

    public SYSFee_BanchControl() {
    }

    public void onInit() {
        super.onInit();
        myInitControler();
        setPermissions();
        onQuery();
    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     * ����
     */
    public void myInitControler() {

        //�õ�ǰʱ���ʼ��ʱ��ؼ�
        date = (TTextFormat)this.getComponent("DTAE");
        date.setValue(TJDODBTool.getInstance().getDBTime());

        table = (TTable)this.getComponent("TABLE");
        exeAll = (TCheckBox)this.getComponent("EXEALL");
        //Ϊִ��
        unpro = (TRadioButton)this.getComponent("UNPRO");
        //��ִ��
        proed = (TRadioButton)this.getComponent("PROED");
        buttonAdd=(TButton)this.getComponent("Button");
        //��tableע��CHECK_BOX_CLICKED��������¼�
        this.callFunction("UI|TABLE|addEventListener",
                          TTableEvent.CHECK_BOX_CLICKED, this,
                          "onTableCheckBoxChangeValue");

    }

    private void setPermissions(){
        ((TComboBox)getComponent("TYPE")).setParmMap("id:ID;name:DESC");
        if (getPopedem("FEE_PHA")) {
            parmType.addData("ID", "PHA_RULE");
            parmType.addData("DESC", "ҩƷ");
        }
        if (getPopedem("FEE")) {
            parmType.addData("ID", "SYS_FEE_RULE");
            parmType.addData("DESC", "��Ŀ");
        }
        if (getPopedem("EXM")) {
            parmType.addData("ID","EXM_RULE");
            parmType.addData("DESC","������");
        }
        if (getPopedem("ORDPACK")) {
            parmType.addData("ID","ORDPACK_RULE");
            parmType.addData("DESC","ҽ���ײ�");
        }
        parmType.setCount(parmType.getCount("ID"));
        ((TComboBox)getComponent("TYPE")).setParmValue(parmType);
        if(parmType.getCount("ID") > 0)
            ((TComboBox)getComponent("TYPE")).setSelectedIndex(1);
    }


    public void onBanch() {
        if (onDoIt()) {
            this.messageBox("ִ�гɹ���");
            onQuery();
            return;
        }
        this.messageBox("ִ��ʧ�ܣ�");
    }



    /**
     * ���Σ����ۼƻ���
     */
    public boolean onDoIt() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        //��ǰʱ�䣨������Ϊ�Ƚ�ʹ�ã�
        long nowTime = now.getTime();
        //�ܵ�parm [[���ۼƻ���������] opt_user opt_date opt_term]
        TParm parmToBanch = new TParm();
        int tblRows = table.getRowCount();
        if (tblRows <= 0) {
            this.messageBox("û�е������ݣ�");
            return false;
        }
        TParm order = new TParm();
        String oldDate = "";
        for (int i = 0; i < tblRows; i++) {

            //���û�л���������
            if (!TypeTool.getBoolean(table.getValueAt(i, 0))) {
                continue;
            }
            //�õ����е�ǰʱ��
            String activeDate = (String) table.getValueAt(i, 1);
            Long exeTime = ( (Timestamp) StringTool.getTimestamp(activeDate,
                "yyyy/MM/dd HH:mm:ss")).getTime();
            if (!"".equals(oldDate) && !oldDate.equals(activeDate)) {
                parmToBanch.setData("OPT_USER", Operator.getID());
                parmToBanch.setData("OPT_TERM", Operator.getIP());
                parmToBanch.setData("OPT_DATE", now);
                //�����������νӿ�
                //------------start------------
                //Ϊ����
                if (unpro.isSelected()) {
                    //�жϵ�������ʱ���Ƿ��ѹ�
                    if (exeTime < nowTime) {
                        //��ִ��ʱ�����ڵ�ǰ��ô������һ��Action
                        TParm result = TIOM_AppServer.executeAction(
                            "action.sys.SYSFeeReadjustAction",
                            "onInsertSYSFeeReadjust", parmToBanch);//����ִ��
                        if (result.getErrCode() < 0) {
                            this.messageBox_(result);
                            return false;
                        }
                    }else{
                        //���ò���
                        //����actionִ������
                        TParm result = TIOM_AppServer.executeAction(
                            "action.sys.SYSFeeReadjustAction",
                            "onSYSFeeReadjustPatch", parmToBanch); //�����Ρ�Ԥִ��
                        if (result.getErrCode() < 0) {
                            this.messageBox_(result);
                            return false;
                        }
                    }
                }
                else {
                    //���ø���
                    //����actionִ������
                    TParm result = TIOM_AppServer.executeAction(
                        "action.sys.SYSFeeReadjustAction",
                        "onUpdateSYSFeeReadjustPatch", parmToBanch);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result);
                        return false;
                    }
                }
                //ˢ�´����ӿڵ�parm
                parmToBanch = new TParm();
                order = new TParm();
                oldDate = "";
            }

            //ѹ����
            if (activeDate.equals( (String) table.getValueAt(i, 1))) {
                //���������νӿڵĲ���
                order.addRowData(table.getParmValue(), i);
                if("Y".equals(table.getParmValue().getValue("ORDERSET_FLG",i))){
                    order.setData("OWN_PRICE",order.getCount("OWN_PRICE")-1, 0);
                    order.setData("OWN_PRICE2",order.getCount("OWN_PRICE2")-1, 0);
                    order.setData("OWN_PRICE3",order.getCount("OWN_PRICE3")-1 ,0);
                }
                parmToBanch.setData("ORDER", order.getData());
            }

            //----------------end----------------------------------------------
            //������ʱ��--�Ƚ���
            oldDate = activeDate;
        }
        //��ֹ���һ������
        if (!"".equals(oldDate)) {
            parmToBanch.setData("OPT_USER", Operator.getID());
            parmToBanch.setData("OPT_TERM", Operator.getIP());
            parmToBanch.setData("OPT_DATE", now);
            //�����������νӿ�
            //------------------------
            //�õ����е�ǰʱ��
            String activeDate = (String) table.getValueAt(tblRows-1, 1);
            Long exeTime = ( (Timestamp) StringTool.getTimestamp(activeDate,
                "yyyy/MM/dd HH:mm:ss")).getTime();
            //�жϵ�������ʱ���Ƿ��ѹ�
            if (exeTime < nowTime) {
                //��ִ��ʱ�����ڵ�ǰ��ô������һ��Action
                TParm result = TIOM_AppServer.executeAction(
                    "action.sys.SYSFeeReadjustAction",
                    "onInsertSYSFeeReadjust", parmToBanch);
                if (result.getErrCode() < 0) {
                    this.messageBox_(result);
                    return false;
                }
            }else{
                //Ϊ����
                if (unpro.isSelected()) {
                    //���ò���
                    //����actionִ������
                    TParm result = TIOM_AppServer.executeAction(
                        "action.sys.SYSFeeReadjustAction",
                        "onSYSFeeReadjustPatch", parmToBanch);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result);
                        return false;
                    }
                }
                else {
                    //���ø���
                    //����actionִ������
                    TParm result = TIOM_AppServer.executeAction(
                        "action.sys.SYSFeeReadjustAction",
                        "onUpdateSYSFeeReadjustPatch", parmToBanch);
                    if (result.getErrCode() < 0) {
                        this.messageBox_(result);
                        return false;
                    }
                }
            }
            //ˢ�´����ӿڵ�parm
            parmToBanch = new TParm();
            oldDate = "";
        }


        //ǰ̨ˢ��
        TIOM_Database.logTableAction("SYS_FEE");

        return true;
    }

    /**
     * ȫ��ִ��
     */
    public void onExe() {
        boolean exeFlg;
        //��õ��ʱ��ֵ
        exeFlg = TypeTool.getBoolean(exeAll.getValue());

        int rows = table.getRowCount();
        for (int i = 0; i < rows; i++) {
            table.setValueAt(exeFlg, i, 0);
        }

    }

    /**
     * ��ѡ����ʱ����ͬ������ҩƷ
     * @param obj Object
     */
    public void onTableCheckBoxChangeValue(Object obj) {

        //ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        table.acceptText();
        //���ѡ�е���/��
        int col = table.getSelectedColumn();
        int row = table.getSelectedRow();
        //���table�ϵ�����
        int rowcount = table.getRowCount();
        //���ѡ�е��ǵ�0�оͼ���ִ�ж���--ִ��
        if (col == 0) {
            boolean exeFlg;
            //��õ��ʱ��ֵ
            exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
            //�õ�ѡ���е�ִ��ʱ��
            String exeDateTime = (String) table.getValueAt(row, 1);
            for (int i = 0; i < rowcount; i++) {
                //��ѡ�е�ִ��ʱ����ͬ�ͻ���
                if (exeDateTime.equals( (String) table.getValueAt(i, 1)))
                    table.setValueAt(exeFlg, i, 0);
            }
        }

    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        table.removeRowAll();
        if(parmType.getCount("ID") < 4 &&
           (getValue("TYPE") == null || getValueString("TYPE").length() == 0)){
            return;
        }
        String now = StringTool.getString( (Timestamp) date.getValue(),
                                          "yyyyMMdd");
        //===========pangben modify 20110512 start
        String region = "";
        if (null != Operator.getRegion() &&
            Operator.getRegion().length() > 0) {
            region = " AND (REGION_CODE='" + Operator.getRegion() + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        }
        //===========pangben modify 201105121 stop

        String selSql =
            " SELECT START_DATE,ORDER_DESC,OWN_PRICE,NHI_PRICE,GOV_PRICE,RPP_CODE,ORDER_CODE,END_DATE,OWN_PRICE2,OWN_PRICE3,ORDERSET_FLG " +
            " FROM SYS_FEE_HISTORY " +
            " WHERE ACTIVE_FLG='Y' " +//ֻ��ʾ��ǰ���õ�
            //delete by huangtt 20141210 start
//            " AND TO_DATE (START_DATE, 'YYYYMMDDHH24MISS') > TO_DATE (" + now +
//            " , 'YYYYMMDD')" 
            //delete by huangtt 20141210 end
            region+//===========pangben modify 201105121
            getAndCondition() +
            " ORDER BY START_DATE ";
       // System.out.println("sql"+selSql);
        TParm result = new TParm(TJDODBTool.getInstance().select(selSql));
        if (result.getCount() <= 0) {
            this.messageBox("Ŀǰû�е�����Ŀ");
            return;
        }
        filterSysFee(result);
        //������ʵ�Parm�ŵ�table��
        table.setParmValue(arrangementData(result));
    }

    /**
     * �õ���ѯ����
     * @return String
     */
    public String getAndCondition() {
        if (unpro.isSelected())
            return "AND RPP_CODE IS NULL ";
        return "AND RPP_CODE IS NOT NULL ";
    }

    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm arrangementData(TParm parm) {
        TParm data = parm;
        for (int i = 0; i < parm.getCount(); i++) {
            data.addData("EXE", unpro.isSelected() ? false : true);
            //���ʱ���Ƿ�Ϊ������ʱ����
            String startDate = checkDateTime( (String) parm.getData(
                "START_DATE", i));
            //�������������ʱ����
            data.setData("START_DATE", i,
                         startDate.substring(0, 4) + "/" +
                         startDate.substring(4, 6) + "/" +
                         startDate.substring(6, 8) + " " +
                         startDate.substring(8, 10) + ":" +
                         startDate.substring(10, 12) + ":" +
                         startDate.substring(12));
            if(data.getValue("ORDERSET_FLG",i).equals("N"))
                continue;
            String sqlForDtl = " SELECT ORDER_CODE,DOSAGE_QTY "+
                               " FROM SYS_ORDERSETDETAIL "+
                               " WHERE ORDERSET_CODE = '" + data.getValue("ORDER_CODE",i) + "'";
            TParm parmDtl = new TParm(TJDODBTool.getInstance().select(sqlForDtl));
            int count = parmDtl.getCount();
            double totFee = 0.0;
            double totFee2 = 0.0;
            double totFee3 = 0.0;
            for (int j = 0; j < count; j++) {
                String code = parmDtl.getValue("ORDER_CODE", j);
                double qty = TypeTool.getDouble(parmDtl.getValue("DOSAGE_QTY", j));
                totFee += getPrice(code) * qty;
                totFee2 += getPrice2(code) * qty;
                totFee3 += getPrice3(code) * qty;
            }
            data.setData("OWN_PRICE", i, totFee);
            data.setData("OWN_PRICE2", i, totFee2);
            data.setData("OWN_PRICE3", i, totFee3);
        }
        return data;
    }

    //����order�õ�����
    public double getPrice(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

    //����order�õ�����
    public double getPrice2(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE2");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

    //����order�õ�����
    public double getPrice3(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE3");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

    /**
     * ����Ƿ�����14λ��yyyyMMddHHmmss��
     * @param dateTime String
     * @return String
     */
    private String checkDateTime(String dateTime) {

        if (dateTime.length() >= 14) {
            return dateTime.substring(0, 14);
        }
        //С��14λ
        int addZero = 14 - dateTime.length();
        for (int i = 0; i < addZero; i++)
            dateTime += "0";
        return dateTime;
    }

    /**
     * ���
     * @param args String[]
     */
    public void onClear() {
        table.removeRowAll();

    }

    public void onProduce(Object ob) {
        onClear();
        if("Y".equals(ob+"")){
            buttonAdd.setEnabled(false);
        }else{
            buttonAdd.setEnabled(true);
        }

    }
    public TParm getType(){
        String SQL = " SELECT CATEGORY_CODE "+
                     " FROM SYS_CATEGORY "+
                     " WHERE RULE_TYPE='"+getValueString("TYPE")+"'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        return parm;
    }


    public void filterSysFee(TParm parm){
        TParm typeParm = getType();
        if(typeParm.getCount() <= 0)
            return;
        for(int i = parm.getCount() - 1;i >= 0;i--){
            boolean isRemove = false;
            for(int j= 0;j<typeParm.getCount();j++){
               if(parm.getValue("ORDER_CODE",i).startsWith(typeParm.getValue("CATEGORY_CODE",j))){
                   isRemove = false;
                   break;
               }
               else
                   isRemove = true;
            }
            if(isRemove)
                parm.removeRow(i);
        }
    }


    public static void main(String[] args) {
        JavaHisDebug.initClient();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_BANCH.x");

    }


}
