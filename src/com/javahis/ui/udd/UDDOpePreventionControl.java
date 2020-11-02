package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.udd.UDDOpePreventionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: ����ҩƷ����Ԥ��ҩƷʹ�ñ���</p>
 *
 * <p>Description:����ҩƷ����Ԥ��ҩƷʹ�ñ���</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.02.12
 * @version 1.0
 */
public class UDDOpePreventionControl  extends TControl{
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
		
		TParm result=UDDOpePreventionTool.getInstance().getselectInum(parm);
		
		for(int i=0;i<result.getCount();i++){
			String time=dateDiff(result.getValue("ORDER_DATE", i).replaceAll("/","-"),result.getValue("OP_DATE", i).replaceAll("/","-"),"yyyy-MM-dd HH:mm:ss");
			result.addData("DATE_DEFFEREBCE", time);
		}
		if(result.getCount()<0){
			this.messageBox("û�в�ѯ����");
			table.removeRowAll();
			return;
		}
		
		if(result.getErrCode()<0){
			this.messageBox("��ѯ���ֳ�����");
			return;
		}
		table.setParmValue(result);
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
	
	public String dateDiff(String startTime, String endTime, String format) {
		//���մ���ĸ�ʽ����һ��simpledateformate����
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000*24*60*60;//һ��ĺ�����
		long nh = 1000*60*60;//һСʱ�ĺ�����
		long nm = 1000*60;//һ���ӵĺ�����
		long ns = 1000;//һ���ӵĺ�����
		long diff;
		String datatime="";
		try {
		//�������ʱ��ĺ���ʱ�����
		diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff/nd;//����������
		long hour = diff%nd/nh;//��������Сʱ
		long min = diff%nd%nh/nm;//�������ٷ���
		long sec = diff%nd%nh%nm/ns;//����������
		//������
		if(day!=0){
			datatime=day+"��"+hour+"Сʱ"+min+"����"+sec+"��";
		}else if(hour!=0){
			datatime= hour+"Сʱ"+min+"����"+sec+"��";
		}else if(min!=0){
			datatime=min+"����"+sec+"��";
		}else{
			datatime=sec+"��";
		}
		 return datatime;
		} catch (Exception e) {
		e.printStackTrace();
		}
		return datatime;
	}
	
}