package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.util.Date;

import jdo.inf.INFAntCuAccountUITool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title:סԺ������ҩ�������¼</p>
 *
 * <p>Description:סԺ������ҩ�������¼</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.03.11
 * @version 1.0
 */
public class INFAntCuAccountUIControl  extends TControl{
	private TTable table;
	
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
  /**
   * ��ʼ��
   */
	public void onInit() {
		callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");
		table=this.getTable("TABLE");
		this.onClear();
		 initPage();
		
   }
	 /**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm=new TParm();
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			String insDate = StringTool.getString(TypeTool.getTimestamp(getValue("INS_DATE")), "yyyyMMddHHmmss");
			String ineDate = StringTool.getString(TypeTool.getTimestamp(getValue("INE_DATE")), "yyyyMMddHHmmss");
			String outsDate = StringTool.getString(TypeTool.getTimestamp(getValue("OUTS_DATE")), "yyyyMMddHHmmss");
			String outeDate = StringTool.getString(TypeTool.getTimestamp(getValue("OUTE_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
			parm.setData("INS_DATE",insDate);
			parm.setData("INE_DATE",ineDate);
			parm.setData("OUTS_DATE",outsDate);
			parm.setData("OUTE_DATE",outeDate);
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
			parm.setData("MR_NO",this.getValueString("MR_NO"));
			parm.setData("SESSION_CODE",this.getValueString("SESSION_CODE"));
	
		
		       INFAntCuAccountUITool.getInstance().setTableString(parm);
		       TParm result=INFAntCuAccountUITool.getInstance().getselectTable();
		
		        TParm result1=INFAntCuAccountUITool.getInstance().getselectBnum();//�걾
				TParm result2=INFAntCuAccountUITool.getInstance().getselectTnum();//ͭ��
				TParm result3=INFAntCuAccountUITool.getInstance().getselectBMnum();//��������
				TParm result4=INFAntCuAccountUITool.getInstance().getselectJnum();//���
				TParm result5=INFAntCuAccountUITool.getInstance().getselectCnum();//�����
				TParm result6=INFAntCuAccountUITool.getInstance().getselectFnum();//����
				TParm result7=INFAntCuAccountUITool.getInstance().getselectESBLsnum();//esbls
				TParm result8=INFAntCuAccountUITool.getInstance().getselectOthernum();//����
				TParm result9=INFAntCuAccountUITool.getInstance().getselectGFnum();//���ٷ���
	
		
		if(result.getCount()<0){
			this.messageBox("û�в�ѯ����");
			table.removeRowAll();
			return;
		}
		
		if(result.getErrCode()<0){
			this.messageBox("��ѯ���ֳ�����");
			return;
		}
		

			TParm totreslut=new TParm();
			totreslut=getResult(result,result1,"B_NUM");
			totreslut=getResult(totreslut,result2,"T_NUM");
			totreslut=getResult(totreslut,result3,"BM_NUM");
			totreslut=getResult(totreslut,result4,"J_NUM");
			totreslut=getResult(totreslut,result5,"C_NUM");
			totreslut=getResult(totreslut,result6,"F_NUM");
			totreslut=getResult(totreslut,result7,"ESBLS_NUM");
			totreslut=getResult(totreslut,result8,"OTHER_NUM");
			totreslut=getResult(totreslut,result9,"GF_NUM");
	     	table.setParmValue(totreslut);
	}
	/**
	 * ��ʼ��
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
		this.setValue("INS_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("INE_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
		this.setValue("OUTS_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("OUTE_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * ���Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "סԺ������ҩ������¼");
    }
    
    /**
	 * �������
	 */
	public void onClear() {
		String clearString = "DEPT_CODE;OPE_CODE";
		table.removeRowAll();
		clearValue(clearString);
		initPage();
	}
	/**
	 * �����¼�
	 */
	public void onTABLEClicked(int row){
		if(row<0){
			return;
		}
		TParm tparm= new TParm();
        tparm = table.getParmValue().getRow(row);
        this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
        this.setValue("SESSION_CODE", tparm.getValue("STATION_CODE"));
        this.setValue("MR_NO", tparm.getValue("MR_NO"));
			    
		  
	}
	
	/**
	 * �ϲ�ÿһ�еĲ�ѯ����
	 * @param oResult
	 * @param parm
	 * @param type
	 * @return
	 */
	public TParm getResult(TParm resultTotnum ,TParm parm, String type){
		//TParm result=new TParm();
		//this.messageBox("parm:::"+parm.getCount());
		String deptCode="";
		String caseNo="";
		String num="";
		for(int i=0;i<resultTotnum.getCount();i++){
			boolean flag=false;
			deptCode=resultTotnum.getValue("DEPT_CODE",i);
			caseNo=resultTotnum.getValue("CASE_NO",i);
			for(int j=0;j<parm.getCount();j++){
				if(deptCode.equals(parm.getValue("DEPT_CODE",j))&&caseNo.equals(parm.getValue("CASE_NO",j))){
					num=parm.getValue(type,j);
					flag=true;
					break;
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
	
}