package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.SPCDrugApplicationTool;
import jdo.spc.SPCInStoreTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �����龫��ҩ����Control
 * </p>
 * 
 * <p>
 * Description: �����龫��ҩ����Control
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
 * @author shendr 2013.07.18
 * @version 1.0
 */
public class SPCDrugApplicationControl extends TControl {

	/** Table�ؼ� */
	private TTable tableD;
	private TTable tableM;

	// �ż�ס���
	private String type;

	// ���뵥��
	private String request_no;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ����ѯ����
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("ORDER_DATE_END", date.toString().substring(0, 19)
				.replace('-', '/'));
		this.setValue("RECLAIM_DATE_END", date.toString().substring(0, 19)
				.replace('-', '/'));
		this.setValue("RECLAIM_DATE_START", StringTool.rollDate(date, -1)
				.toString().substring(0, 19).replace('-', '/'));
		this.setValue("ORDER_DATE_START", StringTool.rollDate(date, -2)
				.toString().substring(0, 19).replace('-', '/'));
		// ��ʼ��TABLE
		tableD = this.getTTable("TABLED");
		tableM = this.getTTable("TABLEM");
		InetAddress addr;
		String ip = "";
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// ��ñ���IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if ("".equals(ip)) {
			this.messageBox("��ȡ���ܹ���Ϣʧ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CABINET_IP", ip);
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);
		this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
		this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
		this.setValue("ORG_CHN_DESC", result.getData("ORG_CHN_DESC", 0));
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		TTable table = new TTable();
		if (tableM.isVisible()) {
			table = tableM;
		} else {
			table = tableD;
		}
		int row = table.getSelectedRow();
		TParm selParm = table.getParmValue().getRow(row);
		setValue("TOXIC_ID", selParm.getValue("TOXIC_ID"));
		setValue("RX_NO", selParm.getValue("RX_NO"));
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String ORDER_DATE_END = this.getValueString("ORDER_DATE_END");
		String RECLAIM_DATE_END = this.getValueString("RECLAIM_DATE_END");
		String RECLAIM_DATE_START = this.getValueString("RECLAIM_DATE_START");
		String ORDER_DATE_START = this.getValueString("ORDER_DATE_START");
		if (!StringUtil.isNullString(ORDER_DATE_END)) {
			ORDER_DATE_END = ORDER_DATE_END.substring(0, 19);
			parm.setData("ORDER_DATE_END", ORDER_DATE_END);
		}
		if (!StringUtil.isNullString(RECLAIM_DATE_END)) {
			RECLAIM_DATE_END = RECLAIM_DATE_END.substring(0, 19);
			parm.setData("RECLAIM_DATE_END", this.getValueString(
					"RECLAIM_DATE_END").substring(0, 19));
		}
		if (!StringUtil.isNullString(RECLAIM_DATE_START)) {
			RECLAIM_DATE_START = RECLAIM_DATE_START.substring(0, 19);
			parm.setData("RECLAIM_DATE_START", this.getValueString(
					"RECLAIM_DATE_START").substring(0, 19));
		}
		if (!StringUtil.isNullString(ORDER_DATE_START)) {
			ORDER_DATE_START = ORDER_DATE_START.substring(0, 19);
			parm.setData("ORDER_DATE_START", this.getValueString(
					"ORDER_DATE_START").substring(0, 19));
		}
		parm.setData("RX_NO", this.getValueString("RX_NO"));
		parm.setData("TOXIC_ID", this.getValueString("TOXIC_ID"));
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		// ��ѯ�ж�
		if (this.getTRadioButton("REQUEST_FLG_A").isSelected()) {// ������
			parm.setData("REQUEST_FLG", "Y");
			if (this.getTRadioButton("REQUEST_TYPE_B").isSelected()) {// ��ϸ
				parm.setData("REQUEST_TYPE", "D");
				resultD = SPCDrugApplicationTool.getInstance().queryOPDOrder(
						parm);
			} else {
				parm.setData("REQUEST_TYPE", "M");
				resultM = SPCDrugApplicationTool.getInstance().queryOPDOrderM(
						parm);
			}
		} else {// δ����
			parm.setData("REQUEST_FLG", "N");
			if (this.getTRadioButton("REQUEST_TYPE_B").isSelected()) {// ��ϸ
				parm.setData("REQUEST_TYPE", "D");
				resultD = SPCDrugApplicationTool.getInstance().queryOPDOrder(
						parm);
			} else {
				parm.setData("REQUEST_TYPE", "M");
				resultM = SPCDrugApplicationTool.getInstance().queryOPDOrderM(
						parm);
			}
		}
		if ("M".equals(parm.getData("REQUEST_TYPE"))) {
			if (resultM.getErrCode() < 0) {
				messageBox("E0008");
			} else {
				tableM.setParmValue(resultM);
				tableM.setVisible(true);
				tableD.setVisible(false);
			}
		} else {
			if (resultD.getErrCode() < 0) {
				messageBox("E0008");
			} else {
				tableD.setParmValue(resultD);
				tableM.setVisible(false);
				tableD.setVisible(true);
			}
		}
	}

	/**
	 * ɨ���龫����
	 */
	public void queryByToxicId() {
		onQuery();
	}

	/**
	 * �Զ��������뵥��
	 */
	public void onSave() {
		TParm parm = new TParm();
		// �������ݣ����뵥����
		getRequestExmParmM(parm);
		// �������ݣ����뵥ϸ��
		getRequestExmParmD(parm);
		// �жϸ������(�ż�ס)
		parm.setData("TYPE", type);
		TParm result = new TParm();
		// �����������ӿڷ���
		result = TIOM_AppServer.executeAction("action.spc.INDRequestAction",
				"onCreateDeptOpdRequestSpc", parm);
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
		onQuery();
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
		TTable table = new TTable();
		if (tableM.isVisible()) {
			table = tableM;
		} else {
			table = tableD;
		}
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "TEC");
		inparm.setData("UNIT_TYPE", "1");
		// ������������� ������ 0202
		String parameter = (String) this.getParameter();
		inparm.setData("APP_ORG_CODE", parameter);
		inparm.setData("TO_ORG_CODE", table.getParmValue().getValue(
				"EXEC_DEPT_CODE", 0));
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("REQUEST_DATE", date);
		inparm.setData("REQUEST_USER", Operator.getID());
		inparm.setData("REASON_CHN_DESC", "");
		inparm.setData("DESCRIPTION", "");
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		inparm.setData("REGION_CODE", Operator.getRegion());
		inparm.setData("DRUG_CATEGORY", "2");
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
		TTable table = new TTable();
		if (tableM.isVisible()) {
			table = tableM;
		} else {
			table = tableD;
		}
		int count = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if ("N".equals(table.getItemString(i, "SEL"))) {
				continue;
			}
			inparm.addData("REQUEST_NO", request_no);
			inparm.addData("SEQ_NO", count + 1);
			inparm.addData("ORDER_CODE", table.getParmValue().getValue(
					"ORDER_CODE", i));
			inparm.addData("BATCH_NO", "");
			inparm.addData("VALID_DATE", tnull);
			inparm.addData("ACTUAL_QTY", 0);
			inparm.addData("QTY", table.getItemDouble(i, "DOSAGE_QTY"));
			inparm.addData("UPDATE_FLG", "0");
			inparm.addData("OPT_USER", user_id);
			inparm.addData("OPT_DATE", date);
			inparm.addData("OPT_TERM", user_ip);
			inparm.addData("START_DATE", formatString(this
					.getValueString("ORDER_DATE_START")));
			inparm.addData("END_DATE", formatString(this
					.getValueString("ORDER_DATE_END")));
			inparm.addData("EXEC_DEPT_CODE", table.getParmValue().getValue(
					"EXEC_DEPT_CODE", i));
			count++;
		}
		inparm.setCount(count);
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
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
	 * ���
	 */
	public void onClear() {
		String tags = "TOXIC_ID;RX_NO";
		clearValue(tags);
		tableD.removeRowAll();
	}

	/**
	 * ��ȡTTable
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��ȡTTextFormat
	 * 
	 * @param tag
	 * @return
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

	/**
	 * ��ȡTRadioButton
	 * 
	 * @param tag
	 * @return
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

}
