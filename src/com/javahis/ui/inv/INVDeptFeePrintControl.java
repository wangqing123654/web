package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.bil.BILSysParmTool;
import jdo.inv.INVsettlementTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:���ҷ���ͳ�Ʊ���
 *
 * <p>Description: ���ҷ���ͳ�Ʊ���
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVDeptFeePrintControl extends TControl{
	private TTable table ;
	private String date  ; //ͳ������   
	
	/**
	 * ��ʼ��
	 */

	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
	}     
	/**
	 * ��ʼ������
	 */
	public void onInitPage(){ 
		String now = SystemTool.getInstance().getDate().toString().replace("-", "") ;
		this.setValue("START_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //����ʱ��
		table = (TTable)this.getComponent("TABLE");    
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
	  TParm parm = INVsettlementTool.getInstance().queryDeptFee(getSearchParm());
		if(parm.getCount()<=0){
			this.messageBox("��������") ;
			this.onClear() ;
			return ;
		}
		double money = 0.00 ;
		for(int i=0;i<parm.getCount();i++){
			money +=parm.getDouble("AMT", i) ;
		}
		parm.addData("ORG_CODE", "") ;
		parm.addData("ORG_DESC", "�ϼ�") ;
		parm.addData("AMT", StringTool.round(money,2)) ;
		parm.setCount(parm.getCount("ORG_CODE")) ;
		table.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("START_DATE"),
		"yyyy/MM/dd ")
		+ " �� "
		+ StringTool.getString((Timestamp) this.getValue("END_DATE"),
				"yyyy/MM/dd ");

	}
	  /**
     * ��ȡ��ѯ��������   
     * @return
     * */
 	private TParm getSearchParm() {
 		TParm searchParm = new TParm();
 		String startDate = getValueString("START_DATE").substring(0, 10).replace("-", "");
 		String endDate = getValueString("END_DATE").substring(0, 10).replace("-", "");
 		searchParm.setData("START_DATE",startDate+"000000"); 
 		searchParm.setData("END_DATE",endDate+"235959");  
 		if(this.getValueString("ORG_CODE").length()>0)
 			searchParm.setData("EXE_DEPT_CODE", this.getValueString("ORG_CODE")) ;
 		return searchParm;
 	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("ORG_CODE") ;
		table.removeRowAll();
	}
	
	
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("SEQ", i+1); //��ֵ 
			result.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i)); 
			result.addData("AMT", tableParm.getDouble("AMT", i)); 
		}
		result.setCount(tableParm.getCount()) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "SEQ");//����
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");
		result.addData("SYSTEM", "COLUMNS", "AMT");
//		System.out.println("result====="+result);
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//�Ʊ�ʱ��
		printParm.setData("TITLE", "TEXT","���ʲ��ŷ���ͳ�Ʊ���") ;
		printParm.setData("DATE", "TEXT","ͳ������:"+date) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�Ʊ���: " + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDeptFeePrint.jhw",
				printParm);
	}
	
	
	 /**
     * ���Excel
     */
    public void onExport() {
        //�õ�UI��Ӧ�ؼ�����ķ���
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("û����Ҫ����������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���ҷ���ͳ�Ʊ���");
    }	
}
