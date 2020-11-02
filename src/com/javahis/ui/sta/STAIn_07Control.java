package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Map;

import jdo.sta.STADeptListTool;
import jdo.sta.STAIn_07Tool;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>Title: STA_IN_07ҽԺ�����������</p>
 *
 * <p>Description: STA_IN_07ҽԺ�����������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-25
 * @version 1.0
 */
public class STAIn_07Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false; //��¼�Ƿ��ύ
    private String DATA_StaDate = ""; //��¼Ŀǰ��ʾ�����ݵ�����(����)
    private String S_TYPE = "";//��¼��ǰͳ�Ƶ�����  month:��ͳ��   day:���ڶ�ͳ��
    private String DATE_Start = "";//��¼��ʼ����
    private String DATE_End = "";//��¼��ֹ����
    private String DAY_DEPT = "";//��¼�������ڶ�ͳ��ʱͳ�ƵĿ���
    private String LEADER = "";//��¼�Ƿ����鳤Ȩ��  ���LEADER=2��ô�����鳤Ȩ��

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
        showFormat();
    }
    /**
    * ��ӿ���comboɸѡ
    * ========pangben modify 20110526
    */
   public void showFormat() {
       //����
       TTextFormat DEPT_CODE = (TTextFormat)this.getComponent(
               "DEPT_CODE");

       //�����������
       if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
           DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL() +
                                     " AND REGION_CODE='" +
                                     Operator.getRegion() +
                                     "' ORDER BY DEPT_CODE");
       } else {
           DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL() +
                                     " ORDER BY DEPT_CODE");
       }
       DEPT_CODE.onQuery();
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
        table1.resetModify();
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.removeRowAll();
        TTextFormat dept = (TTextFormat)this.getComponent("DEPT_CODE");
        dept.setText("");
        dept.setValue("");
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
        TTable table = (TTable)this.getComponent("Table");
        DATA_StaDate=this.getText("STA_DATE").replace("/", "");
        if(DATA_StaDate.length()>0){
        	 this.gridBind(DATA_StaDate);
        }
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
            gridBind(STA_DATE); //���ݰ�
            return;
        }
        String StartDate = STA_DATE + "01"; //ÿ�µ�һ��
        //��ȡ���·ݵ����һ��
        String EndDate = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(STA_DATE),
                                              "yyyyMMdd");
        TParm parm = new TParm();
        parm.setData("DATE_S", StartDate);
        parm.setData("DATE_E", EndDate);
        parm.setData("REGION_CODE", Operator.getRegion());//============pangben modify 20110525
        TParm data = STAIn_07Tool.getInstance().selectData(parm);
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
            data.setData("REGION_CODE", i, Operator.getRegion());//============pangben modify 20110525
        }
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAIn_07Action",
            "insertSTA_IN_07", data);
        if (re.getErrCode() < 0) {
            this.messageBox_("����ʧ�ܣ�");
            return;
        }
        this.messageBox_("���ɳɹ���");
        gridBind(STA_DATE); //�������ɵ�����
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
                getValueString("DEPT_CODE"),Operator.getRegion());//===========pangben modify 20110525
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
            parm.setData("OUT_DEPT", IPD_DEPT_CODE);
        }
        if(STATION_CODE.length()>0){
            parm.setData("OUT_STATION",STATION_CODE);
        }
        if(Dept.length()>0){
            parm.setData("DEPT",Dept);
        }
        parm.setData("REGION_CODE",Operator.getRegion());//===========pangben modify 20110525
        TParm result = STAIn_07Tool.getInstance().selectData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox_("����ʧ�� " + result.getErrText());
            return;
        }
        DAY_DEPT = Dept; //��¼ͳ�ƵĿ���
        gridBind(result);
    }
    /**
     * ����
     */
    public void onSave() {
        TTable table = (TTable)this.getComponent("Table");
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
        String optUser = Operator.getID();
        String optIp = Operator.getIP();
        String STA_DATE = this.getText("STA_DATE").replace("/", "");
        String deptCode=this.getValueString("DEPT_CODE");
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
		TParm re = TIOM_AppServer.executeAction("action.sta.STAIn_07Action",
				"updateSTA_IN_07", parm);
		if (re.getErrCode() < 0) {
			this.messageBox_("����ʧ�ܣ�");
			return;
		} else {
			this.messageBox_(message);
			if (submit)
				STA_CONFIRM_FLG = true;
		}
		gridBind(STA_DATE);
    }
    /**
     * ��ӡ
     */
    public void onPrint() {
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
        if (DATA_StaDate.trim().length() <= 0 &&
            (DATE_Start.length() <= 0 || DATE_End.length() <= 0)) {
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
        printParm.setData("date","TEXT",dataDate);
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_07.jhw", printParm);
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
        Map deptIPD = STADeptListTool.getInstance().getIPDDeptMap(Operator.getRegion());//=============pangben modify 20110525
        //��������ڶ�ͳ�� ����ѡ����ĳһ�����ҽ���ͳ�ƣ���ô��ӡ��ʱ��ֻ��ʾ�ÿ�����Ϣ �����ձ���������ʽ��ӡ
        if("day".equals(S_TYPE)&&DAY_DEPT.length()>0){
            DeptList = STADeptListTool.getInstance().selectNewDeptByCode(DAY_DEPT,Operator.getRegion());//=============pangben modify 20110525
        }
        else{
            if (printType.equals("3")) { //�����������Ҵ�ӡ
                //��ȡ1��2��3������
                DeptList = STATool.getInstance().getDeptByLevel(new String[] {
                    "1", "2", "3"},Operator.getRegion());//=============pangben modify 20110525
            }
            else if (printType.equals("4")) { //�����ļ����Ҵ�ӡ
                //��ȡ1��2��3,4������
                DeptList = STATool.getInstance().getDeptByLevel(new String[] {
                    "1", "2", "3", "4"},Operator.getRegion());//=============pangben modify 20110525
            }
        }
        if(DeptList.getErrCode()<0){
            return DeptList;
        }
        //��ȡ����
        TParm data = new TParm();
        if("month".equals(S_TYPE)){//�������ͳ�� ��ѯ���ݿ� ȡ�ø���ͳ����Ϣ
            data = STAIn_07Tool.getInstance().selectSTA_IN_07(STA_DATE,Operator.getRegion());//=============pangben modify 20110525
        }else if("day".equals(S_TYPE)){//��������ڶ�ͳ�� ��ȡtable�ϵ����ݽ��д�ӡ
            data = ((TTable)this.getComponent("Table_Read")).getParmValue();
        }
        if(data.getErrCode()<0){
            return data;
        }
        boolean isOE = false;//��¼�����Ƿ�������������Ϣ
        int rowCount = 0;//��¼��ӡ���ݵ�����
        for(int i =0;i<DeptList.getCount();i++){
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
            int DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            double DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            double DATA_10 = 0;
            double DATA_11 = 0;
            int DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            double DATA_15 = 0;
            double DATA_16 = 0;
            double DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;
            int DATA_21 = 0;
            int DATA_22 = 0;
            int DATA_23 = 0;
            int DATA_24 = 0;
            int DATA_25 = 0;
            int DATA_26 = 0;
            int DATA_27 = 0;

            //ѭ���������� ȡ�����������Ĳ��ŵ����ݽ����ۼ�
            for(int j=0;j<data.getCount("STA_DATE");j++){
                String subDept = data.getValue("DEPT_CODE",j).substring(0,subIndex);
                //�������id��ȡ��ָ�����Ⱥ� �������ѭ���еĲ���CODE��ô�������ѭ�����Ӳ��ţ��ͽ����ۼ�
                if(subDept.equals(d_CODE)){
                    //�жϸ��ӿ����Ƿ���סԺ����������� ��ô������һ��ѭ��
                    if(deptIPD.get(data.getValue("DEPT_CODE",j)).toString().length()>0){
                        isOE = true; //��סԺ����  ��ʾ�ڱ�����
                        DATA_01 += data.getInt("DATA_01", j);
                        DATA_02 += data.getInt("DATA_02", j);
                        DATA_03 += data.getInt("DATA_03", j);
                        DATA_04 += data.getInt("DATA_04", j);
                        DATA_07 += data.getInt("DATA_07", j);
                        DATA_08 += data.getInt("DATA_08", j);
                        DATA_09 += data.getInt("DATA_09", j);
                        DATA_12 += data.getInt("DATA_12", j);
                        DATA_13 += data.getInt("DATA_13", j);
                        DATA_14 += data.getInt("DATA_14", j);
                        DATA_18 += data.getInt("DATA_18", j);
                        DATA_19 += data.getInt("DATA_19", j);
                        DATA_20 += data.getDouble("DATA_20", j);
                        DATA_21 += data.getInt("DATA_21", j);
                        DATA_22 += data.getInt("DATA_22", j);
                        DATA_23 += data.getInt("DATA_23", j);
                        DATA_24 += data.getInt("DATA_24", j);
                        DATA_25 += data.getInt("DATA_25", j);
                        DATA_26 += data.getInt("DATA_26", j);
                        DATA_27 += data.getInt("DATA_27", j);
                    }
                }
            }
            if(DATA_07!=0){
                DATA_10 = (double)DATA_08/ (double)DATA_07*100;//�޾�����,�׼�������
                DATA_11 = (double)DATA_09/ (double)DATA_07*100;//�޾�����,�пڻ�ŧ��
            }
            if(DATA_02!=0){
                DATA_15 =  (double)DATA_12/(double)DATA_02*100;//��������������������
                DATA_16 = (double)DATA_13/(double)DATA_02 *100;//��������������ʮ����������
                DATA_17 = (double)DATA_14/(double)DATA_02*100;//��������������������
            }
            if(DATA_02!=0){
                DATA_05 = (double)DATA_03/(double)DATA_02;//����ƽ��סԺ��
                DATA_06 = (double)DATA_04/(double)DATA_02;//��ǰƽ��סԺ��
            }

            if(isOE){
                printData.addData("DEPT_DESC", d_DESC);
                printData.addData("DATA_01", DATA_02 == 0 ? "" : DATA_01);
                printData.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
                printData.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
                printData.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
                printData.addData("DATA_05",
                                  DATA_05 == 0 ? "" : df.format(DATA_05));
                printData.addData("DATA_06",
                                  DATA_06 == 0 ? "" : df.format(DATA_06));
                printData.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
                printData.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
                printData.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
                printData.addData("DATA_10",
                                  DATA_10 == 0 ? "" : df.format(DATA_10));
                printData.addData("DATA_11",
                                  DATA_11 == 0 ? "" : df.format(DATA_11));
                printData.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
                printData.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
                printData.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
                printData.addData("DATA_15",
                                  DATA_15 == 0 ? "" : df.format(DATA_15));
                printData.addData("DATA_16",
                                  DATA_16 == 0 ? "" : df.format(DATA_16));
                printData.addData("DATA_17",
                                  DATA_17 == 0 ? "" : df.format(DATA_17));
                printData.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
                printData.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
                printData.addData("DATA_20",
                                  DATA_20 == 0 ? "" : df.format(DATA_20));
                printData.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
                printData.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
                printData.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
                printData.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
                printData.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
                printData.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
                printData.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
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
        printData.addData("SYSTEM", "COLUMNS", "DATA_22");
        printData.addData("SYSTEM", "COLUMNS", "DATA_23");
        printData.addData("SYSTEM", "COLUMNS", "DATA_24");
        printData.addData("SYSTEM", "COLUMNS", "DATA_25");
        printData.addData("SYSTEM", "COLUMNS", "DATA_26");
        printData.addData("SYSTEM", "COLUMNS", "DATA_27");
        return printData;
    }
    /**
     * ������ݰ�
     */
    private void gridBind(String STADATE) {
        TTable table = (TTable)this.getComponent("Table");
        String  deptcode=this.getValueString("DEPT_CODE");
        table.setLockColumns("0");//������һ�в����޸�
        String sql = STASQLTool.getInstance().getSTA_IN_07(STADATE,Operator.getRegion(),deptcode);//=======pangben modify 20110525
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getCount()<=0){
        	table.removeRowAll();
        	return;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        int DATA_01 = 0;
        int DATA_02 = 0;
        int DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        double DATA_06 = 0;
        int DATA_07 = 0;
        int DATA_08 = 0;
        int DATA_09 = 0;
        double DATA_10 = 0;
        double DATA_11 = 0;
        int DATA_12 = 0;
        int DATA_13 = 0;
        int DATA_14 = 0;
        double DATA_15 = 0;
        double DATA_16 = 0;
        double DATA_17 = 0;
        int DATA_18 = 0;
        int DATA_19 = 0;
        double DATA_20 = 0;
        int DATA_21 = 0;
        int DATA_22 = 0;
        int DATA_23 = 0;
        int DATA_24 = 0;
        int DATA_25 = 0;
        int DATA_26 = 0;
        int DATA_27 = 0;
		for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
			DATA_01 += result.getInt("DATA_01", i);
			DATA_02 += result.getInt("DATA_02", i);
			DATA_03 += result.getInt("DATA_03", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_05 += result.getInt("DATA_05", i);
			DATA_06 += result.getInt("DATA_06", i);
			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
			DATA_09 += result.getInt("DATA_09", i);
			DATA_12 += result.getInt("DATA_12", i);
			DATA_13 += result.getInt("DATA_13", i);
			DATA_14 += result.getInt("DATA_14", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getInt("DATA_19", i);
			DATA_20 += result.getInt("DATA_20", i);
			DATA_23 += result.getInt("DATA_23", i);
			DATA_24 += result.getInt("DATA_24", i);
			DATA_25 += result.getInt("DATA_25", i);
			DATA_26 += result.getInt("DATA_26", i);
			DATA_27 += result.getInt("DATA_27", i);
		}
		if(DATA_07!=0){
            DATA_10 = (double)DATA_08/ (double)DATA_07*100;//�޾�����,�׼�������
            DATA_11 = (double)DATA_09/ (double)DATA_07*100;//�޾�����,�пڻ�ŧ��
        }
        if(DATA_02!=0){
            DATA_15 =  (double)DATA_12/(double)DATA_02*100;//��������������������
            DATA_16 = (double)DATA_13/(double)DATA_02 *100;//��������������ʮ����������
            DATA_17 = (double)DATA_14/(double)DATA_02*100;//��������������������
        }
        DATA_21 = DATA_22 + DATA_25;//�����˴Σ��ϼ�
        DATA_22 = DATA_23+DATA_24;//�����˴Σ�����ϼ�
        if(DATA_02!=0){
            DATA_05 = (double)DATA_03/(double)DATA_02;//����ƽ��סԺ��
            DATA_06 = (double)DATA_04/(double)DATA_02;//��ǰƽ��סԺ��
        }
		result.addData("DEPT_CODE", "�ϼ�:");
		result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
        result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
        result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
        result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
        result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
        result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
        result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
        result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
        result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
        result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
        result.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
        result.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
        result.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
        result.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
        result.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
        result.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
        result.addData("CONFIRM_FLG", "N");
        result.addData("CONFIRM_USER", "");
        result.addData("CONFIRM_DATE", "");
        result.addData("OPT_USER", "");
        result.addData("OPT_TERM", "");
        result.addData("STA_DATE", "");
        result.addData("STATION_CODE", "");
        result.addData("REGION_CODE", "");
		table.setParmValue(result);
        DATA_StaDate = STADATE;
    }
    /**
     * ������ݰ�(���ڶα���)
     */
    private void gridBind(TParm result) {
        TTable table = (TTable)this.getComponent("Table_Read");
        table.setLockColumns("all");//���������в����޸�
        DecimalFormat df = new DecimalFormat("0.00");
        int DATA_01 = 0;
        int DATA_02 = 0;
        int DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        double DATA_06 = 0;
        int DATA_07 = 0;
        int DATA_08 = 0;
        int DATA_09 = 0;
        double DATA_10 = 0;
        double DATA_11 = 0;
        int DATA_12 = 0;
        int DATA_13 = 0;
        int DATA_14 = 0;
        double DATA_15 = 0;
        double DATA_16 = 0;
        double DATA_17 = 0;
        int DATA_18 = 0;
        int DATA_19 = 0;
        double DATA_20 = 0;
        int DATA_21 = 0;
        int DATA_22 = 0;
        int DATA_23 = 0;
        int DATA_24 = 0;
        int DATA_25 = 0;
        int DATA_26 = 0;
        int DATA_27 = 0;
		for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
			DATA_01 += result.getInt("DATA_01", i);
			DATA_02 += result.getInt("DATA_02", i);
			DATA_03 += result.getInt("DATA_03", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_05 += result.getInt("DATA_05", i);
			DATA_06 += result.getInt("DATA_06", i);
			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
			DATA_09 += result.getInt("DATA_09", i);
			DATA_12 += result.getInt("DATA_12", i);
			DATA_13 += result.getInt("DATA_13", i);
			DATA_14 += result.getInt("DATA_14", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getInt("DATA_19", i);
			DATA_20 += result.getInt("DATA_20", i);
			DATA_23 += result.getInt("DATA_23", i);
			DATA_24 += result.getInt("DATA_24", i);
			DATA_25 += result.getInt("DATA_25", i);
			DATA_26 += result.getInt("DATA_26", i);
			DATA_27 += result.getInt("DATA_27", i);
		}
		if(DATA_07!=0){
            DATA_10 = (double)DATA_08/ (double)DATA_07*100;//�޾�����,�׼�������
            DATA_11 = (double)DATA_09/ (double)DATA_07*100;//�޾�����,�пڻ�ŧ��
        }
        if(DATA_02!=0){
            DATA_15 =  (double)DATA_12/(double)DATA_02*100;//��������������������
            DATA_16 = (double)DATA_13/(double)DATA_02 *100;//��������������ʮ����������
            DATA_17 = (double)DATA_14/(double)DATA_02*100;//��������������������
        }
        DATA_21 = DATA_22 + DATA_25;//�����˴Σ��ϼ�
        DATA_22 = DATA_23+DATA_24;//�����˴Σ�����ϼ�
        if(DATA_02!=0){
            DATA_05 = (double)DATA_03/(double)DATA_02;//����ƽ��סԺ��
            DATA_06 = (double)DATA_04/(double)DATA_02;//��ǰƽ��סԺ��
        }
		result.addData("DEPT_CODE", "�ϼ�:");
		result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
        result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
        result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
        result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
        result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
        result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
        result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
        result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
        result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
        result.addData("DATA_21", DATA_20 == 0 ? "" : DATA_21);
        result.addData("DATA_22", DATA_20 == 0 ? "" : DATA_22);
        result.addData("DATA_23", DATA_20 == 0 ? "" : DATA_23);
        result.addData("DATA_24", DATA_20 == 0 ? "" : DATA_24);
        result.addData("DATA_25", DATA_20 == 0 ? "" : DATA_25);
        result.addData("DATA_26", DATA_20 == 0 ? "" : DATA_26);
        result.addData("DATA_27", DATA_20 == 0 ? "" : DATA_27);
        result.addData("CONFIRM_FLG", "N");
        result.addData("CONFIRM_USER", "");
        result.addData("CONFIRM_DATE", "");
        result.addData("OPT_USER", "");
        result.addData("OPT_TERM", "");
        result.addData("STA_DATE", "");
        result.addData("STATION_CODE", "");
        result.addData("REGION_CODE", "");
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
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_IN_07",
            STADATE,Operator.getRegion());//=================pangben modify 20110525
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
