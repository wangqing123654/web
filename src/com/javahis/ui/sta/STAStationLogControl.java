package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jdo.sta.STADeptListTool;
import jdo.sta.STAStationLogTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:������־
 * </p>
 * 
 * <p>
 * Description:������־
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
 * @author zhangk 2009-4-24
 * @version JavaHis 1.0
 */
public class STAStationLogControl extends TControl {
	String DEPT_CODE;// ��¼�м�����Ŷ��ձ��Ĳ���ID
	TParm insertParm;// ���ɱ��������
	TParm resultOUT;// ��Ժ����
	TParm resultIN;// ��Ժ����
	TParm resultINPR;// ת�벡��
	TParm resultOUPR;// ��������
	boolean CONFIRM_FLG = false;// ȷ�ϱ�� Ĭ��Ϊfalse��û���ύ true���ύ���
	String LEADER = "";// ��¼�Ƿ����鳤Ȩ�� ���LEADER=2��ô�����鳤Ȩ��
	private String DATA_StaDate = ""; // ��¼Ŀǰ��ʾ�����ݵ�����(����)

	public void onInit() {
		super.onInit();
		initDate();// ��ʼ������
		comboboxInit();// ��ʼ��Combo
		// ������ǰʹ���ߵĲ���CODE��ѯ�����м���ж�Ӧ�Ŀ���CODE
		// this.setValue("STATION_CODE",
		// STADeptListTool.getInstance().selectDeptByIPDCODE(Operator.getStation()).getValue("DEPT_CODE",0));
		// ��ʼ��Ȩ��
		if (this.getPopedem("LEADER")) {
			LEADER = "2";
		}
	}

	/**
	 * ����¼�
	 */
	public void onClear() {
		this
				.clearValue("DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_06_1;DATA_01;DATA_07;DATA_08;"
						+ "DATA_08_1;DATA_15_1;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_11;"
						+ "DATA_12;DATA_13;DATA_14;DATA_15;DATA_10;DATA_09");
		this.clearValue("DATA_23;DATA_24;DATA_25");//add by wanglong 20140304
		((TTable) this.getComponent("Table_IN")).removeRowAll();
		((TTable) this.getComponent("Table_OUT")).removeRowAll();
		// ����ύ��ѡ���ѡ��״̬
		((TCheckBox) this.getComponent("Submit")).setSelected(false);
		this.setValue("STATION_CODE", "");
		this.setText("STATION_CODE", "");
		this.getComboBox("IDEPT_CODE").removeAllItems();
		this.getComboBox("IDEPT_CODE").setEnabled(false);
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (CONFIRM_FLG)
			print();
		else {
			this.messageBox_("��Ҫȷ���ύ�󷽿ɴ�ӡ��");
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		// ��������鳤Ȩ�� ��ô�Ѿ��ύ�����ݲ������޸�
		if (!LEADER.equals("2")) {
			if (CONFIRM_FLG) { // �Ѿ��ύ
				this.messageBox_("���鳤�����޸����ύ�����ݣ�");
				return;
			}
		}
		// ����Ҫ�������Ŀ ���м��� ����ҳ��ؼ�
		setSum();
		TParm updateParm = new TParm(); // ��¼����STA_DAILY_01��Ĳ���
		updateParm.setData("DATA_02", getNumber("DATA_02"));
		updateParm.setData("DATA_03", getNumber("DATA_03"));
		updateParm.setData("DATA_05", getNumber("DATA_05"));
		updateParm.setData("DATA_06", getNumber("DATA_06"));
		updateParm.setData("DATA_04", getNumber("DATA_04"));
		updateParm.setData("DATA_06_1", getNumber("DATA_06_1"));
		updateParm.setData("DATA_01", getNumber("DATA_01"));
		updateParm.setData("DATA_07", getNumber("DATA_07"));
		updateParm.setData("DATA_08", this.getValue("DATA_08"));
		updateParm.setData("DATA_08_1", getValue("DATA_08_1"));
		updateParm.setData("DATA_15_1", getValue("DATA_15_1"));
		updateParm.setData("DATA_16", getNumber("DATA_16"));
		updateParm.setData("DATA_17", getNumber("DATA_17"));
		updateParm.setData("DATA_18", getNumber("DATA_18"));
		updateParm.setData("DATA_19", getNumber("DATA_19"));
		updateParm.setData("DATA_20", getNumber("DATA_20"));
		updateParm.setData("DATA_21", getNumber("DATA_21"));
		updateParm.setData("DATA_11", getValue("DATA_11"));
		updateParm.setData("DATA_12", getValue("DATA_12"));
		updateParm.setData("DATA_13", getValue("DATA_13"));
		updateParm.setData("DATA_14", getValue("DATA_14"));
		updateParm.setData("DATA_15", getValue("DATA_15"));
		updateParm.setData("DATA_10", getNumber("DATA_10"));
		updateParm.setData("DATA_09", getNumber("DATA_09"));
		updateParm.setData("DATA_22", getNumber("DATA_22"));
		updateParm.setData("DATA_23", getNumber("DATA_23"));//�����ù����� add by wanglong 20140305
		updateParm.setData("DATA_24", getNumber("DATA_24"));//����������
		updateParm.setData("DATA_25", getNumber("DATA_25"));//��������� add end
		updateParm.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		updateParm.setData("DEPT_CODE", DEPT_CODE);
		updateParm.setData("STATION_CODE", this.getValue("STATION_CODE")); // Ŀǰ���������code��ͬ
		if (((TCheckBox) this.getComponent("Submit")).isSelected()) {
			updateParm.setData("CONFIRM_FLG", "Y"); // �ύ��ʶ
		
		} else {
			updateParm.setData("CONFIRM_FLG", "N"); // �ύ��ʶ
		}
		updateParm.setData("CONFIRM_USER", Operator.getID()); // ȷ����
		updateParm.setData("OPT_USER", Operator.getID());
		updateParm.setData("OPT_TERM", Operator.getIP());
		// ===============pangben modify 20110520 start
		updateParm.setData("REGION_CODE", Operator.getRegion());
		// ===============pangben modify 20110520 stop
		TParm STATION_DAILY = new TParm();
		STATION_DAILY.setData("REAL_OCUU_BED_NUM", getNumber("DATA_16"));// ʵ�в�����
		STATION_DAILY.setData("OPT_USER", Operator.getID());
		STATION_DAILY.setData("OPT_TERM", Operator.getIP());
		STATION_DAILY.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		STATION_DAILY.setData("DEPT_CODE", DEPT_CODE);
		STATION_DAILY.setData("STATION_CODE", this.getValue("STATION_CODE"));
		TParm parm = new TParm();
		parm.setData("sta_daily_01", updateParm.getData());
		parm.setData("station_daily", STATION_DAILY.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.sta.STAStationLogAction", "updateSTA_DAILY_01", parm);
		if (result.getErrCode() < 0) {
			CONFIRM_FLG = false;// ״̬ �޸�Ϊ δ�ύ false
			this.messageBox_("�޸�ʧ�ܣ�");
			return;
		}
        if (((TCheckBox) this.getComponent("Submit")).isSelected()) {
            if (CONFIRM_FLG == true) {
                this.messageBox_("�޸ĳɹ���");
            } else if (CONFIRM_FLG == false) {
                CONFIRM_FLG = true;// ״̬��ʶΪ True
                this.messageBox_("�ύ�ɹ���");
            }
        } else if (!((TCheckBox) this.getComponent("Submit")).isSelected()) {
            if (CONFIRM_FLG == true) {
                CONFIRM_FLG = false;// ״̬��ʶΪ False
                this.messageBox_("ȡ���ύ�ɹ���");
            } else if (CONFIRM_FLG == false) {
                this.messageBox_("�޸ĳɹ���");
            }
        }
	}

	/**
	 * ��ѯ���� ��������
	 */
	public void onQuery() {
		insertData();
	}

	/**
	 * ��ӡ
	 */
	private void print() {
		TParm parm = new TParm();
		TParm pr = new TParm();
		pr.setData("STA_DATE", this.getText("STA_DATE").toString().replace("/",
				""));
		pr.setData("DEPT_CODE", DEPT_CODE);
		// ===========pangben modify 20110523 start
		pr.setData("REGION_CODE", Operator.getRegion());
		// ===========pangben modify 20110523 stop
		TParm re = STAStationLogTool.getInstance().selectSTA_DAILY_01(pr);
		if (re.getErrCode() < 0) {
			this.messageBox_("��ѯ��������ʧ�ܣ�");
			return;
		}
		if (re.getCount() == 0) {
			this.messageBox_("��û������������ݣ�");
			return;
		}
		TParm par = new TParm();// �������
		par.setData("DEPT", this.getText("STATION_CODE"));// ����
		par.setData("DATE", this.getText("STA_DATE").toString().replace("/",
				"-"));// ����
		TParm Iparm = new TParm();
		TParm Oparm = new TParm();
		parm.setData("All", par.getData());// �����������
		parm.setData("data", re.getData());// ������Ϣ
		TParm inParm = ((TTable) this.getComponent("Table_IN")).getParmValue();
		TParm outParm = ((TTable) this.getComponent("Table_OUT"))
				.getParmValue();
		for (int i = 0; i < inParm.getCount("MR_NO"); i++) {
			Iparm.addData("IPD_NO", inParm.getValue("IPD_NO", i));
			Iparm.addData("MR_NO", inParm.getValue("MR_NO", i)); // ������
			Iparm.addData("PAT_NAME", inParm.getValue("PAT_NAME", i));// ��������
		}
		for (int i = 0; i < outParm.getCount("MR_NO"); i++) {
			Oparm.addData("IPD_NO", outParm.getValue("IPD_NO", i));
			Oparm.addData("MR_NO", outParm.getValue("MR_NO", i)); // ������
			Oparm.addData("PAT_NAME", outParm.getValue("PAT_NAME", i));// ��������
		}
		TParm inPrParm = ((TTable) this.getComponent("Table_INPR"))
				.getParmValue();
		TParm outPrParm = ((TTable) this.getComponent("Table_OUPR"))
				.getParmValue();
		// ������ת�벡�˼��뵽��Ժ������
		for (int i = 0; i < inPrParm.getCount("MR_NO"); i++) {
			Iparm.addData("IPD_NO", inPrParm.getValue("IPD_NO", i));
			Iparm.addData("MR_NO", inPrParm.getValue("MR_NO", i)); // ������
			Iparm.addData("PAT_NAME", inPrParm.getValue("PAT_NAME", i) + "\n("
					+ getdeptDesc(inPrParm.getValue("DEPT_CODE", i)) + ")");// �������������Կ������ƣ�
		}
		// ��ת�����˼��뵽��Ժ������
		for (int i = 0; i < outPrParm.getCount("MR_NO"); i++) {
			Oparm.addData("IPD_NO", outPrParm.getValue("IPD_NO", i));
			Oparm.addData("MR_NO", outPrParm.getValue("MR_NO", i)); // ������
			Oparm.addData("PAT_NAME", outPrParm.getValue("PAT_NAME", i) + "\n("
					+ getdeptDesc(outPrParm.getValue("DEPT_CODE", i)) + ")");// ����������Ҫת���Ŀ������ƣ�
		}
		parm.setData("IN", Iparm.getData());// ��Ժ����
		parm.setData("OUT", Oparm.getData());// ��Ժ����
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_01.jhw", parm);
	}

	private String getdeptDesc(String deptCode) {
		String deptdesc = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		if (parm.getCount() > 0)
			deptdesc = parm.getValue("DEPT_CHN_DESC", 0);
		return deptdesc;
	}

	/**
	 * ���ڳ�ʼ��
	 */
	private void initDate() {
		// ��ȡ���������
		Timestamp yestaday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		this.setValue("STA_DATE", yestaday);
	}

	/**
	 * ��ѯ ��Ժ �ͳ�Ժ �˴�,ת�롢��������
	 */
	public void selectDate() {
		// �м������Ϣ
		TParm staDept = STADeptListTool.getInstance().selectDeptByCode(
				this.getValueString("IDEPT_CODE"),this.getValueString("STATION_CODE"),Operator.getRegion());// ===pangben
		// modify
		// 20110523
		// ��ѯ��Ժ�˴�
		TParm parm2 = new TParm();
		parm2.setData("STATION_CODE", staDept.getValue("STATION_CODE", 0));// ����CODE
		parm2.setData("DEPT_CODE", staDept.getValue("IPD_DEPT_CODE", 0));// סԺ����code
		parm2.setData("DATE", this.getText("STA_DATE").toString().replace("/",
				""));// ��ѯ����
		// ===========pangben modify 20110518 start ����������
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm2.setData("REGION_CODE", Operator.getRegion());
		// ===========pangben modify 20110518 start
		resultIN = STAStationLogTool.getInstance().selectInNum(parm2);
		if (resultIN.getErrCode() < 0) {
			this.messageBox_("��Ժ���˲�ѯʧ�ܣ�");
			return;
		}
		((TTable) this.getComponent("Table_IN")).setParmValue(resultIN);
		resultOUT = STAStationLogTool.getInstance().selectOutNum(parm2);
		if (resultOUT.getErrCode() < 0) {
			this.messageBox_("��Ժ���˲�ѯʧ�ܣ�");
			return;
		}
		((TTable) this.getComponent("Table_OUT")).setParmValue(resultOUT);
		String deptCode = staDept.getValue("IPD_DEPT_CODE", 0);
		String stationCode=staDept.getValue("STATION_CODE", 0);
		String date = this.getText("STA_DATE").toString().replace("/", "");
		int incount = 0;
		resultINPR = new TParm();
		// ==========pangben modify 20110518 start ���selectINPR�����������
		TParm inprParm = STAStationLogTool.getInstance().selectINPR(deptCode,stationCode,
				date, Operator.getRegion());
		// System.out.println("-=-----inprParm----------"+inprParm);
		for (int i = 0; i < inprParm.getCount(); i++) {
			resultINPR.addData("IPD_NO", inprParm.getValue("IPD_NO", i));
			resultINPR.addData("MR_NO", inprParm.getValue("MR_NO", i));
			resultINPR.addData("PAT_NAME", inprParm.getValue("PAT_NAME", i));
			resultINPR.addData("DEPT_CODE", inprParm
					.getValue("IN_DEPT_CODE", i));
			resultINPR.addData("STATION_CODE", inprParm
					.getValue("IN_STATION_CODE", i));
		}
		// ==========pangben modify 20110518 stop
		if (resultINPR.getErrCode() < 0) {
			this.messageBox_("ת�벡�˲�ѯʧ�ܣ�");
			return;
		}
		((TTable) this.getComponent("Table_INPR")).setParmValue(resultINPR);
		resultOUPR = new TParm();
		int outCount = 0;
		// ==========pangben modify 20110518 start ���selectINPR�����������
		TParm ouprParm = STAStationLogTool.getInstance().selectOUPR(deptCode,stationCode,
				date, Operator.getRegion());
		// System.out.println("-=----------"+ouprParm);
		for (int i = 0; i < ouprParm.getCount(); i++) {
			resultOUPR.addData("IPD_NO", ouprParm.getValue("IPD_NO", i));
			resultOUPR.addData("MR_NO", ouprParm.getValue("MR_NO", i));
			resultOUPR.addData("PAT_NAME", ouprParm.getValue("PAT_NAME", i));
			resultOUPR.addData("DEPT_CODE", ouprParm.getValue("OUT_DEPT_CODE",
					i));
			resultOUPR.addData("STATION_CODE", ouprParm
					.getValue("OUT_STATION_CODE", i));
		}
		// ==========pangben modify 20110518 stop
		if (resultOUPR.getErrCode() < 0) {
			this.messageBox_("ת�����˲�ѯʧ�ܣ�");
			return;
		}
		((TTable) this.getComponent("Table_OUPR")).setParmValue(resultOUPR);
	}

	/**
	 * ������Ҫ����� ��Ŀ
	 */
	private void setSum() {
		this.getNumText("DATA_10").setValue(getDATA_10());// �������� = 11+12+13+14
		// this.getNumText("DATA_09").setValue(getDATA_09());//��Ժ�����ܼ�
		// =11+12+13+14+15
		this.getNumText("DATA_16").setValue(getDATA_16());// ʵ�в����� = 7+8+8_1-9
		// ע��˳��Ҫ�ȼ���09����ܼ���16
		this.setValue("DATA_18", this.getValue("DATA_17"));// ʵ�ʿ��Ų����� = ʵ�в�����
	}
	/**
	 * ǰ��
	 */
    public  void  onDaySelBef(){
    	if(this.getValue("STA_DATE")==null){
    		this.messageBox("����������");
    		return;
    	}
    	Timestamp day=(Timestamp)getValue("STA_DATE");
    	Timestamp befday=StringTool.rollDate(day, -1);
		this.setValue("STA_DATE", befday);
		this.onQuery();
    }
    /**
	 * ǰ��
	 */
    public  void  onDaySelAf(){
    	if(this.getValue("STA_DATE")==null){
    		this.messageBox("����������");
    		return;
    	}
    	Timestamp day=(Timestamp)getValue("STA_DATE");
    	Timestamp afday=StringTool.rollDate(day, +1);
		this.setValue("STA_DATE", afday);
		this.onQuery();
    }
	/**
	 * ���� �������� = 11+12+13+14
	 * 
	 * @return int
	 */
	private int getDATA_10() {
		int DATA_11 = Integer.valueOf(getValueString("DATA_11"));
		int DATA_12 = Integer.valueOf(getValueString("DATA_12"));
		int DATA_13 = Integer.valueOf(getValueString("DATA_13"));
		int DATA_14 = Integer.valueOf(getValueString("DATA_14"));
		return DATA_11 + DATA_12 + DATA_13 + DATA_14;
	}

	/**
	 * ��Ժ�����ܼ� =11+12+13+14+15
	 * 
	 * @return int
	 */
	private int getDATA_09() {
		int DATA_11 = Integer.valueOf(getValueString("DATA_11"));
		int DATA_12 = Integer.valueOf(getValueString("DATA_12"));
		int DATA_13 = Integer.valueOf(getValueString("DATA_13"));
		int DATA_14 = Integer.valueOf(getValueString("DATA_14"));
		int DATA_15 = Integer.valueOf(getValueString("DATA_15"));
		// int DATA_15_1 = getNumber("DATA_15_1");
		return DATA_11 + DATA_12 + DATA_13 + DATA_14 + DATA_15;
	}

	/**
	 * ���� ʵ�в����� = 7+8+8_1-9-15_1
	 * 
	 * @return int
	 */
	private int getDATA_16() {
		int DATA_07 = getNumber("DATA_07");
		int DATA_08 = Integer.valueOf(getValueString("DATA_08"));
		int DATA_08_1 = Integer.valueOf(getValueString("DATA_08_1"));
		int DATA_09 = getNumber("DATA_09");
		int DATA_15_1 = Integer.valueOf(getValueString("DATA_15_1"));
		return DATA_07 + DATA_08 + DATA_08_1 - DATA_09 - DATA_15_1;
	}

	/**
	 * ��ȡҳ���TNumberText
	 * 
	 * @param tag
	 *            String
	 * @return TNumberText
	 */
	private TNumberTextField getNumText(String tag) {
		return (TNumberTextField) this.getComponent(tag);
	}

	/**
	 * ��ȡָ�� TNumberText ��ֵ
	 * 
	 * @param tag
	 *            String
	 * @return int
	 */
	private int getNumber(String tag) {
		return this.getNumText(tag).getValue() == null ? 0 : this
				.getValueInt(tag);
	}

	/**
	 * ���ţ�����combobox��ʼ��
	 */
	private void comboboxInit() {
		// ============pangben modify 20110519 start
		TParm deptParm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			deptParm.setData("REGION_CODE", Operator.getRegion());
		TParm dept = STAStationLogTool.getInstance().selectDept(deptParm);
		// ============pangben modify 20110519 stop
		// TParm station = STAStationLogTool.getInstance().selectSTAStation();
		// String sql = STASQLTool.getInstance().get_STA_Station(
		// Operator.getRegion());// ========pangben modify 20110523
		// // ((TComboBox)this.getComponent("DEPT_CODE")).setParmValue(dept);
		// TTextFormat station = (TTextFormat)
		// this.getComponent("STATION_CODE");
		// station.setPopupMenuSQL(sql);
		Object obj = this.getParameter();
		TParm parm = new TParm();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
			this.setValue("STATION_CODE", parm.getValue("STATION"));
			this.callFunction("UI|STATION_CODE|setEnabled", false);
		} else {
			// shibl add
			String userId = Operator.getID();
			this.setValue("USER", userId);
			this.setValue("STATION_CODE", "");
			callFunction("UI|USER|onQuery");
		}
		this.getComboBox("IDEPT_CODE").setEnabled(false);
		this.getComboBox("IDEPT_CODE").setValue("");
	}

	public void selectStation() {
		String deptsql = "SELECT DEPT_CODE,DEPT_DESC,PY1 FROM STA_OEI_DEPT_LIST  WHERE  STATION_CODE='"
				+ this.getValueString("STATION_CODE")
				+ "' AND REGION_CODE='"
				+ Operator.getRegion() + "'";
		TParm inparm = new TParm(TJDODBTool.getInstance().select(deptsql));
		if (inparm.getCount() > 0) {
			getComboBox("IDEPT_CODE").setParmValue(inparm);
			getComboBox("IDEPT_CODE").setSelectedIndex(0);
			getComboBox("IDEPT_CODE").setEnabled(true);
		} else {
			getComboBox("IDEPT_CODE").removeAllItems();
			getComboBox("IDEPT_CODE").setEnabled(false);
			getComboBox("IDEPT_CODE").setParmValue(null);
			this.setValue("IDEPT_CODE", "");
		}
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * ��������
	 */
	private void insertData() {
		if (this.getValueString("STATION_CODE").trim().length() <= 0) {
			this.messageBox_("��ѡ������");
			((TTextFormat) this.getComponent("STATION_CODE"))
					.setFocusable(true);
			return;
		}
		if (this.getValueString("IDEPT_CODE").trim().length() <= 0) {
			this.messageBox_("��ѡ����ң�");
			((TComboBox) this.getComponent("IDEPT_CODE")).setFocusable(true);
			return;
		}
		CONFIRM_FLG = false;
		TParm checkRe = new TParm();
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", this.getValue("IDEPT_CODE"));// ����CODE
		parm.setData("STATION_CODE", this.getValue("STATION_CODE"));// ����CODE
		parm.setData("STA_DATE", this.getText("STA_DATE").toString().replace(
				"/", ""));// ��ѯ����
		// ============pangben modify 20110519 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// ============pangben modify 20110519 stop
		// �Ȳ�ѯ�����ڵ������Ƿ����
		checkRe = STAStationLogTool.getInstance().selectSTA_DAILY_01(parm);
		if (checkRe.getErrCode() < 0) {
			this.messageBox("û�в�ѯ�������ڵ�����");
			return;
		}

		// ���ݴ���
		if (checkRe.getCount("STA_DATE") > 0) {
			if (checkRe.getValue("CONFIRM_FLG", 0).equals("Y")) {// �����Ѿ��ύ
				CONFIRM_FLG = true;// �޸ı�� Ϊtrue ��ʾ�Ѿ��ύ �������޸�
				DEPT_CODE = checkRe.getValue("DEPT_CODE", 0);// ��ȡ�м����CODE
				messageBox_("�����Ѿ��ύ�������������ɣ�");
				setTextValue(checkRe);// ҳ�渳ֵ
				selectDate();// ��ѯ��Ժ�ͳ�Ժ�˴�
				this.setValue("Submit", true);// �ύcombo����Ϊѡ��״̬
				return;
			} else {// �������ݲ�û���ύ
				this.setValue("Submit", false);// �ύcombo����Ϊû��ѡ��״̬
				switch (this.messageBox("��ʾ��Ϣ", "�����Ѵ��ڣ��Ƿ��������ɣ�",
						this.YES_NO_OPTION)) {
				case 0:// ����
					break;
				case 1:// ������
					DEPT_CODE = checkRe.getValue("DEPT_CODE", 0);// ��ȡ�м����CODE
					setTextValue(checkRe);// ������
					selectDate();// ��ѯ��Ժ�ͳ�Ժ�˴�
					return;
				}
			}
		}
		// �����м������ ����STA_DAILY_01������
		// TParm result = STAStationLogTool.getInstance().selectData(parm);
		// ��ѯ�ż����м��
		TParm OPD_P = new TParm();
		OPD_P.setData("DEPT_CODE", this.getValue("IDEPT_CODE"));// ����CODE
		OPD_P.setData("STA_DATE", this.getText("STA_DATE").toString().replace(
				"/", ""));// ��ѯ����
		OPD_P.setData("REGION_CODE", Operator.getRegion());// =========pangben
		// modify 20110523
		TParm resultOPD = STAStationLogTool.getInstance().selectSTA_OPD_DAILY(
				OPD_P);
		// ��ѯ�����м��
		TParm Sattion_P = new TParm();
		Sattion_P.setData("DEPT_CODE", this.getValue("IDEPT_CODE"));// ����CODE
		Sattion_P.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));// ��ѯ����
		Sattion_P.setData("REGION_CODE", Operator.getRegion());// =========pangben
		// modify
		Sattion_P.setData("STATION_CODE",this.getValue("STATION_CODE"));//SHIBL 20130702
		
		TParm resultSattion = STAStationLogTool.getInstance()
				.selectSTA_STATION_DAILY(Sattion_P);
		if (resultOPD.getErrCode() < 0 || resultSattion.getErrCode() < 0) {
			this.messageBox_("��ѯʧ�ܣ�");
			this.onClear();
			return;
		}
		// �����������û������
		if (resultOPD.getCount() <= 0 && resultSattion.getCount() <= 0) {
			this.messageBox_("û�в�ѯ��������ڵ�����");
			this.onClear();
			return;
		}
		insertParm = new TParm();// ��¼����STA_DAILY_01��Ĳ���

		insertParm.setData("DATA_02", "0");// ��ע
		insertParm.setData("DATA_03", "0");// ��ע
		insertParm.setData("DATA_05", "0");// Ƥ��
		insertParm.setData("DATA_06", "0");// ���
		insertParm.setData("DATA_04", "0");// ����
		insertParm.setData("DATA_06_1", "0");// ����
		// �����ܼ�����
		if (resultOPD.getCount() > 0) {
			insertParm.setData("DATA_01", resultOPD.getData("ERD_NUM", 0));
			DEPT_CODE = resultOPD.getValue("DEPT_CODE", 0);// �м䵵����code
		} else
			insertParm.setData("DATA_01", "");
		if (resultSattion.getCount() > 0) {// �м���д�������
			DEPT_CODE = resultSattion.getValue("DEPT_CODE", 0);// �м䵵����code
			// ��ȡǰһ������
			String yesterday = getYesterdayString(this.getText("STA_DATE")
					.toString().replace("/", ""));
			if (yesterday.equals("")) {
				System.out.println("����ǰһ�����");
			}
			int ORIGINAL_NUM = 0;
			String sql = " SELECT DATA_16 FROM STA_DAILY_01 WHERE STA_DATE='"
					+ yesterday + "' AND DEPT_CODE='" + DEPT_CODE + "' AND STATION_CODE='" + this.getValue("STATION_CODE") + "'";
			TParm ORIGINAL_NUMparm = new TParm(this.getDBTool().select(sql));
			if (ORIGINAL_NUMparm.getErrCode() < 0) {
				System.out.println("��������ԭ�в���������");
			}
			if (ORIGINAL_NUMparm.getCount() > 0)
				ORIGINAL_NUM = ORIGINAL_NUMparm.getInt("DATA_16", 0);
			insertParm.setData("DATA_07", ORIGINAL_NUM); // ����ԭ�в�����
			insertParm.setData("DATA_08", resultSattion.getInt("ADM_NUM", 0)); // ��Ժ��
			insertParm.setData("DATA_08_1", resultSattion.getInt(
					"FROM_OTHER_DEPT", 0)); // ����ת����
			insertParm.setData("DATA_15_1", resultSattion.getInt(
					"TRANS_DEPT_NUM", 0)); // ת����������
			insertParm.setData("DATA_17", resultSattion
					.getInt("END_BED_NUM", 0)); // ʵ�ʿ��Ų�����
			insertParm.setData("DATA_18", resultSattion
					.getInt("END_BED_NUM", 0)); // ʵ�ʿ��Ų�����
			insertParm.setData("DATA_19", resultSattion.getInt(
					"DS_TOTAL_ADM_DAY", 0)); // ��Ժ��סԺ����
			insertParm.setData("DATA_20", resultSattion.getInt("GET_TIMES", 0)); // Σ�ز��������������
			insertParm.setData("DATA_21", resultSattion.getInt("SUCCESS_TIMES",
					0)); // ���ȳɹ���
			insertParm.setData("DATA_11", resultSattion
					.getInt("RECOVER_NUM", 0)); // �� ��
			insertParm
					.setData("DATA_12", resultSattion.getInt("EFFECT_NUM", 0)); // ��
			// ת
			insertParm.setData("DATA_13", resultSattion
					.getInt("INVALED_NUM", 0)); // δ ��
			insertParm.setData("DATA_14", resultSattion.getInt("DIED_NUM", 0)); // ��
			// ��
			insertParm.setData("DATA_15", resultSattion.getInt("OTHER_NUM", 0)); // ����
			// 10=11+12+13+14
			int DATA_10 = resultSattion.getInt("RECOVER_NUM", 0)
					+ resultSattion.getInt("EFFECT_NUM", 0)
					+ resultSattion.getInt("INVALED_NUM", 0)
					+ resultSattion.getInt("DIED_NUM", 0);
			// 9=11+12+13+14+15
			// int DATA_09 = resultSattion.getInt("RECOVER_NUM", 0) +
			// resultSattion.getInt("EFFECT_NUM", 0) +
			// resultSattion.getInt("INVALED_NUM", 0) +
			// resultSattion.getInt("DIED_NUM", 0) +
			// resultSattion.getInt("OTHER_NUM", 0) ;
			int DATA_16 = ORIGINAL_NUM + resultSattion.getInt("ADM_NUM", 0)
					+ resultSattion.getInt("FROM_OTHER_DEPT", 0)
					- resultSattion.getInt("DS_ADM_NUM", 0)
					- resultSattion.getInt("TRANS_DEPT_NUM", 0);// ע��Ҫ��ȥת�����Ƶ�����
			insertParm.setData("DATA_10", DATA_10); // �������� = 11+12+13+14
			insertParm
					.setData("DATA_09", resultSattion.getInt("DS_ADM_NUM", 0)); // ��Ժ�����ܼ�
			// =11+12+13+14+15
			insertParm.setData("DATA_16", DATA_16); // ʵ�в����� = 7+8+8_1-9-15_1
			// ע��˳��Ҫ�ȼ���09����ܼ���16
		} else {// ����������
			insertParm.setData("DATA_07", ""); // ����ԭ�в�����
			insertParm.setData("DATA_08", ""); // ��Ժ��
			insertParm.setData("DATA_08_1", ""); // ����ת����
			insertParm.setData("DATA_15_1", ""); // ת����������
			insertParm.setData("DATA_17", ""); // ʵ�ʿ��Ų�����
			insertParm.setData("DATA_18", ""); // ʵ�ʿ��Ų�����
			insertParm.setData("DATA_19", ""); // ��Ժ��סԺ����
			insertParm.setData("DATA_20", ""); // Σ�ز���������
			insertParm.setData("DATA_21", ""); // ���ȳɹ���
			insertParm.setData("DATA_11", ""); // �� ��
			insertParm.setData("DATA_12", ""); // �� ת
			insertParm.setData("DATA_13", ""); // δ ��
			insertParm.setData("DATA_14", ""); // �� ��
			insertParm.setData("DATA_15", ""); // ����
			insertParm.setData("DATA_10", ""); // �������� = 11+12+13+14
			insertParm.setData("DATA_09", ""); // ��Ժ�����ܼ� =11+12+13+14+15
			insertParm.setData("DATA_16", ""); // ʵ�в����� = 7+8+8_1-9
			// ע��˳��Ҫ�ȼ���09����ܼ���16
		}
		insertParm.setData("DATA_22", resultSattion.getInt("CARE_NUMS", 0));// ������
		insertParm.setData("DATA_23", resultSattion.getInt("VIP_NUM", 0)); //�����ù����� add by wanglong 20140304
		insertParm.setData("DATA_24", resultSattion.getInt("BMP_NUM", 0)); //���������� 
		insertParm.setData("DATA_25", resultSattion.getInt("LUP_NUM", 0)); //��������� add end
		insertParm.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		insertParm.setData("DEPT_CODE", DEPT_CODE);
		insertParm.setData("STATION_CODE", this.getValue("STATION_CODE")); // Ŀǰ���������code��ͬ
		insertParm.setData("CONFIRM_FLG", "N"); // �ύ��ʶ Ĭ��Ϊ��N��
		insertParm.setData("CONFIRM_USER", Operator.getID()); // ȷ����
		insertParm.setData("OPT_USER", Operator.getID());
		insertParm.setData("OPT_TERM", Operator.getIP());
		// ==============pangben modify 20110520 start ���������
		insertParm.setData("REGION_CODE", Operator.getRegion());
		TParm newParm = new TParm();// �ܲ���
		TParm parmDel = new TParm();// ɾ�� ����
		parmDel.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		parmDel.setData("DEPT_CODE", DEPT_CODE);
		parmDel.setData("STATION_CODE", this.getValue("STATION_CODE"));
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parmDel.setData("REGION_CODE", Operator.getRegion());
		// ==============pangben modify 20110520 stop
		// ���ò���
		newParm.setData("Del", parmDel.getData());// ����ɾ������
		newParm.setData("Insert", insertParm.getData());// ������Ӳ���
		TParm re = TIOM_AppServer.executeAction(
				"action.sta.STAStationLogAction", "creatData", newParm);
		if (re.getErrCode() < 0) {
			this.messageBox_("����ʧ�ܣ�");
			return;
		}
		this.clearValue("Submit");
		// ��ѯ������
		TParm selectNew = STAStationLogTool.getInstance().selectSTA_DAILY_01(
				insertParm);
		if (selectNew.getErrCode() < 0) {
			this.messageBox_("��ѯʧ�ܣ�");
			return;
		}
		setTextValue(selectNew);
		selectDate();// ��ѯ��Ժ�ͳ�Ժ�˴�
		this.messageBox_("�������");
	}

	/**
	 * ���ؼ���ֵ
	 * 
	 * @param parm
	 *            TParm
	 */
	private void setTextValue(TParm parm) {
		this.getNumText("DATA_02").setValue(parm.getInt("DATA_02", 0));
		this.getNumText("DATA_03").setValue(parm.getInt("DATA_03", 0));
		this.getNumText("DATA_04").setValue(parm.getInt("DATA_04", 0));
		this.getNumText("DATA_05").setValue(parm.getInt("DATA_05", 0));
		this.getNumText("DATA_06").setValue(parm.getInt("DATA_06", 0));
		this.getNumText("DATA_06_1").setValue(parm.getInt("DATA_06_1", 0));
		this.getNumText("DATA_01").setValue(parm.getInt("DATA_01", 0));
		this.getNumText("DATA_07").setValue(parm.getInt("DATA_07", 0));
		setValue("DATA_08", parm.getInt("DATA_08", 0));
		setValue("DATA_08_1", parm.getInt("DATA_08_1", 0));
		setValue("DATA_15_1", parm.getInt("DATA_15_1", 0));
		this.getNumText("DATA_16").setValue(parm.getInt("DATA_16", 0));
		this.getNumText("DATA_17").setValue(parm.getInt("DATA_17", 0));
		this.getNumText("DATA_18").setValue(parm.getInt("DATA_18", 0));
		this.getNumText("DATA_19").setValue(parm.getInt("DATA_19", 0));
		this.getNumText("DATA_20").setValue(parm.getInt("DATA_20", 0));
		this.getNumText("DATA_21").setValue(parm.getInt("DATA_21", 0));
		this.getNumText("DATA_22").setValue(parm.getInt("DATA_22", 0));
		setValue("DATA_11", parm.getInt("DATA_11", 0));
		setValue("DATA_12", parm.getInt("DATA_12", 0));
		setValue("DATA_13", parm.getInt("DATA_13", 0));
		setValue("DATA_14", parm.getInt("DATA_14", 0));
		setValue("DATA_15", parm.getInt("DATA_15", 0));
		this.getNumText("DATA_10").setValue(parm.getInt("DATA_10", 0));
		this.getNumText("DATA_09").setValue(parm.getInt("DATA_09", 0));
		this.getNumText("DATA_23").setValue(parm.getInt("DATA_23", 0)); //�����ù����� add by wanglong 20140304
		this.getNumText("DATA_24").setValue(parm.getInt("DATA_24", 0)); //����������
		this.getNumText("DATA_25").setValue(parm.getInt("DATA_25", 0)); //��������� add end
	}

	/**
	 * ����������ѡ���¼�
	 */
	public void onStationChoose() {
		String code = this.getValue("STATION_CODE").toString();
		this.setValue("DEPT_CODE", code);
	}

	/**
	 * ��ȡǰһ��������ַ���
	 * 
	 * @param date
	 *            String ��ʽ YYYYMMDD
	 * @return String
	 */
	private String getYesterdayString(String date) {
		String yesterday = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date d;
		try {
			d = df.parse(date);
			Calendar ctest = Calendar.getInstance();
			ctest.setTime(d);
			ctest.add(Calendar.DATE, -1);
			d = ctest.getTime();
			yesterday = df.format(d);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return yesterday;
	}
	
    /**
	 * ���Excel
	 */
	public void onExport() {//add by wangbin 20140707
		TTable tableIN = (TTable) this.getComponent("Table_IN");//��Ժ����
		TTable TableOUT = (TTable) this.getComponent("Table_OUT");//��Ժ����
		TTable TableINPR = (TTable) this.getComponent("Table_INPR");//ת��Ʋ���
		TTable TableOUPR = (TTable) this.getComponent("Table_OUPR");//ת���Ʋ���
		
		if (tableIN.getRowCount() < 1 && TableOUT.getRowCount() < 1
				&& TableINPR.getRowCount() < 1 && TableOUPR.getRowCount() < 1) {
			this.messageBox("û����Ҫ����������");
			return;
		} else {
			String[] header;
			List<TParm> parmList = new ArrayList<TParm>();
			//��Ժ������������
			TParm parmIN = tableIN.getShowParmValue();
			if (tableIN.getRowCount() > 0) {
				parmIN.setData("TITLE", "��Ժ����");
				parmIN.setData("HEAD",tableIN.getHeader());
				header = tableIN.getParmMap().split(";");
		        for (int i = 0; i < header.length; i++) {
		        	parmIN.addData("SYSTEM", "COLUMNS", header[i]);
		        }
		        
		        parmList.add(parmIN);
			}
	        
	        //��Ժ������������
			TParm parmOut = TableOUT.getShowParmValue();
			if (TableOUT.getRowCount() > 0) {
				parmOut.setData("TITLE", "��Ժ����");
				parmOut.setData("HEAD",TableOUT.getHeader());
				header = TableOUT.getParmMap().split(";");
				
		        for (int i = 0; i < header.length; i++) {
		        	parmOut.addData("SYSTEM", "COLUMNS", header[i]);
		        }
		        
		        parmList.add(parmOut);
			}
	        
	        //ת��Ʋ�����������
			TParm parmINPR = TableINPR.getShowParmValue();
			if (TableINPR.getRowCount() > 0) {
				parmINPR.setData("TITLE", "ת��Ʋ���");
				parmINPR.setData("HEAD",TableINPR.getHeader());
				header = TableINPR.getParmMap().split(";");
				
		        for (int i = 0; i < header.length; i++) {
		        	parmINPR.addData("SYSTEM", "COLUMNS", header[i]);
		        }
		        
		        parmList.add(parmINPR);
			}
	        
	        //ת���Ʋ�����������
			TParm parmOUPR = TableOUPR.getShowParmValue();
			if (TableOUPR.getRowCount() > 0) {
				parmOUPR.setData("TITLE", "ת���Ʋ���");
				parmOUPR.setData("HEAD",TableOUPR.getHeader());
				header = TableOUPR.getParmMap().split(";");
				
		        for (int i = 0; i < header.length; i++) {
		        	parmOUPR.addData("SYSTEM", "COLUMNS", header[i]);
		        }
		        
		        parmList.add(parmOUPR);
			}
	        
	        TParm[] execleTable = new TParm[parmList.size()];
	        for (int i = 0; i < parmList.size(); i++) {
	        	execleTable[i] = parmList.get(i);
	        }
	        
	        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "������־");
		}
	}

	/**
	 * getDBTool ���ݿ⹤��ʵ��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

}
