package action.inf;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.action.TAction;
import jdo.inf.INFExamTool;
import jdo.inf.INFCaseTool;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>Title: ��Ⱦ��������Action</p>
 *
 * <p>Description: ��Ⱦ��������Action</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class InfAction extends TAction{

    /**
     * д���ؼ�¼��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertINFExamRecord(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        for(int i = 0;i<parm.getCount("EXAM_NO");i++){
            result = INFExamTool.getInstance().insertINFDeptExamM(parm.getRow(i),
                                                                  connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���¼�ؼ�¼��
     * @param parm TParm
     * @return TParm
     */
    public TParm updateINFExamRecord(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        for(int i = 0;i<parm.getCount("EXAM_NO");i++){
            result = INFExamTool.getInstance().updateINFExamRecord(parm.getRow(i),
                                                                  connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ɾ����ؼ�¼��
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteINFExamRecord(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        for(int i = 0;i<parm.getCount("EXAM_NO");i++){
            result = INFExamTool.getInstance().deleteINFExamRecord(parm.getRow(i),
                                                                  connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ����пصǼ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInfCase(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TParm infCaseParm = parm.getParm("INF_CASE");
        TParm infReasonParm = parm.getParm("INF_REASON");
        TParm infAntibioTest = parm.getParm("INF_ANTIBIOTEST");
        TParm infICDPart = parm.getParm("INF_ICDPART");//add by wanglong 20140217
        TParm inIO = parm.getParm("INF_IO");
        TConnection connection = getConnection();
        result = INFCaseTool.getInstance().updateMROINFDiag(infCaseParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        if(infCaseParm.getValue("INF_NO").length() == 0){
            String infNo = INFCaseTool.getInstance().getInfNo();
            infCaseParm.setData("INF_NO",infNo);
            result = INFCaseTool.getInstance().insertInfCase(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infReasonParm.getCount("INFREASON_CODE");i++){
                TParm infReasonParmI = infReasonParm.getRow(i);
                infReasonParmI.setData("INF_NO",infNo);
                result = INFCaseTool.getInstance().insertInfInfreasrcd(infReasonParmI, connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            for(int i = 0;i < infAntibioTest.getCount("CULURE_CODE");i++){
              TParm infAntibioTestI = infAntibioTest.getRow(i);
              infAntibioTestI.setData("INF_NO",infNo);
              result = INFCaseTool.getInstance().insertInfantibiotest(infAntibioTestI, connection);
              if (result.getErrCode() < 0) {
                  connection.rollback();
                  connection.close();
                  return result;
              }
            }
            for (int i = 0; i < infICDPart.getCount("PART_CODE"); i++) {//add by wanglong 20140217
                TParm infICDPartI = infICDPart.getRow(i);
                infICDPartI.setData("INF_NO", infNo);
                result = INFCaseTool.getInstance().insertInfICDPart(infICDPartI, connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            for (int i = 0; i < inIO.getCount("IO_CODE"); i++) {//add by wanglong 20140217
                TParm inIOI = inIO.getRow(i);
                inIOI.setData("INF_NO", infNo);
                result = INFCaseTool.getInstance().insertInfIO(inIOI, connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
        }
        else{
            result = INFCaseTool.getInstance().updateInfCase(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            result = INFCaseTool.getInstance().deleteInfInfreasrcd(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infReasonParm.getCount("INFREASON_CODE");i++){
                result = INFCaseTool.getInstance().insertInfInfreasrcd(infReasonParm.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            result = INFCaseTool.getInstance().deleteInfantibiotest(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infAntibioTest.getCount("CULURE_CODE");i++){
                result = INFCaseTool.getInstance().insertInfantibiotest(infAntibioTest.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            result = INFCaseTool.getInstance().deleteInfICDPart(infCaseParm, connection);//add by wanglong 20140217
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infICDPart.getCount("PART_CODE");i++){
                result = INFCaseTool.getInstance().insertInfICDPart(infICDPart.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            result = INFCaseTool.getInstance().deleteInfIO(infCaseParm, connection);//add by wanglong 20140217
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < inIO.getCount("IO_CODE");i++){
                result = INFCaseTool.getInstance().insertInfIO(inIO.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ����пؿ�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInfAntibiotrcd(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        if(parm.getCount("ORDER_CODE") <= 0)
            return result;
        TConnection connection = getConnection();
        //�������¿����ؼ�¼
        Map map = new HashMap();
        for(int i = 0;i < parm.getCount("ORDER_CODE");i++){
            String caseNo = parm.getValue("CASE_NO",i);
            if(map.get(caseNo) == null){
                //ȡ�����п����
                result = INFCaseTool.getInstance().selectMaxInfNo(parm.getRow(i));
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
                String infNo = "";
                if (result.getCount() > 0 &&
                    result.getData("INF_NO", 0) != null &&
                    result.getValue("INF_NO", 0).length() != 0)
                    infNo = result.getValue("INF_NO", 0);
                //ɾ��ԭ�п����ؼ�¼
                result = INFCaseTool.getInstance().deleteAntibiotrcd(parm.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
                map.put(caseNo,infNo);
            }
            TParm parmI = parm.getRow(i);
            parmI.setData("INF_NO",map.get(caseNo));
            result = INFCaseTool.getInstance().insertAntibiotrcd(parmI, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���¸п��ϱ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCaseReport(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = INFCaseTool.getInstance().updateInfCaseReport(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���¸пؼ�¼����
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInfCase(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = INFCaseTool.getInstance().deleteInfInfreasrcd(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INFCaseTool.getInstance().deleteInfantibiotest(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INFCaseTool.getInstance().deleteInfICDPart(parm, connection);//add by wanglong 20140217
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INFCaseTool.getInstance().deleteInfIO(parm, connection);//add by wanglong 20140217
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INFCaseTool.getInstance().updateInfCaseCancelFlg(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * ����
     * 
     * @param parm
     * @return
     */
    public TParm onSave(TParm parm) {// add by wanglong 20140224
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("��������");
            return result;
        }
        // ȡ������
        TConnection conn = getConnection();
        result = INFCaseTool.getInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    /**
     * ���� �п��м䵵 ����
     * @param parm TParm  �������������֣�"SQL"��ʾSQL������  ��DEPT����ʾ�����б�
     * @return TParm
     */
    public TParm insertDaily_Rec(TParm parm) {// add by wanglong 20140304
        TParm result = new TParm();
        TConnection conn = this.getConnection();
        if (parm == null) {
            result.setErr(-1, "����Ϊ�գ�");
            return result;
        }
        result = INFCaseTool.getInstance().insertDailyRecData(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
}
