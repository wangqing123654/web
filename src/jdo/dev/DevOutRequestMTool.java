package jdo.dev;

import java.util.ArrayList;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: ������������</p>  
 *
 * <p>Description:������������ </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author  fux
 * @version 1.0
 */  
public class DevOutRequestMTool  extends TJDOTool{
	 /**
     * ������
     */
    public DevOutRequestMTool() {
        setModuleName("dev\\DevRequestMModule.x");  
        onInit();
    }
            
    /**
     * ʵ��
     */
    private static DevOutRequestMTool instanceObject;

    /**
     * �õ�ʵ��
     * @return DevOutRequestMTool
     */
    public static DevOutRequestMTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevOutRequestMTool();
        return instanceObject;  
    }
    /**  
     * �õ��豸������Ϣ1
     * @param parm TParm  
     * @return TParm
     */ 
    public TParm queryRequestM(TParm parm){ 
        parm = query("queryRequestM",parm);
        return parm;
    }
    /**      
     * �õ��豸������Ϣ2 
     * @param parm TParm
     * @return TParm
     */
    public TParm queryRequestM(String requestNO){  
    	    TParm parm = new TParm();
    	    parm.setData("REQUEST_NO",requestNO);
            parm = query("queryRequestM",parm);     
        return parm;
    }  
    /**  
     * ȡ�������(�����빺��) 
     * @return String
     */
    public String getRequestNo(){
        return SystemTool.getInstance().getNo("ALL", "DEV",
                "REQUEST_NO1", "REQUEST_NO1");  
    }
    /**
     * д���豸������ϸ��
     * @param parm TParm 
     * @param connection TConnection 
     * @return TParm
     */
    public TParm createNewRequestM(TParm parm, TConnection connection){
    	////system.out.println("д���豸������ϸ��"); 
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {   
			parm = update("createNewRequestM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close(); 
				return parm;
			}
		}
        
        return parm;
    }
    /**
     * �����豸������ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRequestM(TParm parm, TConnection connection){
    	List<TParm> parmList = getTParmList(parm);
    	////system.out.println("parmList"+parmList); 
    	for (TParm tParm : parmList) {     
			parm = update("updateRequestM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    /**
     * ɾ������ϸ��DEV_REQUESTM
     * @param parm
     * @param connection
     * @return
     */
    public TParm deletedevrequestmm(TParm parm) {    
    	parm = update("DELETEDEVREQUESTM", parm);  
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
			////system.out.println("tempParm"+tempParm);  
			parmList.add(tempParm);
		}
    	
    	return parmList;
    }

  
}
