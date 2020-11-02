package action.clp;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.Operator;
import jdo.clp.ClpVarMoncatTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
/**
 * <p>Title:�����ֵ� </p>
 *
 * <p>Description: �����ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110507
 * @version 1.0
 */
public class ClpVarMoncatAction extends TAction{
    public ClpVarMoncatAction() {
    }
    /**
     * �޸�����ӱ���
     * @param varianceParm TParm
     */
    public TParm saveVariance(TParm varianceParm) {
        TConnection conn = getConnection();
        TParm resultVariance = new TParm();
        for (int i = 0; i < varianceParm.getCount("MONCAT_CODE"); i++) {
            TParm parmValue = new TParm();
            //��ñ���е�һ������ִ���޸���ӷ���
            parmValue.setRowData( -1, varianceParm, i);
            //FLG=N ִ���޸ķ��� FLG=Y ִ����ӷ���
            if (varianceParm.getValue("FLG", i).equals("N")) {
                resultVariance = ClpVarMoncatTool.getInstance().
                                 updateClpVariance(parmValue, conn);
            } else {
                //û���ӱ���������ִ����ӷ���
                if (null == varianceParm.getValue("VARIANCE_CODE", i) ||
                    varianceParm.getValue("VARIANCE_CODE", i).equals(""))
                    break;
                //��ӷ���
                resultVariance = ClpVarMoncatTool.getInstance().saveClpVariance(
                        parmValue, conn);
            }

        }
        if (resultVariance == null) {
            //�ع�
            conn.rollback();
            conn.close();
            return null;
        }
        //�ύ
        conn.commit();
        conn.close();
        return resultVariance;
    }

}
