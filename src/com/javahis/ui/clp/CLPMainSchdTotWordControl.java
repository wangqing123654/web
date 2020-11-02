package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �ٴ�·��ʱ�̱���
 * </p>
 * 
 * 
 * <p>
 * Description: �ٴ�·��ʱ�̱���
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 
 * @version 1.0
 */
public class CLPMainSchdTotWordControl  extends TControl{
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
		// ����
		deptCode = (TTextFormat) this.getComponent("DEPT_CODE");

		// ����
		this.stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		table = (TTable) this.getComponent("CLPTABLE");
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.callFunction("UI|CLPTABLE|removeRowAll");
		this.setValue("REGION_CODE", Operator.getRegion());
		// Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
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
		// prtParm.setData("TITLE","TEXT","�ٴ�·�������ܱ�");
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "����Ժ��")
				+ "�ٴ�·��ʱ���ܱ�");
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
		TParm prtTableParm = new TParm();
		TParm parm=table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			prtTableParm.addData("REGION_CHN_ABN", parm.getValue("REGION_CHN_ABN",i));
			prtTableParm.addData("DEPT_CHN_DESC", parm.getValue("DEPT_CHN_DESC",i));
			prtTableParm.addData("STATION_DESC", parm.getValue("STATION_DESC",i));
			prtTableParm.addData("CLNCPATH_CHN_DESC", parm.getValue("CLNCPATH_CHN_DESC",i));
			prtTableParm.addData("TOTAL", parm.getInt("TOTAL",i));
			prtTableParm.addData("AVERAGECOST",StringTool.round(parm.getDouble("AVERAGECOST",i), 2) );
			prtTableParm.addData("REALCOST", StringTool.round(parm.getDouble("REALCOST",i),2));
			prtTableParm.addData("COSEDIFF", StringTool.round(parm.getDouble("COSEDIFF",i),2));
			prtTableParm.addData("STAYHOSP_DAYS", parm.getInt("STAYHOSP_DAYS",i));
			prtTableParm.addData("REAL_STAYHOSP_DAYS", parm.getInt("REAL_STAYHOSP_DAYS",i));
			prtTableParm.addData("STAYHOSP_DAYS_DIFF", parm.getInt("STAYHOSP_DAYS_DIFF",i));
		}
		prtTableParm.setCount(parm.getCount());
		// wangzhilei 20120723 ���
		prtTableParm.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");
		// wangzhilei 20120723 ���
		prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "TOTAL");
		prtTableParm.addData("SYSTEM", "COLUMNS", "AVERAGECOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "REALCOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "COSEDIFF");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM","COLUMNS","REAL_STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS_DIFF");
		prtParm.setData("CLPTABLE", prtTableParm.getData());
		// ��β
		 prtParm.setData("NAME", "TEXT", Operator.getName());
		//prtParm.setData("NAME", "TEXT", "�����ˣ�" + Operator.getName());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPNewMainManagePrt.jhw", prtParm);
	}

	public void onQuery() {
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (!this.checkNullAndEmpty(startDate) || !this.checkNullAndEmpty(endDate)) {
			this.messageBox("������ͳ��ʱ��");
			return;
		}
		TParm parm = this.getSelectTParm();
		if (parm==null) {
			table.removeRowAll();
			return;
		}
		if (parm.getCount()<=0) {
			this.messageBox("��������");
			table.removeRowAll();
			return;
		}
		for (int i = 0; i <parm.getCount(); i++) {
			parm.setData("COSEDIFF",i,StringTool.round(parm.getDouble("SCHD_AMT",i)-parm.getDouble("TOT_AMT",i),2));
		}
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		String regionSelect="";
		// REGION ��������
		if (null!=Operator.getRegion()) {
			regionSelect = " AND P.REGION_CODE='" + Operator.getRegion()
			+ "' ";
		}
		if(((TRadioButton) this.getComponent("RDO_OWN")).isSelected()) {//pangben 2015-5-5����������
			regionSelect+=" AND S.MAIN_CTZ_FLG='Y' AND S.NHI_CTZ_FLG='N' ";
		}else if(((TRadioButton) this.getComponent("RDO_INS")).isSelected()) {//pangben 2015-5-5����������
			if (this.getValueString("SYS_CTZ").length()>0) {
				regionSelect+=" AND S.NHI_CTZ_FLG='Y' AND S.CTZ_CODE='"+this.getValueString("SYS_CTZ")+"' ";	
			}else{
				regionSelect+=" AND S.NHI_CTZ_FLG='Y' ";
			}
		}
		
		// ������ѯ����
		String stationSelect = "";
		if (null!=this.stationCode.getValue() && this.checkNullAndEmpty(this.stationCode.getValue().toString())) {
			stationSelect += " AND P.DS_STATION_CODE ='"
					+ this.stationCode.getValue().toString() + "' ";
		}
		// �õ�ʱ���ѯ����
		String selectCondition = "";
		String admSelectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND P.DS_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}
		// ����
		String deptCode = this.deptCode.getComboValue();
		if (this.checkNullAndEmpty(deptCode)) {
			admSelectCondition += " AND P.DS_DEPT_CODE='" + deptCode + "' ";
		}
		String clncPath = this.getValueString("CLNCPATH_CODE");
		if (this.checkNullAndEmpty(clncPath)) {
			admSelectCondition+=" AND P.CLNCPATH_CODE='"+clncPath+"' ";
		}
		String sql="SELECT  ST.STATION_DESC, M.CLNCPATH_CODE, D.DEPT_CHN_DESC,"+
         " SUM(CM.SCHD_AMT) SCHD_AMT, SUM(RD.TOT_AMT) TOT_AMT,RD.SCHD_CODE,RG.REGION_CHN_ABN AS REGION_CHN_DESC,"+
         " P.DEPT_CODE, P.STATION_CODE, DN.DURATION_CHN_DESC AS SCHD_NAME,BF.CLNCPATH_CHN_DESC FROM "+
         " CLP_THRPYSCHDM CM,ADM_INP P ,SYS_REGION RG,CLP_BSCINFO BF,SYS_DEPT D,SYS_STATION ST,"+
         "CLP_MANAGEM M,SYS_CTZ S, CLP_DURATION DN,("+
         "SELECT SUM(TOT_AMT) TOT_AMT,A.SCHD_CODE,A.CASE_NO,P.CLNCPATH_CODE  FROM IBS_ORDD A,ADM_INP P,SYS_CTZ S "+
         "WHERE A.CASE_NO=P.CASE_NO AND P.CTZ1_CODE=S.CTZ_CODE "+selectCondition+admSelectCondition+regionSelect+stationSelect+" AND A.SCHD_CODE IS NOT NULL AND P.CLNCPATH_CODE IS NOT NULL " +
         "GROUP BY A.SCHD_CODE,A.CASE_NO,P.CLNCPATH_CODE ) RD    "+
         "WHERE CM.SCHD_CODE=RD.SCHD_CODE  "+
         "AND RD.CLNCPATH_CODE=CM.CLNCPATH_CODE "+
         "AND P.CLNCPATH_CODE=CM.CLNCPATH_CODE   "+
         "AND P.REGION_CODE=RG.REGION_CODE "+
         "AND P.DEPT_CODE=D.DEPT_CODE(+) "+
         "AND P.CTZ1_CODE = S.CTZ_CODE "+
         "AND P.CLNCPATH_CODE = BF.CLNCPATH_CODE "+
         "AND P.STATION_CODE = ST.STATION_CODE(+) "+
         "AND P.CASE_NO=M.CASE_NO "+
         "AND P.CASE_NO=RD.CASE_NO "+
         "AND P.CLNCPATH_CODE = M.CLNCPATH_CODE "+
         "AND P.CLNCPATH_CODE =RD.CLNCPATH_CODE "+
         "AND M.DELETE_DTTM IS NULL "+
         "AND P.CLNCPATH_CODE IS NOT NULL "+
         "AND  CM.SCHD_CODE=DN.DURATION_CODE(+) AND P.CLNCPATH_CODE IS NOT NULL "+
         selectCondition+admSelectCondition+regionSelect+stationSelect+" GROUP BY ST.STATION_DESC, M.CLNCPATH_CODE, D.DEPT_CHN_DESC,"+
         "RD.SCHD_CODE,RG.REGION_CHN_ABN, P.DEPT_CODE, P.STATION_CODE, DN.DURATION_CHN_DESC,BF.CLNCPATH_CHN_DESC ORDER BY P.DEPT_CODE,P.STATION_CODE,M.CLNCPATH_CODE,RD.SCHD_CODE";
		TParm searchParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (searchParm.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
			return null;
		}
		return searchParm;
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
		table.removeRowAll();
		((TRadioButton) this.getComponent("RDO_SEL")).setSelected(true);
		onRdoSel();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·��ʱ���ܱ�");
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
	/**
	 * 
	* @Title: onRdoSel
	* @Description: TODO(������ݲ�ѯ����)
	* @author Dangzhang
	* @throws
	 */
	public void onRdoSel(){
		if(((TRadioButton) this.getComponent("RDO_SEL")).isSelected()) {
			callFunction("UI|SYS_CTZ|setEnabled", false);
		}else if(((TRadioButton) this.getComponent("RDO_OWN")).isSelected()) {//pangben 2015-5-5����������
			callFunction("UI|SYS_CTZ|setEnabled", false);
		}else if(((TRadioButton) this.getComponent("RDO_INS")).isSelected()) {//pangben 2015-5-5����������
			callFunction("UI|SYS_CTZ|setEnabled", true);
		}
		this.setValue("SYS_CTZ", "");
	}
}
