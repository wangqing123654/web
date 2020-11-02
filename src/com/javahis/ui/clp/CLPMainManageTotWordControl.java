package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;

import jdo.opd.TotQtyTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.base.TComboBoxModel;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;
import java.util.Vector;

/**
 * <p>
 * Title: �ٴ�·�������ܱ�
 * </p>
 * 
 * 
 * <p>
 * Description: �ٴ�·�������ܱ�
 * </p>
 * 
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
public class CLPMainManageTotWordControl extends TControl {
	public CLPMainManageTotWordControl() {

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
	// �ٴ�·��--xiongwg20150508
	private TTextFormat clncPathCode;

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
		// �ٴ�·��--xiongwg20150508
		this.clncPathCode = (TTextFormat) this.getComponent("CLNCPATH_CODE");
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
				+ "�ٴ�·�������ܱ�");
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
		if (parm == null) {
			this.messageBox("��������");
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
		// �õ��ٴ�·����Ϣ
		sqlbf
				.append(
						"SELECT R.REGION_CHN_ABN, ST.STATION_DESC, B.AVERAGECOST AS AVERAGECOST,M.CLNCPATH_CODE,")
				.append(
						"CASE WHEN SUM (mr.sum_tot) IS NULL THEN 0 ELSE SUM (mr.sum_tot) END AS REALCOST ,(SUM (B.AVERAGECOST)-CASE WHEN SUM (mr.sum_tot) IS NULL THEN 0 ELSE SUM (mr.sum_tot) END) AS COSEDIFF , D.DEPT_CHN_DESC,")
				.append(
						"B.CLNCPATH_CHN_DESC,B.STAYHOSP_DAYS, 0 AS REAL_STAYHOSP_DAYS, 0 AS TOTAL, 0 AS STAYHOSP_DAYS_DIFF,AD.DEPT_CODE,AD.STATION_CODE")
				.append(
						" FROM ADM_INP AD,MRO_RECORD MR,SYS_DEPT D,SYS_STATION ST,CLP_BSCINFO B,SYS_REGION R,CLP_MANAGEM M,SYS_CTZ S ")
				.append(
						" WHERE AD.CASE_NO = MR.CASE_NO AND AD.CASE_NO=M.CASE_NO AND AD.REGION_CODE=R.REGION_CODE(+)")
				.append(
						" AND M.CLNCPATH_CODE = B.CLNCPATH_CODE AND AD.DEPT_CODE = D.DEPT_CODE AND AD.CTZ1_CODE=S.CTZ_CODE ")
				.append(
						" AND AD.STATION_CODE = ST.STATION_CODE AND MR.OUT_DATE IS NOT NULL AND M.DELETE_DTTM IS NULL")
				.append(regionSelect)
				.append(selectCondition)
				.append(admSelectCondition)
				.append(stationSelect)
				.append(clncPathSelect)// �ٴ�·��--xiongwg20150508
				.append(opWhereStr)
				.append(
						" GROUP BY ST.STATION_DESC,D.DEPT_CHN_DESC,M.CLNCPATH_CODE,B.AVERAGECOST,")
				.append(
						" B.CLNCPATH_CHN_DESC,R.REGION_CHN_ABN,AD.DEPT_CODE,AD.STATION_CODE,B.STAYHOSP_DAYS")
				.append(
						" ORDER BY AD.DEPT_CODE,AD.STATION_CODE,M.CLNCPATH_CODE");

		tableparm = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
		StringBuffer sqlbfs = new StringBuffer();
		// �õ�סԺ����סԺ����
		sqlbfs
				.append(
						"SELECT SUM(CASE WHEN ROUND(TO_NUMBER(MR.OUT_DATE-MR.IN_DATE))<=0 THEN 1 ELSE ROUND(TO_NUMBER(MR.OUT_DATE-MR.IN_DATE)) END) AS REAL_STAYHOSP_DAYS, ")
				.append(
						"AD.STATION_CODE,AD.DEPT_CODE,M.CLNCPATH_CODE,COUNT (*) AS TOTAL ")
				.append(
						" FROM ADM_INP AD, MRO_RECORD MR,CLP_MANAGEM M,SYS_CTZ S ")
				.append(opTableStr)
				.append(
						" WHERE MR.CASE_NO = AD.CASE_NO AND AD.CTZ1_CODE=S.CTZ_CODE AND AD.CASE_NO=M.CASE_NO AND MR.OUT_DATE IS NOT NULL AND M.DELETE_DTTM IS NULL")
				.append(opWhereStr)
				.append(regionSelect)
				.append(selectCondition)
				.append(stationSelect)
				.append(clncPathSelect)// �ٴ�·��--xiongwg20150508
				.append(admSelectCondition)
				.append(
						" GROUP BY AD.STATION_CODE,AD.DEPT_CODE,M.CLNCPATH_CODE ")
				.append(
						" ORDER BY AD.DEPT_CODE,AD.STATION_CODE,M.CLNCPATH_CODE");
	
		TParm tableparms = new TParm(TJDODBTool.getInstance().select(sqlbfs.toString()));
//		System.out.println("sqlbf::::"+sqlbf);
//		System.out.println("sqlbfs::::"+sqlbfs);
		if (tableparm.getErrCode()<0 || tableparm.getCount() <= 0) {
			return null;
		}
		if (tableparms.getErrCode()<0) {
			return null;
		}
		
		for (int i = 0; i < tableparm.getCount(); i++) {
			String deptCodeD=tableparm.getValue("DEPT_CODE",i);
        	String stationCode=tableparm.getValue("STATION_CODE",i);
        	String clncpathCode=tableparm.getValue("CLNCPATH_CODE",i);
        	for (int j = 0; j < tableparms.getCount(); j++) {
        		String deptCodeS=tableparms.getValue("DEPT_CODE",j);
            	String stationCodeS=tableparms.getValue("STATION_CODE",j);
            	String clncpathCodeS=tableparms.getValue("CLNCPATH_CODE",j);
            	if (deptCodeD.equals(deptCodeS) && stationCode.equals(stationCodeS)&& clncpathCode.equals(clncpathCodeS)) {    
            		int stayHosp_days_temp = tableparm.getInt("STAYHOSP_DAYS",i);
            		tableparm.setData("REALCOST",i,(tableparm.getDouble("REALCOST",i))/tableparms.getInt("TOTAL",j));//20130723 yanjing modify ʵ�ʷ���
            		tableparm.setData("COSEDIFF",i,tableparm.getDouble("REALCOST",i)-tableparm.getDouble("AVERAGECOST",i));//20130723 yanjing ���ò��죨ʵ�ʷ���-��׼���ã�
//            		tableparm.setData("STAYHOSP_DAYS",i,stayHosp_days_temp*tableparms.getInt("TOTAL",j));//��׼����
            		tableparm.setData("STAYHOSP_DAYS",i,stayHosp_days_temp);//��׼����
            		tableparm.setData("REAL_STAYHOSP_DAYS",i,tableparms.getInt("REAL_STAYHOSP_DAYS",j)/tableparms.getInt("TOTAL",j));//סԺ����
            		tableparm.setData("TOTAL",i,tableparms.getInt("TOTAL",j));//·���˴�            		
//            		tableparm.setData("STAYHOSP_DAYS_DIFF",i,stayHosp_days_temp*tableparms.getInt("TOTAL",j)-tableparms.getInt("REAL_STAYHOSP_DAYS",j));//��������     
            		tableparm.setData("STAYHOSP_DAYS_DIFF",i,tableparm.getInt("REAL_STAYHOSP_DAYS",j)-
            				stayHosp_days_temp);//��������  yanjing 20130722(ʵ������-��׼����)
				}
			}
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
		// ͳ����Ϣend
//		//TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
//		for (int i = 0; i < tableparm.getCount(); i++) {
//			TParm rowParm = tableparm.getRow(i);
//			total += rowParm.getInt("TOTAL");
//			standardFee += rowParm.getDouble("AVERAGECOST");
//			// ʵ�ʷ���
//			realFee += rowParm.getDouble("REALCOST");
//			// ����
//			coseDiff += rowParm.getDouble("COSEDIFF");
//			stayHospDay += rowParm.getInt("STAYHOSP_DAYS");
//			realStayHospDays+=rowParm.getInt("REAL_STAYHOSP_DAYS");
//			// ʵ��סԺ����
//			// ��������
//			diffHospDay += rowParm.getInt("STAYHOSP_DAYS_DIFF");
//		}
//		// �����ܼ�
//		// wangzhilei 20120723 �޸�
//		tableparm.addData("REGION_CHN_ABN", "�ܼƣ�");
//		// wangzhilei 20120723 �޸�
//		tableparm.addData("CLNCPATH_CHN_DESC", "");
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
		((TRadioButton) this.getComponent("RDO_SEL")).setSelected(true);
		onRdoSel();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·�������ܱ�");
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
