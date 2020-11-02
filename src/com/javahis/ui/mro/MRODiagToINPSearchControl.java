package com.javahis.ui.mro;

import java.sql.Timestamp;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 门急诊患者诊断信息模糊查询报表功能需求
 * </p>
 *
 * <p>
 * Description: 门急诊患者诊断信息模糊查询报表功能需求
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 * com.javahis.ui.mro.MRODiagSearchControl
 * 
 * @author yanglu
 * @version 1.0
 */

public class MRODiagToINPSearchControl extends TControl{
	/**
	 * 显示数据的Table
	 */
	private TTable table;
	public void onInit() {
		super.onInit();
		 initDate();
		table = getTable("tTable_1");
//		this.setValue("START_DATE", "");
//		this.setValue("END_DATE", "");
		this.setValue("ICD_CODE", "");
	}
	/**
	 * 得到TABLE对象
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
    /**
     * 构造方法
     */
    public MRODiagToINPSearchControl() {
        super();
    }
    
    public void onQuery(){
		if ("".equals(this.getValue("START_DATE"))
				|| this.getValue("START_DATE") == null) {
			this.messageBox("开始时间不能为空！");
			return;
		} else if ("".equals(this.getValue("END_DATE"))
				|| this.getValue("END_DATE") == null) {
			this.messageBox("结束时间不能为空！");
			return;
		}
//		if("".equals(this.getValue("ICD_CODE"))){
//			this.messageBox("诊断代码不能为空！");
//			return;
//		}
    	//检查时间选择,开始时间若是比结束时间迟,这样是不合理的.提示并初始化
        int subdate = getSubDate((Timestamp)getValue("START_DATE"), (Timestamp)getValue("END_DATE"));
        if(subdate < 0){
            this.messageBox("开始时间必须小于结束时间，请重新选择!");
            return ;
        }
      	 String startDate  = this.getValue("START_DATE").toString();
         startDate = startDate.substring(0,19);
         
         String endDate  = this.getValue("END_DATE").toString();
         endDate = endDate.substring(0,19);

        TParm param = this.getParmForTag("START_DATE;END_DATE;ICD_CODE");
        String icdCode =  this.getValue("ICD_CODE").toString();
        
        
        
//        String sql= "SELECT inp.DS_DATE,dept.DEPT_CHN_DESC AS DS_DEPT_CODE,inp.MR_NO,pat.PAT_NAME,pat.SEX_CODE AS SEX,adm.ICD_CODE,sys.ICD_CHN_DESC FROM ADM_INP inp ";
//        sql+=" INNER JOIN ADM_INPDIAG adm ON adm.CASE_NO = inp.CASE_NO";
//        sql+=" INNER JOIN SYS_DIAGNOSIS_ORG sys ON adm.ICD_CODE = sys.ICD_CODE";
//        sql+=" INNER JOIN SYS_PATINFO pat ON inp.MR_NO = pat.MR_NO ";
//        sql+=" INNER JOIN SYS_DEPT dept ON inp.DS_DEPT_CODE = dept.DEPT_CODE";
//        sql+=" WHERE adm.ICD_CODE LIKE '"+ icdCode+"%' ";
//        sql+="	AND inp.DS_DATE BETWEEN TO_DATE( '"+startDate+"', 'YYYY-MM-DD HH24:MI:SS' )";
//        sql+=" AND TO_DATE( '"+endDate+"', 'YYYY-MM-DD HH24:MI:SS' ) ";
//        sql+=" ORDER BY MR_NO, inp.DS_DATE";
        
        
        
        String sql = "SELECT\r\n" + 
        		"	adm.DS_DATE,\r\n" + 
        		"	dept.DEPT_CHN_DESC AS DS_DEPT_CODE,\r\n" + 
        		"	mro.MR_NO,\r\n" + 
        		"	pat.PAT_NAME,\r\n" + 
        		"	pat.SEX_CODE AS SEX,\r\n" + 
        		"	mro.ICD_CODE,\r\n" + 
        		"	mro.ICD_DESC AS ICD_CHN_DESC \r\n" + 
        		"FROM\r\n" + 
        		"	MRO_RECORD_DIAG mro\r\n" + 
        		"	LEFT JOIN ADM_INP adm ON adm.CASE_NO = mro.CASE_NO\r\n" + 
        		"	LEFT JOIN SYS_PATINFO pat ON pat.MR_NO = mro.MR_NO\r\n" + 
        		"	LEFT JOIN SYS_DEPT dept ON dept.DEPT_CODE = adm.DS_DEPT_CODE \r\n" + 
        		"WHERE\r\n" + 
        		"	mro.IO_TYPE = 'O' \r\n" + 
        		"	AND	mro.ICD_CODE LIKE '"+this.getValue("ICD_CODE")+"%' \r\n" + 
        		"	AND adm.DS_DATE BETWEEN TO_DATE ( '"+startDate+"', 'YYYY-MM-DD HH24:MI:SS' ) \r\n" + 
        		"	AND TO_DATE ( '"+endDate+"', 'YYYY-MM-DD HH24:MI:SS' ) \r\n" + 
        		"ORDER BY\r\n" + 
        		"	mro.MR_NO,\r\n" + 
        		"	adm.DS_DATE";
        
        
        
        TParm result = new TParm(
                TJDODBTool.getInstance().select(sql)
          );
        if(result.getErrCode() < 0 || result.getCount() <= 0){
            this.messageBox("没有查询到数据!");
            this.err(result.getErrName() + "    " + result.getErrText());
            table.removeRowAll();
            return ;
        }
        for(int i = 0 ;i<result.getCount();i++) {
        	if("1".equals(result.getValue("SEX",i))) {
        		result.setData("SEX",i,"男");
        	}else if("2".equals(result.getValue("SEX",i))) {
        		result.setData("SEX",i,"女");
        	}else {
        		result.setData("SEX",i,"未知");
        	}
        }
        table.setParmValue(result);
    }
    
    
    /**
     * 计算两个时间段的相差天数,这个其实天数必须大于结束天数,否则返回-1
     * @param startDate Timestamp
     * @param endDate Timestamp
     * @return int 返回相差天数
     */
    private int getSubDate(Timestamp startDate, Timestamp endDate){
        //一天的毫秒数
        long date = 24 * 60 * 60 * 1000;
        //用时间差处于一天的毫秒数,就是相差的天数
        return (int)((endDate.getTime() - startDate.getTime()) / date);
    }
    
    

    /**
     * 导出到Xls
     */
    public void onExport(){
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "已使用疾病编码检索-住院");

    }
    
    /**
     * 清空数据
     */
    public void onClear(){
        this.clearValue("START_DATE;END_DATE;ICD_CODE");
        initDate();
        table.removeRowAll();

    }
    
    
    /**
     * 初始化时间,起始日期是上一月的1号,结束日期是当前月的1号
     */
    private void initDate(){
        Timestamp currentDate = SystemTool.getInstance().getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        //初始化结束时间,当前年月日时分秒
        this.setValue("END_DATE", new Timestamp(calendar.getTimeInMillis()));
        int year = calendar.get(Calendar.YEAR);
        //注意: 从calendar中取得的月份是从0开始的,最大是11
        int month = calendar.get(Calendar.MONTH);

        //初始化起始时间,前一个月第一天
        int startyear = year;
        int startmonth = month;
        //初始化起始时间,本月的第一天,如果当天是1日,则起始月应该是上一月.如果是1月1日,则起始月应该是上一年的12月1日
        if(calendar.get(Calendar.DATE) == 1){
            //判断当前月是不是1月,如果是1月,则起始年应该是上一年
            startyear = month == 0 ? (year - 1) : year;
            //如果是1月,则起始月应该是上一年的最后一个月
            startmonth = month == 0 ? 11 : month;
        }
        calendar.set(startyear, startmonth, 1, 0, 0, 0);
        this.setValue("START_DATE",new Timestamp(calendar.getTimeInMillis()));
    }
    

}
