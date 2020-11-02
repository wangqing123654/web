package jdo.sum;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:�������๤���� </p>
 *
 * <p>Description:�������๤���� </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMTmptrKindTool extends TJDOTool{

    /**
     * ʵ��
     */
    public static SUMTmptrKindTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static SUMTmptrKindTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SUMTmptrKindTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SUMTmptrKindTool()
    {
        setModuleName("sum\\SUMTmptrKindModule.x");
        onInit();
    }

    /**
     * ������������
     * @param regMethod String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String regMethod = parm.getValue("TMPTRKINDCODE");
        if(existsRegMethod(regMethod)){
            result.setErr(-1,"����������� "+" �Ѿ�����!");
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
     * ������������
     * @param regMethod String
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
     * ����������������ѯ��ʽ��Ϣ(�Һ���)
     * @param regMethod String �����������
     * @return TParm
     */
    public TParm selectdata(String regMethod){
        TParm parm = new TParm();
        regMethod += "%";
        parm.setData("TMPTRKINDCODE",regMethod);
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
     * ɾ����������
     * @param regMethod String
     * @return boolean
     */
    public TParm deletedata(String regMethod){
        TParm parm = new TParm();
        parm.setData("TMPTRKINDCODE",regMethod);
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
     * �ж��Ƿ������������
     * @param regMethod String �����������
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsRegMethod(String regMethod){
        TParm parm = new TParm();
        parm.setData("TMPTRKINDCODE",regMethod);
        return getResultInt(query("exists",parm),"COUNT") > 0;
    }


}
