package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.clp.CLPBillTool;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;

import java.util.HashMap;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �ٴ�·�����÷���
 * </p>
 * 
 * <p>
 * Description: �ٴ�·�����÷���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class CLPBillControl extends TControl {
	public CLPBillControl() {

	}

	// ��׼�ܷ���
	private double totalStandardFee = 0;
	// ʵ���ܷ���
	private double totalFactFee = 0;
	// ����סԺ��
	private String case_no;

	/**
	 * ҳ���ʼ������
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initPage() {
		TParm sendParm = (TParm) this.getParameter();
		case_no = sendParm.getValue("CASE_NO");
		this.setValue("CLNCPATH_CODE", sendParm.getValue("CLNCPATH_CODE"));
		// =========pangben 2012-6-4 start
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
				.getComponent("SCHD_CODE");
		combo_schd.setCaseNo(case_no);
		combo_schd.setClncpathCode(sendParm.getValue("CLNCPATH_CODE"));
		combo_schd.onQuery();
		this.setValue("SCHD_CODE", sendParm.getValue("DURATION_CODE"));// ��ǰʱ��
		// System.out.println("-------------case_no"+case_no);
		initBasicInfo();
		// ��ʼ����������
		initSchdDay();
		onQuery();
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initBasicInfo() {
		TParm selectParm = new TParm();
		selectParm.setData("CASE_NO", case_no);
		TParm result = CLPBillTool.getInstance().selectPatientData(selectParm);
		// System.out.println("��ѯ���Ļ�������:" + result);
		this.setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
		this.setValue("BED_NO", result.getValue("BED_NO", 0));
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		// this.setValue("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE", 0));
		// this.setValue("SCHD_CODE", result.getValue("SCHD_CODE", 0));
		this.setValue("AVERAGECOST", result.getValue("AVERAGECOST", 0));
	}

	/**
	 * ��ʼ����������
	 */
	private void initSchdDay() {
		TParm selectParm = new TParm();
		selectParm.setData("CASE_NO", case_no);
		selectParm.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));
		selectParm.setData("SCHD_CODE", this.getValue("SCHD_CODE")+"%");
		TParm result = CLPBillTool.getInstance().selectDurationSchdDay(
				selectParm);
		String schdDay = "";
		if (result.getCount() > 0) {
			schdDay = result.getValue("SCHD_DAY", 0);
		}
		this.setValue("SCHD_DAY", schdDay);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// ��ʼ���ܷ�����Ϣ
		this.totalFactFee = 0;
		this.totalStandardFee = 0;// totalVariance
		// ���÷���
		queryFeeData();
		// �����ܷ��úͱ�׼������Ϣ
		this.setValue("totalFactFee", StringTool.round(totalFactFee, 2) + "");
		this.setValue("totalStandardFee", StringTool.round(totalStandardFee, 2) + "");
		this.setValue("totalVariance", Math.abs(StringTool.round(totalFactFee
				- totalStandardFee, 2))
				+ "");
		// average charge
		TParm selectTParm = new TParm();
		selectTParm.setData("CLNCPATH_CODE", this
				.getValueString("CLNCPATH_CODE"));
		TParm avgParm = CLPBillTool.getInstance().getaverageCharge(selectTParm);
		this.setValue("AVERAGECOST", avgParm.getValue("AVERAGECOST", 0));
	}

	/**
	 * ����excel
	 */
	public void onExport() {
		TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("û�е�������");
			return;
		}
		// System.out.println("��ͷ"+table.getHeader());
		//
		// System.out.println("������"+table.getShowParmValue());
		ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·�����÷���ͳ�Ʊ�");
	}

	/**
	 * ���÷���
	 */
	private void queryFeeData() {
		TParm selectTParm = getSelectCondition();
		// System.out.println("��ѯ����:" + selectTParm);
		TParm result = CLPBillTool.getInstance().selectBillData(selectTParm);
		TParm tempParm=new TParm();
		// System.out.println("��ѯ�����" + result);
		TTable table = (TTable) this.getComponent("TABLE");
		String headerStr = "���,100;ʱ��,100,SCHD_CODE;�ܷ���,100;"
				+ getChargeHeader();
		table.setHeader(headerStr);
		//table.setItem("SCHD_CODE");
		String parmStr = "SCHD_TYPE_DESC;DURATION_CHN_DESC;TOT;";
		for (int i = 1; i <= 9; i++) {
			parmStr += "REXP_0" + i + ";";
		}
		//=======pangben 2012-6-14
		// 20110701 luhai modify ��ԭ�е�30�иĳ�20�У�ʵ��ʹ�õķ�����ֻ��20�У�����ķ����в�����
		// ���뱣֤�����������=ʵ��������
		for (int i = 10; i <= 30; i++) {
			parmStr += "REXP_" + i + ";";
		}
		table.setParmMap(parmStr);
		// ��������
		StringBuffer collockstr = new StringBuffer();
		for (int i = 0; i < 24; i++) {
			collockstr.append(i);
			if (i < 23) {
				collockstr.append(",");
			}
		}
		String sql=" SELECT DURATION_CODE,DURATION_CHN_DESC FROM  CLP_DURATION WHERE LEAF_FLG='N'";
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		// �ۼ�����
		String shcdCode = "";
		if (null != result.getValue("SCHD_CODE", 0)) {
			shcdCode = result.getValue("SCHD_CODE", 0).substring(0,1);
			String []name= result.getNames();
			//���һ�������� �ۼƲ���ʱʹ��
			for (int i = 0; i < name.length; i++) {
				tempParm.addData(name[i], "");
			}
		}
		double standardFee=0.00;//�ۼ�ÿ��ʱ�̵ı�׼��� ʵ�ʽ��
		double factFee=0.00; 
		tempParm.setCount(1);
		//===========pangben 2012-6-14 start �޸���ʾ �ۼ�ʱ�̻��ÿһ��ʱ�̸��ڵ���ܽ��
		TParm showParm=new TParm();
		int index=0;
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getValue("SCHD_CODE", i).contains(shcdCode)) {
				if ("1".equals(result.getValue("SCHD_TYPE", i))) {
					standardFee+=result.getDouble("TOT", i);
					this.totalStandardFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "��׼");
				} else if ("2".equals(result.getValue("SCHD_TYPE", i))) {
					factFee+=result.getDouble("TOT", i);
					this.totalFactFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "ʵ��");
				}
				showParm.setRowData(index, result, i);
			}else{
				String name="";
				for (int j = 0; j < selparm.getCount(); j++) {
					if (selparm.getValue("DURATION_CODE",j).equals(shcdCode)) {
						name=selparm.getValue("DURATION_CHN_DESC",j);
						break;
					}
				}
				
				tempParm.setData("SCHD_TYPE_DESC",0,"�ۼƱ�׼");
				tempParm.setData("TOT",0,StringTool.round(standardFee, 2));//��׼
				tempParm.setData("DURATION_CHN_DESC",0,name);//��ʾ���ڵ�����
				showParm.setRowData(index, tempParm,0);
				index++;
				tempParm.setData("SCHD_TYPE_DESC",0,"�ۼ�ʵ��");
				tempParm.setData("TOT",0,StringTool.round(factFee, 2));//��׼
				tempParm.setData("DURATION_CHN_DESC",0,name);//��ʾ���ڵ�����
				showParm.setRowData(index, tempParm,0);
				index++;
				standardFee=0.00;//��ֵ��ý��
				factFee=0.00;
				shcdCode=result.getValue("SCHD_CODE", i).substring(0,1);
				if ("1".equals(result.getValue("SCHD_TYPE", i))) {
					standardFee+=result.getDouble("TOT", i);
					this.totalStandardFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "��׼");
				} else if ("2".equals(result.getValue("SCHD_TYPE", i))) {
					factFee+=result.getDouble("TOT", i);
					this.totalFactFee += result.getDouble("TOT", i);
					result.setData("SCHD_TYPE_DESC", i, "ʵ��");
				}
				showParm.setRowData(index, result,i);
			}
			index++;
			if (i==result.getCount()-1) {
				shcdCode=result.getValue("SCHD_CODE", i).substring(0,1);
				String name="";
				for (int j = 0; j < selparm.getCount(); j++) {
					if (selparm.getValue("DURATION_CODE",j).equals(shcdCode)) {
						name=selparm.getValue("DURATION_CHN_DESC",j);
						break;
					}
				}
				tempParm.setData("SCHD_TYPE_DESC",0,"�ۼƱ�׼");
				tempParm.setData("TOT",0,StringTool.round(standardFee, 2));//��׼
				tempParm.setData("DURATION_CHN_DESC",0,name);//��ʾ���ڵ�����
				showParm.setRowData(index, tempParm,0);
				index++;
				tempParm.setData("SCHD_TYPE_DESC",0,"�ۼ�ʵ��");
				tempParm.setData("TOT",0,StringTool.round(factFee, 2));//��׼
				tempParm.setData("DURATION_CHN_DESC",0,name);//��ʾ���ڵ�����
				showParm.setRowData(index, tempParm,0);
				index++;
			}
		}
		showParm.setCount(index);
		table.setLockColumns(collockstr.toString());
		// this.callFunction("UI|TABLE|setParmValue",
		// getTableParmWithSelectData(result));
		this.callFunction("UI|TABLE|setParmValue", showParm);
		// TTable table = (TTable)this.getComponent("TABLE");

	}

	/**
	 * �����ѯ��table
	 * 
	 * @param parm
	 *            TParm
	 */
	private TParm getTableParmWithSelectData(TParm parm) {
		TParm returnparm = new TParm();
		int totalCount = 0;
		double tot = 0;
		double standard = 0;
		String schd_code = "";
		for (int i = 0; i < parm.getCount(); i++) {
			boolean isadd = false;
			TParm rowparm = parm.getRow(i);
			String totstr = rowparm.getValue("TOT");
			double rowtot = Double.parseDouble(totstr);
			returnparm.addData("SCHD_TYPE_DESC", rowparm
					.getValue("SCHD_TYPE_DESC"));
			returnparm.addData("SCHD_CODE", rowparm.getValue("SCHD_CODE"));
			returnparm.addData("TOT", rowtot);
			addChargeDetail(returnparm, rowparm);
			schd_code = rowparm.getValue("SCHD_CODE");
			if (i == 0) {
				tot = 0;
				standard = 0;

			}
			if (!parm.getValue("SCHD_CODE", (i + 1)).equals(schd_code)
					&& i != 0) {
				isadd = true;
			}
			if ("1".equals(rowparm.getValue("SCHD_TYPE"))) {
				// System.out.println("��׼----------------");
				standard += rowtot;
				// �����ܱ�׼����
				totalStandardFee += rowtot;
			} else if ("2".equals(rowparm.getValue("SCHD_TYPE"))) {
				// System.out.println("ʵ��----------------");
				tot += rowtot;
				// ����ʵ���ܷ���
				totalFactFee += rowtot;
			}
			// �����һ��ʱҲ��Ҫͳ���±�׼��ʵ�ʵ�����
			if (i != 0 && i == (parm.getCount() - 1)) {
				isadd = true;
			}
			// ����������
			totalCount++;
			if (isadd) {
				returnparm.addData("SCHD_TYPE_DESC", "�ۼƱ�׼");
				returnparm.addData("SCHD_CODE", rowparm.getValue("SCHD_CODE"));
				returnparm.addData("TOT", standard);
				addChargeDetail(returnparm);
				returnparm.addData("SCHD_TYPE_DESC", "�ۼ�ʵ��");
				returnparm.addData("SCHD_CODE", rowparm.getValue("SCHD_CODE"));
				returnparm.addData("TOT", tot);
				addChargeDetail(returnparm);
				tot = 0;
				standard = 0;
				// �¼�������ҲҪ����������
				totalCount += 2;// ��׼��ʵ���ܹ���������
			}
		}
		returnparm.setCount(totalCount);
		return returnparm;
	}

	/**
	 * ��ӷ�����ϸ����û����ӿ�
	 * 
	 * @param chargeParm
	 *            TParm
	 */
	private void addChargeDetail(TParm toParm, TParm chargeParm) {
		String tmpcol = "REXP_0";
		for (int i = 1; i <= 9; i++) {
			toParm.addData((tmpcol + i), chargeParm.getValue(tmpcol + i));
		}
		tmpcol = "REXP_";
		for (int i = 10; i <= 30; i++) {
			toParm.addData((tmpcol + i), chargeParm.getValue(tmpcol + i));
		}
	}

	private void addChargeDetail(TParm toParm) {
		addChargeDetail(toParm, new TParm());
	}

	private String getChargeHeader() {
		StringBuffer header = new StringBuffer();
		TParm parm = CLPBillTool.getInstance().getChargeHeader(new TParm());
		TParm bilParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, "
										+ "CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, "
										+ "CHARGE16, CHARGE17, CHARGE18, CHARGE19, CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, "
										+ "CHARGE26, CHARGE27, CHARGE28, CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS' "));
		// ============pangben 2012-6-4 �޸ı�ͷ����start
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				if (null != bilParm.getValue("CHARGE0" + i, 0)
						&& bilParm.getValue("CHARGE0" + i, 0).length() > 0) {
					for (int j = 0; j < parm.getCount(); j++) {
						if (bilParm.getValue("CHARGE0" + i, 0).equals(
								parm.getValue("ID", j))) {
							header.append(parm.getValue("CHN_DESC", j)
									+ ",100;");
							break;
						}

					}
				}
			} else {
				if (null != bilParm.getValue("CHARGE" + i, 0)
						&& bilParm.getValue("CHARGE" + i, 0).length() > 0) {
					for (int j = 0; j < parm.getCount(); j++) {
						if (bilParm.getValue("CHARGE" + i, 0).equals(
								parm.getValue("ID", j))) {
							header.append(parm.getValue("CHN_DESC", j)
									+ ",100;");
							break;
						}

					}
				}
			}
			// ============pangben 2012-6-4 stop
		}
		return header.toString().substring(0, header.toString().length() - 1);
	}

	private TParm getSelectCondition() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		this.putParamWithObjNameForQuery("CLNCPATH_CODE", parm);
		// this.putParamWithObjNameForQuery("SCHD_CODE", parm);
		String objstr = this.getValueString("SCHD_CODE");
		if (objstr != null && objstr.length() > 0) {
			// ����ֵ��ؼ�����ͬ
			parm.setData("SCHD_CODE", objstr.trim() + "%");
		}
		this.putParamWithObjNameForQuery("SCHD_DAY", parm);
		parm.setData("REGION_CODE", this.getBasicOperatorMap().get(
				"REGION_CODE"));
		return parm;
	}

	public void onClear() {
		List<String> list = new ArrayList<String>();
		// list.add("CLNCPATH_CODE");
		list.add("SCHD_CODE");
		list.add("SCHD_DAY");
		// list.add("AVERAGECOST");
		// list.add("");
		// list.add("");
		// list.add("");
		// list.add("");
		this.clearInput(list);
	}

	/**
	 * �����Ķ�Ӧ��Ԫ�����óɿ�д�����������óɲ���д
	 * 
	 * @param tableName
	 *            String
	 * @param rowNum
	 *            int
	 * @param columnNum
	 *            int
	 */
	private void setTableEnabled(String tableName, int rowNum, int columnNum) {
		TTable table = (TTable) this.getComponent(tableName);
		int totalColumnMaxLength = table.getColumnCount();
		int totalRowMaxLength = table.getRowCount();
		// System.out.println("��������" + totalColumnMaxLength + "������:" +
		// totalRowMaxLength);
		// ����
		String lockColumnStr = "";
		for (int i = 0; i < totalColumnMaxLength; i++) {
			if (!(i + "").equals(columnNum + "")) {
				lockColumnStr += i + ",";
			}
		}
		lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
		// System.out.println("���д���" + lockColumnStr);
		table.setLockColumns(lockColumnStr);
		// ����
		String lockRowStr = "";
		for (int i = 0; i < totalRowMaxLength; i++) {
			if (!(i + "").equals(rowNum + "")) {
				lockRowStr += i + ",";
			}
		}
		// System.out.println("���д�ǰ��" + lockRowStr + "����" + totalRowMaxLength);
		lockRowStr = lockRowStr.substring(0, ((lockRowStr.length() - 1) < 0 ? 0
				: (lockRowStr.length() - 1)));
		// System.out.println("���д���" + lockRowStr);
		if (lockRowStr.length() > 0) {
			table.setLockRows(lockRowStr);
		}

	}

	/**
	 * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		objstr = objstr;
		parm.setData(paramName, objstr);
	}

	/**
	 * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		// System.out.println(objstr);
		objstr = objstr;
		// ����ֵ��ؼ�����ͬ
		parm.setData(objName, objstr);
	}

	/**
	 * ���ؼ�ֵ����TParam����(���Դ�����ò���ֵ)
	 * 
	 * @param objName
	 *            String
	 */
	private void putParamLikeWithObjName(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr + "%";
			parm.setData(paramName, objstr);
		}

	}

	/**
	 * ���ؼ�ֵ����TParam����(���ò���ֵ��ؼ�����ͬ)
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamLikeWithObjName(String objName, TParm parm) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			objstr = "%" + objstr.trim() + "%";
			// ����ֵ��ؼ�����ͬ
			parm.setData(objName, objstr);
		}
	}

	/**
	 * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm) {
		putParamWithObjNameForQuery(objName, parm, objName);
	}

	/**
	 * ���ڷ���������ȫƥ����в�ѯ�Ŀؼ�
	 * 
	 * @param objName
	 *            String
	 * @param parm
	 *            TParm
	 * @param paramName
	 *            String
	 */
	private void putParamWithObjNameForQuery(String objName, TParm parm,
			String paramName) {
		String objstr = this.getValueString(objName);
		if (objstr != null && objstr.length() > 0) {
			// ����ֵ��ؼ�����ͬ
			parm.setData(paramName, objstr.trim());
		}
	}

	/**
	 * ���ؼ��Ƿ�Ϊ��
	 * 
	 * @param componentName
	 *            String
	 * @return boolean
	 */
	private boolean checkComponentNullOrEmpty(String componentName) {
		if (componentName == null || "".equals(componentName)) {
			return false;
		}
		String valueStr = this.getValueString(componentName);
		if (valueStr == null || "".equals(valueStr)) {
			return false;
		}
		return true;
	}

	/**
	 * �õ�ָ��table��ѡ����
	 * 
	 * @param tableName
	 *            String
	 * @return int
	 */
	private int getSelectedRow(String tableName) {
		int selectedIndex = -1;
		if (tableName == null || tableName.length() <= 0) {
			return -1;
		}
		Object componentObj = this.getComponent(tableName);
		if (!(componentObj instanceof TTable)) {
			return -1;
		}
		TTable table = (TTable) componentObj;
		selectedIndex = table.getSelectedRow();
		return selectedIndex;
	}

	/**
	 * ������֤����
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validNumber(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * ��TParm�м���ϵͳĬ����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		int total = parm.getCount();
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getIP());
	}

	/**
	 * ����Operator�õ�map
	 * 
	 * @return Map
	 */
	private Map getBasicOperatorMap() {
		Map map = new HashMap();
		map.put("REGION_CODE", Operator.getRegion());
		map.put("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		map.put("OPT_DATE", datestr);
		map.put("OPT_TERM", Operator.getIP());
		return map;
	}

	/**
	 * �õ���ǰʱ���ַ�������
	 * 
	 * @param dataFormatStr
	 *            String
	 * @return String
	 */
	private String getCurrentDateStr(String dataFormatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataFormatStr);
		return datestr;
	}

	/**
	 * �õ���ǰʱ���ַ�������
	 * 
	 * @return String
	 */
	private String getCurrentDateStr() {
		return getCurrentDateStr("yyyyMMdd");
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * ����TParm
	 * 
	 * @param from
	 *            TParm
	 * @param to
	 *            TParm
	 * @param row
	 *            int
	 */
	private void cloneTParm(TParm from, TParm to, int row) {
		for (int i = 0; i < from.getNames().length; i++) {
			to.addData(from.getNames()[i], from.getValue(from.getNames()[i],
					row));
		}
	}

	/**
	 * ��¡����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm cloneTParm(TParm from) {
		TParm returnTParm = new TParm();
		for (int i = 0; i < from.getNames().length; i++) {
			returnTParm.setData(from.getNames()[i], from.getValue(from
					.getNames()[i]));
		}
		return returnTParm;
	}

	/**
	 * ����TParm ���null�ķ���
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNullVector(TParm parm, String keyStr, Class type) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getData(keyStr, i) == null) {
				// System.out.println("����Ϊ�����");
				TNull tnull = new TNull(type);
				parm.setData(keyStr, i, tnull);
			}
		}
	}

	/**
	 * ����TParm ��nullֵ����
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNull(TParm parm, String keyStr, Class type) {
		if (parm.getData(keyStr) == null) {
			// System.out.println("����Ϊ�����");
			TNull tnull = new TNull(type);
			parm.setData(keyStr, tnull);
		}
	}

	/**
	 * ��ռ����ж�Ӧ��������ֵ
	 * 
	 * @param inputNames
	 *            List
	 */
	private void clearInput(List<String> inputNames) {
		for (String inputstr : inputNames) {
			this.setValue(inputstr, "");
		}
	}

}
