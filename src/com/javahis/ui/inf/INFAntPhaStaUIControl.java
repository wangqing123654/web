package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.inf.INFAntPhaStaUITool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

public class INFAntPhaStaUIControl  extends TControl{
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
		
		TParm result=new TParm();
			String sDate = StringTool.getString(TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
		//����
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		
		TParm resultTotnum=INFAntPhaStaUITool.getInstance().getSelectTotnum(parm);//��Ժ������
		TParm resultAntnum=INFAntPhaStaUITool.getInstance().getselectAntnum(parm);//��Ժʹ�ÿ���ҩ������
		TParm resultWayTwonum=INFAntPhaStaUITool.getInstance().getselectWayTwonum(parm);//��Ժ ���ƿ���ҩ������
		TParm resultWayOnenum=INFAntPhaStaUITool.getInstance().getselectWayOnenum(parm);//��ԺԤ������ҩ������
		TParm resultMednum=INFAntPhaStaUITool.getInstance().getselectMednum(parm);//��Ժҩ��ʵ����
		TParm resultaddresult=INFAntPhaStaUITool.getInstance().getaddResult(parm);//����+Ԥ��
		TParm resultI=new TParm();
		TParm resultII=new TParm();
		TParm resultW=new TParm();
		if(resultTotnum.getCount()<0){
			this.messageBox("û�в�ѯ����");
			table.removeRowAll();
			return;
		}
		
		if(resultTotnum.getErrCode()<0){
			this.messageBox("��ѯ���ֳ�����");
			return;
		}
		double antRate=0;//����ҩʹ����
		double medRate=0;//ҩ����
		//int totAntnum=0;
		DecimalFormat dec=new DecimalFormat("##.##%");
		/**�ϲ�---------result----------------**/
			result=this.getResult(resultTotnum, resultAntnum, "ANTI_NUM");
			result=this.getResult(result, resultWayTwonum, "WAYII_NUM");
			result=this.getResult(result, resultWayOnenum, "WAYI_NUM");
			result=this.getResult(result, resultMednum, "MED_NUM");
			result=this.getResult(result, resultI, "I_NUM");
			result=this.getResult(result, resultII, "II_NUM");
			result=this.getResult(result, resultW, "W_NUM");
			result=this.getResult(result, resultaddresult, "TOT_ANTNUM");
			
		
		for(int a=0;a<result.getCount();a++){
			if(result.getDouble("TOT_NUM",a)!=0){
				antRate=result.getDouble("ANTI_NUM",a)/result.getDouble("TOT_NUM",a);
				medRate=result.getDouble("MED_NUM",a)/result.getDouble("TOT_NUM",a);
				//totAntnum=result.getInt("WAYII_NUM", a)+result.getInt("WAYI_NUM", a);
				
			}
			
			result.addData("ANT_RATE", dec.format(antRate));
			result.addData("MED_TARE", dec.format(medRate));
			//result.addData("TOT_ANTNUM", totAntnum);
		}
		///System.out.println("result:::::::::"+result);
		
		table.setParmValue(result);
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
		String num="";
		for(int i=0;i<resultTotnum.getCount();i++){
			boolean flag=false;
			deptCode=resultTotnum.getValue("OUT_DEPT",i);
			for(int j=0;j<parm.getCount();j++){
				if(deptCode.equals(parm.getValue("OUT_DEPT",j))){
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
	
	
	/**
	 * ��ʼ��
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * ���Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "����ҩ��ʹ���������");
    }
    
    /**
	 * �������
	 */
	public void onClear() {
		String clearString = "S_DATE;E_DATE;DEPT_CODE;VS_DR_CODE";
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
        this.setValue("DEPT_CODE", tparm.getValue("OUT_DEPT"));
			    
		  
	}
	
	
}