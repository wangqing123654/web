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
public class InvRegressGoodsTotalTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvRegressGoodsTotalTool instanceObject;

    /**
     * ������
     */
    public InvRegressGoodsTotalTool() {
        setModuleName("inv\\INVRegressGoodsTotalModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvVerifyinMTool
     */
    public static InvRegressGoodsTotalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvRegressGoodsTotalTool();
        return instanceObject;
    }

    /**
     * �˻�ͳ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryRegressGoodsTotal(TParm parm){
        TParm result = this.query("queryRegressGoodsTotal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm onQueryRegressGoodsDetail(TParm parm)
    {
      TParm result = query("queryRegressGoodsDetail", parm);
      if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() + 
          result.getErrName());
        return result;
      }
      return result;
    }
}
