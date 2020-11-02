package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: סԺҽ������
 * </p>
 * 
 * <p>
 * Description: סԺҽ������
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
 * @author zhangp 20120210
 * @version 1.0
 */
public class INSOdiCheckAccntControl extends TControl {
	private TTable table;
	private TTable table2;
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table=(TTable)this.getComponent("TABLE1");	
		table2=(TTable)this.getComponent("TABLE2");
		setValue("CTZ_CODE", 1);
		setValue("START_DATE", SystemTool.getInstance().getDate());
		setValue("END_DATE", SystemTool.getInstance().getDate());
		addListener(table);
		addListenerOne(table2);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String regionCode = Operator.getRegion();// ����
		String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		} else {
			messageBox("�������ѯ����");
			return;
		}
		int ctz = getValueInt("CTZ_CODE");
		String sql = "SELECT B.ADM_SEQ, B.CONFIRM_NO, B.YEAR_MON, A.PAT_NAME, D.CTZ_DESC,C.CATEGORY_CHN_DESC, "
				+ " (B.PHA_AMT + B.EXM_AMT + B.TREAT_AMT + B.OP_AMT + B.BED_AMT + B.MATERIAL_AMT + B.OTHER_AMT"
				+ " + B.BLOODALL_AMT + B.BLOOD_AMT) HEJI1, "
				+ " (B.PHA_NHI_AMT + B.EXM_NHI_AMT + B.TREAT_NHI_AMT + B.OP_NHI_AMT + B.BED_NHI_AMT + B.MATERIAL_NHI_AMT"
				+ " + B.OTHER_NHI_AMT + B.BLOODALL_NHI_AMT + B.BLOOD_NHI_AMT) HEJI2, "
				+ " B.OWN_AMT, B.ADD_AMT, B.NHI_PAY, B.NHI_COMMENT, B.ARMYAI_AMT "
				+ " FROM INS_ADM_CONFIRM A, INS_IBS B, SYS_CATEGORY C, SYS_CTZ D "
				+ " WHERE A.CONFIRM_NO = B.CONFIRM_NO "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND A.ADM_CATEGORY = C.CATEGORY_CODE "
				+ " AND A.IN_STATUS IN('2','4','7') AND B.REGION_CODE = '"
					+ regionCode
				+ "' "
				+ " AND B.UPLOAD_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ " AND TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS') "
				+ " AND A.HIS_CTZ_CODE = D.CTZ_CODE AND D.NHI_CTZ_FLG = 'Y' AND B.STATUS='S'";
		if (ctz == 1) {
			sql = sql + " AND SUBSTR(D.CTZ_CODE,0,1)= '1'";// ��ְ
		}
		if (ctz == 2) {
			sql = sql + " AND SUBSTR(D.CTZ_CODE,0,1)= '2'";// �Ǿ�
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("��������");
		}
		this.callFunction("UI|TABLE1|setParmValue", result);
	}

	/**
	 * ������
	 */
	public void onCheckAll() {
		String regionCode = Operator.getRegion();// ����
		String startDate = "";
		String endDate = "";
		int ctz = getValueInt("CTZ_CODE");
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		} else {
			messageBox("�������ѯ����");
			return;
		}
		TParm result = queryCheckAll(regionCode, startDate, endDate, ctz);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("������");
			return;
		}
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
		TParm parm = new TParm();
		parm.addData("HOSP_NHI_NO", hospital);// ҽԺ����
		parm.addData("START_DATE", startDate.substring(0,8));// ��ʼʱ��
		parm.addData("END_DATE", endDate.substring(0,8));// ����ʱ��
		// ����
		parm.addData("TOTAL_AGENT_AMT", result.getData(
						"NHI_PAY", 0));// ����ҽ���籣����֧�����
		parm.addData("TOTAL_AMT", result.getData("HEJI1", 0));// �������
		parm.addData("TOTAL_NHI_AMT", result.getData("HEJI2", 0));// �걨���
		parm.addData("OWN_AMT", result.getData("OWN_AMT", 0));// ȫ�Էѽ��
		parm.addData("ADDPAY_AMT", result.getData("ADD_AMT", 0));// �������
		parm.addData("ALL_TIME", result.getData("COUNT", 0));// ���˴�
		parm.addData("FLG_AGENT_AMT", result.getData(
				"NHI_COMMENT", 0));// ҽ�ƾ����籣����֧�����
		parm.addData("AGENT_AMT", result.getData("ARMYAI_AMT", 0));// �������
		if (ctz == 1) {// ��ְ
			
			parm.addData("ACCOUNT_PAY_AMT", result.getData(
					"ACCOUNT_PAY_AMT", 0));// �����ʻ�ʵ��֧�����
			parm.setData("PIPELINE", "DataDown_yb");
			parm.setData("PLOT_TYPE", "A");
			parm.addData("PARM_COUNT", 12);
		}
		if (ctz == 2) {// �Ǿ�
			parm.setData("PIPELINE", "DataDown_czys");
			parm.setData("PLOT_TYPE", "K");
			parm.addData("PARM_COUNT", 11);
		}
		result = InsManager.getInstance().safe(parm);// ҽ�����ӿڷ���
//		System.out.println("������:::PIPELINE=DataDown_yb:::PLOT_TYPE=K:::����===="
//				+ result);
		messageBox(result.getValue("PROGRAM_MESSAGE"));
	}

	/**
	 * �����˲�ѯ
	 * 
	 * @param regionCode
	 * @param startDate
	 * @param endDate
	 * @param ctz
	 * @return
	 */
	public TParm queryCheckAll(String regionCode, String startDate,
			String endDate, int ctz) {
		String sql = "SELECT SUM (  B.PHA_AMT + B.EXM_AMT + B.TREAT_AMT + B.OP_AMT + B.BED_AMT + B.MATERIAL_AMT"
				+ " + B.OTHER_AMT + B.BLOODALL_AMT + B.BLOOD_AMT ) HEJI1, "
				+ " SUM (  B.PHA_NHI_AMT + B.EXM_NHI_AMT + B.TREAT_NHI_AMT + B.OP_NHI_AMT + B.BED_NHI_AMT"
				+ " + B.MATERIAL_NHI_AMT + B.OTHER_NHI_AMT + B.BLOODALL_NHI_AMT + B.BLOOD_NHI_AMT ) HEJI2, "
				+ " SUM (B.OWN_AMT) AS OWN_AMT, SUM (B.ADD_AMT) AS ADD_AMT, COUNT (A.CONFIRM_NO) AS COUNT, "
				+ " SUM (B.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT, SUM (B.NHI_PAY) AS NHI_PAY, SUM (B.NHI_COMMENT) AS NHI_COMMENT, SUM (B.ARMYAI_AMT) AS ARMYAI_AMT "
				+ " FROM INS_ADM_CONFIRM A, INS_IBS B, SYS_CTZ C "
				+ " WHERE  A.CONFIRM_NO = B.CONFIRM_NO "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND B.REGION_CODE = '"
				+ regionCode
				+ "' AND A.IN_STATUS IN( '2','4','7') "
				+ " AND B.UPLOAD_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ " AND TO_DATE ('"
				+ endDate
				+ "', 'YYYYMMDDHH24MISS') "

				+ " AND A.HIS_CTZ_CODE = C.CTZ_CODE " + " AND C.NHI_CTZ_FLG = 'Y' AND B.STATUS='S'";
		if (ctz == 1) {
			sql = sql + " AND SUBSTR(C.CTZ_CODE,0,1)= '1'";// ��ְ
		}
		if (ctz == 2) {
			sql = sql + " AND SUBSTR(C.CTZ_CODE,0,1)= '2'";// �Ǿ�
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ����ϸ��
	 */
	public void onCheckDetailAccnt() {
		String startDate = "";
		String endDate = "";
		int ctz = getValueInt("CTZ_CODE");
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		}
		//onQuery();// ��ѯtable1
		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
		TTable table2 = (TTable) this.getComponent("TABLE2");// TABLE2
		if (ctz == 1) {
			parm = new TParm();
			// ����
			parm.setData("PIPELINE", "DataDown_yb");
			parm.setData("PLOT_TYPE", "B");
			table2
					.setHeader("�������,100;�ʸ�ȷ�����,100;�ں�,100;����,100;��Ա���,100;֧�����,100;"
							+ "�������,100,double,#########0.00;�걨���,100,double,#########0.00;"
							+ "ȫ�Էѽ��,100,double,#########0.00;�������,100,double,#########0.00;"
							+ "�����˻�֧�����,100,double,#########0.00");
			table2
					.setParmMap("ADM_SEQ;CONFIRM_NO;ISSUE;NAME;CTZ1_CODE;PAY_TYPE;"
							+ "TOTAL_AMT;TOTAL_NHI_AMT;"
							+ "OWN_AMT;ADDPAY_AMT;" + "ACCOUNT_PAY_AMT");
		}
		if (ctz == 2) {
			parm = new TParm();
			// ����
			parm.setData("PIPELINE", "DataDown_czyd");
			parm.setData("PLOT_TYPE", "J");
			table2
					.setHeader("�������,100;�ʸ�ȷ�����,100;�ں�,100;����,100;��Ա���,100;֧�����,100;"
							+ "�������,100,double,#########0.00;�걨���,100,double,#########0.00;"
							+ "ȫ�Էѽ��,100,double,#########0.00;�������,100,double,#########0.00");
			table2
					.setParmMap("ADM_SEQ;CONFIRM_NO;ISSUE;NAME;CTZ1_CODE;PAY_TYPE;"
							+ "TOTAL_AMT;TOTAL_NHI_AMT;" + "OWN_AMT;ADDPAY_AMT");
		}
		parm.addData("HOSP_NHI_NO", hospital);// ҽԺ����
		parm.addData("START_DATE", startDate.substring(0,8));// ��ʼʱ��
		parm.addData("END_DATE", endDate.substring(0,8));// ����ʱ��
		parm.addData("PARM_COUNT", 3);
		TParm result = InsManager.getInstance().safe(parm, "");// ҽ�����ӿڷ���(����)
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}else{
			this.messageBox("��ϸ���سɹ�");
		}
		table2.setParmValue(result);
		//this.callFunction("UI|TABLE2|setParmValue", result);
		countDetail();
	}

	/**
	 * ������ϸ�˲��
	 */
	public void countDetail() {
		TTable table1 = (TTable) this.getComponent("TABLE1");// TABLE1
		TTable table2 = (TTable) this.getComponent("TABLE2");// TABLE2
		if (table1.getParmValue() == null || table2.getParmValue() == null) {
			messageBox("�������ݲ���Ϊ��");
			return;
		}
		// ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMT
		TParm tableParm1 = table1.getParmValue();
		TParm tableParm2 = table2.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < tableParm1.getCount(); i++) {
			String confirmNoLocal = tableParm1.getData("CONFIRM_NO", i)
					.toString();
			boolean canfind = false;
			for (int j = 0; j < tableParm2.getCount(); j++) {
				String confirmNoCenter = tableParm2.getData("CONFIRM_NO", j)
						.toString();
				if (!confirmNoLocal.equals(confirmNoCenter))
					continue;
				canfind = true;
				// ���ؽ��
				double totAmtLocal = tableParm1.getDouble("HEJI1", i);// �������
				double nhiAmtLocal = tableParm1.getDouble("HEJI2", i);// �걨���
				double ownAmtLocal = tableParm1.getDouble("OWN_AMT", i);// ȫ�Էѽ��
				double addAmtLocal = tableParm1.getDouble("ADD_AMT", i);// �������
				// ���Ķ˽��
				double totAmtCenter = tableParm2.getDouble("TOTAL_AMT", j);// �������
				double nhiAmtCenter = tableParm2.getDouble("TOTAL_NHI_AMT", j);// �걨���
				double ownAmtCenter = tableParm2.getDouble("OWN_AMT", j);// ȫ�Էѽ��
				double addAmtCenter = tableParm2.getDouble("ADDPAY_AMT", j);// �������
				if (totAmtLocal != totAmtCenter || nhiAmtLocal != nhiAmtCenter
						|| ownAmtLocal != ownAmtCenter
						|| addAmtLocal != addAmtCenter) {
					parm.addData("STATUS_ONE", "Y");
					parm.addData("STATUS_TWO", "N");
					parm.addData("STATUS_THREE", "N");
					parm.addData("ADM_SEQ", tableParm1.getData("ADM_SEQ", i));
					parm.addData("CONFIRN_NO", tableParm1.getData("CONFIRM_NO",
							i));
					parm.addData("YEAR_MON", tableParm1.getData("YEAR_MON", i));
					parm.addData("NAME", tableParm1.getData("PAT_NAME", i));
					parm.addData("TOT_AMT_LOCAL", tableParm1
							.getData("HEJI1", i));
					parm.addData("TOT_AMT_CENTER", tableParm2.getData(
							"TOTAL_AMT", j));
					parm.addData("NHI_AMT_LOCAL", tableParm1
							.getData("HEJI2", i));
					parm.addData("NHI_AMT_CENTER", tableParm2.getData(
							"TOTAL_NHI_AMT", j));
					parm.addData("OWN_AMT_LOCAL", tableParm1.getData("OWN_AMT",
							i));
					parm.addData("OWN_AMT_CENTER", tableParm2.getData(
							"OWN_AMT", j));
					parm.addData("ADD_AMT_LOCAL", tableParm1.getData("ADD_AMT",
							i));
					parm.addData("ADD_AMT_CENTER", tableParm2.getData(
							"ADDPAY_AMT", j));
				}
			}
			if (!canfind) {
				parm.addData("STATUS_ONE", "N");
				parm.addData("STATUS_TWO", "Y");
				parm.addData("STATUS_THREE", "N");
				parm.addData("ADM_SEQ", tableParm1.getData("ADM_SEQ", i));
				parm.addData("CONFIRN_NO", tableParm1.getData("CONFIRM_NO", i));
				parm.addData("YEAR_MON", tableParm1.getData("YEAR_MON", i));
				parm.addData("NAME", tableParm1.getData("PAT_NAME", i));
				parm.addData("TOT_AMT_LOCAL", tableParm1.getData("HEJI1", i));
				parm.addData("TOT_AMT_CENTER", 0);
				parm.addData("NHI_AMT_LOCAL", tableParm1.getData("HEJI2", i));
				parm.addData("NHI_AMT_CENTER", 0);
				parm.addData("OWN_AMT_LOCAL", tableParm1.getData("OWN_AMT", i));
				parm.addData("OWN_AMT_CENTER", 0);
				parm.addData("ADD_AMT_LOCAL", tableParm1.getData("ADD_AMT", i));
				parm.addData("ADD_AMT_CENTER", 0);
			}
		}
		for (int i = 0; i < tableParm2.getCount(); i++) {
			String confirmNoCenter = tableParm2.getData("CONFIRM_NO", i)
					.toString();
			boolean canfind = false;
			for (int j = 0; j < tableParm1.getCount(); j++) {
				String confirmNoLocal = tableParm1.getData("CONFIRM_NO", i)
						.toString();
				if (!confirmNoLocal.equals(confirmNoCenter))
					continue;
				canfind = true;
			}
			if (!canfind) {
				parm.addData("STATUS_ONE", "N");
				parm.addData("STATUS_TWO", "N");
				parm.addData("STATUS_THREE", "Y");
				parm.addData("ADM_SEQ", tableParm2.getData("ADM_SEQ", i));
				parm.addData("CONFIRN_NO", tableParm2.getData("CONFIRN_NO", i));
				parm.addData("YEAR_MON", tableParm2.getData("ISSUE", i));
				parm.addData("NAME", tableParm2.getData("NAME", i));
				parm.addData("TOT_AMT_LOCAL", 0);
				parm.addData("TOT_AMT_CENTER", tableParm2.getData("TOTAL_AMT",
						i));
				parm.addData("NHI_AMT_LOCAL", 0);
				parm.addData("NHI_AMT_CENTER", tableParm2.getData(
						"TOTAL_NHI_AMT", i));
				parm.addData("OWN_AMT_LOCAL", 0);
				parm
						.addData("OWN_AMT_CENTER", tableParm2.getData(
								"OWN_AMT", i));
				parm.addData("ADD_AMT_LOCAL", 0);
				parm.addData("ADD_AMT_CENTER", tableParm2.getData("ADDPAY_AMT",
						i));
			}
		}
		if (parm.getCount("ADM_SEQ") <= 0) {
			messageBox("����ϸ�ʳɹ�");
			return;
		}
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSOdiCheckDetail.x", parm);
	}

	/**
	 * ���
	 */
	public void onclear() {
		clearValue("CTZ_CODE;START_DATE;END_DATE");
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table2.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table2.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table2.getParmMap(sortColumnOne);
				// ת��parm�е���
				int col = tranParmColIndexOne(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// ��������vectorת��parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

}
