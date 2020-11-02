package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TDebug;
/**
 *
 * <p>Title:����ά�������� </p>
 *
 * <p>Description:����ά�������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class REGClinicAreaTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static REGClinicAreaTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGClinicAreaTool
     */
    public static REGClinicAreaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGClinicAreaTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public REGClinicAreaTool() {
        setModuleName("reg\\REGClinicAreaModule.x");
        onInit();
    }

    /**
     * ��ѯ����
     * @return TParm
     */
    public TParm queryTree() {
        TParm result = query("queryTree");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * ��ѯ����table��Ϣ
     * @return TParm
     */
    public TParm selectdata() {
        TParm parm = new TParm();
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public static void main(String args[]) {
        TDebug.initServer();
       // System.out.println(REGClinicAreaTool.getInstance().queryTree());

    }
}
