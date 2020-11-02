/**
 * 
 */
package com.javahis.ui.pha;

import java.sql.Timestamp;
import java.util.Date;

import jdo.pha.PHADeptPrescriptionChartTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 科室处方量统计
 * </p>
 * 
 * <p>
 * Description: 药房处方量统计报表
 * </p>
 * 
 * <p>
 * Copyright: javahis 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author guangl 2016.05.23
 * @version 1.0
 */
public class PHADeptPrescriptionChartControl extends TControl {

	TParm inParm = new TParm();
	
	// 主表
	private TTable table;

	// 部门号
	private String dept_code;

	// 医生
	private String dr_code;

	// 药型（中成药PHA_C，西药PHA_W）
	private String pha_type;

	// 起止时间
	private TTextFormat s_date;
	private TTextFormat e_date;

	// 附加查询条件
	TCheckBox tcbDose;
	TCheckBox tcbHexp;
	TCheckBox tcb;// 是否含激素处方复选框，暂留
	private boolean dose_flag;
	private boolean hexp_flag;
	private boolean _flag;// 是否含激素处方，药品暂时没有判断激素字段，暂留

	@Override
	public void onInit() {
		// TODO Auto-generated method stub
		super.onInit();
		table = (TTable) getComponent("Table");
		((TComboBox) this.getComponent("REGION_CODE")).setValue(Operator
				.getRegion());

		this.setValue("S_DATE", TJDODBTool.getInstance().getDBTime());
		this.setValue("E_DATE", TJDODBTool.getInstance().getDBTime());

		tcbDose = (TCheckBox) this.getComponent("DOSE_FLAG");
		tcbHexp = (TCheckBox) this.getComponent("HEXP_FLAG");
		tcb = (TCheckBox) this.getComponent("_FLAG");

	}

	/*
	 * 
	 */
	public void onQuery() {
		
		s_date = (TTextFormat) this.getComponent("S_DATE");
		e_date = (TTextFormat) this.getComponent("E_DATE");
		
		TParm tableData = new TParm();
		TParm result = getQueryData();
		if (result.getCount() <= 0) {
            this.messageBox("该查询条件无数据！");
            onClear();
            return;
        }
		
//		// 表格信息
//		if(table.getParmValue() != null){
//			tableData = table.getParmValue();
//			tableData.addParm(result);
//		}else{
//			tableData = result;
//		}
		
		table.setParmValue(result);
//		table.addRow(tableData);
	}

	/*
	 * 
	 */
	public TParm getQueryData() {
		checkQuery();
		// 传参
		

		// 结果
		TParm result = new TParm();

		// 获得统计时间
		Timestamp startDate = (Timestamp) s_date.getValue();
		String tempEnd = e_date.getValue().toString();
		Timestamp endDate = StringTool
				.getTimestamp(tempEnd.substring(0, 4) + tempEnd.substring(5, 7)
						+ tempEnd.substring(8, 10) + "235959", "yyyyMMddHHmmss");

		// 查询参数设置——区域号、起止时间
		if (this.getValueString("REGION_CODE").length() > 0)
			inParm = this.getParmForTag("REGION_CODE");
		if (startDate != null)
			inParm.setData("START_DATE", startDate);
		if (endDate != null)
			inParm.setData("END_DATE", endDate);

		// 查询参数设置——部门号、医生号、药型
		// 在checkQuery()方法中已经获得
		inParm.setData("DEPT_CODE", dept_code);
		inParm.setData("DR_CODE", dr_code);
		inParm.setData("PHA_TYPE", pha_type);

		// 查询参数设置——是否含注射剂、是否含抗菌素、是否含激素
		inParm.setData("DOSE_FLAG", dose_flag);
		inParm.setData("HEXP_FLAG", hexp_flag);
		inParm.setData("_FLAG", _flag);

		result = PHADeptPrescriptionChartTool.getInstance()
				.getQueryData(inParm);
		
		
		return result;
	}

	/*
	 * 检查查询数据是否完备
	 */
	public boolean checkQuery() {
		// 获取部门号、医生号、药型
		dept_code = getValueString("DEPT_CODE");
		dr_code = getValueString("DR_CODE");
		pha_type = getValueString("PHA_TYPE");
		
		dose_flag = false;
		hexp_flag = false;
		_flag = false;
		
		// 获取附加查询条件标记
		if (tcbDose.isSelected()) {
			dose_flag = true;
		}
		if (tcbHexp.isSelected()) {
			hexp_flag = true;
		}
		if (tcb.isSelected()) {
			_flag = true;
		}

		// 部门号和医生号必须都填才能查询，药型不填默认中药西药都统计
		if ("".equals(dept_code) || "".equals(dr_code)) {
			return false;
		}
		return true;
	}

	/*
	 * 清空条件
	 */
	public void onClear() {
		this.setValue("S_DATE", TJDODBTool.getInstance().getDBTime());
		this.setValue("E_DATE", TJDODBTool.getInstance().getDBTime());
		this.clearValue("DEPT_CODE;DR_CODE;PHA_TYPE");
		tcb.setSelected(false);
		tcbDose.setSelected(false);
		tcbHexp.setSelected(false);
		table.removeRowAll();
	}
	
	public void onDelete(){
		int row = table.getSelectedRow();
		if(row == -1){
			messageBox("未选中行！");
		}else{
			table.removeRow(row);
		}
	}

	/*
	 * 打印报表
	 */
	public void onPrint() {
		if(table.getRowCount() <= 0){
			messageBox("没有要打印的内容！");
			return;
		}
		
		//打印用数据集合
		TParm printData =  new TParm();
		//表格信息容器
		TParm parm = new TParm();
		//表格信息集合
		//TParm tableParm = table.getParmValue();
		TParm tableParm = table.getShowParmValue();
		//填充打印用数据集合——表头信息
		printData.setData("TITLE", "TEXT", "处方量统计报表");
		printData.setData("Date", "TEXT", "统计时间：" 
				+ getValue("S_DATE").toString().substring(0,10) + " " + getValue("S_TIME").toString()+ " 至 " 
				+ getValue("E_DATE").toString().substring(0,10) + " " + getValue("E_TIME").toString());
		Timestamp endDate = StringTool
		.getTimestamp(new Date());
		printData.setData("Time", "TEXT", "制表时间：" + endDate.toString().substring(0,19));
		
		String info = "";
		info = "区域：北京爱育华妇儿医院" + "    科室：门急诊药房      统计类型："; 
		if("".equals(inParm.getValue("PHA_TYPE"))){
			info+="中成药,西药";
		}else if("PHA_W".equals(inParm.getValue("PHA_TYPE"))){
			info+="西药";
		}else{
			info+="中成药";
		}
		if(inParm.getBoolean("DOSE_FLAG")){
			info+=",含注射剂处方";
		}
		if(inParm.getBoolean("HEXP_FLAG")){
			info+=",含抗菌素处方";
		}
		if(inParm.getBoolean("_FLAG")){
			info+=",含激素处方";
		}
		printData.setData("Infomation", "TEXT", info);
		
		
		int num1 = 0 , num2 = 0 , num3 = 0 , num4 = 0 , num5 = 0 , num6 = 0;
		double amount1 = 0.00 , amount2 = 0.00 , amount3 = 0.00 , amount4 = 0.00 ,amount5 = 0.00 , amount6 = 0.00;
		
		//填充表格容器
		for (int i = 0; i < tableParm.getCount(); i++) {
			parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE", i));
			parm.addData("DR_CODE", tableParm.getValue("DR_CODE", i));
			parm.addData("PHA_W_NUM", tableParm.getValue("PHA_W_NUM", i));
			parm.addData("PHA_W_AMOUNT", tableParm.getValue("PHA_W_AMOUNT", i));
			parm.addData("PHA_C_NUM", tableParm.getValue("PHA_C_NUM", i));
			parm.addData("PHA_C_AMOUNT", tableParm.getValue("PHA_C_AMOUNT", i));
			parm.addData("DOSE_NUM", tableParm.getValue("DOSE_NUM", i));
			parm.addData("DOSE_AMOUNT", tableParm.getValue("DOSE_AMOUNT", i));
			parm.addData("HEXP_NUM", tableParm.getValue("HEXP_NUM", i));
			parm.addData("HEXP_AMOUNT", tableParm.getValue("HEXP_AMOUNT", i));
			parm.addData("HORM_NUM", tableParm.getValue("HORM_NUM", i));
			parm.addData("HORM_AMOUNT", tableParm.getValue("HORM_AMOUNT", i));
			
			parm.addData("TOTAL_NUM", tableParm.getInt("PHA_W_NUM",i) 
					+ tableParm.getInt("PHA_C_NUM", i) 
					+ tableParm.getInt("DOSE_NUM", i) 
					+ tableParm.getInt("HEXP_NUM", i));
			parm.addData("TOTAL_AMOUNT", String.format("%.2f", tableParm.getDouble("PHA_W_AMOUNT",i) 
					+ tableParm.getDouble("PHA_C_AMOUNT", i) 
					+ tableParm.getDouble("DOSE_AMOUNT", i) 
					+ tableParm.getDouble("HEXP_AMOUNT", i)) );
			
			num1 += Integer.valueOf(tableParm.getValue("PHA_W_NUM" , i));
			num2 += Integer.valueOf(tableParm.getValue("PHA_C_NUM" , i));
			num3 += Integer.valueOf(tableParm.getValue("DOSE_NUM" , i));
			num4 += Integer.valueOf(tableParm.getValue("HEXP_NUM" , i));
			num5 = num1 + num2 + num3 + num4;
			
			amount1 += Double.valueOf(tableParm.getValue("PHA_W_AMOUNT" , i));
			amount2 += Double.valueOf(tableParm.getValue("PHA_C_AMOUNT" , i));
			amount3 += Double.valueOf(tableParm.getValue("DOSE_AMOUNT" , i));
			amount4 += Double.valueOf(tableParm.getValue("HEXP_AMOUNT" , i));
			amount5 = amount1 + amount2 + amount3 + amount4;
		}
		
		parm.addData("DEPT_CODE", null);
		parm.addData("DR_CODE", "总计");
		parm.addData("PHA_W_NUM", String.format("%d", num1));
		parm.addData("PHA_W_AMOUNT", String.format("%.2f", amount1));
		parm.addData("PHA_C_NUM", String.format("%d", num2));
		parm.addData("PHA_C_AMOUNT",String.format("%.2f", amount2));
		parm.addData("DOSE_NUM", String.format("%d", num3));
		parm.addData("DOSE_AMOUNT", String.format("%.2f", amount3));
		parm.addData("HEXP_NUM", String.format("%d", num4));
		parm.addData("HEXP_AMOUNT", String.format("%.2f", amount4));
		parm.addData("HORM_NUM", String.format("%d", num6));
		parm.addData("HORM_AMOUNT", String.format("%.2f", amount6));
		parm.addData("TOTAL_NUM", String.format("%d", num5));
		parm.addData("TOTAL_AMOUNT", String.format("%.2f", amount5));
		//填充容器的字典信息
		parm.setCount(parm.getCount("DEPT_CODE"));
		parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		parm.addData("SYSTEM", "COLUMNS", "DR_CODE");
		parm.addData("SYSTEM", "COLUMNS", "PHA_W_NUM");
		parm.addData("SYSTEM", "COLUMNS", "PHA_W_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "PHA_C_NUM");
		parm.addData("SYSTEM", "COLUMNS", "PHA_C_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "DOSE_NUM");
		parm.addData("SYSTEM", "COLUMNS", "DOSE_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "HEXP_NUM");
		parm.addData("SYSTEM", "COLUMNS", "HEXP_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "HORM_NUM");
		parm.addData("SYSTEM", "COLUMNS", "HORM_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "TOTAL_NUM");
		parm.addData("SYSTEM", "COLUMNS", "TOTAL_AMOUNT");
		
		//填充打印用数据集合——表格信息
		printData.setData("TABLE", parm.getData());
		//填充打印用数据集合——表尾信息
		//printData.setData("TAIL", "TEXT", "制表单位：门急诊药房");
		printData.setData("TAIL", "TEXT", "制表人：" + Operator.getName());
		
		//this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHADeptPrescriptionChart.jhw", printData);
		this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHADeptPrescription.jhw", printData);
	}

	/*
	 * 导出Excel
	 */
	public void onExcel() {
		 ExportExcelUtil.getInstance().exportExcel(table, "科室处方量统计");
	}
	
	

}
