package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �Ƴ�������Tool</p>
 *
 * <p>Description: �Ƴ�������Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-24
 * @version 4.0
 */
public class OPEDeptOpTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPEDeptOpTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static OPEDeptOpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEDeptOpTool();
        return instanceObject;
    }

    public OPEDeptOpTool() {
        this.setModuleName("ope\\OPEDeptOpModule.x");
        this.onInit();
    }
    /**
     * ��ѯ����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = query("selectdata",parm);
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
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��������
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(String DEPT_CODE,String OP_CODE){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",DEPT_CODE);
        parm.setData("OP_CODE",OP_CODE);
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

}
