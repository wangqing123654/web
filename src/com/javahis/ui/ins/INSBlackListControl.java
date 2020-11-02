package com.javahis.ui.ins;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.ins.InsManager;

import jdo.adm.ADMInpTool;
import jdo.ins.INSBlackListTool;
import jdo.ins.INSNoticeTool;
import jdo.reg.RegMethodTool;
import jdo.sys.Operator;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ���������Ϣ����
 * </p>
 * 
 * <p>
 * Description:���������Ϣ����
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
public class INSBlackListControl extends TControl {
	TParm data;
	TParm LocalTabledata;
	int selectRow = -1;
	/**
	 * ���߶�
	 */
	private static int maxHeight = 520;
	/**
	 * localTableDefautĬ��Y
	 */
	private static int localTableDefautY = 306;
	/**
	 * localTableDefautĬ�ϸ߶�
	 */
	private static int localTableDefautHeight = 337;
	/**
	 * downdoadTableDefautHeightĬ�ϸ߶�
	 */
	private static int downdoadTableDefautHeight = 254;
	/**
	 * localCheckBoxYĬ��Y
	 */
	private static int localCheckBoxDefautY = 281;
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
	/**
	 * ��ǰ�����Ƿ񸲸�
	 */
	boolean isAllowCover = false;
	/**
	 * �����Ƿ��ظ�
	 */
	boolean isRepeat = false;
	/**
	 * DataDown_rs ��S�����зָ���
	 */
	private String DATADOWN_RS_S_ROW_SPLIT = "";

	/**
	 * DataDown_rs ��S�����зָ���
	 */
	private String DATADOWN_RS_S_COLUMN_SPLIT = "\\|";

	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

	public void onInit() {
		super.onInit();

		setPossessDownload();
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
		isPossessDownload = true;

	}

	/**
	 * ���Ӷ������б�ļ���change
	 * 
	 * @param row
	 *            int
	 */
	public void onTableRowChange() {
		// ѡ����
		TTable table = ((TTable) getComponent("Table"));
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		selectTableRow((String) tableParm.getData("DR_NAME", row),
				(String) tableParm.getData("BLIST_NO", row), "LocalTable");
	}

	/**
	 * ���ӶԱ������ݿ��б�ļ���change
	 * 
	 * @param row
	 *            int
	 */
	public void onLocalTableRowChange() {
		// ѡ����
		TTable table = ((TTable) getComponent("LocalTable"));
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		selectTableRow((String) tableParm.getData("DR_NAME", row),
				(String) tableParm.getData("BLIST_NO", row), "Table");
	}

	private void selectTableRow(String drName, String blistNo, String tableName) {
		TTable table = ((TTable) getComponent(tableName));
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		int rowCount = table.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			String DR_NAME = (String) tableParm.getData("DR_NAME", rowIndex);
			String BLIST_NO = (String) tableParm.getData("BLIST_NO", rowIndex);
			if (DR_NAME.equals(drName) && BLIST_NO.equals(blistNo)) {
				table.setSelectedRow(rowIndex);
				return;
			}
		}
		// table.setSelectedRow(-1);
	}

	/**
	 * ����
	 */
	public void onSave() {
		TTable table = ((TTable) getComponent("Table"));
		table.acceptText();
		TParm tableParm = table.getParmValue();
		TParm result = new TParm();
		int count = table.getRowCount();
		isRepeat = false;
		isAllowCover = false;
		TParm saveTParm = new TParm();
		int saveCount = 0;

		for (int i = 0; i < count; i++) {
			String drName = (String) tableParm.getData("DR_NAME", i);
			String blistNo = (String) tableParm.getData("BLIST_NO", i);
			boolean saveFlg = tableParm.getBoolean("SAVE_FLG", i);
			// �����ظ����Ҳ��븲��
			isAllowCover(saveFlg, drName, blistNo);
			if (isRepeat && !isAllowCover) {
				return;
			}
			if (saveFlg ) {

				saveTParm.addData("SEQ_NO", tableParm.getData("BLIST_NO", i));
				saveTParm.addData("DR_NAME", tableParm.getData("DOCTOR_NAME", i));
				saveTParm.addData("BLIST_NO", tableParm.getData("BLIST_NO", i));
				saveTParm.addData("VO_BDATE", tableParm.getData("VO_BDATE", i));
				saveTParm.addData("VO_SDATE", tableParm.getData("VO_SDATE", i));
				saveTParm.addData("ST_BDATE", tableParm.getData("ST_BDATE", i));
				saveTParm.addData("ST_SDATE", tableParm.getData("ST_SDATE", i));
				saveTParm.addData("ST_DESC", tableParm.getData("ST_DESC", i));
				saveTParm.addData("VO_DESC", tableParm.getData("VO_DESC", i));
				saveTParm.addData("BLIST_TYPE", tableParm.getData("BLIST_TYPE",
						i));
				saveTParm.addData("OPT_USER", Operator.getID());
				saveTParm.addData("OPT_TERM", Operator.getIP());
				saveCount++;

			}
		}
		saveTParm.setCount(saveCount);
		TParm actionParm = new TParm();
		actionParm.setData("saveTParm", saveTParm.getData());
		actionParm.setData("START_DATE", getText("START_DATE"));
		actionParm.setData("END_DATE", getText("END_DATE"));
		actionParm.setData("DR_NAME", getText("DR_NAME"));
		actionParm.setData("ISALLOWCOVER", isAllowCover);
		result = TIOM_AppServer.executeAction("action.ins.INSBlackListAction",
				"onSave", actionParm);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		this.messageBox("P0005");

		// onUpdate(tableParm);
	}

	/**
	 * �ж���Ҫ����������Ƿ��뱾�������ظ�
	 */
	private boolean isAllowCover(boolean saveFlg, String drName, String blistNo) {
		if (!saveFlg) {
			return false;
		}
		TTable localTable = ((TTable) getComponent("LocalTable"));
		TParm tableParm = localTable.getParmValue();
		int rowCount = localTable.getRowCount();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			String DR_NAME = (String) tableParm.getData("DR_NAME", rowIndex);
			String BLIST_NO = (String) tableParm.getData("BLIST_NO", rowIndex);
			if (DR_NAME.equals(drName) && BLIST_NO.equals(blistNo)) {
				isRepeat = true;
				int infoValue = this.messageBox("ϵͳ��ʾ", "��ǰ���������뱾�������ظ����Ƿ񸲸ǣ�",
						2);
				if (infoValue == 0) {
					isAllowCover = true;
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryTParm = new TParm();
		TTable LocalTable = ((TTable) getComponent("LocalTable"));

		// tTable.setColumnHorizontalAlignmentData("0,left;1,left");
		// tTable.setColumnHorizontalAlignment(1, 0);
		clearValue("NOTICE_DESC");
		
		queryTParm.setData("START_DATE", getText("START_DATE"));
		queryTParm.setData("END_DATE", getText("END_DATE"));
		queryTParm.setData("DR_NAME", "%"+getText("DR_NAME")+"%");

		LocalTabledata = INSBlackListTool.getInstance().selectdata(queryTParm);
		// �жϴ���ֵ
		if (LocalTabledata.getErrCode() < 0) {
			messageBox(LocalTabledata.getErrText());
			return;
		}
		((TTable) getComponent("LocalTable")).setParmValue(LocalTabledata);
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
	 * ���ýӿ� InsManager ��S����
	 * 
	 * @param parm
	 * @return
	 */
	private TParm insManagerS(TParm parm) {
		parm.addData("START_DATE", getText("START_DATE"));
		parm.addData("END_DATE", getText("END_DATE"));
		//parm.addData("DR_NAME", getText("DR_NAME"));
		parm.addData("HOSP_NHI_NO", Operator.getRegion());
		parm.addData("PARM_COUNT", 3);

		parm.setData("PIPELINE", "DataDown_rs");
		parm.setData("PLOT_TYPE", "S");
		TParm dataDown_rs_S =  InsManager.getInstance().safe(parm,"");
		//this.messageBox(dataDown_rs_S.getData()+"");
		//String dataDown_rs_S = "����ִ��״̬|����ִ����Ϣ|ҽԺ����|����|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|��ֹ����|��ʾ����|����������|������˳�������ִ��״̬|����ִ����Ϣ|ҽԺ����|������|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|2012-01-10 09:30:43|��ֹ����|��ʾ����|����Υ��|1#";
		//dataDown_rs_S===={Data={TIMESTAMP=1, PROGRAM_STATE=1, HOSP_NHI_NO=109720.6, DOCTOR_NAME=0, PROGRAM_MESSAGE=�ɹ�!, BLIST_TPYE=1, ST_DESC=1, VO_DESC=1, BLIST_NO=1}}
		
		
//SAVE_FLG;DOCTOR_NAME;VO_BDATE;VO_SDATE;ST_BDATE;ST_SDATE;ST_DESC;VO_DESC;BLIST_TYPE
//		List columnNameList = new ArrayList();
//		columnNameList.add("PROGRAM_STATE");
//		columnNameList.add("PROGRAM_MESSAGE");
//		columnNameList.add("HOSP_NHI_NO");
//		columnNameList.add("DR_NAME");
//		columnNameList.add("VO_BDATE");
//		columnNameList.add("VO_SDATE");
//		columnNameList.add("ST_BDATE");
//		columnNameList.add("ST_SDATE");
//		columnNameList.add("ST_DESC");
//		columnNameList.add("VO_DESC");  
//		columnNameList.add("BLIST_TYPE");
//		columnNameList.add("BLIST_NO");
		//TParm result = dataDownToParm(dataDown_rs_S, columnNameList);
		// TParm result =

		return dataDown_rs_S;
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

	private TParm testInsManagerS(TParm parmSreach) {
		TParm parm = new TParm();
		for (int i = 1; i < 15; i++) {
			parm.addData("SAVE_FLG", "Y");
			parm.addData("DR_NAME", getText("DR_NAME"));
			parm.addData("VO_BDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));
			parm.addData("VO_SDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));
			parm.addData("ST_BDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));
			parm.addData("ST_SDATE", format.format(randomDate(
					getText("START_DATE"), getText("END_DATE"))));

			parm.addData("BLIST_NO", "" + i);
			parm.addData("ST_DESC", "��ֹ����" + i);
			parm.addData("VO_DESC", "��ʾ����" + i);
			parm.addData("BLIST_TYPE", "����������" + i);
		}

		return parm;
	}

	/**
	 * ��ʾ�����б�
	 */
	private void allChecked() {
		TCheckBox saveCheckBox = ((TCheckBox) getComponent("saveCheckBox"));
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		localTable.setVisible(true);
		localTable.setY(localTableDefautY);
		localTable.setHeight(localTableDefautHeight - 92);
		localCheckBox.setY(localCheckBoxDefautY);

		downdoadTable.setHeight(downdoadTableDefautHeight);
		downdoadTable.setVisible(true);
		downloadCheckBox.setX(72);

		saveCheckBox.setVisible(true);

	}

	/**
	 * ���ر����б�
	 */
	private void hiddenLocalTable() {
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		localTable.setVisible(false);
		localCheckBox.setY(localCheckBoxDefautY);

		downdoadTable.setHeight(maxHeight - 25);
		downdoadTable.setVisible(true);
		localCheckBox.setY(downdoadTable.getX() + downdoadTable.getHeight()
				+ 25);
		localCheckBox.setVisible(true);

	}

	/**
	 * ���������б�
	 */
	private void hiddenDownloadTable() {
		TCheckBox saveCheckBox = ((TCheckBox) getComponent("saveCheckBox"));
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		localTable.setVisible(true);
		localTable.setY(downdoadTable.getY() + 25);
		localTable.setHeight(maxHeight - 20);
		localCheckBox.setY(downdoadTable.getY() + 2);

		saveCheckBox.setVisible(false);
		downloadCheckBox.setX(saveCheckBox.getX());
		downdoadTable.setVisible(false);

	}

	/**
	 * ȫ������
	 */
	private void hiddenAll() {
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));
		TCheckBox saveCheckBox = ((TCheckBox) getComponent("saveCheckBox"));

		localTable.setVisible(false);
		localCheckBox.setY(downloadCheckBox.getY() + 20);
		downdoadTable.setVisible(false);
		downloadCheckBox.setX(saveCheckBox.getX());

		saveCheckBox.setVisible(false);

	}

	/**
	 * ������ʾ�����б�
	 */
	public void onShowTable() {
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));

		if (downloadCheckBox.isSelected() && localCheckBox.isSelected()) {
			allChecked();
		} else if (downloadCheckBox.isSelected()) {
			hiddenLocalTable();
		} else if (localCheckBox.isSelected()) {
			hiddenDownloadTable();
		} else {
			hiddenAll();
		}

	}

	/**
	 * ������ʾ�������ݿ���Ϣ�б�
	 */
	public void onLocalCheckBoxClicked() {
		TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
		TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
		TTable downdoadTable = ((TTable) getComponent("Table"));
		TTable localTable = ((TTable) getComponent("LocalTable"));

		if (localCheckBox.isSelected()) {
			// ��ʾ ��ѯ�б�����

			downdoadTable.setHeight(254);
			localTable.setVisible(true);
			if (downloadCheckBox.isSelected()) {
				localTable.setY(306);
				localTable.setHeight(337);
				localCheckBox.setY(281);
			} else {
				localTable.setY(downdoadTable.getY() + 20);
				localCheckBox.setY(downloadCheckBox.getY() + 2);
				localTable.setHeight(597);
			}

		} else {
			// ���� ��ѯ�б�����
			localTable.setVisible(false);
			if (downloadCheckBox.isSelected()) {
				downdoadTable.setHeight(597);
				localCheckBox.setY(597 + 2);
			} else {
				localCheckBox.setY(downloadCheckBox.getY() + 2);
			}

			// localTable.setHeight(localTable.getHeight()+downdoadTable.getHeight());

		}

	}

	/**
	 * ����
	 */
	public void onDownload() {
		TParm parm = new TParm();
		if(StringUtil.isNullString(getText("START_DATE"))){
			this.messageBox("�����뿪ʼʱ�䡣");
			return ;
		}
		if(StringUtil.isNullString(getText("END_DATE"))){
			this.messageBox("���������ʱ�䡣");
			return ;
		}
		data = insManagerS(parm);
		
		// �жϴ���ֵ
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		// ��װ��ѯ�����
		packagDate();
		((TTable) getComponent("Table")).setParmValue(data);
		((TCheckBox) getComponent("saveCheckBox")).setEnabled(true);
		((TCheckBox) getComponent("saveCheckBox")).setSelected(true);
		// ���ɱ༭
		((TTextFormat) getComponent("START_DATE")).setEnabled(false);
		((TTextFormat) getComponent("END_DATE")).setEnabled(false);
		((TextFormatSYSOperator) getComponent("DR_NAME")).setEnabled(false);
		onQuery();
	}
	/**
	 * ��װ��ѯ���
	 * 
	 * @param parm
	 * @param columnName
	 * @return
	 */
	private void packagDate() {
		int parmCount = data.getCount("ST_SDATE");
		//String[] str=new String[parmCount];
		for (int i = 0; i < parmCount; i++) {
			//str[i]="Y";
			data.addData("SAVE_FLG", "Y");
			
		}
	//	data.addData("SAVE_FLG", str);
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
		((TTextFormat) getComponent("START_DATE")).setEnabled(true);
		((TTextFormat) getComponent("END_DATE")).setEnabled(true);
		((TextFormatSYSOperator) getComponent("DR_NAME")).setEnabled(true);
		selectRow = -1;
		if (!isPossessDownload) {
			TCheckBox downloadCheckBox = ((TCheckBox) getComponent("downloadCheckBox"));
			TCheckBox localCheckBox = ((TCheckBox) getComponent("localCheckBox"));
			TTable localTable = ((TTable) getComponent("LocalTable"));

			// �˵�����

			((TMenuItem) getComponent("save")).setVisible(false);
			((TMenuItem) getComponent("download")).setVisible(false);
			// ѡ��ؼ�����
			((TCheckBox) getComponent("saveCheckBox")).setVisible(false);
			downloadCheckBox.setVisible(false);
			downloadCheckBox.setSelected(false);
			localCheckBox.setVisible(false);
			localCheckBox.setSelected(true);
			onShowTable();
			localTable.setY(5);
		} else {
			tTable.setColumnHorizontalAlignmentData("1,left;6,left;7,left");
			// �˵�����
			((TMenuItem) getComponent("query")).setVisible(false);
			((TCheckBox) getComponent("saveCheckBox")).setSelected(false);
			// ѡ��ؼ�����
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

	public static void main(String[] args) {
		INSBlackListControl ins = new INSBlackListControl();
		System.out.println(ins.insManagerS(new TParm()));
	}
}
