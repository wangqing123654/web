package action.ind;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.ind.INDTool;

/**
 * <p>
 * Title: �ս�/�½ᱨ��
 * </p>
 *
 * <p>
 * Description: �ս�/�½ᱨ��
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
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class INDMonthAction
    extends TAction {
    public INDMonthAction() {
    }

    /**
     * ���½��ѯ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryDay(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryDay(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
