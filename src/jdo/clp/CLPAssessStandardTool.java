package jdo.clp;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �ٴ�·��������׼</p>
 *
 * <p>Description: �ٴ�·��������׼</p>
 *
 * <p>Copyright: Copyright (c) javahis 2011</p>
 *
 * <p>Company: �����к� </p>
 *
 * @author ZhenQin 2011-05-04
 * @version 4.0
 */
public class CLPAssessStandardTool
    extends TJDOTool {

    private static CLPAssessStandardTool jdo = null;

    /**
     * ����ͨ�����췽��ȡ�ö����ʵ��
     */
    private CLPAssessStandardTool() {
        super();
        setModuleName("clp\\CLPAssessStandardModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     * @return CLPCureScheduleStatusTool
     */
    public static CLPAssessStandardTool getInstance() {
        if (jdo == null){
            jdo = new CLPAssessStandardTool();
        }
        return jdo;
    }

    /**
     * ��ѯclpΪkey��model
     * @param clp model��key
     * @param param TParm ����
     * @return TParm
     */
    public TParm queryCLP_EVL_CAT(String clp, TParm param){
        TParm result = null;
        result = this.query(clp, param);
        if(result.getErrCode() < 0){
            err(result.getErrName() + result.getErrText());
        }
        return result;

    }

    /**
     * ��ѯ���ݿ��б�����ֵ,���������ܺ�,�����������������׼����code
     * @param clp model��key
     * @param param ����,�ڷ���ֵ��,
     * ������MAXCODE�ֶ�,ͨ��TParm.getValue("MAXCODE", 0)���Եõ���ֵ
     * @return
     */
    public TParm queryMaxCode(String clp, TParm param){
        TParm result = null;
        result = this.query(clp, param);
        if(result.getErrCode() < 0){
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

    /**
     * ����һ��״̬,��Ҫ�ṩREGION_CODE
     * @param clp model��key
     * @param param ����,�ڷ���ֵ��,
     * @return TParm
     */
    public TParm insertCLP_EVL_CAT(String clp, TParm param) {
        TParm result = null;
        result = this.update(clp, param);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }



    /**
     * ����״̬,����ʱ��Ҫ�ṩCAT1_CODE
     * @param param TParm
     * @return TParm ���ظ���״̬
     */
    public TParm updateCLP_EVL_CAT(String clp, TParm param) {
        TParm result = null;
        result = this.update(clp, param);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

    /**
     * ɾ��,��Ҫ�ṩCAT1_CODE
     * @param param ��Ҫ�ṩCAT1_CODE
     * @return TParm ����ɾ��״̬
     */
    public TParm deleteCLP_EVL_CAT(String clp, TParm param) {
        TParm result = null;
        result = this.update(clp, param);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

    /**
     * ɾ��,��Ҫ�ṩCAT1_CODE
     * @param param TParm
     * @return TParm ����ִ��״̬
     */
    public TParm deleteCLP_EVL_CAT(String clp, TParm param, TConnection con) {
        TParm result = null;
        result = this.update(clp, param, con);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + result.getErrText());
        }
        return result;
    }

}
