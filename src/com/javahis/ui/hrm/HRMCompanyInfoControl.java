package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import jdo.hrm.HRMCompany;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMContractM;
import jdo.hrm.HRMPackageD;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TUIStyle;
import com.dongyang.ui.base.TTableCellRenderer;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.DateTool;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �������������Ϣ������
 * </p>
 * 
 * <p>
 * Description: �������������Ϣ������
 * </p>
 * 
 * <p>
 * Copyright: javahis 20090922
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyInfoControl
        extends TControl {

    /** ���塢��ͬ���ײ͡�Ա��TABLE */
    private TTable comTab, conTab, packTab, mrTab;
    /** ��˾��Ϣ */
    private HRMCompany company;
    /** ��ͬ���� */
    private HRMContractM contractM;
    /** ��ͬ��ϸ */
    private HRMContractD contractD;
    /** ɾ��ʱ�õı��� */
    private String tableName;
    /** ������ͬTABLEʱ��¼��ͬ���� */
    private int conDescRow;
    private boolean isChangeContract = false;

    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // add by wanglong 20130304 ��Աtableѡ���б��
        TTableCellRenderer cellRenderer = new TTableCellRenderer(mrTab) {

            public void setComponentForeColor(Component component, boolean isSelected, int row,
                                              int column) {
                if (isSelected) {
                    component.setForeground(Color.RED);// TUIStyle.getTableSelectionForeColor()
                    return;
                }
                /**
                 * �Զ�����������ɫ
                 */
                Color color = getTable().getRowTextColor(row);
                if (color != null) {
                    component.setForeground(color);
                    return;
                }
                // �Զ�����ǰ����ɫ
                color = getColumnForeColor(getTable().getColumnModel().getColumnIndex(column));
                if (color != null) {
                    component.setForeground(color);
                    return;
                }
                component.setForeground(TUIStyle.getTableForeColor());
            }
        };
        mrTab.setCellRenderer(cellRenderer);
        // ��ʼ������
        initData();
        onClear();
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        company = new HRMCompany();
        company.onQuery();
        comTab.setDataStore(company);
        comTab.setDSValue();
        contractM = new HRMContractM();
        contractM.onQuery();
        conTab.setDataStore(contractM);
        contractD = new HRMContractD();
        contractD.onQuery();
        if (isChangeContract) {
            mrTab.setDataStore(contractD);
        }
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        comTab = (TTable) this.getComponent("TAB_COMPANY");
        conTab = (TTable) this.getComponent("TAB_CONTRACT");
        // ��ͬTABLEֵ�ı��¼�
        conTab.addEventListener("TAB_CONTRACT->" + TTableEvent.CHANGE_VALUE, this,
                                "onTabConValueChanged");
        packTab = (TTable) this.getComponent("TAB_PACK");
        mrTab = (TTable) this.getComponent("TAB_MR");
        // ��ͬϸ��TABLEֵ�ı��¼�
        mrTab.addEventListener("TAB_MR->" + TTableEvent.CHANGE_VALUE, this, "onTabMrValueChanged");
        mrTab.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBox");
        // �½��ײ�COMBO����ʼ����COMBO,�����丳ֵ��TABLE
        TComboBox packageCombo = new TComboBox();
        TParm packageParm = HRMCompanyTool.getInstance().getPackageComboParm();
        // System.out.println("packageParm="+packageParm);
        if (packageParm.getErrCode() < 0) {
            this.messageBox_("ȡ���ײ�����ʧ��");
            return;
        }
        packageCombo.setParmValue(packageParm);
        packageCombo.setParmMap("id:ID;name:NAME");
        packageCombo.setShowID(false);
        packageCombo.setShowName(true);
        packageCombo.setTableShowList("name");
        mrTab.addItem("PACKAGE", packageCombo);
    }

    /**
     * ����TABLE�����¼�
     */
    public void onTabComClicked() {
        tableName = "COMPANY";
        int row = comTab.getSelectedRow();
        this.setValue("COMPANY_DESC", company.getItemString(row, "COMPANY_DESC"));
        this.setValue("PY1", company.getItemString(row, "PY1"));
        this.setValue("PY2", company.getItemString(row, "PY2"));
        this.setValue("DESCRIPTION", company.getItemString(row, "DESCRIPTION"));
        this.setValue("ADMINISTRATOR", company.getItemString(row, "ADMINISTRATOR"));
        this.setValue("TEL", company.getItemString(row, "TEL"));
        this.setValue("IND_TYPE_CODE", company.getItemString(row, "IND_TYPE_CODE"));
        this.setValue("FAX_NO", company.getItemString(row, "FAX_NO"));
        this.setValue("CONTACTS_NAME", company.getItemString(row, "CONTACTS_NAME"));
        this.setValue("CONTACTS_TEL", company.getItemString(row, "CONTACTS_TEL"));
        this.setValue("ADDRESS", company.getItemString(row, "ADDRESS"));
        this.setValue("POST_CODE", company.getItemString(row, "POST_CODE"));
        this.setValue("E_MAIL", company.getItemString(row, "E_MAIL"));
        contractM.filt(company.getItemString(row, "COMPANY_CODE"));
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * ��ͬTABLE�����¼�
     */
    public void onTabConClicked() {
        tableName = "CONTRACT";
        int row = conTab.getSelectedRow();
        String contractCode = contractM.getItemString(row, "CONTRACT_CODE");
        conDescRow = row;
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        // ͨ����ͬ���롢���������˺�ͬϸ����Ϣ���ж������ͬϸ���������һ�в��ǿ��У�������һ��
        contractD.filt(contractM.getItemString(row, "CONTRACT_CODE"),
                       contractM.getItemString(row, "COMPANY_CODE"));
        if (contractD.rowCount() < 1
                || !StringUtil.isNullString(contractD.getItemString(contractD.rowCount() - 1,
                                                                    "MR_NO"))) {
            int rowCount = contractD.rowCount();
            contractD.insertRow(contractM.getItemString(row, "COMPANY_CODE"),
                                contractM.getItemString(row, "CONTRACT_CODE"),
                                contractM.getItemString(row, "CONTRACT_DESC"),
                                contractM.getItemDouble(row, "DISCNT"), rowCount + 1);
        }
        if (mrTab.getDataStore() instanceof HRMContractD) {
            mrTab.setDSValue();
        } else {
            mrTab.setDataStore(contractD);
            mrTab.setDSValue();
        }
    }

    /**
     * Ա����ͬ���(�����⣬�޸�)
     */
    public void onChangeContract() {
        TTable table = (TTable) this.getComponent("TAB_MR");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��������Ա��Ϣ����һ���");
            return;
        }
        TParm showParm = table.getShowParmValue().getRow(row);
        if (showParm.getValue("MR_NO").equals("Y")) {
            this.messageBox("�������Ա��δ���浽���ݿ⣬�޷����");
            return;
        }
        if (showParm.getValue("COVER_FLG").equals("Y")) {
            this.messageBox("�ѱ����������޷����");
            return;
        }
        if (showParm.getValue("BILL_FLG").equals("Y")) {
            this.messageBox("���շѲ������޷����");
            return;
        }
        TParm dataStoreParm = mrTab.getDataStore().getRowParm(row);
        TParm parm = new TParm();
        parm.setData("showParm", showParm);
        parm.setData("dataStoreParm", dataStoreParm);
        TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\hrm\\HRMChangeContract.x", parm);
        if (reParm == null) {
            this.messageBox("��ͬ���ʧ��!");
            return;
        }
        // �Ȳ�ѯ������Ƿ�������һ����ͬ��в��ܱ��
        String selectSql =
                " SElECT A.COMPANY_CODE FROM HRM_CONTRACTD A " + " WHERE A.COMPANY_CODE='"
                        + reParm.getValue("COMPANY_CODE") + "' AND A.CONTRACT_CODE='"
                        + reParm.getValue("CONTRACT_CODE") + "' AND A.MR_NO='"
                        + dataStoreParm.getValue("MR_NO") + "' ";
        TParm selectParm = new TParm();
        selectParm.setData(TJDODBTool.getInstance().select(selectSql));
        if (selectParm.getCount("COMPANY_CODE") > 0) {
            this.messageBox("��ͬ���ʧ��!ͬһ����ͬ�ﲻ�����ظ������");
            return;
        }
        String updateSql =
                " UPDATE HRM_CONTRACTD A SET A.COMPANY_CODE='" + reParm.getValue("COMPANY_CODE")
                        + "' , " + " A.CONTRACT_CODE='" + reParm.getValue("CONTRACT_CODE")
                        + "',A.CONTRACT_DESC='" + reParm.getValue("CONTRACT_DESC") + "' "
                        + " WHERE A.COMPANY_CODE='" + dataStoreParm.getValue("COMPANY_CODE") + "'"
                        + " AND A.CONTRACT_CODE='" + dataStoreParm.getValue("CONTRACT_CODE")
                        + "' AND A.MR_NO='" + dataStoreParm.getValue("MR_NO") + "' ";
        TParm result = new TParm();
        isChangeContract = true;
        result.setData(TJDODBTool.getInstance().update(updateSql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            this.messageBox("���ʧ�ܣ����� " + reParm.getValue("CONTRACT_DESC") + " ���Ƿ����иñ����Ա");
            return;
        }
        initData();
        onTabConClicked();
        isChangeContract = false;
        this.messageBox("����ɹ�");
    }

    /**
     * Ա��TABLE�����¼�
     */
    public void onTabMrClicked() {
        tableName = "MR";
        // conTab.clearSelection() ;
    }

    /**
     * ��ͬTABLEֵ�ı��¼�
     * 
     * @param tNode
     */
    public boolean onTabConValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = conTab.getParmMap(column);
        Object value = tNode.getValue();
        Object oldValue = tNode.getOldValue();
        // ����Ǻ�ͬ���ƣ����Զ����ɼ�ƴ���룬������ͬ���Ƹ�ֵ��ÿһ����ͬϸ��
        if ("CONTRACT_DESC".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (StringUtil.isNullString(strValue) || strValue.equalsIgnoreCase(strOldValue)) {
                return true;
            }
            // ========================= chenxi modify 20130313
            for (int i = 0; i < conTab.getRowCount(); i++) {
                if (strValue.equals(conTab.getItemString(i, "CONTRACT_DESC"))) {
                    this.messageBox("��ͬ����������");
                    return true;
                }
            }
            String py1 = TMessage.getPy(strValue);
            contractM.setItem(row, "PY1", py1);
            contractM.setActive(row, true);
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "CONTRACT_DESC", strValue);
                contractD.setItem(i, "SEQ_NO", i + 1);
            }
            mrTab.setDSValue();
            return false;
        }
        // ������ײʹ������ײʹ��븳ֵ��ÿһ����ͬϸ��
        if ("PACKAGE_CODE".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (StringUtil.isNullString(strValue) || strValue.equalsIgnoreCase(strOldValue)) {
                return true;
            }
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "PACKAGE_CODE", strValue);
            }
            mrTab.setDSValue();
            contractM.setActive(row, true);
            return false;
        }
        // ������ۿ���Ϣ��˾֧��%�ȣ����ж��Ƿ��ֵ����1������ǣ���Ϊ�Ƿ����֡������޸�
        if ("DISCNT".equalsIgnoreCase(colName) || "CP_PAY".equalsIgnoreCase(colName)) {
            double num = Double.parseDouble(tNode.getValue() + "");
            double oldNum = Double.parseDouble(tNode.getOldValue() + "");
            if (num == oldNum) {
                return true;
            }
            if (num > 1.0) {
                this.messageBox_("�ۿ���Ӧ����0��1֮��");
                return true;
            }
            String billFlg = contractM.getItemString(row, "BILL_FLG");
            if (billFlg != null && billFlg.equals("Y")) {
                this.messageBox_("�Ѿ����㲻���޸�");
                return true;
            }
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "DISCNT", num);
            }
            // ==== add by lx 2012/05/18 �����ܼ�=====$$//
            TParm parm = conTab.getDataStore().getRowParm(row);
            contractM
                    .setItem(row, "TOT_AMT", StringTool.round(parm.getDouble("SUBTOTAL") * num, 2));
            conTab.setDSValue();
            mrTab.setDSValue();
            contractM.setActive(row, true);
            return false;
        }
        return false;
    }

    /**
     * Ա��TABLEֵ�ı��¼�
     * 
     * @param tNode
     * @return
     */
    public boolean onTabMrValueChanged(TTableNode tNode) {
        // if (conTab.getItemString(conTab.getSelectedRow(), "CONTRACT_DESC").trim().equals("")) {
        // add-by-wanglong-20130302
        // this.messageBox_("��ͬ��Ϊ�գ�����¼����Ա");
        // return true;
        // }
        tableName = "MR";
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mrTab.getParmMap(column);
        Object value = tNode.getValue();
        Object oldValue = tNode.getOldValue();
        if (TypeTool.getBoolean(contractD.getItemData(row, "COVER_FLG"))) {
            this.messageBox_("�ѵ�����Ա��Ϣ�����޸�");
            return true;
        }
        mrTab.acceptText();
        if ("FOREIGNER_FLG".equalsIgnoreCase(colName)) {
            if (!StringUtil.isNullString(contractD.getItemString(row, "IDNO"))) {
                contractD.setItem(row, "IDNO", "");
            }
            // if ("Y".equals(value.toString())) {
            // contractD.setItem(row, "MR_NO", "Y");
            // } else {
            // contractD.setItem(row, "MR_NO", "");
            // }
            return false;
        }
        // ������ۿ���Ϣ��˾֧��%�ȣ����ж��Ƿ��ֵ����1������ǣ���Ϊ�Ƿ����֡������޸�
        if ("DISCNT".equalsIgnoreCase(colName)) {
            double num = Double.parseDouble(tNode.getValue() + "");
            double oldNum = Double.parseDouble(tNode.getOldValue() + "");
            if (num == oldNum) {
                return true;
            }
            if (num > 1 || num < -1) {
                this.messageBox_("�ۿ���Ӧ����0��1֮��");
                return true;
            }
            contractM.setActive(row, true);
            return false;
        }
        // ������
        if ("MR_NO".equalsIgnoreCase(colName)) {// add by wanglong 20130506
            String strValue = (String) value;
            if (strValue.trim().equals("")) {
                return true;
            }
            if (contractD.isActive(row)) {
                this.messageBox("��ȷ���Ĳ����Ų����޸ģ���ɾ��������¼������¼��");
                return true;
            }
            String mrNo = PatTool.getInstance().checkMrno(strValue.trim());
            TParm parm = PatTool.getInstance().getInfoForMrno(mrNo);
            if (parm.getCount() <= 0) {
                this.messageBox_("�����Ų�����");
                return true;
            }
            contractD.setItem(row, "IDNO", parm.getData("IDNO", 0));
            if (!isId(parm.getValue("IDNO", 0))) {
                contractD.setItem(row, "FOREIGNER_FLG", "Y");
            }
            contractD.setItem(row, "MR_NO", mrNo);
            contractD.setItem(row, "PAT_NAME", parm.getData("PAT_NAME", 0));
            contractD.setItem(row, "SEX_CODE", parm.getData("SEX_CODE", 0));
            contractD.setItem(row, "BIRTHDAY", parm.getData("BIRTH_DATE", 0));
            contractD.setItem(row, "MARRIAGE_CODE", parm.getData("MARRIAGE_CODE", 0));
            if ((row == contractD.rowCount() - 1) && contractD.getItemInt(row, "SEQ_NO") == 0) {
                contractD.setItem(row, "SEQ_NO", contractD.getItemInt(row - 1, "SEQ_NO") + 1);
            }
            mrTab.setDSValue(row);
            tNode.setValue(mrNo);// add by wanglong 20130829
            tNode.getTable().grabFocus();
            mrTab.setSelectedColumn(tNode.getTable().getColumnIndex("PACKAGE_CODE"));
            setColorTable();
            return false;
        }
        // �ж��Ƿ�Ϊ��Ч���֤��
        if ("IDNO".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            if (strValue.trim().equals("")) {
                return true;
            }
//            String strOldValue = (String) oldValue;
            String isForeigner = contractD.getItemString(row, "FOREIGNER_FLG");
            String checkid = checkID(strValue);
            if (!checkid.equals("TRUE") && !checkid.equals("FALSE")) {// add by wanglong 20130409
                this.messageBox_("���֤У��λ�������һλӦ��Ϊ" + checkid.substring(17, 18));
                contractD.setItem(row, "SEX_CODE", StringTool.isMaleFromID(checkid));// add by
                                                                                     // wanglong
                                                                                     // 20130417
                contractD.setItem(row, "BIRTHDAY", StringTool.getBirdayFromID(checkid));
                return false;// modify by wanglong 20130416
            }
            if (!isId(strValue)
                    && ("N".equalsIgnoreCase(isForeigner) || StringUtil.isNullString(isForeigner))) {
                this.messageBox_("���֤����ȷ");
                return true;
            }
            // ============xueyf modify 20120305 start
            TParm mrParm =
                    // modify by wanglong 20130502
                    new TParm(
                            TJDODBTool
                                    .getInstance()
                                    .select("SELECT SYS_PATINFO.*,OPT_DATE AS REPORT_DATE FROM SYS_PATINFO WHERE IDNO='"
                                                    + strValue + "' ORDER BY MR_NO DESC"));
            // ============xueyf modify 20120305 stop
            if (mrParm.getCount("MR_NO") <= 0) {
                contractD.setItem(row, "MR_NO", "Y");
            }
            if (mrParm.getCount("MR_NO") > 0) {// ///////add by wanglong 20130502
                if (!isId(strValue)) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д���ʹ�ô�֤������Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", mrParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            contractD.setItem(row, "PAT_NAME", samePatParm.getData("PAT_NAME"));
                            contractD.setItem(row, "SEX_CODE", samePatParm.getData("SEX_CODE"));
                            contractD.setItem(row, "BIRTHDAY", samePatParm.getData("BIRTH_DATE"));
                            contractD.setItem(row, "MARRIAGE_CODE",
                                              samePatParm.getData("MARRIAGE_CODE"));
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                } else {
                    contractD.setItem(row, "MR_NO", mrParm.getData("MR_NO", 0));
                    contractD.setItem(row, "PAT_NAME", mrParm.getData("PAT_NAME", 0));
                    contractD.setItem(row, "MARRIAGE_CODE", mrParm.getData("MARRIAGE_CODE", 0));
                }
            }
            if (isId(strValue)) {// modify by wanglong 20130502
                String sexCode = StringTool.isMaleFromID(strValue);
                contractD.setItem(row, "SEX_CODE", sexCode);
                contractD.setItem(row, "BIRTHDAY", StringTool.getBirdayFromID(strValue));
            }
            if ((row == contractD.rowCount() - 1) && contractD.getItemInt(row, "SEQ_NO") == 0) {
                contractD.setItem(row, "SEQ_NO", contractD.getItemInt(row - 1, "SEQ_NO") + 1);
            }
            mrTab.setDSValue(row);
            tNode.getTable().grabFocus();
            mrTab.setSelectedColumn(tNode.getTable().getColumnIndex("PACKAGE_CODE"));
            setColorTable();
            return false;
        }
        // ����ǳ������֤�ŵ����������ж����֤��û��Ͳ�����д
        if (StringUtil.isNullString(contractD.getItemString(row, "IDNO"))
                && !StringTool.getBoolean(contractD.getItemString(row, "FOREIGNER_FLG"))) {
            this.messageBox_("�����������֤��");
            return true;
        }
        // ��������
        if ("PAT_NAME".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (strValue.equalsIgnoreCase(strOldValue) || StringUtil.isNullString(strValue)) {
                return true;
            }
            String mrNo1 = contractD.getItemString(row, "MR_NO");
            if (!StringUtil.isNullString(mrNo1) && !mrNo1.equalsIgnoreCase("Y")) {// modify by
                                                                                  // wanglong
                                                                                  // 20130608
                String contractCode1 = contractD.getItemString(row, "CONTRACT_CODE");
                String sql1 =
                        "SELECT * FROM HRM_PATADM WHERE MR_NO = '" + mrNo1
                                + "' AND CONTRACT_CODE = '" + contractCode1 + "'";
                // System.out.println("-----------sql1---------------"+sql1);
                TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
                if (result1.getErrCode() < 0) {
                    this.messageBox("�����Ա��Ϣ����");
                    return true;
                }
                if (result1.getCount() > 0) {
                    this.messageBox("ҽ����չ�������ܸ��������������������ݲ�ƥ��");// add by wanglong 20130308
                    return true;
                }
            } else if (!StringUtil.isNullString(mrNo1)) {
                String idNo = contractD.getItemString(row, "IDNO");
                String mrNo = contractD.getItemString(row, "MR_NO");
                Timestamp birthday = contractD.getItemTimestamp(row, "BIRTHDAY");
                // add by wanglong 20130223 ����ͬ��ͬ�������Ա�������źϲ�
                if (idNo.length() == 18 && isId(idNo) && mrNo.equals("Y")) {
                    Timestamp idBirthday = StringTool.getBirdayFromID(idNo);
                    String samePatSql =
                            "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,MARRIAGE_CODE "
                                    + "    FROM SYS_PATINFO      "
                                    + "   WHERE PAT_NAME = '#' # "
                                    + "ORDER BY OPT_DATE DESC NULLS LAST";
                    samePatSql = samePatSql.replaceFirst("#", strValue);
                    String birthYear = StringTool.getString(idBirthday, "yyyy");
                    samePatSql =
                            samePatSql.replaceFirst("#", " AND TO_CHAR( BIRTH_DATE, 'yyyy') = '"
                                    + birthYear + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getCount() > 0) {
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + strValue
                                + "��ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm patParm = (TParm) obj;
                                contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                            }
                        }
                    }
                }
                // add by wanglong 20130223 ����ͬ��ͬ�������Ա�������źϲ�
                else if (birthday != null && mrNo.equals("Y")) {
                    String samePatSql =
                            "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,MARRIAGE_CODE "
                                    + "    FROM SYS_PATINFO      "
                                    + "   WHERE PAT_NAME = '#' # "
                                    + "ORDER BY OPT_DATE DESC NULLS LAST";
                    samePatSql = samePatSql.replaceFirst("#", strValue);
                    String birthYear = StringTool.getString(birthday, "yyyy");
                    samePatSql =
                            samePatSql.replaceFirst("#", " AND TO_CHAR( BIRTH_DATE, 'yyyy') = '"
                                    + birthYear + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getCount() > 0) {
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + strValue
                                + "��ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm patParm = (TParm) obj;
                                contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                            }
                        }
                    }
                }
                contractD.setItem(row, "COVER_FLG", "N");
            }
            String py = SystemTool.getInstance().charToCode(tNode.getValue() + "");
            contractD.setItem(row, "PY1", py);
            contractD.setActive(row, true);
            String comCode = contractM.getItemString(conDescRow, "COMPANY_CODE");
            String contractCode = contractM.getItemString(conDescRow, "CONTRACT_CODE");
            String contractDesc = contractM.getItemString(conDescRow, "CONTRACT_DESC");
            Double discnt = contractM.getItemDouble(conDescRow, "DISCNT");
            // contractD.setItem(row, "MR_NO", "Y");
            int count = contractD.rowCount();
            if (contractD.isActive(contractD.rowCount() - 1)
                    && !contractD.getItemString(row, "PACKAGE_CODE").equals("")) {
                contractD.insertRow(comCode, contractCode, contractDesc, discnt, count + 1);
            }
            // contractD.setItem(row, "SEQ_NO", count);
            mrTab.setDSValue();
            mrTab.getTable().grabFocus();
            mrTab.setSelectedRow(row);
            if (contractD.getItemString(row, "PACKAGE_CODE").equals("")) {
                mrTab.setSelectedColumn(mrTab.getColumnIndex("PACKAGE_CODE"));
            } else mrTab.setSelectedColumn(mrTab.getColumnIndex("STAFF_NO"));
            // onPackage();//delete by wanglong 20130314
            return false;
        }
        // ��������
        if ("BIRTHDAY".equalsIgnoreCase(colName)) {// add by wanglong 20130223
            Timestamp newBirthday = (Timestamp) value;
            String idNo = contractD.getItemString(row, "IDNO");
            String mrNo = contractD.getItemString(row, "MR_NO");
            String patName = contractD.getItemString(row, "PAT_NAME");
            if (idNo.length() == 18 && isId(idNo)) {
                Timestamp birthday = StringTool.getBirdayFromID(idNo);
                if (!newBirthday.equals(birthday)) {
                    this.messageBox("���������֤����");
                    return true;
                }
            } else if (newBirthday != null && mrNo.equals("Y") && !patName.equals("")) {
                String samePatSql =
                        "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,MARRIAGE_CODE "
                                + "    FROM SYS_PATINFO      "
                                + "   WHERE PAT_NAME = '#' # "
                                + "ORDER BY OPT_DATE DESC NULLS LAST";
                samePatSql = samePatSql.replaceFirst("#", patName);
                String birthYear = StringTool.getString(newBirthday, "yyyy");
                samePatSql =
                        samePatSql.replaceFirst("#", " AND TO_CHAR( BIRTH_DATE, 'yyyy') = '"
                                + birthYear + "' ");
                TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                if (result.getCount() > 0) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName
                            + "��ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                        if (obj != null) {
                            TParm patParm = (TParm) obj;
                            contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                        }
                    }
                }
            }
        }
        // Ԥ��ʱ��
        if ("PRE_CHK_DATE".equalsIgnoreCase(colName)) {
            Timestamp now = contractD.getDBTime();
            Timestamp valueTime = (Timestamp) value;
            Timestamp oldValueTime = (Timestamp) oldValue;
            if (valueTime.getTime() == oldValueTime.getTime()) {
                return true;
            }
            if (valueTime.getTime() < now.getTime()) {
                return true;
            }
            return false;
        }
        // ��,30,boolean;��˾����,140;��ͬ����,140;
        // �ۿ�,60,double;���ʽ,100,PAY_TYPE;��˾֧��,100,double;���㷽ʽ,100;��ԼС��,80,double;�ܼ�,60,double;�˵���,100;Ʊ�ݺ�,100
        // BILL_FLG; COMPANY_DESC;CONTRACT_DESC;DISCNT; PAY_TYPE; CP_PAY;
        // RECP_TYPE; TOT_AMT; REAL_COUNT; BILL_NO;RECEIPT_NO
        // ��д�ײʹ���ʱ�����ײ͵��ܼ�д���ͬ�������
        if ("PACKAGE_CODE".equalsIgnoreCase(colName)) {
            String mrNo1 = contractD.getItemString(row, "MR_NO");
            String contractCode1 = contractD.getItemString(row, "CONTRACT_CODE");
            String sql1 =
                    "SELECT * FROM HRM_PATADM WHERE MR_NO = '" + mrNo1 + "' AND CONTRACT_CODE = '"
                            + contractCode1 + "'";
            // System.out.println("-----------sql1---------------"+sql1);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
            if (result.getErrCode() < 0) {
                this.messageBox("�����Ա��Ϣ����");
                return true;
            }
            if (result.getCount() > 0) {
                this.messageBox("ҽ����չ�������ܸ����ײ�");// add by wanglong 20130308
                return true;
            }
            // boolean flag = false;
            // String sql1 =
            // "SELECT PACKAGE_DESC FROM HRM_PACKAGEM WHERE PACKAGE_CODE = '" + value + "'";
            // TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
            // // System.out.println("====================PACKAGE_DESC=================="+result);
            // if (result.getErrCode() < 0) {
            // this.messageBox("��ѯ�ײ���Ϣ����");
            // return true;
            // }
            // if (result.getCount() > 0) {
            // if (result.getValue("PACKAGE_DESC", 0).indexOf("�Զ���") < 0) {
            // this.messageBox("���ܸ��ĳɴ��ײͣ��������ݲ�ƥ��");// modify by wanglong 20130308
            // return true;
            // } else {
            // flag = true;
            // }
            // }
            // this.messageBox("===onTabMrValueChanged===");
            double subTotal = contractM.getItemDouble(conDescRow, "SUBTOTAL");
            double oldPrice = HRMPackageD.getPackageAmt(tNode.getOldValue() + "");
            double newPrice = HRMPackageD.getPackageAmt(tNode.getValue() + "");
            contractM.setItem(conDescRow, "SUBTOTAL", subTotal - oldPrice + newPrice);
            int mrSelectRow = mrTab.getSelectedRow();
            int mrSelectColumn = mrTab.getSelectedColumn();
            mrTab.setDSValue();
            int conSelectRow = conTab.getSelectedRow();
            int conSelectColumn = conTab.getSelectedColumn();
            // $$==== add by lx 2012-05-19 ��ͬ�ײͱ仯���ܼ�Ҳ�仯start====$$//
            // this.messageBox("SUBTOTAL"+contractM.getItemDouble(conDescRow,
            // "SUBTOTAL"));
            contractM.setItem(conDescRow,
                              "TOT_AMT",
                              StringTool.round(contractM.getItemDouble(conDescRow, "SUBTOTAL")
                                      * contractM.getItemDouble(conDescRow, "DISCNT"), 2));
            // this.messageBox("==TOT_AMT=="+contractM.getItemDouble(conDescRow,
            // "TOT_AMT"));
            // $$==== add by lx 2012-05-19 ��ͬ�ײͱ仯���ܼ�Ҳ�仯 end====$$//
            conTab.setDSValue(conDescRow);
            mrTab.setSelectedRow(mrSelectRow);
            mrTab.setSelectedColumn(mrSelectColumn);
            conTab.setSelectedRow(conSelectRow);
            conTab.setSelectedColumn(conSelectColumn);
            // $$======add by lx 2012-06-10 �ı��ײ���ɾ��
            // HRM_ORDER(CONTRACT_CODE,MR_NO)��===$$//
            String flg = contractD.getItemString(row, "FOREIGNER_FLG").trim();// �����ע��
            String idNo = contractD.getItemString(row, "IDNO");
            if (flg.equals("Y") || !idNo.equals("")) {// add by wanglong 20130107
                contractD.setActive(row, true);
                String comCode = contractM.getItemString(conDescRow, "COMPANY_CODE");
                String contractCode = contractM.getItemString(conDescRow, "CONTRACT_CODE");
                String contractDesc = contractM.getItemString(conDescRow, "CONTRACT_DESC");
                Double discnt = contractM.getItemDouble(conDescRow, "DISCNT");
                int count = contractD.rowCount();
                if (contractD.isActive(count - 1)) {// modify by wanglong 20130318
                    if (contractD.getItemString(row, "MR_NO").equals("")) {// add by wanglong
                                                                           // 20130327
                        contractD.setItem(row, "MR_NO", "Y");
                    }
                    int newRow = contractD.insertRow();
                    Timestamp now = SystemTool.getInstance().getDate();
                    contractD.setItem(newRow, "COMPANY_CODE", comCode);
                    contractD.setItem(newRow, "CONTRACT_CODE", contractCode);
                    contractD.setItem(newRow, "CONTRACT_DESC", contractDesc);
                    contractD.setItem(newRow, "PRE_CHK_DATE", now);
                    contractD.setItem(newRow, "PACKAGE_CODE", contractD.packageCode);
                    // this.setItem(newRow, "MR_NO", "Y");
                    contractD.setItem(newRow, "OPT_USER", Operator.getID());
                    contractD.setItem(newRow, "OPT_TERM", Operator.getIP());
                    contractD.setItem(newRow, "DISCNT", discnt);
                    contractD.setItem(newRow, "OPT_DATE", now);
                    contractD.setItem(newRow, "SEQ_NO", 0);
                    contractD.setActive(newRow, false);
                    // contractD.insertRow(comCode, contractCode, contractDesc, discnt, count + 1);
                }
                // contractD.setItem(row, "SEQ_NO", count);
                mrTab.setDSValue();
                mrTab.getTable().grabFocus();
                mrTab.setSelectedRow(row);
            }
            // String contractCode = contractM.getItemString(conDescRow, "CONTRACT_CODE");
            // String strMrNo = mrTab.getItemString(mrSelectRow, "MR_NO");
            // String patADmSQL = "SELECT CASE_NO FROM HRM_PATADM WHERE MR_NO='" + strMrNo + "'";
            // patADmSQL += " AND CONTRACT_CODE='" + contractCode + "'";
            // TParm parm = new TParm(TJDODBTool.getInstance().select(patADmSQL));
            // String caseNo = parm.getValue("CASE_NO", 0);
            // if (flag == true) {// modify by wanglong 20130308
            // String updateADMSQL =
            // "update HRM_PATADM set package_code = '" + value + "' WHERE CASE_NO='"
            // + caseNo + "'";
            // updateAdmSql = StringTool.copyArray(updateAdmSql, new String[]{updateADMSQL });
            // }
            // System.out.println("=====caseNo======="+caseNo);
            // String delADMSQL = "DELETE FROM HRM_PATADM WHERE CASE_NO='" + caseNo + "'";
            // TJDODBTool.getInstance().update(delADMSQL);
            // String sql = "DELETE FROM HRM_ORDER WHERE MR_NO='" + strMrNo + "'";
            // sql += " AND CONTRACT_CODE='" + contractCode + "' AND CASE_NO='" + caseNo + "' ";
            // // System.out.println("=====sql======="+sql);
            // TJDODBTool.getInstance().update(sql);
            // $$======add by lx 2012-06-10 �ı��ײ���ɾ��
            // HRM_ORDER(CONTRACT_CODE,MR_NO)��===$$//
            return false;
        }
        // ���
        if ("SEQ_NO".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            if (strValue.trim().equals("")) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * ɾ���¼�
     */
    public void onDelete() {
        if (StringUtil.isNullString(tableName)) {
            this.messageBox_("û��ɾ������");
            return;
        }
        // ���ɾ��������Ϣ�����ͬ���ϸ����Ϣͬʱɾ��
        if ("COMPANY".equalsIgnoreCase(tableName)) {
            int row = comTab.getSelectedRow();
            String companyCode = company.getItemString(row, "COMPANY_CODE");
            // ===============add by wanglong 20130314
            String coverSql =
                    "SELECT DISTINCT COVER_FLG FROM HRM_CONTRACTD WHERE COMPANY_CODE = '"
                            + companyCode + "' ORDER BY COVER_FLG DESC";// add by wanglong 20130304
            TParm coverParm = new TParm(TJDODBTool.getInstance().select(coverSql));
            if (coverParm.getErrCode() != 0) {
                this.messageBox("��ѯ���屨����Ϣ����");
                return;
            }
            boolean coverFlg = false;
            // if (coverParm.getCount() > 1 || coverParm.getBoolean("COVER_FLG", 0)) {
            // coverFlg = true;
            // }
            if (coverParm.getCount() >= 1) {// modify by wanglong 20130327
                for (int i = 0; i < coverParm.getCount(); i++) {
                    if (coverParm.getBoolean("COVER_FLG", 0)) {
                        coverFlg = true;
                        break;
                    }
                }
            }
            if (coverFlg == true && this.messageBox("��ʾ", "������������Ա�������Ƿ����ɾ��", 2) != 0) {
                return;
            }
            company.deleteRow(row);
            contractM.deleteContract(companyCode);
            contractD.deleteContractByCompanyCode(companyCode);
            comTab.setDSValue();
            conTab.setDSValue();
            mrTab.setDSValue();
            String[] sql = company.getUpdateSQL();
            sql = StringTool.copyArray(sql, contractM.getUpdateSQL());
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]>();
            // for (int i = 0; i < sql.length; i++) {
            // System.out.println("=============sql[" + i + "]==========" + sql[i]);
            // }
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                                 inParm);
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }
            company.resetModify();
            contractM.resetModify();
            contractD.resetModify();
            contractD.getPat().resetModify();
            messageBox("P0001");
            // =============add end
            return;
        }
        // ���ɾ����ͬ������Ϣ�����ͬϸ��ͬʱɾ��
        if ("CONTRACT".equalsIgnoreCase(tableName)) {
            int row = conTab.getSelectedRow();
            String contractCode = contractM.getItemString(row, "CONTRACT_CODE");
            // ================add by wanglong 20130314
            boolean coverFlg = false;
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                if (TypeTool.getBoolean(contractD.getItemData(i, "COVER_FLG"))) {
                    coverFlg = true;
                    break;
                }
            }
            if (coverFlg == true && this.messageBox("��ʾ", "�ú�ͬ������Ա�������Ƿ����ɾ��", 2) != 0) {// modify by
                                                                                        // wanglong
                                                                                        // 20130417
                return;
            }
            contractM.deleteRow(row);
            conTab.setDSValue();
            contractD.deleteContract(contractCode);
            mrTab.setDSValue();
            String[] sql = company.getUpdateSQL();
            sql = StringTool.copyArray(sql, contractM.getUpdateSQL());
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]>();
            // for (int i = 0; i < sql.length; i++) {
            // System.out.println("=============sql["+i+"]=========="+sql[i]);
            // }
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                                 inParm);
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }
            company.resetModify();
            contractM.resetModify();
            contractD.resetModify();
            contractD.getPat().resetModify();
            this.messageBox("P0001");
            onTabChanged();
            // =============add end
            return;
        }
        // ��ͬϸ����Ϣ����ĳ�н���ɾ��
        if ("MR".equalsIgnoreCase(tableName)) {
            int row = mrTab.getSelectedRow();
            if (TypeTool.getBoolean(contractD.getItemData(row, "COVER_FLG"))) {
                this.messageBox("����Ա�ѱ���������ɾ��");// add by wanglong 20130314
                return;
            }
            //=======================add by wanglong 20130922
            String mrSql =
                    "SELECT DISTINCT MR_NO FROM HRM_PATADM WHERE CONTRACT_CODE = '#' AND MR_NO = '#'";
            mrSql = mrSql.replaceFirst("#", contractD.getItemString(row, "CONTRACT_CODE"));
            mrSql = mrSql.replaceFirst("#", contractD.getItemString(row, "MR_NO"));
            TParm mrParm = new TParm(TJDODBTool.getInstance().select(mrSql));
            if (mrParm.getErrCode() != 0) {
                this.messageBox("��ѯ����ҽ��չ����Ϣ����");
                return;
            }
            if (mrParm.getCount() > 0) {
                this.messageBox("����Աҽ����չ��������ɾ��");
                return;
            }
            //=======================add end
            contractD.deleteRow(row);
            mrTab.setDSValue();
            setColorTable();
            // chenxi modify 20130329 ==========================================
            String[] sql = company.getUpdateSQL();
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]>();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                                 inParm);
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }
            company.resetModify();
            contractM.resetModify();
            contractD.resetModify();
            contractD.getPat().resetModify();
            // ============================//add by wanglong20130502
            TParm parm =
                    contractM.onQueryAmt(company.getItemString(comTab.getSelectedRow(),
                                                               "COMPANY_CODE"));
            int count = contractM.rowCount();
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < parm.getCount(); j++) {
                    if (contractM.getItemString(i, "CONTRACT_CODE")
                            .equals(parm.getValue("CONTRACT_CODE", j))) {
                        contractM.setItem(i, "SUBTOTAL", parm.getDouble("SUBTOTAL", j));
                        contractM.setItem(i, "TOT_AMT", parm.getDouble("TOT_AMT", j));
                    }
                }
            }
            parm =
                    contractM.updateAmt(company.getItemString(comTab.getSelectedRow(),
                                                              "COMPANY_CODE"));
            // ============================add end
            // this.messageBox("P0001");
            // chenxi modify 20130329 ===========================================
            // onPackage();//delete by wanglong 20130314
            // return;
        }
    }

    /**
     * ����
     */
    public void onSave() {
        comTab.acceptText();
        conTab.acceptText();
        mrTab.acceptText();
        int rowCount = contractD.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!contractD.isActive(i)) continue;
            String flg = contractD.getItemString(i, "FOREIGNER_FLG").trim();// �����ע��
            String idNo = contractD.getItemString(i, "IDNO").trim();// ���֤��
            String sexCode = contractD.getItemString(i, "SEX_CODE").trim();// �Ա�
            String packageCode = contractD.getItemString(i, "PACKAGE_CODE").trim();// �ײ�
            String birthday = contractD.getItemString(i, "BIRTHDAY");// ��������
            String patName = contractD.getItemString(i, "PAT_NAME").trim();// ����
            if (!flg.equals("Y") && idNo.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա���֤��Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У����֤��Ϊ�գ�");
                return;
            }
            if (sexCode.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա�Ա�Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У��Ա�Ϊ�գ�");
                return;
            }
            if (birthday == null || birthday.length() == 0) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա��������Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У���������Ϊ�գ�");
                return;
            }
            if (packageCode.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա�ײ�Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У��ײ�Ϊ�գ�");
                return;
            }
            if (patName.equals("")) {
                if (!idNo.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У����֤Ϊ " + idNo + " ����Ա����Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У���Ա����Ϊ�գ�");
                return;
            }
        }
        // ����
        Map<String, Integer> singleMrRow = new HashMap<String, Integer>();
        Map<String, Integer> singleMrSeq = new HashMap<String, Integer>();
        Map<String, Integer> singleIdRow = new HashMap<String, Integer>();
        Map<String, Integer> singleIdSeq = new HashMap<String, Integer>();
        for (int i = 0; i < rowCount; i++) {
            int size = singleMrRow.size();
            String mrNo = contractD.getItemString(i, "MR_NO");
            String flg = contractD.getItemString(i, "FOREIGNER_FLG");
            String idNo = contractD.getItemString(i, "IDNO");
            int seqNo = contractD.getItemInt(i, "SEQ_NO");
            if (!mrNo.equals("Y") && !mrNo.equals("")) {
                singleMrRow.put(mrNo, i);
                singleMrSeq.put(mrNo, seqNo);
                if ((size + 1) != singleMrRow.size()) {
                    if (!flg.equals("Y") && singleIdRow.containsKey(idNo)) {
                        this.messageBox("��" + (singleIdRow.get(idNo) + 1) + "��(��ţ�"
                                + singleIdSeq.get(idNo) + ")���" + (i + 1) + "��(��ţ�" + seqNo
                                + ")����Ա���֤����ͬ");
                    } else {
                        this.messageBox("��" + (singleMrRow.get(idNo) + 1) + "��(��ţ�"
                                + singleMrSeq.get(mrNo) + ")���" + (i + 1) + "��(��ţ�" + seqNo
                                + ")����Ա��������ͬ");
                    }
                    return;
                } else {
                    singleIdRow.put(idNo, i);
                    singleIdSeq.put(idNo, seqNo);
                }
            }
        }
        /** ����������Ϣ[��˾��ǩ]SQL */
        String[] sql = onSaveCom();
        if (sql == null) {
            sql = new String[]{};
        }
        // ȡ�ú�ͬ��ϸ�ౣ��SQL������֤
        String[] tempSql = onSaveContractM();
        if (tempSql == null) {
            tempSql = new String[]{};
        }
        sql = StringTool.copyArray(sql, tempSql);
        if (sql == null) {
            this.messageBox_("û������");
            return;
        }
        if (sql.length < 1) {
            this.messageBox_("û������");
            return;
        }
        // sql = StringTool.copyArray(sql, updateAdmSql);//modify by wanglong 20130308
        // ���涯����β���֤���
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                             inParm);
        if (result.getErrCode() != 0) {
            this.messageBox_(result.getErrText());
            this.messageBox("E0001");
            return;
        }
        company.resetModify();
        contractM.resetModify();
        contractD.resetModify();
        contractD.getPat().resetModify();
        // ============================//add by wanglong20130502
        TParm parm =
                contractM
                        .onQueryAmt(company.getItemString(comTab.getSelectedRow(), "COMPANY_CODE"));
        int count = contractM.rowCount();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < parm.getCount(); j++) {
                if (contractM.getItemString(i, "CONTRACT_CODE")
                        .equals(parm.getValue("CONTRACT_CODE", j))) {
                    contractM.setItem(i, "SUBTOTAL", parm.getDouble("SUBTOTAL", j));
                    contractM.setItem(i, "TOT_AMT", parm.getDouble("TOT_AMT", j));
                }
            }
        }
        parm = contractM.updateAmt(company.getItemString(comTab.getSelectedRow(), "COMPANY_CODE"));
        // ============================add end
        int seleRow = mrTab.getSelectedRow();
        int row = comTab.getSelectedRow(); // chenxi modify 20130329
        mrTab.setDSValue();
        comTab.setDSValue(); // chenxi modify 20130329
        if (row >= 0) comTab.setSelectedRow(row); // ==== chenxi modify 20130329
        if (seleRow >= 0) mrTab.setSelectedRow(seleRow);
        if (!isChangeContract) {
            this.messageBox("P0001");
        }
        // this.onQuery();
    }

    /**
     * ���TABLE
     */
    public void onSetDsValue() {
        comTab.setDSValue();
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * �����¼�(������Ϣ)[��˾��ǩ]
     */
    private String[] onSaveCom() {
        String[] sql = new String[]{};
        // System.out.println("selectrow========"+comTab.getSelectedRow());
        if (comTab.getSelectedRow() < 0) {
            String companyDesc = this.getValueString("COMPANY_DESC");
            if (StringUtil.isNullString(companyDesc)) {
                this.messageBox_("�������Ʋ���Ϊ��");
                return null;
            }
            if (company.isHaveSaveDesc(companyDesc) && !isChangeContract) {
                this.messageBox_("�������Ѿ�����");
                return null;
            }
            TParm parmTag =
                    this.getParmForTag("COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL");
            company.newCompany(parmTag);
        } else {
            int row = comTab.getSelectedRow();
            if (StringUtil.isNullString(this.getValueString("COMPANY_DESC"))) {
                this.messageBox_("�������Ʋ���Ϊ��");
                return null;
            }
            TParm parmTag =
                    this.getParmForTag("COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL");
            parmTag.setData("ROW", row);
            company.updateCompany(parmTag);
        }
        sql = company.getUpdateSQL();
        return sql;
    }

    /**
     * �����ͬ��Ϣ
     */
    public String[] onSaveContractM() {
        int[] newMRows = contractM.getNewRows();
        for (int row : newMRows) {// add by wanglong 20130302
            String contractDesc = contractM.getItemString(row, "CONTRACT_DESC");
            if (contractDesc.trim().equals("")) {
                contractM.setActive(row, false);
            } else {
                contractM.setActive(row, true);
            }
        }
        String[] sql = contractM.getUpdateSQL();
        int[] newDRows = contractD.getNewRows();
        for (int row : newDRows) {// add by wanglong 20130302
            String contractDesc = contractD.getItemString(row, "CONTRACT_DESC");
            if (contractDesc.trim().equals("")) {
                contractD.setActive(row, false);
            } else {
                contractD.setActive(row, true);
            }
        }
        sql = StringTool.copyArray(sql, contractD.getSql());
        sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
        // ===============add by wanglong 20130608 ����SYS_PatInfo������ͬ��
        ArrayList<String> sqlList = new ArrayList<String>();
        for (int row : newDRows) {
            if (contractD.isActive(row)) {
                String contractCode = contractD.getItemString(row, "CONTRACT_CODE");// add by wanglong 20130608
                String updateSYSPatInfo =//modify by wanglong 20130829
                        "UPDATE SYS_PATINFO A SET "
                                + "A.PAT_NAME = (SELECT B.PAT_NAME FROM HRM_CONTRACTD B WHERE B.CONTRACT_CODE = '#' AND B.MR_NO = A.MR_NO),"
                                + "A.SEX_CODE = (SELECT B.SEX_CODE FROM HRM_CONTRACTD B WHERE B.CONTRACT_CODE = '#' AND B.MR_NO = A.MR_NO),"
                                + "A.IDNO = (SELECT B.IDNO FROM HRM_CONTRACTD B WHERE B.CONTRACT_CODE = '#' AND B.MR_NO = A.MR_NO),"
                                + "OPT_USER='#',OPT_DATE=SYSDATE,OPT_TERM='#' "
                                + " WHERE MR_NO IN (SELECT MR_NO FROM HRM_CONTRACTD WHERE CONTRACT_CODE = '#')";
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", Operator.getID());
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", Operator.getIP());
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                if (!sqlList.contains(updateSYSPatInfo)) {
                    sqlList.add(updateSYSPatInfo);
                }
            }
        }
        int[] modifyDrows = contractD.getModifiedRows();
        for (int row : modifyDrows) {// add by wanglong 20130302
            if (contractD.isActive(row)) {
                String contractCode = contractD.getItemString(row, "CONTRACT_CODE");
                String updateSYSPatInfo =//modify by wanglong 20130829
                        "UPDATE SYS_PATINFO A SET "
                                + "A.PAT_NAME = (SELECT B.PAT_NAME FROM HRM_CONTRACTD B WHERE B.CONTRACT_CODE = '#' AND B.MR_NO = A.MR_NO),"
                                + "A.SEX_CODE = (SELECT B.SEX_CODE FROM HRM_CONTRACTD B WHERE B.CONTRACT_CODE = '#' AND B.MR_NO = A.MR_NO),"
                                + "A.IDNO = (SELECT B.IDNO FROM HRM_CONTRACTD B WHERE B.CONTRACT_CODE = '#' AND B.MR_NO = A.MR_NO),"
                                + "OPT_USER='#',OPT_DATE=SYSDATE,OPT_TERM='#' "
                                + " WHERE MR_NO IN (SELECT MR_NO FROM HRM_CONTRACTD WHERE CONTRACT_CODE = '#')";
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", Operator.getID());
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", Operator.getIP());
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("#", contractCode);
                if (!sqlList.contains(updateSYSPatInfo)) {
                    sqlList.add(updateSYSPatInfo);
                }
            }
        }
        sql = StringTool.copyArray(sql, (String[]) sqlList.toArray(new String[0]));
        // ===============add end
        if (sql == null || sql.length < 1) {
            return null;
        }
        return sql;
    }

    /**
     * ����¼�
     */
    public void onClear() {
        company.onQuery();
        comTab.setDSValue();
        comTab.clearSelection();
        contractM.filt("#");
        conTab.setDSValue();
        conTab.clearSelection();
        contractD.filt("#");
        mrTab.setDSValue();
        mrTab.clearSelection();
        this.clearValue("COMPANY_CODE;COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL;COUNT;QUERY_CONTRACT;TOT_AMT;CONTRACT_DESC;COMPANY_DESC;PAT_NAME;MR_NO;PACKAGE_CODE");
    }

    /**
     * ҳǩ����¼�,���û��ѡ��ĳ���壬������ʾ��ͬ��ϸ����Ϣ.
     */
    public void onTabChanged() {
        TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("TAB_PANEL");
        if (tabbedPane.getSelectedIndex() == 0) {
            return;
        }
        if (comTab.getSelectedRow() < 0) {
            this.messageBox_("û��������Ϣ");
            tabbedPane.setSelectedIndex(0);
            return;
        }
        int row = comTab.getSelectedRow();
        String comCode = company.getItemString(row, "COMPANY_CODE");
        if (StringUtil.isNullString(comCode)) {
            this.messageBox_("ȡ��������Ϣʧ��");
            return;
        }
        contractM.setCompanyDesc(company.getItemString(row, "COMPANY_DESC"));
        contractM.filt(comCode);
        if (!StringUtil.isNullString(contractM.getItemString(contractM.rowCount() - 1,
                                                             "CONTRACT_DESC"))
                || contractM.rowCount() < 1) {
            contractM.insertRow(comCode);
        }
        // ============================//add by wanglong20130502
        TParm result = contractM.onQueryAmt(comCode);
        int count = contractM.rowCount();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < result.getCount(); j++) {
                if (contractM.getItemString(i, "CONTRACT_CODE")
                        .equals(result.getValue("CONTRACT_CODE", j))) {
                    contractM.setItem(i, "SUBTOTAL", result.getDouble("SUBTOTAL", j));
                    contractM.setItem(i, "TOT_AMT", result.getDouble("TOT_AMT", j));
                }
            }
        }
        result = contractM.updateAmt(comCode);
        // ============================add end
        conTab.setDSValue();
    }

    /**
     * ͨ��excel����Ա����Ϣ
     * excel��һ��Ϊ��ͷ��Ӧ��������ţ����������֤�ţ��Ա��ײʹ��룬���ע�ǣ��������ڣ����ţ��������壬�绰���ʱ࣬��ַ��Ԥ��ʱ�䣬����״̬
     * ���е�˳��ɱ�
     * ������Ϣ�������excel�ĵ�һ��sheetҳ��
     */
    public void onInsertPatByExl() {// refactor by wanglong 20130116
        conTab.acceptText();
        if (conTab == null) {
            this.messageBox_("û�к�ͬ��Ϣ");
            return;
        }
        int clickRow = conTab.getSelectedRow();
        if (clickRow < 0) {
            this.messageBox_("û�к�ͬ��Ϣ");
            return;
        }
        if (conTab.getItemString(clickRow, "CONTRACT_DESC").trim().equals("")) {
            this.messageBox_("û��ѡ���ͬ");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {// ����xls�ļ� add by wanglong20130116

                    public boolean accept(File f) {
                        if (f.isDirectory()) {// �����ļ���
                            return true;
                        }
                        return f.getName().endsWith(".xls");
                    }

                    public String getDescription() {
                        return ".xls";
                    }
                });
        int option = fileChooser.showOpenDialog(null);
        TParm parm = new TParm();
        String discnt = conTab.getItemString(conTab.getSelectedRow(), "DISCNT");// ��ͬ�ۿ���
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Workbook wb = Workbook.getWorkbook(file);
                Sheet st = wb.getSheet(0);
                int row = st.getRows();
                int column = st.getColumns();
                if (row <= 1 || column <= 0) {
                    this.messageBox("excel��û������");
                    return;
                }
                StringBuffer wrongMsg = new StringBuffer();
                // wrongMsg.append("excel�ļ���Ϊ��" + file.getName() + "\r\n");
                String[] title = new String[column];
                for (int j = 0; j < column; j++) {
                    String cell = st.getCell(j, 0).getContents();
                    if (cell.indexOf("�ײʹ���") != -1) {
                        title[j] = "PACKAGE_CODE";
                        continue;
                    }
                    if (cell.indexOf("���") != -1) {
                        title[j] = "SEQ_NO";
                        continue;
                    }
                    if (cell.indexOf("����") != -1) {
                        title[j] = "STAFF_NO";
                        continue;
                    }
                    if (cell.indexOf("����") != -1) {
                        title[j] = "PAT_NAME";
                        continue;
                    }
                    if (cell.indexOf("�Ա�") != -1) {
                        title[j] = "SEX_CODE";
                        continue;
                    }
                    if (cell.indexOf("���֤��") != -1) {
                        title[j] = "IDNO";
                        continue;
                    }
                    if (cell.indexOf("����״̬") != -1) {
                        title[j] = "MARRIAGE_CODE";
                        continue;
                    }
                    if (cell.indexOf("�����ע��") != -1) {
                        title[j] = "FOREIGNER_FLG";
                        continue;
                    }
                    if (cell.indexOf("��������") != -1) {
                        title[j] = "BIRTHDAY";
                        continue;
                    }
                    if (cell.indexOf("��������֧��") != -1) {
                        title[j] = "COMPANY_PAY_FLG";
                        continue;
                    }
                    if (cell.indexOf("�绰") != -1) {
                        title[j] = "TEL";
                        continue;
                    }
                    if (cell.indexOf("�ʱ�") != -1) {
                        title[j] = "POST_CODE";
                        continue;
                    }
                    if (cell.indexOf("��ַ") != -1) {
                        title[j] = "ADDRESS";
                        continue;
                    }
                    if (cell.indexOf("Ԥ��ʱ��") != -1) {
                        title[j] = "PRE_CHK_DATE";
                        continue;
                    }
                    if (cell.indexOf("����") != -1) {// add by wanglong 20130225
                        title[j] = "PAT_DEPT";
                        continue;
                    }
                }
                List<String> titleList = Arrays.asList(title);
                if (!titleList.contains("PACKAGE_CODE")) {// �ײʹ���
                    this.messageBox("ȱ�١��ײʹ��롱�У�����Ϊ�����");
                    return;
                }
                if (!titleList.contains("SEQ_NO")) {// ���
                    this.messageBox("ȱ�١���š��У����Ǳ����");
                    return;
                }
                if (!titleList.contains("STAFF_NO")) {// ����
                    this.messageBox("ȱ�١����š��У����Ǳ����");
                    return;
                }
                if (!titleList.contains("PAT_NAME")) {// ����
                    this.messageBox("ȱ�١��������У�����Ϊ�����");
                    return;
                }
                if (!titleList.contains("SEX_CODE")) {// �Ա�
                    this.messageBox("ȱ�١��Ա��У�����Чֵֻ����д�Ա�ı��룬����������1��\"��\"����");
                    return;
                }
                if (!titleList.contains("IDNO")) {// ���֤��
                    this.messageBox("ȱ�١����֤�š��У���Ĭ�ϱ�����ֵ��������ڡ������ע�ǡ���дΪY֮�󣬴���ſɿ�ȱ��");
                    return;
                }
                if (!titleList.contains("MARRIAGE_CODE")) {// ����״̬
                    this.messageBox("ȱ�١�����״̬���У�����Чֵֻ����д�����ı��룬���߲���д��");
                    return;
                }
                if (!titleList.contains("FOREIGNER_FLG")) {// �����ע��
                    this.messageBox("ȱ�١������ע�ǡ��У�����Чֵֻ����дY��N�����߲���д������N����");
                    return;
                }
                if (!titleList.contains("BIRTHDAY")) {// ��������
                    this.messageBox("ȱ�١��������ڡ��У�����Чֵ����ʽΪyyyyMMdd����������ˣ�������д��");
                    return;
                }
                if (!titleList.contains("COMPANY_PAY_FLG")) {// ��������֧��
                    this.messageBox("ȱ�١���������֧�����У�����Чֵֻ����дY����N�����߲���д������N����");
                    return;
                }
                if (!titleList.contains("TEL")) {// �绰
                    this.messageBox("ȱ�١��绰���У������Ǳ����");
                    return;
                }
                if (!titleList.contains("POST_CODE")) {// �ʱ�
                    this.messageBox("ȱ�١��ʱࡱ�У������Ǳ����");
                    return;
                }
                if (!titleList.contains("ADDRESS")) {// ��ַ
                    this.messageBox("ȱ�١���ַ���У������Ǳ����");
                    return;
                }
                if (!titleList.contains("PRE_CHK_DATE")) {// Ԥ��ʱ��
                    this.messageBox("ȱ�١�Ԥ��ʱ�䡱�У���Чֵ����ʽΪyyyyMMdd�����߲���д��");
                    return;
                }
                if (!titleList.contains("PAT_DEPT")) {// ���� add by wanglong 20130225
                    this.messageBox("ȱ�١����š��У������Ǳ����");
                    return;
                }
                String sexSql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX'";
                TParm sexParm = new TParm(TJDODBTool.getInstance().select(sexSql));
                if (sexParm.getErrCode() != 0 || sexParm.getCount() < 1) {
                    this.messageBox("��ȡϵͳ���Ա���Ϣʱ����");
                    return;
                }
                List<String> sexList = Arrays.asList(sexParm.getStringArray("ID"));
                String packageSql = "SELECT PACKAGE_CODE FROM HRM_PACKAGEM WHERE ACTIVE_FLG = 'Y'";
                TParm packageParm = new TParm(TJDODBTool.getInstance().select(packageSql));
                if (packageParm.getErrCode() != 0 || packageParm.getCount() < 1) {
                    this.messageBox("��ȡϵͳ���ײ���Ϣʱ����");
                    return;
                }
                List<String> packageList = Arrays.asList(packageParm.getStringArray("PACKAGE_CODE"));
                String marriageSql =
                        "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_MARRIAGE'";
                TParm marriageParm = new TParm(TJDODBTool.getInstance().select(marriageSql));
                if (marriageParm.getErrCode() != 0 || marriageParm.getCount() < 1) {
                    this.messageBox("��ȡϵͳ�л���״̬��Ϣʱ����");
                    return;
                }
                List<String> marriageList = Arrays.asList(marriageParm.getStringArray("ID"));
                int count = 0;
                for (int i = 1; i < row; i++) {// һ��һ�м���excel�е�����
                    for (int j = 0; j < column; j++) {// ÿ�ε���һ�е�������
                        String cell = st.getCell(j, i).getContents();
                        parm.addData(title[j], cell.trim());
                    }
                    parm.addData("DISCNT", discnt);// �ۿ�
                    count = parm.getCount("PAT_NAME");
                    parm.setCount(count);
                    // ===================���ݼ�鿪ʼ
                    String packageCode = parm.getValue("PACKAGE_CODE", count - 1);// �ײʹ���
                    if (packageCode.equals("")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,���ײʹ��롱��ȱ��ֵ,�����ᱻ����\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,���ײʹ��롱��ȱ��ֵ�������ᱻ����");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    if (!packageList.contains(packageCode)) {
                        wrongMsg.append("excel��" + (i + 1) + "��,���ײʹ��롱�е�ֵ(ֵ��" + packageCode
                                + ")��ϵͳ�в�����,���ᱻ����\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,���ײʹ��롱�е�ֵ��ϵͳ�в����ڣ������ᱻ����");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    String patName = parm.getValue("PAT_NAME", count - 1);// ����
                    if (patName.equals("")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,����������ȱ��ֵ,���ᱻ����\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,����������ȱ��ֵ�������ᱻ����");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    String idNo = parm.getValue("IDNO", count - 1);
                    String sexCode = parm.getValue("SEX_CODE", count - 1);
                    String birthday = parm.getValue("BIRTHDAY", count - 1);
                    String foreignerFlg = parm.getValue("FOREIGNER_FLG", count - 1);
                    // �й��ˣ�18λ���֤У��λ������ʱ���Զ�������
                    String checkid = checkID(idNo);
                    if (!foreignerFlg.equalsIgnoreCase("Y") && !checkid.equals("TRUE")
                            && !checkid.equals("FALSE")) {// add by wanglong 20130409
                        wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                + "������Ա�������֤��У��λ����(ֵ��" + idNo + "),��ȷֵ:"
                                + checkid.substring(17, 18) + "\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                + "������Ա�������֤��У��λ���󣡣���ȷֵ��" + checkid.substring(17, 18) + "��");// modify by wanglong 20130416
                        parm.setData("IDNO", count - 1, idNo);// modify by wanglong 20130416
                        String idSexCode = StringTool.isMaleFromID(checkid);// add by wanglong
                                                                            // 20130726
                        if (!idSexCode.equals(sexCode)) {// add by wanglong 20130726
                            if (this.messageBox("����", "excel��" + (i + 1) + "��,����Ϊ��" + patName
                                    + "������Ա�Ա��������֤�ϵ��Ա𲻷�\n�Ƿ��Ա�ĳɺ����֤��һ��", 2) == 0) {
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա��������֤�ϵ��Ա𲻷�\r\n");
                            } else if (!sexCode.matches("[12349]")) {// add by wanglong 20130830
                                this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա���ֵ�����벻Ҫֱ��ʹ�����ĵȷǷ��ַ���Ϊ��ʶ����ʱ������Ϊ�����֤һ��");
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա�ʹ���˷Ƿ��ַ���Ϊ��ʶ\r\n");
                            }
                        }
                        parm.setData("BIRTHDAY", count - 1, StringTool.getBirdayFromID(checkid));
                    }
                    // �й��ˣ����֤��ȫ����
                    else if (!foreignerFlg.equalsIgnoreCase("Y") && checkid.equals("FALSE")) {// modify by wanglong 20130409
                        wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա���֤�Ŵ���(ֵ��"
                                + idNo + "),������(��)�������\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                + "������Ա���֤�Ŵ��󣡻�����(��)�������");
                        TParm temp = new TParm();
                        temp.setData("PAT_NAME", patName);
                        temp.setData("STAFF_NO", parm.getValue("STAFF_NO", count - 1));
                        temp.setData("IDNO", idNo);
                        temp.setData("FOREIGNER_FLG", foreignerFlg.toUpperCase());
                        temp.setData("SEX_CODE", sexCode);
                        temp.setData("BIRTHDAY", birthday);
                        TParm reParm = null;
                        while (reParm == null) {
                            reParm =
                                    (TParm) this.openDialog("%ROOT%\\config\\hrm\\HRMIDCheckUI.x",
                                                            temp);// ���֤��д
                        }
                        parm.setData("IDNO", count - 1, reParm.getData("IDNO"));
                        parm.setData("FOREIGNER_FLG", count - 1, reParm.getData("FOREIGNER_FLG"));
                        parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                        parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                    }
                    // �й��ˣ������֤��ȷ��
                    else if (!foreignerFlg.equalsIgnoreCase("Y") && isId(idNo)) {
                        parm.setData("FOREIGNER_FLG", count - 1, "N");
                        String idSexCode = StringTool.isMaleFromID(idNo);// add by wanglong 20130726
                        if (!idSexCode.equals(sexCode)) {// add by wanglong 20130726
                            if (this.messageBox("����", "excel��" + (i + 1) + "��,����Ϊ��" + patName
                                    + "������Ա�Ա��������֤�ϵ��Ա𲻷�\n�Ƿ��Ա�ĳɺ����֤��һ��", 2) == 0) {
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա��������֤�ϵ��Ա𲻷�\r\n");
                            } else if (!sexCode.matches("[12349]")) {//add by wanglong 20130830
                                this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                                + "������Ա�Ա���ֵ�����벻Ҫֱ��ʹ�����ĵȷǷ��ַ���Ϊ��ʶ����ʱ������Ϊ�����֤һ��");
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա�ʹ���˷Ƿ��ַ���Ϊ��ʶ\r\n");
                            }
                        }
                        parm.setData("BIRTHDAY", count - 1, StringTool.getBirdayFromID(idNo));// ���������ڸ�Ϊ�����֤�ϵ���Ϣһ��
                    }
                    // �����
                    else if (foreignerFlg.equalsIgnoreCase("Y")) {
                        parm.setData("FOREIGNER_FLG", count - 1, "Y");
                        if (!sexList.contains(sexCode)) {
                            wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա�Ա����(ֵ��"
                                    + sexCode + ")\r\n");
                            this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա�Ա����");
                            TParm temp = new TParm();
                            temp.setData("PAT_NAME", patName);
                            temp.setData("STAFF_NO", parm.getValue("STAFF_NO", count - 1));
                            temp.setData("IDNO", idNo);
                            temp.setData("FOREIGNER_FLG", foreignerFlg.toUpperCase());
                            // temp.setData("SEX_CODE", sexCode);
                            temp.setData("BIRTHDAY", birthday);
                            TParm reParm = null;
                            while (reParm == null) {
                                reParm =
                                        (TParm) this
                                                .openDialog("%ROOT%\\config\\hrm\\HRMIDCheckUI.x",
                                                            temp);// ���֤��д
                            }
                            parm.setData("IDNO", count - 1, reParm.getData("IDNO"));
                            parm.setData("FOREIGNER_FLG", count - 1,
                                         reParm.getData("FOREIGNER_FLG"));
                            parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                            parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                        }
                        birthday = parm.getValue("BIRTHDAY", count - 1);
                        if (!(parm.getData("BIRTHDAY", count - 1) instanceof Timestamp)
                                && !DateTool.checkDate(birthday, "yyyyMMdd")) {
                            wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                    + "������Ա�������ڴ���(ֵ��" + birthday + ")\r\n");
                            this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա�������ڴ���");
                            TParm temp = new TParm();
                            temp.setData("PAT_NAME", patName);
                            temp.setData("STAFF_NO", parm.getValue("STAFF_NO", count - 1));
                            temp.setData("IDNO", idNo);
                            temp.setData("FOREIGNER_FLG", foreignerFlg.toUpperCase());
                            temp.setData("SEX_CODE", sexCode);
                            // temp.setData("BIRTHDAY", birthday);
                            TParm reParm = null;
                            while (reParm == null) {
                                reParm =
                                        (TParm) this
                                                .openDialog("%ROOT%\\config\\hrm\\HRMIDCheckUI.x",
                                                            temp);// ���֤��д
                            }
                            parm.setData("IDNO", count - 1, reParm.getData("IDNO"));
                            parm.setData("FOREIGNER_FLG", count - 1,
                                         reParm.getData("FOREIGNER_FLG"));
                            parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                            parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                        }
                    }
                    if (parm.getValue("COMPANY_PAY_FLG", count - 1).equalsIgnoreCase("y")) {// ��������֧��
                        parm.setData("COMPANY_PAY_FLG", count - 1, "Y");
                    } else if (!parm.getValue("COMPANY_PAY_FLG", count - 1).equalsIgnoreCase("Y")) {
                        parm.setData("COMPANY_PAY_FLG", count - 1, "N");
                    }
                    String preChkDate = parm.getValue("PRE_CHK_DATE", count - 1);// Ԥ��ʱ��
                    if (preChkDate.equals("")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱��ȱ��ֵ,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱��ȱ��ֵ��������Ϊ��");
                        parm.setData("PRE_CHK_DATE", count - 1, "");
                    } else if (!DateTool.checkDate(preChkDate, "yyyyMMdd")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱�е�ֵ(ֵ��" + preChkDate
                                + ")����,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱�е�ֵ���󣡽�����Ϊ��");
                        parm.setData("PRE_CHK_DATE", count - 1, "");
                    }
                    String marriageCode = parm.getValue("MARRIAGE_CODE", count - 1);// ����״̬
                    if (marriageCode.equals("")) {// add by wanglong 20130502
                        wrongMsg.append("excel��" + (i + 1) + "��,������״̬����ȱ��ֵ,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,������״̬���е�ֵ��ϵͳ�в����ڣ�������Ϊ��");
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    } else if (!marriageList.contains(marriageCode)) {
                        wrongMsg.append("excel��" + (i + 1) + "��,������״̬���е�ֵ(ֵ��" + marriageCode
                                + ")��ϵͳ�в�����,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,������״̬���е�ֵ��ϵͳ�в����ڣ�������Ϊ��");
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    }
                    // ===================���ݼ�����
                }
                String msg = wrongMsg.toString();
                if (!StringUtil.isNullString(msg)) {// modify by wanglong 20130502
                // String dir = TConfig.getSystemValue("UDD_DISBATCH_LocalPath") + "\\";
                    String fileName = "��������������־��" + file.getName() + "��" + ".txt";
                    // this.messageBox_("��Ա��Ϣ��������,������Ϣ���"+dir+fileName);
                    javax.swing.filechooser.FileSystemView fsv =
                            javax.swing.filechooser.FileSystemView.getFileSystemView();
                    FileTool.setString(fsv.getHomeDirectory() + "\\" + fileName, msg);
                    // FileTool.setString(dir + fileName, msg);
                }
                parm.setCount(count);
                if (count < 1) {
                    this.messageBox_("��Ч��������һ�У����������ֹ");
                    return;
                }
                // setColorTable();
            }
            catch (BiffException e) {
                this.messageBox_("excel�ļ���������");
                e.printStackTrace();
                return;
            }
            catch (IOException e) {
                this.messageBox_("���ļ�����");
                e.printStackTrace();
                return;
            }
        } else return;
        String companyCode = contractM.getItemString(conTab.getSelectedRow(), "COMPANY_CODE");
        String contractCode = contractM.getItemString(conTab.getSelectedRow(), "CONTRACT_CODE");
        String contractDesc = contractM.getItemString(conTab.getSelectedRow(), "CONTRACT_DESC");
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)
                || StringUtil.isNullString(contractDesc)) {
            return;
        }
        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        Timestamp optDate = SystemTool.getInstance().getDate();
        int row = contractD.rowCount() - 1;
        for (int i = 0; i < parm.getCount(); i++) {
            TParm parmRow = parm.getRow(i);
            if (!contractD.getItemString(row, "MR_NO").equals("")) {// add by wanglong 20130225
                row = contractD.insertRow();
            }
            int seqNo = parmRow.getInt("SEQ_NO");
            String patName = parmRow.getValue("PAT_NAME");
            String idNo = parmRow.getValue("IDNO");
            String patSql =
                    "SELECT SYS_PATINFO.*,OPT_DATE AS REPORT_DATE FROM SYS_PATINFO WHERE IDNO='"
                            + idNo + "' ORDER BY OPT_DATE DESC";// �������֤�Ų鲡����
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                // System.out.println("=======================patParm============" + patParm);
                this.messageBox("��ѯ��Ա������ʱ����");
                return;
            }
            // ��һ�ξ���
            if (patParm.getCount("MR_NO") <= 0) {
                String birthDay = "";
                if (parmRow.getData("BIRTHDAY") instanceof Timestamp) {
                    birthDay = StringTool.getString(parmRow.getTimestamp("BIRTHDAY"), "yyyyMMdd");
                } else {
                    birthDay = parmRow.getValue("BIRTHDAY");
                }
                String samePatSql =
                        "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS "
                                + " FROM SYS_PATINFO WHERE PAT_NAME = '#' # "
                                + "ORDER BY OPT_DATE DESC NULLS LAST";// ��ͬ��ͬ��
                samePatSql = samePatSql.replaceFirst("#", patName);
                if (birthDay.equals("")) {
                    contractD.setItem(row, "MR_NO", "Y");
                } else {
                    samePatSql =
                            samePatSql.replaceFirst("#",
                                                    " AND TO_CHAR( BIRTH_DATE, 'yyyymmdd') = '"
                                                            + birthDay + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getErrCode() != 0) {
                        this.messageBox("��ѯ��Ա���ξ�����Ϣʱ����");
                        return;
                    }
                    if (result.getCount() < 1) {
                        contractD.setItem(row, "MR_NO", "Y");
                    } else if (result.getCount() == 1) {// add by wanglong 20130409
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                                + ")ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {// add by wanglong 20130726
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm samePatParm = (TParm) obj;
                                contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                                if (!isId(idNo)) {// 20130409
                                    String patIdNo = samePatParm.getValue("IDNO");
                                    if (patIdNo.length() == 18 && isId(patIdNo)) {
                                        idNo = patIdNo;
                                    }
                                }
                                String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                                if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                    this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                            + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                            + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                                }
//                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", 0));
                            } else {
                                contractD.setItem(row, "MR_NO", "Y");
                            }
                        } else contractD.setItem(row, "MR_NO", "Y");
                    } else {
                        int j = 0;
                        for (; j < result.getCount(); j++) {// add by wanglong 20130409
                            String patIdNo = result.getValue("IDNO", j).trim();
                            if (isId(patIdNo) && uptoeighteen(patIdNo).equals(idNo)) {
                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", j));
                                break;
                            }
                        }
                        if (j == result.getCount()) {
                            if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                                    + ")ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                                Object obj =
                                        openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                                if (obj != null) {
                                    TParm samePatParm = (TParm) obj;
                                    contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                                    if (!isId(idNo)) {// 20130409
                                        String patIdNo = samePatParm.getValue("IDNO");
                                        if (patIdNo.length() == 18 && isId(patIdNo)) {
                                            idNo = patIdNo;
                                        }
                                    }
                                    String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                                    if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                        this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                                + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                                + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                                    }
                                } else {
                                    contractD.setItem(row, "MR_NO", "Y");
                                }
                            } else contractD.setItem(row, "MR_NO", "Y");
                        }
                    }
                }
            }
            // ���ھ�����Ϣ
            else if (patParm.getCount("MR_NO") == 1) {
                if (!patParm.getValue("PAT_NAME", 0).equals(patName)) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                            + ")���֤��ͬ������������ͬ�Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                        + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                        + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                            }
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                } else contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", 0));
            }
            // ���ڶ���������Ϣ
            else if (patParm.getCount("MR_NO") > 1) {
                int j = 0;
                for (; j < patParm.getCount("MR_NO"); j++) {
                    if (patParm.getValue("PAT_NAME", j).equals(patName)) {
                        contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", j));
                        break;
                    }
                }
                if (j == patParm.getCount("MR_NO")) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                            + ")���֤��ͬ������������ͬ�Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                        + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                        + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                            }
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                }
            }
            contractD.setItem(row, "IDNO", idNo);
            contractD.setItem(row, "STAFF_NO", parmRow.getValue("STAFF_NO"));
            contractD.setItem(row, "PAT_NAME", patName);
            contractD.setItem(row, "PY1", SystemTool.getInstance().charToCode(patName));
            contractD.setItem(row, "COMPANY_CODE", companyCode);
            contractD.setItem(row, "CONTRACT_CODE", contractCode);
            contractD.setItem(row, "CONTRACT_DESC", contractDesc);
            contractD.setItem(row, "PACKAGE_CODE", parmRow.getValue("PACKAGE_CODE"));
            contractD.setItem(row, "SEX_CODE", parmRow.getValue("SEX_CODE"));
            Timestamp birthday = SystemTool.getInstance().getDate();
            if (!(parmRow.getData("BIRTHDAY") instanceof Timestamp)) {
                birthday = StringTool.getTimestamp(parmRow.getValue("BIRTHDAY"), "yyyyMMdd");
            } else birthday = parmRow.getTimestamp("BIRTHDAY");
            contractD.setItem(row, "BIRTHDAY", birthday);
            // �ۿ� modify by wanglong 20130116
            contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));
            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
            contractD.setItem(row, "SEQ_NO", seqNo);
            // ����״̬ add by wanglong 20130116
            contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE"));
            // ���� add by wanglong 20130225
            contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT"));
            if (parmRow.getValue("PRE_CHK_DATE").equals("")) {
                contractD.setItem(row, "PRE_CHK_DATE", optDate);
            } else {
                Timestamp preChkDate =
                        StringTool.getTimestamp(parmRow.getValue("PRE_CHK_DATE"), "yyyyMMdd");
                contractD.setItem(row, "PRE_CHK_DATE", preChkDate);
            }
            contractD.setItem(row, "FOREIGNER_FLG", parmRow.getValue("FOREIGNER_FLG"));
            contractD.setItem(row, "COMPANY_PAY_FLG", parmRow.getValue("COMPANY_PAY_FLG"));
            contractD.setItem(row, "OPT_USER", optUser);
            contractD.setItem(row, "OPT_TERM", optTerm);
            contractD.setItem(row, "OPT_DATE", optDate);
            contractD.setActive(row, true);
        }
        int newRow = contractD.insertRow();
        contractD.setItem(newRow, "COMPANY_CODE", companyCode);
        contractD.setItem(newRow, "CONTRACT_CODE", contractCode);
        contractD.setItem(newRow, "CONTRACT_DESC", contractDesc);
        contractD.setItem(newRow, "PRE_CHK_DATE", optDate);
        if (!StringUtil.isNullString(contractD.packageCode)) {
            contractD.setItem(newRow, "PACKAGE_CODE", contractD.packageCode);
        } else contractD.setItem(newRow, "PACKAGE_CODE", "");
        // contractD.setItem(row, "MR_NO", "Y");
        contractD.setItem(newRow, "OPT_USER", optUser);
        contractD.setItem(newRow, "OPT_TERM", optTerm);
        contractD.setItem(newRow, "DISCNT", discnt);
        contractD.setItem(newRow, "OPT_DATE", optDate);
        contractD.setItem(newRow, "SEQ_NO",
                          contractD.getItemInt(contractD.rowCount() - 2, "SEQ_NO") + 1);
        contractD.setActive(newRow, false);
        mrTab.setDSValue();
        this.messageBox_("����ɹ�");
    }

    public static void main(String[] args) {
        String id = "340104650406354";
        // System.out.println(""+StringTool.getBirdayFromID(id));
    }

    /**
     * ����TABLE��ɫ
     */
    public void setColorTable() {
        /**
         * ��ɫ
         */
        Color antibioticColor = new Color(255, 0, 0);
        /**
         * ��ͨ��ɫ
         */
        Color normalColor = new Color(0, 0, 0);
        int count = mrTab.getRowCount();
        TParm tableParm = contractD.getBuffer(contractD.PRIMARY);
        // System.out.println("����"+count);
        for (int i = 0; i < count; i++) {
            TParm temp = tableParm.getRow(i);
            if ("N".equals(temp.getValue("MR_NO"))) {
                mrTab.setRowTextColor(i, antibioticColor);
            } else {
                mrTab.setRowTextColor(i, normalColor);
            }
        }
    }

    /**
     * �һ�MENU�����¼�
     * 
     * @param tableName
     */
    public void showPopMenu() {
        TParm action = mrTab.getDataStore().getRowParm(mrTab.getSelectedRow());
        if ("N".equals(action.getValue("MR_NO"))) {
            mrTab.setPopupMenuSyntax("ѡ�񲡰���,openRigthPopMenu");
            return;
        } else {
            mrTab.setPopupMenuSyntax("");
            return;
        }
    }

    /**
     * �Ҽ���ѯ
     */
    public void openRigthPopMenu() {
        TParm action = mrTab.getDataStore().getRowParm(mrTab.getSelectedRow());
        TParm reParm =
                (TParm) this.openDialog("%ROOT%\\config\\hrm\\HRMQueryMrNoUI.x",
                                        action.getValue("IDNO"));
        if (reParm == null) {
            mrTab.getDataStore().setItem(mrTab.getSelectedRow(), "MR_NO", "Y");
            mrTab.setDSValue();
        } else {
            mrTab.getDataStore().setItem(mrTab.getSelectedRow(), "MR_NO", reParm.getData("MR_NO"));
            mrTab.setDSValue();
        }
        setColorTable();
    }

    /**
     * �ײ͸ı��¼�,��ѡ�е��ײʹ�������ͬϸ����Ϣ
     */
    public void onPackage() {
        String packageCode = this.getValueString("PACKAGE_CODE");
        if (StringUtil.isNullString(packageCode)) {
            return;
        }
        contractD.packageCode = packageCode;
        int count = contractD.rowCount();
        String contractCode = contractD.getItemString(count - 1, "CONTRACT_CODE");
        String mrList = "";
        String mrSql =
                "SELECT DISTINCT MR_NO FROM HRM_PATADM WHERE CONTRACT_CODE = '" + contractCode
                        + "' AND MR_NO IN (#)";// add by wanglong 201303014
        for (int i = 0; i < count; i++) {
            mrList += "'" + contractD.getItemString(i, "MR_NO") + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        mrSql = mrSql.replaceFirst("#", mrList);
        TParm mrParm = new TParm(TJDODBTool.getInstance().select(mrSql));
        if (mrParm.getErrCode() != 0) {
            this.messageBox("��ѯ����ҽ��չ����Ϣ����");
            return;
        }
        for (int i = 0; i < count; i++) {
            // add by wanglong 20130314
            if (TypeTool.getBoolean(contractD.getItemData(i, "COVER_FLG"))) {
                this.messageBox(contractD.getItemString(i, "PAT_NAME") + "�ѱ��������ܸ����ײ�");
                continue;
            }
            String mrNo = contractD.getItemString(i, "MR_NO");
            for (int j = 0; j < mrParm.getCount(); j++) {
                if (mrParm.getValue("MR_NO", j).equals(mrNo)) {
                    this.messageBox(contractD.getItemString(i, "PAT_NAME") + "ҽ����չ�������ܸ����ײ�");
                    continue;
                }
            }
            contractD.setItem(i, "PACKAGE_CODE", packageCode);
        }
        double amt = contractD.getContractAmt();
        // this.messageBox_(amt);
        contractM.setItem(conDescRow, "SUBTOTAL", amt);
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * ���ݲ����Ų�ѯ��ͬϸ��
     */
    public void onMrNo() {}

    /**
     * �����ʱ���ҵ�ַ
     */
    public void onPostCheck() {
        String sql =
                "SELECT STATE||CITY ADDRESS FROM SYS_POSTCODE WHERE POST_CODE='"
                        + this.getValueString("POST_CODE") + "'";
        // System.out.println("sql==="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String add = result.getValue("ADDRESS", 0);
        this.setValue("ADDRESS", add);
    }

    /**
     * ����ʼ���ַ�Ƿ���Ч
     */
    public void onCheckEmail() {
        String email = this.getValueString("E_MAIL");
        if (!StringTool.isEmail(email)) {
            this.messageBox_("��ַ��Ч");
            this.callFunction("UI|E_MAIL|grabFocus");
            return;
        }
        this.callFunction("UI|CONTACTS_NAME|grabFocus");
    }

    /**
     * ��ѯ�¼�
     */
    public void onQuery() {
        String companyDesc = this.getValueString("COMPANY_DESC");
        if (StringUtil.isNullString(companyDesc)) {
            this.messageBox_("������ϢΪ��");
            return;
        }
        // caowl 20130308 start �޸�ģ����ѯʱֻ��ѯ��һ����Ϣ������
        TParm companyParm = company.queryCodeByName(companyDesc);
        String str = "COMPANY_CODE =";
        if (companyParm.getCount() <= 0) {
            this.messageBox_("������Ϣ");
            return;
        }
        for (int i = 0; i < companyParm.getCount(); i++) {
            if (i == 0) {
                str += "'" + companyParm.getValue("COMPANY_CODE", i) + "' ";
            } else {
                str += "OR COMPANY_CODE = '" + companyParm.getValue("COMPANY_CODE", i) + "'";
            }
        }
        company.setFilter(str);
        // caowl 20130308 end
        company.filter();
        comTab.setDSValue();
    }

    /**
     * ��ҩ������ҩƷ�����õ�checkBox�¼�
     * 
     * @param obj
     * @return
     */
    public boolean onCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        table.setDSValue();
        return false;
    }

    /**
     * ���Excel add by wanglong 20130117
     */
    public void onExcel() {
        TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("TAB_PANEL");
        if (tabbedPane.getSelectedIndex() == 0) {
            this.messageBox("���ں�ͬ��ϸ��ʹ��");
            return;
        }
        if (mrTab.getRowCount() <= 0) {
            this.messageBox("û������");
            return;
        } else {
            int rowCount = contractD.rowCount();
            if (rowCount <= 0) {
                this.messageBox("û������");
                return;
            }
            boolean flag = false;
            for (int i = 0; i < rowCount; i++) {
                if (!contractD.isActive(i)) continue;
                else {
                    flag = true;
                }
            }
            if (flag == false) {
                this.messageBox("û������");
                return;
            }
        }
        TParm showParm = mrTab.getShowParmValue();
        TParm result = new TParm();
        for (int i = 0; i < mrTab.getRowCount() - 1; i++) {
            result.addData("SEQ_NO", showParm.getValue("SEQ_NO", i));// ���
            result.addData("PAT_NAME", showParm.getValue("PAT_NAME", i));// ����
            result.addData("SEX_CODE", showParm.getValue("SEX_CODE", i));// �Ա�
            String s =
                    StringUtil.getInstance().countAge(showParm.getValue("BIRTHDAY", i)
                                                              .replaceAll("/", ""),
                                                      new Timestamp(0), "");
            result.addData("BIRTHDAY", (s.split("��")[0].equals("0") || s.split("��")[0].equals("1"))
                    ? s.split("��")[0] + "��" : s.split("��")[0] + "��");// ����(����)
            result.addData("MR_NO", showParm.getValue("MR_NO", i));// ������
            result.addData("CONTRACT_DESC", showParm.getValue("CONTRACT_DESC", i));// �ײ�����
        }
        result.setCount(mrTab.getRowCount() - 1);
        result.addData("SYSTEM", "COLUMNS", "SEQ_NO");
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        result.addData("SYSTEM", "COLUMNS", "SEX_CODE");
        result.addData("SYSTEM", "COLUMNS", "BIRTHDAY");
        result.addData("SYSTEM", "COLUMNS", "MR_NO");
        result.addData("SYSTEM", "COLUMNS", "CONTRACT_DESC");
        result.setData("TITLE", comTab.getItemString(comTab.getSelectedRow(), "COMPANY_DESC"));
        result.setData("HEAD", "���,80,double;��Ա����,120;�Ա�,80;����,100;������,140;�ײ�����,200");
        TParm[] execleTable = new TParm[]{result };
        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "����������Ϣ");
        // ExportExcelUtil.getInstance().exportExcel(mrTab,"����������Ϣ");
    }

    /**
     * У�����֤��T40���isID����û��У�����֤�м�ĳ��������Ƿ���ȷ��
     * 
     * @param idcard
     * @return
     */
    public boolean isId(String idcard) {// add by wanglong 20130417
        if ((idcard == null) || (idcard.length() == 0)) {
            return false;
        }
        if (idcard.length() == 15) {
            idcard = uptoeighteen(idcard);
        }
        if (idcard.length() != 18) {
            return false;
        }
        String birthday = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (birthday.matches(regexString)) {
            if (StringTool.isId(idcard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ������֤��ȷ�ԡ�����18λ���֤�����У��λ����ȷ���Զ�������
     * 
     * @param idcard
     * @return
     */
    public String checkID(String idcard) {// add by wanglong 20130409
        if (idcard.length() != 15 && idcard.length() != 18) {
            return "FALSE";
        }
        if (idcard.length() == 15) {
            if (StringTool.isId(idcard)) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
        String date = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (date.matches(regexString)) {
            if (!StringTool.isId(idcard)) {
                String verrifyChar = getIDVerify(idcard);
                if (verrifyChar.equals("")) {// add by wanglong 20130521
                    return "FALSE";
                }
                idcard = idcard.substring(0, 17) + verrifyChar;
                return idcard;
            } else return "TRUE";
        } else return "FALSE";
    }

    /**
     * ���֤15λ��18λ
     * 
     * @param fifteencardid
     * @return
     */
    public String uptoeighteen(String fifteencardid) {// add by wanglong 20130409
        String eightcardid = fifteencardid.substring(0, 6);
        eightcardid = eightcardid + "19";
        eightcardid = eightcardid + fifteencardid.substring(6, 15);
        eightcardid = eightcardid + getIDVerify(eightcardid);
        return eightcardid;
    }

    /**
     * �õ�18λ���֤����λ
     * 
     * @param eightcardid
     * @return
     */
    public String getIDVerify(String eightcardid) {// add by wanglong 20130409
        int[] wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
        int[] vi = {1, 0, 88, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] ai = new int[18];
        int remaining = 0;
        if (eightcardid.length() == 18) {
            eightcardid = eightcardid.substring(0, 17);
        }
        if (eightcardid.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eightcardid.substring(i, i + 1);
                try {// add by wanglong 20130521
                    ai[i] = Integer.parseInt(k);
                }
                catch (Exception e) {
                    return "";
                }
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remaining = sum % 11;
        }
        return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
    }
}
