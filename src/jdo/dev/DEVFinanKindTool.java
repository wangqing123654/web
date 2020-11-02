package jdo.dev;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:�豸������𹤾���</p>
 *
 * <p>Description:�豸������𹤾��� </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company:bluecore </p>
 * 
 * @author fux
 * @version 1.0
 */
public class DEVFinanKindTool  extends TJDOTool{
	
    /**
     * ������
     */
    public DEVFinanKindTool() {
        setModuleName("dev\\DEVFinanKindModule.x");
        onInit();
    }
    /**
     * ʵ��
     */
    private static DEVFinanKindTool instanceObject;

    /** 
     * �õ�ʵ��
     * @return DEVFinanKindTool
     */
    public static DEVFinanKindTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DEVFinanKindTool();
        return instanceObject;
    }
    /**
     * д���豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertFinan(TParm parm){
    	parm = update("insertFinan",parm); 
        return parm;
    }
    /** 
     * д���豸�����Ϣ
     * @param parm TParm
     * @param connection TConnection 
     * @return TParm
     */
    public TParm updateFinan(TParm parm){
    	parm = update("updateFinan",parm);
        return parm;
    }
}
