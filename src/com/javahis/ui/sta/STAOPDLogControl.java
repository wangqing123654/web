package com.javahis.ui.sta;

import java.sql.Timestamp;

import jdo.sta.STADeptListTool;
import jdo.sta.STAOPDLogTool;
import jdo.sta.STAOpdDailyTool;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: �ż�����־
 * </p>
 * 
 * <p>
 * Description: �ż�����־
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-7-7
 * @version 1.0
 */
public class STAOPDLogControl extends TControl {
	private boolean STA_CONFIRM_FLG = false;// ��¼�Ƿ��ύ
	private String DATA_StaDate = "";// ��¼Ŀǰ��ʾ�����ݵ�����(����)

	/**
	 * ��ʼ��
	 */
    public void onInit() {
        super.init();
        this.initData();// ��ʼ������
        this.onQuery();
    }

	/**
	 * ��ʼ������
	 */
    public void initData() {
        // ��ȡ���������
        Timestamp yestaday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("STA_DATE", yestaday);
        STA_CONFIRM_FLG = false;
        this.setValue("Submit", false);
    }
	

    /**
     * table���ݰ�
     * @param STA_DATE
     * @param regionCode
     */
    public void tableBind(String STA_DATE, String regionCode) {
        TTable table = (TTable) this.getComponent("Table");
        // ========pangben modify 20110519 start ����������
        String sql = STASQLTool.getInstance().getSTAOPDLog(STA_DATE, regionCode);
        // ========pangben modify 20110519 stop
        TParm parm = new TParm();
        TParm dataParm = new TParm(this.getDBTool().select(sql));
        int OUTP_NUM_count = 0;// ����ϼ�
        int ERD_NUM_count = 0;// ����ϼ�
        int HRM_NUM_count = 0;// ����ϼ�
        int OTHER_NUM_count = 0;// �����ϼ�
        int GET_TIMES_count = 0;// ���Ⱥϼ�
        int PROF_DR_count = 0;// ר����ϼ�
        int COMM_DR_count = 0;// ��ͨ��ϼ�
        int DR_HOURS_count = 0;// ����Сʱ�ϼ�
        int SUCCESS_TIMES_count = 0;// ���ȳɹ������ϼ�
        int OBS_NUM_count = 0;// �����˴κϼ�
        int ERD_DIED_NUM_count = 0;// �������������ϼ�
        int OBS_DIED_NUM_count = 0;// �������������ϼ�
        int OPE_NUM_count = 0;// ���������ϼ�
        int FIRST_NUM_count = 0;// �����˴κϼ�
        int FURTHER_NUM_count = 0;// �����˴κϼ�
        int APPT_NUM_count = 0;// ԤԼ�˴κϼ�
        int ZR_DR_NUM_count = 0;// ���θ������˴κϼ�
        int ZZ_DR_NUM_count = 0;// �����˴κϼ�
        int ZY_DR_NUM_count = 0;// סԺ�˴κϼ�
        int ZX_DR_NUM_count = 0;// �����˴κϼ�
        for (int i = 0; i < dataParm.getCount(); i++) {
            parm.addData("DEPT_CODE", dataParm.getValue("DEPT_CODE", i));
            parm.addData("OUTP_NUM", dataParm.getValue("OUTP_NUM", i));
            parm.addData("ERD_NUM", dataParm.getValue("ERD_NUM", i));
            parm.addData("HRM_NUM", dataParm.getValue("HRM_NUM", i));
            parm.addData("OTHER_NUM", dataParm.getValue("OTHER_NUM", i));
            parm.addData("GET_TIMES", dataParm.getValue("GET_TIMES", i));
            parm.addData("PROF_DR", dataParm.getValue("PROF_DR", i));
            parm.addData("COMM_DR", dataParm.getValue("COMM_DR", i));
            parm.addData("DR_HOURS", dataParm.getValue("DR_HOURS", i));
            parm.addData("SUCCESS_TIMES", dataParm.getValue("SUCCESS_TIMES", i));
            parm.addData("OBS_NUM", dataParm.getValue("OBS_NUM", i));
            parm.addData("ERD_DIED_NUM", dataParm.getValue("ERD_DIED_NUM", i));
            parm.addData("OBS_DIED_NUM", dataParm.getValue("OBS_DIED_NUM", i));
            parm.addData("OPE_NUM", dataParm.getValue("OPE_NUM", i));
            parm.addData("FIRST_NUM", dataParm.getValue("FIRST_NUM", i));
            parm.addData("FURTHER_NUM", dataParm.getValue("FURTHER_NUM", i));
            parm.addData("APPT_NUM", dataParm.getValue("APPT_NUM", i));
            parm.addData("ZR_DR_NUM", dataParm.getValue("ZR_DR_NUM", i));
            parm.addData("ZZ_DR_NUM", dataParm.getValue("ZZ_DR_NUM", i));
            parm.addData("ZY_DR_NUM", dataParm.getValue("ZY_DR_NUM", i));
            parm.addData("ZX_DR_NUM", dataParm.getValue("ZX_DR_NUM", i));
            OUTP_NUM_count += dataParm.getInt("OUTP_NUM", i);
            ERD_NUM_count += dataParm.getInt("ERD_NUM", i);
            HRM_NUM_count += dataParm.getInt("HRM_NUM", i);
            OTHER_NUM_count += dataParm.getInt("OTHER_NUM", i);
            GET_TIMES_count += dataParm.getInt("GET_TIMES", i);
            PROF_DR_count += dataParm.getInt("PROF_DR", i);
            COMM_DR_count += dataParm.getInt("COMM_DR", i);
            DR_HOURS_count += dataParm.getInt("DR_HOURS", i);
            SUCCESS_TIMES_count += dataParm.getInt("SUCCESS_TIMES", i);
            OBS_NUM_count += dataParm.getInt("OBS_NUM", i);
            ERD_DIED_NUM_count += dataParm.getInt("ERD_DIED_NUM", i);
            OBS_DIED_NUM_count += dataParm.getInt("OBS_DIED_NUM", i);
            OPE_NUM_count += dataParm.getInt("OPE_NUM", i);
            FIRST_NUM_count += dataParm.getInt("FIRST_NUM", i);
            FURTHER_NUM_count += dataParm.getInt("FURTHER_NUM", i);
            APPT_NUM_count += dataParm.getInt("APPT_NUM", i);
            ZR_DR_NUM_count += dataParm.getInt("ZR_DR_NUM", i);
            ZZ_DR_NUM_count += dataParm.getInt("ZZ_DR_NUM", i);
            ZY_DR_NUM_count += dataParm.getInt("ZY_DR_NUM", i);
            ZX_DR_NUM_count += dataParm.getInt("ZX_DR_NUM", i);
        }
        parm.addData("DEPT_CODE", "�ϼƣ�");
        parm.addData("OUTP_NUM", OUTP_NUM_count);
        parm.addData("ERD_NUM", ERD_NUM_count);
        parm.addData("HRM_NUM", HRM_NUM_count);
        parm.addData("OTHER_NUM", OTHER_NUM_count);
        parm.addData("GET_TIMES", GET_TIMES_count);
        parm.addData("PROF_DR", PROF_DR_count);
        parm.addData("COMM_DR", COMM_DR_count);
        parm.addData("DR_HOURS", DR_HOURS_count);
        parm.addData("SUCCESS_TIMES", SUCCESS_TIMES_count);
        parm.addData("OBS_NUM", OBS_NUM_count);
        parm.addData("ERD_DIED_NUM", ERD_DIED_NUM_count);
        parm.addData("OBS_DIED_NUM", OBS_DIED_NUM_count);
        parm.addData("OPE_NUM", OPE_NUM_count);
        parm.addData("FIRST_NUM", FIRST_NUM_count);
        parm.addData("FURTHER_NUM", FURTHER_NUM_count);
        parm.addData("APPT_NUM", APPT_NUM_count);
        parm.addData("ZR_DR_NUM", ZR_DR_NUM_count);
        parm.addData("ZZ_DR_NUM", ZZ_DR_NUM_count);
        parm.addData("ZY_DR_NUM", ZY_DR_NUM_count);
        parm.addData("ZX_DR_NUM", ZX_DR_NUM_count);
        parm.setCount(parm.getCount("OUTP_NUM"));
        table.setParmValue(parm);
        // table.setSQL(sql);
        // table.retrieve();
        // table.setDSValue();
        DATA_StaDate = STA_DATE;
    }



	/**
	 * ����
	 */
	public void onSave() {
	    // �Ƿ��ύ
        boolean submit = ((TCheckBox) this.getComponent("Submit")).isSelected();
        String STADATE = this.getText("STA_DATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("��ѡ������");
            return;
        }
        int reFlg =
                STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY", STADATE,
                                                       Operator.getRegion());
        if (submit && reFlg == 2) {//modify by wanglong 20140212
            // if (STA_CONFIRM_FLG) {
            this.messageBox_("�����Ѿ��ύ�������޸�");
            return;
        }
		TTable table = (TTable) this.getComponent("Table");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		// TDataStore ds = table.getDataStore();
		String optUser = Operator.getID();
		String optIp = Operator.getIP();
		// ��ȡ������ʱ��
		Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
		String message = "�޸ĳɹ���";// ��ʾ��
		if (submit)
			message = "�ύ�ɹ���";
		TParm parm = new TParm();
		TParm result = new TParm();
		for (int i = 0; i < tableParm.getCount("DEPT_CODE")-1; i++) {
			parm.setData("STA_DATE", STADATE);
			parm.setData("DEPT_CODE", tableParm.getValue("DEPT_CODE", i));
			parm.setData("OUTP_NUM", tableParm.getInt("OUTP_NUM", i));
			parm.setData("ERD_NUM", tableParm.getInt("ERD_NUM", i));
			parm.setData("HRM_NUM", tableParm.getInt("HRM_NUM", i));
			parm.setData("OTHER_NUM", tableParm.getInt("OTHER_NUM", i));
			parm.setData("GET_TIMES", tableParm.getInt("GET_TIMES", i));
			parm.setData("PROF_DR", tableParm.getInt("PROF_DR", i));
			parm.setData("COMM_DR", tableParm.getInt("COMM_DR", i));
			parm.setData("DR_HOURS", tableParm.getInt("DR_HOURS", i));
			parm.setData("SUCCESS_TIMES", tableParm.getInt("SUCCESS_TIMES", i));
			parm.setData("OBS_NUM", tableParm.getInt("OBS_NUM", i));
			parm.setData("ERD_DIED_NUM", tableParm.getInt("ERD_DIED_NUM", i));
			parm.setData("OBS_DIED_NUM", tableParm.getInt("OBS_DIED_NUM", i));
			parm.setData("OPE_NUM", tableParm.getInt("OPE_NUM", i));
			parm.setData("FIRST_NUM", tableParm.getInt("FIRST_NUM", i));
			parm.setData("FURTHER_NUM", tableParm.getInt("FURTHER_NUM", i));
			parm.setData("APPT_NUM", tableParm.getInt("APPT_NUM", i));
			parm.setData("ZR_DR_NUM", tableParm.getInt("ZR_DR_NUM", i));
			parm.setData("ZZ_DR_NUM", tableParm.getInt("ZZ_DR_NUM", i));
			parm.setData("ZY_DR_NUM", tableParm.getInt("ZY_DR_NUM", i));
			parm.setData("ZX_DR_NUM", tableParm.getInt("ZX_DR_NUM", i));
			// �ж��Ƿ��ύ
			if (submit) {
				parm.setData("CONFIRM_FLG", "Y");
				parm.setData("CONFIRM_USER", optUser);
				parm.setData("CONFIRM_DATE", CONFIRM_DATE);
			} else {
				parm.setData("CONFIRM_FLG", "N");
				parm.setData("CONFIRM_USER", "");
				parm.setData("CONFIRM_DATE", "");
			}
			parm.setData("OPT_USER", optUser);
			parm.setData("OPT_TERM", optIp);
			parm.setData("OPT_DATE", CONFIRM_DATE);
			// ==============pangben modify 20110519 start
			parm.setData("REGION_CODE", Operator.getRegion());
			// ==============pangben modify 20110519 stop
			result = STAOPDLogTool.getInstance().update_STA_OPD_DAILY(parm);
		}
		if (result.getErrCode() < 0) {
			this.messageBox_("����ʧ�ܣ�");
		} else {
			this.messageBox_(message);
			if (submit)
				STA_CONFIRM_FLG = true;
		}

	}

	/**
	 * ����ָ�����ڵ�������Ϣ
	 */
	public void onGenerate() {
		if (STA_CONFIRM_FLG) {
			this.messageBox_("�����Ѿ��ύ���������µ���!");
			return;
		}
		String STADATE = this.getText("STA_DATE").replace("/", "");
		if (STADATE.trim().length() <= 0) {
			this.messageBox_("��ѡ�����ڣ�");
			return;
		}
		// ��ȡѡ�����ڵ�ǰһ�������
		Timestamp time = StringTool.rollDate(
				StringTool.getTimestamp(STADATE, "yyyyMMdd"), -1);
		String lastDay = StringTool.getString(time, "yyyyMMdd");
		TParm checkLastDay = new TParm();
		checkLastDay.setData("STA_DATE", lastDay);
		// ==========pangben modify 20110519 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			checkLastDay.setData("REGION_CODE", Operator.getRegion());
		// ==========pangben modify 20110519 stop
		// ��ȡѡ�����ڵ�ǰһ�������
		TParm check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(
				checkLastDay);
		if (check.getCount("STA_DATE") <= 0) { // ���ǰһ�����ݲ����ڣ��������룬��Ӱ������׼ȷ��
			switch (this.messageBox("��ʾ��Ϣ",
					StringTool.getString(time, "yyyy��MM��dd��")
							+ "�����ݲ�����\n���������ݻ�Ӱ��׼ȷ��\n�Ƿ��룿", this.YES_NO_OPTION)) {
			case 0: // ����
				break;
			case 1: // ������
				return;
			}
		}
		// ���Ҫ����������Ƿ��Ѿ�����
		TParm checkDay = new TParm();
		checkDay.setData("STA_DATE", STADATE);
		// ==========pangben modify 20110519 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			checkDay.setData("REGION_CODE", Operator.getRegion());
		// ==========pangben modify 20110519 stop
		check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(checkDay);
		if (check.getCount("STA_DATE") > 0) { // ������ݴ��ڣ�ѯ���Ƿ���
			switch (this
					.messageBox("��ʾ��Ϣ", "�����Ѵ��ڣ��Ƿ��������ɣ�", this.YES_NO_OPTION)) {
			case 0: // ����
				break;
			case 1: // ������
				return;
			}
		}
		TParm sql = new TParm();
		sql.setData("ADMDATE", STADATE);
		sql.setData("OPT_USER", Operator.getID());
		sql.setData("OPT_TERM", Operator.getIP());
		// =================pangben modify 20110519 start �������
		sql.setData("REGION_CODE", Operator.getRegion());
		// =================pangben modify 20110519 stop
		TParm dept = new TParm();
		// =========pangben modify 20110519 start ����������
		dept = STADeptListTool.getInstance()
				.selectOE_DEPT(Operator.getRegion());
		// =========pangben modify 20110519 stop
		if (dept.getCount() != 0) {
			TParm parm = new TParm();
			parm.setData("SQL", sql.getData());
			parm.setData("DEPT", dept.getData());
			TParm result = TIOM_AppServer.executeAction(
					"action.sta.STADailyAction", "insertSTA_OPD_DAILY", parm);
			if (result.getErrCode() < 0) {
				System.out.println("" + result);
				return;
			}
			this.messageBox_("�����м䵵����ɹ���");
			// =========pangben modify 20110519
		} else
			this.messageBox_("û����Ҫ����������м䵵���ݣ�");
		initData();
	}


    /**
     * ��ѯ
     */
    public void onQuery() {
        String STADATE = this.getText("STA_DATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("��ѡ������");
            return;
        }
        // ===========pangben modify 20110519 ����������
        tableBind(STADATE, Operator.getRegion());
        // ===========pangben modify 20110519 ����������
        STA_CONFIRM_FLG = false;
        this.setValue("Submit", false);
        // �������״̬
        int reFlg =
                STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY", STADATE,
                                                       Operator.getRegion());
        if (reFlg == 2) {// �Ѿ��ύ
            STA_CONFIRM_FLG = true;
            this.setValue("Submit", true);
        }
    }
    
	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (DATA_StaDate.trim().length() <= 0) {// û��ѡ��ͳ������
			return;
		}
		// =======pangben modify 20110519 start ����������
		TParm data = STAOPDLogTool.getInstance().getPrintData(DATA_StaDate,
				Operator.getRegion());
		// =======pangben modify 20110519 stop
		data.setCount(data.getCount("STA_DATE"));// ��������
		data.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
		data.addData("SYSTEM", "COLUMNS", "OUTP_NUM");
		data.addData("SYSTEM", "COLUMNS", "ERD_NUM");
		data.addData("SYSTEM", "COLUMNS", "HRM_NUM");
		data.addData("SYSTEM", "COLUMNS", "OTHER_NUM");
		data.addData("SYSTEM", "COLUMNS", "GET_TIMES");
		data.addData("SYSTEM", "COLUMNS", "PROF_DR");
		data.addData("SYSTEM", "COLUMNS", "COMM_DR");
		data.addData("SYSTEM", "COLUMNS", "DR_HOURS");
		data.addData("SYSTEM", "COLUMNS", "SUCCESS_TIMES");
		data.addData("SYSTEM", "COLUMNS", "OBS_NUM");
		data.addData("SYSTEM", "COLUMNS", "ERD_DIED_NUM");
		data.addData("SYSTEM", "COLUMNS", "OBS_DIED_NUM");
		data.addData("SYSTEM", "COLUMNS", "OPE_NUM");
		data.addData("SYSTEM", "COLUMNS", "FIRST_NUM");
		data.addData("SYSTEM", "COLUMNS", "FURTHER_NUM");
		TParm printParm = new TParm();
		printParm.setData("T1", data.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STAOPDLog.jhw",
				printParm);
	}


    
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("STA_DATE;Submit");
        TTable table = (TTable) this.getComponent("Table");
        table.removeRowAll();
        table.resetModify();
        this.initData();
    }
    

    /**
     * ��Ԫ��ֵ�ı��¼�
     */
    public void onChangeTableValue(){
        TTable table = (TTable) this.getComponent("Table");
        table.acceptText();
        TParm parm = new TParm();
        TParm dataParm = table.getParmValue();
        int OUTP_NUM_count = 0;// ����ϼ�
        int ERD_NUM_count = 0;// ����ϼ�
        int HRM_NUM_count = 0;// ����ϼ�
        int OTHER_NUM_count = 0;// �����ϼ�
        int GET_TIMES_count = 0;// ���Ⱥϼ�
        int PROF_DR_count = 0;// ר����ϼ�
        int COMM_DR_count = 0;// ��ͨ��ϼ�
        int DR_HOURS_count = 0;// ����Сʱ�ϼ�
        int SUCCESS_TIMES_count = 0;// ���ȳɹ������ϼ�
        int OBS_NUM_count = 0;// �����˴κϼ�
        int ERD_DIED_NUM_count = 0;// �������������ϼ�
        int OBS_DIED_NUM_count = 0;// �������������ϼ�
        int OPE_NUM_count = 0;// ���������ϼ�
        int FIRST_NUM_count = 0;// �����˴κϼ�
        int FURTHER_NUM_count = 0;// �����˴κϼ�
        int APPT_NUM_count = 0;// ԤԼ�˴κϼ�
        int ZR_DR_NUM_count = 0;// ���θ������˴κϼ�
        int ZZ_DR_NUM_count = 0;// �����˴κϼ�
        int ZY_DR_NUM_count = 0;// סԺ�˴κϼ�
        int ZX_DR_NUM_count = 0;// �����˴κϼ�
        for (int i = 0; i < dataParm.getCount()-1; i++) {
            parm.addData("DEPT_CODE", dataParm.getValue("DEPT_CODE", i));
            parm.addData("OUTP_NUM", dataParm.getValue("OUTP_NUM", i));
            parm.addData("ERD_NUM", dataParm.getValue("ERD_NUM", i));
            parm.addData("HRM_NUM", dataParm.getValue("HRM_NUM", i));
            parm.addData("OTHER_NUM", dataParm.getValue("OTHER_NUM", i));
            parm.addData("GET_TIMES", dataParm.getValue("GET_TIMES", i));
            parm.addData("PROF_DR", dataParm.getValue("PROF_DR", i));
            parm.addData("COMM_DR", dataParm.getValue("COMM_DR", i));
            parm.addData("DR_HOURS", dataParm.getValue("DR_HOURS", i));
            parm.addData("SUCCESS_TIMES", dataParm.getValue("SUCCESS_TIMES", i));
            parm.addData("OBS_NUM", dataParm.getValue("OBS_NUM", i));
            parm.addData("ERD_DIED_NUM", dataParm.getValue("ERD_DIED_NUM", i));
            parm.addData("OBS_DIED_NUM", dataParm.getValue("OBS_DIED_NUM", i));
            parm.addData("OPE_NUM", dataParm.getValue("OPE_NUM", i));
            parm.addData("FIRST_NUM", dataParm.getValue("FIRST_NUM", i));
            parm.addData("FURTHER_NUM", dataParm.getValue("FURTHER_NUM", i));
            parm.addData("APPT_NUM", dataParm.getValue("APPT_NUM", i));
            parm.addData("ZR_DR_NUM", dataParm.getValue("ZR_DR_NUM", i));
            parm.addData("ZZ_DR_NUM", dataParm.getValue("ZZ_DR_NUM", i));
            parm.addData("ZY_DR_NUM", dataParm.getValue("ZY_DR_NUM", i));
            parm.addData("ZX_DR_NUM", dataParm.getValue("ZX_DR_NUM", i));
            OUTP_NUM_count += dataParm.getInt("OUTP_NUM", i);
            ERD_NUM_count += dataParm.getInt("ERD_NUM", i);
            HRM_NUM_count += dataParm.getInt("HRM_NUM", i);
            OTHER_NUM_count += dataParm.getInt("OTHER_NUM", i);
            GET_TIMES_count += dataParm.getInt("GET_TIMES", i);
            PROF_DR_count += dataParm.getInt("PROF_DR", i);
            COMM_DR_count += dataParm.getInt("COMM_DR", i);
            DR_HOURS_count += dataParm.getInt("DR_HOURS", i);
            SUCCESS_TIMES_count += dataParm.getInt("SUCCESS_TIMES", i);
            OBS_NUM_count += dataParm.getInt("OBS_NUM", i);
            ERD_DIED_NUM_count += dataParm.getInt("ERD_DIED_NUM", i);
            OBS_DIED_NUM_count += dataParm.getInt("OBS_DIED_NUM", i);
            OPE_NUM_count += dataParm.getInt("OPE_NUM", i);
            FIRST_NUM_count += dataParm.getInt("FIRST_NUM", i);
            FURTHER_NUM_count += dataParm.getInt("FURTHER_NUM", i);
            APPT_NUM_count += dataParm.getInt("APPT_NUM", i);
            ZR_DR_NUM_count += dataParm.getInt("ZR_DR_NUM", i);
            ZZ_DR_NUM_count += dataParm.getInt("ZZ_DR_NUM", i);
            ZY_DR_NUM_count += dataParm.getInt("ZY_DR_NUM", i);
            ZX_DR_NUM_count += dataParm.getInt("ZX_DR_NUM", i);
        }
        parm.addData("DEPT_CODE", "�ϼƣ�");
        parm.addData("OUTP_NUM", OUTP_NUM_count);
        parm.addData("ERD_NUM", ERD_NUM_count);
        parm.addData("HRM_NUM", HRM_NUM_count);
        parm.addData("OTHER_NUM", OTHER_NUM_count);
        parm.addData("GET_TIMES", GET_TIMES_count);
        parm.addData("PROF_DR", PROF_DR_count);
        parm.addData("COMM_DR", COMM_DR_count);
        parm.addData("DR_HOURS", DR_HOURS_count);
        parm.addData("SUCCESS_TIMES", SUCCESS_TIMES_count);
        parm.addData("OBS_NUM", OBS_NUM_count);
        parm.addData("ERD_DIED_NUM", ERD_DIED_NUM_count);
        parm.addData("OBS_DIED_NUM", OBS_DIED_NUM_count);
        parm.addData("OPE_NUM", OPE_NUM_count);
        parm.addData("FIRST_NUM", FIRST_NUM_count);
        parm.addData("FURTHER_NUM", FURTHER_NUM_count);
        parm.addData("APPT_NUM", APPT_NUM_count);
        parm.addData("ZR_DR_NUM", ZR_DR_NUM_count);
        parm.addData("ZZ_DR_NUM", ZZ_DR_NUM_count);
        parm.addData("ZY_DR_NUM", ZY_DR_NUM_count);
        parm.addData("ZX_DR_NUM", ZX_DR_NUM_count);;
        parm.setCount(parm.getCount("OUTP_NUM"));
        table.setParmValue(parm);
    }
    
    /**
	 * ���Excel
	 */
	public void onExport() {//add by wangbin 20140707
		TTable table = (TTable) this.getComponent("Table");
		if (table.getRowCount() > 1) {
			ExportExcelUtil.getInstance().exportExcel(table, "�ż�����־");
		} else {
			this.messageBox("û����Ҫ����������");
			return;
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
