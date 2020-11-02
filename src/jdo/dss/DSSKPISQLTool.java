package jdo.dss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;


/**
 * <p>Title: ָ��ȼ��趨������</p>
 *
 * <p>Description: ָ��ȼ��趨������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ָ��ȼ��趨</p>
 *
 * @author sundx
 * @version 1.0
 */

public class DSSKPISQLTool extends TJDOTool{

    /**
     * ʵ��
     */
    public static DSSKPISQLTool instanceObject;

    /**
     * �õ�ʵ��
     * @return DSSSQLTool
     */
    public static DSSKPISQLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DSSKPISQLTool();
        return instanceObject;
    }

    /**
     * ���캯��
     */
    public DSSKPISQLTool() {
        setModuleName("dss\\DSSKPICodeModule.x");
        onInit();
    }

    /**
     * ��ѯָ��ȼ���Ϣ
     * @return TParm
     */
    public TParm queryDSSKPI() {
        TParm parm = new TParm();
        TParm result = query("queryDSSKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

     /**
      * ͨ���ȼ���Ϣ�����ѯָ��ȼ���Ϣ
      * @return TParm
      */
     public TParm queryDSSKPIByCode(String KPICode) {
         TParm parm = new TParm();
         parm.setData("KPI_CODE",KPICode);
         TParm result = query("queryDSSKPIByCode", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }


     /**
      * ͨ�����ȼ������ѯָ��ȼ���Ϣ
      * @return TParm
      */
     public TParm queryDSSKPIByParentCode(String parentCode) {
         TParm parm = new TParm();
         parm.setData("PARENT_CODE",parentCode);
         TParm result = query("queryDSSKPIByParentCode", parm);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return null;
         }
         return result;
     }

     /**
      * ����DSSKPI�����Ϣ
      * @param parm TParm
      * @return TParm
      */
     public TParm updateDSSKPIByCode(TParm parm){
          TParm result = update("updateDSSKPIByCode", parm);
          if (result.getErrCode() < 0) {
              err(result.getErrCode() + " " + result.getErrText());
              return null;
          }
          return result;
      }

    /**
     * д��DSSKPI
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDSSKPI(TParm parm){
        TParm result = update("insertDSSKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ɾ��DSSKPI
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteDSSKPI(String KPICode){
        TParm parm = new TParm();
        parm.setData("KPI_CODE",KPICode);
        TParm result = update("deleteDSSKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ɾ���ڵ㼰���ӽڵ�
     * @param KPICode String
     * @return TParm
     */
    public TParm deleteKPIAndChlidren(String KPICode){
        TParm parm = queryDSSKPIByParentCode(KPICode);
        if(parm == null)
            return null;
        TParm result = new TParm();
        for(int i = 0;i < parm.getCount("KPI_CODE");i++){
            result = deleteKPIAndChlidren(parm.getValue("KPI_CODE",i));
        }
        result = deleteDSSKPI(KPICode);
        return result;
    }

    /**
     * �õ�������
     * @return TParm
     */
    public String getMaxSeq() {
        TParm parm = new TParm();
        TParm result = query("getMaxSeq", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        if (result.getCount("SEQ") <= 0)
            return "1";
        return "" + (Integer.parseInt(result.getValue("SEQ",0)) + 1);
     }
  }
