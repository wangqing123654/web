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
public class MessageBase {
    /**
   * ȥ�� null �� " "
   * @param s String
   * @return String
   */
  public String check(String s) {
    if (s == null)
      return "";
    return s.trim();
  }

  /**
   * ����������
   * @param s String
   * @param length int
   * @throws HL7Exception
   * @return String
   */
  public String check(String s, int length) throws HL7Exception {
    s = check(s);
    if (s.length() > length)
      throw new HL7Exception("����" + s + "���� " + length);
    return s;
  }

  /**
   * ���ֵ�÷�Χ
   * @param s String
   * @param values String[] ��Ч��Χ
   * @throws HL7Exception
   * @return String
   */
  public String check(String s, String values[]) throws HL7Exception {
    s = check(s);
    String v = "";
    for (int i = 0; i < values.length; i++) {
      if (s.equals(values[i]))
        return s;
      if (v.length() > 0)
        v += ",";
      v += values[i];
    }
    throw new HL7Exception("����" + s + "����[" + v + "]��Χ��");
  }
}
