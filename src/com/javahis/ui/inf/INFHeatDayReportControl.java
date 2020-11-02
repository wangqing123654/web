package com.javahis.ui.inf;

import jdo.inf.INFReportTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 发热日报</p>
 *
 * <p>Description: 发热日报</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFHeatDayReportControl   extends TControl {

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        initUI();
    }


    /**
     * 初始化方法
     */
    public void initUI(){
        setValue("EXAMINE_DATE",SystemTool.getInstance().getDate());
        setValue("STATION_CODE",Operator.getStation());
    }

    /**
     * 查询事件
     */
    public void onQuery(){
        if(getValueString("EXAMINE_DATE").length() == 0 ||
           getValueString("STATION_CODE").length() == 0){
            messageBox("请输入检查日期及病区");
            return;
        }
        TParm parm = new TParm();
        parm.setData("EXAMINE_DATE",getValueString("EXAMINE_DATE").substring(0,10).replace("-",""));
        parm.setData("STATION_CODE",getValueString("STATION_CODE"));
        parm = INFReportTool.getInstance().selectHeatDayReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("STATION_DESC")<=0){
            messageBox("查无资料");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
    }

    /**
     * 打印事件
     */
    public void onPrint(){
        if(getValueString("EXAMINE_DATE").length() == 0 ||
           getValueString("STATION_CODE").length() == 0){
            messageBox("请输入检查日期及病区");
            return;
        }
        TParm parm = new TParm();
        parm.setData("EXAMINE_DATE",getValueString("EXAMINE_DATE").substring(0,10).replace("-",""));
        parm.setData("STATION_CODE",getValueString("STATION_CODE"));
        parm = INFReportTool.getInstance().selectHeatDayReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("STATION_DESC")<=0){
            messageBox("查无资料");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\INFHeatDayReport.jhw",parm);
    }

    /**
     * 导出Excel表格
     */
    public void onExcel(){
       TTable mainTable = getTable("TABLE");
       if(mainTable.getRowCount() <= 0){
           messageBox("无导出资料");
           return;
       }
       ExportExcelUtil.getInstance().exportExcel(mainTable, "发热日报");
   }

    /**
     * 清空方法
     */
    public void  onClear(){
        setValue("EXAMINE_DATE","");
        setValue("STATION_CODE","");
        getTable("TABLE").removeRowAll();
    }

    /**
     * 取得Table控件
     * @param tableTag String
     * @return TTable
     */
    private TTable getTable(String tableTag){
        return ((TTable)getComponent(tableTag));
    }
}
