package action.sys;

import com.dongyang.patch.Patch;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.data.TParm;
import jdo.sys.SYSFeeReadjustBatchTool;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: SYS_FEE���ۼƻ�������ҵ
 * </p>
 *
 * <p>
 * Description: SYS_FEE���ۼƻ�������ҵ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.03
 * @version 1.0
 */
public class SYSFeeReadjustBatchPatch
    extends Patch {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public boolean run() {
        //System.out.println("----111---");
        TConnection conn = TDBPoolManager.getInstance().getConnection();
        // ��ȡ���γ������
        TParm parm = this.getParm();
        //System.out.println("----333---"+parm);
        TParm result = SYSFeeReadjustBatchTool.getInstance().
            onSYSFeeReadjustBatchAction(parm, conn);
        //System.out.println("----444---");
        if (result == null || result.getErrCode() < 0) {
            conn.close();
            return false;
        }
        // ����SYS_FEE���ļ�����
        //System.out.println("----222---");
        conn.commit();
        conn.close();
        TIOM_Database.logTableAction("SYS_FEE");
        return true;
    }

}
