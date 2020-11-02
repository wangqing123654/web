package jdo.spc;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class INDBillPageTool extends TJDOTool {
    private static INDBillPageTool instanceObject;
    public INDBillPageTool() {
        setModuleName("spc\\INDBillPageToolModule.x");
        onInit();
    }
    public static INDBillPageTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new INDBillPageTool();
        }
        return instanceObject;
    }
    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSysFeeMed(TParm parm) {
        TParm result = new TParm();
        result = query("selectSysFeeMed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��ҳ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseIN(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectDispenseIN", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ��ҳ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseOUT(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectDispenseOUT", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ��ҳ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectVerifyin(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectVerifyin", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ��ҳ�˻�����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectRegress(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectRegress", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯָ�����ڵĿ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStockQty(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectStockQty", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
}
