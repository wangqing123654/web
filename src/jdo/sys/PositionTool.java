package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: ��Աְ�𹤾���
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 20080818
 *
 * <p>Company: javahis
 *
 * @author wangl 2008.08.18
 * @version 1.0
 */
public class PositionTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static PositionTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PositionTool
     */
    public static PositionTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new PositionTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public PositionTool()
    {
        setModuleName("sys\\SYSPositionModule.x");
        onInit();
    }
    /**
     * ����ָ�����ְ������
     * @param posCode String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String code = parm.getValue("POS_CODE");
        if(existsPosition(code)){
            result.setErr(-1,"ְ���� "+code+" �Ѿ�����!");
            return result ;
        }
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ָ�����ְ������
     * @param posCode String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����ְ������ѯְ����Ϣ(�Һ���)
     * @param posCode String ְ�����
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        //�����LIKE����--------start----------
        if (parm == null)
            return null;
        if (parm.existData("POS_CODE")) {
            String posCode = parm.getValue("POS_CODE");
            posCode += "%";
            parm.setData("POS_CODE", posCode);
        }
        //�����LIKE����--------end----------

        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��ָ�����ְ������
     * @param posCode String
     * @return boolean
     */
    public TParm deletedata(String posCode){
        TParm parm = new TParm();
        parm.setData("POS_CODE",posCode);
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
     * �ж��Ƿ����ְ��
     * @param posCode String ְ�����
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsPosition(String posCode){
        TParm parm = new TParm();
        parm.setData("POS_CODE",posCode);
        return getResultInt(query("existsPosition",parm),"COUNT") > 0;
    }
}
