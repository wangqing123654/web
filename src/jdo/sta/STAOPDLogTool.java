package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �ż�����־</p>
 *
 * <p>Description: �ż�����־</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-7
 * @version 1.0
 */
public class STAOPDLogTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAOPDLogTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAOPDLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOPDLogTool();
        return instanceObject;
    }

    public STAOPDLogTool() {
        setModuleName("sta\\STAOPDLogModule.x");
        onInit();
    }
    /**
     * ��ѯָ�����ڵ�����
     * @param Date String
     * @return TParm
     * ===============pangben modify 20110525 ����������
     */
    public TParm selectData(String Date,String regionCode){
        TParm parm = new TParm();
        parm.setData("STA_DATE",Date);
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ȡ��ӡ����
     * @param STA_DATE String
     * @return TParm
     * ==============pangben modify 20110519 ����������
     */
    public TParm getPrintData(String STA_DATE,String regionCode){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //==============pangben modify 20110519 start
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        //==============pangben modify 20110519 stop
        TParm result = this.query("selectPrintData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * update_STA_OPD_DAILY�������м��������
     * @param parm TParm
     * @return TParm
     */
    public TParm update_STA_OPD_DAILY(TParm parm){
        TParm result = this.update("update_STA_OPD_DAILY",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
