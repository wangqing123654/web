package action.ind;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ind.INDTool;

/**
 * <p>
 * Title: �ս���ʹ���
 * </p>
 *
 * <p>
 * Description: �ս���ʹ���
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.02
 * @version 1.0
 */
public class INDStockBatchAction extends TAction  {

    public INDStockBatchAction() {
    }

    /**
     * ָ������ҩƷ����ֶ��ս�������ҵ
     * @param parm TParm
     * @return TParm
     */
    public TParm onIndStockBatch(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onIndStockBatchByOrgCode(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
