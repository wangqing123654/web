package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��Ժͳ�Ʊ���</p>
 *
 * <p>Description: ��Ժͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-29
 * @version 4.0
 */
public class ADMInPrintTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMInPrintTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMInPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMInPrintTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMInPrintTool() {
        setModuleName("adm\\ADMInPrintModule.x");
        onInit();
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     */
    public TParm selectInHosp(TParm parm){
        TParm result = this.query("selectInHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
