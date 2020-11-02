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
 * <p>Title: 	ҽԺ��Ⱦ����ͳ�Ʊ�</p>
 *
 * <p>Description:ҽԺ��Ⱦ����ͳ�Ʊ��</p>
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
			String rsDate = StringTool.getString(TypeTool.getTimestamp(getValue("RS_DATE")), "yyyyMMddHHmmss");
			String reDate = StringTool.getString(TypeTool.getTimestamp(getValue("RE_DATE")), "yyyyMMddHHmmss");
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
			parm.setData("RS_DATE",rsDate);
			parm.setData("RE_DATE",reDate);
		//����
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		
		TParm resultTotnum=INFPatStaUITool.getInstance().getselectTotnum(parm);//�������
		TParm resultInfnum=INFPatStaUITool.getInstance().getselectInfnum(parm);//��Ⱦ����
		TParm resultInpTotnum=INFPatStaUITool.getInstance().getSelectInpTotnum(parm);//סԺ����
		TParm resultOutTotnum=INFPatStaUITool.getInstance().getSelectOutTotnum(parm);;//��Ժ����
		TParm resultInf03num=INFPatStaUITool.getInstance().getselectInf03num(parm);//�����Ⱦ����
		TParm resultInf05num=INFPatStaUITool.getInstance().getselectInf05num(parm);//��θȾ����
		TParm resultInf01num=INFPatStaUITool.getInstance().getselectInf01num(parm);//�Ϻ�����Ⱦ����
		TParm resultInf02num=INFPatStaUITool.getInstance().getselectInf02num(parm);//�º�����Ⱦ����
		TParm resultInf04num=INFPatStaUITool.getInstance().getselectInf04num(parm);//�пڸ�Ⱦ����
		TParm resultInf06num=INFPatStaUITool.getInstance().getselectInf06num(parm);//ѪҺ��Ⱦ
		TParm resultInf13num=INFPatStaUITool.getInstance().getselectInf13num(parm);//������Ⱦ
		TParm resultInfDIE1num=INFPatStaUITool.getInstance().getselectInfDIE1num(parm);//������Ӱ��
		TParm resultInfDIE3num=INFPatStaUITool.getInstance().getselectInfD3num(parm);//�ٽ���������
		TParm resultInfDIE4num=INFPatStaUITool.getInstance().getselectInfD4num(parm);//ֱ����������
		TParm resultInfT5num=INFPatStaUITool.getInstance().getselectT5num(parm);//©��
		TParm resultInfTnum=INFPatStaUITool.getInstance().getselectTnum(parm);//ͭ��
		TParm resultInfJnum=INFPatStaUITool.getInstance().getselectJnum(parm);//���
		TParm resultInfMnum=INFPatStaUITool.getInstance().getselectMnum(parm);//ù��
		TParm resultInfOtherTnum=INFPatStaUITool.getInstance().getselecOtherTnum(parm);//����
		
		
		//TParm resultI=new TParm();
		//TParm resultII=new TParm();
		//TParm resultW=new TParm();
		if(resultTotnum.getCount()<0){
			this.messageBox("û�в�ѯ����");
			table.removeRowAll();
			return;
		}
		
		if(resultTotnum.getErrCode()<0){
			this.messageBox("��ѯ���ֳ�����");
			return;
		}
		double infRate=0;//��Ⱦ��
		DecimalFormat dec=new DecimalFormat("##.##%");
		/**�ϲ�---------result----------------**/
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
	 * ��ʼ��
	 */
	public void initPage() {

	    Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("E_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
		this.setValue("RE_DATE", date.toString().substring(0, 10).replace('-','/')+ " 23:59:59");
		this.setValue("RS_DATE", date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	}
	
	/**
     * ���Excel
     */
    public void onExport() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "ҽԺ��Ⱦ����ͳ�Ʊ�");
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
        this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
        //this.setValue("DEPT_CODE", tparm.getValue("DEPT_CODE"));
			    
		  
	}
	
	
}