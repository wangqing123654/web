package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:�ű𹤾��� </p>
 *
 * <p>Description:�ű𹤾��� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class PanelTypeTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static PanelTypeTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelTypeTool
     */
    public static PanelTypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelTypeTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PanelTypeTool() {
        setModuleName("reg\\REGPanelTypeModule.x");
        onInit();
    }
    /**
     * ��ѯ�ű�
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
     * �����ű�
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String admType = parm.getValue("ADM_TYPE");
        String clinicTypeCode = parm.getValue("CLINICTYPE_CODE");
        if(existsClinicType(admType,clinicTypeCode)){
            result.setErr(-1,"�ű�"+" �Ѿ�����!");
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
     * ���ºű�
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
     * �����ż���,�ű�����ѯ�ű�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ���ű�
     * @param admType String
     * @param clinicTypeCode String
     * @return TParm
     */
    public TParm deletedata(String admType,String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �ж��Ƿ���ںű�
     * @param admType String
     * @param clinicTypeCode String
     * @return boolean
     */
    public boolean existsClinicType(String admType,String clinicTypeCode) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        parm.setData("CLINICTYPE_CODE", clinicTypeCode);
        return getResultInt(query("existsClinicType", parm), "COUNT") > 0;
    }
    /**
     * ���ݺű�CODEȡ�úű�DESC
     * @param clinicTypeCode String
     * @return String
     */
    public String getDescByCode(String clinicTypeCode) {
      TParm result = new TParm();
      TParm parm = new TParm();
      parm.setData("CLINICTYPE_CODE",clinicTypeCode);
      result = query("getDescByCode", parm);
      String lresult = result.getValue("CLINICTYPE_DESC",0);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return lresult;
      }
      return lresult;
  }
  /**
   * �Ƿ�Ϊר����
   * @param clinictypeCode String �ű����
   * @return boolean true ר�� false ��ר��
   */
  public boolean selProfFlg(String clinictypeCode) {
      TParm result = new TParm();
      TParm parm = new TParm();
      parm.setData("CLINICTYPE_CODE", clinictypeCode);
      result = query("selProfFlg", parm);
      if (result.getErrCode() < 0) {
          err(result.getErrName() + " " + result.getErrText());
          return false;
      }
      return true;
  }

}
