package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �����������ݿ������</p>
 *
 * <p>Description: �����������ݿ������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLStanddTool extends TJDOTool {
    public CLPEVLStanddTool() {
        setModuleName("clp\\CLPEVLStanddModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    public static CLPEVLStanddTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static CLPEVLStanddTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPEVLStanddTool();
        return instanceObject;
    }

    /**
     * ɾ������
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteData(TParm parm, TConnection connection) {
//        System.out.println("ɾ������ִ��--------------------------");
        TParm result = this.update("deleteData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm insertData(TParm parm, TConnection connection) {
//        System.out.println("���뷽��ִ��--------------------------");
        TParm result = this.update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm selectData(TParm parm) {
//        System.out.println("��ѯ����ִ��--------------------------");
        TParm result = this.query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


}
