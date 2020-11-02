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
     * MSH 对象
     */
    private MSH msh;
    /**
     * 医嘱控制
     */
    private String orderControl;
    /**
     * 医嘱序号
     */
    private String orderNo;
    /**
     * 医嘱顺序号
     */
    private String orderSeqNo;
    /**
     * 应答注记
     */
    private String responseFlg;
    /**
     * 传输日期
     */
    private String reserveDate;
    /**
     * 开立医师代码
     */
    private String orderDrCode;
    /**
     * 开立医师中文名
     */
    private String orderDrName;

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
     * 设置医嘱控制
     * @param orderControl String
     * @throws HL7Exception
     * NW：新增 CA：取消
     */
    public void setOrderControl(String orderControl) throws HL7Exception {
        this.orderControl = check(orderControl, new String[] {"NW", "CA", "CH"});
    }

    /**
     * 取得医嘱控制
     * @return String
     */
    public String getOrderControl() {
        return check(orderControl);
    }

    /**
     * 设置医嘱序号
     * @param orderNo String
     * @throws HL7Exception
     */
    public void setOrderNo(String orderNo) throws HL7Exception {
        this.orderNo = check(orderNo);
    }

    /**
     * 取得医嘱序号
     * @return String
     */
    public String getOrderNo() {
        return check(orderNo);
    }

    /**
     * 设置医嘱顺序号
     * @param orderNo String
     * @throws HL7Exception
     */
    public void setOrderSeqNo(String orderSeqNo) throws HL7Exception {
        this.orderSeqNo = check(orderSeqNo);
    }

    /**
     * 取得医嘱顺序号
     * @return String
     */
    public String getOrderSeqNo() {
        return check(orderSeqNo);
    }

    /**
     * 设置应答注记
     * @param responseFlg String
     * @throws HL7Exception
     */
    public void setResponseFlg(String responseFlg) throws HL7Exception {
        this.responseFlg = check(responseFlg, new String[] {"E", "R", "D", "F",
                                 "N"});
    }

    /**
     * 取得应答注记
     * @return String
     */
    public String getResponseFlg() {
        return check(responseFlg);
    }

    /**
     * 设置传输日期
     * @param reserveDate String
     * @throws HL7Exception
     */
    public void setReserveDate(String reserveDate) throws HL7Exception {
        this.reserveDate = check(reserveDate, 26);
    }

    /**
     * 取得传输日期
     * @param reserveDate String
     * @return String
     */
    public String getReserveDate() {
        return check(reserveDate);
    }

    /**
     * 设置开立医师
     * @param orderDrCode String
     * @throws HL7Exception
     */
    public void setOrderDrCode(String orderDrCode) throws HL7Exception {
        this.orderDrCode = check(orderDrCode);
    }

    /**
     * 取得开立医师
     * @return String
     */
    public String getOrderDrCode() {
        return check(orderDrCode);
    }

    /**
     * 设置开立医师
     * @param orderDrCode String
     * @throws HL7Exception
     */
    public void setOrderDrName(String orderDrName) throws HL7Exception {
        this.orderDrName = check(orderDrName);
    }

    /**
     * 取得开立医师
     * @return String
     */
    public String getOrderDrName() {
        return check(orderDrName);
    }

    /**
     * 转换成 String 类型
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ORC");
        sb.append(getMSH().getFieldSeparator()); //1
        //医嘱控制
        sb.append(getOrderControl());
        sb.append(getMSH().getFieldSeparator()); //2
        sb.append(getMSH().getFieldSeparator()); //3
        //申请者编号
        sb.append(getOrderNo());
    //    sb.append(getMSH().getColSeparator());
        sb.append(getOrderSeqNo());
        sb.append(getMSH().getFieldSeparator()); //4
        sb.append(getMSH().getFieldSeparator()); //5
        sb.append(getMSH().getFieldSeparator()); //6
        //应答注记
        sb.append(getResponseFlg());
        sb.append(getMSH().getFieldSeparator()); //7
        sb.append(getMSH().getFieldSeparator()); //8
        sb.append(getMSH().getFieldSeparator()); //9
        //传输日期
        sb.append(getReserveDate());
        sb.append(getMSH().getFieldSeparator()); //10
        //开立医师
        sb.append(getOrderDrCode());
        sb.append(getMSH().getColSeparator());
        sb.append(getOrderDrName());
        sb.append(getMSH().getFieldSeparator()); //11
        return sb.toString();
    }
}
