package com.javahis.ui.aci;

import java.sql.Timestamp;
import jdo.aci.ACIBadEventTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.system.textFormat.TextFormatBadEventType;
/**
 * <p> Title: �����¼�����ͳ�� </p>
 * 
 * <p> Description: �����¼�����ͳ�� </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 2012.12.24
 * @version 1.0
 */
public class ACIBadEventStatisticsControl
        extends TControl {

    TTable table;// ���
    TRadioButton day;// �գ���ѡ��ť��
    TRadioButton count;// ��������ѡ��ť��
    TTextFormat startDate;// ��ʼʱ�䣨������
    TTextFormat endDate;// ����ʱ�䣨������
    TTextFormat deptCode;// �ϱ����ң�������
    TTextFormat sacClass;// SAC�ּ���������
    TextFormatBadEventType eventType;// �¼����ࣨ������
    TComboBox level;// �¼����༶����������
    private String dateType = "";// ��������
    private String queryType = "";// ��ѯ����
    private String typeLevel = "";// ���༶��
    
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
        sacClass = (TTextFormat) this.getComponent("SAC_CLASS");
        eventType = (TextFormatBadEventType) this.getComponent("EVENT_TYPE");
        level = (TComboBox) this.getComponent("LEVEL");
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
        level.setSelectedIndex(0);
        onChooseLevel();
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
        parm.setData("LEVEL", typeLevel);
        TParm result = new TParm();
        if (queryType.equals("NUM")) {// ������
            result = ACIBadEventTool.getInstance().selectStatisticByCount(parm);
        } else if (queryType.equals("DEPT")) {// ���ϱ�����
            if (!this.getValueString("REPORT_DEPT").equals("")) {
                parm.setData("REPORT_DEPT", this.getValue("REPORT_DEPT"));// �ϱ�����
            }
            result = ACIBadEventTool.getInstance().selectStatisticByDept(parm);
        } else if (queryType.equals("SAC")) {// ��SAC�ּ�
            if (!this.getValueString("SAC_CLASS").equals("")) {
                parm.setData("SAC_CLASS", this.getValue("SAC_CLASS"));// SAC�ּ�
            }
            result = ACIBadEventTool.getInstance().selectStatisticBySac(parm);
        } else if (queryType.equals("TYPE")) {// ���¼�����
            if (!this.getValueString("EVENT_TYPE").equals("")) {
                parm.setData("EVENT_TYPE", this.getValue("EVENT_TYPE"));// �����¼�����
            }
            result = ACIBadEventTool.getInstance().selectStatisticByType(parm);
        }
        if (result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
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
        /* startDateStr + "~" + endDateStr + */"�����¼�ͳ��");
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("DEPT_CODE;SAC_CLASS;EVENT_TYPE");
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

    /**
     * ѡ���ꡱ
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
     * ѡ���¡�
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
     * ѡ���ա�
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
     * ѡ��������
     */
    public void onChooseCount() {
        queryType = "NUM";
        deptCode.setEnabled(false);
        sacClass.setEnabled(false);
        eventType.setEnabled(false);
        level.setEnabled(false);
    }

    /**
     * ѡ���ϱ����ҡ�
     */
    public void onChooseDept() {
        queryType = "DEPT";
        deptCode.setEnabled(true);
        sacClass.setEnabled(false);
        eventType.setEnabled(false);
        level.setEnabled(false);
    }

    /**
     * ѡ��SAC�ּ���
     */
    public void onChooseSac() {
        queryType = "SAC";
        deptCode.setEnabled(false);
        sacClass.setEnabled(true);
        eventType.setEnabled(false);
        level.setEnabled(false);
    }

    /**
     * ѡ���¼����ࡱ
     */
    public void onChooseType() {
        queryType = "TYPE";
        deptCode.setEnabled(false);
        sacClass.setEnabled(false);
        eventType.setEnabled(true);
        level.setEnabled(true);
    }
    
    /**
     * ѡ���¼����༶��
     */
    public void onChooseLevel() {
        typeLevel = this.getValueString("LEVEL");
        eventType.setTypeLevel(typeLevel);
        eventType.onQuery();
    }
}
