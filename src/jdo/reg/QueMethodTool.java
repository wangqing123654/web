package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:���ŷ�ʽ������ </p>
 *
 * <p>Description:���ŷ�ʽ������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class QueMethodTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static QueMethodTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelGroupTool
     */
    public static QueMethodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new QueMethodTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public QueMethodTool() {
        setModuleName("reg\\REGQueMethodModule.x");
        onInit();
    }
    /**
      * ��ѯ���ŷ�ʽ
      * @return TParm
      */
     public TParm queryTree(String quegroupCode,String queNo) {
         TParm parm = new TParm();
         parm.setData("QUEGROUP_CODE", quegroupCode);
         parm.setData("QUE_NO", queNo);
         TParm result = query("queryTree", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;

     }

     /**
      * �������ŷ�ʽ
      * @param  String
      * @return TParm
      */
     public TParm insertdata(TParm parm) {
         TParm result = new TParm();
         String quegroupCode = parm.getValue("QUEGROUP_CODE");
         String queNo = parm.getValue("QUE_NO");
         if(existsQueMethod(quegroupCode,queNo)){
             result.setErr(-1,"���ŷ�ʽ"+" �Ѿ�����!");
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
      * ���¸��ŷ�ʽ
      * @param  String
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
      * ��ѯ���ŷ�ʽ
      * @parm
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
      * ɾ�����ŷ�ʽ
      * @param quegroupCode,queNo String
      * @return boolean
      */
     public TParm deletedata(String quegroupCode,String queNo) {
         TParm parm = new TParm();
         parm.setData("QUEGROUP_CODE", quegroupCode);
         parm.setData("QUE_NO", queNo);
         TParm result = update("deletedata", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
     }

     /**
      * �ж��Ƿ���ڸ��ŷ�ʽ
      * @param quegroupCode,queNo String ���ŷ�ʽ
      * @return boolean TRUE ���� FALSE ������
      */
     public boolean existsQueMethod(String quegroupCode,String queNo) {
         TParm parm = new TParm();
         parm.setData("QUEGROUP_CODE", quegroupCode);
         parm.setData("QUE_NO", queNo);
         return getResultInt(query("existsQueMethod", parm), "COUNT") > 0;
     }
}
