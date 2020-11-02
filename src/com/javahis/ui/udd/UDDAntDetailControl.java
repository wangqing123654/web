/**
 * @className UDDAntDetailControl.java 
 * @author yanjing
 * @Date 2013-3-28 
 * @version V 1.0 
 */
package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDNewTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ʹ�ÿ���ҩ��ĳ�Ժ������ϸ
 * </p>
 * 
 * <p>
 * Description: ʹ�ÿ���ҩ��ĳ�Ժ������ϸ
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
public class UDDAntDetailControl extends TControl {
	private TTable table;
	private TTextField MR_NO;
	private TTextField NAME;
	private TNumberTextField TOT; // �ܼ�

	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.setValue("DEPT_CODE", "");
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("TOT", "0.00");
		// this.setValue("REGION_CODE", "H01");
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ��ʼ����ѯ��ʱ,��ʱ
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool
				.getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		String end_day = StringTool.getString(rollDay, "yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
		// ���ó���ҩ�ﵯ���˵�
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
		try {
			d = defaultFormatter.parse(dateStr);
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
	 * �õ�TABLE����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * ����MR_NO
	 */
	public void onMrNo() {
		MR_NO = (TTextField) this.getComponent("MR_NO");
		String mrNo = MR_NO.getValue();
		MR_NO.setValue(PatTool.getInstance().checkMrno(mrNo));
		// �õ���������
		getPatName(mrNo);
	}

	/**
	 * ��øò��˵�����
	 * 
	 * @param mrNo
	 *            String
	 */
	private void getPatName(String mrNo) {
		NAME = (TTextField) this.getComponent("NAME");
		NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));
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
		if (null!=this.getValue("DS_FLG") &&this.getValue("DS_FLG").toString().length()>0) {
			if (this.getValue("DS_FLG").equals("1")) {
				selAccountData.setData("DS_FLG", "Y");
			}else if(this.getValue("DS_FLG").equals("2")) {
				selAccountData.setData("DS_FLG", "N");
			}
		}
		if (null!=this.getValue("MR_NO")&&this.getValue("MR_NO").toString().length()>0) {
			selAccountData.setData("MR_NO", this.getValue("MR_NO"));
		}
		if (null!=this.getValue("ORDER_CODE")&&this.getValue("ORDER_CODE").toString().length()>0) {
			selAccountData.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
		}
		if (null!=this.getValue("DEPT_CODE")&&this.getValue("DEPT_CODE").toString().length()>0) {
			selAccountData.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		}
		selAccountData.setData("START_DATE", startTime);
		selAccountData.setData("END_DATE", endTime);
		result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
		if (result.getCount() <= 0) {
			this.messageBox("û��Ҫ��ѯ������");
			table.setParmValue(new TParm());
			return;
		}

		// �ܽ��
		double totalAmt = 0.0;
		int count = result.getCount();
		TOT = (TNumberTextField) this.getComponent("TOT");
		// ѭ���ۼ�
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for (int i = 0; i < count; i++) {
			double temp = result.getDouble("SUM_AMT", i);
			totalAmt += temp;
			Timestamp datetemp = result.getTimestamp("BIRTH_DATE", i) == null ? sysDate
					: result.getTimestamp("BIRTH_DATE", i);
			// ��������
			String age = "0";
			if (result.getTimestamp("BIRTH_DATE", i) != null) {
				/*
				 * age = OdiUtil.getInstance().showAge(temp,
				 * parm.getTimestamp("IN_DATE", i));
				 */
				age = DateUtil.showAge(datetemp, sysDate);
			} else {
				age = "";
			}
			result.setData("GRADE", i, age);
			// sumDispenseQty+=result.getInt("SUM_QTY", i);
			result.setData("SUM_QTY", i, result.getInt("SUM_QTY", i));
		}
		TOT.setValue(totalAmt);
		result.setData("REGION_CHN_DESC", count, "�ܼ�:");
		result.setData("MR_NO", count, "");
		result.setData("NAME", count, "");
		result.setData("SEX_CODE", count, "");
		result.setData("GRADE", count, "");
		result.setData("CASE_NO", count, "");
		result.setData("DEPT_DESC", count, "");
		result.setData("DS_DATE", count, "");
		result.setData("DAYS", count, "");
		result.setData("ORDER_CODE", count, "");
		result.setData("ORDER_DESC", count, "");
		result.setData("SPECIFICATION", count, "");
		result.setData("UNIT_DESC", count, "");
		result.setData("OWN_PRICE", count, "");
		result.setData("SUM_QTY", count, "");
		result.setData("SUM_AMT", count, totalAmt);
		result.setData("IN_DATE", count, "");
		result.setData("MAINDIAG", count, "");
//		table = getTable("TABLE");
	      table.setParmValue(result);
			

		// ����table�ϵ�����
//		this.callFunction("UI|TABLE|setParmValue", result);
	}

	/**
	 * ���excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ���������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "ʹ�ÿ���ҩ���Ժ������ϸ");
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		String clear = "DEPT_CODE;ORDER_CODE;ORDER_DESC;TOT;MR_NO;NAME";
		this.clearValue(clear);
		TTable table = (TTable) this.getComponent("Table");
		table.setParmValue(new TParm());
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool
				.getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		String end_day = StringTool.getString(rollDay, "yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);

	}
}
