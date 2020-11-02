package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvPurorderMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvPurorderMTool instanceObject;

    /**
     * ������
     */
    public InvPurorderMTool() {
        setModuleName("inv\\INVPurorderMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static InvPurorderMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPurorderMTool();
        return instanceObject;
    }

    /**
     * ��ѯ����������
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("queryPurOrderM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������������
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createPurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¶�������
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���¶�������
     *
     * @param parm
     * @return
     */
    public TParm onUpdateCheck(TParm parm, TConnection conn) {
        TParm result = this.update("updateCheckFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ����������
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deletePurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
    * ��ѯδ��ɶ�������Ϣ
    *
    * @param parm
    * @return
    */
   public TParm onQueryUnDone(TParm parm) {
       TParm result = this.query("queryUnDonePurOrder", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

   /**
    * ���¶���������״̬
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    */
   public TParm onUpdateFinalFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateFinalFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
   }
}
