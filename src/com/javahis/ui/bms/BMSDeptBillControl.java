package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.bms.BMSDeptBillTool;
import com.javahis.system.textFormat.TextFormatSYSStation;

/**
 * <p>Title: 科室费用查询</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy
 * @version 1.0
 */
public class BMSDeptBillControl
    extends TControl {

    private TTable table;
    private String caseNo ;

    public BMSDeptBillControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table = getTable("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("DEPT_CODE",Operator.getDept()) ;
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).setDeptCode(this.getValueString("DEPT_CODE"));
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
    	double totle = 0.00 ;
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("EXE_DEPT_CODE", this.getValueString("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("EXE_STATION_CODE", this.getValueString("STATION_CODE"));
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("CASE_NO", caseNo);
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", this.getValueString("IPD_NO"));
        }
        if (!"".equals(this.getValueString("OPT_USER"))) {
            parm.setData("OPT_USER", this.getValueString("OPT_USER"));
        }
        parm.setData("START_DATE", this.getValue("START_DATE").toString().substring(0, 10).replace("-", "")+"000000");
        parm.setData("END_DATE", this.getValue("END_DATE").toString().substring(0, 10).replace("-", "")+"235959");
        TParm result = BMSDeptBillTool.getInstance().onQuery(parm);
        if (result== null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            this.onClear() ;
            return;
        }
        for(int i=0;i<result.getCount();i++){
        	totle +=result.getDouble("AMT", i) ;
        }
        this.setValue("TOTLE", totle) ;
        table.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "DEPT_CODE;STATION_CODE;MR_NO;IPD_NO;OPT_USER;"
            + "START_DATE;END_DATE;TOTLE";
        this.clearValue(clearStr);
        table.removeRowAll();
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("DEPT_CODE", Operator.getDept()) ;
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).setDeptCode(this.getValueString("DEPT_CODE"));
        ((TextFormatSYSStation)this.getComponent("STATION_CODE")).onQuery();
    }

    /**
     * 病案号回车事件
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength()));//========= chenxi
        caseNo =this.onQueryByMrNo(this.getValueString("MR_NO")) ;
        this.onQuery();
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 查询个人获取case_no
     */
    public String onQueryByMrNo(String mrNo){
    	String sql ="SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO = '"+mrNo+"'"   ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		caseNo = parm.getValue("CASE_NO", 0).toString() ;
    	return caseNo ; 	
    }
}
