package com.javahis.ui.hrm;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jdo.bil.BilInvoice;
import jdo.hrm.HRMBill;
import jdo.hrm.HRMChargeTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMInvRcp;
import jdo.hrm.HRMOpbReceipt;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatInfo;
import jdo.opb.OPBReceiptTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p> Title: ���������ϸ��ѯ </p>
 *
 * <p> Description: ���������ϸ��ѯ </p>
 *
 * <p> Copyright: Copyright (c) 2013 </p>
 *
 * <p> Company:BlueCore </p>
 *
 * @author wanglong 2013.03.24
 * @version 1.0
 */
public class HRMReceiptDetailControl extends TControl {

    // ��������
    private HRMPatInfo pat;
    // Ʊ�ݶ���
    private HRMOpbReceipt receipt;
    // �վݶ���
    private HRMInvRcp invRcp;
    // Ʊ�Ź������
    private BilInvoice bilInvoice;
    // �˵�����
    private HRMBill bill;
    // ҽ������
    private HRMOrder order;
    // ����ϸ��TABLE
    private TTable tableM, tableD;
    // ��ͬϸ�����
    private HRMContractD contractD;
    // ��ͬ������TEXTFORMAT
    private TTextFormat company, contract;
    // ��ƱTABLE������
    private int receiptRow;

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ��ʼ������
        initData();
        // ȡ�ô������
        if (this.getParameter() != null) {
            TParm parm = (TParm) this.getParameter();
            String contractCode = parm.getValue("CASE_NO", 0);
            String companyCode = parm.getValue("MR_NO", 0);
            this.setValue("COMPANY_CODE", companyCode);
            onCompanyChoose("1");
            this.setValue("CONTRACT_CODE", contractCode);
            tableM.setParmValue(parm);
            receiptRow = 0;
            tableM.setSelectedRow(receiptRow);
            onClickTableM();
        }
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        tableM = (TTable) this.getComponent("TABLEM");
        tableD = (TTable) this.getComponent("TABLED");
    }

    /**
     * ��ʼ������
     */
    public boolean initData() {
        contractD = new HRMContractD();
        pat = new HRMPatInfo();
        invRcp = new HRMInvRcp();
        receipt = new HRMOpbReceipt();
        bill = new HRMBill();
        order = new HRMOrder();
        receiptRow = -1;
        return true;
    }

    /**
     * ����¼�
     */
    public void onClear() {
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.clearValue("MR_NO;PAT_NAME;ID_NO;UPDATE_NO");
        tableM.setParmValue(new TParm());
        tableD.setParmValue(new TParm());
        contractD = new HRMContractD();
        pat = new HRMPatInfo();
        receipt = new HRMOpbReceipt();
        invRcp = new HRMInvRcp();
        bilInvoice = new BilInvoice("OPB");
        bill = new HRMBill();
        bill.onQuery();
        receiptRow = -1;
    }

    /**
     * �������ѡ���¼�
     */
    public void onCompanyChoose(String flag) {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        // ������������ø�����ĺ�ͬ����
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm.getErrCode() != 0) {
            this.messageBox("û������");
        }
        // ����һ��TTextFormat,����ͬ���ֵ������ؼ���ȡ�����һ����ͬ���븳ֵ������ؼ���ʼֵ
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox("��ѯʧ��");
            return;
        }
        contract.setValue(contractCode);
        if (StringUtil.isNullString(flag)) {
            onContractChoose();
        }
    }

    /**
     * ��ͬ����ѡ���¼�
     */
    public void onContractChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.messageBox(companyCode);
            return;
        }
        String contractCode = this.getValueString("CONTRACT_CODE");
        // �����������ͺ�ͬ�����øú�ͬ���˵���Ϣ��ֵ�ڽ�����
        TParm result = HRMChargeTool.getInstance().onQueryReceiptByContract(contractCode);
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0116");// û������
            return;
        }
        tableM.setParmValue(result);
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        String mrNo = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        pat.onQuery(mrNo);
        TParm patParm = pat.getRowParm(0);
        if (StringUtil.isNullString(patParm.getValue("MR_NO"))) {
            this.messageBox("�����Ų�����");
            onClear();
            return;
        }
        TParm result = HRMChargeTool.getInstance().onQueryReceiptByMr(mrNo);
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("E0116");// û������
            return;
        }
        onClear();
        setValue("MR_NO", mrNo);
        setValue("PAT_NAME", patParm.getValue("PAT_NAME"));
        setValue("SEX_CODE", patParm.getValue("SEX_CODE"));
        setValue("IDNO", patParm.getValue("IDNO"));
        tableM.setParmValue(result);
    }

    /**
     * �����س��¼�
     */
    public void onPatName() {
        String patName = getValueString("PAT_NAME").trim();
        if (StringUtil.isNullString(patName)) {
            return;
        }
        TParm result = pat.isSamePatByName(patName);
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
            } else return;
            TParm result1 =
                    HRMChargeTool.getInstance().onQueryReceiptByMr(patParm.getValue("MR_NO"));
            if (result1.getErrCode() != 0) {
                this.messageBox(result1.getErrText());
                return;
            }
            if (result1.getCount() < 1) {
                this.messageBox("E0116");// û������
                return;
            }
            onClear();
            setValue("MR_NO", patParm.getValue("MR_NO"));
            setValue("PAT_NAME", patName);
            setValue("SEX_CODE", patParm.getValue("SEX_CODE"));
            setValue("IDNO", patParm.getValue("IDNO"));
            tableM.setParmValue(result1);
        }
    }

    /**
     * ��TABLE�����¼�
     */
    public void onClickTableM() {
        receiptRow = tableM.getSelectedRow();
        if (receiptRow < 0) {
            return;
        }
        // ȡ��Ʊ�ݺ�
        TParm parm = tableM.getParmValue();
        String receiptNo = parm.getValue("RECEIPT_NO", receiptRow);
        if (StringUtil.isNullString(receiptNo)) {
            this.messageBox("�վݺ�Ϊ��");
            return;
        }
        this.setValue("COMPANY_CODE", parm.getValue("MR_NO", receiptRow));
        onCompanyChoose("1");
        this.setValue("CONTRACT_CODE", parm.getValue("CASE_NO", receiptRow));
        TParm result = HRMChargeTool.getInstance().onQueryOrderdetail(receiptNo);
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
        }
        tableD.setParmValue(result);
    }

    /**
     * �����嵥
     */
    public void onPrintDetail() {
        if (tableM == null) {
            return;
        }
        int row = tableM.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��һ���վ�");
            return;
        }
        TParm parmM = tableM.getParmValue();
        String receiptNo = parmM.getValue("RECEIPT_NO", row);
        if (StringUtil.isNullString(receiptNo)) {
            this.messageBox("ȡ���վݺ�ʧ��");
            return;
        }
        TParm parmD = tableD.getParmValue();
        if (parmD == null) {
            this.messageBox("��ѯ����ʧ��");
            return;
        }
        TParm tableParm = new TParm();
        double sum = 0;
        for (int i = 0; i < parmD.getCount(); i++) {
            tableParm.addData("PAT_NAME", parmD.getValue("PAT_NAME", i));
            tableParm.addData("ORDER_DESC", parmD.getValue("ORDER_DESC", i));
            tableParm.addData("OWN_PRICE", parmD.getValue("OWN_PRICE", i));
            tableParm.addData("DISPENSE_QTY", parmD.getValue("DISPENSE_QTY", i));
            tableParm.addData("UNIT_CODE", parmD.getValue("UNIT_CODE", i));
            tableParm.addData("AR_AMT", parmD.getDouble("AR_AMT", i));
            sum += parmD.getDouble("AR_AMT", i);
        }
        tableParm.setCount(tableParm.getCount("ORDER_DESC"));
        tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        tableParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
        tableParm.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
        tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
        Timestamp now = order.getDBTime();
        TParm result = new TParm();
        result.setData("PRINT_TIME", "TEXT", "��ӡʱ�䣺" + StringTool.getString(now, "yyyy/MM/dd"));
        result.setData("PRINT_USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
        result.setData("BILL_TABLE", tableParm.getData());
        new BigDecimal(sum).setScale(0, BigDecimal.ROUND_HALF_UP);
        result.setData("sum5", "TEXT", "�ϼƣ�" + new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP));
        openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMBillDetail.jhw", result, false);
    }

    /**
     * ��ӡOPB_RECEIPT����PRINT_NO��PRINT_DATE��BIL_INVRCP����ԭ�ȵ�CANCEL_FLGΪ1��CANCEL_USER,CANCEL_DATE�����²���һ��
     */
    public void onRePrint() {
        if (receiptRow < 0) {
            this.messageBox("��ѡ��һ���˵�");
            return;
        }
        bilInvoice = new BilInvoice("OPB");
        String updateNo = getNextUpdateNo();
        if (StringUtil.isNullString(updateNo)) {
            return;
        }
        TParm parm = tableM.getParmValue();
        if (parm == null) {
            return;
        }
        String oldPrintNo = parm.getValue("PRINT_NO", receiptRow);
        if (oldPrintNo.equals("")) {
            this.messageBox("���˵�δ��Ʊ�����ܲ���Ʊ��");
            return;
        }
        String receiptNo = parm.getValue("RECEIPT_NO", receiptRow);
        String flagSql = "SELECT * FROM BIL_OPB_RECP WHERE RECEIPT_NO='" + receiptNo + "'";// add by wanglong 20130329
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵�״̬���� " + flagParm.getErrText());
            return;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("���˵�������");
            onContractChoose();
            return;
        }
        if (StringUtil.isNullString(flagParm.getValue("PRINT_NO", 0))) {
            this.messageBox("���˵�δ��Ʊ,���ܲ���Ʊ��");
            return;
        }
        String patName = "";
        if (this.getValue("COMPANY_CODE").equals(parm.getValue("MR_NO", receiptRow))) {
            patName = company.getText();
        } else {
            this.messageBox("�������Ʋ�һ��");
            return;
        }
        String sexCode = this.getValueString("SEX_CODE");
        receipt = new HRMOpbReceipt();
        receipt.onQueryByReceiptNo(receiptNo);
        if (!receipt.onRePrint(receipt.getItemString(0, "CASE_NO"), receiptNo, updateNo)) {
            this.messageBox("��дƱ��ʧ��");
            return;
        }
        invRcp = new HRMInvRcp();
        if (!invRcp.onRePrint(oldPrintNo, updateNo)) {
            this.messageBox("��д��Ʊʧ��");
            return;
        }
        String[] sql = receipt.getUpdateSQL();
        sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
        String[] tempSql = new String[]{getBilInvoiceUpdate() };
        sql = StringTool.copyArray(sql, tempSql);
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result = TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge", inParm);
        if (result.getErrCode() != 0) {
            this.messageBox("��ӡʧ�� " + result.getErrText());
            return;
        }
        dealPrintData(new String[]{receiptNo }, patName, Operator.getDept(), sexCode);
    }

    /**
     * �����ӡ����
     * 
     * @param receiptNo
     * @param patName
     * @param deptCode
     * @param sexCode
     */
    public void dealPrintData(String[] receiptNo, String patName, String deptCode, String sexCode) {
        int size = receiptNo.length;
        for (int i = 0; i < size; i++) {
            // ȡ��һ��Ʊ�ݺ�
            String recpNo = receiptNo[i];
            if (recpNo == null || recpNo.length() == 0) {
                this.messageBox("��������");
                return;
            }
            // ���ô�ӡһ��Ʊ�ݵķ���
            onPrint(OPBReceiptTool.getInstance().getOneReceipt(recpNo), patName, deptCode, sexCode, recpNo);
        }
    }

    /**
     * ��ӡƱ��
     * 
     * @param recpParm
     * @param patName
     * @param deptCode
     * @param sexCode
     * @param receiptNo
     */
    public void onPrint(TParm recpParm, String patName, String deptCode, String sexCode,
                        String receiptNo) {
        if (recpParm == null) return;
        TParm oneReceiptParm = new TParm();
        // Ʊ����Ϣ
        oneReceiptParm.setData("CASE_NO", recpParm.getData("CASE_NO", 0));
        oneReceiptParm.setData("RECEIPT_NO", receiptNo);
        oneReceiptParm.setData("MR_NO", recpParm.getData("MR_NO", 0));
        oneReceiptParm.setData("BILL_DATE", recpParm.getData("BILL_DATE", 0));
        oneReceiptParm.setData("CHARGE_DATE", recpParm.getData("CHARGE_DATE", 0));
        oneReceiptParm.setData("CHARGE01", recpParm.getDouble("CHARGE01", 0) + recpParm.getDouble("CHARGE02", 0));
        // oneReceiptParm.setData("CHARGE02", recpParm.getData("CHARGE02", 0));
        oneReceiptParm.setData("CHARGE03", recpParm.getData("CHARGE03", 0));
        oneReceiptParm.setData("CHARGE04", recpParm.getData("CHARGE04", 0));
        oneReceiptParm.setData("CHARGE05", recpParm.getData("CHARGE05", 0));
        oneReceiptParm.setData("CHARGE06", recpParm.getData("CHARGE06", 0));
        oneReceiptParm.setData("CHARGE07", recpParm.getData("CHARGE07", 0));
        oneReceiptParm.setData("CHARGE08", recpParm.getData("CHARGE08", 0));
        oneReceiptParm.setData("CHARGE09", recpParm.getData("CHARGE09", 0));
        oneReceiptParm.setData("CHARGE10", recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", recpParm.getData("CHARGE11", 0));
        oneReceiptParm.setData("CHARGE12", recpParm.getData("CHARGE12", 0));
        oneReceiptParm.setData("CHARGE13", recpParm.getData("CHARGE13", 0));
        oneReceiptParm.setData("CHARGE14", recpParm.getData("CHARGE14", 0));
        oneReceiptParm.setData("CHARGE15", recpParm.getData("CHARGE15", 0));
        oneReceiptParm.setData("CHARGE16", recpParm.getData("CHARGE16", 0));
        oneReceiptParm.setData("CHARGE17", recpParm.getData("CHARGE17", 0));
        oneReceiptParm.setData("CHARGE18", recpParm.getData("CHARGE18", 0));
        oneReceiptParm.setData("CHARGE19", recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", recpParm.getData("CHARGE30", 0));
        oneReceiptParm.setData("TOT_AMT", StringTool.round(recpParm.getDouble("TOT_AMT", 0), 2));
        oneReceiptParm.setData("AR_AMT", StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
        oneReceiptParm.setData("CASHIER_CODE", recpParm.getData("CASHIER_CODE", 0));
        oneReceiptParm.setData("PAT_NAME", patName);
        oneReceiptParm.setData("DEPT_CODE", deptCode);
        oneReceiptParm.setData("SEX_CODE", sexCode);
        oneReceiptParm.setData("OPT_USER", Operator.getName());
        oneReceiptParm.setData("OPT_ID", Operator.getID());
        oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        oneReceiptParm.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
        oneReceiptParm.setData("PRINT_NO", recpParm.getData("PRINT_NO", 0));
        TParm patParm =
                new TParm(
                        TJDODBTool
                                .getInstance()
                                .select("SELECT B.DEPT_CHN_DESC FROM HRM_PATADM A,SYS_DEPT B WHERE A.COMPANY_CODE='"
                                                + recpParm.getData("MR_NO", 0)
                                                + "' AND A.CONTRACT_CODE='"
                                                + recpParm.getData("CASE_NO", 0)
                                                + "' AND A.DEPT_CODE=B.DEPT_CODE"));
        oneReceiptParm.setData("MR_NO", "TEXT", "��������:" + patName);
        oneReceiptParm.setData("DEPT_CODE", "TEXT", "����:" + patParm.getValue("DEPT_CHN_DESC", 0));
        oneReceiptParm.setData("CLINICROOM_DESC", "TEXT", "");
        oneReceiptParm.setData("DR_CODE", "TEXT", "");
        oneReceiptParm.setData("QUE_NO", "TEXT", "");
        oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
        oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
                .getHospitalCHNFullName(Operator.getRegion()));
        oneReceiptParm.setData("RECEIPT_NO", "TEXT", receiptNo);
        oneReceiptParm.setData("PRINT_NO", "TEXT", recpParm.getData("PRINT_NO", 0));
        oneReceiptParm.setData("TOT_AMT", "TEXT",
                               StringTool.round(recpParm.getDouble("TOT_AMT", 0), 2));
        oneReceiptParm.setData("AMT_TO_WORD", "TEXT", StringUtil.getInstance().numberToWord(StringTool.round(recpParm.getDouble("TOT_AMT", 0), 2)));
        oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance().numberToWord(StringTool.round(recpParm.getDouble("AR_AMT", 0), 2)));
        oneReceiptParm.setData("OPT_USER", "TEXT", "����Ա:" + Operator.getName());
        String printDate =
                StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");
        oneReceiptParm.setData("PRINT_DATE", "TEXT", printDate);
        oneReceiptParm.setData("YEAR", "TEXT", printDate.substring(0, 4));
        oneReceiptParm.setData("MONTH", "TEXT", printDate.substring(5, 7));
        oneReceiptParm.setData("DAY", "TEXT", printDate.substring(8, 10));
        oneReceiptParm.setData("OPT_ID", "TEXT", Operator.getID());
        if (recpParm.getDouble("CHARGE01", 0) + recpParm.getDouble("CHARGE02", 0) == 0.0) {
            oneReceiptParm.setData("CHARGE01", "TEXT", "");
        } else {
            oneReceiptParm.setData("CHARGE01", "TEXT", recpParm.getDouble("CHARGE01", 0) + recpParm.getDouble("CHARGE02", 0));
        }
        String[] array =
                {"CHARGE03", "CHARGE04", "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
                        "CHARGE09", "CHARGE13", "CHARGE14", "CHARGE15", "CHARGE16", "CHARGE17",
                        "CHARGE18" };
        for (int i = 0; i < array.length; i++) {
            if (recpParm.getData(array[i], 0).equals(0.0)) {
                oneReceiptParm.setData(array[i], "TEXT", "");
            } else {
                oneReceiptParm.setData(array[i], "TEXT", recpParm.getData(array[i], 0));
            }
        }
        oneReceiptParm.setData("CHARGE10", "TEXT", recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", "TEXT", recpParm.getData("CHARGE11", 0));
        if (recpParm.getDouble("CHARGE11", 0) > 0) {// add by wanglong 20130123
            oneReceiptParm.setData("CUSTOM_TEXT1", "TEXT", "����");
        } else {
            oneReceiptParm.setData("CHARGE11", "TEXT", "");
        }
        oneReceiptParm.setData("CHARGE12", "TEXT", recpParm.getData("CHARGE12", 0));
        oneReceiptParm.setData("CHARGE19", "TEXT", recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", "TEXT", recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", "TEXT", recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", "TEXT", recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", "TEXT", recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", "TEXT", recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", "TEXT", recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", "TEXT", recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", "TEXT", recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", "TEXT", recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", "TEXT", recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", "TEXT", recpParm.getData("CHARGE30", 0));
        oneReceiptParm.setData("USER_NAME", "TEXT", "" + Operator.getID());// modify by wanglong 20130123
        oneReceiptParm.setData("OPT_DATE", "TEXT", printDate);
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw", oneReceiptParm);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrintForHRM.class", oneReceiptParm));//����ϲ�modify by wanglong 20130730
    }

    /**
     * ȡ����һƱ��
     * 
     * @return
     */
    private String getNextUpdateNo() {
        String updateNo = bilInvoice.getUpdateNo();
        this.setValue("UPDATE_NO", updateNo);
        if (updateNo.equalsIgnoreCase(bilInvoice.getEndInvno())) {
            this.messageBox("�޿ɴ�ӡƱ��");
            return "";
        }
        if (StringTool.addString(updateNo).equalsIgnoreCase(bilInvoice.getEndInvno())) {
            this.messageBox("��ʣ��һ��Ʊ��");
        }
        return updateNo;
    }

    /**
     * ��ӡʱ����Ʊ�ݵ��ĸ������
     * 
     * @return
     */
    private String getBilInvoiceUpdate() {
        String sqlUpdate =
                "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
        sqlUpdate = sqlUpdate.replaceFirst("#", StringTool.addString(bilInvoice.getUpdateNo()));
        sqlUpdate = sqlUpdate.replaceFirst("#", bilInvoice.getStartInvno());
        // System.out.println("sqlUpdate=" + sqlUpdate);
        return sqlUpdate;
    }

    /**
     * �˷ѣ���BIL_OPB_RECP�����ݱ�ɸ�������һ���µ����ݣ�
     * ���¾ɵ�BIL_INVRCP���ݵ�CANCEL_FLGΪ2, CANCEL_USER, CANCEL_DATE
     */
    public void onDisCharge() {
        bilInvoice = new BilInvoice("OPB");
        if (this.receiptRow < 0) {
            this.messageBox("��ѡ��һ���վ��˷�");
            return;
        }
        TParm parm = tableM.getParmValue();
        if (parm == null) {
            return;
        }
        String receiptNo = parm.getValue("RECEIPT_NO", receiptRow);
        if (StringUtil.isNullString(receiptNo)) {
            this.messageBox("ȡ���˵���ʧ��");
            return;
        }
        String printNo = parm.getValue("PRINT_NO", receiptRow);
        if (StringUtil.isNullString(printNo)) {
            this.messageBox("���˵�δ��Ʊ,�����˷�");
            return;
        }
        String flagSql = "SELECT * FROM BIL_OPB_RECP WHERE RECEIPT_NO='" + receiptNo + "'";// add by wanglong 20130329
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵�״̬���� " + flagParm.getErrText());
            return;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("���˵������ڣ��������˷�");
            onClear();
            return;
        }
        if (StringUtil.isNullString(flagParm.getValue("PRINT_NO", 0))) {
            this.messageBox("���˵�δ��Ʊ,�����˷�");
            return;
        }
        order = new HRMOrder();
        order.onQueryByReceiptNo(receiptNo);
        int count = order.rowCount();
        if (count <= 0) {
            this.messageBox("ȡ��ҽ������ʧ��");
            return;
        }
        if (!order.deleteMedApplyByAppNo()) {// ����ҽ����״̬
            this.messageBox("ɾ��ҽ��ʧ��");
            return;
        }
        receipt = new HRMOpbReceipt();
        receipt.onQueryByReceiptNo(receiptNo);
        String oldPrintNo = receipt.getItemString(0, "PRINT_NO");
        if (oldPrintNo.equals("")) {// add by wanglong 20130221
            messageBox("���վ�δ��Ʊ�������˷�");
            return;
        }
        receipt.onDisCharge(0, oldPrintNo, "");// ����������Ϊ֧����ʽ���Ѿ�����Ҫ��
        receipt.setItem(0, "RESET_RECEIPT_NO",
                        receipt.getItemString(receipt.rowCount() - 1, "RECEIPT_NO"));
        invRcp = new HRMInvRcp();
        invRcp.onQueryByInvNo(oldPrintNo);
        if (!StringUtil.isNullString(invRcp.getItemString(0, "CANCEL_FLG"))
                && !"0".equalsIgnoreCase(invRcp.getItemString(0, "CANCEL_FLG"))) {
            this.messageBox("���˷��˵������ظ��˷�");
            return;
        }
        invRcp.setItem(0, "CANCEL_FLG", "1");
        invRcp.setItem(0, "CANCEL_USER", Operator.getID());
        invRcp.setItem(0, "CANCEL_DATE", invRcp.getDBTime());
        invRcp.setActive(0, true);
        // if (StringTool.getBoolean(invRcp.getItemString(0, "ACCOUNT_FLG"))) {//Ŀǰ��֧�����ս������˷�
        // this.messageBox("���սᷢƱ�����˷�");
        // return;
        // }
        bill = new HRMBill();
        bill.onQueryByReceiptNo(receiptNo);
        bill.setItem(0, "RECEIPT_NO", "");
        bill.setItem(0, "REXP_FLG", "N");
        bill.setItem(0, "OPT_USER", Operator.getID());
        bill.setItem(0, "OPT_DATE", bill.getDBTime());
        bill.setItem(0, "OPT_TERM", Operator.getIP());
        bill.setActive(0, true);
        String[] sql = receipt.getUpdateSQL();
        sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
        sql = StringTool.copyArray(sql, order.getUpdateSQL());
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        String updateDSql =
                "UPDATE HRM_CONTRACTD SET RECEIPT_NO='',BILL_FLG = '0' WHERE RECEIPT_NO='#'";
        updateDSql = updateDSql.replaceFirst("#", receiptNo);
        sql = StringTool.copyArray(sql, new String[]{updateDSql });
        String caseSql =
                "SELECT DISTINCT CASE_NO FROM HRM_ORDER WHERE RECEIPT_NO='" + receiptNo + "'";
        TParm caseParm = new TParm(TJDODBTool.getInstance().select(caseSql));
        if (caseParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵��µ���Ա��Ϣʧ��");
            return;
        }
        if (caseParm.getCount() <= 0) {
            this.messageBox("�˵���û����Ա��Ϣ");
            return;
        }
        for (int i = 0; i < caseParm.getCount("CASE_NO"); i++) {
            String updatePatAdmSql = "UPDATE HRM_PATADM SET BILL_FLG = '0' WHERE CASE_NO = '#'";
            updatePatAdmSql = updatePatAdmSql.replaceFirst("#", caseParm.getValue("CASE_NO", i));
            sql = StringTool.copyArray(sql, new String[]{updatePatAdmSql });
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        // inParm.setData("DIS_LIST",disList);
        TParm result = TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge", inParm);
        if (result.getErrCode() != 0) {
            this.messageBox("�˷�ʧ�� " + result.getErrText());
            return;
        }
        receipt.resetModify();
        invRcp.resetModify();
        order.resetModify();
        order.resetMedApply();
        bill.resetModify();
        this.messageBox("�˷ѳɹ�");
        onClear();
    }
}
