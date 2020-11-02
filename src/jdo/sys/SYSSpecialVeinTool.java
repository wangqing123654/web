/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ����ע��</p>
*
* <p>Description:����ע�� </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSSpecialVeinTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSSpecialVeinTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSPhaRouteTool
     */
    public static SYSSpecialVeinTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSSpecialVeinTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSSpecialVeinTool()
    {
        setModuleName("sys\\SYSSpecialVeinModule.x");
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
     * �������⾲����ҩ����ѯ����
     * @param EXCPHATYPE_CODE String ��ҩ;������
     * @return TParm
     */
    public TParm selectdata(String EXCPHATYPE_CODE){
        TParm parm = new TParm();
        parm.setData("EXCPHATYPE_CODE",EXCPHATYPE_CODE);
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
     * ����ָ�����⾲����ҩ���õ�����
     * @param EXCPHATYPE_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String EXCPHATYPE_CODE= parm.getValue("EXCPHATYPE_CODE");
       //System.out.println("EXCPHATYPE_CODE"+EXCPHATYPE_CODE);
       TParm result=new TParm();
       if(!existsEXCPHATYPECODE(EXCPHATYPE_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"���⾲����ҩ��� "+EXCPHATYPE_CODE+" �Ѿ�����!");
           return result ;
       }

       return result;
	}
	/**
     * �ж��Ƿ��������
     * @param EXCPHATYPE_CODE String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsEXCPHATYPECODE(String EXCPHATYPE_CODE){
        TParm parm = new TParm();
        parm.setData("EXCPHATYPE_CODE",EXCPHATYPE_CODE);
        //System.out.println("existsSpecialVein"+EXCPHATYPE_CODE);
        return getResultInt(query("existsSpecialVein",parm),"COUNT") > 0;
    }
	/**
     * ����ָ��EXCPHATYPE_CODE����
     * @param EXCPHATYPE_CODE String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String EXCPHATYPE_CODE= parm.getValue("EXCPHATYPE_CODE");
        //System.out.println("true or false"+existsEXCPHATYPECODE(EXCPHATYPE_CODE));
        if(existsEXCPHATYPECODE(EXCPHATYPE_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"���⾲����ҩ��� "+EXCPHATYPE_CODE+" �ոձ�ɾ����");
            return result ;
        }

        return result;
    }
    /**
     * ɾ��ָ�����⾲����ҩ�������
     * @param EXCPHATYPE_CODE String
     * @return boolean
     */
    public TParm deletedata(String EXCPHATYPE_CODE){
        TParm parm = new TParm();
        parm.setData("EXCPHATYPE_CODE",EXCPHATYPE_CODE);
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
