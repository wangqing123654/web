package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �ٴ�·��������Ŀά��</p>
 *
 * <p>Description:�ٴ�·��������Ŀά�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLStandmTool
    extends TJDOTool {
    public CLPEVLStandmTool() {
        setModuleName("clp\\CLPEVLStandmModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    public static CLPEVLStandmTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static CLPEVLStandmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPEVLStandmTool();
        return instanceObject;
    }


    /**
     * ��ѯ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
//        System.out.println("���ݲ�ѯ����");
        TParm result = this.query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = this.update("insertData", parm, conn);
//        System.out.println("������Ϣ");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

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
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateData(TParm parm, TConnection conn) {
        TParm result = this.update("updateData", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteData(TParm parm, TConnection connection) {
        TParm result = this.update("deleteData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �õ�������к�
     * @return TParm
     */
    public TParm getMaxSEQ() {
        TParm result = this.query("maxSEQQuery");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }


}
