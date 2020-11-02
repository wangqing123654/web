package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �ż�ҽʦ������Ժͳ�Ʊ���</p>
 *
 * <p>Description: �ż�ҽʦ������Ժͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-19
 * @version 1.0
 */
public class ADMOpdDrPrintTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMOpdDrPrintTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMOpdDrPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMOpdDrPrintTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMOpdDrPrintTool() {
        setModuleName("adm\\ADMOpdDrPrintModule.x");
        onInit();
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     */
    public TParm selectOpdDrCount(TParm parm){
        TParm result = this.query("selectOpdDrCount", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
