package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ·����Ⱦ������ϸ��ѯ</p>
 *
 * <p>Description:·����Ⱦ������ϸ��ѯ </p>
 *
 * <p>Copyright: Copyright (c) blueCore</p>
 *
 * <p>Company: blueCore</p>
 *
 * @author caowl 20121224
 * @version 1.0
 */
public class CLPContagionQueryControl extends TControl{
	
	// ��ʼʱ��
	private TTextFormat start_date;
	// ����ʱ��
	private TTextFormat end_date;
	// ���
	private TTable table;

	public CLPContagionQueryControl() {

	}
	/***
	 * ��ʼ��
	 * */
	 public void onInit() {
	        super.onInit();
	        initPage();	       	      
	      
	    }
	 /**
	  * ��ʼ��ҳ��
	  * */
	 public void initPage(){
		 this.callFunction("UI|CLPTABLE|removeRowAll");
			initControl();
	 }
	/**
	 * ��ʼ���ؼ�
	 * */ 
	 public void initControl(){
		    Timestamp date = StringTool.getTimestamp(new Date());
			// ��ʼ��ʱ���ѯ����
			this.setValue("e_Date", date.toString().substring(0, 10).replace('-',
					'/')
					+ " 23:59:59");
			this.setValue("s_Date", StringTool.rollDate(date, -7).toString()
					.substring(0, 10).replace('-', '/')
					+ " 00:00:00");
			table = (TTable) this.getComponent("Table");
			start_date = (TTextFormat) this.getComponent("s_Date");
			end_date = (TTextFormat) this.getComponent("e_Date");
			
	 }
	 
	 /**
	  * ��ѯ
	  * */
	 public TParm onQuery(){
		 TParm selParm = new TParm();
		 if (!checkData()) {
			return selParm;
		 }
		//��Ժ����
		String deptSelect = "";
		String dept_code = this.getValueString("DEPT_CODE");
		if(dept_code != null && !dept_code.equals("")){
			deptSelect  = " AND A.OUT_DEPT = '"+dept_code+"'";
		}	
		//�õ���Ժ���ڲ�ѯ����
		String selectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND A.OUT_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}
		String sql =" SELECT DISTINCT  A.MR_NO,A.CASE_NO,A.PAT_NAME,  "+
					" CASE WHEN A.SEX = '1' THEN '��'  ELSE 'Ů' END AS SEX,D.DEPT_CHN_DESC AS IN_DEPT,"+
					" A.OUT_STATION,A.IN_DATE,A.OUT_DATE,A.REAL_STAY_DAYS,"+
					" M.CLNCPATH_CHN_DESC,C.ICD_CODE,C.ICD_DESC,S.CHN_DESC AS ICD_STATUS     "+ 
					" FROM MRO_RECORD A,CLP_MANAGEM B,MRO_RECORD_DIAG C,SYS_DICTIONARY S,SYS_DEPT D,CLP_BSCINFO M"+
					" WHERE "+
					" A.CASE_NO = B.CASE_NO"+
					" AND B.CLNCPATH_CODE = M.CLNCPATH_CODE"+
					" AND A.IN_DEPT = D.DEPT_CODE"+
					" AND C.ICD_STATUS = S.ID"+
					" AND S.GROUP_ID = 'ADM_RETURN'"+
					" AND A.CASE_NO = C.CASE_NO"+
					" AND A.INTE_DIAG_CODE = C.ICD_CODE"+
					" AND C.IO_TYPE  = 'Q'"+deptSelect+selectCondition;
		//System.out.println("sql:::::"+sql);		
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println(selParm.getCount());
		if (selParm == null || selParm.getCount() < 0) {
		    this.messageBox("��������");
		    return selParm;
		}
		table.setParmValue(selParm);
		return selParm;
	 }	
	/**
	 * ���
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "·����Ⱦ������ϸͳ�Ʊ���");
		}
	}
	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		String start = this.getValueString("s_Date");
		if (start == null || start.length() <= 0) {
			this.messageBox("��ʼʱ�䲻��Ϊ��");
			return false;
		}
		String end = this.getValueString("e_Date");
		if (end == null || end.length() <= 0) {
			this.messageBox("����ʱ�䲻��Ϊ��");
			return false;
		}

		return true;
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}
	 
}
