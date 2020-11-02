package com.javahis.ui.ins;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.ins.INSNoticeTool;
import jdo.ins.InsManager;
import jdo.reg.RegMethodTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.javahis.util.DateUtil;

/**
 * 
 * <p>
 * Title: ����֪ͨ����
 * </p>
 * 
 * <p>
 * Description:����֪ͨ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSNoticeControl extends TControl {
	TParm data;
	int selectRow = -1;
	/**
	 * DataDown_rs ���зָ���
	 */
	private String DATADOWN_RS_S_ROW_SPLIT = "";

	/**
	 * DataDown_rs ���зָ���
	 */
	private String DATADOWN_RS_S_COLUMN_SPLIT = "\\|";
	/**
	 * ����Ȩ��
	 */
	public static String INS_NOTICE_CONTROL_DOWNLOAD = "INS_NOTICE_CONTROL_DOWNLOAD";
	/**
	 * ��ѯȨ��
	 */
	public static String INS_NOTICE_CONTROL_SREACH = "INS_NOTICE_CONTROL_SREACH";
	/**
	 * ��ǰ�û��Ƿ�ӵ������Ȩ��
	 */
	boolean isPossessDownload = false;
	private TTable table;

	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	TParm regionParm = null;
	public void onInit() {
		super.onInit();
		((TTable) getComponent("Table")).addEventListener("Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		setPossessDownload();
		regionParm=SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		table = ((TTable) getComponent("Table"));
		onClear();

	}

	/**
	 * ���õ�ǰ��¼�û�Ȩ��
	 */
	private void setPossessDownload() {
		isPossessDownload = this.getPopedem("LEADER");
		// Ĭ��Ȩ�� ��ѯ
		// isPossessDownload = false;
		// ����Ȩ��
		// isPossessDownload = true;

	}

	/**
	 * ���Ӷ�Table�ļ��� delete
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// ѡ����
		if (row < 0)
			return;
		TParm data=table.getParmValue();
		setValueForParm("REP_DESC", data, row);
		selectRow = row;
	}

	/**
	 * ����
	 */
	public void onSave() {
		
		table.acceptText();
		TParm tableParm = table.getParmValue();
		TParm result = new TParm();
		TParm saveTParm = new TParm();
		int saveCount = 0;
		for (int i = 0; i < table.getParmValue().getCount("REP_NO"); i++) {

			if (tableParm.getBoolean("SAVE_FLG", i)) {
				saveTParm.addData("REP_NO", tableParm
						.getData("REP_NO", i));
				saveTParm.addData("REP_CODE", tableParm.getData(
						"REP_CODE", i));
				saveTParm.addData("REP_DATE", tableParm.getData(
						"REP_DATE", i));
				saveTParm.addData("PUBLISH_FLG", tableParm.getData(
						"PUBLISH_FLG", i));
				saveTParm.addData("REP_DESC", tableParm.getData(
						"REP_DESC", i));
				saveTParm.addData("OPT_USER", Operator.getID());
				saveTParm.addData("OPT_TERM", Operator.getIP());
				saveCount++;

			}
		}
		saveTParm.setCount(saveCount);
		result = TIOM_AppServer.executeAction("action.ins.INSNoticeAction",
				"onSave", saveTParm);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		this.messageBox("P0005");

		// onUpdate(tableParm);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryTParm = new TParm();
		TTable tTable = ((TTable) getComponent("Table"));
		tTable.setHeader("����,40,boolean;֪ͨ���,180;֪ͨ����,320;֪ͨʱ��,120");
		tTable.setParmMap("PUBLISH_FLG;REP_NO;REP_CODE;REP_DATE;REP_DESC");
		tTable.setLockColumns("0,1,2,3,4");

		tTable.setColumnHorizontalAlignmentData("2,left;3,left;4,left;5,left");
		// tTable.setColumnHorizontalAlignment(1, 0);
		clearValue("NOTICE_DESC");
		queryTParm.setData("START_DATE", getText("START_DATE"));
		queryTParm.setData("END_DATE", getText("END_DATE"));

		data = INSNoticeTool.getInstance().selectdata(queryTParm);
		// �жϴ���ֵ
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		if (data.getCount()<=0) {
			messageBox("û�в�ѯ������");
			((TTable) getComponent("Table")).setParmValue(new TParm());
			return;
		}
		((TTable) getComponent("Table")).setParmValue(data);
	}

	/**
	 * ����
	 */
	public void publishCheckBox() {

		boolean selected = ((TCheckBox) getComponent("publishCheckBox"))
				.isSelected();
		String publishFlgVlaue = "N";
		if (selected) {
			publishFlgVlaue = "Y";
		}
		TTable table = ((TTable) getComponent("Table"));
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setItem(i, "PUBLISH_FLG", publishFlgVlaue);

		}
	}

	/**
	 * ȫѡ
	 */
	public void saveCheckBox() {

		boolean selected = ((TCheckBox) getComponent("saveCheckBox"))
				.isSelected();
		String saveFlgVlaue = "N";
		if (selected) {
			saveFlgVlaue = "Y";
		}
		TTable table = ((TTable) getComponent("Table"));
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			table.setItem(i, "SAVE_FLG", saveFlgVlaue);

		}
	}

	/**
	 * ��ְ
	 * 
	 * @param parm
	 * @return
	 */
	private TParm DataDown_rs_T(TParm parm) {
		
		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "T");
		TParm result = InsManager.getInstance().safe(parm, "");
		//TParm result = dataDownToParm(dataDown_rs_T, columnNameList);
		return result;
	}
	/**
	 * �Ǿ�
	 * @param parm
	 * @return
	 */
	private TParm DataDown_czyd_L(TParm parm){
		parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "L");
		TParm result = InsManager.getInstance().safe(parm, "");
		return result;
	}
	private TParm testInsManagerL(TParm parmSreach) {
		
		String hospital = regionParm.getData("NHI_NO", 0).toString();// ��ȡHOSP_NHI_NO
		parmSreach.addData("HOSP_NHI_NO",hospital);
		parmSreach.addData("PARM_COUNT", 3);
		TParm result=null;
		if (this.getValue("INS_TYPE").toString().equals("1")) {
			result=DataDown_rs_T(parmSreach);
		}else if(this.getValue("INS_TYPE").toString().equals("2")){
			result=DataDown_czyd_L(parmSreach);
		}
		for (int i = 0; i < result.getCount("REP_CODE"); i++) {
			result.addData("SAVE_FLG",false);
			result.addData("PUBLISH_FLG",false);
		}
		return result;
	}

	/**
	 * ����
	 */
	public void onDownload() {
		TParm parm = new TParm();
		parm.addData("START_DATE", getText("START_DATE").replace("/", ""));
		parm.addData("END_DATE", getText("END_DATE").replace("/", ""));
		if(this.getValue("INS_TYPE").toString().length()<=0){
			this.messageBox("��������Ⱥ���");
			return;
		}
		TParm data = testInsManagerL(parm);
		// �жϴ���ֵ
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		((TTable) getComponent("Table")).setParmValue(data);
		((TCheckBox) getComponent("publishCheckBox")).setEnabled(true);
		((TCheckBox) getComponent("saveCheckBox")).setEnabled(true);
		((TCheckBox) getComponent("publishCheckBox")).setSelected(true);
		((TCheckBox) getComponent("saveCheckBox")).setSelected(true);
		// ���ɱ༭
		((TTextFormat) getComponent("START_DATE")).setEnabled(false);
		((TTextFormat) getComponent("END_DATE")).setEnabled(false);

	}

	/**
	 * ���
	 */
	public void onClear() {
		// clearValue("START_DATE;END_DATE");
		TTable tTable = ((TTable) getComponent("Table"));
		((TTable) getComponent("Table")).removeRowAll();
		this.setValue("START_DATE", DateUtil.getFirstDayOfMonth());
		this.setValue("END_DATE", DateUtil.getNowTime("yyyy/MM/dd"));
		clearValue("NOTICE_DESC");
		// ((TTextArea) getComponent("NOTICE_DESC")).setEnabled(false);
		((TTextFormat) getComponent("START_DATE")).setEnabled(true);
		((TTextFormat) getComponent("END_DATE")).setEnabled(true);
		selectRow = -1;
		if (!isPossessDownload) {
			tTable.setHeader("����,40;֪ͨ���,180;֪ͨ����,320;֪ͨʱ��,120");
			tTable.setParmMap("PUBLISH_FLG;REP_NO;REP_CODE;REP_DATE;REP_DESC");
			tTable.setLockColumns("0,1,2,3,4");
			tTable.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left");
			// �˵�����

			((TMenuItem) getComponent("save")).setVisible(false);
			((TMenuItem) getComponent("download")).setVisible(false);
			// ѡ��ؼ�����
			((TCheckBox) getComponent("publishCheckBox")).setVisible(false);
			((TCheckBox) getComponent("saveCheckBox")).setVisible(false);

		} else {
			tTable.setColumnHorizontalAlignmentData("2,left;3,left;4,left");
			// �˵�����
			((TMenuItem) getComponent("query")).setVisible(false);
			((TCheckBox) getComponent("publishCheckBox")).setSelected(false);
			((TCheckBox) getComponent("publishCheckBox")).setSelected(false);
			// ѡ��ؼ�����
			((TCheckBox) getComponent("publishCheckBox")).setEnabled(false);
			((TCheckBox) getComponent("saveCheckBox")).setEnabled(false);
		}

	}

	/**
	 * 
	 * ��ȡ�������
	 * 
	 * @param beginDate
	 *            ��ʼ���ڣ���ʽΪ��yyyy-MM-dd
	 * 
	 * @param endDate
	 *            �������ڣ���ʽΪ��yyyy-MM-dd
	 * 
	 * @return
	 */

	private static Date randomDate(String beginDate, String endDate) {

		try {

			Date start = format.parse(beginDate);// ���쿪ʼ����

			Date end = format.parse(endDate);// �����������

			// getTime()��ʾ������ 1970 �� 1 �� 1 �� 00:00:00 GMT ������ Date �����ʾ�ĺ�������

			if (start.getTime() >= end.getTime()) {

				return null;

			}

			long date = random(start.getTime(), end.getTime());

			return new Date(date);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return null;

	}

	private static long random(long begin, long end) {

		long rtn = begin + (long) (Math.random() * (end - begin));

		// ������ص��ǿ�ʼʱ��ͽ���ʱ�䣬��ݹ���ñ������������ֵ

		if (rtn == begin || rtn == end) {

			return random(begin, end);

		}

		return rtn;

	}

	/**
	 * ��DataDown_rs�����ִ�ת��ΪTParm
	 * 
	 * @param parm
	 * @return
	 */
	private TParm dataDownToParm(String dataDown_rs_S,
			List<String> columnNameList) {
		TParm parm = new TParm();
		if (dataDown_rs_S == null || dataDown_rs_S.trim().equals("")) {
			return parm;
		}
		int columnNameListSize = columnNameList.size();
		String[] rows = dataDown_rs_S.split(DATADOWN_RS_S_ROW_SPLIT);
		for (int i = 0; i < rows.length; i++) {
			String[] columns = rows[i].split(DATADOWN_RS_S_COLUMN_SPLIT);
			if (columns.length == columnNameListSize) {

				parm.addData("SAVE_FLG", "Y");
				parm.addData("PUBLISH_FLG", "Y");
				for (int j = 0; j < columnNameListSize; j++) {
					String value = columns[j];
					if (columnNameList.get(j).endsWith("DATE")
							&& value.length() >= 10) {
						value = value.substring(0, 10);
					}
					parm.addData(columnNameList.get(j), value);
				}

			}
		}

		return parm;
	}

}
