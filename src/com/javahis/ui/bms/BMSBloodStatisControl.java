package com.javahis.ui.bms;

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
 * <p>Title:��Ѫͳ�Ʊ���
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx   20130308
 * @version 4.0
 */
public class BMSBloodStatisControl extends TControl{
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
		if(!this.check()){
			return ;
		}
		String sdate = this.getValueString("S_DATE").substring(0,10).replace("-", "")+"000000" ;
		String edate = this.getValueString("E_DATE").substring(0, 10).replace("-", "")+"235959" ;
		StringBuffer sb  = new StringBuffer() ;
		String sql = "  SELECT  C.DEPT_CHN_DESC,D.STATION_DESC,E.USER_NAME,F.BLDCODE_DESC,sum(A.BLOOD_VOL) BLOOD_VOL," +
				    "   G.UNIT_CHN_DESC     FROM BMS_BLOOD A ,BMS_APPLYM B ,SYS_DEPT C,SYS_STATION D ," +
				    "   SYS_OPERATOR E ,BMS_BLDCODE F ,SYS_UNIT G WHERE      " +
				    "   A.APPLY_NO = B.APPLY_NO   AND A.DEPT_CODE = C.DEPT_CODE" +
				    "  AND A.STATION_CODE =D.STATION_CODE AND B.DR_CODE = E.USER_ID" +
				    "  AND A.BLD_CODE = F.BLD_CODE  AND F.UNIT_CODE =G.UNIT_CODE " +
				    "  AND A.STATE_CODE = '2'   AND   A.OUT_DATE BETWEEN TO_DATE('"+sdate+"', 'YYYYMMDDHH24MISS')   " +
				    "  AND TO_DATE('"+edate+"', 'YYYYMMDDHH24MISS') "  ;
		if(this.getValueString("DEPT_CODE").length()>0){
			sb.append(" AND A.DEPT_CODE = '"+getValueString("DEPT_CODE")+"' ") ;
		}
		if(this.getValueString("USER_NAME").length()>0){
			sb.append(" AND B.DR_CODE= '"+getValueString("USER_NAME")+"' ") ;
		}
		String whereSql = " GROUP BY C.DEPT_CHN_DESC,D.STATION_DESC,E.USER_NAME,F.BLDCODE_DESC,G.UNIT_CHN_DESC " +
				          "  ORDER  BY  C.DEPT_CHN_DESC  "  ;
		if(sb.length()>0)
			sql += sb.toString() +whereSql ;
		else sql += whereSql ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(parm.getErrCode()<0){
			this.messageBox("��������") ;
			return  ;
		}
		if(parm.getCount()<=0){
			this.messageBox("��������") ;
			this.onClear() ;
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
		
		if(this.getValueString("S_DATE")==null || this.getValueString("S_DATE").length()<0){
			this.messageBox("ͳ��ʱ�䲻��Ϊ��") ;
			return false ;
		}
		if(this.getValueString("E_DATE")==null || this.getValueString("E_DATE").length()<0){
			this.messageBox("ͳ��ʱ�䲻��Ϊ��") ;
			return false ;
		}
		return true ;
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("DEPT_CODE;USER_NAME") ;
		table.removeRowAll() ;
	}
	
	
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		TParm tableParm = table.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount("DEPT_CHN_DESC")<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		//==DEPT_CHN_DESC;STATION_DESC;USER_NAME;BLDCODE_DESC;BLOOD_VOL;UNIT_CHN_DESC
		for(int i=0;i<tableParm.getCount("DEPT_CHN_DESC");i++){
			result.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i)); //��ֵ 
			result.addData("STATION_DESC", tableParm.getValue("STATION_DESC", i)); 
			result.addData("USER_NAME", tableParm.getValue("USER_NAME", i)); 
			result.addData("BLDCODE_DESC", tableParm.getValue("BLDCODE_DESC", i)); 
			result.addData("BLOOD_VOL", tableParm.getValue("BLOOD_VOL", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i)); 
		}
		result.setCount(tableParm.getCount("DEPT_CHN_DESC")) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		result.addData("SYSTEM", "COLUMNS", "USER_NAME");
		result.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
		result.addData("SYSTEM", "COLUMNS", "BLOOD_VOL");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//�Ʊ�ʱ��
		printParm.setData("TITLE", "TEXT","������Ѫͳ�Ʊ���") ;
		printParm.setData("DATE", "TEXT","ͳ������:"+date) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�Ʊ���: " + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\bms\\BMSBloodStatis.jhw", 
				printParm);
	}
	
	
	 /**
     * ���Excel
     */
    public void onExport() {
        //�õ�UI��Ӧ�ؼ�����ķ���
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount("DEPT_CHN_DESC") <= 0) {
            this.messageBox("û����Ҫ����������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "������Ѫͳ�Ʊ���");
    }	
}
