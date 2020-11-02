package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.util.Calendar;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.inf.INFReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: ICU病房日志 </p>
 *
 * <p>Description: ICU病房日志 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author WangLong 2014.03.04
 * @version 1.0
 */
public class INFICULogControl extends TControl {

    private TTable table;
	
    /**
     * 初始化方法
     */
    public void onInit() {
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        Timestamp now = SystemTool.getInstance().getDate();
        String sysDate = StringTool.getString(now, "yyyy/MM/dd");
        String startDate = this.getText("START_DATE");// 开始时间
        String endDate = this.getText("END_DATE");// 结束日期
        if (startDate.compareTo(sysDate) >= 0||endDate.compareTo(sysDate) >= 0) {
            this.messageBox("开始日期，结束日期不能大于等于今天");
            return;
        }
        String deptCode = this.getValueString("DEPT_CODE");// 科室
        if (StringUtil.isNullString(deptCode)) {
            this.messageBox("请选择科室");
            return;
        }
        TParm parm = new TParm();
        parm.setData("START_DATE", startDate);
        parm.setData("END_DATE", endDate);
        if (!StringUtil.isNullString(deptCode)) {
            parm.setData("DEPT_CODE", deptCode);
        }
        TParm result = INFReportTool.getInstance().selectICULog(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            messageBox("E0008");// 查无资料
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }
        int i_DATA_08 = 0;
        int i_DATA_16 = 0;
        int i_DATA_24 = 0;
        int i_DATA_23 = 0;
        int i_DATA_25 = 0;
        for (int i = 0; i < result.getCount(); i++) {
            result.setData("STA_DATE", i, StringTool.getString(StringTool.getTimestamp(result
                    .getValue("STA_DATE", i), "yyyyMMdd"), "yyyy/MM/dd"));
            i_DATA_08 += result.getInt("DATA_08", i);
            i_DATA_16 += result.getInt("DATA_16", i);
            i_DATA_24 += result.getInt("DATA_24", i);
            i_DATA_23 += result.getInt("DATA_23", i);
            i_DATA_25 += result.getInt("DATA_25", i);
        }
        result.addData("STA_DATE", "合计");
        result.addData("DATA_08", i_DATA_08);
        result.addData("DATA_16", i_DATA_16);
        result.addData("DATA_24", i_DATA_24);
        result.addData("DATA_23", i_DATA_23);
        result.addData("DATA_25", i_DATA_25);
        result.setCount(result.getCount("STA_DATE"));
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    /**
     * 打印
     */
    public void onPrint(){
        if (table.getRowCount() < 1) {
            return;
        }
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount() - 1; i++) {
            parm.setData("STA_DATE", i, parm.getValue("STA_DATE", i).substring(5));
        }
        parm.addData("SYSTEM", "COLUMNS", "STA_DATE");
        parm.addData("SYSTEM", "COLUMNS", "DATA_08");
        parm.addData("SYSTEM", "COLUMNS", "DATA_16");
        parm.addData("SYSTEM", "COLUMNS", "DATA_24");
        parm.addData("SYSTEM", "COLUMNS", "DATA_23");
        parm.addData("SYSTEM", "COLUMNS", "DATA_25");
        TParm result = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        result.setData("HOSP_DESC", Operator.getHospitalCHNFullName());
        result.setData("DEPT_CODE", "TEXT", this.getText("DEPT_CODE"));
        result.setData("Date", "TEXT", StringTool.getString(now, "yyyy/MM/dd"));
        result.setData("USER_ID", "TEXT", Operator.getName());
        result.setData("TABLE", parm.getData());
        this.openPrintDialog("%ROOT%\\config\\prt\\inf\\INFICULog.jhw", result, false);
    }

    /**
     * 导出
     */
    public void onExport() {
        if (table.getRowCount() < 1) {
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "ICU病房日志");
    }

    /**
     * 清空
     */
    public void onClear() {
        Timestamp now = SystemTool.getInstance().getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -1);
        this.setValue("START_DATE", new Timestamp(cal.getTimeInMillis()));// 一个月前
        this.setValue("END_DATE", StringTool.rollDate(now, -1));// 前一天
        this.clearValue("DEPT_CODE");
        table.setDSValue();
    }
}
