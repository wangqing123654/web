package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_1Tool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��</p>
 *
 * <p>Description: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-12
 * @version 1.0
 */
public class STAOut_1Action
    extends TAction {
    public STAOut_1Action() {
    }
    /**
     * �޸�STA_OUT_01�е�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_OUT_01(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"����Ϊ��");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_1Tool.getInstance().updateSTA_OUT_01(parm,conn);
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
     * ����STA_OUT_01������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_01(TParm parm){
        TParm result = new TParm();
        if(parm==null){
            result.setErr(-1,"����Ϊ��");
            return result;
        }
        TConnection conn = this.getConnection();
        result = STAOut_1Tool.getInstance().insertData(parm,conn);
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
