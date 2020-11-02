/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: �����</p>
*
* <p>Description:����� </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800901
* @version 1.0
*/
public class SYSDiagnosisTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSDiagnosisTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSDiagnosisTool
     */
    public static SYSDiagnosisTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSDiagnosisTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSDiagnosisTool()
    {
        setModuleName("sys\\SYSDiagnosisModule.x");
        onInit();
    }
    /**
     * ���ݼ��������ѯ����
     * @param ICD_CODE String ��������,ICD_TYPE �������ͣ���ҽ����ҽ��
     * @return TParm
     */
    public TParm selectdata(String ICD_CODE,String ICD_TYPE){
        TParm parm = new TParm();
        if(ICD_CODE==null||"".equals(ICD_CODE)){
        	parm.setData("ICD_TYPE",ICD_TYPE);
        	TParm result = query("selectdatawithtype",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
        }else{
        	parm.setData("ICD_CODE",ICD_CODE);
            parm.setData("ICD_TYPE",ICD_TYPE);
            TParm result = query("selectdatawithkeys",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;

        }

    }
    /**
     * Ϊ��ʼ��icd combo��
     * @param icdType
     * @return tparm
     */
    public TParm initCombo(String icdType){
    	TParm parm=new TParm();
    	if(icdType==null||icdType.length()==0){
    		return null;
    	}
    	parm.setData("ICD_TYPE",icdType);
    	TParm result=query("initcombo",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ICD_CODE��ѯ�����Ϣ
     * @param ICD_CODE String
     * @return TParm
     */
    public TParm selectDataWithCode(String ICD_CODE){
        TParm parm = new TParm();
        parm.setData("ICD_CODE",ICD_CODE);
        TParm result = this.query("selectdatawithcode",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
