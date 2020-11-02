package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;

import com.dongyang.ui.TTable;
import jdo.spc.INDSQL;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.TypeTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import jdo.util.Manager;
import jdo.spc.INDTool;
import com.dongyang.ui.TTableNode;
import com.javahis.ui.sys.SYSFee_FeeControl;

/**
 * <p>
 * Title: ���ұ�ҩ����Control
 * </p>
 * 
 * <p>
 * Description: ���ұ�ҩ����Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.05.12
 * @version 1.0
 */
public class INDDeptRequestOfUddControl extends TControl {

	// ������
	private TTable table_m;

	// ϸ����
	private TTable table_d;

	// ���뵥��
	private String request_no;

	// ȫ������Ȩ��
	private boolean dept_flg = true;

	// �ż�ס���
	private String type;
	private ODO odo;

	public INDDeptRequestOfUddControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��������¼�
		addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,
				"onTableMChangeValue");
		// ��TABLE_M�е�CHECKBOX��������¼�
		callFunction("UI|TABLE_M|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableMCheckBoxClicked");
		// ��TABLE_D�е�CHECKBOX��������¼�
		callFunction("UI|TABLE_D|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableDCheckBoxClicked");

		// ��ʼ��������
		initPage();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (!CheckDataM()) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
		parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
		parm.setData("START_DATE", formatString(this
				.getValueString("START_DATE")));
		parm.setData("END_DATE", formatString(this.getValueString("END_DATE")));
		// =======pangben modify 20110511 start ����������
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// =======pangben modify 20110511 stop
		// ������
		if (this.getRadioButton("REQUEST_FLG_A").isSelected()) {
			if (this.getRadioButton("REQUEST_TYPE_B").isSelected()) {// �������ϸ��������
				// ���쵥
				if (null == getValueString("REQUEST_NO")
						|| "".equals(getValueString("REQUEST_NO"))) {
					this.messageBox("���뵥�Ų���Ϊ��");
					return;
				}
			}
			if (null != getValueString("REQUEST_NO")
					&& !"".equals(getValueString("REQUEST_NO"))) {
				parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
			}
			parm.setData("REQUEST_FLG_A", "Y");
		} else {
			parm.setData("REQUEST_FLG_A", "N");
		}

		// lirui 2012-6-4 start ��ҩƷ���࣬���ƶ���ҩƷ
		// ���ؽ����
		TParm result = new TParm();

		// ҩƷ����
		if (getRadioButton("Normal").isSelected()) {
			// ��ͨҩƷ
			parm.setData("CTRLDRUGCLASS_CODE_A", "A");

		}
		if (getRadioButton("drug").isSelected()) {
			// ����ҩ
			parm.setData("CTRLDRUGCLASS_CODE_B", "B");
		}
		// lirui 2012-6-4 end ��ҩƷ���࣬���ƶ���ҩƷ
		// ȫ��ҩƷ
		result = INDTool.getInstance().onQueryDeptFromOdiDspnm(parm);
		TParm parmM = result.getParm("RESULT_M", 0);
		TParm parmD = result.getParm("RESULT_D", 0);
		System.out.println("parmM===" + parmM);
		System.out.println("parmD===" + parmD);
		// if (parmM.getCount() == 0 || parmD.getCount("STOCK_PRICE") <= 0) {
		// this.messageBox("�޲�ѯ����");
		// return;
		// }
		table_m.setParmValue(parmM);
		table_d.setParmValue(parmD);
		// Ĭ�ϼ����ܽ�� by liyh 20120910
		setSumRetailMoneyOnQuery(parmM);
	}

	/**
	 * �������쵥
	 */
	public void onSave() {
		if (!CheckDataM()) {
			return;
		}
		if (!CheckDataD()) {
			return;
		}
		TParm parm = new TParm();
		// �������ݣ����뵥����
		getRequestExmParmM(parm);
		// System.out.println("parm--1-" + parm);
		// �������ݣ����뵥ϸ��
		getRequestExmParmD(parm);
		// �жϸ������(�ż�ס)
		parm.setData("TYPE", type);
		// �������ݣ���������״̬
		getDeptRequestUpdate(parm);
		// <---- д��DRUG_CATEGORYֵ identify by shendr 2013.7.17
		if (getRadioButton("Normal").isSelected()) {
			parm.setData("DRUG_CATEGORY", "1");
		} else if (getRadioButton("drug").isSelected()) {
			parm.setData("DRUG_CATEGORY", "2");
		}
		// ------->
		TParm result = new TParm();
		// System.out.println("parm--3-" + parm);

		// �����������ӿڷ���������ǰ��onCreateDeptExmRequest��ΪonCreateDeptExmRequestSpc
		result = TIOM_AppServer.executeAction("action.spc.INDRequestAction",
				"onCreateDeptOdiRequestSpc", parm);

		String msg = "";
		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			// this.messageBox(result.getErrText());
			String errText = result.getErrText();
			String[] errCode = errText.split(";");
			for (int i = 0; i < errCode.length; i++) {
				String orderCode = errCode[i];
				TParm returnParm = SYSFeeTool.getInstance().getFeeAllData(
						orderCode);
				if (returnParm != null && returnParm.getCount() > 0) {
					returnParm = returnParm.getRow(0);
					msg += orderCode + " " + returnParm.getValue("ORDER_DESC")
							+ "  " + returnParm.getValue("SPECIFICATION")
							+ "\n";
					if (i == errCode.length - 1) {
						msg += "������������ҩƷ���ձ���";
					}
				} else {
					msg += orderCode + "\n";
				}
			}
			this.messageBox(msg);
			return;
		}
		this.messageBox("P0001");
		onClear();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		((TMenuItem) getComponent("save")).setEnabled(true);
		((TMenuItem) getComponent("printM")).setEnabled(true);
		((TMenuItem) getComponent("printD")).setEnabled(false);
		((TMenuItem) getComponent("printRecipe")).setEnabled(false);
		table_m.setVisible(true);
		table_m.removeRowAll();
		table_d.setVisible(false);
		table_d.removeRowAll();
		// ��ջ�������
		String clearString = "APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
				+ "SELECT_ALL;URGENT_FLG;CHECK_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
				+ "PRICE_DIFFERENCE";
		clearValue(clearString);
		getRadioButton("REQUEST_FLG_B").setSelected(true);
		getRadioButton("REQUEST_TYPE_A").setSelected(true);
	}

	/**
	 * �������뵥��
	 */
	public boolean request() {

		// String REQUEST_NO = (String) table_m.getItemData(0,"REQUEST_NO");
		// TParm num=new
		// TParm(TJDODBTool.getInstance().select(INDSQL.checkData(REQUEST_NO)));
		// int number = num.getCount();
		// this.messageBox("wocao--"+number);
		// if(number>1)
		// {
		// this.messageBox("���뵥̫�࣡");
		//        	
		// }

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < table_m.getRowCount(); i++) {
			set.add((String) table_m.getItemData(i, "REQUEST_NO"));
		}
		int number = set.size();
		if (number > 1) {
			this.messageBox("һ��ֻ�ܴ�ӡһ�����뵥�����ݣ�");
			return false;

		}
		return true;
	}

	/**
	 * ��ӡ���ܵ�
	 */
	public void onPrintM() {

		boolean flg = true;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û�л�����Ϣ");
			return;
		}
		boolean no = request();
		if (no == true) {
			Timestamp datetime = StringTool.getTimestamp(new Date());

			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion())
					+ "���ұ�ҩ��");
			date.setData("DATE_AREA", "TEXT", "���뵥��:"
					+ table_m.getItemData(0, "REQUEST_NO"));
			date.setData("ORG_CODE_IN", "TEXT", "���벿��: "
					+ this.getComboBox("APP_ORG_CODE").getSelectedName());
			date.setData("ORG_CODE_OUT", "TEXT", "���ܲ���: "
					+ this.getComboBox("TO_ORG_CODE").getSelectedName());
			date.setData("DATE", "TEXT", "�Ʊ�ʱ��: "
					+ datetime.toString().substring(0, 10));
			// �������
			String order_code = "";
			String unit_type = "1";
			String order_desc = "";
			TParm parm = new TParm();
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				order_code = table_m.getParmValue().getValue("ORDER_CODE", i);
				TParm inparm = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getOrderInfoByCode(order_code, unit_type)));
				if (inparm == null || inparm.getErrCode() < 0) {
					this.messageBox("ҩƷ��Ϣ����");
					return;
				}
				if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
					order_desc = inparm.getValue("ORDER_DESC", 0);
				} else {
					order_desc = inparm.getValue("ORDER_DESC", 0) + "("
							+ inparm.getValue("GOODS_DESC", 0) + ")";
				}
				parm.addData("ORDER_DESC", order_desc);

				parm.addData("SPECIFICATION", table_m.getItemData(i,
						"SPECIFICATION"));
				parm.addData("UNIT", table_m.getItemData(i, "UNIT_CHN_DESC"));
				parm.addData("QTY", table_m.getItemDouble(i, "DOSAGE_QTY"));
				parm.addData("STOCK_PRICE", table_m.getItemDouble(i,
						"STOCK_PRICE"));
				parm
						.addData("STOCK_AMT", table_m.getItemDouble(i,
								"STOCK_AMT"));
				parm
						.addData("OWN_PRICE", table_m.getItemDouble(i,
								"OWN_PRICE"));
				parm.addData("OWN_AMT", table_m.getItemDouble(i, "OWN_AMT"));
			}
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			date.setData("TABLE", parm.getData());
			// ��β����
			date.setData("STOCK_AMT", "TEXT", "�ɹ��ܽ��: "
					+ StringTool.round(Double.parseDouble(this
							.getValueString("SUM_VERIFYIN_PRICE")), 4));

			date.setData("OWN_AMT", "TEXT", "�����ܽ��: "
					+ StringTool.round(Double.parseDouble(this
							.getValueString("SUM_RETAIL_PRICE")), 4));

			// date.setData("DIFF_AMT", "TEXT", "�������: " +
			// StringTool.round(Double.parseDouble(this.
			// getValueString("PRICE_DIFFERENCE")), 4));
			//		          
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DeptRequestM.jhw",
					date);
		}

	}

	/**
	 * ��ӡ��ϸ��
	 */
	public void onPrintD() {
		boolean flg = true;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!"".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û����ϸ��Ϣ");
			return;
		}

	}

	/**
	 * ������벿��
	 */
	public void onChangeAppOrg() {
		if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
			// Ԥ������ⷿ
			TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
							Operator.getRegion())));
			getComboBox("TO_ORG_CODE").setSelectedID(
					sup_org_code.getValue("SUP_ORG_CODE", 0));
		}
	}

	/**
	 * ���ͳ��״̬
	 */
	public void onChangeRequestFlg() {
		if (this.getRadioButton("REQUEST_FLG_B").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				((TMenuItem) getComponent("printM")).setEnabled(true);
				((TMenuItem) getComponent("printD")).setEnabled(false);
				((TMenuItem) getComponent("printRecipe")).setEnabled(false);
				table_m.setVisible(true);
				table_d.setVisible(false);
			} else {
				((TMenuItem) getComponent("printM")).setEnabled(false);
				// ( (TMenuItem) getComponent("printD")).setEnabled(true);
				((TMenuItem) getComponent("printRecipe")).setEnabled(true);
				table_m.setVisible(false);
				table_d.setVisible(true);
			}
		} else {
			((TMenuItem) getComponent("save")).setEnabled(false);
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				((TMenuItem) getComponent("printM")).setEnabled(true);
				((TMenuItem) getComponent("printD")).setEnabled(false);
				((TMenuItem) getComponent("printRecipe")).setEnabled(false);
				table_m.setVisible(true);
				table_d.setVisible(false);
			} else {
				((TMenuItem) getComponent("printM")).setEnabled(false);
				((TMenuItem) getComponent("printD")).setEnabled(true);
				((TMenuItem) getComponent("printRecipe")).setEnabled(true);
				table_m.setVisible(false);
				table_d.setVisible(true);
			}
		}
		onQuery();
	}

	/**
	 * ȫѡ
	 */
	public void onSelectAll() {
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		} else {
			flg = "N";
		}
		for (int i = 0; i < table_m.getRowCount(); i++) {
			table_m.setItem(i, "SELECT_FLG", flg);
		}
		for (int i = 0; i < table_d.getRowCount(); i++) {
			table_d.setItem(i, "SELECT_FLG", flg);
		}
		setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
		setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
		setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
				- getSumRegMoney(), 4));
	}

	/**
	 * ���(TABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 */
	public void onTableMCheckBoxClicked(Object obj) {
		table_m.acceptText();
		// this.messageBox("2222222222");
		// ���ѡ�е���
		int column = table_m.getSelectedColumn();
		if (column == 0) {
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
		}
	}

	/**
	 * ���ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableMChangeValue(Object obj) {
		// ֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// �ж����ݸı�
		if (node.getValue().equals(node.getOldValue()))
			return true;
		int column = node.getColumn();
		int row = node.getRow();

		String flg = "N";
		String flgString = table_d.getItemString(row, "SELECT_FLG");
		if ("N".equals(flgString)) {
			flg = "Y";
		} else {
			flg = "N";
		}
		table_d.setItem(row, "SELECT_FLG", flg);
		if (column == 0) {
			return false;
		}
		if (column == 4) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("������������С�ڻ����0");
				return true;
			}
			double amt1 = StringTool.round(qty
					* table_m.getItemDouble(row, "STOCK_PRICE"), 2);
			double amt2 = StringTool.round(qty
					* table_m.getItemDouble(row, "OWN_PRICE"), 2);
			table_m.setItem(row, "STOCK_AMT", amt1);
			table_m.setItem(row, "OWN_AMT", amt2);
			table_m.setItem(row, "DIFF_AMT", amt2 - amt1);
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
			return false;
		}
		return true;
	}

	/**
	 * ���(TABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 */
	public void onTableDCheckBoxClicked(Object obj) {
		table_d.acceptText();
		// ���ѡ�е���
		int column = table_d.getSelectedColumn();
		if (column == 0) {
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
		}
	}

	public void onPrintRecipe() {
		// this.messageBox(Operator.getDept());
		int row = this.table_d.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ���ӡ���ݣ�");
			return;
		}
		String caseNo = this.table_d.getParmValue().getValue("CASE_NO", row);
		String mrNo = this.table_d.getParmValue().getValue("MR_NO", row);
		String rxNo = this.table_d.getParmValue().getValue("RX_NO", row);
		String exeDeptCode = this.table_d.getParmValue().getValue(
				"EXEC_DEPT_CODE", row);
		// case_no mr_no
		odo = new ODO(caseNo, mrNo, Operator.getDept(), Operator.getID(), "O");
		// TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
		// realDeptCode, rxType, odo, rxNo,
		// order.getItemString(0, "PSY_FLG"));
		// 030101
		// System.out.println(caseNo+"====="+exeDeptCode+"===="+rxNo);
		TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
				exeDeptCode, "1", odo, rxNo, "Y");
		// luhai add ����ǩsql begin
		String rxNo2 = inParam.getValue("RX_NO");
		String caseNo2 = caseNo;
		// **********************************************************
		// luhai modify 2012-05-09 begin ��ҩƷ����Ŀ����ӡ����ǩ begin
		// **********************************************************
		String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '��' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
				+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "
				+ " OPD_ORDER.SPECIFICATION cc, "
				+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN 'Ƥ��' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
				+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
				+ " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (16-LENGTH(SYS_PHAFREQ.FREQ_CHN_DESC)), ' ')|| OPD_ORDER.TAKE_DAYS FF,"
				+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "
				+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,"
				+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '�Ա�  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE "
				+ " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE "
				+ " WHERE       CASE_NO = '"
				+ caseNo2
				+ "'"
				+ "  AND RX_NO = '"
				+ rxNo2
				+ "'"
				+ " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
				+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
				+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
				+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
				+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
				+ "  AND OPD_ORDER.CAT1_TYPE='PHA' "
				+ " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
		// **********************************************************
		// luhai modify 2012-05-09 begin ��ҩƷ����Ŀ����ӡ����ǩ end
		// **********************************************************
		TParm westResult = new TParm(TJDODBTool.getInstance().select(westsql));
		if (westResult.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		if (westResult.getCount() < 0) {
			this.messageBox("û�д���ǩ����.");
			return;
		}

		TParm westParm = new TParm();
		double pageAmt2 = 0;
		DecimalFormat df2 = new DecimalFormat("############0.00");
		for (int i = 0; i < westResult.getCount(); i++) {
			westParm.addData("AA", westResult.getData("AA", i));
			westParm.addData("BB", westResult.getData("BB", i));
			westParm.addData("CC", westResult.getData("CC", i));
			westParm.addData("DD", westResult.getData("DD", i));
			westParm.addData("EE", westResult.getData("EE", i));
			westParm.addData("FF", westResult.getData("FF", i));
			westParm.addData("ER", westResult.getData("ER", i));
			westParm.addData("GG", westResult.getData("GG", i));
			pageAmt2 += (westResult.getDouble("DOSAGE_QTY", i) * westResult
					.getDouble("OWN_PRICE", i));
			if ((i != 0 && (i + 1) % 5 == 0) || i == westResult.getCount() - 1) {
				westParm.addData("AA", "");
				westParm.addData("BB", "");
				westParm.addData("CC", "");
				westParm.addData("DD", "");
				westParm.addData("EE", "");
				westParm.addData("FF", "�������(��):");
				westParm.addData("ER", df2.format(pageAmt2));
				westParm.addData("GG", "");
				pageAmt2 = 0;
			}
		}
		westParm.setCount(westParm.getCount("AA"));
		westParm.addData("SYSTEM", "COLUMNS", "AA");
		westParm.addData("SYSTEM", "COLUMNS", "BB");
		westParm.addData("SYSTEM", "COLUMNS", "CC");
		westParm.addData("SYSTEM", "COLUMNS", "DD");
		westParm.addData("SYSTEM", "COLUMNS", "EE");
		westParm.addData("SYSTEM", "COLUMNS", "FF");
		westParm.addData("SYSTEM", "COLUMNS", "ER");
		westParm.addData("SYSTEM", "COLUMNS", "GG");

		inParam.setData("ORDER_TABLE", westParm.getData());
		// luhai add ����ǩsql end
		Object obj = this.openPrintDialog(
				"%ROOT%\\config\\prt\\OPD\\OpdOrderSheet.jhw", inParam, false);
	}

	/**
	 * ������(TABLE_M)�����¼�
	 */
	public void onTableMClicked() {
		int row_m = table_m.getSelectedRow();
		if (row_m != -1) {
			// ������ѡ���������Ϸ�
			setValue("REQUEST_NO", table_m.getItemData(row_m, "REQUEST_NO"));
		}
	}

	/**
	 * ������(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {

	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		/**
		 * Ȩ�޿��� Ȩ��1:ֻ��ʾ������������ Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ��
		 */
		// �ж��Ƿ���ʾȫԺҩ�ⲿ��
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndOrgByUserId(Operator.getID(), Operator
							.getRegion(),
							"AND B.ORG_TYPE = 'C' AND B.EXINV_FLG = 'Y' ")));
			getComboBox("APP_ORG_CODE").setParmValue(parm);
			dept_flg = false;
			if (parm.getCount("NAME") > 0) {
				getComboBox("APP_ORG_CODE").setSelectedIndex(1);
			}
			// Ԥ������ⷿgetINDORG
			TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
							Operator.getRegion())));
			getComboBox("TO_ORG_CODE").setSelectedID(
					sup_org_code.getValue("SUP_ORG_CODE", 0));
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// ��ʼ��TABLE
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
		// ( (TMenuItem) getComponent("save")).setEnabled(false);
		((TMenuItem) getComponent("printM")).setEnabled(false);
		((TMenuItem) getComponent("printD")).setEnabled(false);
		((TMenuItem) getComponent("printRecipe")).setEnabled(false);
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("APP_ORG_CODE"))) {
			this.messageBox("���벿�Ų���Ϊ��");
			return false;
		}
		if ("Y".equals(this.getValue("REQUEST_FLG_A"))) {
			if ("".equals(getValueString("TO_ORG_CODE"))) {
				this.messageBox("���ղ��Ų���Ϊ��");
				return false;
			}
		}
		return true;
	}

	/**
	 * ���ݼ���
	 * 
	 * @return boolean
	 */
	private boolean CheckDataD() {
		if ("".equals(getValueString("TO_ORG_CODE"))) {
			this.messageBox("���ܲ��Ų���Ϊ��");
			return false;
		}
		if (table_d.getRowCount() == 0) {
			this.messageBox("û����������");
			return false;
		}
		boolean flg = true;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û����������");
			return false;
		}
		return true;
	}

	/**
	 * �������ݣ����뵥����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmM(TParm parm) {
		TParm inparm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		request_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "TEC");
		inparm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
		inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		inparm.setData("REQUEST_DATE", date);
		inparm.setData("REQUEST_USER", Operator.getID());
		inparm.setData("REASON_CHN_DESC", this
				.getValueString("REASON_CHN_DESC"));
		inparm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		// zhangyong20110517
		inparm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("REQUEST_M", inparm.getData());
		return parm;
	}

	/**
	 * �������ݣ����뵥ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmD(TParm parm) {
		TParm inparm = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		int count = 0;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			inparm.addData("REQUEST_NO", request_no);
			inparm.addData("SEQ_NO", count + 1);
			inparm.addData("ORDER_CODE", table_m.getParmValue().getValue(
					"ORDER_CODE", i));
			inparm.addData("BATCH_NO", "");
			inparm.addData("VALID_DATE", tnull);
			inparm.addData("QTY", table_m.getItemDouble(i, "DOSAGE_QTY"));
			inparm.addData("ACTUAL_QTY", 0);
			inparm.addData("UPDATE_FLG", "0");
			inparm.addData("OPT_USER", user_id);
			inparm.addData("OPT_DATE", date);
			inparm.addData("OPT_TERM", user_ip);
			inparm.addData("STATION_CODE", getValueString("APP_ORG_CODE"));
			inparm.addData("START_DATE", formatString(this
					.getValueString("START_DATE")));
			inparm.addData("END_DATE", formatString(this
					.getValueString("END_DATE")));
			count++;
			// System.out.println(count+"----------inparm:"+inparm);
		}
		inparm.setCount(count);
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
	}

	/**
	 * �������ݣ���������״̬
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getDeptRequestUpdate(TParm parm) {
		TParm inparm = new TParm();
		int count = 0;

		for (int i = 0; i < table_d.getRowCount(); i++) {
			if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			inparm.setData("CASE_NO", count, table_d.getParmValue().getValue(
					"CASE_NO", i));
			inparm.setData("ORDER_NO", count, table_d.getParmValue().getInt(
					"CASE_NO_SEQ", i));
			inparm.setData("ORDER_SEQ", count, table_d.getParmValue().getInt(
					"SEQ_NO", i));
			inparm.setData("REQUEST_FLG", count, "Y");
			inparm.setData("REQUEST_NO", count, request_no);
			count++;
		}

		parm.setData("UPDATE", inparm.getData());
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
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * ��ʽ���ַ���(ʱ���ʽ)
	 * 
	 * @param arg
	 *            String
	 * @return String YYYYMMDDHHMMSS
	 */
	private String formatString(String arg) {
		arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10)
				+ arg.substring(11, 13) + arg.substring(14, 16)
				+ arg.substring(17, 19);
		return arg;
	}

	/**
	 * ���������ܽ��
	 * 
	 * @return
	 */
	private double getSumRetailMoney() {
		table_m.acceptText();
		table_d.acceptText();
		double sum = 0;
		if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_m.getItemDouble(i, "OWN_AMT");
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_d.getItemDouble(i, "OWN_AMT");
			}
		}
		return StringTool.round(sum, 4);
	}

	/**
	 * ����ɹ�/�����ܽ��
	 * 
	 * @return
	 * @author liyh
	 * @date 20120910
	 */
	private void setSumRetailMoneyOnQuery(TParm parmM) {
		// �����ܽ��
		double sum_retail = 0.0;
		// �ɹ��ܽ��
		double sum_verifyin = 0.0;
		int count = parmM.getCount();
		if (null != parmM && count > 0) {
			for (int i = 0; i < count; i++) {
				sum_retail += parmM.getDouble("OWN_AMT", i);
				sum_verifyin += parmM.getDouble("STOCK_AMT", i);
			}

		}
		setValue("SUM_RETAIL_PRICE", sum_retail);
		setValue("SUM_VERIFYIN_PRICE", sum_verifyin);
	}

	/**
	 * ����ɱ��ܽ��
	 * 
	 * @return
	 */
	private double getSumRegMoney() {
		table_m.acceptText();
		table_d.acceptText();
		double sum = 0;
		if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_m.getItemDouble(i, "STOCK_AMT");
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_d.getItemDouble(i, "STOCK_AMT");
			}
		}
		return StringTool.round(sum, 4);
	}

	/**
	 * ȡ��SYS_FEE��Ϣ��������״̬����
	 * 
	 * @param order_code
	 *            String
	 */
	private void setSysStatus(String order_code) {
		TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
		String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE") + " ҩƷ����:"
				+ order.getValue("ORDER_DESC") + " ��Ʒ��:"
				+ order.getValue("GOODS_DESC") + " ���:"
				+ order.getValue("SPECIFICATION");
		callFunction("UI|setSysStatus", status_desc);
	}
}
