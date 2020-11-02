package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sta.STATool;
import com.dongyang.ui.TTable;
import jdo.sta.STAIn_05Tool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import java.text.DecimalFormat;
import jdo.sta.STASQLTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TLabel;
import jdo.sta.STADeptListTool;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: STA_IN_05��Ժ����Դ��������Ŀ����</p>
 *
 * <p>Description: STA_IN_05��Ժ����Դ��������Ŀ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-24
 * @version 1.0
 */
public class STAIn_05Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false; //��¼�Ƿ��ύ
    private String DATA_StaDate = ""; //��¼Ŀǰ��ʾ�����ݵ�����(����)
    private String S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
    private String DATE_Start = "";//��¼��ʼ����
    private String DATE_End = "";//��¼��ֹ����
    private String DAY_DEPT = "";//��¼�������ڶ�ͳ��ʱͳ�ƵĿ���
    private String LEADER = "";//��¼�Ƿ����鳤Ȩ��  ���LEADER=2��ô�����鳤Ȩ��
    DecimalFormat df = new DecimalFormat("0.00");

    /**
     * ҳ���ʼ��
     */
    public void onInit() {
        super.init();
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.setValue("PRINT_TYPE","3");
        //��ʼ��Ȩ��
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
        //===============pangben modify 20110526 start
        //����
        TTextFormat DEPT_CODE = (TTextFormat)this.getComponent(
                "DEPT_CODE");
        //�����������
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL() +
                                      " AND REGION_CODE='" +
                                      Operator.getRegion() +
                                      "' ORDER BY DEPT_CODE");
        else
            DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL()+" ORDER BY DEPT_CODE");
        DEPT_CODE.onQuery();
        //===============pangben modify 20110526 stop
    }
    /**
     * ���
     */
    public void onClear() {
        //���ó�ʼʱ��
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.clearValue("Submit");
        TTable table1 = (TTable)this.getComponent("Table");
        table1.removeRowAll();
        //table1.resetModify();
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.removeRowAll();
        TTextFormat dept = (TTextFormat)this.getComponent("DEPT_CODE");
        dept.setText("");
        dept.setValue("");
        this.setValue("PRINT_TYPE","3");//��ʼ����ӡģʽ
        STA_CONFIRM_FLG = false; //��¼�Ƿ��ύ
        DATA_StaDate = ""; //��¼Ŀǰ��ʾ�����ݵ�����(����)
        S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
        DATE_Start = "";//��¼��ʼ����
        DATE_End = "";//��¼��ֹ����
        DAY_DEPT = "";//��¼�������ڶ�ͳ��ʱͳ�ƵĿ���
    }
    /**
     * ��ѯ
     */
    public void onQuery() {
       // TTable table = (TTable)this.getComponent("Table");
        String dept="";
        if (DATA_StaDate.length() > 0) {
            dept = this.getValueString("DEPT_CODE");
        }
        String STA_DATE = this.getText("STA_DATE").replace("/", "");
        gridBind(STA_DATE, dept);
    }
    /**
     * ��������
     */
    public void onGenerate(){
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
        //��ղ���Combo
        TTextFormat dept = (TTextFormat)this.getComponent("DEPT_CODE");
        dept.setText("");
        dept.setValue("");

        STA_CONFIRM_FLG = false; //��ʼĬ������û���ύ
        String STA_DATE = this.getText("STA_DATE").replace("/", "");
        if (STA_DATE.trim().length() <= 0) {
            this.messageBox_("��ѡ��ͳ������!");
            return;
        }
        //����Ƿ����������������
        if (!canGeneration(STA_DATE)) {
            //������������������ݣ���ԭ������
            gridBind(STA_DATE,""); //���ݰ�
            return;
        }
        String StartDate = STA_DATE + "01";//ÿ�µ�һ��
        //��ȡ���·ݵ����һ��
        String EndDate = StringTool.getString(STATool.getInstance().getLastDayOfMonth(STA_DATE),"yyyyMMdd");
        TParm parm = new TParm();
        parm.setData("DATE_S",StartDate);
        parm.setData("DATE_E",EndDate);
        parm.setData("REGION_CODE",Operator.getRegion());//==============pangben modify 20110524
        TParm data = STAIn_05Tool.getInstance().selectData(parm);
        if (data.getErrCode() < 0) {
            this.messageBox_("ͳ�����ݴ���" + data.getErrName() + data.getErrText());
            return;
        }
        String userID = Operator.getID();
        String IP = Operator.getIP();
        for (int i = 0; i < data.getCount("STA_DATE"); i++) {
            data.setData("STA_DATE", i, STA_DATE);
            data.setData("CONFIRM_FLG", i, "N");
            data.setData("CONFIRM_USER", i, "");
            data.setData("CONFIRM_DATE", i, "");
            data.setData("OPT_USER", i, userID);
            data.setData("OPT_TERM", i, IP);
            data.setData("REGION_CODE", i,Operator.getRegion());//==============pangben modify 20110525
        }
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAIn_05Action",
            "insertSTA_IN_05", data);
        if (re.getErrCode() < 0) {
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        this.messageBox_("���ɳɹ���");
        gridBind(STA_DATE,""); //�������ɵ�����
    }
    /**
    * �������ڶα�������
    */
   private void generate_DayData() {
       S_TYPE = "day"; //��¼ͳ������
       String Dept = getValueString("DEPT_CODE");
       String IPD_DEPT_CODE = ""; //סԺ����CODE
       String STATION_CODE = "";//סԺ����CODE
       //���ѡ����ͳ�ƿ��� ��ȡ�ÿ��ҵ� IPD_DEPT_CODE
       if (Dept.trim().length() > 0) {
           //����ѡ�еĿ���CODE��ѯ������ϸ��Ϣ
           TParm deptInfo = STADeptListTool.getInstance().selectNewIPDDeptCode(this.
               getValueString("DEPT_CODE"),Operator.getRegion());//=========pangben modify 20110525
           if (deptInfo.getErrCode() < 0) {
               return;
           }
           IPD_DEPT_CODE = deptInfo.getValue("IPD_DEPT_CODE", 0);
       }
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
       if (IPD_DEPT_CODE.length() > 0) {
           parm.setData("DEPT_CODE", IPD_DEPT_CODE);
       }
       if(STATION_CODE.length()>0){
           parm.setData("OUT_STATION", STATION_CODE);
       }
       if(Dept.length()>0){
           parm.setData("DEPT", Dept);//�м����CODE
       }
       parm.setData("REGION_CODE",Operator.getRegion());//=========pangben modify 20110525
       TParm result = STAIn_05Tool.getInstance().selectData(parm);
       if (result.getErrCode() < 0) {
           this.messageBox_("����ʧ�� " + result.getErrText());
           return;
       }
       DAY_DEPT = Dept;//��¼ͳ�ƵĿ���
       gridBind(result);
   }

    /**
     * ����
     * ===============pangben modify 20110526 �޸ı��淽�� ֮ǰ��DataStore ������
     */
    public void onSave() {
        TTable table = (TTable)this.getComponent("Table");
        String STADATE=this.getText("STA_DATE").replace("/", "");
        if(table.getRowCount()<=0)
            return;
        //��������鳤Ȩ��  ��ô�Ѿ��ύ�����ݲ������޸�
        if(!LEADER.equals("2")){
            if (STA_CONFIRM_FLG) {
                this.messageBox_("�����Ѿ��ύ�������޸�");
                return;
            }
        }
        table.acceptText();
//        TDataStore ds = table.getDataStore();
        String optUser = Operator.getID();
        String optIp = Operator.getIP();
        //��ȡ������ʱ��
        Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
        //�Ƿ��ύ
        boolean submit = ( (TCheckBox)this.getComponent("Submit")).isSelected();
        String message = "�޸ĳɹ���"; //��ʾ��
        if (submit)
            message = "�ύ�ɹ���";
        TParm parm=new TParm();
        TParm tableParm = table.getParmValue();
		for (int i = 0; i< tableParm.getCount("DEPT_CODE"); i++) {
			// �ж��Ƿ��ύ
			if (submit) {
				tableParm.setData("CONFIRM_FLG", i, "Y");
				tableParm.setData("CONFIRM_USER", i, optUser);
				tableParm.setData("CONFIRM_DATE", i, CONFIRM_DATE);
			} else {
				tableParm.setData("CONFIRM_FLG", i, "N");
				tableParm.setData("CONFIRM_USER", i, "");
				tableParm.setData("CONFIRM_DATE", i, "");
			}
			tableParm.setData("OPT_USER", i, optUser);
			tableParm.setData("OPT_TERM", i, optIp);
			tableParm.setData("OPT_DATE", i, CONFIRM_DATE);
			if(!tableParm.getValue("STA_DATE", i).equals("")){
				parm.addRowData(tableParm, i);
			}
		}
//		System.out.println("------------parm----------"+parm);
        TParm re = TIOM_AppServer.executeAction(
           "action.sta.STAIn_05Action",
           "updateSTA_IN_05", parm);

        if (re.getErrCode()<0) {
            this.messageBox_("����ʧ�ܣ�");
            return;
        } else {
            this.messageBox_(message);
           if (submit)
               STA_CONFIRM_FLG = true;
        }
        gridBind(STADATE,"");
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
    	if ("Y".equals(this.getValueString("R_MONTH"))) {// ������Ҫ����
			// ע�⣺�±��������������� ������STA_IN_02����
			S_TYPE="month";
			DATA_StaDate= this.getText("STA_DATE").replace("/", "");
		} else if ("Y".equals(this.getValueString("R_DAYS"))) {// ��������ʱ������
			// ʱ��β�ѯ�ı��������Ƿ��������ݣ�ֻ�в�ѯ��ʾ���ܣ������б��治���޸�
			S_TYPE="day";
			DATE_Start = this.getText("DATE_S").replace("/", "");
			DATE_End = this.getText("DATE_E").replace("/", "");
		}
        if (DATA_StaDate.trim().length() <= 0&&(DATE_Start.length()<=0||DATE_End.length()<=0)) {
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
//        if (!STA_CONFIRM_FLG) {
//            this.messageBox_("�����ύ��������ɱ���");
//            return;
//        }
        TParm printParm = new TParm();
        String dataDate = "";
        if("month".equals(S_TYPE)){
            dataDate = DATA_StaDate.substring(0, 4) + "��" +
                DATA_StaDate.subSequence(4, 6) + "��";
        }else if("day".equals(S_TYPE)){
            dataDate = DATE_Start+"~"+DATE_End;
        }
        TParm data = this.getPrintData(DATA_StaDate);
//        System.out.println("data:"+data);
        if (data.getErrCode() < 0) {
            return;
        }
        printParm.setData("T1", data.getData());
        printParm.setData("TableHeader","TEXT","��        ��");
        printParm.setData("unit","TEXT",Operator.getHospitalCHNFullName());//���λ
        printParm.setData("date","TEXT",dataDate);//��������
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_05.jhw", printParm);
    }

    /**
     * ��ȡ��ӡ����
     * @param STA_DATE String
     * @return TParm
     */
    private TParm getPrintData(String STA_DATE){
        DecimalFormat df = new DecimalFormat("0.00");
        TParm printData = new TParm();//��ӡ����
        String printType = this.getValueString("PRINT_TYPE");//��ӡ����
        TParm DeptList = new TParm();
        Map deptIPD = STADeptListTool.getInstance().getIPDDeptMap(Operator.getRegion());//===========pangben modify 20110525
        //��������ڶ�ͳ�� ����ѡ����ĳһ�����ҽ���ͳ�ƣ���ô��ӡ��ʱ��ֻ��ʾ�ÿ�����Ϣ �����ձ���������ʽ��ӡ
        if ("day".equals(S_TYPE) && DAY_DEPT.length() > 0) {
            DeptList = STADeptListTool.getInstance().selectNewDeptByCode(DAY_DEPT,Operator.getRegion());//===========pangben modify 20110525
        }
        else {
            if (printType.equals("3")) { //�����������Ҵ�ӡ
                //��ȡ1��2��3������
                DeptList = STATool.getInstance().getDeptByLevel(new String[] {
                    "1", "2", "3"},Operator.getRegion());//===========pangben modify 20110525
            }
            else if (printType.equals("4")) { //�����ļ����Ҵ�ӡ
                //��ȡ1��2��3,4������
                DeptList = STATool.getInstance().getDeptByLevel(new String[] {
                    "1", "2", "3", "4"},Operator.getRegion());//===========pangben modify 20110525
            }
        }
        if (DeptList.getErrCode() < 0) {
            return DeptList;
        }
        //��ȡ����
        TParm data = new TParm();
        if ("month".equals(S_TYPE)) { //�������ͳ�� ��ѯ���ݿ� ȡ�ø���ͳ����Ϣ
            data = STAIn_05Tool.getInstance().selectSTA_IN_05(STA_DATE,Operator.getRegion());//===========pangben modify 20110525
        }
        else if ("day".equals(S_TYPE)) { //��������ڶ�ͳ�� ��ȡtable�ϵ����ݽ��д�ӡ
            data = ( (TTable)this.getComponent("Table_Read")).getParmValue();
        }
        if (data.getErrCode() < 0) {
            return data;
        }
        boolean isOE = false; //��¼�����Ƿ�������������Ϣ
        int rowCount = 0; //��¼��ӡ���ݵ�����
        for (int i = 0; i < DeptList.getCount(); i++) {
            isOE = false;
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i); //���ŵȼ�
            String d_CODE = DeptList.getValue("DEPT_CODE", i); //�м䲿��CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i); //�м䲿������
            int subIndex = 0; //��¼���ݿ��Ҽ���Ҫ��ȡCODE�ĳ���
            //�����һ������ code����Ϊ1
            if (d_LEVEL.equals("1")) {
                subIndex = 1;
            }
            //����Ƕ������� code����Ϊ3
            else if (d_LEVEL.equals("2")) {
                subIndex = 3;
//                d_DESC = " " + d_DESC; //����ǰ���ո�
            }
            //������������� code����Ϊ5
            else if (d_LEVEL.equals("3")) {
                subIndex = 5;
//                d_DESC = "  " + d_DESC; //����ǰ���ո�
            }else if(d_LEVEL.equals("4")){
                subIndex = 7;
            }

            //������� �����ۼ��Ӳ��ŵ���ֵ
            int DATA_01 = 0;
            int DATA_02 = 0;
            double DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            int DATA_06 = 0;
            double DATA_07 = 0;
            int DATA_08 = 0;
            double DATA_09 = 0;
            int DATA_10 = 0;
            double DATA_11 = 0;
            int DATA_12 = 0;
            double DATA_13 = 0;
            int DATA_14 = 0;
            double DATA_15 = 0;
            int DATA_16 = 0;
            double DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;
            int DATA_21 = 0;

            int deptCount = 0;//��¼ÿ�������µ��Ӳ��ţ����ʷ����������Ҫȡƽ��ֵ
            //ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
            for(int j=0;j<data.getCount("STA_DATE");j++){
                String subDept = data.getValue("DEPT_CODE",j).substring(0,subIndex);
                //�������id��ȡ��ָ�����Ⱥ� �������ѭ���еĲ���CODE��ô�������ѭ�����Ӳ��ţ��ͽ����ۼ�
                if(subDept.equals(d_CODE)){
                    //�жϸ��ӿ����Ƿ����ż������������� ��ô������һ��ѭ��
                    if(deptIPD.get(data.getValue("DEPT_CODE",j)).toString().length()>0){
                        isOE = true; //�����ż������  ��ʾ�ڱ�����
                        DATA_01 += data.getInt("DATA_01", j);
                        DATA_02 += data.getInt("DATA_02", j);
                        DATA_04 += data.getInt("DATA_04", j);
                        DATA_06 += data.getInt("DATA_06", j);
                        DATA_08 += data.getInt("DATA_08", j);
                        DATA_10 += data.getInt("DATA_10", j);
                        DATA_12 += data.getInt("DATA_12", j);
                        DATA_14 += data.getInt("DATA_14", j);
                        DATA_16 += data.getInt("DATA_16", j);
                        DATA_17 += data.getDouble("DATA_17", j);
                        DATA_18 += data.getInt("DATA_18", j);
                        DATA_19 += data.getInt("DATA_19", j);
                        DATA_21 += data.getInt("DATA_21", j);
                        deptCount++; //�ۼƹ����ܵĶ��ٸ�4�����ŵ�����
                    }
                }
            }
            if(DATA_18!=0){
                DATA_20 = (double)DATA_19/(double)DATA_18*100;//�ɹ���
            }
            if(DATA_01!=0){
                DATA_03 = (double)DATA_02/(double)DATA_01*100;//���б������ٷֱ�
                DATA_05 = (double)DATA_04/(double)DATA_01*100;//�����������ٷֱ�
                DATA_07 = (double)DATA_06/(double)DATA_01*100;//��ʡ���У��ٷֱ�
                DATA_09 = (double)DATA_08/(double)DATA_01*100;//��ʡ��
                DATA_11 = (double)DATA_10 / (double)DATA_01 *100;//����ȷ����
                DATA_13 = (double)DATA_12 / (double)DATA_01 *100;//24Сʱ��Ժ������
                DATA_15 = (double)DATA_14 / (double)DATA_01 *100;//48Сʱ��Ժ������
            }
            if(isOE){
                printData.addData("DEPT_DESC", d_DESC);
                printData.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
                printData.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
                printData.addData("DATA_03",
                                  DATA_03 == 0 ? "" : df.format(DATA_03));
                printData.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
                printData.addData("DATA_05",
                                  DATA_05 == 0 ? "" : df.format(DATA_05));
                printData.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
                printData.addData("DATA_07",
                                  DATA_07 == 0 ? "" : df.format(DATA_07));
                printData.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
                printData.addData("DATA_09",
                                  DATA_09 == 0 ? "" : df.format(DATA_09));
                printData.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
                printData.addData("DATA_11",
                                  DATA_11 == 0 ? "" : df.format(DATA_11));
                printData.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
                printData.addData("DATA_13",
                                  DATA_13 == 0 ? "" : df.format(DATA_13));
                printData.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
                printData.addData("DATA_15",
                                  DATA_15 == 0 ? "" : df.format(DATA_15));
                printData.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
                printData.addData("DATA_17",
                                  DATA_17 == 0 ? "" : df.format(DATA_17));
                printData.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
                printData.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
                printData.addData("DATA_20",
                                  DATA_20 == 0 ? "" : df.format(DATA_20));
				printData.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
                rowCount++;
            }
        }
        printData.setCount(rowCount);
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "DATA_01");
        printData.addData("SYSTEM", "COLUMNS", "DATA_02");
        printData.addData("SYSTEM", "COLUMNS", "DATA_03");
        printData.addData("SYSTEM", "COLUMNS", "DATA_04");
        printData.addData("SYSTEM", "COLUMNS", "DATA_05");
        printData.addData("SYSTEM", "COLUMNS", "DATA_06");
        printData.addData("SYSTEM", "COLUMNS", "DATA_07");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08");
        printData.addData("SYSTEM", "COLUMNS", "DATA_09");
        printData.addData("SYSTEM", "COLUMNS", "DATA_10");
        printData.addData("SYSTEM", "COLUMNS", "DATA_11");
        printData.addData("SYSTEM", "COLUMNS", "DATA_12");
        printData.addData("SYSTEM", "COLUMNS", "DATA_13");
        printData.addData("SYSTEM", "COLUMNS", "DATA_14");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15");
        printData.addData("SYSTEM", "COLUMNS", "DATA_16");
        printData.addData("SYSTEM", "COLUMNS", "DATA_17");
        printData.addData("SYSTEM", "COLUMNS", "DATA_18");
        printData.addData("SYSTEM", "COLUMNS", "DATA_19");
        printData.addData("SYSTEM", "COLUMNS", "DATA_20");
        printData.addData("SYSTEM", "COLUMNS", "DATA_21");
        return printData;
    }
    /**
     * ������ݰ�
     */
    private void gridBind(String STADATE,String deptCode) {
        TTable table = (TTable)this.getComponent("Table");
        String sql = STASQLTool.getInstance().getSTA_IN_05(STADATE,Operator.getRegion(),deptCode);//============pangben modify 20110524
       //=======pangben modify 20110526 start
        TParm result = new TParm(TJDODBTool.getInstance().select(
            sql));
        if (result.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
        int DATA_01 = 0;
        int DATA_02 = 0;
        double DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        int DATA_06 = 0;
        double DATA_07 = 0;
        int DATA_08 = 0;
        double DATA_09 = 0;
        int DATA_10 = 0;
        double DATA_11 = 0;
        int DATA_12 = 0;
        double DATA_13 = 0;
        int DATA_14 = 0;
        double DATA_15 = 0;
        int DATA_16 = 0;
        double DATA_17 = 0;
        int DATA_18 = 0;
        int DATA_19 = 0;
        double DATA_20 = 0;
        //�⼮����
        int DATA_21 = 0;
        
		for (int i = 0; i < result.getCount(); i++) {
			DATA_01 += result.getInt("DATA_01", i);
			DATA_02 += result.getInt("DATA_02", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_06 += result.getInt("DATA_06", i);
//			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
//			DATA_09 += result.getInt("DATA_09", i);
			DATA_10 += result.getInt("DATA_10", i);	
			DATA_12 += result.getInt("DATA_12", i);
			DATA_14 += result.getInt("DATA_14", i);
//			DATA_15 += result.getInt("DATA_15", i);
			DATA_16 += result.getInt("DATA_16", i);
			DATA_17 += result.getInt("DATA_17", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getDouble("DATA_19", i);
			DATA_20 += result.getDouble("DATA_20", i);
			DATA_21 += result.getInt("DATA_21", i);
		}
		if (DATA_18 != 0) {
            DATA_20 = (double) DATA_19 / (double) DATA_18 * 100; //�ɹ���
        }
        if (DATA_01 != 0) {
            DATA_03 = (double) DATA_02 / (double) DATA_01 * 100; //���б������ٷֱ�
            DATA_05 = (double) DATA_04 / (double) DATA_01 * 100; //�����������ٷֱ�
            DATA_07 = (double) DATA_06 / (double) DATA_01 * 100; //��ʡ���У��ٷֱ�
            DATA_09 = (double) DATA_08 / (double) DATA_01 * 100; //��ʡ��
            DATA_11 = (double) DATA_10 / (double) DATA_01 * 100; //����ȷ����
            DATA_13 = (double) DATA_12 / (double) DATA_01 * 100; //24Сʱ��Ժ������
            DATA_15 = (double) DATA_14 / (double) DATA_01 * 100; //48Сʱ��Ժ������
        }
		result.addData("DEPT_CODE", "�ϼ�:");
		result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
        result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        result.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
        result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        result.addData("DATA_07", DATA_07 == 0 ? "" : df.format(DATA_07));
        result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        result.addData("DATA_09", DATA_09 == 0 ? "" : df.format(DATA_09));
        result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
        result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
        result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
        result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        result.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
        result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
        result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
        result.addData("STA_DATE", "");
        result.addData("STATION_CODE", "");
        result.addData("REGION_CODE", "");
		table.setParmValue(result);
       // table.retrieve();
      //  table.setDSValue();
      //=======pangben modify 20110526 stop
        DATA_StaDate = STADATE;
    }
    /**
     * ������ݰ�
     */
    private void gridBind(TParm result) {
        TTable table = (TTable)this.getComponent("Table_Read");
        if (result.getCount("DEPT_CODE") <= 0) {
			table.removeRowAll();
			return;
		}
        int DATA_01 = 0;
        int DATA_02 = 0;
        double DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        int DATA_06 = 0;
        double DATA_07 = 0;
        int DATA_08 = 0;
        double DATA_09 = 0;
        int DATA_10 = 0;
        double DATA_11 = 0;
        int DATA_12 = 0;
        double DATA_13 = 0;
        int DATA_14 = 0;
        double DATA_15 = 0;
        int DATA_16 = 0;
        double DATA_17 = 0;
        int DATA_18 = 0;
        int DATA_19 = 0;
        double DATA_20 = 0;
        //�⼮����
        int DATA_21 = 0;
        
		for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
			DATA_01 += result.getInt("DATA_01", i);
			DATA_02 += result.getInt("DATA_02", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_06 += result.getInt("DATA_06", i);
//			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
//			DATA_09 += result.getInt("DATA_09", i);
			DATA_10 += result.getInt("DATA_10", i);	
			DATA_12 += result.getInt("DATA_12", i);
			DATA_14 += result.getInt("DATA_14", i);
//			DATA_15 += result.getInt("DATA_15", i);
			DATA_16 += result.getInt("DATA_16", i);
			DATA_17 += result.getInt("DATA_17", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getInt("DATA_19", i);
			DATA_20 += result.getDouble("DATA_20", i);
			DATA_21 += result.getInt("DATA_21", i);
		}
		if (DATA_18 != 0) {
            DATA_20 = (double) DATA_19 / (double) DATA_18 * 100; //�ɹ���
        }
        if (DATA_01 != 0) {
            DATA_03 = (double) DATA_02 / (double) DATA_01 * 100; //���б������ٷֱ�
            DATA_05 = (double) DATA_04 / (double) DATA_01 * 100; //�����������ٷֱ�
            DATA_07 = (double) DATA_06 / (double) DATA_01 * 100; //��ʡ���У��ٷֱ�
            DATA_09 = (double) DATA_08 / (double) DATA_01 * 100; //��ʡ��
            DATA_11 = (double) DATA_10 / (double) DATA_01 * 100; //����ȷ����
            DATA_13 = (double) DATA_12 / (double) DATA_01 * 100; //24Сʱ��Ժ������
            DATA_15 = (double) DATA_14 / (double) DATA_01 * 100; //48Сʱ��Ժ������
        }
		result.addData("DEPT_CODE", "�ϼ�:");
		result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
        result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        result.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
        result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        result.addData("DATA_07", DATA_07 == 0 ? "" : df.format(DATA_07));
        result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        result.addData("DATA_09", DATA_09 == 0 ? "" : df.format(DATA_09));
        result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
        result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
        result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
        result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        result.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
        result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
        result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
		table.setParmValue(result);
        //��¼��ѯ���ڶ� ���ڱ����ӡ
        DATE_Start = this.getText("DATE_S");
        DATE_End = this.getText("DATE_E");
    }

    /**
     * ����Ƿ������������
     * ����Ƿ��Ѿ����·ݵ����� �����Ƿ��Ѿ��ύ  true:��������  false:����������
     * @param STADATE String
     * @return boolean
     */
    private boolean canGeneration(String STADATE) {
        boolean can = false;
        //�������״̬
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_IN_05",
            STADATE,Operator.getRegion());//=============pangben modify 20110524
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
        }
        else if (reFlg == 0) { //û������
            can = true;
        }
        else if (reFlg == -1) { //���ݼ�˴���
            can = false;
        }
        if(STA_CONFIRM_FLG==true)
            this.setValue("Submit",true);
        else
            this.setValue("Submit",false);
        return can;
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
            ((TLabel)this.getComponent("tLabel_1")).setVisible(false);
            callFunction("UI|query|setEnabled", true); //���ɰ�ť����
            callFunction("UI|save|setEnabled", true); //���水ť����
            callFunction("UI|Table_Read|setVisible", false);//������ͳ�Ʊ��
            callFunction("UI|Table|setVisible", true);//��ʾ��ͳ�Ʊ��
        }
        else if("days".equals(type)){
            this.setText("tLabel_0","ͳ������");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(false);//��ʾ�·ݿؼ�
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(true);//�������ڶοؼ�
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(true);//�������ڶοؼ�
            ((TLabel)this.getComponent("tLabel_1")).setVisible(true);
            callFunction("UI|query|setEnabled", false); //���ɰ�ť����
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
