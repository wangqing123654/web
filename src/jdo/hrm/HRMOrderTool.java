package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class HRMOrderTool extends TJDOTool {

    /**
     * ʵ��
     */
    public static HRMOrderTool instanceObject;

    /**
     * �õ�ʵ��
     * 
     * @return HRMCompanyTool
     */
    public static HRMOrderTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new HRMOrderTool();
        }
        return instanceObject;
    }

    /**
     * ������
     */
    public HRMOrderTool() {
        setModuleName("hrm\\HRMOrderModule.x");
        onInit();
    }

    /**
     * ����contract_code��mr_noɾ��ĳ�˵�����ҽ����δִ�еģ�
     * 
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteHrmOrder(TParm parm, TConnection conn) {
        TParm result = update("deleteHrmOrder", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����case_no��ѯһ���˵����м����Ŀ
     * 
     * @param parm
     * @return
     */
    public TParm selectMedApplyOrder(TParm parm) {
        TParm result = query("selectMedApplyOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
}
