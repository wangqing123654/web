package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title:�ҺŲ��������� </p>
 *
 * <p>Description:�ҺŲ��������� </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class REGSysParmTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static REGSysParmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGSysParmTool
     */
    public static REGSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGSysParmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public REGSysParmTool() {
        setModuleName("reg\\REGSysParmModule.x");

        onInit();
    }

    /**
     * ��ѯ�ҺŲ���
     * @return TParm
     */
    public TParm selectdata() {
        TParm result = query("selectdata");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¹ҺŲ���
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
     * ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm insert(TParm parm) {
        TParm result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯĬ��֧����ʽ
     * @return TParm
     */
    public TParm selPayWay() {
        TParm result = query("selPayWay");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }

    /**
     * ��ѯĬ�ϳ�����
     * @return TParm
     */
    public TParm selVisitCode() {
        TParm result = query("selVisitCode");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }

    /**
     * ��ѯ�Һ���Ч����
     * @return int
     */
    public int selEffectDays() {
        TParm result = query("selEffectDays");
        int effectDays = result.getInt("EFFECT_DAYS", 0);
        return effectDays;
    }

    /**
     * ��ѯ�Ƿ���Կ�Ժ���Һ�
     * @return boolean
     */
    public boolean selOthHospRegFlg() {
        TParm result = query("selOthHospRegFlg");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        String othhospRegFlg = result.getValue("OTHHOSP_REG_FLG", 0);
        if ("Y".equals(othhospRegFlg))
            return true;
        else
            return false;
    }

    /**
     * ��ѯ������˱��
     * @return boolean
     */
    public boolean selTriageFlg() {
        TParm result = query("selTriageFlg");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;
    }
    
    /**
     * ��ѯĬ�ϳ�����
     * @return boolean
     */
    public boolean selAeadFlg() {
        TParm result = query("selAeadFlg");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        return result.getBoolean("AHEAD_FLG", 0);
    }
    
    /**
     * ��ѯ�Һ��Ƿ��Ʊ
     * @return boolean
     */
    public boolean selTicketFlg() {
        TParm result = query("selTicketFlg");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        return result.getBoolean("TICKET_FLG", 0);
    }
    
    /**
     * ��ѯ�Һ��Ƿ���ﵥ
     * add by huangtt 20140409
     * @return boolean
     */
    public boolean selSingleFlg() {
        TParm result = query("selSingleFlg");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        return result.getBoolean("SINGLE_FLG", 0);
    }
    
    
}
