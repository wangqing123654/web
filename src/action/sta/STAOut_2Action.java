package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_2Tool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ����ͳ�Ʊ���	-- ��ͳ5��1</p>
 *
 * <p>Description: ����ͳ�Ʊ���	-- ��ͳ5��1</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-12
 * @version 1.0
 */
public class STAOut_2Action
    extends TAction {
    public STAOut_2Action() {
    }
    /**
     * ����STA_OUT_02�������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_02(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"��������Ϊ��");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_2Tool.getInstance().insertData(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
