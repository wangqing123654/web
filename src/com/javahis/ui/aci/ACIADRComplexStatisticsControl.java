package com.javahis.ui.aci;

import java.sql.Timestamp;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import jdo.aci.ACIADRTool;
import jdo.sys.SystemTool;

/**
 * <p> Title: ҩƷ�����¼�����ͳ�� </p>
 * 
 * <p> Description: ҩƷ�����¼�����ͳ�� </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 2013.09.30
 * @version 1.0
 */
public class ACIADRComplexStatisticsControl extends TControl {
    TTable table;// ���
    TRadioButton year;// �꣨��ѡ��ť��
    TRadioButton month;// �£���ѡ��ť��
    TRadioButton day;// �գ���ѡ��ť��
    TTextFormat date;// ʱ��
    TRadioButton phaAndDept;// ҩƷ����/�ϱ����ң���ѡ��ť��
    TRadioButton nameAndDept;// �¼�����/�ϱ����ң���ѡ��ť��
    TRadioButton nameAndPha;// �¼�����/ҩƷ���ࣨ��ѡ��ť��
    private String dateType = "";// ��������
    private String queryType = "";// ��ѯ����

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        year = (TRadioButton) this.getComponent("YEAR");
        month = (TRadioButton) this.getComponent("MONTH");
        day = (TRadioButton) this.getComponent("DAY");
        date = (TTextFormat) this.getComponent("DATE");
        phaAndDept = (TRadioButton) this.getComponent("PHA_AND_DEPT");
        nameAndDept = (TRadioButton) this.getComponent("NAME_AND_DEPT");
        nameAndPha = (TRadioButton) this.getComponent("NAME_AND_PHA");
        initUI();
    }

    /**
     * ��ʼ��������Ϣ
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        phaAndDept.setSelected(true);
        onChoosePhaAndDept();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("DATE_TYPE", dateType);
        Timestamp startDate = (Timestamp) this.getValue("DATE");
        if(dateType.equals("YEAR")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy"));
        } else if(dateType.equals("MONTH")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy/MM"));
        } else if(dateType.equals("DAY")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy/MM/dd"));
        }
        TParm result = new TParm();
        if (queryType.equals("PHA_AND_DEPT")) {// ����ҩƷ����/�ϱ����ҡ�
            result = ACIADRTool.getInstance().selectStatisticByPhaAndDept(parm);
        } else if (queryType.equals("NAME_AND_DEPT")) {// �����¼�����/�ϱ����ҡ�
            result = ACIADRTool.getInstance().selectStatisticByNameAndDept(parm);
        } else if (queryType.equals("NAME_AND_PHA")) {// �����¼�����/ҩƷ���ࡱ
            result = ACIADRTool.getInstance().selectStatisticByNameAndPha(parm);
        }
        if(result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
        }
        table.setDSValue();
        table.setHeader(result.getValue("TABLE_HEADER"));
        table.setParmMap(result.getValue("TABLE_PARMMAP"));
        table.setParmValue(result);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        Timestamp startDate = (Timestamp) this.getValue("DATE");
        String dateStr = "";
        if (dateType.equals("YEAR")) {
            dateStr = StringTool.getString(startDate, "yyyy");
        } else if (dateType.equals("MONTH")) {
            dateStr = StringTool.getString(startDate, "yyyy-MM");
        } else if (dateType.equals("DAY")) {
            dateStr = StringTool.getString(startDate, "yyyy-MM-dd");
        }
        if (table.getRowCount() > 0) ExportExcelUtil.getInstance().exportExcel(table, "ҩƷ�����¼�����ͳ��");
    }

    /**
     * ���
     */
    public void onClear() {
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

    /**
     * ѡ���ꡱ��ѡ��ť
     */
    public void onChooseYear() {
        dateType = "YEAR";
        date.setFormat("yyyy");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * ѡ���¡���ѡ��ť
     */
    public void onChooseMonth() {
        dateType = "MONTH";
        date.setFormat("yyyy/MM");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * ѡ���ա���ѡ��ť
     */
    public void onChooseDay() {
        dateType = "DAY";
        date.setFormat("yyyy/MM/dd");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * ѡ��ҩƷ����/�ϱ����ҡ���ѡ��ť
     */
    public void onChoosePhaAndDept() {
        queryType = "PHA_AND_DEPT";
    }

    /**
     * ѡ���¼�����/�ϱ����ҡ���ѡ��ť
     */
    public void onChooseNameAndDept() {
        queryType = "NAME_AND_DEPT";
     
    }
    
    /**
     * ѡ���¼�����/ҩƷ���ࡱ��ѡ��ť
     */
    public void onChooseNameAndPha() {
        queryType = "NAME_AND_PHA";

    }



}
