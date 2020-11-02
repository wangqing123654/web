package jdo.dss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: ��Ч��������</p>
 *
 * <p>Description: ��Ч��������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DSSEvalPlanTool extends TJDOTool{

    /**
     * ʵ��
     */
    public static DSSEvalPlanTool instanceObject;

    /**
     * �õ�ʵ��
     * @return DSSSQLTool
     */
    public static DSSEvalPlanTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DSSEvalPlanTool();
        return instanceObject;
    }

    /**
     * ���캯��
     */
    public DSSEvalPlanTool() {
        setModuleName("dss\\DSSPlanModule.x");
        onInit();
    }

    /**
     * ��Ч��������������Ϣ
     * @return TParm
     */
    public TParm queryDSSEvalPlanM() {
        TParm parm = new TParm();
        TParm result = query("queryDSSEvalPlanM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ��Ч��������ϸ����Ϣ
     * @return TParm
     */
    public TParm queryDSSEvalPlanD() {
        TParm parm = new TParm();
        TParm result = query("queryDSSEvalPlanD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ͨ�����뼨Ч��������ϸ����Ϣ
     * @return TParm
     */
    public TParm queryDSSEvalPlanDByCode(String planCode) {
        TParm parm = new TParm();
        parm.setData("PLAN_CODE", planCode);
        TParm result = query("queryDSSEvalPlanDByCode", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ɾ������������Ϣ
     * @param planCode String
     * @return TParm
     */
    public TParm deletePlanM(String planCode){
        TParm parm = new TParm();
        parm.setData("PLAN_CODE", planCode);
        TParm result = update("deleteDSSEvalPlanM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ɾ��������ϸ����Ϣ
     * @param planCode String
     * @return TParm
     */
    public TParm deletePlanD(String planCode){
        TParm parm = new TParm();
        System.out.println("planCode = " + planCode);
        parm.setData("PLAN_CODE", planCode);
        TParm result = update("deleteDSSEvalPlanD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ȡ���������˳���
     * @return TParm
     */
    public int getDSSEvalMaxMSeq() {
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
    public int getDSSEvalMaxDSeq() {
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
     * �õ�KPIֵ
     * @param KPICode String
     * @return TParm
     */
    public TParm getKPI(String KPICode){
        TParm parm = new TParm();
        parm.setData("KPI_CODE",KPICode);
        TParm result = query("getKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
}
