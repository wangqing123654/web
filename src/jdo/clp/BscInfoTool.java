package jdo.clp;

import java.text.*;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import java.util.Map;

/**
 * <p>Title: ���ݿ����������</p>
 *
 * <p>Description: �ٴ�·����׼�趨</p>
 *
 * <p>Copyright: Copyright (c) Zhang jianguo 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class BscInfoTool
    extends TJDOTool {
    /**
     * ���췽��˽�л�������SQL��������ļ�
     */
    private BscInfoTool() {
        this.setModuleName("clp\\CLPBscInfoModule.x");
        onInit();
    }

    /**
     * ������̬���ݿ⹤����ʵ��
     */
    private static BscInfoTool instance = null;
    /**
     * �������ڸ�ʽ�������
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * ������̬���������ش����ʵ��
     * @return BscInfoTool
     */
    public static BscInfoTool getInstance() {
        if (instance == null) {
            instance = new BscInfoTool();
        }
        return instance;
    }

    /**
     * ��ѯȫ���ٴ�·�����
     * @return TParm
     */
    public TParm getAllBscInfos(TParm parm) {
        TParm result = new TParm();
        result = query("queryAll",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
	/**
	 * ��ҽ����ӵ���ʷ��,��ʼʱ��Ϊ��ǰʱ�䣬����ʱ��Ϊ99991231235959 
	 * lilig 20140825 add
	 *@param parm TParm
     * @return TParm 
	 */
    public TParm insertPackHistory(TParm parm, TConnection conn){
    	  TParm result = new TParm();
          result = update("insertPackHistory",parm, conn);
          // �жϴ���ֵ
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;	
    }
    /**���Ѿ��޶�����ʷ��ҽ���滻����ʹ�õ��ٴ�·����Ŀҽ��
	 * lilig 20140825 add
	 *@param parm TParm
     * @return TParm 
	 */
    public TParm insertPack(TParm parm, TConnection conn){
    	  TParm result = new TParm();
          result = update("insertPack",parm, conn);
          // �жϴ���ֵ
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          return result;	
    }
    /**
     * ģ����ѯ�ٴ�·�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getBscInfoList(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(" SELECT A.*,CASE  WHEN  B.ICD_CHN_DESC IS NULL THEN '' ELSE B.ICD_CHN_DESC END  AS ICD_DESC , ");
        sqlBuf.append(" CASE WHEN C.ICD_CHN_DESC IS NULL THEN '' ELSE C.ICD_CHN_DESC END AS OPE_DESC ");
        sqlBuf.append(" FROM CLP_BSCINFO A,SYS_DIAGNOSIS B,SYS_DIAGNOSIS C  WHERE 1=1 ");
        sqlBuf.append(" AND A.ICD_CODE=B.ICD_CODE(+) AND A.OPE_ICD_CODE=C.ICD_CODE(+) ");
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND A.REGION_CODE = '" + parm.getValue("REGION_CODE") +
                          "'");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND A.CLNCPATH_CODE LIKE '%" +
                          parm.getValue("CLNCPATH_CODE") + "%'");
        }
        if (parm.getValue("CLNCPATH_PYCODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_PYCODE").trim())) {
            sqlBuf.append(" AND A.CLNCPATH_PYCODE LIKE '%" +
                          parm.getValue("CLNCPATH_PYCODE") + "%'");
        }
        if (parm.getValue("CLNCPATH_DESC") != null &&
            !"".equals(parm.getValue("CLNCPATH_DESC").trim())) {
            sqlBuf.append(" AND A.CLNCPATH_DESC LIKE '%" +
                          parm.getValue("CLNCPATH_DESC") + "%'");
        }
        if (parm.getValue("DEPT_CODE") != null &&
            !"".equals(parm.getValue("DEPT_CODE").trim())) {
            sqlBuf.append(" AND A.DEPT_CODE = '" + parm.getValue("DEPT_CODE") +
                          "'");
        }
        if (parm.getValue("STAYHOSP_DAYS") != null &&
            !"".equals(parm.getValue("STAYHOSP_DAYS").trim())) {
            sqlBuf.append(" AND A.STAYHOSP_DAYS = '" +
                          parm.getValue("STAYHOSP_DAYS") + "'");
        }
        if (parm.getValue("AVERAGECOST") != null &&
            !"".equals(parm.getValue("AVERAGECOST").trim())) {
            sqlBuf.append(" AND A.AVERAGECOST >= '" + parm.getValue("AVERAGECOST") +
                          "'");
        }
        if (parm.getValue("VERSION") != null &&
            !"".equals(parm.getValue("VERSION").trim())) {
            sqlBuf.append(" AND A.VERSION >= '" + parm.getValue("VERSION") + "'");
        }
        if (parm.getValue("ACTIVE_FLG") != null &&
            !"".equals(parm.getValue("ACTIVE_FLG").trim())) {
            sqlBuf.append(" AND A.ACTIVE_FLG >= '" + parm.getValue("ACTIVE_FLG") +
                          "'");
        }
        sqlBuf.append(" ORDER BY A.SEQ ");
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
     * ��ȷ��ѯ�ٴ�·�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getBscInfoObject(TParm parm) {
        TParm result = new TParm();
        result = query("queryBscInfo", parm);
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
    public TParm update(TParm parm,TConnection conn) {
        TParm result = new TParm();
        result = this.update("updateBscInfo", parm,conn);
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
     *��׼����
     * @param parm TParm
     */
    public TParm insert(TParm parm,TConnection conn) {
        TParm result = new TParm();
        result = this.update("insertBscInfo", parm,conn);
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
     *��׼��ʷ��
     * @param parm TParm
     * liling 20140827 add
     */
    public TParm insertHis(TParm parm,TConnection conn) {
        TParm result = new TParm();
        result = this.update("insertBscInfoHis", parm,conn);
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
        result = this.update("deleteBscInfo", parm, conn);
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
     *����ʷ���еĵ�ǰʹ�õİ汾�����ݽ���ʱ���޸ĳɵ�ǰʱ��
     * @param parm TParm
     * @return TParm
     * liling 20140829 add 
     */
    public TParm updateBscHis(TParm parm, TConnection conn){
    	TParm result = new TParm();
        result = this.update("updateBscInfoHis", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��ҽ���ײ���ʷ������
     * @param parm
     * @param conn
     * @return
     * liling 20140829 add 
     */
    public TParm deletePackHis(TParm parm, TConnection conn){
    	TParm result = new TParm();
//    	System.out.println("bscinfotool delete++"+parm.getData());
    	for (int i = 0; i < parm.getCount("REGION_CODE"); i++) {
        result = this.update("deletePackHis", parm.getRow(i), conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        }
        return result;
    }
    /**
     * ����
     * @param parm
     * @param conn
     * @return
     * liling 20140910 add 
     */
    public TParm onUse(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("useBscInfo", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * use
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm use(TParm parm) {
        TParm result = new TParm();
        result = this.update("useBscInfo", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * use
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm use(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("forbidBscInfo", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����Ƶ�κ�ҽ�������ж�Ƶ���Ƿ�Ϸ�
     * @param parm TParm
     * @return TParm
     */
    public TParm checkFreq(TParm parm){
        TParm result = new TParm();
        result = this.query("checkFreq", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �Ƿ���ڶ�Ӧ�ٴ�·��
     * @param clpPathCode String
     * @return TParm
     */
    public TParm existBscinfo(TParm parm) {
        TParm result = new TParm();
        result = this.query("existBscinfo", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    public TParm selectDiagICDList(TParm parm){
        StringBuffer sqlbf=new StringBuffer();
        sqlbf.append("SELECT A.CLNCPATH_CODE ,A.ICD_TYPE_BEGIN AS diagnose_icd_type_begin ,A.ICD_CODE_BEGIN AS diagnose_icd_begin ");
        sqlbf.append(" ,A.ICD_TYPE_END AS diagnose_icd_type_end,A.ICD_CODE_END AS diagnose_icd_end , ");
        sqlbf.append(" B.ICD_CHN_DESC AS diagnose_desc_begin ,C.ICD_CHN_DESC AS diagnose_desc_end ");
        sqlbf.append(" FROM CLP_CLNCPATHERDIAGICD A,SYS_DIAGNOSIS B,SYS_DIAGNOSIS C  ");
        sqlbf.append(" WHERE A.ICD_TYPE_BEGIN=B.ICD_TYPE(+) AND A.ICD_CODE_BEGIN=B.ICD_CODE(+) ");
        sqlbf.append(" AND  A.ICD_TYPE_END=C.ICD_TYPE(+) AND A.ICD_CODE_END=C.ICD_CODE(+) ");
        sqlbf.append(" AND  A.CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"'");
        sqlbf.append(" ORDER BY A.SEQ ");
//        System.out.println("-----"+sqlbf.toString());
        Map resultMap= TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result=new TParm(resultMap);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm selectOptICDList(TParm parm) {
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT A.OPERATION_ICD_BEGIN AS operator_diagnose_icd_begin  ,A.OPERATION_ICD_END AS operator_diagnose_icd_end ,B.OPT_CHN_DESC AS operator_diagnose_desc_begin ,C.OPT_CHN_DESC AS operator_diagnose_desc_end ");
        sqlbf.append(" FROM CLP_CLNCPATHEROPTICD A,SYS_OPERATIONICD B,SYS_OPERATIONICD C ");
        sqlbf.append(" WHERE A.OPERATION_ICD_BEGIN=B.OPERATION_ICD(+) ");
        sqlbf.append(" AND   A.OPERATION_ICD_END=C.OPERATION_ICD(+) ");
        sqlbf.append(" AND  A.CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"'");
        sqlbf.append(" ORDER BY A.SEQ");
//        System.out.println("-----"+sqlbf.toString());
        Map resultMap = TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(resultMap);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
