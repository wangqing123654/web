package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import jdo.sys.IReportTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.Operator;
import jdo.bil.BILContractRecordTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.manager.TIOM_AppServer;
import action.bil.BILContractRecordAction;
import jdo.ekt.EKTIO;
import jdo.reg.PatAdmTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.opb.OPBReceiptTool;
import jdo.util.Manager;
import jdo.opd.OrderTool;

import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ���˵�λ�������
 * </p>
 * 
 * <p>
 * Description: ���˵�λ�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author pangben 20110817
 * @version 1.0
 */
public class BILContractRecordControl extends TControl {
	public BILContractRecordControl() {
	}

	private TTable table;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initPage();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ��table�е�CHECKBOX��������¼�
//		callFunction("UI|table|addEventListener",
//				TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");

		table = (TTable) this.getComponent("table");
		onQuery();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMddHHmmss");
		if (startTime.length() <= 0 || endTime.length() <= 0) {
			this.messageBox("�������ѯ��ʱ��");
			return;
		}
		TParm parm = new TParm();
		if (this.getValue("REGION_CODE").toString().length() > 0)
			parm
					.setData("REGION_CODE", this.getValue("REGION_CODE")
							.toString());
		// ���˵�λ
		if (this.getValueString("CONTRACT_CODE").length() > 0)
			parm.setData("CONTRACT_CODE", this.getValue("CONTRACT_CODE")
					.toString());
		// ״̬
		if (this.getValueString("BIL_STATUS").length() > 0)
			parm.setData("BIL_STATUS", this.getValue("BIL_STATUS").toString());
		parm.setData("DATE_S", startTime);
		parm.setData("DATE_E", endTime);
		TParm result = BILContractRecordTool.getInstance().recodeQuery(parm);
		table.setParmValue(result);
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {

		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("S_DATE", yesterday);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		setValue("REGION_CODE", Operator.getRegion());
		this.callFunction("UI|table|removeRowAll");
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("CONTRACT_CODE");
		initPage();
	}

	/**
	 * ���淽���� ִ���޸�OPD_ORDER ���� PRINT_FLG=Y AND PRINT_NO ����ֵ �޸� BIL_REG_RECP ����
	 * PRINT_DATE ����ʱ�� PRINT_NO ����ֵ ��� BIL_INVRCP ������ �޸� BIL_INVOICE ����������Ʊ�ݺŲ���
	 * �޸� BIL_OPB_RECP ���� PRINT_DATE ����ʱ�� PRINT_NO ����ֵ
	 */
	public void exeContract() {
		//this.messageBox(this.getValue("CASHIER_CODE").toString());
		if (null==this.getValue("CASHIER_CODE") || this.getValue("CASHIER_CODE").toString().length()<=0) {
			this.messageBox("��ѡ�������Ա");
			this.grabFocus("CASHIER_CODE");
			return;		
		}
		TParm newParm=new TParm();
		TParm tableParm = table.getParmValue();
		boolean isY = false;
		int count=0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				if ("Y".equals(tableParm.getValue("FLG", i))
						&& "2".equals(tableParm.getValue("BIL_STATUS",
								i))) {
					this.messageBox("�˵���Ϊ:"+tableParm.getValue("RECEIPT_NO",i)+"�Ѿ����,�������޸�");
					break;
				}
				isY = true;
				newParm.setRowData(count,tableParm,i);
				count++;
			}
		}
		newParm.setCount(count);
		// ��ִ�е�����
		if (isY) {
			TParm parm = new TParm();
			parm.setData("OPT_USER", this.getValue("CASHIER_CODE"));
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("recodeParm", newParm.getData());
			TParm result = TIOM_AppServer.executeAction(
					"action.bil.BILContractRecordAction", "onSave", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("ִ��ʧ��");
				return;
			}
			this.messageBox("ִ�гɹ�");
			

			//onQuery();
		} else
			this.messageBox("��ѡ��Ҫִ�е�����");
		onQuery();

	}

	/**
	 * ���ֵ�ı��¼����޸�״̬����˽����ֵ
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public void onTableCheckBoxClicked(Object obj) {

		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		table.acceptText();
		TTable node = (TTable) obj;
		if (node == null)
			return;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		//int column = node.getSelectedColumn();
		int selectRow = node.getSelectedRow();
		// ����Ѿ�������ɣ�����ʾ�����Ա���
		if ("Y".equals(node.getParmValue().getValue("FLG", selectRow))
				&& "2".equals(node.getParmValue().getValue("BIL_STATUS",
						selectRow))) {
			this.messageBox("���˵��Ѿ����,�������޸�");
			node.getParmValue().setData("FLG", selectRow, "N");
			table.setParmValue(node.getParmValue());
		}
	}

	/**
	 * ��ӡ���� �Ѿ���������ݿ��Դ�ӡ����ӡ��Ʊ�ݷ����֣��ҺŴ�Ʊ���շѴ�Ʊ
	 */
	public void onPrint() {
		table.acceptText();
		TParm tableParm=table.getParmValue();
		boolean flg=false;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG",i)) {
				flg=true;
				break;
			}
		}
		if (!flg) {
			this.messageBox("��ѡ��Ҫ��ӡ���˵�");
			return;
		}
		// �ҺŴ�Ʊ
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getBoolean("FLG", i)) {
				// û�н���
				if ("1".equals(tableParm.getValue("BIL_STATUS", i))) {
					this.messageBox("�˵���Ϊ:"+tableParm.getValue("RECEIPT_NO",i)+"û�н���,�����Դ�Ʊ");
					break;
				}
				if ("REG".equals(tableParm.getValue("RECEIPT_TYPE", i))) {
					regPrint(tableParm.getRow(i));
				} else {
					// �����Ʊ
					opdPrint(tableParm.getRow(i));
				}
			}
		}
//		TParm parm = table.getParmValue();
//		
//		// û�н���
//		if ("1".equals(parm.getValue("BIL_STATUS", row))) {
//			this.messageBox("���˵�û�н���,�����Դ�Ʊ");
//			return;
//		}
//		
//		// �ҺŴ�Ʊ
//		if ("REG".equals(parm.getValue("RECEIPT_TYPE", row))) {
//			regPrint(parm.getRow(row));
//		} else {
//			// �����Ʊ
//			opdPrint(parm.getRow(row));
//		}
		
	}

	/**
	 * �ҺŴ�Ʊ��̩��ҽԺ
	 */
	private void regPrint(TParm parm) {
		TParm result = PatAdmTool.getInstance().getRegPringDate(
				parm.getValue("CASE_NO"), "");
		StringBuffer sql = new StringBuffer();
		// ���Ҵ˴ξ����Ʊ�ݺ�
		sql.append("SELECT PRINT_NO FROM BIL_REG_RECP WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND RECEIPT_NO='"
				+ parm.getValue("RECEIPT_NO") + "'");
		TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
		result.setData("PRINT_NO", "TEXT", printNoParm.getValue("PRINT_NO", 0));
		result.setData("DEPT_NAME", "TEXT", result.getValue("DEPT_CODE_OPB")
				+ "   (" + result.getValue("CLINICROOM_DESC_OPB") + ")"); // ������������
																			// ��ʾ��ʽ:����(����)
		result.setData("CLINICTYPE_NAME", "TEXT", result
				.getValue("CLINICROOM_DESC_OPB")
				+ "   (" + result.getValue("QUE_NO_OPB") + "��)"); // �ű�
																	// ��ʾ��ʽ:�ű�(���)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		result.setData("BALANCE_NAME", "TEXT", "�� ��"); // �������
		result.setData("CURRENT_BALANCE", "TEXT", "�� " + "0.00"); // ҽ�ƿ�ʣ����
		result.setData("PAY_DEBIT", "TEXT", "ҽ��"); // ҽ��
		result.setData("PAY_CASH", "TEXT", "�ֽ�"); // �ֽ�
		result.setData("DATE", "TEXT", yMd); // ����
		result.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
//		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				result);
	    this.openPrintWindow(IReportTool.getInstance().getReportPath("REGRECPPrintReport"),
                             IReportTool.getInstance().getReportParm("REGRECPPrintReportTool", result));//����ϲ�modify by wanglong 20130730
	}

	// /**
	// * �ҺŴ�Ʊ
	// * @param parm TParm
	// */
	// private void regPrint(TParm parm) {
	// TParm result = PatAdmTool.getInstance().getRegPringDate(parm.getValue(
	// "CASE_NO"),
	// "");
	// StringBuffer sql = new StringBuffer();
	// //���Ҵ˴ξ����Ʊ�ݺ�
	// sql.append("SELECT PRINT_NO FROM BIL_REG_RECP WHERE CASE_NO='" +
	// parm.getValue(
	// "CASE_NO") + "' AND RECEIPT_NO='" +
	// parm.getValue("RECEIPT_NO") + "'");
	// TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
	// sql.toString()));
	// result.setData("PRINT_NO", "TEXT", printNoParm.getValue("NEXT_NO", 0));
	// this.openPrintDialog("%ROOT%\\config\\prt\\reg\\REG_Print1.jhw",
	// result, false);
	//
	// }

	/**
	 * �����Ʊ
	 * 
	 * @param parm
	 *            TParm
	 */
	// private void opdPrint(TParm parm) {
	// TParm recpParm = OPBReceiptTool.getInstance().getOneReceipt(parm.
	// getValue("RECEIPT_NO"));
	// System.out.println("�����վݵ�����" + recpParm);
	// // TParm regParm =
	// PatAdmTool.getInstance().getRegPringDate(parm.getValue(
	// // "CASE_NO"),
	// // "");
	// TParm oneReceiptParm = new TParm();
	// //Ʊ����Ϣ
	// //����
	// oneReceiptParm.setData("PAT_NAME", "TEXT", parm.getValue("PAT_NAME"));
	// //��ᱣ�Ϻ�
	// oneReceiptParm.setData("Social_NO", "TEXT", "100000001");
	// //��Ա���
	// oneReceiptParm.setData("CTZ_DESC", "TEXT", "ְ��ҽ��");
	// //�������
	// oneReceiptParm.setData("Cost_class", "TEXT", "��ͳ");
	// //ҽ�ƻ�������
	// oneReceiptParm.setData("HOSP_DESC", "TEXT",
	// Manager.getOrganization().
	// getHospitalCHNFullName(parm.getValue(
	// "REGION_CODE")));
	// //���úϼ�
	// oneReceiptParm.setData("TOT_AMT", "TEXT",
	// parm.getValue("AR_AMT"));
	// //ͳ��֧��
	// oneReceiptParm.setData("Overall_pay", "TEXT",
	// StringTool.round(recpParm.
	// getDouble("Overall_pay", 0), 2));
	// //����֧��
	// oneReceiptParm.setData("Individual_pay", "TEXT",
	// parm.getValue("AR_AMT"));
	// //�ֽ�֧��
	// oneReceiptParm.setData("Cash", "TEXT",
	// StringTool.round(recpParm.
	// getDouble("PAY_CASH", 0), 2));
	// //�˻����
	// oneReceiptParm.setData("Recharge", "TEXT",
	// StringTool.round(recpParm.
	// getDouble("Recharge", 0), 2));
	// //��ӡ����
	// oneReceiptParm.setData("OPT_DATE", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// //��ӡ��
	// oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
	// TParm testparm = new TParm();
	// StringBuffer sql = new StringBuffer();
	// sql.append("SELECT ORDER_CODE,HIDE_FLG FROM OPD_ORDER WHERE CASE_NO ='" +
	// parm.getValue("CASE_NO") + "' AND RECEIPT_NO='" +
	// parm.getValue("RECEIPT_NO") + "'");
	// TParm tableinparmSum = new TParm(TJDODBTool.getInstance().select(
	// sql.toString()));
	// TParm tableinparm=new TParm();
	// //ɾ��ϸ���
	// int count=0;
	// for(int i=0;i<tableinparmSum.getCount();i++){
	// if ("N".equals(tableinparmSum.getValue("HIDE_FLG",i))){
	// tableinparm.addData("ORDER_CODE",tableinparmSum.getValue("ORDER_CODE",i));
	// count++;
	// }
	// }
	// tableinparm.setCount(count);
	// int number1 = 0;
	// int number2 = 0;
	// int number3 = 0;
	// int number4 = 0;
	// int number5 = 0;
	// int number6 = 0;
	// int number7 = 0;
	// int number8 = 0;
	// int number9 = 0;
	// int number10 = 0;
	// int number11 = 0;
	// int number12 = 0;
	// int number13 = 0;
	// int number14 = 0;
	// int number15 = 0;
	// int number16 = 0;
	// int number17 = 0;
	// int number18 = 0;
	// int number19 = 0;
	// for (int i = 0; i < tableinparm.getCount("ORDER_CODE"); i++) {
	// String order_CODE = tableinparm.getValue("ORDER_CODE", i);
	// String SQL =
	// "SELECT ORDER_DESC,REXP_CODE,SPECIFICATION,MEDI_QTY,AR_AMT,NHI_PRICE FROM OPD_ORDER WHERE ORDER_CODE='"
	// +
	// order_CODE + "'";
	// TParm rexp = new TParm(TJDODBTool.getInstance().select(SQL));
	// if (!recpParm.getValue("CHARGE01").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("101")) {
	// if (number1 == 0) {
	// testparm.addData("REG_NAME", "��ҩ��");
	// testparm.addData("REG_TOT",
	// recpParm.getDouble("CHARGE01", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number1 = 1;
	// } else {
	// testparm.addData("REG_NAME", "");
	// testparm.addData("REG_TOT",
	// "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	//
	// if (!recpParm.getValue("CHARGE02").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("102")) {
	// if (number2 == 0) {
	// testparm.addData("REG_NAME", "�г�ҩ");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE02", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number2 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// }
	// if (!recpParm.getValue("CHARGE03").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("103")) {
	// if (number3 == 0) {
	// testparm.addData("REG_NAME", "�в�ҩ");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE03", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE04").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("104")) {
	// if (number4 == 0) {
	// testparm.addData("REG_NAME", "����");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE04", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number4 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// }
	// if (!recpParm.getValue("CHARGE05").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("105")) {
	// if (number5 == 0) {
	// System.out.println("�����");
	// testparm.addData("REG_NAME", "�����");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE05", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number5 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// }
	// if (!recpParm.getValue("CHARGE06").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("106")) {
	// if (number6 == 0) {
	// System.out.println("���Ʒ�");
	// testparm.addData("REG_NAME", "���Ʒ�");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE06", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number6 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", " ");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE07").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("107")) {
	// if (number7 == 0) {
	// testparm.addData("REG_NAME", "�����");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE07", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number7 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE08").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("108")) {
	// if (number8 == 0) {
	// testparm.addData("REG_NAME", "����");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE08", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number8 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE09").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("109")) {
	// if (number9 == 0) {
	// testparm.addData("REG_NAME", "X���");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE09", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number9 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE10").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("110")) {
	// if (number10 == 0) {
	// testparm.addData("REG_NAME", "CT��");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE10", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number10 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", "");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE11").equals("") &&
	// rexp.getValue("REXP_CODE", 0).equals("111")) {
	// if (number11 == 0) {
	// testparm.addData("REG_NAME", "������");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE11", 0));
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// number11 = 1;
	// } else {
	// testparm.addData("REG_NAME", " ");
	// testparm.addData("REG_TOT", " ");
	// testparm.addData("ORDER_DESC",
	// rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	// }
	// if (!recpParm.getValue("CHARGE12").equals("") &&
	// rexp.getValue("CHARGE12", 0).equals("112")) {
	// testparm.addData("REG_NAME", "��Ѫ��");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE12", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE13").equals("") &&
	// rexp.getValue("CHARGE13", 0).equals("113")) {
	// testparm.addData("REG_NAME", "������");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE13", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE14").equals("") &&
	// rexp.getValue("CHARGE14", 0).equals("114")) {
	// testparm.addData("REG_NAME", "����");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE14", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE15").equals("") &&
	// rexp.getValue("CHARGE15", 0).equals("115")) {
	// testparm.addData("REG_NAME", "��λ��");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE15", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE16").equals("") &&
	// rexp.getValue("CHARGE16", 0).equals("116")) {
	// testparm.addData("REG_NAME", "��ů��");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE16", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE17").equals("") &&
	// rexp.getValue("CHARGE17", 0).equals("117")) {
	// testparm.addData("REG_NAME", "�໤��");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE17", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE18").equals("") &&
	// rexp.getValue("CHARGE18", 0).equals("118")) {
	// testparm.addData("REG_NAME", "������");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE18", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	//
	// }
	// if (!recpParm.getValue("CHARGE19").equals("") &&
	// rexp.getValue("CHARGE19", 0).equals("119")) {
	// testparm.addData("REG_NAME", "Ӥ����");
	// testparm.addData("REG_TOT", recpParm.getValue("CHARGE19", 0));
	// testparm.addData("ORDER_DESC", rexp.getValue("ORDER_DESC", 0));
	// testparm.addData("SPECIFICATION",
	// rexp.getValue("SPECIFICATION", 0));
	// testparm.addData("MEDI_QTY", rexp.getInt("MEDI_QTY", 0));
	// testparm.addData("AR_AMT", rexp.getDouble("AR_AMT", 0));
	// int number = rexp.getInt("MEDI_QTY");
	// double price = rexp.getDouble("AR_AMT", 0);
	// testparm.addData("ORDER_CASH", number * price);
	// testparm.addData("OWN_PRICE", "0");
	// }
	//
	// }
	// testparm.setCount(tableinparm.getCount("ORDER_CODE"));
	// testparm.addData("SYSTEM", "COLUMNS", "REG_NAME");
	// testparm.addData("SYSTEM", "COLUMNS", "REG_TOT");
	// testparm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
	// testparm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
	// testparm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
	// testparm.addData("SYSTEM", "COLUMNS", "AR_AMT");
	// testparm.addData("SYSTEM", "COLUMNS", "ORDER_CASH");
	// testparm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
	// oneReceiptParm.setData("TABLE", testparm.getData());
	// System.out.println("testparm::"+testparm);
	// if (!testparm.getData("REG_NAME", 0).equals("")) {
	// oneReceiptParm.setData("PAT_NAME1", "TEXT",
	// parm.getValue("PAT_NAME"));
	// oneReceiptParm.setData("FEE_CLASS1", "TEXT",
	// testparm.getData("REG_NAME", 0));
	// oneReceiptParm.setData("TOT1", "TEXT",
	// testparm.getData("REG_TOT", 0));
	// oneReceiptParm.setData("PRINT_DATE1", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// oneReceiptParm.setData("OPT_UESR", "TEXT", Operator.getName());
	// oneReceiptParm.setData("CASE_NO1", "TEXT", "��ͳ");
	// }
	// if (null != testparm.getData("REG_NAME", 1) &&
	// !testparm.getData("REG_NAME", 1).equals("")) {
	// oneReceiptParm.setData("PAT_NAME2", "TEXT",
	// parm.getValue("PAT_NAME"));
	// oneReceiptParm.setData("FEE_CLASS2", "TEXT",
	// testparm.getData("REG_NAME", 1));
	// oneReceiptParm.setData("TOT2", "TEXT",
	// testparm.getData("REG_TOT", 1));
	// oneReceiptParm.setData("PRINT_DATE2", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// oneReceiptParm.setData("OPT_UESR", "TEXT", Operator.getName());
	// oneReceiptParm.setData("CASE_NO2", "TEXT", "��ͳ");
	// }
	//
	// if (null != testparm.getData("REG_NAME", 2) &&
	// !testparm.getData("REG_NAME", 2).equals("")) {
	// oneReceiptParm.setData("PAT_NAME3", "TEXT",
	// parm.getValue("PAT_NAME"));
	// oneReceiptParm.setData("FEE_CLASS3", "TEXT",
	// testparm.getData("REG_NAME", 2));
	// oneReceiptParm.setData("TOT3", "TEXT",
	// testparm.getData("REG_TOT", 2));
	// oneReceiptParm.setData("PRINT_DATE3", "TEXT",
	// StringTool.getString(SystemTool.getInstance().
	// getDate(), "yyyy/MM/dd"));
	// oneReceiptParm.setData("OPT_UESR", "TEXT", Operator.getName());
	// oneReceiptParm.setData("CASE_NO3", "TEXT", "��ͳ");
	// }
	// System.out.println("ҽ�ƿ���Ʊ����" + oneReceiptParm);
	// this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPB_Print1.jhw",
	// oneReceiptParm);
	//
	// }
	/**
	 * �����Ʊ:̩��ҽԺ
	 * 
	 * @param parm
	 *            TParm
	 */
	private void opdPrint(TParm parm) {
		TParm oneReceiptParm = new TParm();
		// �����վݵ�����:�ֽ��շѴ�Ʊ
		TParm recpParm = OPBReceiptTool.getInstance().getOneReceipt(
				parm.getValue("RECEIPT_NO"));
		// Ʊ����Ϣ
		// ����
		oneReceiptParm.setData("PAT_NAME", "TEXT", parm.getValue("USER_NAME"));
		// ҽ�ƻ�������
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Operator
				.getHospitalCHNFullName());
		// ���úϼ�
		oneReceiptParm.setData("TOT_AMT", "TEXT", recpParm.getDouble("TOT_AMT",
				0));
		// ������ʾ��д���
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));

		// ����֧��
		oneReceiptParm.setData("Individual_pay", "TEXT", recpParm.getDouble(
				"TOT_AMT", 0));
		// �ֽ�֧��
		oneReceiptParm.setData("Cash", "TEXT", StringTool.round(recpParm
				.getDouble("PAY_CASH", 0), 2));
		// ��ӡ����
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd"));
		// ҽ�����
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", StringTool.round(recpParm
				.getDouble("PAY_DEBIT", 0), 2));
		// ҽ������
		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
				"CASHIER_CODE", 0));

		// ��ӡ��
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		// if (this.getValue("BILL_TYPE").equals("E")){
		TParm EKTTemp = EKTIO.getInstance().TXreadEKT();
		if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
			this.messageBox("ҽ�ƿ�û��ʹ�ò����Դ�Ʊ");
			return;
		}
		oneReceiptParm.setData("START_AMT", "TEXT", "0.00"); // �𸶽��
		oneReceiptParm.setData("MAX_AMT", "TEXT", ""); // ����޶����
		oneReceiptParm.setData("DA_AMT", "TEXT", "0.00"); // �˻�֧��
		oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(��������嵥)");
		oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
		oneReceiptParm.setData("CARD_CODE", "TEXT", EKTTemp.getValue("MR_NO")
				+ EKTTemp.getValue("SEQ"));
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");
		for (int i = 1; i <= 30; i++) {
			if (i < 10)
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "" : recpParm
						.getData("CHARGE0" + i, 0));
			else
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "" : recpParm
						.getData("CHARGE" + i, 0));
		}
	    oneReceiptParm.setData("RECEIPT_NO", "TEXT", parm.getValue("RECEIPT_NO"));//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//				oneReceiptParm);
	      this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                               IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm));//����ϲ�modify by wanglong 20130730

	}

	/**
	 * ���
	 */
	public void onExport() {
		ExportExcelUtil.getInstance().exportExcel(table, "���˵�λ������㱨��");
	}

	/**
	 * ȫѡ��ť
	 */
	public void onSelectAll() {
		TParm parm = table.getParmValue();
		if (parm.getCount() <= 0) {
			return;
		}
		// ȫѡ��ѡ
		if (this.getValueBoolean("CHK_ALL")) {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG",i,"Y");
				
			}
		} else {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("FLG",i,"N");	
			}
			
		}
		table.setParmValue(parm);
	}
}
