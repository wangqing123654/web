package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:����ά�������� </p>
 *
 * <p>Description:����ά�������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.25
 * @version 1.0
 */
public class PanelAreaTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static PanelAreaTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelAreaTool
     */
    public static PanelAreaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelAreaTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PanelAreaTool() {
        setModuleName("reg\\REGClinicAreaModule.x");
        onInit();
    }
    /**
      * ��ѯ����
      * @return TParm
      */
     public TParm queryTree() {
         TParm result = query("queryTree");
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }
     /**
      * ��������
      * @param parm TParm
      * @return TParm
      */
     public TParm insertdata(TParm parm) {
         TParm result = new TParm();
         String clinicAreaCode = parm.getValue("CLINICAREA_CODE");
         if(existsClinicArea(clinicAreaCode)){
             result.setErr(-1,"����"+" �Ѿ�����!");
             return result ;
         }

         result = update("insertdata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }
     /**
      * ��������
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
      * ��ѯ����
      * @param parm TParm
      * @return TParm
      */
     public TParm selectdata(TParm parm) {
         TParm result = new TParm();
         result = query("selectdata",parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * ɾ������
      * @param clinicAreaCode String
      * @return boolean
      */
     public TParm deletedata(String clinicAreaCode) {
         TParm parm = new TParm();
         parm.setData("CLINICAREA_CODE", clinicAreaCode);
         TParm result = update("deletedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * �ж��Ƿ������
      * @param clinicAreaCode String ��������
      * @return boolean TRUE ���� FALSE ������
      */
     public boolean existsClinicArea(String clinicAreaCode) {
         TParm parm = new TParm();
         parm.setData("CLINICAREA_CODE", clinicAreaCode);
         return getResultInt(query("existsClinicArea", parm), "COUNT") > 0;
     }
     /**
      * �õ�����
      * @param clinicAreaCode String
      * @return int
      */
     public int getclinicAreaCombo(String clinicAreaCode)
     {
         TParm parm = new TParm();
         parm.setData("CLINICAREA_CODE", clinicAreaCode);
         return getResultInt(query("getclinicAreaCombo",parm),"CLINICAREA_CODE");
     }
}
