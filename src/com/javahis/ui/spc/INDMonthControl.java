package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.javahis.system.combo.TComboOrgCode;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;  
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.ui.TComboBox;
import jdo.spc.INDSQL;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: �ս�/�½ᱨ��Control
 * </p>
 * 
 * Description: �ս�/�½ᱨ��Control </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.09.02
 * @version 1.0
 */
public class INDMonthControl extends TControl {

	// ȫ������Ȩ��
	private boolean dept_flg = true;

	public INDMonthControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��������
		initPage();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if ("".equals(this.getValueString("ORG_CODE"))) {
			this.messageBox("��ѡ���ѯ����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
		if (getRadioButton("TYPE_A").isSelected()) {
			parm.setData("ORG_TYPE", "A");
			parm.setData("TITLE", "ҩ����/�½ᱨ��");
		} else {
			parm.setData("ORG_TYPE", "B");
			parm.setData("TITLE", "ҩ����/�½ᱨ��");
		}
		if (getRadioButton("DAY_TYPE").isSelected()) {
			if (TypeTool.getTimestamp(this.getValue("START_DATE")).compareTo(
					TypeTool.getTimestamp(this.getValue("END_DATE"))) > 0) {
				this.messageBox("ͳ��ʱ�����");
				return;
			}
			// �սᱨ��
			parm.setData("START_DATE", this.getValueString("START_DATE")
					.substring(0, 10).replaceAll("-", ""));
			parm.setData("END_DATE", this.getValueString("END_DATE").substring(
					0, 10).replaceAll("-", ""));
		} else {
			// �½ᱨ��
			String month_area = this.getValueString("MONTH_AREA");
			String year = month_area.substring(0, 4);
			String month = month_area.substring(5, 7);
			// luhai modify �����ӡ�½ᱨ��ʱ�Ŀ�ʼ�ͽ���ʱ�� begin
			// parm.setData("START_DATE", year + month + "01");
			// if ("12".equals(month)) {
			// year = TypeTool.getString(TypeTool.getInt(year) + 1);
			// month = "01";
			// }
			// else {
			// month = TypeTool.getString(TypeTool.getInt(month) + 1);
			// }
			// Timestamp end_date = StringTool.getTimestamp(year + month + "01",
			// "yyyyMMdd");
			// parm.setData("END_DATE",
			// StringTool.rollDate(end_date, -1).toString().
			// substring(0, 10).replaceAll("-", ""));
			// ����ʱ��Ϊ���µ�25��
			parm.setData("END_DATE", year + month + "25");
			// ��ʼʱ��Ϊ���µ�25��
			if ("01".equals(month)) {
				year = TypeTool.getString(TypeTool.getInt(year) - 1);
				month = "12";
			} else {
				String str = month;
				month = TypeTool.getString(TypeTool.getInt(month) - 1);
				if (str.startsWith("0")) {
					month = "0" + month;
				}
			}
			parm.setData("START_DATE", year + month + "26");
			// luhai modify �����ӡ�½ᱨ��ʱ�Ŀ�ʼ�ͽ���ʱ�� end
		}
		// ��ʼ����ǰһ��
		String last_trandate = StringTool.rollDate(
				StringTool
						.getTimestamp(parm.getValue("START_DATE"), "yyyyMMdd"),
				-1).toString();
		parm.setData("LAST_TRANDATE", last_trandate.substring(0, 4)
				+ last_trandate.substring(5, 7)
				+ last_trandate.substring(8, 10));
		// System.out.println("parm---"+parm);
		TParm result = new TParm();
		if (getRadioButton("DAY_TYPE").isSelected()) {
			// �սᱨ��
			result = TIOM_AppServer.executeAction("action.spc.INDMonthAction",
					"onQueryDay", parm);
			if (result.getCount("TYPE_CODE") == 0) {
				this.messageBox("�޲�ѯ���");
				return;
			}
		} else {
			// �½ᱨ��
			result = TIOM_AppServer.executeAction("action.spc.INDMonthAction",
					"onQueryDay", parm);
			if (result.getCount("TYPE_CODE") == 0) {
				this.messageBox("�޲�ѯ���");
				return;
			}
		}
		// System.out.println("result=="+result);
		// ��ӡ����
		parm.setData("DATA_PARM", result.getData());
		onPrintData(parm);

		// onPrintExcel(parm);
	}

	// /**
	// * ��ӡ����
	// */
	// public void onPrint() {
	// onQuery();
	// }

	/**
	 * ��շ���
	 */
	public void onClear() {
		getRadioButton("DAY_TYPE").setSelected(true);
		getRadioButton("TYPE_A").setSelected(true);
		this.setValue("ORG_CODE", "");
		if (!dept_flg) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndOrgByUserId(Operator.getID(), Operator
							.getRegion(), " AND B.ORG_TYPE = 'A' ")));
			getComboBox("ORG_CODE").setParmValue(parm);
		} else {
			TComboOrgCode org_code = (TComboOrgCode) this
					.getComponent("ORG_CODE");
			org_code.setOrgType("A");
			org_code.onQuery();
		}
		Timestamp date = SystemTool.getInstance().getDate();
		date = StringTool.rollDate(date, -1);
		this.setValue("MONTH_AREA", date.toString().substring(0, 7).replace(
				'-', '/'));
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/'));
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/'));
		getTextFormat("MONTH_AREA").setEnabled(false);
		getTextFormat("START_DATE").setEnabled(true);
		getTextFormat("END_DATE").setEnabled(true);
	}

	/**
	 * ���ͳ�����
	 */
	public void onChangeCountType() {
		if (getRadioButton("DAY_TYPE").isSelected()) {
			getTextFormat("MONTH_AREA").setEnabled(false);
			getTextFormat("START_DATE").setEnabled(true);
			getTextFormat("END_DATE").setEnabled(true);
		} else {
			getTextFormat("MONTH_AREA").setEnabled(true);
			getTextFormat("START_DATE").setEnabled(false);
			getTextFormat("END_DATE").setEnabled(false);
		}
	}

	/**
	 * ����������
	 */
	public void onChangeOrgType() {
		this.setValue("ORG_CODE", "");
		if (!dept_flg) {
			if (getRadioButton("TYPE_A").isSelected()) {
				TParm parm = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getIndOrgByUserId(Operator.getID(), Operator
								.getRegion(), " AND B.ORG_TYPE = 'A' ")));
				getComboBox("ORG_CODE").setParmValue(parm);
			} else {
				TParm parm = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getIndOrgByUserId(Operator.getID(), Operator
								.getRegion(), " AND B.ORG_TYPE = 'B' ")));
				getComboBox("ORG_CODE").setParmValue(parm);
			}
		} else {
			TComboOrgCode org_code = (TComboOrgCode) this
					.getComponent("ORG_CODE");
			if (getRadioButton("TYPE_A").isSelected()) {
				org_code.setOrgType("A");
			} else {
				org_code.setOrgType("B");
			}
			org_code.onQuery();
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʾȫԺҩ�ⲿ��
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndOrgByUserId(Operator.getID(), Operator
							.getRegion(), " AND B.ORG_TYPE = 'A' ")));
			getComboBox("ORG_CODE").setParmValue(parm);
			if (parm.getCount("NAME") > 0) {
				getComboBox("ORG_CODE").setSelectedIndex(1);
			}
			dept_flg = false;
		}

		Timestamp date = SystemTool.getInstance().getDate();
		date = StringTool.rollDate(date, -1);
		this.setValue("MONTH_AREA", date.toString().substring(0, 7).replace(
				'-', '/'));
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/'));
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/'));
		this.setValue("ORG_CODE", Operator.getDept());
	}

	/**
	 * ��ӡ����
	 * 
	 * @param parm
	 *            TParm
	 */
	private void onPrintData(TParm parm) {
		TParm inParm = new TParm();
		Timestamp datetime = SystemTool.getInstance().getDate();
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE_1", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()));
		data.setData("TITLE_2", "TEXT", parm.getValue("TITLE"));
		data.setData("ORG_CODE", "TEXT", "ͳ�Ʋ���: "
				+ this.getComboBox("ORG_CODE").getSelectedName());
		String start_date = parm.getValue("START_DATE");
		String end_date = parm.getValue("END_DATE");

		data.setData("DATE_AREA", "TEXT", "ͳ������: " + start_date.substring(0, 4)
				+ "/" + start_date.substring(4, 6) + "/"
				+ start_date.substring(6, 8) + " " + "00:00:00" + " ~ "
				+ end_date.substring(0, 4) + "/" + end_date.substring(4, 6)
				+ "/" + end_date.substring(6, 8) + " " + "23:59:59");
		data.setData("DATE", "TEXT", "�Ʊ�����: "
				+ datetime.toString().substring(0, 10).replace('-', '/'));
		data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());

		// �������
		TParm dataParm = parm.getParm("DATA_PARM");
		// System.out.println("dataParm----" + dataParm);

		// ���ڽ��ɱ�����ܼ�
		double last_stock_sum = 0;
		// ���ڽ�����۽���ܼ�
		double last_own_sum = 0;
		// �������ɱ�����ܼ�
		double in_stock_sum = 0;
		// ����������۽���ܼ�
		double in_own_sum = 0;
		// ���ڳ���ɱ�����ܼ�
		double out_stock_sum = 0;
		// ���ڳ������۽���ܼ�
		double out_own_sum = 0;
		// ��ӯ���ɱ�����ܼ�
		double checkmodi_stock_sum = 0;
		// ��ӯ�����۽���ܼ�
		double checkmodi_own_sum = 0;
		// ���ڽ��ɱ�����ܼ�
		double stock_sum = 0;
		// ���ڽ�����۽���ܼ�
		double own_sum = 0;
		// �����������ܼ�
		double profit_loss_sum = 0;
		// ��Ӧ�м��ַ����ӡ������ (pha_base type_code ����)
		for (int i = 0; i < dataParm.getCount("TYPE_DESC"); i++) {
			// �������ɱ����С��
			double in_stock_amt = 0;
			// ����������۽��С��
			double in_own_amt = 0;
			// ���ڳ���ɱ����С��
			double out_stock_amt = 0;
			// ���ڳ������۽��С��
			double out_own_amt = 0;

			if ("A".equals(parm.getValue("ORG_TYPE"))) {  
				// ��һ��
				inParm.addData("TYPE_DESC", dataParm.getValue("TYPE_DESC", i));
				inParm.addData("LAST_STOCK_AMT", StringTool.round(dataParm
						.getDouble("LAST_STOCK_AMT", i), 2));
				inParm.addData("LAST_OWN_AMT", StringTool.round(dataParm
						.getDouble("LAST_OWN_AMT", i), 2));
				// 0
				inParm.addData("LAST_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("LAST_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("LAST_STOCK_AMT",
								i), 2), 2));
				inParm.addData("PROJECT_IN", "���");
				in_stock_amt = in_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"VERIFYIN_STOCK_AMT", i), 2);
				inParm.addData("IN_STOCK_AMT", StringTool.round(dataParm
						.getDouble("VERIFYIN_STOCK_AMT", i), 2));
				in_own_amt = in_own_amt
						+ StringTool.round(dataParm.getDouble(
								"VERIFYIN_OWN_AMT", i), 2);
				inParm.addData("IN_OWN_AMT", StringTool.round(dataParm
						.getDouble("VERIFYIN_OWN_AMT", i), 2));

				// 1
				inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("VERIFYIN_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"VERIFYIN_STOCK_AMT", i), 2), 2));
				inParm.addData("PROJECT_OUT", "�˻�");
				out_stock_amt = out_stock_amt
						+ StringTool.round(out_stock_amt
								+ dataParm.getDouble("REGRESSGOODS_STOCK_AMT",
										i), 2);
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("REGRESSGOODS_STOCK_AMT", i), 2));
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble(
								"REGRESSGOODS_OWN_AMT", i), 2);
				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("REGRESSGOODS_OWN_AMT", i), 2));
				// 2
				inParm.addData("OUT_DIFF_AMT", StringTool.round(
						StringTool.round(dataParm.getDouble(
								"REGRESSGOODS_OWN_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"REGRESSGOODS_STOCK_AMT", i), 2), 2));
				inParm.addData("CHECKMODI_STOCK_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_STOCK_AMT", i), 2));
				inParm.addData("CHECKMODI_OWN_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_OWN_AMT", i), 2));
				inParm.addData("CHECKMODI_DIFF_AMT", StringTool.round(
						StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2));
				inParm.addData("STOCK_AMT", StringTool.round(dataParm
						.getDouble("STOCK_AMT", i), 2));
				inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
						"OWN_AMT", i), 2));
				// 4
				inParm.addData("DIFF_AMT", StringTool.round(StringTool.round(
						dataParm.getDouble("OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("STOCK_AMT", i),
								2), 2));
				inParm.addData("PROFIT_LOSS_AMT", "");

				// �ڶ���

				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");

				inParm.addData("PROJECT_IN", "");
				inParm.addData("IN_STOCK_AMT", "");
				inParm.addData("IN_OWN_AMT", "");
				inParm.addData("IN_DIFF_AMT", "");

				inParm.addData("PROJECT_OUT", "����");
				out_stock_amt = out_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_STOCK_AMT", i), 2);
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("REQUEST_OUT_STOCK_AMT", i), 2));
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_OWN_AMT", i), 2);

				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("REQUEST_OUT_OWN_AMT", i), 2));
				//
				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("REQUEST_OUT_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_STOCK_AMT", i), 2), 2));

				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");

				inParm.addData("PROJECT_IN", "");
				inParm.addData("IN_STOCK_AMT", "");
				inParm.addData("IN_OWN_AMT", "");
				inParm.addData("IN_DIFF_AMT", "");

				inParm.addData("PROJECT_OUT", "�˿�");
				out_stock_amt = out_stock_amt
						- StringTool.round(dataParm.getDouble(
								"RET_IN_STOCK_AMT", i), 2);
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("RET_IN_STOCK_AMT", i), 2) > 0 ? -StringTool
						.round(dataParm.getDouble("RET_IN_STOCK_AMT", i), 2)
						: 0.0);
				// fux modify �����㵽�� (����)

				out_own_amt = out_own_amt
						- StringTool.round(dataParm.getDouble("RET_IN_OWN_AMT",
								i), 2);
				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("RET_IN_OWN_AMT", i), 2) > 0 ? -StringTool
						.round(dataParm.getDouble("RET_IN_OWN_AMT", i), 2)
						: 0.0);
				// fux need ��ע 20141205
				inParm
						.addData(
								"OUT_DIFF_AMT",
								StringTool.round(StringTool.round(dataParm
										.getDouble("RET_IN_OWN_AMT", i), 2)
										- StringTool.round(dataParm.getDouble(
												"RET_IN_STOCK_AMT", i), 2), 2) > 0 ? -StringTool
										.round(
												StringTool
														.round(
																dataParm
																		.getDouble(
																				"RET_IN_OWN_AMT",
																				i),
																2)
														- StringTool
																.round(
																		dataParm
																				.getDouble(
																						"RET_IN_STOCK_AMT",
																						i),
																		2), 2)
										: 0.0);

				// inParm.addData("CHECKMODI_STOCK_AMT", "");
				// inParm.addData("CHECKMODI_OWN_AMT", "");
				// inParm.addData("CHECKMODI_DIFF_AMT", "");

				// fux modify 20160105

				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");

				inParm.addData("PROJECT_IN", "");
				inParm.addData("IN_STOCK_AMT", "");
				inParm.addData("IN_OWN_AMT", "");
				inParm.addData("IN_DIFF_AMT", "");

				inParm.addData("PROJECT_OUT", "����");
				out_stock_amt = out_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"WAS_OUT_STOCK_AMT", i), 2);
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("WAS_OUT_STOCK_AMT", i), 2) > 0 ? StringTool
						.round(dataParm.getDouble("WAS_OUT_STOCK_AMT", i), 2)
						: 0.0);
				// fux modify �����㵽�� (����)
  
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble( 
								"WAS_OUT_OWN_AMT", i), 2);
				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("WAS_OUT_OWN_AMT", i), 2) > 0 ? StringTool
						.round(dataParm.getDouble("WAS_OUT_OWN_AMT", i), 2)
						: 0.0);
				// fux need ��ע 20141205
				inParm
						.addData(
								"OUT_DIFF_AMT",
								StringTool.round(StringTool.round(dataParm
										.getDouble("WAS_OUT_OWN_AMT", i), 2)
										- StringTool.round(dataParm.getDouble(
												"WAS_OUT_STOCK_AMT", i), 2), 2) > 0 ? StringTool
										.round(
												StringTool
														.round(
																dataParm
																		.getDouble(
																				"WAS_OUT_OWN_AMT",
																				i),
																2)
														- StringTool
																.round(
																		dataParm
																				.getDouble(
																						"WAS_OUT_STOCK_AMT",
																						i),
																		2), 2)
										: 0.0);
				
				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				// ���
				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "");

				inParm.addData("IN_STOCK_AMT", "");

				inParm.addData("IN_OWN_AMT", "");
				//
				inParm.addData("IN_DIFF_AMT", "");
				inParm.addData("PROJECT_OUT", "��ֲ��");
				// С�� ��ȥ ϸ���

				inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool
						.round(StringTool.round(dataParm.getDouble(
								"LAST_STOCK_AMT", i), 2)
								+ StringTool.round(in_stock_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2)
						- StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_STOCK_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"REGRESSGOODS_STOCK_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"WAS_OUT_STOCK_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble(
								"RET_IN_STOCK_AMT", i), 2), 2));
				inParm.addData("OUT_OWN_AMT", 0);

				
				inParm.addData("OUT_DIFF_AMT", StringTool.round(0 - StringTool
						.round(StringTool.round(StringTool.round(dataParm
								.getDouble("LAST_STOCK_AMT", i), 2)
								+ StringTool.round(in_stock_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2)
								- StringTool.round(dataParm.getDouble(
										"REQUEST_OUT_STOCK_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"REGRESSGOODS_STOCK_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"WAS_OUT_STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"RET_IN_STOCK_AMT", i), 2), 2), 2));

				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");

				inParm.addData("PROFIT_LOSS_AMT", "");

				// ������
				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "С��");
				inParm.addData("IN_STOCK_AMT", StringTool
						.round(in_stock_amt, 2));
				inParm.addData("IN_OWN_AMT", StringTool.round(in_own_amt, 2));
				inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool
						.round(in_own_amt, 2)
						- StringTool.round(in_stock_amt, 2), 2));
				inParm.addData("PROJECT_OUT", "С��");
				inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("LAST_STOCK_AMT", i), 2)
						+ StringTool.round(in_stock_amt, 2)
						- StringTool.round(dataParm.getDouble("STOCK_AMT", i),
								2)
						+ StringTool.round(dataParm.getDouble(
								"CHECKMODI_STOCK_AMT", i), 2), 2));
				inParm.addData("OUT_OWN_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("LAST_OWN_AMT", i), 2)
						+ StringTool.round(in_own_amt, 2)
						- StringTool.round(dataParm.getDouble("OWN_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2), 2));
				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(StringTool.round(dataParm.getDouble(
								"LAST_OWN_AMT", i), 2)
								+ StringTool.round(in_own_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"OWN_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_OWN_AMT", i), 2), 2)
						- (StringTool.round(StringTool.round(dataParm
								.getDouble("LAST_STOCK_AMT", i), 2)  
								+ StringTool.round(in_stock_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2)), 2));
				// fux modify 20141123  
				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");  
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");
				
				inParm.addData("CHECKMODI_STOCK_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_STOCK_AMT", i), 2));
				inParm.addData("CHECKMODI_OWN_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_OWN_AMT", i), 2));
				// 
				inParm.addData("CHECKMODI_DIFF_AMT", StringTool.round(
						StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2));
				inParm.addData("STOCK_AMT", StringTool.round(dataParm
						.getDouble("STOCK_AMT", i), 2));
				inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
						"OWN_AMT", i), 2));
				inParm.addData("DIFF_AMT", StringTool.round(StringTool.round(
						dataParm.getDouble("OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("STOCK_AMT", i),
								2), 2));
				double profit = StringTool.round(StringTool.round(dataParm
						.getDouble("OWN_AMT", i), 2)
						- StringTool.round(dataParm
								.getDouble("LAST_OWN_AMT", i), 2)
						- StringTool.round(in_own_amt, 2)
						+ StringTool.round(out_own_amt, 2)
						- StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2), 2);
				//
				inParm.addData("PROFIT_LOSS_AMT", StringTool.round(profit, 2));

				// �ܼ�
				last_stock_sum += StringTool.round(dataParm.getDouble(
						"LAST_STOCK_AMT", i), 2);
				last_own_sum += StringTool.round(dataParm.getDouble(
						"LAST_OWN_AMT", i), 2);
				in_stock_sum += StringTool.round(in_stock_amt, 2);
				in_own_sum += StringTool.round(in_own_amt, 2);
				out_stock_sum = out_stock_sum
						+ StringTool.round(out_stock_amt, 2);
				out_own_sum += StringTool.round(out_own_amt, 2);
				checkmodi_stock_sum += StringTool.round(dataParm.getDouble(
						"CHECKMODI_STOCK_AMT", i), 2);
				checkmodi_own_sum += StringTool.round(dataParm.getDouble(
						"CHECKMODI_OWN_AMT", i), 2);
				stock_sum += StringTool.round(dataParm
						.getDouble("STOCK_AMT", i), 2);
				own_sum += StringTool
						.round(dataParm.getDouble("OWN_AMT", i), 2);
				profit_loss_sum += profit;
			} else {
				// ��һ��    
				inParm.addData("TYPE_DESC", dataParm.getValue("TYPE_DESC", i));
				inParm.addData("LAST_STOCK_AMT", StringTool.round(dataParm
						.getDouble("LAST_STOCK_AMT", i), 2));
				inParm.addData("LAST_OWN_AMT", StringTool.round(dataParm
						.getDouble("LAST_OWN_AMT", i), 2));
				//
				inParm.addData("LAST_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("LAST_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("LAST_STOCK_AMT",
								i), 2), 2));
				inParm.addData("PROJECT_IN", "����");
				in_stock_amt = in_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"REQUEST_IN_STOCK_AMT", i), 2);
				in_stock_amt = in_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"GIF_IN_STOCK_AMT", i), 2);
				in_stock_amt = in_stock_amt
						+ StringTool.round(dataParm
								.getDouble("IN_STOCK_AMT", i), 2);
				// fux modify 20150519
				in_own_amt = in_own_amt
						+ StringTool.round(dataParm.getDouble(
								"REQUEST_IN_OWN_AMT", i), 2);
				in_own_amt = in_own_amt
						+ StringTool.round(dataParm.getDouble("IN_OWN_AMT", i),
								2);
				in_own_amt = in_own_amt
						+ StringTool.round(dataParm.getDouble("GIF_IN_OWN_AMT",
								i), 2);
				inParm.addData("IN_STOCK_AMT", StringTool.round(dataParm
						.getDouble("REQUEST_IN_STOCK_AMT", i), 2));
				inParm.addData("IN_OWN_AMT", StringTool.round(dataParm
						.getDouble("REQUEST_IN_OWN_AMT", i), 2));
				inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("REQUEST_IN_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"REQUEST_IN_STOCK_AMT", i), 2), 2));
				inParm.addData("PROJECT_OUT", "�˿�");
				out_stock_amt = out_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"RET_OUT_STOCK_AMT", i), 2);
				out_stock_amt = out_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_STOCK_AMT", i), 2);
				out_stock_amt = out_stock_amt
						+ StringTool.round(dataParm.getDouble(
								"GIF_OUT_STOCK_AMT", i), 2);
				out_stock_amt = out_stock_amt
						+ StringTool.round(dataParm.getDouble("OUT_STOCK_AMT",
								i), 2);
				out_stock_amt = out_stock_amt
						- StringTool.round(dataParm.getDouble(
								"RET_IN_STOCK_AMT", i), 2);
				out_stock_amt = out_stock_amt
						- StringTool.round(dataParm.getDouble(
								"THI_IN_STOCK_AMT", i), 2);
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble(
								"RET_OUT_OWN_AMT", i), 2);
				out_own_amt = out_own_amt
						- StringTool.round(dataParm.getDouble("RET_IN_OWN_AMT",
								i), 2);
				out_own_amt = out_own_amt
						- StringTool.round(dataParm.getDouble("THI_IN_OWN_AMT",
								i), 2);
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble(
								"GIF_OUT_OWN_AMT", i), 2);
				out_own_amt = out_own_amt
						+ StringTool.round(
								dataParm.getDouble("OUT_OWN_AMT", i), 2);
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_OWN_AMT", i), 2);
				//fux modify 20160112  
				out_own_amt = out_own_amt
				        + StringTool.round(dataParm.getDouble(
				 		        "WAS_OUT_OWN_AMT", i), 2);
				// fux modify 20141210
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("RET_OUT_STOCK_AMT", i), 2));

				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("RET_OUT_OWN_AMT", i), 2));
				//  
				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("RET_OUT_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"RET_OUT_STOCK_AMT", i), 2), 2));
				inParm.addData("CHECKMODI_STOCK_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_STOCK_AMT", i), 2));
				inParm.addData("CHECKMODI_OWN_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_OWN_AMT", i), 2));
				// ��ӯ������Ĳ�ֲ�۸�Ϊ���
				inParm.addData("CHECKMODI_DIFF_AMT", StringTool.round(
						StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2));
				inParm.addData("STOCK_AMT", StringTool.round(dataParm
						.getDouble("STOCK_AMT", i), 2));
				inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
						"OWN_AMT", i), 2));
				inParm.addData("DIFF_AMT", StringTool.round(StringTool.round(
						dataParm.getDouble("OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("STOCK_AMT", i),
								2), 2));
				inParm.addData("PROFIT_LOSS_AMT", "");

				// �ڶ��� �ӿ����� ������ ��Ϊ���� ����
				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "����");
				inParm.addData("IN_STOCK_AMT", StringTool.round(dataParm
						.getDouble("GIF_IN_STOCK_AMT", i), 2));

				inParm.addData("IN_OWN_AMT", StringTool.round(dataParm
						.getDouble("GIF_IN_OWN_AMT", i), 2));
				//
				inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("GIF_IN_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"GIF_IN_STOCK_AMT", i), 2), 2));
				inParm.addData("PROJECT_OUT", "����");
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("GIF_OUT_STOCK_AMT", i), 2));
				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("GIF_OUT_OWN_AMT", i), 2));
				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("GIF_OUT_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"GIF_OUT_STOCK_AMT", i), 2), 2));
				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				// ������ ���� ���� ��Ϊ ��ҩ ��ҩ

				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "��ҩ");

				inParm.addData("IN_STOCK_AMT", StringTool.round(dataParm
						.getDouble("IN_STOCK_AMT", i), 2));

				inParm.addData("IN_OWN_AMT", StringTool.round(dataParm
						.getDouble("IN_OWN_AMT", i), 2));
				//    
				inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("IN_OWN_AMT", i), 2)
						- StringTool.round(dataParm
								.getDouble("IN_STOCK_AMT", i), 2), 2));
				inParm.addData("PROJECT_OUT", "��ҩ");
				// ȫ�̱䶯 --- fux
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("OUT_STOCK_AMT", i), 2));

				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("OUT_OWN_AMT", i), 2));

				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("OUT_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("OUT_STOCK_AMT",
								i), 2), 2));

				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				// ������ ���� ������
				// REQUEST_OUT_QTY + COS_OUT_QTY Ϊ������
				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "");

				inParm.addData("IN_STOCK_AMT", "");
				// in_own_amt = in_own_amt + dataParm.getDouble("IN_OWN_AMT",
				// i);
				inParm.addData("IN_OWN_AMT", "");
				//    
				inParm.addData("IN_DIFF_AMT", "");
				inParm.addData("PROJECT_OUT", "������");
				// ȫ�̱䶯 --- fux
				// ���Ĳ�Ҳ���з�ҩ
				// REQUEST_OUT_QTY ������

				// fux modify
				out_own_amt = out_own_amt
						+ StringTool.round(dataParm.getDouble(
								"COS_OUT_OWN_AMT", i), 2);
				inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("REQUEST_OUT_STOCK_AMT", i),
								2)
						+ StringTool.round(dataParm.getDouble(
								"COS_OUT_STOCK_AMT", i), 2), 2));

				inParm.addData("OUT_OWN_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("REQUEST_OUT_OWN_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble(
								"COS_OUT_OWN_AMT", i), 2), 2));

				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_OWN_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"COS_OUT_OWN_AMT", i), 2), 2)
						- StringTool.round(StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"COS_OUT_STOCK_AMT", i), 2), 2), 2));

				// if ("���Ĳ�".equals(dataParm.getValue("TYPE_DESC", i))) {
				//
				// inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
				// .getDouble("REQUEST_OUT_STOCK_AMT", i), 2));
				//
				// inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
				// .getDouble("REQUEST_OUT_OWN_AMT", i), 2));
				//
				// inParm
				// .addData(
				// "OUT_DIFF_AMT",
				// StringTool
				// .round(
				// StringTool
				// .round(
				// dataParm
				// .getDouble(
				// "REQUEST_OUT_OWN_AMT",
				// i),
				// 2)
				// - StringTool
				// .round(
				// dataParm
				// .getDouble(
				// "REQUEST_OUT_STOCK_AMT",
				// i),
				// 2),
				// 2));
				// } else {
				// out_own_amt = out_own_amt
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_OWN_AMT", i), 2);
				// inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool
				// .round(dataParm.getDouble("REQUEST_OUT_STOCK_AMT",
				// i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_STOCK_AMT", i), 2), 2));
				//
				// inParm.addData("OUT_OWN_AMT", StringTool.round(
				// StringTool.round(dataParm.getDouble(
				// "REQUEST_OUT_OWN_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_OWN_AMT", i), 2), 2));
				//
				// inParm
				// .addData(
				// "OUT_DIFF_AMT",
				// StringTool
				// .round(
				// StringTool
				// .round(
				// StringTool
				// .round(
				// dataParm
				// .getDouble(
				// "REQUEST_OUT_OWN_AMT",
				// i),
				// 2)
				// + StringTool
				// .round(
				// dataParm
				// .getDouble(
				// "COS_OUT_OWN_AMT",
				// i),
				// 2),
				// 2)
				// - StringTool
				// .round(
				// StringTool
				// .round(
				// dataParm
				// .getDouble(
				// "REQUEST_OUT_STOCK_AMT",
				// i),
				// 2)
				// + StringTool
				// .round(
				// dataParm
				// .getDouble(
				// "COS_OUT_STOCK_AMT",
				// i),
				// 2),
				// 2),
				// 2));
				// }
				// }
				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");
				// ������ ���� ������
				// THI_IN_QTY + RET_IN_QTY Ϊ������

				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "");

				inParm.addData("IN_STOCK_AMT", "");

				inParm.addData("IN_OWN_AMT", "");
				//
				inParm.addData("IN_DIFF_AMT", "");
				inParm.addData("PROJECT_OUT", "������");
				// out_stock_amt = out_stock_amt
				// + dataParm.getDouble("THO_OUT_STOCK_AMT", i);
				// ������ ��Ϊ ������
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("RET_IN_STOCK_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble(
								"THI_IN_STOCK_AMT", i), 2) > 0 ? StringTool
						.round(-StringTool.round(dataParm.getDouble(
								"RET_IN_STOCK_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"THI_IN_STOCK_AMT", i), 2), 2) : 0.0);
				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("RET_IN_OWN_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble("THI_IN_OWN_AMT",
								i), 2) > 0 ? StringTool.round(-StringTool
						.round(dataParm.getDouble("RET_IN_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("THI_IN_OWN_AMT",
								i), 2), 2) : 0.0);
				inParm.addData("OUT_DIFF_AMT", StringTool.round(dataParm
						.getDouble("RET_IN_OWN_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble("THI_IN_OWN_AMT",
								i), 2) > 0 ? -StringTool.round(StringTool
						.round(StringTool.round(dataParm.getDouble(
								"RET_IN_OWN_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"THI_IN_OWN_AMT", i), 2), 2)
						- StringTool.round(StringTool.round(dataParm.getDouble(
								"RET_IN_STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"THI_IN_STOCK_AMT", i), 2), 2), 2)
						: 0.0);
				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				// fux modify 20160105
				// ������ ���� ����
				// WAS Ϊ����

				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "");

				inParm.addData("IN_STOCK_AMT", "");

				inParm.addData("IN_OWN_AMT", "");
				//
				inParm.addData("IN_DIFF_AMT", "");
				inParm.addData("PROJECT_OUT", "����");
				// out_stock_amt = out_stock_amt
				// + dataParm.getDouble("THO_OUT_STOCK_AMT", i);
				// ������ ��Ϊ ������
				inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
						.getDouble("WAS_OUT_STOCK_AMT", i), 2) > 0 ? StringTool
						.round(StringTool.round(dataParm.getDouble(
								"WAS_OUT_STOCK_AMT", i), 2), 2) : 0.0);
				inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
						.getDouble("WAS_OUT_OWN_AMT", i), 2) > 0 ? StringTool
						.round(StringTool.round(dataParm.getDouble(
								"WAS_OUT_OWN_AMT", i), 2), 2) : 0.0);
				inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("WAS_OUT_OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"WAS_OUT_STOCK_AMT", i), 2), 2));
				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				// ���Ϊ���Ĳ���ʱ��ʾ
				// if ("���Ĳ�".equals(dataParm.getValue("TYPE_DESC", i))) {
				// // ������
				// inParm.addData("TYPE_DESC", "");
				// inParm.addData("LAST_STOCK_AMT", "");
				// inParm.addData("LAST_OWN_AMT", "");
				// inParm.addData("LAST_DIFF_AMT", "");
				// inParm.addData("PROJECT_IN", "");
				// // in_stock_amt = in_stock_amt;
				// inParm.addData("IN_STOCK_AMT", "");
				// // in_own_amt = in_own_amt;
				// inParm.addData("IN_OWN_AMT", "");
				// inParm.addData("IN_DIFF_AMT", "");
				// inParm.addData("PROJECT_OUT", "���Ĳ�");
				// out_stock_amt = out_stock_amt
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_STOCK_AMT", i), 2);
				//
				// out_own_amt = out_own_amt
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_OWN_AMT", i), 2);
				//
				// inParm.addData("OUT_STOCK_AMT", StringTool.round(dataParm
				// .getDouble("COS_OUT_STOCK_AMT", i), 2));
				// inParm.addData("OUT_OWN_AMT", StringTool.round(dataParm
				// .getDouble("COS_OUT_OWN_AMT", i), 2));
				// //
				// inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool
				// .round(dataParm.getDouble("COS_OUT_OWN_AMT", i), 2)
				// - StringTool.round(dataParm.getDouble(
				// "COS_OUT_STOCK_AMT", i), 2), 2));
				// inParm.addData("CHECKMODI_STOCK_AMT", "");
				// inParm.addData("CHECKMODI_OWN_AMT", "");
				// inParm.addData("CHECKMODI_DIFF_AMT", "");
				// inParm.addData("STOCK_AMT", "");
				// inParm.addData("OWN_AMT", "");
				// inParm.addData("DIFF_AMT", "");
				// inParm.addData("PROFIT_LOSS_AMT", "");
				// }

				// ���
				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "");

				inParm.addData("IN_STOCK_AMT", "");

				inParm.addData("IN_OWN_AMT", "");
				//
				inParm.addData("IN_DIFF_AMT", "");
				inParm.addData("PROJECT_OUT", "��ֲ��");
				// С�� ��ȥ ϸ���

				inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool
						.round(StringTool.round(dataParm.getDouble(
								"LAST_STOCK_AMT", i), 2)
								+ StringTool.round(in_stock_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2)
						- StringTool.round(dataParm.getDouble(
								"RET_OUT_STOCK_AMT", i), 2)
						- StringTool.round(dataParm.getDouble(
								"GIF_OUT_STOCK_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("OUT_STOCK_AMT",
								i), 2)
						- StringTool.round(StringTool.round(dataParm.getDouble(
								"REQUEST_OUT_STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"COS_OUT_STOCK_AMT", i), 2), 2)
						+ StringTool.round(dataParm.getDouble(
								"RET_IN_STOCK_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble(
								"THI_IN_STOCK_AMT", i), 2)
								//fux modify 20160112
						- StringTool.round(dataParm.getDouble(
						        "WAS_OUT_STOCK_AMT", i), 2), 2));
				inParm.addData("OUT_OWN_AMT", 0);

				inParm.addData("OUT_DIFF_AMT", StringTool.round(0 - StringTool
						.round((StringTool.round(StringTool.round(dataParm
								.getDouble("LAST_STOCK_AMT", i), 2)
								+ StringTool.round(in_stock_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2)
								- StringTool.round(dataParm.getDouble(
										"RET_OUT_STOCK_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"GIF_OUT_STOCK_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"OUT_STOCK_AMT", i), 2)
								- StringTool.round(StringTool.round(dataParm
										.getDouble("REQUEST_OUT_STOCK_AMT", i),
										2)
										+ StringTool.round(dataParm.getDouble(
												"COS_OUT_STOCK_AMT", i), 2), 2)
								+ StringTool.round(dataParm.getDouble(
										"RET_IN_STOCK_AMT", i), 2) + StringTool
								.round(dataParm
										.getDouble("THI_IN_STOCK_AMT", i), 2)),
								2)								
								//fux modify 20160112  
								+ StringTool.round(dataParm.getDouble(
								        "WAS_OUT_STOCK_AMT", i), 2), 2));
				// inParm.addData("OUT_OWN_AMT", StringTool.round(StringTool
				// .round(StringTool.round(dataParm.getDouble(
				// "LAST_OWN_AMT", i), 2)
				// + StringTool.round(in_own_amt, 2)
				// - StringTool.round(dataParm.getDouble(
				// "OWN_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "CHECKMODI_OWN_AMT", i), 2), 2)
				// - StringTool.round(dataParm.getDouble(
				// "RET_OUT_OWN_AMT", i), 2)
				// - StringTool.round(dataParm.getDouble(
				// "GIF_OUT_OWN_AMT", i), 2)
				// - StringTool.round(
				// dataParm.getDouble("OUT_OWN_AMT", i), 2)
				// - StringTool.round(StringTool.round(dataParm.getDouble(
				// "REQUEST_OUT_OWN_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_OWN_AMT", i), 2), 2)
				// + StringTool.round(dataParm.getDouble("RET_IN_OWN_AMT",
				// i), 2)
				// + StringTool.round(dataParm.getDouble("THI_IN_OWN_AMT",
				// i), 2), 2));

				// inParm.addData("OUT_DIFF_AMT",StringTool.round(
				// StringTool.round(StringTool
				// .round(StringTool.round(dataParm.getDouble(
				// "LAST_OWN_AMT", i), 2)
				// + StringTool.round(in_own_amt, 2)
				// - StringTool.round(dataParm.getDouble(
				// "OWN_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "CHECKMODI_OWN_AMT", i), 2), 2)
				// - StringTool.round(dataParm.getDouble(
				// "RET_OUT_OWN_AMT", i), 2)
				// - StringTool.round(dataParm.getDouble(
				// "GIF_OUT_OWN_AMT", i), 2)
				// - StringTool.round(
				// dataParm.getDouble("OUT_OWN_AMT", i), 2)
				// - StringTool.round(StringTool.round(dataParm.getDouble(
				// "REQUEST_OUT_OWN_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_OWN_AMT", i), 2), 2)
				// + StringTool.round(dataParm.getDouble("RET_IN_OWN_AMT",
				// i), 2)
				// + StringTool.round(dataParm.getDouble("THI_IN_OWN_AMT",
				// i), 2), 2)
				// -
				// StringTool.round((StringTool.round(StringTool.round(dataParm.getDouble(
				// "LAST_STOCK_AMT", i), 2)
				// + StringTool.round(in_stock_amt, 2)
				// - StringTool.round(dataParm.getDouble(
				// "STOCK_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "CHECKMODI_STOCK_AMT", i), 2), 2)
				// - StringTool.round(dataParm.getDouble(
				// "RET_OUT_STOCK_AMT", i), 2)
				// - StringTool.round(dataParm.getDouble(
				// "GIF_OUT_STOCK_AMT", i), 2)
				// - StringTool.round(dataParm.getDouble("OUT_STOCK_AMT",
				// i), 2)
				// - StringTool.round(StringTool.round(dataParm.getDouble(
				// "REQUEST_OUT_STOCK_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "COS_OUT_STOCK_AMT", i), 2), 2)
				// + StringTool.round(dataParm.getDouble(
				// "RET_IN_STOCK_AMT", i), 2)
				// + StringTool.round(dataParm.getDouble(
				// "THI_IN_STOCK_AMT", i), 2)),2),2));

				inParm.addData("CHECKMODI_STOCK_AMT", "");
				inParm.addData("CHECKMODI_OWN_AMT", "");
				inParm.addData("CHECKMODI_DIFF_AMT", "");
				inParm.addData("STOCK_AMT", "");
				inParm.addData("OWN_AMT", "");
				inParm.addData("DIFF_AMT", "");
				inParm.addData("PROFIT_LOSS_AMT", "");

				// �ڰ���
				inParm.addData("TYPE_DESC", "");
				inParm.addData("LAST_STOCK_AMT", "");
				inParm.addData("LAST_OWN_AMT", "");
				inParm.addData("LAST_DIFF_AMT", "");
				inParm.addData("PROJECT_IN", "С��");
				inParm.addData("IN_STOCK_AMT", StringTool
						.round(in_stock_amt, 2));
				inParm.addData("IN_OWN_AMT", StringTool.round(in_own_amt, 2));
				//
				inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool
						.round(in_own_amt, 2)
						- StringTool.round(in_stock_amt, 2), 2));
				inParm.addData("PROJECT_OUT", "С��");
				// fux modify
				inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("LAST_STOCK_AMT", i), 2)
						+ StringTool.round(in_stock_amt, 2)
						- StringTool.round(dataParm.getDouble("STOCK_AMT", i),
								2)
						+ StringTool.round(dataParm.getDouble(
								"CHECKMODI_STOCK_AMT", i), 2), 2));
				inParm.addData("OUT_OWN_AMT", StringTool.round(StringTool
						.round(dataParm.getDouble("LAST_OWN_AMT", i), 2)
						+ StringTool.round(in_own_amt, 2)
						- StringTool.round(dataParm.getDouble("OWN_AMT", i), 2)
						+ StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2), 2));

				inParm.addData("OUT_DIFF_AMT", StringTool.round((StringTool
						.round(StringTool.round(dataParm.getDouble(
								"LAST_OWN_AMT", i), 2)
								+ StringTool.round(in_own_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"OWN_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_OWN_AMT", i), 2), 2))
						- (StringTool.round(StringTool.round(dataParm
								.getDouble("LAST_STOCK_AMT", i), 2)
								+ StringTool.round(in_stock_amt, 2)
								- StringTool.round(dataParm.getDouble(
										"STOCK_AMT", i), 2)
								+ StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2)), 2));
				inParm.addData("CHECKMODI_STOCK_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_STOCK_AMT", i), 2));
				inParm.addData("CHECKMODI_OWN_AMT", StringTool.round(dataParm
						.getDouble("CHECKMODI_OWN_AMT", i), 2));
				//
				inParm.addData("CHECKMODI_DIFF_AMT", StringTool.round(
						StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2)
								- StringTool.round(dataParm.getDouble(
										"CHECKMODI_STOCK_AMT", i), 2), 2));
				// fux modify
				inParm.addData("STOCK_AMT", StringTool.round(dataParm
						.getDouble("STOCK_AMT", i), 2));
				// fux modify
				inParm.addData("OWN_AMT", StringTool.round(dataParm.getDouble(
						"OWN_AMT", i), 2));
				inParm.addData("DIFF_AMT", StringTool.round(StringTool.round(
						dataParm.getDouble("OWN_AMT", i), 2)
						- StringTool.round(dataParm.getDouble("STOCK_AMT", i),
								2), 2));
				double profit = StringTool.round(StringTool.round(dataParm
						.getDouble("OWN_AMT", i), 2)
						- StringTool.round(dataParm
								.getDouble("LAST_OWN_AMT", i), 2)
						- StringTool.round(in_own_amt, 2)
						+ StringTool.round(out_own_amt, 2)
						- StringTool.round(dataParm.getDouble(
								"CHECKMODI_OWN_AMT", i), 2), 2);
				inParm.addData("PROFIT_LOSS_AMT", StringTool.round(profit, 2));
				// �ܼ�
				last_stock_sum += StringTool.round(dataParm.getDouble(
						"LAST_STOCK_AMT", i), 2);
				last_own_sum += StringTool.round(dataParm.getDouble(
						"LAST_OWN_AMT", i), 2);
				in_stock_sum += StringTool.round(in_stock_amt, 2);
				in_own_sum += StringTool.round(in_own_amt, 2);
				out_stock_sum += StringTool.round(out_stock_amt, 2);
				out_own_sum += StringTool.round(out_own_amt, 2);
				checkmodi_stock_sum += StringTool.round(dataParm.getDouble(
						"CHECKMODI_STOCK_AMT", i), 2);
				checkmodi_own_sum += StringTool.round(dataParm.getDouble(
						"CHECKMODI_OWN_AMT", i), 2);
				stock_sum += StringTool.round(dataParm
						.getDouble("STOCK_AMT", i), 2);
				// fux modify 20141204 �����λС��λת�����⣬�����ת�Ƶ����ڽ����
				own_sum += StringTool
						.round(dataParm.getDouble("OWN_AMT", i), 2);
				profit_loss_sum += profit;
			}
			// �г�ҩ�����Ĳ� ������ ��ֵ
		}

		// �ܼ���
		inParm.addData("TYPE_DESC", "�ϼ�");
		inParm.addData("LAST_STOCK_AMT", StringTool.round(last_stock_sum, 2));
		inParm.addData("LAST_OWN_AMT", StringTool.round(last_own_sum, 2));
		//                                        
		inParm.addData("LAST_DIFF_AMT", StringTool.round(StringTool.round(
				last_own_sum, 2)
				- StringTool.round(last_stock_sum, 2), 2));
		inParm.addData("PROJECT_IN", "");
		inParm.addData("IN_STOCK_AMT", StringTool.round(in_stock_sum, 2));

		// fux
		BigDecimal c = new BigDecimal(in_own_sum);
		inParm.addData("IN_OWN_AMT", StringTool.round(in_own_sum, 2));
		//

		inParm.addData("IN_DIFF_AMT", StringTool.round(StringTool.round(
				in_own_sum, 2)
				- StringTool.round(in_stock_sum, 2), 2));
		inParm.addData("PROJECT_OUT", "");
		// ��������ŵ��ܼƲ��
		inParm.addData("OUT_STOCK_AMT", StringTool.round(StringTool.round(
				checkmodi_stock_sum, 2)
				+ StringTool.round(last_stock_sum, 2)
				+ StringTool.round(in_stock_sum, 2)
				- StringTool.round(stock_sum, 2), 2));

		// fux
		// inParm.addData("OUT_OWN_AMT", b.setScale(2,BigDecimal.ROUND_DOWN));
		inParm.addData("OUT_OWN_AMT", StringTool.round(StringTool.round(
				checkmodi_own_sum, 2)
				+ StringTool.round(last_own_sum, 2)
				+ StringTool.round(in_own_sum, 2)
				- StringTool.round(own_sum, 2)
				+ StringTool.round(profit_loss_sum, 2), 2));

		// ��������ŵ��ܼƲ��
		inParm.addData("OUT_DIFF_AMT", StringTool.round(StringTool.round(
				StringTool.round(checkmodi_own_sum, 2)
						+ StringTool.round(last_own_sum, 2)
						+ StringTool.round(in_own_sum, 2)
						+ StringTool.round(profit_loss_sum, 2)
						- StringTool.round(own_sum, 2), 2)
				- (StringTool.round(StringTool.round(checkmodi_stock_sum, 2)
						+ StringTool.round(last_stock_sum, 2)
						+ StringTool.round(in_stock_sum, 2)
						- StringTool.round(stock_sum, 2), 2)), 2));
		// inParm.addData("OUT_DIFF_AMT", StringTool.round(sub(out_own_sum
		// , out_stock_sum), 2));

		inParm.addData("CHECKMODI_STOCK_AMT", StringTool.round(
				checkmodi_stock_sum, 2));
		inParm.addData("CHECKMODI_OWN_AMT", StringTool.round(checkmodi_own_sum,
				2));
		//      
		inParm.addData("CHECKMODI_DIFF_AMT", StringTool.round(StringTool.round(
				checkmodi_own_sum, 2)
				- StringTool.round(checkmodi_stock_sum, 2), 2));
		inParm.addData("STOCK_AMT", StringTool.round(stock_sum, 2));
		// fux

		BigDecimal a = new BigDecimal(own_sum);
		inParm.addData("OWN_AMT", StringTool.round(own_sum, 2));

		inParm.addData("DIFF_AMT", StringTool.round(StringTool
				.round(own_sum, 2)
				- StringTool.round(stock_sum, 2), 2));
		inParm.addData("PROFIT_LOSS_AMT", StringTool.round(profit_loss_sum, 2));

		// д��
		if ("A".equals(parm.getValue("ORG_TYPE"))) {
			inParm.setCount(dataParm.getCount("TYPE_DESC") * 6 + 1);
		} else {
			inParm.setCount(dataParm.getCount("TYPE_DESC") * 8 + 1);
		}
		// luhai 2012-1-24 modify ȡ��������ۺ͵��������������� begin
		inParm.addData("SYSTEM", "COLUMNS", "TYPE_DESC");
		inParm.addData("SYSTEM", "COLUMNS", "LAST_STOCK_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "LAST_OWN_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "LAST_DIFF_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "PROJECT_IN");
		inParm.addData("SYSTEM", "COLUMNS", "IN_STOCK_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "IN_OWN_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "IN_DIFF_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "PROJECT_OUT");
		inParm.addData("SYSTEM", "COLUMNS", "OUT_STOCK_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "OUT_OWN_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "OUT_DIFF_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_STOCK_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_OWN_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "CHECKMODI_DIFF_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
		inParm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		// fux ������� ind/indmonth.jhw
		inParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
		// �������� ��δҪ�����
		inParm.addData("SYSTEM", "COLUMNS", "PROFIT_LOSS_AMT");
		data.setData("TABLE", inParm.getData());

		// ��β����
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDMonth.jhw", data);
		// TTable table = this.getTable("TABLE");
		// if (table.getRowCount() <= 0) {
		// this.messageBox("û�л������");
		// return;
		// }

	}

	// ����ȡ��
	public BigDecimal sub(double d1) {
		// BigDecimal b = new BigDecimal(out_own_sum);
		BigDecimal b = new BigDecimal(d1);
		return b.setScale(2, BigDecimal.ROUND_DOWN);
	}

	/**
	 * ��ӡ���� (��������ɫ)
	 * 
	 * @return
	 */
	// public void onPrintDetail() {
	public void onPrint() {
		if (this.getValueString("ORG_CODE").equals("030707")) {

			TParm inParm = new TParm();
			String startDate = this.getValueString("START_DATE").substring(0,
					10).replaceAll("-", "");
			String endDate = this.getValueString("END_DATE").substring(0, 10)
					.replaceAll("-", "");
			// String orgCode = this.getValueString("ORG_CODE");
			TParm parmStockOut1 = new TParm();
			TParm parmOwmIn1 = new TParm();
			TParm parmStockRet1 = new TParm();
			TParm parmOwmRet1 = new TParm();

			String sqlOut1 = " SELECT SUM (A.INVENT_PRICE*A.REQUEST_IN_QTY/B.DOSAGE_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK A,PHA_TRANSUNIT B "
					+ " WHERE TO_DATE (A.TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate
					+ "', 'YYYYMMDD') "
					+ " AND A.ORG_CODE = '030708' "
					+ " AND A.ORDER_CODE = B.ORDER_CODE  ";
			parmStockOut1 = new TParm(TJDODBTool.getInstance().select(sqlOut1));

			String sqlOut4 = " SELECT SUM (RETAIL_PRICE*REQUEST_IN_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030708' ";
			parmOwmIn1 = new TParm(TJDODBTool.getInstance().select(sqlOut4));

			String sqlRet1 = " SELECT SUM (VERIFYIN_PRICE*RET_OUT_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030708' ";
			parmStockRet1 = new TParm(TJDODBTool.getInstance().select(sqlRet1));

			String sqlRet4 = " SELECT SUM (RETAIL_PRICE*RET_OUT_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030708' ";
			parmOwmRet1 = new TParm(TJDODBTool.getInstance().select(sqlRet4));

			inParm.setData("STOCK_AMT030702a", "TEXT", StringTool.round(
					parmStockOut1.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030702a", "TEXT", StringTool.round(
					parmOwmIn1.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030702a", "TEXT", StringTool
					.round(parmOwmIn1.getDouble("OWN_AMT", 0)
							- parmStockOut1.getDouble("STOCK_AMT", 0), 2));

			double stockOutAmt = parmStockOut1.getDouble("STOCK_AMT", 0);
			double owmOutAmt = parmOwmIn1.getDouble("OWN_AMT", 0);
			double CheckmodiOutAmt = owmOutAmt - stockOutAmt;
			inParm.setData("STOCK_OUT_AMT", "TEXT", StringTool.round(
					stockOutAmt, 2));
			inParm.setData("OWN_OUT_AMT", "TEXT", StringTool
					.round(owmOutAmt, 2));
			inParm.setData("CHECKMODI_OUT_AMT", "TEXT", StringTool.round(
					CheckmodiOutAmt, 2));

			inParm.setData("STOCK_AMT030702a1", "TEXT", StringTool.round(
					parmStockRet1.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030702a1", "TEXT", StringTool.round(
					parmOwmRet1.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030702a1", "TEXT", StringTool
					.round(StringTool.round(
							parmOwmRet1.getDouble("OWN_AMT", 0), 2)
							- StringTool.round(parmStockRet1.getDouble(
									"STOCK_AMT", 0), 2), 2));

			double stockRetAmt = parmStockRet1.getDouble("STOCK_AMT", 0);
			double owmRetAmt = parmOwmRet1.getDouble("OWN_AMT", 0);
			double CheckmodiRetAmt = owmRetAmt - stockRetAmt;
			inParm.setData("STOCK_RET_AMT", "TEXT", StringTool.round(
					stockRetAmt, 2));
			inParm.setData("OWN_RET_AMT", "TEXT", StringTool
					.round(owmRetAmt, 2));
			inParm.setData("CHECKMODI_RET_AMT", "TEXT", StringTool.round(
					CheckmodiRetAmt, 2));

			inParm.setData("TITLE_1", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion()));
			inParm.setData("TITLE_2", "TEXT", "����Ʒ���սḽ��");
			inParm.setData("ORG_CODE", "TEXT", this.getComboBox("ORG_CODE")
					.getSelectedName());
			String start_date = this.getValueString("START_DATE");
			String end_date = this.getValueString("END_DATE");
			inParm.setData("DATE_AREA", "TEXT", start_date.substring(0, 4)
					+ "/" + start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " " + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " ");
			Timestamp datetime = SystemTool.getInstance().getDate();
			inParm.setData("DATE", "TEXT", datetime.toString().substring(0, 10)
					.replace('-', '/'));
			inParm.setData("USER", "TEXT", Operator.getName());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\IND\\INDMonthAttached030707.jhw",
					inParm);

		} else {
			TParm inParm = new TParm();
			String startDate = this.getValueString("START_DATE").substring(0,
					10).replaceAll("-", "");
			String endDate = this.getValueString("END_DATE").substring(0, 10)
					.replaceAll("-", "");
			// String orgCode = this.getValueString("ORG_CODE");
			TParm parmStockOut1 = new TParm();
			TParm parmStockOut2 = new TParm();
			TParm parmStockOut3 = new TParm();
			TParm parmOwmIn1 = new TParm();
			TParm parmOwmIn2 = new TParm();
			TParm parmOwmIn3 = new TParm();

			TParm parmStockRet1 = new TParm();
			TParm parmStockRet2 = new TParm();
			TParm parmStockRet3 = new TParm();
			TParm parmOwmRet1 = new TParm();
			TParm parmOwmRet2 = new TParm();
			TParm parmOwmRet3 = new TParm();

			String sqlOut1 = " SELECT SUM (A.INVENT_PRICE*A.REQUEST_IN_QTY/B.DOSAGE_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK A,PHA_TRANSUNIT B "
					+ " WHERE TO_DATE (A.TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate
					+ "', 'YYYYMMDD') "
					+ " AND A.ORG_CODE = '030702' "
					+ " AND A.ORDER_CODE = B.ORDER_CODE  ";
			parmStockOut1 = new TParm(TJDODBTool.getInstance().select(sqlOut1));
			String sqlOut2 = " SELECT SUM (A.INVENT_PRICE*A.REQUEST_IN_QTY/B.DOSAGE_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK A,PHA_TRANSUNIT B "
					+ " WHERE TO_DATE (A.TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate
					+ "', 'YYYYMMDD') "
					+ " AND A.ORG_CODE = '030703' "
					+ " AND A.ORDER_CODE = B.ORDER_CODE";
			parmStockOut2 = new TParm(TJDODBTool.getInstance().select(sqlOut2));
			String sqlOut3 = " SELECT SUM (A.INVENT_PRICE*A.REQUEST_IN_QTY/B.DOSAGE_QTY)  AS STOCK_AMT "
					+ " FROM IND_DDSTOCK A,PHA_TRANSUNIT B  "
					+ " WHERE TO_DATE (A.TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate
					+ "', 'YYYYMMDD') "
					+ " AND A.ORG_CODE = '030706' "
					+ " AND A.ORDER_CODE = B.ORDER_CODE";
			parmStockOut3 = new TParm(TJDODBTool.getInstance().select(sqlOut3));
			String sqlOut4 = " SELECT SUM (RETAIL_PRICE*REQUEST_IN_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030702' ";
			parmOwmIn1 = new TParm(TJDODBTool.getInstance().select(sqlOut4));
			String sqlOut5 = " SELECT SUM (RETAIL_PRICE*REQUEST_IN_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030703' ";
			parmOwmIn2 = new TParm(TJDODBTool.getInstance().select(sqlOut5));
			String sqlOut6 = " SELECT SUM (RETAIL_PRICE*REQUEST_IN_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030706' ";
			parmOwmIn3 = new TParm(TJDODBTool.getInstance().select(sqlOut6));

			String sqlRet1 = " SELECT SUM (VERIFYIN_PRICE*RET_OUT_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030702' ";
			parmStockRet1 = new TParm(TJDODBTool.getInstance().select(sqlRet1));
			String sqlRet2 = " SELECT SUM (VERIFYIN_PRICE*RET_OUT_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030703' ";
			parmStockRet2 = new TParm(TJDODBTool.getInstance().select(sqlRet2));
			String sqlRet3 = " SELECT SUM (VERIFYIN_PRICE*RET_OUT_QTY) AS STOCK_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030706' ";
			parmStockRet3 = new TParm(TJDODBTool.getInstance().select(sqlRet3));
			String sqlRet4 = " SELECT SUM (RETAIL_PRICE*RET_OUT_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030702' ";
			parmOwmRet1 = new TParm(TJDODBTool.getInstance().select(sqlRet4));
			String sqlRet5 = " SELECT SUM (RETAIL_PRICE*RET_OUT_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030703' ";
			parmOwmRet2 = new TParm(TJDODBTool.getInstance().select(sqlRet5));
			String sqlRet6 = " SELECT SUM (RETAIL_PRICE*RET_OUT_QTY) AS OWN_AMT "
					+ " FROM IND_DDSTOCK "
					+ " WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE ('"
					+ startDate
					+ "', "
					+ " 'YYYYMMDD') "
					+ " AND TO_DATE ('"
					+ endDate + "', 'YYYYMMDD') " + " AND ORG_CODE = '030706' ";
			parmOwmRet3 = new TParm(TJDODBTool.getInstance().select(sqlRet6));

			inParm.setData("STOCK_AMT030702a", "TEXT", StringTool.round(
					parmStockOut1.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030702a", "TEXT", StringTool.round(
					parmOwmIn1.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030702a", "TEXT", StringTool
					.round(parmOwmIn1.getDouble("OWN_AMT", 0)
							- parmStockOut1.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("STOCK_AMT030703b", "TEXT", StringTool.round(
					parmStockOut2.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030703b", "TEXT", StringTool.round(
					parmOwmIn2.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030703b", "TEXT", StringTool
					.round(parmOwmIn2.getDouble("OWN_AMT", 0)
							- parmStockOut2.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("STOCK_AMT030707c", "TEXT", StringTool.round(
					parmStockOut3.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030707c", "TEXT", StringTool.round(
					parmOwmIn3.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030707c", "TEXT", StringTool
					.round(parmOwmIn3.getDouble("OWN_AMT", 0)
							- parmStockOut3.getDouble("STOCK_AMT", 0), 2));
			double stockOutAmt = parmStockOut1.getDouble("STOCK_AMT", 0)
					+ parmStockOut2.getDouble("STOCK_AMT", 0)
					+ parmStockOut3.getDouble("STOCK_AMT", 0);
			double owmOutAmt = parmOwmIn1.getDouble("OWN_AMT", 0)
					+ parmOwmIn2.getDouble("OWN_AMT", 0)
					+ parmOwmIn3.getDouble("OWN_AMT", 0);
			double CheckmodiOutAmt = owmOutAmt - stockOutAmt;
			inParm.setData("STOCK_OUT_AMT", "TEXT", StringTool.round(
					stockOutAmt, 2));
			inParm.setData("OWN_OUT_AMT", "TEXT", StringTool
					.round(owmOutAmt, 2));
			inParm.setData("CHECKMODI_OUT_AMT", "TEXT", StringTool.round(
					CheckmodiOutAmt, 2));

			inParm.setData("STOCK_AMT030702a1", "TEXT", StringTool.round(
					parmStockRet1.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030702a1", "TEXT", StringTool.round(
					parmOwmRet1.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030702a1", "TEXT", StringTool
					.round(StringTool.round(
							parmOwmRet1.getDouble("OWN_AMT", 0), 2)
							- StringTool.round(parmStockRet1.getDouble(
									"STOCK_AMT", 0), 2), 2));
			inParm.setData("STOCK_AMT030703b1", "TEXT", StringTool.round(
					parmStockRet2.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030703b1", "TEXT", StringTool.round(
					parmOwmRet2.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030703b1", "TEXT", StringTool
					.round(StringTool.round(
							parmOwmRet2.getDouble("OWN_AMT", 0), 2)
							- StringTool.round(parmStockRet2.getDouble(
									"STOCK_AMT", 0), 2), 2));
			inParm.setData("STOCK_AMT030707c1", "TEXT", StringTool.round(
					parmStockRet3.getDouble("STOCK_AMT", 0), 2));
			inParm.setData("OWN_AMT030707c1", "TEXT", StringTool.round(
					parmOwmRet3.getDouble("OWN_AMT", 0), 2));
			inParm.setData("CHECKMODI_DIFF_AMT030707c1", "TEXT", StringTool
					.round(StringTool.round(
							parmOwmRet3.getDouble("OWN_AMT", 0), 2)
							- StringTool.round(parmStockRet3.getDouble(
									"STOCK_AMT", 0), 2), 2));

			double stockRetAmt = parmStockRet1.getDouble("STOCK_AMT", 0)
					+ parmStockRet2.getDouble("STOCK_AMT", 0)
					+ parmStockRet3.getDouble("STOCK_AMT", 0);
			double owmRetAmt = parmOwmRet1.getDouble("OWN_AMT", 0)
					+ parmOwmRet2.getDouble("OWN_AMT", 0)
					+ parmOwmRet3.getDouble("OWN_AMT", 0);
			double CheckmodiRetAmt = owmRetAmt - stockRetAmt;
			inParm.setData("STOCK_RET_AMT", "TEXT", StringTool.round(
					stockRetAmt, 2));
			inParm.setData("OWN_RET_AMT", "TEXT", StringTool
					.round(owmRetAmt, 2));
			inParm.setData("CHECKMODI_RET_AMT", "TEXT", StringTool.round(
					CheckmodiRetAmt, 2));

			inParm.setData("TITLE_1", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion()));
			inParm.setData("TITLE_2", "TEXT", "ҩ���սḽ��");
			inParm.setData("ORG_CODE", "TEXT", this.getComboBox("ORG_CODE")
					.getSelectedName());
			String start_date = this.getValueString("START_DATE");
			String end_date = this.getValueString("END_DATE");
			inParm.setData("DATE_AREA", "TEXT", start_date.substring(0, 4)
					+ "/" + start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " " + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " ");
			Timestamp datetime = SystemTool.getInstance().getDate();
			inParm.setData("DATE", "TEXT", datetime.toString().substring(0, 10)
					.replace('-', '/'));
			inParm.setData("USER", "TEXT", Operator.getName());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\IND\\INDMonthAttached.jhw", inParm);
		}
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �õ�TTextFormat����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * @return TParm
	 */
	private TParm getDosageAMT(String org_code, String type_code) {
		String start_date = "";
		String end_date = "";
		if (getRadioButton("DAY_TYPE").isSelected()) {
			start_date = this.getValueString("START_DATE").substring(0, 10)
					.replaceAll("-", "");
			end_date = this.getValueString("END_DATE").substring(0, 10)
					.replaceAll("-", "");
		} else {
			String month_area = this.getValueString("MONTH_AREA");
			String year = month_area.substring(0, 4);
			String month = month_area.substring(5, 7);
			start_date = year + month + "01";
			if ("12".equals(month)) {
				year = TypeTool.getString(TypeTool.getInt(year) + 1);
				month = "01";
			} else {
				month = TypeTool.getString(TypeTool.getInt(month) + 1);
			}
			Timestamp end_datetime = StringTool.getTimestamp(year + month
					+ "01", "yyyyMMdd");
			end_date = StringTool.rollDate(end_datetime, -1).toString()
					.substring(0, 10).replaceAll("-", "");
		}
		start_date = start_date + "000000";
		end_date = end_date + "235959";

		TParm parm = new TParm();
		parm.addData("REGRESSDRUG_STOCK_AMT", 0);
		parm.addData("REGRESSDRUG_OWN_AMT", 0);
		parm.addData("DOSAGE_STOCK_AMT", 0);
		parm.addData("DOSAGE_OWN_AMT", 0);
		// ���﷢ҩ
		String sql_dosage_opd = " SELECT SUM (AR_AMT) AS DOSAGE_OWN_AMT, "
				+ " SUM (COST_AMT) AS DOSAGE_STOCK_AMT "
				+ " FROM OPD_ORDER A, PHA_BASE B "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.EXEC_DEPT_CODE = '" + org_code + "' "
				+ " AND B.TYPE_CODE = '" + type_code + "' "
				+ " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date
				+ "', 'YYYYMMDDHH24MISS') " + " AND TO_DATE ('" + end_date
				+ "', 'YYYYMMDDHH24MISS') AND A.PHA_RETN_CODE IS NULL ";
		TParm dosage_opd = new TParm(TJDODBTool.getInstance().select(
				sql_dosage_opd));
		if (dosage_opd != null && dosage_opd.getCount("DOSAGE_STOCK_AMT") > 0) {
			parm.setData("DOSAGE_STOCK_AMT", 0, parm.getDouble(
					"DOSAGE_STOCK_AMT", 0)
					+ dosage_opd.getDouble("DOSAGE_STOCK_AMT", 0));
			parm.setData("DOSAGE_OWN_AMT", 0, parm.getDouble("DOSAGE_OWN_AMT",
					0)
					+ dosage_opd.getDouble("DOSAGE_OWN_AMT", 0));
		}
		// סԺ��ҩ
		/*
		 * String sql_dosage_odi = " SELECT SUM (A.TOT_AMT) AS DOSAGE_OWN_AMT, "
		 * + " SUM (COST_AMT) AS DOSAGE_STOCK_AMT " +
		 * " FROM IBS_ORDD A, IBS_ORDM B, PHA_BASE C " +
		 * " WHERE A.CASE_NO = B.CASE_NO " +
		 * " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ " +
		 * " AND A.ORDER_CODE = C.ORDER_CODE " + " AND A.EXE_DEPT_CODE = '" +
		 * org_code + "' " + " AND C.TYPE_CODE = '" + type_code + "' " +
		 * " AND A.BILL_DATE BETWEEN TO_DATE ('" + start_date +
		 * "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
		 * "','YYYYMMDDHH24MISS ') AND A.TOT_AMT > 0 ";
		 */
		String sql_dosage_odi = "SELECT SUM(A.OWN_AMT) AS DOSAGE_OWN_AMT,"
				+ "SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)"
				+ "/(A.DISPENSE_QTY1+A.DISPENSE_QTY2+A.DISPENSE_QTY3)* A.DOSAGE_QTY)  AS DOSAGE_STOCK_AMT "
				+ "FROM ODI_DSPNM A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND "
				+ "A.EXEC_DEPT_CODE='" + org_code
				+ "' AND A.DSPN_KIND!='RT' AND "
				+ "A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date
				+ "','YYYYMMDDHH24MISS') " + "AND TO_DATE('" + end_date
				+ "','YYYYMMDDHH24MISS')";

		TParm dosage_odi = new TParm(TJDODBTool.getInstance().select(
				sql_dosage_odi));
		if (dosage_odi != null && dosage_odi.getCount("DOSAGE_STOCK_AMT") > 0) {
			parm.setData("DOSAGE_STOCK_AMT", 0, parm.getDouble(
					"DOSAGE_STOCK_AMT", 0)
					+ dosage_odi.getDouble("DOSAGE_STOCK_AMT", 0));
			parm.setData("DOSAGE_OWN_AMT", 0, parm.getDouble("DOSAGE_OWN_AMT",
					0)
					+ dosage_odi.getDouble("DOSAGE_OWN_AMT", 0));
		}
		// ������ҩ
		/*
		 * String sql_re_dosage_opd =
		 * " SELECT SUM (A.AR_AMT) AS REGRESSDRUG_OWN_AMT , " +
		 * " SUM (A.DOSAGE_QTY * B.STOCK_PRICE) AS REGRESSDRUG_STOCK_AMT " +
		 * " FROM PHA_ORDER_HISTORY A, PHA_BASE B, OPD_ORDER C " +
		 * " WHERE A.ORDER_CODE = B.ORDER_CODE " + " AND A.CASE_NO = C.CASE_NO "
		 * + " AND A.RX_NO = C.RX_NO " + " AND A.SEQ_NO = C.SEQ_NO " +
		 * " AND A.EXEC_DEPT_CODE = '" + org_code + "' " +
		 * " AND B.TYPE_CODE = '" + type_code + "' " +
		 * " AND A.PHA_RETN_DATE >= '" + start_date +
		 * "' AND A.PHA_RETN_DATE <= '" + end_date + "'";
		 */
		String sql_re_dosage_opd = " SELECT SUM (AR_AMT) AS REGRESSDRUG_OWN_AMT, "
				+ " SUM (COST_AMT) AS REGRESSDRUG_STOCK_AMT "
				+ " FROM OPD_ORDER A, PHA_BASE B "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.EXEC_DEPT_CODE = '"
				+ org_code
				+ "' "
				+ " AND B.TYPE_CODE = '"
				+ type_code
				+ "' "
				+ " AND A.PHA_RETN_DATE BETWEEN TO_DATE ('"
				+ start_date
				+ "', 'YYYYMMDDHH24MISS') "
				+ " AND TO_DATE ('"
				+ end_date
				+ "', 'YYYYMMDDHH24MISS') AND A.PHA_RETN_CODE IS NOT NULL ";
		TParm re_dosage_opd = new TParm(TJDODBTool.getInstance().select(
				sql_re_dosage_opd));
		if (re_dosage_opd != null
				&& re_dosage_opd.getCount("REGRESSDRUG_STOCK_AMT") > 0) {
			parm.setData("REGRESSDRUG_STOCK_AMT", 0, parm.getDouble(
					"REGRESSDRUG_STOCK_AMT", 0)
					+ re_dosage_opd.getDouble("REGRESSDRUG_STOCK_AMT", 0));
			parm.setData("REGRESSDRUG_OWN_AMT", 0, parm.getDouble(
					"REGRESSDRUG_OWN_AMT", 0)
					+ re_dosage_opd.getDouble("REGRESSDRUG_OWN_AMT", 0));
		}
		// סԺ��ҩ
		/*
		 * String sql_re_dosage_odi =
		 * " SELECT SUM (A.TOT_AMT) * -1 AS REGRESSDRUG_OWN_AMT, " +
		 * " SUM (COST_AMT) * -1 AS REGRESSDRUG_STOCK_AMT " +
		 * " FROM IBS_ORDD A, IBS_ORDM B, PHA_BASE C " +
		 * " WHERE A.CASE_NO = B.CASE_NO " +
		 * " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ " +
		 * " AND A.ORDER_CODE = C.ORDER_CODE " + " AND A.EXE_DEPT_CODE = '" +
		 * org_code + "' " + " AND C.TYPE_CODE = '" + type_code + "' " +
		 * " AND A.BILL_DATE BETWEEN TO_DATE ('" + start_date +
		 * "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
		 * "','YYYYMMDDHH24MISS ') AND A.TOT_AMT < 0 ";
		 */
		String sql_re_dosage_odi = "SELECT SUM(A.OWN_AMT) AS DOSAGE_OWN_AMT,"
				+ " SUM(A.DOSAGE_QTY*B.STOCK_PRICE) AS DOSAGE_STOCK_AMT "
				+ "FROM ODI_DSPNM A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND "
				+ "A.EXEC_DEPT_CODE='" + org_code
				+ "' AND A.DSPN_KIND='RT' AND "
				+ "A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date
				+ "','YYYYMMDDHH24MISS') " + "AND TO_DATE('" + end_date
				+ "','YYYYMMDDHH24MISS')";
		TParm re_dosage_odi = new TParm(TJDODBTool.getInstance().select(
				sql_re_dosage_odi));
		if (re_dosage_odi != null
				&& re_dosage_odi.getCount("REGRESSDRUG_STOCK_AMT") > 0) {
			parm.setData("REGRESSDRUG_STOCK_AMT", 0, parm.getDouble(
					"REGRESSDRUG_STOCK_AMT", 0)
					+ re_dosage_odi.getDouble("REGRESSDRUG_STOCK_AMT", 0));
			parm.setData("REGRESSDRUG_OWN_AMT", 0, parm.getDouble(
					"REGRESSDRUG_OWN_AMT", 0)
					+ re_dosage_odi.getDouble("REGRESSDRUG_OWN_AMT", 0));
		}

		return parm;
	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

}
