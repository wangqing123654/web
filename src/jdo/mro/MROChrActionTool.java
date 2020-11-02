package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title:���������Tool </p>
 *
 * <p>Description:���������Tool </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-4
 * @version 1.0
 */
public class MROChrActionTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static MROChrActionTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROChrActionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROChrActionTool();
        return instanceObject;
    }

    public MROChrActionTool() {
    }

    /**
     * 1����������
     * 2���޸���ҳ mro_record���MRO_CHAT_FLG�ֶ�
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection conn) {
        TParm chatParm = new TParm();
        chatParm.setData("CASE_NO",parm.getValue("CASE_NO"));
        chatParm.setData("MRO_CHAT_FLG","1");//�޸����״̬�ֶ�Ϊ1  �����
        TParm result = new TParm();
        result = MROChrtvetrecTool.getInstance().insertdata(parm,conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = MRORecordTool.getInstance().updateMRO_CHAT_FLG(chatParm,conn);
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
    public TParm updatedata(TParm parm,TConnection conn) {
        TParm result = MROChrtvetrecTool.getInstance().updatedata(parm,conn);
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
     * @param parm TParm  �����������  CASE_NO��EXAMINE_DATE��EXAMINE_CODE
     * @return TParm
     */
    public TParm deletedata(TParm parm,TConnection conn){
        TParm result = MROChrtvetrecTool.getInstance().deletedata(parm,conn);
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
