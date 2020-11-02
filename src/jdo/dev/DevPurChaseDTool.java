package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
 
/**
 * <p>Title: �豸�빺����</p> 
 *
 * <p>Description:�豸�빺���� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:bulecore </p>
 *
 * @author  fux
 * @version 1.0 
 */
public class DevPurChaseDTool  extends TJDOTool{

    /**
     * ������
     */
    public DevPurChaseDTool() { 
        setModuleName("dev\\DevPurChaseModule.x");
        onInit();
    } 

    /**
     * ʵ��
     */
    private static DevPurChaseDTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DevPurChaseDTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevPurChaseDTool();
        return instanceObject;
    }

    /**
     * д���豸�빺ϸ��
     * @param parm TParm
     * @return TParm 
     */
    public TParm insertDevPurChaseD(TParm parm,String type){
    	//system.out.println("д���豸�빺ϸ��");; 
    	if ("INSERT".equals(type)){   
        parm = update("insertD",parm);
    	}
    	else{
        parm = update("updateD",parm);	
    	} 
        return parm;
    }
 
  
    /** 
     * ɾ���豸�빺ϸ��
     * @param devCode String
     * @return TParm
     */
    public TParm deleteDevBase(String requestNo){
        TParm parm = new TParm();
        parm.setData("REQUEST_NO",requestNo); 
        parm = update("deleteD",parm);
        return parm;
    }
}
