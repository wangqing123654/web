package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sta.STAWorkLogTool;

/**
 * <p>Title: ��������</p>
 *
 * <p>Description: ��������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-21
 * @version 1.0
 */
public class STAWorkLogAction
    extends TAction {
    public STAWorkLogAction() {
    }
    /**
     * ��������
     * @return TParm
     */
    public TParm insertData(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = STAWorkLogTool.getInstance().insertDaily(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //ֱ��ͨ�� �����ύ
        connection.commit();
        connection.close();
        return result;

    }
}
