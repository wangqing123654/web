package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;
/**
 *
 * <p>Title:���ҹ����� </p>
 *
 * <p>Description:���ҹ����� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.10.15
 * @version 1.0
 */
public class DeptTool extends TJDOTool{
    /**
     * ʵ��
     */
    public static DeptTool instanceObject;
    //���ݴ���ȡ��Ӣ����
    private static String GET_ENG_DESC="SELECT DEPT_ENG_DESC FROM SYS_DEPT WHERE DEPT_CODE='#'";

    /**
     * �õ�ʵ��
     * @return DeptTool
     */
    public static DeptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DeptTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public DeptTool() {
        setModuleName("sys\\SYSDeptModule.x");
        onInit();
    }
    /**
     * ���ݿ���CODEȡ�ÿ���DESCRIPTION
     * @param deptCode String
     * @return String
     */
    public String getDescByCode(String deptCode) {
          TParm result = new TParm();
          TParm parm =new TParm();
          parm.setData("DEPT_CODE",deptCode);
          result = query("getDescByCode", parm);
          String value = result.getValue("DEPT_ABS_DESC",0);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return value;
          }
          return value;
      }
      /**
       * ����ҩ������
       * @return TParm
       */
      public TParm selectOrgDept(){
          TParm result = query("selectOrgDept");
              if (result.getErrCode() < 0) {
                  err("ERR:" + result.getErrCode() + result.getErrText() +
                      result.getErrName());
                  return result;
              }
              return result;

      }
      /**
       * ���ҿ���
       * @return TParm
       */
      public TParm selectDept(){
          TParm result = query("selectDept");
              if (result.getErrCode() < 0) {
                  err("ERR:" + result.getErrCode() + result.getErrText() +
                      result.getErrName());
                  return result;
              }
              return result;

      }
      /**
       * ��ѯ�ٴ�ҽ������
       * @return TParm
       */
      public TParm selUserDept(String deptCode){
          TParm parm = new TParm();
          parm.setData("DEPT_CODE",deptCode);
          TParm result = query("selUserDept",parm);
              if (result.getErrCode() < 0) {
                  err("ERR:" + result.getErrCode() + result.getErrText() +
                      result.getErrName());
                  return result;
              }
              return result;

      }

      /**
       * ���ݿƱ����ȡ��Ӣ������
       * @param deptCode
       * @return
       */
      public String getDeptEngDesc(String deptCode){
    	  String deptEngDesc="";
    	  if(StringUtil.isNullString(deptCode)){
    		  return deptEngDesc;
    	  }
    	  String sql=GET_ENG_DESC.replaceFirst("#", deptCode);
    	  TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	  if(result.getErrCode()!=0){
    		  return "";
    	  }
    	  deptEngDesc=result.getValue("DEPT_ENG_DESC",0);
    	  return deptEngDesc;
      }
      /**
       * ���ݿ���,���������ѯ�ɱ�����
       * @param deptCode String
       * @param stationCode String
       * @return String
       */
      public String getCostCenter(String deptCode, String stationCode) {
          String costCenter = "";
          String selByDept =
              " SELECT COST_CENTER_CODE FROM SYS_DEPT WHERE DEPT_CODE = '" +
              deptCode + "' ";
          TParm result = new TParm(TJDODBTool.getInstance().select(selByDept));
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return "";
          }
          costCenter = result.getValue("COST_CENTER_CODE", 0);
          String selByDeptStation =
              " SELECT COST_CENTER_CODE FROM SYS_STADEP_LIST " +
              "  WHERE DEPT_CODE = '" + deptCode + "' AND STATION_CODE = '" +
              stationCode + "' ";
          if (result.getData("COST_CENTER_CODE", 0) == null ||
              costCenter.length() == 0) {
              result = new TParm(TJDODBTool.getInstance().select(selByDeptStation));
              if (result.getErrCode() < 0) {
                  err("ERR:" + result.getErrCode() + result.getErrText() +
                      result.getErrName());
                  return "";
              }
              costCenter = result.getValue("COST_CENTER_CODE", 0);
              return costCenter;
          }
          else {
              return costCenter;
          }

      }
}
