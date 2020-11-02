package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import java.util.Vector;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.base.TComboBoxModel;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>
 * Title: �ٴ�·������ϸ��
 * </p>
 * 
 * <p>
 * Description: �ٴ�·������ϸ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author luhai
 * @version 1.0
 */
public class CLPDetailManageTotWordControl extends TControl {
	public CLPDetailManageTotWordControl() {

	}

	// ��ʼʱ��
	private TTextFormat start_date;
	
	// ����ʱ��
	private TTextFormat end_date;
	// ��ݱ�
	private TComboBox ctz1Code;
	// ����
	private TTextFormat deptCode;
	// ����
	private TTextField age;
	// ת���
	private TComboBox disCHCOde;
	// �������
	private TRadioButton isOP;
	// ���������
	private TRadioButton notOp;
	// ����
	private TTextFormat stationCode;
	// ���
	private TTable table;
	// �ٴ�·��
	private TTextFormat clncPathCode;
	public void onInit() {
		super.onInit();
		initPage();
		initControl();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void initControl() {
		start_date = (TTextFormat) this.getComponent("START_DATE");
		end_date = (TTextFormat) this.getComponent("END_DATE");
		table = (TTable) this.getComponent("CLPTABLE");
		// ��ݱ�
		ctz1Code = (TComboBox) this.getComponent("CTZ1_CODE");
		// ����
		deptCode = (TTextFormat) this.getComponent("DEPT_CODE");
		// ����
		age = (TTextField) this.getComponent("AGE");
		// ת���
		disCHCOde = (TComboBox) this.getComponent("DISCH_CODE");
		// �������
		isOP = (TRadioButton) this.getComponent("isOP");
		// ���������
		notOp = (TRadioButton) this.getComponent("notOP");
		// ����
		this.stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		//�ٴ�·��
		this.clncPathCode = (TTextFormat) this.getComponent("CLNCPATH_CODE");
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.callFunction("UI|CLPTABLE|removeRowAll");
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (this.table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}
		TParm prtParm = new TParm();
		// ��ͷ
		// prtParm.setData("TITLE","TEXT","�ٴ�·������ϸ��");
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "����Ժ��")
				+ "�ٴ�·������ϸ��");
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate)) {
			startDate = startDate.substring(0, (startDate.length() - 2));
		}
		if (this.checkNullAndEmpty(endDate)) {
			endDate = endDate.substring(0, (endDate.length() - 2));
		}
		prtParm.setData("START_DATE", "TEXT", startDate); // �Ʊ�ʱ��start
		prtParm.setData("END_DATE", "TEXT", endDate); // �Ʊ�ʱ��end
		TParm prtTableParm = this.getSelectTParm();
		prtTableParm.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");
		prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		prtTableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		// prtTableParm.addData("SYSTEM","COLUMNS","TOTAL");
		prtTableParm.addData("SYSTEM", "COLUMNS", "AVERAGECOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "REALCOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "COSEDIFF");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM", "COLUMNS", "REAL_STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS_DIFF");
		prtParm.setData("CLPTABLE", prtTableParm.getData());
		// ��β
		prtParm.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPNewDetailManagePrt.jhw", prtParm);
	}

	public void onQuery() {
		TParm parm = this.getSelectTParm();
		
		if (parm == null) {
			this.messageBox("��������!");
			table.removeRowAll();			
			return;			
		}
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		TParm tableparm = new TParm();
		StringBuffer sqlbf = new StringBuffer();
		String regionSelect="";
		// REGION ��������
		if (null!=Operator.getRegion()) {
			regionSelect = " AND AD.REGION_CODE='" + Operator.getRegion()
			+ "' ";
		}
		// ������ѯ����
		String stationSelect = "";
		if (null!=this.stationCode.getValue() && this.checkNullAndEmpty(this.stationCode.getValue().toString())) {
			stationSelect += " AND AD.DS_STATION_CODE ='"
					+ this.stationCode.getValue().toString() + "' ";
		}
		// �ٴ�·��--xiongwg20150508
		String clncPathSelect = "";
		if (null!=this.clncPathCode.getValue() && this.checkNullAndEmpty(this.clncPathCode.getValue().toString())) {
			clncPathSelect += " AND M.CLNCPATH_CODE ='"
					+ this.clncPathCode.getValue().toString() + "' ";
		}
		// �õ�ʱ���ѯ����
		String selectCondition = "";
		String admSelectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND AD.DS_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}
		// ��ݱ�
		String ctz1Code = this.ctz1Code.getValue();
		if (this.checkNullAndEmpty(ctz1Code)) {
			selectCondition += " AND MR.CTZ1_CODE='" + ctz1Code + "'";
		}
		// ����
		String age = this.age.getValue();
		if (this.validInt(age)) {
			selectCondition += " AND (TO_NUMBER(TO_CHAR(SYSDATE,'YYYY')) -TO_NUMBER(TO_CHAR((MR.BIRTH_DATE),'YYYY'))) >= "
					+ age + " ";
		}
		// ����
		String deptCode = this.deptCode.getComboValue();
		if (this.checkNullAndEmpty(deptCode)) {
			admSelectCondition += " AND AD.DS_DEPT_CODE='" + deptCode + "' ";
		}
		// ת���
		String disCHCOde = this.disCHCOde.getValue();
		if (this.checkNullAndEmpty(disCHCOde)) {
			admSelectCondition += " AND AD.DISCH_CODE='" + disCHCOde + "' ";
		}
		// ������str
		String opTableStr = "";
		// ��������������
		String opWhereStr = "";
		// �����ж�
		if (this.isOP.isSelected()) {
			opTableStr = ",MRO_RECORD_OP OP ";
			opWhereStr = " AND OP.CASE_NO=MR.CASE_NO AND OP.SEQ_NO IS NOT NULL ";
		}

		//====modify by caowl 20120910 start
		// �õ��ٴ�·����Ϣ
		String sql = "SELECT  R.REGION_CHN_ABN,ST.STATION_DESC,B.AVERAGECOST AS AVERAGECOST, "+
					       "  M.CLNCPATH_CODE,CASE WHEN MR.SUM_TOT IS NULL THEN 0  ELSE TO_NUMBER(MR.SUM_TOT) END AS REALCOST ,"+
					       "  CASE WHEN MR.SUM_TOT IS NULL THEN 0 ELSE TO_NUMBER(MR.SUM_TOT)-B.AVERAGECOST END AS COSEDIFF,D.DEPT_CHN_DESC,"+
					       "  B.CLNCPATH_CHN_DESC,B.STAYHOSP_DAYS,"+
					       "  CASE WHEN ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE)) <= 0 THEN 1 ELSE ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE)) END AS REAL_STAYHOSP_DAYS,"+
					       "  CASE WHEN ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE)) <= 0 THEN 1 ELSE ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE))-B.STAYHOSP_DAYS END  AS STAYHOSP_DAYS_DIFF,"+
					       "  AD.DEPT_CODE,AD.STATION_CODE,AD.MR_NO,AD.IPD_NO,M.CASE_NO,TO_CHAR(MR.OUT_DATE,'YYYY-MM-DD') AS OUT_DATE"+
					 " FROM ADM_INP AD,MRO_RECORD MR,SYS_DEPT D,SYS_STATION ST,CLP_BSCINFO B,SYS_REGION R, CLP_MANAGEM M"+opTableStr+
					 " WHERE AD.CASE_NO = MR.CASE_NO"+
					   "  AND AD.CASE_NO = M.CASE_NO"+
					   "  AND AD.REGION_CODE = R.REGION_CODE(+)"+
					   "  AND M.CLNCPATH_CODE = B.CLNCPATH_CODE"+
					   "  AND AD.DEPT_CODE = D.DEPT_CODE"+
					   "  AND AD.STATION_CODE = ST.STATION_CODE"+
					   "  AND MR.OUT_DATE IS NOT NULL AND M.DELETE_DTTM IS NULL ";
		sql += regionSelect;
		sql += selectCondition;
		sql += stationSelect;
		sql += clncPathSelect;
		sql += admSelectCondition;
		sql += opWhereStr;		
		sql += " ORDER BY AD.DEPT_CODE,AD.STATION_CODE,M.CLNCPATH_CODE";
		// ===modify by caowl 20120910 end		
		tableparm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (tableparm.getCount() <= 0) {
			
			return null;
		}
	   
		//TParm prtTableParm = new TParm();
		// �����ѯ������parm
		// ͳ����Ϣbegin
//		int total = 0;
//		double standardFee = 0.00;
//		double realFee = 0.00;
//		double coseDiff = 0.00;
//		DecimalFormat formatObject = new DecimalFormat("###########0.00");
//		int stayHospDay = 0; // ��׼סԺ����
//		int diffHospDay = 0; // ��������
//		int realStayHospDays=0;
//		// ͳ����Ϣend
		TParm selParm =null;
		String sql1=null;
//		//TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
		for (int i = 0; i < tableparm.getCount(); i++) {
			//TParm rowParm = tableparm.getRow(i);
			//======modify by caowl 20120910 start 
			sql1 = "SELECT END_DTTM FROM CLP_MANAGEM_HISTORY WHERE CASE_NO = '"+
			tableparm.getData("CASE_NO",i).toString()+"' AND END_DTTM IS NOT NULL";
			selParm = new TParm(TJDODBTool.getInstance().select(sql1));			
			if(selParm.getCount()>0){
				tableparm.setData("EXIT_CODE",i,"���");				
			}else{
				tableparm.setData("EXIT_CODE",i,"");				
			}
			//=======modify by caowl 20120910 end
//			total += rowParm.getInt("TOTAL");
//			standardFee += rowParm.getDouble("AVERAGECOST");
//			// ʵ�ʷ���
//			realFee += rowParm.getDouble("REALCOST");
//			// ����
//			coseDiff += rowParm.getDouble("COSEDIFF");
//			realStayHospDays+=rowParm.getInt("REAL_STAYHOSP_DAYS");
//			stayHospDay += rowParm.getInt("STAYHOSP_DAYS");
//			// ʵ��סԺ����
//			// ��������
//			diffHospDay += rowParm.getInt("STAYHOSP_DAYS_DIFF");
			
		}
//		// �����ܼ�
//		// wangzhilei 20120723 �޸�
//		tableparm.addData("REGION_CHN_ABN", "�ܼƣ�");
//		// wangzhilei 20120723 �޸�
//		tableparm.addData("CLNCPATH_CHN_DESC", "");
//		
//		tableparm.addData("TOTAL", total);
//		tableparm.addData("AVERAGECOST", formatObject.format(standardFee));
//		// ʵ�ʷ���
//		tableparm.addData("REALCOST", formatObject.format(realFee));
//		// ����
//		tableparm.addData("COSEDIFF", formatObject.format(coseDiff));
//		tableparm.addData("STAYHOSP_DAYS", stayHospDay);
//		// ʵ��סԺ����
//		tableparm.addData("REAL_STAYHOSP_DAYS", realStayHospDays);
//		// ��������
//		tableparm.addData("STAYHOSP_DAYS_DIFF", diffHospDay);
//		// ����
//		tableparm.addData("STATION_DESC", "");
//		// �ܼ�end
//		tableparm.setCount(tableparm.getCount()+1);
		return tableparm;
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		this.age.setValue("");
		this.ctz1Code.setValue("");
		this.deptCode.setValue("");
		this.disCHCOde.setValue("");
		this.stationCode.setValue("");
		this.clncPathCode.setValue("");
		table.removeRowAll();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·������ϸ��");
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

	/**
	 * ������֤����
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validInt(String validData) {
		Pattern p = Pattern.compile("([0]{1})|([1-9]{1}[0-9]*)");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

}
