package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TIOM_AppServer;
import java.sql.Timestamp;
import jdo.sys.Operator;

import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
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
 * @author Miracle
 * @version 1.0
 */
public class ODISYSPARMUIControl extends TControl {
	/**
	 * ����������
	 */
	private String actionName = "action.odi.ODIAction";
	/**
	 * ��ʱ��ҩԤ��Ƶ��
	 */
	private String uddStatCode = "";
	private String odiStatCode = "";

	public void onInit() {
		// ��ʼ������
		this.initPage();
	}

	public void initPage() {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM ODI_SYSPARM"));
		TParm temp = parm.getRow(0);
		this.setValue("NS_CHECK_FLG", temp.getBoolean("NS_CHECK_FLG"));
		this.setValue("DSPN_TIME", StringTool.getTimestamp(temp
				.getValue("DSPN_TIME"), "HHmm"));
		this.setValue("START_TIME", StringTool.getTimestamp(temp
				.getValue("START_TIME"), "HHmm"));
		this.uddStatCode = temp.getValue("UDD_STAT_CODE");
		this.odiStatCode = temp.getValue("ODI_STAT_CODE");
		this.setValue("UDD_STAT_CODE", temp.getValue("UDD_STAT_CODE"));
		this.setValue("ODI_STAT_CODE", temp.getValue("ODI_STAT_CODE"));
		this.setValue("ODI_DEFA_FREG", temp.getValue("ODI_DEFA_FREG"));
		this.setValue("IVA_EXPANDTIME", StringTool.getTimestamp(temp
				.getValue("IVA_EXPANDTIME"), "HHmm"));
		this.setValue("DELAY_TIME", temp.getValue("DELAY_TIME"));
		this.setValue("DELAY_SUFFIX", temp.getValue("DELAY_SUFFIX"));
		// ===zhangp 20120702 start
		this.setValue("DS_MED_DAY", temp.getValue("DS_MED_DAY"));
		// ===zhangp 20120702 end
		// <------ identify by shendr 20130808 start
		this.setValue("IVA_STAT", temp.getValue("IVA_STAT"));
		this.setValue("IVA_FIRST", temp.getValue("IVA_FIRST"));
		this.setValue("IVA_UD", temp.getValue("IVA_UD"));
		this.setValue("IVA_OP", temp.getValue("IVA_OP"));
		// end ----->
		if (temp.getValue("BIL_POINT").equals("1")) {
			this.setValue("NS_EXE", "Y");
		} else if (temp.getValue("BIL_POINT").equals("2")) {
			this.setValue("EXM_EXE", "Y");
		}
		// duzhw add 20140504
		this.setValue("MR_IPD_FLG", temp.getBoolean("MR_IPD_FLG"));
		this.setValue("NEWBORN_MR_FLG", temp.getBoolean("NEWBORN_MR_FLG"));
		this.setValue("STOP_BILL_FLG", temp.getBoolean("STOP_BILL_FLG"));// =====pangben
																			// 2015-4-29
																			// ֹͣ���۹���
		this.setValue("PAYWARN_FLG", temp.getBoolean("PAYWARN_FLG"));// wanglong
																		// 20150331
																		// ����Ԥ����Ԥ������ע��
		// ====pangben 2015-6-15 ����ײ�ҽ���趨
		this.setValue("ORDER_CODE", temp.getValue("LUMPWORK_ORDER_CODE"));
		TParm sysfeeParm = new TParm(getDBTool().select(
				"SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE='"
						+ temp.getValue("LUMPWORK_ORDER_CODE") + "'"));
		this.setValue("ORDER_DESC", sysfeeParm.getValue("ORDER_DESC", 0));

		// =====wukai 20170321 ������Һ���� �������þ���ҩ�� ��� start
		TParm stationParm = new TParm(this.getDBTool().select("SELECT 'N' AS ACTIVE_FLG,STATION_CODE,STATION_DESC FROM SYS_STATION"));
		String stationCode = temp.getValue("NPIVAS_STATION");
		if (!stationCode.equals("") && stationCode != null) {
			String[] station = stationCode.split(";");
			for (int i = 0; i < stationParm.getCount("STATION_CODE"); i++) {
				for (int j = 0; j < station.length; j++) {
					if (stationParm.getValue("STATION_CODE", i).equals(station[j])) {
						stationParm.setData("ACTIVE_FLG", i, "Y");
						break;
					}
				}
			}
		}
		TTable table = this.getTTable("STATION_TABLE");
		table.setParmValue(stationParm);
		// ====wukai 20170321 ������Һ���� �������þ���ҩ�� ��� end

		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
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

	public void onSave() {
		TParm parm = new TParm();
		String dspnTime = StringTool.getString((Timestamp) this
				.getValue("DSPN_TIME"), "HHmm");
		String startTime = StringTool.getString((Timestamp) this
				.getValue("START_TIME"), "HHmm");
		String ivaExpandTime = StringTool.getString((Timestamp) this
				.getValue("IVA_EXPANDTIME"), "HHmm");
		String bilpoint = "";
		if (this.getValueString("NS_EXE").equals("Y")) {
			bilpoint = "1";
		} else if (this.getValueString("EXM_EXE").equals("Y")) {
			bilpoint = "2";
		}
		
		// === add by wukai 20170321 ������Һ������Һ 
		TTable table = this.getTTable("STATION_TABLE");
		TParm tableParm = table.getParmValue();
		StringBuilder sb = new StringBuilder("");
		
		for(int i = 0; i < tableParm.getCount("STATION_CODE"); i++) {
			if("Y".equals(tableParm.getValue("ACTIVE_FLG", i))) {
				sb.append(tableParm.getValue("STATION_CODE", i) + ";");
			}
		}

		String station = sb.toString();
		if(StringUtils.isNotEmpty(station) && station.contains(";")) {
			station = station.substring(0, station.lastIndexOf(";"));
		}
		
		String sqlArray[] = new String[] { "UPDATE ODI_SYSPARM SET NS_CHECK_FLG='"
				+ this.getValue("NS_CHECK_FLG")
				+ "', DSPN_TIME = '"
				+ dspnTime
				+ "', START_TIME = '"
				+ startTime
				+ "', UDD_STAT_CODE = '"
				+ this.getValue("UDD_STAT_CODE")
				+ "', ODI_STAT_CODE = '"
				+ this.getValue("ODI_STAT_CODE")
				+ "', ODI_DEFA_FREG = '"
				+ this.getValue("ODI_DEFA_FREG")
				+ "', IVA_EXPANDTIME = '"
				+ ivaExpandTime
				+ "', OPT_DATE = SYSDATE, OPT_USER = '"
				+ Operator.getID()
				+ "', OPT_TERM = '"
				+ Operator.getIP()
				+ "', DELAY_TIME = '"
				+ this.getValue("DELAY_TIME")
				+ "', DELAY_SUFFIX = '"
				+ this.getValue("DELAY_SUFFIX")
				+ "'"
				+
				// ====zhangp 20120702 start
				", DS_MED_DAY = "
				+ this.getValue("DS_MED_DAY")
				// ====zhangp 20120702 end
				// <--------- identify by shendr 2013.08.08
				+ ", IVA_STAT = '"
				+ this.getValue("IVA_STAT")
				+ "', IVA_FIRST = '"
				+ this.getValue("IVA_FIRST")
				+ "', IVA_UD = '"
				+ this.getValue("IVA_UD")
				+ "', IVA_OP = '"
				+ this.getValue("IVA_OP")
				+ "', BIL_POINT = '"
				+ bilpoint
				+ "', MR_IPD_FLG = '"
				+ this.getValue("MR_IPD_FLG")
				+ "', NEWBORN_MR_FLG = '"
				+ this.getValue("NEWBORN_MR_FLG")
				+ "'"
				// wanglong add 20150331 ����Ԥ����Ԥ������ע��
				+ ", PAYWARN_FLG = '"
				+ this.getValue("PAYWARN_FLG")
				+ "', STOP_BILL_FLG = '"
				+ this.getValue("STOP_BILL_FLG")
				+ "', LUMPWORK_ORDER_CODE = '" + this.getValue("ORDER_CODE") + "'"
				+ ", NPIVAS_STATION = '" + station + "'"};
		// ---------->
		// System.out.println("----------------"+sqlArray[0]);
		parm.setData("ARRAY", sqlArray);
		TParm actionParm = TIOM_AppServer.executeAction(actionName,
				"saveOrder", parm);
		if (actionParm.getErrCode() < 0)
			this.messageBox("����ʧ�ܣ�");
		else
			this.messageBox("����ɹ���");
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * ������¼�
	 */
	public void onTableCheckBoxClicked() {
		TTable table = this.getTTable("STATION_TABLE");
		int row = table.getSelectedRow();
		if ("N".equals(table.getItemString(row, "ACTIVE_FLG"))) {
			table.setItem(row, "ACTIVE_FLG", "Y");
		} else if("Y".equals(table.getItemString(row, "ACTIVE_FLG"))){
			table.setItem(row, "ACTIVE_FLG", "N");
		}
	}
	
	
	/**
	 * ȫѡ�¼�
	 */
	public void onSelectAll() {
		boolean selectAll = this.getTCheckBox("SELECT_ALL").isSelected();
		//this.getTCheckBox("SELECT_ALL").setSelected(!selectAll);
		TTable table = this.getTTable("STATION_TABLE");
		for(int i = 0; i < table.getRowCount(); i++) {
			table.setItem(i, "ACTIVE_FLG", selectAll ? "Y" : "N");
		}
		
	}
	
	
	/**
	 * ѡ���¼�
	 */
	public void onSel(Object obj) {
		String temp = "";
		if ("PHA".equals(obj)) {
			temp = this.getValueString("UDD_STAT_CODE");
			if (!OrderUtil.getInstance().isSTFreq(temp)) {
				this.messageBox("������ʱ��ҩƵ�Σ�");
				this.setValue("UDD_STAT_CODE", this.uddStatCode);
				return;
			}
			this.setValue("UDD_STAT_CODE", temp);
			this.uddStatCode = temp;
		}
		if ("TRT".equals(obj)) {
			temp = this.getValueString("ODI_STAT_CODE");
			if (!OrderUtil.getInstance().isSTFreq(temp)) {
				this.messageBox("������ʱ����Ƶ�Σ�");
				this.setValue("ODI_STAT_CODE", this.odiStatCode);
				return;
			}
			this.setValue("ODI_STAT_CODE", temp);
			this.odiStatCode = temp;
		}
	}

	/**
	 * ��ȡTTabtle
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	/**
	 * ��ȡTCheckBox�ؼ�
	 * @param tag
	 * @return
	 */
	private TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}
}
