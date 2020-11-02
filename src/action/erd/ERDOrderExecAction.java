package action.erd;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.erd.ErdOrderExecTool;

/**
 * <p>Title: �������ۻ�ʿִ��Action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-11-12
 * @version 1.0
 */
public class ERDOrderExecAction
    extends TAction {
    public ERDOrderExecAction() {
    }

    /**
     * ִ�����
     * @param parm
     * @return TParm
     */
    public TParm onSave(TParm parm) {

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();

        //���ó��ڴ��õ�չ������
        result = ErdOrderExecTool.getInstance().onExec(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ȡ��ִ�����
     * @param parm
     * @return TParm
     */
    public TParm onUndoSave(TParm parm) {

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();

        //���ó��ڴ��õ�չ������
        result = ErdOrderExecTool.getInstance().onUndoExec(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }

}
