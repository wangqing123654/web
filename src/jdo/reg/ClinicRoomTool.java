package jdo.reg;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

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
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class ClinicRoomTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ClinicRoomTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ClinicRoomTool
     */
    public static ClinicRoomTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ClinicRoomTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ClinicRoomTool() {
        setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }

    /**
     * ��ѯ����
     * @param clinicAreaCode String
     * @return TParm
     */
    public TParm queryTree(String clinicAreaCode) {
        TParm parm = new TParm();
        parm.setData("CLINICAREA_CODE", clinicAreaCode);
        TParm result = query("queryTree", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * ����������ѯ����table��Ϣ
     * @param AREACODE String
     * @return TParm
     */
    public TParm selectdata(String AREACODE) {
        TParm parm = new TParm();
        parm.setData("CLINICAREA_CODE", AREACODE);
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �õ����������ң�For ONW��
     * @param parm TParm ������ʽ  ADM_TYPE:�ż�ס��(����) ;CLINICAREA_CODE:����
     * @return TParm
     */
    public TParm getClinicRoomForONW(TParm parm) {
        TParm result = query("getClinicRoomForAdmType", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������ҵõ�ҩ����For OPD��
     * @param clinicRoom String
     * @return TParm
     */
    public TParm getOrgCodeByRoomNo(String clinicRoom) {
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", clinicRoom);
        TParm result = query("getOrgCodeByRoomNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ����Ӧ��ҩ��
     * @param regionCode String
     * @param admDate String
     * @param sessionCode String
     * @param admType String
     * @param realdrCode String
     * @param realDeptCode String
     * @return TParm
     */
    public TParm getOrgByOdo(String regionCode, String admDate,
                             String sessionCode, String admType,
                             String realdrCode, String realDeptCode,String CASE_NO) {
        String sql =
            "SELECT DISTINCT A.ORG_CODE " +
            "	FROM REG_CLINICROOM A,REG_PATADM B " +
            "	WHERE B.REGION_CODE= '" + regionCode + "'" +
            "		  AND B.ADM_DATE= TO_DATE('" + admDate + "','YYYYMMDD') " +

//            "		  AND B.REALDEPT_CODE='" + realDeptCode + "'" +
//
//            "		  AND B.REALDR_CODE= '" + realdrCode + "'" +
            "             AND B.CASE_NO='"+CASE_NO+"'"+
            "		  AND B.CLINICROOM_NO=A.CLINICROOM_NO";
//    	System.out.println("phaCode--------------->sql->"+sql);

        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//
//    	parm.setData("REGION_CODE",regionCode);
//        parm.setData("ADM_DATE", admDate);
//        parm.setData("SESSION_CODE",sessionCode);
//        parm.setData("ADM_TYPE",admType);
//        parm.setData("REALDR_CODE",realdrCode);
//        TParm result = query("getOrgByODO", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �������Ҳ�ѯ����
     * @param roomcode String
     * @return TParm
     */
    public TParm selectfortablebyarea(String roomcode) {
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", roomcode);
        TParm result = query("selectfortablebyarea", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����Ƿ�����
     * @param clinicroomNo String  ���Һ�
     * @return boolean true ������ false δ����
     */
    public boolean selActiveFlg(String clinicroomNo) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CLINICROOM_NO", clinicroomNo);
        result = query("selActiveFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * ���ݵ�¼ʱ�䡢��¼���ҡ���¼ҽʦ�õ���ǰ����������
     * @param admType String
     * @param admDate String
     * @param sessionCode String
     * @return String
     */
    public String getRoomNo(String admType, String admDate, String sessionCode,
                            String deptCode, String drCode) {
        String roomNo = "";
        TParm parm = new TParm();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("ADM_TYPE", admType);
        parm.setData("ADM_DATE", admDate);
        parm.setData("SESSION_CODE", sessionCode);
        parm.setData("DEPT_CODE", deptCode);
        parm.setData("DR_CODE", drCode);
        TParm result = query("getRoomNo", parm);
        if (result.getErrCode() != 0) {
            //System.out.println("result.errCode" + result.getErrText());
            return null;
        }
//        System.out.println("result==="+result);
        roomNo = result.getValue("CLINICROOM_NO", 0);
//    	System.out.println("roomNo="+roomNo);
        return roomNo;
    }

    /**
     * ��ѯδʹ�ÿ���
     * @param parm TParm(��������:ADM_DATE �ż���:ADM_TYPE ʱ��:SESSION_CODE)
     * @return TParm
     */
    public TParm getNotUseForODO(TParm parm) {
        if (parm == null)
            return parm;
        String sql = "SELECT CLINICROOM_NO AS ID, CLINICROOM_DESC AS NAME, PY1 "+
                     " FROM REG_CLINICROOM "+
                     " WHERE CLINICROOM_NO NOT IN ( "+
                     " SELECT CLINICROOM_NO "+
                     " FROM REG_SCHDAY "+
                     " WHERE ADM_DATE = '"+parm.getValue("ADM_DATE")+"' "+
                     " AND ADM_TYPE = '"+parm.getValue("ADM_TYPE")+"' "+
                     " AND REGION_CODE = '"+parm.getValue("REGION_CODE")+"' " +
                     " AND SESSION_CODE = '"+parm.getValue("SESSION_CODE")+"' ) "+
                     " AND ADM_TYPE='"+parm.getValue("ADM_TYPE")+"' " +
                     " AND REGION_CODE = '"+parm.getValue("REGION_CODE")+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public static void main(String args[]) {
        com.dongyang.util.TDebug.initClient();
        //System.out.println(ClinicRoomTool.getInstance().queryTree("1"));
//        System.out.println(ClinicRoomTool.getInstance().getClinicRoomForAdmType(
//            "E"));
    }
}
