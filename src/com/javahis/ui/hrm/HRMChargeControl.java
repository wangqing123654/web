package com.javahis.ui.hrm;

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
import jdo.hrm.HRMPackageD;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.hrm.HRMPrePay;
import jdo.opb.OPB;
import jdo.opb.OPBReceiptTool;
import jdo.reg.REGSysParmTool;
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
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHRMCompany;
import com.javahis.util.StringUtil;

/**
 * <p> Title: �����������ɷ� </p>
 *
 * <p> Description: �����������ɷ� </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMChargeControl extends TControl {

    // �շ�TABLE������TABLE��ҽ��TABLE
    private TTable feeTable, billTable, billDetailTable;
    // ��ͬ������TEXTFORMAT
    private TTextFormat contract, company;
    // ��ͬϸ�����
    private HRMContractD contractD;
    // Ԥ�������
    private HRMPrePay prePay;
    // ҽ������
    private HRMOrder order;
    // ��������
    private HRMPatInfo pat;
    // ��������
    private HRMPatAdm adm;
    // Ʊ�ݶ���
    private HRMOpbReceipt receipt;
    // �ײ�ϸ�����
    private HRMPackageD packageD;
    // ��Ʊ����
    private HRMInvRcp invRcp;
    // Ʊ�ݹ������
    private BilInvoice bilInvoice;
    // �˵�����
    private HRMBill bill;
    // ��һƱ��
    private String updateNo;
    // Ĭ��֧����ʽ
    private String payType;
    //�˵��б�
    private String billNoList;//add by wanglong 20130510
    // ��Ʊ��
    private String receiptNo;
    // �˵��б�
    private TParm billParm;// add by wanglong 20130324

    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ��ս���
        onClear();
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        contractD = new HRMContractD();
        order = new HRMOrder();
        order.onQuery();
        bill = new HRMBill();
        bill.onQuery();
        receipt = new HRMOpbReceipt();
        invRcp = new HRMInvRcp();
        billParm = new TParm();// add by wanglong 20130324
        // ��ѯĬ��֧����ʽ
        // TParm regParm = REGSysParmTool.getInstance().selPayWay();
        // if (regParm.getErrCode() != 0) {
        // this.messageBox_("��ʼ������ʧ��");
        // return;
        // }
        // payType=regParm.getValue("DEFAULT_PAY_WAY",0);
        payType = "C0";// �ֽ�֧��
        this.setValue("PAY_TYPE", payType);
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        billTable = (TTable) this.getComponent("BILL_TABLE");
        billDetailTable = (TTable) this.getComponent("BILL_DETAIL_TABLE");
        billTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onPatCheckBox");// ����˵�����ʾ����Ա�˵���ϸ add by wanglong 20130510
    }

    /**
     * ����¼�
     */
    public void onClear() {
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.clearValue("MR_NO;PAT_NAME;ID_NO;UPDATE_NO");
        this.setValue("AR_AMT", 0);// Ӧ��
        this.setValue("TOT_AMT", 0);// ʵ��
        this.setValue("CHARGE", 0);// ����
        this.setValue("NOPRINT", "");// ����ӡ�վ�
        this.setValue("PAY_TYPE", "C0");
        this.setValue("PAY_REMARK", "");//֧Ʊ��ע
        this.setValue("CHECK_NO", "");//֧Ʊ�� add by wanglong 20130716
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {
            // ȡ����һƱ�ţ���ֵ��������
            bilInvoice = new BilInvoice("OPB");
            updateNo = getNextUpdateNo();
        } else {
            bilInvoice = new BilInvoice("OPB");
            updateNo = bilInvoice.getUpdateNo();
        }
        billNoList="";//modify by wanglong 20130510
        receiptNo = "";
        this.callFunction("UI|BUT_FEE|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
        this.callFunction("UI|print|setEnabled", false);
        this.callFunction("UI|prePay|setEnabled", true);
        // this.setValue("ALL", "Y");
        this.setValue("UNFEE", "Y");//add by wanglong 20130510
        this.callFunction("UI|ALL_CHOOSE|setEnabled", true);
        // ��ʼ�����ݣ������ݸ���������
        initData();
    }

    /**
     * �˷Ѻ�����¼�
     */
    public void onClearAfterDischarge() {
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("PAT_NAME", "");
        this.clearValue("MR_NO;ID_NO;UPDATE_NO");
        this.setValue("AR_AMT", 0);
        this.setValue("TOT_AMT", 0);
        this.setValue("CHARGE", 0);
        billNoList = "";//modify by wanglong 20130510
        receiptNo = "";
        this.callFunction("UI|BUT_FEE|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
        this.callFunction("UI|print|setEnabled", false);
        this.callFunction("UI|prePay|setEnabled", true);
        // ��ʼ�����ݣ������ݸ���������
        initData();
    }

    /**
     * ˢ�½���
     */
    public void onRefreshUI() {// add by wanglong 20130329
        int billlRow = billTable.getSelectedRow();
        if (billlRow < 0) {
            onClear();
            return;
        }
        if (billParm == null || billParm.getCount() < 1) {
            onClear();
            return;
        }
        String billNo = billParm.getValue("BILL_NO", billlRow);
        onStateChoose();
        if (billParm == null || billParm.getCount() < 1) {
            onClear();
            return;
        }
        int i = 0;
        for (; i < billParm.getCount(); i++) {
            if (billNo.equals(billParm.getValue("BILL_NO", i))) {
                break;
            }
        }
        if (i == billParm.getCount()) {
            onClear();
            return;
        }
        this.clearValue("AR_AMT;TOT_AMT;CHARGE;NOPRINT");
        this.setValue("PAY_TYPE", "C0");
        this.callFunction("UI|BUT_FEE|setEnabled", false);
        billTable.setSelectedRow(i);
        onBillTableClick();
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {
            // ȡ����һƱ�ţ���ֵ��������
            bilInvoice = new BilInvoice("OPB");
            updateNo = getNextUpdateNo();
        } else {
            bilInvoice = new BilInvoice("OPB");
            updateNo = bilInvoice.getUpdateNo();
        }
    }
    
    /**
     * �������ѡ���¼�
     */
    public void onCompanyChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        // ������������ø�����ĺ�ͬ����
        TParm contractParm = contractD.onQueryByCompanyWithBlank(companyCode);//add by wanglong 20130510
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
        }
        // ����һ��TTextFormat,����ͬ���ֵ������ؼ���ȡ�����һ����ͬ���븳ֵ������ؼ���ʼֵ
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 1);//modify by wanglong 20130510
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("��ѯʧ��");
            return;
        }
        contract.setValue(contractCode);
        onContractChoose();// add by wanglong 20130324
    }

    /**
     * ��ͬ����ѡ���¼�
     */
    public void onContractChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.messageBox_(companyCode);
            return;
        }
        String contractCode = this.getValueString("CONTRACT_CODE");
        // �����������ͺ�ͬ�����øú�ͬ���˵���Ϣ��ֵ�ڽ�����
        String sql =//modify by wanglong 20130906
                "WITH AA AS (SELECT DISTINCT CASE WHEN A.REXP_FLG='Y' THEN 'Y' ELSE 'N' END REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,"
                        + "         A.CONTRACT_CODE,A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT,C.PAT_NAME,C.MR_NO,"
                        + "         DENSE_RANK() OVER (PARTITION BY A.RECEIPT_NO ORDER BY C.MR_NO) NUM "
                        + "    FROM HRM_BILL A, HRM_ORDER B, HRM_CONTRACTD C "
                        + "   WHERE A.BILL_NO = B.BILL_NO "
                        + "     AND B.MR_NO = C.MR_NO "
                        + "     AND A.COMPANY_CODE = '#' "
                        + "     # @ ) "
                        + "SELECT '' FLG,CASE WHEN REXP_FLG='Y' THEN 'Y' ELSE 'N' END REXP_FLG,RECEIPT_NO,COMPANY_CODE,CONTRACT_CODE,"
                        + "        WM_CONCAT(PAT_NAME) PAT_NAME,WM_CONCAT(MR_NO) MR_NO,BILL_NO,OWN_AMT,AR_AMT, DISCOUNT_AMT "
                        + "  FROM AA "
                        + " WHERE 1 = 1 "
                        + "   AND NUM < 50 "//ÿ���˵�ֻ��ʾǰ49��
                        + "GROUP BY AA.REXP_FLG,AA.RECEIPT_NO,AA.COMPANY_CODE,AA.CONTRACT_CODE,AA.BILL_NO,AA.OWN_AMT,AA.AR_AMT,AA.DISCOUNT_AMT "
                        + "ORDER BY AA.RECEIPT_NO";
//        String sql = // modify by wanglong 20130510
//                "SELECT '' FLG,CASE WHEN A.REXP_FLG = 'Y' THEN 'Y' ELSE 'N' END REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,A.CONTRACT_CODE,"
//                        + "WM_CONCAT(DISTINCT C.PAT_NAME) PAT_NAME,WM_CONCAT(DISTINCT C.MR_NO) MR_NO,"
//                        + "A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT "
//                        + " FROM HRM_BILL A, HRM_ORDER B, HRM_CONTRACTD C "
//                        + "WHERE A.BILL_NO = B.BILL_NO "
//                        + "  AND A.CONTRACT_CODE = B.CONTRACT_CODE "
//                        + "  AND B.MR_NO = C.MR_NO "
//                        + "  AND B.CONTRACT_CODE = C.CONTRACT_CODE "
//                        + "  AND A.COMPANY_CODE = C.COMPANY_CODE "
//                        + "  AND ROWNUM < 2000 "// 2000��ҽ������ϸ������������ÿ���˿�40��ҽ������ϸ���������ʾ50���ˡ�add by wanglong 20130829
//                                                // ��������������ʾ����ΪWM_CONCAT()�������ַ������ơ�
//                        + "  AND A.COMPANY_CODE = '#' "
//                        + " # "
//                        + " @ "
//                        + "GROUP BY A.REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT";
        sql = sql.replaceFirst("#", companyCode);// modify by wanglong 20130829
        if (!StringUtil.isNullString(contractCode)) {
            sql = sql.replaceFirst("#", " AND A.CONTRACT_CODE = '" + contractCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (this.getValueBoolean("UNFEE")) {// add by wanglong 20130324
            sql = sql.replaceFirst("@", " AND (A.REXP_FLG <> 'Y' OR A.REXP_FLG IS NULL) ");
        } else if (this.getValueBoolean("FEE")) {
            sql = sql.replaceFirst("@", " AND A.REXP_FLG = 'Y' ");
        } 
        // else {
        // sql = sql.replaceFirst("@", "");
        // }
        billParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵��б�ʧ��");
            return;
        }
        order = new HRMOrder();
        bill = new HRMBill();
        receipt = new HRMOpbReceipt();
        invRcp = new HRMInvRcp();
        billTable.setParmValue(billParm);
        billDetailTable.removeRowAll();
        this.clearValue("AR_AMT;TOT_AMT;CHARGE;NOPRINT");
        this.setValue("PAY_TYPE", "C0");
        this.callFunction("UI|BUT_FEE|setEnabled", false);
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {
            // ȡ����һƱ�ţ���ֵ��������
            bilInvoice = new BilInvoice("OPB");
            updateNo = getNextUpdateNo();
        } else {
            bilInvoice = new BilInvoice("OPB");
            updateNo = bilInvoice.getUpdateNo();
        }
    }

    /**
     * ��δ��Ʊ�������Ѵ�Ʊ������ȫ������ѡ��ť�¼�
     */
    public void onStateChoose() {//modify by wanglong 20130510
        // String contractCode = this.getValueString("CONTRACT_CODE");
        // if (contractCode.equals("")) {
        // this.messageBox("��ѡ���ͬ");
        // return;
        // } else {
        if (this.getValueBoolean("FEE")) {
            this.callFunction("UI|ALL_CHOOSE|setEnabled", false);
        } else {
            this.callFunction("UI|ALL_CHOOSE|setEnabled", true);
        }
        onContractChoose();
        // }
    }

    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {//add by wanglong 20130510
        String select = getValueString("ALL_CHOOSE");
        TParm parm = billTable.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        billTable.setParmValue(parm);
        billTable.setSelectedRow(0);
        this.onBillTableClick();
       // table.setSelectedRow(0);// add by wanglong 20130121
    }
    
    /**
     * �˵��б�ѡ�¼�����ʾ����ÿ���˵��˵�
     * 
     * @param obj
     * @return
     */
    public boolean onPatCheckBox(Object obj) {//add by wanglong 20130510
        TTable table = (TTable) obj;
        table.acceptText();
        if (billParm == null) {
            return true;
        }
        if (billParm.getCount() < 1) {
            this.messageBox("û���˵���Ϣ");
            return true;
        }
        if (this.getValueBoolean("FEE")) {// �����ѽɷѵ��˵���ֻ�ܵ�ѡ
            int row = table.getSelectedRow();
            String receiptNo = table.getItemString(row, "RECEIPT_NO");
            for (int i = 0; i < table.getRowCount(); i++) {
                if (table.getItemString(i, "RECEIPT_NO").equals(receiptNo)) {
                    table.setValueAt(table.getItemString(row, "FLG"), i, 0);
                } else {
                    table.setValueAt(table.getItemString(row, "FLG").equals("Y") ? "N" : "Y", i, 0);
                }
            }
        }
        this.onBillTableClick();
        return false;
    }
    
    /**
     * �˵�TABLE�����¼�
     */
    public void onBillTableClick() {//modify by wanglong 20130510
        billNoList = "";
        for (int i = 0; i < billParm.getCount(); i++) {
            if (billTable.getItemString(i, "FLG").equals("Y")
                    && StringUtil.isNullString(billParm.getValue("BILL_NO", i))) {
                this.messageBox_("���㵥��Ϊ��");
                return;
            } else if (billTable.getItemString(i, "FLG").equals("Y")) {
                billNoList += "'" + billParm.getValue("BILL_NO", i) + "',";
            }
        }
        if(billNoList.equals("")){
            billDetailTable.removeRowAll();
            this.setValue("PAT_NAME", "");
            this.clearValue("MR_NO;ID_NO;UPDATE_NO");
            this.setValue("AR_AMT", 0);
            this.setValue("TOT_AMT", 0);
            this.setValue("CHARGE", 0);

            billNoList="";
            receiptNo = "";
            this.callFunction("UI|BUT_FEE|setEnabled", true);
            this.callFunction("UI|delete|setEnabled", false);
            this.callFunction("UI|print|setEnabled", false);
            this.callFunction("UI|prePay|setEnabled", true);
            // ��ʼ�����ݣ������ݸ���������
            order = new HRMOrder();
            order.onQuery();
            bill = new HRMBill();
            bill.onQuery();
            receipt = new HRMOpbReceipt();
            invRcp = new HRMInvRcp();
            payType = "C0";// �ֽ�֧��
            this.setValue("PAY_TYPE", "C0");
            return;
        }
        billNoList = billNoList.substring(0, billNoList.length() - 1);
        double arAmt = 0;
        if (billParm.getBoolean("REXP_FLG", billTable.getSelectedRow())) {
            this.callFunction("UI|BUT_FEE|setEnabled", false);
            this.callFunction("UI|delete|setEnabled", true);
            this.callFunction("UI|print|setEnabled", true);
            this.callFunction("UI|prePay|setEnabled", false);
            String sql = // add by wanglong 20130423
                    "SELECT CASE WHEN PAY_CASH <> 0 THEN 'C0' WHEN PAY_MEDICAL_CARD <> 0 THEN 'EKT' "
                            + " WHEN PAY_BANK_CARD <> 0 THEN 'C1' WHEN PAY_INS_CARD <> 0 THEN 'INS' "
                            + " WHEN PAY_CHECK <> 0 THEN 'T0' WHEN PAY_DEBIT <> 0 THEN 'C4' "
                            + " WHEN PAY_DRAFT <> 0 THEN 'C2' WHEN PAY_BILPAY <> 0 THEN 'PAY_BILPAY' "
                            + " WHEN PAY_INS <> 0 THEN 'PAY_INS' END AS PAY_TYPE "
                            + " FROM BIL_OPB_RECP WHERE ADM_TYPE = 'H' "
                            + "  AND RECEIPT_NO = '"
                            + billParm.getValue("RECEIPT_NO", billTable.getSelectedRow()) + "'";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() != 0 || result.getCount() == 0) {
                this.messageBox("��ѯ֧����ʽ����");
                return;
            } else {
                this.setValue("PAY_TYPE", result.getValue("PAY_TYPE", 0));
            }
        } else {
            this.callFunction("UI|BUT_FEE|setEnabled", true);
            this.callFunction("UI|delete|setEnabled", false);
            this.callFunction("UI|print|setEnabled", false);
            this.callFunction("UI|prePay|setEnabled", true);
            // ȡ���˵���ֵ��������
            for (int i = 0; i < billParm.getCount(); i++) {
                if (billTable.getItemString(i, "FLG").equals("Y")) {
                    arAmt += billParm.getDouble("AR_AMT", i);
                }
            }
            this.setValue("AR_AMT", arAmt);// Ӧ�ս��
            this.setValue("TOT_AMT", arAmt);// ʵ�ս��
            this.setValue("CHARGE", 0);// ����
        }
        // billTableRow = row;
        TParm caseParm = order.getChargeData(billNoList, billParm.getBoolean("REXP_FLG", billTable.getSelectedRow()));
        if (caseParm == null) {
            this.messageBox_("���㵥��Ϊ��");
            return;
        }
        if (caseParm.getErrCode() != 0) {
            this.messageBox_("ȡ�÷�����ϸ����ʧ�� " + caseParm.getErrText());
            return;
        }
        billDetailTable.setParmValue(caseParm);
        // �ܶ�ؼ��õ�����
        this.callFunction("UI|TOT_AMT|grabFocus");
        bill = new HRMBill();
        bill.onQueryByBillNoList(billNoList);//add by wanglong 20130510
        return;
    }

    /**
     * �շ�
     */
    public void onFee() {
        if (this.messageBox("��ʾ", "�Ƿ��շ�", 2) != 0) {
            return;
        }
        String payTypePage = this.getValueString("PAY_TYPE");
        if (payTypePage.equals("")) {
            this.messageBox("��ѡ��֧����ʽ");
            return;
        }
        if ("EKT".equals(payTypePage)) { // ҽ�ƿ��շ�
            this.messageBox("Ŀǰ��֧��ҽ�ƿ��շѣ�");
            return;
        }
        double totAmt = StringTool.getDouble(this.getValueString("TOT_AMT"));
        double arAmt = StringTool.getDouble(this.getValueString("AR_AMT"));
        totAmt = StringTool.round(totAmt, 4);
        arAmt = StringTool.round(arAmt, 4);
        if (totAmt < arAmt) {
            this.messageBox("ʵ�ս��С��Ӧ�ս��");
            return;
        }
        TParm detailParm = billDetailTable.getParmValue();
        if (detailParm == null) {
            this.messageBox_("��ȡ������ϸʧ��");
            return;
        }
        int count = detailParm.getCount();
        if (count < 1) {
            this.messageBox_("û�з�����ϸ����");
            return;
        }
        // ȡ��Ҫ�����SQL
        String[] sql = getOnFeeSql(detailParm);
        if (sql == null || sql.length < 1) {
            this.messageBox_("ȡ������ʧ��");
            onRefreshUI();// add by wanglong 20130329
            return;
        }
        // �ͺ�̨����
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge", inParm);
        // ��鱣����
        if (result.getErrCode() != 0) {
            this.messageBox("����ʧ�� " + result.getErrText());
            return;// add by wanglong 20130118
        } else {
            this.messageBox("P0001");// ����ɹ�
        }
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {//������Ʊ
            String[] receipt = new String[]{receiptNo };
            dealPrintData(receipt,
                          ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE")).getText(),
                          this.getValueString("DEPT_CODE_COM"), this.getValueString("SEX_CODE"));
        }
        onRefreshUI();// add by wanglong 20130329
        // onClear();
    }
    
    /**
     * �����ӡ����
     * @param receiptNo String[]
     * @param patName String
     * @param deptCode String
     * @param sexCode String
     */
    public void dealPrintData(String[] receiptNo, String patName, String deptCode, String sexCode) {
        int size = receiptNo.length;
        for (int i = 0; i < size; i++) {
            // ȡ��һ��Ʊ�ݺ�
            String recpNo = receiptNo[i];
            if (recpNo == null || recpNo.length() == 0) return;
            // System.out.println("recpNo="+recpNo);
            // ���ô�ӡһ��Ʊ�ݵķ���
            onPrint(OPBReceiptTool.getInstance().getOneReceipt(recpNo), patName, deptCode, sexCode);
        }
    }
    
    /**
     * ��ӡƱ��
     * @param recpParm TParm
     * @param patName String
     * @param deptCode String
     * @param sexCode String
     */
    public void onPrint(TParm recpParm, String patName, String deptCode, String sexCode) {
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
        oneReceiptParm.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance()
                .getDate(), "yyyy/MM/dd HH:mm:ss"));
        oneReceiptParm.setData("PRINT_NO", recpParm.getData("PRINT_NO", 0));
        // BILL_OPB_RECT���������MR_NO�ǹ�˾����(COMPANY_CODE)��CASE_NO�Ǻ�ͬ����(CONTRACT_CODE)
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
                               StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
        oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance().numberToWord(StringTool.round(recpParm.getDouble("AR_AMT", 0), 2)));
        oneReceiptParm.setData("OPT_USER", "TEXT", "����Ա:" + Operator.getName());
        String printDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd");
        oneReceiptParm.setData("PRINT_DATE", "TEXT", printDate);
        oneReceiptParm.setData("YEAR", "TEXT", printDate.substring(0, 4));
        oneReceiptParm.setData("MONTH", "TEXT", printDate.substring(5, 7));
        oneReceiptParm.setData("DAY", "TEXT", printDate.substring(8, 10));
        oneReceiptParm.setData("OPT_ID", "TEXT", Operator.getID());
        
        //============================   chenxi modify 20130219 ========
        if(recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0)==0.0)
          oneReceiptParm.setData("CHARGE01", "TEXT","");
        else  oneReceiptParm.setData("CHARGE01", "TEXT",
                recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0));
        
        String[] array = {"CHARGE03","CHARGE04","CHARGE05","CHARGE06","CHARGE07","CHARGE08","CHARGE09",
                          "CHARGE13","CHARGE14","CHARGE15","CHARGE16","CHARGE17","CHARGE18"} ;
        for(int i=0;i<array.length;i++){
            if( recpParm.getData(array[i], 0).equals(0.0))
                 oneReceiptParm.setData(array[i], "TEXT","");
            else
                 oneReceiptParm.setData(array[i], "TEXT",
                         recpParm.getData(array[i], 0));
        }
        // ======================= chen xi modify 20130219============
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
        oneReceiptParm.setData("USER_NAME", "TEXT", "" + Operator.getID());// modify by wanglong
                                                                           // 20130123
        oneReceiptParm.setData("OPT_DATE", "TEXT", printDate);
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw", oneReceiptParm);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrintForHRM.class", oneReceiptParm));//����ϲ�modify by wanglong 20130730
    }
    
    /**
     * ȡ���շ��õ���SQL���
     * @param detailParm TParm
     * @return String[]
     */
    private String[] getOnFeeSql(TParm detailParm) {//modify by wanglong 20130510
        String[] sql = new String[]{};
        if (detailParm == null) {
            return null;
        }
        int count = detailParm.getCount();
        if (count < 1) {
            return null;
        }
        String flagSql = "SELECT * FROM HRM_BILL WHERE BILL_NO IN (#)";// modify by wanglong 20130510
        flagSql = flagSql.replaceFirst("#", billNoList);
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵�״̬���� " + flagParm.getErrText());
            return null;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("���˵�������");
            return null;
        } else if (flagParm.getCount() != billNoList.split(",").length) {//add by wanglong 20130510
            this.messageBox("�˵�����㵥��������ƥ��");
            return null;
        }
        if (flagParm.getValue("REXP_FLG", 0).equals("Y")) {
            this.messageBox("���˵��ѽɷ�");
            return null;
        } else if (!StringUtil.isNullString(flagParm.getValue("RECEIPT_NO", 0))) {
            this.messageBox("���˵��ѽɷѣ���δ��Ʊ");
            return null;
        }
        // ȡ��ԭ��õ�Ʊ�ݺ�
        receiptNo = SystemTool.getInstance().getNo("ALL", "OPB", "RECEIPT_NO", "RECEIPT_NO");
        // �շѶ����ʼ������ֵ
        receipt.onQuery();
        // ���屣�浽BIL_OPB_RECP�е�MR_NO��COMPANY_CODE,CASE_NO��CONTRACT_CODE
        String mrNo = billParm.getValue("COMPANY_CODE", billTable.getSelectedRow());
        String caseNo = billParm.getValue("CONTRACT_CODE", billTable.getSelectedRow());
        TParm orderAmtParm = order.getTParmByBillNoList(billNoList);//modify by wanglong 20130510
        if (orderAmtParm == null) {
            this.messageBox_("�����Ϊ��");
            return null;
        }
        if (orderAmtParm.getErrCode() != 0) {
            this.messageBox_("ȡ���˵���Ӧҽ����Ϣʧ��");
            return null;
        }
        String noPrint = this.getValueString("NOPRINT");
        // �������ӡ����д��Ʊ��
        if (!"Y".equals(noPrint)) {//��ӡ��Ʊ
            receipt.insertForBill(orderAmtParm, receiptNo, caseNo, mrNo, updateNo, //modify by wanglong 20130716����֧Ʊ��
                                  this.getValueString("PAY_TYPE"), this.getValueString("CHECK_NO") + " " + this.getValueString("PAY_REMARK"));
        } else {//����ӡ��Ʊ
            receipt.insertForBill(orderAmtParm, receiptNo, caseNo, mrNo, "", //modify by wanglong 20130716����֧Ʊ��
                                  this.getValueString("PAY_TYPE"), this.getValueString("CHECK_NO") + " " + this.getValueString("PAY_REMARK"));
        }
        sql = receipt.getUpdateSQL();
        // Ʊ�ݶ����ʼ������ֵ
        if (!"Y".equals(noPrint)) {// ������Ʊ
            invRcp.onQuery();
            // System.out.println("3---"+SystemTool.getInstance().getDate());
            TParm invRcpParm = new TParm();
            invRcpParm.setData("RECP_TYPE", "OPB");
            invRcpParm.setData("INV_NO", updateNo);
            invRcpParm.setData("RECEIPT_NO", receiptNo);
            invRcpParm.setData("AR_AMT", receipt.getItemDouble(receipt.rowCount() - 1, "AR_AMT"));
            invRcpParm.setData("STATUS", "0");
            invRcp.insert(invRcpParm);
            // System.out.println("wrong sql"+invRcp.getUpdateSQL()[0]);
            sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
            // ����Ʊ�ݵ���SQL
            String sqlUpdate =
                    "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
            sqlUpdate =
                    sqlUpdate.replaceFirst("#",
                                           StringTool.addString(this.getValueString("UPDATE_NO")));
            sqlUpdate = sqlUpdate.replaceFirst("#", bilInvoice.getStartInvno());
            // System.out.println("sqlUPdate="+sqlUpdate);
            String[] temp = new String[]{sqlUpdate };
            sql = StringTool.copyArray(sql, temp);
        }
        for (int i = 0; i < detailParm.getCount("MR_NO"); i++) {// add by wanglong 20130324
            String updateDSql =
                    "UPDATE HRM_CONTRACTD B SET RECEIPT_NO='#',BILL_NO='#',BILL_FLG = '1' WHERE CONTRACT_CODE='#' AND MR_NO='#'";
            updateDSql = updateDSql.replaceFirst("#", receiptNo);
            updateDSql = updateDSql.replaceFirst("#", detailParm.getValue("BILL_NO", i));
            updateDSql = updateDSql.replaceFirst("#", detailParm.getValue("CONTRACT_CODE", i));
            updateDSql = updateDSql.replaceFirst("#", detailParm.getValue("MR_NO", i));
            String updatePatAdmSql = "UPDATE HRM_PATADM SET BILL_FLG = '1' WHERE CASE_NO = '#'";
            updatePatAdmSql = updatePatAdmSql.replaceFirst("#", detailParm.getValue("CASE_NO", i));
            String[] temp = new String[]{updateDSql, updatePatAdmSql };
            sql = StringTool.copyArray(sql, temp);
        }
        // ѭ��ҽ��ϸ�����Ŀ����
        for (int i = 0; i < count; i++) {
            caseNo = detailParm.getValue("CASE_NO", i);
            mrNo = detailParm.getValue("MR_NO", i);
            if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
                this.messageBox_("�� " + i + " ��ҽ�����ݴ���");
                return null;
            }
        }
        // ҽ������ĳ�ʼ������ֵ
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String orderSql = "UPDATE HRM_ORDER SET RECEIPT_NO='#',PRINT_FLG='Y' WHERE BILL_NO IN (#)";
        orderSql = orderSql.replaceFirst("#", receiptNo);
        orderSql = orderSql.replaceFirst("#", billNoList);
        sql = StringTool.copyArray(sql, new String[]{orderSql });
        String id = Operator.getID();
        String ip = Operator.getIP();
        if (bill.rowCount() < 1) {
            this.messageBox("��ý������ݳ���");
            return null;
        }
        for (int i = 0; i < bill.rowCount(); i++) {//add by wanglong 20130510
            bill.setItem(i, "REXP_FLG", "Y");
            bill.setItem(i, "RECEIPT_NO", receiptNo);
            bill.setItem(i, "OPT_USER", id);
            bill.setItem(i, "OPT_DATE", now);
            bill.setItem(i, "OPT_TERM", ip);
        }
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        return sql;
    }

    /**
     * ���ݲ����Ų�ѯ
     */
    public void onMrNO() {// ��ʱ����û��ʹ��
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        // ȡ��MR_NO�ĳ���
        TParm mrParm =
                new TParm(TJDODBTool.getInstance()
                        .select("SELECT MAX(MR_NO) MR_NO FROM SYS_PATINFO"));
        if (mrParm.getErrCode() != 0) {
            this.messageBox_("ȡ�ò����ų���ʧ��");
            return;
        }
        int mrLength = mrParm.getValue("MR_NO", 0).length();
        if (mrLength < 1) {
            this.messageBox_("ȡ�ò����ų��ȴ���");
            return;
        }
        // MR_NO�Զ����㣬��ֵ��������
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ========= chenxi
        this.setValue("MR_NO", mrNo);
//        int result = bill.onQueryByMrNo(mrNo);
        billTable.setDataStore(bill);
        billTable.setDSValue();
    }

    /**
     * �����¼�,��2���ؼ���Ǯ�������ֵ������Ŀؼ�����ʾ������Ŀ
     */
    public void onTotAmt() {
        Double totAmt = TypeTool.getDouble(this.getValue("TOT_AMT"));
        Double arAmt = TypeTool.getDouble(this.getValue("AR_AMT"));
        if (totAmt < arAmt) {
            this.setValue("TOT_AMT", 0.0);
            this.callFunction("UI|TOT_AMT|grabFoces");
            return;
        }
        double charge = totAmt - arAmt;
        this.setValue("CHARGE", charge);
        this.callFunction("UI|BUT_FEE|grabFocus");
    }

    /**
     * ��ӡOPB_RECEIPT����PRINT_NO��PRINT_DATE��BIL_INVRCP����ԭ�ȵ�CANCEL_FLGΪ1��CANCEL_USER,CANCEL_DATE�����²���һ��
     */
    public void onRePrint() {
        if (billTable.getSelectedRow() < 0) {//modify by wanglong 20130510
            this.messageBox("��ѡ��һ���˵�");
            return;
        }
        bilInvoice = new BilInvoice("OPB");
        String updateNo = getNextUpdateNo();
        if (StringUtil.isNullString(updateNo)) {
            return;
        }
        String receiptNo = billParm.getValue("RECEIPT_NO", billTable.getSelectedRow());//modify by wanglong 20130510
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
        if (!receipt.onQueryByReceiptNo(receiptNo)) {
            this.messageBox_("��ʼ���վ�ʧ��");
            return;
        }
        String oldPrintNo = receipt.getItemString(0, "PRINT_NO");
        if (oldPrintNo == null || oldPrintNo.equals("") || oldPrintNo.equals("null")) {
            this.messageBox("���ܲ�ӡ,û�д�ӡ��Ʊ��");
            return;
        }
        String[] receiptNos = new String[]{receiptNo };
//        String patName = this.getValueString("PAT_NAME");
        String sexCode = this.getValueString("SEX_CODE");
        if (!receipt.onRePrint(receipt.getItemString(0, "CASE_NO"), receiptNo, updateNo)) {
            this.messageBox_("��дƱ��ʧ��");
            return;
        }
        if (!invRcp.onRePrint(oldPrintNo, updateNo)) {
            this.messageBox_("��д��Ʊʧ��");
            return;
        }
        String[] sql = receipt.getUpdateSQL();
        sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
        String[] tempSql = new String[]{getBilInvoiceUpdate() };
        sql = StringTool.copyArray(sql, tempSql);
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMPersonReportAction", "onSave", inParm);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("E0001");
            return;
        }
        dealPrintData(receiptNos,
                      ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE")).getText(),
                      Operator.getDept(), sexCode);
        onClear();
    }

    /**
     * ��ӡʱ����Ʊ�ݵ��ĸ������
     * 
     * @return String
     */
    private String getBilInvoiceUpdate() {
        String sqlUpdate =
                "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
        sqlUpdate =
                sqlUpdate.replaceFirst("#", StringTool.addString(bilInvoice.getUpdateNo()))
                        .replaceFirst("#", bilInvoice.getStartInvno());
        // System.out.println("sqlUpdate=" + sqlUpdate);
        return sqlUpdate;
    }

    /**
     * ȡ����һƱ��
     * 
     * @return String
     */
    private String getNextUpdateNo() {
        String updateNo = bilInvoice.getUpdateNo();
        this.setValue("UPDATE_NO", updateNo);
        // ��˵�ǰƱ��
        if (updateNo.length() == 0 || updateNo == null) {
            this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
            return "";
        }
        if (updateNo.equals(bilInvoice.getEndInvno())) {
            this.messageBox_("���һ��Ʊ��!");
        }
        if (StringTool.bitDifferOfString(updateNo, bilInvoice.getEndInvno()) < 0) {
            this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
            return "";
        }
        return updateNo;
    }

    /**
     * �˷ѣ����˷�ҽ��ɾ������BIL_OPB_RECEIPT�����ݱ�ɸ�������һ���µ����ݣ����¾ɵ�BIL_INVRCP���ݵ�CANCEL_FLGΪ2��CANCEL_USER,CANCEL_DATE
     */
    public void onDelete() {
        bilInvoice = new BilInvoice("OPB");
        if(!this.getValueBoolean("FEE")){//add by wanglong 20130510
            this.messageBox("��ѡ���ѽɷѵ��˵�");
            return;
        }
        if (billTable.getSelectedRow()<0) {
            this.messageBox_("��ѡ��һ���վ��˷�");
            return;
        }
        TParm detailParm = billDetailTable.getParmValue();
        if (detailParm == null) {
            this.messageBox_("��ȡ������ϸʧ��");
            return;
        }
        if (detailParm.getCount() < 1) {
            this.messageBox_("û�з�����ϸ����");
            return;
        }
        String receiptNo = billParm.getValue("RECEIPT_NO", billTable.getSelectedRow());
        if (StringUtil.isNullString(receiptNo)) {
            this.messageBox("ȡ���˵���ʧ��");
            return;
        }
        billNoList="";
        for (int i = 0; i < billParm.getCount(); i++) {//add by wanglong 20130510
            if (billTable.getItemString(i, "FLG").equals("Y")&& StringUtil.isNullString( billParm.getValue("BILL_NO", i))) {
                this.messageBox_("���㵥��Ϊ��");
                return;
            }else if(billTable.getItemString(i, "FLG").equals("Y")){
                billNoList+="'"+billParm.getValue("BILL_NO", i)+"',";
            }
        }
        billNoList = billNoList.substring(0, billNoList.length() - 1);
        String flagSql = "SELECT * FROM HRM_BILL WHERE BILL_NO IN (#)";// add by wanglong 20130329
        flagSql = flagSql.replaceFirst("#", billNoList);
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵�״̬���� " + flagParm.getErrText());
            return;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("���˵�������");
            onContractChoose();
            return;
        } else if (flagParm.getCount() != billNoList.split(",").length) {//add by wanglong 20130510
            this.messageBox("�˵�����㵥��������ƥ��");
            return;
        }
        if (StringUtil.isNullString(flagParm.getValue("RECEIPT_NO", 0))) {
            this.messageBox("���˵�δ�ɷѣ������˷�");
            return;
        } else if (!flagParm.getValue("REXP_FLG", 0).equals("Y")) {
            this.messageBox("���˵�δ��Ʊ�������˷�");
            return;
        }
        order = new HRMOrder();
        order.onQueryByReceiptNo(receiptNo);
        int count = order.rowCount();
        if (count <= 0) {
            this.messageBox_("ȡ��ҽ������ʧ��");
            return;
        }
        if (!order.deleteMedApplyByAppNo()) {// ����Hrm_order��״̬RECEIPT_NO=''
            this.messageBox_("ɾ��ҽ��ʧ��");
            return;
        }
        this.setValue("PAY_TYPE", billParm.getValue("PAY_TYPE",billTable.getSelectedRow()));//add by wanglong 20130313
        receipt = new HRMOpbReceipt();
        receipt.onQueryByReceiptNo(receiptNo);
        String oldPrintNo = receipt.getItemString(0, "PRINT_NO");
        if (oldPrintNo.equals("")) {// add by wanglong 20130221
            messageBox("���վ�δ��Ʊ�������˷�");
            return;
        }
        receipt.onDisCharge(0, oldPrintNo, this.getValueString("PAY_TYPE"));// ��һ����ֵ����
        int[] insertRows = receipt.getNewRows();
        receipt.setItem(0, "RESET_RECEIPT_NO", receipt.getItemString(insertRows[0], "RECEIPT_NO"));// modify by wanglong 20130221
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
        Timestamp now = bill.getDBTime();
        for (int i = 0; i < bill.rowCount(); i++) {//modify by wanglong 20130510
            bill.setItem(i, "RECEIPT_NO", "");
            bill.setItem(i, "REXP_FLG", "N");
            bill.setItem(i, "OPT_USER", Operator.getID());
            bill.setItem(i, "OPT_DATE", now);
            bill.setItem(i, "OPT_TERM", Operator.getIP());
            bill.setActive(i, true);
        }
        String[] sql = receipt.getUpdateSQL();
        sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
        sql = StringTool.copyArray(sql, order.getUpdateSQL());
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        String updateDSql =
                "UPDATE HRM_CONTRACTD SET RECEIPT_NO='',BILL_FLG = '0' WHERE RECEIPT_NO='#'";
        updateDSql = updateDSql.replaceFirst("#", receiptNo);
        sql = StringTool.copyArray(sql, new String[]{updateDSql });
        for (int i = 0; i < detailParm.getCount("MR_NO"); i++) {// add by wanglong 20130324
            String updatePatAdmSql = "UPDATE HRM_PATADM SET BILL_FLG = '0' WHERE CASE_NO = '#'";
            updatePatAdmSql = updatePatAdmSql.replaceFirst("#", detailParm.getValue("CASE_NO", i));
            sql = StringTool.copyArray(sql, new String[]{updatePatAdmSql });
        }
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge", inParm);
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
        onRefreshUI();// add by wanglong 20130329
//        onClear();
    }
    
    /**
     * ��ѯ������ϸ
     */
    public void onReceiptDetail() {
        if (billTable.getSelectedRow() < 0) {//modify by wanglong 20130510
            this.messageBox("��ѡ��һ���˵�");
            return;
        }
        TParm parm = billParm.getRow(billTable.getSelectedRow());//modify by wanglong 20130510
        if (parm == null) {
            this.messageBox("�ӽ����ȡ����ʧ��");
            return;
        }
        if (parm.getValue("RECEIPT_NO").equals("")) {
            this.messageBox("û���˵��ţ�δ�ɷѣ������ܲ鿴�˵���ϸ");
            return;
        }
        TParm result = OPBReceiptTool.getInstance().getOneReceipt(parm.getValue("RECEIPT_NO"));
        if (result.getErrCode() != 0) {
            this.messageBox("��ѯ�˵���Ϣʧ��");
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("�˵���Ϣ������");
            return;
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMReceiptDetail.x", result);
        onRefreshUI();// add by wanglong 20130329
    }
}
