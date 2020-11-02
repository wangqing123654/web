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
 * <p>Title:סԺ���˷��ñ�</p>
 *
 * <p>Description:סԺ���˷��ñ�</p>
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
	private String bilDesc[]={"��λ��","����","��ҩ��","����ҩ��","�ǿ���ҩ��","�г�ҩ��","�в�ҩ��","����","���Ʒ�","�����","������","�����","��Ѫ��","������","������","CT��","���Ϸ�","��ҩ��","�����"};
	
   private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
/**
   * ��ʼ��
   */
	public void onInit() {
		    table=this.getTable("TABLE");
		    Timestamp date = StringTool.getTimestamp(new Date());
			this.setValue("IN_DATE", date.toString().substring(0, 10).replace('-','/'));
		
   }
	//��ѯ
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
		    //--start �ϴ�δ�����ܼ�
			TParm result1=BILInCostTool.getInstance().getSelectSordd(parm);//�ϴ�δ�����ܼ� ���
			TParm result2=BILInCostTool.getInstance().getSelectSrecp(parm);//�ϴ�δ����δ���ϵĽ��
			TParm result3=this.getTparm(result1, bilcode,bilDesc);//����ֻҪ�����е���Ŀ
			TParm result4=this.getTparm(result2, bilcode,bilDesc);//����ֻҪ�����е���Ŀ
			TParm newTParm1=this.getDparm(result3, result4, "STOT_AMT", "SW_AMT","S_TOTAMT");//�ϴ�δ�����ܼ� ���-�ϴ�δ����δ���ϵĽ��
			//--- end �ϴ�δ�����ܼ�
			
			//--start ��Ӧ���ܼ�
			TParm dresult1=BILInCostTool.getInstance().getSelectDSordd(parm);//��Ӧ���ܼ�
			TParm dresult2=BILInCostTool.getInstance().getSelectDSrecp(parm);//��Ӧ��δ���ϵĽ��
			TParm dresult3=this.getTparm(dresult1, bilcode,bilDesc);//����ֻҪ�����е���Ŀ
			TParm dresult4=this.getTparm(dresult2, bilcode,bilDesc);//����ֻҪ�����е���Ŀ
			TParm newTParm2=this.getDparm(dresult3, dresult4, "STOT_AMT", "SW_AMT","DR_TOTAMT");//��Ӧ���ܼ�-��Ӧ��δ���ϵĽ��
//				System.out.println("newTParm2:::::"+newTParm2);
			//--start end 
			
			//--start �ճ�Ժ�����ܼ�
			TParm fresult1=BILInCostTool.getInstance().getSelectFSordd(parm);//�ճ�Ժ�����ܼ�
			//TParm fresult2=BILInCostTool.getInstance().getSelectDFrecp(parm);//�ճ�Ժδ���ϵĽ��
			TParm fresult3=this.getTparm(fresult1, bilcode,bilDesc);//����ֻҪ�����е���Ŀ
			//TParm fresult4=this.getTparm(fresult2, bilcode,bilDesc);//����ֻҪ�����е���Ŀ
			TParm fresult4= new TParm();
			TParm newTParm3=this.getDparm(fresult3, fresult4, "STOT_AMT", "SW_AMT","DC_TOTAMT");//�ճ�Ժ�����ܼ�-�ճ�Ժδ���ϵĽ��
			//--end  �ճ�Ժ�����ܼ�
//				System.out.println("newTParm3:::::"+newTParm3);
			//��ÿ����ͬ�����е���Ŀ�ŵ�һ��list ��
			this.getlist(newTParm1);
			this.getlist(newTParm2);
			this.getlist(newTParm3);
			
			
			//�����Ժ��parm ������һ��
			this.getnewTparm(newTParm1,"S_TOTAMT");
			this.getnewTparm(newTParm2,"DR_TOTAMT");
			newTparm=this.getnewTparm(newTParm3,"DC_TOTAMT");
			
			if(newTparm.getCount("CHARGE_HOSP_DESC")<=0){
				this.messageBox("û�в�ѯ����");
				return;
			}
	   //��ǰδ�����ܼ�	
		double dqtotamt=0;
		for(int i=0;i<newTparm.getCount("CHARGE_HOSP_DESC");i++){
			dqtotamt=newTparm.getDouble("S_TOTAMT", i)+newTparm.getDouble("DR_TOTAMT", i)-newTparm.getDouble("DC_TOTAMT", i);
			newTparm.addData("DQ_TOTAMT", df.format(dqtotamt));
		}
		
		//-----start �ܼ�
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
		  newTparm.addData("CHARGE_HOSP_DESC", "�ܼ�");
		  newTparm.addData("S_TOTAMT", df.format(totSamt));
		  newTparm.addData("DR_TOTAMT", df.format(totDamt));
	      newTparm.addData("DC_TOTAMT", df.format(totEamt));
	      newTparm.addData("DQ_TOTAMT", df.format(totFamt));
	      
		 table.setParmValue(newTparm);
	}
	/**
	 * �ϲ�parm
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
		 * ��ͬ����Ŀ����һ��list��
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
		 * ����ÿ�еĽ��
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
			
			if(parma.getCount()>0&&parmb.getCount()>0){//�ܵ�parma��δ���ϵ�parmb����ֵ
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
				if(flag){//�ܵ�parma��δ���ϵ�parmb����ͬ����Ŀ
					newTParm.addData(newString, stotamt);
				}else{//�ܵ�parma��δ���ϵ�parmbû����ͬ����Ŀ
					newTParm.addData(newString, parma.getDouble(orddamt, i));
				}
				
			}
			}else if(parma.getCount()>0&&parmb.getCount()<=0){//�ܵ�parma��ֵ����δ���ϵ�parmbû����ֵ
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
	 * ����bilDesc[]�е���Ŀ	
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
				if(parm.getValue("REXP_CODE",i).equals(billcode[j])){//�����д��ڵ���Ŀ
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
     * ��ӡ
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
			date.setData("DATE","TEXT","��ӡʱ��:"+today.toString().replaceAll("-", "/").substring(0, today.toString().replaceAll("-", "/").lastIndexOf(".")));
			date.setData("IN_DATE","TEXT","��ѯʱ��:"+this.getValueString("IN_DATE").replaceAll("-", "/").substring(0, 10));
			date.setData("USER","TEXT","��ӡ��Ա:"+Operator.getName());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\BIL\\BILLINPCost.jhw", date);
		} else {
			this.messageBox("û�д�ӡ����");
			return;
		}
	}	
	/**
	 * ���
	 */
	public void onClear() {
		onInit();
		
	    list=new ArrayList<String>();
		mresult=new TParm();
		table.removeRowAll();
	}
}
