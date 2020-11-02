package com.javahis.ui.ind;

import java.sql.*;
import java.text.*;
import java.util.Date;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.ui.*;
import com.dongyang.util.*;
import com.javahis.util.*;
import jdo.ind.*;
import jdo.sys.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class INDTotdrugControl
    extends TControl {
    public INDTotdrugControl() {

    }
    //ռ��
    String Percent;
    //���
    private TTable table;
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        //����
        Timestamp startDate = StringTool.getTimestamp(new Date());
        //����
        Timestamp endDate = StringTool.rollDate(startDate, 1);
        //��ʼ����
        this.setValue("START_DATE", startDate);
        //����ʱ��
        this.setValue("END_DATE", endDate);
        table = (TTable)this.getComponent("DrugTABLE");
        this.callFunction("UI|DrugTABLE|removeRowAll");
        selectcode();
    }

    public void selectcode() {

        if ("Y".equals(this.getValueString("RadioButton0"))) {
            this.callFunction("UI|Drug_class|setEnabled", true);
            this.callFunction("UI|PHA_CLASS|setEnabled", false);
            this.callFunction("UI|ROUTE_CODE|setEnabled", false);
            this.setValue("PHA_CLASS", "");
            this.setValue("ROUTE_CODE", "");

        }
        if ("Y".equals(this.getValueString("RadioButton1"))) {
            this.callFunction("UI|PHA_CLASS|setEnabled", true);
            this.callFunction("UI|Drug_class|setEnabled", false);
            this.callFunction("UI|ROUTE_CODE|setEnabled", false);
            this.setValue("Drug_class", "");
            this.setValue("ROUTE_CODE", "");

        }
        if ("Y".equals(this.getValueString("RadioButton2"))) {
            this.callFunction("UI|ROUTE_CODE|setEnabled", true);
            this.callFunction("UI|PHA_CLASS|setEnabled", false);
            this.callFunction("UI|Drug_class|setEnabled", false);
            this.setValue("Drug_class", "");
            this.setValue("PHA_CLASS", "");
        }
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (this.table.getRowCount() <= 0) {
            this.messageBox("û��Ҫ��ӡ������");
            return;
        }
        TParm prtParm = new TParm();
        //��ͷ
        prtParm.setData("TITLE", "TEXT", "ҩƷ������ͳ�Ʊ���");
        //����
        prtParm.setData("START_DATE", "TEXT", "���ڣ�" +
                        StringTool.getString(StringTool.getTimestamp(new Date()),
                                             "yyyy��MM��dd��") + "��");
        //��ֹ
        prtParm.setData("END_DATE", "TEXT",
                        StringTool.getString(StringTool.rollDate(StringTool.
            getTimestamp(new Date()), 1),
                                             "yyyy��MM��dd��"));
        //ռ��
        prtParm.setData("PERCENT", "TEXT", "ռ�ȣ�" + Percent);
        //����
        String dept = DeptTool.getInstance().getDescByCode(this.getValueString(
            "DEPT_CODE"));
        if (!dept.equals("") && dept.length() > 0) {
            prtParm.setData("DEPT_CODE", "TEXT", "����:" + dept);
        }
        TParm prtTableParm = this.getSelectTParm();
        prtTableParm.setCount(prtTableParm.getCount("ORDER_DESC"));
        prtTableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT");
        prtTableParm.addData("SYSTEM", "COLUMNS", "PRICE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "QTY");
        prtTableParm.addData("SYSTEM", "COLUMNS", "OUT_AMT");
        prtParm.setData("DRUGTABLE", prtTableParm.getData());
        //��β
        prtParm.setData("CREATEUSER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\ind\\INDDrugTotprint.jhw",
                             prtParm);
    }

    public void onQuery() {
        if (!checkData()) {
            return;
        }
        TParm parm = this.getSelectTParm();
        table.setParmValue(parm);
    }

    public TParm getSelectTParm() {
        TParm tableparm = new TParm();
        //�õ�ʱ���ѯ����
        String startdate = this.getValueString("START_DATE");
        startdate = startdate.substring(0, 4) + startdate.substring(5, 7) +
            startdate.substring(8, 10);
        String enddate = this.getValueString("END_DATE");
        enddate = enddate.substring(0, 4) + enddate.substring(5, 7) +
            enddate.substring(8, 10);
        String drug_class = this.getValueString("Drug_class");
        String ROUTE = this.getValueString("ROUTE_CODE");
        String deptcode = this.getValueString("DEPT_CODE");
        String pha_class = this.getValueString("PHA_CLASS");
        TParm inparm = new TParm();
        inparm.setData("START_DATE", startdate);
        inparm.setData("END_DATE", enddate);
        //ҩƷ����
        if (!drug_class.equals("") && drug_class.length() > 0) {
            inparm.setData("SYS_GRUG_CLASS", drug_class);
        }
        //��ҩ��ʽ
        if (!ROUTE.equals("") && ROUTE.length() > 0) {
            inparm.setData("ROUTE_CODE", ROUTE);
        }
        //����
        if (!deptcode.equals("") && deptcode.length() > 0) {
            inparm.setData("ORG_CODE", deptcode);
        }
        //Ժ��
        if (!Operator.getRegion().equals("") &&Operator.getRegion().length() > 0) {
            inparm.setData("REGION_CODE", Operator.getRegion());
        }
        String sql = " SELECT SUM(OUT_AMT) AS TOT FROM IND_DDSTOCK";
        TParm tot = new TParm(this.getDBTool().select(sql));
        double totnumber = tot.getDouble("TOT", 0);
        tableparm = IndDDStockTool.getInstance().onQueryBildrug(inparm);
//        System.out.println("tableparm==="+tableparm);
        TParm prtTableParm = new TParm();
        //�����ѯ������parm
        //ͳ����Ϣbegin
        double number = 0;
        DecimalFormat sf= new DecimalFormat("######0.00");
        DecimalFormat ff= new DecimalFormat("######0.0000");
        boolean pha_flg=true;
        for (int i = 0; i < tableparm.getCount(); i++) {
            TParm rowParm = tableparm.getRow(i);
            String pha=rowParm.getValue("SYS_PHA_CLASS");
            if(!pha_class.equals("")&& pha_class.length()>0)
            {
               pha_flg=SYSFeeTool.getInstance().getType(pha_class,pha);
            }
            if(pha_flg)
            {
                prtTableParm.addData("ORDER_DESC",
                                     rowParm.getValue("ORDER_DESC"));
                prtTableParm.addData("SPECIFICATION",
                                     rowParm.getValue("SPECIFICATION"));
                prtTableParm.addData("QTY",
                                     rowParm.getValue("QTY"));
                prtTableParm.addData("UNIT",
                                     rowParm.getValue("UNIT"));
                prtTableParm.addData("PRICE",
                                     ff.format(StringTool.round(rowParm.
                    getDouble("PRICE"), 4)));
                prtTableParm.addData("OUT_AMT",
                                     sf.format(StringTool.round(rowParm.
                    getDouble("OUT_AMT"), 2)));
                number += rowParm.getDouble("OUT_AMT");
            }
        }
         //ͳ����Ϣend
        String percent =sf.format(StringTool.round(number / totnumber,4)*100);
        Percent = percent + "%" + "";
        this.setValue("PERCENT", Percent);
        prtTableParm.addData("ORDER_DESC", "�ϼƣ�");
        prtTableParm.addData("SPECIFICATION","");
        prtTableParm.addData("QTY","");
        prtTableParm.addData("UNIT","");
        prtTableParm.addData("PRICE","");
        prtTableParm.addData("OUT_AMT",sf.format(StringTool.round(number, 2)));
        return prtTableParm;
    }

    public void onClear() {
        this.setValue("DEPT_CODE", "");
        this.setValue("Drug_class", "");
        this.setValue("PHA_CLASS", "");
        this.setValue("ROUTE_CODE", "");
    }

    /**
     * ����Excel
     */
    public void onExport() {
        if (table.getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(table, "ҩ��������ͳ��ϸ��");
        }
    }

    /**
     * ����Ƿ�Ϊ�ջ�մ�
     * @return boolean
     */
    private boolean checkData() {
        String start = this.getValueString("START_DATE");
        if (start == null || start.length() <= 0) {
            this.messageBox("��ʼʱ�䲻��Ϊ��");
            return false;
        }
        String end = this.getValueString("END_DATE");
        if (end == null || end.length() <= 0) {
            this.messageBox("����ʱ�䲻��Ϊ��");
            return false;
        }

        return true;
    }

    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
