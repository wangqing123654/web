package com.javahis.ui.opb;

import java.text.DecimalFormat;

import jdo.sys.IReportTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 通过勾选打印门诊套餐收据
 * </p>
 * 
 * <p>
 * Description: 通过勾选打印门诊套餐收据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author sunqy 20140714
 * @version 1.0
 */

public class OPBPackageChooseControl extends TControl {
	
	TParm packageParm = null;//套餐
	TParm printParm = new TParm();
	TParm parm = null;
	double sumTotS = 0.00;// 合计应收金额
	double sumTotR = 0.00;// 合计实收金额
	double sumPackage = 0.00;//套餐价格
	TCheckBox inside ;//套餐内
	TCheckBox outside;//套餐外

	
	public void onInit(){
		super.onInit();
//		System.out.println("========come in=======");
		inside = (TCheckBox)this.getComponent("INCLUDE_FLG");//套餐内
		outside = (TCheckBox)this.getComponent("OUTSIDE_FLG");//套餐外
		packageParm = (TParm)this.getParameter();
//		System.out.println("packageParm============="+packageParm);
		parm = (TParm)packageParm.getData("RECEIVE_PARM");
//		System.out.println("parm============="+parm);
		sumTotS = parm.getDouble("SUMTOTS");
		sumTotR = parm.getDouble("SUMTOTR");
		sumPackage = packageParm.getDouble("SUM_PACKAGE");
	}
	
	public void onSelect(){
		boolean engFlag = packageParm.getBoolean("engFlag");
		
		/**
		 * 英文打印
		 */
		/*英文打印  add by lich ----------start */
		if(engFlag){
			
			TParm p = new TParm();//用于置空不勾选的表格
			TParm q = new TParm();//用于计算最终合计
			DecimalFormat df = new DecimalFormat("##########0.00");
//		System.out.println("packageParm.getData=--=-=-=-=-=-=-=-"+packageParm.getData());
			if(inside.isSelected() && outside.isSelected()){//套餐内外都选中
//				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
//					printParm.setData("TITLE", "TEXT", "门诊套餐费用结算清单");
//				}else{
//					printParm.setData("TITLE", "TEXT", "门诊套餐费用明细清单");
//				}
				printParm.setData("TABLEPACKAGE", packageParm.getData());//套餐表格
				printParm.setData("TABLEORDER", parm.getData());//门诊费用表格
				printParm.setData("OTHERPAY", "TEXT", "其他消费");//其他消费
				q.addData("TOT", "合计：");
				q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)+sumPackage));// 合计应收金额
				q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)+sumPackage));// 合计实收金额
				q.setCount(1);
				q.addData("SYSTEM", "COLUMNS", "TOT");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
				printParm.setData("TABLESUM", q.getData());// 合计应收金额
			}
			if(inside.isSelected() && !outside.isSelected()){//只选中套餐内
//				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
//					printParm.setData("TITLE", "TEXT", "门诊套餐费用结算清单");
//				}else{
//					printParm.setData("TITLE", "TEXT", "门诊套餐费用明细清单");
//				}
				p.setData("Visible", false);
				printParm.setData("TABLEPACKAGE", packageParm.getData());//套餐表格
				printParm.setData("TABLEORDER", p.getData());//门诊费用表格
				printParm.setData("OTHERPAY", "TEXT", "");//其他消费
				printParm.setData("TABLESUM", p.getData());//最终总计
			}
			if(!inside.isSelected() && outside.isSelected()){//只选中套餐外
//				if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
//					printParm.setData("TITLE", "TEXT", "门诊费用结算清单");
//				}else{
//					printParm.setData("TITLE", "TEXT", "门诊费用明细清单");
//				}
				p.setData("Visible", false);
				printParm.setData("TABLEPACKAGE", p.getData());//套餐表格
				printParm.setData("TABLEORDER", parm.getData());//门诊费用表格
				printParm.setData("OTHERPAY", "TEXT", "");//其他消费
				q.addData("TOT", "合计：");
				q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
				q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
				q.setCount(1);
				q.addData("SYSTEM", "COLUMNS", "TOT");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
				q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
				printParm.setData("TABLESUM", q.getData());// 合计应收金额
			}
			if(!inside.isSelected() && !outside.isSelected()){//都没有选中
				this.messageBox("没有选中套餐选项!");
				return;
			}
			
			
		
			printParm.setData("HOSP","TEXT",packageParm.getData("HOSP"));//医院名称
			printParm.setData("MR_NO", "TEXT", packageParm.getData("MR_NO"));//病案号
			printParm.setData("PAT_NAME", "TEXT", packageParm.getData("PAT_NAME"));//姓名
			printParm.setData("InsCom","TEXT", packageParm.getData("InsCom"));//保险公司名称
			printParm.setData("InsNum","TEXT", packageParm.getData("InsNum"));//保险号
			printParm.setData("PRINT_NO", "TEXT", packageParm.getData("PRINT_NO"));//收据号
			printParm.setData("DATE", "TEXT", packageParm.getData("BILL_DATE"));//收费日期
			printParm.setData("NOW", "TEXT", packageParm.getData("DATE"));//打印日期
			printParm.setData("OP_NAME", "TEXT", packageParm.getData("OP_NAME"));//经办人
			printParm.setData("TOT_AMT1","TEXT" ,packageParm.getData("TOT_AMT1"));//总金额
			printParm.setData("Birthday","TEXT" ,packageParm.getData("Birthday"));//生日
			printParm.setData("TOT_AMT",df.format(StringTool.round(sumTotS,2)));//合计应收
			printParm.setData("OWN_AMT", df.format(StringTool.round(sumTotR,2)));//合计实收
			printParm.setData("SUM_", df.format(sumPackage));
			//20141223 wangjingchun add start 854
			String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+packageParm.getData("MR_NO")+"'";
			TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
			int i = 1;
			if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
				i=2;
			}
			for(int j=0;j<i;j++){
				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackEnV45.jhw"),
						IReportTool.getInstance().getReportParm("OPDFeeListV45.class", printParm));//报表合并  modify by sunqy 20140630
			}
			//20141223 wangjingchun add end 854
			/*英文打印  add by lich ----------end*/
		
			
		/**
		 * 中文打印
		 */
		}else{
				
				TParm p = new TParm();//用于置空不勾选的表格
				TParm q = new TParm();//用于计算最终合计
				DecimalFormat df = new DecimalFormat("##########0.00");
//			System.out.println("packageParm.getData=--=-=-=-=-=-=-=-"+packageParm.getData());
				if(inside.isSelected() && outside.isSelected()){//套餐内外都选中
					if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
						printParm.setData("TITLE", "TEXT", "门诊套餐费用结算清单");
					}else{
						printParm.setData("TITLE", "TEXT", "门诊套餐费用明细清单");
					}
					printParm.setData("TABLEPACKAGE", packageParm.getData());//套餐表格
					printParm.setData("TABLEORDER", parm.getData());//门诊费用表格
					printParm.setData("OTHERPAY", "TEXT", "其他消费");//其他消费
					q.addData("TOT", "合计：");
					q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
					q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
					q.setCount(1);
					q.addData("SYSTEM", "COLUMNS", "TOT");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
					printParm.setData("TABLESUM", q.getData());// 合计应收金额
				}
				if(inside.isSelected() && !outside.isSelected()){//只选中套餐内
					if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
						printParm.setData("TITLE", "TEXT", "门诊套餐费用结算清单");
					}else{
						printParm.setData("TITLE", "TEXT", "门诊套餐费用明细清单");
					}
					p.setData("Visible", false);
					printParm.setData("TABLEPACKAGE", packageParm.getData());//套餐表格
					printParm.setData("TABLEORDER", p.getData());//门诊费用表格
					printParm.setData("OTHERPAY", "TEXT", "");//其他消费
					printParm.setData("TABLESUM", p.getData());//最终总计
				}
				if(!inside.isSelected() && outside.isSelected()){//只选中套餐外
					if("Y".equals(packageParm.getValue("CLEAR_FLG"))){
						printParm.setData("TITLE", "TEXT", "门诊费用结算清单");
					}else{
						printParm.setData("TITLE", "TEXT", "门诊费用明细清单");
					}
					p.setData("Visible", false);
					printParm.setData("TABLEPACKAGE", p.getData());//套餐表格
					printParm.setData("TABLEORDER", parm.getData());//门诊费用表格
					printParm.setData("OTHERPAY", "TEXT", "");//其他消费
					q.addData("TOT", "合计：");
					q.addData("SUMTOTS", df.format(StringTool.round(sumTotS,2)));// 合计应收金额
					q.addData("SUMTOTR", df.format(StringTool.round(sumTotR,2)));// 合计实收金额
					q.setCount(1);
					q.addData("SYSTEM", "COLUMNS", "TOT");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTS");
					q.addData("SYSTEM", "COLUMNS", "SUMTOTR");
					printParm.setData("TABLESUM", q.getData());// 合计应收金额
				}
				if(!inside.isSelected() && !outside.isSelected()){//都没有选中
					this.messageBox("没有选中套餐选项!");
					return;
				}
				printParm.setData("DEPT", "TEXT", packageParm.getData("DEPT"));// 科室  add by huangtt 20150925
				printParm.setData("MR_NO", "TEXT", packageParm.getData("MR_NO"));//病案号
				printParm.setData("PAT_NAME", "TEXT", packageParm.getData("PAT_NAME"));//姓名
				printParm.setData("PRINT_NO", "TEXT", packageParm.getData("PRINT_NO"));//收据号
				printParm.setData("BILL_DATE", "TEXT", packageParm.getData("BILL_DATE"));//收费日期
				printParm.setData("DATE", "TEXT", packageParm.getData("DATE"));//打印日期
				printParm.setData("OP_NAME", "TEXT", packageParm.getData("OP_NAME"));//经办人
				printParm.setData("SUM_", df.format(sumPackage));
				//20141223 wangjingchun add start 854
				String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+packageParm.getData("MR_NO")+"'";
				TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
				int i1 = 1;
				if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
					i1=2;
				}
				for(int j=0;j<i1;j++){
					this.openPrintDialog(IReportTool.getInstance().getReportPath("OPDFeeListPackV45.jhw"),
							IReportTool.getInstance().getReportParm("OPDFeeListV45.class", printParm));//报表合并  modify by sunqy 20140630
				}
				//20141223 wangjingchun add end 854
			}
		}
	
	
}
