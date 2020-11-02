package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽԺ�š����﹤��ͳ��̨��</p>
 *
 * <p>Description: ҽԺ�š����﹤��ͳ��̨��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-1
 * @version 1.0
 */
public class STAIn_02TTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAIn_02TTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAIn_02TTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_02TTool();
        return instanceObject;
    }

    public STAIn_02TTool() {
        setModuleName("sta\\STAIn_02TModule.x");
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
