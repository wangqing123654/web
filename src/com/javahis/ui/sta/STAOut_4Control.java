package com.javahis.ui.sta;

import com.dongyang.control.*;
import jdo.sta.STAOut_4Tool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sys.Operator;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import java.text.DecimalFormat;

/**
 * <p>Title: ���������ؼ�������ҽԺ���Ѽ���֧���������ͳ2��3��</p>
 *
 * <p>Description: ���������ؼ�������ҽԺ���Ѽ���֧���������ͳ2��3��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_4Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false;//��¼�Ƿ��ύ
    private String DATA_StaDate = "";//��¼Ŀǰ��ʾ�����ݵ�����(����)
    private TTable table1;
    private TTable table2;
    private TTable table3;
    private TTable table4;
    private String LEADER = "";//��¼�Ƿ����鳤Ȩ��  ���LEADER=2��ô�����鳤Ȩ��
    /**
     * ��ʼ��
     */
    public void onInit(){
        super.init();
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        table1 = (TTable)this.getComponent("Table1");
        table2 = (TTable)this.getComponent("Table2");
        table3 = (TTable)this.getComponent("Table3");
        table4 = (TTable)this.getComponent("Table4");
        this.addEventListener("Table1->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        this.addEventListener("Table2->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        this.addEventListener("Table3->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        this.addEventListener("Table4->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        //��ʼ��Ȩ��
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
    }
    public void onClear(){
        this.clearValue("STA_DATE;Submit");
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        STA_CONFIRM_FLG = false;
        String STA_DATE = this.getText("STA_DATE").replace("/","");
        if(STA_DATE.trim().length()<=0){
            this.messageBox_("��ѡ��Ҫͳ�Ƶ�����");
            return;
        }
        //����Ƿ����������������
        if(!canGeneration(STA_DATE)){
            //������������������ݣ���ԭ������
            gridBind(STA_DATE);//���ݰ�
            return;
        }
        TParm parm = STAOut_4Tool.getInstance().selectData(STA_DATE,Operator.getRegion());//=========pangben modify 20110523
        if(parm.getErrCode()<0){
            this.messageBox_("����ͳ�ƴ��� " +parm.getErrText() + parm.getErrName());
            return;
        }
        //���������Ϣ
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        //===========pangben modify 20110523 start
        parm.setData("REGION_CODE",Operator.getRegion());
       //===========pangben modify 20110523 stop
        parm.setData("CONFIRM_FLG","N");
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STAOut_4Action",
            "insertSTA_OUT_04", parm);
        if(result.getErrCode()<0){
            this.messageBox_("��������ʧ�ܣ�"+result.getErrText());
            return;
        }
        this.messageBox_("�����������");
        gridBind(STA_DATE);
    }
    /**
     * ������ݰ�
     */
    private void gridBind(String STADATE){
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        TTable table3 = (TTable)this.getComponent("Table3");
        TTable table4 = (TTable)this.getComponent("Table4");
        table1.setSQL(STASQLTool.getInstance().getSTA_OUT_04_1(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table1.retrieve();
        table1.setDSValue();
        table2.setSQL(STASQLTool.getInstance().getSTA_OUT_04_2(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table2.retrieve();
        table2.setDSValue();
        table3.setSQL(STASQLTool.getInstance().getSTA_OUT_04_3(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table3.retrieve();
        table3.setDSValue();
        table4.setSQL(STASQLTool.getInstance().getSTA_OUT_04_4(STADATE,Operator.getRegion()));//=========pangben modify 20110523
        table4.retrieve();
        table4.setDSValue();
        DATA_StaDate = STADATE;
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
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_OUT_04",STADATE,Operator.getRegion());//====pangben modify 20110523
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
     * ����
     */
    public void onSave(){
        //��������鳤Ȩ��  ��ô�Ѿ��ύ�����ݲ������޸�
        if(!LEADER.equals("2")){
            //�ж������Ƿ��Ѿ��ύ
            if (STA_CONFIRM_FLG) {
                this.messageBox_("�����Ѿ��ύ�������ٴ��޸ģ�");
                return;
            }
        }
        String message = "����ɹ���";//��ʾ��Ϣ
        table1.acceptText();
        table2.acceptText();
        table3.acceptText();
        table4.acceptText();
        //�������Ƿ�������
        if(table1.getRowCount()<=0||table2.getRowCount()<=0||table3.getRowCount()<=0)
            return;
        TDataStore ds = table3.getDataStore();
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
        parm.setData("SQL2",table2.getUpdateSQL());
        parm.setData("SQL3",ds.getUpdateSQL());
        parm.setData("SQL4",table4.getUpdateSQL());
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAOut_4Action",
            "updateSTA_OUT_04", parm);
        if(re.getErrCode()<0){
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        this.messageBox_(message);
        if(((TCheckBox)this.getComponent("Submit")).isSelected())
            STA_CONFIRM_FLG = true;
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        if (DATA_StaDate.trim().length() <= 0) {
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
//        if (!STA_CONFIRM_FLG) {
//            this.messageBox_("�����ύ��������ɱ���");
//            return;
//        }
        TParm printParm = new TParm();
        String dataDate = DATA_StaDate.substring(0,4) + "��" + DATA_StaDate.subSequence(4,6) + "��";
        TParm Basic = new TParm();//�����������
        Basic.setData("Date",dataDate);//��������
        Basic.setData("Unit",Operator.getHospitalCHNFullName());//���λ
        TParm dataP = STAOut_4Tool.getInstance().selectPrint(DATA_StaDate,Operator.getRegion());//=======pangben modify 20110523
        DecimalFormat df = new DecimalFormat("0.00");
        TParm data = new TParm();
        data.addData("DATA_01",dataP.getValue("DATA_01",0));
        data.addData("DATA_02",dataP.getValue("DATA_02",0));
        data.addData("DATA_03",dataP.getValue("DATA_03",0));
        data.addData("DATA_04",dataP.getValue("DATA_04",0));
        data.addData("DATA_05",dataP.getValue("DATA_05",0));
        data.addData("DATA_06",dataP.getValue("DATA_06",0));
        data.addData("DATA_07",dataP.getValue("DATA_07",0));
        data.addData("DATA_08",dataP.getValue("DATA_08",0));
        data.addData("DATA_09",dataP.getValue("DATA_09",0));
        data.addData("DATA_10",dataP.getValue("DATA_10",0));
        data.addData("DATA_11",dataP.getValue("DATA_11",0));
        data.addData("DATA_12",df.format(dataP.getDouble("DATA_12",0)));
        data.addData("DATA_13",df.format(dataP.getDouble("DATA_13",0)));
        data.addData("DATA_14",df.format(dataP.getDouble("DATA_14",0)));
        data.addData("DATA_15",df.format(dataP.getDouble("DATA_15",0)));
        data.addData("DATA_16",df.format(dataP.getDouble("DATA_16",0)));
        data.addData("DATA_17",df.format(dataP.getDouble("DATA_17",0)));
        data.addData("DATA_18",df.format(dataP.getDouble("DATA_18",0)));
        data.addData("DATA_19",df.format(dataP.getDouble("DATA_19",0)));
        data.addData("DATA_20",df.format(dataP.getDouble("DATA_20",0)));
        data.addData("DATA_21",df.format(dataP.getDouble("DATA_21",0)));
        data.addData("DATA_22",df.format(dataP.getDouble("DATA_22",0)));
        data.addData("DATA_23",df.format(dataP.getDouble("DATA_23",0)));
        data.addData("DATA_24",df.format(dataP.getDouble("DATA_24",0)));
        data.addData("DATA_25",df.format(dataP.getDouble("DATA_25",0)));
        data.addData("DATA_26",df.format(dataP.getDouble("DATA_26",0)));
        data.addData("DATA_27",df.format(dataP.getDouble("DATA_27",0)));
        data.addData("DATA_28",df.format(dataP.getDouble("DATA_28",0)));
        data.addData("DATA_29",df.format(dataP.getDouble("DATA_29",0)));
        data.addData("DATA_30",df.format(dataP.getDouble("DATA_30",0)));
        data.addData("DATA_31",df.format(dataP.getDouble("DATA_31",0)));
        data.addData("DATA_32",df.format(dataP.getDouble("DATA_32",0)));
        data.addData("DATA_33",df.format(dataP.getDouble("DATA_33",0)));
        data.addData("DATA_34",df.format(dataP.getDouble("DATA_34",0)));
        data.addData("DATA_35",df.format(dataP.getDouble("DATA_35",0)));
        data.addData("DATA_36",df.format(dataP.getDouble("DATA_36",0)));
        data.addData("DATA_37",df.format(dataP.getDouble("DATA_37",0)));
        data.addData("DATA_38",df.format(dataP.getDouble("DATA_38",0)));
        data.addData("DATA_39",df.format(dataP.getDouble("DATA_39",0)));
        data.addData("DATA_40",df.format(dataP.getDouble("DATA_40",0)));
        data.addData("DATA_41",df.format(dataP.getDouble("DATA_41",0)));
        data.addData("DATA_42",dataP.getValue("DATA_42",0));
        data.addData("DATA_43",df.format(dataP.getDouble("DATA_43",0)));
        data.addData("DATA_44",df.format(dataP.getDouble("DATA_44",0)));
        data.addData("DATA_45",df.format(dataP.getDouble("DATA_45",0)));
        data.addData("DATA_46",df.format(dataP.getDouble("DATA_46",0)));
        data.addData("DATA_47",df.format(dataP.getDouble("DATA_47",0)));
        data.addData("DATA_48",df.format(dataP.getDouble("DATA_48",0)));
        data.addData("DATA_49",df.format(dataP.getDouble("DATA_49",0)));
        data.addData("DATA_50",df.format(dataP.getDouble("DATA_50",0)));
        data.addData("DATA_51",df.format(dataP.getDouble("DATA_51",0)));
        data.addData("DATA_52",df.format(dataP.getDouble("DATA_52",0)));
        data.addData("DATA_53",df.format(dataP.getDouble("DATA_53",0)));
        data.addData("DATA_54",df.format(dataP.getDouble("DATA_54",0)));
        data.addData("DATA_55",df.format(dataP.getDouble("DATA_55",0)));
        data.addData("DATA_56",df.format(dataP.getDouble("DATA_56",0)));
        data.addData("DATA_57",df.format(dataP.getDouble("DATA_57",0)));
        data.addData("DATA_58",df.format(dataP.getDouble("DATA_58",0)));
        data.addData("DATA_59",df.format(dataP.getDouble("DATA_59",0)));
        data.addData("DATA_60",df.format(dataP.getDouble("DATA_60",0)));
        data.addData("DATA_61",df.format(dataP.getDouble("DATA_61",0)));
        data.setCount(1);
        printParm.setData("Basic",Basic.getData());
        printParm.setData("Data",data.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_04.jhw", printParm);
    }

    /**
     * ��Ԫ��ֵ�ı�ʱ��
     */
    public void onCellChange(Object obj) {
        //��ǰ�༭�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        TTable table = node.getTable();
        table1.acceptText();
        table2.acceptText();
        table3.acceptText();
        table4.acceptText();
        DecimalFormat df = new DecimalFormat("0.00");
        String tableName = table.getTag(); //����
//        this.messageBox_(tableName);
        int colunm = node.getColumn(); //��ȡѡ����
        if("Table2".equals(tableName)&&(colunm==3||colunm==4||colunm==5)){
            double DATA_15,DATA_16,DATA_17;
            if(colunm==3)
                DATA_15 = Double.valueOf(node.getValue().toString());
            else
                DATA_15 = table2.getItemDouble(0, "DATA_15");
            if(colunm==4)
                DATA_16 = Double.valueOf(node.getValue().toString());
            else
                DATA_16 = table2.getItemDouble(0, "DATA_16");
            if(colunm==5)
                DATA_17 = Double.valueOf(node.getValue().toString());
            else
                DATA_17 = table2.getItemDouble(0, "DATA_17");
            //��������С��
            table2.setItem(0, "DATA_14",DATA_15+DATA_16+DATA_17);
        }
        if("Table2".equals(tableName)&&(colunm==7||colunm==8||colunm==9||colunm==10)){
            double DATA_19,DATA_20,DATA_21,DATA_22;
            if(colunm==7)
                DATA_19 = Double.valueOf(node.getValue().toString());
            else
                DATA_19 = table2.getItemDouble(0, "DATA_19");
            if(colunm==8)
                DATA_20 = Double.valueOf(node.getValue().toString());
            else
                DATA_20 = table2.getItemDouble(0, "DATA_20");
            if(colunm==9)
                DATA_21 = Double.valueOf(node.getValue().toString());
            else
                DATA_21 = table2.getItemDouble(0, "DATA_21");
            if(colunm==10)
                DATA_22 = Double.valueOf(node.getValue().toString());
            else
                DATA_22 = table2.getItemDouble(0, "DATA_22");

            //סԺС��
            table2.setItem(0, "DATA_18",DATA_19+DATA_20+DATA_21+DATA_22);
        }
        if("Table2".equals(tableName)&&(colunm==12||colunm==13)){
            double DATA_24,DATA_25;
            if(colunm==12)
                DATA_24 = Double.valueOf(node.getValue().toString());
            else
                DATA_24 = table2.getItemDouble(0, "DATA_24");
            if(colunm==13)
                DATA_25 = Double.valueOf(node.getValue().toString());
            else
                DATA_25 = table2.getItemDouble(0, "DATA_25");
            //ҩƷ����С��
            table2.setItem(0, "DATA_23",DATA_24 +DATA_25);
        }
        if("Table2".equals(tableName)&&(colunm==2||colunm==6||colunm==11)){
            double DATA_14,DATA_18,DATA_23;
            if(colunm==2)
                DATA_14 = Double.valueOf(node.getValue().toString());
            else
                DATA_14 = table2.getItemDouble(0, "DATA_14");
            if(colunm==6)
                DATA_18 = Double.valueOf(node.getValue().toString());
            else
                DATA_18 = table2.getItemDouble(0, "DATA_18");
            if(colunm==11)
                DATA_23 = Double.valueOf(node.getValue().toString());
            else
                DATA_23 = table2.getItemDouble(0, "DATA_23");
            //����ҵ������ϼ�=��������С��+סԺС��+ҩƷ����С��
            table2.setItem(0, "DATA_13",DATA_14+DATA_18+DATA_23);
        }
        if("Table2".equals(tableName)&&(colunm==1||colunm==14||colunm==15||colunm==16)){
            double DATA_13,DATA_26,DATA_27,DATA_28;
            if(colunm==1)
                DATA_13 = Double.valueOf(node.getValue().toString());
            else
                DATA_13 = table2.getItemDouble(0, "DATA_13");
            if(colunm==14)
                DATA_26 = Double.valueOf(node.getValue().toString());
            else
                DATA_26 = table2.getItemDouble(0, "DATA_26");
            if(colunm==15)
                DATA_27 = Double.valueOf(node.getValue().toString());
            else
                DATA_27 = table2.getItemDouble(0, "DATA_27");
            if(colunm==16)
                DATA_28 = Double.valueOf(node.getValue().toString());
            else
                DATA_28 = table2.getItemDouble(0, "DATA_28");
            //���������ܼ� = ҵ�������ܼ�+ҵ����+ר���+��������
            table2.setItem(0, "DATA_12",DATA_13 +DATA_26+DATA_27+DATA_28);
        }
        if("Table3".equals(tableName)&&(colunm==2||colunm==4||colunm==5)){
            double DATA_31,DATA_33,DATA_34;
            if(colunm==2)
                DATA_31 = Double.valueOf(node.getValue().toString());
            else
                DATA_31 = table3.getItemDouble(0, "DATA_31");
            if(colunm==4)
                DATA_33 = Double.valueOf(node.getValue().toString());
            else
                DATA_33 = table3.getItemDouble(0, "DATA_33");
            if(colunm==5)
                DATA_34 = Double.valueOf(node.getValue().toString());
            else
                DATA_34 = table3.getItemDouble(0, "DATA_34");
            //�����3  ҵ��֧���ϼ� = ��Ա����С�� + ҩƷ�� + ����ҵ��֧��
            table3.setItem(0, "DATA_30",DATA_31+DATA_33+DATA_34);
        }
        if("Table3".equals(tableName)&&(colunm==1||colunm==6)){
            double DATA_30,DATA_35;
            if(colunm==1)
                DATA_30 = Double.valueOf(node.getValue().toString());
            else
                DATA_30 = table3.getItemDouble(0, "DATA_30");
            if(colunm==6)
                DATA_35 = Double.valueOf(node.getValue().toString());
            else
                DATA_35 = table3.getItemDouble(0, "DATA_35");
            //�����3  ֧���ܼ� = ҵ��֧���ϼ� + ����֧��
            table3.setItem(0, "DATA_29",DATA_30+DATA_35);
        }
        //ƽ��ÿ�����˴�ҽ�Ʒ� ҩ�� DATA_25/DATA_05
        if (("Table2".equals(tableName) && colunm == 13) ||
            ("Table1".equals(tableName) && colunm == 4)) {
            double DATA_05,DATA_25;
            if ("Table1".equals(tableName) && colunm == 4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0, "DATA_05");
            if ("Table2".equals(tableName) && colunm == 13)
                DATA_25 = Double.valueOf(node.getValue().toString());
            else
                DATA_25 = table2.getItemDouble(0, "DATA_25");
            if (DATA_05 != 0) {
                table4.setItem(0, "DATA_48",
                               df.format(DATA_25 / DATA_05));
            }
        }
        //ƽ��ÿ�����˴�ҽ�Ʒ� ���� DATA_16/DATA_05
        if (("Table2".equals(tableName) && colunm == 4) ||
            ("Table1".equals(tableName) && colunm == 4)) {
            double DATA_05,DATA_16;
            if ("Table1".equals(tableName) && colunm == 4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0, "DATA_05");
            if ("Table2".equals(tableName) && colunm == 4)
                DATA_16 = Double.valueOf(node.getValue().toString());
            else
                DATA_16 = table2.getItemDouble(0, "DATA_16");
            if (DATA_05 != 0) {
                table4.setItem(0, "DATA_49",
                               df.format(DATA_16 / DATA_05));
            }
        }
        //ƽ��ÿ�����˴�ҽ�Ʒ� �ϼ� DATA_48+DATA_49
        if (("Table1".equals(tableName) && colunm == 4)||
            ("Table2".equals(tableName) && colunm == 2)||
            ("Table2".equals(tableName) && colunm ==13)) {
            double DATA_14,DATA_25,DATA_05;
            if ("Table1".equals(tableName) && colunm == 4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0, "DATA_05");
            if ("Table2".equals(tableName) && colunm == 2)
                DATA_14 = Double.valueOf(node.getValue().toString());
            else
                DATA_14 = table2.getItemDouble(0, "DATA_14");
            if ("Table2".equals(tableName) && colunm == 13)
                DATA_25 = Double.valueOf(node.getValue().toString());
            else
                DATA_25 = table2.getItemDouble(0, "DATA_25");
            table4.setItem(0, "DATA_47",
                           df.format((DATA_14+DATA_25)/DATA_05));
        }
        double DATA_06;
        if("Table1".equals(tableName)&&colunm==5)
            DATA_06 = Double.valueOf(node.getValue().toString());
        else
            DATA_06 = table1.getItemDouble(0, "DATA_06");
        if(DATA_06!=0){
            double DATA_19,DATA_24,DATA_20,DATA_21;
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ��λ�� DATA_19/DATA_06
            if ("Table2".equals(tableName) && colunm == 7)
                DATA_19 = Double.valueOf(node.getValue().toString());
            else
                DATA_19 = table2.getItemDouble(0, "DATA_19");
            table4.setItem(0, "DATA_51", df.format(DATA_19 / DATA_06));

            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ҩ�� DATA_24/DATA_06
            if("Table2".equals(tableName)&&colunm==12)
                DATA_24 = Double.valueOf(node.getValue().toString());
            else
                DATA_24 = table2.getItemDouble(0, "DATA_24");
            table4.setItem(0, "DATA_52",df.format(DATA_24/DATA_06));
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ���Ʒ� DATA_20/DATA_06
            if("Table2".equals(tableName)&&colunm==8)
                DATA_20 = Double.valueOf(node.getValue().toString());
            else
                DATA_20 = table2.getItemDouble(0, "DATA_20");
            table4.setItem(0, "DATA_53",
                           df.format(DATA_20/DATA_06));
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ���� DATA_21/DATA_06
            if("Table2".equals(tableName)&&colunm==9)
                DATA_21 = Double.valueOf(node.getValue().toString());
            else
                DATA_21 = table2.getItemDouble(0, "DATA_21");
            table4.setItem(0, "DATA_54",
                           df.format(DATA_21/DATA_06));
            //��Ժ��ƽ��ÿ��סԺҽ�Ʒ�
            double DATA_11;
            if("Table1".equals(tableName)&&colunm==12)
                DATA_11 = Double.valueOf(node.getValue().toString());
            else
                DATA_11 = table1.getItemDouble(0, "DATA_11");
            if(DATA_11!=0){
                double DATA_18;
                if("Table2".equals(tableName)&&colunm==6)
                    DATA_18 = Double.valueOf(node.getValue().toString());
                else
                    DATA_18 = table2.getItemDouble(0, "DATA_18");
                double DATA_10;
                if("Table1".equals(tableName)&&colunm==9)
                    DATA_10 = Double.valueOf(node.getValue().toString());
                else
                    DATA_10 = table1.getItemDouble(0, "DATA_10");
                if(DATA_10!=0)
                    table4.setItem(0, "DATA_55",df.format((DATA_18+DATA_24)/DATA_10));
            }
        }
        //ƽ��ÿһ��Ժ��ҽ�Ʒ� �ϼ� DATA_51+DATA_52+DATA_53+DATA_54
        if ("Table4".equals(tableName) && (colunm == 6||colunm == 7||colunm == 8||colunm == 9)) {
            double DATA_51,DATA_52,DATA_53,DATA_54;
            if("Table4".equals(tableName)&&colunm==6)
                DATA_51 = Double.valueOf(node.getValue().toString());
            else
                DATA_51 = table4.getItemDouble(0, "DATA_51");
            if("Table4".equals(tableName)&&colunm==7)
                DATA_52 = Double.valueOf(node.getValue().toString());
            else
                DATA_52 = table4.getItemDouble(0, "DATA_52");
            if("Table4".equals(tableName)&&colunm==8)
                DATA_53 = Double.valueOf(node.getValue().toString());
            else
                DATA_53 = table4.getItemDouble(0, "DATA_53");
            if("Table4".equals(tableName)&&colunm==9)
                DATA_54 = Double.valueOf(node.getValue().toString());
            else
                DATA_54 = table4.getItemDouble(0, "DATA_54");
            table4.setItem(0, "DATA_50",
                           df.format(DATA_51+DATA_52+DATA_53+DATA_54));
        }
        //ƽ��ÿ��ռ�ù̶��ʲ��ܽ���еĺϼ�=�̶��ʲ��ܽ��/������(DATA_36/DATA_02)
        if(("Table1".equals(tableName)&&colunm==1)||("Table3".equals(tableName)&&colunm==7)){
            double DATA_02,DATA_36;
            if("Table1".equals(tableName)&&colunm==1)
                DATA_02 = Double.valueOf(node.getValue().toString());
            else
                DATA_02 = table1.getItemDouble(0, "DATA_02");
            if("Table3".equals(tableName)&&colunm==7)
                DATA_36 = Double.valueOf(node.getValue().toString());
            else
                DATA_36 = table3.getItemDouble(0, "DATA_36");
            if(DATA_02!=0)
                table3.setItem(0, "DATA_38",
                               df.format(DATA_36/DATA_02));
        }
        //ƽ��ÿ��ռ�ù̶��ʲ��ܽ���е�רҵ�豸���=�̶��ʲ��ܽ���רҵ�豸���/������(DATA_37/DATA_02)
        if(("Table1".equals(tableName)&&colunm==1)||("Table3".equals(tableName)&&colunm==8)){
            double DATA_02,DATA_37;
            if("Table1".equals(tableName)&&colunm==1)
                DATA_02 = Double.valueOf(node.getValue().toString());
            else
                DATA_02 = table1.getItemDouble(0, "DATA_02");
            if("Table3".equals(tableName)&&colunm==8)
                DATA_37 = Double.valueOf(node.getValue().toString());
            else
                DATA_37 = table3.getItemDouble(0, "DATA_37");
            if(DATA_02!=0)
                table3.setItem(0, "DATA_39",
                               df.format(DATA_37/DATA_02));
        }
        //���ڲ���Ƿ����=���ڲ���Ƿ���ܶ�/ҵ������ϼ� (DATA_40/DATA_13)
        if(("Table2".equals(tableName)&&colunm==1)||("Table3".equals(tableName)&&colunm==11)){
            double DATA_40,DATA_13;
            if("Table2".equals(tableName)&&colunm==1)
                DATA_13 = Double.valueOf(node.getValue().toString());
            else
                DATA_13 = table2.getItemDouble(0, "DATA_13");
            if("Table3".equals(tableName)&&colunm==11)
                DATA_40 = Double.valueOf(node.getValue().toString());
            else
                DATA_40 = table3.getItemDouble(0, "DATA_40");
            if(DATA_13!=0)
                table3.setItem(0,"DATA_41",df.format(DATA_40/DATA_13*100));
        }
        //ƽ��ÿ�������˴�=�������˴�/�·���
        int days = STATool.getInstance().getDaysOfMonth(DATA_StaDate);
        if("Table1".equals(tableName)&&colunm==4)
            table3.setItem(0,"DATA_42",df.format(Double.valueOf(node.getValue().toString())/(double)days));
        //������ת����=��Ժ����/ƽ�����Ų����� (DATA_06/DATA_08)
        if("Table1".equals(tableName)&&(colunm==5||colunm==7)){
            double DATA_08;
            if(colunm==7)
                DATA_08 = Double.valueOf(node.getValue().toString());
            else
                DATA_08 = table1.getItemDouble(0, "DATA_08");
            if(DATA_08!=0)
                table3.setItem(0,"DATA_44",df.format(DATA_06/DATA_08));
        }
        //ƽ��ҩƷ�ӳ���(%) = ��ҩƷ����С�ƣ�DATA_23��-ҵ��֧��ҩƷ�ѣ�DATA_33����/ҵ��֧��ҩƷ�ѣ�DATA_33��
        if(("Table2".equals(tableName)&&colunm==11)||("Table3".equals(tableName)&&colunm==4)){
            double DATA_23,DATA_33;
            if("Table2".equals(tableName)&&colunm==11)
                DATA_23 = Double.valueOf(node.getValue().toString());
            else
                DATA_23 = table2.getItemDouble(0, "DATA_23");
            if("Table3".equals(tableName)&&colunm==4)
                DATA_33 = Double.valueOf(node.getValue().toString());
            else
                DATA_33 = table3.getItemDouble(0, "DATA_33");
            if(DATA_33!=0)
                table4.setItem(0,"DATA_46",df.format((DATA_23-DATA_33)/DATA_33*100));
        }
        //ƽ��ÿһ��Ժ��ҽ�Ʒ�={סԺ����С��+ҩƷ���루סԺ��}/��Ժ����
        if("Table2".equals(tableName)&&(colunm==6||colunm==12)||("Table1".equals(tableName)&&colunm==5)){
            double DATA_18,DATA_24;
            if("Table2".equals(tableName)&&colunm==6)
                DATA_18 = Double.valueOf(node.getValue().toString());
            else
                DATA_18 = table2.getItemDouble(0, "DATA_18");
            if("Table2".equals(tableName)&&colunm==12)
                DATA_24 = Double.valueOf(node.getValue().toString());
            else
                DATA_24 = table2.getItemDouble(0, "DATA_24");
            if(DATA_06!=0)
                table4.setItem(0,"DATA_50",df.format((DATA_18+DATA_24)/DATA_06));
        }
        //ÿһְ�� �� ÿһҽʦ ���ּ���
        if(("Table1".equals(tableName)&&(colunm==2||colunm==3||colunm==4||colunm==8))||("Table2".equals(tableName)&&colunm==1)){
            double DATA_05,DATA_09,DATA_13,DATA_03,DATA_04;
            if("Table1".equals(tableName)&&colunm==2)
                DATA_03 = Double.valueOf(node.getValue().toString());
            else
                DATA_03 = table1.getItemDouble(0,"DATA_03");
            if("Table1".equals(tableName)&&colunm==3)
                DATA_04 = Double.valueOf(node.getValue().toString());
            else
                DATA_04 = table1.getItemDouble(0,"DATA_04");
            if("Table1".equals(tableName)&&colunm==4)
                DATA_05 = Double.valueOf(node.getValue().toString());
            else
                DATA_05 = table1.getItemDouble(0,"DATA_05");
            if("Table1".equals(tableName)&&colunm==8)
                DATA_09 = Double.valueOf(node.getValue().toString());
            else
                DATA_09 = table1.getItemDouble(0,"DATA_09");
            if("Table2".equals(tableName)&&colunm==1)
                DATA_13 = Double.valueOf(node.getValue().toString());
            else
                DATA_13 = table2.getItemDouble(0,"DATA_13");
            if(DATA_03>0){
                table4.setItem(0,"DATA_56",df.format(DATA_05/DATA_03));
                table4.setItem(0,"DATA_57",df.format(DATA_09/DATA_03));
                table4.setItem(0,"DATA_58",df.format(DATA_13/DATA_03));
            }
            if(DATA_04>0){
                table4.setItem(0,"DATA_59",df.format(DATA_05/DATA_04));
                table4.setItem(0,"DATA_60",df.format(DATA_09/DATA_04));
                table4.setItem(0,"DATA_61",df.format(DATA_13/DATA_04));
            }
        }
    }
}
