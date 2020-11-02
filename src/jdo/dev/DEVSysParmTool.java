package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �豸���뷽ʽ</p>
 *
 * <p>Description:�豸���뷽ʽ </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DEVSysParmTool  extends TJDOTool{
    /**
     * ������
     */
    public DEVSysParmTool() {
        setModuleName("dev\\DevSysParmModule.x");
        onInit();
    }
    /**
     * ʵ��
     */
    private static DEVSysParmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DEVSysParmTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVSysParmTool();
        return instanceObject;
    }

    /**
     * ȡ��ҩ���������Ϣ
     * @return TParm
     */
    public TParm selectDevSysParm(){
        TParm parm = new TParm();
        parm = query("selectDevSysParm",parm);
        return parm;
    }
    /**
     * д��ҩ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDevSysParm(TParm parm){
        parm = update("insertDevSysParm",parm);
        return parm;
    }

    /**
     * ����ҩ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDevSysParm(TParm parm){
        parm = update("updateDevSysParm",parm);
        return parm;
    }
}
