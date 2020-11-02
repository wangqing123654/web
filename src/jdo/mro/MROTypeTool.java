package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ���������Ŀ���͹�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-4-29
 * @version 1.0
 */
public class MROTypeTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROTypeTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROTypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROTypeTool();
        return instanceObject;
    }
    public MROTypeTool(){
        this.setModuleName("mro\\MROTypeModule.x");
        this.onInit();
    }
    /**
     * ��ѯ����
     * @param parm TParm ����˵����TYPE_CODE,TYPE_DESC������������ ���п���
     * @return TParm
     */
    public TParm selectType(TParm parm){
        TParm pm = new TParm();
        pm.setData("TYPE_CODE",parm.getValue("TYPE_CODE")+"%");
        pm.setData("TYPE_DESC",parm.getValue("TYPE_DESC")+"%");
        TParm result = query("selectdata",pm);
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
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��������
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
     * ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(String code){
        TParm parm = new TParm();
        parm.setData("TYPE_CODE",code);
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
}
