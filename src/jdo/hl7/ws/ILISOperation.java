package jdo.hl7.ws;
import com.dongyang.data.TParm;
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
public interface ILISOperation {
    /**
     * �õ�LIS�ط��ļ�ȫ������
     * @param data
     * @param parm
     * @throws HL7Exception
     */
    public void mainLisData(String data,TParm parm) throws HL7Exception;
    /**
     * ǩ��
     * @return boolean
     */
    public TParm lisQS()throws HL7Exception;
    /**
     * ����
     * @return boolean
     */
    public TParm lisDJ()throws HL7Exception;
    /**
     * ���
     * @return boolean
     * @throws HL7Exception 
     */
    public TParm lisSHWC() throws HL7Exception;
    /**
     * ����
     * @return boolean
     */
    public TParm lisBGWC()throws HL7Exception;
    /**
     * �ط�����
     * @return
     */
    public String getReturnValues();
    /**
     * �ط�����
     */
    public String getReturnCode();
    /**
     * ȡ���쳣����Ϣ����
     * @return TParm
     */
    public TParm getAbnormalityParm();
}
