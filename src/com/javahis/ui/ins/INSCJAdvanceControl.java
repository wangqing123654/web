package com.javahis.ui.ins;

import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.ins.INSCJAdvanceTool;
import jdo.ins.InsManager;

/**
 * <p>Title: �Ǿӵ渶�걨������</p>
 *
 * <p>Description: �Ǿӵ渶�걨������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.14
 * @version 1.0
 */
public class INSCJAdvanceControl
    extends TControl {
    TParm data;

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        //��Ժ��ʼʱ��
        setValue("S_DATE", yesterday);
        //��Ժ����ʱ��
        setValue("E_DATE", SystemTool.getInstance().getDate());

    }

    /**
     * ����
     */
    public void onSave() {
        TParm result = new TParm();
        int row = getTTable("Table").getSelectedRow();
        if (row < 0) {
            this.messageBox("���ѡһ������");
            return;
        }
        TParm parm = new TParm();
        parm = getTTable("Table").getParmValue().getRow(row);
        //����渶�����걨
        this.DataDown_czys_I(parm);
        String specialSitu = this.getValueString("SPECIAL_SITU");
        if(specialSitu!=null||specialSitu.length()>0){
            //����渶��������ϴ�
            this.DataDown_czys_T(parm);
            //����INS_ADVANCE_PAYMENT�������
            result = INSCJAdvanceTool.getInstance().upSpecialSitu(parm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
        }
        //�õ��渶�����ϴ���ϸ
        result = INSCJAdvanceTool.getInstance().getAdvUpLoadData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        //סԺ�ϴ�������ϸ
        this.DataUpload_E(parm);
        //����INS_ADVANCE_PAYMENTҽ��״̬,����
        result = INSCJAdvanceTool.getInstance().upInsStaDate(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;

        }
        //����Ʊ��
        result = INSCJAdvanceTool.getInstance().upInsInvNo(parm);

        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;

        }
        else {
            this.messageBox("P0005");
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("S_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue(
                         "S_DATE")), "yyyyMMdd"));
        parm.setData("E_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue(
                         "E_DATE")), "yyyyMMdd"));
        result = INSCJAdvanceTool.getInstance().getINSIbsData(parm);

       // System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount("ADM_SEQ") < 1) {
            //��������
            this.messageBox("E0008");
            this.initPage();
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        return;
    }

    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.clearValue("MR_NO");
    }

    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    /**
     * ����渶�����걨
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_I(TParm parm) {
        TParm result = new TParm();
        parm.setData("PIPELINE", "DataDown_czys");
        parm.setData("PLOT_TYPE", "I");
        parm.addData("ADM_SEQ", "");
        parm.addData("HOSP_NHI_CODE", "");
        result = InsManager.getInstance().safe(parm);
       // System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����渶��������ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_T(TParm parm) {
        TParm result = new TParm();
        parm.setData("PIPELINE", "DataDown_czys");
        parm.setData("PLOT_TYPE", "T");
        parm.addData("CONFIRM_NO", "");
        parm.addData("SPECIAL_SITUATION", "");
        result = InsManager.getInstance().safe(parm);
       // System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * סԺ�ϴ�������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_E(TParm parm) {
        TParm result = new TParm();
        parm.setData("PIPELINE", "DataUpload");
        parm.setData("PLOT_TYPE", "E");
        parm.addData("INVNO", this.getValueString("INV_NO"));
        parm.addData("ADM_SEQ", "");
        parm.addData("NEWADM_SEQ", "");
        parm.addData("INSBRANCH_CODE", "");
        parm.addData("CHARGE_DATE", "");
        parm.addData("SEQ_NO", "");
        parm.addData("HOSP_NHI_NO", "");
        parm.addData("NHI_ORDER_CODE", "");
        parm.addData("ORDER_DESC", "");
        parm.addData("OWN_RATE", "");
        parm.addData("JX", "");
        parm.addData("GG", "");
        parm.addData("PRICE", "");
        parm.addData("QTY", "");
        parm.addData("TOTAL_AMT", "");
        parm.addData("TOTAL_NHI_AMT", "");
        parm.addData("OWN_AMT", "");
        parm.addData("ADDPAY_AMT", "");
        parm.addData("OP_FLG", "");
        parm.addData("ADDPAY_FLG", "");
        parm.addData("NHI_ORD_CLASS_CODE", "");
        parm.addData("PHAADD_FLG", "");
        parm.addData("CARRY_FLG", "");
        parm.addData("PZWH", "");
        parm.addData("INVNO", "");
        result = InsManager.getInstance().safe(parm);
       // System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }

}
