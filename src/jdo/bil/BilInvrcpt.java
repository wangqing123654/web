package jdo.bil;

import com.dongyang.data.TModifiedData;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
/**
 *
 * <p>Title:Ʊ�ݹ������ </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class BilInvrcpt
    extends TModifiedData{
    /**
     * �վ���� RECP_TYPE
     */
    private String recpType;
    /**
     * Ʊ�ݺ� INV_NO
     */
    private String invNo;
    /**
     * �վݺ� RECEIPT_NO
     */
    private String receiptNo;
    /**
     * ������Ա CASHIER_CODE
     */
    private String cashierCode;

    /**
     * �ܽ�� TOT_AMT
     */
    private double arAmt;
    /**
     * ȡ��ע�� CANCEL_FLG
     */
    private String cancelFlg;

    /**
     * ȡ����Ա CANCEL_USER
     */
    private String cancelUser;
    /**
     * ȡ������ CANCEL_DATE
     */
    private Timestamp cancelDate;

    // ������
    public BilInvrcpt() {
        StringBuffer sb = new StringBuffer();
        //�վ����
        sb.append("recpType:RECP_TYPE;");
        //Ʊ�ݺ�
        sb.append("invNo:INV_NO;");
        //�վݺ�
        sb.append("receiptNo:RECEIPT_NO;");
        //������Ա
        sb.append("cashierCode:CASHIER_CODE;");
        //�ܽ��
        sb.append("arAmt:AR_AMT;");
        //ȡ��ע��
        sb.append("cancelFlg:CANCEL_FLG;");
        //ȡ����Ա
        sb.append("cancelUser:CANCEL_USER;");
        //ȡ������
        sb.append("cancelDate:CANCEL_DATE;");
        setMapString(sb.toString());
    }
    /**
     * �����վ����
     * @param recpType String
     */
    public void setRecpType(String recpType) {
        this.recpType = recpType;
    }
    /**
     * �õ��վ����
     * @return String
     */
    public String getRecpType() {
        return recpType;
    }
    /**
     * ����Ʊ�ݺ�
     * @param invNo String
     */
    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }
    /**
     * �õ�Ʊ�ݺ�
     * @return String
     */
    public String getInvNo() {
        return invNo;
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
     * ����������Ա
     * @param cashierCode String
     */
    public void setCashierCode(String cashierCode) {
        this.cashierCode = cashierCode;
    }
    /**
     * �õ�������Ա
     * @return String
     */
    public String getCashierCode() {
        return cashierCode;
    }
    /**
     * �����ܽ��
     * @param arAmt double
     */
    public void setArAmt(double arAmt) {
        this.arAmt = arAmt;
    }
    /**
     * �õ��ܽ��
     * @return double
     */
    public double getArAmt() {
        return arAmt;
    }
    /**
     * ����ȡ��ע��
     * @param cancelFlg String
     */
    public void setCancelFlg(String cancelFlg) {
        this.cancelFlg = cancelFlg;
    }
    /**
     * �õ�ȡ��ע��
     * @return String
     */
    public String getCancelFlg() {
        return cancelFlg;
    }
    /**
     *  ����ȡ����Ա
     * @param cancelUser String
     */
    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }
    /**
     * �õ�ȡ����Ա
     * @return String
     */
    public String getCancelUser() {
        return cancelUser;
    }
    /**
     * ����ȡ������
     * @param cancelDate Timestamp
     */
    public void setCancelDate(Timestamp cancelDate) {
        this.cancelDate = cancelDate;
    }
    /**
     * �õ�ȡ������
     * @return String
     */

    public Timestamp getCancelDate() {
        return cancelDate;
    }
    /**
     * �õ����ز���
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_DATE",  SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }
    /**
     * RECP_TYPE
     * @param recpType String
     * @param invNo String
     * @return boolean
     */
    public boolean initBilInvrcpt( String recpType,String invNo) {
        if (recpType == null ||
            recpType.length() == 0||invNo==null||invNo.length()==0) {
            out("getOneInv���δ���");
            return false;
        }
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", recpType);
        parm.setData("INV_NO",invNo);
        TParm result = BILInvrcptTool.getInstance().getOneInv(parm);
        if (result.getErrCode() < 0) {
            out("getOneInv���ش���");
            return false;
        }
        if(result.getCount("RECEIPT_NO")>1){
            out("getOneInv���ش���");
            return false;
        }
        if (result.getCount() == 1)
            return true;
        return false;
    }
    /**
     * ���ݴ���������װһ��Ʊ�ݶ���
     * @param bilInvrcpt BilInvrcpt
     * @param parm TParm
     * @return BilInvrcpt
     */
    public BilInvrcpt setValueForParm(BilInvrcpt bilInvrcpt,TParm parm){
//        System.out.println("�������parm="+parm);
        //�վ����
        bilInvrcpt.setRecpType(parm.getValue("RECP_TYPE"));
        //Ʊ�ݺ�
        bilInvrcpt.setInvNo(parm.getValue("INV_NO"));
        //�վݺ�
        bilInvrcpt.setReceiptNo(parm.getValue("RECEIPT_NO"));
        //������Ա
        bilInvrcpt.setCashierCode(parm.getValue("CASHIER_CODE"));
        //�ܽ��
        bilInvrcpt.setArAmt(parm.getDouble("AR_AMT"));
        //ȡ��ע��
        bilInvrcpt.setCancelFlg(parm.getValue("CANCEL_FLG"));
        //ȡ����Ա
        bilInvrcpt.setCancelUser(parm.getValue("CANCEL_USER"));
        //ȡ������
        bilInvrcpt.setCancelDate((Timestamp)parm.getData("CANCEL_DATE"));
//        System.out.println("����parm�������============="+bilInvrcpt.getParm());
        return bilInvrcpt;
    }
}
