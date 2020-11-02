package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:��������� </p>
 *
 * <p>Description:��������� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 20090724
 * @version 1.0
 */
public class BILSysParmTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILSysParmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILSysParmTool
     */
    public static BILSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILSysParmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILSysParmTool() {
        setModuleName("bil\\BILSysParmModule.x");
        onInit();
    }

    /**
     * �õ������ս�ʱ���
     * @param admType String
     * @return TParm
     */
    public TParm getDayCycle(String admType) {
        TParm result = new TParm();
        if (admType == null || admType.length() == 0)
            return result.newErrParm( -1, "��������");
        result.setData("ADM_TYPE", admType);
        result = query("getDayCycle", result);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }


}
