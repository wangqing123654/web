package jdo.odo;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OPDAbnormalRegTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPDAbnormalRegTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static OPDAbnormalRegTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPDAbnormalRegTool();
        return instanceObject;
    }

    public OPDAbnormalRegTool() {
        this.setModuleName("opd\\OPDAbnormalRegModule.x");
        this.onInit();
    }
    /**
     * �����Һ���Ϣ(�ǳ�̬����)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveReg(TParm parm,TConnection conn){
        TParm result = this.update("saveReg",parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�ǳ�̬����ҺŵĲ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectRegForOPD(TParm parm){
        TParm result = this.query("selectRegForOPD",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
