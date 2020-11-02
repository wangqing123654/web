package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ҽ������</p>
 *
 * <p>Description: ��Ƕʽҽ������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsPaymTool extends TJDOTool {
/**
 * ʵ��
 */
public static InsPaymTool instanceObject;
/**
 * �õ�ʵ��
 * @return RuleTool
 */
public static InsPaymTool getInstance() {
    if (instanceObject == null)
        instanceObject = new InsPaymTool();
    return instanceObject;
}

/**
 * ������
 */
public InsPaymTool() {
    setModuleName("ins\\INSPAYM.x");
    onInit();
}
/**
 * ��ѯ������������
 * @param parm TParm
 * @return TParm
 */
public TParm seletADM_INP(TParm parm){
    TParm result = new TParm();
    if(parm.getValue("ADM_STAT").equals("Y")){
        result = query("seletADM_INPIN", parm);
    }else{
        result = query("seletADM_INPDS", parm);
    }
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ��ѯ���Ժ���
 * @param parm TParm
 * @return TParm
 */
public TParm seletICDINDSAdm(TParm parm){
    TParm result = new TParm();
    result = query("seletICDINDSAdm",parm);
    if(result.getErrCode()<0){
        err("ERR:" + result.getErrCode() + result.getErrText() +
             result.getErrName());
         return result;
    }
    return result;
}
/**
 * ��ѯ��Ժ���
 * @param parm TParm
 * @return TParm
 */
public TParm selectICDDSAdm(TParm parm) {
    TParm result = new TParm();
    result = query("seletICDINDSAdmDS", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * �ż������
 * @param parm TParm
 * @return TParm
 */
public TParm selectICDDSOEAdm(TParm parm){
    TParm result = new TParm();
    result = query("seletICDINDSAdmDSOE", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ��ѯסԺ����
 * @param parm TParm
 * @return TParm
 */
public TParm seletAdmCount(TParm parm) {
    TParm result = new TParm();
    result = query("seletAdmCount", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ����IBS_CTZ
 * @param parm TParm
 * @return TParm
 */
public TParm updateIbsCtz(TParm parm) {
    TParm result = new TParm();
    result = update("updateIbsCtz", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ���²���������Ϣ
 * @param parm TParm
 * @return TParm
 */
public TParm updateAdmInp(TParm parm) {
    TParm result = new TParm();
    result = update("updateAdmInp", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ����IBS_CTZ
 * @param parm TParm
 * @return TParm
 */
public TParm updateIbsCtz(TParm parm,TConnection con) {
    TParm result = new TParm();
    result = update("updateIbsCtz", parm,con);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ���²���������Ϣ
 * @param parm TParm
 * @return TParm
 */
public TParm updateAdmInp(TParm parm,TConnection con) {
    TParm result = new TParm();
    result = update("updateAdmInp", parm,con);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ���²���������Ϣ
 * @param parm TParm
 * @return TParm
 */
public TParm updateIbsBillM(TParm parm,TConnection con) {
    TParm result = new TParm();
    result = update("updateIbsBillM", parm,con);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
public TParm updateMroFrontpgM(TParm parm,TConnection con){
    TParm result = new TParm();
   result = update("updateMroFrontpgM", parm,con);
   if (result.getErrCode() < 0) {
       err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
   }
   return result;
}

/**
* ���²���������
* @param parm TParm
* @param con TConnection
* @return TParm
*/
public TParm updatePatInfo(TParm parm, TConnection con) {
   TParm result = new TParm();
   result = update("updatePatInfo", parm, con);
   if (result.getErrCode() < 0) {
       err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
   }
   return result;
}
/**
 * ���²���������Ϣ
 * @param parm TParm
 * @return TParm
 */
public TParm updateIbsBillM(TParm parm) {
    TParm result = new TParm();
    result = update("updateIbsBillM", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ��ѯADM_INP���Ƿ����ظ��Ĳ�����
 * @param parm TParm
 * @return TParm
 */
public TParm queryAdmInpCaseNo(TParm parm){
    TParm result = new TParm();
    result = query("queryAdmInpCaseNo", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ��ѯҽ������������Ϣ
 * @param parm TParm
 * @return TParm
 */
public TParm queryInsPatInfo(TParm parm){
    TParm result = new TParm();
    result = query("query", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ɾ���ָ�����
 * @param parm TParm
 * @return TParm
 */
public TParm delInsOrder(TParm parm,TConnection con) {
    TParm result = new TParm();
    result = update("delInsOrder", parm,con);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * ɾ����������
 * @param parm TParm
 * @return TParm
 */
public TParm delInsPayD(TParm parm,TConnection con) {
    TParm result = new TParm();
    result = update("delInsPayD", parm,con);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;
}
/**
 * �޸����
 * @param parm TParm
 * @param con TConnection
 * @return TParm
 */
public TParm editCtzUser(TParm parm,TConnection con){
    TParm result = new TParm();
    result = this.updateAdmInp(parm,con);
    if(result.getErrCode()<0){
        err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
    }
    result = this.updateIbsCtz(parm,con);
    if(result.getErrCode()<0){
        err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
    }
    result = this.updateIbsBillM(parm,con);
    if(result.getErrCode()<0){
        err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
    }
    result = this.updateMroFrontpgM(parm,con);
    if(result.getErrCode()<0){
        err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
    }
//    result = updatePatInfo(parm,con);
//    if(result.getErrCode()<0){
//        err("ERR:" + result.getErrCode() + result.getErrText() +
//           result.getErrName());
//       return result;
//    }
    result = this.delInsOrder(parm,con);
    if(result.getErrCode()<0){
        err("ERR:" + result.getErrCode() + result.getErrText() +
           result.getErrName());
       return result;
    }
    result = this.delInsPayD(parm,con);
    if(result.getErrCode()<0){
       err("ERR:" + result.getErrCode() + result.getErrText() +
          result.getErrName());
      return result;
   }
   return result;
}
    /**
     * ִ������
     * @return TParm
     */
    public TParm batchSql(TParm parm){
        TParm result = new TParm();
        result = query("batchSql",parm);
        return result;
    }
}
