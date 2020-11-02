package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 * ���õ�
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author fudw
 * @version 1.0
 */
public class BILRecpParmTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILRecpParmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return CTZTool
     */
    public static BILRecpParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILRecpParmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILRecpParmTool() {
        setModuleName("bil\\BILRecpParmModule.x");
        onInit();
    }

    /**
     * ��ѯȫ���շ����
     * @return TParm charge_code
     */
    public TParm selectcharge(TParm parm) {
        TParm result = query("selectChargeCode",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
