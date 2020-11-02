package jdo.emr;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���Ӳ���������־Tool</p>
 *
 * <p>Description: ��־��ѯ������ɾ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class OptLogTool
    extends TJDOTool {
    public OptLogTool() {
        this.setModuleName("emr\\EMROptLogModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    public static OptLogTool instance;
    /**
     * ���ڸ�ʽ����
     */
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * �õ�ʵ��
     * @return IBSTool
     */
    public static OptLogTool getInstance() {
        if (instance == null) {
            instance = new OptLogTool();
        }
        return instance;
    }

    /**
     * CASE_NO �����
     * FILE_SEQ �ļ����
     * OPT_SEQ �������
     * OPT_TYPE �������� L:��½ C:�½� O:�� M:�޸� D:ɾ��
     * MR_NO ������
     * IPD_NO סԺ��
     * FILE_NAME �ļ�����
     * DEPT_CODE ����
     * STATION_CODE ����
     * BED_NO ����
     * OPT_USER �����û�
     * OPT_DATE ��������
     * OPT_TERM ����ʱ��
     * @return TParm
     */
    public TParm writeOptLog(TParm parm) {
        TParm result = new TParm();
        result = this.update("writeOptLog", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����������ҽʦ��ֵ��ҽʦ�����Ӳ���������־/EMR_OPTLOG/��½ �½� �� �޸� ɾ��
     * @param obj Object ������Ϣ
     * @param optType �������� L:��½ C:�½� O:�� M:�޸� D:ɾ��
     * @param emrParm FILE_SEQ:�ļ���� FILE_NAME:�ļ�����
     * @return TParm
     */
    public TParm writeOptLog(Object obj, String optType, TParm emrParm) {
        // �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
        if (obj == null && ! (obj instanceof TParm)) {
            return null;
        }
        // ����ת����TParm
        TParm parm = (TParm) obj;

        // �������
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(
            " SELECT NVL(MAX(OPT_SEQ) + 1, 0) AS OPT_SEQ FROM EMR_OPTLOG WHERE ");
        sqlBuf.append(" CASE_NO = '" + parm.getValue("CASE_NO") +
                      "' AND FILE_SEQ = '" + emrParm.getValue("FILE_SEQ") +
                      "'");
        TParm optSeqParm = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        sqlBuf.delete(0, sqlBuf.length());
        // ����
        sqlBuf.append(" SELECT BED_NO FROM ADM_INP AI WHERE CASE_NO = '");
        sqlBuf.append(parm.getValue("CASE_NO") + "'");
        TParm bedNoParm = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));

        // ��װʵ�ʲ���
        TParm realParm = new TParm();
        // CASE_NO �����
        realParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        // FILE_SEQ �ļ����
        realParm.setData("FILE_SEQ", emrParm.getValue("FILE_SEQ"));
        // OPT_SEQ �������
        realParm.setData("OPT_SEQ", optSeqParm.getInt("OPT_SEQ", 0));
        // OPT_TYPE �������� L:��½ C:�½� O:�� M:�޸� D:ɾ��
        realParm.setData("OPT_TYPE", optType);
        // MR_NO ������
        realParm.setData("MR_NO", parm.getValue("MR_NO"));
        // IPD_NO סԺ��
        realParm.setData("IPD_NO", parm.getValue("IPD_NO"));
        // FILE_NAME �ļ�����
        realParm.setData("FILE_NAME", emrParm.getValue("FILE_NAME"));
        // DEPT_CODE ����
        realParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
        // STATION_CODE ����
        realParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        // BED_NO ����
        realParm.setData("BED_NO", bedNoParm.getValue("BED_NO", 0));
        // OPT_USER �����û�
        realParm.setData("OPT_USER", Operator.getID());
        // OPT_DATE ��������
        realParm.setData("OPT_DATE",
                         dateFormat.format(SystemTool.getInstance().getDate()));
        // OPT_TERM ����ʱ��
        realParm.setData("OPT_TERM", Operator.getIP());
        // ��������
        realParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
        TParm result = this.writeOptLog(realParm);
        return result;
    }

    /**
     * ��־��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOptLog(TParm parm) {
        TParm result = new TParm();
        result = this.query("queryOptLog", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("deleteOptLog", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
}
