package com.javahis.ui.clp;

import java.util.ArrayList;
import java.util.List;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.*;

import jdo.clp.CLPDurationTool;
import jdo.clp.CLPVariationTool;
import jdo.sys.Operator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;

import com.dongyang.ui.event.TTableEvent;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;

import java.util.Map;
import com.dongyang.data.TNull;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.PatTool;

/**
 * <p>
 * Title: �ٴ�·���������
 * </p>
 * 
 * <p>
 * Description: �ٴ�·���������
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
public class CLPVariationControl extends TControl {
	public CLPVariationControl() {

	}

	// checkbox�м�¼���û��������õ����жϱ�����Ƿ�������Ҫ�������checkbox
	// ����б���check������
	private String varianceFlg = "VARIANCEFLG";
	// ������Ƿ�ִ��check������
	private String isExe = "ISEXE";
	// ִ�п���check������
	private String rDeptFlg = "RDEPTFLG";
	// ����б�������ֶ�����
	private String medicalMoncat = "MEDICAL_MONCAT";
	// ����б���ԭ���ֶ�����
	private String medicalVariance = "MEDICAL_VARIANCE";
	// ����й���ʦ�����������
	private String manageMoncat = "MANAGE_MONCAT";
	// ����й���ʦ����ԭ������
	private String manageVariance = "MANAGE_VARIANCE";
	// ����н���״̬����
	private String progressCode = "PROGRESS_CODE";
	// ��������ο�������
	private String rDeptCode = "R_DEPT_CODE";
	private int[] chks = new int[] { 12, 15, 22 };
	private String[] chkskeys = new String[] { varianceFlg, isExe, rDeptFlg };
	// ��¼��ǰѡ����
	private int selectRow = -1;
	// ����סԺ��
	private String case_no;
	// �����ٴ�·����Ϣ
	private String clncPathCode;
	// table ��������ʱ�Ŀɱ༭��
	private int[] newTableDataColumn = { 2, 3, 4, 10, 11, 12 };
	// table��ԭ����ʱ�Ŀɱ༭��
	private int[] editTableDataColumn = { 13, 14, 15, 16, 17, 18, 19, 20, 21,
			22, 23, 24, 25 };
	// �����ݱ�ʾ
	private String newDataStr = "��";
	// �����ݱ�ʾ
	private String editDataStr = "�༭";
	private TComboBox cmbSysCharge;
	private Compare compare = new Compare();
	private TTable TABLEVARIATION;
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * ҳ���ʼ������
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ҳ���ʼ������
	 */
	private void initPage() {
		onClear();
		// ��ʼ���������
		initInParm();
		// //��ʼ��������Ϣ
		// initPatientInfo();
		// ��ʼ��ʱ����
		// ע��ʱ������click�¼�����ҳ���н��а�
		initTree();
		// ��ʼ��ִ�пؼ�
		initMonCatCode();
		// ��ʼ�����༭ʱ���¼�
		initTableEdit();
	}

	/**
	 * ��ʼ���������
	 */
	private void initInParm() {
		TParm inParm = (TParm) this.getParameter();
		this.case_no = inParm.getValue("CLP", "CASE_NO");
		this.clncPathCode = inParm.getValue("CLP", "CLNCPATH_CODE");
		String flg = inParm.getValue("CLP", "FLG");
		if (null != flg && flg.equals("Y")) {
			callFunction("UI|close|setEnabled", false);
		}
		TABLEVARIATION= (TTable) this.getComponent("TABLEVARIATION");
	}

	/**
	 * ��ʼ�����༭ʱ���¼�
	 */
	private void initTableEdit() {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onEditTable");
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		addListener(TABLEVARIATION);
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
				TParm tableData = TABLEVARIATION.getParmValue();
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
				String tblColumnName = TABLEVARIATION.getParmMap(sortColumn);
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
		TABLEVARIATION.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

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
	 * �����������
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		int col = table.getSelectedColumn();
		String columnName = table.getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		if ("VARIANCEFLG".equals(columnName)) {
			if (table.getParmValue().getValue(columnName, row).equals("Y")) {
				setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
				setVarianCe(true, row);
			} else {
				setRowStatus(row, RecordStatus.normalRecord, "TABLEVARIATION");
				setVarianCe(false, row);
			}
		}
		return false;

	}

	/**
	 * ����������ԭ��ֵ flg =true ��ѡ״̬ =======pangben 2012-6-12
	 */
	private void setVarianCe(boolean flg, int row) {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		if (flg) {
			tableParm.setData("MEDICAL_MONCAT", row, this
					.getValue("MONCAT_CODE"));
			tableParm.setData("MEDICAL_VARIANCE", row, this
					.getValue("VARIANCE_CODE"));
		} else {
			tableParm.setData("MEDICAL_MONCAT", row, "");
			tableParm.setData("MEDICAL_VARIANCE", row, "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * ���༭ʱ����
	 */
	public void onEditTable(Component com, int row, int column) {
		int selectedRow = this.getSelectedRow("TABLEVARIATION");
		if (selectedRow < 0) {
			return;
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		// �ж����Ƿ���Ա༭
		// �û���ҽ��
		if (column == 10) {
			String orderFlg = tableParm.getValue("ORDER_FLG", selectedRow);
			String url = "";
			if ("Y".equals(orderFlg)) {
				url = "%ROOT%\\config\\clp\\CLPSysFeePopup.x";
			} else if ("N".equals(orderFlg)) {
				url = "%ROOT%\\config\\clp\\CLPChkItemPopup.x";
			} else if ("O".equals(orderFlg)) {
				url = "%ROOT%\\config\\clp\\CLPNursOrderPopup.x";
			}
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", orderFlg);
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("1", getConfigParm()
					.newConfig(url), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturn");
		}
	}

	// �õ�����б༭�ٴ�·����ֵ,���������д�ֵ�����ٴμ���һ��
	public void popReturn(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		if ("1".equals(tag)) {
			String typeCode = result.getValue("ORDER_CODE");
			String typeCHNDesc = result.getValue("ORDER_CHN_DESC");
			TTable table = (TTable) this.getComponent("TABLEVARIATION");
			table.acceptText();
			TParm tableParm = table.getParmValue();
			int row = this.getSelectedRow("TABLEVARIATION");
			tableParm.setData("MAINORD_CODE", row, typeCode);
			tableParm.setData("MAIN_ORDER_CODE_DESC", row, typeCHNDesc);
			table.setParmValue(tableParm);
			if (!"".equals(typeCHNDesc)) {
				this.addNewRow();
			}
		}
	}

	/**
	 * ��ʼ��ִ�пؼ�
	 */
	private void initMonCatCode() {
		TTextFormat tf = (TTextFormat) this.getComponent("MONCAT_CODE");
		// ========pangben 2012-06-27 start ��������վ�������,ֻ��ʾסԺ��
		cmbSysCharge = (TComboBox) this.getComponent("IPD_CHARGE_CODE");
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		// ��ʾ����
		TParm sysParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,DATA FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY SEQ,ID"));
		TParm data = new TParm();
		int index = 0;
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				if (parm.getValue("CHARGE0" + i, 0).length() > 0) {
					index++;
					// ��������ӵ��б���
					data.addData("id", parm.getValue("CHARGE0" + i, 0));
					for (int j = 0; j < sysParm.getCount(); j++) {
						if (parm.getValue("CHARGE0" + i, 0).equals(
								sysParm.getValue("ID", j))) {
							data.addData("text", sysParm.getValue("NAME", j));
							break;
						}
					}

				}
			} else {
				if (parm.getValue("CHARGE" + i, 0).length() > 0) {
					index++;
					data.addData("id", parm.getValue("CHARGE" + i, 0));
					for (int j = 0; j < sysParm.getCount(); j++) {
						if (parm.getValue("CHARGE" + i, 0).equals(
								sysParm.getValue("ID", j))) {
							data.addData("text", sysParm.getValue("NAME", j));
							break;
						}
					}
				}
			}
		}
		data.setCount(index);
		StringBuffer stringData = new StringBuffer();
		stringData.append("[[id,text],[,],");
		// ������ʾ��ʽ
		for (int i = 0; i < data.getCount(); i++) {
			stringData.append("[").append(data.getValue("id", i)).append(",")
					.append(data.getValue("text", i)).append("],");
		}
		String stringValue = stringData.substring(0, stringData
				.lastIndexOf(","));
		stringValue += "]";
		cmbSysCharge.setStringData(stringValue);
		// ========pangben 2012-06-27 stop
		tf
				.setPopupMenuSQL("SELECT MONCAT_CODE AS ID , MONCAT_CHN_DESC AS NAME , MONCAT_ENG_DESC AS ENNAME   FROM CLP_VARMONCAT WHERE REGION_CODE='"
						+ Operator.getRegion() + "'");
		tf.onQuery();
		TCheckBox chkExe=(TCheckBox)this.getComponent("EXEC_FLG");
		chkExe.setSelected(true);
		this.setValue("CHK_USER", "001");
	}

	/**
	 * ��ʼ��ʱ����
	 */
	private void initTree() {
		TTree tree = (TTree) callMessage("UI|TTree|getThis");
		// ��ʼ��Tree�Ļ�����Ϣ
		// �õ�Tree�Ļ�������
		TParm selectTParm = new TParm();
		this.putBasicSysInfoIntoParm(selectTParm);
		// TParm result = CLPDurationTool.getInstance().selectData(selectTParm);
		TTreeNode root = (TTreeNode) callMessage("UI|TTree|getRoot");
		root.setText("�ٴ�·��ʱ��");
		root.setType("Root");
		root.setID("");
		root.removeAllChildren();
		List dataList = getTreeDataList();
		for (Object elem : dataList) {
			TParm dataParm = (TParm) elem;
			String noteType = "Path"; // UI
			TTreeNode treeNode = new TTreeNode("KPILEAVE", noteType);
			if (dataParm.getValue("PARENT_DURATION_CODE") != null
					&& (!"".equals(dataParm.getValue("PARENT_DURATION_CODE")))) {
				treeNode.setText(dataParm.getValue("PARENT_DURATION_CHN_DESC"));
				treeNode.setID(dataParm.getValue("PARENT_DURATION_CODE"));
				if (root.findChildNodeForID(treeNode.getID()) != null) {
					treeNode = root.findNodeForID(treeNode.getID());
				}
				TTreeNode childNode = new TTreeNode("KPILEAVE", noteType);
				childNode.setText(dataParm.getValue("DURATION_CHN_DESC"));
				childNode.setID(dataParm.getValue("DURATION_CODE"));
				treeNode.add(childNode);
			} else {
				treeNode.setText(dataParm.getValue("DURATION_CHN_DESC"));
				treeNode.setID(dataParm.getValue("DURATION_CODE"));
			}

			root.add(treeNode);
		}
		tree.update();
	}

	/**
	 * �õ�ʱ��������
	 * 
	 * @return List
	 */
	private List<TParm> getTreeDataList() {
		List list = new ArrayList();
		TParm selectParm = new TParm();
		selectParm.setData("CASE_NO", case_no);
		selectParm.setData("REGION_CODE", Operator.getRegion());
		TParm result = CLPVariationTool.getInstance().selectDuringInfo(
				selectParm);
		for (int i = 0; i < result.getCount(); i++) {
			TParm parm = result.getRow(i);
			list.add(parm);
		}
		return list;
	}

	/**
	 * �������䶯����
	 */
	public void onMonCatChange() {
		// �õ��������
		String monCatCode = this.getValueString("MONCAT_CODE");
		TTextFormat varianceCode = (TTextFormat) this
				.getComponent("VARIANCE_CODE");
		varianceCode.setValue("");
		StringBuffer sqlbf = new StringBuffer();
		sqlbf
				.append(" SELECT VARIANCE_CODE AS ID , VARIANCE_CHN_DESC AS NAME , VARIANCE_ENG_DESC AS ENNAME ");
		sqlbf.append(" FROM CLP_VARIANCE WHERE MONCAT_CODE = '"
				+ monCatCode.trim() + "' ");
		sqlbf.append(" AND REGION_CODE='" + Operator.getRegion() + "'");
		varianceCode.setPopupMenuSQL(sqlbf.toString());
		varianceCode.onQuery();
	}

	/**
	 * ʱ�����������
	 */
	public void duringTreeClick() {
		this.onQuery();
	}

	/**
	 * ���찴ť���
	 */
	public void varianceChange() {
		// �������쵥ѡ��
		boolean flag = (Boolean) this.callFunction("UI|varianceFlg|isSelected");
		String varianceFlg = "N";
		String varianceType = this.getValueString("MONCAT_CODE");
		String varianceStr = this.getValueString("VARIANCE_CODE");
		if (flag) {
			varianceFlg = "Y";
		} else {
			varianceFlg = "N";
			varianceType = "";
			varianceStr = "";
		}
		HashMap map = new HashMap();
		map.put("VARIANCEFLG", varianceFlg);
		map.put("MEDICAL_MONCAT", varianceType);
		map.put("MEDICAL_VARIANCE", varianceStr);
		map.put("MANAGE_MONCAT", varianceType);
		map.put("MANAGE_VARIANCE", varianceStr);
		// �����TParm�Ķ�Ӧ��λͳһ��ֵ
		setTableTParmValue(map);
		// ���ü�¼״̬
		if (flag) {
			TTable table = (TTable) this.getComponent("TABLEVARIATION");
			TParm tableParm = table.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				String status = tableParm.getRow(i).getValue("STATUS");
				if (newDataStr.equals(status)) {
					continue;
				}
				setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
			}
		}
	}

	/**
	 * ִ�е�ѡ���
	 */
	public void execFlagChange() {
		boolean flag = (Boolean) this.callFunction("UI|IsExeFlag|isSelected");
		String isexe = "";
		String progressStr = this.getValueString("PROGRESS_CODE");
		if (flag) {
			isexe = "Y";
		} else {
			isexe = "N";
			progressStr = "";
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			String status = tableParm.getRow(i).getValue("STATUS");
			if (newDataStr.equals(status)) {
				continue;
			}
//			if ("N".equals(tableParm.getRow(i).getValue("ORDER_FLG"))) {
//				tableParm.setData("ISEXE", i, isexe);
//				tableParm.setData("PROGRESS_CODE", i, progressStr);
//				// ���ü�¼״̬--ֻ�йؼ����Ʋ��д˹���
//				if (flag) {
//					setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
//				}
//			}
			tableParm.setData("ISEXE", i, isexe);
			tableParm.setData("PROGRESS_CODE", i, progressStr);
			// ���ü�¼״̬--ֻ�йؼ����Ʋ��д˹���
			if (flag) {
				setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
			}
		}
		table.setParmValue(tableParm);
	}

	/**
	 * ���ο��Ұ�ť���
	 */
	public void rdeptChange() {
		boolean flag = (Boolean) this.callFunction("UI|rdeptFlg|isSelected");
		String rdeptFlg = "N";
		String rdeptStr = this.getValueString("R_DEPT_CODE");
		if (flag) {
			rdeptFlg = "Y";
		} else {
			rdeptFlg = "N";
			rdeptStr = "";
		}
		HashMap map = new HashMap();
		map.put("RDEPTFLG", rdeptFlg);
		map.put("R_DEPT_CODE", rdeptStr);
		// �����TParm�Ķ�Ӧ��λͳһ��ֵ
		setTableTParmValue(map);
		// ���ü�¼״̬
		if (flag) {
			TTable table = (TTable) this.getComponent("TABLEVARIATION");
			TParm tableParm = table.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				String status = tableParm.getRow(i).getValue("STATUS");
				if (newDataStr.equals(status)) {
					continue;
				}
				setRowStatus(i, RecordStatus.editRecord, "TABLEVARIATION");
			}

		}
	}

	/**
	 * ���ü�¼״̬
	 * 
	 * @param row
	 *            int
	 * @param recordStatus
	 *            RecordStatus
	 */
	private void setRowStatus(int row, RecordStatus recordStatus,
			String tableName) {
		String str = "";
		switch (recordStatus) {
		case newRecord:
			str = "��";
			break;
		case editRecord:
			str = "�༭";
			break;
		default:
			str = "";
			break;
		}
		// System.out.println("��¼״̬���µ�ֵ��" + str);
		TTable table = (TTable) this.getComponent(tableName);
		TParm tableParm = table.getParmValue();
		tableParm.setData("STATUS", row, str);
		table.setParmValue(tableParm);
	}

	/**
	 * ������������״̬
	 * 
	 * @param tableName
	 *            String
	 */
	private void setTableRowStatus(String tableName, RecordStatus recordStatus) {
		// System.out.println("������������״̬");
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			setRowStatus(i, recordStatus, tableName);
		}
	}

	public enum RecordStatus {
		newRecord, editRecord, normalRecord;
	}

	/**
	 * �����TParm�Ķ�Ӧ��λͳһ��ֵ
	 * 
	 * @param map
	 *            HashMap
	 */
	private void setTableTParmValue(HashMap map) {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			Set set = map.keySet();
			Iterator itr = set.iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				// ֻ�з��¼������ݲ��д˹���
				String flagstr = tableParm.getValue("STATUS", i);
				if (!"��".equals(flagstr)) {
					tableParm.setData(key, i, map.get(key));
				}
			}
		}
		// System.out.println("tableParm:" + tableParm);
		table.setParmValue(tableParm);
	}

	/**
	 * ����ʵ������жϱ���ѡ�����Ƿ���Ա༭|
	 * 
	 */
	public void setTableEnabledWithValidColumn() {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		int column = table.getSelectedColumn();
		int row = table.getSelectedRow();
		String status = table.getParmValue().getValue("STATUS", row);
		boolean isNewRow = "��".equals(status);
		// �ж����Ƿ���Ա༭
		boolean isRowCanEdit = false;
		// �ɱ༭�ж�
		int[] source = null;
		// ������
		if (isNewRow) {
			source = this.newTableDataColumn;
			// ԭ����
		} else {
			source = this.editTableDataColumn;
		}
		for (int tmp : source) {
			if (tmp == column) {
				isRowCanEdit = true;
			}
		}
		// ������ǿ��Ա༭����ֱ��return
		if (!isRowCanEdit) {
			return;
		}
		setTableEnabled("TABLEVARIATION", table.getSelectedRow(), table
				.getSelectedColumn());
		// ����checkbox��ѡ��״̬�����ѡ�е�Ϊcheckbox�������������
		if (!isNewRow) {
			for (int i = 0; i < chks.length; i++) {
				if (chks[i] == column) {
					TParm parm = table.getParmValue();
					// ��ִ�н���״̬ʱֻ�йؼ�������Ŀ���н���״̬���ʷǽ���״̬����ʾ
					if (chkskeys[i].equals(this.isExe)
							&& !"N".equals(parm.getRow(row).getValue(
									"ORDER_FLG"))) {
						break;
					}
					if ("Y".equals(parm.getValue(chkskeys[i], row))) {
						parm.setData(chkskeys[i], row, "N");
						clearDefaultValueWithChekKey(chkskeys[i], row, parm);
					} else {
						parm.setData(chkskeys[i], row, "Y");
						addDefaultValueWithChekKey(chkskeys[i], row, parm);
					}
					table.setParmValue(parm);
				}
			}
		}
		// ����ʵ������жϱ��ı༭״̬�����ڱ��ĵ�һ�������
		setRowStatusWithTabelValueChangeAction(column, row);
	}

	/**
	 * ����ѡ�е�check�����Ĭ��ֵ������ԭ�򣬱���������ο��ң��Ƿ�ִ�У�
	 * 
	 * @param chkKey
	 *            String
	 * @param row
	 *            int
	 */
	private void addDefaultValueWithChekKey(String chkKey, int row,
			TParm tableParm) {
		// ����ԭ�򣬱������
		if (chkKey.equals(this.varianceFlg)) {
			tableParm.setData(this.medicalMoncat, row, this
					.getValue("MONCAT_CODE"));
			tableParm.setData(this.medicalVariance, row, this
					.getValue("VARIANCE_CODE"));
			tableParm.setData(this.manageMoncat, row, this
					.getValue("MONCAT_CODE"));
			tableParm.setData(this.manageVariance, row, this
					.getValue("VARIANCE_CODE"));
		}
		if (chkKey.equals(this.isExe)
				&& "N".equals(tableParm.getValue("ORDER_FLG", row))) {
			tableParm.setData(this.progressCode, row, this
					.getValue("PROGRESS_CODE"));
		}
		if (chkKey.equals(this.rDeptFlg)) {
			tableParm
					.setData(this.rDeptCode, row, this.getValue("R_DEPT_CODE"));
		}

	}

	/**
	 * ����ѡ�е�check�����Ĭ��ֵ������ԭ�򣬱���������ο��ң��Ƿ�ִ�У�
	 * 
	 * @param chkKey
	 *            String
	 * @param row
	 *            int
	 */
	private void clearDefaultValueWithChekKey(String chkKey, int row,
			TParm tableParm) {
		// ����ԭ�򣬱������
		if (chkKey.equals(this.varianceFlg)) {
			tableParm.setData(this.medicalMoncat, row, "");
			tableParm.setData(this.medicalVariance, row, "");
			tableParm.setData(this.manageMoncat, row, "");
			tableParm.setData(this.manageVariance, row, "");
		}
		if (chkKey.equals(this.isExe)
				&& "N".equals(tableParm.getValue("ORDER_FLG", row))) {
			tableParm.setData(this.progressCode, row, "");
		}
		if (chkKey.equals(this.rDeptFlg)) {
			tableParm.setData(this.rDeptCode, row, "");
		}

	}

	/**
	 * ����ʵ������жϱ��ı༭״̬�����ڱ��ĵ�һ�������
	 */
	public void setRowStatusWithTabelValueChangeAction(int column, int row) {
		// System.out.println("����״̬���÷���-----------------");
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		// ��������һ�еĻ���״̬���ø��³ɱ༭
		if (row == (table.getRowCount() - 1)) {
			return;
		}
		// ����ʵ������жϱ��ı༭״̬�����ڱ��ĵ�һ�������
		TParm tableParm = table.getParmValue();
		if ("Y".equals(tableParm.getValue("VARIANCEFLG", row))) {
			this.setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
		}
		if ("Y".equals(tableParm.getValue("ISEXE", row))
				&& "N".equals(tableParm.getValue("ORDER_FLG", row))) {
			this.setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
		}
		// System.out.println("--------------" +
		// tableParm.getValue("RDEPTFLG", row));
		if ("Y".equals(tableParm.getValue("RDEPTFLG", row))) {
			this.setRowStatus(row, RecordStatus.editRecord, "TABLEVARIATION");
		}

	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// ���ʱ�̴���
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node == null) {
			this.messageBox("��ѡ��ʱ��!");
			return;
		}
		// //�õ���ѯ��������type2 ���---��׼�У�ʵ��û�У��з���ע��
		// TParm parmType2 = this.getSearchTParm();
		// parmType2.setData("type", "2");
		// TParm data2 =
		// CLPVariationTool.getInstance().selectVariation(parmType2);
		// //�õ���ѯ��������type3 ���---��׼û�У�ʵ���У��з���ע��
		// TParm parmType3 = this.getSearchTParm();
		// parmType3.setData("type", "3");
		// TParm data3 =
		// CLPVariationTool.getInstance().selectVariation(parmType3);
		// �õ���ѯ��������type4 ��� ȫ��
		TParm parmType4 = this.getSearchTParm();
		parmType4.setData("type", "4");

		// //�����ѯ��������
		// System.out.println("data4:::::::"+data4);
		// compare2And3(data2, data3);
		// //��������2��3 �����ݼ��뵽4��
		// append2and3To4(data2, data3, data4);
		// ����ϲ�ͬ��������-Ĭ�ϱ��е������Ѿ��ϲ�ͬ����
		//TCheckBox cmbChk = (TCheckBox) this.getComponent("combination");
		TParm data4 = CLPVariationTool.getInstance().selectVariation(parmType4);
		// ����Ҫ�ϲ�����ʱ����ѯ�������ݲ𿪣����ݿ��е�����Ĭ���Ǻϲ��ģ�
		// if (!cmbChk.isSelected()) {
		// combinationDataToSeparate(data4);
		// }
		// else{
		// //System.out.println("data4::"+data4.getCount());
		// data4=combinationDataToSeparateSum(data4);
		// }
		// else{
		// //data4=
		// CLPVariationTool.getInstance().selectSumVariation(parmType4);
		// }
		// ����
		if (data4.getCount() > 0) {
			// ����ѡ��
			checkDiffer(data4);
			// ���ò���ѡ��ʱ���д���
			differNumberOperate(data4);
			// �����ֵ
			this.callFunction("UI|TABLEVARIATION|setParmValue", data4);
		} else {
			this.messageBox("��������!");
			this.callFunction("UI|TABLEVARIATION|setParmValue", new TParm());
		}
		// ��ѡ��ʱ�̲��Ǹ�ʱ��ʱ���ṩ�û���������
		if (node.getChildCount() <= 0 && node != null) {
			// ���ṩ�û���������
			addNewRow();
		}
	}

	/**
	 * �ϲ�ҽ��
	 * 
	 * @return
	 */
	private TParm getTalbeParmValue(TParm parm) {
		TParm result = new TParm();
		String orderCode = parm.getValue("ORDER_CODE", 0);// ҽ������
		// String schd_code=parm.getValue("SCHD_CODE",0);//ʱ��
		String chkUserCode = parm.getValue("CHKUSER_CODE", 0);// ִ����Ա
		String chkTypeCode = parm.getValue("CHKTYPE_CODE", 0);// ִ������
		// String exec_flg=parm.getValue("EXEC_FLG",0);//ִ��״̬
		String dispenseUnit = parm.getValue("DISPENSE_UNIT", 0);// ��׼��λ
		double tot = 0.00;// ��׼����
		double mainTot = 0.00;// ʵ������
		double totAmt = 0.00;// ��׼���
		double mainTotAmt = 0.00;// ʵ�ʽ��
		int index = 0;// �ۼ�����
		int nowIndex = 0;// �����һ�е����� ��TParm ��ֵ
		boolean flg = false;// ���ֻ��һ������ ��ִ�����¸�ֵ����
		for (int i = 0; i < parm.getCount(); i++) {
			flg = false;
			// �ϲ�
			if (parm.getValue("ORDER_CODE", i).equals(orderCode)
					&& parm.getValue("CHKUSER_CODE", i).equals(chkUserCode)
					&& parm.getValue("CHKTYPE_CODE", i).equals(chkTypeCode)
					&& parm.getValue("DISPENSE_UNIT", i).equals(dispenseUnit)) {
				tot += parm.getDouble("TOT", i);
				mainTot += parm.getDouble("MAINTOT", i);
				totAmt += parm.getDouble("TOT_AMT", i);
				mainTotAmt += parm.getDouble("MAIN_AMT", i);
				nowIndex = i;
				flg = true;
			} else {
				orderCode = parm.getValue("ORDER_CODE", i);// ҽ������
				// schd_code=parm.getValue("SCHD_CODE",i);//ʱ��
				chkUserCode = parm.getValue("CHKUSER_CODE", i);// ִ����Ա
				chkTypeCode = parm.getValue("CHKTYPE_CODE", i);// ִ������
				// exec_flg=parm.getValue("EXEC_FLG",i);//ִ��״̬
				dispenseUnit = parm.getValue("DISPENSE_UNIT", i);// ��׼��λ
				// ���һ������

				if (flg) {
					result.setRowData(index, parm, nowIndex);
					result.setData("TOT", index, tot);
					result.setData("MAINTOT", index, mainTot);
					result.setData("TOT_AMT", index, totAmt);
					result.setData("MAIN_AMT", index, mainTotAmt);
				} else {
					result.setRowData(index, parm, i);
				}
				tot = 0.00;// ��׼����
				mainTot = 0.00;// ʵ������
				totAmt = 0.00;// ��׼���
				mainTotAmt = 0.00;// ʵ�ʽ��
				index++;
			}
		}
		result.setCount(index);
		return result;
	}

	/**
	 * ����ͬҽ�������ݺϲ�
	 * 
	 * @param parm
	 */
	private void combinationData(TParm parm) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm rowParm = parm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			String mainOrderCode = rowParm.getValue("MAINORD_CODE");
			if (orderCode.equals(mainOrderCode)) {

			}
		}
	}

	/**
	 * ��ԭʼ�����еĺϲ����ݲ�
	 */
	public void combinationDataToSeparate(TParm sourceParm) {
		for (int i = 0; i < sourceParm.getCount(); i++) {
			TParm rowParm = sourceParm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE");
			String mainOrderCode = rowParm.getValue("MAINORD_CODE");
			if (!orderCode.equalsIgnoreCase(mainOrderCode)
					&& (this.checkNullAndEmpty(orderCode))
					&& (this.checkNullAndEmpty(mainOrderCode))) {
				TParm newParm = this.cloneTParm(rowParm);
				newParm.setData("ORDER_DESC", "");
				newParm.setData("ORDER_CODE", "");
				newParm.setData("DISPENSE_UNIT", "");
				newParm.setData("TOT", "");
				// luhai add 2011-07-25
				// �������ʱ��Ҫ���ݷ����жϣ����¾����ݵķ��������Ҫͬ��
				newParm.setData("TOT_AMT", "");
				addTParmToAnotherTParm(newParm, sourceParm);
				// ��ձ��м�¼��ʵ�ʼ�¼
				sourceParm.setData("MAIN_ORDER_CODE_DESC", i, "");
				sourceParm.setData("MAINDISPENSE_UNIT", i, "");
				sourceParm.setData("MAINTOT", i, "");
				// luhai add 2011-07-25
				// �������ʱ��Ҫ���ݷ����жϣ����¾����ݵķ��������Ҫͬ��
				sourceParm.setData("MAIN_AMT", i, "");
				sourceParm.setData("MAINORD_CODE", i, "");
			}
		}
	}

	public void onDelete() {
		selectRow = this.getSelectedRow("TABLEVARIATION");
		if (selectRow == -1) {
			this.messageBox("��ѡ����Ҫɾ��������");
			return;
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		TParm tableParm = table.getParmValue();
		String status = tableParm.getValue("STATUS", selectRow);
		// �ж��Ƿ��������������ݣ���ֻ��һ�����������ṩɾ������
		boolean ishasMoreNewRow = "��".equals(tableParm.getValue("STATUS",
				(selectRow - 1)))
				|| "��".equals(tableParm.getValue("STATUS", (selectRow + 1)));
		if ("��".equals(status) && ishasMoreNewRow) {
			table.removeRow(selectRow);
			table.acceptText();
		} else if ("��".equals(status)) {
			this.messageBox("����һ���������޷�ɾ��");
		} else {
			this.messageBox("���¼��������޷�ɾ��");
		}
	}

	/**
	 * ��ӿ��з���
	 */
	private void addNewRow() {
		// ��������ʱĬ�ϸ�������һ�� ���Ա��û����
		this.callFunction("UI|TABLEVARIATION|addRow");
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		tableParm.setData("STATUS", (tableParm.getCount() - 1), "��");
		tableParm.setData("ORDER_FLG", (tableParm.getCount() - 1), "Y");
		tableParm.setData("CASE_NO", (tableParm.getCount() - 1), this.case_no);
		tableParm.setData("CLNCPATH_CODE", (tableParm.getCount() - 1),
				this.clncPathCode);
		tableParm.setData("SCHD_CODE", (tableParm.getCount() - 1),
				getCurrentDuration()); // ȡ��һ�����ݵ�ʱ��
		tableParm.setData("REGION_CODE", (tableParm.getCount() - 1), Operator
				.getRegion());
		tableParm.setData("OPT_USER", (tableParm.getCount() - 1), Operator
				.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		tableParm.setData("OPT_DATE", (tableParm.getCount() - 1), datestr);
		tableParm.setData("OPT_TERM", (tableParm.getCount() - 1), Operator
				.getIP());
		table.setParmValue(tableParm);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ��֤����
		if (!validSaveData()) {
			return;
		}
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		TParm saveParm = new TParm();
		TParm parm = table.getParmValue();
		saveParm.setData("varianceParm", parm.getData());
		saveParm.setData("operatorMap", this.getBasicOperatorMap());
		TParm result = TIOM_AppServer.executeAction(
				"action.clp.CLPVariantionAction", "saveVariance", saveParm);
		if (result.getErrCode() <= 0) {
			this.messageBox("P0001");
			this.onQuery();
		} else {
			this.messageBox("E0001");
		}
	}

	/**
	 * ��֤��������
	 */
	private boolean validSaveData() {
		TTable table = (TTable) this.getComponent("TABLEVARIATION");
		table.acceptText();
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm rowParm = parm.getRow(i);
			if (rowParm.getValue("STATUS").equals("��")
					&& this.checkNullAndEmpty(rowParm
							.getValue("MAIN_ORDER_CODE_DESC"))) {
				// ����
				String mainTot = rowParm.getValue("MAINTOT");
				if ("".equals(mainTot)) {
					this.messageBox("����������");
					return false;
				}
				if ((!"".equals(mainTot)) && (!this.validDouble(mainTot))) {
					this.messageBox("����������Ϸ���ֵ");
					return false;
				}
				if (Double.parseDouble(mainTot) <= 0) {
					this.messageBox("����������Ϸ���ֵ");
					return false;
				}
				// ��λ
				String despenseUnit = rowParm.getValue("MAINDISPENSE_UNIT");
				if (!this.checkNullAndEmpty(despenseUnit)) {
					this.messageBox("�����뵥λ");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ���÷���
	 */
	public void feeAnalyse() {
		TParm sendParm = new TParm();
		sendParm.setData("CASE_NO", this.case_no);
		sendParm.setData("CLNCPATH_CODE", this.clncPathCode);
		// ���ʱ�̴���
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node != null) {
			String schdCode = node.getID();
			sendParm.setData("DURATION_CODE", schdCode);
		}
		// System.out.println("sendParm::::::"+sendParm);
		String resultstr = (String) this.openDialog(
				"%ROOT%\\config\\clp\\CLPBill.x", sendParm);
	}

	public void onClear() {
		TCheckBox combination = (TCheckBox) this.getComponent("combination");
		combination.setSelected(false);
		this.setValue("CHK_USER", "");
		this.setValue("ORDER_FLG", "");
		this.setValue("IPD_CHARGE_CODE", "");
		this.setValue("ORDTYPE_CODE", "");
		this.setValue("PROGRESS_CODE", "");
		this.setValue("MONCAT_CODE", "");
		this.setValue("VARIANCE_CODE", "");
		this.setValue("R_DEPT_CODE", "");
		this.setValue("CLP_DEPT_CODE", "");
		this.setValue("EXE_DEPT_CODE", "");
		this.setValue("DifferNumber", "");
		this.setValue("CLP_STATION_CODE", "");
		this.callFunction("UI|TABLEVARIATION|setParmValue", new TParm());
		TCheckBox chkExe=(TCheckBox)this.getComponent("EXEC_FLG");
		chkExe.setSelected(true);
		this.setValue("CHK_USER", "001");
	}

	/**
	 * ������д
	 */
	public void onEmrWrite() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", case_no);
		parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("IPD_NO", this.getValue("IPD_NO"));
		parm.setData("ADM_DATE", this.getValue("ADM_DATE"));
		parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
		// if (this.isOidrFlg()) {
		parm.setData("RULETYPE", "3");
		// д������(����)
		parm.setData("WRITE_TYPE", "OIDR");
		/**
		 * }else { parm.setData("RULETYPE", "2"); //д������(����)
		 * parm.setData("WRITE_TYPE",""); }
		 **/
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

	}

	/**
	 * ����ǰTOOLBAR����ҳ��ʹ���˹����Ĳ���ѡ��ҳ�� ��ѡ�������˵��ٴη��ظ�ҳ��ʱ��ֹ�˵���Ϊ����ѡ��ҳ��,�����˷���
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		return true;
	}

	/**
	 * ���ò��촦��
	 */
	private void differNumberOperate(TParm parm) {
		TRadioButton differD = (TRadioButton) this.getComponent("differD");
		if (!differD.isSelected()) {
			return;
		}
		String differNumberstr = this.getValueString("DifferNumber");
		if (!this.checkNullAndEmpty(differNumberstr)) {
			return;
		}
		if (!this.validDouble(differNumberstr)) {
			this.messageBox("��������ȷ���ò���ٷֱ�!");
			return;
		}
		double differNumber = Double.parseDouble(differNumberstr);
		for (int i = 0; i < parm.getCount(); i++) {
			TParm datarow = parm.getRow(i);
			String orderCode = datarow.getValue("ORDER_CODE");
			String mainOrdCode = datarow.getValue("MAINORD_CODE");
			// luhai 2011-07-25 ɾ�� ���������жϲ��� begin
			// String tot = datarow.getValue("TOT");
			// tot = "".equals(tot) ? "0" : tot;
			// String mainTot = datarow.getValue("MAINTOT");
			// mainTot = "".equals(mainTot) ? "0" : mainTot;
			// //System.out.println("ʵ������:" + mainTot + "��׼����:" + tot + "������:" +
			// differNumberstr);
			// //System.out.println("orderCode");
			// luhai 2011-07-25 ɾ�� ���������жϲ��� end
			if ("".equals(orderCode) || "".equals(mainOrdCode)) {
				continue;
			}
			// luhai 2011-07-25 ɾ�� ���������жϲ��� begin
			// if (!(Math.abs(Double.parseDouble(mainTot) -
			// Double.parseDouble(tot)) / Double.parseDouble(tot) >=
			// Double.parseDouble(differNumberstr) / 100)) {
			// parm.removeRow(i);
			// i--;
			// }
			// luhai 2011-07-05 ɾ�� ���������жϲ���
			// luhai �������ʵ�ʷ����жϲ��� begin
			double standardAMT = datarow.getDouble("TOT_AMT");
			double mainAMT = datarow.getDouble("MAIN_AMT");
			if (!(Math.abs(mainAMT - standardAMT) / standardAMT >= differNumber / 100)) {
				parm.removeRow(i);
				i--;
			}
			// luhai �������ʵ�ʷ����жϲ��� end
		}
	}

	/**
	 * ����ѡ��
	 */
	private void checkDiffer(TParm parm) {
		TRadioButton differA = (TRadioButton) this.getComponent("differA");
		// ��ѯȫ�����
		if (differA.isSelected()) {
			return;
		}
		// �ϲ����� ���� ���� ����
		// ================pangben 2012-7-11
		TCheckBox cmbChk = (TCheckBox) this.getComponent("combination");
		TRadioButton differN = (TRadioButton) this.getComponent("differN");
		TRadioButton differD = (TRadioButton) this.getComponent("differD");
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmdata = parm.getRow(i);
			if (!cmbChk.isSelected()) {
				if (differN.isSelected()) { // �޲���
					if (checkDataIsDifference(parmdata)) {
						// ��������ʱ����
						parm.removeRow(i);
						i--;
					}
				} else if (differD.isSelected()) {
					// �ǲ�������ʱ����
					// �в���
					if (!checkDataIsDifference(parmdata)) {
						parm.removeRow(i);
						i--;
					}
				}
			} else {
				if (checkDataIsSame(parmdata)) {
					// ��������ʱ����
					if (differN.isSelected()) { // �޲���
						parm.removeRow(i);
						i--;
					}
				} else if (differD.isSelected()) {
					// �ǲ�������ʱ����
					// �в���
					if (!checkDataIsSame(parmdata)) {
						parm.removeRow(i);
						i--;
					}
				}
			}
		}
	}

	/**
	 * �ϲ�ѡ��
	 * 
	 * @return ================pangben 2012-7-11
	 */
	private boolean checkDataIsSame(TParm parmdata) {
		String orderFlag = parmdata.getValue("ORDER_FLG");
		String ordTypeCode = parmdata.getValue("ORDTYPE_CODE");// �ٴ�·������
		String mainOrdTypeCode = parmdata.getValue("MAINORDTYPE_CODE");// ʵ���ٴ�·������
		// �������
		double totAMT = parmdata.getDouble("TOT_AMT");
		double mainAMT = parmdata.getDouble("MAIN_AMT");
		// �ؼ����Ʋ��������
		if ("N".equals(orderFlag)) {
			return false;
		}
		// ��׼�ٴ�·������Ϊ��
		// �ٴ�·������
		if (null == ordTypeCode || null == mainOrdTypeCode
				|| null != ordTypeCode && null != mainOrdTypeCode
				&& !ordTypeCode.equals(mainOrdTypeCode)) {
			return true;
		}
		// ���ò���
		if (totAMT != mainAMT) {
			return true;
		}
		return false;
	}

	/**
	 * ��������Ƿ��ǲ���
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean checkDataIsDifference(TParm parmdata) {
		String orderFlag = parmdata.getValue("ORDER_FLG");
		String orderCode = parmdata.getValue("ORDER_CODE");
		String mainOrdCode = parmdata.getValue("MAINORD_CODE");
		// �������
		double tot = parmdata.getDouble("TOT");
		double mainTot = parmdata.getDouble("MAINTOT");
		// String dispenseUnit = parmdata.getValue("DISPENSE_UNIT");
		// String mainDispenseUnit = parmdata.getValue("MAINDISPENSE_UNIT");
		// String tot = parmdata.getValue("TOT");
		// String mainTot = parmdata.getValue("MAINTOT");
		// �ؼ����Ʋ��������
		if ("N".equals(orderFlag)) {
			return false;
		}
		// System.out.println("--�����Ƿ�����ж�");
		// ��׼��ĿΪ��
		// if (!this.checkNullAndEmpty(orderCode)) {
		// return true;
		// }
		// // ʵ����ĿΪ��
		// if (!this.checkNullAndEmpty(mainOrdCode)) {
		// return true;
		// }
		// ��Ŀ����
		if (null == orderCode || null == mainOrdCode || null != orderCode
				&& null != mainOrdCode && !orderCode.equals(mainOrdCode)) {
			return true;
		}
		// luhai 2011-07-25 �޸� begin
		// //�ڱ�׼��Ŀ��ʵ����Ŀ�����ڵ�����£���λ��ͬ
		// if (!dispenseUnit.equals(mainDispenseUnit)
		// &&
		// (!"".equals(mainTot) &&
		// Double.parseDouble(tot) != Double.parseDouble(mainTot))
		// ) {
		// return true;
		// }
		// luhai 2011-07-25 ɾ�����ݵ�λ�������жϲ���-���÷����жϲ��� begin
		// ��λ��ͬ��Ϊ����
		// if (!dispenseUnit.equals(mainDispenseUnit)&&(!"".equals(mainTot))){
		// return true;
		// }
		// //��λ��ͬ��������ͬ����
		// if (dispenseUnit.equals(mainDispenseUnit)
		// &&
		// (!"".equals(mainTot) &&
		// Double.parseDouble(tot) != Double.parseDouble(mainTot))
		// ) {
		// return true;
		// }
		// luhai 2011-07-25 �޸� end
		// luhai 2011-07-25 ɾ�����ݵ�λ�������жϲ���-���÷����жϲ��� begin
		// luhai 2011-07-25 ������ݷ��ý����ж� begin
		if (tot != mainTot) {
			return true;
		}
		// luhai 2011-07-25 ������ݷ��ý����ж� end
		return false;
	}

	/**
	 * ��������2��3 �����ݼ��뵽4�� append2and3To4
	 * 
	 * @param data2
	 *            TParm
	 * @param data3
	 *            TParm
	 * @param data4
	 *            TParm
	 */
	private void append2and3To4(TParm data2, TParm data3, TParm data4) {
		for (int i = 0; i < data2.getCount(); i++) {
			TParm parm = data2.getRow(i);
			String[] names = parm.getNames();
			for (int k = 0; k < names.length; k++) {
				data4.addData(names[k], parm.getValue(names[k]));
			}
		}
		for (int i = 0; i < data3.getCount(); i++) {
			TParm parm = data3.getRow(i);
			String[] names = parm.getNames();
			for (int k = 0; k < names.length; k++) {
				data4.addData(names[k], parm.getValue(names[k]));
			}
		}
	}

	/**
	 * ��һ��TParm���뵽��һ��TParm
	 * 
	 * @param parm
	 *            TParm
	 * @param toParm
	 *            TParm
	 */
	public void addTParmToAnotherTParm(TParm parm, TParm toParm) {
		String[] names = parm.getNames();
		for (int k = 0; k < names.length; k++) {
			toParm.addData(names[k], parm.getValue(names[k]));
		}
		toParm.setCount(toParm.getCount() + 1);
	}

	/**
	 * ����2��3 ����
	 */
	private void compare2And3(TParm data2, TParm data3) {
		for (int i = 0; i < data2.getCount(); i++) {
			TParm data2Row = data2.getRow(i);
			String standCode2 = data2Row.getValue("ORDER_CODE");
			String standTot2 = data2Row.getValue("TOT");
			String standUnit2 = data2Row.getValue("DISPENSE_UNIT");
			String actualCode2 = data2Row.getValue("MAINORD_CODE");
			String actualTot2 = data2Row.getValue("MAINTOT");
			String actualUnit2 = data2Row.getValue("MAINDISPENSE_UNIT");
			String standardGroupNo2 = data2Row.getValue("S_GROUP") == null ? ""
					: data2Row.getValue("S_GROUP");
			for (int y = 0; y < data3.getCount(); y++) {
				TParm data3Parm = data3.getRow(y);
				String standCode3 = data3Parm.getValue("ORDER_CODE");
				String standTot3 = data3Parm.getValue("TOT");
				String standlUnit3 = data3Parm.getValue("DISPENSE_UNIT");
				String actualCode3 = data3Parm.getValue("MAINORD_CODE");
				String actualTot3 = data3Parm.getValue("MAINTOT");
				String actualUnit = data3Parm.getValue("MAINDISPENSE_UNIT");
				String actualGroupNo3 = data3Parm.getValue("A_GROUP");
				String mainOrderCodeDesc = data3Parm
						.getValue("MAIN_ORDER_CODE_DESC");
				// ��2��3 ���ԱȶԵ�����£���3�����ݼ���2 ������3 �ı�׼ֵ��ֵ���Ա��´�ѭ��������ȥ
				if ((!this.checkNullAndEmpty(actualCode2))
						&& (!this.checkNullAndEmpty(actualTot2))
						&& (!this.checkNullAndEmpty(actualUnit2))
						&& (!this.checkNullAndEmpty(standCode3))
						&& (!this.checkNullAndEmpty(standTot3))
						&& (!this.checkNullAndEmpty(standlUnit3))
						&& standardGroupNo2.trim().equals(actualGroupNo3)) {
					data2Row.setData("MAINORD_CODE", actualCode3);
					data2Row.setData("MAINTOT", actualTot3);
					data2Row.setData("MAINDISPENSE_UNIT", actualUnit);
					data2Row.setData("MAIN_ORDER_CODE_DESC", mainOrderCodeDesc);
					// //����3 �ı�׼ֵ��ֵ���Ա��´�ѭ��������ȥ
					// data3Parm.setData("ORDER_CODE","used");
					// data3Parm.setData("TOT","used");
					// data3Parm.setData("DISPENSE_UNIT","used");
					data3.removeRow(y);
					break;
				}

			}
		}

	}

	/**
	 * �õ���ѯ����
	 * 
	 * @return TParm
	 */
	private TParm getSearchTParm() {
		TParm parm = new TParm();
		TCheckBox tcbExecFlag = (TCheckBox) this.getComponent("EXEC_FLG");
		String execFlag = "Y";
		if (tcbExecFlag.isSelected()) {
			execFlag = "Y";
		} else {
			execFlag = "N";
		}
		parm.setData("EXEC_FLG", execFlag);
		TCheckBox combination = (TCheckBox) this.getComponent("combination");
		String combinationStr = "Y";
		if (combination.isSelected()) {
			combinationStr = "Y";
		} else {
			combinationStr = "N";
		}
		parm.setData("combination", combinationStr);
		parm.setData("CHK_USER", this.getValue("CHK_USER"));
		parm.setData("ORDER_FLG", this.getValue("ORDER_FLG"));
		parm.setData("IPD_CHARGE_CODE", this.getValue("IPD_CHARGE_CODE"));
		parm.setData("DEPT_CODE", this.getValue("CLP_DEPT_CODE"));
		parm.setData("ORDTYPE_CODE", this.getValue("ORDTYPE_CODE"));
		parm.setData("EXE_DEPT_CODE", this.getValue("EXE_DEPT_CODE"));
		parm.setData("CLNCPATH_CODE", clncPathCode);
		parm.setData("R_DEPT_CODE", this.getValue("R_DEPT_CODE"));
		parm.setData("PROGRESS_CODE", this.getValue("PROGRESS_CODE"));
		// parm.setData("MONCAT_CODE", this.getValue("MONCAT_CODE"));
		// parm.setData("VARIANCE_CODE", this.getValue("VARIANCE_CODE"));
		parm.setData("CLP_STATION_CODE", this.getValue("CLP_STATION_CODE"));
		// case_no
		parm.setData("CASE_NO", this.case_no);
		// ����region
		parm.setData("REGION_CODE", Operator.getRegion());
		// ����during
		// ���ʱ�̴���
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node != null) {
			String schdCode = node.getID();
			parm.setData("DURATION_CODE", schdCode);
		}
		return parm;
	}

	/**
	 * �õ���ǰʱ��
	 * 
	 * @return String
	 */
	private String getCurrentDuration() {
		// ���ʱ�̴���
		String schdCode = "";
		TTree duringTree = (TTree) this.getComponent("TTree");
		TTreeNode node = duringTree.getSelectionNode();
		if (node != null) {
			schdCode = node.getID();
		}
		return schdCode;
	}

	// /**
	// * ��ʼ��������Ϣ
	// */
	// private void initPatientInfo() {
	// TParm parm = new TParm();
	// parm.setData("CASE_NO", case_no);
	// TParm resultParm = CLPVariationTool.getInstance().selectPatientInfo(
	// parm);
	// //System.out.println("��ѯ���Ĳ�����ϢresultParm:" + resultParm);
	// if (resultParm.getCount() <= 0) {
	// //System.out.println("��ʼ��������Ϣ��û�в����Ӧ�Ĳ�����Ϣ");
	// return;
	// }
	// //��ֵ
	// TParm data = resultParm.getRow(0);
	// this.setValue("MR_NO", data.getValue("MR_NO"));
	// this.setValue("SEX", data.getValue("SEX"));
	// this.setValue("BED_NO", data.getValue("BED_NO"));
	// this.setValue("VS_DR_CODE", data.getValue("VS_DR_CODE"));
	// this.setValue("MR_NO", data.getValue("MR_NO"));
	// this.setValue("STAYHOSP_DAYS", data.getValue("STAYHOSP_DAYS"));
	// this.setValue("DS_DATE", data.getValue("DS_DATE"));
	// this.setValue("PAT_NAME", data.getValue("PAT_NAME"));
	// this.setValue("CLNCPATH_CODE", data.getValue("CLNCPATH_CODE"));
	// this.setValue("IN_DATE", data.getValue("IN_DATE"));
	// this.setValue("AVERAGECOST", data.getValue("AVERAGECOST"));
	// this.setValue("CASE_NO", data.getValue("CASE_NO"));
	// }

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
	 * ������֤����
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validDouble(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,2}([.][0-9]{1,2}){0,1}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
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
		if (!"".equals(lockRowStr)) {
			lockRowStr = lockRowStr.substring(0,
					((lockRowStr.length() - 1) < 0 ? 0
							: (lockRowStr.length() - 1)));
			// System.out.println("���д���" + lockRowStr);
			if (lockRowStr.length() > 0) {
				table.setLockRows(lockRowStr);
			}
		} else {
			table.setLockRows("");
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
		parm.setData("OPT_TERM", Operator.getRegion());
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
		map.put("OPT_TERM", Operator.getRegion());
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
	 * 
	* @Title: onCombineClick
	* @Description: TODO(�ϲ�ͬ����)
	* @author pangben
	* @throws
	 */
	public void onCombineClick(){
		TCheckBox cmbChk = (TCheckBox) this.getComponent("combination");
		TABLEVARIATION.setParmValue(new TParm());
		if (cmbChk.isSelected()) {
			callFunction("UI|IsExeFlag|setEnabled", false);
			callFunction("UI|varianceFlg|setEnabled", false);
			callFunction("UI|rdeptFlg|setEnabled", false);	
			TABLEVARIATION.setHeader("״,50;��ִ,30,boolean;������,100,CHKTYPE_CODE;���,60,ORDER_FLG;��׼��Ŀ,300;������,50;��λ,45,UNIT;,15;��Ŀ,300; ����,40;��λ,70,UNIT");
			TABLEVARIATION.setColumnHorizontalAlignmentData("0,left;1,center;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,right;10,left");
			TABLEVARIATION.setParmMap("STATUS;EXEC_FLG;CHKTYPE_CODE;ORDER_FLG;ORDER_DESC;TOT;DISPENSE_UNIT;FLG;MAIN_ORDER_CODE_DESC;MAINTOT;MAINDISPENSE_UNIT");
		}else{
			callFunction("UI|IsExeFlag|setEnabled", true);
			callFunction("UI|varianceFlg|setEnabled", true);
			callFunction("UI|rdeptFlg|setEnabled", true);
			TABLEVARIATION.setHeader("״,50;��ִ,30,boolean;ִ����,60,CHK_USER_SHOW;������,100,CHKTYPE_CODE;���,60,ORDER_FLG;��׼��Ŀ,300;������,50;��λ,45,UNIT;��׼��,60;,15;��Ŀ,300;����,40;��λ,70,UNIT;����,30,boolean;�������,150,MONCAT_CODE_SHOW;����ԭ��,150,VARIANCE_CODE_SHOW;ִ��,30,boolean;����״̬,150,PROGRESS_CODE; ִ�л�ʿ,100,USER_SHOW;��ע,120; �������,200,MONCAT_CODE_SHOW;����ԭ��,200,VARIANCE_CODE_SHOW;��ע,200;����,30,boolean; ���ο���,130,R_DEPT_CODE;������Ա,100,CHK_USER");
			TABLEVARIATION.setColumnHorizontalAlignmentData("0,left;1,center;2,left;3,left;4,left;5,left;6,right;7,left;8,right;10,left;11,right;12,left;13,center;14,left;15,left;16,center;17,left;18,left;19,left;20,left;21,left;22,center;23,center;24,left;25,left");
			TABLEVARIATION.setParmMap("STATUS;EXEC_FLG;CHKUSER_CODE;CHKTYPE_CODE;ORDER_FLG;ORDER_DESC;TOT;DISPENSE_UNIT;STANDARD;FLG;MAIN_ORDER_CODE_DESC;MAINTOT;MAINDISPENSE_UNIT;VARIANCEFLG;MEDICAL_MONCAT;MEDICAL_VARIANCE;ISEXE;PROGRESS_CODE;MAINCFM_USER;MEDICAL_NOTE;MANAGE_MONCAT;MANAGE_VARIANCE;MANAGE_NOTE;RDEPTFLG;R_DEPT_CODE;R_USER");
		}
		
	}
}
