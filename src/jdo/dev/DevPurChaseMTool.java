package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
 
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
public class DevPurChaseMTool  extends TJDOTool{

    /**
     * ������
     */
    public DevPurChaseMTool() { 
        setModuleName("dev\\DevPurChaseModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static DevPurChaseMTool instanceObject;

    /**
     * �õ�ʵ��
     * @return MainStockRoomTool
     */
    public static DevPurChaseMTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevPurChaseMTool();
        return instanceObject;
    }

    /**
     * д���豸�빺����
     * @param parm TParm
     * @return TParm  
     */
    public TParm insertDevPurChaseM(TParm parm,String type){
    	//system.out.println("д���豸�빺����");
    	if ("INSERT".equals(type)){ 
        //system.out.println("INSERT"); 
        parm = update("insertM",parm); 
    	} 
    	else{
        parm = update("updateM",parm);	
    	}
        return parm;
    }
    
 
   
    /** 
     * ɾ���豸�빺����
     * @param devCode String
     * @return TParm
     */
    public TParm deleteDevBase(String requestNo){
        TParm parm = new TParm();
        parm.setData("REQUEST_NO",requestNo); 
        parm = update("deleteM",parm); 
        return parm;
    }
}
