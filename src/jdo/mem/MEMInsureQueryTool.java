package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ���ջ�����Ϣ��ѯ������
 * </p>
 * @author liling 20140713
 * 
 *
 */
public class MEMInsureQueryTool extends TJDOTool{
	 /**
     * ʵ��
     */
    private static MEMInsureQueryTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelGroupTool
     */
    public static MEMInsureQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMInsureQueryTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public MEMInsureQueryTool() {
        setModuleName("mem\\MEMInsureQueryModule.x");
        onInit();
    }
    /**
     * ��ѯ���ջ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���±��ջ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
