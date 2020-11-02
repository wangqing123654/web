package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
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
 * Title: ҩ��(�����ػ�����ҩƷ)����������
 * </p>
 * 
 * <p>
 * Description: ҩ��(�����ػ�����ҩƷ)����������
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
public class UDDSellOrderControl extends TControl {
	private TTable table;
	private TNumberTextField TOT;
	DecimalFormat   sf  =   new  DecimalFormat("##0.00"); 
	DecimalFormat   af  =   new  DecimalFormat("##0.0000"); 
	public UDDSellOrderControl() {
	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.setValue("ORDER_CODE", "");
		this.setValue("TOT", 0.00);
		initPage();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		//��ʼ����ѯ����
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
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
		this.setValue("DS_FLG", "3");
		
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		setValue("REGION_CODE", Operator.getRegion());
		// setValue("REE", Operator.getRegion());
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
//		System.out.println("9999999"+d);
		try {
			d = defaultFormatter.parse(dateStr);
//			System.out.println("9999999"+d);
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
		TParm selAccountData = new TParm();
		TParm result = new TParm();
		if (null!=this.getValue("ORDER_CODE") &&this.getValue("ORDER_CODE").toString().length()>0) {
			selAccountData.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
		}
		if (null!=this.getValue("DS_FLG") &&this.getValue("DS_FLG").toString().length()>0) {
			if (this.getValue("DS_FLG").equals("1")) {
				selAccountData.setData("DS_FLG", "Y");
			}else if(this.getValue("DS_FLG").equals("2")) {
				selAccountData.setData("DS_FLG", "N");
			}
		}
		selAccountData.setData("START_DATE", startTime);
		selAccountData.setData("END_DATE", endTime);
		if (null!=this.getValue("TYPE_CODE")&&this.getValue("TYPE_CODE").toString().length()>0) {
			boolean flg=false;
			switch (this.getValueInt("TYPE_CODE")) {
			case 1:
				flg=true;
				break;
			case 2:
				flg=true;
				selAccountData.setData("ANTIBIOTIC_CODE","02");
				break;
			case 3:
				flg=true;
				selAccountData.setData("ANTIBIOTIC_CODE","01");
				break;
			case 4:
				flg=true;
				selAccountData.setData("ANTIBIOTIC_CODE","03");
				break;
			default:
				flg=false;
				if (this.getValue("ADM_TYPE").equals("1")) {
					result = UDDTool.getInstance().getAllOrderOPD(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("2")) {
					result = UDDTool.getInstance().getAllOrderODI(selAccountData);
				}else
					result = UDDTool.getInstance().getAllOrder(selAccountData);
				break;
			}
			if (flg) {
				if (this.getValue("ADM_TYPE").equals("1")) {
					result = UDDTool.getInstance().getSellOrderOPD(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("2")) {
					result = UDDTool.getInstance().getSellOrderODI(selAccountData);
				}else
					result = UDDTool.getInstance().getSellOrder(selAccountData);	
			}
		}else{
			this.messageBox("��ѡ��ҩƷ�����࣡");
			return;
		}
		if (result.getErrCode()<0) {
			table.setParmValue(new TParm());
			this.messageBox("��ѯ��������");
			return;
		}
	
		if (result.getCount() <= 0) {
			this.messageBox("û��Ҫ��ѯ������");
			table.setParmValue(new TParm());
			//table.removeRowAll();
			return;
		}
		//System.out.println("resutlsdfsdfsdf::::"+result);
		// �ܽ��
		double totalAmt = 0.0;
		int count = result.getCount();
//		int sumDispenseQty = 0;// ����
		TOT = (TNumberTextField) this.getComponent("TOT");
		// ѭ���ۼ�
		double stockAmt = 0.00;
		for (int i = 0; i < count; i++) {
			//temp = result.getDouble("SUM_AMT", i);
			totalAmt += result.getDouble("SUM_AMT", i);
			stockAmt += result.getDouble("STOCK_AMT", i);
//			result.setData("SUM_QTY", i, result.getInt("STOCK_AMT", i));
		}
		TOT.setValue(totalAmt);
		result.addData("REGION_CHN_ABN", "�ܼ�:");
		result.addData("ORDER_CODE", "");
		result.addData("ORDER_DESC", "");
		result.addData("SPECIFICATION", "");
		result.addData("OWN_PRICE", "");
		result.addData("SUM_QTY", "");
		result.addData("SUM_AMT", totalAmt);
		result.addData("STOCK_PRICE", "");
		result.addData("STOCK_AMT",stockAmt);
		result.setCount(count+1);
		// ����table�ϵ�����
		table.setParmValue(result);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ���������");
			return;
		}
		if (this.getValue("TYPE_CODE").equals("1")) {
			ExportExcelUtil.getInstance().exportExcel(table, "����������������¼��");
		} else {
			ExportExcelUtil.getInstance().exportExcel(table, "ȫƷ������������¼��");
		}   
	}
	  
	/**
	 * ��ӡ
	 */
	public void onPrint() {    
		if (table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}
	
			if (table.getRowCount() <= 0) {
				this.messageBox("û�д�ӡ����");  
				return;  
			} 
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			if (this.getValue("TYPE_CODE").equals("1")) {
			date.setData("TITLE", "TEXT", "����������������¼��");
			} else{
			date.setData("TITLE", "TEXT", "ȫƷ������������¼��");
			}
			
			String start_date = getValueString("START_DATE");
			String end_date = getValueString("END_DATE");
			date.setData("DATE_AREA", "TEXT", "ͳ������: "
					+ start_date.substring(0, 4) + "/"
					+ start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " "
					+ start_date.substring(11, 13) + ":"
					+ start_date.substring(14, 16) + ":"
					+ start_date.substring(17, 19) + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " "
					+ end_date.substring(11, 13) + ":"
					+ end_date.substring(14, 16) + ":"  
					+ end_date.substring(17, 19));
			date.setData("DATE", "TEXT", "�Ʊ�ʱ��: "  
					+ SystemTool.getInstance().getDate().toString().substring(  
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			date.setData("REGION", "TEXT", "����: " +Operator.getHospitalCHNShortName());
			if(this.getValueString("ADM_TYPE").equals("1")){
				date.setData("ADMTYPE", "TEXT", "�ż�ס���:�ż���"  );  
			}else if(this.getValueString("ADM_TYPE").equals("2")){
				date.setData("ADMTYPE", "TEXT", "�ż�ס���:סԺ"  );  
			}  
			
			// �������    
			TParm parm = new TParm();
			TParm tableParm = table.getShowParmValue();  
			//REGION_CHN_ABN;ORDER_CODE;ORDER_DESC;SPECIFICATION;OWN_PRICE;SUM_QTY;SUM_AMT;STOCK_PRICE;STOCK_AMT
			
			for (int i = 0; i < table.getRowCount(); i++) {

				parm.addData("REGION_CHN_ABN", tableParm.getValue("REGION_CHN_ABN", i));
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue(
						"ORDER_DESC", i));    
				parm.addData("SPECIFICATION", tableParm.getValue(  
						"SPECIFICATION", i));
				parm.addData("STOCK_UNIT",tableParm.getValue(  
						"STOCK_UNIT", i));	  
				parm.addData("OWN_PRICE", af.format(tableParm.getDouble("OWN_PRICE", i)));
				parm.addData("SUM_QTY", sf.format(tableParm.getDouble(      
						"SUM_QTY", i)));
				parm.addData("SUM_AMT", sf.format(tableParm.getDouble("SUM_AMT", i)));
				parm.addData("STOCK_PRICE", af.format(tableParm.getDouble("STOCK_PRICE", i)));
				parm.addData("STOCK_AMT", sf.format(tableParm.getDouble("STOCK_AMT", i)));

			}
			///REGION_CHN_ABN;ORDER_CODE;ORDER_DESC;SPECIFICATION;OWN_PRICE;SUM_QTY;SUM_AMT;STOCK_PRICE;STOCK_AMT
			//����ҩƷ���룬ҩƷ���ƣ������������λ�����۵��ۣ����۽��ɹ����ۣ��ɹ����
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");  
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");  
			parm.addData("SYSTEM", "COLUMNS", "SUM_QTY");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");	
			parm.addData("SYSTEM", "COLUMNS", "SUM_AMT");    
			parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
			date.setData("TABLE", parm.getData());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\UDD\\UDDSellOrder.jhw",
					date);
			// ExportExcelUtil.getInstance().exportExcel(table_m,
			// "סԺҩƷ����ͳ�Ʊ�(������Ϣ)");
		
		
	}
	
	private String getOrg(String valueString) {
		String sql = " SELECT ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE = '"+valueString+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ORG_CHN_DESC",0);
	}
	
	
	/**
	 * ��ղ���
	 */
	public void onClear() {
		 String clear = "TYPE_CODE;ORDER_CODE;ORDER_DESC;TOT";
		 this.clearValue(clear);
		 this.setValue("TYPE_CODE", "3");
		TTable table = (TTable) this.getComponent("TABLE");
		table.setParmValue(new TParm());
		//table.removeRowAll();
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
		this.setValue("DS_FLG", "3");
	}

}
