package jdo.odi;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ����ʱ��</p>
 *
 * <p>Description: ����ʱ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class ODIintoDurationTool extends TJDOTool {
    public ODIintoDurationTool() {
        setModuleName("odi\\ODIintoDurationModule.x");
        onInit();
    }

    private static ODIintoDurationTool instanceObject;

    public static ODIintoDurationTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new ODIintoDurationTool();
        }
        return instanceObject;
    }
    public TParm insertIntoDurationReal(TParm saveParm,TConnection conn) {
        String clncPathCode=saveParm.getValue("CLNC_PATHCODE");
        String nextDuration=saveParm.getValue("NEXT_DURATION");
        String case_no=saveParm.getValue("CASE_NO");
        String opt_user=saveParm.getValue("OPT_USER");
        String region_code=saveParm.getValue("REGION_CODE");
        String opt_date=saveParm.getValue("OPT_DATE");
        String opt_term=saveParm.getValue("OPT_TERM");
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("INSERT INTO CLP_THRPYSCHDM_REAL (CLNCPATH_CODE,SCHD_CODE,CASE_NO,SEQ,REGION_CODE,SUSTAINED_DAYS,OPT_USER,OPT_DATE,OPT_TERM,SCHD_DAY,START_DATE)");
        sqlbf.append("VALUES (");
        sqlbf.append("'" + clncPathCode + "',");
        sqlbf.append("'" + nextDuration + "',");
        sqlbf.append("'" + case_no + "',");
        sqlbf.append("(SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ)+1)) END FROM CLP_THRPYSCHDM_REAL GROUP BY SEQ),");
        sqlbf.append("'" + region_code + "',");
        sqlbf.append(
                "(SELECT CASE WHEN   ROUND(TO_NUMBER(TO_DATE('"+opt_date+"','YYYYMMDDHH24MISS')-(SELECT IN_DATE FROM ADM_INP WHERE CASE_NO ='" +
                case_no + "' ))+1) <=0 THEN 1  "+//END
                "ELSE ROUND(TO_NUMBER(TO_DATE('"+opt_date+"','YYYYMMDDHH24MISS')-(SELECT IN_DATE FROM ADM_INP WHERE CASE_NO ='" +
                case_no + "' ))+1) END  FROM DUAL ),");
        sqlbf.append("'" + opt_user + "',");
        sqlbf.append(" TO_DATE('" + opt_date + "','YYYYMMDDHH24MISS'),");
        sqlbf.append("'" + opt_term + "',");
        sqlbf.append("0,");
        sqlbf.append(" TO_DATE('" + opt_date + "','YYYYMMDDHH24MISS')");
        sqlbf.append(")");
        //System.out.println("����ʱ��ִ�е�sql:" + sqlbf.toString());
        TParm result =null;
        if(conn==null){
            result = new TParm(TJDODBTool.getInstance().update(
                sqlbf.toString()));
        }else{
            result = new TParm(TJDODBTool.getInstance().update(
                sqlbf.toString(),conn));
        }
        return result;
    }

    /**
     * ������һʱ��
     * @param saveParm TParm
     * @return TParm
     */
    public TParm updateNextDuration(TParm saveParm,TConnection conn){
        String clncPathCode = saveParm.getValue("CLNC_PATHCODE");
        String nextDuration = saveParm.getValue("NEXT_DURATION");
        String case_no = saveParm.getValue("CASE_NO");
        TParm result=null;
        StringBuffer sqlbf=new StringBuffer();
        //���һ��==========pangben 2012-6-26
        sqlbf.append("UPDATE CLP_THRPYSCHDM_REAL SET START_DATE =SYSDATE + INTERVAL '1' SECOND ");
        sqlbf.append(" WHERE CLNCPATH_CODE = '"+clncPathCode+"'");
        sqlbf.append(" AND SCHD_CODE='"+nextDuration+"' ");
        sqlbf.append(" AND CASE_NO='"+case_no+"'");
        //System.out.println("������һʱ��sql��"+sqlbf.toString());
        result = new TParm(TJDODBTool.getInstance().update(
                sqlbf.toString(),conn));
        return result;
    }

    public TParm insertIntoDurationReal(TParm saveParm) {
        return insertIntoDurationReal(saveParm, null);
    }

    /**
     * ������һʱ�̷���
     * @param saveParm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateDurationReal(TParm saveParm,TConnection conn){
        TParm result=null;
        if(conn==null){
            result= this.update("updateDuration",saveParm);
        }else{
            result= this.update("updateDuration",saveParm,conn);
        }
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������һʱ�̷���
     * @param saveParm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateDurationReal(TParm saveParm) {
        return updateDurationReal(saveParm,null);
    }
    /**
     * ��ѯʱ���Ƿ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm isDurationEnd(TParm parm){
        TParm result = null;
        result=this.query("isDurationEnd",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
