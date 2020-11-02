package jdo.ekt;

import jdo.adm.ADMInpTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: ҽ�ƿ���ɫͨ��</p>
 *
 * <p>Description: ҽ�ƿ���ɫͨ��</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTGreenPathTool extends TJDOTool {
    /**
     * ʵ��
     */
    private static EKTGreenPathTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static EKTGreenPathTool getInstance() {
        if (instanceObject == null)
            instanceObject = new EKTGreenPathTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public EKTGreenPathTool() {
        setModuleName("ekt\\EKTGreenPathModule.x");
        onInit();
    }

    /**
     * ��������
     * @param regMethod String
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection conn) {
//         System.out.println("��̨Parm:"+parm);
        TParm result = new TParm();
        TParm check = new TParm();
        check.setData("CASE_NO", parm.getData("CASE_NO"));
        check.setData("APPLY_DATE", parm.getData("APPLY_DATE"));
        if (existsPatMethod(check)) {
            result.setErr( -1, "ʱ���ظ�!");
            return result;
        }
        result = update("insertdata", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯȫ�ֶ�
     * @param  TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = query("selectdata", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��ɫͨ��������
     * @param  TParm
     * @return TParm
     */
    public TParm selectGreenPath(TParm parm) {
        TParm result = query("selectGreenPath", parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����ĳ���˵�������ɫͨ��
     * @param CASE_NO String
     * @return TParm
     */
    public TParm cancleGreenPath(String CASE_NO, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm result = update("deletedata", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //���adm_inp�е���ɫͨ�� ֵ
        TParm greenParm = new TParm();
        greenParm.setData("CASE_NO", CASE_NO);
        greenParm.setData("GREENPATH_VALUE", 0);
        result = ADMInpTool.getInstance().updateGREENPATH_VALUE(greenParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����һ����ɫͨ��
     * @param parm TParm   ������CASE_NO��APPLY_DATE;APPLY_AMT
     * @param conn TConnection
     * @return TParm
     */
    public TParm cancleGreenPath(TParm parm, TConnection conn) {
        TParm result = update("deletedata", parm, conn);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��ѯסԺ���� ��ȡԭ����ɫͨ����ֵ
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm adm = ADMInpTool.getInstance().selectall(admParm);
        //��ȥ���ϵ���ɫͨ�����
        double greenPath = adm.getDouble("GREENPATH_VALUE", 0) -
                           parm.getDouble("APPROVE_AMT");
        //�޸�ADM_INP���е���ɫͨ���ֶ�
        TParm greenParm = new TParm();
        greenParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        greenParm.setData("GREENPATH_VALUE", greenPath);
        result = ADMInpTool.getInstance().updateGREENPATH_VALUE(greenParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     *
     * @param caseNo String
     * @return boolean
     */
    public boolean existsPatMethod(TParm parm) {
        return getResultInt(query("existsPatMethod", parm), "COUNT") > 0;
    }

    /**
     * ��ѯ�˾��ﲡ���Ƿ������ɫͨ��
     * @param parm TParm
     * @return TParm
     * =================pangben 20111009
     */
    public TParm selPatEktGreen(TParm parm) {
        TParm result = query("selPatEktGreen", parm);
        if (result.getErrCode() < 0) {
            err("ERR:M " + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
