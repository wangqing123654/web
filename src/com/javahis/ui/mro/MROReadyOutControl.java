
package com.javahis.ui.mro;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROLendTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.LEDMROUI;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ������ʱ����ȷ��
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
 * @author wangbin 2014.07.23
 * @version 4.0
 */
public class MROReadyOutControl extends TControl {

	private String pageParam; // ҳ�洫�α�ʶ(REG:�Һ�,ADM:סԺ)
	private String messageText;// ������Ϣ
	private TTable showTable;
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	
	/**
	 * �����
	 */
	private LEDMROUI ledMroUi;

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
		
		this.onInitPage();
		
		// ҳ������ʵ������
		this.addListener(showTable);
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		showTable = (TTable) this.getComponent("TABLE");
		callFunction("UI|print|setEnabled", false);
		String now = SystemTool.getInstance().getDate().toString().substring(0,
				10).replace('-', '/');
		
		// Ĭ����ʾ�ڶ�������
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("S_DATE", now); // ��ʼʱ��
		this.setValue("E_DATE", tommorwDate); // ����ʱ��
		
		// ������Ϣ
		if ("REG".equals(this.pageParam)) {
			messageText = "�Һž���";
			
			// ��ʾ�����
			openLEDMROUI();
		} else {
			messageText = "סԺ";
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		clearValue("SELECT_ALL");
		
		// ������ȷ��״̬
		if (getRadioButton("CONFIRM_STATUS_NO").isSelected()) {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|print|setEnabled", false);
		} else if (getRadioButton("CONFIRM_STATUS_YES").isSelected()) {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|print|setEnabled", true);
		}
		
		// ��ѯ������Ĳ�������
		TParm resultParm = MROBorrowTool.getInstance().queryMroReg(this.getQueryParm());
		if (resultParm.getCount() <= 0) {
			showTable.setParmValue(new TParm());
			messageBox("��������");
			return;
		}
		
		showTable.setParmValue(resultParm);
		
		// ֻ���ڿ�����ݲſ��Թ�ѡ
		String lockRows = "";
		for (int i = 0; i < showTable.getRowCount(); i++) {
			if (!"1".equals(showTable.getParmValue().getValue("IN_FLG", i))
					// add by wangbin 20141028 �������������ȡ��״̬�У���ȡ����Ҳ���ɹ�ѡ
					|| showTable.getParmValue().getBoolean("CANCEL_FLG", i)) {
				lockRows = lockRows + i;
				if (i < showTable.getRowCount() - 1) {
					lockRows = lockRows + ",";
				}
			}
			
			// ��ȡ���������б�����ɫ����Ϊ��ɫ
			if (showTable.getParmValue().getBoolean("CANCEL_FLG", i)) {
				showTable.setRowColor(i, new Color(255, 255, 0));
			} else {
				// ������ԭɫ
				showTable.removeRowColor(i);
			}
		}
		showTable.setLockRows(lockRows);
	}

	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		parm.setData("S_DATE", getValueString("S_DATE").substring(0, 10)
				.replace("-", "")
				+ "000000");
		parm.setData("E_DATE", getValueString("E_DATE").substring(0, 10)
				.replace("-", "")
				+ "235959");
		// ʱ��
		if (getValueString("SESSION_CODE").length() > 0) {
			parm.setData("SESSION_CODE", getValueString("SESSION_CODE"));
		}
		
		// ���ұ���
		if (getValueString(this.pageParam + "_DEPT_CODE").length() > 0) {
			parm.setData("DEPT_CODE", getValueString(this.pageParam + "_DEPT_CODE"));
		}
		
		// ҽ������
		if (getValueString(this.pageParam + "DR_CODE").length() > 0) {
			parm.setData("DR_CODE", getValueString(this.pageParam + "DR_CODE"));
		}
		
		// ״̬
		String status = ((TComboBox)this.getComponent("STATUS")).getSelectedText();
		if (status.length() > 0) {
			parm.setData("STATUS", status);
		}
		
		// ������
		if (getValueString("MR_NO").length() > 0) {
			parm.setData("MR_NO", getValueString("MR_NO"));
		}
		
		// ������ȷ��״̬
		if (getRadioButton("CONFIRM_STATUS_NO").isSelected()) {
			parm.setData("CONFIRM_STATUS", "0");
		} else if (getRadioButton("CONFIRM_STATUS_YES").isSelected()) {
			parm.setData("CONFIRM_STATUS", "1");
		}
		
		// �Һ�
		if ("REG".equals(this.pageParam)) {
			parm.setData("ADM_TYPE", "O");
		} else {
			// סԺ
			parm.setData("ADM_TYPE", "I");
		}
		
		return parm;
	}

	/**
	 * 
	 * ����
	 */
	public void onSave() {
		TParm parm = showTable.getParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�ޱ�������");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (showTable.getTable().isEditing()) {
			showTable.getTable().getCellEditor().stopCellEditing();
		}
		
		// ���벡�����Ĺ����ǰ׼������
		int checkDataCount = this.setDataForQueue(parm);
		
		if (checkDataCount < 0) {
			return;
		} else if (checkDataCount == 0) {
			// ���ݷ���ֵ�ж��Ƿ�ȫ��û��ѡ
			this.messageBox("�빴ѡ��Ҫ������ڿⲡ������");
			return;
		}
		
		TParm checkParm = new TParm();
		// �����ѹ�ѡ���������ж����Ѿ�ȡ������
		int checkInt = 0;
		// ��ǰ�������ʾ����������
		List<String> showRegMroNoList = new ArrayList<String>();
		// ����ҺŲ��������
		if ("REG".equals(this.pageParam)) {
			showRegMroNoList = ledMroUi.getAllLEDData();
		}
		// ͳ��Ӧ�ô��������ȥ��������
		List<String> needRemoveRegNoList = new ArrayList<String>();
		
		// ��֤��ǰ��ѡ�������Ƿ��Ѿ�ȡ��ԤԼ
		for (int i = parm.getCount() - 1; i > -1; i--) {
			if (parm.getBoolean("FLG", i)) {
				checkParm = MROBorrowTool.getInstance().queryMroRegCancel(parm.getRow(i));
				
				if (checkParm.getErrCode() < 0) {
					this.messageBox("ϵͳ����");
					err("ERR:" + checkParm.getErrCode() + checkParm.getErrText()
							+ checkParm.getErrName());
					return;
				}
				
				if (checkParm.getCount() > 0) {
					checkInt = checkInt + 1;
					this.messageBox("������Ϊ"+parm.getValue("MR_NO", i)+"�Ĳ�����"+parm.getValue("PAT_NAME", i)+"����ȡ��" + messageText);
				}
				
				// add by wangbin 20141029 ��ǰ��������еģ����ұ���ѡ��
				if (showRegMroNoList.contains(parm.getValue("MRO_REGNO", i))) {
					needRemoveRegNoList.add(parm.getValue("MRO_REGNO", i));
				}
			} else {
				// add by wangbin 20141029 ��ǰ��������е�
				if (showRegMroNoList.contains(parm.getValue("MRO_REGNO", i))) {
					// ���ڿ�Ļ�����ȡ����Ҳ��Ҫ����������Ƴ�
					if (!StringUtils.equals("1", parm.getValue("IN_FLG", i))
							|| parm.getBoolean("CANCEL_FLG", i)) {
						needRemoveRegNoList.add(parm.getValue("MRO_REGNO", i));
					} else {
						// û��ѡ�е�����ϴ��ڵĿɱ�������Ҳ��Ҫ��ʱ��֤ȡ��״̬
						checkParm = MROBorrowTool.getInstance().queryMroRegCancel(parm.getRow(i));
						
						if (checkParm.getCount() > 0) {
							needRemoveRegNoList.add(parm.getValue("MRO_REGNO", i));
						}
					}
				}
				
				parm.removeRow(i);
			}
		}
		
		// �����ѡ������ȫ��ȡ�����������ˢ������
		if (parm.getCount() <= checkInt) {
			this.onQuery();
			if ("REG".equals(this.pageParam)) {
				// �����ˢ�������
				this.getLedMroRemoveMroRegNo(needRemoveRegNoList);
			}
			return;
		}
		
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MROBorrowAction", "insertQueue", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		clearValue("SELECT_ALL");
		// ����ɹ����������
		this.onQuery();
		
		if ("REG".equals(this.pageParam)) {
			// �����ˢ�������
			this.getLedMroRemoveMroRegNo(needRemoveRegNoList);
		}
	}

	/**
	 * ȫѡ��ѡ��ѡ���¼�
	 */
	public void onCheckSelectAll() {
		if (showTable.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < showTable.getRowCount(); i++) {
				//ֻ���ڿ�Ĳ������ܽ���
				if ("1".equals(showTable.getParmValue().getValue("IN_FLG", i))
						// add by wangbin 20141028 �������������ȡ��״̬�У��ڿ�δȡ���Ĳſ��Թ�ѡ
						&& !showTable.getParmValue()
								.getBoolean("CANCEL_FLG", i)) {
					showTable.setItem(i, "FLG", "Y");
				}
			}
		} else {
			for (int i = 0; i < showTable.getRowCount(); i++) {
				showTable.setItem(i, "FLG", "N");
			}
		}
	}
	
	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.setValue("PAT_NAME", pat.getName());
			this.onQuery();
		}
	}
	
	/**
	 * Ϊ���벡�����Ĺ����׼������
	 * 
	 * @param parm
	 *            parm����
	 * @return �Ƿ��й�ѡҪ���������
	 */
	private int setDataForQueue(TParm parm) {
		// ����ֵ(-1_ϵͳ����,0_�޹�ѡ����,1_�й�ѡ����)
		int rntResult = 0;
		// ������Ա
		String optUser = Operator.getID();
		// ������ĩ
		String optTerm = Operator.getIP();
		TParm lendParm = new TParm();
		
		// ���ý�������
		if ("REG".equals(this.pageParam)) {
			lendParm.setData("LEND_TYPE", "O");
		} else {
			lendParm.setData("LEND_TYPE", "I");
		}
		
		// ����Һš�סԺҲ�������Ϊһ�ֽ���
		lendParm = MROLendTool.getInstance().selectdata(lendParm);
		
		if (lendParm.getErrCode() < 0) {
			this.messageBox("����ԭ���ֵ��ѯ����");
			err("ERR:" + lendParm.getErrCode() + lendParm.getErrText());
			return -1;
		}
		
		if (lendParm.getCount() <= 0) {
			this.messageBox("ȱ�ٽ���ԭ���ֵ�����");
			return -1;
		}
		
		for (int i = 0; i < parm.getCount(); i++) {
			if (rntResult == 0 && parm.getBoolean("FLG", i)) {
				rntResult = 1;
			}
			
			// ���ĺ�
			parm.addData("QUE_SEQ", "");
			// ����������
			parm.addData("QUE_DATE", parm.getValue("ADM_DATE", i).substring(0, 10));
			// סԺ��
			parm.addData("IPD_NO", parm.getData("IPD_NO", i));
			// Ժ��
			parm.addData("ADM_HOSP", parm.getData("CURT_LOCATION", i));
			// ���ĵ�λ
			parm.addData("REQ_DEPT", parm.getData("DEPT_CODE", i));
			// ������
			parm.addData("MR_PERSON", parm.getData("DR_CODE", i));
			// ����״��
			parm.addData("ISSUE_CODE", "0");

			// ����ԭ��
			parm.addData("LEND_CODE", lendParm.getValue("LEND_CODE", 0));
			// ȡ����
			parm.addData("CAN_FLG", "N");
			// �����
			parm.addData("CASE_NO", parm.getData("CASE_NO", i));
			// ������Ժ��
			parm.addData("QUE_HOSP", parm.getData("CURT_HOSP", i));
			// �黹�������
			parm.addData("IN_DATE", "");
			// �黹�����Ա
			parm.addData("IN_PERSON", "");
			// Ӧ�黹����
			parm.addData("RTN_DATE", "");
			// Ӧ�����
			parm.addData("DUE_DATE", "");
			
			if ("REG".equals(this.pageParam)) {
				// ���ⷽʽ(0_�Һų���, 1_סԺ����, 2_���ĳ���)
				parm.addData("OUT_TYPE", "0");
			} else {
				parm.addData("OUT_TYPE", "1");
			}
			
			// ������Ա
			parm.addData("OPT_USER", optUser);
			// ������ĩ
			parm.addData("OPT_TERM", optTerm);
			// ���İ����
			parm.addData("LEND_BOX_CODE", parm.getData("BOX_CODE", i));
			// ���Ĳ��
			parm.addData("LEND_BOOK_NO", parm.getData("BOOK_NO", i));
			// ʵ�ʳ���ʱ��
			parm.addData("OUT_DATE", "");
		}
		
		return rntResult;
	}

	/**
	 * ���
	 */
	public void onClear() {
		String clearValue = "SESSION_CODE;MR_NO;PAT_NAME;STATUS;SELECT_ALL;"
				+ this.pageParam + "_DEPT_CODE;" + this.pageParam + "_DR_CODE";
		clearValue(clearValue);
		showTable.setParmValue(new TParm());
	}
	
	/**
	 * ��ӡ�����ⲡ������
	 */
	public void onPrint() {
		TParm parm = showTable.getParmValue();
		
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (showTable.getTable().isEditing()) {
			showTable.getTable().getCellEditor().stopCellEditing();
		}
		
		int dataLen = parm.getCount();
		
		// ��ӡ��Parm
		TParm printParm = new TParm();
		// ��ӡ����
		TParm printData = new TParm();
		// ��ӡ�ð����
		String boxCode = "";
		int printDataLen = 0;
		String admTypeSql = "";
		String deptCodeSql = "";
		String drCodeSql = "";
		String admDate = "";
		TParm resultAdmType = new TParm();
		TParm resultDeptCode = new TParm();
		TParm resultDrCode = new TParm();
		for (int i = 0; i < dataLen; i++) {
			if (parm.getBoolean("FLG", i)) {
				boxCode = parm.getValue("BOX_CODE", i) + "-" + parm.getValue("ADM_TYPE", i) + "-" + parm.getValue("BOOK_NO", i);
				// �����
				printData.addData("BOX_CODE", boxCode);
				// ������
				printData.addData("MR_NO", parm.getValue("MR_NO", i));
				// ����
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				
				//��������
				admTypeSql = "SELECT CHN_DESC FROM (SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID ='SYS_ADMTYPE') " +
							"WHERE ID = '"+parm.getValue("ADM_TYPE",i)+"'";
				
				resultAdmType =  new TParm(TJDODBTool.getInstance().select(admTypeSql));
				printData.addData("ADM_TYPE", resultAdmType.getValue("CHN_DESC",0));
				
				//���Ŀ���
				deptCodeSql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+
								parm.getValue("DEPT_CODE",i)+"'";
				resultDeptCode = new TParm(TJDODBTool.getInstance().select(deptCodeSql));
				printData.addData("DEPT_CODE", resultDeptCode.getValue("DEPT_CHN_DESC",0));
				
				//������
				drCodeSql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+parm.getValue("DR_CODE",i)+"'";
				resultDrCode = new TParm(TJDODBTool.getInstance().select(drCodeSql));
				
				printData.addData("DR_CODE", resultDrCode.getValue("USER_NAME",0));
				
				//��������
				admDate = parm.getValue("ADM_DATE",i);
//				System.out.println("admDate = " + admDate);
//				if(null != admDate && "".equals(admDate) && admDate.length() > 0){
					admDate = admDate.substring(0, 11);
			
//				}
				
				printData.addData("ADM_DATE", admDate);
				
				printDataLen = printDataLen + 1;
			}
		}
		
		if (printDataLen < 1) {
			this.messageBox("�빴ѡ��ӡ����");
			return;
		}
		
		printData.setCount(printDataLen);
		
		printData.addData("SYSTEM", "COLUMNS", "BOX_CODE");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "ADM_TYPE");
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
		printData.addData("SYSTEM", "COLUMNS", "DR_CODE");
		printData.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		
		printParm.setData("PRINT_TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MROReadyOutPrint.jhw", printParm);
	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {
		TParm parm = showTable.getParmValue();
		if (null == parm || parm.getCount("MRO_REGNO") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		
		if (showTable.getRowCount() > 0) {
			String title = "";
			// ����Һ�
			if ("REG".equals(pageParam)) {
				title = "�ҺŲ���������ȷ��";
			} else {
				title = "סԺ����������ȷ��";
			}
			
			TParm exportParm = showTable.getShowParmValue(); 
			exportParm.setData("TITLE", title);
			// ������Excel������ȥ����ѡ����һ��
			exportParm.setData("HEAD", showTable.getHeader().replaceAll("ѡ,30,boolean;", ""));
	        String[] header = showTable.getParmMap().split(";");
	        for (int i = 1; i < header.length; i++) {
	        	exportParm.addData("SYSTEM", "COLUMNS", header[i]);
	        }
	        TParm[] execleTable = new TParm[]{exportParm};
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, title);
		}
	}
	
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);

				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж�
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = showTable.getParmValue();
				tableData.removeGroupData("SYSTEM");

				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}

				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = showTable.getParmMap(
						sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);

				// ��������vectorת��parm;
				TParm lastResultParm = new TParm();// ��¼���ս��
				lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// �����м�����

				table.setParmValue(lastResultParm);
			}
		});
	}

	/**
	 * ����������
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            parm
	 * @param group
	 *            ����
	 * @param names
	 *            "ID;NAME"
	 * @param size
	 *            �������
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
	 * vectoryת��param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
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
		return parmTable;

	}


	/**
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
	
	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
	
	/**
	 * ������������������˫������
	 * 
	 * @param mroRegNo
	 */
	public void openInwCheckWindow(String mroRegNo) {
		getRadioButton("CONFIRM_STATUS_NO").setSelected(true);
		onQuery();
	}

	/**
	 * �������ʾ
	 */
	public void openLEDMROUI() {
		Component com = (Component) getComponent();
		TParm parm = new TParm();
		parm.setData("MRO", "ODO");
		while ((com != null) && (!(com instanceof Frame))) {
			com = com.getParent();
		}
		this.ledMroUi = new LEDMROUI((Frame) com, this, parm);
		this.ledMroUi.openWindow();
	}

	/**
	 * ����ƹر�
	 */
	public boolean onClosing() {
		if (null != ledMroUi) {
			this.ledMroUi.close();
		}
		return true;
	}

	/**
	 * �Ƴ����������
	 */
	public void getLedMroRemoveMroRegNo(List<String> mroRegNoList) {
		TParm parm = new TParm();
		
		for (int i = 0; i < mroRegNoList.size(); i++) {
			parm.setData("MRO_REGNO", mroRegNoList.get(i));
			if (ledMroUi != null) {
				ledMroUi.removeMessage(parm);
			}
		}
	}
}
