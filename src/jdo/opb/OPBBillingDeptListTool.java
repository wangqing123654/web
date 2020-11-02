package jdo.opb;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��������ͳ��</p>
 *
 * <p>Description: ��������ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS</p>
 *
 * @author zhangk 2010-4-10
 * @version 1.0
 */
public class OPBBillingDeptListTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPBBillingDeptListTool instanceObject;
    /**
     * �õ�ʵ��
     * @return InvoiceTool
     */
    public static OPBBillingDeptListTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBBillingDeptListTool();
        return instanceObject;
    }

    public OPBBillingDeptListTool() {
        setModuleName("opb\\OPBBillingDeptListModule.x");
        onInit();
    }
    /**
     * ��ѯ�������ҵ�ͳ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDetial(TParm parm){
        TParm result = this.query("selectDetial",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
