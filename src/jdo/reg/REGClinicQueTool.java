package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title:VIP���Ű๤���� </p>
 *
 * <p>Description:VIP���Ű๤���� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.10.06
 * @version 1.0
 */
public class REGClinicQueTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static REGClinicQueTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGClinicQueTool
     */
    public static REGClinicQueTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGClinicQueTool();
        return instanceObject;
    }

    /**
     *  ������
     */
    public REGClinicQueTool() {
        setModuleName("reg\\REGClinicQueModule.x");
        onInit();
    }

    /**
     * ��ѯVIP�����
     * @param admType String
     * @param admDate String
     * @param session String
     * @param clinicRoom String
     * @param visitCode String
     * @param apptCode String
     * @param regmethodCode String
     * @return int
     */
    public int selectqueno(String admType, String admDate,
                           String session, String clinicRoom, String visitCode,
                           String apptCode, String regmethodCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", session);
        parm.setData("CLINICROOM_NO", clinicRoom);
        if ("0".equals(visitCode))
            parm.setData("VISIT_CODE", "Y");
        else
            parm.setData("VISIT_CODE", "N");
        parm.setData("APPT_CODE", apptCode);
        parm.setData("REGMETHOD_CODE", regmethodCode);
        int no = this.getResultInt(query("selectqueno", parm), "QUE_NO");
        if (no == 0)
            return -1;
        return no;

    }
    /**
     * ����VIP�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updatequeno(TParm parm,TConnection conn) {
        parm.setData("QUE_STATUS", "Y");
        TParm result = update("updatequeno", parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯVIP�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selVIPDate(TParm parm) {
        TParm result = query("selVIPDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��С��VIP���� �Һ�ʹ��
	 *  ==========pangb 2012-2-20
     * @param parm
     * @return
     */
    public TParm selVIPMinQueNo(TParm parm){
    	 TParm result = query("selVIPMinQueNo", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
}
