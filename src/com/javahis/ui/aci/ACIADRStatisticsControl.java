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
 * <p> Title: 药品不良事件单项统计 </p>
 * 
 * <p> Description: 药品不良事件单项统计 </p>
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

    TTable table;// 表格
    TRadioButton day;// 日（单选按钮）
    TRadioButton count;// 例数（单选按钮）
    TTextFormat startDate;// 开始时间（下拉框）
    TTextFormat endDate;// 结束时间（下拉框）
    TTextFormat deptCode;// 上报科室（下拉框）
    TTextFormat phaRule;// 药品分类（下拉框）
    TComboBox reportType;// 报告类型（下拉框）
    TextFormatADRName adrID;// 事件分类（下拉框）
    private String dateType = "";// 日期种类
    private String queryType = "";// 查询种类
 
    
    /**
     * 初始化
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
     * 初始化界面信息
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        count.setSelected(true);
        onChooseCount();
    }

    /**
     * 查询
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
        if (queryType.equals("NUM")) {// 按例数
            result = ACIADRTool.getInstance().selectStatisticByCount(parm);
        } else if (queryType.equals("SEX")) {// 按性别
            result = ACIADRTool.getInstance().selectStatisticBySex(parm);
        } else if (queryType.equals("AGE")) {// 按年龄段
            result = ACIADRTool.getInstance().selectStatisticByAge(parm);
        } else if (queryType.equals("DEPT")) {// 按上报科室
            if (!this.getValueString("REPORT_DEPT").equals("")) {
                parm.setData("REPORT_DEPT", this.getValue("REPORT_DEPT"));
            }
            result = ACIADRTool.getInstance().selectStatisticByDept(parm);
        } else if (queryType.equals("PHA")) {// 按药品分类
            if (!this.getValueString("PHA_RULE").equals("")) {
                parm.setData("PHA_RULE", this.getValue("PHA_RULE"));
            }
            result = ACIADRTool.getInstance().selectStatisticByPha(parm);
        } else if (queryType.equals("TYPE")) {// 按报告类型
            if (!this.getValueString("REPORT_TYPE").equals("")) {
                parm.setData("REPORT_TYPE", this.getValue("REPORT_TYPE"));
            }
            result = ACIADRTool.getInstance().selectStatisticByType(parm);
        } else if (queryType.equals("NAME")) {// 按事件名称
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
     * 汇出Excel
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
        /* startDateStr + "~" + endDateStr + */"药品不良事件统计");
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("REPORT_DEPT;PHA_RULE;ADR_ID");
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

    /**
     * 选择“年”单选按钮
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
     * 选择“月”单选按钮
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
     * 选择“日”单选按钮
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
     * 选择“例数”单选按钮
     */
    public void onChooseCount() {
        queryType = "NUM";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }
    
    /**
     * 选择“性别”单选按钮
     */
    public void onChooseSex() {
        queryType = "SEX";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }
    
    /**
     * 选择“年龄段”单选按钮
     */
    public void onChooseAge() {
        queryType = "AGE";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }
    

    /**
     * 选择“上报科室”单选按钮
     */
    public void onChooseDept() {
        queryType = "DEPT";
        deptCode.setEnabled(true);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }

    /**
     * 选择“药品分类”单选按钮
     */
    public void onChoosePha() {
        queryType = "PHA";
        deptCode.setEnabled(false);
        phaRule.setEnabled(true);
        reportType.setEnabled(false);
        adrID.setEnabled(false);
    }

    /**
     * 选择“报告类型”单选按钮
     */
    public void onChooseType() {
        queryType = "TYPE";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(true);
        adrID.setEnabled(false);
    }
    
    /**
     * 选择“事件名称”单选按钮
     */
    public void onChooseName() {
        queryType = "NAME";
        deptCode.setEnabled(false);
        phaRule.setEnabled(false);
        reportType.setEnabled(false);
        adrID.setEnabled(true);
    }
    
}
