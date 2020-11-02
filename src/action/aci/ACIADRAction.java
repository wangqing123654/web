package action.aci;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.aci.ACIADRTool;

/**
 * <p>Title: ҩƷ�����¼��������� </p>
 * 
 * <p>Description: ҩƷ�����¼��������� </p>
 * 
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: BLueCore </p>
 *
 * @author wanglong 2013.09.30
 * @version 1.0
 */
public class ACIADRAction extends TAction {

    /**
     * ����
     * 
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
        // ȡ������
        TConnection conn = getConnection();
        result = ACIADRTool.getInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ɾ��
     * 
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm) {
        TParm result = new TParm();
        TConnection conn = this.getConnection();
        result = ACIADRTool.getInstance().onDeleteADRMData(parm, conn);
        if (result.getErrCode() < 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        result = ACIADRTool.getInstance().onDeleteADRDData(parm, conn);
        if (result.getErrCode() < 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
