package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>Title:������𹤾��� </p>
 *
 * <p>Description:������𹤾��� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class PanelGroupTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PanelGroupTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelGroupTool
     */
    public static PanelGroupTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelGroupTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PanelGroupTool() {
        setModuleName("reg\\REGPanelGroupModule.x");
        onInit();
    }

    /**
     * ��ѯ�������
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
     * �����������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String quegroupCode = parm.getValue("QUEGROUP_CODE");
        if (existsQueGroup(quegroupCode)) {
            result.setErr( -1, "�������" + " �Ѿ�����!");
            return result;
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
     * ���¸������
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
     * ��ѯ�������
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
     * ɾ���������
     * @param quegroupCode String
     * @return boolean
     */
    public TParm deletedata(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ж��Ƿ���ڸ������
     * @param quegroupCode String ����������
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsQueGroup(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        return getResultInt(query("existsQueGroup", parm), "COUNT") > 0;
    }

    /**
     * �õ�������
     * @param quegroupCode String
     * @return int
     */
    public int getMaxQue(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        return getResultInt(query("getMaxQue", parm), "MAX_QUE");
    }

    /**
     * �õ�VIPע��
     * @param quegroupCode String
     * @return boolean
     */
    public boolean getVipFlg(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        return TCM_Transform.getBoolean(getResultString(query("getVipFlg", parm),
            "VIP_FLG"));
    }
    /**
     * ���ݺű��ѯ��������VIPע��
     * @param quegroupCode String
     * @return TParm
     */
    public TParm getInfobyClinicType(String quegroupCode) {
        if(quegroupCode==null||quegroupCode.length()==0)
            return null;
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE",quegroupCode);
        TParm result = query("getInfobyClinicType",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
