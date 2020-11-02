package com.javahis.ui.iva;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.iva.IVADeploymentTool;
import jdo.iva.IVARefundMedicineTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatSYSOperator;

public class IVARefundMedicineControl extends TControl {
	// �õ�table�ؼ�
	private TTable table_d;
	private TTable table_m;
	private TTable table_c;

	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	// �õ�checkbox�ؼ�
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	// �õ�RadioButton�ؼ�
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	public void onInit() {
		super.onInit();

		initPage();
	}

	/*
	 * ��ʼ��ʱ��͵�����Ա
	 */
	public void initPage() {

		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		Operator.getID();
		this.setValue("IVA_RETN_USER", Operator.getID());
		onQueryAll();
	}

	/*
	 * ��ѯ����ҩ�Ĳ���
	 */
	public void onQueryAll() {
		// �õ�TABLE_M�ؼ�
		table_c = this.getTTable("TABLE_C");
		table_c.removeRowAll();
		TParm parm = new TParm();
		TParm reuslt = IVARefundMedicineTool.getInstance().queryInfo(parm);
		table_c.setParmValue(reuslt);
	}

	/*
	 * ��ѯ�Ѿ���ҩ�Ĳ���
	 */
	public void onQueryready() {
		table_c = this.getTTable("TABLE_C");
		table_c.removeRowAll();
		TParm parm = new TParm();
		TParm reuslt = IVARefundMedicineTool.getInstance().queryready(parm);
		table_c.setParmValue(reuslt);
	}

	/*
	 * ��ѯ����ҩƷorδ����ҩƷ��Ϣ
	 */
	public void onQuery() {

		// �õ�TABLE_M�ؼ�
		table_m = this.getTTable("TABLE_M");
		table_m.removeRowAll();
		// ��װǰ̨�õ���ֵ
		TParm parm = new TParm();
		// �õ�ǰ̨�ؼ�ֵ
		String station_code = this.getValueString("STATION_CODE");
		String mr_no = this.getValueString("MR_NO");
		String bed_no = this.getValueString("BED_NO");
		if (station_code != null && !"".equals(station_code)) {
			parm.setData("STATION_CODE", station_code);
		}
		if (mr_no != null && !"".equals(mr_no)) {
			parm.setData("MR_NO", mr_no);
		}
		if (bed_no != null && !"".equals(bed_no)) {
			parm.setData("BED_NO", bed_no);
		}
		TParm result = IVARefundMedicineTool.getInstance().querypatient(parm);
		if(result.getCount() <= 0){
			table_m.removeRowAll();
			this.messageBox("δ��ѯ������");
			return;
		}
//		System.out.println(reuslt);
		table_m.setParmValue(result);
		// String station_code = this.getValueString("STATION_CODE");
		// String iva_barcode = this.getValueString("IVA_BARCODE");
		// String mr_no = this.getValueString("MR_NO");
		// String bed_no = this.getValueString("BED_NO");
		// String iva_deploy_user = this.getValueString("IVA_DEPLOY_USER");
		// String start_time = this.getValueString("START_TIME");
		// String end_time = this.getValueString("END_TIME");
		// �ж�ǰ̨����õ���ֵ�Ƿ�Ϊ��
		// if (iva_barcode != null && !"".equals(iva_barcode)) {
		// parm.setData("IVA_BARCODE", iva_barcode);
		// }
		// if (station_code != null && !"".equals(station_code)) {
		// parm.setData("STATION_CODE", station_code);
		// }
		// if (mr_no != null && !"".equals(mr_no)) {
		// parm.setData("MR_NO", mr_no);
		// }
		// if (bed_no != null && !"".equals(bed_no)) {
		// parm.setData("BED_NO", bed_no);
		// }
		// if (iva_deploy_user != null && !"".equals(iva_deploy_user)) {
		// parm.setData("IVA_DEPLOY_USER", iva_deploy_user);
		// }
		// if (start_time != null && !"".equals(start_time)) {
		// parm.setData("START_TIME", start_time.toString().substring(0, 19));
		// }
		// if (end_time != null && !"".equals(end_time)) {
		// parm.setData("END_TIME", end_time.toString().substring(0, 19));
		// }
		// // �жϵ�ѡ��ť�Ƿ�ѡ��
		// if (getRadioButton("BUTTON_M").isSelected()) {
		// if ((station_code == null || "".equals(station_code))&&
		// (iva_barcode == null || "".equals(iva_barcode))
		// && (mr_no == null || "".equals(mr_no))
		// && (bed_no == null || "".equals(bed_no))) {
		// this.messageBox("��ѯ��������ͬʱΪ�գ�");
		// return;
		// }
		// TParm result = IVADeploymentTool.getInstance().queryInfo(parm);
		// table_m.setParmValue(result);
		// }
		// if (getRadioButton("BUTTON_D").isSelected()) {
		// if ((station_code == null || "".equals(station_code))&&
		// (iva_barcode == null || "".equals(iva_barcode))
		// && (mr_no == null || "".equals(mr_no))
		// && (bed_no == null || "".equals(bed_no))
		// && (iva_deploy_user == null || "".equals(iva_deploy_user))
		// && (start_time == null || "".equals(start_time))
		// && (end_time == null || "".equals(end_time))) {
		// this.messageBox("��ѯ��������ͬʱΪ�գ�");
		// return;
		// }
		// TParm result = IVADeploymentTool.getInstance().querycheck(parm);
		// table_m.setParmValue(result);
		// }
	}

	/*
	 * �����¼�����ʾ��ҩ������Ϣ
	 */
	public void onClick() {

		table_c = this.getTTable("TABLE_C");
		table_m = this.getTTable("TABLE_M");

		// �õ�ѡ��������
		int row = table_c.getSelectedRow();
		// �õ�table��ֵ
		TParm tparm = table_c.getParmValue();
		TParm parm = new TParm();
		if (row != -1) {

			parm.setData("STATION_CODE", tparm.getValue("STATION_CODE", row));

			TParm result = IVARefundMedicineTool.getInstance().querypatient(
					parm);

			table_m.setParmValue(result);
		}

	}

	/*
	 * �����¼�����ʾ������ҩ��ϸ
	 */
	public void onClickdetail() {

		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");

		// �õ�ѡ��������
		int row = table_m.getSelectedRow();
		// �õ�table��ֵ
		TParm tparm = table_m.getParmValue();

		TParm parm = new TParm();
		if (row != -1) {
			parm.setData("CASE_NO", tparm.getValue("CASE_NO", row));
			TParm result = IVARefundMedicineTool.getInstance()
					.querydetail(parm);
			table_d.setParmValue(result);
		}
		onTableCheckBoxClicked(this);

	}

	/*
	 * ���
	 */

	public void onClear() {
		table_m = this.getTTable("TABLE_M");
		table_m.removeRowAll();
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		table_c = this.getTTable("TABLE_C");
		table_c.removeRowAll();
		this.clearValue("STATION_CODE");
		this.clearValue("MR_NO");
		this.clearValue("BED_NO");
//		if (getRadioButton("BUTTON_D").isSelected()) {
//			this.clearValue("IVA_RETN_USER");
//			this.clearValue("START_TIME");
//			this.clearValue("END_TIME");
//		}
	}

	/*
	 * ��ѡ��ť�¼�
	 */
	public void onRadion() {
		if (getRadioButton("BUTTON_D").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("IVA_RETN_USER"))
					.setEnabled(true);
			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
			onClear();
			onQueryready();
		}
		if (getRadioButton("BUTTON_M").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("IVA_RETN_USER"))
					.setEnabled(false);
			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
			initPage();
			onClear();
		}

	}

	/*
	 * ��ҩ��ִ��update��insert
	 */
	public void onSave() {

		table_d = this.getTTable("TABLE_D");
		table_m = this.getTTable("TABLE_M");
		String check_user = this.getValueString("IVA_RETN_USER");
		TParm tparm = table_m.getParmValue();
		// ʱ���ʽ��
		SimpleDateFormat timeFormaterDspnd = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormaterOdiDspnd = new SimpleDateFormat("HHmmss");
		String time = timeFormaterOdiDspnd.format(new Date());
		int timeNo = Integer.parseInt(time);
		SimpleDateFormat timeFormaterDspnm = new SimpleDateFormat(
				"yyyyMMddHHmm");
		// �õ�δѡ�е�����ֵ
		TParm temParm = new TParm();
		TParm result = new TParm();
		// �õ�M����������ѯ��ϸ
		TParm resultdspnm = new TParm();
		// �õ�ÿ����ϸ������
		TParm resultdspnd = new TParm();

		for (int i = 0; i < tparm.getCount(); i++) {

			TParm rowParm = tparm.getRow(i);
			String flg = rowParm.getValue("SELECT_AVG");
			// �ж��Ƿ�ѡ��
			if ("Y".equals(flg)) {// flg.equals("Y")

				rowParm.setData("IVA_RETN_USER", check_user);
				// �õ�M�����ĳ��������ҩ��Ϣ
				TParm parmdspm = IVARefundMedicineTool.getInstance()
						.querypdspnm(rowParm);
				// ����ODI_DSPNM,��ȡ����ϸ
				for (int j = 0; j < parmdspm.getCount(); j++) {
					resultdspnm.setData("CASE_NO", parmdspm.getValue("CASE_NO",
							j));
					resultdspnm.setData("ORDER_NO", parmdspm.getValue(
							"ORDER_NO", j));
					resultdspnm.setData("ORDER_SEQ", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("START_DTTM", timeFormaterDspnm
							.format(new Date()));
					resultdspnm.setData("END_DTTM", timeFormaterDspnm
							.format(new Date()));
					resultdspnm.setData("REGION_CODE", parmdspm.getValue(
							"REGION_CODE", j));
					resultdspnm.setData("STATION_CODE", parmdspm.getValue(
							"STATION_CODE", j));
					resultdspnm.setData("DEPT_CODE", parmdspm.getValue(
							"DEPT_CODE", j));
					resultdspnm.setData("VS_DR_CODE", parmdspm.getValue(
							"VS_DR_CODE", j));
					resultdspnm.setData("BED_NO", parmdspm
							.getValue("BED_NO", j));
					resultdspnm.setData("IPD_NO", parmdspm
							.getValue("IPD_NO", j));
					resultdspnm.setData("MR_NO", parmdspm.getValue("MR_NO", j));
					resultdspnm.setData("DSPN_KIND", "RT");
					resultdspnm.setData("DSPN_DATE", parmdspm.getValue(
							"ORDER_NO", j));
					resultdspnm.setData("DSPN_USER", Operator.getID());
					resultdspnm.setData("ORDER_CAT1_CODE", parmdspm.getValue(
							"ORDER_CAT1_CODE", j));
					resultdspnm.setData("CAT1_TYPE", parmdspm.getValue(
							"CAT1_TYPE", j));
					resultdspnm.setData("EXEC_DEPT_CODE", parmdspm.getValue(
							"EXEC_DEPT_CODE", j));
					resultdspnm.setData("LINKMAIN_FLG", parmdspm.getValue(
							"LINKMAIN_FLG", j));
					resultdspnm.setData("ORDER_CODE", parmdspm.getValue(
							"ORDER_CODE", j));
					resultdspnm.setData("ORDER_DESC", parmdspm.getValue(
							"ORDER_DESC", j));
					resultdspnm.setData("ROUTE_CODE", parmdspm.getValue(
							"ROUTE_CODE", j));
					resultdspnm.setData("DISPENSE_QTY", parmdspm.getValue(
							"DISPENSE_QTY", j));
					resultdspnm.setData("DISPENSE_UNIT", parmdspm.getValue(
							"DISPENSE_UNIT", j));
					resultdspnm.setData("GIVEBOX_FLG", parmdspm.getValue(
							"GIVEBOX_FLG", j));
					resultdspnm.setData("OWN_PRICE", parmdspm.getValue(
							"OWN_PRICE", j));
					resultdspnm.setData("NHI_PRICE", parmdspm.getValue(
							"NHI_PRICE", j));
					resultdspnm.setData("OWN_AMT", parmdspm.getValue("OWN_AMT",
							j));
					resultdspnm.setData("TOT_AMT", parmdspm.getValue("TOT_AMT",
							j));
					resultdspnm.setData("ATC_FLG", parmdspm.getValue("ATC_FLG",
							j));
					resultdspnm.setData("SENDATC_FLG", parmdspm.getValue(
							"SENDATC_FLG", j));
					resultdspnm.setData("DC_TOT", parmdspm.getValue("ORDER_NO",
							j));
					resultdspnm.setData("RTN_NO", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("RTN_NO_SEQ", parmdspm.getValue(
							"CASE_NO", j));
					resultdspnm.setData("RTN_DOSAGE_QTY", parmdspm.getValue(
							"ORDER_NO", j));
					resultdspnm.setData("RTN_DOSAGE_UNIT", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("CANCEL_DOSAGE_QTY", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("PHA_TYPE", parmdspm.getValue(
							"CASE_NO", j));
					resultdspnm.setData("DOSE_TYPE", parmdspm.getValue(
							"ORDER_NO", j));
					resultdspnm.setData("DCTAGENT_FLG", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("URGENT_FLG", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("BILL_FLG", parmdspm.getValue(
							"CASE_NO", j));
					resultdspnm.setData("OPT_USER", Operator.getID());
					resultdspnm.setData("OPT_DATE", parmdspm.getValue(
							"ORDER_SEQ", j));
					resultdspnm.setData("OPT_TERM", Operator.getIP());
					resultdspnm.setData("FINAL_TYPE", parmdspm.getValue(
							"FINAL_TYPE", j));
					resultdspnm.setData("VERIFYIN_PRICE1", parmdspm.getValue(
							"VERIFYIN_PRICE1", j));
					resultdspnm.setData("DISPENSE_QTY1", parmdspm.getValue(
							"DISPENSE_QTY1", j));
					resultdspnm.setData("RETURN_QTY1", parmdspm.getValue(
							"RETURN_QTY1", j));
					resultdspnm.setData("VERIFYIN_PRICE2", parmdspm.getValue(
							"VERIFYIN_PRICE2", j));
					resultdspnm.setData("DISPENSE_QTY2", parmdspm.getValue(
							"DISPENSE_QTY2", j));
					resultdspnm.setData("RETURN_QTY2", parmdspm.getValue(
							"RETURN_QTY2", j));
					resultdspnm.setData("VERIFYIN_PRICE3", parmdspm.getValue(
							"VERIFYIN_PRICE3", j));
					resultdspnm.setData("DISPENSE_QTY3", parmdspm.getValue(
							"DISPENSE_QTY3", j));
					resultdspnm.setData("RETURN_QTY3", parmdspm.getValue(
							"RETURN_QTY3", j));
					resultdspnm.setData("TAKEMED_ORG", parmdspm.getValue(
							"TAKEMED_ORG", j));

					// �õ���ϸD����������
					TParm parmdspnd = IVARefundMedicineTool.getInstance()
							.querydspnd(resultdspnm);
					// ������ϸ
					for (int k = 0; k < parmdspnd.getCount(); k++) {
						resultdspnd.setData("CASE_NO", parmdspnd.getValue(
								"CASE_NO", k));
						resultdspnd.setData("ORDER_NO", parmdspnd.getValue(
								"ORDER_NO", k));
						resultdspnd.setData("ORDER_SEQ", parmdspnd.getValue(
								"ORDER_SEQ", k));
						resultdspnd.setData("ORDER_DATE", timeFormaterDspnd
								.format(new Date()));

						if (j > 0) {
							resultdspnd.setData("ORDER_DATETIME", timeNo + j);
						} else {
							resultdspnd.setData("ORDER_DATETIME", time);
						}

						resultdspnd.setData("NURSE_DISPENSE_FLG", parmdspnd
								.getValue("NURSE_DISPENSE_FLG", k));
						resultdspnd.setData("ORDER_CODE", parmdspnd.getValue(
								"ORDER_CODE", k));
						resultdspnd.setData("DOSAGE_QTY", parmdspnd.getValue(
								"DOSAGE_QTY", k));

						resultdspnd.setData("DOSAGE_UNIT", parmdspnd.getValue(
								"DOSAGE_UNIT", k));
						resultdspnd.setData("EXEC_DEPT_CODE", parmdspnd
								.getValue("EXEC_DEPT_CODE", k));
						resultdspnd.setData("BILL_FLG", parmdspnd.getValue(
								"BILL_FLG", k));
						resultdspnd.setData("OPT_USER", Operator.getID());
						resultdspnd.setData("OPT_TERM", Operator.getIP());
						resultdspnd.setData("TAKEMED_ORG", parmdspnd.getValue(
								"CASE_NO", k));
						IVARefundMedicineTool.getInstance().OninsertDspnd(
								resultdspnd);
					}
				}
				result = TIOM_AppServer.executeAction("action.spc.IVADsAciton",
						"onUpdateReturn", rowParm);

				if (result.getErrCode() < 0) {
					temParm.addParm(rowParm);
				} else {
				}
			} else {
				temParm.setRowData(i, rowParm);
			}
		}

		table_d.removeRowAll();
		table_m.setParmValue(temParm);
	}

	/*
	 * ȫѡ
	 */
	public void onSelectAll() {
		table_m = getTTable("TABLE_M");
		table_m.acceptText();
		if (table_m.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < table_m.getRowCount(); i++) {
			table_m.setItem(i, "SELECT_AVG", getValueString("SELECT_ALL"));
		}
	}

	/*
	 * ��ѡ���¼�
	 */
	public void onTableCheckBoxClicked(Object obj) {
		if (getRadioButton("BUTTON_M").isSelected()) {
			table_m = this.getTTable("TABLE_M");
			int column = table_m.getSelectedRow();
			if ("N".equals(table_m.getItemString(column, "SELECT_AVG"))) {
				table_m.setItem(column, "SELECT_AVG", "Y");
			} else {
				table_m.setItem(column, "SELECT_AVG", "N");
			}
		}

	}
	
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		// �ж��Ƿ�����ӡ����ȫ����ӡ
		String select_all = this.getValueString("SELECT_ALL");
		if("Y".equals(select_all)){
			onPrintAll();
		} else {
			onPrintSingle();
		}
	}
	
	/**
	 * ȫ����ӡ����
	 */
	public void onPrintAll(){
		table_m = getTTable("TABLE_M");
		if(table_m.getRowCount() <= 0){
			messageBox("û��Ҫ��ӡ������");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", "����������ҩ�嵥");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// �������
		TParm parm = new TParm();
		TParm result = IVADeploymentTool.getInstance().queryAllData();
		// ��������е�Ԫ��
		for (int i = 0; i < result.getCount(); i++) {
			parm.addData("PAT_NAME", result.getValue("PAT_NAME", i));
			parm.addData("MR_NO", result.getValue("MR_NO", i));
			parm.addData("STATION_DESC", result.getValue("STATION_DESC", i));
			parm.addData("BED_NO_DESC", result.getValue("BED_NO_DESC", i));
			parm.addData("ORDER_DESC", result.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", result.getValue("SPECIFICATION", i));
			parm.addData("MEDI_QTY", result.getValue("MEDI_QTY", i));
			parm.addData("FREQ_CHN_DESC", result.getValue("FREQ_CHN_DESC", i));
		}
		// ������
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
		// �����ŵ�������
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADeploymentList.jhw", data);
	}
	
	/**
	 * ������ӡ����
	 */
	public void onPrintSingle(){
		table_d = getTTable("TABLE_D");
		if(table_d.getRowCount() <= 0){
			messageBox("û��Ҫ��ӡ������");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", "����������ҩ�嵥");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// �������
		TParm parm = new TParm();
		int rowM = table_m.getSelectedRow();
		TParm tableParmM = table_m.getParmValue().getRow(rowM);
		TParm tableParmD = table_d.getParmValue();
		// ��������е�Ԫ��
		for (int i = 0; i < table_d.getRowCount(); i++) {
			parm.addData("PAT_NAME", tableParmM.getValue("PAT_NAME"));
			parm.addData("MR_NO", tableParmM.getValue("MR_NO"));
			parm.addData("STATION_DESC", tableParmM.getValue("STATION_DESC"));
			parm.addData("BED_NO_DESC", tableParmM.getValue("BED_NO_DESC"));
			parm.addData("ORDER_DESC", tableParmD.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParmD.getValue("SPECIFICATION", i));
			parm.addData("MEDI_QTY", tableParmD.getValue("MEDI_QTY", i));
			parm.addData("FREQ_CHN_DESC", tableParmD.getValue("FREQ_CHN_DESC", i));
		}
		// ������
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
		// �����ŵ�������
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADeploymentList.jhw", data);
	}

}
