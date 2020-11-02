package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.util.Calendar;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.inf.INFReportTool;

/**
 * <p>Title: 导管相关性感染监测记录报表 </p>
 *
 * <p>Description: 导管相关性感染监测记录报表 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author WangLong 2014.03.04
 * @version 1.0
 */
public class INFCatheterMoniterControl extends TControl {

    private TTable tableD;
    private TTable tableM;
    
    /**
     * 初始化方法
     */
    public void onInit() {
        tableD = (TTable) this.getComponent("TABLE_DETAIL");
        tableM = (TTable) this.getComponent("TABLE_SUM");
        onClear();
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE");// 开始时间
        String endDate = this.getText("END_DATE");// 结束日期
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
        TParm result = INFReportTool.getInstance().selectCatheterMoniter(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        TParm M = result.getParm("M");
        TParm D = result.getParm("D");
        if (M.getCount() <= 0) {
            messageBox("E0008");// 查无资料
            tableD.setParmValue(new TParm());
            tableM.setParmValue(new TParm());
            return;
        }
        tableD.setParmValue(M);
        tableM.setParmValue(D);
    }
    
    /**
     * 打印
     */
    public void onPrint(){
        if (tableD.getRowCount() < 1) {
            return;
        }
        TParm parmD = tableD.getParmValue();
        for (int i = 0; i < parmD.getCount(); i++) {
            if(parmD.getValue("ORD_SUPERVISION", i).equals("04")){
                parmD.addData("VIP_FLG", "√");
                parmD.addData("BMP_FLG", "");
                parmD.addData("LUP_FLG", "");
            }else if(parmD.getValue("ORD_SUPERVISION", i).equals("03")){
                parmD.addData("VIP_FLG", "");
                parmD.addData("BMP_FLG", "√");
                parmD.addData("LUP_FLG", "");
            }else if(parmD.getValue("ORD_SUPERVISION", i).equals("02")){
                parmD.addData("VIP_FLG", "");
                parmD.addData("BMP_FLG", "");
                parmD.addData("LUP_FLG", "√");
            }
            parmD.addData("INF_FLG_1", "");
            parmD.addData("INF_FLG_2", "");
            parmD.setData("EFF_DATE", i, StringTool.getString(parmD.getTimestamp("EFF_DATE", i), "yyyy/MM/dd"));
            parmD.setData("DC_DATE", i, StringTool.getString(parmD.getTimestamp("DC_DATE", i), "yyyy/MM/dd"));
        }
        parmD.setCount(parmD.getCount("STA_DATE"));
        parmD.removeData("SYSTEM", "COLUMNS");
        parmD.addData("SYSTEM", "COLUMNS", "STA_DATE");
        parmD.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parmD.addData("SYSTEM", "COLUMNS", "MR_NO");
        parmD.addData("SYSTEM", "COLUMNS", "VIP_FLG");
        parmD.addData("SYSTEM", "COLUMNS", "BMP_FLG");
        parmD.addData("SYSTEM", "COLUMNS", "LUP_FLG");
        parmD.addData("SYSTEM", "COLUMNS", "EFF_DATE");
        parmD.addData("SYSTEM", "COLUMNS", "DC_DATE");
        parmD.addData("SYSTEM", "COLUMNS", "IO_DAYS");
        parmD.addData("SYSTEM", "COLUMNS", "INF_FLG_1");
        parmD.addData("SYSTEM", "COLUMNS", "INF_FLG_2");
        parmD.addData("SYSTEM", "COLUMNS", "SIGNATURE");
        TParm parmM = tableM.getShowParmValue();
        parmM.addData("SYSTEM", "COLUMNS", "ORD_SUPERVISION");
        parmM.addData("SYSTEM", "COLUMNS", "PAT_COUNT");
        parmM.addData("SYSTEM", "COLUMNS", "TOT_DAYS");
        parmM.addData("SYSTEM", "COLUMNS", "AVG_DAYS");
        parmM.addData("SYSTEM", "COLUMNS", "INF_RATE");
        parmM.addData("SYSTEM", "COLUMNS", "OUT_STAY_DAYS");
        parmM.addData("SYSTEM", "COLUMNS", "IO_RATE");
        TParm result = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());
        result.setData("DEPT_CODE", "TEXT", this.getText("DEPT_CODE"));
        result.setData("Date", "TEXT", StringTool.getString(now, "yyyy/MM/dd"));
        result.setData("USER_ID", "TEXT", Operator.getName());
        result.setData("TABLE_DETAIL", parmD.getData());
        result.setData("TABLE_SUM", parmM.getData());
        this.openPrintDialog("%ROOT%\\config\\prt\\inf\\INFCatheterMoniter.jhw", result, false);
    }

    /**
     * 导出
     */
    public void onExport() {
        if (tableD.getRowCount() < 1) {
            return;
        }
        TParm parmD = tableD.getShowParmValue();
        parmD.addData("SYSTEM", "COLUMNS", "STA_DATE");
        parmD.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parmD.addData("SYSTEM", "COLUMNS", "MR_NO");
        parmD.addData("SYSTEM", "COLUMNS", "ORD_SUPERVISION");
        parmD.addData("SYSTEM", "COLUMNS", "EFF_DATE");
        parmD.addData("SYSTEM", "COLUMNS", "DC_DATE");
        parmD.addData("SYSTEM", "COLUMNS", "IO_DAYS");
        parmD.addData("SYSTEM", "COLUMNS", "INF_FLG");
        parmD.addData("SYSTEM", "COLUMNS", "SIGNATURE");
        parmD.setData("TITLE", "导管相关性感染监测记录明细");
        parmD.setData("HEAD", tableD.getHeader());
        TParm parmM = tableM.getShowParmValue();
        parmM.addData("SYSTEM", "COLUMNS", "ORD_SUPERVISION");
        parmM.addData("SYSTEM", "COLUMNS", "PAT_COUNT");
        parmM.addData("SYSTEM", "COLUMNS", "TOT_DAYS");
        parmM.addData("SYSTEM", "COLUMNS", "AVG_DAYS");
        parmM.addData("SYSTEM", "COLUMNS", "INF_RATE");
        parmM.addData("SYSTEM", "COLUMNS", "OUT_STAY_DAYS");
        parmM.addData("SYSTEM", "COLUMNS", "IO_RATE");
        parmM.setData("TITLE", "导管相关性感染监测记录汇总");
        parmM.setData("HEAD", tableM.getHeader());
        TParm[] execleTable = new TParm[]{parmD, parmM };
        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "导管相关性感染监测记录报表");
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
        tableD.setDSValue();
        tableM.setDSValue();
    }
}
