package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import jdo.bms.BMSApplyMTool;
import jdo.sys.PatTool;

import com.dongyang.ui.TMenuItem;

/**
 * <p>
 * Title: ��Ѫ���뵥��ѯ
 * </p>
 *
 * <p>
 * Description: ��Ѫ���뵥��ѯ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSApplyNoQueryControl  extends TControl {

    public BMSApplyNoQueryControl() {  
    }

    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery(){
        TParm parm = new TParm();
        // �ż�ס��
        String adm_type = "O";
        if (this.getRadioButton("ADM_TYPE_E").isSelected()) {
            adm_type = "E";
        }
        else if (this.getRadioButton("ADM_TYPE_I").isSelected()) {
            adm_type = "I";
        }
        parm.setData("ADM_TYPE", adm_type);
        // ������
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        // סԺ��
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", getValueString("IPD_NO"));
        }
        // ��Ѫ����
        if (!"".equals(this.getValueString("APPLY_NO"))) {
            parm.setData("APPLY_NO", getValueString("APPLY_NO"));
        }
        // ��Ѫ����
        parm.setData("START_DATE", getValue("START_DATE"));
        parm.setData("END_DATE", getValue("END_DATE"));
        TParm result = BMSApplyMTool.getInstance().onApplyNoQuery(parm);
        if (result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear(){
        String clearStr = "MR_NO;IPD_NO;PAT_NAME;APPLY_NO";
        this.clearValue(clearStr);
        table.removeRowAll();
        this.getRadioButton("ADM_TYPE_O").setSelected(true);
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ( (TMenuItem) getComponent("apply")).setEnabled(false);
        ( (TMenuItem) getComponent("check")).setEnabled(false);
        ( (TMenuItem) getComponent("cross")).setEnabled(false);
        ( (TMenuItem) getComponent("out")).setEnabled(false);

    }

    /**
     * �����Żس��¼�
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength()));  //  chenxi modify  20121023
    }

    /**
     * ���б�Ѫ���뵥
     */
    public void onApply() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        parm.setData("FROM_FLG", "2");
        TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
                                          parm);
    }

    /**
     * ���м����¼
     */
    public void onCheck() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        TParm result = (TParm) openDialog(
            "%ROOT%\\config\\bms\\BMSPatCheckInfo.x", parm);
    }

    /**
     * ���н�����Ѫ
     */
    public void onCross() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        TParm result = (TParm) openDialog(
            "%ROOT%\\config\\bms\\BMSBloodCross.x", parm);
    }

    /**
     * ����ѪҺ����
     */
    public void onOut() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        TParm result = (TParm) openWindow("%ROOT%\\config\\bms\\BMSBloodOut.x",
                                          parm);
    }
    //======================  chenxi 

    /**
     * ����ѪҺ������ϸ
     */
    public void onFeeDetail() {
    	TParm tableParm = table.getParmValue() ;
        TParm parm = new TParm();
        parm.setData("CASE_NO",tableParm.getValue("CASE_NO",table.getSelectedRow()));
        parm.setData("PAT_NAME",tableParm.getValue("PAT_NAME",table.getSelectedRow()));
        TParm result = (TParm) openWindow("%ROOT%\\config\\bms\\BMSQueryFee.x",parm);
    }
    /**
     * ��񵥻��¼�
     */
    public void onTableClick(){
        ( (TMenuItem) getComponent("apply")).setEnabled(true);
        ( (TMenuItem) getComponent("check")).setEnabled(true);
        ( (TMenuItem) getComponent("cross")).setEnabled(true);
        ( (TMenuItem) getComponent("out")).setEnabled(true);
        ( (TMenuItem) getComponent("feeDetail")).setEnabled(true);
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        table = getTable("TABLE");
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ( (TMenuItem) getComponent("apply")).setEnabled(false);
        ( (TMenuItem) getComponent("check")).setEnabled(false);
        ( (TMenuItem) getComponent("cross")).setEnabled(false);
        ( (TMenuItem) getComponent("out")).setEnabled(false);
        ( (TMenuItem) getComponent("feeDetail")).setEnabled(false);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
