package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ҩƷת���ʵ�tool
*
* <p>Description: ҩƷת���ʵ�tool</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20081005
* @version 1.0
*/
public class PhaTransUnitTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static PhaTransUnitTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PhaTransUnitTool
     */
    public static PhaTransUnitTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaTransUnitTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhaTransUnitTool() {
        setModuleName("pha\\PhaTransUnitModule.x");

        onInit();
    }
    /**
     * ץȡҩƷ��ҩ��λ����ҩ��λ����浥λת������
     * @param parm
     * @return TParm 
     */
    public TParm queryForAmount(TParm parm){
    	TParm result = new TParm();
        result = query("queryForAmount", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
