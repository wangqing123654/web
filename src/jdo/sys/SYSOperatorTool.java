/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import java.util.Map;

/**
 *
 * <p>
 * Title: �û�����
 * </p>
 *
 * <p>
 * Description:�û�����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 200800901
 * @version 1.0
 */
public class SYSOperatorTool
    extends TJDOTool {
    public static final String GET_LCS_SQL =
        "SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL";
    /**
     * ʵ��
     */
    public static SYSOperatorTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return SYSOperatorTool
     */
    public static SYSOperatorTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSOperatorTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSOperatorTool() {
        setModuleName("sys\\SYSOperatorModule.x");
        onInit();
    }

    /**
     * ��ʼ�����棬��ѯ���е�����
     *
     * @return TParm
     */
    public TParm selectall() {
        TParm parm = new TParm();
        // parm.setData("CODE",CODE);
        TParm result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���� USER_ID �����ѯ����
     *
     * @param USER_ID
     *            String ����ICD����
     * @return TParm
     */
    public TParm selectdata(String USER_ID) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݱ䶯������ѯ���ݣ����user_name�Ƿ��С�%�������������
     *
     * @param parm
     *            String user_id,String REGION_CODE,String DEPT_CODE,String
     *            USER_NAME(��ƥ��ģ����ѯ),String POS_CODE,String ROLE_ID
     * @return TParm result
     */
    public TParm selecedataByParameters(TParm parm) {
        // ���user_name�Ƿ��С�%�������������
        String userName = parm.getValue("USER_NAME");
        if (userName != null && !userName.endsWith("%")) {
            userName = userName + "%";
        }
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        //System.out.println("after operator tool==================");
        //System.out.println("data====================" + result);
        return result;
    }

    /**
     * �ж��Ƿ��������
     *
     * @param USER_ID
     *            String
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsOperator(String USER_ID) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        //System.out.println("existsOperator" + USER_ID);
        return getResultInt(query("USER_ID", parm), "COUNT") > 0;
    }

    /**
     * ����ָ�� USER_ID �õ�����
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("USER_ID" + USER_ID);
        TParm result = new TParm();
        if (!existsOperator(USER_ID)) {
            result = update("insertdata", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "���� " + USER_ID + " �Ѿ�����!");
            return result;
        }

        return result;
    }

    /**
     * ����ָ��USER_ID����
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("true or false" + existsOperator(USER_ID));
        if (existsOperator(USER_ID)) {
            result = update("updatedata", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "���� " + USER_ID + " �����ڣ�");
            return result;
        }

        return result;
    }

    /**
     * ɾ��ָ�����ְ������
     *
     * @param USER_ID
     *            String
     * @return boolean
     */
    public TParm deletedata(String USER_ID) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ָ�� USER_ID �õ�����
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("USER_ID" + USER_ID);
        TParm result = new TParm();
        if (!existsOperator(USER_ID)) {
            result = update("insertdata", parm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "���� " + USER_ID + " �Ѿ�����!");
            return result;
        }

        return result;
    }

    /**
     * ����ָ��USER_ID����
     *
     * @param USER_ID
     *            String
     * @return TParm
     */
    public TParm updatedata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String USER_ID = parm.getValue("USER_ID");
        //System.out.println("true or false" + existsOperator(USER_ID));
        if (existsOperator(USER_ID)) {
            result = update("updatedata", parm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        else {
            result.setErr( -1, "���� " + USER_ID + " �����ڣ�");
            return result;
        }

        return result;
    }

    /**
     * ɾ��ָ�����ְ������
     *
     * @param USER_ID
     *            String
     * @return boolean
     */
    public TParm deletedata(String USER_ID, TConnection connection) {
        TParm parm = new TParm();
        parm.setData("USER_ID", USER_ID);
        TParm result = update("deletedata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * odo�춯�����
     *
     * @param parm
     * @param connection
     * @return result ������
     */
    public TParm onSave(TParm parm, TConnection connection) {
        TParm result = deletedata(parm.getParm(JDOOperatorList.DELETED)
                                  .getValue("USER_ID"), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = insertdata(parm.getParm(JDOOperatorList.NEW), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = updatedata(parm.getParm(JDOOperatorList.MODIFIED), connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����û�ID�õ�֤�����
     *
     * @param userId
     *            String �û�ID
     * @return result TParm ֤�����
     */
    public TParm getLcsCode(String userId) {
        TParm result;
        StringBuffer sb = new StringBuffer(GET_LCS_SQL);
        sb.append(" WHERE USER_ID='"
                  + userId
                  + "' AND TO_DATE('"
                  + StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                         "yyyyMMdd")
                  + "','YYYYMMDD') BETWEEN EFF_LCS_DATE AND END_LCS_DATE");
        result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
        return result;
    }

    /**
     * �����쳣��½����
     *
     * @param userId
     * @return
     */
    public boolean updateAbnormaiTimes(String userId) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("USER_ID", userId);
        result = update("updateAbnormaiTimes", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        return true;
    }

    /**
     * �����쳣��½����
     *
     * @param userId
     * @return
     */
    public boolean resetAbnormaiTimes(String userId) {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("USER_ID", userId);
        result = update("resetAbnormaiTimes", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return false;
        }
        return true;
    }

    /**
     * ���� USER_ID �����ѯ����
     *
     * @param USER_ID
     *
     * @return TParm
     */
    public TParm getOperator(String USER_ID,String regionCode) {
        String sql = SYSSQL.getSYSOperator(regionCode) + " WHERE USER_ID='" + USER_ID
            + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����û���Ϣ
     *
     * @param parm
     * @param connection
     * @return
     */
    public TParm onSaveOperator(TParm parm, TConnection conn) {
        TParm result = new TParm();
        // ���ݼ��
        if (parm == null)
            return null;
        // ʹ��������
        if (parm.existData("OPERATOR")) {
            Object update = parm.getData("OPERATOR");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // ������Ϣ
        if (parm.existData("DEPT")) {
            Object update = parm.getData("DEPT");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // ֤����Ϣ
        if (parm.existData("LISCENSE")) {
            Object update = parm.getData("LISCENSE");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // ������Ϣ
        if (parm.existData("CLINICAREA")) {
            Object update = parm.getData("CLINICAREA");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        // ������Ϣ
        if (parm.existData("STATION")) {
            Object update = parm.getData("STATION");
            if (update == null) {
                return null;
            }
            if (update instanceof String[]) {
                String[] updateSql = (String[]) update;
                for (int i = 0; i < updateSql.length; i++) {
                    result = new TParm(TJDODBTool.getInstance().update(
                        updateSql[i], conn));
                    if (result.getErrCode() < 0) {
                        return result;
                    }
                }
            }
            else {
                return null;
            }
        }
        //��¼�����û��Ľ�ɫ�������־,���û�Ȩ�ޣ�Ȩ���飬��ɫ�����仯ʱ,д�����Ϣ��SYS_OPERATORLOG��
        if (parm.existData("userInfoChange")) {
            TParm userInfoChange = new TParm((Map)parm.getData("userInfoChange"));
            /*
             * ����dataParm,һ���޸ĵĲ�ֻ�ǽ�ɫ�ĸı�,���ڰ�������
             */
            for (int i = 0; i < userInfoChange.getCount("MODI_ITEM"); i++) {
                TParm dataParm = new TParm();
                dataParm.setData("MODI_ITEM",
                                 userInfoChange.getData("MODI_ITEM", i));
                dataParm.setData("MODI_ITEM_CHN",
                                 userInfoChange.getData("MODI_ITEM_CHN", i));
                dataParm.setData("ITEM_OLD",
                                 userInfoChange.getData("ITEM_OLD", i));
                dataParm.setData("ITEM_NEW",
                                 userInfoChange.getData("ITEM_NEW", i));
                dataParm.setData("DESCRIPTION",
                                 userInfoChange.getData("DESCRIPTION", i));
                dataParm.setData("USER_ID", userInfoChange.getData("USER_ID"));
                dataParm.setData("OPT_DATE", userInfoChange.getData("OPT_DATE"));
                dataParm.setData("OPT_USER", userInfoChange.getData("OPT_USER"));
                dataParm.setData("OPT_TERM", userInfoChange.getData("OPT_TERM"));
                //ִ��,
                result = this.update("userInfoChange", dataParm, conn);
                if (result.getErrCode() < 0) {
                    return result;
                }else{
                    continue;
                }
            }
        }

        return result;
    }

    /**
     * �õ��ż����շ�Ա
     * @return TParm
     * ======pangben modify 20110421 ��Ӳ���
     */
    public TParm getCasherCode(TParm parm) {
        TParm result = result = query("getCasherCode",parm);
        //if (result.getErrCode() < 0)
        //System.out.println("ȡ���շ�Ա" + result.getErrText());
        return result;
    }

    /**
     * �����û�ID��ѯ���û��Ƿ��ж���ҩ֤��
     * @param user_id String
     * @return boolean
     */
    public boolean getOperatorCtrlFlg(String user_id) {
        TParm parm = getLcsCode(user_id);
        if (parm == null || parm.getCount() == 0) {
            return false;
        }
        for (int i = 0; i < parm.getCount(); i++) {
            if ("1".equals(parm.getValue("LCS_CLASS_CODE", i))) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param userId
     * @param code
     * @return
     */
    public boolean hasPermission(String userId,String code){

    	String sql = " SELECT AUTH_CODE FROM SYS_USER_AUTH WHERE USER_ID ='"+userId+"' AND " +
    			" AUTH_CODE = '"+code+"'" ;
    	TParm TP = new TParm( TJDODBTool.getInstance().select( sql ) );

    	if(TP.getCount() > 0){
    		return true;
    	}

    	return false;
    }
    
    /**
     * ȡ�����û��Լ���Ȩ��  add by huangtt 20150513
     * @param userId
     * @param code
     * @return
     */
    public static TParm getUserPopedem(String userId,String code){
    	String sql = " SELECT AUTH_CODE FROM SYS_USER_AUTH WHERE USER_ID ='"+userId+"' AND " +
    			" GROUP_CODE = '"+code+"'" ;
    	TParm TP = new TParm( TJDODBTool.getInstance().select( sql ) );
    	return TP;
    }

}
