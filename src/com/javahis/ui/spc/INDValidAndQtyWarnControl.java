package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import java.awt.event.KeyEvent;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Calendar;
import java.util.Date;
import com.dongyang.util.TypeTool;

import jdo.spc.INDSQL;
import jdo.spc.IndValidAndQtyWarnTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>
 * Title: ��Ч�ڼ��������ʾ
 * </p>
 * 
 * <p>
 * Description: ��Ч�ڼ��������ʾ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2010.10.28
 * @version 1.0
 */
public class INDValidAndQtyWarnControl extends TControl {

	private TPanel panel_0;

	private TTable table_a;

	private TTable table_b;

	public INDValidAndQtyWarnControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ע�ἤ��SYSFeePopup�������¼�
		callFunction("UI|ORDER_CODE_A|addEventListener", "ORDER_CODE_A->"
				+ TKeyListener.KEY_PRESSED, this, "onCreateEditComoponentUD_A");
		// ע�ἤ��SYSFeePopup�������¼�
		callFunction("UI|ORDER_CODE_B|addEventListener", "ORDER_CODE_B->"
				+ TKeyListener.KEY_PRESSED, this, "onCreateEditComoponentUD_B");
		panel_0 = getPanel("TPanel_0");
		table_a = getTable("TABLE_A");
		table_b = getTable("TABLE_B");
		String dept_code = Operator.getDept();
		// String station_code = Operator.getStation();
		// TParm resultParm = null;
		// String flg = "Y";//�ж���������ҩ�ⲿ�������޶�Ӧ���ݣ�û�оͲ�������
		// TParm deptParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(Operator.getRegion())));
		// for (int i = 0; i < deptParm.getCount(); i++) {
		// if (dept_code==deptParm.getValue("ORG_CODE")) {
		// flg = "N";
		// break;
		// }
		// }
		// // ��ѯ��������
		// if ("Y".equals(flg)) {
		// resultParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(station_code,
		// Operator.getRegion())));
		// }else {
		// resultParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(dept_code,
		// Operator.getRegion())));
		// }
		// String dept_desc = resultParm.getValue("ORG_CHN_DESC",0);
		this.setValue("ORG_CODE_A", dept_code);
		this.setValue("ORG_CODE_B", dept_code);
		// identify by shendr 20131230 ������
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		int deaf_mon = result.getInt("DEAF_MON", 0);
		setValue("DEAF_MON", deaf_mon + "");
		// onQuery(); by liyh 20120810 ȥ����ʼ����ѯ
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		int deaf_mon = this.getValueInt("DEAF_MON");

		if (panel_0.isShowing()) {
			// ���Ŵ���
			org_code = getValueString("ORG_CODE_A");
			if (org_code == null || org_code.length() <= 0) {
				this.messageBox("��ѡ���ѯ����");
				return;
			}
			parm.setData("ORG_CODE", org_code);
			String date = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyyMMdd");
			// fux modify 20150911
			// ��������
			if (getRadioButton("VALID_DATE_B").isSelected()) {
				deaf_mon = 6;
				parm.setData("VALID_DATE", rollMonth(date.substring(0, 6), date
						.substring(6, 8), deaf_mon));
			} else if (getRadioButton("VALID_DATE_A").isSelected()) {
				deaf_mon = 3;
				parm.setData("VALID_DATE", rollMonth(date.substring(0, 6), date
						.substring(6, 8), deaf_mon));
			} else {
				String valid_date = getValueString("VALID_DATE");
				parm.setData("VALID_DATE", valid_date.substring(0, 4)
						+ valid_date.substring(5, 7)
						+ valid_date.substring(8, 10));
			}
			// messageBox(""+rollMonth(date.substring(0, 6), date.substring(6,
			// 8), deaf_mon));
			// ҩƷ����
			order_code = getValueString("ORDER_CODE_A");
			if (order_code != null && order_code.length() > 0) {
				parm.setData("ORDER_CODE", order_code);
			}
			// ��Ч�ڲ�ѯ
			result = IndValidAndQtyWarnTool.getInstance().onQueryValid(parm);
			if (result == null || result.getCount("ORDER_CODE") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_a.removeRowAll();
				return;
			}
			table_a.setParmValue(result);
		} else {
			// ���Ŵ���
			org_code = getValueString("ORG_CODE_B");
			if (org_code == null || org_code.length() <= 0) {
				this.messageBox("��ѡ���ѯ����");
				return;
			}
			parm.setData("ORG_CODE", org_code);
			// ҩƷ����
			order_code = getValueString("ORDER_CODE_B");
			if (order_code != null && order_code.length() > 0) {
				parm.setData("ORDER_CODE", order_code);
			}
			// �����
			if (getRadioButton("STOCK_QTY_A").isSelected()) {
				parm.setData("STOCK_QTY_A", "STOCK_QTY_A");
			} else if (getRadioButton("STOCK_QTY_B").isSelected()) {
				parm.setData("STOCK_QTY_B", "STOCK_QTY_B");
			} else {
				parm.setData("STOCK_QTY_C", getValue("STOCK_QTY_C"));
			}
			// �������ѯ
			result = IndValidAndQtyWarnTool.getInstance().onQueryQty(parm);
			if (result == null || result.getCount("ORDER_CODE") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_b.removeRowAll();
				return;
			}
			table_b.setParmValue(result);
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		if (panel_0.isShowing()) {
			getRadioButton("VALID_DATE_B").setSelected(true);
			getTextFormat("VALID_DATE").setEnabled(false);
			getTextField("DEAF_MON").setEnabled(true);
			this.clearValue("VALID_DATE;ORG_CODE_A;ORDER_CODE_A;ORDER_DESC_A");
			TParm parmNulla = new TParm();
			table_a.setParmValue(parmNulla);
			// table_a.removeRowAll();
		} else {
			getRadioButton("STOCK_QTY_C").setSelected(true);
			this.clearValue("ORG_CODE_B;ORDER_CODE_B;ORDER_DESC_B");
			TParm parmNullb = new TParm();
			table_b.setParmValue(parmNullb);
			// table_b.removeRowAll();
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		int deaf_mon = result.getInt("DEAF_MON", 0);
		setValue("DEAF_MON", deaf_mon + "");
	}

	/**
	 * �����ѡ��
	 */
	public void onChangeRadioButton() {
		if (getRadioButton("VALID_DATE_C").isSelected()) {
			getTextFormat("VALID_DATE").setEnabled(true);
		} else {
			getTextFormat("VALID_DATE").setEnabled(false);
			this.clearValue("VALID_DATE");
		}
		if (getRadioButton("VALID_DATE_B").isSelected()) {
			getTextField("DEAF_MON").setEnabled(true);
		} else {
			getTextField("DEAF_MON").setEnabled(false);
		}
	}

	/**
	 * ��TextField�����༭�ؼ�ʱ����
	 * 
	 * @param com
	 */
	public void onCreateEditComoponentUD_A(KeyEvent obj) {
		TTextField textFilter = getTextField("ORDER_CODE_A");
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn_A");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn_A(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE_A").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC_A").setValue(order_desc);
	}

	/**
	 * ��TextField�����༭�ؼ�ʱ����
	 * 
	 * @param com
	 */
	public void onCreateEditComoponentUD_B(KeyEvent obj) {
		TTextField textFilter = getTextField("ORDER_CODE_B");
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn_B");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn_B(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE_B").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC_B").setValue(order_desc);
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
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * �õ�TPanel����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TPanel getPanel(String tagName) {
		return (TPanel) getComponent(tagName);
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
	 * ����ָ�����·ݺ������Ӽ�������Ҫ���·ݺ�����
	 * 
	 * @param Month
	 *            String �ƶ��·� ��ʽ:yyyyMM
	 * @param Day
	 *            String �ƶ��·� ��ʽ:dd
	 * @param num
	 *            String �Ӽ������� ����Ϊ��λ
	 * @return String
	 */
	public String rollMonth(String Month, String Day, int num) {
		if (Month.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(Month, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// ��ǰ�£�num
		cal.add(cal.MONTH, num);
		// ���¸���1����Ϊ���ڳ�ʼֵ
		cal.set(cal.DATE, 1);
		Timestamp month = new Timestamp(cal.getTimeInMillis());
		String result = StringTool.getString(month, "yyyyMM");
		String lastDayOfMonth = getLastDayOfMonth(result);
		if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
			result += lastDayOfMonth;
		} else {
			result += Day;
		}
		return result;
	}

	/**
	 * ��ȡָ���·ݵ����һ�������
	 * 
	 * @param date
	 *            String ��ʽ YYYYMM
	 * @return Timestamp
	 */
	public String getLastDayOfMonth(String date) {
		if (date.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// ��ǰ�£�1�����¸���
		cal.add(cal.MONTH, 1);
		// ���¸���1����Ϊ���ڳ�ʼֵ
		cal.set(cal.DATE, 1);
		// �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
		cal.add(cal.DATE, -1);
		Timestamp result = new Timestamp(cal.getTimeInMillis());
		return StringTool.getString(result, "dd");
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		TTable table = new TTable();
		if (panel_0.isShowing())
			table = table_a;
		else
			table = table_b;
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "��Ч�ڼ��������ʾ");
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TTable table = new TTable();
		if (panel_0.isShowing()) {
			table = table_a;
			if (table.getRowCount() <= 0) { 
				this.messageBox("û�д�ӡ����");
				return;
			}
			// ��ӡ����  
			TParm date = new TParm();
			// ��ͷ����  
			if(getRadioButton("VALID_DATE_A").isSelected()){
				date.setData("TITLE", "TEXT",  "ҩƷ��Ч����ʾ����(������)");
			}else if(getRadioButton("VALID_DATE_B").isSelected()){
				date.setData("TITLE", "TEXT",  "ҩƷ��Ч����ʾ����(������)");  
			}else{
				date.setData("TITLE", "TEXT",  "ҩƷ��Ч����ʾ����(��ֹ��" + ""+this.getValueString("VALID_DATE").substring(0, 10)+")");
			}
			    
//			String start_date = getValueString("START_DATE");
//			String end_date = getValueString("END_DATE");
//			date.setData("DATE_AREA", "TEXT", "ͳ������: "
//					+ start_date.substring(0, 4) + "/"
//					+ start_date.substring(5, 7) + "/"
//					+ start_date.substring(8, 10) + " "
//					+ start_date.substring(11, 13) + ":"
//					+ start_date.substring(14, 16) + ":"
//					+ start_date.substring(17, 19) + " ~ "
//					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
//					+ "/" + end_date.substring(8, 10) + " "
//					+ end_date.substring(11, 13) + ":"
//					+ end_date.substring(14, 16) + ":"    
//					+ end_date.substring(17, 19));
			date.setData("DATE", "TEXT", "�Ʊ�����: "      
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			String orgDesc = getOrg(this.getValueString("ORG_CODE_A"));
			date.setData("ORG_CODE", "TEXT", "����: " +orgDesc );
			
			// �������
			TParm parm = new TParm();
			TParm tableParm = table.getParmValue();
			// ORDER_CODE;ORDER_DESC;SPECIFICATION;DOSE_CHN_DESC;STOCK_QTY;UNIT_CHN_DESC;BATCH_NO;VALID_DATE;SUP_CHN_DESC
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));  
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", tableParm.getValue(
						"SPECIFICATION", i));
				parm.addData("DOSE_CHN_DESC", tableParm.getValue(
						"DOSE_CHN_DESC", i));
				parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
				parm.addData("UNIT_CHN_DESC", tableParm.getValue(
						"UNIT_CHN_DESC", i));
				parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", i));
				parm.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).subSequence(0, 10));
				parm.addData("SUP_CHN_DESC", tableParm.getValue("SUP_CHN_DESC",
						i));

			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "DOSE_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
			parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
			parm.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
			date.setData("TABLE", parm.getData());  
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\SPC\\INDVaildWarn.jhw",
					date);

		} else {
			table = table_b;
			if (table.getRowCount() <= 0) {
				this.messageBox("û�д�ӡ����");
				return;
			}
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			if(getRadioButton("STOCK_QTY_A").isSelected()){
				date.setData("TITLE", "TEXT",  "ҩƷ�������ʾ����(��߿����)");
			}else if(getRadioButton("STOCK_QTY_B").isSelected()){
				date.setData("TITLE", "TEXT",  "ҩƷ�������ʾ����(��Ϳ����)");  
			}else{  
				date.setData("TITLE", "TEXT",  "ҩƷ�������ʾ����(��ȫ�����)");
			}
			//date.setData("TITLE", "TEXT", "ҩƷ�������ʾ����");
//			String start_date = getValueString("START_DATE");
//			String end_date = getValueString("END_DATE");
//			date.setData("DATE_AREA", "TEXT", "ͳ������: "  
//					+ start_date.substring(0, 4) + "/"  
//					+ start_date.substring(5, 7) + "/"
//					+ start_date.substring(8, 10) + " "
//					+ start_date.substring(11, 13) + ":"
//					+ start_date.substring(14, 16) + ":"
//					+ start_date.substring(17, 19) + " ~ "
//					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
//					+ "/" + end_date.substring(8, 10) + " "
//					+ end_date.substring(11, 13) + ":"
//					+ end_date.substring(14, 16) + ":"  
//					+ end_date.substring(17, 19));  
			date.setData("DATE", "TEXT", "�Ʊ�����: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			String orgDesc = getOrg(this.getValueString("ORG_CODE_A"));
			date.setData("ORG_CODE", "TEXT", "����: " +orgDesc );
			// �������  
			TParm parm = new TParm();  
			TParm tableParm = table.getParmValue();
			// ORDER_CODE;ORDER_DESC;SPECIFICATION;STOCK_QTY;UNIT_CHN_DESC;MAX_QTY;MIN_QTY;SAFE_QTY
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", tableParm.getValue(
						"SPECIFICATION", i));
				parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
				parm.addData("UNIT_CHN_DESC", tableParm.getValue(  
						"UNIT_CHN_DESC", i));
				parm.addData("MAX_QTY", tableParm.getValue("MAX_QTY", i));
				parm.addData("MIN_QTY", tableParm.getValue("MIN_QTY", i));
				parm.addData("SAFE_QTY", tableParm.getValue("SAFE_QTY", i));
			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");  
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MAX_QTY");
			parm.addData("SYSTEM", "COLUMNS", "MIN_QTY");
			parm.addData("SYSTEM", "COLUMNS", "SAFE_QTY");
			date.setData("TABLE", parm.getData());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\SPC\\INDVaildQty.jhw",
					date);
		}

	}

	private String getOrg(String valueString) {
		String sql = " SELECT ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE = '"+valueString+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ORG_CHN_DESC",0);
	}

}
