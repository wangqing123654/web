package jdo.bil;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: Ʊ�ݹ������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class BilInvoice
    extends TModifiedData {
    /**
     * �վ���� RECP_TYPE
     */
    private String recpType;
    /**
     * ��ʼƱ�� START_INVNO
     */
    private String startInvno;
    /**
     * �������� START_VALID_DATE
     */
    private String startValidDate;
    /**
     * ����Ʊ�� END_INVNO
     */
    private String endInvno;
    /**
     * ��ǰƱ�� UPDATE_NO
     */
    private String updateNo;
    /**
     * ������Ա CASHIER_CODE
     */
    private String cashierCode;
    /**
     * �������� END_VALID_DATE
     */
    private Timestamp endValidDate;
    /**
     * ʹ��״̬ STATUS
     */
    private String status;
    /**
     * ʹ����IP
     */
    private String termIP;

    // ������
    public BilInvoice() {
        StringBuffer sb = new StringBuffer();
        //�վ����
        sb.append("recpType:RECP_TYPE;");
        //��ʼƱ��
        sb.append("startInvno:START_INVNO;");
        //��������
        sb.append("startValidDate:START_VALID_DATE;");
        //����Ʊ��
        sb.append("endInvno:END_INVNO;");
        //��ǰƱ��
        sb.append("updateNo:UPDATE_NO;");
        //������Ա
        sb.append("cashierCode:CASHIER_CODE;");
        //��������
        sb.append("endValidDate:END_VALID_DATE;");
        //ʹ��״̬
        sb.append("status:STATUS;");
        sb.append("termIP:TERM_IP;");
        setMapString(sb.toString());
    }

    /**
     * ���γ�ʼ��
     * @param recpType String
     */
    public BilInvoice(String recpType) {

        TParm parm = new TParm();
        parm.setData("RECP_TYPE", recpType);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm result = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
        }
        this.setRecpType(result.getValue("RECP_TYPE", 0));
        this.setUpdateNo(result.getValue("UPDATE_NO", 0));
        this.setCashierCode(result.getValue("CASHIER_CODE", 0));
        this.setEndInvno(result.getValue("END_INVNO", 0));
        this.setStartInvno(result.getValue("START_INVNO", 0));
        this.setStatus(result.getValue("STATUS", 0));
        this.setTermIP(result.getValue("TERM_IP", 0));

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
     * ������ʼƱ��
     * @param startInvno String
     */
    public void setStartInvno(String startInvno) {
        this.startInvno = startInvno;
    }

    /**
     * �õ���ʼƱ��
     * @return String
     */
    public String getStartInvno() {
        return startInvno;
    }

    /**
     * ������������
     * @param startValidDate String
     */
    public void setStartValidDate(String startValidDate) {
        this.startValidDate = startValidDate;
    }

    /**
     * �õ���������
     * @return String
     */
    public String getStartValidDate() {
        return startValidDate;
    }

    /**
     * ���ý���Ʊ��
     * @param endInvno String
     */
    public void setEndInvno(String endInvno) {
        this.endInvno = endInvno;
    }

    /**
     * �õ�����Ʊ��
     * @return String
     */
    public String getEndInvno() {
        return endInvno;
    }

    /**
     * ���õ�ǰƱ��
     * @param updateNo String
     */
    public void setUpdateNo(String updateNo) {
        this.updateNo = updateNo;
    }

    /**
     * �õ���ǰƱ��
     * @return String
     */
    public String getUpdateNo() {
        return updateNo;
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
     * ���ý�������
     * @param endValidDate Timestamp
     */
    public void setEndValidDate(Timestamp endValidDate) {
        this.endValidDate = endValidDate;
    }

    /**
     * �õ���������
     * @return String
     */
    public Timestamp getEndValidDate() {
        return endValidDate;
    }

    /**
     * ����ʹ��״̬
     * @param status String
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * �õ�ʹ��״̬
     * @return String
     */

    public String getStatus() {
        return status;
    }

    /**
     * ����ʹ����IP
     * @param termIP String
     */
    public void setTermIP(String termIP) {
        this.termIP = termIP;
    }

    /**
     * �õ�ʹ����IP
     * @return String
     */
    public String getTermIP() {
        return termIP;
    }

    /**
     * �õ����ز���
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * ��ʼ����ǰʹ�õ�Ʊ��
     * @param receType String
     * @return BilInvoice
     */
    public BilInvoice initBilInvoice(String receType) {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", receType);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm result = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        BilInvoice bilInvoice = new BilInvoice();
        bilInvoice.setRecpType(result.getValue("RECP_TYPE", 0));
        bilInvoice.setUpdateNo(result.getValue("UPDATE_NO", 0));
        bilInvoice.setCashierCode(result.getValue("CASHIER_CODE", 0));
        bilInvoice.setEndInvno(result.getValue("END_INVNO", 0));
        bilInvoice.setStartInvno(result.getValue("START_INVNO", 0));
        bilInvoice.setStatus(result.getValue("STATUS", 0));
        bilInvoice.setTermIP(result.getValue("TERM_IP", 0));
        return bilInvoice;
    }

    /**
     * ���Ʊ����Ч��
     * @return boolean
     */
    public boolean checkEndInvNo() {
        if (this.getUpdateNo() == null || this.getUpdateNo().length() == 0 ||
            this.getUpdateNo().equals("null")) {
            return false;
        }
//        if(this.getUpdate)
        return true;
    }
}
