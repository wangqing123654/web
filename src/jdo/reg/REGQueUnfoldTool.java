package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class REGQueUnfoldTool extends TJDOTool{
	
	 /**
     * ʵ��
     */
    public static REGQueUnfoldTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ClinicAreaTool
     */
    public static REGQueUnfoldTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGQueUnfoldTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public REGQueUnfoldTool() {
        setModuleName("reg\\REGQueUnfoldModule.x");
        onInit();
    }
    
    /**
     * ����VIP�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm addRegQue(TParm parm, TConnection conn) {

        TParm result = update("addRegQue", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    } 

}
