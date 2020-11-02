package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: סԺ��־����</p>
 *
 * <p>Description: סԺ��־����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-29
 * @version 4.0
 */
public class ADMLogTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMLogTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMLogTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMLogTool() {
        setModuleName("adm\\ADMLogModule.x");
        onInit();
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInHosp(TParm parm) {
        TParm result = this.query("selectInHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutHosp(TParm parm) {
        TParm result = this.query("selectOutHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDead(TParm parm){
        TParm result = this.query("selectDead", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯת�벡����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINPR(TParm parm){
        TParm result = this.query("selectINPR", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯת��������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUPR(TParm parm){
        TParm result = this.query("selectOUPR", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ʵ�в�����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBedSum(TParm parm){
        String sql = "SELECT COUNT(BED_NO) AS NUM " +
            " FROM SYS_BED " +
            " WHERE STATION_CODE # " +
            " AND ACTIVE_FLG='Y' " +
            " AND OCCU_RATE_FLG='Y'";
        if(parm.getValue("STATION").length()<=0){
            sql = sql.replace("#","IN (SELECT STATION_CODE FROM SYS_STATION WHERE DEPT_CODE = '"+parm.getValue("DEPT")+"')");
        }else{
            sql = sql.replace("#","='"+parm.getValue("STATION")+"'");
        }
        //=========pangben modify 20110510 start ����������
        if (null != parm.getValue("REGION_CODE") &&
            parm.getValue("REGION_CODE").length() != 0)
            sql += " AND REGION_CODE='" + parm.getValue("REGION_CODE") + "'";
        //=========pangben modify 20110510 stop
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯָ�����ڵ�ʵ�в�����
     * @param parm TParm  <DEPT>���ű������  <DATE>���ڱ������
     * @return TParm
     */
    public TParm selectHave(TParm parm){
        TParm result = this.query("selectHave", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
