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
   * �ֶηָ���
   */
  private String fieldSeparator;

  /**
   * �����ַ�
   */
  private String encodingCharacters;

  /**
   * ����Ӧ�ó���
   */
  private String sendingApplication;

  /**
   * ����Ӧ�ó���
   */
  private String receivingApplication;

  /**
   * ��Ϣʱ��
   */
  private String dateTimeOfMessage;

  /**
   * ��Ϣ����
   */
  private String messageType;

  /**
   * ��Ϣ����ID��
   */
  private String messageControlID;

  /**
   * ����ID��
   */
  private String processingID;

  /**
   * �汾ID��
   */
  private String versionID;

  /**
   * ����ȷ������
   */
  private String acceptAcknowledgmentType;

  /**
   * Ӧ�ó���ȷ������
   */
  private String applicationAcknowledgmentType;
  /**
   * ������
   */
  private String countryCode;
  /**
   * �ֶ��ڷָ���
   */
  private String colSeparator;
  /**
   * �ֶ��ڷָ���
   */
  private String colSeparator1;
  /**
   * ������
   * @throws HL7Exception
   */
  public MSH() throws HL7Exception {
    //�����ֶηָ��� "|"
    setFieldSeparator("|");
    //���ñ����ַ� "^~\&"
    setEncodingCharacters("^~\\&");
    //���ð汾 "2.4"
    setVersionID("2.4");
    //���ý���ȷ������ "NE"
    setAcceptAcknowledgmentType("NE");
    //����Ӧ�ó���ȷ������ "AL"
    setApplicationAcknowledgmentType("AL");
    //�����ֶ��ڷָ��� "^"
    setColSeparator("^");
    //�����ֶ��ڷָ��� "\"
    setColSeparator1("\\");
    //������Ϣ����"ORM^O01"
    setMessageType("ORM^O01");
    //���ù���
    setCountryCode("CHN");
    //����ProcessingID
    setProcessingID("P");
  }
  /**
   * �����ֶηָ���
   * @param fieldSeparator String
   * @throws HL7Exception
   */
  public void setFieldSeparator(String fieldSeparator) throws HL7Exception {
    this.fieldSeparator = check(fieldSeparator, 1);
  }


  /**
   * �õ��ֶηָ���
   * @return String
   */
  public String getFieldSeparator() {
    return check(fieldSeparator);
  }
  /**
   * ���ñ����ַ�
   * @param encodingCharacters String
   * @throws HL7Exception
   */
  public void setEncodingCharacters(String encodingCharacters)throws HL7Exception
  {
    this.encodingCharacters = check(encodingCharacters,4);
  }
  /**
   * �õ������ַ�
   * @return String
   */
  public String getEncodingCharacters()
  {
    return check(encodingCharacters);
  }
  /**
   * ���÷���Ӧ�ó���
   * @param sendingApplication String
   * @throws HL7Exception
   */
  public void setSendingApplication(String sendingApplication)throws HL7Exception
  {
    this.sendingApplication = check(sendingApplication,180);
  }
  /**
   * �õ�����Ӧ�ó���
   * @return String
   */
  public String getSendingApplication()
  {
    return check(sendingApplication);
  }
  /**
   * �õ�����Ӧ�ó���
   * @param receivingApplication String
   * @throws HL7Exception
   */
  public void setReceivingApplication(String receivingApplication)throws HL7Exception
  {
    this.receivingApplication = check(receivingApplication,180);
  }
  /**
   * ���ý���Ӧ�ó���
   * @return String
   */
  public String getReceivingApplication()
  {
    return check(receivingApplication);
  }
  /**
   * ������Ϣʱ��
   * @param dateTimeOfMessage String
   * @throws HL7Exception
   */
  public void setDateTimeOfMessage(String dateTimeOfMessage)throws HL7Exception
  {
    this.dateTimeOfMessage = check(dateTimeOfMessage,26);
  }
  /**
   * �õ���Ϣʱ��
   * @return String
   */
  public String getDateTimeOfMessage()
  {
    return check(dateTimeOfMessage);
  }
  /**
   * ������Ϣ����
   * @param messageType String
   * @throws HL7Exception
   */
  public void setMessageType(String messageType)throws HL7Exception
  {
    this.messageType = check(messageType,13);
  }
  /**
   * �õ���Ϣ����
   * @return String
   */
  public String getMessageType()
  {
    return check(messageType);
  }
  /**
   * ������Ϣ����ID��
   * @param messageControlID String
   * @throws HL7Exception
   */
  public void setMessageControlID(String messageControlID)throws HL7Exception
  {
    this.messageControlID = check(messageControlID,30);
  }
  /**
   * �õ���Ϣ����ID��
   * @return String
   */
  public String getMessageControlID()
  {
    return check(messageControlID);
  }
  /**
   * ���ô���ID��
   * D: ����
   * P: ��Ʒ
   * T: ѵ��
   * @param processingID String
   * @throws HL7Exception
   */
  public void setProcessingID(String processingID)throws HL7Exception
  {
    this.processingID = check(processingID,new String[]{"D","P","T"});
  }
  /**
   * �õ�����ID��
   * @return String
   * D: ����
   * P: ��Ʒ
   * T: ѵ��
   */
  public String getProcessingID()
  {
    return check(processingID);
  }
  /**
   * ���ð汾ID��
   * @param versionID String
   * @throws HL7Exception
   */
  public void setVersionID(String versionID)throws HL7Exception
  {
    this.versionID = check(versionID,new String[]{"2.4"});
  }
  /**
   * �õ��汾ID��
   * @return String
   */
  public String getVersionID()
  {
    return check(versionID);
  }
  /**
   * ���ý���ȷ������
   * AL: ����ȷ��
   * NE: �Ӳ�ȷ��
   * ER: ����/�Ǿܾ��Ľ�������
   * SU: �ɹ����
   * @param acceptAcknowledgmentType String
   * @throws HL7Exception
   */
  public void setAcceptAcknowledgmentType(String acceptAcknowledgmentType)throws HL7Exception
  {
    this.acceptAcknowledgmentType = check(acceptAcknowledgmentType,new String[]{"AL","NE","ER","SU"});
  }
  /**
   * �õ�����ȷ������
   * @return String
   * AL: ����ȷ��
   * NE: �Ӳ�ȷ��
   * ER: ����/�Ǿܾ��Ľ�������
   * SU: �ɹ����
   */
  public String getAcceptAcknowledgmentType()
  {
    return check(acceptAcknowledgmentType);
  }
  /**
   * ����Ӧ�ó���ȷ������
   * AL: ����ȷ��
   * NE: �Ӳ�ȷ��
   * ER: ����/�Ǿܾ��Ľ�������
   * SU: �ɹ����
   * @param applicationAcknowledgmentType String
   * @throws HL7Exception
   */
  public void setApplicationAcknowledgmentType(String applicationAcknowledgmentType)throws HL7Exception
  {
    this.applicationAcknowledgmentType = check(applicationAcknowledgmentType,new String[]{"AL","NE","ER","SU"});
  }
  /**
   * �õ�Ӧ�ó���ȷ������
   * @return String
   * AL: ����ȷ��
   * NE: �Ӳ�ȷ��
   * ER: ����/�Ǿܾ��Ľ�������
   * SU: �ɹ����
   */
  public String getApplicationAcknowledgmentType()
  {
    return check(applicationAcknowledgmentType);
  }
  /**
   * ���ù�����
   * @param countryCode String
   * @throws HL7Exception
   */
  public void setCountryCode(String countryCode)throws HL7Exception
  {
     this.countryCode = check(countryCode,new String[]{"CHN"});
  }
  /**
   * �õ�������
   * @return String
   */
  public String getCountryCode()
  {
     return check(countryCode);
  }
  /**
   * �����ֶ��ڷָ���
   * @param fieldSeparator String
   * @throws HL7Exception
   */
  public void setColSeparator(String colSeparator) throws HL7Exception {
    this.colSeparator = check(colSeparator, 1);
  }


  /**
   * �õ��ֶ��ڷָ���
   * @return String
   */
  public String getColSeparator() {
    return check(colSeparator);
  }
  /**
   * �����ֶ��ڷָ���
   * @param fieldSeparator String
   * @throws HL7Exception
   */
  public void setColSeparator1(String colSeparator1) throws HL7Exception {
    this.colSeparator1 = check(colSeparator1, 1);
  }


  /**
   * �õ��ֶ��ڷָ���
   * @return String
   */
  public String getColSeparator1() {
    return check(colSeparator1);
  }
  /**
   * ת���� String ����
   * @return String
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("MSH");
    sb.append(getFieldSeparator());//1
    //�����ַ�
    sb.append(getEncodingCharacters());
    sb.append(getFieldSeparator());//2
    //����Ӧ�ó���
    sb.append(getSendingApplication());
    sb.append(getFieldSeparator());//3
    sb.append(getFieldSeparator());//4
    //����Ӧ�ó���
    sb.append(getReceivingApplication());
    sb.append(getFieldSeparator());//5
    sb.append(getFieldSeparator());//6
    //��Ϣʱ��
    sb.append(getDateTimeOfMessage());
    sb.append(getFieldSeparator());//7
    sb.append(getFieldSeparator());//8
    //��Ϣ����
    sb.append(getMessageType());
    sb.append(getFieldSeparator());//9
    //��Ϣ����ID��
    sb.append(getMessageControlID());
    sb.append(getFieldSeparator());//10
    //����ID��
    sb.append(getProcessingID());
    sb.append(getFieldSeparator());//11
    //���ð汾ID��
    sb.append(getVersionID());
    sb.append(getFieldSeparator());//12
    sb.append(getFieldSeparator());//13
    sb.append(getFieldSeparator());//14
    //����ȷ������
    sb.append(getAcceptAcknowledgmentType());
    sb.append(getFieldSeparator());//15
    //Ӧ�ó���ȷ������
    sb.append(getApplicationAcknowledgmentType());
    sb.append(getFieldSeparator());//16
    //������
    sb.append(getCountryCode());
    sb.append(getFieldSeparator());//17
    return sb.toString();
  }

}
