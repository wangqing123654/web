package com.javahis.ui.sum;

import java.sql.*;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.ui.*;
import jdo.adm.*;

/**
 * <p>Title: �õ�ѡ�����������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */
public class GetDateSectionControl
    extends TControl {

    TTextFormat stratDate, endDate,sumJhw;
    TButton ok, cancle;
    String caseNo="";

    public GetDateSectionControl() {
    }

    public void onInit() {

        super.onInit();
        myInitCtl();

    }
    /**
     *  �õ��ؼ�
     */
    public void myInitCtl() {

        stratDate = (TTextFormat)this.getComponent("START_DATE");
        endDate = (TTextFormat)this.getComponent("END_DATE");
        sumJhw= (TTextFormat)this.getComponent("SUM_JHW");
        ok = (TButton)this.getComponent("OK");
        cancle = (TButton)this.getComponent("CANCLE");

        //��ʼ������
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //�õ�ǰʱ���ʼ��ʱ��ؼ�
        TParm outsideParm = (TParm)this.getParameter();
        stratDate.setValue(outsideParm.getData("IN_DATE"));
        sumJhw.setValue(outsideParm.getValue("SUM_JHW"));
        endDate.setValue(date);

    }

    /**
     * ѡ���ӡ
     */
    public void onOK() {

        //��ѡ��ʱ�������Ƿ���Ч
//        if(!ckeckDate("")){
//            this.messageBox("ѡ��ʱ�����");
//            return;
//        }
        //���ѡ���ʱ������
        TParm choiceDate=getChoiceDate();

        this.setReturnValue(choiceDate);
        this.closeWindow();
    }

    /**
     * ѡ��ȡ��
     */
    public void onCANCLE() {
        this.closeWindow();
    }

    /**
     * ���ѡ���ʱ��
     * @return TParm
     */
    private TParm getChoiceDate(){
        TParm result = new TParm();
        result.setData("START_DATE",stratDate.getValue());
        result.setData("END_DATE",endDate.getValue());
        result.setData("SUM_JHW",this.getValueString("SUM_JHW"));
        return result;
    }

    /**
     * ����ѡ��ʱ���Ƿ�Ըò�����Ч
     * startDate�����Գ�ǰ�ڲ�����Ժʱ��
     */
    public boolean ckeckDate(String caseNo){

        TParm getInDate = new TParm();
        getInDate.setData("CASE_NO", caseNo);
        TParm result = ADMTool.getInstance().getADM_INFO(getInDate);
        //�õ�ѡ���ʱ���סԺʱ��Ƚ�
        long inDate = ( (Timestamp) result.getData("IN_DATE", 0)).getTime();
        long selStartDate = ( (Timestamp) stratDate.getValue()).getTime();

        if (inDate > selStartDate)
            return false;

        //����ֹʱ���Ƿ�������ʱ���Ժ�

        return true;

    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

}
