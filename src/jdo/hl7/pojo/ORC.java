package jdo.hl7.pojo;

import com.javahis.exception.HL7Exception;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ORC extends MessageBase {
    /**
     * MSH ����
     */
    private MSH msh;
    /**
     * ҽ������
     */
    private String orderControl;
    /**
     * ҽ�����
     */
    private String orderNo;
    /**
     * ҽ��˳���
     */
    private String orderSeqNo;
    /**
     * Ӧ��ע��
     */
    private String responseFlg;
    /**
     * ��������
     */
    private String reserveDate;
    /**
     * ����ҽʦ����
     */
    private String orderDrCode;
    /**
     * ����ҽʦ������
     */
    private String orderDrName;

    /**
     * ���� MSH ����
     * @param msh MSH
     */
    public void setMSH(MSH msh) {
        this.msh = msh;
    }

    /**
     * �õ� MSH ����
     * @return MSH
     */
    public MSH getMSH() {
        return msh;
    }

    /**
     * ����ҽ������
     * @param orderControl String
     * @throws HL7Exception
     * NW������ CA��ȡ��
     */
    public void setOrderControl(String orderControl) throws HL7Exception {
        this.orderControl = check(orderControl, new String[] {"NW", "CA", "CH"});
    }

    /**
     * ȡ��ҽ������
     * @return String
     */
    public String getOrderControl() {
        return check(orderControl);
    }

    /**
     * ����ҽ�����
     * @param orderNo String
     * @throws HL7Exception
     */
    public void setOrderNo(String orderNo) throws HL7Exception {
        this.orderNo = check(orderNo);
    }

    /**
     * ȡ��ҽ�����
     * @return String
     */
    public String getOrderNo() {
        return check(orderNo);
    }

    /**
     * ����ҽ��˳���
     * @param orderNo String
     * @throws HL7Exception
     */
    public void setOrderSeqNo(String orderSeqNo) throws HL7Exception {
        this.orderSeqNo = check(orderSeqNo);
    }

    /**
     * ȡ��ҽ��˳���
     * @return String
     */
    public String getOrderSeqNo() {
        return check(orderSeqNo);
    }

    /**
     * ����Ӧ��ע��
     * @param responseFlg String
     * @throws HL7Exception
     */
    public void setResponseFlg(String responseFlg) throws HL7Exception {
        this.responseFlg = check(responseFlg, new String[] {"E", "R", "D", "F",
                                 "N"});
    }

    /**
     * ȡ��Ӧ��ע��
     * @return String
     */
    public String getResponseFlg() {
        return check(responseFlg);
    }

    /**
     * ���ô�������
     * @param reserveDate String
     * @throws HL7Exception
     */
    public void setReserveDate(String reserveDate) throws HL7Exception {
        this.reserveDate = check(reserveDate, 26);
    }

    /**
     * ȡ�ô�������
     * @param reserveDate String
     * @return String
     */
    public String getReserveDate() {
        return check(reserveDate);
    }

    /**
     * ���ÿ���ҽʦ
     * @param orderDrCode String
     * @throws HL7Exception
     */
    public void setOrderDrCode(String orderDrCode) throws HL7Exception {
        this.orderDrCode = check(orderDrCode);
    }

    /**
     * ȡ�ÿ���ҽʦ
     * @return String
     */
    public String getOrderDrCode() {
        return check(orderDrCode);
    }

    /**
     * ���ÿ���ҽʦ
     * @param orderDrCode String
     * @throws HL7Exception
     */
    public void setOrderDrName(String orderDrName) throws HL7Exception {
        this.orderDrName = check(orderDrName);
    }

    /**
     * ȡ�ÿ���ҽʦ
     * @return String
     */
    public String getOrderDrName() {
        return check(orderDrName);
    }

    /**
     * ת���� String ����
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ORC");
        sb.append(getMSH().getFieldSeparator()); //1
        //ҽ������
        sb.append(getOrderControl());
        sb.append(getMSH().getFieldSeparator()); //2
        sb.append(getMSH().getFieldSeparator()); //3
        //�����߱��
        sb.append(getOrderNo());
    //    sb.append(getMSH().getColSeparator());
        sb.append(getOrderSeqNo());
        sb.append(getMSH().getFieldSeparator()); //4
        sb.append(getMSH().getFieldSeparator()); //5
        sb.append(getMSH().getFieldSeparator()); //6
        //Ӧ��ע��
        sb.append(getResponseFlg());
        sb.append(getMSH().getFieldSeparator()); //7
        sb.append(getMSH().getFieldSeparator()); //8
        sb.append(getMSH().getFieldSeparator()); //9
        //��������
        sb.append(getReserveDate());
        sb.append(getMSH().getFieldSeparator()); //10
        //����ҽʦ
        sb.append(getOrderDrCode());
        sb.append(getMSH().getColSeparator());
        sb.append(getOrderDrName());
        sb.append(getMSH().getFieldSeparator()); //11
        return sb.toString();
    }
}
