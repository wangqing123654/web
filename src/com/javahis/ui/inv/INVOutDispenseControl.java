package com.javahis.ui.inv;

import java.sql.Timestamp;

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
 * <p>Title:���ʳ��ⵥ����
 *
 * <p>Description: ���ʳ��ⵥ����
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVOutDispenseControl extends TControl{
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
		TParm parm =INVsettlementTool.getInstance().queryOutDispense(getSearchParm()) ;
		System.out.println("parm====="+parm);
		if(parm.getCount()<=0){
			this.messageBox("��������") ;
			this.onClear();
			return ;
		}
		double money = 0.00 ;
		for(int i=0;i<parm.getCount();i++){
			money +=parm.getDouble("AMT", i) ;
		}
		parm.addData("ORG_DESC", "�ϼ�") ;
		parm.addData("INV_CODE", "") ;
		parm.addData("INV_CHN_DESC", "") ;
		parm.addData("DESCRIPTION", "") ;
		parm.addData("QTY", "") ;
		parm.addData("UNIT_CHN_DESC", "") ;
		parm.addData("COST_PRICE", "") ;
		parm.addData("AMT", StringTool.round(money,2)) ;
		parm.setCount(parm.getCount("INV_CODE")) ;
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
 			searchParm.setData("FROM_ORG_CODE", this.getValueString("ORG_CODE")) ;
 		if(this.getValueString("REQUEST_TYPE").length()>0)
 			searchParm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE")) ;
 		return searchParm;
 	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("ORG_CODE;REQUEST_TYPE") ;
		TParm clearParm = new TParm() ;
		table.setParmValue(clearParm) ;
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
			result.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i)); //��ֵ 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i)); 
			result.addData("QTY", tableParm.getValue("QTY", i)); 
			result.addData("COST_PRICE", tableParm.getValue("COST_PRICE", i)); 
			result.addData("AMT", tableParm.getValue("AMT", i)); 
		}
		result.setCount(tableParm.getCount()) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "AMT");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//�Ʊ�ʱ��
		String orgDesc = this.getValueString("ORG_CODE").length()>0?tableParm.getValue("ORG_DESC", 0):"ȫ��" ;
		String requestType = this.getValueString("REQUEST_TYPE").length()>0?this.getValueString("REQUEST_TYPE"):"ȫ��" ;
		printParm.setData("TITLE", "TEXT","ר��Ʒ���ⵥ") ;
		printParm.setData("DATE", "TEXT","ͳ������:"+date) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�Ʊ���: " + Operator.getName());
		printParm.setData("ORG_DESC", "TEXT", "����: " + orgDesc);
		printParm.setData("REQUEST_TYPE", "TEXT", "�������: " + requestType);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVOutDispense.jhw",
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
        ExportExcelUtil.getInstance().exportExcel(table, "���ʳ��ⵥ����");
    }	
}
