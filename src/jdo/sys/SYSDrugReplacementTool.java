/**
 * 
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: ���ҩ</p>
*
* <p>Description:���ҩ </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800901
* @version 1.0
*/
public class SYSDrugReplacementTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSDrugReplacementTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSDrugReplacementTool
     */
    public static SYSDrugReplacementTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSDrugReplacementTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSDrugReplacementTool()
    {
        setModuleName("sys\\SYSDrugReplacementModule.x");
        onInit();
    }
    /**
     * ���� ORDER_CODE,REPLACE_ORDER_CODE ��ѯ����
     * @param ORDER_CODE String ,REPLACE_ORDER_CODE String
     * @return TParm
     */
    public TParm selectdata(String ORDER_CODE,String REPLACE_ORDER_CODE){
    	TParm parm = new TParm();
    	TParm result=new TParm();
    	if("".equals(REPLACE_ORDER_CODE)){
    		parm.setData("ORDER_CODE",ORDER_CODE);
            result = query("selectbyordercode",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
    	}else if("".equals(ORDER_CODE)){
    		parm.setData("REPLACE_ORDER_CODE",REPLACE_ORDER_CODE);
            result = query("selectbyreplaceordercode",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            
    	}else{
    		parm.setData("ORDER_CODE",ORDER_CODE);
    		parm.setData("REPLACE_ORDER_CODE",REPLACE_ORDER_CODE);
            result = query("selectdata",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
    	}
    	return result;
        
    }
    public TParm selectSysFee(String ORDER_CODE){
    	TParm parm = new TParm();
        parm.setData("ORDER_CODE",ORDER_CODE);
        TParm result = query("selectsysfee",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
