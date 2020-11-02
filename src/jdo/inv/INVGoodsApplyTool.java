package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

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
public class INVGoodsApplyTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static INVGoodsApplyTool instanceObject;

    /**
     * ������
     */
    public INVGoodsApplyTool() {
        setModuleName("inv\\INVGoodsApplyModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvVerifyinMTool
     */
    public static INVGoodsApplyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVGoodsApplyTool();
        return instanceObject;
    }

    /**
     * ��ѯ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryGoodsApplyReport(TParm parm){
        TParm result = this.query("queryGoodsApplyReport", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
