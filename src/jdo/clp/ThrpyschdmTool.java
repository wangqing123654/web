package jdo.clp;

import java.text.*;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���ݿ����������</p>
 *
 * <p>Description: ����ʱ��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ThrpyschdmTool
    extends TJDOTool {
    /**
     * ���췽��˽�л�������SQL��������ļ�
     */
    private ThrpyschdmTool() {
        this.setModuleName("clp\\CLPBscInfoModule.x");
        onInit();
    }

    /**
     * ������̬���ݿ⹤����ʵ��
     */
    private static ThrpyschdmTool instance = null;
    /**
     * �������ڸ�ʽ�������
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * ������̬���������ش����ʵ��
     * @return BscInfoTool
     */
    public static ThrpyschdmTool getInstance() {
        if (instance == null) {
            instance = new ThrpyschdmTool();
        }
        return instance;
    }

    /**
     * ����ʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm getThrpyschdmList(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(
            " SELECT CD.DURATION_CHN_DESC AS SCHD_DESC, CT.CLNCPATH_CODE,CT.SCHD_CODE,CT.SCHD_DAY,CT.SUSTAINED_DAYS,CT.SEQ,CT.REGION_CODE,CT.SCHD_AMT FROM CLP_THRPYSCHDM CT, CLP_DURATION CD  WHERE 1=1 ");
        sqlBuf.append(" AND CT.SCHD_CODE = CD.DURATION_CODE");
        sqlBuf.append(" AND CT.REGION_CODE = CD.REGION_CODE");
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND CT.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") +
                          "'");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND CT.CLNCPATH_CODE = '" +
                          parm.getValue("CLNCPATH_CODE") + "'");
        }
        sqlBuf.append(" ORDER BY CT.SEQ ");
        //System.out.println("����ʱ�̲�ѯ���:"+sqlBuf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ȷ��ѯ����ʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm getThrpyschdmObject(TParm parm) {
        TParm result = new TParm();
        result = query("queryThrpyschdm", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * update
     *
     * @param parm TParm
     */
    public TParm update(TParm parm) {
        TParm result = new TParm();
        result = this.update("updateThrpyschdm", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * insert
     *
     * @param parm TParm
     */
    public TParm insert(TParm parm) {
        TParm result = new TParm();
        result = this.update("insertThrpyschdm", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * delete
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("deleteThrpyschdm", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
