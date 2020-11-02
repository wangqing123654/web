package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:������ⵥ����
 *
 * <p>Description: ������ⵥ����
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx  
 * @version 4.0
 */
public class INVDispenseInPrintControl extends TControl{
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
		this.setValue("S_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //��ʼʱ��
		this.setValue("E_DATE", StringTool.getTimestamp(now, "yyyyMMdd")) ; //����ʱ��
		table = (TTable)this.getComponent("TABLE");
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		if(this.check()){
			return ;
		}
		String sql = "" ;
		System.out.println("sql========="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(parm.getErrCode()<0){
			this.messageBox("��������") ;
			return  ;
		}
		if(parm.getCount()<=0){
			this.messageBox("��������") ;
			return ;
		}
		table.setParmValue(parm) ;
		date = StringTool.getString((Timestamp) this.getValue("S_DATE"),
		"yyyy/MM/dd ")
		+ " �� "
		+ StringTool.getString((Timestamp) this.getValue("E_DATE"),
				"yyyy/MM/dd ");

	}
	/**
	 * У��
	 * @return
	 */
	public boolean check(){
		if(this.getValueString("")==null || this.getValueString("").length()<0){
			this.messageBox("") ;
			return false ;
		}
		return true ;
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("") ;
		table.removeAll() ;
	}
	
	
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount("")<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		for(int i=0;i<tableParm.getCount("");i++){
			result.addData("", tableParm.getValue("", i)); //��ֵ 
			result.addData("", tableParm.getValue("", i)); 
			result.addData("", tableParm.getValue("", i)); 
		}
		result.setCount(tableParm.getCount("")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "");//����
		result.addData("SYSTEM", "COLUMNS", "");
		result.addData("SYSTEM", "COLUMNS", "");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//�Ʊ�ʱ��
		printParm.setData("TITLE", "TEXT","����") ;
		printParm.setData("DATE", "TEXT","ͳ������:"+date) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�Ʊ���: " + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILItemFeeDetail.jhw",
				printParm);
	}
	
	
	 /**
     * ���Excel
     */
    public void onExport() {
        //�õ�UI��Ӧ�ؼ�����ķ���
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount("") <= 0) {
            this.messageBox("û����Ҫ����������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "");
    }	
}
