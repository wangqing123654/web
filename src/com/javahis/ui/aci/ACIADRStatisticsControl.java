package com.javahis.ui.aci;

import java.sql.Timestamp;
import jdo.aci.ACIADRTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.system.textFormat.TextFormatADRName;
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
public class ACIADRStatisticsControl
        extends TControl {

    TTable table;// ���
    TRadioButton day;// �գ���ѡ��ť��
    TRadioButton count;// ��������ѡ��ť��
    TTextFormat startDate;// ��ʼʱ�䣨������
    TTextFormat endDate;// ����ʱ�䣨������
    TTextFormat deptCode;// �ϱ����ң�������
    TTextFormat phaRule;// ҩƷ���ࣨ������
    TComboBox reportType;// �������ͣ�������
    TextFormatADRName adrID;// �¼����ࣨ������
    private String dateType = "";// ��������
    private String queryType = "";// ��ѯ����
 
    
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        day = (TRadioButton) this.getComponent("DAY");
        count = (TRadioButton) this.getComponent("NUM");
        startDate = (TTextFormat) this.getComponent("START_DATE");
        endDate = (TTextFormat) this.getComponent("END_DATE");
        deptCode = (TTextFormat) this.getComponent("REPORT_DEPT");
        phaRule = (TTextFormat) this.getComponent("PHA_RULE");
        reportType= (TComboBox) this.getComponent("REPORT_TYPE");
        adrID = (TextFormatADRName) this.getComponent("ADR_ID");
        initUI();
    }

    /**
     * ��ʼ��������Ϣ
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        count.setSelected(true);
        onChooseCount();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("DATE_TYPE", dateType);
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        if (dateType.equals("YEAR")) {
            parm.setData("START_DATE", StringTool.getString(startDate, "yyyy"));
            parm.setData("END_DATE", StringTool.getString(endDate, "yyyy"));
        } else if (dateType.equals("MONTH")) {
            parm.setData("START_DATE", StringTool.getString(startDate, "yyyy/MM"));
            parm.setData("END_DATE", StringTool.getString(endDate, "yyyy/MM"));
        } else if (dateType.equals("DAY")) {
            parm.setData("START_DATE", StringTool.getString(startDate, "yyyy/MM/dd"));
            parm.setData("END_DATE", StringTool.getString(endDate, "yyyy/MM/dd"));
        }
        TParm result = new TParm();
        if (queryType.equals("NUM")) {// ������
            result = ACIADRTool.getInstance().selectStatisticByCount(parm);
        } else if (queryType.equals("SEX")) {// ���Ա�
            result = ACIADRTool.getInstance().selectStatisticBySex(parm);
        } else if (queryType.equals("AGE")) {// �������
            result = ACIADRTool.getInstance().selectStatisticByAge(parm);
        } else if (queryType.equals("DEPT")) {// ���ϱ�����
            if (!this.getValueString("REPORT_DEPT").equals("")) {
                parm.setData("REPORT_DEPT", this.getValue("REPORT_DEPT"));
            }
            result = ACIADRTool.getInstance().selectStatisticByDept(parm);
        } else if (queryType.equals("PHA")) {// ��ҩƷ����
            if (!this.getValueString("PHA_RULE").equals("")) {
                parm.setData("PHA_RULE", this.getValue("PHA_RULE"));
            }
            result = ACIADRTool.getInstance().selectStatisticByPha(parm);
        } else if (queryType.equals("TYPE")) {// ����������
            if (!this.getValueString("REPORT_TYPE").equals("")) {
                parm.setData("REPORT_TYPE", this.getValue("REPORT_TYPE"));
            }
            result = ACIADRTool.getInstance().selectStatisticByType(parm);
        } else if (queryType.equals("NAME")) {// ���¼�����
            if (!this.getValueString("ADR_ID").equals("")) {
                parm.setData("ADR_ID", this.getValue("ADR_ID"));
            }
            result = ACIADRTool.getInstance().selectStatisticByName(parm);
        }
        if (result.getErrCode() < 0) {
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
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        String startDateStr = "";
        String endDateStr = "";
        if (dateType.equals("YEAR")) {
            startDateStr = StringTool.getString(startDate, "yyyy");
            endDateStr = StringTool.getString(endDate, "yyyy");
        } else if (dateType.equals("MONTH")) {
            startDateStr = StringTool.getString(startDate, "yyyy-MM");
            endDateStr = StringTool.getString(endDate, "yyyy-MM");
        } else if (dateType.equals("DAY")) {
            startDateStr = StringTool.getString(startDate, "yyyy-MM-dd");
            endDateStr = StringTool.getString(endDate, "yyyy-MM-dd");
        }
        if (table.getRowCount() > 0) ExportExcelUtil.getInstance().exportExcel(table,
        /* startDateStr + "~" + endDateStr + */"ҩƷ�����¼�ͳ��");
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("REPORT_DEPT;PHA_RULE;ADR_ID");
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

    /**
     * ѡ���ꡱ��ѡ��ť
     */
    public void onChooseYear() {
        dateType = "YEAR";
        startDate.setFormat("yyyy");
        endDate.setFormat("yyyy");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("START_DATE", yesterday);
        setValue("END_DATE", today);
    }

    /**
     * ѡ���¡���ѡ��ť
     */
    public void onChooseMonth() {
        dateType = "MONTH";
        startDate.setFormat("yyyy/MM");
        endDate.setFormat("yyyy/MM");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("START_DATE", yesterday);
        setValue("END_DATE", today);
    }

    /**
     * ѡ���ա���ѡ��ť
     */
    public void onChooseDay() {
        dateType = "DAY";
        startDate.setFormat("yyyy/MM/dd");
        endDate.setFormat("yyyy/MM/dd");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("START_DATE", yesterday);
        setValue("END_DATE", today);
    }

    /**
     * ѡ����������ѡ��ť
     */
    public void onChooseCount() {
        queryType = "NUM";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }
    
    /**
     * ѡ���Ա𡱵�ѡ��ť
     */
    public void onChooseSex() {
        queryType = "SEX";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }
    
    /**
     * ѡ������Ρ���ѡ��ť
     */
    public void onChooseAge() {
        queryType = "AGE";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }
    

    /**
     * ѡ���ϱ����ҡ���ѡ��ť
     */
    public void onChooseDept() {
        queryType = "DEPT";
        deptCode.setEnabled(true);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }

    /**
     * ѡ��ҩƷ���ࡱ��ѡ��ť
     */
    public void onChoosePha() {
        queryType = "PHA";
        deptCode.setEnabled(false);
        phaRule.setEnabled(true);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }

    /**
     * ѡ�񡰱������͡���ѡ��ť
     */
    public void onChooseType() {
        queryType = "TYPE";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(true);
        adrID.setEnabled(false);
    }
    
    /**
     * ѡ���¼����ơ���ѡ��ť
     */
    public void onChooseName() {
        queryType = "NAME";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(true);
    }
    
}
