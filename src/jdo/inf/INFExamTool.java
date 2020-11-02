package jdo.inf;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �����¼��</p>
 *
 * <p>Description: �����¼��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFExamTool extends TJDOTool{

    /**
     * ������
     */
    public INFExamTool() {
        setModuleName("inf\\INFExamModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static INFExamTool instanceObject;

    /**
     * �õ�ʵ��
     * @return INFExamTool
     */
    public static INFExamTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new INFExamTool();
        return instanceObject;
    }

    /**
     * ���ݼ���׼�����ѯ����׼�Լ������Ŀ���չ�ϵ
     * @param examStandCode String
     * @return TParm
     */
    public TParm selectINFExamStand(String examStandCode){
        TParm parm = new TParm();
        parm.setData("EXAMSTAND_CODE",examStandCode);
        return query("selectINFExamStand",parm);
    }

    /**
     * ȡ����¼���
     * @return String
     */
    public String getExamNo(){
        return SystemTool.getInstance().getNo("ALL", "INF",
                "EXAM_NO", "EXAM_NO");
    }

    /**
     * ��ѯĳ����һ��������¼���
     * @param deptCode String
     * @param examPeriod String
     * @return String
     */
    public String getDeptExamPeriodMaxSeq(String deptCode,String examPeriod){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("EXAM_PERIOD",examPeriod);
        parm = query("getDeptExamPeriodMaxSeq",parm);
        if(parm.getErrCode() < 0)
            return "";
        if(parm.getCount() <= 0)
            return "01";
        if(parm.getValue("EXAM_PERIOD",0) == null ||
           parm.getValue("EXAM_PERIOD",0).length() ==0)
            return "01";
        int seq = Integer.parseInt(parm.getValue("EXAM_PERIOD",0)) + 1;
        if(("" + seq).length() >= 2)
            return "" + seq;
        return "0" + seq;
    }

    /**
     * д���Ⱦ���Ƽ���¼��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertINFDeptExamM(TParm parm,TConnection connection){
        return update("insertINFDeptExamM",parm,connection);
    }

    /**
     * ȡ�ü���¼����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINFExamRecordD(TParm parm){
        return query("selectINFExamRecordD",parm);
    }

    /**
     * ȡ�ü���¼����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINFExamRecordM(TParm parm){
      return query("selectINFExamRecordM",parm);
    }

    /**
     * ���¸�Ⱦ���Ƽ���¼��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateINFExamRecord(TParm parm,TConnection connection){
        return update("updateINFExamRecord",parm,connection);
    }

    /**
     * ɾ����Ⱦ���Ƽ���¼��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteINFExamRecord(TParm parm,TConnection connection){
        return update("deleteINFExamRecord",parm,connection);
    }
}
