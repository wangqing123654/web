package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSRolePopedomTool;

/**
 *
 * <p>Title: ��ɫȨ�޶�����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class SYSRolePopedomAction
    extends TAction {
    /**
     * ��ѯȨ����
     * @param parm TParm
     * @return TParm
     */
    public TParm queryRoleTree(TParm parm)
    {
        String roleCode = parm.getValue("ROLE_CODE");
        return SYSRolePopedomTool.getInstance().queryRoleTree(roleCode);
    }
    /**
     * ����
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSRolePopedomTool.getInstance().onSave(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
}
