package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hrm.HRMBill;
import jdo.hrm.HRMCheckFeeTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ������� </p>
 * 
 * <p> Description: ������� </p>
 * 
 * <p> Copyright: javahis 20090922 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMDepositControl
        extends TControl {

    // ҽ��TABLE���˵�TAABLE
    private TTable patTable, orderTable, billTable, billDetailTable;
    // ���塢��ͬTTextFormat
    private TTextFormat company, contract;
    // ��ͬϸ�����
    private HRMContractD contractD;
    // ҽ������
    private HRMOrder order;
    // �˵�����
    private HRMBill bill;
    // �������˵���CASEnO
    private List<String> caseNos;
    // ��Ա�б�SQL add by wanglong 20130314
    private static final String PAT_SQL = // modify by wanglong 20130419
            "SELECT * FROM (SELECT 'N' AS FLG,A.COMPANY_CODE,A.CONTRACT_CODE,A.CONTRACT_DESC,A.PACKAGE_CODE,"
                    + "       A.COVER_FLG,A.REAL_CHK_DATE REPORT_DATE,B.CASE_NO,A.MR_NO,A.SEQ_NO,A.PAT_NAME,A.PAT_DEPT,"
                    + "       A.DISCNT,SUM(B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) AR_AMT,"
                    + "       CASE WHEN B.RECEIPT_NO IS NULL AND B.BILL_NO IS NULL THEN 0 "// δ����
                    + "            WHEN B.RECEIPT_NO IS NULL AND B.BILL_NO IS NOT NULL THEN 1 "// �ѽ���
                    + "            WHEN B.RECEIPT_NO IS NOT NULL THEN -1 "// �ѽɷ�
                    + "        END BILL_FLG "
                    + "  FROM HRM_CONTRACTD A, HRM_ORDER B "
                    + " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                    + "   AND A.MR_NO = B.MR_NO "
                    + " @ # $ "
                    + "GROUP BY A.COMPANY_CODE,A.CONTRACT_CODE,A.CONTRACT_DESC,A.COVER_FLG,B.CASE_NO,A.MR_NO,A.SEQ_NO,"
                    + "         A.PAT_NAME,A.REAL_CHK_DATE,A.PACKAGE_CODE,A.PAT_DEPT,A.DISCNT,A.BILL_FLG,B.RECEIPT_NO,B.BILL_NO) "
                    + " WHERE 1=1 % ORDER BY SEQ_NO";
    private BILComparator compare = new BILComparator();// add by wanglong 20130419
    private boolean ascending = false;
    private int sortColumn = -1;
    private TNumberTextField arAmt;// add by wanglong 20130423
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ���
        onClear();
    }

    /**
     * ��ʼ������
     */
    public void initData() {
        contractD = new HRMContractD();
        order = new HRMOrder();
        order.onQuery();
        bill = new HRMBill();
        arAmt.setValue(0);// add by wanglong 20130423
    }

    /**
     * ����¼�
     */
    public void onPartClear() {// add by wanglong 20130408
        this.clearValue("ALL_CHOOSE;DISCNT;START_SEQ;END_SEQ;PAT_COUNT");
        orderTable.removeRowAll();
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        caseNos = new ArrayList<String>();
        // ��ʼ������
        initData();
    }
    
    /**
     * ����¼�
     */
    public void onClear() {
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("COVER_FLG_COMBO", "");// add by wanglong 20130419
        this.setValue("BILL_FLG_COMBO", "");
        this.clearValue("ALL_CHOOSE;ALL;MR_NO;PAT_NAME;DISCNT;START_SEQ;END_SEQ;PAT_COUNT");// modify by wanglong 20130227
        this.callFunction("UI|REPORT|setSelected", true);
        orderTable.removeRowAll();
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        patTable.removeRowAll();
        caseNos = new ArrayList<String>();
        // ��ʼ������
        initData();
        ((TTextField) this.getComponent("MR_NO")).requestFocus();
        this.callFunction("UI|COVER_FLG_COMBO|setValue", "ALL");//add by wanglong 20130423
        this.callFunction("UI|BILL_FLG_COMBO|setValue", "N");
    }

    /**
     * ��ʼ���ؼ�
     */
    public void initComponent() {
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        patTable = (TTable) this.getComponent("PAT_TABLE");
        orderTable = (TTable) this.getComponent("ORDER_TABLE");
        billTable = (TTable) this.getComponent("BILL_TABLE");
        billDetailTable = (TTable) this.getComponent("BILL_DETAIL_TABLE");
        patTable.addEventListener("PAT_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                  "onPatValueChange");// ��Ա�б�ѡ�¼�
        patTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onPatCheckBox");// �����Ա����ʾ��ҽ��
        addSortListener(patTable);// add by wanglong 20130419
        // orderTable.addEventListener("ORDER_TABLE->" + TTableEvent.CHANGE_VALUE, this,
        // "onOrderValueChange");//��ʱû����
        // orderTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
        // "onOrderCheckBox");//��ʱû����
        billTable.addEventListener("BILL_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                   "onBillValueChange");
        arAmt = (TNumberTextField) this.getComponent("AR_AMT");
    }

    /**
     * ��Ա�б�Ԫ���޸��¼�
     * 
     * @param tNode
     *            TTableNode
     * @return boolean
     */
    public boolean onPatValueChange(TTableNode tNode) {
        // int row = tNode.getTable().getSelectedRow();
        // TParm parm = tNode.getTable().getParmValue().getRow(row);
        // String caseNo = parm.getValue("CASE_NO");
        int column = tNode.getColumn();
        String colName = patTable.getParmMap(column);
        if ("FLG".equals(colName)) {
            if ("Y".equals(tNode.getValue())) {
                return false;
            }
        }
        if ("DISCNT".equals(colName)) {// add by wanglong 20130306
            String nodeStr = tNode.getValue().toString();
            if (!nodeStr.matches("\\p{Digit}+(\\056?\\p{Digit}+)?")) {
                this.messageBox("����������");
                return true;
            }
            double discnt = StringTool.getDouble(nodeStr);
            if (discnt < 0 || discnt > 1) {
                this.messageBox("�ۿ�ֻ����0��1֮��");
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * ��Ա�б�ѡ�¼�����ʾ�˵�
     * 
     * @param obj
     * @return
     */
    public boolean onPatCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        this.showBill();
        return false;
    }

    /**
     * ��Ա�б����¼�����ʾ����Ա��ҽ��
     */
    public void showOrderTable() {
        // ================modify by by wanglong 20130227
        // String companyCode=this.getValueString("COMPANY_CODE");
        // String contractCode=this.getValueString("CONTRACT_CODE");
        int row = patTable.getSelectedRow();
        String caseNo = patTable.getParmValue().getValue("CASE_NO", row);
        String companyCode = patTable.getParmValue().getValue("COMPANY_CODE", row);
        String contractCode = patTable.getParmValue().getValue("CONTRACT_CODE", row);
        // this.setValue("CONTRACT_CODE", "");
        contractD = new HRMContractD();
        company.setValue(companyCode);
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("δ��ѯ��������ĺ�ͬ��Ϣ");
            return;
        }
        contract.setValue(contractCode);
        // ================modify end
        if (row < 0) {
            return;
        }
        TParm orderParm = order.getDepositParm(companyCode, contractCode, caseNo);
        if (orderParm == null) {
            // this.messageBox_("ererer");
            return;
        }
        if (orderParm.getErrCode() != 0) {
            this.messageBox("ȡ������ʧ��");
            return;
        }
        orderTable.setParmValue(orderParm);
    }

    /**
     * ҽ����ϸֵ�ı��¼�
     * 
     * @param tNode
     */
    public boolean onOrderValueChange(TTableNode tNode) {
        int row = tNode.getRow();
        TParm orderParm = orderTable.getParmValue();
        if (orderParm == null) {
            return true;
        }
        int count = orderParm.getCount();
        if (count <= 0) {
            return true;
        }
//        String caseNo = orderParm.getValue("CASE_NO", row);
        String orderCode = orderParm.getValue("ORDER_CODE", row);
//        int orderGroupNo = orderParm.getInt("ORDERSET_GROUP_NO", row);
        boolean setMainFlg = TypeTool.getBoolean(orderParm.getData("SETMAIN_FLG", row));
        String billFlg = TypeTool.getBoolean(tNode.getValue()) ? "Y" : "N";
        int column = tNode.getColumn();
        String colName = orderTable.getParmMap(column);
        if ("BILL_FLG".equalsIgnoreCase(colName)) {
            if (setMainFlg) {
                for (int i = row; i < count; i++) {
                    if (!orderCode.equalsIgnoreCase(orderParm.getValue("ORDERSET_CODE", i))) {
                        // this.messageBox_("break");
                        break;
                    }
                    orderParm.setData("BILL_FLG", i, billFlg);
                    // this.messageBox_("in billflg");
                }
            }
            double amt = orderTable.getItemDouble(row, "AR_AMT");
            // double amt=order.getAmt(caseNo, orderCode,
            // orderGroupNo,billFlg,bill.getItemString(bill.rowCount()-1, "BILL_NO"));
            // this.messageBox_(amt);
            if (TypeTool.getBoolean(tNode.getValue())) {
                bill.setItem(bill.rowCount() - 1, "OWN_AMT",
                             bill.getItemDouble(bill.rowCount() - 1, "OWN_AMT") + amt);
                // this.messageBox_("herer");
            } else {
                double originalAmt = bill.getItemDouble(bill.rowCount() - 1, "OWN_AMT");
                double currentAmt = originalAmt - amt;
                bill.setItem(bill.rowCount() - 1, "OWN_AMT", currentAmt);
            }
            billTable.setDSValue();
            orderTable.setParmValue(orderParm);
            return false;
        }
        return true;
    }

    /**
     * ҽ���б�checkBox�¼�
     * 
     * @param obj
     * @return
     */
    public boolean onOrderCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return false;
    }

    /**
     * �˵��б�ֵ�ı��¼�
     * 
     * @param tNode
     * @return
     */
    public boolean onBillValueChange(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = billTable.getParmMap(column);
        if (TypeTool.getBoolean(bill.getItemData(row, "REXP_FLG"))) {
            this.messageBox_("�ѽ����˵������޸�");
            return true;
        }
        if (StringUtil.isNullString(bill.getItemString(row, "BILL_NO"))) {
            this.messageBox_("���˵����˵������޸�");
            return true;
        }
        if ("CUT_AMT".equalsIgnoreCase(colName)) {
            double cutAmt = TypeTool.getDouble(tNode.getValue());
            if (cutAmt <= 0) {
                return true;
            }
            bill.setActive(row, true);
            return false;
        }
        if ("DISCOUNT_RATE".equalsIgnoreCase(colName)) {
            double rate = TypeTool.getDouble(tNode.getValue());
            if (rate <= 0 || rate >= 1) {
                this.messageBox_("�ۿ���Ӧ��0~1֮��");
                return true;
            }
            bill.setActive(row, true);
            return false;
        }
        if ("CUT_DESCRIPTION".equalsIgnoreCase(colName)
                || "DISCOUNT_DESCRIPTION".equalsIgnoreCase(colName)) {
            String value = TypeTool.getString(tNode.getValue());
            if (StringUtil.isNullString(value)) {
                return true;
            }
            bill.setActive(row, true);
            return false;
        }
        return true;
    }

    /**
     * BILL_TABLE�����¼�
     */
    public void onBillTableClick() {
        int row = billTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int count = bill.rowCount();
        if (count <= 0) {
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("ȡ�ý��㵥��ʧ��");
            return;
        }
        // ��ʾ��bill_no��ҽ����û������ʾ
        TParm parm = order.getParmBybillNo(billNo);
        if (parm == null) {
            // this.messageBox_("ȡ������ʧ��");
            billDetailTable.removeRowAll();
            return;
        }
        if (parm.getErrCode() != 0) {
            this.messageBox_("ȡ�ý��㵥��" + billNo + "��Ӧҽ������ʧ��");
            return;
        }
        billDetailTable.setParmValue(parm);
    }

    /**
     * ȫѡ�¼�
     */
    public void onClickAll() {// ��ʱû��
        if (orderTable.getRowCount() <= 0) {
            return;
        }
        String isAll = TypeTool.getBoolean(this.getValue("ALL")) ? "Y" : "N";
        TParm orderParm = orderTable.getParmValue();
        int count = orderParm.getCount();
        if (count <= 0) {
            return;
        }
        orderTable.acceptText();
        double amt = 0;
        double ownamt = 0;
        for (int i = 0; i < count; i++) {
            if (!orderParm.getBoolean("SETMAIN_FLG", i)) {
                continue;
            }
            if (this.getValueBoolean("ALL") && orderParm.getBoolean("BILL_FLG", i)) {
                continue;
            }
//            String caseNo = orderParm.getValue("CASE_NO", i);
            String orderCode = orderParm.getValue("ORDER_CODE", i);
//            int orderGroupNo = orderParm.getInt("ORDERSET_GROUP_NO", i);
            //
            // double amt=order.getAmt(caseNo, orderCode,
            // orderGroupNo,isAll,bill.getItemString(bill.rowCount()-1, "BILL_NO"));
            // Ӧ����
            amt += orderTable.getItemDouble(i, "AR_AMT");
            // �ܼ�
            ownamt += orderTable.getItemDouble(i, "OWN_AMT");
            for (int j = i; j < count; j++) {
                if (!orderCode.equalsIgnoreCase(orderParm.getValue("ORDERSET_CODE", j))) {
                    break;
                }
                orderParm.setData("BILL_FLG", j, isAll);
            }
        }
        if (TypeTool.getBoolean(isAll)) {
            bill.setItem(bill.rowCount() - 1, "OWN_AMT", ownamt); // ԭ�ܼ�
            bill.setItem(bill.rowCount() - 1, "AR_AMT", amt); // �ۿۺ��ܼ�
            bill.setItem(bill.rowCount() - 1, "DISCOUNT_AMT", ownamt - amt); // ����ܼ�
        } else {
            bill.setItem(bill.rowCount() - 1, "OWN_AMT", ownamt);
            bill.setItem(bill.rowCount() - 1, "AR_AMT", amt);
            bill.setItem(bill.rowCount() - 1, "DISCOUNT_AMT", ownamt - amt);
        }
        orderTable.setParmValue(orderParm);
        billTable.setDSValue();
    }

    /**
     * ȫѡ����
     */
    public void onAllPat() {
        TParm parm = patTable.getParmValue();
        int rowCount = parm.getCount();
        if (rowCount <= 0) {
            this.messageBox("����Ա��Ϣ");
            return;
        }
        if (this.getValueString("ALL_CHOOSE").equals("Y")) {// add by wanglong 20130417
            if (rowCount == 1) {
                patTable.setSelectedRow(0);
                showOrderTable();
            } else {
                Set<String> conSet = new HashSet<String>();
                for (int i = 0; i < parm.getCount(); i++) {
                    conSet.add(parm.getValue("CONTRACT_CODE", i));
                }
                if (conSet.size() > 1) {
                    this.messageBox("��ͬ��ͬ�µ���Ա���������ͬһ���˵���");
                    this.setValue("ALL_CHOOSE", "N");
                    for (int i = 0; i < rowCount; i++) {
                        parm.setData("FLG", i, "N");
                    }
                }
            }
        }
        for (int i = 0; i < rowCount; i++) {
            parm.setData("FLG", i, this.getValueString("ALL_CHOOSE"));
        }
        patTable.setParmValue(parm);
        if (this.getValueString("ALL_CHOOSE").equals("N")) {// add by wanglong 20130417
            patTable.setParmValue(parm);
            orderTable.removeRowAll();
            billTable.removeRowAll();
            billDetailTable.removeRowAll();
            caseNos = new ArrayList<String>();
            bill = new HRMBill();
            return;
        }
        this.showBill();
    }

    /**
     * ��������ѡ�¼�������ѡ����������룬��ʼ��������ĺ�ͬ��ϢTTextFormat������ʼ���˵�����
     */
    public void onCompanyCodeChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("û������");
        }
        // // System.out.println("contractParm="+contractParm);
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("δ��ѯ��������ĺ�ͬ��Ϣ");
            return;
        }
        contract.setValue(contractCode);
        this.clearValue("MR_NO;PAT_NAME");// modify by wanglong 20130227
        // ��ѯ
        onContractCodeChoose();
    }

    /**
     * ��ͬ�����ѡ�¼������ò�ѯ�¼�
     */
    public void onContractCodeChoose() {// modify by wanglong 20130314
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        String sql = PAT_SQL + "";
        if (!companyCode.equals("")) {
            sql = sql.replaceFirst("@", " AND A.COMPANY_CODE = '" + companyCode + "' ");
        } else {
            sql = sql.replaceFirst("@", " ");
        }
        if (!contractCode.equals("")) {
            sql = sql.replaceFirst("#", " AND A.CONTRACT_CODE = '" + contractCode + "' ");
        } else {
            sql = sql.replaceFirst("#", " ");
        }
//        if (this.getValueBoolean("UNREPORT")) {// add by wanglong 20130314
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
//        } else if (this.getValueBoolean("REPORT")) {
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
//        } else if (this.getValueBoolean("ALL")) {
//            sql = sql.replaceFirst("\\$", " ");
//        }
        if (this.getValue("COVER_FLG_COMBO").equals("ALL")
                || this.getValue("COVER_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("\\$", " ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
        }
        if (this.getValue("BILL_FLG_COMBO").equals("ALL")
                || this.getValue("BILL_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("%", " ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG >= 0 ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG = -1 ");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("��ѯ��Ա�б�ʧ��");
            return;
        }
        onPartClear();// add by wanglong 20130408
        patTable.setParmValue(result);
        if (result.getCount() >= 0) {
            this.setValue("PAT_COUNT", result.getCount() + "��");
        } else this.setValue("COUNT", "");
//        order.onQueryByContractCode(contractCode);// ��ʼ��order��Ϣ
        // onQuery();
    }

    /**
     * �����Ų�ѯ
     */
    public void onQueryByMr() {// modify by wanglong 20130314
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        this.setValue("MR_NO", mrNo);
        String mrSql = "SELECT * FROM HRM_CONTRACTD WHERE MR_NO = '" + mrNo + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(mrSql));
        if (parm.getErrCode() != 0) {
            this.messageBox(parm.getErrCode() + parm.getErrText());
            return;
        }
        if (parm.getCount() < 1) {
            this.messageBox("����ϵͳ�в����ڴ���Ա");
            return;
        }
        this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        String sql = PAT_SQL + "";
        sql = sql.replaceFirst("@", " ");
        sql = sql.replaceFirst("#", " AND A.MR_NO = '" + mrNo + "' ");
//        if (this.getValueBoolean("UNREPORT")) {// add by wanglong 20130314
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
//        } else if (this.getValueBoolean("REPORT")) {
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
//        } else if (this.getValueBoolean("ALL")) {
//            sql = sql.replaceFirst("\\$", " ");
//        }
        if (this.getValue("COVER_FLG_COMBO").equals("ALL")
                || this.getValue("COVER_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("\\$", " ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
        }
        if (this.getValue("BILL_FLG_COMBO").equals("ALL")
                || this.getValue("BILL_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("%", " ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG >= 0 ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG = -1 ");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("��ѯ��Ա�б�ʧ��");
            return;
        }
        onPartClear();// add by wanglong 20130408
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.callFunction("UI|COVER_FLG_COMBO|setValue", "ALL");//add by wanglong 20130423
        this.callFunction("UI|BILL_FLG_COMBO|setValue", "ALL");
        contractD = new HRMContractD();
        patTable.setParmValue(result);
        if (result.getCount() >= 0) {
            this.setValue("PAT_COUNT", result.getCount() + "��");
        } else this.setValue("COUNT", "");
        order.onQueryByMrNo(mrNo);// ��ʼ��order��Ϣ
    }

    /**
     * ��δ�����������ѱ���������ȫ������ѡ��ť�¼�
     * ��δ�ɷѡ������ѽɷѡ�����ȫ������ѡ��ť�¼�
     */
    public void onStateChoose() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            onQueryByMr();
        } else {
            String contractCode = this.getValueString("CONTRACT_CODE");
            if (contractCode.equals("")) {
                this.messageBox("��ѡ���ͬ");
                return;
            } else {
                onContractCodeChoose();
            }
        }
    }
    
    /**
     * �Ա�ɸѡ
     */
    public void onCustomizeChoose() {// modify by wanglong 20130419
        TParm parm = patTable.getParmValue();
        if (this.getValueString("START_SEQ").equals("")
                || this.getValueString("END_SEQ").equals("")) {
            return;
        }
        if (!this.getValueString("START_SEQ").matches("[0-9]+")
                || !this.getValueString("END_SEQ").matches("[0-9]+")) {
            messageBox("����������");
            return;
        }
        int startSeq = this.getValueInt("START_SEQ");
        int endSeq = this.getValueInt("END_SEQ");
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
        patTable.setParmValue(parm);
        if (parm.getCount() >= 0) {
            this.setValue("PAT_COUNT", parm.getCount() + "��");
        } else this.setValue("COUNT", "");
    }
    
    /**
     * ��ѯ�¼�,����ѡ�����������ͺ�ͬ�����ѯҽ������ʾҽ������
     */
    public void showBill() {
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        List<String> caseNo = new ArrayList<String>();
        TParm patParm = patTable.getParmValue();
        int rowCount = patParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            TParm temp = patParm.getRow(i);
            if ("Y".equals(temp.getValue("FLG"))) {
                caseNo.add(temp.getValue("CASE_NO"));
            }
        }
//        int listSize = caseNo.size();
        // add by lx 2012/05/27
        caseNos = caseNo;
//      StringBuffer caseNoStr = new StringBuffer();//delete by wanglong 20130806
//      for (int i = 0; i < listSize; i++) {
//          caseNoStr.append("'" + caseNo.get(i).toString() + "'");
//          if (i != listSize - 1) caseNoStr.append(",");
//      }
      // this.messageBox_(caseNoStr.toString());
      // =========== add by chenxi 20130207 �˵���ʾ��ǰѡ�е� ��Ա���˵�
        StringBuffer billNoStr = new StringBuffer();
        String caseNoWhere = HRMOrder.getInStatement("CASE_NO", caseNos);//add by wanglong 20130806
        String sql =
                "SELECT DISTINCT(BILL_NO) FROM HRM_ORDER WHERE CONTRACT_CODE = '#' AND (" + caseNoWhere + ")";//modify by wanglong 20130806
        sql = sql.replaceFirst("#", contractCode);
//        System.out.println("sql============="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        for (int i = 0; i < result.getCount(); i++) {
            billNoStr.append("'" + result.getValue("BILL_NO", i) + "'");
            if (i != result.getCount() - 1) billNoStr.append(",");
        }
        // =========== add by chenxi 20130207 �˵���ʾ��ǰѡ�е� ��Ա���˵�
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)) {
            this.messageBox_("��ѯ��������");
            return;
        }
        bill = new HRMBill();
        // ��ѯ���е��˵�����һ���ѽ��㣩
        bill.onQueryByCom(companyCode, contractCode, billNoStr.toString());
        bill.insertRow(-1, companyCode, contractCode);// ������һ��
        billTable.setDataStore(bill);
        billTable.setDSValue();
        // ��û�������˵�����Ա������һ�У�����һ������ʾ���ǵ��˵�
        createNewBill(companyCode, contractCode, caseNos);
        arAmt.setValue(bill.getItemDouble(0, "AR_AMT"));// add by wanglong 20130423
    }

    /**
     * �������˵�
     */
    public void createNewBill(String companyCode, String contractCode, List<String> caseNos) {//modify by wanglong 20130806
        if (caseNos.size() < 1) {
            return;
        }
        TParm orderParm = order.getDepositBillParm(companyCode, contractCode, caseNos);
        int count = orderParm.getCount();
        if (count <= 0) {
            return;
        }
        int row = bill.rowCount() - 1;
        // double amt = orderParm.getDouble("AR_AMT");
        bill.setItem(row, "OWN_AMT", orderParm.getDouble("OWN_AMT"));
        bill.setItem(row, "AR_AMT", orderParm.getDouble("AR_AMT"));
        bill.setItem(row, "DISCOUNT_AMT",
                     orderParm.getDouble("OWN_AMT") - orderParm.getDouble("AR_AMT"));
        billTable.setDSValue();
    }

    /**
     * ����LIST�е�����ƴ��ORDER�Ĺ�������
     * 
     * @return
     */
    public String getCaseFilter() {
        String filter = "";
        if (caseNos.size() <= 0) {
            return filter;
        }
        for (int i = 0; i < caseNos.size(); i++) {
            filter += "CASE_NO='" + caseNos.get(i) + "' OR ";
        }
        filter = filter.substring(0, filter.lastIndexOf("OR"));
        // System.out.println("==filter=="+filter);
        return filter;
    }
    
    /**
     * ���㣨����HRM_Bill���ݣ�����HRM_order���ݣ�
     */
    public void onSave() {
        if (bill.getItemDouble(bill.rowCount() - 1, "AR_AMT") > 0) {
            bill.setActive(bill.rowCount() - 1, true);
        } else {
            this.messageBox("û����Ҫ������˵�");
            return;
        }
        // TParm parm = orderTable.getParmValue();
//        String case_no = "";
//        String orderset_no = "";
//        int orderset_group_no = 0;
        String billNo =
                billTable.getDataStore().getItemString(billTable.getDataStore().rowCount() - 1,
                                                       "BILL_NO");
        if (billNo.equals("")) {
            this.messageBox("���㵥��Ϊ��");
            return;
        }
        String flagSql = "SELECT * FROM HRM_BILL WHERE BILL_NO='" + billNo + "'";// add by wanglong 20130329
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("��ѯ�˵�״̬���� " + flagParm.getErrText());
            return;
        }
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (flagParm.getCount() > 0) {
            this.messageBox("���˵��ѽ���");
            if (!contractCode.equals("")) {
                onContractCodeChoose();
//                order = new HRMOrder();//delete by wanglong 20130417
                bill = new HRMBill();
                orderTable.removeRowAll();
                billTable.removeRowAll();
                billDetailTable.removeRowAll();
            } else {
                onClear();
            }
            return;
        }
        String id = Operator.getID();
//        String ip = Operator.getIP();
        String[] sql = bill.getUpdateSQL();
        String[] sql_order_list = new String[1];
        // add by lx 2012/05/27
        // System.out.println("=======sql======="+this.getCaseFilter());
        String caseNoWhere=HRMOrder.getInStatement("CASE_NO",caseNos);//add by wanglong 20130806
//        String orderSql = "SELECT * FROM HRM_ORDER WHERE CONTRACT_CODE='#' AND (" + caseNoWhere+")";
//        orderSql=orderSql.replaceFirst("#", contractCode);
//        // System.out.println("=======orderSql======="+orderSql);
//        TParm parm = new TParm(TJDODBTool.getInstance().select(orderSql));
//        for (int i = 0; i < parm.getCount("ORDER_DESC"); i++) {
//            // �����������˵������
//            if ("Y".equals(parm.getValue("BILL_FLG", i))) {
//                continue;
//            }
//            case_no = parm.getValue("CASE_NO", i);
//            orderset_no = parm.getValue("ORDERSET_CODE", i);
//            orderset_group_no = parm.getInt("ORDERSET_GROUP_NO", i);
//            String orderCat1Code = parm.getValue("ORDER_CAT1_CODE", i);
//            // �����ҩƷ���������任
//            if ("PHA_W".equals(orderCat1Code)) {
//                sql_order_list[0] =
//                        " UPDATE HRM_ORDER SET BILL_FLG = 'Y', BILL_NO = '" + billNo
//                                + "', BILL_USER = '" + id
//                                + "', BILL_DATE = SYSDATE  WHERE CASE_NO = '" + case_no
//                                + "' AND ORDER_CODE = '" + parm.getValue("ORDER_CODE", i) + "' AND BILL_NO IS NULL";
//            } else {
//                sql_order_list[0] =
//                        " UPDATE HRM_ORDER SET BILL_FLG = 'Y', BILL_NO = '" + billNo
//                                + "', BILL_USER = '" + id
//                                + "', BILL_DATE = SYSDATE  WHERE CASE_NO = '" + case_no
//                                + "' AND ORDERSET_CODE = '" + orderset_no
//                                + "' AND ORDERSET_GROUP_NO = " + orderset_group_no + " AND BILL_NO IS NULL";
//            }
//            sql = StringTool.copyArray(sql, sql_order_list);
//        }
        sql_order_list[0]= " UPDATE HRM_ORDER SET BILL_FLG = 'Y', BILL_NO = '" + billNo
              + "', BILL_USER = '" + id
              + "', BILL_DATE = SYSDATE  WHERE BILL_NO IS NULL AND (" + caseNoWhere + ")";//add by wanglong 20130806
        sql = StringTool.copyArray(sql, sql_order_list);
        // if(order.isModified()){
        // if(!order.updateOpt()){
        // this.messageBox_("����ҽ������ʧ��");
        // return;
        // }
        // }
        String updateBillSql = // add by wanglong 20130829
                "UPDATE HRM_CONTRACTD SET BILL_NO = '#' WHERE COMPANY_CODE = '#' AND CONTRACT_CODE = '#' "
                        + " AND MR_NO IN (SELECT DISTINCT MR_NO FROM HRM_ORDER WHERE 1=1 AND (#))";
        updateBillSql = updateBillSql.replaceFirst("#", billNo);
        updateBillSql = updateBillSql.replaceFirst("#", this.getValueString("COMPANY_CODE"));
        updateBillSql = updateBillSql.replaceFirst("#", contractCode);
        updateBillSql = updateBillSql.replaceFirst("#", caseNoWhere);
        sql = StringTool.copyArray(sql, new String[]{updateBillSql });//add end
        if (sql == null || sql.length <= 0) {
            this.messageBox_("û����Ҫ���������");
            return;
        }
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer
                        .executeAction("action.hrm.HRMDepositAction", "onSaveDeposit", inParm);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("����ʧ�� " + result.getErrText());
        } else {
            this.messageBox("����ɹ�");
            TParm parmValue = patTable.getParmValue();
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (parmValue.getValue("FLG", i).equals("Y")
                        && caseNos.contains(parmValue.getValue("CASE_NO", i))) {//����ɹ����޸���Ա�б�״̬Ϊ���ѽ��㡱
                    parmValue.setData("BILL_FLG", i, 1);
                }
            }
            patTable.setParmValue(parmValue);
            billTable.setSelectedRow(bill.rowCount()-1);
            onBillTableClick();// modify by wanglong 20130329
//            String contractCode = this.getValueString("CONTRACT_CODE");
//            if (!contractCode.equals("")) {
//                onContractCodeChoose();
//                orderTable.removeRowAll();
//                billTable.removeRowAll();
//                billDetailTable.removeRowAll();
//            } else {
//                onClear();
//            }
        }
    }

    /**
     * ȡ�����㣨�ѽɷѣ�����ɾ����������HRM_BILL��ɾ����ͬʱ��HRM_ORDER.BILL_NO�ÿգ�
     */
    public void onDelete() {
        if (billTable == null) {
            return;
        }
        if (bill == null) {
            return;
        }
        if (bill.rowCount() < 1) {this.messageBox("û���˵���Ϣ");
            return;
        }
        int row = billTable.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ��һ���˵�");
            return;
        }
        if (TypeTool.getBoolean(bill.getItemData(row, "REXP_FLG"))) {// ��Ʊע��
            this.messageBox_("�˵��Ѵ�Ʊ������ȡ������");
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("���㵥��Ϊ�գ�ȡ������ʧ��");
        }
        String billSql = "SELECT * FROM HRM_BILL WHERE BILL_NO='" + billNo + "'";
        TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
        if (result1.getErrCode() != 0) {
            this.messageBox("����˵���Ϣʧ�� " + result1.getErrText());
            return;
        }
        if (result1.getCount() < 1) {
            this.messageBox("���˵�δ���㣬����ȡ��");
            return;
        }
        if (!result1.getValue("RECEIPT_NO", 0).equals("")) {
            this.messageBox("���˵��ѽɷѣ�����ȡ��");
            return;
        }
        bill.onQueryByBillNo(billNo);// add by wanglong 20130415
        bill.deleteRow(row);
        //==================================modify by wanglong 20130726
//        order = new HRMOrder();// add by wanglong 20130415
//        order.onQueryByBillNo(billNo);// add by wanglong 20130415
//        order.retrieve();// add by wanglong 20130415
//        if (!order.removeBillByBillNo(billNo)) {
//            this.messageBox("׼������ʧ��");
//            return;
//        }
//        String[] sql = order.getUpdateSQL();
        String[] sql = new String[]{};// add by wanglong 20130726
        String updateOrderSql =
                "UPDATE HRM_ORDER SET BILL_NO = '',BILL_FLG = 'N', BILL_USER = '', BILL_DATE = '', "
                        + "OPT_USER = '#', OPT_DATE = SYSDATE, OPT_TERM='#' WHERE BILL_NO = '#'";// add by wanglong 20130726
        updateOrderSql = updateOrderSql.replaceFirst("#", Operator.getID());
        updateOrderSql = updateOrderSql.replaceFirst("#", Operator.getIP());
        updateOrderSql = updateOrderSql.replaceFirst("#", billNo);
        sql = StringTool.copyArray(sql, new String[]{updateOrderSql });
        //==================================modify end
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        String updateDSql = "UPDATE HRM_CONTRACTD SET BILL_NO = '' WHERE BILL_NO='" + billNo + "'";
        sql = StringTool.copyArray(sql, new String[]{updateDSql });// add by wanglong 20130415
        if (sql == null || sql.length <= 0) {
            this.messageBox("û����Ҫ���������");
            return;
        }
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer
                        .executeAction("action.hrm.HRMDepositAction", "onSaveDeposit", inParm);
        if (result.getErrCode() != 0) {
            this.messageBox_("����ʧ�� " + result.getErrText());
        } else {
            this.messageBox("ȡ������ɹ�");
            String contractCode = this.getValueString("CONTRACT_CODE");
            if (!contractCode.equals("")) {
                onContractCodeChoose();
                orderTable.removeRowAll();
                billTable.removeRowAll();
                billDetailTable.removeRowAll();
            } else {
                onClear();
            }
        }
    }

    /**
     * ��ӡ�շ��嵥
     */
    public void onPrint() {
        if (billTable == null) {
            return;
        }
        int row = billTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("��ѡ��һ���˵����в���");
            return;
        }
        if (bill == null || bill.rowCount() <= 0) {
            this.messageBox_("û���˵��ɴ�ӡ");
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("ȡ�ý��㵥��ʧ��");
            return;
        }
//         TParm parm = order.getParmByBillNoMain(billNo);
        TParm parm = order.getParmByBillNo(billNo);// modify by wanglong 20130403
        if (parm == null) {
            this.messageBox_("û���˵���ϸ��Ϣ�����˵�����δ���㣩");
            return;
        }
        if (parm.getErrCode() != 0) {
            this.messageBox("��ѯҽ����ϸʧ�� " + parm.getErrText());
            return;
        }
        TParm result = new TParm();
        Timestamp now = order.getDBTime();
        String data = "��ӡʱ�䣺" + StringTool.getString(now, "yyyy/MM/dd");
        result.setData("PRINT_TIME", "TEXT", data);
        result.setData("PRINT_USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());
        Vector AR_AMT = (Vector) parm.getData("AR_AMT");
        double sum = 0.00;
        for (int i = 0; i < AR_AMT.size(); i++) {
            // System.out.println(AR_AMT.get(i)+"");
            sum += Double.parseDouble(AR_AMT.get(i) + "");
        }
        new BigDecimal(sum).setScale(0, BigDecimal.ROUND_HALF_UP);
        result.setData("sum5", "TEXT",
                       "�ϼƣ�" + new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP));
        // System.out.println(parm.getData());
        result.setData("BILL_TABLE", parm.getData());
        openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMBillDetail.jhw", result, false);
    }

    /**
     * ͳһ�ۿ�
     */
    public void onSameDiscnt() {// add by wanglong 20130306
        double discnt = StringTool.getDouble(this.getText("DISCNT"));// �ۿ�
        if (discnt <= 0 || discnt > 1) {
            this.messageBox("�ۿ�ֻ����0��1֮��");
            this.setValue("DISCNT", 0);
            return;
        }
        TParm parm = patTable.getParmValue();
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        boolean flag = true;
        for (int i = 0; i < count; i++) {
            if (parm.getBoolean("FLG", i) == true) {
                parm.setData("DISCNT", i, discnt);
                flag = false;
            }
        }
        if (flag == true) {
            this.messageBox("û��ѡ���κ���Ա");
        }
        patTable.setParmValue(parm);
    }

    /**
     * ����ҽ�����ۿۣ��ѽ���Ĳ����ģ�
     */
    public void onChangeOrderDiscnt() {// add by wanglong 20130306
        //onSameDiscnt();
        patTable.acceptText();
        TParm parm = patTable.getParmValue();
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        double discnt = 0;
        String contractCode = "";
        String mrNo = "";
        String caseNo = "";
        boolean flag = true;
        boolean isBilled = true;// �Ƿ����ѽ������Ա
        int doneNum = 0;// �ɹ��޸��ۿ۵�����
        for (int i = 0; i < count; i++) {
            if (parm.getValue("FLG", i).equals("Y")) {
                if (!parm.getValue("BILL_FLG", i).equals("0") && isBilled == true) {
                    this.messageBox("�ѽ�����˵����ۿ۲��ᱻ����");
                    isBilled = false;
                } else if (parm.getValue("BILL_FLG", i).equals("0")) {
                    String[] sql = new String[]{};
                    discnt = parm.getDouble("DISCNT", i);
                    if (discnt <= 0 || discnt > 1) {
                        this.messageBox("�ۿ�ֻ����0��1֮��");
                        return;
                    }
                    contractCode = parm.getValue("CONTRACT_CODE", i);
                    mrNo = parm.getValue("MR_NO", i);
                    caseNo = parm.getValue("CASE_NO", i);
                    String updateContractDSql =
                            "UPDATE HRM_CONTRACTD SET DISCNT=" + discnt + " WHERE CONTRACT_CODE='"
                                    + contractCode + "' AND MR_NO='" + mrNo + "'";
                    sql = StringTool.copyArray(sql, new String[]{updateContractDSql });
                    String updateOrderSql =
                            "UPDATE HRM_ORDER SET DISCOUNT_RATE="
                                    + discnt
                                    + ", AR_AMT=DISPENSE_QTY*OWN_PRICE*"
                                    + discnt
                                    + " WHERE BILL_NO IS NULL AND CAT1_TYPE<>'PHA' AND CONTRACT_CODE='"
                                    + contractCode + "' AND MR_NO='" + mrNo + "'";
                    sql = StringTool.copyArray(sql, new String[]{updateOrderSql });
                    String updatePatAdmSql =
                            "UPDATE HRM_PATADM SET DISCNT=" + discnt + " WHERE CASE_NO='" + caseNo
                                    + "'";
                    sql = StringTool.copyArray(sql, new String[]{updatePatAdmSql });
                    Map<String,String[]> inMap = new HashMap<String,String[]>();
                    inMap.put("SQL", sql);
                    TParm inParm = new TParm();
                    inParm.setData("IN_MAP", inMap);
                    TParm result =
                            TIOM_AppServer.executeAction("action.hrm.HRMDepositAction",
                                                         "onSaveDeposit", inParm);
                    if (result.getErrCode() != 0) {
                        this.messageBox("������" + parm.getData("PAT_NAME", i) + "(������:" + mrNo
                                + ")  �ۿ��޸�ʧ��" + result.getErrText());
                        flag = false;
                    } else {
                        doneNum++;
                        TParm param = new TParm();
                        param.setData("COMPANY_CODE", parm.getValue("COMPANY_CODE", i));
                        param.setData("CONTRACT_CODE", contractCode);
                        param.setData("MR_NO", mrNo);
                        TParm result2 = HRMCheckFeeTool.getInstance().onQueryMaster(param);
                        if (result2.getErrCode() != 0) {
                            this.messageBox("��ѯ�޸ĺ����Ա������Ϣʧ��");
                        }
                        double arAmt = 0;
                        for (int j = 0; j < result2.getCount(); j++) {
                            arAmt += result2.getDouble("AR_AMT", j);
                        }
                        parm.setData("AR_AMT", i, arAmt);//modify by wanglong 20130417
                    }
                }
            }
        }
        if (flag == true && doneNum > 0) {
            this.messageBox("�ۿ��޸ĳɹ�");
        }
        patTable.setParmValue(parm);
        this.showBill();// ����չʾ�˵�
        // this.onClear();
    }
    
 // ====================������begin======================add by wanglong 20130419
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
