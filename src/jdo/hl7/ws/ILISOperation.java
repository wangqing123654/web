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
     * 拿到LIS回发文件全部内容
     * @param data
     * @param parm
     * @throws HL7Exception
     */
    public void mainLisData(String data,TParm parm) throws HL7Exception;
    /**
     * 签收
     * @return boolean
     */
    public TParm lisQS()throws HL7Exception;
    /**
     * 到检
     * @return boolean
     */
    public TParm lisDJ()throws HL7Exception;
    /**
     * 审核
     * @return boolean
     * @throws HL7Exception 
     */
    public TParm lisSHWC() throws HL7Exception;
    /**
     * 报告
     * @return boolean
     */
    public TParm lisBGWC()throws HL7Exception;
    /**
     * 回发内容
     * @return
     */
    public String getReturnValues();
    /**
     * 回发编码
     */
    public String getReturnCode();
    /**
     * 取有异常项消息内容
     * @return TParm
     */
    public TParm getAbnormalityParm();
}
