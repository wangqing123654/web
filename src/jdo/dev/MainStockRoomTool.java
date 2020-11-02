package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ���ⲿ���趨</p>
 *
 * <p>Description: ���ⲿ���趨</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class MainStockRoomTool  extends TJDOTool{
    /**
     * ������
     */
    public MainStockRoomTool() {
        setModuleName("dev\\MainStockRoomModule.x");
        onInit();
    }
    /**
     * ʵ��
     */
    private static MainStockRoomTool instanceObject;
    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static MainStockRoomTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new MainStockRoomTool();
        return instanceObject;
    }

    /**
     * �õ����ⲿ����Ϣ
     * @param deptCode String
     * @return TParm
     */
    public TParm selectDevDeptInf(String deptCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm = query("selectDevDeptInf",parm);
        return parm;
    }
    /**
     * �������ⲿ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDevDeptInf(TParm parm){
        parm = update("updateDevDeptInf",parm);
        return parm;
    }
    /**
     * д�����ⲿ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDevDeptInf(TParm parm){
        parm = update("insertDevDeptInf",parm);
        return parm;
    }

    /**
     * ɾ�����ⲿ����Ϣ
     * @param deptCode String
     * @return TParm
     */
    public TParm deleteDevDeptInf(String deptCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", deptCode);
        parm = update("deleteDevDeptInf",parm);
        return parm;
    }

    /**
     * ȡ���豸�������˳���
     * @return int
     */
    public int getDevDeptMaxSeq(){
        TParm parm = query("getDevDeptMaxSeq");
        if(parm.getErrCode() < 0)
            return 1;
        if(parm.getCount("SEQ") <= 0)
            return 1;
        if(parm.getValue("SEQ",0) == null ||
           parm.getValue("SEQ",0).length() == 0)
            return 1;
        return parm.getInt("SEQ",0) + 1;
    }
}
