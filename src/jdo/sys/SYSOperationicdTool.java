/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ����ICD</p>
*
* <p>Description:����ICD </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSOperationicdTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSOperationicdTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSOperationicdTool
     */
    public static SYSOperationicdTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSOperationicdTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSOperationicdTool()
    {
        setModuleName("sys\\SYSOperationicdModule.x");
        onInit();
    }
    /**
     * ��ʼ�����棬��ѯ���е�����
     * @return TParm
     */
    public TParm selectall(){
    	 TParm parm = new TParm();
//         parm.setData("CODE",CODE);
         TParm result = query("selectall",parm);
         if(result.getErrCode() < 0)
         {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
     * ��������ICD��������ѯ����
     * @param OPERATION_ICD String ����ICD����
     * @return TParm
     */
    public TParm selectdata(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
        TParm result = query("selectdata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ָ������ICD����õ�����
     * @param OPERATION_ICD String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String OPERATION_ICD= parm.getValue("OPERATION_ICD");
       //System.out.println("OPERATION_ICD"+OPERATION_ICD);
       TParm result=new TParm();
       if(!existsPosition(OPERATION_ICD)){
    	   
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"����ICD���� "+OPERATION_ICD+" �Ѿ�����!");
           return result ;
       }

       return result;
	}
	/**
     * �ж��Ƿ��������
     * @param OPERATION_ICD String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsPosition(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
        //System.out.println("existsPosition"+OPERATION_ICD);
        return getResultInt(query("existsICD",parm),"COUNT") > 0;
    }
	/**
     * ����ָ��OPERATION_ICD����
     * @param posCode String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String OPERATION_ICD= parm.getValue("OPERATION_ICD");
        //System.out.println("true or false"+existsPosition(OPERATION_ICD));
        if(existsPosition(OPERATION_ICD)){
        	
        	 result = update("updatedata", parm);
        	
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"����ICD���� "+OPERATION_ICD+" �ոձ�ɾ����");
            return result ;
        }

        return result;
    }
    /**
     * ɾ��ָ�����ְ������
     * @param posCode String
     * @return boolean
     */
    public TParm deletedata(String OPERATION_ICD){
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD",OPERATION_ICD);
        TParm result = update("deletedata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
