package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.util.Calendar;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.inf.INFReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: 新生儿病房日志 </p>
 *
 * <p>Description: 新生儿病房日志 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author WangLong 2014.03.04
 * @version 1.0
 */
public class INFNewBornLogControl extends TControl {

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
        if (endDate.compareTo(sysDate) >= 0) {
            this.messageBox("结束日期不能大于等于今天");
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
        TParm result = INFReportTool.getInstance().selectNewBornLog(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            messageBox("E0008");// 查无资料
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }
        TParm parmValue = new TParm();
        String date="";
        for (int i = 0; i < result.getCount(); i++) {
            result.setData("POST_DATE", i, StringTool.getString(StringTool.getTimestamp(result
                    .getValue("POST_DATE", i), "yyyyMMdd"), "yyyy/MM/dd"));
            if (!date.equals(result.getValue("POST_DATE", i))) {
                date = result.getValue("POST_DATE", i);
                parmValue.addData("POST_DATE", date);
                parmValue.addData("NEWIN_1", 0);
                parmValue.addData("NOWIN_1", 0);
                parmValue.addData("VIPNUM_1", 0);
                parmValue.addData("BMPNUM_1", 0);
                parmValue.addData("NEWIN_2", 0);
                parmValue.addData("NOWIN_2", 0);
                parmValue.addData("VIPNUM_2", 0);
                parmValue.addData("BMPNUM_2", 0);
                parmValue.addData("NEWIN_3", 0);
                parmValue.addData("NOWIN_3", 0);
                parmValue.addData("VIPNUM_3", 0);
                parmValue.addData("BMPNUM_3", 0);
                parmValue.addData("NEWIN_4", 0);
                parmValue.addData("NOWIN_4", 0);
                parmValue.addData("VIPNUM_4", 0);
                parmValue.addData("BMPNUM_4", 0);
            } else if (date.equals(result.getValue("POST_DATE", i))) {
                continue;
            }
        }
        parmValue.setCount(parmValue.getCount("POST_DATE"));
        int i_NEWIN_1 = 0;
        int i_NOWIN_1 = 0;
        int i_VIPNUM_1 = 0;
        int i_BMPNUM_1 = 0;
        int i_NEWIN_2 = 0;
        int i_NOWIN_2 = 0;
        int i_VIPNUM_2 = 0;
        int i_BMPNUM_2 = 0;
        int i_NEWIN_3 = 0;
        int i_NOWIN_3 = 0;
        int i_VIPNUM_3 = 0;
        int i_BMPNUM_3 = 0;
        int i_NEWIN_4 = 0;
        int i_NOWIN_4 = 0;
        int i_VIPNUM_4 = 0;
        int i_BMPNUM_4 = 0;
        for (int i = 0; i < parmValue.getCount(); i++) {
            for (int j = 0; j < result.getCount(); j++) {
                if (parmValue.getData("POST_DATE", i).equals(result.getData("POST_DATE", j))) {
                    if (result.getInt("WEIGHT", j) == 0 || result.getInt("WEIGHT", j) == 1) {
                        parmValue.setData("NEWIN_1",
                                          i,
                                          parmValue.getInt("NEWIN_1", i)
                                                  + result.getInt("NEWIN_COUNT", j));
                        parmValue.setData("NOWIN_1",
                                          i,
                                          parmValue.getInt("NOWIN_1", i)
                                                  + result.getInt("NOWIN_COUNT", j));
                        parmValue.setData("VIPNUM_1",
                                          i,
                                          parmValue.getInt("VIPNUM_1", i)
                                                  + result.getInt("VIP_NUM", j));
                        parmValue.setData("BMPNUM_1",
                                          i,
                                          parmValue.getInt("BMPNUM_1", i)
                                                  + result.getInt("BMP_NUM", j));
                        i_NEWIN_1 += result.getInt("NEWIN_COUNT", j);
                        i_NOWIN_1 += result.getInt("NOWIN_COUNT", j);
                        i_VIPNUM_1 += result.getInt("VIP_NUM", j);
                        i_BMPNUM_1 += result.getInt("BMP_NUM", j);
                    } else if (result.getInt("WEIGHT", j) == 2) {
                        parmValue.setData("NEWIN_2", i, result.getInt("NEWIN_COUNT", j));
                        parmValue.setData("NOWIN_2", i, result.getInt("NOWIN_COUNT", j));
                        parmValue.setData("VIPNUM_2", i, result.getInt("VIP_NUM", j));
                        parmValue.setData("BMPNUM_2", i, result.getInt("BMP_NUM", j));
                        i_NEWIN_2 += result.getInt("NEWIN_COUNT", j);
                        i_NOWIN_2 += result.getInt("NOWIN_COUNT", j);
                        i_VIPNUM_2 += result.getInt("VIP_NUM", j);
                        i_BMPNUM_2 += result.getInt("BMP_NUM", j);
                    } else if (result.getInt("WEIGHT", j) == 3 || result.getInt("WEIGHT", j) == 4) {
                        parmValue.setData("NEWIN_3",
                                          i,
                                          parmValue.getInt("NEWIN_3", i)
                                                  + result.getInt("NEWIN_COUNT", j));
                        parmValue.setData("NOWIN_3",
                                          i,
                                          parmValue.getInt("NOWIN_3", i)
                                                  + result.getInt("NOWIN_COUNT", j));
                        parmValue.setData("VIPNUM_3",
                                          i,
                                          parmValue.getInt("VIPNUM_3", i)
                                                  + result.getInt("VIP_NUM", j));
                        parmValue.setData("BMPNUM_3",
                                          i,
                                          parmValue.getInt("BMPNUM_3", i)
                                                  + result.getInt("BMP_NUM", j));
                        i_NEWIN_3 += result.getInt("NEWIN_COUNT", j);
                        i_NOWIN_3 += result.getInt("NOWIN_COUNT", j);
                        i_VIPNUM_3 += result.getInt("VIP_NUM", j);
                        i_BMPNUM_3 += result.getInt("BMP_NUM", j);
                    } else {
                        parmValue.setData("NEWIN_4", i, result.getInt("NEWIN_COUNT", j));
                        parmValue.setData("NOWIN_4", i, result.getInt("NOWIN_COUNT", j));
                        parmValue.setData("VIPNUM_4", i, result.getInt("VIP_NUM", j));
                        parmValue.setData("BMPNUM_4", i, result.getInt("BMP_NUM", j));
                        i_NEWIN_4 += result.getInt("NEWIN_COUNT", j);
                        i_NOWIN_4 += result.getInt("NOWIN_COUNT", j);
                        i_VIPNUM_4 += result.getInt("VIP_NUM", j);
                        i_BMPNUM_4 += result.getInt("BMP_NUM", j);
                    }
                }
            }
        }
        parmValue.addData("POST_DATE", "合计");
        parmValue.addData("NEWIN_1", i_NEWIN_1);
        parmValue.addData("NOWIN_1", i_NOWIN_1);
        parmValue.addData("VIPNUM_1", i_VIPNUM_1);
        parmValue.addData("BMPNUM_1", i_BMPNUM_1);
        parmValue.addData("NEWIN_2", i_NEWIN_2);
        parmValue.addData("NOWIN_2", i_NOWIN_2);
        parmValue.addData("VIPNUM_2", i_VIPNUM_2);
        parmValue.addData("BMPNUM_2", i_BMPNUM_2);
        parmValue.addData("NEWIN_3", i_NEWIN_3);
        parmValue.addData("NOWIN_3", i_NOWIN_3);
        parmValue.addData("VIPNUM_3", i_VIPNUM_3);
        parmValue.addData("BMPNUM_3", i_BMPNUM_3);
        parmValue.addData("NEWIN_4", i_NEWIN_4);
        parmValue.addData("NOWIN_4", i_NOWIN_4);
        parmValue.addData("VIPNUM_4", i_VIPNUM_4);
        parmValue.addData("BMPNUM_4", i_BMPNUM_4);
        parmValue.setCount(parmValue.getCount("POST_DATE"));
        this.callFunction("UI|TABLE|setParmValue", parmValue);
    }
    
    /**
     * 打印
     */
    public void onPrint(){
        if (table.getRowCount() < 1) {
            return;
        }
        TParm parm = table.getShowParmValue();
        for (int i = 0; i < parm.getCount() - 1; i++) {
            parm.setData("POST_DATE", i, parm.getValue("POST_DATE", i).substring(5));
        }
        parm.addData("SYSTEM", "COLUMNS", "POST_DATE");
        parm.addData("SYSTEM", "COLUMNS", "NEWIN_1");
        parm.addData("SYSTEM", "COLUMNS", "NOWIN_1");
        parm.addData("SYSTEM", "COLUMNS", "VIPNUM_1");
        parm.addData("SYSTEM", "COLUMNS", "BMPNUM_1");
        parm.addData("SYSTEM", "COLUMNS", "NEWIN_2");
        parm.addData("SYSTEM", "COLUMNS", "NOWIN_2");
        parm.addData("SYSTEM", "COLUMNS", "VIPNUM_2");
        parm.addData("SYSTEM", "COLUMNS", "BMPNUM_2");
        parm.addData("SYSTEM", "COLUMNS", "NEWIN_3");
        parm.addData("SYSTEM", "COLUMNS", "NOWIN_3");
        parm.addData("SYSTEM", "COLUMNS", "VIPNUM_3");
        parm.addData("SYSTEM", "COLUMNS", "BMPNUM_3");
        parm.addData("SYSTEM", "COLUMNS", "NEWIN_4");
        parm.addData("SYSTEM", "COLUMNS", "NOWIN_4");
        parm.addData("SYSTEM", "COLUMNS", "VIPNUM_4");
        parm.addData("SYSTEM", "COLUMNS", "BMPNUM_4");
        TParm result = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        result.setData("HOSP_DESC", Operator.getHospitalCHNFullName());
        result.setData("DEPT_CODE", "TEXT", this.getText("DEPT_CODE"));
        result.setData("Date", "TEXT", StringTool.getString(now, "yyyy/MM/dd"));
        result.setData("USER_ID", "TEXT", Operator.getName());
        result.setData("TABLE", parm.getData());
        this.openPrintDialog("%ROOT%\\config\\prt\\inf\\INFNewBornLog.jhw", result, false);
    }

    /**
     * 导出
     */
    public void onExport() {
        if (table.getRowCount() < 1) {
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "新生儿病房日志");
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
