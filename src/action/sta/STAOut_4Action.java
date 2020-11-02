package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_4Tool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���������ؼ�������ҽԺ���Ѽ���֧���������ͳ2��3��</p>
 *
 * <p>Description: ���������ؼ�������ҽԺ���Ѽ���֧���������ͳ2��3��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-16
 * @version 1.0
 */
public class STAOut_4Action
    extends TAction {
    public STAOut_4Action() {
    }
    /**
     * ����STA_OUT_04����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_04(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"��������Ϊ��");
        }
        TConnection conn = this.getConnection();
        result = STAOut_4Tool.getInstance().insertData(parm,conn);
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
    /**
     * �޸�STA_OUT_04�е�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_OUT_04(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"����Ϊ��");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_4Tool.getInstance().updateSTA_OUT_04(parm,conn);
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
