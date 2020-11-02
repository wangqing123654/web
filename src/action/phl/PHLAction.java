package action.phl;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.phl.PhlRegisterTool;
import jdo.phl.PhlExecuteTool;
import jdo.phl.PhlBedTool;

/**
 * <p>
 * Title: �����ҹ���
 * </p>
 *
 * <p>
 * Description: �����ҹ���
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
public class PHLAction
    extends TAction {

    /**
     * �����ұ���
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlRegister(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlRegisterTool.getInstance().onPhlRegister(parm, conn);
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

    /**
     * ��������Ժ�崲
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlBedOut(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlBedTool.getInstance().onUpdateBed(parm, conn);
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

    /**
     * ������ִ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlExecute(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlExecuteTool.getInstance().onPhlExecute(parm, conn);
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
    /**
     * ������ִ��(�ż��ﻤʿվ)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlExecuteForOnw(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlExecuteTool.getInstance().onPhlExecuteForOnw(parm, conn);
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
    /**
     * ��Ӵ�λ
     * add caoyong 2014/4/17
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm PHLBedControl(TParm parm) {
    	TConnection conn = getConnection();
    	TParm result = new TParm();
    	result = PhlBedTool.getInstance().PHLBed(parm, conn);
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
