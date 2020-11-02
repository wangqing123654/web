package com.javahis.ui.opb;

import java.sql.Timestamp;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTable;
import com.javahis.util.DateUtil;

import jdo.opb.OPBExecutiveDeptListTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: 执行科室明细</p>
 *
 * <p>Description: 执行科室明细</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-4-10
 * @version 4.0
 */
public class OPBExecutiveDetialControl
    extends TControl {
    public void onInit(){
        super.onInit();
        pageInit();
    }
    /**
     * 页面初始化
     */
    public void pageInit(){
        TParm obj = (TParm)this.getParameter();
        String caseNo = "";
        String sql = "";
        this.setValue("DATE_S",StringTool.getTimestamp( obj.getValue("DATE_S"),"yyyyMMdd"));
        this.setValue("DATE_E",StringTool.getTimestamp( obj.getValue("DATE_E"),"yyyyMMdd"));
        this.setValue("DEPT_CODE",obj.getValue("DEPT_CODE"));
        this.setValue("DR_CODE",obj.getValue("DR_CODE"));
        TParm parm = new TParm();
        parm.setData("DATE_S",obj.getValue("DATE_S"));
        parm.setData("DATE_E",obj.getValue("DATE_E")+"235959");
        parm.setData("DEPT_CODE",obj.getValue("DEPT_CODE"));
        parm.setData("DR_CODE",obj.getValue("DR_CODE"));
        TParm result = OPBExecutiveDeptListTool.getInstance().selectDetial(parm);
        int count = result.getCount("CASE_NO");
        for(int i = 0; i < count; i++){
        	result.addData("AGE",patAge(result.getTimestamp("BIRTH_DATE", i)));//年龄
        	caseNo = result.getValue("CASE_NO", i);
			sql = "SELECT A.ICD_TYPE, A.ICD_CODE, A.DIAG_NOTE, B.ICD_CHN_DESC " +
				" FROM OPD_DIAGREC A, SYS_DIAGNOSIS B " +
				" WHERE A.ICD_TYPE = B.ICD_TYPE " +
				" AND A.ICD_CODE = B.ICD_CODE " +
				" AND A.CASE_NO = '"+ caseNo +"' " +
				" AND A.MAIN_DIAG_FLG = 'Y'";
			TParm mainDiagParm = new TParm(TJDODBTool.getInstance().select(sql));
			if("000.00".equals(mainDiagParm.getValue("ICD_CODE", 0)) ){
				result.addData("MAIN_DIAG", mainDiagParm.getValue("DIAG_NOTE", 0));
			}else{
				result.addData("MAIN_DIAG", mainDiagParm.getValue("ICD_CHN_DESC", 0));
			}
			
        }
        TTable table = (TTable)this.getComponent("TABLE");
        table.setParmValue(result);
    }
    
    /**
     * 计算年龄
     * @param date
     * @return
     */
    private String patAge(Timestamp date){
 	   Timestamp sysDate = SystemTool.getInstance().getDate();
        Timestamp temp = date == null ? sysDate : date;
        String age = "0";
        age = DateUtil.showAge(temp, sysDate);
        return age;
    }
}
