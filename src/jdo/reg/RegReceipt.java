package jdo.reg;

import com.dongyang.data.TModifiedData;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import jdo.bil.BilInvrcpt;
import jdo.bil.BilInvoice;
import java.sql.Timestamp;
/**
 *
 * <p>Title: �Һ��վݶ���</p>
 *
 * <p>Description: �Һ��վݶ���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class RegReceipt
    extends TModifiedData {
    /**
     * ����� CASE_NO
     */
    private String caseNo;
    /**
     * �վݺ� RECEIPT_NO
     */
    private String receiptNo;
    /**
     * �ż�ס�� ADM_TYPE
     */
    private String admType;
    /**
     * ���� REGION
     */
    private String region;
    /**
     * ������ MR_NO
     */
    private String mrNo;
    /**
     * �����վݺ��� RESET_RECEIPT_NO
     */
    private String resetReceiptNo;
    /**
     * �վ�ӡˢ�� PRINT_NO
     */
    private String printNo;
    /**
     * �������� BILL_DATE
     */
    private Timestamp billDate;
    /**
     * �շ����� CHARGE_DATE
     */
    private Timestamp chargeDate;
    /**
     * �վݴ�ӡ���� PRINT_DATE
     */
    private Timestamp printDate;
    /**
     * �Һŷ� REG_FEE
     */
    private double regFee;
    /**
     * �ۿۺ�Һŷ� REG_FEE_REAL
     */
    private double regFeeReal;
    /**
     * ���� CLINIC_FEE
     */
    private double clinicFee;
    /**
     * �ۿۺ����� CLINIC_FEE_REAL
     */
    private double clinicFeeReal;
    /**
     * ���ӷ� SPC_FEE
     */
    private double spcFee;
    /**
     * ��������1 OTHER_FEE1
     */
    private double otherFee1;
    /**
     * ��������2 OTHER_FEE2
     */
    private double otherFee2;
    /**
     * ��������3 OTHER_FEE3
     */
    private double otherFee3;
    /**
     * Ӧ�ս�� AR_AMT
     */
    private double arAmt;
    /**
     * �ֽ�֧�� PAY_CASH
     */
    private double payCash;
    /**
     * ���п�֧�� PAY_BANK_CARD
     */
    private double payBankCard;
    /**
     * ֧Ʊ֧�� PAY_CHECK
     */
    private double payCheck;
    /**
     * ҽ�ƿ�֧�� PAY_MEDICAL_CARD
     */
    private double payMedicalCard;
    /**
     * ҽ����֧�� PAY_INS_CARD
     */
    private double payInsCard;
    /**
     * ����֧�� PAY_DEBIT
     */
    private double payDebit;
    /**
     * �ż���������� PAY_INS
     */
    private double payIns;
    /**
     * ��ע(д֧Ʊ��) REMARK
     */
    private String remark;
    /**
     * �տ�Ա���� CASH_CODE
     */
    private String cashCode;
    /**
     *�Һ�Ա  ACCOUNT_USER
     */
    private String accountUser;
    /**
     * ���ʱ�־ ACCOUNT_FLG
     */
    private String accountFlg;
    /**
     * �������� ACCOUNT_DATE
     */
    private Timestamp accountDate;
    /**
     * �սᱨ��� ACCOUNT_SEQ
     */
    private String accountSeq;
    /**
     * ���ж��˱���� BANK_SEQ
     */
    private String bankSeq;
    /**
     * ���ж���ʱ�� BANK_DATE
     */
    private Timestamp bankDate;
    /**
     * ���ж�����Ա BANK_USER
     */
    private String bankUser;
    /**
     * Ʊ����ϸ�� bilInvrcpt
     */
    private BilInvrcpt bilInvrcpt;
    /**
     * Ʊ������ bilInvoice
     */
    private BilInvoice bilInvoice;

    // ������
    public RegReceipt() {
        StringBuffer sb = new StringBuffer();
        //�����
        sb.append("caseNo:CASE_NO;");
        //�վݺ�
        sb.append("receiptNo:RECEIPT_NO;");
        //�ż���
        sb.append("admType:ADM_TYPE;");
        //����
        sb.append("region:REGION_CODE;");
        //������
        sb.append("mrNo:MR_NO;");
        //�����վݺ���
        sb.append("resetReceiptNo:RESET_RECEIPT_NO;");
        //�վ�ӡˢ��
        sb.append("printNo:PRINT_NO;");
        //��������
        sb.append("billDate:BILL_DATE;");
        //�շ�����
        sb.append("chargeDate:CHARGE_DATE;");
        //�վݴ�ӡ����
        sb.append("printDate:PRINT_DATE;");
        //�Һŷ�
        sb.append("regFee:REG_FEE;");
        //�ۿ�ǰ�Һŷ�
        sb.append("regFeeReal:REG_FEE_REAL;");
        //����
        sb.append("clinicFee:CLINIC_FEE;");
        //�ۿ�ǰ����
        sb.append("clinicFeeReal:CLINIC_FEE_REAL;");
        //���ӷ�
        sb.append("spcFee:SPC_FEE;");
        //��������1
        sb.append("otherFee1:OTHER_FEE1;");
        //��������2
        sb.append("otherFee2:OTHER_FEE2;");
        //��������3
        sb.append("otherFee3:OTHER_FEE3;");
        //Ӧ�ս��
        sb.append("arAmt:AR_AMT;");
        //�ֽ�֧��
        sb.append("payCash:PAY_CASH;");
        //���п�֧��
        sb.append("payBankCard:PAY_BANK_CARD;");
        //֧Ʊ֧��
        sb.append("payCheck:PAY_CHECK;");
        //ҽ�ƿ�֧��
        sb.append("payMedicalCard:PAY_MEDICAL_CARD;");
        //ҽ����֧��
        sb.append("payInsCard:PAY_INS_CARD;");
        //����֧��
        sb.append("payDebit:PAY_DEBIT;");
        //�ż����������
        sb.append("payIns:PAY_INS;");
        //��ע(д֧Ʊ��)
        sb.append("remark:REMARK;");
        //�տ�Ա����
        sb.append("cashCode:CASH_CODE;");
        //�Һ�Ա
        sb.append("accountUser:ACCOUNT_USER;");
        //���ʱ�־
        sb.append("accountFlg:ACCOUNT_FLG;");
        //��������
        sb.append("accountDate:ACCOUNT_DATE;");
        //�սᱨ���
        sb.append("accountSeq:ACCOUNT_SEQ;");
        //���ж��˱����
        sb.append("bankSeq:BANK_SEQ;");
        //���ж���ʱ��
        sb.append("bankDate:BANK_DATE;");
        //���ж�����Ա
        sb.append("bankUser:BANK_USER;");
        setMapString(sb.toString());
    }

    /**
     * ���þ������
     * @param caseNo String
     */
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    /**
     * �õ��������
     * @return String
     */
    public String getCaseNo() {
        return caseNo;
    }

    /**
     * �����վݺ�
     * @param receiptNo String
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * �õ��վݺ�
     * @return String
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * �����ż���
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * �õ��ż���
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * ��������
     * @param region String
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * �õ�����
     * @return String
     */
    public String getRegion() {
        return region;
    }
    /**
     * ���ò�����
     * @param mrNo String
     */
    public void setMrNo(String mrNo){
        this.mrNo = mrNo;
    }
    /**
     * �õ�������
     * @return String
     */
    public String getMrNo(){
        return mrNo;
    }
    /**
     * ���ó����վݺ���
     * @param resetReceiptNo String
     */
    public void setResetReceiptNo(String resetReceiptNo) {
        this.resetReceiptNo = resetReceiptNo;
    }

    /**
     * �õ������վݺ���
     * @return String
     */
    public String getResetReceiptNo() {
        return resetReceiptNo;
    }
    /**
     * �����վ�ӡˢ��
     * @param printNo String
     */
    public void setPrintNo(String printNo){
        this.printNo = printNo;
    }
    /**
     * �õ��վ�ӡˢ��
     * @return String
     */
    public String getPrintNo(){
        return printNo;
    }
    /**
     * ���ü�������
     * @param billDate Timestamp
     */
    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    /**
     * �õ���������
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate;
    }

    /**
     * �����շ�����
     * @param chargeDate Timestamp
     */
    public void setChargeDate(Timestamp chargeDate) {
        this.chargeDate = chargeDate;
    }

    /**
     * �õ��շ�����
     * @return TimestampValue
     */
    public Timestamp getChargeDate() {
        return chargeDate;
    }
    /**
     * �����վݴ�ӡ����
     * @param printDate Timestamp
     */
    public void setPrintDate(Timestamp printDate){
        this.printDate = printDate;
    }
    /**
     * �õ��վݴ�ӡ����
     * @return Timestamp
     */
    public Timestamp getPrintDate(){
        return printDate;
    }
    /**
     * ���ùҺŷ�
     * @param regFee double
     */
    public void setRegFee(double regFee) {
        this.regFee = regFee;
    }

    /**
     * �õ��Һŷ�
     * @return double
     */
    public double getRegFee() {
        return regFee;
    }

    /**
     * �����ۿ�ǰ�Һŷ�
     * @param regFeeReal double
     */
    public void setRegFeeReal(double regFeeReal) {
        this.regFeeReal = regFeeReal;
    }

    /**
     * �õ��ۿ�ǰ�Һŷ�
     * @return double
     */
    public double getRegFeeReal() {
        return regFeeReal;
    }

    /**
     * ��������
     * @param clinicFee double
     */
    public void setClinicFee(double clinicFee) {
        this.clinicFee = clinicFee;
    }

    /**
     * �õ�����
     * @return double
     */
    public double getClinicFee() {
        return clinicFee;
    }

    /**
     * �����ۿ�ǰ����
     * @param clinicFeeReal double
     */
    public void setClinicFeeReal(double clinicFeeReal) {
        this.clinicFeeReal = clinicFeeReal;
    }

    /**
     * �õ��ۿ�ǰ����
     * @return double
     */
    public double getClinicFeeReal() {
        return clinicFeeReal;
    }

    /**
     * ���ø��ӷ�
     * @param spcFee double
     */
    public void setSpcFee(double spcFee) {
        this.spcFee = spcFee;
    }

    /**
     * �õ����ӷ�
     * @return double
     */
    public double getSpcFee() {
        return spcFee;
    }

    /**
     * ������������1
     * @param otherFee1 double
     */
    public void setOtherFee1(double otherFee1) {
        this.otherFee1 = otherFee1;
    }

    /**
     * �õ���������1
     * @return double
     */
    public double getOtherFee1() {
        return otherFee1;
    }

    /**
     * ������������2
     * @param otherFee2 double
     */
    public void setotherFee2(double otherFee2) {
        this.otherFee2 = otherFee2;
    }

    /**
     * �õ���������2
     * @return double
     */
    public double getotherFee2() {
        return otherFee2;
    }

    /**
     * ������������3
     * @param otherFee3 double
     */
    public void setotherFee3(double otherFee3) {
        this.otherFee3 = otherFee3;
    }

    /**
     * �õ���������3
     * @return double
     */
    public double getotherFee3() {
        return otherFee3;
    }

    /**
     * ����Ӧ�ս��
     * @param arAmt double
     */
    public void setArAmt(double arAmt) {
        this.arAmt = arAmt;
    }

    /**
     * �õ�Ӧ�ս��
     * @return double
     */
    public double getArAmt() {
        return arAmt;
    }

    /**
     * �����ֽ�֧��
     * @param payCash double
     */
    public void setPayCash(double payCash) {
        this.payCash = payCash;
    }

    /**
     * �õ��ֽ�֧��
     * @return double
     */
    public double getPayCash() {
        return payCash;
    }

    /**
     * �������п�֧��
     * @param payBankCard double
     */
    public void setPayBankCard(double payBankCard) {
        this.payBankCard = payBankCard;
    }

    /**
     * �õ����п�֧��
     * @return double
     */
    public double getPayBankCard() {
        return payBankCard;
    }

    /**
     * ����֧Ʊ֧��
     * @param payCheck double
     */
    public void setPayCheck(double payCheck) {
        this.payCheck = payCheck;
    }

    /**
     * �õ�֧Ʊ֧��
     * @return double
     */
    public double getPayCheck() {
        return payCheck;
    }

    /**
     * ����ҽ�ƿ�֧��
     * @param payMedicalCard double
     */
    public void setPayMedicalCard(double payMedicalCard) {
        this.payMedicalCard = payMedicalCard;
    }

    /**
     * �õ�ҽ�ƿ�֧��
     * @return double
     */
    public double getPayMedicalCard() {
        return payMedicalCard;
    }

    /**
     * ����ҽ����֧��
     * @param payInsCard double
     */
    public void setPayInsCard(double payInsCard) {
        this.payInsCard = payInsCard;
    }

    /**
     * �õ�ҽ����֧��
     * @return double
     */
    public double getPayInsCard() {
        return payInsCard;
    }

    /**
     * ���ü���֧��
     * @param payDebit double
     */
    public void setPayDebit(double payDebit) {
        this.payDebit = payDebit;
    }

    /**
     * �õ�����֧��
     * @return double
     */
    public double getPayDebit() {
        return payDebit;
    }

    /**
     * �����ż����������
     * @param payIns double
     */
    public void setPayIns(double payIns) {
        this.payIns = payIns;
    }

    /**
     * �õ��ż����������
     * @return double
     */
    public double getPayIns() {
        return payIns;
    }

    /**
     * ���ñ�ע(д֧Ʊ��)
     * @param remark String
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * �õ���ע(д֧Ʊ��)
     * @return String
     */
    public String getRemark() {
        return remark;
    }

    /**
     * �����տ�Ա����
     * @param cashCode String
     */
    public void setCashCode(String cashCode) {
        this.cashCode = cashCode;
    }

    /**
     * �õ��տ�Ա����
     * @return String
     */
    public String getashCode() {
        return cashCode;
    }

    /**
     * ���ùҺ�Ա
     * @param accountUser String
     */
    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    /**
     * �õ��Һ�Ա
     * @return String
     */
    public String getAccountUser() {
        return accountUser;
    }
    /**
     * ���ý��ʱ�־
     * @param accountFlg String
     */
    public void setAccountFlg(String accountFlg) {
        this.accountFlg = accountFlg;
    }

    /**
     * �õ����ʱ�־
     * @return String
     */
    public String getAccountFlg() {
        return accountFlg;
    }
    /**
     * ���ý�������
     * @param accountDate Timestamp
     */
    public void setAccountDate(Timestamp accountDate) {
        this.accountDate = accountDate;
    }

    /**
     * �õ���������
     * @return String
     */
    public Timestamp getAccountDate() {
        return accountDate;
    }
    /**
     * �����սᱨ���
     * @param accountSeq String
     */
    public void setAccountSeq(String accountSeq) {
        this.accountSeq = accountSeq;
    }

    /**
     * �õ��սᱨ���
     * @return String
     */
    public String getAccountSeq() {
        return accountSeq;
    }
    /**
     * �������ж��˱����
     * @param bankSeq String
     */
    public void setBankSeq(String bankSeq) {
        this.bankSeq = bankSeq;
    }

    /**
     * �õ����ж��˱����
     * @return String
     */
    public String getBankSeq() {
        return bankSeq;
    }
    /**
     * �������ж���ʱ��
     * @param bankDate Timestamp
     */
    public void setBankDate(Timestamp bankDate) {
        this.bankDate = bankDate;
    }

    /**
     * �õ����ж���ʱ��
     * @return String
     */
    public Timestamp getBankDate() {
        return bankDate;
    }
    /**
     * ���������ж�����Ա
     * @param bankUser String
     */
    public void setBankUser(String bankUser) {
        this.bankUser = bankUser;
    }

    /**
     * �õ����ж�����Ա
     * @return String
     */
    public String getBankUser() {
        return bankUser;
    }

    /**
     * ����Ʊ����ϸ��
     */
    public void createBilInvrcpt() {
        bilInvrcpt = new BilInvrcpt();
        //Ʊ����ϸ����

    }

    /**
     * ����Ʊ����ϸ
     * @param bilInvrcpt BilInvrcpt
     */
    public void setBilInvrcpt(BilInvrcpt bilInvrcpt) {
        this.bilInvrcpt = bilInvrcpt;
    }

    /**
     * �õ�Ʊ����ϸ
     * @return bilInvrcpt
     */
    public BilInvrcpt getBilInvrcpt() {
        return bilInvrcpt;
    }

    /**
     * ����Ʊ������
     */
    public void createBilInvoice() {
        BilInvoice bilInvoice = new BilInvoice();
       setBilInvoice( bilInvoice.initBilInvoice("REG"));
        //Ʊ����������

    }

    /**
     * ����Ʊ������
     * @param bilInvoice BilInvrcpt
     */
    public void setBilInvoice(BilInvoice bilInvoice) {
        this.bilInvoice = bilInvoice;
    }

    /**
     * �õ�Ʊ������
     * @return bilInvrcpt
     */
    public BilInvoice getBilInvoice() {
        return bilInvoice;
    }

    /**
     * ��ʼ��Ʊ��
     * @param recpType String
     */
    public void initBilInvoice(String recpType) {
        this.setBilInvoice(getBilInvoice().initBilInvoice(recpType));
    }

    /**
     * �õ����ز���
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }
}
