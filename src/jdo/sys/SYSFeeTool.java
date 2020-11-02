package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class SYSFeeTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    private static SYSFeeTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return PatTool
     */
    public static SYSFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFeeTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SYSFeeTool() {
        setModuleName("sys\\SYSFeeModule.x");
        onInit();
    }
    /**
     * ����ҽ������ѯ����
     * @param orderCode String
     * @return TParm
     */
    public TParm getFee(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getFee", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����ҽ������ѯԺ�ڷ��ô���
     *
     * @param orderCode
     *            String
     * @return TParm
     */
    public TParm getChargHospCode(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getChargHospCode", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����ҽ�������ѯ������Ϣ
     *
     * @param orderCode
     *            String
     * @return TParm ownPrice,nhiPrice
     */
    public TParm getFeeData(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getFeeData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����ҽ�������ѯ�ԷѼۡ�ҽ������������߼�
     * @param orderCode String
     * @return TParm
     */
    public TParm getPrice(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getPrice", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ҽ������ѯ����
     * @param orderCode String
     * @return TParm
     */
    public TParm getFeeOldPrice(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getOldPrice", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �õ�Cat1Code
     * @param orderCode String
     * @return TParm
     */
    public TParm getCat1Code(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getCat1Code", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ͨ��orderCode�õ�ҽ����Ϣ
     * @param orderCode String
     * @return TParm
     */
    public TParm getFeeAllData(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getFeeAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �Ƿ�Ϊ�涨ҩƷ���
     * @param phaType String
     * @param sysFeeType String
     * @return boolean
     */
    public boolean getType(String phaType, String sysFeeType) {
        if (sysFeeType.contains(phaType))
            return true;
        return false;
    }
   /**
     * ͨ��orderCode�õ�ҽ����Ϣ
     * @param orderCode String
     * @return TParm
     */
    public TParm getCtrFlg(String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        TParm result = query("getCtrFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ͨ��orderCode�õ�ҽ����Ϣ
     *
     * @param orderCode String
     * @return TParm
     */
    public TParm onUpdateCtrflg(TParm inparm) {
        if (inparm.getErrCode() < 0) {
            err(inparm.getErrCode() + " " + inparm.getErrText());
            return inparm;
        }
        TParm result = update("updateCtrflg", inparm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ҽ����Ŀ�ֵ��޸�ҩƷҽ����Ӧ
     * @param parm
     * @return
     * ==============pangben 2011-12-16
     */
    public TParm updateINSToSysFee(TParm parm, TConnection conn){
    	 TParm result = update("updateINSToSysFee", parm,conn);
         if (result.getErrCode() < 0) {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
    }
    /**
     * סԺ���÷ָ�
     * �ж�order�Ƿ����sys_Fee
     * @param parm
     * @return
     */
    public TParm querySysFeeIns(TParm parm){
    	TParm result = query("querySysFeeIns", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
