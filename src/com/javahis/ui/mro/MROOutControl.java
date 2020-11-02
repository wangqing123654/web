package com.javahis.ui.mro;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.mro.MROLendTool;
import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �����������
 * 
 * <p>
 * Description:
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangbin 2014.08.19
 * @version 4.0
 */
public class MROOutControl extends TControl {
	
	private String pageParam; // ҳ�洫��
	private TTable tableUp; // ������Ϣ����
	private TTable tableDown; // ������ϸ����
	private TParm lendAreaParm;// �������������ؼ�����
	private Map<String, String> lendAreaMap;// �������������ؼ�Map����
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.pageParam = (String) this.getParameter();
		
		if (StringUtils.isEmpty(pageParam)) {
			this.messageBox("ҳ�洫�δ���");
			return;
		}
		
		// ��ʼ��
		this.onInitPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void onInitPage() {
		tableUp = (TTable) this.getComponent("TABLE_UP");
		tableDown = (TTable) this.getComponent("TABLE_DOWN");
		
		// ҳ�����
		String title = "";
		
		// ����Һ�
		if ("O".equals(pageParam)) {
			// ���ⷽʽ
			getComboBox("OUT_TYPE").setSelectedID("0");
			// ���������ؼ���ʾ
			this.callFunction("UI|CLINIC_AREA|setVisible", true);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			// ���ش�ӡ��ť
//			this.callFunction("UI|print|setVisible", false);
			// ҳ�����
			title = "���ﲡ���������";
		} else if ("I".equals(pageParam)) {
			// ���ⷽʽ
			getComboBox("OUT_TYPE").setSelectedID("1");
			// ���������ؼ���ʾ
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", true);
			// ���ش�ӡ��ť
//			this.callFunction("UI|print|setVisible", false);
			// ҳ�����
			title = "סԺ�����������";
			
			// �趨סԺ�����ͷ���ݽ���(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace("CLINIC_AREA", "ENDEMIC_AREA");
			this.tableUp.setHeader(tableUpHeader);
			
			// �趨סԺ�����ͷ���ݽ���(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("�������,60;", "");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,left;3,center;4,center;5,center");
		} else {
			// ���ⷽʽ
			getComboBox("OUT_TYPE").setSelectedID("2");
			// ҳ�����
			title = "���Ĳ����������";
			
			// ����/����������
			this.callFunction("UI|CLINIC_AREA|setVisible", false);
			this.callFunction("UI|ENDEMIC_AREA|setVisible", false);
			this.callFunction("UI|LEND_AREA|setVisible", true);
			this.callFunction("UI|LEND_AREA|setEnabled", true);
			
			// �趨���ĳ����ͷ���ݽ���(TABLE_UP)
			String tableUpHeader = this.tableUp.getHeader().replace(",CLINIC_AREA", "");
			this.tableUp.setHeader(tableUpHeader);
			
			String tableUpParmMap = this.tableUp.getParmMap().replace("ADM_AREA_CODE", "LEND_AREA_DESC");
			this.tableUp.setParmMap(tableUpParmMap);
			
			this.callFunction("UI|TABLE_UP|setColumnHorizontalAlignmentData",
					"0,left;1,left;2,left;3,right;4,right");
			
			// �趨���ĳ����ͷ���ݽ���(TABLE_DOWN)
			String tableDownHeader = this.tableDown.getHeader().replace("�������,60;", "��������,80;");
			this.tableDown.setHeader(tableDownHeader);
			
			String tableDownParmMap = this.tableDown.getParmMap().replace("QUE_NO;", "ARCHIVES_TYPE;");
			this.tableDown.setParmMap(tableDownParmMap);
			
			this.callFunction("UI|TABLE_DOWN|setColumnHorizontalAlignmentData",
					"1,center;2,center;3,left;4,center;5,center;6,center");
			
			// ����/�����ؼ��趨
			TTextFormat lendArea = ((TTextFormat)this.getComponent("LEND_AREA"));
			lendAreaParm = MROQueueTool.getInstance().selectMroLendArea();
			lendArea.setPopupMenuData(lendAreaParm);
			lendArea.setComboSelectRow();
			lendArea.popupMenuShowData();
			
			// ��װ������������Map
			lendAreaMap = new HashMap<String, String>();
			for (int i = 0; i < lendAreaParm.getCount(); i++) {
				lendAreaMap.put(lendAreaParm.getValue("ID", i), lendAreaParm.getValue("NAME", i));
			}
		}
		
		this.setTitle(title);
		
		// ʱ���趨
		String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
		
		// Ĭ����ʾ�ڶ�������
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("OUT_S_DATE", now); // ��ʼʱ��
		this.setValue("OUT_E_DATE", tommorwDate); // ����ʱ��
	}
	
	/**
	 * ��ѯ��������
	 */
	public void onQuery() {
		clearValue("BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE");
		
		if (!this.validate()) {
			return;
		}
		
		// ��ȡ��ѯ��������
		TParm queryParm = this.getQueryParm();
		
		// ��ѯ������Ĳ�������
		TParm resultParmUp = MROQueueTool.getInstance().selectMroOutIn(queryParm);
		if (resultParmUp.getCount() <= 0) {
			tableDown.setParmValue(new TParm());
			tableUp.setParmValue(new TParm());
			messageBox("��������");
			return;
		}
	
		// ���ĳ���
		if ("L".equals(pageParam)) {
			// ����������ʾ����(��������)
			for (int i = 0; i < resultParmUp.getCount(); i++) {
				resultParmUp.addData("LEND_AREA_DESC", lendAreaMap
						.get(resultParmUp.getValue("ADM_AREA_CODE", i)));
			}
		}
		
		tableUp.setParmValue(resultParmUp);
		
		// ��ѯ������������
		TParm resultParmDown = MROQueueTool.getInstance().selectMroOutInDetail(queryParm);
		
		if (resultParmDown.getErrCode() < 0) {
			this.messageBox("��ѯ�����������ݳ���");
			err("ERR:" + resultParmDown.getErrCode() + resultParmDown.getErrText()
					+ resultParmDown.getErrName());
			return;
		}
		
		// ����ϸ�������Ӱ���������
		for (int i = 0; i < resultParmDown.getCount(); i++) {
			if (StringUtils.equals("I", resultParmDown.getValue("ADM_TYPE", i))) {
				resultParmDown.addData("ARCHIVES_TYPE", "סԺ����");
			} else {
				resultParmDown.addData("ARCHIVES_TYPE", "���ﲡ��");
			}
		}
		
		tableDown.setParmValue(resultParmDown);
		
		// �趨����
		grabFocus("BOX_CODE");
	}
	
	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return parm
	 */
	private TParm getQueryParm() {
		TParm queryParm = new TParm();
		// ����״̬(0_�Ǽ�δ����,1_�ѳ���,2_�ѹ黹)
		queryParm.setData("ISSUE_CODE", "0");
		// ���ӳ����ڼ��ѯ����
		queryParm.setData("OUT_S_DATE", this.getValueString("OUT_S_DATE").substring(0, 10));
		queryParm.setData("OUT_E_DATE", this.getValueString("OUT_E_DATE").substring(0, 10));
		// ���ⷽʽ
		queryParm.setData("OUT_TYPE", getComboBox("OUT_TYPE").getSelectedID());
		if (!StringUtils.equals("L", this.pageParam)) {
			// �ż�ס��ʶ
			queryParm.setData("ADM_TYPE", this.pageParam);
		}
		// ������������ڿ�״̬(0_δ����,1_�ڿ�,2����)
//		queryParm.setData("IN_FLG", "1");
		// ֻ��ѯδȡ���ĳ�������
		queryParm.setData("CAN_FLG", "N");
		
		// ����Һ�
		if ("O".equals(pageParam)) {
			if (StringUtils.isNotEmpty(this.getValueString("CLINIC_AREA"))) {
				// ����
				queryParm.setData("ADM_AREA_CODE", this.getValueString("CLINIC_AREA"));
			}
		} else if ("I".equals(pageParam)) {
			if (StringUtils.isNotEmpty(this.getValueString("ENDEMIC_AREA"))) {
				// ����
				queryParm.setData("ADM_AREA_CODE", this.getValueString("ENDEMIC_AREA"));
			}
		} else {
			if (StringUtils.isNotEmpty(this.getValueString("LEND_AREA"))) {
				// ����/����
				queryParm.setData("ADM_AREA_CODE", this.getValueString("LEND_AREA"));
			}
		}
		
		return queryParm;
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		clearValue("CLINIC_AREA;ENDEMIC_AREA;BOX_CODE;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;LEND_AREA");
		tableUp.setParmValue(new TParm());
		tableDown.setParmValue(new TParm());
	}
	
	/**
	 * ���������Ϣ
	 */
	public boolean onSave(TParm selParm) {
		TParm executeParm = new TParm();
		TParm result = new TParm();
		// ������Ա
		String optUser = Operator.getID();
		// ������ĩ
		String optTerm = Operator.getIP();
		
		// ����ǰ���ݽ������ȼ��жϵ�ǰ�����Ƿ��ִ��
		TParm checkParm = new TParm();
		checkParm.setData("QUE_DATE", SystemTool.getInstance().getDate()
				.toString().substring(0, 10));
		checkParm.setData("MR_NO", selParm.getValue("MR_NO"));
		checkParm.setData("ADM_TYPE", selParm.getValue("ADM_TYPE"));
		checkParm.setData("ISSUE_CODE", "2");
		checkParm.setData("BOX_CODE", selParm.getValue("BOX_CODE"));
		checkParm.setData("BOOK_NO", selParm.getValue("BOOK_NO"));
		
		checkParm = MROQueueTool.getInstance().selectMroQueueInfo(checkParm);
		
		if (checkParm.getErrCode() < 0) {
			this.messageBox("��ѯ�������ݳ���");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		
		TParm lendParm = new TParm();
		
		for (int i = 0; i < checkParm.getCount(); i++) {
			if (StringUtils.equals(selParm.getValue("QUE_SEQ"), checkParm.getValue("QUE_SEQ", i))) {
				continue;
			} else {
				lendParm = new TParm();
				lendParm.setData("LEND_CODE", selParm.getValue("LEND_CODE"));
				lendParm = MROLendTool.getInstance().selectdata(lendParm);
				
				// �Ƚ����ȼ�
				if (lendParm.getInt("PRIORITY", 0) > checkParm.getInt("PRIORITY", i)) {
					this.messageBox("��Ըð����ţ����ڵ�ǰ���������´������ȼ����ߵĴ��������ݣ����ɳ���");
					return false;
				}
			}
		}

		// ���·������
		selParm.setData("TYPE", "OUT");
		selParm.setData("OPT_USER", optUser);
		selParm.setData("OPT_TERM", optTerm);
		// ����״̬(0_δ����,1_�ѳ���,2_�ѹ黹)
		selParm.setData("ISSUE_CODE", "1");
		// �����ڿ�״̬
		selParm.setData("IN_FLG", "2");
		// ��ǰ���Ŀ���
		selParm.setData("CURT_LEND_DEPT_CODE", selParm.getValue("DEPT_CODE"));
		// ��ǰ������Ա
		selParm.setData("CURT_LEND_DR_CODE", selParm.getValue("DR_CODE"));
		// Ӧ�黹����
		String returnDate = "";
		
		// ֻ������ҺŲ��ڳ���ʱ�趨�黹ʱ�䣬סԺ���ڳ�Ժ�Ǽ�ʱ�趨�黹ʱ��
		if ("O".equals(pageParam)) {
			TParm queryParm = new TParm();
			queryParm.setData("LEND_TYPE", pageParam);
			
			// ��ѯ�����黹ʱ��
			result = MROLendTool.getInstance().selectdata(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ�����ֵ����");
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return false;
			}
			
			if (result.getCount() <= 0) {
				this.messageBox("�޶�Ӧ�Ľ����ֵ�����");
				return false;
			}
			
			// �������
			int intervalDay = result.getInt("LEND_DAY", 0);
			Date date = DateUtils.addDays(DateUtil.strToDate(selParm.getValue("QUE_DATE").substring(0, 10).replaceAll("-", "/")), intervalDay);
			// ����Ӧ�黹����
			returnDate = StringTool.getString(date, "yyyy/MM/dd");
		}
		
		// ��Խ��ĳ��⣬���ĳ����ڵǼ�ʱ�Ͱ��ս���ԭ���趨�˹黹����
		if (StringUtils.isEmpty(selParm.getValue("RTN_DATE"))) {
			// Ӧ�黹����,���ճ�������趨
			selParm.setData("RTN_DATE", returnDate);
		} else {
			selParm.setData("RTN_DATE", selParm.getValue("RTN_DATE").substring(0, 10).replaceAll("-", "/"));
		}

		executeParm.setData("MRV", selParm.getData());
		executeParm.setData("Queue", selParm.getData());

		// ִ�в�������
		result = TIOM_AppServer.executeAction("action.mro.MROQueueAction",
				"updateMroOutIn", executeParm);

		if (result.getErrCode() < 0) {
			this.messageBox("��ǰ�������ʧ��");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}

		// ���ɨ�赽�İ����
		clearValue("BOX_CODE");
		// �趨����
		grabFocus("BOX_CODE");

		return true;
	}
	
	/**
	 * ����Żس��¼�(ɨ�谸�������)
	 */
	public void onSaveByBoxCode() {
		TParm parmDown = this.tableDown.getParmValue();
		TParm parmUp = this.tableUp.getParmValue();
		
		if (StringUtils.isEmpty(this.getValueString("BOX_CODE").trim())) {
			return;
		}
		
		if (null == parmUp || parmUp.getCount() <= 0) {
			this.messageBox("�޾�������");
			return;
		}
		
		if (null == parmDown || parmDown.getCount() <= 0) {
			this.messageBox("�޾�������");
			return;
		}
		int len = parmDown.getCount();
		boolean existFlg = false;
		TParm queryParm = new TParm();
		TParm checkAdmInpParm = new TParm();
		
		// ������
		String archivesNo = "";
		for (int i = 0; i < len; i++) {
			archivesNo = parmDown.getValue("BOX_CODE", i) + "-" + parmDown.getValue("ADM_TYPE", i) + "-" + parmDown.getValue("BOOK_NO", i);
			// �ҵ���Ӧ���������
			if (StringUtils.equals(archivesNo, this.getValueString("BOX_CODE"))) {
				
				// �����ǰ�����Ѿ�����ѡ�������κδ���
				if (parmDown.getBoolean("FLG", i)) {
					this.messageBox("��ǰ�����Ѿ���ɳ���");
					// ���ɨ�赽�İ����
					clearValue("BOX_CODE");
					// �趨����
					grabFocus("BOX_CODE");
					return;
				}
				
				// ��֤�ð�����Ƿ��ڿ�
				queryParm.setData("ADM_TYPE", this.getValueString("BOX_CODE").split("-")[1]);
				queryParm.setData("IN_FLG", "2");
				queryParm.setData("BOX_CODE", this.getValueString("BOX_CODE").split("-")[0]);
				queryParm.setData("BOOK_NO", this.getValueString("BOX_CODE").split("-")[2]);
				
				queryParm = MROQueueTool.getInstance().selectMRO_MRV(queryParm);
				
				if (queryParm.getErrCode() < 0) {
					this.messageBox("��ѯ�����ڿ�״̬����");
					err(queryParm.getErrCode() + ":" + queryParm.getErrText());
					return;
				}
				
				if (queryParm.getCount() > 0) {
					this.messageBox("��ǰ�����Ѿ�����");
					// ���ɨ�赽�İ����
					clearValue("BOX_CODE");
					// �趨����
					grabFocus("BOX_CODE");
					return;
				}
				
				// ����סԺ��������ݣ�����ǰ���жϵ�ǰ�����Ƿ��Ѿ���Ժ�������Ժ�򲻿ɳ���
				if ("I".equals(pageParam)) {
					checkAdmInpParm.setData("CASE_NO", parmDown.getRow(i).getValue("CASE_NO"));
					// ��ѯ��Ժ������Ϣ
					checkAdmInpParm = ADMInpTool.getInstance().selectInHosp(checkAdmInpParm);
					
					if (checkAdmInpParm.getErrCode() < 0) {
						this.messageBox("��ѯסԺ���ݴ���");
						err("ERR:" + queryParm.getErrCode() + queryParm.getErrText());
						return;
					} else if (checkAdmInpParm.getCount() <= 0) {
						this.messageBox("��ǰ�����Ѿ���Ժ�����ɳ���");
						return;
					}
				}
				
				if (this.onSave(parmDown.getRow(i))) {
					// ��ѡ
					tableDown.setItem(i, "FLG", "Y");
					// �趨������
					this.setValue("MR_NO", parmDown.getValue("MR_NO", i));
					// �趨����
					this.setValue("PAT_NAME", parmDown.getValue("PAT_NAME", i));
					// �趨����
					this.setValue("DEPT_CODE", parmDown.getValue("DEPT_CODE", i));
					// �趨ҽ��
					this.setValue("DR_CODE", parmDown.getValue("DR_CODE", i));
					
					// ʵ����������
					this.onTableUpCount(parmDown.getRow(i));
				}
				
				existFlg = true;
				break;
			}
		}
		
		// û���ҵ���Ӧ�İ����
		if (!existFlg) {
			this.messageBox("��ǰ�������������޸ð��������");
			// ���ɨ�赽�İ����
			clearValue("BOX_CODE");
			// �趨����
			grabFocus("BOX_CODE");
			return;
		}
	}
	
	/**
	 * �������������
	 */
	private void onTableUpCount(TParm selParmDown) {
		TParm parmUp = tableUp.getParmValue();
		// ���ڲ�ͬ���ⷽʽ��Ӧҳ��������в�ͬ��ȡ�ö�Ӧ�����һ��λ�ã�����������
		int outPosition = this.tableUp.getParmMap().split(";").length;
		
		for (int i = 0; i < parmUp.getCount(); i++) {
			if (StringUtils.equals(selParmDown.getValue("QUE_DATE"), parmUp
					.getValue("QUE_DATE", i))
					&& StringUtils.equals(selParmDown.getValue("ADM_TYPE"),
							parmUp.getValue("ADM_TYPE", i))
					&& StringUtils.equals(selParmDown.getValue("DEPT_CODE"),
							parmUp.getValue("DEPT_CODE", i))
					&& StringUtils.equals(selParmDown.getValue("DR_CODE"),
							parmUp.getValue("DR_CODE", i))) {
				
				// �ҵ���Ӧ�����ݣ�ѡ�У���������ʵ�ʳ�������
				tableUp.setSelectedRow(i);
				int outCount = Integer.parseInt(String.valueOf(tableUp
						.getValueAt(i, outPosition - 1)));

				if (0 == outCount) {
					tableUp.setValueAt("1", i, outPosition - 1);
				} else {
					outCount = outCount + 1;
					tableUp.setValueAt(String.valueOf(outCount), i, outPosition - 1);
				}
				
				// ��ǰ����ҽ���µ����д�����İ�������󣬶�Ӧ��TABLE_UP�е�������Ҫ���²�ѯ
				if (StringUtils.equals(
						String.valueOf(tableUp.getValueAt(i, outPosition - 2)), String
								.valueOf(tableUp.getValueAt(i, outPosition - 1)))) {
					tableUp.removeRow(i);
				}
				
				break;
			}
		}
	}
	
	/**
	 * ��ӡ��������Ĳ�������
	 */
	public void onPrint() {
		TParm parm = tableDown.getParmValue();

		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		int dataLen = parm.getCount();
		
		// ��ӡ��Parm
		TParm printParm = new TParm();
		// ��ӡ����
		TParm printData = new TParm();
		// ��ӡ�ð����
		String boxCode = "";
		int printDataLen = 0;
		
		// ��ӡ���ݳ�������
		String archivesType = "";
		
		if ("O".equals(pageParam)) {
			archivesType = "���ﲡ��";
		} else if ("I".equals(pageParam)) {
			archivesType = "סԺ����";
		} else {
			archivesType = "���Ĳ���";
		}
		
		for (int i = 0; i < dataLen; i++) {
			if (!parm.getBoolean("FLG", i)) {
				// ��������
				printData.addData("ARCHIVES_TYPE", archivesType);
				
				boxCode = parm.getValue("BOX_CODE", i) + "-" + parm.getValue("ADM_TYPE", i) + "-" + parm.getValue("BOOK_NO", i);
				// �����
				printData.addData("BOX_CODE", boxCode);
				// ������
				printData.addData("MR_NO", parm.getValue("MR_NO", i));//parm.getValue("MR_NO", i)
				// ����
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				// ���Ŀ���
				printData.addData("DEPT_CODE", parm.getValue("DEPT_CHN_DESC", i));//
//				System.out.println("DEPT_CODE = = =" + parm.getValue("DEPT_CODE", i));
				// ������
				printData.addData("DR_CODE", parm.getValue("DR_NAME", i));//
//				System.out.println("DR_CODE = = =" + parm.getValue("DR_CODE", i));
				// ��������
				printData.addData("QUE_DATE", parm.getValue("QUE_DATE", i).substring(0, 10));//
//				System.out.println("DR_CODE = = =" + parm.getValue("DR_CODE", i));
				printDataLen = printDataLen + 1;
			}
		}
		//�������ͣ�����ţ������ţ����������Ŀ��ң������ˣ���������
		if (printDataLen < 1) {
			this.messageBox("û�д����ⰸ������");
			return;
		}
		
		printData.setCount(printDataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "ARCHIVES_TYPE");
		printData.addData("SYSTEM", "COLUMNS", "QUE_DATE");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		printData.addData("SYSTEM", "COLUMNS", "BOX_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
//		System.out.println("printData = = = = " + printData);
		printParm.setData("TITLE", "TEXT", archivesType + "����ȷ�ϵ�");
		
		printParm.setData("TABLE1", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROOutPrint.jhw", printParm);
	}
	
	/**
	 * ҳ��ؼ�������֤
	 */
	private boolean validate() {
		if (this.getValueString("OUT_S_DATE").length() < 10
				|| this.getValueString("OUT_E_DATE").length() < 10) {
			this.messageBox("�����ڼ�Ϊ������");
			return false;
		}
		return true;
	}
	
	/**
	 * ȡ������
	 */
	public void onDelete() {
		TParm parm = tableDown.getParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޳�������");
			return;
		}
		
		int selRow = this.tableDown.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ��Ҫȡ�������������");
			return;
		}
		
		int sel = this.messageBox("ѯ��", "�Ƿ�ȡ�����⣿", 0);
		if (0 == sel) {
			
			TParm selParm = parm.getRow(selRow);
			
			TParm result = MROQueueTool.getInstance().deleteMroOut(selParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("ϵͳ����");
				err(result.getErrCode() + ":" + result.getErrText());
				return;
			}
			
			this.messageBox("ȡ���ɹ�");
			
			// ���ݻ���
			this.onQuery();
		}
	}
	
	/**
	 * ��������
	 */
	public void onExport() {
		if (this.tableDown.getRowCount() > 0) {
			String title = "";
			// ����Һ�
			if ("O".equals(pageParam)) {
				title = "����";
			} else if ("I".equals(pageParam)) {
				title = "סԺ";
			} else {
				title = "����";
			}
			ExportExcelUtil.getInstance().exportExcel(tableDown, title + "��������");
		} else {
			this.messageBox("û����Ҫ����������");
			return;
		}
	}
	
	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
	
}
