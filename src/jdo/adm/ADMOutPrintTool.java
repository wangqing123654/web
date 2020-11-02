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
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-18
 * @version 1.0
 */
public class ADMOutPrintTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMOutPrintTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMOutPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMOutPrintTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMOutPrintTool() {
        setModuleName("adm\\ADMOutPrintModule.x");
        onInit();
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     */
    public TParm selectOutHosp(TParm parm){
        TParm result = this.query("selectOutHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
