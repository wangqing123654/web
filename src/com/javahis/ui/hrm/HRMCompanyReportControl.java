package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ����������屨�� </p>
 * 
 * <p> Description: ����������屨�� </p>
 * 
 * <p> Copyright: javahis 20090922 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyReportControl extends TControl {

	private TTable table;// ����TABLE
	private TTextFormat contract, patName;// ��ͬ����������TTextFormat
	private TRadioButton report,unReport;// ������δ����Radio
	private TCheckBox all;// ȫ������TCheckBox
	private String companyCode, contractCode;// ������롢��ͬ����
	private HRMPatInfo pat;// ��������
	private HRMPatAdm adm;// ��������
	private HRMOrder order;// ҽ������
	private HRMContractD contractD;// ��ͬ����
	private String packageCode, sexCode;// �ײʹ��롢�Ա���� add by wanglong 20121217
	private BILComparator compare = new BILComparator();// add by wanglong 20121217
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * ��ʼ���¼�
	 */
	public void onInit() {
		super.onInit();
		initComponent();// ��ʼ���ؼ�
		initData();// ��ʼ������
	}
	
	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		patName = (TTextFormat) this.getComponent("PAT_NAME");
		table = (TTable) this.getComponent("TABLE");
		addSortListener(table);// add by wanglong 20121217
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		report = (TRadioButton) this.getComponent("REPORT");
		unReport = (TRadioButton) this.getComponent("UNREPORT");
		all = (TCheckBox) this.getComponent("ALL");
		table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this,
				"onTabValueChanged");// ��ͬϸ��TABLEֵ�ı��¼�
	}
	
	/**
	 * ��ʼ������
	 */
	private void initData() {
		// ������ʾ�õĿؼ����
		this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE");
		unReport.setSelected(true);
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
		// ʵ�������ݶ���
		pat = new HRMPatInfo();
		adm = new HRMPatAdm();
		adm.onQuery();
		order = new HRMOrder();
		order.onQuery("", "");
		contractD = new HRMContractD();
		contractD.onQuery("", "", "");
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = table.getParmValue().getRow(row);
		this.setValueForParm("PAT_NAME;MR_NO;IDNO;SEX_CODE;PACKAGE_CODE", tableParm);
	}
	
	/**
	 * TABLEֵ�ı��¼�
	 * @param tNode
	 * @return
	 */
	public boolean onTabValueChanged(TTableNode tNode) {
//		if (TypeTool.getBoolean(this.getValue("REPORT"))) {
//			this.messageBox_("�ѱ���Ա�������޸���Ϣ");
//			return true;
//		}
		return false;
	}
	
	/**
	 * �Ƿ񱣴�CHECK_BOX�������������Ϊ�գ����ܱ���
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		TParm parm = table.getParmValue();
		int row = table.getSelectedRow();
		String deptCode = parm.getValue("DEPT_CODE", row);
		if (TypeTool.getBoolean(this.getValue("UNREPORT"))) {
			if (StringUtil.isNullString(deptCode)) {
				this.messageBox_("�������Ҳ���Ϊ��");
				parm.setData("CHOOSE", row, !TypeTool.getBoolean(parm.getData(
						"CHOOSE", row)));
				table.setParmValue(parm);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �һ�MENU�����¼�
	 * @param tableName
	 */
	public void showPopMenu() {
		if (unReport.isSelected()) {
			table.setPopupMenuSyntax("");
		} else {
			table.setPopupMenuSyntax("����,openRigthPopMenu");
		}
	}

	/**
	 * �Ҽ��¼�
	 */
	public void openRigthPopMenu() {
		TParm tableParm = table.getParmValue().getRow(table.getSelectedRow());
		tableParm.setData("PRO", "HRMCompanyReportControl");
		// System.out.println("tableParm"+tableParm);
		if (tableParm.getValue("BILL_NO").length() != 0) {
			this.messageBox("�Ѿ����㲻���Ի��");
			return;
		}
		this.openDialog("%ROOT%\\config\\hrm\\HRMPersonReport.x", tableParm);
	}
	
	/**
	 * ��������ѡ�¼�
	 */
	public void onCompanyChoose() {
		// ����ѡ���������룬���첢��ʼ��������ĺ�ͬ��ϢTTextFormat
		companyCode = this.getValueString("COMPANY_CODE");
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm == null || contractParm.getCount() <= 0
				|| contractParm.getErrCode() != 0) {
			this.messageBox_("û������");
			return;
		}
		// System.out.println("contractParm="+contractParm);
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = contractParm.getValue("ID", 0);// ============xueyf modify 23050305
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("��ѯʧ��");
			return;
		}
		contract.setValue(contractCode);    
		String isReport = this.getValueString("REPORT");
		TParm reportParm = contractD.getUnReportParm(companyCode, contractCode,
				isReport);
		table.setParmValue(reportParm);
		if (reportParm.getCount() >= 0) {
			this.setValue("COUNT", reportParm.getCount() + "");
		} else
			this.setValue("COUNT", 0 + "");
		contractD.setSQL("SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='"
				+ companyCode + "' AND CONTRACT_CODE='" + contractCode
				+ "' AND COVER_FLG='" + isReport + "'");
		contractD.retrieve();
		// ����ѡ��ĺ�ͬ���룬���첢��ʼ���ú�ͬ�Ĳ�����ϢTTextFormat
		TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
	}

	/**
	 * ��ͬ�����ѡ�¼�,����ѡ��ĺ�ͬ���룬���첢��ʼ���ú�ͬ�Ĳ�����ϢTTextFormat
	 */
	public void onConChoose() {
		contractCode = this.getValueString("CONTRACT_CODE");
		if (StringUtil.isNullString(contractCode)) {
			return;
		}
		String isReport = this.getValueString("REPORT");
		TParm reportParm = contractD.getUnReportParm(companyCode, contractCode,
				isReport);
		table.setParmValue(reportParm);
		if (reportParm.getCount() >= 0) {
			this.setValue("COUNT", reportParm.getCount() + "");
		} else
			this.setValue("COUNT", 0 + "");
		contractD.setSQL("SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='"
				+ companyCode + "' AND CONTRACT_CODE='" + contractCode
				+ "' AND COVER_FLG='" + isReport + "'");
		contractD.retrieve();
		// contractD.showDebug();
		TParm patParm = HRMContractD.getPatCombo(companyCode, contractCode);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
	}

	/**
	 * �ײʹ��롢�Ա�����ѡ�¼�
	 */
	public void onPackageAndSexChoose() {// add by wanglong 20121217
		onConChoose();
		packageCode = this.getValueString("PACKAGE_CODE");
		sexCode = this.getValueString("SEX_CODE");
		String isReport = this.getValueString("REPORT");
		TParm reportParm = table.getParmValue();
		if (reportParm == null) {
			return;
		}
		if (reportParm.getCount() < 1) {
			return;
		}
		int count = reportParm.getCount();
		if (StringUtil.isNullString(packageCode)
				&& StringUtil.isNullString(sexCode)) {
		} else if ((!StringUtil.isNullString(packageCode))
				&& (!StringUtil.isNullString(sexCode))) {// sex��Ϊ��
			for (int i = count - 1; i >= 0; i--) {
				if (reportParm.getValue("PACKAGE_CODE", i).equals(packageCode)
						&& reportParm.getValue("SEX_CODE", i).equals(sexCode)) {
					continue;

				} else
					reportParm.removeRow(i);
			}
		} else if (StringUtil.isNullString(packageCode)) {
			for (int i = count - 1; i >= 0; i--) {
				if (!reportParm.getValue("SEX_CODE", i).equals(sexCode)) {
					reportParm.removeRow(i);
				}
			}
		} else if (StringUtil.isNullString(sexCode)) {
			for (int i = count - 1; i >= 0; i--) {
				if (!reportParm.getValue("PACKAGE_CODE", i).equals(packageCode)) {
					reportParm.removeRow(i);
				}
			}
		}
		table.setParmValue(reportParm);
		if (reportParm.getCount() >= 0) {
			this.setValue("COUNT", reportParm.getCount() + "");
		} else
			this.setValue("COUNT", 0 + "");
		this.setValue("PAT_NAME", "");
		this.setValue("MR_NO", "");
		this.setValue("IDNO", "");
		String sql = "SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='"
				+ companyCode + "' AND CONTRACT_CODE='" + contractCode + "'";
		if (!StringUtil.isNullString(packageCode)) {
			sql += " AND PACKAGE_CODE='" + packageCode + "' ";
		}
		if (!StringUtil.isNullString(sexCode)) {
			sql += " AND SEX_CODE='" + sexCode + "' ";
		}
		sql += " AND COVER_FLG='" + isReport + "' ";
		contractD.setSQL(sql);
		contractD.retrieve();
		TParm patParm = HRMContractD.getPatComboByPackageAndSex(companyCode,
				contractCode, packageCode, sexCode);
		patName.setPopupMenuData(patParm);
		patName.setComboSelectRow();
		patName.popupMenuShowData();
	}

	/**
	 * ���ݲ������鵽����
	 */
    public void onPatName() {
        String patName = getText("PAT_NAME").trim();
        if (StringUtil.isNullString(patName)) {
            return;
        }
        String sql =
                "SELECT DISTINCT MR_NO, OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, "
                        + "BIRTH_DATE, POST_CODE, ADDRESS FROM SYS_PATINFO WHERE PAT_NAME = '"
                        + patName + "' ORDER BY OPT_DATE DESC NULLS LAST";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0081");// ���޴˲���
            onClear();
            return;
        } else {
            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
            TParm patParm = new TParm();
            if (obj != null) {
                patParm = (TParm) obj;
                setValue("MR_NO", patParm.getValue("MR_NO"));
                setValue("PAT_NAME", patName);
            } else return;
            onQueryByMr();
        }
    }

	/**
	 * �����Ų�ѯ
	 */
    public void onQueryByMr() {//modify by wanglong 20121217
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        this.setValue("MR_NO", mrNo);
        if (StringUtil.isNullString(companyCode)) {
            TParm result = HRMCompanyTool.getInstance().getContractDByMr(mrNo);
            if (result.getErrCode() != 0) {
                this.messageBox_("��ѯʧ��");
                return;
            }
            if (result.getCount() < 1) {
                return;
            } else if (result.getCount() == 1) {// ��������
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (result.getValue("COVER_FLG").equals("Y")) {
                    report.setSelected(true);// ����"�ѱ���"��ѡ��
                    this.callFunction("UI|save|setEnabled", false);
                    this.callFunction("UI|delete|setEnabled", true);
                } else {
                    unReport.setSelected(true);
                    this.callFunction("UI|save|setEnabled", true);
                    this.callFunction("UI|delete|setEnabled", false);
                }
                this.setValueForParm("COMPANY_CODE;PACKAGE_CODE;PAT_NAME;MR_NO;IDNO;SEX_CODE", result);//modify by wanglong 201212127
                //add by wanglong 20121227
                companyCode=result.getValue("COMPANY_CODE");
                TParm contractParm = contractD.onQueryByCompany(companyCode);
                if (contractParm == null || contractParm.getCount() <= 0
                        || contractParm.getErrCode() != 0) {
                    this.messageBox_("û������");
                    return;
                }
                contract.setPopupMenuData(contractParm);
                contract.setComboSelectRow();
                contract.popupMenuShowData();
                contractCode=result.getValue("CONTRACT_CODE");
                contract.setValue(contractCode);  
                packageCode = result.getValue("PACKAGE_CODE");
            } else {// ��������(����ѡ�񴰿�)
                Object obj = this.openDialog(
                        "%ROOT%\\config\\hrm\\HRMPatRecord.x", result);
                if (obj != null) {
                    TParm rowParm = (TParm) obj;
                    table.setParmValue(rowParm);
                    table.setSelectedRow(0);
                    rowParm = rowParm.getRow(0);
                    if (rowParm.getValue("COVER_FLG").equals("Y")) {
                        report.setSelected(true);// ����"�ѱ���"��ѡ��
                        this.callFunction("UI|save|setEnabled", false);
                        this.callFunction("UI|delete|setEnabled", true);
                    } else {
                        unReport.setSelected(true);
                        this.callFunction("UI|save|setEnabled", true);
                        this.callFunction("UI|delete|setEnabled", false);
                    }
                    this.setValueForParm("COMPANY_CODE;PACKAGE_CODE;PAT_NAME;MR_NO;IDNO;SEX_CODE",
                            rowParm);//modify by wanglong 201212127
                    //add by wanglong 20121227
                    companyCode=rowParm.getValue("COMPANY_CODE");
                    TParm contractParm = contractD.onQueryByCompany(companyCode);
                    if (contractParm == null || contractParm.getCount() <= 0
                            || contractParm.getErrCode() != 0) {
                        this.messageBox_("û������");
                        return;
                    }
                    contract.setPopupMenuData(contractParm);
                    contract.setComboSelectRow();
                    contract.popupMenuShowData();
                    contractCode=rowParm.getValue("CONTRACT_CODE");
                    contract.setValue(contractCode);  
                    packageCode=rowParm.getValue("PACKAGE_CODE");
                }
            }
        } else {// ��������
            TParm result = HRMCompanyTool.getInstance().getContractDByMr(
                    companyCode, contractCode, mrNo);
            if (result.getErrCode() != 0) {
                this.messageBox_("��ѯʧ��");
                return;
            }
            if (result.getCount() < 1) {
                return;
            } else if (result.getCount() == 1) {
                table.setParmValue(result);
                table.setSelectedRow(0);
                result = result.getRow(0);
                if (result.getValue("COVER_FLG").equals("Y")) {
                    report.setSelected(true);// ����"�ѱ���"��ѡ��
                    this.callFunction("UI|save|setEnabled", false);
                    this.callFunction("UI|delete|setEnabled", true);
                } else {
                    unReport.setSelected(true);
                    this.callFunction("UI|save|setEnabled", true);
                    this.callFunction("UI|delete|setEnabled", false);
                }
                this.setValueForParm("PAT_NAME;MR_NO;IDNO;SEX_CODE", result);
            }
        }
    }
	
	/**
	 * ���֤�Ų�ѯ
	 */
	public void onQueryByIdNo() {
		if (StringUtil.isNullString(companyCode)) {
			this.messageBox_("������벻��Ϊ��");
			return;
		}
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("��ͬ���벻��Ϊ��");
			return;
		}
		String idNo = this.getValueString("IDNO");
		String isReport = this.getValueString("REPORT");
		TParm conDParm = HRMCompanyTool.getInstance().getContractDById(
				companyCode, contractCode, idNo, isReport);
		table.setParmValue(conDParm);
	}

	/**
	 * �ѱ���
	 */
	public void onReport() {
		onQueryAfterSave();
		this.callFunction("UI|save|setEnabled", false);
		this.callFunction("UI|delete|setEnabled", true);
	}

	/**
	 * δ����
	 */
	public void onUnReport() {
		onQueryAfterSave();
        this.callFunction("UI|save|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
	}
	
	/**
	 * ȫѡ�¼�
	 */
	public void onChooseAll() {
		if (table == null) {
			return;
		}
		TParm parm = table.getParmValue();
		if (parm == null) {
			return;
		}
		if (parm.getCount() < 1) {
			return;
		}
		int count = parm.getCount();
		if (all.isSelected()) {
			for (int i = 0; i < count; i++) {
				parm.setData("CHOOSE", i, "Y");
			}
		} else {
			for (int i = 0; i < count; i++) {
				parm.setData("CHOOSE", i, "N");
			}
		}
		table.setParmValue(parm);
	}

    /**
     * ɸѡ
     */
    public void onCustomizeChoose() {// modify by wanglong 20130206
        TParm parm = table.getParmValue();
        if (((TRadioButton) this.getComponent("SEQ_BUTTON")).isSelected()) {// ѡ�����
            if (this.getValueString("START_SEQ_NO").equals("")
                    && this.getValueString("END_SEQ_NO").equals("")) {
                onQueryAfterSave();
                return;
            }
            if (!this.getValueString("START_SEQ_NO").matches("\\-?[0-9]+")
                    || !this.getValueString("END_SEQ_NO").matches("\\-?[0-9]+")) {
                messageBox("����������");
                return;
            }
            int startSeq = this.getValueInt("START_SEQ_NO");
            int endSeq = this.getValueInt("END_SEQ_NO");
            if (startSeq > endSeq) {
                startSeq = startSeq + endSeq;
                endSeq = startSeq - endSeq;
                startSeq = startSeq - endSeq;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (parm.getInt("SEQ_NO", i) < startSeq || (parm.getInt("SEQ_NO", i) > endSeq)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        } else if (((TRadioButton) this.getComponent("AGE_BUTTON")).isSelected()) {// ѡ������ add by wanglong 20130225
            if (this.getValueString("START_SEQ_NO").equals("")
                    && this.getValueString("END_SEQ_NO").equals("")) {
                onQueryAfterSave();
                return;
            }
            if (!this.getValueString("START_SEQ_NO").matches("\\-?[0-9]+")
                    || !this.getValueString("END_SEQ_NO").matches("\\-?[0-9]+")) {
                messageBox("����������");
                return;
            }
            int startAge = this.getValueInt("START_SEQ_NO");
            int endAge = this.getValueInt("END_SEQ_NO");
            if (startAge > endAge) {
                startAge = startAge + endAge;
                endAge = startAge - endAge;
                startAge = startAge - endAge;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (parm.getInt("AGE", i) < startAge || (parm.getInt("AGE", i) > endAge)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        } else if (((TRadioButton) this.getComponent("SEX_BUTTON")).isSelected()) {// ѡ���Ա�
            String sexCode = this.getValueString("CHOOSE_SEX_CODE");
            if (sexCode.equals("")) {
                onQueryAfterSave();
                return;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (!parm.getValue("SEX_CODE", i).equals(sexCode)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        } else if (((TRadioButton) this.getComponent("MARRIAGE_BUTTON")).isSelected()) {// ѡ����� add by wanglong 20130225
            String marriageCode = this.getValueString("MARRIAGE_CODE");
            if (marriageCode.equals("")) {
                onQueryAfterSave();
                return;
            }
            int count = parm.getCount();
            for (int i = count - 1; i >= 0; i--) {
                if (!parm.getValue("MARRIAGE_CODE", i).equals(marriageCode)) {
                    parm.removeRow(i);
                }
            }
            table.setParmValue(parm);
            if (parm.getCount() >= 0) {
                this.setValue("COUNT", parm.getCount() + "");
            } else this.setValue("COUNT", "");
        }
    }

    /**
     * ����ɸѡ��ťѡ���¼�
     */
    public void onCustomizeButtonChoose() {// add by wanglong 20130225
        if (((TRadioButton) this.getComponent("SEQ_BUTTON")).isSelected()// ѡ�����
                || ((TRadioButton) this.getComponent("AGE_BUTTON")).isSelected()) {// ѡ������
            this.callFunction("UI|START_SEQ_NO|setEnabled", true);
            this.callFunction("UI|END_SEQ_NO|setEnabled", true);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", false);
        } else if (((TRadioButton) this.getComponent("SEX_BUTTON")).isSelected()) {// ѡ���Ա�
            this.callFunction("UI|START_SEQ_NO|setEnabled", false);
            this.callFunction("UI|END_SEQ_NO|setEnabled", false);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", true);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", false);
        } else if (((TRadioButton) this.getComponent("MARRIAGE_BUTTON")).isSelected()) {// ѡ�����
            this.callFunction("UI|START_SEQ_NO|setEnabled", false);
            this.callFunction("UI|END_SEQ_NO|setEnabled", false);
            this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);
            this.callFunction("UI|MARRIAGE_CODE|setEnabled", true);
        }
    }
    
    /**
     * �����¼�
     */
    public void onSave() {
    	TParm result =onExeParm();
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            return;
        }
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < result.getCount(); i++) {
            mrList += "'" + result.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        // ����Ԥ��ӡ���������start======================$$//
        // δ��ӡ��¼
        List unReportRecords = new ArrayList();
        List listHl7 = new ArrayList();
        // �ж�HRM_ADM���Ƿ�������ݣ��������ݵĻ��� ֻ����һ�� HRM_ADM D����ѱ�����־
        Timestamp now = adm.getDBTime();
        TParm resultParm = new TParm();
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // this.messageBox("==mrNo=="+mrNo);
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (!StringUtil.isNullString(caseNo)) {// �ѱ���(����״̬���绰)
                String tel = parmRow.getValue("TEL");
                resultParm = adm.updateCoverFlg(caseNo, now, tel);
                contractD.updateTel(companyCode, contractCode, mrNo, tel);
                if (resultParm.getErrCode() < 0) {
                    continue;
                }
                resultParm = contractD.updateCoverFlg(companyCode, contractCode, mrNo, now, tel);
                if (resultParm.getErrCode() < 0) {
                    continue;
                }
                this.getHl7List(listHl7, caseNo);
                // ȷʵû��caseno˵�����ֳ�����,��¼�յ�CASE_NO
            } else {
                unReportRecords.add(parmRow);
            }
        }
        // $$==============add by lx 2012/05/22 end=======================$$//
        // ѭ������hrm_patadm,hrm_order,
        boolean flag = true;
        for (int i = 0; i < unReportRecords.size(); i++) {
            TParm parmRow = (TParm) unReportRecords.get(i);
            String mrNo = parmRow.getValue("MR_NO");
            TParm patParm = new TParm();
            for (int j = 0; j < patInfoTparm.getCount(); j++) {// add by wanglong 20130304
                if (patInfoTparm.getValue("MR_NO", j).equals(mrNo)) {
                    patParm = patInfoTparm.getRow(j);
                }
            }
            patParm.setData("PAT_NAME", result.getData("PAT_NAME", i));
            patParm.setData("COMPANY_PAY_FLG", result.getData("COMPANY_PAY_FLG", i));
            String compCode = (String) result.getData("COMPANY_CODE", i);
            String contraCode = (String) result.getData("CONTRACT_CODE", i);
            String packCode = result.getValue("PACKAGE_CODE", i);
            patParm.setData("COMPANY_CODE", compCode);
            patParm.setData("CONTRACT_CODE", contraCode);
            patParm.setData("PACKAGE_CODE", packCode);
            patParm.setData("REPORTLIST", result.getData("REPORTLIST", i));
            patParm.setData("INTRO_USER", result.getData("INTRO_USER", i));
            patParm.setData("DISCNT", result.getData("DISCNT", i));
            patParm.setData("TEL", result.getData("TEL", i));
            patParm.setData("MARRIAGE_CODE", result.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
            patParm.setData("PAT_DEPT", result.getData("PAT_DEPT", i));// add by wanglong 20130225
            if (!adm.onNewAdm(patParm, now)) {
                this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",����HRM_PATADM����ʧ��");
                flag = false;
                adm = new HRMPatAdm();
                adm.onQuery();
                order = new HRMOrder();
                order.onQuery("", "");
                continue;
            }
            String caseNo = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",ȡ������ʧ��");
                flag = false;
                adm = new HRMPatAdm();
                adm.onQuery();
                order = new HRMOrder();
                order.onQuery("", "");
                continue;
            }
            order.filt(caseNo);
            order.initOrderByTParm(packCode, caseNo, mrNo, contractCode, patParm);
            String tel = result.getValue("TEL", i);
            if (!StringUtil.isNullString(tel)) {
                contractD.updateTel(compCode, contraCode, mrNo, tel);
            }
            contractD.updateCoverFlg(companyCode, contractCode, mrNo, parmRow.getValue("TEL"));
            String[] sql = adm.getUpdateSQL();
            String updateAdm = // add by wanglong 20130408
                    "UPDATE HRM_PATADM SET COVER_FLG = 'Y', REPORT_DATE = TO_DATE( '"
                            + StringTool.getString(now, "yyyyMMddHHmmss")
                            + "', 'yyyyMMddHH24miss'), START_DATE = TO_DATE( '"
                            + StringTool.getString(now, "yyyyMMddHHmmss")
                            + "', 'yyyyMMddHH24miss'), END_DATE = TO_DATE( '"
                            + StringTool.getString(now, "yyyyMMddHHmmss")
                            + "', 'yyyyMMddHH24miss') WHERE CASE_NO = '" + caseNo + "'";
            sql = StringTool.copyArray(sql, new String[]{updateAdm });
            sql = StringTool.copyArray(sql, order.getUpdateSQL());
            sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm saveResult =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
            if (saveResult.getErrCode() != 0) {
                this.messageBox("���:" + result.getData("SEQ_NO", i) + "  ����:" + result.getData("PAT_NAME", i) + "\n����ʧ��");
                flag = false;
            } else {
                // this.messageBox_("�����ɹ�");
                this.getHl7List(listHl7, caseNo);
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        // ����HL7��Ϣ
        TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(listHl7);
        if (hl7Parm.getErrCode() < 0) {
            this.messageBox("����HL7��Ϣʧ��" + hl7Parm.getErrText());
            flag = false;
        }
        if (flag = true) {
            this.messageBox("�����ɹ�");
            this.setValue("REPORT", "Y");
        }
        onReport();
        ((TTextField) this.getComponent("MR_NO")).requestFocus();// add by wanglong 20130117
    }

	/**
	 * �õ�HL7����
	 * @param listHl7 List
	 * @param caseNo String
	 * @return List
	 */
	public List getHl7List(List listHl7, String caseNo) {
		String sql = "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='"
				+ caseNo + "' AND SEND_FLG < 2 AND STATUS <> 9";
		//  System.out.println("SQLMED=="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int rowCount = parm.getCount();	
//		String preLabNo = "";
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
//			String labNo = temp.getValue("LAB_NO");
//			if(!preLabNo.equals(labNo)){//delete by wanglong 20130402
				temp.setData("ADM_TYPE", "H");
				temp.setData("FLG", "0");
				listHl7.add(temp);
//				preLabNo = labNo ;
//			}
		}
		return listHl7;
	}

	/**
	 * չ��������Ŀ
	 */
	public void onOpenOrder() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        for (int i = 0; i < countParm; i++) {
            if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parm.getValue("DEPT_CODE", i))) {
                continue;
            }
            for (int j = 0; j < names.length; j++) {
                result.addData(names[j], parm.getValue(names[j], i));
            }
        }
        result.setCount(result.getCount("MR_NO"));
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            return;
        }
        if (count >= 1000) {//add by wanglong 20130426
            this.messageBox_("ÿ�β�����Ҫ����1000��");
            return;
        }
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < result.getCount(); i++) {
            mrList += "'" + result.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        // ����δ������ ����HRM_ADM��������
        // add by lx Ԥ����
        Timestamp now = adm.getDBTime();
        boolean flag = true;
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (StringUtil.isNullString(caseNo)) {
                TParm patParm = new TParm();
                for (int j = 0; j < patInfoTparm.getCount(); j++) {// add by wanglong 20130304
                    if (patInfoTparm.getValue("MR_NO", j).equals(mrNo)) {
                        patParm = patInfoTparm.getRow(j);
                    }
                }
                patParm.setData("PAT_NAME", result.getData("PAT_NAME", i));
                patParm.setData("COMPANY_PAY_FLG", result.getData("COMPANY_PAY_FLG", i));
                patParm.setData("COMPANY_CODE", result.getData("COMPANY_CODE", i));
                patParm.setData("CONTRACT_CODE", result.getData("CONTRACT_CODE", i));
                String packCode = result.getValue("PACKAGE_CODE", i);
                patParm.setData("PACKAGE_CODE", packCode);
                patParm.setData("REPORTLIST", result.getData("REPORTLIST", i));
                patParm.setData("INTRO_USER", result.getData("INTRO_USER", i));
                patParm.setData("DISCNT", result.getData("DISCNT", i));
                patParm.setData("TEL", result.getData("TEL", i));
                patParm.setData("MARRIAGE_CODE", result.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
                patParm.setData("PAT_DEPT", result.getData("PAT_DEPT", i));// add by wanglong 20130225
                // 1.Ԥ����
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",Ԥ��������HRM_PATADM����ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    flag = false;
                    continue;
                }
                // 2.HRM_ORDER
                String admCaseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                // caseNo = admCaseNo1;
                if (StringUtil.isNullString(admCaseNo1)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",ȡ������ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    flag = false;
                    continue;
                }
				order.filt(admCaseNo1);
                order.initOrderByTParm(packCode, admCaseNo1, mrNo, contractCode, patParm);
                String[] sql = adm.getUpdateSQL();
                sql = StringTool.copyArray(sql, order.getUpdateSQL());
                sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
                // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("���:" + result.getData("SEQ_NO", i) + "  ����:" + result.getData("PAT_NAME", i) + "\nҽ��չ��ʧ��");
                }
            } else {
                result.setData("CASE_NO", i, caseNo);// add by wanglong 20130304
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        if (flag == true) {
            this.messageBox("ҽ��չ���ɹ�");
        }
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        onQueryAfterSave();
	}
	
	/**
     *  ���������Ŀ
     */
    public void onCopyOrder() {//add by wanglong 20130508
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        int count = 0;
        for (int i = 0; i < countParm; i++) {
            if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
                count++;
                for (int j = 0; j < names.length; j++) {
                    result.addData(names[j], parm.getData(names[j], i));
                }
            }
        }
        if (count <= 0) {
            this.messageBox("��ѡ��Ҫ�����ļ�¼");
            return;
        }
        result.setCount(result.getCount("MR_NO"));
        if (result.getCount("MR_NO") == 1) {
            String caseNo = "";
            if (StringUtil.isNullString(result.getValue("CASE_NO", 0))) {
                caseNo = adm.getLatestCaseNoBy(result.getValue("MR_NO", 0), result.getValue("CONTRACT_CODE", 0));
                if(!StringUtil.isNullString(caseNo)){
                    this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ����չ��");
                    return;
                }
            }else{
                this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ����չ��");
                return;
            }
  
        }
        // �����򿪶���
        Object o = this.openDialog("%ROOT%\\config\\hrm\\HRMCopyOrder.x", result);
    }
    
    /**
     * ȡ��չ������
     * ==================pangben 2013-3-10 
     */
    public void onCloseOrder() {
        TParm result = onExeParm();
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("û����Ҫ����������");
            return;
        }
        if (report.isSelected()) {
            this.messageBox("�ѱ�����Ա�޷�ȡ��չ��������");
            return;
        }
        for (int i = count - 1; i >= 0; i--) {
            TParm pat = result.getRow(i);
            String patName = pat.getValue("PAT_NAME");
            String mrNo = pat.getValue("MR_NO");
            String contractCode = pat.getValue("CONTRACT_CODE");
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE MR_NO='" + mrNo
                            + "' AND CONTRACT_CODE='" + contractCode + "' AND BILL_NO IS NOT NULL";
            TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
            if (result1.getErrCode() != 0) {
                this.messageBox("����û�����״̬ʧ��");
                return;
            }
            if (result1.getCount() >= 0 && !result1.getValue("BILL_NO", 0).equals("")) {
                this.messageBox(patName + "�ѽ��㣬����ȡ��չ��");
                result.removeRow(i);
            }
        }
        if (this.messageBox("��ʾ", "�Ƿ�ִ��ȡ��չ������", 2) != 0) {
            return;
        }
        result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onDeleteOrder",
                                             result);
        if (result.getErrCode() < 0) {
            this.messageBox("ȡ��չ��ʧ�� " + result.getErrText());
            return;
        }
        if (result.getValue("INDEX_MESSAGE").length() > 0) {
            this.messageBox(result.getValue("INDEX_MESSAGE"));
        } else {
            if (result.getValue("HL7_MESSAGE").length() > 0) {
                this.messageBox("Ա��:" + result.getValue("HL7_MESSAGE") + " ȡ��չ��ʧ�ܣ�����HL7��Ϣʧ�ܣ�");
            }
            if (result.getValue("PAT_MESSAGE").length() > 0) {
                this.messageBox("Ա��:" + result.getValue("PAT_MESSAGE") + " û����Ҫȡ��������");
            }
            this.messageBox("ȡ��չ���ɹ�");
        }
        onQueryAfterSave();
    }

    /**
     * ��Ҫִ�е�����
     * =================pangben 2013-3-10
     * 
     * @return
     */
    private TParm onExeParm() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        int countParm = parm.getCount();
        int index = 0;
        for (int i = 0; i < countParm; i++) {
            if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parm.getValue("DEPT_CODE", i))) {
                continue;
            }
            result.setRowData(index, parm, i);
            index++;
        }
        result.setCount(index);
        return result;
    }
    
	/**
	 * ��ӡ������
	 */
	public void onReportPrint() {
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm result = new TParm();
        String[] names = parm.getNames();
        int countParm = parm.getCount();
        for (int i = 0; i < countParm; i++) {
            if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
                continue;
            }
            if (StringUtil.isNullString(parm.getValue("DEPT_CODE", i))) {
                continue;
            }
            for (int j = 0; j < names.length; j++) {
                result.addData(names[j], parm.getValue(names[j], i));
            }
        }
        result.setCount(result.getCount("MR_NO"));
        int count = result.getCount();
        if (count < 1) {
            this.messageBox_("�ޱ�������");
            return;
        }
        String mrList = "";
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < result.getCount(); i++) {
            mrList += "'" + result.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("��ѯ������Ϣ����");
            return;
        }
        // ����δ������ ����HRM_ADM��������
        // add by lx Ԥ����
        Timestamp now = adm.getDBTime();
        for (int i = 0; i < count; i++) {
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("������Ϊ��");
                continue;
            }
            String caseNo = "";
            String contractCode = parmRow.getValue("CONTRACT_CODE");
            if (parmRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = parmRow.getValue("CASE_NO");
            }
            // �ж���δ�������򱣴����ݣ������ó�δ����
            // 1.����HRM_PATADM
            if (StringUtil.isNullString(caseNo)) {
                TParm patParm = new TParm();
                for (int j = 0; j < patInfoTparm.getCount(); j++) {// add by wanglong 20130304
                    if (patInfoTparm.getValue("MR_NO", j).equals(mrNo)) {
                        patParm = patInfoTparm.getRow(j);
                    }
                }
                patParm.setData("PAT_NAME", result.getData("PAT_NAME", i));
                patParm.setData("COMPANY_PAY_FLG", result.getData("COMPANY_PAY_FLG", i));
                patParm.setData("COMPANY_CODE", result.getData("COMPANY_CODE", i));
                patParm.setData("CONTRACT_CODE", result.getData("CONTRACT_CODE", i));
                String packCode = result.getValue("PACKAGE_CODE", i);
                patParm.setData("PACKAGE_CODE", packCode);
                patParm.setData("REPORTLIST", result.getData("REPORTLIST", i));
                patParm.setData("INTRO_USER", result.getData("INTRO_USER", i));
                patParm.setData("DISCNT", result.getData("DISCNT", i));
                patParm.setData("TEL", result.getData("TEL", i));
                patParm.setData("MARRIAGE_CODE", result.getData("MARRIAGE_CODE", i));// add by wanglong 20130117
                patParm.setData("PAT_DEPT", result.getData("PAT_DEPT", i));// add by wanglong 20130225
                // 1.Ԥ����
                if (!adm.onPreAdm(patParm, now)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������"
                            + result.getData("PAT_NAME", i) + ",Ԥ��������HRM_PATADM����ʧ��");
                    // pat = new HRMPatInfo();
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    continue;
                }
				// 2.HRM_ORDER
                String admCaseNo1 = adm.getItemString(adm.rowCount() - 1, "CASE_NO");
                result.setData("CASE_NO", i, admCaseNo1);// add by wanglong 20130304
                if (StringUtil.isNullString(admCaseNo1)) {
                    this.messageBox_("���:" + result.getData("SEQ_NO", i) + "  ������" + result.getData("PAT_NAME", i) + ",ȡ������ʧ��");
                    adm = new HRMPatAdm();
                    adm.onQuery();
                    order = new HRMOrder();
                    order.onQuery("", "");
                    continue;
                }
                order.filt(admCaseNo1);
                order.initOrderByTParm(packCode, admCaseNo1, mrNo, contractCode, patParm);
                String[] sql = adm.getUpdateSQL();
                sql = StringTool.copyArray(sql, order.getUpdateSQL());
                sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
                // ���������̨���淽���Ĳ���������֤��̨���淽���ķ���ֵ�Ƿ�ɹ�
                TParm inParm = new TParm();
                Map inMap = new HashMap();
                inMap.put("SQL", sql);
                inParm.setData("IN_MAP", inMap);
                TParm saveResult =
                        TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
                if (saveResult.getErrCode() != 0) {
                    this.messageBox("���:" + result.getData("SEQ_NO", i) + "  ����:" + result.getData("PAT_NAME", i) + "\nҽ��չ��ʧ��");
                }
            } else {
                result.setData("CASE_NO", i, caseNo);// add by wanglong 20130304
            }
            adm = new HRMPatAdm();
            adm.onQuery();
            order = new HRMOrder();
            order.onQuery("", "");
        }
        // ����δ��������ӡ�������
        // $$==============add by lx end Ԥ����====================$$//
        TParm errParm = new TParm();
        String message = "";
        for (int i = 0; i < count; i++) {
            // System.out.println("parm==="+parm);
            TParm parmRow = result.getRow(i);
            String mrNo = parmRow.getValue("MR_NO");
            // System.out.println("mrNo========="+mrNo);
            String caseNo = "";
            if (parmRow.getValue("CASE_NO").length() == 0) caseNo =
                    adm.getLatestCaseNoBy(mrNo, contractCode);
            else caseNo = parmRow.getValue("CASE_NO");
            // δ���ɾ���ţ����Բ��ܴ�ӡ?????
            if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
                errParm.addRowData(parm, i);
                message +=
                        "��ţ�" + parmRow.getValue("SEQ_NO") + "  ������" + parmRow.getValue("PAT_NAME") + "\n";
                continue;
            }
			// ����SQL
//			parmRow = order.getReportTParm(mrNo, caseNo);//=======================��õ���������
            parmRow = IReportTool.getInstance().getReportParm("HRMReportSheetNew.class", parmRow);//��õ���������modify by wanglong 20130730
			if (parmRow == null) {
				this.messageBox_("ȡ������ʧ��111");
				continue;
			}
            if (parmRow.getErrCode() != 0) {
                this.messageBox_("ȡ������ʧ��222");
                continue;
            }
//            openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMReportSheetNew.jhw", parmRow,
//                            !this.getValueBoolean("PRINT_FLG"));
            openPrintDialog(IReportTool.getInstance().getReportPath("HRMReportSheetNew.jhw"),
                            parmRow, !this.getValueBoolean("PRINT_FLG"));//����ϲ�modify by wanglong 20130730
        }
        adm = new HRMPatAdm();
        adm.onQuery();
        order = new HRMOrder();
        order.onQuery("", "");
        onQueryAfterSave();
        if (message != null && !message.equals("")) {
            this.messageBox(message + "����չ��������Ŀ!");
        }
    }

	/**
	 * �����Ĳ�ѯ
	 */
	public void onQueryAfterSave() {
	    this.setValue("ALL", "N");
		this.clearValue("PAT_NAME;MR_NO;IDNO;SEX_CODE;PACKAGE_CODE");// add by wanglong 20121217
		onPackageAndSexChoose();// add by wanglong 20121217
	}

	/**
	 * �����ӡ
	 */
	public void onBarCode() {
		if (table == null) {
			return;
		}
		TParm tableParm = table.getParmValue();
		if (tableParm == null) {
			return;
		}
		int count = tableParm.getCount();
		if (count < 1) {
			return;
		}
		HRMPatAdm patAdm = new HRMPatAdm();
		for (int i = 0; i < count; i++) {
			if (!TypeTool.getBoolean(tableParm.getData("CHOOSE", i))) {
				continue;
			}
            String caseNo = adm.getLatestCaseNoBy(tableParm.getValue("MR_NO", i), contractCode);
			if (StringUtil.isNullString(caseNo)) {
				this.messageBox("�����Ϊ�գ����ȴ�ӡ�����������ȱ���");
				continue;
			}
			patAdm.onQueryByCaseNo(caseNo);
			TParm parm = new TParm();
			// ����
			parm.setData("DEPT_CODE", Operator.getDept());
			parm.setData("ADM_TYPE", "H");
	        parm.setData("COMPANY_CODE", tableParm.getValue("COMPANY_CODE", i));//modify by wanglong 20130726
	        parm.setData("CONTRACT_CODE", tableParm.getValue("CONTRACT_CODE", i));
			parm.setData("CASE_NO", caseNo);
			parm.setData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.setData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			parm.setData("ADM_DATE", patAdm.getItemData(0, "REPORT_DATE"));
			parm.setData("POPEDEM", "1");
			String value = (String) this.openDialog(
					"%ROOT%\\config\\med\\MEDApply.x", parm);
		}
	}

	/**
	 * ȡ�������¼�
	 */
    public void onDelete() {
        if (unReport.isSelected()) {
            this.messageBox("�޷�ȡ��������");
            return;
        }
        if (this.messageBox("��ʾ", "�Ƿ�ִ��ȡ����������", 2) != 0) {
            return;
        }
        TParm tableParm = table.getParmValue();
        int rowCount = tableParm.getCount();
        int rightCount = 0;// ��ȷ����
        for (int j = 0; j < rowCount; j++) {
            boolean chosen = tableParm.getBoolean("CHOOSE", j);
            if (!chosen) {
                continue;
            }
            TParm delParm = tableParm.getRow(j);
            String seqNo = delParm.getValue("SEQ_NO");
            String patName = delParm.getValue("PAT_NAME");
            String caseNo = delParm.getValue("CASE_NO");
            if (delParm.getValue("BILL_NO").length() != 0 || delParm.getInt("BILL_FLG") != 0) {
                this.messageBox("���:" + seqNo + "  ������" + patName + " �Ѿ����㣬������ȡ��������");
                continue;
            }
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO='" + caseNo
                            + "' AND BILL_NO IS NOT NULL";
            TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
            if (result1.getErrCode() != 0) {
                this.messageBox("����û�����״̬ʧ��");
                return;
            }
            if (result1.getCount() >= 0 && !result1.getValue("BILL_NO", 0).equals("")) {
                this.messageBox(patName + " ����ҽ�������㣬����ȡ������");
                continue;
            }
            String mrNo = delParm.getValue("MR_NO");
            String companyCode = delParm.getValue("COMPANY_CODE");
            String contractCode = delParm.getValue("CONTRACT_CODE");
            HRMContractD contract = new HRMContractD();
            String conSql =
                    "SELECT * FROM HRM_CONTRACTD WHERE COMPANY_CODE='" + companyCode
                            + "' AND CONTRACT_CODE='" + contractCode + "' AND MR_NO='" + mrNo + "'";
            contract.setSQL(conSql);
            contract.retrieve();
            contract.setItem(0, "COVER_FLG", "N");
            contract.setItem(0, "REAL_CHK_DATE", "");
            HRMPatAdm patAdm = new HRMPatAdm();
            patAdm.onQueryByCaseNo(caseNo);
            patAdm.deleteRow(patAdm.rowCount() - 1);
            HRMOrder order = new HRMOrder();
            order.onQueryByCaseNo(caseNo, "N");
            int count = order.rowCount();
            for (int i = count - 1; i > -1; i--) {
                order.deleteRow(i);
            }
            //Hl7����ȡ��ִ�е���Ϣ��med_apply����status=9,send_flg=1��
            List hl7ParmDel = new ArrayList();
            TParm orderDelParm = order.getBuffer(order.DELETE);
            int rowDelOrderCount = orderDelParm.getCount();
            for (int i = 0; i < rowDelOrderCount; i++) {
                TParm delTemp = new TParm();
                TParm tempDel = orderDelParm.getRow(i);
                if ("Y".equals(tempDel.getValue("SETMAIN_FLG"))
                        && tempDel.getValue("MED_APPLY_NO").length() != 0) {
                    delTemp.setData("ADM_TYPE", "H");
                    delTemp.setData("PAT_NAME", tableParm.getValue("PAT_NAME", j));
                    delTemp.setData("CAT1_TYPE", tempDel.getValue("CAT1_TYPE"));
                    delTemp.setData("CASE_NO", tempDel.getValue("CASE_NO"));
                    delTemp.setData("LAB_NO", tempDel.getValue("MED_APPLY_NO"));
                    delTemp.setData("ORDER_NO", tempDel.getValue("CASE_NO"));
                    delTemp.setData("SEQ_NO", tempDel.getValue("SEQ_NO"));
                    delTemp.setData("FLG", "1");
                    try {
                        if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")
                                && this.messageBox("��ʾ", patName + " ���ּ����ִ�У��Ƿ����ɾ������������ҽ����", 2) != 0) {
                            continue;
                        }
                    }
                    catch (Exception ex) {
                        System.err.print("�����ִ���ж�ʧ�ܡ�");
                        ex.printStackTrace();
                    }
                    hl7ParmDel.add(delTemp);
                }
            }
            if (hl7ParmDel.size() > 0) {// modify by wanglong 20130408
                TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ParmDel);
                if (hl7Parm.getErrCode() < 0) {
                    this.messageBox(patName + " ȡ������ʧ��(HL7��Ϣ����ʧ��) " + hl7Parm.getErrText());
                    continue;
                }
            }
            String[] sql = patAdm.getUpdateSQL();
            sql = StringTool.copyArray(sql, order.getUpdateSQL());
            sql = StringTool.copyArray(sql, contract.getUpdateSQL());
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm saveResult =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
            if (saveResult.getErrCode() != 0) {
                this.messageBox(patName + " ȡ������ʧ��");
                continue;
            }
            rightCount++;
        }
        if (rightCount > 0) {
            this.messageBox("ȡ�������ɹ�");
            this.setValue("UNREPORT", "Y");
            onUnReport();
        }
    }

	/**
	 * ����¼�
	 */
	public void onClear() {
		initData();
		table.setParmValue(new TParm());
		this.setValue("COMPANY_CODE", "");
		this.setValue("CONTRACT_CODE", "");
		this.setValue("PAT_NAME", "");
		this.setValue("MR_NO", "");
		this.setValue("IDNO", "");
		this.setValue("SEX_CODE", "");
		this.setValue("UNREPORT", "Y");
		this.setValue("ALL", "N");
		this.setValue("PRINT_FLG", "N");
		this.setValue("PACKAGE_CODE", "");
		this.setValue("START_SEQ_NO", "");
		this.setValue("END_SEQ_NO", "");
        this.setValue("CHOOSE_SEX_CODE", "");// add by wanglong 20130206
        this.callFunction("UI|SEQ_BUTTON|setSelected", true);// add by wanglong 20130206
        this.callFunction("UI|START_SEQ_NO|setEnabled", true);// add by wanglong 20130206
        this.callFunction("UI|END_SEQ_NO|setEnabled", true);// add by wanglong 20130206
        this.callFunction("UI|CHOOSE_SEX_CODE|setEnabled", false);// add by wanglong 20130206
		this.setValue("COUNT", "");
		companyCode = null;
		contractCode = null;
//		patName.getPopupMenuData().getData().clear();
//		patName.filter();
//		contract.getPopupMenuData().getData().clear();
//		contract.filter();
        patName.setPopupMenuData(new TParm());
        patName.popupMenuShowData();
        contract.setPopupMenuData(new TParm());
        contract.popupMenuShowData();
	}

	/**
	 * ��������
	 */
	public void batchAdd() {
		table.acceptText();
		TParm parm = table.getParmValue();
		TParm result = new TParm();
		String[] names = parm.getNames();
		int countParm = parm.getCount();
		int count = 0;
		for (int i = 0; i < countParm; i++) {
			if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
				count++;
				for (int j = 0; j < names.length; j++) {
					result.addData(names[j], parm.getData(names[j], i));
				}
			}
		}
		if (count <= 0) {
			this.messageBox("��ѡ��Ҫ�����ļ�¼");
			return;
		}
		result.setCount(result.getCount("MR_NO"));
        result.addData("METHOD", "ADD");
        if (result.getCount("MR_NO") == 1) {// add by wanglong 20130422 ��ֻѡ��һ����ʱ����ǰ��ʾ�Ƿ��Ѿ����㡣
            String billSql =
                    "SELECT DISTINCT A.BILL_NO, B.CASE_NO FROM HRM_ORDER A, HRM_PATADM B "
                            + " WHERE A.CASE_NO = B.CASE_NO AND A.MR_NO = '#' "
                            + " AND A.CONTRACT_CODE = '#'";
            billSql = billSql.replaceFirst("#", result.getValue("MR_NO", 0));
            billSql = billSql.replaceFirst("#", result.getValue("CONTRACT_CODE", 0));
            TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
            if (billParm.getErrCode() != 0) {
                this.messageBox("��������Ϣʧ�� " + billParm.getErrText());
                return;
            }
            if (billParm.getCount() > 1
                    || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                            .getValue("BILL_NO", 0).trim()))) {
                this.messageBox("������" + result.getValue("PAT_NAME", 0) + " �ѽ��㣬����������");
                return;
            } else if (billParm.getCount() < 1) {
                String patSql =
                        "SELECT * FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
                patSql = patSql.replaceFirst("#", result.getValue("MR_NO", 0));
                patSql = patSql.replaceFirst("#", result.getValue("CONTRACT_CODE", 0));
                TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));// add by wanglong 20130428
                if (patParm.getErrCode() != 0) {
                    this.messageBox("���ҽ��չ��״̬ʧ�� " + patParm.getErrText());
                    return;
                }
                if (patParm.getCount() < 1) {
                    this.messageBox("������" + result.getValue("PAT_NAME", 0) + " ҽ��δչ��������������");
                    return;
                }
            }
        }
		// �����򿪶���
		Object o = this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
		// �������ɹ���Ҫˢ��pat����
		if (o != null && !o.equals("")) {
			adm = new HRMPatAdm();
			adm.onQuery();
		}

	}

	/**
	 * ����ɾ��
	 */
	public void batchDelete() {
		table.acceptText();
		TParm parm = table.getParmValue();
		TParm result = new TParm();
		String[] names = parm.getNames();
		int countParm = parm.getCount();
		int count = 0;
		for (int i = 0; i < countParm; i++) {
			if ("Y".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {// �ж��Ƿ�ѡ���¼
				count++;
				for (int j = 0; j < names.length; j++) {
					result.addData(names[j], parm.getData(names[j], i));
				}
			}
		}
		if (count <= 0) {
			this.messageBox("��ѡ��Ҫɾ���ļ�¼");
			return;
		}
		result.setCount(result.getCount("MR_NO"));
		result.addData("METHOD", "DELETE");
		this.openDialog("%ROOT%\\config\\hrm\\HRMBatchAdd.x", result);
	}

	/**
     * ���µ绰         add by wanglong 20130110
     */
    public void onUpdateTel(){
        table.acceptText();
        TParm tableParm = table.getParmValue();
        TParm tableShowParm = table.getShowParmValue();
        for (int i = 0; i < tableShowParm.getCount(); i++) {
            tableParm.setData("TEL", i, tableShowParm.getData("TEL", i));
        }
        //System.out.println("===========update Tel========="+tableParm);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction",
                                             "updateHRMPatTEL", tableParm);
        if (result.getErrCode() != 0) {
            this.messageBox("" + result.getErrText());
            return;
        } else if (!result.getValue("MR_NO").equals("")) {
            String patNames = "";
            for (int i = 0; i < result.getCount(); i++) {
                patNames += result.getValue("PAT_NAME", i) + ",";
            }
            patNames = patNames.substring(0, patNames.length() - 1);
            messageBox(patNames + "\n����绰ʧ�ܣ�");
        } else {
            this.messageBox("P0001");// ����ɹ�
        }
        
    }
    
    /**
     * ���Excel
     */
    public void onExcel() {// add by wanglong 20130206
        if (table.getRowCount() <= 0) {
            this.messageBox("E0116");
            return;
        }
        if (unReport.isSelected()) {
            ExportExcelUtil.getInstance().exportExcel(table, "��������δ������Ա�б�");
        } else {
            ExportExcelUtil.getInstance().exportExcel(table, "���������ѱ�����Ա�б�");
        }
    }
    
	// ====================������begin======================add by wanglong 20121217
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================
	
}
