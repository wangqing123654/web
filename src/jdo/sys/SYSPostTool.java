package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: �ʱ�ά��</p>
*
* <p>Description:�ʱ�ά�� </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800905
* @version 1.0
*/
public class SYSPostTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static SYSPostTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SYSPostTool
     */
    public static SYSPostTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSPostTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SYSPostTool()
    {
        setModuleName("sys\\SYSPostCodeModule.x");
        onInit();
    }
    /**
     * ���ݲ�ͬ������ѯʡ���ʱ����Ϣ
     * @param parm: STATE,CITY,POST_NO3 ��Ϊ��ѡ��where����
     * @return TParm
     */
    public TParm selectdata(TParm parm){
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
     * �õ�ʡ
     * @param postNo3 String
     * @return String
     */
    public String getState(String postNo3)
    {
        if(postNo3 == null || postNo3.length() == 0)
            return "";
        TParm parm = new TParm();
        parm.setData("POST_CODE",postNo3);
        return getResultString(query("getState",parm),"STATE");
    }

    /**
     * �õ���
     * @param postNo3 String
     * @return String
     */
    public String getCity(String postNo3)
    {
        if(postNo3 == null || postNo3.length() == 0)
            return "";
        TParm parm = new TParm();
        parm.setData("POST_CODE",postNo3);
        return getResultString(query("getCity",parm),"CITY");
    }

    /**
     * �õ�ʡ��
     * @param postCode String
     * @return TParm
     */
    public TParm getProvinceCity(String postCode) {
        //System.out.println("===========SYSPostTool===================="+postCode);
        TParm parm = new TParm();
        parm.setData("POST_CODE", postCode);
        TParm result = query("selectall", parm);
        return result;
    }
    /**
     * �õ��ʱ�
     * 20111230 zhangp
     * @param code
     * @return
     */
    public TParm getPostCode(String code){
    	TParm parm = new TParm();
    	String sql = "SELECT POST_CODE,ENNAME,CITY_PY FROM SYS_POSTCODE WHERE ENNAME LIKE '%"+code+
    	"%' OR POST_CODE LIKE '%"+code+"%' OR CITY_PY LIKE '%"+code+"%'";
    	parm = (TParm) TJDODBTool.getInstance().select(sql);
    	return parm;
    }

}
