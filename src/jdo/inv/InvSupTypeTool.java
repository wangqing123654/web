package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ��Ӧ���</p>
 *
 * <p>Description: ��Ӧ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvSupTypeTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    public static InvSupTypeTool instanceObject;

    /**
     * ������
     */
    public InvSupTypeTool() {
        setModuleName("inv\\INVSupTypeModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvSupTypeTool
     */
    public static InvSupTypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupTypeTool();
        return instanceObject;
    }


    /**
     * ��ѯ
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
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
     *
     * @param parm
     * @return
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
     *
     * @param parm
     * @param conn
     * @return
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
     *
     * @param parm
     * @param conn
     * @return
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
