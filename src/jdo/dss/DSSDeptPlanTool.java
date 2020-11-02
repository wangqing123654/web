package jdo.dss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DSSDeptPlanTool extends TJDOTool{

    /**
     * ʵ��
     */
    public static DSSDeptPlanTool instanceObject;

    /**
     * �õ�ʵ��
     * @return DSSSQLTool
     */
    public static DSSDeptPlanTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DSSDeptPlanTool();
        return instanceObject;
    }

    /**
     * ���캯��
     */
    public DSSDeptPlanTool() {
        setModuleName("dss\\DSSDeptModule.x");
        onInit();
    }
    /**
     * ����������ϸ��
     * @return TParm
     */
    public TParm queryDSSDeptPlanD() {
        TParm parm = new TParm();
        TParm result = query("queryDSSDeptPlanD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ����������ϸ��
     * @return TParm
     */
    public TParm queryDSSDeptPlanM() {
        TParm parm = new TParm();
        TParm result = query("queryDSSDeptPlanD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ����������ϸ��
     * @return TParm
     */
    public TParm queryDSSDeptCodeLength() {
        TParm parm = new TParm();
        TParm result = query("queryDSSDeptCodeLength", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ���������Ϣ
     * @return TParm
     */
    public TParm analyzeDept(){
        TParm parm = new TParm();
        TParm codeD = queryDSSDeptPlanDInf();
        TParm codeLength = queryDSSDeptCodeLength();
        for(int i = 0;i<codeLength.getCount();i++){
            TParm parmI = new TParm();
            for(int j = 0;j<codeD.getCount();j++){
                if(codeLength.getInt("CODE_LENGTH",i)==codeD.getValue("DEPT_CODE",j).length()){
                    cloneTParm(codeD,parmI,j);
                }
            }
            parm.addData("CODE_GROUP",parmI);
        }
        return parm;
    }


    /**
     * ����������ϸ��
     * @return TParm
     */
    public TParm queryDSSDeptPlanDInf() {
        TParm parm = new TParm();
        TParm result = query("queryDSSDeptPlanDInf", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ����TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from,TParm to,int row){
        for(int i = 0;i < from.getNames().length;i++){
            to.addData(from.getNames()[i],from.getValue(from.getNames()[i],row));
        }
    }

    /**
     * �õ����Ҽ�Ч��ϸ��Ϣ
     * @param planCode String
     * @return TParm
     */
    public TParm queryEvalPlan(String planCode){
        TParm parm = new TParm();
        parm.setData("PLAN_CODE",planCode);
        TParm result = query("queryEvalPlan", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * �õ�������Ϣ
     * @param deptCode String
     * @return TParm
     */
    public TParm queryDeptInf(String deptCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        TParm result = query("queryDeptInf", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * �õ�������Ϣ
     * @param deptCode String
     * @return TParm
     */
    public TParm queryDeptInfByDept(String deptCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        TParm result = query("queryDeptInfByDept", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * �õ�չ��������Ϣ
     * @param deptCode String
     * @param planCode String
     * @return TParm
     */
    public TParm getSaveDate(String deptCode, String planCode){
        TParm parmEval = queryEvalPlan(planCode);
        TParm parmDept = queryDeptInf(deptCode);
        TParm saveDate = new TParm();
        for(int i = 0;i<parmDept.getCount();i++){
            for(int j = 0;j<parmEval.getCount();j++){
              cloneTParm(parmDept,saveDate,i);
              cloneTParm(parmEval,saveDate,j);
            }
        }
        return saveDate;
    }

    /**
     * ����չ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertIntoDSSDeptEvald(TParm parm, TConnection connection){
        TParm result = new TParm();
        for(int i = 0;i<parm.getCount("DEPT_CODE");i++){
            TParm parmI = new TParm();
            cloneTParmSet(parm,parmI,i);
            result = update("deleteDeptEval", parmI,connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        for(int i = 0;i<parm.getCount("DEPT_CODE");i++){
            TParm parmI = new TParm();
            cloneTParmSet(parm,parmI,i);
            parmI.setData("WEIGHT",Double.parseDouble(parmI.getValue("WEIGHT")));
            result = update("deleteDeptEvalM", parmI,connection);
            if (result.getErrCode() < 0)
                return result;
            result = update("insertDeptEval", parmI,connection);
            if (result.getErrCode() < 0)
                return result;
            result = update("insertDeptEvalM", parmI,connection);
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }

    /**
     * ɾ�����Ҽ�Ч��Ϣ
     * @param deptCode String
     * @param planCode String
     * @return TParm
     */
    public TParm deleteDSSDeptEvald(String deptCode,String planCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("PLAN_CODE",planCode);
        TParm result = update("deleteDeptEval", parm);
        result = update("deleteDeptEvalM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }


    public void cloneTParmSet(TParm from,TParm to,int row){
        for(int i = 0;i < from.getNames().length;i++){
            to.setData(from.getNames()[i],from.getValue(from.getNames()[i],row));
        }
    }

    /**
     * ȡ���������˳���
     * @return TParm
     */
    public int getDSSDeptMaxMSeq() {
        TParm parm = new TParm();
        TParm result = query("getSeqM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return 0;
        }
        if(result.getCount("SEQ") <= 0)
            return 0;
        if(result.getValue("SEQ",0).length() == 0)
            return 0;
        return result.getInt("SEQ",0);
    }

    /**
     * ȡ��ϸ�����˳���
     * @return TParm
     */
    public int getDSSDeptMaxDSeq() {
        TParm parm = new TParm();
        TParm result = query("getSeqD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return 0;
        }
        if(result.getCount("SEQ") <= 0)
            return 0;
        if(result.getValue("SEQ",0).length() == 0)
            return 0;
        return result.getInt("SEQ",0);
    }

    /**
     * ��ѯKPI�����Ϣ
     * @param planCode String
     * @param KPICode String
     * @return TParm
     */
    public TParm getKPIInfo(String planCode,String KPICode){
      TParm parm = new TParm();
      parm.setData("PLAN_CODE",planCode);
      parm.setData("KPI_CODE",KPICode);
      TParm result = query("getKPIInfo", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return null;
      }
        return result;
  }

  /**
   * ͨ��PLANȡ��KPI
   * @param planCode String
   * @return TParm
   */
  public TParm getKPIInfoByPlan(String planCode){
      TParm parm = new TParm();
      parm.setData("PLAN_CODE",planCode);
      TParm result = query("getKPIInfoByPlan", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return null;
      }
      return result;
    }
}
