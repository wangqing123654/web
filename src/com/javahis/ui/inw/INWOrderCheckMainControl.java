package com.javahis.ui.inw;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.util.HSSFColor.RED;

import jdo.adm.ADMTool;
import jdo.inw.InwForOdiTool;
import jdo.inw.InwOrderCheckTool;
import jdo.opd.TotQtyTool;
import jdo.pha.TXNewATCTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.JTableBase;
import com.dongyang.ui.base.TTableBase;
import com.dongyang.ui.base.TTableCellRenderer;
import com.dongyang.ui.base.TTableColumn;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.sys.LEDUI;
import com.javahis.util.DateUtil;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��ʿվҽ�����
 * </p>
 * 
 * <p>
 * Description: סԺ��ʿվҽ���������
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */

public class INWOrderCheckMainControl extends TControl {
	private Compare compare = new Compare();

	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	/**
	 * �����ϵ�CUI����
	 */
	private TTextFormat from_Date;
	private TTextFormat to_Date;

	private TTextField from_Time;
	private TTextField to_Time;

	// ҽ�����
	private TRadioButton ord1All;
	private TRadioButton ord1ST;
	private TRadioButton ord1UD;
	private TRadioButton ord1DS;
	private TRadioButton ord1IG;

	// ҽ������
	private TRadioButton ord2All;
	private TRadioButton ord2PHA;
	private TRadioButton ord2PL;
	private TRadioButton ord2ENT;

	// ҩ������
	TCheckBox typeO;
	TCheckBox typeE;
	TCheckBox typeI;
	TCheckBox typeF;

	// �龫
	TCheckBox typeC;

	// ��ʱ/������
	TLabel StUdLab;
	// ���ܹ�ȡҩ
	private TRadioButton medSta;
	// סԺҩ����ҩ
	private TRadioButton medOrg;

	// ��ѯ��ʱ������ add by wanglong 20130626
	private TRadioButton firstDateRadio;// Ĭ��ʱ��
	private TRadioButton secondDateRadio;// ҽ������ʱ��

	// ���״̬
	private TRadioButton checkAll;
	private TRadioButton checkYES;
	private TRadioButton checkNO;

	// ��table
	private TTable mainTable;
	// ȫ��ִ��
	private TCheckBox exeAll;
	// ȫ����ӡ
	private TCheckBox printAll;
	// ĳ�����˵ľ����
	private String caseNo = "";
	private String stationCode = "";

	private LEDUI ledui;

	boolean saveFlg = true;

	Map map;
	// ������Ժ��ҩ add by guoy 20151124
	private TCheckBox includeOutMed;

	// ҩ����Ŷ�Ӧ������map
	Map tranMap = new HashMap();

	// ����ҽ��������ʾ����ɫ��
	Color yellowColor = new Color(255, 255, 0); // ==================duzhw add
												// 20131101
	private boolean opeFlg;// ����ҽ��ע��wanglong add 20140707
	private String opDeptCode;// ��������wanglong add 20140707
	private String opBookSeq;// �������뵥��wanglong add 20140707

	/**
	 * ��ʼ��
	 */
	public INWOrderCheckMainControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		// �õ��ⲿ�Ĳ���
		initParmFromOutside();
		// ������ĳ�ʼ��
		myInitControler();
		if (isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			this.callFunction("UI|ord1ST|setSelected", true);// ѡ�С���ʱ����ѡ��
		}
		// ִ�в�ѯ
		onQuery();
		lockComponet();
		// �������
		addListener(mainTable);
	}

	public void setTranMap() {

	}

	public void lockComponet() {
		// ((TTextFormat)getComponent("DEPT_CODE")).setEnabled(false);
		// ((TTextFormat)getComponent("VC_CODE")).setEnabled(false);
		((TMenuItem) getComponent("save")).setEnabled(saveFlg);
		callFunction("UI|medPrint|setEnabled", false);
		if (isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			this.callFunction("UI|ord1All|setEnabled", false);// ���á�ȫ������ѡ��
			this.callFunction("UI|ord1UD|setEnabled", false);// ���á����ڡ���ѡ��
			this.callFunction("UI|ord1DS|setEnabled", false);// ���á���Ժ��ҩ����ѡ��
			this.callFunction("UI|ord1IG|setEnabled", false);// ���á�סԺ��ҩ����ѡ��
		} else {
			callFunction("UI|medApplyNo|setVisible", false);// ��ʾ
		}
	}

	/**
	 * ��ʼ���������caseNo/stationCode�����ⲿ����������洫���Ĳ�����
	 */
	public void initParmFromOutside() {
		// �Ӳ�����������õ�����TParm
		TParm outsideParm = (TParm) this.getParameter();
		// this.messageBox_(outsideParm);
		if (outsideParm != null) {
			// ������Ų�ѯ��caseNo
			setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
			// ��������ѯ��stationCode
			setStationCode(outsideParm.getValue("INW", "STATION_CODE"));
			// �õ�LED����
			setLedui((LEDUI) outsideParm.getData("INW", "LEDUI"));
			saveFlg = outsideParm.getBoolean("INW", "SAVE_FLG");
			// ����ҽ��ע��wanglong add 20140707
			this.setOpeFlg(outsideParm.getBoolean("INW", "OPE_FLG"));
			// ��������wanglong add 20140707
			this.setOpDeptCode(outsideParm.getValue("INW", "OP_DEPT_CODE"));
			// �������뵥�� wanglong add 20140707
			this.setOpBookSeq(outsideParm.getValue("INW", "OPBOOK_SEQ"));
		}
	}

	/**
	 * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
	 */

	public void myInitControler() {

		// �õ�ʱ��ؼ�
		from_Date = (TTextFormat) this.getComponent("from_Date");
		to_Date = (TTextFormat) this.getComponent("to_Date");
		from_Time = (TTextField) this.getComponent("from_Time");
		to_Time = (TTextField) this.getComponent("to_Time");

		// �õ�table�ؼ�
		mainTable = (TTable) this.getComponent("MAINTABLE");
		// ��tableע��CHECK_BOX_CLICKED��������¼�
		this.callFunction("UI|MAINTABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");

		mainTable.addEventListener(mainTable.getTag() + "->" + TTableEvent.CHANGE_VALUE, this, "onDateTime");
		// ������tableע�ᵥ���¼�����
		this.callFunction("UI|MAINTABLE|addEventListener", "MAINTABLE->" + TTableEvent.CLICKED, this,
				"onMasterTableClicked");
		// �õ���ѯ����UI�Ķ���
		ord1All = (TRadioButton) this.getComponent("ord1All");
		ord1ST = (TRadioButton) this.getComponent("ord1ST");
		ord1UD = (TRadioButton) this.getComponent("ord1UD");
		ord1DS = (TRadioButton) this.getComponent("ord1DS");
		ord1IG = (TRadioButton) this.getComponent("ord1IG");

		ord2All = (TRadioButton) this.getComponent("ord2All");
		ord2PHA = (TRadioButton) this.getComponent("ord2PHA");
		ord2PL = (TRadioButton) this.getComponent("ord2PL");
		ord2ENT = (TRadioButton) this.getComponent("ord2ENT");

		typeO = (TCheckBox) this.getComponent("typeO");
		typeE = (TCheckBox) this.getComponent("typeE");
		typeI = (TCheckBox) this.getComponent("typeI");
		typeF = (TCheckBox) this.getComponent("typeF");

		typeC = (TCheckBox) this.getComponent("typeC");

		includeOutMed = (TCheckBox) this.getComponent("includeOutMed"); // add by guoy 20151124

		this.StUdLab = (TLabel) this.getComponent("STUD_LAB");
		this.medOrg = (TRadioButton) this.getComponent("medOrg");
		this.medSta = (TRadioButton) this.getComponent("medSta");

		firstDateRadio = (TRadioButton) this.getComponent("firstDateRadio");// add
																			// by
																			// wanglong
																			// 20130626
		secondDateRadio = (TRadioButton) this.getComponent("secondDateRadio");

		checkAll = (TRadioButton) this.getComponent("checkAll");
		checkYES = (TRadioButton) this.getComponent("checkYES");
		checkNO = (TRadioButton) this.getComponent("checkNO");
		// �õ�ȫȫ��ִ�пؼ�
		exeAll = (TCheckBox) this.getComponent("exeALL");
		// �õ�ȫ����ӡ�ؼ�
		printAll = (TCheckBox) this.getComponent("PRINT_ALL");
		// ��ʼ��ʱ��
		initDateTime();
	}

	public void onMasterTableClicked(int row) {
		TParm tableDate = mainTable.getParmValue();
		// add shibl 2012-03-05
		setValue("INW_STATION_CODE", tableDate.getValue("STATION_CODE", row));
		setValue("IPD_NO", tableDate.getValue("IPD_NO", row));
		setValue("MR_NO", tableDate.getValue("MR_NO", row));
		setValue("BED_NO", tableDate.getValue("BED_NO", row));
		setValue("VC_CODE", tableDate.getValue("JZDR", row));

		TParm admParm = new TParm();
		admParm.setData("CASE_NO", tableDate.getValue("CASE_NO", row));
		admParm = ADMTool.getInstance().getADM_INFO(admParm);
		TParm sexParm = new TParm(TJDODBTool.getInstance()
				.select("SELECT * FROM SYS_PATINFO A WHERE MR_NO ='" + tableDate.getValue("CASE_NO", row) + "'"));
		setValue("PAT_NAME", tableDate.getValue("PAT_NAME", row));
		setValue("SEX", sexParm.getData("SEX_CODE", 0));
		setValue("SERVICE_LEVELIN", admParm.getData("SERVICE_LEVEL", 0));
		setValue("WEIGHT", admParm.getData("WEIGHT", 0));
		setValue("ADM_DATE", admParm.getData("ADM_DATE", 0));
	}

	/**
	 * ��ʼ��ʱ��ؼ�
	 */
	public void initDateTime() {
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// String now = StringTool.getString(date, "yyyyMMddHHmmss");
		// �ý����00��00��ʼ����ʼʱ��
		from_Date.setValue(date);
		from_Time.setValue("00:00");
		// ���´ΰ�ҩʱ���ʼ��
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(date); // ���ݵ�ǰʱ���õ������ҩʱ��㣨�����ҩʱ�䣬�´ΰ�ҩʱ�䣩
		String disDateTime = (String) dispenseDttm.get(0);
		to_Date.setValue(
				disDateTime.substring(0, 4) + "/" + disDateTime.substring(4, 6) + "/" + disDateTime.substring(6, 8));
		to_Time.setValue(disDateTime.substring(8, 10) + ":" + disDateTime.substring(10, 12));
		String dateStr = StringTool.getString(date, "yyyy/MM/dd");
		this.setValue("ORDER_START_DATE", dateStr + " 00:00");// add by wanglong
																// 20130626
		this.setValue("ORDER_END_DATE",
				disDateTime.substring(0, 4) + "/" + disDateTime.substring(4, 6) + "/" + disDateTime.substring(6, 8)
						+ " " + disDateTime.substring(8, 10) + ":" + disDateTime.substring(10, 12));
	}

	/**
	 * ��ӡ��ҩȷ�ϵ�
	 */
	public void onDispenseSheet() {
		if (!this.checkYES.isSelected() // �����
				|| !this.ord2PHA.isSelected()// ҩ��
				|| !this.medSta.isSelected()) {// ������ҩ
			this.messageBox("ֻ���������,ҩ��,���ܹ�ȡҩ��״̬�²��ܴ�ӡ");
			return;
		}
		mainTable.acceptText();
		Map map = getPhatakeMedNo();
		if (map == null) {
			this.messageBox("û������");
			return;
		}
		Iterator order = map.values().iterator();
		while (order.hasNext()) {
			String it = (String) order.next();
			TParm inParm = new TParm();
			inParm.setData("TAKEMED_NO", it);
			inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
			TTextFormat station = (TTextFormat) getComponent("INW_STATION_CODE");
			String stationName = station.getText();
			if (StringUtil.isNullString(stationName))
				stationName = "ȫԺ";
			String fromDate = StringTool.getString((Timestamp) from_Date.getValue(), "yyyyMMdd");
			String fromTime = (String) from_Time.getValue();
			String fromCheckDate = fromDate + fromTime.substring(0, 2) + fromTime.substring(3);
			String toDate = StringTool.getString((Timestamp) to_Date.getValue(), "yyyyMMdd");
			String toTime = (String) to_Time.getValue();
			String toCheckDate = toDate + toTime.substring(0, 2) + toTime.substring(3);
			inParm.setData("STATION_NAME", stationName);
			inParm.setData("START_DATE", StringTool.getTimestamp(fromCheckDate, "yyyyMMddHHmm"));
			inParm.setData("END_DATE", StringTool.getTimestamp(toCheckDate, "yyyyMMddHHmm"));
			inParm.setData("IS_STATION", false);
			inParm.setData("DONE", Boolean.valueOf(TypeTool.getBoolean(getValue("checkNO"))));// δ���
			String caseNos = getCaseNos();
			inParm.setData("WHERE_1", caseNos);
			String phaDispenseNo = getPhaDispenseNo();
			inParm.setData("WHERE_2", phaDispenseNo);
			inParm.setData("TYPE_T", "��ʱҽ��/������ȡҩ��");
			inParm.setData("WHERE_3", " 'ST','F'");
			if ("''".equalsIgnoreCase(caseNos)) {
				messageBox_("û������");
				return;
			}
			// ���ͷ���
			inParm.setData("WHERE_4", getDoseTypeByWhere());
			// �÷�
			inParm.setData("DOSE_TYPE", getDoseTypeText());
			TParm parmData = this.mainTable.getParmValue();
			TParm parmRowData = new TParm();
			for (int i = 0; i < parmData.getCount("PRINT_FLG"); i++) {
				if (StringTool.getBoolean(parmData.getValue("PRINT_FLG", i))) {
					parmRowData = parmData.getRow(i);
					break;
				}
			}
			// ������Ա��ʱ��
			inParm.setData("USER_NAME", Operator.getName());// ��ʿ
			inParm.setData("DR_NAME", // ҽ��
					getOperatorName(InwForOdiTool.getInstance().queryOrderDrCode(inParm)));// modify by wanglong
																							// 20130607
			if ("''".equalsIgnoreCase(phaDispenseNo)) {
				messageBox_("û������");
				return;
			} else {
				if (InwForOdiTool.getInstance().existDrugOrder(inParm)) {// modify
																			// by
																			// wanglong
																			// 20130607
					inParm.setData("CTRLDRUG_FLG", "Y");
					openPrintWindow("%ROOT%\\config\\prt\\INW\\INWDispenseConfirmList.jhw", inParm, false);
				}
				if (InwForOdiTool.getInstance().existCommonOrder(inParm)) {
					inParm.setData("CTRLDRUG_FLG", "N");
					openPrintWindow("%ROOT%\\config\\prt\\INW\\INWDispenseConfirmList.jhw", inParm, false);
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private String getPhaDispenseNo() {
		mainTable.acceptText();
		TParm parm = mainTable.getParmValue();
		StringBuffer phaDispenseNo = new StringBuffer();
		if (parm.getCount() < 1)
			return "";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("PRINT_FLG", i)))
				phaDispenseNo.append("'").append(parm.getValue("ORDER_NO", i) + parm.getValue("ORDER_SEQ", i))
						.append("',");
		if (phaDispenseNo.length() < 1) {
			return "";
		} else {
			phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
			return phaDispenseNo.toString();
		}
	}

	/**
	 * 
	 * @return
	 */
	private Map getPhatakeMedNo() {
		mainTable.acceptText();
		TParm parm = mainTable.getParmValue();
		StringBuffer phaDispenseNo = new StringBuffer();
		Map orderNoMap = new HashMap();
		if (parm.getCount() < 1)
			return orderNoMap;
		int count = parm.getCount();
		String caseNo = "";
		String orderSeq = "";
		String orderNo = "";
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("PRINT_FLG", i))) {
				caseNo = parm.getValue("CASE_NO", i);
				orderSeq = parm.getValue("ORDER_SEQ", i);
				orderNo = parm.getValue("ORDER_NO", i);
				String sql = "SELECT TAKEMED_NO FROM ODI_DSPNM WHERE CASE_NO='" + caseNo + "' AND ORDER_NO='" + orderNo
						+ "' AND ORDER_SEQ='" + orderSeq + "' AND (DSPN_KIND='F' OR DSPN_KIND='ST')";
				TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
				if (inparm.getCount() <= 0)
					continue;
				if (inparm.getErrCode() < 0)
					continue;
				String takeMedNo = inparm.getValue("TAKEMED_NO", 0);
				if (takeMedNo.equals(""))
					continue;
				if (orderNoMap.get(takeMedNo) == null)
					orderNoMap.put(takeMedNo, takeMedNo);
			}
		return orderNoMap;
	}

	/**
	 * ȡ��PAT table��ѡ�е�CASE_NO��Ϊ����SQLƴWHERE��
	 * 
	 * @return
	 */
	public String getCaseNos() {
		mainTable.acceptText();
		TParm parm = this.mainTable.getParmValue();
		StringBuffer caseNos = new StringBuffer();
		if (parm.getCount() < 1)
			return "''";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("PRINT_FLG", i)))
				caseNos.append("'").append(parm.getValue("CASE_NO", i)).append("',");

		if (caseNos.length() < 1) {
			return "''";
		} else {
			caseNos.deleteCharAt(caseNos.length() - 1);
			return caseNos.toString();
		}
	}

	/**
	 * ȡ��ѡ��ļ��ͷ���
	 * 
	 * @return String
	 */
	private String getDoseTypeByWhere() {
		String getDoseType = "";
		List list = new ArrayList();
		if (this.typeO.isSelected()) {// �ڷ�
			list.add("O");
		}
		if (this.typeE.isSelected()) {// ����
			list.add("E");
		}
		if (this.typeI.isSelected()) {// ���
			list.add("I");
		}
		if (this.typeF.isSelected()) {// ���
			list.add("F");
		}
		if (list == null || list.size() == 0) {
			return "";
		} else {
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1);
		}
		return getDoseType;
	}

	/**
	 * ȡ��ѡ��ļ��ͷ������ڱ�����ʾ
	 * 
	 * @return String
	 */
	private String getDoseTypeText() {
		String getDoseType = "";
		if ("N".equals(this.getValueString("typeO")) || "N".equals(this.getValueString("typeE"))
				|| "N".equals(this.getValueString("typeI")) || "N".equals(this.getValueString("typeF"))) {
			List list = new ArrayList();
			if ("Y".equals(this.getValueString("typeO"))) {
				list.add("�ڷ�");
			}
			if ("Y".equals(this.getValueString("typeE"))) {
				list.add("����");
			}
			if ("Y".equals(this.getValueString("typeI"))) {
				list.add("���");
			}
			if ("Y".equals(this.getValueString("typeF"))) {
				list.add("���");
			}

			if (list == null || list.size() == 0) {
				return "";
			} else {
				for (int i = 0; i < list.size(); i++) {
					getDoseType = getDoseType + list.get(i) + " ,";
				}
				getDoseType = "�÷�: " + getDoseType.substring(0, getDoseType.length() - 1);
			}
		} else {
			getDoseType = "�÷�: ȫ��";
		}
		return getDoseType;

	}

	/**
	 * �޸�ִ�����ں�ʱ�䣨Ϊ������ҽ����
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int (NS_CHECK_DATE_DAY;NS_CHECK_DATE_TIME)
	 */
	public void onDateTime(TTableNode node) {
		mainTable.acceptText();
		int col = node.getColumn();
		String colName = getColName(col, mainTable);
		// int row = mainTable.getSelectedRow();
		int row = node.getRow();// wanglong modify 20150128
		TParm rowParm = mainTable.getParmValue().getRow(row);
		Map map = new HashMap();
		if ("NS_CHECK_DATE".equals(colName)) {
			Timestamp temp = (Timestamp) node.getValue();
			Timestamp date = temp;
			node.setValue(date);
			if (isLinkOrder(rowParm)) {
				Timestamp tempT = (Timestamp) node.getValue();
				String linkstr = rowParm.getValue("CASE_NO") + rowParm.getValue("RX_KIND")
						+ rowParm.getValue("LINK_NO");
				map.put(linkstr, linkstr);
				int count = mainTable.getParmValue().getCount();
				for (int i = 0; i < count; i++) {
					TParm parm = mainTable.getParmValue().getRow(i);
					String str = parm.getValue("CASE_NO") + parm.getValue("RX_KIND") + parm.getValue("LINK_NO");
					if (map.get(str) != null) {
						mainTable.setItem(i, "NS_CHECK_DATE", tempT);
					}
				}
			}
		}
		if ("NS_CHECK_DATE_TIME".equals(colName)) {
			String temp = (String) node.getValue();
			if (temp.length() > 5 || temp.length() < 4) { // chenxi modify
															// 20130325 ���ʱ��ʡȥð��
				this.messageBox("ʱ�䳤�ȴ���");
				node.setValue(node.getOldValue());
				return;
			} else {
				String execDate = "" + mainTable.getParmValue().getValue("NS_CHECK_DATE", row);
				String execTime = "" + temp;
				if (temp.length() == 5) {
					Timestamp checkDateTime = StringTool
							.getTimestamp(execDate.substring(0, 10).replaceAll("/", "").replaceAll("-", "") + " "
									+ execTime.substring(0, 5) + ":00", "yyyyMMdd HH:mm:ss");
					if (checkDateTime == null) {
						this.messageBox("ʱ���ʽ����");
						node.setValue(node.getOldValue());
						return;
					}
					Pattern pattern = Pattern.compile("((0[0-9])|(1[0-9])|(2[0-3])):([0-5][0-9])");
					if (!pattern.matcher(execTime).matches()) {
						this.messageBox("ʱ����ֵ����");
						node.setValue(node.getOldValue());
						return;
					}
				} else if (temp.length() == 4) {
					Timestamp checkDateTime = StringTool
							.getTimestamp(execDate.substring(0, 10).replaceAll("/", "").replaceAll("-", "") + " "
									+ execTime.substring(0, 4) + "00", "yyyyMMdd HHmmss");
					if (checkDateTime == null) {
						this.messageBox("ʱ���ʽ����");
						node.setValue(node.getOldValue());
						return;
					}
					Pattern pattern = Pattern.compile("((0[0-9])|(1[0-9])|(2[0-3]))([0-5][0-9])");
					if (!pattern.matcher(execTime).matches()) {
						this.messageBox("ʱ����ֵ����");
						node.setValue(node.getOldValue());
						return;
					}
				}
			}
			String date = temp;
			node.setValue(date);
			if (isLinkOrder(rowParm)) {
				String tempT = (String) node.getValue();
				String linkstr = rowParm.getValue("CASE_NO") + rowParm.getValue("RX_KIND")
						+ rowParm.getValue("LINK_NO");
				map.put(linkstr, linkstr);
				int count = mainTable.getParmValue().getCount();
				for (int i = 0; i < count; i++) {
					TParm parm = mainTable.getParmValue().getRow(i);
					String str = parm.getValue("CASE_NO") + parm.getValue("RX_KIND") + parm.getValue("LINK_NO");
					if (map.get(str) != null) {
						mainTable.setItem(i, "NS_CHECK_DATE_TIME", tempT);
					}
				}
			}
		}

	}

	/**
	 * �Ƿ�������ҽ��
	 * 
	 * @param linkOrder
	 *            TParm
	 * @return boolean
	 */
	public boolean isLinkOrder(TParm linkOrder) {
		boolean falg = false;
		if (linkOrder.getInt("LINK_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * ���������õ�����
	 * 
	 * @param i
	 *            int
	 * @return String
	 */
	public String getColName(int i, TTable tbl) {
		String colName = "";
		colName = tbl.getParmMap(i);
		return colName;
	}

	/**
	 * ��ѯ���� 1)��ѯ 2)�������ݵ�ʱ��ı���صĿؼ�״̬
	 */
	public void onQuery() {
		// ��ʼ����ǰtable����������
		if (initTable())
			changeStatus();
		if (checkYES.isSelected()) {
			exeAll.setSelected(true);
		} else if (checkNO.isSelected()) {
			exeAll.setSelected(false);
		}
		onPrtAll();
	}

	/**
	 * ����ѯ������֮��ı�ĳЩ�ؼ���״̬
	 */
	public void changeStatus() {
		exeAll.setEnabled(true);
	}

	/**
	 * ��ʼ��Table
	 * 
	 * @return boolean
	 */
	public boolean initTable() {
		TParm selParm = new TParm();
		selParm = getQueryParm();
		TParm query = InwForOdiTool.getInstance().selectOdiOrder(selParm);
		// filterDrugByDoseCode(query);
		if (query.getCount() <= 0) {
			mainTable.setParmValue(query);
			return false;
		}
		TParm parm = new TParm();
		int count = 0;
		map = new HashMap();
		TParm tranParm = new TParm();
		for (int i = 0; i < query.getCount(); i++) {
			TParm tparm = new TParm(TJDODBTool.getInstance()
					.select("SELECT * FROM SYS_DEPT WHERE DEPT_CODE ='" + query.getValue("EXEC_DEPT_CODE", i) + "'"));

			tranParm.addData("EXEC_DEPT_CODE", tparm.getValue("DEPT_CHN_DESC", 0));
		}

		for (int i = 0; i < query.getCount(); i++) {

			mainTable.removeRowColor(i);// duzhw update 20131126 ��ʼ��ȡ�������ɫ

			// �õ�ϵͳ��ʱ����ͣ��ʱ��Ƚ�->�Ƿ���ʾͣ��ʱ�䣬yanjing 20131118
			String sysDate = SystemTool.getInstance().getDate().toString().substring(0, 10).replace("-", "")
					.replace("/", "");
			// parm.addData("EXEC_DEPT_CODE",tranParm.getValue("EXEC_DEPT_CODE",i));
			if (!query.getValue("DC_DATE", i).equals(null) && !"".equals(query.getValue("DC_DATE", i))) {
				String dc_date = query.getValue("DC_DATE", i).toString().substring(0, 10).replace("-", "").replace("/",
						"");
				if (dc_date.compareTo(sysDate) > 0) {
					query.setData("DC_DATE", i, null);
				}
			}
			query.addData("PRINT_FLG", false);
			// ����ִ�б��һ��
			query.addData("EXE_FLG", checkYES.isSelected());
			Timestamp exeDate = (Timestamp) query.getData("NS_CHECK_DATE", i);
			Timestamp exeDcDate = (Timestamp) query.getData("DC_NS_CHECK_DATE", i);
			if (i == 0) {
				query.setData("EXEC_DEPT_CODE", null);
			}

			query.addData("EXEC_DEPT_CODE", tranParm.getValue("EXEC_DEPT_CODE", i));
			if (exeDate != null) {
				String day = StringTool.getString(exeDate, "yyyy/MM/dd");
				String time = StringTool.getString(exeDate, "HH:mm:ss").substring(0, 5);
				query.addData("NS_CHECK_DATE", exeDate);
				query.addData("NS_CHECK_DATE_TIME", time);
			} else {
				query.addData("NS_CHECK_DATE", null);
				query.addData("NS_CHECK_DATE_TIME", null);
			}
			if (exeDcDate != null) {
				String day = StringTool.getString(exeDcDate, "yyyy/MM/dd");
				String time = StringTool.getString(exeDcDate, "HH:mm:ss");
				query.addData("DC_NS_CHECK_DATE_DAY", day);
				query.addData("DC_NS_CHECK_DATE_TIME", time);
			} else {
				query.addData("DC_NS_CHECK_DATE_DAY", null);
				query.addData("DC_NS_CHECK_DATE_TIME", null);
			}
			// ҩƷ���˾���ҩ SHIBL 20120821 ADD
			// ҩƷ
			if (this.ord2PHA.isSelected()) {
				// ����
				if (!this.typeC.isSelected()
						&& (query.getValue("CTRL_FLG", i).equals("N") || query.getValue("CTRL_FLG", i).equals(""))) {
					// ��ȡֵ���ODI_ORDER��ֵ
					if (!query.getValue("TAKEMED_ORG", i).equals("") && this.checkYES.isSelected()) {
						if (this.medSta.isSelected() && (query.getValue("TAKEMED_ORG", i).equals("1")))
							parm.addRowData(query, i);
						else if (this.medOrg.isSelected() && (query.getValue("TAKEMED_ORG", i).equals("2")))
							parm.addRowData(query, i);
					} else {
						// Ĭ��
						query.setData("TAKEMED_ORG", i, getTakeMedOrg(query.getRow(i)));
						parm.addRowData(query, i);
					}
				}
				// �Ǿ���
				else if (this.typeC.isSelected()) {
					if (!query.getValue("TAKEMED_ORG", i).equals("") && this.checkYES.isSelected()) {
						if (this.medSta.isSelected() && (query.getValue("TAKEMED_ORG", i).equals("1")))
							parm.addRowData(query, i);
						else if (this.medOrg.isSelected() && (query.getValue("TAKEMED_ORG", i).equals("2")))
							parm.addRowData(query, i);
					} else {
						// Ĭ��
						query.setData("TAKEMED_ORG", i, getTakeMedOrg(query.getRow(i)));
						parm.addRowData(query, i);
					}
				}
				this.mainTable.setLockCell(count, "TAKEMED_ORG", false);
				count++;
			} // ��ҩƷ�����ȫ��
			else {
				if (query.getValue("CAT1_TYPE", i).equals("PHA")) {
					query.setData("TAKEMED_ORG", i, getTakeMedOrg(query.getRow(i)));
				}
				if (!query.getValue("CAT1_TYPE", i).equals("PHA")) {
					this.mainTable.setLockCell(count, "TAKEMED_ORG", true);
				} else {
					this.mainTable.setLockCell(count, "TAKEMED_ORG", false);
				}
				parm.addRowData(query, i);
				count++;
			}
			// ȡҩ��������-CAT1_TYPE��Ϊ"PHA"���ȡҩ�����ÿ� duzhw 20140106 start
			if (!"PHA".equals(query.getValue("CAT1_TYPE", i))) {
				parm.setData("TAKEMED_ORG", i, "");
			}
			// ȡҩ��������-CAT1_TYPE��Ϊ"PHA"���ȡҩ�����ÿ� duzhw 20140106 end

		}

		parm.setCount(count);
		mainTable.setParmValue(parm);
		setColor();
		return true;
	}

	/**
	 * �õ�ȡҩ��Դע��
	 * 
	 * @param parm
	 * @return
	 */
	public String getTakeMedOrg(TParm parm) {
		// �жϿ�� 20121115 shibl add
		// �����ʱ/���������⴦������
		// ϵͳ�жϸò������ڲ������и�ҩƷ�㹻�Ŀ��,��Ӳ������ܹ�ȡҩ(SET TAKEMED_ORG= ��1��),
		// �����㹻��ҩƷ������סԺҩ����ҩ(SET TAKEMED_ORG= ��2��)
		String MedOrg = "2";
		if (parm.getValue("CAT1_TYPE").equals("PHA") && (Operator.getSpcFlg().equals("Y"))) {// 20130423 shibl add
																								// ��ҩ����
			TParm inparm = new TParm();
			inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			inparm.setData("REGION_CODE", Operator.getRegion());
			inparm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
			// modify by wanglong 20130115
			TParm result = TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "getIndQty", inparm);
			if (result.getErrCode() < 0 && map.get("errorcode") == null) {
				this.messageBox(result.getErrText());
				map.put("errorcode", result.getErrCode());
				return MedOrg;
			}
			// System.out.println(rDto.getOrderCode()+"====dtoReturnList.get(0).getQty()======"+rDto.getQty());
			if (result.getDouble("QTY") >= parm.getDouble("DOSAGE_QTY") && parm.getValue("RX_KIND").equals("ST")// ֻ�����ʱҽ��
					&& StringUtil.isNullString(parm.getValue("LINK_NO"))// ���ҽ�����ڲ���ȡҩ
																		// //add
																		// by
																		// wanglong
																		// 20130619
			) {
				// modify end
				// if (InwForOdiTool.getInstance().inspectIndStock(
				// parm.getValue("STATION_CODE"), parm.getValue("ORDER_CODE"),
				// parm.getDouble("DOSAGE_QTY"))
				// && (parm.getValue("RX_KIND").equals("ST") || parm.getValue(
				// "RX_KIND").equals("UD"))) {
				MedOrg = "1";
			} else {
				MedOrg = "2";
			}
		}
		return MedOrg;
	}

	/**
	 * ��ý����ϵ����в�ѯ����
	 * 
	 * @return TParm
	 */
	public TParm getQueryParm() {
		// ��ý����ϵĲ���
		TParm result = new TParm();
		// ===============pangben modify 20110512 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			result.setData("REGION_CODE", Operator.getRegion());
		// ===============pangben modify 20110512 stop

		// ȡ��ҽ�����
		if (ord1All.isSelected()) {
			// ����
		} else if (ord1ST.isSelected()) {
			// ��ʱ
			// result.setData("RX_KIND", "ST");
			// ��ʱѡ�к�ѡ�������Ժ��ҩ modify by guoy 20151124
			if (includeOutMed.isSelected()) {
				// ������Ժ��ҩ
				result.setData("ST_KIND", "ST");
				result.setData("DS_KIND", "DS");
			} else {
				result.setData("RX_KIND", "ST");
			}
		} else if (ord1UD.isSelected()) {
			// ����
			result.setData("RX_KIND", "UD");
		} else if (ord1DS.isSelected()) {
			// ��Ժ��ҩ
			result.setData("RX_KIND", "DS");
		} else if (ord1IG.isSelected()) {
			// סԺ��ҩ
			result.setData("RX_KIND", "IG");
		}

		// ҽ������
		if (ord2All.isSelected()) {
			// ����
		} else if (ord2PHA.isSelected()) {
			// ҩ��
			result.setData("CAT1_TYPE_PHA", "PHA");
			List<String> doseType = new ArrayList<String>();
			if (typeO.isSelected()) { // �ڷ�
				doseType.add("O");
			}
			if (typeE.isSelected()) { // ����
				doseType.add("E");
			}
			if (typeI.isSelected()) { // ���
				doseType.add("I");
			}
			if (typeF.isSelected()) { // ���
				doseType.add("F");
			}
			int parmCount = doseType.size();
			if (parmCount == 0) {
				result.setData("DOSE_TYPES1", "");
				result.setData("DOSE_TYPE1", "Y");
			} else if (parmCount == 1) {
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPE1", "Y");
			} else if (parmCount == 2) {
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPES2", doseType.get(1));
				result.setData("DOSE_TYPE2", "Y");
			} else if (parmCount == 3) {
				result.setData("DOSE_TYPES3", "Y");
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPES2", doseType.get(1));
				result.setData("DOSE_TYPE3", doseType.get(2));
			} else if (parmCount == 4) {
				result.setData("DOSE_TYPES1", doseType.get(0));
				result.setData("DOSE_TYPES2", doseType.get(1));
				result.setData("DOSE_TYPES3", doseType.get(2));
				result.setData("DOSE_TYPES4", doseType.get(3));
				result.setData("DOSE_TYPE4", "Y");
			}
			// // �ж��Ƿ�ѡ���龫
			// if (this.typeC.isSelected()) {
			// result.setData("CTRLDRUGCLASS_CODE", getPhaCtrlCode());
			// }
		} else if (ord2PL.isSelected()) {
			// ����(������)
			result.setData("CAT1_TYPE_TRT", "PHA");
		} else if (((TRadioButton) this.getComponent("ord2ENT")).isSelected()) {
			// ����
			result.setData("CAT1_TYPE_ENT", "PHA");
		}
		// ���״̬
		if (checkAll.isSelected()) {
			// ����
			if (firstDateRadio.isSelected()) {// add by guoy 20151119
				// result.setData("CHECKTYPE_YES", "Y");
				String fromDate = StringTool.getString((Timestamp) from_Date.getValue(), "yyyyMMdd");
				String fromTime = (String) from_Time.getValue();
				String fromCheckDate = fromDate + fromTime.substring(0, 2) + fromTime.substring(3);
				String toDate = StringTool.getString((Timestamp) to_Date.getValue(), "yyyyMMdd");
				String toTime = (String) to_Time.getValue();
				String toCheckDate = toDate + toTime.substring(0, 2) + toTime.substring(3);
				result.setData("fromCheckDate", fromCheckDate);
				result.setData("toCheckDate", toCheckDate);
				result.setData("NS_CHECK_DATE", fromCheckDate);
			} else {// add by guoy 20151119
				// result.setData("CHECKTYPE_YES", "Y");
				result.setData("fromOrderDate", (Timestamp) this.getValue("ORDER_START_DATE"));
				result.setData("toOrderDate", (Timestamp) this.getValue("ORDER_END_DATE"));
				result.setData("ORDER_DATE", (Timestamp) this.getValue("ORDER_START_DATE"));
			}
		} else if (checkYES.isSelected()) {
			// �����
			if (firstDateRadio.isSelected()) {// add by wanglong 20130626
				result.setData("CHECKTYPE_YES", "Y");
				String fromDate = StringTool.getString((Timestamp) from_Date.getValue(), "yyyyMMdd");
				String fromTime = (String) from_Time.getValue();
				String fromCheckDate = fromDate + fromTime.substring(0, 2) + fromTime.substring(3);
				String toDate = StringTool.getString((Timestamp) to_Date.getValue(), "yyyyMMdd");
				String toTime = (String) to_Time.getValue();
				String toCheckDate = toDate + toTime.substring(0, 2) + toTime.substring(3);
				result.setData("fromCheckDate", fromCheckDate);
				result.setData("toCheckDate", toCheckDate);
				result.setData("NS_CHECK_DATE", fromCheckDate);
			} else {// add by wanglong 20130626
				result.setData("CHECKTYPE_YES", "Y");
				result.setData("fromOrderDate", (Timestamp) this.getValue("ORDER_START_DATE"));
				result.setData("toOrderDate", (Timestamp) this.getValue("ORDER_END_DATE"));
				result.setData("ORDER_DATE", (Timestamp) this.getValue("ORDER_START_DATE"));
			}
		} else if (checkNO.isSelected()) {
			// δ���
			result.setData("CHECKTYPE_NO", "Y");
		}
		if (isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			result.setData("DEPT_CODE", ((TParm) this.getParameter()).getValue("INW", "DEPT_CODE"));
			result.setData("VS_DR_CODE", ((TParm) this.getParameter()).getValue("INW", "VS_DR_CODE"));
			result.setData("OPE_YES", "Y");// ��ʾ����ҽ��
		} else {
			result.setData("OPE_NO", "Y");// ���˵�����ҽ��
		}
		if (setQueryParm(result)) {
			clearTop();
			return result;
		}
		// ���翴���
		if (caseNo != null && !"".equals(caseNo.trim()) && !"null".equals(caseNo)) {

			result.setData("CASE_NO", caseNo);
		} else {
			// Ϊ�յ�ʱ��
		}
		// ���벡����
		if (stationCode != null && !"".equals(stationCode.trim()) && !"null".equals(stationCode)) {
			result.setData("STATION_CODE", stationCode);
		} else {
			// Ϊ�յ�ʱ��
		}
		// �������
		if (!this.getValueString("INW_DEPT_CODE").equals("")) {
			result.setData("DEPT_CODE", getValueString("INW_DEPT_CODE"));
		} else {
			// Ϊ�յ�ʱ��
		}
		// ���뾭��ҽʦ
		if (!this.getValueString("INW_VC_CODE").equals("")) {
			result.setData("VS_DR_CODE", getValueString("INW_VC_CODE"));
		} else {
			// Ϊ�յ�ʱ��
		}
		// System.out.println("-----------------"+result);
		return result;
	}

	/**
	 * �жϹ���ҩƷ�ȼ�
	 * 
	 * @param orderCode
	 * @return
	 */
	private boolean getPhaCtrlCode(String orderCode) {
		boolean CtrlStr = false;
		String sql = " SELECT A.CTRLDRUGCLASS_CODE,B.CTRL_FLG FROM PHA_BASE A,SYS_CTRLDRUGCLASS B"
				+ " WHERE A.CTRLDRUGCLASS_CODE=B.CTRLDRUGCLASS_CODE AND ORDER_CODE = '" + orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0)
			CtrlStr = parm.getBoolean("CTRL_FLG", 0);
		return CtrlStr;
	}

	public boolean setQueryParm(TParm parm) {
		if (!saveFlg)
			return false;
		// shibl 2012-03-05
		if (getValueString("INW_STATION_CODE").length() != 0)
			parm.setData("STATION_CODE", getValue("INW_STATION_CODE"));
		if (getValueString("IPD_NO").length() != 0)
			parm.setData("IPD_NO", getValue("IPD_NO"));
		if (getValueString("MR_NO").length() != 0)
			parm.setData("MR_NO", getValue("MR_NO"));
		if (getValueString("INW_VC_CODE").length() != 0)
			parm.setData("VS_DR_CODE", getValue("INW_VC_CODE"));
		// shibl 2012-03-05
		if (getValueString("INW_DEPT_CODE").length() != 0)
			parm.setData("DEPT_CODE", getValue("INW_DEPT_CODE"));
		parm.setData("DS_DATE_FLG", false);
		return true;
	}

	public void clearTop() {
		if (getValueString("IPD_NO").length() != 0)
			return;
		setValue("PAT_NAME", "");
		setValue("SEX", "");
		setValue("SERVICE_LEVELIN", "");
		setValue("WEIGHT", "");
		setValue("BED_NO", "");
	}

	/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 */
	public void showPopMenu(String tableName) {
		// �õ�table����
		TTable table = (TTable) this.getComponent(tableName);
		// ���ѡ���е�TParm
		TParm action = mainTable.getParmValue().getRow(mainTable.getSelectedRow());
		if ("LIS".equals(action.getValue("CAT1_TYPE")) || "RIS".equals(action.getValue("CAT1_TYPE"))) {
			table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
			return;
		} else {
			table.setPopupMenuSyntax("");
		}
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		// ���ѡ���е�TParm
		TParm action = mainTable.getParmValue().getRow(mainTable.getSelectedRow());
		int groupNo = action.getInt("ORDERSET_GROUP_NO");
		String orderCode = action.getValue("ORDER_CODE");
		String caseNo = action.getValue("CASE_NO");
		TParm parm = getOrderSetDetails(caseNo, groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(String caseNo, int groupNo, String orderSetCode) {
		TParm result = new TParm();
		if (groupNo < 0) {
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			return result;
		}
		// ===========pangben modify 20110516 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		String selSetOrder = "SELECT * FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "' AND ORDERSET_GROUP_NO<>0" + region;
		// ===========pangben modify 20110516 stop
		TParm parm = new TParm(TJDODBTool.getInstance().select(selSetOrder));
		int count = parm.getCount();
		if (count < 0) {
			return result;
		}
		String tempCode;
		int tempNo;
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo && !parm.getBoolean("SETMAIN_FLG", i)) {
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm ownPriceParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='" + parm.getValue("ORDER_CODE", i) + "'"));
				// �����ܼ۸�
				double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0) * parm.getDouble("MEDI_QTY", i);
				result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE", 0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
				result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}
		}
		return result;
	}

	// ȫ��ִ��
	public void onExe() {

		boolean nowFlag = exeAll.isSelected();
		// ��ȫ��ִ�е�ʱ������һ��ʱ��
		Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
		String optName = Operator.getName();

		// �õ�����
		int ordCount = mainTable.getRowCount();
		for (int i = 0; i < ordCount; i++) {
			// ѭ��ȡ���Թ�
			if (nowFlag) {

				selection(i, optName, chackTime);
			} else { // ȡ�����

				unselection(i);
			}
		}

	}

	/**
	 * ѡ��
	 * 
	 * @param i
	 *            int
	 * @param nowFlag
	 *            boolean
	 * @param optName
	 *            String
	 * @param chackTime
	 *            Timestamp
	 */
	private void selection(int i, String optName, Timestamp chackTime) {
		// mainTable.setValueAt(true, i, 13);
		this.mainTable.setItem(i, "EXE_FLG", "Y");
		TParm parm = mainTable.getParmValue();
		if (checkNO.isSelected() && parm.getValue("NS_CHECK_CODE", i).length() == 0) {
			mainTable.setItem(i, "NS_CHECK_CODE", optName);
			mainTable.setItem(i, "NS_CHECK_DATE", StringTool.getString(chackTime, "yyyy/MM/dd"));
			mainTable.setItem(i, "NS_CHECK_DATE_TIME", StringTool.getString(chackTime, "HH:mm:ss").substring(0, 5));
			// mainTable.setValueAt(chackTime,
			// i, 16);
		}
		if (checkYES.isSelected() || checkAll.isSelected()) {
			mainTable.setItem(i, "NS_CHECK_CODE", optName);
			mainTable.setItem(i, "NS_CHECK_DATE", StringTool.getString(chackTime, "yyyy/MM/dd"));
			mainTable.setItem(i, "NS_CHECK_DATE_TIME", StringTool.getString(chackTime, "HH:mm:ss").substring(0, 5));
			// mainTable.setValueAt(chackTime,
			// i, 16);
		}
		// DCʱ�䣬�����Ƿ���DC
		Timestamp dcDate = (Timestamp) mainTable.getValueAt(i, 16); // ��λ�õ���14��16
		if (dcDate != null && !"".equals(dcDate)) {
			mainTable.setItem(i, "DC_NS_CHECK_CODE", optName);
			mainTable.setItem(i, "DC_NS_CHECK_DATE_DAY", StringTool.getString(chackTime, "yyyy/MM/dd"));
			mainTable.setItem(i, "DC_NS_CHECK_DATE_TIME", StringTool.getString(chackTime, "HH:mm:ss"));

		}
	}

	/**
	 * ȡ��ѡ��
	 * 
	 * @param i
	 *            int
	 * @param nowFlag
	 *            boolean
	 * @param optName
	 *            String
	 * @param chackTime
	 *            Timestamp
	 */
	private void unselection(int i) {
		// mainTable.setValueAt(false, i, 13);
		this.mainTable.setItem(i, "EXE_FLG", "N");
		TParm parm = mainTable.getParmValue();
		// SHIBL
		// if (checkNO.isSelected()
		// && parm.getValue("NS_CHECK_CODE", name).length() == 0) {
		// this.messageBox_("213");
		// mainTable.setItem(i, "NS_CHECK_CODE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE_TIME", "");
		// // mainTable.setValueAt("", i, 16);
		// }
		// if (checkYES.isSelected() || checkAll.isSelected()) {
		// mainTable.setItem(i, "NS_CHECK_CODE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE", "");
		// mainTable.setItem(i, "NS_CHECK_DATE_TIME", "");
		// // mainTable.setValueAt("", i, 16);
		// }
		// DCʱ�䣬�����Ƿ���DC
		Timestamp dcDate = (Timestamp) mainTable.getValueAt(i, 16); // ��λ�õ���14��16
		if (dcDate != null && !"".equals(dcDate)) {
			mainTable.setItem(i, "DC_NS_CHECK_CODE", "");
			mainTable.setItem(i, "DC_NS_CHECK_DATE_DAY", "");
			mainTable.setItem(i, "DC_NS_CHECK_DATE_TIME", "");

		} else {
			mainTable.setItem(i, "NS_CHECK_CODE", "");
			mainTable.setItem(i, "NS_CHECK_DATE", "");
			mainTable.setItem(i, "NS_CHECK_DATE_TIME", "");
		}

	}

	/**
	 * table�ϵ�checkBoxע�����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {
		// ��ȫ��ִ�е�ʱ������һ��ʱ��
		Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
		// ��õ����table����
		TTable table = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		table.acceptText();
		TParm tblParm = table.getParmValue();

		// ���ѡ�е���/��
		int col = table.getSelectedColumn();
		int row = table.getSelectedRow();
		// ���table�ϵ�����
		int rowcount = table.getRowCount();
		// ���ѡ�е��ǵ�12�оͼ���ִ�ж���--ִ��
		String columnName = table.getParmMap(col);
		if (columnName.equals("EXE_FLG")) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			// ��ѡʱ
			if (exeFlg) {
				// ��ѡ�ж���
				selection(row, Operator.getName(), chackTime);
				// �õ�ѡ�����ݵ�ҽ�����ͣ����������ǲ�ͬҽ�����͸��Լ������Ӻţ�����Ϊ�˱����ڡ�ȫ������״���»�����ظ����Ӻ������
				String rxKind = (String) tblParm.getValue("RX_KIND", row);
				// -----------------------��������ҽ��start------------------------
				// �ҵ���ͬ�����Ӻ�
				String linkNo = (String) table.getValueAt(row, 5); // ��λ�õ���4��5
				String orderNo = (String) tblParm.getValue("ORDER_NO", row);
				String linkMain = (String) tblParm.getValue("LINKMAIN_FLG", row);
				if (TCM_Transform.getInt(linkNo) > 0) {
					for (int i = 0; i < rowcount; i++) {
						// ���˵�ǰ������к������
						if (i != row && linkNo.equals((String) table.getValueAt(i, 5)) // ��λ�õ���4��5
								&& rxKind.equals((String) tblParm.getValue("RX_KIND", i))
								&& orderNo.equals((String) tblParm.getValue("ORDER_NO", i))) {

							selection(i, Operator.getName(), chackTime);
						}
					}
				}
				// -------------------------�����в�ҩstart-----------------------
				if ("IG".equals(rxKind)) {
					// �в�ҩ��ʱ����RX_NO(����ǩ)
					String rxNo = (String) tblParm.getValue("RX_NO", row);
					for (int i = 0; i < rowcount; i++) {
						// ��¼������RX_NO
						String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
						// ���˵�ǰ������к������
						if (rxNoTemp.equals(rxNo)) {
							selection(i, Operator.getName(), chackTime);
						}
					}
				}
			} else { // ȡ��ʱ
				// ��ѡ�ж���
				unselection(row);
				// �õ�ѡ�����ݵ�ҽ�����ͣ����������ǲ�ͬҽ�����͸��Լ������Ӻţ�����Ϊ�˱����ڡ�ȫ������״���»�����ظ����Ӻ������
				String rxKind = (String) tblParm.getValue("RX_KIND", row);
				// -----------------------��������ҽ��start----------------------------
				// �ҵ���ͬ�����Ӻ�
				String linkNo = (String) table.getValueAt(row, 5); // ��λ�õ���4��5
				String orderNo = (String) tblParm.getValue("ORDER_NO", row);
				String linkMain = (String) tblParm.getValue("LINKMAIN_FLG", row);
				if (TCM_Transform.getInt(linkNo) > 0) {
					for (int i = 0; i < rowcount; i++) {
						// ���˵�ǰ������к������
						if (i != row && linkNo.equals((String) table.getValueAt(i, 5)) // ��λ�õ���4��5
								&& rxKind.equals((String) tblParm.getValue("RX_KIND", i))
								&& orderNo.equals((String) tblParm.getValue("ORDER_NO", i))) {

							unselection(i);
						}
					}
				}
				// -------------------------�����в�ҩstart-----------------------
				if ("IG".equals(rxKind)) {
					// �в�ҩ��ʱ����RX_NO(����ǩ)
					String rxNo = (String) tblParm.getValue("RX_NO", row);
					for (int i = 0; i < rowcount; i++) {
						// ��¼������RX_NO
						String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
						// ���˵�ǰ������к������
						if (rxNoTemp.equals(rxNo)) {
							unselection(i);
						}
					}
				}
			}
			// -----------------------end----------------------------------------
		}
	}

	/**
	 * ��ն���
	 */
	public void onClear() {
		// ���
		if (!isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			this.clearValue("IPD_NO;patName;AGE;SUMFEE;BHFEE");
		}
		firstDateRadio.setSelected(true); // ʱ����� add by wanglong 20130626
		if (isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			this.callFunction("UI|ord1ST|setSelected", true);// ѡ�С���ʱ����ѡ��
		} else {
			ord1All.setSelected(true); // ҽ�����
		}
		ord2All.setSelected(true); // ҽ������
		checkNO.setSelected(true); // ���״̬

		typeO.setEnabled(false);
		typeE.setEnabled(false);
		typeI.setEnabled(false);
		typeF.setEnabled(false);
		typeC.setEnabled(false);

		onRemoveTbl();
		if (!isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			clearQueryCondition();
		}
	}

	public void clearQueryCondition() {
		setValue("INW_DEPT_CODE", "");
		setValue("BED_NO", "");
		setValue("IPD_NO", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		setValue("SEX", "");
		setValue("SERVICE_LEVELIN", "");
		setValue("WEIGHT", "");
		setValue("INW_VC_CODE", "");
		setValue("ADM_DATE", "");
		setValue("TOTAL_AMT", "");
		setValue("PAY_INS", "");
		setValue("YJJ_PRICE", "");
		setValue("GREED_PRICE", "");
		setValue("YJYE_PRICE", "");
		setValue("PRESON_NUM", "");
	}

	/**
	 * ���涯��
	 */
	public boolean onSave() {
		// ������֮�����ѡ��
		exeAll.setSelected(false);
		printAll.setSelected(false);

		// �Ƿ������ݱ�ѡ��
		if (checkConsistSel()) {
			this.messageBox("����Ҫ�������ݣ�");
			return false;
		}

		// add by yangjj 20151211 ȡ�����Ȩ���ж�
		if (!checkNO.isSelected()) {
			String sql1 = " SELECT " + " ROLE_CODE,GROUP_CODE,CODE " + " FROM " + " SYS_ROLE_POPEDOM " + " WHERE "
					+ " GROUP_CODE = 'INW01' AND CODE = 'inwCancelCheck' AND ROLE_CODE='" + Operator.getRole() + "'";
			String sql2 = " SELECT " + " USER_ID,AUTH_CODE,GROUP_CODE " + " FROM " + " SYS_USER_AUTH " + " WHERE "
					+ " USER_ID = '" + Operator.getID() + "' AND AUTH_CODE = 'inwCancelCheck' AND GROUP_CODE = 'INW01'";

			TParm r1 = new TParm(TJDODBTool.getInstance().select(sql1));
			TParm r2 = new TParm(TJDODBTool.getInstance().select(sql2));

			if ((r2.getCount() < 1) && (r1.getCount() < 1)) {
				this.messageBox("��û��ȡ�����ҽ����Ȩ�ޣ�");
				return false;
			}

		}

		// �����ж�
		if (!checkPW()) {
			return false;
		}
		// ���̽���ֵ�ĸı�
		mainTable.acceptText();
		// ���ñ���
		if (checkNO.isSelected()) {
			if (!onCheck()) {
				this.messageBox("E0001");
				// ��ѯ
				onQuery();
				return false;
			}
			if (Operator.getSpcFlg().equals("Y"))
				this.onSendGYPha("A", "1");
		} // �����˱�ѡ��˵������ʱ��--ȡ����ˣ�����Ҫ��֤�Ƿ���ִ�е�
		else {
			if (!onUndoCk()) {
				this.messageBox("E0001");
				onQuery();
				return false;
			}
			if (Operator.getSpcFlg().equals("Y"))
				this.onSendGYPha("D", "1");
		}
		// ����ɹ�֮��ɾ��LED����Ϣ
		TParm caseNoParm = new TParm();
		caseNoParm.setData("CASE_NO", caseNo);
		if (this.getLedui() != null) {
			this.getLedui().removeMessage(caseNoParm);
		}

		this.messageBox("P0001");
		// �������ִ��һ�߲�ѯ
		onQuery();

		return true;
	}

	/**
	 * ����Ƿ������ݱ�ѡ��
	 * 
	 * @return boolean
	 */
	private boolean checkConsistSel() {
		int count = mainTable.getRowCount();
		if (count == 0)
			return true;
		for (int i = 0; i < count; i++) {
			if (checkNO.isSelected() && TypeTool.getBoolean(mainTable.getValueAt(i, 2))) // ��λ�õ���17��2
				return false;
			else if (checkYES.isSelected() && !TypeTool.getBoolean(mainTable.getValueAt(i, 2))) // ��λ�õ���17��2
				return false;
		}
		return true;
	}

	public boolean onUndoCk() {
		if (checkPHACheck())
			return false;
		// �õ���������--չ���˵�caseNo
		TParm undoData = new TParm();
		TParm tablValue = mainTable.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = mainTable.getRowCount();
		// int selRows = 0;
		// add by yangjj 20150616
		TParm p = new TParm();
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) tablValue.getData("CASE_NO", i);
			String orderNo = (String) tablValue.getData("ORDER_NO", i);
			String orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			// ȡҩע��
			String takeMedOrg = (String) tablValue.getData("TAKEMED_ORG", i);
			if (!TypeTool.getBoolean(mainTable.getValueAt(i, 2))) { // ��λ�õ���17��2
				// add by yangjj 20150616
				p.addRowData(tablValue, i);
				undoData.addData("CASE_NO", caseNo);
				undoData.addData("ORDER_NO", orderNo);
				undoData.addData("ORDER_SEQ", orderSeq);
				undoData.addData("OPT_USER", Operator.getID());
				undoData.addData("OPT_DATE", now);
				undoData.addData("OPT_TERM", Operator.getIP());
				// undoData.addData("TAKEMED_ORG", takeMedOrg);
				if (tablValue.getValue("ORDER_CODE", i).equals(tablValue.getValue("ORDERSET_CODE", i)))
					getSetOrder(tablValue.getValue("CASE_NO", i), tablValue.getValue("ORDER_NO", i),
							tablValue.getValue("ORDER_CODE", i), tablValue.getValue("ORDERSET_GROUP_NO", i), now,
							undoData);
				// selRows++;
			}
		}
		// undoData.setCount(selRows);
		undoData.setCount(undoData.getCount("CASE_NO"));
		if (isExec(undoData)) {
			return false;
		}
		// System.out.println("undoData = " + undoData);
		// ����actionִ������
		TParm result = new TParm();
		if (isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			undoData.setData("dataParm", p.getData());
			result = TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onUndoCheckOP", undoData);
		} else {
			result = TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onUndoSave", undoData);
		}
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			return false;
		}

		return true;

	}

	private boolean checkPHACheck() {
		// TParm tablValue = mainTable.getParmValue();
		// int rowCount = mainTable.getRowCount();
		// boolean isCheck = false;
		// String descAll = "";
		// for (int i = 0; i < rowCount; i++) {
		// if (TypeTool.getBoolean(mainTable.getValueAt(i, 13)))
		// continue;
		// String desc = (String)
		// tablValue.getData("ORDER_DESC_AND_SPECIFICATION", i);
		// Timestamp phaCheckDate = (Timestamp)
		// tablValue.getData("PHA_CHECK_DATE", i);
		// if(phaCheckDate != null){
		// descAll += (desc +"\n");
		// isCheck = true;
		// }
		// }
		// if(isCheck)
		// messageBox(descAll + "ҩ��������޷�ȡ����ʿվ���");
		// return isCheck;
		// luhai modify 2012-2-25 �߹����ۣ��Ѿ���˵�ҩƷ����ʿҲ����ȡ����� begin
		TParm tablValue = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		boolean isCheck = false;
		String descAll = "";
		for (int i = 0; i < rowCount; i++) {
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 2))) // ��λ�õ���17��2
				continue;
			String desc = (String) tablValue.getData("ORDER_DESC_AND_SPECIFICATION", i);
			Timestamp phaCheckDate = (Timestamp) tablValue.getData("PHA_CHECK_DATE", i);
			String cat1Type = (String) tablValue.getData("CAT1_TYPE", i);
			if (phaCheckDate != null && !cat1Type.equalsIgnoreCase("PHA")) {
				descAll += (desc + "\n");
				isCheck = true;
			}
		}
		if (isCheck)
			messageBox(descAll + "ҩ��������޷�ȡ����ʿվ���");
		return isCheck;
		// luhai modify 2012-2-25 �߹����ۣ��Ѿ���˵�ҩƷ����ʿҲ����ȡ����� end
	}

	/**
	 * ��ʿȡ����˼���ҩ���Ƿ�ҩ
	 * 
	 * @return
	 */
	private boolean checkUDDCheck() {
		TParm tablValue = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		boolean isCheck = false;
		String descAll = "";
		for (int i = 0; i < rowCount; i++) {
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 2))) // ��λ�õ���17��2
				continue;
			String desc = (String) tablValue.getData("ORDER_DESC_AND_SPECIFICATION", i);
			String cat1Type = (String) tablValue.getData("CAT1_TYPE", i);
			if (!cat1Type.equalsIgnoreCase("PHA")) {
				continue;
			}
			String orderNo = (String) tablValue.getData("ORDER_NO", i);
			String orderSeq = (String) tablValue.getData("ORDER_SEQ", i);
			String SelSql = " SELECT PHA_DOSAGE_CODE,PHA_DOSAGE_DATE " + " FROM ODI_DSPNM " + " WHERE CASE_NO='"
					+ caseNo + "'" + " AND   ORDER_NO = '" + orderNo + "'" + " AND   ORDER_SEQ = '" + orderSeq + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(SelSql));
			for (int j = 0; j < result.getCount(); j++) {
				if (!result.getValue("PHA_DOSAGE_CODE", j).equals(""))
					isCheck = true;
			}
		}
		if (isCheck)
			messageBox(descAll + "ҩ���ѷ�ҩ�޷�ȡ����ʿվ���");
		return isCheck;
	}

	private void getSetOrder(String caseNo, String orderNo, String orderCode, String orderSetGroupNo, Timestamp now,
			TParm parm) {
		String SelSql = " SELECT * " + " FROM ODI_ORDER " + " WHERE CASE_NO='" + caseNo + "'" + " AND   ORDER_NO = '"
				+ orderNo + "'" + " AND   ORDERSET_GROUP_NO = '" + orderSetGroupNo + "'" + " AND   ORDERSET_CODE = '"
				+ orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(SelSql));
		for (int i = 0; i < result.getCount(); i++) {
			parm.addData("CASE_NO", result.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", result.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", result.getValue("ORDER_SEQ", i));
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_DATE", now);
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("TAKEMED_ORG", "");
		}
	}

	/**
	 * ������(ȡ�����)�����ʱ�����Ƿ��Ѿ�ִ��
	 * 
	 * @return boolean
	 */
	public boolean isExec(TParm ordParm) {
		Vector caseNo = (Vector) ordParm.getVector("CASE_NO");
		Vector orderNo = (Vector) ordParm.getVector("ORDER_NO");
		Vector orderSeq = (Vector) ordParm.getVector("ORDER_SEQ");
		String inCaseNo = "";
		String inOrderNo = "";
		String inOrderSeq = "";
		boolean flg1 = true;
		boolean flg2 = true;
		boolean flg3 = true;
		// ------------------------shibl 20120428 modify
		// for (int i = 0; i < ordParm.getCount(); i++) {
		// // //�������ݵ�ʱ��(����ΪƴIN)
		// // if (caseNo.size() != 0) {
		// // //��ǰ���Ѿ������ݲ��Һ��µ�һ������ͬ��ʱ����Ҫ��ǰ������ݺ�Ӷ���(���ö�·����Խ��)
		// // if (!inCaseNo.equals("") &&
		// // ! ( (Vector) caseNo.get(i - 1)).get(0).
		// // equals( ( (Vector) caseNo.get(i)).get(0))) {
		// // inCaseNo += ",";
		// // flg1 = true;
		// // }
		// // if (!inOrderNo.equals("") &&
		// // ! ( (Vector) orderNo.get(i - 1)).get(0).
		// // equals( ( (Vector) orderNo.get(i)).get(0))) {
		// // inOrderNo += ",";
		// // flg2 = true;
		// // }
		// // if (!inOrderSeq.equals("") &&
		// // ! ( (Vector) orderSeq.get(i - 1)).get(
		// // 0).equals( ( (Vector) orderSeq.get(i)).get(0))) {
		// // inOrderSeq += ",";
		// // flg3 = true;
		// // }
		// // inCaseNo += flg1 ? "'" + ( (Vector) caseNo.get(i)).get(0) + "'" :
		// // "";
		// // flg1 = false;
		// // inOrderNo += flg2 ?
		// // "'" + ( (Vector) orderNo.get(i)).get(0) + "'" : "";
		// // flg2 = false;
		// // inOrderSeq += flg3 ?
		// // "'" + ( (Vector) orderSeq.get(i)).get(0) + "'" : "";
		// // flg3 = false;
		// // }
		// }
		// ===========pangben modify 20110516 start �������
		String region = "";
		String checkSql = "";
		TParm result = new TParm();
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		if (isOpeFlg()) {
			return false;
		}
		// ------------------------shibl 20120428 modify
		for (int i = 0; i < ordParm.getCount(); i++) {
			inCaseNo = ((Vector) caseNo.get(i)).get(0) + "";
			inOrderNo = ((Vector) orderNo.get(i)).get(0) + "";
			inOrderSeq = ((Vector) orderSeq.get(i)).get(0) + "";
			checkSql = "SELECT  ORDER_DESC" + " FROM ODI_DSPNM" + " WHERE CASE_NO ='" + inCaseNo + "'"
					+ " AND ORDER_NO ='" + inOrderNo + "'" + " AND ORDER_SEQ ='" + inOrderSeq + "'"
					+ " AND NS_EXEC_CODE IS NOT NULL" + region;
			// ִ��sql���
			TJDODBTool tool = TJDODBTool.getInstance();
			result = new TParm(tool.select(checkSql));
			// �õ����ص�����(PS:��TJDODBTool���غ��װ��TParmȡ���������ⷽ��)
			int count = result.getInt("SYSTEM", "COUNT");
			if (count == 0) {
				continue;
			}
			String hadExecName = "";
			for (int j = 0; j < count; j++) {
				hadExecName += (result.getValue("ORDER_DESC", j) + "\n");
			}
			this.messageBox(hadExecName + "�Ѿ�ִ�в���ȡ��");
			return true;
		}
		return false;
	}

	/**
	 * ���������
	 * 
	 * @return boolean
	 */
	public boolean onCheck() {
		// �õ���������--չ���˵�caseNo
		mainTable.acceptText();
		TParm patData = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		TParm parm = new TParm();// ��������TParm
		int count = 0;
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) patData.getData("CASE_NO", i);
			String orderNo = (String) patData.getData("ORDER_NO", i);
			String orderSeq = patData.getData("ORDER_SEQ", i) + "";
			String setMainFlg = patData.getData("SETMAIN_FLG", i) + "";
			String orderSetGroupNo = patData.getData("ORDERSET_GROUP_NO", i) + "";
			// ȡҩע��
			String takeMedOrg = (String) patData.getData("TAKEMED_ORG", i);
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 2))) { // ��λ�õ���17��2
				// ===========================add by wanglong 20130710
				if ("UD".equals(patData.getValue("RX_KIND", i)) && !"PHA".equals(patData.getValue("CAT1_TYPE", i))
						&& !StringUtil.isNullString(patData.getValue("DC_DATE", i))) {
					TParm result = InwOrderCheckTool.getInstance().queryUnExecOrder(caseNo, orderNo, orderSeq);
					if (result.getErrCode() < 0) {
						this.messageBox_(result.getErrText());
						return false;
					} else if (result.getInt("NUM", 0) > 0) {
						this.messageBox("����:" + patData.getValue("PAT_NAME", i) + " �� "
								+ patData.getValue("ORDER_DESC_AND_SPECIFICATION", i) + "��"
								+ patData.getValue("ORDER_CODE", i) + "��δִ�й�������ͣ�ã�");
						continue;
					}
				}
				// ===========================add end
				// SHIBL 20120820 MODIFY ������ڲ������+ʱ��
				String checkDate = "" + mainTable.getValueAt(i, 23); // duzhw
																		// modify
																		// 20��Ϊ21
																		// 20131122
																		// 21��22
				String checkTime = ("" + mainTable.getValueAt(i, 24)).replace(":", ""); // chenxi modify 20130325//duzhw
																						// modify 21��Ϊ22
																						// 20131122 22��23
				// ��¼��ǰUI��ʾ�����ʱ��--���ڻ��ֶ��޸ĸ�ʱ��
				Timestamp checkDateTime = StringTool
						.getTimestamp(checkDate.substring(0, 10).replaceAll("/", "").replaceAll("-", "") + " "
								+ checkTime.substring(0, 4) + "00", "yyyyMMdd HHmmss");
				// һ���õ�caseNo
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				parm.addData("SETMAIN_FLG", setMainFlg);
				parm.addData("ORDERSETGROUP_NO", orderSetGroupNo);
				parm.addData("ORDER_CODE", patData.getValue("ORDER_CODE", i));// wanglong
																				// add
																				// 20140707
				parm.addData("ORDERSET_CODE", patData.getValue("ORDERSET_CODE", i));// wanglong add 20140707
				parm.addData("CHECK_DATETIME", checkDateTime);
				parm.addData("TAKEMED_ORG", takeMedOrg);

				parm.addData("INFLUTION_RATE", patData.getValue("INFLUTION_RATE", i)); // ��Һ����wukai on 20160531
				count++;
			}
		}
		// ���û�в���
		if (parm.getCount("CASE_NO") <= 0) {
			this.messageBox("��ѡ��Ҫ��˵Ĳ��ˣ�");
			return false;
		}
		// ��������
		parm.setCount(count);
		// ׼��������̨������
		TParm toData = new TParm();
		// ������Ա��Ϣ
		toData.setData("OPT_USER", Operator.getID());
		toData.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		toData.setData("OPT_TERM", Operator.getIP());
		toData.setData("dataParm", parm.getData());
		// ����actionִ������
		TParm result = new TParm();
		if (isOpeFlg()) {// ����ҽ�� wanglong add 20140707
			result = TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onCheckOP", toData);
		} else {
			result = TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onSave", toData);
		}
		// System.out.println("================="+result);
		if (result.getErrCode() == -100) {
			this.messageBox_(result.getErrText());
			return false;
		}
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			return false;
		}
		return true;

	}

	/**
	 * ���ù�ҩ������Ϣ
	 * 
	 * @param flg
	 */
	public boolean onSendGYPha(String flg, String type) {
		// �õ���������--չ���˵�caseNo
		mainTable.acceptText();
		TParm patData = mainTable.getParmValue();
		int rowCount = mainTable.getRowCount();
		TParm parm = new TParm();// ��������TParm
		int count = 0;
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) patData.getData("CASE_NO", i);
			String orderNo = (String) patData.getData("ORDER_NO", i);
			String orderSeq = patData.getData("ORDER_SEQ", i) + "";
			String cat1type = patData.getData("CAT1_TYPE", i) + "";
			String rxkind = patData.getData("RX_KIND", i) + "";
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 2)) && cat1type.equals("PHA") // ��λ�õ���17��2
					&& rxkind.equals("ST") && flg.equals("A") && type.equals("1")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
			if (!TypeTool.getBoolean(mainTable.getValueAt(i, 2)) && cat1type.equals("PHA") // ��λ�õ���17��2
					&& rxkind.equals("ST") && flg.equals("D") && type.equals("1")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 2)) && cat1type.equals("PHA") // ��λ�õ���17��2
					&& rxkind.equals("ST") && flg.equals("A") && type.equals("2")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 2)) && cat1type.equals("PHA") // ��λ�õ���17��2
					&& rxkind.equals("ST") && flg.equals("D") && type.equals("2")) {
				parm.addData("CASE_NO", caseNo);
				parm.addData("ORDER_NO", orderNo);
				parm.addData("ORDER_SEQ", orderSeq);
				count++;
			}
		}
		if (count <= 0) {
			this.messageBox("û�з��͵�����");
			return false;
		}
		parm.setCount(count);
		parm.setData("TYPE", flg);
		TParm result = TIOM_AppServer.executeAction("action.inw.InwOrderCheckAction", "onSendGYPha", parm);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * ���͹���
	 */
	public void onReSendGYPha() {
		if (Operator.getSpcFlg().equals("N")) {
			this.messageBox("�����������ѹر�");
			return;
		}
		String flg = "";
		if (checkYES.isSelected()) {
			flg = "A";
		} else if (this.checkNO.isSelected()) {
			flg = "D";
		} else {
			this.messageBox("����ֻ��������˻�δ�����ʹ��");
			return;
		}
		if (onSendGYPha(flg, "2")) {
			this.messageBox("���ͳɹ�");
		} else {
			this.messageBox("����ʧ��");
		}
	}

	/**
	 * ����table�ϵõ����ݣ���û��ִ�еģ�NS_EXEC_CODE�ǿյ��޳���
	 * 
	 * @param totalParm
	 *            table�ϵõ�����������
	 * @return TParm ����֮����Ҫִ�е�����
	 */
	public TParm arrangeDateToIBS(TParm totalParm) {
		TParm result = new TParm();
		int count = totalParm.getCount();
		// δ��˽���
		if (!checkYES.isSelected()) {
			// ����ɾ��--vector
			for (int i = count - 1; i >= 0; i--) {
				// ���ִ�л�ʿΪ��˵��û��ִ�У������޳���������̨IBS
				if (totalParm.getData("NS_EXEC_CODE", i) != null && !totalParm.getData("NS_EXEC_CODE", i).equals("")
						&& !totalParm.getData("DSPN_KIND", i).equals("PHA")) { // ����ҩƷ���ڻ�ʿվ�Ʒ�
					continue;
				}
				totalParm.removeRow(i);

			}
		} // ��˽���
		else {
			// ����ɾ��--vector
			for (int i = count - 1; i >= 0; i--) {
				// ���ִ�л�ʿΪ��˵��û��ִ�У������޳���������̨IBS
				if (totalParm.getData("NS_EXEC_CODE", i) == null && !totalParm.getData("DSPN_KIND", i).equals("PHA")) { // ����ҩƷ���ڻ�ʿվ�Ʒ�
					continue;
				}
				totalParm.removeRow(i);

			}
		}
		result = totalParm;
		return result;
	}

	/**
	 * ����Ӧtable���Ƿ�������� ��ÿ�β�ѯ��
	 * 
	 * @param tab
	 *            TTable
	 */
	public void existsDateForTabl(TTable tab) {
		if (tab.getRowCount() == 0)
			this.messageBox("û��������ݣ�");
		// ����ȫ��ִ�пؼ�
		exeAll.setSelected(updateAllExe());
	}

	/**
	 * ����ĳ��ҽ����״̬������ʾ��Ϣ
	 */
	public void onClick() {

		TParm action = mainTable.getDataStore().getRowParm(mainTable.getSelectedRow());
		callFunction("UI|setSysStatus", action.getValue("ORDER_CODE") + action.getValue("ORDER_DESC")
				+ action.getValue("GOODS_DESC") + action.getValue("DESCRIPTION") + action.getValue("SPECIFICATION"));

	}

	/**
	 * ��ӡ�ಡ��ҽ����˵�
	 */
	public void onPrintExe() {
		if (this.ord1All.isSelected() || this.ord2All.isSelected() || checkAll.isSelected()) {
			this.messageBox("������ȫ��״̬�´�ӡ��");
			return;
		}
		String orderKind = "";
		if (ord1ST.isSelected()) {
			orderKind = "��ʱ";
		} else if (ord1UD.isSelected()) {
			orderKind = "����";
		} else if (ord1DS.isSelected()) {
			orderKind = "��Ժ��ҩ";
		} else if (ord1IG.isSelected()) {
			orderKind = "��ҩ��Ƭ";
		}
		String orderType = "";
		if (ord2PHA.isSelected()) {
			orderType = "ҩ��";
		} else if (ord2PL.isSelected()) {
			orderType = "������";
		} else if (ord2ENT.isSelected()) {
			orderType = "����";
		}
		String orderExe = "";
		if (this.checkYES.isSelected()) {
			orderExe = "�����";
		} else if (checkNO.isSelected()) {
			orderExe = "δ���";
		}
		mainTable.acceptText();
		TParm tableDate = this.mainTable.getParmValue();
		String caseNo = "", orderNo = "", orderSeq = "", startDttm = "", endDttm = "";
		Map map = new HashMap();
		TParm printData = new TParm();
		int count = 0;
		int tableDataCount = tableDate.getCount();
		for (int i = 0; i < tableDataCount; i++) {
			boolean prtFlg = tableDate.getBoolean("PRINT_FLG", i);
			if (!prtFlg)
				continue;
			String caseNoKey = tableDate.getValue("CASE_NO", i);
			if (map.get(caseNoKey) != null) {
				printData.addData("BED_NO", "");
				// printData.addData("MR_NO", "");
				printData.addData("PAT_NAME", "");
				printData.addData("CASE_NO", caseNoKey);
			} else if (map.get(caseNoKey) == null && i == 0) {
				printData.addData("CASE_NO", caseNoKey);
				printData.addData("BED_NO", this.getNewbedDesc(caseNoKey));
				printData.addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
			} else {
				printData.addData("CASE_NO", caseNoKey);
				printData.addData("BED_NO", " ");
				// printData.addData("MR_NO", "������");
				printData.addData("PAT_NAME", " ");
				printData.addData("LINK_NO", " ");
				printData.addData("ORDER_DESC", " ");
				printData.addData("MEDI_QTY", " ");
				printData.addData("MEDI_UNIT", " ");
				printData.addData("FREQ_CODE", "");
				printData.addData("ROUTE_CODE", " ");
				printData.addData("DC_DATE", " ");
				printData.addData("NS_CHECK_DATE", " ");
				printData.addData("DR_NOTE", " ");
				printData.addData("NS_CHECK_CODE", " ");
				count++;
				printData.addData("CASE_NO", caseNoKey);
				printData.addData("BED_NO", this.getNewbedDesc(caseNoKey));
				// printData.addData("MR_NO", tableDate.getValue("MR_NO", i));
				printData.addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
			}
			map.put(caseNoKey, caseNoKey);
			printData.addData("LINK_NO", tableDate.getValue("LINK_NO", i));
			printData.addData("ORDER_DESC", tableDate.getValue("ORDER_DESC_AND_SPECIFICATION", i));
			printData.addData("MEDI_QTY", tableDate.getValue("MEDI_QTY", i));
			printData.addData("MEDI_UNIT", getUnit(tableDate.getValue("MEDI_UNIT", i)));
			printData.addData("FREQ_CODE",
					getFreqData(tableDate.getValue("FREQ_CODE", i)).getValue("FREQ_CHN_DESC", 0));
			printData.addData("ROUTE_CODE", OrderUtil.getInstance().getRoute(tableDate.getValue("ROUTE_CODE", i)));
			printData.addData("DC_DATE", tableDate.getValue("DC_DATE", i).equals("") ? " "
					: tableDate.getValue("DC_DATE", i).replaceAll("-", "/").substring(5, 16));
			printData.addData("NS_CHECK_DATE",
					tableDate.getValue("NS_CHECK_DATE", i).equals("") ? " "
							: tableDate.getValue("NS_CHECK_DATE", i).replaceAll("-", "/").substring(5, 10) + " "
									+ (tableDate.getValue("NS_CHECK_DATE_TIME", i).equals("") ? " "
											: tableDate.getValue("NS_CHECK_DATE_TIME", i).substring(0, 5)));
			printData.addData("DR_NOTE", tableDate.getValue("DR_NOTE", i));
			printData.addData("NS_CHECK_CODE", getOperatorName(tableDate.getValue("NS_CHECK_CODE", i)));
			count++;
		}
		printData.setCount(count);
		TParm GprintParm = new TParm();
		if (count <= 0) {
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		} else {
			Map patMap = new HashMap();
			Map pat = groupByPatParm(printData);
			// Iterator it = pat.values().iterator();
			for (int j = 0; j < tableDate.getCount(); j++) {
				boolean prtFlg = tableDate.getBoolean("PRINT_FLG", j);
				if (!prtFlg)
					continue;
				if (patMap.get(tableDate.getValue("CASE_NO", j)) == null) {
					if (pat.get(tableDate.getValue("CASE_NO", j)) != null) {
						TParm patParm = (TParm) pat.get(tableDate.getValue("CASE_NO", j));
						int rows = patParm.getCount();
						for (int i = 0; i < rows; i++) {
							GprintParm.addData("BED_NO", patParm.getValue("BED_NO", i));
							GprintParm.addData("PAT_NAME", patParm.getValue("PAT_NAME", i));
							GprintParm.addData("LINK_NO", patParm.getValue("LINK_NO", i));
							GprintParm.addData("ORDER_DESC", patParm.getValue("ORDER_DESC", i));
							GprintParm.addData("MEDI_QTY", patParm.getValue("MEDI_QTY", i));
							GprintParm.addData("MEDI_UNIT", patParm.getValue("MEDI_UNIT", i));
							GprintParm.addData("FREQ_CODE", patParm.getValue("FREQ_CODE", i));
							GprintParm.addData("ROUTE_CODE", patParm.getValue("ROUTE_CODE", i));
							GprintParm.addData("NS_CHECK_DATE", patParm.getValue("NS_CHECK_DATE", i));
							GprintParm.addData("DC_DATE", patParm.getValue("DC_DATE", i));
							GprintParm.addData("DR_NOTE", patParm.getValue("DR_NOTE", i));
							GprintParm.addData("NS_CHECK_CODE", patParm.getValue("NS_CHECK_CODE", i));
						}
					}
				}
				patMap.put(tableDate.getValue("CASE_NO", j), tableDate.getValue("CASE_NO", j));
			}
		}
		GprintParm.setCount(count);
		GprintParm.addData("SYSTEM", "COLUMNS", "BED_NO");
		// printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		GprintParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		GprintParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
		GprintParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_UNIT");
		GprintParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		GprintParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		GprintParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_DATE");
		GprintParm.addData("SYSTEM", "COLUMNS", "DC_DATE");
		GprintParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		GprintParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_CODE");
		TParm printParm = new TParm();
		printParm.setData("TITLE", "TEXT", "ҽ����˵�");
		printParm.setData("ORDER_KIND", "TEXT", "ҽ�����:" + orderKind);
		printParm.setData("ORDER_TYPE", "TEXT", "ҽ������:" + orderType);
		printParm.setData("ORDER_EXE", "TEXT", "���ȷ��:" + orderExe);
		printParm.setData("STATION_CODE", "TEXT", "������   " + getStationDesc(this.getValueString("INW_STATION_CODE")));
		printParm.setData("DATE", "TEXT",
				"������գ�" + this.getValueString("from_Date").replaceAll("-", "/").substring(0, 10) + " "
						+ this.getValueString("from_Time") + " " + "������գ�"
						+ this.getValueString("to_Date").replaceAll("-", "/").substring(0, 10) + " "
						+ this.getValueString("to_Time"));
		printParm.setData("TABLE", GprintParm.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\inw\\inwCheckNewPrint.jhw", printParm);
	}

	/**
	 * ���table�ϵĵ�������
	 */
	public void onRemoveTbl() {
		if (this.ord1ST.isSelected()) {
			this.includeOutMed.setEnabled(true);
		} else {
			this.includeOutMed.setSelected(false);
			this.includeOutMed.setEnabled(false);
		}
		mainTable.setParmValue(new TParm());
		exeAll.setSelected(false);
		printAll.setSelected(false);
		if (this.checkYES.isSelected() && this.ord2PHA.isSelected()) {
			callFunction("UI|medPrint|setEnabled", true);
			this.StUdLab.setEnabled(true);
			this.medSta.setEnabled(true);
			this.medOrg.setEnabled(true);
			this.medSta.setSelected(true);
			this.medOrg.setSelected(false);
		} else {
			callFunction("UI|medPrint|setEnabled", false);
			this.StUdLab.setEnabled(false);
			this.medSta.setEnabled(false);
			this.medOrg.setEnabled(false);
			this.medSta.setSelected(true);
			this.medOrg.setSelected(false);
		}
	}

	/**
	 * ����table�ϵ����ݸ���ȫ��ִ�б���Ƿ�ѡ��
	 */
	public boolean updateAllExe() {
		int rowCount = mainTable.getRowCount();
		if (rowCount != 0) {
			for (int i = 0; i < rowCount; i++) {
				if (!TypeTool.getBoolean(mainTable.getValueAt(i, 2))) // ��λ�õ���17��2
					return false;
			}
			return true;
		}
		return false;
	}

	public void selDOSE(Object flg) {
		boolean temp = TypeTool.getBoolean(flg);
		// ���ѡ��
		typeO.setSelected(temp);
		typeE.setSelected(temp);
		typeI.setSelected(temp);
		typeF.setSelected(temp);
		typeC.setSelected(temp);
		// �༭״̬
		typeO.setEnabled(temp);
		typeE.setEnabled(temp);
		typeI.setEnabled(temp);
		typeF.setEnabled(temp);
		typeC.setEnabled(temp);
		// ���table
		onRemoveTbl();
	}

	/**
	 * ��ѡ����ѡ���ӡ
	 */
	public void onPrtAll() {
		boolean prt = printAll.isSelected();
		int count = mainTable.getRowCount();
		for (int i = 0; i < count; i++) {
			mainTable.setItem(i, "PRINT_FLG", prt);
		}
	}

	/**
	 * �����л���ʱ�����TopMenu
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
		// case 0:
		// if (!onSave())
		// return false;
		// break;
		// case 1:
		// break;
		// case 2:
		// return false;
		// }
		return true;
	}

	/**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "inwCheck";
		String value = (String) this.openDialog("%ROOT%\\config\\inw\\passWordCheck.x", inwCheck);
		if (value == null) {
			return false;
		}
		return value.equals("OK");
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public LEDUI getLedui() {
		return ledui;
	}

	public void setLedui(LEDUI ledui) {
		this.ledui = ledui;
	}

	public boolean isOpeFlg() {
		return opeFlg;
	}

	public void setOpeFlg(boolean opeFlg) {
		this.opeFlg = opeFlg;
	}

	public String getOpDeptCode() {
		return opDeptCode;
	}

	public void setOpDeptCode(String opDeptCode) {
		this.opDeptCode = opDeptCode;
	}

	public String getOpBookSeq() {
		return opBookSeq;
	}

	public void setOpBookSeq(String opBookSeq) {
		this.opBookSeq = opBookSeq;
	}

	/**
	 * ��������ҩƷ��ɫ
	 */
	/**
	 * 
	 */
	private void setColor() {
		TParm tableParm = mainTable.getParmValue();
		Color antibioticColor = new Color(255, 0, 0);
		Color normalColor = new Color(0, 0, 0);
		Color WhiteColor = new Color(255, 255, 255);
		Color blueColor = new Color(0, 0, 255);

		// add by yangjj 20150729
		List<Integer> rowList = new ArrayList<Integer>();
		List<Integer> colList = new ArrayList<Integer>();

		for (int i = 0; i < tableParm.getCount("ANTIBIOTIC_CODE"); i++) {
			String orderCode = tableParm.getValue("ORDER_CODE", i);
			String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '" + orderCode + "'";
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
			sqlparm = sqlparm.getRow(0);
			if (tableParm.getValue("ANTIBIOTIC_CODE", i).length() > 0) {
				mainTable.setRowTextColor(i, antibioticColor);
			} else if (this.getColorByHighRiskOrderCode(orderCode)) {// 20150803 wangjc add
				mainTable.setRowTextColor(i, Color.RED);// ��ΣҩƷ����ʾΪ��ɫ
			} else if (sqlparm.getValue("DRUG_NOTES_DR").length() > 0) {
				mainTable.setRowTextColor(i, blueColor);
			} else {
				mainTable.setRowTextColor(i, normalColor);
			}
			TParm action = tableParm.getRow(i);
			if ("Y".equals(action.getValue("URGENT_FLG"))) {
				mainTable.setRowColor(i, yellowColor);
			} else {
				mainTable.setRowColor(i, WhiteColor);
			}
			// add by yangjj 20150728
			if (!"".equals(mainTable.getParmValue().getValue("DC_DATE", i))) {
				rowList.add(i);
			}
		}

		// add by yangjj 20150729
		JTableBase tablebase = mainTable.getTable();

		System.out.println("col:" + mainTable.getColumnModel().getColumnIndex(8));
		System.out
				.println("hello:" + mainTable.getHorizontalAlignment(1, mainTable.getColumnModel().getColumnIndex(8)));

		MyTableCellRenderer cellRenderer = new MyTableCellRenderer(mainTable, rowList, colList, new Color(255, 0, 0));
		tablebase.setCellRenderer(cellRenderer);
		mainTable.setColumnHorizontalAlignmentData(
				"0,left;1,left;7,left;8,right;9,left;10,right;11,left;12,left;13,left;17,left;18,left;20,left;21,left;22,left;25,left;26,left;29,left;30,left");
		// mainTable.onInit();
	}

	/**
	 * �ж��Ƿ�Ϊ��ΣҩƷ
	 * 
	 * @param orderCode
	 * @return 20150731 wangjc add
	 */
	public boolean getColorByHighRiskOrderCode(String orderCode) {
		boolean a = false;
		String sql = "SELECT HIGH_RISK_FLG FROM PHA_BASE WHERE ORDER_CODE='" + orderCode + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getValue("HIGH_RISK_FLG", 0).equals("Y")) {
			a = true;
		}
		return a;
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = mainTable.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = mainTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
				setColor();
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		mainTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * ���ѡ����ҩ���������ҩ������˵�����й���
	 * 
	 * @param parm
	 */
	private void filterDrugByDoseCode(TParm parm) {
		if (!ord2PHA.isSelected())
			return;
		if (parm.getCount() <= 0)
			return;
		for (int i = parm.getCount() - 1; i >= 0; i--) {
			if (!parm.getValue("ORDER_CAT1_CODE", i).startsWith("PHA"))
				continue;
			if (typeC.isSelected() && !getPhaCtrlCode(parm.getValue("ORDER_CODE", i)))
				parm.removeRow(i);
		}
		parm.setCount(parm.getCount("ORDER_CAT1_CODE"));
	}

	/**
	 * ���ݴ��Ų鵽��λ����
	 */
	private String getNewbedDesc(String caseNo) {
		String bed = "";
		TParm parm = new TParm(TJDODBTool.getInstance()
				.select("SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE CASE_NO = '" + caseNo + "'"));
		if (parm.getCount() > 0) {
			bed = parm.getValue("BED_NO_DESC", 0);
		}
		return bed;
	}

	/**
	 * ȡ��ҩƷ��λ����
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	private String getUnit(String code) {
		if (code.length() == 0)
			return "";
		TParm parm = new TParm(TJDODBTool.getInstance()
				.select(" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT " + " WHERE UNIT_CODE = '" + code + "'"));
		return parm.getValue("UNIT_CHN_DESC", 0);
	}

	/**
	 * ȡ��Ƶ������
	 * 
	 * @param freqCode
	 * @return
	 */
	private TParm getFreqData(String freqCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(" SELECT FREQ_CHN_DESC,FREQ_TIMES,DESCRIPTION "
				+ " FROM SYS_PHAFREQ " + " WHERE FREQ_CODE='" + freqCode + "'"));
		return parm;
	}

	/**
	 * ����userId�õ�����
	 * 
	 * @param userID
	 * @return
	 */
	private String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance()
				.select(" SELECT USER_NAME " + " FROM SYS_OPERATOR " + " WHERE USER_ID = '" + userID + "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * ������������
	 * 
	 * @param parm
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map groupByPatParm(TParm parm) {
		Map result = new HashMap();
		if (parm == null) {
			return null;
		}
		int count = parm.getCount();
		if (count < 1) {
			return null;
		}
		TParm temp = new TParm();
		String[] names = parm.getNames();
		if (names == null) {
			return null;
		}
		if (names.length < 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(";");
		}
		try {
			sb.replace(sb.lastIndexOf(";"), sb.length(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TParm tranParm = new TParm();
		for (int i = 0; i < count; i++) {
			String orderNo = parm.getValue("CASE_NO", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			} else {
				tranParm = (TParm) result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
		return result;
	}

	/**
	 * �õ�����
	 * 
	 * @param stationCode
	 * @return
	 */
	private String getStationDesc(String stationCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION " + " WHERE STATION_CODE = '" + stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * ����ִ�е���ӡ add by yangjj 20150723
	 */
	public void onCategoryPrint() {
		List<TParm> lstParm = new ArrayList<TParm>();
		lstParm = getPatient();
		for (TParm p : lstParm) {
			execPrint(false, p);
		}
	}

	/**
	 * ����ִ�е���ӡ add by yangjj 20150723
	 */
	public void onTemporaryPrint() {
		List<TParm> lstParm = new ArrayList<TParm>();
		lstParm = getPatient();
		for (TParm p : lstParm) {
			execPrint(true, p);
		}
	}

	public List<TParm> getPatient() {
		mainTable = (TTable) this.getComponent("MAINTABLE");
		mainTable.acceptText();
		TParm parm = mainTable.getParmValue();
		List<TParm> lstParm = new ArrayList<TParm>();
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < parm.getCount(); i++) {
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 3))) {
				set.add(parm.getValue("MR_NO", i));
			}
		}

		for (String mrNo : set) {
			String sqlPatient = " SELECT " + " A.MR_NO, " + " A.PAT_NAME, " + " B.CHN_DESC AS SEX, " + " A.BIRTH_DATE, "
					+ " A.IPD_NO " + " FROM " + " SYS_PATINFO A , " + " SYS_DICTIONARY B " + " WHERE " + " A.MR_NO = '"
					+ mrNo + "' " + " AND B.GROUP_ID = 'SYS_SEX' " + " AND B.ID = A.SEX_CODE ";
			TParm result = new TParm(TJDODBTool.getInstance().select(sqlPatient));
			TParm p = new TParm();
			p.setData("MR_NO", result.getValue("MR_NO", 0));
			p.setData("PAT_NAME", result.getValue("PAT_NAME", 0));
			p.setData("SEX", result.getValue("SEX", 0));

			String birthDate = "";
			String age = "";
			if (!"".equals(result.getValue("BIRTH_DATE", 0))) {
				birthDate = result.getValue("BIRTH_DATE", 0).replace("-", "/").substring(0, 10);
				age = DateUtil.showAge(result.getTimestamp("BIRTH_DATE", 0), SystemTool.getInstance().getDate());
			}
			p.setData("BIRTH_DATE", birthDate);
			p.setData("IPD_NO", result.getValue("IPD_NO", 0));
			p.setData("AGE", age);
			lstParm.add(p);
		}

		return lstParm;
	}

	private String getFreDesc(String code) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT FREQ_CHN_DESC,FREQ_ENG_DESC " + " FROM SYS_PHAFREQ " + " WHERE FREQ_CODE = '" + code + "'"));
		if (parm.getCount() <= 0)
			return "";
		return (parm.getValue("FREQ_CHN_DESC", 0) == null || parm.getValue("FREQ_CHN_DESC", 0).equalsIgnoreCase("null")
				|| parm.getValue("FREQ_CHN_DESC", 0).length() == 0) ? code : parm.getValue("FREQ_CHN_DESC", 0);
	}

	public void execPrint(boolean b, TParm pp) {
		System.out.println("pp:" + pp);
		// bΪtrueΪ����ִ�е���falseΪ����ִ�е�

		// ҽ�������Ϊȫ��
		if (((TRadioButton) getComponent("ord1All")).isSelected()) {
			this.messageBox("��ѡ��ҽ�����");
			return;
		}

		TTable mainTable = (TTable) this.getComponent("MAINTABLE");

		mainTable.acceptText();
		TParm p = mainTable.getParmValue();
		// ѡ���ӡ��ѡ�������
		TParm parm = new TParm();
		for (int i = 0; i < p.getCount(); i++) {
			if (TypeTool.getBoolean(mainTable.getValueAt(i, 3))
					&& pp.getValue("MR_NO").equals(mainTable.getValueAt(i, 30))) {
				parm.addRowData(p, i);
			}
		}
		if (parm.getCount() <= 0) {
			// this.messageBox("��ѡ���ӡ���ݣ�");
			return;
		}

		// ��ӡ�ı������
		TParm tableParm = new TParm();

		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("filePatName", "TEXT", pp.getValue("PAT_NAME"));
		data.setData("fileSex", "TEXT", pp.getValue("SEX"));
		// TParm birthDay = new
		// TParm(TJDODBTool.getInstance().select("SELECT BIRTH_DATE FROM SYS_PATINFO
		// WHERE MR_NO = '"+this.getValueString("MR_NO")+"'"));
		data.setData("fileBirthday", "TEXT", pp.getValue("BIRTH_DATE"));
		data.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", pp.getValue("MR_NO"));
		data.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", pp.getValue("IPD_NO"));
		data.setData("NAME", "TEXT", pp.getValue("PAT_NAME"));
		data.setData("SEX", "TEXT", pp.getValue("SEX"));
		data.setData("AGE", "TEXT", pp.getValue("AGE"));
		data.setData("MRNO", "TEXT", pp.getValue("MR_NO"));
		data.setData("BEDNO", "TEXT", "");

		// ��ʱҽ��ִ�е�
		if (((TRadioButton) getComponent("ord1ST")).isSelected() && (!b)) {
			data.setData("TITLE", "TEXT", "��ʱҽ��ִ�е�");
			data.setData("TIMES", "TEXT", "�÷�ʱ��");
			data.setData("TIMES1", "TEXT", "����ʱ��");
			System.out.println("11111111" + parm);
			for (int i = 0; i < parm.getCount(); i++) {
				tableParm.addData("LINK_NO", parm.getData("LINK_NO", i));
				// modify by guoy 20151123 ------start---------
				if (parm.getData("DISPENSE_FLG", i).equals("Y")) {
					tableParm.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i) + "(��)");
				} else {
					tableParm.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
				}
				TParm unitParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '" + parm.getData("MEDI_UNIT", i) + "'"));
				tableParm.addData("MEDI_QTY", parm.getData("MEDI_QTY", i) + unitParm.getValue("UNIT_CHN_DESC", 0));
				// modify by guoy 20151123 ------end---------
				TParm routeParm = new TParm(
						TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
								+ parm.getData("ROUTE_CODE", i) + "'"));
				tableParm.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
				tableParm.addData("FREQ", getFreDesc((String) parm.getData("FREQ_CODE", i)));
				TParm orderDrParm = new TParm(
						TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
								+ parm.getData("ORDER_DR_CODE", i) + "'"));
				tableParm.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
				tableParm.addData("DR_NOTE", parm.getData("DR_NOTE", i));
				tableParm.addData("ORDER_DATE",
						parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
				tableParm.addData("EXEC_DATE", "");
				tableParm.addData("EXEC_DR", "");

				tableParm.addData("INFLUTION_RATE", parm.getData("INFLUTION_RATE", i));// ���ӵ��ٴ�ӡ machao
				data.setData("BEDNO", "TEXT", parm.getData("BED_NO", i));
			}

			tableParm.setCount(parm.getCount());
			tableParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			tableParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			tableParm.addData("SYSTEM", "COLUMNS", "ROUTE");
			tableParm.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");// ����
			tableParm.addData("SYSTEM", "COLUMNS", "FREQ");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DR");
			tableParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
			tableParm.addData("SYSTEM", "COLUMNS", "EXEC_DATE");
			tableParm.addData("SYSTEM", "COLUMNS", "EXEC_DR");

			TParm drParm = new TParm(TJDODBTool.getInstance()
					.select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" + Operator.getID() + "'"));
			data.setData("printer", "TEXT", drParm.getValue("USER_NAME", 0));
			data.setData("printTime", "TEXT",
					SystemTool.getInstance().getDate().toString().substring(0, 17).replace(" ", "��")
							.replaceFirst("-", "��").replaceFirst("-", "��").replaceFirst(":", "ʱ")
							.replaceFirst(":", "��"));
			// this.messageBox("1");
			data.setData("TABLE", tableParm.getData());
			System.out.println("2222" + tableParm);
			/*
			 * TParm para = new TParm(); para.setData("FILE",
			 * "%ROOT%\\config\\prt\\inw\\INWExecST.jhw"); para.setData("DATA", data);
			 * 
			 * this.openWindow("%ROOT%\\config\\inw\\INWOrderSheetPrtXD.x", para);
			 */

			this.openPrintWindow("%ROOT%\\config\\prt\\inw\\INWExecST.jhw", data);

			return;
		}

		// ��ʱҽ��ִ�е������ܣ�
		else if (((TRadioButton) getComponent("ord1ST")).isSelected() && b) {
			data.setData("TITLE", "TEXT", "��ʱҽ��ִ�е������ܣ�");
			data.setData("TIMES", "TEXT", "����ʱ��");
			data.setData("TIMES1", "TEXT", "");
			for (int i = 0; i < parm.getCount(); i++) {
				tableParm.addData("LINK_NO", parm.getData("LINK_NO", i));
				// modify by guoy 20151123 ------start---------
				if (parm.getData("DISPENSE_FLG", i).equals("Y")) {
					tableParm.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i) + "(��)");
				} else {
					tableParm.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
				}

				TParm unitParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '" + parm.getData("MEDI_UNIT", i) + "'"));
				tableParm.addData("MEDI_QTY", parm.getData("MEDI_QTY", i) + unitParm.getValue("UNIT_CHN_DESC", 0));
				// modify by guoy 20151123 ------end---------
				TParm routeParm = new TParm(
						TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
								+ parm.getData("ROUTE_CODE", i) + "'"));
				tableParm.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
				tableParm.addData("FREQ", getFreDesc((String) parm.getData("FREQ_CODE", i)));
				tableParm.addData("ORDER_DATE",
						parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
				TParm orderDrParm = new TParm(
						TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
								+ parm.getData("ORDER_DR_CODE", i) + "'"));
				tableParm.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
				tableParm.addData("DR_NOTE", parm.getData("DR_NOTE", i));
				tableParm.addData("INFLUTION_RATE", parm.getData("INFLUTION_RATE", i));// ���ӵ��ٴ�ӡ machao

				data.setData("BEDNO", "TEXT", parm.getData("BED_NO", i));
			}

			tableParm.setCount(parm.getCount());
			tableParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			tableParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			tableParm.addData("SYSTEM", "COLUMNS", "ROUTE");
			tableParm.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");// ����
			tableParm.addData("SYSTEM", "COLUMNS", "FREQ");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DR");
			tableParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DATE");

			TParm drParm = new TParm(TJDODBTool.getInstance()
					.select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" + Operator.getID() + "'"));
			data.setData("printer", "TEXT", drParm.getValue("USER_NAME", 0));
			data.setData("printTime", "TEXT",
					SystemTool.getInstance().getDate().toString().substring(0, 17).replace(" ", "��")
							.replaceFirst("-", "��").replaceFirst("-", "��").replaceFirst(":", "ʱ")
							.replaceFirst(":", "��"));

			data.setData("TABLE", tableParm.getData());
			System.out.println("1111" + tableParm);
			/*
			 * TParm para = new TParm(); para.setData("FILE",
			 * "%ROOT%\\config\\prt\\inw\\INWExecSTTotal.jhw"); para.setData("DATA", data);
			 * 
			 * this.openWindow("%ROOT%\\config\\inw\\INWOrderSheetPrtXD.x", para);
			 */

			this.openPrintWindow("%ROOT%\\config\\prt\\inw\\INWExecSTTotal.jhw", data);

			return;
		}

		// ����ҽ��ִ�е�
		else if (((TRadioButton) getComponent("ord1UD")).isSelected() && (!b)) {
			data.setData("TITLE", "TEXT", "����ҽ��ִ�е�");
			data.setData("VALIDITY", "TEXT",
					"��Ч��:" + getValueString("from_Date").replaceFirst("-", "��").replaceFirst("-", "��").replace(" ", "��")
							.substring(0, 11) + getValueString("from_Time").replaceFirst(":", "ʱ").substring(0, 5) + "��"
							+ "��"
							+ getValueString("to_Date").replaceFirst("-", "��").replaceFirst("-", "��").replace(" ", "��")
									.substring(0, 11)
							+ getValueString("to_Time").replaceFirst(":", "ʱ").substring(0, 5) + "��");

			// �ڷ�
			TParm oParm = new TParm();
			TParm otParm = new TParm();
			int iO = 0;

			// ������Һ
			TParm fParm = new TParm();
			TParm ftParm = new TParm();
			int iF = 0;
			// ע��
			TParm iParm = new TParm();
			TParm itParm = new TParm();
			int iI = 0;
			// ����
			TParm eParm = new TParm();
			TParm etParm = new TParm();
			int iE = 0;

			for (int i = 0; i < parm.getCount(); i++) {
				TParm routeParm = new TParm(
						TJDODBTool.getInstance().select("SELECT CLASSIFY_TYPE FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
								+ parm.getData("ROUTE_CODE", i) + "'"));
				parm.addData("CLASSIFY", routeParm.getValue("CLASSIFY_TYPE", 0));
			}
			for (int i = 0; i < parm.getCount(); i++) {
				String classify = parm.getData("CLASSIFY", i) + "";
				if ("O".equals(classify)) {
					oParm.addRowData(parm, i);
					oParm.setCount(++iO);
				} else if ("F".equals(classify)) {
					fParm.addRowData(parm, i);
					fParm.setCount(++iF);
				} else if ("I".equals(classify)) {
					iParm.addRowData(parm, i);
					iParm.setCount(++iI);
				} else {
					eParm.addRowData(parm, i);
					eParm.setCount(++iE);
				}

				data.setData("BEDNO", "TEXT", parm.getData("BED_NO", i));
			}
			otParm = getUDPrintTableParm(oParm);
			ftParm = getUDPrintTableParm(fParm);
			itParm = getUDPrintTableParm(iParm);
			etParm = getUDPrintTableParm(eParm);

			if (otParm.getCount() <= 0) {
				otParm = new TParm();
				otParm.setData("Visible", false);
				data.setData("O", "TEXT", "");
			} else {
				data.setData("O", "TEXT", "�ڷ�ҩ");
			}
			if (ftParm.getCount() <= 0) {
				ftParm = new TParm();
				ftParm.setData("Visible", false);
				data.setData("F", "TEXT", "");
			} else {
				data.setData("F", "TEXT", "������Һ");
			}
			if (itParm.getCount() <= 0) {
				itParm = new TParm();
				itParm.setData("Visible", false);
				data.setData("I", "TEXT", "");
			} else {
				data.setData("I", "TEXT", "ע��");
			}
			if (etParm.getCount() <= 0) {
				etParm = new TParm();
				etParm.setData("Visible", false);
				data.setData("E", "TEXT", "");
			} else {
				data.setData("E", "TEXT", "����");
			}
			// ����ҽ�����ࡢ����ִ�е����ӵ�����Ϣ�� =======start$$$
			// ���ڱ���ײ�������⣬����ɾ��table�����Բ�������table��ʽ����ԭ����table����
			// ������ϵqing.wang@bluecore.com.cn
			// data.setData("OTABLE", otParm.getData());
			// data.setData("FTABLE", ftParm.getData());
			// data.setData("ITABLE", itParm.getData());
			// data.setData("ETABLE", etParm.getData());

			TParm temp = new TParm();
			temp.setData("Visible", false);
			data.setData("OTABLE", temp.getData());
			data.setData("OTABLE_", otParm.getData());
			data.setData("FTABLE", temp.getData());
			data.setData("FTABLE_", ftParm.getData());
			data.setData("ITABLE", temp.getData());
			data.setData("ITABLE_", itParm.getData());
			data.setData("ETABLE", temp.getData());
			data.setData("ETABLE_", etParm.getData());
			// ����ҽ�����ࡢ����ִ�е����ӵ�����Ϣ�� =======end$$$

			TParm drParm = new TParm(TJDODBTool.getInstance()
					.select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" + Operator.getID() + "'"));
			data.setData("printer", "TEXT", drParm.getValue("USER_NAME", 0));
			data.setData("printTime", "TEXT",
					SystemTool.getInstance().getDate().toString().substring(0, 17).replace(" ", "��")
							.replaceFirst("-", "��").replaceFirst("-", "��").replaceFirst(":", "ʱ")
							.replaceFirst(":", "��"));

			this.openPrintWindow("%ROOT%\\config\\prt\\inw\\INWExecUDSheetPrt.jhw", data);

			return;
		}

		// ����ҽ��ִ�е������ܣ�
		else if (((TRadioButton) getComponent("ord1UD")).isSelected() && b) {
			data.setData("TITLE", "TEXT", "����ҽ��ִ�е������ܣ�");
			for (int i = 0; i < parm.getCount(); i++) {
				tableParm.addData("LINK_NO", parm.getData("LINK_NO", i));
				// modify by guoy 20151123 ------start---------
				if ("Y".equals(parm.getData("DISPENSE_FLG", i))) {
					tableParm.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i) + "(��)");
				} else {
					tableParm.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
				}
				TParm unitParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '" + parm.getData("MEDI_UNIT", i) + "'"));
				tableParm.addData("MEDI_QTY", parm.getData("MEDI_QTY", i) + unitParm.getValue("UNIT_CHN_DESC", 0));
				// modify by guoy 20151123 ------end---------
				TParm routeParm = new TParm(
						TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
								+ parm.getData("ROUTE_CODE", i) + "'"));
				tableParm.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
				tableParm.addData("FREQ", getFreDesc((String) parm.getData("FREQ_CODE", i)));

				// �÷�ʱ����ʾmodified by wangqing ==== start$$$
				// tableParm.addData("TIMES",
				// TXNewATCTool.getTimeDetail(parm.getData("FREQ_CODE", i) + "").replace("-",
				// ","));
				String[] times = TXNewATCTool.getTimeDetail(parm.getData("FREQ_CODE", i) + "").split("-");
				String times_ = "";
				for (int j = 0; j < times.length; j++) {
					if (j < times.length - 1) {
						times_ += times[j] + "\r\n";
					} else {
						times_ += times[j];
					}
				}
				tableParm.addData("TIMES", times_);
				// �÷�ʱ����ʾmodified by wangqing ==== end$$$

				tableParm.addData("ORDER_DATE",
						parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
				TParm orderDrParm = new TParm(
						TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"
								+ parm.getData("ORDER_DR_CODE", i) + "'"));
				tableParm.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));

				String dcDate = "";
				if (parm.getData("DC_DATE", i) != null) {
					dcDate = parm.getData("DC_DATE", i).toString().replace("-", "/").substring(0, 16);
				}
				tableParm.addData("DC_DATE", dcDate);
				tableParm.addData("DR_NOTE", parm.getData("DR_NOTE", i));

				// ����
				if (parm.getDouble("INFLUTION_RATE", i) > 0) {
					tableParm.addData("INFLUTION_RATE", parm.getData("INFLUTION_RATE", i));
				} else {
					tableParm.addData("INFLUTION_RATE", "");
				}

				data.setData("BEDNO", "TEXT", parm.getData("BED_NO", i));
			}

			tableParm.setCount(parm.getCount());
			tableParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			tableParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			tableParm.addData("SYSTEM", "COLUMNS", "ROUTE");

			// ����
			tableParm.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");

			tableParm.addData("SYSTEM", "COLUMNS", "FREQ");
			tableParm.addData("SYSTEM", "COLUMNS", "TIMES");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
			tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DR");
			tableParm.addData("SYSTEM", "COLUMNS", "DC_DATE");
			tableParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");

			TParm drParm = new TParm(TJDODBTool.getInstance()
					.select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" + Operator.getID() + "'"));
			data.setData("printer", "TEXT", drParm.getValue("USER_NAME", 0));
			data.setData("printTime", "TEXT",
					SystemTool.getInstance().getDate().toString().substring(0, 17).replace(" ", "��")
							.replaceFirst("-", "��").replaceFirst("-", "��").replaceFirst(":", "ʱ")
							.replaceFirst(":", "��"));

			// ����ҽ�����ࡢ����ִ�е����ӵ�����Ϣ�� =======start$$$
			// ���ڱ���ײ�������⣬����ɾ��table�����Բ�������table��ʽ����ԭ����table����
			// ������ϵqing.wang@bluecore.com.cn
			// data.setData("TABLE", tableParm.getData());
			TParm temp = new TParm();
			temp.setData("Visible", false);
			data.setData("TABLE", temp.getData());
			data.setData("TABLE_", tableParm.getData());
			// ����ҽ�����ࡢ����ִ�е����ӵ�����Ϣ�� =======end$$$

			this.openPrintWindow("%ROOT%\\config\\prt\\inw\\INWExecUDTotal.jhw", data);

			/*
			 * TParm para = new TParm(); para.setData("FILE",
			 * "%ROOT%\\config\\prt\\inw\\INWExecUDTotal.jhw"); para.setData("DATA", data);
			 * 
			 * this.openWindow("%ROOT%\\config\\inw\\INWOrderSheetPrtXD.x", para);
			 */

			return;
		}

		else {
			this.messageBox("����ʱ�ͳ���ҽ�����ܴ�ӡִ�е���");
			return;
		}

	}

	public TParm getUDPrintTableParm(TParm parm) {
		TParm p = new TParm();
		p.setCount(parm.getCount());
		for (int i = 0; i < parm.getCount(); i++) {
			p.addData("LINK_NO", parm.getData("LINK_NO", i));
			// modify by guoy 20151123 ----start----------
			if ("Y".equals(parm.getData("DISPENSE_FLG", i))) {
				p.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i) + "(��)");
			} else {
				p.addData("ORDER_DESC", parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
			}
			TParm unitParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '" + parm.getData("MEDI_UNIT", i) + "'"));
			p.addData("MEDI_QTY", parm.getData("MEDI_QTY", i) + unitParm.getValue("UNIT_CHN_DESC", 0));
			// modify by guoy 20151123 ------end---------
			TParm routeParm = new TParm(
					TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
							+ parm.getData("ROUTE_CODE", i) + "'"));
			p.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
			p.addData("FREQ", getFreDesc((String) parm.getData("FREQ_CODE", i)));
			// p.addData("FREQ", parm.getData("FREQ_CODE", i));
			// �÷�ʱ����ʾmodified by wangqing ==== start$$$
			// p.addData("TIMES", TXNewATCTool.getTimeDetail(parm.getData("FREQ_CODE", i) +
			// "").replace("-", " "));
			String[] times = TXNewATCTool.getTimeDetail(parm.getData("FREQ_CODE", i) + "").split("-");
			String times_ = "";
			for (int j = 0; j < times.length; j++) {
				if (j < times.length - 1) {
					times_ += times[j] + "\r\n";
				} else {
					times_ += times[j];
				}
			}
			p.addData("TIMES", times_);
			// �÷�ʱ����ʾmodified by wangqing ==== end$$$
			TParm orderDrParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" + parm.getData("ORDER_DR_CODE", i) + "'"));
			p.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
			p.addData("DR_NOTE", parm.getData("DR_NOTE", i));
			p.addData("TIMES1", parm.getData("ORDER_DATE", i).toString().replace("-", "/").substring(0, 16));

			String dcDate = "";
			if (parm.getData("DC_DATE", i) != null) {
				dcDate = parm.getData("DC_DATE", i).toString().replace("-", "/").substring(0, 16);
			}
			p.addData("DC_DATE", dcDate);
			p.addData("EXEC_DR", "");

			// ����
			if (parm.getDouble("INFLUTION_RATE", i) > 0) {
				p.addData("INFLUTION_RATE", parm.getData("INFLUTION_RATE", i));
			} else {
				p.addData("INFLUTION_RATE", "");
			}

		}
		p.addData("SYSTEM", "COLUMNS", "LINK_NO");
		p.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		p.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		p.addData("SYSTEM", "COLUMNS", "ROUTE");

		// ����
		p.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");

		p.addData("SYSTEM", "COLUMNS", "FREQ");
		p.addData("SYSTEM", "COLUMNS", "TIMES");
		p.addData("SYSTEM", "COLUMNS", "ORDER_DR");
		p.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		p.addData("SYSTEM", "COLUMNS", "TIMES1");
		p.addData("SYSTEM", "COLUMNS", "DC_DATE");
		p.addData("SYSTEM", "COLUMNS", "EXEC_DR");

		return p;
	}

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\INWOrderCheckMain.x");
	}

	/*
	 * ����TTable��Ԫ����ɫ add by yangjj
	 */
	class MyTableCellRenderer extends TTableCellRenderer {

		private List<Integer> rowList;

		private List<Integer> colList;

		private Color color;

		public MyTableCellRenderer(TTableBase table, List<Integer> rowList, List<Integer> colList, Color color) {
			super(table);
			this.rowList = rowList;
			this.colList = colList;
			this.color = color;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component component = getComponent(row, column);
			// ��������ĺ���λ��
			setComponentHorizontalAlignment(component, value, row, column);

			// ���������ǰ����ɫ
			setComponentForeColor(component, isSelected, row, column);
			// ��������ı�����ɫ
			setComponentBackColor(component, isSelected, hasFocus, row, column);
			// �������ֵ
			if ((rowList.contains(row)) && (column == 16)) {
				setComponentValue(component, value, row, 16, color);
			} else {
				setComponentValue(component, value, row, column);
			}
			// �����������
			setComponentFocus(component, hasFocus);
			return component;
		}

		/**
		 * �������ֵ
		 * 
		 * @param component
		 *            Component
		 * @param value
		 *            Object
		 * @param row
		 *            int
		 * @param column
		 *            int
		 */
		public void setComponentValue(Component component, Object value, int row, int column, Color color) {
			if (value instanceof TNull)
				value = "";
			if (component instanceof JLabel) {
				JLabel label = (JLabel) component;
				String type = getType(row, column);
				String sValue = getValue(type, value, column);
				label.setText(sValue);
				label.setForeground(color);
				return;
			}
			if (component instanceof JCheckBox) {
				JCheckBox checkBox = (JCheckBox) component;
				checkBox.setSelected(TCM_Transform.getBoolean(value));
			}
		}

		/**
		 * ��������ĺ���λ��
		 * 
		 * @param component
		 *            Component
		 * @param value
		 *            Object
		 * @param row
		 *            int
		 * @param column
		 *            int
		 */
		public void setComponentHorizontalAlignment(Component component, Object value, int row, int column) {
			int alignment = 0;
			String s = getTable().getHorizontalAlignment(row, getTable().getColumnModel().getColumnIndex(column));
			// s = "right";
			System.out.println("s:" + s);
			if (s != null && s.length() > 0)
				alignment = StringTool.horizontalAlignment(s);
			else
				alignment = getColumnHorizontalAlignment(getTable().getColumnModel().getColumnIndex(column));

			if (component instanceof JCheckBox) {
				((JCheckBox) component).setHorizontalAlignment(alignment);
				return;
			}
			if (component instanceof JLabel) {
				((JLabel) component).setHorizontalAlignment(alignment);
				return;
			}
		}
	}

	/**
	 * �����ӡ
	 */
	public void onMedApplyPrint() {// wanglong add 20150519
		TParm outsideParm = this.getInputParm();
		if (outsideParm == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "I");
		parm.setData("MR_NO", outsideParm.getValue("INW", "MR_NO"));
		parm.setData("CASE_NO", outsideParm.getValue("INW", "CASE_NO"));
		parm.setData("IPD_NO", outsideParm.getValue("INW", "IPD_NO"));
		parm.setData("PAT_NAME", outsideParm.getValue("INW", "PAT_NAME"));
		parm.setData("BED_NO", outsideParm.getValue("INW", "BED_NO"));
		parm.setData("DEPT_CODE", outsideParm.getValue("INW", "DEPT_CODE"));
		parm.setData("STATION_CODE", outsideParm.getValue("INW", "STATION_CODE"));
		parm.setData("ADM_DATE", outsideParm.getData("INW", "ADM_DATE"));
		parm.setData("POPEDEM", "1");
		this.openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
	}
}
