package action.opd;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.odo.OPDAbnormalRegTool;
import jdo.ekt.EKTTool;

/**
 * <p>Title: �ǳ�̬����Action</p>
 *
 * <p>Description: �ǳ�̬����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-10-27
 * @version 1.0
 */
public class OPDAbnormalRegAction
    extends TAction {
    /**
     * �ǳ�̬���ﱣ�涯��
     * @param parm TParm
     * @return TParm
     */
    public TParm saveReg(TParm parm){
        TConnection conn = this.getConnection();
        TParm result = OPDAbnormalRegTool.getInstance().saveReg(parm,conn);
        if(result.getErrCode()!=0){
            conn.close();
        }
        conn.commit();
        conn.close();
        return result;
    }
}
