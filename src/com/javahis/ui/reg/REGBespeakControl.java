package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.bil.BIL;
import jdo.ekt.EKTIO;
import jdo.odo.OPDAbnormalRegTool;
import jdo.reg.ClinicRoomTool;
import jdo.reg.REGClinicQueTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;

/**
 * <p>
 * Title:ҽ�ƿ�������ԤԼ�Һ�
 * </p>
 * 
 * <p>
 * Description:����ִ��ԤԼ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangben
 * @version 4.0.1
 */
public class REGBespeakControl extends TControl {
	/**
	 * ��ʼ��
	 */
	private String admType = "O";// �ż���
	private Pat pat;
	private String CASE_NO;
	private int queNo=-1;//����˳���
	private String regAdmTime="";//ԤԼʱ��
	private boolean VIP=false;
	public void onInit() {
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		admType = parm.getValue("ADM_TYPE");

		pageInit();
	}

	/**
	 * ҳ���ʼ��
	 */
	public void pageInit() {
		initCombo();
		initClinicRoom();
		initSchDay();
	}

	/**
	 * ��ʼ��combo
	 */
	public void initCombo() {
		// ��ʼ������
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());
		// ��ʼ��ʱ��
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		// ��ʼ���Ʊ���Ա
		this.setValue("SERVICE_LEVEL", "1");
		// ����
		if (admType.equals("E")) {
			callFunction("UI|ERD_LEVEL_TITLE|setVisible", true);
			callFunction("UI|ERD_LEVEL|setVisible", true);
		}
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("REGMETHOD_CODE", "A");
		
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	/**
	 * ��ʼ�����combo
	 */
	public void initClinicRoom() {
		TParm pa = new TParm();
		pa.setData("ADM_TYPE", admType);
		pa.setData("ADM_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd"));
		pa.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		pa.setData("REGION_CODE", Operator.getRegion());
		TParm parm = ClinicRoomTool.getInstance().getNotUseForODO(pa);
		TTextFormat tf = (TTextFormat) this.getComponent("CLINICROOM_NO");
		tf.setPopupMenuData(parm);
		tf.setComboSelectRow();
		tf.popupMenuShowData();
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	public void onMrNo() {
		pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
		if (pat == null) {
			this.messageBox_("�޴˲���");
			return;
		}
		this.setValue("MR_NO", pat.getMrNo());
		this.setValue("SEX_CODE", pat.getSexCode());
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("CTZ1_CODE", pat.getCtz1Code());
		this.setValue("CTZ2_CODE", pat.getCtz2Code());
		this.setValue("CTZ3_CODE", pat.getCtz3Code());
		this.setValue("BIRTHDAY", pat.getBirthday());
	}

	// /**
	// * ���ѡ���¼�
	// */
	// public void onClickClinicType(){
	// double reg_fee =
	// BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
	// "REG_FEE",getValueString("CTZ1_CODE"),
	// getValueString("CTZ2_CODE"),
	// getValueString("CTZ3_CODE"),
	// this.getValueString("SERVICE_LEVEL"));
	// //�Һŷ�
	// this.setValue("REG_FEE", reg_fee);
	// double clinic_fee =
	// BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
	// "CLINIC_FEE",getValueString("CTZ1_CODE"),
	// getValueString("CTZ2_CODE"),
	// getValueString("CTZ3_CODE"),
	// this.getValueString("SERVICE_LEVEL"));
	// //����
	// this.setValue("CLINIC_FEE", clinic_fee);
	// //�ܷ���
	// setValue("AR_AMT", reg_fee + clinic_fee);
	// }
	/**
	 * ҽ�ƿ�����
	 */
	public void onEKT() {
		TParm patParm = EKTIO.getInstance().TXreadEKT();
		if (patParm.getErrCode() < 0) {
			this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
			return;
		}
		if (patParm.getValue("MR_NO") == null
				|| patParm.getValue("MR_NO").length() == 0) {
			this.messageBox_("����ʧ��");
		} else {
			this.setValue("MR_NO", patParm.getValue("MR_NO"));
			this.onMrNo();
		}
	}

	/***
	 * ����
	 */
	public void onSave() {
		// �������
		if (!checkData()) {
			return;
		}
		if (getValueString("REGMETHOD_CODE").equals("D")) {
			messageBox("���VIP��");
			return;
		} 
		//У���Ƿ�ԤԼ�Һ�
		String sql="SELECT A.VIP_FLG,A.CLINICTYPE_CODE,B.CLINICAREA_CODE,A.QUE_NO FROM REG_SCHDAY A,REG_CLINICROOM B" +
				" WHERE A.CLINICROOM_NO = B.CLINICROOM_NO AND A.REGION_CODE='"+this.getValue("REGION_CODE")+"'"+
		        " AND A.ADM_TYPE='"+admType+"' AND A.ADM_DATE='"+StringTool.getString((Timestamp) this.getValue("ADM_DATE"), "yyyyMMdd")+
		        "'AND A.SESSION_CODE='"+this.getValueString("SESSION_CODE")+"'"+
				" AND A.CLINICROOM_NO='"+this.getValueString("CLINICROOM_NO")+"'";
		TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
		if (temp.getErrCode()<0) {
			this.messageBox("E0005");
			return;
		}
		if (temp.getCount()<=0) {
			this.messageBox("ҽ���Ű಻����,��鿴�Ű��");
			return;
		}
		queNo=temp.getInt("QUE_NO",0);
		if (null!=temp.getValue("VIP_FLG",0) && temp.getValue("VIP_FLG",0).equals("Y")) {
			if (!onBespeak()) {
				return;
			}
		}
		
		CASE_NO = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
		"CASE_NO");
		// ����Һ���Ϣ
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onNewReg", this.getSaveData(temp.getValue("CLINICTYPE_CODE",0),temp.getValue("CLINICAREA_CODE",0)));
		if (result.getErrCode() != 0) {
		//	EKTIO.getInstance().unConsume(tredeNo, this);
			this.messageBox("E0005");
			return;
		}
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", CASE_NO);
//		//TParm re = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
//		this.setReturnValue(re);
		this.messageBox("P0005");
		this.closeWindow();
	}

	/**
	 * ���
	 */
	public void onClear() {
		this
				.clearValue("MR_NO;CLINICROOM_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SERVICE_LEVEL;REG_FEE;CLINIC_FEE;AR_AMT");
		pat = null;
		CASE_NO = "";
		regAdmTime = "";
		queNo=-1;
		initCombo();
	}

	/**
	 * ��˱��������Ƿ��������
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		if (pat == null) {
			return false;
		}
		if (this.getValueString("ADM_DATE").length() == 0) {
			this.messageBox_("��ѡ������");
			this.grabFocus("DATE");
			return false;
		}
		if (this.getValueString("SESSION_CODE").length() == 0) {
			this.messageBox_("��ѡ��ʱ��");
			this.grabFocus("SESSION_CODE");
			return false;
		}
		if (this.getValueString("DEPT_CODE").length() == 0) {
			this.messageBox_("��ѡ��Ʊ�");
			this.grabFocus("DEPT_CODE");
			return false;
		}
		if (this.getValueString("DR_CODE").length() == 0) {
			this.messageBox_("��ѡ��ҽ��");
			this.grabFocus("DR_CODE");
			return false;
		}
		if (this.getValueString("CLINICROOM_NO").length() == 0) {
			this.messageBox_("��ѡ������");
			this.grabFocus("CLINICROOM_NO");
			return false;
		}
		if (this.getValueString("CTZ1_CODE").length() == 0) {
			this.messageBox_("��������ݲ���Ϊ��");
			this.grabFocus("CTZ1_CODE");
			return false;
		}
		if (this.getValueString("SERVICE_LEVEL").length() == 0) {
			this.messageBox_("����ȼ�����Ϊ��");
			this.grabFocus("SERVICE_LEVEL");
			return false;
		}
		if (!this.emptyTextCheck("REGMETHOD_CODE")) {
			return false;
		}
		// �������
		if (admType.equals("E")
				&& this.getValueString("ERD_LEVEL").length() == 0) {
			this.messageBox_("���˵ȼ�����Ϊ��");
			this.grabFocus("ERD_LEVEL");
			return false;
		}
		return true;
	}

	/**
	 * ��ȡҪ���������
	 * 
	 * @return TParm
	 */
	private TParm getSaveData(String clinictypeCode,String clinicareaCode) {
		TParm parm = new TParm();
		// ����CASE_NO
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("ADM_TYPE", admType);
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_DATE", StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd"));
		parm.setData("REG_DATE", SystemTool.getInstance().getDate());
		parm.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		parm.setData("CLINICTYPE_CODE",clinictypeCode);// �ű�
		parm.setData("CLINICROOM_NO", this.getValue("CLINICROOM_NO"));// ����
		parm.setData("CLINICAREA_CODE", clinicareaCode);// ����
		parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("DR_CODE", this.getValue("DR_CODE"));
		parm.setData("REALDEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("REALDR_CODE", this.getValue("DR_CODE"));
		parm.setData("APPT_CODE", "Y");// ԤԼ
		parm.setData("QUE_NO", queNo);// 
		parm.setData("REG_ADM_TIME", regAdmTime);// ԤԼʱ��
		parm.setData("ERD_LEVEL", this.getValue("ERD_LEVEL"));//����
		
		parm.setData("VISIT_CODE", "1");// ����
		parm.setData("REGMETHOD_CODE", this.getValue("REGMETHOD_CODE"));// �Һŷ�ʽ
		parm.setData("CTZ1_CODE", this.getValue("CTZ1_CODE"));
		parm.setData("CTZ2_CODE", this.getValue("CTZ2_CODE"));
		parm.setData("CTZ3_CODE", this.getValue("CTZ3_CODE"));
		parm.setData("ARRIVE_FLG", "N");// ����ע��
		parm.setData("ADM_REGION", Operator.getRegion());
		parm.setData("ADM_STATUS", "1");// ������� 1���ѹҺ�
		parm.setData("REPORT_STATUS", "1");// ����״̬ 1ȫ��δ���
		parm.setData("SEE_DR_FLG", "N");// ����ע��
		parm.setData("HEAT_FLG", "N");// ����ע��
		parm.setData("VIP_FLG", VIP);// VIPע��
		parm.setData("SERVICE_LEVEL", this.getValue("SERVICE_LEVEL"));// ����ȼ�
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("WEIGHT", 0);
		parm.setData("HEIGHT", 0);
		parm.setData("TRANHOSP_CODE", "");
		parm.setData("TRIAGE_NO", "");
		parm.setData("REQUIREMENT", "");//add by huangtt 20140411
		String []name={"CONTRACT_CODE","REGCAN_USER","REGCAN_DATE","PREVENT_SCH_CODE",
				"DRG_CODE","NHI_NO","INS_PAT_TYPE","CONFIRM_NO"};
		parm.setData("ADM_REGION", Operator.getRegion());
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i],"");
		}
		parm.setData("CLINICROOM_NO", this.getValue("CLINICROOM_NO"));// ���
		TParm saveParm = new TParm();
		saveParm.setData("REG", parm.getData());
		saveParm.setData("EXE_FLG","Y");
		return saveParm;
	}

	/**
	 * У���Ƿ�ԤԼ�Һ�
	 * 
	 * @return
	 */
	private boolean onBespeak() {
		TParm temp = new TParm();
		temp.setData("ADM_TYPE", admType); // �Һ�����
		temp.setData("SESSION_CODE", this.getValue("SESSION_CODE")); // ʱ��
		temp.setData("ADM_DATE", StringTool.getString(
				(Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
		temp.setData("START_TIME", StringTool.getString(SystemTool
				.getInstance().getDate(), "HHmm"));
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// ��ǰʱ��
		String startTime = "";
		if (temp.getValue("ADM_DATE").compareTo(admNowDate) < 0) {
			this.messageBox("�Ѿ����ﲻ���ԹҺ�");
			callFunction("UI|table2|clearSelection");
			return false;
		} else if (temp.getValue("ADM_DATE").compareTo(admNowDate) == 0) {
			startTime += " AND START_TIME>='" + temp.getValue("START_TIME")
					+ "'";
		}
		// ԤԼ�ж��Ƿ��Ѿ�����
		String vipSql = "SELECT CLINICROOM_NO,QUE_NO,QUE_STATUS,START_TIME,SESSION_CODE,ADM_DATE "
				+ " FROM REG_CLINICQUE WHERE  QUE_NO IN(SELECT MIN(QUE_NO) AS QUE_NO FROM REG_CLINICQUE "
				+ " WHERE  CLINICROOM_NO='"
				+ this.getValue("CLINICROOM_NO")
				+ "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE")
				+ "'"
				+ " AND SESSION_CODE='"
				+ this.getValue("SESSION_CODE")
				+ "' AND ADM_TYPE='"
				+ admType
				+ "' AND QUE_STATUS='N'"
				+ startTime
				+ ") AND CLINICROOM_NO='"
				+ this.getValue("CLINICROOM_NO")
				+ "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE")
				+ "'"
				+ " AND SESSION_CODE='"
				+ this.getValue("SESSION_CODE")
				+ "' AND ADM_TYPE='"
				+ admType
				+ "'";
		temp = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (temp.getErrCode() < 0) {
			return false;
		}
		if (null == temp.getValue("QUE_NO", 0)
				|| temp.getValue("QUE_NO", 0).length() <= 0) {
			this.messageBox("�ѹ���,û��ԤԼ����");
			return false;
		}
		queNo= temp.getInt("QUE_NO", 0);
		regAdmTime= temp.getValue("START_TIME", 0);
		VIP= true;
		return true;
	}
	/**
	 * ��ѯҽʦ�Ű�(һ��)
	 */
	public void onQueryDrTable() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// ɸѡ����ר�����ͨ��
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		// ���ǹ���Ȩ��
		if (this.getPopedem("deptFilter"))
			parm.setData("DEPT_CODE_SORT", "1101020101");
		TParm data = SchDayTool.getInstance().selectDrTable(parm);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", data);
	}

//	/**
//	 * ��ѯҽʦ�Ű�(VIP)
//	 */
//	public void onQueryVipDrTable() {
//		TTable table2 = new TTable();
//		table2.removeAll();
//		TParm parm = getParmForTag(
//				"REGION_CODE;ADM_TYPE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE",
//				true);
//		parm.setData("ADM_TYPE", admType);
//		parm.setData("VIP_ADM_DATE", StringTool.getString(
//				(Timestamp) getValue("VIP_ADM_DATE"), "yyyyMMdd"));
//		TParm data2 = REGClinicQueTool.getInstance().selVIPDate(parm);
//		if (data2.getErrCode() < 0) {
//			messageBox(data2.getErrText());
//			return;
//		}
//		this.callFunction("UI|Table2|setParmValue", data2);
//	}
	/**
	 * ��ʼ�����
	 */
	public void initSchDay() {
		new Thread() {
			// �߳�,Ϊ��ʡʱ����ߴ򿪹Һ�������Ч��
			public void run() {
				// ��ʼ��Ĭ��֧����ʽ
				TParm selPayWay = REGSysParmTool.getInstance().selPayWay();
				setValue("PAY_WAY", selPayWay.getValue("DEFAULT_PAY_WAY", 0));
				// ��ʼ������ҽʦ�Ű�
				onQueryDrTable();

				// ��ʼ������VIP���
				//onQueryVipDrTable();
			}
		}.start();
	}
	/**
	 * ���Ӷ�Table1�ļ���
	 */
	public void onTableClicked() {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		int row = (Integer) callFunction("UI|TABLE|getClickedRow");
		if (row < 0)
			return;
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		// TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("TABLE");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// ��ùҺŷ�ʽִ���ж��Ƿ��Ʊ����
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // ����Ƿ���Դ�Ʊע��
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("�Һ�ʧ��");
			return;
		}
	}

}
