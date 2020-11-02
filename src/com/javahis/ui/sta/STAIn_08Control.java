package com.javahis.ui.sta;

import java.sql.Timestamp;

import jdo.sta.STAIn_08Tool;
import jdo.sta.STATool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>Title: STA_IN_08����ҽʦ������</p>
 *
 * <p>Description: STA_IN_08����ҽʦ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-25
 * @version 1.0
 */
public class STAIn_08Control
    extends TControl {
    TParm dr_data = null;//��ѯ��������
    public void onInit(){
        super.init();
//        comboBind();//����combo��
        dateInit();
    }
    public void onQuery(){
        String DATE_S = this.getText("DATE_S").replace("/","");
        String DATE_E = this.getText("DATE_E").replace("/","");
        if(DATE_S.trim().length()<=0||DATE_E.trim().length()<=0){
            this.messageBox_("����ѡ�����ڶ�");
            return;
        }
        TParm dr_list = new TParm();
//        TComboBox combo_dept = (TComboBox)this.getComponent("Dept_Combo");
//        TComboBox combo_dr = (TComboBox)this.getComponent("DR_Combo");
        String t_dept = this.getValueString("Dept_Combo");
        String t_dr = this.getValueString("DR_Combo");
        if(t_dr.length()<=0){
            if (t_dept.length() > 0) { //�ж�combo�Ƿ�ѡ��ֵ ѡ��ָ�����ҵ�ҽʦ
                dr_list = STAIn_08Tool.getInstance().selectDyByDept(
                    t_dept,Operator.getRegion());//==========pangben modify 20110525
            }
            else { //����ҽʦ
                dr_list = STAIn_08Tool.getInstance().selectDr(Operator.getRegion());//==========pangben modify 20110525
            }
        }else{
            dr_list.addData("USER_ID",t_dr);
            dr_list.addData("USER_NAME",this.getText("DR_Combo"));
        }

        dr_data = STAIn_08Tool.getInstance().selectData(DATE_S,DATE_E,dr_list,Operator.getRegion());//==========pangben modify 20110525
        if(dr_data.getErrCode()<0){
            this.messageBox_("����ʧ�ܣ�"+dr_data.getErrName()+dr_data.getErrText());
            return;
        }
        
        ((TTable)this.getComponent("Table")).setParmValue(dr_data);
    }
    /**
     * ��ӡ
     */
    public void onPrint(){
        if(dr_data==null){
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
        TParm print = new TParm();
        print.setData(dr_data.getData());
        print.setCount(dr_data.getCount("DR_NAME"));
        print.addData("SYSTEM", "COLUMNS", "DR_NAME");
        print.addData("SYSTEM", "COLUMNS", "DATA_01");
        print.addData("SYSTEM", "COLUMNS", "DATA_02");
        print.addData("SYSTEM", "COLUMNS", "DATA_03");
        print.addData("SYSTEM", "COLUMNS", "DATA_04");
        print.addData("SYSTEM", "COLUMNS", "DATA_05");
        print.addData("SYSTEM", "COLUMNS", "DATA_06");
        print.addData("SYSTEM", "COLUMNS", "DATA_07");
        print.addData("SYSTEM", "COLUMNS", "DATA_08");
        print.addData("SYSTEM", "COLUMNS", "DATA_09");
        print.addData("SYSTEM", "COLUMNS", "DATA_10");
        print.addData("SYSTEM", "COLUMNS", "DATA_11");
        print.addData("SYSTEM", "COLUMNS", "DATA_12");
        print.addData("SYSTEM", "COLUMNS", "DATA_13");
        print.addData("SYSTEM", "COLUMNS", "DATA_14");
        print.addData("SYSTEM", "COLUMNS", "DATA_15");

        String data_s = StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyy��MM��dd��");
        String data_e = StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyy��MM��dd��");

        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        printParm.setData("unit","TEXT",Operator.getHospitalCHNFullName());//���λ
        printParm.setData("date","TEXT",data_s + " ~ " +data_e);//��������
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_08.jhw", printParm);
    }
    /**
     * ���
     */
    public void onClear(){
        dateInit();//���ڳ�ʼ��
        ((TTextFormat)this.getComponent("Dept_Combo")).setValue("");
        ((TTextFormat)this.getComponent("Dept_Combo")).setText("");
        ((TTextFormat)this.getComponent("DR_Combo")).setValue("");
        ((TTextFormat)this.getComponent("DR_Combo")).setText("");
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
    }
    /**
     * ����combo�䶯 ɸѡҽʦ
     */
    public void onDeptChange(){
        this.clearValue("DR_Combo");
    }
    /**
     * ʱ���ʼ��
     */
    public void dateInit(){
        //���ó�ʼʱ��
        String lastMonth = StringTool.getString(STATool.getInstance().getLastMonth(),"yyyyMM");//��ȡ�ϸ���
        Timestamp endDay = STATool.getInstance().getLastDayOfMonth(lastMonth);//��ȡ�·����һ��
        Timestamp startDay = StringTool.getTimestamp(lastMonth+"01","yyyyMMdd");//��ȡ�·ݵ�һ��
        this.setValue("DATE_S",startDay);
        this.setValue("DATE_E",endDay);
    }
}
