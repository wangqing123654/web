package action.bil;

import jdo.bil.BILArrearsStatisticsTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 门急诊欠费 Action</p>
 *
 * <p>Description: 门急诊欠费Action</p>
 *
 * <p>Copyright: Copyright (c) 20170509</p>
 *
 * <p>Company: javahis </p>
 *
 * @author zhanlgei
 * @version 5.0
 */
public class BILArrearsStatisticssAction extends TAction{

    /**
     * 新建 
     * @param parm TParm
     * @return TParm
     */
    public TParm onNew(TParm parm) {
//    	System.out.println("action");
    	//System.out.println("进入Action中的onNew");
    	//System.out.println("ActionParm:" + parm.getValue("EXPOSURE_NO"));
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = BILArrearsStatisticsTool.getInstance().onNew(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    

    /**
     * 修改
     * @param parm TParm
     * @return TParm
     */
//    public TParm onUpDate(TParm parm) {
    	//System.out.println("进入Action中的onUpDate:" + parm);
//        TParm result = new TParm();
//        if (parm == null) {
//            result.setErr( -1, "参数不能为空！");
//            return result;
//        }
//        TConnection connection = getConnection();
//        result = INFExposureTool.getInstance().onUpDate(parm, connection);
//        if (result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
 
}
