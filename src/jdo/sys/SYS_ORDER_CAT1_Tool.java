package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
*
* <p>Title: ҽ��ϸ����</p>
*
* <p>Description:ҽ��ϸ���� </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/

public class SYS_ORDER_CAT1_Tool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYS_ORDER_CAT1_Tool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static SYS_ORDER_CAT1_Tool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYS_ORDER_CAT1_Tool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYS_ORDER_CAT1_Tool()
    {
        setModuleName("sys\\SYSOrderCAT1Module.x");
        onInit();
    }
    /**
     * ����ҽ��ϸ�������ѯְ����Ϣ
     * @param posCode String ְ�����
     * @return TParm
     */
    public TParm selectdata(String ORDER_CAT1_CODE){
        TParm parm = new TParm();
        parm.setData("ORDER_CAT1_CODE",ORDER_CAT1_CODE);
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
     * ����ָ���������
     * @param ORDER_CAT1_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String ORDER_CAT1_CODE= parm.getValue("ORDER_CAT1_CODE");
       TParm result=new TParm();
       if(!existsPosition(ORDER_CAT1_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"ҽ��ϸ������� "+ORDER_CAT1_CODE+" �Ѿ�����!");
           return result ;
       }

       return result;
	}
	/**
     * ����ָ���������
     * @param posCode String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String ORDER_CAT1_CODE= parm.getValue("ORDER_CAT1_CODE");
        //System.out.println("true or false"+existsPosition(ORDER_CAT1_CODE));
        if(existsPosition(ORDER_CAT1_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"����ҽ��ϸ���� "+ORDER_CAT1_CODE+" �ոձ�ɾ����");
            return result ;
        }

        return result;
    }
	 /**
     * �ж��Ƿ��������
     * @param ORDER_CAT1_CODE String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsPosition(String ORDER_CAT1_CODE){
        TParm parm = new TParm();
        parm.setData("ORDER_CAT1_CODE",ORDER_CAT1_CODE);
        return getResultInt(query("existsPosition",parm),"COUNT") > 0;
    }
    /**
     * ɾ��ָ�����ְ������
     * @param posCode String
     * @return boolean
     */
    public TParm deletedata(String posCode){
        TParm parm = new TParm();
        parm.setData("ORDER_CAT1_CODE",posCode);
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
