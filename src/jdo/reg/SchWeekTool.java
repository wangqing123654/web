package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
/**
 *
 * <p>Title:ҽʦ�ܰ������ </p>
 *
 * <p>Description:ҽʦ�ܰ������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class SchWeekTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static SchWeekTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static SchWeekTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SchWeekTool();
        return instanceObject;
    }
    /**
     * ������
     */
    public SchWeekTool()
    {
        setModuleName("reg\\REGSchWeekModule.x");
        onInit();
    }
    /**
     * �����ܰ��
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
     * �����ܰ��
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
     * ����������ѯ�ܰ��(�Һ���)
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm){
//        System.out.println("�ܰ������"+parm);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ת�ղ�ѯ(��)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataNew(TParm parm){
        //System.out.println("�ܰ���������"+parm);

        String regionWhere = "";
        if (parm.getData("REGION_CODE") != null)
            regionWhere = " AND REGION_CODE = '" + parm.getData("REGION_CODE") +
                          "' ";
        String admTypeWhere = "";
        if (parm.getData("ADM_TYPE") != null)
            admTypeWhere = " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                           "' ";
        String dayOfWeek = "";
        if (parm.getData("DAYOFWEEK") != null)
            dayOfWeek = " AND DAYOFWEEK = '" + parm.getData("DAYOFWEEK") + "' ";
        String roomWhere = "";
        if (parm.getData("CLINICROOM_NO") != null)
            roomWhere = " AND CLINICROOM_NO IN (" +
                        parm.getData("CLINICROOM_NO") + ") ";
        String sql =
                " SELECT REGION_CODE, ADM_TYPE, DAYOFWEEK, SESSION_CODE, CLINICTYPE_CODE,"+
                "        DEPT_CODE, DR_CODE, CLINICROOM_NO, QUEGROUP_CODE, WEST_MEDI_FLG,"+
                "        CREAT_DATE, CLINICTMP_FLG, EXP_DATE, EXP_DATE_E, OPT_USER, OPT_DATE,"+
                "        OPT_TERM "+
                "   FROM REG_SCHWEEK "+
                "  WHERE  (   EXP_DATE IS NULL "+
                "         OR EXP_DATE_E IS NULL "+
                "         OR EXP_DATE > TO_DATE ('"+parm.getData("ADM_DATE")+"', 'yyyyMMdd') "+
                "         OR EXP_DATE_E < TO_DATE ('"+parm.getData("ADM_DATE")+"', 'yyyyMMdd')) "+
                regionWhere +
                admTypeWhere +
                dayOfWeek +
                roomWhere +
                "   ORDER BY ADM_TYPE, DAYOFWEEK, SESSION_CODE, CLINICTYPE_CODE, DEPT_CODE ";
       // System.out.println("�ܰ������sql"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        TParm result = query("selDataNew", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ��
     * @param region String ����
     * @param admType String �ż���
     * @param dayofWeek String ����
     * @param sessionCode String ʱ��
     * @param clinicroomNo String ����
     * @return TParm
     */
    public TParm deletedata(String region,String admType,String dayofWeek,String sessionCode,String clinicroomNo){
        TParm parm = new TParm();
        parm.setData("REGION_CODE",region);
        parm.setData("ADM_TYPE",admType);
        parm.setData("DAYOFWEEK",dayofWeek);
        parm.setData("SESSION_CODE",sessionCode);
        parm.setData("CLINICROOM_NO",clinicroomNo);

        TParm result = update("deletedata",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
