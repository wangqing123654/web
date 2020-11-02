package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.inf.INFPatStaUITool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 	医院感染病例统计表</p>
 *
 * <p>Description:医院感染病例统计表表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.03.07
 * @version 1.0
 */
public class INFPatStaUIControl  extends TControl{
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
		
		TParm result=new TParm();
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			String rsDate = StringTool.getString(TypeTool.getTimestamp(getValue("RS_DATE")), "yyyyMMddHHmmss");
			String reDate = StringTool.getString(TypeTool.getTimestamp(getValue("RE_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
			parm.setData("RS_DATE",rsDate);
			parm.setData("RE_DATE",reDate);
		//科室
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		
		TParm resultTotnum=INFPatStaUITool.getInstance().getselectTotnum(parm);//检测人数
		TParm resultInfnum=INFPatStaUITool.getInstance().getselectInfnum(parm);//感染人数
		TParm resultInpTotnum=INFPatStaUITool.getInstance().getSelectInpTotnum(parm);//住院人数
		TParm resultOutTotnum=INFPatStaUITool.getInstance().getSelectOutTotnum(parm);;//出院人数
		TParm resultInf03num=INFPatStaUITool.getInstance().getselectInf03num(parm);//泌尿道染人数
		TParm resultInf05num=INFPatStaUITool.getInstance().getselectInf05num(parm);//肠胃染人数
		TParm resultInf01num=INFPatStaUITool.getInstance().getselectInf01num(parm);//上呼吸道染人数
		TParm resultInf02num=INFPatStaUITool.getInstance().getselectInf02num(parm);//下呼吸道染人数
		TParm resultInf04num=INFPatStaUITool.getInstance().getselectInf04num(parm);//切口感染人数
		TParm resultInf06num=INFPatStaUITool.getInstance().getselectInf06num(parm);//血液感染
		TParm resultInf13num=INFPatStaUITool.getInstance().getselectInf13num(parm);//其他感染
		TParm resultInfDIE1num=INFPatStaUITool.getInstance().getselectInfDIE1num(parm);//死亡无影响
		TParm resultInfDIE3num=INFPatStaUITool.getInstance().getselectInfD3num(parm);//促进死亡人数
		TParm resultInfDIE4num=INFPatStaUITool.getInstance().getselectInfD4num(parm);//直接死亡人数
		TParm resultInfT5num=INFPatStaUITool.getInstance().getselectT5num(parm);//漏报
		TParm resultInfTnum=INFPatStaUITool.getInstance().getselectTnum(parm);//铜绿
		TParm resultInfJnum=INFPatStaUITool.getInstance().getselectJnum(parm);//金菌
		TParm resultInfMnum=INFPatStaUITool.getInstance().getselectMnum(parm);//霉菌
		TParm resultInfOtherTnum=INFPatStaUITool.getInstance().getselecOtherTnum(parm);//其他
		
		
		//TParm resultI=new TParm();
		//TParm resultII=new TParm();
		//TParm resultW=new TParm();
		if(resultTotnum.getCount()<0){
			this.messageBox("没有查询数据");
			table.removeRowAll();
			return;
		}
		
		if(resultTotnum.getErrCode()<0){
			this.messageBox("查询出现出问题");
			return;
		}
		double infRate=0;//感染率
		DecimalFormat dec=new DecimalFormat("##.##%");
		/**合并---------result----------------**/
		//for(int i=0;i<resultTotnum.getCount();i++){
			result=this.getResult(resultTotnum, resultInfnum, "INF_TONNUM");
			result=this.getResult(result, resultInpTotnum, "INPTOT_NUM");
			result=this.getResult(result, resultOutTotnum, "OUTTOT_NUM");
			result=this.getResult(result, resultInf03num, "INF_03TONNUM");
			result=this.getResult(result, resultInf05num, "INF_05TONNUM");
			result=this.getResult(result, resultInf01num, "INF_01TONNUM");
			result=this.getResult(result, resultInf02num, "INF_02TONNUM");
			result=this.getResult(result, resultInf04num, "INF_04TONNUM");
			result=this.getResult(result, resultInf06num, "INF_06TONNUM");
			result=this.getResult(result, resultInf13num, "INF_13TONNUM");
			result=this.getResult(result, resultInfDIE1num, "INF_DIE1TONNUM");
			result=this.getResult(result, resultInfDIE3num, "INF_DIE3TONNUM");
			result=this.getResult(result, resultInfDIE4num, "INF_DIE4TONNUM");
			result=this.getResult(result, resultInfT5num, "T5_NUM");
			result=this.getResult(result, resultInfTnum, "T1_NUM");
			result=this.getResult(result, resultInfJnum, "T2_NUM");
			result=this.getResult(result, resultInfMnum, "T3_NUM");
			result=this.getResult(result, resultInfOtherTnum, "T4_NUM");
			
		//}
		//System.out.println("resultxxxxxxxxxxxx:::::::::"+result);
		for(int a=0;a<result.getCount();a++){
			if(result.getDouble("TOT_NUM",a)!=0){
				infRate=result.getDouble("INF_TONNUM",a)/result.getDouble("TOT_NUM",a);
				
				
			}
			
			result.addData("INF_RATE", dec.format(infRate));
//			result.addData("T1_NUM", "");
//			result.addData("T2_NUM", "");
//			result.addData("T3_NUM", "");
//			result.addData("T4_NUM", "");
//			result.addData("T5_NUM", "");
		}
		//System.out.println("result:::::::::"+result);
		
		table.setParmValue(result);
	}
	
	
	/**
	 * 合并每一列的查询数据
	 * @param oResult
	 * @param parm
	 * @param type
	 * @return
	 */
	public TParm getResult(TParm resultTotnum ,TParm parm, String type){
		//TParm result=new TParm();
		//this.messageBox("parm:::"+parm.getCount());
		String deptCode="";
		String num="";
		for(int i=0;i<resultTotnum.getCount();i++){
			boolean flag=false;
			deptCode=resultTotnum.getValue("DEPT_CODE",i);
			if(parm.getCount()>0){
				for(int j=0;j<parm.getCount();j++){
					if(deptCode.equals(parm.getValue("DEPT_CODE",j))){
						num=parm.getValue(type,j);
						flag=true;
						break;
					}
				}
			}
		 if(flag){
			 resultTotnum.addData(type, num);
		   }else{
			   
			 resultTotnum.addData(type, 0);
		   }
		}
		return resultTotnum;
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
		this.setValue("RE_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("RS_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "医院感染病例统计表");
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
        //this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
			    
		  
	}
	
	
}