package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.ind.INDEAntibacterialsTool;
import jdo.ind.INDRadStatisticsTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * 
 * <p>
 * Title: Ժ�ڼ��￹��ҩ����ϸͳ��
 * </p>
 * 
 * <p>
 * Description: Ժ�ڼ��￹��ҩ����ϸͳ��
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
 * @author wangm 2013.3.18
 * @version 1.0
 */
public class INDEAntibacterialsControl extends TControl{
	private TTable table = new TTable();
	private String header = ""; // ��ű�ͷ
	private String parmMap = ""; // ��ű�ͷMap
	private String align = "";//�����ݶ��뷽ʽ
	private String reportFlg = ""; // �������ͱ��

	private StringBuffer startTime = new StringBuffer(); // ��ʼ����
	private StringBuffer endTime = new StringBuffer(); // ��ֹ����
	
	
	/**
	 * �����ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.resetDate(); // ��ʼ��ʱ��ؼ�
		this.initTable(); // ��ʼ��table
		// this.initPopeDem(); //��ʼ��Ȩ��
	}

	/**
	 * Ȩ�޳�ʼ��
	 */
	// private void initPopeDem() {
	// // �鳤Ȩ��
	// if (this.getPopedem("LEADER")) {
	// // callFunction("UI|DR_CODE|setEnabled",true);
	// }
	// // ȫԺȨ��
	// if (this.getPopedem("ALL")) {
	// // callFunction("UI|DEPT_CODE|setEnabled",true);
	// // callFunction("UI|DR_CODE|setEnabled",true);
	// }
	// }

	/**
	 * table��ʼ��
	 */
	private void initTable() {
		table = (TTable) this.getComponent("tab_Statistics");
		Map map = this.getTableHeader();
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());
	}

	// table��䷽��
	private void fillTable(TParm parm) {
		table = new TTable();
		table = (TTable) this.getComponent("tab_Statistics");
		table.removeRowAll();

		Map map = this.getTableHeader();
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());

		TParm result = new TParm();
		result = INDEAntibacterialsTool.getInstance().selectReportData(parm);

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
		this.resetDate();
	}

	// ����ʱ��ؼ�
	private void resetDate() {
		Timestamp now = SystemTool.getInstance().getDate();
		this.setValue("txt_StartDate", now);
		this.setValue("txt_StartTime", StringTool.getTimestamp("00:00:00",
				"HH:mm:ss"));
		this.setValue("txt_EndDate", now);
		this.setValue("txt_EndTime", now);
	}

	// ��ñ�ͷ
	private Map getTableHeader() {
		header = "����,90,BILL_DATE;������,100,MR_NO;����,80,PAT_NAME;ҩƷ����,100,ORDER_CODE;ҩƷ����,180,ORDER_DESC;���,80,SPECIFICATION;��λ,50,UNIT_CHN_DESC;���ۼ�,80,double,#########0.00,OWN_PRICE;����,80,double,#########0,DOSAGE_QTY;���۽��,80,double,#########0.00,OWN_AMT;�´�ҽ��,80,USER_NAME";
		parmMap = "BILL_DATE;MR_NO;PAT_NAME;ORDER_CODE;ORDER_DESC;SPECIFICATION;UNIT_CHN_DESC;OWN_PRICE;DOSAGE_QTY;OWN_AMT;USER_NAME";
		align = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,right;9,right;10,left";
		
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

		parm.setData("DATE_START", startTime.toString()); // ��ʼʱ��
		parm.setData("DATE_END", endTime.toString()); // ��ֹʱ��

		reportFlg = "selectEAnti"; // �������ͱ��

		parm.setData("REPORTFLG", reportFlg);
		return parm;
	}
	
	
	
}
