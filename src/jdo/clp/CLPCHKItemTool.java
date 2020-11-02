package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ��ҽ�����ֵ�</p>
 *
 * <p>Description:��ҽ�����ֵ� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPCHKItemTool extends TJDOTool {
    public static CLPCHKItemTool instanceObject;
    public CLPCHKItemTool() {
        setModuleName("clp\\CLPCHKItemModule.x");
        onInit();
    }

    public static CLPCHKItemTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new CLPCHKItemTool();
        }
        return instanceObject;
    }

    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
        TParm result = new TParm();
        result = query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ������
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deleteData(TParm parm,TConnection conn) {
        TParm result = update("deleteData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ж������Ƿ����
     * @param parm TParm
     * @return TParm
     */
    public TParm checkDataExist(TParm parm) {
        TParm result = this.query("checkDataExist", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateData(TParm parm,TConnection conn) {
        TParm result = this.update("updateData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }
    public TParm getMaxSEQ(){
        TParm result = this.query("maxSEQQuery");
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }

    /**
     * ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm,TConnection conn) {
        TParm result = this.update("insertData", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }




}
