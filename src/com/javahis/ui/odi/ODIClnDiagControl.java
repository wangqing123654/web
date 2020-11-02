package com.javahis.ui.odi;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;

import com.javahis.util.OdiUtil;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import jdo.clp.BscInfoTool;
import jdo.adm.ADMInpTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title: ���
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.9.16
 * @version 1.0
 */

public class ODIClnDiagControl extends TControl {

	private TParm parm;
	// ���Ҵ���
	private String dept_code;
	// ҽʦ����
	private String dr_code;

	private String code;
	// �������
	private String onlyCaseNo;
	// �Ա�
	private String sexCode;
	// ����
	private Timestamp birthday;
	// ��Ժʱ��
	private Timestamp inDate;
	// �������
	private String ioType;

	TParm delparm = new TParm();

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ȡ�ô������
		Object obj = this.getParameter();
		String case_no = "";
		if (obj != null) {
			parm = (TParm) obj;
			case_no = parm.getValue("CASE_NO");
			onlyCaseNo = case_no;
			sexCode = parm.getValue("SEX_CODE");
			birthday = parm.getTimestamp("BIRTHDAY");
			inDate = parm.getTimestamp("IN_DATE");
			// ���Ҵ���
			dept_code = parm.getValue("DEPT_CODE");
			// ҽʦ����
			dr_code = parm.getValue("DR_CODE");
		}
		// ����ҽ��Ϲ���
		onChangeICD_TYPE();
		// ��ʼ����ť״̬
		// callFunction("UI|delete|setEnabled", false);
		callFunction("UI|card|setEnabled", false);
		// ע�ἤ��SYSICDPopup�������¼�
		getTable("CLNDIAG_TABLE").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponentUD");
		getTable("CLNDIAG_TABLE").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onClnDiagTableCheckBoxClicked");
		// ��������¼�
		addEventListener("CLNDIAG_TABLE->" + TTableEvent.CHANGE_VALUE, "onTableDChangeValue");
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		TFrame f = ((TFrame) this.getComponent("UI"));
		if (table.getRowCount() >= 4) {
			int height = 496 + (table.getRowCount() * 10);
			f.setHeight(height);
			f.repaint();
		}
	}

	/**
	 * ���淽��
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		// ���ICD_CODE�Ƿ��ظ�
		Map map = new HashMap();
		String value = "";
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			value = (String) table.getItemString(i, "IO_TYPE") + ":" + (String) table.getItemString(i, "ICD_CODE");
			if (map.containsValue(value)) {
				// ͬһ�����������ͬ���
				this.messageBox("E0041");
				return false;
			}
			map.put(i, value);
		}
		// �ж��Ƿ���������
		String io_type = "";
		String main_flg = "";
		String icd_code = "";
		Map main_I = new HashMap();
		Map main_M = new HashMap();
		Map main_O = new HashMap();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			io_type = (String) table.getItemString(i, "IO_TYPE");
			if (StringUtil.isNullString(io_type)) {
				this.messageBox("����д������");
				return false;// add by wanglong 20140404
			}
			main_flg = (String) table.getItemString(i, "MAINDIAG_FLG");
			icd_code = (String) table.getItemString(i, "ICD_CODE");
			if ("I".equals(io_type))
				main_I.put(main_flg, icd_code);
			if ("M".equals(io_type))
				main_M.put(main_flg, icd_code);
			if ("O".equals(io_type))
				main_O.put(main_flg, icd_code);
		}
		if (!main_I.isEmpty()) {
			if (!main_I.containsKey("Y")) {
				// �ż�����ϲ����������
				this.messageBox("�ż�����ϲ����������");
				// onInit();
				delparm.removeData("CASE_NO");
				delparm.removeData("SEQ_NO");
				return false;
			}
		}
		if (!main_M.isEmpty()) {
			if (!main_M.containsKey("Y")) {
				// סԺ��ϲ����������
				this.messageBox("סԺ��ϲ����������");
				// onInit();
				delparm.removeData("CASE_NO");
				delparm.removeData("SEQ_NO");
				return false;
			}
		}
		if (!main_O.isEmpty()) {
			if (!main_O.containsKey("Y")) {
				// ��Ժ��ϲ����������
				this.messageBox("��Ժ��ϲ����������");
				// //ɾ������Ϻ������棬�������һ�б�� duzhw add 20131018
				// StringBuffer lockRow = new StringBuffer();
				// for(int j = 0; j < table.getRowCount()-1; j++){
				// if(j==0){
				// lockRow.append(Integer.toString(j));
				// }else{
				// lockRow.append(",").append(Integer.toString(j));
				// }
				// }
				// //System.out.println("lockRow="+lockRow.toString());
				// table.setLockRows(lockRow.toString());//�����У��������޸�
				// onInit();
				delparm.removeData("CASE_NO");
				delparm.removeData("SEQ_NO");

				return false;
			}
		}
		String ticd_type = "";
		if (getRadioButton("RadioButton1").isSelected())
			ticd_type = "W";
		else
			ticd_type = "C";

		// ��������
		TParm update = new TParm();
		// CASE_NO������case_no
		update.setData("CASE_NO", parm.getValue("CASE_NO"));
		TParm result = new TParm();
		int Outcount = 2;
		int Oecount = 2;
		int incount = 2;
		int count = 0;

		// ��ȡ������ʼ�������Ϣresult_old --duzhw add 20131013
		// TParm result_old = getAdm_InpDiag(parm.getValue("CASE_NO"));

		// ��ȡ�����£��£������Ϣ result
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		for (int i = 0; i < table.getRowCount(); i++) {
			// modified by WangQing 20170516
			if (table.getItemString(i, "ICD_CODE").equals("") || table.getItemString(i, "ICD_DESC").equals("")) {

				continue;
			}

			result.addData("CASE_NO", parm.getValue("CASE_NO"));// CASE_NO�������
			result.addData("MR_NO", parm.getValue("MR_NO"));// MR_NO�������
			result.addData("IPD_NO", parm.getValue("IPD_NO"));// IPD_NO�������
			result.addData("IO_TYPE", table.getItemString(i, "IO_TYPE"));// ����
			result.addData("ICD_KIND", ticd_type);// ��ϱ���
			result.addData("MAIN_FLG",
					"Y".equals(table.getItemString(i, "MAINDIAG_FLG")) ? table.getItemString(i, "MAINDIAG_FLG") : "N");// ��
			result.addData("ICD_CODE", table.getItemString(i, "ICD_CODE"));// ��ϱ���
			result.addData("SEQ_NO", count);// ���
			result.addData("ICD_DESC", orderDesc.getTableShowValue(table.getItemString(i, "ICD_CODE")));// �������
			result.addData("ICD_REMARK", table.getItemString(i, "DESCRIPTION"));// ��ע
			result.addData("ICD_STATUS", "");// ת��
			result.addData("ADDITIONAL_CODE", "");// ������
			result.addData("ADDITIONAL_DESC", "");// �����������
			result.addData("IN_PAT_CONDITION", "");// ��Ժ����
			result.addData("OPT_USER", Operator.getID());
			result.addData("OPT_TERM", Operator.getIP());
			result.addData("EXEC", "Y".equals(table.getItemString(i, "EXEC")) ? table.getItemString(i, "EXEC") : "N");// �жϱ�ʶ->Y�������ݣ�������
																														// N�������ݣ����룩
																														// --duzhw
																														// add
																														// 20131010
			count++;
		}

		// ɸѡ���������ݣ���EXEC=='N'������ result2Ϊ��ǰ��������
		int insertCount = 0;
		TParm result2 = new TParm();
		// TParm old_parm = new TParm();
		// String seq_sql = " select seq_no.nextVal from dual ";
		String new_seq_no = getMaxSeqNo(result.getValue("CASE_NO", 0));
		int seqCount = Integer.parseInt(new_seq_no);
		for (int k = 0; k < result.getCount("CASE_NO"); k++) {
			String exec = result.getValue("EXEC", k);
			// System.out.println("k="+k+" exec="+exec);
			if ("N".equals(exec)) {
				// TParm seq_parm = new TParm(TJDODBTool.getInstance().select(seq_sql));
				// new_seq_no = seq_parm.getValue("NEXTVAL",0);
				String icdCode = result.getValue("ICD_CODE", k);
				result.setData("SEQ_NO", k, seqCount);
				result2.addRowData(result, k);
				seqCount++;
				// System.out.println("����װ��parm[result2]="+result2);
			}
			// else{//֮ǰ�������� - ���ڱȽ� �����κδ���
			// old_parm.addRowData(result, k);
			// }

		}

		// ѭ���Ƚ�Ŀǰ������old_parm �ͳ�ʼ�������� result_old ����Ĳ���Ϊɾ��������
		// TParm del_parm = new TParm();
		// boolean exit = false;
		// for (int j = 0; j < result_old.getCount("CASE_NO"); j++) {
		// String caseNo1 = result_old.getValue("CASE_NO", j);
		// //String ioType1 = result_old.getValue("IO_TYPE", j);
		// String icdCode1 = result_old.getValue("ICD_CODE", j);
		// String mainDiagFlg1 = result_old.getValue("MAINDIAG_FLG", j);
		// System.out.println("caseNo1="+caseNo1+" icdCode1="+icdCode1+"
		// mainDiagFlg1="+mainDiagFlg1);
		// for (int j2 = 0; j2 < old_parm.getCount("CASE_NO"); j2++) {
		// String caseNo2 = old_parm.getValue("CASE_NO", j2);
		// //String ioType2 = result_old.getValue("IO_TYPE", j2);
		// String icdCode2 = old_parm.getValue("ICD_CODE", j2);
		// String mainDiagFlg2 = old_parm.getValue("MAIN_FLG", j2);
		// System.out.println("caseNo2="+caseNo2+" icdCode2="+icdCode2+"
		// mainDiagFlg2="+mainDiagFlg2);
		// if(caseNo1.equals(caseNo2) && icdCode1.equals(icdCode2) &&
		// mainDiagFlg1.equals(mainDiagFlg2)){
		// //System.out.println("��ͬ������Ϊ���������������ݣ������κδ���");
		// exit = true;
		// }
		// }
		// if(!exit){//�������������в�����-��Ϊɾ������
		// del_parm.addRowData(result_old, j);//��׼��ɾ�������ݷŵ�del_parm��
		// }
		// exit = false;//�ûس�ʼ״̬
		// }
		// ��������-�������ݣ�result2 ɾ�����ݣ�del_parm

		// System.out.println("�������� result2="+result2);

		// �������
		String maindiag = "";
		if (!main_I.isEmpty()) {
			maindiag = main_I.get("Y").toString();
		}
		if (!main_M.isEmpty()) {
			maindiag = main_M.get("Y").toString();
		}
		if (!main_O.isEmpty()) {
			maindiag = main_O.get("Y").toString();
		}
		// MAINDIAG
		update.setData("MAINDIAG", maindiag);
		// OPT_USER
		update.setData("OPT_USER", Operator.getID());
		// OPT_TERM
		update.setData("OPT_TERM", Operator.getIP());
		String caseNo = onlyCaseNo;
		parm.setData("CASE_NO", caseNo);
		//
		update.setData("CASE_NO", caseNo);
		TParm clpPathParm = ADMInpTool.getInstance().selectall(parm);
		// System.out.println("�ٴ�·����Ϣ"+clpPathParm);
		// int clpCount = clpPathParm.getCount("CLNCPATH_CODE");
		// ��Ժ�����
		String oldMDiagCode = "";
		// ��Ժ�����
		String oldODiagCode = "";
		// �ٴ�·���Ƿ�������
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			if ("M".equals(table.getItemString(i, "IO_TYPE")) && "Y".equals(table.getItemString(i, "MAINDIAG_FLG")))
				oldMDiagCode = table.getItemString(i, "ICD_CODE");
			if ("O".equals(table.getItemString(i, "IO_TYPE")) && "Y".equals(table.getItemString(i, "MAINDIAG_FLG")))
				oldODiagCode = table.getItemString(i, "ICD_CODE");
		}
		String clpBscInfoSql = "";
		String clpBscInfoDesc = "";
		TParm clpBscInfoParm = new TParm();
		// �Ƿ�����ٴ�·��
		if (clpPathParm.getData("CLNCPATH_CODE", 0) != null && clpPathParm.getValue("CLNCPATH_CODE", 0).length() > 0) {
			// System.out.println("�����ٴ�·����������������������������");
			clpBscInfoSql = " SELECT CLNCPATH_CHN_DESC FROM CLP_BSCINFO " + "  WHERE CLNCPATH_CODE = '"
					+ clpPathParm.getValue("CLNCPATH_CODE", 0) + "' ";
			clpBscInfoParm = new TParm(TJDODBTool.getInstance().select(clpBscInfoSql));
			clpBscInfoDesc = clpBscInfoParm.getValue("CLNCPATH_CHN_DESC", 0);
			TParm inBscParm = new TParm();
			inBscParm.setData("CLNCPATH_CODE", clpPathParm.getValue("CLNCPATH_CODE", 0));
			TParm bscParm = BscInfoTool.getInstance().existBscinfo(inBscParm);
			if (bscParm.getCount("ICD_CODE") > 0) {
				// System.out.println("�����ٴ�·��ICD"+bscParm);
				if (oldMDiagCode.length() > 0) {
					// System.out.println("��Ժ�����"+oldMDiagCode);
					// IO_TYPE;MAINDIAG_FLG
					if (!oldMDiagCode.equals(bscParm.getValue("ICD_CODE", 0))) {
						// System.out.println("1111111111111111111111");
						this.messageBox("��Ժ�����,������" + clpBscInfoDesc + "�ٴ�·��,�������");
					}
				}
				if (oldODiagCode.length() > 0) {
					// System.out.println("��Ժ�����"+oldODiagCode);
					// IO_TYPE;MAINDIAG_FLG
					if (!oldODiagCode.equals(bscParm.getValue("ICD_CODE", 0))) {
						// System.out.println("22222222222222222222");
						this.messageBox("��Ժ�����,������" + clpBscInfoDesc + "�ٴ�·��,�������");
					}
				}

			}
		} else {
			// System.out.println("û���ٴ�·����������������������������");
			if (oldMDiagCode.length() > 0) {
				// System.out.println("��Ժ�����");
				TParm inBscParmNew = new TParm();
				inBscParmNew.setData("ICD_CODE", oldMDiagCode);
				TParm bscParmNew = BscInfoTool.getInstance().existBscinfo(inBscParmNew);
				// System.out.println("��������"+bscParmNew);
				if (bscParmNew.getData("CLNCPATH_CODE") != null && bscParmNew.getValue("CLNCPATH_CODE", 0).length() > 0)
					this.messageBox("�������" + bscParmNew.getValue("CLNCPATH_CODE", 0) + "�ٴ�·��");
			}
			if (oldODiagCode.length() > 0) {
				// System.out.println("��Ժ�����");
				TParm inBscParmNew = new TParm();
				inBscParmNew.setData("ICD_CODE", oldODiagCode);
				TParm bscParmNew = BscInfoTool.getInstance().existBscinfo(inBscParmNew);
				// System.out.println("��������"+bscParmNew);
				if (bscParmNew.getData("CLNCPATH_CODE") != null && bscParmNew.getValue("CLNCPATH_CODE", 0).length() > 0)
					this.messageBox("�������" + bscParmNew.getValue("CLNCPATH_CODE", 0) + "�ٴ�·��");
			}
		}
		update.setData("DIAGDATA", result2.getData());
		update.setData("DELDATA", delparm.getData()); // duzhw add by 20131016
		result2 = TIOM_AppServer.executeAction("action.odi.ODIClnDiagAction", "onSave", update);
		if (result2.getErrCode() < 0) {
			this.messageBox("E0001" + result2.getErrName() + result2.getErrText());
			return false;
		}
		this.messageBox("P0001");
		// �ж��Ƿ��д�Ⱦ��
		Set setCHLR = new HashSet();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getItemString(i, "ICD_CODE").equals(""))
				continue;
			String icd_type = "W";
			if (!getRadioButton("RadioButton1").isSelected())
				icd_type = "C";
			icd_code = (String) table.getValueAt(i, 2);
			String sql = "SELECT CHLR_FLG FROM SYS_DIAGNOSIS";
			sql += " WHERE ICD_TYPE = '" + icd_type + "' AND ICD_CODE = '" + icd_code + "'";
			TDataStore ds = new TDataStore();
			ds.setSQL(sql);
			ds.retrieve();
			String chlr_flg = ds.getItemString(0, "CHLR_FLG");
			if ("Y".equals(chlr_flg))
				setCHLR.add(icd_code);
		}
		// ���ô�Ⱦ�����濨
		if (setCHLR.size() > 0) {
			Iterator iterator = setCHLR.iterator();
			while (iterator.hasNext()) {
				onTransferMROInfect(parm.getValue("MR_NO"), parm.getValue("CASE_NO"), parm.getValue("IPD_NO"),
						TypeTool.getString(iterator.next()));// modify by wanglong 20140307
			}
		}
		// ���delparm duzhw add 20131021
		delparm.removeData("CASE_NO");
		delparm.removeData("SEQ_NO");

		onInit();
		return true;
	}

	/**
	 * ȡMRO_RECORD_DIAG���ADM_INPDIAG��������seq_no��1 duzhw 20131129
	 * 
	 * @return
	 */
	public String getMaxSeqNo(String caseNo) {
		int seqNo1 = 0;
		int seqNo2 = 0;
		int maxSeqNo = 0;
		String seqNoStr1 = "";
		String seqNoStr2 = "";
		boolean flag = false;
		String sql1 = "select max(seq_no) as seq_no from ADM_INPDIAG where case_no = '" + caseNo + "'";
		String sql2 = "select max(seq_no) as seq_no from MRO_RECORD_DIAG where case_no = '" + caseNo + "'";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		seqNoStr1 = result1.getValue("SEQ_NO", 0);
		seqNoStr2 = result2.getValue("SEQ_NO", 0);
		if (!"".equals(seqNoStr1) && seqNoStr1 != null) {
			seqNo1 = Integer.parseInt(seqNoStr1);
		}
		if (!"".equals(seqNoStr2) || seqNoStr2 != null) {
			seqNo2 = Integer.parseInt(seqNoStr2);
		}
		// �жϱȽ�
		if (seqNo1 >= seqNo2) {
			maxSeqNo = seqNo1;
		} else {
			maxSeqNo = seqNo2;
		}
		return Integer.toString(maxSeqNo + 1);

	}

	/**
	 * ģ����ѯ���ڲ��ࣩ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		TTable table = getTable("CLNDIAG_TABLE");
		// update by duzhw 20131011��ɾ����������ҳ�漶�ٵ㱣�棬��Ҫ�ߺ�̨��
		int row = table.getTable().getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ�������ݣ�");
			return;
		}
		if ("Z".equals(table.getItemString(row, "IO_TYPE"))) {// ������ϲ�����ɾ��
			this.messageBox("������ϲ�����ɾ��");
			return;
		}
		String caseNo = parm.getValue("CASE_NO");
		String seqNo = table.getItemString(row, "SEQ_NO");
		// //�ж�������ڱ���סԺ�ڼ��Ƿ񿪹�����ҩ û��������ɾ������������� ����������ɾ�� start 20170706 machao
		// String sql = "SELECT COUNT (*) COUNT "+
		// "FROM ODI_ORDER "+
		// "WHERE CASE_NO = '"+caseNo+"' AND ANTIBIOTIC_CODE IS NOT NULL ";
		// TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// //System.out.println("111111111"+sql);
		// //this.messageBox(parm.getInt("COUNT",0)+"");
		// if(parm.getInt("COUNT",0) > 0 ){
		// this.messageBox("����������ҩ,������ɾ�������������");
		// return;
		// }
		// end 20170706 machao

		// System.out.println("׼��ɾ����caseNo="+caseNo+" seqNo="+seqNo);
		delparm.addData("CASE_NO", caseNo);
		delparm.addData("SEQ_NO", seqNo);
		// System.out.println("delparm="+delparm);
		//
		// TParm delete = new TParm();
		// TParm result = new TParm();
		// result.setData("CASE_NO", caseNo);
		// result.setData("SEQ_NO", seqNo);
		// delete.setData("DIAGDATA", result.getData());
		// result = TIOM_AppServer.executeAction("action.odi.ODIClnDiagAction",
		// "onDelete", delete);
		// if (result.getErrCode() < 0) {
		// this.messageBox("E0001" + result.getErrName()
		// + result.getErrText());
		// }else{
		// this.messageBox("ɾ���ɹ���");
		// }

		table.removeRow(row);
		lockRow();// ����������
	}

	/**
	 * �����з���duzhw 20131210
	 */
	public void lockRow() {
		TTable table = getTable("CLNDIAG_TABLE");
		StringBuffer lockRow = new StringBuffer();
		for (int i = 0; i < table.getRowCount() - 1; i++) {
			String seqNo = table.getItemString(i, "SEQ_NO");
			String exec = table.getItemString(i, "EXEC");
			if (!seqNo.equals("") || exec.equals("Y")) {
				if (i == 0) {
					lockRow.append(Integer.toString(i));
				} else {
					lockRow.append(",").append(Integer.toString(i));
				}
			}
		}
		table.setLockRows(lockRow.toString());// �����У��������޸�
	}

	/**
	 * ��Ⱦ�����濨
	 */
	public void onCard() {
		TTable table = getTable("CLNDIAG_TABLE");
		int row = table.getTable().getSelectedRow();
		String icd_type = "W";
		if (!getRadioButton("RadioButton1").isSelected())
			icd_type = "C";
		String icd_code = (String) table.getValueAt(row, 2);
		String sql = "SELECT CHLR_FLG FROM SYS_DIAGNOSIS";
		sql += " WHERE ICD_TYPE = '" + icd_type + "' AND ICD_CODE = '" + icd_code + "'";
		TDataStore ds = new TDataStore();
		ds.setSQL(sql);
		ds.retrieve();
		String chlr_flg = ds.getItemString(0, "CHLR_FLG");
		if ("Y".equals(chlr_flg)) {
			onTransferMROInfect(parm.getValue("MR_NO"), parm.getValue("CASE_NO"), parm.getValue("IPD_NO"), icd_code);// modify
																														// by
																														// wanglong
																														// 20140307
		} else {
			// ����ϲ��Ǵ�Ⱦ��
			this.messageBox("����ϲ��Ǵ�Ⱦ��");
		}
	}

	/**
	 * ���ô�Ⱦ�����濨
	 * 
	 * @param mr_no
	 *            String
	 * @param case_no
	 *            String
	 * @param ipd_no
	 *            String
	 * @param icd_code
	 *            String
	 */
	public void onTransferMROInfect(String mr_no, String case_no, String ipd_no, String icd_code) {// modify by wanglong
																									// 20140311
		TParm can = new TParm();
		can.setData("MR_NO", mr_no);
		can.setData("CASE_NO", case_no);
		can.setData("IPD_NO", ipd_no);
		can.setData("ICD_CODE", icd_code);
		can.setData("DEPT_CODE", Operator.getDept());
		can.setData("USER_NAME", Operator.getName());
		this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
	}

	/**
	 * ���(CLNDIAG_TABLE)�����¼�
	 */
	public void onTableClicked() {
		String rule_type = parm.getValue("RULE_TYPE");
		TTable table = getTable("CLNDIAG_TABLE");
		int row = table.getSelectedRow();
		String io_type = (String) table.getValueAt(row, 0);
		// �������ֵ��ýӿڣ�����ɾ����ť��״̬
		if (rule_type.equals("I")) {
			if (io_type.equals("M") || io_type.equals("O") || io_type.equals("W") || io_type.equals("Q")
					|| io_type.equals("Z"))// �����������ɾ�� 20170706 machao
				callFunction("UI|delete|setEnabled", true);
			else
				callFunction("UI|delete|setEnabled", false);
		} else {
			if (io_type.equals("I"))
				callFunction("UI|delete|setEnabled", true);
			else
				callFunction("UI|delete|setEnabled", false);
		}
		// ����ICD_TYPE��ICD_CODE�ж��Ƿ�Ϊ��Ⱦ��
		String icd_type = "W";
		if (!getRadioButton("RadioButton1").isSelected())
			icd_type = "C";
		String icd_code = (String) table.getValueAt(row, 2);
		String sql = "SELECT CHLR_FLG FROM SYS_DIAGNOSIS";
		sql += " WHERE ICD_TYPE = '" + icd_type + "' AND ICD_CODE = '" + icd_code + "'";
		TDataStore ds = new TDataStore();
		ds.setSQL(sql);
		ds.retrieve();
		String chlr_flg = ds.getItemString(0, "CHLR_FLG");
		if ("Y".equals(chlr_flg))
			callFunction("UI|card|setEnabled", true);
		else
			callFunction("UI|card|setEnabled", false);
	}

	/**
	 * �������(RadioButton)�ı��¼�
	 */
	public void onChangeICD_TYPE() {
		// ��ҽ���Ϊ true����ҽ���Ϊ false
		boolean icd_type = getRadioButton("RadioButton1").isSelected();
		if (icd_type) {
			ioType = "ICD_TYPE = 'W'";
		} else {
			ioType = "ICD_TYPE = 'C'";
		}
		showAdm_InpDiag(onlyCaseNo);
		// ���һ��������
		insertRow();
		autoHeight();
	}

	/**
	 * ���ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableDChangeValue(Object obj) {
		// ֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// �ж����ݸı�
		// if (node.getValue().equals(node.getOldValue()))
		// return false;
		// Table������
		TTable table = node.getTable();
		String columnName = table.getDataStoreColumnName(node.getColumn());
		int row = node.getRow();
		if ("IO_TYPE".equals(columnName)) {
			// �ж��Ƿ���������
			String io_type = (String) node.getValue();
			// System.out.println("====io_type====="+io_type);
			boolean flg = false;
			for (int i = 0; i < table.getRowCount(); i++) {
				if (i != row) {
					if (io_type.equals(table.getItemString(i, "IO_TYPE"))) {
						if ("Y".equals(table.getItemString(i, "MAINDIAG_FLG")))
							flg = true;
					}
				}
			}
			// System.out.println("====flg====="+flg);
			// �����������
			if (!flg) {
				table.setItem(row, "MAINDIAG_FLG", "Y");
			} else {
				table.setItem(row, "MAINDIAG_FLG", "N");
			}

		}
		if ("MAINDIAG_FLG".equals(columnName)) {
			// �ж��Ƿ���������
			String mianFlg = (String) node.getValue();
			String ioType = table.getItemString(row, "IO_TYPE");
			boolean flg = false;
			for (int i = 0; i < table.getRowCount(); i++) {
				if (i != row) {
					if (mianFlg.equals(table.getItemString(i, "MAINDIAG_FLG"))) {
						if (ioType.equals(table.getItemString(i, "IO_TYPE")))
							flg = true;
					}
				}
			}
			// �����������
			if (!flg) {
				table.setItem(row, "MAINDIAG_FLG", "Y");
			} else {
				this.messageBox("ͬһ�����,ֻ��һ�������");
				table.setItem(row, "MAINDIAG_FLG", "N");
			}

		}

		/**
		 * if ("Y".equals(table.getDataStore().getItemString(row, "MAINDIAG_FLG"))) {
		 * boolean flg = true; for (int i = 0; i < table .getRowCount(); i++) { if (i ==
		 * row) continue; if ("Y".equals(table.getItemString(i, "MAINDIAG_FLG"))) flg =
		 * false; } if (!flg) { // ͬһ�����������ֻ��һ�� this.messageBox("ͬһ�����������ֻ��һ��");
		 * 
		 * table.setItem(row, "MAINDIAG_FLG", "N"); return false; } } return false;
		 **/
		// }
		return false;
	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ����
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentUD(Component com, int row, int column) {
		if (column != 2) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		String icd_type = "";
		if (getRadioButton("RadioButton1").isSelected())
			icd_type = "W";
		else
			icd_type = "C";
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", icd_type);
		// modified by WangQing 20170227
		// ADM_FLG:�ż���|סԺ
		// סԺ���ܿ����Զ������
		parm.setData("ADM_FLG", "סԺ");
		if (icd_type.equals("W")) {
			parm.setData("ICD_EXCLUDE", "Y");
			parm.setData("ICD_MIN_EX", "M80000/0");
			parm.setData("ICD_MAX_EX", "M99890/1");
			parm.setData("ICD_START_EX", "V");
			parm.setData("ICD_END_EX", "Y");
			parm.setData("ICD_MIC_FLG", false);// add by wanglong 20140321 ������̬ѧ���ע��
		}
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// System.out.println("�����Ϣ"+parm);
		// System.out.println("�����Ա�"+sexCode);
		// System.out.println("��������"+birthday);
		// System.out.println("��԰��"+inDate);
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		String ageStr = (StringUtil.showAge(birthday, inDate)).split("��")[0];
		// System.out.println("����"+ageStr);
		int age = TypeTool.getInt(ageStr);
		if (parm.getValue("LIMIT_SEX_CODE").length() > 0) {
			if (!sexCode.equals(parm.getValue("LIMIT_SEX_CODE"))) {
				this.messageBox("����Ա𲻷�");
				return;
			}
		}
		if (!(parm.getInt("START_AGE") == 0 && parm.getInt("END_AGE") == 0)) {
			// System.out.println("11111111111111111"+parm.getInt("START_AGE")+"jieshu"+parm.getInt("END_AGE"));
			if (!(parm.getInt("START_AGE") <= age && age <= parm.getInt("END_AGE"))) {
				this.messageBox("������䲻��");
				return;
			}
		}
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		String icd_code = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icd_code)) {
			table.setItem(table.getSelectedRow(), "ICD_DESC", orderDesc.getTableShowValue(icd_code));
			table.setItem(table.getSelectedRow(), "ICD_CODE", icd_code);
		}
		table.getTable().grabFocus(); // chenxi modify 20130322 ��ϻ�ȡ���㣬���Իس��������
		// ���һ��������
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			insertRow();
			autoHeight();
		}
	}

	/**
	 * �����Ϣ���(CLNDIAGTABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onClnDiagTableCheckBoxClicked(Object obj) {
		// this.messageBox("come in");
		TTable tableDown = (TTable) obj;
		tableDown.acceptText();
		int row = tableDown.getSelectedRow();
		String io_type = tableDown.getItemString(row, "IO_TYPE");
		if ("".equals(tableDown.getItemString(row, "ICD_CODE"))) {
			tableDown.setItem(row, "MAINDIAG_FLG", "N");
			// ���������
			this.messageBox("���������");
			return false;
		}

		//
		boolean flg = false;
		for (int i = 0; i < tableDown.getRowCount(); i++) {
			if (i == row)
				continue;
			if (io_type.equals(tableDown.getItemString(i, "IO_TYPE"))) {
				if ("Y".equals(tableDown.getItemString(i, "MAINDIAG_FLG")))
					flg = true;
			}
		}
		// ���������
		if (flg) {
			/**
			 * ----- duzhw 20131210 modify start ----- // $$ add by lx 2012/3/13����ͬ����������óɲ�ѡ
			 * for (int i = 0; i < tableDown.getRowCount(); i++) { if (i != row) { if
			 * (io_type.equals(tableDown.getItemString(i, "IO_TYPE"))) {
			 * tableDown.setItem(i, "MAINDIAG_FLG", "N"); } } } // $$ return false;
			 **/
			tableDown.setItem(row, "MAINDIAG_FLG", "N");
			// ----- duzhw 20131210 modify end -----
			// �����������
		} else {
			tableDown.setItem(row, "MAINDIAG_FLG", "Y");
		}
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TParm parm = new TParm();
		parm.setData("CASE_NO", "1");
		parm.setData("IPD_NO", "0000002");
		parm.setData("MR_NO", "0000001");
		// RULE_TYPE-> I:סԺҽ����O:����ҽ����E:����ҽ��
		parm.setData("RULE_TYPE", "I");
		JavaHisDebug.runFrame("odi\\ODIClnDiagUI.x", parm);
	}

	/**
	 * ��ʾ�����������Ϣ
	 * 
	 * @param case_no
	 *            String
	 */
	public void showAdm_InpDiag(String case_no) {
		// System.out.println("��ʾ���������Ϣ----------");
		TTable table = getTable("CLNDIAG_TABLE");
		table.setLockRows("");// �Ƚ���-duzhw add 20140409
		String sql = "SELECT 'Y' AS EXEC,CASE_NO,IO_TYPE,ICD_CODE,MAINDIAG_FLG,ICD_TYPE,"
				+ "SEQ_NO,MR_NO,IPD_NO,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM " + " FROM ADM_INPDIAG WHERE 1=1 ";
		if (!case_no.equals("")) {
			sql += " AND CASE_NO = '" + case_no + "' ";
		}
		if (!this.ioType.equals("")) {
			sql += " AND " + ioType;
		}
		sql += "ORDER BY IO_TYPE ,MAINDIAG_FLG DESC , SEQ_NO";
		// System.out.println("���sql->"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm parm = result.getRow(i);
			String icdDesc = this.getICDCode(parm.getValue("ICD_CODE"));
			result.setData("ICD_DESC", i, icdDesc);
		}
		// ��ѯ�������������Ҫ�������������޸�--duzhw add 20131010
		StringBuffer lockRow = new StringBuffer();
		for (int j = 0; j < result.getCount(); j++) {
			if (j == 0) {
				lockRow.append(Integer.toString(j));
			} else {
				lockRow.append(",").append(Integer.toString(j));
			}
		}
		// System.out.println("lockRow="+lockRow.toString());
		table.setLockRows(lockRow.toString());// �����У��������޸�

		table.setParmValue(result);

	}

	/**
	 * ����ָ����
	 * 
	 * @param rule_type
	 *            String
	 */
	public void LockDiagRow(String rule_type) {
		TTable table = getTable("CLNDIAG_TABLE");
		String lockRows = "";
		if (rule_type.equals("I")) {
			for (int i = 0; i < table.getRowCount(); i++) {
				String type = (String) table.getValueAt(i, 0);
				if ("I".equals(type))
					lockRows += i + ",";
			}
		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				String type = (String) table.getValueAt(i, 0);
				if (!"I".equals(type))
					lockRows += i + ",";
			}
		}
		if (lockRows.length() > 0) {
			lockRows = lockRows.substring(0, lockRows.length() - 1);
		}
		table.setLockRows(lockRows);
	}

	/**
	 * ���һ��������
	 */
	public void insertRow() {
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		String io_type = "";
		if (table.getRowCount() > 0) {// modify by wanglong 20140404
			int oldrow = table.getRowCount() - 1;
			io_type = table.getItemString(oldrow, "IO_TYPE");
		}
		String rule_type = parm.getValue("RULE_TYPE");
		String type = "I";
		int row = table.addRow();
		if ("I".equals(rule_type)) {
			if ("I".equals(io_type))
				type = "M";
			if ("M".equals(io_type))
				type = "M";
			if ("O".equals(io_type))
				type = "O";
		}
		table.setItem(row, "IO_TYPE", type);
		table.setItem(row, "MAINDIAG_FLG", "N");
		table.setItem(row, "OPT_USER", Operator.getID());
		Timestamp date = StringTool.getTimestamp(new Date());
		table.setItem(row, "OPT_DATE", date);
		table.setItem(row, "OPT_TERM", Operator.getIP());
	}

	/**
	 * ����������(��ҽ����ҽ���)
	 * 
	 * @return String
	 */
	public String getICDType() {
		this.getTable("CLNDIAG_TABLE").acceptText();
		return this.getRadioButton("RadioButton1").isSelected() ? "W" : "C";
	}

	/**
	 * �ⲿʹ�ã���Ӷ���������
	 * 
	 * @param list
	 *            List
	 */
	public void insertRow(List list) {
		TParm parm = new TParm();
		OrderList orderDesc = new OrderList();
		TTable table = this.getTable("CLNDIAG_TABLE");
		int last_row = table.getRowCount() - 1;
		for (int i = 0; i < list.size(); i++) {
			parm = (TParm) list.get(i);
			table.setItem(last_row, "ICD_CODE", parm.getValue("ICD_CODE"));
			table.setItem(last_row, "ICD_DESC", orderDesc.getTableShowValue(parm.getValue("ICD_CODE")));
			insertRow();
			last_row++;
		}
		autoHeight();
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
	 * ��õ�ѡ��ť����
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getRadioButton(String tag) {
		return (TRadioButton) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * ���ÿ����
	 */
	public void onDeptCln() {
		// �ж�����ҽ������ҽ
		String param = "1," + Operator.getDept() + "," + getICDType();
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		insertRow(diagList);
	}

	/**
	 * ����ҽʦ���
	 */
	public void onDrCln() {
		// �ж�����ҽ������ҽ
		String param = "2," + Operator.getDept() + "," + getICDType();
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ICD_CODE");
		List diagList = new ArrayList();
		for (int i = 0; i < rowCount; i++) {
			TParm diagParm = new TParm();
			diagParm = OdiUtil.getInstance().getDiagNosis(result.getValue("ICD_CODE", i));
			diagList.add(diagParm);
		}
		insertRow(diagList);
	}

	/**
	 * �õ�SYS_DIAGNOSIS���
	 * 
	 * @param icdCode
	 *            String
	 * @return String
	 */
	public synchronized String getICDCode(String icdCode) {
		TDataStore d = com.dongyang.manager.TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
		this.code = icdCode;
		d.filterObject(this, "filter");
		if (d.rowCount() == 0)
			return null;
		if ("en".equals(this.getLanguage())) {
			return d.getRowParm(0).getValue("ICD_ENG_DESC");
		} else {
			return d.getRowParm(0).getValue("ICD_CHN_DESC");
		}
	}

	/**
	 * ���˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filter(TParm parm, int row) {
		return parm.getValue("ICD_CODE", row).equals(this.code);
	}

	/**
	 * 
	 * <p>
	 * Title:
	 * </p>
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2008
	 * </p>
	 * 
	 * <p>
	 * Company:
	 * </p>
	 * 
	 * @author not attributable
	 * @version 1.0
	 */
	public class ODIIcdCode extends TObserverAdapter {
		/**
		 * �õ�����������
		 * 
		 * @param ds
		 *            TDS
		 * @param parm
		 *            TParm
		 * @param row
		 *            int
		 * @param column
		 *            String
		 * @return Object
		 */
		public Object getOtherColumnValue(TDS ds, TParm parm, int row, String column) {
			if ("ICD_DESC".equals(column)) {
				// System.out.println("---"+parm.getValue("ICD_CODE", row));
				return getICDCode(parm.getValue("ICD_CODE", row));
			}
			return "";
		}
	}

	/**
	 * �ж��Ƿ���������
	 * 
	 * @param ioType
	 * @param table
	 * @return
	 */
	/**
	 * private boolean isExistMainDiag(String ioType, TTable table) { boolean flg =
	 * false; for (int i = 0; i < table .getRowCount(); i++) { String theIoType =
	 * table.getDataStore().getItemString(i, "IO_TYPE"); String theMainFlg =
	 * table.getDataStore().getItemString(i, "MAINDIAG_FLG"); // ͬ����ϣ���������� if
	 * (theIoType.equals(ioType) && theMainFlg.equals("Y")) { flg = true; break; }
	 * 
	 * } return flg;
	 * 
	 * }
	 **/

	private void autoHeight() {
		// $$============ add by lx 2012/03/05
		// ����ص����޷�������ϵ�����start====================$$//
		TTable table = getTable("CLNDIAG_TABLE");
		table.acceptText();
		TFrame f = ((TFrame) this.getComponent("UI"));
		if (table.getRowCount() >= 4) {
			int height = 496 + (table.getRowCount() * 20);
			f.setHeight(height);
			f.repaint();
		}
		// $$============ add by lx 2012/03/05
		// ����ص����޷�������ϵ�����END====================$$//
	}

	/**
	 * ��ʾ�����������Ϣ
	 * 
	 * @param case_no
	 *            String duzhw add 20131016
	 */
	public TParm getAdm_InpDiag(String case_no) {
		TTable table = getTable("CLNDIAG_TABLE");
		String sql = "SELECT 'Y' AS EXEC,CASE_NO,IO_TYPE,ICD_CODE,MAINDIAG_FLG,ICD_TYPE,"
				+ "SEQ_NO,MR_NO,IPD_NO,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM " + " FROM ADM_INPDIAG WHERE 1=1 ";
		if (!case_no.equals("")) {
			sql += " AND CASE_NO = '" + case_no + "' ";
		}
		if (!this.ioType.equals("")) {
			sql += " AND " + ioType;
		}
		sql += "ORDER BY IO_TYPE ,MAINDIAG_FLG DESC , SEQ_NO";
		// System.out.println("���sql->"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			table.removeRowAll();
			return result;
		}

		return result;

	}
}
