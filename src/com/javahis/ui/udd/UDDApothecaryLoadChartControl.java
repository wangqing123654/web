package com.javahis.ui.udd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.udd.UddApothecaryLoadChartTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: סԺҩ��ҩʦ����ǩ������ͳ�Ʊ�������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UDDApothecaryLoadChartControl
    extends TControl {

    public TTable table;

    public UDDApothecaryLoadChartControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        table = this.getTable("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("REGION_CODE", Operator.getRegion());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("TYPE"))) {
            this.messageBox("��ѡ���ѯ����");
            return;
        }
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        if (!"".equals(this.getValueString("TYPE"))) {
            parm.setData("TYPE", this.getValueString("TYPE"));
        }
        String start_date = this.getValueString("START_DATE");
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10);
        String end_date = this.getValueString("END_DATE");
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
            end_date.substring(8, 10);
        parm.setData("START_DATE", start_date + "000000");
        parm.setData("END_DATE", end_date + "235959");

        TParm result = UddApothecaryLoadChartTool.getInstance().onQuery(parm);
        if (result == null || result.getCount("QDATE") <= 0) {
            this.messageBox("û�в�ѯ����");
            onClear();
            return;
        }
        sumAMT(result);
        table.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "TOT_AMT;ORG_CODE;TYPE";
        this.clearValue(clearStr);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("REGION_CODE", Operator.getRegion());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        table.setParmValue(new TParm());
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {

    }

    /**
     * ���Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        String type = "";
        if ("CHECK".equals(this.getValueString("TYPE"))) {
            type = "���";
        }
        else if ("DOSAGE".equals(this.getValueString("TYPE"))) {
            type = "��ҩ";
        }
        else if ("DISPENSE".equals(this.getValueString("TYPE"))) {
            type = "��ҩ";
        }
        else {
            type = "��ҩ";
        }

        ExportExcelUtil.getInstance().exportExcel(table,
                                                  "סԺҩʦ����������ͳ�Ʊ�(" + type + ")");
    }

    /**
     * �����ܽ��
     * @param parm TParm
     */
    private void sumAMT(TParm parm) {
        double sum_amt = 0;
        for (int i = 0; i < parm.getCount("QDATE"); i++) {
            sum_amt += parm.getDouble("SUM_AMT", i);
        }
        this.setValue("TOT_AMT", StringTool.round(sum_amt, 2));
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
