package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.util.Date;

import jdo.udd.UDDPatLimitinglevelDetailTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: ʹ�����Ƽ����Ͽ���ҩ��������ϸ����</p>
 *
 * <p>Description:ʹ�����Ƽ����Ͽ���ҩ��������ϸ����</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.02.28
 * @version 1.0
 */
public class UDDPatLimitinglevelDetailControl  extends TControl{
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
			parm.setData("S_DATE",sDate);
			parm.setData("E_DATE",eDate);
		//����
		if(this.getValueString("DEPT_CODE").length()>0){
			parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
		}
		//ҽ��
		if(this.getValueString("VS_DR_CODE").length()>0){
			parm.setData("VS_DR_CODE",this.getValueString("VS_DR_CODE"));
		}
		
		TParm result=UDDPatLimitinglevelDetailTool.getInstance().getselectInum(parm);
		
		
		if(result.getCount()<0){
			this.messageBox("û�в�ѯ����");
			table.removeRowAll();
			return;
		}
		
		if(result.getErrCode()<0){
			this.messageBox("��ѯ���ֳ�����");
			return;
		}
		
	    String deptCode ="";
	    String drCode="";
	    deptCode=result.getValue("DEPT_CODE",0);
	    drCode=result.getValue("VS_DR_CODE",0);
	    int count=0;
	    TParm rParm=new TParm();
	    for(int i=0;i<result.getCount();i++){
	    if(deptCode.equals(result.getValue("DEPT_CODE",i))
	    	&&drCode.equals(result.getValue("VS_DR_CODE",i))){
	    	rParm.addRowData(result, i);
	    	count++;
	    }else{
	    	rParm.addData("REGION_CODE", "С�ƣ�");
	    	rParm.addData("ODI_TYPE", "");
	    	rParm.addData("DEPT_CODE", "");
	    	rParm.addData("VS_DR_CODE", "");
	    	rParm.addData("CASE_NO", "");
	    	rParm.addData("MR_NO", "");
	    	rParm.addData("PAT_NAME", count);
	    	
	    	 deptCode=result.getValue("DEPT_CODE",i);
	 	    drCode=result.getValue("VS_DR_CODE",i);
	    	count=0;
	    	 if(deptCode.equals(result.getValue("DEPT_CODE",i))
	    		    	&&drCode.equals(result.getValue("VS_DR_CODE",i))){
	    		    	rParm.addRowData(result, i);
	    		    	count++;
	    		    	
	    		    }
	    	
	     }
		    if(i==result.getCount()-1){
		    	
		    	rParm.addData("REGION_CODE", "С�ƣ�");
		    	rParm.addData("ODI_TYPE", "");
		    	rParm.addData("DEPT_CODE", "");
		    	rParm.addData("VS_DR_CODE", "");
		    	rParm.addData("CASE_NO", "");
		    	rParm.addData("MR_NO", "");
		    	rParm.addData("PAT_NAME", count);
		    	 
		     }
	    	
	    }
	    rParm.addData("REGION_CODE", "�ܼƣ�");
    	rParm.addData("ODI_TYPE", "");
    	rParm.addData("DEPT_CODE", "");
    	rParm.addData("VS_DR_CODE", "");
    	rParm.addData("CASE_NO", "");
    	rParm.addData("MR_NO", "");
    	rParm.addData("PAT_NAME", result.getCount());
		table.setParmValue(rParm);
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
        ExportExcelUtil.getInstance().exportExcel(table, "����Ӧ�ñ���ͳ��");
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
        this.setValue("SESSION_CODE", tparm.getValue("STATION_CODE"));
        this.setValue("VS_DR_CODE", tparm.getValue("VS_DR_CODE"));
			    
		  
	}
}