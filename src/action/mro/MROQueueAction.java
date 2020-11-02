package action.mro;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MROQueueTool;

/**
 * <p>Title: �������Ĺ���</p>
 *
 * <p>Description: �������Ĺ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2009-5-13
 * @version 1.0
 */
public class MROQueueAction
    extends TAction {
    public MROQueueAction() {
    }
    /**
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOUT(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = MROQueueTool.getInstance().updateOUT(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * �������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateIN(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = MROQueueTool.getInstance().updateOUT(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ������� (��Ժ�����鵵)
     * @param parm TParm
     * @return TParm
     */
    public TParm updateIN_FLG(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = MROQueueTool.getInstance().updateIn_flg(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * ���������
     * @param parm TParm
     * @return TParm
     * @author wangbin
     */
    public TParm updateMroOutIn(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        
        TConnection connection = getConnection();
        // ִ�в�������
        result = MROQueueTool.getInstance().updateMroOutIn(parm,connection);
        
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.rollback();
            connection.close();
            return result;
        }
        
        connection.commit();
        connection.close();
        return result;
    }
    
}
