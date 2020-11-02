package com.javahis.ui.sum;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.base.TFrameBase;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.DateUtil;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

import jdo.adm.ADMTool;
import jdo.erd.ERDForSUMTool;
import jdo.sum.SUMVitalSignTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

/**
 * <p>
 * Title: �������µ�
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: javahis
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * 
 * @version 1.0
 */
public class SUMVitalSignMainControl extends TControl {

	TTable masterTable;// ���¼�¼table������
	TTable detailTable; // ������ϸtable��ϸ��
	int masterRow = -1;// ��tableѡ���к�
	int detailRow = -1;// ϸtableѡ���к�
	TParm patInfo = new TParm();// ������Ϣ
	String admType = "I"; // �ż�ס��
	String caseNo = ""; // �����
	TParm tprDtl = new TParm(); // ���±�����
	TParm inParm = new TParm(); // ���
	boolean isMroFlg = false;// ������ҳ����

	private final static String stoolLink = "+";
	/**
	 * P ���� C ��ͯ N ������
	 */
	String sumType = "";
	// ���� ������
	private List<Integer> MorningList = new ArrayList<Integer>();
	private List<Integer> AfternoonList = new ArrayList<Integer>();

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		masterTable = (TTable) this.getComponent("masterTable");// ��ʼ�����
		detailTable = (TTable) this.getComponent("detailTable");
		this.callFunction("UI|masterTable|addEventListener", "masterTable->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");// table����¼�
		inParm = this.getInputParm();
		if (inParm != null) {
			admType = inParm.getValue("SUM", "ADM_TYPE");
			caseNo = inParm.getValue("SUM", "CASE_NO");
			sumType = inParm.getValue("SUM", "TYPE");
			onQuery();
			if (inParm.getValue("SUM", "FLG").equals("MRO")) {
				isMroFlg = true;
				hideFrame();// ���ؽ���
			}
		}
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// ��¼ʱ��
		this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��

		MorningList.add(0);
		MorningList.add(1);
		MorningList.add(2);
		AfternoonList.add(3);
		AfternoonList.add(4);
		AfternoonList.add(5);
	}

	/**
	 * �����MRO�������������
	 */
	public void hideFrame() {
		if (!(getComponent() instanceof TFrameBase))
			return;
		TFrame frame = (TFrame) getComponent();
		frame.setOpenShow(false);
		onPrint();
		frame.onClosed();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		if ("E".equals(admType)) {
			patInfo = ERDForSUMTool.getInstance().selERDPatInfo(parm); // ������Ϣ
			patInfo.setData("ADM_DAYS", 0, this.getInHospDays(
					patInfo.getTimestamp("IN_DATE", 0),
					patInfo.getTimestamp("OUT_DATE", 0)));
		} else if ("I".equals(admType)) {
			patInfo = ADMTool.getInstance().getADM_INFO(parm);
			patInfo.setData("ADM_DAYS", 0,
					ADMTool.getInstance().getAdmDays(caseNo) + 1);
		}
		TParm result = SUMVitalSignTool.getInstance().selectExmDateUser(parm);// ���¼�¼
		// ��ʼ�����¼�¼table
		for (int row = 0; row < result.getCount(); row++) {
			result.setData("EXAMINE_DATE", row, StringTool.getString(StringTool
					.getDate(result.getValue("EXAMINE_DATE", row), "yyyyMMdd"),
					"yyyy/MM/dd"));
			if ((row + 1) % 7 == 0) {// ÿ��������һ������ɫ
				masterTable.setRowColor(row, new Color(255, 255, 132));
			}
		}
		masterTable.getTable().repaint();
		masterTable.setParmValue(result);
		if (masterTable.getRowCount() > 0) {
			masterTable.setSelectedRow(masterTable.getRowCount() - 1);// Ĭ��ѡ�����һ��
			onMasterTableClicked(masterTable.getRowCount() - 1); // �ֶ�ִ�е���¼�
		}
	}

	/**
	 * ���סԺ����
	 * 
	 * @param inDate
	 * @param outDate
	 * @return
	 */
	private int getInHospDays(Timestamp inDate, Timestamp outDate) {
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		Timestamp endDate = null;
		if (outDate == null)
			endDate = now;
		else
			endDate = outDate;
		int days = StringTool.getDateDiffer(endDate, inDate);
		if (days == 0)
			return 1;
		return days;
	}

	/**
	 * ��ת��
	 * 
	 * @param tprDtl
	 *            TParm
	 * @return TParm
	 */
	public TParm rowToColumn(TParm tprDtl) {
		TParm result = new TParm();
		for (int i = 0; i < tprDtl.getCount(); i++) {
			result.addData("" + i, clearZero(tprDtl.getData("TEMPERATURE", i)));// ����
			result.addData("" + i, clearZero(tprDtl.getData("PLUSE", i)));// ����
			result.addData("" + i, clearZero(tprDtl.getData("RESPIRE", i)));// ����
			/****************** shibl 20120330 modify ***************************/
			result.addData("" + i,
					clearZero(tprDtl.getData("SYSTOLICPRESSURE", i))); // ����ѹ
			result.addData("" + i,
					clearZero(tprDtl.getData("DIASTOLICPRESSURE", i))); // ����ѹ
			result.addData("" + i, clearZero(tprDtl.getData("HEART_RATE", i))); // ����
		}
		return result;
	}

	/**
	 * �½�
	 */
	public void onNew() {
		// �õ�������/���ݿ⵱ǰʱ��
		// String today =
		// StringTool.getString(TJDODBTool.getInstance().getDBTime(),
		// "yyyyMMdd");
		String today = (String) openDialog("%ROOT%\\config\\sum\\SUMTemperatureDateChoose.x");
		if (today.length() == 0) {
			messageBox("δѡ�����ʱ��");
			return;
		}
		for (int i = 0; i < masterTable.getRowCount(); i++) {
			if (today.equals(StringTool.getString(
					StringTool.getDate(
							masterTable.getItemString(i, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd"))) {
				this.messageBox("�Ѵ��ڽ�������\n�������½�");
				return;
			}
		}
		// ����һ������
		TParm MData = new TParm();
		MData.setData("EXAMINE_DATE",
				today.substring(0, 4) + "/" + today.substring(4, 6) + "/"
						+ today.substring(6));
		MData.setData("USER_ID", Operator.getName());
		// Ĭ��ѡ��������
		int newRow = masterTable.addRow(MData);
		masterTable.setSelectedRow(newRow);
		onMasterTableClicked(newRow); // �ֶ�ִ�е����¼�
		// �½���ʱ��д�뵱ǰʱ��
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// סԺ����
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// ��¼ʱ��
		this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
	}

	/**
	 * ���¼�¼table�����¼�
	 */
	public void onMasterTableClicked(int row) {
		masterRow = row; // ��tableѡ���к�
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXAMINE_DATE", StringTool.getString(StringTool.getDate(
				masterTable.getItemString(row, "EXAMINE_DATE"), "yyyy/MM/dd"),
				"yyyyMMdd"));
		// =========================��������
		TParm master = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		this.clearComponent();// ������
		// ��ؼ���ֵ
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// סԺ����
		this.setValue("OPE_DAYS", master.getValue("OPE_DAYS", 0));// �����ڼ���
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);
		this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
		// =========================ϸ������
		tprDtl = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		// ���û�и�������ݲ���հ���
		if (tprDtl.getCount() == 0) {
			detailTable.removeRowAll();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			// return;
		} else {
			detailTable.setParmValue(rowToColumn(tprDtl));
		}
		// =========================ϸ����������
		/**
		 * ����
		 */
		if (this.sumType.equals("P")) {
			setPDownPanel(master);
		} else {
			setCorNDownPanel(master);
		}
	}

	/**
	 * ��ͯ����������ֵ
	 */
	public void setCorNDownPanel(TParm master) {
		this.setValue("STOOL", master.getValue("STOOL", 0));// ���
		// ======================shibl add
		// 20150727================================================
		this.setValue("STOOLX", master.getValue("STOOLX", 0));
		this.setValue("STOOLX_NUM", master.getValue("STOOLX_NUM", 0));
		this.setValue("STOOLW", master.getValue("STOOLW", 0));
		this.setValue("STOOLW_NUM", master.getValue("STOOLW_NUM", 0));
		this.setValue("STOOLB", master.getValue("STOOLB", 0));
		this.setValue("E_STOOL", master.getValue("E_STOOL", 0));
		this.setValue("E_STOOLX", master.getValue("E_STOOLX", 0));
		this.setValue("E_STOOLX_NUM", master.getValue("E_STOOLX_NUM", 0));
		this.setValue("E_STOOLW", master.getValue("E_STOOLW", 0));
		this.setValue("E_STOOLW_NUM", master.getValue("E_STOOLW_NUM", 0));
		this.setValue("E_STOOLB", master.getValue("E_STOOLB", 0));
		this.setValue("URINETIMES_NUM", master.getValue("URINETIMES_NUM", 0));
		this.setValue("BM_FLG", master.getValue("BM_FLG", 0));
		// ============================================================================
		this.setValue("ENEMA", master.getValue("ENEMA", 0));// �೦
		this.setValue("DRAINAGE", master.getValue("DRAINAGE", 0));// ����
		this.setValue("INTAKEFLUIDQTY", master.getValue("INTAKEFLUIDQTY", 0));// ����
		this.setValue("OUTPUTURINEQTY", master.getValue("OUTPUTURINEQTY", 0));// ����
		this.setValue("HEIGHT", master.getValue("HEIGHT", 0));// ���
		this.setValue("USER_DEFINE_1", master.getValue("USER_DEFINE_1", 0));// �Զ���һ
		this.setValue("USER_DEFINE_1_VALUE",
				master.getValue("USER_DEFINE_1_VALUE", 0));
		this.setValue("USER_DEFINE_2", master.getValue("USER_DEFINE_2", 0));// �Զ����
		this.setValue("USER_DEFINE_2_VALUE",
				master.getValue("USER_DEFINE_2_VALUE", 0));
		this.setValue("USER_DEFINE_3", master.getValue("USER_DEFINE_3", 0));// �Զ�����
		this.setValue("USER_DEFINE_3_VALUE",
				master.getValue("USER_DEFINE_3_VALUE", 0));
		this.setValue("URINETIMES", master.getValue("URINETIMES", 0));// С��
		this.setValue("VOMIT", master.getValue("VOMIT", 0));// Ż��
		this.setValue("HEAD_CIRCUM", master.getValue("HEAD_CIRCUM", 0));// ͷΧ
		this.setValue("ABDOMEN_CIRCUM", master.getValue("ABDOMEN_CIRCUM", 0));// ��Χ
		if (!master.getValue("WEIGHT", 0).equals("")) {
			this.setValue("WEIGHT", master.getValue("WEIGHT", 0));// ����
			getTRadioButton("W_KG").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(false);
			this.getTextField("WEIGHT").setEnabled(true);
		} else {
			this.setValue("WEIGHT_G", master.getValue("WEIGHT_G", 0));// ���ؿ�
			getTRadioButton("W_G").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(true);
			this.getTextField("WEIGHT").setEnabled(false);
		}
		this.setValue("WEIGHT_REASON", master.getValue("WEIGHT_REASON", 0));// ����δ��ԭ��
	}

	/**
	 * ���˸�ֵ
	 * 
	 * @param parm
	 */
	public void setPDownPanel(TParm master) {
		this.setValue("STOOL", master.getValue("STOOL", 0));// ���
		this.setValue("AUTO_STOOL", master.getValue("AUTO_STOOL", 0));// �����ű�
		this.setValue("ENEMA", master.getValue("ENEMA", 0));// �೦
		this.setValue("DRAINAGE", master.getValue("DRAINAGE", 0));// ����
		this.setValue("INTAKEFLUIDQTY", master.getValue("INTAKEFLUIDQTY", 0));// ����
		this.setValue("OUTPUTURINEQTY", master.getValue("OUTPUTURINEQTY", 0));// ����
		this.setValue("HEIGHT", master.getValue("HEIGHT", 0));// ���
		this.setValue("USER_DEFINE_1", master.getValue("USER_DEFINE_1", 0));// �Զ���һ
		this.setValue("USER_DEFINE_1_VALUE",
				master.getValue("USER_DEFINE_1_VALUE", 0));
		this.setValue("USER_DEFINE_2", master.getValue("USER_DEFINE_2", 0));// �Զ����
		this.setValue("USER_DEFINE_2_VALUE",
				master.getValue("USER_DEFINE_2_VALUE", 0));
		this.setValue("USER_DEFINE_3", master.getValue("USER_DEFINE_3", 0));// �Զ�����
		this.setValue("USER_DEFINE_3_VALUE",
				master.getValue("USER_DEFINE_3_VALUE", 0));
		this.setValue("URINETIMES", master.getValue("URINETIMES", 0));// С��
		this.setValue("VOMIT", master.getValue("VOMIT", 0));// Ż��
		this.setValue("HEAD_CIRCUM", master.getValue("HEAD_CIRCUM", 0));// ͷΧ
		this.setValue("ABDOMEN_CIRCUM", master.getValue("ABDOMEN_CIRCUM", 0));// ��Χ
		if (!master.getValue("WEIGHT", 0).equals("")) {
			this.setValue("WEIGHT", master.getValue("WEIGHT", 0));// ����
			getTRadioButton("W_KG").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(false);
			this.getTextField("WEIGHT").setEnabled(true);
		} else {
			this.setValue("WEIGHT_G", master.getValue("WEIGHT_G", 0));// ���ؿ�
			getTRadioButton("W_G").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(true);
			this.getTextField("WEIGHT").setEnabled(false);
		}
		this.setValue("WEIGHT_REASON", master.getValue("WEIGHT_REASON", 0));// ����δ��ԭ��
	}

	/**
	 * ������ϸtable�����¼�(ͨ������ע�ᷨ)
	 */
	public void onDTableFocusChange() {
		// PS:���ڴ��ھ���ת�������⣬����ѡ�е���Ϊ����PARM����
		// ��ʼ��ϸtable��ѡ�е��У�����table���кţ�
		detailRow = detailTable.getSelectedColumn();
		((TComboBox) this.getComponent("EXAMINESESSION"))
				.setSelectedIndex(detailRow + 1);
		this.setValue("RECTIME", tprDtl.getValue("RECTIME", detailRow));// ��¼ʱ��
		if (tprDtl.getValue("RECTIME", detailRow).equals("")) {
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			this.setValue("RECTIME", now);// ��¼ʱ��
		}
		this.setValue("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", detailRow));// ���±仯�������
		this.setValue("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", detailRow));// ������
		this.setValue("TMPTRKINDCODE",
				tprDtl.getValue("TMPTRKINDCODE", detailRow));// ��������
		if (tprDtl.getValue("TMPTRKINDCODE", detailRow).equals("")) {
			this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
		}
		this.setValue("NOTPRREASONCODE",
				tprDtl.getValue("NOTPRREASONCODE", detailRow));// δ��ԭ��
		this.setValue("PTMOVECATECODE",
				tprDtl.getValue("PTMOVECATECODE", detailRow));// ���˶�̬
		this.setValue("PTMOVECATEDESC",
				tprDtl.getValue("PTMOVECATEDESC", detailRow));// ���˶�̬��ע
	}

	/**
	 * ����
	 */
	public boolean onSave() {
		masterTable.acceptText();
		detailTable.acceptText();
		if (masterRow < 0) {
			this.messageBox("��ѡ��һ����¼��");
			return false;
		}
		TParm saveParm = new TParm();
		if (sumType.equals("P")) {
			saveParm = this.getPValueFromUI();
		} else {
			saveParm = this.getCorNValueFromUI();
		}
		if (saveParm.getErrCode() < 0) {
			this.messageBox(saveParm.getErrText());
			return false;
		}
		String sql = " SELECT HEIGHT,WEIGHT FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "'";
		TParm HWeightParmadm = new TParm(TJDODBTool.getInstance().select(sql));
		// �����õ�������ػ�дADM_INP
		TParm HWeightParm = new TParm();
		if (!this.getValueString("WEIGHT").equals("")
				&& this.getValueDouble("HEIGHT") > 0) {
			HWeightParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));
		} else {
			HWeightParm
					.setData("HEIGHT", HWeightParmadm.getDouble("HEIGHT", 0));
		}
		try {
			if (!this.getValueString("WEIGHT").equals("")) {
				HWeightParm.setData("WEIGHT", this.getValueString("WEIGHT"));
			} else if (!this.getValueString("WEIGHT_G").equals("")) {
				HWeightParm.setData("WEIGHT",
						this.getValueDouble("WEIGHT_G") / 1000);
			} else {
				HWeightParm.setData("WEIGHT",
						HWeightParmadm.getDouble("WEIGHT", 0));
			}
		} catch (NumberFormatException e) {
			HWeightParm
					.setData("WEIGHT", HWeightParmadm.getDouble("WEIGHT", 0));
		}
		HWeightParm.setData("CASE_NO", caseNo);
		saveParm.setData("HW", HWeightParm.getData());
		// �ж��Ƿ����и����ݲ���/����
		String saveDate = saveParm.getParm("MASET").getValue("EXAMINE_DATE");
		// �õ����table������
		TParm checkDate = new TParm();
		checkDate.setData("CASE_NO", caseNo);
		checkDate.setData("ADM_TYPE", admType);
		checkDate.setData("EXAMINE_DATE", saveDate);
		TParm existParm = SUMVitalSignTool.getInstance()
				.checkIsExist(checkDate);
		// û�и������ݣ�ֱ�ӱ���
		if (existParm.getCount() == 0) {// �����ڼ�¼���½�
			saveParm.setData("I", true);
			// ����actionִ������
			TParm result = TIOM_AppServer.executeAction(
					"action.sum.SUMVitalSignAction",
					sumType.equals("P") ? "onSave" : "onCorNSave", saveParm);
			// ���ñ���
			if (result.getErrCode() < 0) {
				this.messageBox_(result);
				this.messageBox("E0001");
				return false;
			}
			this.messageBox("P0001");
			onClear();
			return true;
		}
		// ���ǲ���--update
		saveParm.setData("I", false);
		// ������0˵���Ѿ��д��ڵĸ���������--���ϡ�û����--���¶���
		if (existParm.getData("DISPOSAL_FLG", 0) != null
				&& existParm.getData("DISPOSAL_FLG", 0).equals("Y")) {
			if (0 == this.messageBox("", "�������Ѿ����Ϲ���\n�Ƿ���ȷ�����棿",
					this.YES_NO_OPTION)) {
				// ����actionִ������
				TParm result = TIOM_AppServer
						.executeAction("action.sum.SUMVitalSignAction",
								sumType.equals("P") ? "onSave" : "onCorNSave",
								saveParm);
				// ���ñ���
				if (result.getErrCode() < 0) {
					this.messageBox_(result);
					this.messageBox("E0001");
					return false;
				}
				this.messageBox("P0001");
				return true;
			} else {
				this.messageBox("û�и������ݣ�");
				return true;
			}
		}
		// ֱ��������--DISPOSAL_FLG==null����N
		TParm result = TIOM_AppServer.executeAction(
				"action.sum.SUMVitalSignAction", sumType.equals("P") ? "onSave"
						: "onCorNSave", saveParm);
		// ���ñ���
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			this.messageBox("����ʧ�ܣ�");
			return false;
		}
		this.messageBox("����ɹ���");
		onClear();
		return true;
	}

	/**
	 * ���� ���棺�ӿؼ�������ֵ�����������Ա�����TDSʹ��
	 */
	public TParm getPValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// �������
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// סԺ����
		masterParm.setData("OPE_DAYS", this.getValue("OPE_DAYS"));// ��������
		masterParm.setData("ECTTIMES", "");// ĿǰECT��������ʱû�ã�
		masterParm.setData("MCFLG", "");// �¾�����ʱû�ã�
		masterParm.setData("HOURSOFSLEEP", "");// ˯��ʱ��-Сʱ����ʱû�ã�
		masterParm.setData("MINUTESOFSLEEP", "");// ˯��ʱ��-�֣���ʱû�ã�
		masterParm.setData("SPECIALSTOOLNOTE", "");// �����ű��������ʱû�ã�
		masterParm.setData("INTAKEDIETQTY", "");// ����-��ʳ������ʱû�ã�
		masterParm.setData("OUTPUTDRAINQTY", "");// �ų�-����������ʱû�ã�
		masterParm.setData("OUTPUTOTHERQTY", "");// �ų�-��������ʱû�ã�
		masterParm.setData("BATH", "");// ϴ�裨��ʱû�ã�
		masterParm.setData("GUESTKIND", "");// ��ͣ���ʱû�ã�
		masterParm.setData("STAYOUTSIDE", "");// ���ޣ���ʱû�ã�
		masterParm.setData("LEAVE", "");// �������ʱû�ã�
		masterParm.setData("LEAVEREASONCODE", "");// ���ԭ����루��ʱû�ã�
		masterParm.setData("NOTE", "");// ��ע����ʱû�ã�
		masterParm.setData("STATUS_CODE", "");// ������״̬����ʱû�ã�
		masterParm.setData("DISPOSAL_FLG", "");// ����ע��
		masterParm.setData("DISPOSAL_REASON", "");// ��������
		masterParm.setData("STOOL", this.getValueString("STOOL"));// ���
		masterParm.setData("AUTO_STOOL", this.getValue("AUTO_STOOL"));// �����ű�
		masterParm.setData("ENEMA", this.getValue("ENEMA"));// �೦
		masterParm.setData("DRAINAGE", this.getValue("DRAINAGE"));// ����
		masterParm.setData("INTAKEFLUIDQTY",
				this.getValueString("INTAKEFLUIDQTY"));// ����-ע��
		masterParm.setData("OUTPUTURINEQTY",
				this.getValueDouble("OUTPUTURINEQTY"));// ����-С����
		masterParm.setData("WEIGHT", this.getValue("WEIGHT"));// ����
		masterParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));// ���
		masterParm.setData("USER_DEFINE_1", this.getValue("USER_DEFINE_1"));// �Զ���һ
		masterParm.setData("USER_DEFINE_1_VALUE",
				this.getValue("USER_DEFINE_1_VALUE"));
		masterParm.setData("USER_DEFINE_2", this.getValue("USER_DEFINE_2"));// �Զ����
		masterParm.setData("USER_DEFINE_2_VALUE",
				this.getValue("USER_DEFINE_2_VALUE"));
		masterParm.setData("USER_DEFINE_3", this.getValue("USER_DEFINE_3"));// �Զ�����
		masterParm.setData("USER_DEFINE_3_VALUE",
				this.getValue("USER_DEFINE_3_VALUE"));
		masterParm.setData("USER_ID", Operator.getID());// ��¼��Ա

		masterParm.setData("URINETIMES", this.getValueString("URINETIMES"));// С��
		masterParm.setData("VOMIT", this.getValueString("VOMIT"));// Ż��
		masterParm.setData("HEAD_CIRCUM", this.getValueString("HEAD_CIRCUM"));// ͷΧ
		masterParm.setData("ABDOMEN_CIRCUM",
				this.getValueString("ABDOMEN_CIRCUM"));// ��Χ
		masterParm.setData("WEIGHT_G", this.getValueString("WEIGHT_G"));// ����g
		masterParm.setData("WEIGHT_REASON",
				this.getValueString("WEIGHT_REASON"));// ����δ��ԭ��

		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		String columnIndex = this.getValueString("EXAMINESESSION");
		for (int i = 0; i < 6; i++) {// ʱ����6��
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// ��¼ʱ��
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// ���±仯�������
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// ������
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// ��������
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// δ��ԭ��
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// ���˶�̬
				if (!StringUtil.isNullString(this
						.getValueString("PTMOVECATECODE"))
						&& StringUtil.isNullString(this
								.getValueString("PTMOVECATEDESC"))) {
					TParm errParm = new TParm();
					errParm.setErr(-1, "����д���˶�̬��ע");
					return errParm;
				}
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// ���˶�̬��ע
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// ��¼ʱ��
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// ���±仯�������
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// ������
				// wanglong modify 20140428
				oneParm.setData(
						"TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("") ? this
								.getValue("TMPTRKINDCODE") : tprDtl.getValue(
								"TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// δ��ԭ��
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// ���˶�̬
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// ���˶�̬��ע
			}
			// �õ�table�ϵ�������
			oneParm.setData("TEMPERATURE",
					TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// ����
			oneParm.setData("PLUSE",
					TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// ����
			oneParm.setData("RESPIRE",
					TCM_Transform.getDouble(detailTable.getValueAt(2, i)));// ����
			/****************** shibl 20120330 modify ***************************/
			oneParm.setData("SYSTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(3, i)));// ����ѹ
			oneParm.setData("DIASTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(4, i)));// ����ѹ
			oneParm.setData("HEART_RATE",
					TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// ����
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * ���������ͯ����
	 * 
	 * @return
	 */
	public TParm getCorNValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// �������
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// סԺ����
		masterParm.setData("OPE_DAYS", this.getValue("OPE_DAYS"));// ��������
		masterParm.setData("ECTTIMES", "");// ĿǰECT��������ʱû�ã�
		masterParm.setData("MCFLG", "");// �¾�����ʱû�ã�
		masterParm.setData("HOURSOFSLEEP", "");// ˯��ʱ��-Сʱ����ʱû�ã�
		masterParm.setData("MINUTESOFSLEEP", "");// ˯��ʱ��-�֣���ʱû�ã�
		masterParm.setData("SPECIALSTOOLNOTE", "");// �����ű��������ʱû�ã�
		masterParm.setData("INTAKEDIETQTY", "");// ����-��ʳ������ʱû�ã�
		masterParm.setData("OUTPUTDRAINQTY", "");// �ų�-����������ʱû�ã�
		masterParm.setData("OUTPUTOTHERQTY", "");// �ų�-��������ʱû�ã�
		masterParm.setData("BATH", "");// ϴ�裨��ʱû�ã�
		masterParm.setData("GUESTKIND", "");// ��ͣ���ʱû�ã�
		masterParm.setData("STAYOUTSIDE", "");// ���ޣ���ʱû�ã�
		masterParm.setData("LEAVE", "");// �������ʱû�ã�
		masterParm.setData("LEAVEREASONCODE", "");// ���ԭ����루��ʱû�ã�
		masterParm.setData("NOTE", "");// ��ע����ʱû�ã�
		masterParm.setData("STATUS_CODE", "");// ������״̬����ʱû�ã�
		masterParm.setData("DISPOSAL_FLG", "");// ����ע��
		masterParm.setData("DISPOSAL_REASON", "");// ��������
		masterParm.setData("STOOL", this.getValueString("STOOL"));// ���
		// ===========================20150727 add
		// shibl=========================================
		masterParm.setData("STOOLX", this.getValueString("STOOLX"));// ϡ�����
		masterParm.setData("STOOLX_NUM", this.getValueString("STOOLX_NUM"));// ϡ��������
		masterParm.setData("STOOLW", this.getValueString("STOOLW"));// ϡˮ���
		masterParm.setData("STOOLW_NUM", this.getValueString("STOOLW_NUM"));// ϡˮ������
		masterParm.setData("STOOLB", this.getValueString("STOOLB"));// Ѫ���
		masterParm.setData("E_STOOL", this.getValueString("E_STOOL"));// �೦��������
		masterParm.setData("E_STOOLX", this.getValueString("E_STOOLX"));// �೦��ϡ�����
		masterParm.setData("E_STOOLX_NUM", this.getValueString("E_STOOLX_NUM"));// �೦��ϡ��������
		masterParm.setData("E_STOOLW", this.getValueString("E_STOOLW"));// �೦��ϡˮ���
		masterParm.setData("E_STOOLW_NUM", this.getValueString("E_STOOLW_NUM"));// �೦��ϡˮ������
		masterParm.setData("E_STOOLB", this.getValueString("E_STOOLB"));// �೦��Ѫ��
		masterParm.setData("URINETIMES_NUM",
				this.getValueString("URINETIMES_NUM"));// С������
		masterParm.setData("BM_FLG", this.getValueString("BM_FLG"));// ��ĸ��ע��
		// =======================================================================================
		masterParm.setData("ENEMA", this.getValue("ENEMA"));// �೦
		masterParm.setData("DRAINAGE", this.getValue("DRAINAGE"));// ����
		masterParm.setData("INTAKEFLUIDQTY",
				this.getValueString("INTAKEFLUIDQTY"));// ����-ע��
		masterParm.setData("OUTPUTURINEQTY",
				this.getValueDouble("OUTPUTURINEQTY"));// ����-С����
		masterParm.setData("WEIGHT", this.getValue("WEIGHT"));// ����
		masterParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));// ���
		masterParm.setData("USER_DEFINE_1", this.getValue("USER_DEFINE_1"));// �Զ���һ
		masterParm.setData("USER_DEFINE_1_VALUE",
				this.getValue("USER_DEFINE_1_VALUE"));
		masterParm.setData("USER_DEFINE_2", this.getValue("USER_DEFINE_2"));// �Զ����
		masterParm.setData("USER_DEFINE_2_VALUE",
				this.getValue("USER_DEFINE_2_VALUE"));
		masterParm.setData("USER_DEFINE_3", this.getValue("USER_DEFINE_3"));// �Զ�����
		masterParm.setData("USER_DEFINE_3_VALUE",
				this.getValue("USER_DEFINE_3_VALUE"));
		masterParm.setData("USER_ID", Operator.getID());// ��¼��Ա

		masterParm.setData("URINETIMES", this.getValueString("URINETIMES"));// С��
		masterParm.setData("VOMIT", this.getValueString("VOMIT"));// Ż��
		masterParm.setData("HEAD_CIRCUM", this.getValueString("HEAD_CIRCUM"));// ͷΧ
		masterParm.setData("ABDOMEN_CIRCUM",
				this.getValueString("ABDOMEN_CIRCUM"));// ��Χ
		masterParm.setData("WEIGHT_G", this.getValueString("WEIGHT_G"));// ����g
		masterParm.setData("WEIGHT_REASON",
				this.getValueString("WEIGHT_REASON"));// ����δ��ԭ��

		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		String columnIndex = this.getValueString("EXAMINESESSION");
		for (int i = 0; i < 6; i++) {// ʱ����6��
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// ��¼ʱ��
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// ���±仯�������
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// ������
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// ��������
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// δ��ԭ��
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// ���˶�̬
				if (!StringUtil.isNullString(this
						.getValueString("PTMOVECATECODE"))
						&& StringUtil.isNullString(this
								.getValueString("PTMOVECATEDESC"))) {
					TParm errParm = new TParm();
					errParm.setErr(-1, "����д���˶�̬��ע");
					return errParm;
				}
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// ���˶�̬��ע
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// ��¼ʱ��
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// ���±仯�������
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// ������
				// wanglong modify 20140428
				oneParm.setData(
						"TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("") ? this
								.getValue("TMPTRKINDCODE") : tprDtl.getValue(
								"TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// δ��ԭ��
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// ���˶�̬
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// ���˶�̬��ע
			}
			// �õ�table�ϵ�������
			oneParm.setData("TEMPERATURE",
					TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// ����
			oneParm.setData("PLUSE",
					TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// ����
			oneParm.setData("RESPIRE",
					TCM_Transform.getDouble(detailTable.getValueAt(2, i)));// ����
			/****************** shibl 20120330 modify ***************************/
			oneParm.setData("SYSTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(3, i)));// ����ѹ
			oneParm.setData("DIASTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(4, i)));// ����ѹ
			oneParm.setData("HEART_RATE",
					TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// ����
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * ���
	 */
	public void onClear() {
		// ��������ȫ�ֱ���
		masterRow = -1;
		detailRow = -1;
		this.clearComponent();
		detailTable.removeRowAll();
		onQuery();// ִ�в�ѯ
	}

	/**
	 * ������
	 */
	public void clearComponent() {
		// �����ϰ벿��
		this.clearValue("EXAMINESESSION;RECTIME;" // INHOSPITALDAYSסԺ���������;OPE_DAYS�������������
				+ "SPCCONDCODE;PHYSIATRICS;"// TMPTRKINDCODE�������಻���
				+ "NOTPRREASONCODE;PTMOVECATECODE;PTMOVECATEDESC");
		// �����°벿��
		if (sumType.equals("P")) {
			clearPDownPanel();
		} else {
			clearCorNDownPanel();
		}
	}

	/**
	 * ��ճ���
	 */
	public void clearPDownPanel() {
		this.clearValue("STOOL;INTAKEFLUIDQTY;OUTPUTURINEQTY;WEIGHT;HEIGHT;URINETIMES;VOMIT;HEAD_CIRCUM;ABDOMEN_CIRCUM;"
				+ "WEIGHT_G;WEIGHT_REASON;USER_DEFINE_1;USER_DEFINE_2;USER_DEFINE_3;USER_DEFINE_1_VALUE;USER_DEFINE_2_VALUE;USER_DEFINE_3_VALUE");
	}

	/**
	 * ������������ͯ
	 */
	public void clearCorNDownPanel() {
		this.clearValue("STOOL;INTAKEFLUIDQTY;OUTPUTURINEQTY;WEIGHT;HEIGHT;URINETIMES;VOMIT;HEAD_CIRCUM;ABDOMEN_CIRCUM;"
				+ "WEIGHT_G;WEIGHT_REASON;USER_DEFINE_1;USER_DEFINE_2;USER_DEFINE_3;USER_DEFINE_1_VALUE;USER_DEFINE_2_VALUE;USER_DEFINE_3_VALUE;"
				+ "STOOLX;STOOLX_NUM;STOOLW;STOOLW_NUM;STOOLB;E_STOOL;E_STOOLX;E_STOOLX_NUM;E_STOOLW;E_STOOLW_NUM;E_STOOLB;URINETIMES_NUM;"
				+ "BM_FLG");
	}

	/**
	 * ����
	 */
	public void onDefeasance() {
		int selRow = masterTable.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ���������ݣ�");
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\sum\\SUMDefeasance.x");
		if (value == null)
			return;
		// �õ�ѡ���е�EXAMINE_DATE
		String examineDate = StringTool.getString(
				StringTool.getDate(
						masterTable.getItemString(selRow, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd");
		String defSel = "SELECT * FROM SUM_VITALSIGN WHERE ADM_TYPE = '"
				+ admType + "' AND CASE_NO = '" + caseNo
				+ "' AND EXAMINE_DATE = '" + examineDate + "'";
		TDS defData = new TDS();
		defData.setSQL(defSel);
		defData.retrieve();
		defData.setItem(0, "DISPOSAL_REASON", value);
		defData.setItem(0, "DISPOSAL_FLG", "Y");
		if (!defData.update()) {
			this.messageBox("����ʧ�ܣ�");
			return;
		}
		this.messageBox("���ϳɹ���");
		onClear();
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (masterTable.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ���ݣ�");
			return;
		}
		TParm prtForSheetParm = new TParm();
		// ��ô�ӡ������
		TParm parmDate = new TParm();
		// ��Ժ����ʱ��
		Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
		String sumKind = onAgeCode(this.caseNo, this.admType);
		parmDate.setData("IN_DATE", inDate);
		parmDate.setData("SUM_JHW", sumType.equals("P")?sumType:sumKind);
		TParm value = (TParm) this.openDialog(
				"%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
		if (value == null) {
			prtForSheetParm.setData("STOP", "ȡ����ӡ��");
			return;
		}
		// �õ�ѡ��ʱ��֮��ġ������+1===>��ӡ������
		int differCount = StringTool.getDateDiffer(
				value.getTimestamp("END_DATE"),
				value.getTimestamp("START_DATE")) + 1;
		if (differCount <= 0) {
			prtForSheetParm.setData("STOP", "��ѯ�������");
			return;
		}
		String jhwName = "";
		String sumJhw = "";
		if ("I".equals(admType)) {
			sumJhw = value.getValue("SUM_JHW");
			jhwName = sumJhw.equals("") ? "SUMVitalSign_PrtSheet"
					: "SUMVitalSign_PrtSheet_" + sumJhw;
			if (jhwName.equals("")) {
				this.messageBox("��������δ����Ӧģ���ļ�");
				return;
			}
		} else if ("E".equals(admType)) {
			jhwName = "SUMVitalSign_PrtSheetE.jhw";
		}
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;
		Timestamp forDate = null;
		int pageNo = 1;
		for (int i = 0; i < pageCount; i++) {
			TParm parm = new TParm();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (i == 0)
				startDate = value.getTimestamp("START_DATE");
			else
				startDate = StringTool.rollDate(forDate, 1);
			int dif = 6;
			// if(i % 7 == 0)
			// dif = 6;
			// else
			// dif = 7 - (i * 7 - differCount) - 1;
			endDate = StringTool.rollDate(startDate, dif);
			forDate = endDate;
			parm.setData("START_DATE", startDate);
			parm.setData("END_DATE", endDate);
			TParm printData = getValueForPrt(parm, dif + 1, i + 1, sumJhw);
			if (printData == null)
				continue;
			printData.setData("PAGENO", "TEXT", pageNo++);
			if (printData.getData("STOP") != null) {
				this.messageBox(printData.getValue("STOP"));
				return;
			}
			// �������µ��ϴ�EMR
			Object returnObj = this.openPrintDialog(
					"%ROOT%\\config\\prt\\sum\\" + jhwName, printData);
			if (returnObj != null) {
				String mr_no = patInfo.getValue("MR_NO", 0);
				EMRTool emrTool = new EMRTool(this.caseNo, mr_no, this);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String fileName = "���µ�" + format.format(startDate) + "-"
						+ format.format(endDate);
				// ======== modify by chenxi 20120702 false��ʾ���µ���ӡʱֻ����һ��
				emrTool.saveEMR(returnObj, fileName, "EMR100002",
						"EMR10000202", false);
			}
		}
	}

	/**
	 * �õ�UI�ϵĲ�������ӡ����
	 * 
	 * @return TPram
	 */
	private TParm getValueForPrt(TParm value, int differCount, int pageNo,
			String type) {
		TParm prtForSheetParm = new TParm();
		// ��������������
		Vector tprSign = getVitalSignDate(value);
		if (((TParm) tprSign.get(0)).getCount() <= 0)
			return null;
		// ��ӡ�����㷨��������ת��������
		prtForSheetParm = dataToCoordinate(tprSign, differCount, type);
		String stationString = "";
		if ("E".equals(admType))
			stationString = patInfo.getValue("ERD_REGION_DESC", 0);
		else if ("I".equals(admType)) {
			String stationCode = patInfo.getValue("STATION_CODE", 0);
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ stationCode + "'"));
			stationString = parm.getValue("STATION_DESC", 0);
		}
		String mrNo = patInfo.getValue("MR_NO", 0);
		// ͨ��MR_NO�õ��Ա�
		Pat pat = Pat.onQueryByMrNo(mrNo);
		String sex = pat.getSexString();
		String ipdNo = patInfo.getValue("IPD_NO", 0);
		String bedNo = patInfo.getValue("BED_NO", 0);
		String bedString = "";
		if ("E".equals(admType))
			bedString = patInfo.getValue("BED_DESC", 0);
		else if ("I".equals(admType)) {
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT BED_NO_DESC FROM SYS_BED WHERE BED_NO='" + bedNo
							+ "'"));
			bedString = bedParm.getValue("BED_NO_DESC", 0);
		}
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ patInfo.getValue("IN_DEPT_CODE", 0) + "'"));
		String Name = pat.getName();
		String age = DateUtil.showAge(pat.getBirthday(),
				patInfo.getTimestamp("IN_DATE", 0));
		prtForSheetParm.setData("MR_NO", "TEXT", mrNo);
		prtForSheetParm.setData("IPD_NO", "TEXT", ipdNo);
		prtForSheetParm.setData("DEPT", "TEXT",
				deptParm.getValue("DEPT_CHN_DESC", 0));
		prtForSheetParm.setData("BED_NO", "TEXT", bedString);
		prtForSheetParm.setData("STATION", "TEXT", stationString);
		prtForSheetParm.setData("NAME", "TEXT", Name);
		prtForSheetParm.setData("SEX", "TEXT", sex);
		prtForSheetParm.setData("AGE", "TEXT", age);
		prtForSheetParm.setData("IN_DATE", "TEXT", StringTool.getString(
				patInfo.getTimestamp("IN_DATE", 0), "yyyy��MM��dd��"));
		prtForSheetParm.setData("BIRTH", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		return prtForSheetParm;
	}

	/**
	 * ��ӡ�����㷨��������ת��������
	 * 
	 * @param tprSign
	 *            Vector ��Ҫ����
	 * @param differCount
	 *            int Ҫ��ӡ��������endDate-startDate��
	 */
	public TParm dataToCoordinate(Vector tprSign, int differCount, String type) {
		TParm mainPrtData = new TParm();
		// ��ϸ������
		TParm master = (TParm) tprSign.get(0); // ���������
		TParm detail = (TParm) tprSign.get(1); // (����)
		int countDays = detail.getCount("SYSTOLICPRESSURE") / 6;
		Vector MorningfinalSystol = new Vector();
		Vector MorningfinalDiastol = new Vector();
		Vector AfternoonfinalSystol = new Vector();
		Vector AfternoonfinalDiastol = new Vector();
		for (int i = 0; i < countDays; i++) {
			String Morningsystol = "";
			String Morningdiastol = "";
			String Afternoonsystol = "";
			String Afternoondiastol = "";
			for (int j = 0; j < 6; j++) {
				if (!StringUtil.isNullString(detail.getValue(
						"SYSTOLICPRESSURE", i * 6 + j))
						&& detail.getInt("SYSTOLICPRESSURE", i * 6 + j) > 0) {
					if (MorningList.contains(j)) {
						Morningsystol = detail.getValue("SYSTOLICPRESSURE", i
								* 6 + j);
					} else if (AfternoonList.contains(j)) {
						Afternoonsystol = detail.getValue("SYSTOLICPRESSURE", i
								* 6 + j);
					}
				}
				if (!StringUtil.isNullString(detail.getValue(
						"DIASTOLICPRESSURE", i * 6 + j))
						&& detail.getInt("DIASTOLICPRESSURE", i * 6 + j) > 0) {
					if (MorningList.contains(j)) {
						Morningdiastol = detail.getValue("DIASTOLICPRESSURE", i
								* 6 + j);
					} else if (AfternoonList.contains(j)) {
						Afternoondiastol = detail.getValue("DIASTOLICPRESSURE",
								i * 6 + j);
					}
				}
			}
			MorningfinalSystol.add(Morningsystol);
			MorningfinalDiastol.add(Morningdiastol);
			AfternoonfinalSystol.add(Afternoonsystol);
			AfternoonfinalDiastol.add(Afternoondiastol);
		}
		// �õ���������
		Vector examineDate = (Vector) master.getData("EXAMINE_DATE");
		// �õ��������������
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0,c99 = 0,c98 = 0,c5 = 0, c6 = 0, c7 = 0, c3S = 0, c3D = 0, c8 = 0, c9 = 0, c10 = 0, c11 = 0, opeDaysVC = 0, cDrainage = 0, cEnema = 0, cAutoStool = 0;
		int userDefine = 0;
		int Sh1 = 0;
		// ���������ó�����
		int countWord = 0;
		// ���ѡ�����������>����/6˵�����������ݣ�������/6Ϊ���¡�����--���ص���Ч����/6
		int newDates = detail.getCount("TEMPERATURE") / 6;
		if (differCount > newDates)
			differCount = newDates;
		// ���ݣ�����/7���õ���Ҫ������ҳ��
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;
		TParm controlPage = new TParm();
		// ������ҳ
		for (int i = 1; i <= pageCount; i++) {
			ArrayList dotList_T = new ArrayList();// ����
			ArrayList dotList_PL = new ArrayList();// ����
			ArrayList dotList_R = new ArrayList();// ����
			ArrayList dotList_P = new ArrayList();// ������
			ArrayList dotList_H = new ArrayList();// ����
			ArrayList lineList_T = new ArrayList();
			ArrayList lineList_PL = new ArrayList();
			ArrayList lineList_R = new ArrayList();
			ArrayList lineList_P = new ArrayList();
			ArrayList lineList_H = new ArrayList();
			// ����ҳ��
			controlPage.addData("PAGE", "" + i);
			// Ƕ����ѭ��������----------------------start-------------------------
			// int date = differCount - (i * 7) % 7;
			int date;
			if (i * 7 <= differCount)
				date = 7;
			else
				date = 7 - (i * 7 - differCount);
			int xT = -1;
			int yT = -1;
			int xPL = -1;
			int yPL = -1;
			int xR = -1;
			int yR = -1;
			int xH = -1;
			int yH = -1;
			String notForward = "";
			String notForward2 = "";
			for (int j = 1; j <= date; j++) {
				// ���ڲ����ʱ��
				Vector respireValue = new Vector();
				// double temForward = (Double) ( (Vector) temperatureV.get( (1
				// + (j - 1) * 6) - 1)).get(0);
				double temper1 = detail.getDouble("TEMPERATURE",
						(1 + (j - 1) * 6) - 1);
				String not1 = nullToEmptyStr(detail.getValue("NOTPRREASONCODE",
						(1 + (j - 1) * 6) - 1));
				if (temper1 != 0
						|| (temper1 == 0 && !StringUtil.isNullString(not1))) {
					notForward = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
					notForward2 = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
				}
				int temperHorizontal = 0;
				int temperVertical = 0;
				for (int exa = 1; exa <= 6; exa++) {
					// �õ�����-------------------start---------------------------
					double temper = detail.getDouble("TEMPERATURE", exa
							+ (j - 1) * 6 - 1);
					double temperBak = temper;
					String tempKindCode = nullToEmptyStr(detail.getValue(
							"TMPTRKINDCODE", exa + (j - 1) * 6 - 1));
					// ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// continue;
						// ���¶�<=35��ʱ��д�����²�����
						if (temper <= 35) {
							// ��ͱ߽�35��
							temper = 35;
							controlPage.addData(
									"NORAISE" + (exa + (j - 1) * 6), "���²���");
						}
						// �õ����µĺ������꣨�㣩
						temperHorizontal = countHorizontal(j, exa);
						temperVertical = (int) (getVertical(temper, "T") + 0.5); // ȡ��
						int dataTemper[] = new int[] {};
						if (temperBak >= 35) {// shibl modify �¶����಻��Ϊ��
							if (tempKindCode.equals("4")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7 };
							} else if (tempKindCode.equals("3")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6 };
							} else {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4 };
							}
						} else if (temperBak < 35) {
							if (tempKindCode.equals("4")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7, 1 };
							} else if (tempKindCode.equals("3")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6, 1 };
							} else {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4, 1 };
							}
						}
						// �������е�
						dotList_T.add(dataTemper);
					}
					// --------------------------end-----------------------------
					// �õ������ĵ�----------------start--------------------------
					double pluse = detail.getDouble("PLUSE", exa + (j - 1) * 6
							- 1);
					// ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
					int pluseHorizontal = 0;
					int pluseVertical = 0;
					if (pluse != 0) {
						// continue;
						// �õ������ĺ������꣨�㣩
						pluseHorizontal = countHorizontal(j, exa);
						if (type.equals("01"))// ������
							pluse = pluse - 40;
						//start machao ���������µ���������λ�ô���ԭ��  ��������
						if(type.equals("N")){
							pluse = pluse - 40;
						}
						//end machao
						pluseVertical = (int) (getVertical(pluse, "PL") + 0.5); // ȡ��
						int dataPluse[] = new int[] {};
						// �õ�һ���������
						dataPluse = new int[] { pluseHorizontal, pluseVertical,
								6, 6, 4 };
						for (int k = 0; k < dotList_T.size(); k++) {
							if (pluseHorizontal == ((int[]) dotList_T.get(k))[0]
									&& pluseVertical == ((int[]) dotList_T
											.get(k))[1]) {
								// �õ�һ���������
								dataPluse = new int[] { pluseHorizontal,
										pluseVertical, 6, 6, 6 };
								break;
							}
						}
						// �������е�
						dotList_PL.add(dataPluse);
					}
					// ---------------------------end----------------------------
					// �õ�����--------------------start--------------------------
					respireValue.add(detail.getValue("RESPIRE", exa + (j - 1)
							* 6 - 1));
					/*
					 * Double respire = Double.parseDouble( ( (Vector)
					 * respireV.get( (exa + (j - 1) * 6) - 1)).get(0) + "");
					 * //��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻��� if (respire == 0)
					 * continue; //�õ������ĺ������꣨�㣩 int respireHorizontal =
					 * countHorizontal(j, exa); int respireVertical = (int)
					 * (getVertical(respire,"R") + 0.5); //ȡ�� //�õ�һ��������� int
					 * dataRespire[] = new int[] { respireHorizontal,
					 * respireVertical, 6, 6, 4}; //�������е�
					 * dotList_R.add(dataRespire);
					 */
					// ----------------------------end---------------------------
					// �õ����ʵĵ�----------------start--------------------------
					double heartRate = detail.getDouble("HEART_RATE",
							(exa + (j - 1) * 6) - 1);
					// ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
					int heartRateHorizontal = 0;
					int heartRateVertical = 0;
					if (heartRate != 0) {
						// continue;
						// �õ����ʵĺ������꣨�㣩
						heartRateHorizontal = countHorizontal(j, exa);
						heartRateVertical = (int) (getVertical(heartRate, "H") + 0.5); // ȡ��
						// �õ�һ���������
						int dataHeartRate[] = new int[] { heartRateHorizontal,
								heartRateVertical, 6, 6, 6 };
						// �������е�
						dotList_H.add(dataHeartRate);
					}
					// ---------------------------end----------------------------
					// �õ�������-----------------start--------------------------
					String tempPhsi = detail.getValue("PHYSIATRICS", exa
							+ (j - 1) * 6 - 1);
					if (!StringUtil.isNullString(tempPhsi)) {
						// �õ��������͵�
						double phsiatrics = TCM_Transform.getDouble(tempPhsi);
						if (phsiatrics <= 35) {
							// ��ͱ߽�35��
							phsiatrics = 35;
						}
						// �õ����µĺ������꣨�㣩
						int phsiHorizontal = countHorizontal(j, exa);
						int phsiVertical = (int) (getVertical(phsiatrics, "P") + 0.5); // ȡ��
						// �õ�һ���������
						int dataPhsi[] = new int[] { phsiHorizontal,
								phsiVertical, 6, 6, 6 };
						// �������е�
						dotList_P.add(dataPhsi);
						// �õ���������-----------start--------------------------
						int dataTempLine[] = new int[] { temperHorizontal + 3,
								temperVertical + 3, phsiHorizontal + 3,
								phsiVertical + 3, 1 };
						lineList_P.add(dataTempLine);
					}
					// ----------------------------end---------------------------
					// �õ�Ϊ����ԭ��
					String not = nullToEmptyStr(detail.getValue(
							"NOTPRREASONCODE", (exa + (j - 1) * 6) - 1));
					if (!StringUtil.isNullString(not)) {
						String sql1 = " SELECT CHN_DESC "
								+ " FROM SYS_DICTIONARY"
								+ " WHERE GROUP_ID='SUM_NOTMPREASON'"
								+ " AND   ID = '" + not + "'";
						TParm result1 = new TParm(TJDODBTool.getInstance()
								.select(sql1));
						controlPage.addData("REASON" + (exa + (j - 1) * 6),
								result1.getValue("CHN_DESC", 0));
					}
					// �õ����µ���----------------start--------------------------
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// if(countWord ==0 ||
						// StringTool.getDateDiffer(StringTool.getTimestamp(""+examineDate.get(countWord),"yyyyMMdd"),
						// StringTool.getTimestamp(""+examineDate.get(countWord
						// - 1),"yyyyMMdd")) <= 1 ||
						// exa != 1)
						if (xT != -1 && yT != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward)) {
							int dataTempLine[] = new int[] { xT + 3, yT + 3,
									temperHorizontal + 3, temperVertical + 3, 1 };
							lineList_T.add(dataTempLine);
						}
						xT = temperHorizontal;
						yT = temperVertical;
					}
					// temForward = temper;
					if (temper != 0
							|| (temper == 0 && !StringUtil.isNullString(not))) {
						notForward = not;
					}
					// --------------------------end----------------------------
					// �õ���������----------------start--------------------------
					if (pluse != 0) {
						if (xPL != -1 && yPL != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward2)) {
							int dataPluseLine[] = new int[] { xPL + 3, yPL + 3,
									pluseHorizontal + 3, pluseVertical + 3, 1 };
							lineList_PL.add(dataPluseLine);
						}
						xPL = pluseHorizontal;
						yPL = pluseVertical;
					}
					// temForward = temper;
					if (pluse != 0
							|| (pluse == 0 && !StringUtil.isNullString(not))) {
						notForward2 = not;
					}
					// --------------------------end----------------------------
					// �õ���������----------------start--------------------------
					/*
					 * if (xR != -1 && yR != -1 && "null".equals(not)) { int
					 * dataRespireLine[] = new int[] { xR + 3, yR + 3,
					 * respireHorizontal + 3, respireVertical + 3, 1};
					 * lineList_R.add(dataRespireLine); } xR =
					 * respireHorizontal; yR = respireVertical;
					 */
					// --------------------------end----------------------------
					// �õ����ʵ���----------------start--------------------------
					if (heartRate != 0) {
						if (xH != -1 && yH != -1
								&& StringUtil.isNullString(not)) {
							int dataHeartRateLine[] = new int[] { xH + 3,
									yH + 3, heartRateHorizontal + 3,
									heartRateVertical + 3, 1 };
							lineList_H.add(dataHeartRateLine);
						}
						xH = heartRateHorizontal;
						yH = heartRateVertical;
					}
					// --------------------------end----------------------------
					// ���˶�̬��Ϣ----------------start--------------------------
					String ptMoveCode = nullToEmptyStr(detail.getValue(
							"PTMOVECATECODE", exa + (j - 1) * 6 - 1));
					if (!StringUtil.isNullString(ptMoveCode)) {
						String ptMoveDesc = nullToEmptyStr(detail.getValue(
								"PTMOVECATEDESC", exa + (j - 1) * 6 - 1));
						controlPage.addData("MOVE" + (exa + (j - 1) * 6),
								ptMoveCode + "||" + ptMoveDesc);
					}
				}
				// �õ�����-------------------------start-------------------------
				String tenmpDate = examineDate.get(countWord++).toString();
				String fomatDate = "";
				if (countWord - 1 == 0) {
					fomatDate = tenmpDate.substring(0, 4) + "."
							+ tenmpDate.substring(4, 6) + "."
							+ tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				} else {
					String tenmpDateForward = examineDate.get(countWord - 2)
							.toString();
					if (!tenmpDateForward.substring(2, 4).equals(
							tenmpDate.substring(2, 4)))
						fomatDate = tenmpDate.substring(0, 4) + "."
								+ tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else if (!tenmpDateForward.substring(4, 6).equals(
							tenmpDate.substring(4, 6)))
						fomatDate = tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else
						fomatDate = tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				}
				controlPage.addData("OPEDAY" + j,
						master.getData("OPE_DAYS", opeDaysVC++));
				// ��Ժ����ʱ��
				Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
				// �õ��ó���������������-�������ӣ�-------------------------------
				int dates = getBornDateDiffer(tenmpDate,
						StringTool.getTimestampDate(inDate)) + 1;
				// dates = getInHospDaysE();
				controlPage.addData("INDATE" + j, dates == 0 ? "" : dates);
				// ��������OPEDAYn
				// -----------------------------------
				// �õ��������������----------------------start------------------------
				for (int k = 0; k < respireValue.size(); k++) {
					try {
						controlPage.addData(
								"L1_" + j + (k + 1),
								(int) Double.parseDouble(""
										+ clearZero(respireValue.get(k))));
					} catch (Exception e) {
						controlPage.addData("L1_" + j + (k + 1),
								clearZero(respireValue.get(k)));
					}
				}
				if (sumType.equals("P")) {
					if (StringUtil.isNullString(master.getValue("STOOL",
							cAutoStool))
							&& StringUtil.isNullString(master.getValue("ENEMA",
									cEnema))) {
						controlPage.addData("L2" + j, "");
						controlPage.addData("L12" + j, "");
						controlPage.addData("L13" + j, "");
						controlPage.addData("L14" + j, "");
					} else {
						controlPage.addData("L2" + j,
								master.getData("STOOL", cAutoStool));
						if (StringUtil.isNullString(master.getValue("ENEMA",
								cEnema))) {
							controlPage.addData("L12" + j, "");
							controlPage.addData("L13" + j, "");
							controlPage.addData("L14" + j, "");
						} else {
							controlPage.addData("L12" + j,
									master.getValue("ENEMA", cEnema));
							controlPage.addData("L13" + j, "/");
							controlPage.addData("L14" + j, "E");
						}
					}
				}else{
					//���Ӵ����ʾ
					onAddCorNStool(controlPage,master.getRow(cAutoStool),j, type);
				}
				cAutoStool++;
				cEnema++;

				if ((clearZero(MorningfinalSystol.get(c3S)) + "").length() == 0
						&& (clearZero(MorningfinalDiastol.get(c3D)) + "")
								.length() == 0)
					controlPage.addData("L3", "");
				else
					controlPage.addData("L3" + j, MorningfinalSystol.get(c3S)
							+ "/" + MorningfinalDiastol.get(c3D));

				if ((clearZero(AfternoonfinalSystol.get(c3S)) + "").length() == 0
						&& (clearZero(AfternoonfinalDiastol.get(c3D)) + "")
								.length() == 0)
					controlPage.addData("L3", "");
				else
					controlPage.addData("L31" + j,
							AfternoonfinalSystol.get(c3S) + "/"
									+ AfternoonfinalDiastol.get(c3D));
				c3S++;
				c3D++;

				// ��ͯ
				if (type.equals("N")) {
					controlPage.addData(
							"L8" + j,
							clearZero(StringTool.round(
									master.getDouble("WEIGHT_G", c6++), 1)));
				} else if (type.equals("C")) {
					controlPage.addData(
							"L8" + j,
							clearZero(StringTool.round(
									master.getDouble("WEIGHT", c6++), 1)));
				} else {
					controlPage.addData("L7" + j,
							clearZero(master.getData("WEIGHT", c6++)));
				}
				// ������ ����ͯ
				if (type.equals("N") || type.equals("C")) {
					controlPage.addData("L7" + j,
							clearZero(master.getData("HEIGHT", c7++)));
					//ĸ����
					String bmflg=master.getValue("BM_FLG", c4++);
					String bmstr=bmflg.equals("Y")?"ĸ��":"";
					Object objin=clearZero(master.getData("INTAKEFLUIDQTY", c99++));
					// modify by wangb 2017/08/04 ����ѡĸ�飬ֻ��ʾ�������ɣ�������ʾ��λ
					if (type.equals("C")) {
						if (bmflg.equals("Y")) {
							if (String.valueOf(objin).length() == 0) {
								controlPage.addData("L4" + j, bmstr);
							} else {
								controlPage.addData("L4" + j, bmstr + "+"
										+ objin);
							}
						} else {
							controlPage.addData("L4" + j, objin.toString());
						}
					} else {
						controlPage.addData("L4" + j,
								objin.toString().length()>0?bmstr+objin+"ml":"");
					}
					controlPage.addData("L5" + j,
							clearZero(master.getData("OUTPUTURINEQTY", c5++)));
					String obj8 = master.getValue("URINETIMES", c8++);// С��
					Object obj8num=clearZero(master.getValue("URINETIMES_NUM", c98++));
					// modify by wangb 2017/08/04 С�����Ϊ0��յ���������ֵʱ����Բ���ܻ��ߣ���ʾʱֻ����ʾ����
					if (obj8 == null || obj8.equals("")) {
						if ("C".equals(type)
								&& String.valueOf(obj8num).length() > 0) {
							controlPage.addData("L9" + j, obj8num + "ml");
						} else {
							controlPage.addData("L9" + j, "");
						}
					} else {
						if ("C".equals(type) && "0".equals(obj8)
								&& String.valueOf(obj8num).length() > 0) {
							controlPage.addData("L9" + j, obj8num + "ml");
						} else {
							controlPage.addData("L9" + j, obj8
									+ "��"
									+ (obj8num.equals("") ? "" : "+" + obj8num
											+ "ml"));
						}
					}
					Object obj9 = master.getValue("VOMIT", c9++);// Ż��
					if (obj9 == null || obj9.equals(""))
						controlPage.addData("L2" + j, "");
					else
						controlPage.addData("L2" + j, obj9);
					Object obj10 = master.getData("HEAD_CIRCUM", c10++);
					if (obj10 == null || obj10.equals(""))
						controlPage.addData("L11" + j, "");
					else
						controlPage.addData("L11" + j,
								StringTool.round(TypeTool.getDouble(obj10), 1));
					Object obj11 = master.getData("ABDOMEN_CIRCUM", c11++);
					if (obj11 == null || obj11.equals(""))
						controlPage.addData("L6" + j, "");
					else
						controlPage.addData("L6" + j,
								StringTool.round(TypeTool.getDouble(obj11), 1));
					// add by wangb 2017/08/07 �����Զ��������Ŀ����Ҫ��ʾ�����µ��� START
					if (type.equals("C")) {
						for (int n = 1; n <= 3; n++) {
							// ��ǻ����
							if ("2".equals(master.getValue("USER_DEFINE_CODE_" + n,
									userDefine))) {
								controlPage.addData("L15" + j, master.getData(
										"USER_DEFINE_" + n + "_VALUE",
										userDefine));
							}
							// T�͹�����
							if ("3".equals(master.getValue("USER_DEFINE_CODE_" + n,
									userDefine))) {
								controlPage.addData("L16" + j, master.getData(
										"USER_DEFINE_" + n + "_VALUE",
										userDefine));
							}
							// θ����ѹ
							if ("4".equals(master.getValue("USER_DEFINE_CODE_" + n,
									userDefine))) {
								controlPage.addData("L17" + j, master.getData(
										"USER_DEFINE_" + n + "_VALUE",
										userDefine));
							}
						}
						userDefine++;
					}
					// add by wangb 2017/08/07 �����Զ��������Ŀ����Ҫ��ʾ�����µ��� END
				} else if (type.equals("P")) {// ����
					controlPage.addData("L4" + j,
							clearZero(master.getData("INTAKEFLUIDQTY", c4++)));
					controlPage.addData("L5" + j,
							clearZero(master.getData("OUTPUTURINEQTY", c5++)));
					controlPage.addData("L6" + j,
							clearZero(master.getData("HEIGHT", c7++)));
					controlPage.addData("L11" + j,
							clearZero(master.getData("DRAINAGE", cDrainage++)));
					controlPage.addData("L80", "����δ��");
					String obj8 = master.getValue("WEIGHT_REASON", c8++);
					if (obj8 == null || obj8.equals(""))
						controlPage.addData("L8" + j, "");
					else
						controlPage.addData("L8" + j, getWeightReason(obj8));

					for (int l = 0; l < master.getCount("USER_DEFINE_1")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_1", l)))
							continue;
						controlPage.addData("L90",
								master.getData("USER_DEFINE_1", l));
						break;
					}
					Object obj9 = master.getData("USER_DEFINE_1_VALUE", c9++);
					if (obj9 == null || obj9.equals(""))
						controlPage.addData("L9" + j, "");
					else
						controlPage.addData("L9" + j, obj9);
					for (int l = 0; l < master.getCount("USER_DEFINE_2")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_2", l)))
							continue;
						controlPage.addData("L100",
								master.getData("USER_DEFINE_2", l));
						break;
					}
					Object obj10 = master.getData("USER_DEFINE_2_VALUE", c10++);
					if (obj10 == null || obj10.equals(""))
						controlPage.addData("L10" + j, "");
					else
						controlPage.addData("L10" + j, obj10);
				} else if (type.equals("")) {
					controlPage.addData("L4" + j,
							clearZero(master.getData("INTAKEFLUIDQTY", c4++)));
					controlPage.addData("L5" + j,
							clearZero(master.getData("OUTPUTURINEQTY", c5++)));
					controlPage.addData("L6" + j,
							clearZero(master.getData("HEIGHT", c7++)));
					controlPage.addData("L11" + j,
							clearZero(master.getData("DRAINAGE", cDrainage++)));
					for (int l = 0; l < master.getCount("USER_DEFINE_1")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_1", l)))
							continue;
						controlPage.addData("L80",
								master.getData("USER_DEFINE_1", l));
						break;
					}
					Object obj8 = master.getData("USER_DEFINE_1_VALUE", c8++);
					if (obj8 == null || obj8.equals(""))
						controlPage.addData("L8" + j, "");
					else
						controlPage.addData("L8" + j, obj8);
					for (int l = 0; l < master.getCount("USER_DEFINE_2")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_2", l)))
							continue;
						controlPage.addData("L90",
								master.getData("USER_DEFINE_2", l));
						break;
					}
					Object obj9 = master.getData("USER_DEFINE_2_VALUE", c9++);
					if (obj9 == null || obj9.equals(""))
						controlPage.addData("L9" + j, "");
					else
						controlPage.addData("L9" + j, obj9);
					for (int l = 0; l < master.getCount("USER_DEFINE_3")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_3", l)))
							continue;
						controlPage.addData("L100",
								master.getData("USER_DEFINE_3", l));
						break;
					}
					Object obj10 = master.getData("USER_DEFINE_3_VALUE", c10++);
					if (obj10 == null || obj10.equals(""))
						controlPage.addData("L10" + j, "");
					else
						controlPage.addData("L10" + j, obj10);
				}
			}
			// ���µ�
			int pageDataForT[][] = new int[dotList_T.size()][5];
			for (int j = 0; j < dotList_T.size(); j++)
				pageDataForT[j] = (int[]) dotList_T.get(j);
			// ������
			int pageDataForPL[][] = new int[dotList_PL.size()][5];
			for (int j = 0; j < dotList_PL.size(); j++)
				pageDataForPL[j] = (int[]) dotList_PL.get(j);
			// ����
			int pageDataForR[][] = new int[dotList_R.size()][5];
			for (int j = 0; j < dotList_R.size(); j++)
				pageDataForR[j] = (int[]) dotList_R.get(j);
			// ����
			int pageDataForH[][] = new int[dotList_H.size()][5];
			for (int j = 0; j < dotList_H.size(); j++)
				pageDataForH[j] = (int[]) dotList_H.get(j);
			// �����µ�
			int pageDataForP[][] = new int[dotList_P.size()][5];
			for (int j = 0; j < dotList_P.size(); j++)
				pageDataForP[j] = (int[]) dotList_P.get(j);
			// ������
			int pageDataForTLine[][] = new int[lineList_T.size()][5];
			for (int j = 0; j < lineList_T.size(); j++) {
				pageDataForTLine[j] = (int[]) lineList_T.get(j);
			}
			// ������
			int pageDataForPLLine[][] = new int[lineList_PL.size()][5];
			for (int j = 0; j < lineList_PL.size(); j++)
				pageDataForPLLine[j] = (int[]) lineList_PL.get(j);
			// ������
			int pageDataForRLine[][] = new int[lineList_R.size()][5];
			for (int j = 0; j < lineList_R.size(); j++)
				pageDataForRLine[j] = (int[]) lineList_R.get(j);
			// ������
			int pageDataForPLine[][] = new int[lineList_P.size()][5];
			for (int j = 0; j < lineList_P.size(); j++)
				pageDataForPLine[j] = (int[]) lineList_P.get(j);
			// ������
			int pageDataForHLine[][] = new int[lineList_H.size()][5];
			for (int j = 0; j < lineList_H.size(); j++) {
				pageDataForHLine[j] = (int[]) lineList_H.get(j);
			}
			controlPage.addData("TEMPDOT", pageDataForT);
			controlPage.addData("PLUSEDOT", pageDataForPL);
			// controlPage.addData("RESPIREDOT", pageDataForR);
			controlPage.addData("PHSIDOT", pageDataForP);
			controlPage.addData("RESPIREDOT", pageDataForH);
			controlPage.addData("TEMPLINE", pageDataForTLine);
			controlPage.addData("PLUSELINE", pageDataForPLLine);
			// controlPage.addData("RESPIRELINE", pageDataForRLine);
			controlPage.addData("PHSILINE", pageDataForPLine);
			controlPage.addData("RESPIRELINE", pageDataForHLine);
			// ----------------------------end----------------------------------
		}
		// ����ҳ��
		controlPage.setCount(pageCount);
		controlPage.addData("SYSTEM", "COLUMNS", "PAGE");
		mainPrtData.setData("TABLE", controlPage.getData());
		return mainPrtData;
	}

	/**
	 * ��Ӵ������
	 * 
	 * @param parm
	 */
	public void onAddCorNStool(TParm controlPage, TParm parm, int num, String type) {
		StringBuffer Stool = new StringBuffer();
		StringBuffer Enema = new StringBuffer();
		// modify by wangb 2017/08/04 ��ͯ���µ��������޴��ʱ��Ĭ��Ϊ0��Ӧ��ʾΪ��0��
		if ("C".equals(type)) {
			// ����
			if (!"".equals(parm.getValue("STOOL"))) {
				Stool.append(parm.getValue("STOOL"));
			}
		} else {
			// ����
			if (!clearZero(parm.getValue("STOOL")).equals("")) {
				Stool.append(parm.getValue("STOOL"));
			}
		}
		// ϡ����
		if (!clearZero(parm.getValue("STOOLX_NUM")).equals("")
				&& !parm.getValue("STOOLX").equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			
			// modify by wangb 2017/08/04 �д����ʱ����Ҫ��ʾ���ĵ�λ��ml��
			if ("C".equals(type)) {
				Stool.append(parm.getValue("STOOLX_NUM")
						+ "ml"
						+ "/"
						+ (parm.getValue("STOOLX").equals("1") ? "" : parm
								.getValue("STOOLX")) + "X");
			} else {
				Stool.append(parm.getValue("STOOLX_NUM")
						+ "/"
						+ (parm.getValue("STOOLX").equals("1") ? "" : parm
								.getValue("STOOLX")) + "X");
			}
		} else if (clearZero(parm.getValue("STOOLX_NUM")).equals("")
				&& !clearZero(parm.getValue("STOOLX")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			Stool.append((parm.getValue("STOOLX").equals("1") ? "" : parm
					.getValue("STOOLX")) + "X");
		}
		// ϡˮ��
		if (!clearZero(parm.getValue("STOOLW_NUM")).equals("")
				&& !clearZero(parm.getValue("STOOLW")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			// modify by wangb 2017/08/04 �д����ʱ����Ҫ��ʾ���ĵ�λ��ml��
			if ("C".equals(type)) {
				Stool.append(parm.getValue("STOOLW_NUM")
						+ "ml"
						+ "/"
						+ (parm.getValue("STOOLW").equals("1") ? "" : parm
								.getValue("STOOLW")) + "��");
			} else {
				Stool.append(parm.getValue("STOOLW_NUM")
						+ "/"
						+ (parm.getValue("STOOLW").equals("1") ? "" : parm
								.getValue("STOOLW")) + "��");
			}
		} else if (clearZero(parm.getValue("STOOLW_NUM")).equals("")
				&& !clearZero(parm.getValue("STOOLW")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			Stool.append((parm.getValue("STOOLW").equals("1") ? "" : parm
					.getValue("STOOLW")) + "��");
		}
		// Ѫ��
		if (!clearZero(parm.getValue("STOOLB")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			Stool.append(parm.getValue("STOOLB") + "Ѫ��");
		}
		StringBuffer EStool = new StringBuffer();
		if (!clearZero(parm.getValue("ENEMA")).equals("")) {
			Enema.append(parm.getValue("ENEMA").equals("1") ? "/E" : "/"+parm
							.getValue("ENEMA") + "E");
			EStool.append("(");
			// ����
			if (!clearZero(parm.getValue("E_STOOL")).equals("")) {
				EStool.append(parm.getValue("E_STOOL"));
			}
			// ϡ����
			if (!clearZero(parm.getValue("E_STOOLX_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLX")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append(parm.getValue("E_STOOLX_NUM")
						+ "/"
						+ (parm.getValue("E_STOOLX").equals("1") ? "" : parm
								.getValue("E_STOOLX")) + "X");
			} else if (clearZero(parm.getValue("E_STOOLX_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLX")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append((parm.getValue("E_STOOLX").equals("1") ? "" : parm
						.getValue("E_STOOLX")) + "X");
			}
			// ϡˮ��
			if (!clearZero(parm.getValue("E_STOOLW_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLW")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append(parm.getValue("E_STOOLW_NUM")
						+ "/"
						+ (parm.getValue("E_STOOLW").equals("1") ? "" : parm
								.getValue("E_STOOLW")) + "��");
			} else if (clearZero(parm.getValue("E_STOOLW_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLW")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append((parm.getValue("E_STOOLW").equals("1") ? "" : parm
						.getValue("E_STOOLW")) + "��");
			}

			// Ѫ��
			if (!clearZero(parm.getValue("E_STOOLB")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append(parm.getValue("E_STOOLB") + "Ѫ��");
			}
			EStool.append(")");
		}
		
		//MODIFY BY YANGJJ 20150922
		//if(Enema.toString().length()>0){
		// modify by wangb 2017/08/04 ��ͯ���µ����������ֻ��д��������ʱ��ֻ��ʾ���ּ��ɣ�������ʾ��+����
		if ("C".equals(type)) {
			if (Stool.toString().length() > 0
					&& (!"0".equals(parm.getValue("STOOLX")) && !"0"
							.equals(parm.getValue("STOOLW")))) {
				Stool.append("+");
			}
		} else {
			if (Stool.toString().length() > 0) {
				Stool.append("+");
			}
		}
		
		controlPage.addData("L10" + num, Stool.toString());
		controlPage.addData("L12" + num, EStool.toString());
		controlPage.addData("L14" + num, Enema.toString());
	}

	public Object clearZero(Object obj) {
		try {
			if (obj == null)
				return "";
			double mun = Double.parseDouble("" + obj);
			if (mun == 0)
				return "";
			else
				return obj;
		} catch (NumberFormatException e) {
			return obj;
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getWeightReason(String id) {
		String sql = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_WEIGHT_REASON' AND ID='"
				+ id + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("CHN_DESC", 0);
	}

	/**
	 * �õ�����������Ĳ�
	 * 
	 * @param nowDate
	 *            String
	 * @return int
	 */
	public int getBornDateDiffer(String date, Timestamp bornDate) {
		Timestamp nowDate = StringTool.getTimestamp(date, "yyyyMMdd");
		return StringTool.getDateDiffer(nowDate, bornDate);
	}

	/**
	 * �����������λ��
	 * 
	 * @param date
	 *            int
	 * @param examineSession
	 *            int
	 * @return int
	 */
	private int countHorizontal(int date, int examineSession) {
		// int adaptX = 7;
		// return 10 * ( (date - 1) * 6 + examineSession) - adaptX;
		int adaptX = 7;
		return 8 * ((date - 1) * 6 + examineSession) - adaptX;
	}

	/**
	 * �������µ�����������
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	private double getVertical(double value, String flag) {
		int adaptY = 8;
		// ���»���������
		if ("T".equals(flag) || "P".equals(flag))
			return (445 - countVertical(value, 42, 34, 40) * 10) - adaptY;
		// ����
		if ("PL".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		// ����
		if ("H".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		return -1;
	}

	/**
	 * �����������λ��--����
	 * 
	 * @param value
	 *            int ���ݿ��м�¼������
	 * @param topValue
	 *            int ���������ֵ--��
	 * @param butValue
	 *            int �������С��ֵ--��
	 * @param level
	 *            int �������С֮����ж��ٵȼ�-����
	 * @return int
	 */
	private double countVertical(double value, double topValue,
			double butValue, int level) {
		return (value - butValue) / ((topValue - butValue) / level) - 1;
	}

	/**
	 * �õ���Ҫ��ӡ��������
	 * 
	 * @param date
	 *            TParm
	 */
	public Vector getVitalSignDate(TParm date) {
		Vector tprSign = new Vector();
		date.setData("ADM_TYPE", admType);
		date.setData("CASE_NO", caseNo);
		// ���£���������������
		TParm vitalSignMstParm = SUMVitalSignTool.getInstance().selectdataMst(
				date);
		TParm vitalSignDtlParm = SUMVitalSignTool.getInstance().selectdataDtl(
				date);
		// ������ǽ����0-������Ϣ 1-ϸ����Ϣ
		tprSign.add(vitalSignMstParm);
		tprSign.add(vitalSignDtlParm);
		return tprSign;
	}

	/**
	 * �õ��������
	 * 
	 * @return
	 */
	public String onAgeCode(String CaseNo, String AdmType) {
		String sumKind = "";
		String age = "";
		if (AdmType.equals("I")) {
			Timestamp indate = patInfo.getTimestamp("IN_DATE", 0);
			Pat pat = Pat.onQueryByMrNo(patInfo.getValue("MR_NO", 0));
			Timestamp birth = pat.getBirthday();
			age = DurationFormatUtils.formatPeriod(birth.getTime(),
					indate.getTime(), "y-M-d-H-m-s");
		}
		if (age.equals(""))
			return sumKind;
		String sql = "SELECT  AGE_CODE,START_AGE,START_MONTH,START_DAY,START_HOUR,"
				+ " END_AGE,END_MONTH,END_DAY,END_HOUR,SHOW_TYPE,SUM_JHW "
				+ " FROM SYS_AGE";
		// System.out.println("=====age===="+age);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0)
			return sumKind;
		for (int i = 0; i < parm.getCount(); i++) {
			String SUM_JHW = parm.getValue("SUM_JHW", i);
			if (checkAge(parm.getRow(i), age)) {
				sumKind = SUM_JHW;
				break;
			}
		}
		return sumKind;
	}

	/**
	 * �����������Ƿ����
	 * 
	 * @param parm
	 * @return
	 */
	public boolean checkAge(TParm parm, String age) {
		String[] t = age.split("-");
		int nYear = Integer.valueOf(t[0]);
		int nMonth = Integer.valueOf(t[1]);
		int nDay = Integer.valueOf(t[2]);
		int nHour = Integer.valueOf(t[3]);
		int count = 0;
		String startAge = parm.getValue("START_AGE");
		String startMonth = parm.getValue("START_MONTH");
		String startDay = parm.getValue("START_DAY");
		String startHour = parm.getValue("START_HOUR");
		String endAge = parm.getValue("END_AGE");
		String endMonth = parm.getValue("END_MONTH");
		String endDay = parm.getValue("END_DAY");
		String endHour = parm.getValue("END_HOUR");
		String showType = parm.getValue("SHOW_TYPE");
		String startNum = "";
		String endNum = "";
		if (showType.contains("��")) {
			if (!startAge.equals(""))
				startNum = startAge;
			if (!endAge.equals(""))
				endNum = endAge;

			count = nYear;
		}
		if (showType.contains("��")) {
			if (!startMonth.equals(""))
				startNum = (TypeTool.getInt(startNum) * 12 + TypeTool
						.getInt(startMonth)) + "";
			if (!endMonth.equals(""))
				endNum = (TypeTool.getInt(endNum) * 12 + TypeTool
						.getInt(endMonth)) + "";

			count = (nYear * 12 + nMonth);
		}
		if (showType.contains("��")) {
			if (!startDay.equals(""))
				startNum = (TypeTool.getInt(startNum) * 30 + TypeTool
						.getInt(startDay)) + "";
			if (!endDay.equals(""))
				endNum = (TypeTool.getInt(endNum) * 30 + TypeTool
						.getInt(endDay)) + "";

			count = (nYear * 12 + nMonth) * 30 + nDay;
		}
		if (showType.contains("Сʱ")) {
			if (!startHour.equals(""))
				startNum = (TypeTool.getInt(startNum) * 24 + TypeTool
						.getInt(startHour)) + "";
			if (!endHour.equals(""))
				endNum = (TypeTool.getInt(endNum) * 24 + TypeTool
						.getInt(endHour)) + "";

			count = ((nYear * 12 + nMonth) * 30 + nDay) * 24 + nHour;
		}
		if (count >= TypeTool.getInt(startNum) && endNum.equals("")) {
			return true;
		}
		if (count >= TypeTool.getInt(startNum)
				&& count <= TypeTool.getInt(endNum)) {
			return true;
		}
		return false;
	}

	/**
	 * ������λѡ���¼���
	 */
	public void onWeightSel() {
		if (this.getTRadioButton("W_KG").isSelected()) {
			double wg = TypeTool.getDouble(getValueString("WEIGHT_G")) / 1000;
			this.getTextField("WEIGHT_G").setEnabled(false);
			this.getTextField("WEIGHT").setEnabled(true);
			this.setValue("WEIGHT", wg + "");
			this.setValue("WEIGHT_G", "");
		}
		if (this.getTRadioButton("W_G").isSelected()) {
			double wg = TypeTool.getDouble(getValueString("WEIGHT")) * 1000;
			this.getTextField("WEIGHT").setEnabled(false);
			this.getTextField("WEIGHT_G").setEnabled(true);
			this.setValue("WEIGHT_G", wg + "");
			this.setValue("WEIGHT", "");
		}
	}

	/**
	 * �õ�TRadioButton����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getTRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * TParm�С�null����תΪ���ַ���
	 * 
	 * @param str
	 * @return
	 */
	public String nullToEmptyStr(String str) {
		if (str == null || str.equalsIgnoreCase("null")) {
			return "";
		}
		return str;
	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if (isMroFlg)
			return true;
		return true;
	}

	public static void main(String[] args) {
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sum\\SUMVitalSign.x");
	}
}
