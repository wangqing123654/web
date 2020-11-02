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
 * <p> Title: 药品不良事件复合统计 </p>
 * 
 * <p> Description: 药品不良事件复合统计 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 2013.09.30
 * @version 1.0
 */
public class ACIADRComplexStatisticsControl extends TControl {
    TTable table;// 表格
    TRadioButton year;// 年（单选按钮）
    TRadioButton month;// 月（单选按钮）
    TRadioButton day;// 日（单选按钮）
    TTextFormat date;// 时间
    TRadioButton phaAndDept;// 药品分类/上报科室（单选按钮）
    TRadioButton nameAndDept;// 事件名称/上报科室（单选按钮）
    TRadioButton nameAndPha;// 事件名称/药品分类（单选按钮）
    private String dateType = "";// 日期种类
    private String queryType = "";// 查询种类

    /**
     * 初始化
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
     * 初始化界面信息
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        phaAndDept.setSelected(true);
        onChoosePhaAndDept();
    }

    /**
     * 查询
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
        if (queryType.equals("PHA_AND_DEPT")) {// 按“药品分类/上报科室”
            result = ACIADRTool.getInstance().selectStatisticByPhaAndDept(parm);
        } else if (queryType.equals("NAME_AND_DEPT")) {// 按“事件名称/上报科室”
            result = ACIADRTool.getInstance().selectStatisticByNameAndDept(parm);
        } else if (queryType.equals("NAME_AND_PHA")) {// 按“事件名称/药品分类”
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
     * 汇出Excel
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
        if (table.getRowCount() > 0) ExportExcelUtil.getInstance().exportExcel(table, "药品不良事件复合统计");
    }

    /**
     * 清空
     */
    public void onClear() {
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

    /**
     * 选择“年”单选按钮
     */
    public void onChooseYear() {
        dateType = "YEAR";
        date.setFormat("yyyy");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * 选择“月”单选按钮
     */
    public void onChooseMonth() {
        dateType = "MONTH";
        date.setFormat("yyyy/MM");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * 选择“日”单选按钮
     */
    public void onChooseDay() {
        dateType = "DAY";
        date.setFormat("yyyy/MM/dd");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * 选择“药品分类/上报科室”单选按钮
     */
    public void onChoosePhaAndDept() {
        queryType = "PHA_AND_DEPT";
    }

    /**
     * 选择“事件名称/上报科室”单选按钮
     */
    public void onChooseNameAndDept() {
        queryType = "NAME_AND_DEPT";
     
    }
    
    /**
     * 选择“事件名称/药品分类”单选按钮
     */
    public void onChooseNameAndPha() {
        queryType = "NAME_AND_PHA";

    }



}
