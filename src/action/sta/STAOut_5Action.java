package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sta.STAOut_5Tool;

/**
 * <p>Title: ��������ҽԺ���ֲ���סԺҽ�Ʒ����걨����ͳ5��3��</p>
 *
 * <p>Description: ��������ҽԺ���ֲ���סԺҽ�Ʒ����걨����ͳ5��3��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_5Action
    extends TAction {
    public STAOut_5Action() {
    }
    /**
     * ����STA_OUT_05�������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_05(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"��������Ϊ��");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_5Tool.getInstance().insertData(parm,conn);
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
