package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDKJGatherTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: סԺ��ҩ��ʹ���������
 * </p>
 * 
 * <p>
 * Description: סԺ��ҩ��ʹ���������
 * </p>
 * 
 * <p>
 * Copyright: Bluecore
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author yanjing
 * @version 1.0
 */
public class UDDKJGatherControl extends TControl {
	private TTable table;
	private TNumberTextField TOT;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.setValue("TOT", "0.00");
		initPage();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ��ʼ��ͳ������ʱ��
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool
				.getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		String end_day = StringTool.getString(rollDay, "yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);

		// ���õ����˵�
		TParm parmIn = new TParm();
		parmIn.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * �õ��ϸ���
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		// System.out.println("9999999"+d);
		try {
			d = defaultFormatter.parse(dateStr);
			// System.out.println("9999999"+d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}

	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}

	/**
	 * ���Table����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		setValue("REGION_CODE", Operator.getRegion());

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if ("".equals(this.getValue("START_DATE"))
				|| this.getValue("START_DATE") == null) {
			this.messageBox("��ʼʱ�䲻��Ϊ�գ�");
			return;
		} else if ("".equals(this.getValue("END_DATE"))
				|| this.getValue("END_DATE") == null) {
			this.messageBox("����ʱ�䲻��Ϊ�գ�");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy/MM/dd HH:mm:ss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy/MM/dd HH:mm:ss");
		TParm result = new TParm();
		TParm selAccountData = new TParm();
		if (this.getValue("ORDER_CODE").equals("")) {
			selAccountData.setData("REGION_CODE", this.getValue("REGION_CODE"));
			selAccountData.setData("START_DATE", startTime);
			selAccountData.setData("END_DATE", endTime);
			result = UDDKJGatherTool.getInstance().selectInformation2(
					selAccountData);
		} else {
			selAccountData.setData("REGION_CODE", this.getValue("REGION_CODE"));
			selAccountData.setData("START_DATE", startTime);
			selAccountData.setData("END_DATE", endTime);
			selAccountData.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
			selAccountData.setData("ORDER_DESC", this.getValue("ORDER_DESC"));
			result = UDDKJGatherTool.getInstance().selectInformation2(
					selAccountData);
		}
		if (result.getCount() <= 0) {
			this.messageBox("û��Ҫ��ѯ������");
			table.removeRowAll();
			return;
		}
		// �ܽ��
		double totalAmt = 0.0;
		int count = result.getCount();
		int sumDispenseQty=0;//����
		TOT = (TNumberTextField) this.getComponent("TOT");
		// ѭ���ۼ�
		for (int i = 0; i < count; i++) {
			double temp = result.getDouble("SUM_AMT", i);
			totalAmt += temp;
			sumDispenseQty+=result.getInt("SUM_QTY", i);
			result.setData("SUM_QTY", i, result.getInt("SUM_QTY", i));
		}
		TOT.setValue(totalAmt);
		result.setData("REGION_CHN_DESC", count, "�ܼ�:");
		result.setData("ORDER_CODE", count, "");
		result.setData("ORDER_DESC", count, "");
		result.setData("SPECIFICATION", count, "");
		result.setData("UNIT_DESC", count, "");
		result.setData("OWN_PRICE", count, "");
		result.setData("SUM_QTY", count, sumDispenseQty);
		result.setData("SUM_AMT", count, totalAmt);
		// ����table�ϵ�����
		this.callFunction("UI|TABLE|setParmValue", result);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ���������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "ʹ�ÿ���ҩ�����");
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		String clear = "ORDER_CODE;ORDER_DESC;TOT";
		this.clearValue(clear);
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool
				.getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		String end_day = StringTool.getString(rollDay, "yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
	}
}
