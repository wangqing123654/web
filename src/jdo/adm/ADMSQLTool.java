package jdo.adm;

import com.dongyang.jdo.*;

/**
 * <p>Title: סԺ����ϵͳSQL Tool</p>
 *
 * <p>Description: סԺ����ϵͳSQL Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk  2009-9-11
 * @version 4.0
 */
public class ADMSQLTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMSQLTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMSQLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMSQLTool();
        return instanceObject;
    }

    public ADMSQLTool() {
    }
    /**
     * ��ȡ��ת�벡����Ϣ
     * @param in_dept String
     * @param in_station String
     * @return String
     */
    public String getWAIT_TRANS_IN(String dept_code,String station_code){
        String sql = "SELECT " +
            " CASE_NO, MR_NO, IPD_NO, " +
            " WAIT_DATE, OUT_DEPT_CODE, OUT_STATION_CODE, " +
            " IN_DEPT_CODE, IN_STATION_CODE, OPT_USER, " +
            " OPT_DATE, OPT_TERM,'' as NAME,'' as AGE,'' as SEX " +
            " FROM ADM_WAIT_TRANS "+
            " WHERE IN_DEPT_CODE IS NOT NULL "+
            " AND IN_STATION_CODE = '"+station_code+"'";
            if(dept_code.length()>0)
                sql += " AND IN_DEPT_CODE='"+ dept_code +"'";
//        String sql = "SELECT " +
//        " CASE_NO, MR_NO, IPD_NO, " +
//        " WAIT_DATE, OUT_DEPT_CODE, OUT_STATION_CODE, " +
//        " IN_DEPT_CODE, IN_STATION_CODE, OPT_USER, " +
//        " OPT_DATE, OPT_TERM,'' as NAME,'' as AGE,'' as SEX " +
//        " FROM ADM_WAIT_TRANS "+
//        " WHERE IN_DEPT_CODE IS NOT NULL " ;     	
    		
        return sql;
    }
    /**
     * ��ȡ��ת��������Ϣ
     * @param in_station String
     * @return String
     */
    public String getWAIT_TRANS_OUT(String dept_code,String station_code){
        String sql = "SELECT " +
            " CASE_NO, MR_NO, IPD_NO, " +
            " WAIT_DATE, OUT_DEPT_CODE, OUT_STATION_CODE, " +
            " IN_DEPT_CODE, IN_STATION_CODE, OPT_USER, " +
            " OPT_DATE, OPT_TERM,'' as NAME,'' as AGE,'' as SEX " +
            " FROM ADM_WAIT_TRANS "+
            " WHERE OUT_DEPT_CODE IS NOT NULL ";
            if(dept_code.length()>0)
                sql += " AND OUT_DEPT_CODE='"+ dept_code +"'";
            if(station_code.length()>0)
                sql += " AND OUT_STATION_CODE = '"+station_code+"'";
        return sql;
    }
    /**
     * �����û�ID��ѯ���û����������п���
     * @param id String
     * @return String
     */
    public String getUserDeptList(String id){
        String sql = "SELECT B.DEPT_CODE,B.DEPT_CHN_DESC "+
            " FROM SYS_OPERATOR_DEPT A,SYS_DEPT B "+
            " where A.DEPT_CODE=B.DEPT_CODE "+
            " and A.USER_ID='"+id+"'";
        return sql;
    }
    /**
     * �����û�ID��ѯ���û����������п���
     * @param id String
     * @return String
     */
    public String getUserStationList(String id){
        String sql = "SELECT B.STATION_CODE,B.STATION_DESC "+
            " FROM SYS_OPERATOR_STATION A,SYS_STATION B "+
            " where A.STATION_CLINIC_CODE=B.STATION_CODE "+
            " and A.USER_ID='"+id+"'";
        return sql;
    }
    
	/**
	 * ��ѯ���п��ң�Ϊ��̬��¼��ѯ��
	 * @param id String
	 * @return String
	 */
	public String getUserStationListForDynaSch(){
	    String sql = "SELECT DISTINCT B.STATION_CODE,B.STATION_DESC "+
	        " FROM SYS_OPERATOR_STATION A,SYS_STATION B "+
	        " where A.STATION_CLINIC_CODE=B.STATION_CODE ORDER BY B.STATION_CODE";
	    return sql;
	}
	
    /**
     * �����û�ID��ѯ���û����������п���
     * @param id String
     * @return String
     */
    public String getUserDeptListForDynaSch(){
        String sql = "SELECT B.DEPT_CODE,B.DEPT_CHN_DESC "+
            " FROM SYS_DEPT B "+
            " where B.FINAL_FLG='Y' AND B.DEPT_GRADE='3' AND B.CLASSIFY='0' AND B.IPD_FIT_FLG='Y' ORDER BY B.DEPT_CODE ";
        return sql;
    }	
}
