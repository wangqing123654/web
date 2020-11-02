package action.emr;

import jdo.emr.EMRClassTool;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���Ӳ��������� </p>
 *
 * <p>Description: ���Ӳ��������� </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author wanglong 2014.04.30
 * @version 1.0
 */
public class EMRAction extends TAction {
    
    /**
     * ����
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("��������");
            return result;
        }
        TConnection conn = getConnection(); // ȡ������
        result = EMRClassTool.getNewInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
