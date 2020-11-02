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
public class PanelRoomTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static PanelRoomTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelRoomTool
     */
    public static PanelRoomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelRoomTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PanelRoomTool() {
        setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    /**
     * ��ѯ����
     * @param clinicRoomNo String
     * @return TParm
     */
    public TParm queryTree(String clinicRoomNo) {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         TParm result = query("queryTree", parm);
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
         String clinicRoomNo = parm.getValue("CLINICROOM_NO");
         if(existsClinicRoom(clinicRoomNo)){
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
//         System.out.println("parm"+parm);
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
      * @param clinicRoomNo String
      * @return boolean
      */
     public TParm deletedata(String clinicRoomNo) {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         TParm result = update("deletedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * �ж��Ƿ��������
      * @param clinicRoomNo String ���Ҵ���
      * @return boolean TRUE ���� FALSE ������
      */
     public boolean existsClinicRoom(String clinicRoomNo) {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         return getResultInt(query("existsClinicRoom", parm), "COUNT") > 0;
     }
     /**
      * �õ�����
      * @param clinicRoomNo String
      * @return int
      */
     public int initclinicroomno(String clinicRoomNo)
     {
         TParm parm = new TParm();
         parm.setData("CLINICROOM_NO", clinicRoomNo);
         return getResultInt(query("initclinicroomno",parm),"CLINICROOM_NO");
     }
     /**
      * �������ҵõ�������For REG��
      * @param clinicRoomNo String
      * @return TParm
      */
     public TParm getAreaByRoom(String clinicRoomNo){
     TParm parm = new TParm();
     parm.setData("CLINICROOM_NO",clinicRoomNo);
     TParm result = query("getAreaByRoom",parm);
     return result;
     }
}
