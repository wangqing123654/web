package com.javahis.ui.sys;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title: ҩƷƵ��
 * </p>
 * 
 * <p>
 * Description:ҩƷƵ��
 * </p>
 * 
 * <p>
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSPhaFreqControl extends TControl {

	TParm data;
	int selectRow = -1;
	int selectTimeRow = -1;
	TParm parmData;
	TTable table, trtTable;
	TDataStore allDs, trtDs;

	// ʱ��TAG��
	private final static String TIME_TAG = "0;1;2;3;4;5;6;7;8;9;10;11;12;13;14;15;16;17;18;19;20;21;22;23;24;25;26;27;28;29;30;31;32;33;34;35;36;37;38;39;40;41;42;43;44;45;46;47";
	private final static String TAG = "FREQ_CODE;SEQ;FREQ_CHN_DESC;PY1;PY2;FREQ_ENG_DESC;DESCRIPTION;TAKE_DAYS;NOCOMPUTE_FLG;STAT_FLG;CYCLE;FREQ_TIMES;SUN_FLG;MON_FLG;TUE_FLG;WED_FLG;THUR_FLG;FRI_FLG;STA_FLG";
	private final static String TRT_SQL = "SELECT FREQ_CODE, STANDING_TIME, OPT_USER, OPT_DATE, OPT_TERM FROM SYS_TRTFREQTIME ORDER BY FREQ_CODE,STANDING_TIME";
	private final static String DELETE_TRT_SQL = "DELETE SYS_TRTFREQTIME WHERE FREQ_CODE='";
	private final static String GET_MAX_SEQ = "SELECT MAX(SEQ) AS SEQ FROM SYS_PHAFREQ";
	private final static String ALL_SQL = "SELECT FREQ_CODE, FREQ_CHN_DESC, FREQ_ENG_DESC, PY1, PY2, "
			+ "SEQ, DESCRIPTION, WESMED_FLG, CHIMED_FLG, CYCLE, "
			+ "FREQ_TIMES, FREQ_UNIT_48, MON_FLG, TUE_FLG, WED_FLG, "
			+ "THUR_FLG, FRI_FLG, STA_FLG, SUN_FLG, NOCOMPUTE_FLG, "
			+ "TAKE_DAYS, STAT_FLG, OPT_USER, OPT_DATE, OPT_TERM "
			+ " FROM SYS_PHAFREQ ORDER BY FREQ_CODE,SEQ";
	// String[] tags = TAG.split(";");
	String[] tags;

	public void onInit() {
		super.onInit();
		init();
	}

	/**
	 * ʱ���CHECKBOX����¼����ı�ʱ���TABLE��ֵ��FREQ_TIMES��ֵ
	 * 
	 * @param text
	 *            ʱ���CHECKBOX��ʾ����
	 * @param tag
	 *            ʱ���CHECKBOX��TAG
	 */
	public void onCheckTime(String text, String tag) {
		String code = this.getValueString("FREQ_CODE");
		if (StringUtil.isNullString(code)) {
			this.setValue(tag, "N");
			this.messageBox_("û�п��Բ�����Ƶ��");
			return;
		}
		int freqTimes = 0;
		if (tags == null || tags.length != 48) {
			this.setValue(tag, "N");
			this.messageBox_("����ȡ������");
			return;
		}
		// ����FREQ_TIMES
		String temp;
		for (int i = 0; i < tags.length; i++) {
			temp = this.getValueString(tags[i]);
			if ("Y".equalsIgnoreCase(temp)) {
				freqTimes++;
			}
		}
		this.setValue("FREQ_TIMES", freqTimes);

		temp = this.getValueString(tag);
		// ����ʱ���TABLE
		if (StringTool.getBoolean(temp)) {
			int row = trtDs.insertRow();
			trtDs.setItem(row, "FREQ_CODE", code);
			trtDs.setItem(row, "STANDING_TIME", text);
			trtDs.setItem(row, "OPT_USER", Operator.getID());
			trtDs.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
			trtDs.setItem(row, "OPT_TERM", Operator.getIP());
		} else {
			int row = trtDs.find("STANDING_TIME='" + text + "'");
			if (row < 0) {
				return;
			}
			trtDs.deleteRow(row);
		}
		// ��ʱ���TABLE����
		trtDs.setFilter("FREQ_CODE='" + code + "'");
		trtDs.filter();
		trtDs.setSort("STANDING_TIME ASC");
		// System.out.println("trtDs start-------------------------");
		// trtDs.showDebug();
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
	}

	/**
	 * ɾ������ʱ��Ƶ��
	 */
	public void onDeletePLN() {
		int row = trtTable.getSelectedRow();
		if (row < 0)
			return;
		trtDs.deleteRow(row);
		trtDs.filter();
		trtDs.setSort("STANDING_TIME ASC");
		// System.out.println("trtDs start-------------------------");
		// trtDs.showDebug();
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
	}

	/**
	 * ʱ���TABLE�ĵ���¼�
	 */
	public void onTIMETABLEClicked(int row) {
		if (row < 0)
			return;
		selectTimeRow = row;
		this.callFunction("UI|DEL|setEnabled", true);
	}

	/**
	 * ʱ��TABLEֵ�ı�
	 */
	public boolean onTimeTableChangeValue(TTableNode tNode) {
		String value = TCM_Transform.getString(tNode.getValue());
		if (StringUtil.isNullString(value) || value.length() != 4)
			return true;
		int time;
		try {
			time = StringTool.getInt(value);
			String temp = StringTool.fill0(TCM_Transform.getString(time), 4);
			if (!value.equalsIgnoreCase(temp))
				return true;
		} catch (Exception e) {
			return true;
		}
		if (time < 0 || time > 2359)
			return true;
		trtDs.setItem(tNode.getRow(), "STANDING_TIME", value);
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
		trtTable.getTable().grabFocus();
		if (trtTable.getRowCount() > 0)
			trtTable.setSelectedRow(0);
		return false;
	}

	/**
	 * ��ʼ�����棬��ѯ���е�����
	 * 
	 * @return TParm
	 */
	public void onQuery() {
		String freqCode = this.getValueString("FREQ_CODE");
		if (StringUtil.isNullString(freqCode)) {
			this.messageBox_("Ƶ�δ��벻��Ϊ��");
			return;
		}
		freqCode = freqCode.trim().toUpperCase();
		selectRow = 0;
		allDs.setFilter("FREQ_CODE = '" + freqCode + "'");
		allDs.filter();
		table.setDataStore(allDs);
		table.setDSValue();
		parmData = allDs.getBuffer(allDs.PRIMARY);
		this.setValueForParm(TAG, parmData, 0);
		String time48 = parmData.getValue("FREQ_UNIT_48", 0);
		if (!set48(time48)) {
			this.messageBox_("û�в�ѯ����");
			// onClear();
		}
		setTrtTable(freqCode);
	}

	/**
	 * ���ô���ʱ��TABLE������DATASTORE������һ�У��Ա�����ʱ��
	 * 
	 * @param freqCode
	 *            String
	 */
	public void setTrtTable(String freqCode) {
		trtDs.setFilter("FREQ_CODE='" + freqCode + "'");
		trtDs.filter();
		// System.out.println("setTrtTable-----------");
		// trtDs.showDebug();
		trtDs.setSort("STANDING_TIME ASC");
		trtTable.setDataStore(trtDs);
		trtTable.setDSValue();
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue(TIME_TAG);
		this.clearValue(TAG);
		
		// Ƶ��-��Ĭ��Ϊ1
		this.setValue("CYCLE", 1);
		
		selectRow = -1;
		// trtTable.removeRowAll();
		// table.removeRowAll();
		// allDs.setFilter("");
		// allDs.filter();
		// table.setDataStore(allDs);
		// table.setDSValue();
		// trtDs.setFilter("");
		// trtDs.filter();
		allDs = new TDataStore();
		allDs.setSQL(ALL_SQL);
		allDs.retrieve();
		table.setDataStore(allDs);
		table.setDSValue();
		parmData = allDs.getBuffer(allDs.PRIMARY);
		trtDs = new TDataStore();
		trtDs.setSQL(TRT_SQL);
		trtDs.retrieve();

		callFunction("UI|FREQ_CODE|setEnabled", true);
	}

	/**
	 * ��ʼ�����棬��ѯ���е�����
	 * 
	 * @return TParm
	 */
	public void init() {
		tags = StringTool.parseLine(TIME_TAG, ";");
		table = (TTable) this.getComponent("TABLE");
		trtTable = (TTable) this.getComponent("TRT_TABLE");
		trtTable.addRow();
		allDs = new TDataStore();
		allDs.setSQL(ALL_SQL);
		allDs.retrieve();
		table.setDataStore(allDs);
		table.setDSValue();
		parmData = allDs.getBuffer(allDs.PRIMARY);
		trtDs = new TDataStore();
		trtDs.setSQL(TRT_SQL);
		trtDs.retrieve();
		trtTable.addEventListener("TRT_TABLE->" + TTableEvent.CHANGE_VALUE,
				this, "onTimeTableChangeValue");
		// trtTable.setDataStore(trtDs);
		// onClear();
	}

	/**
	 * ��TABLE����¼�
	 */
	public void onTableClick() {
		int row = table.getSelectedRow();
		selectRow = row;
		this.setValueForParm(TAG, parmData, row);
		String time48 = parmData.getValue("FREQ_UNIT_48", row);
		if (!set48(time48)) {
			this.messageBox_("û�в�ѯ����");
		}
		String freqCode = parmData.getValue("FREQ_CODE", row);
		setTrtTable(freqCode);
		this.getTextField("FREQ_CODE").setEnabled(false);
	}

	/**
	 * ����48��Combo
	 * 
	 * @param time48
	 *            String ʱ���
	 */
	public boolean set48(String time48) {
		char[] char48 = time48.toCharArray();
		if (char48.length != 48)
			return false;
		for (int i = 0; i < char48.length; i++) {
			// System.out.println(i + ":" + char48[i]);
			this.setValue(i + "", char48[i] + "");
		}
		return true;
	}

	/**
	 * ȡ��48ʱ���
	 * 
	 * @return String
	 */
	public String get48() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < tags.length; i++) {
			// System.out.println(tags[i] + ":" + this.getValueString(tags[i]));
			result.append(this.getValueString(tags[i]));
		}
		return result.toString();

	}

	/**
	 * ����
	 */
	public void onSave() {
		TParm result;
		String[] sql;
		String freqCode = this.getValueString("FREQ_CODE").toUpperCase();
		if (StringUtil.isNullString(freqCode)) {
			this.messageBox_("Ƶ�δ��벻��Ϊ��");
			return;
		}
		
		// add by wangqing 201711212 start
		// ������ҩƷƵ���ֵ�ά��Ӧ������ɵ��Щ���������Ѻ�Щ����ӦֵĬ����1�͸����ˣ����õ��˳�����ʻ��ٺܶࡣ������ ֱ�Ӿ�Ĭ����1�����ˣ�   <1����ʱ������ʾ������С��1����
		int cycle = this.getValueInt("CYCLE");
		if(cycle<1){
			this.messageBox("��������-�ղ���С��1");
			return;
		}
		int freqTimes = this.getValueInt("FREQ_TIMES");
		if(freqTimes<1){
			this.messageBox("��������-�β���С��1");
			return;
		}
		// add by wangqing 201711212 end
			
		
		int row = -1;
		if (allDs == null || allDs.rowCount() < 1) {
			return;
		}
		int exist = allDs.find("FREQ_CODE='" + freqCode + "'");
		if (exist < 0)
			row = allDs.insertRow();
		else
			row = exist;
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		// FREQ_CODE
		allDs.setItem(row, "FREQ_CODE", freqCode);
		// FREQ_CHN_DESC
		allDs.setItem(row, "FREQ_CHN_DESC",
				this.getValueString("FREQ_CHN_DESC"));
		// FREQ_ENG_DESC
		allDs.setItem(row, "FREQ_ENG_DESC",
				this.getValueString("FREQ_ENG_DESC"));
		// PY1
		allDs.setItem(row, "PY1", this.getValueString("PY1"));
		// PY2
		allDs.setItem(row, "PY2", this.getValueString("PY2"));
		// SEQ
		allDs.setItem(row, "SEQ", getMaxSeq() + 1);
		// DESCRIPTION
		allDs.setItem(row, "DESCRIPTION", this.getValueString("DESCRIPTION"));
		// CYCLE
		allDs.setItem(row, "CYCLE", this.getValueInt("CYCLE"));
		// FREQ_TIMES
		allDs.setItem(row, "FREQ_TIMES", this.getValueInt("FREQ_TIMES"));
		// FREQ_UNIT_48
		allDs.setItem(row, "FREQ_UNIT_48", this.get48());
		// MON_FLG
		allDs.setItem(row, "MON_FLG", this.getValueString("MON_FLG"));
		// TUE_FLG
		allDs.setItem(row, "TUE_FLG", this.getValueString("TUE_FLG"));
		// WED_FLG
		allDs.setItem(row, "WED_FLG", this.getValueString("WED_FLG"));
		// THUR_FLG
		allDs.setItem(row, "THUR_FLG", this.getValueString("THUR_FLG"));
		// FRI_FLG
		allDs.setItem(row, "FRI_FLG", this.getValueString("FRI_FLG"));
		// STA_FLG
		allDs.setItem(row, "STA_FLG", this.getValueString("STA_FLG"));
		// SUN_FLG
		allDs.setItem(row, "SUN_FLG", this.getValueString("SUN_FLG"));
		// NOCOMPUTE_FLG
		allDs.setItem(row, "NOCOMPUTE_FLG",
				this.getValueString("NOCOMPUTE_FLG"));
		// TAKE_DAYS
		allDs.setItem(row, "TAKE_DAYS", this.getValueInt("TAKE_DAYS"));
		// STAT_FLG
		allDs.setItem(row, "STAT_FLG", this.getValueString("STAT_FLG"));
		// OPT_USER
		allDs.setItem(row, "OPT_USER", Operator.getID());
		// OPT_DATE
		allDs.setItem(row, "OPT_DATE", now);
		// OPT_TERM
		allDs.setItem(row, "OPT_TERM", Operator.getIP());
		sql = allDs.getUpdateSQL();
		sql = StringTool.copyArray(sql, trtDs.getUpdateSQL());
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			// this.messageBox_(result.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		onClear();
	}

	/**
	 * ȡ�����SEQ
	 * 
	 * @return long
	 */
	public int getMaxSeq() {
		TParm result = new TParm(TJDODBTool.getInstance().select(GET_MAX_SEQ));
		// System.out.println("result" + result);
		// System.out.println("dfdfdf" + result.getValue("SEQ", 0));
		int seq = TCM_Transform.getInt(result.getValue("SEQ", 0));
		return seq;
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		if (selectRow < 0) {
			this.messageBox_("û��ѡ����");
			return;
		}
		String code = allDs.getItemString(selectRow, "FREQ_CODE");
		String deleteTrt = DELETE_TRT_SQL + code + "'";
		// this.messageBox_(selectRow);
		// System.out.println("onDelete---------------");
		// allDs.showDebug();
		allDs.deleteRow(selectRow);
		// System.out.println("onDelete---------------");
		// allDs.showDebug();
		String[] sqlTemp = allDs.getUpdateSQL();
		String[] sql = new String[] { deleteTrt };
		sql = StringTool.copyArray(sql, sqlTemp);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			// this.messageBox_(result.getErrText());
			this.messageBox("E0003");
		} else {
			this.messageBox("P0003");
		}
		onClear();
	}

	/**
	 * ���ݺ��ֲ�ѯƴ������ĸ
	 */
	public void onCode() {
		// this.messageBox_(getMaxSeq());
		this.setValue("SEQ", getMaxSeq());
		TNumberTextField seq = (TNumberTextField) this.getComponent("SEQ");
		seq.grabFocus();
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

}
