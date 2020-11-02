package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import jdo.clp.CLPSingleDiseTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: �����ֲ���ͳ��</p>
 *
 * <p>Description: �����ֲ���ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPSingleDiseControl extends TControl {

	private final String Header_E = "�����,120;������,120;����,90;�Ա�,60,SEX;���,120,CTZ1_CODE;�Һ�ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;������,120,PAT_DISE_CODE;����,100,DEPT_CODE;ҽ��,90,USER_ID";// ���ñ��ı��⣨���
	private final String ParmMap_E = "CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;REG_DATE;DISE_CODE;DEPT_CODE;DR_CODE";// ���ñ��Ķ�ӦMap�����
	private final String Header_I = "�����,120;������,120;����,90;�Ա�,60,SEX;���,120,CTZ1_CODE;��Ժʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��Ժʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;������,120,PAT_DISE_CODE;����,100,DEPT_CODE;����ҽ��,90,USER_ID";// ���ñ��ı��⣨סԺ��
	private final String ParmMap_I = "CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;IN_DATE;OUT_DATE;DISE_CODE;DEPT_CODE;DR_CODE";// ���ñ��Ķ�ӦMap��סԺ��

	private TParm patParm;
	private TTabbedPane tTabbedPane_0;// ѡ����
	private TTable patTable;// ������ϢTable
	private int lastSelectedIndex = 0;// �ϴε�ѡ��ҳǩ����
	private TMenuItem edit;// �����������á���ť
	private TMenuItem export;// ����������������ť
	private String diseCode = "";// ����������
	private String admType = "";// �ż���
	private String caseNo = "";// �����
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		if (!obj.equals("")) {
			TParm parm = (TParm) obj;
			admType = parm.getValue("ADM_TYPE");
			caseNo = parm.getValue("CASE_NO");
		}
		getAllComponent();// ��ȡȫ����������
		initControler();// ע������¼�
		onClear();// ���
		initPage();// ��ʼ������
	}

	/**
	 * ��ȡȫ����������
	 */
	public void getAllComponent() {
		edit = (TMenuItem) this.getComponent("editEMR");// ��ȡ������ѡ��
		export = (TMenuItem) this.getComponent("export");
		tTabbedPane_0 = (TTabbedPane) this.getComponent("tTabbedPane_0");// ѡ����
		patTable = (TTable) this.getComponent("PAT_TABLE");//
	}

	/**
	 * ע������¼�
	 */
	public void initControler() {
		callFunction("UI|PAT_TABLE|addEventListener", "PAT_TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		edit.setEnabled(false);
		this.callFunction("UI|export|setEnabled", false);
		this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		this.setValue("PAT_START_DATE", transDate);
		this.setValue("PAT_END_DATE", transDate);
		this.setValue("SD_START_DATE", transDate);
		this.setValue("SD_END_DATE", transDate);
		this.setValue("PAT_ADM_TYPE", admType.equals("") ? "E" : admType);// Ĭ��ѡ�񡰼��
		changeTableTandM();// ���ı����������ӳ��
		if (admType.equals("E") || admType.equals("I")) {
			tTabbedPane_0.setEnabledAt(1, false);
			if (!caseNo.equals("")) {
				initPat();
			}
		} else {
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|delete|setEnabled", false);
		}
	}

	/**
	 * ҳǩ�л��¼�
	 */
	public void onTabChange() {
		int selectedIndex = tTabbedPane_0.getSelectedIndex();// ��ȡҳǩ����
		if (selectedIndex <= 0) {// ��ѯ����ҳǩ
			TParm patData = (TParm) callFunction("UI|PAT_TABLE|getShowParmValue");
			export.setEnabled(false);
			if (patData.getCount() > 0) {
				edit.setEnabled(true);
			}
			lastSelectedIndex = selectedIndex;
		} else if (selectedIndex == 1) {// ��ѯ������ҳǩ
			TParm sdData = (TParm) callFunction("UI|SD_TABLE|getShowParmValue");
			edit.setEnabled(false);
			if (sdData.getCount() > 0) {
				export.setEnabled(true);
			}
			lastSelectedIndex = selectedIndex; // ��¼�˴ε�ѡ��ҳǩ����
		}
	}

	/**
	 * Table�е��¼�
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		}
		TParm data = (TParm) callFunction("UI|PAT_TABLE|getParmValue");
		patParm = new TParm();
		patParm = data.getRow(row);
		this.callFunction("UI|PAT_DISE_CODE|setValue", patParm.getData("DISE_CODE"));
		caseNo = patParm.getData("CASE_NO") + "";
		if ((patParm.getData("DISE_CODE") == null) || patParm.getData("DISE_CODE").equals("")) {
			diseCode = "";
			this.setValue("DEPT_CODE", patParm.getValue("DEPT_CODE"));
			this.setValue("MR_NO", patParm.getValue("MR_NO"));
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", true);
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|delete|setEnabled", false);
			this.callFunction("UI|editEMR|setEnabled", false);
		} else {
			diseCode = patParm.getData("DISE_CODE") + "";
			if (!this.getParameter().equals("")){
				TParm returnParm = new TParm();
				returnParm.setData("DISE_CODE", diseCode);
				this.setReturnValue(returnParm);// ����
			}
			this.setValue("DEPT_CODE", patParm.getValue("DEPT_CODE"));
			this.setValue("MR_NO", patParm.getValue("MR_NO"));
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|delete|setEnabled", true);
			this.callFunction("UI|editEMR|setEnabled", true);
		}
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	private void initPat() {
		if (caseNo.equals("")) {
			return;
		}
		TParm parm = new TParm();// ���
		parm.setData("START_DATE", new Timestamp(0));
		parm.setData("END_DATE", new Timestamp(new Date().getTime()));
		parm.setData("CASE_NO", caseNo);
		TParm patInfo = new TParm();
		if (admType.equals("E")) {
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromE(parm);
		} else if (admType.equals("I")) {
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromI(parm);
		}
		changeTableTandM();
		if (patInfo.getErrCode() < 0) {
			messageBox(patInfo.getErrText());
			return;
		}
		this.callFunction("UI|PAT_TABLE|setParmValue", new TParm());
		if (patInfo.getCount() <= 0) {
			messageBox("E0008");// ��������
			return;
		}
		this.callFunction("UI|query|setEnabled", false);
		edit.setEnabled(false);
		this.callFunction("UI|clear|setEnabled", false);
		this.callFunction("UI|PAT_START_DATE|setEnabled", false);
		this.callFunction("UI|PAT_END_DATE|setEnabled", false);
		this.setValue("PAT_ADM_TYPE", admType);
		this.callFunction("UI|PAT_ADM_TYPE|setEnabled", false);
		this.setValue("DEPT_CODE", patInfo.getValue("DEPT_CODE", 0));
		this.callFunction("UI|DEPT_CODE|setEnabled", false);
		this.setValue("MR_NO", patInfo.getValue("MR_NO", 0));
		this.callFunction("UI|MR_NO|setEnabled", false);
		this.callFunction("UI|PAT_COUNT|setValue", patInfo.getCount() + "");
		this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
		if ((patInfo.getData("DISE_CODE", 0) == null) || patInfo.getData("DISE_CODE", 0).equals("")) {
			diseCode="";
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", true);
			this.callFunction("UI|PAT_DISE_CODE|setValue", "");
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|delete|setEnabled", false);
			this.callFunction("UI|editEMR|setEnabled", false);
		} else {
			diseCode=patInfo.getData("DISE_CODE", 0)+"";
			if (this.getParameter() != null){
				TParm returnParm = new TParm();
				returnParm.setData("DISE_CODE", diseCode);
				this.setReturnValue(returnParm);// ����
			}
			this.callFunction("UI|PAT_DISE_CODE|setValue", patInfo.getData("DISE_CODE", 0));
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|delete|setEnabled", true);
			this.callFunction("UI|editEMR|setEnabled", true);
		}
		patParm = patInfo.getRow(0);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (lastSelectedIndex <= 0) {
			admType = this.getValue("PAT_ADM_TYPE") + "";// �ż���
			if (admType.equals("")) {
				messageBox("��ѡ���ż���");
				return;
			}
			queryPat();// ��ѯ����
		} else {
			querySingleDise();// ��ѯ������
		}
	}

	/**
	 * ��ѯ������Ϣ
	 */
	private void queryPat() {
		Timestamp startDate = (Timestamp) this.getValue("PAT_START_DATE");
		Timestamp endDate = (Timestamp) this.getValue("PAT_END_DATE");
		endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		if (startDate == null) {
			messageBox("��ʼʱ�����ò���ȷ");
			this.setValue("PAT_START_DATE", transDate);
			return;
		}
		if (endDate == null) {
			messageBox("����ʱ�����ò���ȷ");
			this.setValue("PAT_END_DATE", transDate);
			return;
		}
		String deptCode = (this.getValue("DEPT_CODE") + "").trim();
		String mrNo = (this.getValue("MR_NO") + "").trim();
		TParm parm = new TParm();// ���
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		if (!StringUtil.isNullString(deptCode)) {
			parm.setData("DEPT_CODE", deptCode);
		}
		if (!StringUtil.isNullString(mrNo)) {
			parm.setData("MR_NO", mrNo);
		}
		TParm patInfo = new TParm();
		if (admType.equals("E")) {// ����
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromE(parm);
		} else if (admType.equals("I")) {// סԺ
			patInfo = CLPSingleDiseTool.getInstance().queryPatInfoFromI(parm);
		}
		changeTableTandM();// �ı����������ӳ��
		if (patInfo.getErrCode() < 0) {
			messageBox(patInfo.getErrText());
			return;
		}
		this.callFunction("UI|PAT_TABLE|setParmValue", new TParm());
		if (patInfo.getCount() <= 0) {
			messageBox("E0008");// ��������
			return;
		}
		this.callFunction("UI|PAT_COUNT|setValue", patInfo.getCount() + "");
		this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
	}

	/**
	 * ��ѯ��������Ϣ
	 */
	private void querySingleDise() {
		String admType = this.getValueString("SD_ADM_TYPE").trim();//�ż���
		String diseCode = this.getValueString("SD_DISE_CODE").trim();
		Timestamp startDate = (Timestamp) this.getValue("SD_START_DATE");
		Timestamp endDate = (Timestamp) this.getValue("SD_END_DATE");
		endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);// ����һ���23:59:59.999
		Timestamp date = SystemTool.getInstance().getDate();
		String transDate = StringTool.getString(date, "yyyy/MM/dd");
		if (startDate == null) {
			messageBox("��ʼʱ�����ò���ȷ");
			this.setValue("SD_START_DATE", transDate);
			return;
		}
		if (endDate == null) {
			messageBox("����ʱ�����ò���ȷ");
			this.setValue("SD_END_DATE", transDate);
			return;
		}
		TParm parm = new TParm();// ���
		if(!admType.equals("")){
			parm.setData("ADM_TYPE",admType);
		}
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		if(!diseCode.equals("")){
			parm.setData("DISE_CODE",diseCode);
		}
		TParm singleDiseInfo = CLPSingleDiseTool.getInstance().querySDData(parm);

		if (singleDiseInfo.getErrCode() < 0) {
			messageBox(singleDiseInfo.getErrText());
			return;
		}
		this.callFunction("UI|SD_TABLE|setParmValue", new TParm());
		if (singleDiseInfo.getCount() <= 0) {
			messageBox("E0008");// ��������
			return;
		}
		this.callFunction("UI|SD_COUNT|setValue", singleDiseInfo.getCount()+ "");
		this.callFunction("UI|SD_TABLE|setParmValue", singleDiseInfo);
		export.setEnabled(true);
	}

	/**
	 * ���浥��������
	 */
	public void onSave() {
		diseCode = this.getValue("PAT_DISE_CODE") + "";// ����������
		if (diseCode.trim().equals("")) {
			messageBox("��ѡ�񵥲���");
			return;
		} else {
			TParm parm = new TParm();// ���
			parm.setData("DISE_CODE", diseCode);
			parm.setData("CASE_NO", caseNo);
			TParm result = new TParm();
			if (admType.equals("E")) {// ����
				result = CLPSingleDiseTool.getInstance().updateREGAdmSDInfo(parm);
			} else if (admType.equals("I")) {// סԺ
				result = CLPSingleDiseTool.getInstance().updateADMInpSDInfo(parm);
			}
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			} else {
				messageBox("P0001");// ����ɹ�
				this.callFunction("UI|save|setEnabled", false);
				this.callFunction("UI|delete|setEnabled", true);
				this.callFunction("UI|editEMR|setEnabled", true);
				this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
				TParm patInfoParm = (TParm) callFunction("UI|PAT_TABLE|getParmValue");
				if (patInfoParm.getCount("CASE_NO") == 1) {
					patInfoParm.setData("DISE_CODE", 0, diseCode);
					this.callFunction("UI|PAT_TABLE|setParmValue", patInfoParm);
				} else {
					int rowNum = patTable.getSelectedRow();
					patInfoParm.setData("DISE_CODE", rowNum, diseCode);
					this.callFunction("UI|PAT_TABLE|setParmValue", patInfoParm);
					patTable.setSelectedRow(rowNum);
				}
			}
		}
	}

	/**
	 * ɾ������������
	 */
	public void onDelete() {
		// ɾ���󣬵������������ѡ
		TParm parm = new TParm();// ���
		parm.setData("CASE_NO", caseNo);
		TParm result = new TParm();
		if (admType.equals("E")) {// ����
			result = CLPSingleDiseTool.getInstance().deleteREGAdmSDInfo(parm);
		} else if (admType.equals("I")) {// סԺ
			result = CLPSingleDiseTool.getInstance().deleteADMInpSDInfo(parm);
		}
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		} else {
			messageBox("P0003");// ɾ���ɹ�
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|delete|setEnabled", false);
			this.callFunction("UI|editEMR|setEnabled", false);
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", true);
			this.callFunction("UI|PAT_DISE_CODE|setValue", "");
			TParm patInfo = (TParm) callFunction("UI|PAT_TABLE|getParmValue");
			if (patInfo.getCount("CASE_NO") == 1) {
				patInfo.setData("DISE_CODE", 0, "");
				this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
			} else {
				int rowNum = patTable.getSelectedRow();
				patInfo.setData("DISE_CODE", rowNum, "");
				this.callFunction("UI|PAT_TABLE|setParmValue", patInfo);
				patTable.setSelectedRow(rowNum);
			}
		}
	}

	/**
	 * ����EMRģ��༭��
	 */
	public void onEditEmr() {
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "SD");
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("PAT_NAME", patParm.getValue("PAT_NAME"));
		parm.setData("MR_NO", patParm.getValue("MR_NO"));
		parm.setData("IPD_NO", patParm.getValue("IPD_NO"));
		TParm param = getSDBasicData();//��������ģ���һЩ�����û���Ϣ
		parm.setData("diseData", param);
		parm.setData("STYLETYPE","2");//���˾�û����������
		parm.setData("RULETYPE", "2");//Ȩ�޿��ƣ�Ȩ�ޣ�1,ֻ�� 2,��д 3,���ֶ�д��
		parm.setData("TYPE", "F");//��ΪF���������ʾ�ò���ÿ�ξ���ĵ����ֲ�����ΪF����ֻ��ʾ���ξ���ʱ�ĵ����ֲ���
	    parm.setData("EMR_DATA_LIST",new TParm());
	    parm.addListener("EMR_LISTENER",this,"emrListener");
	    parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
	    Object obj = this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		if (obj != null) {
			TParm acceptParm = (TParm) obj;
			for (int i = 0; i < acceptParm.getCount("FILE_PATH"); i++) {
				TParm emrData = getSDBasicData();//��������ģ���һЩ�����û���Ϣ
				if (acceptParm.getValue("FILE_NAME",i).indexOf("AMI") != -1) {
					emrData.setData("DISE_CODE", "AMI");// ���ַ���
				} else if (acceptParm.getValue("FILE_NAME",i).indexOf("����˥��") != -1) {
					emrData.setData("DISE_CODE", "HF");// ���ַ���
				} 
//				else if (acceptParm.getValue("FILE_NAME").indexOf("��״������·��ֲ��") != -1) {
//				    emrData.setData("DISE_CODE", "CABG");// ���ַ���
//				}
				emrData.setData("ADM_TYPE", admType );//�ż���
				emrData.setData("FILE_PATH", acceptParm.getValue("FILE_PATH",i));//�����ļ�·��
				emrData.setData("FILE_NAME", acceptParm.getValue("FILE_NAME",i));//�����ļ�����
			 	TParm action = TIOM_AppServer.executeAction("action.clp.CLPSingleDiseAction", "insertSDData", emrData);

				if (action.getErrCode() < 0) {
					this.messageBox("E0001");//����ʧ��
					return;
				}
			}
		}
	}
	
	/**
	 * ��������ģ���һЩ�����û���Ϣ
	 * @return
	 */
	public TParm getSDBasicData() {
		TParm data = new TParm();
		data.setData("MR_NO", patParm.getValue("MR_NO"));//������
		data.setData("CASE_NO", patParm.getValue("CASE_NO"));//�����
		if (admType.equals("E")) {
			data.setData("IPD_NO", "");//סԺ��
		} else if (admType.equals("I")) {
			data.setData("IPD_NO", patParm.getValue("IPD_NO"));//סԺ��
		}
		data.setData("PAT_NAME",patParm.getValue("PAT_NAME"));//����
		data.setData("SEX_CODE",patParm.getValue("SEX_CODE"));//�Ա�
		data.setData("BIRTH_DATE", patParm.getData("BIRTH_DATE"));
		data.setData("AGE", StringTool.CountAgeByTimestamp((Timestamp) patParm.getData("BIRTH_DATE"), SystemTool.getInstance().getDate())[0]);// ����
		if (admType.equals("E")) {
//			data.setData("IN_DATE", new TNull(Timestamp.class));
//			data.setData("OUT_DATE", new TNull(Timestamp.class));
			data.setData("STAY_DAYS", 0);
		} else if (admType.equals("I")) {
			data.setData("IN_DATE", StringTool.getTimestamp(StringTool.getString((Timestamp) patParm.getData("IN_DATE"),
							"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
			int stayDays = 0;// סԺ����
			if (patParm.getData("OUT_DATE") != null) {
				data.setData("OUT_DATE", StringTool.getTimestamp(StringTool.getString((Timestamp) patParm.getData("OUT_DATE"),
								"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
				stayDays = StringTool.getDateDiffer(data.getTimestamp("IN_DATE"), data.getTimestamp("OUT_DATE"));
			} else {
				// data.setData("OUT_DATE", new TNull(Timestamp.class));
			}
			data.setData("STAY_DAYS", stayDays);
		}
		data.setData("ICD_CODE", patParm.getValue("ICD_CODE"));
		data.setData("ICD_CHN_DESC", patParm.getValue("ICD_CHN_DESC"));
		if (admType.equals("I")) {
			TParm ASPC = CLPSingleDiseTool.getInstance().queryASandPCFromI(data);// ��ѯ������Դ�Ͳ���״̬
			if (!ASPC.getValue("ADM_SOURCE", 0).equals("")) {// ������Դ
				data.setData("ADM_SOURCE", ASPC.getValue("ADM_SOURCE", 0));
			}
			if (!ASPC.getValue("PATIENT_CONDITION", 0).equals("")) {// ����״̬
				data.setData("PATIENT_CONDITION", ASPC.getValue("PATIENT_CONDITION", 0));
			}
		}
		data.setData("TBYS", Operator.getID());// ���ҽʦID
		data.setData("TBYS_CHN", Operator.getName());// ���ҽʦNAME
		data.setData("OPT_USER", Operator.getID());
		data.setData("OPT_DATE", SystemTool.getInstance().getDate());
		data.setData("OPT_TERM", Operator.getIP());
		return data;
	}		
	
	/**
	 * ���е�����ͳ��
	 */
	public void onExport() {
		this.messageBox("��δʵ�֣�");
	}

	/**
	 * ���ı����������ӳ��
	 */
	public void changeTableTandM() {
		if (admType.equals("E")) {// ����
			patTable.setHeader(Header_E);// ���ñ�������
			patTable.setParmMap(ParmMap_E);// ���ñ���Ӧ��Map
			this.callFunction("UI|PAT_DATE_LABEL|setText", "�Һ�ʱ�䣺");
		} else if (admType.equals("I")) {// סԺ
			patTable.setHeader(Header_I);
			patTable.setParmMap(ParmMap_I);
			this.callFunction("UI|PAT_DATE_LABEL|setText", "��Ժʱ�䣺");
		}
	}

	/**
	 * ���ı����������ӳ��
	 */
	public void onPatChangeTableTandM() {
		admType=this.getValue("PAT_ADM_TYPE")+"";
		if (admType.equals("E")) {// ����
			patTable.setHeader(Header_E);// ���ñ�������
			patTable.setParmMap(ParmMap_E);// ���ñ���Ӧ��Map
			this.callFunction("UI|PAT_DATE_LABEL|setText", "�Һ�ʱ��:");
		} else if (admType.equals("I")) {// סԺ
			patTable.setHeader(Header_I);
			patTable.setParmMap(ParmMap_I);
			this.callFunction("UI|PAT_DATE_LABEL|setText", "��Ժʱ��:");
		}
	}
	
	/**
	 * ���ı����������ӳ��
	 */
	public void onSDChangeTableTandM() {
		String admType=this.getValue("SD_ADM_TYPE")+"";
		if (admType.equals("E")) {// ����
			this.callFunction("UI|SD_DATE_LABEL|setText", "�Һ�ʱ��:");
		} else if (admType.equals("I")) {// סԺ
			this.callFunction("UI|SD_DATE_LABEL|setText", "��Ժʱ��:");
		}else{
			this.callFunction("UI|SD_DATE_LABEL|setText", "����ʱ��:");
		}
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		if (lastSelectedIndex <= 0) {// ��ѯ����ҳǩ
			clearValue("DEPT_CODE;MR_NO;PAT_DISE_CODE;PAT_COUNT");
			this.callFunction("UI|PAT_ADM_TYPE|setEnabled", true);
			this.callFunction("UI|DEPT_CODE|setEnabled", true);
			this.callFunction("UI|MR_NO|setEnabled", true);
			this.callFunction("UI|PAT_DISE_CODE|setEnabled", false);
			edit.setEnabled(false);
			((TTable) this.getComponent("PAT_TABLE")).setDSValue();
		} else {// ������ҳǩ
			clearValue("SD_ADM_TYPE;SD_DISE_CODE;SD_COUNT");
			// this.clearValue("SD_TABLE");
			export.setEnabled(false);
			this.callFunction("UI|SD_TABLE|setParmValue", new TParm());
		}
	}
	
	/**
	 * ȥ��
	 * 
	 * @param str  String
	 * @return String
	 */
	public String nullToString(String str) {
		return str == null?"":str;
	}

}
