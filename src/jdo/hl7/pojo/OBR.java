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
public class OBR extends MessageBase {
    /**
     * MSH 对象
     */
    private MSH msh;
    /**
     * 申请者编号
     */
    private String number;
    /**
     *
     */
    private String orderCode;
    /**
     *
     */
    private String orderDesc;
    /**
     *
     */
    private String subSystemCode;
    /**
     *
     */
    private String subSystemDesc;
    /**
     *
     */
    private String rptTypeCode;
    /**
     *
     */
    private String rptTypeDesc;
    /**
     *
     */
    private String partCode;
    /**
     *
     */
    private String partDesc;
    /**
     *
     */
    private String deviceTypeCode;
    /**
     *
     */
    private String deviceTypeDesc;
    /**
     * XML数据
     */
    private String xmlData;
    /**
     * SeqNo
     */
    private String seq;
    /**
     * 条码号
     */
    private String labGridNumber;
    /**
     * 状态
     */
    private String statusType;
    /**
     * 病案号
     */
    private String OrderNo;
    /**
     * 集合医嘱细项
     */
    private String[] orderCodeArray;

    private String totAmt;

//  private String
    /**
     * 设置 MSH 对象
     * @param msh MSH
     */
    public void setMSH(MSH msh) {
        this.msh = msh;
    }

    /**
     * 得到 MSH 对象
     * @return MSH
     */
    public MSH getMSH() {
        return msh;
    }

    /**
     * 设置申请者编号
     * @param number String
     */
    public void setPalcerOrderNumber(String number) throws HL7Exception {
        this.number = check(number, 22);
    }

    /**
     * 取得申请者编号
     * @return String
     */
    public String getPalcerOrderNumber() {
        return number;
    }

    public void setOrderCode(String orderCode) throws HL7Exception {
        this.orderCode = check(orderCode);
    }

    public String getOrderCode() {
        return check(orderCode);

    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = check(orderDesc);
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setSubSystemCode(String subSystemCode) {
        this.subSystemCode = check(subSystemCode);
    }

    public String getSubSystemCode() {
        return subSystemCode;
    }

    public void setSubSystemDesc(String subSystemDesc) {
        this.subSystemDesc = this.check(subSystemDesc);
    }

    public String getSubSystemDesc() {
        return this.check(subSystemDesc);
    }

    public void setRptTypeCode(String rptTypeCode) {
        this.rptTypeCode = this.check(rptTypeCode);
    }

    public String getRptTypeCode() {
        return this.check(rptTypeCode);
    }

    public void setRptTypeDesc(String rptTypeDesc) {
        this.rptTypeDesc = this.check(rptTypeDesc);
    }

    public String getRptTypeDesc() {
        return this.check(rptTypeDesc);
    }

    public void setPartCode(String partCode) {
        this.partCode = this.check(partCode);
    }

    public String getPartCode() {
        return this.check(partCode);
    }

    public void setPartDesc(String partDesc) {
        this.partDesc = this.check(partDesc);
    }

    public String getPartDesc() {
        return this.check(partDesc);
    }

    public void setDeviceTypeCode(String deviceTypeCode) {
        this.deviceTypeCode = this.check(deviceTypeCode);
    }

    public String getDeviceTypeCode() {
        return this.check(deviceTypeCode);
    }

    public void setDeviceTypeDesc(String deviceTypeDesc) {
        this.deviceTypeDesc = this.check(deviceTypeDesc);
    }

    public String getDeviceTypeDesc() {
        return this.check(deviceTypeDesc);
    }

    public void setXmlData(String xmlData) {
        this.xmlData = this.check(xmlData);
    }

    public String getXmlData() {
        return this.check(xmlData);
    }

//  public void  set
    /**
     * 转换成 String 类型
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("OBR");
        sb.append(getMSH().getFieldSeparator()); //1
        sb.append(this.getSeq());
        sb.append(getMSH().getFieldSeparator()); //2
        //申请者编号
        sb.append(this.getPalcerOrderNumber());
        sb.append(getMSH().getFieldSeparator()); //3
        sb.append(getMSH().getFieldSeparator()); //4
        //请求服务的编号
        sb.append(getOrderCode());
        sb.append(getMSH().getColSeparator1());
        sb.append(getOrderDesc());
        sb.append(getMSH().getColSeparator1());
        sb.append(getSubSystemCode());
        sb.append(getMSH().getColSeparator1());
        sb.append(getSubSystemDesc());
        sb.append(getMSH().getColSeparator1());
        sb.append(getRptTypeCode());
        sb.append(getMSH().getColSeparator1());
        sb.append(getRptTypeDesc());
        sb.append(getMSH().getColSeparator1());
        sb.append(getDeviceTypeCode());
        sb.append(getMSH().getColSeparator1());
        sb.append(getDeviceTypeDesc());
        sb.append(getMSH().getColSeparator());
        sb.append(getPartCode());
        sb.append(getMSH().getColSeparator());
        sb.append(getPartDesc());
        //集合医嘱细项
        sb.append(this.createOrder());
        sb.append(getMSH().getFieldSeparator()); //5
        sb.append(getMSH().getFieldSeparator()); //6
        sb.append(getMSH().getFieldSeparator()); //7
        sb.append(getMSH().getFieldSeparator()); //8
        sb.append(getMSH().getFieldSeparator()); //9
        sb.append(getMSH().getFieldSeparator()); //10
        sb.append(getMSH().getFieldSeparator()); //11
        sb.append(getMSH().getFieldSeparator()); //12
        sb.append(getMSH().getFieldSeparator()); //13
        //xml
        sb.append(getXmlData());
        sb.append(getMSH().getFieldSeparator()); //14
        sb.append(getMSH().getFieldSeparator()); //15
        sb.append(getMSH().getFieldSeparator()); //16
        sb.append(getMSH().getFieldSeparator()); //17
        sb.append(getMSH().getFieldSeparator()); //18
        sb.append(this.getLabGridNumber());
        sb.append(getMSH().getFieldSeparator()); //19
        sb.append(this.getStatusType());
        sb.append(getMSH().getFieldSeparator()); //20
        sb.append(this.getTotAmt());
        sb.append(getMSH().getFieldSeparator()); //21
        return sb.toString();
    }

    public String getSeq() {
        return this.check(this.seq);
    }

    public void setSeq(String seq) {
        this.seq = this.check(seq);
    }

    public String getStatusType() {
        return this.check(this.statusType);
    }

    public void setStatusType(String statusType) {
        this.statusType = this.check(statusType);
    }

    public String getLabGridNumber() {
        return this.check(this.labGridNumber);
    }

    public void setLabGridNumber(String labGridNumber) {
        this.labGridNumber = this.check(labGridNumber);
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String[] getOrderCodeArray() {
        return this.orderCodeArray;
    }

    public String getTotAmt() {
        return totAmt;
    }

    /**
     * 接收集合医嘱细项
     * @param orderCodeArray String[]
     * @author WangM  2007-8-22
     */
    public void setOrderCodeArray(String[] orderCodeArray) {
        if (orderCodeArray == null) {
            return;
        }
        String[] orderCode = new String[orderCodeArray.length];
        for (int i = 0; i < orderCodeArray.length; i++) {
            orderCode[i] = orderCodeArray[i] + this.getMSH().getColSeparator1();
            if (i == orderCodeArray.length - 1) {
                orderCode[i] = orderCodeArray[i];
            }
        }
        this.orderCodeArray = orderCode;
    }

    public void setTotAmt(String totAmt) {
        this.totAmt = totAmt;
    }
    /**
     * OBR字段组合集合医嘱细项
     * @return String
     * @author WangM  2007-8-22
     */
    public String createOrder() {
        String[] order = this.getOrderCodeArray();
        if (order == null) {
            return "";
        }
        String str = "";
        for (int i = 0; i < order.length; i++) {
            str += order[i];
        }
        return str;
    }
}
