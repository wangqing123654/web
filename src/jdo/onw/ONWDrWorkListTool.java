package jdo.onw;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: �ż���ҽʦ������ͳ��</p>
 *
 * <p>Description: �ż���ҽʦ������ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-4
 * @version 1.0
 */
public class ONWDrWorkListTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ONWDrWorkListTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static ONWDrWorkListTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWDrWorkListTool();
        return instanceObject;
    }
    public ONWDrWorkListTool() {
        setModuleName("onw\\ONWDrWorkListModule.x");
        onInit();
    }
    /**
     * ��ѯ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
