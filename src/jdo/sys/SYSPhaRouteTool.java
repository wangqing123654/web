/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ��ҩ;��</p>
*
* <p>Description:��ҩ;�� </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSPhaRouteTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSPhaRouteTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSPhaRouteTool
     */
    public static SYSPhaRouteTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSPhaRouteTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSPhaRouteTool()
    {
        setModuleName("sys\\SYSPhaRouteModule.x");
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
     * ���ݸ�ҩ;����������ѯ����
     * @param ROUTE_CODE String ��ҩ;������
     * @return TParm
     */
    public TParm selectdata(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
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
     * ����ָ����ҩ;������õ�����
     * @param ROUTE_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String ROUTE_CODE= parm.getValue("ROUTE_CODE");
       //System.out.println("ROUTE_CODE"+ROUTE_CODE);
       TParm result=new TParm();
       if(!existsROUTECODE(ROUTE_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"��ҩ;������ "+ROUTE_CODE+" �Ѿ�����!");
           return result ;
       }

       return result;
	}
	/**
     * �ж��Ƿ��������
     * @param ROUTE_CODE String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsROUTECODE(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
        //System.out.println("existsROUTECODE"+ROUTE_CODE);
        return getResultInt(query("existsROUTECODE",parm),"COUNT") > 0;
    }
	/**
     * ����ָ��ROUTE_CODE����
     * @param ROUTE_CODE String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String ROUTE_CODE= parm.getValue("ROUTE_CODE");
        //System.out.println("true or false"+existsROUTECODE(ROUTE_CODE));
        if(existsROUTECODE(ROUTE_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"��ҩ;������ "+ROUTE_CODE+" �ոձ�ɾ����");
            return result ;
        }

        return result;
    }
    /**
     * ɾ��ָ����ҩ;����������
     * @param ROUTE_CODE String
     * @return boolean
     */
    public TParm deletedata(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
        TParm result = update("deletedata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����SYS_PHAROUTE.ORDER_CODE��ѯSYS_FEE�������
     *     ORDER_CODE,ORDER_DESC,DESCRIPTION,GOODS_DESC,ALIAS_DESC
     * @param ROUTE_CODE String ��ҩ;������
     * @return TParm
     */
    public TParm selectsysfee(String ROUTE_CODE){
        TParm parm = new TParm();
        parm.setData("ROUTE_CODE",ROUTE_CODE);
        TParm result = query("selectfromsysfee",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ����SYS_FEE�������
     *     ORDER_CODE,ORDER_DESC,DESCRIPTION,GOODS_DESC,ALIAS_DESC
     * @param ROUTE_CODE String ��ҩ;������
     * @return TParm
     */
    public TParm selectallsysfee(){
        TParm parm = new TParm();
        TParm result = query("selectallfromsysfee",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����OrderCode����;������
     * @param orderCode String ָ����ordercode
     * @return TParm ��ѯ��������Ӧ��;������
     */
    public TParm selectByOrder(String orderCode){
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("selectByOrder",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
