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
public interface IRISOperation {
    /**
     * �õ�RIS�ط��ļ�ȫ������
     * @param data
     * @param parm
     * @throws HL7Exception
     */
    public void mainLisData(String data,TParm parm) throws HL7Exception;
    /**
     * ԤԼ
     * @return boolean
     * @throws HL7Exception 
     */
    public TParm risYY() throws HL7Exception;
    /**
     * ȡ��ԤԼ
     * @return boolean
     */
    public TParm risCanYY()throws HL7Exception ;
    /**
     * ����
     * @return boolean
     */
    public TParm risDJ()throws HL7Exception ;
    /**
     * ���
     * @return boolean
     */
    public TParm risSHWC()throws HL7Exception ;
    /**
     * ����
     * @return boolean
     */
    public TParm risBGWC()throws HL7Exception ;
    /**
     * �ط��ļ�����
     * @return
     */
    public String getBytReturnData();
    /**
     * �ط�����    
     */
    public String getReturnCode();

}
