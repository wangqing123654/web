package jdo.ibs;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: סԺ���õ�������</p>
*
* <p>Description: סԺ���õ�������</p>
*
* <p>Copyright: Copyright (c)bluecore</p>
*
* <p>Company: bluecore</p>
*
* @author caowl
* @version 1.0
*/
public class IBSOrdmTool extends TJDOTool{
	  /**
     * ʵ��
     */
    public static IBSOrdmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return IBSBilldTool
     */
    public static IBSOrdmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSOrdmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IBSOrdmTool() {
        setModuleName("ibs\\IBSOrdmCTZModule.x");
        onInit();
    }
    
    /**
     * ������������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdataM(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdata",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm insertdataMOne(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdataOne",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ������������ ���BILL_EXE_FLG
     * pangben 2016-7-25
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdataLumpworkDExe(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertdataLumpworkDExe",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdataD(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertDdata",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 
    * @Title: insertdataLumpworkD
    * @Description: TODO(�����ײ���Ӳ���IBS_ORDD����)
    * @author pangben
    * @param parm
    * @param connection
    * @return
    * @throws
     */
    public TParm insertdataLumpworkD(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertdataLumpworkD",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����������ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIbsOrddData(TParm parm,TConnection connection) {
        TParm result = new TParm();
        result = update("insertIbsorddData",parm, connection);
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}


