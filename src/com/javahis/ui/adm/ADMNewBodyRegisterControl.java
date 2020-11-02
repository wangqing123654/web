package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.adm.ADMNewBodyRegisterTool;
import jdo.inw.InwForOutSideTool;
import jdo.odi.OdiOrderTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;

/**
 * <p>
 * Title: ������ע��
 * </p>
 *
 * <p>
 * Description: ������ע��
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
 *
 * @author duzhw
 * @version 4.5
 */
public class ADMNewBodyRegisterControl extends TControl {

	private Pat pat;// ������Ϣ
	String caseNo = "";// �������
	TParm acceptData = new TParm(); // �Ӳ�
	String bedNo = "";// ����
	String oper = "";// NEWΪ������������ע�� UPDATEΪ�������޸�
	private boolean crmFlg = true; // crm�ӿڿ���
	String newMrNo = "";// ������Ժʱ���ɵĲ�����

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));

		// ��ӡ��������Ч
		callFunction("UI|Wrist|setEnabled", false);

		initData();
		// �жϲ���Ϊĸ�׻���������
		String acceptMrNo = "";
		Object obj = this.getParameter();

		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			acceptMrNo = acceptData.getData("ADM", "MR_NO").toString();
			if (containsAny(acceptMrNo, "-")) {// ������
				oper = "UPDATE";
			} else {// ĸ��
				oper = "NEW";
			}
		}

		if ("NEW".equals(oper)) {
			// У��סԺ������
			String sql = "SELECT NEWBORN_MR_FLG FROM ODI_SYSPARM ";
			TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (admParm.getCount() > 0) {
				if (!admParm.getValue("NEWBORN_MR_FLG", 0).equals("Y")) {
					this.messageBox("û������������ע���ֵ䣡");
					callFunction("UI|save|setEnabled", false);// ���水ť
					callFunction("UI|new|setEnabled", false);// ������ť
					return;

				} else {
					// �Ա�У��
					String sexCode = acceptData.getData("ODI", "SEX_CODE").toString();
					if (!"2".equals(sexCode)) {
						// this.messageBox("�Ա���ȷ��������Ϊ������ĸ�ף�");
						callFunction("UI|save|setEnabled", false);// ���水ť
						callFunction("UI|new|setEnabled", false);// ������ť
						return;
					} else {
						String mrNo = acceptData.getData("ADM", "MR_NO").toString();
						String deptCode = acceptData.getData("ODI", "DEPT_CODE").toString();
						String stationCode = acceptData.getData("ODI", "STATION_CODE").toString();
						this.setValue("MR_NO", mrNo);
						this.setValue("DEPT_CODE", deptCode);
						this.setValue("STATION_CODE", stationCode);
						this.onQueryNO();
					}
				}
			}
		} else if ("UPDATE".equals(oper)) {
			// ��λ�û�
			callFunction("UI|BED_NO|setEnabled", false);
			callFunction("UI|BED_CHECK_BUTTON|setEnabled", false);
			// ��ӡ��������Ч
			callFunction("UI|Wrist|setEnabled", true);
			// ĸ����Ϣ��ֵ
			setMotherInfo(acceptData);
			// ��������Ϣ��ѯ��ֵ
			setNewBodyInfo(acceptData);
			/*
			 * add by wangqing 20180921
			 * �����޸�Ȩ�޷��书�ܣ�ʵ�����������������Ž����������޸�Ȩ�޵��û������Խ��г��������޸ģ����޸�Ȩ�޵��û����޷��޸ĳ������ڣ����水ťΪ�Ҳ�����״̬
			 */
			if (!acceptData.getBoolean("NEW_BODY", "SAVE_FLG")) {
				callFunction("UI|save|setEnabled", false);
			}
		}
		if ("Y".equals(acceptData.getValue("MEM_FLG"))) {//
			TMenuItem menuItem = (TMenuItem) getComponent("close");
			menuItem.setActionMessage("onClose");
			TDialog dialog = (TDialog) getComponent("UI");
			dialog.setHeight(480);
			dialog.setWidth(900);
		}
		if ("".equals(this.getValue("BODY_MR_NO")) || this.getValue("BODY_MR_NO") == null) {// ��������ϵ�������������Ϊ�գ����ܵ��������Ժ�İ�ť
			TMenuItem resvItem = (TMenuItem) getComponent("resv");
			resvItem.setEnabled(false);
		}
	}

	// �ж��Ƿ�����������-�������������ж� duzhw add 20140327
	public boolean containsAny(String str, String searchChars) {

		if (str.length() != str.replace(searchChars, "").length()) {
			return true;
		}
		return false;
	}

	/**
	 * ĸ����Ϣ��ֵ
	 */
	public void setMotherInfo(TParm acceptData) {
		String mrNo = acceptData.getData("ADM", "MR_NO").toString();
		// String name = acceptData.getData("ODI","PAT_NAME").toString();

		String motherMrNo = mrNo.substring(0, mrNo.indexOf("-"));
		// String motherName = name.substring(0, name.indexOf("֮Ӥ"));
		String motherName = "";
		String sql = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO = '" + motherMrNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			motherName = parm.getValue("PAT_NAME", 0);
		}
		this.setValue("MR_NO", motherMrNo);
		this.setValue("PAT_NAME", motherName);
		this.setValue("IPD_NO", acceptData.getData("ADM", "IPD_NO").toString());

	}

	/**
	 * ��������Ϣ��ֵ
	 */
	public void setNewBodyInfo(TParm acceptData) {

		String sql = "SELECT MR_NO,IPD_NO,PAT_NAME,SEX_CODE,GESTATIONAL_WEEKS,BIRTH_DATE,NEW_BODY_WEIGHT,MERGE_TOMRNO "
				+ ",NEW_BODY_HEIGHT " + // add by yangjj 20150312 ���ӳ�����
				" FROM SYS_PATINFO WHERE MR_NO = '" + acceptData.getData("ADM", "MR_NO").toString() + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		if (parm.getCount() > 0) {

			String birthDate = "";
			if (parm.getValue("BIRTH_DATE", 0).length() > 12) {
				birthDate = parm.getValue("BIRTH_DATE", 0).replaceAll("-", "/").substring(0,
						parm.getValue("BIRTH_DATE", 0).indexOf("."));
			} else if (parm.getValue("BIRTH_DATE", 0).length() > 0 && parm.getValue("BIRTH_DATE", 0).length() < 12) {
				birthDate = parm.getValue("BIRTH_DATE", 0).replaceAll("-", "/");
			}

			this.setValue("BODY_MR_NO", parm.getValue("MR_NO", 0));
			this.setValue("BODY_NAME", parm.getValue("PAT_NAME", 0));
			this.setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));
			this.setValue("BODY_IPD_NO", parm.getValue("IPD_NO", 0));
			this.setValue("GESTATIONAL_WEEKS", parm.getValue("GESTATIONAL_WEEKS", 0));
			this.setValue("BIRTH_DATE", birthDate);

			// add by yangjj 20150710
			double weight = 0.0;
			weight = Double.parseDouble(parm.getValue("NEW_BODY_WEIGHT", 0).toString());

			if (weight % 1.0 == 0) {
				this.setValue("WEIGHT", ((long) weight) + "");
			} else {
				this.setValue("WEIGHT", weight + "");
			}

			// add by guoy 20150730
			double height = 0.0;
			height = Double.parseDouble(parm.getValue("NEW_BODY_HEIGHT", 0).toString());
			if (height % 1.0 == 0) {
				this.setValue("HEIGHT", ((long) height) + "");
			} else {
				this.setValue("HEIGHT", height + "");
			}

			// add by yangjj 20150312 ���ӳ�����
			// this.setValue("HEIGHT", parm.getValue("NEW_BODY_HEIGHT", 0));
			this.setValue("RELATION_MR_NO", parm.getValue("MERGE_TOMRNO", 0));// ����������add by huangjw 20150615
			// ��������
			setBirth();

			// ������ʾ
			String bedSql = "SELECT B.BED_NO_DESC FROM ADM_INP A,SYS_BED B " + " WHERE A.MR_NO = '"
					+ acceptData.getData("ADM", "MR_NO").toString() + "' AND A.BED_NO = B.BED_NO";
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(bedSql));
			if (bedParm.getCount() > 0) {
				this.setValue("BED_NO", bedParm.getValue("BED_NO_DESC", 0));
			}

		}
	}

	/**
	 * ���ݲ����Ų�ѯ������Ϣ
	 */
	public void onQueryNO() {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		this.setValue("IPD_NO", pat.getIpdNo());
		this.setValue("PAT_NAME", pat.getName());

	}

	/**
	 * ��ʼ������
	 */
	public void initData() {
		Timestamp now = SystemTool.getInstance().getDate();
		this.setValue("BIRTH_DATE",
				now.toString().substring(0, 10).replace('-', '/') + " " + now.toString().substring(11, 13) + ":00:00");// ��ǰʱ��
		this.setValue("NOW_DATE", now.toString().substring(0, 10).replace('-', '/'));// ��ǰʱ��
		setBirth();// ��������
		callFunction("UI|BED_NO|setEnabled", false);// ��λ�û�

	}

	/**
	 * ����
	 */
	public void onNew() {
		String newBodyMrNo = "";// ������������
		// 1:����������
		String mrNo = this.getValueString("MR_NO");
		newBodyMrNo = getAddOneMrNo(mrNo);
		boolean flg = true;
		while (flg) {
			if (queryIfHaveMrNo(newBodyMrNo)) {// ����+1������ѯ
				newBodyMrNo = getAddOneMrNo(newBodyMrNo);
			} else {// ������
				flg = false;
			}
		}
		// ����������������
		this.setValue("BODY_MR_NO", newBodyMrNo);
		// ����������סԺ��
		this.setValue("BODY_IPD_NO", pat.getIpdNo());
		// ��������������
		this.setValue("BODY_NAME", getNewBodyName(this.getValueString("PAT_NAME")));

	}

	/**
	 * ����
	 */
	public void onSave() {
		TParm parm = new TParm();
		TParm admParm = new TParm();
		TParm result = new TParm();
		// �ж������������޸�
		if ("NEW".equals(oper)) {// ����

			String bodyMrNo = this.getValueString("BODY_MR_NO");
			if (queryIfHaveMrNo2(bodyMrNo)) {
				this.messageBox("�Ѿ�������Ժ���������ݣ�");
				return;
			} else {
				// ����У��
				if (bodyMrNo == null || bodyMrNo.length() <= 0) {
					this.messageBox("�����������Ų���Ϊ�գ���������");
					return;
				}
				if (!checkData()) {
					return;
				}
				// ��ȡ����������
				// 1:����SYS_PATINFO�����ݣ��鿴������Ϣ�������߼�--���ò����Ƶ�action���������
				parm = getData();
				// String patinfoSql = insertPatinfoSql(parm);
				// result=new TParm(TJDODBTool.getInstance().update(patinfoSql));
				// if (result.getErrCode() < 0) {
				// this.messageBox("����ʧ�ܣ�" + result.getErrName()+ result.getErrText());
				// return;
				// }
				// 2:����ADM_INP���ݣ����˽����ϵ��������⣬�������ݶ���ĸ�׵�ADM_INP������һ��
				// ��ȡĸ��סԺ��Ϣ
				admParm = getAdmInpInfo(this.getValueString("MR_NO"), this.getValueString("IPD_NO"));
				// System.out.println("ĸ��סԺ��Ϣ��"+admParm);

				TParm newAdmParm = admParm.getRow(0);
				// TParm newAdmParm=admParm;
				// System.out.println("ĸ��סԺ��Ϣ2="+newAdmParm);
				newAdmParm.setData("MR_NO", parm.getValue("BODY_MR_NO"));
				newAdmParm.setData("M_CASE_NO", newAdmParm.getValue("CASE_NO"));
				newAdmParm.setData("CASE_NO", parm.getValue("CASE_NO"));
				newAdmParm.setData("IPD_NO", parm.getValue("BODY_IPD_NO"));
				newAdmParm.setData("PAT_NAME", parm.getValue("BODY_NAME"));
				newAdmParm.setData("BIRTH_DATE", parm.getValue("BIRTH_DATE"));
				newAdmParm.setData("SEX_CODE", parm.getValue("SEX_CODE"));
				newAdmParm.setData("GESTATIONAL_WEEKS", parm.getValue("GESTATIONAL_WEEKS"));
				newAdmParm.setData("BED_NO", bedNo);
				newAdmParm.setData("WEIGHT", parm.getDouble("WEIGHT")/* /1000 */);// =====pangben 2014-5-12 ��λת��
																					// ��תǧ��//=====sunqyά�ֿ˲���
				newAdmParm.setData("HEIGHT", parm.getDouble("HEIGHT"));// add by yangjj 20150312 ���ӳ�����
				newAdmParm.setData("OPT_USER", parm.getValue("OPT_USER"));
				newAdmParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				newAdmParm.setData("ADM_TYPE", "I");
				newAdmParm.setData("ID_TYPE", admParm.getValue("ID_TYPE"));
				newAdmParm.setData("REGION_CODE", newAdmParm.getValue("REGION_CODE"));
				newAdmParm.setData("DATE", this.getValue("NOW_DATE"));
				newAdmParm.setData("NEW_BORN_FLG", "Y");// ������ע��

				// System.out.println("������סԺ��Ϣ2:"+newAdmParm);
				// result = TIOM_AppServer.executeAction("action.adm.ADMNewBodyRegisterAction",
				// "insertADMData", newAdmParm); // סԺ�ǼǱ���
				// result = TIOM_AppServer.executeAction("action.adm.ADMInpAction",
				// "insertADMData", newAdmParm); // סԺ�ǼǱ���

				// ���ת����
				newAdmParm.setData("APPT_FLG", "N");
				newAdmParm.setData("ALLO_FLG", "Y");
				newAdmParm.setData("BED_STATUS", "1");
				// result = TIOM_AppServer.executeAction(
				// "action.adm.ADMWaitTransAction", "onInSave", newAdmParm); // �봲����
				result = TIOM_AppServer.executeAction("action.adm.ADMNewBodyRegisterAction", "onInSave", newAdmParm);

				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ�ܣ�" + result.getErrName() + result.getErrText());
					return;
				} else {
					this.messageBox("ע��ɹ���");
					// ��ӡ��������Ч
					callFunction("UI|Wrist|setEnabled", true);

					// add by huangtt 20140504 start
					if (crmFlg) {
						Pat patM = Pat.onQueryByMrNo(
								newAdmParm.getValue("MR_NO").substring(0, newAdmParm.getValue("MR_NO").indexOf("-")));
						String birthday = newAdmParm.getValue("BIRTH_DATE");
						TParm parmCrm = new TParm();
						parmCrm.setData("MR_NO", newAdmParm.getValue("MR_NO"));
						parmCrm.setData("PAT_NAME", newAdmParm.getValue("PAT_NAME"));
						parmCrm.setData("PY1", "");
						parmCrm.setData("FIRST_NAME", "");
						parmCrm.setData("LAST_NAME", "");
						parmCrm.setData("OLDNAME", "");
						parmCrm.setData("ID_TYPE", "");
						parmCrm.setData("IDNO", "");
						parmCrm.setData("SEX_CODE", newAdmParm.getValue("SEX_CODE"));
						parmCrm.setData("BIRTH_DATE", birthday.substring(0, 4) + "/" + birthday.substring(4, 6) + "/"
								+ birthday.substring(6, 8));
						parmCrm.setData("NATION_CODE", "");
						parmCrm.setData("NATION_CODE2", "");
						parmCrm.setData("MARRIAGE", "");
						parmCrm.setData("RESID_POST_CODE", "");
						parmCrm.setData("RESID_ADDRESS", "");
						parmCrm.setData("POST_CODE", "");
						parmCrm.setData("CURRENT_ADDRESS", "");
						parmCrm.setData("CELL_PHONE", "");
						parmCrm.setData("SPECIAL_DIET", "");
						parmCrm.setData("E_MAIL", "");
						parmCrm.setData("TEL_HOME", "");
						parmCrm.setData("CTZ1_CODE", patM.getCtz1Code());
						parmCrm.setData("CTZ2_CODE", patM.getCtz2Code());
						parmCrm.setData("CTZ3_CODE", patM.getCtz3Code());
						parmCrm.setData("HOMEPLACE_CODE", "");
						parmCrm.setData("RELIGION", "");
						parmCrm.setData("BIRTH_HOSPITAL", "");
						parmCrm.setData("SCHOOL_NAME", "");
						parmCrm.setData("SCHOOL_TEL", "");
						parmCrm.setData("SOURCE", "");
						parmCrm.setData("INSURANCE_COMPANY1_CODE", "");
						parmCrm.setData("INSURANCE_COMPANY2_CODE", "");
						parmCrm.setData("INSURANCE_NUMBER1", "");
						parmCrm.setData("INSURANCE_NUMBER2", "");
						parmCrm.setData("GUARDIAN1_NAME", "");
						parmCrm.setData("GUARDIAN1_RELATION", "");
						parmCrm.setData("GUARDIAN1_TEL", "");
						parmCrm.setData("GUARDIAN1_PHONE", "");
						parmCrm.setData("GUARDIAN1_COM", "");
						parmCrm.setData("GUARDIAN1_ID_TYPE", "");
						parmCrm.setData("GUARDIAN1_ID_CODE", "");
						parmCrm.setData("GUARDIAN1_EMAIL", "");
						parmCrm.setData("GUARDIAN2_NAME", "");
						parmCrm.setData("GUARDIAN2_RELATION", "");
						parmCrm.setData("GUARDIAN2_TEL", "");
						parmCrm.setData("GUARDIAN2_PHONE", "");
						parmCrm.setData("GUARDIAN2_COM", "");
						parmCrm.setData("GUARDIAN2_ID_TYPE", "");
						parmCrm.setData("GUARDIAN2_ID_CODE", "");
						parmCrm.setData("GUARDIAN2_EMAIL", "");
						parmCrm.setData("REG_CTZ1_CODE", "");
						parmCrm.setData("REG_CTZ2_CODE", "");
						parmCrm.setData("FAMILY_DOCTOR", "");
						parmCrm.setData("ACCOUNT_MANAGER_CODE", "");
						parmCrm.setData("MEM_TYPE", "");
						parmCrm.setData("START_DATE", "");
						parmCrm.setData("END_DATE", "");
						parmCrm.setData("BUY_MONTH_AGE", "");
						parmCrm.setData("HAPPEN_MONTH_AGE", "");
						parmCrm.setData("MEM_CODE", "");
						parmCrm.setData("REASON", "");
						parmCrm.setData("START_DATE_TRADE", "");
						parmCrm.setData("END_DATE_TRADE", "");
						parmCrm.setData("MEM_FEE", "");
						parmCrm.setData("INTRODUCER1", "");
						parmCrm.setData("INTRODUCER2", "");
						parmCrm.setData("INTRODUCER3", "");
						parmCrm.setData("DESCRIPTION", "");
						TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction", "createMember1",
								parmCrm);
						if (!parmCRM.getBoolean("flg", 0)) {
							this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
						}
					}
					// add by huangtt 20140504 end
				}

			}
			if (!"".equals(this.getValue("BODY_MR_NO")) || this.getValue("BODY_MR_NO") != null) {// ��������ϵ�������������Ϊ�գ����ܵ��������Ժ�İ�ť
				TMenuItem resvItem = (TMenuItem) getComponent("resv");
				resvItem.setEnabled(true);
			}
		} else if ("UPDATE".equals(oper)) {// �޸�
			// ����У��
			if (!checkData()) {
				return;
			}
			// ��ȡҳ������
			parm = getData();
			result = TIOM_AppServer.executeAction("action.adm.ADMNewBodyRegisterAction", "onUpdateSave", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("�޸�ʧ�ܣ�" + result.getErrName() + result.getErrText());
				return;
			} else {
				this.messageBox("�޸ĳɹ���");
				// add by huangtt 20140504 start
				if (crmFlg) {
					Pat patM = Pat.onQueryByMrNo(
							parm.getValue("BODY_MR_NO").substring(0, parm.getValue("BODY_MR_NO").indexOf("-")));
					String birthday = parm.getValue("BIRTH_DATE");
					TParm parmCrm = new TParm();
					parmCrm.setData("MR_NO", parm.getValue("MR_NO"));
					parmCrm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
					parmCrm.setData("PY1", "");
					parmCrm.setData("FIRST_NAME", "");
					parmCrm.setData("LAST_NAME", "");
					parmCrm.setData("OLDNAME", "");
					parmCrm.setData("ID_TYPE", "");
					parmCrm.setData("IDNO", "");
					parmCrm.setData("SEX_CODE", parm.getValue("SEX_CODE"));
					parmCrm.setData("BIRTH_DATE",
							birthday.substring(0, 4) + "/" + birthday.substring(4, 6) + "/" + birthday.substring(6, 8));
					parmCrm.setData("NATION_CODE", "");
					parmCrm.setData("NATION_CODE2", "");
					parmCrm.setData("MARRIAGE", "");
					parmCrm.setData("RESID_POST_CODE", "");
					parmCrm.setData("RESID_ADDRESS", "");
					parmCrm.setData("POST_CODE", "");
					parmCrm.setData("CURRENT_ADDRESS", "");
					parmCrm.setData("CELL_PHONE", "");
					parmCrm.setData("SPECIAL_DIET", "");
					parmCrm.setData("E_MAIL", "");
					parmCrm.setData("TEL_HOME", "");
					parmCrm.setData("CTZ1_CODE", patM.getCtz1Code());
					parmCrm.setData("CTZ2_CODE", patM.getCtz2Code());
					parmCrm.setData("CTZ3_CODE", patM.getCtz3Code());
					parmCrm.setData("HOMEPLACE_CODE", "");
					parmCrm.setData("RELIGION", "");
					parmCrm.setData("BIRTH_HOSPITAL", "");
					parmCrm.setData("SCHOOL_NAME", "");
					parmCrm.setData("SCHOOL_TEL", "");
					parmCrm.setData("SOURCE", "");
					parmCrm.setData("INSURANCE_COMPANY1_CODE", "");
					parmCrm.setData("INSURANCE_COMPANY2_CODE", "");
					parmCrm.setData("INSURANCE_NUMBER1", "");
					parmCrm.setData("INSURANCE_NUMBER2", "");
					parmCrm.setData("GUARDIAN1_NAME", "");
					parmCrm.setData("GUARDIAN1_RELATION", "");
					parmCrm.setData("GUARDIAN1_TEL", "");
					parmCrm.setData("GUARDIAN1_PHONE", "");
					parmCrm.setData("GUARDIAN1_COM", "");
					parmCrm.setData("GUARDIAN1_ID_TYPE", "");
					parmCrm.setData("GUARDIAN1_ID_CODE", "");
					parmCrm.setData("GUARDIAN1_EMAIL", "");
					parmCrm.setData("GUARDIAN2_NAME", "");
					parmCrm.setData("GUARDIAN2_RELATION", "");
					parmCrm.setData("GUARDIAN2_TEL", "");
					parmCrm.setData("GUARDIAN2_PHONE", "");
					parmCrm.setData("GUARDIAN2_COM", "");
					parmCrm.setData("GUARDIAN2_ID_TYPE", "");
					parmCrm.setData("GUARDIAN2_ID_CODE", "");
					parmCrm.setData("GUARDIAN2_EMAIL", "");
					parmCrm.setData("REG_CTZ1_CODE", "");
					parmCrm.setData("REG_CTZ2_CODE", "");
					parmCrm.setData("FAMILY_DOCTOR", "");
					parmCrm.setData("ACCOUNT_MANAGER_CODE", "");
					parmCrm.setData("MEM_TYPE", "");
					parmCrm.setData("START_DATE", "");
					parmCrm.setData("END_DATE", "");
					parmCrm.setData("BUY_MONTH_AGE", "");
					parmCrm.setData("HAPPEN_MONTH_AGE", "");
					parmCrm.setData("MEM_CODE", "");
					parmCrm.setData("REASON", "");
					parmCrm.setData("START_DATE_TRADE", "");
					parmCrm.setData("END_DATE_TRADE", "");
					parmCrm.setData("MEM_FEE", "");
					parmCrm.setData("INTRODUCER1", "");
					parmCrm.setData("INTRODUCER2", "");
					parmCrm.setData("INTRODUCER3", "");
					parmCrm.setData("DESCRIPTION", "");
					TParm parmCRM = TIOM_AppServer.executeAction("action.reg.REGCRMAction", "updateMemberByMrNo1",
							parmCrm);
					if (!parmCRM.getBoolean("flg", 0)) {
						this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
					}
				}
				// add by huangtt 20140504 end
			}
		}

		// add by yangjj 20150714
		String new_weight = this.getValueString("WEIGHT");
		String new_birth = StringTool.getString((Timestamp) this.getValue("BIRTH_DATE"), "yyyy/MM/dd HH:mm:ss");
		String new_case_no = "";
		if ("NEW".equals(oper)) {
			new_case_no = caseNo;
		} else if ("UPDATE".equals(oper)) {
			new_case_no = ((TParm) this.getParameter()).getData("ADM", "CASE_NO").toString();
		}

		String new_age = DateUtil.showAge((Timestamp) this.getValue("BIRTH_DATE"), SystemTool.getInstance().getDate());
		String new_sex = this.getValueString("SEX_CODE");
		String new_name = this.getValueString("BODY_NAME");
		String sql_update_mro = " UPDATE MRO_RECORD SET BIRTH_DATE = TO_DATE('" + new_birth
				+ "','yyyy/MM/dd HH24:mi:ss'),PAT_NAME='" + new_name + "',SEX='" + new_sex + "',AGE = '" + new_age
				+ "',NB_WEIGHT = '" + new_weight + "' WHERE CASE_NO = '" + new_case_no + "'";
		System.out.println(sql_update_mro);
		TJDODBTool.getInstance().update(sql_update_mro);

	}

	/**
	 * ��ӡ���
	 */
	public void onWrist() {

		if (this.getValueString("BODY_MR_NO").length() == 0) {
			return;
		}
		String sql = "SELECT * FROM ADM_INP WHERE 1=1 AND MR_NO='" + this.getValueString("BODY_MR_NO")
				+ "' ORDER BY OPT_DATE DESC";
		String currentCaseNo = new TParm(TJDODBTool.getInstance().select(sql)).getValue("CASE_NO", 0);
		TParm print = new TParm();
		// =============================================================
		print.setData("MR_NO", this.getValueString("BODY_MR_NO"));
		print.setData("CASE_NO", currentCaseNo);

		this.openDialog("%ROOT%\\config\\adm\\ADMWristPrint.x", print);

		// if (this.getValueString("BODY_MR_NO").length() == 0) {
		// return;
		// }
		// String birthday = this.getValueString("BIRTH_DATE");
		// if(birthday.length()>8){
		// birthday = birthday.substring(0, 16).replaceAll("-", "/");
		// }
		// TParm print = new TParm();
		// print.setData("Barcode", "TEXT", this.getValueString("BODY_MR_NO"));
		// print.setData("PatName", "TEXT", "����:"+this.getValueString("BODY_NAME"));
		// //String sexName=this.getComboBox("SEX_CODE").getSelectedName();
		// //print.setData("Sex", "TEXT", sexName);
		// print.setData("Birth", "TEXT", "��������:"+birthday);
		// Pat pat = Pat.onQueryByMrNo(this.getValueString("BODY_MR_NO"));
		//
		// //add by yangjj 20150710
		// double weight = pat.getNewBodyWeight();
		// if(weight % 1.0 == 0){
		// print.setData("Weight","TEXT","����:"+((long)weight)+"g");
		// }else{
		// print.setData("Weight","TEXT","����:"+weight+"g");
		// }
		//
		//
		// print.setData("BedNO","TEXT","����:");
		// this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWrist.jhw",print);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("BODY_MR_NO;BODY_NAME;SEX_CODE;BODY_IPD_NO;" + "BIRTH_DATE;AGE;" + "WEIGHT;" + "HEIGHT;" + // add
																													// by
																													// yangjj���ӳ�����
				"BED_NO;GESTATIONAL_WEEKS;DEPT_CODE;STATION_CODE");
		// ��ӡ��������Ч
		callFunction("UI|Wrist|setEnabled", false);
	}

	/**
	 * ��λ����
	 */
	public void onBedNo() {
		TParm sendParm = new TParm();
		sendParm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		sendParm.setData("STATION_CODE", getValue("STATION_CODE"));
		sendParm.setData("TYPE", "REWBODYREGISTER");
		// ��ȡĸ�����ڷ����
		String sql = "SELECT ROOM_CODE FROM SYS_BED WHERE MR_NO = '" + this.getValueString("MR_NO") + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			sendParm.setData("ROOM_CODE", parm.getValue("ROOM_CODE", 0));
		}
		TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\adm\\ADMQueryBed.x", sendParm);
		if (reParm == null) {
			return;
		}
		this.setValue("BED_NO", reParm.getValue("BED_NO_DESC", 0)); // ��ʾ��������
		bedNo = reParm.getValue("BED_NO", 0);
		// �����û�
		callFunction("UI|BED_NO|setEnabled", false);
		// BED_NO = reParm.getValue("BED_NO", 0); //��¼����
	}

	/**
	 * ����ĸ������ȡ����������
	 */
	public String getNewBodyName(String patName) {
		return patName + "֮Ӥ";
	}

	/**
	 * ��ѯSYS_PATINFO���Ƿ��д˲����ŷ��� true:���� false:������
	 */
	public boolean queryIfHaveMrNo(String bodyMrNo) {
		boolean flg = false;
		String sql = "SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + bodyMrNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			flg = true;
		}
		return flg;
	}

	/**
	 * ���������һλ��1���� �ش��жϲ�����"-"���"-1",�����������һλ��1
	 */
	public String getAddOneMrNo(String mrNo) {
		String returnMrNo = "";
		String sub = "-";
		try {
			int a = mrNo.indexOf(sub);
			if (a >= 0) {// ���һλ��1
				// int length = mrNo.length();
				String lastNum = mrNo.substring(mrNo.length() - 1, mrNo.length());
				int newLastNum = Integer.parseInt(lastNum) + 1;
				returnMrNo = mrNo.substring(0, mrNo.length() - 1) + newLastNum;
			} else {// ��"-1"
				returnMrNo = mrNo + "-1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("�����ɲ����ţ�"+returnMrNo);
		return returnMrNo;
	}

	/**
	 * ��������
	 */
	public void setBirth() {
		if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
			return;

		try {
			// Timestamp now = StringTool.getTimestamp(new Date());
			// String nowDate = now.toString().replaceAll("-", "").substring(0, 17);
			// Timestamp mytime = StringTool.getTimestamp(nowDate, "yyyyMMdd HH:mm:ss");
			// String birthDate = this.getValueString("BIRTH_DATE");
			// String bir2 = birthDate.replaceAll("-", "").substring(0, 17);
			// Timestamp birthDate2 = StringTool.getTimestamp(bir2, "yyyyMMdd HH:mm:ss");
			Timestamp sysDate = TJDODBTool.getInstance().getDBTime();
			String AGE = com.javahis.util.DateUtil.showAge((Timestamp) getValue("BIRTH_DATE"), sysDate);
			setValue("AGE", AGE);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��ȡҳ������
	 */
	public TParm getData() {
		TParm parm = new TParm();
		parm = getParmForTag("BODY_MR_NO;BODY_NAME;SEX_CODE;BODY_IPD_NO;GESTATIONAL_WEEKS;" + "WEIGHT;" + "HEIGHT;" + // add
																														// by
																														// yangjj
																														// 20150312
																														// ���ӳ�����
				"BED_NO");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String birthday = this.getValueString("BIRTH_DATE");

		// if(birthday.length()>0){
		// birthday = birthday.substring(0, 10).replaceAll("-", "/");
		// }
		if (birthday != null && birthday.length() > 0) {
			birthday = SystemTool.getInstance().getDateReplace(birthday, true).toString();
		}
		parm.setData("BIRTH_DATE", birthday);
		caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO", "CASE_NO"); // ����ȡ��ԭ��
		parm.setData("CASE_NO", caseNo);
		return parm;
	}

	/**
	 * ����У��
	 */
	public boolean checkData() {
		boolean flg = true;
		// У���Ա���Ϊ��
		if (this.getValue("SEX_CODE") == null || this.getValue("SEX_CODE").toString().length() <= 0) {
			this.messageBox("�Ա���Ϊ�գ�");
			this.grabFocus("SEX_CODE");
			flg = false;
			return flg;
		}
		// ���ܲ���Ϊ��
		if (this.getValue("GESTATIONAL_WEEKS") == null || this.getValue("GESTATIONAL_WEEKS").toString().length() <= 0) {
			this.messageBox("���ܲ���Ϊ�գ�");
			this.grabFocus("GESTATIONAL_WEEKS");
			flg = false;
			return flg;
		}
		// �������ڲ���Ϊ��
		if (this.getValue("BIRTH_DATE") == null || this.getValue("BIRTH_DATE").toString().length() <= 0) {
			this.messageBox("�������ڲ���Ϊ�գ�");
			this.grabFocus("BIRTH_DATE");
			flg = false;
			return flg;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sTime = getValueString("BIRTH_DATE").substring(0, 19);
			// System.out.println("sTime--"+sTime);
			try {
				Date bt = sdf.parse(sTime);
				Date now = new Date();
				if (bt.after(now)) {
					this.messageBox("�������ڲ��ܳ�����ǰ����!");
					this.grabFocus("BIRTH_DATE");
					flg = false;
					return flg;
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// ��������
		if (this.getValue("WEIGHT") == null || this.getValue("WEIGHT").toString().length() <= 0) {
			this.messageBox("�������ز���Ϊ�գ�");
			this.grabFocus("WEIGHT");
			flg = false;
			return flg;
		}

		// ������ add by yangjj 20150312 ���ӳ�����
		if (this.getValue("HEIGHT") == null || this.getValue("HEIGHT").toString().length() <= 0) {
			this.messageBox("����������Ϊ�գ�");
			this.grabFocus("HEIGHT");
			flg = false;
			return flg;
		}

		// ��λ����Ϊ�� add by sunqy 20140522
		if (this.getValue("BED_NO") == null || this.getValue("BED_NO").toString().length() <= 0) {
			this.messageBox("��ѡ��λ!");
			this.grabFocus("BED_NO");
			flg = false;
			return flg;
		}
		return flg;
	}

	/**
	 * ��ѯADM_INP �����Ƿ����������������
	 */
	public boolean queryIfHaveMrNo2(String bodyMrNo) {
		boolean flg = false;
		String sql = "SELECT A.CASE_NO,A.MR_NO FROM ADM_INP A  WHERE A.MR_NO ='" + bodyMrNo + "' "
				+ " AND A.IN_DATE IS NOT null " + " AND A.DS_DATE IS  NULL AND A.CANCEL_FLG <> 'Y' ";// SQL����
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			flg = true;
		}
		return flg;
	}

	/**
	 * ��ѯĸ��סԺADM_INP��Ϣ������TParm
	 */
	public TParm getAdmInpInfo(String mrNo, String ipdNo) {
		TParm parm = new TParm();
		String sql = "SELECT * FROM ADM_INP WHERE MR_NO = '" + mrNo + "' AND IPD_NO = '" + ipdNo + "'"
				+ " AND IN_DATE IS NOT NULL AND DS_DATE IS  NULL AND CANCEL_FLG <> 'Y' ";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * ����SYS_PATINFO��sql
	 * 
	 * public String insertPatinfoSql(TParm parm){ String sql = "INSERT INTO
	 * SYS_PATINFO(MR_NO,IPD_NO,PAT_NAME,BIRTH_DATE,SEX_CODE,WEIGHT,OPT_USER," +
	 * "OPT_DATE,OPT_TERM,GESTATIONAL_WEEKS)
	 * VALUES('"+parm.getValue("BODY_MR_NO")+"'," +
	 * "'"+parm.getValue("BODY_IPD_NO")+"'," + "'"+parm.getValue("BODY_NAME")+"'," +
	 * "TO_DATE('"+parm.getValue("BIRTH_DATE")+"','yyyyMMddHH24miss')," +
	 * "'"+parm.getValue("SEX_CODE")+"'," + "'"+parm.getValue("WEIGHT")+"'," +
	 * "'"+parm.getValue("OPT_USER")+"'," + "sysdate," +
	 * "'"+parm.getValue("OPT_TERM")+"'," +
	 * "'"+parm.getValue("GESTATIONAL_WEEKS")+"'" + ")";
	 * System.out.println("����syspatinfo-->"+sql); return sql; }
	 */

	// ��ȡ����������
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �����л���ʱ�����TopMenu
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * ȡ��ע��
	 */
	public void onCancel() {
		String bodyMrNo = this.getValueString("BODY_MR_NO");
		// ����У��
		if (bodyMrNo == null || bodyMrNo.length() <= 0) {
			this.messageBox("�����������Ų���Ϊ�գ���������");
			return;
		}
		String sql = "SELECT CASE_NO,MR_NO,IPD_NO FROM ADM_INP WHERE MR_NO='" + bodyMrNo + "'";
		TParm query = new TParm(TJDODBTool.getInstance().select(sql));
		if (query.getCount() <= 0) {
			this.messageBox("δ�����������Ϣ,��������");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", bodyMrNo);
		parm.setData("CASE_NO", query.getValue("CASE_NO", 0));
		parm.setData("IPD_NO", query.getValue("IPD_NO", 0));
		if (!checkDate(parm)) {
			return;
		}
		System.out.println("=-=====parm===========" + parm);
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMNewBodyRegisterAction", "onNewCancel", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ�� ");
			return;
		}
		this.messageBox("����ɹ�");
		this.onClear();
	}

	/**
	 * �������
	 *
	 * @return boolean
	 */
	public boolean checkDate(TParm parm) {
		if (InwForOutSideTool.getInstance().checkOrderisExistExec(parm)) {
			this.messageBox("������ִ�е�ҽ��,����ȡ��ע��");
			return false;
		}
		if (InwForOutSideTool.getInstance().checkOrderisExistCheck(parm)) {
			this.messageBox("��������˵�ҽ��,����ȡ��ע��");
			return false;
		}
		if (InwForOutSideTool.getInstance().checkOrderisExist(parm)) {
			this.messageBox("�����ѿ�����ҽ��,����ȡ��ע��");
			return false;
		}
		TParm parmfee = InwForOutSideTool.getInstance().checkOrderFee(parm);
		if (parmfee.getDouble("TOT_AMT", 0) != 0) {
			this.messageBox("�������ܷ���Ϊ:" + parmfee.getDouble("TOT_AMT", 0) + ",����ȡ��ע��");
			return false;
		}
		return true;
	}

	/**
	 * ������Ժ
	 */
	public void onResv() {
		TParm data = this.getParmForTag("BODY_NAME;SEX_CODE;BIRTH_DATE;HEIGHT;WEIGHT;BODY_MR_NO;GESTATIONAL_WEEKS");
		// TParm checkParm=checkPatIsExist(newMrNo);
		if (!"".equals(this.getValue("RELATION_MR_NO")) && this.getValue("RELATION_MR_NO") != null) {
			this.messageBox("�ò����Ѿ�������Ժ");
			return;
		}
		newMrNo = SystemTool.getInstance().getMrNo();
		TParm newParm = new TParm();
		TParm oldParm = new TParm();
		oldParm.setData("MR_NO", data.getValue("BODY_MR_NO"));
		oldParm.setData("MERGE_TOMRNO", newMrNo);
		oldParm.setData("MERGE_FLG", "Y");

		newParm.setData("MR_NO", newMrNo);
		newParm.setData("PAT_NAME", data.getValue("BODY_NAME"));
		newParm.setData("SEX_CODE", data.getValue("SEX_CODE"));
		newParm.setData("BIRTH_DATE",
				data.getValue("BIRTH_DATE").toString().substring(0, 19).replaceAll("-", "").replaceAll(":", ""));
		newParm.setData("NEW_BODY_HEIGHT", data.getDouble("HEIGHT"));
		newParm.setData("NEW_BODY_WEIGHT", data.getDouble("WEIGHT"));
		newParm.setData("GESTATIONAL_WEEKS", data.getValue("GESTATIONAL_WEEKS"));
		newParm.setData("OPT_TERM", Operator.getIP());
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_DATE",
				SystemTool.getInstance().getDate().toString().substring(0, 19).replaceAll("-", "").replaceAll(":", ""));
		TParm inParm = new TParm();
		inParm.setData("NEWPARM", newParm.getData());
		inParm.setData("OLDPARM", oldParm.getData());
		TParm result = TIOM_AppServer.executeAction("action.adm.ADMNewBodyRegisterAction", "newPatInfo", inParm);
		if (result.getErrCode() < 0) {
			this.messageBox("��Ժʧ�� ");
			return;
		}
		this.messageBox("��Ժ�ɹ�");
		this.setValue("RELATION_MR_NO", newMrNo);
	}
}
