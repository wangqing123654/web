package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ���������ϸ</p>
 *
 * <p>Description: ���������ϸ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.27
 * @version 1.0
 */
public class InvSuptitemDetailTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvSuptitemDetailTool instanceObject;

    /**
     * ������
     */
    public InvSuptitemDetailTool() {
        setModuleName("inv\\INVSuptitemDetailModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvSuptitemDetailTool
     */
    public static InvSuptitemDetailTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSuptitemDetailTool();
        return instanceObject;
    }

    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
       TParm result = this.update("update", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }

   /**
     * ɾ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
       TParm result = this.update("delete", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }


}
