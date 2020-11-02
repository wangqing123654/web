package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.bil.BILInCostTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>Title:住院病人费用表</p>
 *
 * <p>Description:住院病人费用表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.04.22
 * @version 1.0
 */
public class BILInCostControl extends TControl 	{
	
	private TTable table;
	private List<String> list=new ArrayList<String>();
	private TParm mresult=new TParm();
	private String bilcode[]={"020","021","022","022.01","022.02","023","024","025","026","027","028","029","02A","02B","02C","032","035","2E0","2J0"};
	private String bilDesc[]={"床位费","诊察费","西药费","抗菌药物","非抗菌药物","中成药费","中草药费","检查费","治疗费","放射费","手术费","化验费","输血费","输氧费","接生费","CT费","材料费","西药费","麻醉费"};
	
   private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
/**
   * 初始化
   */
	public void onInit() {
		    table=this.getTable("TABLE");
		    Timestamp date = StringTool.getTimestamp(new Date());
			this.setValue("IN_DATE", date.toString().substring(0, 10).replace('-','/'));
		
   }
	//查询
	public void onQuery() {
		   list=new ArrayList<String>();
		   mresult=new TParm();
		   table.removeRowAll();
		  TParm newTparm=new TParm();
		  DecimalFormat df = new DecimalFormat("#.00");
		  TParm parm=new TParm();
			String indate = StringTool.getString(TypeTool.getTimestamp(getValue("IN_DATE")), "yyyyMMdd");
			parm.setData("INS_DATE",indate+" 000000");
			parm.setData("INE_DATE",indate+" 235959");
		    //--start 上次未结算总计
			TParm result1=BILInCostTool.getInstance().getSelectSordd(parm);//上次未结算总计 金额
			TParm result2=BILInCostTool.getInstance().getSelectSrecp(parm);//上次未结算未作废的金额
			TParm result3=this.getTparm(result1, bilcode,bilDesc);//过滤只要数组中的项目
			TParm result4=this.getTparm(result2, bilcode,bilDesc);//过滤只要数组中的项目
			TParm newTParm1=this.getDparm(result3, result4, "STOT_AMT", "SW_AMT","S_TOTAMT");//上次未结算总计 金额-上次未结算未作废的金额
			//--- end 上次未结算总计
			
			//--start 日应收总计
			TParm dresult1=BILInCostTool.getInstance().getSelectDSordd(parm);//日应收总计
			TParm dresult2=BILInCostTool.getInstance().getSelectDSrecp(parm);//日应收未作废的金额
			TParm dresult3=this.getTparm(dresult1, bilcode,bilDesc);//过滤只要数组中的项目
			TParm dresult4=this.getTparm(dresult2, bilcode,bilDesc);//过滤只要数组中的项目
			TParm newTParm2=this.getDparm(dresult3, dresult4, "STOT_AMT", "SW_AMT","DR_TOTAMT");//日应收总计-日应收未作废的金额
//				System.out.println("newTParm2:::::"+newTParm2);
			//--start end 
			
			//--start 日出院费用总计
			TParm fresult1=BILInCostTool.getInstance().getSelectFSordd(parm);//日出院费用总计
			//TParm fresult2=BILInCostTool.getInstance().getSelectDFrecp(parm);//日出院未作废的金额
			TParm fresult3=this.getTparm(fresult1, bilcode,bilDesc);//过滤只要数组中的项目
			//TParm fresult4=this.getTparm(fresult2, bilcode,bilDesc);//过滤只要数组中的项目
			TParm fresult4= new TParm();
			TParm newTParm3=this.getDparm(fresult3, fresult4, "STOT_AMT", "SW_AMT","DC_TOTAMT");//日出院费用总计-日出院未作废的金额
			//--end  日出院费用总计
//				System.out.println("newTParm3:::::"+newTParm3);
			//把每个不同的所有的项目放到一个list 里
			this.getlist(newTParm1);
			this.getlist(newTParm2);
			this.getlist(newTParm3);
			
			
			//计算以后的parm 整合在一起
			this.getnewTparm(newTParm1,"S_TOTAMT");
			this.getnewTparm(newTParm2,"DR_TOTAMT");
			newTparm=this.getnewTparm(newTParm3,"DC_TOTAMT");
			
			if(newTparm.getCount("CHARGE_HOSP_DESC")<=0){
				this.messageBox("没有查询数据");
				return;
			}
	   //当前未结算总计	
		double dqtotamt=0;
		for(int i=0;i<newTparm.getCount("CHARGE_HOSP_DESC");i++){
			dqtotamt=newTparm.getDouble("S_TOTAMT", i)+newTparm.getDouble("DR_TOTAMT", i)-newTparm.getDouble("DC_TOTAMT", i);
			newTparm.addData("DQ_TOTAMT", df.format(dqtotamt));
		}
		
		//-----start 总计
		    double totSamt=0;		
		    double totDamt=0;		
		    double totEamt=0;		
		    double totFamt=0;	
		for(int i=0;i<newTparm.getCount("CHARGE_HOSP_DESC");i++){
			totSamt+=newTparm.getDouble("S_TOTAMT", i);
			totDamt+=newTparm.getDouble("DR_TOTAMT", i);
			totEamt+=newTparm.getDouble("DC_TOTAMT", i);
			totFamt+=newTparm.getDouble("DQ_TOTAMT", i);
		 }
		  newTparm.addData("CHARGE_HOSP_DESC", "总计");
		  newTparm.addData("S_TOTAMT", df.format(totSamt));
		  newTparm.addData("DR_TOTAMT", df.format(totDamt));
	      newTparm.addData("DC_TOTAMT", df.format(totEamt));
	      newTparm.addData("DQ_TOTAMT", df.format(totFamt));
	      
		 table.setParmValue(newTparm);
	}
	/**
	 * 合并parm
	 * @param parm
	 * @param type
	 * @return
	 */
	public TParm getnewTparm(TParm parm,String type){
			   DecimalFormat df = new DecimalFormat("#.00");
//               this.messageBox(""+list.size());
		       double money=0;
		       boolean flag=false;
		       for(int i=0;i<list.size();i++){
		    	   flag=false;
		    	   if(parm.getCount("CHARGE_HOSP_DESC")>0){
		    	   for(int j=0;j<parm.getCount("CHARGE_HOSP_DESC");j++){
			    		  if(list.get(i).equals(parm.getValue("CHARGE_HOSP_DESC", j))){
			    			  money=parm.getDouble(type,j);
			    			  flag=true;
			    			  break;
			    		  }
			    	  }
		    	       if(mresult.getCount("CHARGE_HOSP_DESC")!=list.size()){
		    	          mresult.addData("CHARGE_HOSP_DESC", list.get(i));
		    	       }
		    	   if(flag){
		    		   mresult.addData(type, df.format(money));
		    	   }else{
		    		   mresult.addData(type, df.format(0));  
		    	   }
		       }else{
		    	   mresult.addData(type, df.format(0));  
		       }
		       }
			  return mresult;
		}
		/**
		 * 不同的项目放在一个list里
		 * @param parm
		 * @return
		 */
	public List  getlist(TParm parm){
//			System.out.println("wwww"+parm.getCount("CHARGE_HOSP_DESC"));
		 if(parm.getCount("CHARGE_HOSP_DESC")>0){
			 
			for(int i=0;i<parm.getCount("CHARGE_HOSP_DESC");i++){
				if(!list.contains(parm.getValue("CHARGE_HOSP_DESC", i))){
				 list.add(parm.getValue("CHARGE_HOSP_DESC", i));
				}
			 }
			 }
			return list;
			
		}
		
		/**
		 * 计算每列的金额
		 * @param parma
		 * @param parmb
		 * @param orddamt
		 * @param recp
		 * @param newString
		 * @return
		 */
	public TParm getDparm(TParm parma,TParm parmb,String orddamt,String recp,String newString){
			TParm newTParm=new TParm();
			double stotamt=0;
			boolean flag=false;
			if(parma.getCount()>0){
			
			if(parma.getCount()>0&&parmb.getCount()>0){//总的parma和未作废的parmb都有值
			for(int i=0;i<parma.getCount();i++){
				flag=false;
				for(int j=0;j<parmb.getCount();j++){
					if(parma.getValue("REXP_CODE",i).equals(parmb.getValue("REXP_CODE", j))){
					 stotamt=parma.getDouble(orddamt, i)-parmb.getDouble(recp, j);
					 flag=true;
					 break;
					}
				}
				newTParm.addData("CHARGE_HOSP_DESC", parma.getValue("CHARGE_HOSP_DESC", i));
				newTParm.addData("REXP_CODE", parma.getValue("REXP_CODE", i));
				if(flag){//总的parma和未作废的parmb有相同的项目
					newTParm.addData(newString, stotamt);
				}else{//总的parma和未作废的parmb没有相同的项目
					newTParm.addData(newString, parma.getDouble(orddamt, i));
				}
				
			}
			}else if(parma.getCount()>0&&parmb.getCount()<=0){//总的parma有值并且未作废的parmb没有有值
				for(int i=0;i<parma.getCount();i++){
					 stotamt=parma.getDouble(orddamt, i)-0;
					 newTParm.addData("CHARGE_HOSP_DESC", parma.getValue("CHARGE_HOSP_DESC", i));
					 newTParm.addData("REXP_CODE", parma.getValue("REXP_CODE", i));
					 newTParm.addData(newString, stotamt);
					}
					
					
				}
			}
			return newTParm;
		}
		
	/**
	 * 过滤bilDesc[]中的项目	
	 * @param parm
	 * @param billcode
	 * @param billdesc
	 * @return
	 */
	public TParm getTparm(TParm parm,String[] billcode,String[] billdesc){
		TParm result=new TParm();
		boolean flag=false;
		String billName="";
		if(parm.getCount()>0){
		for(int i=0;i<parm.getCount();i++){
		    flag=false;
			for(int j=0;j<billcode.length;j++){
				if(parm.getValue("REXP_CODE",i).equals(billcode[j])){//数组中存在的项目
				billName=billdesc[j];
				flag=true;
				break;
				}
			}
			if(flag){
				result.addRowData(parm, i);
				result.addData("CHARGE_HOSP_DESC", billName);
			}
		}
		}
		return result;
		
	}
	 /**
     * 打印
     */
	public void onPrint() {
		Timestamp today = SystemTool.getInstance().getDate();
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		if (table.getRowCount() > 0) {
			TParm date = new TParm();
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("CHARGE_HOSP_DESC", tableParm.getValue("CHARGE_HOSP_DESC", i));
				parm.addData("S_TOTAMT", tableParm.getValue("S_TOTAMT", i));
				parm.addData("DR_TOTAMT", tableParm.getValue("DR_TOTAMT", i));
				parm.addData("DC_TOTAMT", tableParm.getValue("DC_TOTAMT", i));
				parm.addData("DQ_TOTAMT", tableParm.getValue("DQ_TOTAMT", i));

			}
			
			parm.setCount(parm.getCount("CHARGE_HOSP_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "CHARGE_HOSP_DESC");
			parm.addData("SYSTEM", "COLUMNS", "S_TOTAMT");
			parm.addData("SYSTEM", "COLUMNS", "DR_TOTAMT");
			parm.addData("SYSTEM", "COLUMNS", "DC_TOTAMT");
			parm.addData("SYSTEM", "COLUMNS", "DQ_TOTAMT");
			date.setData("TABLE", parm.getData());
			date.setData("DATE","TEXT","打印时间:"+today.toString().replaceAll("-", "/").substring(0, today.toString().replaceAll("-", "/").lastIndexOf(".")));
			date.setData("IN_DATE","TEXT","查询时间:"+this.getValueString("IN_DATE").replaceAll("-", "/").substring(0, 10));
			date.setData("USER","TEXT","打印人员:"+Operator.getName());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\BIL\\BILLINPCost.jhw", date);
		} else {
			this.messageBox("没有打印数据");
			return;
		}
	}	
	/**
	 * 清空
	 */
	public void onClear() {
		onInit();
		
	    list=new ArrayList<String>();
		mresult=new TParm();
		table.removeRowAll();
	}
}
