package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��������ά��������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 20012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author liuzhen 2012-8-2
 * @version 1.0
 */
public class MROSeqTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROSeqTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROSeqTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROSeqTool();
        return instanceObject;
    }
    public MROSeqTool(){
        this.setModuleName("mro\\MROSeqModule.x");
        this.onInit();
    }
    /**
     * ��ѯ����
     * @param parm TParm ����˵����TYPE_CODE,TYPE_DESC������������ ���п���
     * @return TParm
     */
    public TParm selectType(TParm parm){
        TParm pm = new TParm();
        //pm.setData("TYPE_CODE",parm.getValue("TYPE_CODE")+"%");
        //pm.setData("TYPE_DESC",parm.getValue("TYPE_DESC")+"%");
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
    public TParm deletedata(int seq){
        TParm parm = new TParm();
        parm.setData("SEQ",seq);
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
