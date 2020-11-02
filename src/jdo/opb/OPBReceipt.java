package jdo.opb;

import com.dongyang.data.TModifiedData;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;
import java.util.List;
import jdo.opd.Order;

/**
 *
 * <p>Title: �����շ�Ʊ��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class OPBReceipt
    extends TModifiedData {
    private OrderList orderList;
    /**
     * �������
     */
    private String caseNo;
    /**
     * �վݺ�
     */
    private String receiptNo;

    /**
     * �ż�ס��
     */
    private String admType;
    /**
     * ����
     */
    private String region;
    /**
     * ������
     */
    private String mrNo;
    /**
     * ���ԭ�վ����
     */
    private String resetReceiptNo;
    /**
     * �վ�ӡˢ����
     */
    private String printNo;
    /**
     * ��������
     */
    private Timestamp billDate;
    /**
     * �վݴ�ӡ����
     */
    private Timestamp chargeDate;
    /**
     * ��ӡ���վ��ϵ��շ�����
     */
    private Timestamp printDate;
    /**
     * �վ����01�����
     */
    private double charge01;
    /**
     * �վ����02�����
     */
    private double charge02;
    /**
     * �վ����03�����
     */
    private double charge03;
    /**
     * �վ����4�����
     */
    private double charge04;
    /**
     * �վ����05�����
     */
    private double charge05;
    /**
     * �վ����06�����
     */
    private double charge06;
    /**
     * �վ����07�����
     */
    private double charge07;
    /**
     * �վ����08�����
     */
    private double charge08;
    /**
     * �վ����09�����
     */
    private double charge09;
    /**
     * �վ����10�����
     */
    private double charge10;
    /**
     * �վ����11�����
     */
    private double charge11;
    /**
     * �վ����12�����
     */
    private double charge12;
    /**
     * �վ����13�����
     */
    private double charge13;
    /**
     * �վ����14�����
     */
    private double charge14;
    /**
     * �վ����15�����
     */
    private double charge15;
    /**
     * �վ����16�����
     */
    private double charge16;
    /**
     * �վ����17�����
     */
    private double charge17;
    /**
     * �վ����18�����
     */
    private double charge18;
    /**
     * �վ����19�����
     */
    private double charge19;
    /**
     * �վ����20�����
     */
    private double charge20;
    /**
     * �վ����21�����
     */
    private double charge21;
    /**
     * �վ����22�����
     */
    private double charge22;
    /**
     * �վ����23�����
     */
    private double charge23;
    /**
     * �վ����24�����
     */
    private double charge24;
    /**
     * �վ����25�����
     */
    private double charge25;
    /**
     * �վ����26�����
     */
    private double charge26;
    /**
     * �վ����27�����
     */
    private double charge27;
    /**
     * �վ����28�����
     */
    private double charge28;
    /**
     * �վ����29�����
     */
    private double charge29;
    /**
     * �վ����30�����
     */
    private double charge30;
    /**
     * Ӧ�����
     */
    private double totAmt;
    /**
     * �ۿ�/����ԭ��
     */
    private String reduceReason;
    /**
     * �ۿ�/������
     */
    private double reduceAmt;
    /**
     * �ۿ�/����ʱ��
     */
    private Timestamp reduceDate;
    /**
     * �ۿ�/�������
     */
    private String reduceDeptCode;
    /**
     * �ۿ���Ա
     */
    private String reduceRespond;
    /**
     * Ӧ�ս��
     */
    private double arAmt;
    /**
     * �ֽ�֧��
     */
    private double payCash;
    /**
     * ҽ�ƿ�֧��
     */
    private double payMedicalCard;
    /**
     * ˢ��
     */
    private double payBankCard;
    /**
     * ҽ����֧��
     */
    private double payInsCard;
    /**
     * ֧Ʊ֧��
     */
    private double payCheck;
    /**
     * ����֧��
     */
    private double payDepit;
    /**
     * Ԥ����֧��
     */
    private double payBilPay;
    /**
     * ҽ��֧��
     */
    private double payIns;
    /**
     * ����֧��1
     */
    private double payOther1;
    /**
     * ����֧����
     */
    private double payOther2;
    /**
     * �տע
     */
    private String payRemark;
    /**
     * �շ�Ա
     */
    private String cashierCode;
    /**
     * �ս��ʱ�־
     */
    private String accountFlg;
    /**
     * �ս�������
     */
    private Timestamp accountDate;
    /**
     * �ս����Ա
     */
    private String accountUser;
    /**
     * ���˵�λ����
     */
    private String contractCode;
    /**
     * ���н������
     */
    private String bankSeq;
    /**
     * ���н���ʱ��
     */
    private Timestamp bankDate;
    /**
     * ���н�����������Ա
     */
    private String bankUser;
    /**
     * ����Ա
     */
    private String optUser;
    /**
     * ����ʱ��
     */
    private Timestamp optDate;
    /**
     * �����ն�
     */
    private String optTerm;
    /**
     * ����ҽ���б�
     * @param orderList OrderList
     */
    public void setOrderList(OrderList orderList) {
        this.orderList = orderList;
    }

    /**
     * �õ�ҽ���б�
     * @return OrderList
     */
    public OrderList getOrderList() {
        return this.orderList;
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
     *  �����վݺ�
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
     * �����ż�ס��
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * �õ��ż�ס��
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
    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * �õ�������
     * @return String
     */
    public String getMrNo() {
        return mrNo;
    }

    /**
     * ���ó��ԭ�վ����
     * @param resetReceiptNo String
     */
    public void setResetReceiptNo(String resetReceiptNo) {
        this.resetReceiptNo = resetReceiptNo;
    }

    /**
     * �õ����ԭ�վ����
     * @return String
     */
    public String getResetReceiptNo() {
        return resetReceiptNo;
    }

    /**
     * �����վ�ӡˢ����
     * @param printNo String
     */
    public void setPrintNo(String printNo) {
        this.printNo = printNo;
    }

    /**
     * �õ��վ�ӡˢ����
     * @return String
     */
    public String getPrintNo() {
        return printNo;
    }

    /**
     * ���ü�������
     * @param billDate Timestamp
     */
    public void setBilDate(Timestamp billDate) {
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
     * �����վݴ�ӡ����
     * @param chargeDate Timestamp
     */
    public void setChargeDate(Timestamp chargeDate) {
        this.chargeDate = chargeDate;
    }

    /**
     * �õ��վݴ�ӡ����
     * @return Timestamp
     */
    public Timestamp getChargeDate() {
        return chargeDate;
    }

    /**
     * ���ô�ӡ���վ��ϵ��շ�����
     * @param printDate Timestamp
     */
    public void setPrintDate(Timestamp printDate) {
        this.printDate = printDate;
    }

    /**
     * �õ���ӡ���վ��ϵ��շ�����
     * @return Timestamp
     */
    public Timestamp getPrintDate() {
        return printDate;
    }

    /**
     * �����վ����01�����
     * @param charge01 double
     */
    public void setCharge01(double charge01) {
        this.charge01 = charge01;
    }

    /**
     * �õ��վ����01�����
     * @return double
     */
    public double getCharge01() {
        return charge01;
    }

    /**
     * �����վ����02�����
     * @param charge02 double
     */
    public void setCharge02(double charge02) {
        this.charge02 = charge02;
    }

    /**
     * �õ��վ����02�����
     * @return double
     */
    public double getCharge02() {
        return charge02;
    }

    /**
     * �����վ����03�����
     * @param charge03 double
     */
    public void setCharge03(double charge03) {
        this.charge03 = charge03;
    }

    /**
     * �õ��վ����03�����
     * @return double
     */
    public double getCharge03() {
        return charge03;
    }

    /**
     * �����վ����04�����
     * @param charge04 double
     */
    public void setCharge04(double charge04) {
        this.charge04 = charge04;
    }

    /**
     * �õ��վ����04�����
     * @return double
     */
    public double getCharge04() {
        return charge04;
    }

    /**
     * �����վ����05�����
     * @param charge05 double
     */
    public void setCharge05(double charge05) {
        this.charge05 = charge05;
    }

    /**
     * �õ��վ����5�����
     * @return double
     */
    public double getCharge05() {
        return charge05;
    }

    /**
     * �����վ����01�����
     * @param charge06 double
     */
    public void setCharge06(double charge06) {
        this.charge06 = charge06;
    }

    /**
     * �õ��վ����06�����
     * @return double
     */
    public double getCharge06() {
        return charge06;
    }

    /**
     * �����վ����07�����
     * @param charge07 double
     */
    public void setCharge07(double charge07) {
        this.charge07 = charge07;
    }

    /**
     * �õ��վ����07�����
     * @return double
     */
    public double getCharge07() {
        return charge07;
    }

    /**
     * �����վ����08�����
     * @param charge08 double
     */
    public void setCharge08(double charge08) {
        this.charge08 = charge08;
    }

    /**
     * �õ��վ����8�����
     * @return double
     */
    public double getCharge08() {
        return charge08;
    }

    /**
     * �����վ����9�����
     * @param charge09 double
     */
    public void setCharge09(double charge09) {
        this.charge09 = charge09;
    }

    /**
     * �õ��վ����09�����
     * @return double
     */
    public double getCharge09() {
        return charge09;
    }

    /**
     * �����վ����10�����
     * @param charge10 double
     */
    public void setCharge10(double charge10) {
        this.charge10 = charge10;
    }

    /**
     * �õ��վ����10�����
     * @return double
     */
    public double getCharge10() {
        return charge10;
    }

    /**
     * �����վ����11�����
     * @param charge11 double
     */
    public void setCharge11(double charge11) {
        this.charge11 = charge11;
    }

    /**
     * �õ��վ����11�����
     * @return double
     */
    public double getCharge11() {
        return charge11;
    }

    /**
     * �����վ����12�����
     * @param charge12 double
     */
    public void setCharge12(double charge12) {
        this.charge12 = charge12;
    }

    /**
     * �õ��վ����12�����
     * @return double
     */
    public double getCharge12() {
        return charge12;
    }

    /**
     * �����վ����13�����
     * @param charge13 double
     */
    public void setCharge13(double charge13) {
        this.charge13 = charge13;
    }

    /**
     * �õ��վ����13�����
     * @return double
     */
    public double getCharge13() {
        return charge13;
    }

    /**
     * �����վ����14�����
     * @param charge14 double
     */
    public void setCharge14(double charge14) {
        this.charge14 = charge14;
    }

    /**
     * �õ��վ����14�����
     * @return double
     */
    public double getCharge14() {
        return charge14;
    }

    /**
     * �����վ����15�����
     * @param charge15 double
     */
    public void setCharge15(double charge15) {
        this.charge15 = charge15;
    }

    /**
     * �õ��վ����15�����
     * @return double
     */
    public double getCharge15() {
        return charge15;
    }

    /**
     * �����վ����16�����
     * @param charge16 double
     */
    public void setCharge16(double charge16) {
        this.charge16 = charge16;
    }

    /**
     * �õ��վ����16�����
     * @return double
     */
    public double getCharge16() {
        return charge16;
    }

    /**
     * �����վ����17�����
     * @param charge17 double
     */
    public void setCharge17(double charge17) {
        this.charge17 = charge17;
    }

    /**
     * �õ��վ����17�����
     * @return double
     */
    public double getCharge17() {
        return charge17;
    }

    /**
     * �����վ����18�����
     * @param charge18 double
     */
    public void setCharge18(double charge18) {
        this.charge18 = charge18;
    }

    /**
     * �õ��վ����18�����
     * @return double
     */
    public double getCharge18() {
        return charge18;
    }

    /**
     * �����վ����19�����
     * @param charge19 double
     */
    public void setCharge19(double charge19) {
        this.charge19 = charge19;
    }

    /**
     * �õ��վ����18�����
     * @return double
     */
    public double getCharge19() {
        return charge19;
    }

    /**
     * �����վ����20�����
     * @param charge20 double
     */
    public void setCharge20(double charge20) {
        this.charge20 = charge20;
    }

    /**
     * �õ��վ����20�����
     * @return double
     */
    public double getCharge20() {
        return charge20;
    }

    /**
     * �����վ����21�����
     * @param charge21 double
     */
    public void setCharge21(double charge21) {
        this.charge21 = charge21;
    }

    /**
     * �õ��վ����21�����
     * @return double
     */
    public double getCharge21() {
        return charge21;
    }

    /**
     * �����վ����22�����
     * @param charge22 double
     */
    public void setCharge22(double charge22) {
        this.charge22 = charge22;
    }

    /**
     * �õ��վ����22�����
     * @return double
     */
    public double getCharge22() {
        return charge22;
    }

    /**
     * �����վ����23�����
     * @param charge23 double
     */
    public void setCharge23(double charge23) {
        this.charge23 = charge23;
    }

    /**
     * �õ��վ����23�����
     * @return double
     */
    public double getCharge23() {
        return charge23;
    }

    /**
     * �����վ����24�����
     * @param charge24 double
     */
    public void setCharge24(double charge24) {
        this.charge24 = charge24;
    }

    /**
     * �õ��վ����24�����
     * @return double
     */
    public double getCharge24() {
        return charge24;
    }

    /**
     * �����վ����25�����
     * @param charge25 double
     */
    public void setCharge25(double charge25) {
        this.charge25 = charge25;
    }

    /**
     * �õ��վ����25�����
     * @return double
     */
    public double getCharge25() {
        return charge25;
    }

    /**
     * �����վ����26�����
     * @param charge26 double
     */
    public void setCharge26(double charge26) {
        this.charge26 = charge26;
    }

    /**
     * �õ��վ����26�����
     * @return double
     */
    public double getCharge26() {
        return charge26;
    }

    /**
     * �����վ����27�����
     * @param charge27 double
     */
    public void setCharge27(double charge27) {
        this.charge27 = charge27;
    }

    /**
     * �õ��վ����27�����
     * @return double
     */
    public double getCharge27() {
        return charge27;
    }

    /**
     * �����վ����28�����
     * @param charge28 double
     */
    public void setCharge28(double charge28) {
        this.charge28 = charge28;
    }

    /**
     * �õ��վ����28�����
     * @return double
     */
    public double getCharge28() {
        return charge28;
    }

    /**
     * �����վ����29�����
     * @param charge29 double
     */
    public void setCharge29(double charge29) {
        this.charge29 = charge29;
    }

    /**
     * �õ��վ����29�����
     * @return double
     */
    public double getCharge29() {
        return charge29;
    }

    /**
     * �����վ����30�����
     * @param charge30 double
     */
    public void setCharge30(double charge30) {
        this.charge30 = charge30;
    }

    /**
     * �õ��վ����30�����
     * @return double
     */
    public double getCharge30() {
        return charge30;
    }

    /**
     * ����Ӧ�����
     * @param totAmt double
     */
    public void setTotAmt(double totAmt) {
        this.totAmt = totAmt;
    }

    /**
     * �õ�Ӧ�����
     * @return double
     */
    public double getTotAmt() {
        return totAmt;
    }

    /**
     * �����ۿ�/����ԭ��
     * @param reduceReason String
     */
    public void setReduceReason(String reduceReason) {
        this.reduceReason = reduceReason;
    }

    /**
     * �õ��ۿ�/����ԭ��
     * @return String
     */
    public String getReduceReason() {
        return reduceReason;
    }

    /**
     * �����ۿ�/�������
     * @param reduceDeptCode String
     */
    public void setReduceDeptCode(String reduceDeptCode) {
        this.reduceDeptCode = reduceDeptCode;
    }

    /**
     * �õ��ۿ�/�������
     * @return String
     */
    public String getReduceDeptCode() {
        return reduceDeptCode;
    }

    /**
     * �����ۿ�/������
     * @param reduceAmt double
     */
    public void setReduceAmt(double reduceAmt) {
        this.reduceAmt = reduceAmt;
    }

    /**
     * �õ��ۿ�/������
     * @return double
     */
    public double getReduceAmt() {
        return reduceAmt;
    }

    /**
     * �����ۿ�/����ʱ��
     * @param reduceDate Timestamp
     */
    public void setReduceDate(Timestamp reduceDate) {
        this.reduceDate = reduceDate;
    }

    /**
     * �õ��ۿ�/����ʱ��
     * @return Timestamp
     */
    public Timestamp getReduceDate() {
        return reduceDate;
    }

    /**
     * �����ۿ���Ա
     * @param reduceRespond String
     */
    public void setReduceRespond(String reduceRespond) {
        this.reduceRespond = reduceRespond;
    }

    /**
     * �õ��ۿ���Ա
     * @return String
     */
    public String getReduceRespond() {
        return reduceRespond;
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
     * ����ˢ��
     * @param payBankCard double
     */
    public void setPayBankCard(double payBankCard) {
        this.payBankCard = payBankCard;
    }

    /**
     * �õ�ˢ��
     * @return double
     */
    public double getPayBankCard() {
        return payBankCard;
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
     * ���ü���֧��
     * @param payDepit double
     */
    public void setPayDepit(double payDepit) {
        this.payDepit = payDepit;
    }

    /**
     * �õ�����֧��
     * @return double
     */
    public double getPayDepit() {
        return payDepit;
    }

    /**
     * ����Ԥ����֧��
     * @param payBilPay double
     */
    public void setPayBilPay(double payBilPay) {
        this.payBilPay = payBilPay;
    }

    /**
     * �õ�Ԥ����֧��
     * @return double
     */
    public double getPayBilPay() {
        return payBilPay;
    }

    /**
     * ����ҽ��֧��
     * @param payIns double
     */
    public void setPayIns(double payIns) {
        this.payIns = payIns;
    }

    /**
     * �õ�ҽ��֧��
     * @return double
     */
    public double getPayIns() {
        return payIns;
    }

    /**
     * ��������֧��1
     * @param payOther1 double
     */
    public void setPayOther1(double payOther1) {
        this.payOther1 = payOther1;
    }

    /**
     * �õ�����֧��1
     * @return double
     */
    public double getPayOther1() {
        return payOther1;
    }

    /**
     * ��������֧��2
     * @param payOther2 double
     */
    public void setPayOther2(double payOther2) {
        this.payOther2 = payOther2;
    }

    /**
     * �õ�����֧��2
     * @return double
     */
    public double getPayOther2() {
        return payOther2;
    }

    /**
     * �����տע
     * @param payRemark String
     */
    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
    }

    /**
     * �õ��տע
     * @return String
     */
    public String getPayRemark() {
        return payRemark;
    }

    /**
     * �����շ�Ա
     * @param cashierCode String
     */
    public void setCashierCode(String cashierCode) {
        this.cashierCode = cashierCode;
    }

    /**
     * �õ��շ�Ա
     * @return String
     */
    public String getCashierCode() {
        return cashierCode;
    }

    /**
     * �����ս��ʱ�־
     * @param accountFlg String
     */
    public void setAccountFlg(String accountFlg) {
        this.accountFlg = accountFlg;
    }

    /**
     * �õ��ս��ʱ�־
     * @return String
     */
    public String getAccountFlg() {
        return accountFlg;
    }

    /**
     * �����ս�������
     * @param accountDate Timestamp
     */
    public void setAccountDate(Timestamp accountDate) {
        this.accountDate = accountDate;
    }

    /**
     * �õ��ս�������
     * @return Timestamp
     */
    public Timestamp getAccountDate() {
        return accountDate;
    }

    /**
     * �����ս����Ա
     * @param accountUser String
     */
    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    /**
     * �õ��ս����Ա
     * @return String
     */
    public String getAccountUser() {
        return accountUser;
    }

    /**
     * ���ü��˵�λ����
     * @param contractCode String
     */
    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    /**
     * �õ����˵�λ����
     * @return String
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * �������н������
     * @param bankSeq String
     */
    public void setBankSeq(String bankSeq) {
        this.bankSeq = bankSeq;
    }

    /**
     * �õ����н������
     * @return String
     */
    public String getBankSeq() {
        return bankSeq;
    }

    /**
     * �������н���ʱ��
     * @param bankDate Timestamp
     */
    public void setBankDate(Timestamp bankDate) {
        this.bankDate = bankDate;
    }

    /**
     * �õ������н���ʱ��
     * @return Timestamp
     */
    public Timestamp getBankDate() {
        return bankDate;
    }

    /**
     * ���������н�����������Ա
     * @param bankUser String
     */
    public void setBankUser(String bankUser) {
        this.bankUser = bankUser;
    }

    /**
     * �õ����н�����������Ա
     * @return String
     */
    public String getBankUser() {
        return bankUser;
    }

    /**
     * ���ò���Ա
     * @param optUser String
     */
    public void setOptUser(String optUser) {
        this.optUser = optUser;
    }

    /**
     * �õ�����Ա
     * @return String
     */
    public String getOptUser() {
        return optUser;
    }

    /**
     * ���ò���ʱ��
     * @param optDate Timestamp
     */
    public void setOptDate(Timestamp optDate) {
        this.optDate = optDate;
    }

    /**
     * �õ�����ʱ��
     * @return Timestamp
     */
    public Timestamp getOptDate() {
        return optDate;
    }

    /**
     * ���ò����ն�
     * @param optTerm String
     */
    public void setOptTerm(String optTerm) {
        this.optTerm = optTerm;
    }

    /**
     * �õ������ն�
     * @return String
     */
    public String getOptTerm() {
        return optTerm;
    }

    // ������
    public OPBReceipt() {
        StringBuffer sb = new StringBuffer();
        // �������
        sb.append("caseNo:CASE_NO;");
        //�վݺ�
        sb.append("receiptNo:RECEIPT_NO;");
        // �ż�ס��
        sb.append("admType:ADM_TYPE;");
        //����
        sb.append("region:REGION_CODE;");
        // ������
        sb.append("mrNo:MR_NO;");
        //���ԭ�վ����
        sb.append("resetReceiptNo:RESET_RECEIPT_NO;");
        //�վ�ӡˢ����
        sb.append("printNo:PRINT_NO;");
        //��������
        sb.append("billDate:BILL_DATE;");
        //�վݴ�ӡ����
        sb.append("chargeDate:CHARGE_DATE;");
        // ��ӡ���վ��ϵ��շ�����
        sb.append("printDate:PRINT_DATE;");
        // �վ����01�����
        sb.append("charge01:CHARGE01;");
        // �վ����02�����
        sb.append("charge02:CHARGE02;");
        // �վ����03�����
        sb.append("charge03:CHARGE03;");
        // �վ����04�����
        sb.append("charge04:CHARGE04;");
        // �վ����05�����
        sb.append("charge05:CHARGE05;");
        // �վ����06�����
        sb.append("charge06:CHARGE06;");
        // �վ����7�����
        sb.append("charge07:CHARGE07;");
        // �վ����8�����
        sb.append("charge08:CHARGE08;");
        // �վ����1�����
        sb.append("charge09:CHARGE09;");
        // �վ����10�����
        sb.append("charge10:CHARGE10;");
        // �վ����11�����
        sb.append("charge11:CHARGE11;");
        // �վ����12�����
        sb.append("charge12:CHARGE12;");
        // �վ����13�����
        sb.append("charge13:CHARGE13;");
        // �վ����14�����
        sb.append("charge14:CHARGE14;");
        // �վ����15�����
        sb.append("charge15:CHARGE15;");
        // �վ����16�����
        sb.append("charge16:CHARGE16;");
        // �վ����17�����
        sb.append("charge17:CHARGE17;");
        // �վ����1�����
        sb.append("charge18:CHARGE18;");
        // �վ����19�����
        sb.append("charge19:CHARGE19;");
        // �վ����20�����
        sb.append("charge20:CHARGE20;");
        // �վ����21�����
        sb.append("charge21:CHARGE21;");
        // �վ����22�����
        sb.append("charge22:CHARGE22;");
        // �վ����23�����
        sb.append("charge23:CHARGE23;");
        // �վ����24�����
        sb.append("charge24:CHARGE24;");
        // �վ����25�����
        sb.append("charge25:CHARGE25;");
        // �վ����26�����
        sb.append("charge26:CHARGE26;");
        // �վ����27�����
        sb.append("charge27:CHARGE27;");
        // �վ����28�����
        sb.append("charge28:CHARGE28;");
        // �վ����29�����
        sb.append("charge29:CHARGE29;");
        // �վ����30�����
        sb.append("charge30:CHARGE30;");
        // Ӧ�����
        sb.append("totAmt:TOT_AMT;");
        //�ۿ�/����ԭ��
        sb.append("reduceReason:REDUCE_REASON;");
        // �ۿ�/������
        sb.append("reduceAmt:REDUCE_AMT;");
        // �ۿ�/����ʱ��
        sb.append("reduceDate:REDUCE_DATE;");
        //�ۿ�/�������
        sb.append("reduceDeptCode:REDUCE_DEPT_CODE;");
        // �ۿ���Ա
        sb.append("reduceRespond:REDUCE_RESPOND;");
        // Ӧ�ս��
        sb.append("arAmt:AR_AMT;");
        //�ֽ�֧��
        sb.append("payCash:PAY_CASH;");
        // ҽ�ƿ�֧��
        sb.append("payMedicalCard:PAY_MEDICAL_CARD;");
        //ˢ��
        sb.append("payBankCard:PAY_BANK_CARD;");
        //ҽ����֧��
        sb.append("payInsCard:PAY_INS_CARD;");
        //֧Ʊ֧��
        sb.append("payCheck:PAY_CHECK;");
        //����֧��
        sb.append("payDepit:PAY_DEBIT;");
        //Ԥ����֧��
        sb.append("payBilPay:PAY_BILPAY;");
        //ҽ��
        sb.append("payIns:PAY_INS;");
        //����֧��1
        sb.append("payOther1:PAY_OTHER1;");
        //����֧����
        sb.append("payOther2:PAY_OTHER2;");
        //�տע
        sb.append("payRemark:PAY_REMARK;");
        //�շ�Ա
        sb.append("cashierCode:CASHIER_CODE;");
        //�ս��ʱ�־
        sb.append("accountFlg:ACCOUNT_FLG;");
        //�ս�������
        sb.append("accountDate:ACCOUNT_DATE;");
        //�ս����Ա
        sb.append("accountUser:ACCOUNT_USER;");
        //���˵�λ����
        sb.append("contractCode:ACCOUNT_CODE;");
        //���н������
        sb.append("bankSeq:BANK_SEQ;");
        //���н���ʱ��
        sb.append("bankDate:BANK_DATE;");
        //���н�����������Ա
        sb.append("bankUser:BANK_USER;");
        //����Ա
        sb.append("optUser:OPT_USER;");
        //����ʱ��
        sb.append("optDate:OPT_DATE;");
        //�����ն�
        sb.append("optTerm:OPT_TERM;");
        setMapString(sb.toString());
    }

    /**
     *
     * @param parm TParm
     */
    public void initCharge(TParm parm) {
        this.setCharge01(parm.getDouble("CHARGE01"));
        this.setCharge02(parm.getDouble("CHARGE02"));
        this.setCharge03(parm.getDouble("CHARGE03"));
        this.setCharge04(parm.getDouble("CHARGE04"));
        this.setCharge05(parm.getDouble("CHARGE05"));
        this.setCharge06(parm.getDouble("CHARGE06"));
        this.setCharge07(parm.getDouble("CHARGE07"));
        this.setCharge08(parm.getDouble("CHARGE08"));
        this.setCharge09(parm.getDouble("CHARGE09"));
        this.setCharge10(parm.getDouble("CHARGE10"));
        this.setCharge11(parm.getDouble("CHARGE11"));
        this.setCharge12(parm.getDouble("CHARGE12"));
        this.setCharge13(parm.getDouble("CHARGE13"));
        this.setCharge14(parm.getDouble("CHARGE14"));
        this.setCharge15(parm.getDouble("CHARGE15"));
        this.setCharge16(parm.getDouble("CHARGE16"));
        this.setCharge17(parm.getDouble("CHARGE17"));
        this.setCharge18(parm.getDouble("CHARGE18"));
        this.setCharge19(parm.getDouble("CHARGE19"));
        this.setCharge20(parm.getDouble("CHARGE20"));
        this.setCharge21(parm.getDouble("CHARGE21"));
        this.setCharge22(parm.getDouble("CHARGE22"));
        this.setCharge23(parm.getDouble("CHARGE23"));
        this.setCharge24(parm.getDouble("CHARGE24"));
        this.setCharge25(parm.getDouble("CHARGE25"));
        this.setCharge26(parm.getDouble("CHARGE26"));
        this.setCharge27(parm.getDouble("CHARGE27"));
        this.setCharge28(parm.getDouble("CHARGE28"));
        this.setCharge29(parm.getDouble("CHARGE29"));
        this.setCharge30(parm.getDouble("CHARGE30"));
    }

    /**
     * �õ����ز���
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        return parm;
    }

    /** 
     * �õ�һ��Ʊ��
     * @param receiptNo String
     * @return OPBReceipt
     */
    public OPBReceipt getOneReceipt(String receiptNo) {
        //��ȡ�շ�����
        TParm result = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
        if (result.getErrCode() < 0)
            return null;
        int row = result.getCount();
        if (row <= 0)
            return null;
        return getReceiptByTParm(result.getRow(0), 1);
    }

    /**
     * ����һ��TParm����һ��Ʊ�ݶ���
     * @param parm TParm
     * @param mark int
     * @return OPBReceipt
     */
    public OPBReceipt getReceiptByTParm(TParm parm, int mark) {
        OPBReceipt opbReceiptOne = new OPBReceipt();
        //�������
        opbReceiptOne.setCaseNo(parm.getValue("CASE_NO"));
        //Ʊ�ݺ�
        opbReceiptOne.setReceiptNo(parm.getValue("RECEIPT_NO"));
        //�ż�ס��
        opbReceiptOne.setAdmType(parm.getValue("ADM_TYPE"));
        //����
        opbReceiptOne.setRegion(parm.getValue("REGION"));
        //������
        opbReceiptOne.setMrNo(parm.getValue("MR_NO"));
        //�س��վݺ�
        opbReceiptOne.setResetReceiptNo(parm.getValue("RESET_RECEIPT_NO"));
        //��ӡ��Ʊ�ݺ�
        opbReceiptOne.setPrintNo(parm.getValue("PRINT_NO"));
        //�շ�ʱ��
        opbReceiptOne.setBilDate( (Timestamp) parm.getData("BILL_DATE"));
        //�Ʒ�ʱ��
        opbReceiptOne.setChargeDate( (Timestamp) parm.getData("CHARGE_DATE"));
        //��Ʊʱ��
        opbReceiptOne.setPrintDate(parm.getTimestamp("PRINT_DATE"));
        //�շ���Ŀ��ϸ1~30
        opbReceiptOne.setCharge01(parm.getDouble("CHARGE01") * mark);
        opbReceiptOne.setCharge02(parm.getDouble("CHARGE02") * mark);
        opbReceiptOne.setCharge03(parm.getDouble("CHARGE03") * mark);
        opbReceiptOne.setCharge04(parm.getDouble("CHARGE04") * mark);
        opbReceiptOne.setCharge05(parm.getDouble("CHARGE05") * mark);
        opbReceiptOne.setCharge06(parm.getDouble("CHARGE06") * mark);
        opbReceiptOne.setCharge07(parm.getDouble("CHARGE07") * mark);
        opbReceiptOne.setCharge08(parm.getDouble("CHARGE08") * mark);
        opbReceiptOne.setCharge09(parm.getDouble("CHARGE09") * mark);
        opbReceiptOne.setCharge10(parm.getDouble("CHARGE10") * mark);
        opbReceiptOne.setCharge11(parm.getDouble("CHARGE11") * mark);
        opbReceiptOne.setCharge12(parm.getDouble("CHARGE12") * mark);
        opbReceiptOne.setCharge13(parm.getDouble("CHARGE13") * mark);
        opbReceiptOne.setCharge14(parm.getDouble("CHARGE14") * mark);
        opbReceiptOne.setCharge15(parm.getDouble("CHARGE15") * mark);
        opbReceiptOne.setCharge16(parm.getDouble("CHARGE16") * mark);
        opbReceiptOne.setCharge17(parm.getDouble("CHARGE17") * mark);
        opbReceiptOne.setCharge18(parm.getDouble("CHARGE18") * mark);
        opbReceiptOne.setCharge19(parm.getDouble("CHARGE19") * mark);
        opbReceiptOne.setCharge20(parm.getDouble("CHARGE20") * mark);
        opbReceiptOne.setCharge21(parm.getDouble("CHARGE21") * mark);
        opbReceiptOne.setCharge22(parm.getDouble("CHARGE22") * mark);
        opbReceiptOne.setCharge23(parm.getDouble("CHARGE23") * mark);
        opbReceiptOne.setCharge24(parm.getDouble("CHARGE24") * mark);
        opbReceiptOne.setCharge25(parm.getDouble("CHARGE25") * mark);
        opbReceiptOne.setCharge26(parm.getDouble("CHARGE26") * mark);
        opbReceiptOne.setCharge27(parm.getDouble("CHARGE27") * mark);
        opbReceiptOne.setCharge28(parm.getDouble("CHARGE28") * mark);
        opbReceiptOne.setCharge29(parm.getDouble("CHARGE29") * mark);
        opbReceiptOne.setCharge30(parm.getDouble("CHARGE30") * mark);
        //TOT_AMT;REDUCE_AMT;AR_AMT;PAY_CASH;PAY_MEDICAL_CARD;
        //PAY_BANK_CARD;PAY_INS_CARD;PAY_CHECK;PAY_DEBIT;PAY_BILPAY;PAY_INS;PAY_OTHER1;PAY_OTHER2;CASHIER_CODE
        //����֧����ʽ
        opbReceiptOne.setTotAmt(parm.getDouble("TOT_AMT") * mark);
        opbReceiptOne.setReduceAmt(parm.getDouble("REDUCE_AMT") * mark);
        opbReceiptOne.setArAmt(parm.getDouble("AR_AMT") * mark);
        opbReceiptOne.setPayCash(parm.getDouble("PAY_CASH") * mark);
        opbReceiptOne.setPayMedicalCard(parm.getDouble("PAY_MEDICAL_CARD") *
                                        mark);
        opbReceiptOne.setPayBankCard(parm.getDouble("PAY_BANK_CARD") * mark);
        opbReceiptOne.setPayInsCard(parm.getDouble("PAY_INS_CARD") * mark);
        opbReceiptOne.setPayCheck(parm.getDouble("PAY_CHECK") * mark);
        opbReceiptOne.setPayDepit(parm.getDouble("PAY_DEBIT") * mark);
        opbReceiptOne.setPayBilPay(parm.getDouble("PAY_BILPAY") * mark);
        opbReceiptOne.setPayIns(parm.getDouble("PAY_INS") * mark);
        opbReceiptOne.setPayOther1(parm.getDouble("PAY_OTHER1") * mark);
        opbReceiptOne.setPayOther2(parm.getDouble("PAY_OTHER2") * mark);
        //֧Ʊ��
        opbReceiptOne.setPayRemark(parm.getValue("PAY_REMARK"));
        //�շ�Ա
        opbReceiptOne.setCashierCode(parm.getValue("CASHIER_CODE"));
        //�ս���
        opbReceiptOne.setAccountFlg("N");
        //�ս�����
        opbReceiptOne.setAccountDate(null);
        //�ս���Ա
        opbReceiptOne.setAccountUser("");
        //
        opbReceiptOne.setContractCode("");
        //���н��˺�
        opbReceiptOne.setBankSeq("");
        //����ʱ��
        opbReceiptOne.setBankDate(null);
        //������Ա
        opbReceiptOne.setBankUser("");
        return opbReceiptOne;
    }

    /**
     * �����˷�ʱ�����ordre
     */
    public void setBackReceiptValue() {
        int count = this.orderList.size(orderList.PRIMARY);
        for (int i = 0; i < count; i++) {
            Order order = (Order) orderList.getOrder(i);
            dealBackReceiptValue(order);
        }
    }

    /**
     * �����˷�ʱorder������
     * @param order Order
     */
    public void dealBackReceiptValue(Order order) {
        order.modifyChargeFlg(false);
        order.modifyBillDate(null);
        order.modifyBillFlg("N");
        order.setReceiptNo("");
    }

    /**
     * ��ʼ��Ʊ�ݴ���ҽ��
     * @param prescriptionList PrescriptionList
     */
    public void initOrderList(PrescriptionList prescriptionList) {
        //�Ӵ���ǩ��ȡ�����ϱ�Ʊ�ݵ�orderList
        OrderList odList = prescriptionList.getOrderList(this);
        //���뱾Ʊ����
        this.setOrderList(odList);
    }
}
