package jdo.spc;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��Ч�ڼ��������ʾ</p>
 *
 * <p>Description: ��Ч�ڼ��������ʾ</p>
 *  
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.10.29
 * @version 1.0
 */
public class IndValidAndQtyWarnTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndValidAndQtyWarnTool instanceObject;

    /** 
     * �õ�ʵ��
     *
     * @return IndValidAndQtyWarnTool
     */
    public static IndValidAndQtyWarnTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndValidAndQtyWarnTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IndValidAndQtyWarnTool() {  
        setModuleName("spc\\INDValidAndQtyWarnModule.x");
        onInit();
    }

    /**
     * ��Ч�ڲ�ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryValid(TParm parm) {
        TParm result = this.query("queryValid", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQueryQty(TParm parm) {
        TParm result = this.query("queryQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
