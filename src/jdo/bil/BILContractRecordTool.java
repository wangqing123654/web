package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:���˵�λ������� </p>
 *
 * <p>Description:���˵�λ������� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110817
 * @version 1.0
 */
public class BILContractRecordTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILContractRecordTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILAccountTool
     */
    public static BILContractRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILContractRecordTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILContractRecordTool() {
        setModuleName("bil\\BILContractRecondModule.x");
        onInit();
    }
    /**
     * ������ݷ���
     * @param parm TParm
     * @return TParm
     */
    public TParm insertRecode(TParm parm,TConnection connection) {
        TParm result = update("insertRecode", parm,connection);
        result=getisError(result);
        return result;

    }
    /**
     * ���ִ�����ʾ
     * @param result TParm
     * @return TParm
     */
    private TParm getisError(TParm result) {
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
        }
        return result;
    }
    /**
     *
     * ��ѯ����
     * @param result TParm
     * @return TParm
     */
    public TParm recodeQuery(TParm parm){
        TParm result = query("selRecode", parm);
        result=getisError(result);
        return result;
    }
    /**
     *  �޸ķ���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRecode(TParm parm,TConnection connection){
         TParm result = update("updateRecode", parm,connection);
         result=getisError(result);
         return result;
    }
    /**
    *
    * ��ѯ����
    * @param result TParm
    * @return TParm
    */
   public TParm regRecodeQuery(TParm parm){
       TParm result = query("selRegRecode", parm);
       result=getisError(result);
       return result;
   }

}
