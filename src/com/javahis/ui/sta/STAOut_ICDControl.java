package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.data.TParm;
import java.util.Vector;
import jdo.sta.STAOut_ICDTool;
import jdo.sta.STATool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TComboBox;
import jdo.sta.STAStationLogTool;
import jdo.sys.SystemTool;
import com.javahis.util.ExportExcelUtil;
import jdo.sta.STASDListTool;
import jdo.sys.Operator;

/**
 * <p>Title: ������ҽ�������ۺ�ͳ�Ʊ�</p>
 *
 * <p>Description: ������ҽ�������ۺ�ͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-3
 * @version 1.0
 */
public class STAOut_ICDControl
    extends TControl {
    String DATE_Start = "";//��¼ͳ����ʼ����
    String DATE_End = "";//��¼ͳ�ƽ�������
    String selectDept = "";//Ҫ��ѯ�Ĳ���
    String selectDeptDesc = "";//Ҫ��ѯ�Ĳ�������
    public void onInit(){
        super.init();
        //tableר�õļ���  ICD10
        ((TTable)this.getComponent("Table_ICD")).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
//        comboboxInit();//combo��ʼ��
        this.setValue("DATE_S",STATool.getInstance().getLastMonth());
        this.setValue("DATE_E",SystemTool.getInstance().getDate());
    }
    /**
     *����combobox��ʼ��
     */
//    private void comboboxInit(){
//        TParm station = STAStationLogTool.getInstance().selectSTAStation();
//        ((TComboBox)this.getComponent("DEPT_Combo")).setParmValue(station);
//    }

    /**
     *��ϵ������� ICD10
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponent(Component com, int row, int column) {

        //����ICD10�Ի������
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����ICD10��������
        textfield.setPopupMenuParameter("ICD10",getConfigParm().newConfig("%ROOT%\\config\\sta\\STAICDPopup.x"));
        //����text���ӽ���ICD10�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newAgentOrder");
    }
    /**
     * ȡ��ICD10����ֵ
     * @param tag String
     * @param obj Object
     */
    public void newAgentOrder(String tag, Object obj) {
        TTable table = (TTable)this.callFunction("UI|Table_ICD|getThis");
        //sysfee���ص����ݰ�
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("SEQ");
        table.setItem(table.getSelectedRow(), "code", orderCode);
        table.setItem(table.getSelectedRow(), "name", parm.getValue("SD_DESC"));
    }

    /**
     * ���ICD��ť
     */
    public void onAdd(){
        TTable table = (TTable)this.getComponent("Table_ICD");
        table.addRow();
    }
    /**
     * ɾ��ICD
     */
    public void onRemove(){
        TTable table = (TTable)this.getComponent("Table_ICD");
        if(table.getSelectedRow()>-1)
            table.removeRow(table.getSelectedRow());
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
        String DATE_S = this.getText("DATE_S").replace("/","");
        String DATE_E = this.getText("DATE_E").replace("/","");
        if(DATE_S.trim().length()<=0||DATE_E.trim().length()<=0){
            this.messageBox_("��ѡ�����ڶ�");
            return;
        }
        DATE_Start = DATE_S;
        DATE_End = DATE_E;
//        DATE_S = DATE_S + "01";
        //��ȡ�·ݵ����һ������
//        DATE_E = StringTool.getString(STATool.getInstance().getLastDayOfMonth(DATE_E),"yyyyMMdd");
        String dept = this.getValueString("DEPT_Combo");//��ȡ����ID
        TTable table = (TTable)this.getComponent("Table_ICD");
        table.acceptText();
        TParm ICD = new TParm();
        //ɸѡѡ�е�ICD
        for(int i=0;i<table.getRowCount();i++){
            if(table.getItemString(i,"name").trim().length()>0){
                ICD.addData("ICD",table.getItemString(i,"code"));
                ICD.addData("DESC",table.getItemString(i,"name"));
                TParm tp = new TParm();
                tp.setData("SEQ",table.getItemString(i,"code"));
                ICD.addData("CONDITION",STASDListTool.getInstance().selectData(tp).getValue("CONDITION",0));
            }
        }
        if(ICD.getCount("ICD")<=0){
            this.messageBox_("��ѡ����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",DATE_S);
        parm.setData("DATE_E",DATE_E);
        if(dept.length()>0){
            parm.setData("DEPTCODE",dept);
            selectDept = dept;
            selectDeptDesc = this.getText("DEPT_Combo");
        }
        parm.setData("ICD",ICD.getData());
//        System.out.println("-=------------------"+parm);
        TParm result = STAOut_ICDTool.getInstance().selectData(parm);
        if(result.getErrCode()<0){
            this.messageBox_("ͳ��ʧ�� "+result.getErrName()+result.getErrText());
            return;
        }
        ((TTable)this.getComponent("Table")).setParmValue(result);
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        TParm printParm = new TParm();
        if(((TTable)this.getComponent("Table")).getRowCount()<=0){
            return;
        }
        TParm data = ((TTable)this.getComponent("Table")).getParmValue();
        data.setCount(data.getCount("ICD_DESC"));
        data.setData("SYSTEM","COLUMNS","ICD_DESC");
        data.setData("SYSTEM","COLUMNS","DATA_01");
        data.setData("SYSTEM","COLUMNS","DATA_02");
        data.setData("SYSTEM","COLUMNS","DATA_03");
        data.setData("SYSTEM","COLUMNS","DATA_04");
        data.setData("SYSTEM","COLUMNS","DATA_05");
        data.setData("SYSTEM","COLUMNS","DATA_06");
        data.setData("SYSTEM","COLUMNS","DATA_07");
        data.setData("SYSTEM","COLUMNS","DATA_08");
        data.setData("SYSTEM","COLUMNS","DATA_09");
        data.setData("SYSTEM","COLUMNS","DATA_10");
        data.setData("SYSTEM","COLUMNS","DATA_11");
        printParm.setData("T1",data.getData());
        //��ѯ���Ų�����
        TParm bedNo = STAOut_ICDTool.getInstance().selectDedNO(selectDept);
        //������Ϣ
        TParm Basic = new TParm(); //�����������
        //�������ڸ�ʽ
        String date = DATE_Start.substring(0, 4) + "��" + DATE_Start.substring(4,6) +
            "��" + DATE_Start.substring(6) + "�� ~ " + DATE_End.substring(0, 4) + "��" +
            DATE_End.substring(4,6) + "��" +DATE_End.substring(6) + "��";
        Basic.setData("DateSE", date); //��������
        Basic.setData("Date",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
        Basic.setData("Unit",Operator.getHospitalCHNFullName());//���λ
        Basic.setData("Level", "");//ҽԺ�ȼ�
        Basic.setData("BEDNO",bedNo.getValue("NUM",0));//��λ��
        if(selectDeptDesc.trim().length()>0)
            Basic.setData("DeptDesc","("+selectDeptDesc+")"); //ͳ�ƵĿ�������
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_ICD.jhw", printParm);
    }
    /**
    * ���Excel
    */
   public void onExcel() {
       //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
       TTable table = (TTable) callFunction("UI|Table|getThis");
       ExportExcelUtil.getInstance().exportExcel(table,"������ҽ�������ۺ�ͳ�Ʊ�");
   }
}
