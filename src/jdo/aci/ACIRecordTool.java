package jdo.aci;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;

/**
 * <p>Title: ������¹ʹ�������</p>
 *
 * <p>Description: ������¹ʹ�������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.05
 * @version 1.0
 */
public class ACIRecordTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ACIRecordTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ACIRecordTool
     */
    public static ACIRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ACIRecordTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ACIRecordTool() {
        setModuleName("aci\\ACIRecordModule.x");
        onInit();
    }

    /**
     * ����������¹�
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String recordNo = SystemTool.getInstance().getNo("ALL", "ACI",
            "RECORD_NO",
            "RECORD_NO"); //����ȡ��ԭ��
        parm.setData("ACI_RECORD_NO", recordNo);
        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²�����¹�
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
     * ��ѯ������¹�
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
     * ɾ��������¹�
     * @param ACIRecordNo String
     * @return TParm
     */
    public TParm deletedata(String ACIRecordNo) {
        TParm parm = new TParm();
        parm.setData("ACI_RECORD_NO", ACIRecordNo);
        TParm result = update("deletedata", parm);
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
