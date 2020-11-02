package action.aci;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.aci.ACIADRTool;

/**
 * <p>Title: 药品不良事件事务处理类 </p>
 * 
 * <p>Description: 药品不良事件事务处理类 </p>
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
     * 保存
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("参数错误");
            return result;
        }
        // 取得链接
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
     * 删除
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
