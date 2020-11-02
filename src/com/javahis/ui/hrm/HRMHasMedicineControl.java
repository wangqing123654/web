package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��������ͳ��
 * </p>
 * 
 * <p>
 * Description: ��ҩƷ������컼�߲�ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author caowl 2012.06.19
 * @version 1.0
 */

public class HRMHasMedicineControl extends TControl {

	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ��ʼ������
	 * 
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("S_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("E_DATE", rollDay);
		// ��ʼ�������ź�����Ϊ��
		this.setValue("MR_NO", "");
		this.setValue("PAT_NAME", "");

		// �Ƴ�������������
		this.callFunction("UI|Table|removeRowAll");
		// ��ʼ����
		TTable table = (TTable) this.getComponent("Table");

	}

	/**
	 * ��ѯ
	 * */
	public void onQuery() {
		// �ӽ����ȡ��ʼʱ��ͽ���ʱ��
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");

		TParm selParm = new TParm();
		String mrNo = this.getValueString("MR_NO");
		String mrWhere = "";
		if (!StringUtil.isNullString(mrNo)) {
			
				mrWhere = " AND B.MR_NO= '"+mrNo+"' ";
		}
		if (getValue("PAT_NAME") != null) {
			if (getValue("PAT_NAME").toString().length() != 0)
				mrWhere += " AND B.PAT_NAME= '"+this.getValueString("PAT_NAME")+"' ";
		}		
		String sql = "SELECT DISTINCT B.COMPANY_CODE,B.CONTRACT_CODE,B.MR_NO,B.PAT_NAME," +
				"(CASE B.COVER_FLG WHEN 'Y' THEN '����' WHEN 'N' THEN 'δ��' ELSE '����' END) FLAG " +
				"FROM HRM_ORDER A, HRM_CONTRACTD B " + 
				"WHERE A.MR_NO = B.MR_NO " +
				"AND A.CAT1_TYPE='PHA' " +
				mrWhere +
				"AND BILL_DATE BETWEEN TO_DATE ('"+startTime+"','yyyymmdd hh24:mi:ss') " +
				"AND TO_DATE ('"+endTime+"','yyyymmdd hh24:mi:ss') " +
				"ORDER BY B.MR_NO";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (selParm.getCount("MR_NO") < 1) {
			// ��������
			this.messageBox("E0008");
			this.initPage();
			
		}		
		//this.get
		this.callFunction("UI|Table|setParmValue", selParm);
	}

	/**
	 * ���
	 * */
	public void onClear() {
		initPage();
		this.clearValue("MR_NO;PAT_NAME");
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
	 * ��܇�¼�
	 */ 
	public   void  onEnter(){
		// ��������
		Pat pat= Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
	}

}
