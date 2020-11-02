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
     * 拿到RIS回发文件全部内容
     * @param data
     * @param parm
     * @throws HL7Exception
     */
    public void mainLisData(String data,TParm parm) throws HL7Exception;
    /**
     * 预约
     * @return boolean
     * @throws HL7Exception 
     */
    public TParm risYY() throws HL7Exception;
    /**
     * 取消预约
     * @return boolean
     */
    public TParm risCanYY()throws HL7Exception ;
    /**
     * 到检
     * @return boolean
     */
    public TParm risDJ()throws HL7Exception ;
    /**
     * 审核
     * @return boolean
     */
    public TParm risSHWC()throws HL7Exception ;
    /**
     * 报告
     * @return boolean
     */
    public TParm risBGWC()throws HL7Exception ;
    /**
     * 回发文件内容
     * @return
     */
    public String getBytReturnData();
    /**
     * 回发编码    
     */
    public String getReturnCode();

}
