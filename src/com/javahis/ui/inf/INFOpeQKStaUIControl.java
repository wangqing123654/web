package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inf.INFOpeQK2StaUITool;
import jdo.inf.INFOpeQKStaUITool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 抗菌药品手术预防药品使用报表</p>
 *
 * <p>Description:抗菌药品手术预防药品使用报表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.02.12
 * @version 1.0
 */
public class INFOpeQKStaUIControl  extends TControl{
	private TTable table;
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
  /**
   * 初始化
   */
	public void onInit() {
		callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");
		table=this.getTable("TABLE");
		this.onClear();
		 initPage();
		
   }
	 /**
	 * 查询
	 */
	public void onQuery() {
		TParm parm=new TParm();
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			String opesDate = StringTool.getString(TypeTool.getTimestamp(getValue("OPES_DATE")), "yyyyMMddHHmmss");
			String opeeDate = StringTool.getString(TypeTool.getTimestamp(getValue("OPEE_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
			parm.setData("OPES_DATE",opesDate);
			parm.setData("OPEE_DATE",opeeDate);
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
			parm.setData("OPE_CODE",this.getValueString("OPE_CODE"));
			parm.setData("HEALTH_LEVEL",this.getValueString("HEALTH_LEVEL"));
	
		
		TParm result=INFOpeQKStaUITool.getInstance().getselectInum(parm);
		
		
		
		if(result.getCount()<0){
			this.messageBox("没有查询数据");
			table.removeRowAll();
			return;
		}
		
		if(result.getErrCode()<0){
			this.messageBox("查询出现出问题");
			return;
		}
		table.setParmValue(result);
	}
	/**
	 * 初始化
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1天
		// 初始化查询区间
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
		this.setValue("OPEE_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("OPES_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "医院手术切口感染监测记录(1)");
    }
    
    /**
	 * 清空内容
	 */
	public void onClear() {
		String clearString = "S_DATE;E_DATE;DEPT_CODE;VS_DR_CODE";
		table.removeRowAll();
		clearValue(clearString);
		initPage();
	}
	/**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			return;
		}
		TParm tparm= new TParm();
        tparm = table.getParmValue().getRow(row);
        this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
        this.setValue("SESSION_CODE", tparm.getValue("STATION_CODE"));
        this.setValue("VS_DR_CODE", tparm.getValue("VS_DR_CODE"));
			    
		  
	}
	
	
	
}