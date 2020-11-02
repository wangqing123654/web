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
 * <p>Title: ����������ϸ��ѯ</p>
 *
 * <p>Description:����������ϸ��ѯ </p>
 *
 * <p>Copyright: Copyright (c) blueCore</p>
 *
 * <p>Company: blueCore</p>
 *
 * @author caowl 20121224
 * @version 1.0
 */
public class CLPDeathQueryControl extends TControl{
	
	// ��ʼʱ��
	private TTextFormat start_date;
	// ����ʱ��
	private TTextFormat end_date;
	// ���
	private TTable table;

	public CLPDeathQueryControl() {

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
		String sql = " SELECT A.MR_NO,A.CASE_NO,A.PAT_NAME,CASE WHEN A.SEX = '1' THEN '��'  ELSE 'Ů' END AS SEX,"+
			         "  (SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = A.IN_DEPT) AS IN_DEPT ," +
			         "(SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = A.OUT_DEPT) AS OUT_DEPT," +
			         "S.STATION_DESC AS OUT_STATION,A.IN_DATE,A.OUT_DATE,REAL_STAY_DAYS ,"+
			         " B.MAINDIAG,C.ICD_CHN_DESC,M.CLNCPATH_CHN_DESC"+
			         " FROM MRO_RECORD A,ADM_INP B,SYS_DIAGNOSIS C,CLP_MANAGEM D,SYS_STATION S,CLP_BSCINFO M"+
			         " WHERE A.CASE_NO = B.CASE_NO"+
			         " AND A.OUT_STATION = S.STATION_CODE"+
			         " AND A.CODE1_STATUS = '4'"+//��ʾ��������
			         " AND B.MAINDIAG = C.ICD_CODE"+
			         " AND A.CASE_NO = D.CASE_NO"+
			         " AND M.CLNCPATH_CODE = D.CLNCPATH_CODE"+deptSelect+selectCondition;
		//System.out.println("sqllll:::"+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
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
			ExportExcelUtil.getInstance().exportExcel(table, " ����������ϸͳ�Ʊ���");
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
