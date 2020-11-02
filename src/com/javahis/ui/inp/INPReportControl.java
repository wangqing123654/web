package com.javahis.ui.inp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
*
* <p>Title: 会诊报告统计</p>
*
* <p>Description: 会诊报告统计</p>
*
* <p>Copyright: Copyright (c) caoyong 20139019</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class INPReportControl extends TControl{
	private TTable table;
	private int sortColumn = -1;
	private boolean ascending = false;
	private Compare compare = new Compare();
    private TParm result = new TParm();
	
	public void setResult(TParm result){
		this.result=result;
		
	}

	public TParm getResult(){
		return result;
	}
	/**
	 * 得到TABLE对象
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	 
	 /**
	  * 初始化数据
	  */
	 public void onInit(){
		 callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
		 this.setValue("REGION_CODE", Operator.getRegion());//初始化区域
		 TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
	        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
	                getValueString("REGION_CODE")));
	     this.initPage();//初始化给查询时间、区域赋值
	        
	    }
	/**
	 * 查询数据
	 */
	public void onQuery(){
		table=this.getTable("TABLE");
		if(table.getRowCount()>0){
			table.removeRowAll();
		}
		 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
         "S_DATE")), "yyyyMMdd");//查询起时间
         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
         "E_DATE")), "yyyyMMdd");//查询迄时间
	 String sql="SELECT A.REGION_CODE,A.CONS_CODE,B.MR_NO AS MR_NO,B.IPD_NO AS IPD_NO, " +
	 		    "C.PAT_NAME AS PAT_NAME,B.IN_DATE AS IN_DATE,A.ACCEPT_DEPT_CODE, " +
	 		    "A.ACCEPT_DR_CODE,A.RECIPIENT_DATE,A.ASSIGN_DR_CODE,A.REPORT_DATE " +
	 		    "FROM INP_CONS A,ADM_INP B,SYS_PATINFO C  " +
	 		    "WHERE " +
	 		    "A.CONS_DATE BETWEEN " +
	 		    "TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS') AND "+
                "TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS') AND " +
                "A.REGION_CODE='"+this.getValueString("REGION_CODE")+"' " ;
	 
	 if(!"".equals(this.getValueString("DEPT_CODE"))&&this.getValueString("DEPT_CODE")!=null){//科室查询
			sql+=" AND A.ACCEPT_DEPT_CODE='"+this.getValueString("DEPT_CODE")+"' ";
		}
	
	  if(!"".equals(this.getValueString("DR_CODE"))&&this.getValueString("DR_CODE")!=null){//医生查询
			sql+=" AND A.ACCEPT_DR_CODE='"+this.getValueString("DR_CODE")+"'";
		}
	        sql+=" AND A.CASE_NO=B.CASE_NO AND B.MR_NO=C.MR_NO ORDER BY A.CASE_NO ";
	      TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	      if(parm.getErrCode()<0){
	    	  this.messageBox("查询出现问题");
	    	  return ;
	      }
	      if(parm.getCount()<=0){
	    	  this.messageBox("没有查询数据");
	    	  return;
	      }/*
	    	 int count=0;
	    	 String regionCode=parm.getValue("REGION_CODE",0);//区域
	    	 String deptCode=parm.getValue("ACCEPT_DEPT_CODE",0);//科室
	    	 String drCode=parm.getValue("ACCEPT_DR_CODE",0);//;医生
	    	 String pathName=parm.getValue("PATH_NAME",0);//患者名称
	    	 String caseNO=parm.getValue("CASE_NO",0);//就诊号
	    	 String indNO=parm.getValue("IND_NO",0);//住院号
	    	 
	    	 for(int i=0;i<parm.getCount("REGION_CODE");i++){//累计病患会诊次数
	    		 if(caseNO.equals(parm.getValue("CASE_NO",i))){
	    			 count++;
	    		 }else{
	    			 this.addResutl(regionCode,deptCode,drCode,indNO,pathName,count);
	    			  count=0;
	    			  caseNO=parm.getValue("CASE_NO",i);
	    			  regionCode=parm.getValue("REGION_CODE",i);
	    	    	  deptCode=parm.getValue("ACCEPT_DEPT_CODE",i);
	    	    	  drCode=parm.getValue("ACCEPT_DR_CODE",i);
	    	    	  indNO=parm.getValue("IND_NO",i);
	    	    	  pathName=parm.getValue("PATH_NAME",i);
	    			  if(caseNO.equals(parm.getValue("CASE_NO",i))){
	    			     count ++;
	    			  }
	    		    }
	    		 if(i==parm.getCount("REGION_CODE")-1){
	    			 this.addResutl(regionCode,deptCode,drCode,indNO,pathName,count); 
		    	  }
	    	     }
	    	 //统计总计
	    	      result.addData("REGION_CODE", "统计总计");
	 		      result.addData("DEPT_CODE", "");
	 		      result.addData("DR_CODE", "");
	 		      result.addData("IND_NO", "");
	 		      result.addData("PATH_NAME", "");
 		          result.addData("SUMNUMBER", parm.getCount());
	              table=this.getTable("TABLE");*/
	              table.setParmValue(parm);
	      
	}
	/**
	 * 单击事件
	 */
	public void onTABLEClicked(int row){
		 TParm tparm = table.getParmValue().getRow(row);
		 this.setValue("REGION_CODE",tparm.getValue("REGION_CODE"));
		 this.setValue("DEPT_CODE",tparm.getValue("DEPT_CODE"));
		 this.setValue("DR_CODE",tparm.getValue("DR_CODE"));
	}
	/**
	 * 统计会诊次数
	 * @param regionCode
	 * @param deptCode
	 * @param drCode
	 * @param caseNO
	 * @param pathName
	 * @param count
	 */
	public void addResutl(String regionCode,String deptCode, String drCode,String indNo,String pathName, int count ){
		result.addData("REGION_CODE", regionCode);
		result.addData("DEPT_CODE", deptCode);
		result.addData("DR_CODE", drCode);
		result.addData("IND_NO", indNo);
		result.addData("PATH_NAME", pathName);
		result.addData("SUMNUMBER", count);
	}
	/**
	 * 清空
	 */
	public void onClear() {
		String clearString="REGION_CODE;DEPT_CODE;DR_CODE";
		clearValue(clearString);
		this.onInit();
		table.removeRowAll();
	   
		
	}
	/**
	 * 打印
	 */
	public void onPrint() {
		
		if(table.getRowCount()<=0){
			this.messageBox("没有打印数据");
			return ;
		}
		Timestamp today = SystemTool.getInstance().getDate();
			TParm parm = new TParm();
			//TParm tableParm = table.getParmValue();
			TParm  tableParm = table.getShowParmValue();
			// 打印数据
			/* String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "S_DATE")), "yyyyMMdd");
	         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "E_DATE")), "yyyyMMdd");*/
			TParm date = new TParm();
			date.setData("title", "TEXT", "会诊报告单");
			date.setData("user","TEXT",Operator.getName());
			if(!"".equals(this.getValueString("DEPT_CODE"))){
            date.setData("Unit","TEXT","科室："+tableParm.getValue("ACCEPT_DEPT_CODE",0));
			}
            if(!"".equals(this.getValueString("DR_CODE"))){
            date.setData("Doctor","TEXT","医生："+tableParm.getValue("ACCEPT_DR_CODE",0));		
            }
            date.setData("date","TEXT",this.getValueString("S_DATE").substring(0,10).replace("-","/")+"--"
            		    +this.getValueString("E_DATE").substring(0,10).replace("-","/"));			
            //REGION_CODE;CONS_CODE;MR_NO;IPD_NO;PAT_NAME;IN_DATE;ACCEPT_DEPT_CODE;ACCEPT_DR_CODE;RECIPIENT_DATE;ASSIGN_DR_CODE;REPORT_DATE
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("REGION_CODE", tableParm.getValue("REGION_CODE", i));
				parm.addData("CONS_CODE", tableParm.getValue("CONS_CODE", i));
				parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("IPD_NO", tableParm.getValue("IPD_NO", i));
				parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
				parm.addData("IN_DATE", tableParm.getValue("IN_DATE",i));
				parm.addData("ACCEPT_DEPT_CODE", tableParm.getValue("ACCEPT_DEPT_CODE",i));
				parm.addData("ACCEPT_DR_CODE", tableParm.getValue("ACCEPT_DR_CODE",i));
				parm.addData("RECIPIENT_DATE", tableParm.getValue("RECIPIENT_DATE",i));
				parm.addData("ASSIGN_DR_CODE", tableParm.getValue("ASSIGN_DR_CODE",i));
				parm.addData("REPORT_DATE", tableParm.getValue("REPORT_DATE",i));

			}
			
				parm.setCount(parm.getCount("MR_NO"));
				parm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
				parm.addData("SYSTEM", "COLUMNS", "CONS_CODE");
				parm.addData("SYSTEM", "COLUMNS", "MR_NO");
				parm.addData("SYSTEM", "COLUMNS", "IPD_NO");
				parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
				parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
				parm.addData("SYSTEM", "COLUMNS", "ACCEPT_DEPT_CODE");
				parm.addData("SYSTEM", "COLUMNS", "ACCEPT_DR_CODE");
				parm.addData("SYSTEM", "COLUMNS", "RECIPIENT_DATE");
				parm.addData("SYSTEM", "COLUMNS", "ASSIGN_DR_CODE");
				parm.addData("SYSTEM", "COLUMNS", "REPORT_DATE");
				date.setData("TABLE", parm.getData());
				this.openPrintWindow("%ROOT%\\config\\prt\\INP\\INPReport.jhw", date);
			
	}
	/**
	 *初始化
	 */
	private void initPage() {

		table = getTable("TABLE");
		//给查询时间段赋值
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
         Timestamp today = SystemTool.getInstance().getDate();
         setValue("S_DATE", yesterday);
         setValue("E_DATE", today);
//      // 为表单添加监听器，为排序做准备。
// 		addListener(table);
       /* Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("E_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("REGION_CODE",Operator.getRegion());*/
	}
	
	/**
     * 汇出Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "会诊报告统计表");
    }
    
    /**
	 * 得到 Vector 值
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * 转换parm中的列
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}
		return index;
	}
	/**
	 * vectory转成param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	    //================start===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
    	//================end=================
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		//================start===============
		//table.setParmValue(parmTable);
		return parmTable;
		//================end=================
		// System.out.println("排序后===="+parmTable);
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
//				TParm tableData = CLP_BSCINFO.getParmValue();
				TParm tableData = table.getShowParmValue();
				//=====标题行 和 "总计"行  不参与处理处理======
//				TParm titRowParm=new TParm();//记录标题行
//				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm=new TParm();//记录“总计”行
				totRowParm.addRowData(table.getParmValue(), tableData.getCount()-1);
				int rowCount=tableData.getCount();//数据的总行数（包括小计行和总计行）
//				tableData.removeRow(0);//去除第一行（标题行）
				tableData.removeRow(tableData.getCount()-1);//去除最后一行(总计行)
				//=========================================
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm=new TParm();//记录最终结果
				TParm tmpParm=cloneVectoryParam(vct, new TParm(), strNames);
//				lastResultParm.addRowData(titRowParm, 0);//加入标题行
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);//加入中间数据
				}
				lastResultParm.addRowData(totRowParm, 0);//加入总计行
				lastResultParm.setCount(rowCount);
//				System.out.println("lastResultParm:\r\n"+lastResultParm+"\r\n\r\n");////////////////////
				table.setParmValue(lastResultParm);
				//getTMenuItem("save").setEnabled(false);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

}


