package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: סԺҽʦͳ�Ʊ���</p>
 *
 * <p>Description: סԺҽʦͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-19
 * @version 1.0
 */
public class ADMDrCountPrintTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMDrCountPrintTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMDrCountPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMDrCountPrintTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMDrCountPrintTool() {
        setModuleName("adm\\ADMDrCountPrintModule.x");
        onInit();
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     */
    public TParm selectDrCount(TParm parm){
        TParm result = this.query("selectDrCount", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
