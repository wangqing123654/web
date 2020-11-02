package jdo.inv;

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
 * @author zhangh 2013.7.12
 * @version 1.0
 */
public class INVValidAndQtyWarnTool
    extends TJDOTool { 
    /**
     * ʵ��
     */
    public static INVValidAndQtyWarnTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndValidAndQtyWarnTool
     */
    public static INVValidAndQtyWarnTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVValidAndQtyWarnTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INVValidAndQtyWarnTool() {
        setModuleName("inv\\INVValidAndQtyWarnModule.x");
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
