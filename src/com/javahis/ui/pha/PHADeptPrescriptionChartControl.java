/**
 * 
 */
package com.javahis.ui.pha;

import java.sql.Timestamp;
import java.util.Date;

import jdo.pha.PHADeptPrescriptionChartTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ���Ҵ�����ͳ��
 * </p>
 * 
 * <p>
 * Description: ҩ��������ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Copyright: javahis 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author guangl 2016.05.23
 * @version 1.0
 */
public class PHADeptPrescriptionChartControl extends TControl {

	TParm inParm = new TParm();
	
	// ����
	private TTable table;

	// ���ź�
	private String dept_code;

	// ҽ��
	private String dr_code;

	// ҩ�ͣ��г�ҩPHA_C����ҩPHA_W��
	private String pha_type;

	// ��ֹʱ��
	private TTextFormat s_date;
	private TTextFormat e_date;

	// ���Ӳ�ѯ����
	TCheckBox tcbDose;
	TCheckBox tcbHexp;
	TCheckBox tcb;// �Ƿ񺬼��ش�����ѡ������
	private boolean dose_flag;
	private boolean hexp_flag;
	private boolean _flag;// �Ƿ񺬼��ش�����ҩƷ��ʱû���жϼ����ֶΣ�����

	@Override
	public void onInit() {
		// TODO Auto-generated method stub
		super.onInit();
		table = (TTable) getComponent("Table");
		((TComboBox) this.getComponent("REGION_CODE")).setValue(Operator
				.getRegion());

		this.setValue("S_DATE", TJDODBTool.getInstance().getDBTime());
		this.setValue("E_DATE", TJDODBTool.getInstance().getDBTime());

		tcbDose = (TCheckBox) this.getComponent("DOSE_FLAG");
		tcbHexp = (TCheckBox) this.getComponent("HEXP_FLAG");
		tcb = (TCheckBox) this.getComponent("_FLAG");

	}

	/*
	 * 
	 */
	public void onQuery() {
		
		s_date = (TTextFormat) this.getComponent("S_DATE");
		e_date = (TTextFormat) this.getComponent("E_DATE");
		
		TParm tableData = new TParm();
		TParm result = getQueryData();
		if (result.getCount() <= 0) {
            this.messageBox("�ò�ѯ���������ݣ�");
            onClear();
            return;
        }
		
//		// �����Ϣ
//		if(table.getParmValue() != null){
//			tableData = table.getParmValue();
//			tableData.addParm(result);
//		}else{
//			tableData = result;
//		}
		
		table.setParmValue(result);
//		table.addRow(tableData);
	}

	/*
	 * 
	 */
	public TParm getQueryData() {
		checkQuery();
		// ����
		

		// ���
		TParm result = new TParm();

		// ���ͳ��ʱ��
		Timestamp startDate = (Timestamp) s_date.getValue();
		String tempEnd = e_date.getValue().toString();
		Timestamp endDate = StringTool
				.getTimestamp(tempEnd.substring(0, 4) + tempEnd.substring(5, 7)
						+ tempEnd.substring(8, 10) + "235959", "yyyyMMddHHmmss");

		// ��ѯ�������á�������š���ֹʱ��
		if (this.getValueString("REGION_CODE").length() > 0)
			inParm = this.getParmForTag("REGION_CODE");
		if (startDate != null)
			inParm.setData("START_DATE", startDate);
		if (endDate != null)
			inParm.setData("END_DATE", endDate);

		// ��ѯ�������á������źš�ҽ���š�ҩ��
		// ��checkQuery()�������Ѿ����
		inParm.setData("DEPT_CODE", dept_code);
		inParm.setData("DR_CODE", dr_code);
		inParm.setData("PHA_TYPE", pha_type);

		// ��ѯ�������á����Ƿ�ע������Ƿ񺬿����ء��Ƿ񺬼���
		inParm.setData("DOSE_FLAG", dose_flag);
		inParm.setData("HEXP_FLAG", hexp_flag);
		inParm.setData("_FLAG", _flag);

		result = PHADeptPrescriptionChartTool.getInstance()
				.getQueryData(inParm);
		
		
		return result;
	}

	/*
	 * ����ѯ�����Ƿ��걸
	 */
	public boolean checkQuery() {
		// ��ȡ���źš�ҽ���š�ҩ��
		dept_code = getValueString("DEPT_CODE");
		dr_code = getValueString("DR_CODE");
		pha_type = getValueString("PHA_TYPE");
		
		dose_flag = false;
		hexp_flag = false;
		_flag = false;
		
		// ��ȡ���Ӳ�ѯ�������
		if (tcbDose.isSelected()) {
			dose_flag = true;
		}
		if (tcbHexp.isSelected()) {
			hexp_flag = true;
		}
		if (tcb.isSelected()) {
			_flag = true;
		}

		// ���źź�ҽ���ű��붼����ܲ�ѯ��ҩ�Ͳ���Ĭ����ҩ��ҩ��ͳ��
		if ("".equals(dept_code) || "".equals(dr_code)) {
			return false;
		}
		return true;
	}

	/*
	 * �������
	 */
	public void onClear() {
		this.setValue("S_DATE", TJDODBTool.getInstance().getDBTime());
		this.setValue("E_DATE", TJDODBTool.getInstance().getDBTime());
		this.clearValue("DEPT_CODE;DR_CODE;PHA_TYPE");
		tcb.setSelected(false);
		tcbDose.setSelected(false);
		tcbHexp.setSelected(false);
		table.removeRowAll();
	}
	
	public void onDelete(){
		int row = table.getSelectedRow();
		if(row == -1){
			messageBox("δѡ���У�");
		}else{
			table.removeRow(row);
		}
	}

	/*
	 * ��ӡ����
	 */
	public void onPrint() {
		if(table.getRowCount() <= 0){
			messageBox("û��Ҫ��ӡ�����ݣ�");
			return;
		}
		
		//��ӡ�����ݼ���
		TParm printData =  new TParm();
		//�����Ϣ����
		TParm parm = new TParm();
		//�����Ϣ����
		//TParm tableParm = table.getParmValue();
		TParm tableParm = table.getShowParmValue();
		//����ӡ�����ݼ��ϡ�����ͷ��Ϣ
		printData.setData("TITLE", "TEXT", "������ͳ�Ʊ���");
		printData.setData("Date", "TEXT", "ͳ��ʱ�䣺" 
				+ getValue("S_DATE").toString().substring(0,10) + " " + getValue("S_TIME").toString()+ " �� " 
				+ getValue("E_DATE").toString().substring(0,10) + " " + getValue("E_TIME").toString());
		Timestamp endDate = StringTool
		.getTimestamp(new Date());
		printData.setData("Time", "TEXT", "�Ʊ�ʱ�䣺" + endDate.toString().substring(0,19));
		
		String info = "";
		info = "���򣺱�������������ҽԺ" + "    ���ң��ż���ҩ��      ͳ�����ͣ�"; 
		if("".equals(inParm.getValue("PHA_TYPE"))){
			info+="�г�ҩ,��ҩ";
		}else if("PHA_W".equals(inParm.getValue("PHA_TYPE"))){
			info+="��ҩ";
		}else{
			info+="�г�ҩ";
		}
		if(inParm.getBoolean("DOSE_FLAG")){
			info+=",��ע�������";
		}
		if(inParm.getBoolean("HEXP_FLAG")){
			info+=",�������ش���";
		}
		if(inParm.getBoolean("_FLAG")){
			info+=",�����ش���";
		}
		printData.setData("Infomation", "TEXT", info);
		
		
		int num1 = 0 , num2 = 0 , num3 = 0 , num4 = 0 , num5 = 0 , num6 = 0;
		double amount1 = 0.00 , amount2 = 0.00 , amount3 = 0.00 , amount4 = 0.00 ,amount5 = 0.00 , amount6 = 0.00;
		
		//���������
		for (int i = 0; i < tableParm.getCount(); i++) {
			parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE", i));
			parm.addData("DR_CODE", tableParm.getValue("DR_CODE", i));
			parm.addData("PHA_W_NUM", tableParm.getValue("PHA_W_NUM", i));
			parm.addData("PHA_W_AMOUNT", tableParm.getValue("PHA_W_AMOUNT", i));
			parm.addData("PHA_C_NUM", tableParm.getValue("PHA_C_NUM", i));
			parm.addData("PHA_C_AMOUNT", tableParm.getValue("PHA_C_AMOUNT", i));
			parm.addData("DOSE_NUM", tableParm.getValue("DOSE_NUM", i));
			parm.addData("DOSE_AMOUNT", tableParm.getValue("DOSE_AMOUNT", i));
			parm.addData("HEXP_NUM", tableParm.getValue("HEXP_NUM", i));
			parm.addData("HEXP_AMOUNT", tableParm.getValue("HEXP_AMOUNT", i));
			parm.addData("HORM_NUM", tableParm.getValue("HORM_NUM", i));
			parm.addData("HORM_AMOUNT", tableParm.getValue("HORM_AMOUNT", i));
			
			parm.addData("TOTAL_NUM", tableParm.getInt("PHA_W_NUM",i) 
					+ tableParm.getInt("PHA_C_NUM", i) 
					+ tableParm.getInt("DOSE_NUM", i) 
					+ tableParm.getInt("HEXP_NUM", i));
			parm.addData("TOTAL_AMOUNT", String.format("%.2f", tableParm.getDouble("PHA_W_AMOUNT",i) 
					+ tableParm.getDouble("PHA_C_AMOUNT", i) 
					+ tableParm.getDouble("DOSE_AMOUNT", i) 
					+ tableParm.getDouble("HEXP_AMOUNT", i)) );
			
			num1 += Integer.valueOf(tableParm.getValue("PHA_W_NUM" , i));
			num2 += Integer.valueOf(tableParm.getValue("PHA_C_NUM" , i));
			num3 += Integer.valueOf(tableParm.getValue("DOSE_NUM" , i));
			num4 += Integer.valueOf(tableParm.getValue("HEXP_NUM" , i));
			num5 = num1 + num2 + num3 + num4;
			
			amount1 += Double.valueOf(tableParm.getValue("PHA_W_AMOUNT" , i));
			amount2 += Double.valueOf(tableParm.getValue("PHA_C_AMOUNT" , i));
			amount3 += Double.valueOf(tableParm.getValue("DOSE_AMOUNT" , i));
			amount4 += Double.valueOf(tableParm.getValue("HEXP_AMOUNT" , i));
			amount5 = amount1 + amount2 + amount3 + amount4;
		}
		
		parm.addData("DEPT_CODE", null);
		parm.addData("DR_CODE", "�ܼ�");
		parm.addData("PHA_W_NUM", String.format("%d", num1));
		parm.addData("PHA_W_AMOUNT", String.format("%.2f", amount1));
		parm.addData("PHA_C_NUM", String.format("%d", num2));
		parm.addData("PHA_C_AMOUNT",String.format("%.2f", amount2));
		parm.addData("DOSE_NUM", String.format("%d", num3));
		parm.addData("DOSE_AMOUNT", String.format("%.2f", amount3));
		parm.addData("HEXP_NUM", String.format("%d", num4));
		parm.addData("HEXP_AMOUNT", String.format("%.2f", amount4));
		parm.addData("HORM_NUM", String.format("%d", num6));
		parm.addData("HORM_AMOUNT", String.format("%.2f", amount6));
		parm.addData("TOTAL_NUM", String.format("%d", num5));
		parm.addData("TOTAL_AMOUNT", String.format("%.2f", amount5));
		//����������ֵ���Ϣ
		parm.setCount(parm.getCount("DEPT_CODE"));
		parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		parm.addData("SYSTEM", "COLUMNS", "DR_CODE");
		parm.addData("SYSTEM", "COLUMNS", "PHA_W_NUM");
		parm.addData("SYSTEM", "COLUMNS", "PHA_W_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "PHA_C_NUM");
		parm.addData("SYSTEM", "COLUMNS", "PHA_C_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "DOSE_NUM");
		parm.addData("SYSTEM", "COLUMNS", "DOSE_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "HEXP_NUM");
		parm.addData("SYSTEM", "COLUMNS", "HEXP_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "HORM_NUM");
		parm.addData("SYSTEM", "COLUMNS", "HORM_AMOUNT");
		parm.addData("SYSTEM", "COLUMNS", "TOTAL_NUM");
		parm.addData("SYSTEM", "COLUMNS", "TOTAL_AMOUNT");
		
		//����ӡ�����ݼ��ϡ��������Ϣ
		printData.setData("TABLE", parm.getData());
		//����ӡ�����ݼ��ϡ�����β��Ϣ
		//printData.setData("TAIL", "TEXT", "�Ʊ�λ���ż���ҩ��");
		printData.setData("TAIL", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
		
		//this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHADeptPrescriptionChart.jhw", printData);
		this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHADeptPrescription.jhw", printData);
	}

	/*
	 * ����Excel
	 */
	public void onExcel() {
		 ExportExcelUtil.getInstance().exportExcel(table, "���Ҵ�����ͳ��");
	}
	
	

}
