package jdo.opd;

import jdo.reg.RegMethodTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ����ҽ������վ����</p>
*
* <p>Description:����ҽ������վ����</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800904
* @version 1.0
*/
public class OPDMainTool extends TJDOTool{

	/**
     * ʵ��
     */
    public static OPDMainTool instanceObject;
    /**
     * �õ�ʵ��
     * @return OPDMainTool
     */
    public static OPDMainTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new OPDMainTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public OPDMainTool()
    {
        setModuleName("opd\\OPDMainModule.x");
        onInit();
    }
    /**
     * ��ʼ�������ص�������ʾ����
     *
     * @return TParm
     */
    public TParm initDiaTree(){
        TParm parm = new TParm();
        TParm result = query("initdiatree",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ʼ��ҽ����ص�������ʾ����
     *
     * @return TParm
     */
    public TParm initOrderTree(){
        TParm parm = new TParm();
        TParm result = query("initordertree",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
