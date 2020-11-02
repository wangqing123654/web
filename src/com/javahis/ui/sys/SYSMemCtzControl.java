package com.javahis.ui.sys;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import jdo.mem.MEMCtzTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:�������
 * </p>
 * 
 * <p>
 * Description:�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author duzhw 2013.12.30
 * @version 1.0
 */
public class SYSMemCtzControl extends TControl {
	TParm delParm = new TParm();// ҽ��ɾ��
	TParm updateParm = new TParm();// ҽ���޸�
	String ctzCodeOper = "";// ȫ�� ˢ��
	double rate = 0.0000;// �ۿ���

	/**
	 * �õ����е��վ����
	 */
	TParm allCode = SYSChargeHospCodeTool.getInstance().selectalldata();

	/**
	 * ��ݱ�
	 */
	private TTable tableCtz;
	/**
	 * ��Ŀ�����ۿ۱�
	 */
	private TTable mainTable;
	/**
	 * ��Ŀ��ϸ�ۿ۱�
	 */
	private TTable detailTable;

	// ��Ŀ����������
	private int mainRow;
	// ���÷�Χ
	TCheckBox typeO;
	TCheckBox typeE;
	TCheckBox typeI;
	TCheckBox typeH;

	TCheckBox typeCtzDept;
	TCheckBox typeMainCtz;
	TCheckBox typeNhiCtz;
	TCheckBox typeMrctzUpd;

	TCheckBox mainCtzFlg;// �����
	TCheckBox qUseFlg;// ����ʱ��

	TCheckBox typeUse;
	TComboBox deptCode1;// ����������
	TComboBox deptCode;// ����������-�ұ�
	TTextFormat ctzCode1;// �������������

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();

		initComponent();
		initData();
		// ����ϸdetailTable��������¼�
		// addEventListener("DETILTABLE->" + TTableEvent.CHANGE_VALUE,
		// "onTableChangeValue");
		// �ۿ�Ĭ��Ϊ1,������
		this.setValue("DISCOUNT", "1.0000");
		this.setValue("SERVICE_LEVEL", 1); // Ĭ�Ϸ���ȼ�Ϊ�Է�
	}

	/**
	 * ��ʼ������
	 */
	public void initData() {

		// ��ѯ��ʼ����ʱ���û�
		callFunction("UI|S_DATE|setEnabled", false);
		callFunction("UI|E_DATE|setEnabled", false);

		String sql = "SELECT * FROM SYS_CTZ ORDER BY CTZ_CODE,SEQ ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("result==="+result);
		tableCtz.setParmValue(result);
		ifUseFlg();// ��ʼ���Ƿ�����״̬
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		tableCtz = getTable("TABLECTZ");
		mainTable = getTable("MAINTABLE");
		detailTable = getTable("DETILTABLE");

		TParm parm = new TParm();
		mainTable.setParmValue(parm);
		detailTable.setParmValue(parm);

		// �õ����÷�Χ�ؼ�
		typeO = (TCheckBox) this.getComponent("OPD_FIT_FLG");
		typeE = (TCheckBox) this.getComponent("EMG_FIT_FLG");
		typeI = (TCheckBox) this.getComponent("IPD_FIT_FLG");
		typeH = (TCheckBox) this.getComponent("HRM_FIT_FLG");

		typeCtzDept = (TCheckBox) this.getComponent("CTZ_DEPT_FLG");
		typeMainCtz = (TCheckBox) this.getComponent("MAIN_CTZ_FLG");
		typeNhiCtz = (TCheckBox) this.getComponent("NHI_CTZ_FLG");
		typeMrctzUpd = (TCheckBox) this.getComponent("MRCTZ_UPD_FLG");
		mainCtzFlg = (TCheckBox) this.getComponent("MAINCTZFLG");
		qUseFlg = (TCheckBox) this.getComponent("Q_USE_FLG");

		typeUse = (TCheckBox) this.getComponent("USE_FLG");
		deptCode1 = (TComboBox) this.getComponent("DEPT_CODE1");// ����������1
		deptCode = (TComboBox) this.getComponent("DEPT_CODE");// ����������-�ұ�
		ctzCode1 = (TTextFormat) this.getComponent("CTZCODE");// �������������

		// �ײ�ϸ������ҽ���¼�
		detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDetailCreateEditComponent");
		// �ײ�ϸ��ֵ�ı��¼�
		detailTable.addEventListener("DETILTABLE->" + TTableEvent.CHANGE_VALUE,
				this, "onTableChangeValue");

	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onDetailCreateEditComponent(Component com, int row, int column) {
		// this.messageBox("-----onDetailCreateEditComponent in-----");
		// �����ǰ�кţ�ֻ������ҽ������������һ��ҽ��
		column = detailTable.getColumnModel().getColumnIndex(column);
		String columnName = detailTable.getParmMap(column);
		if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		// String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
		TTextField textfield = (TTextField) com;
		// this.messageBox("=====textfield==="+textfield.getValue());
		textfield.onInit();
		TParm parm = new TParm();
		// parm.setData("HRM_TYPE", "ANYCHAR");
		// ��table�ϵ���text����sys_fee��������
		textfield
				.setPopupMenuParameter(
						"ORDER",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");
	}

	/**
	 * ����ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// �ж��Ƿ���ҩƷ(PHA_WҩƷ)
		String orderCat1Code = parm.getValue("ORDER_CAT1_CODE");
		int row = detailTable.getSelectedRow();
		int oldRow = row;
		// if (!StringUtil.isNullString(orderTable.getItemString(row,
		// "ORDER_CODE"))) {
		// return;
		// }
		String order_code = parm.getValue("ORDER_CODE");
		String order_desc = parm.getValue("ORDER_DESC");
		// �ж��Ƿ����ظ�����
		for (int i = 0; i < detailTable.getDataStore().rowCount(); i++) {
			if (i == detailTable.getSelectedRow()) {
				continue;
			}
			if (order_code.equals(detailTable.getDataStore().getItemData(i,
					"ORDER_CODE"))
					&& detailTable.getDataStore().getItemData(i, "SETMAIN_FLG")
							.equals("Y")) {
				this.messageBox(order_desc + "(" + order_code + ") �Ѵ���");
				return;
			}
		}
		detailTable.acceptText();

		String orderSetCode = parm.getValue("ORDER_CODE");
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, B.DOSAGE_QTY,"
				+ " A.UNIT_CODE AS MEDI_UNIT, A.OWN_PRICE,A.OWN_PRICE AS PACKAGE_PRICE, A.OWN_PRICE * B.DOSAGE_QTY "
				+ " AS OWN_AMT, A.EXEC_DEPT_CODE, A.OPTITEM_CODE, A.INSPAY_TYPE "
				+ " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' "
				+ " AND B.ORDERSET_CODE = '" + orderSetCode + "'";
		// System.out.println("sql===----"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ҩ��
		String sqlPha = " SELECT OWN_PRICE FROM SYS_FEE WHERE ACTIVE_FLG = 'Y'"
				+ " AND ORDER_CODE = '" + orderSetCode + "'";
		System.out.println("sqlPha" + sqlPha);
		TParm resultPha = new TParm(TJDODBTool.getInstance().select(sqlPha));
		detailTable.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		detailTable.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		detailTable.setItem(row, "ORDER_DESC_DETAIL", "");
		detailTable.setItem(row, "ORDER_CODE_DETAIL", "");
		detailTable.setItem(row, "UNIT_PRICE",
				resultPha.getValue("OWN_PRICE", 0));

		// ������ҽ�� ϸ��
		if (result.getCount("ORDER_CODE") > 0) {
			for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
				insertRow("DETILTABLE", false);// ����һ�п�����
				detailTable.setItem(row + i + 1, "ORDER_DESC",
						parm.getValue("ORDER_DESC"));
				detailTable.setItem(row + i + 1, "ORDER_CODE",
						parm.getValue("ORDER_CODE"));
				detailTable.setItem(row + i + 1, "ORDER_DESC_DETAIL",
						result.getValue("ORDER_DESC", i));
				detailTable.setItem(row + i + 1, "ORDER_CODE_DETAIL",
						result.getValue("ORDER_CODE", i));
				detailTable.setItem(row + i + 1, "UNIT_PRICE",
						result.getValue("OWN_AMT", i));
			}

		}

		insertRow("DETILTABLE", true);// ����һ�п�����
		parm = null;
	}

	/**
	 * ��TABLE����¼�
	 */
	public void onCtzClick() {
		// ��ݴ���״̬����Ч
		callFunction("UI|CTZ_CODE|setEnabled", false);

		int selectedIndx = tableCtz.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		TParm tableparm = tableCtz.getParmValue();

		String ctzCode = tableparm.getValue("CTZ_CODE", selectedIndx);
		String ctzDesc = tableparm.getValue("CTZ_DESC", selectedIndx);
		String memType = tableparm.getValue("MEM_TYPE", selectedIndx);
		String memCode = tableparm.getValue("MEM_CODE", selectedIndx);// ��Ա����
		String deptCode = tableparm.getValue("DEPT_CODE", selectedIndx);
		String discountRate = tableparm.getValue("DISCOUNT_RATE", selectedIndx);
		String py1 = tableparm.getValue("PY1", selectedIndx);
		String descript = tableparm.getValue("DESCRIPT", selectedIndx);
		String useFlg = tableparm.getValue("USE_FLG", selectedIndx);
		String seq = tableparm.getValue("SEQ", selectedIndx);
		String overdraft = tableparm.getValue("OVERDRAFT", selectedIndx);
		String mroCtz = tableparm.getValue("MRO_CTZ", selectedIndx);
		String nhiNo = tableparm.getValue("NHI_NO", selectedIndx);// ҽ����� add by
																	// huangjw
																	// 20141011
		String level = tableparm.getValue("SERVICE_LEVEL", selectedIndx);// ����ȼ�
																			// add
																			// by
																			// huangtt
																			// 20160119
		if ("Y".equals(useFlg)) {
			typeUse.setSelected(true);
		} else {
			typeUse.setSelected(false);
		}
		ifUseFlg();// �ж��Ƿ�����״̬
		String startDate = tableparm.getValue("START_DATE", selectedIndx);
		if (startDate.length() > 0) {
			startDate = startDate.replaceAll("-", "/").substring(0, 10);
		}
		String endDate = tableparm.getValue("END_DATE", selectedIndx);
		if (endDate.length() > 0) {
			endDate = endDate.replaceAll("-", "/").substring(0, 10);
		}
		String ctzDeptFlg = tableparm.getValue("CTZ_DEPT_FLG", selectedIndx);
		if ("Y".equals(ctzDeptFlg)) {
			typeCtzDept.setSelected(true);
		} else {
			typeCtzDept.setSelected(false);
		}
		String mainCtzFlg = tableparm.getValue("MAIN_CTZ_FLG", selectedIndx);
		if ("Y".equals(mainCtzFlg)) {
			typeMainCtz.setSelected(true);
		} else {
			typeMainCtz.setSelected(false);
		}
		String nhiCtzFlg = tableparm.getValue("NHI_CTZ_FLG", selectedIndx);
		if ("Y".equals(nhiCtzFlg)) {
			typeNhiCtz.setSelected(true);
		} else {
			typeNhiCtz.setSelected(false);
		}
		String mrctzUpdFlg = tableparm.getValue("MRCTZ_UPD_FLG", selectedIndx);
		if ("Y".equals(mrctzUpdFlg)) {
			typeMrctzUpd.setSelected(true);
		} else {
			typeMrctzUpd.setSelected(false);
		}
		String flgO = tableparm.getValue("OPD_FIT_FLG", selectedIndx);
		if ("Y".equals(flgO)) {
			typeO.setSelected(true);
		} else {
			typeO.setSelected(false);
		}
		String flgE = tableparm.getValue("EMG_FIT_FLG", selectedIndx);
		if ("Y".equals(flgE)) {
			typeE.setSelected(true);
		} else {
			typeE.setSelected(false);
		}
		String flgI = tableparm.getValue("IPD_FIT_FLG", selectedIndx);
		if ("Y".equals(flgI)) {
			typeI.setSelected(true);
		} else {
			typeI.setSelected(false);
		}
		String flgH = tableparm.getValue("HRM_FIT_FLG", selectedIndx);
		if ("Y".equals(flgH)) {
			typeH.setSelected(true);
		} else {
			typeH.setSelected(false);
		}

		this.setValue("CTZ_CODE", ctzCode);
		this.setValue("CTZ_DESC", ctzDesc);
		this.setValue("MEM_TYPE", memType);
		this.setValue("MEM_CODE", memCode);
		this.setValue("DEPT_CODE", deptCode);
		this.setValue("DISCOUNT", discountRate);
		this.setValue("PY1", py1);
		this.setValue("DESCRIPT", descript);
		this.setValue("START_DATE", startDate);
		this.setValue("END_DATE", endDate);
		this.setValue("SEQ", seq);
		this.setValue("OVERDRAFT", overdraft);
		this.setValue("MRO_CTZ", mroCtz);

		this.setValue("CTZ_CODE3", ctzCode);
		this.setValue("DEPT_CODE3", deptCode);
		this.setValue("NHI_NO_Q", nhiNo);// ҽ����� add by huangjw 20141011
		this.setValue("SERVICE_LEVEL", level);// ����ȼ� add by huangtt 20160119
		rate = Double.parseDouble(discountRate);// ȫ������

		ctzCodeOper = ctzCode;
		queryDetailData(ctzCode);

	}

	/**
	 * ��ϸ���ѯ
	 */
	public void queryDetailData(String ctzCode) {
		String sql = "SELECT 'Y' AS EXEC,B.DEPT_CODE,B.CTZ_DESC,A.CTZ_CODE,A.CHARGE_HOSP_CODE,"
				+ " A.DISCOUNT_RATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,C.USER_NAME,D.CHARGE_HOSP_DESC "
				+ " FROM SYS_CHARGE_DETAIL A,sys_ctz B,sys_operator C,SYS_CHARGE_HOSP D"
				+ " WHERE A.CTZ_CODE = B.CTZ_CODE AND A.OPT_USER = C.USER_ID AND A.CHARGE_HOSP_CODE = D.CHARGE_HOSP_CODE AND A.CTZ_CODE = '"
				+ ctzCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("sql="+sql);
		// System.out.println("result="+result);
		mainTable.setParmValue(result);
		// mainTable.acceptText();
		// -------------��ϸ�б�-----------------

		// �ֿ���ѯ��ʾ
		String sql2 = "(SELECT 'Y' AS EXEC, B.DEPT_CODE,B.CTZ_DESC,A.CTZ_CODE,A.ORDER_CODE,"
				+ " A.ORDER_DESC,'' AS ORDER_CODE_DETAIL,'' AS ORDER_DESC_DETAIL,A.DISCOUNT_RATE,A.OPT_USER,"
				+ " A.OPT_DATE,A.OPT_TERM,A.ORDERSET_CODE,A.HIDE_FLG,A.SETMAIN_FLG,A.UNIT_PRICE,C.USER_NAME "
				+ " FROM SYS_CTZ_ORDER_DETAIL A,SYS_CTZ B,sys_operator C"
				+ " WHERE A.CTZ_CODE = B.CTZ_CODE AND A.OPT_USER = C.USER_ID  AND A.CTZ_CODE = '"
				+ ctzCode
				+ "'"
				+ " AND A.SETMAIN_FLG = 'Y') "
				+ " UNION (SELECT 'Y' AS EXEC, B.DEPT_CODE,B.CTZ_DESC,A.CTZ_CODE,'' AS ORDER_CODE,"
				+ " '' AS ORDER_DESC,A.ORDER_CODE AS ORDER_CODE_DETAIL,A.ORDER_DESC AS ORDER_DESC_DETAIL,A.DISCOUNT_RATE,A.OPT_USER,"
				+ " A.OPT_DATE,A.OPT_TERM,A.ORDERSET_CODE,A.HIDE_FLG,A.SETMAIN_FLG,A.UNIT_PRICE,C.USER_NAME"
				+ " FROM SYS_CTZ_ORDER_DETAIL A,SYS_CTZ B,sys_operator C  "
				+ " WHERE A.CTZ_CODE = B.CTZ_CODE AND A.OPT_USER = C.USER_ID AND A.CTZ_CODE = '"
				+ ctzCode
				+ "'"
				+ " AND A.SETMAIN_FLG = 'N') ORDER BY ORDERSET_CODE  ";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		// 20150702 wangjc add start
		// �����ۺ�����ʾ��ֻ�ڽ�����ʾ�������浽���ݿ�
		for (int i = 0; i < result2.getCount(); i++) {
			// add by huangtt 20160119 ϸ���еļ�Ǯ�� sys_fee�е�own_price�Ƿ�һ�£������ͬ��Ҫ����
			String orderCode = "";
			if (result2.getBoolean("SETMAIN_FLG", i)) {
				orderCode = result2.getValue("ORDER_CODE", i);
			} else {
				orderCode = result2.getValue("ORDER_CODE_DETAIL", i);
			}
			double ownPrice = getSysFeePrice(orderCode);
			if (ownPrice != result2.getDouble("UNIT_PRICE", i)) {
				result2.setData("UNIT_PRICE", i, ownPrice);
				result2.setData("EXEC", i, "U");
			}
			// add by huangtt 20160119
			result2.setData("NEW_PRICE", i, result2.getDouble("UNIT_PRICE", i)
					* result2.getDouble("DISCOUNT_RATE", i));
		}
		// 20150702 wangjc add end
		// detailTable.removeRowAll();
		result2 = this.getDetailTableFormat(result2);
		detailTable.setParmValue(result2);
		// System.out.println("result2="+result2);
		insertRow("DETILTABLE", true);// ����һ�п�����
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		boolean mainCtzFlg1 = false;// ������Ƿ��ѡ
		if (mainCtzFlg.isSelected()) {
			mainCtzFlg1 = true;
		}
		boolean qUseFlg1 = false;// ����ʱ���Ƿ��ѡ
		if (qUseFlg.isSelected()) {
			qUseFlg1 = true;
		}
		String sDate = "";
		String eDate = "";
		sDate = this.getValueString("S_DATE");
		eDate = this.getValueString("E_DATE");
		if (qUseFlg1) {
			// У�鿪ʼ����ʱ��
			if (sDate.length() > 0) {
				sDate = sDate.replaceAll("-", "/").substring(0, 10)
						+ " 00:00:00";
			}
			if (eDate.length() > 0) {
				eDate = eDate.replaceAll("-", "/").substring(0, 10)
						+ " 23:59:59";
			}
		}
		String deptCode = this.getComboBox("DEPT_CODE1").getSelectedID();
		String ctzCode = getValueString("CTZCODE");

		// System.out.println("mainCtzFlg1="+mainCtzFlg1+" deptCode="+deptCode+" ctzCode="+ctzCode);
		String querySql = "SELECT A.* FROM SYS_CTZ A " + " WHERE 1=1 ";
		if (mainCtzFlg1) {
			querySql += " AND A.MAIN_CTZ_FLG = 'Y' ";
		}
		if (qUseFlg1) {
			querySql += " AND A.USE_FLG = 'Y' ";
			if (sDate.length() > 0 && eDate.length() > 0) {
				querySql += " AND A.START_DATE BETWEEN TO_DATE('" + sDate
						+ "','yyyy/mm/dd hh24:mi:ss') " + " AND TO_DATE('"
						+ eDate + "','yyyy/mm/dd hh24:mi:ss')";
			}
		}
		if (deptCode.length() > 0) {
			querySql += " AND A.DEPT_CODE = '" + deptCode + "' ";
		}
		if (ctzCode.length() > 0) {
			querySql += " AND A.CTZ_CODE = '" + ctzCode + "' ";
		}
		querySql += " ORDER BY CTZ_CODE,SEQ ";
		// System.out.println("��ѯsql="+querySql);
		TParm result = new TParm(TJDODBTool.getInstance().select(querySql));
		if (result.getCount() <= 0) {
			this.messageBox("�������ݣ�");
			tableCtz.removeRowAll();
			onClear();// �������
			return;
		}
		tableCtz.setParmValue(result);

	}

	/**
	 * ��������
	 */
	public void onExport() {
		System.out.println("111111");
		TTable tableup = (TTable) getComponent("MAINTABLE");
		TTable tabledown = (TTable) getComponent("DETILTABLE");

		TParm parmup = new TParm();
		TParm parmdown = new TParm();
		TParm exportParm = new TParm();

		parmup = tableup.getParmValue();
		parmdown = tabledown.getParmValue();
		System.out.println("111111111111" + parmup);
		System.out.println("111111111111" + parmdown);

		int count = 0;
		String header = "��0;��1;��2;��3;��4;��5;��6;��7;��8;��9";
		if (parmup.getCount() > 0) {
			exportParm.setData("COLUMN0", count, "Ժ�ڷ��ô���");
			exportParm.setData("COLUMN1", count, "���۱���");
			exportParm.setData("COLUMN2", count, "������Ա");
			exportParm.setData("COLUMN3", count, "��������");
			exportParm.setData("COLUMN4", count, "������ĩ");
			exportParm.setData("COLUMN5", count, "");
			exportParm.setData("COLUMN6", count, "");
			exportParm.setData("COLUMN7", count, "");
			exportParm.setData("COLUMN8", count, "");
			exportParm.setData("COLUMN9", count, "");
			count++;
			if (this.getValue("USE_FLG").equals("Y")) {
				exportParm.setData("COLUMN0", count, "����");
			} else {
				exportParm.setData("COLUMN0", count, "������");
			}
			exportParm.setData("COLUMN1", count, "");
			exportParm.setData("COLUMN2", count, "");
			exportParm.setData("COLUMN3", count, "");
			exportParm.setData("COLUMN4", count, "");
			exportParm.setData("COLUMN5", count, "");
			exportParm.setData("COLUMN6", count, "");
			exportParm.setData("COLUMN7", count, "");
			exportParm.setData("COLUMN8", count, "");
			exportParm.setData("COLUMN9", count, "");
			count++;
			for (int i = 0; i < parmup.getCount(); i++) {
				count++;
				exportParm.setData("COLUMN0", count,
						parmup.getData("CHARGE_HOSP_DESC", i));
				exportParm.setData("COLUMN1", count,
						parmup.getData("DISCOUNT_RATE", i));
				exportParm.setData("COLUMN2", count,
						parmup.getData("USER_NAME", i));
				exportParm.setData(
						"COLUMN3",
						count,
						parmup.getData("OPT_DATE", i).toString()
								.substring(0, 19));
				exportParm.setData("COLUMN4", count,
						parmup.getData("OPT_TERM", i));
				exportParm.setData("COLUMN5", count, "");
				exportParm.setData("COLUMN6", count, "");
				exportParm.setData("COLUMN7", count, "");
				exportParm.setData("COLUMN8", count, "");
				exportParm.setData("COLUMN9", count, "");

			}

		}
		if (parmdown.getCount() > 0) {
			count++;
			exportParm.setData("COLUMN0", count, "��Ŀ����");
			exportParm.setData("COLUMN1", count, "��Ŀ����");
			exportParm.setData("COLUMN2", count, "�������");
			exportParm.setData("COLUMN3", count, "��۴���");
			exportParm.setData("COLUMN4", count, "����");
			exportParm.setData("COLUMN5", count, "���۱���");
			exportParm.setData("COLUMN6", count, "�ۺ�۸�");
			exportParm.setData("COLUMN7", count, "������Ա");
			exportParm.setData("COLUMN8", count, "��������");
			exportParm.setData("COLUMN9", count, "������ĩ");
			for (int i = 0; i < parmdown.getCount(); i++) {
				count++;
				// ��Ŀ����,160;��Ŀ����,100;�������,160;��۴���,100;����,70,double,#0.0000;���۱���,75,double,#0.0000;�ۺ�۸�,70;������Ա,80,OPT_USER;��������,80,Timestamp;������ĩ,120
				exportParm.setData("COLUMN0", count,
						parmdown.getData("ORDER_DESC", i));
				exportParm.setData("COLUMN1", count,
						parmdown.getData("ORDER_CODE", i));
				exportParm.setData("COLUMN2", count,
						parmdown.getData("ORDER_DESC_DETAIL", i));
				exportParm.setData("COLUMN3", count,
						parmdown.getData("ORDER_CODE_DETAIL", i));
				exportParm.setData("COLUMN4", count,
						parmdown.getData("UNIT_PRICE", i));
				exportParm.setData("COLUMN5", count,
						parmdown.getData("DISCOUNT_RATE", i));
				exportParm.setData("COLUMN6", count,
						parmdown.getData("NEW_PRICE", i));
				exportParm.setData("COLUMN7", count,
						parmdown.getData("USER_NAME", i));
				exportParm.setData(
						"COLUMN8",
						count,
						parmdown.getData("OPT_DATE", i).toString()
								.substring(0, 19));
				exportParm.setData("COLUMN9", count,
						parmdown.getData("OPT_TERM", i));
			}

		}
		exportParm.setCount(count++);
		System.out.println("::::::::::::::::::::::" + exportParm);
		if (exportParm.getCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		// CHARGE_HOSP_CODE;DISCOUNT_RATE;OPT_USER;OPT_DATE;OPT_TERM;EXEC
		// ORDER_DESC;ORDER_CODE;ORDER_DESC_DETAIL;ORDER_CODE_DETAIL;UNIT_PRICE;DISCOUNT_RATE;NEW_PRICE;OPT_USER;OPT_DATE;OPT_TERM;EXEC
		ExportExcelUtil
				.getInstance()
				.exportExcel(
						"��0,130;��1,130;��2,130;��3,130;��4,130;��5,130;��6,130;��7,130;��8,130;��9,130",
						"COLUMN0;COLUMN1;COLUMN2;COLUMN3;COLUMN4;COLUMN5;COLUMN6;COLUMN7;COLUMN8;COLUMN9",
						exportParm, "��������ж�Ӧ����Ŀ�����ۿ�");

	}

	/**
	 * ����ݵ�ѡ�����¼�
	 */
	public void clickCTZMain() {
		onQuery();
	}

	/**
	 * Ч���Ƿ����õ�ѡ�����¼�
	 */
	public void clickQUseFlg() {
		if (qUseFlg.isSelected()) {
			// ��տ�ʼ����ʱ�䲢����Ч
			this.setValue("S_DATE", "");
			this.setValue("E_DATE", "");
			callFunction("UI|S_DATE|setEnabled", true);
			callFunction("UI|E_DATE|setEnabled", true);
		} else {
			// ��տ�ʼ����ʱ�䲢�û�
			this.setValue("S_DATE", "");
			this.setValue("E_DATE", "");
			callFunction("UI|S_DATE|setEnabled", false);
			callFunction("UI|E_DATE|setEnabled", false);
		}
		onQuery();
	}

	/**
	 * �������
	 */
	public void onSave() {
		// ��������
		TParm update = new TParm();
		TParm result = new TParm();

		TParm insertOrderParm = new TParm();
		TParm insertOrderData = new TParm();
		TParm delData = new TParm();
		TParm updateParm = new TParm();
		TParm updateData = new TParm();
		TParm mainUpdateParm = new TParm();// ��������
		TParm mainUpdateData = new TParm();
		// detailTable.acceptText();
		detailTable.acceptText();
		mainTable.acceptText();
		TParm detailParm = detailTable.getParmValue();// ��ϸ��
		// System.out.println("detailParm::::"+detailParm);
		TParm mainParm = mainTable.getParmValue(); // ��������
		// System.out.println("--detailParm.getCount()="+detailParm.getCount("ORDER_CODE"));
		if (mainParm.getCount() > 0) {// ���жϷ��������Ƿ�������
			for (int m = 0; m < mainParm.getCount(); m++) {
				String exec = mainParm.getValue("EXEC", m);
				if ("U".equals(exec)) {// 1���޸�����-��������
					mainUpdateParm.addData("CTZ_CODE",
							this.getValue("CTZ_CODE3"));
					mainUpdateParm.addData("CHARGE_HOSP_CODE",
							mainTable.getItemString(m, "CHARGE_HOSP_CODE"));
					mainUpdateParm.addData("DISCOUNT_RATE",
							mainTable.getItemString(m, "DISCOUNT_RATE"));
					mainUpdateParm.addData("OPT_USER", Operator.getID());
					mainUpdateParm.addData("OPT_TERM", Operator.getIP());
				}
			}
			// ѭ���������������޸�����
			for (int k = 0; k < mainUpdateParm.getCount("CTZ_CODE"); k++) {
				mainUpdateData.addRowData(mainUpdateParm, k);
			}
			// System.out.println("���������޸����ݣ�--->"+mainUpdateData);
			update.setData("MAINUPDATEDATA", mainUpdateData.getData());

			if (detailParm.getCount() > 0) {// 2:���ж�ϸ����Ƿ�������
				for (int i = 0; i < detailParm.getCount(); i++) {
					String exec = detailParm.getValue("EXEC", i);

					if ("Y".equals(exec)) {// ������

					} else if ("U".equals(exec)) {// �޸�����-��ϸ��
						// messageBox("UUUUUUUUU");
						updateParm.addData("CTZ_CODE",
								this.getValue("CTZ_CODE3"));
						if (detailParm.getValue("ORDER_CODE", i).length() > 0) {
							updateParm.addData("ORDER_CODE",
									detailParm.getValue("ORDER_CODE", i));
							updateParm.addData("ORDER_DESC",
									detailParm.getValue("ORDER_DESC", i));
						}
						// ORDER_DESC_DETAIL;ORDER_CODE_DETAIL;UNIT_PRICE
						else {
							updateParm
									.addData("ORDER_CODE", detailParm.getValue(
											"ORDER_CODE_DETAIL", i));
							updateParm
									.addData("ORDER_DESC", detailParm.getValue(
											"ORDER_DESC_DETAIL", i));
						}
						// ���ݿ⻹�ü��뵥���ֶ�....
						updateParm.addData("UNIT_PRICE",
								detailTable.getItemString(i, "UNIT_PRICE"));
						updateParm.addData("DISCOUNT_RATE",
								detailParm.getValue("DISCOUNT_RATE", i));
						updateParm.addData("OPT_DATE",
								detailTable.getItemString(i, "OPT_DATE"));
						updateParm.addData("OPT_USER",
								detailTable.getItemString(i, "OPT_USER"));
						updateParm.addData("OPT_TERM",
								detailTable.getItemString(i, "OPT_TERM"));
					} else {// ����
						// //SYS_CTZ_ORDER_DETAIL
						// if(detailParm.getValue("ORDER_DESC", i).length()>0){
						if (detailParm.getValue("ORDER_CODE_DETAIL", i)
								.length() > 0) {
							boolean flgDetail = onCheckOrder(
									this.getValueString("CTZ_CODE3"),
									detailParm.getValue("ORDER_CODE_DETAIL", i));
							if (flgDetail == true) {
								insertOrderParm.addData("CTZ_CODE",
										this.getValue("CTZ_CODE3"));
								// insertOrderParm.addData("ORDER_CODE",
								// detailParm.getValue("ORDER_CODE", i));
								// insertOrderParm.addData("ORDER_DESC",
								// detailTable.getItemString(i, "ORDER_DESC"));
								insertOrderParm.addData("ORDER_CODE",
										detailParm.getValue(
												"ORDER_CODE_DETAIL", i));
								insertOrderParm.addData("ORDER_DESC",
										detailParm.getValue(
												"ORDER_DESC_DETAIL", i));
								insertOrderParm.addData("UNIT_PRICE",
										detailParm.getValue("UNIT_PRICE", i));
								insertOrderParm.addData("DISCOUNT_RATE",
										detailTable.getItemString(i,
												"DISCOUNT_RATE"));
								insertOrderParm.addData("OPT_DATE", detailTable
										.getItemString(i, "OPT_DATE"));
								insertOrderParm.addData("OPT_USER", detailTable
										.getItemString(i, "OPT_USER"));
								insertOrderParm.addData("OPT_TERM", detailTable
										.getItemString(i, "OPT_TERM"));
								insertOrderParm.addData("ORDERSET_CODE",
										detailParm.getValue("ORDER_CODE", i));
								insertOrderParm.addData("HIDE_FLG", "N");
								insertOrderParm.addData("SETMAIN_FLG", "N");
							} else {
								this.messageBox("������ͬϸ����Ŀ");
							}
						} else if (detailParm.getValue("ORDER_CODE", i)
								.length() > 0) {
							boolean flgMain = onCheckOrder(
									this.getValueString("CTZ_CODE3"),
									detailParm.getValue("ORDER_CODE", i));
							if (flgMain == true) {
								insertOrderParm.addData("CTZ_CODE",
										this.getValue("CTZ_CODE3"));
								// insertOrderParm.addData("ORDER_CODE",
								// detailParm.getValue("ORDER_CODE", i));
								insertOrderParm.addData("ORDER_CODE",
										detailParm.getValue("ORDER_CODE", i));
								insertOrderParm.addData("ORDER_DESC",
										detailParm.getValue("ORDER_DESC", i));
								insertOrderParm.addData("UNIT_PRICE",
										detailParm.getValue("UNIT_PRICE", i));
								insertOrderParm.addData("DISCOUNT_RATE",
										detailTable.getItemString(i,
												"DISCOUNT_RATE"));
								insertOrderParm.addData("OPT_DATE", detailTable
										.getItemString(i, "OPT_DATE"));
								insertOrderParm.addData("OPT_USER", detailTable
										.getItemString(i, "OPT_USER"));
								insertOrderParm.addData("OPT_TERM", detailTable
										.getItemString(i, "OPT_TERM"));
								insertOrderParm.addData("ORDERSET_CODE",
										detailParm.getValue("ORDER_CODE", i));
								insertOrderParm.addData("HIDE_FLG", "N");
								insertOrderParm.addData("SETMAIN_FLG", "Y");
								// //�ж��Ǽ���ҽ�����������setmain_flg
								// TParm mainParm2 =
								// getDetailOrderSql(detailParm.getValue("ORDER_CODE",
								// i));
								// if(mainParm2.getCount()>0){
								// insertOrderParm.addData("SETMAIN_FLG", "Y");
								// }
								// }
							} else {
								this.messageBox("������ͬ������Ŀ");
							}

						}

						// this.messageBox("������ͬ��Ŀ");
					}
				}
				// ѭ������ҽ������������
				// System.out.println("������insertOrderParm--��"+insertOrderParm);
				for (int j = 0; j < insertOrderParm.getCount("ORDER_DESC"); j++) {
					insertOrderData.addRowData(insertOrderParm, j);
				}
				// TParm orderDetailParm = new TParm();//ϸ��
				// orderDetailParm = getOrderDetail(insertOrderData);
				// //System.out.println("orderDetailParm="+orderDetailParm);
				//
				// for (int p = 0; p < orderDetailParm.getCount("ORDER_CODE");
				// p++) {
				// insertOrderData.addRowData(orderDetailParm, p);
				// }

				// ѭ������ҽ����ɾ������
				for (int k = 0; k < delParm.getCount("CTZ_CODE"); k++) {
					delData.addRowData(delParm, k);
				}
				// ѭ������ҽ�����޸�����
				for (int m = 0; m < updateParm.getCount("ORDER_CODE"); m++) {
					updateData.addRowData(updateParm, m);
				}

				update.setData("INSERTDATA", insertOrderData.getData());
				update.setData("DELDATA", delData.getData());
				update.setData("UPDATEDATA", updateData.getData());

			}
			// result = MEMCtzTool.getInstance().onSave(update);
			// System.out.println("---update:"+update);
			result = TIOM_AppServer.executeAction("action.mem.MEMCtzAction",
					"onSave", update);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ�ܣ�");
				return;
			}

		}

		// -----------------------------------------
		String oper = "UPDATE";

		TTextField ctzCode = (TTextField) this.getComponent("CTZ_CODE");
		boolean flag = ctzCode.isEnabled();
		if (flag) {
			oper = "ADD";
		}
		System.out.println("oper====" + oper);
		// ҳ������У��
		if (checkData(oper)) {
			// TParm result = new TParm();
			// ��ȡҳ������
			TParm ctzParm = getData();
			ctzParm.setData("OPER", oper);
			// System.out.println("ctzParm="+ctzParm);
			if ("ADD".equals(oper)) {
				result = MEMCtzTool.getInstance().onSaveMemCtzInfoData(ctzParm);
				if (result.getErrCode() >= 0) {
					// ����ѭ������SYS_CHARGE_DETAIL��
					insertDataDetail(ctzParm);
				}

			} else if ("UPDATE".equals(oper)) {
				result = MEMCtzTool.getInstance().updateMemCtzInfoData(ctzParm);
				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ�ܣ�" + result.getErrName()
							+ result.getErrText());
					return;
				}
				// �޸��ۿ���-�жϵ�ǰ�ۿ����Ƿ����ȫ���ۿ���
				double currRate = this.getValueDouble("DISCOUNT");
				if (currRate != rate) {
					result = MEMCtzTool.getInstance().updateDiscountRate(
							ctzParm);
				}

				deptDataSame();
			}

			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ�ܣ�" + result.getErrName()
						+ result.getErrText());
				return;
			}
			this.messageBox("����ɹ���");
			onClear();

			// initData();
			// ��ݴ���״̬����Ч
			callFunction("UI|CTZ_CODE|setEnabled", false);
			// ���delParm
			delParm.removeData("CTZ_CODE");
			delParm.removeData("ORDER_CODE");

			// //��ʼ��
			// queryDetailData(ctzCodeOper);

		}

		initData();// add by huangjw 20141011

	}

	/**
	 * ����Ƿ����ظ������
	 */
	private boolean onCheckOrder(String value, String value2) {
		String sql = "SELECT ORDER_CODE " + " FROM SYS_CTZ_ORDER_DETAIL "
				+ " WHERE  CTZ_CODE = '" + value + "'" + " AND ORDER_CODE = '"
				+ value2 + "' ";
		// System.out.println("sql===----"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount("ORDER_CODE") > 0) {
			// ��ѯ������
			return false;
		} else {
			// û��ѯ��
			return true;
		}
	}

	/**
	 * ɾ��ϸ��
	 */
	public void delCtzDetail() {
		TParm result = new TParm();
		int selectedIndx = tableCtz.getSelectedRow();
		if (selectedIndx < 0) {
			this.messageBox("û��ѡ����ݴ��룡");
			return;
		}
		int detailIndx = detailTable.getSelectedRow();
		if (detailIndx < 0) {
			this.messageBox("��ѡ��Ҫɾ����ϸ��!");
			return;
		}
		// ��ɾ�������ݷŵ�delParm��
		String ctzCode = this.getValueString("CTZ_CODE3");
		String orderCode = detailTable.getItemString(detailIndx, "ORDER_CODE");
		String orderCodedetail = detailTable.getItemString(detailIndx,
				"ORDER_CODE_DETAIL");
		delParm.setData("CTZ_CODE", ctzCode);
		if (orderCode.length() > 0) {
			delParm.setData("ORDER_CODE", orderCode);
		} else {
			delParm.setData("ORDER_CODE", orderCodedetail);
		}
		if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ�����ݣ�", "��Ϣ",
				JOptionPane.YES_NO_OPTION) == 0) {
			result = MEMCtzTool.getInstance().delCtzDetailCode(delParm);
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ�ܣ�");
			} else {
				this.messageBox("ɾ���ɹ���");
				detailTable.removeRow(detailIndx);
			}
		}

	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		TParm result = new TParm();
		TParm parm = new TParm();
		int selectedIndx = tableCtz.getSelectedRow();
		if (selectedIndx < 0) {
			this.messageBox("û��ѡ��ɾ�����ݣ�");
			return;
		}
//		int detailIndx = detailTable.getSelectedRow();
//		if (detailIndx >= 0) {
//			// ��ɾ�������ݷŵ�delParm��
//			String ctzCode = this.getValueString("CTZ_CODE3");
//			String orderCode = detailTable.getItemString(detailIndx,
//					"ORDER_CODE");
//			String orderCodedetail = detailTable.getItemString(detailIndx,
//					"ORDER_CODE_DETAIL");
//			delParm.setData("CTZ_CODE", ctzCode);
//			if (orderCode.length() > 0) {
//				delParm.setData("ORDER_CODE", orderCode);
//			} else {
//				delParm.setData("ORDER_CODE", orderCodedetail);
//			}
//			if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ�����ݣ�", "��Ϣ",
//					JOptionPane.YES_NO_OPTION) == 0) {
//				result = MEMCtzTool.getInstance().delCtzDetailCode(delParm);
//				if (result.getErrCode() < 0) {
//					this.messageBox("ɾ��ʧ�ܣ�");
//				} else {
//					this.messageBox("ɾ���ɹ���");
//					onInit();
//					onClear();
//				}
//			}
//		} else {
			String ctzCode = tableCtz.getItemString(selectedIndx, "CTZ_CODE");
			// ����Ƿ����ɾ��
			if (checkDelCtz(ctzCode)) {
				if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ�����ݣ�", "��Ϣ",
						JOptionPane.YES_NO_OPTION) == 0) {
					parm.setData("CTZ_CODE", ctzCode);
					result = MEMCtzTool.getInstance().delCtzCode(parm);
					if (result.getErrCode() < 0) {
						this.messageBox("ɾ��ʧ�ܣ�");
					} else {
						this.messageBox("ɾ���ɹ���");
						onInit();
						onClear();
					}
				}

			} else {
				this.messageBox("��Ŀ����/��ϸ�������ݣ�����ɾ����");
				return;
			}
//		}
	}

	/**
	 * �������
	 */
	public void onClear() {
		// ���״̬����Ч
		callFunction("UI|CTZ_CODE|setEnabled", true);
		callFunction("UI|START_DATE|setEnabled", false);
		callFunction("UI|END_DATE|setEnabled", false);
		this.clearValue("CTZ_CODE;CTZ_DESC;MEM_TYPE;MEM_CODE;DEPT_CODE;DISCOUNT;PY1;SEQ;DESCRIPT;"
				+ "START_DATE;END_DATE;OVERDRAFT;MRO_CTZ");
		this.clearValue("NHI_NO_Q");// add by huangjw 20141011
		// �ۿ�Ĭ��Ϊ1,������
		this.setValue("DISCOUNT", "1.0000");
		this.setValue("SERVICE_LEVEL", 1); // Ĭ�Ϸ���ȼ�Ϊ�Է�
		// ��ѡ������
		typeUse.setSelected(false); // �Ƿ�����
		typeCtzDept.setSelected(false); // �Ƿ��жϿ���
		typeMainCtz.setSelected(false); // �����
		typeNhiCtz.setSelected(false); // ҽ�����
		typeMrctzUpd.setSelected(false); // ���²���
		typeO.setSelected(false); // ��������
		typeE.setSelected(false); // ��������
		typeI.setSelected(false); // סԺ����
		typeH.setSelected(false); // ��������
		detailTable.setParmValue(new TParm());
		detailTable.removeRowAll();
		mainTable.removeRowAll();
	}

	/**
	 * ҳ������У��
	 */
	public boolean checkData(String oper) {
		boolean flag = true;
		TParm result = new TParm();
		TParm parm = new TParm();
		if ("ADD".equals(oper)) {// ����������У����������
			if (this.getValueString("CTZ_CODE").length() == 0) {
				this.messageBox("��ݴ��벻��Ϊ�գ�");
				return flag = false;
			} else {
				// У��������ݴ�����Ƿ����
				parm.setData("CTZ_CODE", this.getValueString("CTZ_CODE"));
				result = MEMCtzTool.getInstance().checkCtzCode(parm);
			}
			if (result.getCount() > 0) {
				this.messageBox("����ݴ����Ѵ��ڣ�");
				return flag = false;
			}
		}

		// �ۿ۲��ܴ���1
		double discount = this.getValueDouble("DISCOUNT");
		if (discount > 1) {
			this.messageBox("�ۿ��ʲ��ܴ���1");
			this.grabFocus("DISCOUNT");
			return flag = false;
		}

		// ������ѡ��ѡ��ʱ�����������������Ϊ��
		if (typeCtzDept.isSelected()) {
			String deptCode = this.getComboBox("DEPT_CODE").getSelectedID();
			if (deptCode.length() <= 0) {
				this.messageBox("����ѡ��ѡ��ʱ������������Ϊ�գ�");
				this.grabFocus("DEPT_CODE");
				return flag = false;
			}
		}

		// ͸֧��Ȳ���Ϊ��
		String overdraft = this.getValueString("OVERDRAFT");
		double fee = Double.parseDouble(overdraft);
		if (fee < 0) {
			this.messageBox("͸֧��Ȳ���Ϊ����");
			this.grabFocus("OVERDRAFT");
			return flag = false;
		}

		return flag;

	}

	/**
	 * �����ݱ��Ƿ����ɾ��
	 */
	public boolean checkDelCtz(String ctzCode) {
		boolean flag = true;
		TParm result1 = new TParm();
		TParm result2 = new TParm();
		TParm parm = new TParm();
		parm.setData("CTZ_CODE", ctzCode);
		// ��Ŀ����
		result1 = MEMCtzTool.getInstance().checkDelCtz(parm);
		// System.out.println("result1.getCount()="+result1.getCount());
		if (result1.getCount() > 0) {// ��Ŀ����������ݲ���ɾ��
			return flag = false;
		}
		// ��Ŀ��ϸ
		result2 = MEMCtzTool.getInstance().checkFeeCtzCode(parm);
		// System.out.println("result2.getCount()="+result2.getCount());
		if (result2.getCount() > 0) {// ��Ŀ��ϸ�������ݲ���ɾ��
			return flag = false;
		}
		return flag;
	}

	/**
	 * ��ȡҳ������
	 */
	public TParm getData() {
		TParm parm = new TParm();
		// ��ȡҳ������
		String ctzCode1 = this.getValueString("CTZ_CODE");
		String ctzDesc = this.getValueString("CTZ_DESC");
		String memType = this.getValueString("MEM_TYPE");
		String memCode = this.getValueString("MEM_CODE");
		String deptCode = this.getValueString("DEPT_CODE");
		String discount = this.getValueString("DISCOUNT");
		String serviceLevel = this.getValueString("SERVICE_LEVEL"); // ����ȼ� add
																	// by
																	// huangtt
																	// 20160119
		String py1 = this.getValueString("PY1");
		String seq = this.getValueString("SEQ");
		String overdraft = this.getValueString("OVERDRAFT");
		String descript = this.getValueString("DESCRIPT");
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		String mroCtz = this.getValueString("MRO_CTZ");
		String nhiNoQ = this.getValueString("NHI_NO_Q");// ҽ����ݴ��� add by huangjw
														// 20141011
		String typeCtzDept1 = "";// �Ƿ��жϿ���
		if (typeCtzDept.isSelected()) {
			typeCtzDept1 = "Y";
		} else {
			typeCtzDept1 = "N";
		}
		String typeMainCtz1 = "";// �����
		if (typeMainCtz.isSelected()) {
			typeMainCtz1 = "Y";
		} else {
			typeMainCtz1 = "N";
		}
		String typeNhiCtz1 = "";// ҽ�����
		if (typeNhiCtz.isSelected()) {
			typeNhiCtz1 = "Y";
		} else {
			typeNhiCtz1 = "N";
		}
		String typeMrctzUpd1 = "";// ���²���
		if (typeMrctzUpd.isSelected()) {
			typeMrctzUpd1 = "Y";
		} else {
			typeMrctzUpd1 = "N";
		}
		String typeO1 = "";// ��������
		if (typeO.isSelected()) {
			typeO1 = "Y";
		} else {
			typeO1 = "N";
		}
		String typeE1 = "";// ��������
		if (typeE.isSelected()) {
			typeE1 = "Y";
		} else {
			typeE1 = "N";
		}
		String typeI1 = "";// סԺ����
		if (typeI.isSelected()) {
			typeI1 = "Y";
		} else {
			typeI1 = "N";
		}
		String typeH1 = "";// ��������
		if (typeH.isSelected()) {
			typeH1 = "Y";
		} else {
			typeH1 = "N";
		}
		String typeUse1 = "";// �Ƿ�����
		if (typeUse.isSelected()) {
			typeUse1 = "Y";
		} else {
			typeUse1 = "N";
		}
		parm.setData("CTZ_CODE", ctzCode1);
		parm.setData("CTZ_DESC", ctzDesc);
		parm.setData("MEM_TYPE", memType);
		parm.setData("MEM_CODE", memCode);
		parm.setData("DEPT_CODE", deptCode);
		parm.setData("DISCOUNT_RATE", discount);
		parm.setData("SERVICE_LEVEL", serviceLevel); // ����ȼ���ݴ��� add by huangtt
														// 20160119
		parm.setData("PY1", py1);
		parm.setData("SEQ", seq);
		parm.setData("OVERDRAFT", overdraft);
		parm.setData("DESCRIPT", descript);
		parm.setData("MRO_CTZ", mroCtz);
		parm.setData("NHI_NO", nhiNoQ);// ҽ����ݴ��� add by huangjw 20141011
		if (startDate.length() > 0) {
			parm.setData("START_DATE", startDate.replaceAll("-", "/")
					.substring(0, 10));
		} else {
			parm.setData("START_DATE", "");
		}
		if (endDate.length() > 0) {
			parm.setData("END_DATE",
					endDate.replaceAll("-", "/").substring(0, 10));
		} else {
			parm.setData("END_DATE", "");
		}

		parm.setData("CTZ_DEPT_FLG", typeCtzDept1);
		parm.setData("MAIN_CTZ_FLG", typeMainCtz1);
		parm.setData("NHI_CTZ_FLG", typeNhiCtz1);
		parm.setData("MRCTZ_UPD_FLG", typeMrctzUpd1);
		parm.setData("OPD_FIT_FLG", typeO1);
		parm.setData("EMG_FIT_FLG", typeE1);
		parm.setData("IPD_FIT_FLG", typeI1);
		parm.setData("HRM_FIT_FLG", typeH1);
		parm.setData("USE_FLG", typeUse1);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		return parm;
	}

	/**
	 * �Ƿ�ѡ�Ƿ�����ѡ��
	 */
	public void ifUseFlg() {
		if (typeUse.isSelected()) {
			// this.messageBox("��ѡ��");
			// ����ʱ������Ч
			callFunction("UI|START_DATE|setEnabled", true);
			callFunction("UI|END_DATE|setEnabled", true);
		} else {
			// this.messageBox("δѡ");
			// ����ʱ������Ч
			callFunction("UI|START_DATE|setEnabled", false);
			callFunction("UI|END_DATE|setEnabled", false);
		}
	}

	/**
	 * ����һ������-��Ŀ��ϸ�ۿ��б�
	 */
	public void insertRow(String opertable, boolean focusFlg) {
		// this.messageBox("11111111");
		TTable operTable = (TTable) callFunction("UI|" + opertable + "|getThis");
		operTable.acceptText();
		// int oldrow = table.getRowCount() - 1;
		int row = operTable.addRow();
		// operTable.setItem(row, "CTZ_CODE", ctzCode);
		operTable.setItem(row, "DISCOUNT_RATE", "1.0000");
		operTable.setItem(row, "OPT_USER", Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		operTable.setItem(row, "OPT_DATE", date);
		operTable.setItem(row, "OPT_TERM", Operator.getIP());
		if (focusFlg) {
			// this.messageBox("--- ���役�� -----");
			// Ĭ������ѡ�е���
			operTable.setSelectedRow(row);
			operTable.setSelectedColumn(0);
			// operTable.getTable().setRowSelectionInterval(row, row);
			// operTable.getTable().setColumnSelectionInterval(0, 0);
			operTable.getTable().changeSelection(row, 0, true, true);
			operTable.getTable().editCellAt(row, 0, null);
			// ģ�ⵥ
			/*
			 * try { Robot robot = new Robot();
			 * 
			 * robot.mouseMove(operTable.getTable().getcol); robot.delay(2000);
			 * 
			 * robot.mousePress(InputEvent.BUTTON1_MASK); robot.delay(2000);
			 * robot.mouseRelease(InputEvent.BUTTON1_MASK); } catch
			 * (AWTException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
			operTable.getTable().getEditorComponent().requestFocus();
		}
	}

	/**
	 * ��Ŀ�ۿ۷�����������¼�
	 */
	public void onMainTableChangeValue() {
		// �޸�������
		TParm mainParm = mainTable.getParmValue();
		String exec = mainParm.getValue("EXEC", mainTable.getSelectedRow());
		if ("Y".equals(exec)) {
			mainParm.setData("EXEC", mainTable.getSelectedRow(), "U");
			mainTable.setParmValue(mainParm);
		}
	}

	/**
	 * ��Ŀ��ϸ������¼�
	 */
	public void onTableChangeValue() {
		int selectedIndx = tableCtz.getSelectedRow();
		if (selectedIndx < 0) {
			return;
		}
		// TParm tableparm=tableCtz.getParmValue();
		// String ctzCode = tableparm.getValue("CTZ_CODE",selectedIndx);
		// String deptCode = tableparm.getValue("DEPT_CODE",selectedIndx);
		detailTable.acceptText();
		// ���һ��������
		int oldrow = detailTable.getRowCount() - 1;
		if (detailTable.getSelectedRow() == oldrow) {
			String orderDesc = detailTable.getItemString(oldrow, "ORDER_DESC");
			if (orderDesc.length() > 0) {
				insertRow("DETILTABLE", false);
				// String orderIdNo = SystemTool.getInstance().getNo("ALL",
				// "SYS", "PACKAGESECTIOND", "PACKAGESECTIOND");
				// detailTable.setItem(oldrow, 2,
				// String.valueOf(ctzCode));//��������
				// detailTable.setItem(oldrow, 3,
				// String.valueOf(deptCode));//���ÿ���

			}
		}

		// �޸�������
		TParm detailparm = detailTable.getParmValue();
		if (detailTable.getSelectedColumn() == 5) {
			// 20150702 wangjc add start
			detailparm.setData(
					"NEW_PRICE",
					detailTable.getSelectedRow(),
					detailparm.getDouble("UNIT_PRICE",
							detailTable.getSelectedRow())
							* detailparm.getDouble("DISCOUNT_RATE",
									detailTable.getSelectedRow()));
			// 20150702 wangjc add end
		}
		// add by huangtt 20151012 start
		boolean isDedug = true; // add by huangtt 20160505 ��־���
		try {
			if (detailTable.getSelectedColumn() == 6
					&& detailTable.getSelectedRow() != oldrow) {
				detailparm.setData(
						"DISCOUNT_RATE",
						detailTable.getSelectedRow(),
						detailparm.getDouble("NEW_PRICE",
								detailTable.getSelectedRow())
								/ detailparm.getDouble("UNIT_PRICE",
										detailTable.getSelectedRow()));

			}

		} catch (Exception e) {
			// TODO: handle exception
			if (isDedug) {
				System.out
						.println(" come in class: SYSMemCtzControl.class ��method ��onTableChangeValue");
				e.printStackTrace();

			}
		}

		// add by huangtt 20151012 end
		String exec = detailparm.getValue("EXEC", detailTable.getSelectedRow());
		if ("Y".equals(exec)) {
			detailparm.setData("EXEC", detailTable.getSelectedRow(), "U");
			// detailTable.setParmValue(detailparm);//20150702 wangjc modify
		}

		detailparm = getDetailTableFormat(detailparm);
		detailTable.setParmValue(detailparm);// 20150702 wangjc add
	}

	/**
	 * ������Ŀ��ϸ�ۿ۱���ۺ�۸��ʽ add by huangtt 20151012
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getDetailTableFormat(TParm parm) {
		DecimalFormat df = new DecimalFormat("##########0.0000");
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			parm.setData("NEW_PRICE", i,
					df.format(parm.getDouble("NEW_PRICE", i)));
		}
		return parm;
	}

	/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 */
	public void showPopMenu() {
		detailTable.acceptText();
		// TParm orderParm=detailTable.getParmValue();
		// String exec =
		// orderParm.getValue("EXEC",detailTable.getSelectedRow());
		// String setmainFlg =
		// orderParm.getValue("SETMAIN_FLG",detailTable.getSelectedRow());
		detailTable.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onOrderSetShow");

	}

	/**
	 * ��ѯ��ʾҽ��ϸ��TABLE�һ��¼�������ϸ���б�
	 */
	public void onOrderSetShow() {
		TParm parm = new TParm();
		TParm orderparm = detailTable.getParmValue();
		int row = detailTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		// String orderCode = orderTable.getItemString(row, "ORDER_CODE");
		String orderCode = orderparm.getValue("ORDER_CODE", row);
		parm.setData("ORDERSET_CODE", orderCode);
		Object obj = this.openDialog(
				"%ROOT%\\config\\mem\\MEMCTZOrderSetShow.x", parm);
	}

	/**
	 * ѭ�����������SYS_CHARGE_DETAIL
	 */
	public void insertDataDetail(TParm ctzParm) {
		TParm result = new TParm();
		for (int i = 0; i < allCode.getCount(); i++) {
			String hospChargeCode = allCode.getValue("CHARGE_HOSP_CODE", i);
			String sql = "insert into SYS_CHARGE_DETAIL(ctz_code,charge_hosp_code,discount_rate,"
					+ "opt_user,opt_date,opt_term) values('"
					+ ctzParm.getValue("CTZ_CODE")
					+ "',"
					+ "'"
					+ hospChargeCode
					+ "','"
					+ ctzParm.getValue("DISCOUNT_RATE")
					+ "','"
					+ Operator.getID()
					+ "',sysdate,'"
					+ Operator.getIP()
					+ "')";
			// System.out.println("===i-->"+i+" sql="+sql);
			result = new TParm(TJDODBTool.getInstance().update(sql));
		}

	}

	/**
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * �õ�ҳ�������������
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * ��������ͬ��
	 */
	public void deptDataSame() {
		setValue("DEPT_CODE3", this.getValue("DEPT_CODE"));
	}

	/**
	 * ��ѯ����ҽ��-ϸ����ӵ�result��
	 */
	public TParm getOrderDetail(TParm insertOrderParm) {
		TParm parm = new TParm();
		TParm result = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		int num = 0;
		// System.out.println("insertOrderParm.getCount()="+insertOrderParm.getCount());
		for (int i = 0; i < insertOrderParm.getCount(); i++) {
			// String sql =
			// "SELECT * FROM SYS_ORDERSETDETAIL WHERE HIDE_FLG = 'Y' AND " +
			// " ORDERSET_CODE = '"+insertOrderParm.getValue("ORDER_CODE")+"' ";
			// parm = new TParm(TJDODBTool.getInstance().select(sql));
			parm = getDetailOrderSql(insertOrderParm.getValue("ORDER_CODE", i));
			// System.out.println("i="+i+" parm="+parm);
			if (parm.getCount() > 0) {
				for (int j = 0; j < parm.getCount(); j++) {
					result.setData("ORDERSET_CODE", num,
							parm.getValue("ORDERSET_CODE", j));
					result.setData("OPT_USER", num, Operator.getID());
					result.setData("HIDE_FLG", num, "Y");
					result.setData("ORDER_CODE", num,
							parm.getValue("ORDER_CODE", j));
					result.setData("OPT_TERM", num, Operator.getIP());
					result.setData("ORDER_DESC", num,
							parm.getValue("ORDER_DESC", j));
					result.setData("OPT_DATE", date);
					result.setData("DISCOUNT_RATE", num,
							insertOrderParm.getValue("DISCOUNT_RATE", i));
					result.setData("CTZ_CODE", num,
							insertOrderParm.getValue("CTZ_CODE", i));
					result.setData("SETMAIN_FLG", num, "N");
					num++;
				}
			}
		}
		// System.out.println("��ѯ������ϸ�"+result);
		return result;
	}

	/**
	 * ��ȡ����ҽ����ϸsql
	 */
	public TParm getDetailOrderSql(String order) {
		TParm result = new TParm();
		String sql = "SELECT A.ORDER_CODE,B.ORDERSET_CODE,A.ORDER_DESC, A.SPECIFICATION, DOSAGE_QTY,"
				+ " UNIT_CODE AS MEDI_UNIT, OWN_PRICE, OWN_PRICE * DOSAGE_QTY "
				+ " AS OWN_AMT, EXEC_DEPT_CODE, OPTITEM_CODE, INSPAY_TYPE "
				+ " FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE and A.ACTIVE_FLG = 'Y' AND B.HIDE_FLG='Y' "
				+ " AND B.ORDERSET_CODE = '" + order + "'";
		// System.out.println("sql====>"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * �����ۿۼ���ʵ�ս��
	 */
	public void queryRate() {
		// У���ۿ۲��ܴ���1
		double rate = this.getValueDouble("DISCOUNT");
		if (rate > 1) {
			this.messageBox("�ۿ��ʲ��ܴ���1��");
			this.setValue("DISCOUNT", "1.0000");
			return;
		}
	}

	/**
	 * ����ۿ�<1��,��Ĭ�Ϸ���ȼ���ֻ��Ϊ���ԷѼ� add by huangtt 20160119
	 */
	public void selServiceLevel() {
		double rate = this.getValueDouble("DISCOUNT");
		String level = this.getValueString("SERVICE_LEVEL");
		if (rate < 1) {
			if (!("1".equals(level))) {
				this.messageBox("����ۿ�<1��,��Ĭ�Ϸ���ȼ���ֻ��Ϊ���ԷѼ�!");
				this.setValue("SERVICE_LEVEL", 1);
			}

		}
	}

	public Double getSysFeePrice(String orderCode) {
		String sql = "SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("OWN_PRICE", 0);
	}

}
