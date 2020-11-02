package jdo.inv;


import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:���ʿ��� </p>
 *
 * <p>Description: ���ʿ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-5-21
 * @version 1.0
 */

public class INVOrgTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static INVOrgTool instanceObject;
    /**
     * �õ�ʵ��
     * @return DeptTool
     */
    public static INVOrgTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVOrgTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INVOrgTool() {
        setModuleName("inv\\INVOrgModule.x");
        onInit();
    }
    /**
     * ���ʿ���
     * @return TParm
     */
    public TParm getDept(){
    TParm result = query("getDept");
    if (result.getErrCode() < 0)
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
    return result;

}






}
