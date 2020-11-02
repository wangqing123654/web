package jdo.hl7.pojo;
import java.io.Serializable;

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
 * @version JAVAHIS 1.0
 */
public class MSH extends MessageBase  implements Serializable{
    /**
   * 字段分隔符
   */
  private String fieldSeparator;

  /**
   * 编码字符
   */
  private String encodingCharacters;

  /**
   * 发送应用程序
   */
  private String sendingApplication;

  /**
   * 接收应用程序
   */
  private String receivingApplication;

  /**
   * 消息时间
   */
  private String dateTimeOfMessage;

  /**
   * 信息类型
   */
  private String messageType;

  /**
   * 信息控制ID号
   */
  private String messageControlID;

  /**
   * 处理ID号
   */
  private String processingID;

  /**
   * 版本ID号
   */
  private String versionID;

  /**
   * 接受确认类型
   */
  private String acceptAcknowledgmentType;

  /**
   * 应用程序确认类型
   */
  private String applicationAcknowledgmentType;
  /**
   * 国别简称
   */
  private String countryCode;
  /**
   * 字段内分隔符
   */
  private String colSeparator;
  /**
   * 字段内分隔符
   */
  private String colSeparator1;
  /**
   * 构造器
   * @throws HL7Exception
   */
  public MSH() throws HL7Exception {
    //设置字段分隔符 "|"
    setFieldSeparator("|");
    //设置编码字符 "^~\&"
    setEncodingCharacters("^~\\&");
    //设置版本 "2.4"
    setVersionID("2.4");
    //设置接受确认类型 "NE"
    setAcceptAcknowledgmentType("NE");
    //设置应用程序确认类型 "AL"
    setApplicationAcknowledgmentType("AL");
    //设置字段内分隔符 "^"
    setColSeparator("^");
    //设置字段内分隔符 "\"
    setColSeparator1("\\");
    //设置信息类型"ORM^O01"
    setMessageType("ORM^O01");
    //设置国别
    setCountryCode("CHN");
    //设置ProcessingID
    setProcessingID("P");
  }
  /**
   * 设置字段分隔符
   * @param fieldSeparator String
   * @throws HL7Exception
   */
  public void setFieldSeparator(String fieldSeparator) throws HL7Exception {
    this.fieldSeparator = check(fieldSeparator, 1);
  }


  /**
   * 得到字段分隔符
   * @return String
   */
  public String getFieldSeparator() {
    return check(fieldSeparator);
  }
  /**
   * 设置编码字符
   * @param encodingCharacters String
   * @throws HL7Exception
   */
  public void setEncodingCharacters(String encodingCharacters)throws HL7Exception
  {
    this.encodingCharacters = check(encodingCharacters,4);
  }
  /**
   * 得到编码字符
   * @return String
   */
  public String getEncodingCharacters()
  {
    return check(encodingCharacters);
  }
  /**
   * 设置发送应用程序
   * @param sendingApplication String
   * @throws HL7Exception
   */
  public void setSendingApplication(String sendingApplication)throws HL7Exception
  {
    this.sendingApplication = check(sendingApplication,180);
  }
  /**
   * 得到发送应用程序
   * @return String
   */
  public String getSendingApplication()
  {
    return check(sendingApplication);
  }
  /**
   * 得到接收应用程序
   * @param receivingApplication String
   * @throws HL7Exception
   */
  public void setReceivingApplication(String receivingApplication)throws HL7Exception
  {
    this.receivingApplication = check(receivingApplication,180);
  }
  /**
   * 设置接收应用程序
   * @return String
   */
  public String getReceivingApplication()
  {
    return check(receivingApplication);
  }
  /**
   * 设置消息时间
   * @param dateTimeOfMessage String
   * @throws HL7Exception
   */
  public void setDateTimeOfMessage(String dateTimeOfMessage)throws HL7Exception
  {
    this.dateTimeOfMessage = check(dateTimeOfMessage,26);
  }
  /**
   * 得到消息时间
   * @return String
   */
  public String getDateTimeOfMessage()
  {
    return check(dateTimeOfMessage);
  }
  /**
   * 设置信息类型
   * @param messageType String
   * @throws HL7Exception
   */
  public void setMessageType(String messageType)throws HL7Exception
  {
    this.messageType = check(messageType,13);
  }
  /**
   * 得到信息类型
   * @return String
   */
  public String getMessageType()
  {
    return check(messageType);
  }
  /**
   * 设置信息控制ID号
   * @param messageControlID String
   * @throws HL7Exception
   */
  public void setMessageControlID(String messageControlID)throws HL7Exception
  {
    this.messageControlID = check(messageControlID,30);
  }
  /**
   * 得到信息控制ID号
   * @return String
   */
  public String getMessageControlID()
  {
    return check(messageControlID);
  }
  /**
   * 设置处理ID号
   * D: 调试
   * P: 产品
   * T: 训练
   * @param processingID String
   * @throws HL7Exception
   */
  public void setProcessingID(String processingID)throws HL7Exception
  {
    this.processingID = check(processingID,new String[]{"D","P","T"});
  }
  /**
   * 得到处理ID号
   * @return String
   * D: 调试
   * P: 产品
   * T: 训练
   */
  public String getProcessingID()
  {
    return check(processingID);
  }
  /**
   * 设置版本ID号
   * @param versionID String
   * @throws HL7Exception
   */
  public void setVersionID(String versionID)throws HL7Exception
  {
    this.versionID = check(versionID,new String[]{"2.4"});
  }
  /**
   * 得到版本ID号
   * @return String
   */
  public String getVersionID()
  {
    return check(versionID);
  }
  /**
   * 设置接受确认类型
   * AL: 总是确认
   * NE: 从不确认
   * ER: 错误/是拒绝的仅有条件
   * SU: 成功完成
   * @param acceptAcknowledgmentType String
   * @throws HL7Exception
   */
  public void setAcceptAcknowledgmentType(String acceptAcknowledgmentType)throws HL7Exception
  {
    this.acceptAcknowledgmentType = check(acceptAcknowledgmentType,new String[]{"AL","NE","ER","SU"});
  }
  /**
   * 得到接受确认类型
   * @return String
   * AL: 总是确认
   * NE: 从不确认
   * ER: 错误/是拒绝的仅有条件
   * SU: 成功完成
   */
  public String getAcceptAcknowledgmentType()
  {
    return check(acceptAcknowledgmentType);
  }
  /**
   * 设置应用程序确认类型
   * AL: 总是确认
   * NE: 从不确认
   * ER: 错误/是拒绝的仅有条件
   * SU: 成功完成
   * @param applicationAcknowledgmentType String
   * @throws HL7Exception
   */
  public void setApplicationAcknowledgmentType(String applicationAcknowledgmentType)throws HL7Exception
  {
    this.applicationAcknowledgmentType = check(applicationAcknowledgmentType,new String[]{"AL","NE","ER","SU"});
  }
  /**
   * 得到应用程序确认类型
   * @return String
   * AL: 总是确认
   * NE: 从不确认
   * ER: 错误/是拒绝的仅有条件
   * SU: 成功完成
   */
  public String getApplicationAcknowledgmentType()
  {
    return check(applicationAcknowledgmentType);
  }
  /**
   * 设置国别简称
   * @param countryCode String
   * @throws HL7Exception
   */
  public void setCountryCode(String countryCode)throws HL7Exception
  {
     this.countryCode = check(countryCode,new String[]{"CHN"});
  }
  /**
   * 得到国别简称
   * @return String
   */
  public String getCountryCode()
  {
     return check(countryCode);
  }
  /**
   * 设置字段内分隔符
   * @param fieldSeparator String
   * @throws HL7Exception
   */
  public void setColSeparator(String colSeparator) throws HL7Exception {
    this.colSeparator = check(colSeparator, 1);
  }


  /**
   * 得到字段内分隔符
   * @return String
   */
  public String getColSeparator() {
    return check(colSeparator);
  }
  /**
   * 设置字段内分隔符
   * @param fieldSeparator String
   * @throws HL7Exception
   */
  public void setColSeparator1(String colSeparator1) throws HL7Exception {
    this.colSeparator1 = check(colSeparator1, 1);
  }


  /**
   * 得到字段内分隔符
   * @return String
   */
  public String getColSeparator1() {
    return check(colSeparator1);
  }
  /**
   * 转换成 String 类型
   * @return String
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("MSH");
    sb.append(getFieldSeparator());//1
    //编码字符
    sb.append(getEncodingCharacters());
    sb.append(getFieldSeparator());//2
    //发送应用程序
    sb.append(getSendingApplication());
    sb.append(getFieldSeparator());//3
    sb.append(getFieldSeparator());//4
    //接收应用程序
    sb.append(getReceivingApplication());
    sb.append(getFieldSeparator());//5
    sb.append(getFieldSeparator());//6
    //消息时间
    sb.append(getDateTimeOfMessage());
    sb.append(getFieldSeparator());//7
    sb.append(getFieldSeparator());//8
    //信息类型
    sb.append(getMessageType());
    sb.append(getFieldSeparator());//9
    //信息控制ID号
    sb.append(getMessageControlID());
    sb.append(getFieldSeparator());//10
    //处理ID号
    sb.append(getProcessingID());
    sb.append(getFieldSeparator());//11
    //设置版本ID号
    sb.append(getVersionID());
    sb.append(getFieldSeparator());//12
    sb.append(getFieldSeparator());//13
    sb.append(getFieldSeparator());//14
    //接受确认类型
    sb.append(getAcceptAcknowledgmentType());
    sb.append(getFieldSeparator());//15
    //应用程序确认类型
    sb.append(getApplicationAcknowledgmentType());
    sb.append(getFieldSeparator());//16
    //国别简称
    sb.append(getCountryCode());
    sb.append(getFieldSeparator());//17
    return sb.toString();
  }

}
