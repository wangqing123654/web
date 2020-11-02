package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ���տ�����Ϣ</p>
 *
 * <p>Description: ���տ�����Ϣ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-7-26
 * @version 1.0
 */
public class STADeptInfoTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STADeptInfoTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STADeptInfoTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new STADeptInfoTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public STADeptInfoTool() {
        setModuleName("sta\\STADeptInfoModule.x");
        onInit();
    }
    /**
     * ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        result = update("updatedata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ����Ϣ
     * @param regMethod String
     * @return boolean
     */
    public TParm deletedata(String dept_code){
        TParm parm = new TParm();
        parm.setData("STA_DEPT_CODE",dept_code);
        TParm result = update("deletedata",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm){
        TParm result = new TParm();
        result = query("selectdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
