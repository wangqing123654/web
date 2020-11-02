package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:�ҺŲ��������� </p>
 *
 * <p>Description:�ҺŲ��������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class SysParmTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static SysParmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SysParmTool
     */
    public static SysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SysParmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SysParmTool() {
        setModuleName("reg\\REGSysParmModule.x");

        onInit();
    }
    /**
     * ��ѯ�ҺŲ���
     * @param
     * @return TParm
     */
    public TParm selectdata() {
        TParm result = query("selectdata");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¹ҺŲ���
     * @param  String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
