package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: סԺ���Tool</p>
 *
 * <p>Description: סԺ���Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-9-9
 * @version 4.0
 */
public class ADMDiagTool
    extends TJDOTool {
    /**
    * ʵ��
    */
   public static ADMDiagTool instanceObject;

   /**
    * �õ�ʵ��
    * @return SYSRegionTool
    */
   public static ADMDiagTool getInstance() {
       if (instanceObject == null)
           instanceObject = new ADMDiagTool();
       return instanceObject;
   }

    public ADMDiagTool() {
        setModuleName("adm\\ADMDiagModule.x");
        onInit();
    }
    /**
     * ����CASE_NO��ѯĳһ���������������Ϣ��������ҳʹ�ã�
     * @param parm TParm ���������CASE_NO
     * @return TParm
     */
    public TParm queryDiagForMro(TParm parm){
        TParm result = this.query("queryDiagForMro", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ���ȫ�ֶ�
     * @param parm TParm
     * @return TParm
     */
    public TParm queryData(TParm parm){
        TParm result = this.query("queryData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDiag(TParm parm,TConnection conn){
        TParm result = this.update("insertDiag",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
