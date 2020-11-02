package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:�Һŷ�ʽ������ </p>
 *
 * <p>Description:�Һŷ�ʽ������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.12
 * @version 1.0
 */
public class RegMethodTool extends TJDOTool{

    /**
     * ʵ��
     */
    public static RegMethodTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static RegMethodTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new RegMethodTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public RegMethodTool()
    {
        setModuleName("reg\\REGRegMethodModule.x");
        onInit();
    }
    /**
     * �����Һŷ�ʽ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String regMethod = parm.getValue("REGMETHOD_CODE");
        if(existsRegMethod(regMethod)){
            result.setErr(-1,"�Һŷ�ʽ "+" �Ѿ�����!");
            return result ;
        }
        result = update("insertdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���¹Һŷ�ʽ
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݹҺŷ�ʽ�����ѯ��ʽ��Ϣ(�Һ���)
     * @param regMethod String �Һŷ�ʽ����
     * @return TParm
     */
    public TParm selectdata(String regMethod){
        TParm parm = new TParm();
        regMethod += "%";
        parm.setData("REGMETHOD_CODE",regMethod);
        TParm result = query("selectdata",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���Һŷ�ʽ
     * @param regMethod String
     * @return boolean
     */
    public TParm deletedata(String regMethod){
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE",regMethod);
        TParm result = update("deletedata",parm);
        // �жϴ���ֵ
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ж��Ƿ���ڹҺŷ�ʽ
     * @param regMethod String �Һŷ�ʽ����
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsRegMethod(String regMethod){
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE",regMethod);
        return getResultInt(query("existsRegMethod",parm),"COUNT") > 0;
    }
    /**
     * ��ѯ�Һŷ�ʽcombo�Ƿ������ע��
     * @param regmethodCode String �Һŷ�ʽ����
     * @return boolean true ������ false ��������
     */
    public boolean selComboFlg(String regmethodCode){
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE", regmethodCode);
        TParm result = query("selComboFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;
    }
    /**
     * �õ���Ʊע��
     * @param regMethod String
     * @return boolean
     */
    public boolean selPrintFlg(String regMethod) {
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE", regMethod);
        TParm result = query("selPrintFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        boolean printFlg = result.getBoolean("PRINT_FLG", 0);
        return printFlg;
    }
    
    /**
     * �õ��ֳ��ű��
     * @param regMethod
     * @return
     */
    public boolean selSiteNumFlg(String regMethod){
    	 TParm parm = new TParm();
         parm.setData("REGMETHOD_CODE", regMethod);
         TParm result = query("selSiteNumFlg", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrName() + " " + result.getErrText());
             return false;
         }
         boolean siteNumFlg = result.getBoolean("SITENUM_FLG", 0);
         return siteNumFlg;
    	
    }

}
