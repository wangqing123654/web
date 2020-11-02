package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.mro.MROCatalogingTool;
import jdo.mro.MROTransDataToDBFTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ������Ŀ
 * </p>
 * 
 * <p>
 * Description: ������Ŀ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROCatalogingControl extends TControl {
	private TParm data = new TParm();
	private String printType = "";// ��¼��ӡ����

	// =================������==============add by wanglong 20120921
	private BILComparator compare = new BILComparator();
	private int sortColumn = -1;
	private boolean ascending = false;

	// ��ʼ��
	public void onInit() {
		super.onInit();
		onClear();
		// ������������
		callFunction("UI|OP_CODE|setPopupMenuParameter", "OPICD",
				"%ROOT%\\config\\sys\\SYSOpICD.x");
		// �������������ش�ֵ
		callFunction("UI|OP_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// ICD10��������
		callFunction("UI|OUT_DIAG_CODE1|setPopupMenuParameter", "ICD10",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");
		// ����ICD10�����ش�ֵ
		callFunction("UI|MR_NO|addEventListener", TTextFieldEvent.KEY_RELEASED,
				this, "onQueryMrNo");
		// mr_no �س��¼�
		callFunction("UI|OUT_DIAG_CODE1|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ICD10Return");
		// ========pangben modify 20110621 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110621 stop
		TTable table = (TTable) this.getComponent("MRO_TABLE");// add by
																// wanglong
																// 20120921
		addSortListener(table);// add by wanglong 20120921 ������

	}

	/**
	 * mr_no �س��¼�
	 */
	public void onQueryMrNo() {
		String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
		setValue("MR_NO", mrNo);
		this.onQuery();
	}

	/**
	 * ����ICDѡ�񷵻����ݴ���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null) {
			this.clearValue("OP_DESC;OP_CODE");
			return;
		}
		TParm returnParm = (TParm) obj;
		this.setValue("OP_DESC", returnParm.getValue("OPT_CHN_DESC"));
		this.setValue("OP_CODE", returnParm.getValue("OPERATION_ICD"));
		if (this.getValueString("OP_CODE").trim().length() <= 0) {
			this.setValue("OP_CODE", "");
		}
	}

	/**
	 * ���ICD10ѡ�񷵻����ݴ���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ICD10Return(String tag, Object obj) {
		if (obj == null)
			return;
		TParm returnParm = (TParm) obj;
		this.setValue("OUT_DIAG_CODE1", returnParm.getValue("ICD_CODE"));
		this.setValue("OUT_DIAG_DESC", returnParm.getValue("ICD_CHN_DESC"));
	}

	/*
	 * 
	 * ��ѯ
	 */
	public void onQuery() {
		TParm results = new TParm();
		if (this.getValue("checkType").equals("")) {
			this.messageBox_("��ѡ���ѯ���");
			return;
		}
		// ��ȡ��ѯ����
		TParm parm = this
				.getParmForTag("checkType;OUT_DEPT;PAT_NAME;SEX;IPD_NO;MR_NO;IDNO;CHARGE_START;CHARGE_END;"
						+ "OUT_DIAG_CODE1;OP_CODE;CODE1_STATUS;VS_DR_CODE;DIRECTOR_DR_CODE;OFFICE;"
						+ "CONTACTER;CONT_TEL;H_TEL");
		// ��Ժ����
		parm.setData("OUT_DATE_START", StringTool.getString(
				getUITime("OUT_DATE_START"), "yyyyMMdd"));
		parm.setData("OUT_DATE_END", StringTool.getString(
				getUITime("OUT_DATE_END"), "yyyyMMdd"));
		// ��Ժ����

		parm.setData("IN_DATE_START", StringTool.getString(
				getUITime("IN_DATE_START"), "yyyyMMdd"));
		parm.setData("IN_DATE_END", StringTool.getString(
				getUITime("IN_DATE_END"), "yyyyMMdd"));

		// ����
		parm.setData("BIRTH_DATE", StringTool.getString(
				getUITime("BIRTH_DATE"), "yyyyMMdd"));
		// סԺ����ɱ��
		if (((TCheckBox) this.getComponent("ADMCHK_FLG")).isSelected())
			parm.setData("ADMCHK_FLG", "Y");
		// ҽ ʦ��ɱ��
		if (((TCheckBox) this.getComponent("DIAGCHK_FLG")).isSelected())
			parm.setData("DIAGCHK_FLG", "Y");
		// �� ����ɱ��
		if (((TCheckBox) this.getComponent("BILCHK_FLG")).isSelected())
			parm.setData("BILCHK_FLG", "Y");
		// ��������ɱ��
		if (((TCheckBox) this.getComponent("QTYCHK_FLG")).isSelected())
			parm.setData("QTYCHK_FLG", "Y");
		/**
		 * ������ҳ�У����ֶ��ٴ����鲡���ֵ䡢 ��ѧ�����ֵ�ͬʱ�����ϲ�ѯ���ܣ�����ά���������ֶ� TEST_EMR,TEACH_EMR
		 * Modify ZhenQin ------ 2010-05-09 //;
		 */
		TParm dataTMP = this.getParmForTag("TEST_EMR;TEACH_EMR");
		if (!dataTMP.getValue("TEST_EMR").equals("")) {
			parm.setData("TEST_EMR", dataTMP.getValue("TEST_EMR"));
		}
		if (!dataTMP.getValue("TEACH_EMR").equals("")) {
			parm.setData("TEACH_EMR", dataTMP.getValue("TEACH_EMR"));
		}
		// ===========pangben modify 20110518 start ����������
		if (null != getValueString("REGION_CODE")
				&& getValueString("REGION_CODE").length() > 0)
			parm.setData("REGION_CODE", getValueString("REGION_CODE"));
		// ===========pangben modify 20110518 start

		 //�������
		 parm.setData("CONFIRM_DATE",
		 StringTool.getString(getUITime("CONFIRM_DATE"),"yyyyMMdd"));
		// //�����ȼ�
		// if(null != this.getValue("OP_LEVEL") &&
		// this.getValueString("OP_LEVEL").length() > 0){
		// parm.setData("OP_LEVEL", getValueString("OP_LEVEL"));
		// }
		// //��������
		// parm.setData("OP_DATE",
		// StringTool.getString(getUITime("OP_DATE"),
		// "yyyyMMdd"));
		// ��ѯ��������MRO_RECORD���ݣ���סԺ����������
		data = MROCatalogingTool.getInstance().queryMROInfo(parm);
		// �ж�סԺ�����������Ƿ�Ϊ�գ��������һ��������Ϊ�գ������סԺ�������ֱ��жϹ��̣����Ϊ�գ�ֱ����ʾ���
		if ((null != this.getValue("OUT_DIAG_CODE1") && this.getValueString(
				"OUT_DIAG_CODE1").length() > 0)
				|| (null != this.getValue("OP_LEVEL") && this.getValueString(
						"OP_LEVEL").length() > 0)
				|| (null != this.getValue("OP_CODE") && this.getValueString(
						"OP_CODE").length() > 0)
				|| (null != StringTool.getString(getUITime("OP_DATE"),
						"yyyyMMdd") && StringTool.getString(
						getUITime("OP_DATE"), "yyyyMMdd").length() > 0)) {
			//�ж�סԺ�����Ƿ�Ϊ�գ����Ϊ�գ��ж����������������Ϊ�գ������ѭ����������������
			if ((null != this.getValue("OUT_DIAG_CODE1") && this
					.getValueString("OUT_DIAG_CODE1").length() > 0)) {
				//�õ�MRO_RECORD_DIAG�����ݣ�ǿת��Map���ͣ��Ա����MRO_RECORD��������
				//��Map���ϣ���Ϊ�ַ�����ֵΪVector
				Map mapDiag = ((Map) filterResultsFromDiag());
				//����һ��Vector����������ֵ
				Vector vectorDiag = null;
				//�ж�Map�����Ƿ�Ϊ�գ����Ϊ�գ���δ��MRO_RECORD_DIAG���в�ѯ����Ч���ݣ������ж���������
				if(null != mapDiag && mapDiag.size() > 0){
					//�õ���MRO_RECORD_DIAG���в�ѯ����������CASE_NO
					vectorDiag = (Vector) mapDiag.get("CASE_NO");
					//ѭ���ж��������Ƿ���ڴӴ�MRO_RECORD_DIAG���в�ѯ������CASE_NO
					for (int i = 0; i < data.getCount(); i++) {
						//�õ��������ݵ�ÿһ������
						TParm parmRow = data.getRow(i);
						//�õ�ÿһ�����ݵ�CASE_NO
						String caseNo = parmRow.getValue("CASE_NO");
						//�ж��Ƿ���ÿһ�е�CASE_NO������У���ӵ����յĽ��results��
						if (vectorDiag.contains(caseNo)) {
							results.addRowData(data, i);
						}
					}
				}
				//�ж����������Ƿ�Ϊ�գ����Ϊ�գ���ֱ����ʾ�����ѯ���
				//�����Ϊ�գ������ɸѡ���ݹ���
				if ((null != this.getValue("OP_LEVEL") && this.getValueString(
						"OP_LEVEL").length() > 0)
						|| (null != this.getValue("OP_CODE") && this
								.getValueString("OP_CODE").length() > 0)
						|| (null != StringTool.getString(getUITime("OP_DATE"),
								"yyyyMMdd") && StringTool.getString(
								getUITime("OP_DATE"), "yyyyMMdd").length() > 0)) {
					//��Map���ϣ���Ϊ�ַ�����ֵΪVector
					Map mapOp = ((Map) filterResultsFromOP());
					//����һ��Vector����������ֵ
					Vector vectorOp = null;
					//���סԺ������������������д����ôסԺ����ɸѡ������CASE_NO���������ͨ��ɸѡ�����������������
					//����Ҫ��ù������ݣ������м����vectorResult����¼Ҫ��������������
					Vector vectorResult = new Vector();
					//�ж�Map�����Ƿ�Ϊ�գ����Ϊ�գ���δ��MRO_RECORD_OP���в�ѯ����Ч����
					//��סԺ������������δ��ѯ����Ч���ݣ�������ʾ
					if(null != mapOp && mapOp.size() > 0){
						//�õ���MRO_RECORD_OP���в�ѯ����������CASE_NO
						vectorOp = (Vector) mapOp.get("CASE_NO");
						//��¼���������¶����ϵ����ݣ������м����vectorResult��
						for (int i = 0; i < vectorDiag.size(); i++) {
							if(vectorOp.contains(vectorDiag.get(i))){
								vectorResult.add(vectorDiag.get(i));
							}
						}
						//��ΪסԺ���������д��ɸѡ���������������оͻ�������ս����results
						//���Ա��������һ�β���results����������
						while (results.getCount() > 0) {
							for (int i = 0; i < results.getCount(); i++) {
								results.removeRow(i);
							}
						}
						//ѭ���ж��������Ƿ���ڹ��˺��CASE_NO������У���ӵ����յĽ��results��
						for (int i = 0; i < data.getCount(); i++) {
							TParm parmRow = data.getRow(i);
							String caseNo = parmRow.getValue("CASE_NO");
							if (vectorResult.contains(caseNo)) {
								results.addRowData(data, i);
							}
						}
						setValues(results);
					}else{
//						this.messageBox_("û�в�ѯ��������������Ϣ��");
//						return;
						while (results.getCount() > 0) {
							for (int i = 0; i < results.getCount(); i++) {
								results.removeRow(i);
							}
						}
						setValues(results);
					}
				}else{
					setValues(results);
				}
				// �ж����������Ƿ�Ϊ�գ����Ϊ�գ�����ʾ�������Ϊ�գ������ѭ�����Ȼ��MRO_RECORD_OP�����ݣ��ٹ����������ݣ�����ͬ��
			} else if ((null != this.getValue("OP_LEVEL") && 
					this.getValueString("OP_LEVEL").length() > 0)
					|| (null != this.getValue("OP_CODE") && 
							this.getValueString("OP_CODE").length() > 0)
					|| (null != StringTool.getString(getUITime("OP_DATE"),"yyyyMMdd") && 
							StringTool.getString(getUITime("OP_DATE"), "yyyyMMdd").length() > 0)) {
				Map map = ((Map) filterResultsFromOP());
				Vector vector = null;
				if(null != map && map.size() > 0){
					vector = (Vector) map.get("CASE_NO");
					for (int i = 0; i < data.getCount(); i++) {
						TParm parmRow = data.getRow(i);
						String caseNo = parmRow.getValue("CASE_NO");
						if (vector.contains(caseNo)) {
							results.addRowData(data, i);
						}
					}
				}
				setValues(results);
			}
		}else{
			setValues(data);
		}
	}

	private void setValues(TParm parm) {
		if(parm.getCount() >= 0)
			((TTextField) this.getComponent("PAT_COUNT")).setText(parm.getCount() + "");
		else
			((TTextField) this.getComponent("PAT_COUNT")).setText(0 + "");
		Timestamp now = SystemTool.getInstance().getDate();
		if (parm.getCount("MR_NO") <= 0) {
			((TTable) this.getComponent("MRO_TABLE")).removeRowAll();
			this.messageBox_("û�в�ѯ��������������Ϣ��");
			return;
		}
//		Timestamp now = SystemTool.getInstance().getDate();
		// ����סԺ�����ͳ�Ժ�����ĳ�ʼֵ
		for (int i = 0; i < parm.getCount(); i++) {
			int inDays = 0;
			// �жϲ����Ƿ��Ժ
			if (parm.getValue("OUT_DATE", i).length() > 0) {
				// ��������Ѿ���Ժ
				// סԺ���� = ��Ժʱ�� - סԺ����
				inDays = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool.getString(parm.getTimestamp("OUT_DATE", i), "yyyyMMdd"),"yyyyMMdd"),
						StringTool.getTimestamp(StringTool.getString(parm.getTimestamp("IN_DATE", i),"yyyyMMdd"), "yyyyMMdd"));
				// �����Ժ����
				int outDays = StringTool.getDateDiffer(now, parm.getTimestamp(
						"OUT_DATE", i));
				// �����Ժ����Ϊ�� ���Զ���һ
				if (outDays == 0) {
					outDays = 1;
				}
				parm.setData("OUT_DAYS", i, outDays);
			} else {// ���������Ժ
				// סԺ���� = ��ǰʱ�� - סԺ����
				inDays = StringTool.getDateDiffer(StringTool.getTimestamp(
						StringTool.getString(now, "yyyyMMdd"), "yyyyMMdd"),
						StringTool.getTimestamp(StringTool.getString(parm
								.getTimestamp("IN_DATE", i), "yyyyMMdd"),
								"yyyyMMdd"));
				// ���ü����Ժ����
				parm.setData("OUT_DAYS", i, "");
			}
			if (inDays == 0)// ���סԺ����Ϊ����ôĬ��Ϊ1
				inDays = 1;
			parm.setData("REAL_STAY_DAYS", i, inDays);
		}
		
		TComboBox combo = (TComboBox) this.getComponent("checkType");
		printType = combo.getSelectedText();
		((TTable) this.getComponent("MRO_TABLE")).setParmValue(parm);
	}

	/**
	 * ��ѯMRO_RECORD_DIAG������
	 * 
	 * @return MRO_RECORD_DIAG��Ľ��
	 */
	private Object filterResultsFromDiag() {
		TParm parm = new TParm();
		TParm result = null;
		parm.setData("IO_TYPE", "O");
		if (null != this.getValue("OUT_DIAG_CODE1")
				&& this.getValueString("OUT_DIAG_CODE1").length() > 0) {
			parm.setData("ICD_CODE", this.getValueString("OUT_DIAG_CODE1"));
		}
		if (null != StringTool.getString(getUITime("CONFIRM_DATE"), "yyyyMMdd")
				&& StringTool.getString(getUITime("CONFIRM_DATE"), "yyyyMMdd")
						.length() > 0) {
			parm.setData("CONFIRM_DATE", StringTool.getString(
					getUITime("CONFIRM_DATE"), "yyyyMMdd"));
		}
		result = MROCatalogingTool.getInstance().queryMRO_DIAGInfo(parm);
		return result.getData().get("Data");
	}

	/**
	 * ��ѯMRO_RECORD_OP������
	 * 
	 * @return MRO_RECORD_OP��Ľ��
	 */
	private Object filterResultsFromOP() {
		TParm parm = new TParm();
		TParm result = null;
		if (null != this.getValue("OP_LEVEL")
				&& this.getValueString("OP_LEVEL").length() > 0) {
			parm.setData("OP_LEVEL", this.getValueString("OP_LEVEL"));
		}
		if (null != this.getValue("OP_CODE")
				&& this.getValueString("OP_CODE").length() > 0) {
			parm.setData("OP_CODE", this.getValueString("OP_CODE"));
		}
		if (null != StringTool.getString(getUITime("OP_DATE"), "yyyyMMdd")
				&& StringTool.getString(getUITime("OP_DATE"), "yyyyMMdd")
						.length() > 0) {
			parm.setData("OP_DATE", StringTool.getString(getUITime("OP_DATE"),
					"yyyyMMdd"));
		}
		// parm.setData("CASE_NO", caseNo);
		result = MROCatalogingTool.getInstance().queryMRO_OPInfo(parm);
		// ((Map)result.getData().get("Data")).containsValue(caseNo);
		// System.out.println("ddddddddddddddddddddd"+result);
		// System.out.println("fdfsfdsfsdfdsfds"+result.getData().get("Data"));
		return result.getData().get("Data");
	}

	/**
	 * ���
	 */
	public void onClear() {
		this
				.clearValue("checkType;OUT_DEPT;PAT_NAME;SEX;IPD_NO;MR_NO;IDNO;OUT_DATE_START;"
						+ "OUT_DATE_END;IN_DATE_START;IN_DATE_END;CHARGE_START;CHARGE_END;"
						+ "OUT_DIAG_CODE1;OUT_DIAG_DESC;OUT_DIAG_DATE;OP_CODE;OP_DESC;OP_DATE;"
						+ "CODE1_STATUS;VS_DR_CODE;DIRECTOR_DR_CODE;OFFICE;BIRTH_DATE;CONTACTER;"
						+ "CONT_TEL;H_TEL;ADMCHK_FLG;DIAGCHK_FLG;BILCHK_FLG;QTYCHK_FLG");
		((TTable) this.getComponent("MRO_TABLE")).clearSelection();// ȡ��ѡ����
		this.setValue("REGION_CODE", Operator.getRegion());// =======pangben
															// modify 20110621
	}

	/**
	 * ��������
	 */
	public void onLending() {
		// ��ȡѡ����
		TTable table = (TTable) this.getComponent("MRO_TABLE");
		int rowIndex = table.getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("��ѡ��һ�ݲ�����");
			return;
		}
		TParm parm = new TParm();
		// add by wangbin סԺ�������� 20140917 START
		parm.setData("ADM_TYPE", "I");
		// add by wangbin סԺ�������� 20140917 END
		parm.setData("MR_NO", data.getValue("MR_NO", rowIndex));
		parm.setData("IPD_NO", data.getValue("IPD_NO", rowIndex));
		parm.setData("CASE_NO", data.getValue("CASE_NO", rowIndex));
		parm.setData("PAT_NAME", data.getValue("PAT_NAME", rowIndex));// ��������
		parm.setData("VS_CODE", data.getValue("VS_DR_CODE", rowIndex));// סԺҽʦ
		this.openDialog("%ROOT%\\config\\mro\\MROLendReg.x", parm);
	}

	/**
	 * ���á�������ҳ��
	 */
	public void callRecordPG() {
		// ��ȡѡ����
		int rowIndex = ((TTable) this.getComponent("MRO_TABLE"))
				.getSelectedRow();
		TParm parm = new TParm();
		// ------------modify by wanglong 20121029----------------------
		// parm.setData("MR_NO", data.getValue("MR_NO", rowIndex));
		// parm.setData("CASE_NO", data.getValue("CASE_NO",rowIndex));
		TParm patInfo = (TParm) callFunction("UI|MRO_TABLE|getShowParmValue");
		parm.setData("MR_NO", patInfo.getValue("MR_NO", rowIndex));
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		// -------------modify end--------------------------------------
		parm.setData("USER_TYPE", "4");// ������Ŀ���� ��ʾ
		parm.setData("OPEN_USER", Operator.getID());
		this.openWindow("%ROOT%\\config\\mro\\MRORecord.x", parm);
		// this.onQuery();//delete by wanglong 20121029
	}

	/**
	 * ��ȡ���ڿؼ��ķ���ֵ
	 * 
	 * @param tag
	 *            String
	 * @return Timestamp
	 */
	public Timestamp getUITime(String tag) {
		Timestamp time = (Timestamp) ((TTextFormat) this.getComponent(tag))
				.getValue();
		return time;
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TTable table = (TTable) this.getComponent("MRO_TABLE");
		TParm data = table.getParmValue();
		TParm printData = new TParm();
		TParm TableData = new TParm();
		for (int i = 0; i < data.getCount("CASE_NO"); i++) {
			TableData.addData("MR_NO", data.getData("MR_NO", i));
			TableData.addData("PAT_NAME", data.getData("PAT_NAME", i));
			TableData.addData("SEX", data.getData("SEX", i));
			TableData.addData("AGE", StringUtil.showAge(data.getTimestamp(
					"BIRTH_DATE", i), data.getTimestamp("IN_DATE", i)));
			TableData.addData("IN_DATE", StringTool.getString(data
					.getTimestamp("IN_DATE", i), "yyyy-MM-dd"));
			TableData.addData("OUT_DATE", StringTool.getString(data
					.getTimestamp("OUT_DATE", i), "yyyy-MM-dd"));
			TableData.addData("REAL_STAY_DAYS", data.getData("REAL_STAY_DAYS",
					i));
			TableData.addData("OUT_DAYS", data.getData("OUT_DAYS", i));
			TableData.addData("OUT_DEPT", data.getData("OUT_DEPT", i));
			TableData.addData("IPD_NO", data.getData("IPD_NO", i));
		}
		TableData.setCount(data.getCount("CASE_NO"));
		TableData.addData("SYSTEM", "COLUMNS", "MR_NO");
		TableData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		TableData.addData("SYSTEM", "COLUMNS", "SEX");
		TableData.addData("SYSTEM", "COLUMNS", "AGE");
		TableData.addData("SYSTEM", "COLUMNS", "IN_DATE");
		TableData.addData("SYSTEM", "COLUMNS", "OUT_DATE");
		TableData.addData("SYSTEM", "COLUMNS", "REAL_STAY_DAYS");
		TableData.addData("SYSTEM", "COLUMNS", "OUT_DAYS");
		TableData.addData("SYSTEM", "COLUMNS", "OUT_DEPT");
		TableData.addData("SYSTEM", "COLUMNS", "IPD_NO");
		TParm basic = new TParm();
		basic.addData("PrintUser", Operator.getName());// �Ʊ���
		printData.setData("basic", basic.getData());
		printData.setData("T1", TableData.getData());
		printData.setData("checkType", "TEXT", printType);// ��ӡ����
		printData.setData("printDate", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy��MM��dd��"));
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROCatalogPring.jhw",
				printData);
	}

	/**
	 * ������� ==========pangben modify 20110706
	 */
	public void onShow() {
		TTable table = ((TTable) this.getComponent("MRO_TABLE"));
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		Runtime run = Runtime.getRuntime();
		try {
			// �õ���ǰʹ�õ�ip��ַ
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// ������ҳ����
			run.exec("IEXPLORE.EXE " + ip + parm.getValue("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ȫ�ļ���UI
	 */
	public void onAllSearch() {
		Runtime run = Runtime.getRuntime();
		try {
			// �õ���ǰʹ�õ�ip��ַ
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRSearchServlet?method=");
			// ������ҳ���� parm.getValue("MR_NO")
			run.exec("IEXPLORE.EXE " + ip + "init");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * LIS��� ==========pangben modify 20110706
	 */
	public void onLISShow() {
		TTable table = ((TTable) this.getComponent("MRO_TABLE"));
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		Runtime run = Runtime.getRuntime();
		try {
			// �õ���ǰʹ�õ�ip��ַhttp://127.0.0.1:8080/BlueCore/jsp/lis/reportMain.jsp?MR_NO=000000001112
			// String
			// ip="http://"+TIOM_AppServer.SOCKET.getIP()+":"+TIOM_AppServer.SOCKET.getPort()+"/BlueCore/jsp/lis/reportMain.jsp?MR_NO=";
			String ip = "http://192.168.1.103:"
					+ TIOM_AppServer.SOCKET.getPort()
					+ "/BlueCore/jsp/lis/reportMain.jsp?MR_NO=";
			// ������ҳ����
			run.exec("IEXPLORE.EXE " + ip + parm.getValue("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ����EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(
				((TTable) this.getComponent("MRO_TABLE")), "������Ŀ");
	}

	public void saveDBF() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("�������Ժ����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("OUT_DATE_START"), true));
		parm.setData("END_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("OUT_DATE_END"), false));
		MROTransDataToDBFTool.getInstance().getMRODBF(parm, this);
	}

	// ====================������begin======================modify by wanglong
	// 20120921
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ����ָ���������������е�index
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �����������ݣ���Vectorת��Parm
	 * 
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */

	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================
}
