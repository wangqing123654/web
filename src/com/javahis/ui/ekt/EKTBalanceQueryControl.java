package com.javahis.ui.ekt;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.Date;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �������ѯ
 * </p>
 * 
 * <p>
 * Description: �������ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20110707
 * @version 1.0
 */
public class EKTBalanceQueryControl extends TControl {
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
		// Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	private TTable table;

	public EKTBalanceQueryControl() {
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		boolean flg = this.getValueBoolean("TABLE_TYPE1");//�Ƿ������Һ�
		if(flg){
			onQueryTable();//��
		}else{
			this.onChangeTable();//��
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");
		this.setValue("REGION_CODE", Operator.getRegion());
		// ��ʼ����ѯ����
		this.setValue("DATE_E",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("DATE_S", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// �󶨿ؼ��¼�
		callFunction("UI|MR_NO|addEventListener", "MR_NO->"
				+ TKeyListener.KEY_RELEASED, this, "onKeyReleased");

	}

	public void onKeyReleased(KeyEvent e) {
		if (e.getKeyCode() != 10) {
			return;
		}
		TTextField mrNO = (TTextField) this.getComponent("MR_NO");
		mrNO.setValue(PatTool.getInstance().checkMrno(mrNO.getValue()));
		mrNO.setFocusable(true);
		this.onQuery();
	}

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("��鿴ҽ�ƿ��Ƿ���ȷʹ��");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("MR_NO", parm.getValue("MR_NO"));
		onQueryMrno();
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ӡ������");
			return;
		}

		// ========================================================================================

		TParm parmValue = new TParm();
		String url = "";
		boolean flg = this.getValueBoolean("TABLE_TYPE1");
		if(flg){
			for (int i = 0; i < parm.getCount(); i++) {
				parmValue.addData("APPLY_DATE", parm.getValue("APPLY_DATE", i));
				parmValue.addData("APPLY_USER", parm.getValue("APPLY_USER", i));
				parmValue.addData("APPLY_AMT", parm.getValue("APPLY_AMT", i));
				parmValue.addData("APPLY_CAUSE", parm.getValue("APPLY_CAUSE", i));
				parmValue.addData("APPROVE_USER", parm.getValue("APPROVE_USER", i));
				parmValue.addData("APPROVE_DATE", parm.getValue("APPROVE_DATE", i));
				parmValue.addData("APPROVE_AMT", parm.getValue("APPROVE_AMT", i));
				parmValue.addData("PAY_OTHER", parm.getValue("PAY_OTHER", i));
				parmValue.addData("GREEN_BALANCE",
						parm.getValue("GREEN_BALANCE", i));
				parmValue.addData("ADM_TYPE_DESC",
						parm.getValue("ADM_TYPE_DESC", i));
				parmValue.addData("DEPT_ABS_DESC",
						parm.getValue("DEPT_ABS_DESC", i));
				parmValue.addData("REALDR_CODE", parm.getValue("REALDR_CODE", i));
				parmValue.addData("MR_NO", parm.getValue("MR_NO", i));
				parmValue.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				parmValue.addData("CASE_NO", parm.getValue("CASE_NO", i));
				parmValue.addData("ADM_DATE", parm.getValue("ADM_DATE", i));

			}
			parmValue.addData("SYSTEM", "COLUMNS", "APPLY_DATE");
			parmValue.addData("SYSTEM", "COLUMNS", "APPLY_USER");
			parmValue.addData("SYSTEM", "COLUMNS", "APPLY_AMT");
			parmValue.addData("SYSTEM", "COLUMNS", "APPLY_CAUSE");
			parmValue.addData("SYSTEM", "COLUMNS", "APPROVE_USER");
			parmValue.addData("SYSTEM", "COLUMNS", "APPROVE_DATE");
			parmValue.addData("SYSTEM", "COLUMNS", "APPROVE_AMT");
			parmValue.addData("SYSTEM", "COLUMNS", "PAY_OTHER");
			parmValue.addData("SYSTEM", "COLUMNS", "GREEN_BALANCE");
			parmValue.addData("SYSTEM", "COLUMNS", "ADM_TYPE_DESC");
			parmValue.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
			parmValue.addData("SYSTEM", "COLUMNS", "REALDR_CODE");
			parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
			parmValue.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parmValue.addData("SYSTEM", "COLUMNS", "CASE_NO");
			parmValue.addData("SYSTEM", "COLUMNS", "ADM_DATE");
			url = "%ROOT%\\config\\prt\\EKT\\EKTBalance.jhw";
		}else{
//			MR_NO;PAT_NAME;ADM_DATE;APPLY_USER;REG_AR_AMT;OPB_AR_AMT;SUM;DEPT_CHN_DESC;USER_NAME
			for (int i = 0; i < parm.getCount("MR_NO"); i++) {
				parmValue.addData("MR_NO", parm.getValue("MR_NO", i));
				parmValue.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				parmValue.addData("ADM_DATE", parm.getValue("ADM_DATE", i)
						.equals("")
						|| parm.getValue("ADM_DATE", i) == null ? "" : parm
						.getValue("ADM_DATE", i).substring(0, 10));
				parmValue.addData("APPLY_USER", parm.getValue("APPLY_USER", i));
				parmValue.addData("REG_AR_AMT", parm.getValue("REG_AR_AMT", i));
				parmValue.addData("OPB_AR_AMT", parm.getValue("OPB_AR_AMT", i));
				parmValue.addData("SUM", parm.getValue("SUM", i));
				parmValue.addData("DEPT_CHN_DESC", parm.getValue("DEPT_CHN_DESC", i));
				parmValue.addData("USER_NAME", parm.getValue("USER_NAME", i));
			}
			parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
			parmValue.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parmValue.addData("SYSTEM", "COLUMNS", "ADM_DATE");
			parmValue.addData("SYSTEM", "COLUMNS", "APPLY_USER");
			parmValue.addData("SYSTEM", "COLUMNS", "REG_AR_AMT");
			parmValue.addData("SYSTEM", "COLUMNS", "OPB_AR_AMT");
			parmValue.addData("SYSTEM", "COLUMNS", "SUM");
			parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
			parmValue.addData("SYSTEM", "COLUMNS", "USER_NAME");
			url = "%ROOT%\\config\\prt\\EKT\\EKTGreenPath.jhw";
		}
		parmValue.setCount(parm.getCount("MR_NO"));
		TParm result = new TParm();
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("DATE_S")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("DATE_E")), "yyyy/MM/dd");
		result.setData("DATE", "TEXT", sDate + " �� " + eDate);// ͳ��
		result.setData("T1", parmValue.getData());
		result.setData(
				"TITLE",
				"TEXT",
				(null != Operator.getHospitalCHNFullName() ? Operator
						.getHospitalCHNFullName() : "����Ժ��") + "�������ѯ");
		// ¬�������Ʊ���
		// ��β
		result.setData("OPT_USER", "TEXT", Operator.getName());
		this.openPrintWindow(url, result);

		// ========================================================================================
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		this.setValue("MR_NO", "");
		this.setValue("OPT_USER", "");
		this.setValue("PAT_NAME", "");
		this.setValue("ADM_TYPE", "");
		TParm parm = new TParm();
		table.setParmValue(parm);
		initPage();
		TRadioButton type= (TRadioButton) this.getComponent("TABLE_TYPE1");
		type.setSelected(true);
//		table.removeRowAll();

	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getParmValue();
		int parmc = parm.getCount("MR_NO");
		int parmN = parm.getCount("PAT_NAME");
		int parmm = parm.getCount("OTHER");
		int parmCC = parm.getCount();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�������ѯ��");
	}

	/**
	 * ��ѯ������
	 */
	public void onQueryMrno() {
		Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		setValue("PAT_NAME", pat.getName());
		this.onQuery();
	}

	/**
	 * ���ı������Ϊ�ڶ���
	 */
	public void onChangeTable() {
		// ���ô��ť������
//		this.callFunction("UI|print|setEnabled", false);
		// ���ò�ѯ����
		String date_s = getValueString("DATE_S");
		String date_e = getValueString("DATE_E");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String region = "";
		if (null != this.getValueString("REGION_CODE")
				&& this.getValueString("REGION_CODE").length() > 0) {
			region = " AND A.REGION_CODE = '" + getValueString("REGION_CODE") + "'";
		}
		
		
		// ====zhangp 20120814 start  G.USER_NAME APPLY_USER,
		String regaccntSql = 
			" SELECT  A.CASE_NO, A.MR_NO,C.PAT_NAME, B.ADM_DATE, A.OTHER_FEE1 REG_AR_AMT, E.DEPT_CHN_DESC," +
			" F.USER_NAME" +
			" FROM BIL_REG_RECP A," +
			" REG_PATADM B," +
			" SYS_PATINFO C," +
//			" (SELECT DISTINCT  CASE_NO, APPLY_USER,ADM_TYPE" +
//			" FROM EKT_GREEN_PATH" +
//			" GROUP BY CASE_NO, APPLY_USER,ADM_TYPE) D," +
			" SYS_DEPT E," +
			" SYS_OPERATOR F "+
//			" SYS_OPERATOR G" +
			" WHERE A.CASE_NO = B.CASE_NO(+)" +
			" AND A.MR_NO = C.MR_NO" +
//			" AND A.CASE_NO = D.CASE_NO(+)" +
			" AND B.REALDEPT_CODE = E.DEPT_CODE(+)" +
			" AND B.REALDR_CODE = F.USER_ID(+)" +
//			" AND D.APPLY_USER = G.USER_ID(+)" +
			region +
			" AND A.ACCOUNT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')" +
			" AND A.OTHER_FEE1 <> 0" +
			" ORDER BY A.CASE_NO" ;
//		System.out.println("regaccntSql==="+regaccntSql);
		TParm regResult = new TParm(TJDODBTool.getInstance().select(regaccntSql));
		if (regResult.getErrCode() < 0) {
			return;
		}
		String opbaccntSql = 
			" SELECT  A.CASE_NO, A.MR_NO,C.PAT_NAME, B.ADM_DATE,  A.PAY_OTHER1 OPB_AR_AMT, E.DEPT_CHN_DESC," +
			" F.USER_NAME" +
			" FROM BIL_OPB_RECP A," +
			" REG_PATADM B," +
			" SYS_PATINFO C," +
//			" (SELECT DISTINCT CASE_NO, APPLY_USER,ADM_TYPE" +
//			" FROM EKT_GREEN_PATH" +
//			" GROUP BY CASE_NO, APPLY_USER,ADM_TYPE) D," +
			" SYS_DEPT E," +
			" SYS_OPERATOR F " +
//			" SYS_OPERATOR G" +
			" WHERE A.CASE_NO = B.CASE_NO(+)" +
			" AND A.MR_NO = C.MR_NO" +
//			" AND A.CASE_NO = D.CASE_NO(+)" +
			" AND B.REALDEPT_CODE = E.DEPT_CODE(+)" +
			" AND B.REALDR_CODE = F.USER_ID(+)" +
//			" AND D.APPLY_USER = G.USER_ID(+)" +
			region +
			" AND A.ACCOUNT_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')" +
			" AND A.PAY_OTHER1 <> 0" +
			" ORDER BY A.CASE_NO" ;
//		System.out.println("opbaccntSql====="+opbaccntSql);
		TParm opbResult = new TParm(TJDODBTool.getInstance().select(opbaccntSql));
		if (opbResult.getErrCode() < 0) {
			return;
		}
		
		//====huangtt 20131014 start
		String ektSql="SELECT  D.CASE_NO, D.ADM_TYPE, G.USER_NAME APPLY_USER FROM EKT_GREEN_PATH D,SYS_OPERATOR G" +
				" WHERE D.APPLY_USER=G.USER_ID" +
				" GROUP BY D.CASE_NO, D.ADM_TYPE, G.USER_NAME" +
				" ORDER BY D.CASE_NO";
		TParm ektResult = new TParm(TJDODBTool.getInstance().select(ektSql));
		TParm regParm = new TParm();
		for(int i=0;i<regResult.getCount("CASE_NO");i++){
			String caseNo = regResult.getValue("CASE_NO", i);
			TParm ektParm = new TParm();
			int ekt = 0;
			for(int j=0;j<ektResult.getCount("CASE_NO");j++){
				if(ektResult.getValue("CASE_NO", j).equals(caseNo)){
					ektParm.addData("CASE_NO", ektResult.getData("CASE_NO", j));
					ektParm.addData("APPLY_USER", ektResult.getData("APPLY_USER", j));
				}
			}
			if(ektParm.getCount("CASE_NO")>0){

				for(int k=0;k<ektParm.getCount("CASE_NO");k++){
					regParm.addData("CASE_NO", regResult.getData("CASE_NO", i));
					regParm.addData("MR_NO", regResult.getData("MR_NO", i));
					regParm.addData("PAT_NAME", regResult.getData("PAT_NAME", i));
					regParm.addData("ADM_DATE", regResult.getData("ADM_DATE", i));
					regParm.addData("APPLY_USER", ektParm.getData("APPLY_USER", k));
					if(k>0){
						regParm.addData("REG_AR_AMT", 0);
					}else{
						regParm.addData("REG_AR_AMT", regResult.getData("REG_AR_AMT", i));
					}
					
					regParm.addData("DEPT_CHN_DESC", regResult.getData("DEPT_CHN_DESC", i));
					regParm.addData("USER_NAME", regResult.getData("USER_NAME", i));
				}
	
			}

		}
		

		TParm opbParm = new TParm();
		for(int i=0;i<opbResult.getCount("CASE_NO");i++){
			String caseNo = opbResult.getValue("CASE_NO", i);
			TParm ektParm = new TParm();
			int ekt = 0;
			for(int j=0;j<ektResult.getCount();j++){
				if(ektResult.getValue("CASE_NO", j).equals(caseNo)){
					ektParm.addData("CASE_NO", ektResult.getData("CASE_NO", j));
					ektParm.addData("APPLY_USER", ektResult.getData("APPLY_USER", j));
				}
			}
			if(ektParm.getCount("CASE_NO")>0){

				for(int k=0;k<ektParm.getCount("CASE_NO");k++){
					opbParm.addData("CASE_NO", opbResult.getData("CASE_NO", i));
					opbParm.addData("MR_NO", opbResult.getData("MR_NO", i));
					opbParm.addData("PAT_NAME", opbResult.getData("PAT_NAME", i));
					opbParm.addData("ADM_DATE", opbResult.getData("ADM_DATE", i));
					opbParm.addData("APPLY_USER", ektParm.getData("APPLY_USER", k));
					if(k>0){
						opbParm.addData("OPB_AR_AMT", 0);
					}else{
						opbParm.addData("OPB_AR_AMT", opbResult.getData("OPB_AR_AMT", i));
					}
					
					opbParm.addData("DEPT_CHN_DESC", opbResult.getData("DEPT_CHN_DESC", i));
					opbParm.addData("USER_NAME", opbResult.getData("USER_NAME", i));
				}
	
			}
		
		}
		regResult = regParm;
		opbResult = opbParm;
		//====huangtt 20131014 end
		
		TParm result = doParm(regResult, opbResult);
		table.setHeader("������,100;��������,100;��������,100;������,80;�Һŷ�,80,double,########0.00;�����շ�,100,double,########0.00;�ϼ�,100,double,########0.00;�������,100;����ҽʦ,80");
		table.setParmMap("MR_NO;PAT_NAME;ADM_DATE;APPLY_USER;REG_AR_AMT;OPB_AR_AMT;SUM;DEPT_CHN_DESC;USER_NAME");
		table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,right;5,right;6,right;7,left;8,left");
		table.setParmValue(result);
	}
	
	/**
	 * ����Һź���������
	 * ====zhangp 20120814
	 * @param regParm
	 * @param opbParm
	 * @return
	 */
	public TParm doParm(TParm regParm,TParm opbParm){
		for (int i = 0; i < regParm.getCount("MR_NO"); i++) {
			regParm.addData("OPB_AR_AMT", 0);
			regParm.addData("FLG", "Y");//��ʾΪδ��������
			for (int j = 0; j < opbParm.getCount("MR_NO"); j++) {
				if(regParm.getData("MR_NO", i).equals(opbParm.getData("MR_NO", j)) && regParm.getData("CASE_NO", i).equals(opbParm.getData("CASE_NO", j)) && regParm.getBoolean("FLG", i)){
					regParm.setData("OPB_AR_AMT", i, opbParm.getData("OPB_AR_AMT", j));
					regParm.setData("FLG", i, "N");//��δ����ע�Ǹ�ΪN
					opbParm.removeRow(j);
				}
			}
		}
		for (int i = 0; i < opbParm.getCount("MR_NO"); i++) {
			regParm.addData("MR_NO", opbParm.getData("MR_NO", i));
			regParm.addData("PAT_NAME", opbParm.getData("PAT_NAME", i));
			regParm.addData("ADM_DATE", opbParm.getData("ADM_DATE", i));
			regParm.addData("APPLY_USER", opbParm.getData("APPLY_USER", i));
			regParm.addData("REG_AR_AMT", 0);
			regParm.addData("OPB_AR_AMT", opbParm.getData("OPB_AR_AMT", i));
			regParm.addData("DEPT_CHN_DESC", opbParm.getData("DEPT_CHN_DESC", i));
			regParm.addData("USER_NAME", opbParm.getData("USER_NAME", i));
		}
		double reg = 0;
		double opb = 0;
		double sum = 0;
		for (int i = 0; i < regParm.getCount("MR_NO"); i++) {
			regParm.addData("SUM", regParm.getDouble("REG_AR_AMT", i) + regParm.getDouble("OPB_AR_AMT", i));
			reg += regParm.getDouble("REG_AR_AMT", i);
			opb += regParm.getDouble("OPB_AR_AMT", i);
			sum += regParm.getDouble("SUM", i);
		}
		regParm.addData("MR_NO", "");
		regParm.addData("PAT_NAME", "");
		regParm.addData("ADM_DATE", "");
		regParm.addData("APPLY_USER", "�ϼ�");
		regParm.addData("REG_AR_AMT", reg);
		regParm.addData("OPB_AR_AMT", opb);
		regParm.addData("DEPT_CHN_DESC", "");
		regParm.addData("USER_NAME", "");
		regParm.addData("SUM", sum);
		return regParm;
	}
	/**
	 * ��һ�����
	 */
	public void onQueryTable(){
		// ���ô�ӡ��ť����
		this.callFunction("UI|print|setEnabled", true);
		// ���ñ���ͷ
		table.setHeader("��������,80;������,70;������,80,double,########0.00;����ԭ��,60;"
				+ "������,70;����ʱ��,80;�������,80,double,########0.00;���׽��,80,double,########0.00;"
				+ "���,80,double,########0.00;�ż���,50;����,60;����ҽʦ,60;������,100;��������,80;"
				+ "�����,100;����ʱ��,80");
		table.setParmMap("APPLY_DATE;APPLY_USER;APPLY_AMT;APPLY_CAUSE;APPROVE_USER;"
				+ "APPROVE_DATE;APPROVE_AMT;PAY_OTHER;GREEN_BALANCE;ADM_TYPE_DESC;"
				+ "DEPT_ABS_DESC;REALDR_CODE;MR_NO;PAT_NAME;CASE_NO;ADM_DATE");
		table.setColumnHorizontalAlignmentData("1,left;2,right;3,left;4,left;5,left;6,right;7,right;8,right;"
				+ "9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left");
		TParm parm = new TParm();
		String date_s = getValueString("DATE_S");
		String date_e = getValueString("DATE_E");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		if (null != this.getValueString("REGION_CODE")
				&& this.getValueString("REGION_CODE").length() > 0)
			parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
		parm.setData("DATE_S", date_s);
		parm.setData("DATE_E", date_e);
		// ====zhangp 20120615 start
		String accntSql = "SELECT DISTINCT ACCOUNT_SEQ FROM BIL_OPB_RECP WHERE ACCOUNT_DATE BETWEEN TO_DATE('"
				+ date_s
				+ "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"
				+ date_e
				+ "','YYYYMMDDHH24MISS') AND PAY_OTHER1 <>0";
		TParm temp = new TParm(TJDODBTool.getInstance().select(accntSql));
		if (temp.getErrCode() < 0) {
			return;
		}
		// if (temp.getCount() < 0) {
		// messageBox("��������");
		// onClear();
		// return;
		// }
		String account_seqs = "";
		if (temp.getCount() > 0) {
			for (int i = 0; i < temp.getCount(); i++) {
				account_seqs += ",'" + temp.getData("ACCOUNT_SEQ", i) + "'";
			}
			account_seqs = account_seqs.substring(1, account_seqs.length());
		}
		if (account_seqs.equals("")) {
			account_seqs = "''";
		}
		String regaccntSql = "SELECT DISTINCT ACCOUNT_SEQ FROM BIL_REG_RECP WHERE ACCOUNT_DATE BETWEEN TO_DATE('"
				+ date_s
				+ "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"
				+ date_e
				+ "','YYYYMMDDHH24MISS') AND OTHER_FEE1 <>0";
		TParm regtemp = new TParm(TJDODBTool.getInstance().select(regaccntSql));
		if (regtemp.getErrCode() < 0) {
			return;
		}
		String regaccount_seqs = "";
		if (regtemp.getCount() > 0) {
			for (int i = 0; i < regtemp.getCount(); i++) {
				regaccount_seqs += ",'" + regtemp.getData("ACCOUNT_SEQ", i)
						+ "'";
			}
			regaccount_seqs = regaccount_seqs.substring(1,
					regaccount_seqs.length());
		}
		if (regaccount_seqs.equals("")) {
			regaccount_seqs = "''";
		}
		// ====zhangp 20120615 end
		// TParm result =
		// CLPOverPersonManagerTool.getInstance().selectData("selectOverflowCase",parm);
		StringBuffer sqlbf = new StringBuffer();
		// sqlbf.append("SELECT A.CASE_NO,CASE WHEN A.ADM_TYPE='E' THEN '����' WHEN A.ADM_TYPE='O' THEN '����' WHEN A.ADM_TYPE='I' THEN 'סԺ' WHEN A.ADM_TYPE='H' THEN '����'  end AS ADM_TYPE_DESC ,");
		// sqlbf.append(" B.PAT_NAME ,A.ADM_DATE,A.OPT_USER,C.USER_NAME AS OPT_USER_DESC,A.GREEN_BALANCE,A.GREEN_PATH_TOTAL,A.MR_NO ");
		// sqlbf.append("  FROM REG_PATADM A,SYS_PATINFO B,SYS_OPERATOR C  WHERE A.MR_NO=B.MR_NO(+) AND A.OPT_USER=C.USER_ID(+)  ");
		// sqlbf.append(" AND (A.GREEN_BALANCE>0  or GREEN_PATH_TOTAL>0) ");
		sqlbf.append("SELECT   A.CASE_NO,"
				+ " CASE"
				+ " WHEN A.ADM_TYPE = 'E'"
				+ " THEN '����'"
				+ " WHEN A.ADM_TYPE = 'O'"
				+ " THEN '����'"
				+ " WHEN A.ADM_TYPE = 'I'"
				+ " THEN 'סԺ'"
				+ " WHEN A.ADM_TYPE = 'H'"
				+ " THEN '����'"
				+ " END AS ADM_TYPE_DESC,"
				+ " B.PAT_NAME, A.MR_NO, A.ADM_DATE, I.USER_NAME AS REALDR_CODE,"
				+ " J.DEPT_ABS_DESC, A.GREEN_BALANCE,"
				+ " (SUM (G.PAY_OTHER1) + SUM (F.OTHER_FEE1)) PAY_OTHER,"
				+ " TO_DATE (D.APPLY_DATE, 'YYYYMMDDHH24MISS') AS APPLY_DATE,"
				+ " D.APPLY_AMT, C.USER_NAME AS APPLY_USER, E.CHN_DESC AS APPLY_CAUSE,"
				+ " D.APPROVE_DATE, H.USER_NAME AS APPROVE_USER, D.APPROVE_AMT"
				+ " FROM REG_PATADM A,"
				+ " SYS_PATINFO B,"
				+ " SYS_OPERATOR C,"
				+ " EKT_GREEN_PATH D,"
				+ " (SELECT ID, CHN_DESC"
				+ " FROM SYS_DICTIONARY"
				+ " WHERE GROUP_ID = 'APPLY_CAUSE') E,"
				+ " BIL_REG_RECP F,"
				+
				// ===zhangp 20120716 start
				// " BIL_OPB_RECP G," +
				" (SELECT CASE_NO,ACCOUNT_SEQ,SUM(PAY_OTHER1) PAY_OTHER1 FROM BIL_OPB_RECP WHERE ACCOUNT_SEQ IN ("
				+ account_seqs
				+ ") GROUP BY CASE_NO,ACCOUNT_SEQ) G,"
				+
				// ===zhangp 20120716 end
				" SYS_OPERATOR H," + " SYS_OPERATOR I," + " SYS_DEPT J"
				+ " WHERE A.MR_NO = B.MR_NO "
				+ " AND D.APPLY_USER = C.USER_ID "
				+ " AND A.CASE_NO = D.CASE_NO "
				+ " AND D.APPLY_CAUSE = E.ID (+)"
				+ " AND D.APPROVE_USER = H.USER_ID "
				+ " AND A.REALDR_CODE = I.USER_ID "
				+ " AND A.REALDEPT_CODE = J.DEPT_CODE "
				+ " AND A.CASE_NO = F.CASE_NO (+)"
				+ " AND A.CASE_NO = G.CASE_NO (+)"
				+ " AND (A.GREEN_BALANCE > 0 OR GREEN_PATH_TOTAL > 0)");
		// ===zhangp 20120618 start
		// if(!"".equals(date_s)){
		// //====zhangp 20120615 start
		// //
		// sqlbf.append(" AND A.ADM_DATE>=TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ");
		// sqlbf.append(" AND G.CHARGE_DATE>=TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ");
		// //====zhangp 20120615 end
		// }
		// if(!"".equals(date_e)){
		// //====zhangp 20120615 start
		// //
		// sqlbf.append(" AND A.ADM_DATE<=TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') ");
		// sqlbf.append(" AND G.CHARGE_DATE<=TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') ");
		// //====zhangp 20120615 end
		// }
		sqlbf.append(" AND (G.ACCOUNT_SEQ IN (" + account_seqs
				+ ") OR F.ACCOUNT_SEQ IN (" + regaccount_seqs + "))");
		// ===zhangp 20120618 end
		String admType = this.getValue("ADM_TYPE") + "";
		if (!"".equals(admType)) {
			sqlbf.append(" AND A.ADM_TYPE ='" + admType + "'");
		}
		String mrNo = this.getValueString("MR_NO");
		if (!"".equals(mrNo)) {
			sqlbf.append(" AND A.MR_NO ='" + mrNo + "'");
		}
		// String optUser=this.getValueString("OPT_USER");
		// if(!"".equals(optUser)){
		// sqlbf.append(" AND A.OPT_USER ='"+optUser+"'");
		// }
		sqlbf.append("GROUP BY A.CASE_NO," + " A.ADM_TYPE," + " B.PAT_NAME,"
				+ " A.ADM_DATE," + " D.APPLY_DATE," + " D.APPLY_AMT,"
				+ " C.USER_NAME," + " E.CHN_DESC," + " D.APPROVE_DATE,"
				+ " H.USER_NAME," + " D.APPROVE_AMT," + " A.GREEN_BALANCE,"
				+ " A.GREEN_PATH_TOTAL," + " A.MR_NO," + " I.USER_NAME,"
				+ " J.DEPT_ABS_DESC " + "ORDER BY A.ADM_DATE DESC");
		System.out.println("��ѯsql��" + sqlbf.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sqlbf.toString()));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("��������");
			onClear();
			return;
		}
		double sumPay = 0;
		double sumApply = 0;
		double sumApprove = 0;
		double sumBalance = 0;
		for (int i = 0; i < result.getCount(); i++) {
			sumPay += result.getDouble("PAY_OTHER", i);
			sumApply += result.getDouble("APPLY_AMT", i);
			sumApprove += result.getDouble("APPROVE_AMT", i);
			sumBalance += result.getDouble("GREEN_BALANCE", i);
		}
		result.addData("CASE_NO", "");
		result.addData("ADM_TYPE_DESC", "");
		result.addData("PAT_NAME", "");
		result.addData("MR_NO", "");
		result.addData("ADM_DATE", "");
		result.addData("REALDR_CODE", "");
		result.addData("DEPT_ABS_DESC", "");
		result.addData("GREEN_BALANCE", StringTool.round(sumBalance, 2));
		result.addData("PAY_OTHER", StringTool.round(sumPay, 2));
		result.addData("APPLY_DATE", "�ϼ�");
		result.addData("APPLY_AMT", StringTool.round(sumApply, 2));
		result.addData("APPLY_USER", "");
		result.addData("APPLY_CAUSE", "");
		result.addData("APPROVE_DATE", "");
		result.addData("APPROVE_USER", "");
		result.addData("APPROVE_AMT", StringTool.round(sumApprove, 2));
		result.setCount(result.getCount("CASE_NO"));
		table.setParmValue(result);
	}

}
