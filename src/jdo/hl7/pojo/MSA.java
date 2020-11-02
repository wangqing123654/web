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
public class MSA extends MessageBase{
    /**
     * MSH 对象
     */
    private MSH msh;
    public MSA(){
        try {
            msh = new MSH();
        } catch (HL7Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 相应结果
     * CA=成功
     * CE=失败
     * CR=拒绝
     */
    private String acknowledgmentCode;
    /**
     * 条码号
     */
    private String labNo;
    /**
     * 状态
     */
    private String messageStater;

    public MSH getMsh() {
        return msh;
    }

    public String getLabNo() {
        return labNo;
    }

    public String getAcknowledgmentCode() {
        return acknowledgmentCode;
    }

    public String getMessageStater() {
        return messageStater;
    }

    public void setMsh(MSH msh) {
        this.msh = msh;
    }

    public void setAcknowledgmentCode(String acknowledgmentCode) {
        this.acknowledgmentCode = acknowledgmentCode;
    }

    public void setLabNo(String labNo) {
        this.labNo = labNo;
    }

    public void setMessageStater(String messageStater) {
        this.messageStater = messageStater;
    }

    /**
    * 转换成 String 类型
    * @return String
    */
   public String toString() {
       StringBuffer sb = new StringBuffer();
       sb.append("MSA");
       sb.append(this.getMsh().getFieldSeparator());//0
       sb.append(this.getAcknowledgmentCode());
       sb.append(this.getMsh().getFieldSeparator());//1
       sb.append(this.getLabNo());
       sb.append(this.getMsh().getFieldSeparator());//2
       sb.append(this.getMessageStater());
       sb.append(this.getMsh().getFieldSeparator());//3
       return sb.toString();
   }
}
