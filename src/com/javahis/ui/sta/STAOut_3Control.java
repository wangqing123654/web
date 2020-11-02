package com.javahis.ui.sta;

import com.dongyang.jdo.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.ui.TLabel;
import com.dongyang.control.*;
import jdo.sta.STATool;
import com.dongyang.ui.TRadioButton;
import jdo.sta.STASQLTool;
import jdo.sta.STAOut_3Tool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import java.sql.Timestamp;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>Title: ��Ժ���˼����������������±���(��ͳ5��2)</p>
 *
 * <p>Description: ��Ժ���˼����������������±���(��ͳ5��2)</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-14
 * @version 1.0
 */
public class STAOut_3Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false;//��¼�Ƿ��ύ
    private String DATA_StaDate = "";//��¼Ŀǰ��ʾ�����ݵ�����(����)
    private String STA_DATA_TYPE = "";//ͳ������
    private String S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
    private String DATE_Start = "";//��¼��ʼ����
    private String DATE_End = "";//��¼��ֹ����
    private TParm Day_Data;//��¼���ڶβ�ѯ��������
    private String LEADER = "";//��¼�Ƿ����鳤Ȩ��  ���LEADER=2��ô�����鳤Ȩ��

    /**
     * ҳ���ʼ��
     */
    public void onInit(){
        super.init();
        String title= (String) this.getParameter();
        this.setTitle(title);
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        //����н�SEQ�滻Ϊ��Ӧ�Ĳ�������
        TDataStore store = new TDataStore();
        store.setSQL("SELECT SEQ, ICD_DESC, CONDITION FROM STA_173_LIST");
        store.retrieve();
        OrderList orderDesc = new OrderList(store);
        TTable table = (TTable)this.getComponent("Table");
        table.addItem("ORDER_LIST", orderDesc);
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.addItem("ORDER_LIST", orderDesc);
        //��ʼ��Ȩ��
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
    }
    /**
     * ���
     */
    public void onClear(){
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.clearValue("Submit");
        TTable table1 = (TTable)this.getComponent("Table");
        table1.removeRowAll();
        table1.resetModify();
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.removeRowAll();
        STA_CONFIRM_FLG = false; //��¼�Ƿ��ύ
        DATA_StaDate = ""; //��¼Ŀǰ��ʾ�����ݵ�����(����)
        S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
        DATE_Start = "";//��¼��ʼ����
        DATE_End = "";//��¼��ֹ����
    }
    /**
     * ��ѯ����
     */
    public void onQuery(){
        if("Y".equals(this.getValueString("R_MONTH"))){//������Ҫ����
            //ע�⣺�±��������������� ������STA_IN_02����
            generate_MonthData();
        }
        else if("Y".equals(this.getValueString("R_DAYS"))){//��������ʱ������
            //ʱ��β�ѯ�ı��������Ƿ��������ݣ�ֻ�в�ѯ��ʾ���ܣ������б��治���޸�
            generate_DayData();
        }
    }
    /**
     * �����±�����
     */
    private void generate_MonthData() {
        S_TYPE = "month";//��¼ͳ������
        STA_CONFIRM_FLG = false;//��ʼĬ������û���ύ
        if(((TRadioButton)this.getComponent("Type0")).isSelected())//�ܼ�
            STA_DATA_TYPE = "0";
        else if(((TRadioButton)this.getComponent("Type1")).isSelected())//��
            STA_DATA_TYPE = "1";
        else if(((TRadioButton)this.getComponent("Type2")).isSelected())//Ů
            STA_DATA_TYPE = "2";
        //�������ݵ�����
        String STADATE = this.getText("STA_DATE").replace("/","");
        if(STADATE.trim().length()<=0){
            this.messageBox_("��ѡ��ͳ�Ƶ����£�");
            return;
        }
        //����Ƿ����������������
        if(!canGeneration(STADATE)){
            //������������������ݣ���ԭ������
            gridBind(STADATE);//���ݰ�
            return;
        }
        String StartDate = STADATE + "01"; //ÿ�µ�һ��
        //��ȡ���·ݵ����һ��
        String EndDate = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(STADATE),
                                              "yyyyMMdd");
        TParm parm = new TParm();
        parm.setData("DATE_S", StartDate);
        parm.setData("DATE_E", EndDate);
        //���ɵĽ����
        TParm result = STAOut_3Tool.getInstance().selectDiseaseSum(parm,STA_DATA_TYPE);
        for(int i=0;i<result.getCount("STA_DATE");i++){
            result.setData("STA_DATE",i,STADATE);
            result.setData("CONFIRM_FLG",i,"N");
            result.setData("CONFIRM_USER",i,"");
            result.setData("CONFIRM_DATE",i,"");
            result.setData("OPT_USER",i,Operator.getID());
            result.setData("OPT_TERM",i,Operator.getIP());
            //============pangben modify 20110523 start
            result.setData("REGION_CODE",i,Operator.getRegion());
             //============pangben modify 20110523 stop
        }
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAOut_3Action",
            "insertSTA_OUT_03", result);
        if (re.getErrCode() < 0) {
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        this.messageBox_("���ɳɹ���");
        gridBind(STADATE);//�������ɵ�����
    }
    /**
     * �������ڶα�������
     */
    private void generate_DayData() {
        S_TYPE = "day"; //��¼ͳ������
        if ( ( (TRadioButton)this.getComponent("Type0")).isSelected()) //�ܼ�
            STA_DATA_TYPE = "0";
        else if ( ( (TRadioButton)this.getComponent("Type1")).isSelected()) //��
            STA_DATA_TYPE = "1";
        else if ( ( (TRadioButton)this.getComponent("Type2")).isSelected()) //Ů
            STA_DATA_TYPE = "2";
        String DATE_S = this.getText("DATE_S").replace("/", "");
        String DATE_E = this.getText("DATE_E").replace("/", "");
        TParm parm = new TParm();
        if (DATE_S.length() <= 0 || DATE_E.length() <= 0) {
            this.messageBox_("��ѡ�����ڷ�Χ");
            ( (TTextFormat)this.getComponent("DATE_S")).grabFocus();
            return;
        }
        parm.setData("DATE_S", DATE_S);
        parm.setData("DATE_E", DATE_E);
        //===========pangben modify 20110523 start
        parm.setData("REGION_CODE", Operator.getRegion());
        //===========pangben modify 20110523 stop
        TParm result = STAOut_3Tool.getInstance().selectDiseaseSum(parm,STA_DATA_TYPE);
        if (result.getErrCode() < 0) {
            this.messageBox_("����ʧ�� " + result.getErrText());
            return;
        }
        Day_Data = result;//��¼���ڶβ�ѯ����������Ϊ��������ʹ��
        gridBind(result);
    }

    /**
     * ������ݰ�
     */
    private void gridBind(String STADATE){
        TTable table = (TTable)this.getComponent("Table");
        //========= pangben modify 20110523 �������
        String sql = STASQLTool.getInstance().getSTA_OUT_03(STADATE,STA_DATA_TYPE,Operator.getRegion());
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
        DATA_StaDate = STADATE;
    }
    /**
     * ������ݰ�(���ڶα���)
     */
    private void gridBind(TParm data) {
        TTable table = (TTable)this.getComponent("Table_Read");
        table.setParmValue(data);
        //��¼��ѯ���ڶ� ���ڱ����ӡ
        DATE_Start = this.getText("DATE_S");
        DATE_End = this.getText("DATE_E");
    }
    /**
    * ����
    */
   public void onSave(){
       if(STA_CONFIRM_FLG){
           this.messageBox_("�����Ѿ��ύ�������޸�");
           return;
       }
       TTable table = (TTable)this.getComponent("Table");
       table.acceptText();
       TDataStore ds = table.getDataStore();
       String optUser=Operator.getID();
       String optIp=Operator.getIP();
       //��ȡ������ʱ��
       Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
       //�Ƿ��ύ
       boolean submit = ((TCheckBox)this.getComponent("Submit")).isSelected();
       String message = "�޸ĳɹ���";//��ʾ��
       if(submit)
           message = "�ύ�ɹ���";
       for(int i=0;i<ds.rowCount();i++){
           //�ж��Ƿ��ύ
           if(submit){
               ds.setItem(i, "CONFIRM_FLG", "Y");
               ds.setItem(i, "CONFIRM_USER", optUser);
               ds.setItem(i, "CONFIRM_DATE", CONFIRM_DATE);
           }
           else{
               ds.setItem(i, "CONFIRM_FLG", "N");
               ds.setItem(i, "CONFIRM_USER", "");
               ds.setItem(i, "CONFIRM_DATE", "");
           }
           ds.setItem(i,"OPT_USER",optUser);
           ds.setItem(i,"OPT_TERM",optIp);
           ds.setItem(i,"OPT_DATE",CONFIRM_DATE);
       }
       if(ds.update()){
           this.messageBox_(message);
           if(submit)
                STA_CONFIRM_FLG = true;
       }
       else
           this.messageBox_("����ʧ�ܣ�");
   }
   /**
     * ��ӡ
     */
    public void onPrint(){
        //������ݵ�����
        if (DATA_StaDate.trim().length() <= 0&&(DATE_Start.length()<=0||DATE_End.length()<=0)) {
            return;
        }
//        //��������Ƿ����ύ״̬
//        if(!STA_CONFIRM_FLG){
//            this.messageBox_("����û���ύ�����ܴ�ӡ");
//            return;
//        }
        TParm print = new TParm();
        if("month".equals(S_TYPE)){//�������ͳ�� ��ѯ���ݿ� ȡ�ø���ͳ����Ϣ
            print = STAOut_3Tool.getInstance().selectPrint(DATA_StaDate,STA_DATA_TYPE,Operator.getRegion());//=======pangben modify 20110523 ����������
        }else if("day".equals(S_TYPE)){//��������ڶ�ͳ�� ��ȡtable�ϵ����ݽ��д�ӡ
//            String StartDate = DATA_StaDate + "01"; //ÿ�µ�һ��
//            //��ȡ���·ݵ����һ��
//            String EndDate = StringTool.getString(STATool.getInstance().
//                                                  getLastDayOfMonth(DATA_StaDate),
//                                                  "yyyyMMdd");
//            TParm parm = new TParm();
//            parm.setData("DATE_S", StartDate);
//            parm.setData("DATE_E", EndDate);
//            print = STAOut_3Tool.getInstance().selectDiseaseSum(parm,STA_DATA_TYPE);
            print =Day_Data;
        }
        //��SEQ�滻Ϊ��Ӧ�Ĳ�������
        int printCount = print.getCount("STA_DATE");
        if (printCount <= 0) {
            return;
        }
        TParm printDate = new TParm();
        //�����ݷ�Ϊ4ҳ��ʾ
        int count = 0;
        int page1 = 52; //���������ʾ������
        int page2 = 104;
        int page3 = 156;
        int page4 = printCount;
        //��һҳ���� ����һҳ��Ϣ ��Ϊ����
        int page_f1 = 0; //��¼��ҳ����
        if (page1 % 2 == 0)
            page_f1 = page1 / 2;
        else
            page_f1 = page1 / 2 + 1;
        for (int i = 0; i < page_f1; i++) {
            printDate.addData("ICD_DESC_1", print.getValue("ICD_DESC", i));
            printDate.addData("SEQ_1", print.getValue("SEQ", i));
            printDate.addData("DATA_01_1", print.getValue("DATA_01", i));
            printDate.addData("DATA_02_1", print.getValue("DATA_02", i));
            printDate.addData("DATA_03_1", print.getValue("DATA_03", i));
            printDate.addData("DATA_04_1", print.getValue("DATA_04", i));
            printDate.addData("DATA_05_1", print.getValue("DATA_05", i));
            printDate.addData("DATA_06_1", print.getValue("DATA_06", i));
            printDate.addData("DATA_07_1", print.getValue("DATA_07", i));

            printDate.addData("ICD_DESC_2",
                              print.getValue("ICD_DESC", i + page_f1));
            printDate.addData("SEQ_2", print.getValue("SEQ", i + page_f1));
            printDate.addData("DATA_01_2", print.getValue("DATA_01", i + page_f1));
            printDate.addData("DATA_02_2", print.getValue("DATA_02", i + page_f1));
            printDate.addData("DATA_03_2", print.getValue("DATA_03", i + page_f1));
            printDate.addData("DATA_04_2", print.getValue("DATA_04", i + page_f1));
            printDate.addData("DATA_05_2", print.getValue("DATA_05", i + page_f1));
            printDate.addData("DATA_06_2", print.getValue("DATA_06", i + page_f1));
            printDate.addData("DATA_07_2", print.getValue("DATA_07", i + page_f1));
            count++;
        }
        //�ڶ�ҳ���� ���ڶ�ҳ��Ϣ ��Ϊ����
        int page_f2 = 0; //��¼��ҳ����
        if ((page2 - page1) % 2 == 0)
            page_f2 = (page2 - page1) / 2;
        else
            page_f2 = (page2 - page1) / 2 + 1;
        for (int i = page1; i < (page1 + page_f2); i++) {
            printDate.addData("ICD_DESC_1", print.getValue("ICD_DESC", i));
            printDate.addData("SEQ_1", print.getValue("SEQ", i));
            printDate.addData("DATA_01_1", print.getValue("DATA_01", i));
            printDate.addData("DATA_02_1", print.getValue("DATA_02", i));
            printDate.addData("DATA_03_1", print.getValue("DATA_03", i));
            printDate.addData("DATA_04_1", print.getValue("DATA_04", i));
            printDate.addData("DATA_05_1", print.getValue("DATA_05", i));
            printDate.addData("DATA_06_1", print.getValue("DATA_06", i));
            printDate.addData("DATA_07_1", print.getValue("DATA_07", i));

            printDate.addData("ICD_DESC_2",
                              print.getValue("ICD_DESC", i + page_f2));
            printDate.addData("SEQ_2", print.getValue("SEQ", i + page_f2));
            printDate.addData("DATA_01_2", print.getValue("DATA_01", i + page_f2));
            printDate.addData("DATA_02_2", print.getValue("DATA_02", i + page_f2));
            printDate.addData("DATA_03_2", print.getValue("DATA_03", i + page_f2));
            printDate.addData("DATA_04_2", print.getValue("DATA_04", i + page_f2));
            printDate.addData("DATA_05_2", print.getValue("DATA_05", i + page_f2));
            printDate.addData("DATA_06_2", print.getValue("DATA_06", i + page_f2));
            printDate.addData("DATA_07_2", print.getValue("DATA_07", i + page_f2));
            count++;
        }
        //����ҳ���� ������ҳ��Ϣ ��Ϊ����
        int page_f3 = 0;
        if ((page3 - page2) % 2 == 0)
            page_f3 = (page3 - page2) / 2;
        else
            page_f3 = (page3 - page2) / 2 + 1;
        for (int i = page2; i < (page2 + page_f3); i++) {
            printDate.addData("ICD_DESC_1", print.getValue("ICD_DESC", i));
            printDate.addData("SEQ_1", print.getValue("SEQ", i));
            printDate.addData("DATA_01_1", print.getValue("DATA_01", i));
            printDate.addData("DATA_02_1", print.getValue("DATA_02", i));
            printDate.addData("DATA_03_1", print.getValue("DATA_03", i));
            printDate.addData("DATA_04_1", print.getValue("DATA_04", i));
            printDate.addData("DATA_05_1", print.getValue("DATA_05", i));
            printDate.addData("DATA_06_1", print.getValue("DATA_06", i));
            printDate.addData("DATA_07_1", print.getValue("DATA_07", i));

            printDate.addData("ICD_DESC_2",
                              print.getValue("ICD_DESC", i + page_f3));
            printDate.addData("SEQ_2", print.getValue("SEQ", i + page_f3));
            printDate.addData("DATA_01_2", print.getValue("DATA_01", i + page_f3));
            printDate.addData("DATA_02_2", print.getValue("DATA_02", i + page_f3));
            printDate.addData("DATA_03_2", print.getValue("DATA_03", i + page_f3));
            printDate.addData("DATA_04_2", print.getValue("DATA_04", i + page_f3));
            printDate.addData("DATA_05_2", print.getValue("DATA_05", i + page_f3));
            printDate.addData("DATA_06_2", print.getValue("DATA_06", i + page_f3));
            printDate.addData("DATA_07_2", print.getValue("DATA_07", i + page_f3));
            count++;
        }
        //����ҳ���� ������ҳ��Ϣ ��Ϊ����
        int page_f4 = 0;
        if ((page4 - page3) % 2 == 0)
            page_f4 = (page4 - page3) / 2;
        else
            page_f4 = (page4 - page3) / 2 + 1;
        for (int i = page3; i < (page3 + page_f4); i++) {
            printDate.addData("ICD_DESC_1", print.getValue("ICD_DESC", i));
            printDate.addData("SEQ_1", print.getValue("SEQ", i));
            printDate.addData("DATA_01_1", print.getValue("DATA_01", i));
            printDate.addData("DATA_02_1", print.getValue("DATA_02", i));
            printDate.addData("DATA_03_1", print.getValue("DATA_03", i));
            printDate.addData("DATA_04_1", print.getValue("DATA_04", i));
            printDate.addData("DATA_05_1", print.getValue("DATA_05", i));
            printDate.addData("DATA_06_1", print.getValue("DATA_06", i));
            printDate.addData("DATA_07_1", print.getValue("DATA_07", i));

            printDate.addData("ICD_DESC_2",
                              print.getValue("ICD_DESC", i + page_f4));
            printDate.addData("SEQ_2", print.getValue("SEQ", i + page_f4));
            printDate.addData("DATA_01_2", print.getValue("DATA_01", i + page_f4));
            printDate.addData("DATA_02_2", print.getValue("DATA_02", i + page_f4));
            printDate.addData("DATA_03_2", print.getValue("DATA_03", i + page_f4));
            printDate.addData("DATA_04_2", print.getValue("DATA_04", i + page_f4));
            printDate.addData("DATA_05_2", print.getValue("DATA_05", i + page_f4));
            printDate.addData("DATA_06_2", print.getValue("DATA_06", i + page_f4));
            printDate.addData("DATA_07_2", print.getValue("DATA_07", i + page_f4));
            count++;
        }
        printDate.setCount(count);
        printDate.addData("SYSTEM", "COLUMNS", "ICD_DESC_1");
        printDate.addData("SYSTEM", "COLUMNS", "SEQ_1");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_01_1");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_02_1");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_03_1");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_04_1");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_05_1");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_06_1");
        printDate.addData("SYSTEM", "COLUMNS", "ICD_DESC_2");
        printDate.addData("SYSTEM", "COLUMNS", "SEQ_2");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_01_2");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_02_2");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_03_2");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_04_2");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_05_2");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_06_2");
        //��ӡ����
        String dataDate = "";
        if("month".equals(S_TYPE)){
            dataDate = DATA_StaDate.substring(0, 4) + "��" +
                DATA_StaDate.subSequence(4, 6) + "��";
        }else if("day".equals(S_TYPE)){
            dataDate = DATE_Start+"~"+DATE_End;
        }
        TParm parm = new TParm();
        //����
        parm.setData("date","TEXT",dataDate);
        parm.setData("unit","TEXT",Operator.getHospitalCHNFullName());
        //��������
        if(STA_DATA_TYPE.equals("0"))
            parm.setData("type","TEXT","�ϼ�");
        else if(STA_DATA_TYPE.equals("1"))
            parm.setData("type","TEXT","��");
        else if(STA_DATA_TYPE.equals("2"))
            parm.setData("type","TEXT","Ů");
        parm.setData("T1",printDate.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_03.jhw", parm);
    }


    /**
     * ����Ƿ������������
     * ����Ƿ��Ѿ����·ݵ����� �����Ƿ��Ѿ��ύ  true:��������  false:����������
     * @param STADATE String
     * @return boolean
     */
    private boolean canGeneration(String STADATE){
        boolean can=false;
        //�������״̬
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_OUT_03",
            STADATE," DATA_TYPE='"+STA_DATA_TYPE+"'",Operator.getRegion());//======pangben modify 20110523  ����������

        //�����Ѿ��ύ
        if (reFlg == 2) {
            this.messageBox_("�����Ѿ��ύ�������������ɣ�");
            can = false;
            STA_CONFIRM_FLG = true; //��ʶ�����Ѿ��ύ
        }
        //���ݴ��ڵ�û���ύ
        else if (reFlg == 1) {
            switch (this.messageBox("��ʾ��Ϣ",
                                    "�����Ѵ��ڣ��Ƿ��������ɣ�", this.YES_NO_OPTION)) {
                case 0: //����
                    can = true;
                    break;
                case 1: //������
                    can = false;
                    break;
            }
        }else if(reFlg==0){//û������
            can=true;
        }else if(reFlg==-1){//���ݼ�˴���
            can=false;
        }
        if(STA_CONFIRM_FLG==true)
            this.setValue("Submit",true);
        else
            this.setValue("Submit",false);
        return can;
    }

    /**
     * SEQ����滻��Ӧ�Ĳ�������
     */
    public class OrderList
        extends TLabel {
        TDataStore dataStore;
        public OrderList(TDataStore dataStore1){
            dataStore = dataStore1;
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("SEQ");
            Vector d = (Vector) parm.getData("ICD_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return d.get(i).toString();
            }
            return s;
        }
    }
    /**
     * ����ͳ�Ʒ�ʽ ��ʾ���������·ݻ���ʱ��οؼ�
     * @param type String "month":��ͳ��  "days":����ͳ��
     */
    private void setDateVisble(String type){
        if("month".equals(type)){//��ͳ��
            this.setText("tLabel_1","ͳ���·�");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(true);//��ʾ�·ݿؼ�
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(false);//�������ڶοؼ�
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(false);//�������ڶοؼ�
            ((TLabel)this.getComponent("tLabel_2")).setVisible(false);
            callFunction("UI|save|setEnabled", true); //���水ť����
            callFunction("UI|Table_Read|setVisible", false);//������ͳ�Ʊ��
            callFunction("UI|Table|setVisible", true);//��ʾ��ͳ�Ʊ��
        }
        else if("days".equals(type)){
            this.setText("tLabel_1","ͳ������");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(false);//��ʾ�·ݿؼ�
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(true);//�������ڶοؼ�
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(true);//�������ڶοؼ�
            ((TLabel)this.getComponent("tLabel_2")).setVisible(true);
            callFunction("UI|save|setEnabled", false); //���水ť����
            callFunction("UI|Table_Read|setVisible", true);//��ʾ��ͳ�Ʊ��
            callFunction("UI|Table|setVisible", false);//������ͳ�Ʊ��
        }
        onClear();//���
    }
    /**
     * ��ͳ��Radio ѡ���¼�
     */
    public void onR_MONTH_Click() {
        this.setDateVisble("month");
    }

    /**
     * ���ڶ�ͳ��Radio ѡ���¼�
     */
    public void onR_DAYS_Click() {
        this.setDateVisble("days");
    }
    /**
     * ���� ���ڶβ�ѯ�ĳ�ʼʱ��
     */
    private void initDate(){
        //���ó�ʼʱ��
        Timestamp lastMonth = STATool.getInstance().getLastMonth();//��ȡ�ϸ��µ��·�
        //���µ�һ��
        this.setValue("DATE_S",StringTool.getTimestamp(StringTool.getString(lastMonth,"yyyyMM")+"01","yyyyMMdd"));
        //�������һ��
        this.setValue("DATE_E",STATool.getInstance().getLastDayOfMonth(StringTool.getString(lastMonth,"yyyyMM")));
    }
}
