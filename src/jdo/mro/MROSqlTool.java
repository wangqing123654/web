package jdo.mro;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ��������ϵͳ ��ѯ�����</p>
 *
 * <p>Description: ��������ϵͳ ��ѯ�����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-13
 * @version 1.0
 */
public class MROSqlTool
    extends TJDODBTool {

    /**
     * ʵ��
     */
    public static MROSqlTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROSqlTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROSqlTool();
        return instanceObject;
    }

    public MROSqlTool() {
    }
    /**
     * ��ѯ������Ϣ
     * @param MR_NO String
     * @param CASE_NO String
     * @return String
     */
    public String getOPSelectSQL(String MR_NO,String CASE_NO){
        String sql = "SELECT CASE_NO, SEQ_NO, IPD_NO," +
            " MR_NO, OP_CODE, OP_DESC,  " +
            " OP_REMARK, OP_DATE, ANA_WAY,  " +
            " ANA_DR, MAIN_SUGEON, MAIN_SUGEON_REMARK, AST_DR1,  " +
            " AST_DR2, HEALTH_LEVEL, OP_LEVEL, OPT_USER, OPT_DATE,"+
            "   OPT_TERM,MAIN_FLG,AGN_FLG,OPERATION_TYPE,OPE_SITE,OPE_TIME,ANA_LEVEL ,NNIS_CODE,OP_END_DATE" + //add caoyong OP_END_DATE 20140325
            " "+
            " FROM MRO_RECORD_OP "+
            " WHERE MR_NO = '"+MR_NO+"' " +
            " AND CASE_NO = '"+CASE_NO+"'";
        return sql;
    }
    /**
     * ��ѯ��������������
     * @param CASE_NO String
     * @return String
     */
    public String getChildWeightSQL(String CASE_NO){
        String sql = "SELECT BORNWEIGHT "+
            "FROM SUM_NEWARRIVALSIGN "+
            "WHERE CASE_NO='"+CASE_NO+"' "+
            "AND BORNWEIGHT IS NOT NULL AND BORNWEIGHT <> 0";
        return sql;
    }
}
