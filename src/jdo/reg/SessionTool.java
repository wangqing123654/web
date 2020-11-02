package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.javahis.util.JavaHisDebug;
import java.util.ArrayList;
import com.dongyang.jdo.TJDODBTool;
/**
 *
 * <p>Title:�Һ�ʱ�ι����� </p>
 *
 * <p>Description:�Һ�ʱ�ι����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class SessionTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static SessionTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SessionTool
     */
    public static SessionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SessionTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SessionTool() {
        setModuleName("reg\\REGSessionModule.x");

        onInit();
    }
    /**
     * �����Һ�ʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���¹Һ�ʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����ż�ס��,�����ѯ�Һ�ʱ��(�Һ���)
     * @param admType String
     * @param regionCode String
     * @param sessionCode String
     * @return TParm
     */
    public TParm selectdata(String admType,String regionCode,String sessionCode) {
        TParm parm = new TParm();
//        admType += "%";
        if (!"".equals(admType))
            parm.setData("ADM_TYPE", admType);
//        regionCode += "%";
        if (!"".equals(regionCode))
            parm.setData("REGION_CODE", regionCode);
//        sessionCode += "%";
        if (!"".equals(sessionCode))
            parm.setData("SESSION_CODE", sessionCode);
//        System.out.println("���"+parm);
        TParm result = query("selectdata", parm);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��ѡ�йҺ�ʱ��
     * @param admType String
     * @param regionCode String
     * @param sessionCode String
     * @return TParm
     */
    public TParm deletedata(String admType,String regionCode,String sessionCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("REGION_CODE", regionCode);
        parm.setData("SESSION_CODE", sessionCode);

        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ȡ���ż�ס��combo��Ϣ
     * @return TParm
     */
    public TParm getadmTypeCombo() {
        TParm parm = new TParm();
        return query("getadmTypeCombo", parm);
    }

    /**
     * ȡ������combo��Ϣ
     * @return TParm
     */
    public TParm getRegionCombo() {
        TParm parm = new TParm();
        return query("getRegionCombo", parm);
    }
    /**
     * �ж��Ƿ���ڹҺ�ʱ��
     * @param sessionCode String �Һ�ʱ��
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsSession(String sessionCode){
        TParm parm = new TParm();
        parm.setData("SESSION_CODE",sessionCode);
        return getResultInt(query("existsSession",parm),"COUNT") > 0;
    }
    /**
     * ȡ�ùҺ�ʱ������
     * @param admType String
     * @param regionCode String
     * @return String
     */
    public String getDefSessionNow(String admType ,String regionCode) {
        String whereRegion = "";
        if(!"".equals(regionCode))
            whereRegion = "    AND REGION_CODE = '"+regionCode+"' ";
        //yanjing �������20130502
        String sql =
            " SELECT SESSION_CODE "+
            "   FROM REG_SESSION "+
            "  WHERE ADM_TYPE='"+admType+"' "+
            whereRegion +
            "    AND (       TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN START_REG_TIME "+
            "                                                 AND END_REG_TIME "+
            "            AND (START_REG_TIME < END_REG_TIME) "+
            "            OR (    (   TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN '00:00:00' AND END_REG_TIME "+
            "                    OR TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN START_REG_TIME "+
            "                                                        AND '24:00:00') "+
            "            AND (START_REG_TIME > END_REG_TIME) ) ) ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String sessionCode = result.getValue("SESSION_CODE",0);
        return sessionCode;
    }
    
    public String getDefSessionNow_New(String admType ,String regionCode) {
        String whereRegion = "";
        if(!"".equals(regionCode))
            whereRegion = "    AND REGION_CODE = '"+regionCode+"' ";
        //yanjing �������20130502
        String sql =
            " SELECT SESSION_CODE,SEQ "+
            "   FROM REG_SESSION "+
            "  WHERE ADM_TYPE='"+admType+"' "+
            whereRegion +
            "    AND (       TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN START_REG_TIME "+
            "                                                 AND END_REG_TIME "+
            "            AND (START_REG_TIME < END_REG_TIME) "+
            "            OR (    (   TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN '00:00:00' AND END_REG_TIME "+
            "                    OR TO_CHAR (SYSDATE, 'HH24:MI:SS') BETWEEN START_REG_TIME "+
            "                                                        AND '24:00:00') "+
            "            AND (START_REG_TIME > END_REG_TIME) ) ) ORDER BY SEQ ";
        //System.out.println("sql11:::"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String sessionCode = result.getValue("SESSION_CODE",0);
        return sessionCode;
    }

    /**
     * ȡ�ÿ���ʱ������
     * @param admType String
     * @param regionCode String
     * @return String
     */
    public String getDrSessionNow(String admType,String regionCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        if (!"".equals(regionCode))
            parm.setData("REGION_CODE", regionCode);
        return getResultString(query("getDrSessionNow", parm), "SESSION_CODE");
    }
    /**
     * �õ�ʱ�α���б�
     * @param admType String
     * @param regionCode String
     * @return String[]
     */
    public String[] getSessionCode(String admType,String regionCode)
    {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE",admType);
        if(!"".equals(regionCode))
            parm.setData("REGION_CODE",regionCode);
        TParm result = query("getSessionCode",parm);
        if(result.getErrCode() < 0)
            return null;
        ArrayList list = new ArrayList();
        int count = result.getCount();
        for(int i = 0;i < count;i ++)
            list.add(result.getValue("SESSION_CODE",i));
        return (String[])list.toArray(new String[]{});
    }
    /**
     * �����ż����ʱ�δ����ѯʱ����Ϣ
     * @param admType String
     * @param session_code String
     * @param regionCode String
     * @return TParm
     */
    public TParm getSessionInfoByCode(String admType,String session_code,String regionCode){
        TParm parm = new TParm();
        parm.setData("ADM_TYPE",admType);
        parm.setData("SESSION_CODE",session_code);
        if(!"".equals(regionCode))
            parm.setData("REGION_CODE",regionCode);
        TParm result = query("getSessionInfoByCode",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �ж�ĳ��ʱ���Ƿ��ǿ����
     * @param admType String
     * @param session_code String
     * @param regionCode String
     * @return boolean
     */
    public boolean isCrossDays(String admType,String session_code,String regionCode){
        TParm session = this.getSessionInfoByCode(admType,session_code,regionCode);
        boolean result = false;
        if(session.getCount()>0){
            if(session.getValue("START_CLINIC_TIME",0).compareTo(session.getValue("END_CLINIC_TIME",0))>0){
                result = true;
            }
        }
        return result;
    }
    /**
     * ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
     * @param admType String
     * @param session_code String
     * @param regionCode String
     * @return Timestamp
     */
    public Timestamp getDateForSession(String admType,String session_code,String regionCode){
        //��ǰ������ʱ��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //�жϸ�ʱ���Ƿ����
        if(!this.isCrossDays(admType,session_code,regionCode)){
            //��������췵�ص�ǰ������ʱ��
            return date;
        }
        //�������
        TParm session = this.getSessionInfoByCode(admType,session_code,regionCode);//ʱ����Ϣ
        String time = StringTool.getString(date,"HH:mm:ss");
        //�жϵ�ǰʱ���Ƿ��Ѿ���������ʱ��
        if(time.compareTo(session.getValue("END_CLINIC_TIME",0))<0){
            //���û�г�������ʱ�� ��ô����ǰһ�������
            date = StringTool.rollDate(date,-1);
        }
        return date;
    }
    public static void main(String args[])
    {
        JavaHisDebug.initClient();
       // System.out.println(StringTool.getString(SessionTool.getInstance().getSessionCode("E","")));
    }
/**
 * ��ȡ�Һſ�ʼʱ��
 *yanjing20130503
 */
public String getStartTime(String admType,String sessionCode){
	String whereSession = "";
	if (!"".equals(sessionCode)) 
		 whereSession = "    AND SESSION_CODE = '"+sessionCode+"' ";
			String sql = "SELECT START_REG_TIME"+
				" FROM REG_SESSION"+
				" WHERE ADM_TYPE='"+admType+"'"+
						whereSession;
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		    String startTime = result.getValue("START_REG_TIME",0);
	return startTime;
	
}
/**
 * ��ȡ�ҺŽ���ʱ��
 * yanjing 20130503
 */
public String getEndTime(String admType,String sessionCode){
	String whereSession = "";
	if (!"".equals(sessionCode)) 
		 whereSession = "    AND SESSION_CODE = '"+sessionCode+"' ";
			String sql = "SELECT END_REG_TIME"+
				" FROM REG_SESSION"+
				" WHERE ADM_TYPE='"+admType+"'"+
						whereSession;
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		    String endTime = result.getValue("END_REG_TIME",0);
	return endTime;
}
	
}
