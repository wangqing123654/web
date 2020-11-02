package com.javahis.ui.ins;

import java.awt.Color;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.ins.INSCheckAccountTool;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 
 * Title: �������
 * 
 * Description:������ˣ��Զ����� �ܶ��� ��ϸ���� ����ȷ��
 * 
 * Copyright: BlueCore (c) 2011
 * 
 * @author pangben 2012-1-8
 * @version 2.0
 */
public class INSCheckAccountControl extends TControl {
	// private boolean exeProgress=false;//ִ���Զ�����
	private String[] company = {"OPT_USER", "ACCOUNT_DATE", "INS_CROWD_TYPE",
			"INS_PAT_TYPE", "CONFIRM_NO" };
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	private TTable tableOpd;// �����˱�
	private TParm checkOutParm = new TParm();// У������ѡ�е�����
	private TParm opdParm;// �ܶ�������
	private TTabbedPane tabPanel;// ҳǩ
	private TTable tableOpdOrder;// ����ϸ�ʱ�
	// private String[] opdTableMap = { "FLG", "CASE_NO", "CONFIRM_NO", "MR_NO",
	// "PAT_NAME", "SEX_CODE", "RECP_TYPE", "INV_NO", "INSAMT_FLG",
	// "REGION_CODE" };// ���˱�������ʾֵ
	private TParm checkOutOpbParm = new TParm();// ��ϸ��ѯ����ʹ�û�õ�ǰ����������
	private boolean flg = false;// �жϴ˲����Ƿ�֮ǰ�����ݹ�ѡȥ��
	private TTable tableRule;// ��ϸ������ʾ����
	private TParm opdOrderParm;// ��ϸ������
	private TTable tableAccount;// �������
	Color red = new Color(255, 0, 0); // ��ϸ���ز�ͬ������ɫ
	private TParm regionParm;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		this.callFunction("UI|ACCOUNT_DATE|setValue", SystemTool.getInstance()
				.getDate());
		tableOpd = (TTable) this.getComponent("TABLE_OPD");// �ܶ���
		tabPanel = (TTabbedPane) this.getComponent("TTABPANEL");// ҳǩ
		tableOpdOrder = (TTable) this.getComponent("TABLE_OPD_ORDER");// ��ϸ����
		tableRule = (TTable) this.getComponent("TABLE_RULE");// ��ϸ��������
		// �ܶ��˸�ѡ���¼�
		tableOpd.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// ��ϸ���˸�ѡ���¼�
		tableOpdOrder.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tableAccount = (TTable) this.getComponent("TABLE_ACCOUNT");// �������
		regionParm = SYSRegionTool.getInstance().selectdata(
	                Operator.getRegion()); // ���ҽ���������
		//this.setValue("USER_ID", Operator.getID());
		this.setValue("INS_CROWD_TYPE", 1);
		this.setValue("INS_PAT_TYPE", 1);
	}

	/**
	 * �Զ�����
	 */
	public void onAutoSave() {
		// ��Ҫ�Զ����˵�����
//		TParm insOpdParm = INSOpdTJTool.getInstance().selectAutoAccount(
//				new TParm());
//		if (insOpdParm.getErrCode() < 0) {
//			this.messageBox("����ʧ��");
//			// exeProgress=true;
//			return;
//		}
//		if (insOpdParm.getCount() <= 0) {
//			this.messageBox("û����Ҫ�Զ����˵�����");
//			// exeProgress=true;
//			return;
//		}
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSChooseVisit.x", new TParm());
//		if (getValue("ACCOUNT_DATE") == null
//				|| getValueString("ACCOUNT_DATE").length() <= 0) {
//			messageBox("��ѡ�����ʱ��!");
//			return;
//		}
//		// ��������
//		if (!this.emptyTextCheck("INS_CROWD_TYPE,INS_PAT_TYPE")) {
//			return;
//		}
		
//		int insType = getInsType();
//		insOpdParm.setData("INS_TYPE", insType);
//		insOpdParm.setData("ACCOUNT_DATE", getValue("ACCOUNT_DATE"));// ����ʱ��
//		insOpdParm.setData("OPT_USER", Operator.getID());// ������Ա
//		insOpdParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO",0));// ������Ա
//		// insOpdParm
//		// .setData("REGION_CODE", insOpdParm.getValue("REGION_CODE", 0));//
//		// ������Ա
//		parm = new TParm(INSTJReg.getInstance().insOPBAutoAccount(
//				insOpdParm.getData()));
//		if (parm.getErrCode() < 0) {
//			this.messageBox(parm.getErrText());
//			return;
//		}
		//this.messageBox("�Զ����˳ɹ�");
	}

	/**
	 * ��þ������
	 * 
	 * @return
	 */
	private int getInsType() {
		int insType = -1;
		if (this.getValueInt("INS_CROWD_TYPE") == 1
				&& this.getValueInt("INS_PAT_TYPE") == 1) {
			insType = 1;// ��ְ��ͨ
		} else if (this.getValueInt("INS_CROWD_TYPE") == 1
				&& this.getValueInt("INS_PAT_TYPE") == 2) {
			insType = 2;// ��ְ����
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2
				&& this.getValueInt("INS_PAT_TYPE") == 2) {
			insType = 3;// �Ǿ�����
		}
		return insType;
	}

	/**
	 * ��ѯ����������
	 */
	public void onQueryOpd() {
//		if (!checkAutoAccount())
//			return;
		if (!checkOut()) {
			return;
		}
		TParm parm = checkOutParm();
		parm.setData("INSAMT_FLG", 3);// ���˱�־
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));
		TParm result =INSOpdTJTool.getInstance()
				.selectMrNoAccount(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			tableOpd.removeRowAll();
			((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// ��һ��ҳǩȫѡ
			return;
		}
		//System.out.println("result::"+result);
		
		tableOpd.setParmValue(result);
	}

	/**
	 * У���Ƿ��Ѿ��Զ�����
	 * 
	 * @return
	 */
	private boolean checkAutoAccount() {
		// ��Ҫ�Զ����˵�����
		TParm parm=new TParm();
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));
		TParm insOpdParm = INSOpdTJTool.getInstance().selectAutoAccount(
				parm);
		if (insOpdParm.getErrCode() < 0) {
			this.messageBox("E0005");
			// exeProgress=true;
			return false;
		}
		if (insOpdParm.getCount() > 0) {
			this.messageBox("�û�����δ���ʵ����Ѽ�¼");
			// insOpdParm.setErr(-1, "��ִ���Զ�����");
			return false;
		}
		return true;

	}

	/**
	 * У���ѯ����
	 */
	private TParm checkOutParm() {
		TParm parm = new TParm();
		for (int i = 0; i < company.length; i++) {
			if (this.getValueString(company[i]).length() > 0) {
				parm.setData(company[i], this.getValueString(company[i]));
			}
		}

		return parm;
	}

	/**
	 * INS_CROWD_TYPE combox У��
	 */
	public void onSelType() {
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {
			this.setValue("INS_PAT_TYPE", 1);
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			this.setValue("INS_PAT_TYPE", 2);
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
		}
	}

	/**
	 * ������ȫѡ����
	 */
	public void onSelectAllOpd() {
		boolean flg = false;// �ж��Ƿ�ѡ��
		if (this.getValueBoolean("SELECT_ALL")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = tableOpd.getParmValue();// �����������
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		tableOpd.setParmValue(parm);
	}

	/**
	 * ����ϸ��ȫѡ����
	 */
	public void onSelectAllOpdOrder() {
		boolean flg = false;// �ж��Ƿ�ѡ��
		if (this.getValueBoolean("SELECT_ALL_O")) {
			flg = true;
		} else {
			flg = false;
		}
		TParm parm = tableOpdOrder.getParmValue();// �����������
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		tableOpdOrder.setParmValue(parm);
	}
	/**
	 * �ж��Ƿ���Զ���
	 * @return
	 */
	private boolean getAccount(){
		Date date =new Date();
		String time=date.getHours()+""+date.getMinutes();
		//StringTool.getDate(, "HHmm");
		if (time.compareTo("1500")<0) {
			this.messageBox("�����������Ժ���ж��˲���");
			return false;
		}
		return true;
	}
	/**
	 * ����������
	 */
	public void onOpdLoadDown() {
		if (!getAccount()) {
			return;
		}
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpd)) {
			if (checkOutExe(tableOpd)) {// У���Ƿ����ִ��
				int insType = getInsType();
				checkOutParm.setData("INS_TYPE", insType);// ҽ���������
				checkOutParm.setData("ACCOUNT_DATE", df1
						.format(getValue("ACCOUNT_DATE")));// ����ʱ��
				if (null!=this.getValue("OPT_USER")&&this.getValue("OPT_USER").toString().length()>0) {
					checkOutParm.setData("USER_ID", this.getValue("OPT_USER"));// ������Ա
				}
				checkOutParm.setData("PAT_TYPE", this
						.getValueInt("INS_CROWD_TYPE"));// ��Ա���

				checkOutParm.setData("REGION_CODE", regionParm.getValue("NHI_NO",0));// ������Ա
				if (insType == 1) {
					checkOutParm.setData("PAY_KIND", "11");// ֧�����PAY_KIND
				} else if (insType == 2) {
					checkOutParm.setData("PAY_KIND", "12");// ֧�����PAY_KIND
				} else if (insType == 3) {
					checkOutParm.setData("PAY_KIND", "22");// ֧�����PAY_KIND
				}
				TParm temParm =checkOutParm();
				temParm.setData("INSAMT_FLG", 3);// ���˱�־
				temParm.setData("START_DATE", StringTool.setTime((Timestamp) this
						.getValue("ACCOUNT_DATE"), "00:00:00"));
				temParm.setData("END_DATE", StringTool.setTime((Timestamp) this
						.getValue("ACCOUNT_DATE"), "23:59:59"));
				TParm result = INSOpdTJTool.getInstance().selectSpecialPatAccount(temParm);
				if (result.getErrCode() < 0) {
					this.messageBox("E0005");
					return;
				}
				checkOutParm.setData("specialParm",result.getData());
				opdParm = new TParm(INSTJReg.getInstance().insOpdAccount(
						checkOutParm.getData()));
				if (opdParm.getErrCode() < 0) {
					this.messageBox(opdParm.getErrText());
				} else {
					onSaveOpd();
					//this.messageBox("P0005");
				}
			} else {
				this.messageBox("�������ݲ�����ִ��");
			}
		} else {
			this.messageBox("��ѡ������");
		}
	}

	/**
	 * У��ѡ�е������Ƿ����ִ�� �����Ҫִ�е����� ����Ʊ�ݲ���ͬ �ж��Ƿ�ѡ���� ���е����� ͨ��CAES_NO ���� �����Կ��˲���
	 * 
	 * @return
	 */
	private boolean checkOutExe(TTable table) {
		TParm parm = table.getParmValue();
		// �ж�ѡ�е�������һ��CASE_NO���Ƕ����CASE_NO
		StringBuffer caseNoTemp = new StringBuffer();
		for (int index = 0; index < checkOutParm.getCount("CASE_NO"); index++) {
			if (!caseNoTemp.toString().contains(
					checkOutParm.getValue("CASE_NO", index))) {
				caseNoTemp
						.append(checkOutParm.getValue("CASE_NO", index) + ",");
			}
		}
		String[] caseNos = caseNoTemp.toString().split(",");// caseNo �ĸ���
		if (caseNos.length <= 0) {
			this.messageBox("û����Ҫִ�е�����");
			return false;
		}
		if (caseNos.length <= 1) {
			return true;
		} else {
			// У��ÿһ��CASE_NO �Ƿ�ȫ��ѡ��
			for (int i = 0; i < caseNos.length; i++) {
				int checkOutCount = 0;// ѡ�е�����
				int tableCount = 0;// ����е�����
				for (int j = 0; j < checkOutParm.getCount("CASE_NO"); j++) {
					if (caseNos[i].equals(checkOutParm.getValue("CASE_NO", j))) {
						checkOutCount++;// �ۼ�ѡ�е����ݸ���
					}
				}
				for (int j = 0; j < parm.getCount(); j++) {
					if (caseNos[i].equals(parm.getValue("CASE_NO", j))) {
						tableCount++;// �ۼƱ���и���
					}
				}
				if (checkOutCount == tableCount) {
					continue;
				} else {
					return false;
				}

			}
			return true;

		}

	}

	/**
	 * У���Ƿ���ѡ�е�����
	 * 
	 * @param table
	 * @return
	 */
	private boolean checkOutOnSelected(TTable table) {
		table.acceptText();
		TParm parm = table.getParmValue();
		checkOutParm = new TParm();
		boolean flg = false;// У���Ƿ���ѡ�е�����
		// if (parm.getCount() <= 0) {
		// this.messageBox("û����Ҫ���ص�����");
		// return false;
		// }
		// TParm tempParm =parm;
		if (parm==null) {
			return false;
		}
		int index = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {
				flg = true;
				checkOutParm.setRowData(index, parm, i);
				// tempParm.removeRow(i);
				// checkOutParmValue(parm, i);// ��ҪУ���Ƿ���˲���
				index++;
				// //System.out.println("checkOutParm!!111111::::"+checkOutParm);
			}
		}
		if (flg) {
			checkOutParm.setCount(index);
			if (tabPanel.getSelectedIndex() == 1) {// �˷�����Ҫ����
				// �����ǰ�ǵ�һ��ҳǩ����õ����ݱ���
				checkOutOpbParm = parm;
			} else {
				checkOutOpbParm = new TParm();
			}
		}

		return flg;
	}

	// /**
	// * У��ѡ�����ݵĸ���
	// *
	// * @param parm
	// * @param i
	// */
	// private void checkOutParmValue(TParm parm, int i) {
	// for (int j = 0; j < opdTableMap.length; j++) {
	// checkOutParm.addData(opdTableMap[j], parm.getValue(opdTableMap[j],
	// i));
	// }
	// }

	/**
	 * ��������˼�¼
	 */
	public void onSaveOpd() {
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpd)) {
			if (checkOutExe(tableOpd)) {// У���Ƿ����ִ��
				if (null == opdParm || opdParm.getInt("INSAMT_FLG") != 5
						|| opdParm.getErrCode() < 0) {
					this.messageBox("δ����,�����Ա���");
					return;
				}
				if (opdParm.getInt("INSAMT_FLG") == 5) {// ���������
					TParm result = new TParm(INSTJReg.getInstance()
							.insOpdAccountSave(checkOutParm.getData()));
					if (result.getErrCode() < 0) {
						this.messageBox("���˱���ʧ��");
					} else {
						this.messageBox("���˳ɹ�");
						onQueryOpd();// ��ѯ
					}
				} else {
					this.messageBox("δ����,�����Ա���");
				}

			} else {
				this.messageBox("�������ݲ�����ִ��");
			}
		} else {
			this.messageBox("��ѡ������");
		}
	}

	/**
	 * ������ϸ����
	 */
	public void onSaveOpdOrder() {
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpdOrder)) {
			if (checkOutExe(tableOpdOrder)) {// У���Ƿ����ִ��
				if (null == opdOrderParm || opdOrderParm.getErrCode() < 0) {
					this.messageBox("δ����,�����Ա���");
				} else {
					TParm result = new TParm(INSTJReg.getInstance()
							.insOpdOrderAccountSave(checkOutParm.getData()));
					if (result.getErrCode() < 0) {
						this.messageBox("���˱���ʧ��");
					} else {
						this.messageBox("����ϸ�˳ɹ�");
						onQueryOpdOrder();// ��ѯ
						tableRule.removeRowAll();
					}
				}

			} else {
				this.messageBox("�������ݲ�����ִ��");
			}
		} else {
			this.messageBox("��ѡ������");
		}
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("USER_ID;CONFIRM_NO");
		tableOpd.removeRowAll();// �����˱�
		onClearOpdOrder();
		this.setValue("INS_CROWD_TYPE", 1);// ҽ����ҽ���
		this.setValue("INS_PAT_TYPE", 1);
		callFunction("UI|INS_CROWD_TYPE|setEnabled", true);
		callFunction("UI|INS_PAT_TYPE|setEnabled", true);
		tabPanel.setSelectedIndex(0);// ҳǩ
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// ��һ��ҳǩȫѡ
		checkOutParm = null;// ѡ������
		opdParm = null;// �ܶ�����������
		opdOrderParm = null;// ��ϸ����������
		tableAccount.removeRowAll();// �������
		checkOutOpbParm = new TParm();
	}

	/**
	 * У�������Ƿ����ִ��
	 */
	public void checkOutOpdOrder() {
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpd)) {
			if (checkOutExe(tableOpd)) {// У���Ƿ����ִ��
				onQueryOpdOrder(checkOutParm, false);
			} else {
				tabPanel.setSelectedIndex(0);
				this.messageBox("�������ݲ�����ִ��");
			}
		} else {
			tabPanel.setSelectedIndex(0);
			this.messageBox("��ѡ������");
		}
	}

	/**
	 * ��ϸ�ʲ�ѯ�¼�
	 */
	public void onQueryOpdOrder() {
//		if (!checkAutoAccount())
//			return;
		onQueryOpdOrder(checkOutOpbParm, true);
	}

	/**
	 * ��ѯ����ϸ������
	 */
	private void onQueryOpdOrder(TParm parm, boolean flg) {
		TParm opdOrderParm = new TParm();
		if (null == parm) {
			return;
		}
		if (flg) {// ��ѡ��ѯ��ť�ܿ� �Ƴ� ��ͬ������
			parm = removeOpdOrderParm(parm);
			if (parm.getCount("CASE_NO") <= 0) {
				this.messageBox("û�в�ѯ������");
				initShow();
				return;
			}
		}
		//System.out.println("��ѯ��ϸ��������ʾ����:::" + parm);
		int index = 0;
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);
			tempParm.setData("INSAMT_FLG", 3);
			tempParm = INSOpdOrderTJTool.getInstance().selectOpdOrderAccount(
					tempParm);
			if (tempParm.getErrCode() < 0) {
				this.messageBox("��ѯ��ϸ����ʧ��");
				return;
			}
			// ����ѯ�����ݷ���TParm ��
			for (int j = 0; j < tempParm.getCount(); j++) {
				opdOrderParm.setRowData(index, tempParm, j);
				index++;
			}
		}
		opdOrderParm.setCount(index);
		// opdOrderParm.setCount(index+1);
		if (opdOrderParm.getCount("CASE_NO") <= 0) {
			this.messageBox("û�в�ѯ������");
			initShow();
			return;
		}
		tableOpdOrder.setParmValue(opdOrderParm);
	}

	/**
	 * û�����ݳ�ʼ����
	 */
	private void initShow() {
		checkOutOpbParm = new TParm();
		tabPanel.setSelectedIndex(0);
		tableOpdOrder.removeRowAll();
	}

	/**
	 * �Ƴ���ͬ������ ���� RECP_TYPE ��ϸ����ʹ��
	 * 
	 * @param parm
	 */
	private TParm removeOpdOrderParm(TParm parm) {
		// StringBuffer recpType = new StringBuffer();
		// recpType.append(parm.getValue("RECP_TYPE", 0)+",");
		TParm tempParm = new TParm();
		int index = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 0; j < checkOutParm.getCount(); j++) {
				if (i != getSelectedRow(parm, checkOutParm.getValue("CASE_NO",
						j), checkOutParm.getValue("CONFIRM_NO", j),
						checkOutParm.getValue("SEQ_NO", j))) {
					tempParm.setRowData(index, parm, i);
				}
			}
		}
		tempParm.setCount(index);
		return tempParm;
	}

	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {

		switch (tabPanel.getSelectedIndex()) {
		case 1:
			checkOutOpdOrder();
			break;
		case 0:
			onQueryOpd();
			break;
		}
		tableRule.removeRowAll();// ��ϸ������ʾ����
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// ��һ��ҳǩȫѡ
		// tableOpd.removeRowAll();
	}

	/**
	 * �ܶ�����ϸ��checkBox�¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		TParm oldTempParm = new TParm();
		if (tabPanel.getSelectedIndex() == 1
				|| tabPanel.getSelectedIndex() == 0) {
			TParm oldParm = table.getParmValue();
			int index = 0;
			for (int i = 0; i < oldParm.getCount(); i++) {
				if (oldParm.getBoolean("FLG", i)) {
					oldTempParm.setRowData(index, oldParm, i);
					index++;
					flg = true;
				}
			}
			oldTempParm.setCount(index);
		}
		table.acceptText();// ��������
		if (tabPanel.getSelectedIndex() == 1) {
			commManager(table, oldTempParm, 1);
			tableRule.removeRowAll();// ��ϸ������ʾ����
			opdOrderParm = null;// ��ϸ����������
		} else if (tabPanel.getSelectedIndex() == 0) {
			commManager(table, oldTempParm, 0);
		}
		return false;

	}

	/**
	 * �ڶ���ҳǩѡ��checkBox����
	 * 
	 * @param oldTempParm
	 *            �����˲�����ѡ��ѡ�� ��ִ�н�ԭ����ѡ����ɾ��
	 */
	private void commManager(TTable table, TParm oldTempParm, int type) {

		// StringBuffer caseNo = new StringBuffer();
		// �ڶ���ҳǩ����ϸ������ ��Ҫ�ܿ� �����Կ��˲��� ��CASE_NO ����
		TParm parm = table.getParmValue();
		TParm tempParm = new TParm();// ѡ�е�����
		int index = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			// �ۼ���Ҫ����������
			if (parm.getBoolean("FLG", i)) {
				tempParm.setRowData(index, parm, i);
				index++;
			}
		}
		tempParm.setCount(index);
		TParm newParm = new TParm();
		int newIndex = 0;
		if (type == 0) {// ��һ��ҳǩ����
			if (oldTempParm.getCount() < tempParm.getCount()) {
				flg = false;// ��ִ���Ѿ���ѡ������ɾ������
			} else if (oldTempParm.getCount() > tempParm.getCount()) {// ����ǰ�����������й����������Ƴ�
				boolean unFlg = false;// ��ѯ����ͬ��
				for (int i = 0; i < oldTempParm.getCount(); i++) {
					for (int j = 0; j < tempParm.getCount(); j++) {

						if (tempParm.getValue("CASE_NO", j).equals(
								oldTempParm.getValue("CASE_NO", i))
								&& tempParm.getValue("CONFIRM_NO", j).equals(
										oldTempParm.getValue("CONFIRM_NO", i))
								&& tempParm.getValue("RECP_TYPE", j).equals(
										oldTempParm.getValue("RECP_TYPE", i))) {// Ψһ�����ж�
							// ���û�е�ѡ�Ѿ���ѡ�����ݲ�ִ��ɾ������
							unFlg = false;
							break;
						}
						flg = true;
						unFlg = true;
					}
					if (unFlg) {
						newParm.setRowData(newIndex, oldTempParm, i);
						newIndex++;
					}
				}
				newParm.setCount(newIndex);
				tempParm = newParm;
			}
		}
		if (flg) {
			// ���ѡ������Ѿ���ѡ�����ݲ鿴�������Ƿ��й����ģ�������������Ҳ����û�й�ѡ״̬
			for (int i = 0; i < tempParm.getCount(); i++) {
				for (int j = 0; j < parm.getCount(); j++) {
					if (type == 0) {// �����˲���
						if (tempParm.getValue("INV_NO", i).equals(
								parm.getValue("INV_NO", j))) {
							parm.setData("FLG", j, "N");
						}
					} else if (type == 1) {// ����ϸ�ʲ���
						if (tempParm.getValue("CASE_NO", i).equals(
								parm.getValue("CASE_NO", j))
								&& tempParm.getValue("CONFIRM_NO", i).equals(
										parm.getValue("CONFIRM_NO", j))
								&& tempParm.getValue("SEQ_NO", i).equals(
										parm.getValue("SEQ_NO", j))) {

							parm.setData("FLG", j, "N");
						}
					}
				}
			}
			table.setParmValue(parm);
			flg = false;
			return;
		}
		//System.out.println("�������ݣ�����" + parm);
		for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 0; j < tempParm.getCount(); j++) {
				if (type == 0) {
					if (tempParm.getValue("INV_NO", j).equals(
							parm.getValue("INV_NO", i))) {
						parm.setData("FLG", i, "Y");
					}
				} else if (type == 1) {
					if (tempParm.getValue("INV_NO", j).equals(
							parm.getValue("INV_NO", i))
							&& tempParm.getValue("RECP_TYPE", j).equals(
									parm.getValue("RECP_TYPE", i))) {
						parm.setData("FLG", i, "Y");
					}
				}

			}
		}
		table.setParmValue(parm);
	}

	/**
	 * ���ѡ�е�����,��ϸ��ʹ��
	 * 
	 * @return
	 */
	private int getSelectedRow(TParm parm, String caseNo, String confirmNo,
			String seqNo) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (caseNo.equals(parm.getValue("CASE_NO", i))
					&& confirmNo.equals(parm.getValue("CONFIRM_NO", i))
					&& seqNo.equals(parm.getValue("SEQ_NO", i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * ��ϸ������
	 */
	public void onOpdOrderLoadDown() {
		if (!getAccount()) {
			return;
		}
//		if (!checkAutoAccount())
//			return;
		if (checkOutOnSelected(tableOpdOrder)) {
			if (checkOutExe(tableOpdOrder)) {// У���Ƿ����ִ��
				int insType = getInsType();
				checkOutParm.setData("INS_TYPE", insType);// ҽ���������
				checkOutParm.setData("ACCOUNT_DATE", df1
						.format(getValue("ACCOUNT_DATE")));// ����ʱ��
				checkOutParm.setData("OPT_USER", this.getValue("OPT_USER"));// ������Ա
				//checkOutParm.setData("PAT_TYPE", "11");// ֧�����
				checkOutParm.setData("REGION_CODE", regionParm.getValue(
						"NHI_NO", 0));// ������Ա
				opdOrderParm = INSTJReg.getInstance().insOpdOrderAccount(
						checkOutParm.getData()); // ҽ������
				// onQueryOpdOrder(checkOutParm);
				if (null == opdOrderParm || opdOrderParm.getErrCode() < 0) {
					this.messageBox(opdOrderParm.getErrText());
				} else {
					this.messageBox("P0005");
					// //System.out.println("opdOrderParm::::"+opdOrderParm);
					TParm ownParm = opdOrderParm.getParm("ownParm");// ��������
					
					String sql = "SELECT COLUMN_NAME,COLUMN_DESC FROM INS_IO WHERE PIPELINE='"
							+ opdOrderParm.getValue("PIPELINE")
							+ "' AND PLOT_TYPE='"
							+ opdOrderParm.getValue("PLOT_TYPE")
							+ "' AND IN_OUT='OUT' AND COLUMN_NAME NOT IN('PROGRAM_STATE','PROGRAM_MESSAGE') ORDER BY ID";
					TParm insIoParm = new TParm(TJDODBTool.getInstance()
							.select(sql));
					for (int i = 0; i < insIoParm.getCount(); i++) {
						insIoParm.addData("OWN_AMT", ownParm.getValue(insIoParm
								.getValue("COLUMN_NAME", i)));
						insIoParm
								.addData("INS_AMT", opdOrderParm
										.getValue(insIoParm.getValue(
												"COLUMN_NAME", i)));
					}
					tableRule.setParmValue(insIoParm);
					Map map = new HashMap();
					TParm insParm = tableRule.getParmValue();
					// ��Ӳ�ͬ������ɫ
					for (int i = 0; i < insParm.getCount(); i++) {
						if (!insParm.getValue("OWN_AMT", i).equals(
								insParm.getValue("INS_AMT", i))) {
							map.put(i, red);
						}

					}
					if (map.size() > 0) {
						tableRule.setRowTextColorMap(map);
					}
				}
				// ��������
			} else {
				// tabPanel.setSelectedIndex(0);
				this.messageBox("�������ݲ�����ִ��");
			}
		} else {
			// tabPanel.setSelectedIndex(0);
			this.messageBox("��ѡ������");
		}
	}

	/**
	 * ����ܶ�������
	 */
	public void onClearOpd() {
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// ��һ��ҳǩȫѡ
		tableOpd.removeRowAll();
	}

	/**
	 * ��ն���ϸ������
	 */
	public void onClearOpdOrder() {
		tableOpdOrder.removeRowAll();// ����ϸ�ʱ�
		tableRule.removeRowAll();// ��ϸ������ʾ����
	}

	/**
	 * ���㱣���ѯ����
	 */
	public void onQueryAccount() {
//		if (!checkAutoAccount())
//			return;
		if (!checkOut()) {
			return;
		}
		TParm parm = checkOutParm();
		// parm.setData("INSAMT_FLG", 5);// ���˱�־
		parm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));
		TParm result = INSOpdTJTool.getInstance().selectSaveAccount(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getInt("INSAMT_FLG", i) != 5) {
				this.messageBox("δִ�ж�����,��鿴");
				tableAccount.removeRowAll();
				tabPanel.setSelectedIndex(0);
				return;
			}

		}
		TParm accParm = getAccountParm(result);
		// //System.out.println("accParm:l:::"+accParm);
		tableAccount.setParmValue(accParm);
	}

	/**
	 * �޸Ľ���,��ʾ����
	 */
	private TParm getAccountParm(TParm parm) {
		int check_type = getInsType();// �������11��ְ��ͨ12��ְ����22�Ǿ�����
		int check = -1;
		switch (check_type) {
		case 1:
			check = 11;
			break;
		case 2:
			check = 12;
			break;
		case 3:
			check = 22;
			break;
		}
		double total_amt = 0.00;// TOTAL_AMT �������
		double nhi_amt = 0.00; // NHI_AMT �걨���
		double own_amt = 0.00; // �Էѽ��
		double addpay_amt = 0.00;// �������
		double account_pay_amt = 0.00;// �����ʻ�ʵ��֧�����
		double account_pay_amt_b = 0.00; // �����ʻ�ʵ��֧�����(�˷�)
		double apply_amt = 0.00; // ͳ������걨���(��������)
		double apply_amt_b = 0.00;// ͳ������籣֧�����(�˷�)
		double flg_agent_amt = 0.00;// ���������걨���(��������)
		double flg_agent_amt_b = 0.00; // ҽ�ƾ���֧�����(�˷�)
		int sum_pertime = 0; // ���˴�
		int sum_pertime_b = 0;// ���˴�(�˷�)
		double army_ai_amt = 0.00; // ���в������(��������)
		double army_ai_amt_b = 0.00;// ���в������(�����˷�)
		double servant_amt = 0.00; // ����Ա�������(��������)
		double servant_amt_b = 0.00;// ����Ա�������(�����˷�)
		double mz_agent_amt = 0.00; // ���������������
		double mz_agent_amt_b = 0.00; // ���������������(�˷�)
		double fy_agent_amt = 0.00; // �Ÿ����������
		double fy_agent_amt_b = 0.00; // �Ÿ����������(�˷�)
		double fd_agent_amt = 0.00; // �ǵ����֢�������
		double fd_agent_amt_b = 0.00;// �ǵ����֢�������(�˷�)
		double unreim_amt = 0.00; // ����δ�������
		double unreim_amt_b = 0.00;// ����δ�������(�˷�)
		double otot_amt = 0.00; // ר������籣֧��
		double otot_amt_b = 0.00;// ר������籣֧��(�˷�)
		TParm accParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			total_amt += parm.getDouble("TOT_AMT", i);
			nhi_amt += parm.getDouble("NHI_AMT", i);
			own_amt += parm.getDouble("OWN_AMT", i);
			addpay_amt += parm.getDouble("ADD_AMT", i);
			// �˷������ۼ�
			if (parm.getValue("RECP_TYPE", i).equals("REGT")
					|| parm.getValue("RECP_TYPE", i).equals("OPBT")) {
				account_pay_amt += parm.getDouble("ACCOUNT_PAY_AMT", i);
				sum_pertime += parm.getInt("SUM_PERTIME", i);
				apply_amt += parm.getDouble("APPLY_AMT", i);
				flg_agent_amt += parm.getDouble("FLG_AGENT_AMT", i);
				army_ai_amt += parm.getDouble("ARMY_AI_AMT", i);
				servant_amt += parm.getDouble("SERVANT_AMT", i);
				unreim_amt += parm.getDouble("UNREIM_AMT", i);
				otot_amt += parm.getDouble("OTOT_AMT", i);
			} else {
				account_pay_amt_b += parm.getDouble("ACCOUNT_PAY_AMT", i);
				apply_amt_b += parm.getDouble("APPLY_AMT", i);
				flg_agent_amt_b += parm.getDouble("FLG_AGENT_AMT", i);
				sum_pertime_b += parm.getInt("SUM_PERTIME", i);
				army_ai_amt_b += parm.getDouble("ARMY_AI_AMT", i);
				servant_amt_b += parm.getDouble("SERVANT_AMT", i);
				unreim_amt_b += parm.getDouble("UNREIM_AMT", i);
				otot_amt_b += parm.getDouble("OTOT_AMT", i);
			}
		}
		accParm.addData("FLG", "N");
		accParm.addData("CHECK_TYPE", check);
		accParm.addData("TOTAL_AMT", total_amt);
		accParm.addData("NHI_AMT", nhi_amt);
		accParm.addData("OWN_AMT", own_amt);
		accParm.addData("ADDPAY_AMT", addpay_amt);
		accParm.addData("ACCOUNT_PAY_AMT", account_pay_amt);
		accParm.addData("ACCOUNT_PAY_AMT_B", account_pay_amt_b);

		accParm.addData("APPLY_AMT", apply_amt);
		accParm.addData("APPLY_AMT_B", apply_amt_b);
		accParm.addData("FLG_AGENT_AMT", flg_agent_amt);
		accParm.addData("FLG_AGENT_AMT_B", flg_agent_amt_b);
		accParm.addData("SUM_PERTIME", sum_pertime);
		accParm.addData("SUM_PERTIME_B", sum_pertime_b);
		accParm.addData("ARMY_AI_AMT", army_ai_amt);
		accParm.addData("ARMY_AI_AMT_B", army_ai_amt_b);
		accParm.addData("SERVANT_AMT", servant_amt);
		accParm.addData("SERVANT_AMT_B", servant_amt_b);
		accParm.addData("MZ_AGENT_AMT", mz_agent_amt);
		accParm.addData("MZ_AGENT_AMT_B", mz_agent_amt_b);
		accParm.addData("FY_AGENT_AMT", fy_agent_amt);
		accParm.addData("FY_AGENT_AMT_B", fy_agent_amt_b);
		accParm.addData("FD_AGENT_AMT", fd_agent_amt);
		accParm.addData("FD_AGENT_AMT_B", fd_agent_amt_b);
		accParm.addData("UNREIM_AMT", unreim_amt);
		accParm.addData("UNREIM_AMT_B", unreim_amt_b);
		accParm.addData("OTOT_AMT", otot_amt);
		accParm.addData("OTOT_AMT_B", otot_amt_b);
		accParm.addData("INSAMT_FLG", "5");
		accParm.setCount(1);
		return accParm;
	}

	/**
	 * ���㱣�����
	 */
	public void onAccountSave() {
		tableAccount.acceptText();
		TParm parm = tableAccount.getParmValue();
		//System.out.println("��������parm:::::" + parm);
		if (parm.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return;
		}
		boolean flg = false;// �ж��Ƿ�����Ҫִ�е�����
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {
				flg = true;
				break;
			}
		}
		TParm accountParm = checkOutParm();
		// parm.setData("INSAMT_FLG", 5);// ���˱�־
		accountParm.setData("START_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "00:00:00"));
		accountParm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("ACCOUNT_DATE"), "23:59:59"));

		if (flg) {
			accountParm.setData("CHECK_DATE", df1.format(this
					.getValue("ACCOUNT_DATE")));
			// ��ѯ�Ƿ�������� �������ڲ�ѯ
			TParm checkAccountParm = INSCheckAccountTool.getInstance()
					.queryInsCheckAccount(accountParm);
			if (checkAccountParm.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}
			if (checkAccountParm.getCount() > 0) {
				this.messageBox("�Ѵ�������");
				return;
			}
			parm = parm.getRow(0);
			//System.out.println("parm::::" + parm);
			parm.setData("CHECK_DATE", df1
					.format(this.getValue("ACCOUNT_DATE")));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result = INSCheckAccountTool.getInstance()
					.insertInsCheckAccount(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
			} else {
				this.messageBox("P0005");
			}

		} else {
			this.messageBox("��ѡ������");
		}
	}

	/**
	 * У������
	 * 
	 * @return
	 */
	private boolean checkOut() {
		if (getValue("ACCOUNT_DATE") == null
				|| getValueString("ACCOUNT_DATE").length() <= 0) {
			messageBox("��ѡ�����ʱ��!");
			return false;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE,INS_PAT_TYPE")) {
			return false;
		}
		return true;
	}

	public void onClearAccount() {
		tableAccount.removeRowAll();
	}

}
