package com.javahis.ui.spc;

/**
 * <p>
 * Title: �ɹ��ƻ����ɶ�����Control
 * </p>
 *
 * <p>
 * Description: �ɹ��ƻ����ɶ�����Control
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
 * @author zhangy 2009.07.20
 * @version 1.0
 */
import java.sql.Timestamp;

import jdo.ind.INDTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;

public class INDOrderFormControl
    extends TControl {
    private TParm parm;
    private String org_code;
    private String plan_month;
    private String plan_no;

    private TTable table;

    public INDOrderFormControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            org_code = parm.getValue("ORG_CODE");
            plan_month = parm.getValue("PLAN_MONTH");
            plan_no = parm.getValue("PLAN_NO");
        }
        // ��ʼ��������
        initPage();
    }

    /**
     * ���ɷ���
     */
    public void onCreate() {
        if (!CheckData()) {
            return;
        }
        TParm result = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("PURORDER_DATE", date);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE",Operator.getRegion());
        result.setData("PUR_M", parm.getData());
        TParm parmD = new TParm();
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                parmD.setRowData(count, table.getParmValue().getRow(i));
                count++;
            }
        }
        result.setData("PUR_D", parmD.getData());
        result = TIOM_AppServer.executeAction("action.ind.INDPurPlanAction",
                                              "onCreatePurOrder", result);
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    }

    /**
     * ȫѡ��ѡ��ѡ���¼�
     */
    public void onCheckSelectAll() {
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "Y");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "N");
                this.setValue("SUM_MONEY", 0);
            }
        }
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
        // ���ѡ�е���
        int column = table.getSelectedColumn();
        int row = table.getSelectedRow();
        if (column == 0) {
            double atm = table.getItemDouble(row, "ATM");
            if ("Y".equals(table.getItemString(row, column))) {
                table.setItem(row, "SELECT_FLG", true);
                atm = this.getValueDouble("SUM_MONEY") + atm;
                this.setValue("SUM_MONEY", atm);
            }
            else {
                table.setItem(row, "SELECT_FLG", false);
                atm = this.getValueDouble("SUM_MONEY") - atm;
                this.setValue("SUM_MONEY", atm);
            }
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLE_M|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        table = getTable("TABLE_M");
        this.setValue("ORG_CODE", org_code);
        this.setValue("PLAN_MONTH", StringTool.getTimestamp(plan_month,
            "yyyy-MM-dd"));
        this.setValue("PLAN_NO", plan_no);
        TParm inparm = new TParm();
        inparm.setData("ORG_CODE", org_code);
        inparm.setData("PLAN_NO", plan_no);
        TParm result = INDTool.getInstance().onQueryCreatePurPlanD(inparm);
        table.setParmValue(result);

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

    /**
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �����ܽ��
     *
     * @return
     */
    private double getSumMoney() {
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            sum += table.getItemDouble(i, "ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckData() {
        // �ж��Ƿ��б�ѡ����
        table.acceptText();
        boolean flg = false;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                flg = true;
            }
        }
        if (!flg) {
            this.messageBox("û��ѡ����");
            return false;
        }
        return true;
    }
}
