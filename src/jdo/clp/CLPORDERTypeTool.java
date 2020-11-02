package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description:�ٴ�·����Ŀҽ������ </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPORDERTypeTool extends TJDOTool {
    public CLPORDERTypeTool() {
        setModuleName("clp\\CLPORDERTypeModule.x");
        onInit();
    }
    private static CLPORDERTypeTool instanceObject;

    public static CLPORDERTypeTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new CLPORDERTypeTool();
        }
        return instanceObject;
    }

    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOrderCodeFromSysFeeData(TParm parm) {
        TParm result = new TParm();
        result = query("selectOrderCodeFromSysFeeData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ٴ�·����Ŀҽ������
     * @param parm TParm
     * @return TParm
     */
    public TParm saveORDERType(TParm parm,TConnection conn) {
        TParm result = new TParm();
        TParm delParm = new TParm();
        delParm.setData("ORDER_CODE",parm.getValue("ORDER_CODE"));
        delParm.setData("ORDER_FLG",parm.getValue("ORDER_FLG"));
        result = update("delORDERType", delParm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //ֻ���ٴ�·������ڵ����ݲſɽ��в��������������ṩɾ������
        if(!"".equals(parm.getValue("ORDER_CODE"))){
            result = update("saveORDERType", parm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    public TParm delORDERType(TParm parm,TConnection conn) {
        TParm result = update("delORDERType", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
