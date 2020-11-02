package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: �����ײ�ά��
 * </p>
 * @author liling 20140508
 * 
 *
 */
public class MEMLumpworkTool extends TJDOTool {
    /**
     * ʵ��
     */
    private static MEMLumpworkTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelGroupTool
     */
    public static MEMLumpworkTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEMLumpworkTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public MEMLumpworkTool() {
        setModuleName("mem\\MEMLumpworkModule.x");
        onInit();
    }

    /**
     * ���������ײ�
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String quegroupCode = parm.getValue("LUMPWORK_CODE");
        if (existsQueGroup(quegroupCode)) {
            result.setErr( -1, "�����ײͱ���Ѵ���!");
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
     * ���°����ײ�
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
     * ��ѯ�����ײ�
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
     * ɾ�������ײ�
     * @param quegroupCode String
     * @return boolean
     */
    public TParm deletedata(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("LUMPWORK_CODE", quegroupCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ж��Ƿ���ڰ����ײ�
     * @param quegroupCode String �����ײʹ���
     * @return boolean TRUE ���� FALSE ������
     */
    public boolean existsQueGroup(String quegroupCode) {
    	TParm parm = new TParm();
//        TParm parm = new TParm(TJDODBTool.getInstance().select(
//        		"SELECT COUNT(*) AS COUNT FROM MEM_LUMPWORK WHERE LUMPWORK_CODE='"+quegroupCode+"'"
//        ));
        parm.setData("LUMPWORK_CODE", quegroupCode);
//        parm.getValue("COUNT", 0);
       return getResultInt(query("existsQueGroup", parm), "COUNT") > 0;
//        return getResultInt(parm,"COUNT")> 0;
    }

}
