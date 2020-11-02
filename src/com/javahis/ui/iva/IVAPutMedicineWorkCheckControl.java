package com.javahis.ui.iva;

import java.sql.Timestamp;

import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.iva.IVADeploymentTool;
import jdo.iva.IVAPutMedicineWorkCheckTool;
import jdo.iva.IVAPutMedicineWorkTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ������ҩControl
 * </p>
 * 
 * <p>
 * Description: ������ҩControl
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
 * @author zhangy 2013.07.21
 * @version 1.0
 */

public class IVAPutMedicineWorkCheckControl extends TControl {
	// �õ�table�ؼ�
	private TTable table_d;
	private TTable table_m;
	private boolean save = false;

	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	// �õ�checkbox�ؼ�
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	// �õ�RadioButton�ؼ�
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	// ��ʼ��
	public void onInit() {
		super.onInit();
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
//		initPage();
	}

	// ��ʼ����ҩ��Ա
	public void initPage() {
		Operator.getID();
//		this.setValue("PREPARE_CHECK_USER", Operator.getID());
//		((TTextFormat) getComponent("START_TIME")).setEnabled(false);
//		((TTextFormat) getComponent("END_TIME")).setEnabled(false);

	}

	// ��ѯ����ҩor��ҩ��Ϣ
	public void onQuery() {
		// �õ�TABLE_M�ؼ�
		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");
		table_m.removeRowAll();
		table_d.removeRowAll();
		// ��װǰ̨�õ���ֵ
		TParm parm = new TParm();
		// �õ�ǰ̨�ؼ�ֵ
		String station_desc = this.getValueString("STATION_DESC");
		String mr_no = this.getValueString("MR_NO");
		String prepare_user = this.getValueString("PREPARE_CHECK_USER");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		// �ж�ǰ̨����õ���ֵ�Ƿ�Ϊ��
		if (station_desc != null && !"".equals(station_desc)) {
			parm.setData("STATION_DESC", station_desc);
		}
		if (mr_no != null && !"".equals(mr_no)) {
			parm.setData("MR_NO", mr_no);
		}
		if (start_time != null && !"".equals(start_time)) {
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
		}else{
			this.messageBox("�������ѯ���䣡");
			return;
		}
		if (end_time != null && !"".equals(end_time)) {
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
		}else{
			this.messageBox("�������ѯ���䣡");
			return;
		}
		TParm result = new TParm();
		// �жϵ�ѡ��ť�Ƿ�ѡ��
		if (getRadioButton("BUTTON_M").isSelected()) {
			parm.setData("FLG", "N");
		}
		if (getRadioButton("BUTTON_D").isSelected()) {
			parm.setData("FLG", "Y");
			if (prepare_user != null && !"".equals(prepare_user)) {
				parm.setData("PREPARE_CHECK_USER", prepare_user);
			}
		}
		result = IVAPutMedicineWorkCheckTool.getInstance().queryInfo(parm);
		if(!save){
			if(result.getCount() <= 0){
				this.messageBox("δ��ѯ������");
				return;
			}
		}
		save=false;
		table_m.setParmValue(result);
	}

	/*
	 * �����¼�
	 */
	public void onClick() {
		// �õ�table�ؼ�
		table_m = this.getTTable("TABLE_M");
		table_d = this.getTTable("TABLE_D");
		// �õ�ѡ��������
		int row = table_m.getSelectedRow();
		// �õ�table��ֵ
		TParm tparm = table_m.getParmValue();
		// �õ���ѯϸ�������
		TParm parm = new TParm();
		if (row != -1) {
			// ���ؼ���ֵ
//			this.setValue("STATION_DESC", tparm.getValue("STATION_CODE", row));
//			this.setValue("MR_NO", tparm.getValue("MR_NO", row));
//			this.setValue("PAT_NAME", tparm.getValue("PAT_NAME", row));
//			String station_desc = this.getValueString("STATION_DESC");
			String start_time = this.getValueString("START_TIME");
			String end_time = this.getValueString("END_TIME");
			if (getRadioButton("BUTTON_M").isSelected()) {
				parm.setData("FLG", "N");
			}
			if (getRadioButton("BUTTON_D").isSelected()) {
				parm.setData("FLG", "Y");
//				this.setValue("PREPARE_CHECK_USER", tparm.getValue("PREPARE_CHECK_USER",
//						row));
				parm.setData("PREPARE_CHECK_USER", tparm.getValue("PREPARE_CHECK_USER",
						row));
			}
			
			// ��parm��ֵ����ѯϸ��
			parm.setData("CASE_NO", tparm.getValue("CASE_NO", row));
			parm.setData("START_TIME", start_time.toString().substring(0, 19));
			parm.setData("END_TIME", end_time.toString().substring(0, 19));
			parm.setData("STATION_DESC", tparm.getValue("STATION_CODE", row));
//			System.out.println("CASE_NO====="+tparm.getValue("CASE_NO", row));
			TParm result = IVAPutMedicineWorkCheckTool.getInstance().querydetail(
					parm);
			for(int i=0; i<result.getCount("ORDER_CODE");i++){
				result.setData("EXEC_DATE", i, 
						result.getValue("EXEC_DATE", i).substring(0, 4)
						+"-"
						+result.getValue("EXEC_DATE", i).substring(4, 6)
						+"-"
						+result.getValue("EXEC_DATE", i).substring(6, 8)
						+" "
						+result.getValue("EXEC_DATE", i).substring(8, 10)
						+":"
						+result.getValue("EXEC_DATE", i).substring(10, 12));
			}
			table_d.setParmValue(result);
		}
		onTableCheckBoxClicked(this);
	}

	/*
	 * ���
	 */

	public void onClear() {
		table_m = this.getTTable("TABLE_M");
		table_m.removeRowAll();
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		this.clearValue("STATION_DESC;PREPARE_CHECK_USER;MR_NO;PAT_NAME;BED_NO");
//		this.clearValue("MR_NO");
//		this.clearValue("PAT_NAME");
//		this.clearValue("BED_NO");
//		if (getRadioButton("BUTTON_D").isSelected()) {
//			this.clearValue("PREPARE_USER");
//			this.clearValue("START_TIME");
//			this.clearValue("END_TIME");
//		}
		this.onInit();
	}

	/*
	 * ��ѡ��ť�¼�
	 */
	public void onRadion() {
		// �жϵ�ѡ��ť�Ƿ�ѡ��
		if (getRadioButton("BUTTON_D").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("PREPARE_CHECK_USER"))
					.setEnabled(true);
			onClear();
//			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
		}
		if (getRadioButton("BUTTON_M").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("PREPARE_CHECK_USER"))
					.setEnabled(false);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
			this.onClear();
			initPage();
		}

	}

	// /*
	// * ��ҩ��֮�󱣴�
	// */
	// public void onSave() {
	// table_m.acceptText();
	// table_d = this.getTTable("TABLE_D");
	// table_m = this.getTTable("TABLE_M");
	// String check_user = this.getValueString("PREPARE_USER");
	// TParm tparm = table_m.getParmValue();
	// // �õ�δѡ�е�����ֵ
	// System.out.println("--------tparm---"+tparm);
	// TParm temParm = new TParm();
	// TParm result=new TParm();
	//		
	//		
	// System.out.println("----tparm.getCount-------**"+tparm.getCount());
	// for (int i = 0; i < tparm.getCount(); i++) {
	//		
	// TParm rowParm = tparm.getRow(i);
	// String flg = rowParm.getValue("SELECT_AVG");
	// // �ж��Ƿ�ѡ��
	// if ("Y".equals(flg)) {// flg.equals("Y")
	//
	// rowParm.setData("PREPARE_USER", check_user);
	// result = TIOM_AppServer.executeAction(
	// "action.spc.IVADsAciton", "onUpdatePut", rowParm);
	//
	// if (result.getErrCode() < 0) {
	// temParm.setRowData(i, rowParm);
	// }
	// } else {
	// temParm.setRowData(i, rowParm);
	// // temParm.setRowData(i, rowParm, i+1);
	//				
	//				
	// }
	// }
	//		
	// System.out.println("----tparm.getCount-------**"+tparm.getCount());
	// System.out.println(result);
	// System.out.println(temParm);
	// table_d.removeRowAll();
	// table_m.setParmValue(temParm);
	// }

	/*
	 * ȫѡ
	 */
	public void onSelectAll() {
		table_m = getTTable("TABLE_M");
		table_m.acceptText();
		if (table_m.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < table_m.getRowCount(); i++) {
			table_m.setItem(i, "SELECT_AVG", getValueString("SELECT_ALL"));
		}
	}

	/*
	 * ��ѡ���¼�
	 */
	public void onTableCheckBoxClicked(Object obj) {
		if (getRadioButton("BUTTON_M").isSelected()) {
			table_m = this.getTTable("TABLE_M");
			int column = table_m.getSelectedRow();
			int row = table_m.getSelectedColumn();
			if ("N".equals(table_m.getItemString(column, "SELECT_AVG"))
					&& row == 0) {
				table_m.setItem(column, "SELECT_AVG", "Y");
			} else if ("Y".equals(table_m.getItemString(column, "SELECT_AVG"))
					&& row == 0){
				table_m.setItem(column, "SELECT_AVG", "N");
			}

		}
	}

	public void onSave() {
		String check_user = this.getValueString("PREPARE_CHECK_USER");
		table_d = this.getTTable("TABLE_D");
		table_m = this.getTTable("TABLE_M");
		TParm tparm = table_m.getParmValue();
		int num = 0;
		for(int n=0;n<tparm.getCount("CASE_NO");n++){
			if(tparm.getValue("SELECT_AVG", n).equals("Y")){
				num++;
			}
		}
		if(num == 0){
			this.messageBox("û��Ҫ���������");
			return;
		}
		if(check_user.equals("")){
			String type = "singleExe";
			TParm inParm = (TParm) this.openDialog(
					"%ROOT%\\config\\inw\\passWordCheck.x", type);
			String OK = inParm.getValue("RESULT");
			if (!OK.equals("OK")) {
				return;
			}
			this.setValue("PREPARE_CHECK_USER", inParm.getValue("USER_ID"));
			check_user = this.getValueString("PREPARE_CHECK_USER");
		}
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		// �õ�δѡ�е�����ֵ
//		TParm temParm = new TParm();
		TParm result = new TParm();
		int number = 0;
		for (int i = 0; i < tparm.getCount(); i++) {
			TParm rowParm = tparm.getRow(i);
			String flg = rowParm.getValue("SELECT_AVG");
			// �ж��Ƿ�ѡ��
			if ("Y".equals(flg)) {// flg.equals("Y")
//				TParm  tabel_d_parm = IVAPutMedicineWorkTool.getInstance().querydetail(rowParm);
				rowParm.setData("PREPARE_CHECK_USER", check_user);
				String sqlM = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
						+ " A.START_DTTM,A.END_DTTM,B.BAR_CODE, "
						+ " B.ORDER_DATE,B.ORDER_DATETIME "
						+ " FROM ODI_DSPNM A,ODI_DSPND B "
						+ " WHERE A.CASE_NO='"+rowParm.getValue("CASE_NO")
//						+ " AND B.BATCH_CODE='"+rowParm.getValue("BATCH_CODE")
						+"' AND A.PREPARE_TIME BETWEEN TO_DATE('"
						+ start_time.toString().substring(0, 19)
						+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
						+ end_time.toString().substring(0, 19)
						+ "','YYYY-MM-DD HH24:MI:SS') "
						+ " AND A.IVA_FLG='Y' "
						+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
						+ " AND A.CASE_NO=B.CASE_NO "
						+ " AND A.ORDER_NO=B.ORDER_NO "
						+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
						+ " A.START_DTTM AND A.END_DTTM "
						+ " AND B.IVA_FLG='Y' AND B.PREPARE_USER IS NOT NULL "
						+ " AND B.PREPARE_CHECK_USER IS NULL "
						+ " AND B.IVA_RETN_USER IS NULL ";
//				System.out.println("sqlM============"+sqlM);
				TParm updateParm = new TParm(TJDODBTool.getInstance().select(sqlM));
				for(int j=0;j<updateParm.getCount("CASE_NO");j++){
					updateParm.setData("PREPARE_CHECK_USER",j, check_user);
				}
				for(int j=0;j<updateParm.getCount("CASE_NO");j++){
					result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
							"onUpdatePutCheck", updateParm.getRow(j));
					if (result.getErrCode() < 0) {
						number++;
//						temParm.addParm(updateParm.getRow(j));
					}
				}
//				if (result.getErrCode() < 0) {
//					temParm.setRowData(i, rowParm);
//					number++;
//				}
			} 
//			else {
//				temParm.setRowData(i, rowParm);
//				// temParm.setRowData(i, rowParm, i+1);
//			}
		}
		if(number <= 0){
			this.messageBox("����ɹ�");
		}else{
			this.messageBox("����ʧ��");
		}
		table_d.removeRowAll();
		table_m.removeRowAll();
		this.clearValue("MR_NO;PAT_NAME");
		save=true;
		this.onQuery();
	}
	
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		// �ж��Ƿ�����ӡ����ȫ����ӡ
		String select_all = this.getValueString("SELECT_ALL");
		if("Y".equals(select_all)){
			onPrintAll();
		} else {
			onPrintSingle();
		}
	}
	
	/**
	 * ȫ����ӡ����
	 */
	public void onPrintAll(){
		table_m = getTTable("TABLE_M");
		if(table_m.getRowCount() <= 0){
			messageBox("û��Ҫ��ӡ������");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", "����������ҩ�嵥");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// �������
		TParm parm = new TParm();
		TParm result = IVADeploymentTool.getInstance().queryAllData();
		// ��������е�Ԫ��
		for (int i = 0; i < result.getCount(); i++) {
			parm.addData("PAT_NAME", result.getValue("PAT_NAME", i));
			parm.addData("MR_NO", result.getValue("MR_NO", i));
			parm.addData("STATION_DESC", result.getValue("STATION_DESC", i));
			parm.addData("BED_NO_DESC", result.getValue("BED_NO_DESC", i));
			parm.addData("ORDER_DESC", result.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", result.getValue("SPECIFICATION", i));
			parm.addData("MEDI_QTY", result.getValue("MEDI_QTY", i));
			parm.addData("FREQ_CHN_DESC", result.getValue("FREQ_CHN_DESC", i));
		}
		// ������
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
		// �����ŵ�������
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADeploymentList.jhw", data);
	}
	
	/**
	 * ������ӡ����
	 */
	public void onPrintSingle(){
		table_d = getTTable("TABLE_D");
		if(table_d.getRowCount() <= 0){
			messageBox("û��Ҫ��ӡ������");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", "����������ҩ�嵥");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// �������
		TParm parm = new TParm();
		int rowM = table_m.getSelectedRow();
		TParm tableParmM = table_m.getParmValue().getRow(rowM);
		TParm tableParmD = table_d.getParmValue();
		// ��������е�Ԫ��
		for (int i = 0; i < table_d.getRowCount(); i++) {
			parm.addData("PAT_NAME", tableParmM.getValue("PAT_NAME"));
			parm.addData("MR_NO", tableParmM.getValue("MR_NO"));
			parm.addData("STATION_DESC", tableParmM.getValue("STATION_DESC"));
			parm.addData("BED_NO_DESC", tableParmM.getValue("BED_NO_DESC"));
			parm.addData("ORDER_DESC", tableParmD.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParmD.getValue("SPECIFICATION", i));
			parm.addData("MEDI_QTY", tableParmD.getValue("MEDI_QTY", i));
			parm.addData("FREQ_CHN_DESC", tableParmD.getValue("FREQ_CHN_DESC", i));
		}
		// ������
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
		// �����ŵ�������
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\IVA\\IVADeploymentList.jhw", data);
	}
	
	/**
	 * ��ѯ������Ϣ
	 */
	public void onQueryNO() {
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			clearValue("MR_NO;PAT_NAME;");
			this.messageBox("�޴˲�����!");
			return;
		}
		//modify by huangtt 20160930 EMPI���߲�����ʾ  start
		String mrNo = PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO", mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
	            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
	            setValue("MR_NO", pat.getMrNo());
	    }
		//modify by huangtt 20160930 EMPI���߲�����ʾ  end
		setValue("PAT_NAME", pat.getName().trim());
		this.onQuery();
	}

}
