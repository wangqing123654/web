package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_1Tool;
import com.dongyang.ui.TTable;
import jdo.sta.STASQLTool;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import com.dongyang.ui.TCheckBox;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import jdo.sta.STATool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TLabel;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 * <p>Title: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��</p>
 *
 * <p>Description: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-8
 * @version 1.0
 */
public class STAOut_1Control
    extends TControl {
    private boolean CONFIRM_FLG = false;//��¼�Ƿ��ύ
    private String DATA_StaDate = "";//��¼Ŀǰ��ʾ�����ݵ�����(����)
    private String S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
    private String DATE_Start = "";//��¼��ʼ����
    private String DATE_End = "";//��¼��ֹ����
    private TParm DATA_PrintDay=null;//��¼����ͳ�Ƶ��������ڴ�ӡ
    private String LEADER = "";//��¼�Ƿ����鳤Ȩ��  ���LEADER=2��ô�����鳤Ȩ��

    public void onInit(){
        super.init();
        String title= (String) this.getParameter();
        this.setTitle(title);
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        this.addEventListener("Table1->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        initDate();
        //��ʼ��Ȩ��
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
    }
    /**
     * ��������
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
        CONFIRM_FLG = false;//��ʼĬ������û���ύ
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
        insertData(STADATE);
    }
    /**
     * �������ڶα�������
     */
    private void generate_DayData() {
        S_TYPE = "day"; //��¼ͳ������
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
        //===========pangben modify 20110520 start
        parm.setData("REGION_CODE", Operator.getRegion());
         //===========pangben modify 20110520 stop
        DATA_PrintDay = STAOut_1Tool.getInstance().getDataByDate(parm);
        if (DATA_PrintDay.getErrCode() < 0) {
            this.messageBox_("����ʧ�� " + DATA_PrintDay.getErrText());
            return;
        }
        gridBind(DATA_PrintDay);
    }
    /**
     * ����Ƿ������������
     * ����Ƿ��Ѿ����·ݵ����� �����Ƿ��Ѿ��ύ  true:��������  false:����������
     * @param STADATE String
     * @return boolean
     */
    private boolean canGeneration(String STADATE){
        boolean can=false;
        TParm out_1Parm = new TParm();
        out_1Parm.setData("STA_DATE",STADATE);
        //=============pangben modify 20110520 start �����������
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            out_1Parm.setData("REGION_CODE",Operator.getRegion());
         //=============pangben modify 20110520 stop
        //��ѯ�Ƿ���ڸ��·ݵ�����
        TParm out_1 = STAOut_1Tool.getInstance().selectSTA_OUT_01(out_1Parm);
        if(out_1.getCount()>0){
            //����������ݲ����Ѿ��ύ��������������
            if(out_1.getValue("CONFIRM_FLG",0).equals("Y")){
                this.messageBox_("�����Ѿ��ύ�������������ɣ�");
                can = false;
                CONFIRM_FLG = true;//��ʶ�����Ѿ��ύ
            }else{
                switch (this.messageBox("��ʾ��Ϣ",
                                        "�����Ѵ��ڣ��Ƿ��������ɣ�", this.YES_NO_OPTION)) {
                    case 0://����
                        can = true;
                        break;
                    case 1://������
                        can = false;
                        break;
                }
            }
        }
        else
            can = true;
        if(CONFIRM_FLG==true)
            this.setValue("Submit",true);
        else
            this.setValue("Submit",false);
        return can;
    }
    /**
     * ������Table��ʼ��SQL��䣬������
     */
    private void gridBind(String STADATE){
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        //==============pangben modify 20110520 ��Ӳ���
        table1.setSQL(STASQLTool.getInstance().getSTA_OUT_01_1(STADATE,Operator.getRegion()));
        table1.retrieve();
        table1.setDSValue();
        //==============pangben modify 20110520 ��Ӳ���
        table2.setSQL(STASQLTool.getInstance().getSTA_OUT_01_2(STADATE,Operator.getRegion()));
        table2.retrieve();
        table2.setDSValue();
        DATA_StaDate = table1.getDataStore().getItemString(0,0);
    }
    /**
     * ������Table������
     */
    private void gridBind(TParm data){
        //����data �Ա�󶨵ڶ���Table �����������ͬһ��TParm ���ʱ�ᱨ��
        TParm data1 = new TParm();
        data1.addRowData(data,0);
        TTable table1 = (TTable)this.getComponent("Table1_Read");
        TTable table2 = (TTable)this.getComponent("Table2_Read");
        table1.setParmValue(data);
        table2.setParmValue(data1);
        //��¼��ѯ���ڶ� ���ڱ����ӡ
        DATE_Start = this.getText("DATE_S");
        DATE_End = this.getText("DATE_E");
    }

    /**
     * ���������� �����뵽STA_OUT_01����
     * @param STADATE String
     */
    private void insertData(String STADATE){
        DecimalFormat df = new DecimalFormat("0.00"); //�趨Double���͸�ʽ
        String StartDate = STADATE + "01"; //ÿ�µ�һ��
        //��ȡ���·ݵ����һ��
        String EndDate = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(STADATE),
                                              "yyyyMMdd");
        TParm p = new TParm();
        p.setData("DATE_S", StartDate);
        p.setData("DATE_E", EndDate);
        //==========pangben modify 20110520 ����������
        p.setData("REGION_CODE", Operator.getRegion());
        //��ȡ �����м�� �������ݺ�
        TParm opd_Daily = STAOut_1Tool.getInstance().selectSTA_OPD_DAILY_Sum(p);
        //��ȡ �������� �������ݺ�
        TParm daily02 = STAOut_1Tool.getInstance().selectSTA_DAILY_02_Sum(p);
        //��ȡ�����ձ��������һ�������  Ϊ�˻�ȡ ʵ�в�������ֻ�ܰ�������ͳ�Ƶ�����
        TParm daily02_day = STAOut_1Tool.getInstance().getSTA_DAILY_02_DAY_SUM(
            EndDate,Operator.getRegion());//==========pangben modify 20110520 ����������
        if (opd_Daily.getErrCode() < 0 || daily02.getErrCode() < 0 ||
            daily02_day.getErrCode() < 0) {
            this.messageBox_("���ݻ��ܴ���");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATA_01", "1");
        parm.setData("DATA_02", daily02.getInt("DATA_02", 0) +
                     daily02.getInt("DATA_03", 0) +
                     opd_Daily.getInt("OTHER_NUM", 0)); //�ܼ�=����+����+����
        parm.setData("DATA_03",
                     daily02.getInt("DATA_02", 0) + daily02.getInt("DATA_03", 0)); //�ż����
        parm.setData("DATA_04", daily02.getData("DATA_02", 0)); //�����˴�
        parm.setData("DATA_05",daily02.getData("DATA_03",0));//�����
        parm.setData("DATA_06",daily02.getData("DATA_04",0));//��������
        parm.setData("DATA_07",daily02.getData("DATA_05",0));//�۲�����
        parm.setData("DATA_08",daily02.getData("DATA_06",0));//�۲�����
        parm.setData("DATA_09",opd_Daily.getData("HRM_NUM",0));//�����������
        parm.setData("DATA_10",daily02.getData("DATA_08",0));//��Ժ����
        parm.setData("DATA_11",daily02.getData("DATA_09",0));//��Ժ�����ܼ�
        parm.setData("DATA_12",daily02.getData("DATA_10",0));//���в���������
        parm.setData("DATA_13",daily02.getData("DATA_11",0));//����
        parm.setData("DATA_14",daily02.getData("DATA_12",0));//��ת
        parm.setData("DATA_15",daily02.getData("DATA_13",0));//δ��
        parm.setData("DATA_16",daily02.getData("DATA_14",0));//����
        parm.setData("DATA_17","");//�����˴�  ����
        parm.setData("DATA_18",daily02_day.getData("DATA_17",0));//ʵ�в�����
        parm.setData("DATA_19",daily02.getData("DATA_18",0));//ʵ�ʿ����ܴ�����
        parm.setData("DATA_20",daily02_day.getData("DATA_19",0));//ƽ�����Ŵ�λ��
        parm.setData("DATA_21",daily02.getData("DATA_20",0));//ʵ��ռ���ܴ�����
        parm.setData("DATA_22",daily02.getData("DATA_21",0));//��Ժ��ռ���ܴ�����
        parm.setData("DATA_23",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_11",0)/daily02.getDouble("DATA_10",0)*100));//������
        parm.setData("DATA_24",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_12",0)/daily02.getDouble("DATA_10",0)*100));//��ת��
        parm.setData("DATA_25",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_14",0)/daily02.getDouble("DATA_10",0)*100));//������
        parm.setData("DATA_26", daily02_day.getInt("DATA_17", 0) == 0 ? "" :
                     df.format((daily02.getDouble("DATA_11", 0) +
                                daily02.getDouble("DATA_12", 0) +
                                daily02.getDouble("DATA_13", 0) +
                                daily02.getDouble("DATA_14", 0) +
                                daily02.getDouble("DATA_15", 0) +
                                daily02.getDouble("DATA_15_1", 0)) /
                               daily02_day.getDouble("DATA_17", 0))
            ); //��λ��ת����
        parm.setData("DATA_27",STATool.getInstance().getDaysOfMonth(STADATE));//����������
        if (daily02.getInt("DATA_09", 0) != 0)
            parm.setData("DATA_29",
                         daily02.getInt("DATA_21", 0) / daily02.getInt("DATA_09", 0)); //��Ժ��ƽ��סԺ��
        else
            parm.setData("DATA_29", 0);
        //ÿ����ÿ���š��������֮��
        if (daily02.getDouble("DATA_02", 0) + daily02.getDouble("DATA_03", 0) != 0)
            parm.setData("DATA_30",
            		StringTool.round((daily02.getDouble("DATA_03", 0) /
                            (daily02.getDouble("DATA_02", 0) +
                                    daily02.getDouble("DATA_03", 0))), 2));
        else
            parm.setData("DATA_30", "");
        //����ÿ���ż�����Ժ����  ���ĵ���ͬ
        if (daily02.getInt("DATA_02", 0) + daily02.getInt("DATA_03", 0) != 0) {
            double chu = daily02.getDouble("DATA_02", 0) +
                daily02.getDouble("DATA_03", 0);
            double DATA_31 = daily02.getDouble("DATA_08", 0) / (chu / 100);
            parm.setData("DATA_31", DATA_31);
        }
        else
            parm.setData("DATA_31", 0);
        //�ż������ռ�����
        double sumZc = opd_Daily.getDouble("OTHER_NUM",0)+daily02.getDouble("DATA_02",0)+daily02.getDouble("DATA_03",0);//�����=����������
        if(sumZc!=0)
            parm.setData("DATA_32",(daily02.getDouble("DATA_02",0)+daily02.getDouble("DATA_03",0))/sumZc*100);
        else
            parm.setData("DATA_32","");
        //���ﲡ����
        if(daily02.getDouble("DATA_03",0)!=0)
            parm.setData("DATA_33",df.format(daily02.getDouble("DATA_04",0)/daily02.getDouble("DATA_03",0)*100));
        else
            parm.setData("DATA_33",0);
        //�۲��Ҳ�����
        if(daily02.getDouble("DATA_05",0)!=0)
            parm.setData("DATA_34",df.format(daily02.getDouble("DATA_06",0)/daily02.getDouble("DATA_05",0)*100));
        else
            parm.setData("DATA_34",0);
        //����ʹ����
        if(daily02.getDouble("DATA_18",0)!=0){
            parm.setData("DATA_28",daily02.getDouble("DATA_20",0)/daily02.getDouble("DATA_18",0)*100);//����ʹ����
        }else{
            parm.setData("DATA_28","");
        }
        //��Ҫ����
        parm.setData("STA_DATE",STADATE);
        parm.setData("CONFIRM_FLG","N");
        parm.setData("CONFIRM_USER",Operator.getID());
        parm.setData("CONFIRM_DATE","");
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        //==============pangben modify 20110520 start ɾ��ʹ��
        parm.setData("REGION_CODE",Operator.getRegion());
        //==============pangben modify 20110520 stop
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STAOut_1Action",
            "insertSTA_OUT_01", parm);
        if(result.getErrCode()<0){
            this.messageBox_("��������ʧ�ܣ�");
            return;
        }
        this.gridBind(STADATE);//���Ѿ����ɵ�����
    }

    /**
     * ����
     */
    public void onSave(){
        //�ж������Ƿ��Ѿ��ύ
        //��������鳤Ȩ��  ��ô�Ѿ��ύ�����ݲ������޸�
        if(!LEADER.equals("2")){
            if (CONFIRM_FLG) {
                this.messageBox_("�����Ѿ��ύ�������ٴ��޸ģ�");
                return;
            }
        }
        String message = "����ɹ���";//��ʾ��Ϣ
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        table1.acceptText();
        table2.acceptText();
        //�������Ƿ�������
        if(table1.getRowCount()<=0||table2.getRowCount()<=0)
            return;
        TDataStore ds = table2.getDataStore();
        ds.showDebug();
        ds.setItem(0,"OPT_USER",Operator.getID());//�����޸���Ա��Ϣ
        ds.setItem(0,"OPT_TERM",Operator.getIP());//�����޸�IP
        ds.setItem(0,"OPT_DATE",SystemTool.getInstance().getDate());//�����޸�ʱ��
        //�ж��Ƿ��ύ
        if(((TCheckBox)this.getComponent("Submit")).isSelected()){
            ds.setItem(0,"CONFIRM_FLG","Y");//����״̬Ϊ �ύ
            ds.setItem(0,"CONFIRM_USER",Operator.getID());//�����ύ��
            ds.setItem(0,"CONFIRM_DATE",SystemTool.getInstance().getDate());//�ύʱ��
            message = "�ύ�ɹ�!";
        }
        TParm parm = new TParm();
        parm.setData("SQL1",table1.getUpdateSQL());
        parm.setData("SQL2",ds.getUpdateSQL());
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAOut_1Action",
            "updateSTA_OUT_01", parm);
        if(re.getErrCode()<0){
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        this.messageBox_(message);
        if(((TCheckBox)this.getComponent("Submit")).isSelected())
                CONFIRM_FLG = true;
    }
    /**
     * ���
     */
    public void onClear(){
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.clearValue("Submit");
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        table1.removeRowAll();
        table1.resetModify();
        table2.removeRowAll();
        table2.resetModify();
        TTable table1R = (TTable)this.getComponent("Table1_Read");
        TTable table2R = (TTable)this.getComponent("Table2_Read");
        table2R.removeRowAll();
        table1R.removeRowAll();
        CONFIRM_FLG = false; //��¼�Ƿ��ύ
        DATA_StaDate = ""; //��¼Ŀǰ��ʾ�����ݵ�����(����)
        S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
        DATE_Start = "";//��¼��ʼ����
        DATE_End = "";//��¼��ֹ����
        DATA_PrintDay = null;
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        if (DATA_StaDate.trim().length() <= 0&&(DATE_Start.length()<=0||DATE_End.length()<=0)) {
            return;
        }
//        if(!CONFIRM_FLG){
//            this.messageBox_("�����ύ��������ɱ���");
//            return;
//        }
        TParm printParm = new TParm();
        //������ʾ������
        String dataDate = "";
        if("month".equals(S_TYPE)){
            dataDate = DATA_StaDate.substring(0, 4) + "��" +
                DATA_StaDate.subSequence(4, 6) + "��";
        }else if("day".equals(S_TYPE)){
            dataDate = DATE_Start+"~"+DATE_End;
        }
        String printDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��");
        TParm Basic = new TParm();//�����������
        Basic.setData("Date",dataDate);//��������
        Basic.setData("Units",Operator.getHospitalCHNFullName());//���λ
        Basic.setData("printDate",printDate);
        TParm data = new TParm();
        if("month".equals(S_TYPE)){//�������ͳ�� ��ѯ���ݿ� ȡ�ø���ͳ����Ϣ
            TParm select = new TParm();
            select.setData("STA_DATE", DATA_StaDate);
            //===========pangben modify 20110523 start
            select.setData("REGION_CODE", Operator.getRegion());
             //===========pangben modify 20110523 stop
            data = STAOut_1Tool.getInstance().selectSTA_OUT_01(select);
        }else if("day".equals(S_TYPE)){//��������ڶ�ͳ�� ��ȡtable�ϵ����ݽ��д�ӡ
            data = DATA_PrintDay;
        }
        if(data==null)
            return;
        if(data.getErrCode()<0){
            return;
        }
        Basic.setData("BC","�������ϣ�ҽԺȫ�꿪���ͥ���� "+data.getValue("DATA_35",0)+" ��");
        printParm.setData("Basic",Basic.getData());
        printParm.setData("Data",data.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_01.jhw", printParm);
    }
    /**
     * ����ͳ�Ʒ�ʽ ��ʾ���������·ݻ���ʱ��οؼ�
     * @param type String "month":��ͳ��  "days":����ͳ��
     */
    private void setDateVisble(String type){
        if("month".equals(type)){//��ͳ��
            this.setText("tLabel_0","ͳ���·�");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(true);//��ʾ�·ݿؼ�
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(false);//�������ڶοؼ�
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(false);//�������ڶοؼ�
            ((TLabel)this.getComponent("tLabel_2")).setVisible(false);
            callFunction("UI|save|setEnabled", true); //���水ť����
            callFunction("UI|Table1_Read|setVisible", false);//������ͳ�Ʊ��
            callFunction("UI|Table2_Read|setVisible", false);
            callFunction("UI|Table1|setVisible", true);//��ʾ��ͳ�Ʊ��
            callFunction("UI|Table2|setVisible", true);
        }
        else if("days".equals(type)){
            this.setText("tLabel_0","ͳ������");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(false);//��ʾ�·ݿؼ�
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(true);//�������ڶοؼ�
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(true);//�������ڶοؼ�
            ((TLabel)this.getComponent("tLabel_2")).setVisible(true);
            callFunction("UI|save|setEnabled", false); //���水ť����
            callFunction("UI|Table1_Read|setVisible", true);//��ʾ��ͳ�Ʊ��
            callFunction("UI|Table2_Read|setVisible", true);
            callFunction("UI|Table1|setVisible", false);//������ͳ�Ʊ��
            callFunction("UI|Table2|setVisible", false);
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
    /**
     * ���ֵ�ı��¼�
     */
    public void onCellChange(Object obj) {
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        //��ǰ�༭�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        int colunm = node.getColumn();//��ȡѡ����
        DecimalFormat df = new DecimalFormat("0.00");
        double ban = 0;//��¼�۲���������
        if(node.getTable().getTag().equals("Table1")&&colunm==7){
            if(table1.getItemDouble(0,6)>0){
                ban = Double.valueOf(node.getValue().toString())/table1.getItemDouble(0,6)*100;
                table2.setItem(0,"DATA_34",df.format(ban));
            }
        }
        if(node.getTable().getTag().equals("Table1")&&colunm==6){
            if(Integer.valueOf(node.getValue().toString())>0){
                ban = table1.getItemDouble(0,7)/Integer.valueOf(node.getValue().toString())*100;
                table2.setItem(0,"DATA_34",df.format(ban));
            }
        }
    }
}
