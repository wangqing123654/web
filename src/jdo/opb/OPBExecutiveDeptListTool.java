package jdo.opb;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ִ�п���ͳ��</p>
 *
 * <p>Description: ִ�п���ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-4-10
 * @version 1.0
 */
public class OPBExecutiveDeptListTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static OPBExecutiveDeptListTool instanceObject;
    /**
     * �õ�ʵ��
     * @return InvoiceTool
     */
    public static OPBExecutiveDeptListTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBExecutiveDeptListTool();
        return instanceObject;
    }

    public OPBExecutiveDeptListTool() {
        setModuleName("opb\\OPBExecutiveDeptListModule.x");
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
