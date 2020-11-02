package com.javahis.ui.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSTJAdm;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTextField;

/**
 * 
 * <p>
 * Title:סԺ�ʸ�ȷ�������غͿ���
 * </p>
 * 
 * <p>
 * Description:סԺ�ʸ�ȷ�������غͿ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2011-11-25
 * @version 2.0
 */
public class INSADMConfirmControl extends TControl {

	private String caseNO;// �����
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private TParm regionParm;// ҽ���������
	private String confirmNo;// �ʸ�ȷ����������ʹ��
	// �ڶ���ҳǩ
	private String pageTwo = "UNIT_CODE;INS_UNIT;IDNO;PAT_NAME;PAT_AGE;SEX_CODE;REGION_CODE;HOSP_CLASS_CODE;"
			+ "CTZ1_CODE;DIAG_DESC;IN_START_DATE;INP_TIME;USER_ID;TRANHOSP_DESC;TRAN_CLASS;"
			+ "TRAN_NUM;TRANHOSP_DAYS;INLIMIT_DATE;HOMEBED_TYPE;HOMEDIAG_DESC;"
			+ "HOMEBED_TIME;HOMEBED_DAYS;INSBRANCH_CODE;ADDPAY_AMT;ADDINS_AMT;"
			+ "ADDNUM_AMT;INSBASE_LIMIT_BALANCE;INS_LIMIT_BALANCE;INSOCC_CODE;EMG_FLG;"
			+ "INS_FLG;CANCEL_FLG;INS_CODE;CONFIRM_NO;UNIT_DESC";
	// ��һ��ҳǩ
	private String pageOne = "CONFIRM_NO1;RESV_NO;MR_NO;IDNO1;PAT_NAME1;ADM_PRJ1;"
			+ "ADM_CATEGORY1;SPEDRS_CODE1;DEPT_CODE1;DIAG_DESC1;IN_DATE;"
			+ "INSBRANCH_CODE1;INSOCC_CODE1;PERSONAL_NO;PRE_CONFIRM_NO;"
			+ "TRAN_NUM1;GS_CONFIRM_NO;PRE_OWN_AMT;PRE_NHI_AMT;PRE_ADD_AMT;"
			+ "PRE_OUT_TIME;SPE_DISEASE;OVERINP_FLG1;BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1";
	// ������ҳǩ
	private String pageThree = "REGION_CODE2;ADM_PRJ;ADM_CATEGORY;SPEDRS_CODE;START_STANDARD_AMT;RESTART_STANDARD_AMT;"
			+ "OWN_RATE;DECREASE_RATE;REALOWN_RATE;INSOWN_RATE;INSCASE_NO;STATION_DESC;BED_NO;"
			+ "TRANHOSP_RESTANDARD_AMT;DEPT_CODE;OVERINP_FLG;DEPT_DESC";
	private TParm insParm ;// ˢ������

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		getEnabledIsFalse(pageTwo + ";" + pageThree + ";APP_DATE;REGION_CODE1",
				false);// ����״̬
		this.setValue("APP_DATE", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE1", Operator.getRegion());// ҽԺ����
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		// System.out.println("regionParm:::"+regionParm);
		// ��ʼĬ������״̬
		onExeEnable(true);
		callFunction("UI|readCard|setEnabled", false);
		this.setValue("INSOCC_CODE1", "1");
		this.setValue("ADM_PRJ1", "2");
		this.setValue("ADM_CATEGORY1", "21");

		// this.setValue("INS_CROWD_TYPE", 1);// ��Ⱥ���
	}

	/**
	 * ��ѯԤԼδ�᰸
	 */
	public void onResvNClose() {
		queryTemp(true);

	}

	/**
	 * ��ѯסԺδ�᰸
	 */
	public void onAdmNClose() {
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
		// if (this.getValue("IDNO1").toString().length() > 0) {
		// //System.out.println("IDNO"+result.getValue("IDNO"));
		// //System.out.println("IDNO1"+this.getValue("IDNO1"));
		// if (null != insParm
		// && !result.getValue("IDNO").equals(this.getValue("IDNO1"))) {
		// this.messageBox("ˢ��������Ϣ��סԺ������Ϣ����");
		// onClear();
		// return;
		// }
		// }
		// //System.out.println("result::"+result);
		this.setValueForParm("RESV_NO;MR_NO;IN_DATE", result);
		setValueParm(result);
		this.setValue("DEPT_CODE1", result.getValue("DEPT_CODE"));
		caseNO = result.getValue("CASE_NO");// �����
		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", caseNO);
		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
		if (queryParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// System.out.println("resultl:::::::" + result);
		this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
				queryParm.getRow(0));
		this.setValue("DIAG_DESC1", result.getValue("DIAG_CODE")
				+ result.getValue("ICD_CHN_DESC"));// סԺ���
		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//ҽԺ����
		setValue("OVERINP_FLG1", "N");
		callFunction("UI|OVERINP_FLG1|setEnabled", true);
		this.setValue("ADM_PRJ1", "2");// ��ҽר��
		// getComboBox("PAY_TYPE").grabFocus();
		this.grabFocus("ADM_CATEGORY1");// ��ҽ���
		// getTextField("ADM_CATEGORY1").grabFocus();
	}

	/**
	 * ��֤����
	 * 
	 * @return
	 */
	private boolean checkSave() {
		if (getRadioButton("RO_Open").isSelected()) {// ����
			if (!this
					.emptyTextCheck("RESV_NO,REGION_CODE1,MR_NO,IDNO1,PAT_NAME1,INSOCC_CODE1,IN_DATE")) {
				return false;
			}
			if (this.getValue("INSBRANCH_CODE1").toString().length() <= 0) {// ������
				this.messageBox("ҽ�������Ĳ�����Ϊ��");
				this.grabFocus("INSBRANCH_CODE1");
				return false;
			}
		} else {// ����
			if (!this
					.emptyTextCheck("RESV_NO,MR_NO,IDNO1,PAT_NAME1,CONFIRM_NO1,INS_CROWD_TYPE")) {// �ʸ�ȷ������
				return false;
			}
		}
		if (this.getValueString("IDNO1").length() == 15
				|| this.getValueString("IDNO1").length() == 18) {

		} else {
			this.messageBox("���֤����ӦΪ15��18��");
			getTextField("IDNO1").grabFocus();
			return false;
		}
		if (getRadioButton("RO_Open").isSelected()) {// ����
			if (this.getValue("ADM_CATEGORY1").toString().length() <= 0) {// ��ҽ���
				this.messageBox("��ҽ��𲻿���Ϊ��");
				this.grabFocus("ADM_CATEGORY1");
				return false;
			}
			if (!this.emptyTextCheck("ADM_PRJ1")) {// ����ҽר��
				return false;
			}
			// �������
			if ((this.getValueString("ADM_CATEGORY1").equals("31")
					|| this.getValueString("ADM_CATEGORY1").equals("32")
					|| this.getValueString("ADM_CATEGORY1").equals("33") || this
					.getValueString("ADM_CATEGORY1").equals("34"))) {
				if (this.getValue("SPEDRS_CODE1").toString().length() <= 0) {// �������
					this.messageBox("������𲻿���Ϊ��");
					this.grabFocus("SPEDRS_CODE1");
					return false;

				} else {
					boolean flg = false;
					// ����ҽ���Ϊ���ⲡ��ѡ��ʱ�����������ѡ
					for (int i = 31; i < 35; i++) {
						if (this.getValueInt("ADM_CATEGORY1") == i) {
							flg = true;
							break;
						}
					}
					if (!flg) {
						if (this.getValue("SPEDRS_CODE1").toString().length() > 0) {
							this.messageBox("��ҽ���Ϊ���ⲡ,������𲻿���ѡ��");
							return false;
						}
					}
					if (this.getValueString("ADM_CATEGORY1").equals("31")// ��ҽ���
							|| this.getValueString("ADM_CATEGORY1")
									.equals("33")) {
						flg = false;// У��ʹ��
						for (int i = 1; i < 4; i++) {
							if (this.getValueInt("SPEDRS_CODE1") == i) {// �������
								flg = true;
								break;
							}
						}
						if (!flg) {
							// ��10�����������ϰ���ƶѪ��
							// ��11����������ѪС���������񲡯
							// ��21������Ѫ�Ѳ���
							// ��22����������ֲ�����������ơ�
							if (this.getValueInt("SPEDRS_CODE1") == 10
									|| this.getValueInt("SPEDRS_CODE1") == 11
									|| this.getValueInt("SPEDRS_CODE1") == 22
									|| this.getValueInt("SPEDRS_CODE1") == 21) {
								flg = true;
							}
						}
						if (!flg) {
							this.messageBox("��������������,ֻ��ѡ1,2,3,10,11,21,22");
							return false;
						}
					} else if (this.getValueString("ADM_CATEGORY1")
							.equals("34")
							|| this.getValueString("ADM_CATEGORY1")
									.equals("32")) {
						flg = false;// У��ʹ��
						for (int i = 4; i < 10; i++) {
							if (this.getValueInt("SPEDRS_CODE1") == i) {// �������
								flg = true;
								break;
							}
						}
						if (!flg) {
							if (this.getValueInt("SPEDRS_CODE1") == 30) {
								flg = true;
							}
						}
						if (!flg) {
							this.messageBox("��������������,ֻ��ѡ4,5,6,7,8,9,30");
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * ����ı��ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TTextField getTextField(String name) {
		return (TTextField) this.getComponent(name);
	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * ��������б�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TComboBox getComboBox(String name) {
		return (TComboBox) this.getComponent(name);
	}

	/**
	 * ��ø�ѡ�������б�
	 * 
	 * @param name
	 * @return
	 */
	private TCheckBox getCheckBox(String name) {
		return (TCheckBox) this.getComponent(name);
	}

	/**
	 * ���ҳǩ�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TTabbedPane getTabbedPane(String name) {
		return (TTabbedPane) this.getComponent(name);
	}

	/**
	 * ����/��������
	 */
	public void onSave() {
		// if (null==insParm ||null==insParm.getValue("SID") ||
		// insParm.getValue("SID").length()<=0) {
		// this.messageBox("��ִ��ˢ������");
		// return;
		// }
		if (!checkSave()) {
			return;
		}
		TParm result = null;
		if (getRadioButton("RO_Open").isSelected()) {// ����
			result = onSaveOpen();
		} else {// ����
			result = onSaveDown();
		}
		if (null == result) {
			return;
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");// ִ��ʧ��
			return;
		}
		if (getRadioButton("RO_Upd").isSelected()) {
			TParm queryParm = new TParm();
			queryParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO1"));// �ʸ�ȷ�������
			queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(
					queryParm);
			if (queryParm.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}
			// System.out.println("resultl:::::::" + result);
			this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
					queryParm.getRow(0));
			// this.setValueForParm(pageTwoNHI+";"+pageThree,result);
			this.messageBox("�ʸ�ȷ�������سɹ�");
		} else {
			this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
					result.getRow(0));
			this.messageBox("�ʸ�ȷ���鿪���ɹ�");
		}
		getTabbedPane("tTabbedPane_1").setSelectedIndex(1);
		// getEnabledIsFalse(pageTwo, false);
	}

	/**
	 * �ʸ�ȷ���鿪������
	 */
	private void openParm(TParm parm) {
		parm.setData("APP_DATE", df.format(this.getValue("APP_DATE")));// ��������
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������
		parm.setData("HOSP_CLASS_CODE", regionParm.getValue("HOSP_CLASS", 0));// ҽԺ�ȼ�
		parm.setData("INS_CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));// ��Ⱥ���
		parm.setData("SFBEST_TRANHOSP",
				this.getValueBoolean("OVERINP_FLG1") ? "1" : "0");// �Ƿ����
		// System.out.println("����ע��"+this.getValueBoolean("EMG_FLG1"));
		parm.setData("EMG_FLG", this.getValueBoolean("EMG_FLG1") ? "1" : "0");// �Ƿ���
		String[] pageOnes = pageOne.split(";");// ��һ��ҳǩ����
		for (int i = 0; i < pageOnes.length; i++) {
			parm.setData(pageOnes[i], this.getValue(pageOnes[i]));// ��õ�һ��ҳǩ����
		}
		if (this.getValueInt("INS_CROWD_TYPE") == 2) {// �Ǿ�
			parm.setData("HOMEDIAG_DESC1", this.getText("HOMEDIAG_CODE1"));// �Ҵ���������
			parm.setData("TRAMA_ATTEST", this.getText("TRAMA_ATTEST"));// ����֤��
		}
		parm.setData("DEPT_DESC", this.getText("DEPT_CODE1"));// ��������
		parm.setData("IN_DATE", this.getValue("IN_DATE").toString().replace(
				"-", "").substring(0, 8));// סԺ��ʼ����
		parm.setData("CASE_NO", caseNO);// �����
		parm.setData("IN_STATUS", "0");// ��Ժ״̬
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// System.out.println("/��õ�һ��ҳǩ����insParm::" + parm);

	}

	/**
	 * ִ�п�������
	 * 
	 * @return
	 */
	private TParm onSaveOpen() {
		TParm result = new TParm();
		TParm queryParm = new TParm();
		if (this.getValue("ADM_CATEGORY1").toString().length() <= 0) {
			this.grabFocus("ADM_CATEGORY1");
			this.messageBox("��ҽ�����Ϊ��");
			return null;
		}
		if (this.getValue("ADM_PRJ1").toString().length() <= 0) {
			this.grabFocus("ADM_PRJ1");
			this.messageBox("��ҽר������Ϊ��");
			return null;
		}		
		//���껼�߲�ˢ��
        if("Y".equals(getValueString("OVERINP_FLG1")))
        {
        	System.out.println("---------kuanian-----------------");
        	if (getValueString("PERSONAL_NO").length() <= 0) {
    			this.messageBox("��ִ��ˢ������");
    			return null;
    		}
        	TParm insParm = new TParm();
        	openParm(insParm);
    		insParm.setData("REGION_CODE", Operator.getRegion());
    		insParm.setData("CHECK_CODES","");
    		insParm.setData("CROWD_TYPE",getValue("INS_CROWD_TYPE"));
    		insParm.setData("SPEDRS_CODE1"," ");
    		Map insMap =insParm.getData();
    		result = new TParm(INSTJAdm.getInstance().onAdmConfirmOpen(insMap));
    		if (result.getErrCode() < 0) {
    			this.messageBox(result.getErrText());
    			return null;
    		}    		
        }else
        {
    		if (null == insParm || null == insParm.getValue("SID")
    				|| insParm.getValue("SID").length() <= 0) {
    			this.messageBox("��ִ��ˢ������");
    			return null;
    		}        
    		openParm(insParm);
    		insParm.setData("REGION_CODE", Operator.getRegion());
    		// insParm.setData("EXEFLG","Y");//ִ��ˢ��
    		// סԺ���INP_DIAG_DESC
    		if (null != insParm.getValue("PRE_OUT_TIME")
    				&& insParm.getValue("PRE_OUT_TIME").length() > 0) {
    			insParm.setData("PRE_OUT_TIME", insParm.getValue("PRE_OUT_TIME")
    					.replace("-", "").replace("/", "").substring(0, 8));

    		}
//    		  for (Iterator i = mapParm.keySet().iterator(); i.hasNext();) {
//    			   Object obj = i.next();
//    			   System.out.println(obj);// ѭ�����key
//    			   System.out.println("key=" + obj + " value=" + mapParm.get(obj));
//    			  }
//    		   result = new TParm(INSTJAdm.getInstance().onAdmConfirmOpen(a.getData()));
    		result = new TParm(INSTJAdm.getInstance().onAdmConfirmOpen(insParm.getData()));
    		if (result.getErrCode() < 0) {
    			this.messageBox(result.getErrText());
    			return null;
    		}
    		// } 		
        }
        if (null != result.getValue("NEWMESSAGE")
				&& result.getValue("NEWMESSAGE").length() > 0) {
			this.messageBox(result.getValue("NEWMESSAGE"));
		}
		// ����������ʾ�˲����ʸ�ȷ������Ϣ
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			this.messageBox(result.getValue("MESSAGE"));
			if (null != result.getValue("FLG")
					&& result.getValue("FLG").length() > 0) {// �ֽ�֧��
				return null;
			}
			queryAmdConfrim(result);
			return null;
		}
		return result;    		
		
	}

	/**
	 * ��ѯ��Ϣ�����ؿ���ʹ��
	 * 
	 * @param queryParm
	 */
	private void queryAmdConfrim(TParm queryParm) {
		this.setValueForParm(pageTwo + ";" + pageThree + ";INSCASE_NO",
				queryParm.getRow(0));
		this.setValue("REGION_CODE2", queryParm.getValue("REGION_CODE", 0));
		getTabbedPane("tTabbedPane_1").setSelectedIndex(1);
	}

	/**
	 * ִ�����ز���
	 * 
	 * @return
	 */
	private TParm onSaveDown() {
		// ���ز���
		TParm parm = new TParm();
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������
		parm.setData("IDNO", this.getValue("IDNO1"));// ���֤����
		parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO1"));// �ʸ�ȷ������
		parm.setData("CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));// ��Ⱥ���
		parm.setData("MR_NO", this.getValue("MR_NO"));// ������
		parm.setData("OPT_USER", Operator.getID());// ID
		parm.setData("CASE_NO", caseNO);// �����
		parm.setData("RESV_NO", this.getValue("RESV_NO"));// ԤԼ����
		parm.setData("OPT_TERM", Operator.getIP());// IP
		parm.setData("ADM_CATEGORY", this.getValue("ADM_CATEGORY1"));// ��ҽ���==pangben 2012-8-13
		parm.setData("HOSP_CLASS_CODE", regionParm.getValue("HOSP_CLASS", 0));// ҽԺ�ȼ�==pangben 2012-8-14
		// System.out.println("����������Σ���������"+parm);
		TParm admConfirmParm = new TParm();
		// ��ѯ�Ѿ����ڵ����ݣ�ִ�п������
		if (this.getCheckBox("OVERINP_FLG1").isSelected()) {
			admConfirmParm.setData("CONFIRM_NO", confirmNo);
			admConfirmParm = INSADMConfirmTool.getInstance()
					.queryCheckAdmComfirm(admConfirmParm);
			if (admConfirmParm.getErrCode() < 0) {
				return admConfirmParm;
			}
			parm.setData("admConfirmParm", admConfirmParm.getRow(0).getData());
		}
		TParm result = new TParm(INSTJAdm.getInstance().onAdmConfirmDown(
				parm.getData()));
		return result;
	}

	/**
	 * ���ÿؼ�����ѡ
	 * 
	 * @param name
	 * @param status
	 */
	private void getEnabledIsFalse(String name, boolean status) {
		String[] names = name.split(";");
		if (names.length <= 0) {
			return;
		}
		for (int i = 0; i < names.length; i++) {
			callFunction("UI|" + names[i] + "|setEnabled", status);
		}
	}

	public void onClear() {
		// ͷ��
		clearValue("CONFIRM_NO2;INS_ODI_NO;INS_CROWD_TYPE");
		// ��һ��ҳǩ
		clearValue(pageOne);
		getRadioButton("RO_Upd").isSelected();
		// �ڶ���ҳǩ
		clearValue(pageTwo);
		// ������ҳǩ
		clearValue(pageThree);
		caseNO = null;// �����
		insParm = null;// ˢ������
		confirmNo = null;// �ʸ�ȷ����������ʹ��
		callFunction("UI|IDNO1|setEnabled", true);// �����޸�IDNO
		this.setValue("INSOCC_CODE1", "1");
		this.setValue("ADM_PRJ1", "2");
		this.setValue("ADM_CATEGORY1", "21");
		callFunction("UI|OVERINP_FLG1|setEnabled", true);
	}

	/**
	 * �ӳ��걨
	 */
	public void onDelayApp() {
		// ���˱���

		// סԺ���
		// סԺ����
		if (!this.emptyTextCheck("PERSONAL_NO,DIAG_DESC1")) {
			return;
		}
		if (null == this.getValue("IN_DATE")
				|| this.getValue("IN_DATE").toString().length() <= 0) {
			this.messageBox("������סԺ����");
			this.grabFocus("IN_DATE");
			return;
		}
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
		TParm parm = new TParm();
		parm.setData("OWN_NO", this.getValue("PERSONAL_NO"));// ���˱���
		parm.setData("DIAG_DESC", this.getValue("DIAG_DESC1"));// ҽԺ���
		parm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������
		parm.setData("IN_DATE", df1.format(this.getValue("IN_DATE")));// ��Ժʱ��
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSDelayApply.x", parm);
	}

	/**
	 * �����ҽ�����߲�ѯ
	 * 
	 */
	public void onEveInsPat() {
		queryTemp(false);
	}

	/**
	 * ��ѯ����
	 * 
	 * @param flg
	 *            true:ԤԼδ�᰸ false:����Ȳ�ѯ
	 */
	private void queryTemp(boolean flg) {
		TParm parm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		if (flg) {
			parm.setData("FLG", "Y");
		} else {
			parm.setData("FLG", "N");
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSResvNClose.x", parm);
		if (this.getValue("IDNO1").toString().length() > 0) {
			if (null != insParm
					&& !result.getValue("IDNO").equals(this.getValue("IDNO1"))) {
				this.messageBox("ˢ��������Ϣ��סԺ������Ϣ����");
				// onClear();
				// return;
			}
		}
		this.setValueForParm("RESV_NO;MR_NO;IN_DATE", result);
		setValueParm(result);
		this.setValue("DEPT_CODE1", result.getValue("DEPT_CODE"));
		caseNO = result.getValue("CASE_NO");// �����
		if (!flg) {
			// Ĭ��ѡ����
			this.getRadioButton("RO_Open").setSelected(true);
			//��������Ϊ"Y",���ɱ༭
			this.setValue("OVERINP_FLG1", "Y");
			callFunction("UI|OVERINP_FLG1|setEnabled", false);
			//confirmNo = result.getValue("CONFIRM_NO");
			callFunction("UI|INS_CROWD_TYPE|setEnabled", true);// ��Ⱥ���
			//���껼��ȡ�ø��˱���
			String sql = " SELECT  A.PERSONAL_NO, B.MRO_CTZ  " +
					     " FROM JAVAHIS.INS_ADM_CONFIRM A, SYS_CTZ B  " +
					     " WHERE  A.MR_NO  ='"+ getValue("MR_NO")+"' " +
					     " AND  A.CASE_NO  ='"+caseNO+"' " +
					     " AND  A.HIS_CTZ_CODE  = B.CTZ_CODE ";
		    TParm resultIns = new TParm(TJDODBTool.getInstance().select(sql));
		    if (resultIns.getErrCode() < 0) 
		    {
		      messageBox("���˱���ȡ��ʧ�ܣ�");
		      return;
		    }
		    //���˱���
		    setValue("PERSONAL_NO", resultIns.getData("PERSONAL_NO", 0));
		    //��Ⱥ���
		    setValue("INS_CROWD_TYPE", resultIns.getData("MRO_CTZ",0));
		} else {
			this.setValue("OVERINP_FLG1", "N");
			callFunction("UI|OVERINP_FLG1|setEnabled", true);
		}
		this.setValue("DIAG_DESC1", result.getValue("DIAG_CODE")
				+ result.getValue("ICD_CHN_DESC"));// סԺ���
		// this.setValue("REGION_CODE1", result.getValue("REGION_CODE"));//ҽԺ����
		this.setValue("ADM_PRJ1", "2");// ��ҽר��
		// getComboBox("PAY_TYPE").grabFocus();
		this.grabFocus("ADM_CATEGORY1");// ��ҽ���
	}

	/**
	 * ˢ������
	 */
	public void onReadCard() {
		TParm parm = new TParm();
		if (!this.emptyTextCheck("MR_NO")) {
			return;
		}
		// parm.setData("MR_NO", this.getValue("MR_NO"));// ������
		// ��Ⱥ���
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCardOne.x", parm);
		if (null == insParm)
			return;
		int returnType = insParm.getInt("RETURN_TYPE");// ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			this.messageBox("��ȡҽ����ʧ��");
			return;
		}
		setParm(insParm, 1);
		this.grabFocus("ADM_CATEGORY1");
	}

	/**
	 * ִ��ˢ�� �� ��ѯ������Ϣ��ֵ
	 * 
	 * @param parm
	 */
	private void setParm(TParm parm, int type) {
		this.setValue("INS_CROWD_TYPE", parm.getValue("CROWD_TYPE"));// ��Ⱥ���ֵ
		this.setValue("PERSONAL_NO", parm.getValue("PERSONAL_NO"));// ���˱���
		// ���ݷ��ص����֤�����ò�����Ϣ
		parm.setData("IDNO", parm.getValue("SID"));// ���֤����
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("���������֤��Ϊ:" + parm.getValue("SID") + "����Ϣ");
			// onClear();
			parm = null;
			return;
		}
		// ��ҪУ��ʹ�� =================pangben 2012-3-18 �Ժ���Ҫ����
		if (type == 1) {// ˢ��
			if (this.getValue("IDNO1").toString().length() > 0) {
				if (!parm.getValue("SID").equals(this.getValue("IDNO1"))) {
					this.messageBox("ˢ��������Ϣ��סԺ������Ϣ����,\nҽ�����֤����Ϊ:"
							+ parm.getValue("SID"));
					// onClear();
					parm = null;
					return;
				}
			}
		}
		// ��ҪУ��ʹ�� =================pangben stop
		if (parm.getInt("CROWD_TYPE") == 1) {// ��ְ
			getIsEnabled(
					"GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_NHI_AMT;PRE_CONFIRM_NO;PRE_OUT_TIME",
					true);
			getIsEnabled("BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTEST",
					false);
		} else if (parm.getInt("CROWD_TYPE") == 2) {// �Ǿ�
			getIsEnabled(
					"GS_CONFIRM_NO;PRE_OWN_AMT;PRE_ADD_AMT;PRE_NHI_AMT;PRE_CONFIRM_NO;PRE_OUT_TIME",
					false);
			getIsEnabled("BEARING_OPERATIONS_TYPE;HOMEDIAG_CODE1;TRAMA_ATTEST",
					true);
		}

		// this.setValue("MR_NO", result.getRow(0).getValue("MR_NO"));// ������
		setValueParm(result.getRow(0));
		callFunction("UI|IDNO1|setEnabled", false);// ִ��ˢ���������޸�IDNO
	}

	/**
	 * ��ֵ
	 * 
	 * @param parm
	 */
	private void setValueParm(TParm parm) {
		this.setValue("PAT_NAME1", parm.getValue("PAT_NAME"));// ����
		this.setValue("IDNO1", parm.getValue("IDNO"));// ���֤����
	}

	/**
	 * ���ñ༭״̬
	 * 
	 * @param name
	 * @param flg
	 */
	private void getIsEnabled(String name, boolean flg) {
		String[] names = name.split(";");
		for (int i = 0; i < names.length; i++) {
			callFunction("UI|" + names[i] + "|setEnabled", flg);
		}
		this.clearValue(name);
	}

	/**
	 * ���ؿ�����ѡ��ѡ��
	 */
	public void onExe() {
		this.onClear();
		if (this.getRadioButton("RO_Upd").isSelected()) {// ����
			onExeEnable(true);
			callFunction("UI|readCard|setEnabled", false);
			// this.setValue("INS_CROWD_TYPE", 1);// ��Ⱥ���

		} else {// ����
			onExeEnable(false);
			callFunction("UI|readCard|setEnabled", true);
			this.setValue("INS_CROWD_TYPE", "");
		}
	}

	private void onExeEnable(boolean flg) {
		callFunction("UI|CONFIRM_NO1|setEnabled", flg);// �ʸ�ȷ����
		callFunction("UI|INS_CROWD_TYPE|setEnabled", flg);// ��Ⱥ���
	}

	/**
	 * ��ѯ��ť����
	 */
	public void onQueryInsInfo() {
		TParm queryParm = new TParm();
		// �ʸ�ȷ�����ź�ҽ��סԺ���
		if (this.getValue("CONFIRM_NO2").toString().length() <= 0
				&& this.getValue("INSCASE_NO1").toString().length() <= 0) {
			this.messageBox("�������ѯ������");
			this.grabFocus("CONFIRM_NO2");
			return;
		}
		if (this.getValue("CONFIRM_NO2").toString().length() > 0) {
			queryParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO2"));// �ʸ�ȷ�������
		}
		if (this.getValue("INSCASE_NO1").toString().length() > 0) {
			queryParm.setData("INSCASE_NO", this.getValue("INSCASE_NO1"));// ҽ��סԺ���
		}
		// ��ѯ����
		queryParm = INSADMConfirmTool.getInstance().queryADMConfirm(queryParm);
		if (queryParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (queryParm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			return;
		}
		queryAmdConfrim(queryParm);
	}
	/**
	 * סԺҽ���ʸ�ȷ������ʷ
	 */
	public void onConfirmNo() {
		TParm parm = new TParm();
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSSearchConfirm.x", parm);
	}
}
