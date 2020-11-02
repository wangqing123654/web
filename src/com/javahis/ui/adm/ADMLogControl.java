package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import jdo.adm.ADMLogTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: סԺ��־ ����</p>
 *
 * <p>Description: סԺ��־ ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-10-29
 * @version 4.0
 */
public class ADMLogControl extends TControl {
    TTable TABLE;
    TComboBox TypeCombo;
    TParm printData;//��¼��ӡ��Ϣ
    String printDept = "";
    String printStation = "";
    String printDate = "";
    
    /**
     * ��ʼ��
     */
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("Table");
        TypeCombo = (TComboBox)this.getComponent("TYPE");
        TypeCombo.setSelectedIndex(5);
        this.setValue("DATE",SystemTool.getInstance().getDate());//��ʼ������Ϊ��ǰʱ��
        //========pangben modify 20110510 start Ȩ�����
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110510 stop
    }
    
    /**
     * ��������ѡ���¼�
     */
    public void onTypeChoose(){
        String type = TypeCombo.getSelectedID();
        //==========pangben modfiy 20110510 start ����ͷ�������
        if ("1".equals(type)) { //��Ժ����
            TABLE.setHeader("����,140;��λ,100;������,110;�Ա�,50;����,100;���,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;3,left;4,left;5,left");
        } else if ("2".equals(type)) { //��Ժ����
            TABLE.setHeader("����,140;��λ,100;������,110;�Ա�,50;����,100;���,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;3,left;4,left;5,left");
        } else if ("3".equals(type)) { //��������
            TABLE.setHeader("����,140;��λ,100;������,110;�Ա�,50;����,100;���,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;3,left;4,left;5,left");
        } else if ("4".equals(type)) { //ת�벡��
            TABLE.setHeader("����,140;��λ,100;������,110;�Ա�,50;����,100;��Դ����,100;���,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;3,left;4,left;5,left;6,left");
        } else if ("5".equals(type)) { //ת������
            TABLE.setHeader("����,140;��λ,100;������,110;�Ա�,50;����,100;ת�����,100;���,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;3,left;4,left;5,left;6,left");
        } else if ("6".equals(type)) { //����
            TABLE.setHeader(
                    "����,140;�̶���λ��,120;סԺ����,90;��Ժ����,90;��Ժ��������,120;ԭ������,90;ʵ������,90;ת������,90;ת������,90");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right");
        }
        //==========pangben modfiy 20110510 stop
        printDate = "";
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
        if(this.getValue("DATE")==null){
            this.messageBox_("��ѡ���ѯ���ڣ�");
            this.grabFocus("DATE");
            return;
        }
        if(this.getValue("DEPT")==null){
            this.messageBox_("��ѡ����ң�");
            this.grabFocus("DEPT");
            return;
        }
//        if(this.getValue("STATION")==null){
//            this.messageBox_("��ѡ������");
//            this.grabFocus("STATION");
//            return;
//        }
        String type = TypeCombo.getSelectedID();
        if("1".equals(type)){//��Ժ����
            selectInHosp();
        }else if("2".equals(type)){//��Ժ����
            selectOutHosp();
        }else if("3".equals(type)){//��������
            selectDead();
        }else if("4".equals(type)){//ת�벡��
            selectINPR();
        }else if("5".equals(type)){//ת������
            selectOUPR();
        }else if("6".equals(type)){//����
            onSelectSum();
        }
        printDept = this.getText("DEPT");
        printStation = this.getText("STATION");
        printDate = StringTool.getString((Timestamp)this.getValue("DATE"),"yyyy��MM��dd��");
    }
    
    /**
     * ������Ժ������Ϣ
     */
    private void selectInHosp(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("IN_DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("IN_STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectInHosp(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start ����������
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;MR_NO;CHN_DESC;PAT_NAME;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    
    /**
     * ���� ��Ժ������Ϣ
     */
    private void selectOutHosp(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DS_DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("DS_STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectOutHosp(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start ����������
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;MR_NO;CHN_DESC;PAT_NAME;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    
    /**
     * ��ѯ����������Ϣ
     */
    private void selectDead(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DS_DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("DS_STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectDead(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start ����������
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;MR_NO;CHN_DESC;PAT_NAME;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    
    /**
     * ��ѯת�벡����Ϣ
     */
    private void selectINPR(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectINPR(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start ����������
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;MR_NO;CHN_DESC;PAT_NAME;DEPT_CHN_DESC;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    
    /**
     * ��ѯת��������Ϣ
     */
    private void selectOUPR(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectOUPR(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
         //=============pangben modify 20110510 start ����������
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;MR_NO;CHN_DESC;PAT_NAME;DEPT_CHN_DESC;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    
    /**
     * �����������
     */
    private void onSelectSum(){
        if(this.getValueString("DEPT").length()<=0){
            this.messageBox_("��ѡ��ͳ�ƿ���!");
            this.grabFocus("DEPT");
            return;
        }
        TParm parm = new TParm();
        //=============pangben modify 20110510 start ����������
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        parm.setData("DEPT",this.getValue("DEPT"));
        if(this.getValueString("STATION").length()>0){
            parm.setData("STATION",this.getValueString("STATION"));
        }
        TParm bed = ADMLogTool.getInstance().selectBedSum(parm);
        if(bed.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        TParm seParm = new TParm();
        //=============pangben modify 20110510 start ����������
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            seParm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        seParm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            seParm.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            seParm.setData("STATION_CODE",this.getValue("STATION"));
        }
        TParm inHosp = ADMLogTool.getInstance().selectInHosp(seParm);
        TParm outHosp = ADMLogTool.getInstance().selectOutHosp(seParm);
        TParm INPR = ADMLogTool.getInstance().selectINPR(seParm);
        TParm OUPR = ADMLogTool.getInstance().selectOUPR(seParm);
        TParm dead = ADMLogTool.getInstance().selectDead(seParm);
        TParm patSum = ADMLogTool.getInstance().selectHave(seParm);//��ȡ��ѯ���ڵ�ʵ�в�����
        TParm yesteday = new TParm();
        Timestamp ye = StringTool.rollDate((Timestamp)this.getValue("DATE"),-1);
        yesteday.setData("DATE",StringTool.getString(ye,"yyyyMMdd"));
        if(this.getValueString("DEPT").length()>0){
            yesteday.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if (this.getValueString("STATION").length() > 0) {
            yesteday.setData("STATION_CODE", this.getValue("STATION"));
        }
        //=============pangben modify 20110510 start ����������
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            yesteday.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        TParm yesPatSum = ADMLogTool.getInstance().selectHave(yesteday);//��ȡ��ѯ����ǰһ���ʵ�в�����
        TParm result = new TParm();
        result.addData("REGION_CHN_DESC",null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0?Operator.getHospitalCHNShortName():"����Ժ��");
        result.addData("BED",bed.getValue("NUM",0));
        result.addData("IN",inHosp.getCount());
        result.addData("OUT",outHosp.getCount());
        result.addData("DEAD",dead.getCount());
        result.addData("YESTEDAY",yesPatSum.getValue("NUM",0));
        result.addData("PATNUM",patSum.getValue("NUM",0));
        result.addData("INPR",INPR.getCount());
        result.addData("OUPR",OUPR.getCount());
        //=============pangben modify 20110510 start ����������
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED;IN;OUT;DEAD;YESTEDAY;PATNUM;INPR;OUPR");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    
    /**
     * ����ѡ���¼�
     */
    public void onDEPT(){
        this.clearValue("STATION");
    }
    
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("DEPT;STATION");
        this.setValue("DATE", SystemTool.getInstance().getDate());
        TABLE.removeRowAll();
        TypeCombo.setSelectedIndex(5);
        onTypeChoose();
        // =======pangben modify 20110510 start
        this.setValue("REGION_CODE", Operator.getRegion());
    }
    
    /**
     * ��ӡ
     */
    public void onPrint(){
        //===========pangben modify 20110510 start
        if (null==TABLE.getParmValue()||TABLE.getParmValue().getCount("REGION_CHN_DESC") < 1){
            this.messageBox("û��Ҫ��ӡ������");
            return ;
        }
        //===========pangben modify 20110510 stop
        String type = TypeCombo.getSelectedID();
        if("1".equals(type)){//��Ժ����
            printInHosp();
        }else if("2".equals(type)){//��Ժ����
            printOutHosp();
        }else if("3".equals(type)){//��������
            printDead();
        }else if("4".equals(type)){//ת�벡��
            printINPR();
        }else if("5".equals(type)){//ת������
            printOUPR();
        }else if("6".equals(type)){//����
            printSum();
        }
    }
    
    /**
     * ��ӡ��Ժ������Ϣ
     */
    private void printInHosp(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC", printData.getValue("BED_NO_DESC", i));
            print.addData("PAT_NAME", printData.getValue("PAT_NAME", i));
            print.addData("CHN_DESC", printData.getValue("CHN_DESC", i));
            print.addData("MR_NO",printData.getValue("MR_NO",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "MR_NO");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
       String region= Operator.getHospitalCHNFullName();
       printParm.setData("TITLE", "TEXT",
                    ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"����ҽԺ") + "��Ժ�����ձ���");
       //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",printDate);
        printParm.setData("DEPT","TEXT",printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog1.jhw",printParm);
    }
    
    /**
     * ��ӡ��Ժ������Ϣ
     */
    private void printOutHosp(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
            print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
            print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
            print.addData("MR_NO",printData.getValue("MR_NO",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "MR_NO");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = Operator.getHospitalCHNFullName();
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "����ҽԺ") + "��Ժ�����ձ���");
        //========pangben modify 20110510 stop
        printParm.setData("DATE", "TEXT", printDate);
        printParm.setData("DEPT", "TEXT", printDept);
        printParm.setData("STATION", "TEXT", printStation);
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog1.jhw",printParm);
    }
    
    /**
     * ��ӡ����������Ϣ
     */
    private void printDead(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
            print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
            print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
            print.addData("MR_NO",printData.getValue("MR_NO",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "MR_NO");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = Operator.getHospitalCHNFullName();
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "����ҽԺ") + "���������ձ���");
        //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",printDate);
        printParm.setData("DEPT","TEXT",printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog1.jhw",printParm);

    }
    
    /**
     * ��ӡת�벡����Ϣ
     */
    private void printINPR(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
            print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
            print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
            print.addData("MR_NO",printData.getValue("MR_NO",i));
            print.addData("DEPT_CHN_DESC",printData.getValue("DEPT_CHN_DESC",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
       print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
       //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "MR_NO");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = Operator.getHospitalCHNFullName();
        printParm.setData("TITLE", "TEXT",
                         (this.getValue("REGION_CODE") != null &&
                          !this.getValue("REGION_CODE").equals("") ? region :
                          "����ҽԺ") + "ת�벡���ձ���");
        //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",printDate);
        printParm.setData("DEPT","TEXT",printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic",Basic.getData());
        printParm.setData("TRAN_DEPT","TEXT","�ο�ת��");
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog2.jhw",printParm);

    }
    
    /**
     * ��ӡת��������Ϣ
     */
    private void printOUPR(){
       TParm print = new TParm();
       int count = 0;
       for(int i=0;i<printData.getCount();i++){
           //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
           print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
           print.addData("MR_NO", printData.getValue("MR_NO", i));
           print.addData("CHN_DESC", printData.getValue("CHN_DESC", i));
           print.addData("PAT_NAME", printData.getValue("PAT_NAME", i));
           print.addData("DEPT_CHN_DESC",printData.getValue("DEPT_CHN_DESC",i));
           print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
           count++;
       }
       print.setCount(count);
       //=========pangben modify 201110510 start
       print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
       //=========pangben modify 201110510 stop
       print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
       print.addData("SYSTEM", "COLUMNS", "MR_NO");
       print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
       print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
       print.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
       print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
       TParm Basic = new TParm();
       Basic.setData("P_USER",Operator.getName());
       Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
       TParm printParm = new TParm();
       printParm.setData("T1", print.getData());
       //========pangben modify 20110510 start
       String region = Operator.getHospitalCHNFullName();
       printParm.setData("TITLE", "TEXT",
                        (this.getValue("REGION_CODE") != null &&
                         !this.getValue("REGION_CODE").equals("") ? region :
                         "����ҽԺ") + "ת�������ձ���");
       //========pangben modify 20110510 stop
       printParm.setData("DATE","TEXT",printDate);
       printParm.setData("DEPT","TEXT",printDept);
       printParm.setData("STATION","TEXT",printStation);
       printParm.setData("Basic",Basic.getData());
       printParm.setData("TRAN_DEPT","TEXT","ת�����");
       this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog2.jhw",printParm);
    }
    
    /**
     * ��ӡ������Ϣ
     */
    private void printSum() {
        TParm print = new TParm();
        int count = 0;
        for (int i = 0; i < printData.getCount("BED"); i++) {
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED", printData.getValue("BED", i));
            print.addData("IN", printData.getValue("IN", i));
            print.addData("OUT", printData.getValue("OUT", i));
            print.addData("DEAD", printData.getValue("DEAD", i));
            print.addData("YESTEDAY",
                          printData.getValue("YESTEDAY", i));
            print.addData("PATNUM", printData.getValue("PATNUM", i));
            print.addData("INPR", printData.getValue("INPR", i));
            print.addData("OUPR", printData.getValue("OUPR", i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED");
        print.addData("SYSTEM", "COLUMNS", "IN");
        print.addData("SYSTEM", "COLUMNS", "OUT");
        print.addData("SYSTEM", "COLUMNS", "DEAD");
        print.addData("SYSTEM", "COLUMNS", "YESTEDAY");
        print.addData("SYSTEM", "COLUMNS", "PATNUM");
        print.addData("SYSTEM", "COLUMNS", "INPR");
        print.addData("SYSTEM", "COLUMNS", "OUPR");
        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy��MM��dd��"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = Operator.getHospitalCHNFullName();
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "����ҽԺ") + "סԺ��־���ܱ�");
        //========pangben modify 20110510 stop
        printParm.setData("DATE", "TEXT", printDate);
        printParm.setData("DEPT", "TEXT", printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog3.jhw", printParm);
    }
}
