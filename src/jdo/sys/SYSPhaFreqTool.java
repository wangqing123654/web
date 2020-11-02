/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
*
* <p>Title: ҩƷƵ��</p>
*
* <p>Description:ҩƷƵ�� </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 20080901
* @version 1.0
*/
public class SYSPhaFreqTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSPhaFreqTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSPhaRouteTool
     */
    public static SYSPhaFreqTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSPhaFreqTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSPhaFreqTool()
    {
        setModuleName("sys\\SYSPhaFreqModule.x");
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
     * ����Ƶ�δ����ѯ����
     * @param FREQ_CODE String Ƶ�δ���
     * @return TParm
     */
    public TParm selectdata(String FREQ_CODE){
        TParm parm = new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
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
     * ����ָ��Ƶ�δ���õ�����
     * @param FREQ_CODE String
     * @return TParm
     */
	public TParm insertdata(TParm parm) {
       String FREQ_CODE= parm.getValue("FREQ_CODE");
       //System.out.println("FREQ_CODE"+FREQ_CODE);
       TParm result=new TParm();
       if(!existsFREQCODE(FREQ_CODE)){
    	   result = update("insertdata",parm);
           if(result.getErrCode() < 0)
           {
               err("ERR:" + result.getErrCode() + result.getErrText() +
                   result.getErrName());
               return result;
           }
       }else{
    	   result.setErr(-1,"Ƶ�δ��� "+FREQ_CODE+" �Ѿ�����!");
           return result ;
       }

       return result;
	}
	/**
     * �ж��Ƿ��������
     * @param FREQ_CODE String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsFREQCODE(String FREQ_CODE){
        TParm parm = new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        //System.out.println("existsFREQCODE"+FREQ_CODE);
        return getResultInt(query("existsFREQCODE",parm),"COUNT") > 0;
    }
	/**
     * ����ָ��UNIT_CODE����
     * @param UNIT_CODE String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String FREQ_CODE= parm.getValue("FREQ_CODE");
        //System.out.println("true or false"+existsFREQCODE(FREQ_CODE));
        if(existsFREQCODE(FREQ_CODE)){
        	 result = update("updatedata", parm);
        	 if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
        }else{
        	result.setErr(-1,"Ƶ�δ��� "+FREQ_CODE+" �ոձ�ɾ����");
            return result ;
        }

        return result;
    }
    /**
     * ɾ��ָ��Ƶ�δ�������
     * @param FREQ_CODE String
     * @return boolean
     */
    public TParm deletedata(String FREQ_CODE){
        TParm parm = new TParm();
        TParm result=new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        //Todo:�������ű�����ж�
//        if(!allowupdate(FREQ_CODE)){
        	result = update("deletedata",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
//        }else{
//        	result.setErr(-1,"Ƶ�δ��� "+FREQ_CODE+" ���ڱ�ʹ�ã�������ɾ����");
//        	return result;
//        }

    }
    /**
     * ����UNIT_CODE�ж�SYS_FEE����û�и�UNIT_CODE������������ɾ��
     * @param UNIT_CODE
     * @return
     */
    public boolean allowupdate(String FREQ_CODE){
    	TParm parm = new TParm();
        parm.setData("FREQ_CODE",FREQ_CODE);
        return getResultInt(query("allowupdate",parm),"COUNT") > 0;
    }
    /**
     * ����Ƶ�δ��������������
     * @param FREQ_CODE Ƶ�δ���
     * @return
     */
    public TParm selectSYSTRTFREQTIME(String FREQ_CODE){
    	TParm parm=new TParm();
    	TParm result =new TParm();
    	parm.setData("FREQ_CODE",FREQ_CODE);
    	result=this.query("selectSYSTRTFREQTIME",parm);
    	if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����������¼�Ƿ����
     * @param FREQ_CODE Ƶ�δ���
     * @param STANDING_TIME ʱ��
     * @return
     */
    public int existSYSTRTFREQTIME(TParm inParm){
        TParm parm = new TParm();
        return getResultInt(query("existSYSTRTFREQTIME",inParm),"COUNT");
    }
    /**
     * ���븽������
     * @param parm
     * @return
     */
    public TParm insertSYSTRTFREQTIME(TParm parm){
    	 String FREQ_CODE= parm.getValue("FREQ_CODE");
    	 String STANDING_TIME= parm.getValue("STANDING_TIME");
         TParm result=new TParm();
         TParm inParm =new TParm();
         inParm.setData("FREQ_CODE",FREQ_CODE);
         inParm.setData("STANDING_TIME",STANDING_TIME);
         if(existSYSTRTFREQTIME(inParm)>0){
      	   result = update("insertSYSTRTFREQTIME",parm);
             if(result.getErrCode() < 0)
             {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
         }else{
      	   result.setErr(-1,"�ü�¼�Ѿ�����!");
             return result ;
         }

         return result;
    }
    public TParm deleteSYSTRTFREQTIME(String FREQ_CODE,String STANDING_TIME){
         TParm result=new TParm();
         TParm parm=new TParm();
         parm.setData("FREQ_CODE",FREQ_CODE);
         parm.setData("STANDING_TIME",STANDING_TIME);
         //Todo:�������ű�����ж�
//         if(!allowupdate(FREQ_CODE)){
         	result = update("deleteSYSTRTFREQTIME",parm);
             if(result.getErrCode() < 0)
             {
                 err("ERR:" + result.getErrCode() + result.getErrText() +
                     result.getErrName());
                 return result;
             }
             return result;
//         }else{
//         	result.setErr(-1,"Ƶ�δ��� "+FREQ_CODE+" ���ڱ�ʹ�ã�������ɾ����");
//         	return result;
//         }
    }
    /**
     * ץȡָ��Ƶ�λ�������
     * @param freqCode
     * @return TParm
     */
    public TParm queryForAmount(String freqCode){
    	TParm parm=new TParm();
    	TParm result =new TParm();
    	parm.setData("FREQ_CODE",freqCode);
    	result=this.query("queryForAmount",parm);
    	if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
