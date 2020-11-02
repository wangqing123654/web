package com.javahis.ui.pha;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.bil.BILSysParmTool;
import jdo.pha.PHAOEStatisticsTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: �ţ���������ϸ�����ܱ���
 * </p>
 * 
 * <p>
 * Description: �ţ���������ϸ�����ܱ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangm 2013.3.12
 * @version 1.0
 */
public class PHAOEStatisticsControl extends TControl {
	private TTable table = new TTable();
	private String header = ""; // ��ű�ͷ
	private String parmMap = ""; // ��ű�ͷMap
	private String align = "";//�����ݶ��뷽ʽ
	private String reportFlg = ""; // �������ͱ��

	private StringBuffer startTime = new StringBuffer(); // ��ʼ����
	private StringBuffer endTime = new StringBuffer(); // ��ֹ����

	private TComboBox cmbHospital = new TComboBox(); // ����ҽԺ
	private TComboBox cmbType = new TComboBox(); // ��������
	
	private TTextField orderCode;//ҩƷ����
	private TTextField orderDesc;//ҩƷ����
	
	private TTextField mrNo;//������
	private TTextField name;//��������
	
	private TComboBox regionCode; //����
	private TNumberTextField total;//�ϼƽ��

	/**
	 * �����ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.resetDate(); // ��ʼ��ʱ��ؼ�
		this.initTable(); // ��ʼ��table
		this.initPage();  //��ʼ������
//		this.initPopeDem();		//��ʼ��Ȩ��
	}

	/**
	 * Ȩ�޳�ʼ��
	 */
//	private void initPopeDem() {
//		// �鳤Ȩ��
//		if (this.getPopedem("LEADER")) {
//			// callFunction("UI|DR_CODE|setEnabled",true);
//		}
//		// ȫԺȨ��
//		if (this.getPopedem("ALL")) {
//			// callFunction("UI|DEPT_CODE|setEnabled",true);
//			// callFunction("UI|DR_CODE|setEnabled",true);
//		}
//	}

	//��ʼ������
	private void initPage(){
		orderCode = (TTextField)this.getComponent("txt_OrderCode");
		orderDesc = (TTextField)this.getComponent("txt_OrderDesc");
		mrNo = (TTextField)this.getComponent("txt_MR_NO");
		mrNo.setEnabled(false);
		name = (TTextField)this.getComponent("txt_Name");
		regionCode = (TComboBox)this.getComponent("cbl_RegionCode");
		regionCode.setValue(Operator.getRegion());
		total = (TNumberTextField)this.getComponent("txt_Sum");
		this.callFunction("UI|cbl_RegionCode|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
	              getValueString("cbl_RegionCode")));
		// ע�ἤ��SYSFeePopup�������¼�
		orderCode.setPopupMenuParameter("TAG", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"));
        // ������ܷ���ֵ����
		orderCode.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
	}
	
	
	/**
	 * table��ʼ��
	 */
	private void initTable() {
		table = (TTable) this.getComponent("tab_Statistics");
		Map map = this.getTableHeader("1", "1");
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());
	}

	// table��䷽��
	private void fillTable(TParm parm) {
		table = new TTable();
		table = (TTable) this.getComponent("tab_Statistics");
		table.removeRowAll();

		Map map = this.getTableHeader(parm.getValue("HOSPITAL_ID"), parm
				.getValue("TYPE_ID"));
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());

		TParm result = new TParm();
		result = PHAOEStatisticsTool.getInstance().selectReportData(parm);
		
		
//		int qty = 0;	//����
		double dossum = 0;	//���۽��
		int count = result.getCount();
		if(parm.getValue("TYPE_ID").equals("1")){
			// �������_(��������) //����������� //Ժ��������� //Ժ�ڼ������
			
			for (int i = 0; i < count; i++) {
	            double temp = result.getDouble("OWN_AMT_SUM", i);
	            dossum += temp;
//	            qty+=result.getInt("DOSAGE_QTY_SUM", i);

	            double changeFormat = result.getDouble("OWN_PRICE", i);
	            DecimalFormat obj = new DecimalFormat("###########0.0000");
	            result.setData("OWN_PRICE", i, obj.format(changeFormat));
	        }
			total.setValue(dossum);
			
			result.setData("REGION_CODE", count, "�ܼ�:");
			result.setData("ORDER_CODE", count, "");
			result.setData("ORDER_DESC", count, "");
			result.setData("SPECIFICATION", count, "");
			result.setData("UNIT_CHN_DESC", count, "");
			result.setData("OWN_PRICE", count, "");
//			result.setData("DOSAGE_QTY_SUM", count, qty);
			result.setData("DOSAGE_QTY_SUM", count, "");
			result.setData("OWN_AMT_SUM", count, dossum);
		}else if((parm.getValue("HOSPITAL_ID").equals("1")||parm.getValue("HOSPITAL_ID").equals("3"))&&parm.getValue("TYPE_ID").equals("2")){
			// ������ϸ_(��������) //Ժ��������ϸ
			
			for (int i = 0; i < count; i++) {
	            double temp = result.getDouble("OWN_AMT", i);
	            dossum += temp;
//	            qty+=result.getInt("DOSAGE_QTY", i);

	            double changeFormat = result.getDouble("OWN_PRICE", i);
	            DecimalFormat obj = new DecimalFormat("###########0.0000");
	            result.setData("OWN_PRICE", i, obj.format(changeFormat));
	        }
			total.setValue(dossum);
			
			result.setData("REGION_CODE", count, "�ܼ�:");
			result.setData("ORDER_CODE", count, "");
			result.setData("ORDER_DESC", count, "");
			result.setData("BILL_DATE", count, "");
			result.setData("MR_NO", count, "");
			result.setData("PAT_NAME", count, "");
			result.setData("SPECIFICATION", count, "");
			result.setData("UNIT_CHN_DESC", count, "");
			result.setData("OWN_PRICE", count, "");
//			result.setData("DOSAGE_QTY", count, qty);
			result.setData("DOSAGE_QTY", count, "");
			result.setData("OWN_AMT", count, dossum);
			result.setData("USER_NAME", count, "");
			
		}else if(parm.getValue("HOSPITAL_ID").equals("2")&&parm.getValue("TYPE_ID").equals("2")){
			// ����������ϸ
			
			for (int i = 0; i < count; i++) {
	            double temp = result.getDouble("OWN_AMT", i);
	            dossum += temp;
//	            qty+=result.getInt("DOSAGE_QTY", i);

	            double changeFormat = result.getDouble("OWN_PRICE", i);
	            DecimalFormat obj = new DecimalFormat("###########0.0000");
	            result.setData("OWN_PRICE", i, obj.format(changeFormat));
	        }
			total.setValue(dossum);
			
			result.setData("REGION_CODE", count, "�ܼ�:");
			result.setData("ORDER_CODE", count, "");
			result.setData("ORDER_DESC", count, "");
			result.setData("CASE_NO", count, "");
			result.setData("BILL_DATE", count, "");
			result.setData("MR_NO", count, "");
			result.setData("PAT_NAME", count, "");
			result.setData("SPECIFICATION", count, "");
			result.setData("UNIT_CHN_DESC", count, "");
			result.setData("OWN_PRICE", count, "");
//			result.setData("DOSAGE_QTY", count, qty);
			result.setData("DOSAGE_QTY", count, "");
			result.setData("OWN_AMT", count, dossum);
			result.setData("USER_NAME", count, "");
		}

		if (result.getCount() < 0) {
			this.messageBox("û�з������������ݣ�");
			return;
		}
		table.setParmValue(result);
	}

	// ��ѯ��ť�����¼�
	public void onQuery() {
		if (!this.checkConditions()) {
			return;
		}
		TParm parm = new TParm();
		parm = this.encParameter(); // ��ò�ѯ����
		fillTable(parm);
	}

	// �����ܷ�Χ�����������ı䴥���¼�
	public void onCMBHospitalChange() {
		cmbHospital = (TComboBox) this.getComponent("cmb_Hospital");
		String tempStr = cmbHospital.getValue();
		cmbType = (TComboBox) this.getComponent("cmb_Type");

		if (tempStr.equals("4")) { // 4���� ��Ժ�ڼ�� 1����ȫ����� 2����������� 3����Ժ�����
			cmbType.setStringData("[[id,name],[,],[1,����]]");
		} else if (tempStr.equals("1") || tempStr.equals("2")
				|| tempStr.equals("3")) {
			cmbType.setStringData("[[id,name],[,],[1,����],[2,��ϸ]]");
		} else if (tempStr.equals("")) {
			cmbType.setStringData("[[id,name],[,]]");
		}
		cmbType.setValue("");
		
		//���Ʋ����Ų�ѯ����
		mrNo.setValue("");
		name.setValue("");
		mrNo.setEnabled(false);
	}
	
	public void onCMBTypeChange(){
		cmbType = (TComboBox) this.getComponent("cmb_Type");
		String tempStr = cmbType.getValue();
		if(tempStr.equals("1")){
			mrNo.setEnabled(false);
			mrNo.setValue("");
			name.setValue("");
		}else if(tempStr.equals("2")){
			mrNo.setEnabled(true);
		}
	}

	// ��������ť�����¼�
	public void onExport() {
		TTable expTable = (TTable) callFunction("UI|tab_Statistics|getThis");
		if (expTable.getRowCount() <= 0) {
			messageBox("�޵�������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(expTable, reportFlg);
	}

	// ��հ�ť�����¼�
	public void onClear() {
		this.setValue("cmb_Hospital", "");
		this.setValue("cmb_Type", "");
		this.resetDate();
		orderCode.setValue("");
		orderDesc.setValue("");
		mrNo.setValue("");
		name.setValue("");
		total.setValue("");
		table.removeRowAll();
		mrNo.setEnabled(false);
	}

	// ����ʱ��ؼ�
	private void resetDate() {
		this.setValue("txt_StartDate",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("txt_EndDate", rollDay);
//		Timestamp now = SystemTool.getInstance().getDate();
//		this.setValue("txt_StartDate", now);
		this.setValue("txt_StartTime", StringTool.getTimestamp("00:00:00",
				"HH:mm:ss"));
//		this.setValue("txt_EndDate", now);
		this.setValue("txt_EndTime", StringTool.getTimestamp("23:59:59",
		"HH:mm:ss"));
	}
	
	// ��ñ�ͷ
	private Map getTableHeader(String h_id, String t_id) {
		if (t_id.equals("1")) {
			// �������_(��������) //����������� //Ժ��������� //Ժ�ڼ������
			header = "����,120,tbl_RegionCode;ҩƷ����,150,ORDER_CODE;ҩƷ����,150,ORDER_DESC;���,100,SPECIFICATION;����,100,double,#########0.00,DOSAGE_QTY_SUM;��λ,100,UNIT_CHN_DESC;���ۼ�,100,OWN_PRICE;���۽��,100,double,#########0.0000,OWN_AMT_SUM";
			parmMap = "REGION_CODE;ORDER_CODE;ORDER_DESC;SPECIFICATION;DOSAGE_QTY_SUM;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT_SUM";
			align = "0,left;1,left;2,left;3,left;4,right;5,left;6,right;7,right";
		} else if ((h_id.equals("1") || h_id.equals("3")) && t_id.equals("2")) {
			// ������ϸ_(��������) //Ժ��������ϸ
			header = "����,120,tbl_RegionCode;ҩƷ����,80,ORDER_CODE;ҩƷ����,200,ORDER_DESC;����,100,BILL_DATE;������,100,MR_NO;����,80,PAT_NAME;���,80,SPECIFICATION;����,80,double,#########0.00,DOSAGE_QTY;��λ,50,UNIT_CHN_DESC;���ۼ�,80,OWN_PRICE;���۽��,80,double,#########0.0000,OWN_AMT";
			parmMap = "REGION_CODE;ORDER_CODE;ORDER_DESC;BILL_DATE;MR_NO;PAT_NAME;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT";
			align = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,left;9,right;10,right";
		} else if (h_id.equals("2") && t_id.equals("2")) {
			// ����������ϸ
			header = "����,120,tbl_RegionCode;ҩƷ����,80,ORDER_CODE;ҩƷ����,200,ORDER_DESC;�����,100,CASE_NO;����,100,BILL_DATE;������,100,MR_NO;����,80,PAT_NAME;���,80,SPECIFICATION;����,80,double,#########0.00,DOSAGE_QTY;��λ,50,UNIT_CHN_DESC;���ۼ�,80,OWN_PRICE;���۽��,80,double,#########0.0000,OWN_AMT";
			parmMap = "REGION_CODE;ORDER_CODE;ORDER_DESC;CASE_NO;BILL_DATE;MR_NO;PAT_NAME;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT";
			align = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,right;9,left;10,right;11,right";
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("header", header);
		map.put("parmMap", parmMap);
		map.put("align", align);
		return map;
	}

	private boolean checkConditions() {
		if (getValueString("txt_StartDate").equals("")
				|| getValueString("txt_EndDate").equals("")) {
			this.messageBox("��ѡ���������ڣ�");
			return false;
		}
		if (getValueString("cmb_Hospital").equals("")) {
			this.messageBox("��ѡ����ܷ�Χ��");
			return false;
		}
		if (getValueString("cmb_Type").equals("")) {
			this.messageBox("��ѡ��������ͣ�");
			return false;
		}
		return true;
	}
	
	// ��װ��ѯ����
	private TParm encParameter() {
		TParm parm = new TParm(); // �����б�

		startTime = new StringBuffer();
		endTime = new StringBuffer();

		String str = getValueString("txt_StartDate").toString();
		str = str.substring(0, 10).replaceAll("-", "/");

		startTime.append(str);
		startTime.append(" ");

		if (getValueString("txt_StartTime").equals("")) {
			startTime.append("00:00:00");
		} else {
			startTime.append(StringTool.getString(TCM_Transform
					.getTimestamp(getValue("txt_StartTime")), "HH:mm:ss"));
		}

		str = getValueString("txt_EndDate").toString();
		str = str.substring(0, 10).replaceAll("-", "/");

		endTime.append(str);
		endTime.append(" ");

		if (getValueString("txt_EndTime").equals("")) {
			endTime.append("23:59:59");
		} else {
			endTime.append(StringTool.getString(TCM_Transform
					.getTimestamp(getValue("txt_EndTime")), "HH:mm:ss"));
		}

		String hosId = getValueString("cmb_Hospital");
		String typeId = getValueString("cmb_Type");

		parm.setData("DATE_START", startTime.toString()); // ��ʼʱ��
		parm.setData("DATE_END", endTime.toString()); // ��ֹʱ��

		parm.setData("REGION_CODE", regionCode.getValue());//����
		
		if(!orderCode.getValue().equals("")){
			parm.setData("ORDER_CODE", orderCode.getValue());//ҩƷ����
		}
		if(!mrNo.getValue().equals("")){
			parm.setData("MR_NO", mrNo.getValue());//������
		}
		
		parm.setData("HOSPITAL_ID", hosId); // ����ҽԺ
		parm.setData("TYPE_ID", typeId); // ��������

		if (hosId.equals("1") && typeId.equals("1")) {
			reportFlg = "selectOSUM"; // �������_(��������)
		} else if (hosId.equals("1") && typeId.equals("2")) {
			reportFlg = "selectODET"; // ������ϸ_(��������)
		} else if (hosId.equals("2") && typeId.equals("1")) {
			reportFlg = "selectOTJSUM"; // �����������
		} else if (hosId.equals("2") && typeId.equals("2")) {
			reportFlg = "selectOTJDET"; // ����������ϸ
		} else if (hosId.equals("3") && typeId.equals("1")) {
			reportFlg = "selectOTGSUM"; // Ժ���������
		} else if (hosId.equals("3") && typeId.equals("2")) {
			reportFlg = "selectOTGDET"; // Ժ��������ϸ
		} else if (hosId.equals("4") && typeId.equals("1")) {
			reportFlg = "selectETGSUM"; // Ժ�ڼ������
		}

		parm.setData("REPORTFLG", reportFlg);
		return parm;
	}

	/**
     * ����MR_NO
     */
    public void onMrNo() {
        String strMrNo = mrNo.getValue();
        mrNo.setValue(PatTool.getInstance().checkMrno(strMrNo));
        //�õ���������
        getPatName(strMrNo);
    }

    /**
     * ��øò��˵�����
     * @param mrNo String
     */
    private void getPatName(String strMrNo){
    	name.setValue(PatTool.getInstance().getNameForMrno(strMrNo));
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
     * ���ܷ���ֵ����
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        System.out.println(parm);
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
        	orderCode.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
        	orderDesc.setValue(order_desc);
    }
}
