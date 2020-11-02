package jdo.dev;

import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: ����ϸ������</p>
 *
 * <p>Description:����ϸ�����ࣩ</p>
 *  
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author  fux  
 * @version 1.0
 */  
public class DevOutRequestDTool  extends TJDOTool{
	 /**
     * ������
     */
    public DevOutRequestDTool() {
    	//����������
        setModuleName("dev\\DevRequestDModule.x");    
        onInit();
    }

    /**
     * ʵ��
     */
    private static DevOutRequestDTool instanceObject;

    /**
     * �õ�ʵ��  
     * @return MainStockRoomTool
     */  
    public static DevOutRequestDTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevOutRequestDTool();
        return instanceObject;
    }

    /**  
     * �õ��豸������Ϣ
     * @param parm TParm
     * @return TParm 
     */
    public TParm queryRequestD(TParm parm){  
            parm = query("queryRequestD",parm);    
        return parm;
    }
    /**  
     * �õ��豸������Ϣ  
     * @param parm TParm      
     * @return TParm
     */        
    public TParm queryRequestD(String requestNO){  
    	    TParm parm = new TParm();   
    	    parm.setData("REQUEST_NO",requestNO);  
            parm = query("queryRequestD",parm);    
        return parm;    
    }

 
    /**
     * �����豸������ϸ��  
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */ 
    public TParm updateRequestD(TParm parm, TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("updateRequestD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();   
				return parm;
			}  
		}   
        return parm; 
    }
    /**
     * д���豸������ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm 
     */
    public TParm createNewRequestD(TParm parm, TConnection connection){
    	////system.out.println("д���豸������ϸ��");
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("createNewRequestD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close(); 
				return parm;
			}  
		}  
        return parm;
    }
    /**
     * �����豸������ϸ��״̬
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm updateRequestDDFlg(TParm parm, TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {  
			parm = update("updateRequestDFlg",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}  
		}  
        return parm;
    }
    /**
     * ɾ������ϸ��DEV_REQUESTDD
     * @param parm
     * @param connection
     * @return
     */
    public TParm deletedevrequestdd(TParm parm) {  
    	parm = update("deletedevrequestd", parm);
		if (parm.getErrCode() < 0) {
   		    err("ERR:" + parm.getErrCode() + parm.getErrText() +
			             parm.getErrName());
	        return parm;  
		}    
    	return parm;
    }   
    /**
     * ������parmת����parmList
     * @param parm
     * @return
     */
    public List<TParm> getTParmList(TParm parm){
    	// ���ؽ��list
    	List<TParm> parmList = new ArrayList<TParm>();
    	TParm tempParm;
    	String[] names = parm.getNames();
    	// һ��parm���Ŷ���������
    	int count = parm.getCount(names[0]);
    	for (int i=0; i<count; i++) {
			tempParm = new TParm();
			for (String name : names) {
				tempParm.setData(name, parm.getData(name, i));
			}
			parmList.add(tempParm);
		}
    	
    	return parmList; 
    }




}
