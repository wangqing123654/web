package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsSysFeeTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static InsSysFeeTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static InsSysFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InsSysFeeTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public InsSysFeeTool() {
        setModuleName("ins\\InsSysFeeModule.x");
        onInit();
    }
    /**
 * ��ѯSYS_FEE
 * @param parm TParm
 * @return TParm
 */
public TParm selSysFee(TParm parm) {
    //System.out.println("002 Tool ��ѯ=====��");

    TParm result = new TParm();
    result = query("query", parm);
   // System.out.println("result=" + result);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;

}


}
