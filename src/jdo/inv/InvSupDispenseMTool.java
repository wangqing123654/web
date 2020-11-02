package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

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
public class InvSupDispenseMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvSupDispenseMTool instanceObject;

    /**
     * ������
     */
    public InvSupDispenseMTool() {
        setModuleName("inv\\INVSupDispenseMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvSupDispenseMTool
     */
    public static InvSupDispenseMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupDispenseMTool();
        return instanceObject;
    }

    /**
     * ��ѯ���뵥����
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������뵥����
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * ��ѯĳ�����ư���ȫ�����Σ���������0��
     * @param parm TParm
     * @return TParm
     */
    public TParm queryPackByBatch(TParm parm){
        TParm result = this.query("queryPackByBatch", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
