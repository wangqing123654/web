package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: STA_IN_06Σ��������Ч����̨��</p>
 *
 * <p>Description: STA_IN_06Σ��������Ч����̨��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-2
 * @version 1.0
 */
public class STAIn_06TTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAIn_06TTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAIn_06TTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_06TTool();
        return instanceObject;
    }

    public STAIn_06TTool() {
        setModuleName("sta\\STAIn_06TModule.x");
        onInit();
    }
    /**
     * ��ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBQ(TParm parm){
        TParm result = this.query("selectBQ",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectQS(TParm parm){
        TParm result = this.query("selectQS",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
